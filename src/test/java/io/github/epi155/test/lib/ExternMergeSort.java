package io.github.epi155.test.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.function.UnaryOperator;

/**
 * utility class to sort a large file without loading it entirely into memory
 */
public class ExternMergeSort {
    private static final String PRFX = "ems-";
    private static final String SUFX = ".txt";
    private static final Logger log = LoggerFactory.getLogger(ExternMergeSort.class);
    private final File inputFile;
    private final File outputFile;
    private final int maxNumRecord;
    private final Comparator<String> comparator;
    private Charset charset = StandardCharsets.UTF_8;
    private int nmWriterThreads = 1;
    private ExecutorService mergerPool;
    private UnaryOperator<String> inRec;

    /**
     * main constructor with required fields
     *
     * @param inputFile    input file
     * @param outputFile   output file
     * @param comp         comparator
     * @param maxNumRecord maximum number of records to load into memory
     */
    public ExternMergeSort(File inputFile, File outputFile, Comparator<String> comp, int maxNumRecord) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.comparator = comp;
        this.maxNumRecord = maxNumRecord;
    }

    /**
     * alternative constructor with required fields
     *
     * @param inputFile    input file
     * @param outputFile   output file
     * @param comp         comparator
     * @param maxNumRecord maximum number of records to load into memory
     */
    public ExternMergeSort(String inputFile, String outputFile, Comparator<String> comp, int maxNumRecord) {
        this(new File(inputFile), new File(outputFile), comp, maxNumRecord);
    }

    /**
     * sets the character set
     *
     * @param charset input/output file charset
     * @return this
     */
    public ExternMergeSort withCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * sets the number of threads to use in the merge phase
     *
     * @param maxThread max thread in merge phase
     * @return this
     */
    public ExternMergeSort withMaxThreads(int maxThread) {
        this.nmWriterThreads = maxThread;
        return this;
    }

    /**
     * performs the sort
     */
    public void execute() {
        mergerPool = Executors.newFixedThreadPool(nmWriterThreads);
        long sTime = System.nanoTime();
        log.debug("splitting ...");
        List<File> tempFiles = split();
        log.debug("splitted in {} files", tempFiles.size());
        if (tempFiles.size() > 1) {
            merge(tempFiles);
        } else {
            log.debug("No merge required");
        }
        mergerPool.shutdown();
        long eTime = System.nanoTime();
        if (log.isInfoEnabled()) {
            long dTime = eTime - sTime;
            Double time = 1e-9 * dTime;
            NumberFormat nf = new DecimalFormat("##,##0.000000");
            log.info("Sort finished in {} seconds", nf.format(time));
        }
    }

    private void merge(List<File> tempFiles) {
        log.debug("Merging {} files ...", tempFiles.size());
        List<File> mergeFiles = new ArrayList<>();
        Phaser phaser = new Phaser();
        if (tempFiles.size() == 2) {
            MergeTask mergeTask = new MergeTask(tempFiles.get(0), tempFiles.get(1), outputFile, phaser);
            mergerPool.submit(mergeTask);
        } else {
            Queue<File> queue = new LinkedList<>(tempFiles);
            while (!queue.isEmpty()) {
                File sx = queue.poll();
                File dx = queue.poll();
                if (dx == null) {
                    mergeFiles.add(sx);
                } else {
                    try {
                        File cx = File.createTempFile(PRFX, SUFX);
                        cx.deleteOnExit();
                        mergeFiles.add(cx);
                        MergeTask mergeTask = new MergeTask(sx, dx, cx, phaser);
                        mergerPool.submit(mergeTask);
                    } catch (IOException e) {
                        throw new ExternalMergeSortException(e, "Temporary files error");
                    }
                }
            }
        }
        phaser.awaitAdvance(0);
        if (!mergeFiles.isEmpty()) {
            merge(mergeFiles);
        }
    }

    private List<File> split() {
        log.trace("loading ...");
        long nmRec = 0;
        List<String> data = new ArrayList<>(maxNumRecord);
        List<File> splitFiles = new LinkedList<>();
        try (BufferedReader br = Files.newBufferedReader(inputFile.toPath(), charset)) {
            String line;
            while ((line = br.readLine()) != null) {
                nmRec++;
                if (line.length() == 1 && line.charAt(0) == '\u001a') {
                    log.debug("Found records with SUB [\u241a] line {}, stop processing the file", nmRec);
                    break;
                }
                if (inRec != null)
                    line = inRec.apply(line);
                data.add(line);
                if (data.size() >= maxNumRecord) {
                    File chunk = sortAndSave(data);
                    splitFiles.add(chunk);
                    data.clear();
                }
            }
            if (!data.isEmpty()) {
                File chunk;
                if (splitFiles.isEmpty()) {
                    chunk = outputFile;
                    sortAndSave(data, chunk);
                } else {
                    chunk = sortAndSave(data);
                }
                splitFiles.add(chunk);
            }
        } catch (IOException e) {
            throw new ExternalMergeSortException(e, "Error reading the file {}", inputFile);
        }
        return splitFiles;
    }

    private void sortAndSave(List<String> data, File file) {
        log.trace("Sorting ...");
        data.sort(comparator);
        save(data, file);
    }

    private void save(List<String> data, File file) {
        log.trace("Saving ...");
        try (BufferedWriter bw = Files.newBufferedWriter(file.toPath(), charset)) {
            for (String datum : data) {
                bw.write(datum);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ExternalMergeSortException(e, "Error writing the file {}", file.getName());
        }
    }

    private File sortAndSave(List<String> data) {
        log.trace("Sorting ...");
        data.sort(comparator);
        File file;
        try {
            file = File.createTempFile(PRFX, SUFX);
            file.deleteOnExit();
            save(data, file);
        } catch (IOException e) {
            throw new ExternalMergeSortException(e, "Error creating temporary files");
        }
        return file;
    }

    /**
     * alter the record before starting the sort
     *
     * @param inRec alter function
     * @return this
     */
    public ExternMergeSort withInRec(UnaryOperator<String> inRec) {
        this.inRec = inRec;
        return this;
    }

    private static class ExternalMergeSortException extends RuntimeException {

        private static final long serialVersionUID = 1999938357239025060L;

        public ExternalMergeSortException(IOException e, String pattern, Object... objects) {
            super(MessageFormatter.arrayFormat(pattern, objects).getMessage(), e);
            log.error(getMessage(), this);
        }
    }

    class MergeTask implements Runnable {
        private final File src1;
        private final File src2;
        private final File dest;
        private final Phaser phaser;

        private MergeTask(File src1, File src2, File dest, Phaser phaser) {
            this.src1 = src1;
            this.src2 = src2;
            this.dest = dest;
            this.phaser = phaser;

            this.phaser.register();
        }

        @Override
        public void run() {
            log.trace("Merging {}/{} ...", src1.getName(), src2.getName());
            try {
                performMerge();
                log.trace("Merged into {}", dest.getName());
                try {
                    Files.delete(src1.toPath());
                } catch (IOException e) {
                    log.error("Error deleting {}", src1.getName(), e);
                }
                try {
                    Files.delete(src2.toPath());
                } catch (IOException e) {
                    log.error("Error deleting {}", src2.getName(), e);
                }
            } finally {
                phaser.arriveAndDeregister();
            }
        }

        public void performMerge() {
            try (
                BufferedReader br1 = Files.newBufferedReader(src1.toPath(), charset);
                BufferedReader br2 = Files.newBufferedReader(src2.toPath(), charset);
                BufferedWriter wrt = Files.newBufferedWriter(dest.toPath(), charset)
            ) {
                String line1 = br1.readLine();
                String line2 = br2.readLine();
                while (line1 != null || line2 != null) {
                    if (line1 == null) {
                        // end-of-file 1
                        wrt.write(line2);
                        wrt.newLine();
                        line2 = br2.readLine();
                    } else if (line2 == null) {
                        // end-of-file 2
                        wrt.write(line1);
                        wrt.newLine();
                        line1 = br1.readLine();
                    } else {
                        int comp = comparator.compare(line1, line2);
                        if (comp == 0) {
                            wrt.write(line1);
                            wrt.newLine();
                            line1 = br1.readLine();
                            wrt.write(line2);
                            wrt.newLine();
                            line2 = br2.readLine();
                        } else if (comp < 0) {
                            wrt.write(line1);
                            wrt.newLine();
                            line1 = br1.readLine();
                        } else /* comp > 0 */ {
                            wrt.write(line2);
                            wrt.newLine();
                            line2 = br2.readLine();
                        }
                    }
                }
            } catch (IOException e) {
                throw new ExternalMergeSortException(e, "Error merging file {},{} -> {}", src1.getName(), src2.getName(), dest.getName());
            }
        }
    }
}

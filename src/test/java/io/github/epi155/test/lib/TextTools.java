package io.github.epi155.test.lib;

import java.nio.CharBuffer;

/**
 * Utility class to handle text strings
 */
public class TextTools {
    private static final String EMPTY_STRING = "";

    private TextTools() {
    }

    /**
     * Add padding spaces to the right, or truncate to the right to the required length
     *
     * @param s starting string
     * @param t length required
     * @return final string
     */
    public static String rpad(String s, int t) {
        int len = s.length();
        if (len > t) return s.substring(0, t);
        if (len == t) return s;
        return s + CharBuffer.allocate(t - len).toString().replace('\0', ' ');
    }

    /**
     * Add padding characters to the right, or truncate to the right to the required length
     *
     * @param s   starting string
     * @param t   length required
     * @param pad fill character
     * @return final string
     */
    public static String rpad(String s, int t, char pad) {
        int len = s.length();
        if (len > t) return s.substring(0, t);
        if (len == t) return s;
        return s + CharBuffer.allocate(t - len).toString().replace('\0', pad);
    }

    /**
     * Add padding spaces to the left, or truncate to the left to the required length
     *
     * @param s starting string
     * @param t length required
     * @return final string
     */
    public static String lpad(String s, int t) {
        int len = s.length();
        if (len > t) return s.substring(len - t);
        if (len == t) return s;
        return CharBuffer.allocate(t - len).toString().replace('\0', ' ') + s;
    }

    /**
     * Add padding characters to the left, or truncate left to the required length
     *
     * @param s   starting string
     * @param t   length required
     * @param pad fill character
     * @return final string
     */
    public static String lpad(String s, int t, char pad) {
        if (s == null)
            return null;
        int len = s.length();
        if (len > t) return s.substring(len - t);
        if (len == t) return s;
        return CharBuffer.allocate(t - len).toString().replace('\0', pad) + s;
    }

    /**
     * Removes the pad character from the right
     *
     * @param s   starting string
     * @param pad pad character
     * @return final string
     */
    public static String rtrim(String s, char pad) {
        if (s == null) return null;
        char[] chars = s.toCharArray();
        int max = chars.length - 1;
        for (; max >= 0; max--) if (chars[max] != pad) break;
        if (max < 0) return EMPTY_STRING;
        return new String(chars, 0, ++max);
    }

    /**
     * Removes the pad character from the right leaving one if there are no other characters
     *
     * @param s   starting string
     * @param pad pad character
     * @return final string
     */
    public static String rtrim1(String s, char pad) {
        if (s == null) return null;
        char[] chars = s.toCharArray();
        int max = chars.length - 1;
        for (; max >= 0; max--) if (chars[max] != pad) break;
        if (max < 0) return String.valueOf(pad);
        return new String(chars, 0, ++max);
    }

    /**
     * Removes the pad character from the left
     *
     * @param s   starting string
     * @param pad pad character
     * @return final string
     */
    public static String ltrim(String s, char pad) {
        if (s == null) return null;
        char[] chars = s.toCharArray();
        int min = 0;
        for (; min < chars.length; min++) if (chars[min] != pad) break;
        if (min == chars.length) return EMPTY_STRING;
        return new String(chars, min, chars.length - min);
    }

    /**
     * Removes the pad character from the left leaving one if there are no other characters
     *
     * @param s   starting string
     * @param pad pad character
     * @return final string
     */
    public static String ltrim1(String s, char pad) {
        if (s == null) return null;
        char[] chars = s.toCharArray();
        int min = 0;
        for (; min < chars.length; min++) if (chars[min] != pad) break;
        if (min == chars.length) return String.valueOf(pad);
        return new String(chars, min, chars.length - min);
    }

    /**
     * Truncate the string to the right if it exceeds the maximum length allowed
     *
     * @param s starting string
     * @param t maximum length
     * @return final string
     */
    public static String rtrunc(String s, int t) {
        if (s == null) return null;
        int len = s.length();
        return (len > t) ? s.substring(0, t) : s;
    }

    /**
     * Truncate the string to the left if it exceeds the maximum length allowed
     *
     * @param s starting string
     * @param t maximum length
     * @return final string
     */
    public static String ltrunc(String s, int t) {
        if (s == null) return null;
        int len = s.length();
        return (len > t) ? s.substring(len - t) : s;
    }

    /**
     * Indicates whether the string is null or zero-length or made up of only spaces
     *
     * @param cs string to evaluate
     * @return result of the blank string test
     */
    public static boolean isBlank(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates whether the string is null or zero-length or made up of only spaces
     *
     * @param cs string to evaluate
     * @return result of the non-blank string test
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Indicates whether the string is null or zero-length or made up of zeroes only
     *
     * @param cs string to evaluate
     * @return result of the zeroes string test
     */
    public static boolean isZeroes(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (cs.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates whether the string is null or zero-length or made up of zeroes only
     *
     * @param cs string to evaluate
     * @return result of the non-zeroes string test
     */
    public static boolean isNotZeroes(final CharSequence cs) {
        return !isZeroes(cs);
    }

    /**
     * Indicates whether the string is non-null, of length greater than zero and composed of numeric characters only
     *
     * @param cs string to evaluate
     * @return result of the numeric string test
     */
    public static boolean isNumeric(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            final char c = cs.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

//    public static String compressGZip(String message) {
//        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//        try {
//            GZIPOutputStream gzip = new GZIPOutputStream(baos);
//            gzip.write(message.getBytes());
//            gzip.close();
//            byte[] raw = baos.toByteArray();
//            return "echo '" + Base64.getEncoder().encodeToString(raw) +"' | base64 -d | gzip -dc\n";
//        } catch (Exception e) {
//            return message;
//        }
//    }

}

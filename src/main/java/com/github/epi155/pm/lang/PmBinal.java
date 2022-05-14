package com.github.epi155.pm.lang;

/**
 * Boolean functional/DSL style
 *
 * <pre> if (test) { doTrue; } else { doFalse; } </pre>
 * to <pre> Binal.of(test).isTrue(() -> doTrue).isFalse(() -> doFalse) </pre>
 */
class PmBinal implements Binal {

    private final boolean test;

    protected PmBinal(boolean test) {
        this.test = test;
    }

    /**
     * @param action action on <b>true</b>
     * @return instance for action on <b>false</b>
     */
    @Override
    public BinalFalse isTrue(Runnable action) {
        if (test) {
            action.run();
        }
        return this.new PmFalse();
    }

    /**
     * @param action action on <b>false</b>
     * @return instance for action on <b>true</b>
     */
    @Override
    public BinalTrue isFalse(Runnable action) {
        if (!test) {
            action.run();
        }
        return this.new PmTrue();
    }

    /**
     * Handler for <b>true</b> action
     */
    class PmTrue implements BinalTrue {
        private PmTrue() {
        }

        /**
         * @param action action on <b>true</b>
         */
        @Override
        public void isTrue(Runnable action) {
            if (test) {
                action.run();
            }
        }
    }

    /**
     * Handler for <b>false</b> action
     */
    class PmFalse implements BinalFalse {
        private PmFalse() {
        }

        /**
         * @param action action on <b>false</b>
         */
        @Override
        public void isFalse(Runnable action) {
            if (!test) {
                action.run();
            }
        }
    }
}

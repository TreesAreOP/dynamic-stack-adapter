package de.taop.hskl.dynamicStackAdapter;

/**
 * @author Adrian Bernhart
 */
class BuilderNotReadyException extends RuntimeException {

        public BuilderNotReadyException(String message) {
            super(message);
        }

        public BuilderNotReadyException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
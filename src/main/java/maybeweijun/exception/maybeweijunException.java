package maybeweijun.exception;

/**
 * Root exception type for the application. Specific error cases are represented
 * as nested static subclasses to provide semantic meaning and user-friendly messages.
 */
public class MaybeWeijunException extends Exception {
    public MaybeWeijunException() {
        super();
    }

    public MaybeWeijunException(String message) {
        super(message);
    }

    public static class InvalidCommandException extends MaybeWeijunException {
        public InvalidCommandException() {
            super("Unknown command. Please use 'todo', 'deadline', 'mark', 'unmark', or 'list'.");
        }
    }

    public static class DateTimeParseException extends MaybeWeijunException {
        public DateTimeParseException() {
            super("Invalid date/time format found in file");
        }
        public DateTimeParseException(String message) {
            super(message);
        }
    }

    public static class InvalidParametersException extends MaybeWeijunException {
        public InvalidParametersException() {
            super("Invalid parameters. Please check your input.");
        }
        public InvalidParametersException(String message) {
            super(message);
        }
    }

    public static class InvalidDateTimeException extends MaybeWeijunException {
        public InvalidDateTimeException() {
            super("Invalid date/time format. Please use 'yyyy-MM-dd HHmm'.");
        }
        public InvalidDateTimeException(String message) {
            super(message);
        }
    }

    public static class EmptyTodoException extends MaybeWeijunException {
        public EmptyTodoException() {
            super("The description of a todo cannot be empty.");
        }
        public EmptyTodoException(String message) {
            super(message);
        }
    }

    public static class EmptyDeadlineException extends MaybeWeijunException {
        public EmptyDeadlineException() {
            super("Invalid deadline format. Use: deadline <description> /by <date>");
        }
        public EmptyDeadlineException(String message) {
            super(message);
        }
    }

    public static class EmptyEventException extends MaybeWeijunException {
        public EmptyEventException() {
            super("Invalid event format. Use: event <description> /from <start> /to <end>");
        }
        public EmptyEventException(String message) {
            super(message);
        }
    }

    public static class InvalidTaskNumberException extends MaybeWeijunException {
        public InvalidTaskNumberException() {
            super("Invalid task number.");
        }
        public InvalidTaskNumberException(String message) {
            super(message);
        }
    }

    public static class InvalidMarkException extends MaybeWeijunException {
        public InvalidMarkException() {
            super("Please provide a valid task number to mark.");
        }
    }

    public static class InvalidUnmarkException extends MaybeWeijunException {
        public InvalidUnmarkException() {
            super("Please provide a valid task number to unmark.");
        }
    }

    public static class InvalidDeleteException extends MaybeWeijunException {
        public InvalidDeleteException() {
            super("Please provide a valid task number to delete.");
        }
    }

    public static class OnlyTodoException extends MaybeWeijunException {
        public OnlyTodoException() {
            super("You cannot type todo and not do anything");
        }
    }

    public static class OnlyDeadlineException extends MaybeWeijunException {
        public OnlyDeadlineException() {
            super("You cannot type deadline and not do anything");
        }
    }

    public static class OnlyEventException extends MaybeWeijunException {
        public OnlyEventException() {
            super("You cannot type event and not do anything");
        }
    }

    public static class InvalidDateRangeException extends MaybeWeijunException {
        public InvalidDateRangeException() {
            super("End date must be after start date");
        }
        public InvalidDateRangeException(String message) {
            super(message);
        }
    }

    public static class StorageLoadException extends MaybeWeijunException {
        public StorageLoadException() {
            super("Failed to load storage file.");
        }
        public StorageLoadException(String message) {
            super(message);
        }
    }

    public static class StorageSaveException extends MaybeWeijunException {
        public StorageSaveException() {
            super("Failed to save storage file.");
        }
        public StorageSaveException(String message) {
            super(message);
        }
    }

    public static class InvalidStorageFormatException extends MaybeWeijunException {
        public InvalidStorageFormatException() {
            super("Invalid storage file format.");
        }
        public InvalidStorageFormatException(String message) {
            super(message);
        }
    }
}
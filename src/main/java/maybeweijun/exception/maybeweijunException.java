package maybeweijun.exception;

/**
 * Root exception type for the application. Specific error cases are represented
 * as nested static subclasses to provide semantic meaning and user-friendly messages.
 */
public class maybeweijunException extends Exception {
    public maybeweijunException() {
        super();
    }

    public maybeweijunException(String message) {
        super(message);
    }

    public static class InvalidCommandException extends maybeweijunException {
        public InvalidCommandException() {
            super("You... Please use 'todo', 'deadline', 'mark', 'unmark', 'find' or 'list'.");
        }
    }

    public static class EmptyFindException extends maybeweijunException {
        public EmptyFindException() {
            super("The description of a find cannot be empty.");
        }
        public EmptyFindException(String message) {
            super(message);
        }
    }
    public static class DateTimeParseException extends maybeweijunException {
        public DateTimeParseException() {
            super("Invalid date/time format found in file");
        }
        public DateTimeParseException(String message) {
            super(message);
        }
    }

    public static class InvalidParametersException extends maybeweijunException {
        public InvalidParametersException() {
            super("Invalid parameters. Please check your input.");
        }
        public InvalidParametersException(String message) {
            super(message);
        }
    }

    public static class InvalidDateTimeException extends maybeweijunException {
        public InvalidDateTimeException() {
            super("Invalid date/time format. Please use 'yyyy-MM-dd HHmm'.");
        }
        public InvalidDateTimeException(String message) {
            super(message);
        }
    }

    public static class EmptyTodoException extends maybeweijunException {
        public EmptyTodoException() {
            super("The description of a todo cannot be empty.");
        }
        public EmptyTodoException(String message) {
            super(message);
        }
    }

    public static class EmptyDeadlineException extends maybeweijunException {
        public EmptyDeadlineException() {
            super("Invalid deadline format. Use: deadline <description> /by <date>");
        }
        public EmptyDeadlineException(String message) {
            super(message);
        }
    }

    public static class EmptyEventException extends maybeweijunException {
        public EmptyEventException() {
            super("Invalid event format. Use: event <description> /from <start> /to <end>");
        }
        public EmptyEventException(String message) {
            super(message);
        }
    }

    public static class InvalidTaskNumberException extends maybeweijunException {
        public InvalidTaskNumberException() {
            super("Invalid task number.");
        }
        public InvalidTaskNumberException(String message) {
            super(message);
        }
    }

    public static class InvalidMarkException extends maybeweijunException {
        public InvalidMarkException() {
            super("Please provide a valid task number to mark.");
        }
    }

    public static class InvalidUnmarkException extends maybeweijunException {
        public InvalidUnmarkException() {
            super("Please provide a valid task number to unmark.");
        }
    }

    public static class InvalidDeleteException extends maybeweijunException {
        public InvalidDeleteException() {
            super("Please provide a valid task number to delete.");
        }
    }

    public static class OnlyTodoException extends maybeweijunException {
        public OnlyTodoException() {
            super("You cannot type todo and not do anything");
        }
    }

    public static class OnlyDeadlineException extends maybeweijunException {
        public OnlyDeadlineException() {
            super("You cannot type deadline and not do anything");
        }
    }

    public static class OnlyEventException extends maybeweijunException {
        public OnlyEventException() {
            super("You cannot type event and not do anything");
        }
    }

    public static class InvalidDateRangeException extends maybeweijunException {
        public InvalidDateRangeException() {
            super("End date must be after start date");
        }
        public InvalidDateRangeException(String message) {
            super(message);
        }
    }

    public static class StorageLoadException extends maybeweijunException {
        public StorageLoadException() {
            super("Failed to load storage file.");
        }
        public StorageLoadException(String message) {
            super(message);
        }
    }

    public static class StorageSaveException extends maybeweijunException {
        public StorageSaveException() {
            super("Failed to save storage file.");
        }
        public StorageSaveException(String message) {
            super(message);
        }
    }

    public static class InvalidStorageFormatException extends maybeweijunException {
        public InvalidStorageFormatException() {
            super("Invalid storage file format.");
        }
        public InvalidStorageFormatException(String message) {
            super(message);
        }
    }
}
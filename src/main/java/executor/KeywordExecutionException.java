package executor;

public class KeywordExecutionException extends AssertionError {

	private static final long serialVersionUID = 1L;

	private final String keyword;

	public KeywordExecutionException(String keyword, Throwable cause) {

		super("Keyword execution failed: " + keyword + " | Reason: "
				+ (cause != null ? cause.getMessage() : "Unknown error"));

		this.keyword = keyword;

		if (cause != null) {
			initCause(cause);
		}
	}

	public String getKeyword() {

		return keyword;
	}
}
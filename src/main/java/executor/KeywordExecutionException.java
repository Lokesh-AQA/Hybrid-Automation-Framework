package executor;

public class KeywordExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public KeywordExecutionException(String keywordName, Throwable cause) {
		super("Keyword execution failed: " + keywordName, cause);
	}
}

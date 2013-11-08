public class SyntaxError extends RuntimeException {
	private static final long serialVersionUID = -2029283297399035058L;

	private String statement;
	private int lineNumber;
	private int columnNumber;
	
	SyntaxError () {
		this ("General Syntax Error!");
	}
	SyntaxError (String message) {
		this (message, "", -1, 0);
	}
	
	public SyntaxError (String message, String statement, int lineNumber) {
		this (message, statement, lineNumber, 0);
	}
	
	public SyntaxError (String message, String statement, int lineNumber, int columnNumber) {
		super (message);
		this.statement = statement;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}
	
	public String getLocalizedMessage () {
		String trace = "";
		
		trace += this.getMessage();
		trace += " at line " +lineNumber;
		trace += "\n" + statement;
		
		if (columnNumber > 0)
		{
			trace += "\n";
			
			for (int i = 0; i < columnNumber; i++)
				trace += " ";
			
			trace += "^";
		}
		
		return trace; 
	}
}
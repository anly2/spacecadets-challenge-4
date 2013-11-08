public class LogicError extends RuntimeException {
	private static final long serialVersionUID = -7819357994617430352L;

	private String statement;
	private int lineNumber;
	private int columnNumber;
	
	public LogicError () {
		this ("General Logic Error!", "", -1, 0);
	}
	public LogicError (String message) {
		this (message, "", -1, 0);
	}
	
	public LogicError (String message, String statement, int lineNumber) {
		this (message, statement, lineNumber, 0);
	}
	
	public LogicError (String message, String statement, int lineNumber, int columnNumber) {
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
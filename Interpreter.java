import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Interpreter extends Environment {
	//Implement Environment
	HashMap<String, Integer> scope;
	String[] statements;
	int _pc;
	
	Interpreter (String code) {
		 scope = new HashMap<String, Integer>();
		 statements = code.split(";\\s?"); //could be ("\\s*;\\s*")
		 _pc = 0; //Program Counter
	}

	
	protected static ArrayList<Command> commands = new ArrayList<Command>();
	{
		commands.add(new CmdClear());
		commands.add(new CmdIncr());
		commands.add(new CmdDecr());
		commands.add(new CmdWhile());
		commands.add(new CmdEnd());
		commands.add(new CmdJump());
		commands.add(new CmdOutput());
		commands.add(new CmdInput());
	}
	
	
	public Interpreter evaluate () throws SyntaxError, LogicError {
		while (_pc < statements.length)
		{
			String statement = statements[_pc].trim();
			
			if (statement.length() <= 0) {
				_pc++;
				continue;
			}
			
			boolean known = false;
			
			for (Command cmd: commands)
				if (statement.matches(cmd.pattern)) {
					cmd.run(this);
					
					known = true;
					break;
				}
			
			if (!known)
				throw new SyntaxError ("Unknown command", this.statements[this._pc], this._pc);
			
			_pc++;
		}

		return this;
	}

	public boolean condition(String cond) throws SyntaxError
	{
		cond = cond.trim();
		String[] operands = cond.split(" ");
		
		int err_col = 0; //keep track of the col for error-reporting
		boolean state = false;
		
		for (int o = 0; o < operands.length; o++)
		{
			String op = operands[o].toLowerCase();
			err_col += op.length();
			
			if (op.indexOf("not") == 0)
			{
				//Handle case when operands are not supplied (null pointer exception)
				if (o+1 >= operands.length)
					throw new SyntaxError ("Could not find second operand for operator \"not\"", this.statements[this._pc], this._pc, err_col);
				
				if (o-1 < 0)
					state = !(variable(operands[o+1])>0);
				else
					state = !(variable(operands[o-1]).equals(variable(operands[o+1])) );
			}
			else
			
			if (op.indexOf("is") == 0)
			{
				//Handle case when operands are not supplied (null pointer exception)
				if (o+1 >= operands.length)
					throw new SyntaxError ("Could not find second operand for operator \"is\"", this.statements[this._pc], this._pc, err_col);
				
				if (o-1 < 0)
					state = (variable(operands[o+1])>0);
				else
					state = (variable(operands[o-1]).equals(variable(operands[o+1])) );
			}
		}
		
		return state;
	}

	public Integer variable (String name) throws LogicError
	{
		if (scope.containsKey(name))
			return scope.get(name);
		
		try {
			return new Integer(name);
		} catch (Exception e) { }
		
		// Logic Error: undefined variable <<name>>
		throw new LogicError("Undefined variable <<"+name+">>",  this.statements[this._pc], this._pc);
	}
	

	
	public static HashMap<String, Integer> evaluate (String code) {
		Interpreter env = new Interpreter(code).evaluate();
		return env.scope;
	}

	public static void main (String[] args){
		HashMap<String, Integer> result;
		
		/* Determine the address of the source file */ 
			//Default source file 
			String sourceFile = "barebone/sample 5.bbs";
			
			//If there are (/is) argument(s) supplied use that
			if (args.length > 0)
				sourceFile = args[0];
			
			//Handles sloppy users who forget about spaces in command lines
			// We can assume that all the arguments are one address because that is all we expect
			if (args.length > 1) 
				for (int i = 1; i < args.length; i++) //Start from i=1 because we already included the first argument
					sourceFile += " " + args[i]; //Concatenate
			
			//Look in the default folder if the address is just a single file name
			if (sourceFile.indexOf("/") == -1)
				sourceFile = "barebone/"+sourceFile;
		/* End of Address Fetch */
		
		
		/* Evaluate and handle errors */
			try {
				 result = evaluate((new File(sourceFile)).content());
			}
			catch (SyntaxError|LogicError e){
				System.err.print(e.toString());
				return;
			}
		/* End of evaluation */
		
		/* Iterate and print the key=>value pairs */
			Iterator it = result.entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        System.out.println(pairs.getKey() + " = " + pairs.getValue());
			}
		/* End of Iteration */
	}
}

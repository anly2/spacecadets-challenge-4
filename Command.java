import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/* Redundant Class
 * It was supposed to act as an interface but only made things more complicated *
 
abstract class Environment {
	HashMap scope;  // env.scope is the scope containing defined variables and their values
	String[] statements;  // env.statements is a String array listing the statements in the program code
	int _pc;  // env._pc is the environment's program counter
	// env.statements[env._pc]   is the currently run statement
}
*/


public abstract class Command {
	public String name;
	public String pattern;
	public abstract void run (Interpreter env);
	// env.statements[env._pc]   is the currently run statement
}

class CmdClear extends Command{	
	CmdClear () {
		name = "clear";
		pattern = "clear[,\\s]+((,?\\s*([a-zA-Z_]\\w*))+)[;]?";
	}
		
	public void run (Interpreter env) {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
		
		String[] operands = matcher.group(1).split("\\s*,\\s*");
		  
		for (String operand: operands)
			env.scope.put(operand, 0);
	}
}

class CmdIncr extends Command{	
	CmdIncr () {
		name = "increment";
		pattern = "(incr|\\+\\+)[,\\s]+((,?\\s*([a-zA-Z_]\\w*))+)[;]?";
	}
		
	public void run (Interpreter env) throws LogicError {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
		
		String[] operands = matcher.group(2).split("\\s*,\\s*");
		int err_col = 0; //for keeping track of the column
		  
		for (String operand: operands)
		{
			err_col = statement.indexOf(",", err_col+1); //keep track of the column for error-reporting
			
			if (!env.scope.containsKey(operand))
				throw new LogicError ("Undefined variable <<"+operand+">>", statement, env._pc, statement.indexOf(operand, err_col));
			
			env.scope.put(operand, env.scope.get(operand) + 1);
		}
	}
}

class CmdDecr extends Command{	
	CmdDecr () {
		name = "decrement";
		pattern = "(decr|\\-\\-)[,\\s]+((,?\\s*([a-zA-Z_]\\w*))+)[;]?";
	}
		
	public void run (Interpreter env) throws LogicError {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
		
		String[] operands = matcher.group(2).split("\\s*,\\s*");
		int err_col = 0; //for keeping track of the column
		  
		for (String operand: operands)
		{
			err_col = statement.indexOf(",", err_col+1); //keep track of the column for error-reporting
			
			if (!env.scope.containsKey(operand))
				throw new LogicError ("Undefined variable <<"+operand+">>", statement, env._pc, statement.indexOf(operand, err_col));
			
			env.scope.put(operand, env.scope.get(operand) - 1);
		}
	}
}


class CmdWhile extends Command{	
	CmdWhile () {
		name = "while";
		pattern = "while[,\\s]+(.+)[,\\s]+do[;]?";
	}
		
	public void run (Interpreter env) throws SyntaxError {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
	
		int pairedEnd = (new Lookup(env.statements)).matching(env._pc);
		
		if (pairedEnd == -1)
			throw new SyntaxError ("Could not find matching \"end\" statement for \"while\"",  env.statements[env._pc], env._pc);
		
		if (!env.condition(matcher.group(1)))
			env._pc = pairedEnd;
	}
}

class CmdEnd extends Command{	
	CmdEnd () {
		name = "end";
		pattern = "end[,\\s]*[;]?";
	}
		
	public void run (Interpreter env) throws SyntaxError {
		int pairedWhile = (new Lookup(env.statements)).matching(env._pc);
		
		if (pairedWhile == -1)
			throw new SyntaxError ("Could not find matching \"while\" statement for \"end\"", env.statements[env._pc], env._pc);
		
		env._pc = pairedWhile - 1; // -1 to compensate the automatic increment
	}
}

class CmdJump extends Command{	
	CmdJump () {
		name = "jump";
		pattern = "jump[,\\s]((to)\\s(\\d+)|-?\\d+)[;]?";
	}
		
	public void run (Interpreter env) throws SyntaxError {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
		
		int pointer = -1;
		
		// Handle relative format
		if (matcher.group(2) == null) {
			try {
				pointer = (int) new Integer(matcher.group(1));
				
				//Make it hold natural sense? ("jump 1"  means  "skip 1"  not  "jump to next")
				//pointer += (pointer >= 0)? 1 : -1;
			}
			catch (NumberFormatException e) {
				throw new SyntaxError ("Invalid jump pointer!",  env.statements[env._pc], env._pc,  env.statements[env._pc].indexOf(matcher.group(1)));
			}
			
			pointer = env._pc + pointer;
		}
		
		// Handle the normal "jump to" format
		else { 
			try {
				pointer = (int) new Integer(matcher.group(3));
			}
			catch (NumberFormatException e) {
				throw new SyntaxError ("Invalid jump pointer!",  env.statements[env._pc], env._pc,  env.statements[env._pc].indexOf(matcher.group(3)));
			}
		}
		
		if (pointer < 0 || pointer >= env.statements.length)
			throw new SyntaxError ("Jump pointer <<"+pointer+">> out of bounds!",statement, env._pc);
		
		env._pc = pointer - 1; // -1 to compensate the automatic increment
	}
}

class CmdOutput extends Command{	
	CmdOutput () {
		name = "output";
		pattern = "(output|out|<<)[,\\s]+((,?\\s*([a-zA-Z_]\\w*))+)[;]?";
	}
		
	public void run (Interpreter env) throws LogicError {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
		
		String[] operands = matcher.group(2).split("\\s*,\\s*");
		int err_col = 0; //for keeping track of the column
		  
		for (String operand: operands)
		{
			err_col = statement.indexOf(",", err_col+1); //keep track of the column for error-reporting
			
			if (!env.scope.containsKey(operand))
				throw new LogicError ("Undefined variable <<"+operand+">>",  env.statements[env._pc], env._pc, statement.indexOf(operand, err_col));

	        System.out.println(operand + " = " + env.scope.get(operand));
		}
	}
}

class CmdInput extends Command{	
	CmdInput () {
		name = "input";
		pattern = "(input|>>)[,\\s]+((,?\\s*([a-zA-Z_]\\w*))+)[;]?";
	}
		
	public void run (Interpreter env) throws LogicError {
		String statement = env.statements[env._pc].trim();
		
		Matcher matcher = Pattern.compile(pattern).matcher(statement);
		matcher.find(0); //Clean start
		
		String[] operands = matcher.group(2).split("\\s*,\\s*");
		int err_col = 0; //for keeping track of the column
		Scanner scanner = new Scanner(System.in);
		  
		for (String operand: operands)
		{
			err_col = statement.indexOf(",", err_col+1); //keep track of the column for error-reporting
			
			int n;
			
			try {
				System.out.print("Enter value for <<"+operand+">>: ");
				n = scanner.nextInt();
			}
			catch (Exception e) {
				throw new LogicError ("Incorrect input for <<"+operand+">>", env.statements[env._pc], env._pc, statement.indexOf(operand, err_col)); 
			}

	        env.scope.put(operand, n);
		}
		
		scanner.close();
	}
}

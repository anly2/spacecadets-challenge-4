import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class Interprit {
	public static void main (String[] args){
		HashMap<String, Integer> result = evaluate((new File("barebone/sample 1.bbs")).content());
		
		Iterator it = result.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
		}
	}
	
	private static HashMap<String, Integer> scope = new HashMap<String, Integer>();
		
	public static HashMap<String, Integer> evaluate (String code) {
		String[] statements = code.split(";\n?");
		
		evaluate(statements);
		
		return scope;
	}
	public static void evaluate (String[] statements) {		
		for (int i = 0; i < statements.length; i++)
			statement(statements[i],  i,statements);
	}
	
	public static void statement (String s, int programCounter, String[] statements) {
		String cmd;
		String[] operands = s.trim().split("[ ;]");
		
		cmd = "clear";
		if (operands[0].toLowerCase().equals(cmd.toLowerCase())) {
			String var = operands[1];
			scope.put(var, 0);
		}
		
		cmd = "incr";
		if (operands[0].toLowerCase().equals(cmd.toLowerCase())) {
			String var = operands[1];
			scope.put(var, scope.get(var)+1);
		}
		
		cmd = "decr";
		if (operands[0].toLowerCase().equals(cmd.toLowerCase())) {
			String var = operands[1];
			scope.put(var, scope.get(var)+1);
		}
		
		cmd = "while";
		if (operands[0].toLowerCase().equals(cmd.toLowerCase())) {
			int endIndex = closure("while", "end;", statements, programCounter);
			
			String[] loopStatements = Arrays.copyOfRange(statements, programCounter+1, endIndex-1);
			
			////condition
			evaluate(loopStatements);
		}
	}

	public static int closure(String w1, String w2, String[] statements) {
		return closure(w1, w2, statements, 0);
	}
	public static int closure(String w1, String w2, String[] statements, int offset)
	{
		int indent = 0;
		
		for (int j = offset; j < statements.length; j++)
		{
			if (statements[j].trim().toLowerCase().indexOf(w1) == 0)
				indent++;
			
			if (statements[j].trim().toLowerCase().indexOf(w2) == 0)
			{
				indent--;
				if (indent == 0) return j;
			}
		}
		
		return -1;
	}
}

import java.util.ArrayList;

class Lookup {
	private String[] statements;
	
	/* Constructors */
		/** Load a list of statements */
		public Lookup (String[] stmts) {
			loadStatements(stmts);
		}
	/* End of Constructors */
	
		
	/* Accessors */
		/** Load a list of statements */
		public void loadStatements (String[] stmts) {
			this.statements = new String[stmts.length];
			
			//Normalize and store
			for (int i = 0; i < stmts.length; i++)
				this.statements[i] = stmts[i].trim();
		}
	/* End of Accessors */
		
		
	/* Pairs of commands/symbols */
		protected static final ArrayList<Pair> pairs = new ArrayList<Pair>();
		{
			//Order is relevant, as it decides the search direction
			pairs.add(new Pair("\\s*while.*", "\\s*end.*"));
		}
	/* End of Pairs */
		
		
	/* Methods */
		public int matching (int index)
		{
			// The looked up word*
			String w = this.statements[index];
			Pair pair = null;
			
			// TODO needs better usage of pairs rather than just sticking with the first found
			for (Pair p: pairs)
				if (p.has(w))
				{
					pair = p;
					break;
				}
			
			if (pair == null)
				return -1;
			
			
			int level = 0;
			int step = pair.isFirst(w)? 1 : -1;
			
			for (int j = index; (j >= 0) && (j < this.statements.length); j += step)
			{
				String statement = this.statements[j];
				int iMatched = 0; //inverted matched <- what we actually need
				
				if (pair.isFirst(statement)) {
					level += step;
					iMatched = -1;
				}
				else
				if (pair.isSecond(statement)) {
					level -= step;
					iMatched = 1;
				}
				
				if (level == 0 && iMatched == step)
					return j;
			}
			
			return -1;
		}
		
		/** Looks up the word starting at the specified index and returns the starting index of the closing sequence (/word) */
		public int matching (String code, int index) {
			//TODO the actual implementation for a whole string
			// Potentially easier if Pair contains regex-ions   // while m.find()
			return -1;	
		}
	/* End of Methods */
}
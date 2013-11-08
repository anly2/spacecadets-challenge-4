
public class Pair {
	protected String string1;
	protected String string2;
	
	public Pair (String s1, String s2) {
		this.string1 = s1;
		this.string2 = s2;
	}
	
	public Pair (String s1, String s2, boolean transformToLowerCase) {
		this.string1 = s1.toLowerCase();
		this.string2 = s2.toLowerCase();
	}
	
	public String first () {
		return this.string1;	
	}
	
	public String second () {
		return this.string2;
	}
		
	public boolean has (String s) {
		return (this.isFirst(s) || this.isSecond(s));
	}
	
	public boolean isFirst (String s) {
		return (s.matches(this.string1));
	}
	
	public boolean isSecond (String s) {
		return (s.matches(this.string2));
	}
}

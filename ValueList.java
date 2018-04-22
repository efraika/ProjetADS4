import java.util.HashMap;

public class ValueList extends HashMap<String, IsVar>{

	public ValueList (){}

	public int getIntValue (String s) throws Exception {
		if (!this.containsKey(s)){
			throw new Exception ("Constant " + s + " doesn't exist");
		}
		return this.get(s).getVal();
	}

	public boolean isVar(String s) throws Exception {
		if(!this.containsKey(s)){
			throw new Exception("Constant "+s+" doesn't exist");
		}
		return this.get(s).isVar();
	}

	public void addValue (String s, IsVar n) throws Exception {
		this.put(s, n);
	}
}

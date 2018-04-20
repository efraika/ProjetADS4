import java.util.HashMap;

public class ConstantEnvironment extends HashMap<String, Integer>{

	public ConstantEnvironment (){}
	
	public int getConstant (String s) throws Exception {
		if (!this.containsKey(s)){
			throw new Exception ("Constant " + s + " doesn't exists");
		}
		return this.get(s);
	}

	public void addConstant (String s, int n) throws Exception {
		this.put(s, n);
	}
}

import java.util.HashMap;

public class ValueList extends HashMap<String, Value>{

	public ValueList (){}

	public void addValue (String s, int n, boolean isVar) throws Exception {
		if (!this.containsKey(s)){
			this.put(s, new Value(n, isVar));
		}else{
			throw new Exception ("Variable " + s + " already defined");
		}
	}

	public Value getValue (String s) {
		if (this.containsKey(s)){
			return this.get(s);
		}
		return null;
	}

	public boolean changeValue (String s, int n) throws Exception {
		if (!this.containsKey(s)) {
			return false;
		}
		if(!this.get(s).isVar()){
			throw new Exception ("Variable " + s + " is a constant");
		}
		this.put(s, new Value(n, true));
		return true;
	}

	public ValueList clone (){
		ValueList copy = new ValueList();
		Object [] keys = this.keySet().toArray();
		for (int i = 0; i < keys.length; i++){
			copy.put((String)keys[i], this.get(keys[i]));
		}
		return copy;
	}
}

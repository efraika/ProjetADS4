import java.util.LinkedList;
import java.util.HashMap;

public class ValueEnvironment extends LinkedList<ValueList> {

	public ValueEnvironment (){}

	public void addValue (String s, int n, boolean isVar) throws Exception {
		this.peek().addValue(s, n, isVar);
	}

	public int getValue (String s) throws Exception {
		for (int i = 0; i < this.size(); i++){
			Value v = this.get(i).getValue(s);
			if (v != null){
				return v.getVal();
			}
		}
		throw new Exception ("Variable " + s + " doesn't exists");
	}

	public void changeValue (String s, int n) throws Exception {
		for (int i = 0; i < this.size(); i++){
			if (this.get(i).changeValue(s, n))
				return;
		}
		throw new Exception ("Variable " + s + " doesn't exists");
	}

	public ValueEnvironment clone (){
		ValueEnvironment copy = new ValueEnvironment ();
		for (int i = 0; i < this.size(); i++){
			copy.add(this.get(i).clone());
		}
		return copy;
	}
}

class ValueList extends HashMap<String, Value>{

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

class Value{

	private int value;
	private final boolean isVar;

	public Value(int n, boolean b){
		this.value = n;
 		this.isVar = b;
	}

	public int getVal(){
		return this.value;
	}

	public boolean isVar(){
		return this.isVar;
	}
}

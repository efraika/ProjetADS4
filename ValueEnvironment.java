import java.util.*;

class ValueEnvironment extends LinkedList<ValueList> {

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

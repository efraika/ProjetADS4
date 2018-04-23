import java.util.LinkedList;
import java.util.HashMap;
import java.util.Objects;

public class FunctionEnvironment extends LinkedList<FunctionList> {

	public FunctionEnvironment (){}

	public void addFunction (String name, String [] args, Program p, ValueEnvironment varEnv) throws Exception {
		this.peek().addFunction(name, args, p, varEnv);
	}

	public Function getFunction (String name, int nbArgs) throws Exception {
		for (int i = 0; i < this.size(); i++){
			Function f = this.get(i).getFunction(name, nbArgs);
			if (f != null){
				return f;
			}
		}
		throw new Exception ("Function " + name + " doesn't exists");
	}
}

class FunctionList extends HashMap<Signature, Function> {

	public FunctionList (){}

	public void addFunction (String name, String [] args, Program p, ValueEnvironment varEnv) throws Exception {
		Signature newFunctionSign = new Signature(name, args.length);
		if (!this.containsKey(newFunctionSign)){
			this.put(newFunctionSign, new Function (args, p, varEnv));
		}else{
			throw new Exception ("Function signature already exists");
		}
	}

	public Function getFunction (String name, int nbArgs){
		Signature sign = new Signature(name, nbArgs);
		if (this.containsKey(sign)){
			return this.get(sign);
		}
		return null;
	}
}

class Signature {
	private String name;
	private int nbArgs;

	public Signature (String s, int n){
		this.name = s;
		this.nbArgs = n;
	}

	public boolean equals(Object obj){
		if (obj == this){
			return true;
		}
		if (obj == null || !(obj instanceof Signature)){
			return false;
		}
		Signature s = (Signature) obj;
		return (this.name.equals(s.name) && this.nbArgs == s.nbArgs);
	}

	public int hashCode (){
		return Objects.hash(this.name, this.nbArgs);
	}
}

class Function {
	private String [] args;
	private Program body;
	private ValueEnvironment declarationEnv;

	public Function (String [] args, Program p , ValueEnvironment varEnv){
		this.args = args;
		this.body = p;
		this.declarationEnv = varEnv;
	}

	public String [] getArgs (){
		return this.args;
	}

	public Program getProgram (){
		return this.body;
	}

	//La valeur n est forcément entre 0 et (args.length - 1)
	public String getArgs (int n){
		return this.args[n];
	}

	public ValueEnvironment getDeclarationEnv (){
		return this.declarationEnv;
	}
}

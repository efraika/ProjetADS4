import java.util.ArrayList;
import java.awt.Graphics2D;

abstract class Expression {
	public abstract int eval(ValueEnvironment env) throws Exception;
}

class Int extends Expression {
	private int value;

	public Int (int n) {
		this.value = n;
	}

	public int eval (ValueEnvironment env) {
		return this.value;
	}
}

class Var extends Expression {
	private String name;

	public Var (String s) {
		this.name = s;
	}

	public int eval (ValueEnvironment env) throws Exception {
		return env.getValue(name);
	}
}

class Sum extends Expression {
	private Expression left, right;

	public Sum (Expression e1, Expression e2) {
		this.left = e1;
		this.right = e2;
	}

	public int eval (ValueEnvironment env) throws Exception {
		return (left.eval(env) + right.eval(env));
	}
}

class Difference extends Expression {
	private Expression left, right;

	public Difference (Expression e1, Expression e2) {
		this.left = e1;
		this.right = e2;
	}

	public int eval (ValueEnvironment env) throws Exception {
		return (left.eval(env) - right.eval(env));
	}
}

class Product extends Expression {
	private Expression left, right;

	public Product (Expression e1, Expression e2) {
		this.left = e1;
		this.right = e2;
	}

	public int eval (ValueEnvironment env) throws Exception {
		return (left.eval(env) * right.eval(env));
	}
}

class Quotient extends Expression {
	private Expression left, right;

	public Quotient (Expression e1, Expression e2) {
		this.left = e1;
		this.right = e2;
	}

	public int eval (ValueEnvironment env) throws Exception {
		return (left.eval(env) / right.eval(env));
	}
}

class Program extends ArrayList<Instruction> {
	public void run(Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		varEnv.addFirst(new ValueList());
		funcEnv.addFirst(new FunctionList());
		for (Instruction inst : this) {
			inst.exec(g, varEnv, funcEnv);
		}
		varEnv.remove();
		funcEnv.remove();
	}

	public void run (Graphics2D g) throws Exception {
		this.run(g, new ValueEnvironment(), new FunctionEnvironment());
	}
}

abstract class Instruction {
	public abstract void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception;
}

class Declaration extends Instruction {
	private String name;
	private Expression e;
	private final boolean isVar;

	public Declaration (String name, Expression e, boolean b){
		this.name = name;
		this.e = e;
		this.isVar = b ;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		varEnv.addValue(name, e.eval(varEnv), isVar);
	}
}

class DeclarationFunction extends Instruction {
	private String name;
	private String [] args;
	private Program p;

	public DeclarationFunction (String s, ArrayList<String> var, Program p){
		this.name = s;
		this.args = new String [var.size()];
		for (int i = 0; i < var.size(); i++){
			this.args[i] = var.get(i);
		}
		this.p = p;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		funcEnv.addFunction(name, args, p, varEnv.clone());
	}
}

class Assignation extends Instruction {
	private String name;
	private Expression e;

	public Assignation (String name, Expression e){
		this.name = name;
		this.e = e;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		varEnv.changeValue(name, e.eval(varEnv));
	}
}

class DoFunction extends Instruction {
	String name;
	Expression [] args;

	public DoFunction (String s, ArrayList<Expression> var){
		this.name = s;
		this.args = new Expression [var.size()];
		for (int i = 0; i < var.size(); i++){
			this.args[i] = var.get(i);
		}
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		Function f = funcEnv.getFunction(name, args.length);
		ValueEnvironment env = f.getDeclarationEnv();
		env.addFirst(new ValueList());
		for (int i = 0; i < args.length; i++){
			env.addValue(f.getArgs(i), args[i].eval(varEnv), true);
		}
		f.getProgram().run(g, env, funcEnv);
	}
}

class Draw extends Instruction {
	private Figure f;

	public Draw (Figure f){
		this.f = f;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		f.draw(g, varEnv);
	}
}

class Bloc extends Instruction {
	private Program p;

	public Bloc (Program p){
		this.p = p;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		p.run(g, varEnv, funcEnv);
	}
}

class Conditional extends Instruction {
	private Expression bool;
	private Instruction ifTrue;
	private Instruction ifFalse;

	public Conditional (Expression b, Instruction ifTrue, Instruction ifFalse){
		this.bool = b;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		if (this.bool.eval(varEnv) == 0){
			this.ifTrue.exec(g, varEnv, funcEnv);
		}else{
			this.ifFalse.exec(g, varEnv, funcEnv);
		}
	}
}

class Loop extends Instruction {
	private Expression e;
	private Program p;

	public Loop (Expression e, Program p){
		this.e = e;
		this.p = p;
	}

	public void exec (Graphics2D g, ValueEnvironment varEnv, FunctionEnvironment funcEnv) throws Exception {
		int nbLoop = e.eval(varEnv);
		for (int i = 0; i < nbLoop; i++){
			this.p.run(g, varEnv, funcEnv);
		}
	}
}

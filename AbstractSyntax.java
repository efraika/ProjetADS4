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

class Constant extends Expression {
	private String name;

	public Constant (String s) {
		this.name = s;
	}

	public int eval (ValueEnvironment env) throws Exception {
		return env.getList().getIntValue(name);
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
	public void run(Graphics2D g, ValueEnvironment env) throws Exception {
		for (Instruction inst : this) {
			inst.exec(g, env);
		}
	}

	public void run (Graphics2D g) throws Exception {
		ValueEnvironment env = new ValueEnvironment();
		for (Instruction inst : this){
			inst.exec(g, env);
		}
	}
}

abstract class Instruction {
	public abstract void exec (Graphics2D g, ValueEnvironment env) throws Exception;
}

class Declaration extends Instruction {
	private String name;
	private Expression e;
	private final boolean type;

	public Declaration (String name, Expression e, boolean b){
		this.name = name;
		this.e = e;
		this.type = b ;
	}

	public void exec (Graphics2D g, ValueEnvironment env) throws Exception {
		env.getList().addValue(name, new IsVar( e.eval(env), type));
	}
}

class Assignation extends Instruction {
	private String name;
	private Expression e;

	public Assignation (String name, Expression e){
		this.name = name;
		this.e = e;
	}

	public void exec (Graphics2D g, ValueEnvironment env) throws Exception {
		if(env.getList().isVar(name)){
			env.getList().addValue(name, new IsVar(e.eval(env), true));
		}else throw new Exception("You can't change the value of a constant");
	}
}

class Draw extends Instruction {
	private Figure f;

	public Draw (Figure f){
		this.f = f;
	}

	public void exec (Graphics2D g, ValueEnvironment env) throws Exception{
		f.draw(g, env);
	}
}

class Bloc extends Instruction {
	private Program p;

	public Bloc (Program p){
		this.p = p;
	}

	public void exec (Graphics2D g, ValueEnvironment env) throws Exception{
		ValueList newList= new ValueList();
		newList.putAll(env.getList());
		env.addList(newList);
		p.run(g, env);
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

	public void exec (Graphics2D g, ValueEnvironment env) throws Exception{
		if (this.bool.eval(env) == 0){
			this.ifTrue.exec(g, env);
		}else{
			this.ifFalse.exec(g, env);
		}
	}
}

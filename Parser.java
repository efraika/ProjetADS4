import java.io.*;
import java.util.*;

class Parser{

  //prog -> suiteInst $
  //inst -> Begin suiteInst End
  //		| DrawCircle(expr, expr, expr, couleur)
  //		| FillCircle (expr, expr, expr, couleur)
  //		| DrawRect(expr, expr, expr, expr, couleur)
  //		| FillRect(expr, expr, expr, expr, couleur)
  //		| Const identificateur = expr
  //suiteInst -> inst ; suiteInst | epsilon
  //expr -> nombre | (expr operateur expr) | identificateur

  private LookAhead reader;

  public Parser(LookAhead r){reader=r;}

  //prog -> suiteInst $
  public Program nontermProg() throws Exception {
	Program p = nontermSInst();
	reader.eat(Sym.EOF);
	return p;
  }

  //expr -> nombre | (expr operateur expr) | identificateur
  public Expression nontermExp() throws Exception {
 	if(reader.check(Sym.NOMBRE)){
	  Int value = new Int (reader.getIntValue());
      reader.eat(Sym.NOMBRE);
	  return value;
    }else if (reader.check(Sym.ID)) {
	  Variable value = new Variable(reader.getStringValue());
      reader.eat(Sym.ID);
	  return value;
    }else if (reader.check(Sym.LPAR)){
      reader.eat(Sym.LPAR);
      Expression e1 = nontermExp();
	  String op = reader.getStringValue();
      reader.eat(Sym.OP);
      Expression e2 = nontermExp();
      reader.eat(Sym.RPAR);
	  switch (op){
	  		case "+" :	return new Sum (e1, e2);
			case "-" :	return new Difference (e1, e2);
	  		case "*" :	return new Product (e1, e2);
			case "/" :	return new Quotient (e1, e2);
			default  :	throw new Exception ("Operateur doesn't exists");
	  }
    }else{
		throw new Exception ("'(', identificateur or number not found");
	}
  }

  //inst -> Begin suiteInst End
  //		| DrawCircle(expr, expr, expr, couleur)
  //		| FillCircle (expr, expr, expr, couleur)
  //		| DrawRect(expr, expr, expr, expr, couleur)
  //		| FillRect(expr, expr, expr, expr, couleur)
  //		| Const identificateur = expr
  //		| If expr Then Inst Else Inst
  //		| Var identificateur = expr
  //		| identificateur = expr
  public Instruction nontermInst() throws  Exception {
    if(reader.check(Sym.CONST)){
      reader.eat(Sym.CONST);
	  String name = reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.EQ);
      return new Declaration (name, nontermExp(), false);
    }else if(reader.check(Sym.BEGIN)){
      reader.eat(Sym.BEGIN);
      Program p = nontermSInst();
      reader.eat(Sym.END);
	  return new Bloc(p);
    }else if (reader.check(Sym.IF)) {
      reader.eat(Sym.IF);
      Expression bool = nontermExp();
      reader.eat(Sym.THEN);
      Instruction ifTrue = nontermInst();
      reader.eat(Sym.ELSE);
      Instruction ifFalse = nontermInst();
	  return new Conditional(bool, ifTrue, ifFalse);
    }else if (reader.check(Sym.VAR)) {
      reader.eat(Sym.VAR);
	  String name = reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.EQ);
      return new Declaration (name, nontermExp(), true);
    }else if (reader.check(Sym.ID)) {
	  String name = reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.EQ);
      return new Assignation(name, nontermExp());
    }else if (reader.check(Sym.DRAWC)) {
      reader.eat(Sym.DRAWC);
      reader.eat(Sym.LPAR);
      Expression x = nontermExp();
      reader.eat(Sym.VIRG);
      Expression y = nontermExp();
      reader.eat(Sym.VIRG);
      Expression rayon = nontermExp();
      reader.eat(Sym.VIRG);
	  String color = reader.getStringValue();
      reader.eat(Sym.COULEUR);
      reader.eat(Sym.RPAR);
	  return new Draw(new Circle(color, x, y, rayon, true));
    }else if(reader.check(Sym.FILLC)){
      reader.eat(Sym.FILLC);
      reader.eat(Sym.LPAR);
      Expression x = nontermExp();
      reader.eat(Sym.VIRG);
      Expression y = nontermExp();
      reader.eat(Sym.VIRG);
      Expression rayon = nontermExp();
      reader.eat(Sym.VIRG);
	  String color = reader.getStringValue();
      reader.eat(Sym.COULEUR);
      reader.eat(Sym.RPAR);
	  return new Draw(new Circle(color, x, y, rayon, false));
    }else if (reader.check(Sym.DRAWR)) {
      reader.eat(Sym.DRAWR);
      reader.eat(Sym.LPAR);
      Expression x = nontermExp();
      reader.eat(Sym.VIRG);
      Expression y = nontermExp();
      reader.eat(Sym.VIRG);
      Expression length = nontermExp();
      reader.eat(Sym.VIRG);
      Expression height = nontermExp();
      reader.eat(Sym.VIRG);
	  String color = reader.getStringValue();
      reader.eat(Sym.COULEUR);
      reader.eat(Sym.RPAR);
	  return new Draw(new Rect(color, x, y, length, height, true));
    }else if (reader.check(Sym.FILLR)){
      reader.eat(Sym.FILLR);
      reader.eat(Sym.LPAR);
      Expression x = nontermExp();
      reader.eat(Sym.VIRG);
      Expression y = nontermExp();
      reader.eat(Sym.VIRG);
      Expression length = nontermExp();
      reader.eat(Sym.VIRG);
      Expression height = nontermExp();
      reader.eat(Sym.VIRG);
	  String color = reader.getStringValue();
      reader.eat(Sym.COULEUR);
      reader.eat(Sym.RPAR);
	  return new Draw(new Rect(color, x, y, length, height, false));
    }else{
		throw new Exception ("'Const', 'Begin', 'DrawCircle', 'FillCircle', 'DrawRect' or 'FillRect' not found");
	}
  }

  //suiteInst -> inst ; suiteInst | epsilon
  public Program nontermSInst() throws Exception {
	Program p = new Program();
	if(reader.check(Sym.EOF) || reader.check(Sym.END)){
		return p;
	}else{
		p.add(nontermInst());
		reader.eat(Sym.PVIRG);
		p.addAll(nontermSInst());
		return p;
	}
  }
}

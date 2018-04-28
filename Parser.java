import java.io.*;
import java.util.*;

class Parser{

  //prog -> suiteInst $
  //inst -> Begin suiteInst End
  //		| DrawCircle(expr, expr, expr, couleur)
  //		| FillCircle (expr, expr, expr, couleur)
  //		| DrawRect(expr, expr, expr, expr, couleur)
  //		| FillRect(expr, expr, expr, expr, couleur)
  //		| If expr Then Inst Else Inst
  //		| Const identificateur = expr
  //		| Var identificateur = expr
  //		| identificateur = expr
  //		| Def id (args) suiteInst End
  //		| Do id (expressions)
  //		| While expr do suiteInst End
  //    	| For (identificateur, expr, expr, expr ) suiteInst End
  //suiteInst -> inst ; suiteInst | epsilon
  //expr -> nombre | (expr operateur expr) | identificateur
  //args -> id suiteArgs | epsilon
  //suiteArgs -> , id suiteArgs | epsilon
  //nombres -> expr suiteNombres | epsilon
  //suiteNombres -> , expr suiteNombres | epsilon

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
    }else if (reader.check(Sym.ID)){
	  Var value = new Var(reader.getStringValue());
      reader.eat(Sym.ID);
	  return value;
    }else if (reader.check(Sym.LPAR)){
      reader.eat(Sym.LPAR);
      Expression e1 = nontermExp();
	  String ope = termOperateur();
      Expression e2 = nontermExp();
      reader.eat(Sym.RPAR);
	  return new Operation (e1, e2, ope);
    }else{
		throw new Exception ("'(', identificateur or number not found");
	}
  }

  //operateur -> + | - | * | /
  public String termOperateur () throws Exception {
  	if (reader.check(Sym.OP)){
		String operateur = reader.getStringValue();
		reader.eat(Sym.OP);
		return operateur;
	}else{
		throw new Exception ("'+', '-', '*' or '/' not found");
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
  //		| Def id (args) suiteIinst End
  //		| Do id (expressions)
  //		| While expr do suiteInst End
  //    	| For (identificateur, expr, expr, expr ) suiteInst End
  public Instruction nontermInst() throws  Exception {
    if(reader.check(Sym.CONST)){
      reader.eat(Sym.CONST);
	  String name = reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.EQ);
      return new Declaration (name, nontermExp(), true);
    }else if(reader.check(Sym.BEGIN)){
      reader.eat(Sym.BEGIN);
      Program p = nontermSInst();
      reader.eat(Sym.END);
	  return new Bloc(p);
    }else if (reader.check(Sym.IF)){
      reader.eat(Sym.IF);
      Expression bool = nontermExp();
      reader.eat(Sym.THEN);
      Instruction ifTrue = nontermInst();
      reader.eat(Sym.ELSE);
      Instruction ifFalse = nontermInst();
	  return new Conditional(bool, ifTrue, ifFalse);
    }else if (reader.check(Sym.VAR)){
      reader.eat(Sym.VAR);
	  String name = reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.EQ);
      return new Declaration (name, nontermExp(), false);
    }else if (reader.check(Sym.ID)){
	  String name = reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.EQ);
      return new Assignation(name, nontermExp());
	}else if (reader.check(Sym.DEF)){
	  reader.eat(Sym.DEF);
	  String name = reader.getStringValue();
	  reader.eat(Sym.ID);
	  reader.eat(Sym.LPAR);
	  ArrayList<String> args = nontermArgs();
	  reader.eat(Sym.RPAR);
	  Program p = nontermSInst();
	  reader.eat(Sym.END);
	  return new DeclarationFunction(name, args, p);
	}else if (reader.check(Sym.DO)){
	  reader.eat(Sym.DO);
	  String name = reader.getStringValue();
	  reader.eat(Sym.ID);
	  reader.eat(Sym.LPAR);
	  ArrayList<Expression> args = nontermNombres();
	  reader.eat(Sym.RPAR);
	  return new DoFunction(name, args);
	}else if (reader.check(Sym.WHILE)){
	  reader.eat(Sym.WHILE);
	  Expression e = nontermExp();
	  reader.eat(Sym.DO);
	  Program p = nontermSInst();
	  reader.eat(Sym.END);
	  return new While (e, p);
    }else if (reader.check(Sym.DRAWC)){
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
    }else if (reader.check(Sym.DRAWR)){
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
    }else if (reader.check(Sym.FOR)){
      reader.eat(Sym.FOR);
      reader.eat(Sym.LPAR);
      String i= reader.getStringValue();
      reader.eat(Sym.ID);
      reader.eat(Sym.VIRG);
      Expression a= nontermExp();
      reader.eat(Sym.VIRG);
      Expression b= nontermExp();
      reader.eat(Sym.VIRG);
      Expression c= nontermExp();
      reader.eat(Sym.RPAR);
      Program inst= nontermSInst();
      reader.eat(Sym.END);
      return new For(i,a,b,c,inst);
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

  //args -> id suiteArgs | epsilon
  //suiteArgs -> , id suiteArgs | epsilon
  public ArrayList<String> nontermArgs() throws Exception {
  	ArrayList<String> e = new ArrayList<String>();
	if (reader.check(Sym.RPAR)){
		return e;
	}else if (reader.check(Sym.ID)){
		e.add(reader.getStringValue());
		reader.eat(Sym.ID);
		while (!reader.check(Sym.RPAR)){
			reader.eat(Sym.VIRG);
			e.add(reader.getStringValue());
			reader.eat(Sym.ID);
		}
		return e;
	}else{
		throw new Exception ("'(' or id not found");
	}
  }

  //nombres -> expr suiteNombres | epsilon
  //suiteNombres -> , expr suiteNombres | epsilon
  public ArrayList<Expression> nontermNombres () throws Exception {
  	ArrayList<Expression> e = new ArrayList<Expression>();
	if (reader.check(Sym.RPAR)){
		return e;
	}else{
		e.add(nontermExp());
		while (!reader.check(Sym.RPAR)){
			reader.eat(Sym.VIRG);
			e.add(nontermExp());
		}
		return e;
	}
  }
}

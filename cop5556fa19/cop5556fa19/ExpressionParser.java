/**
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */

package cop5556fa19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cop5556fa19.AST.Block;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldList;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class ExpressionParser {
	
	@SuppressWarnings("serial")
	class SyntaxException extends Exception {
		Token t;
		
		public SyntaxException(Token t, String message) {
			super(t.line + ":" + t.pos + " " + message);
		}
	}
	
	final Scanner scanner;
	Token t;  //invariant:  this is the next token


	ExpressionParser(Scanner s) throws Exception {
		this.scanner = s;
		t = scanner.getNext(); //establish invariant
	}


	Exp exp() throws Exception {
		Token first = t;
		Exp e0 = andExp();
		while (isKind(KW_or)) {
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}

	
	private Exp andExp() throws Exception {
		// TODO Auto-generated method stub
		Token first = t;
		Exp e0 = comExp();
		while (isKind(KW_and)) {
			Token op = consume();
			Exp e1 = comExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
//		throw new UnsupportedOperationException("andExp");  //I find this is a more useful placeholder than returning null.
	}

	private Exp comExp() throws Exception {
		Token first = t;
		Exp e0 = biorExp();
		while (isKind(REL_EQEQ, REL_NOTEQ, REL_LE, REL_GE, REL_LT, REL_GT)) {
			Token op = consume();
			Exp e1 = biorExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp biorExp() throws Exception {
		Token first = t;
		Exp e0 = bixorExp();
		while (isKind(BIT_OR)) {
			Token op = consume();
			Exp e1 = bixorExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp bixorExp() throws Exception {
		Token first = t;
		Exp e0 = biampExp();
		while (isKind(BIT_XOR)) {
			Token op = consume();
			Exp e1 = biampExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp biampExp() throws Exception {
		Token first = t;
		Exp e0 = bishiExp();
		while (isKind(BIT_AMP)) {
			Token op = consume();
			Exp e1 = bishiExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp bishiExp() throws Exception {
		Token first = t;
		Exp e0 = canExp();
		while (isKind(BIT_SHIFTL, BIT_SHIFTR)) {
			Token op = consume();
			Exp e1 = canExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp canExp() throws Exception {
		Token first = t;
		Exp e0 = addExp();
		if (isKind(DOTDOT)) {
			Token op = t;
			Exp e1 = canTail();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp canTail() throws Exception {
		Token first = consume();
		first = t;
		Exp e0 = addExp();
		if (isKind(DOTDOT)) {
			Token op = t;
			Exp e1 = canTail();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp addExp() throws Exception {
		Token first = t;
		Exp e0 = mulExp();
		while (isKind(OP_PLUS, OP_MINUS)) {
			Token op = consume();
			Exp e1 = mulExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp mulExp() throws Exception {
		Token first = t;
		Exp e0 = unaryExp();
		while (isKind(OP_TIMES, OP_DIV, OP_MOD, OP_DIVDIV)) {
			Token op = consume();
			Exp e1 = unaryExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp unaryExp() throws Exception {
		Token first = t;
		Exp e0 = null;
		if (isKind(KW_not, OP_HASH, OP_MINUS, BIT_XOR)) {
			Token op = consume();
			e0 = unaryExp();
			e0 = new ExpUnary(first, op.kind, e0);
		} else {
			e0 = powExp();
		}
		return e0;
	}
	
	private Exp powExp() throws Exception {
		Token first = t;
		Exp e0 = term();
		if (isKind(OP_POW)) {
			Token op = t;
			Exp e1 = powTail();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp powTail() throws Exception {
		Token first = consume();
		first = t;
		Exp e0 = term();
		if (isKind(OP_POW)) {
			Token op = t;
			Exp e1 = powTail();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	
	private Exp term() throws Exception {
		if (isKind(KW_nil)) {
			Token first = consume();
			return new ExpNil(first);
		} else if (isKind(KW_true)) {
			Token first = consume();
			return new ExpTrue(first);
		} else if (isKind(KW_false)) {
			Token first = consume();
			return new ExpFalse(first);
		} else if (isKind(INTLIT)) {
			Token first = consume();
			return new ExpInt(first);
		} else if (isKind(STRINGLIT)) {
			Token first = consume();
			return new ExpString(first);
		} else if (isKind(DOTDOTDOT)) {
			Token first = consume();
			return new ExpVarArgs(first);
		} else if (isKind(KW_function)) {
			Token first = consume();
			FuncBody body = functionBody();
			return new ExpFunction(first, body);
		} else if (isKind(NAME)) {
			Token first = consume();
			return new ExpName(first);
		} else if (isKind(LCURLY)) {
			Token first = consume();
			FieldList fieldList = null;
			List<Field> fields = new ArrayList<Field>();
			if (!(isKind(RCURLY))) { fieldList = fieldList(); fields = fieldList.fields;}
			match(RCURLY);
			return new ExpTable(t, fields);
		} else if (isKind(LPAREN)) {
			Token first = consume();
			Exp e0 = exp();
			match(RPAREN);
			return e0;
		} else {
			error(t.kind);
			return null;
		}
	}
	
	private FuncBody functionBody() throws Exception{
		if (isKind(LPAREN)) {
			Token first = consume();
			ParList p = null;
			if (!(isKind(RPAREN))) { p = parList();}
			match(RPAREN);
			Block b = block();
			match(KW_end);
			return new FuncBody(first, p, b);
		} else {
			error(LPAREN);
			return null;
		}
	}
	
	private ParList parList() throws Exception{
		if (isKind(DOTDOTDOT)) {
			Token first = consume();
			List<Name> nameList = null;
			return new ParList(first, nameList, false);
		} else if (isKind(NAME)) {
			List<Name> nameList = new ArrayList<Name>();
			Token first = consume();
			nameList.add(new Name(first, first.text));
			while (isKind(COMMA)) {
				Token tmp = consume();
				if (isKind(NAME)) {
					tmp = consume();
					nameList.add(new Name(tmp, tmp.text));
				} else {
					error(NAME);
				}
			}
			return new ParList(first, nameList, true);
		} else {
			error(DOTDOTDOT, NAME);
			return null;
		}
	}
	
	private FieldList fieldList() throws Exception{
		List<Field> fields = new ArrayList<Field>();
		Token first = t;
		fields.add(field());
		while (isKind(COMMA, SEMI)) {
			Token tmp = consume();
			if (!(isKind(RCURLY))) { fields.add(field());}
			else { break;}
		}
		return new FieldList(first, fields);
	}
	
	private Field field() throws Exception{
		if (isKind(LSQUARE)) {
			Token first = consume();
			Exp key = exp();
			match(RSQUARE);
			match(ASSIGN);
			Exp value = exp();
			return new FieldExpKey(first, key, value);
		} else if (isKind(NAME)) {
			Token first = consume();
			match(ASSIGN);
			Exp value = exp();
			return new FieldNameKey(first, new Name(first, first.text), value);
		} else {
			Token first = t;
			Exp value = exp();
			return new FieldImplicitKey(first, value);
		}
	}
	
	private Block block() {
		return new Block(null);  //this is OK for Assignment 2
	}

	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind kind) throws Exception {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		error(kind);
		return null; // unreachable
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind... kinds) throws Exception {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		error(kinds);
		return null; // unreachable
	}

	Token consume() throws Exception {
		Token tmp = t;
        t = scanner.getNext();
		return tmp;
	}
	
	void error(Kind... expectedKinds) throws SyntaxException {
		String kinds = Arrays.toString(expectedKinds);
		String message;
		if (expectedKinds.length == 1) {
			message = "Expected " + kinds + " at " + t.line + ":" + t.pos;
		} else {
			message = "Expected one of" + kinds + " at " + t.line + ":" + t.pos;
		}
		throw new SyntaxException(t, message);
	}

	void error(Token t, String m) throws SyntaxException {
		String message = m + " at " + t.line + ":" + t.pos;
		throw new SyntaxException(t, message);
	}
	


}

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
import cop5556fa19.AST.Chunk;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldList;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.FuncName;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.AST.RetStat;
import cop5556fa19.AST.Stat;
import cop5556fa19.AST.StatAssign;
import cop5556fa19.AST.StatBreak;
import cop5556fa19.AST.StatDo;
import cop5556fa19.AST.StatFor;
import cop5556fa19.AST.StatForEach;
import cop5556fa19.AST.StatFunction;
import cop5556fa19.AST.StatGoto;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatLocalAssign;
import cop5556fa19.AST.StatLocalFunc;
import cop5556fa19.AST.StatRepeat;
import cop5556fa19.AST.StatWhile;
import cop5556fa19.AST.Var;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class Parser {
	
	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;
		
		public SyntaxException(Token t, String message) {
			super(t.line + ":" + t.pos + " " + message);
		}
	}
	
	final Scanner scanner;
	Token t;  //invariant:  this is the next token


	Parser(Scanner s) throws Exception {
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
	
	Exp exp(Token first) throws Exception {
		Exp e0 = andExp(first);
		while (isKind(KW_or)) {
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
	}
	
	private Exp andExp() throws Exception {
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
	
	private Exp andExp(Token first) throws Exception {
		Exp e0 = comExp(first);
		while (isKind(KW_and)) {
			Token op = consume();
			Exp e1 = comExp();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;
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
	
	private Exp comExp(Token first) throws Exception {
		Exp e0 = biorExp(first);
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
	
	private Exp biorExp(Token first) throws Exception {
		Exp e0 = bixorExp(first);
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
	
	private Exp bixorExp(Token first) throws Exception {
		Exp e0 = biampExp(first);
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
	
	private Exp biampExp(Token first) throws Exception {
		Exp e0 = bishiExp(first);
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
	
	private Exp bishiExp(Token first) throws Exception {
		Exp e0 = canExp(first);
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
	
	private Exp canExp(Token first) throws Exception {
		Exp e0 = addExp(first);
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
	
	private Exp addExp(Token first) throws Exception {
		Exp e0 = mulExp(first);
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
	
	private Exp mulExp(Token first) throws Exception {
		Exp e0 = unaryExp(first);
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
	
	private Exp unaryExp(Token first) throws Exception {
		Exp e0 = null;
		if (isKind(KW_not, OP_HASH, OP_MINUS, BIT_XOR)) {
			Token op = consume();
			e0 = unaryExp();
			e0 = new ExpUnary(first, op.kind, e0);
		} else {
			e0 = powExp(first);
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
	
	private Exp powExp(Token first) throws Exception {
		Exp e0 = term(first);
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
			Exp e0 = new ExpName(first);
			if (isKind(LPAREN, LSQUARE, DOT, COLON, LCURLY, STRINGLIT)) {
				e0 = prefixExpTail(first, e0);
			}
			return e0;
		} else if (isKind(LCURLY)) {
			consume();
			FieldList fieldList = null;
			List<Field> fields = new ArrayList<Field>();
			if (!(isKind(RCURLY))) { fieldList = fieldList(); fields = fieldList.fields;}
			match(RCURLY);
			return new ExpTable(t, fields);
		} else if (isKind(LPAREN)) {
			Token first = consume();
			Exp e0 = exp();
			match(RPAREN);
			if (isKind(LPAREN, LSQUARE, DOT, COLON, LCURLY, STRINGLIT)) {
				e0 = prefixExpTail(first, e0);
			}
			return e0;
		} else {
			error(t.kind);
			return null;
		}
	}
	
	private Exp term(Token first) throws Exception {
		if (first.kind == KW_nil) {
			return new ExpNil(first);
		} else if (first.kind == KW_true) {
			return new ExpTrue(first);
		} else if (first.kind == KW_false) {
			return new ExpFalse(first);
		} else if (first.kind == INTLIT) {
			return new ExpInt(first);
		} else if (first.kind == STRINGLIT) {
			return new ExpString(first);
		} else if (first.kind == DOTDOTDOT) {
			return new ExpVarArgs(first);
		} else if (first.kind == KW_function) {
			FuncBody body = functionBody();
			return new ExpFunction(first, body);
		} else if (first.kind == NAME) {
			Exp e0 = new ExpName(first);
			if (isKind(LPAREN, LSQUARE, DOT, COLON, LCURLY, STRINGLIT)) {
				e0 = prefixExpTail(first, e0);
			}
			return e0;
		} else if (first.kind == LCURLY) {
			FieldList fieldList = null;
			List<Field> fields = new ArrayList<Field>();
			if (!(isKind(RCURLY))) { fieldList = fieldList(); fields = fieldList.fields;}
			match(RCURLY);
			return new ExpTable(t, fields);
		} else if (first.kind == LPAREN) {
			Exp e0 = exp();
			match(RPAREN);
			if (isKind(LPAREN, LSQUARE, DOT, COLON, LCURLY, STRINGLIT)) {
				e0 = prefixExpTail(first, e0);
			}
			return e0;
		} else {
			error(first.kind);
			return null;
		}
	}
	
	private FuncBody functionBody() throws Exception{
		Token first = t;
		if (isKind(LPAREN)) {
			consume();
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
			return new ParList(first, nameList, true);
		} else if (isKind(NAME)) {
			List<Name> nameList = new ArrayList<Name>();
			Token first = consume();
			nameList.add(new Name(first, first.text));
			while (isKind(COMMA)) {
				Token tmp = consume();
				if (isKind(NAME)) {
					tmp = consume();
					nameList.add(new Name(tmp, tmp.text));
				} else if (isKind(DOTDOTDOT)) {
					tmp = consume();
					return new ParList(first, nameList, true);
				} else {
					error(NAME);
				}
			}
			return new ParList(first, nameList, false);
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
			consume();
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
			if (isKind(ASSIGN)) {
				match(ASSIGN);
				Exp value = exp();
				return new FieldNameKey(first, new Name(first, first.text), value);
			} else {
				Exp value = exp(first);
				return new FieldImplicitKey(first, value);
			}
		} else {
			Token first = t;
			Exp value = exp();
			return new FieldImplicitKey(first, value);
		}
	}
	
	private List<Exp> expList() throws Exception{
		List<Exp> es = new ArrayList<Exp>();
		es.add(exp());
		while (isKind(COMMA)) {
			consume();
			es.add(exp());
		}
		return es;
	}
	
	private List<Exp> expList(List<Exp> es) throws Exception{
		es.add(exp());
		while (isKind(COMMA)) {
			consume();
			es.add(exp());
		}
		return es;
	}
	
	private List<Exp> argList() throws Exception{
		List<Exp> args = new ArrayList<Exp>();
		if (isKind(LPAREN)) {
			consume();
			if (!(isKind(RPAREN))) { args = expList();}
			match(RPAREN);
		} else if (isKind(LCURLY)) {
			Token tmp = consume();
			FieldList fieldList = null;
			List<Field> fields = new ArrayList<Field>();
			if (!(isKind(RCURLY))) { fieldList = fieldList(); fields = fieldList.fields;}
			match(RCURLY);
			args.add(new ExpTable(tmp, fields));
		} else if (isKind(STRINGLIT)) {
			Token tmp = consume();
			args.add(new ExpString(tmp));
		}
		return args;		
	}
	
	private List<Exp> argList(List<Exp> args) throws Exception{
		if (isKind(LPAREN)) {
			consume();
			if (!(isKind(RPAREN))) { args = expList(args);}
			match(RPAREN);
		} else if (isKind(LCURLY)) {
			Token tmp = consume();
			FieldList fieldList = null;
			List<Field> fields = new ArrayList<Field>();
			if (!(isKind(RCURLY))) { fieldList = fieldList(); fields = fieldList.fields;}
			match(RCURLY);
			args.add(new ExpTable(tmp, fields));
		} else if (isKind(STRINGLIT)) {
			Token tmp = consume();
			args.add(new ExpString(tmp));
		}
		return args;		
	}
	
	private Exp prefixExpTail(Token first, Exp e) throws Exception{
		Exp e1 = null;
		if (isKind(LPAREN, LCURLY, STRINGLIT)) {
			List<Exp> args = new ArrayList<Exp>();
			args = argList();
			e1 =  new ExpFunctionCall(first, e, args);
		} else if (isKind(LSQUARE)) {
			consume();
			Exp key = exp();
			match(RSQUARE);
			e1 = new ExpTableLookup(first, e, key);
		} else if (isKind(DOT)) {
			consume();
			if (isKind(NAME)) {
				Token tmp = consume();
				Exp key = new ExpName(tmp);
				e1  = new ExpTableLookup(first, e, key);
			} else {
				error(t.kind, NAME);
			}
		} else if (isKind(COLON)) {
			consume();
			if (isKind(NAME)) {
				Token tmp = consume();
				Exp key = new ExpName(tmp);
				e1 = new ExpTableLookup(first, e, key);
				if (isKind(LPAREN, LCURLY, STRINGLIT)) {
					List<Exp> args = new ArrayList<Exp>();
					args.add(e);
					args = argList();
					e1 = new ExpFunctionCall(first, e1, args);
				} else {
					error(t.kind, LPAREN, LCURLY, STRINGLIT);
				}
			}
		} else {
			error(t.kind, LPAREN, LCURLY, STRINGLIT, LSQUARE, DOT, COLON);
		}
		if (isKind(LPAREN, LCURLY, STRINGLIT, LSQUARE, DOT, COLON)) {
			e1 = prefixExpTail(first, e1);
		}
		return e1;
	}
	
	Stat stat() throws Exception {
		Token first = t;
		Stat stat = null;
		if (isKind(SEMI)) {
			stat = null;
		} else if (isKind(KW_break)) {
			consume();
			stat = new StatBreak(first);
		} else if (isKind(COLONCOLON)) {
			consume();
			if (isKind(NAME)) {
				Token tmp = consume();
				Name label = new Name(tmp, tmp.getName());
				match(COLONCOLON);
				stat = new StatLabel(first, label);
			} else {
				error(t.kind, NAME);
			}
		} else if (isKind(KW_goto)) {
			consume();
			if (isKind(NAME)) {
				Token tmp = consume();
				Name name = new Name(tmp, tmp.getName());
				stat = new StatGoto(first, name);
			} else {
				error(t.kind, NAME);
			}
		} else if (isKind(KW_repeat)) {
			consume();
			Block b = block();
			match(KW_until);
			Exp e = exp();
			stat = new StatRepeat(first, b, e);
		} else if (isKind(KW_while)) {
			consume();
			Exp e = exp();
			match(KW_do);
			Block b = block();
			match(KW_end);
			stat = new StatWhile(first, e, b);
		} else if (isKind(KW_do)) {
			consume();
			Block b = block();
			match(KW_end);
			stat = new StatDo(first, b);
		} else if (isKind(KW_if)) {
			consume();
			List<Exp> es = new ArrayList<Exp>();
			List<Block> bs = new ArrayList<Block>();
			es.add(exp());
			match(KW_then);
			bs.add(block());
			while (isKind(KW_elseif)) {
				consume();
				es.add(exp());
				match(KW_then);
				bs.add(block());
			}
			if (isKind(KW_else)) {
				consume();
				bs.add(block());
			}
			match(KW_end);
			stat = new StatIf(first, es, bs);
		} else if (isKind(KW_function)) {
			consume();
			FuncName name = funcName();
			FuncBody body = functionBody();
			stat = new StatFunction(first, name, body);
		} else if (isKind(NAME)) {
			List<Exp> vs = new ArrayList<Exp>();
			List<Exp> es = new ArrayList<Exp>();
			vs = varList();
			match(ASSIGN);
			es = expList();
			stat = new StatAssign(first, vs, es);
		} else if (isKind(KW_local)) {
			consume();
			if (isKind(KW_function)) {
				consume();
				if (isKind(NAME)) {
					Token tmp = consume();
					List<ExpName> names = new ArrayList<ExpName>();
					names.add(new ExpName(tmp));
					FuncName name = new FuncName(tmp, names, null);
					FuncBody body = functionBody();
					stat = new StatLocalFunc(first, name, body);
				} else {
					error(t.kind, NAME);
				}
			} else if (isKind(NAME)) {
				List<ExpName> names = new ArrayList<ExpName>();
				List<Exp> es = new ArrayList<Exp>();
				names = nameList();
				if (isKind(ASSIGN)) {
					es = expList();
				}
				stat = new StatLocalAssign(first, names, es);
			} else {
				error(t.kind, KW_function, NAME);
			}
		} else if (isKind(KW_for)) {
			consume();
			if (isKind(NAME)) {
				Token tmp = consume();
				ExpName name = new ExpName(tmp);
				if (isKind(ASSIGN)) {
					consume();
					Exp ebeg = exp();
					match(COMMA);
					Exp eend = exp();
					Exp einc = null;
					if (isKind(COMMA)) {
						consume();
						einc = exp();
					}
					match(KW_do);
					Block g = block();
					match(KW_end);
					stat = new StatFor(first, name, ebeg, eend, einc, g);
				} else if (isKind(COMMA, KW_in)) {
					List<ExpName> names = new ArrayList<ExpName>();
					List<Exp> es = new ArrayList<Exp>();
					names.add(name);
					if (isKind(COMMA)) {
						consume();
						names = nameList(names);
						match(KW_in);
					} else {
						consume();
					}
					es = expList();
					match(KW_do);
					Block b = block();
					match(KW_end);
					stat = new StatForEach(first, names, es, b);	
				} else {
					error(t.kind, ASSIGN, COMMA, KW_in);
				}
			} else {
				error(t, "Cannot use at beginning of statement.");
			}
		}
		return stat;
	}
	
	
	private FuncName funcName() throws Exception {
		Token first = t;
		List<ExpName> names = new ArrayList<ExpName>();
		ExpName nameAfterColon = null;
		if (isKind(NAME)) {
			Token tmp = consume();
			names.add(new ExpName(tmp));
			while (isKind(DOT)) {
				consume();
				if (isKind(NAME)) {
					tmp = consume();
					names.add(new ExpName(tmp));
				} else {
					error(NAME);
				}
			}
			if (isKind(COLON)) {
				consume();
				if (isKind(NAME)) {
					tmp = consume();
					nameAfterColon = new ExpName(tmp);
				} else {
					error(NAME);
				}
			}
		} else {
			error(NAME);
		}
		return new FuncName(first, names, nameAfterColon);
	}
	
	private Exp var() throws Exception {
		if (isKind(NAME)) {
			Token first = consume();
			Exp e0 = new ExpName(first);
			if (isKind(LSQUARE, DOT)) {
				e0 = varTail(first, e0);
			}
			return e0;
		} else {
			error(t.kind, NAME);
			return null;
		}
	}
	
	private Exp varTail(Token first, Exp e) throws Exception {
		Exp e1 = null;
		if (isKind(LSQUARE)) {
			consume();
			Exp key = exp();
			match(RSQUARE);
			e1 = new ExpTableLookup(first, e, key);
		} else if (isKind(DOT)) {
			consume();
			if (isKind(NAME)) {
				Token tmp = consume();
				Exp key = new ExpName(tmp);
				e1 = new ExpTableLookup(first, e, key);
			}
		} else {
			error(t.kind, LSQUARE, DOT);
		}
		if (isKind(LPAREN, LCURLY, STRINGLIT, LSQUARE, DOT, COLON)) {
			e1 = prefixExpTail(first, e1);
		}
		return e1;
	}
	
	private List<Exp> varList() throws Exception {
		List<Exp> vs = new ArrayList<Exp>();
		vs.add(var());
		while (isKind(COMMA)) {
			consume();
			vs.add(var());
		}
		return vs;
	}
	
	private List<ExpName> nameList() throws Exception {
		List<ExpName> ns = new ArrayList<ExpName>();
		Token name = null;
		if (isKind(NAME)) {
			name = consume();
			ns.add(new ExpName(name));
			while (isKind(COMMA)) {
				consume();
				if (isKind(NAME)) {
					name = consume();
					ns.add(new ExpName(name));
				} else {
					error(t.kind, NAME);
				}
			}
		} else {
			error(t.kind, NAME);
		}
		return ns;
	}
	
	private List<ExpName> nameList(List<ExpName> ns) throws Exception {
		Token name = null;
		if (isKind(NAME)) {
			name = consume();
			ns.add(new ExpName(name));
			while (isKind(COMMA)) {
				consume();
				if (isKind(NAME)) {
					name = consume();
					ns.add(new ExpName(name));
				} else {
					error(t.kind, NAME);
				}
			}
		} else {
			error(t.kind, NAME);
		}
		return ns;
	}
	
	RetStat retStat() throws Exception {
		Token first = t;
		List<Exp> el = new ArrayList<Exp>();
		match(KW_return);
		if (isExpStart()) {
			el = expList();
		}
		if (isKind(SEMI)) {
			consume();
		}
		return new RetStat(first, el);
	}
	
	Block block() throws Exception {
		Token first = t;
		List<Stat> stats = new ArrayList<Stat>();
		Stat stat = null;
		while (isStatStart()) {
			stat = stat();
			stats.add(stat);
		}
		if (isKind(KW_return)) {
			RetStat retStat = retStat();
			stats.add(retStat);
		}
		return new Block(first, stats);
	}
	
	Chunk chunk() throws Exception {
		Token first = t;
		Block b = block();
		return new Chunk(first, b);
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
	
	protected boolean isExpStart() {
		return isKind(KW_nil, KW_false, KW_true, INTLIT, STRINGLIT, DOTDOTDOT, KW_function, NAME, LPAREN, LCURLY, OP_MINUS, KW_not, OP_HASH, BIT_XOR);
	}
	
	protected boolean isStatStart() {
		return isKind(COLONCOLON, NAME, SEMI, KW_break, KW_goto, KW_do, KW_while, KW_repeat, KW_if, KW_for, KW_function, KW_local);
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

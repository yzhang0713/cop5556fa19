package interpreter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cop5556fa19.Token;
import cop5556fa19.AST.ASTVisitor;
import cop5556fa19.AST.Block;
import cop5556fa19.AST.Chunk;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpList;
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
import java.lang.Math;

public abstract class ASTVisitorAdapter implements ASTVisitor {
	
	@SuppressWarnings("serial")
	public static class StaticSemanticException extends Exception{
		
			public StaticSemanticException(Token first, String msg) {
				super(first.line + ":" + first.pos + " " + msg);
			}
		}
	
	
	@SuppressWarnings("serial")
	public
	static class TypeException extends Exception{

		public TypeException(String msg) {
			super(msg);
		}
		
		public TypeException(Token first, String msg) {
			super(first.line + ":" + first.pos + " " + msg);
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class GotoException extends Exception {
		StatLabel label = null;
	}
	
	@SuppressWarnings("serial")
	public static class InLoopBreakException extends Exception {
	
	}
	
	@SuppressWarnings("serial")
	public static class BreakException extends Exception {
	
	}
	
	@SuppressWarnings("serial")
	public static class ReturnException extends Exception {
		List<LuaValue> vals = new ArrayList<LuaValue>();
	}
	
	@SuppressWarnings("serial")
	public static class TableLookupException extends Exception {
		LuaTable table = null;
		LuaValue key = null;
	}
	
	public abstract List<LuaValue> load(Reader r) throws Exception;

	@Override
	public Object visitExpNil(ExpNil expNil, Object arg) {
		LuaValue val = LuaNil.nil;
		return val;
	}

	@Override
	public Object visitExpBin(ExpBinary expBin, Object arg) throws Exception {
		Exp e0 = expBin.e0;
		Exp e1 = expBin.e1;
		Token.Kind op = expBin.op;
		LuaValue v0 = (LuaValue) e0.visit(this, arg);
		LuaValue v1 = (LuaValue) e1.visit(this, arg);
//		System.out.println(op.toString());
		switch (op) {
			case DOTDOT: {
				if (v0 instanceof LuaString && v1 instanceof LuaString) {
					return (LuaValue) new LuaString(((LuaString) v0).value + ((LuaString) v1).value);
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case OP_PLUS: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v + ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			} 
			case OP_MINUS: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v - ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case OP_TIMES: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v * ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case OP_DIV: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v / ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case OP_DIVDIV: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(Math.floorDiv(((LuaInt) v0).v, ((LuaInt) v1).v)); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case OP_MOD: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v % ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case OP_POW: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt((int) Math.pow(((LuaInt) v0).v, ((LuaInt) v1).v)); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case REL_GT: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaBoolean(((LuaInt) v0).v > ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case REL_LT: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaBoolean(((LuaInt) v0).v < ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case REL_EQEQ: {
				if (v0.equals(v1)) {
					return (LuaValue) new LuaBoolean(true); 
				} else {
					return (LuaValue) new LuaBoolean(false);
				}
			}
			case REL_GE: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaBoolean(((LuaInt) v0).v >= ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case REL_LE: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaBoolean(((LuaInt) v0).v <= ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case REL_NOTEQ: {
				if (v0.equals(v1)) {
					return (LuaValue) new LuaBoolean(false);
				} else {
					return (LuaValue) new LuaBoolean(true);
				}
			}
			case KW_and: {
				if (v0 instanceof LuaBoolean && v1 instanceof LuaBoolean) {
					return (LuaValue) new LuaBoolean(((LuaBoolean) v0).value && ((LuaBoolean) v1).value); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case KW_or: {
				if (v0 instanceof LuaBoolean && v1 instanceof LuaBoolean) {
					return (LuaValue) new LuaBoolean(((LuaBoolean) v0).value || ((LuaBoolean) v1).value); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case BIT_AMP: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v & ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case BIT_OR: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v | ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case BIT_XOR: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v ^ ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case BIT_SHIFTL: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v << ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			case BIT_SHIFTR: {
				if (v0 instanceof LuaInt && v1 instanceof LuaInt) {
					return (LuaValue) new LuaInt(((LuaInt) v0).v >> ((LuaInt) v1).v); 
				} else {
					throw new StaticSemanticException(expBin.firstToken, "Cannot perform binary operation.");
				}
			}
			default: {
				throw new StaticSemanticException(expBin.firstToken, "Invalid operator.");
			}
		}
	}

	@Override
	public Object visitUnExp(ExpUnary unExp, Object arg) throws Exception {
		Exp e = unExp.e;
		Token.Kind op = unExp.op;
		LuaValue v = (LuaValue) e.visit(this, arg);
		switch (op) {
			case OP_MINUS: {
				if (v instanceof LuaInt) {
					return (LuaValue) new LuaInt(-(((LuaInt) v).v));
				} else {
					throw new StaticSemanticException(unExp.firstToken, "Cannot perform unary operation.");
				}
			}
			case KW_not: {
				if (v instanceof LuaBoolean) {
					return (LuaValue) new LuaBoolean(!(((LuaBoolean) v).value));
				} else {
					throw new StaticSemanticException(unExp.firstToken, "Cannot perform unary operation.");
				}
			}
			case BIT_XOR: {
				if (v instanceof LuaInt) {
					return (LuaValue) new LuaInt(~(((LuaInt) v).v));
				} else {
					throw new StaticSemanticException(unExp.firstToken, "Cannot perform unary operation.");
				}
			}
			case OP_HASH: {
				if (v instanceof LuaString) {
					return (LuaValue) new LuaInt(((LuaString) v).value.length());
				} else {
					throw new StaticSemanticException(unExp.firstToken, "Cannot perform unary operation.");
				}
			}
			default: {
				throw new StaticSemanticException(unExp.firstToken, "Invalid operator.");
			}
		}
	}

	@Override
	public Object visitExpInt(ExpInt expInt, Object arg) {
		LuaValue val = new LuaInt(expInt.v);
		return val;
	}

	@Override
	public Object visitExpString(ExpString expString, Object arg) {
		LuaValue val = new LuaString(expString.v);
		return val;
	}

	@Override
	public Object visitExpTable(ExpTable expTableConstr, Object arg) throws Exception {
		List<Field> fields = expTableConstr.fields;
		LuaTable table = new LuaTable();
		LuaValue key = null;
		LuaValue val = null;
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i) instanceof FieldExpKey) {
				LuaValue[] p = (LuaValue[]) fields.get(i).visit(this, arg);
				key = p[0];
				val = p[1];
				table.put(key, val);
			} else if (fields.get(i) instanceof FieldNameKey) {
				LuaValue[] p = (LuaValue[]) fields.get(i).visit(this, arg);
				key = p[0];
				val = p[1];
				table.put(key, val);
			} else if (fields.get(i) instanceof FieldImplicitKey) {
				val = (LuaValue) fields.get(i).visit(this, arg);
				table.putImplicit(val);
			} else {
				throw new StaticSemanticException(expTableConstr.firstToken, "Error building table.");
			}
		}
		return table;
	}

	@Override
	public Object visitExpList(ExpList expList, Object arg) throws Exception {
		List<LuaValue> vals = new ArrayList<LuaValue>();
		List<Exp> es = expList.list;
		for (int i = 0; i < es.size(); i++) {
			vals.add((LuaValue) es.get(i).visit(this, arg));
		}
		return vals;
	}

	@Override
	public Object visitParList(ParList parList, Object arg) throws Exception {
		List<Name> nameList = parList.nameList;
		List<LuaValue> vals = new ArrayList<LuaValue>();
		for (int i = 0; i < nameList.size(); i++) {
			vals.add((LuaValue) nameList.get(i).visit(this, arg));
		}
		return vals;
	}

	@Override
	public Object visitFunDef(ExpFunction funcDec, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitName(Name name, Object arg) {
		LuaValue val = new LuaString(name.name);
		return val;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		List<Stat> stats = block.stats;
		if (stats.isEmpty()) {
			return null;
		} else {
			Boolean finished = false;
			for (int i = 0; i < stats.size() && !finished; i++) {
//				System.out.println(i);
				try {
//					System.out.println(stats.get(i).toString());
					stats.get(i).visit(this, arg);
				}
				catch(BreakException eBreak) {
					break;
				}
				catch(GotoException eGoto) {
//					System.out.println(eGoto.label.toString()+" exception");
//					Boolean check = block.equals(eGoto.label.enclosingBlock);
//					System.out.println(check);
					if (block.equals(eGoto.label.enclosingBlock)) {
						eGoto.label.visit(this, arg);
						break;
					} else {
						throw eGoto;
					}
				}
			}
			return null;
		}
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg, Object arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg) throws Exception {
		if (((LuaTable) arg).get("inLoop").equals(new LuaBoolean(true))) {
			throw new InLoopBreakException();
		} else {
			throw new BreakException();
		}
	}

	@Override
	public Object visitStatGoto(StatGoto statGoto, Object arg) throws Exception {
//		statGoto.label.visit(this, arg);
		GotoException eGoto = new GotoException();
		eGoto.label = statGoto.label;
//		System.out.println(statGoto.label.toString());
//		System.out.println(statGoto.label.enclosingBlock.toString());
		throw eGoto;
	}

	@Override
	public Object visitStatDo(StatDo statDo, Object arg) throws Exception {
		((LuaTable) arg).put("inDoBlock", new LuaBoolean(true)); 
		statDo.b.visit(this, arg);
		return null;
	}

	@Override
	public Object visitStatWhile(StatWhile statWhile, Object arg) throws Exception {
		Exp e = statWhile.e;
		Block b = statWhile.b;
		while (((LuaBoolean) e.visit(this, arg)).value) {
			((LuaTable) arg).put("inLoop", new LuaBoolean(true));
			try {
				b.visit(this, arg);
			}
			catch(InLoopBreakException eBreak) {
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitStatRepeat(StatRepeat statRepeat, Object arg) throws Exception {
		Block b = statRepeat.b;
		Exp e = statRepeat.e;
		b.visit(this, arg);
		while (((LuaBoolean) e.visit(this, arg)).value) {
			((LuaTable) arg).put("inLoop", new LuaBoolean(true));
			try {
				b.visit(this, arg);
			}
			catch(InLoopBreakException eBreak) {
				break;
			}
		}
		return null;
	}

	@Override
	public Object visitStatIf(StatIf statIf, Object arg) throws Exception {
		List<Exp> es = statIf.es;
		List<Block> bs = statIf.bs;
		Boolean executed = false;
		for (int i = 0; i < es.size(); i++) {
			LuaValue val = (LuaValue) es.get(i).visit(this, arg);
//			System.out.println(val.toString());
			if (val.equals(new LuaBoolean(true)) || val.equals(new LuaInt(0))) {
				executed = true;
				bs.get(i).visit(this, arg);
				break;
			} else if (val.equals(new LuaBoolean(false)) || val.equals(LuaNil.nil)) {
				continue;
			} else {
				throw new StaticSemanticException(statIf.firstToken, "Invalid logic value in if statement.");
			}
		}
		if (bs.size() > es.size() && !executed) {
			bs.get(bs.size()-1).visit(this,arg);
		}
		return null;
	}

	@Override
	public Object visitStatFor(StatFor statFor1, Object arg) throws Exception {
		ExpName name = statFor1.name;
		Exp ebeg = statFor1.ebeg;
		Exp eend = statFor1.eend;
		Exp einc = statFor1.einc;
		Block g = statFor1.g;
		Exp name1 = new ExpString(name.firstToken);
		LuaValue v = (LuaValue) name1.visit(this, arg);
		LuaValue vbeg = (LuaValue) ebeg.visit(this, arg);
		LuaValue vend = (LuaValue) eend.visit(this, arg);
		LuaValue vinc = (LuaValue) einc.visit(this, arg);
		for (((LuaInt) v).v = ((LuaInt) vbeg).v; ((LuaInt) v).v <= ((LuaInt) vend).v; ((LuaInt) v).v += ((LuaInt) vinc).v) {
			g.visit(this,  arg);
		}
		return null;
	}

	@Override
	public Object visitStatForEach(StatForEach statForEach, Object arg) throws Exception {
//		List<ExpName> names = statForEach.names;
//		List<Exp> exps = statForEach.exps;
//		Block b = statForEach.b;
//		List<LuaValue> keys = new ArrayList<LuaValue>();
//		Exp name1 = null;
//		for (ExpName name : names) {
//			name1 = new ExpString(name.firstToken);
//			keys.add((LuaValue) name1.visit(this, arg));
//		}
//		LuaValue val = null;
//		for (Exp e : exps) {
//			val = (LuaValue) e.visit(this, arg);
//			for (int i = 0; i <= keys.size() - 1; i++) {
//				((LuaTable) arg).put(keys.get(i), val.get(i));
//			}
//		}
		return null;
	}

	@Override
	public Object visitFuncName(FuncName funcName, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatFunction(StatFunction statFunction, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatLocalFunc(StatLocalFunc statLocalFunc, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatLocalAssign(StatLocalAssign statLocalAssign, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitRetStat(RetStat retStat, Object arg) throws Exception {
        ExpList expList = new ExpList(retStat.firstToken, retStat.el);
		List<LuaValue> vals = (List<LuaValue>) expList.visit(this,arg);
		ReturnException eRet = new ReturnException();
		eRet.vals = vals;
		throw eRet;
	}

	@Override
	public Object visitChunk(Chunk chunk, Object arg) throws Exception {
		List<LuaValue> vals = null;
		try {
			chunk.block.visit(this, arg);
		}
		catch(ReturnException eRet) {
			vals = eRet.vals;
		}
//		System.out.println(vals.toString());
//		System.out.println(arg.toString());
		return vals;
	}

	@Override
	public Object visitFieldExpKey(FieldExpKey fieldExpKey, Object arg) throws Exception {
		LuaValue[] pair = new LuaValue[2];
		pair[0] = (LuaValue) fieldExpKey.key.visit(this, arg);
		pair[1] = (LuaValue) fieldExpKey.value.visit(this, arg);
		return pair;
	}

	@Override
	public Object visitFieldNameKey(FieldNameKey fieldNameKey, Object arg) throws Exception {
		LuaValue[] pair = new LuaValue[2];
		pair[0] = (LuaValue) fieldNameKey.name.visit(this, arg);
		pair[1] = (LuaValue) fieldNameKey.exp.visit(this, arg);
		return pair;
	}
	
	@Override
	public Object visitFieldImplicitKey(FieldImplicitKey fieldImplicitKey, Object arg) throws Exception {
		return fieldImplicitKey.exp.visit(this, arg);
	}

	@Override
	public Object visitExpTrue(ExpTrue expTrue, Object arg) {
		return new LuaBoolean(true);
	}

	@Override
	public Object visitExpFalse(ExpFalse expFalse, Object arg) {
		return new LuaBoolean(false);
	}

	@Override
	public Object visitFuncBody(FuncBody funcBody, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpVarArgs(ExpVarArgs expVarArgs, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatAssign(StatAssign statAssign, Object arg) throws Exception {
		List<Exp> varList = statAssign.varList;
		List<Exp> expList = statAssign.expList;
		LuaValue key;
		LuaValue val;
		LuaTable table = null;
		Boolean isTable = false;
		for (int i = 0; i < varList.size(); i++) {
			if (varList.get(i) instanceof ExpName) {
				Exp var = new ExpString(varList.get(i).firstToken);
				key = (LuaValue) var.visit(this, arg);
			} else if (varList.get(i) instanceof ExpTableLookup) {
				isTable = true;
//				System.out.println(((ExpTableLookup) varList.get(i)).table.toString());
				table = (LuaTable) ((ExpTableLookup) varList.get(i)).table.visit(this, arg);
				key = (LuaValue) ((ExpTableLookup) varList.get(i)).key.visit(this, arg);
			} else {
				key = (LuaValue) varList.get(i).visit(this, arg);
			}
			if (i < expList.size()) {
				val = (LuaValue) expList.get(i).visit(this, arg);
			} else {
				val = LuaNil.nil;
			}
			if (isTable) {
				table.put(key, val);
			} else {
				((LuaTable) arg).put(key, val);
			}
		}
//		System.out.println(((LuaTable) arg).toString());
		return null;
	}

	@Override
	public Object visitExpTableLookup(ExpTableLookup expTableLookup, Object arg) throws Exception {
//		Exp table = expTableLookup.table;
//		Exp key = expTableLookup.key;
//		LuaTable table = (LuaTable) ((LuaTable) arg).get((LuaValue) expTableLookup.table.visit(this, arg));
		LuaTable table = (LuaTable) expTableLookup.table.visit(this, arg);
		LuaValue key = (LuaValue) expTableLookup.key.visit(this, arg);
		return table.get(key);
	}

	@Override
	public Object visitExpFunctionCall(ExpFunctionCall expFunctionCall, Object arg) throws Exception {
//		System.out.println(expFunctionCall.toString());
		return null;
	}

	@Override
	public Object visitLabel(StatLabel statLabel, Object arg) throws Exception {
		Block b = statLabel.enclosingBlock;
		int index = statLabel.index;
		List<Stat> stats = b.stats;
		if (index == stats.size()-1) {
			return null;
		} else {
			for (int i = index+1; i < stats.size(); i++) {
				try {
					stats.get(i).visit(this, arg);
				}
				catch(BreakException eBreak) {
					break;
				}
				catch(GotoException eGoto) {
					if (b.equals(eGoto.label.enclosingBlock)) {
						eGoto.label.visit(this, arg);
						break;
					} else {
						throw eGoto;
					}
				}
			}
			return null;
		}
	}

	@Override
	public Object visitFieldList(FieldList fieldList, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpName(ExpName expName, Object arg) {
		LuaValue val = ((LuaTable) arg).get(new LuaString(expName.name));
		return val;
	}



}

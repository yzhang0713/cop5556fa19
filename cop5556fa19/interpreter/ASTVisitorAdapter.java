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
	
	public abstract List<LuaValue> load(Reader r) throws Exception;

	@Override
	public Object visitExpNil(ExpNil expNil, Object arg) {
		LuaValue val = LuaNil.nil;
		return val;
	}

	@Override
	public Object visitExpBin(ExpBinary expBin, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitUnExp(ExpUnary unExp, Object arg) throws Exception {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpList(ExpList expList, Object arg) throws Exception {
		List<LuaValue> vals = new ArrayList<LuaValue>();
		List<Exp> es = expList.list;
		for (Exp e : es) {
			vals.add((LuaValue) e.visit(this, arg));
		}
		return vals;
	}

	@Override
	public Object visitParList(ParList parList, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFunDef(ExpFunction funcDec, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitName(Name name, Object arg) {
		LuaValue val = ((LuaTable) arg).get(new LuaString(name.name));
		return val;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		List<Stat> stats = block.stats;
		if (stats.isEmpty()) {
			return null;
		} else {
			for (Stat s : stats) {
				if (s instanceof RetStat) {
					return (List<LuaValue>) ((RetStat) s).visit(this, arg);
				} else if (s instanceof StatAssign) {
//					System.out.println(s.toString());
					((StatAssign) s).visit(this, arg);
				} else if (s instanceof StatIf) {
					((StatIf) s).visit(this, arg);
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatGoto(StatGoto statGoto, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatDo(StatDo statDo, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatWhile(StatWhile statWhile, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatRepeat(StatRepeat statRepeat, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatIf(StatIf statIf, Object arg) throws Exception {
		List<Exp> es = statIf.es;
		List<Block> bs = statIf.bs;
//		System.out.println(es.toString());
//		System.out.println(bs.toString());
//		LuaBoolean bool = new LuaBoolean(false);
		Boolean executed = false;
		for (int i = 0; i < es.size(); i++) {
			LuaValue val = (LuaValue) es.get(i).visit(this, arg);
//			bool = (LuaBoolean) es.get(i).visit(this, arg);
//			System.out.println(bool.value);
			if ((val instanceof LuaBoolean && ((LuaBoolean) val).value) || (val instanceof LuaInt && ((LuaInt) val).v == 0)) {
//				System.out.println(bs.get(i).toString());
				executed = true;
				return (List<LuaValue>) bs.get(i).visit(this, arg);
			} else if ((val instanceof LuaBoolean && !((LuaBoolean) val).value) || (val instanceof LuaNil)) {
				continue;
			} else {
				throw new StaticSemanticException(statIf.firstToken, "Invalid logic value in if statement.");
			}
		}
		if (bs.size() > es.size() && !executed) {
			return (List<LuaValue>) bs.get(bs.size()-1).visit(this,arg);
		} else {
			return null;
		}
	}

	@Override
	public Object visitStatFor(StatFor statFor1, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatForEach(StatForEach statForEach, Object arg) throws Exception {
		throw new UnsupportedOperationException();
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
//        System.out.println(expList.toString());
		List<LuaValue> vals = (List<LuaValue>) expList.visit(this,arg);
//		System.out.println(vals.toString());
		return vals;
	}

	@Override
	public Object visitChunk(Chunk chunk, Object arg) throws Exception {
		Block b = chunk.block;
//		System.out.println(chunk.toString());
//		List<LuaValue> vals = (List<LuaValue>) b.visit(this, arg);
		return (List<LuaValue>) b.visit(this, arg);
//		System.out.println(vals.get(1).toString());
//		return vals;
	}

	@Override
	public Object visitFieldExpKey(FieldExpKey fieldExpKey, Object object) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFieldNameKey(FieldNameKey fieldNameKey, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object visitFieldImplicitKey(FieldImplicitKey fieldImplicitKey, Object arg) throws Exception {
		throw new UnsupportedOperationException();
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
		for (int i = 0; i < varList.size(); i++) {
			if (varList.get(i) instanceof ExpName) {
				Exp var = new ExpString(varList.get(i).firstToken);
				key = (LuaValue) var.visit(this, arg);
			} else {
				key = (LuaValue) varList.get(i).visit(this, arg);
			}
			val = (LuaValue) expList.get(i).visit(this, arg);
			((LuaTable) arg).put(key, val);
		}
//		System.out.println(((LuaTable) arg).toString());
		return null;
	}

	@Override
	public Object visitExpTableLookup(ExpTableLookup expTableLookup, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpFunctionCall(ExpFunctionCall expFunctionCall, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLabel(StatLabel statLabel, Object ar) {
		throw new UnsupportedOperationException();
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

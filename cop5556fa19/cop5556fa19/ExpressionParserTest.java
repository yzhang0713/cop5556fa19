/* *
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

import static cop5556fa19.Token.Kind.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.Expression;
import java.io.Reader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

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
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.ParList;
import cop5556fa19.ExpressionParser.SyntaxException;

class ExpressionParserTest {

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	
	// creates a scanner, parser, and parses the input.  
	Exp parseAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		ExpressionParser parser = new ExpressionParser(scanner);  // Create a parser
		Exp e = parser.exp(); // Parse and expression
		show("e=" + e);  //Show the resulting AST
		return e;
	}
	


	@Test
	void testIdent0() throws Exception {
		String input = "x";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testIdent1() throws Exception {
		String input = "(x)";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testString() throws Exception {
		String input = "\"string\"";
		Exp e = parseAndShow(input);
		assertEquals(ExpString.class, e.getClass());
		assertEquals("string", ((ExpString) e).v);
	}
	
	@Test
	void testString1() throws Exception {
		String input = "(\"string\")";
		Exp e = parseAndShow(input);
		assertEquals(ExpString.class, e.getClass());
		assertEquals("string", ((ExpString) e).v);
	}

	@Test
	void testInt() throws Exception {
		String input = "102";
		Exp e = parseAndShow(input);
		assertEquals(ExpInt.class, e.getClass());
		assertEquals(102, ((ExpInt) e).v);
	}
	
	@Test
	void testInt1() throws Exception {
		String input = "(201)";
		Exp e = parseAndShow(input);
		assertEquals(ExpInt.class, e.getClass());
		assertEquals(201, ((ExpInt) e).v);
	}
	
	@Test
	void testBoolean0() throws Exception {
		String input = "true";
		Exp e = parseAndShow(input);
		assertEquals(ExpTrue.class, e.getClass());
	}

	@Test
	void testBoolean1() throws Exception {
		String input = "false";
		Exp e = parseAndShow(input);
		assertEquals(ExpFalse.class, e.getClass());
	}

	@Test
	void testNil() throws Exception {
		String input = "nil";
		Exp e = parseAndShow(input);
		assertEquals(ExpNil.class, e.getClass());
	}
	
	@Test
	void testBinary0() throws Exception {
		String input = "1 + 2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(1,OP_PLUS,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary0() throws Exception {
		String input = "-2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, 2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary2() throws Exception {
		String input = "not#~-2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeExpUnary(
				KW_not
				, Expressions.makeExpUnary(
						OP_HASH
						, Expressions.makeExpUnary(
								BIT_XOR
								, Expressions.makeExpUnary(OP_MINUS, 2))));
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary1() throws Exception {
		String input = "-*2\n";
		assertThrows(SyntaxException.class, () -> {
		Exp e = parseAndShow(input);
		});	
	}
	
	@Test
	void testRightAssoc() throws Exception {
		String input = "\"concat\" .. \"is\"..\"right associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeExpString("\"concat\"")
				, DOTDOT
				, Expressions.makeBinary("\"is\"",DOTDOT,"\"right associative\""));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testRightAssoc1() throws Exception {
		String input = "3 ^ 5 ^ 7";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(3)
				, OP_POW
				, Expressions.makeBinary(5, OP_POW, 7));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssoc() throws Exception {
		String input = "\"minus\" - \"is\" - \"left associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeExpString("\"minus\"")
				, OP_MINUS
				, Expressions.makeExpString("\"is\"")), OP_MINUS, 
				Expressions.makeExpString("\"left associative\""));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testLeftAssocOr() throws Exception {
		String input = "1 or 2 or 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(1, KW_or, 2)
				, KW_or
				, Expressions.makeInt(3));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocAnd() throws Exception {
		String input = "1 and 2 and 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(1, KW_and, 2)
				, KW_and
				, Expressions.makeInt(3));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocBitor() throws Exception {
		String input = "1 | 2 | 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(1, BIT_OR, 2)
				, BIT_OR
				, Expressions.makeInt(3));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocBitxor() throws Exception {
		String input = "1 ~ 2 ~ 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(1, BIT_XOR, 2)
				, BIT_XOR
				, Expressions.makeInt(3));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocBitamp() throws Exception {
		String input = "1 & 2 & 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(1, BIT_AMP, 2)
				, BIT_AMP
				, Expressions.makeInt(3));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocBitshi() throws Exception {
		String input = "1 >> 2 << 3 >> 4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeBinary(1, BIT_SHIFTR, 2)
						, BIT_SHIFTL
						, Expressions.makeInt(3))
				, BIT_SHIFTR
				, Expressions.makeInt(4));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocAdd() throws Exception {
		String input = "1 + 2 - 3 + 4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeBinary(1, OP_PLUS, 2)
						, OP_MINUS
						, Expressions.makeInt(3))
				, OP_PLUS
				, Expressions.makeInt(4));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocMul() throws Exception {
		String input = "1 * 2 / 3 // 4 % 5 * 6";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeBinary(
								Expressions.makeBinary(
										Expressions.makeBinary(1, OP_TIMES, 2)
										, OP_DIV
										, Expressions.makeInt(3))
								, OP_DIVDIV
								, Expressions.makeInt(4))
						, OP_MOD
						, Expressions.makeInt(5))
				, OP_TIMES
				, Expressions.makeInt(6));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssocCom() throws Exception {
		String input = "1 < 2 > 3 <= 4 >= 5 ~= 6 == 7 < 8";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeBinary(
						Expressions.makeBinary(
								Expressions.makeBinary(
										Expressions.makeBinary(
												Expressions.makeBinary(
														Expressions.makeBinary(1, REL_LT, 2)
														, REL_GT
														, Expressions.makeInt(3))
												, REL_LE
												, Expressions.makeInt(4))
										, REL_GE
										, Expressions.makeInt(5))
								, REL_NOTEQ
								, Expressions.makeInt(6))
						, REL_EQEQ
						, Expressions.makeInt(7))
				, REL_LT
				, Expressions.makeInt(8));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
		
	@Test
	void testPrecedence() throws Exception {
		String input = "1 + 2 * 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(
				Expressions.makeInt(1)
				, OP_PLUS
				, Expressions.makeBinary(2, OP_TIMES, 3));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testVarArg() throws Exception {
		String input = "...";
		Exp e = parseAndShow(input);
		assertEquals(ExpVarArgs.class, e.getClass());
	}

}

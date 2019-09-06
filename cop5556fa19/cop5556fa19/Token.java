/**
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites or repositories,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */

package cop5556fa19;

public class Token {
	public enum Kind {
		NAME, // DONE
		INTLIT, // DONE
		KW_and, // DONE
		KW_break, // DONE
		KW_do, // DONE
		KW_else, // DONE
		KW_elseif, // DONE
		KW_end, // DONE
		KW_false, // DONE
		KW_for, // DONE
		KW_function, // DONE
		KW_goto, // DONE
		KW_if, // DONE
		KW_in, // DONE
		KW_local, // DONE
		KW_nil, // DONE
		KW_not, // DONE
		KW_or, // DONE
		KW_repeat, // DONE
		KW_return, // DONE
		KW_then, // DONE
		KW_true, // DONE
		KW_until, // DONE
		KW_while, // DONE
		OP_PLUS, // + DONE
		OP_MINUS, // - DONE
		OP_TIMES, // * DONE
		OP_DIV, // / DONE
		OP_MOD, // % DONE
		OP_POW, // ^ DONE
		OP_HASH, // # DONE
		BIT_AMP, // & DONE
		BIT_XOR, // ~ DONE
		BIT_OR,  //  | DONE
		BIT_SHIFTL, // << DONE
		BIT_SHIFTR, //  >> DONE
		OP_DIVDIV, // // DONE
		REL_EQEQ,  // == DONE
		REL_NOTEQ, // ~= DONE
		REL_LE, // <= DONE
		REL_GE, // >= DONE
		REL_LT, // < DONE
		REL_GT, // > DONE
		ASSIGN, // = DONE
		LPAREN, // DONE
		RPAREN, // DONE
		LCURLY, // DONE
		RCURLY, // DONE
		LSQUARE, // DONE
		RSQUARE, // DONE
		COLONCOLON, // :: DONE
		SEMI, // DONE
		COLON, // DONE
		COMMA, // DONE
		DOT,   // . DONE
		DOTDOT,  // .. DONE
		DOTDOTDOT, // ... DONE
		STRINGLIT, // DONE
		EOF; // DONE
	}
	
	public final Kind kind;
	public final String text;
	public final int pos;
	public final int line;
	

	public Token(Kind kind, String text, int pos, int line) {
		super();
		this.kind = kind;
		this.text = text;
		this.pos = pos;
		this.line = line;
	}

	@Override
	public String toString() {
		return "Token [kind=" + kind + ", text=" + text + ", pos=" + (pos+1) + ", line=" + (line+1) + "]";
	}

	/*precondition:  kind is NUMLIT */
	public int getIntVal() {
		return Integer.parseInt(text); 
	}
	
	/*precondition:  kind is STRINGLIT */
	public String getStringVal() {
		return text.substring(1,text.length()-1);  //remove delimiters
	}
	
	/*precondition:   kind is NAME */
	public String getName() {
		return text;
	}
}

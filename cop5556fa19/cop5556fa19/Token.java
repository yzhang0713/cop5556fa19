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
		NAME,
		INTLIT,
		KW_and,
		KW_break,
		KW_do,
		KW_else,
		KW_elseif,
		KW_end,
		KW_false,
		KW_for,
		KW_function,
		KW_goto,
		KW_if,
		KW_in,
		KW_local,
		KW_nil,
		KW_not,
		KW_or,
		KW_repeat,
		KW_return,
		KW_then,
		KW_true,
		KW_until,
		KW_while,
		OP_PLUS, // + DONE
		OP_MINUS, // - DONE need to consider comments
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
		STRINGLIT, 
		EOF;
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

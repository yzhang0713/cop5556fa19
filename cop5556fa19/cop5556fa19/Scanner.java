

/* *
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


import static cop5556fa19.Token.Kind.*;


import java.io.IOException;
import java.io.Reader;

public class Scanner {
	
	private enum State{
		START,
		HAVE_EQ, // =
		HAVE_LS, // <
		HAVE_GR, // >
		HAVE_TL, // ~
		HAVE_DV, // /
		HAVE_CL, // :
		HAVE_DT, // .
		HAVE_CR, // \r
		IN_NAME,
		IN_INTLIT,
		IN_STRLIT;
	}
	
	Reader r;


	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {	
		public LexicalException(String arg0) {
			super(arg0);
		}
	}
	
	public Scanner(Reader r) throws IOException {
		this.r = r;
	}

	// Initialize current position and line number
	int currPos = -1;
	int currLine = 0;
	
	// Integer to hold current character
	int ch;
	
	void getChar() throws IOException{
//		System.out.println(ch);
//		System.out.println(currPos);
//		System.out.println(currLine);
		// Based on the value of ch, update currPos and currLine
		if (ch == '\n' || ch == '\r') {
			currLine++;
			currPos = -1;
		}
		// Read next character, update currPos
		ch = r.read();
		if (ch == '\n') { ch = r.read(); } // CR LF case
		currPos++;
//		System.out.println(ch);
//		System.out.println(currPos);
//		System.out.println(currLine);
	}
	
	public Token getNext() throws Exception {
			Token t = null; // Initialize Token
			StringBuilder sb; // Initialize StringBuilder for the text of Token
			int pos = -1;
			int line = -1; // Initialize position and line number
			State state = State.START; // Initialize state
			
//			//replace this code.  Just for illustration
//		    if (r.read() == -1) { return new Token(EOF,"eof",0,0);}
//			throw new LexicalException("Useful error message");
			
			while (t == null) {
				switch (state) {
					case START: {
						// Skip white space
						getChar();
						while (Character.isWhitespace(ch)) { getChar(); }
						pos = currPos;
						line = currLine;
						switch (ch) {
							case -1: {t = new Token(EOF,"eof",pos,line);} break;
							case '+': {t = new Token(OP_PLUS,"+",pos,line);} break;
							case '-': {t = new Token(OP_MINUS,"-",pos,line);} break;
							case '*': {t = new Token(OP_TIMES,"*",pos,line);} break;
							case '%': {t = new Token(OP_MOD,"%",pos,line);} break;
							case '^': {t = new Token(OP_POW,"^",pos,line);} break;
							case '#': {t = new Token(OP_HASH,"#",pos,line);} break;
							case '&': {t = new Token(BIT_AMP,"&",pos,line);} break;
							case '~': {t = new Token(BIT_XOR,"~",pos,line);} break;
							case '|': {t = new Token(BIT_OR,"|",pos,line);} break;
							default : break;
						}
					} break;
					default : break;
				}
			}
			return t;
			
		    
		}

}

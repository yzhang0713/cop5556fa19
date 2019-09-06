

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
		HAVE_DDT, // ..
		HAVE_CR, // \r
		HAVE_SQT, // '
		HAVE_DQT, // "
		IN_NAME,
		IN_INTLIT,
		IN_STRLIT,
		IN_COMMNT;
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
			StringBuilder sb = null; // Initialize StringBuilder for the text of Token
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
						while (Character.isWhitespace(ch)) { getChar(); }
						pos = currPos;
						line = currLine;
						switch (ch) {
							case 0: {getChar();} break;
							case -1: {t = new Token(EOF,"eof",pos,line);} break;
							case '+': {t = new Token(OP_PLUS,"+",pos,line); getChar();} break;
							case '*': {t = new Token(OP_TIMES,"*",pos,line); getChar();} break;
							case '%': {t = new Token(OP_MOD,"%",pos,line); getChar();} break;
							case '^': {t = new Token(OP_POW,"^",pos,line); getChar();} break;
							case '#': {t = new Token(OP_HASH,"#",pos,line); getChar();} break;
							case '&': {t = new Token(BIT_AMP,"&",pos,line); getChar();} break;
							case '|': {t = new Token(BIT_OR,"|",pos,line); getChar();} break;
							case '(': {t = new Token(LPAREN,"(",pos,line); getChar();} break;
							case ')': {t = new Token(RPAREN,")",pos,line); getChar();} break;
							case '{': {t = new Token(LCURLY,"{",pos,line); getChar();} break;
							case '}': {t = new Token(RCURLY,"}",pos,line); getChar();} break;
							case '[': {t = new Token(LSQUARE,"[",pos,line); getChar();} break;
							case ']': {t = new Token(RSQUARE,"]",pos,line); getChar();} break;
							case ',': {t = new Token(COMMA,",",pos,line); getChar();} break;
							case ';': {t = new Token(SEMI,";",pos,line); getChar();} break;
							case '~': {state = State.HAVE_TL; getChar();} break;
							case '=': {state = State.HAVE_EQ; getChar();} break;
							case '/': {state = State.HAVE_DV; getChar();} break;
							case '<': {state = State.HAVE_LS; getChar();} break;
							case '>': {state = State.HAVE_GR; getChar();} break;
							case ':': {state = State.HAVE_CL; getChar();} break;
							case '.': {state = State.HAVE_DT; getChar();} break;
							default : {
								if (Character.isDigit(ch)) {
									switch (ch) {
										case '0': {t = new Token(INTLIT,"0",pos,line); getChar();} break;
										default : {
											state = State.IN_INTLIT;
											sb = new StringBuilder();
											sb.append((char)ch);
											getChar();
										} break;
									}
								} else if (Character.isJavaIdentifierStart(ch)) {
									state = State.IN_NAME;
									sb = new StringBuilder();
									sb.append((char)ch);
									getChar();
								} else if (ch == '-') {
									switch (ch) {
										case '-': {state = State.IN_COMMNT; getChar();} break;
										default : {t = new Token(OP_MINUS,"-",pos,line);} break;
									}
								} else if (ch == 34) {
									state = State.HAVE_DQT;
									sb = new StringBuilder();
									getChar();
								} else if (ch == 39) {
									state = State.HAVE_SQT;
									sb = new StringBuilder();
									getChar();
								} else {
									throw new LexicalException("Invilid input at "); // need more info
								}
							} break;
						}
					} break;
					case HAVE_TL: {
						switch (ch) {
							case '=': {t = new Token(REL_NOTEQ,"~=",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(BIT_XOR,"~",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_EQ: {
						switch (ch) {
							case '=': {t = new Token(REL_EQEQ,"==",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(ASSIGN,"=",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_DV: {
						switch (ch) {
							case '/': {t = new Token(OP_DIVDIV,"//",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(OP_DIV,"/",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_LS: {
						switch (ch) {
							case '<': {t = new Token(BIT_SHIFTL,"<<",pos,line); state = State.START; getChar();} break;
							case '=': {t = new Token(REL_LE,"<=",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(REL_LT,"<",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_GR: {
						switch (ch) {
							case '>': {t = new Token(BIT_SHIFTR,">>",pos,line); state = State.START; getChar();} break;
							case '=': {t = new Token(REL_GE,">=",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(REL_GT,">",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_CL: {
						switch (ch) {
							case ':': {t = new Token(COLONCOLON,"::",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(COLON,":",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_DT: {
						switch (ch) {
							case '.': {state = State.HAVE_DDT; getChar();} break;
							default : {t = new Token(DOT,".",pos,line); state = State.START;} break;
						}
					} break;
					case HAVE_DDT: {
						switch (ch) {
							case '.': {t = new Token(DOTDOTDOT,"...",pos,line); state = State.START; getChar();} break;
							default : {t = new Token(DOTDOT,"..",pos,line); state = State.START;} break;
						}
					} break;
					case IN_INTLIT: {
						if (Character.isDigit(ch)) {
							sb.append((char)ch);
							getChar();
						} else {t = new Token(INTLIT,sb.toString(),pos,line); state = State.START;}							
					} break;
					case IN_NAME: {
						if (Character.isJavaIdentifierPart(ch)) {
							sb.append((char)ch);
							getChar();
						} else {
							String str;
							str = sb.toString();							
							switch (str) {
								case "and": {t = new Token(KW_and,str,pos,line); state = State.START;} break;
								case "break": {t = new Token(KW_break,str,pos,line); state = State.START;} break;
								case "do": {t = new Token(KW_do,str,pos,line); state = State.START;} break;
								case "else": {t = new Token(KW_else,str,pos,line); state = State.START;} break;
								case "elseif": {t = new Token(KW_elseif,str,pos,line); state = State.START;} break;
								case "end": {t = new Token(KW_end,str,pos,line); state = State.START;} break;
								case "false": {t = new Token(KW_false,str,pos,line); state = State.START;} break;
								case "for": {t = new Token(KW_for,str,pos,line); state = State.START;} break;
								case "function": {t = new Token(KW_function,str,pos,line); state = State.START;} break;
								case "goto": {t = new Token(KW_goto,str,pos,line); state = State.START;} break;
								case "if": {t = new Token(KW_if,str,pos,line); state = State.START;} break;
								case "in": {t = new Token(KW_in,str,pos,line); state = State.START;} break;
								case "local": {t = new Token(KW_local,str,pos,line); state = State.START;} break;
								case "nil": {t = new Token(KW_nil,str,pos,line); state = State.START;} break;
								case "not": {t = new Token(KW_not,str,pos,line); state = State.START;} break;
								case "or": {t = new Token(KW_or,str,pos,line); state = State.START;} break;
								case "repeat": {t = new Token(KW_repeat,str,pos,line); state = State.START;} break;
								case "return": {t = new Token(KW_return,str,pos,line); state = State.START;} break;
								case "then": {t = new Token(KW_then,str,pos,line); state = State.START;} break;
								case "true": {t = new Token(KW_true,str,pos,line); state = State.START;} break;
								case "until": {t = new Token(KW_until,str,pos,line); state = State.START;} break;
								case "while": {t = new Token(KW_while,str,pos,line); state = State.START;} break;
								default : {t = new Token(NAME,str,pos,line); state = State.START;} break;
							}
						}
					} break;
					case IN_COMMNT: {if (ch == '\n' || ch == '\r') {state = State.START; getChar();} else {getChar();}} break;
					case HAVE_DQT: {
						if (ch >= 0 && ch <= 127) {
							if (ch == 34) {
								if (sb.length() == 0) {throw new LexicalException("Zero length string literal at ");}
								else {t = new Token(STRINGLIT,sb.toString(),pos,line); state = State.START; getChar();}
							} else if (ch == 39) {
								throw new LexicalException("Unbalanced qutation mark at");
							} else if (ch == 92) {
								sb.append((char)ch);
								getChar();
								if (ch=='a'||ch=='b'||ch=='f'||ch=='n'||ch=='r'||ch=='v'||ch=='t'||ch==34||ch==39||ch==92) {
									sb.append((char)ch);
									getChar();
								} else {
									throw new LexicalException("Should not contain backslash in string literal");
								}
							} else {sb.append((char)ch); getChar();}
						} else {throw new LexicalException("Invalid input at ");}
					} break;
					case HAVE_SQT: {
						if (ch >= 0 && ch <= 127) {
							if (ch == 39) {
								if (sb.length() == 0) {throw new LexicalException("Zero length string literal at ");}
								else {t = new Token(STRINGLIT,sb.toString(),pos,line); state = State.START; getChar();}
							} else if (ch == 34) {
								throw new LexicalException("Unbalanced apostrophe at");
							} else if (ch == 92) {
								sb.append((char)ch);
								getChar();
								if (ch=='a'||ch=='b'||ch=='f'||ch=='n'||ch=='r'||ch=='v'||ch=='t'||ch==34||ch==39||ch==92) {
									sb.append((char)ch);
									getChar();
								} else {
									throw new LexicalException("Should not contain backslash in string literal");
								}
							} else {sb.append((char)ch); getChar();}
						} else {throw new LexicalException("Invalid input at ");}
					} break;
					default : break;
				}
			}
			return t;
			
		    
		}

}

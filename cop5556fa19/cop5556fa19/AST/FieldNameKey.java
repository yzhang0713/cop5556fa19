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

package cop5556fa19.AST;

import cop5556fa19.Token;

public class FieldNameKey extends Field {
	
	public final Name name;
	public final Exp exp;
	
	

	public FieldNameKey(Token firstToken, Name name, Exp exp) {
		super(firstToken);
		this.name = name;
		this.exp = exp;
	}


	@Override
	public String toString() {
		return "FieldNameKey [name=" + name + ", exp=" + exp + ", firstToken=" + firstToken + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exp == null) ? 0 : exp.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldNameKey other = (FieldNameKey) obj;
		if (exp == null) {
			if (other.exp != null)
				return false;
		} else if (!exp.equals(other.exp))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitFieldNameKey(this,arg);
	}

}

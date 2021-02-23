package cop5556fa19.AST;

import cop5556fa19.Token;

public class TableDeref extends Var {

	public final Exp table;
	public final Exp key;

	public TableDeref(Token firstToken, Exp table, Exp key) {
		super(firstToken);
		this.table = table;
		this.key = key;
	}

	@Override
	public String toString() {
		return "TableDeref [table=" + table + ", key=" + key + ", firstToken=" + firstToken + "]";
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
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
		TableDeref other = (TableDeref) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitTableDeref(this, arg);
	}

}

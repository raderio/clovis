package guitar.parser;


public class Type {

	public TypeKind kind;

	public Type(TypeKind kind) {
		this.kind = kind;
	}

	public String toString() {
		switch (kind) {
		case TYPE_INT:
			return "int";
		case TYPE_DOUBLE:
			return "double";
		case TYPE_VOID:
			return "void";
		case TYPE_STRING:
			return "str";
		case TYPE_BYTE:
			return "byte";
		default:
			return "nigger";
		}
	}
}

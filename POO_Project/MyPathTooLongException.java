
public class MyPathTooLongException extends MyInvalidPathException {

	public MyPathTooLongException(Directory dir, User user) {
		super(dir,"", user);
	}
	
	public String toString() {
		return "|Path Too Long| "+"D: "+this.current.name+" Arguments: "+this.input+" User: "+this.user.username+" at ["+this.time+"]";
	}
}

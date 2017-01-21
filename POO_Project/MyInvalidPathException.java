import java.time.LocalDateTime;

public class MyInvalidPathException extends Exception {

	Directory current;
	String input;
	User user;
	LocalDateTime time;
	
	public MyInvalidPathException(Directory dir,String input,User user) {
		this.current=dir;
		this.input=input;
		this.user=user;
		this.time=LocalDateTime.now();
	}
	
	public String toString() {
		return "|Invalid Path| "+"D: "+this.current.name+" Arguments: "+this.input+" User: "+this.user.username+" at ["+this.time+"]";
	}
}

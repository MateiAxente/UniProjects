import java.time.LocalDateTime;

public class MyNotEnoughSpaceException extends Exception {

	int size;
	User user;
	LocalDateTime time;
	
	public MyNotEnoughSpaceException(Directory dir,User user) {
		this.size=dir.size;
		this.user=user;
		this.time=LocalDateTime.now();
	}
	
	public String toString() {
		return "|Not Enough Space| "+"Directory Size: "+this.size+" User: "+this.user.username+" at ["+this.time+"]";
	}
}

import java.time.LocalDateTime;

public class User {

	String username;
	String password;
	String firstname;
	String lastname;
	LocalDateTime created;
	LocalDateTime lastLogin;
	
	public User() {
		
	}
	
	public User(String username,String password) {
		this.username=username;
		this.password=password;
		this.created=LocalDateTime.now();
		this.lastLogin=this.created;
	}
	
	public User(String username,String password,String firstname,String lastname) {
		this.username=username;
		this.password=password;
		this.firstname=firstname;
		this.lastname=lastname;
		this.created=LocalDateTime.now();
		this.lastLogin=this.created;
	}
	
	public String userinfo() {
		return "UserName: "+this.username+" FirstName: "+this.firstname+" LastName: "+this.lastname+" Created: "+this.created+" LastLogin: "+this.lastLogin;
	}
}

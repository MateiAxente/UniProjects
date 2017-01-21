
public class Singleton {
	
	User user;
	
	private static final Singleton Instance = new Singleton();
	
	private Singleton() {
		
	}
	
	public static final Singleton getInstance() {
		return Instance;
	}
}

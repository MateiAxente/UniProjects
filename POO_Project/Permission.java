
public class Permission {
	boolean read;
	boolean write;
	User owner;
	
	public Permission() {
		this.read=true;
		this.write=true;
		this.owner=null;
	}
	
	public Permission(boolean r,boolean w,User owner) {
		this.read=r;
		this.write=w;
		this.owner=owner;
	}
}

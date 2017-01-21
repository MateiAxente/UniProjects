import java.time.LocalDateTime;

public class File implements Repository {
	Directory parent;
	String name;
	int size;
	String type;
	Permission perm;
	byte[] content;
	LocalDateTime created;
	
	public File(Directory parent,String name) {
		this.parent=parent;
		this.name=name;
		this.type="text";
		this.size=0;
		this.content=null;
		if(this.parent!=null) {
			this.perm = new Permission(this.parent.perm.read,this.parent.perm.write,null);
		}
		else {
			this.perm = new Permission(true,true,null);
		}
		this.created=LocalDateTime.now();
	}
	
	public File(Directory parent,String name,String size) {
		this.parent=parent;
		this.name=name;
		this.type="text";
		this.size=Integer.parseInt(size);
		this.content=null;
		this.perm=new Permission(true,true,null);
		this.created=LocalDateTime.now();
	}
	
	public File(Directory parent,String name,int size,String type,Permission perm,byte[] content) {
		this.parent=parent;
		this.name=name;
		this.size=size;
		this.type=type;
		this.content=content;
		this.perm=perm;
	}
	
	@Override
	public void accept(Command c) {
		c.execute(this);
		
	}
	
	public String toString() {
		return this.name;
	}

	public String toString(int r) {
		if(this instanceof File == false)
			return null;
		String res="\n";
		while(r>1) {
			res+="    ";
			r--;
		}
		if(r==1)
			res+="|---";
		res+=this.name;
		return res;
	}
	
	public String toList() {
		String res = new String();
		res="F: "+this.name+" "+this.size+" "+this.type+" ["+this.created+"]";
		res+=" -";
		if(this.perm.read)
			res+="r";
		if(this.perm.write)
			res+="w";
		res+="-";
		return res;
	}
	
	public String toList(int r) {
		if(this instanceof File == false)
			return null;
		String res="\n";
		while(r>1) {
			res+="    ";
			r--;
		}
		if(r==1)
			res+="|---";
		res+=this.toList();
		return res;
	}
	
	public File clone(Directory parent) {
		File f = new File(parent,this.name,this.size,this.type,this.perm,this.content);
		f.created=this.created;
		return f;
	}

}

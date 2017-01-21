import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Directory implements Repository {
	Directory parent;
	String name;
	int size;
	Permission perm;
	List<Repository> children;
	LocalDateTime created;
	
	public Directory(Directory parent,String name) {
		this.parent=parent;
		this.name=name;
		this.size=0;
		this.children=new ArrayList<Repository>();
		if(this.parent!=null) {
			this.perm = new Permission(this.parent.perm.read,this.parent.perm.write,null);
		}
		else {
			this.perm = new Permission(true,true,null);
		}
		this.created=LocalDateTime.now();
	}
	
	public Directory(Directory parent,String name,Permission perm) {
		this(parent,name);
		this.perm=perm;
		this.size=0;
	}
	
	@Override
	public void accept(Command c) throws MyInvalidPathException, MyNotEnoughSpaceException {
		c.execute(this);
	}
	
	public void addChild(Repository child) {
		this.children.add(child);
		Directory dir=this;
		while(dir.parent!=null) {
			dir.size=dir.calculateSize();
			dir=dir.parent;
		}
	}

	public void removeChild(Repository child) {
		this.children.remove(child);
		Directory dir=this;
		while(dir.parent!=null) {
			dir.size=dir.calculateSize();
			dir=dir.parent;
		}
	}
	
	public String toString() {
		return this.name;
	}

	public String toList() {
		String res = new String();
		res="D: "+this.name+" ["+this.created+"] "+this.size;
		res+=" -";
		if(this.perm.read)
			res+="r";
		if(this.perm.write)
			res+="w";
		res+="- ";
		return res;
	}
	
	public String toString(int r) {
		if(this instanceof Directory == false)
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
	
	public String toList(int r) {
		if(this instanceof Directory == false)
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
		
	public void toLiniar(Repository rep,Directory parent,Vector<Repository> v) {
		if(rep instanceof File) {
			v.add(((File)rep).clone(parent));
		}
		if(rep instanceof Directory) {
			Directory d = ((Directory)rep).clone(parent);
			v.add(d);
			if(((Directory)rep).children!=null)
				for(int i=0;i<((Directory)rep).children.size();i++)
					this.toLiniar(((Directory)rep).children.get(i),d,v);
		}
	}
	
	public Directory clone(Directory parent) {
		Directory d = new Directory(parent,this.name,this.perm);
		d.created=this.created;
		return d;
	}
	/*
	public Directory cloneAll(Directory parent) {
		Directory d = new Directory(parent,this.name,this.perm);
		d.created=this.created;
		for(int i=0;i<this.children.size();i++)
			if(this.children.get(i) instanceof File)
				d.addChild(((File)this.children.get(i)).clone(d));
			else
				d.addChild(((Directory)this.children.get(i)).cloneAll(d));
		return d;
	}
	*/
	
	public Directory cloneAll(CloudService s,Directory parent) {
		Directory d = new Directory(parent,this.name,this.perm);
		for(int i=0;i<this.children.size();i++)
			if(s.searchAll(this.children.get(i).toString())==null) {
				if(this.children.get(i) instanceof File)
					d.addChild(((File)this.children.get(i)).clone(d));
				else
					d.addChild(((Directory)this.children.get(i)).cloneAll(s,d));
			}
			else
				d.addChild(s.searchAll(this.children.get(i).toString()));
		return d;
	}
	
	public Repository search(String name) {
		Repository rep=null;
		
		if(this.toString().equals(name))
			return this;
		
		for(int i=0;i<this.children.size();i++)
			if(this.children.get(i) instanceof File) {
				if(this.children.get(i).toString().equals(name))
					rep=this.children.get(i);
			}
			else
				if(this.children.get(i) instanceof Directory) {
					if(rep==null)
						rep=((Directory)this.children.get(i)).search(name);
				}
				else
					if(((MachineId)this.children.get(i)).child.equals(name))
						rep=this.children.get(i);
		return rep;
	}
	
	public Directory rebuild(CloudService s,Directory dir) {
		Directory d = dir.clone(dir.parent);
		//if(dir.children)
		for(int i=0;i<dir.children.size();i++)
			if(dir.children.get(i) instanceof File)
				d.addChild(((File)dir.children.get(i)).clone(d));
			else
				if(dir.children.get(i) instanceof Directory)
					d.addChild(dir.rebuild(s,(Directory)dir.children.get(i)));
				else {
					MachineId m=(MachineId)dir.children.get(i);
					Repository rep=s.station[m.id].search(m.child);
					if(rep instanceof File)
						d.addChild(((File)rep).clone(d));
					else
						d.addChild(this.rebuild(s,(Directory)rep));
				}
		return d;
	}
	
	public int calculateSize() {
		int s=0;
		for(int i=0;i<this.children.size();i++)
			if(this.children.get(i) instanceof File)
				s+=((File)this.children.get(i)).size;
			else {
				((Directory)this.children.get(i)).size=((Directory)this.children.get(i)).calculateSize();
				s+=((Directory)this.children.get(i)).size;
			}
		return s;
	}
	
	public Vector<Repository> move(String name) {
		Vector<Repository> v = new Vector<Repository>();
		for(int i=0;i<this.children.size();i++)
			if(this.children.get(i).toString().matches(name))
				v.add(this.children.get(i));
		return v;
	}
	
}

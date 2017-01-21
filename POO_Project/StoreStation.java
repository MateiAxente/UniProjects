import java.util.HashSet;
import java.util.Iterator;

public abstract class StoreStation {

	MachineId id;
	int capacity;
	HashSet<Repository> data;
	
	public void store(Repository rep) {
		data.add(rep);
	}
	
	public Repository search(String name) {
		
		Iterator<Repository> i=data.iterator();
		Repository rep=null,res=null;
		while(i.hasNext()) {
			rep = i.next();
			if(rep.toString().equals(name))
				res=rep;
			if(rep instanceof Directory) {
				if(res==null)
					res=((Directory)rep).search(name);
			}
		}
		return res;
	}
	
	public int freeSpace() {
		Repository rep;
		int space = this.capacity;
		if(this.data!=null) {
		Iterator<Repository> i=data.iterator();
		while(i.hasNext()) {
			rep=i.next();
			if(rep instanceof Directory)
				space-=((Directory)rep).size;
			else
				if(rep instanceof File)
					space-=((File)rep).size;
		}
		}
		return space;
	}
}

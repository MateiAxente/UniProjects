
public class MachineId implements Repository {

	int id;
	String child;
	
	public MachineId(int id) {
		this.id=id;
		this.child=new String();
	}
	
	public MachineId(int id,String child) {
		this.id=id;
		this.child=child;
	}
	
	@Override
	public void accept(Command c) {
		// TODO Auto-generated method stub
		
	}

	public String toString() {
		return this.id+" - "+this.child;
	}
	
	public String toString(int r) {
		String res="\n";
		while(r>1) {
			res+="    ";
			r--;
		}
		if(r==1)
			res+="|---";
		res+=this.id+" - "+this.child;
		return res;
	}

	@Override
	public String toList() {
		return null;
	}

	@Override
	public String toList(int r) {
		return null;
	}
}

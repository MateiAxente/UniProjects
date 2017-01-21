import java.util.HashSet;

public class Station extends StoreStation {
	String name;
	
	public Station(String name,int id) {
		this.id=new MachineId(id);
		this.name=name;
		this.capacity=10*1024;
		this.data = new HashSet<Repository>();
	}
}

import java.util.Vector;

public class CloudService {

	User user;
	Station station[]={new Station("statie_1",0),new Station("statie_2",1),new Station("statie_3",2)};
	
	public CloudService(User user) {
		this.user=user;
	}
	
	public void upload(Directory dir) throws MyNotEnoughSpaceException {
		if(dir.size<=this.allFreeSpace()) {
			int i=0;
			while(station[i].freeSpace()==0)
				i++;
			//System.out.println("FREEE!!! : "+station[i].freeSpace());
			if(station[i].freeSpace()>dir.size) {
				station[i].store(dir.cloneAll(this,null));
			}
			else {
				
				//System.out.println("THIS WAY");
				
				Vector<Repository> v = new Vector<Repository>();
				//v=dir.toLiniar(dir,v);
				dir.toLiniar(dir,null,v);

				//System.out.println(v.size());
						
				int j=0,space=station[i].freeSpace();
				int nr=i;
				while(space!=0) {
					if(v.get(j) instanceof Directory) {
						if(this.searchAll(v.get(j).toString())==null) {
							if(((Directory)v.get(j)).parent != null)
								((Directory)v.get(j)).parent.addChild(v.get(j));
						}
						else
							((Directory)v.get(j)).parent.addChild(this.searchAll(v.get(j).toString()));
					}
					else {
						if(v.get(j) instanceof File)
							if(space > ((File)v.get(j)).size) {
								if(this.searchAll(v.get(j).toString())==null) {
									((File)v.get(j)).parent.addChild(v.get(j));
									space-=((File)v.get(j)).size;
									//System.out.println("NAME: "+((File)v.get(j)).name);
								}
								else
									((File)v.get(j)).parent.addChild(this.searchAll(v.get(j).toString()));
							}
							else {
								space=0;
								j--;
								//System.out.println("Empty at "+j);
							}
					}
					j++;
				}
				
				this.station[i].store(v.get(0));

				nr++;
				
//				/System.out.println("J = "+j+" size = "+v.size());
				
				space=this.station[nr].freeSpace();
				
				while(j<v.size()) {
					if(v.get(j) instanceof File) {
						if(space<((File)v.get(j)).size) {
							nr++;
							space=this.station[nr].freeSpace();
						}
						else
							space-=((File)v.get(j)).size;
							
						if(this.searchAll(((File)v.get(j)).parent.toString())!=null) {
							//System.out.println(((File)v.get(j)).parent.toString()+"----"+this.searchAll(((File)v.get(j)).parent.toString()).id+" != "+nr);
							if(this.searchAll(((File)v.get(j)).parent.toString()).id==nr) {
								
								((File)v.get(j)).parent.addChild(v.get(j));
							}
							else {
							//System.out.println("been here at "+v.get(j).toString()+" with parent "+((File)v.get(j)).parent.toList());
							
								((File)v.get(j)).parent.addChild(new MachineId(nr,v.get(j).toString()));
								this.station[nr].store(v.get(j));
								//System.out.println("Here lies: "+v.get(j));
							}
						}
					}
					else
						if(v.get(j) instanceof Directory) {
							if(this.searchAll(((Directory)v.get(j)).parent.toString())!=null) {
								if(this.searchAll(((Directory)v.get(j)).parent.toString()).id==nr) {
									((Directory)v.get(j)).parent.addChild(v.get(j));
									//System.out.println("been here at d "+v.get(j).toString());
								}
								else {
									//System.out.println("been here at d "+v.get(j).toString());
									((Directory)v.get(j)).parent.addChild(new MachineId(nr,v.get(j).toString()));
									this.station[nr].store(v.get(j));
									//System.out.println("Here lies: "+v.get(j));
								}
							}
						}
						j++;
					}			
				}
		}
		else
			throw new MyNotEnoughSpaceException(dir,user);
	}
	
	public void sync(Directory dir) {
		MachineId m=this.searchAll(dir.name);
		Directory d=(Directory)this.station[m.id].search(m.child);
		d=d.rebuild(this,d);
		d.size=d.calculateSize();
		dir.size=d.size;
		dir.children=d.children;
	}
	
	public int allFreeSpace() {
		return this.station[0].freeSpace()+this.station[1].freeSpace()+this.station[2].freeSpace();
	}
	
	public MachineId searchAll(String name) {
		MachineId m=null;
		for(int i=0;i<3;i++)
			if(this.station[i].search(name)!=null)
				m = new MachineId(i,name);
		return m;
	}
	
}

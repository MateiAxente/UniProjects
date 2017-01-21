import java.util.StringTokenizer;

public class Cd extends ReadCommand {
	
	TermW window;
	User user;
	String path;
	Directory current;
	
	public Cd(TermW window,Directory current) {
		this.path=null;
		this.current=current;
		this.window=window;
	}
	
	public Cd(TermW window,User user,String path,Directory current) {
		this.user=user;
		StringTokenizer st = new StringTokenizer(path);
		this.path=st.nextToken();
		this.current=current;
		this.window=window;
	}
	
	@Override
	public void execute(Repository rep) throws MyInvalidPathException {
		this.execute((Directory)rep);
		
	}

	@Override
	public void execute(Directory dir) throws MyInvalidPathException {
		if(this.path==null)
			while(dir.parent!=null)
				dir=dir.parent;
		else {
			StringTokenizer t=new StringTokenizer(this.path,"/");
			while(t.hasMoreTokens()) {
				String aux=new String();
				aux=t.nextToken();
				if(aux.compareTo("..")==0)
					if(dir.parent!=null)
						dir=dir.parent;
					else
						throw new MyInvalidPathException(this.current,this.path,this.user);
				else {
					int i=0;
					while(i<dir.children.size())
						if(dir.children.get(i).toString().equals(aux)==false)
							i++;
						else
							break;
					if(i<dir.children.size()) {
						if(this.readPermission(this.user,dir.children.get(i))) {
							if(dir.children.get(i) instanceof Directory)
								dir=(Directory)dir.children.get(i);
							else
								this.window.output(dir.children.get(i).toString()+" is not a directory");
						}
						else
							this.window.output("Permission denied");
					}
					else
						throw new MyInvalidPathException(this.current,this.path,this.user);
				}
			}
		}
		this.current=dir;
	}

	@Override
	public void execute(File file) {
		
	}

}

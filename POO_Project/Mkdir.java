import java.util.StringTokenizer;

public class Mkdir extends WriteCommand {
	
	TermW window;
	User user;
	String input;
	
	public Mkdir(TermW window,String name) {
		this.window=window;
		this.user=null;
		this.input=name;
	}
	
	public Mkdir(TermW window,User user,String name) {
		this.window=window;
		this.user=user;
		this.input=name;
	}
	
	@Override
	public void execute(Repository rep) {
		this.execute((Directory)rep);
		
	}

	@Override
	public void execute(Directory dir) {
		if(this.writePermission(this.user,dir)) {
			StringTokenizer st = new StringTokenizer(this.input);
			String aux="";//,last=null;//=st.nextToken();
			Directory d=null;
			while(st.hasMoreTokens()) {
				//last=aux;
				aux=st.nextToken();
				if(d==null)
					d = new Directory(dir,aux);
				switch(aux) {
				case "--":
					d.perm.read=false;
					d.perm.write=false;
					d.perm.owner=this.user;
					break;
				case "-r-":
					d.perm.write=false;
					if(d.perm.read==false)
						this.window.output("Permission not available");
					d.perm.owner=this.user;
					break;
				case "-w-":
					d.perm.read=false;
					if(d.perm.write==false)
						this.window.output("Permission not available");
					d.perm.owner=this.user;
					break;
				case "-rw-":
					if(d.perm.read==false || d.perm.write==false)
						this.window.output("Permission not available");
					d.perm.owner=this.user;
					break;
				default:
					d = new Directory(dir,aux);
					dir.addChild(d);
					break;
				}
			}
		}
		else
			this.window.output("Permission denied");
	}

	@Override
	public void execute(File file) {
		// TODO Auto-generated method stub
		
	}

}

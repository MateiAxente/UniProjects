import java.util.StringTokenizer;

public class Touch extends WriteCommand {
	
	TermW window;
	User user;
	String input;
	
	public Touch(TermW window,String name) {
		this.window=window;
		this.user=null;
		this.input=name;
	}
	
	public Touch(TermW window,User user,String name) {
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
			File f=null;
			while(st.hasMoreTokens()) {
				//last=aux;
				aux=st.nextToken();
				if(f==null)
					f = new File(dir,aux);
				switch(aux) {
				case "--":
					f.perm.read=false;
					f.perm.write=false;
					f.perm.owner=this.user;
					break;
				case "-r-":
					f.perm.write=false;
					if(f.perm.read==false)
						this.window.output("Permission not available");
					f.perm.owner=this.user;
					break;
				case "-w-":
					f.perm.read=false;
					if(f.perm.write==false)
						this.window.output("Permission not available");
					f.perm.owner=this.user;
					break;
				case "-rw-":
					if(f.perm.read==false || f.perm.write==false)
						this.window.output("Permission not available");
					f.perm.owner=this.user;
					break;
				case "text":
					f.type=aux;
					break;
				case "binary":
					f.type=aux;
					break;
				default:
					if(Character.isDigit(aux.charAt(0))) {
						f.size=Integer.parseInt(aux);
						dir.size=dir.calculateSize();
					}
					else {
						f = new File(dir,aux);
						dir.addChild(f);
					}
					break;
				}
			}
			//dir.addChild(d);
		}
		else
			this.window.output("Permission denied");
	}

	@Override
	public void execute(File file) {
		// TODO Auto-generated method stub
		
	}

}

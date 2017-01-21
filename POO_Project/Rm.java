import java.util.StringTokenizer;

public class Rm extends WriteCommand {

	public TermW window;
	public User user;
	public String path;
	boolean rec;
	
	public Rm(TermW window,User user,String path) {
		this.window=window;
		this.user=user;
		this.rec=false;
		this.path=path;
		String aux;
		String name=null;
		//this.window.output("PATH_IN: "+path);
		if(this.path!=null) {
			StringTokenizer st = new StringTokenizer(this.path);
			aux=st.nextToken();
			while(aux.equals(""))
				aux=st.nextToken();
			//this.window.output("AUX: "+aux);
			while(st.hasMoreTokens()) {
				//this.window.output("AUX: "+aux);
				switch(aux) {
				case "-r":
					this.rec=true;
					aux=st.nextToken();
					break;
				default:
					name=aux;
					break;
				}
			}
			switch(aux) {
			case "-r":
				this.rec=true;
				//aux=st.nextToken();
				break;
			case "-POO":
				break;
			default:
				name=aux;
				break;
			}
			this.path=name;
		}
		//this.window.output(this.toString());
	}

	@Override
	public void execute(Repository rep) throws MyInvalidPathException, MyNotEnoughSpaceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Directory dir) throws MyInvalidPathException, MyNotEnoughSpaceException {
		if(this.writePermission(this.user,dir)) {
			if(this.rec) {
				int size=dir.children.size();
				Rm rm = new Rm(this.window,this.user,"-r");
				for(int i=0;i<size;i++) {
					dir.children.get(i).accept(rm);
					if(size!=dir.children.size()) {
						size=dir.children.size();
						i--;
					}
				}
				if(dir.children.size()==0)
					dir.parent.removeChild(dir);					
			}
			else
				if(dir.children.size()==0)
					dir.parent.removeChild(dir);
				else
					this.window.output("Directory is not empty");
		}
		else
			this.window.output("Permission denied for "+dir.toString());
	}

	@Override
	public void execute(File file) {
		if(this.writePermission(this.user,file))
			file.parent.removeChild(file);
		else
			this.window.output("Permission denied for "+file.toString());
	}

}

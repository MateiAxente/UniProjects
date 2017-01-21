
public class Pwd extends ReadCommand {

	TermW window;
	User user=null;
	String result;
	
	public Pwd(TermW window) {
		this.window=window;
		this.user=null;
	}
	
	public Pwd(TermW window,User user) {
		this.window=window;
		this.user=user;
	}
	
	@Override
	public void execute(Repository rep) throws MyPathTooLongException {
		if(rep instanceof Directory)
			this.execute((Directory)rep);
		else
			if(rep instanceof File)
				this.execute((File)rep);
	}

	@Override
	public void execute(Directory dir) throws MyPathTooLongException {
		if(this.readPermission(this.user,dir)) {
			Directory d=dir;
			String result=new String();
			result=dir.name+"/";
			while(d.parent!=null) {
				d=d.parent;
				result=d.name+"/"+result;
			}
			if(this.user==null) {
				this.result=result;
			}
			else
				if(result.length()<255)
					this.window.output(result);
				else
					throw new MyPathTooLongException(dir,this.user);
		}
		else
			this.window.output("Permission denied");
	}

	@Override
	public void execute(File file) {
		// TODO Auto-generated method stub
		
	}

}

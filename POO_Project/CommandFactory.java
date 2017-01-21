
public class CommandFactory {

	public TermW window;
	public User user;
	public String path;
	public Directory current;
	public CloudService cs;
	
	public CommandFactory(TermW window,CloudService cs,User user) {
		this.window=window;
		this.cs=cs;
		this.user=user;
		this.path=null;
		this.current=null;
	}
	
	public CommandFactory(TermW window,CloudService cs,String path,Directory current) {
		this.window=window;
		this.cs=cs;
		this.user=null;
		this.path=path;
		this.current=current;
	}
	
	public CommandFactory(TermW window,CloudService cs,User user,String path,Directory current) {
		this.window=window;
		this.cs=cs;
		this.user=user;
		this.path=path;
		this.current=current;
	}
	
	public Command getCommand(String c) {
		if(c.equals("cd"))
			if(path!=null)
				return new Cd(window,user,path,current);
			else
				return new Cd(window,current);
		if(c.equals("ls"))
			if(this.path!=null)
				return new Ls(window,user,path,-1);
			else
				return new Ls(window,user,null,-1);
		if(c.equals("touch"))
			return new Touch(window,user,path);
		if(c.equals("mkdir"))
			return new Mkdir(window,user,path);
		if(c.equals("rm"))
			return new Rm(window,user,path);
		if(c.equals("pwd"))
			return new Pwd(window,user);
		if(c.equals("cat"))
			return new Cat(window,user,path);
		if(c.equals("upload"))
			return new Upload(window,cs,path,user);
		if(c.equals("sync"))
			return new Sync(window,cs,path);
		return null;
	}
}

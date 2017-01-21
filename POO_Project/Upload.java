import java.util.StringTokenizer;

public class Upload implements Command {

	TermW window;
	String path;
	CloudService cs;
	User user;
	
	public Upload(TermW window,CloudService cs,String path,User user) {
		this.window=window;
		this.cs=cs;
		this.user=user;
		StringTokenizer st =new StringTokenizer(path);
		String aux=st.nextToken();
		while(st.hasMoreTokens() && aux.equals(""))
			aux=st.nextToken();
		this.path=aux;
	}
	
	@Override
	public void execute(Repository rep) throws MyInvalidPathException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Directory dir) throws MyInvalidPathException, MyNotEnoughSpaceException {
		
			this.cs.upload(dir);
	}

	@Override
	public void execute(File file) {
		// TODO Auto-generated method stub
		
	}

}

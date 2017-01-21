import java.util.StringTokenizer;

public class Sync implements Command {

	TermW window;
	String path;
	CloudService cs;
	
	public Sync(TermW window,CloudService cs,String path) {
		this.window=window;
		this.cs=cs;
		StringTokenizer st =new StringTokenizer(path);
		String aux=st.nextToken();
		while(st.hasMoreTokens() && aux.equals(""))
			aux=st.nextToken();
		this.path=aux;
	}
	
	@Override
	public void execute(Repository rep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Directory dir) {
		this.cs.sync(dir);
	}

	@Override
	public void execute(File file) {
		// TODO Auto-generated method stub
		
	}

}

import java.util.StringTokenizer;

public class Cat extends ReadCommand {

	TermW window;
	User user;
	String name;
	
	public Cat(TermW window,User user,String name) {
		this.window=window;
		StringTokenizer st = new StringTokenizer(name);
		String aux=st.nextToken();
		while(aux.equals(""))
			aux=st.nextToken();
		this.name=aux;
		this.user=user;
	}
	
	@Override
	public void execute(Repository rep) {
		if(rep instanceof Directory)
			this.execute((Directory)rep);
		else
			if(rep instanceof File)
				this.execute((File)rep);
	}

	@Override
	public void execute(Directory dir) {
		this.window.output(dir.toString()+" is not a file");
	}

	@Override
	public void execute(File file) {
		this.window.output(file.toString()+" 's content:\n");
	}

}

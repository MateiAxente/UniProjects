import java.util.StringTokenizer;

public abstract class ReadCommand implements Command {

	public boolean readPermission(User user,Repository rep) {
		if(user==null)
			return true;
		if(user.username.equals("root"))
			return true;
		if(rep instanceof Directory)
			if(((Directory)rep).perm.owner==null)
				return true;
			else
				if(((Directory)rep).perm.owner.username.equals(user.username))
					return ((Directory)rep).perm.read;
		if(rep instanceof File)
			if(((File)rep).perm.owner==null)
				return true;
			else
				if(((File)rep).perm.owner.username.equals(user.username))
					return ((File)rep).perm.read;
		return false;
	}
	
	public String inputToPath(String input) {
		StringTokenizer st = new StringTokenizer(input,"/");
		String path = st.nextToken();
		while(st.countTokens()>1)
			path+="/"+st.nextToken();
		return path;
	}
	
}

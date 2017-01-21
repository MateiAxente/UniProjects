import javax.swing.UIManager;

import de.javasoft.plaf.synthetica.SyntheticaClassyLookAndFeel;

public class LookAndFeel {

	public static void main(String[] args) throws MyInvalidPathException, MyNotEnoughSpaceException {
		try 
	    {
	      UIManager.setLookAndFeel(new SyntheticaClassyLookAndFeel());
	    } 
	    catch (Exception e) 
	    {
	      e.printStackTrace();
	    }
		TermW POO = new TermW("MyCloud LookAndFeel");
	}
	
}

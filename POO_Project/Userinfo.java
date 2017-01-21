import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class Userinfo extends JList<String> {

	public Userinfo(User user,Dimension d) {
		DefaultListModel<String> info = new DefaultListModel<String>();

		this.setModel(info);
		
		this.setPreferredSize(new Dimension(d.width-300,20));
		
		this.setVisibleRowCount(-1);
		
		this.setBounds(0,0,d.width-15,20);
									
		info.addElement("Username: "+user.username);
		info.addElement("Firstname: "+user.firstname);
		info.addElement("Lastname: "+user.lastname);
		info.addElement("Created: ["+user.created+"]");
		info.addElement("Last login: ["+user.lastLogin+"]");
		
	}
}

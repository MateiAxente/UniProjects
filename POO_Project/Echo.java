import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Echo extends JDialog {

	public JLabel j;
	
	public Echo(String name) {
		JDialog dialog = new JDialog();
		dialog.setTitle("ECHO!");
		j = new JLabel(name);
		j.setPreferredSize(new Dimension(160,90));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setBounds(100,100,160,90);
		dialog.add(j);
		dialog.setVisible(true);
		dialog.setAlwaysOnTop(true);
	}
}

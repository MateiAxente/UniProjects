import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.text.Format;
import java.time.LocalDateTime;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.print.SimpleDoc;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public class TermW extends JFrame implements ActionListener {
	
	public JPanel act,p;
	public JScrollPane scroll;
	public JTextField input;
	public JTextField in;
	public JTextField com;
	Dimension d;
	
	
	public Vector<User> users;
	public Directory current;
	public Directory home;
	public Pwd current_path;
	public Singleton single;
	public CloudService cs;
	public Logger log =Logger.getLogger("Log");
	
	public TermW(String name) throws MyInvalidPathException, MyNotEnoughSpaceException {
		
		super(name);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(500,400));
		this.getContentPane().setBackground(Color.black);
		this.setLayout(null);
		this.setLocation(500,100);
		
		
		
			home=new Directory(null,"home");
			current=home;

			users = new Vector<User>();
			users.add(new User("root","student"));
			users.add(new User("guest",null));
			
			single=Singleton.getInstance();
			single.user=users.get(1);
			
			current_path=new Pwd(this);

			cs = new CloudService(single.user);
			
			FileHandler h = null;
			
			try {
				h = new FileHandler("MyCloudLog.txt");
				h.setFormatter(new SimpleFormatter());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			log.addHandler(h);
			log.setUseParentHandlers(false);
			
			
			
		d = this.getSize();		
		
		act = new JPanel();
		act.setLayout(new BoxLayout(act,BoxLayout.Y_AXIS));
		act.setBackground(Color.black);
		act.setForeground(Color.white);
		act.setBounds(new Rectangle(0,0,d.width-15,d.height-35));
		
		
		scroll=new JScrollPane(act);
		scroll.setBounds(new Rectangle(0,0,d.width-15,d.height-35));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		this.add(scroll);
		
		output("Welcome to MyCloud!");
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		p.setBackground(Color.darkGray);
		p.setForeground(Color.white);
		p.setMaximumSize(new Dimension(9999,20));
		p.setMinimumSize(new Dimension(d.width-15,20));
		
		
		try {
			current.accept(current_path);
		} catch (MyInvalidPathException e) {
			log.info(e.toString());
		} catch (MyNotEnoughSpaceException e) {
			log.info(e.toString());
		}
		
		
		com = new JTextField(single.user.username+"@"+current_path.result+"~: ");
		com.setBackground(new Color(0,60,0));
		com.setForeground(Color.white);
		
		com.setText(" "+single.user.username+"@"+current_path.result+"~: ");
		
		com.setMaximumSize(new Dimension(com.getText().length(),20));
		com.setMinimumSize(new Dimension(com.getText().length(),20));
		com.setBorder(null);
		com.setEditable(false);
		p.add(com);
		
		in = new JTextField();
		in.setBackground(new Color(0,20,0));
		in.setForeground(Color.white);
		in.setMaximumSize(new Dimension(9999,20));
		in.setMinimumSize(new Dimension(d.width-15-com.getText().length()-10,20));
		in.setBorder(null);
		p.add(in);
		
		act.add(p);
		
		in.addActionListener(this);
		in.requestFocus();

		this.show();
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		
		in.setEditable(false);
		in.setBackground(new Color(20,20,20));
		com.setBackground(new Color(0,30,0));
		
		this.act.add(p);
		
		
		
			String line=in.getText();
			if(line != null && line.compareTo("") != 0) {
				StringTokenizer st=new StringTokenizer(line);
				String aux = st.nextToken();
				switch(aux) {
				case "echo":
					if(st.nextToken().equals("-POO")) {
						Echo e = new Echo(st.nextToken());
					}
					else
						output(st.nextToken());
					break;
				case "newuser":
					users.add(new User(st.nextToken(),st.nextToken(),st.nextToken(),st.nextToken()));
					break;
				case "userinfo":
					if(st.hasMoreTokens()) {
						if(st.nextToken().equals("-POO")) {
							Userinfo list = new Userinfo(single.user,d);
							this.act.add(list);
							this.show();
						}
					}
					else
						output(single.user.userinfo());
					break;
				case "login":
					aux=st.nextToken();
					int i=0;
					while(i<users.size() && users.get(i).username.equals(aux)==false)
						i++;
					if(i<users.size()) {
						if(users.get(i).password.equals(st.nextToken())) {
							single.user=users.get(i);
							single.user.lastLogin=LocalDateTime.now();
							cs.user=single.user;
							log.info("|LOGIN| "+single.user.username+" "+LocalDateTime.now());
						}
						else
							output("Incorrect password");
					}
					else
						output("User not found");
					break;
				case "logout":
					log.info("|LOGIN| "+single.user.username+" "+LocalDateTime.now());
					single.user=users.get(1);
					single.user.lastLogin=LocalDateTime.now();
					break;
				default:
					if(single.user.username.equals("guest")==false) {
						CommandFactory f;
						if(st.hasMoreTokens())
							f = new CommandFactory(this,cs,single.user,st.nextToken("\0"),current);
						else
							f = new CommandFactory(this,cs,single.user);
						Command c = f.getCommand(aux);
						if(c!=null) {
							try {
								boolean toDO=true;
								if(c instanceof Ls && ((Ls)c).path!=null) {
									Vector<Repository> v = current.move(((Ls)c).path);
									for(int j=0;j<v.size();j++)
										v.get(j).accept(c);
									if(v.size()==0)
										output("Repository not found");
									toDO=false;
								}
								if(c instanceof Rm) {
									Vector<Repository> v = current.move(((Rm)c).path);
									for(int j=0;j<v.size();j++)
										v.get(j).accept(c);
									if(v.size()==0)
										output("Repository not found");
									toDO=false;
								}
								if(c instanceof Upload) {
									Vector<Repository> v = current.move(((Upload)c).path);
									for(int j=0;j<v.size();j++)
										v.get(j).accept(c);
									if(v.size()==0)
										output("Repository not found");
									toDO=false;
								}
								if(c instanceof Sync) {
									Vector<Repository> v = current.move(((Sync)c).path);
									for(int j=0;j<v.size();j++)
										v.get(j).accept(c);
									if(v.size()==0)
										output("Repository not found");
									toDO=false;
								}
								if(c instanceof Cat) {
									Vector<Repository> v = current.move(((Cat)c).name);
									for(int j=0;j<v.size();j++)
										v.get(j).accept(c);
									if(v.size()==0)
										output("Repository not found");
									toDO=false;
								}
								if(toDO)
									current.accept(c);
							} catch (MyInvalidPathException e) {
								log.info(e.toString());
							} catch (MyNotEnoughSpaceException e) {
								log.info(e.toString());
							}
						}
						else
							output("Invalid command");
						if(c instanceof Cd)
							current=((Cd)c).current;
					}
					else
						output("Permission denied");
					break;
				}
				
			}		
			

			d = this.getSize();
			
			p = new JPanel();
			p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
			p.setBackground(Color.darkGray);
			p.setForeground(Color.white);
			p.setMaximumSize(new Dimension(9999,20));
			p.setMinimumSize(new Dimension(d.width-15,20));
			
			com = new JTextField();
			com.setBackground(new Color(0,60,0));
			com.setForeground(Color.white);
			com.setMaximumSize(new Dimension(com.getText().length(),20));
			com.setMinimumSize(new Dimension(com.getText().length(),20));
			com.setBorder(null);
			com.setEditable(false);
			p.add(com);
			
			in = new JTextField();
			in.setBackground(new Color(0,20,0));
			in.setForeground(Color.white);
			in.setMaximumSize(new Dimension(9999,20));
			in.setMinimumSize(new Dimension(d.width-15-com.getText().length()-10,20));
			in.setBorder(null);
			p.add(in);
			
			
			try {
				current.accept(current_path);
				com.setText("  "+single.user.username+"@"+current_path.result+"~: ");
			} catch (MyInvalidPathException e) {
				log.info(e.toString());
			} catch (MyNotEnoughSpaceException e) {
				log.info(e.toString());
			}			
			
			
			act.add(p);
			

			in.requestFocus();
			in.addActionListener(this);
			
		this.show();
		
		JScrollBar v=this.scroll.getVerticalScrollBar();
		v.setValue(v.getMaximum());
	}
	
	
	public void output(String data) {
		StringTokenizer str = new StringTokenizer(data,"\n");
		while(str.hasMoreTokens()) {
			JTextField text = new JTextField(str.nextToken());
			text.setBackground(Color.black);
			text.setForeground(Color.white);
			text.setMaximumSize(new Dimension(99999,20));
			text.setMinimumSize(new Dimension(d.width-15,20));
			text.setBorder(null);
			text.setEditable(false);
			this.act.add(text);
			this.show();
		}
	}
}

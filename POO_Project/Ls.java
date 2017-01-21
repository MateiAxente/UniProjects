import java.awt.Color;
import java.awt.Dimension;
import java.util.StringTokenizer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Ls extends ReadCommand {

	public TermW window;
	public User user;
	public String path;
	boolean rec;
	boolean all;
	boolean poo;
	int level=0;
	
	public Ls(TermW window,User user,String path,int level) {
		this.window=window;
		this.user=user;
		this.level=level;
		this.rec=false;
		this.all=false;
		this.poo=false;
		this.path=path;
		String aux;
		String name=null;
		//this.window.output("PATH_IN: "+path);
		if(this.path!=null) {
			StringTokenizer st = new StringTokenizer(this.path);
			aux=st.nextToken();
			while(aux.equals(""))
				aux=st.nextToken();
			//this.window.output("AUX: "+aux);
			while(st.hasMoreTokens()) {
				//this.window.output("AUX: "+aux);
				switch(aux) {
				case "-a":
					this.all=true;
					aux=st.nextToken();
					break;
				case "-r":
					this.rec=true;
					aux=st.nextToken();
					break;
				case "-ar":
					this.all=true;
					this.rec=true;
					aux=st.nextToken();
					break;
				case "-ra":
					this.all=true;
					this.rec=true;
					aux=st.nextToken();
					break;
				case "-POO":
					this.poo=true;
					break;
				default:
					name=aux;
					break;
				}
			}
			switch(aux) {
			case "-a":
				this.all=true;
				//aux=st.nextToken();
				break;
			case "-r":
				this.rec=true;
				//aux=st.nextToken();
				break;
			case "-ar":
				this.all=true;
				this.rec=true;
				//aux=st.nextToken();
				break;
			case "-ra":
				this.all=true;
				this.rec=true;
				//aux=st.nextToken();
				break;
			case "-POO":
				this.poo=true;
				break;
			default:
				name=aux;
				break;
			}
			this.path=name;
		}
		//this.window.output(this.toString());
	}
	
	/*public Ls(TermW window,User user,Directory current,String path,int level) {
		this.window=window;
		this.user=user;
		this.current=current;
		this.level=level;
	}*/
	
	@Override
	public void execute(Repository rep) throws MyInvalidPathException, MyNotEnoughSpaceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Directory dir) throws MyInvalidPathException, MyNotEnoughSpaceException {
		//if(this.readPermission(this.user,dir)) {
			if(this.rec) {
				if(this.all) {
					if(this.level>=0)
						this.window.output(dir.toList(this.level));
					Ls ls = new Ls(this.window,this.user,"-ar",this.level+1);
					if(this.readPermission(this.user,dir))
						for(int i=0;i<dir.children.size();i++) {
							dir.children.get(i).accept(ls);
						}
				}
				else {
					if(this.level>=0)
						this.window.output(dir.toString(this.level)+"/");
					Ls ls = new Ls(this.window,this.user,"-r",this.level+1);
					if(this.readPermission(this.user,dir))
						for(int i=0;i<dir.children.size();i++) {
							dir.children.get(i).accept(ls);
						}
				}
			}
			else {
				if(this.all)
					if(poo) {
						DefaultTableModel data = new DefaultTableModel();
						JTable j = new JTable(data);
						//Object[][] data;// = new Object[][]();
						data.addColumn("Dir/File");
						data.addColumn("Name");
						data.addColumn("");
						data.addColumn("");
						data.addColumn("");
						data.addColumn("");
						for(int i=0;i<dir.children.size();i++) {
							StringTokenizer st = new StringTokenizer(dir.children.get(i).toList());
							String data1,data2,data3,data4,data5,data6;
							data1=st.nextToken();
							data2=st.nextToken();
							data3=st.nextToken();
							data4=st.nextToken();
							data5=st.nextToken();
							if(st.hasMoreTokens())
								data6=st.nextToken();
							else
								data6="";
							data.addRow(new String[]{data1,data2,data3,data4,data5,data6});
						}
						
						JTable table = new JTable(data);
						table.setRowHeight(20);
						table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
						JScrollPane scr = new JScrollPane(table);
						Dimension d = this.window.getSize();
						table.setMaximumSize(new Dimension(d.width,100));
						scr.setMaximumSize(new Dimension(d.width,100));
						scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
						this.window.act.add(scr);
					}						
					else
						for(int i=0;i<dir.children.size();i++)
							this.window.output(dir.children.get(i).toList());
				else
					for(int i=0;i<dir.children.size();i++)
						this.window.output(dir.children.get(i).toString());
			}
		//}
		//else
			//this.window.output("Permission denied");
	}

	@Override
	public void execute(File file) {
		if(this.all)
			this.window.output(file.toList(this.level));
		else
			this.window.output(file.toString(this.level));
	}

	
	public String toString() {
		return "PATH: "+this.path+" R: "+this.rec+" A: "+this.all;
	}
}

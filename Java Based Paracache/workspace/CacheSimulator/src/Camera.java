/**
 * FILE: Camera.java
 * AUTHOR: Karishma Rao
 * DATE: February 16th, 2003
 */

/**
 * DESCRIPTION: Launches the various CAMERA applications
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


//DRIVER CLASS - CONTAINS THE main METHOD AND CREATES THE FRAME 
public class Camera{
	public static void main(String []args){
		JFrame frame = new CameraFrame();
		frame.setSize(500, 400);
		frame.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.show();

	}//END MAIN
}//END CLASS Camera

/////////////////////////////////////////////////////////////////////////////////////////////

class CameraFrame extends JFrame {

	//DECLARE ALL THE COMPONENTS

	JPanel cameraPanel = new JPanel();
	JPanel cachePanel = new JPanel();
	JPanel memoryPanel = new JPanel();
	JPanel blockPanel = new JPanel();
	JPanel offsetPanel = new JPanel();
	JPanel submitPanel = new JPanel();

	JPanel backgroundPanel = new JPanel();
	JLabel title = new JLabel();


	JLabel cacheLabel = new JLabel("Tag Size in Bit [1-12]",SwingConstants.CENTER);
	JLabel memoryLabel = new JLabel("Memory Size in Bit [1-16]",SwingConstants.CENTER);
	JLabel blockLabel = new JLabel("Index Size in Bit [1-12]",SwingConstants.CENTER);
	JLabel offsetLabel = new JLabel("Offset Size in Bit [1-5]",SwingConstants.CENTER);

	JTable cachetype = new JTable(2,2){
		//Function to highlight specific cell selected
		public Component prepareRenderer (TableCellRenderer renderer, int index_row, int index_col){
			Component comp = super.prepareRenderer(renderer, index_row, index_col);

			if( index_row < 2 ){
				comp.setBackground(new Color(204,255,204,200));
			} else {
				// comp.setBackground(Color.CYAN);
			}                           

			if(isCellSelected(index_row, index_col)){           
				comp.setFont(comp.getFont().deriveFont(
						Font.BOLD));
				comp.setBackground(new Color(234,255,234));

			}
			if(isCellSelected(0,1))
			{
				cacheLabel.setText("Tag Size in Bit [1-12]");
				memoryLabel.setText("Memory Size in Bit [1-16]");
				blockLabel.setText("Block Size in Bit [1-12]");
				blockTextBox.disable();
				blockTextBox.setBackground(new Color(100,100,100));
				cacheTextBox.enable();
				cacheTextBox.setBackground(Color.WHITE);
				offsetTextBox.enable();
				offsetTextBox.setBackground(Color.WHITE);
				title.setText("<html><body align=center><font face=\"Calibri\" "
						+ "<h1>Cache and Virtual Memory Simulator</h1>"
						+ "<h3> Select Simulator Type and fill in any 2 text boxes.</h3></html>");

			}
			else if(isCellSelected(1,1))
			{
				cacheLabel.setText("TLB Size inn Bit [1-6]");
				memoryLabel.setText("Physical Memory in Bit [1-9]");
				blockLabel.setText("Virtual Memory in Bit [1-12]");

				//blockTextBox.disable();
				//blockTextBox.setBackground(new Color(100,100,100));
				//cacheTextBox.disable();
				//cacheTextBox.setBackground(new Color(100,100,100));
				offsetTextBox.disable();
				offsetTextBox.setBackground(new Color(100,100,100));
				blockTextBox.enable();
				blockTextBox.setBackground(Color.WHITE);
				
			
				title.setText("<html><body align=center><font face=\"Calibri\" "
						+ "<h1>Cache and Virtual Memory Simulator</h1>"
						+ "<h3> Select Simulator Type and fill in memory text box.</h3></html>");

			}
			else  
			{
				cacheLabel.setText("Tag Size in Bit [1-12]");
				memoryLabel.setText("Memory Size in Bit [1-16]");
				blockLabel.setText("Block Size in Bit [1-12]");
				blockTextBox.enable();
				blockTextBox.setBackground(Color.WHITE);
				cacheTextBox.enable();
				cacheTextBox.setBackground(Color.WHITE);
				offsetTextBox.enable();
				offsetTextBox.setBackground(Color.WHITE);
				title.setText("<html><body align=center><font face=\"Calibri\" "
						+ "<h1>Cache and Virtual Memory Simulator</h1>"
						+ "<h3> Select Simulator Type and fill in any 3 text boxes.</h3></html>");
			}
		
			return comp;
		}
	    public boolean isCellEditable(int row, int column) {                
            return false;     }
	};




	JPanel buttonPanel = new JPanel();

	JButton submit = new JButton("GO!");
	JTextField cacheTextBox = new JTextField();





	JLabel backgroundLabel = new JLabel(new ImageIcon("background.jpg"));
	JTextField memoryTextBox = new JTextField();
	JTextField blockTextBox = new JTextField();
	JTextField offsetTextBox = new JTextField();


	//initialize variable for cache
	int cacheSize =20;
	int memorySize=20;
	int wordSize=20;



	//CONSTRUCTOR
	public CameraFrame(){

		setTitle("Cache and Virtual Memory Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage(CameraFrame.class.getResource("camera.jpg")));
		//title.setBackground(new Color(255,255,255));

		title.setText("<html><body align=center><font face=\"Calibri\" "
				+ "<h1>Cache and Virtual Memory Simulator</h1>"
				+ "<h3> Select Simulator Type and fill in any 3 text boxes.</h3></html>");


		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int first =0, second=0, third=0, fourth=0;
				if (!memoryTextBox.getText().equals(""))
				{
					try
					{
						first = Integer.parseInt( memoryTextBox.getText());
					}
					catch(NumberFormatException nfe)  
					{  
						first=0;  
					}  
				}
				if (!cacheTextBox.getText().equals(""))
				{
					try {second = Integer.parseInt(cacheTextBox.getText());}
					catch(NumberFormatException nfe)  { second=0;}  
				}
				if (!blockTextBox.getText().equals(""))
				{
					try
					{		
						third = Integer.parseInt(blockTextBox.getText());
					}
					catch(NumberFormatException nfe)  
					{  
						third=0;  
					}  
				}
				if (!offsetTextBox.getText().equals(""))
				{
					try
					{
						fourth= Integer.parseInt(offsetTextBox.getText());
					}
					catch(NumberFormatException nfe)  
					{  
						fourth=0;  
					}  
				}


				if(cachetype.getSelectedColumn()==0 && cachetype.getSelectedRow()==0)
				{
					if (DMCnormalisation(first,second,third,fourth))
					{
						JFrame frame = new DMCFrame(cacheSize, memorySize, wordSize);
						frame.setSize(1200, 700);
						frame.setResizable(false);
						frame.show();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Please fill in any 3 boxes. Memory size must be larger than sum of the other data");
					}
				}
				else if(cachetype.getSelectedColumn()==0 && cachetype.getSelectedRow()==1)
				{
					if (SACnormalisation(first,second,third,fourth))
					{
						JFrame frame = new SACFrame(cacheSize,memorySize, wordSize);
						frame.setSize(1200, 700);
						frame.setResizable(false);
						frame.show();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Please fill in any 3 boxes. Memory size must be larger than sum of the other data");
					}
				}
				else if(cachetype.getSelectedColumn()==1 && cachetype.getSelectedRow()==0)
				{
					if (FACnormalisation(first,second,fourth))
					{
						JFrame frame = new FACFrame( memorySize, wordSize);
						frame.setSize(1200, 700);
						frame.setResizable(false);
						frame.show();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Please fill in any 2 boxes. Memory size must be larger than sum of the other data");
					}
				}
				else if(cachetype.getSelectedColumn()==1 && cachetype.getSelectedRow()==1)
				{
					if (VMnormalisation(first,second,third))
					{
					JFrame frame = new VMFrame(first,(int)Math.pow(2,second),third);
					frame.setSize(1200, 700);
					frame.setResizable(false);
					frame.show();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Please fill in the boxes correctly. TLB must be the smallest.");

					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "No Simulator Type is Selected");
				}
			}	


		});
		cachetype.setCellSelectionEnabled(true);
		ListSelectionModel cellSelectionModel = cachetype.getSelectionModel();
		cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			@SuppressWarnings("deprecation")
			public void valueChanged(ListSelectionEvent e) {
				String selectedData = null;

				int selectedRow = cachetype.getSelectedRow();
				int selectedColumn = cachetype.getSelectedColumn();

			}

		});



		GridLayout labelTextLayout = new GridLayout (1,2);

		cachetype.setRowSelectionAllowed(false);
		cachetype.setColumnSelectionAllowed(false);
		cachetype.setCellSelectionEnabled(true);
		cachetype.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable arg0,Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
				Component tableCellRendererComponent = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
				int align = DefaultTableCellRenderer.CENTER;     	
				((DefaultTableCellRenderer)tableCellRendererComponent).setHorizontalAlignment(align);
				return tableCellRendererComponent;
			}
		};

		cachetype.getColumnModel().getColumn(0).setCellRenderer(renderer);
		cachetype.getColumnModel().getColumn(1).setCellRenderer(renderer);


		cachetype.setShowGrid(false);
		cachetype.setValueAt("Direct Mapped Cache", 0, 0);
		cachetype.setValueAt("Fully Associative Cache", 0, 1);
		cachetype.setValueAt("Set Associative Cache", 1, 0);
		cachetype.setValueAt("Virtual Memory and Paging", 1, 1);
		cachetype.setRowHeight(22);





		memoryPanel.setLayout(labelTextLayout);
		memoryPanel.add(memoryLabel);
		memoryPanel.add(memoryTextBox);
		memoryPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		//memoryPanel.setBackground(new Color(0,0,0,0));

		cachePanel.setLayout(labelTextLayout);
		cachePanel.add(cacheLabel);
		cachePanel.add(cacheTextBox);
		cachePanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		//cachePanel.setBackground(new Color(0,0,0,0));

		blockPanel.setLayout(labelTextLayout);
		blockPanel.add(blockLabel);
		blockPanel.add(blockTextBox);
		blockPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		//blockPanel.setBackground(new Color(0,0,0,0));

		offsetPanel.setLayout(labelTextLayout);
		offsetPanel.add(offsetLabel);
		offsetPanel.add(offsetTextBox);
		offsetPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		//offsetPanel.setBackground(new Color(0,0,0,0));

		cameraPanel.setLayout(new GridLayout(6,1,0,0));
		title.setPreferredSize(new Dimension(420, 80));

		cameraPanel.setPreferredSize(new Dimension(500, 320));


		cameraPanel.add(cachetype);
		cameraPanel.add(memoryPanel);

		cameraPanel.add(cachePanel);
		cameraPanel.add(blockPanel);
		cameraPanel.add(offsetPanel);

		submitPanel.setLayout(new GridLayout(1,1));
		submitPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		submitPanel.add(submit);
		submitPanel.setBackground(new Color(0,0,0,0));

		cameraPanel.add(submitPanel);


		Container c = getContentPane();
		backgroundPanel.setLayout(new BorderLayout());
		JPanel Test = new TestPane();
		Test.setLayout(new FlowLayout());
		cameraPanel.setBackground(new Color(0,0,0,0));
		cameraPanel.setBorder(BorderFactory.createEmptyBorder(10,50,50,50));
		Test.add(title);
		Test.add(cameraPanel);


		c.setLayout(new BorderLayout());
		c.add(Test, BorderLayout.CENTER);


		pack();

	}
	public class TestPane extends JPanel {

		private Font titlefont;
		private BufferedImage bi;

		public TestPane() {
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(30, 0, 0, 0);
			try {
				bi = ImageIO.read(new File("background.jpg"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			titlefont = UIManager.getFont("Label.font");
			setBackground(Color.BLACK);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 200);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			int x = (getWidth() - bi.getWidth()) / 2;
			int y = (getHeight()- bi.getHeight()) / 2;
			g2d.drawImage(bi, x, y, this);
			g2d.setFont(titlefont);
			g2d.setColor(Color.WHITE);
			g2d.dispose();
		}

	}
	public boolean VMnormalisation (int a, int b, int c)
	{
		if (a>0 && b>0 && c>0 && a<=9 && b<65 && c<= 12 && b<a &&b<c) return true;
		else return false;
	}
	public boolean DMCnormalisation (int a, int b, int c, int d)
	{
		int count =0;
		if (a>0 && a<17)
			count++;
		if (b>0 && b<=12)
			count++;
		if ((c>0 && c<a-b && b>0 )||(c>0 && c<a-d && d>0 && a>0))
			count++;
		if ((d>0 && d<a-b && b>0 && a>0)|| (d>0 && d<a-c && c>0 && a>0) || (d>0 && a==0))
			count++;
		if (count==3)
		{

			if (a==0)
			{
				memorySize=b+c+d;
				cacheSize=c;
				wordSize=d;
			}
			else if(b==0)
			{
				memorySize=a;
				cacheSize=c;
				wordSize=d;
			}
			else if (c==0)
			{
				memorySize=a;
				cacheSize=a-b-d;
				wordSize=d;
			}
			else if (d==0)
			{
				memorySize=a;
				cacheSize=c;
				wordSize= a-b-c;
			}
			return true;
		}
		else
			return false;
	}

	public boolean FACnormalisation (int a, int b, int d)
	{
		int count =0;
		if (a>0 && a<17)
			count++;
		if (b>0 && b<=12 && (b<a || a==0))
			count++;

		if ((d>0 && (d<a-b || a==0)))
			count++;
		if (count==2)
		{

			if (a==0)
			{
				memorySize=b+d;
				cacheSize=b;
				wordSize=d;
			}
			else if(b==0)
			{
				memorySize=a;
				cacheSize=b;
				wordSize=d;
			}

			else if (d==0)
			{
				memorySize=a;
				cacheSize=b;
				wordSize= a-b;
			}
			return true;
		}
		else
			return false;
	}

	public boolean SACnormalisation (int a, int b, int c, int d)
	{
		int count =0;
		if (a>0 && a<17)
			count++;
		if (b>0 && b<=12)
			count++;
		if ((c>0 && c<a-b && b>0 )||(c>0 && c<a-d && d>0 && a>0))
			count++;
		if ((d>0 && d<a-b && b>0 && a>0)|| (d>0 && d<a-c && c>0 && a>0) || (d>0 && a==0))
			count++;
		if (count==3)
		{

			if (a==0)
			{
				memorySize=b+c+d;
				cacheSize=c+1;
				wordSize=d;
			}
			else if(b==0)
			{
				memorySize=a;
				cacheSize=c+1;
				wordSize=d;
			}
			else if (c==0)
			{
				memorySize=a;
				cacheSize=a-b-d+1;
				wordSize=d;
			}
			else if (d==0)
			{
				memorySize=a;
				cacheSize=c+1;
				wordSize= a-b-c;
			}
			return true;
		}
		else
			return false;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}//END CLASS Camera
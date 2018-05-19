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
import javax.swing.*;
import javax.swing.border.*;

//DRIVER CLASS - CONTAINS THE main METHOD AND CREATES THE FRAME 
public class Camera{
	public static void main(String []args){
		JFrame frame = new CameraFrame();
		frame.setSize(500, 400);
		frame.setResizable(false);
		frame.show();
	}//END MAIN
}//END CLASS Camera

/////////////////////////////////////////////////////////////////////////////////////////////

class CameraFrame extends JFrame {
	
	//DECLARE ALL THE COMPONENTS
	
	JPanel cameraPanel = new JPanel();	
	JEditorPane cameraEditPane = new JEditorPane();
	
	JButton dmc = new JButton("Direct Mapped Cache");
	JButton fac = new JButton("Fully Associative Cache");
	JButton sac = new JButton("Set Associative Cache");
	JButton vm = new JButton("Virtual Memory and Paging");
	
	//initialize variable for cache
	int cacheSize =20;
	int memorySize=20;
	int wordSize=20;
	int setA = 2;
	//JButton about = new JButton("About CAMERA");	
	
	//CONSTRUCTOR
	public CameraFrame(){
		
		setTitle("CAMERA Workbenches");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage(CameraFrame.class.getResource("cam.gif")));
		
		cameraEditPane.setContentType("text/html");
		cameraEditPane.setText("<body bgcolor = \"aqua\" color = \"blue\" align = \"center\"><h1><i>Camera Workbenches</i></h1>"
								+"<h2><i>Cache And Memory Resource Allocation Workbenches</i></h2>"
								+"<h3> Launch CAMERA's cache mapping and virtual memory workbenches by clicking on the buttons below.</h3></body>");
		cameraEditPane.setBorder(BorderFactory.createCompoundBorder
			(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
			
		dmc.setSize(new Dimension(30, 50));
		dmc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				


			    // prompt the user to enter the configuration
				while(true)
				{
				try {
					 cacheSize =  Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of cache block (0-12)?"));
					 if (cacheSize <=12 && cacheSize>=0)
					 break;
				}
				catch (NumberFormatException e1) {
					  }
				}
				while(true)
				{
				try {

					 memorySize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of memory block?"
					 		+ "\n Input must be more than " +Integer.toString(cacheSize)+" and less than 17."));
					   if(memorySize>cacheSize && memorySize<=16)
					     {
					     break;
					     }

				}
				catch (NumberFormatException e1) {
					  }
				}
				while(true)
				{
				try {
					wordSize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of word per block?"
							+ "\n Input must be positive integer less than or equal to " + Integer.toString(memorySize-cacheSize) +"."));
				     if(wordSize<=memorySize-cacheSize && wordSize>=0)
				     {
				     break;
				     }
				
				}
				catch (NumberFormatException e1 ) {
					  }
				}
				

			     JFrame frame = new DMCFrame(cacheSize, memorySize, wordSize);

			     
			    
				frame.setSize(1200, 700);
				frame.setResizable(false);
				frame.show();
			}
		});
		
		fac.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				  // prompt the user to enter the configuration
				while(true)
				{
				try {
					 cacheSize =  Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of cache block (0-12)?"));
					 if (cacheSize>=0 && cacheSize<=12)
					 break;
				}
				catch (NumberFormatException e1) {
					  }
				}
				while(true)
				{
				try {

					 memorySize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of memory block?"
					 		+ "\n Input must be more than  " +Integer.toString(cacheSize)+" and less than 17."));
					   if(memorySize>cacheSize && memorySize<17)
					     {
					     break;
					     }

				}
				catch (NumberFormatException e1) {
					  }
				}
				while(true)
				{
				try {
					wordSize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of word per block?"
							+ "\n Input must be less than or equal to " + Integer.toString(memorySize-cacheSize) +"."));
				     if(wordSize<=memorySize-cacheSize && wordSize>=0)
				     {
				     break;
				     }
				
				}
				catch (NumberFormatException e1 ) {
					  }
				}
				
				JFrame frame = new FACFrame(cacheSize, memorySize, wordSize);
				
				frame.setSize(1200, 700);
				frame.setResizable(false);
				frame.show();
			}
		});
		
		sac.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				while(true)
				{
				try {
					 cacheSize =  Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of cache block (0-12)?"));
					 if (cacheSize <=12 && cacheSize>=0)
					 break;
				}
				catch (NumberFormatException e1) {
					  }
				}
				while(true)
				{
				try {

					 memorySize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of memory block?"
					 		+ "\n Input must be more than " +Integer.toString(cacheSize)+" and less than 17."));
					   if(memorySize>cacheSize && memorySize<=16)
					     {
					     break;
					     }

				}
				catch (NumberFormatException e1) {
					  }
				}
				while(true)
				{
				try {
					wordSize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many bit of word per block?"
							+ "\n Input must be positive integer less than or equal to " + Integer.toString(memorySize-cacheSize) +"."));
				     if(wordSize<=memorySize-cacheSize && wordSize>=0)
				     {
				     break;
				     }
				
				}
				catch (NumberFormatException e1 ) {
					  }
				}
						     
			     JFrame frame = new SACFrame(cacheSize, memorySize, wordSize);
			     
			    
				frame.setSize(1200, 700);
				frame.setResizable(false);
				frame.show();
			}
		});
				
				
		
		vm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFrame frame = new VMFrame();
				frame.setSize(1200, 700);
				frame.setResizable(false);
				frame.show();
			}
		});		
		
		/*about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAboutFrame();				
			}
		});*/
		
		cameraPanel.setLayout(new GridLayout(2, 2));
		cameraPanel.setPreferredSize(new Dimension(300, 300));
		cameraPanel.setBorder(BorderFactory.createCompoundBorder
			(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
			
		cameraPanel.add(dmc);
		cameraPanel.add(fac);
		cameraPanel.add(sac);
		cameraPanel.add(vm);
		
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(cameraEditPane, BorderLayout.NORTH);
		c.add(cameraPanel, BorderLayout.CENTER);	
		
		pack();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	//FUNCTION TO DISPLAY DETAILS ABOUT CAMERA
	/*public void showAboutFrame() {
		JFrame aboutFrame = new JFrame("About CAMERA");
		aboutFrame.setSize(new Dimension(450, 400));
		aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		aboutFrame.setIconImage(Toolkit.getDefaultToolkit().createImage(DMCFrame.class.getResource("cam.gif")));
	
		JEditorPane aboutPane = new JEditorPane();
		aboutPane.setEnabled(false);
		aboutPane.setContentType("text/html");
		
		aboutPane.setText("<h1 color = \"blue\" align = \"center\">CAMERA Workbenches</h1><h2 align = \"center\" color = \"blue\">Cache And Memory Resource Allocation Workbenches</h2>"
						  +"<h3 align = \"center\">A Master's Project developed under the guidance of Dr. Linda M. Null, Computer Science and Math Department, Penn State Harrisburg.</h3>"
						  +"<p align = \"center\">CAMERA is a collection of interactive simulators and workbenches for concepts in memory management such as cache mapping schemes and virtual memory and paging."
						  +"It was developed as a teaching tool and an enhancement to the book <i>The Essentials of Computer Organization and Architecture</i>, by Dr. Linda M. Null and Julie Lobur.</p>"
						  +"<p align = \"center\">Copyright &copy 2003 Karishma Rao</p>");
		aboutPane.setCaretPosition(0);
		
		JScrollPane aboutScroll = new JScrollPane();
		aboutScroll.getViewport().setView(aboutPane);			
		
		aboutFrame.getContentPane().add(aboutScroll);
		aboutFrame.show();
	}//END FUNCTION showAboutFrame*/
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
}//END CLASS Camera
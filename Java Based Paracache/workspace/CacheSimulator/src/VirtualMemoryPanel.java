import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//CLASS FOR THE VIRTUAL MEMORY PANEL
class VirtualMemoryPanel extends JPanel {

	//DECLARE THE COMPONENTS

	//BOOLEAN ARRAY OF 8 TO INDICATE WHETHER A PAGE IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolPages = new boolean[Constant.MAXSIZE];

	//DATA MEMBERS USED AS MEASUREMENTS FOR DRAWING THE LINES FOR THE VIRTUAL MEMORY PAGES
	private Dimension dV;
	private int dx, dy, offsetX, offsetY;
	private int pageSize;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR	    
	public VirtualMemoryPanel(int page_Size){

		//SET THE PROPERTIES OF THE PANEL
		pageSize = page_Size;
		setPreferredSize(new Dimension(120, pageSize*25 + 60));
		Border virtualMemBorder= BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createTitledBorder(virtualMemBorder, " Virtual Memory ")); 
		
	}//END CONSTRUCTOR

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//OVERRIDE THE paintComponent METHOD
	public void paintComponent(Graphics g){

		dV = this.getSize();             //GET THE DIMENSIONS OF THE PANEL
		
		//RESET THE BACKGROUND COLOR
		g.setColor(new Color(255, 250, 250));
		g.fillRect(0, 0, dV.width, dV.height);

		dV.height -= 20;               
		dV.width -= 20;   
	

		//EVALUATE THE HORIZONTAL PARAMETERS           
		//dy = (int)(dV.height/pageSize);        //8 PAGE MEMORY
		dy = 25;
		int temp = dy*pageSize;
		offsetY = 30;
		offsetX = (int) (dV.width/2 - 30);	     //USED TO HORIZONTALY CENTER THE DRAWING IN THE PANEL
		
		//DRAW THE 8 PAGES
		g.setColor(Color.black);
		for (int i = 0; i < pageSize+1; i++)
			//HORIZONTAL LINES TO DRAW THE PAGES
			g.drawLine(offsetX, offsetY + dy*i, offsetX + 80, offsetY + dy*i);

		for (int i = 0; i < pageSize; i++)
			//DRAW THE PAGE LABELS    
			g.drawString("Page "+i, offsetX+20, offsetY + dy*i + 18);		

		//DRAW THE 2 VERTICAL LINES
		g.drawLine(offsetX, offsetY, offsetX, offsetY + temp);
		g.drawLine(offsetX + 80, offsetY, offsetX + 80, offsetY + temp);	

		//HIGHLIGHT PAGE IF REQUIRED
		for (int i = 0; i < pageSize; i++){
			if (boolPages[i]){
				g.setColor(Color.yellow);
				g.fillRect(offsetX+1, offsetY+1 + dy*i, 79, dy - 1);

				//DRAW THE PAGE LABELS
				g.setColor(Color.black);
				g.drawString("Page "+i, offsetX+20, offsetY + dy*i +18);
			}
		}
	
	}//END FUNCTION paintComponent

}//END CLASS VirtualMemoryPanel

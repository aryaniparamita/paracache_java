/*
 * FILE:	PageTablePanel.java
 * AUTHOR:	Karishma Rao
 * DATE:	February 5th, 2003
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//CLASS FOR THE TLB PANEL
class PageTablePanel extends JPanel {

	//DECLARE THE COMPONENTS

	//BOOLEAN ARRAY OF 8 TO INDICATE WHETHER A ROW IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolRows = new boolean[Constant.MAXSIZE];
	
	//STRING ARRAYS OF 8 TO HOLD THE VALUES OF THE FRAME NUMBER AND VALID BIT FOR EACH ROW
	String[] frameNum = new String[Constant.MAXSIZE];
	String[] validBit = new String[Constant.MAXSIZE];

	//DATA MEMBERS USED AS MEASUREMENTS FOR DRAWING THE ROWS
	private Dimension dP;
	private int dx, dy, offsetX, offsetY;
	private int tLB;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR	    
	public PageTablePanel(int TLB){
		tLB = TLB;
		//SET THE PROPERTIES OF THE PANEL
		setPreferredSize(new Dimension(200, tLB*20 + 60));
		Border pageTableBorder= BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createTitledBorder(pageTableBorder, " Page Table ")); 

		//INITIALIZE THE STRING ARRAYS
		for (int i = 0; i < tLB; i++){
			frameNum[i] = "-";
			validBit[i] = "0";
		}
	}//END CONSTRUCTOR

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//OVERRIDE THE paintComponent METHOD
	public void paintComponent(Graphics g){

		dP = this.getSize();             //GET THE DIMENSIONS OF THE PANEL
		
		//RESET THE BACKGROUND COLOR
		g.setColor(new Color(250, 255, 250));
		g.fillRect(0, 0, dP.width, dP.height);

		dP.height -= 20;               
		dP.width -= 40;   
	

		//EVALUATE THE HORIZONTAL PARAMETERS           
		//dy = (int)(dP.height/(tLB+1));        //8 ROWS AND THE HEADER
		dy = 20;
		int temp = dy*(tLB+1);
		offsetY = 18;
		
		dx = (int) ((dP.width-20)/2);
		offsetX = 40;	     //USED TO HORIZONTALY CENTER THE DRAWING IN THE PANEL
		
		//DRAW THE 8 ROWS PLUS THE HEADER ROW
		g.setColor(Color.black);
		for (int i = 0; i < (tLB+2); i++)
			//HORIZONTAL LINES TO DRAW THE ROWS
			g.drawLine(offsetX, offsetY + dy*i, offsetX + 2*dx, offsetY + dy*i);

		//DRAW THE ROW CONTENTS
		g.drawString("Frame Number", offsetX + 23, offsetY+17);
		g.drawString("Valid Bit", offsetX + dx + 43, offsetY+17);
		
		for (int i = 0; i < tLB; i++){	
			g.drawString(""+i, offsetX/2, offsetY + dy*(i+1) + 17);
			g.drawString(frameNum[i], offsetX+dx/2, offsetY + dy*(i+1) + 17);
			g.drawString(validBit[i], offsetX+3*dx/2, offsetY + dy*(i+1) + 17);
		}

		//DRAW THE 3 VERTICAL LINES
		g.drawLine(offsetX, offsetY, offsetX, offsetY + temp);
		g.drawLine(offsetX + dx, offsetY, offsetX + dx, offsetY + temp);
		g.drawLine(offsetX + 2*dx, offsetY, offsetX + 2*dx, offsetY + temp);	

		
		//HIGHLIGHT ROW IF REQUIRED
		for (int i = 1; i < (tLB+1); i++){
			if (boolRows[i-1]){
				g.setColor(Color.pink);
				g.fillRect(offsetX+1, offsetY+1 + dy*i, dx-1, dy-1);
				g.fillRect(offsetX+1+dx, offsetY+1+dy*i, dx-1, dy-1);
				
				g.setColor(Color.black);
				g.drawString(frameNum[i-1], offsetX+dx/2, offsetY + dy*i +17);
				g.drawString(validBit[i-1], offsetX+3*dx/2, offsetY + dy*i +17);
				
				break;
			}
		}
	
	}//END FUNCTION paintComponent

}//END CLASS PageTablePanel
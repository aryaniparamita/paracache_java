import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//CLASS FOR THE TLB PANEL
class TLBPanel extends JPanel {
	
	//DECLARE THE COMPONENTS

	//BOOLEAN ARRAY OF 4 TO INDICATE WHETHER A ROW IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolRows = new boolean[Constant.MAXSIZE];
	
	//STRING ARRAYS OF 4 TO HOLD THE VALUES OF THE VIRTUAL AND PHYSICAL PAGE NUMBERS FOR EACH ROW
	String[] virtPageNum = new String[Constant.MAXSIZE];
	String[] physPageNum = new String[Constant.MAXSIZE];

	//DATA MEMBERS USED AS MEASUREMENTS FOR DRAWING THE ROWS
	private Dimension dT;
	private int dx, dy, offsetX, offsetY, tLB;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR	    
	public TLBPanel(int TLB){
		tLB = TLB;
		//SET THE PROPERTIES OF THE PANEL
		setPreferredSize(new Dimension(200, tLB*30+75));
		Border tlbBorder= BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createTitledBorder(tlbBorder, " Translation Lookaside Buffer ")); 

		//INITIALIZE THE STRING ARRAYS
		for (int i = 0; i < tLB; i++){
			virtPageNum[i] = "-";
			physPageNum[i] = "-";
		}
	}//END CONSTRUCTOR

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//OVERRIDE THE paintComponent METHOD
	public void paintComponent(Graphics g){


		
		dT = this.getSize();             //GET THE DIMENSIONS OF THE PANEL
		
		//RESET THE BACKGROUND COLOR
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, dT.width, dT.height);
		
		
		dT.height -= 20;               
		dT.width -= 20;   
	


		//EVALUATE THE HORIZONTAL PARAMETERS           
		//dy = (int)((dT.height-20)/5);        //4 ROWS AND THE HEADER
		dy = 30;
		int temp = dy*(tLB+1);
		offsetY = 20;
		
		dx = (int) (dT.width/2);
		offsetX = 10;	     //USED TO HORIZONTALY CENTER THE DRAWING IN THE PANEL
		
		//DRAW THE 8 ROWS PLUS THE HEADER ROW
		g.setColor(Color.black);
		for (int i = 0; i < tLB+2; i++)
			//HORIZONTAL LINES TO DRAW THE ROWS
			g.drawLine(offsetX, offsetY + dy*i, offsetX + 2*dx, offsetY + dy*i);

		//DRAW THE ROW CONTENTS
		g.drawString("Virtual Page Number", offsetX + 15, offsetY+24);
		g.drawString("Physical Page Number", offsetX + dx + 10, offsetY+24);
		
		for (int i = 0; i < tLB; i++){			
			g.drawString(virtPageNum[i], offsetX+dx/2, offsetY + dy*(i+1) + 24);
			g.drawString(physPageNum[i], offsetX+3*dx/2, offsetY + dy*(i+1) + 24);
		}

		//DRAW THE 3 VERTICAL LINES
		g.drawLine(offsetX, offsetY, offsetX, offsetY + temp);
		g.drawLine(offsetX + dx, offsetY, offsetX + dx, offsetY + temp);
		g.drawLine(offsetX + 2*dx, offsetY, offsetX + 2*dx, offsetY + temp);	

		
		//HIGHLIGHT ROW IF REQUIRED
		for (int i = 1; i <= tLB; i++){
			if (boolRows[i-1]){
				g.setColor(Color.pink);
				g.fillRect(offsetX+1, offsetY+1 + dy*i, dx-1, dy-1);
				g.fillRect(offsetX+1+dx, offsetY+1+dy*i, dx-1, dy-1);
				
				g.setColor(Color.black);
				g.drawString(virtPageNum[i-1], offsetX+dx/2, offsetY + dy*i +24);
				g.drawString(physPageNum[i-1], offsetX+3*dx/2, offsetY + dy*i +24);
				
				break;
			}
		}
	
	}//END FUNCTION paintComponent

}//END CLASS TLBPanel

/**
 * FILE:	PhysicalMemoryPanel.java
 * AUTHOR:	Karishma Rao
 * DATE:	December 2nd, 2002
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//CLASS FOR THE PHYSICAL MEMORY PANEL
class PhysicalMemoryPanel extends JPanel{
	
	//BOOLEAN ARRAY OF 4 TO INDICATE WHETHER A FRAME IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolFrames = new boolean[Constant.MAXSIZE];

	//BOOLEAN ARRAY OF 32 TO INDICATE WHETHER A MEMORY WORD IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolWords = new boolean[Constant.MAXSIZE];

	//STRING ARRAY OF 4*16 TO HOLD THE VALUE OF THE VIRTUAL MEMORY PAGE THAT IS IN A PARTICULAR PHYSICAL MEMORY PAGE
	String[][] stringWords = new String[Constant.MAXSIZE2D][Constant.MAXSIZE2D];
	
	//DATA MEMBERS USED AS MEASUREMENTS TO DRAW THE MEMORY BLOCKS AND WORDS
	Dimension dM;
	int dxM, dyM, offsetXM, offsetYM;
	int physicalMemoryWidth, physicalMemoryHeight;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR
	public PhysicalMemoryPanel(int physical_Memory_Width, int physical_Memory_Height){

		//SET THE PROPERTIES OF THE PANEL
		physicalMemoryWidth = physical_Memory_Width;
		physicalMemoryHeight = physical_Memory_Height;
		setPreferredSize(new Dimension(500, physicalMemoryHeight*20*4 + 40));
		Border memBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createTitledBorder(memBorder, " Physical Memory ")); 
		
		//INITIALIZE THE ARRAY THAT SHOWS EACH VIRTUAL MEMORY WORD IN EACH PHYSICAL MEMORY FRAME
		for (int i = 0; i < physicalMemoryHeight; i++)
			for (int j = 0; j < 32; j++)
				stringWords[i][j] = "";

	}//END CONSTRUCTOR	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//OVERRIDE THE paintComponent METHOD
	public void paintComponent(Graphics g){
		
		/* TO CALCULATE THE MEASUREMENTS FOR DRAWING THE BLOCKS AND WORDS,
		* THE DIMENSION OF THE PANEL IS RETRIEVED, THEN THE HEIGHT AND WIDTH ARE REDUCED BY 20 PIXELS EACH
		* TO ACCOUNT FOR THE BORDER. PAGE NUMBERS OCCUPY SOME PIXELS ON THE LEFT AS WELL.
		* THE BLOCK AND WORD DIMENSIONS ARE THEN CALCULATED BASED ON THE AREA LEFT.
		*/

		//40 PIXELS FOR PAGE LABELS
		dM = this.getSize();
		//RESET THE BACKGROUND COLOR
		g.setColor(new Color(255, 255, 250));
		g.fillRect(0, 0, dM.width, dM.height);

		dM.height -= 20;
		int verticalBlock = physicalMemoryHeight * 4;
		//dyM = (int)((dM.height)/verticalBlock);        //16 BLOCK MEMORY
		dyM = 20;
		int temp2 = dyM*verticalBlock;
		offsetYM = 18;   //USED TO VERTICALLY CENTER THE DRAWING IN THE PANEL

		dM.width -= 20;
		//dxM = (int) ((dM.width - 15)/8);       //8 WORDS PER BLOCK
		dxM = 50;
		int temp3 = dxM*8;
		offsetXM = (int)((dM.width - temp3 - 15)/2);   //USED TO HORIZONTALLY CENTER THE DRAWING IN THE PANEL


		//DRAW THE 16 MEMORY BLOCKS WITH 8 WORDS PER BLOCK
		g.setColor(Color.black);
		for (int i = 0; i <= verticalBlock; i++){
			//HORIZONTAL LINES TO DRAW THE BLOCKS
			g.drawLine(offsetXM + 50, offsetYM + dyM*i, offsetXM + 50 + temp3, offsetYM + dyM*i);
		}//END FOR

		for (int i = 0; i < 9; i++) {
			//VERTICAL LINES TO DIVIDE THE BLOCKS INTO WORDS
			g.drawLine(offsetXM + dxM*i + 50, offsetYM, offsetXM + dxM*i + 50, offsetYM + temp2);
		}//END FOR
			
		for (int i = 0; i < physicalMemoryHeight; i++){
			for (int j = 0; j < 32; j++)			
				//IF THE MEMORY HAS DATA IN IT, THEN WRITE IT ON THE SCREEN
		//		if (!boolFrames[i] && !stringWords[i][j].equals("")){
		//			g.setColor(Color.black);
				g.drawString(stringWords[i][j], offsetXM + dxM*(j%8) + 52, dyM*i*4 + offsetYM + dyM*(int)(j/8) + 14);				
			//		}
		}

		//PAGE LABELS
		for (int i = 1; i <= physicalMemoryHeight; i++){
			//DRAW THE PAGE FRAME LABELS
			g.drawString("Page "+(i-1), offsetXM-12, offsetYM + dyM*2*(i-1) + dyM*2*i);
			g.drawString("Frame", offsetXM-12, offsetYM + dyM*2*(i-1) + dyM*2*i + 12);
		}
		
		//INDICATING THE 32 WORDS THAT BELONG TO A PAGE
		for (int i = 1; i <= verticalBlock*4; i+=16){
			g.drawLine(offsetXM+43, offsetYM+i*dyM/4, offsetXM+45, offsetYM+i*dyM/4);
			g.drawLine(offsetXM+43, offsetYM+i*dyM/4, offsetXM+43, offsetYM+(i+14)*dyM/4);
			g.drawLine(offsetXM+43, offsetYM+(i+14)*dyM/4, offsetXM+45, offsetYM+(i+14)*dyM/4);
		}
		
		//HIGHLIGHT A FRAME, IF REQUIRED
		for (int i = 0; i < physicalMemoryHeight; i++){
			if (boolFrames[i]){
				g.setColor(Color.blue);
				for (int j = 0; j < 4; j++){
					for (int k = 0; k < 8; k++)
						g.fillRect(offsetXM + dxM*k + 51, dyM*i*4 + offsetYM + dyM*j + 1, dxM - 1, dyM - 1);

				}
			}
		}	
		
		for (int i = 0; i < physicalMemoryHeight; i++){
			if (boolFrames[i]){
				//HIGHLIGHT THE PARTICULAR WORD, IF REQUIRED
				for (int j = 0; j < 32; j++)
					//HIGHLIGHT THE REQUIRED WORD IN BLACK
					if (boolWords[j]){
						g.setColor(Color.black);
						g.fillRect(offsetXM + dxM*(j%8) + 51, dyM*i*4 + offsetYM + 1 + dyM*(int)(j/8), dxM - 1, dyM - 1);
					}
				break;
			}
		}			
	
		for (int i = 0; i < physicalMemoryHeight; i++){
			if (boolFrames[i]){
				g.setColor(Color.white);
				for (int j = 0; j < 32; j++)
					//IF THE MEMORY HAS DATA IN IT, THEN REWRITE IT ON THE SCREEN
				//	if (!stringWords[i][j].equals("")){						
						g.drawString(stringWords[i][j], offsetXM + dxM*(j%8) + 52, dyM*i*4 + offsetYM + dyM*(int)(j/8) + 14);				
				//	}				
			}					
		}
	
	}//END FUNCTION paintComponent

}//END CLASS PhysicalMemoryPanel

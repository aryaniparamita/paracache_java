/**
 * FILE:	MemoryPanel.java
 * AUTHOR:	Karishma Rao
 * DATE:	December 2nd, 2002
 */


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


//CLASS FOR THE MEMORY PANEL
class MemoryPanel extends JPanel{

	
	//BOOLEAN ARRAY OF 32 TO INDICATE WHETHER A MEMORY BLOCK IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolBlocks = new boolean[Constant.MAXSIZE];

	//BOOLEAN ARRAY OF 8 TO INDICATE WHETHER A MEMORY WORD IS TO BE HIGHLIGHTED OR NOT
	boolean[] boolWords = new boolean[Constant.MAXSIZE];

	//DATA MEMBERS USED AS MEASUREMENTS TO DRAW THE MEMORY BLOCKS AND WORDS
	Dimension dM;
	int dxM, dyM, offsetXM, offsetYM;

	//STRING ARRAY OF HEX VALUES USED TO LABEL THE MEMORY BLOCKS IN THE PANEL
	String[] memLabel = new String[Constant.MAXSIZE];
	
    int memory_Size=32;
    int word_Size=1;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR
	public MemoryPanel(int memorySize, int wordSize){
		memory_Size=memorySize;
		word_Size=wordSize;
		setPreferredSize(new Dimension(wordSize*60 + 60, memorySize*20 + 20*3));
		//SET THE PROPERTIES OF THE PANEL
		Border memBorder = BorderFactory.createEtchedBorder();
		setBorder(BorderFactory.createTitledBorder(memBorder, " Memory ")); 
		int j=0;
		for (int i=0;i<memorySize;i++)
		{
			
			memLabel[i]=Integer.toHexString(j).toUpperCase() ;
			while(memLabel[i].length()<3)
			{
				memLabel[i]="0"+memLabel[i];
			}
			j+=wordSize;
		}
	}//END CONSTRUCTOR	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//OVERRIDE THE paintComponent METHOD
	public void paintComponent(Graphics g){
		
		/* TO CALCULATE THE MEASUREMENTS FOR DRAWING THE BLOCKS AND WORDS,
		* THE DIMENSION OF THE PANEL IS RETRIEVED, THEN THE HEIGHT AND WIDTH ARE REDUCED BY 20 PIXELS EACH
		* TO ACCOUNT FOR THE BORDER. ALSO, THE HEIGHT IS FURTHER REDUCED TO ACCOUNT FOR THE WORD LABELS
		* AND THE WIDTH IS FURTHER REDUCED LATER TO ACCOUNT FOR BLOCK LABELS.
		* THE BLOCK AND WORD DIMENSIONS ARE THEN CALCULATED BASED ON THE AREA LEFT.
		*/

		//15 PIXELS FOR WORD LABELS AND 20 FOR BLOCK LABELS
		dM = this.getSize();
		//dM.height -= 20;
		dyM = (int)((dM.height - 60)/memory_Size);        
		int temp2 = dyM*memory_Size;
		offsetYM = (int)((dM.height - 40 - temp2)/2);   //USED TO VERTICALLY CENTER THE DRAWING IN THE PANEL

		//dM.width -= 20;
		dxM = (int) ((dM.width - 60)/word_Size);       //8 WORDS PER BLOCK
		int temp3 = dxM*word_Size;
		offsetXM = (int)((dM.width - temp3 - 60)/2);   //USED TO HORIZONTALLY CENTER THE DRAWING IN THE PANEL

		//RESET THE BACKGROUND COLOR
		g.setColor(new Color(255, 255, 250));
		g.fillRect(0, 0, dM.width, dM.height);

		//DRAW THE MEMORY BLOCKS WITH WORDS PER BLOCK
		g.setColor(Color.black);
		for (int i = 0; i < memory_Size+1; i++){

			//HORIZONTAL LINES TO DRAW THE BLOCKS
			g.drawLine(offsetXM + Constant.OFFX,  dyM*i+ Constant.OFFX, 
					offsetXM + Constant.OFFX + dxM*word_Size, dyM*i + Constant.OFFX);
		}//END FOR

		for (int i = 0; i < word_Size; i++) {

			//DRAW THE WORD LABELS
			g.drawString("+"+i, offsetXM + 15 + dxM/2 + dxM*i, 31);   

			//VERTICAL LINES TO DIVIDE THE BLOCKS INTO WORDS
			g.drawLine(offsetXM + dxM*i + Constant.OFFX, Constant.OFFX,
					offsetXM + dxM*i + Constant.OFFX, dyM*memory_Size + 40);
		}//END FOR
		
		//VERTICAL LINES AT THE RIGHT END OF THE BLOCK
		g.drawLine(offsetXM + dxM*word_Size + Constant.OFFX, Constant.OFFX,
				offsetXM + dxM*word_Size + Constant.OFFX, dyM*memory_Size + 40 );
		
		
		//BLOCK LABELS
		for (int i = 0; i < memory_Size; i++)

			//DRAW THE BLOCK LABELS
			g.drawString(memLabel[i], offsetXM + 6,  dyM*(i)+Constant.OFFX+15);

		//INSERT INITIAL DATA IN MAIN MEMORY - Bi Wj
		for (int i = 0; i < memory_Size; i++) {
			for (int j = 0; j < word_Size; j++) {

				//DRAW THE BLOCK AND WORD VALUES INSIDE THE WORDS
				g.drawString("B"+(i)+"W"+(j), offsetXM + dxM*j + Constant.OFFX+3,  Constant.OFFX + dyM*i +15);
			}//END FOR
		}//END FOR

		//HIGHLIGHT A BLOCK, IF REQUIRED
		for (int j = 0; j < memory_Size; j++){
			if (boolBlocks[j]){
				g.setColor(Color.blue);
				for (int i = 0; i < word_Size; i++){
					
					g.fillRect(offsetXM + dxM*i + Constant.OFFX, Constant.OFFX  + dyM*(j), dxM, dyM);

					//HIGHLIGHT THE REQUIRED WORD IN BLACK
					if (boolWords[i]){
						g.setColor(Color.black);
						g.fillRect(offsetXM + dxM*i + Constant.OFFX, Constant.OFFX+ dyM*(j), dxM, dyM);
						g.setColor(Color.blue);
					}
				}
				//WRITE THE DATA IN HIGHLIGHTED MEMORY BLOCK IN WHITE
				g.setColor(Color.white);
				for (int i = 0; i < word_Size; i++)
					g.drawString(""+j+"_"+i, offsetXM + Constant.OFFX + dxM*i + 3,
								 Constant.OFFX + dyM*(j) + 15);
			}
		}

	}//END FUNCTION paintComponent

}//END CLASS MemoryPanel


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* FILE: SACFrame.java
 * AUTHOR: Karishma Rao
 * DATE: February 16th, 2003
 */

/*
 * DESCRIPTION: Implements the interactive tutorial to demonstrate the working of Set Associative Cache Mapping
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;

//CLASS SACFrame IS THE MAIN FRAME OF THE APPLICATION
class SACFrame extends JFrame {

	//DECLARE ALL THE COMPONENTS

	//PANEL FOR DRAWING THE CACHE
	private SACachePanel cachePanel;  

	//PANEL FOR DRAWING THE MEMORY       
	private MemoryPanel memoryPanel;

	//PANELS TO CONTAIN THE PROGRESS BAR, NAVIGATION BUTTONS, CACHE AND CACHE HITS AND MISSES FIELDS AND NUMBER OF WAYS COMBOBOX
	private JPanel progressButtonPanel, cache, cacheHitsMisses, cacheWays, cacheHitsMissesWays;

	//PANELS TO CONTAIN THE ADDRESS REFERENCE STRING AND MAIN MEMORY ADDRESS BITS
	private JPanel pStringListMM, pAddRefStr, pAutoSelfGen, pBitsInMM1, pBitsInMM2, pBitsInMM;

	//NAVIGATION BUTTONS AND ADDRESS REFERENCE STRING GENERATION BUTTONS
	private JButton restart, next, back, quit, autoGen, selfGen;

	//LABELS FOR THE VARIOUS COMPONENTS
	private JLabel lNumWays, lCacheHits, lCacheMisses, lProgress, lBits1, lBits2,tProgress;

	//TEXTFIELDS FOR CACHE HITS AND MISSES AND MAIN MEMORY ADDRESS BITS
	private JTextField tCacheHits, tCacheMisses, tTag, tSet, tBlock, tWord1, tWord2;
	
	//COMBOBOX AND OPTION STRING FOR CHOICE OF 2-WAY OR 4-WAY SET ASSOCIATIVE CACHE
	private JComboBox cNumWays; 
	private String[] numWays= {"2", "4","8","16","32"};


	//SCROLLPANES FOR PROGRESS UPDATE TEXTAREA AND ADDRESS REFERENCE STRING LISTBOX
	private JScrollPane progressScroll, addRefStrScroll, memoryPanelScroll;

	//LISTBOX FOR ADDRESS REFERENCE STRING
	private JList addRefStrList;

	//BORDERS FOR THE VARIOUS PANELS
	private Border cacheHMBorder, bitsInMM1Border, bitsInMM2Border, bitsInMMBorder, addRefStrBorder;

	//ARRAY TO HOLD THE 256 POSSIBLE MEMORY ADDRESSES
	private String[] addresses = new String[Constant.MAXSIZE];
	private Vector listData = new Vector();


	//INT USED TO TRACK STEPPING THROUGH THE TUTORIAL (WHEN THE Next AND back BUTTONS ARE USED)
	private int moveStatus = 0;

	//INT USED TO INDICATE THE ADDRESS REFERENCE STRING IN QUESTION
	private int evaluateIndex = 0;
	
	//INT USED TO INDICATE THE NUMBER OF WAYS IN THE CACHE
	//private int numWaysSel = 0;

	//INTS TO KEEP COUNT OF THE NUMBER OF CACHE HITS AND MISSES OCCURRING DURING ONE RUNTHROUGH
	private int cacheHits, cacheMisses;

	//STRINGS USED TO CONVERT THE DECIMAL MEMORY ADDRESS VALUES INTO HEX AND BINARY
	private String hexAddress = new String();
	private String binAddress = new String();

	//INTS USED TO MAINTAIN THE DECIMAL VALUE OF THE MEMORY ADDRESS IN QUESTION - IN MAIN MEMORY AS WELL AS IN CACHE
	private int intSetDec = 0;
	private int intWordDec = 0;
	private int intBlockDecMem = 0;
	private int intBlockDec = 0;

	//INT USED TO HOLD THE VALUE OF THE BLOCK IN THE CACHE SET WHERE THE MEMORY BLOCK EXISTS, IF AT ALL
	private int memInCacheBlock = -1;

	//STRINGS USED TO MAINTAIN THE MAIN MEMORY ADDRESS BITS
	private String tag = new String();
	private String set = new String();
	private String word = new String();
	private String blockMem = new String();


	//DATA MEMBERS TO HOLD THE EMPTY AND LRU STATUS OF THE CACHE BLOCKS WITHIN THE SETS
	private int emptyCacheBlock = -1;
	private int lruCacheBlock = -1;
	private int statusLRU;
	private int[][] statusCacheLRU2Way = new int[Constant.MAXSIZE2D][Constant.MAXSIZE2D];
	private boolean[][]	statusCacheEmpty2Way = new boolean[Constant.MAXSIZE2D][Constant.MAXSIZE2D];
	

	//BOOLEAN USED TO KEEP TRACK OF WHETHER THE RESTART BUTTON WAS PRESSED - USED TO ENABLE Next BUTTON APPROPRIATELY
	private boolean reStarted = true;
	
	//BOOLEAN USED TO INDICATE WHETHER THE Next BUTTON WAS CLICKED OR THE Back BUTTON
	private boolean nextClicked = true;	
	//INTEGER USED TO VARY CACHE AND MEMORY SIZE
	private int cacheSize=10;
	private int blockSize=10;
	private int wordSize=10;
	private int memorySize=10;
	private int cacheSetSize=10;
	private int tagDigit=1;
	private int cacheDigit, memoryDigit;
	private int wordDigit=3;
	private int setDigit=1;
	private int numWay = 1, numWaysPower=2;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	    
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR
	public SACFrame(int cacheSizeBit, int memorySizeBit, int wordSizeBit){

		//SET PROPERTIES OF THE MAIN FRAME
		setTitle("Set Associative Cache");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage(SACFrame.class.getResource("camera.jpg")));

		//CREATE COMPONENTS AND SET THEIR PROPERTIES

		//UPDATE CACHE AND MEMORY SIZE INSIDE THE CLASS
		this.cacheSize=(int)Math.pow(2,cacheSizeBit);
		this.blockSize=(int)(Math.pow(2, memorySizeBit-wordSizeBit));
		this.memorySize=(int)(Math.pow(2, memorySizeBit));
		this.wordSize=(int)(Math.pow(2, wordSizeBit));
		cacheSetSize = (int)(Math.pow(2,cacheSizeBit-numWay));
		
		
		//Determine Tag, Block, and Words
		wordDigit = wordSizeBit;
		memoryDigit = memorySizeBit;
	    cacheDigit = cacheSizeBit;
	    

		//NAVIGATION BUTTONS
		restart = new JButton("Restart");
		restart.setBackground(new Color(255,255,255));
		next = new JButton("Next");
		next.setPreferredSize(new Dimension(20,10));
		next.setBackground(new Color(255,255,0));
		back = new JButton("Back");
		back.setBackground(new Color(200,200,255));
		quit = new JButton("Quit");
		quit.setBackground(new Color(255,200,200));


		//CACHE HITS AND MISSES INFO.
		lCacheHits = new JLabel("Hits",SwingConstants.CENTER);
		lCacheMisses = new JLabel("Misses",SwingConstants.CENTER);	
		tCacheHits = new JTextField(5);
		tCacheMisses = new JTextField(5);
		tCacheHits.setEditable(false);
		tCacheHits.setFont(new Font("Monospaced", Font.BOLD, 14));
		tCacheHits.setText("<html>  0");
		tCacheMisses.setEditable(false);
		tCacheMisses.setFont(new Font("Monospaced", Font.BOLD, 14));
		tCacheMisses.setText("<html>  0");
		
		//NUMBER OF WAYS LABEL AND COMBOBOX
		lNumWays = new JLabel("Ways",SwingConstants.CENTER);
		cNumWays = new JComboBox(numWays);

		//PROGRESS UPDATE AREA
		tProgress = new JLabel("",SwingConstants.CENTER);
		tProgress.setOpaque(true);
		tProgress.setBackground(new Color(254,254,254));
		tProgress.setBorder(null);
		tProgress.setFont(new Font("Calibri",Font.PLAIN, 16));
		tProgress.setText("<html>System Configurations : "
						 + cacheSize +" Blocks in Cache | "	  +  blockSize +" Blocks in Main Memory | "  + wordSize +" Words per Block"
						  +"<br> Please generate the Address Reference String. Then click on <b>\"Next\"</b> to continue.</html>");
		progressScroll = new JScrollPane();
		progressScroll.getViewport().add(tProgress);
		progressScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		progressScroll.getViewport().setOpaque(true);
		lProgress = new JLabel("");

		//ADDRESS REFERENCE STRING
		addRefStrList = new JList();
		addRefStrList.setEnabled(false);
		addRefStrScroll = new JScrollPane();
		addRefStrScroll.getViewport().setView(addRefStrList);
		addRefStrScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		addRefStrScroll.setPreferredSize(new Dimension(140, 300));

		//BUTTONS USED TO ADDRESS GENERATION
		autoGen = new JButton("<html>Auto<br>Generate</html>");
		autoGen.setOpaque(true);
		autoGen.setBackground(new Color(250,250,200));
		selfGen = new JButton("<html>Self<br>Generate</html>");
		selfGen.setOpaque(true);
		selfGen.setBackground(new Color(250,250,200));
		

		//BITS IN MAIN MEMORY ADDRESS
		lBits1 = new JLabel("   TAG                 SET             WORD");
		lBits2 = new JLabel("                 BLOCK                  WORD");

		tTag = new JTextField(6);
		tTag.setEditable(false);
		tSet = new JTextField();
		tSet.setEditable(false);	
		tBlock = new JTextField();
		tBlock.setEditable(false);
		tWord1 = new JTextField(6);
		tWord1.setEditable(false);
		tWord2 = new JTextField(6);
		tWord2.setEditable(false);
	

		//SET THE FONT STYLES FOR THE BITS IN MAIN MEMORY ADDRESS
		tTag.setFont(new Font("Monospaced", Font.BOLD, 14));
		tSet.setFont(new Font("Monospaced", Font.BOLD, 14));
		tWord1.setFont(new Font("Monospaced", Font.BOLD, 14));
		tBlock.setFont(new Font("Monospaced", Font.BOLD, 14));
		tWord2.setFont(new Font("Monospaced", Font.BOLD, 14));
		
		//REGISTER LISTENERS	
		cNumWays.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numWay =  cNumWays.getSelectedIndex();
				numWaysPower = (int)Math.pow(2, cNumWays.getSelectedIndex())*2;
				if(cacheSize>=numWaysPower)
				{
				cachePanel.numWays = cNumWays.getSelectedIndex();
				cacheSetSize = (int)(Math.pow(2,cacheDigit-numWay));
				
				repaint();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Sorry cache is not big enough");
					cachePanel.numWays=0;
					cNumWays.setSelectedIndex(0);
					numWay =  0;
					numWaysPower = 2;
					
				}
			}
		});
		
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reStart();
			}
		});

		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextClicked = true;
				step();
			}
		});
		
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextClicked = false;
				step();		
			}
		});
		
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirmQuit = JOptionPane.showConfirmDialog(null, "Really Quit?", "Quit Confirmation",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				switch (confirmQuit){
					case JOptionPane.YES_OPTION: removeInstance();
					case JOptionPane.NO_OPTION: break;
				}
			}
		});

		autoGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoGenerateString();
			}
		});

		selfGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selfGenerateString();
			}
		});
		
		//DISABLE NAVIGATION BUTTONS FOR NOW
		next.setEnabled(false);
		back.setEnabled(false);

		//CREATE PANELS
		//CREATE PANELS



				cachePanel = new SACachePanel(cacheSize, wordSize,tagDigit){
					@Override
					public void paintComponent(Graphics g)
		            {
		                super.paintComponent(g);
		                setOpaque(false);
		                setBackground(Color.BLUE);
		            }
				};
				memoryPanel = new MemoryPanel(blockSize,wordSize){
					@Override
					public void paintComponent(Graphics g)
		            {
		                super.paintComponent(g);
		                setOpaque(false);
		                setBackground(Color.BLUE);
		            }
				};

		progressButtonPanel = new JPanel();
		cache = new JPanel();
		cacheHitsMisses = new JPanel();
		cacheWays = new JPanel();
		cacheHitsMissesWays = new JPanel();
		pAutoSelfGen = new JPanel();
		pAddRefStr = new JPanel();
		pStringListMM = new JPanel();
		pBitsInMM = new JPanel();
		pBitsInMM1 = new JPanel();
		pBitsInMM2 = new JPanel();

		//ADD COMPONENTS TO THE PANELS

		progressButtonPanel.setLayout(new BorderLayout());
		//PANEL WITH PROGRESS UPDATE TEXT AREA AND NAVIGATION BUTTONS
		//progressButtonPanel.add(pBitsInMM,"West");
		//lProgress.setPreferredSize(new Dimension(210,80));
		//progressButtonPanel.add(lProgress,"West");	
		pAutoSelfGen.setPreferredSize(new Dimension(210,80));
		progressButtonPanel.add(pAutoSelfGen, "West");
		progressButtonPanel.add(progressScroll,"Center");
		progressButtonPanel.setBorder(null);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,2));
		buttonPanel.add(back);
		buttonPanel.add(next);	

		buttonPanel.add(quit);
		buttonPanel.add(restart);
		
		progressButtonPanel.add(buttonPanel,"East");

		//PANEL WITH CACHE BLOCKS, HITS AND MISSES INFO.

		cacheHitsMissesWays.setOpaque(true);
		cacheHitsMissesWays.setBackground(new Color(255,255,255));

		
		//PANEL WITH NUMWAYS INFO


		
		cacheHitsMissesWays.setLayout(new GridLayout(1,6));
		cacheHitsMissesWays.add(lNumWays);
		cacheHitsMissesWays.add(cNumWays);
		cacheHitsMissesWays.add(lCacheHits);
		cacheHitsMissesWays.add(tCacheHits);
		cacheHitsMissesWays.add(lCacheMisses);
		cacheHitsMissesWays.add(tCacheMisses);


		cacheHMBorder= BorderFactory.createEtchedBorder();
		cacheHitsMissesWays.setBorder(BorderFactory.createTitledBorder(cacheHMBorder, "")); 

		cache.setLayout(new BorderLayout());
		cache.add(cachePanel, "Center");
		JScrollPane cachePanelScroll = new JScrollPane(cachePanel);
		cachePanelScroll.setPreferredSize(new Dimension(250,500));
		cachePanelScroll.setVerticalScrollBarPolicy(cachePanelScroll.VERTICAL_SCROLLBAR_AS_NEEDED);
		cachePanelScroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		cache.add(cacheHitsMissesWays, "North");
		cache.add(cachePanelScroll);
		


		
		//PANEL WITH ADDRESS REFERENCE STRING AND STRING GENERATION BUTTONS
		pAutoSelfGen.setLayout(new GridLayout(1, 2));
		pAutoSelfGen.add(autoGen);
		pAutoSelfGen.add(selfGen);
		
		pAddRefStr.setLayout(new BorderLayout());
		pAddRefStr.setPreferredSize(new Dimension(160, 400));

		pAddRefStr.add(addRefStrScroll, "Center");
		//pAddRefStr.add(pAutoSelfGen, "South");
		progressButtonPanel.add(pAutoSelfGen, "West");
		addRefStrBorder= BorderFactory.createEtchedBorder();
		pAddRefStr.setBorder(BorderFactory.createTitledBorder(addRefStrBorder, " Address Reference String ")); 

		//PANEL WITH THE MAIN MEMORY ADDRESS BITS INFO.
		pBitsInMM1.setLayout(new BorderLayout());
		bitsInMM1Border= BorderFactory.createEtchedBorder();
		pBitsInMM1.setBorder(BorderFactory.createTitledBorder(bitsInMM1Border, " Main Memory Address ")); 
		pBitsInMM1.add(tTag, "West");
		pBitsInMM1.add(tSet, "Center");
		pBitsInMM1.add(tWord1, "East");	
		pBitsInMM1.add(lBits1, "South");

		pBitsInMM2.setLayout(new BorderLayout());
		bitsInMM2Border= BorderFactory.createEtchedBorder();
		pBitsInMM2.setBorder(BorderFactory.createTitledBorder(bitsInMM2Border, " Memory Block and Word Bits ")); 
		pBitsInMM2.add(tBlock, "Center");
		pBitsInMM2.add(tWord2, "East");	
		pBitsInMM2.add(lBits2, "South");

		pBitsInMM.setLayout(new GridLayout(2, 1));
		bitsInMMBorder= BorderFactory.createEtchedBorder();
		pBitsInMM.setBorder(BorderFactory.createTitledBorder(bitsInMMBorder, ""));
		pBitsInMM.add(pBitsInMM1);
		pBitsInMM.add(pBitsInMM2);

		//PANEL CONTAINING THE ADDRESS REF. STRING PANEL AND BITS IN MM PANEL
		pStringListMM.setLayout(new BorderLayout());
		pStringListMM.setPreferredSize(new Dimension(210, 600));
		pStringListMM.add(pAddRefStr, "Center");
		pStringListMM.add(pBitsInMM, "North");
		
		//ADD COMPONENTS TO THE FRAME CONTAINER
				Container c = getContentPane();
				c.setLayout(new BorderLayout());
				c.add(cache, "Center");
				//c.add(memoryPanel, "Center");
				memoryPanelScroll = new JScrollPane(memoryPanel){
					@Override
					public void paintComponent(Graphics g)
		            {
		                super.paintComponent(g);
		                setOpaque(false);
		                setBackground(Color.BLUE);
		            }
				};

				
				memoryPanelScroll.setPreferredSize(new Dimension(640,500));
				memoryPanelScroll.setVerticalScrollBarPolicy(memoryPanelScroll.VERTICAL_SCROLLBAR_ALWAYS);
				memoryPanelScroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
				memoryPanelScroll.getViewport().setOpaque(false);
				memoryPanelScroll.setBorder(null);
				memoryPanelScroll.getViewport().setBackground(new Color(255,255,0));
				

				c.add(memoryPanelScroll,"East");
				c.add(pStringListMM, "West");
				c.add(progressButtonPanel, "North");
		
		//INITIALIZE THE ARRAYS RELATED TO THE EMPTY AND LRU CACHE BLOCKS
		for (int i = 0; i < cacheSetSize; i++){
			for (int j = 0; j < numWaysPower; j++){
				statusCacheLRU2Way[i][j] = 0;
				statusCacheEmpty2Way[i][j] = true;
			}
		}		

		for (int i = 0; i < wordSize; i++){
			cachePanel.boolWords[i] = false;
			memoryPanel.boolWords[i] = false;
		}
		for (int i = 0; i < blockSize; i++)
			memoryPanel.boolBlocks[i] = false;
		/*
		* CALL THE FUNCTION TO GENERATE THE ARRAY addresses, WHICH CONTAINS ALL THE POSSIBLE MEMORY ADDRESSES
		* THIS ARRAY WILL BE USEFUL IN THE AUTO GENERATION OF ADDRESSES AS WELL AS FOR VALIDATION OF 
		* ADDRESS STRINGS INPUT BY THE USER IF HE/SHE CHOOSES SELF GENERATION.
		*/
		addresses = CheckAddress.createAddresses(memorySize);

		pack();
		
	}//END CONSTRUCTOR
	/*
	* FUNCTION TO RESTART THE APPLICATION
	* BRINGS THE FRAME TO ITS ORIGINAL STARTING CONFIGURATION
	*/
	public void reStart(){

		//UNDO THE HIGHLIGHTS, IF ANY, OF THE PREVIOUS STEPS

		//UPDATE THE STATE OF THE CACHE AND MEMORY
		for (int i = 0; i < cacheSetSize; i++){
			for (int j = 0; j < numWaysPower; j++){
				cachePanel.stringBlocks2Way[i][j] = "";
				cachePanel.tag2Way[i][j] = "";
				cachePanel.boolBlocks2Way[i][j] = false;
			}
		}
			
		for (int i = 0; i < wordSize; i++){
			cachePanel.boolWords[i] = false;
			memoryPanel.boolWords[i] = false;
		}
		for (int i = 0; i < blockSize; i++)
			memoryPanel.boolBlocks[i] = false;

		//UPDATE THE CACHE HITS AND MISSES FIELDS
		cacheHits = 0;
		cacheMisses = 0;
		tCacheHits.setText("<html>  0");
		tCacheMisses.setText("<html>  0");

		//REFRESH THE ADDRESS REFERENCE STRING TO NULL
		listData.removeAllElements();
		addRefStrList.setListData(listData);

		//UPDATE THE BITS IN MAIN MEMORY ADDRESS
		tTag.setText("<html>");
		tSet.setText("<html>");
		tBlock.setText("<html>");
		tWord1.setText("<html>");
		tWord2.setText("<html>");
		tTag.setBackground(new Color(205, 205, 205));
		tSet.setBackground(new Color(205, 205, 205));
		tWord1.setBackground(new Color(205, 205, 205));
		tBlock.setBackground(new Color(205, 205, 205));
		tWord2.setBackground(new Color(205, 205, 205));

		//UPDATE THE PROGRESS FIELD 
		tProgress.setText("<html>Let's start over.<br>Please generate the Address Reference String.");

		//DISABLE THE NEXT AND back BUTTONS
		next.setEnabled(false);
		back.setEnabled(false);
		
		//ENABLE ADDRESS GENERATION BUTTONS
		autoGen.setEnabled(true);
		selfGen.setEnabled(true);
		
		//ENABLE COMBOBOX
		cNumWays.setEnabled(true);

		//RESET THE VALUES OF moveStatus, evaluateIndex, numWays AND reStarted
		moveStatus = 0;
		evaluateIndex = 0;
		numWay = 0;	
		reStarted = true;
		
		//RESET ALL THE VALUES RELATED TO EMPTY AND LRU CACHE BLOCKS
		memInCacheBlock = -1;
		emptyCacheBlock = -1;
		lruCacheBlock = -1;
		statusLRU = 0;
		
		for (int i = 0; i < cacheSetSize; i++){
			for (int j = 0; j < numWaysPower; j++){
				statusCacheLRU2Way[i][j] = 0;
				statusCacheEmpty2Way[i][j] = true;
			}
		}	
	
		//CALL THE REPAINT METHOD
		repaint();

	}//END FUNCTION reStart

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void step(){
		
		/** 
		 * EACH TIME THE NEXT BUTTON IS PRESSED, ALL THE STATES THAT OCURRED UPTO THE CURRENT STATE ARE EVALUATED.
		 * THERE IS A while STATEMENT THAT PERFORMS THIS FUNCTION AND CONTAINS A switch STATEMENT WITHIN 
		 * IT TO EVALUATE EACH STEP AS IT OCCURS.  
		 */
		
		///////////////////////////// INITIALIZATION ////////////////////////////////////////
		
		//UPDATE THE STATE OF THE CACHE AND MEMORY
		for (int i = 0; i < cacheSetSize; i++){
			for (int j = 0; j < numWaysPower; j++){
				cachePanel.stringBlocks2Way[i][j] = "";
				cachePanel.tag2Way[i][j] = "";
				cachePanel.boolBlocks2Way[i][j] = false;
			}
		}

		
		for (int i = 0; i < wordSize; i++){
			cachePanel.boolWords[i] = false;
			memoryPanel.boolWords[i] = false;
		}
		for (int i = 0; i < memorySize; i++)
			memoryPanel.boolBlocks[i] = false;

		//UPDATE THE CACHE HITS AND MISSES FIELDS
		cacheHits = 0;
		cacheMisses = 0;
		tCacheHits.setText("<html>  0");
		tCacheMisses.setText("<html>  0");

		//UPDATE THE BITS IN MAIN MEMORY ADDRESS
		tTag.setText("");
		tSet.setText("");
		tBlock.setText("");
		tWord1.setText("");
		tWord2.setText("");
		tTag.setBackground(new Color(205, 205, 205));
		tSet.setBackground(new Color(205, 205, 205));
		tWord1.setBackground(new Color(205, 205, 205));
		tBlock.setBackground(new Color(205, 205, 205));
		tWord2.setBackground(new Color(205, 205, 205));

		//RESET THE VALUE OF evaluateIndex
		evaluateIndex = 0;
		
		//RESET ALL THE VALUES RELATED TO EMPTY AND LRU CACHE BLOCKS
		memInCacheBlock = -1;
		emptyCacheBlock = -1;
		lruCacheBlock = -1;
		statusLRU = 0;
		
		for (int i = 0; i < cacheSetSize; i++){
			for (int j = 0; j < numWaysPower; j++){
				statusCacheLRU2Way[i][j] = 0;
				statusCacheEmpty2Way[i][j] = true;
			}
		}	
	
		//DISABLE ADDRESS GENERATION BUTTONS
		autoGen.setEnabled(false);
		selfGen.setEnabled(false);
		
		//DISABLE NUMWAYS COMBOBOX UNTIL RESTART
		cNumWays.setEnabled(false);
		
		///////////////////////////// END INITIALIZATION ////////////////////////////////////
		
		//IF Next WAS CLICKED, INCREMENT moveStatus	
		if (nextClicked)
			moveStatus++;
		else{
			//DECREMENT moveStatus AND ENABLE NEXT SINCE IT MIGHT BE DISABLED
			moveStatus--;
			next.setEnabled(true);
		}
		
		//IF NO MORE back MOVES CAN BE MADE, DISABLE back BUTTON
		if (moveStatus == 0){
			back.setEnabled(false);
			tProgress.setText("<html>You cannot go back any further."
							 +"<br>Please click on \"Next\" or \"Restart\" to continue.");
				
			
			//CLEAR THE SELECTED ADDRESS REFERENCE STRING
			addRefStrList.clearSelection();
		}
		else
			//ENABLE back BUTTON ONCE THE FIRST MOVE IS MADE		
			back.setEnabled(true);
		
		//INITIALIZE THE VARIABLE THAT KEEPS TRACK OF THE STATE WE ARE CURRENTLY EVALUATING.
		int tempState = 1;
		
		//CONTINUE TO EVALUATE EACH STATE TILL WE REACH THE CURRENT STATE
		while (tempState <= moveStatus){
			
			switch (tempState%6){

			case 1: //IF A NEW CYCLE IS BEGINNING, OBTAIN NEXT ADDRESS REFERENCE
		
				//OBTAIN THE ADDRESS REFERENCE STRING
				addRefStrList.setSelectedIndex(evaluateIndex);
				
				//ENSURE THAT THE LIST SCROLLS AND SELECTED INDEX IS VISIBLE
				//DUE TO REPAINTING CONSTRAINTS, ONLY DO THIS IN THE CURRENT STATE
				if (tempState == moveStatus)
					addRefStrList.ensureIndexIsVisible(evaluateIndex);
			
				//OBTAIN THE NUMBER OF WAYS SELECTED
				numWay = cNumWays.getSelectedIndex();

				//EVALUATE THE TAG, BLOCK AND WORD
				hexAddress = (String)addRefStrList.getSelectedValue();
				int intAddress = Integer.parseInt(hexAddress, 16);
				binAddress = Integer.toBinaryString(intAddress);
			
				//USING CLASS INTEGER'S parseInt FUNCTION RETURNS A BINARY STRING WITHOUT LEADING 0'S
				//ENSURE THAT binAddress is in full length
			
				if (binAddress.length() < memoryDigit){
					int zeroes = memoryDigit - binAddress.length();
					for (int i = 0; i < zeroes; i++)
						binAddress = '0'+binAddress;
				}
				//RETRIEVE THE TAG, BLOCK AND WORD BITS FROM binAddress BASED ON NUMBER OF WAYS

				setDigit = cacheDigit - cNumWays.getSelectedIndex()-1;
				tagDigit = memoryDigit - wordDigit - setDigit;
				tag = binAddress.substring(0, tagDigit);
				set = binAddress.substring(tagDigit, memoryDigit - wordDigit);
				word = binAddress.substring(memoryDigit - wordDigit);
				blockMem = binAddress.substring(0, memoryDigit - wordDigit);

			
				//CALCULATE THE ACTUAL CACHE AND MEMORY BLOCKS AND WORDS IN QUESTION
				if (!set.equals(""))
				{
				intSetDec = Integer.parseInt(set, 2);
				}
				else
				{
					intSetDec =0;
				}
				intWordDec = Integer.parseInt(word, 2);
				intBlockDecMem = Integer.parseInt(blockMem, 2);
			
				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html>The memory address we want is obtained from the Address Reference String."
									  +"<br>It is (in hexadecimal): " + hexAddress+".");
				
				}
			
				break;
		
			case 2: //EVALUATE THE BITS IN MAIN MEMORY ADDRESS AND HIGHLIGHT THEM 

				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html>The hexadecimal address " + hexAddress 
									  + " evaluates to its binary equivalent " + binAddress+"."
									  + "<br>Hence the bits in the Main Memory Address are divided into "
									  + tag + " --> Tag,  "+ set + " --> Set,  " + word + " --> Word."
									  + "<br>The above field values are used to access the required cache block."
									  + "<br>From the "+ memoryDigit
									  + " bit binary address, "+binAddress+", evaluated above "
									  + "we can also retrieve the following data:"
									  +"<br>The leftmost " + blockMem.length()
									  + " bits, "+blockMem+", and the rightmost "+word.length()
									  + " bits, "+word
									  +", indicate the actual memory block and word");

				

					//HIGHLIGHT THE BITS IN MAIN MEMORY ADDRESS IN GREEN
					tTag.setBackground(Color.green);					
					tSet.setBackground(Color.green);					
					tWord1.setBackground(Color.green);					
					tBlock.setBackground(Color.green);					
					tWord2.setBackground(Color.green);					
				}
				
				tTag.setText(" "+tag);
				tSet.setText("    "+set);
				tWord1.setText(" "+word);
				tBlock.setText("      "+tag+set);
				tWord2.setText(" "+word);

				break;		
		
			case 3: //EVALUATE THE CACHE SET IN QUESTION AND HIGHLIGHT IT

				//UNDO HIGHLIGHTS OF PREVIOUS STEP
				tTag.setBackground(new Color(205, 205, 205));
				tSet.setBackground(new Color(205, 205, 205));
				tWord1.setBackground(new Color(205, 205, 205));
				tBlock.setBackground(new Color(205, 205, 205));
				tWord2.setBackground(new Color(205, 205, 205));

				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html>The bits in the Main Memory Address indicating the cache set are " 
									  + set +"."
									  +"<br>The bits identifying the memory block within the cache set are the tag bits "+tag
									  + "<br> so, the block with this tag in the set "+intSetDec+" is searched in the cache.");
					
				}
			
	
				for (int i = 0; i < numWaysPower; i++)
					cachePanel.boolBlocks2Way[intSetDec][i] = true;
				
					//DETERMINE IF MEMORY BLOCK IS IN CACHE
					memInCacheBlock = getCacheBlock(numWay, intSetDec, tag);
					
					if (memInCacheBlock == -1){//MEMORY BLOCK IS NOT IN CACHE
						
						cacheMisses++;
						tCacheMisses.setText("<html>  "+cacheMisses);
						
						if (tempState == moveStatus){
							tProgress.setText(tProgress.getText() + "<br>Since the matching tag was not found in the set, there was a cache miss.");
							
						}
					}
					
					else{ //MEMORY BLOCK IS IN CACHE
						cacheHits++;
						tCacheHits.setText("<html>  "+cacheHits);
						
						if (tempState == moveStatus){
							tProgress.setText(tProgress.getText() + "<br>Since the matching tag was found in the set, there was a cache hit.");
							
						}
					}				
				
				break;
		
			case 4: //HIGHLIGHT THE MEMORY BLOCK 
			
				//UNDO THE HIGHLIGHTS OF THE PREVIOUS STEP

					for (int i = 0; i < numWaysPower; i++)
						cachePanel.boolBlocks2Way[intSetDec][i] = false;
						
				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html>Since the leftmost "+blockMem.length()
										+ " bits of the address reference string are "+blockMem
									  +", the main memory block in question is, in Hexadecimal, "+Integer.toHexString(intBlockDecMem)+".");
				
				}
			
				//HIGHLIGHT THE MEMORY BLOCK
				memoryPanel.boolBlocks[intBlockDecMem] = true;
				memoryPanel.boolWords[intWordDec] = true;
			
				break;
		
			case 5: //HIGHLIGHT THE CACHE SET AND THE BLOCK WITH THE MEMORY NOW IN IT
				
				//UNDO THE HIGHLIGHTS OF THE PREVIOUS STEP
				memoryPanel.boolBlocks[intBlockDecMem] = false;
				memoryPanel.boolWords[intWordDec] = false;
			
				/*
				* NOW, THERE ARE 3 WAYS TO GO FROM HERE
				* 1. IF THERE IS AN EMPTY BLOCK WITHIN THE SET, SIMPLY BRING THE MEMORY BLOCK INTO CACHE
				* 2. IF THE REQUIRED MEMORY BLOCK IS ALREADY IN THE CACHE SET, DO NOTHING
				* 3. IF THE CACHE SET IS FULL, FIND THE LRU BLOCK AND REPLACE IT WITH THE REQUIRED MEMORY BLOCK
				*/

				//DETERMINE IF MEMORY BLOCK IS IN CACHE
				memInCacheBlock = getCacheBlock(numWay, intSetDec, tag);
			
				//IF THE MEMORY BLOCK WAS NOT IN THE CACHE SET AND AN EMPTY BLOCK IS AVAILABLE
				//BRING THE MEMORY BLOCK AND TAG INTO THE CACHE AND HIGHLIGHT THE CACHE SET
			
				if (memInCacheBlock == -1){
					
					//GET THE FIRST EMPTY BLOCK IN THE SET
					emptyCacheBlock = getFirstEmptyCacheBlock(numWay, intSetDec);
					
					if (!(emptyCacheBlock == -1)){ //AN EMPTY BLOCK IS AVAILABLE WITHIN THE SET
						
						//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
						if (tempState == moveStatus){
							tProgress.setText("<html>As we saw earlier, the required memory block was not in the cache set."
											  +"<br>Since there was an empty block in the set, we brought the memory block into it."
											  +"<br>We also stored the tag, "+tag+", of the memory block with the cache block."
											  +"<br><br>Remember that the memory block could be brought into any empty cache block."
											  +"<br>In our example, we are using the first available empty block in the set.");
							
						}
							
						
							//UPDATE THE LRU AND EMPTY STATUS OF THE BLOCK
							statusLRU++;
							statusCacheLRU2Way[intSetDec][emptyCacheBlock] = statusLRU;
							statusCacheEmpty2Way[intSetDec][emptyCacheBlock] = false;
								
							//UPDATE THE CACHE ARRAYS KEEPING TRACK OF MEMORY BLOCKS AND TAGS
							cachePanel.stringBlocks2Way[intSetDec][emptyCacheBlock] = ""+intBlockDecMem;
							cachePanel.tag2Way[intSetDec][emptyCacheBlock] = tag;

							//HIGHLIGHT THE CACHE BLOCK IN YELLOW
							cachePanel.boolBlocks2Way[intSetDec][emptyCacheBlock] = true;
							cachePanel.boolWords[intWordDec] = true;							
						
						//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
						intBlockDec = emptyCacheBlock;  
					}//END IF EMPTY BLOCK AVAILABLE IN THE SET				
					
					else{ //EMPTY BLOCK NOT AVAILABLE IN THE SET SO LRU BLOCK IS REPLACED
						
						//GET THE LRU CACHE BLOCK WITHIN THE SET
						lruCacheBlock = getLRUCacheBlock(numWay, intSetDec);
						
						//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
						if (tempState == moveStatus){
							tProgress.setText("<html>As we saw earlier, the required memory block was not in the cache set."
											  +"<br>Since the cache set is full, we brought the memory block into the least recently used block in that set."
											  +"<br>We also stored the tag, "+tag+", of the memory block with the cache block."
											  +"<br><br>Remember that the memory block could be brought into any empty cache block."
											  +"<br>In our example, we are using the first available empty block in the set.");
						
						}

						//UPDATE THE COUNTER FOR THE LRU CACHE BLOCK
						
						
							//UPDATE THE CACHE ARRAYS KEEPING TRACK OF MEMORY BLOCKS AND TAGS													
							cachePanel.stringBlocks2Way[intSetDec][lruCacheBlock] = ""+intBlockDecMem;
							cachePanel.tag2Way[intSetDec][lruCacheBlock] = tag;							
							
							//UPDATE THE LRU AND EMPTY STATUS OF THE BLOCK
							statusLRU++;
							statusCacheLRU2Way[intSetDec][lruCacheBlock] = statusLRU;

							//HIGHLIGHT THE CACHE BLOCK IN YELLOW
							cachePanel.boolBlocks2Way[intSetDec][lruCacheBlock] = true;
							cachePanel.boolWords[intWordDec] = true;
					
						//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
						intBlockDec = lruCacheBlock;  				
						
					}//END EMPTY BLOCK NOT AVAILABLE IN THE SET SO LRU BLOCK IS REPLACED
					
				}//END IF MEMORY BLOCK NOT IN CACHE
			
				else {//MEMORY BLOCK IS ALREADY IN THE REQUIRED SET
					
					//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
					if (tempState == moveStatus){
						tProgress.setText("<html>As we saw earlier, the required memory block is already in the cache.");
	
					}
					
				
						
						//UPDATE THE LRU AND EMPTY STATUS OF THE BLOCK
						statusLRU++;
						statusCacheLRU2Way[intSetDec][memInCacheBlock] = statusLRU;	
						
						//HIGHLIGHT THE CACHE BLOCK WITH THE MEMORY BLOCK ALREADY IN IT
						cachePanel.boolBlocks2Way[intSetDec][memInCacheBlock] = true;
						cachePanel.boolWords[intWordDec] = true;
					
					
					//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
					intBlockDec = memInCacheBlock;   
					
				}//END IF MEMORY BLOCK IS ALREADY IN CACHE			
			
				break;
		
			case 0:	//LAST STEP IN CYCLE - CLEANUP STEP!
			
				//UNDO HIGHLIGHTS OF PREVIOUS STEP

					cachePanel.boolBlocks2Way[intSetDec][intBlockDec] = false;
					cachePanel.boolWords[intWordDec] = false;
	
			
				tTag.setText("<html>");
				tSet.setText("<html>");
				tWord1.setText("<html>");
				tBlock.setText("<html>");
				tWord2.setText("<html>");

				tTag.setBackground(new Color(205, 205, 205));
				tSet.setBackground(new Color(205, 205, 205));
				tBlock.setBackground(new Color(205, 205, 205));
				tWord1.setBackground(new Color(205, 205, 205));
				tWord2.setBackground(new Color(205, 205, 205));

				//CLEAR THE SELECTED ADDRESS REFERENCE STRING
				addRefStrList.clearSelection();

				//INCREMENT THE INDEX SO AS TO POINT TO THE NEXT ADDRESS REFERENCE STRING
				evaluateIndex++;

				//IF THE LAST ADDRESS REFERENCE STRING HAS BEEN REACHED, DO THE APPROPRIATE
				if (evaluateIndex == listData.size()){
					
					if (tempState == moveStatus){
						tProgress.setText("<html>This completes the runthrough."
										  +"<br>Please click on \"Restart\", generate an Address Reference String "
										  +"OR click \"Quit\" to finish.");
	
					}
					next.setEnabled(false);
					
					//ENABLE ADDRESS GENERATION BUTTONS 
					autoGen.setEnabled(true);
					selfGen.setEnabled(true);
					
					reStarted = false;
				}

				//ELSE AN ACCESS CYCLE HAS BEEN COMPLETED SO SHOW THE APPROPRIATE MESSAGE IN THE PROGRESS FIELD
				else{
					if (tempState == moveStatus){
						tProgress.setText("<html>This completes an access cycle.");

					}
					
					//CLEAR THE SELECTION IN THE ADDRESS REFERENCE STRING
					addRefStrList.clearSelection();
				}
			
				break;
			
			default:
				JOptionPane.showMessageDialog(null, "Uh Oh, there's a problem in switch-case!");
			}//END switch
			
			tempState++;
			
		}//END while
		
		//CALL THE REPAINT METHOD
		repaint();
		
	}//END step
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION TO GENERATE A STRING OF 10 ADDRESS REFERENCES
	public void autoGenerateString(){
		int random;

		for (int i = 0; i < 10; i++){

			//RANDOMLY SELECT AN INDEX BETWEEN 0 AND MEMORY SIZE
			random = (int)(Math.random() * memorySize);

			//ADD THE ADDRESS AT THIS INDEX IN ARRAY addresses TO THE LIST OF ADDRESS REFERENCE STRINGS
		//	listData[i] = addresses[random];
			listData.add(addresses[random]);
		}

		//ADD THIS LIST TO THE LISTBOX
		addRefStrList.setListData(listData);
		
		//ENABLE THE Next BUTTON AND DISABLE back BUTTON
//		moveStatus = 0;
		next.setEnabled(true);
		back.setEnabled(false);

		//UPDATE THE PROGRESS FIELD
		tProgress.setText("<html>We have automatically generated an address string of 10 addresses for you to work with."
						  +"<br>Click on \"Next\" to continue.");

	}//END FUNCTION autoGenerateString

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION ALLOWING THE USER TO SELF GENERATE THE ADDRESS REFERENCE STRING
	public void selfGenerateString(){

		int option = 0;
		int index = 0;

		//SET THE VALUES OF THE CUSTOM ARGUMENTS USED IN THE UPCOMING CALL TO THE METHOD JOptionPane.showOptionDialog
		JTextField textfield = new JTextField();
		Object[] array = {"Enter String", textfield};
		Object[] options = {"Continue", "Done"};

		//ALLOW THE USER TO INPUT UPTO 10 ADDRESSES
		while ((option == 0) && index < 10){

			//CALL THE showOptionDialog METHOD USING THE ABOVE CUSTOM ARGUMENTS
			option = JOptionPane.showOptionDialog(this, array, "Self Generate", JOptionPane.YES_NO_OPTION, 
												  JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			//IF THE "CONTINUE" BUTTON WAS CLICKED THEN VALIDATE INPUT 
			if (option == 0){
				//THEN VALIDATE INPUT
				if ((CheckAddress.validateInput(textfield.getText(),memorySize)) || (CheckAddress.validateInput(textfield.getText().toUpperCase(),memorySize))){
					//IF THE INPUT IS VALID, ADD IT TO THE LIST OF ADDRESS REFERENCE STRINGS
				//	listData[index] = textfield.getText().toUpperCase();
					listData.add(textfield.getText().toUpperCase());
					index++;
				}
				else 
					//IF THE INPUT IS INVALID, NOTIFY USER AND PROMPT AGAIN
					JOptionPane.showMessageDialog(this, "Invalid Input. Please try again.", "Invalid Input", 
												  JOptionPane.ERROR_MESSAGE);

				//RESET THE TEXTFIELD
				textfield.setText("<html>");
			}
			//ELSE IF DONE BUTTON WAS CLICKED ON INPUT, VALIDATE INPUT
			else if ((option == 1) && ((!textfield.getText().equals("")))){

				//IF INPUT WAS VALID, ADD TO ADDRESS REFERENCE STRING AND QUIT THE DIALOG BOX
				if ((CheckAddress.validateInput(textfield.getText(),memorySize)) || (CheckAddress.validateInput(textfield.getText().toUpperCase(),memorySize))){
				//	listData[index] = textfield.getText().toUpperCase();
					listData.add(textfield.getText().toUpperCase());
					index++;
					tProgress.setText("<html>You have generated an Address Reference String of "+index+" address."
									  +"<br>Please click on \"Next\" to continue.");

				}
				else{
					//IF INPUT WAS INVALID, NOTIFY USER AND QUIT THE DIALOG BOX
					JOptionPane.showMessageDialog(this, "Invalid Input. Quitting without saving last entry.", 
												  "Invalid Input", JOptionPane.ERROR_MESSAGE);
					tProgress.setText("<html>You have generated an Address Reference String of "+index+" address."
									  +"<br>Please click on \"Next\" to continue.");

				}

			}
		}
		//PUT THE STRING OF INPUTS INTO THE ADDRESS REFERENCE STRING LISTBOX
		addRefStrList.setListData(listData);

		//ENABLE Next BUTTON AND DISABLE back BUTTON ONLY IF AT LEAST ONE VALID ENTRY WAS MADE 
		if (index > 0){
	//		moveStatus = 0;
			next.setEnabled(true);
			back.setEnabled(false);
		}

	}//END FUNCTION selfGenerateString

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//RETURNS THE INDEX OF THE MEMORY BLOCK IN THE CACHE SET, IF IT EXISTS
	//OTHERWISE, A VALUE OF -1 IS RETURNED
	public int getCacheBlock(int numWays, int set, String tag){

			for (int i = 0; i < numWaysPower; i++)
				if (cachePanel.tag2Way[set][i].equals(tag))
					return i;

		return -1;
	}//END FUNCTION getCacheBlock
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//FUNCTION TO RETURN THE INDEX OF THE FIRST AVAILABLE EMPTY BLOCK IN THE SET
	//IF NOT AVAILABLE, THEN A VALUE OF -1 IS RETURNED
	public int getFirstEmptyCacheBlock(int numWays, int set){
			for (int i = 0; i < numWaysPower; i++)
				if (statusCacheEmpty2Way[set][i])
					return i;
		return -1;
	}//END FUNCTION getFirstEmptyCacheBlock
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//FUNCTION TO RETURN THE INDEX OF THE LRU BLOCK IN THE SET	
	public int getLRUCacheBlock(int numWays, int set){
		
		//INTEGER TO HOLD THE VALUE OF THE INDEX WITH THE LEAST LRU VALUE
		int minimum = 0;
			
		//COMPARE THE VALUES OF THE ARRAY statusCacheLRU AND RETURN THE INDEX WITH THE MINIMUM VALUE
			for (int i = 0; i < numWaysPower; i++){
				if (statusCacheLRU2Way[set][minimum] > statusCacheLRU2Way[set][i])
					minimum = i;
			}
		
		return minimum;
		
	}//END FUNCTION getLRUCacheBlock
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//FUNCTION TO CLOSE THIS FRAME
	public void removeInstance(){
		this.dispose();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}//END CLASS SACFrame
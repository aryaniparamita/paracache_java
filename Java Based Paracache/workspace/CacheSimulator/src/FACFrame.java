/*
 * FILE: FACFrame.java
 * AUTHOR: Karishma Rao
 * DATE: February 16th, 2003
 */

/**
 * DESCRIPTION: Implements the interactive tutorial to demonstrate the working of Fully Associative Cache Mapping.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;

//CLASS FACFrame IS THE MAIN FRAME OF THE APPLICATION
class FACFrame extends JFrame {

	//DECLARE ALL THE COMPONENTS

	//PANEL FOR DRAWING THE CACHE
	private CachePanel cachePanel;  

	//PANEL FOR DRAWING THE MEMORY       
	private MemoryPanel memoryPanel;

	//PANELS TO CONTAIN THE PROGRESS BAR, NAVIGATION BUTTONS, CACHE AND CACHE HITS AND MISSES FIELDS
	private JPanel progressButtonPanel, cache, cacheHitsMisses;

	//PANELS TO CONTAIN THE ADDRESS REFERENCE STRING AND MAIN MEMORY ADDRESS BITS
	private JPanel pStringListMM, pAddRefStr, pAutoSelfGen, pBitsInMM;

	//NAVIGATION BUTTONS AND ADDRESS REFERENCE STRING GENERATION BUTTONS
	private JButton restart, next, back, quit, autoGen, selfGen;

	//LABELS FOR THE VARIOUS COMPONENTS
	private JLabel lCacheHits, lCacheMisses, lProgress, lBits, tProgress;

	//TEXTFIELDS FOR CACHE HITS AND MISSES AND MAIN MEMORY ADDRESS BITS
	private JTextField tCacheHits, tCacheMisses, tTag, tWord;

	//TEXTAREA FOR PROGRESS UPDATE
	//private JTextArea tProgress;

	//SCROLLPANES FOR PROGRESS UPDATE TEXTAREA AND ADDRESS REFERENCE STRING LISTBOX
	private JScrollPane progressScroll, addRefStrScroll,memoryPanelScroll;

	//LISTBOX FOR ADDRESS REFERENCE STRING
	private JList addRefStrList;

	//BORDERS FOR THE VARIOUS PANELS
	private Border cacheHMBorder, bitsInMMBorder, addRefStrBorder;

	//TEMP ARRAY USED TO GENERATE THE STRING OF 256 POSSSIBLE MEMORY ADDRESSES
	//String[] tempAddress = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

	//ARRAY TO HOLD THE 256 POSSIBLE MEMORY ADDRESSES
	private String[] addresses = new String[Constant.MAXSIZE];

	//vECTOR USED TO MAINTAIN THE ADDRESSES IN THE REFERENCE STRING LIST
	//	private String[] listData = new String[10];
	private Vector listData = new Vector();

	//INT USED TO TRACK STEPPING THROUGH THE TUTORIAL (WHEN THE Next AND Back BUTTONS ARE USED)
	private int moveStatus = 0;

	//INT USED TO INDICATE THE ADDRESS REFERENCE STRING IN QUESTION
	private int evaluateIndex = 0;

	//INTS TO KEEP COUNT OF THE NUMBER OF CACHE HITS AND MISSES OCCURRING DURING ONE RUNTHROUGH
	private int cacheHits, cacheMisses;

	//INT USED TO KEEP TRACK OF LEAST RECENTLY USED CACHE BLOCK - INCREMENTED AT EACH ACCESS TO CACHE
	private int statusLRU = 0;

	//INT ARRAY USED TO KEEP TRACK OF LEAST RECENTLY USED CACHE BLOCK - EACH INDEX REPRESENTS A CACHE BLOCK
	private int[] statusCacheLRU = new int[Constant.MAXSIZE];

	//BOOLEAN ARRAY USED TO KEEP TRACK OF FIRST AVAILABLE EMPTY CACHE BLOCK - EACH INDEX REPRESENTS A CACHE BLOCK
	private boolean[] statusCacheEmpty = new boolean[Constant.MAXSIZE];

	//INT USED TO KEEP TRACK OF WHICH CACHE BLOCK A PARTICULAR MEMORY BLOCK IS IN
	private int memInCache = -1;

	//INTS USED TO KEEP TRACK OF THE FIRST AVAILABLE AND LEAST RECENTLY USED CACHE BLOCKS 
	int emptyCacheBlock = -1;
	int lruCacheBlock = -1;

	//STRINGS USED TO CONVERT THE DECIMAL MEMORY ADDRESS VALUES INTO HEX AND BINARY
	private String hexAddress = new String();
	private String binAddress = new String();

	//INTS USED TO MAINTAIN THE DECIMAL VALUE OF THE MEMORY ADDRESS IN QUESTION - IN MAIN MEMORY AS WELL AS IN CACHE
	private int intBlockDec = 0;
	private int intWordDec = 0;
	private int intBlockDecMem = 0;

	//STRINGS USED TO MAINTAIN THE MAIN MEMORY ADDRESS BITS
	private String tag = new String();
	private String word = new String();
	private String blockMem = new String();

	//STRINGS USED FOR CONVERSION OF ADDRESSES FROM DECIMAL - HEX - BIN
	private String blockDec = new String();
	private String wordDec = new String();

	//BOOLEAN USED TO KEEP TRACK OF WHETHER THE RESTART BUTTON WAS PRESSED - USED TO ENABLE Next BUTTON APPROPRIATELY
	private boolean reStarted = true;

	//BOOLEAN USED TO INDICATE WHETHER THE Next BUTTON WAS CLICKED OR NOT
	private boolean nextClicked = true;

	//INTEGER USED TO VARY CACHE AND MEMORY SIZE
	int cacheSize=10;
	int blockSize=10;
	int wordSize=10;
	int memorySize=10;

	int tagDigit=1;
	int blockDigit=1;
	int wordDigit=3;
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//CONSTRUCTOR
	public FACFrame(int memorySizeBit, int wordSizeBit){
		this.blockSize=(int)(Math.pow(2, memorySizeBit-wordSizeBit));
		this.memorySize=(int)(Math.pow(2, memorySizeBit));
		this.wordSize=(int)(Math.pow(2, wordSizeBit));

		//SET PROPERTIES OF THE MAIN FRAME
		setTitle("Fully Associative Cache");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().createImage(FACFrame.class.getResource("camera.jpg")));

		//Determine Tag, Block, and Words
		wordDigit = wordSizeBit;
		tagDigit=memorySizeBit -wordDigit;

		//CREATE COMPONENTS AND SET THEIR PROPERTIES

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
		lCacheHits = new JLabel("Cache Hits");
		lCacheMisses = new JLabel("Cache Misses");	
		tCacheHits = new JTextField(5);
		tCacheMisses = new JTextField(5);
		tCacheHits.setEditable(false);
		tCacheHits.setFont(new Font("Monospaced", Font.BOLD, 14));
		tCacheHits.setText("  0");
		tCacheMisses.setEditable(false);
		tCacheMisses.setFont(new Font("Monospaced", Font.BOLD, 14));
		tCacheMisses.setText("  0");

		tProgress = new JLabel("",SwingConstants.CENTER);
		tProgress.setOpaque(true);
		tProgress.setBackground(new Color(254,254,254));
		tProgress.setBorder(null);


		String all="";
		for (int i=0;i< blockSize;i++){
			addresses[i]=Integer.toHexString(i).toUpperCase();
			all+=addresses[i];
		}
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
		addRefStrScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		addRefStrScroll.setPreferredSize(new Dimension(140, 300));

		//BUTTONS USED TO ADDRESS GENERATION
		autoGen = new JButton("<html>Auto<br>Generate</html>");
		autoGen.setOpaque(true);
		autoGen.setBackground(new Color(250,250,200));
		selfGen = new JButton("<html>Self<br>Generate</html>");
		selfGen.setOpaque(true);
		selfGen.setBackground(new Color(250,250,200));







		//BUTTONS USED TO ADDRESS GENERATION
		autoGen = new JButton("<html>Auto<br>Generate</html>");
		autoGen.setOpaque(true);
		autoGen.setBackground(new Color(250,250,200));
		selfGen = new JButton("<html>Self<br>Generate</html>");
		selfGen.setOpaque(true);
		selfGen.setBackground(new Color(250,250,200));

		//BITS IN MAIN MEMORY ADDRESS
		lBits = new JLabel("                   TAG                        WORD");

		tTag = new JTextField(9);
		tTag.setEditable(false);
		tWord = new JTextField(7);
		tWord.setEditable(false);

		//SET THE FONT STYLES FOR THE BITS IN MAIN MEMORY ADDRESS
		tTag.setFont(new Font("Monospaced", Font.BOLD, 14));
		tWord.setFont(new Font("Monospaced", Font.BOLD, 14));

		//REGISTER LISTENERS	
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
		cachePanel = new CachePanel(cacheSize, wordSize,tagDigit){
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
		pAutoSelfGen = new JPanel();
		pAddRefStr = new JPanel();
		pStringListMM = new JPanel();
		pBitsInMM = new JPanel();




		//PANEL WITH ADDRESS REFERENCE STRING AND STRING GENERATION BUTTONS
		pAutoSelfGen.setLayout(new GridLayout(1, 2));
		pAutoSelfGen.add(autoGen);
		pAutoSelfGen.add(selfGen);

		pAddRefStr.setLayout(new BorderLayout());
		pAddRefStr.setPreferredSize(new Dimension(160, 400));

		pAddRefStr.add(addRefStrScroll, "Center");
		//pAddRefStr.add(pAutoSelfGen, "South");
		addRefStrBorder= BorderFactory.createEtchedBorder();
		pAddRefStr.setBorder(BorderFactory.createTitledBorder(addRefStrBorder, " Address Reference String ")); 



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



		//ADD COMPONENTS TO THE PANELS

		//PANEL WITH PROGRESS UPDATE TEXT AREA AND NAVIGATION BUTTONS
		progressButtonPanel.setLayout(new BorderLayout());
		//PANEL WITH PROGRESS UPDATE TEXT AREA AND NAVIGATION BUTTONS
		//progressButtonPanel.add(pBitsInMM,"West");
		//lProgress.setPreferredSize(new Dimension(210,80));
		//progressButtonPanel.add(lProgress,"West");	

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

		cacheHitsMisses.add(lCacheHits);
		cacheHitsMisses.add(tCacheHits);
		cacheHitsMisses.add(lCacheMisses);
		cacheHitsMisses.add(tCacheMisses);
		cacheHMBorder= BorderFactory.createEtchedBorder();
		cacheHitsMisses.setBorder(BorderFactory.createTitledBorder(cacheHMBorder, "")); 

		cache.setLayout(new BorderLayout());
		cache.add(cachePanel, "Center");
		JScrollPane cachePanelScroll = new JScrollPane(cachePanel);
		cachePanelScroll.setPreferredSize(new Dimension(250,500));
		cachePanelScroll.setVerticalScrollBarPolicy(cachePanelScroll.VERTICAL_SCROLLBAR_AS_NEEDED);
		cachePanelScroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		cache.add(cacheHitsMisses, "North");
		cache.add(cachePanelScroll);



		//PANEL WITH ADDRESS REFERENCE STRING AND STRING GENERATION BUTTONS
		pAutoSelfGen.setLayout(new GridLayout(1, 2));
		pAutoSelfGen.add(autoGen);
		pAutoSelfGen.add(selfGen);
		pAutoSelfGen.setPreferredSize(new Dimension(210,80));
		progressButtonPanel.add(pAutoSelfGen,"West");
		pAddRefStr.setLayout(new BorderLayout());
		pAddRefStr.setPreferredSize(new Dimension(160, 400));

		pAddRefStr.add(addRefStrScroll, "Center");
		//pAddRefStr.add(pAutoSelfGen, "South");
		addRefStrBorder= BorderFactory.createEtchedBorder();
		pAddRefStr.setBorder(BorderFactory.createTitledBorder(addRefStrBorder, " Address Reference String ")); 

		//PANEL WITH THE MAIN MEMORY ADDRESS BITS INFO.
		pBitsInMM.setLayout(new BorderLayout());
		bitsInMMBorder= BorderFactory.createEtchedBorder();
		pBitsInMM.setBorder(BorderFactory.createTitledBorder(bitsInMMBorder, " Main Memory Address ")); 
		pBitsInMM.add(tTag, "Center");
		pBitsInMM.add(tWord, "East");	
		pBitsInMM.add(lBits, "South");

		//PANEL CONTAINING THE ADDRESS REF. STRING PANEL AND BITS IN MM PANEL
		pStringListMM.setLayout(new BorderLayout());
		pStringListMM.setPreferredSize(new Dimension(210, 600));
		pStringListMM.add(pAddRefStr, "Center");
		pStringListMM.add(pBitsInMM, "North");






		//INITIALIZE ARRAYS THAT HOLDS STATUS OF EMPTY AND LRU CACHE BLOCKS
		for (int i = 0; i < cacheSize; i++){
			statusCacheEmpty[i] = true;
			statusCacheLRU[i] = 0;
		}

		/*
		 * CALL THE FUNCTION TO GENERATE THE ARRAY addresses, WHICH CONTAINS ALL THE POSSIBLE MEMORY ADDRESSES
		 * THIS ARRAY WILL BE USEFUL IN THE AUTO GENERATION OF ADDRESSES AS WELL AS FOR VALIDATION OF 
		 * ADDRESS STRINGS INPUT BY THE USER IF HE/SHE CHOOSES SELF GENERATION.
		 */
		addresses = CheckAddress.createAddresses(memorySize);

		pack();
	}//END CONSTRUCTOR

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * FUNCTION TO RESTART THE APPLICATION
	 * BRINGS THE FRAME TO ITS ORIGINAL STARTING CONFIGURATION
	 */
	public void reStart(){

		//UNDO THE HIGHLIGHTS, IF ANY, OF THE PREVIOUS STEPS

		//UPDATE THE STATE OF THE CACHE AND MEMORY
		for (int i = 0; i < cacheSize; i++){
			cachePanel.stringBlocks[i] = "";
			cachePanel.boolBlocks[i] = false;
			cachePanel.tag[i] = "";
			cachePanel.boolTags[i] = false;

			statusCacheEmpty[i] = true;
			statusCacheLRU[i] = 0;
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
		tCacheHits.setText("  0");
		tCacheMisses.setText("  0");

		//UPDATE THE VALUES USED FOR BRINGING MEMORY BLOCKS IN CACHE
		statusLRU = 0;
		memInCache = -1;
		lruCacheBlock = -1;

		//REFRESH THE ADDRESS REFERENCE STRING TO NULL
		listData.removeAllElements();
		addRefStrList.setListData(listData);

		//UPDATE THE BITS IN MAIN MEMORY ADDRESS
		tTag.setText("");
		tWord.setText("");
		tTag.setBackground(new Color(205, 205, 205));
		tWord.setBackground(new Color(205, 205, 205));

		//UPDATE THE PROGRESS FIELD 
		tProgress.setText("<html> Let's start over. <br>Please generate the Address Reference String.");

		//DISABLE THE NEXT AND BACK BUTTONS
		next.setEnabled(false);
		back.setEnabled(false);

		//ENABLE ADDRESS GENERATION BUTTONS
		autoGen.setEnabled(true);
		selfGen.setEnabled(true);

		//RESET THE VALUES OF moveStatus AND addSel AND reStarted
		moveStatus = 0;
		evaluateIndex = 0;
		reStarted = true;

		//CALL THE REPAINT METHOD
		repaint();

	}//END FUNCTION reStart

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION TO SIMULATE A STEP IN THE SIMULATION ON THE FRAME
	public void step(){

		/** 
		 * EACH TIME THE NEXT BUTTON IS PRESSED, ALL THE STATES THAT OCURRED UPTO THE CURRENT STATE ARE EVALUATED.
		 * THERE IS A while STATEMENT THAT PERFORMS THIS FUNCTION AND CONTAINS A switch STATEMENT WITHIN 
		 * IT TO EVALUATE EACH STEP AS IT OCCURS.  
		 */

		////////////////////// INITIALIZATION ///////////////////////////////////////

		//UPDATE THE STATE OF THE CACHE AND MEMORY
		for (int i = 0; i < cacheSize; i++){
			cachePanel.stringBlocks[i] = "";
			cachePanel.boolBlocks[i] = false;
			cachePanel.tag[i] = "";
			cachePanel.boolTags[i] = false;

			statusCacheEmpty[i] = true;
			statusCacheLRU[i] = 0;
		}
		for (int i = 0; i < wordSize; i++){
			cachePanel.boolWords[i] = false;
			memoryPanel.boolWords[i] = false;
		}
		for (int i = 0; i < blockSize; i++)
			memoryPanel.boolBlocks[i] = false;

		//UPDATE THE BITS IN MAIN MEMORY ADDRESS
		tTag.setText("");
		tWord.setText("");
		tTag.setBackground(new Color(205, 205, 205));
		tWord.setBackground(new Color(205, 205, 205));

		//UPDATE THE CACHE HITS AND MISSES FIELDS
		cacheHits = 0;
		cacheMisses = 0;
		tCacheHits.setText("  0");
		tCacheMisses.setText("  0");

		//UPDATE THE VALUES USED FOR BRINGING MEMORY BLOCKS IN CACHE
		statusLRU = 0;
		memInCache = -1;
		lruCacheBlock = -1;

		//RESET THE VALUE OF addSel
		evaluateIndex = 0;

		//DISABLE ADDRESS GENERATION BUTTONS
		autoGen.setEnabled(false);
		selfGen.setEnabled(false);

		////////////////////// END INITIALIZATION /////////////////////////////////////

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
			//tProgress.setCaretPosition(0);

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

			case 1:	//IF A NEW CYCLE IS BEGINNING, OBTAIN NEXT ADDRESS REFERENCE

				//OBTAIN THE ADDRESS REFERENCE STRING
				addRefStrList.setSelectedIndex(evaluateIndex);

				//ENSURE THAT THE LIST SCROLLS AND SELECTED INDEX IS VISIBLE
				//DUE TO REPAINTING CONSTRAINTS, ONLY DO THIS IN THE CURRENT STATE
				if (tempState == moveStatus)
					addRefStrList.ensureIndexIsVisible(evaluateIndex);

				//EVALUATE THE TAG, BLOCK AND WORD
				hexAddress = (String)addRefStrList.getSelectedValue();
				int intAddress = Integer.parseInt(hexAddress, 16);
				binAddress = Integer.toBinaryString(intAddress);

				//USING CLASS INTEGER'S parseInt FUNCTION RETURNS A BINARY STRING WITHOUT LEADING 0'S
				//ENSURE THAT binAddress is 8 bits
				if (binAddress.length() <tagDigit+wordDigit){
					int zeroes = tagDigit+wordDigit - binAddress.length();
					for (int i = 0; i < zeroes; i++)
						binAddress = '0'+binAddress;
				}

				tag = binAddress.substring(0, tagDigit);
				word = binAddress.substring(tagDigit);

				//CALCULATE THE ACTUAL CACHE AND MEMORY BLOCKS AND WORDS IN QUESTION
				intWordDec = Integer.parseInt(word, 2);
				intBlockDecMem = Integer.parseInt(tag, 2);

				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html>The memory address we want is obtained from the Address Reference String."
							+"<br>It is (in hexadecimal): " + hexAddress+".");
					//tProgress.setCaretPosition(0);
				}

				break;

			case 2:	//EVALUATE THE BITS IN MAIN MEMORY ADDRESS AND HIGHLIGHT THEM 		

				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html> The hexadecimal address " + hexAddress 
							+ " evaluates to its binary equivalent " + binAddress+"."
							+ "<br>Hence the bits in the Main Memory Address are divided into"
							+ tag + " --> Tag,  " + word + " --> Word."
							+ "<br>The tag bits identify the memory block, "
							+ "and the word bits identify the word within the block.");
					//tProgress.setCaretPosition(0);

					//HIGHLIGHT THE BITS IN MAIN MEMORY ADDRESS IN GREEN
					tTag.setBackground(Color.green);
					tWord.setBackground(Color.green);
				}

				tTag.setText("      "+tag);				
				tWord.setText("  "+word);

				break;

			case 3:	//FIND THE CACHE BLOCK IN QUESTION AND HIGHLIGHT IT		

				//UNDO HIGHLIGHTS OF PREVIOUS STEP
				tTag.setBackground(new Color(205, 205, 205));
				tWord.setBackground(new Color(205, 205, 205));

				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("<html>Every time a memory block is placed in cache, its tag field is stored with it as well."
							+"<br>So, to find the required memory block in cache, its tag, "+tag
							+" is compared to all the valid tag fields in cache.");
					//tProgress.setCaretPosition(0);
				}

				//GET THE BLOCK IN CACHE WHERE MEMORY BLOCK EXISTS, IF AT ALL
				memInCache = getCacheBlock(tag);

				//IF MEMORY BLOCK IS NOT IN CACHE...
				if (memInCache == -1){
					if (tempState == moveStatus){
						String next =  tProgress.getText() + "<html><br>Since the memory block is not in cache, there is a cache miss."
								+"<br>So the block needs to be brought in from memory.";
						tProgress.setText(next);
						//tProgress.setCaretPosition(0);
					}

					//GET FIRST EMPTY CACHE BLOCK, IF AVAILABLE
					emptyCacheBlock = getFirstEmptyCacheBlock();

					//IF EMPTY CACHE BLOCK IS AVAILABLE, THIS IS WHERE THE MEMORY WILL BE BROUGHT SO DISPLAY IT
					if (!(emptyCacheBlock == -1)){

						//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
						if (tempState == moveStatus){
							String next = tProgress.getText()+"<html><br>Since the cache has empty space, the first available block will be filled."
									+"<br>See the highlighted cache block.";
							tProgress.setText(next);
							//tProgress.setCaretPosition(0);
						}

						//HIGHLIGHT THE CACHE BLOCK IN YELLOW
						cachePanel.boolBlocks[emptyCacheBlock] = true;
						cachePanel.boolTags[emptyCacheBlock] = true;

						//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
						intBlockDec = emptyCacheBlock;
					}

					//ELSE DISPLAY THE LRU CACHE BLOCK WHICH WILL BE REPLACED
					else {

						//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
						if (tempState == moveStatus){
							String next = tProgress.getText() + "<br>Since the cache is full, the least recently used cache block will be replaced."
									+"<br>See the highlighted cache block.";
							tProgress.setText(next);
							//tProgress.setCaretPosition(0);
						}

						lruCacheBlock = getLRUCacheBlock();

						//HIGHLIGHT THE CACHE BLOCK IN YELLOW
						cachePanel.boolBlocks[lruCacheBlock] = true;
						cachePanel.boolTags[lruCacheBlock] = true;

						//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
						intBlockDec = lruCacheBlock;
					}

					//UPDATE COUNT OF CACHE MISSES
					cacheMisses++;
					tCacheMisses.setText("  "+cacheMisses);
				}
				else{

					if (tempState == moveStatus){

						tProgress.setText(tProgress.getText() + "<br>Since the required memory block is in cache block "
								+memInCache+" there is a cache hit.");
						//tProgress.setCaretPosition(0);
					}

					//HIGHLIGHT THE CACHE BLOCK IN YELLOW
					//TO CAUSE HIGHLIGHTING ON THE CACHE, WE NEED TO MODIFY IT'S STATE, i.e. IT'S DATA MEMBERS
					cachePanel.boolBlocks[memInCache] = true;
					cachePanel.boolWords[intWordDec] = true;
					cachePanel.boolTags[memInCache] = true;

					//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
					intBlockDec = memInCache;

					//UPDATE COUNT OF CACHE HITS
					cacheHits++;
					tCacheHits.setText("  "+cacheHits);
				}

				break;

			case 4:	//EVALUATE THE MEMORY BLOCK IN QUESTION AND HIGHLIGHT IT

				//UNDO THE HIGHLIGHTS OF THE PREVIOUS STEP
				cachePanel.boolBlocks[intBlockDec] = false;
				cachePanel.boolWords[intWordDec] = false;
				cachePanel.boolTags[intBlockDec] = false;

				//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
				if (tempState == moveStatus){
					tProgress.setText("Highlighted is the memory block in question. Since the tag bits are "+tag
							+", the memory block, in decimal, is "+intBlockDecMem+".");
					//tProgress.setCaretPosition(0);
				}

				//SET THE MEMORY STATE SO AS TO HIGHLIGHT THE REQUIRED MEMORY BLOCK
				memoryPanel.boolBlocks[intBlockDecMem] = true;

				//SET THE MEMORY STATE SO AS TO HIGHLIGHT THE REQUIRED WORD
				memoryPanel.boolWords[intWordDec] = true;

				break;

			case 5:	//HIGHLIGHT THE CACHE BLOCK WITH THE MEMORY BLOCK NOW IN IT

				//UNDO HIGHLIGHTS OF PREVIOUS STEP
				memoryPanel.boolBlocks[intBlockDecMem] = false;
				memoryPanel.boolWords[intWordDec] = false;

				/*
				 * NOW, THERE ARE 3 WAYS TO GO FROM HERE
				 * 1. IF THERE IS AN EMPTY CACHE BLOCK, SIMPLY BRING THE MEMORY BLOCK INTO CACHE
				 * 2. IF THE REQUIRED MEMORY BLOCK IS ALREADY IN CACHE, DO NOTHING
				 * 3. IF THE CACHE IS FULL, FIND THE LRU BLOCK AND REPLACE IT WITH THE REQUIRED MEMORY BLOCK
				 */

				//IF THE MEMORY BLOCK WAS NOT IN CACHE AND AN EMPTY CACHE BLOCK IS AVAILABLE
				//BRING THE MEMORY BLOCK AND TAG INTO CACHE AND HIGHLIGHT CACHE BLOCK
				if ((memInCache == -1) && !(emptyCacheBlock == -1)){

					//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
					if (tempState == moveStatus){
						tProgress.setText("<html> As we saw earlier, the required memory block was not in cache."
								+"<br> Since there was empty space in cache, we brought the memory block into it."
								+"<br> We also stored the tag, "+tag+", of the memory block with the cache block."
								+"<br> <br>Remember that the memory block could be brought into any empty cache block."
								+"<br> In our example, we are using the first available empty block.");

					}

					//UPDATE THE COUNTER FOR THE LRU CACHE BLOCK
					statusLRU++;
					statusCacheLRU[emptyCacheBlock] = statusLRU;
					statusCacheEmpty[emptyCacheBlock] = false;

					//UPDATE THE CACHE ARRAYS KEEPING TRACK OF MEMORY BLOCKS AND TAGS
					cachePanel.stringBlocks[emptyCacheBlock] = ""+intBlockDecMem;
					cachePanel.tag[emptyCacheBlock] = tag;

					//HIGHLIGHT THE CACHE BLOCK IN YELLOW
					cachePanel.boolBlocks[emptyCacheBlock] = true;
					cachePanel.boolWords[intWordDec] = true;
					cachePanel.boolTags[emptyCacheBlock] = true;

					//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
					intBlockDec = emptyCacheBlock;

				}//END IF

				//IF MEMORY BLOCK IS ALREADY IN CACHE THEN JUST HIGHLIGHT THE CACHE BLOCK
				else if ((memInCache >= 0) && (memInCache < cacheSize)){

					//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
					if (tempState == moveStatus){
						tProgress.setText("As we saw earlier, the required memory block is already in cache.");

					}

					//UPDATE THE COUNTER FOR THE LRU CACHE BLOCK
					statusLRU++;
					statusCacheLRU[memInCache] = statusLRU;

					//HIGHLIGHT THE CACHE BLOCK IN YELLOW
					cachePanel.boolBlocks[memInCache] = true;
					cachePanel.boolWords[intWordDec] = true;
					cachePanel.boolTags[memInCache] = true;

					//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
					intBlockDec = memInCache;

				}//END ELSE IF

				//IF THE MEMORY BLOCK IS NOT IN CACHE AND THE CACHE IS FULL
				//FIND THE LRU CACHE BLOCK AND REPLACE IT WITH THE MEMORY BLOCK, THEN HIGHLIGHT THE CACHE BLOCK
				else {

					//FIND THE LRU CACHE BLOCK
					lruCacheBlock = getLRUCacheBlock();

					//IF THIS IS THE CURRENT STATE, UPDATE THE PROGRESS FIELD
					if (tempState == moveStatus){
						tProgress.setText("<html>As we saw earlier, the cache is full."
								+"<br>So we picked the least recently used cache block, "+ lruCacheBlock
								+",<br> and replaced it with the required memory block.");

					}

					//UPDATE THE COUNTER FOR THE LRU CACHE BLOCK
					statusLRU++;
					statusCacheLRU[lruCacheBlock] = statusLRU;
					statusCacheEmpty[lruCacheBlock] = false; //redundant stmt

					//UPDATE THE CACHE ARRAYS KEEPING TRACK OF MEMORY BLOCKS AND TAGS
					cachePanel.stringBlocks[lruCacheBlock] = ""+intBlockDecMem;
					cachePanel.tag[lruCacheBlock] = tag;

					//HIGHLIGHT THE CACHE BLOCK IN YELLOW
					cachePanel.boolBlocks[lruCacheBlock] = true;
					cachePanel.boolWords[intWordDec] = true;
					cachePanel.boolTags[lruCacheBlock] = true;

					//STORE THE CHANGED CACHE BLOCK INDEX IN COMMON VARIABLE
					intBlockDec = lruCacheBlock;

				}//END ELSE

				break;

			case 0:	//LAST STEP IN CYCLE - CLEANUP STEP!

				//UNDO HIGHLIGHTS OF PREVIOUS STEP
				cachePanel.boolBlocks[intBlockDec] = false;
				cachePanel.boolWords[intWordDec] = false;
				cachePanel.boolTags[intBlockDec] = false;

				tTag.setText("");
				tWord.setText("");

				tTag.setBackground(new Color(205, 205, 205));
				tWord.setBackground(new Color(205, 205, 205));

				//CLEAR THE SELECTED ADDRESS REFERENCE STRING
				addRefStrList.clearSelection();

				//INCREMENT THE INDEX SO AS TO POINT TO THE NEXT ADDRESS REFERENCE STRING
				evaluateIndex++;

				//IF THE LAST ADDRESS REFERENCE STRING HAS BEEN REACHED, DO THE APPROPRIATE
				if (evaluateIndex == listData.size()){

					if (tempState == moveStatus){
						tProgress.setText("<html>This completes the runthrough."
								+"<br>Please click on \"Restart\", generate the Address Reference String "
								+"<br>OR click \"Quit\" to finish.");

					}
					next.setEnabled(false);

					//ENABLE ADDRESS GENERATION BUTTONS 
					autoGen.setEnabled(true);
					selfGen.setEnabled(true);

					reStarted = false;
				}

				//ELSE AN ACCESS CYCLE HAS BEEN COMPLETED SO SHOW THE APPROPRIATE MESSAGE IN THE PROGRESS FIELD
				else{
					if(tempState == moveStatus){
						tProgress.setText("This completes an access cycle.");

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

	}//END FUNCTION step

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * FUNCTION TO CREATE ARRAY addresses
	 * THIS ARRAY IS USED FOR AUTO GENERATION OF ADDRESSES AND FOR VALIDATION WHEN THE USER CHOOSES SELF GENERATION
	 */

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * FUNCTION TO CREATE ARRAY addresses
	 * THIS ARRAY IS USED FOR AUTO GENERATION OF ADDRESSES AND FOR VALIDATION WHEN THE USER CHOOSES SELF GENERATION
	 */


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION TO GENERATE A STRING OF 10 ADDRESS REFERENCES
	public void autoGenerateString(){
		int random;

		for (int i = 0; i < 10; i++){

			//RANDOMLY SELECT AN INDEX BETWEEN 0 AND MEMORY SIZE
			random = (int)(Math.random() * memorySize);

			//ADD THE ADDRESS AT THIS INDEX IN ARRAY addresses TO THE LIST OF ADDRESS REFERENCE STRINGS
			listData.add(addresses[random]);
		}

		//ADD THIS LIST TO THE LISTBOX
		addRefStrList.setListData(listData);

		//ENABLE THE Next BUTTON AND DISABLE back BUTTON
		next.setEnabled(true);
		back.setEnabled(false);

		//UPDATE THE PROGRESS FIELD
		tProgress.setText("We have automatically generated an address string of 10 addresses for you to work with.");

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
				if ((CheckAddress.validateInput(textfield.getText(),memorySize)) ||
						(CheckAddress.validateInput(textfield.getText().toUpperCase(),memorySize))){
					//IF THE INPUT IS VALID, ADD IT TO THE LIST OF ADDRESS REFERENCE STRINGS
					index++;
					listData.add(textfield.getText().toUpperCase());
				}
				else 
					//IF THE INPUT IS INVALID, NOTIFY USER AND PROMPT AGAIN
					JOptionPane.showMessageDialog(this, "Invalid Input. Please try again.", "Invalid Input", 
							JOptionPane.ERROR_MESSAGE);

				//RESET THE TEXTFIELD
				textfield.setText("");
			}
			//ELSE IF DONE BUTTON WAS CLICKED ON INPUT, VALIDATE INPUT
			else if ((option == 1) && ((!textfield.getText().equals("")))){

				//IF INPUT WAS VALID, ADD TO ADDRESS REFERENCE STRING AND QUIT THE DIALOG BOX
				if ((CheckAddress.validateInput(textfield.getText(),memorySize)) || 
						(CheckAddress.validateInput(textfield.getText().toUpperCase(),memorySize))){
					index++;
					listData.add(textfield.getText().toUpperCase());
					tProgress.setText("You have generated an Address Reference String of "+index+" address.");

				}
				else{
					//IF INPUT WAS INVALID, NOTIFY USER AND QUIT THE DIALOG BOX
					JOptionPane.showMessageDialog(this, "Invalid Input. Quitting without saving last entry.", 
							"Invalid Input", JOptionPane.ERROR_MESSAGE);
					tProgress.setText("You have generated an Address Reference String of "+index+" address.");

				}

			}
		}
		//PUT THE STRING OF INPUTS INTO THE ADDRESS REFERENCE STRING LISTBOX
		addRefStrList.setListData(listData);

		//ENABLE Next BUTTON AND DISABLE back BUTTON ONLY IF AT LEAST ONE VALID ENTRY WAS MADE 
		if (index > 0){
			next.setEnabled(true);
			back.setEnabled(false);
		}

	}//END FUNCTION selfGenerateString

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////





	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION TO RETURN THE INDEX OF THE CACHE BLOCK A PARTICULAR MEMORY BLOCK IS IN
	//THE MEMORY BLOCK'S TAG IS COMPARED TO ALL TAGS IN CACHE AND IF THERE IS NO MATCH, A VALUE OF -1 IS RETURNED
	public int getCacheBlock(String memTag){

		for (int i = 0; i < cacheSize; i++){
			if (memTag.equals(cachePanel.tag[i]))
				return i;
		}
		return -1;	

	}//END FUNCTION getCacheBlock

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION TO RETURN THE INDEX OF THE FIRST EMPTY CACHE BLOCK, IF THERE IS ONE
	//OTHERWISE A VALUE OF -1 IS RETURNED

	public int getFirstEmptyCacheBlock(){

		for (int i = 0; i < cacheSize; i++){
			if (statusCacheEmpty[i] == true)
				return i;
		}
		return -1;

	}//END FUNCTION getFirstEmptyCacheBlock

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//FUNCTION TO RETURN THE INDEX OF THE LEAST RECENTLY USED CACHE BLOCK

	public int getLRUCacheBlock(){

		//INTEGER TO HOLD THE VALUE OF THE INDEX WITH THE LEAST LRU VALUE
		int minimum = 0;

		//COMPARE THE VALUES OF THE ARRAY statusCacheLRU AND RETURN THE INDEX WITH THE MINIMUM VALUE
		for (int i = 1; i < cacheSize; i++){
			if (statusCacheLRU[minimum] > statusCacheLRU[i])
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


}//END CLASS FACFrame

/*********************************************************************************************************************/




/**
 * FILE:	CachePanel.java
 * AUTHOR:	Karishma Rao
 * DATE:	February 16th, 2003
 */

/**
 * This class does not implement previous state storage of the cache blocks.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

//CLASS FOR THE CACHE PANEL
class CheckAddress extends JPanel {
	private static String[] addresses = new String[Constant.MAXSIZE];
	
	public static String[] createAddresses(int memorySize){


		for (int i=0;i< memorySize;i++){
			addresses[i]=Integer.toHexString(i).toUpperCase();
		}
		return addresses;
	}
	public static boolean validateInput(String test, int memorySize){
		for (int i = 0; i < memorySize; i++){

			//CHECK IF INPUT VALUE EXISTS IN ARRAY addresses - RETURN TRUE OR FALSE ACCORDINGLY
			if (addresses[i].equals(test))
				return true;
		}
		return false;
	}
	//END FUNCTION validateInput
	
	
}
package com.gabbytt.soundex.interfaces;

import java.util.regex.Pattern;


public interface IRule {
	public String getSoundExCode();		//The rule this will encode (not 1-to-1 relationship) many rules may encode 'M0' 
	public Pattern getPattern(); 	    //the java regular expression used for matching
	public String getPatternString();   // string that built the pattern (mostly for debugging)
	public String getRuleDescription();  // Describes the rule's purpose (for readability and debugging)
	public String getRuleName(); 		 // Describes the rule's purpose (for readability and debugging)
	public int getPrecedenceIndex();	// Order that rules are applied are significant.  From greatest precedence to least (100->0) 
										// precedence less that 0 are ignored (though some rules encoded 'ignore' that have greater precedence )
	public int compareTo(IRule o) ;		// sort by precedence
	public Boolean ignore();  //this soundbyte is not a factor of the soundEx expression 

	//public SoundByte encode();  
}

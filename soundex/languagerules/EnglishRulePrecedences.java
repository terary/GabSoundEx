package com.gabbytt.soundex.languagerules;
//Precedence inwhich rules are applied is significant.
//Match/encode larger character patterns before smaller
//Match/encode common prefix/suffix before root word
//
//Larger precedence is applied before lower  				*tricky*
//100->0
//

public enum EnglishRulePrecedences  {
	SPACE(121),
	PRFIX_SUFFIX_THREE_CHARS(120),
	PRFIX_SUFFIX_TWO_CHARS(110),
	FIRST_PASS(100),
	SILENT(99),
	FOUR_CHARS(70), //vaca{tion]
	SCH_SPECIAL(60), //School Schwank Schedule
	SCH_FINAL(50), //schumer,  
	THREE_CHARS(40),
	xH(30), // ph/sh/th  double letter singly sound combo 
	IGNORE_TWO_CHAR(29),  // primarly [ch]ocolate [ch]rome ya[ch]t tea[ch] te[ch]
					  // for every rule there seem to be too many exceptsion
	DOUBLE_CHAR(20),
	SINGLE_CHAR(10),
	
	//IGNORED_LAST_PASS(2),
	// order of precedence:  largest to smallest
	LAST_CATCH_ALL_IGNORE_LAST_PASS(-1)
	;
	
	private final int _precedenceIndex;
	
	EnglishRulePrecedences (int precedenceIndex){
	this._precedenceIndex = precedenceIndex;
	}
	public int getPrecedenceIndex() {
	return _precedenceIndex;
	}

}

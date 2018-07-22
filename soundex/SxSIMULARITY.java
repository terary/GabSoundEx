package com.gabbytt.soundex;

public enum SxSIMULARITY {
//	STRING_CONTAINS(50),   //  
	STRING_EQUAL(40), //if stringA= stringB == all are true
	SOUNDEX_EQUAL(30), //if soundExA=SoundExB = group same, group numberSame
	GROUP_SAME(20), // if groupA=groupdB --> then numbers must be different (because above checks groupCode+number
//	GROUP_SAME_NUMBER_DIFF(10), //I think this is implied from above.
	NOT_SIMILAR(0),  // if none of the above - this is distictly different
	IGNORED(-1);
	private final int _ranking;
	SxSIMULARITY(int ranking){
		this._ranking = ranking;
	}
	public int getSimilarityRanking() {return this._ranking ;}
	
}

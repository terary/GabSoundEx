package com.gabbytt.soundex.interfaces;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;



public interface ISxEncoder {
	ArrayList<IRule>  getRuleSet();
//	Set<IRule> getRuleSet();
	Locale getLocale();
//	ArrayList<SxSoundByte> encode(String word);
//	ArrayList<ISxSoundByte> encode(String word);
	String sanitize(String word);
	String[] makeWordFrom(String sentence);
	ArrayList<? extends ISxSoundByte>  encode(String word);
	public  Boolean isValidChar(String s);

	
}

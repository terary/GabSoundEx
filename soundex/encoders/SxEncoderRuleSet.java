package com.gabbytt.soundex.encoders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gabbytt.soundex.SxSoundByte;
import com.gabbytt.soundex.interfaces.*;

public class SxEncoderRuleSet implements ISxEncoder {
	//allow or not to allow ' ' is the question
	final static private String _invalidCharPatternString = "[^a-z ]"; 
	final private Pattern _invalidCharPattern = Pattern.compile(_invalidCharPatternString);
	//final private Pattern _invalidCharPattern = Pattern.compile("[a-z]*");
	
	
	//static Set<IRule> _ruleSet;
	ArrayList<IRule> _ruleSet;
	static Locale _locale;
	
	
//	public SxEncoderRuleSet(Locale loc, ArrayList<IRule>  ruleCollection){
	public SxEncoderRuleSet(ILanguageRuleSet ruleCollection){
		//ruleSet = new HashSet<IRule>(RulesEnglish.toArrayList());
		_locale = ruleCollection.getLocale();
		_ruleSet = ruleCollection.toArrayList();
 
	
	}
	@Override
	public String sanitize(String word) {
//		//	final private Pattern _invalidCharPattern = Pattern.compile("[a-z]*");
//
//		//return _invalidCharPattern.matcher(s.toLowerCase()).matches();
//		String patternString = "[a-z]*" ;
//		Pattern pat = Pattern.compile(patternString);
//
////		String newWord = word.toLowerCase().replaceAll("[^a-z ]" , "");
//		String newWord = word.toLowerCase().replaceAll(_invalidCharPatternString , "");
//		System.err.println("Before: '" + word + "' and after: '"+newWord+"'");
//		
//		 return  word.replace(_invalidCharPatternString , "").toLowerCase();// _invalidCharPattern .matcher(s.toLowerCase()).matches();

		 return word.toLowerCase().replaceAll(_invalidCharPatternString , "");
	}
	
	@Override
	public String[] makeWordFrom(String sentence) {
		sentence =sanitize(sentence);//  sentence.replaceAll("[^a-z ]","");  //i18n issues
		String[] words = sentence.split("\\s+");
		
		
		return words;
		
	}

	
	@Override
	public ArrayList<IRule>  getRuleSet() {
		
		return _ruleSet;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public ArrayList<? extends ISxSoundByte> encode(String word) {
		ArrayList<SxSoundByte> soundBytes = new ArrayList<SxSoundByte>();
		//******************88
		//_originalWord = originalWord;
		//_sanitizedWord =_originalWord.toLowerCase().replaceAll("[^\\p{IsAlphabetic}]", "");
		//_sanitizedWord =_originalWord.toLowerCase().replaceAll("[^a-z]", "");
		//_sanitizedWord =word.toLowerCase().replaceAll(_invalidCharPattern , "");
		
		String intermediateSubstitute = "*";
		
		String unencoddedWord = word;
//		_unencoded  =word; // word.toLowerCase().replaceAll("[^\\p{IsAlphabetic}]", "");
//		_unencoded  =_sanitizedWord; // word.toLowerCase().replaceAll("[^\\p{IsAlphabetic}]", "");
//		RulesEnglish e;// = new RulesEnglish();
		
		

		for(IRule rule: _ruleSet) {
			unencoddedWord = encodeSxByte(rule,unencoddedWord ,intermediateSubstitute,soundBytes );
		}
		
		Collections.sort((List<SxSoundByte>)soundBytes); 	// <--- important,  reorder soundByts by 
		// by word order not ruleSet.  
		// applied larger-to0smaller letter group with some exceptions
		// yeilding soundByte collection ordered applied rule.. We want
		// soundByte collection order same as the word they represent.

		//*******************
		//(ArrayList<ISxSoundByte>)
		return soundBytes ;
	}
	private String encodeSxByte(IRule rule,String word,String intermediateSubstitute,ArrayList<SxSoundByte> soundBytes) {
		
//      StringBuilder  debugOutput ; 
      StringBuffer unencoded = new StringBuffer();
      word = word.toLowerCase();
//      System.out.print("Working on:" + rule.getRuleName());
      int lastEnd = 0;
      SxSoundByte soundByte;
          Matcher matcher = rule.getPattern().matcher(word);

//          boolean found = false;
          while (matcher.find()) {
          	
          	String tmp = repeatChr(intermediateSubstitute,matcher.group().length());
          	matcher.appendReplacement(unencoded, tmp );
          	lastEnd =matcher.end();
          	soundByte = new SxSoundByte(matcher.group() ,rule.getSoundExCode(), matcher.start(),rule.ignore());
          	soundBytes.add(soundByte);
//               found = true;
          }
          unencoded.append(word.substring(lastEnd)) ;//+= word.substring(lastEnd);
	return unencoded.toString();           
	}

	
	@Override
	public Boolean isValidChar(String s) {
		// TODO not tested
//		 Pattern p = Pattern.compile("[a-zA-Z]");
//		 Matcher m = p.matcher("aA".toLowerCase());
//		 boolean b = m.matches();
		 return _invalidCharPattern.matcher(s.toLowerCase()).matches();
		 //return s.toLowerCase().matches("[a-z]*");
		//return null;
	}

	
	private String repeatChr(String chr,int count) {
		return new String(new char[count]).replace("\0", chr);
	}

}

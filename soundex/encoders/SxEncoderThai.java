package com.gabbytt.soundex.encoders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gabbytt.soundex.interfaces.*;
import com.gabbytt.soundex.SxSoundByte;


public class SxEncoderThai implements ISxEncoder {
    private static final Hashtable<String, String> t1Map = createMap("กขฃคฅฆงจฉชฌซศษสญยฎดฏตณนฐฑฒถทธบปผพภฝฟมรลฬฤฦวหฮอ","กกกกกกงจชชชซซซซยยดดตตนนททททททบปพพพฟฟมรรรรรวหหอ");
    private static final Hashtable<String, String> t2Map = createMap("กขฃคฅฆงจฉชซฌฎฏฐฑฒดตถทธศษสญณนรลฬฤฦบปพฟภผฝมำยวไใหฮาๅึืเแโุูอ", "1111112333333333333333333444444445555555667777889AAABCDEEF");
	Pattern _stripPattern01 = Pattern.compile("[ก-ฮ][ิุู]?์");
	Pattern _stripPattern02 = Pattern.compile("[่-๋]");
	Pattern _stripPattern03 = Pattern.compile( "[็ํฺๆฯ]");
	Boolean IS_IGNORED =true;

	static Set<IRule> _ruleSet;
	static Locale _locale;
	//	public RuleSetEnglish(Locale loc, ArrayList<IRule> ruleCollection){
	//public RuleSetThai(Locale loc,ArrayList<IRule> ruleCollection) {
	public SxEncoderThai() {
		_locale = new Locale("TH-th");
		_ruleSet = new HashSet<IRule>();  //Thai has no rule set- yet.
		//Hope to re-do thai ruleSet such that it is more compatable with 
		//others.  Until then, ThaiRuleSet is self-contained.
	}
	@Override
	public String sanitize(String word) {
		return word; // stubbed for latter  development
	}
	
	@Override
	public String[] makeWordFrom(String sentence) {
		String[] words = new String[0];
		
		return words; // stubbed for latter  development
		
	}


	@Override
	public ArrayList<IRule> getRuleSet() {
		return new ArrayList<IRule>(0);
		//return _ruleSet;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public ArrayList<ISxSoundByte> encode(String word) {
		// TODO Auto-generated method stub
		return toSoundBytes(word);
		//return null;
	}

	@Override
	public  Boolean isValidChar(String s) {
		
        if(s.codePointAt(0) >= hexStr2int("0x0E01") && s.codePointAt(0) <= hexStr2int("0E30")) {
            return true;
        }else if(s.codePointAt(0) >= hexStr2int("0E40") && s.codePointAt(0) <= hexStr2int("0E46")) {
            return true;
        }else if(s.codePointAt(0) >= hexStr2int("0E32") && s.codePointAt(0) <= hexStr2int("0E33")) {
            return true;
        }
        else if(s.codePointAt(0) >= hexStr2int("0E5A") && s.codePointAt(0) <= hexStr2int("0E58")) {
            return true;  //Told these are not actual Thai letters but they're on the chart
        }
        else if(s.codePointAt(0) == hexStr2int("0E4F") ) {
            return true;
        }

        return false;
	}
	//****************************************
    private static int hexStr2int(String hex) {
        //purpose to covernt codePoint hex to int
        //Not trully hex to int, tested only within the scope of Thai Language
        //Any value outside of that range will have undetermined results.

        hex=hex.replace("0x", "");
        return (int) Long.parseLong(hex, 16);
    }

    private String sanitizeNonThaiAlphaStrict(String word) {
        //return should be only alpha characters, no accents
        String cleanWord = "";
        String letters[] = word.split("");
        for(String letter :letters) {
            if(letter.length()>0 && isValidChar(letter)) {
                cleanWord += letter;
            }
        }
        return cleanWord;
    }

	
	
    private ArrayList<ISxSoundByte> toSoundBytes(String word) {
    	ArrayList<ISxSoundByte> bytes = new ArrayList<ISxSoundByte> ();
    	//word = encodeWord(ThaiRule.IGNORE001,word,"*",bytes);
    	String ignoreSoundExChar = "-";
    	String intermediateSubstitute = "*";

    	word = encodeWord(_stripPattern01,ignoreSoundExChar, word,IS_IGNORED,intermediateSubstitute,bytes);
    	word = encodeWord(_stripPattern02,ignoreSoundExChar, word,IS_IGNORED,intermediateSubstitute,bytes);
    	word = encodeWord(_stripPattern03,ignoreSoundExChar, word,IS_IGNORED,intermediateSubstitute,bytes);

    	word =determinePrimaryGroupCode(word,  word.toCharArray(),  bytes);
    	encodeRemainder(word ,  word.toCharArray(), bytes.get(0).getWordPart().length(), bytes)  ;
		
    	return bytes;
    }
	private String encodeWord(Pattern pat,String soundExCode,String word,Boolean isIgnored, String intermediateSubstitute,ArrayList<ISxSoundByte> soundBytes) {
		
	      StringBuffer unencoded = new StringBuffer();
	      word = word.toLowerCase();
	      int lastEnd = 0;
	      SxSoundByte soundByte;
	          Matcher matcher = pat.matcher(word);

	          while (matcher.find()) {
	          	
	          	String tmp = repeatChr(intermediateSubstitute,matcher.group().length());
	          	matcher.appendReplacement(unencoded, tmp );
	          	lastEnd =matcher.end();
	          	soundByte = new SxSoundByte(matcher.group() ,soundExCode, matcher.start(),isIgnored);
	          	soundBytes.add(soundByte);
	          }
	          unencoded.append(word.substring(lastEnd)) ;//+= word.substring(lastEnd);
		return unencoded.toString();           
	}


	private static Boolean strMatch(String subject, String test) {
	  return Pattern.compile( test).matcher(subject).find();
	}
	
	private void encodeRemainder(String word,  char[] inChars, int startPosition, ArrayList<ISxSoundByte> sbytes)  {
		String soundGroupCode = ""; //Gabby expects groupd-by-sound:  apple-> P1P1L3.
							   // This algorithm (and I) do not support grouping in Thai
							   // arbirtatry asigning by algorithm
	
		int i_v = 0;
        for(int i =0;i<inChars.length;i++) {
            String xChar = inChars[i] + "";
            if(xChar.equals("*") || xChar.equals("-")) {continue;}

            if (strMatch(xChar, "[ะัิี]")) { // # 7. ตัวคั่นเฉยๆ
                i_v=i;
                soundGroupCode="G";
                sbytes.add(new SxSoundByte(xChar, "", startPosition + i,IS_IGNORED ));
            }else if( "[าๅึืู]".indexOf(xChar)>=0  ) {  // # 8.คั่นและใส่
                i_v = i;
                soundGroupCode="H";
                sbytes.add(new SxSoundByte(xChar, soundGroupCode+t2Translate(xChar), startPosition + i,!IS_IGNORED ));
            }else if(strMatch(xChar, "['ุ']")) {   //# 9.สระอุ
            	soundGroupCode="J";
                i_v =i;
                if(i==0 || !strMatch(xChar, "[ตธ]")  ) {   // #9.b
	                sbytes.add(new SxSoundByte(xChar, soundGroupCode+t2Translate(xChar), startPosition + i,!IS_IGNORED));
                }else {
	                sbytes.add(new SxSoundByte(xChar, "", startPosition + i,IS_IGNORED ));
                }
            }else if(strMatch(xChar, "[หอ]")) {   // # 10
            	soundGroupCode="K";
                if(i+1 <inChars.length &&  strMatch(inChars[i+1]+"", "/ึืุู/")  ) {
	                sbytes.add(new SxSoundByte(xChar, soundGroupCode+t2Translate(xChar), startPosition + i,!IS_IGNORED ));
                }
            }else if(strMatch(xChar, "[รวยฤฦ]")) {  // #11
            	soundGroupCode="L";
                if((i_v==i-1) || (i+1<inChars.length && strMatch(inChars[i+1]+"", "/ึืุู/")  )) {
	                sbytes.add(new SxSoundByte(xChar, soundGroupCode+t2Translate(xChar), startPosition + i,!IS_IGNORED ));
                }
            }else {
            	soundGroupCode="M";
                sbytes.add(new SxSoundByte(xChar, soundGroupCode+t2Translate(xChar), startPosition + i,!IS_IGNORED ));
            }
        }
	}

	private static String repeatChr(String chr,int count) {
		return new String(new char[count]).replace("\0", chr);
	}
    private static Hashtable<String,String> createMap(String keys,String values){
        Hashtable<String, String> mapping = new Hashtable<String , String>();

        String[] _keys =  keys.split("");
        String[] _values =  values.split("");

        for(int i=0;i<_keys.length;i++){
            mapping.put(_keys[i], _values[i]);
        }
        return mapping;
    }

	private static String t2Translate(String chr){

        if(chr.indexOf(" ")>=0 ) {
            return chr;
        }else if(chr.indexOf("้")>=0 ) { //failed to strip accent
            return "";
        }else if(chr.indexOf("่")>=0 ) { //failed to strip accent
            return "";
        }else if(t2Map.containsKey(chr)) {
            return t2Map.get(chr);
        } {
            return "?(" + chr + ")";
        }

    }
    private static String t1Translate(String chr){
        if(t1Map.containsKey(chr)) {
            return t1Map.get(chr);
        }else {
            return chr;
        }
    }
	private static String determinePrimaryGroupCode(String word, char[] inChars, ArrayList<ISxSoundByte> sbytes) {
        String soundExIndices = "";
        SxSoundByte soundByte ;
        //char[] inChars = word.toCharArray();
        word.charAt(0);
        String firstChar =  "" + word.charAt(0);
        if( "ก".codePointAt(0) <= firstChar.codePointAt(0)  && firstChar.codePointAt(0) <= "ฮ".codePointAt(0)  ){

            soundExIndices += t1Translate(inChars[0]+"");
            inChars = Arrays.copyOfRange(inChars, 1, inChars.length);
            soundByte = new SxSoundByte(firstChar,soundExIndices , 0,false);
        }else{
            soundExIndices += t1Translate(inChars[1] + "");
            soundExIndices += t2Translate(inChars[0] + "");
            inChars = Arrays.copyOfRange(inChars, 2, inChars.length);
            soundByte = new SxSoundByte(word.substring(0,1),soundExIndices , 0,false);
        }
        sbytes.add(soundByte);
        return String.valueOf(inChars);
        //return soundExIndices;
	}

	//****************************************

}

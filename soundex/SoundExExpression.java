package com.gabbytt.soundex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import com.gabbytt.soundex.interfaces.*;
import com.gabbytt.soundex.SoundExExpression.compareMethod;
import com.gabbytt.soundex.encoders.*;
/**
 * Different considerations should be given for attempting group of word comparisions (sentence to sentence).
 *   Position becomes relevant for not matching words.
 * 
 * 	 goal: "The beagle t'is for warning."
 *   attempt: "north is baker the warn warm bagel too beagle was warm angle to much"
 * 
 * 
 *   matches: beagle warming:warm the
 *   non-matches: for t'is
 * 
 *   'for' simple doesn't exist in the attempt text
 *   
 *   't'is' should match 'is' however the matching sByte 's' will match all occurrences of 's'
 *   and 't' matching all occurrences of 't'.
 * 			Attempting to use a quntification of matching (scoring) yields poor result.
 * 			'tis' will match '[th]is' and 'is' with the same score really no way to know
 * 			which is intended (google adds words doesn't always hear all words so counting is not reliable).
 * 			
 * 			warming warmer and rewarm score very low when attempting to do match/non-match count score,
 * 			while the soundEx score is acceptable.
 * 
 *  
 *  		Hence, while doing groups of words if a word doesn't match either string comparision or
 *  		soundEx comparison  - then it's considered non-match (not similar).
 * 
 * @author terary
 *
 */

public class SoundExExpression {
	public static final int NOT_MATCHED=-1;
	//public static final int MATCHED=0;
	Locale _locale;
	ISxEncoder sxEncoder;
	String _originalWord;
	ArrayList<SxSoundByte> _soundBytes;
	String _sxCode;
	int _countSoundBytesNotIgnored=0;
	public SoundExExpression(Locale loc) {
		_locale = loc;
		sxEncoder = SxEncoderFactory.getEncoder(_locale ); 
	} 
	public SoundExExpression(ISxEncoder encoder) {
		_locale = encoder.getLocale();
		///sxEncoder = SxEncoderFactory.getEncoder(_locale ); 
		sxEncoder = SxEncoderFactory.getEncoder(encoder); 
	} 
	public SoundExExpression(ILanguageRuleSet s) {
		sxEncoder = SxEncoderFactory.getEncoder(s); 
		_locale = sxEncoder.getLocale();
		///sxEncoder = SxEncoderFactory.getEncoder(_locale ); 
	} 

	public SoundExExpression compileWithIgnored_debug(String word) {
		_originalWord= word;
		_soundBytes = (ArrayList<SxSoundByte>)sxEncoder.encode(word);
		_sxCode = compileSoundExCodeWithIgnore_debug(_soundBytes );
		return this;
	}
	
	public SoundExExpression compile(String word) {
		_originalWord= word;
		_soundBytes = (ArrayList<SxSoundByte>)sxEncoder.encode(word);
		_sxCode = compileSoundExCode(_soundBytes );
		
		return this;
	}
	private String compileSoundExCodeWithIgnore_debug(ArrayList<SxSoundByte> soundBytes) {
		StringBuilder sxCode = new StringBuilder();
		for(SxSoundByte sbyte: soundBytes) {
			sxCode.append(sbyte.getSoundExCode());
		}
		_countSoundBytesNotIgnored = soundBytes.size();
		return sxCode.toString();
	}
	
	private String compileSoundExCode(ArrayList<SxSoundByte> soundBytes) {
		StringBuilder sxCode = new StringBuilder();
		_countSoundBytesNotIgnored=0; 

		for(SxSoundByte sbyte: soundBytes) {
			if(!sbyte.isIgnored())
			{sxCode.append(sbyte.getSoundExCode());
			_countSoundBytesNotIgnored++; 
			}
		}
		return sxCode.toString();
	}
	public String findMostSimilarWord(String wordsAsString, SxRuntimeSettings cpx, Float minimumSimilarity) {
		String attemptWords[] = uniqueWordList(wordsAsString.trim());// makeWords(sGoal);
		return findMostSimilarWord(attemptWords, cpx, minimumSimilarity); 
	}
	public HashMap<String , Float> wordSimilarities(String[] attemptWords, SxRuntimeSettings cpx){
		return wordSimilarities(attemptWords, cpx, 0.0f);
	}
	public HashMap<String , Float> wordSimilarities(String[] attemptWords, SxRuntimeSettings cpx,Float minimumSimilarity){
		SoundExExpression sxAttempt = new SoundExExpression(this._locale);
		HashMap<String , Float> wordCompareScores = new HashMap<String,Float>();

		for(String attemptWord:attemptWords) {
			sxAttempt.compile(attemptWord);
			int[] diffs = this.comapareSoundEx( sxAttempt , cpx.MINIMUM_SIMULARITY,cpx.compMethod);
			
			//int countAttemptBytes = sxAttempt.getCountSoundBytes();
			int countAttemptBytes = sxAttempt.getCountSoundBytesNotIgnored();// .getCountSoundBytes();
			int countSameBytes = countMatches(diffs);
			int countGoalBytes = this.getCountSoundBytesNotIgnored();
			int countTotalConsideredBytes = countAttemptBytes + countGoalBytes - countSameBytes ;
			Float score = (float) countSameBytes /countTotalConsideredBytes ;
			if(score  >= minimumSimilarity) {
				//System.out.println(this.getOriginalWord() + "("+this.getSoundExCode()+")"  +" : " +attemptWord+ "("+sxAttempt.getSoundExCode()+") score: " + score +"(sameBytes:"+countSameBytes+", totalConsideredBytes:"+countTotalConsideredBytes +")");
				wordCompareScores.put(attemptWord, score);
			}
			
		}
		return wordCompareScores; 
	}
	
	public String findMostSimilarWord(String[] attemptWords, SxRuntimeSettings cpx,Float minimumSimilarity) {
		HashMap<String , Float> wordComareScores = wordSimilarities(attemptWords,cpx, minimumSimilarity );
		Float bestScore=0.0f;
		String bestWord = "";
		for (String key : wordComareScores.keySet()) {
			if(wordComareScores.get(key) >bestScore) {
				bestWord = key;
				bestScore = wordComareScores.get(key);
			}
		}
		return bestWord;
	}
	public static int countMatches(int[] diffs) {
		int matches =0;
		for(int i=0; i<diffs.length; i++) {
			if(diffs[i] > SoundExExpression.NOT_MATCHED) {
				matches++;
			}
		}
		return matches;
	}

	private String[] uniqueWordList(String stringOfwords) {
		//SoundExExpression sxGoalWord= new SoundExExpression(new Locale("EN-us"));
		HashMap<String,String> unqWordsHash = new HashMap<String,String>();
		String words[] = this.makeWordsFrom(stringOfwords.toLowerCase());// makeWords(sGoal);
		for(int i=0; i<words.length;i++) {
			if(!unqWordsHash.containsKey(words[i])) {
				unqWordsHash.put(words[i], words[i]);
			}
		}
		return unqWordsHash.keySet().toArray(new String[unqWordsHash.keySet().size()]);
	}

	
	public String getOriginalWord() {
		return _originalWord;
	}
	public String getSoundExCode() {
		return _sxCode;
	}
	public String x_getSoundExCodeSlice(int start, int end) {
		if(start>=(end+1)) {return"";}
		String soundExCode = getSoundExCode();
		String sliceCode = "";
		
		String[] codes = soundExCode.split("(?=[A-Z])");
		
		for(int i=start;i<end+1;i++) {
			sliceCode += codes[i]; 
		}
		
		return sliceCode ;
		
	}
	
	public SxSoundByte getSoundByteAt(int i) {
		return _soundBytes.get(i);
	}
	public int getCountSoundBytes() {
		return _soundBytes.size();
	}
	public int getCountSoundBytesNotIgnored() {
		return _countSoundBytesNotIgnored;
	}
	public String[] makeWordsFrom(String sentence){
		return sxEncoder.makeWordFrom(sentence);
	}
	public String sanitize(String word) {
		return sxEncoder.sanitize(word);
	}
	
	public int[] compareExistence(SoundExExpression otherSx, SxSIMULARITY MINIMUM_SIMULARITY ) {
		/**
		 * Check to see if similar soundByte exists in other SoundExExpression, regardless
		 * of repetition or position.  ("apple").compareExistence("paul"),
		 * Both 'p's of apple will match the singular 'p' of paul.
		 * 
		 * Match is determined by 'MINIMUM_SIMULARITY'  see SxSoundByte.compareSoundByte(...) for explanation
		 */
		return comapareSoundEx(otherSx, MINIMUM_SIMULARITY, compareMethod.EXISTENCE);
	}	
	public int[] compareStrict(SoundExExpression otherSx, SxSIMULARITY MINIMUM_SIMULARITY ) {
		/**
		 * ("apple").compareExistence("paul"),
		 *  first 'p' of apple will match first match 'p' of paul, second 'p' not a match
		 *  'l' will match.
		 *  
		 * ("apple").compareExistence("paulp")
		 * same as above,  except -- second 'p' of apple WILL NOT match because the last found index (l=3 of paulp)
		 * is greater than the index of the second 'p' of apple.  
		 * 
		 * Hence, repetition and position are loosely factors.  
		 * 
		 * purpose:
		 * 		That the student can see a more detailed (though not always accurate) differentiation
		 *  
		 * 
		 * Match is determined by 'MINIMUM_SIMULARITY'  see SxSoundByte.compareSoundByte(...) for explanation
		 */
		return comapareSoundEx(otherSx, MINIMUM_SIMULARITY, compareMethod.STRICT);
	}	
	public enum compareMethod{
		STRICT,
		EXISTENCE;
	}
	public int[] comapareSoundEx(SoundExExpression otherSx, SxRuntimeSettings cpxRec ) {
		return comapareSoundEx(otherSx, cpxRec.MINIMUM_SIMULARITY, cpxRec.compMethod );
	}	
	public int[] comapareSoundEx(SoundExExpression otherSx, SxSIMULARITY MINIMUM_SIMULARITY, compareMethod compMethod ) {
		/**
		 * 
		 * @returns int[] length same as this.soundEx.size()
		 * 			each element:
		 * 			-1 (aka NOT_MATCHED) indicates no match
		 * 			>=-1 is the index of the 'other'.soundbyte(index) that matched.
		 * 			ignored is always NO_MATCH by virtue of 'ignore'
		 * 
		 * Match is determined by 'MINIMUM_SIMULARITY'  see SxSoundByte.compareSoundByte(...) for explanation
		 * 
		 * 
		 */
		
		int[] pairingIndexes = intArrayCreator(_soundBytes.size(),NOT_MATCHED);
		int thisIdx=-1; //
		int lastFoundIdx=-1;
		int foundIdx=0;
		for(SxSoundByte thisByte: _soundBytes) {
			thisIdx++;
			if(!thisByte.isIgnored()) {
				if(compMethod == compareMethod.EXISTENCE) {lastFoundIdx = -1;} 
				
				foundIdx = SoundExExpression.scanForSoundByte(thisByte,otherSx,lastFoundIdx+1  ,MINIMUM_SIMULARITY);
				if(foundIdx>NOT_MATCHED) {
					pairingIndexes[thisIdx] = foundIdx;
					lastFoundIdx = foundIdx;
				}
			}
		}
		return pairingIndexes;
	}
	public static class SxRuntimeSettings{
		public SxSIMULARITY MINIMUM_SIMULARITY;
		public compareMethod compMethod; 
		public UF_IgnoredMatching uf_IgnoredMatching;
		public SoundExExpression.DelineateFormatter formatter;
		public SxRuntimeSettings(SxSIMULARITY MINIMUM_SIMULARITY,compareMethod compMethod,UF_IgnoredMatching i, SoundExExpression.DelineateFormatter f){
				this.MINIMUM_SIMULARITY =MINIMUM_SIMULARITY;
				this.compMethod = compMethod;
				this.uf_IgnoredMatching = i;
				formatter = f;
		}
	}
	
	public static class DelineateFormatter{
		/**
		 * See code for 'DelineateDifferences' for usage
		 */
		public String frontMatched="";
		public String rearMatched="";
		public String frontNotMatched="";
		public String rearNotMatched="";

		public DelineateFormatter(String fMatched, String rMatched,String fNotMatched,String rNotMatched ){
			frontMatched = fMatched;
			rearMatched = rMatched;
			frontNotMatched = fNotMatched;
			rearNotMatched = rNotMatched;
		}
		
		public String formatMatched(String val) {
			return frontMatched + val + rearMatched;
		}
		public String formatNotMatched(String val) {
			return frontNotMatched + val + rearNotMatched;
		}
	
	
	}
	public String x_DelineateDifferences(SoundExExpression other, DelineateFormatter formatting,SxSIMULARITY MINIMUM_SIMULARITY ) {
		/**
		 * Alia/overload for method  of same name.
		 */
		return x_DelineateDifferences(other, formatting, MINIMUM_SIMULARITY, compareMethod.EXISTENCE);
	}
	public String x_DelineateDifferences(SoundExExpression other, DelineateFormatter formatting,SxSIMULARITY MINIMUM_SIMULARITY, compareMethod compMethod ) {
		/**
		 * Usage: 
		 * 
		 * 		SoundExExpression.DelineateFormatter dFormater = new SoundExExpression.DelineateFormatter("","","(",")");
		 *		System.out.println("Extra Sounds:" + sxAttemptWord.DelineateDifferences(sxGoalWord, dFormater, SxSIMULARITY.SOUNDEX_EQUAL));
		 * 		
		 * Purpose:
		 * 		Create a string the delineat the difference between this and other. Specifically, sxSoundByte elements
		 * 		that are in this and not in other.
		 */
		String diffString = "";
		int[] diffIndices = comapareSoundEx(other, MINIMUM_SIMULARITY,compMethod);
		for(int i=0; i<diffIndices.length;i++) {
			SxSoundByte Abyte = getSoundByteAt(i);
			if(diffIndices[i] > SoundExExpression.NOT_MATCHED || Abyte.isIgnored()){
				diffString += formatting.frontMatched + Abyte.getWordPart() + formatting.rearMatched;
			}else {//if(diffIndices[i] > SoundExExpression.NOT_MATCHED){
				diffString += formatting.frontNotMatched + Abyte.getWordPart() + formatting.rearNotMatched;

			}
		}

		
		return diffString;
	}
	public ArrayList<SxSoundByte> extractDifferenceBytes(SoundExExpression other, SxSIMULARITY MINIMUM_SIMULARITY) {
		/**
		 * Alia/overload for method  of same name.
		 */
		return extractDifferenceBytes(other, MINIMUM_SIMULARITY, compareMethod.EXISTENCE); 
	}
	public ArrayList<SxSoundByte> extractDifferenceBytes(SoundExExpression other, SxSIMULARITY MINIMUM_SIMULARITY, compareMethod compMethod ) {
		/** 
		 * Usage: 
		 * 
		 * 		ArrayList<SxSoundByte> troubleBytes = new  ArrayList<SxSoundByte>();
		 * 		troubleBytes.addAll(sxAttemptWord.extractDifferenceBytes(sxGoalWord, SxSIMULARITY.SOUNDEX_EQUAL,  compareMethod.EXISTENCE));
		 * 		...
		 * 		for(SxSoundByte s: troubleBytes) {
		 * 			System.out.println(s.getWordPart()+ " -> " +s.getSoundExCode());
		 * 		}
		 * 		
		 * Purpose:
		 * 		extract sounbytes from this that are not in other.
		 */
		ArrayList<SxSoundByte> diffBytes = new ArrayList<SxSoundByte> ();
		int[] diffIndices = comapareSoundEx(other, MINIMUM_SIMULARITY,compMethod);
		for(int i=0; i<diffIndices.length;i++) {
			SxSoundByte Abyte = getSoundByteAt(i);
			if(diffIndices[i] == SoundExExpression.NOT_MATCHED && !Abyte.isIgnored()){
				diffBytes.add(Abyte);	
			}
		}
		return diffBytes ;
	}
	
	public String x_DelineateDifferencesDebug(SoundExExpression other, DelineateFormatter formatting,SxSIMULARITY MINIMUM_SIMULARITY) {
		String diffString = getOriginalWord() + " -> " + getSoundExCode() +" , "+ other.getOriginalWord() + " -> " + other.getSoundExCode();
		int[] diffIndices = compareExistence(other, MINIMUM_SIMULARITY);
		for(int i=0; i<diffIndices.length;i++) {
			SxSoundByte Abyte = getSoundByteAt(i);
			diffString  += Abyte.getWordPart() + "(" + Abyte.getSoundExCode() + ")";
			if(Abyte.isIgnored()) {
				diffString += " isIGNORE ";
			}else if(diffIndices[i] == SoundExExpression.NOT_MATCHED){
				diffString += " NO MATCH ";
			}else if(diffIndices[i] > SoundExExpression.NOT_MATCHED){
				SxSoundByte Bbyte = other.getSoundByteAt(diffIndices[i]);	
				diffString += " MATCHED: " + Bbyte.getWordPart() + "(" + Bbyte.getSoundExCode() + ") ["+ i + ":" + diffIndices[i]+"]";

			}
			diffString+= "\n\t";
		}

		
		return diffString;
	}
	
	

	private int[] intArrayCreator(int size,int defaultValue) {
		/**
		 * usage 
		 * 	int arrOints[] = intArrayCreate(3,-1);
		 * 
		 * purpose
		 * 	 create and initialize array of ints[size]  to some default value
		 * 
		 */
		int arr[] = new int[size];
		for(int i=0;i<arr.length;i++) {
			arr[i] = defaultValue;
		}
		    
		return arr;
	}
	public enum UF_IgnoredMatching{
		NOT_MATCH,
		MATCH,
		IGNORE
	}
	
	public String UF_formatDifferneces(SxRuntimeSettings rts, int[] diffs) {
		SoundExExpression.DelineateFormatter format = rts.formatter;
		UF_IgnoredMatching ignoreMatching = rts.uf_IgnoredMatching;
		String formatedString = "";
		for(int i=0; i<diffs.length;i++) {
			SxSoundByte Abyte = this.getSoundByteAt(i);

			//ignores are considered matches or not?
			if(Abyte.isIgnored()) {
				if(ignoreMatching== UF_IgnoredMatching.IGNORE) {
					formatedString += Abyte.getWordPart();
				}else if(ignoreMatching== UF_IgnoredMatching.NOT_MATCH) {
					formatedString += format.formatNotMatched(Abyte.getWordPart());
				}else { //IgnoredMatching.MATCH
					formatedString += format.formatMatched(Abyte.getWordPart());
				}
			}else {
				
				if(diffs[i] == SoundExExpression.NOT_MATCHED ){
					formatedString += format.formatNotMatched(Abyte.getWordPart());
				}else {
					formatedString += format.formatMatched(Abyte.getWordPart());
				}
			}
		}
		return formatedString;
	}
	
	public static void UF_harvestStatsGetWordParts(SoundExExpression sxWord,int[] diffs,HashMap<String,String> wordParts,SxRuntimeSettings rts) {
	/**
	 * arguments:
	 * 		sxWord - precompiled soundExExpression
	 *  	diffs  - return value of comapareSoundEx(...)
	 * 			 	 non match =<-1     match>=0
	 * 
	 *  	wordParts 
	 *  		unique list of wordParts 
	 * 
	 * usage:
	 * 
	 *      HashMap<String,String> missingSounds = new HashMap<String,String>()
	 *      ...
	 *		int[] diffs = sxGoalWord.comapareSoundEx(sxAttempt, SxSIMULARITY,compareMethod);
	 *	    SoundExExpression.harvestStatsGetWordParts(sxGoalWord,diffs,missingSounds);
	 *      ...
	 * 		System.out.println("missing sounds." + missingSounds.values())
	 * 
	 * puprose:
	 * 		insert string of missing soundExCode(s) into missingSounds.  Mostly for client code to 
	 * 		track user's troubled sounds, for statistics and improved user experience 
	 * 
	 */
		//**************************************
		UF_IgnoredMatching ignoreMatching = rts.uf_IgnoredMatching;
		for(int i=0; i<diffs.length;i++) {
				SxSoundByte Abyte = sxWord.getSoundByteAt(i);
				if(wordParts.containsKey(Abyte.getWordPart())) {
					continue;
				}
				if(Abyte.isIgnored() ) {
					if(ignoreMatching== UF_IgnoredMatching.NOT_MATCH) {
						wordParts.put(Abyte.getWordPart(),Abyte.getWordPart());
					}
				}else {
					
					if(diffs[i] == SoundExExpression.NOT_MATCHED ){
						wordParts.put(Abyte.getWordPart(),Abyte.getWordPart());
					}
				}
		}
	}
	

	protected static int indexOf(SxSoundByte needle,SoundExExpression haystack,int startPosition, SxSIMULARITY MINIMUM_SIMULARITY) {
		/**
		 * Alias to 'scanForSoundByte(...)'
		 * 
		 */
		return scanForSoundByte(needle,haystack,startPosition, MINIMUM_SIMULARITY);
	}
	protected static int scanForSoundByte(SxSoundByte needle,SoundExExpression haystack,int startPosition, SxSIMULARITY MINIMUM_SIMULARITY) {
		/**
		 * usage:
		 *		int foundIdx = SoundExExpression.scanForSoundByte(thisByte,otherSx,lastFoundIdx+1  ,MINIMUM_SIMULARITY);
		 *
		 * purpose
		 * 		find first/next occurance of  needle in hastack.  Same idea as String.indexOf(). 
		 *
		 */
		if(startPosition<0) {return -1;}
		
		for(int i =startPosition; i<haystack.getCountSoundBytes();i++) {
			SxSoundByte straw = haystack.getSoundByteAt(i);
			if(!straw.isIgnored() && SxSoundByte.compareSoundByte2(needle,straw,MINIMUM_SIMULARITY))  {
				return i;
			}
		}//for haystack.SoundBytes
		return -1; // if we haven't already found it.
	}

	
	
}

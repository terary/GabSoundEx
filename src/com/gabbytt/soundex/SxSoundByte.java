package com.gabbytt.soundex;


import com.gabbytt.soundex.interfaces.ISxSoundByte;

public class SxSoundByte  implements Comparable <ISxSoundByte>, ISxSoundByte {
	private String _originalText;
	private String _soundExCode ;
	private int _wordPositionStart;  //position within orginaText
	private Boolean _isIgnored;
	private String _groupCode;
	
	public SxSoundByte(String originalText, String seCode, int startPos,Boolean isIgnored){
		_originalText = originalText;
		_soundExCode = seCode;
		// not certain all soundEx codes will be only one character
		//  A)  Maybe expand encoding
		//  B)  i18n may require more than one character
		//_groupCode = _soundExCode.substring(0,1 );
		_groupCode = _soundExCode.replaceAll("[0-9]","");
		_wordPositionStart = startPos;
		_isIgnored = isIgnored;
		
	} 

	public Boolean isIgnored() {
		return _isIgnored;
	}
	public String getSoundExCode() {
		return _soundExCode;
	}
	public String getGroupCode() {
		return _groupCode;
	}

	public String getWordPart() {
		return _originalText;
	}
	public int getStartPosition() {
		return _wordPositionStart;
	}

	
	
	@Override
	public int compareTo(ISxSoundByte o) {
		// TODO Auto-generated method stub
        //int compStartPos=((SxSoundByte)o).getStartPosition();
        int compStartPos=o.getStartPosition();
        /* For Ascending order*/
        return  this._wordPositionStart-compStartPos;
//		return 0;
	}
//	public int compareTo(Object o) {
//		// TODO Auto-generated method stub
//        int compStartPos=((SxSoundByte)o).getStartPosition();
//        /* For Ascending order*/
//        return  this._wordPositionStart-compStartPos;
////		return 0;
//	}

	 @Override
	    public String toString() {
	        return "[ wordseg=" +this.getWordPart()+ ", soudExCod=" + this._soundExCode+"]";
	    }
	 
	 //*****
//	public enum compareResultType{
//		STRING_EQUAL(40), //if stringA= stringB == all are true
//		SOUNDEX_EQUAL(30), //if soundExA=SoundExB = group same, group numberSame
//		GROUP_SAME(20), // if groupA=groupdB --> then numbers must be different (because above checks groupCode+number
////		GROUP_SAME_NUMBER_DIFF(10), //I think this is implied from above.
//		NOT_SIMILAR(0),  // if none ove the above - this is distictly different
//		IGNORED(-1);
//		private final int _ranking;
//		compareResultType(int ranking){
//			this._ranking = ranking;
//		}
//		public int getSimilarityRanking() {return this._ranking ;}
//		
//	}

	 public static SxSIMULARITY compareSoundByte(SxSoundByte A, SxSoundByte B) {
		 SxSIMULARITY  result = SxSIMULARITY.NOT_SIMILAR;
		if(A.isIgnored() || B.isIgnored()) {
			result = SxSIMULARITY.IGNORED;
		}else if(A.getWordPart().equals(B.getWordPart()) && (A.getSoundExCode().equals(B.getSoundExCode()))  ) {
			result = SxSIMULARITY.STRING_EQUAL;               //special cases where same collection of letters
																   // different sounds - different soundEx schedule/schwart
		}else if(A.getSoundExCode().equals(B.getSoundExCode())  ) {
			result = SxSIMULARITY.SOUNDEX_EQUAL;
		}else if(A.getGroupCode().equals(B.getGroupCode())  ) {
			result = SxSIMULARITY.GROUP_SAME;
		}//else - not similar
		return result;
	}
	public static Boolean compareSoundByte2(SxSoundByte A, SxSoundByte B,SxSIMULARITY   MINIMUM_SIMILARITY) {
		if(A.isIgnored() || B.isIgnored()) {
			return false;
		}
		SxSIMULARITY result = SxSoundByte.compareSoundByte(A,B);
		if(result.getSimilarityRanking()>=MINIMUM_SIMILARITY.getSimilarityRanking()) {
			return true;
		}else {
			return false;
		}
	}

}

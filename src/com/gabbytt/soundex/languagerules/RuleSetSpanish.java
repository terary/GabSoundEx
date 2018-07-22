package com.gabbytt.soundex.languagerules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

//import com.gabbytt.soundex.rules.English;
//import com.gabbytt.soundex.rules.EnglishPrecedences;
//import com.gabbytt.soundex.rules.IRule;
import com.gabbytt.soundex.interfaces.*;

public enum RuleSetSpanish implements IRule, ILanguageRuleSet  {

	/*Convention:
	 * SoundEx code = letter(group) + number..  
	 * 				Letter should be symbolic of sound.  Number is arbitrary (exception being single char).
	 * 				SoundEx code Q0->(C|G|J|K|Q|S|X|Z),  '0' being single character 
	 * 				numbers greater than zero are arbitrary.
	 * 
	 *  			soundEx code compare, 
	 *  				Lazy:   if( letter1=letter2 ) -> match
	 * 					Strict:  if(letterNumber1=letterNumber2) -> match
	 * 
	 * 				abs(number1-numbers2) has no relevance
	 * 
	 * 				SoundExCode: H2 -> ph: mega[ph]one, because SoundEx codes: 
	 * 					H1,H2,H3  are for TH,PH,SH respectively 
	 * 
	 * 
	 * 	SoundEx Codes should be uppercase letter+number only.. No special characters.  Encoding uses
	 *  uses special characters for intermedial processing.
	 *  
	 *  
	 *  If rule is not clear and consistent the result should be to ignore.
	 *  	dou[gh] cou[gh]  ignore 'gh'  (but not 'g' and 'h').
	 * 
	 *  Keep in mind English is imperfect and with several implementations.  We do not want to over-do 
	 *  analysis. Our end objective is to encourage students when they are correct (or near correct) 
	 *  and to inform them when *WE KNOW* where they can imporve.  With English the 'WE KNOW' part
	 *  will be difficult and should default to 'ignore'
	 *  
	 * 
	 * 
	 */

		//things not otherwise encoded will be encoded with ignore
		//CATCH_ALL("-","Catch All", "\\p{IsAlphabetic}",EnglishPrecedences.LAST_CATCH_ALL_IGNORE_LAST_PASS, "All Characters not otherwise encoded."),
		CATCH_ALL("-","Catch All", "[a-z]",EnglishRulePrecedences.LAST_CATCH_ALL_IGNORE_LAST_PASS, "All Characters not otherwise encoded."),

		/* TWO letter [re]bate */
		//RE("R1","Re","\\bre",EnglishPrecedences.PRFIX_SUFFIX_TWO_CHARS, "[Re]course but not windb[re]aker" ),
		RE("R2","Re","\\bre",EnglishRulePrecedences.PRFIX_SUFFIX_TWO_CHARS, "[Re]course but not windb[re]aker" ),

		LY("L1","Ly","ly\\b",EnglishRulePrecedences.PRFIX_SUFFIX_TWO_CHARS	, "word[ly] but not stee[ly]ard " ),
		
		UN("N2","Un","\\bun",EnglishRulePrecedences.PRFIX_SUFFIX_TWO_CHARS, "[un]known not wo[un]ded" ),

		/* three letter (pre) */
		PRE("P1","Pre","pre(?=["+RuleSetSpanish.CONSONANTS+"])",EnglishRulePrecedences.PRFIX_SUFFIX_THREE_CHARS, "[pre]tentious" ),
//		ING("I1","ing","ing",EnglishPrecedences.PRFIX_SUFFIX_THREE_CHARS, "walk[ing]" ),
		OUS("O1","OUS","ous",EnglishRulePrecedences.THREE_CHARS, "previ[ous]" ),
		
		/* (s|t)ion --- (!s^!t)ion becomes N from MN rule +vowel rule*/
		ION("M2","ION","(s|t)ion",EnglishRulePrecedences.FOUR_CHARS, "vaca[tion]" ),

//		KN("M0","KN","kn",EnglishPrecedences.DOUBLE_CHAR, "KN->N->M0" ),  //capture 'kn' but encode only 'n'

		//GH silent therefore not same as sh/th/ph
		// cough->kof dough->doh  What the hell man...?  
		//becuase in inconsistencies -- we just ALWAYS ignore it
		GH("-","GH","gh",EnglishRulePrecedences.SILENT, "ni[gh]t" ),

		
		C_SOFT("S1","c[i|e|y]|ae,","c(?=[i|e|y]|ae)",EnglishRulePrecedences.DOUBLE_CHAR, "C[iey]->S1 or c[ae]->S1"),  // This catches [c]ensor, [c]aesar, while the [c]arlos passes throug
		CH("-","ch","(ch)",EnglishRulePrecedences.IGNORE_TWO_CHAR, "chocolate/chloe" ),
		
			
			
		/* sh,th,ph  xH */	
//		TH("H1","TH","th",EnglishPrecedences.xH, "wea[th]er" ),
//		PH("H2","PH","ph",EnglishPrecedences.xH, "[ph]oto"),
//		SH("H3","SH","sh",EnglishPrecedences.xH, "[sh]out"),

		/* sch: schwenk, schedule, preschool, schema */
		SCHEDULE("SK1","SCHEDULE","sch(?=edule)",EnglishRulePrecedences.SCH_SPECIAL, "re[sch]edule"),
		SCHOOL("SK1","SCHOOL","sch(?=ool)",EnglishRulePrecedences.SCH_SPECIAL, "pre[sch]ool"),
		SCHEM("SK1","SCHEM","sch(?=em)",EnglishRulePrecedences.SCH_SPECIAL, "i[sch]ema"),
		SCH("SC1","SCH_GENERAL","sch",EnglishRulePrecedences.SCH_FINAL, "All 'sch' not in withing *school*, *schedule*, *schem*"),
		
		/* Single char subtitutions */
		BFPV("B0","BFPV,","(b|f|p|v)",EnglishRulePrecedences.SINGLE_CHAR, "BFPV->B0"),
		
		//SXZ("S0","SXZ,","(s|x|z)",EnglishPrecedences.SINGLE_CHAR, "S,X,Z,->S0"),
		S("S0","S,","(s)",EnglishRulePrecedences.SINGLE_CHAR, "S,->S0"),
		XZ("S1","XZ,","(x|z)",EnglishRulePrecedences.SINGLE_CHAR, "X,Z,->S1"),

		//Hard C
		CGJKQ("Q0","CGJKQX,","(c|g|j|k|q)",EnglishRulePrecedences.SINGLE_CHAR, "CGJKQ->Q0"),
		DT("D0","DT,","(d|t)",EnglishRulePrecedences.SINGLE_CHAR, "DT->D0"),
		L("L0","L,","l",EnglishRulePrecedences.SINGLE_CHAR, "L->L0"),
		MN("M0","MN,","(m|n)",EnglishRulePrecedences.SINGLE_CHAR, "MN->M0"),
		R("R0","R,","(r)",EnglishRulePrecedences.SINGLE_CHAR, "R->R0")
		;
	
//
//	
	public static final String CONSONANTS = "bcdfghjklmnpqrstvwxz";
	//System.err.println();
	
		private final int IGNORED = 0;    	   //Precedence zero or less are silently disregarded
		private final String _soundExCode;	   // Strings that match patter become this code  
		private final String _ruleName;	 	   // because everything should have a name -- readibility purposes only
		private final String _description;	   // code/debug -- readibility purposes only
		private final String _patternString;   // regEx string form to be come the 'pattern' regularExpression
		private final int _precedenceIndex;    // There is a trick to this. About be compiled and immutable
		private final Pattern 	_pattern;	   // caution making changes.
	

		RuleSetSpanish(String soundExCode,String name, String patternString, EnglishRulePrecedences precedenceIndex, String description ){
			this._soundExCode=  soundExCode;
			this._ruleName= name;
			this._description = description;
			this._patternString = patternString;
			this._pattern = Pattern.compile(_patternString);

			_precedenceIndex = precedenceIndex.getPrecedenceIndex();
			
		}
		public String getRuleName() {
			return _ruleName;
		}
		public String getRuleDescription() {
			return _description;
		}
		public int getPrecedenceIndex() {
			return _precedenceIndex;
		}
		public String getSoundExCode() {
			return _soundExCode;
		}
		public String getName() {
			return getRuleName();
		}
		public String getDescription() {
			return _description;
		}
			
		public String getPatternString() {
			return _patternString;
		}
		public Pattern getPattern() {
			return _pattern;
		}
		public Boolean ignore() {  //similar notion as 'null'.  There is no defined comparisions or soundEx value
			return (_precedenceIndex < IGNORED) 
					|| _precedenceIndex == EnglishRulePrecedences.SILENT.getPrecedenceIndex()
					|| _precedenceIndex == EnglishRulePrecedences.IGNORE_TWO_CHAR.getPrecedenceIndex();
		}

//		public static ArrayList<IRule> toArrayList(){
		public ArrayList<IRule> toArrayList(){
			//Precedence inwhich rules are applied is significant.
			//		Match/encode larger character patterns before smaller
			//		Match/encode common prefix/suffix before root word
			//
			//Larger precedence is applied before lower  				*tricky*
			//		100->0
			//
			ArrayList<IRule> arrayList= new ArrayList<IRule>() ;
			for(RuleSetSpanish r: RuleSetSpanish.values()) {
				arrayList.add(r);
			}
			Comparator<IRule> comparatorReverse = new Comparator<IRule>() {
			    @Override
			    public int compare(IRule left, IRule right) {
			        return  -1 * (left.getPrecedenceIndex()  - right.getPrecedenceIndex())  ; // use your logic
			    }
			};

			Collections.sort(arrayList, comparatorReverse); // use the comparator as much as u want
			return arrayList;
		}


		@Override //Comparable
		public int compareTo(IRule o) {
			
	        int compStartPos=o.getPrecedenceIndex();
	        return  (this._precedenceIndex-compStartPos) * -1;  //great presedence (100) should be applied before lower Precedence
		}
		float _version = 0.0001f;
		public float version() {return _version;}
		public final static Locale locale  = new Locale("EN-US");
		public Locale getLocale() {return locale;}

}

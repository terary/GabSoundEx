package com.gabbytt.soundex.encoders;

import java.util.Locale;
import com.gabbytt.soundex.interfaces.*;
import com.gabbytt.soundex.languagerules.RuleSetEnglish;
import com.gabbytt.soundex.languagerules.RuleSetSpanish;
import com.gabbytt.soundex.encoders.*;
import com.gabbytt.soundex.encoders.*;

public class SxEncoderFactory {

	public static ISxEncoder getEncoder(Locale loc){
		ISxEncoder encoder;
		if(loc.getLanguage().toLowerCase().equals("en-us") ) {
			encoder = new SxEncoderRuleSet(RuleSetEnglish.CATCH_ALL);  // passing the type not the value,  didn't know how to do that
																	   // so passed 'CATCH_ALL'  
//			encoder = new SxEncoderRuleSet(new Locale("EN-us"), RuleSetEnglish.toArrayList());

		}else if(loc.getLanguage().toLowerCase().equals("es-mx") ) {
			encoder = new SxEncoderRuleSet( RuleSetSpanish.CATCH_ALL);  // passing the type not the value,  didn't know how to do that
																		// so passed 'CATCH_ALL'  

			//			encoder = new SxEncoderRuleSet(new Locale("ES-mx"), RuleSetSpanish.toArrayList());
			
		}else if(loc.getLanguage().toLowerCase().equals("th-th") ) {
			encoder = new SxEncoderThai();
		}else {
			encoder = new SxEncoderRuleSet( RuleSetSpanish.CATCH_ALL);  // passing the type not the value,  didn't know how to do that
			//encoder = null;
		}
		return encoder;
	}
	public static ISxEncoder getEncoder(ILanguageRuleSet languageRuleSet){
		ISxEncoder encoder;
		encoder = new SxEncoderRuleSet(languageRuleSet);
		
		return encoder;
	}

	public static ISxEncoder getEncoder(ISxEncoder encoder){
		//admittedly pointless.
		//  Want client code to be able to provide home-grown encoders
		
		return encoder;
	}

//ISxEncoder

}



//
//RuleSetEnglish rse = new RuleSetEnglish(new Locale("EN-en"), RulesEnglish.toArrayList());
//soundBytes = rse.encode(word);
//System.out.println("English '" + word+ "' ->" + testMakeSoundExCode(soundBytes));
////System.out.println(soundBytes.get(0).getWordPart() + " -> " + soundBytes.get(0).getSoundExCode());
//
////************* Spanish
////word = "Reknown"
//rse = new RuleSetEnglish(new Locale("ES-mx"), RulesSpanish.toArrayList());
//soundBytes = rse.encode("Reknowning");
//System.out.println("Spanish '" + word+ "' ->" + testMakeSoundExCode(soundBytes));
//
//
////********* Thai
//word = "แสงอาทิตย์";
//RuleSetThai rst = new RuleSetThai();
//soundBytes = rst.encode("แสงอาทิตย์");

package com.gabbytt.dev;
import java.util.Locale;

import com.gabbytt.soundex.*;
import com.gabbytt.soundex.SoundExExpression.SxRuntimeSettings;
import com.gabbytt.soundex.SoundExExpression.UF_IgnoredMatching;
import com.gabbytt.soundex.SoundExExpression.compareMethod;

public class Main {

	static SoundExExpression.DelineateFormatter outputFormat = new SoundExExpression.DelineateFormatter("(", ")", "[", "]");		
	static SoundExExpression sxControl = new SoundExExpression(new Locale("en-us"));
	static SoundExExpression sxTest = new SoundExExpression(new Locale("en-us"));
	static SxRuntimeSettings SXSettings = new SxRuntimeSettings();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		boolean ignoreMatching = true;
//		example2();
		example1();
		
	}

	
	static void example2(){
		SXSettings.MINIMUM_SIMULARITY = SxSIMULARITY.SOUNDEX_EQUAL;
		SXSettings.uf_IgnoredMatching =SoundExExpression.UF_IgnoredMatching.IGNORE;
		
		String ctrlWord = "Photography";
		String testWord = "Phonograph";

		
		
		sxControl.compile(ctrlWord);
		sxTest.compile(testWord);
		
		int[] test2ctrl= sxControl.compareStrict(sxTest, SXSettings.MINIMUM_SIMULARITY);
		printResult("test2ctrl:",sxControl,test2ctrl);
		
		int[] ctrl2test= sxTest.compareStrict(sxControl, SXSettings.MINIMUM_SIMULARITY );
		printResult("ctrl2test:",sxTest,ctrl2test);
		
		System.out.println(outputFormat.formatMatched("Matched") + " " + outputFormat.formatNotMatched("Not Matched"));
		
	}

	static void printResult(String label, SoundExExpression sx, int[] compareResults) {

		System.out.println( label + " '"+sx.getOriginalWord()+"' -> '" +sx.getSoundExCode()+"' :" + sx.UF_formatDifferneces(SXSettings, compareResults));

	}
	
	static void example1(){
		SXSettings.MINIMUM_SIMULARITY = SxSIMULARITY.SOUNDEX_EQUAL;
		SXSettings.uf_IgnoredMatching =SoundExExpression.UF_IgnoredMatching.IGNORE;
		
		String ctrlWord = "record";
		String testWord = "prerecorded";

		
		
		sxControl.compile(ctrlWord);
		sxTest.compile(testWord);
		
		int[] test2ctrl= sxControl.compareStrict(sxTest, SXSettings.MINIMUM_SIMULARITY);
		printResult("test2ctrl:",sxControl,test2ctrl);
		
		int[] ctrl2test= sxTest.compareStrict(sxControl, SXSettings.MINIMUM_SIMULARITY );
		printResult("ctrl2test:",sxTest,ctrl2test);
		
		System.out.println(outputFormat.formatMatched("Matched") + " " + outputFormat.formatNotMatched("Not Matched"));
	}
	
	
	
	
	
	
	
	
}

package com.gabbytt.dev;
import java.util.Locale;

import com.gabbytt.soundex.*;
import com.gabbytt.soundex.SoundExExpression.SxRuntimeSettings;
import com.gabbytt.soundex.SoundExExpression.UF_IgnoredMatching;
import com.gabbytt.soundex.SoundExExpression.compareMethod;

public class Main {

	static SoundExExpression.DelineateFormatter outputFormat = new SoundExExpression.DelineateFormatter("(", ")", "[", "]");		

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		boolean ignoreMatching = true;
		example2();
		
	}

	
	static void example2(){
		SxRuntimeSettings SXSettings = new SxRuntimeSettings();
		SXSettings.MINIMUM_SIMULARITY = SxSIMULARITY.SOUNDEX_EQUAL;
		
		String ctrlWord = "rewrite";
		String testWord = "reward";

		
		
		SoundExExpression sxControl = new SoundExExpression(new Locale("en-us"));
		sxControl.compile(ctrlWord);
		
		SoundExExpression sxTest = new SoundExExpression(new Locale("en-us"));
		sxTest.compile(testWord);
		
		int[] test2ctrl= sxControl.compareExistence(sxTest, SxSIMULARITY.GROUP_SAME);
		System.out.println("test2ctrl: '" +sxControl.getOriginalWord()+"' -> '" +sxControl.getSoundExCode()+ "' :" + sxControl.UF_formatDifferneces(SXSettings, test2ctrl));
		
		int[] ctrl2test= sxTest.compareExistence(sxControl, SxSIMULARITY.GROUP_SAME);
		System.out.println("ctrl2test '"+sxTest.getOriginalWord()+"' -> '" +sxTest.getSoundExCode()+"' :" + sxTest.UF_formatDifferneces(SXSettings, ctrl2test ));

		
		
		System.out.println(outputFormat.formatMatched("Matched") + " " + outputFormat.formatNotMatched("Not Matched"));
		/**
		 * Output:
		 * test2ctrl: 'rewrite' -> 'R1R0D0' :(re)w(r)i(t)e
		 * ctrl2test 'reward' -> 'R1W0R0D0' :(re)[w]a(r)(d)
		 * (Matched) [Not Matched]
		 * 
		 * 
		 * Explained:
		 * re[w]ard -> w is silent in write
		 * rewri(t)e <-> rewar(d)  
		 * (re)ward <-> (re)write
		 * rewa(r)d <-> rew(r)ite
		 */
		
		
		
	}
	static void example1(){
		SxRuntimeSettings SXSettings = new SxRuntimeSettings();
		
		String ctrlWord = "rewrite";
		String testWord = "reward";

		
		
		SoundExExpression sxControl = new SoundExExpression(new Locale("en-us"));
		sxControl.compile(ctrlWord);
		
		SoundExExpression sxTest = new SoundExExpression(new Locale("en-us"));
		sxTest.compile(testWord);
		
		int[] test2ctrl= sxControl.compareExistence(sxTest, SxSIMULARITY.GROUP_SAME);
		System.out.println("test2ctrl: '" +sxControl.getOriginalWord()+"' -> '" +sxControl.getSoundExCode()+ "' :" + sxControl.UF_formatDifferneces(SXSettings, test2ctrl));
		
		int[] ctrl2test= sxTest.compareExistence(sxControl, SxSIMULARITY.GROUP_SAME);
		System.out.println("ctrl2test '"+sxTest.getOriginalWord()+"' -> '" +sxTest.getSoundExCode()+"' :" + sxTest.UF_formatDifferneces(SXSettings, ctrl2test ));

		
		
		System.out.println(outputFormat.formatMatched("Matched") + " " + outputFormat.formatNotMatched("Not Matched"));
		/**
		 * Output:
		 * test2ctrl: 'rewrite' -> 'R1R0D0' :(re)w(r)i(t)e
		 * ctrl2test 'reward' -> 'R1W0R0D0' :(re)[w]a(r)(d)
		 * (Matched) [Not Matched]
		 * 
		 * 
		 * Explained:
		 * re[w]ard -> w is silent in write
		 * rewri(t)e <-> rewar(d)  
		 * (re)ward <-> (re)write
		 * rewa(r)d <-> rew(r)ite
		 */
		
		
		
	}
	
	
	
	
	
	
	
	
}

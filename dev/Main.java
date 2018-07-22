package com.gabbytt.dev;
import java.util.Locale;

import com.gabbytt.soundex.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("What");
		SoundExExpression sx1 = new SoundExExpression(new Locale("en-us"));
		sx1.compile("Word");
		System.out.println(sx1.getSoundExCode());
		
		
		//.comapareSoundEx("shome", cpxRec);
		
	}

}

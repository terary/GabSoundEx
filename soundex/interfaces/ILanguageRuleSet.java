package com.gabbytt.soundex.interfaces;

import java.util.ArrayList;
import java.util.Locale;

public interface ILanguageRuleSet {
	Locale getLocale();
	float version() ;

	ArrayList<IRule> toArrayList(); // must be sorted by rule precedences
}

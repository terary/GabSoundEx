### Use case:
Speach-To-Text as a teaching utilitity

Teacher: "Can you say 'photography'?"
Student: "Phonography"
Teacher: "Very good student.  You have all the sounds but you need to work on your 'T' sound a little more.

### Purpose:

To help language students determine the quality their pronounciation.  Identify strengths and weakness, which sounds require more focus.

### Method:
Speach to Text output compared to some 'goal' text.  

### Supposition:
A word is an ordered collection (list) of sounds.

### Concern:
A &#8745; B
A - B
B - A
Set notation used only to describe the concern. This code deals with *lists* of sounds.



------------
#### Brief Explaination
Sounds are encapsulated as SoundBytes. Each SoundByte has a soundEx code (determined by encoding rules), something like: Foot->F0D0  and Phone->F2M0. Where the letter part is the soundEx group the number part is arbritary (according to encoding ruleset).  Together they form the soundEx code.

Output is mostly* determined by Encoding Ruleset.  Encoding behavior is effected by:
* MINIMUM_SIMULARITY
  * IGNORED(-1);     	// Not compared
  *	NOT_SIMILAR(0),  			// match anything to anything
  *	(default) GROUP_SAME(20),  	// 'F0' and 'F2' - match
  * SOUNDEX_EQUAL(30),  // 'F0' matches only 'F0'
  * STRING_EQUAL(40),   // Ultimately this will yeild a string compare

* compMethod:
   * STRICT  Out of order sounds are consider non-match
   * (default) EXISTENCE Matched if the sound exists in the collection 


* IgnoredMatching
   * NOT_MATCH ignore is considered non-match
   * (default) MATCH, ignore is considered match
   * IGNORE ignore is ignored


*Some encoding rule-sets will ignore some sounds. For example many English SoundXs ignore vowels. 'IgnoredMatching' determines if an ignored sound should be considered a match, non-match or ignored.  For generous  user feedback consider IGNORE as matching (because it's not non matching). For processing, seeking one sound in the other collection IGNORED are ignored.

All other output factors determining output is determined by the Encodeing Ruleset.

-----
Example:
		
		SXSettings.MINIMUM_SIMULARITY = SxSIMULARITY.SOUNDEX_EQUAL;
		SXSettings.uf_IgnoredMatching =SoundExExpression.UF_IgnoredMatching.IGNORE;
		
		String ctrlWord = "Photography";
		String testWord = "Phonograph";
		
		sxControl.compile(ctrlWord);
		sxTest.compile(testWord);
		
		int[] test2ctrl= sxControl.compareStrict(sxTest, SXSettings.MINIMUM_SIMULARITY);
		printResult("test2ctrl:",sxControl,test2ctrl);
		
		int[] ctrl2test= sxTest.compareStrict(sxControl, SXSettings.MINIMUM_SIMULARITY );
		printResult("ctrl2test:", sxTest,  ctrl2test);

Output:
(ph)o[t]o(g)(r)a(ph)y
(ph)o[n]o(g)(r)a(ph)

Hence, the words 'photography' and 'phonograph' differ in sound only by 'pho{t}ography' and 'pho{n}ograph'

*Very good little student!  Everything sounds great but you need to work on your T's a little more.
Encoding Ruleset desginates 'y' as a vowel and is ignored. 

-----
Example:

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

(re)(c)o(r)(d)
\[pre](re)(c)o(r)(d)e[d]

Hence the student is missing the 'Pre...' and the '.. ed' sounds.

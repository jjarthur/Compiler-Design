// COMP3290 CD Compiler
//
//	TokId enum -	Enumeration of Token Values for Scanner.
//		  	Ensures that the Token values are public to whole compiler.
//
//			The enumeration values are the ones expected to be printed as strings in part 1.
//
//			Enumeration values can be turned into Strings with t1.name().
//			Enumeration values are Comparable, i.e. t1.compareTo(t2) gives the usual answers,
//				   however != and == still work directly.
//
//	Rules of Use:   Please look at the rules for the released OutputController.
//			Similar rules apply here.
//
// M.Hannaford
// 28-Jul-2016
//		Modified:
//		4-Aug-2016	added some comments to fully explain the operator/delimiter token values
//

public enum TokId {	TEOF,   // End of File Token
    // The 31 keywords come first
    TCD, TCONS, TTYPS, TISKW, TARRS, TMAIN, TBEGN, TENDK, TARRY, TOFKW, TFUNC,
    TVOID, TCNST, TINTG, TREAL, TBOOL, TFORK, TREPT, TUNTL, TIFKW, TELSE, TINKW,
    TOUTP, TOUTL, TRETN, TNOTK, TANDK, TORKW, TXORK, TTRUE, TFALS,

    // then the operators and delimiters
    TLBRK, TRBRK, TLPAR, TRPAR, TSEMI, TCOMA, TCOLN, TDOTT, TASGN, TINPT,
    //			  [      ]      (      )      ;      ,      :      .      <<     >>
    TDEQL, TNEQL, TGRTR, TLEQL, TLESS, TGREQ, TADDT, TSUBT, TMULT, TDIVT, TPERC, TCART,
//			  ==     !=     >      <=     <      >=     +      -      *      /      %      ^

    // then the tokens which need tuple values
    TIDNT, TILIT, TFLIT, TSTRG, TUNDF	// TUNDF is only a pseudo-token-value
};

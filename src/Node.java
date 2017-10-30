//
//	COMP3290/6290 CD A3
//
//	Node - Enumerated Class - Syntax tree node values for TreeNode
//
//	M. Hannaford
//	21-Jul-2017 - Changed by P.Mahata for CD compiler
//
//			This is for Version 1.0 of CD Syntax.
//
//			I have managed to complete the parser using this enumeration, but you should still
//			  check it for omissions.
//			Remember that the idea is to build a picture of the structure of the input program.
//			Where a particular <id> is within the tree (ie its context) may decide exactly how
//			  it is to be interpreted.
//			MRH.
//

public enum Node {	NUNDEF, NPROG,	NGLOB,	NILIST,	NINIT,	NFUNCS, NMAIN,	NSDLST,	NTYPEL,	NRTYPE,
			NATYPE, NFLIST,	NSDECL,	NALIST,	NARRD,	NFUND,  NPLIST,	NSIMP,	NARRP,	NARRC,
			NDLIST, NSTATS,	NFORL,	NREPT,	NASGNS,	NIFTH,	NIFTE,	NASGN,	NINPUT,	NOUTP, NOUTL,
			NLINE,	NCALL,	NRETN,	NVLIST,	NSIMV, NAELT,	NARRV,	NEXPL,	NBOOL,	NNOT,	NAND,
			NOR,	NXOR,	NEQL,	NNEQ,	NGRT,	NLSS,	NLEQ,	NADD,	NSUB,	NMUL,
			NDIV,	NMOD,	NPOW,	NILIT,	NFLIT,	NTRUE,	NFALS,	NFCALL,	NPRLST,	NSTRG,	NGEQ  };
// COMP3290/6290 CD Compiler
//
// Symbol Table class - Contains all information reqd about a name.
//
// M.Hannaford
// 16-Jul-2005	as CD5 compiler's Symbol Table class
//
//	Modified:	20-Jul-2006	for CD6 compiler
//			10-Aug-2014	for CD14 compiler
//			01-Aug-2015	for CD15 compiler
//			15-Aug-2015	HashTableItem brought in as a
//					private inner class
//			23-Jul-2016	for CD16 compiler
//

public class HashTable {

	private static final int TABLESIZE = 97;

	private HashTableItem[] table;

	public HashTable() {
		table = new HashTableItem[TABLESIZE];
	}

	public int hash(String nm) {
		int h = 0;
		for (int i=0; i<nm.length(); i++)
			h = h + (int)nm.charAt(i);
		h %= TABLESIZE;
		return h;
	}

	public StRec lookupSymbol(String s) {
		int h = hash(s);
		HashTableItem x = table[h];
		while (x != null) {
			if ((x.getName()).equals(s)) return x.getData();
			x = x.getNext();
		}
		return null;
	}

	public StRec insertSymbol(String s, int ln) {
		StRec d = lookupSymbol(s);
		if (d != null) return null;
		int h = hash(s);
		d = new StRec(s,ln);
		table[h] = new HashTableItem(s,d,table[h]);
		return d;
	}

	public StRec insertSymbol(String s, int v, int ln) {
		StRec d = lookupSymbol(s);
		if (d != null) return null;
		int h = hash(s);
		d = new StRec(s,v,ln);
		table[h] = new HashTableItem(s,d,table[h]);
		return d;
	}
	public StRec insertSymbol(String s, double v, int ln) {
		StRec d = lookupSymbol(s);
		if (d != null) return null;
		int h = hash(s);
		d = new StRec(s,v,ln);
		table[h] = new HashTableItem(s,d,table[h]);
		return d;
	}

  // COMP3290/6290 CD Compiler
  //
  // Symbol Table Node class - Links StRec records into the hash table.
  //
  // M.Hannaford
  // 16-Jul-2005	as CD5 compiler's Symbol Table Node class
  //
  //	Modified:	20-Jul-2006	for CD6 compiler
  //			10-Aug-2014	for CD14 compiler
  //			01-Aug-2015	for CD15 compiler
  //			15-Aug-2015	altered to be a private inner class
  //					within HashTable - string consts now in
  //					st[3] within Parser.
  //			23-Jul-2016	for CD16 compiler
  //

  private class HashTableItem {

	private String name;
	private StRec data;
	private HashTableItem next;

	private HashTableItem(String s, StRec d, HashTableItem n) {
		name = s.substring(0);
		data = d;
		next = n;
	}

	private String getName() {
		return name;
	}

	private StRec getData() {
		return data;
	}

	private HashTableItem getNext() {
		return next;
	}
  }

}

package ZClasses;

public class ZPrologData {
	static final String ZPD_KEY = "zpd027k88zmqaa0";
	
	String filename;
	PrologFileReader pfr;
	PrologFileWriter pfw;
	
	ZCompressedArray<Fact> keyfacts;
	ZCompressedArray<Fact> facts;
	
	public ZPrologData() {
		filename = null;
		pfr = null;
		keyfacts = new ZCompressedArray<Fact>(10);
		facts = new ZCompressedArray<Fact>(10);
	}
	
	public ZPrologData(String file) {
		filename = file;
		pfr = new PrologFileReader(filename, this);
		pfw = new PrologFileWriter(filename, this);
		keyfacts = new ZCompressedArray<Fact>(10);
		facts = new ZCompressedArray<Fact>(10);
	}
	
	public boolean addFact(String pred, String[] args, boolean key, boolean unique) {
		Fact fact;
		if (key) {
			if (isKeyPredicate(pred))
				fact = new Fact(pred, args);
			else
				fact = new Fact(ZPD_KEY + pred, args);
			if (existsInKeyFacts(fact))
				return false;
			keyfacts.add(fact);
		}
		else {
			if (isKeyPredicate(pred))
				return false;
			fact = new Fact(pred, args);
			if ((unique)&&(existsInFacts(fact)))
				return false;
			facts.add(fact);
		}
		return true;
	}
	
	public boolean removeFact(Fact fact) {
		return facts.remove(fact);
	}
	
	public boolean existsInFacts(String predicate, int argnum) {
		if (facts.size() == 0)
			return false;
		else {
			for (int i = 0; i < facts.size(); i++) {
				if ((facts.get(i).predicate.equals(predicate))&&(facts.get(i).arguments.length == argnum))
					return true;
			}
			return false;
		}
	}
	
	public boolean existsInFacts(Fact fact) {
		if (facts.size() == 0)
			return false;
		else {
			for (int i = 0; i < facts.size(); i++) {
				if (compareFacts(facts.get(i), fact) == 0)
					return true;
			}
			return false;
		}
	}
	
	public boolean existsInKeyFacts(String predicate, int argnum) {
		if (keyfacts.size() == 0)
			return false;
		else {
			for (int i = 0; i < keyfacts.size(); i++) {
				if ((keyfacts.get(i).predicate.equals(predicate))&&(keyfacts.get(i).arguments.length == argnum))
					return true;
				else if ((keyfacts.get(i).predicate.equals(ZPD_KEY + predicate))&&(keyfacts.get(i).arguments.length == argnum))
					return true;
			}
			return false;
		}
	}
	
	public boolean existsInKeyFacts(Fact fact) {
		if (keyfacts.size() == 0)
			return false;
		else {
			for (int i = 0; i < keyfacts.size(); i++) {
				if ((keyfacts.get(i).predicate.equals(fact.predicate))&&(keyfacts.get(i).arguments.length == fact.arguments.length))
					return true;
			}
			return false;
		}
	}
	
	public Fact findKey(String pred, int argnum) {
		for (int i = 0; i < keyfacts.size(); i++) {
			if ((keyfacts.get(i).predicate.equals(pred))&&(keyfacts.get(i).arguments.length == argnum))
				return keyfacts.get(i);
			else if ((keyfacts.get(i).predicate.equals(ZPD_KEY + pred))&&(keyfacts.get(i).arguments.length == argnum))
				return keyfacts.get(i);
		}
		return null;
	}
	
	public int compareFacts(Fact fact1, Fact fact2) {
		int result = fact1.predicate.compareTo(fact2.predicate);
		if (result != 0)
			return result;
		else if (fact1.arguments.length != fact2.arguments.length) {
			if (fact1.arguments.length < fact2.arguments.length)
				return -1;
			else
				return 1;
		}
		else {
			for (int i = 0; i < fact1.arguments.length; i++) {
				try {
					result = Integer.compare(Integer.parseInt(fact1.arguments[i]), Integer.parseInt(fact2.arguments[i]));
				} catch (Exception e) {
					result = fact1.arguments[i].compareTo(fact2.arguments[i]);
				}
				if (result != 0)
					return result;
			}
			return 0;
		}
	}
	
	public void changeFile(String file) {
		if (file != null) {
			filename = file;
			pfr = new PrologFileReader(filename, this);
			pfw = new PrologFileWriter(filename, this);
		}
		else {
			filename = null;
			pfr = null;
			pfw = null;
		}
	}
	
	public boolean loadData(boolean uniquefacts) {
		if (pfr != null) {
			return pfr.readFile(uniquefacts);
		}
		return false;
	}
	
	public boolean saveData() {
		if (pfw != null) {
			return pfw.writeFile();
		}
		return false;
	}
	
	public void printData() {
		for (int i = 0; i < keyfacts.size(); i++)
			System.out.println(keyfacts.get(i).toString());
		for (int i = 0; i < facts.size(); i++)
			System.out.println(facts.get(i).toString());
	}
	
	public Fact[] getFacts() {
		Fact[] list = new Fact[facts.size()];
		for (int i = 0; i < facts.size(); i++)
			list[i] = facts.get(i);
		return list;
	}
	
	public Fact[] getKeyFacts() {
		Fact[] list = new Fact[keyfacts.size()];
		for (int i = 0; i < keyfacts.size(); i++)
			list[i] = keyfacts.get(i);
		return list;
	}
	
	public static String getPredicateOfFact(String fact) {
		int index = 0;
		while ((fact.charAt(index) != '(')&&(index < fact.length()))
			index++;
		if (index < fact.length())
			return fact.substring(0, index);
		return null;
	}
	
	public static String[] getArgumentsOfFact(String fact) {
		int index = 0;
		String body = null;
		while ((index < fact.length())&&(fact.charAt(index) != '('))
			index++;
		if (index < fact.length()) {
			fact = fact.substring(++index);
			index = 0;
			while((index < fact.length())&&(fact.charAt(index) != ')'))
				index++;
			if (index < fact.length())
				body = fact.substring(0, index);
		}
		if (body == null)
			return null;
		//body = body.replaceAll(" ", "");
		ZCompressedArray<String> args = new ZCompressedArray<String>(5);
		index = 0;
		boolean instring = false;
		while (index < body.length()) {
			if (body.charAt(index) == '"')
				instring = !instring;
			if ((!instring)&&(index < body.length() - 1)&&(body.substring(index, index + 2).equals(", "))) {
				args.add(body.substring(0, index++));
				body = body.substring(++index);
				index = -1;
			}
			index++;
		}
		args.add(body.substring(0, index));
		String[] output = new String[args.size()];
		for (int i = 0; i < args.size(); i++)
			output[i] = args.get(i);
		return output;
	}
	
	public void sortData() {
		sortByPredicate(true);
		sortByPredicate(false);
		
		String currentpred;
		int currentargnum;
		int left = 0;
		int right = 0;
		while (left < facts.size()) {
			currentpred = facts.get(left).predicate;
			currentargnum = facts.get(left).arguments.length;
			while ((right < facts.size())&&(facts.get(right).predicate.compareTo(currentpred) == 0)&&(facts.get(right).arguments.length == currentargnum)) {
				right++;
				//System.out.println(right);
			}
			for (int j = currentargnum - 1; j > -1; j--) {
				for (int i = left; i < right - 1; i++) {
					if (facts.get(i).arguments[j].compareTo(facts.get(i + 1).arguments[j]) > 0) {
						facts.swap(i, i + 1);
						if (i != left)
							i -= 2;
					}
				}
			}
			left = right++;
			//System.out.println("Completed set.");
		}
	}
	
	public Fact[] search(String pred, String[] args) {
		if ((pred == null)||(args == null))
			return getFacts();
		ZCompressedArray<Fact> query = new ZCompressedArray<Fact>(facts.size());
		boolean match;
		for (int i = 0; i < facts.size(); i++) {
			if ((facts.get(i).predicate.equalsIgnoreCase(pred))&&(facts.get(i).arguments.length == args.length)) {
				if ((args == null)||(args.length == 0))
					query.add(facts.get(i));
				else {
					match = true;
					for (int j = 0; j < args.length; j++) {
						if ((args[j] != null)&&(!facts.get(i).arguments[j].toLowerCase().contains(args[j].toLowerCase())))
							match = false;
					}
					if (match)
						query.add(facts.get(i));
				}
			}
		}
		Fact[] result = new Fact[query.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = query.get(i);
		return result;
	}
	
	private void sortByPredicate(boolean sortkeys) {
		ZCompressedArray<Fact> list;
		if (sortkeys)
			list = keyfacts;
		else
			list = facts;
		for (int i = 0; i < list.size() - 1; i++) {
			if (list.get(i).predicate.compareTo(list.get(i + 1).predicate) > 0) {
				list.swap(i, i + 1);
				if (i != 0)
					i -= 2;
			}
			else if ((list.get(i).predicate.compareTo(list.get(i + 1).predicate) == 0)&&(list.get(i).arguments.length > list.get(i + 1).arguments.length)) {
				list.swap(i, i + 1);
				if (i != 0)
					i -= 2;
			}
		}
	}
	
	public static boolean isKeyPredicate(String pred) {
		if (pred.length() > ZPD_KEY.length())
			return pred.substring(0, ZPD_KEY.length()).equals(ZPD_KEY);
		return false;
	}
	
	public static boolean isKeyFact(Fact fact) {
		if (fact.predicate.length() > ZPD_KEY.length())
			return fact.predicate.substring(0, ZPD_KEY.length()).equals(ZPD_KEY);
		return false;
	}
	
	public static String removeKeyFromPredicate(String pred) {
		return pred.substring(ZPD_KEY.length());
	}
	
	public class Fact {
		public String predicate;
		public String[] arguments;
		
		public Fact(String pred, String[] args) {
			predicate = pred;
			arguments = args;
		}
		
		public String getArgumentsInForm() {
			String output = "";
			for (int i = 0; i < arguments.length; i++) {
				output += arguments[i];
				if (i != arguments.length - 1)
					output += ", ";
			}
			return output;
		}
		
		public String toString() {
			String output = predicate + " { ";
			for (int i = 0; i < arguments.length; i++) {
				output += arguments[i];
				if (i != arguments.length - 1)
					output += ",";
				output += " ";
			}
			output += "}.";
			return output;
		}
		
		public String toDisplayString() {
			return "<<< KEY >>> " + removeKeyFromPredicate(toString());
		}
	}
	
	private class PrologFileReader extends ZFileReader {
		String filename;
		ZPrologData owner;
		
		public PrologFileReader(String file, ZPrologData owner) {
			filename = file;
			this.owner = owner;
		}
		
		public boolean readFile(boolean unique) {
			if (openFile(filename)) {
				String line;
				while (getScanner().hasNext()) {
					line = getScanner().nextLine();
					if ((ZPrologData.getPredicateOfFact(line) != null)&&(ZPrologData.getArgumentsOfFact(line) != null)) {
						owner.addFact(ZPrologData.getPredicateOfFact(line), ZPrologData.getArgumentsOfFact(line), false, unique);
						//TODO: Improve this.
						owner.addFact(ZPrologData.getPredicateOfFact(line), ZPrologData.getArgumentsOfFact(line), true, unique);
					}
					else
						System.out.println("Error caught for: " + line);
				}
				return true;
			}
			return false;
		}
	}
	
	private class PrologFileWriter extends ZFileWriter {
		String filename;
		ZPrologData owner;
		
		public PrologFileWriter(String file, ZPrologData owner) {
			filename = file;
			this.owner = owner;
		}
		
		public boolean writeFile() {
			if (openFile(filename)) {
				for (int i = 0; i < owner.keyfacts.size(); i++)
					getFormatter().format("%s(%s).\n", owner.keyfacts.get(i).predicate, owner.keyfacts.get(i).getArgumentsInForm());
				for (int i = 0; i < owner.facts.size(); i++)
					getFormatter().format("%s(%s).\n", owner.facts.get(i).predicate, owner.facts.get(i).getArgumentsInForm());
				closeFile();
				return true;
			}
			else
				return false;
		}
	}
}

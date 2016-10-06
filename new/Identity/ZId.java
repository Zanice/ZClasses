package ZClasses.Identity;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Random;

public class ZId {
	private static final int ID_STRING_SECTION_COUNT = 6;
	private static final int ID_STRING_SECTION_LENGTH = 2;
	private static final int RNG_BOUND = 62;
	private static final int[][] CHAR_RANGES = new int[][] {
		{ 48, 57 },
		{ 65, 90 },
		{ 97, 122 }
	};
	
	private static LinkedList<ZId> generatedIds = new LinkedList<ZId>();
	
	private final String id;
	
	private ZId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object other) {
		try {
			ZId otherId = (ZId) other;
			return id.equals(otherId.id);
		} catch (ClassCastException cce) {
			return false;
		}
	}
	
	public static ZId generateNewId() {
		return new ZId(generateUniqueIdString());
	}
	
	public static String generateUniqueIdString() {
		// generate a random id string
		String attemptedId = null;
		boolean uniqueId = false;
		
		while (!uniqueId) {
			attemptedId = generateIdString();
			
			uniqueId = true;
			for (ZId existingId : generatedIds) {
				if (attemptedId.equals(existingId))
					uniqueId = false;
			}
		}
		
		return attemptedId;
	}
	
	public static String generateIdString() {
		Random rng = new Random();
		String id = "";
		int nextRoot;
		
		for (int sectionNum = 0; sectionNum < ID_STRING_SECTION_COUNT; sectionNum++) {
			for (int charNum = 0; charNum < ID_STRING_SECTION_LENGTH; charNum++) {
				nextRoot = rng.nextInt(62);
				try {
					id += characterFromRootInt(nextRoot);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			
			if (sectionNum < ID_STRING_SECTION_COUNT - 1)
				id += "-";
		}
		
		return id;
	}
	
	public static char characterFromRootInt(int root) throws Exception {
		if (root < 0 || root >= RNG_BOUND)
			throw new Exception("CANNOT USE ROOT FOR ID GENERATION");
		
		int prevRangeEnd = 0;
		for (int rangeIndex = 0; rangeIndex < CHAR_RANGES.length; rangeIndex++) {
			if (root < CHAR_RANGES[rangeIndex][0])
				root += CHAR_RANGES[rangeIndex][0] - prevRangeEnd;
			if (root <= CHAR_RANGES[rangeIndex][1])
				break;
			prevRangeEnd = CHAR_RANGES[rangeIndex][1] + 1;
		}
		
		return (char) root;
	}
}

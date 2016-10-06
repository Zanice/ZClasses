package ZClasses;

import java.security.InvalidParameterException;

public class ZNumberSystems {
	public static char valueToHex(int val) {
		if (val > -1) {
			if (val < 10)
				return (char) (val + 48);
			if (val < 16)
				return (char) (55 + val);
		}
		throw new InvalidParameterException();
	}
	
	public static int hexToValue(char hex) {
		try {
			return Integer.parseInt(hex + "");
		} catch (Exception e) {
			int val = Character.getNumericValue(hex) - 55;
			if ((val > 9)&&(val < 16))
				return val;
		}
		throw new InvalidParameterException();
	}
	
	public static int compareHex(String hex1, String hex2) {
		int result = Integer.compare(hexToValue(hex1.charAt(0)), hexToValue(hex2.charAt(0)));
		if (result != 0) {
			if (result < 0)
				return -1;
			else
				return 1;
		}
		if ((hex1.length() > 0)&&(hex2.length() > 0))
			return compareHex(hex1.substring(1), hex2.substring(1));
		return Integer.compare(hex1.length(), hex2.length());
	}
}

package ZClasses;

import java.util.Random;

public class ZIdGenerator {
	private ZCompressedArray<String> generated;
	
	public ZIdGenerator() {
		generated = new ZCompressedArray<String>(10);
	}
	
	public String newId() {
		String id = generateId();
		while (generated.contains(id)) {
			id = generateId();
		}
		generated.add(id);
		return id;
	}
	
	public void openId(String id) {
		generated.remove(id);
	}
	
	private String generateId() {
		String id = "";
		Random ran = new Random();
		while (id.length() != 18) {
			id += ZNumberSystems.valueToHex(ran.nextInt(16));
			if ((id.length() == 6)||(id.length() == 9))
				id += "-";
		}
		return id;
	}
}

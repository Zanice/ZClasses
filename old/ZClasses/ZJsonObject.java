package ZClasses;

import java.util.ArrayList;

public class ZJsonObject {
	public JsonElement jsonData;
	
	public ZJsonObject(String data) {
		data = data.trim();
		jsonData = new JsonElement("_JSONDATA", constructRec(getDelimeterContents(data, '{', '}')));
	}
	
	private JsonElement[] constructRec(String data) {
		data = data.trim();
		ArrayList<JsonElement> entries = new ArrayList<JsonElement>();
		int index = 0, brackdepth = 0, layerdepth = 0;
		while (index < data.length()) {
			switch (data.charAt(index)) {
			case ',':
				if ((brackdepth == 0)&&(layerdepth == 0)) {
					entries.add(parseEntry(data.substring(0, index)));
					data = data.substring(index + 1);
					index = -1;
				}
				break;
			case '[':
				brackdepth++;
				break;
			case ']':
				brackdepth--;
				break;
			case '{':
				layerdepth++;
				break;
			case '}':
				layerdepth--;
				break;
			}
			index++;
		}
		entries.add(parseEntry(data.substring(0, index)));
		
		JsonElement[] result = new JsonElement[entries.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = entries.get(i);
		return result;
	}
	
	private JsonElement parseEntry(String entry) {
		if (entry.length() == 0)
			return null;
		
		String label = "";
		String value = "";
		int index = 0;
		while (index < entry.length()) {
			if (entry.charAt(index) == ':') {
				label = entry.substring(0, index);
				value = entry.substring(index + 1);
				break;
			}
			index++;
		}
		if (index == entry.length())
			throw new IllegalArgumentException();
		
		label = getDelimeterContents(label, '"', '"');
		if (value.length() > 0) {
			switch (value.charAt(0)) {
			case '"':
				return new JsonElement(label, getDelimeterContents(value, '"', '"'));
			case '{':
				return new JsonElement(label, constructRec(getDelimeterContents(value, '{', '}')));
			case '[':
				value = getDelimeterContents(value, '[', ']');
				if ((value.length() > 0)&&(value.charAt(0) == '{')) {
					JsonElement[] list = parseJsonElements(value);
					return new JsonElement(label, list);
				}
				else {
					JsonElement[] list = parseStrings(value);
					return new JsonElement(label, list);
				}
			}
		}
		return new JsonElement(label, value);
	}
	
	private JsonElement[] parseStrings(String entry) {
		ArrayList<JsonElement> list = new ArrayList<JsonElement>();
		int index = 0;
		int listelem = 0;
		while (index < (entry = entry.trim()).length()) {
			if (entry.charAt(index) == ',') {
				list.add(new JsonElement("LISTELEM" + listelem, entry.substring(0, index)));
				entry = entry.substring(index + 1);
				index = -1;
				listelem++;
			}
			index++;
		}
		list.add(new JsonElement("LISTELEM" + index, entry));
		
		JsonElement[] result = new JsonElement[list.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = list.get(i);
		return result;
	}
	
	/*
	private String[] parseToStringList(String entry) {
		ArrayList<String> list = new ArrayList<String>();
		int index = 0;
		while (index < (entry = entry.trim()).length()) {
			if (entry.charAt(index) == ',') {
				list.add(entry.substring(0, index));
				entry = entry.substring(index + 1);
				index = -1;
			}
			index++;
		}
		list.add(entry);
		
		String[] result = new String[list.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = list.get(i);
		return result;
	}
	*/
	
	private JsonElement[] parseJsonElements(String entry) {
		ArrayList<JsonElement> list = new ArrayList<JsonElement>();
		int listelem = 0;
		while ((entry = entry.trim()).length() > 0) {
			if (entry.charAt(0) == ',')
				entry = entry.substring(1).trim();
			list.add(new JsonElement("LISTELEM" + listelem, constructRec(getDelimeterContents(entry, '{', '}'))));
			entry = removeFirstDelimeterSet(entry, '{', '}');
			listelem++;
		}
		
		JsonElement[] result = new JsonElement[list.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = list.get(i);
		return result;
	}
	
	/*
	private JsonElement[][] parseToJsonElementList(String entry) {
		ArrayList<JsonElement[]> list = new ArrayList<JsonElement[]>();
		int index = 0;
		entry = entry.replace(',', ' ');
		while ((entry = entry.trim()).length() > 0) {
			list.add(constructRec(getDelimeterContents(entry, '{', '}')));
			entry = removeFirstDelimeterSet(entry, '{', '}');
		}
		
		JsonElement[][] result = new JsonElement[list.size()][];
		for (int i = 0; i < result.length; i++)
			result[i] = list.get(i);
		return result;
	}
	*/
	
	private String getDelimeterContents(String text, char leftDel, char rightDel) {
		int index = 0, depth = 0, left;
		while (index < text.length()) {
			if (text.charAt(index) == leftDel) {
				depth = 1;
				left = ++index;
				while (index < text.length()) {
					if (text.charAt(index) == rightDel) {
						depth--;
						if (depth == 0)
							return text.substring(left, index);
					}
					else if (text.charAt(index) == leftDel)
						depth++;
					index++;
				}
				throw new IllegalArgumentException();
			}
			index++;
		}
		
		return text;
	}
	
	private String removeFirstDelimeterSet(String text, char leftDel, char rightDel) {
		int index = 0, depth = 0;
		while (index < text.length()) {
			if (text.charAt(index) == leftDel) {
				depth = 1;
				index++;
				while (index < text.length()) {
					if (text.charAt(index) == rightDel) {
						depth--;
						if (depth == 0)
							return text.substring(index + 1).trim();
					}
					else if (text.charAt(index) == leftDel)
						depth++;
					index++;
				}
				throw new IllegalArgumentException();
			}
			index++;
		}
		
		return text;
	}
	
	public class JsonElement {
		private String label;
		private Object value;
		
		public JsonElement(String label, String value) {
			initialize(label, value);
		}
		
		public JsonElement(String label, JsonElement[] value) {
			initialize(label, value);
		}
		
		private void initialize(String label, Object value) {
			this.label = label;
			this.value = value;
		}
		
		public boolean valueIsJsonElementArray() {
			return value.getClass().equals(JsonElement[].class);
		}
		
		public boolean valueIsString() {
			return value.getClass().equals(String.class);
		}
		
		public JsonElement subvalue(String label) {
			if (valueIsJsonElementArray()) {
				JsonElement[] values = (JsonElement[]) value;
				for (int i = 0; i < values.length; i++) {
					if (values[i].label.equals(label))
						return values[i];
				}
			}
			return null;
		}
		
		public JsonElement index(int index) {
			if (valueIsJsonElementArray()) {
				JsonElement[] values = (JsonElement[]) value;
				int current = 0;
				while (current <= index) {
					if (!values[current].label.equals("LISTELEM" + current))
						return null;
				}
				return values[current];
			}
			return null;
		}
		
		public String value() {
			if (valueIsString())
				return (String) value;
			return null;
		}
		
		public void printElement() {
			System.out.println("\tTest.");
		}
	}
}

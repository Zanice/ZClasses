package ZClasses;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ZTextFields {
	private ZDisplay reference;
	
	private TextField[] textfields;
	private int size;
	
	public ZTextFields(ZDisplay parent) {
		reference = parent;
		textfields = new TextField[1];
		size = 0;
	}
	
	public int numberOfTextFields() { return size; }
		
	private void addCapacity(int i) {
		TextField[] copy = textfields;
		textfields = new TextField[textfields.length + i];
		for (int index = 0; index < size; index++)
			textfields[index] = copy[index];
	}
	
	public void addNewTextField(int assignedID, String text, boolean editable, boolean tabbable, int x, int y, int width, int height) {
		TextField tf = new TextField(assignedID, text, false, editable, tabbable, x, y, width, height);
		if (size == textfields.length)
			addCapacity(1);
		tf.id = size;
		textfields[size] = tf;
		reference.add(tf.field);
		size++;
	}
	
	public void addNewLockedTextField(int assignedID, String text, boolean editable, boolean tabbable, int x, int y, int width, int height) {
		TextField tf = new TextField(assignedID, text, true, editable, tabbable, x, y, width, height);
		if (size == textfields.length)
			addCapacity(1);
		tf.id = size;
		textfields[size] = tf;
		reference.add(tf.field);
		size++;
	}
		
	public TextField get(int textfieldID) throws NullPointerException {
		if (size != 0) {
			if ((textfieldID >= 0)&&(textfieldID < size)) 
				return textfields[textfieldID];
			else 
				throw new IndexOutOfBoundsException();
		}
		else throw new NullPointerException();
	}
	
	public int idOf(int assignedID) {
		int index = 0;
		while (index < size) {
			if (textfields[index].idA == assignedID)
				break;
			index++;
		}
		return index;
	}
	
	public void removeAllDeclaredAfterID(int assignedID) {
		int id = idOf(assignedID);
		id++;
		while (id < size) {
			reference.remove(textfields[id].field);
			textfields[id++] = null;
		}
		size -= id - idOf(assignedID) - 1;
	}
	
	public void paintTextFields(Graphics g) {
		for (int i = 0; i < size; i++) {
			textfields[i].field.setBounds(textfields[i].x, textfields[i].y, textfields[i].width, textfields[i].height);
		}
	}
	
	public void clearTextFields() {
		textfields = new TextField[1];
		size = 0;
	}
	
	public class TextField {
		private JTextField field;
		private int id;
		private int idA;
		private boolean locked;
		private boolean editable;
		private boolean tabbable;
		private int x;
		private int y;
		private int width;
		private int height;
		
		public TextField(int assignedID, String text, boolean locked, boolean editable, boolean tabbable, int x, int y, int width, int height) {
			idA = assignedID;
			this.locked = locked;
			this.editable = editable;
			this.tabbable = tabbable;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			
			field = new JTextField();
			field.setLayout(null);
			field.setBounds(x, y, width, height);
			field.setVisible(true);
			field.setEditable(editable);
			if (text != null)
				field.setText(text);
			field.setFocusable(tabbable);
			field.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					;
				}

				public void insertUpdate(DocumentEvent e) {
					reference.textFieldTypeEvent(id, idA, field.getText(), true);
				}

				public void removeUpdate(DocumentEvent e) {
					reference.textFieldTypeEvent(id, idA, field.getText(), false);
				}
				
			});
			field.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reference.textFieldEnterEvent(id, idA, field.getText());
				}
			});
		}
		
		public int getID() { return id; }
		
		public int getAssignedID() { return idA; }
		public void setAssignedID(int i) { idA = i; } 
		
		public boolean isLocked() { return locked; }
		public void setLocked(boolean b) { locked = b; }
		
		public boolean isEditable() { return editable; }
		public void setEditable(boolean b) { 
			editable = b;
			field.setEditable(b);
		}
		
		public int getX() { return x; }
		public void setX(int i) { x = i; }
		
		public int getY() { return y; }
		public void setY(int i) { y = i; }
		
		public int getWidth() { return width; }
		public void setWidth(int i) { width = i; }
		
		public int getHeight() { return height; }
		public void setHeight(int i) { height = i; }
		
		public String getText() { return field.getText(); }
		public void setText(String text) { field.setText(text); }
		
		public void makeFocused() {
			field.grabFocus();
		}
	}
}

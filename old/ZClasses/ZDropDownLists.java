package ZClasses;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class ZDropDownLists {
	private ZDisplay reference;
	
	private DropDownList[] dropdowns;
	private int size;
	
	public ZDropDownLists(ZDisplay parent) {
		reference = parent;
		dropdowns = new DropDownList[1];
		size = 0;
	}
	
	public int numberOfDropDownLists() { return size; }
		
	private void addCapacity(int i) {
		DropDownList[] copy = dropdowns;
		dropdowns = new DropDownList[dropdowns.length + i];
		for (int index = 0; index < size; index++)
			dropdowns[index] = copy[index];
	}
	
	public void addNewDropDownList(int assignedID, Object[] options, int cells, int x, int y, int width, int height) {
		DropDownList<Object> ddl = new DropDownList<Object>(assignedID, options, false, cells, x, y, width, height);
		if (size == dropdowns.length)
			addCapacity(1);
		ddl.id = size;
		dropdowns[size] = ddl;
		reference.add(ddl.list);
		size++;
	}
	
	public void addNewLockedDropDownList(int assignedID, Object[] options, int cells, int x, int y, int width, int height) {
		DropDownList<Object> ddl = new DropDownList<Object>(assignedID, options, true, cells, x, y, width, height);
		if (size == dropdowns.length)
			addCapacity(1);
		ddl.id = size;
		dropdowns[size] = ddl;
		reference.add(ddl.list);
		size++;
	}
		
	public DropDownList get(int textfieldID) throws NullPointerException {
		if (size != 0) {
			if ((textfieldID >= 0)&&(textfieldID < size)) 
				return dropdowns[textfieldID];
			else 
				throw new IndexOutOfBoundsException();
		}
		else throw new NullPointerException();
	}
	
	public int IDOf(int assignedID) {
		int index = 0;
		while (index < size) {
			if (dropdowns[index].idA == assignedID)
				break;
			index++;
		}
		return index;
	}
	
	public void removeAllDeclaredAfterID(int assignedID) {
		int id = IDOf(assignedID);
		id++;
		while (id < size) {
			reference.remove(dropdowns[id].list);
			dropdowns[id++] = null;
		}
		size -= id - IDOf(assignedID) - 1;
	}
	
	public void paintDropDownLists(Graphics g) {
		for (int i = 0; i < size; i++) {
			dropdowns[i].list.setBounds(dropdowns[i].x, dropdowns[i].y, dropdowns[i].width, dropdowns[i].height);
		}
	}
	
	public void clearDropDownLists() {
		dropdowns = new DropDownList[1];
		size = 0;
	}
	
	public class DropDownList<T> {
		private JComboBox<T> list;
		private int id;
		private int idA;
		private boolean locked;
		private int x;
		private int y;
		private int width;
		private int height;
		
		public DropDownList(int assignedID, T[] options, boolean locked, int cells, int x, int y, int width, int height) {
			idA = assignedID;
			this.locked = locked;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			
			list = new JComboBox<T>(options);
			list.setLayout(null);
			list.setBounds(x, y, width, height);
			list.setSelectedIndex(-1);
			list.setVisible(true);
			list.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					reference.dropDownSelectionEvent(id, idA, list.getSelectedIndex(), list.getSelectedItem());
				}
			});
		}
		
		public int getID() { return id; }
		
		public int getAssignedID() { return idA; }
		public void setAssignedID(int i) { idA = i; } 
		
		public boolean isLocked() { return locked; }
		public void setLocked(boolean b) { locked = b; }
		
		public int getX() { return x; }
		public void setX(int i) { x = i; }
		
		public int getY() { return y; }
		public void setY(int i) { y = i; }
		
		public int getWidth() { return width; }
		public void setWidth(int i) { width = i; }
		
		public int getHeight() { return height; }
		public void setHeight(int i) { height = i; }
		
		public Object getSelectedOption() { return list.getSelectedItem(); }
		
		public int getSelectedIndex() { return list.getSelectedIndex(); }
		
		public void changeOptions(T[] newOptions) {
			list.removeAllItems();
			for (T item : newOptions) {
				list.addItem(item);
			}
		}
	}
}

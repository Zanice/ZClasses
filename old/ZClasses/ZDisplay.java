package ZClasses;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*	ABSTRACT CLASS ZDisplay
 * 	@Zanice
 * 
 * 	This class manages a unique JPanel for painting images, along with
 * 	holding usable buttons and fields. Most methods are abstract, to be
 * 	defined through inheritance depending on the desired use.
 */

public abstract class ZDisplay extends JPanel {
	//Window container variable.
	private JFrame window;
	
	//Input handler variables.
	private ZMouse m;
	private ZKeyboard k;
	
	//Interface manager variables.
	private ZButtons b;
	private ZFields f;
	private ZTextFields tf;
	private ZDropDownLists ddl;
	
	//Display dimension variables.
	private int dimx;
	private int dimy;
	
	//Offset variables, used for scenarios like a scrolling screen. 
	private int oX;
	private int oY;
	
	public ZDisplay(int dimX, int dimY) {
		//Initialize the variables, and configure inputs and display.
		super();
		
		window = null;
		oX = 0;
		oY = 0;
		b = new ZButtons(this);
		f = new ZFields(this);
		tf = new ZTextFields(this);
		ddl = new ZDropDownLists(this);
		m = new ZMouse();
		m.configure(this);
		k = new ZKeyboard();
		k.configure(this);
		addMouseListener(m);
		addMouseMotionListener(m);
		addKeyListener(k);
		setFocusable(true);
		setEnabled(true);
		
		if (dimX >= 0)
			dimx = dimX;
		if (dimY >= 0)
			dimy = dimY;
		
		setup();
	}
	
	//Get methods for the variables of ZDisplay.
	public JFrame getWindow() { return window; }
	public ZMouse getMouseListener() { return m; }
	public ZKeyboard getKeyListener() { return k; }
	public ZButtons getButtons() { return b; }
	public ZFields getFields() { return f; }
	public ZTextFields getTextFields() { return tf; }
	public ZDropDownLists getDropDownLists() { return ddl; }
	public int getXDimension() { return dimx; }
	public int getYDimension() { return dimy; }
	public int getXOffset() { return oX; }
	public int getYOffset() { return oY; }
	
	public ZWindow getZWindow() {
		try {
			return (ZWindow) window;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void setWindow(JFrame window) {
		this.window = window;
	}
	
	//Set variables for the offsets.
	public void setXOffset(int x) { oX = x; }
	public void setYOffset(int y) { oY = y; } 
	
	public void paintComponent(Graphics g) {
		paintDisplay(g);
		
		getButtons().paintButtons(g);
		getFields().paintFields(g);
		getTextFields().paintTextFields(g);
		getDropDownLists().paintDropDownLists(g);
	}
	
	//Abstract methods, to be implemented by subclass.
	public abstract void setup();
	public abstract void paintDisplay(Graphics g);
	public abstract void paintElements(Graphics g, Object obj, int x, int y, int fieldID, int fieldAssignedID);
	public abstract void buttonEvent(int buttonID, int assignedID);
	public abstract void fieldEvent(int fieldID, int assignedID);
	public abstract void textFieldTypeEvent(int textfieldID, int assignedID, String text, boolean insertion);
	public abstract void textFieldEnterEvent(int textfieldID, int assignedID, String text);
	public abstract void dropDownSelectionEvent(int dropdownlistID, int assignedID, int index, Object item);
	public abstract void typeEvent(boolean pressed, int keycode);
	public abstract void onResize();
}

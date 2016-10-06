package ZClasses.Interface;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import ZClasses.Interface.ZDisplay.Area;
import ZClasses.Interface.ZDisplay.Button;
import ZClasses.Interface.ZDisplay.Component;
import ZClasses.Interface.ZDisplay.ComponentType;

public class ZMouseActionHandler implements MouseListener, MouseMotionListener {
	private ZDisplay host;
	
	private int mouseX;
	private int mouseY;
	private int eventX;
	private int eventY;
	
	private Button mousedButton;
	private Button pressedButton;
	
	private Area mousedArea;
	private Area pressedArea;
	
	public ZMouseActionHandler() {
		super();
	}
	
	public ZDisplay getHost() {
		return host;
	}
	
	public void configure(ZDisplay host) {
		this.host = host;
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}
	
	public int getEventX() {
		return eventX;
	}
	
	public int getEventY() {
		return eventY;
	}
	
	public Component getMoused(ComponentType type) {
		switch (type) {
			case BUTTON:
				return mousedButton;
			case AREA:
				return mousedArea;
			default:
				return null;
		}
	}

	public Component getPressed(ComponentType type) {
		switch (type) {
			case BUTTON:
				return pressedButton;
			case AREA:
				return pressedArea;
			default:
				return null;
		}
	}
	
	public void updateMousedComponents() {
		mousedButton = (Button) host.getOccupancy(ComponentType.BUTTON, mouseX, mouseY);
		mousedArea = (Area) host.getOccupancy(ComponentType.AREA, mouseX, mouseY);
	}
	
	@Override
	public void mouseMoved(MouseEvent me) {
		mouseX = me.getX();
		mouseY = me.getY();

		updateMousedComponents();

		host.repaint();
	}

	@Override
	public void mousePressed(MouseEvent me) {
		mouseX = eventX = me.getX();
		mouseY = eventY = me.getY();

		if (mousedButton != null)
			pressedButton = mousedButton;
		if (mousedArea != null)
			pressedArea = mousedArea;

		updateMousedComponents();
		
		host.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		mouseX = eventX = me.getX();
		mouseY = eventY = me.getY();

		updateMousedComponents();

		host.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (mousedButton != null && pressedButton != null && mousedButton.equals(pressedButton))
			host.onButtonEvent(pressedButton);
		if (mousedArea != null && pressedArea != null && mousedArea.equals(pressedArea))
			host.onAreaEvent(pressedArea);

		pressedButton = null;
		pressedArea = null;

		updateMousedComponents();
		
		host.repaint();
	}
	
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
}

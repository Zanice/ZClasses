package ZClasses.Interface;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class ZDisplay extends JPanel {
	private static final int COLOR_BRIGHTER_DIFF = 50;
	private static final int COLOR_DARKER_DIFF = 50;
	private static final int BUTTON_PRESS_GRAPHIC_BUFFER = 4;
	
	public static enum ComponentType {
		BUTTON,
		AREA,
		TEXT_FIELD,
		DROP_DOWN_LIST
	}
	
	private ZWindow window;
	
	private int dimensionX;
	private int dimensionY;
	
	private int offsetX;
	private int offsetY;
	
	private ZMouseActionHandler mouseHandler;
	private ZKeyActionHandler keyHandler;
	
	private ComponentBundle<Button> buttons;
	private ComponentBundle<Area> areas;
	
	public ZDisplay(int dimensionX, int dimensionY) {
		this(dimensionX, dimensionY, new ZMouseActionHandler(), new ZKeyActionHandler());
	}
	
	public ZDisplay(int dimensionX, int dimensionY, ZMouseActionHandler mouseHandler, ZKeyActionHandler keyHandler) {
		super();
		
		window = null;
		
		this.dimensionX = dimensionX;
		this.dimensionY = dimensionY;
		
		offsetX = 0;
		offsetY = 0;
		
		this.mouseHandler = mouseHandler;
		this.mouseHandler.configure(this);
		addMouseListener(this.mouseHandler);
		addMouseMotionListener(this.mouseHandler);
		
		this.keyHandler = keyHandler;
		this.keyHandler.configure(this);
		addKeyListener(this.keyHandler);
		
		buttons = new ComponentBundle<Button>();
		areas = new ComponentBundle<Area>();
		
		setFocusable(true);
		setEnabled(true);
		
		postConstructorSetup();
	}
	
	public JFrame getWindow() {
		return window;
	}
	
	public ZWindow getZWindow() {
		try {
			return (ZWindow) window;
		} catch (ClassCastException cce) {
			return null;
		}
	}
	
	public void configure(ZWindow window) {
		this.window = window;
	}
	
	public int getDimensionX() {
		return dimensionX;
	}
	
	public int getDimensionY() {
		return dimensionY;
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public void setOffsetX(int x) {
		offsetX = x;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
	
	public void setOffsetY(int y) {
		offsetY = y;
	}
	
	public ZMouseActionHandler getMouseActionHandler() {
		return mouseHandler;
	}
	
	public ZKeyActionHandler getKeyActionHandler() {
		return keyHandler;
	}
	
	public int getMousedId(ComponentType type) {
		Component moused = mouseHandler.getMoused(type);
		return moused == null ? -1 : moused.getAssignedId();
	}
	
	public int getPressedId(ComponentType type) {
		Component pressed = mouseHandler.getPressed(type);
		return pressed == null ? -1 : pressed.getAssignedId();
	}
	
	public Component createComponent(ComponentType type, int aliasId, boolean locked) {
		switch (type) {
			case BUTTON:
				Button button = new Button(buttons.getComponentCount(), aliasId, locked);
				if (buttons.addNewComponent(button))
					return button;
				break;
			case AREA:
				Area area = new Area(areas.getComponentCount(), aliasId, locked);
				if (areas.addNewComponent(area))
					return area;
				break;
		}
		
		return null;
	}
	
	public ComponentBundle getComponents(ComponentType type) {
		switch (type) {
			case BUTTON:
				return buttons;
			case AREA:
				return areas;
			default:
				return null;
		}
	}
	
	public Component getOccupancy(ComponentType type, int x, int y) {
		switch (type) {
			case BUTTON:
				return buttons.getOccupancyTarget(x, y);
			case AREA:
				return areas.getOccupancyTarget(x, y);
			default:
				return null;
		}
	}
	
	public static Color createBrighterColor(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		
		red += COLOR_BRIGHTER_DIFF;
		green += COLOR_BRIGHTER_DIFF;
		blue += COLOR_BRIGHTER_DIFF;
		
		red = red > 255 ? 255 : red;
		green = green > 255 ? 255 : green;
		blue = blue > 255 ? 255 : blue;
		
		return new Color(red, green, blue);
	}
	
	public static Color createDarkerColor(Color color) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		
		red -= COLOR_DARKER_DIFF;
		green -= COLOR_DARKER_DIFF;
		blue -= COLOR_DARKER_DIFF;
		
		red = red < 0 ? 0 : red;
		green = green < 0 ? 0 : green;
		blue = blue < 0 ? 0 : blue;
		
		return new Color(red, green, blue);
	}
	
	@Override
	public final void paintComponent(Graphics g) {
		paintUnderComponents(g);
		
		buttons.paintComponents(g);
		areas.paintComponents(g);
		
		paintAboveComponents(g);
		
		repaint();
	}
	
	public abstract void postConstructorSetup();
	public abstract void paintUnderComponents(Graphics g);
	public abstract void paintAboveComponents(Graphics g);
	public abstract void paintAreaElement(Graphics g, Area area);
	public abstract void onButtonEvent(Button button);
	public abstract void onAreaEvent(Area area);
	public abstract void onKeyEvent(boolean keyDownEvent, int keyCode);
	public abstract void onResize();
	
	// -----------------------------------------------------------------------------
	
	public class ComponentBundle<T extends Component> implements Iterable<T> {
		private Map<Integer, Integer> idMap;
		private Map<Integer, T> componentMap;
		
		public ComponentBundle() {
			idMap = new HashMap<Integer, Integer>();
			componentMap = new HashMap<Integer, T>();
		}
		
		public int getComponentCount() {
			return componentMap.size();
		}
		
		public T getComponentByAssignedId(int id) {
			return componentMap.get(id);
		}
		
		public T getComponentByAliasId(int id) {
			return getComponentByAssignedId(idMap.get(id));
		}
		
		protected boolean addNewComponent(T component) {
			boolean idExists = idMap.containsKey(component.getAliasId()) || idMap.containsValue(component.getAssignedId());
			
			if (idExists)
				return false;
			
			idMap.put(component.getAliasId(), component.getAssignedId());
			componentMap.put(component.getAssignedId(), component);
			
			return true;
		}
		
		public Component getOccupancyTarget(int x, int y) {
			Component occupyingComponent = null;
			
			for (Component component : componentMap.values()) {
				if (component.occupiesPoint(x, y)) {
					if (occupyingComponent == null || component.getAliasId() > occupyingComponent.getAliasId())
						occupyingComponent = component;
				}
			}

			return occupyingComponent;
		}
		
		public void paintComponents(Graphics g) {
			for (T component : componentMap.values())
				component.paintComponent(g);
		}

		@Override
		public Iterator<T> iterator() {
			return componentMap.values().iterator();
		}
	}
	
	public abstract class Component {
		private ZDisplay display;
		private ComponentBundle<Component> bundle;
		
		private final int assignedId;
		private final int aliasId;
		private boolean locked;
		
		private int locationX;
		private int locationY;
		private int width;
		private int height;
		
		public Component(int assignedId, int aliasId, boolean locked) {
			this.assignedId = assignedId;
			this.aliasId = aliasId;
			this.locked = locked;
			
			locationX = 0;
			locationY = 0;
			width = 0;
			height = 0;
		}
		
		public ZDisplay getHostDisplay() {
			return display;
		}
		
		public ComponentBundle<Component> getHostBundle() {
			return bundle;
		}
		
		public int getAssignedId() {
			return assignedId;
		}
		
		public int getAliasId() {
			return aliasId;
		}
		
		public boolean isLocked() {
			return locked;
		}
		
		public int getLocationX() {
			return locationX;
		}
		
		public int getLocationY() {
			return locationY;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public void configureHosts(ZDisplay display, ComponentBundle<Component> bundle) {
			this.display = display;
			this.bundle = bundle;
		}
		
		public void configureSpacial(int locationX, int locationY, int width, int height) {
			this.locationX = locationX;
			this.locationY = locationY;
			this.width = width;
			this.height = height;
		}
		
		public boolean occupiesPoint(int x, int y) {
			int componentX = locationX;
			int componentY = locationY;

			if (!locked) {
				componentX += display.getOffsetX();
				componentY += display.getOffsetY();
			}

			boolean withinX = componentX < x && x < componentX + width;
			boolean withinY = componentY < y && y < componentY + height;

			return withinX && withinY;
		}
		
		public abstract ComponentType getType();
		public abstract void paintComponent(Graphics g);
	}
	
	// -----------------------------------------------------------------------------
	
	public class Button extends Component {
		private String label;
		private int offset;
		
		private Color baseColor;
		private Color borderColor;
		private Color labelColor;
		
		private Button(int assignedId, int aliasId, boolean locked) {
			super(assignedId, aliasId, locked);
			
			label = "";
			offset = 0;
			
			baseColor = Color.BLACK;
			borderColor = Color.BLACK;
			labelColor = Color.BLACK;
		}
		
		public String getLabel() {
			return label;
		}
		
		public int getLabelOffset() {
			return offset;
		}
		
		public Color getBaseColor() {
			return baseColor;
		}
		
		public Color getBorderColor() {
			return borderColor;
		}
		
		public Color getLabelColor() {
			return labelColor;
		}
		
		public void configureInfo(String label, int offset) {
			this.label = new String(label);
			this.offset = offset;
		}
		
		public void configureColors(Color base, Color border, Color letters) {
			baseColor = base;
			borderColor = border;
			labelColor = letters;
		}
		
		@Override
		public ComponentType getType() {
			return ComponentType.BUTTON;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			ZDisplay display = getHostDisplay();
			ComponentBundle<Component> bundle = getHostBundle();

			int graphicX = getLocationX();
			int graphicY = getLocationY();
			int graphicWidth = getWidth();
			int graphicHeight = getHeight();

			if (!isLocked()) {
				graphicX += display.getOffsetX();
				graphicY += display.getOffsetY();
			}

			Color base = baseColor;
			if (display.getMousedId(ComponentType.BUTTON) == getAssignedId())
				base = createBrighterColor(base);
			if (display.getPressedId(ComponentType.BUTTON) == getAssignedId()) {
				g.setColor(createDarkerColor(base));
				g.fillRect(graphicX + 1, graphicY, graphicWidth - 1, graphicHeight);
				graphicX += BUTTON_PRESS_GRAPHIC_BUFFER;
				graphicY += BUTTON_PRESS_GRAPHIC_BUFFER;
				graphicWidth -= BUTTON_PRESS_GRAPHIC_BUFFER * 2;
				graphicHeight -= BUTTON_PRESS_GRAPHIC_BUFFER * 2;
			}

			g.setColor(base);
			g.fillRect(graphicX + 1, graphicY, graphicWidth - 1, graphicHeight);
			
			if (display.getPressedId(ComponentType.BUTTON) == getAssignedId()) {
				graphicX -= BUTTON_PRESS_GRAPHIC_BUFFER;
				graphicY -= BUTTON_PRESS_GRAPHIC_BUFFER;
				graphicWidth += BUTTON_PRESS_GRAPHIC_BUFFER * 2;
				graphicHeight += BUTTON_PRESS_GRAPHIC_BUFFER * 2;
			}

			g.setColor(borderColor);
			g.drawRect(graphicX, graphicY, graphicWidth, graphicHeight);
			
			g.setColor(labelColor);
			g.drawString(label, graphicX + offset + 2, graphicY + (getHeight() / 2) + 4);
		}
	}
	
	public class Area extends Component {
		private Object element;
		
		private Color baseColor;
		private Color borderColor;
		
		private Area(int assignedId, int aliasId, boolean locked) {
			super(assignedId, aliasId, locked);
			
			baseColor = Color.BLACK;
			borderColor = Color.BLACK;
		}
		
		public Object getElement() {
			return element;
		}
		
		public void setElement(Object element) {
			this.element = element;
		}
		
		public Color getBaseColor() {
			return baseColor;
		}
		
		public Color getBorderColor() {
			return borderColor;
		}
		
		public void configureColors(Color base, Color border) {
			baseColor = base;
			borderColor = border;
		}
		
		@Override
		public ComponentType getType() {
			return ComponentType.AREA;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			ZDisplay display = getHostDisplay();
			ComponentBundle<Component> bundle = getHostBundle();

			int graphicX = getLocationX();
			int graphicY = getLocationY();

			if (!isLocked()) {
				graphicX += display.getOffsetX();
				graphicY += display.getOffsetY();
			}

			Color base = baseColor;
			if (display.getMousedId(ComponentType.AREA) == getAssignedId())
				base = createBrighterColor(base);
			if (display.getPressedId(ComponentType.AREA) == getAssignedId())
				base = createBrighterColor(base);

			g.setColor(base);
			g.fillRect(graphicX + 1, graphicY, getWidth() - 1, getHeight());

			g.setColor(borderColor);
			g.drawRect(graphicX, graphicY, getWidth(), getHeight());
			
			paintAreaElement(g, this);
		}
	}
}

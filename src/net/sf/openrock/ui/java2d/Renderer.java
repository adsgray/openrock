/*
 * Copyright (C) 2009  Daniel Nilsson
 * 
 * This file is part of OpenRock.
 *
 * OpenRock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenRock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenRock.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.openrock.ui.java2d;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;


class Renderer {

	private static final int ANY_BUTTON_MASK = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;

	private final Canvas canvas;
	private BufferStrategy bufferStrategy;
	private List<Widget> widgets = new ArrayList<Widget>();
	private List<MouseEvent> mouseEvents = new ArrayList<MouseEvent>();
	private Widget mouseGrab;
	
	public Renderer(GraphicsConfiguration gc, Dimension size) {
		Listener listener = new Listener();
		canvas = new Canvas(gc);
		canvas.addMouseListener(listener);
		canvas.addMouseWheelListener(listener);
		canvas.addMouseMotionListener(listener);
        canvas.setPreferredSize(size);
        canvas.setIgnoreRepaint(true);
	}

	public void addWidget(Widget w) {
		widgets.add(w);
	}
	
	public void removeWidget(Widget w) {
		widgets.remove(w);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}

	public void initializeRederer() {
        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();
	}

	public void deliverEvents() {
		List<MouseEvent> events;
		synchronized (mouseEvents) {
			events = new ArrayList<MouseEvent>(mouseEvents);
			mouseEvents.clear();
		}
		for (MouseEvent event : events) {
			if (mouseGrab != null) {
				Widget w = mouseGrab;
				event.translatePoint(-w.getX(), -w.getY());
				if (event.getID() == MouseEvent.MOUSE_RELEASED
						&& (event.getModifiersEx() & ANY_BUTTON_MASK) == 0) {
					mouseGrab = null;
				}
				deliverEvent(w, event);	
			} else {
				// Backwards so that the overlapping is correct
				for (int i = widgets.size() - 1; i >= 0; i--) {
					Widget w = widgets.get(i);
					if (w.isInside(event.getX() - w.getX(), event.getY() - w.getY())) {
						event.translatePoint(-w.getX(), -w.getY());
						if (event.getID() == MouseEvent.MOUSE_PRESSED) {
							mouseGrab = w;
						}
						deliverEvent(w, event);
						break;
					}
				}
			}
		}
	}

	private void deliverEvent(Widget widget, MouseEvent event) {
		switch (event.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				widget.mousePressed(event);
				break;
			case MouseEvent.MOUSE_RELEASED:
				widget.mouseRelease(event);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				widget.mouseDragged(event);
				break;
			case MouseEvent.MOUSE_WHEEL:
				widget.mouseWheelMoved((MouseWheelEvent) event);
				break;
			default:
				System.err.println("Unknown event type: " + event.getID());
				break;
		}
	}
	
	public void renderFrame() {
		Graphics2D g2d = beginRender();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Dimension d = getSize();
		
		for (Widget w : widgets) {
			w.reshape(d.width, d.height);
		}
		for (Widget w : widgets) {
			Graphics2D g2dCopy = (Graphics2D) g2d.create(w.getX(), w.getY(), w.getWidth(), w.getHeight());
			w.render(g2dCopy);
		}
		endRender(g2d);
	}
	
	private Dimension getSize() {
		return canvas.getSize();
	}

	private Graphics2D beginRender() {
		Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
		Dimension d = getSize();
		g2d.setColor(new Color(0, 0, 128));
		g2d.fillRect(0, 0, d.width, d.height);
		return g2d;
	}

	private void endRender(Graphics2D g2d) {
		g2d.dispose();
		if (!bufferStrategy.contentsLost()) {
			bufferStrategy.show();
		} else {
			System.out.println("Lost surface!");
		}
	}

	public class Listener implements MouseListener, MouseWheelListener, MouseMotionListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			synchronized (mouseEvents) {
				mouseEvents.add(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			synchronized (mouseEvents) {
				mouseEvents.add(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			synchronized (mouseEvents) {
				mouseEvents.add(e);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			synchronized (mouseEvents) {
				mouseEvents.add(e);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

	}

}

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

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

abstract class Widget {

	private int x;
	private int y;
	private int width;
	private int height;
	
	public Widget() {
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public boolean isInside(int px, int py) {
		return px >= 0 && px < width && py >= 0 && py < height;
	}

	public abstract void reshape(int renderWidth, int renderHeight);
	
	public abstract void render(Graphics2D g2d);
	
	public abstract void mousePressed(MouseEvent e);

	public abstract void mouseRelease(MouseEvent event);

	public abstract void mouseDragged(MouseEvent e);
	
	public abstract void mouseWheelMoved(MouseWheelEvent e);

}

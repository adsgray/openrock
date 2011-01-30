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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


class SweepWidget extends Widget {

	private static final String[] LABELS = new String[] { "Off", "Clean", "Yes", "Hard", "Really" };
	
	private int selected = 1;

	public SweepWidget() {
	}
	
	public double getSelectedSweep() {
		return selected / 4.0;
	}

	public void startTurn() {
		selected = 1;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		selected = e.getY() * 5 / getHeight();
	}

	@Override
	public void mouseRelease(MouseEvent event) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds(renderWidth - 85, renderHeight - 210, 75, 200);
	}

	@Override
	public void render(Graphics2D g2d) {
		g2d.setColor(new Color(0, 0, 0, 192));
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		for (int i = 0; i < 5; i++) {
			if (i == selected) {
				g2d.setColor(new Color(255, 255, 255, 192));
			} else {
				g2d.setColor(new Color(255, 255, 255, 128));
			}
			g2d.fillRoundRect(2, 2 + i*getHeight()/5, getWidth()-4, getHeight()/5-4, 8, 8);
			g2d.setColor(Color.BLACK);
			g2d.drawString(LABELS[i], 10, 27 + i*getHeight()/5);
		}
	}

}

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

import net.sf.openrock.game.Game;


class GoWidget extends Widget {

	private Game game;
	private boolean enabled;
	private boolean pressed;

	public GoWidget() {
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (enabled) {
			pressed = true;
		}
	}

	@Override
	public void mouseRelease(MouseEvent event) {
		if (enabled) {
			pressed = false;
			if (isInside(event.getX(), event.getY())) {
				game.getWorldCtrl().throwStone();
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds(10, renderHeight/2 - 130, 75, 50);
	}

	@Override
	public void render(Graphics2D g2d) {
		if (enabled) {
			g2d.setColor(new Color(0, 255, 0, 192));
		} else {
			g2d.setColor(new Color(128, 128, 128, 192));
		}
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		if (pressed) {
			g2d.setColor(Color.WHITE);
		} else {
			g2d.setColor(Color.BLACK);
		}
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		g2d.drawString("GO!", getWidth()/2 - 27, getHeight()/2 + 12);
	}

}

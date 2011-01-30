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


class NextWidget extends Widget {

	
	private Game game;

	public NextWidget() {
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseRelease(MouseEvent event) {
		game.getMatchCtrl().nextEnd(false);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds((renderWidth - 490) / 2, renderHeight - 150, 490, 130);
	}

	@Override
	public void render(Graphics2D g2d) {
		g2d.setColor(new Color(0, 0, 0, 192));
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
		g2d.drawString("Click to Continue", getWidth()/2 - 220, 80);
	}

}

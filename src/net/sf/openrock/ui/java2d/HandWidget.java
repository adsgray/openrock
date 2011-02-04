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
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import net.sf.openrock.game.Game;
import net.sf.openrock.game.MatchController;
import net.sf.openrock.model.Stone;
import net.sf.openrock.model.Vect2d;

/* ADSG: change colour of stone depending on which team's turn it is */

class HandWidget extends Widget {


	private Stone stone;
	private Game game;
	private double rot;
	private boolean right;
	private boolean enabled;

	public HandWidget() {
		stone = new Stone(Vect2d.ZERO, Vect2d.ZERO, 0, 0, 0);
		right = true;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
	
	public boolean isRight() {
		return right;
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
			right = !right;
			game.getWorldCtrl().handChanged(right);
		}
	}

	@Override
	public void mouseRelease(MouseEvent event) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds(10, renderHeight/2 - 70, 75, 50);
	}

	@Override
	public void render(Graphics2D g2d) {
		MatchController match = game.getMatchCtrl();

		stone.setTeam(match.getCurrentTeam()); 

		if (enabled) {
			g2d.setColor(new Color(255, 255, 255, 192));
		} else {
			g2d.setColor(new Color(128, 128, 128, 192));
		}
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		g2d.translate(getWidth()/2.0, getHeight()/2.0);
		double s = 0.4 * getHeight() / stone.getRadius();
		g2d.scale(s, s);
		g2d.rotate(rot);
		ViewWidget.drawStone(g2d, stone);
		if (right) {
			rot -= 0.05;
		} else {
			rot += 0.05;
		}
	}

}

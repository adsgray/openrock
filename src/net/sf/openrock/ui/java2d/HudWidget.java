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

import net.sf.openrock.model.CurlingConstants;
import net.sf.openrock.model.Match;


class HudWidget extends Widget {

	private Match match;
	
	public HudWidget() {
	}
	
	public void setMatch(Match match) {
		this.match = match;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseRelease(MouseEvent event) {
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	@Override
	public boolean isInside(int px, int py) {
		return false;	// Clicks go though
	}
	
	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds((renderWidth - 490) / 2, 10, 490, 45);
	}

	@Override
	public void render(Graphics2D g2d) {
		g2d.setColor(new Color(0, 0, 0, 192));
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		for (int t = 0; t < 2; t++) {
			int dy = t * 20;
			String text;
			g2d.setColor(Color.WHITE);
			g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
			g2d.drawString(match.getTeamName(t), 160, 18 + dy);
			for (int e = 0; e < match.getEnds() + 1; e++) {
				int dx = e * 15;
				int s = match.getScore(t, e);
				if (s != Match.NO_SCORE) {
					g2d.setColor(Color.WHITE);
				} else if (e == match.getEnds()) {
					g2d.setColor(new Color(255, 0, 0, 64));
				} else {
					g2d.setColor(new Color(255, 255, 255, 64));
				}
				g2d.fillRect(290 + dx, 3 + dy, 13, 18);
				g2d.setColor(Color.BLACK);
				if (s != Match.NO_SCORE) {
					text = Integer.toString(s);
					g2d.drawString(text, 291 + dx, 18 + dy);
				}
			}
			g2d.setColor(Color.WHITE);
			g2d.fillRect(460, 3 + dy, 23, 18);
			g2d.setColor(Color.BLACK);
			text = Integer.toString(match.getTotalScore(t));
			g2d.drawString(text, text.length() == 1 ? 466 : 461, 18 + dy);
			int s = 8 - match.getStones(t);
			if (match.getTurn() == t && (System.currentTimeMillis() & 0x200) == 0) {
				s++;
			}
			for (int i = 0; i < 8; i++) {
				if (i < s) {
					g2d.setColor(transparent(CurlingConstants.TEAM_COLORS[t], 64));
				} else {
					g2d.setColor(CurlingConstants.TEAM_COLORS[t]);
				}
				g2d.fillOval(5 + 18*i, 5 + dy, 15, 15);
			}
		}
	}

	private Color transparent(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

}

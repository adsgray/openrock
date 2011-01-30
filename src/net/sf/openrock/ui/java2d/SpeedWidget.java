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
import java.awt.LinearGradientPaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

import net.sf.openrock.game.Game;


class SpeedWidget extends Widget {

	private static final float[] FRACTIONS = new float[] {0.0f, 0.5f, 1.0f};
	private static final Color[] COLORS_ENABLED = new Color[] {Color.RED, Color.YELLOW, Color.GREEN};
	private static final Color[] COLORS_DISABLED = new Color[] {
		new Color(255, 0, 0, 128), new Color(255, 255, 0, 128), new Color(0, 255, 0, 128)};
	
	private double speed;
	private Game game;
	private boolean enabled;
	private Area area;
	private double startAngle;
	private double needleWidth;
	private double angleExtent;
	private double centerX;
	private double centerY;
	private double radiusOut;
	private double radiusIn;
	
	public SpeedWidget() {
		speed = 0.5;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}

	public double getSpeed() {
		return speed;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (enabled) {
			speedFromPos(e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (enabled) {
			speedFromPos(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseRelease(MouseEvent event) {
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (enabled) {
			speed -= e.getWheelRotation() * 0.01;
			speed = Math.max(0, Math.min(speed, 1.0));
			game.getWorldCtrl().speedChanged(speed);
		}
	}

	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds(10, renderHeight/2 - 10, renderHeight/8, renderHeight/2);
		recalculateShape();
	}

	private void recalculateShape() {
		radiusOut = getHeight();
		startAngle = -Math.asin(getHeight() / (2*radiusOut)) * 180 / Math.PI;
		angleExtent = -startAngle * 2;
		centerX = getWidth() - radiusOut;
		centerY = getHeight() / 2.0;
		radiusIn = (radiusOut - getWidth()) / Math.cos(startAngle / 180 * Math.PI);
		needleWidth = (radiusOut - radiusIn) * 0.1 / radiusOut;
		double w1 = radiusOut*2;
		double h1 = radiusOut*2;
		double x1 = centerX - radiusOut;
		double y1 = centerY - radiusOut;
		double w2 = radiusIn*2;
		double h2 = radiusIn*2;
		double x2 = centerX - radiusIn;
		double y2 = centerY - radiusIn;
		area = new Area(new Arc2D.Double(x1, y1, w1, h1, startAngle, angleExtent, Arc2D.PIE));
		area.subtract(new Area(new Ellipse2D.Double(x2, y2, w2, h2)));
	}

	@Override
	public boolean isInside(int px, int py) {
		return super.isInside(px, py) && area.contains(px, py);
	}
	
	@Override
	public void render(Graphics2D g2d) {
		if (enabled) {
			g2d.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), FRACTIONS, COLORS_ENABLED));
		} else {
			g2d.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), FRACTIONS, COLORS_DISABLED));
		}
		g2d.fill(area);
		g2d.setColor(Color.BLACK);
		double alpha = startAngle * Math.PI / 180 + needleWidth + (1.0 - speed) * (angleExtent * Math.PI / 180 - 2 * needleWidth);
		Path2D path = new Path2D.Double();
		path.moveTo(centerX + radiusIn * Math.cos(alpha - needleWidth), centerY + radiusIn * Math.sin(alpha - needleWidth));
		path.lineTo(centerX + radiusOut * Math.cos(alpha), centerY + radiusOut * Math.sin(alpha));
		path.lineTo(centerX + radiusIn * Math.cos(alpha + needleWidth) , centerY + radiusIn * Math.sin(alpha + needleWidth));
		path.closePath();
		g2d.fill(path);
	}

	private void speedFromPos(int x, int y) {
		double alpha = Math.atan2(y - centerY, x - centerX);
		double min = startAngle * Math.PI / 180 + needleWidth;
		double range = angleExtent * Math.PI / 180 - 2 * needleWidth;
		speed = 1.0 - (alpha - min) / range;
		speed = Math.max(0, Math.min(speed, 1.0));
		game.getWorldCtrl().speedChanged(speed);
	}

}

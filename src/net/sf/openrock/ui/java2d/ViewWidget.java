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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.openrock.model.CurlingConstants;
import net.sf.openrock.model.Stone;
import net.sf.openrock.model.Vect2d;
import net.sf.openrock.model.World;



class ViewWidget extends Widget {

	private World world;
	private List<PointerListener> listeners = new CopyOnWriteArrayList<PointerListener>();
	private volatile double range = 10;
	protected double scrollX;
	protected double scrollY;
	private int lastX;
	private int lastY;

	// change to -1 to reverse direction
	protected int orientation = 1;

	public ViewWidget() {
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public void setScrollX(double scrollX) {
		this.scrollX = scrollX;
	}
	
	public void setScrollY(double scrollY) {
		this.scrollY = scrollY;
	}
	
	public void addPointerListener(PointerListener l) {
		listeners.add(l);
	}
	
	public void removePointerListener(PointerListener l) {
		listeners.remove(l);
	}
	
	protected void firePointerPlaced(double x, double y) {
		for (PointerListener l : listeners) {
			l.pointerPlaced(x, y);
		}
	}

	protected void firePointerMoved(double x, double y) {
		for (PointerListener l : listeners) {
			l.pointerMoved(x, y);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
			double s = getHeight() / range;
			scrollX -= (e.getX() - lastX) / s;
			scrollY += (e.getY() - lastY) / s;
			lastX = e.getX();
			lastY = e.getY();
		}
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
			double s = getHeight() / range;
			double x = (e.getX() - getWidth()/2) / s + scrollX;
			double y = (e.getY() - getHeight()/2) / (s * orientation) + scrollY;
			firePointerMoved(x, y);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			lastX = e.getX();
			lastY = e.getY();
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			double s = getHeight() / range;
			double x = (e.getX() - getWidth()/2) / s + scrollX;
			double y = (e.getY() - getHeight()/2) / (s * orientation) + scrollY;
			firePointerPlaced(x, y);
		}
	}
	
	@Override
	public void mouseRelease(MouseEvent event) {
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		range = Math.max(2, Math.min(range + e.getWheelRotation(), 50));
	}
	
	@Override
	public void reshape(int renderWidth, int renderHeight) {
		setBounds(0, 0, renderWidth, renderHeight);
	}

	protected void renderTranslate(Graphics2D g2d)
	{
		g2d.translate(getWidth()/2, getHeight()/2);
		double s = getHeight() / range;
		g2d.scale(s, s * orientation);
		g2d.translate(-scrollX, -scrollY);
	}

	@Override
	public void render(Graphics2D g2d) {

		renderTranslate(g2d);
		drawIce(g2d);
		
		for (Stone stone : world.getStones()) {
			if (!stone.isLive()) {
				drawCoverage(g2d, stone);
			}
		}
		for (Stone stone : world.getStones()) {
			if (stone.isLive()) {
				drawPrediction(g2d, stone);
			}
		}
		for (Stone stone : world.getStones()) {
			if (stone.isLive()) {
				drawPrediction(g2d, stone);
			}
			drawStone(g2d, stone);
		}
		drawBroom(g2d);
	}

	private void drawCoverage(Graphics2D g2d, Stone stone) {
		g2d.setColor(new Color(0, 0, 0, 32));
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke((float) (stone.getRadius()*2), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		Vect2d pos = stone.getPosition();
		Vect2d diff = pos.minus(CurlingConstants.STONE_START);
		Vect2d end = pos.plus(diff);
		g2d.draw(new Line2D.Double(pos.getX(), pos.getY(), end.getX(), end.getY()));
		g2d.setStroke(oldStroke);
	}

	private void drawPrediction(Graphics2D g2d, Stone stone) {
		g2d.setColor(new Color(0, 255, 0, 32));
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke((float) (stone.getRadius()*2), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		Vect2d pos = stone.getPosition();
		Vect2d end = stone.getExtrapolatedPosition();
		g2d.draw(new Line2D.Double(pos.getX(), pos.getY(), end.getX(), end.getY()));
		g2d.setStroke(oldStroke);
	}

	private void drawBroom(Graphics2D g2d) {
		AffineTransform oldT = g2d.getTransform();
		g2d.translate(world.getBroomX(), world.getBroomY());
		g2d.setColor(Color.BLACK);
		g2d.fill(new Ellipse2D.Double(-0.04, -0.12, 0.08, 0.24));
		g2d.setColor(Color.ORANGE);
		g2d.fill(new Rectangle2D.Double(-0.015, 0.0, 0.03, 0.50));
		g2d.fill(new Ellipse2D.Double(-0.015, -0.01, 0.03, 0.02));
		g2d.setColor(Color.BLACK);
		g2d.fill(new Ellipse2D.Double(-0.015, 0.49, 0.03, 0.02));
		g2d.setTransform(oldT);
	}

	static void drawStone(Graphics2D g2d, Stone stone) {
		AffineTransform oldT = g2d.getTransform();
		g2d.translate(stone.getPosition().getX(), stone.getPosition().getY());
		g2d.rotate(stone.getA());
		double r = stone.getRadius();
		g2d.setColor(Color.GRAY);
		g2d.fill(new Ellipse2D.Double(-r, -r, r*2, r*2));
		r = r -0.03;
		g2d.setColor(CurlingConstants.TEAM_COLORS[stone.getTeam()]);
		g2d.fill(new Ellipse2D.Double(-r, -r, r*2, r*2));
		g2d.setColor(stone.isFreeGuard() ? Color.BLUE : Color.BLACK);
		g2d.fill(new Rectangle2D.Double(-0.01, -0.04, 0.02, r));
		g2d.setTransform(oldT);
	}

	private void drawIce(Graphics2D g2d) {
		drawFootRectangle(g2d, CurlingConstants.ICE_LENGTH, CurlingConstants.ICE_WIDTH, Color.WHITE);
		drawFootCicle(g2d, CurlingConstants.TEE_TO_CENTER, CurlingConstants.TWELVE_FEET_RADIUS, Color.RED);
		drawFootCicle(g2d, CurlingConstants.TEE_TO_CENTER, CurlingConstants.EIGHT_FEET_RADIUS, Color.WHITE);
		drawFootCicle(g2d, CurlingConstants.TEE_TO_CENTER, CurlingConstants.FOUR_FEET_RADIUS, Color.BLUE);
		drawFootCicle(g2d, CurlingConstants.TEE_TO_CENTER, CurlingConstants.ONE_FEET_RADIUS, Color.WHITE);
		drawFootCenterLine(g2d, CurlingConstants.TEE_TO_CENTER + CurlingConstants.TEE_TO_HACK, CurlingConstants.THIN_LINE, Color.BLACK);
		drawFootLineC(g2d, CurlingConstants.TEE_TO_CENTER, CurlingConstants.ICE_WIDTH, CurlingConstants.THIN_LINE, Color.BLACK);
		drawFootLineB(g2d, CurlingConstants.TEE_TO_CENTER+CurlingConstants.TWELVE_FEET_RADIUS, CurlingConstants.ICE_WIDTH, CurlingConstants.THIN_LINE, Color.BLACK);
		drawFootLineB(g2d, CurlingConstants.TEE_TO_CENTER-CurlingConstants.TEE_TO_HOG, CurlingConstants.ICE_WIDTH, CurlingConstants.THICK_LINE, Color.RED);
	}

	private void drawFootCenterLine(Graphics2D g2d, double dist, double lineWidth, Color color) {
		g2d.setColor(color);
		g2d.fill(new Rectangle2D.Double(-lineWidth/2, -dist, lineWidth, dist*2));
	}

	private void drawFootLineC(Graphics2D g2d, double dist, double width, double lineWidth, Color color) {
		g2d.setColor(color);
		g2d.fill(new Rectangle2D.Double(-width/2, dist-lineWidth/2, width, lineWidth));
		g2d.fill(new Rectangle2D.Double(-width/2, -dist-lineWidth/2, width, lineWidth));
	}

	private void drawFootLineB(Graphics2D g2d, double dist, double width, double lineWidth, Color color) {
		g2d.setColor(color);
		g2d.fill(new Rectangle2D.Double(-width/2, dist-lineWidth, width, lineWidth));
		g2d.fill(new Rectangle2D.Double(-width/2, -dist-lineWidth, width, lineWidth));
	}

	private void drawFootRectangle(Graphics2D g2d, double length, double width, Color color) {
		g2d.setColor(color);
		g2d.fill(new Rectangle2D.Double(-width/2, -length/2, width, length));
	}

	private void drawFootCicle(Graphics2D g2d, double dist, double radius, Color color) {
		g2d.setColor(color);
		g2d.fill(new Ellipse2D.Double(-radius, dist-radius, radius*2, radius*2));
		g2d.fill(new Ellipse2D.Double(-radius, -dist-radius, radius*2, radius*2));
	}

}

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
package net.sf.openrock.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.openrock.game.GameConfig;


public class World {

	private List<Stone> stones = new ArrayList<Stone>();
	private List<Stone> added = new ArrayList<Stone>();
	private double broomX;
	private double broomY;
	private volatile boolean clear;

	private GameConfig config;
	
	public World() {
	}

	public void setGameConfig(GameConfig config) {
		this.config = config;
	}
	
	public boolean step(double dt) {
		int steps = 10;
		if (clear) {
			clear = false;
			stones.clear();
		}
		synchronized (added) {
			stones.addAll(added);
			added.clear();
		}
		for (int step = 0; step < steps; step++) {
			List<Stone> remove = new ArrayList<Stone>();
			for (Stone stone : stones) {
				stone.step(dt / steps);
				Vect2d pos = stone.getPosition();
				if (pos.getX() + stone.getRadius() > CurlingConstants.ICE_WIDTH/2
						|| pos.getX() - stone.getRadius() < -CurlingConstants.ICE_WIDTH/2
						|| pos.getY() - stone.getRadius() > CurlingConstants.TEE_TO_CENTER + CurlingConstants.TWELVE_FEET_RADIUS) {
					remove.add(stone);
				} else if (!stone.isLive() && !stone.isHit()
						&& pos.getY() - stone.getRadius() < CurlingConstants.TEE_TO_CENTER - CurlingConstants.TEE_TO_HOG) {
					remove.add(stone);
				}
			}
			stones.removeAll(remove);
			for (int i = 0; i < stones.size(); i++) {
				Stone s1 = stones.get(i);
				for (int j = i+1; j < stones.size(); j++) {
					Stone s2 = stones.get(j);
					if (collides(s1, s2)) {
						solveCollision(s1, s2);
						s1.setHit(true);
						s2.setHit(true);
					}
				}
			}
		}
		boolean live = false;
		for (Stone stone : stones) {
			if (stone.isLive()) {
				live = true;
				break;
			}
		}
		return live;
	}

	private void solveCollision(Stone s1, Stone s2) {
		Vect2d n = s1.getPosition().minus(s2.getPosition()).normalized();	// Impact normal
		Vect2d t = n.cross(-1);												// Impact tangent
		// Assume that stones are just touching (as they should be,
		// but due to the simulation steps they may be overlapping)
		double r1 = s1.getRadius();
		double r2 = s2.getRadius();
		Vect2d p1 = n.times(-r1);								// impact point relative to stone center
		Vect2d p2 = n.times(r2);
		Vect2d v1 = s1.getVelocity().plus(Vect2d.cross(s1.getDa(), p1));	// impact point velocity
		Vect2d v2 = s2.getVelocity().plus(Vect2d.cross(s2.getDa(), p2));
		Vect2d v = v1.minus(v2);											// Relative impact velocity
		
		if (v.dot(n) >= 0) {
			// Due to error in simulation the stones seems to have collided
			// even though they are moving away from each other.
			return;
		}
		
		double m1 = s1.getMass();
		double m2 = s2.getMass();
		double i1 = s1.getMomentOfInertia();
		double i2 = s2.getMomentOfInertia();
		double e = 1.0;		// elasticity of collision
		double f = 0.3;		// friction in point of collision

		// The formula for impulse along a (unit) vector, x, is
		//                     −(1 + e) v . x
		// j_x =  -----------------------------------------------
		//        1⁄m1 + 1⁄m2 + (p1 X x)^2 ⁄ i1 + (p2 X x)^2 ⁄ i2
		
		// Impulse along normal
		// Special case since objects are circular: p X n == 0
		double nq = 1/m1 + 1/m2;  
		double nj = -(1 + e) * v.dot(n) / nq;
		// Impulse along tangent
		// Special case since objects are circular: p X t == r
		double tq = nq + r1*r1 / i1 + r2*r2 / i2;
		double tj = -(1 + e) * v.dot(t) / tq;
		
		// Limit the impulse along the tangent to be less than what friction allows
		if (Math.abs(tj) > f * nj) {
			tj = Math.signum(tj) * f * nj;
		}
		
		Vect2d impulse = n.times(nj).plus(t.times(tj));
		s1.applyImpulse(impulse, p1);
		s2.applyImpulse(impulse.times(-1), p2);
	}

	private boolean collides(Stone s1, Stone s2) {
		Vect2d diff = s2.getPosition().minus(s1.getPosition());
		double d = diff.length();
		return d < s1.getRadius() + s2.getRadius();
	}

	public void addStone(Stone s) {
		synchronized (added) {
			added.add(s);
		}
	}

	// ADSG: make optional
	public void markShotStones() {
		if (stones.isEmpty()) {
			return;
		}

		List<Stone> sorted = new ArrayList<Stone>(stones);
		Collections.sort(sorted, new ScoreFactory.BestStoneComp());

		boolean mark = true;
		int team = sorted.get(0).getTeam();

		/* Go through entire list of stones.
		Mark those which would count at conclusion of end.
		 */
		for (Stone s : sorted) {
			if (s.getTeam() != team) {
				mark = false;
			}
			double d = s.getPosition().minus(ScoreFactory.CENTER).length() - s.getRadius();
			if (d > CurlingConstants.TWELVE_FEET_RADIUS) {
				mark = false;
			}
			s.setShot(mark);
		}
	}
	
	public void clearStones() {
		clear = true;
	}

	public void replaceStones(List<Stone> ss) {
		clear = true;
		added.clear();
		added.addAll(ss);
	}

	public List<Stone> getStones() {
		return Collections.unmodifiableList(stones);
	}

	public void setBroomLocation(double x, double y) {
		broomX = x;
		broomY = y;
	}
	
	public double getBroomX() {
		return broomX;
	}
	
	public double getBroomY() {
		return broomY;
	}

	public void markFreeGuards(int team) {
		for (Stone s : stones) {
			if (s.getTeam() == team) {
				continue;
			}
			if (s.getPosition().getY() + s.getRadius() >= CurlingConstants.TEE_TO_CENTER) {
				continue;
			}
			double d = s.getPosition().minus(ScoreFactory.CENTER).length() - s.getRadius();
			if (d <= CurlingConstants.TWELVE_FEET_RADIUS) {
				continue;
			}
			s.setFreeGuard(true);
		}
	}

	public int getBestTeam() {
		ScoreIF score = config.getScore();
		return score.getBestTeam();
	}

	public int getPoints() {
		ScoreIF score = config.getScore();
		return score.getPoints();
	}

	public StateIF getState()
	{
		return new World.State();
	}

	class State implements StateIF {
		private List<Stone> stones;

		State()
		{
			stones = new ArrayList<Stone>();
			World w = World.this;

			for (Stone s : w.stones) {
				stones.add(s.clone());
			}

		}

		@Override
		public void restoreState()
		{
			World w = World.this;
			w.stones = stones;
		}

	}
}

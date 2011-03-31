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

import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ScoreFactory {

	public static final Logger logger = Logger.getLogger(ScoreFactory.class.getName());

	public static ScoreIF CreateNormalScore(World world) {
		return new NormalScore(world);
	}

	public static ScoreIF CreateSkinsScore(World world) {
		return new SkinsScore(world);
	}

	public static final Vect2d CENTER = 
		new Vect2d(0.0, CurlingConstants.TEE_TO_CENTER);

	public static class BestStoneComp implements Comparator<Stone> {

		@Override
		public int compare(Stone o1, Stone o2) {
			double d1 = o1.getPosition().minus(CENTER).length();
			double d2 = o2.getPosition().minus(CENTER).length();
			if (d1 < d2) {
				return -1;
			} else if (d1 > d2) {
				return 1;
			} else {
				return 0;
			}
		}

	}

}


class NormalScore implements ScoreIF {


	protected World world;

	public NormalScore(World world) { 
		ScoreFactory.logger.info("NormalScore");
		this.world = world; 
	}


	// this method should be in World
	public int getBestTeam() {
		List<Stone> stones = world.getStones();

		if (stones.isEmpty()) {
			return -1;
		}
		List<Stone> sorted = new ArrayList<Stone>(stones);
		Collections.sort(sorted, new ScoreFactory.BestStoneComp());
		Stone s = sorted.get(0);
		double d = s.getPosition().minus(ScoreFactory.CENTER).length() - s.getRadius();
		if (d > CurlingConstants.TWELVE_FEET_RADIUS) {
			return -1;
		} else {
			return s.getTeam();
		}
	}

	public int getPoints() {
		// instead of getting stones from world, should
		// be passed a sorted array, which has only
		// the team numbers
		List<Stone> stones = world.getStones();

		if (stones.isEmpty()) {
			return 0;
		}
		List<Stone> sorted = new ArrayList<Stone>(stones);
		Collections.sort(sorted, new ScoreFactory.BestStoneComp());
		int p = 0;
		int team = sorted.get(0).getTeam();
		for (Stone s : sorted) {
			if (s.getTeam() != team) {
				break;
			}
			double d = s.getPosition().minus(ScoreFactory.CENTER).length() - s.getRadius();
			if (d > CurlingConstants.TWELVE_FEET_RADIUS) {
				break;
			}
			p++;
		}
		return p;
	}


}


class SkinsScore extends NormalScore {

	private int score = 1;

	/*
		Could have each end worth a different number of
		skins. Store in Match object?
		Initially, set score to Match.skins[0] (end 1)
		If carryover, add Match.skins[i] to this.score.
		If skin won, set this.score to Match.skins[i+1]
	 */
	public SkinsScore(World world)
	{
		super(world);
		ScoreFactory.logger.info("SkinsScore");
	}

	// add info logging calls here
	@Override
	public int getPoints() {
		List<Stone> stones = world.getStones();
		int points = super.getPoints();
		// second stone thrown (zero based)
		int hammerteam = stones.get(1).getTeam();
		int bestteam = getBestTeam();

		// team with hammer must score 2 or more
		if (hammerteam == bestteam && points >= 2)
		{
			// return carried-over score, if any
			// and set next end to be worth 1
			int ret = score;
			score = 1;
			return ret;
		}

		// team without hammer must steal at least 1
		if (hammerteam != bestteam && points > 0)
		{
			int ret = score;
			score = 1;
			return ret;
		}

		// carry-over
		score += 1;
		return 0;
	}
}

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
package net.sf.openrock.game;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.openrock.model.CurlingConstants;
import net.sf.openrock.model.Stone;
import net.sf.openrock.model.Vect2d;
import net.sf.openrock.model.World;
import net.sf.openrock.ui.UIProvider;
import net.sf.openrock.game.Physics;


public class WorldController {

	private static final Logger logger = Logger.getLogger(WorldController.class.getName());

	private final Game game;
	private final UIProvider ui;
	private final World world;
	private final PhysicsIF physics;
	
	private Stone currentStone;
	private boolean live = false;
	private List<Stone> savedStones;


	WorldController(Game game, UIProvider ui) {
		this.game = game;
		this.ui = ui;
		world = new World();
		world.setBroomLocation(0, CurlingConstants.TEE_TO_CENTER);
		ui.setWorld(world);

		physics = Physics.CreateDefaultPhysics(world);
	}
	
	void prepareForNewEnd() {
		world.clearStones();
	}

	void prepareFreeGuardZone(int team) {
		List<Stone> stones = world.getStones();
		savedStones = new ArrayList<Stone>(stones.size());
		for (Stone s : stones) {
			savedStones.add(s.clone());
		}
		logger.fine("Saved stones: " + savedStones);
		logger.info("Marking free guards");
		world.markFreeGuards(team);
	}

	int getPoints() {
		return world.getPoints();
	}

	int getBestTeam() {
		return world.getBestTeam();
	}

	public void broomMoved(double x, double y) {
		world.setBroomLocation(x, y);
		game.sendBroom(x, y);
	}

	public void speedChanged(double speed) {
		ui.setSpeed(speed);
		game.sendSpeed(speed);
	}

	public void handChanged(boolean right) {
		ui.setHand(right);
		game.sendHand(right);
	}

	public void stoneAdded(Stone stone) {
		world.addStone(stone);
	}

	public void stonesUpdated(List<Stone> stones) {
		world.replaceStones(stones);
	}

	public void throwStone() {
		// do speed and dir calculations externally
		double v = physics.getSpeed(ui.getSpeed());
		Vect2d dir = physics.getDir();

		Vect2d vel = dir.times(v);
		double da = 1.0;
		// should make this dependent on orientation
		if (ui.isHandRight()) {
			da = -da;
		}
		currentStone = new Stone(CurlingConstants.STONE_START, vel, 0, da, game.getMatchCtrl().getCurrentTeam());
		double sweep = ui.getSelectedSweep();
		currentStone.setSweep(sweep);
		logger.info("New stone: " + currentStone);
		world.addStone(currentStone);
		game.sendNewStone(currentStone);
		ui.endTurn();
	}

	public void step(double dt) {
		updateSweep();
		int freeGuards = countFreeGuards();
		boolean moving = world.step(dt);
		if (countFreeGuards() < freeGuards) {
			logger.info("Free guard removed, resetting stones with: " + savedStones);
			world.replaceStones(savedStones);
		}
		if (!moving && live) {
			live = false;
			allStopped();
		} else if (moving && !live) {
			live = true;
		}
	}

	private int countFreeGuards() {
		int cnt = 0;
		for (Stone s : world.getStones()) {
			if (s.isFreeGuard()) {
				cnt++;
			}
		}
		return cnt;
	}

	private void updateSweep() {
		if (currentStone == null) {
			return;
		}
		double sweep = ui.getSelectedSweep();
		if (sweep != currentStone.getSweep()) {
			currentStone.setSweep(sweep);
			game.sendAllStones(world.getStones());
		}
	}

	private void allStopped() {
		logger.info("All stones have stopped moving");
		currentStone = null;
		for (Stone s : world.getStones()) {
			s.setFreeGuard(false);
		}
		game.getMatchCtrl().nextTurn();
	}

}

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

import java.util.logging.Logger;

import net.sf.openrock.model.Match;
import net.sf.openrock.ui.UIProvider;
import net.sf.openrock.model.StateIF;
import net.sf.openrock.model.StateControllerIF;


public class MatchController {
	
	private static final Logger logger = Logger.getLogger(MatchController.class.getName());

	private final Game game;
	private final UIProvider ui;
	private Match match;
	private boolean waitForNext;
	
	MatchController(Game game, UIProvider ui) {
        this.game = game;
		this.ui = ui;
		match = new Match("Team 1", "Team 2", 10);
        ui.setMatch(match);
	}
	
	void newMatch(Match m) {
		logger.info("Staring a new match");
		match = m;
        ui.setMatch(match);
		game.startTurn();
	}

	public StateIF getMatchState()
	{
		return match.getState();
	}

	public int getCurrentTeam() {
		return match.getTurn();
	}

	public int getStones() {
		return match.getStones();
	}

	void nextTurn() {
		logger.info("Next turn");
		match.nextTurn();
		int stones = match.getStones(match.getTurn());
		if (stones == 0) {
			endEnded();
		} else {
			if (stones > 6) {
				game.getWorldCtrl().prepareFreeGuardZone(match.getTurn());
			}
			game.startTurn();
		}
	}

	public void nextEnd(boolean fromNet) {
		if (!waitForNext) {
			// We got this from the network, but the button was already pressed locally
			logger.finer("Duplicate nextEnd ignored");
			return;
		}
		logger.info("Next end");
		waitForNext = false;
		if (!fromNet) {
			game.sendNextEnd();
		}
		ui.hideNextButton();
		int i = game.getWorldCtrl().getBestTeam();
		if (i < 0) {
			i = match.getTurn();
		}
		if (match.nextEnd(i)) {
			game.getWorldCtrl().prepareForNewEnd();
			game.startTurn();
		} else {
			game.gameOver("Match winner: " + match.getTeamName(match.getWinner()));
		}
	}

	private void endEnded() {
		int i = game.getWorldCtrl().getBestTeam();
		if (i >= 0) {
			int p = game.getWorldCtrl().getPoints();
			logger.info("The end has ended. Team " + i + " gets " + p + " points");
			match.setScore(i, p);
		} else {
			i = match.getTurn();
			logger.info("The end has ended. No points, team " + i + " is still first");
			match.setScore(i, 0);
		}
		waitForNext = true;
		ui.showNextButton();
	}

}

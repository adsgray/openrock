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
package net.sf.openrock.ui;

import net.sf.openrock.game.Game;
import net.sf.openrock.model.Match;
import net.sf.openrock.model.World;


public interface UIProvider {

	void setGame(Game game);

	void setMatch(Match match);

	void setWorld(World world);

	void renderFrame();

	void deliverEvents();

	void startTurn();

	void endTurn();

	void showNextButton();

	void hideNextButton();

	void showGameDialog(String msg);

	void showNetworkDialog(String msg);

	void hideNetworkDialog();

	double getSelectedSweep();

	double getSpeed();

	void setSpeed(double value);

	void setHand(boolean right);

	boolean isHandRight();

}

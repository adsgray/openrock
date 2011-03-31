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

import java.util.List;

import net.sf.openrock.model.Match;
import net.sf.openrock.model.Stone;
import net.sf.openrock.net.NetworkHandler;
import net.sf.openrock.ui.UIProvider;
import net.sf.openrock.model.StateControllerIF;
import net.sf.openrock.model.StateController;


public class Game {

	private final UIProvider ui;
	private final MatchController matchCtrl;
	private final WorldController worldCtrl;
	
	private NetworkHandler netHandler;
	private boolean[] localTeam = new boolean[2];
	private String localTeamName;
	private int localEnds;

	private StateControllerIF stateCtrl;
	private GameConfig config;
	
	public Game(UIProvider ui) {
		this.ui = ui;
		matchCtrl = new MatchController(this, ui);
		worldCtrl = new WorldController(this, ui);
	}

	public void createConfig(ConfigImporter imp)
	{
		config = GameConfigFactory.instance();

		config.createPlayers(worldCtrl.getWorld(),
				     imp.playersType(),
				     imp.maxSpeedError(),
				     imp.maxDirError());

		config.createScoring(worldCtrl.getWorld(),
				     imp.scoringType());

		config.createCurl(imp.curlType());

		worldCtrl.setGameConfig(config);
	}

	public MatchController getMatchCtrl() {
		return matchCtrl;
	}
	
	public WorldController getWorldCtrl() {
		return worldCtrl;
	}

	private void setupRealStateController()
	{

		stateCtrl = StateController.createRealStateController(matchCtrl, worldCtrl, ui);

		ui.setStateController(stateCtrl);
		worldCtrl.setStateController(stateCtrl);
	}

	private void setupNullStateController()
	{
		stateCtrl = StateController.createNullStateController();
		ui.setStateController(stateCtrl);
		worldCtrl.setStateController(stateCtrl);
	}
	
	public void newHotSeatMatch(String team1, String team2, int ends) {
		localTeam[0] = true;
		localTeam[1] = true;

		setupRealStateController();
		matchCtrl.newMatch(new Match(team1, team2, ends));
	}

	public void newNetServerMatch(String team1, int ends, int port) {
		setupNullStateController();

		ui.setMatch(new Match(team1, "?", ends));

		localTeamName = team1;
		localEnds = ends;
		localTeam[0] = true;
		localTeam[1] = false;
		netHandler = new NetworkHandler(this, team1, ends);
		netHandler.listen(port);
		ui.showNetworkDialog("Waiting for connection...");
	}

	public void newNetClientMatch(String team2, int ends, String host, int port) {
		setupNullStateController();

		ui.setMatch(new Match("?", team2, 10));


		localTeamName = team2;
		localTeam[0] = false;
		localTeam[1] = true;
		netHandler = new NetworkHandler(this, team2, ends);
		netHandler.connect(host, port);
		ui.showNetworkDialog("Connecting to remote host...");
	}

	public void abortNetwork() {
		netHandler.close();
		ui.showGameDialog("Network connection aborted");
	}
	
	public void pollNetwork() {
		if (netHandler != null) {
			if (!netHandler.poll()) {
				netHandler = null;
			}
		}
	}
	
	public void networkConnected(String remoteTeamName, int ends) {
		Match match;
		if (localTeam[0]) {
			match = new Match(localTeamName, remoteTeamName, localEnds);
		} else {
			match = new Match(remoteTeamName, localTeamName, ends);
		}
		ui.hideNetworkDialog();
		matchCtrl.newMatch(match);
	}

	public void networkDisconnected(boolean error, String msg) {
		netHandler.close();
		netHandler = null;
		if (error) {
			ui.showGameDialog(msg);
		}
	}

	void startTurn() {
		if (isCurrentTeamLocal()) {
			ui.startTurn();
		}
	}

	void gameOver(String result) {
		if (netHandler != null) {
			netHandler.sendGoodbye();
		}
		ui.showGameDialog(result);
	}

	void sendSpeed(double speed) {
		if (isCurrentTeamLocal() && netHandler != null) {
			netHandler.sendSpeedSelection(speed);
		}
	}
	
	void sendHand(boolean right) {
		if (isCurrentTeamLocal() && netHandler != null) {
			netHandler.sendHandSelection(right);
		}
	}
	
	void sendBroom(double x, double y) {
		if (isCurrentTeamLocal() && netHandler != null) {
			netHandler.sendBroomLocation(x, y);
		}
	}

	void sendNewStone(Stone stone) {
		if (netHandler != null) {
			netHandler.sendNewStone(stone);
		}
	}

	void sendAllStones(List<Stone> stones) {
		if (netHandler != null) {
			netHandler.sendAllStones(stones);
		}
	}

	void sendNextEnd() {
		if (netHandler != null) {
			netHandler.sendNextEnd();
		}
	}

	private boolean isCurrentTeamLocal() {
		return localTeam[matchCtrl.getCurrentTeam()];
	}

}

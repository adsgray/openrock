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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.sf.openrock.Main;
import net.sf.openrock.game.Game;
import net.sf.openrock.model.CurlingConstants;
import net.sf.openrock.model.Match;
import net.sf.openrock.model.World;
import net.sf.openrock.model.StateControllerIF;
import net.sf.openrock.ui.UIProvider;


public class Java2dGui implements PointerListener, UIProvider {

	private final Frame frame;
	private final Renderer renderer;
	private final ViewWidget view;
	private final HudWidget hud;
	private final SpeedWidget speed;
	private final HandWidget hand;
	private final GoWidget go;
	private final SwingGui2 swingGui;

	private final UndoWidget undo;
	private final RedoWidget redo;

	private final SmallViewWidget smallview;
	
	private Game game;
	private StateControllerIF state;
	private boolean broomMovable;
	private NextWidget next;
	private SweepWidget sweep;
	
	public Java2dGui() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration gc = device.getDefaultConfiguration();
		frame = new Frame(Main.APP_NAME, gc);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		swingGui = new SwingGui2();
		renderer = new Renderer(gc, new Dimension(800, 600));
		view = new ViewWidget();
		view.addPointerListener(this);
		view.setScrollX(0.0);
		view.setScrollY(CurlingConstants.TEE_TO_CENTER - CurlingConstants.TEE_TO_HOG/2 + CurlingConstants.TWELVE_FEET_RADIUS/2);


		hud = new HudWidget();
		speed = new SpeedWidget();
		hand = new HandWidget();
		go = new GoWidget();
		sweep = new SweepWidget();
		next = new NextWidget();
		undo = new UndoWidget();
		redo = new RedoWidget();
		renderer.addWidget(view);
		renderer.addWidget(hud);
		renderer.addWidget(speed);
		renderer.addWidget(hand);
		renderer.addWidget(go);
		renderer.addWidget(sweep);
		renderer.addWidget(undo);
		renderer.addWidget(redo);

		smallview = new SmallViewWidget();
		renderer.addWidget(smallview);

        frame.add(renderer.getCanvas());
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        renderer.initializeRederer();
        swingGui.showGameDialog("Welcome to " + Main.APP_NAME + " " + Main.APP_VERSION);
	}

	@Override
	public void setGame(Game game) {
		this.game = game;
		swingGui.setGame(game);
		speed.setGame(game);
		hand.setGame(game);
		go.setGame(game);
		next.setGame(game);
	}

	@Override
	public void setStateController(StateControllerIF state)
	{
		this.state = state;
		undo.setStateController(state);
		redo.setStateController(state);
	}
	
	@Override
	public void renderFrame() {
		renderer.renderFrame();
	}
	
	@Override
	public void deliverEvents() {
		renderer.deliverEvents();
	}
	
	@Override
	public void pointerPlaced(double x, double y) {
		if (broomMovable
				&& x >= -CurlingConstants.ICE_WIDTH/2
				&& x <= CurlingConstants.ICE_WIDTH/2
				&& y >= CurlingConstants.TEE_TO_CENTER - CurlingConstants.TEE_TO_HOG
				&& y <= CurlingConstants.TEE_TO_CENTER + CurlingConstants.TWELVE_FEET_RADIUS) {
			game.getWorldCtrl().broomMoved(x, y);
		}
	}

	@Override
	public void pointerMoved(double x, double y) {
		if (broomMovable
				&& x >= -CurlingConstants.ICE_WIDTH/2
				&& x <= CurlingConstants.ICE_WIDTH/2
				&& y >= CurlingConstants.TEE_TO_CENTER - CurlingConstants.TEE_TO_HOG
				&& y <= CurlingConstants.TEE_TO_CENTER + CurlingConstants.TWELVE_FEET_RADIUS) {
			game.getWorldCtrl().broomMoved(x, y);
		}
	}

	public void enableStateButtons()
	{
		undo.setEnabled(state.isUndoPossible());
		redo.setEnabled(state.isRedoPossible());
	}

	@Override
	public void startTurn() {
		sweep.startTurn();
		speed.setEnabled(true);
		hand.setEnabled(true);
		go.setEnabled(true);
		broomMovable = true;

		enableStateButtons();
	}

	@Override
	public void endTurn() {
		speed.setEnabled(false);
		hand.setEnabled(false);
		go.setEnabled(false);
		broomMovable = false;

		undo.setEnabled(false);
		redo.setEnabled(false);
	}

	@Override
	public double getSelectedSweep() {
		return sweep.getSelectedSweep();
	}

	@Override
	public double getSpeed() {
		return speed.getSpeed();
	}

	@Override
	public void hideNetworkDialog() {
		swingGui.hideNetworkDialog();
	}

	@Override
	public void hideNextButton() {
		renderer.removeWidget(next);
	}

	@Override
	public boolean isHandRight() {
		return hand.isRight();
	}

	@Override
	public void setHand(boolean right) {
		hand.setRight(right);
	}

	@Override
	public void setMatch(Match match) {
		hud.setMatch(match);
	}

	@Override
	public void setSpeed(double value) {
		speed.setSpeed(value);
	}

	@Override
	public void setWorld(World world) {
		view.setWorld(world);
		smallview.setWorld(world);
	}

	@Override
	public void showGameDialog(String msg) {
		swingGui.showGameDialog(msg);
	}

	@Override
	public void showNetworkDialog(String msg) {
		swingGui.showNetworkDialog(msg);
	}

	@Override
	public void showNextButton() {
		renderer.addWidget(next);
	}

}

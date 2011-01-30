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
package net.sf.openrock;

import net.sf.openrock.ui.UIProvider;
import net.sf.openrock.ui.java2d.Java2dGui;


public class Main implements Runnable {

	private static final long NANOS_PER_SEC = 1000000000L;
	private static final long NANOS_PER_MILLI = 1000000L;
	
	private static final int TICK_RATE = 30;

	public static final String APP_NAME = "OpenRock Curling";
	public static final String APP_VERSION = "1.0 beta";
	public static final String COPYRIGHT = "Copyright (C) 2009  Daniel Nilsson";
	public static final String HOMEPAGE = "http://sourceforge.net/projects/openrock/";
	
	public static void main(String[] args) {
		Main main = new Main();
		main.run();
	}


	private UIProvider gui;
	private net.sf.openrock.game.Game game;

	public Main() {
		gui = new Java2dGui();
		game = new net.sf.openrock.game.Game(gui);
		gui.setGame(game);
	}

	@Override
	public void run() {
		long nextTick = System.nanoTime() + NANOS_PER_SEC / TICK_RATE;
		long dropMask = 0;
		
		// This loop tries to keep a constant tick rate dropping rendered frames if the computer is too slow.
		// It is important to keep the physics engine delta time constant so the calculations are the same
		// regardless of computer speed. More so in network games.
		while (true) {
			long delta = nextTick - System.nanoTime();
			
			if (delta > 0) {
				// Render one frame
				gui.renderFrame();
			} else {
				// No time to render frame, skip frame
				dropMask |= 1;
			}
			dropMask <<= 1;
			
			delta = nextTick - System.nanoTime();
			while (delta > 0) {
				// Fast computer so we can sleep for a while
				try {
					Thread.sleep(delta / NANOS_PER_MILLI, (int) (delta % NANOS_PER_MILLI));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				delta = nextTick - System.nanoTime();
			}
			
			// Advance the game time by one tick
			game.getWorldCtrl().step(1.0 / TICK_RATE);
			nextTick += NANOS_PER_SEC / TICK_RATE;
			gui.deliverEvents();
			game.pollNetwork();
		}
	}

}

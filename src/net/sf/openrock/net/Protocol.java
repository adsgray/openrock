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
package net.sf.openrock.net;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.openrock.game.Game;
import net.sf.openrock.model.Stone;
import net.sf.openrock.model.Vect2d;


public class Protocol {

	private static final Logger logger = Logger.getLogger(Protocol.class.getName());
	
	private static final byte MSG_HELLO = 1;
	private static final byte MSG_GOODBYE = 2;
	private static final byte MSG_BROOM = 3;
	private static final byte MSG_SPEED = 4;
	private static final byte MSG_HAND = 5;
	private static final byte MSG_STONE = 6;
	private static final byte MSG_STONES = 7;
	private static final byte MSG_NEXT = 8;
	
	private static final byte STONE_FLAG_HIT = 1 << 0;
	private static final byte STONE_FLAG_FREE_GUARD = 1 << 1;
	
	private static final Charset charset = Charset.forName("UTF-8");


	private Protocol() {
	}
	
	public static boolean parseMsg(ByteBuffer buf, Game game) {
		buf.mark();
		try {
			byte type = buf.get();
			switch (type) {
				case MSG_HELLO:
					parseHelloMsg(buf, game);
					break;
				case MSG_GOODBYE:
					parseGoodbyeMsg(buf, game);
					break;
				case MSG_BROOM:
					parseBroomMsg(buf, game);
					break;
				case MSG_SPEED:
					parseSpeedMsg(buf, game);
					break;
				case MSG_HAND:
					parseHandMsg(buf, game);
					break;
				case MSG_STONE:
					parseStoneMsg(buf, game);
					break;
				case MSG_STONES:
					parseStonesMsg(buf, game);
					break;
				case MSG_NEXT:
					parseNextMsg(buf, game);
					break;
				default:
					System.out.println("Unknown network message: " + type);
					break;
			}
		} catch (BufferUnderflowException e) {
			// Not enough data, wait until more has been read and retry
			buf.reset();
			return false;
		}
		return true;
	}

	private static void parseHelloMsg(ByteBuffer buf, Game game) {
		String remoteTeam = getString(buf);
		int ends = buf.getInt();
		logger.log(Level.FINE, "Received HELLO: {0} {1}", new Object[] { remoteTeam, ends });
		game.networkConnected(remoteTeam, ends);
	}

	public static void createHelloMsg(ByteBuffer buf, String localTeam, int ends) {
		buf.put(MSG_HELLO);
		putString(buf, localTeam);
		buf.putInt(ends);
		logger.log(Level.FINE, "Sending HELLO: {0} {1}", new Object[] { localTeam, ends });
	}

	private static void parseGoodbyeMsg(ByteBuffer buf, Game game) {
		logger.log(Level.FINE, "Received GOODBYE");
		game.networkDisconnected(false, "Goodbye!");
	}

	public static void createGoodbyeMsg(ByteBuffer buf) {
		buf.put(MSG_GOODBYE);
		logger.log(Level.FINE, "Sending GOODBYE");
	}

	private static void parseBroomMsg(ByteBuffer buf, Game game) {
		double x = buf.getDouble();
		double y = buf.getDouble();
		logger.log(Level.FINE, "Received BROOM: {0} {1}", new Object[] { x, y });
		game.getWorldCtrl().broomMoved(x, y);
	}

	public static void createBroomMsg(ByteBuffer buf, double x, double y) {
		buf.put(MSG_BROOM);
		buf.putDouble(x);
		buf.putDouble(y);
		logger.log(Level.FINE, "Sending BROOM: {0} {1}", new Object[] { x, y });
	}

	private static void parseSpeedMsg(ByteBuffer buf, Game game) {
		double value = buf.getDouble();
		logger.log(Level.FINE, "Received SPEED: {0}", value);
		game.getWorldCtrl().speedChanged(value);
	}

	public static void createSpeedMsg(ByteBuffer buf, double speed) {
		buf.put(MSG_SPEED);
		buf.putDouble(speed);
		logger.log(Level.FINE, "Sending SPEED: {0}", speed);
	}

	private static void parseHandMsg(ByteBuffer buf, Game game) {
		boolean right = (buf.get() != 0);
		logger.log(Level.FINE, "Received HAND: {0}", right);
		game.getWorldCtrl().handChanged(right);
	}

	public static void createHandMsg(ByteBuffer buf, boolean right) {
		buf.put(MSG_HAND);
		buf.put((byte) (right ? 1 : 0));
		logger.log(Level.FINE, "Sending HAND: {0}", right);
	}

	private static void parseStoneMsg(ByteBuffer buf, Game game) {
		Stone stone = getStone(buf);
		logger.log(Level.FINE, "Received STONE");
		game.getWorldCtrl().stoneAdded(stone);
	}

	public static void createStoneMsg(ByteBuffer buf, Stone stone) {
		buf.put(MSG_STONE);
		putStone(buf, stone);
		logger.log(Level.FINE, "Sending STONE");
	}

	private static void parseStonesMsg(ByteBuffer buf, Game game) {
		int n = buf.get();
		List<Stone> stones = new ArrayList<Stone>(n);
		for (int i = 0; i < n; i++) {
			stones.add(getStone(buf));
		}
		logger.log(Level.FINE, "Received STONES: {0}", stones.size());
		game.getWorldCtrl().stonesUpdated(stones);
	}

	public static void createStonesMsg(ByteBuffer buf, List<Stone> stones) {
		buf.put(MSG_STONES);
		buf.put((byte) stones.size());
		for (Stone stone : stones) {
			putStone(buf, stone);
		}
		logger.log(Level.FINE, "Sending STONES: {0}", stones.size());
	}

	private static void parseNextMsg(ByteBuffer buf, Game game) {
		logger.log(Level.FINE, "Received NEXT");
		game.getMatchCtrl().nextEnd(true);
	}

	public static void createNextMsg(ByteBuffer buf) {
		buf.put(MSG_NEXT);
		logger.log(Level.FINE, "Sending NEXT");
	}

	private static String getString(ByteBuffer buf) {
		int l = buf.getInt();
		ByteBuffer str = ByteBuffer.allocate(l);
		buf.get(str.array());
		return charset.decode(str).toString();
	}

	private static void putString(ByteBuffer buf, String string) {
		ByteBuffer str = charset.encode(string);
		buf.putInt(str.limit());
		buf.put(str);
	}

	private static Vect2d getVect2d(ByteBuffer buf) {
		double x = buf.getDouble();
		double y = buf.getDouble();
		return new Vect2d(x, y);
	}

	private static void putVect2d(ByteBuffer buf, Vect2d v) {
		buf.putDouble(v.getX());
		buf.putDouble(v.getY());
	}

	private static Stone getStone(ByteBuffer buf) {
		int team = buf.getInt();
		Vect2d p = getVect2d(buf);
		Vect2d v = getVect2d(buf);
		double a = buf.getDouble();
		double da = buf.getDouble();
		double s = buf.getDouble();
		byte flags = buf.get();
		boolean hit = (flags & STONE_FLAG_HIT) != 0;
		boolean freeGuard = (flags & STONE_FLAG_FREE_GUARD) != 0;
		Stone stone = new Stone(p, v, a, da, team);
		stone.setSweep(s);
		stone.setHit(hit);
		stone.setFreeGuard(freeGuard);
		return stone;
	}

	private static void putStone(ByteBuffer buf, Stone stone) {
		buf.putInt(stone.getTeam());
		putVect2d(buf, stone.getPosition());
		putVect2d(buf, stone.getVelocity());
		buf.putDouble(stone.getA());
		buf.putDouble(stone.getDa());
		buf.putDouble(stone.getSweep());
		byte flags = 0;
		if (stone.isHit()) {
			flags |= STONE_FLAG_HIT;
		}
		if (stone.isFreeGuard()) {
			flags |= STONE_FLAG_FREE_GUARD;
		}
		buf.put(flags);
	}

}

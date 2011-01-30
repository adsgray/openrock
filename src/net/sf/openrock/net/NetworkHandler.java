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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.openrock.game.Game;
import net.sf.openrock.model.Stone;


public class NetworkHandler {

	private static final Logger logger = Logger.getLogger(NetworkHandler.class.getName());

	private static final int BUFFER_SIZE = 1024;
	
	private final Game game;
	private final String localTeam;
	private final int ends;
	
	private String host;
	private int port;
	private boolean server;
	private boolean connect;
	private boolean close;
	private ServerSocketChannel ssc;
	private SocketChannel channel;
	private ByteBuffer sendBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(BUFFER_SIZE);


	public NetworkHandler(Game game, String localTeam, int ends) {
		this.game = game;
		this.localTeam = localTeam;
		this.ends = ends;
	}

	public void listen(int port) {
		this.port = port;
		server = true;
		connect = true;
	}

	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		server = false;
		connect = true;
	}

	public void close() {
		close = true;
	}

	public void sendBroomLocation(double x, double y) {
		Protocol.createBroomMsg(sendBuffer, x, y);
		flushSendBuffer();
	}

	public void sendSpeedSelection(double speed) {
		Protocol.createSpeedMsg(sendBuffer, speed);
		flushSendBuffer();
	}

	public void sendHandSelection(boolean right) {
		Protocol.createHandMsg(sendBuffer, right);
		flushSendBuffer();
	}

	public void sendNewStone(Stone stone) {
		Protocol.createStoneMsg(sendBuffer, stone);
		flushSendBuffer();
	}

	public void sendAllStones(List<Stone> stones) {
		Protocol.createStonesMsg(sendBuffer, stones);
		flushSendBuffer();
	}

	public void sendNextEnd() {
		Protocol.createNextMsg(sendBuffer);
		flushSendBuffer();
	}

	public void sendGoodbye() {
		Protocol.createGoodbyeMsg(sendBuffer);
		flushSendBuffer();
	}

	public boolean poll() {
		try {
			if (close) {
				if (channel != null) {
					channel.close();
				}
				return false;
			}
			if (connect && (channel == null || !channel.isConnected())) {
				if (server) {
					waitForClient();
				} else {
					connectToServer();
				}
			} else if (connect) {
				logger.log(Level.INFO, "Connected to {0}", channel.socket().getRemoteSocketAddress());
				Protocol.createHelloMsg(sendBuffer, localTeam, ends);
				flushSendBuffer();
				connect = false;
			} else {
				if (channel.read(receiveBuffer) < 0) {
					logger.info("Remote closed network connection");
					channel.close();
					game.networkDisconnected(true, "Lost network connection");
					return false;
				}
				receiveBuffer.flip();
				while (Protocol.parseMsg(receiveBuffer, game)) {
				}
				receiveBuffer.compact();
			}
		} catch(IOException e) {
			logger.log(Level.WARNING, "Network connection reception error", e);
			try {
				if (channel != null) {
					channel.close();
				}
			} catch (IOException ex) {
				logger.log(Level.WARNING, "Error closing the network connection", ex);
			}
			String msg;
			if (e instanceof UnknownHostException) {
				msg = "Unknown host: " + e.getMessage();
			} else {
				msg = "Network error: " + e.getMessage();
			}
			game.networkDisconnected(true, msg);
			return false;
		}
		return true;
	}

	private void flushSendBuffer() {
		try {
			sendBuffer.flip();
			channel.write(sendBuffer);
			sendBuffer.compact();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Network connection transmission error", e);
		}
	}

	private void connectToServer() throws IOException {
		if (channel == null) {
			InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName(host), port);
			logger.log(Level.INFO, "Connecting to {0}", remote);
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(remote);
		}
		if (channel.isConnectionPending()) {
			channel.finishConnect();
		}
	}

	private void waitForClient() throws IOException {
		if (ssc == null) {
			ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			InetSocketAddress endpoint = new InetSocketAddress(port);
			logger.log(Level.INFO, "Listening on {0}", endpoint);
			ssc.socket().bind(endpoint);
		}
		if (channel == null) {
			channel = ssc.accept();
			if (channel != null) {
				channel.configureBlocking(false);
				ssc.close();
				ssc = null;
			}
		}
	}

}

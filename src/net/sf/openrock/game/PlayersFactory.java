
package net.sf.openrock.game;

import java.util.logging.Logger;

import net.sf.openrock.model.World;
import net.sf.openrock.game.PhysicsIF;
import net.sf.openrock.game.Physics;
import net.sf.openrock.model.Vect2d;

public class PlayersFactory
{
	static final Logger logger = Logger.getLogger(PlayersFactory.class.getName());

	public enum PlayersType {
		PERFECT,
		IMPERFECT,
		PERFECTSKIPS
	}

	static PlayersIF CreatePlayersByType(World world,
					PlayersType playerstype,
					int maxspeederror,
					int maxdirerror)
	{
		PlayersIF players = null;

		switch(playerstype)
		{
			case PERFECT:
			players = new PerfectPlayers(world);
			break;

			case IMPERFECT:
			players = new ImperfectPlayers(world,
					(double)maxspeederror/100,
					(double)maxdirerror/100);
			break;

			case PERFECTSKIPS:
			players = new PerfectSkips(world,
					(double)maxspeederror/100,
					(double)maxdirerror/100);
			break;
		}

		return players;
	}
}


class PerfectPlayers implements PlayersIF
{
	PhysicsIF physics;

	PerfectPlayers(World world)
	{
		physics = Physics.CreateDefaultPhysics(world);
		PlayersFactory.logger.info("perfectplayers");
	}

	public PhysicsIF getPhysics(int stones)
	{
		return physics;
	}
}

class ImperfectPlayers implements PlayersIF
{
	protected PhysicsIF[] players = new PhysicsIF[4];

	ImperfectPlayers() {
	}

	ImperfectPlayers(World world, double maxspeederror, double maxdirerror)
	{
		// make later players more skilled

		PlayersFactory.logger.info("imperfect players");

		PhysicsIF dflt = Physics.CreateDefaultPhysics(world);

		for (int i = 0; i < 4; i++)
		{
			players[i] = Physics.CreateErrorPhysics(dflt,
					maxspeederror / (i + 1),
					maxdirerror / (i + 1));
		}
	}

	public PhysicsIF getPhysics(int stones)
	{
		int player = (8 - stones) / 2;
		return players[player];
	}
}

class PerfectSkips extends ImperfectPlayers
{

	PerfectSkips(World world, double maxspeederror, double maxdirerror)
	{
		PlayersFactory.logger.info("perfect skips");

		PhysicsIF dflt = Physics.CreateDefaultPhysics(world);

		for (int i = 0; i < 3; i++)
		{
			players[i] = Physics.CreateErrorPhysics(dflt,
					maxspeederror / (i + 1),
					maxdirerror / (i + 1));
		}

		// fourth thrower has no errors
		players[3] = dflt;
	}
}

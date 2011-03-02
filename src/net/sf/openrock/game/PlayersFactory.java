
package net.sf.openrock.game;

import net.sf.openrock.model.World;
import net.sf.openrock.game.PhysicsIF;
import net.sf.openrock.game.Physics;
import net.sf.openrock.model.Vect2d;

public class PlayersFactory
{
	static PlayersIF CreatePerfectPlayers(World world)
	{
		return new PerfectPlayers(world);
	}

	static PlayersIF CreateImperfectPlayers(World world, double maxspd,
				double maxdir)
	{
		return new ImperfectPlayers(world, maxspd, maxdir);
	}

	static PlayersIF CreatePerfectSkips(World world, double maxspd,
				double maxdir)
	{
		return new PerfectSkips(world, maxspd, maxdir);
	}
}

class PerfectPlayers implements PlayersIF
{
	PhysicsIF physics;

	PerfectPlayers(World world)
	{
		physics = Physics.CreateDefaultPhysics(world);
	}

	public PhysicsIF getPhysics(int stones)
	{
		return physics;
	}
}

class ImperfectPlayers implements PlayersIF
{
	protected PhysicsIF[] players = new PhysicsIF[4];

	ImperfectPlayers() {}

	ImperfectPlayers(World world, double maxspeederror, double maxdirerror)
	{
		// make later players more skilled

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

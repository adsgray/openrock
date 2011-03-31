package net.sf.openrock.game;

import net.sf.openrock.model.CurlIF;
import net.sf.openrock.model.ScoreIF;
import net.sf.openrock.model.World;
import net.sf.openrock.model.ScoreFactory;
import net.sf.openrock.model.CurlFactory;

public class GameConfigFactory {
	static GameConfig instance() {
		return new DefaultConfig();
	}
}

class DefaultConfig implements GameConfig
{
	PlayersIF players;
	CurlIF    curl;
	ScoreIF score;

	public PlayersIF getPlayers() { return players; }
	public CurlIF    getCurl() { return curl; }
	public ScoreIF   getScore() { return score; }

	public void createPlayers(World world,
				  int playerstype,
	                          int maxspeederror,
				  int maxdirerror)
	{
		players = PlayersFactory.CreatePlayersByType(world,
			PlayersFactory.PlayersType.values()[playerstype],
			maxspeederror,
			maxdirerror);
	}

	public void createCurl(int curltype)
	{
		curl = CurlFactory.CreateCurlByType(
			CurlFactory.CurlType.values()[curltype]);
	}

	public void createScoring(World world, int scoringtype)
	{
		switch (scoringtype) {
			case 0:
			score = ScoreFactory.CreateNormalScore(world);
			break;

			case 1:
			score = ScoreFactory.CreateSkinsScore(world);
			break;
		}
	}

}

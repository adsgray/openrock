package net.sf.openrock.game;

// move these all into a GameConfig/ directory?
import net.sf.openrock.model.World;
import net.sf.openrock.model.CurlIF;
import net.sf.openrock.model.ScoreIF;

public interface GameConfig {
	public PlayersIF getPlayers();
	public ScoreIF getScore();
	public CurlIF getCurl();

	public void createPlayers(World world,
				  int playerstype,
	                          int maxspeederror,
				  int maxdirerror);

	public void createCurl(int curltype);

	public void createScoring(World world, int scoringtype);
}

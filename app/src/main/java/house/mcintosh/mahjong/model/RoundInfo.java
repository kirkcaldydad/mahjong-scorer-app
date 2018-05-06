package house.mcintosh.mahjong.model;

import java.util.Map;

public interface RoundInfo
{
	public boolean hasRound();

	public Round getRound();

	public Player getPlayer(int position);

	public Wind getPlayerWind(Player player);

	public int getScore(Player player);

	public int getScoreIncrement(Player player);
}

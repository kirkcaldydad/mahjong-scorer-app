package house.mcintosh.mahjong.model;

import java.util.Map;

import house.mcintosh.mahjong.exception.InternalException;

public class StartingScoreInfo implements RoundInfo
{
	private final Player[] m_seats;
	private final Map<Player, Integer> m_totalScores;
	private final Map<Player, Wind> m_playerWinds;

	public StartingScoreInfo(Player[] seats, Map<Player, Integer> totalScores, Map<Player, Wind> playerWinds)
	{
		m_seats = seats;
		m_totalScores = totalScores;
		m_playerWinds = playerWinds;
	}

	@Override
	public boolean hasRound()
	{
		return false;
	}

	@Override
	public Round getRound()
	{
		throw new InternalException("Cannot get round from StartingScoreInfo");
	}

	@Override
	public Player getPlayer(int position)
	{
		return m_seats[position];
	}

	@Override
	public Wind getPlayerWind(Player player)
	{
		return m_playerWinds.get(player);
	}

	@Override
	public int getScore(Player player)
	{
		return m_totalScores.get(player);
	}

	@Override
	public int getScoreIncrement(Player player)
	{
		throw new InternalException("Cannot get score increment from StartingScoreInfo");
	}
}

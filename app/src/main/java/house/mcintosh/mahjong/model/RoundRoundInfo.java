package house.mcintosh.mahjong.model;

import java.util.Map;

public class RoundRoundInfo implements RoundInfo
{
	private final Round m_round;
	private final Player[] m_seats;
	private final Map<Player, Integer> m_totalScores;
	private final Map<Player, Integer> m_scoreIncrements;

	public RoundRoundInfo(Round round, Player[] seats, Map<Player, Integer> totalScores, Map<Player, Integer> scoreIncrements)
	{
		m_round = round;
		m_seats = seats;

		m_totalScores = totalScores;
		m_scoreIncrements = scoreIncrements;
	}

	@Override
	public boolean hasRound()
	{
		return true;
	}

	@Override
	public Round getRound()
	{
		return m_round;
	}

	@Override
	public Player getPlayer(int position)
	{
		return m_seats[position];
	}

	@Override
	public Wind getPlayerWind(Player player)
	{
		return m_round.getPlayerWind(player);
	}

	@Override
	public int getScore(Player player)
	{
		return m_totalScores.get(player);
	}

	@Override
	public int getScoreIncrement(Player player)
	{
		return m_scoreIncrements.get(player);
	}
}

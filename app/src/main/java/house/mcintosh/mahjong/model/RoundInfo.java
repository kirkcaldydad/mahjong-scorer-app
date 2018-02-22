package house.mcintosh.mahjong.model;

import java.util.Map;

public class RoundInfo
{
	private final Round m_round;
	private final Player[] m_seats;
	private final Map<Player, Integer> m_totalScores;
	private final Map<Player, Integer> m_scoreIncrements;

	public RoundInfo(Round round, Player[] seats, Map<Player, Integer> totalScores, Map<Player, Integer> scoreIncrements)
	{
		m_round = round;
		m_seats = seats;

		m_totalScores = totalScores;
		m_scoreIncrements = scoreIncrements;
	}

	public Round getRound()
	{
		return m_round;
	}

	public Player getPlayer(int position)
	{
		return m_seats[position];
	}

	public int getScore(Player player)
	{
		return m_totalScores.get(player);
	}

	public int getScoreIncrement(Player player)
	{
		return m_scoreIncrements.get(player);
	}
}

package house.mcintosh.mahjong.model;

public class RoundInfo
{
	private final Round m_round;
	private final Player[] m_seats;

	public RoundInfo(Round round, Player[] seats)
	{
		m_round = round;
		m_seats = seats;
	}

	public Round getRound()
	{
		return m_round;
	}

	public Player getPlayer(int position)
	{
		return m_seats[position];
	}
}

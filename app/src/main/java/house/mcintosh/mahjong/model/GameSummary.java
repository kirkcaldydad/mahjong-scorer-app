package house.mcintosh.mahjong.model;

/**
 * A reduced representation of a Game.
 */

public class GameSummary
{
	private final String m_playerNames;
	private final String m_createdOn;
	private final String m_lastModifiedOn;

	public GameSummary(String playerNames, String createdOn, String lastModifiedOn)
	{
		m_playerNames		= playerNames;
		m_createdOn			= createdOn;
		m_lastModifiedOn	= lastModifiedOn;
	}

	public String getPlayerNames()
	{
		return m_playerNames;
	}

	public String getCreatedOn()
	{
		return m_createdOn;
	}

	public String getLastModifiedOn()
	{
		return m_lastModifiedOn;
	}
}

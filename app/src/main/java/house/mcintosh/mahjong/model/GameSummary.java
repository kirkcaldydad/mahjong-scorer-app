package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

import house.mcintosh.mahjong.scoring.ScoringScheme;

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

	static public GameSummary fromJson(ObjectNode gameNode)
	{
		// Create all the players so that they are available by ID from the cache inside Players.
		ArrayNode playersNode = (ArrayNode)gameNode.get("players");

		for (JsonNode playerNode : playersNode)
		{
			Player.fromJson(playerNode);
		}

		// Get the player names in seat order.
		ArrayNode seatsNode = (ArrayNode)gameNode.get("seats");

		StringBuilder sb = new StringBuilder();

		for (int i = 0 ; i < seatsNode.size() ; i++)
		{
			JsonNode seatNode = seatsNode.get(i);

			if (!seatNode.has("playerId"))
				continue;

			Player player = Player.get(new PlayerId(seatNode.get("playerId")));

			if (sb.length() > 0)
				sb.append(", ");

			sb.append(player.getName());
		}

		return new GameSummary(sb.toString(), "now", "later");
	}
}

package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import house.mcintosh.mahjong.scoring.ScoringScheme;

/**
 * A reduced representation of a Game.
 */

public class GameSummary
{
	private final static String DATE_DISPLAY_FORMAT = "d MMMM yyyy";

	private final String m_playerNames;
	private final GameMeta m_meta;

	public GameSummary(String playerNames, GameMeta meta)
	{
		m_playerNames		= playerNames;
		m_meta				= meta;
	}

	public String getPlayerNames()
	{
		return m_playerNames;
	}

	public String getCreatedOn()
	{
		Date createdOn = m_meta.getCreatedOn();

		// Convert to a display value.

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_DISPLAY_FORMAT);

		return new SimpleDateFormat(DATE_DISPLAY_FORMAT).format(createdOn);
	}

	public String getLastModifiedOn()
	{
		Date lastModifiedOn = m_meta.getLastModifiedOn();

		return new SimpleDateFormat(DATE_DISPLAY_FORMAT).format(lastModifiedOn);
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

		GameMeta meta = GameMeta.fromJson(gameNode.path("meta"));

		return new GameSummary(sb.toString(), meta);
	}
}

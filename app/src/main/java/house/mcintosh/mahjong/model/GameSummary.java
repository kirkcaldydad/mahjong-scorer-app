package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A reduced representation of a Game.
 */

public final class GameSummary
{
	private final static String DATE_DISPLAY_FORMAT = "d MMMM yyyy";

	private final List<Player> m_players;
	private final Map<Player, Integer> m_scores;
	private final int m_highestScore;
	private final GameMeta m_meta;
	private final File m_file;

	private GameSummary(List<Player> players, Map<Player, Integer> scores, int highestScore, GameMeta meta, File file)
	{
		m_players = players;
		m_scores = scores;
		m_highestScore = highestScore;
		m_meta = meta;
		m_file = file;
	}

	public List<Player> getPlayers()
	{
		return m_players;
	}

	public int getScore(Player player)
	{
		return m_scores.get(player);
	}

	public Integer getHighestScore()
	{
		return m_highestScore;
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

	public File getFile()
	{
		return m_file;
	}

	static public GameSummary fromJson(ObjectNode gameNode, File file)
	{
		// Create all the players so that they are available by ID from the cache inside Players.
		ArrayNode playersNode = (ArrayNode)gameNode.get("players");

		for (JsonNode playerNode : playersNode)
		{
			Player.fromJson(playerNode);
		}

		// Load the player scores, and find the highest score.
		int highestScore = Integer.MIN_VALUE;
		Map<Player, Integer> scores = new HashMap<>();

		JsonNode scoresNode = gameNode.get("scores");

		Iterator<Map.Entry<String, JsonNode>> fields = scoresNode.fields();

		while (fields.hasNext())
		{
			Map.Entry<String, JsonNode> field = fields.next();

			PlayerId playerId = new PlayerId(field.getKey());
			int score = field.getValue().asInt();

			highestScore = Math.max(highestScore, score);

			Player player = Player.get(playerId);

			scores.put(player, score);
		}

		// Get the player in seat order.
		ArrayNode seatsNode = (ArrayNode)gameNode.get("seats");
		List<Player> players = new ArrayList<>(seatsNode.size());

		for (int i = 0 ; i < seatsNode.size() ; i++)
		{
			JsonNode seatNode = seatsNode.get(i);

			if (!seatNode.has("playerId"))
				continue;

			Player player = Player.get(new PlayerId(seatNode.get("playerId")));

			players.add(player);
		}

		GameMeta meta = GameMeta.fromJson(gameNode.path("meta"));

		return new GameSummary(players, scores, highestScore, meta, file);
	}
}

package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.util.JsonUtil;

/**
 * A Round is a set of hands - one for each player.
 */
public final class Round
{
	private final Map<Player, Entry>	m_entries			= new HashMap<>();
	private final Wind					m_prevailingWind;
	
	public Round(Wind prevailingWind)
	{
		m_prevailingWind = prevailingWind;
	}

	/**
	 * Add a hand into the round.  If there is already a hand for the player it is replaced.
	 */
	public void addHand(Player player, ScoredHand hand, Wind playerWind)
	{
		m_entries.put(player, new Entry(player, hand, playerWind));
	}

	/**
	 * @return true if this round contains no hands.
	 */
	public boolean isEmpty()
	{
		return m_entries.isEmpty();
	}

	/**
	 * @return The number of hands currently added into this round.
	 */
	public int getHandCount()
	{
		return m_entries.size();
	}

	/**
	 * Gets all the players that have a Mahjong hand in this Round.  For a valid Round there
	 * should be exactly one Mahjong hand, but we allow more or fewer to allow the round to
	 * be built up and edited before it is added to a Game.
	 */
	public List<Player> getMahjongPlayers()
	{
		List<Player> mahjongPlayers = new ArrayList<>(1);

		for (Entry entry : m_entries.values())
		{
			if (entry.hand.isMahjong())
				mahjongPlayers.add(entry.player);
		}

		return mahjongPlayers;
	}

	public Wind getPrevailingWind()
	{
		return m_prevailingWind;
	}
	
	public Wind getPlayerWind(Player player)
	{
		return m_entries.get(player).playerWind;
	}
	
	public ScoredHand getHand(Player player)
	{
		return m_entries.get(player).hand;
	}

	public ObjectNode toJson()
	{
		ObjectNode round = JsonUtil.createObjectNode();

		round.put("prevailingWind", m_prevailingWind.name());

		ArrayNode entries = JsonUtil.createArrayNode();

		for (Entry entry : m_entries.values())
		{
			entries.add(entry.toJson());
		}

		round.set("hands", entries);

		return round;
	}

	static public Round fromJson(JsonNode roundNode, ScoringScheme scheme)
	{
		Wind prevailingWind = Wind.valueOf(roundNode.get("prevailingWind").asText());

		Round round = new Round(prevailingWind);

		ArrayNode handEntryNodes = (ArrayNode)roundNode.get("hands");

		for (JsonNode entryNode : handEntryNodes)
		{
			Entry entry = Entry.fromJson(entryNode, scheme, prevailingWind);

			round.addHand(entry.player, entry.hand, entry.playerWind);
		}

		return round;
	}
	
	public int getPlayerScore(Player player)
	{


		// If there are only two players, need to calculate the score in a different way, because
		// following the normal scoring with only two players does nothing with the non-mahjong
		// player's score.

		if (m_entries.size() == 2)
			return calculateTwoPlayerScore(player);

		return calculateThreeFourPlayerScore(player);
	}

	private int calculateThreeFourPlayerScore(Player player)
	{
		int score = 0;

		Entry thisPlayerEntry = m_entries.get(player);

		if (thisPlayerEntry.hand.isMahjong())
		{
			// Calculate score based on this being the mahjong player.

			Entry receivingEntry = thisPlayerEntry;

			for (Entry givingEntry : m_entries.values())
			{
				if (givingEntry.player.equals(player))
					continue;

				int eastMultiplier = 1;

				if (receivingEntry.playerWind == Wind.EAST || givingEntry.playerWind == Wind.EAST)
					eastMultiplier = 2;

				score += receivingEntry.hand.getTotalScore() * eastMultiplier;
			}
		}
		else
		{
			// Calculate score based on this not being mahjong player.

			for (Entry thatPlayerEntry : m_entries.values())
			{
				if (thatPlayerEntry.player.equals(player))
					continue;

				int eastMultiplier = 1;

				if (thisPlayerEntry.playerWind == Wind.EAST || thatPlayerEntry.playerWind == Wind.EAST)
					eastMultiplier = 2;

				if (thatPlayerEntry.hand.isMahjong())
					score -= thatPlayerEntry.hand.getTotalScore() * eastMultiplier;
				else
					score += (thisPlayerEntry.hand.getTotalScore() - thatPlayerEntry.hand.getTotalScore()) * eastMultiplier;
			}
		}

		return score;
	}

	private int calculateTwoPlayerScore(Player player)
	{
		Entry thatPlayerEntry = null;

		Entry thisPlayerEntry = m_entries.get(player);



		for (Entry playerEntry : m_entries.values())
		{
			if (!playerEntry.player.equals(player))
			{
				thatPlayerEntry = playerEntry;
				break;
			}
		}

		int thisHandScore;
		int thatHandScore;

		if (thisPlayerEntry.hand.isMahjong())
		{
			thisHandScore = 2 * thisPlayerEntry.hand.getTotalScore();
			thatHandScore = thatPlayerEntry.hand.getTotalScore();
		}
		else
		{
			thisHandScore = thisPlayerEntry.hand.getTotalScore();
			thatHandScore = 2 * thatPlayerEntry.hand.getTotalScore();
		}

		int score = thisHandScore - thatHandScore;

		return score;
	}

	public boolean hasHandForAll(Collection players)
	{
		return m_entries.keySet().containsAll(players);
	}

	public boolean hasHandFor(Player player)
	{
		return m_entries.keySet().contains(player);
	}

	static private class Entry
	{
		private final Player		player;
		private final ScoredHand	hand;
		private final Wind			playerWind;
		
		private Entry(Player player, ScoredHand hand, Wind playerWind)
		{
			this.player		= player;
			this.hand		= hand;
			this.playerWind	= playerWind;
		}

		static Entry fromJson (JsonNode entryNode, ScoringScheme scheme, Wind prevailingWind)
		{
			Player player = Player.get(new PlayerId(entryNode.get("playerId")));
			Wind playerWind = Wind.valueOf(entryNode.get("playerWind").asText());
			ScoredHand hand = ScoredHand.fromJson(entryNode.get("hand"), scheme, playerWind, prevailingWind);

			return new Entry(player, hand, playerWind);
		}

		ObjectNode toJson()
		{
			ObjectNode entry = JsonUtil.createObjectNode();

			entry.put("playerId", player.getId());
			entry.set("hand", hand.toJson());
			entry.put("playerWind", playerWind.name());

			return entry;
		}
	}
}

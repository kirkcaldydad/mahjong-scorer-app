package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.exception.InvalidModelException;
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
	private Player						m_mahjongPlayer;
	
	public Round(Wind prevailingWind)
	{
		m_prevailingWind = prevailingWind;
	}
	
	public void addHand(Player player, ScoredHand hand, Wind playerWind)
	{
		if (m_entries.containsKey(player))
			throw new InvalidModelException("Duplicate player");
		
		if (hand.isMahjong())
		{
			if (m_mahjongPlayer != null)
				throw new InvalidModelException("Already got mahjong hand in round");
			
			m_mahjongPlayer = player;
		}
		
		m_entries.put(player, new Entry(player, hand, playerWind));
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
		if (m_mahjongPlayer == null)
			throw new InvalidModelException("Round has no mahjong hand");
		
		int score = 0;
		
		if (player.equals(m_mahjongPlayer))
		{
			// Calculate score based on this being the mahjong player.
			
			Entry receivingEntry = m_entries.get(player);
			
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
			
			Entry thisPlayerEntry = m_entries.get(player);
			
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

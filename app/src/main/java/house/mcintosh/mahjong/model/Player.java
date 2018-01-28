package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.util.JsonUtil;

public final class Player implements Serializable
{
	/** All the instances that have been created so far. */
	static private final Set<Player> s_allPlayers = new HashSet<>();
	
	private String m_name;

	private final PlayerId m_id;
	
	/**
	 * Private constructor so that instance must be created through factory methods to
	 * ensure that there is only one instance for each Player.
	 */
	private Player(String name, PlayerId id)
	{
		m_name	= name;
		m_id	= id;
	}
	
	/**
	 * Create a new Player instance from a json node.
	 */
	static public synchronized Player fromJson(JsonNode playerNode)
	{
		PlayerId	id		= new PlayerId(playerNode.get("id"));
		String		name	= playerNode.get("name").asText("");
		Player		newOne	= new Player(name, id);
		
		for (Player player : s_allPlayers)
			if (player.equals(newOne))
				return player;
		
		s_allPlayers.add(newOne);
		
		return newOne;
	}

	static public Player create(String name)
	{
		PlayerId id = PlayerId.create();

		return new Player(name, id);
	}

	static public synchronized Player get(PlayerId id)
	{
		for (Player player : s_allPlayers)
			if (player.m_id.equals(id))
				return player;

		throw new InvalidModelException("Cannot find player with id");
	}
	
	/** Override so that instances can be used as keys in maps and sets */
	@Override
	public int hashCode()
	{
		return m_id.hashCode();
	}
	
	/** Override so that instances can be used as keys in maps and sets */
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Player))
			return false;
		
		Player otherPlayer = (Player)other;
		
		return m_id.equals(otherPlayer.m_id);
	}

	public String getName()
	{
		return m_name;
	}

	/**
	 * Update the name of the player.  Since each player is a singleton, this magically updates
	 * the name of all uses of the player.  Changing the name does not change the use of this
	 * player as a key in sets and maps, since equals() and hashCode() are based on the unique
	 * ID only.
	 */
	public void setName(String name)
	{
		m_name = name;
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}

	public String getId()
	{
		return m_id.getValue();
	}

	public ObjectNode toJson()
	{
		ObjectNode player = JsonUtil.createObjectNode();

		player.put("name", m_name);
		player.put("id", m_id.getValue());

		return player;
	}
}

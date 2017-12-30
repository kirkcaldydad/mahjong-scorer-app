package house.mcintosh.mahjong.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import house.mcintosh.mahjong.exception.LoadException;
import house.mcintosh.mahjong.util.JsonUtil;

/**
 * A tile in a hand.
 */
public final class Tile
{
	//
	// The ordering of these enumerations affects the sort order of Sets in a Hand.
	//
	
	public enum Suit
	{
		CIRCLES, CHARACTERS, BAMBOO;

		public final String drawableNameComponent;

		Suit()
		{
			drawableNameComponent = this.name().toLowerCase();
		}
	}

	public enum Number
	{
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

		public final String drawableNameComponent;

		Number()
		{
			drawableNameComponent = Integer.toString(this.ordinal() + 1);
		}

		Number next()
		{
			return Number.values()[this.ordinal() + 1];
		}
	}

	public enum Dragon
	{
		RED, WHITE, GREEN;

		public final String drawableNameComponent;

		Dragon()
		{
			drawableNameComponent = this.name().toLowerCase();
		}
	}

	public enum Type
	{
		DRAGON, WIND, SUIT;

		public final String drawableNameComponent;

		Type()
		{
			drawableNameComponent = this.name().toLowerCase();
		}
	}

	private final Suit m_suit;
	private final Number m_number;
	private final Wind m_wind;
	private final Dragon m_dragon;
	private final Type m_type;

	private Tile(Suit suit, Number number, Wind wind, Dragon dragon, Type type)
	{
		this.m_suit = suit;
		this.m_number = number;
		this.m_wind = wind;
		this.m_dragon = dragon;
		this.m_type = type;
	}

	public Tile(Suit suit, Number number)
	{
		this(suit, number, null, null, Type.SUIT);
	}

	public Tile(Wind wind)
	{
		this(null, null, wind, null, Type.WIND);
	}

	public Tile(Dragon dragon)
	{
		this(null, null, null, dragon, Type.DRAGON);
	}

	public Suit getSuit()
	{
		return m_suit;
	}

	public Number getNumber()
	{
		return m_number;
	}

	public Wind getWind()
	{
		return m_wind;
	}

	public Dragon getDragon()
	{
		return m_dragon;
	}

	public Type getType()
	{
		return m_type;
	}

	public boolean isMajor()
	{
		switch (this.m_type)
		{
		case SUIT:
			switch (this.m_number)
			{
			case ONE:
			case NINE:
				return true;
			default:
				return false;
			}

		default:
			return true;
		}
	}

	/**
	 * Creates a new tile in same suit as the current tile but with the next number
	 * in sequence.
	 * 
	 * Only valid to be called for SUIT types with numbers up to EIGHT.
	 */
	public Tile createNextNumber()
	{
		return new Tile(this.m_suit, this.m_number.next());
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof Tile))
			return false;

		Tile that = (Tile)other;

		return     this.m_type == that.m_type
				&& this.m_dragon == that.m_dragon
				&& this.m_wind == that.m_wind
				&& this.m_suit == that.m_suit
				&& this.m_number == that.m_number;
	}

	public int hashCode()
	{
		int hash = m_type.hashCode();

		if (m_dragon != null)
			hash ^= m_dragon.hashCode();

		if (m_wind != null)
			hash ^= m_wind.hashCode();

		if (m_suit != null)
			hash ^= m_suit.hashCode();

		if (m_number != null)
			hash ^= m_number.hashCode();

		return hash;
	}

	public ObjectNode toJson()
	{
		ObjectNode tile = JsonUtil.createObjectNode();

		switch (m_type)
		{
			case DRAGON:
				tile.put("dragon", m_dragon.name());
				break;

			case WIND:
				tile.put("wind", m_wind.name());
				break;

			case SUIT:
				tile.put("suit", m_suit.name());
				tile.put("number", m_number.name());
				break;
		}

		return tile;
	}

	static public Tile fromJson(JsonNode node)
	{
		if (node.has("dragon"))
			return new Tile(Dragon.valueOf(node.get("dragon").asText()));

		if (node.has("wind"))
			return new Tile(Wind.valueOf(node.get("wind").asText()));

		if (node.has("suit"))
			return new Tile(Suit.valueOf(node.get("suit").asText()), Number.valueOf(node.get("number").asText()));

		throw new LoadException("Invalid Tile JSON");
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append('[');
			
		switch(m_type)
		{
		case DRAGON:
			sb.append(m_dragon).append(" DRAGON");
			break;
			
		case WIND:
			sb.append(m_wind).append(" WIND");
			break;
			
		case SUIT:
			sb.append(m_number).append(' ').append(m_suit);
			break;
		}
		
		sb.append(']');
		
		return sb.toString();
	}
}

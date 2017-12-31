package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.util.JsonUtil;

/**
 * A Set contains a number of Tiles, contributing to a Hand.
 */
public class Group implements Serializable
{
	public enum Type
	{
		PAIR(2, 2),
		CHOW(3, 3),
		PUNG(3, 3),
		KONG(4, 3);

		private final int size;
		private final int handSize;

		Type(int size, int handSize)
		{
			this.size = size;
			this.handSize = handSize;
		}

		public int getHandSize()
		{
			return handSize;
		}
	}

	public enum Visibility
	{
		EXPOSED,
		CONCEALED
	}

	private final Type m_type;
	/**
	 * List of the tiles in this set.  Unmodifiable list that is only ever created on construction.
	 */
	private final List<Tile> m_tiles;
	private final Visibility m_visibility;

	/**
	 * Construct a new instance from an existing instance.  Intended for use by sub-class.
	 * <p>
	 * Underlying data structures are shared with the other Set, but they are immutable.
	 */
	protected Group(Group other)
	{
		this.m_type = other.m_type;
		this.m_tiles = other.m_tiles;
		this.m_visibility = other.m_visibility;
	}

	public Group(Type type, Tile tile, Visibility visibility)
	{
		this.m_type = type;
		List<Tile> tiles = new ArrayList<>(type.size);

		switch (type)
		{
			case PAIR:
				this.m_visibility = Visibility.EXPOSED;
				addIdenticalTiles(tiles, tile, type.size);
				break;

			case CHOW:
				this.m_visibility = Visibility.EXPOSED;
				addChowTiles(tiles, tile);
				break;

			default:
				this.m_visibility = visibility;
				addIdenticalTiles(tiles, tile, type.size);
				break;
		}

		this.m_tiles = Collections.unmodifiableList(tiles);
	}

	public Group(Type type, Tile tile)
	{
		this(type, tile, Visibility.EXPOSED);

		// Only pair or chow can be created without specifying visibility.
		switch (type)
		{
			case PAIR:
			case CHOW:
				break;

			default:
				throw new InvalidModelException("Visibility required");
		}
	}

	public Type getType()
	{
		return this.m_type;
	}

	public Visibility getVisibility()
	{
		return this.m_visibility;
	}

	public boolean isConcealed()
	{
		return this.m_visibility == Visibility.CONCEALED;
	}

	public List<Tile> getTiles()
	{
		return this.m_tiles;
	}

	public Tile getFirstTile()
	{
		return this.m_tiles.get(0);
	}

	public Tile.Type getTileType()
	{
		return this.m_tiles.get(0).getType();
	}

	private final void addIdenticalTiles(List<Tile> tiles, Tile tile, int size)
	{
		while (size-- > 0)
			tiles.add(tile);
	}

	private void addChowTiles(List<Tile> tiles, Tile tile)
	{
		// Be tolerant of dodgy initial tile specified.

		Tile.Number number = tile.getNumber();

		if (number == Tile.Number.EIGHT || number == Tile.Number.NINE)
			tile = new Tile(tile.getSuit(), Tile.Number.SEVEN);

		tiles.add(tile);
		tile = tile.createNextNumber();
		tiles.add(tile);
		tile = tile.createNextNumber();
		tiles.add(tile);
	}

	public ObjectNode toJson()
	{
		ObjectNode group = JsonUtil.createObjectNode();

		group.put("type", m_type.name());
		group.put("visibility", m_visibility.name());

		group.set("firstTile", m_tiles.get(0).toJson());

		return group;
	}

	static public Group fromJson(JsonNode group)
	{
		Type type = Type.valueOf(group.get("type").asText());
		Visibility visibility = Visibility.valueOf(group.get("visibility").asText());
		Tile firstTile = Tile.fromJson(group.get("firstTile"));

		return new Group(type, firstTile, visibility);
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb
				.append('[')
				.append(m_visibility)
				.append(',')
				.append(m_type)
				.append(',');

		boolean first = true;
		for (Tile tile : m_tiles)
		{
			if (!first)
			{
				sb.append(',');
				first = false;
			}
			sb.append(tile);
		}

		sb.append(']');

		return sb.toString();
	}
}

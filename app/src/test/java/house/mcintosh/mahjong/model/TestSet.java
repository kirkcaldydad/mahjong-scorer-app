package house.mcintosh.mahjong.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;

public class TestSet
{
	@Test
	public void createChow1()
	{
		Group chow = new Group(Group.Type.CHOW, new Tile(Tile.Suit.BAMBOO, Tile.Number.ONE), Group.Visibility.EXPOSED);
		
		assertEquals(Group.Visibility.EXPOSED, chow.getVisibility());
		
		List<Tile> tiles = chow.getTiles();
		
		assertEquals(3, tiles.size());
		assertEquals(Tile.Number.ONE,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.TWO,	tiles.get(1).getNumber());
		assertEquals(Tile.Number.THREE,	tiles.get(2).getNumber());

		assertEquals(Tile.Suit.BAMBOO,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(1).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(2).getSuit());
		
		// Check that we cannot change the list.
		
		try
		{
			tiles.add(new Tile(Tile.Suit.BAMBOO, Tile.Number.FOUR));
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			// OK
		}
		
		assertEquals(Group.Type.CHOW, chow.getType());
	}
	
	@Test
	public void createChow4()
	{
		Group chow = new Group(Group.Type.CHOW, new Tile(Tile.Suit.CIRCLES, Tile.Number.FOUR), Group.Visibility.CONCEALED);
		
		// Visiblity is overridden to be exposed.
		assertEquals(Group.Visibility.EXPOSED, chow.getVisibility());
		
		List<Tile> tiles = chow.getTiles();
		
		assertEquals(3, tiles.size());
		assertEquals(Tile.Number.FOUR,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.FIVE,	tiles.get(1).getNumber());
		assertEquals(Tile.Number.SIX,	tiles.get(2).getNumber());

		assertEquals(Tile.Suit.CIRCLES,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.CIRCLES,	tiles.get(1).getSuit());
		assertEquals(Tile.Suit.CIRCLES,	tiles.get(2).getSuit());
	}

	@Test
	public void createChow8()
	{
		Group chow = new Group(Group.Type.CHOW, new Tile(Tile.Suit.BAMBOO, Tile.Number.EIGHT));
		
		// Visibility defaults to exposed.
		assertEquals(Group.Visibility.EXPOSED, chow.getVisibility());
		
		List<Tile> tiles = chow.getTiles();
		
		assertEquals(3, tiles.size());
		assertEquals(Tile.Number.SEVEN,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.EIGHT,	tiles.get(1).getNumber());
		assertEquals(Tile.Number.NINE,	tiles.get(2).getNumber());

		assertEquals(Tile.Suit.BAMBOO,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(1).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(2).getSuit());
	}

	@Test
	public void createChow9()
	{
		Group chow = new Group(Group.Type.CHOW, new Tile(Tile.Suit.CIRCLES, Tile.Number.NINE), Group.Visibility.EXPOSED);
		
		assertEquals(Group.Visibility.EXPOSED, chow.getVisibility());
		
		List<Tile> tiles = chow.getTiles();
		
		assertEquals(3, tiles.size());
		assertEquals(Tile.Number.SEVEN,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.EIGHT,	tiles.get(1).getNumber());
		assertEquals(Tile.Number.NINE,	tiles.get(2).getNumber());

		assertEquals(Tile.Suit.CIRCLES,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.CIRCLES,	tiles.get(1).getSuit());
		assertEquals(Tile.Suit.CIRCLES,	tiles.get(2).getSuit());
	}

	@Test
	public void createPungWind()
	{
		Group chow = new Group(Group.Type.PUNG, new Tile(Wind.SOUTH), Group.Visibility.CONCEALED);
		
		assertEquals(Group.Visibility.CONCEALED, chow.getVisibility());
		
		List<Tile> tiles = chow.getTiles();
		
		assertEquals(3, tiles.size());
		assertEquals(Tile.Type.WIND,		tiles.get(0).getType());
		assertEquals(Tile.Type.WIND,		tiles.get(1).getType());
		assertEquals(Tile.Type.WIND,		tiles.get(2).getType());
		
		assertEquals(Wind.SOUTH,	tiles.get(0).getWind());
		assertEquals(Wind.SOUTH,	tiles.get(1).getWind());
		assertEquals(Wind.SOUTH,	tiles.get(2).getWind());
	}
	
	@Test
	public void createKongDragon()
	{
		Group chow = new Group(Group.Type.KONG, new Tile(Tile.Dragon.WHITE), Group.Visibility.CONCEALED);
		
		assertEquals(Group.Visibility.CONCEALED, chow.getVisibility());
		
		List<Tile> tiles = chow.getTiles();
		
		assertEquals(4, tiles.size());
		assertEquals(Tile.Dragon.WHITE,	tiles.get(0).getDragon());
		assertEquals(Tile.Dragon.WHITE,	tiles.get(1).getDragon());
		assertEquals(Tile.Dragon.WHITE,	tiles.get(2).getDragon());
		assertEquals(Tile.Dragon.WHITE,	tiles.get(3).getDragon());

		assertEquals(Tile.Type.DRAGON,	tiles.get(0).getType());
		assertEquals(Tile.Type.DRAGON,	tiles.get(1).getType());
		assertEquals(Tile.Type.DRAGON,	tiles.get(2).getType());
		assertEquals(Tile.Type.DRAGON,	tiles.get(3).getType());
	}
	
	@Test
	public void createPairLong()
	{
		Group pair = new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.FIVE), Group.Visibility.EXPOSED);
		
		assertEquals(Group.Visibility.EXPOSED, pair.getVisibility());
		
		List<Tile> tiles = pair.getTiles();
		assertEquals(2, tiles.size());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(1).getSuit());
		
		assertEquals(Tile.Number.FIVE,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.FIVE,	tiles.get(1).getNumber());
	}
	
	@Test
	public void createPairShort()
	{
		Group pair = new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.FIVE));
		
		assertEquals(Group.Visibility.EXPOSED, pair.getVisibility());
		
		List<Tile> tiles = pair.getTiles();
		assertEquals(2, tiles.size());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(1).getSuit());
		
		assertEquals(Tile.Number.FIVE,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.FIVE,	tiles.get(1).getNumber());
	}
	
	@Test
	public void createPairConcealedOverride()
	{
		Group pair = new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.FIVE), Group.Visibility.CONCEALED);
		
		assertEquals(Group.Visibility.EXPOSED, pair.getVisibility());
		
		List<Tile> tiles = pair.getTiles();
		assertEquals(2, tiles.size());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(0).getSuit());
		assertEquals(Tile.Suit.BAMBOO,	tiles.get(1).getSuit());
		
		assertEquals(Tile.Number.FIVE,	tiles.get(0).getNumber());
		assertEquals(Tile.Number.FIVE,	tiles.get(1).getNumber());
	}
}

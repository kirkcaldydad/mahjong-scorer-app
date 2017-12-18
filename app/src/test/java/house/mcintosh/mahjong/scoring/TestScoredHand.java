package house.mcintosh.mahjong.scoring;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import house.mcintosh.mahjong.exception.InvalidHandException;
import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.model.Wind;

public class TestScoredHand
{
	@Test
	public void setSorting()
	{
		
		// A bunch of sets to be added to a Hand, in the order that we expect them to be
		// sorted in the hand.
				
		List<ScoredGroup> sets	= new ArrayList<>();
		ScoringScheme		scheme	= ScoringScheme.instance();

		sets.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Dragon.RED), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Dragon.GREEN), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.EAST), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Wind.SOUTH), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.WEST), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.CHARACTERS, Tile.Number.FIVE), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.CHARACTERS, Tile.Number.EIGHT), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CIRCLES, Tile.Number.SIX), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Dragon.WHITE)), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Wind.EAST)), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Wind.NORTH), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Suit.CHARACTERS, Tile.Number.FOUR)), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN)), scheme, Wind.EAST, Wind.EAST));
		sets.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Suit.CIRCLES, Tile.Number.ONE)), scheme, Wind.EAST, Wind.EAST));
		
		int iterations = 1000;
		
		while (iterations-- > 0)
		{
			buildAndCheckHandRandomOrder(sets);
		}
	}
	
	@Test
	public void testMahjongHands()
	{
		ScoringScheme		scheme	= ScoringScheme.instance();
		ScoredHand		hand	= new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Dragon.RED), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(4*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(4*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals((4+16)*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Wind.EAST)), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals((4+16+2)*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Wind.SOUTH), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		assertTrue(hand.isMahjong());
		assertEquals((10+4+16+2+4)*2, hand.getTotalScoreUnlimited());
		

		hand = new ScoredHand(scheme);
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Dragon.WHITE)), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CIRCLES, Tile.Number.SIX), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.WEST), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals((2+16), hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Dragon.GREEN), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals((2+16+32)*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.EAST), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		assertTrue(hand.isMahjong());
		assertEquals((10+2+16+32+16)*2*2*2*2, hand.getTotalScoreUnlimited());

		hand = new ScoredHand(scheme);
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.CHARACTERS, Tile.Number.FIVE), Group.Visibility.CONCEALED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(4, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.CHARACTERS, Tile.Number.EIGHT), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(4+2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.NORTH), Group.Visibility.EXPOSED), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(4+2+16, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CHARACTERS, Tile.Number.FOUR)), scheme, Wind.EAST, Wind.EAST));
		assertFalse(hand.isMahjong());
		assertEquals(4+2+16, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN)), scheme, Wind.EAST, Wind.EAST));
		assertTrue(hand.isMahjong());
		assertEquals(10+4+2+16, hand.getTotalScoreUnlimited());
	}
	
	@Test
	public void testLimit()
	{
		ScoringScheme		scheme	= ScoringScheme.instance();
		ScoredHand		hand	= new ScoredHand(scheme);
		

		hand = new ScoredHand(scheme);
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Dragon.WHITE)), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals(2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.NORTH), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals((2+32)*2*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Dragon.RED), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals((2+32+32)*2*2*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Dragon.GREEN), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals((2+32+32+32)*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.EAST), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertTrue(hand.isMahjong());
		assertEquals((10+2+32+32+32+32)*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		assertEquals(scheme.LimitScore, hand.getTotalScore());
		
		assertTrue(hand.requiresPairConcealedInfo());
		
		hand.setMahjongPairConcealed(true);
		
		assertEquals((10+2+32+32+32+32)*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		assertEquals(scheme.LimitScore, hand.getTotalScore());
		
		// Not realistic scores, but test that finishing scores work.
		
		hand.setMahjongByLooseTile(true);
		assertEquals((10+2+32+32+32+32)*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByWallTile(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByLastWallTile(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByLastDiscard(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByOnlyPossibleTile(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByRobbingKong(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByOriginalCall(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		assertEquals(scheme.LimitScore, hand.getTotalScore());
	}

	@Test
	public void testFlagSerialisation()
	{
		ScoringScheme	scheme	= ScoringScheme.instance();
		ScoredHand		hand	= new ScoredHand(scheme);


		hand = new ScoredHand(scheme);
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Dragon.WHITE)), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals(2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.NORTH), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals((2+32)*2*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Dragon.RED), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals((2+32+32)*2*2*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Dragon.GREEN), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertFalse(hand.isMahjong());
		assertEquals((2+32+32+32)*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Wind.EAST), Group.Visibility.CONCEALED), scheme, Wind.NORTH, Wind.NORTH));
		assertTrue(hand.isMahjong());
		assertEquals((10+2+32+32+32+32)*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		assertEquals(scheme.LimitScore, hand.getTotalScore());

		hand = checkSerialisation(hand, scheme, Wind.NORTH, Wind.NORTH);

		assertTrue(hand.requiresPairConcealedInfo());

		hand.setMahjongPairConcealed(true);
		hand = checkSerialisation(hand, scheme, Wind.NORTH, Wind.NORTH);

		assertEquals((10+2+32+32+32+32)*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		assertEquals(scheme.LimitScore, hand.getTotalScore());

		// Not realistic scores, but test that finishing scores work.

		hand.setMahjongByLooseTile(true);
		assertEquals((10+2+32+32+32+32)*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByWallTile(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByLastWallTile(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand = checkSerialisation(hand, scheme, Wind.NORTH, Wind.NORTH);
		hand.setMahjongByLastDiscard(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByOnlyPossibleTile(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByRobbingKong(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand.setMahjongByOriginalCall(true);
		assertEquals((10+2+32+32+32+32+2)*2*2*2*2*2*2*2*2*2*2*2*2*2, hand.getTotalScoreUnlimited());
		hand = checkSerialisation(hand, scheme, Wind.NORTH, Wind.NORTH);
	}

	private ScoredHand checkSerialisation(ScoredHand hand, ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		ObjectNode origHandJson = hand.toJson();
		String origHandStr = origHandJson.toString();
		ScoredHand rebuiltHand = ScoredHand.fromJson(origHandJson, scheme, ownWind, prevailingWind);

		String rebuiltHandStr = rebuiltHand.toJson().toString();

		assertEquals(origHandStr, rebuiltHandStr);

		return rebuiltHand;
	}

	private void buildAndCheckHandRandomOrder(List<ScoredGroup> sets)
	{
		// Copy the sets so that we can randomise the order of adding entries to the hand.
		
		List<ScoredGroup> setsCopy = new LinkedList<>(sets);
		
		ScoredHand	hand	= new ScoredHand(ScoringScheme.instance());
		Random		random	= new Random();
		
		int remaining;
		
		System.out.print("Added set in order: ");
		
		int handTileCount = 0;
		
		while ((remaining = setsCopy.size()) > 0)
		{
			int index = random.nextInt(remaining);
			
			ScoredGroup set = setsCopy.remove(index);
			
			if (index < 10)
				System.out.print(' ');
			
			System.out.print(index);
			System.out.print(' ');
			
			handTileCount += set.getType().getHandSize();
			
			try
			{
				hand.add(set);
			}
			catch (InvalidHandException ihe)
			{
				if (hand.isMahjong())
					fail();
				else
					assertFalse(handTileCount < ScoringScheme.instance().MahjongHandSize);
			}
			
			if (hand.isMahjong())
				System.out.print(" Mahjong ");
			else if (setsCopy.size() == sets.size() - 5)
				System.out.print("         ");
			
			// Check that the subset of sets that are in the hand are in the order specified
			// by the input list.
			
			checkHandOrder(hand, sets);
		}
		
		System.out.println();
	}
	
	private void checkHandOrder(ScoredHand hand, List<ScoredGroup> sets)
	{
		int previousSetIndex = -1;
		
		for (Group set : hand)
		{
			int setIndex = sets.indexOf(set);
			
			assertTrue(setIndex >= 0);
			assertTrue(setIndex > previousSetIndex);
			
			//System.out.print('.');
			
			previousSetIndex = setIndex;
		}
	}
}

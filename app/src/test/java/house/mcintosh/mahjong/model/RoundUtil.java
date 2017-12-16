package house.mcintosh.mahjong.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public class RoundUtil
{
	public static Round createRound(Player[] players, Wind prevailingWind, Player eastPlayer, Player mahjongPlayer)
	{
		// Get the players into order for play.
		Player[] orderedPlayers = new Player[4];
		int eastIndex;
		
		for (eastIndex = 0; eastIndex < 4 ; eastIndex++)
		{
			if (eastPlayer.equals(players[eastIndex]))
				break;
		}
		
		for (int playerIndex = 0 ; playerIndex < 4 ; playerIndex++)
		{
			orderedPlayers[playerIndex] = players[(eastIndex + playerIndex) % 4];
		}
		
		// Add four hands to the Round, one of which may be the mahjong hand.
		Wind playerWind = Wind.EAST;
		int handIndex	= 0;
		Round round		= new Round(prevailingWind);
		
		for (Player player : orderedPlayers)
		{
			if (player != null)
			{
				if (player.equals(mahjongPlayer))
				{
					round.addHand(player, createMahjongHand136(playerWind, prevailingWind), playerWind);
				}
				else
				{
					round.addHand(player, createHand(handIndex, playerWind, prevailingWind), playerWind);
					handIndex++;
				}
			}
			
			playerWind = playerWind.next();
		}
		
		return round;
	}
	
	public static ScoredHand createMahjongHand136(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Dragon.RED), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.CHOW, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO), Group.Visibility.CONCEALED), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Suit.BAMBOO, Tile.Number.SEVEN), Group.Visibility.CONCEALED), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.PAIR, new Tile(Tile.Suit.BAMBOO, Tile.Number.THREE)), scheme, playerWind, prevailingWind));
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Dragon.WHITE), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		assertTrue(hand.isMahjong());
		assertEquals((10+4+16+4)*2*2, hand.getTotalScoreUnlimited());
		
		return hand;
	}
	
	/**
	 * Create a hand from the selection available depending on an index.
	 * @param index
	 * @param playerWind
	 * @param prevailingWind
	 * @return
	 */
	private static ScoredHand createHand(int index, Wind playerWind, Wind prevailingWind)
	{
		switch (index % 2)
		{
		case 0:
			return createHand2(playerWind, prevailingWind);
		case 1:
			return createHand4(playerWind, prevailingWind);
		case 2:
			return createHand16(playerWind, prevailingWind);
		default:
			throw new InvalidModelException("Should never get here");
		}
	}
	
	public static ScoredHand createHand2(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.CHARACTERS, Tile.Number.TWO), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		assertEquals(2, hand.getTotalScoreUnlimited());
		
		return hand;
	}
	
	public static ScoredHand createHand4(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.PUNG, new Tile(Tile.Suit.BAMBOO, Tile.Number.THREE), Group.Visibility.CONCEALED), scheme, playerWind, prevailingWind));
		assertEquals(4, hand.getTotalScoreUnlimited());
		
		return hand;
	}

	public static ScoredHand createHand16(Wind playerWind, Wind prevailingWind)
	{
		ScoringScheme scheme = ScoringScheme.instance();
		
		ScoredHand hand = new ScoredHand(scheme);
		
		hand.add(new ScoredGroup(new Group(Group.Type.KONG, new Tile(Tile.Suit.CIRCLES, Tile.Number.NINE), Group.Visibility.EXPOSED), scheme, playerWind, prevailingWind));
		assertEquals(16, hand.getTotalScoreUnlimited());
		
		return hand;
	}

}

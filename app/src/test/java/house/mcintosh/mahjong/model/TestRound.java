package house.mcintosh.mahjong.model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public class TestRound
{
	@Test
	public void testCreatingRound1() throws IOException
	{
		Wind prevailingWind = Wind.EAST;
		
		Round round = new Round(prevailingWind);
		
		Player eastPlayer	= Player.create("East Player");
		Player southPlayer	= Player.create("South Player");
		Player westPlayer	= Player.create("West Player");
		Player northPlayer	= Player.create("North Player");
		
		round.addHand(eastPlayer, RoundUtil.createMahjongHand136(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, RoundUtil.createHand2(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, RoundUtil.createHand4(Wind.WEST, prevailingWind), Wind.WEST);
		round.addHand(northPlayer, RoundUtil.createHand16(Wind.NORTH, prevailingWind), Wind.NORTH);
		
		// Check the score for each player.
		
		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);
		int northScore	= round.getPlayerScore(northPlayer);

		assertEquals(+136*6, eastScore);
		assertEquals(-136*2 +2-4+2-16, southScore);
		assertEquals(-136*2 +4-2+4-16, westScore);
		assertEquals(-136*2 +16-2+16-4, northScore);
		assertEquals(0, eastScore + southScore + westScore + northScore);		
	}
	
	@Test
	public void testCreatingRound2() throws IOException
	{
		Wind prevailingWind = Wind.NORTH;
		
		Round round = new Round(prevailingWind);
		
		Player eastPlayer	= Player.create("East Player");
		Player southPlayer	= Player.create("South Player");
		Player westPlayer	= Player.create("West Player");
		Player northPlayer	= Player.create("North Player");
		
		round.addHand(eastPlayer, RoundUtil.createHand2(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, RoundUtil.createMahjongHand136(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, RoundUtil.createHand4(Wind.WEST, prevailingWind), Wind.WEST);
		round.addHand(northPlayer, RoundUtil.createHand16(Wind.NORTH, prevailingWind), Wind.NORTH);
		
		// Check the score for each player.
		
		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);
		int northScore	= round.getPlayerScore(northPlayer);

		assertEquals(-136*2 +4-8+4-32, eastScore);
		assertEquals(+136*4, southScore);
		assertEquals(-136 +8-4+4-16, westScore);
		assertEquals(-136 +32-4+16-4, northScore);
		assertEquals(0, eastScore + southScore + westScore + northScore);		
	}
	
	@Test
	public void testCreatingRound3() throws IOException
	{
		Wind prevailingWind = Wind.WEST;
		
		Round round = new Round(prevailingWind);
		
		Player eastPlayer	= Player.create("East Player");
		Player southPlayer	= Player.create("South Player");
		Player westPlayer	= Player.create("West Player");
		Player northPlayer	= Player.create("North Player");
		
		round.addHand(eastPlayer, RoundUtil.createHand2(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, RoundUtil.createHand4(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, RoundUtil.createMahjongHand136(Wind.WEST, prevailingWind), Wind.WEST);
		round.addHand(northPlayer, RoundUtil.createHand16(Wind.NORTH, prevailingWind), Wind.NORTH);
		
		// Check the score for each player.
		
		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);
		int northScore	= round.getPlayerScore(northPlayer);

		assertEquals(-136*2 +4-8+4-32, eastScore);
		assertEquals(-136 +8-4+4-16, southScore);
		assertEquals(+136*4, westScore);
		assertEquals(-136 +32-4+16-4, northScore);
		assertEquals(0, eastScore + southScore + westScore + northScore);		
	}

	@Test
	public void testCreatingTwoPlayerRound() throws IOException
	{
		Wind prevailingWind = Wind.WEST;

		Round round = new Round(prevailingWind);

		Player eastPlayer	= Player.create("East Player");

		Player westPlayer	= Player.create("West Player");

		round.addHand(eastPlayer, RoundUtil.createHand2(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(westPlayer, RoundUtil.createMahjongHand136(Wind.WEST, prevailingWind), Wind.WEST);

		// Check the score for each player.

		int eastScore	= round.getPlayerScore(eastPlayer);
		int westScore	= round.getPlayerScore(westPlayer);

		assertEquals(-136*2 +2, eastScore);
		assertEquals(+136*2 -2, westScore);
		assertEquals(0, eastScore + westScore);
	}


	@Test
	public void testCreatingThreePlayerRound() throws IOException
	{
		Wind prevailingWind = Wind.WEST;

		Round round = new Round(prevailingWind);

		Player eastPlayer	= Player.create("East Player");
		Player southPlayer	= Player.create("South Player");
		Player westPlayer	= Player.create("West Player");

		round.addHand(eastPlayer, RoundUtil.createHand2(Wind.EAST, prevailingWind), Wind.EAST);
		round.addHand(southPlayer, RoundUtil.createHand4(Wind.SOUTH, prevailingWind), Wind.SOUTH);
		round.addHand(westPlayer, RoundUtil.createMahjongHand136(Wind.WEST, prevailingWind), Wind.WEST);

		// Check the score for each player.

		int eastScore	= round.getPlayerScore(eastPlayer);
		int southScore	= round.getPlayerScore(southPlayer);
		int westScore	= round.getPlayerScore(westPlayer);

		assertEquals(-136*2 +4-8, eastScore);
		assertEquals(-136*1 +8-4, southScore);
		assertEquals(+136*3, westScore);
		assertEquals(0, eastScore + southScore + westScore);
	}

}

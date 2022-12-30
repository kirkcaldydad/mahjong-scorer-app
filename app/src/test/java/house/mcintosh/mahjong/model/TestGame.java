package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

import house.mcintosh.mahjong.exception.InvalidGameStateException;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.util.TestUtil;

public class TestGame
{

	@Test
	public void gameRun() throws IOException
	{
		Game game = new Game(TestUtil.loadDefaultScoringScheme());
		
		Player[] players = new Player[4];
		
		Player mickey	= Player.create("Mickey");
		Player donald	= Player.create("Donald");
		Player pluto	= Player.create("Pluto");
		Player goofy	= Player.create("Goofy");
		
		players[0] = mickey;
		players[1] = donald;
		players[2] = pluto;
		players[3] = goofy;
		
		game.setPlayer(mickey, 0);
		game.setPlayer(donald, 1);
		game.setPlayer(pluto, 2);
		game.setPlayer(goofy, 3);
		
		game.startGame(mickey);
		
		// First prevailing wind - east moves on every round.
		
		Round round = RoundUtil.createRound(players, Wind.EAST, mickey, pluto);
		
		int scoreM = round.getPlayerScore(mickey);
		int scoreD = round.getPlayerScore(donald);
		int scoreP = round.getPlayerScore(pluto);
		int scoreG = round.getPlayerScore(goofy);
		
		game.addRound(round);
		
		assertEquals(Wind.EAST, game.getPrevailingWind());
		assertEquals(donald, game.getEastPlayer());
		
		scoreM = game.getPlayerScore(mickey);
		scoreD = game.getPlayerScore(donald);
		scoreP = game.getPlayerScore(pluto);
		scoreG = game.getPlayerScore(goofy);
		
		assertEquals(2000-2*136+4-8+4-4, game.getPlayerScore(mickey));
		assertEquals(2000-1*136+8-4+4-2, game.getPlayerScore(donald));
		assertEquals(2000+4*136, game.getPlayerScore(pluto));
		assertEquals(2000-1*136+4-4+2-4, game.getPlayerScore(goofy));
		
		assertEquals(8000, scoreM + scoreD + scoreP + scoreG);
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.EAST, game.getPrevailingWind());
		assertEquals(pluto, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		// Second prevailing wind - east wins sometimes so no moving on.
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(donald, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));

		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(pluto, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(pluto, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.SOUTH, game.getPrevailingWind());
		assertEquals(goofy, game.getEastPlayer());
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.WEST, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		// Third prevailing wind
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), donald));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		assertEquals(Wind.NORTH, game.getPrevailingWind());
		assertEquals(mickey, game.getEastPlayer());
		
		// Fourth prevailing wind
		
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), donald));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), pluto));
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), donald));
		assertFalse(game.isFinished());
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
		assertFalse(game.isFinished());
		game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), mickey));
		
		// When game is finished, play sticks at last round position without advancing.
		assertEquals(Wind.NORTH, game.getPrevailingWind());
		assertEquals(goofy, game.getEastPlayer());
		assertTrue(game.isFinished());
		
		scoreM = game.getPlayerScore(mickey);
		scoreD = game.getPlayerScore(donald);
		scoreP = game.getPlayerScore(pluto);
		scoreG = game.getPlayerScore(goofy);
		assertEquals(8000, scoreM + scoreD + scoreP + scoreG);
		
		
		// Try adding another round to the finished game.
		
		try
		{
			game.addRound(RoundUtil.createRound(players, game.getPrevailingWind(), game.getEastPlayer(), goofy));
			fail();
		}
		catch(InvalidGameStateException e)
		{
			// OK - expected.
		}

		// Test serialisation to JSON and deserialisation of the game.

		ObjectNode	gameJson	= game.toJson();
		String		gameStr		= gameJson.toString();

		System.out.println(gameStr);

		// Reconstitute the game from the JSON.

		Game rebuiltGame = Game.fromJson(gameJson, TestUtil.loadDefaultScoringScheme());

		// Should now have two identical games, which produce identical JSON.

		ObjectNode rebuiltGameJson = rebuiltGame.toJson();
		String rebuiltGameStr = rebuiltGameJson.toString();

		assertEquals(gameStr, rebuiltGameStr);

		// Scores should match.

		for (Player player : players)
		{
			int originalGameScore = game.getPlayerScore(player);
			int rebuiltGameScore = rebuiltGame.getPlayerScore(player);
			assertEquals("score mismatch for player:" + player, originalGameScore, rebuiltGameScore);
		}
	}

	@Test
	public void gameInterruptedRun() throws IOException
	{
		// Play two identical games, one of which is saved and restored a few times.  They should end up identical.

		ScoringScheme scheme = TestUtil.loadDefaultScoringScheme();

		Game gameC = new Game(scheme);
		Game gameI = new Game(scheme);

		Player[] players = new Player[4];

		Player mickey	= Player.create("Mickey");
		Player donald	= Player.create("Donald");
		Player pluto	= Player.create("Pluto");
		Player goofy	= Player.create("Goofy");

		players[0] = mickey;
		players[1] = donald;
		players[2] = pluto;
		players[3] = goofy;

		gameC.setPlayer(mickey, 0);
		gameC.setPlayer(donald, 1);
		gameC.setPlayer(pluto, 2);
		gameC.setPlayer(goofy, 3);

		gameC.startGame(mickey);

		gameI.setPlayer(mickey, 0);
		gameI.setPlayer(donald, 1);
		gameI.setPlayer(pluto, 2);
		gameI.setPlayer(goofy, 3);

		gameI.startGame(mickey);
		// First prevailing wind - east moves on every round.

		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), pluto));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), pluto));
		pauseAndCompare(gameI, scheme, gameC);
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));
		pauseAndCompare(gameI, scheme, gameC);

		// Second prevailing wind - east wins sometimes so no moving on.

		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), pluto));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), pluto));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), goofy));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), goofy));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), pluto));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), pluto));
		gameI = pauseAndCompare(gameI, scheme, gameC);
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));

		// Third prevailing wind

		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), goofy));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), goofy));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), pluto));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), pluto));
		gameI = pauseAndCompare(gameI, scheme, gameC);
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), donald));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), donald));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));

		// Fourth prevailing wind

		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), goofy));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), goofy));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), donald));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), donald));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), pluto));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), pluto));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), donald));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), donald));
		gameI = pauseAndCompare(gameI, scheme, gameC);
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), goofy));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), goofy));
		gameC.addRound(RoundUtil.createRound(players, gameC.getPrevailingWind(), gameC.getEastPlayer(), mickey));
		gameI.addRound(RoundUtil.createRound(players, gameI.getPrevailingWind(), gameI.getEastPlayer(), mickey));


		// Test serialisation to JSON and deserialisation of the game.

		String		gameCStr	= gameC.toJson().toString();
		String		gameIStr	= gameI.toJson().toString();
		assertEquals(gameCStr, gameIStr);
	}

	private Game pauseAndCompare(Game gamePaused, ScoringScheme scheme, Game gameContinuous)
	{
		ObjectNode savedGameJson = gamePaused.toJson();
		String savedGameStr = savedGameJson.toString();
		String referenceGame = gameContinuous.toJson().toString();

		assertEquals(referenceGame, savedGameStr);

		return Game.fromJson(savedGameJson, scheme);
	}
}


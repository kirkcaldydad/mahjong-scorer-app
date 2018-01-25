package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import house.mcintosh.mahjong.exception.InvalidGameStateException;
import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.util.JsonUtil;

public final class Game
{
	private Player[]				m_seats				= new Player[4];;
	private int						m_seatsOccupied		= 0;
	private List<Round>				m_rounds			= new ArrayList<>();
	private Map<Player, Integer>	m_scores			= new HashMap<>();
	private boolean					m_started			= false;
	private boolean					m_finished			= false;
	
	private final ScoringScheme		m_scheme;
	private final GameMeta			m_meta;
	
	private Player		m_startingPlayer;
	private Player		m_endingPlayer;
	private Player		m_eastPlayer;
	private Wind		m_prevailingWind;
	
	public Game(ScoringScheme scheme)
	{
		m_scheme = scheme;
		m_meta = new GameMeta();
	}

	private Game(ScoringScheme scheme, GameMeta meta)
	{
		m_scheme = scheme;
		m_meta = meta;
	}

	public ObjectNode toJson()
	{
		ObjectNode	game	= JsonUtil.createObjectNode();
		ArrayNode	seats	= JsonUtil.createArrayNode();
		ArrayNode	players	= JsonUtil.createArrayNode();
		ObjectNode	scores	= JsonUtil.createObjectNode();

		game.put("version", "1");
		game.set("meta", m_meta.toJson());

		for (Player player : m_seats)
		{
			ObjectNode seat = JsonUtil.createObjectNode();

			if (player != null)
			{
				players.add(player.toJson());
				seat.put("playerId", player.getId());

				if (player.equals(m_startingPlayer))
					seat.put("startingPlayer", true);

				if (player.equals(m_endingPlayer))
					seat.put("endingPlayer", true);

				if (player.equals(m_eastPlayer))
					seat.put("eastPlayer", true);

				scores.put(player.getId(), m_scores.get(player));
			}

			seats.add(seat);
		}

		ArrayNode rounds = JsonUtil.createArrayNode();

		for (Round round : m_rounds)
			rounds.add(round.toJson());

		game.set("players", players);
		game.set("seats", seats);
		game.set("rounds", rounds);
		game.set("scores", scores);
		game.put("started", m_started);
		game.put("finished", m_finished);
		game.put("prevailingWind", m_prevailingWind.name());

		return game;
	}

	static public Game fromJson(JsonNode gameNode, ScoringScheme scheme)
	{
		GameMeta meta = GameMeta.fromJson(gameNode.path("meta"));

		Game game = new Game(scheme, meta);

		// Create all the players so that they are available by ID from the cache inside Players.
		ArrayNode playersNode = (ArrayNode)gameNode.get("players");

		for (JsonNode playerNode : playersNode)
		{
			Player.fromJson(playerNode);
		}

		// Create the seats
		ArrayNode seatsNode = (ArrayNode)gameNode.get("seats");

		for (int i = 0 ; i < seatsNode.size() ; i++)
		{
			JsonNode seatNode = seatsNode.get(i);

			if (!seatNode.has("playerId"))
				continue;

			Player player = Player.get(new PlayerId(seatNode.get("playerId")));
			game.m_seats[i] = player;

			game.m_seatsOccupied++;

			if (seatNode.path("startingPlayer").asBoolean(false))
				game.m_startingPlayer = player;

			if (seatNode.path("endingPlayer").asBoolean(false))
				game.m_endingPlayer = player;

			if (seatNode.path("eastPlayer").asBoolean(false))
				game.m_eastPlayer = player;
		}

		// Initialise the game state indicators.

		game.m_started = gameNode.path("started").asBoolean(false);
		game.m_finished = gameNode.path("finished").asBoolean(false);
		game.m_prevailingWind = Wind.valueOf(gameNode.path("prevailingWind").asText());

		// Load the rounds.

		ArrayNode roundsNode = (ArrayNode)gameNode.get("rounds");

		for (JsonNode roundNode : roundsNode)
		{
			game.m_rounds.add(Round.fromJson(roundNode, scheme));
		}

		// Load the scores.

		ObjectNode scoresNode = (ObjectNode)gameNode.get("scores");

		for (Iterator<String> it = scoresNode.fieldNames(); it.hasNext(); )
		{
			String playerId = it.next();

			Player player = Player.get(new PlayerId(playerId));

			game.m_scores.put(Player.get(new PlayerId(playerId)), scoresNode.get(playerId).asInt());
		}

		return game;
	}
	
	public void setPlayer(Player player, int index)
	{
		if (m_started)
			throw new InvalidGameStateException("Cannot add players after game has started.");
		
		if (index < 0 || index > 3)
			throw new InvalidModelException("Invalid seat index for player.");
		
		if (m_seats[index] != null)
			throw new InvalidModelException("Seat already occupied.");
		
		m_seats[index] = player;
		m_seatsOccupied++;
		
		m_scores.put(player, m_scheme.InitialScore);
	}

	public Player getPlayer(int index)
	{
		if (index < 0 || index >= 4)
			throw new InvalidModelException("Invalid index for game game player: " + index);

		return m_seats[index];
	}

	public List<Player> getPlayers()
	{
		List<Player> players = new ArrayList<>(4);

		for (Player player : m_seats)
		{
			if (player != null)
				players.add(player);
		}

		return players;
	}

	public void rotateSeats()
	{
		Player player3 = m_seats[3];
		m_seats[3] = m_seats[2];
		m_seats[2] = m_seats[1];
		m_seats[1] = m_seats[0];
		m_seats[0] = player3;
	}
	
	public void startGame(Player eastPlayer)
	{
		if (m_started)
			throw new InvalidGameStateException("Game is already started.");
		
		if (m_finished)
			throw new InvalidGameStateException("Game is finished.");
		
		if (m_seatsOccupied < 2)
			throw new InvalidGameStateException("Must have at least two players.");
		
		m_startingPlayer	= eastPlayer;
		m_endingPlayer		= endingPlayer();
		m_eastPlayer		= eastPlayer;
		m_prevailingWind	= Wind.EAST;
		m_started			= true;
	}

	/**
	 * Determine whether the given round has hands set for all players in the game.
	 */
	public boolean isCompleteRound(Round round)
	{
		List<Player> allPlayers = new ArrayList<>();

		for (Player player : m_seats)
			if (player != null)
				allPlayers.add(player);

		return round.hasHandForAll(allPlayers);
	}
	
	public void addRound(Round round)
	{
		if (!m_started)
			throw new InvalidGameStateException("Game is not started.");
		
		if (m_finished)
			throw new InvalidGameStateException("Game is finished.");
		
		m_rounds.add(round);
		
		// Update the score with the new round scores.
		
		for (Player player : m_seats)
		{
			if (player == null)
				continue;
			
			Integer currentScore = m_scores.get(player);
			
			m_scores.put(player, currentScore + round.getPlayerScore(player));
		}
		
		// Move the player and prevailing wind on to the next round.
		
		if (round.getHand(m_eastPlayer).isMahjong())
			// Continue game without moving east player on.
			return;
		
		// Check for the end of the game.
		
		if (m_eastPlayer.equals(m_endingPlayer) && m_prevailingWind == Wind.NORTH)
		{
			m_finished = true;
			return;
		}
		
		//Step east player forward to find the next player.
		int eastPlayerIndex = findPlayerIndex(m_eastPlayer);
		
		do
		{
			eastPlayerIndex++;
			
			if (eastPlayerIndex >= m_seats.length)
				eastPlayerIndex = 0;
		}
		while (m_seats[eastPlayerIndex] == null);
		
		m_eastPlayer = m_seats[eastPlayerIndex];
		
		if (m_eastPlayer == m_startingPlayer)
			m_prevailingWind = m_prevailingWind.next();
	}

	/**
	 * Remove the last added round from the hand and return it.
	 *
	 * @return The last added round or null if there is no round to return.
	 */
	public Round popRound()
	{
		if (m_rounds.size() == 0)
			return null;

		// To get the state of the game back to the way it was before the removed round,
		// set it back to the start of the game, and add all the rounds again except for the
		// last one.  Simplistic but simple and avoids complicated calculations to reverse
		// scores and the position of play.

		List<Round> allRounds = m_rounds;

		m_rounds = new ArrayList<>();
		m_prevailingWind = Wind.EAST;
		m_eastPlayer = m_startingPlayer;
		m_finished = false;

		// Reset the scores.

		Map<Player, Integer> initialScores = new HashMap<>();

		for (Player player : m_scores.keySet())
			initialScores.put(player, m_scheme.InitialScore);

		m_scores = initialScores;

		// Add all the rounds to the game, except the most recent one.

		int lastRoundIndex = allRounds.size() - 1;

		for (int i = 0 ; i < lastRoundIndex ; i++)
		{
			addRound(allRounds.get(i));
		}

		return allRounds.get(lastRoundIndex);
	}

	public int getRoundCount()
	{
		return m_rounds.size();
	}
	
	public Wind getPrevailingWind()
	{
		return m_prevailingWind;
	}
	
	public Player getEastPlayer()
	{
		return m_eastPlayer;
	}
	
	public Wind getPlayerWind(Player player)
	{
		int		index	= findPlayerIndex(m_eastPlayer);
		Wind	wind	= Wind.EAST;
		
		while (true)
		{
			if (player.equals(m_seats[index]))
			{
				return wind;
			}
			
			index	= (index+1) % m_seats.length;
			wind	= wind.next();
		}
	}
	
	public int getPlayerScore(Player player)
	{
		Integer score = m_scores.get(player);

		if (score == null)
			System.out.println("got null score for " + player.toJson());

		return score;
	}

	public ScoringScheme getScoringScheme()
	{
		return m_scheme;
	}
	
	public boolean isStarted()
	{
		return m_started;
	}
	
	public boolean isFinished()
	{
		return m_finished;
	}

	/**
	 * @return The last player in the sequence around the table.
	 */
	private Player endingPlayer()
	{
		int		playerIndex;
		
		playerIndex = findPlayerIndex(m_startingPlayer);
		
		// Found the player that is east, so step backwards to find the previous player.
		
		do
		{
			if (playerIndex <= 0)
				playerIndex = m_seats.length;
			
			playerIndex--;
		}
		while (m_seats[playerIndex] == null);
		
		return m_seats[playerIndex];
	}

	private int findPlayerIndex(Player player)
	{
		for (int playerIndex = 0; playerIndex < m_seats.length ; playerIndex++)
			if (player.equals(m_seats[playerIndex]))
				return playerIndex;
		
		throw new InvalidModelException("Player not found");
	}

	public GameMeta getMeta()
	{
		return m_meta;
	}
}

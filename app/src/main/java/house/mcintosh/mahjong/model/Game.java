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

public class Game
{
	private Player[]				m_seats				= new Player[4];;
	private int						m_seatsOccupied		= 0;
	private List<Round>				m_rounds			= new ArrayList<>();
	private Map<Player, Integer>	m_scores			= new HashMap<>();
	private boolean					m_started			= false;
	private boolean					m_finished			= false;
	
	private final ScoringScheme		m_scheme;
	
	private Player		m_startingPlayer;
	private Player		m_endingPlayer;
	private Player		m_eastPlayer;
	private Wind		m_prevailingWind;
	
	public Game(ScoringScheme scheme)
	{
		m_scheme = scheme;
	}

	public ObjectNode toJson()
	{
		ObjectNode	game	= JsonUtil.createObjectNode();
		ArrayNode	seats	= JsonUtil.createArrayNode();
		ArrayNode	players	= JsonUtil.createArrayNode();
		ObjectNode	scores	= JsonUtil.createObjectNode();

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
		// Create all the players so that they are available by ID from the cache inside Players.
		ArrayNode playersNode = (ArrayNode)gameNode.get("players");

		for (JsonNode playerNode : playersNode)
		{
			Player.fromJson(playerNode);
		}

		Game game = new Game(scheme);

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
}

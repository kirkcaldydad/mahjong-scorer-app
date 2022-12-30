package house.mcintosh.mahjong.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Round;
import house.mcintosh.mahjong.model.RoundInfo;
import house.mcintosh.mahjong.model.RoundRoundInfo;
import house.mcintosh.mahjong.model.StartingScoreInfo;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public final class GameInfoActivity extends AppCompatActivity
{
	private static final String LOG_TAG = GameInfoActivity.class.getName();

	// Identifiers for extra information included in intents targeted at this activity.

	// This when info for a game is to be displayed.
	public static final String EXTRA_KEY_GAME_FILE = GameInfoActivity.class.getName() + ":gameFile";

	private Game m_game;
	private TextView m_prevailingWindView;

	private Map<Wind, CharSequence> m_windNames = new HashMap<>();

	private Round m_round;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_info);

		// Load the game and display it.

		Intent launchIntent = getIntent();

		File gamefile = (File) launchIntent.getSerializableExtra(EXTRA_KEY_GAME_FILE);
		GameFile gameFile = GameFile.load(this, gamefile);
		m_game = gameFile.getGame();

		ScoringScheme scheme = m_game.getScoringScheme();
		((TextView) findViewById(R.id.txtScoringSchemeName)).setText(scheme.getDisplayName());

		String prevailingWindName = m_game.getPrevailingWind().getName(this);
		((TextView) findViewById(R.id.txtPrevailingWindName)).setText(prevailingWindName);

		// Create a list of RoundInfo instances to be displayed.  We can also get the
		// scores for all rounds from the game, for display and calculating the increments
		// for each player.

		Game scoreCalcGame = new Game(scheme);
		Player[] seats = m_game.getSeats();

		for (int i = 0; i < seats.length; i++)
		{
			scoreCalcGame.setPlayer(seats[i], i);
		}

		ArrayList<RoundInfo> roundInfos = new ArrayList<>(m_game.getRoundCount());
		List<Round> rounds = m_game.getRounds();
		List<Map<Player, Integer>> roundScores = m_game.getRoundScores();
		Map<Player, Integer> previousScores = m_game.getIntialScores();

		for (int i = 0 ; i < rounds.size() ; i++)
		{
			Round round = rounds.get(i);
			Map<Player, Integer> playerScores = roundScores.get(i);
			Map<Player, Integer> scoreIncrements = new HashMap<>();

			for (Map.Entry<Player, Integer> playerEntry : playerScores.entrySet())
			{
				Player player = playerEntry.getKey();
				Integer roundScore = playerEntry.getValue();

				scoreIncrements.put(player, roundScore - previousScores.get(player));
			}

			roundInfos.add(0, new RoundRoundInfo(round, m_game.getSeats(), playerScores, scoreIncrements));

			previousScores = playerScores;
		}

		// Add a sort of fake roundInfo that represents the initial state of the game.

		Map<Player, Integer> initialScores = m_game.getIntialScores();
		Map<Player, Wind> playerWinds = new HashMap<>();

		for (Player player : initialScores.keySet())
		{
			playerWinds.put(player, m_game.getPlayerWind(player));
		}

		StartingScoreInfo startScoreInfo = new StartingScoreInfo(m_game.getSeats(), initialScores, playerWinds);

		roundInfos.add(startScoreInfo);

		RoundInfosAdapter adapter = new RoundInfosAdapter(this, roundInfos, new TileDrawables(this));

		ListView roundsList = findViewById(R.id.roundsList);

		roundsList.setAdapter(adapter);

		((TextView) findViewById(R.id.txtRoundCount)).setText(Integer.toString(rounds.size()));
	}
}

package house.mcintosh.mahjong.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import house.mcintosh.mahjong.exception.InvalidModelException;
import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Round;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.util.DisplayUtil;

public final class GamePlayActivity extends AppCompatActivity
{
	private static final String LOG_TAG = GamePlayActivity.class.getName();

	// Identifiers for extra information included in intents targeted at this activity.

	// This when a game play is started.
	public static final String EXTRA_KEY_GAME_FILE = GamePlayActivity.class.getName() + ":gameFile";

	// These when an entered hand is being returned from a called activity.
	public static final String EXTRA_KEY_ENTERED_HAND = GamePlayActivity.class.getName() + ":hand";
	public static final String EXTRA_KEY_PLAYER = GamePlayActivity.class.getName() + ":player";

	public static final int ENTER_HAND_REQUEST_CODE = 1;

	private Game m_game;
	private GameFile m_gameFile;
	private TextView m_prevailingWindView;

	private PlayerViews[] m_playerViews = new PlayerViews[4];
	private Map<Wind, CharSequence> m_windNames = new HashMap<>();

	private Round m_round;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_play);

		// Load the game and display it.

		Intent launchIntent = getIntent();

		File gamefile = (File)launchIntent.getSerializableExtra(EXTRA_KEY_GAME_FILE);
		m_gameFile = GameFile.load(gamefile);
		m_game = m_gameFile.getGame();

		// Remember the various views we need to access to avoid repeated lookups and to
		// enable them to be referenced as a group.

		m_playerViews[0] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind0),
						findViewById(R.id.tvPlayerName0),
						findViewById(R.id.tvPlayerScore0),
						findViewById(R.id.tvPlayerRoundScore0)
				);
		m_playerViews[1] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind1),
						findViewById(R.id.tvPlayerName1),
						findViewById(R.id.tvPlayerScore1),
						findViewById(R.id.tvPlayerRoundScore1)
				);
		m_playerViews[2] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind2),
						findViewById(R.id.tvPlayerName2),
						findViewById(R.id.tvPlayerScore2),
						findViewById(R.id.tvPlayerRoundScore2)
				);
		m_playerViews[3] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind3),
						findViewById(R.id.tvPlayerName3),
						findViewById(R.id.tvPlayerScore3),
						findViewById(R.id.tvPlayerRoundScore3)
				);

		m_windNames.put(Wind.EAST, getText(R.string.east));
		m_windNames.put(Wind.SOUTH, getText(R.string.south));
		m_windNames.put(Wind.WEST, getText(R.string.west));
		m_windNames.put(Wind.NORTH, getText(R.string.north));

		m_prevailingWindView = findViewById(R.id.tvPrevailingWind);

		// Attach listeners for clicks that enter player hands for scoring.
		for (int i = 0 ; i < 4 ; i++)
		{
			Player player = m_game.getPlayer(i);
			if (player != null)
				m_playerViews[i].playerName.setOnClickListener(new EnterHandClickListener(player));
		}

		// Create a new round, with the currently prevailing wind.  This is currently empty,
		// waiting for hands to be added to it.

		m_round = new Round(m_game.getPrevailingWind());

		displayGame();
	}

	/*
	 * Invoked when the EnterHandActivity is finished, and needs to return the entered hand to the game.
	 */
	@Override
	public void onNewIntent(Intent intent)
	{
		Log.d(LOG_TAG, "GamePlayActivity.inNewIntent() intent:" + intent);

		ScoredHand hand = ((ScoredHandWrapper)intent.getSerializableExtra(EXTRA_KEY_ENTERED_HAND)).getHand();
		Player player = (Player)intent.getSerializableExtra(EXTRA_KEY_PLAYER);

		// Update the round with the player's hand.

		try
		{
			m_round.addHand(player, hand, m_game.getPlayerWind(player));
		}
		catch (InvalidModelException ime)
		{
			Log.e(LOG_TAG, "InvalidModelException: "  + ime);
		}

		// Check whether we now have hands for all players.
		if (m_game.isCompleteRound(m_round))
		{
			m_game.addRound(m_round);
			m_round = new Round(m_game.getPrevailingWind());

			m_gameFile.save();
		}

		// Scores have changed and the game may now have moved on...
		displayGame();
	}

	private void displayGame()
	{
		displayPlayer(m_game.getPlayer(0), m_playerViews[0]);
		displayPlayer(m_game.getPlayer(1), m_playerViews[1]);
		displayPlayer(m_game.getPlayer(2), m_playerViews[2]);
		displayPlayer(m_game.getPlayer(3), m_playerViews[3]);

		m_prevailingWindView.setText(m_windNames.get(m_game.getPrevailingWind()));
	}

	private void displayPlayer(Player player, PlayerViews views)
	{
		if (player == null)
		{
			views.wind.setText("");
			views.playerName.setText("");
			views.score.setText("");
		}
		else
		{
			views.wind.setText(m_windNames.get(m_game.getPlayerWind(player)));
			views.playerName.setText(player.getName());
			views.score.setText(String.format(Locale.UK, "%d", m_game.getPlayerScore(player)));
		}

		if (m_round.hasHandFor(player))
		{
			ScoredHand playerHand = m_round.getHand(player);

			views.playerName.setTextAppearance(R.style.complete);
			views.roundScore.setText(DisplayUtil.getTotalScoreWithStatus(this, playerHand));
			views.roundScore.setVisibility(View.VISIBLE);
		}
		else
		{
			views.playerName.setTextAppearance(R.style.available);
			views.roundScore.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * A simple object in which to cache the view elements associated with each player.
	 */
	private class PlayerViews
	{
		final TextView wind;
		final TextView playerName;
		final TextView score;
		final TextView roundScore;

		PlayerViews(View windView, View playerName, View score, View roundScore)
		{
			this.wind = (TextView)windView;
			this.playerName = (TextView)playerName;
			this.score = (TextView)score;
			this.roundScore = (TextView)roundScore;
		}
	}

	/**
	 * Listener for a click on a name (or similar) that triggers entering or editing a hand.
	 */
	private class EnterHandClickListener implements View.OnClickListener
	{
		private final Player m_player;

		EnterHandClickListener(Player player)
		{
			m_player = player;
		}

		@Override
		public void onClick(View view)
		{
			Intent intent = new Intent(view.getContext(), EnterHandActivity.class);

			intent.putExtra(EnterHandActivity.PLAYER_KEY, m_player);
			intent.putExtra(EnterHandActivity.OWN_WIND_KEY, m_game.getPlayerWind(m_player));
			intent.putExtra(EnterHandActivity.PREVAILING_WIND_KEY, m_game.getPrevailingWind());

			ScoredHand hand = m_round.hasHandFor(m_player) ? m_round.getHand(m_player) : new ScoredHand(m_game.getScoringScheme());

			intent.putExtra(EnterHandActivity.HAND_KEY, new ScoredHandWrapper(hand));

			startActivityForResult(intent, ENTER_HAND_REQUEST_CODE);
		}
	}
}

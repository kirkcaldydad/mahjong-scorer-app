package house.mcintosh.mahjong.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Round;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoredHand;
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
				m_playerViews[i].playerName.setOnClickListener(new EnterHandClickListener(player, this));
		}

		// Create a new round, with the currently prevailing wind.  This is currently empty,
		// waiting for hands to be added to it.

		m_round = new Round(m_game.getPrevailingWind());

		displayGame(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// Menu item click is handled in onOptionsItemSelected().
		getMenuInflater().inflate(R.menu.menu_game_play, menu);

		return true;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		if (menu != null)
		{
			MenuItem item = menu.findItem(R.id.action_edit_previous_round);

			item.setEnabled(m_game.getRoundCount() > 0);
		}

		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * Invoked when an item on the bar at the top is selected, including the back arrow button at the top.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				return confirmLoseEnteredRoundForNavigateUp();

			case R.id.action_edit_previous_round:
				editPreviousRound();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Handler for the back button to make sure that the hand entered so far is returned to
	 * the parent activity.
	 */
	@Override
	public void onBackPressed()
	{
		confirmLoseEnteredRoundForNavigateUp();
	}

	public boolean confirmLoseEnteredRoundForNavigateUp()
	{
		final GamePlayActivity self = this;

		DialogInterface.OnClickListener navUpListener =
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// User clicked Continue without saving button.  Navigate to parent.

						Intent returnHandIntent = NavUtils.getParentActivityIntent(self);

						NavUtils.navigateUpTo(self, returnHandIntent);
					}
				};

		return confirmLoseEnteredRound(navUpListener);
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

		m_round.addHand(player, hand, m_game.getPlayerWind(player));

		// Add to the game if it is now a complete and valid round.

		List<Player> mahjongPlayers = m_round.getMahjongPlayers();

		if (mahjongPlayers.size() == 1 && !m_game.isFinished())
		{
			if (m_game.isCompleteRound(m_round))
			{
				m_game.addRound(m_round);
				m_round = new Round(m_game.getPrevailingWind());

				m_gameFile.save();

				String eastPlayerName = m_game.getEastPlayer().getName();

				Toast toast = Toast.makeText(this, getResources().getString(R.string.notificationEastPlayer, eastPlayerName), Toast.LENGTH_LONG);
				toast.show();
			}
		}

		if (m_game.isFinished())
		{
			displayGameFinished();
		}

		// Scores have changed and the game may now have moved on...

		boolean showMultiMahjongError = mahjongPlayers.size() > 1;
		displayGame(showMultiMahjongError);

		if (showMultiMahjongError)
		{
			Toast toast = Toast.makeText(this, getText(R.string.multiMahjongErrorMessage), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private void displayGame(boolean showMultiMahjongError)
	{
		displayPlayer(m_game.getPlayer(0), m_playerViews[0], showMultiMahjongError);
		displayPlayer(m_game.getPlayer(1), m_playerViews[1], showMultiMahjongError);
		displayPlayer(m_game.getPlayer(2), m_playerViews[2], showMultiMahjongError);
		displayPlayer(m_game.getPlayer(3), m_playerViews[3], showMultiMahjongError);

		m_prevailingWindView.setText(m_windNames.get(m_game.getPrevailingWind()));
	}

	private void displayPlayer(Player player, PlayerViews views, boolean showMultiMahjongError)
	{
		if (player == null)
		{
			views.wind.setText("");
			views.playerName.setText("");
			views.score.setText("");

			views.wind.setVisibility(View.INVISIBLE);
			views.playerName.setVisibility(View.INVISIBLE);
			views.score.setVisibility(View.INVISIBLE);
		}
		else
		{
			views.wind.setText(m_windNames.get(m_game.getPlayerWind(player)));
			views.playerName.setText(player.getName());
			views.score.setText(String.format(Locale.UK, "%d", m_game.getPlayerScore(player)));

			views.wind.setVisibility(View.VISIBLE);
			views.playerName.setVisibility(View.VISIBLE);
			views.score.setVisibility(View.VISIBLE);
		}

		if (m_round.hasHandFor(player))
		{
			ScoredHand playerHand = m_round.getHand(player);

			int styleResource =
					(showMultiMahjongError && playerHand.isMahjong()) ?
							R.style.error :
							R.style.complete;

			views.playerName.setTextAppearance(styleResource);
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
	 * If there are entries in the round that have not yet been saved in the game, prompt the
	 * user to confirm that the changes will be lost.
	 *
	 * @return	false if there are no changes to be saved.  false if there are changes, in which case
	 * 			a dialog is displayed to confirm whether navigation should proceed.
	 */
	private boolean confirmLoseEnteredRound(DialogInterface.OnClickListener continueListener)
	{
		if (m_round.isEmpty())
			// carry on with normal navigation.
			return false;

		// Prompt the user about losing entered hands.
		int handCount = m_round.getHandCount();
		Resources resources = getResources();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder
				.setTitle(resources.getQuantityString(R.plurals.titleConfirmLoseHand, handCount, handCount))
				.setMessage(resources.getQuantityString(R.plurals.questionConfirmLoseHand, handCount, handCount))
				.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// User clicked Cancel button.  Stay on this page.  Nothing to do.
					}
				})
				.setNegativeButton(R.string.dontSave, continueListener);

		AlertDialog dialog = builder.create();

		dialog.show();

		// consume the navigation event so no navigation occurs.
		return true;
	}

	private void editPreviousRound()
	{
		if (!m_round.isEmpty())
		{
			// Prompt about losing entered information.

			final GamePlayActivity self = this;

			DialogInterface.OnClickListener editRoundListener =
					new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							// User clicked 'Continue without saving' button.  Discard the entered
							// round and replace it with an empty round, then try again to edit
							// the previous round, which will now not come down this prompt route.
							m_round = new Round(m_round.getPrevailingWind());

							editPreviousRound();
						}
					};

			confirmLoseEnteredRound(editRoundListener);

			return;
		}

		Round roundToEdit = m_game.popRound();

		if (roundToEdit == null)
			// No previous round to edit.  Should never get here.
			return;

		m_round = roundToEdit;

		displayGame(false);
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
		private final Context m_context;

		EnterHandClickListener(Player player, Context context)
		{
			m_player = player;
			m_context = context;
		}

		@Override
		public void onClick(View view)
		{
			if (m_game.isFinished())
			{
				Toast toast = Toast.makeText(m_context, R.string.notificationGameFinished, Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			Intent intent = new Intent(view.getContext(), EnterHandActivity.class);

			intent.putExtra(EnterHandActivity.PLAYER_KEY, m_player);
			intent.putExtra(EnterHandActivity.OWN_WIND_KEY, m_game.getPlayerWind(m_player));
			intent.putExtra(EnterHandActivity.PREVAILING_WIND_KEY, m_game.getPrevailingWind());

			ScoredHand hand = m_round.hasHandFor(m_player) ? m_round.getHand(m_player) : new ScoredHand(m_game.getScoringScheme(), false);

			intent.putExtra(EnterHandActivity.HAND_KEY, new ScoredHandWrapper(hand));

			startActivityForResult(intent, ENTER_HAND_REQUEST_CODE);
		}
	}

	/**
	 * Show a dialog to tell that the game has finished.
	 */
	private void displayGameFinished()
	{
		if (!m_game.isFinished())
			return;

		// Look for the winning player.  Just a small chance that there is a draw!

		int highestScore = Integer.MIN_VALUE;
		List<Player> highestScoringPlayers = new ArrayList<>(1);

		for (Player player : m_game.getPlayers())
		{
			int playerScore = m_game.getPlayerScore(player);

			if (playerScore < highestScore)
				continue;

			if (playerScore > highestScore)
				highestScoringPlayers.clear();

			highestScore = playerScore;

			highestScoringPlayers.add(player);
		}

		int winningPlayerCount = highestScoringPlayers.size();

		StringBuilder winningPlayerNames = new StringBuilder();

		for (Player player : highestScoringPlayers)
		{
			if (winningPlayerNames.length() > 0)
				winningPlayerNames.append(", ");

			winningPlayerNames.append(player.getName());
		}

		Resources resources = getResources();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder
				.setTitle(resources.getQuantityString(R.plurals.titleGameFinished, winningPlayerCount, winningPlayerNames, highestScore))
				.setMessage(resources.getQuantityString(R.plurals.messageGameFinished, winningPlayerCount, winningPlayerNames, highestScore));

		AlertDialog dialog = builder.create();

		dialog.show();
	}
}

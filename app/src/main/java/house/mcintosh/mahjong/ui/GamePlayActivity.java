package house.mcintosh.mahjong.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Wind;

public class GamePlayActivity extends AppCompatActivity
{
	public static final String EXTRA_KEY_GAME_FILE = GamePlayActivity.class.getName() + ":gameFile";

	private Game m_game;
	private GameFile m_gameFile;
	private TextView m_prevailingWindView;

	private PlayerViews[] m_playerViews = new PlayerViews[4];
	private Map<Wind, CharSequence> m_windNames = new HashMap<>();

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
						findViewById(R.id.tvPlayerScore0)
				);
		m_playerViews[1] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind1),
						findViewById(R.id.tvPlayerName1),
						findViewById(R.id.tvPlayerScore1)
				);
		m_playerViews[2] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind2),
						findViewById(R.id.tvPlayerName2),
						findViewById(R.id.tvPlayerScore2)
				);
		m_playerViews[3] =
				new PlayerViews(
						findViewById(R.id.tvPlayerWind3),
						findViewById(R.id.tvPlayerName3),
						findViewById(R.id.tvPlayerScore3)
				);

		m_windNames.put(Wind.EAST, getText(R.string.east));
		m_windNames.put(Wind.SOUTH, getText(R.string.south));
		m_windNames.put(Wind.WEST, getText(R.string.west));
		m_windNames.put(Wind.NORTH, getText(R.string.north));

		m_prevailingWindView = (TextView) findViewById(R.id.tvPrevailingWind);

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
	{;
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
			views.score.setText("" + m_game.getPlayerScore(player));
		}
	}

	private class PlayerViews
	{
		final TextView wind;
		final TextView playerName;
		final TextView score;

		public PlayerViews(View windView, View playerName, View score)
		{
			this.wind = (TextView)windView;
			this.playerName = (TextView)playerName;
			this.score = (TextView)score;
		}
	}
}

package house.mcintosh.mahjong.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import house.mcintosh.mahjong.model.RoundInfo;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.util.DisplayUtil;

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

		File gamefile = (File)launchIntent.getSerializableExtra(EXTRA_KEY_GAME_FILE);
		GameFile gameFile = GameFile.load(this, gamefile);
		m_game = gameFile.getGame();

		ScoringScheme scheme = m_game.getScoringScheme();
		((TextView)findViewById(R.id.txtScoringSchemeName)).setText(scheme.getDisplayName());

		String prevailingWindName = m_game.getPrevailingWind().getName(this);
		((TextView)findViewById(R.id.txtPrevailingWindName)).setText(prevailingWindName);

		// Create a list of RoundInfo instances to be displayed.

		ArrayList<RoundInfo> roundInfos = new ArrayList<>(m_game.getRoundCount());

		for (Round round : m_game.getRounds())
		{
			roundInfos.add(new RoundInfo(round, m_game.getSeats()));
		}

		RoundInfosAdapter adapter = new RoundInfosAdapter(this, roundInfos, new TileDrawables(this));

		ListView roundsList = findViewById(R.id.roundsList);

		roundsList.setAdapter(adapter);
	}
}

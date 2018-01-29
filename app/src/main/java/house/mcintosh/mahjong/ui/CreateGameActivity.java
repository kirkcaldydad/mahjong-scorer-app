package house.mcintosh.mahjong.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoringScheme;

public final class CreateGameActivity extends AppCompatActivity
{
	private final static String LOG_TAG = CreateGameActivity.class.getName();

	public final static String GAME_FILE_KEY = CreateGameActivity.class.getName() + ".GAME_FILE";

	private String[] m_names = new String[]{"", "", "", ""};

	/**
	 * The wind for each play position.  Stays null until the first time that winds are rotated,
	 * making sure that user has thought about wind position, since it cannot be changed manually
	 * once the game has been created.
	 */
	private Wind[] m_winds = null;
	private TextView[] m_windTextViews = new TextView[4];
	private View[] m_eastSymbols = new View[4];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editcreate_players);

		// Create listeners for the names being typed in.

		((EditText) findViewById(R.id.playerName0)).addTextChangedListener(new NameWatcher(0));
		((EditText) findViewById(R.id.playerName1)).addTextChangedListener(new NameWatcher(1));
		((EditText) findViewById(R.id.playerName2)).addTextChangedListener(new NameWatcher(2));
		((EditText) findViewById(R.id.playerName3)).addTextChangedListener(new NameWatcher(3));

		m_windTextViews[0] = (TextView) findViewById(R.id.playerWindText0);
		m_windTextViews[1] = (TextView) findViewById(R.id.playerWindText1);
		m_windTextViews[2] = (TextView) findViewById(R.id.playerWindText2);
		m_windTextViews[3] = (TextView) findViewById(R.id.playerWindText3);

		m_eastSymbols[0] = findViewById(R.id.imgEastWind0);
		m_eastSymbols[1] = findViewById(R.id.imgEastWind1);
		m_eastSymbols[2] = findViewById(R.id.imgEastWind2);
		m_eastSymbols[3] = findViewById(R.id.imgEastWind3);

		findViewById(R.id.instructionScrollArea).setVisibility(View.VISIBLE);

		displayWinds();
		setButtonState();
	}

	public void onRotateWindClick(View view)
	{
		if (m_winds == null)
			m_winds = new Wind[]{Wind.EAST, Wind.SOUTH, Wind.WEST, Wind.NORTH};
		else
		{
			Wind last = m_winds[3];
			m_winds[3] = m_winds[2];
			m_winds[2] = m_winds[1];
			m_winds[1] = m_winds[0];
			m_winds[0] = last;
		}

		displayWinds();
		setButtonState();
	}

	private void displayWinds()
	{
		// Update the fields to display the new wind values, setting to blank if
		// no winds have been set yet.

		for (int i = 0; i < 4; i++)
		{
			TextView windView = m_windTextViews[i];
			View eastSymbolView = m_eastSymbols[i];
			String windName;
			Wind wind = null;

			if (m_winds == null)
			{
				windName = "";
			}
			else
			{
				wind = m_winds[i];
				windName = wind.getName(this);
			}

			windView.setText(windName);

			if (wind == Wind.EAST)
			{
				windView.setTypeface(windView.getTypeface(), Typeface.BOLD);
				eastSymbolView.setVisibility(View.VISIBLE);
			}
			else
			{
				windView.setTypeface(Typeface.create(windView.getTypeface(), Typeface.NORMAL));
				eastSymbolView.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void onStartGameClick(View view)
	{
		// Create a new game instance and save it.

		// TODO: make scoring scheme selectable.
		Game game = new Game(ScoringScheme.instance());

		Player eastPlayer = null;

		for (int i = 0; i < 4; i++)
		{
			String name = m_names[i];

			if (name == null || name.isEmpty())
				continue;

			Player player = Player.create(name);

			game.setPlayer(player, i);

			if (m_winds[i] == Wind.EAST)
				eastPlayer = player;
		}

		game.startGame(eastPlayer);

		// Save the game to a file.

		GameFile gameFile = new GameFile(this, game);

		gameFile.save();

		// Send the name of the created game file to the calling activity.

		Intent result = new Intent();
		result.putExtra(GAME_FILE_KEY, gameFile.getFile());
		setResult(Activity.RESULT_OK, result);
		finish();
	}

	/**
	 * Determine whether buttons should be enabled or disabled and set them accordingly.
	 */
	private void setButtonState()
	{
		// Only enable the Save button if there are at least two players defined, and winds
		// are set such that East is on a configured player (i.e. East position does not
		// have a blank name).

		int playerCount = 0;
		boolean gotEastPlayer = false;

		for (int i = 0; i < m_names.length; i++)
		{
			String name = m_names[i];

			if (name != null && !name.isEmpty())
			{
				playerCount++;

				if (m_winds != null && m_winds[i] == Wind.EAST)
					gotEastPlayer = true;
			}
		}

		((Button) findViewById(R.id.startButton)).setEnabled(playerCount >= 2 && gotEastPlayer);
	}

	/**
	 * Responds to changes in the text in a player name field.  One instance is
	 * associated with each field.  As the text is changed, it is copied into
	 * m_names and the Start button is enabled/disabled depending on how many
	 * players are defined.
	 */
	private class NameWatcher implements TextWatcher
	{
		final private int m_nameIndex;

		public NameWatcher(int nameIndex)
		{
			m_nameIndex = nameIndex;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			m_names[m_nameIndex] = s.toString().trim();
			setButtonState();


		}

		@Override
		public void afterTextChanged(Editable nameField)
		{
		}
	}
}

package house.mcintosh.mahjong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import house.mcintosh.mahjong.model.Player;

public class EditPlayersActivity extends AppCompatActivity
{
	private final static String LOG_TAG = EditPlayersActivity.class.getName();

	public final static String PLAYERS_KEY = CreateGameActivity.class.getName() + ".PLAYERS";
	public final static String PLAYERS_NAMES_KEY = CreateGameActivity.class.getName() + ".PLAYER_NAMES";

	public final static String RETURN_PLAYER_NAMES_ACTION = "EditPlayersActivity.RETURN_PLAYER_NAMES";

	private Player[] m_players;

	EditText m_playerName0;
	EditText m_playerName1;
	EditText m_playerName2;
	EditText m_playerName3;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editcreate_players);

		Intent intent = getIntent();
		m_players = (Player[]) intent.getSerializableExtra(PLAYERS_KEY);

		m_playerName0 = findViewById(R.id.playerName0);
		m_playerName1 = findViewById(R.id.playerName1);
		m_playerName2 = findViewById(R.id.playerName2);
		m_playerName3 = findViewById(R.id.playerName3);

		// Set the name text on in the input fields.

		m_playerName0.setText(m_players[0] == null ? "" : m_players[0].getName());
		m_playerName1.setText(m_players[1] == null ? "" : m_players[1].getName());
		m_playerName2.setText(m_players[2] == null ? "" : m_players[2].getName());
		m_playerName3.setText(m_players[3] == null ? "" : m_players[3].getName());

		// Disable any input field for which there is no player.

		m_playerName0.setVisibility(m_players[0] == null ? View.INVISIBLE : View.VISIBLE);
		m_playerName1.setVisibility(m_players[1] == null ? View.INVISIBLE : View.VISIBLE);
		m_playerName2.setVisibility(m_players[2] == null ? View.INVISIBLE : View.VISIBLE);
		m_playerName3.setVisibility(m_players[3] == null ? View.INVISIBLE : View.VISIBLE);

		// Don't show the wind fields or buttons at all.

		findViewById(R.id.playerWindText0).setVisibility(View.GONE);
		findViewById(R.id.playerWindText1).setVisibility(View.GONE);
		findViewById(R.id.playerWindText2).setVisibility(View.GONE);
		findViewById(R.id.playerWindText3).setVisibility(View.GONE);
		findViewById(R.id.windSelectButton).setVisibility(View.GONE);
		findViewById(R.id.startButton).setVisibility(View.GONE);
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
				returnEnteredNames();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		returnEnteredNames();
	}

	/**
	 * Finishes this activity, returning the entered hand to the calling activity.
	 */
	private void returnEnteredNames()
	{
		// Check that all the required names are set.

		String name0 = m_playerName0.getText().toString().trim();
		String name1 = m_playerName1.getText().toString().trim();
		String name2 = m_playerName2.getText().toString().trim();
		String name3 = m_playerName3.getText().toString().trim();

		if (
				   (m_players[0] != null && name0.isEmpty())
				|| (m_players[1] != null && name1.isEmpty())
				|| (m_players[2] != null && name2.isEmpty())
				|| (m_players[3] != null && name3.isEmpty())
				)
		{
			Toast toast = Toast.makeText(this, getResources().getString(R.string.notificationEmptyPlayerName), Toast.LENGTH_SHORT);
			toast.show();

			return;
		}

		Intent returnNamesIntent = NavUtils.getParentActivityIntent(this);

		returnNamesIntent.setAction(RETURN_PLAYER_NAMES_ACTION);

		String[] updatedNames = new String[] {name0, name1, name2, name3};

		returnNamesIntent.putExtra(PLAYERS_NAMES_KEY, updatedNames);

		NavUtils.navigateUpTo(this, returnNamesIntent);
	}
}

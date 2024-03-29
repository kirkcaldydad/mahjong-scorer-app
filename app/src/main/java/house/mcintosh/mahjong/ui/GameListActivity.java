package house.mcintosh.mahjong.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.List;

import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.GameSummary;

public final class GameListActivity extends AppCompatActivity
{
	private static final String LOG_TAG = GameListActivity.class.getName();
	private static final int CREATE_GAME_REQUEST_CODE = 1;

	private GameSummariesAdapter m_summariesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final GameListActivity self = this;

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(self, CreateGameActivity.class);
				startActivityForResult(intent, CREATE_GAME_REQUEST_CODE);
			}
		});

		final List<GameSummary> games = getGames();

		m_summariesAdapter = new GameSummariesAdapter(this, games);

		ListView messageListView = findViewById(R.id.gameListView);

		messageListView.setAdapter(m_summariesAdapter);

		// Create a handler for clicks on the games on the list.

		final Context context = this;

		messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				GameSummary selectedGame = games.get(position);

				playGame(selectedGame.getFile());
			}
		});
	}

	private List<GameSummary> getGames()
	{

		List<GameSummary> games = GameFile.getAllGames(this);

		GameSummary.sort(games);

		return games;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar.
		getMenuInflater().inflate(R.menu.menu_game_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			showAboutDialog();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Start the game play activity to play the game.
	 * @param file	The file containing the current state of the game.
	 */
	private void playGame(File file)
	{
		Intent playGameIntent = new Intent(this, GamePlayActivity.class);
		playGameIntent.putExtra(GamePlayActivity.EXTRA_KEY_GAME_FILE, file);
		startActivity(playGameIntent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result)
	{
		switch (requestCode)
		{
			case CREATE_GAME_REQUEST_CODE:
				if (resultCode == Activity.RESULT_OK)
				{
					File createdGameFile = (File)result.getSerializableExtra(CreateGameActivity.GAME_FILE_KEY);

					GameSummary summary = GameFile.loadGameSummary(createdGameFile);

					m_summariesAdapter.insert(summary, 0);

					Log.d(LOG_TAG, "Created filename: " + createdGameFile.getAbsolutePath());

					// Start the activity to play the game.

					playGame(createdGameFile);
				}

		}
	}

	private void showAboutDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder
				.setTitle(R.string.aboutMahjongScorerTitle)
				.setMessage(getString(R.string.aboutMessage, BuildConfig.VERSION_NAME))
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							// User clicked Cancel button.  Stay on this page.  Nothing to do.
						}
					});

		AlertDialog dialog = builder.create();

		dialog.show();
	}
}

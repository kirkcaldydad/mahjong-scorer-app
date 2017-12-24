package house.mcintosh.mahjong.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import house.mcintosh.mahjong.io.GameFile;
import house.mcintosh.mahjong.model.GameSummary;

public class GameListActivity extends AppCompatActivity
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

		List<GameSummary> games = getGames();

		m_summariesAdapter = new GameSummariesAdapter(this, games);

		ListView messageListView = findViewById(R.id.gameListView);

		messageListView.setAdapter(m_summariesAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_game_list, menu);
		return true;
	}

	private List<GameSummary> getGames()
	{

		List<GameSummary> games = GameFile.getAllGames(this);

		return games;
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
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result)
	{
		switch (requestCode)
		{
			case CREATE_GAME_REQUEST_CODE:
				if (resultCode == Activity.RESULT_OK)
				{
					String createdGameFileName = result.getStringExtra(CreateGameActivity.GAME_FILE_NAME_KEY);

					GameSummary summary = GameFile.getGameSummary(this, createdGameFileName);

					m_summariesAdapter.insert(summary, 0);

					Log.e(LOG_TAG, "Created filename: " + createdGameFileName);
				}

		}
	}
}

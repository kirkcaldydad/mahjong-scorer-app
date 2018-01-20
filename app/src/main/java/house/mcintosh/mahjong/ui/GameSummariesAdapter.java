package house.mcintosh.mahjong.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import house.mcintosh.mahjong.model.GameSummary;
import house.mcintosh.mahjong.model.Player;

public final class GameSummariesAdapter extends ArrayAdapter<GameSummary>
{
	public GameSummariesAdapter(@NonNull Context context, @NonNull List<GameSummary> gameSummaries)
	{
		super(context, 0, gameSummaries);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Get the data item for this position
		GameSummary game = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_summary, parent, false);
		}
		// Lookup views for data population
		TextView tvPlayerName0		= (TextView) convertView.findViewById(R.id.tvPlayerName0);
		TextView tvPlayerName1		= (TextView) convertView.findViewById(R.id.tvPlayerName1);
		TextView tvPlayerName2		= (TextView) convertView.findViewById(R.id.tvPlayerName2);
		TextView tvPlayerName3		= (TextView) convertView.findViewById(R.id.tvPlayerName3);

		TextView tvPlayerScore0		= (TextView) convertView.findViewById(R.id.tvPlayerScore0);
		TextView tvPlayerScore1		= (TextView) convertView.findViewById(R.id.tvPlayerScore1);
		TextView tvPlayerScore2		= (TextView) convertView.findViewById(R.id.tvPlayerScore2);
		TextView tvPlayerScore3		= (TextView) convertView.findViewById(R.id.tvPlayerScore3);
		
		TextView tvCreatedOn		= (TextView) convertView.findViewById(R.id.tvCreatedOn);
		TextView tvLastModifiedOn	= (TextView) convertView.findViewById(R.id.tvLastModifiedOn);

		// Populate the data into the template view using the data object
		tvCreatedOn.setText(game.getCreatedOn());
		tvLastModifiedOn.setText(game.getLastModifiedOn());

		int highestScore = game.getHighestScore();

		List<Player> players = game.getPlayers();

		int playerCount = players.size();

		switch (playerCount)
		{
			case 4:
				showPlayer(game, tvPlayerName3, tvPlayerScore3, --playerCount);
				// fall through
			case 3:
				showPlayer(game, tvPlayerName2, tvPlayerScore2, --playerCount);
				// fall through
			case 2:
				showPlayer(game, tvPlayerName1, tvPlayerScore1, --playerCount);
				// fall through
			case 1:
				showPlayer(game, tvPlayerName0, tvPlayerScore0, --playerCount);
		}

		switch (players.size())
		{
			case 1:
				hidePlayer(tvPlayerName1, tvPlayerScore1);
				// fall through
			case 2:
				hidePlayer(tvPlayerName2, tvPlayerScore2);
				// fall through
			case 3:
				hidePlayer(tvPlayerName3, tvPlayerScore3);
				// fall through
			case 4:
		}

		// Return the completed view to render on screen
		return convertView;
	}

	private void showPlayer(GameSummary game, TextView nameView, TextView scoreView, int playerIndex)
	{
		Player player = game.getPlayers().get(playerIndex);

		nameView.setText(player.getName());
		scoreView.setText(Integer.toString(game.getScore(player)));

		int typeface = ( game.getScore(player) == game.getHighestScore() ) ? Typeface.BOLD : Typeface.NORMAL;

		nameView.setTypeface(null, typeface);
		scoreView.setTypeface(null, typeface);

		nameView.setVisibility(View.VISIBLE);
		scoreView.setVisibility(View.VISIBLE);
	}

	private void hidePlayer(TextView nameView, TextView scoreView)
	{
		nameView.setVisibility(View.INVISIBLE);
		scoreView.setVisibility(View.INVISIBLE);
	}
}

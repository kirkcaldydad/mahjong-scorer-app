package house.mcintosh.mahjong.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import house.mcintosh.mahjong.model.GameSummary;

public class GameSummariesAdapter extends ArrayAdapter<GameSummary>
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
		// Lookup view for data population
		TextView tvPlayers			= (TextView) convertView.findViewById(R.id.tvPlayers);
		TextView tvCreatedOn		= (TextView) convertView.findViewById(R.id.tvCreatedOn);
		TextView tvLastModifiedOn	= (TextView) convertView.findViewById(R.id.tvLastModifiedOn);

		// Populate the data into the template view using the data object
		tvPlayers.setText(game.getPlayerNames());
		tvCreatedOn.setText(game.getCreatedOn());
		tvLastModifiedOn.setText(game.getLastModifiedOn());

		// Return the completed view to render on screen
		return convertView;
	}
}

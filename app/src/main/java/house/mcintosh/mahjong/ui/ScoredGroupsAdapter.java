package house.mcintosh.mahjong.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.util.DisplayUtil;

/**
 * Adapter to map the groups within a ScoredHand for display in a ListView.
 */

public class ScoredGroupsAdapter extends ArrayAdapter<ScoredGroup>
{
	private final TileDrawables m_tileDrawables;

	private final DeleteActioner m_deleteActioner;

	public ScoredGroupsAdapter(
			@NonNull Context context,
			@NonNull ScoredHand hand,
			@NonNull TileDrawables tileDrawables,
			@Nullable DeleteActioner deleteActioner)
	{
		super(context, 0, hand);

		m_tileDrawables = tileDrawables;
		m_deleteActioner = deleteActioner;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Get the data item for this position.

		ScoredGroup group = getItem(position);

		// Check if an existing view is being reused, otherwise inflate the view, then update
		// the views within it to reflect the state of the ScoredGroup.

		if (convertView == null)
		{
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.scored_group, parent, false);
		}

		DisplayUtil.displayTileGroup(group, (LinearLayout)convertView.findViewById(R.id.layoutTileGroup), m_tileDrawables);

		TextView scoreView = convertView.findViewById(R.id.txtGroupScore);
		scoreView.setText(DisplayUtil.getBasicScore(group));

		TextView multiplierView = convertView.findViewById(R.id.txtHandMultipliers);
		multiplierView.setText(DisplayUtil.getScoreMultipliers(group));

		ImageButton deleteButton = convertView.findViewById(R.id.btnDelete);

		deleteButton.setTag(R.id.deleteButtonPositionTag, position);

		deleteButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View button)
			{
				if (m_deleteActioner != null)
					m_deleteActioner.requestDelete((int)button.getTag(R.id.deleteButtonPositionTag));
			}
		});

		return convertView;
	}

	public static abstract class DeleteActioner
	{
		public abstract void requestDelete(int position);
	}
}

package house.mcintosh.mahjong.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import house.mcintosh.mahjong.model.GameSummary;
import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Round;
import house.mcintosh.mahjong.model.RoundInfo;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;

public final class RoundInfosAdapter extends ArrayAdapter<RoundInfo>
{
	private final TileDrawables m_tileDrawables;

	private int standardTileMargin;
	private int groupTileMargin;
	private int tileHeight;
	private int tileWidth;

	public RoundInfosAdapter(@NonNull Context context, @NonNull List<RoundInfo> roundInfos, TileDrawables tileDrawables)
	{
		super(context, 0, roundInfos);
		m_tileDrawables = tileDrawables;

		standardTileMargin = (int)convertDpToPixel(2, getContext());
		groupTileMargin = (int)convertDpToPixel(8, getContext());
		tileHeight = (int)convertDpToPixel(40, getContext());
		tileWidth = (int)convertDpToPixel(30, getContext());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Context context = getContext();

		TextView[] playerNameViews = new TextView[4];
		TextView[] playerWindViews = new TextView[4];
		TextView[] playerScoreIncrementViews = new TextView[4];
		TextView[] playerScoreViews = new TextView[4];
		LinearLayout[] infoLayoutContainers = new LinearLayout[4];
		LinearLayout[] tileLayoutContainers = new LinearLayout[4];

		// Get the data item for this position
		RoundInfo game = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.round_detail, parent, false);
		}

		playerNameViews[0] = (TextView)convertView.findViewById(R.id.txtPlayerName0);
		playerNameViews[1] = (TextView)convertView.findViewById(R.id.txtPlayerName1);
		playerNameViews[2] = (TextView)convertView.findViewById(R.id.txtPlayerName2);
		playerNameViews[3] = (TextView)convertView.findViewById(R.id.txtPlayerName3);

		playerWindViews[0] = (TextView)convertView.findViewById(R.id.txtPlayerWind0);
		playerWindViews[1] = (TextView)convertView.findViewById(R.id.txtPlayerWind1);
		playerWindViews[2] = (TextView)convertView.findViewById(R.id.txtPlayerWind2);
		playerWindViews[3] = (TextView)convertView.findViewById(R.id.txtPlayerWind3);

		playerScoreIncrementViews[0] = (TextView)convertView.findViewById(R.id.txtPlayerScoreIncrement0);
		playerScoreIncrementViews[1] = (TextView)convertView.findViewById(R.id.txtPlayerScoreIncrement1);
		playerScoreIncrementViews[2] = (TextView)convertView.findViewById(R.id.txtPlayerScoreIncrement2);
		playerScoreIncrementViews[3] = (TextView)convertView.findViewById(R.id.txtPlayerScoreIncrement3);

		playerScoreViews[0] = (TextView)convertView.findViewById(R.id.txtPlayerScore0);
		playerScoreViews[1] = (TextView)convertView.findViewById(R.id.txtPlayerScore1);
		playerScoreViews[2] = (TextView)convertView.findViewById(R.id.txtPlayerScore2);
		playerScoreViews[3] = (TextView)convertView.findViewById(R.id.txtPlayerScore3);

		infoLayoutContainers[0] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer0);
		infoLayoutContainers[1] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer1);
		infoLayoutContainers[2] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer2);
		infoLayoutContainers[3] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer3);

		tileLayoutContainers[0] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer0);
		tileLayoutContainers[1] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer1);
		tileLayoutContainers[2] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer2);
		tileLayoutContainers[3] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer3);

		RoundInfo roundInfo = getItem(position);
		int highestScore = Integer.MIN_VALUE;

		for (int i = 0 ; i < 4 ; i++)
		{
			Player player = roundInfo.getPlayer(i);
			Round round = roundInfo.getRound();
			LinearLayout tileLayoutContainer = tileLayoutContainers[i];
			tileLayoutContainer.removeAllViews();

			if (player != null)
			{
				playerNameViews[i].setText(player.getName());

				playerWindViews[i].setText(round.getPlayerWind(player).getName(context));

				int increment = roundInfo.getScoreIncrement(player);
				String displayIncrement = Integer.toString(increment);

				if (increment >= 0)
					displayIncrement = "+" + displayIncrement;

				int score = roundInfo.getScore(player);
				String displayScore = "= " + Integer.toString(score);

				playerScoreIncrementViews[i].setText(displayIncrement);
				playerScoreViews[i].setText(displayScore);

				ScoredHand hand = round.getHand(player);

				displayHand(tileLayoutContainer, hand);

				infoLayoutContainers[i].setVisibility(View.VISIBLE);
				tileLayoutContainers[i].setVisibility(View.VISIBLE);

				highestScore = Math.max(highestScore, score);
			}
			else
			{
				infoLayoutContainers[i].setVisibility(View.GONE);
				tileLayoutContainers[i].setVisibility(View.GONE);
			}
		}

		for (int i = 0 ; i < 4 ; i++)
		{
			Player player = roundInfo.getPlayer(i);

			if (player == null)
				continue;

			int typeface = ( roundInfo.getScore(player) == highestScore ) ? Typeface.BOLD : Typeface.NORMAL;

			playerScoreViews[i].setTypeface(null, typeface);
		}

		// Return the completed view to render on screen
		return convertView;
	}

	private void displayHand(LinearLayout parent, ScoredHand hand)
	{
		int handSize = hand.size();
		int lastGroupIndex = handSize - 1;

		for (int groupIndex = 0 ; groupIndex < handSize ; groupIndex++)
		{
			boolean firstGroup = groupIndex == 0;
			boolean lastGroup = groupIndex == lastGroupIndex;

			ScoredGroup group = hand.get(groupIndex);
			List<Tile> tiles = group.getTiles();

			int groupSize = tiles.size();
			int lastTileIndex = groupSize - 1;

			for (int tileIndex = 0 ; tileIndex < groupSize ; tileIndex++)
			{
				boolean firstTile = tileIndex == 0;
				boolean lastTile = tileIndex == lastTileIndex;

				Tile tile = tiles.get(tileIndex);
				ImageView imageView = new ImageView(getContext(), null, 0, R.style.tile);

				Drawable tileDrawable = group.isConcealed() && (tileIndex == 0 || tileIndex == 3) ?
										m_tileDrawables.getTileBack() :
										m_tileDrawables.get(tile);
				imageView.setImageDrawable(tileDrawable);

				parent.addView(imageView);

				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)imageView.getLayoutParams();
				layoutParams.width = tileWidth;
				layoutParams.height = tileHeight;

				int tileMarginLeft = firstTile ? groupTileMargin : standardTileMargin;
				int tileMarginRight = lastTile ? groupTileMargin : standardTileMargin;

				if (firstGroup && firstTile)
					tileMarginLeft = 0;

				if (lastGroup && lastTile)
					tileMarginRight = 0;

				layoutParams.setMargins(tileMarginLeft, standardTileMargin, tileMarginRight, standardTileMargin);
				imageView.setLayoutParams(layoutParams);
			}

			firstGroup = false;
		}
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density.
	 *
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context)
	{
				Resources resources = context.getResources();
				DisplayMetrics metrics = resources.getDisplayMetrics();
				float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				return px;
	}
}

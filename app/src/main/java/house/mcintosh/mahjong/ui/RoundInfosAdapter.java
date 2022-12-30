package house.mcintosh.mahjong.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import house.mcintosh.mahjong.model.Player;
import house.mcintosh.mahjong.model.Round;
import house.mcintosh.mahjong.model.RoundInfo;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.util.DisplayUtil;

public final class RoundInfosAdapter extends ArrayAdapter<RoundInfo>
{
	private final TileDrawables m_tileDrawables;

	private int standardTileMargin;
	private int groupTileMargin;
	private int standardTilePadding;
	private int backTilePadding;
	private int tileHeight;
	private int tileWidth;

	public RoundInfosAdapter(@NonNull Context context, @NonNull List<RoundInfo> roundInfos, TileDrawables tileDrawables)
	{
		super(context, 0, roundInfos);
		m_tileDrawables = tileDrawables;

		standardTileMargin = DisplayUtil.getPxDimension(context, R.dimen.displayTileMargin);
		groupTileMargin = DisplayUtil.getPxDimension(context, R.dimen.displayGroupMargin);
		standardTilePadding = DisplayUtil.getPxDimension(context, R.dimen.displayTilePadding);
		backTilePadding = DisplayUtil.getPxDimension(context, R.dimen.displayTileBorder);
		tileHeight = DisplayUtil.getPxDimension(context, R.dimen.displayTileHeight);
		tileWidth = DisplayUtil.getPxDimension(context, R.dimen.displayTileWidth);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Context context = getContext();

		TextView[] playerNameViews = new TextView[4];
		TextView[] playerWindViews = new TextView[4];
		TextView[] playerScoreIncrementViews = new TextView[4];
		TextView[] playerScoreViews = new TextView[4];
		TextView[] handDescriptionViews = new TextView[4];
		LinearLayout[] infoLayoutContainers = new LinearLayout[4];
		LinearLayout[] tileLayoutContainers = new LinearLayout[4];

		// Get the data item for this position
		RoundInfo roundInfo = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.round_detail, parent, false);
		}

		int colorResource = (position % 2) == 0 ? R.color.secondaryLightBackgroundColor : R.color.primaryLightBackgroundColor;
		convertView.setBackgroundResource(colorResource);

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

		handDescriptionViews[0] = (TextView)convertView.findViewById(R.id.txtHandDescription0);
		handDescriptionViews[1] = (TextView)convertView.findViewById(R.id.txtHandDescription1);
		handDescriptionViews[2] = (TextView)convertView.findViewById(R.id.txtHandDescription2);
		handDescriptionViews[3] = (TextView)convertView.findViewById(R.id.txtHandDescription3);

		infoLayoutContainers[0] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer0);
		infoLayoutContainers[1] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer1);
		infoLayoutContainers[2] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer2);
		infoLayoutContainers[3] = (LinearLayout)convertView.findViewById(R.id.layoutInfoPlayer3);

		tileLayoutContainers[0] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer0);
		tileLayoutContainers[1] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer1);
		tileLayoutContainers[2] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer2);
		tileLayoutContainers[3] = (LinearLayout)convertView.findViewById(R.id.layoutTilesPlayer3);

		int highestScore = Integer.MIN_VALUE;

		if (roundInfo.hasRound())
		{
			int roundNumber = getCount() - position - 1;

			String roundNumberMessage = context.getResources().getString(R.string.roundNumber, Integer.toString(roundNumber));
			((TextView)convertView.findViewById(R.id.txtRoundDescription)).setText(roundNumberMessage);

			String prevailingWindName = roundInfo.getRound().getPrevailingWind().getName(context);
			((TextView)convertView.findViewById(R.id.txtPrevailingWindName)).setText(prevailingWindName);

			for (int i = 0; i < 4; i++)
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

					playerScoreIncrementViews[i].setVisibility(View.VISIBLE);

					ScoredHand hand = round.getHand(player);

					CharSequence handDescription = DisplayUtil.getScoreDescription(context, hand);

					TextView handDescriptionView = handDescriptionViews[i];
					handDescriptionView.setText(handDescription);

					handDescriptionView.setVisibility(handDescription.length() > 0 ? View.VISIBLE : View.GONE);

					displayHandTiles(tileLayoutContainer, hand);

					infoLayoutContainers[i].setVisibility(View.VISIBLE);
					tileLayoutContainers[i].setVisibility(View.VISIBLE);

					highestScore = Math.max(highestScore, score);
				}
				else
				{
					infoLayoutContainers[i].setVisibility(View.GONE);
					tileLayoutContainers[i].setVisibility(View.GONE);
					handDescriptionViews[i].setVisibility(View.GONE);
				}
			}

			for (int i = 0; i < 4; i++)
			{
				Player player = roundInfo.getPlayer(i);

				if (player == null)
					continue;

				int typeface = (roundInfo.getScore(player) == highestScore) ? Typeface.BOLD : Typeface.NORMAL;

				playerScoreViews[i].setTypeface(null, typeface);
				playerNameViews[i].setTypeface(null, typeface);
			}
		}
		else
		{
			// No round available - This must be the initial score.

			((TextView)convertView.findViewById(R.id.txtRoundDescription)).setText(R.string.gameStart);

			String prevailingWindName = Wind.EAST.getName(context);
			((TextView)convertView.findViewById(R.id.txtPrevailingWindName)).setText(prevailingWindName);

			for (int i = 0 ; i < 4 ; i++)
			{
				Player player = roundInfo.getPlayer(i);

				if (player != null)
				{
					playerNameViews[i].setText(player.getName());
					playerWindViews[i].setText(roundInfo.getPlayerWind(player).getName(context));

					playerScoreViews[i].setText(Integer.toString(roundInfo.getScore(player)));
					playerScoreViews[i].setTypeface(null, Typeface.NORMAL);

					playerScoreIncrementViews[i].setVisibility(View.GONE);
				}
				else
				{
					infoLayoutContainers[i].setVisibility(View.GONE);
				}

				handDescriptionViews[i].setVisibility(View.GONE);
				tileLayoutContainers[i].setVisibility(View.GONE);
			}
		}

		// Return the completed view to render on screen
		return convertView;
	}

	private void displayHandTiles(LinearLayout parent, ScoredHand hand)
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

				Drawable tileDrawable;
				int padding;

				if (group.isConcealed() && (tileIndex == 0 || tileIndex == 3))
				{
					tileDrawable = m_tileDrawables.getTileBack();
					padding = backTilePadding;
				}
				else
				{
					tileDrawable = m_tileDrawables.get(tile);
					padding = standardTilePadding;
				}

				imageView.setImageDrawable(tileDrawable);
				imageView.setPadding(padding, padding, padding, padding);

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
}

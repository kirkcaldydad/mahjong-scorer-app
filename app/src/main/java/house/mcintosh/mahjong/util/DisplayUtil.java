package house.mcintosh.mahjong.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.scoring.ScoreContribution;
import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.ui.TileDrawables;

/**
 * Utilities class to factor out some common display operations.
 */

public final class DisplayUtil
{
	/**
	 * Display a group of tiles.
	 *
	 * @param group				The group of tiles to display.
	 * @param view				A GroupView which must have four children of type ImageView.
	 * @param drawables			Cache of drawables for the tile images.
	 */

	public static void displayTileGroup(
			Group group,
			ViewGroup view,
			TileDrawables drawables)
	{
		// Get the tiles to display, or use an empty list to display no tiles.
		List<Tile> tiles = (group == null) ? Collections.<Tile>emptyList() : group.getTiles();

		int i = 0;
		for ( ; i < tiles.size(); i++)
		{
			Drawable tileDrawable;

			if (group.isConcealed() && (i == 0 || i == 3))
			{
				tileDrawable = drawables.getTileBack();
			}
			else
			{
				Tile tile = tiles.get(i);
				tileDrawable = drawables.get(tile);
			}


			ImageView imageView = (ImageView)view.getChildAt(i);

			imageView.setImageDrawable(tileDrawable);
			imageView.setVisibility(View.VISIBLE);
		}

		// Hide any display tiles to the right of those we need to display.
		while (i < 4)
		{
			View imageView = view.getChildAt(i);
			imageView.setVisibility(View.INVISIBLE);
			i++;
		}
	}

	public static String getBasicScore(ScoredGroup group)
	{
		int basicScore = 0;

		for (ScoreContribution contribution : group.getScore())
		{
			basicScore += contribution.getScore();
		}

		return Integer.toString(basicScore);
	}

	public static String getScoreMultipliers(ScoredGroup group)
	{
		StringBuilder multipliers = new StringBuilder();

		for (ScoreContribution contribution : group.getScore())
		{
			int multiplier = contribution.getHandMultiplier();

			if (multiplier != 1)
			{
				multipliers.append("x").append(multiplier);
			}
		}

		return multipliers.toString();
	}

	public static String getTotalScore(ScoredHand hand)
	{
		return Integer.toString(hand.getTotalScore());
	}

	public static String getTotalCalculation(ScoredHand hand)
	{
		int basicScore = 0;
		int multiplier = 1;

		for (ScoredGroup group : hand)
		{
			for (ScoreContribution contribution : group.getScore())
			{
				basicScore += contribution.getScore();
				multiplier *= contribution.getHandMultiplier();
			}
		}

		if (multiplier != 1)
			return "(" + basicScore + 'x' + multiplier + ')';

		return "";
	}
}

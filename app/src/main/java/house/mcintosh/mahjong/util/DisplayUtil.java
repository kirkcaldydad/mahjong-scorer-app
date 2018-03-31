package house.mcintosh.mahjong.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.scoring.ScoreContribution;
import house.mcintosh.mahjong.scoring.ScoreList;
import house.mcintosh.mahjong.scoring.ScoredGroup;
import house.mcintosh.mahjong.scoring.ScoredHand;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.ui.R;
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

	public static String getHandScoreWithStatus(Context context, ScoredHand hand)
	{
		int score = hand.getTotalScore();

		StringBuilder sb = new StringBuilder();

		sb.append('(').append(score);

		if (hand.isMahjong())
			sb.append(' ').append(context.getText(R.string.mahjong));

		sb.append(')');

		return sb.toString();
	}

	public static String getScoreIncrementWithStatus(Context context, int fromRoundScore, int toRoundScore, ScoredHand hand)
	{
		int scoreIncrement = toRoundScore - fromRoundScore;

		StringBuilder sb = new StringBuilder();

		if (scoreIncrement >= 0)
			sb.append('+');

		sb.append(scoreIncrement);

		if (hand.isMahjong())
			sb.append(' ').append(context.getText(R.string.mahjong));

		return sb.toString();
	}

	public static CharSequence getWholeHandScores(ScoredHand hand)
	{
		int basicScore = 0;

		StringBuilder sb = new StringBuilder();

		for (ScoreContribution contribution : hand.getWholeHandScores())
		{
			basicScore += contribution.getScore();

			int multiplier = contribution.getHandMultiplier();

			if (multiplier != 1)
			{
				sb.append('x').append(multiplier);
			}
		}

		if (basicScore > 0)
			sb.insert(0, "   ").insert(0, basicScore);

		return sb;
	}

	public static String getTotalScore(ScoredHand hand)
	{
		int score = hand.getTotalScore();

		if (score == 0)
			return "";

		return Integer.toString(score);
	}

	public static CharSequence getTotalCalculation(ScoredHand hand)
	{
		int groupBasicScore = 0;
		int handBasicScore = 0;
		int multiplier = 1;

		for (ScoredGroup group : hand)
		{
			for (ScoreContribution contribution : group.getScore())
			{
				groupBasicScore += contribution.getScore();
				multiplier *= contribution.getHandMultiplier();
			}
		}

		for (ScoreContribution contribution : hand.getWholeHandScores())
		{
			handBasicScore += contribution.getScore();
			multiplier *= contribution.getHandMultiplier();
		}

		StringBuilder sb = new StringBuilder();

		if (groupBasicScore > 0 && handBasicScore > 0 && multiplier != 1)
			sb.append('(');

		if (groupBasicScore > 0 && (handBasicScore > 0 || multiplier != 1))
			sb.append(groupBasicScore);

		if (groupBasicScore > 0 && handBasicScore > 0)
			sb.append('+');

		if (handBasicScore > 0 && (groupBasicScore > 0 || multiplier != 1))
			sb.append(handBasicScore);

		if (groupBasicScore > 0 && handBasicScore > 0 && multiplier != 1)
			sb.append(')');

		if (multiplier != 1 && (groupBasicScore > 0 || handBasicScore > 0))
			sb.append('x').append(multiplier);

		return sb;
	}

	public static CharSequence getScoreDescription(Context context, ScoredHand hand)
	{
		StringBuilder sb = new StringBuilder();
		ScoreList scores = hand.getWholeHandScores();

		for (ScoreContribution contribution : scores)
		{
			if (!contribution.hasScore())
				continue;

			ScoringScheme.ScoreElement element = contribution.getElement();

			if (!element.hasDescription())
				continue;

			if (sb.length() != 0)
				sb.append(", ");

			sb.append(element.getDescription(context));
		}

		return sb;
	}
}

package house.mcintosh.mahjong.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;
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
}

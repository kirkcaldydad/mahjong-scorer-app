package house.mcintosh.mahjong.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

import house.mcintosh.mahjong.model.Tile;

/**
 * A cache of the drawables associated with tiles in the current context.
 */

public final class TileDrawables
{
	private final Context m_context;

	private final Map<Tile, Drawable> m_cache = new HashMap<>();

	private Drawable m_tileBackDrawable;

	TileDrawables(Context context)
	{
		m_context = context;
	}

	/**
	 * Get the drawable associated with a tile.
	 */
	public Drawable get(Tile tile)
	{
		// Get the drawable from cache if available, else load it into cache.

		Drawable drawable = m_cache.get(tile);

		if (drawable == null)
		{
			// Derive the name of the drawable, and look up the drawable itself.

			String name;
			Tile.Type type = tile.getType();

			switch (type)
			{
				case SUIT:
					name = tile.getSuit().drawableNameComponent + tile.getNumber().drawableNameComponent;
					break;
				case DRAGON:
					name = type.drawableNameComponent + '_' + tile.getDragon().drawableNameComponent;
					break;
				case WIND:
					name = type.drawableNameComponent + '_' + tile.getWind().drawableNameComponent;
					break;
				default:
					// Never get here.
					name = null;
			}

			Resources resources = m_context.getResources();
			int resourceId = resources.getIdentifier(name, "drawable", m_context.getPackageName());
			drawable = getDrawable(resourceId);

			m_cache.put(tile, drawable);
		}

		return drawable;
	}

	/**
	 * @return	The special drawable that represents the back of a tile.
	 */
	public Drawable getTileBack()
	{
		if (m_tileBackDrawable == null)
			m_tileBackDrawable = getDrawable(R.drawable.tile_back);

		return m_tileBackDrawable;
	}

	private Drawable getDrawable(int resourceId)
	{
		return m_context.getResources().getDrawable(resourceId, m_context.getTheme());
	}
}

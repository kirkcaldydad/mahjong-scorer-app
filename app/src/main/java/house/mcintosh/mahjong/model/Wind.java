package house.mcintosh.mahjong.model;

import android.content.Context;

import house.mcintosh.mahjong.ui.R;

public enum Wind
{
	EAST(R.string.east),
	SOUTH(R.string.south),
	WEST(R.string.west),
	NORTH(R.string.north);

	public final String drawableNameComponent;
	private final int m_nameResourceId;

	Wind(int nameResourceId)
	{
		drawableNameComponent = this.name().toLowerCase();
		m_nameResourceId = nameResourceId;
	}
	
	/**
	 * @return The next wind in the normal rotation sequence of Winds.
	 */
	public Wind next()
	{
		Wind[] values = Wind.values();
		
		return values[(this.ordinal() + 1) % values.length];
	}

	public String getName(Context context)
	{
		return context.getResources().getString(m_nameResourceId);
	}
}

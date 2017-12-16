package house.mcintosh.mahjong.model;

public enum Wind
{
	EAST,
	SOUTH,
	WEST,
	NORTH;
	
	/**
	 * @return The next wind in the normal rotation sequence of Winds.
	 */
	public Wind next()
	{
		Wind[] values = Wind.values();
		
		return values[(this.ordinal() + 1) % values.length];
	}
}

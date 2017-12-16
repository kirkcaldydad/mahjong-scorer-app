package house.mcintosh.mahjong.model;

import java.util.Comparator;

/**
 * Compares two Sets for the purpose of sorting Sets into a consistent order.  The order
 * in which dragons, winds and suits are defined in their enumerations contributes to the
 * sort order.
 */

public class SetComparator implements Comparator<Group>
{
	@Override
	public int compare(Group s1, Group s2)
	{
		Group.Type s1Type = s1.getType();
		Group.Type s2Type = s2.getType();
		
		if (s1Type == Group.Type.PAIR && s2Type == Group.Type.PAIR)
			return compareSets(s1, s2);
		
		if (s1Type == Group.Type.PAIR && s2Type != Group.Type.PAIR)
			return 1;
		
		if (s2Type == Group.Type.PAIR && s1Type != Group.Type.PAIR)
			return -1;
		
		return compareSets(s1, s2);
	}
	
	private int compareSets(Group s1, Group s2)
	{
		Tile s1Tile = s1.getFirstTile();
		Tile s2Tile = s2.getFirstTile();
		
		int comparison = s1Tile.getType().ordinal() - s2Tile.getType().ordinal();
		
		if (comparison != 0)
			return comparison;
		
		switch (s1Tile.getType())
		{
		case DRAGON:
			return s1Tile.getDragon().ordinal() - s2Tile.getDragon().ordinal();
			
		case WIND:
			return s1Tile.getWind().ordinal() - s2Tile.getWind().ordinal();
			
		default:
			break;
		}
		
		// Must be a suit.
		
		comparison = s1Tile.getSuit().ordinal() - s2Tile.getSuit().ordinal();
		
		if (comparison != 0)
			return comparison;
		
		// Same suit.  Pung and Kong always come before Chow.
		
		Group.Type s1Type = s1.getType();
		Group.Type s2Type = s2.getType();
		
		if (s1Type == Group.Type.CHOW && s2Type != Group.Type.CHOW)
			return 1;
		
		if (s2Type == Group.Type.CHOW && s1Type != Group.Type.CHOW)
			return -1;
		
		return s1Tile.getNumber().ordinal() - s2Tile.getNumber().ordinal();
	}
}

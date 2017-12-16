package house.mcintosh.mahjong.scoring;

import java.util.ArrayList;

public class ScoreList extends ArrayList<ScoreContribution>
{
	private static final long serialVersionUID = 1L;
	
	public static final ScoreList EMPTY = new ScoreList();

	public int getTotal()
	{
		int score		= 0;
		int multiplier	= 1;
		
		for (ScoreContribution pair : this)
		{
			score 		+= pair.getScore();
			multiplier	*= pair.getHandMultiplier();
		}
		
		return score * multiplier;
	}
	
	public boolean add(ScoreContribution score)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Add a contribution to the list, and return the list itself for chaining.
	 */
	public ScoreList append(ScoreContribution contribution)
	{
		// Won't stop all modifications to EMPTY, but helps.
		
		if (this == EMPTY)
			throw new UnsupportedOperationException();
		
		super.add(contribution);
		
		return this;
	}
	
	public ScoreList append(ScoreList scores)
	{
		// Won't stop all modifications to EMPTY, but helps.
		
		if (this == EMPTY)
			throw new UnsupportedOperationException();
		
		for (ScoreContribution score : scores)
			super.add(score);
		
		return this;
	}
}

package house.mcintosh.mahjong.scoring;

import house.mcintosh.mahjong.exception.InvalidScoreSchemeException;
import house.mcintosh.mahjong.scoring.ScoringScheme.ScoreElement;

/**
 * A ScorePair is a combination of a simple score and a hand multiplier.
 */

public class ScoreContribution
{
	static public final ScoreContribution NULL = new ScoreContribution(ScoreElement.UNKNOWN, 0, 1);
	
	private final int			m_score;
	private final int			m_multiplier;
	private final ScoreElement	m_element;
	
	ScoreContribution(ScoreElement element, int score, int multiplier)
	{
		m_score			= score;
		m_multiplier	= multiplier;
		m_element		= element;
	
		if (score < 0)
			throw new InvalidScoreSchemeException("Invalid score value");
		
		if (multiplier < 1)
			throw new InvalidScoreSchemeException("Invalid score multiplier");
	}
	
	final int getScore()
	{
		return m_score;
	}
	
	final int getHandMultiplier()
	{
		return m_multiplier;
	}
	
	final ScoreElement getElement()
	{
		return m_element;
	}
	
	public final String toString()
	{
		return '[' + m_element.toString() + ", " + m_score + ", " + m_multiplier + "]";
	}
}

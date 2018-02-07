package house.mcintosh.mahjong.scoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;

import house.mcintosh.mahjong.exception.InvalidScoreSchemeException;
import house.mcintosh.mahjong.scoring.ScoringScheme.ScoreElement;
import house.mcintosh.mahjong.util.JsonUtil;

/**
 * A ScorePair is a combination of a simple score and a hand multiplier.
 */

public final class ScoreContribution implements Serializable
{
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
	
	public final int getScore()
	{
		return m_score;
	}
	
	public final int getHandMultiplier()
	{
		return m_multiplier;
	}

	public final boolean hasScore()
	{
		return m_score != 0 || m_multiplier != 1;
	}
	
	public final ScoreElement getElement()
	{
		return m_element;
	}
	
	public final String toString()
	{
		return '[' + m_element.toString() + ", " + m_score + ", " + m_multiplier + "]";
	}

	JsonNode toJson()
	{
		ObjectNode contributionNode = JsonUtil.createObjectNode();

		contributionNode.put("name", m_element.name());

		if (m_score != 0)
			contributionNode.put("score", m_score);

		if (m_multiplier != 1)
			contributionNode.put("multiplier", m_multiplier);

		return contributionNode;
	}

	static ScoreContribution fromJson(JsonNode node)
	{
		ScoreElement element = ScoreElement.valueOf(node.get("name").asText());

		int score = node.path("score").asInt(0);
		int multiplier = node.path("multiplier").asInt(1);
		return new ScoreContribution(element, score, multiplier);
	}
}

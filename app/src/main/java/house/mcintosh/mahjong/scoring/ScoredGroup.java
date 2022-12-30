package house.mcintosh.mahjong.scoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;

import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoringScheme.ScoreElement;

/**
 * A Group that has been scored.  Immutable because the Set is is constructed from is immutable, and
 * the score is calculated during construction.
 */
public final class ScoredGroup extends Group implements Serializable
{
	private final ScoreList	m_score;

	// We keep references to these items, but there is no need to include them in our
	// json serialisation because they are restored from a higher level during deserialisation.
	private final ScoringScheme m_scheme;
	private final Wind m_ownWind;
	private final Wind m_prevailingWind;
	
	public ScoredGroup(Group group, ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		super(group);
		
		switch (getType())
		{
		case PAIR:
			m_score = scorePair(scheme, ownWind, prevailingWind);
			break;
		case CHOW:
			m_score = scoreChow(scheme, ownWind, prevailingWind);
			break;
		case PUNG:
			m_score = scorePung(scheme, ownWind, prevailingWind);
			break;
		case KONG:
			m_score = scoreKong(scheme, ownWind, prevailingWind);
			break;
			
		default:
			// Never get here.
			m_score = ScoreList.EMPTY;
			break;
		}

		m_scheme = scheme;
		m_ownWind = ownWind;
		m_prevailingWind = prevailingWind;
	}

	/**
	 * Get a new instance, identical to this instance, except that visibility is toggled.
	 */
	public ScoredGroup toggleVisibility()
	{
		Group group = super.toggleVisibility();

		return new ScoredGroup(group, m_scheme, m_ownWind, m_prevailingWind);
	}
	
	public ScoreList getScore()
	{
		return m_score;
	}
	
	private ScoreList scorePair(ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		switch (getTileType())
		{
		case SUIT:
			return scheme.getScoreContribution(ScoreElement.PairSuitScore);
		case WIND:
			{
				Wind wind = getFirstTile().getWind();
				if (wind == ownWind)
					return scheme.getScoreContribution(ScoreElement.PairOwnWindScore);
				if (wind == prevailingWind)
					return scheme.getScoreContribution(ScoreElement.PairPrevailingWindScore);
				return scheme.getScoreContribution(ScoreElement.PairWindScore);
			}
		case DRAGON:
			return scheme.getScoreContribution(ScoreElement.PairDragonScore);
		
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}
	
	private ScoreList scoreChow(ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		switch (getTileType())
		{
		case SUIT:
			return scheme.getScoreContribution(ScoreElement.ChowSuitScore);
		
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}
	
	private ScoreList scorePung(ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		Tile	tile		= getFirstTile();
		boolean concealed	= isConcealed();
		
		switch (getTileType())
		{
		case SUIT:
			{
				if (tile.isMajor())
					return concealed ? scheme.getScoreContribution(ScoreElement.PungConcealedMajorSuitScore) : scheme.getScoreContribution(ScoreElement.PungExposedMajorSuitScore);
				else
					return concealed ? scheme.getScoreContribution(ScoreElement.PungConcealedMinorSuitScore) : scheme.getScoreContribution(ScoreElement.PungExposedMinorSuitScore);
			}
			
		case WIND:
			{
				Wind wind = tile.getWind();
				if (wind == ownWind && wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.PungConcealedPrevailingOwnWindScore)	: scheme.getScoreContribution(ScoreElement.PungExposedPrevailingOwnWindScore);
				if (wind == ownWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.PungConcealedOwnWindScore)			: scheme.getScoreContribution(ScoreElement.PungExposedOwnWindScore);
				if (wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.PungConcealedPrevailingWindScore)	: scheme.getScoreContribution(ScoreElement.PungExposedPrevailingWindScore);
				
				return concealed		? scheme.getScoreContribution(ScoreElement.PungConcealedWindScore)				: scheme.getScoreContribution(ScoreElement.PungExposedWindScore);
			}
			
		case DRAGON:
			return concealed ? scheme.getScoreContribution(ScoreElement.PungConcealedDragonScore) : scheme.getScoreContribution(ScoreElement.PungExposedDragonScore);
			
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}
	
	private ScoreList scoreKong(ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		Tile	tile		= getFirstTile();
		boolean concealed	= isConcealed();
		
		switch (getTileType())
		{
		case SUIT:
			{
				if (tile.isMajor())
					return concealed ? scheme.getScoreContribution(ScoreElement.KongConcealedMajorSuitScore) : scheme.getScoreContribution(ScoreElement.KongExposedMajorSuitScore);
				else
					return concealed ? scheme.getScoreContribution(ScoreElement.KongConcealedMinorSuitScore) : scheme.getScoreContribution(ScoreElement.KongExposedMinorSuitScore);
			}
			
		case WIND:
			{
				Wind wind = tile.getWind();
				if (wind == ownWind && wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.KongConcealedPrevailingOwnWindScore)	: scheme.getScoreContribution(ScoreElement.KongExposedPrevailingOwnWindScore);
				if (wind == ownWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.KongConcealedOwnWindScore)			: scheme.getScoreContribution(ScoreElement.KongExposedOwnWindScore);
				if (wind == prevailingWind)
					return concealed	? scheme.getScoreContribution(ScoreElement.KongConcealedPrevailingWindScore)	: scheme.getScoreContribution(ScoreElement.KongExposedPrevailingWindScore);
				
				return concealed		? scheme.getScoreContribution(ScoreElement.KongConcealedWindScore)				: scheme.getScoreContribution(ScoreElement.KongExposedWindScore);
			}
			
		case DRAGON:
			return concealed ? scheme.getScoreContribution(ScoreElement.KongConcealedDragonScore) : scheme.getScoreContribution(ScoreElement.KongExposedDragonScore);
			
		default:
			// Never get here.
			return ScoreList.EMPTY;
		}
	}

	static public ScoredGroup fromJson(JsonNode scoredGroup, ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		Group group = Group.fromJson(scoredGroup);

		return new ScoredGroup(group, scheme, ownWind, prevailingWind);
	}
}

package house.mcintosh.mahjong.scoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import house.mcintosh.mahjong.exception.InvalidHandException;
import house.mcintosh.mahjong.model.Group;
import house.mcintosh.mahjong.model.GroupComparator;
import house.mcintosh.mahjong.model.Tile;
import house.mcintosh.mahjong.model.Wind;
import house.mcintosh.mahjong.scoring.ScoringScheme.ScoreElement;
import house.mcintosh.mahjong.util.JsonUtil;

/**
 * A hand that is scored.  A scored hand can change. As sets are added to it the score and
 * Mahjong status of the hand change.
 *
 * Extends ArrayList so that an ArrayAdaptor can be used for display.  However, ScoredGroups should
 * be added using the add() method only, ensuring that the list is sorted and the score updated.
 */

public class ScoredHand extends ArrayList<ScoredGroup>
{
	private final ScoringScheme					m_scheme;
	
	private ScoreList							m_scores;
	
	private boolean	m_requirePairConcealedInfo	= false;
	private boolean	m_mahjongPairConcealed		= false;
	private boolean	m_mahjongByLooseTile		= false;
	private boolean	m_mahjongByWallTile			= false;
	private boolean	m_mahjongByLastWallTile		= false;
	private boolean	m_mahjongByLastDiscard		= false;
	private boolean	m_mahjongByRobbingKong		= false;
	private boolean	m_mahjongByOnlyPossibleTile	= false;
	private boolean	m_mahjongByOriginalCall		= false;
	private boolean	m_nonMahjongByOriginalCall	= false;

	private boolean	m_isMahjong					= false;
	private int		m_totalScoreUnlimited		= 0;
	private int		m_totalScoreLimited			= 0;
	
	public ScoredHand(ScoringScheme scheme)
	{
		m_scheme		= scheme;
	}

	@Override
	public boolean add(ScoredGroup group)
	{
		super.add(group);

		Collections.sort(this, new GroupComparator());
		
		updateScore();

		return true;
	}
	
	public int getTotalScore()
	{
		return m_totalScoreLimited;
	}
	
	public int getTotalScoreUnlimited()
	{
		return m_totalScoreUnlimited;
	}
	
	public boolean isMahjong()
	{
		return m_isMahjong;
	}
	
	public void setMahjongByWallTile(boolean fromWall)
	{
		m_mahjongByWallTile = fromWall;
		updateScore();
	}
	
	public boolean isMahjongByWallTile()
	{
		return m_mahjongByWallTile;
	}

	public void setMahjongByLastWallTile(boolean isLast)
	{
		m_mahjongByLastWallTile = isLast;
		updateScore();
	}
	
	public boolean isMahjongByLastWallTile()
	{
		return m_mahjongByLastWallTile;
	}

	public void setMahjongByOnlyPossibleTile(boolean only)
	{
		m_mahjongByOnlyPossibleTile = only;
		updateScore();
	}
	
	public boolean isMahjongByOnlyPossibleTile()
	{
		return m_mahjongByOnlyPossibleTile;
	}
	
	/**
	 * Evaluation of the Mahjong hand should call this to determine whether additional info
	 * is required about concealment of the pair.  setMahjongPairConcealed can then be called
	 * if necessary.
	 */
	public boolean requiresPairConcealedInfo()
	{
		return m_requirePairConcealedInfo;
	}
	
	public void setMahjongPairConcealed(boolean concealed)
	{
		m_mahjongPairConcealed = concealed;
		updateScore();
	}

	public boolean isMahjongByLooseTile()
	{
		return m_mahjongByLooseTile;
	}

	public void setMahjongByLooseTile(boolean mahjongByLooseTile)
	{
		this.m_mahjongByLooseTile = mahjongByLooseTile;
		updateScore();
	}

	public boolean isMahjongByLastDiscard()
	{
		return m_mahjongByLastDiscard;
	}

	public void setMahjongByLastDiscard(boolean mahjongByLastDiscard)
	{
		this.m_mahjongByLastDiscard = mahjongByLastDiscard;
		updateScore();
	}

	public boolean isMahjongByRobbingKong()
	{
		return m_mahjongByRobbingKong;
	}

	public void setMahjongByRobbingKong(boolean mahjongByRobbingKong)
	{
		this.m_mahjongByRobbingKong = mahjongByRobbingKong;
		updateScore();
	}

	public boolean isMahjongByOriginalCall()
	{
		return m_mahjongByOriginalCall;
	}

	public void setMahjongByOriginalCall(boolean mahjongByOriginalCall)
	{
		this.m_mahjongByOriginalCall = mahjongByOriginalCall;
		updateScore();
	}

	public boolean isNonMahjongByOriginalCall()
	{
		return m_nonMahjongByOriginalCall;
	}

	public void setNonMahjongByOriginalCall(boolean nonMahjongByOriginalCall)
	{
		this.m_nonMahjongByOriginalCall = nonMahjongByOriginalCall;
		updateScore();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("[\n");
		
		for (Group set : this)
		{
			sb.append("  ");
			sb.append(set);
			sb.append('\n');
		}
		
		sb.append(']');
		
		return sb.toString();
	}
	
	/**
	 * Recalculate the score of the hand, based on the current sets.  Also performs some
	 * sanity checking on the hand, and calculates whether it is a mahjong hand.
	 */
	private void updateScore()
	{
		// Zero score in case we exit early.
		m_totalScoreLimited = m_totalScoreUnlimited = 0;
		
		ScoreList scores = new ScoreList();
		
		int effectiveHandTiles	= 0;
		int pairCount			= 0;
		
		for (ScoredGroup group : this)
		{
			scores.append(group.getScore());
			
			effectiveHandTiles += group.getType().getHandSize();
			
			if (group.getType() == Group.Type.PAIR)
				pairCount++;
		}
		
		if (m_nonMahjongByOriginalCall)
			scores.append(m_scheme.getScoreContribution(ScoreElement.OriginalCallHandScore));
		
		if (effectiveHandTiles == m_scheme.MahjongHandSize && pairCount == 1)
			m_isMahjong = true;
		else if (effectiveHandTiles >= m_scheme.MahjongHandSize)
		{
			m_isMahjong = false;
			// TODO: Re-enable this.
			//throw new InvalidHandException("Too many tiles for non-mahjong hand");
		}
		else
			m_isMahjong = false;
		
		if (m_isMahjong)
		{
			// Additional scoring that applies to mahjong hand only.
			scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongHandScore));
			
			// Look for all major and no chows
			
			boolean			allMajor				= true;
			boolean			noChow					= true;
			Set<Tile.Suit>	suits					= new HashSet<>();
			boolean			allNonPairsConcealed	= true;
			
			for (ScoredGroup group : this)
			{
				Tile firstTile = group.getFirstTile();
				
				if (group.getType() == Group.Type.CHOW)
				{
					noChow		= false;
					allMajor	= false;
				}
				
				if (!firstTile.isMajor())
					allMajor	= false;
				
				if (firstTile.getType() == Tile.Type.SUIT)
					suits.add(firstTile.getSuit());
				
				if (group.getType() != Group.Type.PAIR && !group.isConcealed())
					allNonPairsConcealed = false;
			}
			
			if (allMajor)
				scores.append(m_scheme.getScoreContribution(ScoreElement.AllMajorHandScore));
			
			if (noChow)
				scores.append(m_scheme.getScoreContribution(ScoreElement.NoChowsHandScore));
			
			if (suits.size() == 1)
				scores.append(m_scheme.getScoreContribution(ScoreElement.SingleSuitHandScore));
			
			if (allNonPairsConcealed)
				m_requirePairConcealedInfo = true;
			
			if (allNonPairsConcealed && m_mahjongPairConcealed)
				scores.append(m_scheme.getScoreContribution(ScoreElement.AllConcealedHandScore));
			
			if (m_mahjongByWallTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByWallTileHandScore));
			
			if (m_mahjongByLastWallTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByLastWallTileHandScore));
			
			if (m_mahjongByOnlyPossibleTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByOnlyPossibleTileHandScore));
			
			if (m_mahjongByLooseTile)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByLooseTileHandScore));
			
			if (m_mahjongByLastDiscard)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByLastDiscardHandScore));
			
			if (m_mahjongByRobbingKong)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByRobbingKongHandScore));

			if (m_mahjongByOriginalCall)
				scores.append(m_scheme.getScoreContribution(ScoreElement.MahjongByOriginalCallHandScore));
		}
		
		
		m_totalScoreUnlimited	= scores.getTotal();
		m_totalScoreLimited		= Math.min(m_totalScoreUnlimited, m_scheme.LimitScore);
		m_scores				= scores;
	}

	public ObjectNode toJson()
	{
		ObjectNode	hand		= JsonUtil.createObjectNode();
		ArrayNode	groups		= JsonUtil.createArrayNode();

		for (ScoredGroup group : this)
		{
			groups.add(group.toJson());
		}

		hand.set("groups", groups);

		if (m_mahjongPairConcealed)
			hand.put("mahjongPairConcealed", true);
		if (m_mahjongByLooseTile)
			hand.put("mahjongByLooseTile", true);
		if (m_mahjongByWallTile)
			hand.put("mahjongByWallTile", true);
		if (m_mahjongByLastWallTile)
			hand.put("mahjongByLastWallTile", true);
		if (m_mahjongByLastDiscard)
			hand.put("mahjongByLastDiscard", true);
		if (m_mahjongByRobbingKong)
			hand.put("mahjongByRobbingKong", true);
		if (m_mahjongByOnlyPossibleTile)
			hand.put("mahjongByOnlyPossibleTile", true);
		if (m_mahjongByOriginalCall)
			hand.put("mahjongByOriginalCall", true);
		if (m_nonMahjongByOriginalCall)
			hand.put("nonMahjongByOriginalCall", true);

		return hand;
	}

	static public ScoredHand fromJson(JsonNode hand, ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		ScoredHand scoredHand = new ScoredHand(scheme);

		scoredHand.setMahjongPairConcealed(hand.path("mahjongPairConcealed").asBoolean(false));
		scoredHand.setMahjongByLooseTile(hand.path("mahjongByLooseTile").asBoolean(false));
		scoredHand.setMahjongByWallTile(hand.path("mahjongByWallTile").asBoolean(false));
		scoredHand.setMahjongByLastWallTile(hand.path("mahjongByLastWallTile").asBoolean(false));
		scoredHand.setMahjongByLastDiscard(hand.path("mahjongByLastDiscard").asBoolean(false));
		scoredHand.setMahjongByRobbingKong(hand.path("mahjongByRobbingKong").asBoolean(false));
		scoredHand.setMahjongByOnlyPossibleTile(hand.path("mahjongByOnlyPossibleTile").asBoolean(false));
		scoredHand.setMahjongByOriginalCall(hand.path("mahjongByOriginalCall").asBoolean(false));
		scoredHand.setNonMahjongByOriginalCall(hand.path("nonMahjongByOriginalCall").asBoolean(false));

		ArrayNode groups = (ArrayNode)hand.get("groups");

		for (JsonNode groupJson : groups)
		{
			scoredHand.add(ScoredGroup.fromJson(groupJson, scheme, ownWind, prevailingWind));
		}

		return scoredHand;
	}
}

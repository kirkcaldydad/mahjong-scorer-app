package house.mcintosh.mahjong.scoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

public final class ScoredHand extends ArrayList<ScoredGroup> implements Serializable
{
	/**
	 * The standard mahjong hand multipliers that are fairly straightforward in that they
	 * can be applied to any mahjong hand (although some are incompatible with each other,
	 * but ignore that for now.
	 */
	public enum HandCompletedBy
	{
		MAHJONG_LOOSE_TILE(			true, ScoreElement.MahjongByLooseTileHandScore),
		MAHJONG_WALL_TILE(			true, ScoreElement.MahjongByWallTileHandScore),
		MAHJONG_LAST_WALL_TILE(		true, ScoreElement.MahjongByLastWallTileHandScore),
		MAHJONG_LAST_DISCARD(		true, ScoreElement.MahjongByLastDiscardHandScore),
		MAHJONG_ROBBING_KONG(		true, ScoreElement.MahjongByRobbingKongHandScore),
		MAHJONG_ONLY_POSSIBLE_TILE(	true, ScoreElement.MahjongByOnlyPossibleTileHandScore),
		MAHJONG_ORIGINAL_CALL(		true, ScoreElement.MahjongByOriginalCallHandScore),
		MAHJONG_PAIR_CONCEALED(		true, ScoreElement.AllConcealedHandScore),
		NON_MAHJONG_ORIGINAL_CALL(	false, ScoreElement.OriginalCallHandScore);

		public final ScoringScheme.ScoreElement scoreElement;
		public final boolean forMahjong;

		private HandCompletedBy(boolean forMahjong, ScoringScheme.ScoreElement scoreElement)
		{
			this.forMahjong = forMahjong;
			this.scoreElement = scoreElement;
		}
	}

	private final ScoringScheme					m_scheme;
	private final boolean						m_sort;
	
	private ScoreList							m_wholeHandScores;
	private ScoreList							m_groupScores;
	
	private boolean	m_requirePairConcealedInfo	= false;

	private boolean	m_isMahjong					= false;
	private int		m_totalScoreUnlimited		= 0;
	private int		m_totalScoreLimited			= 0;

	/** An array of booleans, indicating which of the HandCompletedBy values apply to this hand. */
	private Set<HandCompletedBy> m_handCompletedBy = new HashSet<>();

	private ScoredGroup m_latestAddition;
	
	public ScoredHand(ScoringScheme scheme)
	{
		this(scheme, true);
	}

	public ScoredHand(ScoringScheme scheme, boolean sort)
	{
		m_scheme = scheme;
		m_sort = sort;
	}

	@Override
	public boolean add(ScoredGroup group)
	{
		super.add(group);

		m_latestAddition = group;

		if (m_sort)
			Collections.sort(this, new GroupComparator());
		
		updateScore();

		return true;
	}

	@Override
	public ScoredGroup remove(int position)
	{
		ScoredGroup removedGroup = super.remove(position);

		if (removedGroup == m_latestAddition)
			m_latestAddition = null;

		updateScore();

		return removedGroup;
	}

	public void replaceLatestAddition(ScoredGroup group)
	{
		// Remove the latest addition, and add the new group instead of it.

		remove(m_latestAddition);
		add(group);
	}

	public int getLatestAdditionPosition()
	{
		if (m_latestAddition == null)
			return -1;

		return this.indexOf(m_latestAddition);
	}

	public ScoringScheme getScoringScheme()
	{
		return m_scheme;
	}

	public int getTotalScore()
	{
		return m_totalScoreLimited;
	}
	
	public int getTotalScoreUnlimited()
	{
		return m_totalScoreUnlimited;
	}

	public ScoreList getWholeHandScores()
	{
		return m_wholeHandScores;
	}

	public ScoreList getGroupScores()
	{
		return m_groupScores;
	}
	
	public boolean isMahjong()
	{
		return m_isMahjong;
	}

	/**
	 * @return	true if, as a result of the new information, tiles or groups were changed in
	 * 			a way that might require them to be redisplayed.
	 */
	public boolean setMahjongCompletedBy(HandCompletedBy completed, boolean value)
	{
		// This could be info about concealed state of the pair in a Mahjong hand.  If so,
		// treat it differently - update the tile group to record the information, then leave
		// updateScore to work out how this converts to a score multiplier.

		boolean tilesChanged = false;

		if (completed == HandCompletedBy.MAHJONG_PAIR_CONCEALED)
		{
			// Find the pair group and replace if necessary.

			int pairIndex;

			for (pairIndex = 0 ; pairIndex < this.size() ; pairIndex++)
			{
				if (this.get(pairIndex).getType() == Group.Type.PAIR)
					break;
			}

			if (pairIndex < this.size())
			{
				if (this.get(pairIndex).isConcealed() != value)
				{
					ScoredGroup oldPair = super.remove(pairIndex);
					this.add(pairIndex, oldPair.toggleVisibility());
					tilesChanged = true;
				}
			}
		}
		else if (value)
			m_handCompletedBy.add(completed);
		else
			m_handCompletedBy.remove(completed);

		updateScore();

		return tilesChanged;
	}

	public boolean isMahjongCompletedBy(HandCompletedBy completed)
	{
		return m_handCompletedBy.contains(completed);
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

	/**
	 * Indicates whether the pair in this hand is concealed.  To be used in conjunction with
	 * requiresPairConcealedInfo().  Only applicable for a mahjong hand.  Result is undefined
	 * for non-mahjong hands.
	 */
	public boolean isPairConcealed()
	{
		for (ScoredGroup group : this)
		{
			if (group.getType() == Group.Type.PAIR)
				return group.isConcealed();
		}

		return false;
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
		
		ScoreList wholeHandScores = new ScoreList();
		ScoreList groupScores = new ScoreList();
		
		int effectiveHandTiles	= 0;
		int pairCount			= 0;
		
		for (ScoredGroup group : this)
		{
			groupScores.append(group.getScore());
			
			effectiveHandTiles += group.getType().getHandSize();
			
			if (group.getType() == Group.Type.PAIR)
				pairCount++;
		}
		
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

		m_requirePairConcealedInfo = false;
		
		if (m_isMahjong)
		{
			// Additional scoring that applies to mahjong hand only.
			wholeHandScores.append(m_scheme.getScoreContribution(ScoreElement.MahjongHandScore));
			
			// Look for all major and no chows
			
			boolean			allMajor				= true;
			boolean			noChow					= true;
			Set<Tile.Suit>	suits					= new HashSet<>();
			boolean			allGroupsConcealed		= true;
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

				if (!group.isConcealed())
				{
					allGroupsConcealed = false;

					if (group.getType() != Group.Type.PAIR)
						allNonPairsConcealed = false;
				}
			}

			if (allMajor)
				wholeHandScores.append(m_scheme.getScoreContribution(ScoreElement.AllMajorHandScore));
			
			if (noChow)
				wholeHandScores.append(m_scheme.getScoreContribution(ScoreElement.NoChowsHandScore));
			
			if (suits.size() == 1)
				wholeHandScores.append(m_scheme.getScoreContribution(ScoreElement.SingleSuitHandScore));

			if (allGroupsConcealed)
				wholeHandScores.append(m_scheme.getScoreContribution(ScoreElement.AllConcealedHandScore));
			
			if (allNonPairsConcealed)
				m_requirePairConcealedInfo = true;

			// Add in mahjong only hand completion scores - these should only be set for a mahjong
			// hand, but do it inside here because we don't really trust the UI code that's calling us.

			for (HandCompletedBy completedBy : m_handCompletedBy)
			{
				if (completedBy.forMahjong)
				{
					wholeHandScores.append(m_scheme.getScoreContribution(completedBy.scoreElement));
				}
			}
		}
		else
		{
			// Non-Mahjong hand.  Add in the completion scores that apply to non-mahjong hands.

			for (HandCompletedBy completedBy : m_handCompletedBy)
			{
				if (!completedBy.forMahjong)
				{
					wholeHandScores.append(m_scheme.getScoreContribution(completedBy.scoreElement));
				}
			}
		}



		m_totalScoreUnlimited	= new ScoreList().append(groupScores).append(wholeHandScores).getTotal();
		m_totalScoreLimited		= Math.min(m_totalScoreUnlimited, m_scheme.LimitScore);
		m_wholeHandScores		= wholeHandScores;
		m_groupScores			= groupScores;
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

		ArrayNode completedBy = JsonUtil.createArrayNode();

		for (HandCompletedBy by : m_handCompletedBy)
		{
			completedBy.add(by.name());
		}

		if (completedBy.size() > 0)
			hand.set("completedBy", completedBy);

		return hand;
	}

	static public ScoredHand fromJson(JsonNode hand, ScoringScheme scheme, Wind ownWind, Wind prevailingWind)
	{
		ScoredHand scoredHand = new ScoredHand(scheme);

		JsonNode completedByNode = hand.path("completedBy");

		if (!completedByNode.isMissingNode())
		{
			for (JsonNode byNode : ((ArrayNode)completedByNode))
			{
				String by = byNode.asText("");
				HandCompletedBy completedBy = HandCompletedBy.valueOf(by);

				if (completedBy != null)
					scoredHand.m_handCompletedBy.add(completedBy);
			}
		}

		ArrayNode groups = (ArrayNode)hand.get("groups");

		for (JsonNode groupJson : groups)
		{
			scoredHand.add(ScoredGroup.fromJson(groupJson, scheme, ownWind, prevailingWind));
		}

		return scoredHand;
	}

	/**
	 * @return	The maximum number of tiles that can be added to this hand, excluding the extra
	 * 			tile that comes with each Kong.
	 */
	public int getAvailableTileCapacity()
	{
		int tileCount = 0;
		int pairCount = 0;

		for (Group group : this)
		{
			Group.Type type = group.getType();

			tileCount += type.getHandSize();

			if (type == Group.Type.PAIR)
				pairCount++;
		}

		if (pairCount > 1)
			// Cannot become a Mahjong hand.
			return Math.max(0, m_scheme.MahjongHandSize - 1 - tileCount);

		// else pairCount <= 1, so could become a mahjong hand.
		return Math.max(0, m_scheme.MahjongHandSize - tileCount);
	}
}

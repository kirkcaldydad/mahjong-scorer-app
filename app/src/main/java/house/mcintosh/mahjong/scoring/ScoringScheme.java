package house.mcintosh.mahjong.scoring;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import house.mcintosh.mahjong.ui.R;
import house.mcintosh.mahjong.util.JsonUtil;

public final class ScoringScheme implements Serializable
{
	public enum ScoreElement
	{
		// Group scores

		PairSuitScore,
		PairWindScore,
		PairOwnWindScore,
		PairPrevailingWindScore,
		PairDragonScore,
		
		ChowSuitScore,
		
		PungExposedMinorSuitScore,
		PungExposedMajorSuitScore,
		PungConcealedMinorSuitScore,
		PungConcealedMajorSuitScore,
		
		PungExposedPrevailingOwnWindScore,
		PungExposedOwnWindScore,
		PungExposedPrevailingWindScore,
		PungExposedWindScore,
		PungConcealedPrevailingOwnWindScore,
		PungConcealedOwnWindScore,
		PungConcealedPrevailingWindScore,
		PungConcealedWindScore,
		
		PungOwnWindMultiplier,
		PungPrevailingWindMultiplier,
		
		PungExposedDragonScore,
		PungConcealedDragonScore,
		
		KongExposedMinorSuitScore,
		KongExposedMajorSuitScore,
		KongConcealedMinorSuitScore,
		KongConcealedMajorSuitScore,
		
		KongExposedPrevailingOwnWindScore,
		KongExposedOwnWindScore,
		KongExposedPrevailingWindScore,
		KongExposedWindScore,
		KongConcealedPrevailingOwnWindScore,
		KongConcealedOwnWindScore,
		KongConcealedPrevailingWindScore,
		KongConcealedWindScore,
		
		KongOwnWindMultiplier,
		KongPrevailingWindMultiplier,
		
		KongExposedDragonScore,
		KongConcealedDragonScore,

		// Whole hand scores
		
//		OwnFlowerHandScore		 ,
//		OwnSeasonHandScore		 ,
//		AllFlowersHandScore,
//		AllSeasonsHandScore		 ,
		OriginalCallHandScore(				R.string.scoreDescriptionOriginalCallHandScore),

		// Mahjong hand scores
		MahjongHandScore(					R.string.scoreDescriptionMahjongHandScore),
		MahjongByNoScoreHandScore(			R.string.scoreDescriptionMahjongByNoScoreHandScore),
		NoChowsHandScore(					R.string.scoreDescriptionNoChowsHandScore),
		SingleSuitHandScore(				R.string.scoreDescriptionSingleSuitHandScore),
		AllMajorHandScore(					R.string.scoreDescriptionAllMajorHandScore),
		AllConcealedHandScore(				R.string.scoreDescriptionAllConcealedHandScore),
		MahjongByLooseTileHandScore(		R.string.scoreDescriptionMahjongByLooseTileHandScore),
		MahjongByOnlyPossibleTileHandScore(	R.string.scoreDescriptionMahjongByOnlyPossibleTileHandScore),
		MahjongByWallTileHandScore(			R.string.scoreDescriptionMahjongByWallTileHandScore),
		MahjongByLastWallTileHandScore(		R.string.scoreDescriptionMahjongByLastWallTileHandScore),
		MahjongByLastDiscardHandScore(		R.string.scoreDescriptionMahjongByLastDiscardHandScore),
		MahjongByRobbingKongHandScore(		R.string.scoreDescriptionMahjongByRobbingKongHandScore),
		MahjongByOriginalCallHandScore(		R.string.scoreDescriptionMahjongByOriginalCallHandScore);

		private final int descriptionId;

		ScoreElement()
		{
			this.descriptionId = 0;
		}

		ScoreElement(int descriptionId)
		{
			this.descriptionId = descriptionId;
		}

		public boolean hasDescription()
		{
			return descriptionId != 0;
		}

		/**
		 * Get a text description of the ScoreElement if it has one.
		 *
		 * @param context	A context from which to read the string.
		 *
		 * @return A string description, or null if the ScoreElement has no description.
		 */
		public String getDescription(Context context)
		{
			if (descriptionId == 0)
				return null;

			return context.getString(descriptionId);
		}
	}
	
	private Map<ScoreElement, ScoreList> m_contributions = new HashMap<>();

	public int MahjongHandSize	= 14;
	public int LimitScore		= 1000;
	public int InitialScore		= 2000;

	private int m_resourceId;
	private String m_fileName;
	private String m_displayName;


	public ScoreList getScoreContribution(ScoreElement element)
	{
		return m_contributions.get(element);
	}

	public String getDisplayName()
	{
		return m_displayName;
	}

	public static String getDisplayName(Context context, int resourceId) throws IOException
	{
		try (InputStream inStream = context.getResources().openRawResource(resourceId))
		{
			ObjectNode node = (ObjectNode) JsonUtil.load(inStream);

			return node.path("name").asText("");
		}
	}

	/**
	 * @return	true if a score element has some score or a multiplier.  False if it has
	 * 			nothing that will affect the score of a group or hand.
	 */
	public boolean hasScore(ScoreElement element)
	{
		for (ScoreContribution contribution : getScoreContribution(element))
		{
			if (contribution.hasScore())
				return true;
		}

		return false;
	}
	
	// Set scores
	
	private ScoreList addScoreContribution(ScoreContribution contribution)
	{
		ScoreList list = new ScoreList();
		
		list.append(contribution);
		m_contributions.put(contribution.getElement(), list);
		
		return list;
	}

	public static ScoringScheme load(Context context, int resourceId) throws IOException
	{
		try (InputStream inStream = context.getResources().openRawResource(resourceId))
		{

			ObjectNode node = (ObjectNode) JsonUtil.load(inStream);

			ScoringScheme scheme = fromJson(node);

			scheme.m_resourceId = resourceId;

			return scheme;
		}
	}

	public static ScoringScheme fromJson(InputStream inStream, String fileName) throws IOException
	{
		ObjectNode node = (ObjectNode) JsonUtil.load(inStream);

		ScoringScheme scheme = fromJson(node);

		scheme.m_fileName = fileName;

		return scheme;
	}
	
	private static ScoringScheme fromJson(ObjectNode node)
	{
		ScoringScheme scheme = new ScoringScheme();

		scheme.MahjongHandSize	= node.get("mahjongHandSize").asInt(14);
		scheme.LimitScore		= node.get("limitScore").asInt(1000);
		scheme.InitialScore		= node.get("initialScore").asInt(2000);
		scheme.m_displayName	= node.get("name").asText("");
		
		// Load the contributions into a map from where they can be organised into ScoreLists.
		
		Map<ScoreElement, ScoreContribution> contributions = new HashMap<>();
		
		ArrayNode contributionsArray =(ArrayNode) node.get("contributions");
		
		for (JsonNode contributionNode : contributionsArray)
		{
			ScoreContribution contribution = ScoreContribution.fromJson(contributionNode);
			
			contributions.put(contribution.getElement(), contribution);
		}
		
		scheme.addScoreContribution(contributions.get(ScoreElement.PairSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PairWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PairOwnWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PairPrevailingWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PairDragonScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.ChowSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedMinorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedMajorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedMinorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedMajorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedPrevailingOwnWindScore))
			.append(contributions.get(ScoreElement.PungOwnWindMultiplier))
			.append(contributions.get(ScoreElement.PungPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedOwnWindScore))
			.append(contributions.get(ScoreElement.PungOwnWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedPrevailingWindScore))
			.append(contributions.get(ScoreElement.PungPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedPrevailingOwnWindScore))
			.append(contributions.get(ScoreElement.PungOwnWindMultiplier))
			.append(contributions.get(ScoreElement.PungPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedOwnWindScore))
			.append(contributions.get(ScoreElement.PungOwnWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedPrevailingWindScore))
			.append(contributions.get(ScoreElement.PungPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungExposedDragonScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.PungConcealedDragonScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedMinorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedMajorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedMinorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedMajorSuitScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedPrevailingOwnWindScore))
			.append(contributions.get(ScoreElement.KongOwnWindMultiplier))
			.append(contributions.get(ScoreElement.KongPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedOwnWindScore))
			.append(contributions.get(ScoreElement.KongOwnWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedPrevailingWindScore))
			.append(contributions.get(ScoreElement.KongPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedPrevailingOwnWindScore))
			.append(contributions.get(ScoreElement.KongOwnWindMultiplier))
			.append(contributions.get(ScoreElement.KongPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedOwnWindScore))
			.append(contributions.get(ScoreElement.KongOwnWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedPrevailingWindScore))
			.append(contributions.get(ScoreElement.KongPrevailingWindMultiplier));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedWindScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongExposedDragonScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.KongConcealedDragonScore));
	//	scheme.addScoreContribution(contributions.get(ScoreElement.OwnFlowerHandScore));
	//	scheme.addScoreContribution(contributions.get(ScoreElement.OwnSeasonHandScore));
	//	scheme.addScoreContribution(contributions.get(ScoreElement.AllFlowersHandScore));
	//	scheme.addScoreContribution(contributions.get(ScoreElement.AllSeasonsHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.OriginalCallHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByNoScoreHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.NoChowsHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.SingleSuitHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.AllMajorHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.AllConcealedHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByLooseTileHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByOnlyPossibleTileHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByWallTileHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByLastWallTileHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByLastDiscardHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByRobbingKongHandScore));
		scheme.addScoreContribution(contributions.get(ScoreElement.MahjongByOriginalCallHandScore));

		return scheme;
	}

	/**
	 * @return  A Json structure from which this ScoringScheme can be loaded.
	 */
	public ObjectNode getIdJson()
	{
		ObjectNode node = JsonUtil.createObjectNode();

		if (m_resourceId != 0)
			node.put("resourceId", m_resourceId);

		if (m_fileName != null)
			node.put("fileName", m_fileName);

		return node;
	}

	/**
	 * Parse the resourceId of the resource that holds the detail of a scoring scheme
	 * from a schemeId as returned by getIdJson().
	 *
	 * @return	The resourceId, or 0 if there is no resource Id available.
	 */
	static public int getResourceId(ObjectNode schemeId)
	{
		return schemeId.path("resourceId").asInt(0);
	}

	/**
	 * Parse the name of a file that holds the detail of a scoring scheme
	 * from a schemeId as returned by getIdJson().
	 *
	 * @return	The filename, or null if there is no filename available.
	 */
	static public String getFileName(ObjectNode schemeId)
	{
		return schemeId.path("fileName").asText(null);
	}

	public ObjectNode toJson()
	{
		ObjectNode scheme = JsonUtil.createObjectNode();

		// Temporary - only required to export current score scheme.

		scheme.put("mahjongHandSize", MahjongHandSize);
		scheme.put("limitScore", LimitScore);
		scheme.put("initialScore", InitialScore);

		Map<ScoreElement, ScoreContribution> allContributions = new HashMap<>();

		for (ScoreList scoreList : m_contributions.values())
		{
			for (ScoreContribution contribution : scoreList)
			{
				ScoreContribution prevContribution = allContributions.get(contribution.getElement());

				if (prevContribution != null &&
						(prevContribution.getScore() != contribution.getScore() || prevContribution.getHandMultiplier() != contribution.getHandMultiplier()))
				{
					System.err.println("mismatch for contribution " + contribution.getElement());
				}

				allContributions.put(contribution.getElement(), contribution);
			}
		}

		ArrayNode contributionsArray = scheme.withArray("contributions");

		Set<ScoreElement> unusedElements = new HashSet<>();
		unusedElements.addAll(Arrays.asList(ScoreElement.values()));

		for (ScoreElement element : ScoreElement.values())
		{
			ScoreContribution contribution = allContributions.get(element);

			if (contribution != null)
			{
				contributionsArray.add(contribution.toJson());
				unusedElements.remove(element);
			}
		}

		if (unusedElements.size() > 0)
			System.err.println("Unused score elements: " + unusedElements);

		return scheme;
	}
}


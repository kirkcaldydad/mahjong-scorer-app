package house.mcintosh.mahjong.scoring;

import java.util.HashMap;
import java.util.Map;

public class ScoringScheme
{
	enum ScoreElement
	{
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
		
//		OwnFlowerHandScore		 ,
//		OwnSeasonHandScore		 ,
//		AllFlowersHandScore,
//		AllSeasonsHandScore		 ,
		OriginalCallHandScore		 ,
	
		// Mahjong hand scores
		MahjongHandScore,
		NoChowsHandScore,
		SingleSuitHandScore,
		AllMajorHandScore,
		AllConcealedHandScore,
		MahjongByLooseTileHandScore,
		MahjongByOnlyPossibleTileHandScore,
		MahjongByWallTileHandScore,
		MahjongByLastWallTileHandScore,
		MahjongByLastDiscardHandScore,
		MahjongByRobbingKongHandScore,
		MahjongByOriginalCallHandScore,
		
		UNKNOWN
	}
	
	static private ScoringScheme s_instance = new ScoringScheme();
	
	private Map<ScoreElement, ScoreList> m_contributions = new HashMap<>();
	
	/**
	 * Private constructor for creating an instance.
	 */
	private ScoringScheme()
	{
		load();
	}
	
	public static ScoringScheme instance()
	{
		return s_instance;
	}
	
	public ScoreList getScoreContribution(ScoreElement element)
	{
		return m_contributions.get(element);
	}
	
	// Based on scores from http://mahjongbritishrules.com/scoring/overview.html
	
	public int MahjongHandSize	= 14;
	public int LimitScore		= 1000;
	public int InitialScore		= 2000;
	
	// Set scores
	
	private ScoreList addScoreContribution(ScoreContribution contribution)
	{
		ScoreList list = new ScoreList();
		
		list.append(contribution);
		m_contributions.put(contribution.getElement(), list);
		
		return list;
	}
	
	private void load()
	{
		addScoreContribution(new ScoreContribution(ScoreElement.PairSuitScore, 0, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PairWindScore, 0, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PairOwnWindScore, 2, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PairPrevailingWindScore, 2, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PairDragonScore, 2, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.ChowSuitScore, 0, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedMinorSuitScore, 2, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedMajorSuitScore, 4, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedMinorSuitScore, 4, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedMajorSuitScore, 8, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedPrevailingOwnWindScore, 4, 1))
			.append(new ScoreContribution(ScoreElement.PungOwnWindMultiplier, 0, 2))
			.append(new ScoreContribution(ScoreElement.PungPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedOwnWindScore, 4, 1))
			.append(new ScoreContribution(ScoreElement.PungOwnWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedPrevailingWindScore, 4, 1))
			.append(new ScoreContribution(ScoreElement.PungPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedWindScore, 4, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedPrevailingOwnWindScore, 8, 1))
			.append(new ScoreContribution(ScoreElement.PungOwnWindMultiplier, 0, 2))
			.append(new ScoreContribution(ScoreElement.PungPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedOwnWindScore, 8, 1))
			.append(new ScoreContribution(ScoreElement.PungOwnWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedPrevailingWindScore, 8, 1))
			.append(new ScoreContribution(ScoreElement.PungPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedWindScore, 8, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.PungExposedDragonScore, 4, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.PungConcealedDragonScore, 8, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedMinorSuitScore, 8, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedMajorSuitScore, 16, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedMinorSuitScore, 16, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedMajorSuitScore, 32, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedPrevailingOwnWindScore, 16, 1))
			.append(new ScoreContribution(ScoreElement.KongOwnWindMultiplier, 0, 2))
			.append(new ScoreContribution(ScoreElement.KongPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedOwnWindScore, 16, 1))
			.append(new ScoreContribution(ScoreElement.KongOwnWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedPrevailingWindScore, 16, 1))
			.append(new ScoreContribution(ScoreElement.KongPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedWindScore, 16, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedPrevailingOwnWindScore, 32, 1))
			.append(new ScoreContribution(ScoreElement.KongOwnWindMultiplier, 0, 2))
			.append(new ScoreContribution(ScoreElement.KongPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedOwnWindScore, 32, 1))
			.append(new ScoreContribution(ScoreElement.KongOwnWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedPrevailingWindScore, 32, 1))
			.append(new ScoreContribution(ScoreElement.KongPrevailingWindMultiplier, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedWindScore, 32, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.KongExposedDragonScore, 16, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.KongConcealedDragonScore, 32, 2));
	//	addScoreContribution(new ScoreContribution(ScoreElement.OwnFlowerHandScore, 0, 2));
	//	addScoreContribution(new ScoreContribution(ScoreElement.OwnSeasonHandScore, 0, 2));
	//	addScoreContribution(new ScoreContribution(ScoreElement.AllFlowersHandScore, 0, 2));
	//	addScoreContribution(new ScoreContribution(ScoreElement.AllSeasonsHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.OriginalCallHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongHandScore, 10, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.NoChowsHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.SingleSuitHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.AllMajorHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.AllConcealedHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByLooseTileHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByOnlyPossibleTileHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByWallTileHandScore, 2, 1));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByLastWallTileHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByLastDiscardHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByRobbingKongHandScore, 0, 2));
		addScoreContribution(new ScoreContribution(ScoreElement.MahjongByOriginalCallHandScore, 0, 2));
	}
}


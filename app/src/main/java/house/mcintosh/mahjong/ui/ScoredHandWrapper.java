package house.mcintosh.mahjong.ui;

import java.io.Serializable;

import house.mcintosh.mahjong.scoring.ScoredHand;

/**
 * A thin wrapper for ScoredHand to use in intents, because Intent serialisation of a class
 * that extends an ArrayList causes grief.
 */

public class ScoredHandWrapper implements Serializable
{
	public static ScoredHand m_hand;

	public ScoredHandWrapper(ScoredHand hand)
	{
		m_hand = hand;
	}

	public ScoredHand getHand()
	{
		return m_hand;
	}
}

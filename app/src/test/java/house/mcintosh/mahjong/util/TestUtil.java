package house.mcintosh.mahjong.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import house.mcintosh.mahjong.scoring.ScoringScheme;

public class TestUtil
{
	public static ScoringScheme loadDefaultScoringScheme() throws IOException
	{
		String scoringSchemeFile = "res/raw/scoring_scheme_british.json";
		InputStream inStream = TestUtil.class.getClassLoader().getResourceAsStream(scoringSchemeFile);
		return ScoringScheme.fromJson(inStream, scoringSchemeFile);
	}
}

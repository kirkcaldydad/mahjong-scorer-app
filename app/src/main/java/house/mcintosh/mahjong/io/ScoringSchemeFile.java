package house.mcintosh.mahjong.io;

import android.content.Context;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

import house.mcintosh.mahjong.exception.MissingScoringSchemeException;
import house.mcintosh.mahjong.scoring.ScoringScheme;

/**
 * Loader (Saver) for scoring schemes.
 */

public class ScoringSchemeFile
{
	public static ScoringScheme load(Context context, ObjectNode schemeId) throws IOException
	{
		if (schemeId == null)
			throw new MissingScoringSchemeException("Invalid schemeId");

		int resourceId = ScoringScheme.getResourceId(schemeId);

		if (resourceId != 0)
			return ScoringScheme.load(context, resourceId);

		// TODO: Handle loading from a file.
		throw new MissingScoringSchemeException("Loading scoring scheme from file is not yet implemented.");
	}
}

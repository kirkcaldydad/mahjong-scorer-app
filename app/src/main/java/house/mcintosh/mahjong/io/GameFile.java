package house.mcintosh.mahjong.io;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import house.mcintosh.mahjong.exception.LoadException;
import house.mcintosh.mahjong.exception.MissingScoringSchemeException;
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.GameSummary;
import house.mcintosh.mahjong.scoring.ScoringScheme;
import house.mcintosh.mahjong.ui.R;
import house.mcintosh.mahjong.util.JsonUtil;

/**
 * Handler for loading and saving games in files.  Each instance consists of a Game and knowledge
 * of the file in which it is stored.
 */

public class GameFile
{
	private final static String LOG_TAG = GameFile.class.getName();

	private final Game m_game;
	private final File m_file;

	private static final FilenameFilter GAME_FILENAME_FILTER =
			new FilenameFilter()
			{
				private final Pattern FILE_PATTERN = Pattern.compile("^game[0-9]{17}\\.json$");

				@Override
				public boolean accept(File dir, String name)
				{
					Matcher matcher = FILE_PATTERN.matcher(name);
					return matcher.matches();
				}
			};

	public GameFile(Context context, Game game)
	{
		m_game = game;

		// Create a new abstract File in which to store the game.

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		String nowStr = dateFormat.format(new Date());

		String filename = "game" + nowStr + ".json";

		File directory = context.getExternalFilesDir(null);

		m_file = new File(directory, filename);
	}

	private GameFile(Game game, File file)
	{
		m_game = game;
		m_file = file;
	}

	public void save()
	{
		File parentDirectory = m_file.getParentFile();

		if (!parentDirectory.exists() && !parentDirectory.mkdirs())
			Log.e(LOG_TAG, "Directory not created: " + m_file.getParentFile().getName());

		m_game.getMeta().setLastModifiedOnToNow();

		try
		{
			JsonUtil.writeFile(m_game.toJson(), m_file);
		}
		catch (IOException ioe)
		{
			Log.e(LOG_TAG, "Cannot write file: " + m_file.getAbsolutePath() + " " + ioe.getMessage());

			// TODO: display error message.
			return;
		}
	}

	public static GameFile load(Context context, File file)
	{
		GameFile gameFile;

		try
		{
			JsonNode gameNode = JsonUtil.load(file);

			ObjectNode scoringSchemeIdNode = Game.getScoringSchemeId(gameNode);

			ScoringScheme scheme;

			try
			{
				scheme = ScoringSchemeFile.load(context, scoringSchemeIdNode);
			}
			catch (MissingScoringSchemeException msse)
			{
				Log.e(LOG_TAG, "No scoring scheme available:" + msse);
				Log.i(LOG_TAG, "Using British Scoring Scheme");
				scheme = ScoringScheme.load(context, R.raw.scoring_scheme_british);
			}

			Game game = Game.fromJson(gameNode, scheme);

			gameFile = new GameFile(game, file);
		}
		catch (IOException ioe)
		{
			throw new LoadException("Cannot load game file : " + file.getAbsolutePath());
		}

		return gameFile;
	}

	public void delete()
	{
		boolean deleted = m_file.delete();

		if (!deleted)
		{
			// Shouldn't ever happen, but doesn't really matter.
			Log.e(LOG_TAG, "Cannot delete game file:" + m_file.getAbsolutePath());
		}
	}

	public File getFile()
	{
		return m_file;
	}

	public Game getGame()
	{
		return m_game;
	}

	static public List<GameSummary> getAllGames(Context context)
	{
		List<GameSummary> allGames = new ArrayList<>();
		File directory = context.getExternalFilesDir(null);

		if (!directory.isDirectory())
		{
			Log.e(LOG_TAG, "Cannot load game files - no directory.");
			return allGames;
		}

		File[] files = directory.listFiles(GAME_FILENAME_FILTER);

		for (File file : files)
		{
			GameSummary summary = loadGameSummary(file);

			if (summary != null)
				allGames.add(summary);
		}

		return allGames;
	}

	public static GameSummary loadGameSummary(File gameFile)
	{
		GameSummary summary;

		try
		{
			ObjectNode gameNode = (ObjectNode)JsonUtil.load(gameFile);
			summary = GameSummary.fromJson(gameNode, gameFile);
		}
		catch (IOException ioe)
		{
			Log.e(LOG_TAG, "Cannot load game file: " + gameFile.getAbsolutePath());
			summary = null;
		}

		return summary;
	}
}
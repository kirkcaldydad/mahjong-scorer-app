package house.mcintosh.mahjong.io;

import android.content.Context;
import android.util.Log;

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
import house.mcintosh.mahjong.model.Game;
import house.mcintosh.mahjong.model.GameSummary;
import house.mcintosh.mahjong.ui.CreateGameActivity;
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
	private final String m_filename;

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

		// Save the game to a file.

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		String nowStr = dateFormat.format(new Date());

		m_filename = "game" + nowStr + ".json";

		File directory = context.getExternalFilesDir(null);

		m_file = new File(directory, m_filename);
	}

	public void save()
	{
		File parentDirectory = m_file.getParentFile();

		if (!parentDirectory.exists() && !parentDirectory.mkdirs())
			Log.e(LOG_TAG, "Directory not created: " + m_file.getParentFile().getName());

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

	public String getFilename()
	{
		return m_filename;
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

	static private GameSummary loadGameSummary(File gameFile)
	{
		GameSummary summary;

		try
		{
			ObjectNode gameNode = (ObjectNode)JsonUtil.loadFile(gameFile);
			summary = GameSummary.fromJson(gameNode);
		}
		catch (IOException ioe)
		{
			Log.e(LOG_TAG, "Cannot load game file: " + gameFile.getAbsolutePath());
			summary = null;
		}

		return summary;
	}

	static public GameSummary getGameSummary(Context context, String filename)
	{
		File directory = context.getExternalFilesDir(null);

		if (!directory.isDirectory())
			throw new LoadException("Cannot load game file - no directory.");

		File gameFile = new File(directory, filename);

		GameSummary summary = loadGameSummary(gameFile);

		return summary;
	}
}
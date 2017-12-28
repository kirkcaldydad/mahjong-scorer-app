package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.Random;

/**
 * A unique Id to identify a player.  Each player in each game is independently
 * identified by a PlayerId.
 */
public class PlayerId implements Serializable
{
	private final String value;

	static private Random random = new Random();

	static private Character[] hexChars = new Character[]
			{
					'0', '1', '2', '3',
					'4', '5', '6', '7',
					'8', '9', 'a', 'b',
					'c', 'd', 'e', 'f'
			};

	PlayerId(String value)
	{
		this.value = value;
	}

	public PlayerId(JsonNode idNode)
	{
		value = idNode.asText();
	}

	static public PlayerId create()
	{
		byte[] bytes = new byte[16];

		random.nextBytes(bytes);

		// Convert the bytes to hex.

		StringBuilder sb = new StringBuilder();

		for (byte cur : bytes)
		{
			sb.append(hexChars[(cur & 0xF0) >> 4]);
			sb.append(hexChars[(cur & 0x0F)]);
		}

		return new PlayerId(sb.toString());
	}

	String getValue()
	{
		return this.value;
	}

	@Override
	public String toString()
	{
		return this.value;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PlayerId))
			return false;

		return ((PlayerId)obj).value.equals(this.value);
	}
}

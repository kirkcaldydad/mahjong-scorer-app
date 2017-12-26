package house.mcintosh.mahjong.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import house.mcintosh.mahjong.exception.InvalidModelException;

public class TimeUtil
{
	private static final String UTC_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

	public static String toUTCString(Date date)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat(UTC_DATE_PATTERN);
		dateFormatter.setTimeZone(UTC_TIMEZONE);
		String strUTCDate = dateFormatter.format(date);
		return strUTCDate;
	}

	public static String getUTCNow()
	{
		return toUTCString(new Date());
	}

	public static Date fromUTCString(String dateStr)
	{
		try
		{
			SimpleDateFormat dateFormatter = new SimpleDateFormat(UTC_DATE_PATTERN);
			dateFormatter.setTimeZone(UTC_TIMEZONE);
			Date date = dateFormatter.parse(dateStr);

			return date;
		}
		catch (ParseException pe)
		{
			throw new InvalidModelException("Cannot parse date.");
		}
	}
}


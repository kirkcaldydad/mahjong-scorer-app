package house.mcintosh.mahjong.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;

import house.mcintosh.mahjong.util.JsonUtil;
import house.mcintosh.mahjong.util.TimeUtil;

public final class GameMeta
{
	private final Date m_createdOn;
	private Date m_lastModifiedOn;

	public GameMeta()
	{
		m_createdOn = m_lastModifiedOn = new Date();
	}

	public GameMeta(Date createdOn)
	{
		m_createdOn = createdOn;
		m_lastModifiedOn = createdOn;
	}

	public Date getCreatedOn()
	{
		return m_createdOn;
	}

	public Date getLastModifiedOn()
	{
		return m_lastModifiedOn;
	}

	public void setLastModifiedOnToNow()
	{
		this.m_lastModifiedOn = new Date();
	}

	public ObjectNode toJson()
	{
		ObjectNode meta = JsonUtil.createObjectNode();

		meta.put("createdOn", TimeUtil.toUTCString(m_createdOn));
		meta.put("lastModifiedOn", TimeUtil.toUTCString(m_lastModifiedOn));

		return meta;
	}

	public static GameMeta fromJson(JsonNode metaNode)
	{
		Date createdOn = TimeUtil.fromUTCString(metaNode.path("createdOn").asText());
		Date lastModifiedOn = TimeUtil.fromUTCString(metaNode.path("lastModifiedOn").asText());

		GameMeta meta = new GameMeta(createdOn);

		meta.m_lastModifiedOn = lastModifiedOn;

		return meta;
	}
}

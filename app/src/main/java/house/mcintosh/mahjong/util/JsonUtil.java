package house.mcintosh.mahjong.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;

public class JsonUtil
{
	static private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static public ObjectNode createObjectNode()
	{
		return OBJECT_MAPPER.getNodeFactory().objectNode();
	}

	static public ArrayNode createArrayNode()
	{
		return OBJECT_MAPPER.getNodeFactory().arrayNode();
	}
}

package house.mcintosh.mahjong.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class JsonUtil
{
	static private ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static private ObjectMapper PRETTY_OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

	static public ObjectNode createObjectNode()
	{
		return OBJECT_MAPPER.getNodeFactory().objectNode();
	}

	static public ArrayNode createArrayNode()
	{
		return OBJECT_MAPPER.getNodeFactory().arrayNode();
	}

	static public void writeFile(JsonNode node, File file) throws IOException
	{
		PRETTY_OBJECT_MAPPER.writeValue(file, node);
	}

	static public JsonNode loadFile(File file) throws IOException
	{
		return OBJECT_MAPPER.readTree(file);
	}
}

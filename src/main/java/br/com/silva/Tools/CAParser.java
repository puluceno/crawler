package br.com.silva.Tools;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.pmw.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import br.com.silva.model.CA;

public class CAParser {
	private static Gson gson = new Gson();
	private static JsonParser parser = new JsonParser();

	/**
	 * 
	 * @param caObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Document toDocument(CA caObj) {
		String json = gson.toJson(caObj);
		DBObject parse = (DBObject) JSON.parse(json);
		return new Document(parse.toMap());
	}

	/**
	 * 
	 * @param document
	 * @return
	 */
	public static CA toObject(Document document) {
		if (document != null) {
			try {
				JsonObject obj = parser.parse(document.toJson()).getAsJsonObject();
				return (new Gson()).fromJson(obj, CA.class);
			} catch (Exception e) {
				Logger.error("Failed to parse CA with number {}", document.get("number"));
				Logger.trace(e);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param ca
	 * @return
	 */
	public static Object toJson(Object ca) {
		if (ca instanceof CA)
			return gson.toJson(ca);
		if (ca instanceof Document)
			return ((Document) ca).toJson();
		if (ca instanceof ArrayList<?>) {
			List<String> caList = new ArrayList<String>();
			((ArrayList<?>) ca).forEach(document -> caList.add((String) toJson(document)));
			return caList;
		}

		return ca == null ? "" : ca.toString();
	}
}

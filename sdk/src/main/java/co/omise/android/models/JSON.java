package co.omise.android.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class JSON {
    private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

    public static String string(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getString(key) : null;
    }

    public static boolean bool(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getBoolean(key) : false;
    }

    public static int integer(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getInt(key) : 0;
    }

    public static long long_(JSONObject json, String key) throws JSONException {
        return json.has(key) ? json.getLong(key) : 0;
    }

    public static DateTime dateTime(JSONObject json, String key) throws JSONException {
        return json.has(key) ?
                DateTime.parse(json.getString(key), dateTimeFormatter) :
                null;
    }

    public static List<String> stringList(JSONObject json, String key) throws JSONException {
        List<String> list = new ArrayList<>();

        if (!json.isNull(key)) {
            JSONArray jArray = json.getJSONArray(key);
            for (int i = 0; i < jArray.length(); i++) {
                String str = jArray.getString(i);
                list.add(str);
            }
        }
        return list;
    }

    public static List<Integer> integerList(JSONObject json, String key) throws JSONException {
        List<Integer> list = new ArrayList<>();

        if (!json.isNull(key)) {
            JSONArray jArray = json.getJSONArray(key);
            for (int i = 0; i < jArray.length(); i++) {
                int integer = jArray.getInt(i);
                list.add(integer);
            }
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Model> List<T> modelList(JSONObject json, String key, Class<T> clazz) throws JSONException {
        List<T> list = new ArrayList<>();

        if (!json.isNull(key)) {
            JSONArray jArray = json.getJSONArray(key);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject object = jArray.getJSONObject(i);
                Model model = ModelParserUtil.parseModelFromJson(object.toString(), clazz);
                list.add((T) model);
            }
        }

        return list;
    }
}

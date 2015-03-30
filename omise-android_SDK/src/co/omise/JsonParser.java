package co.omise;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
	
	public Token parseTokenJson(final String json) throws JSONException{
		
		Token token = new Token();
		Card card = new Card();
		
		//token obj
		JSONObject rootObject = new JSONObject(json);
		token.setId(rootObject.getString("id"));
		token.setLivemode(rootObject.getBoolean("livemode"));
		token.setLocation(rootObject.getString("location"));
		token.setUsed(rootObject.getBoolean("used"));
		token.setCreated(rootObject.getString("created"));
		
		//card obj
		JSONObject cardObject = rootObject.getJSONObject("card");
		card.setId(cardObject.getString("id"));
		card.setLivemode(cardObject.getBoolean("livemode"));
		card.setCountry(cardObject.getString("country"));
		card.setCity(cardObject.getString("city"));
		card.setPostalCode(cardObject.getString("postal_code"));
		card.setFinancing(cardObject.getString("financing"));
		card.setLastDigits(cardObject.getString("last_digits"));
		card.setBrand(cardObject.getString("brand"));
		card.setExpirationMonth(cardObject.getString("expiration_month"));
		card.setExpirationYear(cardObject.getString("expiration_year"));
		card.setFingerprint(cardObject.getString("fingerprint"));
		card.setName(cardObject.getString("name"));
		card.setCreated(cardObject.getString("created"));
		card.setSecurityCodeCheck(cardObject.getBoolean("security_code_check"));
		token.setCard(card);
		
		return token;
	}
}

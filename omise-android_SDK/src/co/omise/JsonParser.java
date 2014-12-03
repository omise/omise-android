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
		token.setCard(card);
		
		return token;
	}
	
	public Charge parseChargeJson(final String json) throws JSONException{
		
		Charge charge = new Charge();
		Card card = new Card();
		
		//charge obj
		JSONObject rootObject = new JSONObject(json);
		charge.setId(rootObject.getString("id"));
		charge.setLivemode(rootObject.getBoolean("livemode"));
		charge.setLocation(rootObject.getString("location"));
		charge.setAmount(rootObject.getInt("amount"));
		charge.setCurrency(rootObject.getString("currency"));
		charge.setDescription(rootObject.getString("description"));
		charge.setCapture(rootObject.getBoolean("capture"));
		charge.setAuthorized(rootObject.getBoolean("authorized"));
		charge.setCaptured(rootObject.getBoolean("captured"));
		charge.setTransaction(rootObject.getString("transaction"));
		charge.setReturnUri(rootObject.getString("return_uri"));
		charge.setReference(rootObject.getString("reference"));
		charge.setAuthorizeUri(rootObject.getString("authorize_uri"));
		
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
		charge.setCard(card);
		
		
		charge.setCustomer(rootObject.getString("customer"));
		charge.setIp(rootObject.getString("ip"));
		charge.setCreated(rootObject.getString("created"));
		
		return charge;
	}
	
}
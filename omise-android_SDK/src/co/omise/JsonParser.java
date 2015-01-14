package co.omise;

import java.util.ArrayList;

import org.json.JSONArray;
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
	
	public Customer parseCreateCustomerJson(final String json) throws JSONException{
		Customer customer = new Customer();
		Cards cards = new Cards();

		//customer obj
		JSONObject rootObject = new JSONObject(json);
		customer.setId(rootObject.getString("id"));
		customer.setLivemode(rootObject.getBoolean("livemode"));
		customer.setLocation(rootObject.getString("location"));
		customer.setDefaultCard(rootObject.getString("default_card"));
		customer.setEmail(rootObject.getString("email"));
		customer.setDescription(rootObject.getString("description"));
		customer.setCreated(rootObject.getString("created"));
		
		JSONObject cardsObject = rootObject.getJSONObject("cards");
		cards.setFrom(cardsObject.getString("from"));
		cards.setTo(cardsObject.getString("to"));
		cards.setOffset(cardsObject.getInt("offset"));
		cards.setLimit(cardsObject.getInt("limit"));
		cards.setTotal(cardsObject.getInt("total"));
		cards.setOffset(cardsObject.getInt("offset"));
		cards.setLocation(cardsObject.getString("location"));

		ArrayList<Card> cardList = new ArrayList<Card>();
		JSONArray cardsData = cardsObject.getJSONArray("data");
		for (int i = 0; i < cardsData.length(); i++) {
			JSONObject cardObject = cardsData.getJSONObject(i);
			Card card = new Card();
			card.setId(cardObject.getString("id"));
			card.setLivemode(cardObject.getBoolean("livemode"));
			card.setLocation(cardObject.getString("location"));
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
			cardList.add(card);
		}
		
		cards.setCards(cardList);
		customer.setCards(cards);
		
		return customer;
	}
	
}
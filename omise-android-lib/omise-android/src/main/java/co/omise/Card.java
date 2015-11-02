package co.omise;

/**
 * Card class
 */
public class Card {

	private String id = null;
	private boolean livemode = false;
	private String country = null;
	private String number = null;
	private String city = null;
	private String postalCode = null;
	private String financing = null;
	private String lastDigits = null;
	private String brand = null;
	private String expirationMonth = null;
	private String expirationYear = null;
	private String fingerprint = null;
	private String name = null;
	private String created = null;
	private String location = null;
    private String securityCode = null;
	private boolean securityCodeCheck = false;
	private String bank = null;
	
	@Override
	public String toString(){
		return "Card[id=" + id +
				",livemode=" + livemode +
				",country=" + country +
				",number=" + number +
				",city=" + city +
				",postalCode=" + postalCode +
				",financing=" + financing +
				",lastDigits=" + lastDigits +
				",brand=" + brand +
				",expirationMonth=" + expirationMonth +
				",expirationYear=" + expirationYear +
				",fingerprint=" + fingerprint +
				",name=" + name +
				",created=" + created +
				",location=" + location +
				",bank=" + bank +
				",securityCodeCheck=" + securityCodeCheck + "]";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isLivemode() {
		return livemode;
	}
	public void setLivemode(boolean livemode) {
		this.livemode = livemode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getFinancing() {
		return financing;
	}
	public void setFinancing(String financing) {
		this.financing = financing;
	}
	public String getLastDigits() {
		return lastDigits;
	}
	public void setLastDigits(String lastDigits) {
		this.lastDigits = lastDigits;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getExpirationMonth() {
		return expirationMonth;
	}
	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}
	public String getExpirationYear() {
		return expirationYear;
	}
	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getFingerprint() {
		return fingerprint;
	}
	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public boolean getSecurityCodeCheck() {
		return securityCodeCheck;
	}
	public void setSecurityCodeCheck(boolean securityCodeCheck) {
		this.securityCodeCheck = securityCodeCheck;
	}
	public String getBank() {return bank;}
	public void setBank(String bank) {this.bank = bank;}
}

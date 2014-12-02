package co.omise;

@SuppressWarnings("serial")
public class OmiseException extends Exception {
	public OmiseException(String cause){
		super(cause);
	}
	public OmiseException(Exception e){
		super(e);
	}
}

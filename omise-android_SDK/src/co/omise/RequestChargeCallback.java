package co.omise;
public interface RequestChargeCallback {
	
	/**
	 * Its call if token succeeded to get.
	 * @param token
	 */
	public void onRequestSucceeded(final Charge charge);
	
	/**
	 * Its call if charge failed to get.
	 * @param errorCode is define in co.omise.OmiseCallback
	 */
	public void onRequestFailed(final int errorCode);
}

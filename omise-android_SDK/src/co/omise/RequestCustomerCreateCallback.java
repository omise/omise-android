package co.omise;
public interface RequestCustomerCreateCallback extends OmiseCallback{
	
	/**
	 * Its call if create customer succeeded to get.
	 * @param customer
	 */
	public void onRequestSucceeded(final Customer customer);
	
	/**
	 * Its call if token failed to get.
	 * @param errorCode is define in co.omise.OmiseCallback
	 */
	public void onRequestFailed(final int errorCode);
}

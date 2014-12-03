package co.omise;
public interface RequestChargeCallback {
	
	public static final int ERRCODE_TIMEOUT = 0x00;
	public static final int ERRCODE_CONNECTION_FAILED = 0x01;
	public static final int ERRCODE_BAD_REQUEST = 0x02;
	public static final int ERRCODE_INVALID_JSON = 0x03;
	public static final int ERRCODE_UNKNOWN = 0x10;
	
	/**
	 * Its call if token succeeded to get.
	 * @param token
	 */
	public void onRequestSucceeded(final Token token);
	
	/**
	 * Its call if token failed to get.
	 * @param errorCode is define in co.omise.RequestTokenCallback
	 */
	public void onRequestFailed(final int errorCode);
}

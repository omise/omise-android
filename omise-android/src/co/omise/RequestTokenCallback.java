package co.omise;
public interface RequestTokenCallback {
	
	public static final int ERRCODE_TIMEOUT = 0x00;
	public static final int ERRCODE_CONNECTION_FAILED = 0x01;
	public static final int ERRCODE_BAD_REQUEST = 0x02;
	public static final int ERRCODE_INVALID_JSON = 0x03;
	public static final int ERRCODE_UNKNOWN = 0x10;
	
	/**
	 * tokenの取得に成功した場合にコールされます。
	 * @param token
	 */
	public void onRequestSucceeded(final Token token);
	
	/**
	 * tokenの取得に失敗した場合にコールされます。
	 * @param errorCode co.omise.RequestTokenCallbackに定義されています。
	 */
	public void onRequestFailed(final int errorCode);
}

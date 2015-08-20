package co.omise;

public interface OmiseCallback {
	public static final int ERRCODE_TIMEOUT = 0x00;
	public static final int ERRCODE_CONNECTION_FAILED = 0x01;
	public static final int ERRCODE_BAD_REQUEST = 0x02;
	public static final int ERRCODE_INVALID_JSON = 0x03;
	public static final int ERRCODE_UNKNOWN = 0x10;
}

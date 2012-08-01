package com.jasonzqshen.familyaccounting.core.utils;

public class CoreMessage {
	public static final String ERR_FILE_NOT_EXISTS = "File %s does not exist.";
	public static final String ERR_FILE_FORMAT_ERROR = "Format of file %s is incorrect.";
	public static final String ERR_FILE_NOT_ROOT_ELEM = "No root XML element.";
	public static final String ERR_PARAMETER_LENGTH = "The length of parameters should be %d, but it is %d.";
	public static final String ERR_PARAMETER_TYPE = "The parameter is not instance of %s";

	public enum MessageType {
		INFO, WARNING, ERROR
	}

	public final String _msg;
	public final Exception _exp;
	public final MessageType _type;

	/**
	 * Constructor, if message type is error, exp is mandatory
	 * 
	 * @param msg
	 * @param type
	 * @param exp
	 */
	public CoreMessage(String msg, MessageType type, Exception exp) {
		_msg = msg;
		_exp = exp;
		_type = type;
	}

	@Override
	public String toString() {
		if (_type == MessageType.ERROR) {
			return String.format("Error message: %s, exception: %s", _msg,
					_exp.toString());
		} else if (_type == MessageType.WARNING) {
			if (_exp != null)
				return String.format("Warning message: %s, exception: %s",
						_msg, _exp.toString());
			else
				return String.format("Warning message: %s", _msg);
		} else if (_type == MessageType.INFO) {
			return String.format("Information: %s", _msg, _exp.toString());
		}
		return null;
	}
}

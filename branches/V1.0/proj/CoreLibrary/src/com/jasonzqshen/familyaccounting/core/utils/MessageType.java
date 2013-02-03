package com.jasonzqshen.familyaccounting.core.utils;

public enum MessageType {
	
	INFO("INFO"), WARNING("WARN"), ERROR("ERRO");
	private final String _str;

	private MessageType(String str) {
		_str = str;
	}

	public String toString() {
		return _str;
	}
}

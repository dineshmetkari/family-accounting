package com.jasonzqshen.familyaccounting.core.utils;

public class DebugInformation {

	public final Class<?> CLASS;
	public final int LINE_NUMBER;
	public final String INFO;
	public final MessageType TYPE;

	public DebugInformation(Class<?> cl, int lineNum, String info,
			MessageType type) {
		CLASS = cl;
		LINE_NUMBER = lineNum;
		INFO = info;
		TYPE = type;
	}

	public String toString() {
		return String.format("%s, %s\t----%s(%d)", TYPE, INFO, CLASS.getSimpleName(),
				LINE_NUMBER);
	}
}

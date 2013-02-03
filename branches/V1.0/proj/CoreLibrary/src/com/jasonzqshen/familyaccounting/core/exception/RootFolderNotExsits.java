package com.jasonzqshen.familyaccounting.core.exception;

public class RootFolderNotExsits extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1160846118380633688L;
	public RootFolderNotExsits(String folderPath) {
		super(String.format("Root Folder %s does not exist.", folderPath));
	}
}

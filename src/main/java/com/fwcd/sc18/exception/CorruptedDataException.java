package com.fwcd.sc18.exception;

/**
 * Indicates that some data is inconsistent or broken.
 */
public class CorruptedDataException extends RuntimeException {
	private static final long serialVersionUID = -1958340756648952066L;
	private final int corruptedIndividualIndex;
	
	public CorruptedDataException(String msg) {
		super(msg);
		corruptedIndividualIndex = -1;
	}
	
	public CorruptedDataException(int corruptedIndividualIndex) {
		super("The individual " + corruptedIndividualIndex + " has been corrupted.");
		this.corruptedIndividualIndex = corruptedIndividualIndex;
	}

	public int getCorruptedIndex() {
		return corruptedIndividualIndex;
	}
}

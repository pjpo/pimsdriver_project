package com.github.pjpo.pimsdriver.processor;

import java.io.IOException;
import java.io.Writer;

import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine.Element;

public class RsfLineHandler extends PmsiLineHandler {

	private boolean wasHeader = false;
	
	private boolean wasRsfA = false;

	private long rsfHeader;
	
	private long rsfA;
	
	private Long currentParent = null;
	
	private String finess = null;
	
	private String version = null;
	
	public RsfLineHandler(final long pmsiPosition, final Writer writer) throws IOException {
		super(pmsiPosition, writer);
	}
	
	@Override
	protected Long getParent() {
		return currentParent;
	}

	@Override
	protected void calculateParent(IPmsiLine line) {
		// IF LAST ELEMENT WAS RSFHEADER, USE ITS ID AS THE PARENT ID FOR EVERY ELEMENT
		if (wasHeader)
			rsfHeader = getPmsiPosition() - 1;
		else if (wasRsfA)
			rsfA = getPmsiPosition() - 1;
			
		// CHECKS IF THIS ELEMENT IS HEADER OR RSFA FOR NEXT ITERATION THROUGH CALCULATEPARENT
		if (line.getName().equals("rsfheader")) {
			startsearch : for (Element element : line.getElements()) {
				if (element.getName().equals("Finess")) {
					finess = element.getElement().toString();
					break startsearch;
				}
			}
			version = line.getVersion();
			wasHeader = true;
			wasRsfA = false;
			currentParent = null;
		} else if (line.getName().equals("rsfa")) {
			wasHeader = false;
			wasRsfA = true;
			currentParent = rsfHeader;
		} else {
			wasHeader = false;
			wasRsfA = false;
			currentParent = rsfA;
		}

	}

	@Override
	public String getFiness() {
		return finess;
	}

	@Override
	public String getVersion() {
		return version;
	}
	
}

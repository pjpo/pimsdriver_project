package com.github.pjpo.pimsdriver.processor;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;

import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine.Element;

public class RssLineHandler extends PmsiLineHandler {

	private boolean wasHeader = false;
	
	private boolean wasMain = false;

	private long rssHeader;
	
	private long rssMain;
	
	private Long currentParent = null;
	
	private String finess = null;
	
	private String version = null;
	
	private LocalDate pmsiDate = null;
	
	public RssLineHandler(final long pmsiPosition, final Writer writer) throws IOException {
		super(pmsiPosition, writer);
	}
	
	@Override
	protected Long getParent() {
		return currentParent;
	}

	@Override
	protected void calculateParent(IPmsiLine line) {
		// IF LAST ELEMENT WAS RSSHEADER, STORE IT
		if (wasHeader)
			rssHeader = getPmsiPosition() - 1;
		else if (wasMain)
			rssMain = getPmsiPosition() - 1;
		
		// CHECKS IF THIS ELEMENT IS HEADER OR MAIN FOR NEXT ITERATION THROUGH CALCULATEPARENT
		if (line.getName().equals("rssheader")) {
			initsearch : for (Element element : line.getElements()) {
				if (element.getName().equals("Finess")) {
					finess = element.getElement().toString();
				} else if (element.getName().equals("FinPeriode")) {
					pmsiDate = LocalDate.parse(element.getElement().toString(), format);
					break initsearch;
				}
			}
			version = line.getVersion();
			wasHeader = true;
			wasMain = false;
			currentParent = null;
		} else if (line.getName().equals("rssmain")){
			wasHeader = false;
			wasMain = true;
			currentParent = rssHeader;
		} else {
			wasHeader = false;
			wasMain = false;
			currentParent = rssMain;
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

	@Override
	public LocalDate getPmsiDate() {
		return pmsiDate;
	}
}

package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Writer;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;

@Stateful
public class RsfParserBean implements RsfParser {

	private final static Logger LOGGER = Logger
			.getLogger(RsfParserBean.class.getName());

	private Path rsfFile = null;

	private Path rsfResult = null;
	
	private String finess = null;
	
	private String version = null;
	
	private Long endPmsiPosition = null;
	
	@PostActivate
	public void construct() {
	}
	
	@Override
	public Writer getStoreWriter() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String processRsf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResult() {
		return null;
	}
	
	@PrePassivate
	public void destroy() {
	}

}

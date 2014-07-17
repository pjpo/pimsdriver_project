package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;

import com.github.aiderpmsi.pims.parser.utils.PimsParserFromWriter;
import com.github.aiderpmsi.pims.parser.utils.SimpleParser;
import com.github.aiderpmsi.pims.parser.utils.SimpleParserFactory;
import com.github.pjpo.pimsdriver.processor.ErrorCatcher;
import com.github.pjpo.pimsdriver.processor.RsfLineHandler;
import com.github.pjpo.pimsdriver.processor.ejb.RsfParser.ObservableValue.ObservableListener;

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
		try {
			rsfResult = Files.createTempFile("", "");
			rsfFile =  Files.createTempFile("", "");
		} catch (Throwable e) {
			if (rsfResult != null)
				e = ErrorCatcher.execute(() -> { Files.deleteIfExists(rsfResult); rsfResult = null; }, e);
			if (rsfFile != null)
				e = ErrorCatcher.execute(() -> { Files.deleteIfExists(rsfFile); rsfFile = null; }, e);
			LOGGER.log(Level.SEVERE, "Unable to create temp file", e);
		}
	}
	
	@Override
	public Writer getStoreWriter() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ObservableValue<Float> processRsf(ObservableListener<Float> observer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result getResult() {
		Result result = new Result(null, null, null, null);
		return result;
	}
	
	@PrePassivate
	public void destroy() {
		Throwable e = null;
		if (rsfResult != null)
			e = ErrorCatcher.execute(() -> { Files.deleteIfExists(rsfResult); rsfResult = null; }, e);
		if (rsfFile != null)
			e = ErrorCatcher.execute(() -> { Files.deleteIfExists(rsfFile); rsfFile = null; }, e);
		if (e != null)
			LOGGER.log(Level.SEVERE, "Unable to delete temp file", e);
	}

}

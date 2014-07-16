package com.github.pjpo.pimsdriver.processor;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.ejb.Asynchronous;
import javax.ejb.Local;

@Local
public interface RsfParser {

	public Writer getWriter();
	
	@Asynchronous
	public Future<Boolean> processRsf();

	public Reader getResult();
	
}

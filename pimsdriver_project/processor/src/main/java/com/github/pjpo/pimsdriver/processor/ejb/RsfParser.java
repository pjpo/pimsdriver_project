package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Writer;
import javax.ejb.Local;


@Local
public interface RsfParser {

	public Writer getStoreWriter();
	
	public String processRsf();

	public String getResult();
	
}

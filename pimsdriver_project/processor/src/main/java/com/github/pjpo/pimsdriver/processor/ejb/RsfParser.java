package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import javax.ejb.Local;

@Local
public interface RsfParser extends Parser {

	Reader getReader();
}

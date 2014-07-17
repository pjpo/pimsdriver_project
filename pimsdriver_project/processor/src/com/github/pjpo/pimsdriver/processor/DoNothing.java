package com.github.pjpo.pimsdriver.processor;

import java.util.LinkedList;
import java.util.List;

import com.github.aiderpmsi.pims.parser.utils.PimsParserFromReader;
import com.github.aiderpmsi.pims.parser.utils.PimsParserFromWriter;
import com.github.aiderpmsi.pims.parser.utils.SimpleParserFactory;
import com.github.aiderpmsi.pims.treebrowser.TreeBrowserException;

public class DoNothing {

	public DoNothing() {
		List<String> liste = new LinkedList<>();
		try {
			SimpleParserFactory spf = new SimpleParserFactory();
			new PimsParserFromWriter();
		} catch (TreeBrowserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package com.github.pjpo.pimsdriver.processor;

import java.io.IOException;
import java.io.Writer;
import com.github.aiderpmsi.pims.parser.linestypes.ConfiguredPmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.EndOfFilePmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.LineNumberPmsiLine;
import com.github.aiderpmsi.pims.parser.utils.Utils.LineHandler;

/**
 * Process makeing the calls to database while main process reads pmsi file
 * @author jpc
 *
 */
public abstract class PmsiLineHandler implements LineHandler {

	/** Line number */
	private String lineNumber = null;
	
	/** Position in the pmsi */
	private Long pmsiPosition = null;
	
	/** Writer */
	private final Writer writer;
	
	public PmsiLineHandler(final long pmsiPosition, final Writer writer) throws IOException {
		this.pmsiPosition = pmsiPosition;
		this.writer = writer;
	}

	@Override
	public void handle(final IPmsiLine line) throws IOException {
		// IF WE ARE IN LINENUMBER, STORE THIS LINENUMBER
		if (line instanceof LineNumberPmsiLine) {
			final LineNumberPmsiLine pmsiLine = (LineNumberPmsiLine) line;
			lineNumber = pmsiLine.getLine();
		}
		// IF WE ARE IN A CLASSIC LINE, WRITES THE CONTENT
		else if (line instanceof ConfiguredPmsiLine) {
			// SETS THE NEW PARENT FOR THIS LINE
			calculateParent(line);
			
			// WRITES THE CONTENT
			// 1 - PMSI POSITION (UNIQUE IN EACH ROOT)
			writer.append(Long.toString(pmsiPosition));
			writer.append('|');
		
			// 2 - PARENT (NULL FOR HEADER)
			final Long pmel_parent = getParent();
			writer.append(pmel_parent == null ? "\\N" : Long.toString(pmel_parent));
			writer.append('|');
		
			// 3 - KIND OF CONTENT
			escape(line.getName(), writer);
			writer.append('|');

			// 4 -WRITES LINE NUMBER
			writer.append(lineNumber);
			writer.append('|');
		
			// 5 - WRITES LINE
			escape(line.getMatchedLine(), writer);

			// 6 - END LINE
			writer.append("\r\n");

			pmsiPosition++;
		} else if (line instanceof EndOfFilePmsiLine) {
			// EOF, CLOSE WRITER
			writer.close();
		}
	}
	
	private void escape(final CharSequence sgt, final Writer writer) throws IOException {		
		for (int i = 0 ; i < sgt.length() ; i++) {
			final char character = sgt.charAt(i); 
			if (character == '\\')
				for (final char escapeChar : escapeEscape)
					writer.append(escapeChar);
			else if (character == '|')
				for (final char escapeChar : escapeDelim)
					writer.append(escapeChar);
			else
				writer.append(character);
		}
	}
	
	public Long getPmsiPosition() {
		return pmsiPosition;
	}

	public String getLineNumber() {
		return lineNumber;
	}
	
	protected abstract Long getParent();

	protected abstract void calculateParent(final IPmsiLine line);
	
	public abstract String getFiness();

	public abstract String getVersion();
	
	private static final char[] escapeEscape = {'\\', '\\'};

	private static final char[] escapeDelim = {'\\', '|'};

}

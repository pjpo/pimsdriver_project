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
			writer.write(Long.toString(pmsiPosition));
			writer.write('\n');
		
			// 2 - PARENT (NULL FOR HEADER)
			final Long pmel_parent = getParent();
			writer.append(pmel_parent == null ? "N" : ":" + Long.toString(pmel_parent));
			writer.append('\n');
		
			// 3 - KIND OF CONTENT
			writer.write(line.getName());
			writer.append('\n');

			// 4 -WRITES LINE NUMBER
			writer.append(lineNumber);
			writer.append('\n');
		
			// 5 - WRITES LINE
			writer.write(line.getMatchedLine().sequence, line.getMatchedLine().start, line.getMatchedLine().count);

			// 6 - END LINE
			writer.append('\n');

			pmsiPosition++;
		} else if (line instanceof EndOfFilePmsiLine) {
			// EOF, CLOSE WRITER
			writer.close();
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

}

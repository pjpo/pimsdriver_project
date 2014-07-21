package com.github.pjpo.pimsdriver.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.github.aiderpmsi.pims.grouper.model.RssActe;
import com.github.aiderpmsi.pims.grouper.model.RssContent;
import com.github.aiderpmsi.pims.grouper.model.RssDa;
import com.github.aiderpmsi.pims.grouper.model.RssMain;
import com.github.aiderpmsi.pims.grouper.utils.Grouper;
import com.github.aiderpmsi.pims.grouper.utils.GrouperFactory;
import com.github.aiderpmsi.pims.parser.linestypes.ConfiguredPmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.EndOfFilePmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine;
import com.github.aiderpmsi.pims.parser.linestypes.IPmsiLine.Element;
import com.github.aiderpmsi.pims.parser.linestypes.LineNumberPmsiLine;
import com.github.aiderpmsi.pims.parser.utils.Utils.LineHandler;
import com.github.aiderpmsi.pims.treebrowser.TreeBrowserException;

/**
 * Process makeing the calls to database while main process reads pmsi file
 * @author jpc
 *
 */
public class GroupHandler implements LineHandler {

	/** Rss (list of Rums) */
	private final List<RssContent> fullRss = new ArrayList<>();
	
	/** Last rss number */
	private String lastNumRss = null;
	
	/** Used Grouper */
	private final Grouper grouper;
		
	/** Position in pmsi */
	protected Long pmsiPosition;
	
	/** List of pmsipositions of current rss */
	private final List<Long> pmsiPositions = new ArrayList<>();
	
	/** Writer to write the content of the grouping */
	private final Writer writer;
	
	public GroupHandler(final Long startPmsiPosition, final Writer writer) throws IOException {
		this.pmsiPosition = startPmsiPosition;
		this.writer = writer;
		// CREATES THE GROUPER
		try {
			GrouperFactory gf = new GrouperFactory();
			grouper = gf.newGrouper();
		} catch (TreeBrowserException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void handle(final IPmsiLine line) throws IOException {
		if (line instanceof ConfiguredPmsiLine) {

			// DEPENDING ON THE LINE TYPE :
			// 1 - IF IT IS A RSSMAIN, CREATE A NEW RSSCONTENT IN FULLRSS
			// 2 - IF IT IS A RSSACTE OR RSSDA, JUST INSERT THE DATAS TO CURRENT FULLRSS
			// 3 - IF IT IS A RSSHEADER, INCREMENT PMSIPOSITION
			if (line.getName().equals("rssmain")) {
				RssContent newContent = new RssContent();
				String newNumRss = null;

				EnumMap<RssMain, String> mainContent = new EnumMap<>(RssMain.class);
				for (Element element : line.getElements()) {
					if (element.getName().equals("NumRSS"))
						newNumRss = element.getElement().toString();
					else if (element.getName().equals("NbSeances"))
						mainContent.put(RssMain.nbseances, element.getElement().toString());
					else if (element.getName().equals("DP"))
						mainContent.put(RssMain.dp, element.getElement().toString());
					else if (element.getName().equals("DR"))
						mainContent.put(RssMain.dr, element.getElement().toString());
					else if (element.getName().equals("ModeEntree"))
						mainContent.put(RssMain.modeentree, element.getElement().toString());
					else if (element.getName().equals("ModeSortie"))
						mainContent.put(RssMain.modesortie, element.getElement().toString());
					else if (element.getName().equals("PoidsNouveauNe"))
						mainContent.put(RssMain.poidsnouveaune, element.getElement().toString());
					else if (element.getName().equals("Sexe"))
						mainContent.put(RssMain.sexe, element.getElement().toString());
					else if (element.getName().equals("DateEntree"))
						mainContent.put(RssMain.dateentree, element.getElement().toString());
					else if (element.getName().equals("DateSortie"))
						mainContent.put(RssMain.datesortie, element.getElement().toString());
					else if (element.getName().equals("DDN"))
						mainContent.put(RssMain.ddn, element.getElement().toString());
					else if (element.getName().equals("AgeGestationnel"))
						mainContent.put(RssMain.agegestationnel, element.getElement().toString());
				}

				newContent.setRssmain(mainContent);
				
				// IF THIS RSS HAS A DIFFERENT NUMRSS THAN PRECEDENT, STORE PRECEDENTS (EXCEPT IF THERE WAS NO PRECEDENT RSS)
				if (lastNumRss != null && !newNumRss.equals(lastNumRss)) {
					groupAndStore();
					// REINIT FULL RSS
					fullRss.clear();
					pmsiPositions.clear();
				}
				
				fullRss.add(newContent);
				lastNumRss = newNumRss;
				pmsiPositions.add(pmsiPosition++);

			} else if (line.getName().equals("rssacte")) {
				EnumMap<RssActe, String> acteRss = new EnumMap<>(RssActe.class);

				for (Element element : line.getElements()) {
					if (element.getName().equals("Activite"))
						acteRss.put(RssActe.activite, element.getElement().toString());
					else if (element.getName().equals("CodeCCAM"))
						acteRss.put(RssActe.codeccam, element.getElement().toString());
					else if (element.getName().equals("Phase"))
						acteRss.put(RssActe.phase, element.getElement().toString());
				}
				
				fullRss.get(fullRss.size() - 1).getRssacte().add(acteRss);
				pmsiPosition++;
			} else if (line.getName().equals("rssda")) {
				EnumMap<RssDa, String> daRss = new EnumMap<>(RssDa.class);

				for (Element element : line.getElements()) {
					if (element.getName().equals("DA"))
						daRss.put(RssDa.da, element.getElement().toString());
				}

				fullRss.get(fullRss.size() - 1).getRssda().add(daRss);
				pmsiPosition++;
			} else if (line.getName().equals("rssheader")) {
				pmsiPosition++;
			}
		} else if (line instanceof LineNumberPmsiLine) {
			
		} else if (line instanceof EndOfFilePmsiLine) {
			// GROUP AND STORE REMAIN RSS
			if (fullRss.size() != 0) {
				groupAndStore();
				// REINIT FULL RSS
				fullRss.clear();
				pmsiPositions.clear();
			}
			// EOF, CLOSE WRITER
			writer.close();
		}
	}

	private void groupAndStore() throws IOException {
		HashMap<?, ?> group;
		try {
			// MUST CATCH EVERY EXCEPTIONs (EVEN RUNTIMEEXCEPTION)
			group = grouper.group(fullRss);
		} catch (Exception e) {
			throw new IOException(e);
		}

		if (group == null) {
			throw new IOException("Groupage result is null, implementation error");
		}
		
		final StringBuilder lineBuilder = new StringBuilder();

		final Object[] elements = {
				group.get("racine"), group.get("modalite"), group.get("gravite"), group.get("erreur")};

		Consumer<Object> consumer = (element) -> {
			if (element == null) {
				lineBuilder.append("N\n");
			} else {
				lineBuilder.append(":");
				lineBuilder.append(element.toString());
				lineBuilder.append("\n");
			}
		};

		for (Object element : elements) {
			consumer.accept(element);
		}
		
		final String line = lineBuilder.toString();  
		
		// WRITES THE ELEMENT
		for (Long pmsiPosition : pmsiPositions) {
			writer.write(Long.toString(pmsiPosition));
			writer.write('\n');
			writer.write(line);
		}
	}

}

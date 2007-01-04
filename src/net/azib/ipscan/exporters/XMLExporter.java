/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.azib.ipscan.config.Version;

/**
 * XMLExporter
 *
 * @author anton
 */
public class XMLExporter implements Exporter {
	
	static final String ENCODING = "UTF-8";

	private PrintWriter output;
	private int ipFetcherIndex;
	private String[] fetcherNames;
	
	/*
	 * @see net.azib.ipscan.exporters.Exporter#getLabel()
	 */
	public String getLabel() {
		return "exporter.xml";
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#getFilenameExtension()
	 */
	public String getFilenameExtension() {
		return "xml";
	}
	
	/*
	 * @see net.azib.ipscan.exporters.Exporter#setAppend(boolean)
	 */
	public void setAppend(boolean append) {
		if (append) {
			throw new ExporterException("xml.noAppend");
		}
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#start(java.io.OutputStream, java.lang.String)
	 */
	public void start(OutputStream outputStream, String feederInfo) throws IOException {
		output = new PrintWriter(new OutputStreamWriter(outputStream, ENCODING));
		output.println("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\" standalone=\"yes\"?>");
		output.println("<!-- This file has been generated by " + Version.FULL_NAME + " -->");
		output.println("<!-- Visit the website at " + Version.WEBSITE + " -->");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		output.println("<scanning_report time=\"" + sdf.format(new Date()) + "\">");
		
		// this is a hack to extract feeder name from the feederInfo
		// this may not work with some non-standard feeders
		int colonPos = feederInfo.indexOf(':');
		String feederName = null;
		if (colonPos >= 0) {
			feederName = feederInfo.substring(0, colonPos);
			feederInfo = feederInfo.substring(colonPos + 1);
		}
		output.print("\t<feeder" + (feederName != null ? " name=\"" + feederName.trim() +"\"" : "") + ">");
		output.print("<![CDATA[" + feederInfo.trim() + "]]>");
		output.println("</feeder>");
		
		output.println("\t<hosts>");
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#end()
	 */
	public void end() throws IOException {
		output.println("\t</hosts>");
		output.println("</scanning_report>");
		if (output.checkError()) {
			throw new IOException();
		}
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#setFetchers(java.lang.String[])
	 */
	public void setFetchers(String[] fetcherNames) throws IOException {
		ipFetcherIndex = IPListExporter.findFetcherByLabel("fetcher.ip", fetcherNames);
		this.fetcherNames = fetcherNames;
	}

	/*
	 * @see net.azib.ipscan.exporters.Exporter#nextAdressResults(java.lang.Object[])
	 */
	public void nextAdressResults(Object[] results) throws IOException {		
		output.println("\t\t<host address=\"" + results[ipFetcherIndex] + "\">");
		
		for (int i = 0; i < results.length; i++) {
			if (results[i] != null) {
				output.println("\t\t\t<result name=\"" + fetcherNames[i] + "\"><![CDATA[" + results[i] + "]]></result>");
			}
		}
		
		output.println("\t\t</host>");
	}


	/*
	 * @see net.azib.ipscan.exporters.Exporter#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}

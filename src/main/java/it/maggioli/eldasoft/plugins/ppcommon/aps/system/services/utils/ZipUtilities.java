/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility per la compressione di file.
 * 
 * @author marco.perazzetta
 */
public class ZipUtilities {
	
	/**
	 * Stream contenuto in un file zip valido senza file o cartelle all'interno.
	 */
	private static final byte[] EMPTY_ZIP = {80,75,05,06,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00};


	/**
	 * Crea uno zip a partire da una lista di file in input.
	 * 
	 * @param files
	 *            file da inserire nello zip
	 * @return stream contenente lo zip dei file; nel caso di lista vuota lo
	 *         stream è quello del file zip senza allegati
	 * @throws IOException
	 */
	public static byte[] getZip(List<Allegato> files) throws IOException {

		// creazione dello zip in memoria contenente l'elenco di file
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		if (files.size() > 0) {
			ZipOutputStream zipOut = new ZipOutputStream(baos);
			zipOut.setMethod(ZipOutputStream.DEFLATED);
			zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

			// inserimento dei file
			for (Allegato documento : files) {
				ZipEntry entry = new ZipEntry(documento.getNome());
				entry.setSize(documento.getContenuto().length);
				zipOut.putNextEntry(entry);
				zipOut.write(documento.getContenuto());
				zipOut.closeEntry();
			}

			// chiusura dello zip
			zipOut.flush();
			zipOut.close();
		} else {
			// si crea uno zip valido senza file contenuti all'interno
			baos.write(EMPTY_ZIP, 0, 22);
		}

		// assegnazione alla response del contenuto zippato
		return baos.toByteArray();
	}
}

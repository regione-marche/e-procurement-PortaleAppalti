package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers.SbloccaAccountAction;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 * Classe con metodi di utilit&agrave; sulle stringhe, non disponibili nelle commons-lang alla versione utilizzata.
 * 
 * @author Stefano.Sabbadin
 */
public class StringUtilities {
	
    public static final char CSV_DELIMITER = ';';
    private static final char CSV_QUOTE = '"';
    private static final char[] CSV_SEARCH_CHARS = new char[] {CSV_DELIMITER, CSV_QUOTE, CharUtils.CR, CharUtils.LF};
    
    /**
     * <p>Returns a <code>String</code> value for a CSV column enclosed in double quotes,
     * if required.</p>
     *
     * <p>If the value contains a comma, newline or double quote, then the
     *    String value is returned enclosed in double quotes.</p>
     * </p>
     *
     * <p>Any double quote characters in the value are escaped with another double quote.</p>
     *
     * <p>If the value does not contain a comma, newline or double quote, then the
     *    String value is returned unchanged.</p>
     * </p>
     *
     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * @param str the input CSV column String, may be null
     * @return the input String, enclosed in double quotes if the value contains a comma,
     * newline or double quote, <code>null</code> if null string input
     * @since Commons Lang 2.4
     */
    public static String escapeCsv(String str) {
        if (StringUtils.containsNone(str, CSV_SEARCH_CHARS)) {
            return str;
        }
        try {
            StringWriter writer = new StringWriter();
            escapeCsv(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new UnhandledException(ioe);
        }
    }

    /**
     * <p>Writes a <code>String</code> value for a CSV column enclosed in double quotes,
     * if required.</p>
     *
     * <p>If the value contains a comma, newline or double quote, then the
     *    String value is written enclosed in double quotes.</p>
     * </p>
     *
     * <p>Any double quote characters in the value are escaped with another double quote.</p>
     *
     * <p>If the value does not contain a comma, newline or double quote, then the
     *    String value is written unchanged (null values are ignored).</p>
     * </p>
     *
     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
     *
     * @param str the input CSV column String, may be null
     * @param out Writer to write input string to, enclosed in double quotes if it contains
     * a comma, newline or double quote
     * @throws IOException if error occurs on underlying Writer
     * @since Commons Lang 2.4
     */
    public static void escapeCsv(Writer out, String str) throws IOException {
        if (StringUtils.containsNone(str, CSV_SEARCH_CHARS)) {
            if (str != null) {
                out.write(str);
            }
            return;
        }
        out.write(CSV_QUOTE);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == CSV_QUOTE) {
                out.write(CSV_QUOTE); // escape double quote
            }
            if (c == CharUtils.LF) {
                out.write(' '); // remove new line
            } else if (c == CharUtils.CR) {
            	// no chars
            } else {
                out.write(c);
            }
        }
        out.write(CSV_QUOTE);
    }	

	/**
	 * Converte una stringa in notazione camel case in una in notazione snake
	 * convertita in maiuscole. Esempi:
	 * <ul>
	 * <li>myPersonalCard => MY_PERSONAL_CARD</li>
	 * <li>IBANCode => IBAN_CODE</li>
	 * <li>lastWSCall => LAST_WS_CALL</li>
	 * </ul>
	 * 
	 * @param text
	 *            stringa in notazione camel case, ovvero composta da parole
	 *            unite con iniziali in maiuscolo e nessun separatore di parola
	 * @return null se la stringa &egrave; vuota, altrimetnmi la stringa
	 *         convertita in maiuscolo con le parole separate da "_"
	 */
	public static String camelToUpperCaseSnake(String text) {
		StringBuffer result = new StringBuffer();
		
		if (text == null) {
			// nel caso di stringa null non faccio nulla
			return null;
		}

		LinkedList<String> words = new LinkedList<String>();	
	    for (String w : text.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
	    	words.add(w);
	    }    
		boolean firstWord = true;
		for (String word : words) {
			if (!firstWord) {
				if (!result.toString().endsWith("_")) {
					// si evita di aggiungere underscore successivi ad eventuali underscore presenti nel testo in input
					result.append("_");				
				}
			} else {
				firstWord = false;
			}
			result.append(word.toUpperCase());
		}
		
		return result.toString();
	}

	/**
	 * A partire dal contenuto in input si genera il digest SHA256.
	 * 
	 * @param contenuto
	 *            contenuto da elaborare
	 * @return sha 256 del file
	 */
	public static String getSha256(String contenuto) {
		byte[] sha256 = DigestUtils.sha256(contenuto);
		StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < sha256.length; i++) {
		    String hex = Integer.toHexString(0xff & sha256[i]);
		    if(hex.length() == 1) hexString.append('0');
		    hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Legge il file PDF in input e genera una stringa di testo come
	 * concatenazione del contenuto delle pagine del pdf
	 * 
	 * @param filePdf
	 *            file pdf da leggere
	 * @return stringa contenente il testo delle pagine
	 * @throws IOException
	 */
	public static String getPdfContentAsString(File filePdf)
			throws IOException {
		PdfReader reader = new PdfReader(filePdf.getAbsolutePath());
		String content = getPdfContentAsString(reader);
		reader.close();
		return content;
	}
	
	/**
	 * Legge lo stream relativo al file PDF in input e genera una stringa di
	 * testo come concatenazione del contenuto delle pagine del pdf
	 * 
	 * @param filePdf
	 *            file pdf da leggere
	 * @return stringa contenente il testo delle pagine
	 * @throws IOException
	 */
	public static String getPdfContentAsString(byte[] filePdf)
			throws IOException {
		PdfReader reader = new PdfReader(filePdf);
		String content = getPdfContentAsString(reader);
		reader.close();
		return content;
	}

	/**
	 * Legge il file PDF in input e genera una stringa di testo come
	 * concatenazione del contenuto delle pagine del pdf e poi di tutti i
	 * commenti.
	 * 
	 * @param filePdf
	 *            file pdf da leggere
	 * @return stringa contenente il testo delle pagine
	 * @throws IOException
	 */
	public static String getPdfContentAsString(PdfReader reader)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		int pages = reader.getNumberOfPages();
		for (int i = 1; i <= pages; i++) {
		    String textFromPage = PdfTextExtractor.getTextFromPage(reader, i);
		    sb.append(textFromPage).append("\n");
		}
        for (int i = 1; i <= pages; i++) {
        	PdfDictionary page = reader.getPageN(i);
        	PdfArray annotsArray = null;
        	if(page.getAsArray(PdfName.ANNOTS)==null) {
        		continue;
        	}
        	annotsArray = page.getAsArray(PdfName.ANNOTS);
        	for (ListIterator<PdfObject> iter = annotsArray.listIterator(); iter.hasNext();) {
        		PdfDictionary annot = (PdfDictionary) PdfReader.getPdfObject(iter.next());
        		PdfString content = (PdfString) PdfReader.getPdfObject(annot.get(PdfName.CONTENTS));
        		if (content != null) {
        			sb.append(content).append("\n");
        		}
        	}
        }
		return sb.toString();
	}
	
	/**
	 * Consente di stampare un dato numerico in formato NON SCIENTIFICO
	 * 
	 * @param d
	 *            decimale da convertire da notazione scientifica a non
	 *            scientifica
	 * @param decimals
	 *            numero massimo di decimali ammessi (eventualmente viene
	 *            effettuato in arrotondamento)
	 * @return stringa formattata nel formato corretto non pi&ugrave;
	 *         scientifico
	 */
	public static String getDoubleNormalNotation(double d, int decimals) {
		StringBuilder pattern = new StringBuilder("#0");
		if (decimals > 0) {
			pattern.append(".");
		}
		for (int i = 0; i < decimals; i++) {
			pattern.append("#");
		}
		// si usa il Locale inglese in modo da ottenere sempre il punto decimale
		NumberFormat formatter = new DecimalFormat(pattern.toString(),
				new DecimalFormatSymbols(Locale.ENGLISH));
		return formatter.format(d);
	}
}

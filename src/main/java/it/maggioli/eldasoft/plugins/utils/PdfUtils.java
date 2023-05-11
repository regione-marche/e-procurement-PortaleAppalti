package it.maggioli.eldasoft.plugins.utils;

import com.itextpdf.text.pdf.PdfReader;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.IOException;
import java.util.Map;

public final class PdfUtils {

    private PdfUtils() { }

    /**
     * Check if the uploaded pdf has the expected hash
     *
     * @param content
     * @param expectedHash
     * @return
     * @throws IOException
     */
    public static boolean hasCorrectHash(byte[] content, String expectedHash) throws IOException {
        PdfReader readInputPDF = new PdfReader(content);
        Map<String, String> pdfDictionary = readInputPDF.getInfo();
        String testoPdf = StringUtilities.getPdfContentAsString(readInputPDF);
        String digestTestoPdf = StringUtilities.getSha256(testoPdf);
        return (expectedHash.equals(pdfDictionary.get(PortGareSystemConstants.PDF_HASH_DICTIONARY))
                || expectedHash.equals(pdfDictionary.get("Keywords")))  //Controllo aggiunto per retrocompatibilità, in futuro potrà essere rimosso
                && expectedHash.equals(digestTestoPdf);
    }

}

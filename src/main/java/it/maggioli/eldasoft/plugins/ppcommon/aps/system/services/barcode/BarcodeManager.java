package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;
import org.xml.sax.SAXException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore della generazione del codice a barre
 * 
 * @version 1.1
 * @author Stefano.Sabbadin
 */
public class BarcodeManager extends AbstractService implements IBarcodeManager {

    /**
     * UID
     */
    private static final long serialVersionUID = 6347543787093354642L;

    @Override
    public void init() throws Exception {
	ApsSystemUtils.getLogger().debug(
		this.getClass().getName() + ": inizializzato ");
    }

    @Override
    public void generateBarcode(InputStream configFile, OutputStream out,
	    String message) throws ApsException {
	final int dpi = 120;
	DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
	try {
	    Configuration cfg = builder.build(configFile);
	    BitmapCanvasProvider canvas = new BitmapCanvasProvider(out,
		    MimeTypes.MIME_GIF, dpi, BufferedImage.TYPE_BYTE_BINARY,
		    false, 0);
	    BarcodeGenerator gen = BarcodeUtil.getInstance()
		    .createBarcodeGenerator(cfg);
	    gen.generateBarcode(canvas, message);
	    canvas.finish();
	} catch (ConfigurationException e) {
	    throw new ApsException(
		    "Errore inaspettato durante la lettura del file di configurazione per la generazione del timbro digitale",
		    e);
	} catch (SAXException e) {
	    throw new ApsException(
		    "Errore inaspettato durante l'interpretazione del file xml di configurazione per la generazione del timbro digitale",
		    e);
	} catch (IOException e) {
	    throw new ApsException(
		    "Errore inaspettato durante la gestione dei file di configurazione o di output del timbro digitale",
		    e);
	} catch (BarcodeException e) {
	    throw new ApsException(
		    "Errore inaspettato durante la definizione dell'oggetto per la generazione del timbro digitale",
		    e);
	}
    }

}

package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.barcode.IBarcodeManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * @author stefano.sabbadin
 * @since 1.2
 */
public class ProcessPageEsitoAction extends BaseAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -1322627248030911761L;

//    /**
//     * Tipo di evento per la richiesta di partecipazione ad una gara
//     */
//    private static final String RICHIESTA_PARTECIPAZIONE_GARA = "FS6";
//
//    /**
//     * Tipo di evento per la richiesta di invio offerta
//     */
//    private static final String RICHIESTA_INVIO_OFFERTA = "FS7";
//
//    /**
//     * Tipo di evento per la richiesta di invio documentazione per le
//     * verifiche all'art.48
//     */
//    private static final String RICHIESTA_COMPROVA_REQUISITI = "FS8";

    private Map<String, Object> session;

    private IBandiManager bandiManager;
    private IBarcodeManager barcodeManager;

    private LinkedHashMap<String, String> valueBarcodeLotti = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> filenameBarcodeLotti = new LinkedHashMap<String, String>();

    private InputStream inputStream;

    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    public void setBandiManager(IBandiManager bandiManager) {
    	this.bandiManager = bandiManager;
    }

    public void setBarcodeManager(IBarcodeManager barcodeManager) {
    	this.barcodeManager = barcodeManager;
    }

    public LinkedHashMap<String, String> getValueBarcodeLotti() {
    	return valueBarcodeLotti;
    }

    public LinkedHashMap<String, String> getFilenameBarcodeLotti() {
    	return filenameBarcodeLotti;
    }

    public InputStream getInputStream() {
    	return inputStream;
    }

    /**
	 * genera il PDF della richiesta di partecipazione 
	 */
    public String createPdf() {
    	String target = SUCCESS;
    	
    	WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
    		.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);

    	if (partecipazioneHelper == null) {
    		// la sessione e' scaduta, occorre riconnettersi
    		this.addActionError(this.getText("Errors.sessionExpired"));
    		target = ERROR;
    	} else {
    		// la sessione non e' scaduta, per cui proseguo regolarmente
    		try {
    			// si generano i barcode dei lotti selezionati
    			String idLotto = null;
    			String barcodeValue = null;
    			// String nomeImmagine = null;
    			
    			partecipazioneHelper.getLottiBarcodeFiles().clear();
    			if (partecipazioneHelper.isLottiDistinti()) {
    				LottoGaraType[] lotti = this.bandiManager
    					.getLottiGara(partecipazioneHelper.getIdBando());
    				
    				for (int i = 0; i < lotti.length; i++) {
    					if (partecipazioneHelper.getLotti().contains(lotti[i].getCodiceLotto())) {
    						idLotto = "Lotto: " + lotti[i].getOggetto();
    						barcodeValue = this.genTextForBarcode(
    								partecipazioneHelper, 
    								this.getCurrentUser().getUsername(), 
    								lotti[i].getCodiceLotto());
    						this.valueBarcodeLotti.put(idLotto, barcodeValue);
    						this.genBarcodeImage(
    								barcodeValue, 
    								idLotto, 
    								partecipazioneHelper);
    					}
    				}
    			} else {
    				barcodeValue = this.genTextForBarcode(
    						partecipazioneHelper,
    						this.getCurrentUser().getUsername(),
    						partecipazioneHelper.getLotti().first());
    				idLotto = "Gara: " + partecipazioneHelper.getDescBando();
    				this.valueBarcodeLotti.put(idLotto, barcodeValue);
    				this.genBarcodeImage(
    						barcodeValue, 
    						idLotto,
    						partecipazioneHelper);
    			}

    			// si determina il nome del file di destinazione nell'area
    			// temporanea
    			String nomePdf = FileUploadUtilities.generateFileName() + ".pdf";
    			File filePdf = new File(this.getTempDir().getAbsolutePath() + File.separatorChar + nomePdf);
    			
    			StringBuilder xml = genBarcodeXml(partecipazioneHelper);

    			// si carica il report jasper, lo si filla con i dati e si
    			// genera il pdf
    			InputStream isJasper = this.getRequest().getSession().getServletContext()
    				.getResourceAsStream(PortGareSystemConstants.GARE_JASPER_FOLDER + "Barcode.jasper");
    			
    			JRXmlDataSource jrxmlds = new JRXmlDataSource(
    				new ByteArrayInputStream(xml.toString().getBytes("UTF-8")), "/rich_partecipazione/lotto");
    			
    			JasperPrint print = JasperFillManager.fillReport(isJasper, new HashMap<String, Object>(), jrxmlds);
    			JRExporter exporter = new JRPdfExporter();
    			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, filePdf.getAbsolutePath());
    			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
    			exporter.exportReport();

    			// generato il report con JasperReport, si memorizza lo stream
    			// per il download e si inserisce una referenza nel bean in
    			// sessione per la rimozione alla rimozione del bean dalla
    			// sessione
    			this.inputStream = new FileInputStream(filePdf);
    			partecipazioneHelper.getTempFiles().add(filePdf);

    		} catch (FileNotFoundException e) {
    			// non dovrebbe mai verificarsi in quanto il file va scritto per
    			// cui non esiste ma va creato
    			ApsSystemUtils.logThrowable(e, this, "createPdf");
    			ExceptionUtils.manageExceptionError(e, this);
    			target = ERROR;
    		} catch (JRException e) {
    			ApsSystemUtils.logThrowable(e, this, "createPdf");
    			ExceptionUtils.manageExceptionError(e, this);
    			// il download e' una url indipendente e non dentro una porzione
    			// di pagina
    			target = ERROR;
    		} catch (UnsupportedEncodingException e) {
    			// non dovrebbe mai verificarsi in quanto l'encoding specificata
    			// e' corretta
    			ApsSystemUtils.logThrowable(e, this, "createPdf");
    			ExceptionUtils.manageExceptionError(e, this);
    			target = ERROR;
    		} catch (Throwable e) {
    			ApsSystemUtils.logThrowable(e, this, "createPdf");
    			ExceptionUtils.manageExceptionError(e, this);
    			target = ERROR;
    		} 
    	}
    	return target;
    }

    private StringBuilder genBarcodeXml(
    		WizardPartecipazioneHelper partecipazioneHelper) 
    {
    	StringBuilder xml = new StringBuilder();
    	xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	xml.append("<rich_partecipazione>\n");
    	for (Iterator<String> iterator = partecipazioneHelper.getLottiBarcodeFiles().keySet().iterator(); iterator.hasNext();) {
    		String lotto = iterator.next();
    		File file = partecipazioneHelper.getLottiBarcodeFiles().get(lotto);
    		xml.append("  <lotto>\n");
    		xml.append("    <nome>").append(lotto).append("</nome>\n");
    		xml.append("    <barcode_file>").append(file.getAbsolutePath())
    		.append("</barcode_file>\n");
    		xml.append("  </lotto>\n");
    	}
    	xml.append("</rich_partecipazione>\n");
    	return xml;
    }

    /**
     * Genera la stringa cifrata e in base64 da utilizzare per la generazione
     * del timbro digitale
     * 
     * @param partecipazioneHelper
     *            helper del wizard di partecipazione
     * @param impresa
     *            identificativo dell'impresa (o meglio, username dell'utente
     *            associato all'impresa)
     * @param codiceLotto
     *            codice del lotto
     * @return stringa cifrata
     * @throws ApsException
     */
    private String genTextForBarcode(
    		WizardPartecipazioneHelper partecipazioneHelper, String impresa,
    		String codiceLotto) throws ApsException 
    {
    	String risultato = null;
    	StringBuilder output = new StringBuilder();
    	char separatore = '|';
    	output.append(partecipazioneHelper.getTipoEvento());
    	//	if (partecipazioneHelper.getTipoEvento() == 1)
    	//	    output
    	//		    .append(ProcessPageEsitoAction.RICHIESTA_PARTECIPAZIONE_GARA);
    	//	else if (partecipazioneHelper.getTipoEvento() == 2)
    	//	    output.append(ProcessPageEsitoAction.RICHIESTA_INVIO_OFFERTA);
    	//	else
    	//	    output.append(ProcessPageEsitoAction.RICHIESTA_COMPROVA_REQUISITI);
    	output.append(separatore);
    	output.append(impresa);
    	output.append(separatore);
    	output.append(partecipazioneHelper.getGenere());
    	output.append(separatore);
    	output.append(partecipazioneHelper.getIdBando());
    	output.append(separatore);
    	output.append(codiceLotto);
    	output.append(separatore);
    	output.append(partecipazioneHelper.isRti() ? 1 : 0);
    	output.append(separatore);
    	// 2012-05-15: eliminata la ragione sociale per diminuire la dimensione
    	// del timbro (si mantiene il campo vuoto non usato)
    	//	output.append(partecipazioneHelper.isRti() ? partecipazioneHelper
    	//		.getDenominazioneRTI().substring(
    	//			0,
    	//			Math.min(60, partecipazioneHelper.getDenominazioneRTI()
    	//				.length())) : "");

    	try {
    		ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
    				FactoryCriptazioneByte.CODICE_CRIPTAZIONE_ADVANCED, 
    				output.toString().getBytes(),
    				ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
    		DatoBase64 base64 = new DatoBase64(criptatore.getDatoCifrato(), DatoBase64.FORMATO_ASCII);
    		risultato = base64.getDatoBase64();
    	} catch (CriptazioneException e) {
    		throw new ApsException(
    				"Errore inaspettato durante la criptazione dei dati da includere nel timbro digitale",
    				e);
    	}
    	return risultato;
    }

    /**
     * Genera l'immagine contenente il barcode nell'area work dell'applicazione,
     * e salva il riferimento in un attributo della classe, nonch&egrave;
     * aggiunge il riferimento al file creato nell'oggetto wizard in sessione in
     * modo da effettuare l'eliminazione in automatico alla rimozione
     * dell'oggetto in sessione
     * 
     * @param barcodeValue
     *            stringa da cifrare come codice a barre
     * @param idLotto
     *            identificativo del lotto
     * @param partecipazioneHelper
     *            helper del wizard di partecipazione
     * @throws FileNotFoundException
     * @throws ApsException
     */
    private void genBarcodeImage(
    		String barcodeValue, 
    		String idLotto,
    		WizardPartecipazioneHelper partecipazioneHelper)
    	throws FileNotFoundException, ApsException 
    {
    	String nomeImmagine = FileUploadUtilities.generateFileName() + ".gif";
    	File f = new File(this.getTempDir().getAbsolutePath() + File.separatorChar + nomeImmagine);
    	InputStream configFile = this.getRequest().getSession().getServletContext()
    		.getResourceAsStream(CommonSystemConstants.WEBINF_FOLDER + "barcode-config.xml");
    	this.barcodeManager.generateBarcode(configFile, new FileOutputStream(f), barcodeValue);
    	this.filenameBarcodeLotti.put(idLotto, nomeImmagine);
    	partecipazioneHelper.getTempFiles().add(f);
    	partecipazioneHelper.getLottiBarcodeFiles().put(idLotto, f);
    }

    /**
     * Ritorna la directory per i file temporanei prendendola sempre da
     * javax.servlet.context.tempdir (cartella di default temporanea per il
     * singolo contesto)
     * 
     * @return path alla directory per i file temporanei
     */
    private File getTempDir() {
    	return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext());
    }
    
}

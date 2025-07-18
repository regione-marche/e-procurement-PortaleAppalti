package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.user.UserDetails;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.ImpresaAggiornabileType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.nso.client.model.NsoWsOrder;
import it.maggioli.eldasoft.nso.client.model.OrderRequestToNso;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.IOrdiniManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Azione per la gestione delle operazioni a livello di dettaglio ordine
 * 
 * @author 
 * @since 
 */
public class OrdiniAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 6758181859672380080L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, Object> session;
	
	private IOrdiniManager ordiniManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;

	@Validate(EParamValidation.DIGIT)
	private String currentFrame;
	private long id;
	private Long genere;
	
	private NsoWsOrder dettaglioOrdine;
	private String orderXml;
	@Validate(EParamValidation.GENERIC)
	private String dataLimiteModifiche;
	

	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numComunicazioniInviate;
	
	private SimpleDateFormat sdfIta = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdfIta2 = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat sdfISO8601 = new SimpleDateFormat("yyyy-MM-dd");
		

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setOrdiniManager(IOrdiniManager ordiniManager) {
		this.ordiniManager = ordiniManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public NsoWsOrder getDettaglioOrdine() {
		return dettaglioOrdine;
	}

	public int getNumComunicazioniRicevute() {
		return numComunicazioniRicevute;
	}

	public int getNumComunicazioniRicevuteDaLeggere() {
		return numComunicazioniRicevuteDaLeggere;
	}

	public int getNumComunicazioniArchiviate() {
		return numComunicazioniArchiviate;
	}

	public int getNumComunicazioniArchiviateDaLeggere() {
		return numComunicazioniArchiviateDaLeggere;
	}

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}
	
	public Long getGenere() {
		return genere;
	}

	public void setGenere(Long genere) {
		this.genere = genere;
	}

	public String getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrderXml() {
		return orderXml;
	}

	public void setOrderXml(String orderXml) {
		this.orderXml = orderXml;
	}
	
	public String getDataLimiteModifiche() {
		return dataLimiteModifiche;
	}
	
	public void setDataLimiteModifiche(String dataLimiteModifiche) {
		this.dataLimiteModifiche = dataLimiteModifiche;
	}

	/**
	 * recupera il codice impresa associato all'utente
	 * @throws ApsException 
	 */
	private String getIdImpresa() throws ApsException {
		String codimp = (String) this.session.get(PortGareSystemConstants.SESSION_ID_IMPRESA);
		if(StringUtils.isEmpty(codimp)) {
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				codimp = this.bandiManager.getIdImpresa(userDetails.getUsername());
				this.session.put(PortGareSystemConstants.SESSION_ID_IMPRESA, codimp);	
			}
		}	
		return codimp;
	}
	
	/**
	 * crea la parte iniziale del nome del file composto da nazione e codfisc o part iva
	 * @return
	 */
	private String getFileNameImpresa() throws ApsException {
		String nsoImprFileName = (String) this.session.get(PortGareSystemConstants.SESSION_ID_NSO_FILE_NAME_IMPR);
		if(StringUtils.isEmpty(nsoImprFileName)) {
			UserDetails userDetails = this.getCurrentUser();
			if(userDetails == null || userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) throw new ApsException("Errore inaspettato durante l'interpretazione dei dati dell'impresa");
			DatiImpresaDocument datiImpresa = this.bandiManager.getDatiImpresa(userDetails.getUsername(), null);
			ImpresaAggiornabileType impresa = datiImpresa.getDatiImpresa().getImpresa();
			String codiceFiscale = StringUtils.trimToEmpty(impresa.getCodiceFiscale());
			String partitaIVA = StringUtils.trimToEmpty(impresa.getPartitaIVA());
			String nazione = StringUtils.trimToEmpty(impresa.getSedeLegale().getNazione());
			nsoImprFileName = StringUtils.substring(nazione, 0,2).toUpperCase().concat(StringUtils.isNotBlank(codiceFiscale)?codiceFiscale:partitaIVA);
			this.session.put(PortGareSystemConstants.SESSION_ID_NSO_FILE_NAME_IMPR, nsoImprFileName);
		}
		return nsoImprFileName;
		
	}
	

	/**
	 * Estrae il contratto e le comunicazioni scambiate con l'Amministrazione.
	 * 
	 * @return target struts
	 */
	public String view() {
		this.setTarget(SUCCESS);
		logger.debug("Called view with id: {}",this.id);
		try {
			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

				String codimp = this.getIdImpresa();
				
				this.dettaglioOrdine = this.ordiniManager.getOrderById(codimp, this.id);
				
				// applica il foglio di stile (XSLT)
				try {
					this.orderXml = this.ordiniManager.getOrderXmlByFileName(codimp, this.dettaglioOrdine.getFileName());
					if(StringUtils.isNotEmpty(this.orderXml)) { 
						this.orderXml = this.applyXsltToXml(this.orderXml);
					}
				} catch (Exception e) {
					this.orderXml = "";
					ApsSystemUtils.logThrowable(e, this, "view");
				}
				
				// leggi le comunicazioni
				if(StringUtils.isNotEmpty(this.dettaglioOrdine.getTender())) {
					StatisticheComunicazioniPersonaliType stat = this.bandiManager.getStatisticheComunicazioniPersonali(
									this.getCurrentUser().getUsername(), 
									this.dettaglioOrdine.getTender(),null,
									null);
					this.numComunicazioniRicevute = stat.getNumComunicazioniRicevute();
					this.numComunicazioniRicevuteDaLeggere = stat.getNumComunicazioniRicevuteDaLeggere();
					this.numComunicazioniArchiviate = stat.getNumComunicazioniArchiviate();
					this.numComunicazioniArchiviateDaLeggere = stat.getNumComunicazioniArchiviateDaLeggere();
					this.numComunicazioniInviate = stat.getNumComunicazioniInviate();
				}
				//this.setGenere(????); //DEFINIRE UN GENERE PER GLI ORDINI NSO!!!
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.dettaglioOrdine.getTender());
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA, this.genere);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * Accetta un ordine  
	 */
	public String accetta() {
		this.setTarget(SUCCESS);
		try {
			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				String codimp = this.getIdImpresa();
				
				Date dataLimiteOrdine = null;
				try {
					dataLimiteOrdine = sdfIta.parse(this.dataLimiteModifiche);
				} catch(ParseException e) {
					try {
						dataLimiteOrdine = sdfIta2.parse(this.dataLimiteModifiche);
					} catch (ParseException e1) {
						try {
							dataLimiteOrdine = sdfISO8601.parse(this.dataLimiteModifiche);
						} catch (ParseException e2) {
							throw new Exception(this.getText("LABEL_ERR_ORDER_LIMITDATE"));
						}
						
					}
				}
				
				if(dataLimiteOrdine == null ) {
					throw new Exception(this.getText("LABEL_ERR_ORDER_LIMITDATE"));
				}
				
				
				this.dettaglioOrdine = this.ordiniManager.getOrderById(codimp, this.id);
				String[] ssp = this.dettaglioOrdine.getReceiver().split(":");
				String acceptedOrder = "<urn:OrderResponse xmlns:urn=\"urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2\">" + 
						"    <urn1:CustomizationID xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">urn:fdc:peppol.eu:poacc:trns:order_response:3</urn1:CustomizationID>" + 
						"    <urn1:ProfileID xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">urn:fdc:peppol.eu:poacc:bis:ordering:3</urn1:ProfileID>" + 
						"    <urn1:OrderResponseCode xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">AP</urn1:OrderResponseCode>" + 
						"    <urn1:OrderReference xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">" + 
						"        <urn2:ID xmlns:urn2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"+this.dettaglioOrdine.getOrderCode()+"#"+this.dettaglioOrdine.getOrderDate()+"#"+this.dettaglioOrdine.getEndpoint()+"</urn2:ID>" +
						"    </urn1:OrderReference>" + 
						"    <urn1:SellerSupplierParty xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">" + 
						"        <urn1:Party>" + 
						"            <urn2:EndpointID schemeID=\""+ssp[0]+"\" xmlns:urn2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"+ssp[1]+"</urn2:EndpointID>" + 
						"        </urn1:Party>" + 
						"    </urn1:SellerSupplierParty>" + 
						"</urn:OrderResponse>";
				
				//String xml = this.ordiniManager.getOrderXmlByFileName(codimp, this.dettaglioOrdine.getFileName());
				//TODO generate response
				
				OrderRequestToNso orderReq = new OrderRequestToNso();
				orderReq.base64FileEncoded(Base64.encodeBase64String(acceptedOrder.getBytes()));
				//the file name will be changed on api as expected
				orderReq.fileName(this.getFileNameImpresa()+"_OZ_00001.xml");// change file Name
				orderReq.lastConfirmationDate(dataLimiteOrdine);

				this.ordiniManager.confirmOrderFO(
						codimp, 
						this.dettaglioOrdine.getEndpoint(), 
						this.dettaglioOrdine.getOrderCode(), 
						this.dettaglioOrdine.getOrderDate(), 
						orderReq);
				//reload from api beacuse of the status
				this.dettaglioOrdine = this.ordiniManager.getOrderById(codimp, this.id);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "accetta");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		return this.getTarget();
	}
	
	/**
	 * Rifiuta un ordine  
	 */
	public String rifiuta() {
		this.setTarget(SUCCESS);
		try {
			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				String codimp = this.getIdImpresa();
				
				this.dettaglioOrdine = this.ordiniManager.getOrderById(codimp, this.id);
				
				String[] ssp = this.dettaglioOrdine.getReceiver().split(":");
				String deniedOrder = "<urn:OrderResponse xmlns:urn=\"urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2\">" + 
						"    <urn1:CustomizationID xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">urn:fdc:peppol.eu:poacc:trns:order_response:3</urn1:CustomizationID>" + 
						"    <urn1:ProfileID xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">urn:fdc:peppol.eu:poacc:bis:ordering:3</urn1:ProfileID>" + 
						"    <urn1:OrderResponseCode xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">RE</urn1:OrderResponseCode>" + 
						"    <urn1:OrderReference xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">" + 
						"        <urn2:ID xmlns:urn2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"+this.dettaglioOrdine.getOrderCode()+"#"+this.dettaglioOrdine.getOrderDate()+"#"+this.dettaglioOrdine.getEndpoint()+"</urn2:ID>" +
						"    </urn1:OrderReference>" + 
						"    <urn1:SellerSupplierParty xmlns:urn1=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\">" + 
						"        <urn1:Party>" + 
						"            <urn2:EndpointID schemeID=\""+ssp[0]+"\" xmlns:urn2=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">"+ssp[1]+"</urn2:EndpointID>" + 
						"        </urn1:Party>" + 
						"    </urn1:SellerSupplierParty>" + 
						"</urn:OrderResponse>";
				
				OrderRequestToNso orderReq = new OrderRequestToNso();
				orderReq.base64FileEncoded(Base64.encodeBase64String(deniedOrder.getBytes()));
				//the file name will be changed on api as expected
				orderReq.fileName(this.getFileNameImpresa()+"_OZ_00001.xml");
				
				this.ordiniManager.cancelOrderFO(
						codimp, 
						this.dettaglioOrdine.getEndpoint(), 
						this.dettaglioOrdine.getOrderCode(), 
						this.dettaglioOrdine.getOrderDate(), 
						orderReq);
				//reload from api beacuse of the status
				this.dettaglioOrdine = this.ordiniManager.getOrderById(codimp, this.id);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "rifiuta");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}		
		return this.getTarget();
	}
		
	/**
	 * applica il foglio di stile XSLT all'xml e restituisci il frammento di html corrispondente 
	 * @throws UnsupportedEncodingException 
	 */
    private String applyXsltToXml(String xml)
        throws TransformerConfigurationException, TransformerException, UnsupportedEncodingException
    {
    	// recupera l'xslt in localizzato nella lingua corrente
    	String xsltFilename = "NSO - Stylesheet Ver_4_0.xslt";		// default (italiano)
    	Lang currentLang = this.getCurrentLang();
    	if(currentLang != null) {
    		xsltFilename = "NSO - Stylesheet Ver_4_0_" + currentLang.getCode() +  ".xslt";
    	}
    
    	// apri il foglio di stile XSLT
    	File xsl = new File(ServletActionContext
				.getServletContext()
				.getRealPath("/resources/xslt/") + xsltFilename);
    	javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsl);
    	
    	// inizializza lo stream di input (xml) da trasformare con il foglio di stile xslt
    	ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xmlStream);

        // inizializza lo stream di output (html)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(out);
    	
        // applica la trasformazione xslt al contenuto xml
        javax.xml.transform.TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
        javax.xml.transform.Transformer trans = transFact.newTransformer(xsltSource);
        trans.transform(xmlSource, result);
        
        // trasforma in html lo stream di output
        String html = "";
        if(out.size() > 0) {
            html = new String(out.toByteArray(), Charset.forName("UTF-8")); // StandardCharsets.UTF_8);
            
            // rimuovi la sezione "body" dal CSS nel frammento generato
            int i1 = html.indexOf("body");
            int i2 = i1 + 4;
            int n = 0;
            while(i2 < html.length()) {
            	String s = html.substring(i2, i2 + 1);
            	if("{".equals(s)) {
            		n++;
            	} else if("}".equals(s)) {
            		n--;
            		if(n <= 0) break;
            	}
            	i2++;
            }
            
            html = html.substring(0, i1) + html.substring(i2 + 1, html.length());
        }

	    return html;
    }

}

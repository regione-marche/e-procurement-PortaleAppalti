package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.eorders;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.maggioli.eldasoft.nso.client.invoker.ApiException;
import it.maggioli.eldasoft.nso.client.model.InvoiceData;
import it.maggioli.eldasoft.nso.client.model.InvoiceDraftKeeper;
import it.maggioli.eldasoft.nso.client.model.InvoiceDraftKeeper.StatoEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.AltriDatiGestionaliType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiBeniServiziType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiBolloType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiBolloType.BolloVirtualeEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiCassaPrevidenzialeType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiDDTType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiPagamentoType;
import it.maggioli.eldasoft.nso.invoice.client.model.DatiPagamentoType.CondizioniPagamentoEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DettaglioLineeType;
import it.maggioli.eldasoft.nso.invoice.client.model.DettaglioLineeType.NaturaEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DettaglioLineeType.RitenutaEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.DettaglioPagamentoType;
import it.maggioli.eldasoft.nso.invoice.client.model.FatturaElettronicaBodyType;
import it.maggioli.eldasoft.nso.invoice.client.model.FatturaElettronicaType;
import it.maggioli.eldasoft.nso.invoice.client.model.IndirizzoType;
import it.maggioli.eldasoft.nso.invoice.client.model.IscrizioneREAType;
import it.maggioli.eldasoft.nso.invoice.client.model.IscrizioneREAType.StatoLiquidazioneEnum;
import it.maggioli.eldasoft.nso.invoice.client.model.NsoWsFatture;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IIndirizzoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IndirizzoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.FattureManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Azione per la gestione delle operazioni a livello di dettaglio ordine
 * 
 * @author 
 * @since 
 */
public class FatturaAction extends EncodedDataAction implements SessionAware, ModelDriven<FatturaModel> {
	
	private static final long serialVersionUID = -8564702344327853135L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, Object> session;
	private FattureManager fattManager;
	private IBandiManager bandiManager;
	
	private Long id;
	private Long idFatt;
	private String orderCode;
	private FatturaElettronicaType fatturaElettronica;
	private int ambitoTerritoriale;
	private ArrayList<IIndirizzoImpresa> indirizzi;
	@Validate
	private FatturaModel model = new FatturaModel();
	private List<DettaglioLineeType> dettaglioLinee; 
	private DatiBeniServiziType dbs;
	private NsoWsFatture fatture;
	private InputStream inputStream;

	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String docFatturaDesc;
	private File docFattura;
	@Validate(EParamValidation.UNLIMITED_TEXT)
	private String docFatturaContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String docFatturaFileName;

	private byte[] fatturaXMLContent;
	@Validate(EParamValidation.FILE_NAME)
	private String fileName;
	private Boolean sendDisabled = Boolean.TRUE;
	private String warningInvioFattura = "";
	@Validate(EParamValidation.PROGRESSIVO_INVIO)
	private String progInvio;
	private SimpleDateFormat sdfIta = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat sdfIta2 = new SimpleDateFormat("dd/MM/yyyy");
	private SimpleDateFormat sdfISO8601 = new SimpleDateFormat("yyyy-MM-dd");
	
	public String getDocFatturaContentType() {
		return docFatturaContentType;
	}

	public void setDocFatturaContentType(String docFatturaContentType) {
		this.docFatturaContentType = docFatturaContentType;
	}

	public String getDocFatturaFileName() {
		return docFatturaFileName;
	}

	public void setDocFatturaFileName(String docFatturaFileName) {
		this.docFatturaFileName = docFatturaFileName;
	}

	public File getDocFattura() {
		return docFattura;
	}

	public void setDocFattura(File docFattura) {
		this.docFattura = docFattura;
	}

	public String getDocFatturaDesc() {
		return docFatturaDesc;
	}

	public void setDocFatturaDesc(String docFatturaDesc) {
		this.docFatturaDesc = docFatturaDesc;
	}

	public List<DettaglioLineeType> getDettaglioLinee() {
		return dettaglioLinee;
	}

	public void setDettaglioLinee(List<DettaglioLineeType> dettaglioLinee) {
		this.dettaglioLinee = dettaglioLinee;
	}

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdFatt() {
		return idFatt;
	}

	public void setIdFatt(Long idFatt) {
		this.idFatt = idFatt;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public void setFattManager(FattureManager fattManager) {
		this.fattManager = fattManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public ArrayList<IIndirizzoImpresa> getIndirizzi(){
		return this.indirizzi;
	}
	
	public FatturaElettronicaType getFatturaElettronica() {
		return fatturaElettronica;
	}

	public int getAmbitoTerritoriale() {
		return ambitoTerritoriale;
	}
	
	@Override
	public FatturaModel getModel() {
		return this.model;
	}

	public NsoWsFatture getFatture() {
		return fatture;
	}

	public void setFatture(NsoWsFatture fatture) {
		this.fatture = fatture;
	}

	public byte[] getFatturaXMLContent() {
		return fatturaXMLContent;
	}

	public void setFatturaXMLContent(byte[] fatturaXMLContent) {
		this.fatturaXMLContent = fatturaXMLContent;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public FatturaAction() {
		super();
		logger.debug("Build FatturaAction");
		logger.debug("FatturaAction this.getMaps(): [{}]",this.getMaps().keySet());
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public String cancel() {
		logger.debug("Called FatturaAction.cancel");
		return "cancelToOrder";
	}
	
	public String back() {
		logger.debug("Called FatturaAction.back");
		return "back";
	}
	
	public String view() {
		logger.debug("Called FatturaAction.view");
		this.setTarget(SUCCESS);
		return this.getTarget();
	}
	
	public String rigenera() {
		this.setTarget("reload");
		UserDetails userDetails = this.getCurrentUser();
		try {
			this.model = new FatturaModel();
			this.idFatt = this.fattManager.createFattura(this.getId(),userDetails.getUsername());
			this.fattManager.createDraft(this.id, this.idFatt, this.orderCode);
		} catch (ApiException e) {
			ApsSystemUtils.logThrowable(e, this, "rigenera");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(ERROR);
			return this.getTarget();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "rigenera");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(ERROR);
			return this.getTarget();
		}
		logger.debug("Called FatturaAction.rigenera with {}",this.getTarget());
		return this.getTarget();
	}

	public String crea() {
		logger.debug("Called FatturaAction.crea");
		this.setTarget(SUCCESS);
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,	"fatt-dg");
		logger.debug("id: {}",this.getId());
		logger.debug("orderCode: {}",this.getOrderCode());
		
		try {
			// 1. invoke nso api to find the others
			InvoiceDraftKeeper dd = this.fattManager.getLastDataForOrder(this.getId());
			logger.debug("InvoiceDraftKeeper: {}",dd);
			UserDetails userDetails = this.getCurrentUser();
			if(dd==null || StatoEnum.INVIATA.getValue().equals(dd.getStato().getValue())) {
				logger.debug("DraftData not found, create fattura from scratch");
				// 3. negative invoke creation API
				this.idFatt = this.fattManager.createFattura(this.getId(),userDetails.getUsername());
				logger.debug("Created fattura from scratch with id {}",idFatt);
				if(this.idFatt!=null) {
					this.fattManager.createDraft(this.id, this.idFatt, this.orderCode);
				}
			} else {
				this.idFatt = dd.getIdFattura();
			}
			logger.debug("CondizioniPagamentoEnum: {}",Arrays.asList(CondizioniPagamentoEnum.values()));
			logger.debug("TP01: {}",CondizioniPagamentoEnum.fromValue("TP01"));
			this.fatturaElettronica = this.fattManager.getDatiGeneraliFattura(idFatt);
			WizardDatiImpresaHelper datiImpresaHelper = getDatiImpresa(userDetails);
			this.ambitoTerritoriale = Integer.valueOf(datiImpresaHelper.getDatiPrincipaliImpresa().getAmbitoTerritoriale());
			logger.debug("getAmbitoTerritoriale: {}",this.ambitoTerritoriale);
			IndirizzoImpresaHelper indirizzoImpresa = this.transformToIndirizzoImpresaHelper(this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().getStabileOrganizzazione());
			logger.debug("indirizzoImpresa: {}",indirizzoImpresa);
			
			if(StringUtils.isEmpty(this.getModel().getStabileOrganizzazione())) {
				logger.debug("Reset of StabileOrganizzazione");
//				this.getModel().setStabileOrganizzazione(this.indirizzi.stream().map(el->el.toString()).filter(el->el.equals(indirizzoImpresa.toString())).findFirst().orElse(""));
				this.getModel().setStabileOrganizzazione("");
				for(IIndirizzoImpresa ind : this.indirizzi) {
					if(ind.toString().equals(indirizzoImpresa.toString())) {
						this.getModel().setStabileOrganizzazione(ind.toString());
						break;
					}
				}
				logger.debug("Form: {}",this.getModel());
			}
			logger.debug("TCAPRA: {}",datiImpresaHelper.getAltriDatiAnagraficiImpresa().getTipologiaCassaPrevidenza());
			model.setTcapre(StringUtils.trimToNull(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getTipologiaCassaPrevidenza()));
			
			
			
//			this.fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiSAL().get(0);
			if(!this.fatturaElettronica.getFatturaElettronicaBody().isEmpty()) {
				FatturaElettronicaBodyType febt = this.fatturaElettronica.getFatturaElettronicaBody().get(0);
				if(StringUtils.isEmpty(this.getModel().getNumero())) {
					this.getModel().setNumero(febt.getDatiGenerali().getDatiGeneraliDocumento().getNumero());
				}
				logger.debug("this.getModel().getDatiPagamento().isNull? {}",(this.getModel().getDatiPagamento()==null));
				if(this.getModel().getDatiPagamento()==null) {
					logger.debug("setDatiPagamento with {}",febt.getDatiPagamento());
					if(!febt.getDatiPagamento().isEmpty()) {
						this.getModel().setDatiPagamento(febt.getDatiPagamento());
						for(DettaglioPagamentoType dpt : this.getModel().getDatiPagamento().get(0).getDettaglioPagamento()) {
							if(StringUtils.isEmpty(this.getModel().getAbi()))
								this.getModel().setAbi(dpt.getAbi());
							if(StringUtils.isEmpty(this.getModel().getBic()))
								this.getModel().setBic(dpt.getBic());
							if(StringUtils.isEmpty(this.getModel().getCab()))
								this.getModel().setCab(dpt.getCab());
							if(StringUtils.isEmpty(this.getModel().getDataScadenzaPagamento()) && dpt.getDataScadenzaPagamento()!=null)
								this.getModel().setDataScadenzaPagamento(sdf.format(dpt.getDataScadenzaPagamento().toGregorianCalendar().getTime()));
							if(StringUtils.isEmpty(this.getModel().getIstitutoFinanziario()))
								this.getModel().setIstitutoFinanziario(dpt.getIstitutoFinanziario());
							if(StringUtils.isEmpty(this.getModel().getIban()))
								this.getModel().setIban(dpt.getIban());
						}
					} else {
						this.getModel()
										.setDatiPagamento(Collections.singletonList(new DatiPagamentoType()
																										.condizioniPagamento(CondizioniPagamentoEnum.TP01)
																										.dettaglioPagamento(Collections.singletonList(new DettaglioPagamentoType())
																								)));
					}
					logger.debug("this.getModel().getDatiPagamento(): {}",this.getModel().getDatiPagamento());
				}
				if(this.getModel().getStatoLiquidazione()==null) {
					IscrizioneREAType ireat = this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().getIscrizioneREA();
					if(ireat!=null && ireat.getStatoLiquidazione()!=null) {
						this.getModel().setStatoLiquidazione(ireat.getStatoLiquidazione().getValue());
					} else {
						this.getModel().setStatoLiquidazione(StatoLiquidazioneEnum.LN.getValue());
					}
				}
				
				if(StringUtils.isNotEmpty(model.getTcapre()) 
						&& febt.getDatiGenerali()!=null 
						&& febt.getDatiGenerali().getDatiGeneraliDocumento()!=null
						&& febt.getDatiGenerali().getDatiGeneraliDocumento().getDatiCassaPrevidenziale()!=null
						&& !febt.getDatiGenerali().getDatiGeneraliDocumento().getDatiCassaPrevidenziale().isEmpty()) {
					
					DatiCassaPrevidenzialeType datiCassaPrevidenzialeItem = febt.getDatiGenerali().getDatiGeneraliDocumento().getDatiCassaPrevidenziale().get(0);
					
					if(StringUtils.isEmpty(model.getDatCassAliquota()) && datiCassaPrevidenzialeItem.getAlCassa()!=null) {
						model.setDatCassAliquota(datiCassaPrevidenzialeItem.getAlCassa().toPlainString());
					}
					if(StringUtils.isEmpty(model.getDatCassImponibile()) && datiCassaPrevidenzialeItem.getImponibileCassa()!=null) {
						model.setDatCassImponibile(datiCassaPrevidenzialeItem.getImponibileCassa().toPlainString());
					}
					if(StringUtils.isEmpty(model.getDatCassAliquotaIva()) && datiCassaPrevidenzialeItem.getAliquotaIVA()!=null) {
						model.setDatCassAliquotaIva(datiCassaPrevidenzialeItem.getAliquotaIVA().toPlainString());
					}
					if(StringUtils.isEmpty(model.getDatCassNatura()) && datiCassaPrevidenzialeItem.getNatura()!=null) {
						model.setDatCassNatura(datiCassaPrevidenzialeItem.getNatura().getValue());
					}
					if(StringUtils.isEmpty(model.getDatCassRitenuta()) && datiCassaPrevidenzialeItem.getRitenuta()!=null) {
						model.setDatCassRitenuta(datiCassaPrevidenzialeItem.getRitenuta().getValue());
					}
					if(StringUtils.isEmpty(model.getDatCassTipoCassa()) && datiCassaPrevidenzialeItem.getTipoCassa()!=null) {
						model.setDatCassTipoCassa(datiCassaPrevidenzialeItem.getTipoCassa().getValue());
					}
				}
				
				if(model.getTipoDocumento()==null && febt.getDatiGenerali().getDatiGeneraliDocumento().getTipoDocumento()!=null) {
					model.setTipoDocumento(febt.getDatiGenerali().getDatiGeneraliDocumento().getTipoDocumento());
				}
				
				if(StringUtils.isEmpty(model.getData()) && febt.getDatiGenerali().getDatiGeneraliDocumento().getData()!=null) {
					model.setData(sdf.format(febt.getDatiGenerali().getDatiGeneraliDocumento().getData().toGregorianCalendar().getTime()));
				}
				if(StringUtils.isEmpty(this.getModel().getDdtnum()) && febt.getDatiGenerali().getDatiDDT()!=null && !febt.getDatiGenerali().getDatiDDT().isEmpty()){
					DatiDDTType ddtt = febt.getDatiGenerali().getDatiDDT().get(0);
					this.getModel().setDdtnum(ddtt.getNumeroDDT());
					if(ddtt.getDataDDT()!=null) {
						this.getModel().setDdtdata(sdf.format(ddtt.getDataDDT().toGregorianCalendar().getTime()));
					}
					if(ddtt.getRiferimentoNumeroLinea()!=null && !ddtt.getRiferimentoNumeroLinea().isEmpty()) {
						this.getModel().setDdtrifnum(ddtt.getRiferimentoNumeroLinea().get(0).toString());
					}
				}
			}
			
			this.getModel().setBolloVirtuale(BolloVirtualeEnum.SI.getValue());
			logger.debug("this.getModel().getDatiPagamento: {}",this.getModel().getDatiPagamento());
		} catch (ApiException e) {
			ApsSystemUtils.logThrowable(e, this, "crea");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "crea");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(ERROR);
		}
		
		return this.getTarget();
	}
	
	private IndirizzoImpresaHelper transformToIndirizzoImpresaHelper(IndirizzoType so) {
		logger.debug("Found IndirizzoType {}",so);
		logger.debug("FatturaAction this.getMaps(): [{}]",this.getMaps().keySet());
		IndirizzoImpresaHelper help = new IndirizzoImpresaHelper();
		if(so!=null) {
			help.setCap(so.getCap());
			help.setComune(so.getComune());
			help.setIndirizzo(so.getIndirizzo());
			help.setNazione("");
			LinkedHashMap<String, String> nazioni = this.getMaps().get(InterceptorEncodedData.LISTA_NAZIONI_CODIFICATE);
			for(Entry<String, String> en : nazioni.entrySet()) {
				if(en.getValue().equalsIgnoreCase(so.getNazione())) {
					help.setNazione(en.getKey());
					break;
				}
			}
//			help.setNazione(this.getMaps().get(InterceptorEncodedData.LISTA_NAZIONI_CODIFICATE).entrySet().stream()
//																								.filter(en->en.getValue().equalsIgnoreCase(so.getNazione()))
//																								.map(en->en.getKey())
//																								.findFirst().orElse(""));
			help.setNumCivico(so.getNumeroCivico());
			help.setProvincia(so.getProvincia());
		}
		return help;
	}

	private WizardDatiImpresaHelper getDatiImpresa(UserDetails userDetails) throws ApsException {
		WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		if(datiImpresaHelper==null) {
			DatiImpresaDocument datiImpresa = bandiManager.getDatiImpresa(userDetails.getUsername(),null);
			datiImpresaHelper = new WizardDatiImpresaHelper(datiImpresa.getDatiImpresa());
		}
		this.indirizzi = datiImpresaHelper.getIndirizziImpresa();
		return datiImpresaHelper;
	}
	
	public String next() throws ApsException {
		logger.debug("Called next");
		return saveHeader();
	}
	
	public String saveHeader() throws ApsException {
		logger.debug("Called FatturaAction.saveHeader");
		logger.debug("Form: {}",this.getModel());
		
		try {
			this.setTarget(this.crea());
			logger.debug("Form: {}",this.getModel());
			
			if(StringUtils.isEmpty(this.getModel().getNumero())
					|| !StringUtils.isAlphanumeric(this.getModel().getNumero())
					|| !StringUtils.containsAny(this.getModel().getNumero(),"0123456789")) {
				this.addFieldError("numero", this.getI18nLabel("LABEL_ERR_FATT_CODICEFATT"));
				this.setTarget(ERROR);
				return this.getTarget();
			} else {
				this.fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().setNumero(this.getModel().getNumero());
			}
			
			if(StringUtils.isEmpty(model.getData())) {
				this.addFieldError("data", this.getI18nLabel("LABEL_ERR_FATT_DATAFATT"));
				this.setTarget(ERROR);
				return this.getTarget();
			} else {
				Date dataFatt = null;
				try {
					dataFatt = sdfIta.parse(model.getData());
				} catch(ParseException e) {
					try {
						dataFatt = sdfIta2.parse(model.getData());
					} catch (ParseException e1) {
						try {
							dataFatt = sdfISO8601.parse(model.getData());
						} catch (ParseException e2) {
							this.addFieldError("data", this.getI18nLabel("LABEL_ERR_FATT_DATAFATT"));
							this.setTarget(ERROR);
							return this.getTarget();
						}
						
					}
				}
				
				if(dataFatt == null ) {
					this.addFieldError("data", this.getI18nLabel("LABEL_ERR_FATT_DATAFATT"));
					this.setTarget(ERROR);
					return this.getTarget();
				} else {
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(dataFatt);
					XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
					this.fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().setData(date2);
				}
			}
			
			if(StringUtils.isEmpty(model.getDataScadenzaPagamento())) {
				this.addFieldError("dataScadenzaPagamento", this.getI18nLabel("LABEL_ERR_FATT_DATASCADPAG"));
				this.setTarget(ERROR);
				return this.getTarget();
			}
			if(model.getTipoDocumento()==null) {
				this.addFieldError("tipoDocumento", this.getI18nLabel("LABEL_ERR_FATT_TIPODOC"));
				this.setTarget(ERROR);
				return this.getTarget();
			} else {
				this.fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().setTipoDocumento(model.getTipoDocumento());
			}
			
			
			this.fatturaElettronica.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().setDatiBollo(new DatiBolloType().bolloVirtuale(BolloVirtualeEnum.SI));
			
			if(StringUtils.isNotEmpty(this.getModel().getStabileOrganizzazione())) {
				IndirizzoType it = this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().getStabileOrganizzazione();
				if(it==null) {
					it = new IndirizzoType();
				}
				logger.debug("{}",this.getModel().getStabileOrganizzazione());
//				this.indirizzi.stream().forEach(el->logger.debug("{}",el));
//				Optional<IIndirizzoImpresa> opInd = this.indirizzi.parallelStream().filter(el->el.toString().equals(this.getModel().getStabileOrganizzazione())).findFirst();
//				logger.debug("Found impr with {}:{}",this.getModel().getStabileOrganizzazione(),opInd.isPresent());
//				if(opInd.isPresent()) {
//					IIndirizzoImpresa impr = opInd.get();
//					it.setCap(impr.getCap());
//					it.setComune(impr.getComune());
//					it.setIndirizzo(impr.getIndirizzo());
//					String codiceNazione = this.getMaps().get(InterceptorEncodedData.LISTA_NAZIONI_CODIFICATE).get(impr.getNazione());
//					it.setNazione(codiceNazione);
//					it.setNumeroCivico(impr.getNumCivico());
//					it.setProvincia(impr.getProvincia());
//					this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().setStabileOrganizzazione(it);
//				}
				IIndirizzoImpresa impr = null;
				for(IIndirizzoImpresa el : this.indirizzi) {
					if(el.toString().equals(this.getModel().getStabileOrganizzazione())) {
						impr = el;
						break;
					}
				}
				if(impr!=null) {
					it.setCap(impr.getCap());
					it.setComune(impr.getComune());
					it.setIndirizzo(impr.getIndirizzo());
					String codiceNazione = this.getMaps().get(InterceptorEncodedData.LISTA_NAZIONI_CODIFICATE).get(impr.getNazione());
					it.setNazione(codiceNazione);
					it.setNumeroCivico(impr.getNumCivico());
					if(StringUtils.isEmpty(impr.getNumCivico())) {
						it.setNumeroCivico("s.n.c.");
					}
					it.setProvincia(impr.getProvincia());
					this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().setStabileOrganizzazione(it);
				}
			}
			
			if(this.getModel().getStatoLiquidazione()!=null) {
				logger.debug("this.getModel().getStatoLiquidazione(): {}",this.getModel().getStatoLiquidazione());
				IscrizioneREAType ireat = this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().getIscrizioneREA();
				if(ireat == null) {
					ireat = new IscrizioneREAType();
					this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore().setIscrizioneREA(ireat);
					logger.debug("CedentePrestatore: {}",this.fatturaElettronica.getFatturaElettronicaHeader().getCedentePrestatore());
				}
				ireat.setStatoLiquidazione(this.getModel().getStatoLiquidazione());
			}
			// iniziamo ad inserire i dati direi di inserire direttamente quelli da UI a prescindere dai dati presenti a DB !!!
			// 2.4   <DatiPagamento>
			if(this.getModel().getCondPag()!=null && this.getModel().getCondPag().length>0) {
				logger.debug("this.getModel().getCondPag(): {}",this.getModel().getCondPag().length);
				logger.debug("this.fatturaElettronica.getFatturaElettronicaBody().isEmpty(): {}",this.fatturaElettronica.getFatturaElettronicaBody().isEmpty());
				if(!this.fatturaElettronica.getFatturaElettronicaBody().isEmpty()) {
					FatturaElettronicaBodyType febt = this.fatturaElettronica.getFatturaElettronicaBody().get(0);
					for(int i = 0; i< this.getModel().getCondPag().length;i++) {
						// 2.4.1   <CondizioniPagamento>
						logger.debug("FatturaElettronicaBodyType: {}",febt);
						logger.debug("this.getModel().getCondPag()[i]: {}",this.getModel().getCondPag()[i]);
						logger.debug("CondizioniPagamentoEnum.fromValue: {}",CondizioniPagamentoEnum.fromValue(this.getModel().getCondPag()[i]));
						DatiPagamentoType dt = new DatiPagamentoType().condizioniPagamento(CondizioniPagamentoEnum.fromValue(this.getModel().getCondPag()[i]));
						DettaglioPagamentoType dettaglioPagamento = new DettaglioPagamentoType();
						dettaglioPagamento.setAbi(this.getModel().getAbi());
						dettaglioPagamento.setBic(this.getModel().getBic());
						dettaglioPagamento.setCab(this.getModel().getCab());
						try {
							
							Date dataScadFatt = null;
							try {
								dataScadFatt = sdfIta.parse(model.getDataScadenzaPagamento());
							} catch(ParseException e) {
								try {
									dataScadFatt = sdfIta2.parse(model.getDataScadenzaPagamento());
								} catch (ParseException e1) {
									try {
										dataScadFatt = sdfISO8601.parse(model.getDataScadenzaPagamento());
									} catch (ParseException e2) {
										this.addFieldError("dataScadenzaPagamento", this.getI18nLabel("LABEL_ERR_FATT_DATASCADPAG"));
										this.setTarget(ERROR);
										return this.getTarget();
									}
									
								}
							}
							GregorianCalendar gcal = new GregorianCalendar();
							gcal.setTime(dataScadFatt);
							XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
							dettaglioPagamento.setDataScadenzaPagamento(cal);
						} catch(Exception e) {
							logger.warn("Impossible add DataScadenzaPagamento: {}",e.getMessage());
						}
						dettaglioPagamento.setIban(this.getModel().getIban());
						dettaglioPagamento.setIstitutoFinanziario(this.getModel().getIstitutoFinanziario());
						
						
						// 2.4.2   <DettaglioPagamento>
						dt.setDettaglioPagamento(Collections.singletonList(dettaglioPagamento));
						logger.debug("DatiPagamentoType: {}",dt);
						febt.setDatiPagamento(Collections.singletonList(dt));
						
						logger.debug("FatturaElettronicaBodyType: {}",febt);
					}
					this.getModel().setDatiPagamento(febt.getDatiPagamento());
				}
			}
			if(StringUtils.isNotEmpty(this.getModel().getTcapre())) {
				//TODO dati cassa previdenziale
				FatturaElettronicaBodyType febt = this.fatturaElettronica.getFatturaElettronicaBody().get(0);
				DatiCassaPrevidenzialeType datiCassaPrevidenzialeItem = new DatiCassaPrevidenzialeType();
				
				if(StringUtils.isNotEmpty(model.getDatCassAliquota())) {
//					BigDecimal bd = BigDecimal.valueOf(Double.valueOf(model.getDatCassAliquota()));
					datiCassaPrevidenzialeItem.setAlCassa(BigDecimal.valueOf(Double.valueOf(model.getDatCassAliquota())).setScale(0,RoundingMode.HALF_UP));
				}
				if(StringUtils.isNotEmpty(model.getDatCassImponibile())) {
//					BigDecimal bd = BigDecimal.valueOf(Double.valueOf(model.getDatCassAliquota()));
					datiCassaPrevidenzialeItem.setImponibileCassa(BigDecimal.valueOf(Double.valueOf(model.getDatCassImponibile())).setScale(0,RoundingMode.HALF_UP));
				}
				if(StringUtils.isNotEmpty(model.getDatCassAliquotaIva())) {
//					BigDecimal bd = BigDecimal.valueOf(Double.valueOf(model.getDatCassAliquota()));
					datiCassaPrevidenzialeItem.setAliquotaIVA(BigDecimal.valueOf(Double.valueOf(model.getDatCassAliquotaIva())).setScale(0,RoundingMode.HALF_UP));
				}
				if(StringUtils.isNotEmpty(model.getDatCassNatura())) {
					datiCassaPrevidenzialeItem.natura(DatiCassaPrevidenzialeType.NaturaEnum.fromValue(model.getDatCassNatura()));
				}
				if(StringUtils.isNotEmpty(model.getDatCassRitenuta())) {
					datiCassaPrevidenzialeItem.setRitenuta(DatiCassaPrevidenzialeType.RitenutaEnum.fromValue(model.getDatCassRitenuta()));
				}
				if(StringUtils.isNotEmpty(model.getDatCassTipoCassa())) {
					datiCassaPrevidenzialeItem.setTipoCassa(DatiCassaPrevidenzialeType.TipoCassaEnum.fromValue(model.getDatCassTipoCassa()));
				}
				febt.getDatiGenerali().getDatiGeneraliDocumento().addDatiCassaPrevidenzialeItem(datiCassaPrevidenzialeItem );
			}
			
			if(StringUtils.isNotEmpty(this.getModel().getDdtnum())) {
				FatturaElettronicaBodyType febt = this.fatturaElettronica.getFatturaElettronicaBody().get(0);
				DatiDDTType datiDDTItem = null;
				if(febt.getDatiGenerali().getDatiDDT()==null || febt.getDatiGenerali().getDatiDDT().isEmpty()) {
					febt.getDatiGenerali().addDatiDDTItem(new DatiDDTType() );
				}
				datiDDTItem = febt.getDatiGenerali().getDatiDDT().get(0);
				datiDDTItem.setNumeroDDT(this.getModel().getDdtnum());
				if(this.getModel().getDdtdata()!=null) {
					try {
						
						Date dataDDT;
						try {
							dataDDT = sdfIta.parse(model.getDdtdata());
						} catch(ParseException e) {
							try {
								dataDDT = sdfIta2.parse(model.getDdtdata());
							} catch (ParseException e1) {
								try {
									dataDDT = sdfISO8601.parse(model.getDdtdata());
								} catch (ParseException e2) {
									this.addFieldError("dataScadenzaPagamento", this.getI18nLabel("LABEL_ERR_FATT_DDTDATA"));
									this.setTarget(ERROR);
									return this.getTarget();
								}
								
							}
						}
						if(dataDDT == null ) {
							this.addFieldError("data", this.getI18nLabel("LABEL_ERR_FATT_DDTDATA"));
							this.setTarget(ERROR);
							return this.getTarget();
						} else {
							GregorianCalendar gcal = new GregorianCalendar();
							gcal.setTime(dataDDT);
							XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
							datiDDTItem.setDataDDT(cal);
						}
					} catch(Exception e) {
						logger.warn("Impossible add DataScadenzaPagamento: {}",e.getMessage());
					}
				}
				if(this.getModel().getDdtrifnum()!=null && StringUtils.isNumeric(this.getModel().getDdtrifnum())) {
					datiDDTItem.setRiferimentoNumeroLinea(Collections.singletonList(Integer.valueOf(this.getModel().getDdtrifnum())));
				}
					
			}
			
			logger.debug("SAVE-PRE: {}",this.fatturaElettronica);
			this.fattManager.updateDatiGeneraliFattura(this.idFatt, this.fatturaElettronica);
			//no sense to reload data if page and action will be changed			
//			this.setTarget(this.crea());
			logger.debug("SAVE-POST: {}",this.fatturaElettronica);
			logger.debug("Form: {}",this.getModel());
		} catch (ApiException e) {
			ApsSystemUtils.logThrowable(e, this, "saveHeader");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(ERROR);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "saveHeader");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(ERROR);
		}
		
		
		return this.getTarget();
	}
	
	public String elaboraLinee() throws ApsException {
		logger.debug("elaboraLinee for {}",this.getIdFatt());
		logger.debug("elaboraLinee for orderCode: {}",this.getOrderCode());
		this.setTarget(SUCCESS);
		try {
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,	"fatt-dl");
			this.dbs = this.fattManager.getDatiLinee(this.getIdFatt());
			this.setDettaglioLinee(this.dbs.getDettaglioLinee());
		} catch (Throwable t) {
			logger.error("Error {}",t.getMessage());
			ApsSystemUtils.logThrowable(t, this, "elaboraLinee");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(ERROR);
		}
		return this.getTarget();
	}
	
	
	
	public String saveLinee() throws ApsException {
		logger.debug("saveLinee");
		this.setTarget(SUCCESS);
		if(ERROR.equals(this.elaboraLinee())) return ERROR;
		try {
		List<Integer> numberOfLines = new ArrayList<Integer>();
		for(int i=0;i<this.getParameters().get("numeroLinea").length;i++) {
			numberOfLines.add(Integer.valueOf(this.getParameters().get("numeroLinea")[i]));
		}
		List<DettaglioLineeType> dettaglioLineeFiltered = new LinkedList<DettaglioLineeType>();
		for(DettaglioLineeType el : this.getDettaglioLinee()) {
			if(numberOfLines.contains(el.getNumeroLinea())) {
				int index = numberOfLines.indexOf(el.getNumeroLinea());
				if(el.getAltriDatiGestionali()!=null) {
					el.getAltriDatiGestionali().clear();
				}
				// change data
				if(StringUtils.isNotEmpty(this.getParameters().get("quantita")[index])) {
					Double quantita = Double.valueOf(this.getParameters().get("quantita")[index]);
					BigDecimal bd = BigDecimal.valueOf(quantita);
					bd.setScale(2,RoundingMode.HALF_UP);
					el.setQuantita(bd);
				}
				if(StringUtils.isNotEmpty(this.getParameters().get("prezzoUnitario")[index])) {
					Double prezzoUnitario = Double.valueOf(this.getParameters().get("prezzoUnitario")[index]);
					BigDecimal bd = BigDecimal.valueOf(prezzoUnitario);
					bd.setScale(2,RoundingMode.HALF_UP);
					el.setPrezzoUnitario(bd);
				}
				el.setRitenuta(null);//for reset
				if(StringUtils.isNotEmpty(this.getParameters().get("ritenuta")[index])) {
					el.setRitenuta(RitenutaEnum.SI);
				}
				if(StringUtils.isNotEmpty(this.getParameters().get("natura")[index])) {
					String natura = this.getParameters().get("natura")[index];
					el.setNatura(NaturaEnum.fromValue(natura));
				}
				if(StringUtils.isNotEmpty(this.getParameters().get("esigibilitaIva")[index])) {
					AltriDatiGestionaliType e = new AltriDatiGestionaliType();
					e.setRiferimentoTesto(this.getParameters().get("esigibilitaIva")[index]);
					e.setTipoDato("Esig.IVA");
					el.addAltriDatiGestionaliItem(e);
				}
				if(StringUtils.isNotEmpty(this.getParameters().get("riferimentoNormativo")[index])) {
					AltriDatiGestionaliType e = new AltriDatiGestionaliType();
					e.setRiferimentoTesto(this.getParameters().get("riferimentoNormativo")[index]);
					e.setTipoDato("Rif.Norm.");
					el.addAltriDatiGestionaliItem(e);
				}
				dettaglioLineeFiltered.add(el);
			}
		}
//		List<DettaglioLineeType> dettaglioLineeFiltered = this.getDettaglioLinee().parallelStream()
//																.filter(el->numberOfLines.contains(el.getNumeroLinea()))
//																//TODO modifica i dati
//																.map(el->{
//																	// get index
//																	int index = numberOfLines.indexOf(el.getNumeroLinea());
//																	if(el.getAltriDatiGestionali()!=null) {
//																		el.getAltriDatiGestionali().clear();
//																	}
//																	// change data
//																	if(StringUtils.isNotEmpty(this.getParameters().get("quantita")[index])) {
//																		Double quantita = Double.valueOf(this.getParameters().get("quantita")[index]);
//																		el.setQuantita(BigDecimal.valueOf(quantita));
//																	}
//																	if(StringUtils.isNotEmpty(this.getParameters().get("prezzoUnitario")[index])) {
//																		Double prezzoUnitario = Double.valueOf(this.getParameters().get("prezzoUnitario")[index]);
//																		el.setPrezzoUnitario(BigDecimal.valueOf(prezzoUnitario));
//																	}
//																	el.setRitenuta(null);//for reset
//																	if(StringUtils.isNotEmpty(this.getParameters().get("ritenuta")[index])) {
//																		el.setRitenuta(RitenutaEnum.SI);
//																	}
//																	if(StringUtils.isNotEmpty(this.getParameters().get("natura")[index])) {
//																		String natura = this.getParameters().get("natura")[index];
//																		el.setNatura(NaturaEnum.fromValue(natura));
//																		logger.debug("Natura: {}-> {}:{}",natura,el.getNatura().name(),el.getNatura().getValue());
//																	}
//																	if(StringUtils.isNotEmpty(this.getParameters().get("esigibilitaIva")[index])) {
//																		AltriDatiGestionaliType e = new AltriDatiGestionaliType();
//																		e.setRiferimentoTesto(this.getParameters().get("esigibilitaIva")[index]);
//																		e.setTipoDato("Esig.IVA");
//																		el.addAltriDatiGestionaliItem(e);
//																	}
//																	if(StringUtils.isNotEmpty(this.getParameters().get("riferimentoNormativo")[index])) {
//																		AltriDatiGestionaliType e = new AltriDatiGestionaliType();
//																		e.setRiferimentoTesto(this.getParameters().get("riferimentoNormativo")[index]);
//																		e.setTipoDato("Rif.Norm.");
//																		el.addAltriDatiGestionaliItem(e);
//																	}
//																	return el;
//																})
//																.collect(Collectors.toList());
//		dettaglioLineeFiltered.forEach(el->logger.debug("{}",el));
		// send response

			this.dbs.setDettaglioLinee(dettaglioLineeFiltered);
			this.fattManager.updateLinee(idFatt, this.dbs);
		} catch (Exception e1) {
			logger.error("Error on update linee",e1);
			ApsSystemUtils.logThrowable(e1, this, "elaboraLinee");
			ExceptionUtils.manageExceptionError(e1, this);
		}
		if(ERROR.equals(this.elaboraLinee())) return ERROR;
		return this.getTarget();
	}
	
	public String last() {
		logger.debug("Called last");
		return "last";
	}

	public String riepilogo() {
		return riepilogo("sdi");
	}
	
	@SuppressWarnings("finally")
	private String riepilogo(String type) {
		this.setTarget(SUCCESS);
		logger.debug("Called riepilogo with type ({})",type);
		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,	"fine");
		try {
			logger.debug("Called riepilogo.getDatiFatturaById");
			this.fatture = this.fattManager.getDatiFatturaById(this.idFatt,type);
			this.fatturaXMLContent = this.fatture.getPlainFileContents();
			this.fileName = this.getFatture().getPlainFileName();
			logger.debug("Called riepilogo.getDatiFatturaById");
		} catch (ApiException e) {
			logger.error("Error {}",e);
			this.setTarget(ERROR);
		} catch (Exception e) {
			logger.error("Error {}",e);
			this.setTarget(ERROR);
		} finally {
			logger.debug("Terminato riepilogo: {}",this.getTarget());
			return this.getTarget();
		}
	}
	
    public String getFileName() {
        return fileName;
    }
	
	public String download() {
		try {
			logger.debug("id: {}",id);
			logger.debug("idFatt: {}",idFatt);
			logger.debug("orderCode: {}",orderCode);
			this.riepilogo("ubl");
			this.setTarget("export");
			logger.debug("this.fatture.getProgressivoInvio(): {}",this.fatture.getProgressivoInvio());
			//TODO call the action to create the "DOWNLOAD" element
			this.fattManager.createDraftDownload(this.id, this.idFatt, this.orderCode, this.fatture.getProgressivoInvio());
			this.fileName = this.getFatture().getPlainFileName();
			this.inputStream = new ByteArrayInputStream(this.fatturaXMLContent);
			logger.debug("download finished [{}]",this.fileName);
		} catch(Exception e) {
			logger.error("Error on download {}",e);
			this.setTarget(ERROR);
		}
		logger.debug("download: {}",this.getTarget());
		return this.getTarget();
	}
	
	public String upload() {
		logger.debug("FatturaAction.upload - START");
		//this.setTarget(SUCCESS);
		this.setTarget("successUpload");
		try {
			logger.debug("{}",this.docFattura);
			if(this.docFattura != null) {
				//TODO unpack data from p7m
				if(this.docFatturaFileName.endsWith("p7m") || this.docFatturaFileName.endsWith("xml")) {
					Map<String,String> res = new HashMap<String,String>();
					InputStream inputFile = null;
					if(this.docFatturaFileName.endsWith("p7m")) {
						byte[] content = this.getContenutoDocumentoFirmato(docFattura, docFatturaFileName, null);
						if(content != null) {
							inputFile = new ByteArrayInputStream(content);
							res.putAll(verificaXML(inputFile));
						} else {
							this.warningInvioFattura = "Impossibile aprire il file ";
							//TODO send error message 
							//and clear memory
							this.docFattura = null;
							System.gc();
							return this.getTarget();
						}
					} else {
						inputFile = new FileInputStream(this.docFattura);
						res.putAll(verificaXML(inputFile));
					}
					
					logger.debug("VerificaXML {}",res.keySet());
					if(res.size()!=2) {
						logger.warn("Impossible find something in the XML: {}",res.keySet());
						this.warningInvioFattura = "WARN_FATT_NO_DATA";
						return this.getTarget();
					}
					
					
					if(!res.get("codOrd").equals(this.orderCode)) {
						logger.warn("The order inside the XML [{}]is not the one in page: {}",res.get("codOrd"),this.orderCode);
						this.warningInvioFattura = "WARN_FATT_NO_MATCHING_ORDER";
						//TODO send error message 
						//and clear memory
						this.docFattura = null;
						System.gc();
						return this.getTarget();
					}
					
					//imposto la variabile progInvio
					this.progInvio = res.get("progInvio");
					// send request to check data to API and print out result
					try {
						this.fattManager.checkFattura(this.orderCode, this.id, res.get("progInvio"));
					} catch(ApiException ex) {
						if(ex.getCode()==403) {
							logger.warn("Not Allowed to do SEND");
							this.warningInvioFattura = "WARN_FATT_NO_SEND";
							//TODO send error message
							//and clear memory
							this.docFattura = null;
							System.gc();
							return this.getTarget();
						}
						this.warningInvioFattura = "Fattura NON generata da sistema, si concede magnanimamente comunque di inviarla";
					}
					this.warningInvioFattura = "WARN_FATT_SEND";
					this.sendDisabled=Boolean.FALSE;
					//TODO if OK save file in Session and print out warning
					this.session.put(PortGareSystemConstants.SESSION_ID_NSO_FILE_SDI,	Files.readAllBytes(this.docFattura.toPath()));
					this.session.put(PortGareSystemConstants.SESSION_ID_NSO_FILENAME_SDI,	this.docFatturaFileName);
				} else  {
//					this.warningInvioFattura = "Caricato file non corretto, si accettano esclusivamente file con estensione .p7m o .xml";
					this.warningInvioFattura = "WARN_FATT_FILEEXT";
					this.docFattura = null;
					System.gc();
					return this.getTarget();
				}
			}
		} catch(Exception e) {
			logger.error("Error on File Upload: {}",e.getMessage(),e);
			ApsSystemUtils.logThrowable(e, this, "upload");
			ExceptionUtils.manageExceptionError(e, this);
			this.docFattura = null;
			System.gc();
			return this.getTarget();
		}
		logger.debug("FatturaAction.upload  - FINISH [{}]",this.getTarget());
		return this.getTarget();
	}
	
	public String inviaFattura() {
		this.setTarget(SUCCESS);
		logger.debug("Called inviaFattura");
		logger.debug("{}:{}",PortGareSystemConstants.SESSION_ID_NSO_FILENAME_SDI,this.session.get(PortGareSystemConstants.SESSION_ID_NSO_FILENAME_SDI));
		logger.debug("{} is present:{}",PortGareSystemConstants.SESSION_ID_NSO_FILE_SDI,(this.session.get(PortGareSystemConstants.SESSION_ID_NSO_FILE_SDI)!=null));
		try {
			InvoiceDraftKeeper dd = this.fattManager.getLastDataForOrder(this.getId());
			logger.debug("Obtained InvoiceDraftKeeper {}",dd);
			byte[]  inv = (byte[]) this.session.get(PortGareSystemConstants.SESSION_ID_NSO_FILE_SDI);

			InvoiceData invoiceData = new InvoiceData();
			invoiceData.setBase64FileEncoded(Base64.encodeBase64String(inv));
			invoiceData.setCodOrd(this.orderCode);
			invoiceData.setFileName(String.valueOf(this.session.get(PortGareSystemConstants.SESSION_ID_NSO_FILENAME_SDI)));
			if(dd!=null && dd.getProgInvio()!=null && dd.getProgInvio().equals(this.progInvio)) {
				invoiceData.setIdFattura(dd.getIdFattura());
			}
			invoiceData.setIdOrdine(this.id);
			invoiceData.setProgInvio(this.progInvio);
			logger.debug("Invoice data TRY");
			this.fattManager.inviaFattura(invoiceData );
			logger.debug("Invoice data SENT");
			this.warningInvioFattura = "WARN_FATT_SENT";
		} catch (ApiException e) {
			logger.error("Impossible send data",e);
			ApsSystemUtils.logThrowable(e, this, "inviaFattura");
			ExceptionUtils.manageExceptionError(e, this);
			this.warningInvioFattura = "WARN_FATT_SENTERR";
		} catch(Exception e) {
			logger.error("Impossible send data",e);
			ApsSystemUtils.logThrowable(e, this, "inviaFattura");
			ExceptionUtils.manageExceptionError(e, this);
			this.warningInvioFattura = "WARN_FATT_SENTERR";
		}
		//TODO blank data
		this.docFatturaFileName = null;
		this.session.remove(PortGareSystemConstants.SESSION_ID_NSO_FILENAME_SDI);
		this.session.remove(PortGareSystemConstants.SESSION_ID_NSO_FILE_SDI);
		
		logger.debug("Called inviaFattura -> {}",this.getTarget());
		return this.getTarget();
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private Map<String,String> verificaXML(final File xmlFile) {
		FileInputStream fileIS = null;
		logger.debug("Verifica di {}",xmlFile.getName());
		try {
			fileIS = new FileInputStream(xmlFile);
			return  verificaXML(fileIS);
		} catch (FileNotFoundException e) {
			logger.error("Exception.",e);
		}
		return Collections.emptyMap();
	}
	
	private Map<String,String> verificaXML(final InputStream is) {
		Map<String,String> res = new HashMap<String,String>();
		try {
			//Parse XML file
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setNamespaceAware(true);
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(is);
	         
	        //Get XPath expression
//	        XPathFactory xpathfactory = XPathFactory.newInstance();
//	        XPath xpath = xpathfactory.newXPath();
//	        xpath.setNamespaceContext(new NamespaceResolver(doc));
//	        String expression = "/ns3:FatturaElettronica/FatturaElettronicaBody/DatiGenerali/DatiOrdineAcquisto/IdDocumento";
//	        String codOrd = extractXpathInfoFromDocument(doc, xpath, expression);
//	        if(StringUtils.isNotEmpty(codOrd)) res.put("codOrd", codOrd);
//	        expression = "/ns3:FatturaElettronica/FatturaElettronicaHeader/DatiTrasmissione/ProgressivoInvio";
//	        String progInvio = extractXpathInfoFromDocument(doc, xpath, expression);
//	        if(StringUtils.isNotEmpty(progInvio)) res.put("progInvio", progInvio);
//	        logger.debug("Found: {} with {}",codOrd, progInvio);
	        
			String codOrd = extractElemInfoFromDocument(doc, "OrderReference","ID");
			if(StringUtils.isNotEmpty(codOrd)) res.put("codOrd", codOrd);
			String progInvio = extractElemInfoFromDocument(doc, "UUID", null);
			if(StringUtils.isNotEmpty(progInvio)) res.put("progInvio", progInvio);
			logger.debug("Found: {} with {}",codOrd, progInvio);
			
			// handle errors
		} catch (Exception ex) {
			logger.error("Exception.",ex);
		} finally {
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					//not handled
				}
			}
		}
		return res;
	}

	private String extractElemInfoFromDocument(Document doc, String elem, String subElem) throws XPathExpressionException {
		NodeList nodeList = doc.getElementsByTagNameNS("*", elem);
		if (nodeList != null && nodeList.getLength() > 0) {
			Node n = nodeList.item(0);
			if (subElem == null) {
				return StringUtils.trimToEmpty(n.getTextContent());
			} else {
				for (int i = 0; i < n.getChildNodes().getLength(); i++) {
					Node nnn = n.getChildNodes().item(i);
					if (subElem.equals(nnn.getLocalName())) {
						return StringUtils.trimToEmpty(nnn.getTextContent());
					}
				}
			}
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * Deprecated since UBL format used but not deleted because of a future probable use
	 * @param doc
	 * @param xpath
	 * @param expression
	 * @return
	 * @throws XPathExpressionException
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String extractXpathInfoFromDocument(Document doc, XPath xpath, String expression) throws XPathExpressionException {
		NodeList nodeList = (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);
		if(nodeList!=null && nodeList.getLength()>0) {
			Node n = nodeList.item(0);
			logger.debug("value -> {}:{}:{}",n.getNodeName(),n.getNodeValue(),n.getTextContent());
			if(StringUtils.isNotEmpty(n.getNodeValue())) return StringUtils.trimToEmpty(n.getNodeValue());
			return StringUtils.trimToEmpty(n.getTextContent());
			
		}
		return StringUtils.EMPTY;
	}

	public Boolean getSendDisabled() {
		return sendDisabled;
	}

	public void setSendDisabled(Boolean sendDisabled) {
		this.sendDisabled = sendDisabled;
	}

	public String getWarningInvioFattura() {
		return warningInvioFattura;
	}

	public void setWarningInvioFattura(String warningInvioFattura) {
		this.warningInvioFattura = warningInvioFattura;
	}

	public String getProgInvio() {
		return progInvio;
	}

	public void setProgInvio(String progInvio) {
		this.progInvio = progInvio;
	}
	
}

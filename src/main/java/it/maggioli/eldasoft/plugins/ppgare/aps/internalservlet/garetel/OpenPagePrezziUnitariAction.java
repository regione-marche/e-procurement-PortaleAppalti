package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;

import java.math.BigDecimal;
import java.util.List;

/**
 * ...
 *  
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class OpenPagePrezziUnitariAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2557187148944079519L;

	private IBandiManager bandiManager;
//	private ICodificheManager codificheManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	private int operazione;

	private Integer discriminante;

	/* Array di prezzi unitari inserito per la visualizzazione nella tabella per evitare i Double con esponenziale */
	@Validate(EParamValidation.IMPORTO)
	private String[] prezziUnitariString;
	
	/* Array dei ribassi percentuali inserito per la visualizzazione nella tabella per evitare i Double con esponenziale */
	@Validate(EParamValidation.PERCENTUALE)
	private String[] ribassoPercentualeString;

	private boolean[] checkVoceSelezionata;
	
	// visualizza la colonna della griglia "data consegna richiesta"
	private boolean visualizzaDataConsegna;
	
	// visualizza la colonna della griglia "Prezzo unitario"
	private boolean visualizzaPrezzoUnitario;

	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}	
	
	public void setPrezziUnitariString(String[] prezziUnitariString) {
		this.prezziUnitariString = prezziUnitariString;
	}

	public String[] getPrezziUnitariString() {
		return prezziUnitariString;
	}

	public String[] getRibassoPercentualeString() {
		return ribassoPercentualeString;
	}

	public void setRibassoPercentualeString(String[] ribassoPercentualeString) {
		this.ribassoPercentualeString = ribassoPercentualeString;
	}

	/** Contenitore della lista lavorazioni e forniture definita per la gara. */
	private List<VoceDettaglioOffertaType> vociDettaglio;

	public Integer getDiscriminante() {
		return discriminante;
	}

	public void setDiscriminante(Integer discriminante) {
		this.discriminante = discriminante;
	}

	public List<VoceDettaglioOffertaType> getVociDettaglio() {
		return vociDettaglio;
	}
	
	public boolean[] getCheckVoceSelezionata() {
		return checkVoceSelezionata;
	}

	public void setCheckVoceSelezionata(boolean[] checkVoceSelezionata) {
		this.checkVoceSelezionata = checkVoceSelezionata;
	}

	public boolean isVisualizzaDataConsegna() {
		return visualizzaDataConsegna;
	}

	public void setVisualizzaDataConsegna(boolean visualizzaDataConsegna) {
		this.visualizzaDataConsegna = visualizzaDataConsegna;
	}

	public boolean isVisualizzaPrezzoUnitario() {
		return visualizzaPrezzoUnitario;
	}

	public void setVisualizzaPrezzoUnitario(boolean visualizzaPrezzoUnitario) {
		this.visualizzaPrezzoUnitario = visualizzaPrezzoUnitario;
	}

	public String getSTEP_PREZZI_UNITARI() { return WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI; }

	public String getSTEP_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_OFFERTA;	}

	public String getSTEP_SCARICA_OFFERTA() { return WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA; }

	public String getSTEP_DOCUMENTI() { return WizardOffertaEconomicaHelper.STEP_DOCUMENTI; }

	public int getVOCI_SOGGETTE_RIBASSO() { return 1; }

	public int getVOCI_NON_SOGGETTE_RIBASSO() { return 2; }


	public int getTIPO_VALIDATORE_DATA() { return PortGareSystemConstants.TIPO_VALIDATORE_DATA; }

	public int getTIPO_VALIDATORE_IMPORTO() { return PortGareSystemConstants.TIPO_VALIDATORE_IMPORTO; }

	public int getTIPO_VALIDATORE_TABELLATO() { return PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO; }

	public int getTIPO_VALIDATORE_NOTE() { return PortGareSystemConstants.TIPO_VALIDATORE_NOTE; }

	public int getTIPO_VALIDATORE_NUMERO() { return PortGareSystemConstants.TIPO_VALIDATORE_NUMERO; }

	public int getTIPO_VALIDATORE_FLAG() { return PortGareSystemConstants.TIPO_VALIDATORE_FLAG; }

	public int getTIPO_VALIDATORE_STRINGA() { return PortGareSystemConstants.TIPO_VALIDATORE_STRINGA; }

	/**
	 * ... 
	 */
	@Override
	public String openPage() {
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		try {
			if (this.discriminante == null) {
				this.discriminante = getVOCI_SOGGETTE_RIBASSO();
			}
			
			boolean ribassiPesati = (helper.getGara().getTipoRibasso() != null &&
									 helper.getGara().getTipoRibasso() == 3);
			
			// inizializza le voci di dettaglio
			if (this.discriminante == getVOCI_SOGGETTE_RIBASSO()) {
				this.vociDettaglio = helper.getVociDettaglio();
				
				List<AttributoAggiuntivoOffertaType> attributiAggiuntivi = helper
					.getAttributiAggiuntivi();
				for (AttributoAggiuntivoOffertaType attributo : attributiAggiuntivi) {
					if (attributo.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO) {
						this.getMaps().put(
								attributo.getCodice(),
								InterceptorEncodedData.parseXml(attributo.getValoriAmmessi()));
					}
				}	
				
				// imposta peso, ribasso percentuale e ribasso pesato 
				if(ribassiPesati) {
					String[] ribassoPercArray = new String[this.vociDettaglio.size()];
					for (int i = 0; i < this.vociDettaglio.size(); i++) {
						
						Double ribassoPercentuale = helper.getVociDettaglio().get(i).getRibassoPercentuale();
						
						String ribassoPerc = null;
						if(ribassoPercentuale != null) {
							ribassoPerc = StringUtils.stripEnd(
									BigDecimal.valueOf(ribassoPercentuale).setScale(5, BigDecimal.ROUND_HALF_UP).toString(), 
									"0");
							if(ribassoPerc.endsWith(".")) {
								ribassoPerc = ribassoPerc + ("0");
							}
						}
						ribassoPercArray[i] = ribassoPerc;
					}
					this.ribassoPercentualeString = ribassoPercArray;
				}
			
			} else {
				this.vociDettaglio = helper.getVociDettaglioNoRibasso();
			}
			
			// inizializza i prezzi unitari
			String[] prezziUnitariStringArray = new String[helper.getPrezzoUnitario().length];
			for (int i = 0; i < helper.getPrezzoUnitario().length; i++) {
				String prezzoUni = null;
				if(helper.getPrezzoUnitario()[i] != null){
					prezzoUni = StringUtils.stripEnd(BigDecimal.valueOf(helper.getPrezzoUnitario()[i]).setScale(5, BigDecimal.ROUND_HALF_UP).toString(),"0");
					if(prezzoUni.endsWith(".")){
						prezzoUni = prezzoUni + ("0");
					}
				}
				prezziUnitariStringArray[i] = prezzoUni;
			}
			this.prezziUnitariString = prezziUnitariStringArray;
			
			
			// inizializza le voci selezionate (solo per itergara = 8)
			boolean[] checkVoceSelezionateArray = new boolean[helper.getVociDettaglio().size()];
			for (int i = 0; i < helper.getPrezzoUnitario().length; i++) {
				Double qtaOfferta = (helper.getVociDettaglio().get(i) != null ? helper.getVociDettaglio().get(i).getQuantitaOfferta() : null);				
				checkVoceSelezionateArray[i] = true;
				if(qtaOfferta != null && qtaOfferta == 0.0) {
					checkVoceSelezionateArray[i] = false;
				}
			}
			this.checkVoceSelezionata = checkVoceSelezionateArray;
			
			
			// visualizza le colonne della griglia "data consegna richiesta", "prezzo unitario"
			// solo se e' presente almeno un valore in una riga della griglia
			this.visualizzaDataConsegna = false;
			if(this.vociDettaglio != null) {
				for(int i = 0; i < this.vociDettaglio.size(); i++) {
					if(this.vociDettaglio.get(i).getDataConsegnaRichiesta() != null) {
						this.visualizzaDataConsegna = true;
						break;
					}
				}
			}
			this.visualizzaPrezzoUnitario = ribassiPesati;
			if(this.vociDettaglio != null) {
				for(int i = 0; i < this.vociDettaglio.size(); i++) {
					if(this.vociDettaglio.get(i).getPrezzoUnitario() != null) {
						this.visualizzaPrezzoUnitario = true;
						break;
					}
				}
			}
			
			this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA,
							 WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
		} catch (XmlException xmle) {
			ApsSystemUtils.logThrowable(xmle, this, "openPage");
			ExceptionUtils.manageExceptionError(xmle, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Throwable ex) {
			ApsSystemUtils.logThrowable(ex, this, "openPage");
			ExceptionUtils.manageExceptionError(ex, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

}

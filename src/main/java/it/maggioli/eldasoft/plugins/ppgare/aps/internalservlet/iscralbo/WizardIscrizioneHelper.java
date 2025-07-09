package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;

import it.eldasoft.sil.portgare.datatypes.AggIscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.CategoriaType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.ListaStazioniAppaltantiType;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IAltriDatiAnagraficiImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiUlterioriImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IIndirizzoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IRaggruppamenti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Contenitore dei dati del wizard iscrizione impresa all'albo fornitori.
 *
 * @author Stefano.Sabbadin
 */
public class WizardIscrizioneHelper 
	implements HttpSessionBindingListener, Serializable, IRaggruppamenti 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 906925625169735827L;
	private static final Logger logger = ApsSystemUtils.getLogger();

	/**
	 * 2=se e' un catalogo, 1=se e' un semplice elenco
	 */
	private int tipologia;

	/**
	 * Data/ora scadenza entro la quale inviare richieste
	 */
	private Date dataScadenza;

	/**
	 * Data ufficiale NTP di presentazione della richiesta (ha senso solo per gli
	 * invii effettivi, per i salvataggi in bozza deve essere valorizzata ma non
	 * ha alcun senso in quanto viene generata all'atto dell'invio di una
	 * richiesta al backoffice)
	 */
	private Date dataPresentazione;

	/**
	 * Chiave del bando per elenco fornitori usato per l'iscrizione
	 */
	private String idBando;

	/**
	 * Descrizione del bando per elenco fornitori a cui si vuole iscriversi
	 */
	private String descBando;

	/**
	 * Helper per lo step dati impresa
	 */
	private WizardDatiImpresaHelper impresa;

	/**
	 * true se e' prevista un'unica SA, false altrimenti
	 */
	private boolean unicaStazioneAppaltante;

	/**
	 * Codici delle stazioni appaltanti per cui si richiede l'iscrizione (nel caso
	 * di unica stazione appaltante, e' la stazione stessa)
	 */
	private TreeSet<String> stazioniAppaltanti;

	/**
	 * true se non sono previste categorie, false altrimenti
	 */
	private boolean categorieAssenti;

	/**
	 * Helper per lo step di inserimento categorie/prestazioni d'interesse
	 */
	private WizardCategorieHelper categorie;
	
	/**
	 * Helper per lo step di inserimento dei documenti
	 */
	private WizardDocumentiHelper documenti;

	/**
	 * Identificativo univoco della comunicazione (valorizzata nel caso di
	 * completamento)
	 */
	private Long idComunicazione;
	
	/**
	 * Identificativo univoco della comunicazione in stato BOZZA (valorizzata 
	 * se presente la comunicazione in stato bozza non ancora completata/inviata) 
	 */
	private Long idComunicazioneBozza;

	/**
	 * Indica, quando settato, che si tratta di un aggiornamento di dati o
	 * documenti della domanda d'iscrizione; di default vale false, e quindi
	 * indica che si riferisce all'iscrizione stessa o alla sua bozza
	 */
	private boolean aggiornamentoIscrizione;

	/**
	 * Indica, quando settato, che si tratta di un aggiornamento solo dei
	 * documenti e non dei dati.
	 */
	private boolean aggiornamentoSoloDocumenti;

	/**
	 * Indica il tipo di gestione delle classifiche usato dall'elenco (a range, o
	 * fino ad una classifica massima).
	 */
	private Integer tipoClassifica;

	/**
	 * Denominazione dell'associazione temporanea d'impresa per cui il richiedente
	 * rappresenta la mandataria.
	 */
	private String denominazioneRTI;

	/**
	 * Elenco delle componenti in caso di consorzio o RTI.
	 */
	private List<IComponente> componenti;

	/**
	 * true se impresa RTI, false altrimenti
	 */
	private boolean rti;

	/**
	 * true se impresa ammessa in RTI, false altrimenti
	 */
	private boolean ammesseRTI;

	/**
	 * true se la pagina RTI e' editabile, false altrimenti
	 */
	private boolean editRTI;

	/** Stack delle pagine navigate. */
	private Stack<String> stepNavigazione;

	private boolean datiModificati;
	
	/**
	 * Step di navigazione corrispondente allo step di inserimento dei dati anagrafici
	 */
	public static final String STEP_IMPRESA = "impresa";
	/**
	 * Step di navigazione corrispondente allo step di inserimento della denominazionedella RTI
	 */
	public static final String STEP_DENOMINAZIONE_RTI = "RTI";
	/**
	 * Step di navigazione corrispondente allo step di inserimento dei dettagli della RTI
	 */
	public static final String STEP_DETTAGLI_RTI = "Componenti";
	/**
	 * Step di navigazione corrispondente allo step di selezione delle categoria
	 */
	public static final String STEP_SELEZIONE_CATEGORIE = "categorie";
	/**
	 * Step di navigazione corrispondente allo step di riepilogo delle categoria
	 */
	public static final String STEP_RIEPILOGO_CATEGORIE = "riepilogoCategorie";
	/**
	 * Step di navigazione corrispondente allo step di generazione e scaricamento della domanda di iscrizione
	 */
	public static final String STEP_SCARICA_ISCRIZIONE = "generaPdfRichiesta";
	/**
	 * Step di navigazione corrispondente allo step di documentazione richiesta
	 */
	public static final String STEP_DOCUMENTAZIONE_RICHIESTA = "documenti";
	/**
	 * Step di navigazione corrispondente allo step di documentazione richiesta per il rinnovo
	 */
	public static final String STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO = "documenti";	
	/**
	 * Step di navigazione corrispondente allo step di presentazione della domanda di iscrizione
	 */
	public static final String STEP_PRESENTA_ISCRIZIONE = "riepilogo";

	// QFORM - QUESTIONARI
	public static final String STEP_QUESTIONARIO = "openQC";
	
	public static final String STEP_RIEPILOGO_QUESTIONARIO = "openQCSummary";

	private List<FirmatarioBean> listaFirmatariMandataria = new ArrayList<FirmatarioBean>();
	private FirmatarioBean firmatarioSelezionato = new FirmatarioBean();

	private ComponentiRTIList componentiRTI = null;
	
	private String serialNumberMarcaBollo;
	
	// mostra la richiesta dei requisiti coordinatore di sicurezza
	private boolean richiestaCoordinatoreSicurezza;
	
	// in possesso dei requisiti coordinatore di sicurezza
	// ("Boolean" perchè può essere vuoto e tal caso non va esportato nel doc XML)
	private Boolean requisitiCoordinatoreSicurezza;
	
	// mostra la richiesta dei requisiti "ascesa torre"  (GAREALBO.REQTORRE)
	private boolean richiestaAscesaTorre;
	
	// in requisiti "ascesa torre"
	// ("Boolean" perchè può essere vuoto e tal caso non va esportato nel doc XML)
	private Boolean requisitiAscesaTorre;
	
	private int idFirmatarioSelezionatoInLista;
	private boolean iscrizioneDomandaVisible = true;
	private boolean rinnovoIscrizione = false;
	private boolean fromBozza = false;
	
	protected String actionPrefix;

	
	public int getTipologia() {
		return tipologia;
	}

	public void setTipologia(int tipologia) {
		this.tipologia = tipologia;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	public String getIdBando() {
		return idBando;
	}

	public void setIdBando(String idBando) {
		this.idBando = idBando;
	}

	public String getDescBando() {
		return descBando;
	}

	public void setDescBando(String descBando) {
		this.descBando = descBando;
	}

	public boolean isUnicaStazioneAppaltante() {
		return unicaStazioneAppaltante;
	}

	public void setUnicaStazioneAppaltante(boolean unicaStazioneAppaltante) {
		this.unicaStazioneAppaltante = unicaStazioneAppaltante;
	}

	public TreeSet<String> getStazioniAppaltanti() {
		return stazioniAppaltanti;
	}

	public void setStazioniAppaltanti(TreeSet<String> stazioniAppaltanti) {
		this.stazioniAppaltanti = stazioniAppaltanti;
	}

	public boolean isCategorieAssenti() {
		return categorieAssenti;
	}

	public void setCategorieAssenti(boolean categorieAssenti) {
		this.categorieAssenti = categorieAssenti;
	}

	public WizardCategorieHelper getCategorie() {
		return categorie;
	}

	public void setCategorie(WizardCategorieHelper categorie) {
		this.categorie = categorie;
	}

	public WizardDocumentiHelper getDocumenti() {
		return documenti;
	}

	public void setDocumenti(WizardDocumentiHelper documenti) {
		this.documenti = documenti;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
		if(this.documenti != null) {
			this.documenti.setIdComunicazione(idComunicazione);
		}
	}
	
	public Long getIdComunicazioneBozza() {
		return idComunicazioneBozza;
	}

	public void setIdComunicazioneBozza(Long idComunicazioneBozza) {
		this.idComunicazioneBozza = idComunicazioneBozza;
	}

	public boolean isAggiornamentoIscrizione() {
		return aggiornamentoIscrizione;
	}

	public void setAggiornamentoIscrizione(boolean aggiornamentoIscrizione) {
		this.aggiornamentoIscrizione = aggiornamentoIscrizione;
	}

	public boolean isAggiornamentoSoloDocumenti() {
		return aggiornamentoSoloDocumenti;
	}

	public void setAggiornamentoSoloDocumenti(boolean aggiornamentoSoloDocumenti) {
		this.aggiornamentoSoloDocumenti = aggiornamentoSoloDocumenti;
	}

	public Integer getTipoClassifica() {
		return tipoClassifica;
	}

	public void setTipoClassifica(Integer tipoClassifica) {
		this.tipoClassifica = tipoClassifica;
	}

	public IDatiPrincipaliImpresa getDatiPrincipaliImpresa() {
		return this.impresa.getDatiPrincipaliImpresa();
	}

	public ArrayList<IIndirizzoImpresa> getIndirizziImpresa() {
		return this.impresa.getIndirizziImpresa();
	}

	public ArrayList<ISoggettoImpresa> getLegaliRappresentantiImpresa() {
		return this.impresa.getLegaliRappresentantiImpresa();
	}

	public ArrayList<ISoggettoImpresa> getDirettoriTecniciImpresa() {
		return this.impresa.getDirettoriTecniciImpresa();
	}

	public ArrayList<ISoggettoImpresa> getAltreCaricheImpresa() {
		return this.impresa.getAltreCaricheImpresa();
	}

	public ArrayList<ISoggettoImpresa> getCollaboratoriImpresa() {
		return this.impresa.getCollaboratoriImpresa();
	}

	public IAltriDatiAnagraficiImpresa getAltriDatiAnagraficiImpresa() {
		return this.impresa.getAltriDatiAnagraficiImpresa();
	}

	public IDatiUlterioriImpresa getDatiUlterioriImpresa() {
		return this.impresa.getDatiUlterioriImpresa();
	}

	@Override
	public List<IComponente> getComponenti() {
		return componenti;
	}

	public void setComponenti(List<IComponente> componenti) {
		this.componenti = componenti;
	}

	public String getDenominazioneRTI() {
		return denominazioneRTI;
	}

	public void setDenominazioneRTI(String denominazioneRTI) {
		this.denominazioneRTI = denominazioneRTI;
	}

	public boolean isRti() {
		return rti;
	}

	public void setRti(boolean rti) {
		if(rti != this.rti) {
			this.componenti.clear();
			this.componentiRTI.clear();
		}
		this.rti = rti;
	}

	public boolean isAmmesseRTI() {
		return ammesseRTI;
	}

	public void setAmmesseRTI(boolean ammesseRTI) {
		this.ammesseRTI = ammesseRTI;
	}

	public boolean isEditRTI() {
		return editRTI;
	}

	public void setEditRTI(boolean editRTI) {
		this.editRTI = editRTI;
	}

	@Override
	public boolean checkQuota() {
		return false;
	}

	/**
	 * Informazione non gestita (vedere checkQuota)
	 */
	@Override
	public Double getQuotaRTI() {
		return null;
	}

	/**
	 * Informazione non gestita (vedere checkQuota)
	 */
	@Override
	public void setQuotaRTI(Double quotaRTI) {
	}

	public boolean isRichiestaCoordinatoreSicurezza() {
		return richiestaCoordinatoreSicurezza;
	}

	public void setRichiestaCoordinatoreSicurezza(boolean richiestaCoordinatoreSicurezza) {
		this.richiestaCoordinatoreSicurezza = richiestaCoordinatoreSicurezza;
	}

	/**
	 * ("Boolean" perche' può essere non valorizzato ed in tal caso non va scritto nel doc XML)
	 */
	public Boolean getRequisitiCoordinatoreSicurezza() {
		return requisitiCoordinatoreSicurezza;
	}

	public void setRequisitiCoordinatoreSicurezza(Boolean requisitiCoordinatoreSicurezza) {
		this.requisitiCoordinatoreSicurezza = requisitiCoordinatoreSicurezza;
	}

	public boolean isRichiestaAscesaTorre() {
		return richiestaAscesaTorre;
	}

	public void setRichiestaAscesaTorre(boolean richiestaAscesaTorre) {
		this.richiestaAscesaTorre = richiestaAscesaTorre;
	}

	/**
	 * ("Boolean" perche' può essere non valorizzato ed in tal caso non va scritto nel doc XML)
	 */
	public Boolean getRequisitiAscesaTorre() {
		return requisitiAscesaTorre;
	}

	public void setRequisitiAscesaTorre(Boolean requisitiAscesaTorre) {
		this.requisitiAscesaTorre = requisitiAscesaTorre;
	}

	public Stack<String> getStepNavigazione() {
		return stepNavigazione;
	}

	public void setStepNavigazione(Stack<String> stepNavigazione) {
		this.stepNavigazione = stepNavigazione;
	}

	public List<FirmatarioBean> getListaFirmatariMandataria() {
		return listaFirmatariMandataria;
	}

	public void setListaFirmatariMandataria(
			List<FirmatarioBean> listaFirmatariMandataria) {
		this.listaFirmatariMandataria = listaFirmatariMandataria;
	}

	public FirmatarioBean getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(FirmatarioBean firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}

	public int getIdFirmatarioSelezionatoInLista() {
		return idFirmatarioSelezionatoInLista;
	}

	public void setIdFirmatarioSelezionatoInLista(int idFirmatarioSelezionatoInLista) {
		this.idFirmatarioSelezionatoInLista = idFirmatarioSelezionatoInLista;
	}

	public boolean isDatiModificati() {
		return datiModificati;
	}

	public void setDatiModificati(boolean datiModificati) {
		this.datiModificati = datiModificati;
	}

	public ComponentiRTIList getComponentiRTI() {
		return componentiRTI;
	}

	public void setComponentiRTI(ComponentiRTIList componentiRTI) {
		this.componentiRTI = componentiRTI;
	}

	public boolean isIscrizioneDomandaVisible() {
		return iscrizioneDomandaVisible;
	}
	
	public void setIscrizioneDomandaVisible(boolean iscrizioneDomandaVisible) {
		this.iscrizioneDomandaVisible = iscrizioneDomandaVisible;
	}
	
	public boolean isFromBozza() {
		return fromBozza;
	}
	
	public void setFromBozza(boolean fromBozza) {
		this.fromBozza = fromBozza;
	}
	
	public String getSerialNumberMarcaBollo() {
		return serialNumberMarcaBollo;
	}

	public void setSerialNumberMarcaBollo(String serialNumberMarcaBollo) {
		this.serialNumberMarcaBollo = serialNumberMarcaBollo;
	}

	public boolean isRinnovoIscrizione() {
		return rinnovoIscrizione;
	}

	public void setRinnovoIscrizione(boolean rinnovoIscrizione) {
		this.rinnovoIscrizione = rinnovoIscrizione;
	}


	/**
	 * Inizializza il contenitore vuoto attribuendo i valori di default
	 */
	public WizardIscrizioneHelper() {
		this.actionPrefix = "openPageIscrAlbo"; 
		this.stepNavigazione = new Stack<String>();
		this.dataScadenza = null;
		this.dataPresentazione = null;
		this.idBando = null;
		this.descBando = null;
		this.impresa = null;
		this.rti = false;
		//this.ammesseRTI = false;
		//this.editRTI = false;
		this.unicaStazioneAppaltante = false;
		this.stazioniAppaltanti = new TreeSet<String>();
		this.categorieAssenti = false;
		this.categorie = new WizardCategorieHelper();
		this.documenti = new WizardDocumentiHelper();
		this.idComunicazione = null;
		this.idComunicazioneBozza = null;
		this.aggiornamentoIscrizione = false;
		this.aggiornamentoSoloDocumenti = false;
		this.tipoClassifica = PortGareSystemConstants.TIPO_CLASSIFICA_INTERVALLO;
		this.denominazioneRTI = null;
		this.componenti = new ArrayList<IComponente>();
		this.componentiRTI = new ComponentiRTIList(); 
		this.idFirmatarioSelezionatoInLista = -1;
		this.serialNumberMarcaBollo = null;
		this.richiestaCoordinatoreSicurezza = false;
		this.requisitiCoordinatoreSicurezza = null;
		this.richiestaAscesaTorre = false;
		this.requisitiAscesaTorre = null;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0 l'evento di sessione scaduta
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		this.documenti.valueUnbound(arg0);
		
		Iterator<File> iter = this.documenti.getPdfGenerati().listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	@Override
	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	public void setImpresa(WizardDatiImpresaHelper impresa) {
		
		if(!impresa.isLiberoProfessionista()) {
			listaFirmatariMandataria = new ArrayList<FirmatarioBean>();
			for (int i = 0; i < impresa.getLegaliRappresentantiImpresa().size(); i++) {
				addSoggettoMandataria(
						impresa.getLegaliRappresentantiImpresa().get(i), 
						i,
						CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI, 
						listaFirmatariMandataria);
			}			
			for (int i = 0; i < impresa.getDirettoriTecniciImpresa().size(); i++) {
				addSoggettoMandataria(
						impresa.getDirettoriTecniciImpresa().get(i), 
						i,
						CataloghiConstants.LISTA_DIRETTORI_TECNICI, 
						listaFirmatariMandataria);
			}			
			for (int i = 0; i < impresa.getAltreCaricheImpresa().size(); i++) {
				addSoggettoMandataria(
						impresa.getAltreCaricheImpresa().get(i), 
						i,
						CataloghiConstants.LISTA_ALTRE_CARICHE, 
						listaFirmatariMandataria);
			}			
			this.idFirmatarioSelezionatoInLista = -1;
		} else {
//			// LIBERO PROFESSIONISTA
//			FirmatarioBean firmatario = new FirmatarioBean();
//			firmatario.setNominativo(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
//			listaFirmatari.add(firmatario);
		}
		
		this.impresa = impresa;
	}
	
	private void addSoggettoMandataria(
			ISoggettoImpresa soggetto, 
			int index, 
			String lista, 
			List<FirmatarioBean> listaFirmatari) 
	{
		if (soggetto.getDataFineIncarico() == null && "1".equals(soggetto.getResponsabileDichiarazioni())) {
			FirmatarioBean firmatario = new FirmatarioBean();
			String cognome = StringUtils.capitalize(soggetto.getCognome());
			String nome = StringUtils.capitalize(soggetto.getNome());
			firmatario.setNominativo(new StringBuilder().append(cognome)
									.append(" ").append(nome).toString());
			firmatario.setIndex(index);
			firmatario.setLista(lista);
			listaFirmatari.add(firmatario);
		}
	}

	/**
	 * Crea l'oggetto documento per la generazione della stringa XML per l'inoltro
	 * della richiesta al backoffice. L'oggetto restituito dipende dal flag
	 * aggiornamentoIscrizione, pertanto se:
	 * <ul>
	 * <li>aggiornamentoIscrizione = false si ritorna un oggetto della classe
	 * IscrizioneImpresaElencoOperatoriDocument</li>
	 * <li>aggiornamentoIscrizione = true si ritorna un oggetto della classe
	 * AggiornamentoIscrizioneImpresaElencoOperatoriDocument</li>
	 * </ul>
	 *
	 * @param attachFileContents 
	 * 				true se si intendono allegare i documenti ed il loro 
	 * 				contenuto, false altrimenti
	 *
	 * @return oggetto documento contenente i dati dell'iscrizione dell'impresa o
	 * 		   di un suo aggiornamento
	 * 
	 * @throws IOException
	 */
	public XmlObject getXmlDocument(boolean attachFileContents) throws IOException {

		GregorianCalendar dataUfficiale = new GregorianCalendar();
		if (this.dataPresentazione != null) {
			dataUfficiale.setTime(this.dataPresentazione);
		}

		XmlObject document;

		if (!this.aggiornamentoIscrizione) {

			IscrizioneImpresaElencoOperatoriDocument doc
				= IscrizioneImpresaElencoOperatoriDocument.Factory.newInstance();
			IscrizioneImpresaElencoOpType iscrizImpresa
				= doc.addNewIscrizioneImpresaElencoOperatori();
			iscrizImpresa.setDataPresentazione(dataUfficiale);
			// set dei dati dell'impresa
			this.addImpresa(iscrizImpresa.addNewDatiImpresa());
			// set delle stazioni appaltanti
			this.addStazioniAppaltanti(iscrizImpresa.addNewStazioniAppaltanti());
			// set delle categorie
			this.addCategorie(iscrizImpresa.addNewCategorieIscrizione());
			// set dell'eventuale numero di serie della marca da bollo
			if (StringUtils.isNotBlank(this.serialNumberMarcaBollo)) {
				iscrizImpresa.setSerialNumberMarcaBollo(this.serialNumberMarcaBollo);
			}
			// set dei documenti (se esistenti)
			this.addDocumenti(iscrizImpresa.addNewDocumenti(), attachFileContents);
			// inserisco i dati sull'RTI o sul consorzio
			if (this.isAmmesseRTI() && this.isRti()) {
				iscrizImpresa.setDenominazioneRTI(this.denominazioneRTI);
			}
			
			//if((this.isRti() && this.componentiRTI.size()>0) || (this.impresa.isConsorzio() && this.componenti.size()>0)){
			if(this.isRti() && componentiRTI.size() > 0) {
				ListaPartecipantiRaggruppamentoType listaPartecipanti = iscrizImpresa.addNewPartecipantiRaggruppamento();
				for(IComponente comp : componentiRTI)
					addNewComponentToGroup(listaPartecipanti, comp);
			} else if(this.impresa.isConsorzio() && this.componenti.size() > 0) {
				ListaPartecipantiRaggruppamentoType listaPartecipanti = iscrizImpresa.addNewPartecipantiRaggruppamento();
				for(IComponente comp: componenti)
					addNewComponentToGroup(listaPartecipanti, comp);
			}

			if(this.isIscrizioneDomandaVisible()) {
				// inserisco i dati sui firmatari
				this.addFirmatari(iscrizImpresa);
			}
			
			// imposta il flag "possesso dei requisiti coordinatore di sicurezza"
			if(this.richiestaCoordinatoreSicurezza && this.requisitiCoordinatoreSicurezza != null) {
				iscrizImpresa.setRequisitiCoordinatoreSicurezza( this.requisitiCoordinatoreSicurezza );
			}
			
			// imposta il flag "requisiti ascesa torre"
			if(this.richiestaAscesaTorre && this.requisitiAscesaTorre != null) {
				iscrizImpresa.setRequisitiAscesaTorre( this.requisitiAscesaTorre );
			}
			
			// QFORM
			// gestione questionari
			logger.debug("Controllo la gestione dei questionari: {}",this.documenti.isGestioneQuestionario());
			if(this.documenti.isGestioneQuestionario()) {
				iscrizImpresa.setQuestionarioCompletato(this.documenti.isQuestionarioCompletato());
				iscrizImpresa.setQuestionarioId(this.documenti.getQuestionarioId());
				//iscrizImpresa.setRiepilogoPdfAutomatico(this.documenti.getQuestionarioPdfRiepilogoAutomatico() != null);
			}
			
			document = doc;

		} else {
			AggiornamentoIscrizioneImpresaElencoOperatoriDocument doc =
				AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.newInstance();
			AggIscrizioneImpresaElencoOpType aggImpresa = 
				doc.addNewAggiornamentoIscrizioneImpresaElencoOperatori();
			aggImpresa.setDataPresentazione(dataUfficiale);
			// set dei dati dell'impresa
			this.addImpresa(aggImpresa.addNewDatiImpresa());
			// set delle stazioni appaltanti (eventuali)
			if (this.stazioniAppaltanti.size() > 0) {
				this.addStazioniAppaltanti(aggImpresa.addNewStazioniAppaltanti());
			}
			// set delle categorie (eventuali)
			if (this.categorie.getCategorieSelezionate().size() > 0) {
				this.addCategorie(aggImpresa.addNewCategorieIscrizione());
			}
			// set dei documenti (eventuali)
			if ((this.documenti.getRequiredDocs().size()
					+ this.documenti.getAdditionalDocs().size()) > 0) {
				this.addDocumenti(aggImpresa.addNewDocumenti(), attachFileContents);
			}
			// inserisco i dati sull'RTI o sul consorzio
			if (this.denominazioneRTI != null) {
				aggImpresa.setDenominazioneRTI(this.denominazioneRTI);
				this.setRti(true);
			}
			// set dei firmatari
			if(this.isIscrizioneDomandaVisible()) {
				// inserisco i dati sui firmatari
				this.addFirmatari(aggImpresa);
			}
			
			// imposta il flag "possesso dei requisiti coordinatore di sicurezza"
			if(this.richiestaCoordinatoreSicurezza && this.requisitiCoordinatoreSicurezza != null) {
				aggImpresa.setRequisitiCoordinatoreSicurezza( this.requisitiCoordinatoreSicurezza );
			}
			
			// imposta il flag "requisiti ascesa torre"
			if(this.richiestaAscesaTorre && this.requisitiAscesaTorre != null) {
				aggImpresa.setRequisitiAscesaTorre( this.requisitiAscesaTorre );
			}

			// QFORM
			// gestione questionari
			if(this.documenti.isGestioneQuestionario()) {
				aggImpresa.setQuestionarioCompletato(this.documenti.isQuestionarioCompletato());
				aggImpresa.setQuestionarioId(this.documenti.getQuestionarioId());
				//aggImpresa.setRiepilogoPdfAutomatico(this.documenti.getQuestionarioPdfRiepilogoAutomatico() != null);
			}

			document = doc;
		}
		document.documentProperties().setEncoding("UTF-8");
		return document;
	}

	private static void addNewComponentToGroup(ListaPartecipantiRaggruppamentoType listaPartecipanti, IComponente comp) {
		PartecipanteRaggruppamentoType part = listaPartecipanti.addNewPartecipante();
		part.setRagioneSociale(comp.getRagioneSociale());
		part.setTipoImpresa(comp.getTipoImpresa());
		part.setCodiceFiscale(comp.getCodiceFiscale());
		part.setPartitaIVA(comp.getPartitaIVA());
		part.setNazione(comp.getNazione());
		part.setAmbitoTerritoriale(comp.getAmbitoTerritoriale());
		part.setIdFiscaleEstero(comp.getIdFiscaleEstero());
	}

	/**
	 * Crea i dati dell'impresa
	 *
	 * @param datiImpresa
	 */
	private void addImpresa(DatiImpresaType datiImpresa) {

		WizardDatiImpresaHelper.addNewImpresa(this.getDatiPrincipaliImpresa(),
				this.getDatiUlterioriImpresa(), this.getIndirizziImpresa(),
				this.getAltriDatiAnagraficiImpresa(),
				this.getImpresa().isLiberoProfessionista(),
				datiImpresa);
		// set dei soggetti
		if (!this.getImpresa().isLiberoProfessionista()) {
			WizardDatiImpresaHelper.addNewReferentiImpresa(
					this.getImpresa().getLegaliRappresentantiImpresa(), datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(
					this.getImpresa().getDirettoriTecniciImpresa(), datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(
					this.getImpresa().getAltreCaricheImpresa(),datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(
					this.getCollaboratoriImpresa(), datiImpresa);
		}
	}

	/**
	 * Crea i dati delle stazioni appaltanti
	 *
	 * @param listaSA
	 */
	private void addStazioniAppaltanti(ListaStazioniAppaltantiType listaSA) {
		for (Iterator<String> iterator = this.stazioniAppaltanti.iterator(); iterator.hasNext();) {
			listaSA.addStazioneAppaltante(iterator.next());
		}
	}

	/**
	 * Crea i dati delle categorie
	 *
	 * @param listaCategorie la lista delle categorie
	 */
	private void addCategorie(ListaCategorieIscrizioneType listaCategorie) {
		CategoriaType categoria;
		String codice;

		for (Iterator<String> iterator = this.categorie.getCategorieSelezionate().iterator(); iterator.hasNext();) {
			codice = (String) iterator.next();
			categoria = listaCategorie.addNewCategoria();
			categoria.setCategoria(codice);
			if (StringUtils.isNotBlank(this.categorie.getClasseDa().get(codice))) {
				categoria.setClassificaMinima(this.categorie.getClasseDa().get(codice));
			}
			if (StringUtils.isNotBlank(this.categorie.getClasseA().get(codice))) {
				categoria.setClassificaMassima(this.categorie.getClasseA().get(codice));
			}
			if (StringUtils.isNotBlank(this.categorie.getNota().get(codice))) {
				categoria.setNota(this.categorie.getNota().get(codice));
			}
		}
	}

	/**
	 * Crea gli eventuali documenti definiti per l'impresa
	 *
	 * @param listaDocumenti
	 * @param attachFileContents
	 * 
	 * @throws IOException
	 */
	private void addDocumenti(ListaDocumentiType listaDocumenti, boolean attachFileContents)
		throws IOException 
	{
		boolean applicataCifratura = false;
		this.documenti.addDocumenti(listaDocumenti, attachFileContents, applicataCifratura);
	}

	/**
	 * crea i dati dei firmatari
	 */
	private void addFirmatari(XmlObject iscrizione) {
		// iscrizione { IscrizioneImpresaElencoOpType | AggIscrizioneImpresaElencoOpType }
		if(componentiRTI.size() == 0 || !rti) {
			// UNICO FIRMATARIO
			if(StringUtils.isEmpty(this.firmatarioSelezionato.getLista())) {
				this.firmatarioSelezionato = this.listaFirmatariMandataria.get(0);
			}

			SoggettoImpresaHelper soggettoFromLista = null;
			FirmatarioType firmatario = (iscrizione instanceof IscrizioneImpresaElencoOpType
					? ((IscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario()
					: ((AggIscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario());

			if(!impresa.isLiberoProfessionista()) {
				soggettoFromLista = getFirmatarioMandatariaFromListeSoggetti(this.firmatarioSelezionato);
				fillMandatariaImpresa(firmatario,soggettoFromLista);
			} else {
				// LIBERO PROFESSIONISTA
				fillMandatariaLiberoProfessionista(firmatario);
			}
		} else {
			// FIRMATARI IN CASO DI RTI
			int idxMandataria = componentiRTI.getMandataria(impresa.getDatiPrincipaliImpresa());
			
			for(int i = 0; i < componentiRTI.size(); i++) {
				SoggettoImpresaHelper currentFirmatario = componentiRTI.getFirmatario(componentiRTI.get(i));
				
				if(i == idxMandataria) {
					// CASO 1 mandataria
					if(impresa.isLiberoProfessionista()) {
						// CASO 1.1 : mandataria di tipo libero professionista
						FirmatarioType firmatario = (iscrizione instanceof IscrizioneImpresaElencoOpType
								? ((IscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario()
								: ((AggIscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario());
						fillMandatariaLiberoProfessionista(firmatario);
					} else {
						// CASO 1.2 : mandataria di tipo non libero professionista
						if(currentFirmatario != null) {
							FirmatarioType firmatario = (iscrizione instanceof IscrizioneImpresaElencoOpType
									? ((IscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario()
									: ((AggIscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario());
							firmatario.setNome(currentFirmatario.getNome());
							firmatario.setCognome(currentFirmatario.getCognome());
							FirmatarioBean infoQualificaFirmatario = null;
							boolean found = false;
							for(int j  = 0; j < listaFirmatariMandataria.size() && !found; j++) {
								if(listaFirmatariMandataria.get(j).getNominativo().equalsIgnoreCase(firmatario.getCognome() + " " + firmatario.getNome())) {
									infoQualificaFirmatario = listaFirmatariMandataria.get(j);
									found = true;
								}
							}

							SoggettoImpresaHelper soggettoFromLista = getFirmatarioMandatariaFromListeSoggetti(infoQualificaFirmatario);

							fillMandatariaImpresa(firmatario, soggettoFromLista);
						}
					}
				} else {
					// CASO 2 : mandanti
					if(currentFirmatario != null) {
						if(StringUtils.isNotEmpty(currentFirmatario.getCodiceFiscale())) {
							FirmatarioType firmatario = (iscrizione instanceof IscrizioneImpresaElencoOpType
									? ((IscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario()
									: ((AggIscrizioneImpresaElencoOpType)iscrizione).addNewFirmatario());
							//essendo il C.F un campo obbligatorio per i firmatari delle mandanti, lo uso
							//come discriminenta per vedere se il firmatario è stato valorizzato o meno 
							//nello step del download del pdf della richiesta iscrizione (salvataggio parziale)
							if("6".equals(componentiRTI.get(i).getTipoImpresa())) {
								// libero professionista
								firmatario.setQualifica("libero professionista");
							} else {
								// Non libero professionista
								firmatario.setQualifica(currentFirmatario.getSoggettoQualifica());
							}
							fillMandante(firmatario, currentFirmatario, componentiRTI.get(i));
						}
					}
				}
			}
		}
	}

	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione.
	 */
	public void fillStepsNavigazione() {
		this.stepNavigazione.removeAllElements();
		this.stepNavigazione.push(WizardIscrizioneHelper.STEP_IMPRESA);
		if((this.isEditRTI() || this.isAmmesseRTI()) 
		   && !this.isRinnovoIscrizione() && !this.isAggiornamentoIscrizione()) 
		{
			this.stepNavigazione.push(WizardIscrizioneHelper.STEP_DENOMINAZIONE_RTI);
		}
		if(!this.isAggiornamentoIscrizione() && !this.isAggiornamentoSoloDocumenti() && !this.isRinnovoIscrizione() && (this.isRti() || this.isEditRTI()|| this.getImpresa().isConsorzio())) {
			this.stepNavigazione.push(WizardIscrizioneHelper.STEP_DETTAGLI_RTI);
		}
		//se (iscrizione || aggiornamento dati/doc ) && ci sono categorie => inserisco step
		if((!this.isAggiornamentoSoloDocumenti() && !this.isRinnovoIscrizione()) && !this.isCategorieAssenti()) {
			this.stepNavigazione.push(WizardIscrizioneHelper.STEP_SELEZIONE_CATEGORIE);
		}
		if((!this.isAggiornamentoSoloDocumenti() && !this.isRinnovoIscrizione()) && !this.isCategorieAssenti()) {
			this.stepNavigazione.push(WizardIscrizioneHelper.STEP_RIEPILOGO_CATEGORIE);
		}
		
		// QFORM - QUESTIONARI
		// NB: i QFrom sono disponibili solo per l'iscrizione ad elenco
		boolean abilitaQuestionari = false;
		if(this.documenti.isGestioneQuestionario() && !this.isRinnovoIscrizione()) {
			abilitaQuestionari = true;
		}
		if(abilitaQuestionari) {
			this.stepNavigazione.push(WizardIscrizioneHelper.STEP_QUESTIONARIO);
			this.stepNavigazione.push(WizardIscrizioneHelper.STEP_RIEPILOGO_QUESTIONARIO);
		} else {
			// 20/04/2018 1.14.10 introdotto anche in aggiornamento lo step "Scarica domanda"...  
			//if(!this.isAggiornamentoIscrizione() && this.isIscrizioneDomandaVisible()) {
			if(this.isIscrizioneDomandaVisible()) {
				this.stepNavigazione.push(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
			}
			if(!this.isRinnovoIscrizione()) {
				this.stepNavigazione.push(WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA);
			}
			else {
				this.stepNavigazione.push(WizardIscrizioneHelper.STEP_DOCUMENTAZIONE_RICHIESTA_RINNOVO);
			}
		}
		
		this.stepNavigazione.push(WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE);
	}

	/**
	 * Ritorna lo step di navigazione successivo allo step in input
	 * 
	 * @param stepAttuale
	 *            step attuale
	 * @return step successivo previsto dopo l'attuale, null se non previsto
	 */
	public String getNextStepNavigazione(String stepAttuale) {
		String step = null;
		int posizione = this.stepNavigazione.indexOf(stepAttuale);
		if (posizione != -1 && posizione + 1 < this.stepNavigazione.size()) {
			step = this.stepNavigazione.elementAt(posizione + 1);
		}
		return step;
	}

	/**
	 * Ritorna lo step di navigazione precedente lo step in input
	 * 
	 * @param stepAttuale
	 *            step attuale
	 * @return step precedente previsto dopo l'attuale, null se non previsto
	 */
	public String getPreviousStepNavigazione(String stepAttuale) {
		String step = null;
		int posizione = this.stepNavigazione.indexOf(stepAttuale);
		if (posizione != -1 && posizione - 1 >= 0) {
			step = this.stepNavigazione.elementAt(posizione - 1);
		}
		return step;
	}

	/**
	 * recupera il soggetto impresa in base al firmatario  
	 */
	protected SoggettoImpresaHelper getFirmatarioMandatariaFromListeSoggetti(FirmatarioBean firmatarioSelezionato) {
		SoggettoImpresaHelper soggettoFromLista = null;
		List<ISoggettoImpresa> soggetti = null;
		if (CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioSelezionato.getLista())) {
			soggetti = impresa.getLegaliRappresentantiImpresa();
		} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioSelezionato.getLista())) {
			soggetti = impresa.getDirettoriTecniciImpresa();
		} else {
			soggetti = impresa.getAltreCaricheImpresa();
		}
		if(soggetti == null) {
			ApsSystemUtils.getLogger().warn("WizardIscrizioneHelper.getFirmatarioMandatariaFromListeSoggetti", 
					"lista soggetti impresa non trovata nei dati impresa (lista=" + firmatarioSelezionato.getLista() + ")" );
		} else {
			soggettoFromLista = (SoggettoImpresaHelper)soggetti.get(firmatarioSelezionato.getIndex());
		}
		return soggettoFromLista;
	}

	void fillMandatariaLiberoProfessionista(FirmatarioType firmatario) {
		firmatario.setNome(this.impresa.getDatiPrincipaliImpresa().getRagioneSociale());
		firmatario.setCognome(null);
		firmatario.setCodiceFiscaleFirmatario(this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
		firmatario.setComuneNascita(this.impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
		firmatario.setDataNascita(CalendarValidator.getInstance()
				.validate(this.impresa.getAltriDatiAnagraficiImpresa().getDataNascita(), "dd/MM/yyyy"));
		firmatario.setProvinciaNascita(this.impresa.getAltriDatiAnagraficiImpresa().getProvinciaNascita());
		firmatario.setQualifica("Libero professionista");
		firmatario.setSesso(this.impresa.getAltriDatiAnagraficiImpresa().getSesso());	
		firmatario.setComuneNascita(this.impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
		firmatario.setCodiceFiscaleImpresa(this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
		firmatario.setPartitaIVAImpresa(this.impresa.getDatiPrincipaliImpresa().getPartitaIVA());
		firmatario.setTipoImpresa(this.impresa.getDatiPrincipaliImpresa().getTipoImpresa());
		firmatario.setRagioneSociale(this.impresa.getDatiPrincipaliImpresa().getRagioneSociale());
		IndirizzoType residenza = firmatario.addNewResidenza();
		residenza.setCap(this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale());
		residenza.setIndirizzo(this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale());
		residenza.setNumCivico(this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale());
		residenza.setComune(this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
		residenza.setNazione(this.impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
		residenza.setProvincia(this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale());
	}

	void fillMandatariaImpresa(FirmatarioType firmatario, SoggettoImpresaHelper soggettoFromLista) {
		firmatario.setNome(soggettoFromLista.getNome());
		firmatario.setCognome(soggettoFromLista.getCognome());
		firmatario.setCodiceFiscaleFirmatario(soggettoFromLista.getCodiceFiscale());
		firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
		firmatario.setDataNascita(CalendarValidator.getInstance()
				.validate(soggettoFromLista.getDataNascita(), "dd/MM/yyyy"));
		firmatario.setProvinciaNascita(soggettoFromLista.getProvinciaNascita());
		firmatario.setQualifica(soggettoFromLista.getSoggettoQualifica());
		firmatario.setSesso(soggettoFromLista.getSesso());	
		firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
		firmatario.setCodiceFiscaleImpresa(this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
		firmatario.setRagioneSociale(this.impresa.getDatiPrincipaliImpresa().getRagioneSociale());
		firmatario.setPartitaIVAImpresa(this.impresa.getDatiPrincipaliImpresa().getPartitaIVA());
		firmatario.setTipoImpresa(this.impresa.getDatiPrincipaliImpresa().getTipoImpresa());
		IndirizzoType residenza = firmatario.addNewResidenza();
		residenza.setCap(soggettoFromLista.getCap());
		residenza.setIndirizzo(soggettoFromLista.getIndirizzo());
		residenza.setNumCivico(soggettoFromLista.getNumCivico());
		residenza.setComune(soggettoFromLista.getComune());
		residenza.setNazione(soggettoFromLista.getNazione());
		residenza.setProvincia(soggettoFromLista.getProvincia());
	}

	void fillMandante(FirmatarioType firmatario, SoggettoImpresaHelper currentFirmatario, IComponente currentComponenteRTI) {
		firmatario.setNome(currentFirmatario.getNome());
		firmatario.setCognome(currentFirmatario.getCognome());;
		firmatario.setCodiceFiscaleFirmatario(currentFirmatario.getCodiceFiscale());
		firmatario.setComuneNascita(currentFirmatario.getComuneNascita());
		firmatario.setNazione(currentFirmatario.getNazione());		// oppure firmatario.setNazione(currentComponenteRTI.getNazione()); 		
		firmatario.setProvinciaNascita(currentFirmatario.getProvinciaNascita());
		firmatario.setSesso(currentFirmatario.getSesso());
		String cf = ("2".equals(currentComponenteRTI.getAmbitoTerritoriale()) 
				     ? currentComponenteRTI.getIdFiscaleEstero()
				     : currentComponenteRTI.getCodiceFiscale());
		firmatario.setCodiceFiscaleImpresa(cf);
		firmatario.setPartitaIVAImpresa(currentComponenteRTI.getPartitaIVA());
		firmatario.setDataNascita(CalendarValidator.getInstance().validate(currentFirmatario.getDataNascita(), "dd/MM/yyyy"));
		firmatario.setRagioneSociale(currentComponenteRTI.getRagioneSociale());
		firmatario.setTipoImpresa(currentComponenteRTI.getTipoImpresa());
		IndirizzoType residenza = firmatario.addNewResidenza();
		residenza.setCap(currentFirmatario.getCap());
		residenza.setIndirizzo(currentFirmatario.getIndirizzo());
		residenza.setNumCivico(currentFirmatario.getNumCivico());
		residenza.setComune(currentFirmatario.getComune());
		residenza.setNazione(currentFirmatario.getNazione());
		residenza.setProvincia(currentFirmatario.getProvincia());
	}

	public void cleanRTI() {
		this.componenti = new ArrayList<IComponente>();
		this.componentiRTI = new ComponentiRTIList();
	}
	
	/**
	 * componi la lista dei soggetti firmatari 
	 */
	protected  List<FirmatarioBean> composeListaFirmatariMandataria() {
		return ComponentiRTIList.composeListaFirmatariMandataria(this.impresa);
	}

	/**
	 * salva il firmatario della mandataria in caso di RTI 
	 */
	public SoggettoFirmatarioImpresaHelper saveFirmatarioMandataria(String firmatarioSelezionato) {
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		
		boolean mandatariaTrovata = false;
		for(int i = 0; i < listaFirmatariMandataria.size() && !mandatariaTrovata; i++) {
			String[] v = firmatarioSelezionato.split("-");
			String listaFirmatarioSelezionato = (v != null && v.length > 0 ? v[0] : "");
			int indiceFirmatarioSelezionato = Integer.parseInt((v != null && v.length > 1 ? v[1] : ""));
			
			ISoggettoImpresa soggettoFromLista = null;
			if(listaFirmatarioSelezionato.equals(CataloghiConstants.LISTA_ALTRE_CARICHE)) {
				soggettoFromLista = impresa.getAltreCaricheImpresa().get(indiceFirmatarioSelezionato);
			} else if(listaFirmatarioSelezionato.equals(CataloghiConstants.LISTA_DIRETTORI_TECNICI)) {
				soggettoFromLista = impresa.getDirettoriTecniciImpresa().get(indiceFirmatarioSelezionato);
			} else {
				soggettoFromLista = impresa.getLegaliRappresentantiImpresa().get(indiceFirmatarioSelezionato);
			}
			
			FirmatarioBean f = listaFirmatariMandataria.get(i); 
			if(f.getNominativo().equalsIgnoreCase(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome())
			   && listaFirmatarioSelezionato.equalsIgnoreCase(f.getLista()) ) 
			{
				mandatariaTrovata = true;
			}
			
			firmatario.copyFrom(soggettoFromLista);
			firmatario.setNominativo(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome());
			
			setIdFirmatarioSelezionatoInLista(i);
		}
		
		componentiRTI.addFirmatario(impresa.getDatiPrincipaliImpresa(), firmatario);
		
		return firmatario;
	}

	/**
	 * restituisce, in base allo step corrente, lo step successiva 
	 * relativa al wizard di iscrizione/aggiornamento ad elenco operarori
	 * Se "currentStep" è vuoto restituisce il primo step del wizard
	 * i.e.: getNextTarget[NEXTSTEP]"  
	 */
	public String getNextAction(String currentStep) {
		String target = (StringUtils.isEmpty(currentStep)
				? this.getStepNavigazione().firstElement()		// restitusci la prima pagina del wizard
				: this.getNextStepNavigazione(currentStep) );	// restitusci la prossima pagina del wizard
		String action = this.actionPrefix + StringUtils.capitalize(target);
		
		// (QFORM - QUESTIONARI)  
		if(this.documenti.isGestioneQuestionario()) {
			if(STEP_QUESTIONARIO.equals(target)) {
				action = target;
			} else if(STEP_RIEPILOGO_QUESTIONARIO.equals(target)) {
				action = target;
			}
		}
		logger.debug("getNextAction: {} -> {}",currentStep,action);
		return action;
	}
	
	/**
	 * restituisce, in base allo step corrente, lo step previuos 
	 * relativa al wizard di iscrizione/aggiornamento ad elenco operarori
	 * i.e.: getPreviuosTarget[NEXTSTEP]"  
	 */
	public String getPreviousAction(String currentStep) {
		String target = this.getPreviousStepNavigazione(currentStep);
		String action = this.actionPrefix + StringUtils.capitalize(target);
		
		// (QFORM - QUESTIONARI)
		if(this.documenti.isGestioneQuestionario()) {
			if(STEP_QUESTIONARIO.equals(target)) {
				action = target;
			} else if(STEP_RIEPILOGO_QUESTIONARIO.equals(target)) {
				action = target;
			}
		}
		
		return action;
	}

	/**
	 * restituisce una descrizione del tipo di funzione (iscrizione, aggiornamento, rinnovo...)
	 */
	public String getDescrizioneFunzione() {
		if(isAggiornamentoIscrizione()) {
			if(isAggiornamentoSoloDocumenti())
				return "Aggiornamento documenti";
			else
				return "Aggiornamento dati/documenti";
		} else if(isRinnovoIscrizione()) {
			return "Richiesta rinnovo iscrizione";		//"Rinnovo iscrizione"
		} else {
			return "Richiesta iscrizione";				//"Iscrizione"
		}
	}
		
	/**
	 * restituisce i documeni richiesti definiti in BO 
	 * @throws ApsException 
	 * @throws Throwable 
	 */
	public List<DocumentazioneRichiestaType> getDocumentiRichiestiBO() throws ApsException {
		List<DocumentazioneRichiestaType> documentiRichiestiBO = null;
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
			
			if(isRinnovoIscrizione()) {
				// RINNOVO
				BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
				
				// verifico lato BO se l'impresa si e' presentata in RTI o meno
				TipoPartecipazioneType tipoPartecipazione = bandiManager.getTipoPartecipazioneImpresa(
						action.getCurrentUser().getUsername(),
						idBando,
						null
				);
				
				// e recupero i documenti richiesti in base a questa info
				documentiRichiestiBO = bandiManager.getDocumentiRichiestiRinnovoIscrizione(
						idBando, 
						impresa.getDatiPrincipaliImpresa().getTipoImpresa(),
						tipoPartecipazione.isRti()
				);
			} else {
				// ISCRIZIONE O AGGIORNAMENTO DATI-DOCUMENTI
				documentiRichiestiBO = bandiManager.getDocumentiRichiestiBandoIscrizione(
						idBando, 
						impresa.getDatiPrincipaliImpresa().getTipoImpresa(),
						isRti());
			}
		} catch (Exception ex) {
			ApsSystemUtils.getLogger().error("getDocumentiRichiestiBO", ex);
			throw ex;
		}
		return documentiRichiestiBO;
	}

	/**
	 * (QCompiler/QFORM)
	 * verifica se la modulistica del questionario e' cambiata in BO 
	 */
	public boolean isQuestionarioModulisticaVariata() {
		if(documenti.isQuestionarioAllegato()) {
			try {
				QuestionarioType questionarioBO = WizardDocumentiHelper.getQuestionarioAssociatoBO(this.idBando, null, 0);
				long idModelloBO = (questionarioBO != null && questionarioBO.getId() > 0 ? questionarioBO.getId() : 0);				
				long idModello = (documenti.getQuestionarioId() > 0 ? documenti.getQuestionarioId() : 0);				
				logger.debug("idModelloBO: {}, idModello: {}",idModelloBO,idModello);
				return (idModelloBO > 0 && idModello > 0 && idModello != idModelloBO );
			} catch (ApsException e) {
				logger.warn("Impossibile ottenere il modello da BO tramite ws");
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * (QCompiler/QFORM)
	 * (QCompiler) indica se e' l'helper ha un questionario associato e 
	 * va attivata la gestione "questioniario"   
	 */
	public boolean isGestioneQuestionario() {
		return this.documenti.isGestioneQuestionario();
	}
	
	/**
	 * (QCompiler/QFORM)
	 * (QCompiler) indica se e' presente l'allegato del questionario 
	 * tra i documenti dell'helper   
	 */
	public boolean isQuestionarioAllegato() {
		return this.documenti.isQuestionarioAllegato();
	}
	
}

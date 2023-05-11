package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.contratti;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;
import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Azione per la gestione delle operazioni a livello di dettaglio contratto.
 * 
 * @author stefano.sabbadin
 * @since 1.11.5
 */
public class ContrattiAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5477854445026886811L;

	private Map<String, Object> session;

	/** Manager per l'interrogazione dei contratti. */
	private IContrattiManager contrattiManager;

	/** Manager per l'interrogazione delle comunicazioni. */
	private IBandiManager bandiManager;

	/** Manager per l'aggiornamento delle comunicazioni. */
	private IComunicazioniManager comunicazioniManager;

	/** Codice del contratto. */
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;
	private long id;
	@Validate(EParamValidation.GENERIC)
	private String idPrg;

	/** Dettaglio del contratto. */
	private ContrattoType dettaglioContratto;

	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numComunicazioniInviate;
	
	/** Id del documento, valorizzato quando si richiede il download previo aggiornamento della comunicazione di invio ordine. */
	private long idDocumento;
	
	private Long genere;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setContrattiManager(IContrattiManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public ContrattoType getDettaglioContratto() {
		return dettaglioContratto;
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
	
	public long getIdDocumento() {
		return idDocumento;
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

	public String getIdPrg() {
		return idPrg;
	}

	public void setIdPrg(String idPrg) {
		this.idPrg = idPrg;
	}

	/**
	 * Estrae il contratto e le comunicazioni scambiate con l'Amministrazione.
	 * 
	 * @return target struts
	 */
	public String view() {
		try {
			this.dettaglioContratto = this.contrattiManager
					.getDettaglioContratto(this.codice);

			// nel caso di utente loggato si estraggono le comunicazioni
			// personali dell'utente
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {

				StatisticheComunicazioniPersonaliType stat = this.bandiManager
						.getStatisticheComunicazioniPersonali(this.getCurrentUser().getUsername(), this.codice,null, null);
				this.numComunicazioniRicevute = stat.getNumComunicazioniRicevute();
				this.numComunicazioniRicevuteDaLeggere = stat.getNumComunicazioniRicevuteDaLeggere();
				this.numComunicazioniArchiviate = stat.getNumComunicazioniArchiviate();
				this.numComunicazioniArchiviateDaLeggere = stat.getNumComunicazioniArchiviateDaLeggere();
				this.numComunicazioniInviate = stat.getNumComunicazioniInviate();
				this.setGenere(4L);
				this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
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
	 * Aggiorna la comunicazione relativa alla ODA come letta e quindi inoltra
	 * all'azione per il download dello stream contenente il documento digitale
	 * richiesto.
	 */
	public String download() {
		String target = SUCCESS;
		try {
			this.dettaglioContratto = this.contrattiManager
					.getDettaglioContratto(this.codice);

			if (this.dettaglioContratto.getDataLettura() == null) {
				// si aggiorna la comunicazione in quanto si scarica il
				// documento di offerta contenuto
				this.comunicazioniManager
						.updateDataLetturaDestinatarioComunicazione(
								this.dettaglioContratto.getIdProgramma(),
								this.dettaglioContratto.getIdComunicazione(),
								this.dettaglioContratto.getIdDestinatario());
			}
			this.idDocumento = this.dettaglioContratto.getIdDocumento();			
			this.id = this.idDocumento;
			
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "download");
			ExceptionUtils.manageExceptionError(t, this);
			// il download e' una url indipendente e non dentro una porzione di pagina
			target = ERROR;
		}
		return target;
	}
	
}

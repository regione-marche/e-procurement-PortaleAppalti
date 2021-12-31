package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

public class MonitoraggioManager  extends AbstractService implements IMonitoraggioManager{

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6277101390629555777L;

	private IComunicazioniManager comunicazioniManager;

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	/** Reference al DAO di gestione delle operazioni su DB. */
	private IMonitoraggioDAO monitoraggioDAO;

	/**
	 * @param monitoraggioDAO
	 *            the monitoraggioDAO to set
	 */
	public void setMonitoraggioDAO(IMonitoraggioDAO monitoraggioDAO) {
		this.monitoraggioDAO = monitoraggioDAO;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	@Override
	public List<OperatoreEconomicoKO> getOperatoriEconomiciKO(String utente,
			String email, Date dataRegistrazioneDa, Date dataRegistrazioneA) {
		return this.monitoraggioDAO.searchOperatoriEconomiciKO(utente, email, dataRegistrazioneDa, dataRegistrazioneA);
	}

	@Override
	public String getEmailOperatore(String utenteSelezionato) {
		return this.monitoraggioDAO.getEmailOperatore(utenteSelezionato);
	}

	@Override
	public List<FlussiInviatiKO> getInviiFlussiConErrori(String utente, String mittente, 
			String tipoComunicazione, String riferimentoProcedura)
			throws ApsException {

		List<FlussiInviatiKO> flussiKO = null;

		/* --- criteri di ricerca --- */
		DettaglioComunicazioneType dettaglioComunicazione = new DettaglioComunicazioneType();
		dettaglioComunicazione
				.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		if (StringUtils.isNotEmpty(utente)) {
			dettaglioComunicazione.setChiave1(utente);
		}
		if (StringUtils.isNotEmpty(mittente)) {
			dettaglioComunicazione.setMittente(mittente);
		}
		if (StringUtils.isNotEmpty(tipoComunicazione)) {
			dettaglioComunicazione.setTipoComunicazione(tipoComunicazione);
		}
		if (StringUtils.isNotEmpty(riferimentoProcedura)) {
			dettaglioComunicazione.setChiave2(riferimentoProcedura);
		}
		dettaglioComunicazione
				.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);

		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager
				.getElencoComunicazioni(dettaglioComunicazione);
		if (comunicazioni.size() > 0) {
			flussiKO = new ArrayList<FlussiInviatiKO>();
			for (DettaglioComunicazioneType comunicazione : comunicazioni) {
				FlussiInviatiKO flussoKO = new FlussiInviatiKO();
				flussoKO.setIdComunicazione(comunicazione.getId());
				flussoKO.setDataInserimento(comunicazione.getDataInserimento());
				flussoKO.setUtente(comunicazione.getChiave1());
				flussoKO.setMittente(comunicazione.getMittente());
				flussoKO.setTipoComunicazione(comunicazione
						.getTipoComunicazione());
				flussoKO.setRiferimentoProcedura(comunicazione.getChiave2());
				flussiKO.add(flussoKO);
			}
			// si ordinano
			Collections.sort(flussiKO);
			// in ordine inverso, dal piu' recente al meno
			Collections.reverse(flussiKO);
		}

		return flussiKO;
	}

	@Override
	public DettaglioComunicazioneType getFlusso(Long flussoSelezionato) throws ApsException {
		DettaglioComunicazioneType dettaglioComunicazione = new DettaglioComunicazioneType();
		dettaglioComunicazione.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		dettaglioComunicazione.setId(flussoSelezionato);
		return this.comunicazioniManager.getElencoComunicazioni(dettaglioComunicazione).get(0);
	}

	@Override
	public void rielaboraFlusso(DettaglioComunicazioneType comunicazione) throws ApsException {
		DettaglioComunicazioneType[] comunicazioni = new DettaglioComunicazioneType[1];
		comunicazioni[0] = comunicazione;
		this.comunicazioniManager.updateStatoComunicazioni(comunicazioni, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
	}
}

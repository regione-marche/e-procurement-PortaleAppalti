package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm;

import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginEngAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginTitAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

import java.rmi.RemoteException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;

/**
 * Servizio gestore della protocollazione.
 * 
 * @author Stefano.Sabbadin
 */
public class WSDMManager extends AbstractService implements IWSDMManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 2030662377511532319L;

	/** Reference al wrapper del servizio WSDM. */
	private WSDMWrapper wsDM;

	/**
	 * @param wsDM
	 *            the wsDM to set
	 */
	public void setWsDM(WSDMWrapper wsDM) {
		this.wsDM = wsDM;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}
	
	public WSDMLoginAttrType getLoginAttr() {
		WSDMLoginAttrType login = new WSDMLoginAttrType();
		login.setCognome(wsDM.getCognome());
		login.setNome(wsDM.getNome());
		login.setRuolo(wsDM.getRuolo());
		login.setCodiceUO(wsDM.getCodiceUO());
		
		WSDMLoginEngAttrType loginEng = new WSDMLoginEngAttrType();
		loginEng.setIdUtente(wsDM.getIdUtente());
		loginEng.setIdUtenteUnitaOperativa(wsDM.getIdUnitaOperativa());
		login.setLoginEngAttr(loginEng);
		
		WSDMLoginTitAttrType loginTit = new WSDMLoginTitAttrType();
		login.setLoginTitAttr(loginTit);

		return login;
	}

	@Override
	public WSDMProtocolloDocumentoType inserisciProtocollo(WSDMLoginAttrType login, 
			WSDMProtocolloDocumentoInType protocolloIn) throws ApsException {
		WSDMProtocolloDocumentoType risultato = null;
		synchronized (this) {
			try {
				WSDMProtocolloDocumentoResType out = wsDM.getWSDM_PortType()
						.WSDMProtocolloInserisci(login, protocolloIn);
				if (out.isEsito()) {
					risultato = out.getProtocolloDocumento();
				} else {
					throw new ApsException(
							"Errore durante l'inserimento di un protocollo da parte del mittente con codice fiscale "
									+ protocolloIn.getMittenti(0)
											.getCodiceFiscale()
									+ " con oggetto "
									+ protocolloIn.getOggetto()
									+ ": "
									+ out.getMessaggio());
				}
			} catch (RemoteException e) {
				throw new ApsException(
						"Errore inaspettato durante l'inserimento di un di un protocollo da parte del mittente con codice fiscale "
								+ protocolloIn.getMittenti(0).getCodiceFiscale()
								+ " con oggetto " + protocolloIn.getOggetto(), e);
			}			
		}
		return risultato;
	}

}

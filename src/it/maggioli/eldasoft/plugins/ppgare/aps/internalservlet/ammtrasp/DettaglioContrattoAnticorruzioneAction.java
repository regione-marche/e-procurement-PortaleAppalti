package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.ammtrasp;

import java.util.Arrays;

import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.DettaglioContrattoType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.OperatoreInvitatoType;
import it.eldasoft.www.appalti.WSBandiEsitiAvvisi.OperatoreType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.ILeggeTrasparenzaManager;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ModelDriven;

public class DettaglioContrattoAnticorruzioneAction extends EncodedDataAction
	implements ModelDriven<DettaglioContrattoAnticorruzioneBean> {

	private static final long serialVersionUID = 5231224368853216870L;

	private ILeggeTrasparenzaManager leggeTrasparenzaManager;

	private DettaglioContrattoAnticorruzioneBean model = new DettaglioContrattoAnticorruzioneBean();
	private String codice;

	public void setLeggeTrasparenzaManager(ILeggeTrasparenzaManager leggeTrasparenzaManager) {
		this.leggeTrasparenzaManager = leggeTrasparenzaManager;
	}

	@Override
	public DettaglioContrattoAnticorruzioneBean getModel() {
		return this.model;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	/**
	 * ... 
	 */
	public String view() {
		boolean paramOk = true;

		if (this.codice == null) {
			this.addActionError("LABEL_RIFERIMENTO_PROCEDURA", IS_REQUIRED, null);
			paramOk = false;
		}

		if (SUCCESS.equals(this.getTarget()) && paramOk) {
			try {
				DettaglioContrattoType dettaglio = this.leggeTrasparenzaManager.getDettaglioContratto(null, codice);
				
				if(dettaglio != null) {
					model.setCig(dettaglio.getCig());
					model.setOggetto(dettaglio.getOggetto());
					model.setCodicefiscaleProponente(dettaglio.getStrutturaProponenteCF());
					model.setDenominazioneProponente(dettaglio.getStrutturaProponenteDenominazione());
					model.setSceltaContraente(dettaglio.getSceltaContraente());
					model.setImportoAggiudicazione(dettaglio.getImportoAggiudicazione());
					model.setImportoContratto(dettaglio.getImportoContratto());
					model.setImportoSommeLiquidate(dettaglio.getImportoSommeLiquidate());
					model.setDataInizio(dettaglio.getDataInizio());
					model.setDataUltimazione(dettaglio.getDataUltimazione());
					if (dettaglio.getElencoOperatoriInvitati() != null) {
						model.setElencoOperatoriInvitati(Arrays.asList(dettaglio.getElencoOperatoriInvitati()));
					}

					String[] partitaIvaAggiudicataria = null;
					String[] ragioneSocialeAggiudicataria = null;
					String[] codiceFiscaleAggiudicataria = null;
					OperatoreInvitatoType[] rtiAggiudicataria = null;
					
					if(dettaglio.getAggiudicatario() != null) {
						if(dettaglio.getAggiudicatario().length > 1) {
							//aggiudicatari multipli
							partitaIvaAggiudicataria = new String[dettaglio.getAggiudicatario().length];
							ragioneSocialeAggiudicataria = new String[dettaglio.getAggiudicatario().length];
							codiceFiscaleAggiudicataria = new String[dettaglio.getAggiudicatario().length];
							rtiAggiudicataria = new OperatoreInvitatoType[dettaglio.getAggiudicatario().length];
							
							int i = 0;
							for(OperatoreInvitatoType aggiudicatario : dettaglio.getAggiudicatario()) {
								if(aggiudicatario.getRti()){
									//memorizzo le componenti della RTI aggiudicataria
									OperatoreType[] rti =aggiudicatario.getComponentiRti();
									rtiAggiudicataria[i] = new OperatoreInvitatoType();
									rtiAggiudicataria[i].setComponentiRti(rti);
								}
							
								partitaIvaAggiudicataria[i] = aggiudicatario.getPartitaIva();
								ragioneSocialeAggiudicataria[i] = aggiudicatario.getRagioneSociale();
								codiceFiscaleAggiudicataria[i] = aggiudicatario.getCodiceFiscale();

								i++;
							}
						} else {
							//singolo aggiudicatario
							if(dettaglio.getAggiudicatario()[0].getComponentiRti()!= null) {
								//RTI
								OperatoreType[] rti = dettaglio.getAggiudicatario()[0].getComponentiRti();
								rtiAggiudicataria = new OperatoreInvitatoType[1];
								rtiAggiudicataria[0] = new OperatoreInvitatoType();
								rtiAggiudicataria[0].setComponentiRti(rti);
								ragioneSocialeAggiudicataria = new String[1];
								ragioneSocialeAggiudicataria[0] = dettaglio.getAggiudicatario()[0].getRagioneSociale();
								model.setRagioneSocialeAggiudicataria(ragioneSocialeAggiudicataria); 
								model.setRtiAggiudicataria(rtiAggiudicataria);
							} else {
								//Non RTI	
								partitaIvaAggiudicataria = new String[1];
								ragioneSocialeAggiudicataria = new String[1];
								codiceFiscaleAggiudicataria = new String[1];
								partitaIvaAggiudicataria[0] = dettaglio.getAggiudicatario()[0].getPartitaIva();
								ragioneSocialeAggiudicataria[0] = dettaglio.getAggiudicatario()[0].getRagioneSociale();
								codiceFiscaleAggiudicataria[0] = dettaglio.getAggiudicatario()[0].getCodiceFiscale();
							}
						}

					}
					model.setRtiAggiudicataria(rtiAggiudicataria);
					model.setPivaAggiudicataria(partitaIvaAggiudicataria);
					model.setRagioneSocialeAggiudicataria(ragioneSocialeAggiudicataria);
					model.setCodiceFiscaleAggiudicataria(codiceFiscaleAggiudicataria);
				} else {
					this.addActionError(this.getText("Errors.noDetails", new String[] {codice}));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "view");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		}
		return this.getTarget();
	}
	
}

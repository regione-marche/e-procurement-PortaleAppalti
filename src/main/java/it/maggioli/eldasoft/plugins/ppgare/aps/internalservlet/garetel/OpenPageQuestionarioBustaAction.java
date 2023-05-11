package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaGara;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaRiepilogo;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.dgue.DgueBuilder;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardDocumentiHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * Action di apertura delle pagine del questionario.
 *
 * @author 
 */
public class OpenPageQuestionarioBustaAction extends AbstractOpenPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4555621935721232511L;

	// target per la action
	private static final String MODULISTICA_CAMBIATA = "modulisticaCambiata";
	
	private int tipoBusta;
		
	private Long iddocdig;
	@Validate(EParamValidation.GENERIC)
	private String dgueLinkSecondHref;
	@Validate(EParamValidation.GENERIC)
	private String dgueLinkThirdHref;
	

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public Long getIddocdig() {
		return iddocdig;
	}

	public void setIddocdig(Long iddocdig) {
		this.iddocdig = iddocdig;
	}
	
	public String getDgueLinkSecondHref() {
		return dgueLinkSecondHref;
	}

	public void setDgueLinkSecondHref(String dgueLinkSecondHref) {
		this.dgueLinkSecondHref = dgueLinkSecondHref;
	}

	public String getDgueLinkThirdHref() {
		return dgueLinkThirdHref;
	}

	public void setDgueLinkThirdHref(String dgueLinkThirdHref) {
		this.dgueLinkThirdHref = dgueLinkThirdHref;
	}

	public static final int getBUSTA_AMMINISTRATIVA() {
		return PortGareSystemConstants.BUSTA_AMMINISTRATIVA;
	}
	
	public static final int getBUSTA_TECNICA() {
		return PortGareSystemConstants.BUSTA_TECNICA;
	}

	public static final int getBUSTA_ECONOMICA() {
		return PortGareSystemConstants.BUSTA_ECONOMICA;
	}

	public static final int getBUSTA_PRE_QUALIFICA() {
		return PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
	}

	/**
	 * restituisce l'helper Documenti Busta
	 * @throws Exception 
	 */
	protected WizardDocumentiBustaHelper getWizardDocumentiBustaHelper() throws Exception {
		BustaDocumenti busta = GestioneBuste.getBustaFromSession(this.tipoBusta);
		return busta.getHelperDocumenti();
	}

	/**
	 * (QCompiler) apri la pagina del questionario 
	 */
	public String openPage() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				WizardDocumentiBustaHelper documentiBustaHelper = getWizardDocumentiBustaHelper();
				
				// verifica se la modulistica del questionario e' cambiata in BO
				if(documentiBustaHelper.isQuestionarioModulisticaVariata()) {
					this.setTarget(MODULISTICA_CAMBIATA);
				}
				
				// per gare a lotti... 
				// ...aggiorna lo stato di primo accesso ai lotti (busta tecnica ed conomica)
				BustaRiepilogo bustaRiepilogo = GestioneBuste.getBustaRiepilogoFromSession();
				BustaDocumenti busta = bustaRiepilogo.getGestioneBuste().getBusta(this.tipoBusta);
				RiepilogoBusteHelper riepilogo = bustaRiepilogo.getHelper();
				
				if(this.tipoBusta == BustaGara.BUSTA_PRE_QUALIFICA) {
					if(riepilogo.getBustaPrequalifica() != null) {
						riepilogo.setPrimoAccessoPrequalificaEffettuato(true);
					}
				}
				if(this.tipoBusta == BustaGara.BUSTA_AMMINISTRATIVA) {
					if(riepilogo.getBustaAmministrativa() != null) {
						riepilogo.setPrimoAccessoAmministrativaEffettuato(true);
					}
				}
				if(this.tipoBusta == BustaGara.BUSTA_TECNICA) {
					if(riepilogo.getBusteTecnicheLotti() != null) {
					   if( !riepilogo.getPrimoAccessoTecnicheEffettuato().get(documentiBustaHelper.getCodice()) ) {
						   riepilogo.getPrimoAccessoTecnicheEffettuato().put(documentiBustaHelper.getCodice(), true);
					   }
					} else if(riepilogo.getBustaTecnica() != null) {
						riepilogo.setPrimoAccessoTecnicaEffettuato(true);
					}
				}
				if(this.tipoBusta == BustaGara.BUSTA_ECONOMICA) {
					if(riepilogo.getBusteEconomicheLotti() != null) {
					   if( !riepilogo.getPrimoAccessoEconomicheEffettuato().get(documentiBustaHelper.getCodice()) )	{
							riepilogo.getPrimoAccessoEconomicheEffettuato().put(documentiBustaHelper.getCodice(), true);
					   }
					} else if(riepilogo.getBustaTecnica() != null) {
						riepilogo.setPrimoAccessoEconomicaEffettuato(true);
					}
				} 

				ApsSystemUtils.getLogger().debug("Cerco il file del questionario se presente");
				if(documentiBustaHelper.getAdditionalDocs() != null) {
					QCQuestionario questionario = documentiBustaHelper.getQuestionarioAllegato(WizardDocumentiHelper.QUESTIONARIO_GARE_FILENAME);
					ApsSystemUtils.getLogger().debug("questionario.isNull? {}",(questionario==null));
					if(questionario!=null) {
						questionario.addServerFilesUuids(documentiBustaHelper);
						ApsSystemUtils.getLogger().debug("Impostati i serverFiles");
						String json = questionario.getQuestionario();
						documentiBustaHelper.updateQuestionario(json);
					}
				}
				
				// DGUE - genera i link "second" e "third" per la gestione del DGUE
				// Bisogna generare in link "first"
				DgueBuilder dgue = new DgueBuilder()
						.setCodiceGara(busta.getCodiceGara())
						.setCodiceLotto(busta.getCodiceLotto())
						.setDgueLinkActionRti("/do/FrontEnd/GareTel/dgueRti.action")
						.setDgueLinkAction("/do/FrontEnd/GareTel/dgue.action")
						.setGestioneBuste(busta.getGestioneBuste())
						.setTipoBusta(this.tipoBusta)
						.initLink23();
				this.dgueLinkSecondHref = dgue.getDgueLinkSecondHref();
				this.dgueLinkThirdHref = dgue.getDgueLinkThirdHref();
				iddocdig = dgue.getIddocdig();
				
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "openPage");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} 
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

}

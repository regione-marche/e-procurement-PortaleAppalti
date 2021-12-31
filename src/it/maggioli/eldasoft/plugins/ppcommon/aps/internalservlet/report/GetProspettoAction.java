package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report;

import it.eldasoft.report.datatypes.DatiRisultatoType;
import it.eldasoft.report.datatypes.DefinizioneReportType;
import it.eldasoft.report.datatypes.RisultatoReportType;
import it.eldasoft.www.Report.ValParametroType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.report.IReportManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Esegue la generazione del prospetto.
 * 
 * @author Stefano.Sabbadin
 */
public class GetProspettoAction extends BaseAction implements SessionAware {

	/** UID. */
	private static final long serialVersionUID = -3055422672154500420L;
	
	public String getReport() {
		String target = SUCCESS;

		try {
			ValParametroType[] parametri = (ValParametroType[]) this.session
					.get("parametriReport");
//			if (parametri == null) {
//				this.addActionError(this.getText("Errors.sessionExpired"));
//				target = CommonSystemConstants.PORTAL_ERROR;
//			} else {
				RisultatoReportType ris = this._reportManager
						.getRisultatoReport(this.id, parametri);
				if ("1".equals(ris.getTipoOutput())) {
					this.setDatiRisultato(ris.getDati());
				} else {
					this.filename = ris.getNomeFile();
					this.contentType = "application/octet-stream";
					this.inputStream = new ByteArrayInputStream(
							ris.getContenutoFile());
					target = "download";
				}
//			}
		} catch (ApsException e) {
			ApsSystemUtils.logThrowable(e, this, "getReport");
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}

		return target;
	}

//  /**
//   * Esegue la generazione del prospetto.
//   * 
//   * @param mapping
//   *          mapping della risposta
//   * @param form
//   *          form eventualmente ricevuto
//   * @param request
//   *          request HTTP
//   * @param response
//   *          response HTTP
//   * @return forward dell'action
//   * @throws Exception
//   *           eccezione ritornata in caso di errori inaspettati
//   *           nell'applicativo
//   */
//  public final ActionForward execute(final ActionMapping mapping,
//      final ActionForm form, final HttpServletRequest request,
//      final HttpServletResponse response) throws Exception {
//    logger.debug("execute: inizio metodo");
//
//    ActionForward forward =
//        mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
//
//    String codReport = request.getParameter("cod");
//    String numPagina = StringUtils.stripToNull(request.getParameter("pagina"));
//    Integer pagina = 1;
//    try {
//      pagina = Integer.valueOf(numPagina);
//    } catch (NumberFormatException e) {
//      logger.warn("Ricevuto numero di pagina in formato errato ("
//          + numPagina
//          + ")", e);
//    }
//
//    try {
//
//      ValParametroType[] parametri =
//          (ValParametroType[]) request.getSession().getAttribute(
//              "parametriReport");
//      if (parametri == null) {
//        forward = mapping.findForward(CostantiGeneraliStruts.FORWARD_ERROR);
//        logger.error("Sessione scaduta");
//        request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//            "Sessione scaduta");
//      } else {
//
//        GetRisultatoReportResponse risultato2 =
//            this.wfcManager.getRisultatoReport(codReport, parametri, pagina);
//
//        if (risultato2.getErrore() != null) {
//          forward = mapping.findForward(CostantiGeneraliStruts.FORWARD_ERROR);
//          logger.error("Errore durante l'elaborazione dell'operazione");
//          request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//              "Errore inaspettato durante l'elaborazione dell'operazione");
//          request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//              risultato2.getErrore());
//        } else {
//          RisultatoReportType estrazione = risultato2.getRisultatoReport();
//          if ("1".equals(estrazione.getTipoOutput())) {
//            // estrazione di una griglia di risultati
//            UtilityStruts.historyAdd(request, "Vai al prospetto di dettaglio",
//                "Prospetto di dettaglio");
//            request.setAttribute("dati", estrazione.getDati());
//            this.creaAttributiPaginazione(request, pagina, estrazione.getDati()
//                .getTotRecord(), estrazione.getDati().getMaxDimPagina());
//          } else {
//            // estrazione di un file da scaricare
//            UtilityWeb.download(estrazione.getNomeFile(),
//                estrazione.getContenutoFile(), response);
//            forward = null;
//          }
//        }
//      }
//    } catch (RemoteException e) {
//      forward = mapping.findForward(CostantiGeneraliStruts.FORWARD_ERROR);
//      logger.error(
//          "Impossibile connettersi al servizio per il reperimento dei dati", e);
//      request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//          "Impossibile connettersi al servizio per il reperimento dei dati");
//      request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//          e.getMessage());
//    } catch (XmlException e) {
//      forward = mapping.findForward(CostantiGeneraliStruts.FORWARD_ERROR);
//      logger.error("Errore durante l'interpretazione dei dati richiesti", e);
//      request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//          "Errore durante l'interpretazione dei dati richiesti");
//      request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//          e.getMessage());
//    } catch (Exception e) {
//      forward = mapping.findForward(CostantiGeneraliStruts.FORWARD_ERROR);
//      logger.error("Errore durante l'elaborazione dell'operazione");
//      request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//          "Errore inaspettato durante l'elaborazione dell'operazione");
//      request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//          e.getMessage());
//    }
//
//    logger.debug("execute: fine metodo");
//
//    return forward;
//  }

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @param reportManager
	 *            the reportManager to set
	 */
	public void setReportManager(IReportManager reportManager) {
		this._reportManager = reportManager;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param definizione
	 *            the definizione to set
	 */
	public void setDefinizione(DefinizioneReportType definizione) {
		this.definizione = definizione;
	}

	/**
	 * @return the definizione
	 */
	public DefinizioneReportType getDefinizione() {
		return definizione;
	}
	
	/**
	 * @param datiRisultatoType the datiRisultato to set
	 */
	public void setDatiRisultato(DatiRisultatoType datiRisultatoType) {
		this.datiRisultato = datiRisultatoType;
	}
	
	/**
	 * @return the datiRisultato
	 */
	public DatiRisultatoType getDatiRisultato() {
		return datiRisultato;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/** Sessione HTTP. */
	private Map<String, Object> session;
	
	/** Riferimento al web service Report */
	private IReportManager _reportManager;

	/** Codice del report. */
	private String id;

	/** Tipo di output del report. */
	private String type;

	/** Definizione del report. */
	private DefinizioneReportType definizione;
	
	/** Risultato estratto per il report. */ 
	private DatiRisultatoType datiRisultato;

	private InputStream inputStream;
	
	private String filename;
	
	private String contentType;

}
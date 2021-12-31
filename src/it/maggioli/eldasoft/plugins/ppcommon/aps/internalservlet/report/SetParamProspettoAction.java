package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import it.eldasoft.report.datatypes.DefinizioneReportType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.report.IReportManager;

import com.agiletec.apsadmin.system.BaseAction;

/**
 * Verifica la valorizzazione dei parametri e gestisce gli eventuali errori di
 * formato o mancanza di parametri.
 * 
 * @author Stefano.Sabbadin
 */
public class SetParamProspettoAction extends BaseAction implements SessionAware {

//  /**
//   * Verifica i parametri obbligatori mancanti ed il formato dei dati inseriti, segnalando gli eventuali errori all'utente.
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
//    String target = CostantiGeneraliStruts.FORWARD_OK;
//
//    String codReport = request.getParameter("cod");
//
//    try {
//      GetDefinizioneReportResponse risposta =
//          this.wfcManager.getDefinizioneReport(codReport);
//      if (risposta.getErrore() != null) {
//        target = CostantiGeneraliStruts.FORWARD_ERROR;
//        logger.error("Errore durante l'elaborazione dell'operazione");
//        request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//            "Errore inaspettato durante l'elaborazione dell'operazione");
//        request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//            risposta.getErrore());
//      } else {
//        DefinizioneReportType def = risposta.getDefinizione();
//
//        ValParametroType[] parametri =
//          new ValParametroType[def.getParametroArray().length];
//
//        for (int i = 0; i < def.getParametroArray().length; i++) {
//          DefParametroType defParametro = def.getParametroArray()[i];
//          String valore =
//              StringUtils.stripToNull(request.getParameter(defParametro
//                  .getCodice()));
//
//          if (defParametro.getObbligatorio() && valore == null) {
//            // errore: il parametro va valorizzato
//            target = "errorCheck";
//            logger.error("Il parametro "
//                + defParametro.getCodice()
//                + " è obbligatorio");
//            request.setAttribute(
//                CostantiGenerali.ATTR_DESC_ERRORE,
//                "Valorizzare il parametro obbligatorio \""
//                    + defParametro.getDescrizione()
//                    + "\"");
//          }
//
//          if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
//            if ("D".equals(defParametro.getTipo())
//                && !UtilityDate.isDataInFormato(valore,
//                    UtilityDate.FORMATO_GG_MM_AAAA)) {
//              target = "errorCheck";
//              logger.error("Errore nel formato del parametro "
//                  + defParametro.getCodice());
//              request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//                  "Le date devono essere specificate nel formato gg/mm/aaaa");
//            } else if ("I".equals(defParametro.getTipo())
//                && !valore.matches("[0-9]+")) {
//              target = "errorCheck";
//              logger.error("Errore nel formato del parametro "
//                  + defParametro.getCodice());
//              request
//                  .setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//                      "Gli interi devono essere specificati solo con cifre numeriche");
//            } else if ("F".equals(defParametro.getTipo())
//                && !valore.matches("[0-9]+(\\.[0-9]+)?")) {
//              target = "errorCheck";
//              logger.error("Errore nel formato del parametro "
//                  + defParametro.getCodice());
//              request
//                  .setAttribute(
//                      CostantiGenerali.ATTR_DESC_ERRORE,
//                      "I numeri decimali devono essere specificati solo con cifre numeriche separate dal carattere punto (.)");
//            }
//          }
//          
//          if ("errorCheck".equals(target)) {
//            // termino al primo errore
//            break;
//          } else {
//            // valorizzo il parametro
//            ValParametroType parametro = new ValParametroType();
//            parametro.setCodice(defParametro.getCodice());
//            parametro.setValore(StringUtils.stripToNull(request
//                .getParameter(defParametro.getCodice())));
//            parametri[i] = parametro;
//          }
//        }
//        
//        if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
//          // si inserisce in sessione il titolo per non richiederla ogni volta in caso di paginazione
//          request.getSession().setAttribute("titolo", def.getNome());
//          // si inseriscono in sessione i parametri per poter lanciare la paginazione
//          request.getSession().setAttribute("parametriReport", parametri);
//        }
//        
//      }
//    } catch (RemoteException e) {
//      target = CostantiGeneraliStruts.FORWARD_ERROR;
//      logger.error(
//          "Impossibile connettersi al servizio per il reperimento dei dati", e);
//      request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//          "Impossibile connettersi al servizio per il reperimento dei dati");
//      request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//          e.getMessage());
//    } catch (XmlException e) {
//      target = CostantiGeneraliStruts.FORWARD_ERROR;
//      logger.error("Errore durante l'interpretazione dei dati richiesti", e);
//      request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//          "Errore durante l'interpretazione dei dati richiesti");
//      request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//          e.getMessage());
//    } catch (Exception e) {
//      target = CostantiGeneraliStruts.FORWARD_ERROR;
//      logger.error("Errore durante l'elaborazione dell'operazione");
//      request.setAttribute(CostantiGenerali.ATTR_DESC_ERRORE,
//          "Errore inaspettato durante l'elaborazione dell'operazione");
//      request.setAttribute(CostantiGenerali.ATTR_MSG_TECNICO_ERRORE,
//          e.getMessage());
//    }
//
//    logger.debug("execute: fine metodo");
//
//    return mapping.findForward(target);
//  }

	/** UID. */
	private static final long serialVersionUID = -7828438661667780973L;

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

	/** Sessione HTTP. */
    private Map<String, Object> session;

    /** Riferimento al web service Report */
	private IReportManager _reportManager;

	/** Codice del report. */
	private String id;

	/** Definizione del report. */
	private DefinizioneReportType definizione;
}
package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati per i documenti
 *
 * @author Stefano.Sabbadin
 */
public class WizardDocumentiHelper extends DocumentiAllegatiHelper implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -747416711915050922L;

    // pdf generati per la domanda d'iscrizione e altre stampe pdf
	private List<File> pdfGenerati = null;
	private long questionarioId;

	public WizardDocumentiHelper() {
		super(null, null, null, true, true);
		this.pdfGenerati = new ArrayList<File>();
	}

	public List<File> getPdfGenerati() {
		return pdfGenerati;
	}
		

	public long getQuestionarioId() {
		return questionarioId;
	}

	public void setQuestionarioId(long questionarioId) {
		this.questionarioId = questionarioId;
	}

	/**
	 * (QCompiler/QFORM) verifica se e' presente un questionario associato alla busta in BO
	 * 
	 * @throws ApsException 
	 */
	public static QuestionarioType getQuestionarioAssociatoBO(
			String codice,
			String tipologia, 
			int tipoBusta) 
		throws ApsException 
	{
		IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
			.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());		
	
		List<QuestionarioType> q = bandiManager.getQuestionari(
				codice, 
				null, // <-- "tipologia" attualmente non usato!!!
				null);	// ??? per gli elenchi operatore qual è la "tipologia" ?
		
		QuestionarioType questionario = null;
		if(q != null && q.size() > 0) {
			questionario = q.get(0);
		}
		
		return questionario;
	}

	/**
	 * (QCompiler/QFORM)
	 * verifica se la modulistica del questionario e' cambiata in BO 
	 */
	public boolean isQuestionarioModulisticaVariata() {
		long id = (this.questionarioId > 0 ? this.questionarioId : 0);
		long idModello = (this.questionarioAssociato != null ? this.questionarioAssociato.getId() : 0);		
		return (idModello != id); 
	}

	/**
	 * (QCompiler/QFORM)
	 * verifica se la modulistica del questionario e' cambiata in BO 
	 */
	public boolean isQuestionarioCompletato() {
		boolean completato = false; 
		if(this.questionarioAssociato != null 
		   && StringUtils.isNotEmpty(this.questionarioAssociato.getOggetto())) 
		{
			QCQuestionario q = new QCQuestionario( this.questionarioAssociato.getOggetto() );
			completato = q.getValidationStatus() && q.getSummaryGenerated(); 
		}
		return completato;
	}

}

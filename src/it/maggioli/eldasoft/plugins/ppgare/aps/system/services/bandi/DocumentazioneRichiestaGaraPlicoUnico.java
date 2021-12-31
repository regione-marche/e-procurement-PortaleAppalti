package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi;

import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;

/**
 * Wrapper per il mantenimento del set di documenti richiesti, suddivisi per
 * busta e per lotto, nel caso di gara a plico unico con offerte distinte.
 * 
 * @author Stefano.Sabbadin
 * 
 */
public class DocumentazioneRichiestaGaraPlicoUnico {

	private DocumentazioneRichiestaType[] documentiBustaAmministrativa;
	private LottoDettaglioGaraType[] lotti;

	/**
	 * @return the documentiBustaAmministrativa
	 */
	public DocumentazioneRichiestaType[] getDocumentiBustaAmministrativa() {
		return documentiBustaAmministrativa;
	}

	/**
	 * @param documentiBustaAmministrativa
	 *            the documentiBustaAmministrativa to set
	 */
	public void setDocumentiBustaAmministrativa(
			DocumentazioneRichiestaType[] documentiBustaAmministrativa) {
		this.documentiBustaAmministrativa = documentiBustaAmministrativa;
	}

	/**
	 * @return the lotti
	 */
	public LottoDettaglioGaraType[] getLotti() {
		return lotti;
	}

	/**
	 * @param lotti
	 *            the lotti to set
	 */
	public void setLotti(LottoDettaglioGaraType[] lotti) {
		this.lotti = lotti;
	}

}

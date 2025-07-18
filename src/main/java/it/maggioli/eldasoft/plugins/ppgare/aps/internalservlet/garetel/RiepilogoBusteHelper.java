package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBustePartecipazioneType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoDocumentoAllegatoType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoDocumentoRichiestoType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoLottoBustaType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoAllegatoBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.DocumentoMancanteBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Helper di gestione della busta di riepilogo   
 * 
 * ... 
 */
public class RiepilogoBusteHelper implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2764661710232563418L;
	
	private RiepilogoBustaBean bustaAmministrativa;
	private RiepilogoBustaBean bustaTecnica;
	private RiepilogoBustaBean bustaEconomica;
	private RiepilogoBustaBean bustaPrequalifica;
	private LinkedHashMap<String, RiepilogoBustaBean> busteTecnicheLotti;
	private LinkedHashMap<String, RiepilogoBustaBean> busteEconomicheLotti;
	private LinkedHashMap<String, String> listaCodiciInterniLotti;
	private List<String> listaCompletaLotti;
	private List<String> listaLottiRimossi;
	private Long idComunicazione;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	private Boolean primoAccessoPrequalificaEffettuato;
	private Boolean primoAccessoAmministrativaEffettuato;
	private Boolean primoAccessoTecnicaEffettuato;
	private Boolean primoAccessoEconomicaEffettuato;
	private LinkedHashMap<String, Boolean> primoAccessoTecnicheEffettuato;
	private LinkedHashMap<String, Boolean> primoAccessoEconomicheEffettuato;
	private List<SoggettoFirmatarioImpresaHelper> ultimiFirmatariInseriti;
	private WizardDatiImpresaHelper impresa;
	private boolean modified;						// indica se lo stato dell'helper e' cambiato e  va salvata la relativa FS10R/FS11R
	@Validate(EParamValidation.ERRORE)
	private String documentazioneMancanteError;
	
	public RiepilogoBustaBean getBustaAmministrativa() {
		return bustaAmministrativa;
	}
	
	public void setBustaAmministrativa(RiepilogoBustaBean bustaAmministrativa) {
		this.bustaAmministrativa = bustaAmministrativa;
	}
	
	public RiepilogoBustaBean getBustaTecnica() {
		return bustaTecnica;
	}
	
	public void setBustaTecnica(RiepilogoBustaBean bustaTecnica) {
		this.bustaTecnica = bustaTecnica;
	}
	
	public RiepilogoBustaBean getBustaEconomica() {
		return bustaEconomica;
	}
	
	public void setBustaEconomica(RiepilogoBustaBean bustaEconomica) {
		this.bustaEconomica = bustaEconomica;
	}
	
	public RiepilogoBustaBean getBustaPrequalifica() {
		return bustaPrequalifica;
	}
	
	public void setBustaPrequalifica(RiepilogoBustaBean bustaPrequalifica) {
		this.bustaPrequalifica = bustaPrequalifica;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getCodiceGara() {
		return codiceGara;
	}
	
	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}
	
	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public List<String> getListaCompletaLotti() {
		return listaCompletaLotti;
	}

	public void setListaCompletaLotti(List<String> listaCompletaLotti) {
		this.listaCompletaLotti = listaCompletaLotti;
	}

	public LinkedHashMap<String, RiepilogoBustaBean> getBusteTecnicheLotti() {
		return busteTecnicheLotti;
	}

	public void setBusteTecnicheLotti(LinkedHashMap<String, RiepilogoBustaBean> busteTecnicheLotti) {
		this.busteTecnicheLotti = busteTecnicheLotti;
	}

	public LinkedHashMap<String, RiepilogoBustaBean> getBusteEconomicheLotti() {
		return busteEconomicheLotti;
	}

	public List<String> getListaLottiRimossi() {
		return listaLottiRimossi;
	}

	public void setListaLottiRimossi(List<String> listaLottiRimossi) {
		this.listaLottiRimossi = listaLottiRimossi;
	}

	public void setBusteEconomicheLotti(LinkedHashMap<String, RiepilogoBustaBean> busteEconomicheLotti) {
		this.busteEconomicheLotti = busteEconomicheLotti;
	}	

	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	public void setImpresa(WizardDatiImpresaHelper impresa) {
		this.impresa = impresa;
	}
	
	public List<SoggettoFirmatarioImpresaHelper> getUltimiFirmatariInseriti() {
		return ultimiFirmatariInseriti;
	}
	
	public void setUltimiFirmatariInseriti(List<SoggettoFirmatarioImpresaHelper>ultimiFirmatariInseriti) {
		this.ultimiFirmatariInseriti = ultimiFirmatariInseriti;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	public HashMap<String, String> getListaCodiciInterniLotti() {
		return listaCodiciInterniLotti;
	}
	
	public void setListaCodiciInterniLotti(LinkedHashMap<String, String> listaCodiciInterniLotti) {
		this.listaCodiciInterniLotti = listaCodiciInterniLotti;
	}
	
	public Boolean getPrimoAccessoPrequalificaEffettuato() {
		return primoAccessoPrequalificaEffettuato;
	}

	public void setPrimoAccessoPrequalificaEffettuato(Boolean primoAccessoPrequalificaEffettuato) {
		this.primoAccessoPrequalificaEffettuato = primoAccessoPrequalificaEffettuato;
	}

	public Boolean getPrimoAccessoAmministrativaEffettuato() {
		return primoAccessoAmministrativaEffettuato;
	}

	public void setPrimoAccessoAmministrativaEffettuato(Boolean primoAccessoAmministrativaEffettuato) {
		this.primoAccessoAmministrativaEffettuato = primoAccessoAmministrativaEffettuato;
	}

	public Boolean getPrimoAccessoTecnicaEffettuato() {
		return primoAccessoTecnicaEffettuato;
	}

	public void setPrimoAccessoTecnicaEffettuato(Boolean primoAccessoTecnicaEffettuato) {
		this.primoAccessoTecnicaEffettuato = primoAccessoTecnicaEffettuato;
	}

	public Boolean getPrimoAccessoEconomicaEffettuato() {
		return primoAccessoEconomicaEffettuato;
	}

	public void setPrimoAccessoEconomicaEffettuato(Boolean primoAccessoEconomiaEffettuato) {
		this.primoAccessoEconomicaEffettuato = primoAccessoEconomiaEffettuato;
	}

	public LinkedHashMap<String, Boolean> getPrimoAccessoTecnicheEffettuato() {
		return primoAccessoTecnicheEffettuato;
	}
	
	public void setPrimoAccessoTecnicheEffettuato(LinkedHashMap<String, Boolean> primoAccessoTecnicheEffettuato) {
		this.primoAccessoTecnicheEffettuato = primoAccessoTecnicheEffettuato;
	}
	
	public LinkedHashMap<String, Boolean> getPrimoAccessoEconomicheEffettuato() {
		return primoAccessoEconomicheEffettuato;
	}
	
	public void setPrimoAccessoEconomicheEffettuato(LinkedHashMap<String, Boolean> primoAccessoEconomicheEffettuato) {
		this.primoAccessoEconomicheEffettuato = primoAccessoEconomicheEffettuato;
	}
	
	public String getDocumentazioneMancanteError() {
		return documentazioneMancanteError;
	}

	/**
	 * costruttore 
	 */
	public RiepilogoBusteHelper(
			IBandiManager bandiManager, 
			String codiceGara, 
			String codiceLotto, 
			String tipoImpresa, 
			boolean isRti, 
			boolean isPlicoUnicoOfferteDistinte, 
			String tipoComunicazione, 
			String username,
			String progressivoOfferta,
			List<String> lottiAttivi) throws ApsException
	{	
		this.bustaPrequalifica = null;
		this.bustaAmministrativa = null;
		this.bustaTecnica = null;
		this.bustaEconomica = null;
		
		if(tipoComunicazione.equals(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT)) {
			bustaPrequalifica = new RiepilogoBustaBean(retrieveDocumentazionerichiestaDb(
					bandiManager,
					codiceGara,
					codiceLotto,
					tipoImpresa,
					isRti,
					PortGareSystemConstants.BUSTA_PRE_QUALIFICA));
		} else if(tipoComunicazione.equals(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT)) {
			bustaAmministrativa = new RiepilogoBustaBean(retrieveDocumentazionerichiestaDb(
					bandiManager, 
					codiceGara, 
					codiceLotto, 
					tipoImpresa, 
					isRti, 
					PortGareSystemConstants.BUSTA_AMMINISTRATIVA));

			if(!isPlicoUnicoOfferteDistinte) {
				bustaTecnica = new RiepilogoBustaBean(retrieveDocumentazionerichiestaDb(
						bandiManager, 
						codiceGara, 
						codiceLotto, 
						tipoImpresa, 
						isRti, 
						PortGareSystemConstants.BUSTA_TECNICA));
				bustaEconomica = new RiepilogoBustaBean(retrieveDocumentazionerichiestaDb(
						bandiManager, 
						codiceGara, 
						codiceLotto, 
						tipoImpresa, 
						isRti, 
						PortGareSystemConstants.BUSTA_ECONOMICA));
			} 
		} else {
			// esiste un altro tipo di richiesta da gestire ???
		}
		
		if( !isPlicoUnicoOfferteDistinte ) {
			this.busteEconomicheLotti = null;
			this.busteTecnicheLotti = null;
			this.listaCompletaLotti = null;
			this.listaLottiRimossi = null;
			this.primoAccessoPrequalificaEffettuato = false;
			this.primoAccessoAmministrativaEffettuato = false;
			this.primoAccessoEconomicaEffettuato = false;
			this.primoAccessoTecnicaEffettuato = false;
		} else {
			this.busteEconomicheLotti = new LinkedHashMap<String, RiepilogoBustaBean>();
			this.busteTecnicheLotti = new LinkedHashMap<String, RiepilogoBustaBean>();
			this.listaCodiciInterniLotti = new LinkedHashMap<String, String>();
			this.listaLottiRimossi = new ArrayList<String>();
			
			this.primoAccessoPrequalificaEffettuato = false;
			this.primoAccessoAmministrativaEffettuato = false;
			this.primoAccessoEconomicheEffettuato = new LinkedHashMap<String, Boolean>();
			this.primoAccessoTecnicheEffettuato = new LinkedHashMap<String, Boolean>();

			this.listaCompletaLotti = this.retrieveListaLotti(bandiManager, codiceGara, username);
			
			for(int i = 0; i < this.listaCompletaLotti.size(); i++) {
				this.busteTecnicheLotti.put(this.listaCompletaLotti.get(i), null);
				this.busteEconomicheLotti.put(this.listaCompletaLotti.get(i), null);
				this.primoAccessoEconomicheEffettuato.put(this.listaCompletaLotti.get(i), false);
				this.primoAccessoTecnicheEffettuato.put(this.listaCompletaLotti.get(i), false);
			}
		}
		
		if(isRti) {
			// le strutture UltimiFirmatariInseriti e CorrelazioniFirmatari
			// verranno utilizzate solo in caso di RTI
			this.setUltimiFirmatariInseriti(new ArrayList<SoggettoFirmatarioImpresaHelper>());
		}
		
		// (Questionari - QFORM)
		// verifica se esistono questionari associati alle relative buste
		List<QuestionarioType> q = bandiManager.getQuestionari(codiceGara, null, null);
		if(q != null) {
			for(int i = 0; i < q.size(); i++) {
				Integer tipoBusta = Integer.parseInt(q.get(i).getBusta());
				String codice;
				switch (tipoBusta) {
				case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
					this.aggiornaStatoQuestionarioBusta(this.bustaPrequalifica, q.get(i));
					break;

				case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
					this.aggiornaStatoQuestionarioBusta(this.bustaAmministrativa, q.get(i));
					break;
					
				case PortGareSystemConstants.BUSTA_TECNICA:
					if (this.listaCompletaLotti == null) {
						// gara a lotto unico
						this.aggiornaStatoQuestionarioBusta(this.bustaTecnica, q.get(i));
					} else {
						// gara a plico unico con offerte distinte
						codice = q.get(i).getChiave1();
						if(lottiAttivi.contains(codice)) {
							RiepilogoBustaBean riepilogoBusta = new RiepilogoBustaBean();
							this.aggiornaStatoQuestionarioBusta(riepilogoBusta, q.get(i));
							this.busteTecnicheLotti.put(codice, riepilogoBusta);
						}
					}
					break;

				case PortGareSystemConstants.BUSTA_ECONOMICA:
					if (this.listaCompletaLotti == null) {
						// gara a lotto unico
						this.aggiornaStatoQuestionarioBusta(this.bustaEconomica, q.get(i));
					} else {
						// gara a plico unico con offerte distinte
						codice = q.get(i).getChiave1();
						if(lottiAttivi.contains(codice)) {
							RiepilogoBustaBean riepilogoBusta = new RiepilogoBustaBean();
							this.aggiornaStatoQuestionarioBusta(riepilogoBusta, q.get(i));
							this.busteEconomicheLotti.put(codice, riepilogoBusta);
						}
					}
					break;
				}
			}
		}
		
		this.idComunicazione = null;
		this.codiceGara = codiceGara;
		this.codice = codiceLotto;
		this.progressivoOfferta = progressivoOfferta;
		this.modified = false;
		this.documentazioneMancanteError = null;
		this.modified = false;
	}

	/**
	 * Genera l'XML con i dati per la busta riepilogativa dell'offerta/domanda partecipazione.
	 * 
	 * @return documento xml
	 */
	public XmlObject getXmlDocument(){
		XmlObject document = null;

		// verifica se si tratta di una richiesta di offerta (bustaAmministativa != null) 
		// o una richiesta di partecipazione (bustaPrequalifica != null)
		// e genera il documento xml relativo...
		if(bustaAmministrativa != null) {
			document = this.getXmlDocumentOfferta();
		}
		if(bustaPrequalifica != null) {
			document = this.getXmlDocumentPartecipazione();
		}

		return document;
	}

	/**
	 * Genera l'XML con i dati per la busta riepilogativa dell'offerta
	 * 
	 * @return documento xml
	 */
	private XmlObject getXmlDocumentOfferta() {
		if(bustaAmministrativa == null) {
			return null;
		} 
		
		RiepilogoBusteOffertaDocument doc = RiepilogoBusteOffertaDocument.Factory.newInstance();
		RiepilogoBusteOffertaType riepilogoBuste = doc.addNewRiepilogoBusteOfferta();

		// SCRITTURA BUSTA AMMINISTRATIVA  
		this.aggiungiDocumentazione(riepilogoBuste.addNewBustaAmministrativa(), PortGareSystemConstants.BUSTA_AMMINISTRATIVA);
		
		// SCRITTURA BUSTA TECNICA 
		this.aggiungiDocumentazione(riepilogoBuste.addNewBustaTecnica(), PortGareSystemConstants.BUSTA_TECNICA);

		// SCRITTURA BUSTA ECONOMICA 
		this.aggiungiDocumentazione(riepilogoBuste.addNewBustaEconomica(), PortGareSystemConstants.BUSTA_ECONOMICA);
		
		RiepilogoBustaType bustaAmm = riepilogoBuste.getBustaAmministrativa();
		if(bustaAmm != null) {
			bustaAmm.setPresaVisioneDocumenti(primoAccessoAmministrativaEffettuato);
		}
		
		if(listaCompletaLotti == null) {
			// GARA NO LOTTI
			RiepilogoBustaType bustaTec = riepilogoBuste.getBustaTecnica();
			if(bustaTec != null) {
				bustaTec.setPresaVisioneDocumenti(primoAccessoTecnicaEffettuato);
			}
			
			RiepilogoBustaType bustaEco = riepilogoBuste.getBustaEconomica();
			if(bustaEco != null) {
				bustaEco.setPresaVisioneDocumenti(primoAccessoEconomicaEffettuato);
			}
		}

		if(listaCompletaLotti != null) {
			// GARA A LOTTI
			for(int i = 0; i < listaCompletaLotti.size(); i++) {
				RiepilogoBustaBean currBustaTecnica = busteTecnicheLotti.get(listaCompletaLotti.get(i));
				RiepilogoBustaBean currBustaEconomica = busteEconomicheLotti.get(listaCompletaLotti.get(i));

				// verifica se per il lotto corrente e' presente a busta tecnica od economica...
				RiepilogoLottoBustaType lotto = null;
				
				if(currBustaTecnica != null) {
					lotto = riepilogoBuste.addNewLotto();
					lotto.setOggetto(currBustaTecnica.getOggetto());
					lotto.setCodiceLotto(listaCompletaLotti.get(i));
					
					if(busteTecnicheLotti.get(listaCompletaLotti.get(i)) != null) {
//						lotto.setOggetto( currBustaTecnica.getOggetto());
						lotto.setCodiceInterno(listaCodiciInterniLotti.get(listaCompletaLotti.get(i)));
						
						RiepilogoBustaType bustaTecnicaLotto = lotto.addNewBustaTecnica();
						bustaTecnicaLotto.setPresaVisioneDocumenti(primoAccessoTecnicheEffettuato.get(listaCompletaLotti.get(i)));
						
						// documenti inseriti tecnica 
						if(currBustaTecnica != null && currBustaTecnica.getDocumentiInseriti() != null) {
							for(int j = 0; j < currBustaTecnica.getDocumentiInseriti().size(); j++) {
								DocumentoAllegatoBean currDocument = currBustaTecnica.getDocumentiInseriti().get(j);
								RiepilogoDocumentoAllegatoType documentoInserito = bustaTecnicaLotto.addNewDocumentoAllegato();
								documentoInserito.setDescrizione(currDocument.getDescrizione());
								documentoInserito.setDimensione(currDocument.getDimensione());
								if(currDocument.getId() != null && currDocument.getId() > 0) {
									documentoInserito.setId(currDocument.getId());
								}
								documentoInserito.setNomeFile(currDocument.getNomeFile());
								documentoInserito.setSha1(currDocument.getSha1());
								if(StringUtils.isNotEmpty(currDocument.getUuid())) {
									documentoInserito.setUuid(currDocument.getUuid());
								}
							}
							// documenti mancanti tecnica 
							for(int j = 0; j < currBustaTecnica.getDocumentiMancanti().size(); j++) {
								DocumentoMancanteBean currDocument = currBustaTecnica.getDocumentiMancanti().get(j);
								RiepilogoDocumentoRichiestoType documentoMancante = bustaTecnicaLotto.addNewDocumentoRichiestoMancante();
								documentoMancante.setDescrizione(currDocument.getDescrizione());
								if(currDocument.getId() != null && currDocument.getId() > 0) {
									documentoMancante.setId(currDocument.getId());
								}
								documentoMancante.setObbligatorio(currDocument.isObbligatorio());
							}
	                        // QFORM - gestione questionario
	                        if(currBustaTecnica.isQuestionarioPresente()) {
	                        	currBustaTecnica.aggiornaRiepilogoQuestionario(bustaTecnicaLotto);
	                        }
						}
					}
				}
				
				if(currBustaEconomica != null) {
					// verifica se il lotto i-esimo e' gia' stato aggiunto 
					// altrimenti aggiungilo ora...
					boolean aggiungiLotto = (lotto == null);
					if(aggiungiLotto) {
						lotto = riepilogoBuste.addNewLotto();
						lotto.setOggetto(currBustaEconomica.getOggetto());
						lotto.setCodiceLotto(listaCompletaLotti.get(i));
					}
					
					RiepilogoBustaType bustaEconomicaLotto = lotto.addNewBustaEconomica();
					bustaEconomicaLotto.setPresaVisioneDocumenti(primoAccessoEconomicheEffettuato.get(listaCompletaLotti.get(i)));
					if( currBustaEconomica != null && currBustaEconomica.getDocumentiInseriti() != null ) {
						if(aggiungiLotto) {
//							lotto.setOggetto(currBustaEconomica.getOggetto());
							lotto.setCodiceInterno(listaCodiciInterniLotti.get(listaCompletaLotti.get(i)));
						}

						// documenti inseriti economica 
						for(int j = 0; j < currBustaEconomica.getDocumentiInseriti().size(); j++) {
							DocumentoAllegatoBean currDocument = currBustaEconomica.getDocumentiInseriti().get(j);
							RiepilogoDocumentoAllegatoType documentoInserito = bustaEconomicaLotto.addNewDocumentoAllegato();
							documentoInserito.setDescrizione(currDocument.getDescrizione());
							documentoInserito.setDimensione(currDocument.getDimensione());
							if(currDocument.getId() != null && currDocument.getId() > 0) {
								documentoInserito.setId(currDocument.getId());
							}
							documentoInserito.setNomeFile(currDocument.getNomeFile());
							documentoInserito.setSha1(currDocument.getSha1());
							if(StringUtils.isNotEmpty(currDocument.getUuid())) {
								documentoInserito.setUuid(currDocument.getUuid());
							}
						}
						// documenti mancanti economica 
						for(int j = 0; j < currBustaEconomica.getDocumentiMancanti().size(); j++) {
							DocumentoMancanteBean currDocument = currBustaEconomica.getDocumentiMancanti().get(j);
							RiepilogoDocumentoRichiestoType documentoMancante = bustaEconomicaLotto.addNewDocumentoRichiestoMancante();
							documentoMancante.setDescrizione(currDocument.getDescrizione());
							if(currDocument.getId() != null && currDocument.getId() > 0) {
								documentoMancante.setId(currDocument.getId());
							}
							documentoMancante.setObbligatorio(currDocument.isObbligatorio());
						}
						// gestione questionario
		                if(currBustaEconomica.isQuestionarioPresente()) {
		                	currBustaEconomica.aggiornaRiepilogoQuestionario(bustaEconomicaLotto);
		                }
					}
				}
			}
		}
		
		// ultimi firmatari memorizzati per rti 
		if(ultimiFirmatariInseriti != null) {
			for(int i = 0; i < ultimiFirmatariInseriti.size(); i++) {
				SoggettoFirmatarioImpresaHelper firmatario = ultimiFirmatariInseriti.get(i);
				
				if(firmatario != null) {
					FirmatarioType f = riepilogoBuste.addNewFirmatario();
					f.setNome( firmatario.getNome() != null ? firmatario.getNome() : "" );
					f.setCognome( firmatario.getCognome() != null ? firmatario.getCognome() : "" );
					f.setCodiceFiscaleFirmatario( firmatario.getCodiceFiscale() != null ? firmatario.getCodiceFiscale() : "" );
					f.setNazione( firmatario.getNazione() != null ? firmatario.getNazione() : "" );
					f.setProvinciaNascita( firmatario.getProvinciaNascita() != null ? firmatario.getProvinciaNascita() : "" );
					f.setSesso( firmatario.getSesso() != null ? firmatario.getSesso() : "" );
					f.setDataNascita(CalendarValidator.getInstance().validate(firmatario.getDataNascita(), "dd/MM/yyyy"));
					f.setComuneNascita( firmatario.getComuneNascita() != null ? firmatario.getComuneNascita() : "" );
					f.setQualifica( firmatario.getSoggettoQualifica() != null ? firmatario.getSoggettoQualifica() : "" );
					if(StringUtils.isBlank(firmatario.getSoggettoQualifica())) {
						f.setQualifica("libero professionista");
					}
					
					IndirizzoType residenza = f.addNewResidenza();
					residenza.setCap( firmatario.getCap() != null ? firmatario.getCap() : "" );
					residenza.setIndirizzo( firmatario.getIndirizzo() != null ? firmatario.getIndirizzo() : "" );
					residenza.setNumCivico( firmatario.getNumCivico() != null ? firmatario.getNumCivico() : "" );
					residenza.setComune( firmatario.getComune() != null ? firmatario.getComune() : "" );
					residenza.setNazione( firmatario.getNazione() != null ? firmatario.getNazione() : "" );
					residenza.setProvincia( firmatario.getProvincia() != null ? firmatario.getProvincia() : "" );
					
					f.setPartitaIVAImpresa( firmatario.getPartitaIvaImpresa() != null ? firmatario.getPartitaIvaImpresa() : "" );
					f.setCodiceFiscaleImpresa( firmatario.getCodiceFiscaleImpresa() != null ? firmatario.getCodiceFiscaleImpresa() : "" );
				}
			}
		}

		doc.documentProperties().setEncoding("UTF-8");
		return doc;
	}
	
	/**
	 * Genera l'XML con i dati per la busta riepilogativa della domanda di partecipazione
	 * 
	 * @return documento xml
	 */
	private XmlObject getXmlDocumentPartecipazione() {
		if(bustaPrequalifica == null) {
			return null;
		}
		
		RiepilogoBustePartecipazioneDocument doc = RiepilogoBustePartecipazioneDocument.Factory.newInstance();
		RiepilogoBustePartecipazioneType riepilogoBuste = doc.addNewRiepilogoBustePartecipazione();

		// SCRITTURA BUSTA PREQUALIFICA  
		this.aggiungiDocumentazione(riepilogoBuste.addNewBustaPrequalifica(), PortGareSystemConstants.BUSTA_PRE_QUALIFICA);
	
		RiepilogoBustaType bustaPreq = riepilogoBuste.getBustaPrequalifica();
		if(bustaPreq  != null) {
			bustaPreq.setPresaVisioneDocumenti(primoAccessoPrequalificaEffettuato);
		}
		
		// in fase di prequalifica per una gara a lotti e' possibile che ogni  
		// lotto abbia requisiti siano lotti in fase prequalifica aver 
		//if(listaCompletaLotti != null) {
		//	for(int i = 0; i < listaCompletaLotti.size(); i++) {
		//		...
		//	}
		//}

		doc.documentProperties().setEncoding("UTF-8");
		return doc;
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione estratta da un WizardDocumentiBustaHelper e 
	 * trasferirlo all'interno di un xml
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry presenti nell'helper
	 * @param int tipologia della busta (1 = AMMINISTRATIVA, 2 = TECNICA, 3 = ECONOMICA)
	 * 
	 * **/
	private void aggiungiDocumentazione(RiepilogoBustaType documentazione, int tipoBusta) {
		if(tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
			// Busta di prequalifica a null quando si parte da situazione 
			// completamente pulita e si torna da inizio compilazione domanda 
			// di partecipazione 
			if(this.bustaPrequalifica != null) {
				if(this.bustaPrequalifica.getDocumentiInseriti() != null) {
					this.aggiungiDocumentiInseritiPrequalifica(documentazione, this.bustaPrequalifica.getDocumentiInseriti());
				}
				if(this.bustaPrequalifica.getDocumentiInseriti().isEmpty() && this.bustaPrequalifica.getDocumentiMancanti().isEmpty()) {
					// mancano tutti i documenti richiesti
					// N.B questa parte � stata implementata per coprire il caso 
					// in cui non vi siano comunicazioni FS10A e si accedesse 
					// alla voce di riepilogo, in questo modo vengono visualizzati 
					// immediatamente i documenti mancanti (questo vale anche 
					// per la busta tecnica e la busta economica)
					this.aggiungiTuttiRichiestiAMancantiPrequalifica(documentazione);
					
				} else if( !this.bustaPrequalifica.getDocumentiMancanti().isEmpty() ) {
					// mancano una parte di documenti richiesti
					this.aggiungiDocumentiMancantiPrequalifica(documentazione, this.bustaPrequalifica.getDocumentiMancanti());
				}

				// gestione questionario
				if(this.bustaPrequalifica.isQuestionarioPresente()) {
					this.bustaPrequalifica.aggiornaRiepilogoQuestionario(documentazione);
				}
			}
		}
		
		if(tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			/* Busta amministrativa a null quando si parte da situazione completamente pulita e si torna da iniza compilazione offerta */
			if(this.bustaAmministrativa != null) {
				if(this.bustaAmministrativa.getDocumentiInseriti() != null) {
					this.aggiungiDocumentiInseritiAmministrativa(documentazione, this.bustaAmministrativa.getDocumentiInseriti());
				}
				if(this.bustaAmministrativa.getDocumentiInseriti().isEmpty() && this.bustaAmministrativa.getDocumentiMancanti().isEmpty()) {
					//mancano tutti i documenti richiesti
					//N.B questa parte � stata implementata per coprire il caso in cui non vi siano comunicazioni FS11A/B/C 
					//e si accedesse alla voce di riepilogo, in questo modo vengono visualizzati immediatamente
					//i documenti mancanti (questo vale anche per la busta tecnica e la busta economica)
					this.aggiungiTuttiRichiestiAMancantiAmministrativa(documentazione);	
					
				} else if( !this.bustaAmministrativa.getDocumentiMancanti().isEmpty() ) {
					//mancano una parte di documenti richiesti
					this.aggiungiDocumentiMancantiAmministrativa(documentazione, this.bustaAmministrativa.getDocumentiMancanti());
				}
				
				// gestione questionario
				if(this.bustaAmministrativa.isQuestionarioPresente()) {
					this.bustaAmministrativa.aggiornaRiepilogoQuestionario(documentazione);
				}
			}
		} else if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
			if(this.bustaTecnica != null) {
				if(!this.bustaTecnica.getDocumentiInseriti().isEmpty()) {
					this.aggiungiDocumentiInseritiTecnica(documentazione, this.bustaTecnica.getDocumentiInseriti());
				}
				if(this.bustaTecnica.getDocumentiInseriti().isEmpty() && this.bustaTecnica.getDocumentiMancanti().isEmpty()) {
					this.aggiungiTuttiRichiestiAMancantiTecnica(documentazione);
				}
				else if( !this.bustaTecnica.getDocumentiMancanti().isEmpty() ) {
					this.aggiungiDocumentiMancantiTecnica(documentazione, this.bustaTecnica.getDocumentiMancanti());
				}
				
				// gestione questionario
				if(this.bustaTecnica.isQuestionarioPresente()) {
					this.bustaTecnica.aggiornaRiepilogoQuestionario(documentazione);
				}
			}
		} else {
			if(this.bustaEconomica != null) {
				if(!this.bustaEconomica.getDocumentiInseriti().isEmpty()) {
					this.aggiungiDocumentiInseritiEconomica(documentazione, this.bustaEconomica.getDocumentiInseriti());
				}
				if(this.bustaEconomica.getDocumentiMancanti().isEmpty() && this.bustaEconomica.getDocumentiInseriti().isEmpty()) {
					this.aggiungiTuttiRichiestiAMancantiEconomica(documentazione);
				}
				else if(!this.bustaEconomica.getDocumentiMancanti().isEmpty()) {
					this.aggiungiDocumentiMancantiEconomica(documentazione, this.bustaEconomica.getDocumentiMancanti());
				}
				
				// gestione questionario
				if(this.bustaEconomica.isQuestionarioPresente()) {
					this.bustaEconomica.aggiornaRiepilogoQuestionario(documentazione);
				}
			}
		}
	}

	/**
	 * Metodo che mi permette di aggiugere la documentazione inserita all'interno dell'xml di riepilogo, 
	 * per la busta amministrativa
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiInseritiAmministrativa(RiepilogoBustaType documentazione, List<DocumentoAllegatoBean> documentiInseriti) {
		for(int i = 0; i < this.bustaAmministrativa.getDocumentiInseriti().size(); i++) {
			RiepilogoDocumentoAllegatoType documentiBusta = documentazione.addNewDocumentoAllegato();
			documentiBusta.setNomeFile(this.bustaAmministrativa.getDocumentiInseriti().get(i).getNomeFile());
			documentiBusta.setDimensione(this.bustaAmministrativa.getDocumentiInseriti().get(i).getDimensione());
			documentiBusta.setDescrizione(this.bustaAmministrativa.getDocumentiInseriti().get(i).getDescrizione());
			documentiBusta.setSha1(this.bustaAmministrativa.getDocumentiInseriti().get(i).getSha1());
			if(this.bustaAmministrativa.getDocumentiInseriti().get(i).getId() != null
			   && this.bustaAmministrativa.getDocumentiInseriti().get(i).getId() > 0) {
				documentiBusta.setId(this.bustaAmministrativa.getDocumentiInseriti().get(i).getId());
			}
			if(StringUtils.isNotEmpty(this.bustaAmministrativa.getDocumentiInseriti().get(i).getUuid())) {
				documentiBusta.setUuid(this.bustaAmministrativa.getDocumentiInseriti().get(i).getUuid());
			}
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione mancante all'interno dell'xml di riepilogo, 
	 * per la busta tecnica
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiMancantiAmministrativa(RiepilogoBustaType documentazione, List<DocumentoMancanteBean> documentiMancanti) {
		for(int i = 0; i < documentiMancanti.size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			if(documentiMancanti.get(i).getId() != null && documentiMancanti.get(i).getId() > 0) {
				documentiRichiestiMancanti.setId(documentiMancanti.get(i).getId());
			}
			documentiRichiestiMancanti.setDescrizione(documentiMancanti.get(i).getDescrizione());
			documentiRichiestiMancanti.setObbligatorio(documentiMancanti.get(i).isObbligatorio());
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione inserita all'interno dell'xml di riepilogo, 
	 * per la busta economica
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiInseritiTecnica(RiepilogoBustaType documentazione, List<DocumentoAllegatoBean> documentiInseriti) {
		for(int i = 0; i < this.bustaTecnica.getDocumentiInseriti().size(); i++) {
			RiepilogoDocumentoAllegatoType documentiBusta = documentazione.addNewDocumentoAllegato();
			documentiBusta.setNomeFile(this.bustaTecnica.getDocumentiInseriti().get(i).getNomeFile());
			documentiBusta.setDimensione(this.bustaTecnica.getDocumentiInseriti().get(i).getDimensione());
			documentiBusta.setDescrizione(this.bustaTecnica.getDocumentiInseriti().get(i).getDescrizione());
			documentiBusta.setSha1(this.bustaTecnica.getDocumentiInseriti().get(i).getSha1());
			if(this.bustaTecnica.getDocumentiInseriti().get(i).getId() != null
			   && this.bustaTecnica.getDocumentiInseriti().get(i).getId() > 0) {
				documentiBusta.setId(this.bustaTecnica.getDocumentiInseriti().get(i).getId());
			}
			if(StringUtils.isNotEmpty(this.bustaTecnica.getDocumentiInseriti().get(i).getUuid())) {
				documentiBusta.setUuid(this.bustaTecnica.getDocumentiInseriti().get(i).getUuid());
			}
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione mancante all'interno dell'xml di riepilogo, 
	 * per le varie buste
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiMancantiTecnica(RiepilogoBustaType documentazione, List<DocumentoMancanteBean> documentiMancanti) {
		for(int i = 0; i < documentiMancanti.size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			if(documentiMancanti.get(i).getId() != null && documentiMancanti.get(i).getId() > 0) {
				documentiRichiestiMancanti.setId(documentiMancanti.get(i).getId());
			}
			documentiRichiestiMancanti.setDescrizione(documentiMancanti.get(i).getDescrizione());
			documentiRichiestiMancanti.setObbligatorio(documentiMancanti.get(i).isObbligatorio());
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione inserita all'interno dell'xml di riepilogo, 
	 * per le varie buste
	 * 
	 * @param busta 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiInseritiEconomica(RiepilogoBustaType documentazione, List<DocumentoAllegatoBean> documentiInseriti) {
		for(int i = 0; i < this.bustaEconomica.getDocumentiInseriti().size(); i++) {
			RiepilogoDocumentoAllegatoType documentiBusta = documentazione.addNewDocumentoAllegato();
			documentiBusta.setNomeFile(this.bustaEconomica.getDocumentiInseriti().get(i).getNomeFile());
			documentiBusta.setDimensione(this.bustaEconomica.getDocumentiInseriti().get(i).getDimensione());
			documentiBusta.setDescrizione(this.bustaEconomica.getDocumentiInseriti().get(i).getDescrizione());
			documentiBusta.setSha1(this.bustaEconomica.getDocumentiInseriti().get(i).getSha1());
			if(this.bustaEconomica.getDocumentiInseriti().get(i).getId() != null
			   && this.bustaEconomica.getDocumentiInseriti().get(i).getId() > 0) {
				documentiBusta.setId(this.bustaEconomica.getDocumentiInseriti().get(i).getId());
			}
			if(StringUtils.isNotEmpty(this.bustaEconomica.getDocumentiInseriti().get(i).getUuid())) {
				documentiBusta.setUuid(this.bustaEconomica.getDocumentiInseriti().get(i).getUuid());
			}
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione mancante all'interno dell'xml di riepilogo, 
	 * per le varie buste
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiMancantiEconomica(RiepilogoBustaType documentazione, List<DocumentoMancanteBean> documentiMancanti) {
		for(int i = 0; i < documentiMancanti.size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			if(documentiMancanti.get(i).getId() != null && documentiMancanti.get(i).getId() > 0) {
				documentiRichiestiMancanti.setId(documentiMancanti.get(i).getId());
			}
			documentiRichiestiMancanti.setDescrizione(documentiMancanti.get(i).getDescrizione());
			documentiRichiestiMancanti.setObbligatorio(documentiMancanti.get(i).isObbligatorio());
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione inserita all'interno dell'xml di riepilogo, 
	 * per la busta di prequalifica
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiInseritiPrequalifica(RiepilogoBustaType documentazione, List<DocumentoAllegatoBean> documentiInseriti) {
		for(int i = 0; i < this.bustaPrequalifica.getDocumentiInseriti().size(); i++) {
			RiepilogoDocumentoAllegatoType documentiBusta = documentazione.addNewDocumentoAllegato();
			documentiBusta.setNomeFile(this.bustaPrequalifica.getDocumentiInseriti().get(i).getNomeFile());
			documentiBusta.setDimensione(this.bustaPrequalifica.getDocumentiInseriti().get(i).getDimensione());
			documentiBusta.setDescrizione(this.bustaPrequalifica.getDocumentiInseriti().get(i).getDescrizione());
			documentiBusta.setSha1(this.bustaPrequalifica.getDocumentiInseriti().get(i).getSha1());
			if(this.bustaPrequalifica.getDocumentiInseriti().get(i).getId() != null
			   && this.bustaPrequalifica.getDocumentiInseriti().get(i).getId() > 0) {
				documentiBusta.setId(this.bustaPrequalifica.getDocumentiInseriti().get(i).getId());
			}
			if(StringUtils.isNotEmpty(this.bustaPrequalifica.getDocumentiInseriti().get(i).getUuid())) {
				documentiBusta.setUuid(this.bustaPrequalifica.getDocumentiInseriti().get(i).getUuid());
			}
		}
	}

	/**
	 * Metodo che mi permette di aggiugnere la documentazione mancante all'interno dell'xml di riepilogo, 
	 * per la busta di prequalifica
	 * 
	 * @param RiepilogoBustaType oggetto su cui andare a scrivere le entry per i documenti mancanti
	 * @param List<DocumentazioneRichiestaType> lista dei documenti mancanti per una determinata busta 
	 * 
	 */
	private void aggiungiDocumentiMancantiPrequalifica(RiepilogoBustaType documentazione, List<DocumentoMancanteBean> documentiMancanti) {
		for(int i = 0; i < documentiMancanti.size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			if(documentiMancanti.get(i).getId() != null && documentiMancanti.get(i).getId() > 0) {
				documentiRichiestiMancanti.setId(documentiMancanti.get(i).getId());
			}
			documentiRichiestiMancanti.setDescrizione(documentiMancanti.get(i).getDescrizione());
			documentiRichiestiMancanti.setObbligatorio(documentiMancanti.get(i).isObbligatorio());
		}
	}
	
	/**
	 * Metodo che permette di recuperare la lista dei documenti richiesti per una data busta da B.O.
	 * 
	 * @param IBandiManager gestore dei bandi
	 * @param String codice della gara
	 * @param String codice lotto
	 * @param String tipo dell'impresa
	 * @param Boolean partecipa in RTI
	 * @param Int tipo busta
	 * @return lista dei documenti
	 */
	public List<DocumentazioneRichiestaType> retrieveDocumentazionerichiestaDb(
			IBandiManager bandiManager, 
			String codice, 
			String codiceLotto,
			String tipoImpresa, 
			boolean isRti, 
			int busta) throws ApsException 
	{
		return bandiManager.getDocumentiRichiestiBandoGara(
				codice, 
				codiceLotto, 
				tipoImpresa, 
				isRti, 
				busta + "");
	}

	/**
	 * ...
	 */ 
	public List<DocumentazioneRichiestaType> retrieveDocumentazioneRichiestaDBLotto(
			IBandiManager bandiManager, 
			String codice, 
			String codiceLotto,
			String tipoImpresa, 
			boolean isRti, 
			int busta, 
			List<DocumentazioneRichiestaType> documentiComuni) throws ApsException
	{
		List<DocumentazioneRichiestaType> documenti = new ArrayList<DocumentazioneRichiestaType>();
		documenti.addAll(documentiComuni);
		List<DocumentazioneRichiestaType> documentiSpecifici = bandiManager
				.getDocumentiRichiestiBandoGara(
						codice, 
						codiceLotto,
						tipoImpresa, 
						isRti, 
						busta + "");
		documenti.addAll(documentiSpecifici);
		return documenti;
	}

	/**
	 * Metodo che permette di popolare l'xml con tutti i documenti mancanti richiesti per la busta amministrativa
	 * @param RiepilogoBustaType xml a cui aggiungere le infromazioni per la busta amministrativa
	 */
	private void aggiungiTuttiRichiestiAMancantiAmministrativa(RiepilogoBustaType documentazione) {
		for(int i = 0; i < this.bustaAmministrativa.getDocRichiesti().size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			documentiRichiestiMancanti.setDescrizione(this.bustaAmministrativa.getDocRichiesti().get(i).getNome());
			documentiRichiestiMancanti.setId(this.bustaAmministrativa.getDocRichiesti().get(i).getId());
			documentiRichiestiMancanti.setObbligatorio(this.bustaAmministrativa.getDocRichiesti().get(i).isObbligatorio());
		}
	}

	/**
	 * Metodo che permette di popolare l'xml con tutti i documenti mancanti richiesti per la busta tecnica
	 * @param RiepilogoBustaType xml a cui aggiungere le infromazioni per la busta tecnica
	 ***/
	private void aggiungiTuttiRichiestiAMancantiTecnica(RiepilogoBustaType documentazione) {
		for(int i = 0; i < this.bustaTecnica.getDocRichiesti().size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			documentiRichiestiMancanti.setDescrizione(this.bustaTecnica.getDocRichiesti().get(i).getNome());
			documentiRichiestiMancanti.setId(this.bustaTecnica.getDocRichiesti().get(i).getId());
			documentiRichiestiMancanti.setObbligatorio(this.bustaTecnica.getDocRichiesti().get(i).isObbligatorio());
		}
	}

	/**
	 * Metodo che permette di popolare l'xml con tutti i documenti mancanti richiesti per la busta economica
	 * @param RiepilogoBustaType xml a cui aggiungere le infromazioni per la busta economica
	 ***/
	private void aggiungiTuttiRichiestiAMancantiEconomica(RiepilogoBustaType documentazione) {
		for(int i = 0; i < this.bustaEconomica.getDocRichiesti().size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			documentiRichiestiMancanti.setDescrizione(this.bustaEconomica.getDocRichiesti().get(i).getNome());
			documentiRichiestiMancanti.setId(this.bustaEconomica.getDocRichiesti().get(i).getId());
			documentiRichiestiMancanti.setObbligatorio(this.bustaEconomica.getDocRichiesti().get(i).isObbligatorio());
		}

	}

	/**
	 * Metodo che permette di popolare l'xml con tutti i documenti mancanti richiesti per la busta di prequalifica 
	 * @param RiepilogoBustaType xml a cui aggiungere le infromazioni per la busta di prequalifica
	 */
	private void aggiungiTuttiRichiestiAMancantiPrequalifica(RiepilogoBustaType documentazione) {
		for(int i = 0; i < this.bustaPrequalifica.getDocRichiesti().size(); i++) {
			RiepilogoDocumentoRichiestoType documentiRichiestiMancanti = documentazione.addNewDocumentoRichiestoMancante();
			documentiRichiestiMancanti.setDescrizione(this.bustaPrequalifica.getDocRichiesti().get(i).getNome());
			documentiRichiestiMancanti.setId(this.bustaPrequalifica.getDocRichiesti().get(i).getId());
			documentiRichiestiMancanti.setObbligatorio(this.bustaPrequalifica.getDocRichiesti().get(i).isObbligatorio());
		}
	}

	/**
	 * ...
	 */
	public void popolaBusteLotti(RiepilogoBustaType documenti, RiepilogoBustaBean bustaLotto) {
		bustaLotto.popolaBusta(documenti);
	}

	/**
	 * ... 
	 */
	public void riallinaeDocumentiLotti (List<WizardDocumentiBustaHelper> documenti, LinkedHashMap<String, RiepilogoBustaBean> busteLotto) throws Exception {
		Set<String> keySet = busteLotto.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		int i = 0;

		while(keyIterator.hasNext()) {
			RiepilogoBustaBean lotto = busteLotto.get(keyIterator.next());
			if(!documenti.isEmpty()){
				lotto.riallineaDocumenti(documenti.get(i));
				i++;
			}
		}	
	}

	/**
	 * ... 
	 */
	private List<String> retrieveListaLotti(
			IBandiManager bandiManager, 
			String codiceGara, 
			String username) 
		throws ApsException 
	{
		List<String> lista = new ArrayList<String>();
		LottoGaraType[] lotti = bandiManager.getLottiGaraPlicoUnicoPerRichiesteOfferta(username, codiceGara, null); //??? this.progressivoOfferta); ???
		if(this.listaCodiciInterniLotti == null) {
			this.listaCodiciInterniLotti = new LinkedHashMap<String, String>();
		}
		if(lotti != null) {
			for(int i = 0; i < lotti.length; i++) {
				lista.add(lotti[i].getCodiceLotto());
				this.listaCodiciInterniLotti.put(lotti[i].getCodiceLotto(), lotti[i].getCodiceInterno());
			}
		}
		return lista;
	}

	/**
	 * verifica se il documento e' presente nella sezione "inseriti" o "mancanti"
	 * Se non e' stato inserito aggiungilo ai "mancanti"
	 */
	private boolean integraDocumentiInseritiMancanti(
			RiepilogoBustaBean riepilogoBusta,
			DocumentazioneRichiestaType documento) 
	{
		boolean integrazione = false;
		
		long idBoDaIns = documento.getId();
		
		boolean inserito = false;
		for(int k = 0; k < riepilogoBusta.getDocumentiInseriti().size(); k++) {
			if(riepilogoBusta.getDocumentiInseriti().get(k).getId().longValue() == idBoDaIns) {
				inserito = true;
				break;
			}
		}
		if( !inserito ) {
			// vado a vedere se manca nei mancanti
			for(int k = 0; k < riepilogoBusta.getDocumentiMancanti().size(); k++) {
				if(riepilogoBusta.getDocumentiMancanti().get(k).getId().longValue() == idBoDaIns) {
					inserito = true;
					break;
				}
			}
		} 
//		else {
//			// se il documento risulta inserito, rimuovilo dai mancanti...
//			for(int k = 0; k < riepilogoBusta.getDocumentiMancanti().size(); k++) {
//				if(riepilogoBusta.getDocumentiMancanti().get(k).getId().longValue() == idBoDaIns) {
//					riepilogoBusta.getDocumentiMancanti().remove(k);
//					break;
//				}
//			}
//		}
		// se non e' tra inseriti ne mancanti => inserisco in mancanti
		if( !inserito ) {
			DocumentoMancanteBean docIntegratoDaBO = new DocumentoMancanteBean();
			docIntegratoDaBO.setDescrizione(documento.getNome());
			docIntegratoDaBO.setId(documento.getId());
			docIntegratoDaBO.setObbligatorio(documento.isObbligatorio());
			
			riepilogoBusta.getDocumentiMancanti().add(docIntegratoDaBO);
			integrazione = integrazione || true;
		}
		
		return integrazione;
	}

	/**
	 * ... 
	 */
	public boolean integraBustaAmministrativaFromBO(
			String codiceGara, 
			String codiceLotto, 
			IBandiManager bandiManager, 
			WizardDatiImpresaHelper datiImpresa, 
			WizardPartecipazioneHelper partecipazioneHelper) throws ApsException
	{
		List<DocumentazioneRichiestaType> docComuniRichiestiAmministrativa = retrieveDocumentazionerichiestaDb(
				bandiManager, 
				codiceGara, 
				codiceLotto, 
				datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
				partecipazioneHelper.isRti(), 
				PortGareSystemConstants.BUSTA_AMMINISTRATIVA);
		
		// --- ricalcolo gli eventuali documenti mancanti ---
		boolean integrazione = false;
		this.bustaAmministrativa.getDocumentiMancanti().clear();
		for(int j = 0; j < docComuniRichiestiAmministrativa.size(); j++) {
			boolean trovato = false;
			for(int k = 0; k < this.bustaAmministrativa.getDocRichiesti().size(); k++) {
				if(this.bustaAmministrativa.getDocRichiesti().get(k).getId() == docComuniRichiestiAmministrativa.get(j).getId()) {
					trovato = true;
					break;
				}
			}
			
			if( !trovato ) {
				// il documento corrente non e' stato censito nella configurazione attuale per la busta => integrazione
				//this.bustaAmministrativa.getDocumentiMancanti().add(docIntegratoDaBO);
				DocumentazioneRichiestaType docBO = new DocumentazioneRichiestaType(
						docComuniRichiestiAmministrativa.get(j).getNome(), 
						docComuniRichiestiAmministrativa.get(j).isObbligatorio(), 
						null, 
						null, 
						docComuniRichiestiAmministrativa.get(j).getId(), 
						docComuniRichiestiAmministrativa.get(j).getFormato(),
						docComuniRichiestiAmministrativa.get(j).getGenerato(),
						docComuniRichiestiAmministrativa.get(j).getIdstampa());

				if( !this.bustaAmministrativa.getDocRichiesti().contains(docBO) ) {
					this.bustaAmministrativa.getDocRichiesti().add(docBO);
				}
				integrazione = integrazione || true;
			}
			
			// verifica se il documento e' presente negli inseriti o nei mancanti...
			if( this.integraDocumentiInseritiMancanti(this.bustaAmministrativa, docComuniRichiestiAmministrativa.get(j)) ) {
				integrazione = integrazione || true;
			}
		}
		return integrazione;
	}
	
	/**
	 * ...
	 */
	public boolean integraBustaTecnicaFromBO(
			RiepilogoBustaBean bustaTecnicaLotto,
			String codiceGara,
			String lottoCorrente, 
			IBandiManager bandiManager,
			WizardDatiImpresaHelper datiImpresa,
			WizardPartecipazioneHelper partecipazioneHelper) 
		throws ApsException 
	{
		List<DocumentazioneRichiestaType> docComuniRichiestiTecnica = null;
		List<DocumentazioneRichiestaType> docCompletaTecnica = null;
		if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
			docComuniRichiestiTecnica = retrieveDocumentazionerichiestaDb(
					bandiManager, 
					codiceGara, 
					null, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_TECNICA);
			docCompletaTecnica = retrieveDocumentazioneRichiestaDBLotto(
					bandiManager, 
					this.codiceGara, 
					lottoCorrente, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_TECNICA, 
					docComuniRichiestiTecnica);
		} else {
			docCompletaTecnica = retrieveDocumentazionerichiestaDb(
					bandiManager, 
					codiceGara, 
					codiceGara, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_TECNICA);
		}

		// --- ricalcolo gli eventuali documenti mancanti ---
		boolean integrazione = false;
		bustaTecnicaLotto.getDocumentiMancanti().clear();
		if(bustaTecnicaLotto != null) {
			for(int j = 0; j < docCompletaTecnica.size(); j++) {
				boolean trovato = false;
				for(int k = 0; k < bustaTecnicaLotto.getDocRichiesti().size()&& !trovato; k++) {
					if(bustaTecnicaLotto.getDocRichiesti().get(k).getId() ==  docCompletaTecnica.get(j).getId()) {
						trovato = true;
					}
				}

				if( !trovato ) {
					DocumentazioneRichiestaType docBO = new DocumentazioneRichiestaType(
							docCompletaTecnica.get(j).getNome(), 
							docCompletaTecnica.get(j).isObbligatorio(), 
							null, 
							null, 
							docCompletaTecnica.get(j).getId(), 
							docCompletaTecnica.get(j).getFormato(),
							docCompletaTecnica.get(j).getGenerato(),
							docCompletaTecnica.get(j).getIdstampa());
					if(!bustaTecnicaLotto.getDocRichiesti().contains(docBO)) {
						bustaTecnicaLotto.getDocRichiesti().add(docBO);
					}
					integrazione = integrazione || true;
				}
				
				// verifica se il documento e' presente negli inseriti o nei mancanti...
				if( this.integraDocumentiInseritiMancanti(bustaTecnicaLotto, docCompletaTecnica.get(j)) ) {
					integrazione = integrazione || true;
				}
			}
		}
		return integrazione;
	}
	
	/**
	 * ...
	 */
	public boolean integraBustaEconomicaFromBO(
			RiepilogoBustaBean bustaEconomicaLotto,
			String codiceGara, 
			String lottoCorrente,
			IBandiManager bandiManager,
			WizardDatiImpresaHelper datiImpresa,
			WizardPartecipazioneHelper partecipazioneHelper) 
		throws ApsException 
	{
		/* tramite servizio controllo che non siano stati inseriti nuovo 
		 * documenti obbligatori specifici per il lotto via BO 
		 */
		List<DocumentazioneRichiestaType> docComuniRichiestiEconomica = null;
		List<DocumentazioneRichiestaType> docCompletaEconomica = null;

		if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
			docComuniRichiestiEconomica = retrieveDocumentazionerichiestaDb(
					bandiManager, 
					this.getCodiceGara(), 
					null, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_ECONOMICA);
			docCompletaEconomica = retrieveDocumentazioneRichiestaDBLotto(
					bandiManager,
					this.getCodiceGara(), 
					lottoCorrente, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_ECONOMICA, 
					docComuniRichiestiEconomica);
		} else {
			docCompletaEconomica = retrieveDocumentazionerichiestaDb(
					bandiManager, 
					codiceGara,  
					codiceGara, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_ECONOMICA);
		}

		// --- ricalcolo gli eventuali documenti mancanti ---
		boolean integrazione = false;
		bustaEconomicaLotto.getDocumentiMancanti().clear();
		for(int j = 0; j < docCompletaEconomica.size(); j++) {
			boolean trovato = false;
			for(int k = 0 ; k < bustaEconomicaLotto.getDocRichiesti().size()&& !trovato; k++) {
				if(bustaEconomicaLotto.getDocRichiesti().get(k).getId() ==  docCompletaEconomica.get(j).getId()) {
					trovato = true;
				}
			}
			
			if(!trovato) {
				//bustaEconomicaLotto.getDocumentiMancanti().add(docIntegratoDaBO);
				DocumentazioneRichiestaType docBO = new DocumentazioneRichiestaType(
						docCompletaEconomica.get(j).getNome(), 
						docCompletaEconomica.get(j).isObbligatorio(), 
						null, 
						null, 
						docCompletaEconomica.get(j).getId(), 
						docCompletaEconomica.get(j).getFormato(),
						docCompletaEconomica.get(j).getGenerato(),
						docCompletaEconomica.get(j).getIdstampa());
				if(!bustaEconomicaLotto.getDocRichiesti().contains(docBO)) {
					bustaEconomicaLotto.getDocRichiesti().add(docBO);
				}
				integrazione = integrazione || true;
			}
				
			// verifica se il documento e' presente negli inseriti o nei mancanti...
			if( this.integraDocumentiInseritiMancanti(bustaEconomicaLotto, docCompletaEconomica.get(j)) ) {
				integrazione = integrazione || true; 
			}
		}
		return integrazione;
	}
	
	/**
	 * ... 
	 */
	public boolean integraBustaPrequalificaFromBO(
			String codiceGara, 
			String codiceLotto, 
			IBandiManager bandiManager, 
			WizardDatiImpresaHelper datiImpresa, 
			WizardPartecipazioneHelper partecipazioneHelper) throws ApsException 
	{
		List<DocumentazioneRichiestaType> docComuniRichiestiPrequalifica = 
			retrieveDocumentazionerichiestaDb(
					bandiManager, 
					codiceGara, 
					codiceLotto, 
					datiImpresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazioneHelper.isRti(), 
					PortGareSystemConstants.BUSTA_PRE_QUALIFICA);
		
		this.bustaPrequalifica.getDocumentiMancanti().clear();
		boolean integrazione = false; 
		for(int j = 0; j < docComuniRichiestiPrequalifica.size(); j++) {
			boolean trovato = false;
			for(int k = 0; k < bustaPrequalifica.getDocRichiesti().size() && !trovato; k++) {
				if(bustaPrequalifica.getDocRichiesti().get(k).getId() == docComuniRichiestiPrequalifica.get(j).getId()) {
					trovato = true;
				}
			}

			if(!trovato) {
				// il documento corrente non e' stato censito nella configurazione 
				// attuale per la busta => integrazione
				//bustaPrequalifica.getDocumentiMancanti().add(docIntegratoDaBO);
				DocumentazioneRichiestaType docBO = new DocumentazioneRichiestaType(
						docComuniRichiestiPrequalifica.get(j).getNome(), 
						docComuniRichiestiPrequalifica.get(j).isObbligatorio(), 
						null, 
						null,
						docComuniRichiestiPrequalifica.get(j).getId(), 
						docComuniRichiestiPrequalifica.get(j).getFormato(),
						docComuniRichiestiPrequalifica.get(j).getGenerato(),
						docComuniRichiestiPrequalifica.get(j).getIdstampa());
				if(!bustaPrequalifica.getDocRichiesti().contains(docBO)) {
					bustaPrequalifica.getDocRichiesti().add(docBO);
				}
				integrazione = integrazione || true;
			}
			
			// verifica se il documento e' presente negli inseriti o nei mancanti...
			if( this.integraDocumentiInseritiMancanti(this.bustaPrequalifica, docComuniRichiestiPrequalifica.get(j)) ) { 
				integrazione = integrazione || true;
			}
		}
		return integrazione;
	}

	
	/**
	 * ...
	 * @throws Exception 
	 */
	public RiepilogoBustaBean recuperaDocumentazioneLotto(LottoDettaglioGaraType[] lotti, String codiceLotto, int tipoBusta) {
		if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
			return recuperaDocumentazioneTecnicaLotto(lotti, codiceLotto);
		} else {
			return recuperaDocumentazioneEconomicaLotto(lotti, codiceLotto);
		}
	}
	
	/**
	 * recupera la documentazione del lotto della busta tecnica 
	 */
	private RiepilogoBustaBean recuperaDocumentazioneTecnicaLotto(LottoDettaglioGaraType[] lotti, String codiceLotto) {
		this.documentazioneMancanteError = null;
		for(int i = 0; i < lotti.length; i++) {
			if(codiceLotto.equals(lotti[i].getCodiceLotto())) {
				if(lotti[i].getDocumentoBustaTecnica() != null) {
					return new RiepilogoBustaBean(Arrays.asList(lotti[i].getDocumentoBustaTecnica()), lotti[i].getOggetto());
                } else {
                	RiepilogoBustaBean riepilogo = this.busteTecnicheLotti.get(codiceLotto);
                	if (riepilogo != null) { 
                		if(riepilogo.isQuestionarioPresente()) {
	                		// nel caso di questionario, non sono presenti documenti 
	                		// e si ritorna l'elemento del riepilogo stesso inalterato 
	                		// e ricostruito in precedenza previo aggiornamento dell'oggetto del lotto
	                		riepilogo.setOggetto(lotti[i].getOggetto());
	                		return riepilogo;
						} else {
							this.documentazioneMancanteError = "Errors.riepilogoOfferta.lottoBustaTecSenzaDocumenti";
						}
                	}
                }
			}
		}
		return null;
	}
	
	/**
	 * recupera la documentazione del lotto della busta economica
	 */
	private RiepilogoBustaBean recuperaDocumentazioneEconomicaLotto(LottoDettaglioGaraType[] lotti, String codiceLotto) {
		this.documentazioneMancanteError = null;
		for(int i = 0; i < lotti.length; i++) {
			if(codiceLotto.equals(lotti[i].getCodiceLotto())) {
				if(lotti[i].getCostoFisso() != null && lotti[i].getCostoFisso() == 1) {
					// un lotto OEPV a costo fisso non ha offerta economica!!!
					return null;
				} else {
					if(lotti[i].getDocumentoBustaEconomica() != null) {
						return new RiepilogoBustaBean(Arrays.asList(lotti[i].getDocumentoBustaEconomica()),lotti[i].getOggetto());
					} else {
	                	RiepilogoBustaBean riepilogo = this.busteEconomicheLotti.get(codiceLotto);
	                	if (riepilogo != null) {
		                	if (riepilogo.isQuestionarioPresente()) {
		                		// nel caso di questionario, non sono presenti documenti 
		                		// e si ritorna l'elemento del riepilogo stesso inalterato 
		                		// e ricostruito in precedenza previo aggiornamento dell'oggetto del lotto
		                		riepilogo.setOggetto(lotti[i].getOggetto());
		                		return riepilogo;
			                } else{
								this.documentazioneMancanteError = "Errors.riepilogoOfferta.lottoBustaEcoSenzaDocumenti";
							}	
	                	}
					}
				}
			}
		}
		return null;
	}
		
	/**
	 * integra le informazioni dei documenti di riepilogo recuperando 
	 * le informazioni dalla busta amministrativa, tecnica, economica,
	 * ad esempio "Uuid"
	 * (presente dalla versione 3.2.0)   
	 */
	public boolean verificaIntegraDatiDocumenti(BustaDocumenti busta, String codiceLotto) {
		WizardDocumentiBustaHelper documenti = busta.getHelperDocumenti();
		RiepilogoBustaBean riepilogo = this.getRiepilogoBusta(busta.getTipoBusta(), codiceLotto);
		
		int documentiAggiornati = 0;
		if(documenti != null && riepilogo != null) {
			for(int j = 0; j < riepilogo.getDocumentiInseriti().size(); j++) {
				DocumentoAllegatoBean docRiepilogo = riepilogo.getDocumentiInseriti().get(j);
				
				// verifica solo i documenti che non hanno UUID...
				if( StringUtils.isEmpty(docRiepilogo.getUuid()) ) {
				
					if(docRiepilogo.getId() != null && docRiepilogo.getId() > 0) {
						// documenti richiesti...
						for (Attachment attachment : documenti.getRequiredDocs()) {
							Long key = (attachment != null
											? attachment.getId() : -1L);
							String uuid = (StringUtils.isNotEmpty(attachment.getUuid())
									   		? attachment.getUuid() : null);
							if(key == docRiepilogo.getId()) {
								// aggiorna il documento del riepilogo...
								docRiepilogo.setUuid(uuid);
								documentiAggiornati++;
								break;
							}
						}
					} else {
						// documenti ulteriori...
						for (Attachment attachment : documenti.getAdditionalDocs()) {
							String key = (StringUtils.isNotEmpty(attachment.getDesc())
											? attachment.getDesc() : "");
							String uuid = (StringUtils.isNotEmpty(attachment.getUuid())
								   			? attachment.getUuid() : null);
							if(key.equals(docRiepilogo.getDescrizione())) {
								// aggiorna il documento del riepilogo...
								docRiepilogo.setUuid(uuid);
								documentiAggiornati++;
								break;
							}
						}
					}
				}
			}
		}

		// traccia nel log l'integrazione dei documenti (DEBUG)...
		if(documentiAggiornati > 0) {
			this.modified = true;
			ApsSystemUtils.getLogger().warn(
					"verificaIntegraDatiDocumenti() " +
					"integrate informazioni nella busta di riepilogo " +
					"id=" + (documenti.getIdComunicazione() != null ? documenti.getIdComunicazione() : "") + ", " +
					"codice=" + (StringUtils.isNotEmpty(documenti.getCodice()) ? documenti.getCodice() : "") + ", " +
					"tipobusta=" + (StringUtils.isNotEmpty(busta.getDescrizioneBusta()) ? busta.getDescrizioneBusta() : ""));
		}
		
		return this.modified;
	}
	
	/**
	 * ... 
	 */
	public boolean verificaIntegraDatiDocumenti(BustaDocumenti busta) {
		return this.verificaIntegraDatiDocumenti(busta, null);
	}
	
	/**
	 * memorizza la lista dei firmatari utilizzati 
	 * nell'ultimo nell'helper di un'offerta (tecnica / economica)  
	 */
	public void memorizzaUltimiFirmatariInseriti(ComponentiRTIList componentiRTI) {
		if(this.ultimiFirmatariInseriti == null) {
			this.ultimiFirmatariInseriti = new ArrayList<SoggettoFirmatarioImpresaHelper>();
		}
		this.ultimiFirmatariInseriti.clear();
		for(int i = 0; i < componentiRTI.getListaFirmatari().size(); i++) {
			this.ultimiFirmatariInseriti.add( (SoggettoFirmatarioImpresaHelper)componentiRTI.getListaFirmatari().get(i) );
		}
	}

	/**
	 * verifica se esiste un qustionario associato alla busta del lotto 
	 */
	public boolean verificaPresenzaQuestionario(
			int tipoBusta,
			String codiceLotto, 
			List<QuestionarioType> questionari) 
	{
		boolean presente = false;
		if(questionari != null && questionari.size() > 0) {
			for(int i = 0; i < questionari.size(); i++) {
				if(codiceLotto.equalsIgnoreCase(questionari.get(i).getChiave1()) &&
				   Integer.toString(tipoBusta).equals(questionari.get(i).getBusta()) ) 
				{
					presente = true;
					break;
				}
			}
		}
		return presente;
	}

	/**
	 * restituisce il riepilogo relativo al tipo busta 
	 */
	public RiepilogoBustaBean getRiepilogoBusta(int tipoBusta, String codiceLotto) { 
		RiepilogoBustaBean busta = null;
		if(tipoBusta == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
			busta = this.bustaPrequalifica;
		} else if(tipoBusta == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			busta = this.bustaAmministrativa;
		} else if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) {
			if (this.bustaTecnica != null) {
				busta = this.bustaTecnica;
			} else if (this.busteTecnicheLotti != null) {
				busta = this.busteTecnicheLotti.get(codiceLotto);
			}
		} else if(tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
			if (this.bustaEconomica != null) {
				busta = this.bustaEconomica;
			} else if (this.busteEconomicheLotti != null) {
				busta = this.busteEconomicheLotti.get(codiceLotto);
			}
		}
		return busta;
	}
	
	/**
	 * resetta il riepilogo di una data busta o lotto (stato 99)
	 */
	public void reset(BustaDocumenti busta, String codiceLotto) {
		// resetta i documenti della busta...
		busta.getHelperDocumenti().clear();
		
		// aggiorna le info del riepilogo...
		if(busta.getTipoBusta() == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
			primoAccessoPrequalificaEffettuato = false;
		} else if(busta.getTipoBusta() == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			primoAccessoAmministrativaEffettuato = false;
		} else if(busta.getTipoBusta() == PortGareSystemConstants.BUSTA_TECNICA) {
			if (this.bustaTecnica != null) {
				primoAccessoTecnicaEffettuato = false;
			} else if (this.busteTecnicheLotti != null) {
				primoAccessoTecnicheEffettuato.put(codiceLotto, false);
			}
		} else if(busta.getTipoBusta() == PortGareSystemConstants.BUSTA_ECONOMICA) {
			if (this.bustaEconomica != null) {
				primoAccessoEconomicaEffettuato = false;
			} else if (this.busteEconomicheLotti != null) {
				primoAccessoEconomicheEffettuato.put(codiceLotto, false);
			}
		}
		
		// (Questionari - QFORM) aggiorna il questionario
		RiepilogoBustaBean riepilogo = getRiepilogoBusta(busta.getTipoBusta(), codiceLotto);
		if(riepilogo != null) 
			resetQuestionario(riepilogo);
	}

	/**
	 * (Questionari - QFORM)
	 * resetta i dati del questionario relativi ad una busta di riepilogo
	 */
	public void resetQuestionario(RiepilogoBustaBean riepilogo) {
		riepilogo.setQuestionarioPresente(false);
		riepilogo.setQuestionarioCompletato(false);
		riepilogo.setQuestionarioId(0);
		riepilogo.setRiepilogoPdfAuto(false);
	}
	
	/**
	 * (Questionari - QFORM)
	 * inizializza lo stato del questionario di una busta di riepilogo
	 */
	private void aggiornaStatoQuestionarioBusta(RiepilogoBustaBean riepilogo, QuestionarioType questionario) {
		if(riepilogo != null) {
			resetQuestionario(riepilogo);
			riepilogo.setQuestionarioPresente(questionario != null);
			
			if(questionario != null && riepilogo.isQuestionarioPresente()) {
				int tipoBusta = 0;
				if(StringUtils.isNotEmpty(questionario.getBusta())) {
					tipoBusta = Integer.parseInt(questionario.getBusta()); 
				}	
				
				QCQuestionario q = new QCQuestionario(tipoBusta, questionario.getOggetto());
				if(q != null) {
					riepilogo.setQuestionarioCompletato(q.getValidationStatus() && q.getSummaryGenerated());
					riepilogo.setQuestionarioId(questionario.getId());
					riepilogo.setRiepilogoPdfAuto(q.isRiepilogoPdfAuto());
				}
			}
		}
	}


}

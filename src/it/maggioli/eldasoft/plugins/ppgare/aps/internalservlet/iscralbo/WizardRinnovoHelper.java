package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;

public class WizardRinnovoHelper extends WizardIscrizioneHelper {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3424636093455539105L;
	
	private String codice;
	private int tipoElenco;
	private Date dataTermine;
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getTipoElenco() {
		return tipoElenco;
	}

	public void setTipoElenco(int tipoElenco) {
		this.tipoElenco = tipoElenco;
	}
	
	/**
	 * costruttore per WizardRinnovoHelper
	 */
	public WizardRinnovoHelper() {
		super();
		this.actionPrefix = "openPageRinnovo";
	}	

	/**
	 * Crea l'oggetto documento per la generazione della stringa XML per l'inoltro
	 * della richiesta al backoffice. 
	 *
	 * @param attachFileContents 
	 * 				true se si intendono allegare i documenti ed il loro 
	 * 				contenuto, false altrimenti
	 *
	 * @return oggetto documento contenente i dati del rinnovo dell'impresa o
	 * 		   di un suo aggiornamento
	 */
	public XmlObject getXmlDocument(boolean attachFileContents) throws IOException {
	
		GregorianCalendar dataUfficiale = new GregorianCalendar();
		if (this.getDataPresentazione() != null) {
			dataUfficiale.setTime(this.getDataPresentazione());
		}
		
		XmlObject document;
		
		RinnovoIscrizioneImpresaElencoOperatoriDocument doc = RinnovoIscrizioneImpresaElencoOperatoriDocument
			.Factory.newInstance();	
		RinnovoIscrizioneImpresaElencoOperatoriType allegato = doc
			.addNewRinnovoIscrizioneImpresaElencoOperatori();
		
		allegato.setDataPresentazione(dataUfficiale);
		
		// set dei documenti (se esistenti)
		this.getDocumenti().addDocumenti(allegato.addNewDocumenti(), true);
	
		if((this.getComponentiRTI().size() == 0 || !this.isRti()) && 
		   this.isIscrizioneDomandaVisible()) {
			// UNICO FIRMATARIO

			if(StringUtils.isEmpty(this.getFirmatarioSelezionato().getLista())) {
				this.setFirmatarioSelezionato(this.getListaFirmatariMandataria().get(0));
			}

			SoggettoImpresaHelper soggettoFromLista = null;
			FirmatarioType firmatario = allegato.addNewFirmatario();

			if(!this.getImpresa().isLiberoProfessionista()) {
				soggettoFromLista = getFirmatarioMandatariaFromListeSoggetti(
						this.getImpresa(), 
						this.getFirmatarioSelezionato());
				this.fillMandatariaImpresa(firmatario,soggettoFromLista);
			} else {
				// LIBERO PROFESSIONISTA
				this.fillMandatariaLiberoProfessionista(firmatario);
			}
		} else {
			// FIRMATARI IN CASO DI RTI
			int idxMandataria = this.getComponentiRTI().getMandataria(this.getImpresa().getDatiPrincipaliImpresa());
			
			for(int i = 0; i < this.getComponentiRTI().size(); i++) {
				SoggettoImpresaHelper currentFirmatario = this.getComponentiRTI().getFirmatario(this.getComponentiRTI().get(i));
			
				if(i == idxMandataria) { 
					// CASO 1 mandataria
					if(this.getImpresa().isLiberoProfessionista()) {
						// CASO 1.1 : mandataria di tipo libero professionista
						FirmatarioType firmatario = allegato.addNewFirmatario();
						this.fillMandatariaLiberoProfessionista(firmatario);
					} else {
						// CASO 1.2 : mandataria di tipo non libero professionista
						if(this.isIscrizioneDomandaVisible() && currentFirmatario != null) {
							FirmatarioType firmatario = allegato.addNewFirmatario();
							firmatario.setNome(currentFirmatario.getNome());
							firmatario.setCognome(currentFirmatario.getCognome());
							FirmatarioBean infoQualificaFirmatario = null;
							
							boolean found = false;
							for(int j = 0; j < this.getListaFirmatariMandataria().size() && !found; j++) {
								if(this.getListaFirmatariMandataria().get(j).getNominativo()
								   .equalsIgnoreCase(firmatario.getCognome() + " " + firmatario.getNome())) {
									infoQualificaFirmatario = this.getListaFirmatariMandataria().get(j);
									found = true;
								}
							}

							SoggettoImpresaHelper soggettoFromLista = this.getFirmatarioMandatariaFromListeSoggetti(infoQualificaFirmatario);

							this.fillMandatariaImpresa(firmatario, soggettoFromLista);
						}
					}
				} else {
					// CASO 2 : mandanti
					if(currentFirmatario != null) {
						if(StringUtils.isNotEmpty(currentFirmatario.getCodiceFiscale())) {
							FirmatarioType firmatario = allegato.addNewFirmatario();
							//essendo il C.F unc ampo obbligatorio per i firmatari delle mandanti, lo uso
							//come discriminenta per vedere se il firmatario è stato valorizzato o meno 
							//nello step del download del pdf della richiesta iscrizione (salvataggio parziale)
							if("6".equals(this.getComponentiRTI().get(i).getTipoImpresa())) {
								// libero professionista
								firmatario.setQualifica("libero professionista");
							} else {
								// Non libero professionista
								firmatario.setQualifica(currentFirmatario.getSoggettoQualifica());
							}	
							this.fillMandante(firmatario, currentFirmatario, this.getComponentiRTI().get(i));
						}
					}
				}
			}
		}

		document = doc;
		
		document.documentProperties().setEncoding("UTF-8");
		return document;
	}

	/**
	 * ... 
	 */
	private SoggettoImpresaHelper getFirmatarioMandatariaFromListeSoggetti(
			WizardDatiImpresaHelper impresa, 
			FirmatarioBean firmatarioSelezionato)
	{
		SoggettoImpresaHelper soggettoFromLista = null;

		if (CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioSelezionato.getLista())) {
			soggettoFromLista = (SoggettoImpresaHelper)impresa.getLegaliRappresentantiImpresa()
				.get(firmatarioSelezionato.getIndex());
		} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioSelezionato.getLista())) {
			soggettoFromLista = (SoggettoImpresaHelper)impresa.getDirettoriTecniciImpresa()
				.get(firmatarioSelezionato.getIndex());
		} else {
			soggettoFromLista = (SoggettoImpresaHelper)impresa.getAltreCaricheImpresa()
				.get(firmatarioSelezionato.getIndex());
		}

		return soggettoFromLista;
	}

}

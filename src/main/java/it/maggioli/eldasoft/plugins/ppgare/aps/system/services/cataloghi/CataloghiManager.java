package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.AbstractService;
import it.eldasoft.www.sil.WSGareAppalto.ArticoloType;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaCatalogoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneOutType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.ElencoBandiIscrizioneOutType;
import it.eldasoft.www.sil.WSGareAppalto.EsitoOutType;
import it.eldasoft.www.sil.WSGareAppalto.GetArticoloOutType;
import it.eldasoft.www.sil.WSGareAppalto.GetCategorieArticoliOEOutType;
import it.eldasoft.www.sil.WSGareAppalto.GetProdottoOutType;
import it.eldasoft.www.sil.WSGareAppalto.ProdottoType;
import it.eldasoft.www.sil.WSGareAppalto.RisultatoDecimaleOutType;
import it.eldasoft.www.sil.WSGareAppalto.RisultatoNumericoOutType;
import it.eldasoft.www.sil.WSGareAppalto.SearchArticoliOutType;
import it.eldasoft.www.sil.WSGareAppalto.SearchProdottiOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.BandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servizio gestore dei cataloghi.
 *
 * @version 1.8.5
 * @author Stefano.Sabbadin
 */
public class CataloghiManager extends AbstractService implements ICataloghiManager {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6366297928884210592L;

	/**
	 * Riferimento al wrapper per il web service Gare Appalto
	 */
	private WSGareAppaltoWrapper wsGareAppalto;

	/**
	 * Imposta il web service gare
	 *
	 * @param proxyWsGare web service gare.
	 */
	public void setWsGareAppalto(WSGareAppaltoWrapper proxyWsGare) {
		this.wsGareAppalto = proxyWsGare;
	}

	@Override
	public void init() throws Exception {
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato ");
	}

	@Override
	public List<BandoIscrizioneType> searchCataloghi(String username,
													 Integer stato, boolean isAttivo) throws ApsException {

		List<BandoIscrizioneType> bandi = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			ElencoBandiIscrizioneOutType retWS = wsGareAppalto.getProxyWSGare()
				.searchCataloghi(username, stato, stazioneAppaltante, isAttivo);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandiIscrizione() != null) {
					bandi = Arrays.asList(retWS.getElencoBandiIscrizione());
				} else {
					bandi = new ArrayList<BandoIscrizioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la ricerca cataloghi per l'impresa "
								+ username + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura dei cataloghi per l'impresa "
							+ username, t);
		}
		return bandi;
	}

	@Override
	public List<BandoIscrizioneType> getElencoCataloghi(boolean isAttivo) throws ApsException {

		List<BandoIscrizioneType> lista = null;
		try {
			String stazioneAppaltante = BandiManager.getApplicationFilter(
					PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE, null);

			ElencoBandiIscrizioneOutType retWS = wsGareAppalto.getProxyWSGare()
				.getElencoCataloghi(stazioneAppaltante, isAttivo);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoBandiIscrizione() != null) {
					lista = Arrays.asList(retWS.getElencoBandiIscrizione());
				} else {
					lista = new ArrayList<BandoIscrizioneType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura dell'elenco cataloghi: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura dell'elenco cataloghi",
							t);
		}
		return lista;
	}

	@Override
	public DettaglioBandoIscrizioneType getDettaglioCatalogo(
					String codiceCatalogo) throws ApsException {

		DettaglioBandoIscrizioneType dettaglio = null;
		try {
			DettaglioBandoIscrizioneOutType retWS = wsGareAppalto.getProxyWSGare().getDettaglioCatalogo(
							codiceCatalogo);
			if (retWS.getErrore() == null) {
				dettaglio = retWS.getBandoIscrizione();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura del dettaglio catalogo: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura del dettaglio catalogo",
							t);
		}
		return dettaglio;
	}

	@Override
	public SearchResult<ArticoloType> searchArticoli(String codiceCatalogo,
					String codiceCategoria, String codice, String tipo,
					String descrizione, String colore, String username, 
					int indicePrimoRecord, int maxNumRecord) throws ApsException {

		SearchResult<ArticoloType> risultato = new SearchResult<ArticoloType>();
		List<ArticoloType> lista = null;
		try {
			SearchArticoliOutType retWS = wsGareAppalto.getProxyWSGare().searchArticoli(
							codiceCatalogo, username, codiceCategoria, codice, tipo, descrizione,
							colore, indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoArticoli() != null) {
					lista = Arrays.asList(retWS.getElencoArticoli());
				} else {
					lista = new ArrayList<ArticoloType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException("Errore durante la ricerca articoli: "
								+ retWS.getErrore());
			}
			risultato.setDati(lista);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotaliFiltrati());
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la ricerca articoli", t);
		}
		return risultato;
	}

	@Override
	public ArticoloType getArticolo(Long id) throws ApsException {

		ArticoloType dettaglio = null;
		try {
			GetArticoloOutType retWS = wsGareAppalto.getProxyWSGare().getArticolo(id);
			if (retWS.getErrore() == null) {
				dettaglio = retWS.getArticolo();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura del dettaglio articolo: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura del dettaglio articolo",
							t);
		}
		return dettaglio;
	}

	@Override
	public Long getNumProdottiOEInArticolo(long idArticolo, String username)
					throws ApsException {

		Long risultato = null;
		try {
			RisultatoNumericoOutType retWS = wsGareAppalto.getProxyWSGare().getNumProdottiOEInArticolo(
							idArticolo, username);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura del numero di prodotti inseriti in articolo per l'O.E.: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura del numero di prodotti inseriti in articolo per l'O.E.",
							t);
		}
		return risultato;
	}

	@Override
	public Long getNumProdottiAltriOEInArticolo(long idArticolo, String username)
					throws ApsException {

		Long risultato = null;
		try {
			RisultatoNumericoOutType retWS = wsGareAppalto.getProxyWSGare()
							.getNumProdottiAltriOEInArticolo(idArticolo, username);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura del numero di prodotti inseriti in articolo da altri O.E.: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura del numero di prodotti inseriti in articolo da altri O.E.",
							t);
		}
		return risultato;
	}

	@Override
	public Double getPrezzoMiglioreArticolo(long idArticolo)
					throws ApsException {

		Double risultato = null;
		try {
			RisultatoDecimaleOutType retWS = wsGareAppalto.getProxyWSGare().getPrezzoMiglioreArticolo(
							idArticolo);
			if (retWS.getErrore() == null) {
				risultato = retWS.getValore();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura del prezzo migliore articolo: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura del prezzo migliore articolo",
							t);
		}
		return risultato;
	}

	@Override
	public Boolean isImpresaAbilitataCatalogo(String codiceCatalogo,
					String username) throws ApsException {

		Boolean risultato = null;
		try {
			EsitoOutType retWS = wsGareAppalto.getProxyWSGare().isImpresaAbilitataCatalogo(
							codiceCatalogo, username);
			if (retWS.getErrore() == null) {
				risultato = retWS.getEsito();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la verifica se l'impresa " + username
								+ " risulta abilitata al catalogo "
								+ codiceCatalogo + ": " + retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la verifica se l'impresa "
							+ username + " risulta abilitata al catalogo "
							+ codiceCatalogo, t);
		}
		return risultato;
	}

	@Override
	public SearchResult<ProdottoType> searchProdotti(String codiceCatalogo,
					Long idArticolo, String username, String terminiRicerca,
					int indicePrimoRecord, int maxNumRecord) throws ApsException {

		SearchResult<ProdottoType> risultato = new SearchResult<ProdottoType>();
		List<ProdottoType> lista = null;
		try {
			SearchProdottiOutType retWS = wsGareAppalto.getProxyWSGare()
							.searchProdotti(codiceCatalogo, idArticolo, username,
											terminiRicerca, indicePrimoRecord, maxNumRecord);
			if (retWS.getErrore() == null) {
				if (retWS.getElencoProdotti() != null) {
					lista = Arrays.asList(retWS.getElencoProdotti());
				} else {
					lista = new ArrayList<ProdottoType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException("Errore durante la ricerca prodotti: "
								+ retWS.getErrore());
			}
			risultato.setDati(lista);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotaliFiltrati());
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la ricerca prodotti", t);
		}
		return risultato;
	}

	@Override
	public SearchResult<ProdottoType> searchProdotti(String codiceCatalogo,
					Long idArticolo, String username, ProdottiSearchBean parametri) 
					throws ApsException {

		SearchResult<ProdottoType> risultato = new SearchResult<ProdottoType>();
		List<ProdottoType> lista = null;
		try {
			SearchProdottiOutType retWS = wsGareAppalto.getProxyWSGare()
							.advancedSearchProdotti(codiceCatalogo, idArticolo, username,
											parametri.getTipologia(), parametri.getDescrizioneArticolo(),
											parametri.getColore(), parametri.getCodice(), parametri.getNome(),
											parametri.getDescrizioneAggiuntiva(), parametri.getStato(),
											parametri.getStartIndexNumber(), parametri.getiDisplayLength());
			if (retWS.getErrore() == null) {
				if (retWS.getElencoProdotti() != null) {
					lista = Arrays.asList(retWS.getElencoProdotti());
				} else {
					lista = new ArrayList<ProdottoType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException("Errore durante la ricerca prodotti: "
								+ retWS.getErrore());
			}
			risultato.setDati(lista);
			risultato.setNumTotaleRecord(retWS.getNumElementiTotali());
			risultato.setNumTotaleRecordFiltrati(retWS.getNumElementiTotaliFiltrati());
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la ricerca prodotti", t);
		}
		return risultato;
	}

	@Override
	public ProdottoType getProdotto(long id) throws ApsException {

		ProdottoType dettaglio = null;
		try {
			GetProdottoOutType retWS = wsGareAppalto.getProxyWSGare().getProdotto(id);
			if (retWS.getErrore() == null) {
				dettaglio = retWS.getProdotto();
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la lettura del dettaglio prodotto: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la lettura del dettaglio prodotto",
							t);
		}
		return dettaglio;
	}

	@Override
	public List<CategoriaCatalogoType> getCategorieArticoliOE(
					String codiceCatalogo, String username) throws ApsException {

		List<CategoriaCatalogoType> lista = null;
		try {
			GetCategorieArticoliOEOutType retWS = wsGareAppalto.getProxyWSGare().getCategorieArticoliOE(
							codiceCatalogo, username);
			if (retWS.getErrore() == null) {
				if (retWS.getCategorie() != null) {
					lista = Arrays.asList(retWS.getCategorie());
				} else {
					lista = new ArrayList<CategoriaCatalogoType>();
				}
			} else {
				// se si verifica un errore durante l'estrazione dei dati con il
				// servizio, allora si ritorna un'eccezione che contiene il
				// messaggio di errore
				throw new ApsException(
								"Errore durante la ricerca categorie iscritte e articoli per l'O.E.: "
								+ retWS.getErrore());
			}
		} catch (RemoteException t) {
			throw new ApsException(
							"Errore inaspettato durante la ricerca categorie iscritte e articoli per l'O.E.",
							t);
		}
		return lista;
	}
}

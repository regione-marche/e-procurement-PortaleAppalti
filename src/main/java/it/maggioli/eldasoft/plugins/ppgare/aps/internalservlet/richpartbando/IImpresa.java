package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import org.apache.commons.lang.StringUtils;

/**
 * Interfaccia generica per l'impresa
 *
 * @author ...
 */
public interface IImpresa {

	public String getRagioneSociale();
	public void setRagioneSociale(String ragioneSociale);

	public String getTipoImpresa();
	public void setTipoImpresa(String tipoImpresa);

	public String getAmbitoTerritoriale();
	public void setAmbitoTerritoriale(String ambitoTerritoriale);
	
	public String getNazione();
	public void setNazione(String nazione);

	public String getCodiceFiscale();
	public void setCodiceFiscale(String codiceFiscale);

	public String getPartitaIVA();
	public void setPartitaIVA(String partitaIVA);
	
	public String getIdFiscaleEstero();
	public void setIdFiscaleEstero(String idFiscaleEstero);

	/**
	 * restituisce True se l'ambito territoriale di un OE e' italiano 
	 */
	public static boolean isAmbitoTerritorialeItalia(IImpresa impresa) {
		// 1 o NULL indica operatore italiano
		return "1".equals(impresa.getAmbitoTerritoriale()) || StringUtils.isEmpty(impresa.getAmbitoTerritoriale());	
	}	

	/**
	 * restituisce True se l'ambito territoriale di un OE e' estero o extra europeo 
	 */	
	public static boolean isAmbitoTerritorialeUEExtraUE(IImpresa impresa) {
		// 2 indica operatore italiano
		return "2".equals(impresa.getAmbitoTerritoriale());
	}

	/**
	 * calcola una chiave per l'ennupla (codice fiscale, nazionalita) 
	 */
	public static String getChiaveCF(String cf, String naz) {
		return (StringUtils.isNotEmpty(cf) ? cf.toUpperCase() : ""); //+ "|" + (StringUtils.isNotEmpty(naz) ? naz.toUpperCase() : ""); ???
	}
	
	/**
	 * calcola una chiave per l'oggetto IImpresa 
	 */
	public static String getChiaveCF(IImpresa impresa) {
		String key = "";
		// 1 o NULL indica operatore italiano
		if(isAmbitoTerritorialeItalia(impresa)) {
			// operatore italiano
			key = getChiaveCF(impresa.getCodiceFiscale(), impresa.getNazione());
		} else {
			// operatore UE o extra UE
			key = getChiaveCF(impresa.getIdFiscaleEstero(), impresa.getNazione());
		}
		return key;
	}

	/**
	 * verifica se due IImprese sono la stessa in base a CF, PICA o IDFiscale  
	 */
	public static boolean equals(IImpresa a, IImpresa b, boolean... gruppiIvaAbilitati) {
		boolean equals = false; 
		if(a != null && b != null) {
			boolean gruppiIva = (gruppiIvaAbilitati.length > 0 && gruppiIvaAbilitati[0] ? gruppiIvaAbilitati[0] : false);
			
			// 1 o NULL indica operatore italiano
			boolean ait = isAmbitoTerritorialeItalia(a);
			boolean bit = isAmbitoTerritorialeItalia(b);
			
			if(ait && bit) {
				// imprese italiane
				String acf = StringUtils.isNotEmpty(a.getCodiceFiscale()) ? a.getCodiceFiscale() : null;
				String api = StringUtils.isNotEmpty(a.getPartitaIVA()) ? a.getPartitaIVA() : null;
				String bcf = StringUtils.isNotEmpty(b.getCodiceFiscale()) ? b.getCodiceFiscale() : null;
				String bpi = StringUtils.isNotEmpty(b.getPartitaIVA()) ? b.getPartitaIVA() : null;
				
				if(gruppiIva) {
					// con gruppi iva
					// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi
					// 1)
					// se CF A = CF B e PIVA A = PIVA B allora A e B sono la stessa ditta
					if(acf != null && (acf.equalsIgnoreCase(bcf))) {
						if(api != null && bpi != null) {
							if(api.equalsIgnoreCase(bpi)) {
								equals = true;
							}
						} else {
							equals = true;
						}
					}
					
					// 2) se P.IVA non e' vuota
					//    - se PIVA A = PIVA B e CF A = CF B allora A e B sono la stessa ditta
					if(api != null && (api.equalsIgnoreCase(bpi))) {
						if(acf != null && bcf != null) {
							if(acf.equalsIgnoreCase(bcf)) {
								equals = true;
							}
						} else {
							equals = true;
						}
					} 
					
					// 3)
					// se P.IVA e' vuota, se CF A = P.IVA B allora A e B sono la stessa ditta
					if(api == null && acf != null && acf.equalsIgnoreCase(bpi)) {
						equals = true;
					}
					if(bpi == null && bcf != null && bcf.equalsIgnoreCase(api)) {
						equals = true;
					}
				} else {
					// senza gruppi iva
					// se CF A = CF B allora A e B sono la stessa ditta
					if(acf != null && (acf.equalsIgnoreCase(bcf))) {
						equals = true;
					}
					
					// se PIVA A = PIVA B allora A e B sono la stessa ditta
					if(api != null && (api.equalsIgnoreCase(bpi))) {
						equals = true;
					}
				}
				
			} else if(!ait && !bit) {
				// imprese estere
				String aidfis = StringUtils.isNotEmpty(a.getIdFiscaleEstero()) ? a.getIdFiscaleEstero() : null;
				String bidfis = StringUtils.isNotEmpty(b.getIdFiscaleEstero()) ? b.getIdFiscaleEstero() : null;
				
				// se Id Fiscale Estero non e' vuoto, se Id Fiscale Estero = Id Fiscale Estero B allora A e B sono la stessa ditta
				if(gruppiIva) {
					// NB: nel caso di gruppi IVA e' possibile avere due ditte con la stessa PIVA ma con CF diversi	
					equals = (aidfis != null && aidfis.equalsIgnoreCase(bidfis));
				} else {
					equals = (aidfis != null && aidfis.equalsIgnoreCase(bidfis));
				}
			}
		}
		return equals;
	}

}

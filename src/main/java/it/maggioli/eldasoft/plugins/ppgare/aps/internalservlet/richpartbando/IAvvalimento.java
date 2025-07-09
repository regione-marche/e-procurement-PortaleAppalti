package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.util.List;

/**
 * Interfaccia per la gestione dell'avvalimento, nei wizard presenti in sessione dei dati
 * relativi alla presentazione di domande/richieste
 * 
 * @author ...
 */
public interface IAvvalimento {

	boolean isAvvalimento();
	void setAvvalimento(boolean avvalimento);

	List<IImpresaAusiliaria> getImpreseAusiliarie();
	void setImpreseAusiliarie(List<IImpresaAusiliaria> impreseAusiliarie);

}

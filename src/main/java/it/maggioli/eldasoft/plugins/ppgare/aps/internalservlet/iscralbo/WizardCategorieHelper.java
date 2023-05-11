package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati categorie d'interesse
 * 
 * @author Stefano.Sabbadin
 */
public class WizardCategorieHelper implements Serializable {
    /**
	 * UID
	 */
	private static final long serialVersionUID = -1805651614845068148L;

	/**
     * Codici categorie selezionate in precedenti comunicazioni e pertanto
     * bloccate
     */
    private TreeSet<String> categorieBloccate;

    /** Codici categorie selezionate */
    private LinkedHashSet<String> categorieSelezionate;

    /** Hash "classificazione a partire da" per le categorie selezionate */
    private HashMap<String, String> classeDa;

    /** Hash "classificazione fino a" per le categorie selezionate */
    private HashMap<String, String> classeA;

    /** Hash "note" per le categorie selezionate */
    private HashMap<String, String> nota;

    /** Set per individuare le foglie per le categorie selezionate */
    private Set<String> foglie;

    public Set<String> getCategorieBloccate() {
    	return categorieBloccate;
    }

    public LinkedHashSet<String> getCategorieSelezionate() {
    	return categorieSelezionate;
    }

    public void setCategorieSelezionate(LinkedHashSet<String> categorieSelezionate) {
    	this.categorieSelezionate = categorieSelezionate;
    }

    public HashMap<String, String> getClasseDa() {
    	return classeDa;
    }

    public HashMap<String, String> getClasseA() {
    	return classeA;
    }
    
	public HashMap<String, String> getNota() {
		return nota;
	}

	public Set<String> getFoglie() {
		return foglie;
	}

	public void setFoglie(Set<String> foglia) {
		this.foglie = foglia;
	}

	/**
	 * costruttore
	 */
    public WizardCategorieHelper() {
		this.categorieBloccate = new TreeSet<String>();
		this.categorieSelezionate = new LinkedHashSet<String>();
		this.classeDa = new HashMap<String, String>();
		this.classeA = new HashMap<String, String>();
		this.nota = new HashMap<String, String>();
		this.foglie = new  HashSet<String>();
    }

}

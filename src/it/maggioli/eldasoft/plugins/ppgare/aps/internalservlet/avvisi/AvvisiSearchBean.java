package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi;

import it.maggioli.eldasoft.plugins.ppcommon.aps.BaseSearchBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi.BandiSearchBean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Bean per la raccolta dei criteri di filtro nelle form di ricerca avvisi.
 * 
 * @author Stefano.Sabbadin
 * @since 1.8.0
 */
public class AvvisiSearchBean extends BaseSearchBean implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8602794282923863971L;

	private String stazioneAppaltante;
	private String oggetto;
	private String tipoAvviso;
	private String dataPubblicazioneDa;
	private String dataPubblicazioneA;
	private String dataScadenzaDa;
	private String dataScadenzaA;
	private String altriSoggetti;
	private String sommaUrgenza;

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = stazioneAppaltante;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTipoAvviso() {
		return tipoAvviso;
	}

	public void setTipoAvviso(String tipoAvviso) {
		this.tipoAvviso = tipoAvviso;
	}

	public String getDataPubblicazioneDa() {
		return dataPubblicazioneDa;
	}

	public void setDataPubblicazioneDa(String dataTermineDa) {
		if (!"".equals(dataTermineDa))
			this.dataPubblicazioneDa = dataTermineDa;
	}

	public String getDataPubblicazioneA() {
		return dataPubblicazioneA;
	}

	public void setDataPubblicazioneA(String dataTermineA) {
		if (!"".equals(dataTermineA))
			this.dataPubblicazioneA = dataTermineA;
	}

	public String getDataScadenzaDa() {
		return dataScadenzaDa;
	}

	public void setDataScadenzaDa(String dataScadenzaDa) {
		if (!"".equals(dataScadenzaDa))
			this.dataScadenzaDa = dataScadenzaDa;
	}

	public String getDataScadenzaA() {
		return dataScadenzaA;
	}

	public void setDataScadenzaA(String dataScadenzaA) {
		if (!"".equals(dataScadenzaA))
			this.dataScadenzaA = dataScadenzaA;
	}

	public String getAltriSoggetti() {
		return altriSoggetti;
	}

	public void setAltriSoggetti(String altriSoggetti) {
		this.altriSoggetti = altriSoggetti;
	}

	public String getSommaUrgenza() {
		return sommaUrgenza;
	}

	public void setSommaUrgenza(String sommaUrgenza) {
		this.sommaUrgenza = sommaUrgenza;
	}
	
}

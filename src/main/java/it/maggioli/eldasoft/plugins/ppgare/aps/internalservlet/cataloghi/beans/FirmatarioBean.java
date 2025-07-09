package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean per composizione della lista dei possibili soggetti firmatari del
 * ripilogo modifiche prodotti di un catalogo
 *
 * @author Marco Perazzetta
 */
public class FirmatarioBean implements Serializable {

	private static final long serialVersionUID = -8117325934295967836L;

	@Validate(EParamValidation.GENERIC)
	private String nominativo;
	private Integer index;
	@Validate(EParamValidation.GENERIC)
	private String lista;
	@Validate(EParamValidation.GENERIC)
	private String codiceFiscale;

	public String getNominativo() {
		return nominativo;
	}

	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}
	
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	/**
	 * costruttore
	 */
	public FirmatarioBean() {
	}

	/**
	 * costruttore
	 */
	public FirmatarioBean(String firmatario) {
		this.lista = StringUtils.isNotBlank(firmatario) ? StringUtils.trim(StringUtils.substringBefore(firmatario, "-")) : null;
		this.index = StringUtils.isNotBlank(firmatario) ? Integer.parseInt(StringUtils.trim(StringUtils.substringAfter(firmatario, "-"))) : null;
	}

	/**
	 * costruttore
	 */
	public FirmatarioBean(FirmatarioType firmatarioType) {
		this.nominativo = firmatarioType.getCognome() + " " + firmatarioType.getNome();
	}

	/**
	 * restituisce le qualifiche del firmatario 
	 */
	public List<String> getQualifiche(List<ISoggettoImpresa> altreCaricheImpresa) {
		List<String> qualifiche = new ArrayList<String>();
		try {
			if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(this.lista)) {
				qualifiche.add(InterceptorEncodedData
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
							 + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(this.lista)) {
				qualifiche.add(InterceptorEncodedData
						.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
						.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
							 + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
			} else {
				if(altreCaricheImpresa != null) {
					String codice = altreCaricheImpresa.get(this.index).getSoggettoQualifica();
					qualifiche.add(InterceptorEncodedData
							.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
							.get(codice));
				}
			}
		} catch(Throwable t) {
			ApsSystemUtils.logThrowable(t, "FirmatarioBean", "getQualifiche");
		}
		return qualifiche;
	}

}

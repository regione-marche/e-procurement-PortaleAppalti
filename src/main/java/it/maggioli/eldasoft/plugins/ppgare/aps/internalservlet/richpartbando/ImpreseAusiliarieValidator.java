package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.apsadmin.system.BaseAction;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;

/**
 * Helper per la validazione dei dati inseriti nella lista delle imprese ausiliarie
 *
 * @author ...
 */
public class ImpreseAusiliarieValidator extends ComponentiValidator {

	private IAvvalimento avvalimento;
	
	@Override
	protected String errorsMoreDuplicatedRagioneSociale(String[] args) {
		return action.getText("Errors.impreseAusiliarie.moreDuplicatedRagioneSociale", args);
	}
	
	@Override
	protected String errorsDuplicatedRagioneSociale(String[] args) {
		return action.getText("Errors.impreseAusiliarie.duplicatedRagioneSociale", args);
	}

	@Override
	protected String errorsMoreDuplicatedCodiceFiscale(String[] args) {
		return action.getText("Errors.impreseAusiliarie.moreDuplicatedCodiceFiscale", args);
	}

	@Override
	protected String errorsDuplicatedCodiceFiscale(String[] args) {
		return action.getText("Errors.impreseAusiliarie.duplicatedCodiceFiscale", args);
	}

	@Override
	protected String errorsSameCodiceFiscale(String[] args) {
		return action.getText("Errors.impreseAusiliarie.sameCodiceFiscale", args);
	}
	
	@Override
	protected String errorsSameCodiceFiscaleOperatore(String[] args) {
		return action.getText("Errors.impreseAusiliarie.sameCodiceFiscaleOperatore", args);
	}
	
	@Override
	protected String errorsWrongForeignFiscalField(String[] args) {
		return action.getText("Errors.wrongForeignFiscalField", args);
	}
	
	@Override
	protected String errorsSamePartitaIVA(String[] args) {
		return action.getText("Errors.impreseAusiliarie.samePartitaIVA", args);
	}
	
	@Override
	protected String errorsDuplicatedPartitaIva(String[] args) {
		return action.getText("Errors.impreseAusiliarie.duplicatedPartitaIVA", args);
	}

	
	/**
	 * costruttore 
	 */	
	public ImpreseAusiliarieValidator(
			IAvvalimento avvalimento,
			IDatiPrincipaliImpresa impresa,
			boolean isRti,
			BaseAction action) 
	{
		super(null, impresa, action);
		this.avvalimento = avvalimento;
		this.isRti = isRti;
		validaConImpresaLoggata = false;
		imprese = getElencoImpreseAusiliarie();
	}
	
	/**
	 * genera l'elenco delle imprese ausiliarie  
	 */
	private List<IImpresa> getElencoImpreseAusiliarie() {
		List<IImpresa> impreseAusiliarie = new ArrayList<IImpresa>();
		if(avvalimento.getImpreseAusiliarie() != null) {
			avvalimento.getImpreseAusiliarie().stream()
				.forEach(c -> impreseAusiliarie.add(c));
		}
		return impreseAusiliarie;
	}

	/**
	 * validazione del campo ragione sociale
	 */
	@Override
	public boolean validateInputRagioneSociale(IImpresa form, String id) {
		boolean controlliOK = super.validateInputRagioneSociale(form, id);
		
		// PORTAPPALT-1220
		// se la compilante e' impresa semplice, allora NON puo' essere anche ausiliaria, 
		// pero' se e' mandataria di RT allora puo' essere ausiliaria, quindi puo' stare nella lista
		if(controlliOK) {
			if(StringUtils.isNotEmpty(form.getRagioneSociale())) {
				if( !isRti ) {
					// verifica se la ditta loggata e' in avvalimento...
					boolean stessoCf = (datiPrincipaliImpresa.getCodiceFiscale().equalsIgnoreCase(form.getCodiceFiscale()) ||
										datiPrincipaliImpresa.getCodiceFiscale().equalsIgnoreCase(form.getIdFiscaleEstero()));
					boolean stessaPi = (
							StringUtils.isNotBlank(form.getPartitaIVA()) && StringUtils.isNotBlank(datiPrincipaliImpresa.getPartitaIVA())
							&& datiPrincipaliImpresa.getPartitaIVA().equalsIgnoreCase(form.getPartitaIVA())
					);
					if( (!gruppiIVAAbilitati && stessoCf) || (gruppiIVAAbilitati && stessoCf && stessaPi) ) {
						// l'impresa NON puo' far parte delle ausiliarie
						controlliOK = false;
						String cf = (StringUtils.isNotEmpty(form.getIdFiscaleEstero()) ? form.getIdFiscaleEstero() : form.getCodiceFiscale());
						action.addFieldError("ragioneSociale", 
								 action.getText("Errors.impreseAusiliarie.impresaSempliceNonAmmessa", new String[] {form.getRagioneSociale(), cf}));
					}
				}
			}
		}
		
		return controlliOK;
	}
	
	/**
	 * verifica se ci sono imprese ausiliarie che fanno parte dell'RTI
	 * @param impresa (optional) impresa ausiliaria da verificare
	 */
	public IImpresa isImpreseAusiliariePresentiInRti(IImpresaAusiliaria... impresaAusiliaria) {
		IImpresa impresa = null;
		
		// PORTAPPALT-1220: i controlli incrociati tra lista RT e ausiliarie sono disabilitati!!!
//		List<IComponente> componentiRTI = (partecipazione.isRti() ? partecipazione.getComponenti() : null);
//		if(componentiRTI != null) {
//			IImpresaAusiliaria imprAusiliaria = (impresaAusiliaria.length > 0 ? impresaAusiliaria[0] : null);
//			
//			// verifica se esistono imprese ausiliaria presenti anche in RTI...
//			impresa = imprese.stream()
//					.filter(imp -> componentiRTI.stream()
//									.anyMatch(rti -> IImpresa.equals(imp, rti, gruppiIVAAbilitati))
//					)
//					.findFirst()
//					// ...se non ne esistono verifica se l'"impresaAusiliaria" e' gia' presente anche in RTI...
//					.orElse(componentiRTI.stream()
//								.filter(rti -> IImpresa.equals(imprAusiliaria, rti, gruppiIVAAbilitati))
//								.findFirst()
//								.orElse(null)
//					);
//		}

		return impresa;
	}

}

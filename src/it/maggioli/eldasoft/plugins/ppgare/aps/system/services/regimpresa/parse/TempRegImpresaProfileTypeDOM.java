package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.parse;

import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model.TempRegImpresaProfile;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.parse.EntityTypeDOM;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * Classe per la gestione dell'XML per il profilo PTRI.
 * 
 * @author Stefano.Sabbadin
 */
public class TempRegImpresaProfileTypeDOM extends EntityTypeDOM {

	@SuppressWarnings("rawtypes")
	@Override
	protected IApsEntity createEntityType(Element contentElem, Class entityClass)
			throws ApsSystemException {
		TempRegImpresaProfile regImpresaProfile = (TempRegImpresaProfile) super
				.createEntityType(contentElem, entityClass);
		String codFiscaleAttributeName = this.extractXmlAttribute(contentElem,
				"codiceFiscaleAttributeName", false);
		if (null != codFiscaleAttributeName
				&& !codFiscaleAttributeName.equals(NULL_VALUE)) {
			regImpresaProfile
					.setCodiceFiscaleAttributeName(codFiscaleAttributeName);
		}
		String partitaIVAAttributeName = this.extractXmlAttribute(contentElem,
				"partitaIVAAttributeName", false);
		if (null != partitaIVAAttributeName
				&& !partitaIVAAttributeName.equals(NULL_VALUE)) {
			regImpresaProfile
					.setPartitaIVAAttributeName(partitaIVAAttributeName);
		}
		String mailAttributeName = this.extractXmlAttribute(contentElem,
				"mailAttributeName", false);
		if (null != mailAttributeName && !mailAttributeName.equals(NULL_VALUE)) {
			regImpresaProfile.setMailAttributeName(mailAttributeName);
		}
		return regImpresaProfile;
	}

	@Override
	protected Element createRootTypeElement(IApsEntity currentEntityType) {
		Element typeElement = super.createRootTypeElement(currentEntityType);
		TempRegImpresaProfile regImpresaProfile = (TempRegImpresaProfile) currentEntityType;
		this.setXmlAttribute(typeElement, "codiceFiscaleAttributeName",
				regImpresaProfile.getCodiceFiscaleAttributeName());
		this.setXmlAttribute(typeElement, "partitaIVAAttributeName",
				regImpresaProfile.getPartitaIVAAttributeName());
		this.setXmlAttribute(typeElement, "mailAttributeName",
				regImpresaProfile.getMailAttributeName());
		return typeElement;
	}

	private void setXmlAttribute(Element element, String name, String value) {
		String valueToSet = value;
		if (null == value || value.trim().length() == 0) {
			valueToSet = NULL_VALUE;
		}
		element.setAttribute(name, valueToSet);
	}

	@Override
	protected String getEntityTypeRootElementName() {
		return "profiletype";
	}

	@Override
	protected String getEntityTypesRootElementName() {
		return "profiletypes";
	}

	private static final String NULL_VALUE = "**NULL**";

}
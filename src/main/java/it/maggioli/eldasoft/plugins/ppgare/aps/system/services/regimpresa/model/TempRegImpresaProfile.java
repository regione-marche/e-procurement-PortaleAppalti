package it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.model;

import java.util.List;

import com.agiletec.aps.system.common.entity.model.ApsEntity;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AbstractComplexAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;
import com.agiletec.aps.system.common.entity.model.attribute.BooleanAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.DateAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.ITextAttribute;
import com.agiletec.aps.system.common.entity.model.attribute.NumberAttribute;

/**
 * A ITempRegImpresaProfile implementation. It contains a set of attributes
 * specified in the configuration of ProfileManager.
 * 
 * @author Stefano.Sabbadin
 */
public class TempRegImpresaProfile extends ApsEntity implements
		ITempRegImpresaProfile {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 7014092313735943528L;

	@Override
	public String getUsername() {
		return this.getId();
	}

	@Override
	public Object getValue(String key) {
		AttributeInterface attribute = (AttributeInterface) this
				.getAttribute(key);
		if (null != attribute) {
			return this.getValue(attribute);
		}
		return null;
	}

	private Object getValue(AttributeInterface attribute) {
		if (null == attribute)
			return "";
		if (attribute.isTextAttribute()) {
			return ((ITextAttribute) attribute).getText();
		} else if (attribute instanceof NumberAttribute) {
			return ((NumberAttribute) attribute).getValue();
		} else if (attribute instanceof BooleanAttribute) {
			return ((BooleanAttribute) attribute).getValue();
		} else if (attribute instanceof DateAttribute) {
			return ((DateAttribute) attribute).getDate();
		} else if (!attribute.isSimple()) {
			String text = "";
			List<AttributeInterface> attributes = ((AbstractComplexAttribute) attribute)
					.getAttributes();
			for (int i = 0; i < attributes.size(); i++) {
				if (i > 0)
					text += ",";
				AttributeInterface attributeElem = attributes.get(i);
				text += this.getValue(attributeElem);
			}
			return text;
		}
		return null;
	}

	@Override
	public IApsEntity getEntityPrototype() {
		TempRegImpresaProfile prototype = (TempRegImpresaProfile) super
				.getEntityPrototype();
		prototype.setCodiceFiscaleAttributeName(this
				.getCodiceFiscaleAttributeName());
		prototype.setPartitaIVAAttributeName(this.getPartitaIVAAttributeName());
		prototype.setMailAttributeName(this.getMailAttributeName());
		return prototype;
	}

	@Override
	public boolean isPublicProfile() {
		return _publicProfile;
	}

	@Override
	public void setPublicProfile(boolean publicProfile) {
		this._publicProfile = publicProfile;
	}

	public String getCodiceFiscaleAttributeName() {
		return _codiceFiscaleAttributeName;
	}

	public void setCodiceFiscaleAttributeName(String _codiceFiscaleAttributeName) {
		this._codiceFiscaleAttributeName = _codiceFiscaleAttributeName;
	}

	public String getPartitaIVAAttributeName() {
		return _partitaIVAAttributeName;
	}

	public void setPartitaIVAAttributeName(String _partitaIVAAttributeName) {
		this._partitaIVAAttributeName = _partitaIVAAttributeName;
	}

	@Override
	public String getMailAttributeName() {
		return _mailAttributeName;
	}

	public void setMailAttributeName(String mailAttributeName) {
		this._mailAttributeName = mailAttributeName;
	}

	private boolean _publicProfile;

	private String _codiceFiscaleAttributeName;

	private String _partitaIVAAttributeName;

	private String _mailAttributeName;

}

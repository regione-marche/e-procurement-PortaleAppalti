package test.com.agiletec.aps.system.common.entity;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;

import test.com.agiletec.aps.BaseTestCase;

import com.agiletec.aps.system.common.entity.ApsEntityManager;
import com.agiletec.aps.system.common.entity.IEntityManager;
import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface;

public class TestEntityManager extends BaseTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.init();
	}
	
	public void testGetAttributeTypes() throws Throwable {
		if (null == this._entityManager) return;
		Map<String, AttributeInterface> attributes = this._entityManager.getEntityAttributePrototypes();
		assertNotNull(attributes);
		assertNotNull(attributes.get("Text"));
	}
	
	public void testGetEntityTypes() throws Throwable {
		if (null == this._entityManager) return;
		Map<String, AttributeInterface> attributes = this._entityManager.getEntityAttributePrototypes();
		String testTypeCode = "XXX";
		String testAttributeName = "testAttributeName";
		Map<String, IApsEntity> entityTypes = this._entityManager.getEntityPrototypes();
		try {
			assertNotNull(entityTypes);
			assertTrue(entityTypes.size()>0);
			assertNull(entityTypes.get(testTypeCode));
			IApsEntity entityPrototype = new ArrayList<IApsEntity>(entityTypes.values()).get(0);
			int initAttributeNumber = entityPrototype.getAttributeList().size();
			
			entityPrototype.setTypeCode(testTypeCode);
			entityPrototype.setTypeDescr("testDescription");
			assertNull(entityPrototype.getAttribute(testAttributeName));
			AttributeInterface newAttribute = attributes.get("Text");
			newAttribute.setName(testAttributeName);
			newAttribute.setRequired(true);
			entityPrototype.addAttribute(newAttribute);
			
			((ApsEntityManager) this._entityManager).addEntityPrototype(entityPrototype);
			
			entityTypes = this._entityManager.getEntityPrototypes();
			IApsEntity extractedEntityPrototype = entityTypes.get(testTypeCode);
			assertNotNull(extractedEntityPrototype);
			assertEquals(initAttributeNumber+1, extractedEntityPrototype.getAttributeList().size());
		} catch (Throwable t) {
			throw t;
		} finally {
			((ApsEntityManager) this._entityManager).removeEntityPrototype(testTypeCode);
			entityTypes = this._entityManager.getEntityPrototypes();
			assertNull(entityTypes.get(testTypeCode));
		}
	}
	
	private void init() {
		ListableBeanFactory factory = (ListableBeanFactory) this.getApplicationContext();
		String[] defNames = factory.getBeanNamesForType(IEntityManager.class);
		if (null != defNames && defNames.length>0) {
			this._entityManager = (IEntityManager) this.getApplicationContext().getBean(defNames[0]);
		}
	}
	
	private IEntityManager _entityManager;
	
}
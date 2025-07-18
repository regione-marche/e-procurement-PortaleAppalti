package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import it.eldasoft.www.sil.WSGareAppalto.ParametroQuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * ...
 *
 * ...
 */
public class QCQuestionario implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3520740545067307824L;
	
//	tipoImpresa                  Tipologia impresa relativa alla partecipazione (tab.Ag008)
//	rti                          Partecipazione in RT(1) oppure no(0)
//	ambitoTerritoriale           Operatore economico italiano(1) o UE/extra UE(2)
//	iscrittoCCIAA                Operatore iscritto CCIAA (1) o non iscritto(0)
//	classeDimensioneImpresa      Classe di dimensione operatore (tab.G_062)
//	numPartecipanti              N.componenti RT (mandante + mandatarie) (1 se non RT)
//	elencoLottiPartecipazione    Elenco dei lotti per cui l''operatore presenta domanda di partecipazione o offerta'	
	
	private static final String CONTENT 									= "content";
	private static final String REPORT_VARIABLES 							= "reportVariables";
	private static final String SURVEY 										= "survey";
	private static final String SYS_VARIABLES 								= "sysVariables";
	private static final String RESULT										= "result";
	private static final String SERVERFILESUUID								= "serverFilesUuids";
//	private static final String DELETEDFILES									= "deletedFiles";
	
	private static final String SYS_VARIABLES_TIPO_IMPRESA					= "tipoImpresa";
	private static final String SYS_VARIABLES_AMBITO_TERRITORIALE			= "ambitoTerritoriale";
	private static final String SYS_VARIABLES_RAGIONESOCIALE_IMPRESA		= "ragioneSocialeImpresa";
	private static final String SYS_VARIABLES_INDIRIZZO_IMPRESA				= "indirizzoImpresa";
	private static final String SYS_VARIABLES_CLASSE_DIMENSIONE_IMPRESA 	= "classeDimensioneImpresa";
	private static final String SYS_VARIABLES_ELENCO_LOTTI_PARTECIPAZIONE	= "elencoLottiPartecipazione";
	private static final String SYS_VARIABLES_ELENCO_CATEGORIE				= "elencoCategorie";
	private static final String SYS_VARIABLES_RTI 							= "rti";
	private static final String SYS_VARIABLES_NUM_PARTECIPANTI				= "numPartecipanti";
	private static final String SYS_VARIABLES_ID_QFORM						= "idQForm";
	private static final String SYS_VARIABLES_DGUEREQUEST					= "dgueRequest";
	private static final String OFFERTA_MOSTRA_IMPORTO						= "showAmount";
	
	private static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");
		
	/**
	 * info per la funzione di validazione "validateAllegati(...)" 
	 */
	public class AnomalieQuestionarioInfo {
		private String uuid;
		private String filename;
		private String source;
		
		public AnomalieQuestionarioInfo(String uuid, String filename, String source) {
			this.uuid = uuid;
			this.filename = filename;
			this.source = source;
		}

		public String getUuid() { return uuid; }
		public String getFilename() { return filename; }
		public String getSource() { return source; }
	}	
	
	
	private String questionario;			// questionario in formato json
	private int tipoBusta;					// in caso questionario per gara, tipo busta (1=amm, 2=tec, 3=eco, 4=preq)
	private String result;					// codice risultato
	private String message;					// eventuale messaggio di errore 
	private boolean riepilogoPdfAuto;
	private List<AnomalieQuestionarioInfo> validateInfo;
	
	public String getQuestionario() {
		return questionario;
	}

	public void setQuestionario(String questionario) {
		this.questionario = questionario;
	}

	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRiepilogoPdfAuto() {
		return riepilogoPdfAuto;
	}
	
	public List<AnomalieQuestionarioInfo> getValidateInfo() {
		return validateInfo;
	}

	/**
	 * costruttore
	 */
	public QCQuestionario(int tipoBusta, String questionarioJson) {
		this.questionario = questionarioJson;
		this.tipoBusta = tipoBusta;
		this.result = null;
		this.message = null;
	}

	/**
	 * costruttore
	 */
	public QCQuestionario(String questionarioJson) {
		this(-1, questionarioJson);
	}

	/**
	 * restituisce il nodo json relativo al path ("report.validationStatus") 
	 */
	private JSONObject getJsonNode(String path) {
		JSONObject obj = null;
		try {
			JSONObject json = (JSONObject) JSONSerializer.toJSON(this.questionario);
			
			String[] p = path.split("\\.");
			if(p != null && p.length > 0) {
				obj = json;
				for(int i = 0; i < p.length; i++) {
					obj = (JSONObject) obj.get(p[i]);
				}
			}
		} catch(Exception ex) {
			obj = null;
			ApsSystemUtils.getLogger().error("getJsonNode", ex);
		}
		return obj;
	}

	/**
	 * trova un nodo "key=value" all'interno della struttura JSON in un dato nodo "root" 
	 */
	private JSONObject findJsonNode(Object root, String key, String value) {
		if(root == null) {
			return null;
		} 

		JSONObject attrib = null;
		JSONObject n = (JSONObject) root;
		Iterator<?> keys = n.keys();
		while(keys.hasNext()) {
			String k = (String) keys.next();
			String v = null;
			
			Object item = n.get(k);
			if(item instanceof JSONObject) {
				v = n.getString(k);
			} else if(item instanceof JSONArray) {
				JSONArray a = (JSONArray) item;
				for(int i = 0; i < a.size(); i++) {
					attrib = this.findJsonNode(a.get(i), key, value);
					if(attrib != null) {
						break; 
					}
				}
			} else {
				v = (item != null ? item.toString() : null);
			}
			
			// verifica se e' stato trovato il nodo
			if(attrib != null) {
				break; 
			} else if(k.equals(key) && value.equals(v)) {
				attrib = (JSONObject) root;
				break;
			}
		}
		return attrib;
	}
		
//	private String getJsonValue(JSONObject node, String name) {
//		String value = node.getString(name);
//		return (StringUtils.isNotEmpty(value) && value.equalsIgnoreCase("null") ? null : value);
//	}
//
//    private JSONObject addJsonArrayIfNull(JSONObject node, String name) {
//	    if(node.containsKey(name)) {
//	    	try {
//	    		node.getJSONArray(name);
//	    		ApsSystemUtils.getLogger().trace(name + "-> array exists");
//	    	} catch(JSONException ex) {
//	    		ApsSystemUtils.getLogger().debug(name + "-> array not present");
//	    		String e = node.getString(name);
//	    		if(StringUtils.isNotBlank(e)) {
//	    			JSONArray arr = new JSONArray();
//	    			arr.add(e);
//	    			node.put("puntoOrdinante", arr);
//	    		}
//	    	}
//	    }
//	    return node;
//    }

	/**
	 * modifica nel json un attributo della sezione "sysVariables"
	 */
	private void putSysVariable(JSONArray section, String chiave, Object[] value) {
		JSONObject newItem = null;
		for(int i = 0; i < section.size(); i++) {
			JSONObject item = section.getJSONObject(i);
			if(item != null && chiave.equalsIgnoreCase(item.getString("name"))) {
				newItem = item;	
				break;
			}
		}
		
		JSONArray v = new JSONArray();
		if(value != null) {
			for(int i = 0; i < value.length; i++) {
				if(value[i] != null) {
					v.add(value[i]);
				} else {
					// valore vuoto
					if(value[i] instanceof Long || value[i] instanceof Integer) {
						v.add(0);
					} else if(value[i] instanceof String) {
						v.add("");
					}
				}
			}
		}
		
		if(newItem == null) {
			newItem = new JSONObject();
			newItem.put("name", chiave);
			newItem.put("values", v);
			section.add(newItem);
		} else {
			newItem.put("values", v);
		}
	}

//	/**
//	 * modifica nel json un attributo della sezione "reportVariables"
//	 */
//	private void putReportVariable(JSONArray section, String chiave, String value) {
//		JSONObject newItem = null;
//		for(int i = 0; i < section.size(); i++) {
//			JSONObject item = section.getJSONObject(i);
//			if(item != null && chiave.equalsIgnoreCase(item.getString("name"))) {
//				newItem = item;	
//				break;
//			}
//		}
//
//		//...
//	}

	/**
	 * valorizzazione dei parametri in "sysVariables"...
	 */
	private void addSysVariables(
			WizardDatiImpresaHelper impresa,
			boolean isRti,
			List<IComponente> componenti,
			List<String> codiciInterniLotti,
			List<String> codiciCategorie,
			String idQuestionario,
			String dgueRequest) 
	{
		try {
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());
			
			// recupera l'elenco dei parametri validati da BO per la sezione "sysVariables" 
			List<ParametroQuestionarioType> parametri = bandiManager.getParametriQuestionario();
			
			// recupera le "sysVariables" nel json del questionario
			JSONObject json = (JSONObject) JSONSerializer.toJSON(this.questionario);
			
			JSONArray sysVariables = null;
			if(json.get(SYS_VARIABLES) != null) {
				sysVariables = json.getJSONArray(SYS_VARIABLES); 
			} else {
				sysVariables = new JSONArray();
			}
			
			if(parametri != null && sysVariables != null) {
				
		    	String tipoImpresa = impresa.getDatiPrincipaliImpresa().getTipoImpresa();
		    	if(isRti) {
		    		// in caso di rti il "tipo impresa" vale 3 o 10 (imprese o liberi prof.)
		    		tipoImpresa = (impresa.isLiberoProfessionista() ? "10" : "3");
		    	}
			
				// aggiorna le "sysVariables"...
				for(int i = 0; i < parametri.size(); i++) {
//					tipoImpresa                  Tipologia impresa relativa alla partecipazione (tab.Ag008)
//					rti                          Partecipazione in RT(1) oppure no(0)
//					ambitoTerritoriale           Operatore economico italiano(1) o UE/extra UE(2)
//					iscrittoCCIAA                Operatore iscritto CCIAA (1) o non iscritto(0)
//					classeDimensioneImpresa      Classe di dimensione operatore (tab.G_062)
//					numPartecipanti              N.componenti RT (mandante + mandatarie) (1 se non RT)
//					elencoLottiPartecipazione    Elenco dei lotti per cui l'operatore presenta domanda di partecipazione o offerta
//					dgueRequest					 1 se e' presente la dgue request, 0 viceversa
					
					if(SYS_VARIABLES_TIPO_IMPRESA.equalsIgnoreCase(parametri.get(i).getChiave())) {
						this.putSysVariable(sysVariables, SYS_VARIABLES_TIPO_IMPRESA, 
								new String[] {tipoImpresa});
						
					} else if(SYS_VARIABLES_AMBITO_TERRITORIALE.equalsIgnoreCase(parametri.get(i).getChiave())) {
						String ambito = ("italia".equalsIgnoreCase(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale())
										 ? "1" : "2");
						this.putSysVariable(sysVariables, SYS_VARIABLES_AMBITO_TERRITORIALE, 
								new String[] {ambito});
						
					} else if(SYS_VARIABLES_RAGIONESOCIALE_IMPRESA.equalsIgnoreCase(parametri.get(i).getChiave())) {
						this.putSysVariable(sysVariables, SYS_VARIABLES_RAGIONESOCIALE_IMPRESA, 
								new String[] {impresa.getDatiPrincipaliImpresa().getRagioneSociale()});
						
					} else if(SYS_VARIABLES_INDIRIZZO_IMPRESA.equalsIgnoreCase(parametri.get(i).getChiave())) {
						String value = 
							(StringUtils.isNotEmpty(impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()) ? impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale() + " " : "") +
							(StringUtils.isNotEmpty(impresa.getDatiPrincipaliImpresa().getCapSedeLegale()) ? impresa.getDatiPrincipaliImpresa().getCapSedeLegale() + " " : "") +
							(StringUtils.isNotEmpty(impresa.getDatiPrincipaliImpresa().getComuneSedeLegale()) ? impresa.getDatiPrincipaliImpresa().getComuneSedeLegale() + " " : "") +
							(StringUtils.isNotEmpty(impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale()) ? "(" + impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale() + ")" : "");
						this.putSysVariable(sysVariables, SYS_VARIABLES_INDIRIZZO_IMPRESA, 
								new String[] {value});
						
					} else if(SYS_VARIABLES_RTI.equalsIgnoreCase(parametri.get(i).getChiave())) {
						this.putSysVariable(sysVariables, SYS_VARIABLES_RTI, 
								new String[] { (isRti ? "1" : "0") });
						
					} else if(SYS_VARIABLES_CLASSE_DIMENSIONE_IMPRESA.equalsIgnoreCase(parametri.get(i).getChiave())) {
						this.putSysVariable(sysVariables, SYS_VARIABLES_CLASSE_DIMENSIONE_IMPRESA, 
								new String[] {impresa.getDatiUlterioriImpresa().getClasseDimensioneDipendenti()});
						
					} else if(SYS_VARIABLES_NUM_PARTECIPANTI.equalsIgnoreCase(parametri.get(i).getChiave())) {
						int n = 1;
						if(isRti && componenti != null) {
							n = n + componenti.size();
						}
						this.putSysVariable(sysVariables, SYS_VARIABLES_NUM_PARTECIPANTI, 
									new String[] { Integer.toString(n) });
							
					} else if(SYS_VARIABLES_ELENCO_LOTTI_PARTECIPAZIONE.equalsIgnoreCase(parametri.get(i).getChiave())) {
						if(codiciInterniLotti != null) {
							// lotti dell'offerta di gara
							String[] lotti = new String[codiciInterniLotti.size()];
							for(int j = 0; j < codiciInterniLotti.size(); j++) {
								lotti[j] = codiciInterniLotti.get(j);
							}
							this.putSysVariable(sysVariables, SYS_VARIABLES_ELENCO_LOTTI_PARTECIPAZIONE, 
												lotti);
						} else {
							this.putSysVariable(sysVariables, SYS_VARIABLES_ELENCO_LOTTI_PARTECIPAZIONE, 
												null);
						}
						
					} else if(SYS_VARIABLES_ELENCO_CATEGORIE.equalsIgnoreCase(parametri.get(i).getChiave())) {
						if(codiciCategorie != null) {
							// categorie selezionate nell'elenco operatori 
							String[] codCategorie = new String[codiciCategorie.size()];
							for(int j = 0; j < codiciCategorie.size(); j++) {
								codCategorie[j] = codiciCategorie.get(j);
							}
							this.putSysVariable(sysVariables, SYS_VARIABLES_ELENCO_CATEGORIE,
												codCategorie);
						} else {
							this.putSysVariable(sysVariables, SYS_VARIABLES_ELENCO_CATEGORIE,
												null);
						}
					}
				}
				
				// aggiungi l'id del questionario ("idQForm")
				this.putSysVariable(sysVariables, SYS_VARIABLES_ID_QFORM, new String[]{ idQuestionario });
				
				// aggiungi dgueRequest {0|1} "dgueRequest")
				this.putSysVariable(sysVariables, SYS_VARIABLES_DGUEREQUEST, new Integer[]{ ("1".equals(dgueRequest) ? 1 : 0) });
				
				// ricrea il json dall'oggetto...
				json.put(SYS_VARIABLES, sysVariables);
				this.questionario = json.toString();
			}
		} catch(Exception e) {
			ApsSystemUtils.getLogger().error("addSystemVariables", e);
		}
	}

	/**
	 * valorizzazione dei parametri in "sysVariables" per una busta...
	 */
	public void addSysVariablesBusta(
			WizardDatiImpresaHelper impresa,
			WizardPartecipazioneHelper partecipazione,
			String idQuestionario,
			boolean dgueRequest) 
	{
		this.addSysVariables(
				impresa, 
				partecipazione.isRti(),
				partecipazione.getComponenti(),
				partecipazione.getCodiciInterniLotti(), 
				null,
				idQuestionario,
				(dgueRequest ? "1" : "0"));
	}

	/**
	 * valorizzazione dei parametri in "sysVariables" per un elenco...
	 */
	public void addSysVariablesElenco(
			WizardIscrizioneHelper iscrizione,
			String idQuestionario) 
	{
		//CategoriaBandoIscrizioneType[] categorie = new CategoriaBandoIscrizioneType[iscrizione.getCategorie().getCategorieSelezionate().size()];		
		List<String> codiciCategorie = new ArrayList<String>();
		for (String cat : iscrizione.getCategorie().getCategorieSelezionate()) {
			codiciCategorie.add(cat);
			//if (iscrizione.getCategorie().getFoglie().contains(cat)) {
			//	this.categorieSelezionate.add(cat);
			//}
		}
		this.addSysVariables(
				iscrizione.getImpresa(), 
				iscrizione.isRti(),
				iscrizione.getComponentiRTI(),
				null, 
				codiciCategorie,
				idQuestionario,
				null);
	}		
	
	/**
	 * valorizzazione dei parametri in "reportVariables"...
	 * @param integer 
	 */
	public String getJsonReportOfferta(String xmlOfferta, Integer nascondiImportoBasiGara) {
		String json = "";
		try {
			// XML -> JSON	
			xmlOfferta = xmlOfferta.replaceAll(" xsi:nil=\"true\"", "")		// pulisci i nil nell'xml
								   .replaceAll("&", "&amp;");				// escape & -> &amp;
			
			XMLSerializer xmlSerializer = new XMLSerializer();
	        JSON jsonObj = xmlSerializer.read(xmlOfferta);
	        JSONObject offObj = (JSONObject) jsonObj;
	        
	        if(offObj.containsKey("puntoOrdinante")) {
	        	try {
	        		offObj.getJSONArray("puntoOrdinante");
	        		ApsSystemUtils.getLogger().trace("puntoOrdinante-> array");
	        	} catch(JSONException ex) {
	        		ApsSystemUtils.getLogger().debug("puntoOrdinante-> not array");
	        		String el = offObj.getString("puntoOrdinante");
	        		if(StringUtils.isNotBlank(el)) {
	        			JSONArray arr = new JSONArray();
	        			arr.add(el);
	        			offObj.put("puntoOrdinante", arr);
	        		}
	        	}
	        }
	        
	        if(offObj.containsKey("puntoIstruttore")) {
	        	try {
	        		offObj.getJSONArray("puntoIstruttore");
	        		ApsSystemUtils.getLogger().trace("puntoIstruttore-> array");
	        	} catch(JSONException ex) {
	        		ApsSystemUtils.getLogger().debug("puntoIstruttore-> not array");
	        		String el = offObj.getString("puntoIstruttore");
	        		if(StringUtils.isNotBlank(el)) {
	        			JSONArray arr = new JSONArray();
	        			arr.add(el);
	        			offObj.put("puntoIstruttore", arr);
	        		}
	        	}
	        }
	        
	        // aggiungi "showAmount" al documento xml dell'offerta
	        offObj.put(OFFERTA_MOSTRA_IMPORTO, nascondiImportoBasiGara != 1);
	        
	        
	        String txt = offObj.toString();
	        txt = txt.replaceAll(":null", ":\"\"");			// pulisci i null nel json
	        
			// aggiorna la sezione "reportVariables" per la creazione del report PDF 
			JSONObject obj = (JSONObject) JSONSerializer.toJSON(this.questionario);	
			
			JSONObject content = (JSONObject) obj.get(CONTENT);
			if(content == null) {
				content = new JSONObject();
			}
			JSONArray sysVariables = (JSONArray) obj.get(SYS_VARIABLES);
			if(sysVariables == null) {
				sysVariables = new JSONArray();
			}
			JSONObject reportVariables = (JSONObject) obj.get(REPORT_VARIABLES);
			if(reportVariables == null) {
				reportVariables = new JSONObject();
			} 
			JSONObject survey = (JSONObject) obj.get(SURVEY);
			if(survey == null) {
				survey = new JSONObject();
			}
			
			// prepare la struttura json da utilizzare per il pdf
			obj.clear();
			content.put(SYS_VARIABLES, sysVariables);
			content.put(REPORT_VARIABLES, txt);
			content.put(SURVEY, survey);
			obj.put(CONTENT, content);
			
			json = obj.toString();
			
		} catch(Exception ex) {
			ApsSystemUtils.getLogger().error("getJsonReportOfferta", ex);
		}	
		return json;
	}
		
	/**
	 * valorizzazione dei parametri in "reportVariables"...
	 */
	public String getJsonReportIscrizione(String xmlIscrizione) {
		String json = this.getJsonReportOfferta(xmlIscrizione, 1);
		return json;
	}

	/**
	 * ... 
	 */
	@Override
	public String toString() {
		return this.questionario;
	}

	/**
	 * indica se un questionario e' stato completato (json "report.validationStatus") 
	 */
	public boolean getValidationStatus() {
		boolean validationStatus = false;
		JSONObject result = this.getJsonNode(SURVEY + "." + RESULT);
		if(result != null) {
			validationStatus = result.getBoolean("validationStatus");
		}
		return validationStatus;
	}
	
	/**
	 * indica se e' stato generato il pdf del riepologo per il questionario (json "report.summaryGenerated") 
	 */
	public boolean getSummaryGenerated() {
		boolean summaryGenerated = false;
		this.riepilogoPdfAuto = true;
		
		JSONObject survey = this.getJsonNode(SURVEY);
		JSONObject result = survey.getJSONObject(RESULT);
		if(result != null) {
			//String filename = result.getString("summaryFileName");
			String uuid = result.getString("summaryUUID");
			uuid = (StringUtils.isNotEmpty(uuid) && uuid.equalsIgnoreCase("null") ? null : uuid);
			String generato = null;
			
			if(StringUtils.isNotEmpty(uuid)) {
				// pdf riepilogo autogenerato
				generato = result.getString("summaryGenerated");
				generato = (StringUtils.isNotEmpty(generato) && generato.equalsIgnoreCase("null") ? null : generato);
			} else {
				// pdf riepilogo firmato con quesito...
				// trova il "questionCode" relativo al pdf di riepilogo "controlType=printpdf"
				String qc = null;
				JSONObject pdfprint = this.findJsonNode(survey, "controlType", "printpdf");
				if(pdfprint != null) {
					/*
					{
						"valuesPlaceholder":"",
						"hidden":false,
						"sysVariableName":null,
						"visibilityRules":[],
						"questionCode":"8",
						"description":"",
						"cardinalityMax":-1,
						"title":"Pdf riepilogo firmato",
						"cardinalityAllowed":false,
						"decimalPrecision":2,
						"required":false,
						"controlType":"printpdf",
						"cardinalitySysVariableName":null,
						"options":"","value":""
					}
					*/
					this.riepilogoPdfAuto = false;
					qc = pdfprint.getString("questionCode");
				}
				
				// trova il questito associato al "questionCode" relativo al pdf di riepilogo 
				JSONObject question = (StringUtils.isNotEmpty(qc)
									   ? this.findJsonNode(result, "questionCode", qc)
									   : null);
				
				// estrai dal quesito l'attributo "summaryGenerated" corrispondente al "summaryUUID"
				if(question != null) {
					JSONArray values = question.getJSONArray("values");
					if(values != null) {
						for(int i = 0; i < values.size(); i++) {
							/*
							{
								"questionCode":"8",
								"values":[{
									"fileDescription":"",
									"displayValue":null,
									"fileName":"",
									"summaryGenerated":true,
									"valueSequence":0,"value":""
								}],
								"description":"",
								"title":"Pdf riepilogo firmato"
							}
							*/
							JSONObject item = values.getJSONObject(i);
							uuid = item.getString("value");
							uuid = (StringUtils.isNotEmpty(uuid) && uuid.equalsIgnoreCase("null") ? null : uuid);
							generato = item.getString("summaryGenerated");
							generato = (StringUtils.isNotEmpty(generato) && generato.equalsIgnoreCase("null") ? null : generato);
							if(StringUtils.isNotEmpty(uuid)) {
								break;
							}
						}
					}
				}
			}
			
			summaryGenerated = (StringUtils.isNotEmpty(generato) ? Boolean.parseBoolean(generato) : false);
			summaryGenerated = (summaryGenerated && StringUtils.isNotEmpty(uuid));
		}
		return summaryGenerated;
	}
	
	/**
	 * aggiunge al json la sezione "serverFilesUuids" contentente l'elenco dei file presenti nella comunicazione 
	 */
	public void addServerFilesUuids(DocumentiAllegatiHelper documenti) {
		// recupera "serverFiles" nel json del questionario
		JSONObject json = (JSONObject) JSONSerializer.toJSON(this.questionario);
		
		JSONArray serverFiles = null;
		if (json.get(SERVERFILESUUID) != null)
			serverFiles = json.getJSONArray(SERVERFILESUUID);
		else
			serverFiles = new JSONArray();

		serverFiles.clear();
		if(documenti != null) {
			addUuidToServerFiles(documenti.getRequiredDocs(), serverFiles);
			addUuidToServerFiles(documenti.getAdditionalDocs(), serverFiles);
		}
		json.put(SERVERFILESUUID, serverFiles);
		
		this.questionario = json.toString();
	}

	private void addUuidToServerFiles(List<Attachment> attachments, JSONArray serverFiles) {
		if (CollectionUtils.isNotEmpty(attachments))
			attachments.stream().map(Attachment::getUuid).forEach(serverFiles::add);
	}

	/**
	 * decodifica il parametro "form" inviato da angular 
	 * @throws UnsupportedEncodingException 
	 */
	public static String decodeFormParameter(String value) {
		try {
			return new String(Base64.decodeBase64(value), "UTF-8");
		} catch(Exception ex) {
			return null;
		}
	}
	
	/**
	 * general method for json tree navigation
	 */
	@SuppressWarnings("unchecked")
	private void browseJsonTree(JSONObject node, String path, Function<JSONObject, Object> leafFun) {
		if(node != null) {
			try {
				int i = (path != null ? path.indexOf(".") : -1);
				String first = (i >= 0 ? path.substring(0, i) : path); 
				if(first == null) {
					// add new leaf...
					leafFun.apply(node);
				} else {
					// navigate tree...
					String last = (i >= 0 ? path.substring(i + 1) : null);
					JSONArray nodes = node.getJSONArray(first);
					if(nodes != null)
						nodes.stream()
							.forEach(n -> browseJsonTree((JSONObject)n, last, leafFun));
				}
			} catch (Exception ex) {
				ApsSystemUtils.getLogger().error("validateNode", ex);
			}
		}
	}
	
	/**
	 * recupera la lista degli allegati presenti nel documento json per la sezione 
	 * "survey.result.structures.sections.groups.questions.values"
	 */
	private HashMap<String, String> getAllegatiJson() {
		HashMap<String, String> allegati = new HashMap<String, String>();
		try {
			// recupera la sezione json "survey.result"
			JSONObject survey = getJsonNode(SURVEY);
			JSONObject result = (survey != null ? survey.getJSONObject(RESULT) : null);
			//			"values":[{
			//				"fileDescription":"Relazione Tecnica Lotto 2",
			//				"displayValue":"BUSTA B.zip.p7m",
			//				"fileName":"BUSTA B.zip.p7m",
			//				"summaryGenerated":"",
			//				"valueSequence":0,
			//				"value":"000000000000002406121656271798"
			//			}],
			Function<JSONObject, Object> listaAllegatiAdd = leaf -> {
				if(StringUtils.isNotEmpty(leaf.getString("value"))
				   && StringUtils.isNotEmpty(leaf.getString("fileName"))
				  ) {
					allegati.put(leaf.getString("value"), leaf.getString("fileName"));  // <uuid, filename>  
				}
				return leaf;
			};

			browseJsonTree(result, "structures.sections.groups.questions.values", listaAllegatiAdd);
			
		} catch (Exception ex) {
			ApsSystemUtils.getLogger().error("getAllegatiJson", ex);
		}
		return allegati;
	}

	/**
	 * verifica e valida la sincronizzazione dei dati tra il questionario.json e l'helper dei documenti  
	 * @throws UnsupportedEncodingException 
	 */
	public boolean validateAllegati(DocumentiAllegatiHelper documenti) {
		boolean continua = true;
		validateInfo = null;
		try {
			HashMap<String, String> allegatiJson = getAllegatiJson();
			
			// in caso ci siano solo gli upload del json i documenti disallineati sono sutti quelli nel json!!!
			if(documenti != null) {
				List<AnomalieQuestionarioInfo> anomalie = new ArrayList<AnomalieQuestionarioInfo>();
				
				// verifica se gli allegati json e i documenti allegati sono sincronizzati
				// conta gli allegati json che NON sono presenti nell'helper dei documenti...
				allegatiJson.entrySet().stream()
						.filter(j -> documenti.getAdditionalDocs().stream()
											.noneMatch(d -> d.getUuid().equals(j.getKey()))
									 && documenti.getRequiredDocs().stream()
							 				.noneMatch(d -> d.getUuid().equals(j.getKey())) )
						.forEach(j -> anomalie.add( new AnomalieQuestionarioInfo(j.getKey(), j.getValue(), "json") ));
				
				// conta i documenti ulteriori dell'helper che NON sono presenti tra gli allegati json...
				documenti.getAdditionalDocs().stream()
						.filter(d -> !(d.getFileName().equalsIgnoreCase(DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME)
									   || d.getFileName().equalsIgnoreCase(DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME)) )
						.filter(d -> !d.getDesc().equalsIgnoreCase(DocumentiAllegatiHelper.QUESTIONARIO_PDFRIEPILOGO))
						.filter(d -> allegatiJson.entrySet().stream()
										.noneMatch(j -> d.getUuid().equalsIgnoreCase(j.getKey())) )
						.forEach(d -> anomalie.add( new AnomalieQuestionarioInfo(d.getUuid(), d.getFileName(), "helper U") ));
				
				// conta i documenti richiesti dell'helper che NON sono presenti tra gli allegati json...
				documenti.getRequiredDocs().stream()
						.filter(d -> !(d.getFileName().equalsIgnoreCase(DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME)
									   || d.getFileName().equalsIgnoreCase(DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME)) )
						.filter(d -> !d.getDesc().equalsIgnoreCase(DocumentiAllegatiHelper.QUESTIONARIO_PDFRIEPILOGO))
						.filter(d -> allegatiJson.entrySet().stream()
										.noneMatch(j -> j.getKey().equalsIgnoreCase(d.getUuid())) )
						.forEach(d -> anomalie.add( new AnomalieQuestionarioInfo(d.getUuid(), d.getFileName(), "helper R") ));
				
				validateInfo = (anomalie.size() > 0 ? anomalie : null);
			}
			
			// verifica se ci sono documenti mancanti e quindi se json ed helper NON sono sincronizzati...
			continua = (validateInfo == null);
			
//			if(validateInfo != null && validateInfo.size() > 0) {
//				for(ValidateInfo doc : validateInfo) {
//					writelog("Allegato mancante : " + d.source + ", " + d.uuid + ", " + d.filename);
//				}
//			}
		} catch (Exception ex) {
			ApsSystemUtils.getLogger().error("validateAllegati", ex);
		}
		return continua;
	}
	
	
//	//******************************************************************************************************************************
//	// SOLO TEST/DEBUG	
//	private static void writelog(String txt) {
//		try {
//			File f = new File("c:\\temp\\verifica_gare_OE.txt");
//			FileWriter fr = new FileWriter(f, true);
//			BufferedWriter br = new BufferedWriter(fr);
//			br.write(txt + "\n");
//			br.close();
//			fr.close();
//		} catch (Exception ex) {
//			System.out.println("An error occurred. " + ex.getMessage());
//		}
//		ApsSystemUtils.getLogger().info(txt);
//		System.out.println(txt);
//	}
//
//	private static Date toDate(String value) {
//		try {
//			return DDMMYYYY.parse(value);
//		} catch (Exception e) {
//			return null;
//		}
//	}
//	
//	private static void verificaBusta(BustaDocumenti busta, String lotto, List<String> stati) throws Throwable {
//		try {
//			if(busta.getComunicazioneFlusso().getId() <= 0) {
//				if(StringUtils.isNotEmpty(lotto))
//					busta.get(stati, lotto);
//				else 
//					busta.get(stati);
//			}
//			
//			if(busta.getComunicazioneFlusso().getId() > 0) {
//				writelog("verifica " + busta.getDescrizioneBusta() + " #" + busta.getProgressivoOfferta() 
//						 + " " + (lotto != null ? lotto : ""));
//				
//				DocumentiAllegatiHelper documentiHelper = busta.getHelperDocumenti();
//				QCQuestionario questionarioJson = documentiHelper.getQuestionarioGare(busta.getTipoBusta());
//				if( !questionarioJson.validateAllegati(documentiHelper) ) {
//					writelog("\tERR: questionario e documenti non allineati");
//				} else {
//					writelog("\tOK: allegati allineati");
//				}
//			}
//		} catch (Exception ex) {
//			writelog("ERR: " + ex.getMessage());
//		}
//	}
//	
//	private static void verificaOfferta(GestioneBuste buste, List<String> stati) throws Throwable {
//		writelog("-----------------------------------------------------------------------");
//		writelog("Verifica username " + buste.getUsername() + " gara " + buste.getCodiceGara() + " #" + buste.getProgressivoOfferta());
//		
//		verificaBusta(buste.getBustaAmministrativa(), null, stati);
//		
//		List<String> lotti = buste.getBustaPartecipazione().getLottiAttivi();
//		if(lotti != null && lotti.size() > 0) {
//			for(String lotto : lotti) {
//				verificaBusta(buste.getBustaTecnica(), lotto, stati);
//				verificaBusta(buste.getBustaEconomica(), lotto, stati);
//			}
//		} else {
//			verificaBusta(buste.getBustaTecnica(), null, stati);
//			verificaBusta(buste.getBustaEconomica(), null, stati);
//		}	
//		
//		writelog("");
//	}
//	
//	public static void verificaGareOE(String username, String codiceGara, String dal, String al) {
//		writelog("INIZIO VERIFICA GARE OE ");
//		writelog("FILTRI");
//		writelog("username:    " + username);
//		writelog("codice gara: " + codiceGara);
//		writelog("dal:         " + dal);
//		writelog("al:          " + al);
//		try {
//			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
//			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
//			
//			List<String> stati = new ArrayList<String>();
//			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
//			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
//			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
//			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
//			//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
//			//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_SCARTATA);
//			//stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
//			
//			Date from = toDate(dal);
//			Date to = toDate(al);
//			
//			// trova le offerte corrispondenti ai filtri
//			DettaglioComunicazioneType criteri = new DettaglioComunicazioneType();
//			criteri.setApplicativo("PA");
//			criteri.setTipoComunicazione("FS11");
//			if(StringUtils.isNotEmpty(username)) criteri.setChiave1(username);
//			if(StringUtils.isNotEmpty(codiceGara)) criteri.setChiave2(codiceGara);
//			//if(StringUtils.isNotEmpty(daData)) criteri.setDataInserimento(null);
//			//if(StringUtils.isNotEmpty(aData)) criteri.setDataInserimento(null);
//			
//			List<DettaglioComunicazioneType> offerte = comunicazioniManager.getElencoComunicazioni(criteri);
//			
//			offerte = offerte.stream()
//				.filter(o -> stati.contains(o.getStato()))
//				.filter(o -> o.getDataInserimento() != null 
//							 && (from == null || (from != null && from.compareTo(o.getDataInserimento().getTime()) <= 0))
//							 && (to == null || (to != null && o.getDataInserimento().getTime().compareTo(to) <= 0)) )
//				.collect(Collectors.toList());
//			
//			for(DettaglioComunicazioneType offerta : offerte) {
//				try {
//					GestioneBuste gestioneBuste = new GestioneBuste(
//							offerta.getChiave1(),
//							offerta.getChiave2(), 
//							offerta.getChiave3(),
//							PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
//					
//					gestioneBuste.get(stati);
//					
//					verificaOfferta(gestioneBuste, stati);
//				} catch (Exception ex) {
//					writelog("ERR: " + ex.getMessage());
//				}
//			}
//			
//		} catch (Throwable ex) {
//			writelog("ERR: " + ex.getMessage());
//		}
//		writelog("FINE VERIFICA GARE OE");
//	}
//		
//	public static void verificaGareSha1(String username, String codiceGara, String dal, String al) {
//		writelog("INIZIO SHA1 ");
//		writelog("FILTRI");
//		writelog("username:    " + username);
//		writelog("codice gara: " + codiceGara);
//		writelog("dal:         " + dal);
//		writelog("al:          " + al);
//		try {
//			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
//			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ctx.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER);
//			
//			// trova tutti i documenti in W_DOCDIG
//		    ComunicazioneType comunicazione = comunicazioniManager.getComunicazione("ALL", -1);
//		    
//		    writelog("documeni trovati: " + comunicazione.getAllegato().length);
//		    
//		    // calcola gli sha1 di tutti i file in W_DOCDIG
//			for(AllegatoComunicazioneType allegato : comunicazione.getAllegato()) {
//				try {
//					ComunicazioneType info = comunicazioniManager.getComunicazione("ALL", -1, allegato.getId().toString());
//					
//					byte[] contenuto = (info != null && info.getAllegato() != null ? info.getAllegato()[0].getFile() : null);
//					
//					String sha1 = null;
//					try {
//						sha1 = (contenuto != null ? DigestUtils.shaHex(contenuto) : null);
//					} catch (IOException e) {
//						//throw e;
//					}
//					
//					writelog("iddocdig=" + allegato.getId() + " uuid=" + allegato.getUuid() + " filename=" + allegato.getNomeFile() + ", SHA1=" + sha1);					
//
//				} catch (Exception ex) {
//					writelog("ERR: " + ex.getMessage());
//				}
//			}
//			
//		} catch (Throwable ex) {
//			writelog("ERR: " + ex.getMessage());
//		}
//		writelog("FINE SHA1 ");
//	}
	
}

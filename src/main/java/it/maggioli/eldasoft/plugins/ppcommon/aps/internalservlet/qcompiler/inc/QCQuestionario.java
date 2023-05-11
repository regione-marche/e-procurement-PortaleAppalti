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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		
	private String questionario;			// questionario in formato json
	private int tipoBusta;					// in caso questionario per gara, tipo busta (1=amm, 2=tec, 3=eco, 4=preq)
	private String result;					// codice risultato
	private String message;					// eventuale messaggio di errore 
	
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

	/**
	 * costruttore
	 */
	public QCQuestionario(int tipoBusta, String json) {
		this.questionario = json;
		this.tipoBusta = tipoBusta;
		this.result = null;
		this.message = null;
	}

	/**
	 * costruttore
	 */
	public QCQuestionario(String json) {
		this.questionario = json;
		this.tipoBusta = -1;
		this.result = null;
		this.message = null;
	}

	/**
	 * restituisce l'oggetto del json relativo al path ("report.validationStatus") 
	 */
	private JSONObject getJsonObject(String path) {
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
			ApsSystemUtils.getLogger().error("getJsonObject", ex);
		}
		return obj;
	}

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
			xmlOfferta = xmlOfferta.replaceAll(" xsi:nil=\"true\"", "");	// pulisci i nil nell'xml
			
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
	 * ... 
	 */
	private JSONObject findJsonNode(Object node, String key, String value) {
		if(node == null) {
			return null;
		} 

		JSONObject attrib = null;
		JSONObject n = (JSONObject) node;
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
				attrib = (JSONObject) node;
				break;
			}
		}
		return attrib;
	}

	/**
	 * indica se un questionario e' stato completato (json "report.validationStatus") 
	 */
	public boolean getValidationStatus() {
		boolean validationStatus = false;
		JSONObject result = this.getJsonObject(SURVEY + "." + RESULT);
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
		
		JSONObject result = this.getJsonObject(SURVEY + "." + RESULT);
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
				// trova il questionCode relativo al pdf di riepilogo (controlType=printpdf => questionCode)
				JSONObject question = null;
				JSONObject survey = this.getJsonObject(SURVEY);
				if(survey != null) {
					JSONObject pdfprint = this.findJsonNode(survey, "controlType", "printpdf");
					if(pdfprint != null) {
						String qc = pdfprint.getString("questionCode");
						if(StringUtils.isNotEmpty(qc)) {
							question = this.findJsonNode(result, "questionCode", qc);
						}
					}
				}
				if(question != null) {
					JSONArray values = question.getJSONArray("values");
					if(values != null) {
						for(int i = 0; i < values.size(); i++) {
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

}

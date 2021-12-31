<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<%-- Parametri da passare a questa jsp

		criteriValutazione				<= QUESTO E' DA PASSARE ESTERNAMENTE			
		criteriValutazioneEditabile		<= QUESTO E' DA PASSARE ESTERNAMENTE
		criteriValutazioneValore		<= QUESTO E' DA PASSARE ESTERNAMENTE
		tipoBusta						
		
	Esempio di chiamata alla jsp			
	
		<c:set var="criteriValutazione" scope="request" value="${...}"/>
		<c:set var="criteriValutazioneValue" scope="request" value="${...}"/>
		<c:set var="criteriValutazioneEditabile" scope="request" value="${...}"/>
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/criteriValutazioneOEPV.jsp">
			<jsp:param name="tipoBusta" value="${BUSTA_ECONOMICA}" />
		</jsp:include> 		
--%>

<%-- 
	*** HINT *** 
	se il tag <s:text name=...> usato per formattare valori numerici genera 
	una pagina vuota, significa che la tag library genera un'eccezione.
	Per risolvere il problema e' sufficiente moltiplicare per 1 il valore 
	passato al tag <s:param value="..."/>         
	Es: 
		<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
 --%>
 
<c:set var="tipoBusta" value="0" />
<c:choose>
	<c:when test="${param.tipoBusta == BUSTA_TECNICA}" ><c:set var="tipoBusta" value="1" /></c:when>
	<c:when test="${param.tipoBusta == BUSTA_ECONOMICA}" ><c:set var="tipoBusta" value="2" /></c:when>	
</c:choose>

<s:set var="criteriValutazione" value="#request.criteriValutazione"/>
<s:set var="criteriValutazioneValore" value="#request.criteriValutazioneValore"/>
 
 
<s:set var="rowIndex" value="-1"/> 
<c:forEach var="row" items="${criteriValutazione}" varStatus="stat">

	<s:set var="editabile">${criteriValutazioneEditabile[stat.index]}</s:set>
	<c:set var="valore" value="${criteriValutazioneValore[stat.index]}"/>   <%-- value="${row['valore']}"/> --%>	
	<s:set var="rowIndex" value="%{#rowIndex + 1}"/>	
	<s:set var="sValore" value="#criteriValutazioneValore[#rowIndex]"/>
	<s:set var="sMin">${row['valoreMin']}</s:set>
	<s:set var="sMax">${row['valoreMax']}</s:set>
	

	<%-- CREA GLI INPUT NON EDITABILI E NON VISIBILE     --%>
	<%-- NB: evita di complicare la scrittura del codice --%>
	<c:if test="${row['tipo'] != tipoBusta}">
		<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}"/>
	</c:if>
	
	<%-- CREA GLI INPUT EDITABILI --%>
	<c:if test="${row['tipo'] == tipoBusta}">
	
		<%-- 
		formattazione dei numeri in virgola, dove "X[.Y]" indica  
		  X	 num cifre parte intera
		  Y	 num decimali dopo la virgola (opzionale)
		--%>
		<c:set var="formattazione" value="15" />
		<c:set var="maxDecimali" value="0" />
		<c:set var="maxDopoVirgola" value="0" />
		<c:if test="${not empty row['numeroDecimali']}" >
			<c:set var="formattazione" value="${formattazione}.${row['numeroDecimali']}" />
			<c:set var="maxDecimali" value="${fn:split(formattazione,'.')[0]}" /> 
			<c:set var="maxDopoVirgola" value="${fn:split(formattazione,'.')[1]}" />
		</c:if>

		<c:set var="limitiMinMax" value="" />
		<c:if test="${not empty row['valoreMin'] && empty row['valoreMax']}"><c:set var="limitiMinMax">, <wp:i18n key="LABEL_DA"/> <s:text name="format.money5dec"><s:param value="%{#sMin * 1}"/></s:text></c:set></c:if>
		<c:if test="${empty row['valoreMin'] && not empty row['valoreMax']}"><c:set var="limitiMinMax">, <wp:i18n key="LABEL_FINO_A"/> <s:text name="format.money5dec"><s:param value="%{#sMax * 1}"/></s:text></c:set></c:if>
		<c:if test="${not empty row['valoreMin'] && not empty row['valoreMax']}"><c:set var="limitiMinMax">, <wp:i18n key="LABEL_DA"/> <s:text name="format.money5dec"><s:param value="%{#sMin * 1}"/></s:text> <wp:i18n key="LABEL_FINO_A"/> <s:text name="format.money5dec"><s:param value="%{#sMax * 1}"/></s:text></c:set></c:if>
		 
		<div class="fieldset-row <c:if test='${stat.index == 0}'>first-row</c:if>">
			<div class="label">
				<c:set var="forLabel"><s:if test="%{#editabile}">for="criterioValutazione_${stat.index}"</s:if></c:set>	
				<label ${forLabel}>${row['descrizione']} :
					<s:if test="%{true}"><span class="required-field">*</span></s:if>
				</label>
			</div>
			
			<div class="element">
				<c:choose>
					<c:when test="${row['formato'] == 1}">
						<%-- DATA --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" placeholder="GG/MM/AAAA" data-parsley-pattern="^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$" value="${valore}" aria-required="true"/>
						</s:if>
						<s:else>
							<s:date name="%{#sValore}" format="dd/MM/yyyy" />
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}"/>
						</s:else>
						&nbsp;&nbsp;
						<s:if test="%{#editabile}">(<wp:i18n key="LABEL_FORMATO_DATA" />) </s:if>
					</c:when>
					
					<c:when test="${row['formato'] == 2}">
						<%-- IMPORTO --%>
						<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="${valore}" aria-required="true"/>
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}"/>
						</s:else>
						&euro;&nbsp;
						<s:if test="%{#editabile}">(<wp:i18n key="LABEL_MASSIMO"/> ${row['numeroDecimali']} <wp:i18n key="LABEL_DECIMALI"/>${limitiMinMax})</s:if>
					</c:when>
					
					<c:when test="${row['formato'] == 6}">
						<%-- DECIMALE --%>
						<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="${valore}" aria-required="true"/>
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}"/>
						</s:else>
						&nbsp;&nbsp;
						<s:if test="%{#editabile}">(<wp:i18n key="LABEL_MASSIMO"/> ${row['numeroDecimali']} <wp:i18n key="LABEL_DECIMALI"/>${limitiMinMax}) </s:if>
					</c:when>
					
					<c:when test="${row['formato'] == 3}">
						<%-- LISTA VALORI --%>
						<s:if test="%{#editabile}">
							<select id="criterioValutazione_${stat.index}" name="criterioValutazione" aria-required="true" >
								<option value=""><wp:i18n key="OPT_CHOOSE_VALORE"/></option>
								<c:forEach var="item" items="${row['listaValori']}" >
									 <option value="${item.key}" <c:if test="${item.key == row['valore']}">selected="selected"</c:if> >
									 ${item.value}
									 </option>
								</c:forEach>
							</select>
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}" />
						</s:else>
					</c:when>
					
					<c:when test="${row['formato'] == 4}">
						<%-- TESTO --%>
						<s:if test="%{#editabile}">
							<textarea id="criterioValutazione_${stat.index}" name="criterioValutazione" cols="40" rows="5" maxlength="2000" aria-required="true">${row['valore']}</textarea>
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}"/>
						</s:else>
						&nbsp;&nbsp;
						<s:if test="%{#editabile}">(<wp:i18n key="LABEL_MASSIMO_2000_CARATTERI"/>) </s:if>
					</c:when>
					
					<c:when test="${row['formato'] == 5}">
						<%-- INTERO --%>
						<%-- ^-?\d{0,15}(\.\d{0,2})?$  --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^-?\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="${valore}" aria-required="true"/>
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}"/>
						</s:else>
						&nbsp;&nbsp;
						<s:if test="%{#editabile}">(<wp:i18n key="LABEL_INDICARE_UN_INTERO"/>${limitiMinMax}) </s:if>
					</c:when>
					
					<c:when test="${row['formato'] == 50}">
						<%-- OFFERTA COMPLESSIVA MEDIANTE IMPORTO --%>
						<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="${valore}" aria-required="true" />
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}" />
						</s:else>
						&euro;&nbsp;
						<s:if test="%{#editabile}">(<wp:i18n key="LABEL_MASSIMO"/> ${row['numeroDecimali']} decimali${limitiMinMax}) </s:if>
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/notePrezziUnitari.jsp"></jsp:include>
					</c:when>
					
					<c:when test="${row['formato'] == 51}">
						<%-- OFFERTA COMPLESSIVA MEDIANTE RIBASSO --%>
						<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="${valore}" aria-required="true" />
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}" />
						</s:else>
						&nbsp;&nbsp;
						<s:if test="%{#editabile}">(massimo ${row['numeroDecimali']} <wp:i18n key="LABEL_DECIMALI"/>${limitiMinMax}) </s:if>
					</c:when>
					
					<c:when test="${row['formato'] == 52}">
						<%-- OFFERTA COMPLESSIVA MEDIANTE PREZZI UNITARI --%>
						<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
						<s:if test="%{#editabile}">
							<input type="text" id="criterioValutazione_${stat.index}" name="criterioValutazione" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="${valore}" aria-required="true" />
						</s:if>
						<s:else>
							<s:text name="format.money5dec"><s:param value="%{#sValore * 1}"/></s:text>
							<input type="hidden" id="criterioValutazione_${stat.index}" name="criterioValutazione" value="${valore}" />
						</s:else>
						&euro;&nbsp;
						<s:if test="%{#editabile}">(massimo ${row['numeroDecimali']} decimali${limitiMinMax}) </s:if>
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/notePrezziUnitari.jsp"></jsp:include>
					</c:when>
					
					<c:otherwise>
						<%-- 100 O ALTRO... --%>
					</c:otherwise>
			  	</c:choose>
			</div>
		</div>
	</c:if>
</c:forEach>


<%-- gestione della formattazione/validazione dei criteri di valutazione (date e importi) --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/parsley_validators.jsp" />


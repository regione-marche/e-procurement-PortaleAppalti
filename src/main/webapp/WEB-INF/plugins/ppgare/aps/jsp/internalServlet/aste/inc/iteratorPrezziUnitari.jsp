<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<%-- Parametri da passare a questa jsp

		vociDettaglio			<= QUESTO E' L'UNICO DA PASSARE ESTERNAMENTE!!! 	
		vociDettaglioSummary		
		vociDettaglioColonneVisibili
		vociDettaglioTitoliColonne
		vociDettaglioTipiColonne
		vociDettaglioColonneEditabili
		vociDettaglioColQta
		vociDettaglioColPrz
		vociDettaglioColImp
		vociDettaglioEditabile		

	Esempio di chiamata alla jsp			
	
		<c:set var="vociDettaglio" scope="request" value="${...}"/>		
		<jsp:include page="/WEB-INF/plu0gins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorPrezziUnitari.jsp" >
			<jsp:param name="vociDettaglioSummary" value="..." />
			...
		</jsp:include> 
--%>

<c:set var="visibleColumns" value="${fn:split(param.vociDettaglioColonneVisibili, ',')}" />
<c:set var="columnTitles" value="${fn:split(param.vociDettaglioTitoliColonne, ',')}" />
<c:set var="columnFormats" value="${fn:split(param.vociDettaglioTipiColonne, ',')}" />
<s:set var="totaleOffertaPrezziUnitari" value="0" />
  
<div class="table-container">
	<table id="tableViewVociDettaglio" summary="${param.vociDettaglioSummary}" class="info-table">	
		<thead>
			<c:forEach var="row" items="${vociDettaglio}" begin="0" end="0">																		
				<tr>	
					<c:forEach var="colname" items="${visibleColumns}" varStatus="i">
						<c:catch>
							<th scope="col">
            					${columnTitles[i.index]}
            				</th>
						</c:catch>				
          			</c:forEach>
				</tr>
			</c:forEach>
		</thead>		
		<!-- TIPO_VALIDATORE_DATA 		= 1 -->
		<!-- TIPO_VALIDATORE_IMPORTO 	= 2 -->
		<!-- TIPO_VALIDATORE_TABELLATO 	= 3 -->
		<!-- TIPO_VALIDATORE_NOTE 		= 4 -->
		<!-- TIPO_VALIDATORE_NUMERO 	= 5 -->
		<!-- TIPO_VALIDATORE_FLAG 		= 6 -->
		<!-- TIPO_VALIDATORE_STRINGA 	= 7 -->
		<tbody>	
			<s:set var="rowIndex" value="-1"/>
			<c:forEach var="row" items="${vociDettaglio}">
				<s:set var="rowIndex" value="%{#rowIndex + 1}"/>
				<s:set var="sRow" value="#request.vociDettaglio[#rowIndex]"/>
				
				<%-- NB: verifica se la voce è soggetta a       --%>
				<%-- soloSicurezza o nonSoggettoRibasso         --%>			
				<c:set var="rowReadonly" value="false"/>						  	 		
				<c:if test="${row['soloSicurezza'] != null}">
					<c:if test="${row['soloSicurezza']}">
						<c:set var="rowReadonly" value="true"/>
					</c:if>
				</c:if>
				<c:if test="${row['nonSoggettoRibasso'] != null}">
					<c:if test="${row['nonSoggettoRibasso']}">
						<c:set var="rowReadonly" value="true"/>
					</c:if>
				</c:if>
				
				<c:set var="rowVisible" value="true"/>
				<c:if test="${discriminante==1}">	<%-- 1=VOCI_SOGGETTE_RIBASSO  --%>
					<c:if test="${rowReadonly}">
						<c:set var="rowVisible" value="false"/>
					</c:if>
				</c:if>
				<c:if test="${discriminante==2}">	<%-- 2=VOCI_NON_SOGGETTE_RIBASSO  --%>
					<c:if test="${!rowReadonly}">
						<c:set var="rowVisible" value="false"/>
					</c:if>
				</c:if>										
						
				<c:if test="${rowVisible}">										
        		
					<tr>
						<s:set var="colIndex" value="-1"/>
						<c:forEach var="colname" items="${visibleColumns}" varStatus="i">
							<s:set var="colIndex" value="%{#colIndex + 1}"/>
							<c:catch>
								<s:set var="sColname">${colname}</s:set>			 							                										 			
				 				<s:set var="tipoValidatore">${columnFormats[i.index]}</s:set>
								
								<s:set var="colvalueS" value="%{#sRow[#sColname]}"/>														
								<c:set var="colvalue" value="${row[colname]}" />
													
								<c:set var="value">
									<s:if test="%{#tipoValidatore == 1}"> 		<!-- TIPO_VALIDATORE_DATA = 1 -->
	  									${colvalue}
									</s:if>
									<s:elseif test="%{#tipoValidatore == 2}"> 	<!-- TIPO_VALIDATORE_IMPORTO = 2 -->
										<s:text name="format.money5dec"><s:param value="%{#colvalueS}"/></s:text>&nbsp;&euro;
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == 3}"> 	<!-- TIPO_VALIDATORE_TABELLATO = 3 -->
										${colvalue}
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == 4}"> 	<!-- TIPO_VALIDATORE_NOTE = 4 -->
										${colvalue}
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == 5}"> 	<!-- TIPO_VALIDATORE_NUMERO = 5 -->
										<s:text name="format.money5dec"><s:param value="%{#colvalueS}"/></s:text>
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == 6}"> 	<!-- TIPO_VALIDATORE_FLAG = 6 -->
										${colvalue}
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == 7}"> 	<!-- TIPO_VALIDATORE_STRINGA = 7 -->
	 									${colvalue}
									</s:elseif>
									<s:else>
	 									${colvalue}
									</s:else>
								</c:set>
								
								<td <s:if test="%{#tipoValidatore == 2 || #tipoValidatore == 5}">class="money-content"</s:if>>
									${value}
	            				</td>
	            			</c:catch>
	         			</c:forEach>
					</tr>
					
					<c:if test="${param.vociDettaglioColQta != null && !empty param.vociDettaglioColQta && 
								  param.vociDettaglioColPrz != null && !empty param.vociDettaglioColPrz}" >
						<s:set var="qta">${param.vociDettaglioColQta}</s:set>
						<s:set var="prz">${param.vociDettaglioColPrz}</s:set>
						<s:set var="totaleOffertaPrezziUnitari" 
							   value="%{#totaleOffertaPrezziUnitari + (#sRow[#qta] * #sRow[#prz])}"/>
					</c:if>	
					
				</c:if>
				
			</c:forEach>
		</tbody>
	</table>
	
<%-- 	<c:if test="${param.vociDettaglioEditabile}" > --%>
		<div class="back-link">
			<strong><wp:i18n key="LABEL_IMPORTO_TOTALE" /> : 
				<span id="totaleImportoView">
 					<s:text name="format.money5dec"><s:param value="%{#totaleOffertaPrezziUnitari}"/></s:text>&nbsp;&euro;
				</span>
			</strong>
		</div>
<%-- 	</c:if> --%>
	
</div>


<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_ARTICOLO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_ARTICOLO"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" /></h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CODICE_CATEGORIA" /> :</label>
			<s:property value="articolo.codiceCategoria" />
		</div>
	
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_DESCRIZIONE_CATEGORIA" /> :</label>
			<s:property value="articolo.descrizioneCategoria" />
		</div>
	
		<div class="detail-row">
			<label><wp:i18n key="LABEL_TIPO_ARTICOLO" /> :</label>
			<s:iterator value="maps['tipiArticolo']">
				<s:if test="%{key == articolo.tipo}"><s:property value="%{value}"/></s:if>
			</s:iterator>
		</div>
		
		<s:if test="%{articolo.tipo == TIPO_PRODOTTO_BENE}">
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CODICE" /> :</label>
				<s:property value="articolo.codice" />
			</div>
		</s:if>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_DESCRIZIONE" /> :</label>
			<s:property value="articolo.descrizione" />
		</div>
	</div>
	
	<div class="detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_INFO_PRODOTTI" /></h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_OBBLIGO_INS_IMMAGINE" />&#63; </label>
			<s:if test="%{articolo.obbligoImmagine}">
				<s:text name="label.yes"/>
			</s:if>
			<s:else>
				<s:text name="label.no"/>
			</s:else>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_OBBLIGO_INS_DESCR" />&#63; </label>
			<s:if test="%{articolo.obbligoDescrizioneAggiuntiva}">
				<s:text name="label.yes"/>
			</s:if>
			<s:else>
				<s:text name="label.no"/>
			</s:else>
		</div>

		<s:if test="%{articolo.tipo == TIPO_PRODOTTO_BENE}">
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_OBBLIGO_INS_DIM" />&#63; </label>
				<s:if test="%{articolo.obbligoDimensioni}">
					<s:text name="label.yes"/>
				</s:if>
				<s:else>
					<s:text name="label.no"/>
				</s:else>
			</div>
		</s:if>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CERTIFICAZIONI_RICH" /> :</label>
			<s:property value="articolo.certificazioniRichieste" />
		</div>

		<c:if test="${sessionScope.currentUser != 'guest' && impresaAbilitataAlCatalogo}">
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_FACSIMILE_CERTIFICAZIONI" /> :</label>
				<s:if test="%{articolo.facSimileCertificazioni.length > 0}">
					<s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" /> 
					<s:set var="numeroDocumentiAllegati" value="0" />
					<s:set var="elencoDocumentiAllegati" value="%{articolo.facSimileCertificazioni}"/>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
						<jsp:param name="path" value="downloadDocumentoPubblico"/>
					</jsp:include>
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENT" />
				</s:else>
			</div>
			
			<div class="detail-row">
				<label><wp:i18n key="LABEL_ALTRI_DOCUMENTI" /> :</label>
				<s:if test="%{articolo.documentiUlteriori.length > 0}">
					<s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" /> 
					<s:set var="numeroDocumentiAllegati" value="0" />
					<s:set var="elencoDocumentiAllegati" value="%{articolo.documentiUlteriori}"/>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
						<jsp:param name="path" value="downloadDocumentoPubblico"/>
					</jsp:include>
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENT" />
				</s:else>
			</div>
		</c:if>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_OBBLIGO_SCHEDA_TECNICA" />&#63; </label>
			<s:if test="%{articolo.obbligoSchedaTecnica}">
				<s:text name="label.yes"/>
			</s:if>
			<s:else>
				<s:text name="label.no"/>
			</s:else>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_INSERIMENTO_GARANZIA" />&#63; </label>
			<s:if test="%{articolo.obbligoGaranzia}">
				<s:text name="label.yes"/>
			</s:if>
			<s:else>
				<s:text name="label.no"/>
			</s:else>
		</div>
	</div>	
	
	<div class="detail-section <c:if test='${sessionScope.currentUser == "guest" || impresaAbilitataAlCatalogo}'>last-detail-section</c:if>">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ALTRI_DATI" /></h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_COLORE_ARTICOLO" /> :</label>
			<s:property value="articolo.colore" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_TEMPO_MAX_CONSEGNA" /> :</label>
			<s:property value="articolo.tempoMaxConsegna" />
			&nbsp;
			<s:iterator value="maps['tipiUnitaMisuraTempiConsegna']">
				<s:if test="%{key == articolo.unitaMisuraTempoConsegna}"><s:property value="%{value}"/></s:if>
			</s:iterator>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_NOTE" /> :</label>
			<s:property value="articolo.note" />
		</div>
	</div>

	<c:if test="${sessionScope.currentUser != 'guest' && !impresaAbilitataAlCatalogo}">
		<div class="detail-section last-detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_INFO_PRODOTTI" /></h3><!--solo per utenti loggati-->

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_PROD_CARICATI_OE" /> :</label>
				<s:property value="prodottiCaricatiOE" />
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_PROD_CARICATI_ALTRI_OE" /> :</label>
				<s:property value="prodottiCaricatiAOE" />
			</div>

			<s:if test="%{visualizzaPrezziOE}" >
				<div class="detail-row">
					<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_MIGLIOR_PREZZO_OE" /> :</label>
					<s:if test="%{migliorPrezzoAOE != null}">
						<s:text name="format.money"><s:param value="migliorPrezzoAOE"/></s:text>&nbsp;&euro;
					</s:if>
				</div>
			</s:if>
		</div>
	</c:if>

	<c:if test="${sessionScope.currentUser != 'guest' && impresaAbilitataAlCatalogo && impresaIscrittaACategoria}">
		<s:if test="%{!limiteInserimentoProdottiRaggiunto}">
			<div class="azioni">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageDefinizioneProdotto.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_DETTAGLIO_ARTICOLO_INSERISCI" var="valueInserisciProdButton" />
						<wp:i18n key="TITLE_DETTAGLIO_ARTICOLO_INSERISCI" var="titleInserisciProdButton" />
						<s:submit value="%{#attr.valueInserisciProdButton}" 
								  title="%{#attr.titleInserisciProdButton}" 
								  cssClass="button" />
						<input type="hidden" name="catalogo" value="${codiceCatalogo}"/>
						<input type="hidden" name="articoloId" value="${articoloId}"/>
						<input type="hidden" name="prodottiCaricati" value="${prodottiCaricatiOE}"/>
						<s:hidden name="wizardSourcePage" value="%{PAGINA_DETTAGLIO_ARTICOLO}"/>
					</div>
				</form>
			</div>
		</s:if>
		<s:else>
			<div class="note">
				<s:text name="Errors.limiteInserimentoProdottiPerArticolo"><s:param value="maxProdottiArticolo"/></s:text>
			</div>
		</s:else>
	</c:if>

	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchArticoli.action" />&amp;codiceCatalogo=<s:property value="codiceCatalogo"/>&amp;last=1">
			<wp:i18n key="LINK_BACK_TO_ARTICLES_LIST" />
		</a>
	</div>
</div>
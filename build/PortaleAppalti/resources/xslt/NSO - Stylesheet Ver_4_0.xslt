<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    Descrizione  :     			Stylesheet for Italian Order system NSO
    Versione : 			     	4.0
    Prodotto da :  	            MEF - RGS
	Ultima modifica :		    02/03/2020
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2" xmlns:n1="urn:oasis:names:specification:ubl:schema:xsd:Order-2" xmlns:n2="urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2" xmlns:n3="http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" />
	<xsl:decimal-format name="number" decimal-separator="," grouping-separator="."/>
	
	<xsl:template match="n1:Order | n2:OrderResponse | n3:StandardBusinessDocument">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<title>NSO</title>
				 <style type="text/css">
				
body {
	margin: 0em;
	text-align: center;
    color: #122D4E;
	background-repeat: repeat-x;*/
	background: white; /*#efeff7;*/
}

div.font1 {
	font-family: verdana, helvetica,sans-serif;
	font-size: 1em;
	font-weight: normal;
	margin-top: 0.0em;
	margin-right: 0em;
	margin-left: 0em;
	margin-bottom: 0em;
	padding: 0px;
	color: #122D4E;
	line-height: 130%;
}

div.font1b {
	font-family: verdana, helvetica,sans-serif;
	font-size: 1em;
	font-weight: bold;
	margin-top: 0.0em;
	margin-right: 0em;
	margin-left: 0em;
	margin-bottom: 0em;
	padding: 0px;
	color: #122D4E;
	line-height: 130%;
}

div.font2 {
	font-family: verdana, helvetica,sans-serif;
	font-size:0.9em;
	font-weight: normal;
	margin-top: 0.0em;
	margin-right: 0em;
	margin-left: 0em;
	margin-bottom: 0em;
	padding: 0px;
	color: #122D4E;
	line-height: 140%;
}

div.font7 {
	font-family: arial, verdana, helvetica,sans-serif;
	font-size: 1.3em;
	font-weight: bold;
	margin-top: 0.0em;
	margin-right: 0em;
	margin-left: 0em;
	margin-bottom: 0em;
	padding: 0px;
	color: #02749A;
	line-height: 140%;
}

div.font8 {
	font-family: arial, verdana, helvetica,sans-serif;
	font-size: 1.5em;
	font-weight: bold;
	margin-top: 0.0em;
	margin-right: 0em;
	margin-left: 0em;
	margin-bottom: 0em;
	padding: 0px;
	color: #02749A;
	line-height: 140%;
}

div.font9 {
	font-family: arial, verdana, helvetica,sans-serif;
	font-size: 1.5em;
	font-weight: normal;
	margin-top: 0.0em;
	margin-right: 0em;
	margin-left: 0em;
	margin-bottom: 0em;
	padding: 0px;
	color: #122D4E;
	line-height: 140%;
}

div.footer {
	font-family: verdana, helvetica,sans-serif;
	font-size: 1.3em;
	font-style: oblique;
	width: 68em;
	text-align: center;
	margin-left: auto;
	margin-right: auto;
	margin-top: 1em;
	color: #122D4E;
	border-top: 0.1em solid #02749A;
	border-top-style:dotted;
	line-height: 130%;
}

div.ordercontent {
	border: 0.0em solid #02749A;
	border-top: 0.1em solid #02749A;
	background-color: white;
	width: 68em;
	text-align: left;
	margin-left: auto;
	margin-right: auto;
	margin-top: 1em;
}

div.columnname {
	margin-left: 0px;
	margin-right: 0px;
	margin-top: 0.1em;
	margin-bottom: 0.6em;
	padding-left: 0.15em;
	/*  background: white; */
	font-style: normal;
	font-size: 0.9em;
	color: #122D4E;
	background-repeat: repeat-x;
}
div.columnname2 {
	/*margin-left: 0px;
	margin-right: 0px;
	margin-top: 0.0em;
	margin-bottom: 0.0em;
	padding-left: 0.0em;    */
	/*  background: white; */
	font-style: normal;
	font-weight: bold;
	font-size: 1.0em;
	color: #122D4E;
}
div.columnname3 {
	margin-left: 0px;
	margin-right: 0px;
	margin-top: 0.1em;
	margin-bottom: 0.6em;
	padding-left: 0.0em;
	/*  background: white; */
	font-style: normal;
	font-size: 0.9em;
	color: #122D4E;
	background-repeat: repeat-x;
}

div.columnnamebold {
	margin-left: 0px;
	margin-right: 0px;
	margin-top: 0em;
	margin-bottom: 0.2em;
	padding-left: 0.15em;
	/*  background: white; */
	font-style: normal;
	font-weight: bold;
	font-size: 1.2em;
	color: #122D4E;
	background-repeat: repeat-x;
}



div.leftparties {
	width: 33.8em;
	height: auto;
	padding: 0.2em;
	margin-right : 2.0em;
	margin-bottom : 0.6em;
	border: 0.1em solid #02749A;
	background: #b0b2b5;
	overflow: hidden;
}

div.leftheader {
	float: left;
	width: 34em;
	height: auto;
	margin-right : 2.5em;
}

div.rightheader {
	/* background-color: blue;*/
	height: auto;
	padding: 0,2em;	
	margin-bottom : 0.6em;    
	line-height: 150%;
	overflow: hidden;
}

div.orderdate {
    float: left;
	height: 4.8em;
	width: 14.8em;
	padding: 0em;	
	padding-left: 0.2em;
	margin-right: 1em;
	line-height: 150%;
	border: 0.1em solid #02749A;
	overflow: hidden;
	background: #b0b2b5;
}

div.duepayment {
    float: left;
	height: 4.8em;
	width: 14.8em;
	padding: 0em;	
	padding-left: 0.2em;
	line-height: 150%;
	border: 0.1em solid #02749A;    
	overflow: hidden;
	background: #b0b2b5;
}

div.ordertime {
    float: left;
	height: 3em;
	width: 14.8em;
	margin-right: 1em;
	padding: 0em;	
	padding-left: 0.2em;
	text-align: center;
	overflow: hidden;
}

div.ordercurrency {
    float: left;
	height: 3em;
	width: 14.8em;
	padding: 0em;	
	padding-left: 0.2em;
	text-align: right;
	overflow: hidden;
}



div.rightinfo {
	width: 30.5em;
	height: auto;
	padding: 0.2em;
	padding-left: 0.5em;
	border: 0.1em solid #02749A;	
	overflow: hidden;
}



div.rightorderheader {
	text-align: right;		
	height: 8.5em;
	padding: 0.2em;		
	line-height: 150%;	
	overflow: hidden;
}

div.orderline {
/*	 background-color: lime;*/
	margin: 0;
	padding: 0.0em;
	border-bottom: 0.0em solid #02749A;
}

div.ListItem {
	padding: 0em;
	overflow: hidden;
	font-size: 1.2em;
}



.hdrcol22 {
    background-color: #b0b2b5;
    font-family: arial, verdana, helvetica,sans-serif;
    font-size: 0.9em;
    border-bottom: 1px solid #02749A;
    font-weight: bold;
}

.hdrcol23 {
    /*background-color: white;*/
    font-family: arial, verdana, helvetica,sans-serif;
    font-size: 1.1em;

}

.hdrcol234 {
    background-color: white;
    font-family: arial, verdana, helvetica,sans-serif;
    font-size: 0.9em;
    border-bottom: 0.05em solid #02749A;
}

.hdrcol25 {
    background-color: #B5D5FB;
    font-family: arial, verdana, helvetica,sans-serif;
    font-size: 0.9em;
     font-weight: bold;
}

.hdrcolSmall {
    background-color: white;
    font-family: arial, verdana, helvetica,sans-serif;
    font-size: 0.9em;
}

.font9 {
	font-family: arial, verdana, helvetica,sans-serif;
	font-size: 0.9em;
	font-weight: normal;
	color: #122D4E;
}



.ntable {
border-collapse: collapse;
border: 1px solid #02749A;
}

.ntableMainRow {
background: #ECEDEE;
border-top: 1px solid #02749A;
}



.ntable2 { 
  border-collapse: collapse;
border: 1px solid #02749A; 
}
.ntable2 tr {
background: white;
}

.ntable2 tr:nth-child(odd) {
background: white;
}

.ntable2 tr:nth-child(odd) {
background: #ECEDEE;
}

.ntablenoborder { 
  background-color: white; 
  border-collapse: collapse; 
  border: 0px solid #02749A;
  margin: 0em;
  padding: 0em;
} /* table-layout: fixed */

div.otherinfo {
	border: 0.0em solid #02749A;
	background-color: white;
	width: 68em;
	text-align: left;
	margin-left: auto;
	margin-right: auto;
	margin-top: 1em;
	margin-bottom: 1em;
	/*page-break-before: always; */
}

div.headingBig {	
	padding-top: 0.8em;
	padding-bottom: 0.4em;
	padding-left: 0.4em;
	padding-right: 0.0em;
	text-align: left;
	font-size: 1.4em;
	font-weight: bold;
	font-family: arial, verdana, helvetica,sans-serif;
	color: #02749A;
}

div.floatbox {
	vertical-align: text-top;
	height: auto;
	width: 68em;
    overflow: off;
}

div.minifloat {
	vertical-align: text-top;
	font-size: 1.2em;
	height: auto;
	width: 26em;
    border: 0.1em solid #02749A;
	padding: 0.2em;
	padding-left: 0.5em;
}

div.invoicee {
	vertical-align: text-top;
	float: left;
	margin-right: 2.5em;
}

div.originator {
	vertical-align: text-top;
	
	float: right;
 }

div.delivery {
	vertical-align: text-top;
	width: 68em;
	padding: 0.2em;
	border: 0.1em solid #02749A;
	background: #b0b2b5;
}

div.leftdelivery {
	vertical-align: text-top;
	width: 32em;
	padding: 0.2em;
	padding-left: 0.5em;
	float: left;
}

div.rightdelivery {
	vertical-align: text-top;
	width: 32em;
	padding: 0.2em;
	float: right;
}

.clear { 
        clear: both; 
}
				 </style>
			</head>
			<body>
			<xsl:choose>
									<!--only order without SBDH-->
									<xsl:when test="/n1:Order">
										<xsl:call-template name="OrderContent"/>
										<xsl:call-template name="OtherInfo"/>
									</xsl:when>
									<!--only orderResponseAgreement without SBDH-->
									<xsl:when test="/n2:OrderResponse">
										<xsl:call-template name="OrderResponseAgreement"/>
									</xsl:when>
									<!--order or orderResponse with SBDH-->
									<xsl:when test="/n3:StandardBusinessDocument">
										<xsl:call-template name="SBDH"/>
									</xsl:when>
			</xsl:choose>
			<div style="margin-top: 2em;"/>
				<xsl:call-template name="Footer"/>
			</body>
		</html>
	</xsl:template>
	
	<!--****SBDH-->
	<xsl:template name="SBDH">
		<div class="font8">Busta SBDH
		<br/>
		 Mittente: <xsl:value-of select="n3:StandardBusinessDocumentHeader/n3:Sender/n3:Identifier"/>
		 <br/>
		 Destinatario <xsl:value-of select="n3:StandardBusinessDocumentHeader/n3:Receiver/n3:Identifier"/>
		 </div>
		 <xsl:choose>
			<!--check if the message contains an order-->						
			<xsl:when test="/n3:StandardBusinessDocument/n1:Order">
				<xsl:apply-templates select="/n3:StandardBusinessDocument/n1:Order"/>
			</xsl:when>
			<!--check if the message contains an orderResponseAgreement-->		
			<xsl:when test="/n3:StandardBusinessDocument/n2:OrderResponse">
				<xsl:apply-templates select="/n3:StandardBusinessDocument/n2:OrderResponse"/>
			</xsl:when>
		</xsl:choose>
		       
    </xsl:template>
	
	<!--order wrapped in a SBDH-->
	<xsl:template match="/n3:StandardBusinessDocument/n1:Order">
		<xsl:call-template name="OrderContent"/>
		<xsl:call-template name="OtherInfo"/>
	</xsl:template>
	
	<!--orderResponse wrapped in a SBDH-->
	<xsl:template match="/n3:StandardBusinessDocument/n2:OrderResponse">
		<xsl:call-template name="OrderResponseAgreement"/>
	</xsl:template>

	
	<xsl:template name="Footer">
	<div id="footer" class="footer"> 
		<br/>           
		<b>Tipo Documento: </b><xsl:value-of select=".//cbc:CustomizationID"/> 
		<br/>
		<b>Tipo Processo: </b><xsl:value-of select=".//cbc:ProfileID"/>
		<br/>
		<br/>
    </div>
       
    </xsl:template>
  
    <!--OrderResponse or OrderAgreeement-->
	<xsl:template name="OrderResponseAgreement">
		<xsl:if test="cbc:CustomizationID">
				<xsl:if test="contains(cbc:CustomizationID,'response')"> 
					<xsl:call-template name="ResponseContent"/>
				</xsl:if>
				<xsl:if test="contains(cbc:CustomizationID,'agreement')"> 
				<xsl:call-template name="OrderContent"/>
				<xsl:call-template name="OtherInfo"/>
				</xsl:if>
			</xsl:if>
		</xsl:template>
	
	<!--*****ORDER-->
	<xsl:template name="OrderContent">
		<div id="ordercontent" class="ordercontent">
					<div id="header">
						<div id="mainheader">
							<!-- main header -->
							<div id="orderheader" class="rightorderheader">
								<xsl:call-template name="Order"/>
							</div>
							<!-- order header -->
						</div>
						<div id="topheader">
						<div class="leftheader">
							<div id="parties" class="leftparties">
								<xsl:call-template name="Seller"/>
							</div>
							<div id="parties" class="leftparties">
								<xsl:call-template name="Buyer"/>
							</div>
							<!-- parties -->
						</div>
						<div id="sumheader" class="rightheader">
							<xsl:call-template name="Sumheader"/>
							<br/>
							<div id="orderinfo" class="rightinfo">
								<xsl:call-template name="Orderinfo"/>
							</div>
						</div>
						<p class="clear" />
							<!-- sumheader -->
					</div>
						<!-- top header -->
					</div>
					<!-- header -->
					<p class="clear" />
					<xsl:call-template name="Line"/>
					<xsl:call-template name="Totals"/>
					<p class="clear" />
		</div>
		<!-- OrderContent -->
	</xsl:template>

	<!--*****ORDER RESPONSE-->
	<xsl:template name="ResponseContent">
		<div id="responsecontent" class="ordercontent">
					<div id="header">
						<div id="mainheader">
							<!-- main header -->
							<div id="responseheader" class="rightorderheader">
								<xsl:call-template name="Response"/>
							</div>
							<!-- response header -->
						</div>
						<div id="topheader">
						<div class="leftheader">
							<div id="parties" class="leftparties">
								<xsl:call-template name="Seller"/>
							</div>
							<div id="parties" class="leftparties">
								<xsl:call-template name="Buyer"/>
							</div>
							<!-- parties -->
						</div>
						<div id="responsesumheader" class="rightheader">
							<xsl:call-template name="ResponseSumheader"/>
							<br/>
							<div id="responseinfo" class="rightinfo">
								<xsl:call-template name="Responseinfo"/>
							</div>
						</div>
						<p class="clear" />
							<!-- sumheader -->
					</div>
						<!-- top header -->
						
					</div>
					<p class="clear" />
					<p class="clear" />
					<xsl:if test="cac:OrderLine">
						<xsl:call-template name="ResponseLine"/>
					</xsl:if>
					<p class="clear" />
		</div>
		<!-- ResponseContent -->
	</xsl:template>
	
	<xsl:template name="Response">
		<div>
			<div class="font2">&#160;</div>
			<div class="font2">&#160;</div>
			<div class="font8">RISPOSTA
			<xsl:if test="cbc:OrderResponseCode">
				<xsl:if test="contains(cbc:OrderResponseCode,'AB')"> DI RICEZIONE
				</xsl:if>
				<xsl:if test="contains(cbc:OrderResponseCode,'AP')"> DI ACCETTAZIONE
				</xsl:if>
				<xsl:if test="contains(cbc:OrderResponseCode,'RE')"> DI DINIEGO
				</xsl:if>
				<xsl:if test="contains(cbc:OrderResponseCode,'CA')"> CON MODIFICHE
				</xsl:if>
			</xsl:if>
			</div>
			<div class="font7">Identificativo del documento:&#160;<xsl:value-of select="cbc:ID"/>
			</div>
		</div>
	</xsl:template>
	<!-- response -->
	
	<xsl:template name="Order">
		<div>
			<div class="font2">&#160;</div>
			<div class="font2">&#160;</div>
			
			
			<div class="font8">ORDINE
			<xsl:if test="cbc:CustomizationID">
				<xsl:if test="contains(cbc:CustomizationID,'agreement')"> PRE-CONCORDATO
				</xsl:if>
			</xsl:if>
			<!--for order-->			
			<xsl:if test="cac:OrderDocumentReference/cbc:ID">
				<xsl:if test="contains(cac:OrderDocumentReference/cbc:ID,'#Connected')"> INIZIALE COLLEGATO
				</xsl:if>
				<xsl:if test="contains(cac:OrderDocumentReference/cbc:ID,'#Revised')"> SOSTITUTIVO
				</xsl:if>
				<xsl:if test="contains(cac:OrderDocumentReference/cbc:ID,'#Cancelled')"> DI REVOCA
				</xsl:if>
				<xsl:if test="contains(cac:OrderDocumentReference/cbc:ID,'#Invoice')"> INIZIALE DI CONVALIDA
				</xsl:if>
			</xsl:if>
			<!--for order agreement-->	
			<xsl:if test="cac:OrderReference/cbc:ID">
				<xsl:if test="contains(cac:OrderReference/cbc:ID,'#Connected')"> INIZIALE COLLEGATO
				</xsl:if>
				<xsl:if test="contains(cac:OrderReference/cbc:ID,'#Revised')"> SOSTITUTIVO
				</xsl:if>
				<xsl:if test="contains(cac:OrderReference/cbc:ID,'#Cancelled')"> DI REVOCA
				</xsl:if>
				<xsl:if test="contains(cac:OrderReference/cbc:ID,'#Invoice')"> INIZIALE DI CONVALIDA
				</xsl:if>
			</xsl:if>
			</div>
			<div class="font7">Identificativo del documento:&#160;<xsl:value-of select="cbc:ID"/>
			</div>
		</div>
	</xsl:template>
	<!-- Order -->
		
	
	<xsl:template name="Buyer">
		<div class="columnnamebold">Dati del Cliente</div>
		<div style="margin-top: 0.8em; margin-left: 1.2em; line-height: 120%">
			<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyName">
			<div class="ListItem">
				<b>Denominazione: </b><b><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyName/cbc:Name"/></b>
			</div></xsl:if>
			<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity">
			<div class="ListItem">
				<b>Denominazione legale: </b> <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
			</div></xsl:if>
			<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress">
			<div class="ListItem">
				<b>Indirizzo </b><br/>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName">
					<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;</xsl:if>
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine/cbc:Line"/>
			</div>
			<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
			<div class="ListItem">
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
			</div></xsl:if>
			<div class="ListItem">
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone">
						<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;
				</xsl:if>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName">
						<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
				</xsl:if>
				<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
						<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
				</xsl:if>
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
			</div>
			</xsl:if>
			<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID">
				<div class="ListItem">										
					<b>Id. fiscale: </b>
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
				</div>
			</xsl:if>
			<xsl:if test="cac:BuyerCustomerParty/cac:Party/cbc:EndpointID">
					<div class="ListItem">
						<b>Endpoint: </b><xsl:if test="cac:BuyerCustomerParty/cac:Party/cbc:EndpointID/@schemeID"> <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cbc:EndpointID/@schemeID"/>: </xsl:if> <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cbc:EndpointID"/>
					</div>
				</xsl:if>
			<xsl:if test="(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Name) or (cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail) or
				(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telephone) or
				(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or
				(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) or
				(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:ID) or (cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID)">		
						<xsl:if test="(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Name)">
						<div class="ListItem"><b>Contatti: </b>
							<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Name"/>
							</div>
						</xsl:if>
						<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail">
							<div class="ListItem"><b>E-mail: </b><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
							</div>
						</xsl:if>
						<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telephone">
							<div class="ListItem"><b>Tel: </b><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Telephone"/>
							</div>
						</xsl:if>
						<xsl:if test="(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode)">
							<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID">
								<div class="ListItem">
									<b>Altri dati legali:</b>
								</div>
								<div class="ListItem"><xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"> <xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"/> : </xsl:if>
									<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
								</div> </xsl:if>
							<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div class="ListItem"><xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, 
									<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:if>
						</xsl:if>
						<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID">
							<div class="ListItem">
								<b>Ulteriori indicazioni: </b>
							<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
							</div>
						</xsl:if>
			</xsl:if>
			<!--for order agreement-->
			<xsl:if test="cac:BuyerCustomerParty/cac:DeliveryContact">
				<div class="ListItem">
					<b>Contatto del cliente: </b>
					<xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Name"/>
				</div>
				
				<xsl:if test="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ElectronicMail">
					<div class="ListItem"><b>E-mail: </b><xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Telephone">
					<div class="ListItem"><b>Tel: </b><xsl:value-of select="cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Telephone"/>
					</div>
				</xsl:if>
			</xsl:if>
		</div>
	</xsl:template>
	<!-- Buyer -->
	<xsl:template name="Seller">
		<div class="columnnamebold">Dati del Fornitore</div>
		<div style="margin-top: 0.8em; margin-left: 1.2em; line-height: 120%">
			<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyName">
				<div class="ListItem">
					<b>Denominazione: </b><b><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyName/cbc:Name"/></b>
				</div></xsl:if>
			<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity">
				<div class="ListItem">
					<b>Denominazione legale: </b> <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
				</div></xsl:if>
			<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress">
				<div class="ListItem">
					<b>Indirizzo</b><br/>
					<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName">
						<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;</xsl:if>
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine/cbc:Line"/>
				</div>
			<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
				<div class="ListItem">
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
				</div></xsl:if>
			<div class="ListItem">
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone">
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;
				</xsl:if>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName">
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
				</xsl:if>
				<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
				</xsl:if>
				<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
			</div>
			</xsl:if>
			<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID">
				<div class="ListItem">										
					<div class="font1b" style="display: inline">Id. fiscale: </div>
					<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
				</div>
			</xsl:if>
			<xsl:if test="cac:SellerSupplierParty/cac:Party/cbc:EndpointID">
				<div class="ListItem">
					<b>Endpoint: </b><xsl:if test="cac:SellerSupplierParty/cac:Party/cbc:EndpointID/@schemeID"> <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cbc:EndpointID/@schemeID"/>: </xsl:if> <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cbc:EndpointID"/>
				</div>
			</xsl:if>
			<xsl:if test="(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name) or (cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail) or
				(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone) or
				(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or
				(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) or
				(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:ID) or (cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID)">		
				<xsl:if test="(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name)">
					<div class="ListItem"><b>Contatti: </b>
							<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name"/>
							</div>
						</xsl:if>
						<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail">
							<div class="ListItem"><b>E-mail: </b><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
							</div>
						</xsl:if>
						<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone">
							<div class="ListItem"><b>Tel: </b><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Telephone"/>
							</div>
						</xsl:if>
						<xsl:if test="(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode)">
							<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID">
								<div class="ListItem">
									<b>Altri dati legali:</b>
								</div>
								<div class="ListItem"><xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"> <xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"/> : </xsl:if>
									<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
								</div> </xsl:if>
							<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div class="ListItem"><xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, 
									<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:if>
						</xsl:if>
						<xsl:if test="cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID[.!='']">
							<div class="ListItem">
								<b>Ulteriori indicazioni: </b>
							<xsl:value-of select="cac:SellerSupplierParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
							</div>
						</xsl:if>
			</xsl:if>
		</div>
	</xsl:template>
	<!-- Seller -->
	<xsl:template name="Sumheader">
		<div id="orderdate" class="orderdate">
			<div class="columnnamebold">Data emissione Documento</div>
			<div class="font9" style="text-align: center;">
				<xsl:call-template name="date">
					<xsl:with-param name="text" select="cbc:IssueDate"/>
				</xsl:call-template>
			</div>
		</div>
		<!-- order date -->
		<div id="duepayment" class="duepayment">
			<div class="columnnamebold">Totale da pagare</div>
			<div class="font9" style="text-align: center;">
			<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableAmount">
				<xsl:call-template name="numberdecdef">
					<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:PayableAmount"/>
					
				</xsl:call-template>
				</xsl:if>
				<!--for order agreement-->
				<xsl:if test="cac:LegalMonetaryTotal/cbc:PayableAmount">
				<xsl:call-template name="numberdecdef">
					<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:PayableAmount"/>
				</xsl:call-template>
				</xsl:if>
			</div>
		</div>
		<!-- Payment terms -->
		
		<div id="ordertime" class="ordertime">
		<xsl:if test="cbc:IssueTime">
			<div class="font1b" style="display: inline">Ora emissione: </div>
			<div class="font1" style="display: inline">
					<xsl:value-of select="cbc:IssueTime"/>
			</div>
		</xsl:if>
		</div>
		
		<div id="ordercurrency" class="ordercurrency">
			<xsl:if test="cbc:DocumentCurrencyCode">
				<div class="font1" style="text-align: right; display: inline"><b>Valuta Documento:&#160;</b><xsl:value-of select="cbc:DocumentCurrencyCode"/>&#160;</div>
			</xsl:if></div>
	</xsl:template>
	<!-- Sumheader -->
	
	<xsl:template name="ResponseSumheader">
		<div id="responsedate" class="orderdate">
			<div class="columnnamebold">Data emissione Documento</div>
			<div class="font9" style="text-align: center;">
				<xsl:call-template name="date">
					<xsl:with-param name="text" select="cbc:IssueDate"/>
				</xsl:call-template>
			</div>
		</div>
		<!-- response date -->
		<div id="ordref" class="duepayment">
			<div class="columnnamebold">Documento di riferimento</div>
			<div class="font1" style="text-align: center;">
				<xsl:value-of select="cac:OrderReference/cbc:ID"/>
				
			</div>
		</div>
		<!-- ordref -->
		
		<div id="responsetime" class="ordertime">
		<xsl:if test="cbc:IssueTime">
			<div class="font1b" style="display: inline">Ora emissione: </div>
			<div class="font1" style="display: inline">
					<xsl:value-of select="cbc:IssueTime"/>
			</div>
		</xsl:if>
		</div>
		
		<div id="responsecurrency" class="ordercurrency">
			<div class="font1" style="text-align: right; display: inline"><b>Valuta Documento:</b>&#160;<xsl:value-of select="cbc:DocumentCurrencyCode"/>&#160;</div>
		</div>
	</xsl:template>
	<!-- ReponseSumheader -->
	
	<xsl:template name="Responseinfo">
		<xsl:if test="cbc:Note">
			<div class="font1b" style="display: inline">Note al Documento: </div>
		</xsl:if>
		<div class="font1" style="display: inline">
			<xsl:value-of select="cbc:Note"/>
		</div>
		<br/>
		
		<xsl:variable name="ordref" select="cac:OrderReference/cbc:ID"/>
		<xsl:if test="contains($ordref,'#')">
		<!--xsl:if test="matches(cac:OrderReference/cbc:ID,'(.*\#\d{4}-\d{2}-\d{2}\#.*)')"-->
				<div class="font1b" style="display: inline">ID ordine:  </div>
				<div class="font1" style="display: inline">				
					<xsl:value-of select="substring-before($ordref, '#')"/></div> 
				<br/><div class="font1b" style="display: inline">Data ordine:  </div>
				<div class="font1" style="display: inline">	
					<xsl:value-of select="substring-before(substring-after($ordref, '#'),'#')"/></div> 
				<br/><div class="font1b" style="display: inline">Cliente:  </div>
				<div class="font1" style="display: inline">	
					<xsl:value-of select="substring-after(substring-after($ordref, '#'),'#')"/></div> 
		</xsl:if>
		<xsl:if test="cbc:SalesOrderID">
			<br/>
			<div class="font1b" style="display: inline">Identificativo dell'offerta: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cbc:SalesOrderID"/>
			</div>
		</xsl:if>
		
		<xsl:if test="cbc:CustomerReference">
			<br/>
			<div class="font1b" style="display: inline">Contatto del Cliente: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cbc:CustomerReference"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:Delivery/cac:PromisedDeliveryPeriod">
			<br/><div class="font1b" style="display: inline">Periodo di esecuzione della fornitura: </div>
			<div class="font1" style="display: inline">
				<xsl:if test="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate">
			<xsl:call-template name="date">
				<xsl:with-param name="text" select="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate"/>
			</xsl:call-template>
					- </xsl:if>
			<xsl:call-template name="date">
				<xsl:with-param name="text" select="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndDate"/>
			</xsl:call-template>
			</div>
		</xsl:if>
		
	</xsl:template>
	<!-- Responseinfo -->
	
	<xsl:template name="Orderinfo">
		<xsl:if test="cbc:Note">
			<div class="font1b" style="display: inline">Note al documento: </div>
		</xsl:if>
		<div class="font1" style="display: inline">
			<xsl:value-of select="cbc:Note"/>
		</div>
		<br/><br/>
		<xsl:if test="cbc:OrderTypeCode">
			<div class="font1b" style="display: inline">Tipo di Ordinazione: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cbc:OrderTypeCode"/>
			</div>
			<div class="font1" style="display: inline">

			<xsl:if test="cbc:OrderTypeCode='220'">
				<div class="font1" style="display: inline">- Ordinazione di acquisto</div>
			</xsl:if>
			<xsl:if test="cbc:OrderTypeCode='227'">
				<div class="font1" style="display: inline">- Ordinazione di consegna</div>
			</xsl:if>
			
			</div>
		</xsl:if>
		<!--for order agreement-->
		<xsl:if test="cbc:CustomizationID">
				<xsl:if test="contains(cbc:CustomizationID,'agreement')"> 
					<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms">
						<div class="font1b" style="display: inline">Tipo e sotto-tipo di Ordinazione: </div>
						<div class="font1" style="display: inline">
						<xsl:value-of select="cac:DeliveryTerms/cbc:SpecialTerms"/>
							<div class="font1" style="display: inline">

								
								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='220'">
								<div class="font1" style="display: inline">- Ordinazione di acquisto</div>
								</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='227'">
								<div class="font1" style="display: inline">- Ordinazione di consegna</div>
								</xsl:if>
								<xsl:if test="starts-with(cac:DeliveryTerms/cbc:SpecialTerms,'220#OF')">
								<div class="font1" style="display: inline">- Ordinazione di acquisto - Ordinazione di fatturazione per prodotti già in possesso del Cliente o già consumati</div>
								</xsl:if>
								<xsl:if test="starts-with(cac:DeliveryTerms/cbc:SpecialTerms,'220#OFR')">
								<div class="font1" style="display: inline">- Ordinazione di acquisto - Ordinazione di fatturazione e reintegro</div>
								</xsl:if>
								<xsl:if test="starts-with(cac:DeliveryTerms/cbc:SpecialTerms,'227#CD')">
								<div class="font1" style="display: inline">- Ordinazione di consegna - Ordinazione in conto deposito</div>
								</xsl:if>
								<xsl:if test="starts-with(cac:DeliveryTerms/cbc:SpecialTerms,'227#CV')">
								<div class="font1" style="display: inline">- Ordinazione di consegna - Ordinazione in conto visione</div>
								</xsl:if>
								<xsl:if test="starts-with(cac:DeliveryTerms/cbc:SpecialTerms,'227#CG')">
								<div class="font1" style="display: inline">- Ordinazione di consegna - Ordinazione in comodato gratuito</div>
								</xsl:if>
							</div>
						</div>
					</xsl:if>
				</xsl:if>
			</xsl:if>
		
		<xsl:if test="cbc:AccountingCost">
			<br/>
			<div class="font1b" style="display: inline">Classificazione contabile della fornitura: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cbc:AccountingCost"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID">
			<br/>
			<div class="font1b" style="display: inline">ID Acquirente: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cac:BuyerCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cbc:CustomerReference">
			<br/>
			<div class="font1b" style="display: inline">Contatto del Cliente: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cbc:CustomerReference"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:ValidityPeriod/cbc:EndDate">
			<br/>
			<div class="font1b" style="display: inline">Data scadenza Ordine: </div>
			<div class="font1" style="display: inline">
			<xsl:call-template name="date">
					<xsl:with-param name="text" select="cac:ValidityPeriod/cbc:EndDate"/>
			</xsl:call-template>
			</div>
		</xsl:if>
		<xsl:if test="cac:QuotationDocumentReference/cbc:ID">
			<br/>
			<div class="font1b" style="display: inline">Quotazione di riferimento: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cac:QuotationDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:OrderDocumentReference/cbc:ID">
			<br/>
			<div class="font1b" style="display: inline">Documento di riferimento: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cac:OrderDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<!--for order agreement-->
		<xsl:if test="cac:OrderReference/cbc:ID">
			<br/>
			<div class="font1b" style="display: inline">Documento di riferimento: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cac:OrderReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:OriginatorDocumentReference">
			<br/>
			<div class="font1b" style="display: inline">Codice Identificativo Gara: </div>
			<div class="font1" style="display: inline">
					<xsl:value-of select="cac:OriginatorDocumentReference/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:Contract">
			<br/>
			<div class="font1b" style="display: inline">Identificativo del contratto: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cac:Contract/cbc:ID"/>
			</div>
		</xsl:if>
		<xsl:if test="cac:PaymentTerms/cbc:Note">
			<br/>
			<div class="font1b" style="display: inline">Termini di pagamento: </div>
			<div class="font1" style="display: inline">
				<xsl:value-of select="cac:PaymentTerms/cbc:Note"/>
			</div>
		</xsl:if>
	</xsl:template>
	<!-- Orderinfo -->
	
	<xsl:template name="Line">
		<div>&#160;</div>
		<div id="orderline" class="orderline">
			<table width="100%" cellspacing="0" cellpadding="3" class="ntable" role="presentation" >
				<tr height="25">
					<td width="4%" class="hdrcol22" align="left">ID linea</td>
					<td width="6%" class="hdrcol22" align="left">ID attribuito<br/>dal fornitore</td>
					<td width="40%" class="hdrcol22" align="left">Denominazione bene/servizio</td>
					<td width="6%" class="hdrcol22" align="center">Quantità<br/>(Q.tà effettiva)</td>
					<td width="6%" class="hdrcol22" align="center">UdM</td>
					<td width="6%" class="hdrcol22" align="center">Prezzo unitario*</td>
					<td width="11%" class="hdrcol22" align="center">Importo linea</td>
					<td width="10%" class="hdrcol22" align="center">Sconti<br/>Maggiorazioni</td>
					<td width="11%" class="hdrcol22" align="center">IVA %</td>
				</tr>
				<xsl:for-each select="cac:OrderLine">
					<tr class="ntableMainRow">
						<td width="4%" valign="top" align="right" class="hdrcol23">
							<!-- line number -->
							<xsl:value-of select="cac:LineItem/cbc:ID"/>.&#160;
						</td>
						<td width="6%" valign="top" align="left" class="hdrcol23">
							<!-- identifier -->
							<xsl:value-of select="cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ID"/>
						</td>
						<td width="40%" valign="top" align="left" class="hdrcol23">
							<!-- description -->
							<xsl:value-of select="cac:LineItem/cac:Item/cbc:Name"/>
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- quantity -->
							<xsl:call-template name="numberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cbc:Quantity"/>
							</xsl:call-template>&#160;
							<xsl:if test="cac:LineItem/cac:Delivery/cbc:Quantity">
							<br/>(<xsl:call-template name="numberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cbc:Quantity"/>
							</xsl:call-template></xsl:if>
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- UoM -->
							<xsl:call-template name="UoM">
								<xsl:with-param name="text" select="cac:LineItem/cbc:Quantity/@unitCode"/>
							</xsl:call-template>
							<xsl:if test="cac:LineItem/cac:Delivery/cbc:Quantity">
								<br/><xsl:call-template name="UoM">
									<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cbc:Quantity/@unitCode"/>
								</xsl:call-template>)</xsl:if>
						</td>
							
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Price-->
							<xsl:call-template name="numberdecdef8">
								<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:PriceAmount"/>
							</xsl:call-template>
						</td>
						<td width="11%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Line tot -->
							<xsl:call-template name="numberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cbc:LineExtensionAmount"/>
							</xsl:call-template>
						</td>
						<td width="10%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Allowance/charge amounts-->
							<xsl:for-each select="cac:LineItem/cac:AllowanceCharge[cbc:ChargeIndicator='true']">
								+ <xsl:call-template name="numberdecdef">
									<xsl:with-param name="text" select="cbc:Amount"/>
								</xsl:call-template><br/>
							</xsl:for-each>
							<!-- Allowance/charge amounts-->
							<xsl:for-each select="cac:LineItem/cac:AllowanceCharge[cbc:ChargeIndicator='false']">
								- <xsl:call-template name="numberdecdef">
									<xsl:with-param name="text" select="cbc:Amount"/>
								</xsl:call-template><br/>
							</xsl:for-each>
							
						</td>
						<td width="11%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- VAT %-->
							<xsl:call-template name="numberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cac:Item/cac:ClassifiedTaxCategory/cbc:Percent"/>
							</xsl:call-template>&#160;
						</td>
					</tr>
					<!--additional line info-->
					<!--additional line charge/allowance info-->
					<xsl:if test="cac:LineItem/cac:AllowanceCharge[cbc:ChargeIndicator='false']">
						<tr>
							<td width="4%" class="hdrcol23" align="right"></td>
							<td width="6%" class="hdrcol23" align="left" colspan="8"><b>Dettagli sconti linea d'ordine</b></td>
						</tr>
						<xsl:for-each select="cac:LineItem/cac:AllowanceCharge[cbc:ChargeIndicator='false']">	
							<tr>
								<td width="4%" class="hdrcol23" align="right"></td>
								<td width="6%" class="hdrcol23" align="left" colspan="8">+ Tipo di sconto: <i><xsl:value-of select="cbc:AllowanceChargeReason"/>&#160;<xsl:value-of select="cbc:AllowanceChargeReasonCode"/></i>
									<xsl:if test="cbc:MultiplierFactorNumeric"> - Percentuale di sconto: <xsl:value-of select="cbc:MultiplierFactorNumeric"/>% </xsl:if>
									<xsl:if test="cbc:BaseAmount"> - Base di calcolo: <xsl:value-of select="cbc:BaseAmount"/>&#160; </xsl:if>
								</td>
							</tr>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="cac:LineItem/cac:AllowanceCharge[cbc:ChargeIndicator='true']">
						<tr>
							<td width="4%" class="hdrcol23" align="right"></td>
							<td width="6%" class="hdrcol23" align="left" colspan="8"><b>Dettagli maggiorazioni linea d'ordine</b></td>
						</tr>
						<xsl:for-each select="cac:LineItem/cac:AllowanceCharge[cbc:ChargeIndicator='true']">	
							<tr>
								<td width="4%" class="hdrcol23" align="right"></td>
								<td width="6%" class="hdrcol23" align="left" colspan="8">+ Tipo di maggiorazione: <i><xsl:value-of select="cbc:AllowanceChargeReason"/>&#160;<xsl:value-of select="cbc:AllowanceChargeReasonCode"/></i>
									<xsl:if test="cbc:MultiplierFactorNumeric"> - Percentuale di maggiorazione: <xsl:value-of select="cbc:MultiplierFactorNumeric"/>% </xsl:if>
									<xsl:if test="cbc:BaseAmount"> - Base di calcolo: <xsl:value-of select="cbc:BaseAmount"/>&#160; </xsl:if>
								</td>
							</tr>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="cac:LineItem/cac:Price/cbc:BaseQuantity or cac:LineItem/cac:Price/cac:AllowanceCharge">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Prezzo bene/servizio: </b>
								<xsl:call-template name="numberdecdef8">
									<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:PriceAmount"/>
								</xsl:call-template>&#160;
								<xsl:value-of select="cac:LineItem/cac:Price/cbc:PriceAmount/@currencyID"/>&#160;(prezzo al netto IVA)
								<xsl:if test="cac:LineItem/cac:Price/cbc:BaseQuantity">
									- <i>quantità base: </i><xsl:value-of select="cac:LineItem/cac:Price/cbc:BaseQuantity"/>
									&#160;
									<xsl:call-template name="UoM">
										<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:BaseQuantity/@unitCode"/>
									</xsl:call-template> </xsl:if>
								<!--for order agreement-->
								<xsl:if test="cac:LineItem/cac:Price/cbc:PriceType">
									- <i>tipo di prezzo: </i><xsl:value-of select="cac:LineItem/cac:Price/cbc:PriceType"/>
									&#160;
									<xsl:call-template name="UoM">
										<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:BaseQuantity/@unitCode"/>
									</xsl:call-template> </xsl:if>
								<xsl:if test="cac:LineItem/cac:Price/cac:AllowanceCharge">
									<xsl:if test="cac:LineItem/cac:Price/cac:AllowanceCharge/cbc:BaseAmount"><i> - Prezzo base: </i><xsl:value-of select="cac:LineItem/cac:Price/cac:AllowanceCharge/cbc:BaseAmount"/>&#160;</xsl:if>
									<i>Sconto/maggiorazione </i><xsl:call-template name="chargeindicator">
										<xsl:with-param name="amount" select="cac:LineItem/cac:Price/cac:AllowanceCharge/cbc:Amount"/>
										<xsl:with-param name="chrg" select="cac:LineItem/cac:Price/cbc:ChargeIndicator"/>
									</xsl:call-template>&#160;</xsl:if>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="cbc:Note">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Note alla linea d'ordine: </b>
								<xsl:value-of select="cbc:Note"/>
							</td>
						</tr>
					</xsl:if>
					<!--for order agreement-->
					<xsl:if test="cac:LineItem/cbc:Note">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Note alla linea d'ordine: </b>
								<xsl:value-of select="cac:LineItem/cbc:Note"/>
							</td>
						</tr>
					</xsl:if>	
					<xsl:if test="cac:LineItem/cac:Item/cbc:Description">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Descrizione bene/servizio: </b>
								<xsl:value-of select="cac:LineItem/cac:Item/cbc:Description"/>
							</td>
							</tr>
					</xsl:if>
					<xsl:if test="cac:LineItem/cbc:AccountingCost">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Classificazione contabile: </b>
								<xsl:value-of select="cac:LineItem/cbc:AccountingCost"/>
							</td>
						</tr>
					</xsl:if> 
					<xsl:if test="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Riferimenti a ulteriore documentazione: </b>
								<xsl:for-each select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference">
									<xsl:if test="cbc:ID">
										<xsl:value-of select="cbc:ID"/> //
									</xsl:if>
								</xsl:for-each>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="cac:LineItem/cac:Item/cac:BuyersItemIdentification/cbc:ID">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Identificativo attribuito dal cliente: </b>
								<xsl:value-of select="cac:LineItem/cac:Item/cac:BuyersItemIdentification/cbc:ID"/>
							</td>
						</tr>
					</xsl:if> 
					<xsl:if test="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Identificativo standard: </b>
								<xsl:value-of select="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID"/>
							</td>
						</tr>
					</xsl:if> 
					<xsl:if test="cac:LineItem/cac:Item/cac:CommodityClassification">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Classificazione bene/servizio: </b>
								<xsl:for-each select="cac:LineItem/cac:Item/cac:CommodityClassification">  
									<xsl:value-of select="cbc:ItemClassificationCode"/>&#160;
									<xsl:value-of select="cbc:ItemClassificationCode/@listID"/>&#160;
									<xsl:value-of select="cbc:ItemClassificationCode/@listVersionID"/> //
								</xsl:for-each></td>
						</tr>
					</xsl:if> 
					<xsl:if test="cac:LineItem/cac:Item/cac:AdditionalItemProperty">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Ulteriori proprietà: </b>
								<table role="presentation" border="0" width="100%" cellpadding="0" cellspacing="0">
									<xsl:for-each select="cac:LineItem/cac:Item/cac:AdditionalItemProperty">
										<tr>
											<td height="5px" valign="up" align="left" class="hdrcol23"><b>+ </b></td>
											<td width="98%" valign="bottom" align="left" class="hdrcol23">
												Denominazione proprietà: <xsl:value-of select="cbc:Name"/><xsl:if test="cbc:NameCode"> -  <xsl:value-of select="cbc:NameCode"/>&#160;<i>codifica proprietà</i>
												</xsl:if><xsl:if test="cbc:ValueQualifier"> (<i>classificazione proprietà: </i><xsl:value-of select="cbc:ValueQualifier"/>)
												</xsl:if>
												: <xsl:value-of select="cbc:Value"/>&#160;<i>valore proprietà</i>
												<xsl:if test="cbc:ValueQuantity"> (<i>unità di misura:&#160;</i><xsl:value-of select="cbc:ValueQuantity"/> - <xsl:value-of select="cbc:ValueQuantity/@unitCode"/>)</xsl:if>
												
											</td>
										</tr>
									</xsl:for-each>
								</table></td>
						</tr>
					</xsl:if> 
					<xsl:if test="cac:LineItem/cac:Item/cac:ItemInstance">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Specificazioni bene/servizio: </b>
								<table role="presentation" border="0" width="100%" cellpadding="0" cellspacing="0">
									<xsl:for-each select="cac:LineItem/cac:Item/cac:ItemInstance">
										<tr>
											<td height="5px" valign="up" align="left" class="hdrcol23"><b>+ </b></td>
											<td width="98%" valign="bottom" align="left" class="hdrcol23">
												<xsl:if test="cbc:SerialID">Numero di serie bene/servizio: <xsl:value-of select="cbc:SerialID"/>&#160;</xsl:if>
												<xsl:if test="cac:LotIdentification/cbc:LotNumberID">(<i>Numero del lotto: </i><xsl:value-of select="cac:LotIdentification/cbc:LotNumberID"/>)</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table></td>
						</tr>
					</xsl:if> 
					
					
					
					<xsl:if test="cac:LineItem/cac:OriginatorParty/cac:PartyIdentification/cbc:ID or cac:LineItem/cac:OriginatorParty/cac:PartyName/cbc:Name">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Richiedente: </b>
								<!-- originator -->
								<xsl:value-of select="cac:LineItem/cac:OriginatorParty/cac:PartyIdentification/cbc:ID"/>&#160;
								<xsl:value-of select="cac:LineItem/cac:OriginatorParty/cac:PartyName/cbc:Name"/>
							</td>
						</tr>
					</xsl:if>			
					<xsl:if test="cac:LineItem/cbc:PartialDeliveryIndicator">
						<tr>
							<td/>
							<td colspan="8"  class="hdrcol23"><b>Indicatore di consegna parziale: </b>
								<xsl:call-template name="Partialdelivery">
									<xsl:with-param name="text" select="cac:LineItem/cbc:PartialDeliveryIndicator"/>
								</xsl:call-template>
							</td>
						</tr></xsl:if>
					<!--for order agreement-->
					<xsl:if test="cac:LineItem/cac:Item/cac:TransactionConditions">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Condizioni della transazione: </b>
								<xsl:value-of select="cac:LineItem/cac:Item/cac:TransactionConditions/cbc:ActionCode"/>&#160;</td>
						</tr>
					</xsl:if>			
					<xsl:if test="cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Periodo di esecuzione: </b>
								<!-- delivery period -->
								<xsl:call-template name="date">
									<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartDate"/>
								</xsl:call-template>
								<xsl:if test="cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartDate and cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndDate">
									-
								</xsl:if>
								<xsl:call-template name="date">
									<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndDate"/>
								</xsl:call-template>
								<!-- delivery period for order agreement-->
								<xsl:call-template name="date">
									<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate"/>
								</xsl:call-template>
								&#160;
								<xsl:value-of select="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartTime"/>
								&#160;
								<xsl:if test="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate and cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndDate">
									-
								</xsl:if> 
								<xsl:call-template name="date">
									<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndDate"/>
								</xsl:call-template>
								&#160;
								<xsl:value-of select="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndTime"/>
								&#160;
							</td>
						</tr>
					</xsl:if> 
					
					<!--for order agreement-->
					<xsl:if test="cac:LineItem/cac:Item/cac:Certificate">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Certificazioni: </b>
								<table role="presentation" border="0" width="100%" cellpadding="0" cellspacing="0">
									<xsl:for-each select="cac:LineItem/cac:Item/cac:Certificate">
										<tr>
											<td height="5px" valign="up" align="left" class="hdrcol23"><b>+ </b></td>
											<td width="98%" valign="bottom" align="left" class="hdrcol23">
												Identificativo: <xsl:value-of select="cbc:ID"/>&#160; - Tipo: <xsl:value-of select="cbc:CertificateType"/>&#160;
												(codice tipo: <xsl:value-of select="cbc:CertificateTypeCode"/>)&#160;
												<xsl:if test="cbc:Remarks"><i>Note:&#160;</i></xsl:if>
												<xsl:for-each select="cbc:Remarks"><xsl:value-of select="."/>&#160;</xsl:for-each>
												<br/>Emesso da: <xsl:value-of select="cac:IssuerParty/cac:PartyName/cbc:Name"/>&#160; 
												<xsl:if test="cac:DocumentReference"><i>Riferimento:&#160;</i></xsl:if>
												<xsl:for-each select="cac:DocumentReference"><xsl:value-of select="cbc:ID"/>&#160;</xsl:for-each>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</td>
						</tr>
					</xsl:if>	
					<!--for order agreement-->
					<xsl:if test="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cbc:DocumentType
						or cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cac:Attachment">
						<tr>
							<td/>
							<td colspan="8" class="hdrcol23">
								<b>Riferimenti ad ulteriore documentazione: </b><br/>
								Identificativo documento addizionale: <xsl:value-of select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cbc:ID"/> - 
								<xsl:if test="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cbc:DocumentType">
									Tipo documento: <xsl:value-of select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cbc:DocumentType"/> - 
								</xsl:if>
								<xsl:if test="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cac:Attachment">
									Nome file: <xsl:value-of select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename"/> - 
									Codice mime: <xsl:value-of select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode"/> 
									<xsl:if test="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cac:Attachment/cac:ExternalReference"> - Collegamento esterno: 
										<xsl:value-of select="cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference/cac:Attachment/cac:ExternalReference/cbc:URI"/>
									</xsl:if>
								</xsl:if>
							</td>
						</tr>
					</xsl:if>			
				</xsl:for-each>
			</table>
			</div>
</xsl:template>
		<!-- Line -->
		
<xsl:template name="ResponseLine">
		<div>&#160;</div>
		<div id="responseline" class="orderline">
			<table width="100%" cellspacing="0" cellpadding="3" class="ntable" role="presentation" >
				<tr height="25">
					<td width="4%" class="hdrcol22" align="center">Codice risposta*</td>
					<td width="4%" class="hdrcol22" align="left">ID linea</td>
					<td width="6%" class="hdrcol22" align="center">ID linea ordine originale</td>
					
					<td width="30%" class="hdrcol22" align="left">Descrizione bene/servizio</td>
					<td width="4%" class="hdrcol22" align="right">Quantità ordinata</td>
					<td width="6%" class="hdrcol22" align="center">UdM</td>
					<td width="6%" class="hdrcol22" align="center">Prezzo unitario</td>
					<td width="17%" class="hdrcol22" align="center">Periodo esecuzione</td>
					<td width="23%" class="hdrcol22" align="center">Quantità consegnata in ritardo</td>
					
				</tr>
				<xsl:for-each select="cac:OrderLine">
					<tr class="col2{position() mod 2}">
						<td width="4%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- line status -->
							<i><xsl:if test="cac:LineItem/cbc:LineStatusCode[.='1']">Aggiunta</xsl:if>
							<xsl:if test="cac:LineItem/cbc:LineStatusCode[.='5']">Accettata</xsl:if>
							<xsl:if test="cac:LineItem/cbc:LineStatusCode[.='42']">Accettata</xsl:if>
							<xsl:if test="cac:LineItem/cbc:LineStatusCode[.='7']">Rifiutata</xsl:if>
								<xsl:if test="cac:LineItem/cbc:LineStatusCode[.='3']">Modificata</xsl:if></i>
						</td>
						<td width="4%" valign="top" align="left" class="hdrcol23" nowrap="nowrap">
							<!-- line number -->
							<xsl:value-of select="cac:LineItem/cbc:ID"/>.&#160;
						</td>
						<td width="6%" valign="top" align="left" class="hdrcol23" nowrap="nowrap">
							<!-- order line ref -->
							<xsl:value-of select="cac:OrderLineReference/cbc:LineID"/>.&#160;
						</td>
					
						<td width="30%" valign="top" align="left" class="hdrcol23">
							<!-- description -->
							<table>
							<tr>
								<td class="hdrcol23" align="left"><xsl:value-of select="cac:LineItem/cac:Item/cbc:Name"/>
								</td>
							</tr>
							<xsl:if test="cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ID">
								<tr>
									<td class="hdrcol23" align="left" nowrap="nowrap">
									Id Fornitore:&#160;<xsl:value-of select="cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ID"/>
									</td>
								</tr>
							</xsl:if>
							<xsl:if test="cac:LineItem/cac:Item/cac:BuyersItemIdentification/cbc:ID">
								<tr>
									<td class="hdrcol23" align="left" nowrap="nowrap">
									Id Cliente:&#160;<xsl:value-of select="cac:LineItem/cac:Item/cac:BuyersItemIdentification/cbc:ID"/>
									</td>
								</tr>
							</xsl:if>
							<xsl:if test="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID">
								<tr>
									<td class="hdrcol23" align="left" nowrap="nowrap">
									Id Standard:&#160;<xsl:value-of select="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID/@schemeID"/>&#160;<xsl:value-of select="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID"/>
									
									</td>
								</tr>
							</xsl:if>
							</table>
							
						</td>
						<td width="4%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- quantity -->
							<xsl:call-template name="numberdecdef">
								<xsl:with-param name="text" select="cac:LineItem/cbc:Quantity"/>
							</xsl:call-template>&#160;
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- UoM -->
							<xsl:call-template name="UoM">
								<xsl:with-param name="text" select="cac:LineItem/cbc:Quantity/@unitCode"/>
							</xsl:call-template>
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- Price-->
							<xsl:call-template name="numberdecdef8">
								<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:PriceAmount"/>
							</xsl:call-template>
							<xsl:if test="cac:LineItem/cac:Price/cbc:BaseQuantity">&#160;(
								<xsl:value-of select="cac:LineItem/cac:Price/cbc:BaseQuantity"/>&#160;
								<xsl:call-template name="UoM">
									<xsl:with-param name="text" select="cac:LineItem/cac:Price/cbc:BaseQuantity/@unitCode"/>
								</xsl:call-template>)
							</xsl:if>
							
						</td>
						<td width="17%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<xsl:if test="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate">
							<xsl:call-template name="date">
								<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate"/>
							</xsl:call-template>
								- </xsl:if>
							<xsl:call-template name="date">
								<xsl:with-param name="text" select="cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndDate"/>
							</xsl:call-template>
						</td>
						<td width="23%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
						<xsl:value-of select="cac:LineItem/cbc:MaximumBackorderQuantity"/>
								
						</td>
						
					</tr>
					<!--substituted lines-->
					<xsl:if test="cac:SellerSubstitutedLineItem">
						<tr height="5" style="border-left: 0.1em solid #02749A; border-right: 0.1em solid #02749A;">
							<td width="4%" class="hdrcol25" align="left" colspan="9">LINEA SOSTITUTIVA</td>
						</tr>
					<tr height="5" style="border-left: 0.1em solid #02749A; border-right: 0.1em solid #02749A;">
					<td width="4%" class="hdrcol25" align="left"></td>
					<td width="6%" class="hdrcol25" align="left" colspan="2">ID linea</td>
					<td width="33%" class="hdrcol25" align="left">Descrizione bene/servizio sostitutivo</td>
					<td width="1%" class="hdrcol25" align="right"></td>
					<td width="6%" class="hdrcol25" align="center">Natura Imposta</td>
					<td width="6%" class="hdrcol25" align="center">IVA %</td>
						<td width="17%" class="hdrcol25" align="left" colspan="2">Ulteriori proprietà</td>
					
				</tr>
					<tr style="background-color:#D4E6FC; border-left: 0.1em solid #02749A; border-right: 0.1em solid #02749A;">
						<td width="4%" valign="top" align="left" class="hdrcol23" nowrap="nowrap">
							
						</td>
						<td width="6%" valign="top" align="left" class="hdrcol23" colspan="2">
							<!-- line number -->
							<xsl:value-of select="cac:SellerSubstitutedLineItem/cbc:ID"/>.&#160;
						</td>
						<td width="33%" valign="top" align="left" class="hdrcol23">
							<table>
							<tr>
								<td class="hdrcol23" align="left" nowrap="nowrap"><xsl:value-of select="cac:SellerSubstitutedLineItem/cac:Item/cbc:Name"/>
								</td>
							</tr>
							<xsl:if test="cac:SellerSubstitutedLineItem/cac:Item/cac:SellersItemIdentification/cbc:ID">
								<tr>
									<td class="hdrcol23" align="left" nowrap="nowrap">
									Id Fornitore:&#160;<xsl:value-of select="cac:SellerSubstitutedLineItem/cac:Item/cac:SellersItemIdentification/cbc:ID"/>
									</td>
								</tr>
							</xsl:if>
							<xsl:if test="cac:SellerSubstitutedLineItem/cac:Item/cac:StandardItemIdentification/cbc:ID">
								<tr>
									<td class="hdrcol23" align="left" nowrap="nowrap">
									Id Standard:&#160;<xsl:value-of select="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID/@schemeID"/>&#160;<xsl:value-of select="cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ID"/>
								</td>
								</tr>
							</xsl:if>
							<xsl:for-each select="cac:SellerSubstitutedLineItem/cac:Item/cac:CommodityClassification">
								<tr>
									<td class="hdrcol23" align="left" nowrap="nowrap">
									Classificazione bene/servizio:&#160;<xsl:value-of select="cbc:ItemClassificationCode/@listID"/>&#160;<xsl:value-of select="cbc:ItemClassificationCode/@versionID"/>&#160;<xsl:value-of select="cbc:ItemClassificationCode"/>
									</td>
								</tr>
							</xsl:for-each>
							</table>
						</td>
						<td width="1%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- VAT category -->
							<xsl:value-of select="cac:SellerSubstitutedLineItem/cac:Item/cac:ClassifiedTaxCategory/cbc:ID"/>&#160;
						</td>
						<td width="6%" valign="top" align="center" class="hdrcol23" nowrap="nowrap">
							<!-- VAT percent -->
							<xsl:value-of select="cac:SellerSubstitutedLineItem/cac:Item/cac:ClassifiedTaxCategory/cbc:Percent"/>&#160;
							
						</td>
						<td width="17%" valign="top" align="left" class="hdrcol23" colspan="2">
							<table role="presentation" border="0" width="100%" cellpadding="0" cellspacing="0">
									<xsl:for-each select="cac:SellerSubstitutedLineItem/cac:Item/cac:AdditionalItemProperty">
										<tr>
										<td width="98%" valign="bottom" align="left" class="hdrcol23">
											<xsl:value-of select="cbc:Name"/><xsl:if test="cbc:NameCode"> - <xsl:value-of select="cbc:NameCode"/>
											</xsl:if><xsl:if test="cbc:ValueQualifier"> ( <xsl:value-of select="cbc:ValueQualifier"/>)
											</xsl:if>
											: <xsl:value-of select="cbc:Value"/>
											<xsl:if test="cbc:ValueQuantity"> (<xsl:value-of select="cbc:ValueQuantity"/> - <xsl:value-of select="cbc:ValueQuantity/@unitCode"/>)</xsl:if>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</td>
						</tr>
					</xsl:if>
				</xsl:for-each>
			</table>
			<table width="100%" cellspacing="0" cellpadding="0" role="presentation" >
			<tr>
				  <td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				  <td width="52%" class="hdrcol23" align="left"><div class="hdrcolSmall" align="left" nowrap="nowrap">N° di linee: <xsl:value-of select='format-number(count(cac:OrderLine/cac:LineItem/cbc:ID), "###.###", "number")'/></div></td>
				  <td width="39%" class="hdrcol23" align="right" nowrap="nowrap">&#160;
				  <div class="hdrcolSmall" align="right" nowrap="nowrap">*Stato linea: 1 linea aggiunta; 3 linea modificata; 5 o 42
				  linea accettata; 7 linea declinata.</div></td>
			</tr>
				
		</table>
			</div>
</xsl:template>
		<!-- ResponseLine -->
<xsl:template name="Totals">
            <table width="100%" cellspacing="0" cellpadding="0" role="presentation" >
			<tr>
				  <td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				  <td width="52%" class="hdrcol23" align="left"><div class="hdrcolSmall" align="left" nowrap="nowrap">N° di linee: <xsl:value-of select='format-number(count(cac:OrderLine/cac:LineItem/cbc:ID), "###.###", "number")'/>&#160;&#160;&#160;&#160;&#160;&#160;&#160;*Prezzo unitario IVA esclusa.</div></td>
				  <td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				  <td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
					<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
			</tr>
            	<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableAmount or cac:LegalMonetaryTotal/cbc:PayableAmount">
			<tr>
			  <td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
			  <td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
			  <td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
				<td width="20%" class="hdrcol23" align="right" nowrap="nowrap"><b>Totale delle linee d'ordine:</b></td>
				<td width="11%" class="hdrcol23" align="right" nowrap="nowrap">
				<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableAmount">
					<xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:LineExtensionAmount"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="cac:LegalMonetaryTotal/cbc:PayableAmount">
					<xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:LineExtensionAmount"/>
					</xsl:call-template>
				</xsl:if>
				</td>
			</tr></xsl:if>
           
            <xsl:if test="cac:AnticipatedMonetaryTotal/cbc:ChargeTotalAmount or cac:LegalMonetaryTotal/cbc:ChargeTotalAmount">
            	<tr>
            		<td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            		<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            		<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            		<td width="20%" class="hdrcol23" align="right" nowrap="nowrap"><b>Totale maggiorazioni documento:</b></td>
            		<td width="11%" class="hdrcol23" align="right" nowrap="nowrap">
            			<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:ChargeTotalAmount">
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:ChargeTotalAmount"/>
            				</xsl:call-template>
            				
            			</xsl:if>
            			<xsl:if test="cac:LegalMonetaryTotal/cbc:ChargeTotalAmount">
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:ChargeTotalAmount"/>
            				</xsl:call-template>
            			</xsl:if>
            		</td>
            	</tr> </xsl:if>
        <xsl:for-each select="cac:AllowanceCharge[cbc:ChargeIndicator='true']">				
				<tr>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="left" class="hdrcol23">&#160;</td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
					<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">(<i><xsl:value-of select="cbc:AllowanceChargeReason"/>&#160;<xsl:value-of select="cbc:AllowanceChargeReasonCode"/>: 
						<xsl:if test="cbc:MultiplierFactorNumeric"><xsl:value-of select="cbc:MultiplierFactorNumeric"/>% </xsl:if><xsl:value-of select="cbc:BaseAmount"/>&#160;
						<xsl:if test="cac:TaxCategory">- Tipo imposta <xsl:value-of select="cac:TaxCategory/cac:TaxScheme/cbc:ID"/>&#160;<xsl:value-of select="cac:TaxCategory/cbc:ID"/>&#160;
							<xsl:if test="cac:TaxCategory/cbc:Percent"><xsl:value-of select="cac:TaxCategory/cbc:Percent"/>%</xsl:if>  </xsl:if>
					</i></td>
					<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">
				  <i><xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cbc:Amount"/>
				  </xsl:call-template>)</i>
					</td>
			</tr>
		  	</xsl:for-each>
            <xsl:if test="cac:AnticipatedMonetaryTotal/cbc:AllowanceTotalAmount or cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount">
            	<tr>
            		<td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            		<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            		<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            		<td width="20%" class="hdrcol23" align="right" nowrap="nowrap"><b>Totale sconti documento:</b></td>
            		<td width="11%" class="hdrcol23" align="right" nowrap="nowrap">
            			<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:AllowanceTotalAmount">-
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:AllowanceTotalAmount"/>
            				</xsl:call-template>
            			</xsl:if>
            			<xsl:if test="cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount">-
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:AllowanceTotalAmount"/>
            				</xsl:call-template>
            			</xsl:if>
            		</td>
            	</tr> </xsl:if>
              			  	<xsl:for-each select="cac:AllowanceCharge[cbc:ChargeIndicator='false']">				
				<tr>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"> &#160;</td>
				  <td valign="top" align="left" class="hdrcol23">&#160;</td>
				   <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
					<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">(<i><xsl:value-of select="cbc:AllowanceChargeReason"/>&#160;<xsl:value-of select="cbc:AllowanceChargeReasonCode"/>: 
						<xsl:if test="cbc:MultiplierFactorNumeric"><xsl:value-of select="cbc:MultiplierFactorNumeric"/>% </xsl:if><xsl:value-of select="cbc:BaseAmount"/>&#160;
						<xsl:if test="cac:TaxCategory">- Tipo imposta <xsl:value-of select="cac:TaxCategory/cac:TaxScheme/cbc:ID"/>&#160;<xsl:value-of select="cac:TaxCategory/cbc:ID"/>&#160;
							<xsl:if test="cac:TaxCategory/cbc:Percent"><xsl:value-of select="cac:TaxCategory/cbc:Percent"/>%</xsl:if>  </xsl:if>
					</i></td>
					<td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><i>-
						<xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cbc:Amount"/>
						</xsl:call-template>)</i>
					</td>
				</tr>
              </xsl:for-each>
            	<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount or cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount">
            		<tr>
            			<td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            			<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            			<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            			<td width="20%" class="hdrcol23" align="right" nowrap="nowrap">Importo al lordo IVA:</td>
            			<td width="11%" class="hdrcol23" align="right" nowrap="nowrap">
            				<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount">
            					<xsl:call-template name="numberdecdef">
            						<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount"/>
            					</xsl:call-template>
            					
            				</xsl:if>
            				<xsl:if test="cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount">
            					<xsl:call-template name="numberdecdef">
            						<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:TaxInclusiveAmount"/>
            					</xsl:call-template>
            				</xsl:if>
            			</td>
            		</tr> </xsl:if>
            <xsl:if test="cac:TaxTotal/cbc:TaxAmount">	  
				<tr>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="left" class="hdrcol23">&#160;</td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
					<td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><b>Imposta totale:</b></td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">
				  		<xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cac:TaxTotal/cbc:TaxAmount"/>
						</xsl:call-template></td>
				</tr></xsl:if>
            	<!--for order agreement-->
            	<xsl:for-each select="cac:TaxTotal/cac:TaxSubtotal"> 
            		<tr>
            			<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
            			<td valign="top" align="left" class="hdrcol23">&#160;</td>
            			<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
            			<td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><i>(Imposta&#160;
            				<xsl:value-of select="cac:TaxCategory/cac:TaxScheme/cbc:ID"/>&#160;
            				<xsl:value-of select="cac:TaxCategory/cbc:ID"/>&#160;
            				<xsl:if test="cac:TaxCategory/cbc:Percent">
            					<xsl:value-of select="cac:TaxCategory/cbc:Percent"/>%&#160;</xsl:if>
            				<xsl:if test="cac:TaxCategory/cbc:TaxExemptionReason">
            					<xsl:value-of select="cac:TaxCategory/cbc:TaxExemptionReason"/>&#160;</xsl:if>
            				su &#160; <xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cbc:TaxableAmount"/>
            				</xsl:call-template>:</i></td>
            			<td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><i>
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cbc:TaxAmount"/>
            				</xsl:call-template>)</i></td>
            		</tr></xsl:for-each>	 
            	<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount or cac:LegalMonetaryTotal/cbc:TaxExclusiveAmount">
            		<tr>
            			<td width="4%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            			<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            			<td width="13%" class="hdrcol23" align="right" nowrap="nowrap">&#160;</td>
            			<td width="20%" class="hdrcol23" align="right" nowrap="nowrap">Importo al netto IVA:</td>
            			<td width="11%" class="hdrcol23" align="right" nowrap="nowrap">
            				<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount">
            					<xsl:call-template name="numberdecdef">
            						<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount"/>
            					</xsl:call-template>
            				</xsl:if>
            				<xsl:if test="cac:LegalMonetaryTotal/cbc:TaxExclusiveAmount">
            					<xsl:call-template name="numberdecdef">
            						<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:TaxExclusiveAmount"/>
            					</xsl:call-template>
            				</xsl:if>
            			</td>
            		</tr> </xsl:if>
             <xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PrepaidAmount or cac:LegalMonetaryTotal/cbc:PrepaidAmount">
            	<tr>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
            		<td valign="top" align="left" class="hdrcol23">&#160;</td>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">Anticipo:</td>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">
            			<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PrepaidAmount">
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:PrepaidAmount"/>
            				</xsl:call-template>
            			</xsl:if>
            			<xsl:if test="cac:LegalMonetaryTotal/cbc:PrepaidAmount"><b>
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:PrepaidAmount"/>
            				</xsl:call-template></b>
            			</xsl:if>
            		</td>
            	</tr></xsl:if>
            <xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableRoundingAmount or cac:LegalMonetaryTotal/cbc:PayableRoundingAmount">
            	<tr>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
            		<td valign="top" align="left" class="hdrcol23">&#160;</td>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">Arrotondamento:</td>
            		<td valign="top" align="right" class="hdrcol23" nowrap="nowrap">
            			<xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableRoundingAmount">
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:PayableRoundingAmount"/>
            				</xsl:call-template>
            			</xsl:if>
            			<xsl:if test="cac:LegalMonetaryTotal/cbc:PayableRoundingAmount">
            				<xsl:call-template name="numberdecdef">
            					<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:PayableRoundingAmount"/>
            				</xsl:call-template>
            			</xsl:if>
            		</td>
            	</tr></xsl:if>
           <tr>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="left" class="hdrcol23">&#160;</td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="right" class="hdrcol234" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="right" class="hdrcol234" nowrap="nowrap">&#160;</td>
			</tr>
            <xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableAmount or cac:LegalMonetaryTotal/cbc:PayableAmount">
		  	<tr>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="left" class="hdrcol23">&#160;</td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">&#160;</td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap"><b>Ammontare da pagare:</b></td>
				  <td valign="top" align="right" class="hdrcol23" nowrap="nowrap">
				  <xsl:if test="cac:AnticipatedMonetaryTotal/cbc:PayableAmount"><b>
				  		<xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cac:AnticipatedMonetaryTotal/cbc:PayableAmount"/>
						</xsl:call-template></b>
					</xsl:if>
					 <xsl:if test="cac:LegalMonetaryTotal/cbc:PayableAmount"><b>
				  		<xsl:call-template name="numberdecdef">
							<xsl:with-param name="text" select="cac:LegalMonetaryTotal/cbc:PayableAmount"/>
						</xsl:call-template></b>
					</xsl:if>
					</td>
			</tr></xsl:if>
		</table>
	</xsl:template>
	<!-- template Totals -->
	
	<xsl:template name="OtherInfo">
		<div id="otherinfo" class="ordercontent">
		<!-- Comment Content Start -->
				
					<div id="other" class="otherinfo">
							<xsl:call-template name="OtherDelivery"/>
						<div class="floatbox">
							<xsl:call-template name="Invoicee"/>
							<xsl:call-template name="Originator"/>
						</div><p class="clear" />
						<xsl:call-template name="Attachment"/>
					</div>
					<!-- other -->
		</div>
		<!-- other info -->
	</xsl:template>
	<!-- template OtherInfo -->
	
	<xsl:template name="OtherDelivery">
		<xsl:if test="cac:Delivery"> 
						<div class="headingBig">Informazioni sulla consegna</div>	
						<div class="delivery">
						<div class="leftdelivery">
							<xsl:if test="cac:Delivery/cac:DeliveryLocation/cbc:ID">
									<div class="ListItem">
									<b>Identificativo del luogo: </b>
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cbc:ID"/>
							</div></xsl:if>
							<!--added on order transaction 3.1-->
							<xsl:if test="cac:Delivery/cac:DeliveryLocation/cbc:Name">
								<div class="ListItem">
									<b>Nome del luogo: </b>
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cbc:Name"/>
								</div></xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address">
								<div class="ListItem">	<b>Indirizzo </b><br/>
								<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName">
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:StreetName"/>&#160; </xsl:if>
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine/cbc:Line"/>
								</div>
								<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName">
									<div class="ListItem"><xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName"/>
									</div></xsl:if>
							<div class="ListItem">
								<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone">
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone"/>&#160;	</xsl:if>
								<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName">
									<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName"/>,&#160;</xsl:if>
								<xsl:if test="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity">
										<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity"/>,&#160;
								</xsl:if>
								<xsl:value-of select="cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode"/>
							</div>
							</xsl:if>
							<!--for order agreement-->
							<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address">
								<div class="ListItem">	<b>Indirizzo </b><br/>
									<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:StreetName">
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:StreetName"/>&#160;</xsl:if>
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cac:AddressLine/cbc:Line"/>
								</div>
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName">
								<div class="ListItem">
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName"/>
								</div>
								</xsl:if>
								<div class="ListItem">
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:PostalZone">
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:PostalZone"/>&#160;</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CityName">
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CityName"/>&#160;</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity">
										<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity"/>&#160;
								</xsl:if>
								<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:if>
							<div class="ListItem">
								<xsl:if test="cac:Delivery/cac:RequestedDeliveryPeriod">
									<b>Periodo di esecuzione della fornitura: </b>
									<xsl:call-template name="date">
										<xsl:with-param name="text" select="cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartDate"/>
									</xsl:call-template>
									- 
									<xsl:call-template name="date">
										<xsl:with-param name="text" select="cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndDate"/>
									</xsl:call-template>
								</xsl:if>
								<!--for order agreement-->
								<xsl:if test="cac:Delivery/cac:PromisedDeliveryPeriod">
									<b>Periodo di esecuzione della fornitura: </b>
									<xsl:call-template name="date">
										<xsl:with-param name="text" select="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartDate"/>
									</xsl:call-template>&#160;
									<xsl:value-of select="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:StartTime"/>
									&#160;
									- 
									<xsl:call-template name="date">
										<xsl:with-param name="text" select="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndDate"/>
									</xsl:call-template>&#160;
									<xsl:value-of select="cac:Delivery/cac:PromisedDeliveryPeriod/cbc:EndTime"/>
								</xsl:if>
							</div>
						
						</div>
						<div class="rightdelivery">
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PartyIdentification/cbc:ID">
								<div class="ListItem">
									<b>Identificativo beneficiario: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PartyIdentification/cbc:ID"/>
								</div></xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PartyName/cbc:Name">
								<div class="ListItem">
									<b>Denominazione del beneficiario: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PartyName/cbc:Name"/>
								</div></xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Name">
								<div class="ListItem">
									<b>Contatti: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Name"/>
								</div></xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Telephone">
								<div class="ListItem">
									<b>Tel: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Telephone"/>
								</div></xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ElectronicMail">
								<div class="ListItem">
									<b>Email: </b><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ElectronicMail"/>
								</div></xsl:if>
							<!--added on order transaction 3.1-->
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Address">
								<div class="ListItem">	<b>Indirizzo del beneficiario</b><br/>
									<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:StreetName">
										<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:StreetName"/>&#160; </xsl:if>
									<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cac:AddressLine/cbc:Line"/>
								</div>
								<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:AdditionalStreetName">
									<div class="ListItem"><xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:AdditionalStreetName"/>
									</div></xsl:if>
								<div class="ListItem">
									<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:PostalZone">
										<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:PostalZone"/>&#160;	</xsl:if>
									<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:CityName">
										<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:CityName"/>,&#160;</xsl:if>
									<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:CountrySubentity">
										<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cbc:CountrySubentity"/>,&#160;
									</xsl:if>
									<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:Address/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:if>
						<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PostalAddress">
						<div class="ListItem">
							<b>Indirizzo del beneficiario</b><br/>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:StreetName">
								<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:StreetName"/>&#160;</xsl:if>
							<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cac:AddressLine/cbc:Line"/>
						</div></xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:AdditionalStreetName">
						<div class="ListItem">
							<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:AdditionalStreetName"/>
						</div></xsl:if>
						<div class="ListItem">
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:PostalZone">
									<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:PostalZone"/>&#160;
							</xsl:if> 
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:CityName">
									<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:CityName"/>,&#160;
							</xsl:if>
							<xsl:if test="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:CountrySubentity">
									<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cbc:CountrySubentity"/>,&#160;
							</xsl:if>
							<xsl:value-of select="cac:Delivery/cac:DeliveryParty/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
						</div>
						<div class="ListItem">
							<xsl:if test="cac:DeliveryTerms">
								<b>Istruzioni particolari dell'Ordinazione </b></xsl:if>
						</div>
							<xsl:if test="cac:DeliveryTerms/cbc:ID">	
							<div class="ListItem">
								<b>Identificativo dei termini di trasferimento dei beni: </b>
								<xsl:value-of select="cac:DeliveryTerms/cbc:ID"/>
							</div></xsl:if>
							<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms">
							<div class="ListItem">
								<b>Sotto-tipo di Ordinazione: </b>
							<xsl:value-of select="cac:DeliveryTerms/cbc:SpecialTerms"/>
							</div>
							
							<div class="font1" style="display: inline">

								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='OF'">
								<div class="font1" style="display: inline">- Ordinazione di fatturazione per prodotti già in possesso del Cliente o già consumati</div>
								</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='OFR'">
								<div class="font1" style="display: inline">- Ordinazione di fatturazione e reintegro</div>
								</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='CD'">
								<div class="font1" style="display: inline">- Ordinazione in conto deposito</div>
								</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='CV'">
								<div class="font1" style="display: inline">- Ordinazione in conto visione</div>
								</xsl:if>
								<xsl:if test="cac:DeliveryTerms/cbc:SpecialTerms='CG'">
								<div class="font1" style="display: inline">- Ordinazione in comodato gratuito</div>
								</xsl:if>
							</div>
							
							</xsl:if>
							<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation">
								<div class="ListItem">
									<b>Descrizione e luogo: </b>
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cbc:ID/@schemeID"/>&#160;
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cbc:ID"/>
								</div>
								<!--for order agreement-->
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address">
									<div class="ListItem">
										<b>Indirizzo</b><br/>
										<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:StreetName">
											<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:StreetName"/>&#160;</xsl:if>
										<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cac:AddressLine/cbc:Line"/>
									</div></xsl:if>
								<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName">
									<div class="ListItem">
										<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:AdditionalStreetName"/>
									</div></xsl:if>
								<div class="ListItem">
									<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:PostalZone">
										<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:PostalZone"/>&#160;
									</xsl:if> 
									<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CityName">
										<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CityName"/>,&#160;
									</xsl:if>
									<xsl:if test="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity">
										<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cbc:CountrySubentity"/>,&#160;
									</xsl:if>
									<xsl:value-of select="cac:DeliveryTerms/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:if>
					</div>
					<p class="clear" />
				</div>
			</xsl:if>
		<br/>
	</xsl:template>
	<!-- template OtherDelivery -->
	
	<xsl:template name="Invoicee">	
		<xsl:if test="cac:AccountingCustomerParty">		
			<div class="invoicee">
			<div class="headingBig">Intestatario della Fattura</div>			
			<div class="minifloat">
			<xsl:if test="cac:AccountingCustomerParty">
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyName/cbc:Name"> 
					<div><b>Denominazione: </b>
					<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyName/cbc:Name"/>
					</div>
				</xsl:if>
				<xsl:if test="(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName)">
					<div>
						<b>Denominazione legale intestatario fattura: </b>
					</div>
					<div><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:RegistrationName"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
					<div>
						<b>Indirizzo:</b><br/>
						<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName">
							<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:StreetName"/>&#160;</xsl:if>
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine/cbc:Line"/>
					</div>
					<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName">
					<div>
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AdditionalStreetName"/>
					</div></xsl:if>
				<div>
					<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone">
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PostalZone"/>&#160;
					</xsl:if>
					<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName">
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CityName"/>,
					</xsl:if>
					<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity">
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentity"/>,
					</xsl:if>
					<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode"/>
				</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID[.!='']">
					<div><b>Id. fiscale: </b> <xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:CompanyID"/>
					</div>
				</xsl:if>
					<xsl:if test="(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName) or (cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Country/cbc:IdentificationCode) [.!='']">
						<div>
							<b>Altri dati legali:</b>
						</div>
						<div><xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"> <xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID/@schemeID"/> : </xsl:if>
						<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID"/>
						</div>
							<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName">
								<div><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CityName"/>, 
									<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:IdentificationCode"/>
								</div>
							</xsl:if>
					</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID">
					<div>
						<b>Ulteriori indicazioni: </b>
					<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
				</xsl:if>
				<xsl:if test="(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name)">
					<div><b>Contatti: </b>
					<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail">
					<div><b>E-mail: </b><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telephone">
					<div><b>Tel: </b><xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID[.!='']">
						<div>
						<b>Endpoint: </b>
							<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID/@schemeID"/>:<xsl:value-of select="cac:AccountingCustomerParty/cac:Party/cbc:EndpointID"/>
						</div>
				</xsl:if>	
			</xsl:if>
			</div>
			</div>
			</xsl:if>
	</xsl:template>
	<!-- template Invoicee contact-->
	
	<xsl:template name="Originator">	
	<xsl:if test="cac:OriginatorCustomerParty">	
			<div class="originator">	
			<div class="headingBig">Estensore dell'Ordine</div>			
			<div class="minifloat">	
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:PartyName/cbc:Name">
					<div>
						<b>Denominazione: </b>
					<xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:PartyName/cbc:Name"/></div>
				</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID">
					<div>
						<b>Identificativo: </b>
					<xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:PartyIdentification/cbc:ID"/>
					</div>
				</xsl:if>
				<xsl:if test="(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Name)">
						<div><b>Contatti: </b>
						<xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Name"/>
						</div>
					</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail">
					<div><b>E-mail: </b><xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ElectronicMail"/>
					</div>
				</xsl:if>
				<xsl:if test="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Telephone">
					<div><b>Tel: </b><xsl:value-of select="cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Telephone"/>
					</div>
				</xsl:if>
			</div>
		</div>
		</xsl:if>
	</xsl:template>	
	<!-- template OriginatorCustomer contact-->
	
<xsl:template name="Attachment">
		<xsl:if test="cac:AdditionalDocumentReference/cbc:ID[.!='']">
			<table>
				<tr height="10">
					<td/>
				</tr>
			</table>
			<div class="headingBig">Documenti addizionali</div>
			<table class="ntable" cellpadding="1" cellspacing="1" width="100%" role="presentation" border="0">
				<tr height="25">
					<td width="10%" class="hdrcol22" align="left">&#160;&#160;ID.</td>
					<td width="18%" class="hdrcol22" align="left">Tipo documento</td>
					<td width="42%" class="hdrcol22" align="left">Collegamento esterno</td>
					<td width="15%" class="hdrcol22" align="left">Nome file</td>
					<td width="15%" class="hdrcol22" align="left">Codice mime</td>
				</tr>
				<xsl:for-each select="cac:AdditionalDocumentReference">
					<tr height="25">
						<td class="hdrcol23" align="left" nowrap="nowrap">&#160;&#160;<xsl:value-of select="cbc:ID"/>.</td>
						<td class="hdrcol23" align="left" nowrap="nowrap">
							<xsl:value-of select="cbc:DocumentType"/>
						</td>
						<td class="hdrcol23" align="left" nowrap="nowrap">
							<xsl:variable name="urlString" select="concat('data:' , cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode, ';base64,', cac:Attachment/cbc:EmbeddedDocumentBinaryObject)"/>
							<xsl:variable name="filename" select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename"/>
							
							<xsl:choose>
     						<xsl:when test="system-property('xsl:vendor')='Microsoft'">
							
								<xsl:variable name="base64" select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject"/>
								<xsl:variable name="mimeCode" select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode"/>
							
								<a href="javascript:void(0);" onclick="downloadIE('{$base64}', '{$mimeCode}','{$filename}')" title="download del file {$filename}">
							
								<xsl:if test="cac:Attachment/cbc:EmbeddedDocumentBinaryObject and cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename and cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode">
								Download
								</xsl:if>
								</a>
							
							</xsl:when>
							
							<xsl:otherwise>
							
							<a href="{$urlString}" download="{$filename}" title="download del file {$filename}">
								<!-- xsl:value-of select="cac:Attachment/cac:ExternalReference/cbc:URI"/ -->
								<xsl:if test="cac:Attachment/cbc:EmbeddedDocumentBinaryObject and cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename and cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode">
								Download
								</xsl:if>
							</a>
							
							</xsl:otherwise>
       						</xsl:choose>
							
							
							
						</td>
						<td class="hdrcol23" align="left" nowrap="nowrap">
							<xsl:value-of select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@filename"/>
						</td>
						<td class="hdrcol23" align="left" nowrap="nowrap">
							<xsl:value-of select="cac:Attachment/cbc:EmbeddedDocumentBinaryObject/@mimeCode"/>
						</td>
					</tr>
				</xsl:for-each>
			</table>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="time">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<!--xsl:value-of select="concat(substring($text, 9, 2),'.', substring($text, 6, 2),'.', substring($text, 1, 4))"/-->
				<xsl:value-of select="concat(substring($text, 1, 2),':', substring($text, 4, 2),':', substring($text, 7, 2))"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template time -->
	<xsl:template name="date">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<!--xsl:value-of select="concat(substring($text, 9, 2),'.', substring($text, 6, 2),'.', substring($text, 1, 4))"/-->
				<xsl:value-of select="concat(substring($text, 1, 4),'-', substring($text, 6, 2),'-', substring($text, 9, 2))"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template date -->
	
	<xsl:template name="numberdec">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.##0,00", "number")'/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template numberdec -->
	<xsl:template name="numberdecdef">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.##0,00", "number")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='format-number(0.00, "###.##0,00", "number")'/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template numberdec -->
	<xsl:template name="numberdecdef8">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.########0,########", "number")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='format-number(0.00, "###.########0,########", "number")'/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template numberdec -->
	<xsl:template name="numberdecdef2">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.##0,0", "number")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='format-number(0.00, "###.##0,0", "number")'/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template numberdec -->
	<xsl:template name="number">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.###", "number")'/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template number -->
	<xsl:template name="numberdef">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.!='']">
				<xsl:value-of select='format-number($text, "###.###", "number")'/>
			</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template numberdef -->
	<xsl:template name="chargeindicator">
		<xsl:param name="chrg"/>
		<xsl:param name="amount"/>
		<xsl:choose>
			<xsl:when test="$chrg[.='true']">
				<xsl:value-of select='format-number($amount, "###.##0,00", "number")'/>
			</xsl:when>
			<xsl:when test="$chrg[.='false']">
				<xsl:value-of select='format-number($amount, "-###.##0,00", "number")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$amount[.!='']">
						<xsl:value-of select='format-number($amount, "###.##0,00", "number")'/>
					</xsl:when>
					<xsl:otherwise>0,00</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!-- template numberdef -->
	
	
	<xsl:template name="Partialdelivery">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.='true']">Sì</xsl:when>
			<xsl:when test="$text[.='false']">No</xsl:when>
			<!--xsl:otherwise>Non presente</xsl:otherwise-->
		</xsl:choose>
	</xsl:template>	
	
	<!-- template numberdef -->
	<xsl:template name="UoM">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="$text[.='C62']">Unità</xsl:when>
			<xsl:when test="$text[.='PR']">Paio</xsl:when>
			<xsl:when test="$text[.='XBK']">Pacchetto</xsl:when>
			<xsl:when test="$text[.='DZP']">Pacchetto dozzina</xsl:when>
			<xsl:when test="$text[.='KT']">Equipaggiamento kit</xsl:when>
			<xsl:when test="$text[.='RM']">Risma</xsl:when>
			<xsl:when test="$text[.='XBX']">Pacco imballaggio</xsl:when>
			<xsl:when test="$text[.='XOB']">Bancale</xsl:when>
			<xsl:when test="$text[.='KGM']">kg</xsl:when>
			<xsl:when test="$text[.='GRM']">g</xsl:when>
			<xsl:when test="$text[.='MC']">μg</xsl:when>
			<xsl:when test="$text[.='LTR']">l</xsl:when>
			<xsl:when test="$text[.='HGM']">hg</xsl:when>
			<xsl:when test="$text[.='MTR']">m</xsl:when>
			<xsl:when test="$text[.='CMT']">cm</xsl:when>
			<xsl:when test="$text[.='MMT']">mm</xsl:when>
			<xsl:when test="$text[.='LTR']">l</xsl:when>
			<xsl:when test="$text[.='MLT']">ml</xsl:when>
			<xsl:when test="$text[.='MTK']">m²</xsl:when>
			<xsl:when test="$text[.='CMK']">cm²</xsl:when>
			<xsl:when test="$text[.='MTQ']">m³</xsl:when>
			<xsl:when test="$text[.='CMQ']">cm³</xsl:when>
			<xsl:when test="$text[.='MMQ']">mm³</xsl:when>
			<xsl:when test="$text[.='KMT']">km</xsl:when>
			<xsl:when test="$text[.='TNE']">t</xsl:when>
			<xsl:when test="$text[.='KWH']">kWh</xsl:when>
			<xsl:when test="$text[.='DAY']">Giorno</xsl:when>
			<xsl:when test="$text[.='HUR']">Ora</xsl:when>
			<xsl:when test="$text[.='MIN']">Minuti</xsl:when>
			<xsl:when test="$text[.='ANN']">Anno</xsl:when>
			<xsl:when test="$text[.='E49']">Giorno di lavoro</xsl:when>
			<xsl:when test="$text[.='BQL']">Becquerel</xsl:when>
			<xsl:when test="$text[.='4N']">Mega-Becquerel</xsl:when>
			<xsl:when test="$text[.='GBQ']">Giga-Becquerel</xsl:when>
			<xsl:when test="$text[.='CUR']">Curie</xsl:when>
			<xsl:when test="$text[.='MCU']">Millicurie</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- template UoM -->
</xsl:stylesheet>
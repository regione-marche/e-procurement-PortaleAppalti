package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.struts2.ServletActionContext;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.type.PdfFieldTypeEnum;
import net.sf.jasperreports.engine.fill.JRTemplatePrintElement;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.type.PdfaConformanceEnum;

/**
 * Jasper Report PDF Exporter for PDF UA document (Accessibility)
 * 
 * @author 
 * @since 
 */
public class JRPdfExporterEldasoft extends JRPdfExporter {
	
	private BaseAction action;
	private JasperPrint print;
	private String reportName;	
	private boolean pdfACompliance = false;		// PDF-A
	private boolean pdfUACompliance = false;	// PDF-UA
	
	private String uuid;
	private ByteArrayOutputStream baosPdf;
	
	public String getUuid() {
		return uuid;
	}
	
	public ByteArrayOutputStream getOutputStream() {
		return baosPdf;
	}


	public static JRPdfExporterEldasoft newInstance() {
		JRPdfExporterEldasoft exporter = new JRPdfExporterEldasoft();
		return exporter;
	}
	public JRPdfExporterEldasoft setPdfACompliance(boolean value) {
		this.pdfACompliance = value;
		return this;
	}
	public JRPdfExporterEldasoft setPdfUACompliance(boolean value) {
		this.pdfUACompliance = value;
		return this;
	}
	public JRPdfExporterEldasoft setReportName(String reportName) {
		this.reportName = reportName;
		return this;
	}
	public JRPdfExporterEldasoft setAction(BaseAction action) {
		this.action = action;
		return this;
	}
	public JRPdfExporterEldasoft setPrint(JasperPrint print) {
		this.print = print;
		return this;
	}
	

	/**
	 * ********************************************************************************
	 * Custom PDF Graphic Stream Engine
	 *  
	 * NB: marca gli elementi grafici del PDF come artefatti per compatibilita' 
	 *     con lo standard PDF-UA (la validazione viene verificata con il tool PAC)
	 * ********************************************************************************
	 */
	protected class PdfUAGraphicsStreamEngine extends PDFGraphicsStreamEngine {
		
		private final List<String> PATH_CONSTRUCTION = Arrays.asList(
    			"m"			// m (moveto): Sposta il punto corrente alla posizione (x, y)
    			, "l" 		// l (lineto): Disegna una linea dal punto corrente alla posizione (x, y)
    			, "c" 		// c (curveto): Disegna una curva di Bézier cubica
    			, "v"		// v (curveto, versione abbreviata): Traccia una curva di Bézier cubica senza i primi due punti di controllo
    			, "y" 		// y (curveto, versione abbreviata): Traccia una curva di Bézier cubica senza gli ultimi due punti di controllo
    			, "h"		// ? ...
    			, "re"		// ? ...
    	);
    	private final List<String> PATH_PAINTING = Arrays.asList(
        		"s"			// s (stroke, senza chiudere il percorso): Applica il tracciato, ma non chiude il percorso
        		, "S"		// S (stroke): Applica il tracciato del percorso (stroke)
        		, "f"		// f (fill): Riempi il percorso
        		, "F"		// F (fill, senza chiudere il percorso): Riempi il percorso, ma non chiuderlo
        		, "f*"		// ? ...
        		, "B"		// B (fill and stroke, senza chiudere il percorso): Riempi e traccia il percorso, ma senza chiuderlo.
        		, "B*"		// ? ...
        		, "b"		// b (fill and stroke): Riempi il percorso e poi lo traccia.
        		, "b*"		// ? ...
        		, "n"		// n (closepath): Chiude il percorso, senza applicare il riempimento o il tracciato.
        );

	    private final PDDocument document;
	    private OutputStream replacementStream = null;
	    private ContentStreamWriter replacement = null;
	    boolean inOperator = false;
	    private int markedContentDepth = 0;
	    private boolean inArtifact = false;
		
	    /**
	     * costruttore
	     */
	    public PdfUAGraphicsStreamEngine(PDDocument document, PDPage page) {
	        super(page);
	        this.document = document;
	    }
	    
	    /**
	     * <p>
	     * This method retrieves the next operation before its registered
	     * listener is called. The default does nothing.
	     * </p>
	     * <p>
	     * Override this method to retrieve state information from before the
	     * operation execution.
	     * </p> 
	     */
	    protected void nextOperation(Operator operator, List<COSBase> operands) {
	    	//...
	    }

	    /**
	     * <p>
	     * This method writes content stream operations to the target canvas. The default
	     * implementation writes them as they come, so it essentially generates identical
	     * copies of the original instructions {@link #processOperator(Operator, List)}
	     * forwards to it.
	     * </p>
	     * <p>
	     * Override this method to achieve some fancy editing effect.
	     * </p> 
	     */
        protected void write(ContentStreamWriter contentStreamWriter, Operator operator, List<COSBase> operands) throws IOException {
            String operatorString = operator.getName();

            boolean unmarked = markedContentDepth == 0;
            boolean inArtifactBefore = inArtifact;

            if (unmarked && (!inArtifactBefore) && PATH_CONSTRUCTION.contains(operatorString)) {
            	// Begin Marked Content
                //super.write(contentStreamWriter, Operator.getOperator("BMC"), Collections.singletonList(COSName.ARTIFACT));
                contentStreamWriter.writeTokens(Collections.singletonList(COSName.ARTIFACT));
    	        contentStreamWriter.writeToken(Operator.getOperator("BMC"));
                inArtifact = true;
            }

            //super.write(contentStreamWriter, operator, operands);
            contentStreamWriter.writeTokens(operands);
	        contentStreamWriter.writeToken(operator);

            if (unmarked && inArtifactBefore && PATH_PAINTING.contains(operatorString)) {
            	// End Marked Content
                //super.write(contentStreamWriter, Operator.getOperator("EMC"), Collections.emptyList());
                contentStreamWriter.writeTokens(Collections.emptyList());
    	        contentStreamWriter.writeToken(Operator.getOperator("EMC"));
                inArtifact = false;
            }
        }

    	@Override
        public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
            if (inArtifact) {
                System.err.println("Structural error in content stream: Path not properly closed by path painting instruction.");
            }
            markedContentDepth++;
            super.beginMarkedContentSequence(tag, properties);
        }

        @Override
        public void endMarkedContentSequence() {
            markedContentDepth--;
            super.endMarkedContentSequence();
        }

	    // stub implementation of PDFGraphicsStreamEngine abstract methods
	    @Override
	    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException { }

	    @Override
	    public void drawImage(PDImage pdImage) throws IOException { }

	    @Override
	    public void clip(int windingRule) throws IOException { }

	    @Override
	    public void moveTo(float x, float y) throws IOException { }

	    @Override
	    public void lineTo(float x, float y) throws IOException { }

	    @Override
	    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException { }

	    @Override
	    public Point2D getCurrentPoint() throws IOException { return new Point2D.Float(); }

	    @Override
	    public void closePath() throws IOException { }

	    @Override
	    public void endPath() throws IOException { }

	    @Override
	    public void strokePath() throws IOException { }

	    @Override
	    public void fillPath(int windingRule) throws IOException { }

	    @Override
	    public void fillAndStrokePath(int windingRule) throws IOException { }

	    @Override
	    public void shadingFill(COSName shadingName) throws IOException { }

	    // Actual editing methods
	    @Override
	    public void processPage(PDPage page) throws IOException {
	        PDStream stream = new PDStream(document);
	        replacement = new ContentStreamWriter(replacementStream = stream.createOutputStream(COSName.FLATE_DECODE));
	        super.processPage(page);
	        replacementStream.close();
	        page.setContents(stream);
	        replacement = null;
	        replacementStream = null;
	    }

	    public void processFormXObject(PDFormXObject formXObject, PDPage page) throws IOException {
	        PDStream stream = new PDStream(document);
	        replacement = new ContentStreamWriter(replacementStream = stream.createOutputStream(COSName.FLATE_DECODE));
	        super.processChildStream(formXObject, page);
	        replacementStream.close();
	        try (OutputStream outputStream = formXObject.getCOSObject().createOutputStream()) {
	        	//stream.createInputStream().transferTo(outputStream);
	        	while (stream.createInputStream().available() > 0) {
	        		int x = stream.createInputStream().read();
	        		outputStream.write(x);
	        	}
	        	outputStream.flush();
	        	outputStream.close();
	        } finally {
	            replacement = null;
	            replacementStream = null;
	        }
	    }

	    // PDFStreamEngine overrides to allow editing
	    @Override
	    public void showForm(PDFormXObject form) throws IOException {
	        // DON'T descend into XObjects
	        // super.showForm(form);
	    }

	    @Override
	    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
	        if (inOperator) {
	            super.processOperator(operator, operands);
	        } else {
	            inOperator = true;
	            nextOperation(operator, operands);
	            super.processOperator(operator, operands);
	            write(replacement, operator, operands);
	            inOperator = false;
	        }
	    }
    
	}
	
	
	/**
	 * export a report
	 * 
	 * @throws JRException 
	 */
	@Override
	public void exportReport() throws JRException {
		try {
			exportReport_6_16_0();
			//exportReport_7_0_0();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "exportReport");
			throw new JRException(t);
		}
	}

//	/**
//	 * export Jasperreport 7.0.0
//	 * @throws Exception 
//	 */
//	private void exportReport_7_0_0() throws Exception {
//		
//	}
	
	/**
	 * export Jasperreport 6.16.0
	 * @throws Exception 
	 */
	private void exportReport_6_16_0() throws Exception {
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		configuration.setTagged(true);
		configuration.setEncrypted(false);
		configuration.setTagLanguage(Locale.ITALIAN.getLanguage());
		configuration.setMetadataTitle(reportName);
		configuration.setDisplayMetadataTitle(true);
		
		// PDF-A Compliance
		if(pdfACompliance) {
			ApsSystemUtils.getLogger().debug("PDF-A Abilitato.");
			addPdfAInfo(configuration);
		} else {
			ApsSystemUtils.getLogger().debug("PDF-A NON Abilitato.");
		}

		// PDF-UA Accessibility Compliance
		if(pdfUACompliance) {
			ApsSystemUtils.getLogger().debug("PDF-UA Abilitato.");
			addPdfAInfo(configuration);
		} else {
			ApsSystemUtils.getLogger().debug("PDF-UA NON Abilitato.");
		}

		// genera il PDF...
		ByteArrayOutputStream baosJasper = new ByteArrayOutputStream();
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		jasperPrintList.add(print);
		setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		setExporterOutput(new SimpleOutputStreamExporterOutput(baosJasper));
		setConfiguration(configuration);
		super.exportReport();
		
		// aggiungi un digest e i metadati relativi a PDF-A, PDF-UA allo stream del PDF...
		PDDocument pdf = PDDocument.load(baosJasper.toByteArray());
		addDigest(pdf, baosJasper);
		if(pdfUACompliance)
			addPdfUAInfo(pdf, reportName);
		baosPdf = new ByteArrayOutputStream();
        pdf.save(baosPdf);
		pdf.close();
	}
	
	/*
	 * PDF-A (pdfa1a, pdfa2a)
	 * 
     * @param pdf the pdf instance created from BAOS
     * @param title document
     * @return BAOS containing metadata (UA-identifier, title)
     */
    private void addPdfAInfo(SimplePdfExporterConfiguration configuration) {
    	// Occorre impostare un font di default che sia embedded
		// Occorre impostare un file di tipo icc esterno
		// Occorre modificare i jasper in modo che abbiano per il tag font l'attributo fontName=Arial
    	configuration.setPdfaConformance(PdfaConformanceEnum.PDFA_1A);								// pdfa1a
    	javax.servlet.ServletContext ctx = action.getRequest().getSession().getServletContext();
    	configuration.setIccProfilePath(ctx.getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));   // pdfa1a
    	JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();		// pdfa1a
    	jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "Arial");		// pdfa1a
    	jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");	// pdfa1a
    	jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");		// pdfa1a
    }
	
	/**
	 * calcola il digest sul contenuto testuale ed imposta la relativa keyword del documento pdf
	 * @throws IOException 
	 */
	private void addDigest(PDDocument pdf, ByteArrayOutputStream baos) throws IOException {
	    String contenuto = StringUtilities.getPdfContentAsString(baos.toByteArray());
	    uuid = StringUtilities.getSha256(contenuto);
		pdf.getDocumentInformation().setCustomMetadataValue(PortGareSystemConstants.PDF_HASH_DICTIONARY, uuid);
	}
	
	/*
	 * PDF-UA (accessibility compliance)
	 * 
     * @param pdf the pdf instance created from BAOS
     * @param title document
     * @return BAOS containing metadata (UA-identifier, title)
     */
    private static void addPdfUAInfo(PDDocument pdf, String title) throws TransformerException, IOException, BadFieldValueException {
    	// PDF UA Identifier ==> NON SERVE PER SUPERARE L'ACCESSIBILITA' CON IL TOOL "PAC" !!!

    	// document Title
    	//
    	pdf.getDocumentInformation().setTitle(title);
    	//pdf.getDocumentInformation().setAuthor("Tutorialspoint");
    	//pdf.getDocumentInformation().setTitle("Sample document"); 
    	//pdf.getDocumentInformation().setCreator("PDF Examples"); 
    	//pdf.getDocumentInformation().setSubject("Example document");
    	//pdf.getDocumentInformation().setCreationDate( Calendar );
        //pdf.getDocumentInformation().setModificationDate( Calendar ); 

        // document DisplayDocTitle = true
    	//
        pdf.getDocumentCatalog().setViewerPreferences(new PDViewerPreferences(new COSDictionary()));
        pdf.getDocumentCatalog().getViewerPreferences().setDisplayDocTitle(true);		// displayDocTitle = true
        
        // document version
    	//
        pdf.getDocumentCatalog().setVersion("1.4");										// version = 1.4
        
        // document Tagged = true
    	//
        pdf.getDocumentCatalog().setMarkInfo(new PDMarkInfo());
        pdf.getDocumentCatalog().getMarkInfo().setMarked(true);							// tagged pdf = true

        // document Language
        //
        XMPMetadata xmp = XMPMetadata.createXMPMetadata();
        xmp.createAndAddDublinCoreSchema();
   		xmp.getDublinCoreSchema().addLanguage("it");									// XMP language = it, en, de, fr, ...
   		xmp.getDublinCoreSchema().setTitle(title);										// XMP title
        //xmp.getDublinCoreSchema().setDescription(title);

        // document ???
        xmp.createAndAddPDFAExtensionSchemaWithDefaultNS();
        xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfa/ns/schema#", "pdfaSchema");
        xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfa/ns/property#", "pdfaProperty");
        xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfua/ns/id/", "pdfuaid");
        
        // document PDF-UA Identification  
        PDFAIdentificationSchema pdfuaSchema = xmp.createAndAddPDFAIdentificationSchema();
        //pdfaSchema.setPart(2);
        //pdfaSchema.setConformance("A");
        pdfuaSchema.setPart(1);
        pdfuaSchema.setConformance("U");
        
        // document PDF-UA info
        XMPSchema uaSchema = new XMPSchema(XMPMetadata.createXMPMetadata(), "pdfaSchema", "pdfaSchema", "pdfaSchema");
        uaSchema.setTextPropertyValue("schema", "PDF/UA Universal Accessibility Schema");
        uaSchema.setTextPropertyValue("namespaceURI", "http://www.aiim.org/pdfua/ns/id/");
        uaSchema.setTextPropertyValue("prefix", "pdfuaid");
        XMPSchema uaProp = new XMPSchema(XMPMetadata.createXMPMetadata(),"pdfaProperty", "pdfaProperty", "pdfaProperty");
        uaProp.setTextPropertyValue("name", "part");
        uaProp.setTextPropertyValue("valueType", "Integer");
        uaProp.setTextPropertyValue("category", "internal");
        uaProp.setTextPropertyValue("description", "descrizione");
        uaSchema.addUnqualifiedSequenceValue("property", uaProp);
        xmp.getPDFExtensionSchema().addBagValue("schemas", uaSchema);
        xmp.getPDFExtensionSchema().setPrefix("pdfuaid");
        xmp.getPDFExtensionSchema().setTextPropertyValue("part", "1");

        XmpSerializer serializer = new XmpSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializer.serialize(xmp, baos, true);

        PDMetadata metadata = new PDMetadata(pdf);
        metadata.importXMPMetadata(baos.toByteArray());
        pdf.getDocumentCatalog().setMetadata(metadata);
        
        // PDF-UA fix decorative elements as "artifacts" (line, box, rectangle, background, images, etc)
        JRPdfExporterEldasoft instance = new JRPdfExporterEldasoft(); 
		for (PDPage page : pdf.getDocumentCatalog().getPages()) {
			PdfUAGraphicsStreamEngine marker = instance.new PdfUAGraphicsStreamEngine(pdf, page);
			marker.processPage(page);
		}
    } 
    
    /**
     * converte del testo in un PDF compatibile con gli standard PDF-A, PDF-UA  
     */	
    public static byte[] textToPdf(
    		String text
    		, String titolo
    		, boolean isActiveFunctionPdfA
    		, boolean isActiveFunctionPdfUA
    		, BaseAction action
    ) throws Exception {
    	byte[] pdf = null;
    	
//    	// PDF-A
//	    if(isActiveFunctionPdfA) {
//			try {
//				ApsSystemUtils.getLogger().debug("Trasformazione contenuto in PDF-A");
//    			//InputStream iccFilePath = new FileInputStream(SpringAppContext.getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
//    			InputStream iccFilePath = new FileInputStream(action.getRequest().getSession().getServletContext().getRealPath(PortGareSystemConstants.PDF_A_ICC_PATH));
//				pdf = UtilityStringhe.string2PdfA(text, iccFilePath);
//			} catch (com.itextpdf.text.DocumentException e) {
//				DocumentException de = new DocumentException("Impossibile creare il contenuto in PDF-A.");
//				de.initCause(e);
//				throw de;
//			}
//		} else {
//			pdf = UtilityStringhe.string2Pdf(text);
//		}
//		
//		// PDF-UA
//		if(isActiveFunctionPdfUA) {
//			ApsSystemUtils.getLogger().debug("Aggiungi info per compatibilita' PDF-UA");
//			PDDocument docPdf = PDDocument.load(pdf);
//			addPdfUAInfo(docPdf, titolo);
//			ByteArrayOutputStream baosPdf = new ByteArrayOutputStream();
//			docPdf.save(baosPdf);
//			docPdf.close();
//			pdf = baosPdf.toByteArray();
//		}		

    	// genera il PDF con il report "testo.jasper"
		try {
	    	// la sessione non e' scaduta, per cui proseguo regolarmente
			//javax.servlet.ServletContext ctx = SpringAppContext.getServletContext();
			ApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IAppParamManager appParamManager = (IAppParamManager) wac.getBean(CommonSystemConstants.APP_PARAM_MANAGER);

			//BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
			javax.servlet.ServletContext ctx = action.getRequest().getSession().getServletContext();

			// se subFolderTemplate e' NULL o vuoto, si utilizza il folder standard plugins/ppgare/aps/jasper/
			//String subFolderTemplate = (String) appParamManager.getConfigurationValue(AppParamManager.SUBFOLDER_TEMPLATE_JASPER);
			String subFolderTemplate = "";
			subFolderTemplate = (StringUtils.isEmpty(subFolderTemplate) ? "" : subFolderTemplate + File.separator);
			
			String rptPath = PortGareSystemConstants.GARE_JASPER_FOLDER;
			if(StringUtils.isNotEmpty(subFolderTemplate)) 
				rptPath = rptPath + subFolderTemplate;
			
			// prepara i parametri del report... 
			HashMap<String, Object> params = new HashMap<String, Object>();
	    	params.put("titolo", titolo);
			params.put("testo", text);
			String json = "{\"DATA\": \"" + "123" + "\"}";	// <= per generare il report serve un datasource con almeno 1 riga!!

			// crea il report...
			JRAbstractTextDataSource jrds = new JsonDataSource(new ByteArrayInputStream(json.getBytes("UTF-8")));
			InputStream isJasper = ctx.getResourceAsStream(rptPath + "Testo" + PortGareSystemConstants.ESTENSIONE_JASPER_REPORT);
		    JasperPrint print = JasperFillManager.fillReport(isJasper, params, jrds);
		    
		    if(print == null) {
		    	// ERRORE
		    	ApsSystemUtils.getLogger().error("Invalid or empty datasource.");
		    } else {
		    	// genera il PDF (JasperReport 6.16.0)...
		    	JRPdfExporterEldasoft exporter = JRPdfExporterEldasoft.newInstance()
		     			.setReportName(titolo)
		     			.setPdfACompliance(isActiveFunctionPdfA)
		     			.setPdfUACompliance(isActiveFunctionPdfUA)
		     			.setPrint(print)
		     			.setAction(action);
				exporter.exportReport();
				
		     	ByteArrayOutputStream baosPdf = exporter.getOutputStream();
				pdf = baosPdf.toByteArray();
			}

		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("textToPdf", t);
			ApsSystemUtils.logThrowable(t, null, "textToPdf");
		}

		return pdf;
    }

    /**
     * converte del testo in un PDF compatibile con gli standard PDF-A, PDF-UA  
     */	
    public static byte[] textToPdf(
    		String text
    		, String titolo
    		//, HttpServletRequest request
    		, BaseAction action
    ) throws Exception {
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
				.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
						 ServletActionContext.getRequest());
    	boolean isActiveFunctionPdfA = customConfigManager.isActiveFunction("PDF", "PDF-A", false);
		boolean isActiveFunctionPdfUA = customConfigManager.isActiveFunction("PDF", "PDF-UA", false);		
		byte[] pdf = textToPdf(text, titolo, isActiveFunctionPdfA, isActiveFunctionPdfUA, action);
		return pdf;
    }


    
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    // ATTENZIONE:
    // QUESTA PARTE PUO' ESSERE RIMOSSA PERCHE' GLI ELEMENTI GRAFICI VENGONO MARCATI
    // COME "ARTIFACT" CON PDFBOX DOPO LA GENERAZIONE DEL PDF !!!
    //////////////////////////////////////////////////////////////////////////////////    
    //////////////////////////////////////////////////////////////////////////////////
    
//    /**
//	 * PDF-UA fix accessibilita' 
//	 */
//    private void disableLineWitdh(JRPen... pen) { 
//    	if(pen.length > 0) {
//    		for(int i = 0; i < pen.length; i++)
//    			pen[i].setLineWidth(new Float(0f));
//    	}
//    }
//
//    /**
//	 * PDF-UA fix accessibilita' 
//	 */
//    private void fixPdfUA(Object element) {
//    	if(pdfUACompliance) {
//    		boolean disableBackground = false;
//    		
//	    	if(element instanceof JRPrintLine) {
//	    		disableLineWitdh(((JRPrintLine) element).getLinePen());
//	    		
//	    	} else if(element instanceof JRLineBox) {
//	    		JRLineBox box = (JRLineBox) element;
//	    		disableLineWitdh(box.getLeftPen(), box.getTopPen(), box.getRightPen(), box.getBottomPen());
//				
//	    	} else if(element instanceof JRPrintRectangle) {
//	    		disableLineWitdh(((JRPrintRectangle)element).getLinePen());
//	    		disableBackground = true;
//	    		
//    		} else if(element instanceof JRPrintEllipse) {
//    			disableLineWitdh(((JRPrintEllipse)element).getLinePen());
//    			disableBackground = true;
//	    		
//    		} else if(element instanceof JRPrintFrame) {
//    			disableBackground = true;
//    		
//    		} else if(element instanceof JRPrintText) {	// text + label
//    			disableBackground = true;
//    		
//    		} else if(element instanceof JRPrintImage) {
//    			JRPrintImage image = ((JRPrintImage)element);
//    			disableLineWitdh(image.getLinePen()); 
//    			disableBackground = true;
//    		}
//
//	    	if(disableBackground) {
//	    		JRTemplatePrintElement tpe = (JRTemplatePrintElement) element;
//	    	    //tpe.getTemplate().setBackcolor(Color.red);		// <=== DEBUG ONLY
//	    		tpe.getTemplate().setMode(ModeEnum.TRANSPARENT);	// disabilita il background per compatibilita' con PDF UA
//	    	}
//    	}
//    }
//
//	/**
//	 * PDF-UA fix accessibilita'
//	 */
//    @Override
//	protected void exportLine(JRPrintLine line)
//	{
//    	fixPdfUA(line);
//    	super.exportLine(line);
//	}
//    
//	/**
//	 * PDF-UA fix accessibilita'
//	 */
//    @Override
//	protected void exportBox(JRLineBox box, JRPrintElement element)
//	{
//    	fixPdfUA(box);
//		super.exportBox(box, element);
//	}
//    
//    /**
//	 * PDF-UA fix accessibilita'
//	 */
//    @Override
//	protected void exportRectangle(JRPrintRectangle rectangle)
//	{
//    	fixPdfUA(rectangle);
//		super.exportRectangle(rectangle);
//	}
//    
//    /**
//	 * PDF-UA fix accessibilita'
//	 */
//    @Override
//    protected void exportEllipse(JRPrintEllipse ellipse)
//    {
//    	fixPdfUA(ellipse);
//    	super.exportEllipse(ellipse);
//    }
//
//    /**
//	 * PDF-UA fix accessibilita' 
//	 */
//    @Override
//    public void exportFrame(JRPrintFrame frame) throws DocumentException, IOException, JRException
//    {
//    	fixPdfUA(frame);
//		super.exportFrame(frame);
//    }
//    
//	/**
//	 * PDF-UA fix accessibilita' 
//	 */
//    @Override
//	public void exportFieldText(JRPrintText text, PdfFieldTypeEnum fieldType) throws DocumentException
//	{
//    	fixPdfUA(text);
//		super.exportFieldText(text, fieldType);
//	}
//    
//    /**
//     * PDF-UA fix accessibilita'
//     */
//    @Override
//    public void exportText(JRPrintText text) throws DocumentException 
//    {
//    	fixPdfUA(text);
//    	super.exportText(text);
//    }
//
//    /**
//     * PDF-UA fix accessibilita'
//     */
//    @Override
//    public void exportImage(JRPrintImage printImage) throws DocumentException, IOException, JRException
//    {
//    	fixPdfUA(printImage);
//    	super.exportImage(printImage);
//    }

}

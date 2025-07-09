package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.security.PGPEncryptionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.openpgp.PGPException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

public class DocumentiComunicazioneHelper extends DocumentiAllegatiHelper 
	implements HttpSessionBindingListener, Serializable 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8450365797159239515L;

	// file generati contenenti il testo della comunicazione
	private List<File> fileGenerati = null;

	/**
	 * costruttore  
	 */
	public DocumentiComunicazioneHelper(
		boolean creaDocRichiesti
		, boolean creaDocUlteriori
	) {	
		super(null, null, null, creaDocRichiesti, creaDocUlteriori);
		fileGenerati = new ArrayList<File>();
	}

	public DocumentiComunicazioneHelper() {
		this(false, true);
	}
	
	public List<File> getFileGenerati() {
		return fileGenerati;
	}

	public void setFileGenerati(List<File> fileGenerati) {
		this.fileGenerati = fileGenerati;
	}

	public byte[] getChiaveSessione() {
		return chiaveSessione;
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
	}
	
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		super.valueUnbound(event);
		
		Iterator<File> iter = this.fileGenerati.listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * verifica ed inizializza la chiave di sessione   
	 * @throws GeneralSecurityException 
	 * @throws UnsupportedEncodingException 
	 * @throws ApsException 
	 */
	public void refreshChiaveSessione(
			String username
			, String codiceGara
			, String codiceLotto
			, int tipoBusta
	) throws ApsException, UnsupportedEncodingException, GeneralSecurityException {
		this.username = username;
		
		String tipoComunicazione = null;
		if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) 
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA;
		if(tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA)
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;
		
		if(tipoComunicazione != null) {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
			
			String sessionKey = null;
			
			// NB: verifica se esiste una chiave pubblica/di cifratura per la busta in CHIAVIBUSTE  
			if(chiaveCifratura == null) {
				chiaveCifratura = bandiManager.getChiavePubblica(codiceGara, tipoComunicazione);
			}
			
			// verifica se e' attiva la cifratura della busta
			if (chiaveCifratura != null) {
				byte[] chiaveSessione = null; 
				chiaveSessione = EncryptionUtils.decodeSessionKey(sessionKey, username);
				this.chiaveSessione = chiaveSessione;
			}
		}
	}
	
	/**
	 * calcola la session key per l'invio della comunicazione
	 * @throws ApsException 
	 * @throws GeneralSecurityException 
	 * @throws PGPException 
	 * @throws IOException 
	 */
	public String getEncriptedSessionKey(
			String codiceGara
			, String codiceLotto
			, int tipoBusta
	) throws ApsException, GeneralSecurityException, IOException, PGPException {
		String comkeySess = null;
		
		String tipoComunicazione = null;
		if(tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) 
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA;
		if(tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA)
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;

		if(tipoComunicazione == null) {
			ApsSystemUtils.getLogger().error("DocumentiComunicazioneHelper.getEncriptedSessionKey() tipo busta non valido " + tipoBusta);
			throw new ApsException("DocumentiComunicazioneHelper.getEncriptedSessionKey() tipo busta non valido " + tipoBusta);
			
		} else if(chiaveSessione != null && chiaveCifratura != null) {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IBandiManager bandiManager = (IBandiManager) ctx.getBean(PortGareSystemConstants.BANDI_MANAGER);
	
			// recupera dalla tabella CHIAVIBUSTE la chiave pubblica/di cifratura associata alla busta
			byte[] chiavePubblica = chiaveCifratura; 
//			if(chiaveCifratura != null) {	QUESTO NON SERVE !!!
//				byte[] chiavePubblica = bandiManager.getChiavePubblica(
//						codiceGara, 
//						tipoComunicazione
//				);
//			}
			
			if (chiavePubblica != null) {
				byte[] sessionKey = EncryptionUtils.decodeSessionKey(
						EncryptionUtils.encodeSessionKey(chiaveSessione, username)
						, username
				);
				comkeySess = Base64.encodeBase64String(PGPEncryptionUtils.encrypt(new ByteArrayInputStream(chiavePubblica), sessionKey));
			}
		}
		
		return comkeySess;
	}
	
}
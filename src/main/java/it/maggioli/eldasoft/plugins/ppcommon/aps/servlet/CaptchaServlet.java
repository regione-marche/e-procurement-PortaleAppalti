package it.maggioli.eldasoft.plugins.ppcommon.aps.servlet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.noise.CurvedLineNoiseProducer;

import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;

public class CaptchaServlet extends HttpServlet {
		
	/**
	 * UID
	 */
	private static final long serialVersionUID = 600194473639720650L;
	
	public static final String SESSION_ID_CAPTCHA = "CAPTCHA_INSTANCE";
	private static final String IMAGETYPE = "jpeg";
	
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		Logger log = ApsSystemUtils.getLogger();
		if (log.isTraceEnabled()) {
			log.trace("CaptchaServlet: BEGIN");
		}
		
		byte[] captchaBytes = null;
        try {
        	BufferedImage challenge = createChallengeImage(request);

	        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
	        ImageIO.write(challenge, IMAGETYPE, imgOutputStream);
	        captchaBytes = imgOutputStream.toByteArray();
	        
        } catch (IllegalArgumentException ex) {
        	captchaBytes = null;
        	if (log.isTraceEnabled()) {
        		log.trace("CaptchaServlet: ERR", ex);
			}
        } catch (IOException ex) {
        	captchaBytes = null;
        	if (log.isTraceEnabled()) {
        		log.trace("CaptchaServlet: ERR", ex);
			}
		} catch (Throwable t) {
			captchaBytes = null;
        	if (log.isTraceEnabled()) {
        		log.trace("CaptchaServlet: ERR", t);
			}
		}

        if(captchaBytes != null) {
	        try {
		        // send captcha image to client...
	        	response.setContentType("image/" + IMAGETYPE);
	        	response.setHeader("Content-Disposition","inline; filename=" + "captcha.jpeg" );
		        ServletOutputStream out = response.getOutputStream();
		        out.write(captchaBytes);
		        out.flush();
		        out.close();
		        
		        if (log.isTraceEnabled()) {
					log.trace("CaptchaServlet: OK");
				}
		    } catch(Exception ex) {
		    	if (log.isTraceEnabled()) {
					log.trace("CaptchaServlet: ERR", ex);
				}
		    }
        } 
        
		if (log.isTraceEnabled()) {
			log.trace("CaptchaServlet: END");
		}
	}

	private Captcha.Builder createCaptchaBuilder() {
		return new Captcha.Builder(200, 50)
			.addBackground(new GradiatedBackgroundProducer())
			.addNoise(new CurvedLineNoiseProducer(new Color(0x303030), 3))
			.addNoise(new CurvedLineNoiseProducer(new Color(0x303030), 2))
			.addText()
			.gimp()
			.gimp(new DropShadowGimpyRenderer())
			.addBorder();
	}
	
	/**
	 * crea l'immagine del codice di sicurezza
	 */
	private BufferedImage createChallengeImage(javax.servlet.http.HttpServletRequest request) {
		BufferedImage challenge = null; 
		try {
	    	Captcha.Builder captcha = createCaptchaBuilder();
	    	Captcha instance = captcha.build();
	    	challenge = captcha.build().getImage();
	    	
	    	request.getSession().setAttribute(SESSION_ID_CAPTCHA, instance);
		} catch(Exception ex) {
			challenge = null;
		}
		return challenge;
	}

}


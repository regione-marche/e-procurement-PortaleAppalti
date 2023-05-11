package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.www.sil.WSGareAppalto.VendorRatingType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;

import java.util.Calendar;
import java.util.Date;


public class OpenPageVendorRatingAction extends AbstractOpenPageAction  {
    private static final long serialVersionUID = 1L;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void setBandiManager(IBandiManager bandiManager) {
        this.bandiManager = bandiManager;
    }

    private IBandiManager bandiManager;

    private VendorRatingType vendorRatingCorrente;
    private VendorRatingType vendorRatingFuturo;

    public VendorRatingType getVendorRatingCorrente() {
        return vendorRatingCorrente;
    }

    public void setVendorRatingCorrente(VendorRatingType vendorRatingCorrente) {
        this.vendorRatingCorrente = vendorRatingCorrente;
    }

    public VendorRatingType getVendorRatingFuturo() {
        return vendorRatingFuturo;
    }

    public void setVendorRatingFututro(VendorRatingType vendorRatingFuturo) {
        this.vendorRatingFuturo = vendorRatingFuturo;
    }


    @Override
    public String openPage() {
        String target = SUCCESS;
        try {
           setVendorRatingCorrente(bandiManager.getVendorRating(getCurrentUser().getUsername(), new Date()));
           Date dataFinePeriodoAttuale = this.vendorRatingCorrente.getDataFineValidita();
           // definito il trimestre corrente, si verifica se esiste un trimestre successivo a partire dalla data fine del trimestre corrente
           if (dataFinePeriodoAttuale != null) {
               Calendar cal = Calendar.getInstance();
               cal.setTime(dataFinePeriodoAttuale);
               cal.add(Calendar.DATE, 1);
               setVendorRatingFututro(bandiManager.getVendorRating(getCurrentUser().getUsername(), cal.getTime()));        	   
           }
        } catch (Throwable t) {
            ApsSystemUtils.logThrowable(t, this, "openPage");
            ExceptionUtils.manageExceptionError(t, this);
            target = CommonSystemConstants.PORTAL_ERROR;
        }

        return target;
    }
}

package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import java.util.Map;

public interface PagoPaService {
	public String getIuv(PagoPaPagamentoModel model, Map<Integer, String> map) throws Exception;
	public String invioBozzaPagamento() throws Exception;
	public String confermaPagamento(String urlCancel, String urlKo, String urlOk, String urlS2S, String iuv) throws Exception;
	public String getRicevutaByIuv(String iuv)  throws Exception;
}

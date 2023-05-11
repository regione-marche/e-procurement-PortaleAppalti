package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.services.AbstractDAO;

/**
 * Implementazione del DAO di monitoraggio.
 * 
 * @author Eleonora.Favaro
 */
public class MonitoraggioDAO extends AbstractDAO implements IMonitoraggioDAO {

	/** SQL di base per estrazione utenti non attivati sia nella ricerca che per il dettaglio. */
	private final String LOAD_OPERATORI_ECONOMICI_KO ="select a.username, ps.textvalue as mail, a.registrationdate "+
	  "from authusers a inner join jpuserprofile_authuserprofiles ap on a.username = ap.username inner join jpuserprofile_profilesearch ps on a.username = ps.username " +
	  "where a.active=0 and a.delegateuser is null " +
	  "and a.lastpasswordchange is null " +
	  "and ap.profiletype = 'PFI' " +
	  "and ps.attrname = 'email'";
	
	/** Filtro di ricerca per username. */
	private final String USERNAME_FILTER_LIKE = "and upper(a.username) like ?";
	/** Filtro per username esatto. */
	private final String USERNAME_FILTER = "and a.username = ?";
	/** Filtro di ricerca per email. */
	private final String EMAIL_FILTER = "and upper(ps.textvalue) like ?";
	/** Filtro di ricerca per data riferimento superiore a . */
	private final String REGISTRAZIONE_DA_FILTER = "and a.registrationdate >= ?";
	/** Filtro di ricerca per data riferimento inferiore a . */
	private final String REGISTRAZIONE_A_FILTER = "and a.registrationdate <= ?";
	
	@Override
	public List<OperatoreEconomicoKO> searchOperatoriEconomiciKO(String utente,
			String email, Date dataRegistrazioneDa, Date dataRegistrazioneA) {
		Connection conn = null;
		List<OperatoreEconomicoKO> operatoriKO = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			
			// definizione query
			StringBuilder s = new StringBuilder(LOAD_OPERATORI_ECONOMICI_KO);		
			if (StringUtils.isNotEmpty(utente)) {
				s.append(USERNAME_FILTER_LIKE);
			}
			if (StringUtils.isNotEmpty(email)) {
				s.append(EMAIL_FILTER);
			}
			if (dataRegistrazioneDa != null) {
				s.append(REGISTRAZIONE_DA_FILTER);
			}
			if (dataRegistrazioneA != null) {
				s.append(REGISTRAZIONE_A_FILTER);
			}
			s.append(" order by a.registrationdate desc");
			
			// impostazione parametri
			stat = conn.prepareStatement(s.toString());
			int i = 1;
			if(StringUtils.isNotEmpty(utente)){
				stat.setString(i++, "%" + StringUtils.upperCase(utente) + "%");
			}
			if(StringUtils.isNotEmpty(email)){
				stat.setString(i++, "%" + StringUtils.upperCase(email) + "%");
			}
			if(dataRegistrazioneDa != null){
				stat.setDate(i++, new java.sql.Date(dataRegistrazioneDa.getTime()));
			}
			if(dataRegistrazioneA != null){
				stat.setDate(i++, new java.sql.Date(dataRegistrazioneA.getTime()));
			}

			// esecuzione ed estrazione dei dati
			res = stat.executeQuery();
			operatoriKO = new ArrayList<OperatoreEconomicoKO>();
			OperatoreEconomicoKO operatoreEconomico = null;
			while (res.next()) {
				String userName = res.getString(1);
				String mail = res.getString(2);
				Date dataRegistrazione = res.getDate(3);
				operatoreEconomico = new OperatoreEconomicoKO();
				operatoreEconomico.setUtente(userName);
				operatoreEconomico.setEmail(mail);
				operatoreEconomico.setDataRegistrazione(dataRegistrazione);	
				operatoriKO.add(operatoreEconomico);
			}

		} catch (Throwable t) {
			processDaoException(t, "Errore nella ricerca di operatori economici non attivi", "searchOperatoriEconomiciKO");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		return operatoriKO;
	}

	@Override
	public String getEmailOperatore(String utenteSelezionato) {
		Connection conn = null;
		String email = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		try {
			conn = this.getConnection();
			// definizione query
			StringBuilder s = new StringBuilder(LOAD_OPERATORI_ECONOMICI_KO);		
			s.append(USERNAME_FILTER);
			stat = conn.prepareStatement(s.toString());
			// impostazione parametri
			stat.setString(1, utenteSelezionato);
			// esecuzione ed estrazione dei dati
			res = stat.executeQuery();
			if (res.next()) {
				email = res.getString(2);
			}
		} catch (Throwable t) {
			processDaoException(t, "Errore nella ricerca della mail per operatore economico non attivo", "getEmailOperatore");
		} finally {
			closeDaoResources(res, stat, conn);
		}
		
		return email;
	}
}

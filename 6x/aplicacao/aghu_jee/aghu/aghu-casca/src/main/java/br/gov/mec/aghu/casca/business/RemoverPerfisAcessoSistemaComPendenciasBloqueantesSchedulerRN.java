package br.gov.mec.aghu.casca.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RemoverPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN extends BaseBusiness {

	@EJB
	private PerfilUsuarioON perfilUsuarioON;
	
	private static final Log LOG = LogFactory.getLog(RemoverPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	private static final long serialVersionUID = 6422847922636001025L;
	
	public void removerPerfisAcessoSistemaComPendenciasBloqueantes(Date expiration, String cron) {
		LOG.info("Rotina RemoverPerfisAcessoSistemaComPendenciasBloqueantesScheduler.removerPerfisAcessoSistemaComPendenciasBloqueantes iniciada em: "
							+ Calendar.getInstance().getTime());
		this.getPerfilUsuarioON().removerPerfisNaoBloqueantes();

		LOG.info("Rotina RemoverPerfisAcessoSistemaComPendenciasBloqueantesScheduler.removerPerfisAcessoSistemaComPendenciasBloqueantes finalizada em: "
							+ Calendar.getInstance().getTime());
	}
	
	protected PerfilUsuarioON getPerfilUsuarioON() {
		return perfilUsuarioON;
	}

}

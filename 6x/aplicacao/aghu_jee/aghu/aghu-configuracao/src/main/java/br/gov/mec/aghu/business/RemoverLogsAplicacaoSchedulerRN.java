package br.gov.mec.aghu.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RemoverLogsAplicacaoSchedulerRN extends BaseBusiness {

	@EJB
	private AghLogAplicacaoON aghLogAplicacaoON;
	
	private static final Log LOG = LogFactory.getLog(RemoverLogsAplicacaoSchedulerRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	private static final long serialVersionUID = -4822626252503152291L;

	public void removerLogsAplicacao() {
		LOG.info("Rotina RemoverLogsAplicacaoSchedulerRN.removerLogsAplicacao iniciada em: " + Calendar.getInstance().getTime());
		this.getAghLogAplicacaoON().removerLogsAplicacao();
		LOG.info("Rotina RemoverLogsAplicacaoSchedulerRN.removerLogsAplicacao finalizada em: " + Calendar.getInstance().getTime());
	}

	protected AghLogAplicacaoON getAghLogAplicacaoON() {
		return aghLogAplicacaoON;
	}

}

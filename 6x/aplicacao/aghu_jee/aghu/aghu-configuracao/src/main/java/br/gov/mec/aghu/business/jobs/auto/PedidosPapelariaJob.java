package br.gov.mec.aghu.business.jobs.auto;

import java.util.Calendar;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class PedidosPapelariaJob extends AghuJob {

	private IComprasFacade comprasFacade = ServiceLocator.getBean(IComprasFacade.class, "aghu-compras");
	
	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext)
			throws ApplicationBusinessException {
		comprasFacade.processarPedidosPapelaria(Calendar.getInstance().getTime(), getCron(jobExecutionContext), getServidorAgendador(jobExecutionContext));
	}
}

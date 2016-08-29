package br.gov.mec.aghu.business.scheduler.auto;

import java.util.Date;

import org.quartz.Trigger;

import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.business.jobs.auto.SolicitacaoCompraAlmoxarifadoJob;
import br.gov.mec.aghu.business.scheduler.AghuScheduler;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class SolicitacaoCompraAlmoxarifadoScheduler extends AghuScheduler {
	
	
	/**
	 * Agenda geração automática de SC por almoxarifado.
	 * 
	 * @param nomeProcesso Nome do processo.
	 * @param cron Cron.
	 * @param almoxarifados Almoxarifados.
	 * @return Processo agendado.
	 * @throws ApplicationBusinessException 
	 */
	public Trigger agendar(Date expiration, String cron, final String nomeProcessoQuartz,
			final String descricao, final Boolean executar, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		getParametros().put(SolicitacaoCompraAlmoxarifadoJob.EXECUTAR, executar);
		getParametros().put(SolicitacaoCompraAlmoxarifadoJob.DESCRICAO, descricao);
		
		return agendar(expiration, cron, nomeProcessoQuartz, servidorLogado);
	}

	@Override
	protected Class<? extends AghuJob> getClasseJob() {
		return SolicitacaoCompraAlmoxarifadoJob.class;
	}

}

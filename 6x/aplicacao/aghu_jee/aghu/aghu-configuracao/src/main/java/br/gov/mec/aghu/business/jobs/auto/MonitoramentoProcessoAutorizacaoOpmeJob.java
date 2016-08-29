package br.gov.mec.aghu.business.jobs.auto;

import java.util.List;

import org.quartz.JobExecutionContext;

import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.business.jobs.AghuJob;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class MonitoramentoProcessoAutorizacaoOpmeJob extends AghuJob {

	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade = ServiceLocator.getBean(IBlocoCirurgicoOpmesFacade.class, "aghu-blococirurgico");

	@Override
	protected void doExecutarProcessoNegocio(AghJobDetail job, JobExecutionContext jobExecutionContext) throws ApplicationBusinessException {
		// String nomeProcessoQuartz = getNomeProcessoQuartz(jobExecutionContext);
		RapServidores servidor = this.getServidorAgendador(jobExecutionContext);
		try {
			avaliaOpmes(servidor);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}
	}

	private void avaliaOpmes(RapServidores servidorLogado) throws ApplicationBusinessException, BaseException {
		for (Integer seq : buscaDadosProcessoAutorizacaoOpme()) {
			iniciaProcessoOPME(servidorLogado, seq.shortValue());
		}
	}

	private void iniciaProcessoOPME(RapServidores servidorLogado, Short seq) throws BaseException {
		this.blocoCirurgicoOpmesFacade.iniciarFluxoAutorizacaoOPMEs(servidorLogado, obterRequisicaoOpme(seq));
	}

	private MbcRequisicaoOpmes obterRequisicaoOpme(Short seq) {
		return this.blocoCirurgicoOpmesFacade.obterRequisicaoOpme(seq);
	}

	private List<Integer> buscaDadosProcessoAutorizacaoOpme() throws ApplicationBusinessException {
		return this.blocoCirurgicoOpmesFacade.buscaDadosProcessoAutorizacaoOpme();
	}

}

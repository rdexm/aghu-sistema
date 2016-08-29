package br.gov.mec.aghu.blococirurgico.workflow.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioAcaoHistoricoWF;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFRota;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AghWFTemplateFluxo;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;


@Modulo(ModuloEnum.BLOCO_CIRURGICO)
@Stateless
public class WorkflowFacade extends BaseFacade implements IWorkflowFacade {

	@EJB
	private WorkflowRN workflowRN;
	
	private static final long serialVersionUID = 6500220314955169328L;

	
	private WorkflowRN getWorkflowRN() {
		return workflowRN;
	}
	
	@Override
	public AghWFTemplateFluxo recuperarTemplate(String modulo) {		
		return getWorkflowRN().getTemplateFluxo(modulo);
	}

	@Override
	public AghWFFluxo criarFluxo(AghWFTemplateFluxo templateFluxo,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		return getWorkflowRN().criarFluxo(templateFluxo, requisicao);
	}

	@Override
	public AghWFExecutor adicionarExecutor(AghWFExecutor executor, MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		return getWorkflowRN().adicionarExecutor(executor,requisicao);
	}

	@Override
	public void executarEtapa(AghWFExecutor executor, AghWFEtapa etapa, String observacao, Boolean salvaHistorico, MbcRequisicaoOpmes requisicao) throws BaseException {
		 getWorkflowRN().executarEtapa(executor, etapa, observacao , salvaHistorico,requisicao);
	}

	@Override
	public void rejeitarEtapa(AghWFEtapa etapa, AghWFExecutor executor, String justificativa, MbcRequisicaoOpmes requisicao) throws BaseException {
		getWorkflowRN().rejeitarEtapa(etapa, executor, justificativa, requisicao);
	}

	@Override
	public void cancelarFluxo(AghWFExecutor executor, AghWFFluxo fluxo, String justificativa) throws ApplicationBusinessException {
		getWorkflowRN().cancelarFluxo(executor,fluxo, justificativa);
	}
	
	@Override
	public void adicionarPendenciaAutorizacao(AghWFEtapa etapa, MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor, AghWFEtapa etapaDestino) throws ApplicationBusinessException {
		getWorkflowRN().adicionarPendenciaAutorizacao(etapa, requisicao, templateEtapaDestino, executor, etapaDestino);
	}

	@Override
	public List<AghWFRota> recuperarRotas(AghWFEtapa etapaOrigem) {
		return getWorkflowRN().getRotas(etapaOrigem);
	}

	@Override
	public AghWFEtapa adicionarEtapa(AghWFEtapa etapaAnterior, AghWFFluxo fluxo, AghWFTemplateEtapa templateEtapa, AghWFExecutor executor) {
		return getWorkflowRN().adicionarEtapa(etapaAnterior, fluxo, templateEtapa, executor);
	}

	@Override
	public AghWFExecutor obterExecutorPorRapServidor(RapServidores servidor, Integer seqEtapa) {
		return getWorkflowRN().getExecutorPorRapServidor(servidor, seqEtapa);
	}

	@Override
	public void adicionarExecutores(List<AghWFExecutor> executores,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		getWorkflowRN().adicionarExecutores(executores,requisicao);
	}

	@Override
	public AghWFEtapa adicionarEtapaExecutores(AghWFEtapa etapaAnterior, AghWFFluxo fluxo, AghWFTemplateEtapa templateEtapa, AghWFExecutor executor, MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		return getWorkflowRN().adicionarEtapaExecutores(etapaAnterior, fluxo, templateEtapa, executor, requisicao);
	}
	
	@Override
	public List<AghWFExecutor> obterExecutoresPorFluxo(RapServidores servidor, Integer seqFluxo) {
		return getWorkflowRN().getExecutoresPorFluxo(servidor, seqFluxo);
	}

	@Override
	public void concluirFluxo(AghWFExecutor executor, AghWFFluxo fluxo,	String observacao) {
		getWorkflowRN().concluirFluxo(executor, fluxo, observacao);
		
	}

	@Override
	public AghWFTemplateEtapa obterUltimaEtapaFluxo(AghWFFluxo fluxo) {
		return getWorkflowRN().obterUltimaEtapaFluxo(fluxo);
	}

	@Override
	public void salvarHistoricoAcao(AghWFFluxo fluxo, AghWFEtapa etapa,
			AghWFExecutor executor, Date dataRegistro, String justificativa,
			String observacao, DominioAcaoHistoricoWF acao) {
		getWorkflowRN().salvarHistorico(fluxo, etapa, executor, dataRegistro, justificativa, observacao, acao);
		
	}
	
	
	
	@Override
	public List<AghWFExecutor> obterExecutoresEnvolvidosNoProcessoDeAutorizacao(Integer seqFluxo) {
		return getWorkflowRN().getExecutoresEnvolvidosNoProcessoDeAutorizacao(seqFluxo);
	}

	@Override
	public void executarEtapaCancelamentoFluxo(AghWFExecutor executor, AghWFEtapa etapa, String justificativa, MbcRequisicaoOpmes requisicao) throws BaseException {
		getWorkflowRN().setExecutarEtapaCancelamentoFluxo(executor, etapa, justificativa, Boolean.TRUE, requisicao);
	}
	
}

package br.gov.mec.aghu.blococirurgico.workflow.business;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioAcaoHistoricoWF;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFRota;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AghWFTemplateFluxo;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;

import java.io.Serializable;

import br.gov.mec.aghu.core.exception.BaseException;
/**
 * Interface genérica para workflows.
 * @author paulo
 *
 */
public interface IWorkflowFacade extends Serializable {
	
	AghWFTemplateFluxo recuperarTemplate(String modulo);
	AghWFEtapa adicionarEtapa(AghWFEtapa etapaAnterior, AghWFFluxo fluxo, AghWFTemplateEtapa templateEtapa, AghWFExecutor executor);
	List<AghWFRota> recuperarRotas(AghWFEtapa etapaOrigem);	
	void cancelarFluxo(AghWFExecutor executor, AghWFFluxo fluxo, String justificativa) throws ApplicationBusinessException;
	AghWFExecutor obterExecutorPorRapServidor(RapServidores servidor, Integer seqEtapa);	
	List<AghWFExecutor> obterExecutoresPorFluxo(RapServidores servidor, Integer seqFluxo);
	void concluirFluxo(AghWFExecutor executor, AghWFFluxo fluxo, String observacao);
	AghWFTemplateEtapa obterUltimaEtapaFluxo(AghWFFluxo fluxo);
	List<AghWFExecutor> obterExecutoresEnvolvidosNoProcessoDeAutorizacao(Integer seqFluxo);
	void salvarHistoricoAcao(AghWFFluxo fluxo, AghWFEtapa etapa, AghWFExecutor executor, Date dataRegistro, String justificativa, String observacao,	DominioAcaoHistoricoWF acao);
	void executarEtapa(AghWFExecutor executor, AghWFEtapa etapa, String observacao, Boolean salvaHistorico, MbcRequisicaoOpmes requisicao) throws BaseException;
	void adicionarPendenciaAutorizacao(AghWFEtapa etapa,MbcRequisicaoOpmes requisicao,AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor,AghWFEtapa etapaDestino) throws ApplicationBusinessException;
	AghWFExecutor adicionarExecutor(AghWFExecutor executor,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException;
	void adicionarExecutores(List<AghWFExecutor> executores,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException;
	AghWFEtapa adicionarEtapaExecutores(AghWFEtapa etapaAnterior,AghWFFluxo fluxo, AghWFTemplateEtapa templateEtapa,AghWFExecutor executor, MbcRequisicaoOpmes requisicao)throws ApplicationBusinessException;
	void rejeitarEtapa(AghWFEtapa etapa, AghWFExecutor executor,String justificativa, MbcRequisicaoOpmes requisicao)throws BaseException;
	AghWFFluxo criarFluxo(AghWFTemplateFluxo templateFluxo,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException;
	void executarEtapaCancelamentoFluxo(AghWFExecutor executor,AghWFEtapa etapa, String justificativa,MbcRequisicaoOpmes requisicao) throws BaseException;
	
}

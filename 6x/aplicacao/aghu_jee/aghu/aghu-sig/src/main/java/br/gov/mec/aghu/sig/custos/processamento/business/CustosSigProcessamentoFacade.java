package br.gov.mec.aghu.sig.custos.processamento.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioSigTipoAlertaDetalhado;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigPassos;
import br.gov.mec.aghu.model.SigProcessamentoAnalises;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCustoLog;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.business.AlertaProcessamentoON;
import br.gov.mec.aghu.sig.custos.business.CalculoMovimentosObjetoCentroCustoON;
import br.gov.mec.aghu.sig.custos.business.IntegracaoCentralPendenciasON;
import br.gov.mec.aghu.sig.custos.business.ManterProcessamentoMensalON;
import br.gov.mec.aghu.sig.custos.vo.ProcessamentoCustoFinalizadoVO;
import br.gov.mec.aghu.sig.custos.vo.SomatorioAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseOtimizacaoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.sig.dao.SigPassosDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAlertasDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoAnalisesDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoLogDAO;
import br.gov.mec.aghu.sig.dao.SigProcessamentoPassosDAO;


@Modulo(ModuloEnum.SIG_CUSTOS_ATIVIDADE)
@Stateless
public class CustosSigProcessamentoFacade extends BaseFacade implements Serializable, ICustosSigProcessamentoFacade {


@EJB
private AlertaProcessamentoON alertaProcessamentoON;

@EJB
private ManterProcessamentoMensalON manterProcessamentoMensalON;

@Inject
private SigMvtoContaMensalDAO sigMvtoContaMensalDAO;

@Inject
private SigPassosDAO sigPassosDAO;

@Inject
private SigProcessamentoAnalisesDAO sigProcessamentoAnalisesDAO;

@Inject
private SigProcessamentoCustoLogDAO sigProcessamentoCustoLogDAO;

@Inject
private SigProcessamentoPassosDAO sigProcessamentoPassosDAO;

@Inject
private SigProcessamentoAlertasDAO sigProcessamentoAlertasDAO;

@Inject
private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;

@EJB
private OrquestracaoProcessamentoDiarioMensal orquestracaoProcessamentoDiarioMensalRN;

@EJB
private IntegracaoCentralPendenciasON integracaoCentralPendenciasON;

@EJB
private CalculoMovimentosObjetoCentroCustoON calculoMovimentosObjetoCentroCustoON;

	private static final long serialVersionUID = -1383065205219333807L;

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<ProcessamentoCustoFinalizadoVO> executarProcessamentoCustoAutomatizado() throws ApplicationBusinessException{
		return this.orquestracaoProcessamentoDiarioMensalRN.executarProcessamentoCustoAutomatizado();
	}
	
	public void adicionarPendenciaProcessamentoFinalizado(ProcessamentoCustoFinalizadoVO vo)throws ApplicationBusinessException{
		this.integracaoCentralPendenciasON.adicionarPendenciaProcessamentoFinalizado(vo);
	}
	
	public String calcularTempoExecucao(SigProcessamentoCusto processamento, SigProcessamentoPassos passo){
		return this.sigProcessamentoCustoLogDAO.calcularTempoExecucao(processamento, passo);
	}

	@Override
	public List<SigProcessamentoCustoLog> pesquisarHistoricoProcessamentoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo) {
		return this.getSigProcessamentoCustoLogDAO().pesquisarHistoricoProcessamentoCusto(firstResult, maxResult, orderProperty, asc, processamentoCusto,
				etapa, passo);
	}

	@Override
	public Long pesquisarHistoricoProcessamentoCustoCount(SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo) {
		return this.getSigProcessamentoCustoLogDAO().pesquisarHistoricoProcessamentoCustoCount(processamentoCusto, etapa, passo);
	}

	@Override
	public List<SigProcessamentoCusto> pesquisarProcessamentoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao) {
		return this.getSigProcessamentoCustoDAO().pesquisarProcessamentoCusto(firstResult, maxResult, orderProperty, asc, competencia, situacao);
	}

	@Override
	public Long pesquisarProcessamentoCustoCount(SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao) {
		return this.getSigProcessamentoCustoDAO().pesquisarProcessamentoCustoCount(competencia, situacao);
	}

	@Override
	public List<SigProcessamentoCusto> pesquisarCompetencia(DominioSituacaoProcessamentoCusto... situacao) {
		return this.getSigProcessamentoCustoDAO().pesquisarCompetencia(situacao);
	}

	@Override
	public List<SigProcessamentoCusto> pesquisarCompetenciaSemProducao(SigObjetoCustoVersoes objetoCustoVersao, SigDirecionadores direcionador) {
		return this.getSigProcessamentoCustoDAO().pesquisarCompetenciaSemProducao(objetoCustoVersao, direcionador);
	}

	@Override
	public List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoObjetoCustos(final Integer firstResult, final Integer maxResult,
			Integer seqCompetencia,  Integer codigoCentroCusto, String nomeProdutoServico, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.getSigProcessamentoCustoDAO().buscarCustosVisaoObjetoCustos(firstResult, maxResult, seqCompetencia, codigoCentroCusto, nomeProdutoServico,sigCentroProducao, tiposCentroProducao);
	}
	
	@Override
	public SomatorioAnaliseCustosObjetosCustoVO obterSomatorioVisualizarObjetoCusto(Integer seqCompetencia,  Integer codigoCentroCusto, String nomeProdutoServico,
			SigCentroProducao sigCentroProducao, Integer seqObjetoCustoVersao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.getSigProcessamentoCustoDAO().obterSomatorioVisualizarObjetoCusto(seqCompetencia, codigoCentroCusto, nomeProdutoServico, sigCentroProducao, seqObjetoCustoVersao, tiposCentroProducao);
	}

	@Override
	public SomatorioAnaliseCustosObjetosCustoVO obterSomatorioVisualizarCentroCusto(Integer seqCompetencia, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao,  DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.getSigProcessamentoCustoDAO().obterSomatorioVisualizarCentroCusto(seqCompetencia, fccCentroCustos, sigCentroProducao, tiposCentroProducao);
	}

	@Override
	public Long buscarCustosVisaoObjetoCustosCount(Integer seqCompetencia,  Integer codigoCentroCusto, String nomeProdutoServico,
			SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.getSigProcessamentoCustoDAO().buscarCustosVisaoObjetoCustosCount(seqCompetencia, codigoCentroCusto, nomeProdutoServico, sigCentroProducao, tiposCentroProducao);
	}

	@Override
	public List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoCentroCustos(Integer firstResult, Integer maxResult, Integer seqCompetencia,
			FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.getSigProcessamentoCustoDAO().buscarCustosVisaoCentroCustos(firstResult, maxResult, seqCompetencia, fccCentroCustos, sigCentroProducao, tiposCentroProducao);
	}
	
	@Override
	public VisualizarAnaliseOtimizacaoVO buscarCustosVisaoCentroCustosOtimizacao(Integer seqCompetencia,
			FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.calculoMovimentosObjetoCentroCustoON.buscarCustosVisaoCentroCustosOtimizacao(seqCompetencia, fccCentroCustos, sigCentroProducao, tiposCentroProducao);
	}
	
	@Override
	public Long buscarCustosVisaoCentroCustosCount(Integer seq, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return this.getSigProcessamentoCustoDAO().buscarCustosVisaoCentroCustosCount(seq, fccCentroCustos, sigCentroProducao, tiposCentroProducao);
	}

	@Override
	public List<SigPassos> listarTodosPassos() {
		return this.getSigPassosDAO().listarTodosOrdenadosPelaDescricao();
	}

	@Override
	public void alterarProcessamentoCusto(SigProcessamentoCusto processamentoCusto) {
		this.getSigProcessamentoCustoDAO().atualizar(processamentoCusto);
	}

	@Override
	public void refreshProcessamentoCusto(SigProcessamentoCusto processamentoCusto) {
		this.getSigProcessamentoCustoDAO().refresh(processamentoCusto);
	}

	@Override
	public void excluirProcessamentoCustoLog(Integer pmuSeq) {
		this.getSigProcessamentoCustoLogDAO().removerPorProcessamento(pmuSeq);
	}

	@Override
	public void excluirProcessamentoPassos(Integer pmuSeq) {
		getSigProcessamentoPassosDAO().removerPorProcessamento(pmuSeq);
	}

	@Override
	public SigProcessamentoCusto reprocessarProcessamentoCusto(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException {
		return this.getManterProcessamentoMensalON().reprocessarProcessamentoCusto(processamentoCusto);
	}

	@Override
	public SigProcessamentoAnalises obterProcessamentoAnalise(Integer seqProcessamentoAnalise) {
		return this.getSigProcessamentoAnalisesDAO().obterPorChavePrimaria(seqProcessamentoAnalise);
	}

	@Override
	public void atualizarProcessamentoAnalise(SigProcessamentoAnalises sigProcessamentoAnalises) {
		this.getSigProcessamentoAnalisesDAO().atualizar(sigProcessamentoAnalises);
	}

	@Override
	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCentroCusto(Integer seqProcessamento, Integer codigoCentroCusto) {
		return this.getAlertaProcessamentoON().buscarAlertasPorProcessamentoCentroCusto(seqProcessamento, codigoCentroCusto);
	}

	@Override
	public List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCentroCustoSemAnalise(SigProcessamentoCusto sigProcessamentoCusto,
			FccCentroCustos fccCentroCustos, DominioSigTipoAlertaDetalhado tipoAlerta, Integer firstResult, Integer maxResult) {
		return this.getAlertaProcessamentoON().buscarAlertasPorProcessamentoCentroCustoSemAnalise(sigProcessamentoCusto, fccCentroCustos, tipoAlerta, firstResult, maxResult);
	}

	@Override
	public Integer buscarAlertasPorProcessamentoCentroCustoSemAnaliseCount(SigProcessamentoCusto sigProcessamentoCusto, FccCentroCustos fccCentroCustos,
			DominioSigTipoAlertaDetalhado tipoAlerta) {
		return this.getAlertaProcessamentoON().buscarAlertasPorProcessamentoCentroCustoSemAnalise(sigProcessamentoCusto, fccCentroCustos, tipoAlerta, null, null).size();
	}

	@Override
	public SigProcessamentoCusto obterProcessamentoCusto(Date dataCompetenciaDefault) {
		return this.getManterProcessamentoMensalON().obterProcessamentoCusto(dataCompetenciaDefault);
	}

	@Override
	public List<VisualizarAlertasProcessamentoVO> buscarTotaisParaCadaTipoAlerta(SigProcessamentoCusto processamentoCusto) throws ApplicationBusinessException {
		return this.getAlertaProcessamentoON().buscarTotaisParaCadaTipoAlerta(processamentoCusto);
	}

	@Override
	public Integer buscarTotaisParaCadaTipoAlertaCount(SigProcessamentoCusto processamentoCusto) throws ApplicationBusinessException {
		return this.getAlertaProcessamentoON().buscarTotaisParaCadaTipoAlerta(processamentoCusto).size();
	}

	@Override
	public SigProcessamentoCusto obterProcessamentoCusto(Integer seq) {
		return getSigProcessamentoCustoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public SigProcessamentoCusto incluirProcessamentoCusto(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException {
		return getManterProcessamentoMensalON().incluirProcessamentoCusto(processamentoCusto);
	}

	@Override
	public List<SigPassos> pesquisarListaPassosProcessamento() {
		return getSigPassosDAO().listarTodosOrdenadosPeloCampoOrdem();
	}

	@Override
	public List<SigProcessamentoPassos> pesquisarSigProcessamentoPassos(SigProcessamentoCusto processamentoCusto) {
		return getSigProcessamentoPassosDAO().pesquisarSigProcessamentoPassos(processamentoCusto);
	}

	@Override
	public void removerHistoricoProcessamento(Integer seqProcessamento) {
		this.getSigProcessamentoCustoLogDAO().removerPorProcessamento(seqProcessamento);
	}

	// ONs e DAOs

	protected SigProcessamentoCustoDAO getSigProcessamentoCustoDAO() {
		return sigProcessamentoCustoDAO;
	}

	protected SigProcessamentoCustoLogDAO getSigProcessamentoCustoLogDAO() {
		return sigProcessamentoCustoLogDAO;
	}

	protected SigProcessamentoAnalisesDAO getSigProcessamentoAnalisesDAO() {
		return sigProcessamentoAnalisesDAO;
	}

	protected SigProcessamentoAlertasDAO getSigProcessamentoAlertasDAO() {
		return sigProcessamentoAlertasDAO;
	}

	protected SigPassosDAO getSigPassosDAO() {
		return sigPassosDAO;
	}

	protected SigProcessamentoPassosDAO getSigProcessamentoPassosDAO() {
		return sigProcessamentoPassosDAO;
	}

	protected ManterProcessamentoMensalON getManterProcessamentoMensalON() {
		return manterProcessamentoMensalON;
	}

	protected AlertaProcessamentoON getAlertaProcessamentoON() {
		return alertaProcessamentoON;
	}
	
	protected SigMvtoContaMensalDAO getSigMvtoContaMensalDAO() {
		return sigMvtoContaMensalDAO;
	}

}
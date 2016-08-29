package br.gov.mec.aghu.sig.custos.processamento.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import br.gov.mec.aghu.sig.custos.vo.ProcessamentoCustoFinalizadoVO;
import br.gov.mec.aghu.sig.custos.vo.SomatorioAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAlertasProcessamentoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseOtimizacaoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;

public interface ICustosSigProcessamentoFacade extends Serializable {
		
	/**
	 * Representa o tamanho da paginação do Scrollable.
	 * 
	 * @author rmalvezzi
	 */
	public static final Integer SCROLLABLE_FETCH_SIZE = 200;

	List<ProcessamentoCustoFinalizadoVO> executarProcessamentoCustoAutomatizado() throws ApplicationBusinessException;
	
	void adicionarPendenciaProcessamentoFinalizado(ProcessamentoCustoFinalizadoVO vo)throws ApplicationBusinessException;
	
	String calcularTempoExecucao(SigProcessamentoCusto processamento, SigProcessamentoPassos passo);
	
	List<SigProcessamentoCustoLog> pesquisarHistoricoProcessamentoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo);

	Long pesquisarHistoricoProcessamentoCustoCount(SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo);

	List<SigProcessamentoCusto> pesquisarProcessamentoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao);

	Long pesquisarProcessamentoCustoCount(SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao);

	List<SigProcessamentoCusto> pesquisarCompetencia(DominioSituacaoProcessamentoCusto... situacao);

	List<SigProcessamentoCusto> pesquisarCompetenciaSemProducao(SigObjetoCustoVersoes objetoCustoVersao, SigDirecionadores direcionador);

	List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoObjetoCustos(final Integer firstResult, final Integer maxResult, Integer seqCompetencia,
			Integer codigoCentroCusto, String nomeProdutoServico, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao);

	Long buscarCustosVisaoObjetoCustosCount(Integer seqCompetencia, Integer codigoCentroCusto, String nomeProdutoServico, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao);

	List<SigPassos> listarTodosPassos();

	List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoCentroCustos(Integer firstResult, Integer maxResult, Integer seq,
			FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao);
	
	VisualizarAnaliseOtimizacaoVO buscarCustosVisaoCentroCustosOtimizacao(Integer seq,
			FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao);

	Long buscarCustosVisaoCentroCustosCount(Integer seq, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao);

	void atualizarProcessamentoAnalise(SigProcessamentoAnalises sigProcessamentoAnalises);

	SigProcessamentoAnalises obterProcessamentoAnalise(Integer seqProcessamentoAnalise);

	/**
	 * Busca alertas de um processamento
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento 		Seq do processamento
	 * @param codigoCentroCusto		Seq do centro custo (possivel filtro)
	 * @return						Lista de Todos os alteras
	 * 
	 */
	List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCentroCusto(Integer seqProcessamento, Integer codigoCentroCusto);

	/**
	 * Busca alertas de um processamento e análises sem parecer
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento		Seq do processamento
	 * @param codigoCentroCusto		Seq do centro custo (possivel filtro) (null para desconsiderar)
	 * @param tipoAlerta			Tipo do alerta para filtro (null para desconsiderar)
	 * @return						Lista de Todos os alteras
	 * 
	 */
	List<VisualizarAlertasProcessamentoVO> buscarAlertasPorProcessamentoCentroCustoSemAnalise(SigProcessamentoCusto processamentoCusto, FccCentroCustos fccCentroCustos, DominioSigTipoAlertaDetalhado tipoAlerta, Integer firstResult, Integer maxResult);

	/**
	 * Busca tamanho da lista dos alertas de um processamento e análises sem parecer
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento		Seq do processamento
	 * @param codigoCentroCusto		Seq do centro custo (possivel filtro) (null para desconsiderar)
	 * @param tipoAlerta			Tipo do alerta para filtro (null para desconsiderar)
	 * @return						Size da Lista de Todos os alteras
	 * 
	 */
	Integer buscarAlertasPorProcessamentoCentroCustoSemAnaliseCount(SigProcessamentoCusto processamentoCusto, FccCentroCustos fccCentroCustos, DominioSigTipoAlertaDetalhado tipoAlerta);
	
	SigProcessamentoCusto obterProcessamentoCusto(Integer seq);

	SigProcessamentoCusto obterProcessamentoCusto(Date dataCompetenciaDefault);

	SigProcessamentoCusto incluirProcessamentoCusto(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException;

	List<SigPassos> pesquisarListaPassosProcessamento();

	List<SigProcessamentoPassos> pesquisarSigProcessamentoPassos(SigProcessamentoCusto processamentoCusto);

	void alterarProcessamentoCusto(SigProcessamentoCusto processamentoCusto);

	void excluirProcessamentoCustoLog(Integer pmuSeq);

	void excluirProcessamentoPassos(Integer pmuSeq);

	SigProcessamentoCusto reprocessarProcessamentoCusto(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException;

	void refreshProcessamentoCusto(SigProcessamentoCusto processamentoCusto);

	/**
	 * Busca total de cada Tipo de Alerta para um processamento, além de análises sem parecer.
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto	Seq do processamento  
	 * @return						Lista com a representação visual dos totais
	 * @throws ApplicationBusinessException 
	 */
	List<VisualizarAlertasProcessamentoVO> buscarTotaisParaCadaTipoAlerta(SigProcessamentoCusto processamentoCusto) throws ApplicationBusinessException;

	/**
	 * Busca tamanho da lista dos total de cada Tipo de Alerta para um processamento, além de análises sem parecer.
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto	Seq do processamento  
	 * @return						Size da lista com a representação visual dos totais
	 * @throws ApplicationBusinessException 
	 */
	Integer buscarTotaisParaCadaTipoAlertaCount(SigProcessamentoCusto processamentoCusto) throws ApplicationBusinessException;
	
	/**
	 * Remove o histórico (log) do processamento informado por parâmetro
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamento
	 */
	void removerHistoricoProcessamento(Integer seqProcessamento);

	SomatorioAnaliseCustosObjetosCustoVO obterSomatorioVisualizarObjetoCusto(
			Integer seqCompetencia, Integer codigoCentroCusto,
			String nomeProdutoServico, SigCentroProducao sigCentroProducao,
			Integer seqObjetoCustoVersao, DominioTipoCentroProducaoCustos[] tiposCentroProducao);
	
	SomatorioAnaliseCustosObjetosCustoVO obterSomatorioVisualizarCentroCusto(Integer seqCompetencia, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao,  DominioTipoCentroProducaoCustos[] tiposCentroProducao);

}
package br.gov.mec.aghu.sig.custos.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.dominio.DominioTipoRateio;
import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigComunicacaoEventos;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigEscalaPessoa;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.DetalhamentoCustosGeralVO;
import br.gov.mec.aghu.sig.custos.vo.DetalheProducaoObjetoCustoVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPesoClienteVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;

public interface ICustosSigCadastrosBasicosFacade extends Serializable {

	/**
	 * Verifica se um contrato já foi contabilizado
	 * 
	 * @param contrato
	 * @return
	 */
			
	public boolean verificarContratoContabilizado(ScoContrato contrato);
	
	/**
	 * Retorna a contagem de registros de Centro de Producão para o paginator.
	 * 
	 * @param nomeCentroProducao
	 * @param tipoCentroProducao
	 * @param situacao
	 * @return 						Número total de registros encontrados
	 */
	Long listarCentroProducaoCount(final String nomeCentroProducao, final DominioTipoCentroProducaoCustos tipoCentroProducao, final DominioSituacao situacao);

	/**
	 * Pesquisa por todos os itens da tabela {@code SigCentroProducao}. Método utilizado para paginator.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nomeCentroProducao
	 * @param tipoCentroProducao
	 * @param situacao
	 * @return 						Lista com os {@link SigCentroProducao} filtrados pelos parâmetros 
	 */
	List<SigCentroProducao> pesquisarCentroProducaoCentroTipoSituacao(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final String nomeCentroProducao, final DominioTipoCentroProducaoCustos tipoCentroProducao, final DominioSituacao situacao);

	void excluirCentroProducao(final Integer seqCentroProducao) throws ApplicationBusinessException;

	SigCentroProducao obterSigCentroProducao(final Integer seq);

	List<SigCentroProducao> pesquisarCentroProducao(Object paramPesquisa, DominioSituacao situacao) throws BaseException;

	void alterarCentroProducao(final SigCentroProducao centroProducao) throws ApplicationBusinessException;;

	void inserirCentroProducao(final SigCentroProducao centroProducao) throws ApplicationBusinessException;

	List<FccCentroCustos> pesquisarCentroCustosHierarquia(FccCentroCustos principal);

	List<SigCentroProducao> pesquisarCentroProducao();

	void persistirGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException;

	void excluirGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException;

	SigGrupoOcupacoes obterGrupoOcupacao(Integer seq);

	List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String descricao,
			DominioSituacao situacao, FccCentroCustos centroCusto);

	Long pesquisarGrupoOcupacaoCount(String descricao, DominioSituacao situacao, FccCentroCustos centroCusto);

	void validarRepeticaoCargosGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao, List<SigGrupoOcupacaoCargos> listaOcupacaoCargo, RapOcupacaoCargo ocupacaoCargo,
			Integer posicao) throws ApplicationBusinessException;

	List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(Object paramPesquisa, FccCentroCustos centroCustoAtividade);

	Long pesquisarComunicacaoEventosCount(SigComunicacaoEventos sigComunicacaoEventos);

	SigComunicacaoEventos obterComunicacaoEvento(Integer seqComunicacaoEvento);

	void excluirComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos);

	void persistComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos) throws ApplicationBusinessException;

	List<SigDirecionadores> pesquisarDirecionadores(DominioTipoDirecionadorCustos tipoDirecionador, DominioTipoCalculoObjeto tipoCalculo, Boolean coletaSistema);

	List<SigDirecionadores> pesquisarDirecionadores(DominioSituacao situacao, DominioTipoDirecionadorCustos tipo);

	List<SigDirecionadores> pesquisarDirecionadores(Boolean indTempoIsNull, Boolean filtrarFatConvHoraIsNotNull);

	List<SigDirecionadores> pesquisarDirecionadoresTempoMaiorMes();

	List<SigDirecionadores> pesquisarDirecionadoresAtivosInativo(Boolean indTempoIsNull, Boolean ativo);

	SigDirecionadores obterDirecionador(Integer seq);

	Long pesquisarDirecionadorAtividadeCount(SigDirecionadores direcionador);

	List<SigObjetoCustoClientes> pesquisarObjetoCustoClientePorObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersoes);

	List<SigAtividades> pesquisarDirecionadorAtividade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SigDirecionadores direcionador);

	List<SigDirecionadores> pesquisaDirecionadoresDoObjetoCusto(SigObjetoCustoVersoes versao, DominioSituacao direcionadorRateioSituacao,
			DominioTipoDirecionadorCustos indTipo, DominioTipoCalculoObjeto indTipoCalculo);

	void persistirDirecionador(SigDirecionadores direcionador, RapServidores servidorResponsavel) throws ApplicationBusinessException;

	List<DominioTipoCalculoObjeto> listarTiposCalculoObjeto(DominioTipoDirecionadorCustos tipoDirecionadorCusto);
	
	List<SigDirecionadores> pesquisarDirecionadoresTipoATAB(Boolean ativo);

	Map<Integer, Double> pesquisarPesoTabelaUnificadaSUS(Integer codigoCentroCusto);

	void persistirPesosObjetoCusto(List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto, FccCentroCustos centroCusto, DominioTipoRateio tipoRateio,
			SigDirecionadores direcionador, Boolean estaUtilizandoTabelaSus, Map<Integer, Double> mapeamentoSus) throws ApplicationBusinessException;

	void validarInclusaoAlteracaoClienteObjetoCusto(SigObjetoCustoClientes sigObjetoCustoClientes, List<SigObjetoCustoClientes> listaClientes, boolean alteracao)
			throws ApplicationBusinessException;

	void persistCliente(SigObjetoCustoClientes cliente);

	void excluirCliente(SigObjetoCustoClientes clienteExcluir);

	void validarExclusaoClienteObjetoCusto(SigObjetoCustoVersoes objetoCustoVersoes) throws ApplicationBusinessException;

	VisualizarAnaliseCustosObjetosCustoVO obterDetalheVisualizacaoAnaliseCC(Integer seqCompetencia, FccCentroCustos fccCentroCustos);

	VisualizarAnaliseCustosObjetosCustoVO obterDetalheVisualizacaoAnaliseOC(Integer seqCompetencia, Integer seqObjetoCustoVersao,Integer codigoCentroCusto, Short seqPagador);

	void persistirProducaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao, SigDirecionadores direcionador, SigProcessamentoCusto competencia,
			List<SigDetalheProducao> listaClientes) throws ApplicationBusinessException;

	boolean verificarPreenchimentoValoresClientes(List<SigDetalheProducao> listaClientes) throws ApplicationBusinessException;

	BigDecimal calcularValorTotal(List<SigDetalheProducao> listaClientes);

	void verificarEdicaoDetalheProducao(Integer seq) throws ApplicationBusinessException;

	void persistirSigDetalheProducao(SigDetalheProducao detalhe);

	Long pesquisarProducaoCount(FccCentroCustos centroCusto, SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto, SigDirecionadores direcionador);

	List<DetalheProducaoObjetoCustoVO> pesquisarProducao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto, SigDirecionadores direcionador);

	void excluirDetalheProducao(Integer seqDetalheProducao) throws ApplicationBusinessException;

	SigDetalheProducao obterDetalheProducao(Integer seqDetalheProducao);

	List<SigDetalheProducao> listarClientesObjetoCustoVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			SigProcessamentoCusto competencia);

	List<ObjetoCustoPesoClienteVO> pesquisarObjetoCustoPesoCliente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome, DominioSituacaoVersoesCustos situacao);

	Long pesquisarObjetoCustoPesoClienteCount(FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome, DominioSituacaoVersoesCustos situacao);

	List<SigObjetoCustoClientes> buscaObjetoClienteVersaoAtivo(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			Boolean semValor);

	void atualizarValorCliente(List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException;

	SigObjetoCustoClientes validaIndicacaoTodosCC(Integer ocvSeq, Integer dirSeq);

	void associarCentrosCustoClientes(SigObjetoCustoClientes sigObjetoCustoClientes, RapServidores servidor);

	/**
	 * Busca os totais de cada Movimento separados por Tipo do Movimento Diretos (Insumos, Equipamentos, Pessoas e Serviços) e Indiretos.
	 * 
	 * @author rmalvezzi
	 * @param pmuSeq 					Seq do Processamento
	 * @param ocvSeq 					Seq do Objeto Custo Versão
	 * @param fccCentroCustos 			Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Lista com os movimentos
	 */
	List<DetalhamentoCustosGeralVO> buscarMovimentosGeral(Integer pmuSeq, Integer ocvSeq, FccCentroCustos fccCentroCustos,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca a lista dos movimentos de Servicos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Lista com os movimentos
	 */
	List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca o tamanho da lista dos movimentos de Servicos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Tamanho da lista doos movimentoso
	 */
	Integer buscarMovimentosServicosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca a lista dos movimentos dos Indiretos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Lista com os movimentos
	 */
	List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosIndiretos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens, Integer seqObjetoCustoDebita);

	/**
	 * Busca o tamanho da lista dos movimentos dos Indiretos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Tamanho da lista doos movimentos
	 */
	Integer buscarMovimentosIndiretosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca a lista dos movimentos de Pessoas.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Lista com os movimentos
	 */
	List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosPessoas(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca o tamanho da lista dos movimentos de Pessoas.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Tamanho da lista doos movimentos
	 */
	Integer buscarMovimentosPessoasCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca a lista dos movimentos de Insumos.
	 * 
	 * @author rmalvezzi
	  * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Lista com os movimentos
	 */
	List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosInsumos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca o tamanho da lista dos movimentos de Insumos.
	 * 
	 * @author rmalvezzi
	  * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Tamanho da lista doos movimentos
	 */
	Integer buscarMovimentosInsumosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, DominioTipoVisaoAnalise tipoVisaoAnaliseItens);

	/**
	 * Busca a lista dos movimentos de Equipamentos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Lista com os movimentos
	 */
	List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosEquipamentos(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens);
  
	/**
	 * Busca o tamanho da lista lista dos movimentos de Equipamentos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia 			Seq do Processamento
	 * @param seqObjetoVersao 			Seq do Objeto Custo Versão
	 * @param seqCentroCusto 			Seq do Centro de Custo associado
	 * @param tipoVisaoAnaliseItens 	Tipo da consulta (Objeto Custo ou Centro Custo) 
	 * @return 							Tamanho da lista doos movimentos
	 */
	Integer buscarMovimentosEquipamentosCount(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,
			DominioTipoVisaoAnalise tipoVisaoAnaliseItens);
	
	/**
	 * Busca a escala assistencial pelo centro de custo.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param centroCustos
	 * @return
	 */
	List<SigEscalaPessoa> pesquisarEscalaPessoas(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCustos);

	/**
	 * Busca o tamanho da lista de escalas assistenciais.
	 * @param centroCustos
	 * @return
	 */
	Long pesquisarEscalaPessoasCount(FccCentroCustos centroCustos);

	
	/**
	 * 
	 * @param centroCustos
	 * @param escala
	 * @return
	 */
	public void verificarEscalaPessoasCentroCusto(FccCentroCustos centroCustos, SigEscalaPessoa escala) throws ApplicationBusinessException ;
	/**
	 * Busca a escala assistencial pelo centro de custo.
	 * @param centroCustos
	 * @return
	 */
	boolean pesquisarEscalaPessoasCentroCusto(FccCentroCustos centroCustos);

	/**
	 * Grava uma nova escala assistencial
	 * @param sigEscalaPessoas
	 */
	void gravar(SigEscalaPessoa sigEscalaPessoas);

	/**
	 * Exclui uma escala assistencial
	 * @param sigEscalaPessoas
	 */
	void excluir(SigEscalaPessoa sigEscalaPessoas);

	/**
	 * Obtem uma escala assistencial pelo id da escala
	 * @param seq
	 * @return
	 */
	SigEscalaPessoa obterEscalaPessoas(Integer seq);

	
	/**
	 * Verifica se a escala informada já não existe associada ao centro de custo
	 * @param sigEscalaPessoas
	 * @return
	 */
	SigEscalaPessoa verificaExistenciaDaAtividadeNoCentroDeCusto(
			SigEscalaPessoa sigEscalaPessoas);

	/**
	 * Edita uma escala assistencial
	 * @param sigEscalaPessoas
	 */
	void editar(SigEscalaPessoa sigEscalaPessoas);

	List<SigComunicacaoEventos> pesquisarComunicacaoEventos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigComunicacaoEventos sigComunicacaoEventos);

	void adicionarPendenciaProcessamentoHomologado(Integer seq) throws ApplicationBusinessException;
	
	List<SigCategoriaConsumos> buscaCategoriasDeConsumo(Integer firstResult, Integer maxResult, String descricao, boolean asc, String categoriaConsumo, DominioIndContagem indContagem, DominioSituacao situacao);
	
	Long buscaCategoriasDeConsumoCount(String descricao, DominioIndContagem indContagem, DominioSituacao situacao);

	SigCategoriaConsumos obterCategoriaConsumo(Integer seq);

	void excluirCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException;

	void persistirCategoriaConsumo(SigCategoriaConsumos categoriaConsumo, RapServidores servidor) throws ApplicationBusinessException;

	public void atualizarCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException;
}
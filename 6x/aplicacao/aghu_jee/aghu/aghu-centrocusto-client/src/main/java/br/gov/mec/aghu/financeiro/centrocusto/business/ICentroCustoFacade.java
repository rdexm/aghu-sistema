package br.gov.mec.aghu.financeiro.centrocusto.business;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.centrocusto.vo.CentroCustosVO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcuAgrupaGrupoMaterial;
import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface ICentroCustoFacade extends Serializable {
	
	public abstract List<FccCentroCustos> pesquisarCentroCustosSuperior(
			final String strPesquisa);

	
	public abstract Long pesquisarCentroCustosSuperiorCount(
			final String strPesquisa);

	public abstract FccCentroCustos pesquisarCentroCustosPorMatriculaVinculo(
			final Integer matricula, final Short vinCodigo);

	public abstract Long obterFccCentroCustoCount(
			final FccCentroCustos centroCusto,
			final FcuGrupoCentroCustos grupoCentroCusto,
			final FccCentroCustos centroCustoSuperior, final RapServidores servidorChefia, DominioTipoCentroProducaoCustos[] tiposCentroProducao);

	/**
	 * Metodo para efetuar a pesquisa de centros de custo. Foi decidido com o
	 * Vicente e Geraldo para essa caso passar os objetos internos ao invÃ©s dos
	 * campos da interface por se tratar de uma tela com uma grande quantidade
	 * de filtros
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param centroCusto
	 * @param grupoCentroCusto
	 * @param centroCustoSuperior
	 * @param servidor
	 * @param conjAtividades
	 * @return
	 */
	
	public abstract List<FccCentroCustos> pesquisarCentroCustos(
			final Integer firstResult, final Integer maxResults,
			final FccCentroCustos centroCusto,
			final FcuGrupoCentroCustos grupoCentroCusto,
			final FccCentroCustos centroCustoSuperior, RapServidores servidorChefia, DominioTipoCentroProducaoCustos[] tiposCentroProducao);


	/**
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param codigo
	 * @return
	 */
	
	FccCentroCustos obterFccCentroCustos(final Integer codigo);
	FccCentroCustos obterCentroCusto(Integer codigo);
	List<Integer> getCodigosCentrosCusto(List<FccCentroCustos> ccs);
	/**
	 * 
	 * @dbtables FccCentroCustos select
	 * 
	 * @param codigo
	 * @return
	 */
	
	public abstract FccCentroCustos obterFccCentroCustosAtivos(
			final Integer codigo);

	/**
	 * 
	 * @dbtables FcuGrupoCentroCustos select
	 * 
	 * @param filtro
	 * @return
	 */
	
	public abstract List<FcuGrupoCentroCustos> pesquisarGruposCentroCustos(
			final String filtro);

	
	public abstract Long pesquisarGruposCentroCustosCount(final String filtro);

	/**
	 * @param valor
	 * @return
	 */
	
	
	public abstract List<FccCentroCustos> pesquisarCentroCustosPorCodigoDescricao(
			final String filtro);

	
	public abstract Long pesquisarCentroCustosPorCodigoDescricaoCount(
			String filtro);

	/**
	 * @param descricaoCentroCustoBuscaLov
	 * @return
	 */
	
	public abstract List<FccCentroCustos> pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(
			final String descricaoCentroCustoBuscaLov);

	
	public abstract Long pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(
			String filtro);

	/**
	 * MÃ©todo responsÃ¡vel pela persistencia do centro de custo.
	 * 
	 * @dbtables FccCentroCustos insert,update
	 * 
	 * @param centroCusto
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	
	public abstract void persistirCentroCusto(final FccCentroCustos centroCusto, final Boolean isEdicao)
			throws ApplicationBusinessException, BaseException;

	/**
	 * Retornar centro de custo de acordo com o cÃ³digo ou descriÃ§Ã£o informados
	 * 
	 * @dbtables FccCentroCustos select
	 * @param centroCusto
	 *            ou descriÃ§Ã£o
	 * @return lista de centro de custo
	 */
	
	public abstract List<FccCentroCustos> pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(
			final String centroCusto, final boolean somenteAtivo);

	public abstract List<FccCentroCustos> pesquisarCentroCustos(
			final Object objPesquisa);

	public abstract FccCentroCustos pesquisaCentroCustoPorSituacaoPacienteVO(
			final SituacaoPacienteVO situacaoPacienteVO);

	public abstract FccCentroCustos obterCentroCustoPorChavePrimaria(
			final Integer codigo);

	public abstract List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemDescricao(
			final Object centroCusto);

	public abstract Integer pesquisarCentroCustosAtivosOrdemDescricaoCount(
			final Object centroCusto);

	/**
	 * Retorna os centros custos ativos
	 * 
	 * @param parametro
	 * @return
	 */
	public abstract List<FccCentroCustos> pesquisarCentroCustosAtivos(
			final Object parametro);

	/**
	 * Pesquisa centros de custos do usuÃ¡rio, tanto o centro de custo de atuaÃ§Ã£o do usuÃ¡rio,
	 * como os centros de custo filhos deste
	 * @param parametro
	 * @return
	 */
	public abstract List<FccCentroCustos> pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(
			final Object parametro);

	/**
	 * Retorna centro de custo do usuÃ¡rio
	 * @author clayton.bras
	 * @return
	 */
	public abstract FccCentroCustos obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao();

	public abstract List<FccCentroCustos> pesquisarCentroCustosServidor(
			Object centroCusto);

	public abstract Long pesquisarCentroCustosServidorCount(
			Object centroCusto);

	public abstract List<FccCentroCustos> pesquisarCentroCustosServidorOrdemDescricao(
			Object centroCusto);

	public abstract List<FccCentroCustos> pesquisarCentroCustosAplicacaoOrdemDescricao(
			Object centroCusto);

	public abstract List<FccCentroCustos> pesquisarCentroCustosAtivosPorCodigoOuDescricao(
			Object objPesquisa);

	public abstract Long pesquisarCentroCustosCount(Object objPesquisa);

	/**
	 * Pesquisa os centros de custo ativos, do usuÃ¡rio ou filhoes do ccusto do usuario, inclui ccusto AtuaÃ§Ã£o e lotacao.
	 * @param parametro
	 * @return
	 * @author bruno.mourao
	 * @throws ApplicationBusinessException  
	 * @since 14/03/2012
	 */
	public abstract List<FccCentroCustos> pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(Object parametro,
			final DominioCaracteristicaCentroCusto caracteristica) throws ApplicationBusinessException;
	/**
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public abstract List<FccCentroCustos> obterListaServicosEmEspecialidades(
			String objPesquisa);

	public FccCentroCustos pesquisarCentroCustoAtivoPorCodigo(Integer cCodigo);

	/**
	 * Busca todos os centros de custos levando em consideração os filtros informados por parametro e adiciona o filtro de ser apenas Centros de custo
	 * que não sejam do grupo de obras.
	 * 
	 * @author rmalvezzi
	 * @param paramPesquisa				Possiveis filtros de Código ou Descrição do Centro de Custo (NULL se for para desconsiderar esse parametro).
	 * @param seqCentroProducao			Filtro pelo Código do Centro de Produção (NULL se for para desconsiderar esse parametro).
	 * @param situacao					Filtro pela Situação do Centro de Custo (NULL se for para desconsiderar esse parametro).
	 * @return							Retorna Lista dos Centros de Custos que correspondem aos filtros informados por parametro.   
	 */
	List<FccCentroCustos> pesquisarCentroCustosPorCentroProdExcluindoGcc(Object paramPesquisa, Integer seqCentroProducao, DominioSituacao situacao);

	//CENTRO PRODUÃ‡ÃƒO---------------------------------------------------------------
	public boolean existeCentroCustoAssociado(SigCentroProducao centroProducao);
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSC();	
	
	/**
	 * Pesquisa hierárquica de centro de custo do servidor por código e/ou descrição do centro de custo
	 * @param paramPesquisa
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(Object paramPesquisa);	
	
	public Long pesquisarCentroCustoUsuarioGerarSCSuggestionCount(Object paramPesquisa);	

	public FccCentroCustos pesquisarCentroCustoAtuacaoLotacaoServidor();

	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizaSC();
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizacaoSC();
	
	public boolean centroCustoAceitaProjeto(FccCentroCustos centroCusto);
	
	public List<FccCentroCustos> pesquisarCentroCustosComLimitResult(Object objPesquisa, Integer limit);
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizaSs();
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizacaoSs();

	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSs();
	
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSSSuggestion(Object paramPesquisa);

	List<FccCentroCustos> pesquisarCentroCustosPorCentroProdAtivo(SigCentroProducao centroProducao);

	public Long pesquisarCentroCustoCount(SigCentroProducao centroProducao,
			DominioTipoCentroProducaoCustos tipo, String descricao,
			DominioSituacao situacao);
	
	List<FccCentroCustos> pesquisarCentroCustoComStatusDaEspecialidade(Object paramPesquisa, DominioSituacao ativaOuInativa);
	
	public List<FccCentroCustos> pesquisarCentroCusto(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao centroProducao,
			DominioTipoCentroProducaoCustos tipo, String descricao,
			DominioSituacao situacao);
	
	public Set<Integer> pesquisarCentroCustoComHierarquia(Integer cctCodigo);
	
	List<FccCentroCustos> pesquisarCentroCustoPorCodigoEDescricao(Object paramPesquisa);

	Long pesquisarCentroCustoPorCodigoEDescricaoCount(Object paramPesquisa);

	CentroCustosVO obterCentroCustoParaSolicitacaoCompraOuServico(Integer numeroSolicitacaoCompraServico, boolean isSolicitacaoCompra);
	
	List<FcuAgrupaGrupoMaterial> pesquisarFcuAgrupaGrupoMaterialAtivos(String param, Integer maxResults);

	Long pesquisarFcuAgrupaGrupoMaterialAtivosCount(String param);

	public List<FccCentroCustos> pesquisarCCLotacaoEAtuacaoFuncionario(Object filtro);
	
	public Long pesquisarCCLotacaoEAtuacaoFuncionarioCount(Object filtro);
	public FccCentroCustos obterCentroCustoPorUnidadeFuncional( Short unfSeq );


	public Long pesquisarCentroCustosAtivosCount(String objPesquisa);


	public Long pesquisarCentroCustosAtivosOrdemDescricaoCountL(String objPesquisa);
	
	
	public Long obterCentroCustoAtivosCount();
	
	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemOuDescricao(String objPesquisa);
	
	public Long pesquisarCentroCustosAtivosOrdemOuDescricaoCount(String objPesquisa);

	void enviarEmailNotificacaoChefe(FccCentroCustos centroCusto) throws ApplicationBusinessException;
	

	
}
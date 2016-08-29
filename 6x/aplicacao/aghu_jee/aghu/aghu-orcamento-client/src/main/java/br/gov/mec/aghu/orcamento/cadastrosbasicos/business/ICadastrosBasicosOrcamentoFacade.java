package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSiasgServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoResultVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ICadastrosBasicosOrcamentoFacade extends Serializable {

	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(
			final Object strPesquisa);

	/**
	 * Retorna a quantidade de naturezas de despesa conforme filtros de grupo de natureza de despesa, descricao e situacao da natureza de despesa
	 * @param grupoNatureza
	 * @param descricaoNatureza
	 * @param indSituacao
	 * @return Integer
	 */
	public Long countPesquisaListaNaturezaDespesa(
			FsoGrupoNaturezaDespesa grupoNatureza, String descricaoNatureza, DominioSituacao indSituacao);

	/**
	 * Retorna uma lista de naturezas de despesa por grupo de natureza, descricao ou situacao
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param grupoNatureza
	 * @param descricaoNatureza
	 * @param indSituacao
	 * @return List
	 */
	public List<FsoNaturezaDespesa> pesquisarListaNaturezaDespesa(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FsoGrupoNaturezaDespesa grupoNatureza,
			String descricaoNatureza, DominioSituacao indSituacao);

	/**
	 * Obtem a natureza de despesa pela chave primaria
	 * @param codigoNatureza
	 * @return  FsoNaturezaDespesa
	 */
	public FsoNaturezaDespesa obterNaturezaDespesa(
			FsoNaturezaDespesaId codigoNatureza);

	/**
	 * Insere a natureza de despesa
	 * @param naturezaDespesa
	 * @throws ApplicationBusinessException
	 */
	public void inserirNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa)
			throws ApplicationBusinessException;

	void alterarNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) throws ApplicationBusinessException;
	
	void excluirNaturezaDespesa(FsoNaturezaDespesaId id) throws ApplicationBusinessException;

	/**
	 * Retorna uma lista paginada de verbas de gestão na base que atendem os filtros passados como parâmetro
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FsoVerbaGestao verbaGestaoFiltro, DominioSituacao situacao,
			Boolean convenio, String descricaoVerba,
			String nroInterno, BigInteger nroConvSiafi, String planoInterno);

	
	/**
	 * Retorna a quantidade de verbas de gestão na base que atendem os filtros passados como parâmetro
	 * @param verbaGestaoFiltro
	 * @param situacao
	 * @param convenio
	 * @param descricaoVerba
	 * @param nroInterno
	 * @param nroConvSiafi
	 * @return int
	 */
	public Long pesquisarVerbaGestaoCount(FsoVerbaGestao verbaGestaoFiltro,
			DominioSituacao situacao, Boolean convenio, String descricaoVerba,
			String nroInterno, BigInteger nroConvSiafi, String planoInterno);

	/**
	 * Pesquisa verba de gestão ATIVAS por código ou descrição
	 * @param paramPesquisa
	 * @return List
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(
			Object paramPesquisa);
	
	public List<FsoVerbaGestao> pesquisarVerbaGestao(
			Object filter, Date data, Integer max);

	public FsoVerbaGestao obterVerbaGestaoPorChavePrimaria(Integer seq);

	public void gravaFontesRecursoXVerbaGestao (FsoVerbaGestao verbaGestao,
			List<FsoFontesXVerbaGestao> inserirFontesXVerba,
			List<FsoFontesXVerbaGestao> removerFontesXVerba) throws ApplicationBusinessException;

	public List<FsoFontesRecursoFinanc> pesquisarFonteRecursoPorCodigoOuDescricao(
			Object paramPesquisa);

	public List<FsoFontesXVerbaGestao> pesquisarFontesXVerba(
			FsoVerbaGestao verbaGestao);

	FsoGrupoNaturezaDespesa obterGrupoNaturezaDespesa(Integer codigo);

	List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria, int first, int max,
			String orderField, Boolean orderAsc);

	Long contarGruposNaturezaDespesa(
			FsoGrupoNaturezaDespesaCriteriaVO criteria);

	/**
	 * Exclui uma verba de gestão
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	void excluirVerbaGestao(Integer seq) throws ApplicationBusinessException;
	
	FsoVerbaGestao obterVerbaGestaoProjetoFipe(FccCentroCustos ccAplic);
	
	void incluir(FsoGrupoNaturezaDespesa grupo) throws ApplicationBusinessException;

	void alterar(FsoGrupoNaturezaDespesa grupo) throws ApplicationBusinessException;

	void excluir(Integer codigo) throws ApplicationBusinessException;

	public Long countPesquisaFontesRecursoFinanc(
			FsoFontesRecursoFinanc fontesRecursoFinanc);

	public FsoFontesRecursoFinanc obterFontesRecursoFinanc(Long codigoFonte);

	public List<FsoFontesRecursoFinanc> listaPesquisaFontesRecursoFinanc(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FsoFontesRecursoFinanc fontesRecursoFinanc);

	public Boolean verificarFonteRecursoFinancUsadaEmVerbaGestao(
			FsoFontesRecursoFinanc fontesRecursoFinanc);

	void incluirFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc)
			throws ApplicationBusinessException;

	void alterarFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc)
			throws ApplicationBusinessException;

	void excluirFontesRecursoFinanc(final Long codigo) throws ApplicationBusinessException;

	List<FsoParametrosOrcamentoResultVO> pesquisarParametrosOrcamento(
			FsoParametrosOrcamentoCriteriaVO criteria, int first, int max,
			String orderField, Boolean orderAsc);

	Long contarParametrosOrcamento(FsoParametrosOrcamentoCriteriaVO criteria);

	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(
			FsoGrupoNaturezaDespesa grupo, Object filter);
	
	FsoParametrosOrcamento obterParametrosOrcamento(Integer id);
	FsoParametrosOrcamento obterParametrosOrcamento(Integer id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	void alterar(FsoParametrosOrcamento entidade) throws ApplicationBusinessException;
	void incluir(FsoParametrosOrcamento entidade) throws ApplicationBusinessException;
	void excluirFsoParametrosOrcamento(Integer seq) throws ApplicationBusinessException;

	/**
	 * Método que retorna uma cópia de um FsoParametrosOrcamento com campo seq setado com valor nulo e ServidorInclusao com o usuário logado
	 */
	FsoParametrosOrcamento clonarParametroOrcamento(FsoParametrosOrcamento parametroOriginal) throws ApplicationBusinessException;
	
	FccCentroCustos getCentroCustoScParam(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valor);

	Boolean hasUniqueRequiredCentroCustoScParam(ScoMaterial material,FccCentroCustos centroCusto, BigDecimal valorTotal);

	List<FccCentroCustos> listarCentroCustosScParams(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valorTotal, Object filter);

	Boolean isCentroCustoValidScParam(ScoMaterial material,FccCentroCustos ccSolicitante, BigDecimal valor, FccCentroCustos ccAplicacao);

	Boolean hasUniqueRequiredVerbaGestaoScParam(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valorTotal);

	FsoVerbaGestao getVerbaGestaoScParam(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valor);

	List<FsoVerbaGestao> listarVerbasGestaoScParams(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valorTotal, Object filter);
	
	Boolean isVerbaGestaoValidScParam(ScoMaterial material,FccCentroCustos ccSolicitante, BigDecimal valor,	FsoVerbaGestao verbaGestao);

	boolean hasUniqueRequiredGrupoNaturezaScParam(ScoMaterial material, FccCentroCustos centroCusto, BigDecimal valorTotal);

	public FsoGrupoNaturezaDespesa getGrupoNaturezaScParam(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valorTotal);

	public List<FsoGrupoNaturezaDespesa> listarGruposNaturezaScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valorTotal, Object filter);

	public boolean hasUniqueRequiredNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto,
			FsoGrupoNaturezaDespesa fsoGrupoNaturezaDespesa,
			BigDecimal valorTotal);

	public FsoNaturezaDespesa getNaturezaScParam(ScoMaterial material,
			FccCentroCustos centroCusto,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa, BigDecimal valorTotal);

	public List<FsoNaturezaDespesa> listarNaturezaScParams(
			ScoMaterial material, FccCentroCustos centroCusto,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa,
			BigDecimal valorTotal, Object filter);
	
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesaAtivas(
			FsoGrupoNaturezaDespesa grupo, Object filter);

	public boolean isNaturezaValidScParam(ScoMaterial material,
			FccCentroCustos centroCusto, BigDecimal valorTotal,
			FsoNaturezaDespesa naturezaDespesa);

	Boolean existeVerbaGestaoComFonteVigente(Integer id, Date data);

	Boolean existeNaturezaDespesaAtiva(FsoNaturezaDespesaId id);
	
	FsoParametrosOrcamento getAcaoScParam(ScoMaterial material, FccCentroCustos centroCusto,
			BigDecimal valor, FsoParametrosOrcamentoCriteriaVO.Parametro param);
	
	public List<FsoNaturezaDespesa> listarTodasNaturezaDespesa(
			Object objPesquisa);
	
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesaPorGrupo(
			FsoGrupoNaturezaDespesa grupo, Object filter);
	
	public Long pesquisarNaturezaDespesaPorGrupoCount(
			FsoGrupoNaturezaDespesa grupo, Object filter);
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricaoAtivos(final Object strPesquisa);
	
	public Long listarTodasNaturezaDespesaCount(Object objPesquisa);

	public boolean hasUniqueRequiredNaturezaSsParam(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa);

	public Boolean hasUniqueRequiredGrupoNaturezaSsParam(ScoServico servico);

	public Boolean hasUniqueRequiredVerbaGestaoSsParam(ScoServico servico);

	public FsoNaturezaDespesa getNaturezaSsParam(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa);

	public FsoGrupoNaturezaDespesa getGrupoNaturezaSsParam(ScoServico servico);

	public FsoVerbaGestao getVerbaGestaoSsParam(ScoServico servico);

	public List<FsoGrupoNaturezaDespesa> listarGruposNaturezaSsParams(
			ScoServico servico, Object filter);

	public List<FsoNaturezaDespesa> listarNaturezaSsParams(ScoServico servico,
			FsoGrupoNaturezaDespesa grupoNaturezaDespesa, Object filter);

	public List<FsoVerbaGestao> listarVerbasGestaoSsParams(ScoServico servico,
			Object filter);

	public boolean isNaturezaValidSsParam(ScoServico servico,
			FsoNaturezaDespesa naturezaDespesa);

	public boolean isVerbaGestaoValidSsParam(ScoServico servico,
			FsoVerbaGestao verbaGestao);
	
	public List<ScoSiasgServico> pesquisarCatSer(Object objCatSer);
	
	
	/**
	 * C3 de 31584
	 * 
	 * @param seqGrupo
	 * @param param
	 * @param maxResults
	 * @return
	 */
	List<FsoNaturezaDespesa> pesquisarFsoNaturezaDespesaAtivosPorGrupo(Integer seqGrupo, String param, Integer maxResults);

	/**
	 * C3 de 31584
	 * 
	 * @param seqGrupo
	 * @param param
	 * @return
	 */
	Long pesquisarFsoNaturezaDespesaAtivosPorGrupoCount(Integer seqGrupo, String param);
	
	/**
	 * C5 de 31584
	 * 
	 * @param seqGrupo
	 * @param param
	 * @return
	 */
	FsoNaturezaDespesa obterFsoNaturezaDespesa(Integer seqGrupo, Byte codigo);
	
	FsoParametrosOrcamento pesquisarRegraOrcamentaria(FsoParametrosOrcamentoCriteriaVO criteria);
	
	FsoNaturezaDespesa getNaturezaScGrupoMaterial(ScoMaterial material, BigDecimal paramVlrNumerico);

	/**
	 * #46298 - Suggestion Box de Grupo Natureza de Despesa - Lista
	 */
	public List<FsoGrupoNaturezaDespesa> obterListaGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Grupo Natureza de Despesa - Count
	 */
	public Long obterCountGrupoNaturezaDespesaAtivosPorCodigoOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Natureza de Despesa por Grupo Natureza de Despesa - Lista
	 */
	public List<FsoNaturezaDespesa> obterListaNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(FsoGrupoNaturezaDespesa grupoNaturezaDespesa, String filter);

	/**
	 * #46298 - Suggestion Box de Natureza de Despesa por Grupo Natureza de Despesa - Count
	 */
	public Long obterCountNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(FsoGrupoNaturezaDespesa grupoNaturezaDespesa, String filter);

	/**
	 * #46298 - Suggestion Box de Verba de Gestao - Lista
	 */
	public List<FsoVerbaGestao> obterListaVerbaGestaoAtivosPorSeqOuDescricao(String filter);

	/**
	 * #46298 - Suggestion Box de Verba de Gestao - Count
	 */
	public Long obterCountVerbaGestaoAtivosPorSeqOuDescricao(String filter);
}
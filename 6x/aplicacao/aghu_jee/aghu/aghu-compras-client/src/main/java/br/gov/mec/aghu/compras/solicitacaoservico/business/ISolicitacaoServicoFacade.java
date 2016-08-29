package br.gov.mec.aghu.compras.solicitacaoservico.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.compras.vo.LoteSolicitacaoServicoVO;
import br.gov.mec.aghu.compras.vo.SolServicoVO;
import br.gov.mec.aghu.compras.vo.SolicitacaoServicoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoSsJn;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface ISolicitacaoServicoFacade extends Serializable {

	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoPorNumeroOuDescricao(String filtro);

	public List<ScoSolicitacaoCompraServico> pesquisarSolServicosPorSolCompra(ScoSolicitacaoDeCompra solCompra);

	public void mensagemErroDuplicadoScoSolServicoCompras() throws ApplicationBusinessException;

	public void mensagemErroDuplicadoScoSolServicoComprasSolCompras() throws ApplicationBusinessException;

	public void inserirScoSolCompraServico(ScoSolicitacaoCompraServico solCompraServico) throws ApplicationBusinessException;

	public void excluirScoSolCompraServico(ScoSolicitacaoCompraServico solCompraServico) throws ApplicationBusinessException;

	public Long countSolicitacaoServicoAutorizarSs(LoteSolicitacaoServicoVO filtroPesquisa);

	/**
	 * Retorna lista paginada de solicitações de serviço conforme regras da tela
	 * de autorização
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoAutorizarSs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, LoteSolicitacaoServicoVO filtroPesquisa);

	/**
	 * Retorna quantidade de solicitações de serviço conforme regras da tela de
	 * encaminhar solicitações
	 * 
	 * @param filtroPesquisa
	 * @return Integer
	 */
	public Long countSolicitacaoServicoEncaminharSs(LoteSolicitacaoServicoVO filtroPesquisa);

	/**
	 * Retorna lista paginada de solicitações de serviço conforme regras da tela
	 * de encaminhamento
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoEncaminharSs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, LoteSolicitacaoServicoVO filtroPesquisa);

	/**
	 * Inativa uma lista de solicitações de serviço
	 * 
	 * @param listaSolicitacoes
	 * @param motivoExclusao
	 * @throws BaseException
	 */
	public void inativarListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacoes, String motivoInativacao) throws BaseException;

	/**
	 * Encaminha uma lista de solicitacoes de servico para o proximo ponto de
	 * parada
	 * 
	 * @param listaSolicitacoes
	 * @param pontoParadaAtual
	 * @param proximoPontoParada
	 * @param funcionarioComprador
	 * @param autorizacaoChefia
	 * @throws BaseException
	 */
	public void encaminharListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual,
			ScoPontoParadaSolicitacao proximoPontoParada, RapServidores funcionarioComprador, Boolean autorizacaoChefia)
			throws BaseException;

	public ScoSolicitacaoServico obterSolicitacaoServico(Integer numero);

	public ScoSolicitacaoServico obterSolicitacaoServicoOriginal(Integer numero);

	public List<ScoSolicitacaoCompraServico> pesquisarSolicitacaoDeCompraPorServico(ScoSolicitacaoServico solicitacaoServico);

	/**
	 * Autoriza uma lista de solicitacoes de servico
	 * 
	 * @param listaSolicitacoes
	 * @throws BaseException
	 */
	public void autorizarListaSolicitacaoServico(List<ScoSolicitacaoServico> listaSolicitacoes) throws BaseException;

	List<SolServicoVO> listarSolicitacoesDeServicos(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, ScoServico servico,
			Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente, ScoSolicitacaoServico solicitacaoServico,
			ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException;

	Long listarSolicitacoesDeServicosCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente,
			ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException;

	List<SolServicoVO> listarSolicitacoesDeServicosSemParametros(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException;

	Long listarSolicitacoesDeServicosCountSemParametros(List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException;

	void resgatarSs(Integer slsNumero) throws BaseException;

	/**
	 * Retorna uma lista de solicitacoes de serviço de determinado serviço,
	 * natureza de despesa e verba de gestão pesquisando por codigo e descrição
	 * da solicitação de serviço
	 * 
	 * @param filter
	 * @param servico
	 * @param naturezaDespesa
	 * @param verbaGestao
	 * @return
	 */
	public List<ScoSolicitacaoServico> pesquisarSolicServicoCodigoDescricao(Object filter, ScoServico servico,
			FsoNaturezaDespesa naturezaDespesa);

	public List<Integer> obterListaNumeroSs(List<ScoSolicitacaoServico> listaSs);
	
	public List<ScoServico> listarServicosAtivos(Object param);

	public Long listarServicosAtivosCount(Object param);

	public void persistirSolicitacaoDeServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoCompraClone)
			throws BaseException;

	public ScoSolicitacaoServico clonarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException;

	public void validaUrgentePrioritario(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException;

	public List<SolicitacaoServicoVO> obterRelatorioSolicitacaoServico(List<Integer> listaCodSS) throws ApplicationBusinessException;

	public List<ScoSsJn> listarPesquisaFasesSolicitacaoServico(Integer numero);

	public Long countPesquisaFasesSolicitacaoServico(Integer numero);

	public ScoSsJn obterFaseSolicitacaoServico(Integer numero, Short codigoPontoParada, Integer seq);

	public List<ScoFaseSolicitacao> listarDadosLicitacaoSs(Integer numero);

	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimentoSs(Integer numero);

	public boolean isReadonlyEdicao(ScoSolicitacaoServico solicitacaoServico);

	/**
	 * Verifica se o usuario logado possui alguma permissão associada à
	 * solicitação de serviço
	 * 
	 * @param usuarioLogado
	 * @param gravar
	 * @return Boolean
	 */
	public Boolean verificarPermissoesSolicitacaoServico(String login, Boolean gravar);

	public boolean verificaPemissaoUsuario(String permissao, String login) throws ApplicationBusinessException;

	List<SolServicoVO> listarSolicitacoesDeServicoCompradoreEngenharia(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, ScoServico servico,
			Date dtSolInicial, Date dtSolFinal, ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual,
			RapServidores servidorCompra, ScoModalidadeLicitacao modalidadeLicitacao) throws ApplicationBusinessException;

	Long listarSolicitacoesDeServicoCompradoreEngenhariaCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial, Date dtSolFinal,
			ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra,
			ScoModalidadeLicitacao modalidadeLicitacao) throws ApplicationBusinessException;

	List<ScoServico> listarServicos(Object param);

	/**
	 * Retorna uma lista de 10 solicitacoes de serviço que estao associadas a
	 * determinada natureza de despesa
	 * 
	 * @param id
	 * @param isExclusao
	 * @return List
	 */
	List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaNaturezaDespesa(FsoNaturezaDespesaId id, Boolean isExclusao);

	/**
	 * Retorna uma lista de 10 solicitacoes de serviço ativas em AF que estao
	 * associadas a natureza de despesa
	 * 
	 * @param id
	 * @return List
	 */
	List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaAutorizacaoFornEfetivada(FsoNaturezaDespesaId id);

	/**
	 * Retorna uma lista de até 10 solicitações de serviço associadas à verba de
	 * gestão
	 * 
	 * @param verbaGestao
	 * @return List
	 */
	List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada);

	/**
	 * Retorna o ponto de parada Autorização conforme cadastros de ponto de
	 * parada da solicitação
	 * 
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsAutorizacao();

	public ScoSolicitacaoServico duplicarSS(ScoSolicitacaoServico solicitacaoServico , boolean flagFlush,
			boolean isDuplicarPac, boolean manterCcOriginal) throws BaseException;

	public List<ScoSolicitacaoCompraServico> listarSolicitacaoCompraServico(ScoSolicitacaoDeCompra numeroSC, ScoSolicitacaoServico numeroSS);

	public Boolean verificarAcoesPontoParadaSs(Integer slsNumero, Date data);

	/**
	 * Atualiza a SS passando pelas validacoes necessarias
	 * 
	 * @param solicitacaoServico
	 * @param solicitacaoServicoClone
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException;

	/**
	 * Obter os servicos ativos do perfil de engenharia
	 * 
	 * @param codigo
	 *            ou nome do servico
	 * @param se
	 *            indEngenharia ou não
	 * @return lista de servicos
	 */
	public List<ScoServico> listarServicosAtivosEngenharia(Object param, String indEngenharia);

	/**
	 * Obter os servicos do perfil de engenharia
	 * 
	 * @param codigo
	 *            ou nome do servico
	 * @param se
	 *            indEngenharia ou não
	 * @return lista de servicos
	 */
	public List<ScoServico> listarServicosEngenharia(Object param, String indEngenharia);

	/**
	 * Obter a quantidade de servicos ativos do perfil de engenharia
	 * 
	 * @param codigo
	 *            ou nome do servico , se indEngenharia ou não
	 * @return lista de servicos
	 */
	public Long listarServicosAtivosEngenhariaCount(Object param, String indEngenharia);

	/**
	 * Obter a quantidade de servicos do perfil de engenharia
	 * 
	 * @param codigo
	 *            ou nome do servico, se indEngenharia ou não
	 * @return lista de servicos
	 */
	public Long listarServicosEngenhariaCount(Object param, String indEngenharia);

	public Boolean habilitarEncaminharSS(ScoSolicitacaoServico solicitacaoServico, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoEncaminhar, List<FccCentroCustos> listaCentroCustosUsuario);

	public Boolean habilitarAutorizarSS(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException;

	Boolean verificarPontoParadaChefia(List<ScoSolicitacaoServico> solicitacoes);

	public void devolverSolicitacoesServico(List<ScoSolicitacaoServico> listaSolicitacaoServico, String justificativa) throws BaseException;

	public void inserirJournalSS(ScoSsJn scoSsJn) throws ApplicationBusinessException;

	public void inserirSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico, RapServidores servidorLogado, boolean flagFlush,
			boolean duplicacao) throws BaseException;

	public void validarRegrasOrcamentarias(ScoSolicitacaoServico solicitacaoServico, ScoSolicitacaoServico solicitacaoServicoClone)
			throws BaseException;

	void desatacharSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico);
	
	Boolean verificarSolicitacaoDevolvidaAutorizacao(Integer numero);

	List<ScoServico> listarSuggestionServicos(Object objPesquisa);
	
	Long listarSuggestionServicosCount(Object pesquisa);
}

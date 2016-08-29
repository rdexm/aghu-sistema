package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO;
import br.gov.mec.aghu.compras.vo.PlanejamentoCompraVO;
import br.gov.mec.aghu.compras.vo.ProcessoGeracaoAutomaticaVO;
import br.gov.mec.aghu.compras.vo.RelatorioSolicitacaoCompraEstocavelVO;
import br.gov.mec.aghu.compras.vo.SolCompraVO;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoArquivoAnexo;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.model.ScoAutTempSolicitaId;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacaoID;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.suprimentos.vo.ScoPlanejamentoVO;


@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface ISolicitacaoComprasFacade extends Serializable {

	/**
	 * Pesquisa geral de registros na tabela {@code ScoAutTempSolicita}. Pode
	 * ser filtrada por parametros de entrada.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param autTempSol
	 * @return Listagem contendo os registros encontrados.
	 */
	List<ScoAutTempSolicita> pesquisarAutSolicitacaoTemp(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio);

	/**
	 * Faz a contagem de registros na tabela {@code ScoAutTempSolicita}.
	 * 
	 * @param autTempSol
	 * @return Inteiro indicando o numero de registros encontrados.
	 */
	Long pesquisarAutSolicitacaoTempCount(FccCentroCustos centroCusto, RapServidores servidor, Date dtInicio);

	ScoAutTempSolicita obterAutTempSolicitacao(ScoAutTempSolicitaId chavePrimaria);

	ScoAutTempSolicita obterScoAutTempSolicitaFull(ScoAutTempSolicitaId chavePrimaria);

	void cadastrarAutTempSolicitacao(ScoAutTempSolicita autTempSol) throws ApplicationBusinessException;

	void excluirAutTempSolicitacao(ScoAutTempSolicitaId id) throws ApplicationBusinessException;

	/**
	 * Pesquisa paginação de ScoPontoServidor
	 * 
	 * @param scoPontoParadaSolicitacao
	 * @param servidor
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<ScoPontoServidor> pesquisarScoPontoServidor(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, RapServidores servidor,
			RapServidores servidorAutorizado, Integer firstResult, Integer maxResults, String orderProperty, boolean asc);

	//#5228 - Cadastro de Caminhos da Solicitação
	List<ScoCaminhoSolicitacao> pesquisarCaminhoSolicitacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada);

	/**
	 * Conta paginação de ScoPontoServidor
	 * 
	 * @param scoPontoParadaSolicitacao
	 * @param servidor
	 * @return
	 */
	public Long pesquisarScoPontoServidorCount(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, RapServidores servidor,
			RapServidores servidorAutorizado);

	List<ScoDireitoAutorizacaoTemp> listarScoDireitoAutorizacaoTemp(ScoPontoServidor pontoServidor, RapServidores servidor);

	void validaDataInicioFimScoDireitoAutorizacaoTemp(Date dataInicio, Date dataFim) throws ApplicationBusinessException;

	void mensagemErroDuplicadoScoDireitoAutorizacaoTemp() throws ApplicationBusinessException;

	void inserirScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException;

	void alterarScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException;

	void excluirScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException;

	void validarConflitoPeriodoDatas(List<ItensScoDireitoAutorizacaoTempVO> listaGrade, ScoDireitoAutorizacaoTemp direitoAutorizacao,
			Integer idLista) throws ApplicationBusinessException;

	/**
	 * Retorna uma lista de solicitacoes de compras de determinado material,
	 * natureza de despesa e verba de gestão pesquisando por codigo e descrição
	 * da solicitação de compras
	 * 
	 * @param filter
	 * @param material
	 * @param naturezaDespesa
	 * @param verbaGestao
	 * @return List
	 */
	List<ScoSolicitacaoDeCompra> pesquisarSolicCompraCodigoDescricao(Object filter, ScoMaterial material, FsoNaturezaDespesa naturezaDespesa);

	Long pesquisarCaminhoSolicitacaoCount(ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada);

	ScoCaminhoSolicitacao obterCaminhoSolicitacao(ScoCaminhoSolicitacaoID chavePrimaria);

	/**
	 * Retorna uma lista de solicitacoes de compras que estao associadas a
	 * natureza de despesa
	 * 
	 * @param id
	 * @param isExclusao
	 * @return List
	 */
	List<ScoSolicitacaoDeCompra> buscarSolicitacaoCompraAssociadaNaturezaDespesa(FsoNaturezaDespesaId id, Boolean isExclusao);

	void inserirCaminhoSolicitacao(ScoCaminhoSolicitacao caminhoSolicitacao) throws ApplicationBusinessException;

	void excluirCaminhoSolicitacao(ScoCaminhoSolicitacaoID id) throws ApplicationBusinessException;

	Boolean verificarPermissoesSolicitacaoCompras(String login, Boolean gravar);

	/**
	 * Inativa uma lista de solicitações de compras
	 * 
	 * @param listaSolicitacoes
	 * @param motivoExclusao
	 * @param servidor
	 * @throws BaseException
	 */
	public void inativarListaSolicitacaoCompras(List<Integer> listaSolicitacoes, String motivoInativacao) throws BaseException;

	/**
	 * Encaminha uma lista de códigos de solicitação de compras para determinado
	 * ponto de parada
	 * 
	 * @param listaSolicitacoes
	 * @param pontoParadaAtual
	 * @param proximoPontoParada
	 * @param funcionarioComprador
	 * @param autorizacaoChefia
	 * @throws BaseException
	 */
	public void encaminharListaSolicitacaoCompras(List<Integer> listaSolicitacoes, ScoPontoParadaSolicitacao pontoParadaAtual,
			ScoPontoParadaSolicitacao proximoPontoParada, RapServidores funcionarioComprador, Boolean autorizacaoChefia)
			throws BaseException;

	/**
	 * Retorna lista de solicitações de compras conforme preenchimento do VO
	 * filtroPesquisa. Utilizado na tela de encaminhar solicitações de compras
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarLoteSolicitacaoCompras(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa);

	List<Integer> obterListaNumeroSc(List<ScoSolicitacaoDeCompra> listaSc);
	
	List<ScoLogGeracaoScMatEstocavel> pesquisarLogGeracaoScMaterialEstocavel(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato);

	Long contarLogGeracaoScMaterialEstocavel(ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato);

	List<ProcessoGeracaoAutomaticaVO> pesquisarProcessoGeracaoCodigoData(Object param);

	Boolean verificarAcoesPontoParadaSc(Integer slcNumero, Date data);

	void persistirScoAcoesPontoParada(ScoAcoesPontoParada acao);

	void removerScoAcoesPontoParada(Long seq);

	/**
	 * Retorna a quantidade de solicitações de compras conforme preenchimento do
	 * VO filtroPesquisa. Utilizado na tela de encaminhar solicitações de
	 * compras
	 * 
	 * @param filtroPesquisa
	 * @param servidor
	 * @return Integer
	 */
	public Long countLoteSolicitacaoCompras(PesqLoteSolCompVO filtroPesquisa);

	List<SolCompraVO> listarSolicitacoesDeCompras(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada,
			ScoMaterial material, Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente, ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoPontoParadaSolicitacao pontoParadaAtual) throws ApplicationBusinessException;

	/**
	 * Pesquisa solicitacoes de compras ativas associadas a um item de af
	 * 
	 * @param itemAfId
	 * @return List
	 */
	List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPorItemAf(ScoItemAutorizacaoFornId itemAfId);

	Long listarSolicitacoesDeComprasCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial, Date dtSolFinal, DominioSimNao pendente,
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual)
			throws ApplicationBusinessException;

	List<SolCompraVO> listarSolicitacoesDeComprasSemParametros(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos) throws ApplicationBusinessException;

	Long listarSolicitacoesDeComprasCountSemParametros(List<FccCentroCustos> listaCentroCustos)
			throws ApplicationBusinessException;
	

	public List<ScoMaterial> listarMateriaisAtivos(String param, String servidor);

	public Long listarMateriaisAtivosCount(String param, String servidor);

	public List<ScoMaterial> listarMateriaisSC(String param);


	/**
	 * Método que persiste a SC que envolve estórias da Manter Solicitação de
	 * Compras
	 * 
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraClone
	 * @throws BaseException
	 * 
	 */
	public void persistirSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoCompra, ScoSolicitacaoDeCompra solicitacaoCompraClone)
			throws BaseException;

	/**
	 * Insere SC.
	 * 
	 * Usado em operações internas do sistema, ignora validações de perfil,
	 * material genérico, etc.
	 * 
	 * @param solicitacaoDeCompra
	 * @throws BaseException
	 */
	void inserirScoSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws BaseException;

	/**
	 * Atualiza SC.
	 * 
	 * Usado em operações internas do sistema, ignora validações de perfil,
	 * material genérico, etc.
	 * 
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @param servidorLogado 
	 * @throws BaseException
	 */
	void atualizarScoSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws BaseException;

	/**
	 * Busca uma lista de 10 verbas de gestões associadas à SCs
	 * 
	 * @param verbaGestao
	 * @return
	 */
	List<ScoSolicitacaoDeCompra> buscarSolicitacaoComprasAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada);

	public ScoSolicitacaoDeCompra obterSolicitacaoDeCompra(Integer numero);

	public ScoSolicitacaoDeCompra obterSolicitacaoDeCompraOriginal(Integer numero);

	public ScoSolicitacaoDeCompra clonarSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoCompra) throws ApplicationBusinessException;

	public void duplicarSC(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean mantemCcOriginal) throws BaseException;
	
	public ScoSolicitacaoDeCompra duplicarSCPorPAC(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException;

	/**
	 * Retorna uma lista de solicitacoes de compras ativas em AF que estao
	 * associadas a natureza de despesa
	 * 
	 * @param id
	 * @return List
	 */
	List<ScoSolicitacaoDeCompra> buscarSolicitacaoCompraAssociadaAutorizacaoFornEfetivada(FsoNaturezaDespesaId id);

	public boolean isReadonlyEdicao(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoGeral);

	/**
	 * RN 36 Não permitir devolução com pontos de parada (atual e anterior)
	 * iguais
	 * 
	 * @param solicitacaoDeCompra
	 * @param usuarioLogado
	 * @return
	 */
	public boolean isReadonlyDevolucao(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoGeral);

	public boolean isRequeridDescricaoCompra(ScoMaterial scoMaterial) throws ApplicationBusinessException;

	public Double getUltimoValorCompra(ScoMaterial scoMaterial) throws ApplicationBusinessException;

	/**
	 * Verifica se determinado link deve ser mostrado ou nao na tela
	 * 
	 * @param listaControle
	 * @param item
	 * @param verificarParcela
	 * @param verificarLibRef
	 * @param verificarAf
	 * @param verificarLibAss
	 * @param protegerQtde
	 * @return Boolean
	 */
	Boolean verificarHabilitacaoCamposAf(List<PlanejamentoCompraVO> listaControle, ScoSolicitacaoDeCompra item, Boolean verificarParcela,
			Boolean verificarLibRef, Boolean verificarAf, Boolean verificarLibAss, Boolean protegerQtde);

	/**
	 * Obtem a posicao do item da SC na lista de PlanejamentoCompraVO
	 * 
	 * @param item
	 * @param listaControle
	 * @return Integer
	 */
	Integer obterIndiceListaControle(ScoSolicitacaoDeCompra item, List<PlanejamentoCompraVO> listaControle);

	/**
	 * Obtem a posicao do item da SC na lista de ScoPlanejamentoVO
	 * 
	 * @param item
	 * @param listAlteracoes
	 * @return Integer
	 */
	Integer obterIndiceLista(ScoSolicitacaoDeCompra item, List<ScoPlanejamentoVO> listAlteracoes);

	/**
	 * Baseado numa SC cria um PlanejamentoCompraVO
	 * 
	 * @param scoItem
	 * @return PlanejamentoCompraVO
	 */
	PlanejamentoCompraVO preencherControleVO(ScoSolicitacaoDeCompra scoItem);

	/**
	 * Calcula o saldo das parcelas baseado numa SC
	 * 
	 * @param scoItem
	 * @param listaControle
	 * @return Integer
	 */
	Integer obterQtdSaldoParcelas(ScoSolicitacaoDeCompra scoItem, List<PlanejamentoCompraVO> listaControle);

	/**
	 * Consulta de arquivos relacionados à etapa do processo de compras, levando
	 * em consideração sua hierarquia Ex: Consulta de arquivos relacionados à
	 * solicitação de compra e ao material, ou arquivos relacionados ao PAC,
	 * suas solicitações compra ou serviço e seus materiais ou serviços
	 * 
	 * @param origemSolicitacao
	 * @param numeroSolicitacao
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<ScoArquivoAnexo> pesquisarArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origemSolicitacao,
			Integer numeroSolicitacao);

	/**
	 * Consulta número de arquivos relacionados à etapa do processo de compras,
	 * levando em consideração sua hierarquia Ex: Consulta número de arquivos
	 * relacionados à solicitação de compra e ao material, ou número de arquivos
	 * relacionados ao PAC, suas solicitações compra ou serviço e seus materiais
	 * ou serviços
	 * 
	 * @param origem
	 *            da consulta
	 * @param numero
	 * @return numero de arquivos relacionados
	 */
	public Long pesquisarArquivosPorNumeroOrigemCount(DominioOrigemSolicitacaoSuprimento origem, Integer numero);

	void resgatarSc(Integer slcNumero) throws BaseException;

	/**
	 * Verifica a existência de arquivos relacionados à etapa do processo de
	 * compras, levando em consideração sua hierarquia Ex: Consulta existência
	 * de arquivos relacionados à solicitação de compra e ao material, ou
	 * existência de arquivos relacionados ao PAC, suas solicitações compra ou
	 * serviço e seus materiais ou serviços
	 * 
	 * @param origem
	 *            da consulta
	 * @param numero
	 * @return numero de arquivos relacionados
	 */
	public Boolean verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento origem, Integer numero);

	/**
	 * Baseado numa SC monta um ScoPlanejamentoVO
	 * 
	 * @param item
	 * @return ScoPlanejamentoVO
	 */
	ScoPlanejamentoVO montarItemObjetoVO(ScoSolicitacaoDeCompra item);

	/**
	 * Obtem a descricao do ponto de parada
	 * 
	 * @param item
	 * @param proximo
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	String obterDescricaoPontoParada(ScoSolicitacaoDeCompra item, Boolean proximo) throws ApplicationBusinessException;

	/**
	 * Inclui um anexo a determinada fase do processo de compras
	 * 
	 * @param arquivoAnexo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void incluirArquivoAnexo(ScoArquivoAnexo arquivoAnexo) throws ApplicationBusinessException;

	/**
	 * Exclui um anexo de determinada fase do processo de compras
	 * 
	 * @param seqArquivo
	 * @throws ApplicationBusinessException
	 */
	public void excluirArquivoAnexo(Long seqArquivo) throws ApplicationBusinessException;

	/**
	 * Altera um anexo de determinada fase do processo de compras
	 * 
	 * @param arquivoAnexo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void alterarArquivoAnexo(ScoArquivoAnexo arquivoAnexo) throws ApplicationBusinessException;

	/**
	 * Retorna o conteudo do parametro tamanhoMaximoPermitido para arquivos
	 * anexos
	 * 
	 * @return BigDecimal
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal tamanhoMaximoPermitido() throws ApplicationBusinessException;

	/**
	 * Retorna lista de solicitações de compras conforme regras para autorização
	 * da SC.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasAutorizarSc(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, PesqLoteSolCompVO filtroPesquisa);

	/**
	 * Recebendo um Map com uma lista de Solicitações de Compra, verifica se
	 * todas as solicitações marcadas (value = true) estão no Ponto de Parada
	 * chefia (DominioTipoPontoParada.CH)
	 * 
	 * @param solicitacoes
	 * @return boolean
	 */
	public Boolean verificarPontoParadaChefia(List<ScoSolicitacaoDeCompra> solicitacoes);

	/**
	 * Retorna quantidade de solicitações de compras conforme regras para
	 * autorização da SC.
	 * 
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long countSolicitacaoComprasAutorizarSc(PesqLoteSolCompVO filtroPesquisa);

	/**
	 * Autoriza uma lista de codigos de solicitacoes de compras
	 * 
	 * @param listaSolicitacoes
	 * @throws BaseException
	 */
	void autorizarListaSolicitacaoCompras(List<Integer> listaSolicitacoes) throws BaseException;

	public void validaQtdeSolicitadaAprovada(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException;

	public void alteraQuantidadeAprovada(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException;

	public void validaUrgentePrioritario(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException;

	/**
	 * Retorna solicitações de compras conforme regras da tela de solicitacao de
	 * compras para liberação.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasLiberacaoSc(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, PesqLoteSolCompVO filtroPesquisa);

	//#5262 - Fases de Solicitação de Compra		 
	public ScoScJn obterFaseSolicitacaoCompra(Integer numero, Short codigoPontoParada, Integer seq);

	public Long countPesquisaFasesSolicitacaoCompra(Integer numero);

	public ScoFaseSolicitacao obterDadosAutorizacaoFornecimento(Integer numero);

	public ScoItemLicitacao obterDataDigitacaoPublicacaoLicitacao(Integer lctNumero, Short numero);

	public ScoItemAutorizacaoForn obterDadosItensAutorizacaoFornecimento(Integer afNumero, Integer numero);

	/**
	 * Retorna quantidade de solicitações de compras conforme regras da tela de
	 * solicitacao de compras para liberação.
	 * 
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long countSolicitacaoComprasLiberacaoSc(PesqLoteSolCompVO filtroPesquisa);

	/**
	 * Retorna o ponto de parada da "chefia"
	 * 
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsAutorizacao();

	/**
	 * Retorna lista de solicitações de compras conforme regras de visualização
	 * do planejamento
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPlanejamentoSc(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, PesqLoteSolCompVO filtroPesquisa, Boolean pesquisarScMaterialEstocavel);

	/**
	 * Retorna quantidade de solicitações de compras conforme regras da tela de
	 * planejamento
	 * 
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long countSolicitacaoComprasPlanejamentoSc(PesqLoteSolCompVO filtroPesquisa, Boolean pesquisarScMaterialEstocavel);

	/**
	 * Dispara manualmente o processo de geracao de SC automatica para material
	 * estocavel para o almoxarifado central
	 * 
	 * @param dtAnalise
	 * @return
	 * @throws BaseException
	 */
	List<String> gerarSolicitacaoCompraAlmox(Date dtAnalise) throws BaseException;

	/**
	 * Dispara manualmente o processo de geracao de SC automatica para material
	 * estocavel por almoxarifado
	 * 
	 * @param dtAnalise
	 * @param almox
	 * @return
	 * @throws BaseException
	 */
	List<String> gerarSolicitacaoCompraAlmox(Date dtAnalise, SceAlmoxarifado almox) throws BaseException;

	/**
	 * Atualiza a SC com os dados editados na grade da tela do planejamento,
	 * atualizando os dados da AF e gerando parcelas de entrega quando
	 * necessario
	 * 
	 * @param listaAlteracoes
	 * @param nroLibRefs
	 * @param nroLibAss
	 * @throws BaseException
	 */
	void atualizarPlanejamentoSco(List<ScoPlanejamentoVO> listaAlteracoes, List<Integer> nroLibRefs, List<Integer> nroLibAss)
			throws BaseException;

	/**
	 * Verifica se a solicitacao de compra esta vinculada à alguma AF
	 * 
	 * @param solicitacao
	 * @return Boolean
	 */
	public Boolean verificarAutorizacaoFornecimentoVinculada(ScoSolicitacaoDeCompra solicitacao);

	List<SolCompraVO> listarSolicitacoesDeComprasComprador(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada,
			ScoMaterial material, Date dtSolInicial, Date dtSolFinal, ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra, Boolean isPerfilGeral)
			throws ApplicationBusinessException;

	Long listarSolicitacoesDeComprasCompradorCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto,
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial, Date dtSolFinal,
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra,
			Boolean isPerfilGeral) throws ApplicationBusinessException;

	public boolean verificaPemissaoUsuario(String permissao, String servidor) throws ApplicationBusinessException;

	public Boolean verificarFiltroPlanejamentoVazio(PesqLoteSolCompVO filtroPesquisa);

	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoCompraPorNumeroOuDescricao(Object pesquisa, Boolean filtrarAssociadas);

	public List<RelatorioSolicitacaoCompraEstocavelVO> pesquisarSolicitacaoMaterialEstocavel(Date dtInicial, Date dtFinal,
			Integer numSolicitacao, Date dataCompetencia) throws ApplicationBusinessException;

	public List<ScoPropostaFornecedor> listarDataDigitacaoPropostaForn(Integer numero);

	public List<ScoFaseSolicitacao> listarDadosLicitacao(Integer numero);

	public List<ScoScJn> listarPesquisaFasesSolicitacaoCompra(Integer numero);

	public List<ScoAcoesPontoParada> listarAcoesPontoParada(Integer numero, Short codigoPontoParada, DominioTipoSolicitacao tipoSolicitacao);

	public List<String> gerarScRepAutomatica(Date dtAnalise, SceAlmoxarifado almox) throws BaseException;

	/**
	 * Atualiza o parametro cadastrado como horario para execucao automatica da
	 * geracao de SC para material estocavel
	 * 
	 * @param horaAgendamento
	 * @throws BaseException
	 */
	public void atualizarHorarioAgendamentoGeracaoAutomaticaSolCompras(Date horaAgendamento) throws BaseException;

	/**
	 * Obtem o parametro cadastrado como horario para execucao automatica da
	 * geracao de SC para material estocavel
	 * 
	 * @return Date
	 * @throws ApplicationBusinessException
	 * @throws ParseException
	 */
	public Date carregarHorarioAgendamentoGeracaoAutomaticaSolCompras() throws ApplicationBusinessException, ParseException;

	/**
	 * Atualiza o parametro cadastrado como horario para execucao automatica da
	 * geracao de SC para material estocavel
	 * 
	 * @param msg
	 * @throws ApplicationBusinessException
	 */
	public void atualizarUltimaExecucaoGeracaoAutomaticaSolCompras(String msg) throws ApplicationBusinessException;

	List<ScoSolicitacaoDeCompra> obterScoSolicitacoesDeCompras(Object param);

	/**
	 * Obtem natureza de despesa cadastrada no grupo do material.
	 * 
	 * @param grupoNatureza
	 *            Grupo da Natureza
	 * @param material
	 *            Material
	 * @return Natureza
	 */
	public FsoNaturezaDespesa obterNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNatureza, ScoMaterial material);

	/**
	 * Valida valores orçamentários de uma SC.
	 * 
	 * @param sc
	 *            SC
	 * @throws BaseException
	 */
	void validarRegrasOrcamentarias(ScoSolicitacaoDeCompra sc) throws BaseException;

	public Boolean isPerfilComprador(ScoSolicitacaoDeCompra solicitacaoDeCompra, String login);

	public Boolean isPerfilPlanejamento(ScoSolicitacaoDeCompra solicitacaoDeCompra, String login);

	public Boolean isPerfilGeral(ScoSolicitacaoDeCompra solicitacaoDeCompra, String login);

	public Boolean habilitarAutorizarSC(ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException;

	Boolean habilitarEncaminharSC(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador,
			Boolean temPermissaoPlanejamento, Boolean temPermissaoEncaminhar, List<FccCentroCustos> listaCentroCustosUsuario);

	void verificarMaterialSelecionado(ScoMaterial material, Boolean temPermissaoCadastrar, Boolean temPermissaoChefia,
			Boolean temPermissaoAreasEspecificas, Boolean temPermissaoGeral, Boolean temPermissaoPlanejamento)
			throws ApplicationBusinessException;

	 Boolean bloqueiaSolicitacaoComprasMatEstocavel(ScoMaterial material,
				Boolean temPermissaoCadastrar, Boolean temPermissaoChefia, Boolean temPermissaoAreasEspecificas,
				Boolean temPermissaoGeral, Boolean temPermissaoPlanejamento);
	/**
	 * Devolve solicitações de compras.
	 * 
	 * @param nroScos
	 *            ID's das SC's a serem devolvidas.
	 * @param justificativaDevolucao
	 *            Justificativa para devolução.
	 */
	void devolverSolicitacoesCompras(List<Integer> nroScos, String justificativaDevolucao) throws BaseException;

	public void inserirJournalSC(ScoSolicitacaoDeCompra solicitacaoDeCompra, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException;

	/**
	 * Obtem data da ultima execucao do processo
	 * 
	 * @return Date
	 */
	public Date obterDataUltimaExecucao();

	/** Obtem último processo de geração. */
	public ProcessoGeracaoAutomaticaVO obterUltimoProcessoGeracao();

	/**
	 * Obtem SC a partir do log de geração.
	 * 
	 * @param item
	 *            Log
	 * @return SC
	 */
	Integer obterSolicitacaoDeCompra(ScoLogGeracaoScMatEstocavel item);

	/**
	 * Obtem SCs de material que não estão em AF.
	 * 
	 * @param material
	 *            Material
	 * @return SCs
	 */
	List<ScoSolicitacaoDeCompra> pesquisarScsNaoAf(ScoMaterial material);

	boolean isRequiredCcProjeto(FccCentroCustos centroCusto) throws ApplicationBusinessException;

	void desatacharSolicitacaoCompras(ScoSolicitacaoDeCompra solicitacaoDeCompra);

	Long pesquisarMaterialReposicaoCount(FiltroReposicaoMaterialVO filtro);

	void gerarParcelasPorScsMateriaisDiretos(List<Integer> listaSolicitacoes) throws ApplicationBusinessException;

	ScoParamProgEntgAf obterScoParamProgEntgAfPorSolicitacaoDeCompra(Integer numero);

	void excluirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega);

	void persistirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega, Boolean inserir) throws ApplicationBusinessException;
	
	void inserirSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra)	throws BaseException;
	
	/**
	 * Verificar compras Web.
	 * 
	 * @param material ScoMaterial
	 * @param param AghParametros
	 * @return Boolean
	 */	
	
	Boolean verificarComprasWeb(AghParametros param, ScoMaterial material);
	
	Boolean verificarSolicitacaoDevolvidaAutorizacao(Integer numero);

	List<ScoMaterial> listarMateriaisSC(Object param, Integer gmtCodigo, Short almCodigo);

	Integer listarMateriaisSCCount(String param, Integer gmtCodigo);
}

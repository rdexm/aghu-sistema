package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.compras.vo.ScoRamoComercialCriteriaVO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornRamoComercialId;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoJustificativaPreco;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.model.ScoMotivoCancelamentoItem;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Interface para facade do submudulo de cadastros basicos do modulo de compras.
 */
@SuppressWarnings({"PMD.ExcessiveClassLength"})
public interface IComprasCadastrosBasicosFacade extends Serializable {
	ScoRamoComercial obterScoRamoComercial(Short codigo);
	
	List<ScoRamoComercial> pesquisarScoRamosComerciais(
			ScoRamoComercialCriteriaVO criteria, 
			int firstResult, int maxResults, 
			String orderProperty, boolean asc);
	
	Boolean verificarProtocoloPac(RapServidores servidorLogado);
	
	Long contarScoRamosComerciais(ScoRamoComercialCriteriaVO criteria);
	
	void persistir(ScoRamoComercial ramo) throws ApplicationBusinessException;
	
	/**
	 * Pesquisa geral de registros na tabela {@code ScoUnidadeMedida}. Pode ser
	 * filtrada por par�metros de entrada.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unidadeMedida
	 * @return Listagem contendo os registros encontrados.
	 */
	List<ScoUnidadeMedida> pesquisarUnidadeMedida(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			ScoUnidadeMedida unidadeMedida);

	/**
	 * Retorna a contagem de registros de Unidade de Medida para o paginator.
	 * 
	 * @param unidadeMedida
	 * @return número total de registros encontrados
	 */
	Long listarUnidadeMedidaCount(ScoUnidadeMedida unidadeMedida);

	/**
	 * Alterra ou Insere um novo registro de Exercicio.
	 * 
	 * @param unidadeMedida
	 * @throws ApplicationBusinessException
	 */
	void cadastrarUnidadeMedida(ScoUnidadeMedida unidadeMedida)
			throws ApplicationBusinessException;

	/**
	 * Obtém o registro de {@code ScoUnidadeMedida} a partir do
	 * {@code codigo}.
	 * 
	 * @param codigo
	 * @return Registro não encontrado.
	 */
	ScoUnidadeMedida obterUnidadeMedida(String codigo);

	List<ScoFormaPagamento> pesquisarFormasPagamento(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			final ScoFormaPagamento scoFormaPagamento);

	Long pesquisarFormasPagamentoCount(
			final ScoFormaPagamento scoFormaPagamento);

	ScoFormaPagamento obterFormaPagamento(Short codigo);

	void inserirFormaPagamento(ScoFormaPagamento scoFormaPagamento)
			throws ApplicationBusinessException;

	void alterarFormaPagamento(ScoFormaPagamento scoFormaPagamento)
			throws ApplicationBusinessException;

	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaProposta();
	
	// Estoria #5609
	List<ScoJustificativa> pesquisarJustificativas(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			final ScoJustificativa comprasJustificativa);

	public Long pesquisarJustificativaCount(
			final ScoJustificativa comprasJustificativa);

	public ScoJustificativa obterJustificativa(final Short codigo);

	public void inserirJustificativa(final ScoJustificativa comprasJustificativa)
			throws ApplicationBusinessException;

	public void alterarJustificativa(final ScoJustificativa comprasJustificativa)
			throws ApplicationBusinessException;

	//Motivo Alteração AF
	public Long pesquisarScoMotivoAlteracaoAfCount(ScoMotivoAlteracaoAf motivoAlteracao);

	public List<ScoMotivoAlteracaoAf> pesquisarScoMotivoAlteracaoAf(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, ScoMotivoAlteracaoAf motivoAlteracao);
	
	public ScoMotivoAlteracaoAf obterScoMotivoAlteracaoAf(Short codigo);
	
	public void persistirScoMotivoAlteracaoAf(ScoMotivoAlteracaoAf motivoAlteracao) throws ApplicationBusinessException;
	
	// Estoria #5608 - Características do usuário X centro de custo
	public List<ScoCaracteristicaUsuarioCentroCusto> pesquisarCaracteristicaUserCC(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc,
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC);

	public Long pesquisarCaracteristicaUserCCCount(
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC);

	ScoCaracteristicaUsuarioCentroCusto obterCaracteristicaUserCC(final Integer seq);

	ScoDireitoAutorizacaoTemp obterDireitoAutorizacaoTemporarioPorId(ScoDireitoAutorizacaoTemp direito);
	
	public void inserirCaracteristicaUserCC(
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC)
			throws ApplicationBusinessException;

	public void alterarCaracteristicaUserCC(
			final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC)
			throws ApplicationBusinessException;

	public void excluirCaracteristicaUserCC(final ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC)
			throws ApplicationBusinessException;

	public List<ScoCaracteristica> pesquisarCaracteristicasPorCodigoOuDescricao(
			Object objPesquisa);

	public Long pesquisarCaracteristicasPorCodigoOuDescricaoCount(
			Object objPesquisa);    
	
	public ScoPontoServidor obterPontoParadaServidorCodigoMatriculaVinculo(
			Short codigo, Short vinculo, Integer matricula);
	
	// Estoria #5227 - Pontos de Parada da Solicitação
	ScoPontoParadaSolicitacao obterPontoParada(final Short codigo);

	List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, 
			final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao);

	Long pesquisarPontoParadaSolicitacaoCount(final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao);

	void inserirPontoParadaSolicitacao(final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao)
			throws ApplicationBusinessException;

	void alterarPontoParadaSolicitacao(final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, final ScoPontoParadaSolicitacao scoPontoParadaSolAnterior)
			throws ApplicationBusinessException;

	void excluirPontoParadaSolicitacao(final Short scoPontoParadaSolicitacao) throws ApplicationBusinessException;
	
	List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorEnviadoPara(Short ppsEnviadoPara);
	
	List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(String filtro);
	
	List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos(String filtro);
	
	Long pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(String filtro);

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao (
			String filtro, Boolean fromLiberacao) throws ApplicationBusinessException;
	
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao (
			String filtro, Short pontoOrigem);
	
	 /**
     * lista ScoPontoParadaSolicitacao por codigo ou descrição para SB
     * @param pesquisa
     * @return
     */
	public List<ScoPontoParadaSolicitacao> listarPontoParadaPontoServidor(Object pesquisa);
	
	public List<ScoPontoParadaSolicitacao> listarPontoParadaSolicitacao(Object pesquisa);
	
	public Boolean verificarPontoParadaPermitido(ScoPontoParadaSolicitacao pontoParada);
	
	public Boolean verificarPontoParadaComprador(ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException;
	
	public ScoPontoParadaSolicitacao obterPontoParadaAutorizacao() throws ApplicationBusinessException;
	
	// Estoria #22310 - Parâmetros de Autorização da Solicitação de Compra
	ScoParamAutorizacaoSc obterParamAutorizacaoSc(final Integer seq);
	
	ScoParamAutorizacaoSc obterParametrosAutorizacaoSCPrioridade (FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicacao,
			RapServidores servidor);

	List<ScoParamAutorizacaoSc> pesquisarParamAutorizacaoSc(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, 
			final ScoParamAutorizacaoSc scoParamAutorizacaoSc);

	Long pesquisarParamAutorizacaoScCount(final ScoParamAutorizacaoSc scoParamAutorizacaoSc);

	void inserirParamAutorizacaoSc(final ScoParamAutorizacaoSc scoParamAutorizacaoSc)
			throws ApplicationBusinessException;

	void alterarParamAutorizacaoSc(final ScoParamAutorizacaoSc scoParamAutorizacaoSc)
			throws ApplicationBusinessException;
		
	ScoPontoParadaSolicitacao obterPontoParadaPorTipo(DominioTipoPontoParada tipoPontoParada);
	
	/**
	 * Estoria 5232 - RN4
	 * 
	 * @throws BaseException
	 */
	void enviarSolicitacaoCompras(ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoSolicitacaoDeCompra solicitacaoDeCompraOld,
			ScoPontoParadaSolicitacao pontoParadaDestino) throws BaseException;
	
	// Estoria #5600 - Locais dos Processos
	ScoLocalizacaoProcesso obterLocalizacaoProcesso(final Short codigo);
		
	List<ScoLocalizacaoProcesso> listarLocalizacaoProcesso(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, 
			final ScoLocalizacaoProcesso scoLocalizacaoProcesso);

	Long listarLocalizacaoProcessoCount(final ScoLocalizacaoProcesso scoLocalizacaoProcesso);

	void inserirLocalizacaoProcesso(final ScoLocalizacaoProcesso scoLocalizacaoProcesso)
			throws ApplicationBusinessException;

	void alterarLocalizacaoProcesso(final ScoLocalizacaoProcesso scoLocalizacaoProcesso)
			throws ApplicationBusinessException;
	
	void excluirLocalizacaoProcesso(final Short codigo) throws ApplicationBusinessException;
	
	/**
	 * Obtém uma unidade(s) de medida ativa(s) por código ou descrição
	 * 
	 * @param parametro
	 * @return
	 */
	List<ScoUnidadeMedida> pesquisarUnidadeMedidaAtivaPorCodigoDescricao(Object parametro);
	
	/**
	 * Obtém uma unidade de medida por código ou descrição
	 * 
	 * @param parametro
	 * @return
	 */
	List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object parametro);
	
	Long pesquisarUnidadeMedidaPorCodigoDescricaoCount(Object param);
	
	List<ScoUnidadeMedida> obterUnidadesMedida(String objPesquisa);
	
	List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorCodigoDescricao(Object objPesquisa, boolean apenasAtivos);
	
	List<ScoUnidadeMedida> pesquisarUnidadeMedidaPorSigla(Object objPesquisa, boolean apenasAtivos);
	
	Boolean isQuantidadePontoParadaServidorMaiorQueUm(Integer matricula,
			Short vinculo);
	
	Long pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(
			final Short codigo, final Integer matricula, final Short vinculo);
	
	List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricao(
			final Object parametro, final Boolean indAtivo);
	
	// Estoria #5601 - Tempos Localização Pac
	ScoTempoAndtPac obterTempoAndtPac(final ScoTemposAndtPacsId chavePrimaria);
	ScoTempoAndtPac obterPorChavePrimariaSemLazy(ScoTemposAndtPacsId chavePrimaria);
	
	List<ScoTempoAndtPac> listarTempoAndtPac(
			    Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
				final ScoModalidadeLicitacao modalidadeLicitacao, 
				final ScoLocalizacaoProcesso localizacaoProcesso, 
				final ScoTempoAndtPac tempolocalizacaoPac);

	Long listarTempoAndtPacCount(final ScoModalidadeLicitacao modalidadeLicitacao, 
				final ScoLocalizacaoProcesso localizacaoProcesso, final ScoTempoAndtPac tempoLocalizacaoPac);

	void inserirTempoAndtPac(final ScoTempoAndtPac tempoLocalizacaoPac)
				throws ApplicationBusinessException;

	void alterarTempoAndtPac(final ScoTempoAndtPac tempoLocalizacaoPac)
				throws ApplicationBusinessException;
		
	void excluirTempoAndtPac(final ScoTemposAndtPacsId id) throws ApplicationBusinessException;
				
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos(String filtro, Boolean isPerfilGeral);

	public Long pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivosCount(String filtro, Boolean isPerfilGeral);
	
	ScoModalidadeLicitacao obterScoModalidadeLicitacaoPorChavePrimaria(
			String codigo);

	public List<ScoModalidadeLicitacao> listarModalidadeLicitacaoAtivas(Object pesquisa);

	/**
	 * Retorna lista de ScoModalidadeLicitacao de acordo com o parametro
	 * informado
	 * 
	 * @param parametro
	 * @return
	 */
	List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(
			Object parametro);

	public List<ScoModalidadeLicitacao> obterModalidadesLicitacaoAprovadasPorCodigo(String codigo);
	
	// #5599 - Modalidade Pac
	public List<ScoModalidadeLicitacao> listarModalidadeLicitacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, 
			final ScoModalidadeLicitacao scoModalidadePac);

	public Long listarModalidadeCount(String filter);

	public Long listarModalidadeLicitacaoCount(final ScoModalidadeLicitacao scoModalidadePac);
	
	public ScoModalidadeLicitacao obterModalidadeLicitacao(final String codigo);
	
	public void inserirModalidadeLicitacao(final ScoModalidadeLicitacao scoModalidadePac)
			throws ApplicationBusinessException;

	public void alterarModalidadeLicitacao(final ScoModalidadeLicitacao scoModalidadePac)
			throws ApplicationBusinessException;
			
	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaProposta (
		    Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
		    Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio);

	public Long pesquisarCriterioEscolhaPropostaCount(Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio);

	public ScoCriterioEscolhaProposta obterCriterioEscolhaProposta(Short codigoCriterio);
	
	public void persistirCriterioEscolhaProposta(ScoCriterioEscolhaProposta criterioEscolhaProposta)
			throws ApplicationBusinessException;
	
	public void excluirCriterioEscolhaProposta(Short criterioEscolha)
			throws ApplicationBusinessException;

	/**
	 * Retorna lista de ScoModalidadeLicitacao de acordo com o parametro
	 * informado. Se o parâmetro situacao estiver NULL, não é considerado na
	 * pesquisa.
	 * 
	 * @param parametro
	 * @param situacao
	 * @return
	 */
	List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacaoPorCodigoDescricao(Object parametro, DominioSituacao situacao);

	Long listarJustificativasPrecoContratadoCount(ScoJustificativaPreco justificativa);

	List<ScoJustificativaPreco> pesquisarJustificativasPrecoContratado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoJustificativaPreco justificativa);

	void persistirNivelJustificativaPreco(ScoJustificativaPreco justificativa);

	ScoJustificativaPreco obterJustificativaPorCodigo(Short codigo);

	void persistirScoMotivoCancelamentoItem(ScoMotivoCancelamentoItem motivoCancel) throws ApplicationBusinessException;

	ScoMotivoCancelamentoItem obterScoMotivoCancelamentoItem(String codigo);

	Long pesquisarScoMotivoCancelamentoItemCount(ScoMotivoCancelamentoItem motivoCancel);

	List<ScoMotivoCancelamentoItem> pesquisarScoMotivoCancelamentoItem(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoMotivoCancelamentoItem motivoCancel);

	FccCentroCustos obterCcAplicacaoAlteracaoRmGppg(RapServidores servidorLogado);
	
	FccCentroCustos obterCcAplicacaoAlteracaoRmGppg();

	ScoPontoParadaSolicitacao obterScoPontoParadaSolicitacaoPorChavePrimaria(Short codigo);

	Long listarModalidadeLicitacaoAtivasCount(Object pesquisa);
	
	List<ScoModalidadeLicitacao> listarModalidadeLicitacao(Object pesquisa);

	ScoCaracteristicaUsuarioCentroCusto montarScoCaracUsuario(DominioCaracteristicaCentroCusto preencherCcSolicRm);

	ScoCaracteristicaUsuarioCentroCusto obterCaracteristica(DominioCaracteristicaCentroCusto preencherCcSolicRm);
	
	// Implementação das interfaces referentes ao CRUD de agencia
	

	public List<FcpAgenciaBanco> pesquisarListaAgencia(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short codBanco, Integer codigoAgencia) throws BaseException;
	
	/**
	 * Obter count lista de agências.
	 * @param fcpAgenciaBanco
	 * @return
	 * @throws BaseException
	 */
	public Long pesquisarCountListaAgencia(Short codBanco, Integer codigoAgencia) throws BaseException;

	/**
	 * Inserir agência.
	 * @param fcpAgenciaBanco
	 * @throws BaseException
	 */
	public void persistirAgencia(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException;
	
	public void verificarAgenciaBancariaComMesmoCodigo(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException;
	
	/**
	 * Excluir agência.
	 * @param fcpAgenciaBanco
	 * @throws ApplicationBusinessException
	 */
	public void excluirAgencia(FcpAgenciaBanco fcpAgenciaBanco) throws BaseException;

	// Assinatura dos metodos da classe BANCO
	
	/**
	 * Obter lista banco com paginação.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fcpRetencaoTributo
	 * @return
	 * @throws BaseException
	 */
	public List pesquisarListaBanco(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpBanco fcpBanco) throws BaseException;
	
	/**
	 * Obter count lista de bancos.
	 * @param fcpRetencaoTributo
	 * @return
	 * @throws BaseException
	 */
	public Long pesquisarCountListaBanco(FcpBanco fcpBanco) throws BaseException;

	/**
	 * Pesquisar banco por chave primária.
	 * @param fcpBanco
	 * @return
	 * @throws BaseException
	 */
	public FcpBanco pesquisarBanco(Short codigoBanco) throws BaseException;
	
	/**
	 * Inserir banco.
	 * @param fcpRetencaoTributo
	 * @throws BaseException
	 */
	public boolean inserirBanco(FcpBanco fcpBanco) throws BaseException;
	
	/**
	 * Atualizar banco.
	 * @param fcpRetencaoTributo
	 * @throws BaseException
	 */
	public void atualizarBanco(FcpBanco fcpBanco) throws BaseException;
	
	/**
	 * Excluir banco.
	 * @param fcpRetencaoTributo
	 * @throws ApplicationBusinessException
	 */
	public boolean excluirBanco(FcpBanco fcpBanco) throws BaseException;
	
	/**
	 * Pesquisar banco por código ou descrição
	 * @param parametro
	 * @return
	 * @throws BaseException
	 */
	public List<FcpBanco> pesquisarSuggestionBox(String parametro) throws BaseException;
	
	/**
	 * Pesquisar banco por código ou descrição
	 * @param parametro
	 * @return
	 * @throws BaseException
	 */
	public Long pesquisarSuggestionBoxCount(String parametro) throws BaseException;
	
	// Assinatura dos metodos para Retencao Tributo
	
	/**
	 * Obter lista código de recolhimento com paginação.
	 * @param fcpRetencaoTributo	
	 * @return list
	 * @throws BaseException 
	 */
	public List pesquisarListaCodigoRecolhimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) throws BaseException;
	
	/**
	 * Obter count lista de recolhimento.
	 * @param fcpRetencaoTributo	
	 * @return count
	 * @throws BaseException
	 */
	public Long pesquisarCountCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException;

	/**	
	 * Inserir código recolhimento.
	 * @param fcpRetencaoTributo
	 * @throws BaseException
	 */
	public void inserirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException;
	
	/**
	 * Atualizar código de recolhimento.
	 * @param fcpRetencaoTributo
	 * @throws BaseException
	 */
	public void atualizarCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException;
	
	/**
	 * Excluir código de recolhimento.
	 * @param fcpRetencaoTributo
	 * @throws BaseException
	 */
	public void excluirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws ApplicationBusinessException;
	
	/**
	 * Obter lista de recolhimento por código ou descrição.
	 * @param fcpRetencaoTributo	
	 * @return count
	 * @throws BaseException
	 */
	public List<FcpRetencaoTributo> pesquisarRecolhimentoPorCodigoOuDescricao(Object parametro) throws BaseException;
	
	/**
	  * Obter count lista de recolhimento por código ou descrição.
	  * @param fcpRetencaoTributo 
	  * @return count
	  * @throws BaseException
	  */
	 public Long pesquisarRecolhimentoPorCodigoOuDescricaoCount(final String parametro);
	 
	 //Assinatura de metodos para Tributo
	 
	 /**
		 * Obter lista tributo com paginação.
		 * @param fcpTributo	
		 * @return list
		 * @throws BaseException
		 */
		public List<FcpRetencaoAliquota> pesquisarListaTributo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) throws BaseException;
		
		/**
		 * Obter count lista de tributo.
		 * @param fcpTributo	
		 * @return count
		 * @throws BaseException
		 */
		public Long pesquisarCountTributo(FcpRetencaoTributo fcpRetencaoTributo) throws BaseException;

		/**	
		 * Inserir tributo.
		 * @param fcpTributo
		 * @throws BaseException
		 */
		public void inserirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException;
		
		/**
		 * Atualizar tributo.
		 * @param fcpTributo
		 * @throws BaseException
		 */
		public void atualizarRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException;
		
		/**
		 * Excluir tributo.
		 * @param fcpRetencaoAliquota
		 * @throws BaseException
		 */
		public void excluirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException;
		
		/**
		 * Obter RapServidor apartir do usuario.
		 * @param fcpRetencaoAliquota
		 * @throws BaseException
		 */
		public RapServidores obterRapServidor(Usuario usuario) throws BaseException;


		List<ScoEtapaModPac> listarEtapasPac(
				ScoTempoAndtPac tempoLocalizacaoPac,
				DominioObjetoDoPac objetoPacPesquisa);

		void validaEtapasComTempoPrevistoExecucao(
				List<ScoEtapaModPac> etapasModPac) throws ApplicationBusinessException;

		void inserirEtapaPac(ScoEtapaModPac etapaModPac) throws ApplicationBusinessException;

		void alterarEtapaModPac(ScoEtapaModPac etapaModPac) throws BaseException;

		void excluirEtapaModPac(ScoEtapaModPac etapaModPac) throws BaseException;
		
		Long pesquisarScoRamosComerciaisPorFornecedorCount(
				VScoFornecedor fornRamo);

		List<ScoFornRamoComercial> pesquisarScoRamosComerciaisPorFornecedor(
				VScoFornecedor fornRamo, int firstResult, int maxResults,
				String orderProperty, boolean asc);

		void excluirScoFornRamoComercial(ScoFornRamoComercial fornRamoComercial)
				throws ApplicationBusinessException;

		ScoFornRamoComercial obterFornRamoComerciaNumeroCodigo(
				Integer frnNumero, Short rcmCodigo);

		void cadastrarScoFornRamoComercial(
				ScoFornRamoComercial fornRamoComercial) throws BaseException;
		
		ScoFornRamoComercial obterFornRamoComercia(ScoFornRamoComercialId id);

		List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricaoOrderByDescricao(
				Object filter, Boolean false1);

		Long pesquisarLocalizacaoProcessoPorCodigoOuDescricaoCount(
				Object filter, Boolean false1);

		Long pesquisarUnidadeMedidaPorSiglaCount(Object objPesquisa,
				boolean b); 
	

		Long pesquisarModalidadesCount(String pesquisa); 
		
		ScoCaracteristicaUsuarioCentroCusto obterCaracteristica(RapServidores servidor, DominioCaracteristicaCentroCusto carac);
}   
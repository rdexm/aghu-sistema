package br.gov.mec.aghu.compras.pac.business;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoQuadroAprovacaoVO;
import br.gov.mec.aghu.compras.pac.vo.ItemPropostaAFVO;
import br.gov.mec.aghu.compras.pac.vo.LicitacoesLiberarCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoVO;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioQuadroPropostasLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.VisualizarExtratoJulgamentoLicitacaoVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.compras.vo.DupItensPACVO;
import br.gov.mec.aghu.compras.vo.EstatisticaPacVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.compras.vo.PACsPendetesVO;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.compras.vo.RelatorioEspelhoPACVO;
import br.gov.mec.aghu.compras.vo.RelatorioResumoVerbaGrupoVO;
import br.gov.mec.aghu.dominio.DominioAgrupadorItemFornecedorMarca;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSimNaoTodos;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioTipoDuplicacaoPAC;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.dominio.DominioVisaoExtratoJulgamento;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAcoesPontoParada;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVOPai;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPgtoLicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.suprimentos.vo.ScoLocalizacaoProcessoComprasVO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public interface IPacFacade extends Serializable {

	public Long countItemLicitacaoPorNumeroPac(Integer numeroPac);

	public List<ScoItemLicitacao> pesquisarItemLicitacaoPorNumeroPac(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer numeroPac);

	/**
	 * Retorna o valor total da proposta do fornecedor
	 * 
	 * @param propostaFornecedor
	 * @return BigDecimal
	 */
	BigDecimal obterValorTotalProposta(ScoPropostaFornecedor propostaFornecedor);

	/**
	 * Retorna o valor total de um determinado item de proposta
	 * 
	 * @param itemProposta
	 * @return BigDecimal
	 */
	BigDecimal obterValorTotalItemProposta(ScoItemPropostaFornecedor itemProposta);

	/**
	 * Conta itens do PAC de uma liticação.
	 * 
	 * @param numero
	 *            Número da licitação.
	 * @return Número de itens do PAC.
	 */
	Long contarPacLicitacao(Integer numero);

	/**
	 * Pesquisa itens do PAC de uma licitação.
	 * 
	 * @param numero:  Número da licitação.
	 * @param first: Primeiro registro.
	 * @param max: Número máximo de registros.
	 * @param order: Ordenamento.
	 * @param asc: Direção do ordenamento.
	 * @return Itens do PAC.
	 */
	List<ScoAndamentoProcessoCompra> pesquisarPacLicitacao(Integer numero, Integer first, Integer max, String order, boolean asc);

	List<ScoAndamentoProcessoCompra> pesquisarPacLicitacao(Integer numeroPAC);
	
	/**
	 * Obtem andamento do PAC.
	 * 
	 * @param seq
	 *            ID.
	 * @return Andamento do PAC.
	 */
	ScoAndamentoProcessoCompra obterAndamentoPac(Integer seq);

	/**
	 * Remove andamento do PAC.
	 * 
	 * @param seq
	 *            ID
	 */
	void excluir(Integer seq) throws ApplicationBusinessException;

	/**
	 * Altera andamento do PAC.
	 * 
	 * @param andamento
	 *            Andametno do PAC a ser alterado.
	 */
	void alterar(ScoAndamentoProcessoCompra andamento);

	/**
	 * Inclui andamento do PAC.
	 * 
	 * @param andamento
	 *            Andamento do PAC a ser incluído.
	 * @throws ApplicationBusinessException
	 */
	void incluir(ScoAndamentoProcessoCompra andamento) throws ApplicationBusinessException;

	/**
	 * Retorna a unidade de medida "default" das solicitações de serviço
	 * 
	 * @return ScoUnidadeMedida
	 */
	public ScoUnidadeMedida obterUnidadeMedidaSs();

	/**
	 * Pesquisa localizações do PAC.
	 * 
	 * @param filtro
	 * @return
	 */
	List<ScoLocalizacaoProcesso> pesquisarScoLocalizacaoProcesso(Object filtro, Integer max);

	/**
	 * Obtem dias de permanência do andamento do PAC.
	 * 
	 * @param modalidade
	 *            ID da Modalidade.
	 * @param localizacao
	 *            ID da Localização.
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Short obterMaxDiasPermAndamentoPac(String modalidade, Short localizacao);

	public ScoLicitacao obterLicitacao(Integer numero);

	List<ScoLicitacao> listarLicitacoesAtivas(Object pesquisa, DominioModalidadeEmpenho modemp);

	/**
	 * Obtem número de licitações a liberar.
	 * 
	 * @param criteria
	 *            Critério
	 * @return Licitações
	 */
	Long pesquisarLicitacoesLiberarCount(LicitacoesLiberarCriteriaVO criteria);

	List<ScoLicitacao> pesquisarLicitacoesPorNumeroDescricao(Object parametro);

	ScoCondicaoPagamentoPropos obterCondicaoPagamentoPropostaPorNumero(Integer numeroCondicaoPagamento);

	/**
	 * Obtem licitações a liberar.
	 * 
	 * @param criteria
	 *            Critério
	 * @param firstResult
	 *            Primeiro Registro
	 * @param maxResult
	 *            Número Máximo de Registros
	 * @param order
	 *            Ordenamento
	 * @param asc
	 *            Direção do Ordenamento
	 * @return Licitações
	 */
	List<ScoLicitacao> pesquisarLicitacoesLiberar(LicitacoesLiberarCriteriaVO criteria, Integer firstResult, Integer maxResult,
			String order, boolean asc);

	ScoItemLicitacao obterItemLicitacaoPorNumeroLicitacaoENumeroItem(Integer numeroLCT, Short numeroItem);

	/**
	 * Realiza as ações de julgar, desclassificar, cancelar ou deixar pendente
	 * um item de proposta do fornecedor
	 * 
	 * @param itemProposta
	 * @param faseSolicitacao
	 * @param condicaoPagamentoEscolhida
	 * @param motivoCancelamento
	 * @param pendentePor
	 * @param criterioEscolha
	 * @param motivoDesclassificacao
	 * @throws ApplicationBusinessException
	 */
	public void registrarJulgamentoPac(ScoItemPropostaFornecedor itemProposta, ScoFaseSolicitacaoVO faseSolicitacao,
			ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida, DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento,
			DominioSituacaoJulgamento pendentePor, ScoCriterioEscolhaProposta criterioEscolha,
			DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao) throws BaseException;

	/**
	 * Obtem condições de pagamento do PAC
	 * 
	 * @param numero
	 *            Número da licitação.
	 * @param numero
	 *            Número do Item.
	 * @return lista de condições de pagamento do PAC
	 */
	List<ScoCondicaoPgtoLicitacaoVO> obterCondicaoPgtoPac(Integer numeroPac, Short numeroItem, Integer first, Integer max, String order,
			boolean asc);

	public ScoCondicaoPgtoLicitacao obterCondicaoPagamentoPorChavePrimaria(Integer seqCondicao);

	public ScoCondicaoPgtoLicitacao buscarCondicaoPagamentoPK(Integer seqCondicaoPgto);
	
	void atualizarItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException;

	/**
	 * Obtem próximo numero da condição de pagamento para esta Licitação ou
	 * Licitacao/Item
	 * 
	 * @param numero
	 *            Número da licitação.
	 * @param numero
	 *            Número do Item.
	 * @return próximo numero da condição de pagamento
	 */
	public Integer obterProxNumCondicaoPagamento(Integer numeroLicitacao, Short numeroItem);

	/**
	 * Obtem número de condições de pagamento do PAC
	 * 
	 * @param numero
	 *            Número da licitação.
	 * @param numero
	 *            Número do Item.
	 * @return lista de condições de pagamento do PAC
	 */
	Long obterCondicaoPgtoPacCount(Integer numeroLicitacao, Short numeroItem);

	/**
	 * Método verifica número de condições de pagamento cadastradas e valor do
	 * parâmetro P_ACEITA_UNICA_COND_PGTO retornando permissão para cadastrar
	 * nova condição de pagamento
	 * 
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @return boolean indicando permissão para cadastro de nova condição de
	 *         pagamento
	 */
	public boolean permitirNovaCondicaoPgto(Integer numeroLicitacao, Short numeroItem);

	/**
	 * De acordo com o valor do parâmetro P_ACEITA_UNICA_COND_PGTO informa
	 * condição para cadastro de unica condição de pagamento
	 * 
	 * @return true para somente uma condição de pagamento, false permite mais
	 *         de uma condição
	 */
	public boolean verificarParamUnicaCondicaoPgto();

	List<ScoParcelasPagamento> obterParcelasPgtoProposta(Integer numeroProposta);

	/**
	 * Retorna uma string contendo o parecer técnico de determinado item de
	 * proposta, baseado em sua marca e/ou marca/modelo
	 * 
	 * @param item
	 * @return String
	 */
	String obterParecerTecnicoItemProposta(ScoItemPropostaFornecedor item);

	/**
	 * Monta um ScoItemLicitacaoVO baseado em um ScoItemLicitacao
	 * 
	 * @param item
	 * @return ScoItemPacVO
	 */
	ScoItemPacVO montarItemObjetoVO(ScoItemLicitacao item);

	public Boolean verificarPermissoesPac(String login, Boolean gravar);

	/**
	 * Obtem o codigo da solicitacao de compra ou servico associado a
	 * determinado item de licitacao
	 * 
	 * @param item
	 * @return Integer
	 */
	Integer obterNumeroSolicitacao(ScoItemLicitacao item);

	Long contarLicitacaoParecerTecnico(Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida);

	/**
	 * Obtem parcelas das condições de pagamento do PAC
	 * 
	 * @param numero
	 *            Número da licitação.
	 * @param numero
	 *            Número do Item.
	 * @return parcelas das condições de pagamento do PAC
	 */
	List<ScoParcelasPagamento> obterParcelasPagamento(Integer seqCondicaoPgto);

	public List<ScoItemPropostaFornecedor> pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(Integer numeroPac, Short numeroItem);

	/**
	 * Retorna se a unidade de medida do item de proposta do fornecedor é a
	 * mesma da solicitação
	 * 
	 * @param unidadeSolicitada
	 * @param embalagem
	 * @return Boolean
	 */
	public Boolean validarEmbalagemProposta(String unidadeSolicitada, ScoUnidadeMedida embalagem);

	/**
	 * Realiza as operacoes de julgamento em Lote
	 * 
	 * @param listaItensProposta
	 * @param condicaoPagamentoEscolhida
	 * @param criterioEscolha
	 * @throws ApplicationBusinessException
	 */
	void registrarJulgamentoPacLote(List<ScoItemPropostaVO> listaItensProposta, ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida,
			ScoCriterioEscolhaProposta criterioEscolha) throws ApplicationBusinessException;

	/**
	 * Retorna uma lista com todas as condições de pagamento da proposta
	 * (proposta e item da proposta)
	 * 
	 * @param numeroFornecedor
	 * @param numeroPac
	 * @param numeroItem
	 * @return
	 */
	public List<ScoCondicaoPagamentoPropos> pesquisarCondicaoPagamentoProposta(Integer numeroFornecedor, Integer numeroPac, Short numeroItem);

	/**
	 * Remove o item passado como paramaetro da lista tambem passada por
	 * parametro
	 * 
	 * @param item
	 * @param list
	 */
	void removerItemLista(ScoItemPacVO item, List<ScoItemPacVO> list);

	/**
	 * Suggestion para Formas de Pagamento (ScoFormaPagamento)
	 * 
	 * @param codigo
	 *            ou descrição
	 * @return lista de formas de pagamento
	 */
	List<ScoFormaPagamento> listarFormasPagamento(Object pesquisa);

	/**
	 * Inclui/Atualiza condição de pagamento e suas parcelas Remove da base de
	 * dados as parcelas que devem ser excluídas
	 * 
	 * @param ScoCondicaoPgtoLicitacao
	 *            condicaoPagamento e suas parcelas. Lista de parcelas a serem
	 *            excluídas
	 */
	public void gravarCondicaoPagtoParcelas(ScoCondicaoPgtoLicitacao condicaoPagamento, List<ScoParcelasPagamento> listaParcelas,
			List<ScoParcelasPagamento> listaParcelasExcluidas) throws ApplicationBusinessException;

	/**
	 * Exclui condição de pagamento da licitação e suas parcelas
	 * 
	 * @param seq
	 *            da ScoCondicaoPgtoLicitacao
	 */
	public void excluirCondicaoPgto(Integer seqCondicaoPgto);

	Long listarProcessosAdmCompraCount(ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer) throws ApplicationBusinessException;

	List<ScoLicitacao> listarProcessosAdmCompra(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer) throws ApplicationBusinessException;

	/**
	 * Verifica se um item da licitação possui proposta escolhida
	 * 
	 * @param numLicitacao
	 * @param numero
	 * @return
	 */
	public Boolean verificarItemPacPossuiPropostaEscolhida(Integer numLicitacao, Short numero);

	/**
	 * Insere um item de proposta de fornecedor
	 * 
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	void inserirItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException;

	/**
	 * Atualiza um item de proposta de fornecedor
	 * 
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	void atualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException;

	public List<ScoLicitacao> pesquisarLicitacaoParecerTecnico(Integer firstResult, Integer maxResult, String order, boolean asc,
			Integer numeroPac, String descricaoPac, ScoModalidadeLicitacao modalidade, Boolean vencida);

	/**
	 * Pesquisa propostas de um fornecedor dentro de determinado PAC que ainda
	 * nao possua ind_escolhido
	 * 
	 * @param numeroPac
	 * @param fornecedor
	 * @param julgados
	 * @return List
	 */
	List<ScoItemPropostaVO> pesquisarPropostaFornecedorParaJulgamentoLote(Integer numeroPac, ScoFornecedor fornecedor,
			DominioSimNaoTodos julgados);

	/**
	 * Encaminha PAC ao parecer técnico.
	 * 
	 * @param licitacao
	 *            PAC
	 */
	public void encaminharParecerTecnico(ScoLicitacao licitacao);

	/**
	 * Encaminha PAC à comissão de licitação.
	 * 
	 * @param licitacao
	 *            PAC
	 */
	public void encaminharComissao(ScoLicitacao licitacao);

	/**
	 * Contabiliza propostas de fornecedores para uma determinada licitação.
	 * 
	 * @param licitacao
	 *            Licitação
	 * @param fornecedor
	 *            Fornecedor
	 * @return Número de Propostas
	 */
	public Long contarPropostas(ScoLicitacao licitacao, String fornecedor);

	/**
	 * Método que pesquisa propostas de fornecedores pelo número da licitação
	 * 
	 * @author clayton.bras
	 * @param licitacao
	 * @param fornecedor
	 * @param asc
	 * @param order
	 * @param max
	 * @param first
	 * @return
	 */
	List<ScoPropostaFornecedor> pesquisarPropostas(ScoLicitacao licitacao, String fornecedor, Integer first, Integer max, String order,
			boolean asc);

	/**
	 * Método que realiza a exclusão da proposta de fornecedor
	 * 
	 * @author clayton.bras
	 */
	void excluir(ScoPropostaFornecedorId id);

	/**
	 * Copia já persistindo no banco as condições de pagamento da licitação
	 * (PAC) para proposta passada como parâmetro
	 * 
	 * @param pfrLctNumero
	 * @param pfrFrnNumero
	 * @param numeroItem
	 * @param numeroItemLicitacao
	 */
	void copiarCondicaoPagamentoLicitacao(Integer pfrLctNumero, Integer pfrFrnNumero, Short numeroItem, Short numeroItemLicitacao)
			throws ApplicationBusinessException;

	/**
	 * Retorna se o item de proposta do fornecedor está em AF
	 * 
	 * @param numeroPac
	 * @param numeroItem
	 * @param fornecedor
	 * @return Boolean
	 */
	public Boolean verificarItemPropostaFornecedorEmAf(Integer numeroPac, Short numeroItem, ScoFornecedor fornecedor);

	/**
	 * Retorna o próximo número de um item de proposta de fornecedor
	 * considerando o que está no banco e o que está na tela e ainda não foi
	 * persistido
	 * 
	 * @param listaItensPropostas
	 * @param listaItensPropostasExclusao
	 * @param numeroPac
	 * @param numeroFornecedor
	 * @return Short
	 */
	public Short obterProximoNumeroItemPropostaFornecedor(List<ScoItemPropostaVO> listaItensPropostas,
			List<ScoItemPropostaVO> listaItensPropostasExclusao, Integer numeroPac, Integer numeroFornecedor);

	/**
	 * Retorna se a unidade de medida utilizada na solicitação de compras base
	 * com a unidade de medida proposta pelo fornecedor na proposta conforme o
	 * fator de conversão informado
	 * 
	 * @param faseSolicitacao
	 * @param fatorConversao
	 * @param embalagem
	 * @return Boolean
	 */
	public Boolean validarFatorConversao(ScoFaseSolicitacaoVO faseSolicitacao, Integer fatorConversao, ScoUnidadeMedida embalagem);

	/**
	 * Valida se o fornecedor já fez alguma proposta para o mesmo item de
	 * licitação com a mesma marca comercial ou mesmo fornecedor
	 * 
	 * @param listaItensPropostas
	 * @param faseSolicitacao
	 * @param marcaComercial
	 * @param fornecedor
	 */
	public void validarInsercaoItemPropostaDuplicado(List<ScoItemPropostaVO> listaItensPropostas, ScoFaseSolicitacaoVO faseSolicitacao,
			ScoMarcaComercial marcaComercial, ScoFornecedor scoFornecedor) throws ApplicationBusinessException;

	/**
	 * Valida valor unitario do item deve ser maior que zero
	 * 
	 * @param valorUnitarioItemProposta
	 * @throws ApplicationBusinessException
	 */
	public void validarInsercaoItemPropostaValorUnitario(BigDecimal valorUnitarioItemProposta) throws ApplicationBusinessException;

	/**
	 * Verifica se a quantidade informada no item de proposta do fornecedor é
	 * iguao a da da solicitação, basedado no fator de conversão informado na
	 * tela
	 * 
	 * @param qtdItemProposta
	 * @param fatorConversao
	 * @param qtdItemSolicitacao
	 * @return Boolean
	 */
	public Boolean validarQuantidadePropostaDiferenteSolicitacao(Long qtdItemProposta, Integer fatorConversao, Integer qtdItemSolicitacao);

	/**
	 * Processa as listas de ScoItemPropostaVO passadas como parametro para
	 * persistencia e exclusao de itens de proposta
	 * 
	 * @param listaItensPropostas
	 * @param listaItensPropostasExclusao
	 * @param propostaEmEdicao
	 * @throws ApplicationBusinessException
	 */
	public void gravarItemProposta(List<ScoItemPropostaVO> listaItensPropostas, List<ScoItemPropostaVO> listaItensPropostasExclusao,
			Boolean propostaEmEdicao) throws ApplicationBusinessException;

	/**
	 * Retorna a unidade do material do item da licitacao (PAC)
	 * 
	 * @param item
	 * @return String
	 */
	public String obterUnidadeMaterial(ScoItemLicitacao item);

	/**
	 * Retorna o número do complemento da AF de determinado item de licitação
	 * (PAC)
	 * 
	 * @param item
	 * @return String
	 */
	public String obterComplementoAutorizacaoFornecimento(ScoItemLicitacao item);

	/**
	 * Obtém o tipo (DominioTipoSolicitacao) de determinado item da licitação
	 * (PAC)
	 * 
	 * @param item
	 * @return DominioTipoSolicitacao
	 */
	public DominioTipoSolicitacao obterTipoSolicitacao(ScoItemLicitacao item);

	/**
	 * Obtem uma descrição da solicitação de compra/serviço de um item da
	 * licitação (PAC) conforme preenchimento dos campos
	 * 
	 * @param item
	 * @return String
	 */
	public String obterDescricaoSolicitacao(ScoItemLicitacao item);

	/**
	 * Retorna o nome de um material ou serviço de um item de licitação (PAC)
	 * 
	 * @param item
	 * @param concatenarCodigo
	 * @return String
	 */
	public String obterNomeMaterialServico(ScoItemLicitacao item, Boolean concatenarCodigo);

	/**
	 * Obtém a quantidade solicitada do material/serviço de um item de licitação
	 * (PAC)
	 * 
	 * @param item
	 * @return Integer
	 */
	public Integer obterQuantidadeMaterialServico(ScoItemLicitacao item);

	/**
	 * Retorna a descrição de um material/serviço de um item de licitação (PAC)
	 * 
	 * @param item
	 * @return String
	 */
	public String obterDescricaoMaterialServico(ScoItemLicitacao item);

	/**
	 * Retorna o valor total da licitação (PAC)
	 * 
	 * @param numeroLicitacao
	 * @return BigDecimal
	 */
	public BigDecimal obterValorTotalPorNumeroLicitacao(Integer numeroLicitacao);

	/**
	 * Retorna o valor de um item de licitacao (PAC)
	 * 
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @return BigDecimal
	 */
	public BigDecimal obterValorTotalItemPac(Integer numeroLicitacao, Short numeroItem);

	/**
	 * Exclui um item do PAC, avaliando se a exclusao deve ser lógica ou física
	 * 
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @param motivoExclusao
	 * @throws BaseException
	 */
	public void excluirItemPac(Integer numeroLicitacao, Short numeroItem, String motivoExclusao, Boolean indExcluido) throws BaseException;

	/**
	 * Reativa um item de licitação que foi previamente excluido logicamente
	 * 
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @throws BaseException
	 */
	public void reativarItemPac(Integer numeroLicitacao, Short numeroItem) throws BaseException;

	void persistirPac(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) throws BaseException;

	void validaFrequenciaCompras(ScoLicitacao licitacao) throws ApplicationBusinessException;

	/**
	 * Verifica se o item de licitacao possui proposta
	 * 
	 * @param numLicitacao
	 * @param numero
	 * @param propostaEscolhida
	 * @return Boolean
	 */
	public Boolean verificarLicitacaoProposta(Integer numLicitacao, Short numero, Boolean propostaEscolhida);

	/**
	 * Persiste no banco as alterações realizadas na tela de itens do PAC
	 * 
	 * @param numeroLicitacao
	 * @param listCompleta
	 * @param listAlteracoes
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	public void gravarAlteracoesItensPac(Integer numeroLicitacao, List<ScoItemPacVO> listaOriginal, List<ScoItemPacVO> listAlteracoes)
			throws ApplicationBusinessException, BaseException;

	/**
	 * Reordena os codigos dos itens de licitação de um PAC com a mesma ordem
	 * definida pelo usuario na tela
	 * 
	 * @param numeroLicitacao
	 * @param listaGradeAtual
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	public void reordenarItensPac(Integer numeroLicitacao, List<ScoItemPacVO> listaGradeAtual) throws ApplicationBusinessException, BaseException;

	/**
	 * Verifica se um item de licitação pode ser editado
	 * 
	 * @param numeroLicitacao
	 * @param validarProposta
	 * @param validarPublicacao
	 * @return Boolean
	 */
	public Boolean verificarEdicaoItensPac(Integer numeroLicitacao, Boolean validarProposta, Boolean validarPublicacao);

	public Boolean verificarEdicaoItensPacPropostaLote(Integer numeroLicitacao, Short numero);
	
	/**
	 * Obtem ponto de parada anterior da Solicitação de Compra/Servido do item
	 * de uma licitação
	 * 
	 * @param itemLicitacao
	 * @return
	 */
	public ScoPontoParadaSolicitacao obterPontoParadaAnteriorItemLicitacao(ScoItemLicitacao itemLicitacao);

	/**
	 * Conta itens de uma proposta autorizados pelo fornecedor.
	 * 
	 * @param proposta
	 *            Proposta
	 * @return Itens
	 */
	public Long contarItensEmAf(ScoPropostaFornecedor proposta);

	public ScoPropostaFornecedor obterPropostaFornecedor(ScoPropostaFornecedorId idProposta);

	List<ItensPACVO> pesquisarRelatorioItensPAC(Integer numero, boolean flagNaoExcluidas) throws ApplicationBusinessException;

	Set<RelatorioEspelhoPACVO> gerarDadosRelatorioEspelhoPAC(final Integer numLicitacao);

	/**
	 * Retorna uma lista para suggestion de fases da tela de cadastro de item de
	 * proposta
	 * 
	 * @param param
	 * @param numeroPac
	 * @return List
	 */
	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac);

	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac, List<ScoItemPropostaVO> listaItensPropostas)
			throws ApplicationBusinessException;

	/**
	 * Retorna uma lista de ScoItemPropostaVO associadas a determinada licitacao
	 * e fornecedor
	 * 
	 * @param numeroPac
	 * @param fornecedor
	 * @return List
	 */
	public List<ScoItemPropostaVO> pesquisarItemPropostaPorNumeroLicitacao(Integer numeroPac, ScoFornecedor fornecedor);

	public List<RelUltimasComprasPACVOPai> gerarRelatorioUltimasComprasPAC(final Integer numLicitacao, final List<Integer> itens,
			final List<String> itensModalidade, final Integer qtdeUltimasCompras, DominioAgrupadorItemFornecedorMarca agrupador);

	public List<PreItemPacVO> preSelecionarItensPac(ScoPontoParadaSolicitacao caixa, RapServidores comprador, ScoServico servico,
			ScoMaterial material) throws ApplicationBusinessException;

	public List<PreItemPacVO> pesquisarPreItensPac(DominioTipoSolicitacao tipoSolicitacao, Integer numeroIni, Integer numeroFim)
			throws ApplicationBusinessException;

	public List<DupItensPACVO> pesquisarItensPAC(Integer numero, DominioTipoDuplicacaoPAC tipoDuplicacaoPAC)
			throws ApplicationBusinessException;

	public ScoLicitacao duplicarPAC(Integer numeroPAC, DominioTipoDuplicacaoPAC tipoDuplicacaoPAC, RapServidores servidorLogado,
			List<DupItensPACVO> itensPACVO) throws BaseException;

	public ScoFaseSolicitacaoVO obterFaseVOPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem);

	/**
	 * Retorna a moeda padrão que deve ser utilizada no cadastro de propostas de
	 * fornecedores, conforme parâmetro do banco P_MOEDA_CORRENTE
	 * 
	 * @return FcpMoeda
	 * @throws ApplicationBusinessException
	 */
	public FcpMoeda obterMoedaPadraoItemProposta() throws ApplicationBusinessException;

	/**
	 * Insere ou altera na base de dados um ScoPropostaFornecedor
	 * 
	 * @param proposta
	 * @param propostaEmEdicao
	 * @throws ApplicationBusinessException
	 */
	public void persistirPropostaFornecedor(ScoPropostaFornecedor proposta, Boolean propostaEmEdicao) throws ApplicationBusinessException;

	public List<RelatorioQuadroPropostasLicitacaoVO> pesquisarQuadroProvisorioItensPropostas(Set<Integer> listaNumPac, Short numeroInicial,
			Short numeroFinal, String listaItens);

	public List<RelatorioQuadroPropostasLicitacaoVO> pesquisarQuadroJulgamentoPropostas(Set<Integer> listaNumPac, Short numeroItemInicial,
			Short numeroItemFinal, String listaItens) throws ApplicationBusinessException;

	public List<PreItemPacVO> listaAssociadasSSItensPac(Integer numeroIni, Integer numeroFim) throws ApplicationBusinessException;

	public List<PreItemPacVO> listaAssociadasSCItensPac(Integer numeroIni, Integer numeroFim) throws ApplicationBusinessException;

	public void persistirItemPac(ScoLicitacao pac, List<PreItemPacVO> listaItensPac) throws BaseException;

	/**
	 * Conta PAC's para julgamento conforme critério de consulta.
	 * 
	 * @param criteria
	 *            Critério
	 * @return Quantidade de PAC's para julgamento.
	 */
	public Long contarPacsParaJulgamento(PacParaJulgamentoCriteriaVO criteria);

	/**
	 * Pesquisa PAC's para julgamento conforme critério de consulta.
	 * 
	 * @param criteria
	 *            Critério
	 * @param first
	 *            Primeiro Registro
	 * @param max
	 *            Número Máximo de Registros
	 * @param order
	 *            Campo Ordenador
	 * @param asc
	 *            Direção do Ordenamento
	 * @return PAC's para Julgamento
	 */
	public List<PacParaJulgamentoVO> pesquisarPacsParaJulgamento(PacParaJulgamentoCriteriaVO criteria, Integer first, Integer max,
			String order, boolean asc);

	/**
	 * Encaminha PAC's ao comprador.
	 * 
	 * @param pacIds
	 *            PAC's a serem encaminhados.
	 * @throws ApplicationBusinessException
	 */
	public void encaminharComprador(Set<Integer> pacIds, Boolean limpaParecer) throws ApplicationBusinessException;

	public RelatorioResumoVerbaGrupoVO obterDadosRelatorioVerbaGrupo(Integer numLicitacao);

	/**
	 * Pesquisa PAC's para impressão do quadro de aprovação.
	 * 
	 * @param pacs
	 *            ID's dos PAC's
	 * @param assinaturas
	 *            Flag para exibição de assinaturas.
	 * @return PAC's
	 */
	public List<ItemLicitacaoQuadroAprovacaoVO> pesquisarPacsQuadroAprovacao(Set<Integer> pacs, Boolean assinaturas);

	/**
	 * Pesquisar os números de Solicitações de Compra relacionadas aos itens de
	 * um PAC
	 * 
	 * @param scoLicitacao
	 * @return lista dos números da Solicitações de Compra relacionadas a um PAC
	 */
	public List<Integer> retornarListaNumeroSolicicaoCompraPorPAC(ScoLicitacao scoLicitacao);

	/**
	 * Pesquisar os números de Solicitações de Serviço relacionadas aos itens de
	 * um PAC
	 * 
	 * @param scoLicitacao
	 * @return lista dos números da Solicitações de Serviço relacionadas a um
	 *         PAC
	 */
	public List<Integer> retornarListaNumeroSolicicaoServicoPorPAC(ScoLicitacao scoLicitacao);

	/**
	 * Valida unidade de medida SC/Material.
	 * 
	 * @param solicitacaoCompra
	 *            SC
	 * @return true = São diferentes; false = São iguais.
	 */
	public boolean validarUnidadeMedida(ScoSolicitacaoDeCompra solicitacaoCompra);

	/**
	 * Valida item julgado ou cancelado.
	 * 
	 * @param item
	 *            Item
	 * @return true = Julgado ou cancelado.
	 */
	public boolean validarJulgadoCancelado(ScoItemLicitacao item);

	public PropFornecAvalParecerVO obterParecerMaterialSituacaoItemProposta(ScoItemPropostaFornecedor item);

	public Boolean verificarItemProposta(ScoLicitacao licitacao);

	public List<ItemPropostaAFVO> pesquisarItensPac(Integer numeroPac) throws ApplicationBusinessException;

	/**
	 * Insere item de licitação.
	 * 
	 * @param itemLicitacao
	 *            Item de Licitação
	 * @throws ApplicationBusinessException
	 */
	public void inserir(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException;

	/**
	 * Persiste condição de pagamento de licitação.
	 * 
	 * @param cond
	 *            Condição de Pagamento
	 */
	public void persistir(ScoCondicaoPgtoLicitacao cond) throws ApplicationBusinessException;

	public ScoMaterial obterMaterialPorChavePrimaria(Integer codigoMaterial);

	public ScoServico obterServicoPorChavePrimaria(Integer codigoMaterial);

	/**
	 * A leitura dos itens da licitação deve ser baseado na QUERY
	 * V_SCO_ITENS_LICITACAO
	 * 
	 * @param nroLicitacao
	 * @return
	 */
	public List<Object[]> montarListaDetalhesLicitacao(Integer nroLicitacao);

	public void alterarLicitacao(ScoLicitacao licitacao, ScoLicitacao licitacaoClone) throws ApplicationBusinessException;

	public void inserirLicitacao(ScoLicitacao licitacao) throws BaseException;

	public Boolean verificarUtilizaParecerTecnico() throws ApplicationBusinessException;

	List<ScoAcoesPontoParada> listarAcoesPontoParadaPac(Integer seqAndamento);

	Boolean verificarAcoesPontoParadaPac(Integer seqAndamento);
	
	Boolean validarEdicaoAcoes(ScoAndamentoProcessoCompra andamento, ScoAndamentoProcessoCompra primeiroAndamento);

	List<ScoLocalizacaoProcessoComprasVO> pesquisarLocalizacaoProcessoCompra(Integer first, Integer max, String order, boolean asc,
			Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento, ScoModalidadeLicitacao modalidadeCompra,
			Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel);

	Long pesquisarLocalizacaoProcessoCompraCount(Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento,
			ScoModalidadeLicitacao modalidadeCompra, Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel);

	ScoLicitacao buscarLicitacaoPorNumero(Integer numeroPac);

	List<VisualizarExtratoJulgamentoLicitacaoVO> buscarPropostasFornecedor(Integer numeroPac);

	Boolean validarCamposObrigatorioLocalizacao(Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento,
			ScoModalidadeLicitacao modalidadeCompra, Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel)
			throws ApplicationBusinessException;

	ScoLicitacao obterLicitacaoPorNroPAC(Integer numeroPAC);

	List<ScoLoteLicitacao> listarLotesPorPac(Integer nroPac);

	void gravarLoteSolicitacao(ScoLoteLicitacao loteLicitacao) throws ApplicationBusinessException;

	void excluirLoteSolicitacao(ScoLoteLicitacaoId idLoteDelecao) throws ApplicationBusinessException;

	List<VisualizarExtratoJulgamentoLicitacaoVO> verificaVisaoExtratoJulgamento(DominioVisaoExtratoJulgamento visao,
			List<VisualizarExtratoJulgamentoLicitacaoVO> list, List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoAux,
			VisualizarExtratoJulgamentoLicitacaoVO item);

	List<ItensPACVO> listarItensLictacaoPorPac(Integer nroPac);

	Boolean verificarDependenciasDoItem(Integer nroPac, Integer materialCod, Short nroLote);

	String obterParecer(Integer codigo);
	
	String obterParecerAtivo(Integer codigo);

	void associarItensLote(List<ItensPACVO> itensLicitacao) throws ApplicationBusinessException;

	void validarSeExisteLote(Integer nroPac, Short nroLote) throws ApplicationBusinessException;

	Boolean verificarExisteItensAssociados(Integer lctNumero, Short numero)throws ApplicationBusinessException;
	
	Long pesquisarScoLocalizacaoProcessoCount(Object filtro);

	void enviarEmail(Integer pac, VisualizarExtratoJulgamentoLicitacaoVO vo, byte[] pdf, RapServidores usuarioLogado, String nomeArquivoPDF)
			throws ApplicationBusinessException;

	void validarContatosFornecedores(List<VisualizarExtratoJulgamentoLicitacaoVO> vos) throws ApplicationBusinessException;

	ScoLoteLicitacao obterLote(ScoLoteLicitacao lote);

	void desfazerExcluirLotes(List<ScoLoteLicitacao> lotesLicitacao, Integer nroPac) throws ApplicationBusinessException;

	void gerarLotesByPacItens(Integer nroPac) throws ApplicationBusinessException;

	public String geraArquivoCSV(List<RelUltimasComprasPACVO> dados, Integer integer) throws IOException;

	public void downloaded(String fileName) throws IOException;

	public String geraArquivoCSVItem(List<RelUltimasComprasPACVOPai> dados, Integer numeroPAC) throws IOException ;
	
	public List<RelUltimasComprasPACVO> gerarUltimasComprasPAC(Integer numeroPAC, 
			List<Short> listaItens,
			List<String> listaCodigosModalidades);

	public List<Short> converterItensToShort(List<Integer> itens);

	public List<RelUltimasComprasPACVO> buscarUltimasComprasPAC(Integer numLicitacao, List<Integer> itens, List<String> itensModalidade, Integer qtdRegistros, DominioAgrupadorItemFornecedorMarca tipoAgrupamento);
	
	public Long pesquisarUltimasComprasMateriasCount(String modCod, Date dataNR, Integer matCod, boolean historico);
	
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMaterias(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,String modl, Date DataNrInicial, Integer matCodigo, boolean historico);

	public List<String> obterEmailsFornecedor(Integer numeroFornecedor);

	public String gerarIdentificacaoCotacao();

	public String obterRamalCotacao() throws ApplicationBusinessException;

	public List<ScoSolicitacaoDeCompra> obterSolicitacoesCompraPorNumero(
			Object filter);
	
	public boolean isValidForSearch(ConsultarAndamentoProcessoCompraVO filtro);
	
	List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompraForCSV(
			ConsultarAndamentoProcessoCompraVO filtro);

	String geraArquivoAndamentoProcessoCompra(
			List<ConsultarAndamentoProcessoCompraDataVO> dados)
			throws IOException;

	List<PACsPendetesVO> obterQtdPACsPendentes();

	List<EstatisticaPacVO> gerarEstatisticas();

	Long consultarAndamentoProcessoCompraCount(
			ConsultarAndamentoProcessoCompraVO filtro);

	List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompra(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, ConsultarAndamentoProcessoCompraVO filtro);
	
	ScoItemPropostaFornecedor obterItemPropostaFornecedorPorID(ScoItemPropostaFornecedorId id);
	
	public void validarSelecaoPregaoEletronico(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException;
	
	public void gerarPropostaPregaoBB(Integer numPac, String nomeArquivoProcessado) throws ApplicationBusinessException;
	
	public List<ScoFaseSolicitacao> obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(Integer numeroLCT, Short numeroItem);
	
public Long pesquisarLicitacoesPorNumeroDescricaoCount(String parametro);
	
	public String obterLocalidadeAtualPACPorNumLicitacao(Integer numeroLicitacao);
	
	public List<String> listarHistoricoEtapas(Integer numeroLicitacao);
	
	public String obterTempoTotal(Integer numeroLicitacao);
	
	public List<LocalPACVO> pesquisarLocalPACPorNumeroDescricao(Object param, Integer numeroLicitacao, String codigoModalidade);
	
	public Long pesquisarLocalPACPorNumeroDescricaoCount(Object param, Integer numeroLicitacao, String codigoModalidade);
	
	public List<EtapasRelacionadasPacVO> listarEtapasRelacionadasPAC(Integer numeroLicitacao, Short codigoLocalizacao, String codigoModalidade);
	
	public EtapasRelacionadasPacVO verificaNecessidadeSalvarEtapaPAC(EtapasRelacionadasPacVO codigoEtapa, Integer numeroLicitacao);
	
	public void atualizarSituacaoEtapaPAC(EtapasRelacionadasPacVO vo, RapServidores servidor);
	
	public String alterarTempoTotal(String dataInicioFim);
	
	public String geraArquivoPAC(List<ScoLicitacao> listaExcel) throws IOException;
	
	public String geraArquivoItensPAC(List<ScoItemPacVO> listaExcel, Integer pac) throws IOException;
	
	public void excluirTodosLoteSolicitacao(List<ScoLoteLicitacao> lotesSolicitacao, Integer nroPac) throws ApplicationBusinessException;
	
	public void atualizarItemPropostaFornecedor(ScoItemPropostaFornecedor itemProposta, boolean novo) throws ApplicationBusinessException;
	
	public Integer obterCodMatServ(ScoItemLicitacao item);
	
	public ScoLicitacao obterLicitacaoPorModalidadeEditalAno(ScoModalidadeLicitacao modalidade, Integer edital, Integer ano);
	
	public void validaValoresRegrasOrcamentarias(List<PreItemPacVO> listaItensVOPac) throws ApplicationBusinessException;
	
}
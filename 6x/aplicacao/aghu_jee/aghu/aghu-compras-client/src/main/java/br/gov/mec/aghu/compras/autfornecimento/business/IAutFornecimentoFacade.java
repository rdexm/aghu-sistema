/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailAtrasoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ConsultaProgramacaoEntregaItemVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroPesquisaAssinarAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItemAutFornecimentoJnVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PersistenciaProgEntregaItemAfVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItemAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFJnVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ResponsavelAfVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAutorizacaoFornJnVO;
import br.gov.mec.aghu.compras.pac.vo.GeracaoAfVO;
import br.gov.mec.aghu.compras.vo.AFPFornecedoresVO;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.compras.vo.AfFiltroVO;
import br.gov.mec.aghu.compras.vo.AfsContratosFuturosVO;
import br.gov.mec.aghu.compras.vo.AlteracaoEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoManualVO;
import br.gov.mec.aghu.compras.vo.ConsultarParcelasEntregaMateriaisVO;
import br.gov.mec.aghu.compras.vo.ExcluirProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.compras.vo.FiltroParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.FiltroProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.compras.vo.MaterialServicoVO;
import br.gov.mec.aghu.compras.vo.ModalAlertaGerarVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAutFornPedidoVO;
import br.gov.mec.aghu.compras.vo.PesquisaAutFornPedidosVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalFornecedoresVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaItemAFVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoManualParcelasEntregaFiltroVO;
import br.gov.mec.aghu.compras.vo.ValidacaoFiltrosVO;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAfEmpenho;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.suprimentos.vo.CalculoValorTotalAFVO;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.view.VScoPacientesCUM;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings("PMD.ExcessiveClassLength")
public interface IAutFornecimentoFacade extends Serializable {

	/**
	 * Retorna uma lista pagina de autorizações de fornecimento baseado no VO de
	 * filtro
	 * 
	 * @param first
	 * @param max
	 * @param order
	 * @param asc
	 * @param filtro
	 * @return List
	 */
	List<ScoAutorizacaoForn> pesquisarAutorizacaoFornecimento(Integer first, Integer max, String order, boolean asc,
			PesquisaAutorizacaoFornecimentoVO filtro);

	RelatorioAFPVO pesquisarAFsPorLicitacaoComplSeqAlteracao(Integer numPac, Short nroComplemento) throws ApplicationBusinessException;

	RelatorioAFJnVO pesquisarJnAFsPorLicitacaoComplSeqAlteracao(Integer pacNumero, Short nroComplemento, Short sequenciaAlteracao)
			throws ApplicationBusinessException;

	/**
	 * Retorna a quantidade de AFs baseado no VO de filtro
	 * 
	 * @param filtro
	 * @return
	 */
	Long contarAutorizacaoFornecimento(PesquisaAutorizacaoFornecimentoVO filtro);

	String buscarPrazosAtendimento(Integer cdpNumero) throws ApplicationBusinessException;

	/**
	 * Obtem uma solicitacao associada a uma programacao de entrega de item de
	 * af por PK
	 * 
	 * @param speSeq
	 * @return ScoSolicitacaoProgramacaoEntrega
	 */
	ScoSolicitacaoProgramacaoEntrega obterSolicitacaoProgEntregaPorId(Long speSeq);

	List<ScoAfEmpenho> buscarEmpenhoPorAfNum(Integer numeroAf) throws ApplicationBusinessException;

	ScoAutorizacaoFornJn buscarUltimaScoAutorizacaoFornJnPorNroAF(Integer afnNumero, Short sequenciaAlteracao);

	/**
	 * Pesquisa uma AF por chave primaria
	 * 
	 * @param numeroAf
	 * @return
	 */
	ScoAutorizacaoForn obterAfByNumero(Integer numeroAf);
	
	ScoAutorizacaoForn obterAfByNumeroComPropostaFornecedor(Integer numeroAf);

	ScoAutorizacaoForn obterAfDetalhadaByNumero(Integer numeroAf);

	List<ScoAutorizacaoForn> listarAfByFornAndLic(ScoLicitacao licitacao, ScoFornecedor fornecedor);

	void atualizarAutorizacaoForn(ScoAutorizacaoForn e, boolean flush);

	void desatacharAutorizacaoForn(ScoAutorizacaoForn scoAutorizacaoForn);

	List<ScoItemAutorizacaoForn> pesquisarAutorizacoesFornecimentoPorSeqDescricao(Object param);

	Boolean existeAutorizacaoFornecimentoNotaImportacao(Integer numeroAutorizacaoFornecimento, Short codigoFormaPagamentoImportacao);

	/**
	 * Obtem o valor bruto (não efetivado) da AF
	 * 
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param listaItens
	 * @return Double
	 */
	Double obterValorBrutoAf(Integer afnNumero, Short numeroComplemento, List<ScoItemAutorizacaoForn> listaItens);

	/**
	 * Exclui uma sequência de alteração da AF
	 * 
	 * @param autorizacaoFornecimento
	 * @param ultimaSequenciaAlteracao
	 * @throws BaseException
	 */
	void excluirSequenciaAlteracao(ScoAutorizacaoForn autorizacaoFornecimento, Short ultimaSequenciaAlteracao) throws BaseException;

	/**
	 * Baseado na lista de parcelas calcula a qtde programada para um item de AF
	 * 
	 * @param listaParcelas
	 * @return Integer
	 */
	Integer obterQtdeProgramadaProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas);

	/**
	 * Obtem o valor de acrescimo da AF
	 * 
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param valorBruto
	 * @param listaItens
	 * @return Double
	 */
	Double obterValorAcrescimoAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, List<ScoItemAutorizacaoForn> listaItens);
	List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoComprasPorAutorizacaoFornecimento(Integer afnNumero, Integer numero, Boolean indExclusao);
	List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoServicoPorAutorizacaoFornecimento(Integer afnNumero, Integer numero, Boolean indExclusao);
	
	/**
	 * Obtem o valor de desconto da AF
	 * 
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param valorBruto
	 * @param listaItens
	 * @return Double
	 */
	Double obterValorDescontoAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, List<ScoItemAutorizacaoForn> listaItens);

	/**
	 * Baseado na lista de parcelas calcula a qtde efetivada (já entregue) para
	 * um item de AF
	 * 
	 * @param listaParcelas
	 * @return Integer
	 */
	Integer obterQtdeEfetivadaProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas);

	/**
	 * Baseado na lista de parcelas calcula o valor efetivado (entregue) para um
	 * item de AF
	 * 
	 * @param listaParcelas
	 * @return BigDecimal
	 */
	BigDecimal obterValorTotalProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas);

	/**
	 * Retorna uma lista de solicitações associadas à programação de entrega do
	 * item da AF ordenado por prioridade
	 * 
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @param seq
	 * @param parcela
	 * @return List
	 */
	List<ScoSolicitacaoProgramacaoEntrega> pesquisarSolicitacaoProgEntregaPorItemProgEntrega(Integer iafAfnNumero, Integer iafNumero,
			Integer seq, Integer parcela);

	Long pesquisarListaAfsAssinarCount(FiltroPesquisaAssinarAFVO filtro);
	
	/**
	 * Baseado na lista de parcelas calcula o valor programado para um item de
	 * AF
	 * 
	 * @param listaParcelas
	 * @return BigDecimal
	 */
	BigDecimal obterValorProgramadoProgEntregaItemAf(List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas);

	/**
	 * Remove uma programação de entrega para um item de AF
	 * 
	 * @param item
	 * @throws BaseException
	 */
	void excluirProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento item) throws BaseException;

	/**
	 * Obtem o valor do IPI da AF
	 * 
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param valorBruto
	 * @param valorDesconto
	 * @param valorAcrescimo
	 * @param listaItens
	 * @return Double
	 */
	Double obterValorIpiAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, Double valorDesconto, Double valorAcrescimo,
			List<ScoItemAutorizacaoForn> listaItens);

	/**
	 * Obtem o valor Efetivado da AF
	 * 
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param listaItens
	 * @return
	 */
	Double obterValorEfetivadoAf(Integer afnNumero, Short numeroComplemento, List<ScoItemAutorizacaoForn> listaItens);

	/**
	 * Obtem a cor definida para o inputText conforme o andamento da AF
	 * 
	 * @param andamento
	 * @return
	 */
	String obterCorFundoAndamentoAutorizacaoFornecimento(DominioAndamentoAutorizacaoFornecimento andamento);

	/**
	 * Calcula o valor liquido de uma AF
	 * 
	 * @param valorBruto
	 * @param valorIpi
	 * @param valorAcrescimo
	 * @param valorDesconto
	 * @return Double
	 */
	Double obterValorLiquidoAf(Double valorBruto, Double valorIpi, Double valorAcrescimo, Double valorDesconto);

	/**
	 * Calcula o valor total de uma AF
	 * 
	 * @param valorLiquido
	 * @param valorEfetivado
	 * @return Double
	 */
	Double obterValorTotalAf(Double valorLiquido, Double valorEfetivado);

	/**
	 * Obtem o maior número de sequencia de alteração da AF
	 * 
	 * @param numAf
	 * @return
	 */
	Integer obterMaxSequenciaAlteracaoAF(Integer numAf);

	/**
	 * Obtem o andamento de uma AF
	 * 
	 * @param afnNumero
	 * @param numeroComplemento
	 * @return
	 */
	DominioAndamentoAutorizacaoFornecimento obterAndamentoAutorizacaoFornecimento(Integer afnNumero, Short numeroComplemento);

	/**
	 * Pesquisa os itens de AF ativos existentes para determinada AF, filtrando
	 * por situacao da AF
	 * 
	 * @param afnNumero
	 * @param filtraTipos
	 * @return List
	 */
	List<ScoItemAutorizacaoForn> pesquisarItemAfAtivosPorNumeroAf(Integer afnNumero, Boolean filtraTipos);

	/**
	 * Obter a lista de itens de uma af pela sua chave primária
	 * 
	 * @param afnNumero
	 * @return
	 */
	public List<ScoItemAutorizacaoForn> pesquisarItemAfPorNumeroAf(Integer afnNumero);
	
	/**
	 * Para itens com unidade de medida do fornecedor diferente da unidade de
	 * medida padrão do item obtem a descrição da quantidade do item de uma AF
	 * no formato: Quantidade Solicitada/Fator de Conversao do Fornecedor + Cód.
	 * da Unidade de Medida do Fornecedor + Fator de Conversao do Fornecedor +
	 * Unidade de Medida padrão + Valor unitário * Fator de conversão do
	 * Fornecedor
	 * 
	 */
	public String obterDescricaoQtdItemAF(ScoItemAutorizacaoForn itemAutForn, Integer qtd);

	/**
	 * Obtem o valor total da parcela do item da AF para uma SC
	 * 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @param qtd
	 * @param qtdEntregue
	 * @param valorEfetivado
	 * @return Double
	 */
	Double obterValorTotalProgEntregaItemAf(Integer iafAfnNum, Integer iafNumero, Integer qtd, Integer qtdEntregue, Double valorEfetivado);

	/**
	 * Obtem o nome do arquivo da bandeira com a cor definida conforme o
	 * andamento da AF
	 * 
	 * @param andamento
	 * @return String
	 */
	String obterNomeImagemAutorizacaoFornecimento(DominioAndamentoAutorizacaoFornecimento andamento);

	List<ScoAutorizacaoForn> pesquisarAutorizacaoFornecimentoValidasInsercao(Integer numeroAutorizacaoFornecimento);

	boolean isEmAf(ScoPropostaFornecedor proposta);

	/**
	 * Atualiza os dados possíveis de uma AF com a validações necessárias da
	 * tela
	 * 
	 * @param autorizacaoFornecimento
	 * @throws ApplicationBusinessException
	 */
	void gravarAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException;

	/**
	 * Atualiza os dados possíveis de uma AF com a validações somente da RN
	 * 
	 * @param autorizacaoFornecimento
	 * @throws ApplicationBusinessException
	 */
	void atualizarAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException;

	/**
	 * Pesquisa um item de AF pela solicitacao de servico
	 * 
	 * @param slsNumero
	 * @param filtraContrato
	 * @param filtraSituacao
	 * @return ScoItemAutorizacaoForn
	 */
	ScoItemAutorizacaoForn obterItemAfPorSolicitacaoServico(Integer slsNumero, Boolean filtraContrato, Boolean filtraSituacao);

	void gerarAf(Integer numPac, DominioModalidadeEmpenho modalidadeEmpenhoSelecionada) throws BaseException;

	List<ScoItemAutorizacaoFornJnVO> buscarListaItensJn(ScoAutorizacaoFornJn autForn) throws ApplicationBusinessException;

	/**
	 * Metodo que busca a autorizacao de fornecimento por numero do PAC e
	 * Complemento
	 * 
	 * @param numPac
	 * @param numComplemento
	 * @return ScoAutorizacaoForn
	 */
	ScoAutorizacaoForn buscarAutFornPorNumPac(Integer numPac, Short numComplemento);

	/**
	 * Retorna uma lista de até 10 AFs associadas à verba de gestão
	 * 
	 * @param verbaGestao
	 * @return List
	 */
	List<ScoAutorizacaoForn> pesquisarVerbaGestaoAssociadaAf(FsoVerbaGestao verbaGestao);

	Integer obterMaxNumEmpenhoPorAfeEspecie(Integer numAf, int numEsp) throws BaseException;

	ScoAfEmpenho obterEmpenhoPorChavePrimaria(Integer seqAf) throws BaseException;

	/**
	 * Realiza uma consulta nas tabelas de motivo de alteracao da AF para ser
	 * usada em suggestion
	 * 
	 * @param objPesquisa
	 * @return List
	 */
	List<ScoMotivoAlteracaoAf> listarScoMotivoAlteracaoAf(Object objPesquisa);

	/**
	 * Valida se a data de previsão de entrega de uma programação de entrega de
	 * item de AF já foi previamente utilizada em outra programação de entrega
	 * 
	 * @param afnNumero
	 * @param numeroItem
	 * @param parcelaAtual
	 * @param previsaoEntrega
	 * @return
	 */
	public Integer validarDataPrevisaoEntregaDuplicada(Integer afnNumero, Integer numeroItem, Integer parcelaAtual, Date previsaoEntrega);

	/**
	 * Obtem quantidade saldo do item da AF
	 * 
	 * @param qtdeSolicitada
	 * @param qtdeRecebida
	 * @return Integer
	 */
	Integer obterQtdeSaldoItemAF(Integer qtdeSolicitada, Integer qtdeRecebida);

	Double obterValorBrutoItemAF(Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado, Boolean isSc);

	/**
	 * Converte a unidade de medida da AF para a unidade de medida padrão do
	 * hospital
	 * 
	 * @param autorizacaoFornecimento
	 * @return
	 * @throws BaseException
	 */
	Integer converterUnidadeAf(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException;

	Double obterValorDescontoItemAF(Double percDesconto, Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada,
			Integer qtdeRecebida, BigDecimal valorEfetivado, Boolean isSc);

	/**
	 * Calcula a prioridade de uma programação de entrega item de AF baseado na
	 * data da solicitação, na pré-existência de prioridade e no recebimento do
	 * material
	 * 
	 * @param listaPea
	 * @param tipoSolicitacao
	 */
	void calcularPrioridadeEntrega(List<ParcelaItemAutFornecimentoVO> listaPea, DominioTipoSolicitacao tipoSolicitacao);

	/**
	 * Obtem o maior número de parcela para uma programação de entrega de um
	 * item de AF
	 * 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @return
	 */
	Integer obterMaxNumeroParcela(Integer iafAfnNum, Integer iafNumero);

	Double obterValorAcrescimoItemAF(Double percAcrescimo, Double percAcrescimoItem, Double valorUnitario, Integer qtdeSolicitada,
			Integer qtdeRecebida, BigDecimal valorEfetivado, Boolean isSc);

	/**
	 * Libera a assinatura de uma AF
	 * 
	 * @param autorizacaoFornecimento
	 * @throws BaseException
	 */
	void liberarAssinaturaAf(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException;

	Double obterValorIpiItemAF(Double percIPI, Double percAcrescimo, Double percAcrescimoItem, Double percDesconto,
			Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado,
			Boolean isSc);

	Double obterValorSaldoItemAF(Double percIPI, Double percAcrescimo, Double percAcrescimoItem, Double percDesconto,
			Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, BigDecimal valorEfetivado,
			Boolean isSc);

	Double obterValorTotalItemAF(Double valorEfetivado, Double percIPI, Double percAcrescimo, Double percAcrescimoItem,
			Double percDesconto, Double percDescontoItem, Double valorUnitario, Integer qtdeSolicitada, Integer qtdeRecebida, Boolean isSc);

	/**
	 * Retorna todas as parcelas nao assinadas do planejamento
	 * 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @return List
	 */
	List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero);

	/**
	 * obter descricao da solicitacao de compra ou servico
	 * 
	 * @param fases
	 * @return String
	 */
	String obterDescricaoSolicitacao(List<ScoFaseSolicitacao> fases);

	/**
	 * Obter número da solicitação de compra/serviço
	 * 
	 * @param fases
	 * @return Integer
	 */
	Integer obterNumeroSolicitacao(List<ScoFaseSolicitacao> fases);

	/**
	 * Obtem a maior sequencia de alteracao anterior a sequencia atual de uma AF
	 * 
	 * @param numAf
	 * @param sequenciaAlteracao
	 * @return Short
	 */
	Short obterMaxSequenciaAlteracaoAnteriorAfJn(Integer numAf, Short sequenciaAlteracao);

	/**
	 * Pesquisa um item de AF pela solicitacao de compra
	 * 
	 * @param slcNumero
	 * @param filtraContrato
	 * @param filtraSituacao
	 * @return ScoItemAutorizacaoForn
	 */
	ScoItemAutorizacaoForn obterItemAfPorSolicitacaoCompra(Integer slcNumero, Boolean filtraContrato, Boolean filtraSituacao);

	/**
	 * Persiste um prog entrega de item da AF executando as validacoes
	 * necessarias
	 * 
	 * @param progEntregaItemAutorizacaoFornecimento
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	void persistirProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento)
			throws ApplicationBusinessException;

	/**
	 * obtem dados material ou servico da solicitacao de compra ou servico
	 * 
	 * @param fases
	 * @return String
	 */
	MaterialServicoVO obterDadosMaterialServico(List<ScoFaseSolicitacao> fases);

	/**
	 * verifica se material do item esta em uma SC
	 * 
	 * @param fases
	 * @return boolean
	 */
	Boolean verificarMaterialFase(List<ScoFaseSolicitacao> fases);

	/**
	 * metodo que veirfica se o usuario tem permissao e a situacao da af para
	 * desabilitar edicao do registro
	 * 
	 * @param componente
	 * @param metodo
	 * @param situacaoAf
	 * @return boolean
	 */
	boolean desabilitarPermisaoSituacao(String login, String componente, String metodo, DominioSituacaoAutorizacaoFornecedor situacaoAf);

	/**
	 * Atualiza os itens de AF passando pelas validacoes necessarias
	 * 
	 * @param itemAutorizacaoForn
	 * @return
	 * @throws ApplicationBusinessException
	 */
	ScoItemAutorizacaoForn atualizarItemAutorizacaoFornecimento(ScoItemAutorizacaoForn itemAutorizacaoForn)
			throws ApplicationBusinessException;

	/**
	 * Obtem versões anteriores de uma AF.
	 * 
	 * @param numPac
	 *            Número do PAC
	 * @param nroComplemento
	 *            Número do Complemento
	 * @param first
	 * @param max
	 * @return Versões Anteriores
	 */
	public List<ScoAutorizacaoFornJn> buscarAutFornJNPorNumPacNumCompl(Integer numPac, Short nroComplemento, int first, int max);

	/**
	 * Obtem uma Programacao de Entrega de Item da AF pela pk (iafAfnNumero,
	 * iafNumero, seq e parcela)
	 * 
	 * @param id
	 * @return ScoProgEntregaItemAutorizacaoFornecimento
	 */
	ScoProgEntregaItemAutorizacaoFornecimento obterProgEntregaPorChavePrimaria(ScoProgEntregaItemAutorizacaoFornecimentoId id);

	/**
	 * Verifica se existe parcela ativa do planejamento nao assinada
	 * 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @return Boolean
	 */
	Boolean verificarProgEntregaItemAfPlanejamento(Integer iafAfnNum, Integer iafNumero);

	/**
	 * Conta versões anteriores de uma AF.
	 * 
	 * @param numPac
	 *            Número do PAC
	 * @param nroComplemento
	 *            Número do Complemento
	 * @return Versões Anteriores
	 */
	public Long contarAutFornJNPorNumPacNumCompl(Integer numPac, Short nroComplemento);

	/**
	 * Persiste uma lista de programação de entrega para um item de AF
	 * 
	 * @param listaPea
	 * @param listaPeaExclusao
	 * @param id
	 * @param previsaoEntrega
	 * @param valorParcelaAf
	 * @param qtdParcelaAf
	 * @param indPlanejada
	 * @param tipoSolicitacao
	 * @param novoRegistro
	 * @return
	 * @throws BaseException
	 */
	PersistenciaProgEntregaItemAfVO persistirProgEntregaItemAutorizacaoFornecimento(List<ParcelaItemAutFornecimentoVO> listaPea,
			List<ParcelaItemAutFornecimentoVO> listaPeaExclusao, ScoProgEntregaItemAutorizacaoFornecimentoId id, Date previsaoEntrega,
			Double valorParcelaAf, Integer qtdParcelaAf, DominioSimNao indPlanejada, DominioTipoSolicitacao tipoSolicitacao,
			Boolean novoRegistro, Double valorLiberar, Integer qtdLiberar) throws ApplicationBusinessException, BaseException;

	void excluirListaSolicitacaoProgramacao(List<ParcelaItemAutFornecimentoVO> listaPeaExclusao) throws BaseException;

	ScoAutorizacaoForn montarSolicitacaoProgEntrega(ParcelaItemAutFornecimentoVO parcela, DominioTipoSolicitacao tipoSolicitacao,
			ScoProgEntregaItemAutorizacaoFornecimento progEntrega, Date prevEntrega, Double valorParcela, Integer qtdeParcelaAf,
			DominioSimNao planejada, ScoProgEntregaItemAutorizacaoFornecimentoId id) throws BaseException;

	public List<PesquisaAutorizacaoFornecimentoVO> pesquisarListaAfsAssinar(Integer first, Integer max,
			String order, boolean asc, FiltroPesquisaAssinarAFVO filtro);

	RelatorioAFPVO pesquisarAFPedidoPorNumEPac(Integer afpNumero, Integer numeroPac, Short nroComplemento, int espEmpenho)
			throws ApplicationBusinessException;

	/**
	 * Libera a assinatura de uma programacao de entrega de item da AF
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	void liberarAssinaturaProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento item) throws BaseException;

	String verificarCaracteristicaAssinarAf();

	/**
	 * Obtem versão de uma AF.
	 * 
	 * @param id
	 *            ID da versão.
	 * @return Versão da AF.
	 */
	public ScoAutorizacaoFornJn obterScoAutorizacaoFornJn(Integer numeroAf, Short complementoAf, Short sequenciaAlteracao);

	/**
	 * Calcula valor total da AF com base em uma sequência de alteração
	 * (Journal).
	 * 
	 * @param numero
	 *            Número da AF
	 * @param seq
	 *            Sequência
	 * @return Valor Total
	 */
	public CalculoValorTotalAFVO getValorTotalAf(Integer numero, Integer seq);

	/**
	 * Realiza a confirmacao ou a gravacao da distribuicao feita nas
	 * solicitacoes
	 */
	List<PriorizaEntregaVO> gravarPriorizacaoEntrega(Boolean processoRecebimento, DominioTipoSolicitacao tipoSolicitacao,
			List<PriorizaEntregaVO> listaPriorizacao, Integer qtdeLimite, Double valorLimite) throws ApplicationBusinessException;

	/**
	 * Obtem responsáveis a partir da versão de uma AF.
	 * 
	 * @param numeroAf
	 * @param complementoAf
	 * @param sequenciaAlteracao
	 * @return Responsáveis
	 */
	public List<ResponsavelAfVO> obterResponsaveisAutorizacaoFornJn(Integer numeroAf, Short complementoAf, Short sequenciaAlteracao);

	/**
	 * Obtem lista de versões anteriores dos itens de uma versão da AF
	 * 
	 * @param numeroAF
	 * @param seqAF
	 * @param sequenciaAlteracao
	 * @return Lista de VO's dos itens
	 */
	public List<ItemAutFornecimentoJnVO> obterListaItemAutFornecimentoJnVO(Integer seqAF, Integer numeroAF, Integer sequenciaAlteracao);

	/**
	 * Obtem condição de pagamento a partir da versão de uma AF.
	 * 
	 * @param numeroAf
	 *            Número da AF
	 * @param complementoAf
	 *            Complemento da AF
	 * @param sequenciaAlteracao
	 *            Seq. Alteração
	 * @return Condição de pagamento.
	 */
	public ScoCondicaoPagamentoPropos obterCondPgtoAutorizacaoFornJn(Integer numeroAf, Short complementoAf, Short sequenciaAlteracao);

	public void validarComplementoAssinarAf(final FiltroPesquisaAssinarAFVO filtro) throws ApplicationBusinessException;

	/**
	 * Obtem o ultimo motivo de alteração da Autorização de Fornecimento
	 * 
	 * @param autForn
	 *            ScoAutorizacaoForn
	 * @return ScoMotivoAlteracaoAf
	 */
	public ScoMotivoAlteracaoAf buscarUltimoMotivoAlteracao(Integer numeroAf, Integer numeroPac, Short complementoAf)
			throws ApplicationBusinessException;

	public List<AutorizacaoFornPedidosVO> pesquisarAutFornPedidosPorFiltro(Integer first, Integer max, PesquisaAutFornPedidosVO filtroVO)
			throws ApplicationBusinessException;
	
	public Long pesquisarAutFornPedidosPorFiltroCount(PesquisaAutFornPedidosVO filtroVO);

	/**
	 * valida regras RN 16,17,19 ao marcar checkbox contrato
	 * 
	 * @param itemAutorizacaoForn
	 * @throws BaseException
	 */
	public void validarScContrato(ItensAutFornVO itemAutorizacaoForn) throws BaseException;

	/**
	 * verifica se item da af possue saldo
	 * 
	 * @param itemAutorizacaoForn
	 * @return Boolean
	 */
	public Boolean verificarExisteSaldo(ItensAutFornVO itemAutorizacaoForn);

	/**
	 * metodo que gera uma solicitacao de compra com o saldo existente do item
	 * da af
	 * 
	 * @param itemAutorizacaoForn
	 * @throws BaseException
	 */
	public void processarSolicitacaoCompra(ItensAutFornVO itemAutorizacaoForn) throws BaseException;

	/**
	 * metodo que atualiza a situacao e ind exclusao do item e da Af
	 * 
	 * @param itemAutorizacaoFornVo
	 * @throws BaseException
	 */
	public void alterarSituacaoItem(ItensAutFornVO itemAutorizacaoFornVo) throws BaseException;

	/**
	 * metodo que executa as alteraçoes no item da AF
	 * 
	 * @param item
	 * @throws BaseException
	 */
	public void alterarItemAF(ItensAutFornVO item) throws BaseException;

	/**
	 * Retorna uma lista das solicitacoes associadas a determinada programacao
	 * de entrega de um item de AF quando nao existe recebimento previo. Neste
	 * caso faz o rateio da quantidade recebidas entre as solicitacoes.
	 * 
	 * @param peaIafAfnNumero
	 * @param peaIafNumero
	 * @param peaSeq
	 * @param peaParcela
	 * @param nrpSeq
	 * @param nroItem
	 * @param qtdLimite
	 * @param valorLimite
	 * @param tipoSolicitacao
	 * @return
	 */
	List<PriorizaEntregaVO> pesquisarSolicitacaoProgEntregaItemAf(Integer peaIafAfnNumero, Short peaIafNumero, Integer peaSeq,
			Integer peaParcela, Integer nrpSeq, Integer nroItem, Integer qtdLimite, Double valorLimite,
			DominioTipoSolicitacao tipoSolicitacao);

	/**
	 * Valida valor unitario se valor efetivado >= (quantidade Solicitacao *
	 * valor unitario)
	 * 
	 * @param itemAutFornVO
	 * @throws ApplicationBusinessException
	 */
	public void validarValorUnitarioItemAF(ItensAutFornVO itemAutFornVO) throws ApplicationBusinessException;

	/**
	 * desabilita check box exclusao de acordo com a situacao do item
	 * 
	 * @param situacaoAf
	 * @return boolean
	 */
	public boolean desabilitarCheckExclusao(DominioSituacaoAutorizacaoFornecedor situacaoAf);

	/**
	 * Valida quandidade (qtdeSolicitada < qtdeRecebida) para solicitacao de
	 * compra e valor (valorEfetivado < valorUnitario) para solicitacao de
	 * servico
	 * 
	 * @param fasesSolicitacao
	 * @param qtdeRecebida
	 * @param qtdeSolicitada
	 * @param valorUnitario
	 * @param valorEfetivado
	 * @throws ApplicationBusinessException
	 */
	public void validarItemEntregue(List<ScoFaseSolicitacao> fasesSolicitacao, Integer qtdeRecebida, Integer qtdeSolicitada,
			Double valorUnitario, Double valorEfetivado) throws ApplicationBusinessException;

	/**
	 * item nao pode ser excluido e estornado ao mesmo tempo
	 * 
	 * @param itemAutFornVO
	 * @throws ApplicationBusinessException
	 */
	public void validarExclusaoEstornoItemAF(ItensAutFornVO itemAutFornVO) throws ApplicationBusinessException;

	public void validaIndConsignadoItemAF(ItensAutFornVO itemAutFornVO) throws BaseException;

	//void atualizarSituacaoAf(List<ItensAutFornVO> listaItens, ScoAutorizacaoForn autFornecimendo) throws BaseException;

	/***
	 * Quando desmarcado o ind contrato desmarca tambem o ind consignado do item
	 * da Af e exibe mensagem
	 * 
	 * @param itemAutorizacaoForn
	 * @throws ApplicationBusinessException
	 */
	public void validaIndConsignado(ItensAutFornVO itemAutorizacaoForn) throws ApplicationBusinessException;

	/**
	 * Retorna a lista de contatos por email do fornecedor.
	 * 
	 * @param numero
	 * @return lista de contatos de fornecedor que permitem receber emails
	 */
	public List<String> obterContatosPorEmailFornecedor(final ScoFornecedor fornecedor) throws BaseException;

	/**
	 * Verifica se existe Parcela NÃO Enviada ainda ao Fornecedor e com
	 * (DT_PREV_ENTREGA + Prazo Entrega) > Data Atual. -- Nesse caso deverá
	 * atualizar a DT_PREV_ENTREGA das respectivas Parcelas com a Data Atual
	 * (Hoje) + o Prazo de Entrega indicado pelo Fornecedor na Proposta, -- para
	 * que o Fornecedor tenha tempo hábil para entrega do material.
	 * 
	 * @param numeroAf
	 *            , numeroAfp, prazo de Entrega, data Prevista para entrega
	 * @return
	 */
	public void updateParcelasAFP(Integer numAf, Integer numAfp, Short prazoEntrega) throws ApplicationBusinessException;

	/**
	 * Grava Data envio da AFP ao Fornecedor somente se ainda não gravada
	 * (Preserva sempre a primeira Dt Envio)
	 * 
	 * @param numeroAf
	 *            , numeroAfp
	 * @return
	 */
	public void updateAFP(Integer numAf, Integer numAfp) throws ApplicationBusinessException;

	public List<PesquisaAutorizacaoFornecimentoVO> confirmarAssinaturaAf(Set<PesquisaAutorizacaoFornecimentoVO> lista) throws BaseException;

	public void cancelarAssinaturaAf(PesquisaAutorizacaoFornecimentoVO item) throws ApplicationBusinessException;

	/**
	 * Grava IND_ENVIO_FORNECEDOR nas tabelas SCO_AF_PEDIDOS e
	 * SCO_PROGR_ENTREGA_ITENS_AF para indicar que a AFP não deverá ser enviada
	 * ao fornecedor operação realizada através do botão 'Não Enviar' da tela de
	 * Pesquisa AF's Entregas Liberadas #5566
	 * 
	 * @param numeroAF
	 *            , numeroAFP
	 */
	public void alterarAFP(List<AutorizacaoFornPedidosVO> listaSel) throws ApplicationBusinessException;

	/**
	 * Pesquisas pelos itens de uma Autorização de Fornecimento Pedido
	 * utilizando os filtros passados. Funcionalidade implementada para estória
	 * #5564.
	 * 
	 * @param filtro
	 * @return Lista da PesquisaItemAFPVO que representam os itens de
	 *         autorização de fornecimento pedido para o filtro passado por
	 *         parâmetro.
	 */
	List<PesquisaItemAFPVO> pesquisarItensAFPedido(final FiltroAFPVO filtro);

	/**
	 * Registra a entrega dos itens de parcelas de recebimento gerando e
	 * retornando uma nota de recebimento provisório. Insere nas tabelas
	 * SceNotaRecebProvisorio, SceItemRecebProvisorio, SceItemRecbXProgrEntrega
	 * Atualiza as tabelas ScoSolicitacaoProgramacaoEntrega,
	 * ScoProgEntregaItemAutorizacaoFornecimento e ScoItemAutorizacaoForn
	 * 
	 * @param listaItemAF
	 * @param documentoFiscalEntrada
	 * @param servidorLogado
	 * @param force
	 * @return SceNotaRecebProvisorio
	 * @throws MECBaseException
	 * @throws ItemRecebimentoValorExcedido
	 */
	public SceNotaRecebProvisorio receberParcelaItensAF(List<RecebimentoMaterialServicoVO> listaItemAF,
			SceDocumentoFiscalEntrada documentoFiscalEntrada, RapServidores servidorLogado, ScoFornecedor fornecedor, boolean force)
			throws BaseException, ItemRecebimentoValorExcedido;

	/**
	 * Verifica necessidade de execução do procedimento Confirmação do
	 * Recebimento logo após a geração com sucesso da Nota de Recebimento
	 * Provisório
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarConfirmacaoImediataRecebimento() throws ApplicationBusinessException;

	/**
	 * Envia um email ao solicitante da compra de cada item que foi recebido
	 * avisando que o material se encontra disponível para conferência
	 * 
	 * @param listaItemAF
	 * @throws ApplicationBusinessException
	 */
	public void enviarEmailSolicitanteCompras(List<RecebimentoMaterialServicoVO> listaItemAF) throws ApplicationBusinessException;

	/**
	 * Pesquisa lista de parcelas com saldo de uma autorização de fornecimento
	 * 
	 * @param numero
	 *            da AF
	 * @return Retorna lista de VO das parcelas da AF
	 **/
	public List<RecebimentoMaterialServicoVO> pesquisarProgEntregaItensComSaldoPositivo(Integer numeroAF, Short complementoAf,
			DominioTipoFaseSolicitacao tipoSolicitacao);

	/**
	 * Busca lista de Autorização de Fornecimento com saldo na programação de
	 * entrega, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numero
	 *            da AF
	 * @param numero
	 *            de complemento de AF
	 * @param fornecedor
	 * @return Retorna a lista de Autorização de Fornecimento
	 **/
	public List<ScoAutorizacaoForn> pesquisarAFNumComplementoFornecedor(Object numeroAf, Short numComplementoAf, Integer numFornecedor,
			ScoMaterial material, ScoServico servico);

	/**
	 * Busca lista de complementos de AF que tenham saldo na programação de
	 * entrega, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numero
	 *            da AF
	 * @param numero
	 *            de complemento de AF
	 * @param fornecedor
	 * @return Retorna a lista de complmento de AF
	 **/
	public List<ScoAutorizacaoForn> pesquisarComplementoNumAFNumComplementoFornecedor(Integer numeroAf, Object numComplementoAf,
			Integer numFornecedor, ScoMaterial material, ScoServico servico);

	/**
	 * Calcula a qtde total entregue de programacoes de entrega por uma parcela
	 * item de af
	 * 
	 * @param iafAfnNumero
	 * @param iafNumero
	 * @param parcela
	 * @return Integer
	 */
	Integer calcularTotalEntreguePorItemAf(Integer iafAfnNumero, Integer iafNumero, Integer parcela);

	/**
	 * Busca lista de fornecedores cujas AF tenham saldo na programação de
	 * entrega, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numero
	 *            da AF
	 * @param numero
	 *            de complemento de AF
	 * @param fornecedor
	 * @return Retorna a lista de fornecedores
	 **/
	public List<ScoFornecedor> pesquisarFornecedorNumAfNumComplementoFornecedor(Integer numeroAf, Short numComplementoAf,
			Object fornFilter, ScoMaterial material, ScoServico servico, SceDocumentoFiscalEntrada documentoFiscalEntrada);

	/**
	 * Buscar lista de materiais presentes em parcelas de AF que ainda tenham
	 * saldo. Filtrando pelo número da af, e/ou número do complemento, e/ou
	 * fornecedor da AF, e/ou código do material ou sua descrição
	 * 
	 * @param numeroAf
	 * @param numComplementoAf
	 * @param numFornecedor
	 * @param param
	 * @return
	 */
	public List<ScoMaterial> pesquisarMaterialaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, String matFilter);

	/**
	 * Buscar lista de servicos presentes em parcelas de AF que ainda tenham
	 * saldo. Filtrando pelo número da af, e/ou número do complemento, e/ou
	 * fornecedor da AF, e/ou código do servico ou sua descrição
	 * 
	 * @param numeroAf
	 * @param numComplementoAf
	 * @param numFornecedor
	 * @param param
	 * @return
	 */
	public List<ScoServico> pesquisarServicoaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, String param);

	/**
	 * Verifica permissão para informar Nota Fiscal de Entrada se
	 * P_ENTRADA_NF_MAT_SERV.VlrTexto() == "C" return false, nao permite nota
	 * fiscal
	 * 
	 * @return true/false
	 * @throws ApplicationBusinessException
	 */
	public boolean permiteNotaFiscalEntrada() throws ApplicationBusinessException;

	/**
	 * Pesquisa um item de AF pela chave primaria
	 * 
	 * @param afNumero
	 * @param numero
	 * @return
	 */
	ScoItemAutorizacaoForn obterDadosItensAutorizacaoFornecimento(Integer afNumero, Integer numero);

	public List<ParcelasAutFornPedidoVO> pesquisarParcelasAfpPorFiltro(Integer numeroAF, Integer numeroAFP, DominioTipoFaseSolicitacao tipo);

	public void persistirAutorizacaoForn(ScoAutorizacaoForn autForn, boolean flush);

	void persistir(ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega) throws BaseException;

	/**
	 * Obtem o maior seq para determinada parcela de entrega de um item de AF
	 * 
	 * @param iafAfnNum
	 * @param iafNumero
	 * @param parcela
	 * @return Integer
	 */
	Integer obterMaxNumeroSeqParcelaItemAf(Integer iafAfnNum, Integer iafNumero, Integer parcela);

	/**
	 * Pesquisa os itens de solicitação pendentes PAC.
	 * 
	 * @param numeroLicitacao
	 * @return
	 */
	List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoPorNumeroLicitacao(Integer numeroLicitacao);

	/**
	 * Listar afs que não possuem contratos.
	 * 
	 * @param filtro
	 * @param param
	 * @return
	 */
	public List<AfsContratosFuturosVO> montarListaItemAutorizacaoForn(AfFiltroVO filtro, DominioModalidadeEmpenho param);

	public List<GeracaoAfVO> listarPacsParaAF(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer) throws ApplicationBusinessException;

	public Integer listarPacsParaAFCount(ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer)
			throws ApplicationBusinessException;

	/**
	 * Exclui parcelas pendentes de itens relacionados a uma SC.
	 * 
	 * @param sc
	 *            SC
	 */
	void excluirParcelasPendentes(ScoSolicitacaoDeCompra sc) throws BaseException;

	DominioTipoFaseSolicitacao getItemAutorizacaoFornecedorFaseSolicitacaoTipoPorAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoForn);

	List<PesquisaGeralAFVO> listarAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<PesquisaGeralIAFVO> listarItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<PesquisaGeralPAFVO> listarPedidosAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro);

	List<PesquisaGeralPIAFVO> listarParcelasItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro);

	Integer obterQtdeEntregaPendente(Integer codMat);

	Long listarAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro);

	Long listarItensAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro);

	Long listarPedidosAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro);

	Long listarParcelasItensAutorizacoesFornecimentoFiltradoCount(FiltroPesquisaGeralAFVO filtro);

	Long listProgEntregaFornecedorCount(AcessoFornProgEntregaFiltrosVO filtro);

	void validaFiltrosProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro) throws ApplicationBusinessException;

	List<AFPFornecedoresVO> listProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro, Boolean isCount, Integer firstResult,
			Integer maxResult);

	List<ParcelasAFPendEntVO> listarParcelasAFsPendentes(FiltroParcelasAFPendEntVO filtro, Integer firstResult, Integer maxResult,
			String order, boolean asc) throws ApplicationBusinessException;

	Long listarParcelasAFsPendentesCount(FiltroParcelasAFPendEntVO filtro);

	AutorizacaoFornecimentoEmailAtrasoVO carregarLabelsParaTelaAutFornEmailAtraso(Integer numeroAutorizacaoFornecedor);

	AutorizacaoFornecimentoEmailVO montarCorpoEmail(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException;

	void enviarEmailFornecedor(AutorizacaoFornecimentoEmailVO dadosEmail) throws ApplicationBusinessException;

	List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoGeral(Integer numeroAf, Integer numeroComplemento)
			throws ApplicationBusinessException;

	List<ConsultaProgramacaoEntregaItemVO> obterProgramacaoPendente(Integer numeroAf, Integer numeroComplemento)
			throws ApplicationBusinessException;

	ScoAutorizacaoFornecedorPedido obterScoAutorizacaoFornecedorPedidoPorChavePrimaria(ScoAutorizacaoFornecedorPedidoId id);

	void validarDatas(Date dtInicial, Date dtFinal) throws ApplicationBusinessException;

	String determinarProcessamentoAFs(AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO,
			List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException;

	void enviarEmailMultiplosFornecedores(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException;

	void selecionarAFs(ParcelasAFPendEntVO parcela, List<ParcelasAFPendEntVO> afsPendentes, List<ParcelasAFPendEntVO> afsSelecionados);

	Boolean desativarAtivarPeriodoEntrega(FiltroParcelasAFPendEntVO filtro);

	ScoContatoFornecedor obterPrimeiroContatoPorFornecedor(ScoFornecedor fornecedor);

	void selecionarTodasAFs(boolean todasAFsSelecionadas, List<ParcelasAFPendEntVO> listaAFs, List<ParcelasAFPendEntVO> listaAFsSelecionadas);

	void desfazerSelecaoTodasAFs(List<ParcelasAFPendEntVO> listaAFs, List<ParcelasAFPendEntVO> listaAFsSelecionadas);

	List<PesquisarPlanjProgrEntregaItensAfVO> pesquisarProgrEntregaItensAf(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException;

	Long pesquisarProgrEntregaItensAfCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro) throws ApplicationBusinessException;

	void liberarEntrega(List<PesquisarPlanjProgrEntregaItensAfVO> lista, Date dataPrevisaoEntrega, RapServidores usuarioLogado)
			throws ApplicationBusinessException;

	Date obterDataLiberacaoEntrega(Date dataPrevisaoEntrega) throws ApplicationBusinessException;

	// #24878
	List<ProgrGeralEntregaAFVO> pesquisarItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro,Integer firstResult, Integer maxResult) throws ApplicationBusinessException;

	ValidacaoFiltrosVO validarVisualizacaoFiltros(FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO camposVO);

	void pesquisarInfoComplementares(ProgrGeralEntregaAFVO item);

	List<ProgramacaoEntregaItemAFVO> listarProgramacaoEntregaItensAF(Integer numeroAF);

	String buscarQuantidadeAFsProgramadas(Integer codigoMaterial, Integer afnNumero);

	void verificaAtualizacaoRegistro(ProgramacaoEntregaItemAFVO programacaoEntregaItemAFVO,
			List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAFAlterados);

	AlteracaoEntregaProgramadaVO gerarParcelasParaMatDiretos(DominioSimNao entregaProgramada, Integer afNumero)
			throws ApplicationBusinessException;

	List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItemNumeroLctNumeroComplemento(Integer numeroAf, Short numeroComplemento);

	List<ExcluirProgramacaoEntregaItemAFVO> pesquisarListaProgrEntregaItensAfExclusao(Integer numeroAF);

	void excluirListaProgrEntregaItensAf(Integer numeroAF, List<ExcluirProgramacaoEntregaItemAFVO> listaItensProgramacao);

	void excluirProgrEntregaItensAf(Integer numeroAF) throws ApplicationBusinessException;

	List<ProgramacaoEntregaGlobalFornecedoresVO> listarProgramacaoEntregaGlobalFornecedores(Integer codigoGrupoMaterial, Date dataInicial,
			Date dataFinal, String tipoValor, ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO,
			ScoFornecedor fornecedor);

	void gravarProgramacaoEntregaItemAF(List<ProgramacaoEntregaItemAFVO> listaProgramacaoEntregaItemAF) throws ApplicationBusinessException;

	AlteracaoEntregaProgramadaVO gerarProgramacaoParcela(Integer afNumero, Boolean gerarProgramacao);

	boolean verificarGeracaoParcelas(AlteracaoEntregaProgramadaVO alteracaoVO);

	void manterAutorizacaoFornecimento(Integer numeroAF, List<ConsultaItensAFProgramacaoEntregaVO> listVO) throws BaseException;

	List<ConsultaItensAFProgramacaoEntregaVO> consultarItensAFProgramacaoEntrega(Integer numeroAF) throws BaseException;

	void gravarParcelaEntregaMaterial(List<ConsultarParcelasEntregaMateriaisVO> listaParcelas, ConsultarParcelasEntregaMateriaisVO vo) throws ApplicationBusinessException;

	List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPendentesMaterial(Integer numeroAf, Short numeroComplemento);

	List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItemNumeroAfNumeroComplemento(Integer numeroAf, Short numeroComplemento)
			throws ApplicationBusinessException;

	List<ConsultaItensAFProgramacaoManualVO> consultarItensAFProgramacaoManual(Integer numeroItem, Integer numeroAF,
			Short numeroComplemento, Integer numeroFornecedor, Integer codigoMaterial, Integer codigoGrupoMaterial, Boolean isIndProgramado)
			throws ApplicationBusinessException;

	ModalAlertaGerarVO preGerarProgramacao(ProgramacaoManualParcelasEntregaFiltroVO filtro,
			List<ConsultaItensAFProgramacaoManualVO> listaItensAF, ModalAlertaGerarVO modalAlertaGerarVO)
			throws ApplicationBusinessException;

	void validarPesquisa(ProgramacaoManualParcelasEntregaFiltroVO filtro) throws ApplicationBusinessException;

	void gerarProgramacao(ProgramacaoManualParcelasEntregaFiltroVO filtro, List<ConsultaItensAFProgramacaoManualVO> listaItensAF)
			throws BaseException;


	public List<ConsultarParcelasEntregaMateriaisVO> buscarEntregasPorItem() throws ApplicationBusinessException;
	
	public Long countItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro) throws ApplicationBusinessException;

	public String verificaCorFundoPrevEntregas(Date dtPrevEntrega) throws ApplicationBusinessException;
	
	public List<VScoPacientesCUM> pesquisarPacientesCUMPorAFeAFP(Integer afeAfnNumero, Integer afeNumero);
	
	public Boolean publicaAfpFornecedorEntrega(Integer afnNumero, Integer afeNumero) throws ApplicationBusinessException;
}

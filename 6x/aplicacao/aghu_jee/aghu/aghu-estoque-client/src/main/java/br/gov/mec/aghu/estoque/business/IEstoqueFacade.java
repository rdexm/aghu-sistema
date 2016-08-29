package br.gov.mec.aghu.estoque.business;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.vo.*;
import br.gov.mec.aghu.compras.vo.GeraSolicCompraEstoqueVO;
import br.gov.mec.aghu.compras.vo.ItensAutFornUpdateSCContrVO;
import br.gov.mec.aghu.compras.vo.SociosFornecedoresVO;
import br.gov.mec.aghu.dominio.DominioAgruparRelMensal;
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia;
import br.gov.mec.aghu.dominio.DominioEstocavelConsumoSinteticoMaterial;
import br.gov.mec.aghu.dominio.DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque;
import br.gov.mec.aghu.dominio.DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioOrdenacaoConsumoSinteticoMaterial;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioTransferenciaMaterial;
import br.gov.mec.aghu.dominio.DominioOrderBy;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPesquisaNotaRecebimento;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.vo.ClassificacaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.ComposicaoGruposVO;
import br.gov.mec.aghu.estoque.vo.ConfirmacaoRecebimentoFiltroVO;
import br.gov.mec.aghu.estoque.vo.EntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.EntradaSaidaSemLicitacaoVO;
import br.gov.mec.aghu.estoque.vo.EstatisticaEstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.EstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.EstoqueGeralVO;
import br.gov.mec.aghu.estoque.vo.EstoqueMaterialVO;
import br.gov.mec.aghu.estoque.vo.FiltroMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.GerarNotaRecebimentoVO;
import br.gov.mec.aghu.estoque.vo.GrupoMaterialNumeroSolicitacaoVO;
import br.gov.mec.aghu.estoque.vo.HistoricoScoMaterialVO;
import br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.ItemNotaRecebimentoVO;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioRelVO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.estoque.vo.MaterialClassificacaoVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoVO;
import br.gov.mec.aghu.estoque.vo.PacoteMateriaisVO;
import br.gov.mec.aghu.estoque.vo.PendenciasDevolucaoVO;
import br.gov.mec.aghu.estoque.vo.PesquisaRequisicaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.PosicaoFinalEstoqueVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.estoque.vo.QuantidadesVO;
import br.gov.mec.aghu.estoque.vo.RecebimentoFiltroVO;
import br.gov.mec.aghu.estoque.vo.RelatorioAjusteEstoqueVO;
import br.gov.mec.aghu.estoque.vo.RelatorioConsumoSinteticoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioContagemEstoqueParaInventarioVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDetalhesAjusteEstoqueVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDiarioMateriaisComSaldoAteVinteDiasVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisEstocaveisGrupoCurvaAbcVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisValidadeVencidaVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMensalMateriaisClassificacaoAbcVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMensalMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioTransferenciaMaterialVO;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialVO;
import br.gov.mec.aghu.estoque.vo.SceEntrSaidSemLicitacaoVO;
import br.gov.mec.aghu.estoque.vo.SceRelacionamentoMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceSuggestionBoxMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.TipoMovimentoVO;
import br.gov.mec.aghu.estoque.vo.ValidaConfirmacaoRecebimentoVO;
import br.gov.mec.aghu.internacao.vo.SceRmrPacienteVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoComposicao;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceCfop;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumosId;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceFornecedorMaterial;
import br.gov.mec.aghu.model.SceHistoricoFechamentoMensal;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemPacoteMateriais;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceItemRmpsId;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceItemTransferenciaId;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceLoteId;
import br.gov.mec.aghu.model.SceMotivoMovimento;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceMovimentoMaterialId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.model.SceRefCodes;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedor;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedorJn;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceReqMaterialRetornos;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoesId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSiasgMaterialMestre;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VSceItemNotaRecebimentoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface IEstoqueFacade extends Serializable {

	/**
	 * Persiste ScoMaterial
	 * 
	 * @param material
	 * @throws BaseException
	 */
	void persistirScoMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException;

	/**
	 * Insere SceEstoqueAlmoxarifado
	 * 
	 * @param estoqueAlmoxarifado
	 * @throws BaseException
	 */
	void inserirSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) throws BaseException;

	void estornarMedicamentoRequisicaoMaterial(AfaDispensacaoMdtos dispensacaoMdto, AfaDispensacaoMdtos dispensacaoMdtoOld,
			String etiqueta, String nomeMicrocomputador) throws BaseException;

	List<SceReqMaterial> pesquisaRequisicoesMateriaisEstornar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SceReqMaterial sceReqMateriais);

	Long pesquisaRequisicoesMateriaisEstornarCount(SceReqMaterial sceReqMateriais);

	List<SceAlmoxarifado> obterAlmoxarifadoPorSeqDescricao(String param);

	SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(Short seqAlmoxarifadoOrigem,
			Integer codigoMaterial, Integer fornecedor);

	SceTipoMovimento obterSceTipoMovimentosAtivoPorSeq(Short seq);
	
	SceTipoMovimento obterTipoMovimentoPorChavePrimaria(Object param);

	List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoAjustes(String param) throws ApplicationBusinessException;

	List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoBloqueioDesbloqueioComProblema(String param, boolean mostrarTodos)
			throws ApplicationBusinessException;

	List<SceMotivoMovimento> obterMotivoMovimentoPorSeqDescricaoETMV(Short tmvSeq, Byte tmvComplemento, Object param);

	List<GerarNotaRecebimentoVO> pesquisarAutorizacoesFornecimentoPorSeqDescricao(Object param);

	List<VSceItemNotaRecebimentoAutorizacaoFornecimento> pesquisarMaterialPorAutorizacaoFornecimentoNaoEfetivadaOuParcialmenteAtendida(
			Integer afnNumero);

	SceItemRms obterItemRmsOriginal(SceItemRms itemRmsModificado);

	SceAlmoxarifadoTransferenciaAutomatica obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(Short almSeq,
			Short almSeqRecebe);

	SceTransferencia obterTransferenciaPorSeq(Integer seq);

	Long pesquisarTransferenciaAutoAlmoxarifadoCount(SceTransferencia transferencia);

	Long pesquisarEstornoTransferenciaAutoAlmoxarifadoCount(SceTransferencia transferencia);

	List<SceTransferencia> pesquisarTransferenciaAutoAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceTransferencia transferencia);

	SceItemTransferencia obterItemTransferenciaPorChave(Integer ealSeq, Integer trfSeq);

	String obterNomeMaterialItemTransferencia(Integer ealSeq, Integer trfSeq);

	Long pesquisaRequisicoesMateriaisCount(PesquisaRequisicaoMaterialVO filtro);

	List<SceReqMaterial> pesquisaRequisicoesMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			PesquisaRequisicaoMaterialVO filtro);

	Long pesquisaRequisicoesMateriaisEfetivarCount(PesquisaRequisicaoMaterialVO filtro);

	List<SceReqMaterial> pesquisaRequisicoesMateriaisEfetivar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			PesquisaRequisicaoMaterialVO filtro);

	List<ScePacoteMateriais> pesquisarPacoteMateriaisParaTrasnferenciaEventual(String _input);

	List<ScePacoteMateriais> pesquisarPacoteMateriaisGerarRequisicaoMaterial(Short seqAlmoxarifado, Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer codigoGrupoMaterial, String objPesquisa);

	SceReqMaterial obterRequisicaoMaterial(Integer seqReq);

	SceReqMaterial obterOriginal(Integer seqReq);

	Long pesquisarEstoqueMaterialPorAlmoxarifadoCount(Short almoxSeq, Integer numeroFornecedor, Integer codMaterial);

	List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifado(Short almoxSeq, Integer numeroFornecedor, Object paramPesq);

	EstatisticaEstoqueAlmoxarifadoVO obterEstatisticasAlmoxarifadoPorMaterialAlmoxDataComp(Short almoxSeq, Short almSeqConsumo,
			Integer codMaterial, Date dtCompetencia) throws BaseException;

	List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoOrderByFornEAlmx(Integer firstResult, Integer maxResult,
			Short almoxSeq, Integer numeroFornecedor, Integer codMaterial);

	Long pesquisarHistoricosProblemaPorFiltroCount(Integer codigoMaterial, Short almoxSeq, Integer codFornecedor, Short motivoProblema,
			Integer fornecedorEntrega);

	List<SceHistoricoProblemaMaterial> pesquisarHistoricosProblemaPorFiltro(Integer firstResult, Integer maxResult, Integer codigoMaterial,
			Short almoxSeq, Integer codFornecedor, Short motivoProblema, Integer fornecedorEntrega);

	SceHistoricoProblemaMaterial obterHistoricosProblemaPorSeq(Integer seqHistorico);

	List<SceMotivoProblema> pesquisaMotivosProblemasPorSeqDescricao(String paramPesq, DominioSituacao situacao);

	List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorPacote(Integer codigoGrupo, ScePacoteMateriaisId pacote);

	void preencheConsumoMedioItemRequisicao(SceItemRms itemRms, Integer codigoCCAplicacao) throws ApplicationBusinessException;

	List<SceFornecedorEventual> obterFornecedorEventual(Object param);

	void validaCamposObrigatorios(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException;

	List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriais(Integer reqMaterialSeq);

	SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorSeq(Integer seq);

	SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorSeqFornecedor(Integer seq, Integer numeroFornecedor);

	SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorNumeroSerieFornecedor(Long numero, String serie, Integer numeroFornecedor);

	SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorNumeroSerie(Long numero, String serie);

	List<SceNotaRecebimento> pesquisarNotaRecebimentoPorDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada);

	RequisicaoMaterialVO buscaMateriaisItensImprimir(Integer reqMat, DominioOrderBy orderBy, boolean inserirMedia) throws BaseException;

	List<PosicaoFinalEstoqueVO> buscaDadosPosicaoFinalEstoque(Date dtCompetencia, Integer codigoGrupo, String estocavel, String orderBy,
			Integer fornecedor, String siglaTipoUsoMdto, Short almoxSeq) throws BaseException;

	NotaRecebimentoVO pesquisaDadosNotaRecebimento(Integer numNotaRec, boolean isConsiderarNotaEmpenho) throws BaseException;

	SceNotaRecebimento obterSceNotaRecebimento(Integer seq);

	ImpImpressora defineImpressoraImpressao(SceReqMaterial reqMateriais) throws BaseException;

	SceEstoqueAlmoxarifado buscaSceEstoqueAlmoxarifadoPorId(Integer seq);

	/**
	 * Obtém almoxarifados ativos por código ou descrição
	 * 
	 * @param parametro
	 *            Código ou descrição
	 * @return Lista de almoxarifados
	 */

	List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricao(Object parametro);

	/**
	 * Obtém almoxarifados por código ou descrição
	 * 
	 * @param parametro
	 *            Código ou descrição
	 * @return Lista de almoxarifados
	 */
	List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(Object parametro);

	/**
	 * Recupera lista de itens do relatório de Material com Validade Vencida <br>
	 * 
	 * @return List<RelatorioMateriaisValidadeVencidaVO>
	 * @author rodrigo.figueiredo
	 * @throws ApplicationBusinessException
	 * @since 13/09/2011
	 */
	List<RelatorioMateriaisValidadeVencidaVO> pesquisarDadosRelatorioMaterialValidadeVencida(Short seqAlmoxarifado, Integer codigoGrupo,
			Date dataInicial, Date dataFinal, Integer numeroFornecedor) throws ApplicationBusinessException;

	/**
	 * Gera o relatório de materiais com validade vencida como arquivo CSV
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param dataInicial
	 * @param dataFinal
	 * @param numeroFornecedor
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	String gerarCSVRelatorioRelatorioMaterialValidadeVencida(Short seqAlmoxarifado, Integer codigoGrupo, Date dataInicial, Date dataFinal,
			Integer numeroFornecedor) throws IOException, ApplicationBusinessException;

	public boolean dataValida(Date dtEmissaoInicial, Date dtEmissaoFinal);
	
	/**
	 * Efetua o download do relatório gerado em CSV
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	void efetuarDownloadCSVMaterialValidadeVencida(final String fileName) throws IOException;

	List<RelatorioAjusteEstoqueVO> pesquisarDadosRelatorioAjusteEstoque(Date dataCompetencia, List<String> siglasTipoMovimento);

	/**
	 * Obtém dados do Relatório de Contagem de Estoque para Inventário
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param estocavel
	 * @param ordem
	 * @return List<RelatorioContagemEstoqueParaInventarioVO>
	 */
	List<RelatorioContagemEstoqueParaInventarioVO> pesquisarDadosRelatorioContagemEstoqueInventario(Short seqAlmoxarifado,
			Integer codigoGrupo, DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, boolean disponivelEstoque) throws ApplicationBusinessException;

	/**
	 * Gera um arquivo CSV do Relatório de Contagem de Estoque para Inventário
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoGrupo
	 * @param estocavel
	 * @param ordem
	 * @param mostraSaldo 
	 * @return String Nome do Arquivo
	 * @throws IOException
	 */
	String geraCSVRelatorioContagemEstoqueInventario(Short seqAlmoxarifado, Integer codigoGrupo,
			DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, boolean disponivelEstoque, boolean mostraSaldo) throws IOException;

	/**
	 * Pesquisa por todos os campos que forem peenchidos.
	 * 
	 * @param reqMaterial
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param dataLimite
	 * @param dtLimiteInferior
	 * @return
	 * @author bruno.mourao
	 * @since 24/04/2012
	 */
	List<SceReqMaterial> pesquisarRequisicaoMaterialFiltroCompleto(SceReqMaterial reqMaterial, int firstResult, int maxResults,
			String orderProperty, boolean asc, Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material);

	/**
	 * Efetua o count da pesquisa realizada por todos os campos
	 * 
	 * @param reqMaterial
	 * @param dataLimite
	 * @param dtLimiteInferior
	 * @return
	 * @author bruno.mourao
	 * @since 24/04/2012
	 */
	Long pesquisarRequisicaoMaterialFiltroCompletoCount(SceReqMaterial reqMaterial, Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material);

	/**
	 * Verifica se existe reforco em determinada solicitacao de compras
	 * 
	 * @param slcNumero
	 * @return Boolean
	 */
	Boolean verificarExisteReforcoSolicitacaoCompras(Integer slcNumero);

	/**
	 * Pesquisa notas de recebimento para consulta
	 * 
	 * @param seqNotaRecebimento
	 * @param estorno
	 * @param debitoNotaRecebimento
	 * @param situacaoPesquisaNR
	 * @param responsavelSituacao
	 * @param dataSituacao
	 * @param numeroProcessoCompAutorizacaoForn
	 * @param numeroComplementoAutorizacaoForn
	 * @param situacaoAutorizacaoForn
	 * @param numeroFornecedor
	 * @param numDocFiscalEntrada
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return List<SceNotaRecebimento>
	 */
	List<SceNotaRecebimento> pesquisarNotasRecebimentoConsulta(Integer seqNotaRecebimento, Boolean estorno, Boolean debitoNotaRecebimento,
			DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, Date dataSituacao, Date dataFinal,
			Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn,
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	/**
	 * Efetua o count da pesquisa notas de recebimento para consulta
	 * 
	 * @param seqNotaRecebimento
	 * @param estorno
	 * @param debitoNotaRecebimento
	 * @param situacaoPesquisaNR
	 * @param responsavelSituacao
	 * @param dataSituacao
	 * @param numeroProcessoCompAutorizacaoForn
	 * @param numeroComplementoAutorizacaoForn
	 * @param situacaoAutorizacaoForn
	 * @param numeroFornecedor
	 * @param numDocFiscalEntrada
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return count
	 */
	Long pesquisarNotasRecebimentoConsultaCount(Integer seqNotaRecebimento, Boolean estorno, Boolean debitoNotaRecebimento,
			DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, Date dataSituacao, Date dataFinal,
			Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn,
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada);

	SceNotaRecebimento obterNotaRecebimento(Integer seqNotaRecebimento);

	SceNotaRecebimento obterNotaRecebimento(Integer seqNotaRecebimento, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	SceNotaRecebimento obterSceNotaRecebimentoFULL(Integer seq);

	List<ItemNotaRecebimentoVO> pesquisarItensNotaRecebimento(Integer seqNotaRecebimento);

	/**
	 * 
	 * @param pSiglaTipoMovimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	String obterSiglaTipoMovimento(AghuParametrosEnum pSiglaTipoMovimento) throws ApplicationBusinessException;

	/**
	 * Gera o Relatório de Ajuste de Estoque
	 * 
	 * @param dtCompetencia
	 * @param siglasTipoMovimento
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	String gerarCSVRelatorioAjusteEstoque(Date dtCompetencia, List<String> siglasTipoMovimento) throws IOException,
			ApplicationBusinessException;

	/**
	 * Gera o relatório de materiais classificação ABC como arquivo CSV.
	 * 
	 * @param dtCompetencia
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	String gerarCSVRelatorioMensalMateriaisClassificacaoAbc(Date dtCompetencia) throws ApplicationBusinessException, IOException;

	/**
	 * Recupera lista de itens do relatório mensal de materiais Classificação
	 * ABC <br>
	 * 
	 * @param mesCompetencia
	 *            ;
	 * @param grupo
	 *            ;
	 * @return List<RelatorioMensalMateriaisClassificacaoAbcVO>
	 * 
	 * @since 05/09/2011
	 */
	List<RelatorioMensalMateriaisClassificacaoAbcVO> pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(Date mesCompetencia)
			throws ApplicationBusinessException;

	public Boolean mostrarBotaoRecebimentoAntigo();

	/**
	 * Efetua a pesquias de Estoque Geral
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param dataCompetencia
	 * @param fornecedor
	 * @param matCodigo
	 * @param nomeMaterial
	 * @param gmtCodigo
	 * @param indEstocavel
	 * @param indGenerico
	 * @param umdCodigo
	 * @param clasABC
	 * @param subClasABC
	 * @return List<SceEstoqueGeral>
	 */
	List<SceEstoqueGeral> pesquisarEstoqueGeral(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Date dataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia, Integer fornecedor, Integer matCodigo,
			String nomeMaterial, Integer gmtCodigo, Boolean indEstocavel, Boolean indGenerico, String umdCodigo, DominioClassifABC clasABC,
			DominioClassifABC subClasABC);
	
	List<SceEstoqueGeral> listarEstoqueMaterial(Integer codigoMaterial);

	/**
	 * Efetua o count da pesquisa de Estoque Geral
	 * 
	 * @param dataCompetencia
	 * @param fornecedor
	 * @param matCodigo
	 * @param nomeMaterial
	 * @param gmtCodigo
	 * @param indEstocavel
	 * @param indGenerico
	 * @param umdCodigo
	 * @param clasABC
	 * @param subClasABC
	 * @return count da pesquisa
	 */
	Long pesquisarEstoqueGeralCount(Date dataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia, Integer fornecedor,
			Integer matCodigo, String nomeMaterial, Integer gmtCodigo, Boolean indEstocavel, Boolean indGenerico, String umdCodigo,
			DominioClassifABC clasABC, DominioClassifABC subClasABC);

	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e
	 * ano.
	 * 
	 * @param paramPesquisa
	 * @return
	 * 
	 */
	List<EstoqueGeralVO> pesquisarDatasCompetenciasEstoqueGeralPorMesAno(Object paramPesquisa) throws ApplicationBusinessException;

	/**
	 * Obtem um movimento de material
	 * 
	 * @param mmtId
	 * @return SceMovimentoMaterial
	 */
	SceMovimentoMaterial obterMovimetnoMaterialPorId(SceMovimentoMaterialId mmtId);

	RelatorioDetalhesAjusteEstoqueVO obterDetalhesAjusteEstoque(Integer mmtSeq);

	/**
	 * Persist um objeto SceMovimentoMaterial
	 * */
	void persistirMovimentoMaterial(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador) throws BaseException;

	/**
	 * Persist um objeto SceValidade
	 * */
	void inserirValidadeMaterial(SceValidade sceValidade, SceMovimentoMaterial movimento) throws BaseException;

	void inserirValidadeMaterial(SceValidade validade) throws BaseException;

	void excluirValidadeMaterial(SceValidade validade) throws BaseException;

	void atualizarValidadeMaterial(SceValidade validade) throws BaseException;

	void inserirValidadeMaterial(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeDisponivel) throws BaseException;

	/**
	 * Persist um objeto SceValidade
	 * */
	void atualizarValidadeMaterial(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeValidades, boolean somaNoFinal)
			throws BaseException;

	List<SceLote> listarLotesPorCodigoOuMarcaComercialEMaterial(Object objPesquisa, Integer codigoMaterial);

	SceValidade obterValidadePorDataValidadeEstoqueAlmoxarifado(Date dtValidade, Integer seqEstoqueAlmoxarifado);

	List<SceValidade> listarValidadesPorEstoqueAlmoxarifado(Integer ealSeq);

	List<SceLoteFornecedor> pesquisarLoteFornecedorPorMaterialValidade(Integer lotMatCodigo, Date dtValidade);

	/**
	 * Persist um objeto SceValidade
	 * */
	void inserirLoteFornecedor(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes)
			throws BaseException;

	/**
	 * Persist um objeto SceValidade
	 * */
	void atualizarLoteFornecedor(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes,
			Integer loteQtdeAnterior) throws BaseException;

	Boolean verificarAutorizacaoFornecimentoSaldoNotaRecebimento(Integer afnNumero, Short sequenciaAlteracao);

	/**
	 * Busca data da última movimentação do material
	 * 
	 * @param codMaterial
	 *            , almSeq, tmvSeq
	 * @return Date
	 */
	Date obterDtUltimaMovimentacao(Integer codMaterial, Short almSeq, Short tmvSeq);

	/**
	 * Busca data da última compra do material
	 * 
	 * @param codMaterial
	 *            , almSeq
	 * @return Date
	 */
	Date obterDtUltimaCompra(Integer codMaterial, Short almSeq);

	/**
	 * Busca valor da ultima compra do material
	 * 
	 * @param codMaterial
	 *            , almSeq, tmvSeq
	 * @return BigDecimal
	 */
	BigDecimal obterValorUltimaCompra(Integer codMaterial, Short almSeq, Short tmvSeq);

	List<SceMovimentoMaterial> pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(Object parametro) throws ApplicationBusinessException;

	Long pesquisarEstornarNotaRecebimentoCount(SceNotaRecebimento notaRecebimento);

	List<SceNotaRecebimento> pesquisarEstornarNotaRecebimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SceNotaRecebimento notaRecebimento);

	Long pesquisarDocumentoFiscalEntradaCount(SceDocumentoFiscalEntrada documentoFiscalEntrada);

	List<SceDocumentoFiscalEntrada> pesquisarDocumentoFiscalEntrada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceDocumentoFiscalEntrada documentoFiscalEntrada);

	void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException;

	void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador)
			throws BaseException;

	void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador, boolean flush)
			throws BaseException;

	/**
	 * Verifica se um recebimento provisorio eh referente a servico ou material
	 * 
	 * @param notaRecebProv
	 * @return Boolean
	 */
	Boolean verificarRecebimentoServico(SceNotaRecebProvisorio notaRecebProv);

	void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento, Short numeroItem,
			ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException;

	void atualizarNotaRecebimento(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal,
			String nomeMicrocomputador, Boolean flush) throws BaseException;

	void persistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException;

	void removerDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException;

	void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;

	void estornarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;

	void persistirRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;

	void persistirItensRequisicaoMaterial(SceItemRms sceItemRms, Boolean estorno, String nomeMicrocomputador) throws BaseException;

	void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException;

	void persistirEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador) throws BaseException;

	void desbloqueioQuantidadesEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador) throws BaseException;

	void bloqueioQuantidadesEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador) throws BaseException;

	void bloqueioDesbloqueioPersistirLote(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador, Boolean bloquear, SceLoteDocumento loteDocs) throws BaseException;

	void bloqueioDesbloqueioAtualizarLote(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador, Boolean bloquear, SceLoteDocumento loteDocs) throws BaseException;

	void bloqueioDesbloqueioQuantidadesProblema(SceHistoricoProblemaMaterial historico, SceTipoMovimento tipoMovimento,
			Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException;

	void verificaBotaoVoltarBloqueioDesbloqueioProblema(SceHistoricoProblemaMaterial historico, SceTipoMovimento tipoMovimento,
			List<SceValidade> listaValidadeInicial, Boolean mostrouGradeValidade) throws BaseException;

	void verificaBotaoVoltarBloqueioDesbloqueio(Integer estoqueAlmoxarifado) throws BaseException;

	List<SceItemNotaRecebimento> pesquisaItensNotaRecebimentoUltimos60DiasPormaterial(Integer codMaterial, Object param);

	List<SceLoteDocumento> pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(Integer estoqueAlmoxarifado, Integer codMaterial);

	void persistirSceLoteDocumento(SceLoteDocumento loteDocumento) throws BaseException;

	void atualizarSceLoteDocumento(SceLoteDocumento loteDocumento) throws BaseException;

	List<SceEntradaSaidaSemLicitacao> pesquisarEntradaSaidaPorMaterial(Integer codMaterial, Object param);

	/**
	 * Obtém itens nota recebimento
	 * 
	 * @param notaRecebimento
	 * @return List<SceItemNotaRecebimento>
	 */
	List<SceItemNotaRecebimento> obterItensNotaRecebimento(SceNotaRecebimento notaRecebimento);

	void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException;

	/**
	 * Obtém lista de materiais para catálogo
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 * @throws BaseListException
	 */
	List<ScoMaterial> pesquisarListaMateriaisParaCatalogo(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial, DominioSimNao estocavel, DominioSimNao generico,
			String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ, Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo,
			VScoClasMaterial classificacaoMaterial);

	/**
	 * Obtem a NR atraves da seq da Nota de Recebimento Provisorio
	 * 
	 * @param nrpSeq
	 * @return SceNotaRecebimento
	 */
	SceNotaRecebimento obterNotaRecebimentoPorNotaRecebimentoProvisorio(Integer nrpSeq);

	/**
	 * 
	 * @param numTransferenciaMaterial
	 * @param ordemImpressao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<RelatorioTransferenciaMaterialVO> gerarDadosRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial,
			DominioOrdenacaoRelatorioTransferenciaMaterial ordemImpressao) throws ApplicationBusinessException;

	/**
	 * 
	 * @param numTransferenciaMaterial
	 * @param parametros
	 * @param ordem
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	String gerarCsvRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial, List<RelatorioTransferenciaMaterialVO> dados,
			Map<String, Object> parametros) throws FileNotFoundException, IOException;

	boolean isFatItensContaHospitalarComMateriaisOrteseseProteses(final Integer cthSeq, final Short seq, final Integer phiSeq)
			throws ApplicationBusinessException;

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param dtGeracao
	 * @param indTransfAutomatica
	 * @param sceAlmoxarifadoSeq
	 * @param sceAlmoxarifadoRecebSeq
	 * @param numero
	 * @param descricao
	 * @param nome
	 * @param estorno
	 * @return
	 */
	List<SceTransferencia> pesquisarTransferenciaAutomatica(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer seq, Date dtGeracao, Boolean indTransfAutomatica, Short sceAlmoxarifadoSeq, Short sceAlmoxarifadoRecebSeq, Long numero,
			String descricao, String nome, Boolean estorno);

	/**
	 * 
	 * @param seq
	 * @param dtGeracao
	 * @param indTransfAutomatica
	 * @param sceAlmoxarifadoSeq
	 * @param sceAlmoxarifadoRecebSeq
	 * @param numero
	 * @param descricao
	 * @param nome
	 * @param estorno
	 * @return
	 */
	Long pesquisarTransferenciaAutomaticaCount(Integer seq, Date dtGeracao, Boolean indTransfAutomatica, Short sceAlmoxarifadoSeq,
			Short sceAlmoxarifadoRecebSeq, Long numero, String descricao, String nome, Boolean estorno);

	/**
	 * Pesquisa Pendencias de Confirmacao de Devolucao ao Fornecedor pelo numero
	 * da NR
	 * 
	 * @param numeroNr
	 * @return List
	 */
	List<PendenciasDevolucaoVO> pesquisarPendenciasDevolucao(Integer numeroNr);

	/**
	 * 
	 * @param codigo
	 * @return
	 */
	SceTransferencia obterTransferenciaAutomatica(Integer codigo);

	SceTransferencia obterTransferenciaPorId(Integer seqTransf);

	SceTransferencia obterTransferenciaOriginal(Integer seqTransf);

	List<SceItemTransferencia> pesquisarListaItensTransferenciaPorTransferencia(Integer seqTransferencia);

	List<ItemTransferenciaAutomaticaVO> pesquisarListaItensTransferenciaVOPorTransferencia(Integer seqTransferencia);

	void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO> listaItemTransferenciaAutomaticaVO,
			String nomeMicrocomputador) throws BaseException;

	void atualizarTransferenciaAutoAlmoxarifado(SceTransferencia transferencia, String nomeMicrocomputador) throws BaseException;

	void atualizarTransferenciaAutoAlmoxarifado(SceTransferencia transferencia, SceTransferencia oldTransferencia,
			String nomeMicrocomputador) throws BaseException;

	SceItemTransferencia obterItemTransferenciaPorId(SceItemTransferenciaId id);

	void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;

	void removerTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;

	void removerTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento,
			Long numeroClassifMatNiv5) throws BaseException;

	void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;

	void removerItemTransferenciaAutoAlmoxarifado(SceItemTransferencia itemTransferencia) throws BaseException;

	Boolean existeTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento,
			Long numeroClassifMatNiv5);

	/**
	 * Remove o pacote de materiais
	 * 
	 * @param pacote
	 * @throws BaseListException
	 */
	void removerPacoteMaterial(final ScePacoteMateriais pacote) throws BaseListException;

	/**
	 * 
	 * @param codigo
	 * @return
	 */
	FccCentroCustos obterFccCentroCustos(final Integer codigo);

	/**
	 * 
	 * @param pacoteId
	 * @return
	 */
	ScePacoteMateriais obterPacoteMateriaisComItensPorChavePrimaria(ScePacoteMateriaisId pacoteId);

	/**
	 * Pesquisa PacoteMateriais de acordo com os parâmetros informados
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @param numeroAlmoxarifado
	 * @param situacao
	 * @return
	 */
	List<ScePacoteMateriais> pesquisarPacoteMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote, Short numeroAlmoxarifado,
			DominioSituacao situacao);

	/**
	 * Retorna a quantidade de pacotes de materiais obtidos na pesquisa
	 */
	Long pesquisarPacoteMateriaisCount(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote,
			Short numeroAlmoxarifado, DominioSituacao situacao);

	/**
	 * Pesquisa os almoxarifados ativos, por código e descrição, ordenados pela
	 * descrição
	 * 
	 * @param parametro
	 * @return
	 */
	List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(Object parametro);

	/**
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numero
	 * @return
	 */
	List<SceItemPacoteMateriais> pesquisarItensPacoteMateriais(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao,
			Integer numero);

	/**
	 * Realiza as operacoes de persistencia das listas de priorizacao de entrega
	 * quando utilizado diretamente pela tela de confirmacao de recebimento ou
	 * desbloqueio de material, realizando as validacoes necessarias
	 * 
	 * @param listaPriorizacao
	 */
	void persistirSceItemRecbXProgrEntrega(List<PriorizaEntregaVO> listaPriorizacao) throws ApplicationBusinessException;

	/**
	 * Obtém pacote de materiais como objeto de valor, com variáveis primitivas,
	 * necessárias para pesquisa
	 * 
	 * @param id
	 * @return
	 */
	PacoteMateriaisVO obterPacoteMaterialVO(ScePacoteMateriaisId id);

	/**
	 * Obtém almoxarifado por Id
	 * 
	 * @param seq
	 * @return
	 */
	SceAlmoxarifado obterAlmoxarifadoPorId(Short seq);

	/**
	 * Retorna ítens de pacote de materiais como VO's
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 * @throws ApplicationBusinessException
	 */
	List<ItemPacoteMateriaisVO> pesquisarItensPacoteMateriaisVO(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao,
			Integer numeroPacote) throws ApplicationBusinessException;

	/**
	 * Realiza a confirmacao da devolucao ao fornecedor retornando os itens
	 * confirmados com sucesso
	 * 
	 * @param listaPendencias
	 * @param seqDfe
	 * @param seqNotaRecebimento
	 * @param numeroDfs
	 * @param serieDfs
	 * @param dataDfs
	 * @param nomeMicroComputador
	 * @return
	 * @throws BaseException
	 */
	List<String> confirmarDevolucaoFornecedor(List<PendenciasDevolucaoVO> listaPendencias, Integer seqDfe, Integer seqNotaRecebimento,
			Long numeroDfs, String serieDfs, Date dataDfs, String nomeMicroComputador) throws BaseException;

	/**
	 * Realiza o cancelamento da Devolucao ao Fornecedor retornando os itens
	 * cancelados com sucesso
	 * 
	 * @param listaPendencias
	 * @param numeroDfs
	 * @param serieDfs
	 * @param dataDfs
	 * @param nomeMicroComputador
	 * @param seqNrp
	 * @return List
	 * @throws BaseException
	 */
	List<String> cancelarDevolucaoFornecedor(List<PendenciasDevolucaoVO> listaPendencias, Long numeroDfs, String serieDfs, Date dataDfs,
			String nomeMicroComputador, Integer seqNrp) throws BaseException;

	/**
	 * Persiste (ou atualiza) no banco de dados o pacote de materiais, com ítens
	 * 
	 * @param pacote
	 * @throws BaseListException
	 */
	void persistirPacoteMaterais(ScePacoteMateriais pacote) throws BaseListException;

	/**
	 * Pesquisa Estoque Almoxarifado através do Almoxarifado e Material Obs.
	 * Esta pesquisa considera somente o Fornecedor Padrão/HU
	 * 
	 * @param seqAlmoxarifado
	 * @param numeroFornecedor
	 * @param codigoMaterial
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoFornecedorPadrao(Short seqAlmoxarifado, Integer codigoMaterial) throws ApplicationBusinessException;

	/**
	 * Obtém o ítem de pacote de material, a ser armazenado no grid, com campos
	 * necessários
	 * 
	 * @param seqEstoqueAlmoxarifado
	 * @param codigoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	ItemPacoteMateriaisVO obterItemPacoteMateriaisVO(Integer seqEstoqueAlmoxarifado, Integer codigoMaterial)
			throws ApplicationBusinessException;

	/**
	 * Método que realiza validações para inserção de ítens
	 * 
	 * @param itensPacote
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @param seqEstoque
	 * @param quantidade
	 * @param codigoMaterial
	 * @param seqAlmoxarifadoPai
	 * @param seqAlmoxarifadoFilho
	 * @param situacaoAlmoxarifadoPai
	 * @throws BaseListException
	 * @throws ApplicationBusinessException
	 */
	void verificarInclusaoItensPacoteMateriais(List<ItemPacoteMateriaisVO> itensPacote, Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numeroPacote, Integer seqEstoque, Integer quantidade, Integer codigoMaterial,
			Short seqAlmoxarifadoPai, Short seqAlmoxarifadoFilho, DominioSituacao situacaoAlmoxarifadoPai) throws BaseListException,
			ApplicationBusinessException;

	/**
	 * Método que valida se o último ítem da lista está sendo removido
	 * 
	 * @param quantidade
	 * @throws ApplicationBusinessException
	 */
	void verificarUltimoItemPacoteMateriais(int quantidade) throws ApplicationBusinessException;

	// 6617
	List<SceEstoqueAlmoxarifado> pesquisarSceEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SceEstoqueAlmoxarifado estoqueAlmox);

	// 6617
	Long pesquisarSceEstoqueAlmoxarifadoCount(SceEstoqueAlmoxarifado estoqueAlmox);

	Long pesquisarSceAlmoxTransfAutomaticasCount(Short almoxOrigem, Short almoxDestino, DominioSituacao situacao);

	List<SceAlmoxarifadoTransferenciaAutomatica> pesquisarSceAlmoxTransfAutomaticas(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Short almoxOrigem, Short almoxDestino, DominioSituacao situacao);

	void inserirSceAlmoxTransfAutomaticas(SceAlmoxarifadoTransferenciaAutomatica almoxarifadoTransferenciaAutomatica) throws BaseException;

	void atualizarSceAlmoxTransfAutomaticas(SceAlmoxarifadoTransferenciaAutomatica almoxarifadoTransferenciaAutomatica)
			throws BaseException;

	void excluirSceAlmoxTransfAutomaticas(SceAlmoxarifadoTransferenciaAutomatica almoxarifadoTransferenciaAutomatica) throws BaseException;

	/**
	 * Realiza a pesquisa de requisições de materiais da Consulta Geral
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param automatica
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @return
	 */
	List<SceReqMaterial> pesquisarRequisicaoMaterialConsultaGeral(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, Boolean automatica, String nomeImpressora, Short almSeq,
			Integer ccReq, Integer ccApli, DominioImpresso impresso);

	/**
	 * Realiza a pesquisa de requisições de materiais da Consulta RM
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nroRM
	 * @param dtGeracao
	 * @param dtConfirmacao
	 * @param dtEfetivacao
	 * @param indSituacao
	 * @param indEstorno
	 * @param nomeImpressora
	 * @param almSeq
	 * @param ccReq
	 * @param ccApli
	 * @param impresso
	 * @return
	 */
	List<SceReqMaterial> pesquisarRequisicaoMaterialConsultaRM(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao, DominioSituacaoRequisicaoMaterial indSituacao,
			Boolean indEstorno, String nomeImpressora, Short almSeq, Integer ccReq, Integer ccApli, DominioImpresso impresso,
			List<FccCentroCustos> hierarquiaCc, List<SceAlmoxarifado> listaAlmox, List<DominioSituacaoRequisicaoMaterial> listaSituacao);

	/**
	 * Retorna a quantidade de requisições encontradas na Consulta Geral
	 */
	Long pesquisarRequisicaoMaterialConsultaGeralCount(Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, Boolean automatica, String nomeImpressora, Short almSeq,
			Integer ccReq, Integer ccApli, DominioImpresso impresso);

	/**
	 * Retorna a quantidade de requisições encontradas na Consulta RM
	 */
	Long pesquisarRequisicaoMaterialConsultaRMCount(Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, String nomeImpressora, Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso, List<FccCentroCustos> hierarquiaCc, List<SceAlmoxarifado> listaAlmox,
			List<DominioSituacaoRequisicaoMaterial> listaSituacao);

	/**
	 * Pesquisa nomes das impressoras das requisições de materiais
	 * 
	 * @param parametro
	 * @return
	 */
	List<LinhaReportVO> pesquisarNomesImpressorasRequisicaoMaterial(String parametro);

	/**
	 * Pesquisa almoxarifados que possuem o mesmo centro de custo do usuário
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 * 
	 */
	List<SceAlmoxarifado> pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(String parametro, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

	List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisCurvaAbc(boolean considerarCompras);

	List<MovimentoMaterialVO> pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(String parametro) throws ApplicationBusinessException;

	List<MovimentoMaterialVO> pesquisarMovimentosMaterial(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao,
			SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor,
			SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer nroDocGeracao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException;

	Long pesquisarMovimentosMaterialCount(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao,
			SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor,
			SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer nroDocGeracao);

	void validaCamposPesquisaMovimentoMaterial(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao,
			SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor,
			SceMovimentoMaterial movimentoMaterialDataCompetencia, Integer nroDocGeracao) throws ApplicationBusinessException;

	List<RelatorioMensalMaterialVO> pesquisarDadosRelatorioMensalMaterial(Date dataCompetencia, DominioAgruparRelMensal agrupar)
			throws BaseException;

	String gerarCSVRelatorioMateriaisComSaldoAteVinteDias(List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> dados)
			throws ApplicationBusinessException, IOException;

	List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarMateriaisComSaldoAteVinteDias(Integer duracaoEstoque, Double percAjuste)
			throws ApplicationBusinessException;

	String efetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(String fileName) throws IOException;

	ScePacoteMateriais obterPacoteMateriais(ScePacoteMateriaisId idPacoteMateriais);

	List<SceEstoqueAlmoxarifado> pesquisarMateriaisPorTransferencia(Short almoxSeq, Short almoxSeqReceb, Integer frnNumero, String paramPesq);

	void efetivarRequisicaoMaterialAutomatica(Date date, String cron, String nomeMicrocomputador) throws ApplicationBusinessException;
	
	Long pesquisarMateriaisPorTransferenciaCount(Short almoxSeq, Short almoxSeqReceb, Integer frnNumero, String paramPesq);

	void inserir(SceItemTransferencia itemTransferencia) throws BaseException;

	SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoOrigem(Short almSeq, Integer matCodigo, Integer frnNumero)
			throws ApplicationBusinessException;

	/**
	 * Método que retorna uma lista de itens de requisição de materiais
	 * 
	 * @param seqRequisicaoMateriais
	 * @return
	 */
	List<SceItemRms> pesquisarItensRequisicaoMateriais(Integer seqRequisicaoMaterial);

	Long pesquisarEstoqueAlmoxarifadoValidadeMaterialCount(SceEstoqueAlmoxarifado estoqueAlmox);

	List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoValidadeMaterial(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceEstoqueAlmoxarifado estoqueAlmox);

	void tratarDispensacaoMedicamentoEstoque(AfaDispensacaoMdtos dispMdtoNew, String etiqueta, String nomeMicrocomputador)
			throws BaseException;

	Long obterQtdeDispByUnfAndMaterial(Short seqUnf, Integer matCodigo);

	/**
	 * Método que retorna uma lista de almoxarifados.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param centroCustos
	 * @param diasEstoqueMinimo
	 * @param central
	 * @param calculaMediaPonderada
	 * @param bloqueiaEntTransf
	 * @param situacao
	 * @return
	 */
	List<SceAlmoxarifado> listarAlmoxarifados(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short codigo,
			String descricao, Integer codigoCentroCustos, Integer diasEstoqueMinimo, Boolean central, Boolean calculaMediaPonderada,
			Boolean bloqueiaEntTransf, DominioSituacao situacao);

	/**
	 * Método que retorna a quantidade de Almoxarifados.
	 * 
	 * @param codigo
	 * @param descricao
	 * @param centroCustos
	 * @param diasEstoqueMinimo
	 * @param central
	 * @param calculaMediaPonderada
	 * @param bloqueiaEntTransf
	 * @param situacao
	 * @return
	 */
	Long listarAlmoxarifadosCount(Short codigo, String descricao, Integer codigoCentroCustos, Integer diasEstoqueMinimo, Boolean central,
			Boolean calculaMediaPonderada, Boolean bloqueiaEntTransf, DominioSituacao situacao);

	void gravarAlmoxarifado(SceAlmoxarifado alterado) throws ApplicationBusinessException, ApplicationBusinessException;

	void atualizarProcessoAlmoxarifado(final Short seq, String processo) throws ApplicationBusinessException;

	Boolean isAlmoxarifadoAlterado(SceAlmoxarifado alterado);

	List<SceDevolucaoAlmoxarifado> pesquisarDevolucaoAlmoxarifado(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo,
			Boolean estornada, Boolean pesquisaEstorno, Date dtInicio, Date dtFim);

	List<SceDevolucaoAlmoxarifado> pesquisarDevolucaoAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, Boolean estorno, Boolean pesquisaEstorno,
			Date dtInicio, Date dtFim);

	Long pesquisarDevolucaoAlmoxarifadoCount(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, Boolean estorno,
			Boolean pesquisaEstorno, Date dtInicio, Date dtFim);

	List<ItemDevolucaoAlmoxarifadoVO> pesquisarItensComMaterialDevolucaoAlmoxarifado(Integer numeroDaSelecionado);

	List<ItemDevolucaoAlmoxarifadoVO> pesquisarItensComMaterialLoteDocumentosPorDevolucaoAlmoxarifado(Integer dalSeq);

	void estornarDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException;

	List<RelatorioDevolucaoAlmoxarifadoVO> gerarDadosRelatorioDevolucaoAlmoxarifado(Integer numeroDevAlmox)
			throws ApplicationBusinessException, ApplicationBusinessException;

	String gerarCsvRelatorioDevolucaoAlmoxarifado(Integer numeroDevolAlmox, List<RelatorioDevolucaoAlmoxarifadoVO> dados,
			Map<String, Object> parametros) throws FileNotFoundException, IOException;

	SceLoteDocImpressao getLoteDocImpressaoByNroEtiqueta(String etiqueta) throws ApplicationBusinessException;

	List<SceLoteDocImpressao> pesquisarPorDadosInformados(SceLoteDocImpressao entidade);

	Double getCustoMedioPonderado(Integer matCodigo, Date dataCompetencia, Integer frnNumero);

	List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(Object param);

	SceDevolucaoAlmoxarifado obterDevolucaoAlmoxarifadoPorChavePrimaria(Object chavePrimaria);

	SceDevolucaoAlmoxarifado obterDevolucaoAlmoxarifadoOriginal(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado);

	void inserirDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) throws ApplicationBusinessException;

	void inserirItemDevolucaoAlmoxarifado(SceItemDas itemDevolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException;

	Boolean habilitarCampoDataFinalConsultarNotaRecebimento(Date dataSituacao);

	void verificaDataSituacaoInicialDataSituacaoFinal(Date dataSituacao, Date dataFinal) throws ApplicationBusinessException;

	Boolean verificarValidadeItemDaPersistido(SceDocumentoValidade docValidade);

	void persistirDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoNew,
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO, String nomeMicrocomputador) throws BaseException;

	void verificarQuantidadeValidadesContraItemDa(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO, SceDocumentoValidade novaValidade)
			throws ApplicationBusinessException;

	void verificarQuantidadeDocumentoValidade(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO, SceDocumentoValidade novaValidade)
			throws ApplicationBusinessException;

	void verificarQuantidadeLoteContraQuantidadeValidade(SceLoteDocumento lote, SceDocumentoValidade validade)
			throws ApplicationBusinessException;

	void verificarValidadeDataDuplicada(List<SceDocumentoValidade> listaDocumentoValidade, SceDocumentoValidade novaValidade)
			throws ApplicationBusinessException;

	void verificarItemDaDuplicado(ItemDevolucaoAlmoxarifadoVO novoItemDevolucaoAlmoxarifadoVO,
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO) throws ApplicationBusinessException;

	List<AghAtendimentosVO> listarAtendimentosPaciente(Integer pacCodigo) throws ApplicationBusinessException;

	SceItemRmps obterSceItemRmpsPorChavePrimaria(final SceItemRmpsId chavePrimaria);

	SceItemRmps buscarItemRmpsPorRmpSeq(final Integer rmpSeq);

	SceRmrPaciente obterSceRmrPacientePorChavePrimaria(final Integer seq);

	void removerItemPacoteMaterial(SceItemPacoteMateriais item);

	void inserirTransferenciaMaterialEventual(SceTransferencia transferencia) throws BaseException;

	List<SceItemTransferencia> pesquisarListaItensTransferenciaEventual(SceTransferencia transferencia);

	void atualizarItemTransfEventual(SceItemTransferencia itemTransferencia, String nomeMicrocomputador) throws BaseException;

	List<ItemPacoteMateriaisVO> pesquisarItensTrsEventualPacoteMateriaisVO(ScePacoteMateriais pacote, Short almSeq);

	void inserirItemTransferenciaEventual(List<ItemPacoteMateriaisVO> listItemPacoteMateriaisVO, SceTransferencia transferencia)
			throws BaseException;

	Long pesquisarTransferenciaMateriaisEventualCount(SceTransferencia transferencia);

	List<SceTransferencia> pesquisarTransferenciaMateriaisEventual(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceTransferencia transferencia);

	void inserirMovimentoMaterial(SceMovimentoMaterial elemento, String nomeMicrocomputador) throws BaseException;

	Integer recupararQuantidadeDisponivelTodosEstoques(EstoqueAlmoxarifadoVO estoque);

	Double getValorUnitarioLiquido(Double valorUnitarioItemAutorizacaoFornecimento, Integer quantidadeRecebida, Double percAcrescimo,
			Double percAcrescimoItem, Double percDesconto, Double percDescontoItem, Double percIpi);

	List<SceHistoricoFechamentoMensal> pesquisarHistoricoFechamentoMensalPorDataCompetencia(Date dataCompetencia);

	SceAlmoxarifado obterAlmoxarifadoPorSeq(Short seq);

	Double buscarUltimoCustoEntradaPorMaterialTipoMov(ScoMaterial matCodigo, Short shortValue);

	Integer obterConsumoTotalNosUltimosSeisMeses(Integer codigoMat);

	Integer obterConsumoTotalNosUltimosSeisMesesPeloMedicamento(Integer codigoMat) throws BaseException;

	List<SceTipoMovimento> obterTipoMovimentoPorSigla(Object param);

	SceReqMaterialRetornos obterSceReqMaterialRetornosPorChavePrimaria(Integer seq);

	List<SceConversaoUnidadeConsumos> listarConversaoUnidadeConsumo(Integer codigo);

	Long listarConversaoUnidadeConsumoFiltroCount(ScoMaterial material, ScoUnidadeMedida unidadeMedida, BigDecimal fatorConversao);

	List<SceConversaoUnidadeConsumos> listarConversaoUnidadeConsumoFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, ScoMaterial material, ScoUnidadeMedida unidadeMedida, BigDecimal fatorConversao);

	void persistirConversao(SceConversaoUnidadeConsumos conversao) throws ApplicationBusinessException;

	void atualizarConversao(SceConversaoUnidadeConsumos conversao);

	void excluirConversao(SceConversaoUnidadeConsumosId conversaoId);

	SceConversaoUnidadeConsumos obterConversaoUnidadeConsumos(SceConversaoUnidadeConsumosId id);

	SceEstoqueAlmoxarifado obterSceEstoqueAlmoxarifadoPorChavePrimaria(Integer seq);

	List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado(Integer fornecedorNumero, Integer ealSeq);

	void persistirSceMovimentoMaterial(SceMovimentoMaterial sceMovimentoMaterial);

	List<SceRmrPaciente> listarRmrPacientesPorSeqNfGeralNaoNula(Integer seq);

	List<SceRmrPacienteVO> listarRmrPacientePorRmpSeqENumero(Integer rmpSeq, Short numero);

	List<SceRmrPacienteVO> listarSceRmrPacienteVOPorCirurgiaESituacao(final Integer crgSeq, final DominioSituacao situacao);

	SceLote obterSceLotePorChavePrimaria(SceLoteId sceLoteId);

	List<SceItemRmps> listarItensRmpsPorRmpSeqNfNaoNula(Integer seq);

	Short obterProximoNumero(Integer seq);

	SceItemRmps inserirSceItemRmps(SceItemRmps sceItemRmps, boolean flush);

	SceItemRmps atualizarSceItemRmps(SceItemRmps sceItemRmps, boolean flush);

	SceFornecedorMaterial obterFornecedorMaterialPorFornecedorNumeroEMaterialCodigo(Integer frnNumero, Integer matCodigo);

	List<SceRefCodes> buscarSceRefCodesPorTipoOperConversao(final String valor, final String dominio);

	void inserirSceFornecedorMaterial(SceFornecedorMaterial sceFornecedorMaterial, boolean flush);

	void atualizarSceFornecedorMaterial(SceFornecedorMaterial sceFornecedorMaterial, boolean flush);

	List<SceLoteDocImpressao> efetuarPesquisaUnitarizacaoDeMedicamentosComEtiqueta(SceLoteDocImpressao entidadePesquisa,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	Long efetuarPesquisaUnitarizacaoDeMedicamentosComEtiquetaCount(SceLoteDocImpressao entidadePesquisa);

	public abstract void efetuarInclusao(SceLoteDocImpressao entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			Boolean comReducao) throws IllegalStateException, BaseException;

	List<String> obterRazoesExcessaoEtiquetasExtras();

	List<HistoricoScoMaterialVO> identificarAlteracoesScoMaterial(ScoMaterialJN atual, ScoMaterialJN anterior)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

	ScoMaterial obterMaterialPorId(Integer codigo);

	Boolean habilitarConfaz();

	List<ScoMaterialJN> pesquisarScoMaterialJNPorCodigoMaterial(final Integer codigoMaterial, DominioOperacoesJournal operacao);

	List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(Integer reqMaterialSeq, DominioOrderBy orderBy,
			DominioSituacaoRequisicaoMaterial indSituacao);

	List<RelatorioConsumoSinteticoMaterialVO> pesquisarRelatorioConsumoSinteticoMaterial(final Integer cctCodigo, final Short almSeq,
			final DominioEstocavelConsumoSinteticoMaterial estocavel, final Long cn5Numero, final Date dt_competencia,
			final DominioOrdenacaoConsumoSinteticoMaterial ordenacao, final ScoGrupoMaterial grupoMaterial);

	SceRmrPaciente inserirSceRmrPaciente(SceRmrPaciente sceRmrPaciente, boolean flush);

	SceRmrPaciente atualizarSceRmrPaciente(SceRmrPaciente sceRmrPaciente, boolean flush);

	void removerSceRmrPaciente(SceRmrPaciente sceRmrPaciente, boolean flush);

	void removerSceItemRmps(SceItemRmps sceItemRmps, boolean flush);

	Long obterListaSceItemRmpsPorSceRmrPacienteCount(Integer seq);

	Long countSceRmrPaciente(Integer rmpSeq);

	List<SceMovimentoMaterial> obterConsumoMensal(Integer codigoMaterial, Date dtCompetencia);

	Integer consumoMedioSazonal(Date data1, Date data2, Date data3, Integer codMaterial, Short almSeq,
			DominioIndOperacaoBasica indOperacaoBasica);

	SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterialUnidadeMedida(ScoMaterial material, ScoUnidadeMedida unidadeMedida);

	SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterial(ScoMaterial material);

	List<FcpMoeda> pesquisarMoeda(Object parametro);

	FcpMoeda obterMoedaPorChavePrimaria(Object chaveBusca);

	Double getUltimoValorCompra(ScoMaterial scoMaterial) throws ApplicationBusinessException;

	List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(Short seqAlmoxarifado,
			Integer codigoMaterial, Integer numeroFornecedor);

	Long quantidadeEfetivadaItensAF(Integer numeroAF, Integer numeroItem);

	List<ItensAutFornUpdateSCContrVO> pesquisarFaseSolicitacaoItemAF(ScoItemAutorizacaoFornId itemAutorizacaoFornId, Date pdataCompetencia,
			Integer pFornecedorPadrao);

	void atualizarPontoPedido(final Date dataCompetencia, final Integer codigoMaterial) throws BaseException;

	Integer buscarEstoqueAlmoxarifadoPorAutFornNumeroItlNumeroFornPadrao(Integer afNumero, Short itlNumero, Integer fornecedorPadrao);

	void atualizarEstoqueAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado);

	SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoPorChavePrimaria(Integer seqEstoqueAlmoxarifado);

	SceEstoqueGeral pesquisarEstoqueGeralPorMatDtCompFornecedor(Integer codMaterial, Date dataCompetencia, Integer numero);

	void atualizarEstoqueAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, String nomeMicrocomputador) throws BaseException;

	SceHistoricoProblemaMaterial pesquisarQtdeBloqueadaPorProblema(Integer ealSeq);

	List<SceLoteDocImpressao> pesquisarLoteDocImpSemLoteDocumento(SceLoteDocImpressao entidade);

	boolean existeEstoqueAlmoxarifadoItemAFConsignado(Integer afnNumero, Integer numero);

	Integer obterSaldosEstoqueAlmoxarifadoItemAFConsignado(Integer afnNumero, Integer numero);

	Integer obterQtdeConsignadaEstoqueGeralItemAF(Integer afnNumero, Integer numero);

	/**
	 * Realiza a pesquisa de notas de recebimento provisório para realizar a
	 * confirmação do recebimento.
	 * 
	 * 
	 * #25016 - Confirmação de recebimento de Materiais e Serviços
	 * 
	 * @param filtroVO
	 *            * Integer nrpSeq - seq da nota de recebimento provisório; *
	 *            Integer lctNumero - número AF; * Short nroComplemento - número
	 *            de complemento AF; * Integer numeroFornecedor - númedo
	 *            fornecedor; * Integer seqNota - seq da nota.
	 * @param indConfirmado
	 * @param indEstorno
	 * @return
	 */
	List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorioConfirmacao(ConfirmacaoRecebimentoFiltroVO filtroVO,
			final Boolean indConfirmado, final Boolean indEstorno);

	/**
	 * Realiza a pesquisa dos itens de uma nota de recebimento provisório para
	 * realizar a confirmação do recebimento.
	 * 
	 * #25016 - Confirmação de recebimento de Materiais e Serviços
	 * 
	 * @param nrpSeq
	 * @param indExclusao
	 * @return
	 */
	List<ItemRecebimentoProvisorioVO> pesquisarItensNotaRecebimentoProvisorio(Integer nrpSeq, final Boolean indExclusao)
			throws ApplicationBusinessException;

	/**
	 * Atualizar objeto nota de recebimento provisorio
	 * 
	 * @param notaRecebimentoProvisorioOriginal
	 * @param notaRecebimentoProvisorio
	 * @param nomeMicrocomputador
	 * @throws BaseException
	 */

	void atualizarNotaRecebimentoProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal,
			SceNotaRecebProvisorio notaRecebimentoProvisorio, String nomeMicrocomputador) throws BaseException;

	/**
	 * Obter maior seq + 1 da tabela SceNotaRecebProvisorio
	 * 
	 * @return Integer
	 */
	Integer obterProximoxSeqSceNotaRecebProvisorio();

	/**
	 * Atualiza instância da classe SceNotaRecebProvisorio
	 * 
	 * @param SceNotaRecebProvisorio
	 */
	void atualizarNotaRecProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws BaseException;

	/**
	 * Atualiza instância da classe SceItemRecebProvisorio
	 * 
	 * @param itemRecebProvisorio
	 */
	void atualizarItemRecebProvisorio(SceItemRecebProvisorio itemRecebProvisorio);

	// /**
	// * Atualiza instância da classe SceItemRecbXProgrEntrega
	// *
	// * @param itemRecbXProgrEntrega
	// */
	// void atualizarItemRecebXProgrEntrega(SceItemRecbXProgrEntrega
	// itemRecbXProgrEntrega);

	/**
	 * Confirmar recebimento de nota de recebimento a partir de uma nota de
	 * recebimento provisorio
	 * 
	 * @param notaRecebimentoProvisorio
	 * @param notaRecebimentoProvisorioOriginal
	 * @param nomeMicrocomputador
	 * @param validaRegras
	 * @return
	 * @throws BaseException
	 */

	public ValidaConfirmacaoRecebimentoVO confirmarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador, Boolean validaRegras, boolean flush,
			boolean validarEsl) throws BaseException;

	public abstract List<ScoSiasgMaterialMestre> obterCatMat(String catmat);

	/**
	 * 
	 * @param itemReceb
	 *            Persiste SceItemRecbXProgrEntrega
	 */
	void persistirSceItemRecbXProgrEntrega(SceItemRecbXProgrEntrega itemReceb);

	/**
	 * Realiza a contagem relativa a pesquisa dos itens de uma nota de
	 * recebimento provisório para realizar a consulta, estorno ou devolução do
	 * recebimento:
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisarNotasRecebimentoProvisorio
	 * 
	 * @param popularFiltro
	 * @return
	 */
	Long contarNotasRecebimentoProvisorio(RecebimentoFiltroVO popularFiltro);

	/**
	 * Realiza a pesquisa dos itens de uma nota de recebimento provisório para
	 * realizar a consulta, estorno ou devolução do recebimento realizando
	 * ligação com Notas Recebimento e adicionando possíveis erros de
	 * consistência.
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisarNotasRecebimentoProvisorio
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param errosNotasRecebimentoProvisorio
	 * @return
	 */
	List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorio(RecebimentoFiltroVO filtroVO, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, List<ApplicationBusinessException> errosNotasRecebimentoProvisorio);

	List<ItemRecebimentoProvisorioRelVO> pesquisarRelatorioItemRecProv(final List<Integer> listaNrpSeq);

	/**
	 * Obtem o valor total dos itens vinculados a um documento fiscal
	 * 
	 * @param dfeSeq
	 * @return
	 */
	Double obterValorTotalItemNotaFiscal(Integer dfeSeq);

	/**
	 * Realiza o estorno de um recebimento que já foi confirmado
	 * 
	 * @param notaRecebimentoProvisorio
	 *            (a coluna IND_CONFIRMADO = 'S', indica que já foi confirmado)
	 * @return
	 */
	void estornarRecebConfirmado(SceNotaRecebProvisorio notaRecebimentoProvisorio, String nomeMicrocomputador) throws BaseException;

	/**
	 * Estornar uma nota de recebimento provisorio
	 * 
	 * @param notaRecebimentoProvisorio
	 * @param notaRecebimentoProvisorioOriginal
	 * @throws BaseException
	 */
	void estornarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio, SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal)
			throws BaseException;

	/**
	 * Pesquisa itens de recebimento provisorio que foram alterados na tela
	 * 
	 * @param lista
	 * @return
	 */
	List<ItemRecebimentoProvisorioVO> pesquisarItensProvisoriosAlterados(List<ItemRecebimentoProvisorioVO> lista);
	
	void validarMaterialRM(ScoMaterial mat, SceReqMaterial reqMaterial, List<SceItemRms> listaItens, Boolean isAlmoxarife) throws ApplicationBusinessException ;

	/**
	 * Gravar itens de recebimento provisorio
	 * 
	 * @param listaItens
	 * @param valida
	 * @return
	 * @throws ApplicationBusinessException
	 */
	ValidaConfirmacaoRecebimentoVO gravarItensRecebimento(List<ItemRecebimentoProvisorioVO> listaItens, Boolean valida)
			throws ApplicationBusinessException;

	/**
	 * Calcula a variacao do item que teve o valor alterado na tela de
	 * confirmacao de recebimento
	 * 
	 * @param item
	 */
	void calcularVariacaoItemRecebimentoProvisorio(ItemRecebimentoProvisorioVO item);

	SceNotaRecebimento obterNotaRecebimentoOriginal(Integer notaRecebimentoSeq);

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento
	 * provisório associadas a um item de AF.
	 * 
	 * @param itemAf
	 *            Item de AF
	 * @param maxRps
	 *            Limite de notas de RP a serem retornadas.
	 * @return Quantidade
	 */
	QtdeRpVO somarQtdeItensNotaRecebProvisorio(ScoItemAutorizacaoForn itemAf, int maxRps);

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento
	 * provisório associadas a uma parcela.
	 * 
	 * @param parcela
	 *            Parcela
	 * @param maxRps
	 *            Limite de notas de RP a serem retornadas.
	 * @return Quantidade
	 */
	QtdeRpVO somarQtdeItensNotaRecebProvisorio(ScoProgEntregaItemAutorizacaoFornecimento parcela, int maxRps);

	/**
	 * Obtém uma nota de recebimento provisório pelo seu id
	 * 
	 * @param seq
	 * @return SceNotaRecebProvisorio
	 */
	SceNotaRecebProvisorio obterNotaRecebProvisorio(Integer seq);

	List<PendenciasDevolucaoVO> pesquisarGeracaoPendenciasDevolucao(Integer numeroNr);

	List<SceConversaoUnidadeConsumos> pesquisarConversaoPorMaterialCodigoUnidadeMaterialDescricao(Integer matCodigo, Object param);

	Boolean verificaExisteBoletimOcorrencia(Integer seqNotaRecebimento);

	void gerarDevolucao(List<PendenciasDevolucaoVO> listaPendencias, SceNotaRecebimento notaRecebimento,
			SceDocumentoFiscalEntrada docFiscalEntrada, String nomeMicrocomputador) throws BaseException;

	/**
	 * Clonar objeto nota recebimento provisorio para posterior update na tabela
	 * 
	 * @param notaRecebimentoProvisorio
	 * @return
	 * @throws ApplicationBusinessException
	 */
	SceNotaRecebProvisorio clonarNotaRecebimentoProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorio)
			throws ApplicationBusinessException;

	List<SceBoletimOcorrencias> pesquisarBoletimOcorrenciaNotaRecebimentoSituacao(Integer seqNotaRecebimento,
			DominioBoletimOcorrencias situacao, Boolean indSituacaoDiferente);

	List<SceBoletimOcorrencias> pesquisarBoletimOcorrenciaNotaRecebimento(Integer seqNotaRecebimento);
	

	/**
	 * Obtem materiais de almoxarifado para geração automática de SC.
	 * 
	 * @param almoxarifado
	 *            Almoxarifado
	 * @param almoxCentral
	 *            Almoxarifado Central
	 * @param fornecedorPadraoId
	 *            Fornecedor Padrão
	 * @return Materiais
	 */
	List<GeraSolicCompraEstoqueVO> obterMateriaisGeracaoSc(SceAlmoxarifado almoxarifado, SceAlmoxarifado almoxCentral,
			Integer fornecedorPadraoId);

	/**
	 * Método para suggestion para buscar notas fiscais de um determinado
	 * fornecedor pelo número da nota fiscal, Razao Social do fornecedor ou pelo
	 * próprio fornecedor
	 * 
	 * @param param
	 * @param fornecedor
	 * @return
	 */
	List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntradaNumeroOuFornecedor(Object param, ScoFornecedor fornecedor);

	Integer getObterNotaFiscal(SceLoteDocumento loteDoc);

	String nameHeaderEfetuarDownloadCSVMensalMateriaisClassificacaoAbc(Date competencia);

	String nameHeaderEfetuarDownloadCSVRelatorioAjusteEstoque(Date competencia);

	String nameHeaderEfetuarDownloadCSVRelatorioTransferenciaMaterial(Integer numTransferenciaMaterial);

	/**
	 * Indica nota de recebimento provisório é de serviço.
	 * 
	 * @return Flag
	 */
	boolean isNotaRecebProvisorioServico(SceNotaRecebProvisorio notaRecebProv);

	SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(final Short seqAlmoxarifadoOrigem,
			final Integer codigoMaterial, final Integer numeroFornecedor);

	void gerarFechamentoEstoqueMensalManual() throws BaseException;

	void gerarInterfaceamentoUnitarizacao(SceLoteDocImpressao loteDocImpressao, String nomeMicrocomputador, Integer qtdeEtiquetas)
			throws BaseException;

	Boolean isItemAFCurvaA(Integer afnNumero, Integer fornecedorPadrao);

	SceAlmoxarifado obterSceAlmoxarifadoPorChavePrimaria(Short seq);

	/**
	 * Busca quantidade e quantidade devolvida
	 * 
	 * C5 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param numero
	 * @return
	 */
	QuantidadesVO obterQuantidadeQuantidadeDevolvidaSceItemEntrSaidSemLicitacao(Integer afnNumero, Integer numero);

	/**
	 * Consulta quantidade de materiais recebidos pelo Hospital, mas ainda não
	 * incorporados ao estoque.
	 * 
	 * C5 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @return
	 */
	Long obterQuantidadeRecParcelasSceItemRecebProvisorio(Integer matCodigo);

	/**
	 * Classificação ABC do material
	 * 
	 * C2 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @param numeroFornecedor
	 * @param dataCompetencia
	 * @return
	 */
	DominioClassifABC obterClassificacaoABCMaterialSceEstoqueGeral(Integer matCodigo, Integer numeroFornecedor, Date dataCompetencia);

	/**
	 * Quantidade disponível do material em estoque
	 * 
	 * C10 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @param controleValidade
	 * @param dataReferencia
	 * @param almoxUnico
	 * @return
	 */
	QuantidadesVO obterQtdDisponivelQtdBloqueadaSceValidade(Integer matCodigo, boolean indControleValidade, Date dataReferencia,
			boolean almoxUnico);

	/**
	 * Busca do código, grupo do material e número de sua solicitação de compra
	 * 
	 * C6 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @param iafNumero
	 * @return
	 */
	GrupoMaterialNumeroSolicitacaoVO obterCodigoGrupoMaterialNumeroSolicitacaoSceItemRecebProvisorio(Integer afnNumero, Integer numero);

	/**
	 * Busca o SceEstoqueAlmoxarifado filtrando pelo material do mesmo almox,
	 * fornecedor e seq
	 * 
	 * C1 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param matCodigo
	 * @param numeroFornecedor
	 * @param seq
	 * @return
	 */
	SceEstoqueAlmoxarifado obterSceEstoqueAlmoxarifadoPorMaterialFornecedor(Integer matCodigo, Integer numeroFornecedor);

	Date obterDataValidadeSceValidade(Integer ealSeq);

	void popularHistoricoConsumo(EstatisticaEstoqueAlmoxarifadoVO estatistica, Integer codMaterial, Short almoxSeq, Date dtCompetencia)
			throws ApplicationBusinessException;

	void popularConsumoMedio(EstatisticaEstoqueAlmoxarifadoVO estatistica);

	void popularConsumoPonderado(EstatisticaEstoqueAlmoxarifadoVO estatistica);

	EstoqueMaterialVO obterEstoqueMaterial(Integer matCodigo, Date dataCompetencia, Integer numeroFornecedor);

	List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoCodigoGrupoMaterial(Short almoxSeq, Object paramPesq,
			List<Integer> listaGrupos, boolean usaFornNaPesquisa, Boolean somenteEstocaveis, Boolean somenteDiretos)
			throws ApplicationBusinessException;

	List<SceEstoqueAlmoxarifado> pesquisarMateriaisEstoquePorCodigoDescricaoAlmoxarifado(String parametro, Short seAlmoxarifado,
			List<Integer> listaGrupos, Boolean somenteEstocaveis, Boolean somenteDiretos) throws ApplicationBusinessException;

	Date buscaDataEncerramento(Integer pac);
	
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMateriasHistorico(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,String modl, Integer matCodigo);

	public Long pesquisarUltimasComprasMateriasHistoricoCount(String modl, Integer matCodigo);

	public abstract void validarInsercaoPacoteRequisicaoMaterial(SceReqMaterial reqMaterial, List<SceEstoqueAlmoxarifado> lstItemToAdd,
			Boolean isAlmoxarife) throws ApplicationBusinessException;

	public abstract void popularConsumosItemPacoteVO(ItemPacoteMateriaisVO itemPacoteMateriaisVO) throws ApplicationBusinessException;

	public abstract List<SceAlmoxarifadoGrupos> pesquisarGruposPorAlmoxarifado(SceAlmoxarifado almox);

	public abstract void cancelarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;// ///

	List<SceAlmoxarifadoComposicao> pesquisarPorAlmoxarifado(SceAlmoxarifado almox);

	List<SceAlmoxarifadoGrupos> pesquisarGruposPorComposicao(SceAlmoxarifadoComposicao comp);

	List<ComposicaoGruposVO> pesquisarComposicaoAlmox(SceAlmoxarifado almoxarifado);

	void persistirComposicaoAlmox(SceAlmoxarifado almoxarifado, List<ComposicaoGruposVO> listaVO) throws ApplicationBusinessException;// ///

	Boolean verificarGrupoOutraComposicao(ScoGrupoMaterial grupo, Integer seqComposicao, SceAlmoxarifado almox,
			List<ComposicaoGruposVO> listaTela);

	void removerComposicaoAlmox(SceAlmoxarifado almoxarifado);

	Long consultarCampoObservacaoParcelaPendente(Integer iafAfnNumero, Integer iafNumero);

	List<SceTipoMovimento> obterSiglaTipoMovimento(Object param);

	List<ScoAutorizacaoForn> pesquisarAF(Object numeroAf, Short numComplementoAf, Integer numFornecedor);

	public List<ScoAutorizacaoForn> pesquisarComplemento(Integer numeroAf, Object numComplementoAf, Integer numFornecedor);

	List<ScoFornecedor> pesquisarFornecedor(Integer numeroAf, Short numComplementoAf, Object numFornecedor, Boolean indAdiantamentoAF);

	List<ItensRecebimentoAdiantamentoVO> pesquisarItensRecebimentoAdiantamento(Integer numeroAF);

	List<SceDocumentoFiscalEntrada> pesquisarNFEntradaGeracaoNumeroOuFornecedor(Object param, ScoFornecedor fornecedor);

	SceNotaRecebProvisorio preReceberItensAdiantamentoAF(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoAdiantamentoVO> listaItensAdiantamento) throws ApplicationBusinessException;

	SceNotaRecebProvisorio preReceberItensMateriais(FiltroRecebeMaterialServicoSemAFVO filtro, List<ItensRecebimentoVO> listaItensMateriais)
			throws ApplicationBusinessException;

	Double obterValorMaterialSemAF(Integer codigoMaterial);

	List<RecebimentosProvisoriosVO> pesquisarRecebimentoProvisorio(ConfirmacaoRecebimentoFiltroVO filtroVO, Boolean indConfirmado,
			Boolean indEstorno);

	List<SceItemEntrSaidSemLicitacao> listaItensRecebimentoProvisorio(Integer seq);

	void atualizarEntrSaidSemLicitacao(SceEntrSaidSemLicitacao sceEntrSaidSemLicitacao);

	SceEntrSaidSemLicitacao obterEntrSaidSemLicitacao(Integer seq);

	ValidaConfirmacaoRecebimentoVO confirmarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador, Boolean validaRegras, boolean flush)
			throws BaseException;

	Double verificarDoacoes(Integer matCodigo, String rmpSeq, Boolean porNotaFiscal);

	Double buscarCustoOrteseOuProteseUltimaEntrada(Integer matCodigo);

	void atualizarSceLoteDocImpressao(SceLoteDocImpressao loteDocImpressao) throws BaseException;

	public abstract void alterarSituacaoGeradaRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador)
			throws BaseException;

	List<EstoqueAlmoxarifadoVO> pesquisarEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codMaterial, Integer numeroFrn, Short seqAlmox, DominioSituacao situacao, Boolean estocavel, Integer seq,
			String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre, VScoClasMaterial classificacaoMaterial)
			throws ApplicationBusinessException;

	Long listaEstoqueAlmoxarifadoCount(Integer seq, Integer codMaterial, Integer numeroFrn, Short seqAlmox, DominioSituacao situacao,
			Boolean estocavel, String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre,
			VScoClasMaterial classificacaoMaterial);
	
	public void salvarMotivoMovimento(SceMotivoMovimento sceMotivoMovimento) throws BaseException;
	
	public void removerMotivoMovimento(SceMotivoMovimento sceMotivoMovimento) throws BaseException;
	
	public List<SceMotivoMovimento> listarMotivoMovimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short seq, Byte complemento);
	
	public Long listarMotivoMovimentoCount(Short seq, Byte complemento);
	
	public List<SceTipoMovimento> listarTipoMovimento(Object param);
	
	public Long listarTipoMovimentoCount(Object param);

	List<ClassificacaoMaterialVO> pesquisarClassificacoes(Integer codGrupo,
			Object parametro);

	Long pesquisarClassificacoesCount(Integer codGrupo, Object parametro);

	List<MaterialClassificacaoVO> listarMateriasPorClassificacao(Long cn5,
			Integer codGrupo);

	/*
	 * Verifica se existe uma SceReqMaterial pelo código do ScoGrupoMaterial
	 * 
	 * C7 de 31584
	 * 
	 * @param gmtCodigo
	 * @return
	 */
	boolean verificarExistenciaSceReqMateriaisPorScoGrupoMaterial(Integer gmtCodigo);
	
	void adicionarMaterialClassificacao(Long cn5Numero, Integer matCodigo,
			ScoMaterial materialSuggestion, String nomeMicrocomputador) throws BaseException;

	void removerMateriaisClassificacao(ScoMateriaisClassificacoesId id) throws BaseException;
	
	public List<ClassificacaoMaterialVO> pesquisarClassificacaoDoMaterial(Integer codMaterial);
	
	public abstract List<SceTransferencia> pesquisarEstornoTransferenciaAutoAlmoxarifado(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceTransferencia transferencia);

	
	/**
	 * Efetua o download de um arquivo CSV referente ao Relatório de Contagem de Estoque para Inventário
	 * @param fileName
	 * @throws IOException 
	 */
	public abstract void efetuarDownloadCSVRelatorioContagemEstoqueInventario(
			String fileName) throws IOException;

	/**
	 * Efetua o download em CSV do Relatório de Ajuste de estoque
	 * @param fileName
	 * @throws IOException
	 */
	public abstract void efetuarDownloadCSVRelatorioAjusteEstoque(
			String fileName, Date dataCompetencia) throws IOException;

	/**
	 * Efetua o download do relatório gerado em CSV
	 * @param fileName
	 * @throws IOException
	 */
	public abstract void efetuarDownloadCSVMensalMateriaisClassificacaoAbc(
			String fileName, Date competencia) throws IOException;

	public abstract void estornarNotaRecebimento(
			SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal, String nomeMicrocomputador, Boolean flush) throws BaseException;

	public abstract SceEstoqueGeral obterSceEstoqueGeralPorMaterialDataCompetencia(ScoMaterial material, Date dataCompetencia);

	/**
	 * 
	 * @param fileName
	 * @param numTransferenciaMaterial
	 * @throws IOException
	 */
	public abstract void efetuarDownloadCSVRelatorioTransferenciaMaterial(
			final String fileName, final Integer numTransferenciaMaterial)
			throws IOException;
	
// TODO migrar esse metodos, no momento não foi possível devido ao fato de não poder importar as classes do Quartz
//	@Asynchronous
//	@Begin(flushMode = FlushModeType.MANUAL)
//	public abstract QuartzTriggerHandle gerarFechamentoEstoqueMensal(
//			@Expiration Date date, @IntervalCron String cron)
//			throws BaseException;
//
//	@Asynchronous
//	@Begin(flushMode = FlushModeType.MANUAL)
//	public abstract QuartzTriggerHandle efetivarRequisicaoMaterialAutomatica(
//			@Expiration Date date, @IntervalCron String cron, String nomeMicrocomputador)
//			throws BaseException;
//
//	public abstract void efetuarDownloadCsvRelatorioDevolucaoAlmoxarifado(
//			String fileName, Integer numeroDA) throws IOException;
	
//	/**
//	 * Atualiza instância da classe SceItemRecbXProgrEntrega
//	 * 
//	 * @param itemRecbXProgrEntrega
//	 */
//	public void atualizarItemRecebXProgrEntrega(SceItemRecbXProgrEntrega itemRecbXProgrEntrega);
	
	public List<FcpAgenciaBanco> pesquisarAgenciaBanco(Object param);
	
	public Long pesquisarAgenciaBancoCount(Object param);
	
	public List<SociosFornecedoresVO> listarSociosFornecedores(Integer firstResults, Integer maxResults, String orderProperty, 
			boolean asc, Integer filtroCodigo,
			String filtroNomeSocio, String filtroRG, Long filtroCPF, Integer filtroFornecedor) ;
	
	public Long listarSociosFornecedoresCount(Integer filtroCodigo, String filtroNomeSocio, String filtroRG, 
			Long filtroCPF, Integer filtroFornecedor) ;
	
	void gravarSocioFornecedores(ScoSocios socio, List<ScoFornecedor> listaFornecedores) throws BaseException;
	
	public void removerSocios(ScoSocios scoSocios);
	
	public void removerScoSociosFornecedores(ScoSociosFornecedores scoSociosFornecedores);
	
	public Long pesquisarListaCFOPCount(SceCfop cfop) throws BaseException;
	
	public List<SceCfop> pesquisarListaCFOP(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, SceCfop cfop) throws BaseException;
	
	public SceCfop pesquisarCFOP(Short codigo) throws BaseException;
	
	public void excluirCFOP(SceCfop cfop) throws BaseException;
	
	public void atualizarSCFOP(SceCfop cfop) throws BaseException;
	
	public void inserirCFOP(SceCfop cfop) throws ApplicationBusinessException;
	
	/**
	 * Pesquisa a lista paginada de materiais dos fornecedores
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return List<SceRelacionamentoMaterialFornecedorVO> Lista paginada de materiais dos fornecedores
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarListagemPaginadaMaterialFornecedor(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) throws ApplicationBusinessException;
	
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarMaterialFornecedor(Integer id) throws ApplicationBusinessException;
	
	/**
	 * Pesquisa informações do material em edição
	 * 
	 * @param codigoMaterialFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public SceRelacionamentoMaterialFornecedor carregarMaterialFornecedor(
			Long codigoMaterialFornecedor) throws ApplicationBusinessException;
	
	/**
	 * Pesquisa o histórico de materiais do fornecedor
	 * 
	 * @param codigoMaterialFornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedorJn> pesquisarHistoricoMaterialFornecedor(
			Long codigoMaterialFornecedor) throws ApplicationBusinessException;
	
	/**
	 * Count da lista paginada de materiais dos fornecedores
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarListagemPaginadaMaterialFornecedorCount(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) throws ApplicationBusinessException;

	/**
	 * Método responsável por persistir sceRelacionamentoMaterialFornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 * @throws ApplicationBusinessException
	 */
	public void persistirMaterialFornecedor(
			SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException;
	

	/**
	 * Consulta para obter o(s) Material(is) do Fornecedor associados a um Material x Fornecedor do Hospital.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public List<SceRelacionamentoMaterialFornecedor> pesquisarListaMateriaHospitalFornecedor(
			SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException;
	
	/**
	 * Pesquisar se já existe um Material do Hospital relacionado ao Material do Fornecedor.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException;

	/** 
	 * Ativação do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public void alterarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor);
	
	/**
	 * Inclusão do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public void incluirMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor);
	
	/**
	 * Pesquisa material por fornecedor
	 * @param numeroFornecedor
	 * @param parametro
	 * @return
	 */
	public List<SceSuggestionBoxMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Integer numeroFornecedor, Object parametro) throws BaseException;

	/**
	 * Pesquisa material por código ou nome
	 * @param objPesquisa
	 * @return
	 * @throws BaseException
	 */
	public abstract List<ScoMaterial> pesquisarMaterial(Object objPesquisa) throws BaseException;

	/**
	 * Pesquisa a quantidade total de registros de material por código ou nome
	 * @param objPesquisa
	 * @return
	 * @throws BaseException
	 */
	public abstract Long pesquisarMaterialCount(Object objPesquisa) throws BaseException;

	public SceTipoMovimento obterSceTipoMovimentosAtivoPorSeq(BigDecimal vTmvSeq)
			throws ApplicationBusinessException;

	public SceReqMaterial obterSceReqMaterialPorChavePrimaria(Integer vRmsSeq);

	public void atualizarSceReqMaterial(SceReqMaterial sceReqMaterial);

	public void inserirSceReqMaterial(SceReqMaterial sceReqMaterial);

	public void inserirSceItemRms(SceItemRms sceItemRms);
	
	Boolean verificarExistenciaAFRecebimento(Integer numeroRecebimento);

	SceItemRecebProvisorio obterItemRecebProvisorioPorChavePrimaria(SceItemRecebProvisorioId id);
	
	public EntradaMateriasDiaVO pesquisarEntradaMateriasDia(Date dataGeracao) throws ApplicationBusinessException;
	
	public List<Double> buscarCustoMedicamento(Integer matCodigo, Date competencia, Short tmvSeq);

	public List<Double> buscarCustoMedicamentoPelaUltimaEntradaEstoque(Integer matCodigo, Date competencia, Short tmvSeq);

    public List<BigDecimal> obterCustoMedioPonderadoDoMaterialEstoqueGeral(Integer matCodigo, Date dtCompetencia, Integer frnNumero);

	ScoFornecedor obterFornecedorPorId(Integer codigo);
	
	/**
	 * #6635 F1
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterTotalGeralComprasDia(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F2
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterTotalGeralDevolucoesDia(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F3
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterTotalDiferencaFormula(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F4
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterQuantidadeMateriaisConsumidosDia(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F5
	 * @param dataGeracao
	 * @return
	 */
	public Double obterQuantidadeServicosDia(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F6
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterQuantidadePatrimonioEntradaDia(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F7
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterQuantidadeMateriaisConsumoEntradaDia(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F8
	 * @param dataGeracao
	 * @return
	 */
	public Double obterQuantidadeServicosMes(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F9
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal obterQuantidadeServicosEntradaMes(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F10
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal consumoMatDiaFormula(Date dataGeracao) throws ApplicationBusinessException;
	
	/**
	 * #6635 F11
	 * @param dataGeracao
	 * @return
	 */
	public BigDecimal consumoMatMesFormula(Date dataGeracao) throws ApplicationBusinessException;

	//34163
    public String gerarRelatoriosExcelESSL(EntradaSaidaSemLicitacaoVO filtroSL) throws IOException, ApplicationBusinessException;
    
    public List<EntradaSaidaSemLicitacaoVO> listarEntradaSaidaSemLicitacaoSL(EntradaSaidaSemLicitacaoVO filtroSL) throws ApplicationBusinessException;
    
    public EntradaSaidaSemLicitacaoVO listarCnpjRazaoSocial(EntradaSaidaSemLicitacaoVO objetoRetorno);
    
    public List<SceEntrSaidSemLicitacaoVO> obterNumeroESLPorSiglaTipoMovimento(String filter, String siglaTipoMovimento);
    
    public ScoAutorizacaoForn obterPorChavePrimaria(Integer pk);
	
	public Long obterNumeroESLPorSiglaTipoMovimentoCount(String filter, String siglaTipoMovimento);

    //34348
    public List<TipoMovimentoVO> obterTipoMovimentoListBox();
	
	public void validaRegras(EntradaSaidaSemLicitacaoVO vo) throws ApplicationBusinessException;
	
	public void inserir(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) throws BaseException;
	
	public void atualizar(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) throws BaseException;
	
	public List<SceTipoMovimento> obterTipoMovimentoPorSigla(String pesquisa);
	
	
	public void atualizarSceDocumentoFiscalEntrada(SceDocumentoFiscalEntrada elemento);

	public void atualizarDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException ;

	ScoFornecedor obterFornecedorPorCNPJ(Long cnpj);

	RapServidores obterServidorAtivoPorUsuario(final String loginUsuario) throws ApplicationBusinessException;
	Long obterNotaFiscalItemRecebimento(Integer numRecebimento);

	boolean isHabilitarExcluirHCPA();
}
package br.gov.mec.aghu.estoque.business;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroRecebeMaterialServicoSemAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoAdiantamentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentosProvisoriosVO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.vo.GeraSolicCompraEstoqueVO;
import br.gov.mec.aghu.compras.vo.ItensAutFornUpdateSCContrVO;
import br.gov.mec.aghu.compras.vo.SociosFornecedoresVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
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
import br.gov.mec.aghu.estoque.cadastrosbasicos.materiais.business.SceRelacionamentoMaterialFornecedorON;
import br.gov.mec.aghu.estoque.dao.FcpAgenciaBancoDAO;
import br.gov.mec.aghu.estoque.dao.FcpMoedaDAO;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoComposicaoDAO;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoGruposDAO;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoTransferenciaAutomaticaDAO;
import br.gov.mec.aghu.estoque.dao.SceBoletimOcorrenciasDAO;
import br.gov.mec.aghu.estoque.dao.SceConsumoTotalMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceConversaoUnidadeConsumosDAO;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.estoque.dao.SceEntrSaidSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceEntradaSaidaSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceFornecedorEventualDAO;
import br.gov.mec.aghu.estoque.dao.SceFornecedorMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoFechamentoMensalDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemEntrSaidSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemPacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmpsDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocumentoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceMotivoMovimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceMotivoProblemaDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.ScePacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceRefCodesDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMaterialRetornosDAO;
import br.gov.mec.aghu.estoque.dao.SceRmrPacienteDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.estoque.dao.ScoSiasgMaterialMestreDAO;
import br.gov.mec.aghu.estoque.dao.VSceItemNotaRecebimentoAutorizacaoFornecimentoDAO;
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
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;

/**
 * @author aghu
 * 
 */
@Modulo(ModuloEnum.ESTOQUE)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class EstoqueFacade extends BaseFacade implements IEstoqueFacade {

	@Inject
	private SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO;

	@Inject
	private SceBoletimOcorrenciasDAO sceBoletimOcorrenciasDAO;

	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;

	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@EJB
	private SceValidadesRN sceValidadesRN;
	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;
	@Inject
	private SceAlmoxarifadoComposicaoDAO sceAlmoxarifadoComposicaoDAO;
	@Inject
	private FcpAgenciaBancoDAO fcpAgenciaBancoDAO;
	@EJB
	private AtualizarPontoPedidoRN atualizarPontoPedidoRN;
	@EJB
	private ManterRequisicaoMaterialON manterRequisicaoMaterialON;
	@EJB
	private ConfirmacaoRecebimentoON confirmacaoRecebimentoON;
	@EJB
	private ConsultarHistoricoMaterialON consultarHistoricoMaterialON;
	@EJB
	private DispensacaoMedicamentoON dispensacaoMedicamentoON;
	@EJB
	private LoteFornecedorON loteFornecedorON;
	@EJB
	private RelatorioConsumoSinteticoMateriaisON relatorioConsumoSinteticoMateriaisON;
	@EJB
	private ScoMaterialRN scoMaterialRN;
	@EJB
	private SceLoteDocImpressaoRN sceLoteDocImpressaoRN;
	@EJB
	private DevolucaoAlmoxarifadoRN devolucaoAlmoxarifadoRN;
	@EJB
	private GerarListaTransferenciaAutoAlmoxarifadoON gerarListaTransferenciaAutoAlmoxarifadoON;
	@EJB
	private PesquisarEstoqueGeralON pesquisarEstoqueGeralON;
	@EJB
	private GerarRelatorioMensalMaterialON gerarRelatorioMensalMaterialON;
	@EJB
	private GerarRequisicaoMaterialON gerarRequisicaoMaterialON;
	@EJB
	private GerarItemNotaRecebimentoON gerarItemNotaRecebimentoON;
	@EJB
	private SceAlmoxarifadosRN sceAlmoxarifadosRN;
	@EJB
	private SceTransferenciaRN sceTransferenciaRN;
	@EJB
	private SceNotaRecebimentoRN sceNotaRecebimentoRN;
	@EJB
	private SceNotaItemRecebProvisorioON sceNotaItemRecebProvisorioON;
	@EJB
	private GerarRelatorioMateriaisValidadeVencidaON gerarRelatorioMateriaisValidadeVencidaON;
	@EJB
	private GerarRelatorioTransferenciaMaterialON gerarRelatorioTransferenciaMaterialON;
	@EJB
	private MovimentoMaterialON movimentoMaterialON;
	@EJB
	private PesquisaMovimentoMaterialON pesquisaMovimentoMaterialON;
	@EJB
	private EstornarNotaRecebimentoON estornarNotaRecebimentoON;
	@EJB
	private SceItemNotaRecebimentoRN sceItemNotaRecebimentoRN;
	@EJB
	private ManterMaterialON manterMaterialON;
	@EJB
	private ManterDocumentoFiscalEntradaON manterDocumentoFiscalEntradaON;
	@EJB
	private ImprimirEtiquetasUnitarizacaoON imprimirEtiquetasUnitarizacaoON;
	@EJB
	private SceLoteDocumentoON sceLoteDocumentoON;
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	@EJB
	private EstornoMedicamentoDispensadoON estornoMedicamentoDispensadoON;
	@EJB
	private ManterPacoteMateriaisON manterPacoteMateriaisON;
	@EJB
	private RelatorioDevolucaoAlmoxarifadoRN relatorioDevolucaoAlmoxarifadoRN;
	@EJB
	private SceNotaRecebimentoProvisorioRN sceNotaRecebimentoProvisorioRN;
	@EJB
	private GeracaoDevolucaoON geracaoDevolucaoON;
	@EJB
	private GeracaoRelatoriosExcelON geracaoRelatoriosExcelON;
	@EJB
	private GerarFechamentoEstoqueMensalRN gerarFechamentoEstoqueMensalRN;
	@EJB
	private EfetivarRequisicaoMaterialON efetivarRequisicaoMaterialON;
	@EJB
	private ItemDevolucaoAlmoxarifadoRN itemDevolucaoAlmoxarifadoRN;
	@EJB
	private ConfirmacaoDevolucaoON confirmacaoDevolucaoON;
	@EJB
	private SceEstoqueGeralRN sceEstoqueGeralRN;
	@EJB
	private ManterEstoqueAlmoxarifadoON manterEstoqueAlmoxarifadoON;
	@EJB
	private DevolucaoAlmoxarifadoON devolucaoAlmoxarifadoON;
	@EJB
	private GerarRelatorioMateriaisClassificacaoABCON gerarRelatorioMateriaisClassificacaoABCON;
	@EJB
	private GerarRelatorioContagemEstoqueInventarioON gerarRelatorioContagemEstoqueInventarioON;
	@EJB
	private ManterTransferenciaON manterTransferenciaON;
	@EJB
	private SceItemRecbXProgrEntregaRN sceItemRecbXProgrEntregaRN;
	@EJB
	private PesquisarEstoqueAlmoxarifadoON pesquisarEstoqueAlmoxarifadoON;
	@EJB
	private SceItemTransferenciaRN sceItemTransferenciaRN;
	@EJB
	private ImprimirEtiquetasExtrasON imprimirEtiquetasExtrasON;
	@EJB
	private ManterSociosFornecedoresRN manterSociosFornecedoresRN;
	@EJB
	private HistoricoProblemaMaterialON historicoProblemaMaterialON;
	@EJB
	private SceLoteDocumentoRN sceLoteDocumentoRN;
	@EJB
	private GerarRelatorioMateriaisComSaldoAteVinteDiasON gerarRelatorioMateriaisComSaldoAteVinteDiasON;
	@EJB
	private RelatorioMateriaisEstocaveisGrupoCurvaAbcON relatorioMateriaisEstocaveisGrupoCurvaAbcON;
	@EJB
	private SceItemTransferenciaON sceItemTransferenciaON;
	@EJB
	private ManterAlmoxarifadoON manterAlmoxarifadoON;
	@EJB
	private GerarNotaRecebimentoON gerarNotaRecebimentoON;
	@EJB
	private EstornarRequisicaoMaterialON estornarRequisicaoMaterialON;
	@EJB
	private RecebimentoON recebimentoON;
	@EJB
	private SceConversaoUnidadeConsumosRN sceConversaoUnidadeConsumosRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	@EJB
	private SceItemRecbXProgrEntregaON sceItemRecbXProgrEntregaON;
	@EJB
	private SceItemRmsRN sceItemRmsRN;
	@EJB
	private ValidadeMaterialON validadeMaterialON;
	@EJB
	private SceReqMateriaisRN sceReqMateriaisRN;
	@EJB
	private GerarRelatorioAjusteEstoqueON gerarRelatorioAjusteEstoqueON;
	@EJB
	private RecebimentoSemAFON recebimentoSemAFON;
	@EJB
	private ComposicaoGruposAlmoxON composicaoGruposAlmoxON;
	@EJB
	private SceDocumentoFiscalEntradaRN sceDocumentoFiscalEntradaRN;
	@Inject
	private SceEntrSaidSemLicitacaoDAO sceEntrSaidSemLicitacaoDAO;
	@Inject
	private ScoSiasgMaterialMestreDAO scoSiasgMaterialMestreDAO;
	@EJB
	private ManterMotivoMovimentoRN manterMotivoMovimentoRN; 
	@EJB
	private ClassificarMaterialON classificarMaterialON;
	@EJB
	private EfetivarRequisicaoMaterialJobON efetivarRequisicaoMaterialJobON;
	@EJB
	private SceRelacionamentoMaterialFornecedorON sceRelacionamentoMaterialFornecedorON;
	
	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;

	@Inject
	private FcpMoedaDAO fcpMoedaDAO;

	@Inject
	private SceItemPacoteMateriaisDAO sceItemPacoteMateriaisDAO;

	@Inject
	private SceConsumoTotalMateriaisDAO sceConsumoTotalMateriaisDAO;

	@Inject
	private SceRefCodesDAO sceRefCodesDAO;

	@Inject
	private SceValidadeDAO sceValidadeDAO;

	@Inject
	private SceItemRmsDAO sceItemRmsDAO;

	@Inject
	private SceLoteFornecedorDAO sceLoteFornecedorDAO;

	@Inject
	private SceLoteDocImpressaoDAO sceLoteDocImpressaoDAO;

	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	@Inject
	private SceItemRmpsDAO sceItemRmpsDAO;

	@Inject
	private SceLoteDocumentoDAO sceLoteDocumentoDAO;

	@Inject
	private SceHistoricoFechamentoMensalDAO sceHistoricoFechamentoMensalDAO;

	@Inject
	private SceAlmoxarifadoTransferenciaAutomaticaDAO sceAlmoxarifadoTransferenciaAutomaticaDAO;

	@Inject
	private SceConversaoUnidadeConsumosDAO sceConversaoUnidadeConsumosDAO;

	@Inject
	private SceReqMaterialRetornosDAO sceReqMaterialRetornosDAO;

	@Inject
	private ScePacoteMateriaisDAO scePacoteMateriaisDAO;

	@Inject
	private SceRmrPacienteDAO sceRmrPacienteDAO;

	@Inject
	private SceLoteDAO sceLoteDAO;

	@Inject
	private SceEntradaSaidaSemLicitacaoDAO sceEntradaSaidaSemLicitacaoDAO;

	@Inject
	private SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO;

	@Inject
	private SceMotivoProblemaDAO sceMotivoProblemaDAO;

	@Inject
	private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;

	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	@Inject
	private SceFornecedorEventualDAO sceFornecedorEventualDAO;

	@Inject
	private SceDevolucaoAlmoxarifadoDAO sceDevolucaoAlmoxarifadoDAO;

	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;

	@Inject
	private SceItemTransferenciaDAO sceItemTransferenciaDAO;

	@Inject
	private SceTransferenciaDAO sceTransferenciaDAO;

	@Inject
	private VSceItemNotaRecebimentoAutorizacaoFornecimentoDAO vSceItemNotaRecebimentoAutorizacaoFornecimentoDAO;

	@Inject
	private SceFornecedorMaterialDAO sceFornecedorMaterialDAO;

	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;

	@Inject
	private SceMotivoMovimentoDAO sceMotivoMovimentoDAO;

	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;

	@Inject
	private SceAlmoxarifadoGruposDAO sceAlmoxarifadoGruposDAO;

	@Inject
	private SceItemEntrSaidSemLicitacaoDAO sceItemEntrSaidSemLicitacaoDAO;
	
	@EJB
	private SceCfopON sceCfopOn;
	
	private static final long serialVersionUID = 7199237948807996933L;

	@Override
	public void persistirScoMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		this.getManterMaterialON().persistirMaterial(material, nomeMicrocomputador);
	}

	protected ManterRequisicaoMaterialON getManterRequisicaoMaterialON() {
		return manterRequisicaoMaterialON;
	}

	@Override
	public void inserirSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) throws BaseException {
		getSceEstoqueAlmoxarifadoRN().inserir(estoqueAlmoxarifado);
	}

	@Override
	public void estornarMedicamentoRequisicaoMaterial(AfaDispensacaoMdtos dispensacaoMdto, AfaDispensacaoMdtos dispensacaoMdtoOld,
			String etiqueta, String nomeMicrocomputador) throws BaseException {
		getEstornoMedicamentoDispensadoON().estornarMedicamentoRequisicaoMaterial(dispensacaoMdto, dispensacaoMdtoOld, etiqueta,
				nomeMicrocomputador);
	}

	protected EstornoMedicamentoDispensadoON getEstornoMedicamentoDispensadoON() {
		return estornoMedicamentoDispensadoON;
	}

	@Override
	public List<SceReqMaterial> pesquisaRequisicoesMateriaisEstornar(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceReqMaterial sceReqMateriais) {
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisEstornar(firstResult, maxResult, orderProperty, asc, sceReqMateriais);
	}

	@Override
	public Long pesquisaRequisicoesMateriaisEstornarCount(SceReqMaterial sceReqMateriais) {
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisEstornarCount(sceReqMateriais);
	}

	@Override
	public List<SceAlmoxarifado> obterAlmoxarifadoPorSeqDescricao(String param) {
		return getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeqDescricao(param);
	}

	@Override
	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(Short seqAlmoxarifadoOrigem,
			Integer codigoMaterial, Integer fornecedor) {
		return getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(seqAlmoxarifadoOrigem,
				codigoMaterial, fornecedor);
	}

	@Override
	public SceTipoMovimento obterSceTipoMovimentosAtivoPorSeq(Short seq) {
		return getSceTipoMovimentosDAO().obterSceTipoMovimentosAtivoPorSeq(seq);
	}

	@Override
	public List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoAjustes(String param) throws ApplicationBusinessException {
		return getSceMovimentoMaterialRN().obterTipoMovimentoPorSeqDescricaoAjustes(param);
	}

	@Override
	public List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoBloqueioDesbloqueioComProblema(String param, boolean mostrarTodos)
			throws ApplicationBusinessException {
		return getSceMovimentoMaterialRN().obterTipoMovimentoPorSeqDescricaoBloqueioDesbloqueioComProblema(param, mostrarTodos);
	}

	@Override
	public List<SceMotivoMovimento> obterMotivoMovimentoPorSeqDescricaoETMV(Short tmvSeq, Byte tmvComplemento, Object param) {
		return getSceMotivoMovimentoDAO().obterMotivoMovimentoPorSeqDescricaoETMV(tmvSeq, tmvComplemento, param);
	}

	@Override
	public List<GerarNotaRecebimentoVO> pesquisarAutorizacoesFornecimentoPorSeqDescricao(Object param) {
		return this.getGerarNotaRecebimentoON().pesquisarAutorizacoesFornecimentoPorSeqDescricao(param);
	}

	@Override
	public List<VSceItemNotaRecebimentoAutorizacaoFornecimento> pesquisarMaterialPorAutorizacaoFornecimentoNaoEfetivadaOuParcialmenteAtendida(
			Integer afnNumero) {
		return getVSceItemNotaRecebimentoAutorizacaoFornecimentoDAO()
				.pesquisarMaterialPorAutorizacaoFornecimentoNaoEfetivadaOuParcialmenteAtendida(afnNumero);
	}

	@Override
	public SceItemRms obterItemRmsOriginal(SceItemRms itemRmsModificado) {
		return getSceItemRmsDAO().obterOriginal(itemRmsModificado);
	}

	protected VSceItemNotaRecebimentoAutorizacaoFornecimentoDAO getVSceItemNotaRecebimentoAutorizacaoFornecimentoDAO() {
		return vSceItemNotaRecebimentoAutorizacaoFornecimentoDAO;
	}

	protected SceItemRecbXProgrEntregaON getSceItemRecbXProgrEntregaON() {
		return sceItemRecbXProgrEntregaON;
	}

	@Override
	public void persistirSceItemRecbXProgrEntrega(SceItemRecbXProgrEntrega itemReceb) {
		getSceItemRecbXProgrEntregaRN().persistir(itemReceb);
	}

	@Override
	public SceAlmoxarifadoTransferenciaAutomatica obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(Short almSeq,
			Short almSeqRecebe) {
		return getSceAlmoxarifadoTransferenciaAutomaticaDAO().obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(almSeq,
				almSeqRecebe);
	}

	protected SceAlmoxarifadoTransferenciaAutomaticaDAO getSceAlmoxarifadoTransferenciaAutomaticaDAO() {
		return sceAlmoxarifadoTransferenciaAutomaticaDAO;
	}

	@Override
	public SceTransferencia obterTransferenciaPorSeq(Integer seq) {
		return this.getSceTransferenciaDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void validarMaterialRM(ScoMaterial mat, SceReqMaterial reqMaterial, List<SceItemRms> listaItens, Boolean isAlmoxarife) throws ApplicationBusinessException {
		this.getGerarRequisicaoMaterialON().validarMaterialRM(mat, reqMaterial, listaItens, isAlmoxarife);
	}

	@Override
	public Long pesquisarTransferenciaAutoAlmoxarifadoCount(SceTransferencia transferencia) {
		return getSceTransferenciaDAO().pesquisarTransferenciaAutoAlmoxarifadoCount(transferencia);
	}

	@Override
	public Long pesquisarEstornoTransferenciaAutoAlmoxarifadoCount(SceTransferencia transferencia) {
		return getSceTransferenciaDAO().pesquisarEstornoTransferenciaAutoAlmoxarifadoCount(transferencia);
	}

	@Override
	public List<SceTransferencia> pesquisarTransferenciaAutoAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceTransferencia transferencia) {
		return getSceTransferenciaDAO().pesquisarTransferenciaAutoAlmoxarifado(firstResult, maxResult, orderProperty, asc, transferencia);
	}

	@Override
	public List<SceTransferencia> pesquisarEstornoTransferenciaAutoAlmoxarifado(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, SceTransferencia transferencia) {
		return getSceTransferenciaDAO().pesquisarEstornoTransferenciaAutoAlmoxarifado(firstResult, maxResult, orderProperty, asc,
				transferencia);
	}

	@Override
	public SceItemTransferencia obterItemTransferenciaPorChave(Integer ealSeq, Integer trfSeq) {
		return getSceItemTransferenciaDAO().obterItemTransferenciaPorChave(ealSeq, trfSeq);
	}

	@Override
	public String obterNomeMaterialItemTransferencia(Integer ealSeq, Integer trfSeq) {
		return getSceItemTransferenciaDAO().obterNomeMaterialItemTransferencia(ealSeq, trfSeq);
	}

	protected SceItemTransferenciaDAO getSceItemTransferenciaDAO() {
		return sceItemTransferenciaDAO;
	}

	protected SceItemTransferenciaRN getSceItemTransferenciaRN() {
		return sceItemTransferenciaRN;
	}

	protected SceAlmoxarifadosRN getSceAlmoxarifadoRN() {
		return sceAlmoxarifadosRN;
	}

	protected SceTransferenciaRN getSceTransferenciaRN() {
		return sceTransferenciaRN;
	}

	protected DevolucaoAlmoxarifadoON getDevolucaoAlmoxarifadoON() {
		return devolucaoAlmoxarifadoON;
	}

	@Override
	public Boolean mostrarBotaoRecebimentoAntigo() {
		return getRecebimentoON().mostrarBotaoRecebimentoAntigo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisaRequisicoesMateriaisCount
	 * (br.gov.mec.aghu.estoque.vo.PesquisaRequisicaoMaterialVO)
	 */
	@Override
	public Long pesquisaRequisicoesMateriaisCount(PesquisaRequisicaoMaterialVO filtro) {
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisCount(filtro);
	}

	@Override
	public List<SceReqMaterial> pesquisaRequisicoesMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			PesquisaRequisicaoMaterialVO filtro) {
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriais(firstResult, maxResult, orderProperty, asc, filtro);
	}

	@Override
	public Long pesquisaRequisicoesMateriaisEfetivarCount(PesquisaRequisicaoMaterialVO filtro) {
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisEfetivarCount(filtro);
	}

	@Override
	public List<SceReqMaterial> pesquisaRequisicoesMateriaisEfetivar(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, PesquisaRequisicaoMaterialVO filtro) {
		return getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisefetivar(firstResult, maxResult, orderProperty, asc, filtro);
	}

	@Override
	public Boolean verificarAutorizacaoFornecimentoSaldoNotaRecebimento(Integer afnNumero, Short sequenciaAlteracao) {
		return this.getSceNotaRecebimentoDAO().verificarAutorizacaoFornecimentoSaldoNotaRecebimento(afnNumero, sequenciaAlteracao);
	}

	@Override
	public List<ScePacoteMateriais> pesquisarPacoteMateriaisParaTrasnferenciaEventual(String _input) {
		return getScePacoteMateriaisDAO().pesquisarPacoteMateriaisParaTrasnferenciaEventual(_input);
	}

	@Override
	public List<ScePacoteMateriais> pesquisarPacoteMateriaisGerarRequisicaoMaterial(Short seqAlmoxarifado,
			Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer codigoGrupoMaterial, String objPesquisa) {
		return getScePacoteMateriaisDAO().pesquisarPacoteMateriaisGerarRequisicaoMaterial(seqAlmoxarifado, codigoCentroCustoProprietario,
				codigoCentroCustoAplicacao, codigoGrupoMaterial, objPesquisa);
	}

	@Override
	public SceReqMaterial obterRequisicaoMaterial(Integer seqReq) {

		return getSceReqMateriaisDAO().obterMaterialPorId(seqReq);
	}

	@Override
	public SceReqMaterial obterOriginal(Integer seqReq) {
		return getSceReqMateriaisDAO().obterOriginal(seqReq);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoCodigoGrupoMaterial(Short almoxSeq, Object paramPesq,
			List<Integer> listaGrupos, boolean usaFornNaPesquisa, Boolean somenteEstocaveis, Boolean somenteDiretos)
			throws ApplicationBusinessException {
		return getSceReqMateriaisRN().pesquisarEstoqueMaterialPorAlmoxarifadoCodigoGrupoMaterial(almoxSeq, paramPesq, listaGrupos,
				usaFornNaPesquisa, somenteEstocaveis, somenteDiretos);
	}

	@Override
	public Long pesquisarEstoqueMaterialPorAlmoxarifadoCount(Short almoxSeq, Integer numeroFornecedor, Integer codMaterial) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueMaterialPorAlmoxarifadoCount(almoxSeq, numeroFornecedor, codMaterial);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifado(Short almoxSeq, Integer numeroFornecedor, Object paramPesq) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueMaterialPorAlmoxarifado(almoxSeq, numeroFornecedor, paramPesq);
	}

	@Override
	public EstatisticaEstoqueAlmoxarifadoVO obterEstatisticasAlmoxarifadoPorMaterialAlmoxDataComp(Short almoxSeq, Short almSeqConsumo,
			Integer codMaterial, Date dtCompetencia) throws BaseException {
		return getSceEstoqueAlmoxarifadoRN().obterEstatisticasAlmoxarifadoPorMaterialAlmoxDataComp(almoxSeq, almSeqConsumo, codMaterial,
				dtCompetencia);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoOrderByFornEAlmx(Integer firstResult, Integer maxResult,
			Short almoxSeq, Integer numeroFornecedor, Integer codMaterial) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueMaterialPorAlmoxarifadoOrderByFornEAlmx(firstResult, maxResult, almoxSeq,
				numeroFornecedor, codMaterial);
	}

	@Override
	public Long pesquisarHistoricosProblemaPorFiltroCount(Integer codigoMaterial, Short almoxSeq, Integer codFornecedor,
			Short motivoProblema, Integer fornecedorEntrega) {
		return getSceHistoricoProblemaMaterialDAO().pesquisarHistoricosProblemaPorFiltroCount(codigoMaterial, almoxSeq, codFornecedor,
				motivoProblema, fornecedorEntrega);
	}

	@Override
	public List<SceHistoricoProblemaMaterial> pesquisarHistoricosProblemaPorFiltro(Integer firstResult, Integer maxResult,
			Integer codigoMaterial, Short almoxSeq, Integer codFornecedor, Short motivoProblema, Integer fornecedorEntrega) {
		return getSceHistoricoProblemaMaterialDAO().pesquisarHistoricosProblemaPorFiltro(firstResult, maxResult, codigoMaterial, almoxSeq,
				codFornecedor, motivoProblema, fornecedorEntrega);
	}

	@Override
	public SceHistoricoProblemaMaterial obterHistoricosProblemaPorSeq(Integer seqHistorico) {
		return getSceHistoricoProblemaMaterialDAO().obterSceHistoricoProblemaMaterial(seqHistorico);
	}

	@Override
	public List<SceMotivoProblema> pesquisaMotivosProblemasPorSeqDescricao(String paramPesq, DominioSituacao situacao) {
		return getSceMotivoProblemaDAO().pesquisaMotivosProblemasPorSeqDescricao(paramPesq, situacao);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorPacote(Integer codigoGrupo, ScePacoteMateriaisId pacote) {
		return getSceReqMateriaisRN().pesquisarEstoqueAlmoxarifadoPorPacote(codigoGrupo, pacote);
	}

	@Override
	public void preencheConsumoMedioItemRequisicao(SceItemRms itemRms, Integer codigoCCAplicacao) throws ApplicationBusinessException {
		getSceItemRmsRN().preencheConsumoMedioItemRequisicao(itemRms, codigoCCAplicacao);
	}

	@Override
	public List<SceFornecedorEventual> obterFornecedorEventual(Object param) {
		return getSceFornecedorEventualDAO().obterFornecedorEventualPorSeqDescricao(param);
	}

	@Override
	public List<EstoqueAlmoxarifadoVO> pesquisarEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codMaterial, Integer numeroFrn, Short seqAlmox, DominioSituacao situacao, Boolean estocavel, Integer seq,
			String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre, VScoClasMaterial classificacaoMaterial)
			throws ApplicationBusinessException {
		return getPesquisarEstoqueAlmoxarifadoON().pesquisarEstoqueAlmoxarifado(firstResult, maxResult, orderProperty, asc, codMaterial,
				numeroFrn, seqAlmox, situacao, estocavel, seq, codigoUnidadeMedida, codigoGrupoMaterial, termoLivre, classificacaoMaterial);
	}

	@Override
	public Long listaEstoqueAlmoxarifadoCount(Integer seq, Integer codMaterial, Integer numeroFrn, Short seqAlmox,
			DominioSituacao situacao, Boolean estocavel, String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre,
			VScoClasMaterial classificacaoMaterial) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoCount(seq, codMaterial, numeroFrn, seqAlmox, situacao, estocavel,
				codigoUnidadeMedida, codigoGrupoMaterial, termoLivre, classificacaoMaterial);
	}

	@Override
	public void validaCamposObrigatorios(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException {
		getSceReqMateriaisRN().validaCamposObrigatorios(sceReqMateriais);
	}

	@Override
	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriais(Integer reqMaterialSeq) {
		return getSceItemRmsRN().pesquisarListaSceItemRmsPorSceReqMateriais(reqMaterialSeq);
	}

	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(Integer reqMaterialSeq, DominioOrderBy orderBy,
			DominioSituacaoRequisicaoMaterial indSituacao) {
		return getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(reqMaterialSeq, orderBy, indSituacao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterDocumentoFiscalEntradaPorSeq(java.lang.Integer)
	 */
	@Override
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorSeq(Integer seq) {
		return this.getSceDocumentoFiscalEntradaDAO().obterDocumentoFiscalEntradaPorSeq(seq);
	}

	@Override
	public void persistirSceItemRecbXProgrEntrega(List<PriorizaEntregaVO> listaPriorizacao) throws ApplicationBusinessException {
		this.getSceItemRecbXProgrEntregaON().persistirSceItemRecbXProgrEntrega(listaPriorizacao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterDocumentoFiscalEntradaPorSeqFornecedor(java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorSeqFornecedor(Integer seq, Integer numeroFornecedor) {
		return this.getSceDocumentoFiscalEntradaDAO().obterDocumentoFiscalEntradaPorSeqFornecedor(seq, numeroFornecedor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterDocumentoFiscalEntradaPorNumeroSerieFornecedor(java.lang.Long,
	 * java.lang.String, java.lang.Integer)
	 */
	@Override
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorNumeroSerieFornecedor(Long numero, String serie, Integer numeroFornecedor) {
		return this.getSceDocumentoFiscalEntradaDAO().obterDocumentoFiscalEntradaPorNumeroSerieFornecedor(numero, serie, numeroFornecedor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterDocumentoFiscalEntradaPorNumeroSerie(java.lang.Long,
	 * java.lang.String)
	 */
	@Override
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorNumeroSerie(Long numero, String serie) {
		return this.getSceDocumentoFiscalEntradaDAO().obterDocumentoFiscalEntradaPorNumeroSerie(numero, serie);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarNotaRecebimentoPorDocumentoFiscalEntrada
	 * (br.gov.mec.aghu.model.SceDocumentoFiscalEntrada)
	 */
	@Override
	public List<SceNotaRecebimento> pesquisarNotaRecebimentoPorDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		return this.getSceNotaRecebimentoDAO().pesquisarNotaRecebimentoPorDocumentoFiscalEntrada(documentoFiscalEntrada);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#buscaMateriaisItensImprimir
	 * (java.lang.Integer, br.gov.mec.aghu.dominio.DominioOrderBy, boolean)
	 */
	@Override
	public RequisicaoMaterialVO buscaMateriaisItensImprimir(Integer reqMat, DominioOrderBy orderBy, boolean inserirMedia)
			throws BaseException {
		return getSceReqMateriaisRN().buscaMateriaisItensImprimir(reqMat, orderBy, inserirMedia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#buscaDadosPosicaoFinalEstoque
	 * (java.util.Date, java.lang.Integer, java.lang.String, java.lang.String,
	 * java.lang.Integer)
	 */
	@Override
	public List<PosicaoFinalEstoqueVO> buscaDadosPosicaoFinalEstoque(Date dtCompetencia, Integer codigoGrupo, String estocavel,
			String orderBy, Integer fornecedor, String siglaTipoUsoMdto, Short almoxSeq) throws BaseException {
		return getSceEstoqueGeralDAO().buscaDadosPosicaoFinalEstoque(dtCompetencia, codigoGrupo, estocavel, orderBy, fornecedor,
				siglaTipoUsoMdto, almoxSeq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisaDadosNotaRecebimento
	 * (java.lang.Integer, boolean)
	 */
	@Override
	public NotaRecebimentoVO pesquisaDadosNotaRecebimento(Integer numNotaRec, boolean isConsiderarNotaEmpenho) throws BaseException {
		return getSceNotaRecebimentoRN().pesquisaDadosNotaRecebimento(numNotaRec, isConsiderarNotaEmpenho);
	}

	@Override
	public SceNotaRecebimento obterSceNotaRecebimento(Integer seq) {
		return getSceNotaRecebimentoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public ImpImpressora defineImpressoraImpressao(SceReqMaterial reqMateriais) throws BaseException {
		return getSceReqMateriaisRN().defineImpressoraImpressao(reqMateriais);
	}

	@Override
	public SceEstoqueAlmoxarifado buscaSceEstoqueAlmoxarifadoPorId(Integer seq) {
		return this.getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoPorId(seq);
	}

	@Override
	@BypassInactiveModule
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricao(Object parametro) {
		return getSceAlmoxarifadoDAO().pesquisarAlmoxarifadosAtivosPorCodigoDescricao(parametro);
	}

	@Override
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(Object parametro) {
		return getSceAlmoxarifadoDAO().pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
	}

	@Override
	public List<RelatorioMateriaisValidadeVencidaVO> pesquisarDadosRelatorioMaterialValidadeVencida(Short seqAlmoxarifado,
			Integer codigoGrupo, Date dataInicial, Date dataFinal, Integer numeroFornecedor) throws ApplicationBusinessException {
		return getGerarRelatorioMateriaisValidadeVencidaON().pesquisarDadosRelatorioMaterialValidadeVencida(seqAlmoxarifado, codigoGrupo,
				dataInicial, dataFinal, numeroFornecedor);
	}

	@Override
	public String gerarCSVRelatorioRelatorioMaterialValidadeVencida(Short seqAlmoxarifado, Integer codigoGrupo, Date dataInicial,
			Date dataFinal, Integer numeroFornecedor) throws IOException, ApplicationBusinessException {
		return getGerarRelatorioMateriaisValidadeVencidaON().gerarCSVRelatorioRelatorioMaterialValidadeVencida(seqAlmoxarifado,
				codigoGrupo, dataInicial, dataFinal, numeroFornecedor);
	}

	@Override
	public boolean dataValida(Date dtEmissaoInicial, Date dtEmissaoFinal){
		return sceDocumentoFiscalEntradaRN.dataValida(dtEmissaoInicial,dtEmissaoFinal);
	}

	@Override
	public void efetuarDownloadCSVMaterialValidadeVencida(final String fileName) throws IOException {
		getGerarRelatorioMateriaisValidadeVencidaON().nameHeaderEfetuarDownloadCSVMaterialValidadeVencida();
	}

	@Override
	public List<RelatorioAjusteEstoqueVO> pesquisarDadosRelatorioAjusteEstoque(Date dataCompetencia, List<String> siglasTipoMovimento) {
		return getGerarRelatorioAjusteEstoqueON().pesquisarDadosRelatorioAjusteEstoque(dataCompetencia, siglasTipoMovimento);
	}

	@Override
	public List<RelatorioContagemEstoqueParaInventarioVO> pesquisarDadosRelatorioContagemEstoqueInventario(Short seqAlmoxarifado,
			Integer codigoGrupo, DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, boolean disponivelEstoque) throws ApplicationBusinessException {
		return getGerarRelatorioContagemEstoqueInventarioON().pesquisarDadosRelatorioContagemEstoqueInventario(seqAlmoxarifado,
				codigoGrupo, estocavel, ordem, disponivelEstoque);
	}

	@Override
	public String geraCSVRelatorioContagemEstoqueInventario(Short seqAlmoxarifado, Integer codigoGrupo,
			DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, boolean disponivelEstoque, boolean mostraSaldo) throws IOException {
		return getGerarRelatorioContagemEstoqueInventarioON().geraCSVRelatorioContagemEstoqueInventario(seqAlmoxarifado, codigoGrupo,
				estocavel, ordem, disponivelEstoque, mostraSaldo);
	}

	@Override
	public List<SceReqMaterial> pesquisarRequisicaoMaterialFiltroCompleto(SceReqMaterial reqMaterial, int firstResult, int maxResults,
			String orderProperty, boolean asc, Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material) {
		return getSceReqMateriaisDAO().pesquisarRequisicaoCompleta(reqMaterial, firstResult, maxResults, orderProperty, asc, dataLimite,
				dtLimiteInferior,material);
	}

	@Override
	public Long pesquisarRequisicaoMaterialFiltroCompletoCount(SceReqMaterial reqMaterial, Date dataLimite, Boolean dtLimiteInferior,ScoMaterial material) {
		return getSceReqMateriaisDAO().pesquisarRequisicaoCompletaCount(reqMaterial, dataLimite, dtLimiteInferior,material);
	}

	@Override
	public List<SceNotaRecebimento> pesquisarNotasRecebimentoConsulta(Integer seqNotaRecebimento, Boolean estorno,
			Boolean debitoNotaRecebimento, DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, Date dataSituacao, Date dataFinal,
			Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn,
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getSceNotaRecebimentoDAO().pesquisarNotasRecebimentoConsulta(seqNotaRecebimento, estorno, debitoNotaRecebimento,
				situacaoPesquisaNR, dataSituacao, dataFinal, numeroProcessoCompAutorizacaoForn, numeroComplementoAutorizacaoForn,
				situacaoAutorizacaoForn, numeroFornecedor, documentoFiscalEntrada, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarNotasRecebimentoConsultaCount(Integer seqNotaRecebimento, Boolean estorno, Boolean debitoNotaRecebimento,
			DominioSituacaoPesquisaNotaRecebimento situacaoPesquisaNR, Date dataSituacao, Date dataFinal,
			Integer numeroProcessoCompAutorizacaoForn, Short numeroComplementoAutorizacaoForn,
			DominioSituacaoAutorizacaoFornecimento situacaoAutorizacaoForn, Integer numeroFornecedor,
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		return getSceNotaRecebimentoDAO().pesquisarNotasRecebimentoConsultaCount(seqNotaRecebimento, estorno, debitoNotaRecebimento,
				situacaoPesquisaNR, dataSituacao, dataFinal, numeroProcessoCompAutorizacaoForn, numeroComplementoAutorizacaoForn,
				situacaoAutorizacaoForn, numeroFornecedor, documentoFiscalEntrada);
	}

	@Override
	public SceNotaRecebimento obterNotaRecebimento(Integer seqNotaRecebimento, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getSceNotaRecebimentoDAO().obterPorChavePrimaria(seqNotaRecebimento, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	public SceNotaRecebimento obterSceNotaRecebimentoFULL(Integer seq) {
		return getSceNotaRecebimentoDAO().obterSceNotaRecebimentoFULL(seq);
	}

	@Override
	public SceNotaRecebimento obterNotaRecebimento(Integer seqNotaRecebimento) {
		return getSceNotaRecebimentoDAO().obterPorChavePrimaria(seqNotaRecebimento);
	}

	@Override
	public List<ItemNotaRecebimentoVO> pesquisarItensNotaRecebimento(Integer seqNotaRecebimento) {
		return this.gerarItemNotaRecebimentoON.pesquisarItensNotaRecebimento(seqNotaRecebimento);
	}

	@Override
	public String obterSiglaTipoMovimento(AghuParametrosEnum pSiglaTipoMovimento) throws ApplicationBusinessException {
		return getSceTipoMovimentosRN().obterSiglaTipoMovimento(pSiglaTipoMovimento);
	}

	@Override
	public String gerarCSVRelatorioAjusteEstoque(Date dtCompetencia, List<String> siglasTipoMovimento) throws IOException,
			ApplicationBusinessException {
		return getGerarRelatorioAjusteEstoqueON().gerarCSVRelatorioAjusteEstoque(dtCompetencia, siglasTipoMovimento);
	}

	@Override
	public String gerarCSVRelatorioMensalMateriaisClassificacaoAbc(Date dtCompetencia) throws ApplicationBusinessException, IOException {
		return getGerarRelatorioMateriaisClassificacaoABCON().gerarCSVRelatorioMensalMateriaisClassificacaoAbc(dtCompetencia);
	}

	@Override
	public String nameHeaderEfetuarDownloadCSVMensalMateriaisClassificacaoAbc(Date competencia) {
		return getGerarRelatorioMateriaisClassificacaoABCON().nameHeaderEfetuarDownloadCSVMensalMateriaisClassificacaoAbc(competencia);
	}

	public String nameHeaderEfetuarDownloadCSVRelatorioAjusteEstoque(Date competencia) {
		return getGerarRelatorioAjusteEstoqueON().nameHeaderEfetuarDownloadCSVRelatorioAjusteEstoque(competencia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(java.util.Date)
	 */
	@Override
	public List<RelatorioMensalMateriaisClassificacaoAbcVO> pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(Date mesCompetencia)
			throws ApplicationBusinessException {
		return getGerarRelatorioMateriaisClassificacaoABCON().pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(mesCompetencia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisarEstoqueGeral
	 * (java.lang.Integer, java.lang.Integer, java.lang.String, boolean,
	 * java.util.Date, br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia,
	 * java.lang.Integer, java.lang.Integer, java.lang.String,
	 * java.lang.Integer, java.lang.Boolean, java.lang.Boolean,
	 * java.lang.String, br.gov.mec.aghu.dominio.DominioClassifABC,
	 * br.gov.mec.aghu.dominio.DominioClassifABC)
	 */
	@Override
	public List<SceEstoqueGeral> pesquisarEstoqueGeral(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Date dataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia, Integer fornecedor, Integer matCodigo,
			String nomeMaterial, Integer gmtCodigo, Boolean indEstocavel, Boolean indGenerico, String umdCodigo, DominioClassifABC clasABC,
			DominioClassifABC subClasABC) {

		return getSceEstoqueGeralDAO().pesquisarEstoqueGeral(firstResult, maxResult, orderProperty, asc, dataCompetencia,
				comparacaoDataCompetencia, fornecedor, matCodigo, nomeMaterial, gmtCodigo, indEstocavel, indGenerico, umdCodigo, clasABC,
				subClasABC);

	}

	public List<SceEstoqueGeral> listarEstoqueMaterial(Integer codigoMaterial){
		return getSceEstoqueGeralDAO().listarEstoqueMaterial(codigoMaterial);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisarEstoqueGeralCount
	 * (java.util.Date,
	 * br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia,
	 * java.lang.Integer, java.lang.Integer, java.lang.String,
	 * java.lang.Integer, java.lang.Boolean, java.lang.Boolean,
	 * java.lang.String, br.gov.mec.aghu.dominio.DominioClassifABC,
	 * br.gov.mec.aghu.dominio.DominioClassifABC)
	 */
	@Override
	public Long pesquisarEstoqueGeralCount(Date dataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer fornecedor, Integer matCodigo, String nomeMaterial, Integer gmtCodigo, Boolean indEstocavel, Boolean indGenerico,
			String umdCodigo, DominioClassifABC clasABC, DominioClassifABC subClasABC) {
		return getSceEstoqueGeralDAO().pesquisarEstoqueGeralCount(dataCompetencia, comparacaoDataCompetencia, fornecedor, matCodigo,
				nomeMaterial, gmtCodigo, indEstocavel, indGenerico, umdCodigo, clasABC, subClasABC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDatasCompetenciasEstoqueGeralPorMesAno(java.lang.Object)
	 */
	@Override
	public List<EstoqueGeralVO> pesquisarDatasCompetenciasEstoqueGeralPorMesAno(Object paramPesquisa) throws ApplicationBusinessException {
		return getPesquisarEstoqueGeralON().pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(paramPesquisa);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterMovimetnoMaterialPorId
	 * (br.gov.mec.aghu.model.SceMovimentoMaterialId)
	 */
	@Override
	public SceMovimentoMaterial obterMovimetnoMaterialPorId(SceMovimentoMaterialId mmtId) {
		return getMovimentoMaterialON().obterMovimetnoMaterialPorId(mmtId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterDetalhesAjusteEstoque
	 * (java.lang.Integer)
	 */
	@Override
	public RelatorioDetalhesAjusteEstoqueVO obterDetalhesAjusteEstoque(Integer mmtSeq) {
		return getSceMovimentoMaterialDAO().obterDetalhesAjusteEstoque(mmtSeq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#persistirMovimentoMaterial
	 * (br.gov.mec.aghu.model.SceMovimentoMaterial)
	 */
	@Override
	public void persistirMovimentoMaterial(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador) throws BaseException {
		getMovimentoMaterialON().inserir(sceMovimentoMaterial, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserirValidadeMaterial
	 * (br.gov.mec.aghu.model.SceValidade,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial)
	 */
	@Override
	public void inserirValidadeMaterial(SceValidade sceValidade, SceMovimentoMaterial movimento) throws BaseException {
		getValidadeMaterialON().inserir(sceValidade, movimento, sceValidade.getQtdeDisponivel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserirValidadeMaterial
	 * (br.gov.mec.aghu.model.SceValidade)
	 */
	@Override
	public void inserirValidadeMaterial(SceValidade validade) throws BaseException {
		getSceValidadesRN().inserir(validade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#excluirValidadeMaterial
	 * (br.gov.mec.aghu.model.SceValidade)
	 */
	@Override
	public void excluirValidadeMaterial(SceValidade validade) throws BaseException {
		getSceValidadesRN().remover(validade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#atualizarValidadeMaterial
	 * (br.gov.mec.aghu.model.SceValidade)
	 */
	@Override
	public void atualizarValidadeMaterial(SceValidade validade) throws BaseException {
		getSceValidadesRN().atualizar(validade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserirValidadeMaterial
	 * (br.gov.mec.aghu.model.SceValidade,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial, java.lang.Integer)
	 */
	@Override
	public void inserirValidadeMaterial(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeDisponivel)
			throws BaseException {
		getValidadeMaterialON().inserir(sceValidade, movimento, qtdeDisponivel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#atualizarValidadeMaterial
	 * (br.gov.mec.aghu.model.SceValidade,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial, java.lang.Integer, boolean)
	 */
	@Override
	public void atualizarValidadeMaterial(SceValidade sceValidade, SceMovimentoMaterial movimento, Integer qtdeValidades,
			boolean somaNoFinal) throws BaseException {
		getValidadeMaterialON().atualizar(sceValidade, movimento, qtdeValidades, somaNoFinal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * listarLotesPorCodigoOuMarcaComercialEMaterial(java.lang.Object,
	 * java.lang.Integer)
	 */
	@Override
	public List<SceLote> listarLotesPorCodigoOuMarcaComercialEMaterial(Object objPesquisa, Integer codigoMaterial) {
		return getSceLoteDAO().listarLotesPorCodigoOuMarcaComercialEMaterial(objPesquisa, codigoMaterial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterValidadePorDataValidadeEstoqueAlmoxarifado(java.util.Date,
	 * java.lang.Integer)
	 */
	@Override
	public SceValidade obterValidadePorDataValidadeEstoqueAlmoxarifado(Date dtValidade, Integer seqEstoqueAlmoxarifado) {
		return getSceValidadeDAO().obterValidadePorDataValidadeEstoqueAlmoxarifado(dtValidade, seqEstoqueAlmoxarifado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * listarValidadesPorEstoqueAlmoxarifado(java.lang.Integer)
	 */
	@Override
	public List<SceValidade> listarValidadesPorEstoqueAlmoxarifado(Integer ealSeq) {
		return getSceValidadeDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(ealSeq, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarLoteFornecedorPorMaterialValidade(java.lang.Integer,
	 * java.util.Date)
	 */
	@Override
	public List<SceLoteFornecedor> pesquisarLoteFornecedorPorMaterialValidade(Integer lotMatCodigo, Date dtValidade) {
		return getSceLoteFornecedorDAO().pesquisarLoteFornecedorPorMaterialValidade(lotMatCodigo, dtValidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserirLoteFornecedor
	 * (br.gov.mec.aghu.model.SceLoteFornecedor,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public void inserirLoteFornecedor(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes)
			throws BaseException {
		getLoteFornecedorON().inserir(loteForn, movimento, qtdeValidades, qtdeLotes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#atualizarLoteFornecedor
	 * (br.gov.mec.aghu.model.SceLoteFornecedor,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial, java.lang.Integer,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void atualizarLoteFornecedor(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades,
			Integer qtdeLotes, Integer loteQtdeAnterior) throws BaseException {
		getLoteFornecedorON().atualizar(loteForn, movimento, qtdeValidades, qtdeLotes, loteQtdeAnterior);
	}

	@Override
	public Date obterDtUltimaMovimentacao(Integer codMaterial, Short almSeq, Short tmvSeq) {
		return getSceMovimentoMaterialDAO().obterDtUltimaMovimentacao(codMaterial, almSeq, tmvSeq);
	}

	@Override
	public Date obterDtUltimaCompra(Integer codMaterial, Short almSeq) {
		return getSceMovimentoMaterialDAO().obterDtUltimaCompra(codMaterial, almSeq);
	}

	@Override
	public BigDecimal obterValorUltimaCompra(Integer codMaterial, Short almSeq, Short tmvSeq) {
		return getSceMovimentoMaterialDAO().obterValorUltimaCompra(codMaterial, almSeq, tmvSeq);
	}

	/**
	 * get de ONs e DAOs
	 */

	private SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}

	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}

	private SceLoteDAO getSceLoteDAO() {
		return sceLoteDAO;
	}

	private SceValidadeDAO getSceValidadeDAO() {
		return sceValidadeDAO;
	}

	private SceLoteFornecedorDAO getSceLoteFornecedorDAO() {
		return sceLoteFornecedorDAO;
	}

	private SceReqMateriaisDAO getSceReqMateriaisDAO() {
		return sceReqMateriaisDAO;
	}

	private SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}

	private SceItemRmsDAO getSceItemRmsDAO() {
		return sceItemRmsDAO;
	}

	private SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}

	private ScePacoteMateriaisDAO getScePacoteMateriaisDAO() {
		return scePacoteMateriaisDAO;
	}

	private SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}

	private SceReqMateriaisRN getSceReqMateriaisRN() {
		return sceReqMateriaisRN;
	}

	private SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}

	private SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}

	private SceMotivoMovimentoDAO getSceMotivoMovimentoDAO() {
		return sceMotivoMovimentoDAO;
	}

	private SceItemRmsRN getSceItemRmsRN() {
		return sceItemRmsRN;
	}

	private SceMotivoProblemaDAO getSceMotivoProblemaDAO() {
		return sceMotivoProblemaDAO;
	}

	private SceFornecedorEventualDAO getSceFornecedorEventualDAO() {
		return sceFornecedorEventualDAO;
	}

	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	private SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}

	private ManterMaterialON getManterMaterialON() {
		return manterMaterialON;
	}

	private GerarRelatorioTransferenciaMaterialON getGerarRelatorioTransferenciaMaterialON() {
		return gerarRelatorioTransferenciaMaterialON;
	}

	private GerarRelatorioMateriaisValidadeVencidaON getGerarRelatorioMateriaisValidadeVencidaON() {
		return gerarRelatorioMateriaisValidadeVencidaON;
	}

	public SceEstoqueGeralRN getSceEstoqueGeralRN() {
		return sceEstoqueGeralRN;
	}

	private GerarRelatorioContagemEstoqueInventarioON getGerarRelatorioContagemEstoqueInventarioON() {
		return gerarRelatorioContagemEstoqueInventarioON;
	}

	private GerarRelatorioAjusteEstoqueON getGerarRelatorioAjusteEstoqueON() {

		return gerarRelatorioAjusteEstoqueON;
	}

	private GerarRelatorioMateriaisClassificacaoABCON getGerarRelatorioMateriaisClassificacaoABCON() {
		return gerarRelatorioMateriaisClassificacaoABCON;
	}

	private MovimentoMaterialON getMovimentoMaterialON() {
		return movimentoMaterialON;
	}

	private ValidadeMaterialON getValidadeMaterialON() {
		return validadeMaterialON;
	}

	private SceValidadesRN getSceValidadesRN() {
		return sceValidadesRN;
	}

	private LoteFornecedorON getLoteFornecedorON() {
		return loteFornecedorON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(java.lang.Object)
	 */
	@Override
	public List<SceMovimentoMaterial> pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(Object parametro)
			throws ApplicationBusinessException {
		return getMovimentoMaterialON().pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(parametro);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarEstornarNotaRecebimentoCount
	 * (br.gov.mec.aghu.model.SceNotaRecebimento)
	 */
	@Override
	public Long pesquisarEstornarNotaRecebimentoCount(SceNotaRecebimento notaRecebimento) {
		return this.getSceNotaRecebimentoDAO().pesquisarEstornarNotaRecebimentoCount(notaRecebimento);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarEstornarNotaRecebimento(java.lang.Integer, java.lang.Integer,
	 * java.lang.String, boolean, br.gov.mec.aghu.model.SceNotaRecebimento)
	 */
	@Override
	public List<SceNotaRecebimento> pesquisarEstornarNotaRecebimento(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceNotaRecebimento notaRecebimento) {
		return this.getSceNotaRecebimentoDAO()
				.pesquisarEstornarNotaRecebimento(firstResult, maxResult, orderProperty, asc, notaRecebimento);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDocumentoFiscalEntradaCount
	 * (br.gov.mec.aghu.model.SceDocumentoFiscalEntrada)
	 */
	@Override
	public Long pesquisarDocumentoFiscalEntradaCount(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		return this.getSceDocumentoFiscalEntradaDAO().pesquisarDocumentoFiscalEntradaCount(documentoFiscalEntrada);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDocumentoFiscalEntrada(java.lang.Integer, java.lang.Integer,
	 * java.lang.String, boolean,
	 * br.gov.mec.aghu.model.SceDocumentoFiscalEntrada)
	 */
	@Override
	public List<SceDocumentoFiscalEntrada> pesquisarDocumentoFiscalEntrada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		return this.getSceDocumentoFiscalEntradaDAO().pesquisarDocumentoFiscalEntrada(firstResult, maxResult, orderProperty, asc,
				documentoFiscalEntrada);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#gerarNotaRecebimento(
	 * br.gov.mec.aghu.model.SceNotaRecebimento)
	 */
	@Override
	public void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		getGerarNotaRecebimentoON().gerarNotaRecebimento(notaRecebimento, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarNotaRecebimentoSolicitacaoCompraAutomatica
	 * (br.gov.mec.aghu.model.SceNotaRecebimento)
	 */
	@Override
	public void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador)
			throws BaseException {
		getGerarNotaRecebimentoON().gerarNotaRecebimentoSolicitacaoCompraAutomatica(notaRecebimento, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#gerarItemNotaRecebimento
	 * (br.gov.mec.aghu.model.SceItemNotaRecebimento)
	 */
	@Override
	public void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador, boolean flush)
			throws BaseException {
		this.getGerarItemNotaRecebimentoON().gerarItemNotaRecebimento(itemNotaRecebimento, nomeMicrocomputador, flush);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarItemNotaRecebimentoSolicitacaoCompraAutomatica
	 * (br.gov.mec.aghu.model.SceItemNotaRecebimento, java.lang.Short,
	 * br.gov.mec.aghu.model.ScoMarcaComercial, java.lang.Integer)
	 */
	@Override
	public void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento, Short numeroItem,
			ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException {
		this.getGerarItemNotaRecebimentoON().gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(itemNotaRecebimento, numeroItem,
				marcaComercial, fatorConversao, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#atualizarNotaRecebimento
	 * (br.gov.mec.aghu.model.SceNotaRecebimento)
	 */
	@Override
	public void atualizarNotaRecebimento(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal,
			String nomeMicrocomputador, Boolean flush) throws BaseException {
		getSceNotaRecebimentoRN().atualizar(notaRecebimento, notaRecebimentoOriginal, nomeMicrocomputador, flush);
	}

	private GerarNotaRecebimentoON getGerarNotaRecebimentoON() {
		return gerarNotaRecebimentoON;
	}

	private GerarItemNotaRecebimentoON getGerarItemNotaRecebimentoON() {
		return gerarItemNotaRecebimentoON;
	}

	private SceNotaRecebimentoRN getSceNotaRecebimentoRN() {
		return sceNotaRecebimentoRN;
	}

	private SceDocumentoFiscalEntradaDAO getSceDocumentoFiscalEntradaDAO() {
		return sceDocumentoFiscalEntradaDAO;
	}

	private SceDevolucaoAlmoxarifadoDAO getSceDevolucaoAlmoxarifadoDAO() {
		return sceDevolucaoAlmoxarifadoDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * persistirDocumentoFiscalEntrada
	 * (br.gov.mec.aghu.model.SceDocumentoFiscalEntrada)
	 */
	@Override
	public void persistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		getManterDocumentoFiscalEntradaON().persistirDocumentoFiscalEntrada(documentoFiscalEntrada);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#removerDocumentoFiscalEntrada
	 * (br.gov.mec.aghu.model.SceDocumentoFiscalEntrada)
	 */
	@Override
	public void removerDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		getManterDocumentoFiscalEntradaON().removerDocumentoFiscalEntrada(documentoFiscalEntrada);
	}

	protected EfetivarRequisicaoMaterialON getEfetivarRequisicaoMaterialON() {
		return efetivarRequisicaoMaterialON;
	}

	private ManterDocumentoFiscalEntradaON getManterDocumentoFiscalEntradaON() {
		return manterDocumentoFiscalEntradaON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#efetivarRequisicaoMaterial
	 * (br.gov.mec.aghu.model.SceReqMaterial)
	 */
	@Override
	public void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		getEfetivarRequisicaoMaterialON().efetivarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#estornarRequisicaoMaterial
	 * (br.gov.mec.aghu.model.SceReqMaterial)
	 */
	@Override
	public void estornarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		getEstornarRequisicaoMaterialON().estornarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}

	protected GerarRequisicaoMaterialON getGerarRequisicaoMaterialON() {
		return gerarRequisicaoMaterialON;
	}

	protected ManterEstoqueAlmoxarifadoON getManterEstoqueAlmoxarifadoON() {
		return manterEstoqueAlmoxarifadoON;
	}

	protected HistoricoProblemaMaterialON getHistoricoProblemaMaterialON() {
		return historicoProblemaMaterialON;
	}

	protected PesquisarEstoqueAlmoxarifadoON getPesquisarEstoqueAlmoxarifadoON() {
		return pesquisarEstoqueAlmoxarifadoON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#persistirRequisicaoMaterial
	 * (br.gov.mec.aghu.model.SceReqMaterial)
	 */
	@Override
	public void persistirRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		getGerarRequisicaoMaterialON().persistirRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * persistirItensRequisicaoMaterial(br.gov.mec.aghu.model.SceItemRms,
	 * java.lang.Boolean)
	 */
	@Override
	public void persistirItensRequisicaoMaterial(SceItemRms sceItemRms, Boolean estorno, String nomeMicrocomputador) throws BaseException {
		getGerarRequisicaoMaterialON().persistirItensRequisicaoMaterial(sceItemRms, estorno, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#excluirItemRequisicaoMaterial
	 * (br.gov.mec.aghu.model.SceItemRms, java.lang.Integer, java.lang.Boolean)
	 */
	@Override
	public void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException {
		getGerarRequisicaoMaterialON().excluirItemRequisicaoMaterial(sceItemRms, countItensLista, estorno);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#persistirEstoqueAlmox
	 * (br.gov.mec.aghu.model.SceEstoqueAlmoxarifado)
	 */
	@Override
	public void persistirEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador) throws BaseException {
		getManterEstoqueAlmoxarifadoON().persistirEstoqueAlmox(estoqueAlmox, nomeMicrocomputador);
	}

	@Override
	public Boolean verificarExisteReforcoSolicitacaoCompras(Integer slcNumero) {
		return this.getSceEstoqueAlmoxarifadoDAO().verificarExisteReforcoSolicitacaoCompras(slcNumero);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * desbloqueioQuantidadesEstoqueAlmox
	 * (br.gov.mec.aghu.model.SceEstoqueAlmoxarifado, java.lang.Integer)
	 */
	@Override
	public void desbloqueioQuantidadesEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador) throws BaseException {
		getManterEstoqueAlmoxarifadoON().desbloqueioQuantidadesEstoqueAlmox(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * bloqueioQuantidadesEstoqueAlmox
	 * (br.gov.mec.aghu.model.SceEstoqueAlmoxarifado, java.lang.Integer)
	 */
	@Override
	public void bloqueioQuantidadesEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador) throws BaseException {
		getManterEstoqueAlmoxarifadoON().bloqueioQuantidadesEstoqueAlmox(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
	}

	@Override
	public void bloqueioDesbloqueioPersistirLote(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador, Boolean bloquear, SceLoteDocumento loteDocs) throws BaseException {
		sceLoteDocumentoON.validarDocumentoOrigem(loteDocs);
		if (bloquear) {
			manterEstoqueAlmoxarifadoON.bloqueioQuantidadesEstoqueAlmox(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		} else {
			manterEstoqueAlmoxarifadoON.desbloqueioQuantidadesEstoqueAlmox(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		}
		sceLoteDocumentoON.inserir(loteDocs);
	}

	@Override
	public void bloqueioDesbloqueioAtualizarLote(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio,
			String nomeMicrocomputador, Boolean bloquear, SceLoteDocumento loteDocs) throws BaseException {
		if (bloquear) {
			manterEstoqueAlmoxarifadoON.bloqueioQuantidadesEstoqueAlmox(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		} else {
			manterEstoqueAlmoxarifadoON.desbloqueioQuantidadesEstoqueAlmox(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);
		}
		sceLoteDocumentoRN.atualizar(loteDocs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * bloqueioDesbloqueioQuantidadesProblema
	 * (br.gov.mec.aghu.model.SceHistoricoProblemaMaterial,
	 * br.gov.mec.aghu.model.SceTipoMovimento, java.lang.Integer)
	 */
	@Override
	public void bloqueioDesbloqueioQuantidadesProblema(SceHistoricoProblemaMaterial historico, SceTipoMovimento tipoMovimento,
			Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException {
		getHistoricoProblemaMaterialON().bloqueioDesbloqueioQuantidadesProblema(historico, tipoMovimento, qtdeAcaoBloqueioDesbloqueio,
				nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificaBotaoVoltarBloqueioDesbloqueioProblema
	 * (br.gov.mec.aghu.model.SceHistoricoProblemaMaterial,
	 * br.gov.mec.aghu.model.SceTipoMovimento)
	 */
	@Override
	public void verificaBotaoVoltarBloqueioDesbloqueioProblema(SceHistoricoProblemaMaterial historico, SceTipoMovimento tipoMovimento,
			List<SceValidade> listaValidadeInicial, Boolean mostrouGradeValidade) throws BaseException {
		getHistoricoProblemaMaterialON().verificaBotaoVoltarBloqueioDesbloqueioProblema(historico, tipoMovimento, listaValidadeInicial,
				mostrouGradeValidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificaBotaoVoltarBloqueioDesbloqueio(java.lang.Integer)
	 */
	@Override
	public void verificaBotaoVoltarBloqueioDesbloqueio(Integer estoqueAlmoxarifado) throws BaseException {
		getManterEstoqueAlmoxarifadoON().verificaBotaoVoltarBloqueioDesbloqueio(estoqueAlmoxarifado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisaItensNotaRecebimentoUltimos60DiasPormaterial(java.lang.Integer,
	 * java.lang.Object)
	 */
	@Override
	public List<SceItemNotaRecebimento> pesquisaItensNotaRecebimentoUltimos60DiasPormaterial(Integer codMaterial, Object param) {
		return getSceItemNotaRecebimentoDAO().pesquisaItensNotaRecebimentoUltimos60DiasPormaterial(codMaterial, param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public List<SceLoteDocumento> pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(Integer estoqueAlmoxarifado, Integer codMaterial) {
		return getSceLoteDocumentoDAO().pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(estoqueAlmoxarifado, codMaterial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#persistirSceLoteDocumento
	 * (br.gov.mec.aghu.model.SceLoteDocumento)
	 */
	@Override
	public void persistirSceLoteDocumento(SceLoteDocumento loteDocumento) throws BaseException {
		getSceLoteDocumentoON().inserir(loteDocumento);
	}

	public void gerarInterfaceamentoUnitarizacao(SceLoteDocImpressao loteDocImpressao, String nomeMicrocomputador, Integer qtdeEtiquetas)
			throws BaseException {
		getImprimirEtiquetasUnitarizacaoON().gerarInterfaceamentoUnitarizacao(loteDocImpressao, nomeMicrocomputador, qtdeEtiquetas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#atualizarSceLoteDocumento
	 * (br.gov.mec.aghu.model.SceLoteDocumento)
	 */
	@Override
	public void atualizarSceLoteDocumento(SceLoteDocumento loteDocumento) throws BaseException {
		getSceLoteDocumentoRN().atualizar(loteDocumento);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarEntradaSaidaPorMaterial(java.lang.Integer, java.lang.Object)
	 */
	@Override
	public List<SceEntradaSaidaSemLicitacao> pesquisarEntradaSaidaPorMaterial(Integer codMaterial, Object param) {
		return getSceEntradaSaidaSemLicitacaoDAO().pesquisarEntradaSaidaPorMaterial(codMaterial, param);
	}

	protected EstornarRequisicaoMaterialON getEstornarRequisicaoMaterialON() {
		return estornarRequisicaoMaterialON;
	}

	protected SceItemNotaRecebimentoRN getSceItemNotaRecebimentoRN() {
		return sceItemNotaRecebimentoRN;
	}

	private ManterTransferenciaON getManterTransferenciaON() {
		return manterTransferenciaON;
	}

	private ManterPacoteMateriaisON getManterPacoteMateriaisON() {
		return manterPacoteMateriaisON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterItensNotaRecebimento
	 * (br.gov.mec.aghu.model.SceNotaRecebimento)
	 */
	@Override
	public List<SceItemNotaRecebimento> obterItensNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		return getSceItemNotaRecebimentoDAO().pesquisarItensNotaRecebimentoPorNotaRecebimento(notaRecebimento);
	}

	protected SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO() {
		return sceItemNotaRecebimentoDAO;
	}

	@Override
	public void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException {
		getEstornarNotaRecebimentoON().estornarNotaRecebimento(seqNotaRecebimento, nomeMicrocomputador);
	}

	protected EstornarNotaRecebimentoON getEstornarNotaRecebimentoON() {
		return estornarNotaRecebimentoON;
	}

	private SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}

	private PesquisarEstoqueGeralON getPesquisarEstoqueGeralON() {
		return pesquisarEstoqueGeralON;
	}

	private SceLoteDocumentoDAO getSceLoteDocumentoDAO() {
		return sceLoteDocumentoDAO;
	}

	private SceLoteDocumentoRN getSceLoteDocumentoRN() {
		return sceLoteDocumentoRN;
	}

	private SceEntradaSaidaSemLicitacaoDAO getSceEntradaSaidaSemLicitacaoDAO() {
		return sceEntradaSaidaSemLicitacaoDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarListaMateriaisParaCatalogo(java.lang.Integer, java.lang.Integer,
	 * java.lang.String, boolean, java.lang.Integer, java.lang.String,
	 * br.gov.mec.aghu.dominio.DominioSituacao,
	 * br.gov.mec.aghu.dominio.DominioSimNao,
	 * br.gov.mec.aghu.dominio.DominioSimNao, java.lang.String,
	 * br.gov.mec.aghu.dominio.DominioClassifyXYZ, java.lang.Integer)
	 */
	@Override
	public List<ScoMaterial> pesquisarListaMateriaisParaCatalogo(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial, DominioSimNao estocavel,
			DominioSimNao generico, String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ, Integer codigoGrupoMaterial,
			Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {

		return getManterMaterialON().pesquisarListaMateriaisParaCatalogo(firstResult, maxResults, orderProperty, asc, codigoMaterial,
				nomeMaterial, situacaoMaterial, estocavel, generico, codigoUnidadeMedida, classifyXYZ, codigoGrupoMaterial, codCatMat,
				codMatAntigo, classificacaoMaterial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarDadosRelatorioTransferenciaMaterialItens(java.lang.Integer,
	 * br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioTransferenciaMaterial)
	 */
	@Override
	public List<RelatorioTransferenciaMaterialVO> gerarDadosRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial,
			DominioOrdenacaoRelatorioTransferenciaMaterial ordemImpressao) throws ApplicationBusinessException {
		return getGerarRelatorioTransferenciaMaterialON().gerarDadosRelatorioTransferenciaMaterialItens(numTransferenciaMaterial,
				ordemImpressao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarCsvRelatorioTransferenciaMaterialItens(java.lang.Integer,
	 * java.util.List, java.util.Map)
	 */
	@Override
	public String gerarCsvRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial,
			List<RelatorioTransferenciaMaterialVO> dados, Map<String, Object> parametros) throws FileNotFoundException, IOException {
		return getGerarRelatorioTransferenciaMaterialON().gerarCsvRelatorioTransferenciaMaterialItens(numTransferenciaMaterial, dados,
				parametros);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * efetuarDownloadCSVRelatorioTransferenciaMaterial(java.lang.String,
	 * java.lang.Integer)
	 */
	@Override
	public String nameHeaderEfetuarDownloadCSVRelatorioTransferenciaMaterial(final Integer numTransferenciaMaterial) {
		return getGerarRelatorioTransferenciaMaterialON().nameHeaderEfetuarDownloadCSVRelatorioTransferenciaMaterial(
				numTransferenciaMaterial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * isFatItensContaHospitalarComMateriaisOrteseseProteses(java.lang.Integer,
	 * java.lang.Short, java.lang.Integer)
	 */
	@Override
	public boolean isFatItensContaHospitalarComMateriaisOrteseseProteses(final Integer cthSeq, final Short seq, final Integer phiSeq)
			throws ApplicationBusinessException {
		return getManterMaterialON().isFatItensContaHospitalarComMateriaisOrteseseProteses(cthSeq, seq, phiSeq);
	}

	@Override
	public boolean isHabilitarExcluirHCPA() {
		return getManterMaterialON().isHabilitarExcluirHCPA();
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarTransferenciaAutomatica(java.lang.Integer, java.lang.Integer,
	 * java.lang.String, boolean, java.lang.Integer, java.util.Date,
	 * java.lang.Boolean, java.lang.Short, java.lang.Short, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public List<SceTransferencia> pesquisarTransferenciaAutomatica(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer seq, Date dtGeracao, Boolean indTransfAutomatica, Short sceAlmoxarifadoSeq, Short sceAlmoxarifadoRecebSeq,
			Long numero, String descricao, String nome, Boolean estorno) {
		return getSceTransferenciaDAO().pesquisarTransferenciaAutomatica(firstResult, maxResult, orderProperty, asc, seq, dtGeracao,
				indTransfAutomatica, sceAlmoxarifadoSeq, sceAlmoxarifadoRecebSeq, numero, descricao, nome, estorno);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarTransferenciaAutomaticaCount(java.lang.Integer, java.util.Date,
	 * java.lang.Boolean, java.lang.Short, java.lang.Short, java.lang.Long,
	 * java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	@Override
	public Long pesquisarTransferenciaAutomaticaCount(Integer seq, Date dtGeracao, Boolean indTransfAutomatica, Short sceAlmoxarifadoSeq,
			Short sceAlmoxarifadoRecebSeq, Long numero, String descricao, String nome, Boolean estorno) {
		return getSceTransferenciaDAO().pesquisarTransferenciaAutomaticaCount(seq, dtGeracao, indTransfAutomatica, sceAlmoxarifadoSeq,
				sceAlmoxarifadoRecebSeq, numero, descricao, nome, estorno);
	}

	@Override
	public SceTransferencia obterTransferenciaAutomatica(Integer codigo) {
		return getManterTransferenciaON().obterTransferenciaAutomatica(codigo);
	}

	@Override
	public SceTransferencia obterTransferenciaPorId(Integer seqTransf) {
		return getSceTransferenciaDAO().obterTransferenciaPorId(seqTransf);
	}

	@Override
	public SceTransferencia obterTransferenciaOriginal(Integer seqTransf) {
		return getSceTransferenciaDAO().obterOriginal(seqTransf);
	}

	@Override
	public List<SceItemTransferencia> pesquisarListaItensTransferenciaPorTransferencia(Integer seqTransferencia) {
		return getSceItemTransferenciaDAO().pesquisarListaItensTransferenciaPorTransferencia(seqTransferencia);
	}

	@Override
	public List<ItemTransferenciaAutomaticaVO> pesquisarListaItensTransferenciaVOPorTransferencia(Integer seqTransferencia) {
		return getSceItemTransferenciaON().pesquisarListaItensTransferenciaVOPorTransferencia(seqTransferencia);
	}

	@Override
	public void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO> listaItemTransferenciaAutomaticaVO,
			String nomeMicrocomputador) throws BaseException {
		this.getSceItemTransferenciaRN().atualizarItensTransfAutoAlmoxarifados(listaItemTransferenciaAutomaticaVO, nomeMicrocomputador);
	}

	@Override
	public void atualizarTransferenciaAutoAlmoxarifado(SceTransferencia transferencia, String nomeMicrocomputador) throws BaseException {
		this.getSceTransferenciaRN().atualizar(transferencia, nomeMicrocomputador);
	}

	@Override
	public void atualizarTransferenciaAutoAlmoxarifado(SceTransferencia transferencia, SceTransferencia oldTransferencia,
			String nomeMicrocomputador) throws BaseException {
		this.getSceTransferenciaRN().atualizar(transferencia, oldTransferencia, nomeMicrocomputador);
	}

	@Override
	public SceItemTransferencia obterItemTransferenciaPorId(SceItemTransferenciaId id) {
		return getSceItemTransferenciaDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		getGerarListaTransferenciaAutoAlmoxarifadoON().persistirTransferenciaAutoAlmoxarifado(transferencia);
	}

	@Override
	public void removerTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		getGerarListaTransferenciaAutoAlmoxarifadoON().removerTransferenciaAutoAlmoxarifado(transferencia);
	}

	@Override
	public void removerTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento,
			Long numeroClassifMatNiv5) throws BaseException {
		getGerarListaTransferenciaAutoAlmoxarifadoON().removerTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado,
				seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);
	}

	@Override
	public void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException {
		getGerarListaTransferenciaAutoAlmoxarifadoON().gerarListaTransferenciaAutoAlmoxarifado(transferencia);
	}

	@Override
	public void removerItemTransferenciaAutoAlmoxarifado(SceItemTransferencia itemTransferencia) throws BaseException {
		getGerarListaTransferenciaAutoAlmoxarifadoON().removerItemTransferenciaAutoAlmoxarifado(itemTransferencia);
	}

	protected GerarListaTransferenciaAutoAlmoxarifadoON getGerarListaTransferenciaAutoAlmoxarifadoON() {
		return gerarListaTransferenciaAutoAlmoxarifadoON;
	}

	@Override
	public Boolean existeTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento,
			Long numeroClassifMatNiv5) {
		return getGerarListaTransferenciaAutoAlmoxarifadoON().existeTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado,
				seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);
	}

	protected SceTransferenciaDAO getSceTransferenciaDAO() {
		return sceTransferenciaDAO;
	}

	@Override
	public void removerPacoteMaterial(final ScePacoteMateriais pacote) throws BaseListException {
		getManterPacoteMateriaisON().removerPacoteMaterial(pacote);
	}

	@Override
	public FccCentroCustos obterFccCentroCustos(final Integer codigo) {
		return getManterPacoteMateriaisON().obterFccCentroCustos(codigo);
	}

	@Override
	public ScePacoteMateriais obterPacoteMateriaisComItensPorChavePrimaria(ScePacoteMateriaisId pacoteId) {
		return getManterPacoteMateriaisON().obterPacoteMateriaisComItensPorChavePrimaria(pacoteId);
	}

	@Override
	public List<ScePacoteMateriais> pesquisarPacoteMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote, Short numeroAlmoxarifado,
			DominioSituacao situacao) {
		return getManterPacoteMateriaisON().pesquisarPacoteMateriais(firstResult, maxResult, orderProperty, asc,
				codigoCentroCustoProprietario, codigoCentroCustoAplicacao, numeroPacote, numeroAlmoxarifado, situacao);
	}

	@Override
	public Long pesquisarPacoteMateriaisCount(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao,
			Integer numeroPacote, Short numeroAlmoxarifado, DominioSituacao situacao) {
		return getManterPacoteMateriaisON().pesquisarPacoteMateriaisCount(codigoCentroCustoProprietario, codigoCentroCustoAplicacao,
				numeroPacote, numeroAlmoxarifado, situacao);
	}

	@Override
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(Object parametro) {
		return getManterPacoteMateriaisON().pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(parametro);
	}

	@Override
	public List<SceItemPacoteMateriais> pesquisarItensPacoteMateriais(Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numero) {
		return getManterPacoteMateriaisON()
				.pesquisarItensPacoteMateriais(codigoCentroCustoProprietario, codigoCentroCustoAplicacao, numero);
	}

	@Override
	public PacoteMateriaisVO obterPacoteMaterialVO(ScePacoteMateriaisId id) {
		return getManterPacoteMateriaisON().obterPacoteMaterialVO(id);
	}

	@Override
	public List<PendenciasDevolucaoVO> pesquisarPendenciasDevolucao(Integer numeroNr) {
		return this.getConfirmacaoDevolucaoON().pesquisarPendenciasDevolucao(numeroNr);
	}

	private ConfirmacaoDevolucaoON getConfirmacaoDevolucaoON() {
		return confirmacaoDevolucaoON;
	}

	@Override
	public SceNotaRecebimento obterNotaRecebimentoPorNotaRecebimentoProvisorio(Integer nrpSeq) {
		return getSceNotaRecebimentoDAO().obterNotaRecebimentoPorNotaRecebimentoProvisorio(nrpSeq);
	}

	@Override
	public SceAlmoxarifado obterAlmoxarifadoPorId(Short seq) {
		if (seq == null) {
			return null;
		}
		return getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeq(seq);
	}

	@Override
	public List<ItemPacoteMateriaisVO> pesquisarItensPacoteMateriaisVO(Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numeroPacote) throws ApplicationBusinessException {
		return getManterPacoteMateriaisON().pesquisarItensPacoteMateriaisVO(codigoCentroCustoProprietario, codigoCentroCustoAplicacao,
				numeroPacote);
	}

	@Override
	public void persistirPacoteMaterais(ScePacoteMateriais pacote) throws BaseListException {
		getManterPacoteMateriaisON().persistirPacoteMaterais(pacote);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarMateriaisEstoquePorCodigoDescricaoAlmoxarifado(String parametro, Short seAlmoxarifado,
			List<Integer> listaGrupos, Boolean somenteEstocaveis, Boolean somenteDiretos) throws ApplicationBusinessException {
		return getManterEstoqueAlmoxarifadoON().pesquisarMateriaisEstoquePorCodigoDescricao(parametro, seAlmoxarifado, listaGrupos,
				somenteEstocaveis, somenteDiretos);
	}

	@Override
	public SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoFornecedorPadrao(Short seqAlmoxarifado, Integer codigoMaterial) throws ApplicationBusinessException {
		return getGerarRequisicaoMaterialON().pesquisarEstoqueAlmoxarifadoFornecedorPadrao(seqAlmoxarifado, codigoMaterial);
	}

	@Override
	public ItemPacoteMateriaisVO obterItemPacoteMateriaisVO(Integer seqEstoqueAlmoxarifado, Integer codigoMaterial)
			throws ApplicationBusinessException {
		return getManterPacoteMateriaisON().obterItemPacoteMateriaisVO(seqEstoqueAlmoxarifado, codigoMaterial);
	}

	@Override
	public void verificarInclusaoItensPacoteMateriais(List<ItemPacoteMateriaisVO> itensPacote, Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numeroPacote, Integer seqEstoque, Integer quantidade, Integer codigoMaterial,
			Short seqAlmoxarifadoPai, Short seqAlmoxarifadoFilho, DominioSituacao situacaoAlmoxarifadoPai) throws BaseListException,
			ApplicationBusinessException {
		getManterPacoteMateriaisON().verificarInclusaoItensPacoteMateriais(itensPacote, codigoCentroCustoProprietario,
				codigoCentroCustoAplicacao, numeroPacote, seqEstoque, quantidade, codigoMaterial, seqAlmoxarifadoPai, seqAlmoxarifadoFilho,
				situacaoAlmoxarifadoPai);
	}

	@Override
	public void verificarUltimoItemPacoteMateriais(int quantidade) throws ApplicationBusinessException {
		getManterPacoteMateriaisON().verificarUltimoItemPacoteMateriais(quantidade);

	}

	// 6617
	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarSceEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceEstoqueAlmoxarifado estoqueAlmox) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarSceEstoqueAlmoxarifado(firstResult, maxResult, orderProperty, asc, estoqueAlmox);
	}

	// 6617
	@Override
	public Long pesquisarSceEstoqueAlmoxarifadoCount(SceEstoqueAlmoxarifado estoqueAlmox) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarSceEstoqueAlmoxarifadoCount(estoqueAlmox);
	}

	@Override
	public Long pesquisarSceAlmoxTransfAutomaticasCount(Short almoxOrigem, Short almoxDestino, DominioSituacao situacao) {
		return getSceAlmoxarifadoTransferenciaAutomaticaDAO().pesquisarSceAlmoxTransfAutomaticasCount(almoxOrigem, almoxDestino, situacao);
	}

	@Override
	public List<SceAlmoxarifadoTransferenciaAutomatica> pesquisarSceAlmoxTransfAutomaticas(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Short almoxOrigem, Short almoxDestino, DominioSituacao situacao) {
		return getSceAlmoxarifadoTransferenciaAutomaticaDAO().pesquisarSceAlmoxTransfAutomaticas(firstResult, maxResult, orderProperty,
				asc, almoxOrigem, almoxDestino, situacao);
	}

	@Override
	public void inserirSceAlmoxTransfAutomaticas(SceAlmoxarifadoTransferenciaAutomatica almoxarifadoTransferenciaAutomatica)
			throws BaseException {
		getSceAlmoxarifadoTransferenciaAutomaticaDAO().persistir(almoxarifadoTransferenciaAutomatica);
		getSceAlmoxarifadoTransferenciaAutomaticaDAO().flush();
	}

	@Override
	public void atualizarSceAlmoxTransfAutomaticas(SceAlmoxarifadoTransferenciaAutomatica almoxarifadoTransferenciaAutomatica)
			throws BaseException {
		getSceAlmoxarifadoTransferenciaAutomaticaDAO().atualizar(almoxarifadoTransferenciaAutomatica);
		getSceAlmoxarifadoTransferenciaAutomaticaDAO().flush();
	}

	@Override
	public void excluirSceAlmoxTransfAutomaticas(SceAlmoxarifadoTransferenciaAutomatica almoxarifadoTransferenciaAutomatica)
			throws BaseException {
		almoxarifadoTransferenciaAutomatica = getSceAlmoxarifadoTransferenciaAutomaticaDAO().obterPorChavePrimaria(
				almoxarifadoTransferenciaAutomatica.getId());
		getSceAlmoxarifadoTransferenciaAutomaticaDAO().remover(almoxarifadoTransferenciaAutomatica);
		getSceAlmoxarifadoTransferenciaAutomaticaDAO().flush();
	}

	@Override
	public List<SceReqMaterial> pesquisarRequisicaoMaterialConsultaGeral(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, Boolean automatica, String nomeImpressora, Short almSeq,
			Integer ccReq, Integer ccApli, DominioImpresso impresso) {

		return getSceReqMateriaisDAO().pesquisarRequisicaoMaterialConsultaGeral(firstResult, maxResult, orderProperty, asc, nroRM,
				dtGeracao, dtConfirmacao, dtEfetivacao, indSituacao, indEstorno, automatica, nomeImpressora, almSeq, ccReq, ccApli,
				impresso);

	}

	@Override
	public List<SceReqMaterial> pesquisarRequisicaoMaterialConsultaRM(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, String nomeImpressora, Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso, List<FccCentroCustos> hierarquiaCc, List<SceAlmoxarifado> listaAlmox,
			List<DominioSituacaoRequisicaoMaterial> listaSituacao) {

		return getSceReqMateriaisDAO().pesquisarRequisicaoMaterialConsultaRM(firstResult, maxResult, orderProperty, asc, nroRM, dtGeracao,
				dtConfirmacao, dtEfetivacao, indSituacao, indEstorno, nomeImpressora, almSeq, ccReq, ccApli, impresso, hierarquiaCc,
				listaAlmox, listaSituacao);

	}

	@Override
	public List<String> confirmarDevolucaoFornecedor(List<PendenciasDevolucaoVO> listaPendencias, Integer seqDfe,
			Integer seqNotaRecebimento, Long numeroDfs, String serieDfs, Date dataDfs, String nomeMicroComputador) throws BaseException {
		return this.getConfirmacaoDevolucaoON().confirmarDevolucaoFornecedor(listaPendencias, seqDfe, seqNotaRecebimento, numeroDfs,
				serieDfs, dataDfs, nomeMicroComputador);
	}

	@Override
	public List<String> cancelarDevolucaoFornecedor(List<PendenciasDevolucaoVO> listaPendencias, Long numeroDfs, String serieDfs,
			Date dataDfs, String nomeMicroComputador, Integer seqNrp) throws BaseException {
		return this.getConfirmacaoDevolucaoON().cancelarDevolucaoFornecedor(listaPendencias, numeroDfs, serieDfs, dataDfs,
				nomeMicroComputador, seqNrp);
	}

	@Override
	public Long pesquisarRequisicaoMaterialConsultaGeralCount(Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, Boolean automatica, String nomeImpressora, Short almSeq,
			Integer ccReq, Integer ccApli, DominioImpresso impresso) {
		return getSceReqMateriaisDAO().pesquisarRequisicaoMaterialConsultaGeralCount(nroRM, dtGeracao, dtConfirmacao, dtEfetivacao,
				indSituacao, indEstorno, automatica, nomeImpressora, almSeq, ccReq, ccApli, impresso);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarRequisicaoMaterialConsultaRMCount(java.lang.Integer,
	 * java.util.Date, java.util.Date, java.util.Date,
	 * br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial,
	 * java.lang.Boolean, java.lang.String, java.lang.Short, java.lang.Integer,
	 * java.lang.Integer, br.gov.mec.aghu.dominio.DominioImpresso)
	 */
	@Override
	public Long pesquisarRequisicaoMaterialConsultaRMCount(Integer nroRM, Date dtGeracao, Date dtConfirmacao, Date dtEfetivacao,
			DominioSituacaoRequisicaoMaterial indSituacao, Boolean indEstorno, String nomeImpressora, Short almSeq, Integer ccReq,
			Integer ccApli, DominioImpresso impresso, List<FccCentroCustos> hierarquiaCc, List<SceAlmoxarifado> listaAlmox,
			List<DominioSituacaoRequisicaoMaterial> listaSituacao) {
		return getSceReqMateriaisDAO().pesquisarRequisicaoMaterialConsultaRMCount(nroRM, dtGeracao, dtConfirmacao, dtEfetivacao,
				indSituacao, indEstorno, nomeImpressora, almSeq, ccReq, ccApli, impresso, hierarquiaCc, listaAlmox, listaSituacao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarNomesImpressorasRequisicaoMaterial(java.lang.String)
	 */
	@Override
	public List<LinhaReportVO> pesquisarNomesImpressorasRequisicaoMaterial(String parametro) {
		return getSceReqMateriaisDAO().pesquisarNomesImpressorasRequisicaoMaterial(parametro);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao
	 * (java.lang.Object)
	 */
	@Override
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(String parametro,
			final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		return getManterRequisicaoMaterialON().pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(parametro, dataFimVinculoServidor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * buscarRelatorioMaterialEstocaveisCurvaAbc(boolean)
	 */
	@Override
	public List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisCurvaAbc(boolean considerarCompras) {
		return getRelatorioMateriaisEstocaveisGrupoCurvaAbcON().buscarRelatorioMaterialEstocaveisCurvaAbc(considerarCompras);
	}

	public RelatorioMateriaisEstocaveisGrupoCurvaAbcON getRelatorioMateriaisEstocaveisGrupoCurvaAbcON() {
		return relatorioMateriaisEstocaveisGrupoCurvaAbcON;
	}

	private PesquisaMovimentoMaterialON getPesquisaMovimentoMaterialON() {
		return pesquisaMovimentoMaterialON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(java.lang.Object)
	 */
	@Override
	public List<MovimentoMaterialVO> pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(String parametro)
			throws ApplicationBusinessException {
		return getPesquisaMovimentoMaterialON().pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(parametro);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisarMovimentosMaterial
	 * (br.gov.mec.aghu.model.SceTipoMovimento,
	 * br.gov.mec.aghu.model.ScoMaterial, java.util.Date,
	 * br.gov.mec.aghu.model.SceAlmoxarifado,
	 * br.gov.mec.aghu.model.FccCentroCustos,
	 * br.gov.mec.aghu.dominio.DominioSimNao,
	 * br.gov.mec.aghu.model.ScoFornecedor,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial,
	 * br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia,
	 * java.lang.Integer, java.lang.Integer, java.lang.Integer,
	 * java.lang.String, boolean)
	 */
	@Override
	public List<MovimentoMaterialVO> pesquisarMovimentosMaterial(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao,
			SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor,
			SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer nroDocGeracao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException {
		return getPesquisaMovimentoMaterialON().pesquisarMovimentosMaterial(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto,
				indEstorno, fornecedor, movimentoMaterialDataCompetencia, comparacaoDataCompetencia, nroDocGeracao, firstResult, maxResult,
				orderProperty, asc);
	}

	public Long pesquisarMovimentosMaterialCount(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao,
			SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor,
			SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia,
			Integer nroDocGeracao) {
		return getSceMovimentoMaterialDAO().pesquisarMovimentosMaterialCount(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto,
				indEstorno, fornecedor, movimentoMaterialDataCompetencia, comparacaoDataCompetencia, nroDocGeracao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * validaCamposPesquisaMovimentoMaterial
	 * (br.gov.mec.aghu.model.SceTipoMovimento,
	 * br.gov.mec.aghu.model.ScoMaterial, java.util.Date,
	 * br.gov.mec.aghu.model.SceAlmoxarifado,
	 * br.gov.mec.aghu.model.FccCentroCustos,
	 * br.gov.mec.aghu.dominio.DominioSimNao,
	 * br.gov.mec.aghu.model.ScoFornecedor,
	 * br.gov.mec.aghu.model.SceMovimentoMaterial, java.lang.Integer)
	 */
	@Override
	public void validaCamposPesquisaMovimentoMaterial(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao,
			SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor,
			SceMovimentoMaterial movimentoMaterialDataCompetencia, Integer nroDocGeracao) throws ApplicationBusinessException {
		getPesquisaMovimentoMaterialON().validaCampos(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto, indEstorno,
				fornecedor, movimentoMaterialDataCompetencia, nroDocGeracao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDadosRelatorioMensalMaterial(java.util.Date)
	 */
	@Override
	public List<RelatorioMensalMaterialVO> pesquisarDadosRelatorioMensalMaterial(Date dataCompetencia, DominioAgruparRelMensal agrupar)
			throws BaseException {
		return getGerarRelatorioMensalMaterialON().pesquisarDadosRelatorioMensalMaterial(dataCompetencia, agrupar);
	}

	private GerarRelatorioMensalMaterialON getGerarRelatorioMensalMaterialON() {
		return gerarRelatorioMensalMaterialON;
	}

	private GerarRelatorioMateriaisComSaldoAteVinteDiasON getGerarRelatorioMateriaisComSaldoAteVinteDiasON() {
		return gerarRelatorioMateriaisComSaldoAteVinteDiasON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarCSVRelatorioMateriaisComSaldoAteVinteDias(java.util.List)
	 */
	@Override
	public String gerarCSVRelatorioMateriaisComSaldoAteVinteDias(List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> dados)
			throws ApplicationBusinessException, IOException {
		return getGerarRelatorioMateriaisComSaldoAteVinteDiasON().gerarCsvRelatorioMateriaisComSaldoAteVinteDias(dados);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarMateriaisComSaldoAteVinteDias()
	 */
	@Override
	public List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarMateriaisComSaldoAteVinteDias(Integer duracaoEstoque,
			Double percAjuste) throws ApplicationBusinessException {
		return getGerarRelatorioMateriaisComSaldoAteVinteDiasON().pesquisarMateriaisComSaldoAteVinteDias(duracaoEstoque, percAjuste);
	}

	@Override
	public EntradaMateriasDiaVO pesquisarEntradaMateriasDia(Date dataGeracao) throws ApplicationBusinessException{

		return movimentoMaterialON.entradaMateriasDia(dataGeracao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * efetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias
	 * (java.lang.String)
	 */
	@Override
	public String efetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(String fileName) {
		return getGerarRelatorioMateriaisComSaldoAteVinteDiasON().efetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterPacoteMateriais(
	 * br.gov.mec.aghu.model.ScePacoteMateriaisId)
	 */
	@Override
	public ScePacoteMateriais obterPacoteMateriais(ScePacoteMateriaisId idPacoteMateriais) {
		return getScePacoteMateriaisDAO().obterPorChavePrimaria(idPacoteMateriais);
	}

	/*
	 * Gerar Fechamento Mensal de Estoque
	 */

	@Override
	public void gerarFechamentoEstoqueMensalManual() throws BaseException {
		this.getGerarFechamentoEstoqueMensalRN().gerarFechamentoEstoqueMensalManual();
	}

	@Override
	public void efetivarRequisicaoMaterialAutomatica(Date date, String cron, String nomeMicrocomputador) throws ApplicationBusinessException {
		efetivarRequisicaoMaterialJobON.efetivarRequisicaoMaterialAutomatica (nomeMicrocomputador); 
	}
	
	public GerarFechamentoEstoqueMensalRN getGerarFechamentoEstoqueMensalRN() {
		return gerarFechamentoEstoqueMensalRN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarMateriaisPorTransferencia(java.lang.Short, java.lang.Short,
	 * java.lang.Integer, java.lang.Object)
	 */
	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarMateriaisPorTransferencia(Short almoxSeq, Short almoxSeqReceb, Integer frnNumero,
			String paramPesq) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarMateriaisPorTransferencia(almoxSeq, almoxSeqReceb, frnNumero, paramPesq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarMateriaisPorTransferenciaCount(java.lang.Short, java.lang.Short,
	 * java.lang.Integer, java.lang.Object)
	 */
	@Override
	public Long pesquisarMateriaisPorTransferenciaCount(Short almoxSeq, Short almoxSeqReceb, Integer frnNumero, String paramPesq) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarMateriaisPorTransferenciaCount(almoxSeq, almoxSeqReceb, frnNumero, paramPesq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserir(br.gov.mec.aghu
	 * .model.SceItemTransferencia)
	 */
	@Override
	public void inserir(SceItemTransferencia itemTransferencia) throws BaseException {
		getSceItemTransferenciaRN().inserir(itemTransferencia);
	}

	protected SceItemTransferenciaON getSceItemTransferenciaON() {
		return sceItemTransferenciaON;
	}

	protected DispensacaoMedicamentoON getDispensacaoMedicamentoON() {
		return dispensacaoMedicamentoON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterEstoqueAlmoxarifadoOrigem(java.lang.Short, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoOrigem(Short almSeq, Integer matCodigo, Integer frnNumero)
			throws ApplicationBusinessException {
		return getSceItemTransferenciaON().obterEstoqueAlmoxarifadoOrigem(almSeq, matCodigo, frnNumero);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarItensRequisicaoMateriais(java.lang.Integer)
	 */
	@Override
	public List<SceItemRms> pesquisarItensRequisicaoMateriais(Integer seqRequisicaoMaterial) {
		return getSceItemRmsDAO().pesquisarItensRequisicaoMateriais(seqRequisicaoMaterial);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarEstoqueAlmoxarifadoValidadeMaterialCount
	 * (br.gov.mec.aghu.model.SceEstoqueAlmoxarifado)
	 */
	@Override
	public Long pesquisarEstoqueAlmoxarifadoValidadeMaterialCount(SceEstoqueAlmoxarifado estoqueAlmox) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoValidadeMaterialCount(estoqueAlmox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarEstoqueAlmoxarifadoValidadeMaterial(java.lang.Integer,
	 * java.lang.Integer, java.lang.String, boolean,
	 * br.gov.mec.aghu.model.SceEstoqueAlmoxarifado)
	 */
	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoValidadeMaterial(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, SceEstoqueAlmoxarifado estoqueAlmox) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoValidadeMaterial(firstResult, maxResult, orderProperty, asc,
				estoqueAlmox);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * tratarDispensacaoMedicamentoEstoque
	 * (br.gov.mec.aghu.model.AfaDispensacaoMdtos, java.lang.String)
	 */
	@Override
	public void tratarDispensacaoMedicamentoEstoque(AfaDispensacaoMdtos dispMdtoNew, String etiqueta, String nomeMicrocomputador)
			throws BaseException {
		getDispensacaoMedicamentoON().tratarDispensacaoMedicamentoEstoque(dispMdtoNew, etiqueta, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterQtdeDispByUnfAndMaterial
	 * (java.lang.Short, java.lang.Integer)
	 */
	@Override
	public Long obterQtdeDispByUnfAndMaterial(Short seqUnf, Integer matCodigo) {
		return getSceEstoqueAlmoxarifadoDAO().obterQtdeDispByUnfAndMaterial(seqUnf, matCodigo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#listarAlmoxarifados(java
	 * .lang.Integer, java.lang.Integer, java.lang.String, boolean,
	 * java.lang.Short, java.lang.String, java.lang.Integer, java.lang.Integer,
	 * java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 * br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public List<SceAlmoxarifado> listarAlmoxarifados(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short codigo, String descricao, Integer codigoCentroCustos, Integer diasEstoqueMinimo, Boolean central,
			Boolean calculaMediaPonderada, Boolean bloqueiaEntTransf, DominioSituacao situacao) {

		return getSceAlmoxarifadoDAO().listarAlmoxarifados(firstResult, maxResult, orderProperty, asc, codigo, descricao,
				codigoCentroCustos, diasEstoqueMinimo, central, calculaMediaPonderada, bloqueiaEntTransf, situacao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#listarAlmoxarifadosCount
	 * (java.lang.Short, java.lang.String, java.lang.Integer, java.lang.Integer,
	 * java.lang.Boolean, java.lang.Boolean, java.lang.Boolean,
	 * br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	public Long listarAlmoxarifadosCount(Short codigo, String descricao, Integer codigoCentroCustos, Integer diasEstoqueMinimo,
			Boolean central, Boolean calculaMediaPonderada, Boolean bloqueiaEntTransf, DominioSituacao situacao) {

		return getSceAlmoxarifadoDAO().listarAlmoxarifadosCount(codigo, descricao, codigoCentroCustos, diasEstoqueMinimo, central,
				calculaMediaPonderada, bloqueiaEntTransf, situacao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#gravarAlmoxarifado(br
	 * .gov.mec.aghu.model.SceAlmoxarifado,
	 * br.gov.mec.aghu.model.SceAlmoxarifado)
	 */
	@Override
	public void gravarAlmoxarifado(SceAlmoxarifado alterado) throws ApplicationBusinessException {
		getManterAlmoxarifadoON().persist(alterado);
	}

	@Override
	public void atualizarProcessoAlmoxarifado(Short seq, String processo) throws ApplicationBusinessException {
		getManterAlmoxarifadoON().atualizarProcessoAlmoxarifado(seq, processo);
	}

	private ManterAlmoxarifadoON getManterAlmoxarifadoON() {
		return manterAlmoxarifadoON;
	}

	protected DevolucaoAlmoxarifadoRN getDevolucaoAlmoxarifadoRN() {
		return devolucaoAlmoxarifadoRN;
	}

	protected ItemDevolucaoAlmoxarifadoRN getItemDevolucaoAlmoxarifadoRN() {
		return itemDevolucaoAlmoxarifadoRN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#isAlmoxarifadoAlterado
	 * (br.gov.mec.aghu.model.SceAlmoxarifado,
	 * br.gov.mec.aghu.model.SceAlmoxarifado)
	 */
	@Override
	public Boolean isAlmoxarifadoAlterado(SceAlmoxarifado alterado) {
		return getSceAlmoxarifadoRN().isAlmoxarifadoAlterado(alterado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDevolucaoAlmoxarifado(java.lang.Integer, java.lang.Short,
	 * java.lang.Integer, java.lang.Boolean, java.lang.Boolean, java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public List<SceDevolucaoAlmoxarifado> pesquisarDevolucaoAlmoxarifado(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo,
			Boolean estornada, Boolean pesquisaEstorno, Date dtInicio, Date dtFim) {
		return this.getSceDevolucaoAlmoxarifadoDAO().pesquisarDevolucaoAlmoxarifado(numeroDa, almoxarifadoSeq, cctCodigo, estornada,
				pesquisaEstorno, dtInicio, dtFim);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDevolucaoAlmoxarifado(java.lang.Integer, java.lang.Integer,
	 * java.lang.String, boolean, java.lang.Integer, java.lang.Short,
	 * java.lang.Integer, java.lang.Boolean, java.lang.Boolean, java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public List<SceDevolucaoAlmoxarifado> pesquisarDevolucaoAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, Boolean estorno, Boolean pesquisaEstorno,
			Date dtInicio, Date dtFim) {
		return this.getSceDevolucaoAlmoxarifadoDAO().pesquisarDevolucaoAlmoxarifado(firstResult, maxResult, orderProperty, asc, numeroDa,
				almoxarifadoSeq, cctCodigo, estorno, pesquisaEstorno, dtInicio, dtFim);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarDevolucaoAlmoxarifadoCount(java.lang.Integer, java.lang.Short,
	 * java.lang.Integer, java.lang.Boolean, java.lang.Boolean, java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Long pesquisarDevolucaoAlmoxarifadoCount(Integer numeroDa, Short almoxarifadoSeq, Integer cctCodigo, Boolean estorno,
			Boolean pesquisaEstorno, Date dtInicio, Date dtFim) {
		return this.getSceDevolucaoAlmoxarifadoDAO().pesquisarDevolucaoAlmoxarifadoCount(numeroDa, almoxarifadoSeq, cctCodigo, estorno,
				pesquisaEstorno, dtInicio, dtFim);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarItensComMaterialDevolucaoAlmoxarifado(java.lang.Integer)
	 */
	@Override
	public List<ItemDevolucaoAlmoxarifadoVO> pesquisarItensComMaterialDevolucaoAlmoxarifado(Integer numeroDaSelecionado) {
		return this.getDevolucaoAlmoxarifadoON().pesquisarItensComMaterialPorDevolucaoAlmoxarifado(numeroDaSelecionado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarItensComMaterialLoteDocumentosPorDevolucaoAlmoxarifado
	 * (java.lang.Integer)
	 */
	@Override
	public List<ItemDevolucaoAlmoxarifadoVO> pesquisarItensComMaterialLoteDocumentosPorDevolucaoAlmoxarifado(Integer dalSeq) {
		return this.getDevolucaoAlmoxarifadoON().pesquisarItensComMaterialLoteDocumentosPorDalSeq(dalSeq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#estornarDevolucaoAlmoxarifado
	 * (br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado)
	 */
	@Override
	public void estornarDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador)
			throws BaseException {
		this.getDevolucaoAlmoxarifadoON().estornarDevolucaoAlmoxarifado(devolucaoAlmoxarifado, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarDadosRelatorioDevolucaoAlmoxarifado(java.lang.Integer)
	 */
	@Override
	public List<RelatorioDevolucaoAlmoxarifadoVO> gerarDadosRelatorioDevolucaoAlmoxarifado(Integer numeroDevAlmox)
			throws ApplicationBusinessException {
		return getRelatorioDevolucaoAlmoxarifadoRN().gerarDadosRelatorioDevolucaoAlmoxarifado(numeroDevAlmox);
	}

	protected RelatorioDevolucaoAlmoxarifadoRN getRelatorioDevolucaoAlmoxarifadoRN() {
		return relatorioDevolucaoAlmoxarifadoRN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * gerarCsvRelatorioDevolucaoAlmoxarifado(java.lang.Integer, java.util.List,
	 * java.util.Map)
	 */
	@Override
	public String gerarCsvRelatorioDevolucaoAlmoxarifado(Integer numeroDevolAlmox, List<RelatorioDevolucaoAlmoxarifadoVO> dados,
			Map<String, Object> parametros) throws FileNotFoundException, IOException {
		return getRelatorioDevolucaoAlmoxarifadoRN().gerarCsvRelatorioDevolucaoAlmoxarifado(numeroDevolAlmox, dados, parametros);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * getLoteDocImpressaoByNroEtiqueta(java.lang.String)
	 */
	@Override
	public SceLoteDocImpressao getLoteDocImpressaoByNroEtiqueta(String etiqueta) throws ApplicationBusinessException {
		return getSceLoteDocumentoRN().getLoteDocImpressaoByNroEtiqueta(etiqueta);
	}

	protected SceLoteDocImpressaoDAO getSceLoteDocImpressaoDAO() {
		return sceLoteDocImpressaoDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#pesquisarPorDadosInformados
	 * (br.gov.mec.aghu.model.SceLoteDocImpressao)
	 */
	@Override
	public List<SceLoteDocImpressao> pesquisarPorDadosInformados(SceLoteDocImpressao entidade) {
		return getSceLoteDocImpressaoDAO().pesquisarPorDadosInformados(entidade);
	}

	@Override
	public List<SceLoteDocImpressao> pesquisarLoteDocImpSemLoteDocumento(SceLoteDocImpressao entidade) {
		return getSceLoteDocImpressaoDAO().pesquisarLoteDocImpSemLoteDocumento(entidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#getCustoMedioPonderado
	 * (br.gov.mec.aghu.model.ScoMaterial, java.util.Date, java.lang.Integer)
	 */
	@Override
	public Double getCustoMedioPonderado(Integer matCodigo, Date dataCompetencia, Integer frnNumero) {
		return getSceEstoqueGeralDAO().getCustoMedioPonderado(matCodigo, dataCompetencia, frnNumero);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(java.lang.Object)
	 */
	@Override
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(Object param) {
		return this.getSceAlmoxarifadoDAO().pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterDevolucaoAlmoxarifadoPorChavePrimaria(java.lang.Object)
	 */
	@Override
	public SceDevolucaoAlmoxarifado obterDevolucaoAlmoxarifadoPorChavePrimaria(Object chavePrimaria) {
		return getSceDevolucaoAlmoxarifadoDAO().obterPorChavePrimaria(chavePrimaria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterDevolucaoAlmoxarifadoOriginal
	 * (br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado)
	 */
	@Override
	public SceDevolucaoAlmoxarifado obterDevolucaoAlmoxarifadoOriginal(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) {
		return getSceDevolucaoAlmoxarifadoDAO().obterOriginal(devolucaoAlmoxarifado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserirDevolucaoAlmoxarifado
	 * (br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado)
	 */
	@Override
	public void inserirDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) throws ApplicationBusinessException {
		this.getDevolucaoAlmoxarifadoRN().persistirDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * inserirItemDevolucaoAlmoxarifado(br.gov.mec.aghu.model.SceItemDas)
	 */
	@Override
	public void inserirItemDevolucaoAlmoxarifado(SceItemDas itemDevolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException {
		this.getItemDevolucaoAlmoxarifadoRN().persistirItemDevolucaoAlmoxarifado(itemDevolucaoAlmoxarifado, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * habilitarCampoDataFinalConsultarNotaRecebimento(java.util.Date)
	 */
	@Override
	public Boolean habilitarCampoDataFinalConsultarNotaRecebimento(Date dataSituacao) {
		return getSceNotaRecebimentoRN().habilitarCampoDataFinalConsultarNotaRecebimento(dataSituacao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificaDataSituacaoInicialDataSituacaoFinal(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public void verificaDataSituacaoInicialDataSituacaoFinal(Date dataSituacao, Date dataFinal) throws ApplicationBusinessException {
		getSceNotaRecebimentoRN().verificaDataSituacaoInicialDataSituacaoFinal(dataSituacao, dataFinal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificarValidadeItemDaPersistido
	 * (br.gov.mec.aghu.model.SceDocumentoValidade)
	 */
	@Override
	public Boolean verificarValidadeItemDaPersistido(SceDocumentoValidade docValidade) {
		return getDevolucaoAlmoxarifadoON().verificarValidadeItemDaPersistido(docValidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * persistirDevolucaoAlmoxarifado
	 * (br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado, java.util.List)
	 */
	@Override
	public void persistirDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoNew,
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO, String nomeMicrocomputador) throws BaseException {
		getDevolucaoAlmoxarifadoON().persistirDevolucaoAlmoxarifado(devolucaoAlmoxarifadoNew, listaItemDevolucaoAlmoxarifadoVO,
				nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificarQuantidadeValidadesContraItemDa
	 * (br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO,
	 * br.gov.mec.aghu.model.SceDocumentoValidade)
	 */
	@Override
	public void verificarQuantidadeValidadesContraItemDa(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO,
			SceDocumentoValidade novaValidade) throws ApplicationBusinessException {
		getDevolucaoAlmoxarifadoON().verificarQuantidadeValidadesContraItemDa(itemDevolucaoAlmoxarifadoVO, novaValidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificarQuantidadeDocumentoValidade
	 * (br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO,
	 * br.gov.mec.aghu.model.SceDocumentoValidade)
	 */
	@Override
	public void verificarQuantidadeDocumentoValidade(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO,
			SceDocumentoValidade novaValidade) throws ApplicationBusinessException {
		getDevolucaoAlmoxarifadoON().verificarQuantidadeDocumentoValidade(itemDevolucaoAlmoxarifadoVO, novaValidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificarQuantidadeLoteContraQuantidadeValidade
	 * (br.gov.mec.aghu.model.SceLoteDocumento,
	 * br.gov.mec.aghu.model.SceDocumentoValidade)
	 */
	@Override
	public void verificarQuantidadeLoteContraQuantidadeValidade(SceLoteDocumento lote, SceDocumentoValidade validade)
			throws ApplicationBusinessException {
		getDevolucaoAlmoxarifadoON().verificarQuantidadeLoteContraQuantidadeValidade(lote, validade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * verificarValidadeDataDuplicada(java.util.List,
	 * br.gov.mec.aghu.model.SceDocumentoValidade)
	 */
	@Override
	public void verificarValidadeDataDuplicada(List<SceDocumentoValidade> listaDocumentoValidade, SceDocumentoValidade novaValidade)
			throws ApplicationBusinessException {
		getDevolucaoAlmoxarifadoON().verificarValidadeDataDuplicada(listaDocumentoValidade, novaValidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#verificarItemDaDuplicado
	 * (br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO, java.util.List)
	 */
	@Override
	public void verificarItemDaDuplicado(ItemDevolucaoAlmoxarifadoVO novoItemDevolucaoAlmoxarifadoVO,
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO) throws ApplicationBusinessException {
		getDevolucaoAlmoxarifadoON().verificarItemDaDuplicado(novoItemDevolucaoAlmoxarifadoVO, listaItemDevolucaoAlmoxarifadoVO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#listarAtendimentosPaciente
	 * (java.lang.Integer)
	 */
	@Override
	public List<AghAtendimentosVO> listarAtendimentosPaciente(Integer pacCodigo) throws ApplicationBusinessException {
		return this.getGerarRequisicaoMaterialON().listarAtendimentosPaciente(pacCodigo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterSceItemRmpsPorChavePrimaria(br.gov.mec.aghu.model.SceItemRmpsId)
	 */
	@Override
	public SceItemRmps obterSceItemRmpsPorChavePrimaria(final SceItemRmpsId chavePrimaria) {
		return getSceItemRmpsDAO().obterPorChavePrimaria(chavePrimaria);
	}

	protected SceLoteDocumentoON getSceLoteDocumentoON() {
		return sceLoteDocumentoON;
	}

	protected ImprimirEtiquetasUnitarizacaoON getImprimirEtiquetasUnitarizacaoON() {
		return imprimirEtiquetasUnitarizacaoON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#buscarItemRmpsPorRmpSeq
	 * (java.lang.Integer)
	 */
	@Override
	public SceItemRmps buscarItemRmpsPorRmpSeq(final Integer rmpSeq) {
		return getSceItemRmpsDAO().buscarItemRmpsPorRmpSeq(rmpSeq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * obterSceRmrPacientePorChavePrimaria(java.lang.Integer)
	 */
	@Override
	public SceRmrPaciente obterSceRmrPacientePorChavePrimaria(final Integer seq) {
		return this.getSceRmrPacienteDAO().obterPorChavePrimaria(seq);
	}

	protected SceItemRmpsDAO getSceItemRmpsDAO() {
		return sceItemRmpsDAO;
	}

	protected SceRmrPacienteDAO getSceRmrPacienteDAO() {
		return sceRmrPacienteDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#removerItemPacoteMaterial
	 * (br.gov.mec.aghu.model.SceItemPacoteMateriais)
	 */
	@Override
	public void removerItemPacoteMaterial(SceItemPacoteMateriais item) {
		getSceItemPacoteMateriaisDAO().remover(getSceItemPacoteMateriaisDAO().merge(item));
        getSceItemPacoteMateriaisDAO().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * inserirTransferenciaMaterialEventual
	 * (br.gov.mec.aghu.model.SceTransferencia)
	 */
	@Override
	public void inserirTransferenciaMaterialEventual(SceTransferencia transferencia) throws BaseException {
		this.getSceTransferenciaRN().inserirTrasnferenciaEventual(transferencia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarListaItensTransferenciaEventual
	 * (br.gov.mec.aghu.model.SceTransferencia)
	 */
	@Override
	public List<SceItemTransferencia> pesquisarListaItensTransferenciaEventual(SceTransferencia transferencia) {
		return this.getSceItemTransferenciaDAO().pesquisarListaItensTransferenciaEventual(transferencia.getSeq());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#atualizarItemTransfEventual
	 * (br.gov.mec.aghu.model.SceItemTransferencia)
	 */
	@Override
	public void atualizarItemTransfEventual(SceItemTransferencia itemTransferencia, String nomeMicrocomputador) throws BaseException {
		this.getSceItemTransferenciaRN().atualizar(itemTransferencia, nomeMicrocomputador);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarItensTrsEventualPacoteMateriaisVO
	 * (br.gov.mec.aghu.model.ScePacoteMateriais, java.lang.Short)
	 */
	@Override
	public List<ItemPacoteMateriaisVO> pesquisarItensTrsEventualPacoteMateriaisVO(ScePacoteMateriais pacote, Short almSeq) {
		return this.getSceItemPacoteMateriaisDAO().pesquisarItensTrsEventualPacoteMateriaisVO(pacote, almSeq);
	}

	protected SceItemPacoteMateriaisDAO getSceItemPacoteMateriaisDAO() {
		return sceItemPacoteMateriaisDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * inserirItemTransferenciaEventual(java.util.List,
	 * br.gov.mec.aghu.model.SceTransferencia)
	 */
	@Override
	public void inserirItemTransferenciaEventual(List<ItemPacoteMateriaisVO> listItemPacoteMateriaisVO, SceTransferencia transferencia)
			throws BaseException {
		getSceItemTransferenciaRN().inserirItemTransferenciaEventual(listItemPacoteMateriaisVO, transferencia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarTransferenciaMateriaisEventualCount
	 * (br.gov.mec.aghu.model.SceTransferencia)
	 */
	@Override
	public Long pesquisarTransferenciaMateriaisEventualCount(SceTransferencia transferencia) {
		return this.getSceTransferenciaDAO().pesquisarTransferenciaMateriaisEventualCount(transferencia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarTransferenciaMateriaisEventual(java.lang.Integer,
	 * java.lang.Integer, java.lang.String, boolean,
	 * br.gov.mec.aghu.model.SceTransferencia)
	 */
	@Override
	public List<SceTransferencia> pesquisarTransferenciaMateriaisEventual(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceTransferencia transferencia) {
		return getSceTransferenciaDAO().pesquisarTransferenciaMateriaisEventual(firstResult, maxResult, orderProperty, asc, transferencia);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#inserirMovimentoMaterial
	 * (br.gov.mec.aghu.model.SceMovimentoMaterial)
	 */
	@Override
	public void inserirMovimentoMaterial(SceMovimentoMaterial elemento, String nomeMicrocomputador) throws BaseException {
		this.getSceMovimentoMaterialRN().inserir(elemento, nomeMicrocomputador, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * recupararQuantidadeDisponivelTodosEstoques
	 * (br.gov.mec.aghu.estoque.vo.EstoqueAlmoxarifadoVO)
	 */
	@Override
	public Integer recupararQuantidadeDisponivelTodosEstoques(EstoqueAlmoxarifadoVO estoque) {
		return getPesquisarEstoqueAlmoxarifadoON().recupararQuantidadeDisponivelTodosEstoques(estoque);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#getValorUnitarioLiquido
	 * (java.lang.Double, java.lang.Integer, java.lang.Double, java.lang.Double,
	 * java.lang.Double, java.lang.Double, java.lang.Double)
	 */
	@Override
	public Double getValorUnitarioLiquido(Double valorUnitarioItemAutorizacaoFornecimento, Integer quantidadeRecebida,
			Double percAcrescimo, Double percAcrescimoItem, Double percDesconto, Double percDescontoItem, Double percIpi) {
		return getSceItemNotaRecebimentoRN().getValorUnitarioLiquido(valorUnitarioItemAutorizacaoFornecimento, quantidadeRecebida,
				percAcrescimo, percAcrescimoItem, percDesconto, percDescontoItem, percIpi);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarHistoricoFechamentoMensalPorDataCompetencia(java.util.Date)
	 */
	@Override
	public List<SceHistoricoFechamentoMensal> pesquisarHistoricoFechamentoMensalPorDataCompetencia(Date dataCompetencia) {
		return getSceHistoricoFechamentoMensalDAO().pesquisarHistoricoFechamentoMensalPorDataCompetencia(dataCompetencia);
	}

	protected SceHistoricoFechamentoMensalDAO getSceHistoricoFechamentoMensalDAO() {
		return sceHistoricoFechamentoMensalDAO;
	}

	@Override
	public SceAlmoxarifado obterAlmoxarifadoPorSeq(Short seq) {
		return this.getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeq(seq);
	}

	@Override
	public Double buscarUltimoCustoEntradaPorMaterialTipoMov(ScoMaterial matCodigo, Short shortValue) {
		return this.getSceMovimentoMaterialDAO().buscarUltimoCustoEntradaPorMaterialTipoMov(matCodigo, shortValue);
	}

	@Override
	public Integer obterConsumoTotalNosUltimosSeisMeses(Integer codigoMat) {
		return this.getSceConsumoTotalMateriaisDAO().obterConsumoTotalNosUltimosSeisMeses(codigoMat);
	}

	@Override
	public Integer obterConsumoTotalNosUltimosSeisMesesPeloMedicamento(Integer codigoMat) throws BaseException {
		return this.getSceConsumoTotalMateriaisDAO().obterConsumoTotalNosUltimosSeisMesesPeloMedicamento(codigoMat);
	}

	protected SceConsumoTotalMateriaisDAO getSceConsumoTotalMateriaisDAO() {
		return sceConsumoTotalMateriaisDAO;
	}

	@Override
	public List<SceTipoMovimento> obterTipoMovimentoPorSigla(Object param) {
		return getSceTipoMovimentosDAO().obterTipoMovimentoPorSigla(param);
	}
	
	@Override
	public SceTipoMovimento obterTipoMovimentoPorChavePrimaria(Object param) {
		return getSceTipoMovimentosDAO().obterPorChavePrimaria(param);
	}

	@Override
	public SceReqMaterialRetornos obterSceReqMaterialRetornosPorChavePrimaria(Integer seq) {
		return this.getSceReqMaterialRetornosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<SceConversaoUnidadeConsumos> listarConversaoUnidadeConsumo(Integer codigo) {
		return this.getSceConversaoUnidadeConsumosDAO().listarConversaoUnidadeConsumo(codigo);
	}

	public List<SceConversaoUnidadeConsumos> listarConversaoUnidadeConsumoFiltro(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, ScoMaterial material, ScoUnidadeMedida unidadeMedida, BigDecimal fatorConversao) {
		return this.getSceConversaoUnidadeConsumosDAO().listarConversaoUnidadeConsumoFiltro(firstResult, maxResult, orderProperty, asc,
				material, unidadeMedida, fatorConversao);
	}

	public Long listarConversaoUnidadeConsumoFiltroCount(ScoMaterial material, ScoUnidadeMedida unidadeMedida, BigDecimal fatorConversao) {
		return this.getSceConversaoUnidadeConsumosDAO().listarConversaoUnidadeConsumoFiltroCount(material, unidadeMedida, fatorConversao);
	}

	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void persistirConversao(SceConversaoUnidadeConsumos conversao) throws ApplicationBusinessException {
		this.getSceConversaoUnidadeConsumosRN().persistir(conversao);
	}

	@Override
	public Boolean verificarRecebimentoServico(SceNotaRecebProvisorio notaRecebProv) {
		return this.getConfirmacaoRecebimentoON().verificarRecebimentoServico(notaRecebProv);
	}

	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void atualizarConversao(SceConversaoUnidadeConsumos conversao) {
		this.getSceConversaoUnidadeConsumosRN().atualizar(conversao);
	}

	@Override
	@Secure("#{s:hasPermission('equipamentoCirurgicoCadastro','executar')}")
	public void excluirConversao(SceConversaoUnidadeConsumosId conversaoId) {
		this.getSceConversaoUnidadeConsumosRN().excluir(conversaoId);
	}

	@Override
	public SceConversaoUnidadeConsumos obterConversaoUnidadeConsumos(SceConversaoUnidadeConsumosId id) {
		return this.getSceConversaoUnidadeConsumosDAO().obterPorChavePrimaria(id);
	}

	protected SceReqMaterialRetornosDAO getSceReqMaterialRetornosDAO() {
		return sceReqMaterialRetornosDAO;
	}

	protected SceConversaoUnidadeConsumosDAO getSceConversaoUnidadeConsumosDAO() {
		return sceConversaoUnidadeConsumosDAO;
	}

	protected SceConversaoUnidadeConsumosRN getSceConversaoUnidadeConsumosRN() {
		return sceConversaoUnidadeConsumosRN;
	}

	@Override
	public SceEstoqueAlmoxarifado obterSceEstoqueAlmoxarifadoPorChavePrimaria(Integer seq) {
		return this.getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado(Integer fornecedorNumero, Integer ealSeq) {
		return this.getSceEstoqueAlmoxarifadoDAO().listaEstoqueAlmoxarifado(fornecedorNumero, ealSeq);
	}

	@Override
	public void persistirSceMovimentoMaterial(SceMovimentoMaterial sceMovimentoMaterial) {
		this.getSceMovimentoMaterialDAO().persistir(sceMovimentoMaterial);
	}

	@Override
	public List<SceRmrPaciente> listarRmrPacientesPorSeqNfGeralNaoNula(Integer seq) {
		return this.getSceRmrPacienteDAO().listarRmrPacientesPorSeqNfGeralNaoNula(seq);
	}

	@Override
	public List<SceRmrPacienteVO> listarRmrPacientePorRmpSeqENumero(Integer rmpSeq, Short numero) {
		return this.getSceRmrPacienteDAO().listarRmrPacientePorRmpSeqENumero(rmpSeq, numero);
	}

	@Override
	public List<SceRmrPacienteVO> listarSceRmrPacienteVOPorCirurgiaESituacao(final Integer crgSeq, final DominioSituacao situacao) {
		return this.getSceRmrPacienteDAO().listarSceRmrPacienteVOPorCirurgiaESituacao(crgSeq, situacao);
	}

	@Override
	public List<SceItemRmps> listarItensRmpsPorRmpSeqNfNaoNula(Integer rmpSeq) {
		return this.getSceItemRmpsDAO().listarItensRmpsPorRmpSeqNfNaoNula(rmpSeq);
	}

	@Override
	public Short obterProximoNumero(Integer seq) {
		return this.getSceItemRmpsDAO().obterProximoNumero(seq);
	}

	@Override
	public SceItemRmps inserirSceItemRmps(SceItemRmps sceItemRmps, boolean flush) {
		this.getSceItemRmpsDAO().persistir(sceItemRmps);
		if (flush) {
			this.getSceItemRmpsDAO().flush();
		}
		return sceItemRmps;
	}

	@Override
	public SceItemRmps atualizarSceItemRmps(SceItemRmps sceItemRmps, boolean flush) {
		SceItemRmps retorno = this.getSceItemRmpsDAO().atualizar(sceItemRmps);
		if (flush) {
			this.getSceItemRmpsDAO().flush();
		}
		return retorno;
	}

	@Override
	public SceFornecedorMaterial obterFornecedorMaterialPorFornecedorNumeroEMaterialCodigo(Integer frnNumero, Integer matCodigo) {
		return this.getSceFornecedorMaterialDAO().obterFornecedorMaterialPorFornecedorNumeroEMaterialCodigo(frnNumero, matCodigo);
	}

	@Override
	public List<SceRefCodes> buscarSceRefCodesPorTipoOperConversao(String valor, String dominio) {
		return this.getSceRefCodesDAO().buscarSceRefCodesPorTipoOperConversao(valor, dominio);
	}

	@Override
	public void inserirSceFornecedorMaterial(SceFornecedorMaterial sceFornecedorMaterial, boolean flush) {
		this.getSceFornecedorMaterialDAO().persistir(sceFornecedorMaterial);
		if (flush) {
			this.getSceFornecedorMaterialDAO().flush();
		}
	}

	@Override
	public void atualizarSceFornecedorMaterial(SceFornecedorMaterial sceFornecedorMaterial, boolean flush) {
		this.getSceFornecedorMaterialDAO().atualizar(sceFornecedorMaterial);
		if (flush) {
			this.getSceFornecedorMaterialDAO().flush();
		}
	}

	@Override
	public void efetuarInclusao(SceLoteDocImpressao entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			Boolean comReducao) throws IllegalStateException, BaseException {
		this.getImprimirEtiquetasExtrasON().inserir(entidade, nomeMicrocomputador, dataFimVinculoServidor, comReducao);
	}

	@Override
	public List<String> obterRazoesExcessaoEtiquetasExtras() {
		return this.getImprimirEtiquetasExtrasON().getRazaoExecessao();
	}

	@Override
	public List<SceLoteDocImpressao> efetuarPesquisaUnitarizacaoDeMedicamentosComEtiqueta(SceLoteDocImpressao entidadePesquisa,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getImprimirEtiquetasExtrasON().efetuarPesquisaUnitarizacaoDeMedicamentosComEtiqueta(entidadePesquisa, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long efetuarPesquisaUnitarizacaoDeMedicamentosComEtiquetaCount(SceLoteDocImpressao entidadePesquisa) {
		return getImprimirEtiquetasExtrasON().efetuarPesquisaUnitarizacaoDeMedicamentosComEtiquetaCount(entidadePesquisa);
	}

	@Override
	public List<RelatorioConsumoSinteticoMaterialVO> pesquisarRelatorioConsumoSinteticoMaterial(final Integer cctCodigo,
			final Short almSeq, final DominioEstocavelConsumoSinteticoMaterial estocavel, final Long cn5Numero, final Date dt_competencia,
			final DominioOrdenacaoConsumoSinteticoMaterial ordenacao, ScoGrupoMaterial grupoMaterial) {
		return getRelatorioConsumoSinteticoMateriaisON().pesquisarRelatorioConsumoSinteticoMaterial(cctCodigo, almSeq, estocavel,
				cn5Numero, dt_competencia, ordenacao, grupoMaterial);
	}

	@Override
	public SceRmrPaciente inserirSceRmrPaciente(SceRmrPaciente sceRmrPaciente, boolean flush) {
		this.getSceRmrPacienteDAO().persistir(sceRmrPaciente);
		if (flush) {
			this.getSceRmrPacienteDAO().flush();
		}
		return sceRmrPaciente;
	}

	@Override
	public SceRmrPaciente atualizarSceRmrPaciente(SceRmrPaciente sceRmrPaciente, boolean flush) {
		SceRmrPaciente retorno = this.getSceRmrPacienteDAO().atualizar(sceRmrPaciente);
		if (flush) {
			this.getSceRmrPacienteDAO().flush();
		}
		return retorno;
	}

	@Override
	public void removerSceRmrPaciente(SceRmrPaciente sceRmrPaciente, boolean flush) {
		this.getSceRmrPacienteDAO().remover(sceRmrPaciente);
		if (flush) {
			this.getSceRmrPacienteDAO().flush();
		}
	}

	protected RelatorioConsumoSinteticoMateriaisON getRelatorioConsumoSinteticoMateriaisON() {
		return relatorioConsumoSinteticoMateriaisON;
	}

	protected ImprimirEtiquetasExtrasON getImprimirEtiquetasExtrasON() {
		return imprimirEtiquetasExtrasON;
	}

	protected SceRefCodesDAO getSceRefCodesDAO() {
		return sceRefCodesDAO;
	}

	protected SceFornecedorMaterialDAO getSceFornecedorMaterialDAO() {
		return sceFornecedorMaterialDAO;
	}

	protected FcpMoedaDAO getFcpMoedaDAO() {
		return fcpMoedaDAO;
	}

	@Override
	public FcpMoeda obterMoedaPorChavePrimaria(Object chaveBusca) {
		return this.getFcpMoedaDAO().obterPorChavePrimaria(chaveBusca);
	}

	@Override
	public List<FcpMoeda> pesquisarMoeda(Object parametro) {
		return this.getFcpMoedaDAO().pesquisarMoeda(parametro);
	}

	@Override
	public void removerSceItemRmps(SceItemRmps sceItemRmps, boolean flush) {
		this.getSceItemRmpsDAO().remover(sceItemRmps);
		if (flush) {
			this.getSceItemRmpsDAO().flush();
		}
	}

	@Override
	public Long obterListaSceItemRmpsPorSceRmrPacienteCount(Integer seq) {
		return this.getSceItemRmpsDAO().obterListaSceItemRmpsPorSceRmrPacienteCount(seq);
	}

	@Override
	public Long countSceRmrPaciente(Integer rmpSeq) {
		return this.getSceItemRmpsDAO().countSceRmrPaciente(rmpSeq);
	}

	@Override
	public Integer getObterNotaFiscal(SceLoteDocumento loteDoc) {
		return this.getImprimirEtiquetasUnitarizacaoON().obterNroNf(loteDoc);
	}

	@Override
	public List<SceMovimentoMaterial> obterConsumoMensal(Integer codigoMaterial, Date dtCompetencia) {
		return this.getSceMovimentoMaterialDAO().obterConsumoMensal(codigoMaterial, dtCompetencia);
	}

	@Override
	public Integer consumoMedioSazonal(Date data1, Date data2, Date data3, Integer codMaterial, Short almSeq,
			DominioIndOperacaoBasica indOperacaoBasica) {
		return this.getSceMovimentoMaterialDAO().consumoMedioSazonal(data1, data2, data3, codMaterial, almSeq, indOperacaoBasica);
	}

	@Override
	public SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterialUnidadeMedida(ScoMaterial material, ScoUnidadeMedida unidadeMedida) {
		return getSceConversaoUnidadeConsumosDAO().obterConversaoUnidadePorMaterialUnidadeMedida(material, unidadeMedida);
	}

	@Override
	public SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterial(ScoMaterial material) {
		return getSceConversaoUnidadeConsumosDAO().obterConversaoUnidadePorMaterial(material);

	}

	@Override
	public Double getUltimoValorCompra(ScoMaterial scoMaterial) throws ApplicationBusinessException {
		return this.getScoMaterialRN().getUltimoValorCompra(scoMaterial);
	}

	@Override
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(Short seqAlmoxarifado,
			Integer codigoMaterial, Integer numeroFornecedor) {
		return this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(seqAlmoxarifado,
				codigoMaterial, numeroFornecedor);
	}

	protected ScoMaterialRN getScoMaterialRN() {
		return scoMaterialRN;
	}

	@Override
	public Long quantidadeEfetivadaItensAF(Integer numeroAF, Integer numeroItem) {
		return this.getSceItemNotaRecebimentoDAO().quantidadeEfetivadaItensAF(numeroAF, numeroItem);
	}

	@Override
	public List<ItensAutFornUpdateSCContrVO> pesquisarFaseSolicitacaoItemAF(ScoItemAutorizacaoFornId itemAutorizacaoFornId,
			Date pdataCompetencia, Integer pFornecedorPadrao) {
		return this.getSceEstoqueGeralDAO().pesquisarFaseSolicitacaoItemAF(itemAutorizacaoFornId, pdataCompetencia, pFornecedorPadrao);
	}

	@Override
	public void atualizarPontoPedido(final Date dataCompetencia, final Integer codigoMaterial) throws BaseException {
		this.getAtualizarPontoPedidoRN().atualizarPontoPedido(dataCompetencia, codigoMaterial);
	}

	@Override
	public void atualizarEstoqueAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, String nomeMicrocomputador)
			throws BaseException {
		this.getSceEstoqueAlmoxarifadoRN().atualizar(sceEstoqueAlmoxarifado, nomeMicrocomputador, true);
	}

	@Override
	public SceHistoricoProblemaMaterial pesquisarQtdeBloqueadaPorProblema(Integer ealSeq) {
		return this.getSceHistoricoProblemaMaterialDAO().pesquisarQtdeBloqueadaPorProblema(ealSeq);
	}

	@Override
	public boolean existeEstoqueAlmoxarifadoItemAFConsignado(Integer afnNumero, Integer numero) {
		return this.getSceEstoqueAlmoxarifadoDAO().existeEstoqueAlmoxarifadoItemAFConsignado(afnNumero, numero);
	}

	@Override
	public Integer obterSaldosEstoqueAlmoxarifadoItemAFConsignado(Integer afnNumero, Integer numero) {
		return this.getSceEstoqueAlmoxarifadoDAO().obterSaldosEstoqueAlmoxarifadoItemAFConsignado(afnNumero, numero);
	}

	@Override
	public Integer obterQtdeConsignadaEstoqueGeralItemAF(Integer afnNumero, Integer numero) {
		return this.getSceEstoqueGeralDAO().obterQtdeConsignadaEstoqueGeralItemAF(afnNumero, numero);
	}

	@Override
	public ValidaConfirmacaoRecebimentoVO confirmarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador, Boolean validaRegras, boolean flush)
			throws BaseException {
		return this.getConfirmacaoRecebimentoON().confirmarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal,
				nomeMicrocomputador, validaRegras, flush);
	}

	@Override
	public void atualizarNotaRecebimentoProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador) throws BaseException {
		this.getSceNotaRecebimentoProvisorioRN().atualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
	}

	@Override
	public Integer obterProximoxSeqSceNotaRecebProvisorio() {
		return this.getSceNotaRecebProvisorioDAO().obterProximoxSeq();
	}

	@Override
	public void atualizarNotaRecProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws BaseException {
		this.getSceNotaRecebProvisorioDAO().atualizar(notaRecebimentoProvisorio);
	}

	@Override
	public void atualizarItemRecebProvisorio(SceItemRecebProvisorio itemRecebProvisorio) {
		this.getSceItemRecebProvisorioDAO().atualizar(itemRecebProvisorio);
	}

	// @Override
	// public void atualizarItemRecebXProgrEntrega(SceItemRecbXProgrEntrega
	// itemRecbXProgrEntrega){
	// this.getSceItemRecbXProgrEntregaDAO().atualizar(itemRecbXProgrEntrega);
	// }

	protected AtualizarPontoPedidoRN getAtualizarPontoPedidoRN() {
		return atualizarPontoPedidoRN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarNotasRecebimentoProvisorioConfirmacao
	 * (br.gov.mec.aghu.estoque.vo.ConfirmacaoRecebimentoFiltroVO,
	 * java.lang.Boolean, java.lang.Boolean)
	 */
	@Override
	@Secure("#{s:hasPermission('recebimentoProvisorio','consultar')}")
	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorioConfirmacao(final ConfirmacaoRecebimentoFiltroVO filtroVO,
			final Boolean indConfirmado, final Boolean indEstorno) {
		return getConfirmacaoRecebimentoON().pesquisarNotasRecebimentoProvisorioConfirmacao(filtroVO, indConfirmado, indEstorno);
	}

	protected ConfirmacaoRecebimentoON getConfirmacaoRecebimentoON() {
		return confirmacaoRecebimentoON;
	}

	protected RecebimentoON getRecebimentoON() {
		return recebimentoON;
	}

	protected SceNotaRecebimentoProvisorioRN getSceNotaRecebimentoProvisorioRN() {
		return sceNotaRecebimentoProvisorioRN;
	}

	protected SceNotaRecebProvisorioDAO getSceNotaRecebProvisorioDAO() {
		return sceNotaRecebProvisorioDAO;
	}

	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}

	protected SceItemRecbXProgrEntregaRN getSceItemRecbXProgrEntregaRN() {
		return sceItemRecbXProgrEntregaRN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarItensNotaRecebimentoProvisorio(java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('recebimentoProvisorio','consultar')}")
	public List<ItemRecebimentoProvisorioVO> pesquisarItensNotaRecebimentoProvisorio(final Integer nrpSeq, final Boolean indExclusao)
			throws ApplicationBusinessException {
		return this.getConfirmacaoRecebimentoON().pesquisarItensNotaRecebimentoProvisorio(nrpSeq, indExclusao);
	}

	@Override
	@Secure("#{s:hasPermission('recebimentoProvisorio','consultar')}")
	public List<ItemRecebimentoProvisorioRelVO> pesquisarRelatorioItemRecProv(final List<Integer> listaNrpSeq) {
		return this.getSceItemRecebProvisorioDAO().pesquisarRelatorioItemRecProv(listaNrpSeq);
	}

	@Override
	public Double obterValorTotalItemNotaFiscal(Integer dfeSeq) {
		return this.getSceItemRecebProvisorioDAO().obterValorTotalItemNotaFiscal(dfeSeq);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * pesquisarNotasRecebimentoProvisorio
	 * (br.gov.mec.aghu.estoque.vo.RecebimentoFiltroVO, java.lang.Integer,
	 * java.lang.Integer, java.lang.String, boolean, java.util.List)
	 */
	@Override
	@Secure("#{s:hasPermission('recebimento','consultar')}")
	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorio(final RecebimentoFiltroVO filtroVO, final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc,
			final List<ApplicationBusinessException> errosNotasRecebimentoProvisorio) {
		return this.getRecebimentoON().pesquisarNotasRecebimentoProvisorio(filtroVO, firstResult, maxResult, orderProperty, asc,
				errosNotasRecebimentoProvisorio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#
	 * contarNotasRecebimentoProvisorio
	 * (br.gov.mec.aghu.estoque.vo.RecebimentoFiltroVO)
	 */
	@Override
	@Secure("#{s:hasPermission('recebimento','consultar')}")
	public Long contarNotasRecebimentoProvisorio(final RecebimentoFiltroVO popularFiltro) {
		return this.getSceNotaRecebProvisorioDAO().contarNotasRecebimentoProvisorio(popularFiltro);
	}

	/** {@inheritDoc} */
	@Override
	public List<GeraSolicCompraEstoqueVO> obterMateriaisGeracaoSc(SceAlmoxarifado almoxarifado, SceAlmoxarifado almoxCentral,
			Integer fornecedorPadraoId) {
		return getSceEstoqueAlmoxarifadoDAO().obterMateriaisGeracaoSc(almoxarifado, almoxCentral, fornecedorPadraoId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#estornarRecebConfirmado
	 * (br.gov.mec.aghu.estoque.SceNotaRecebProvisorio)
	 */
	public void estornarRecebConfirmado(SceNotaRecebProvisorio notaRecebimentoProvisorio, String nomeMicrocomputador) throws BaseException {
		this.getRecebimentoON().estornarRecebConfirmado(notaRecebimentoProvisorio, nomeMicrocomputador);
	}

	public void estornarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal) throws BaseException {
		this.getConfirmacaoRecebimentoON().estornarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
	}

	public List<ItemRecebimentoProvisorioVO> pesquisarItensProvisoriosAlterados(List<ItemRecebimentoProvisorioVO> lista) {
		return this.getConfirmacaoRecebimentoON().pesquisarItensProvisoriosAlterados(lista);
	}

	public ValidaConfirmacaoRecebimentoVO gravarItensRecebimento(List<ItemRecebimentoProvisorioVO> listaItens, Boolean valida)
			throws ApplicationBusinessException {
		return this.getConfirmacaoRecebimentoON().gravarItensRecebimento(listaItens, valida);
	}

	public void calcularVariacaoItemRecebimentoProvisorio(ItemRecebimentoProvisorioVO item) {
		this.getConfirmacaoRecebimentoON().calcularVariacaoItemRecebimentoProvisorio(item);
	}

	@Override
	public QtdeRpVO somarQtdeItensNotaRecebProvisorio(ScoItemAutorizacaoForn itemAf, int maxRps) {
		return getSceNotaItemRecebProvisorioON().somarQtdeItensNotaRecebProvisorio(itemAf, maxRps);
	}

	@Override
	public QtdeRpVO somarQtdeItensNotaRecebProvisorio(ScoProgEntregaItemAutorizacaoFornecimento item, int maxRps) {
		return getSceNotaItemRecebProvisorioON().somarQtdeItensNotaRecebProvisorio(item, maxRps);
	}

	protected SceNotaItemRecebProvisorioON getSceNotaItemRecebProvisorioON() {
		return sceNotaItemRecebProvisorioON;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterNotaRecebProvisorio
	 * (java.lang.Integer)
	 */
	@Override
	public SceNotaRecebProvisorio obterNotaRecebProvisorio(Integer seq) {
		SceNotaRecebProvisorio notaRecebProv = getSceNotaRecebProvisorioDAO().obterPorChavePrimaria(seq);

		getSceNotaRecebProvisorioDAO().initialize(notaRecebProv);
		getSceNotaRecebProvisorioDAO().initialize(notaRecebProv.getDocumentoFiscalEntrada());
		getSceNotaRecebProvisorioDAO().initialize(notaRecebProv.getScoAfPedido());
		if (notaRecebProv.getScoAfPedido() != null) {
			getSceNotaRecebProvisorioDAO().initialize(notaRecebProv.getScoAfPedido().getScoAutorizacaoForn());
			if (notaRecebProv.getScoAfPedido().getScoAutorizacaoForn() != null) {
				getSceNotaRecebProvisorioDAO().initialize(notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor());
				if (notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor() != null) {
					getSceNotaRecebProvisorioDAO().initialize(
							notaRecebProv.getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor());
				}
			}
		}

		return notaRecebProv;
	}

	public SceNotaRecebimento obterNotaRecebimentoOriginal(Integer notaRecebimentoSeq) {
		return this.getSceNotaRecebimentoDAO().obterOriginal(notaRecebimentoSeq);
	}

	@Override
	public List<PendenciasDevolucaoVO> pesquisarGeracaoPendenciasDevolucao(Integer numeroNr) {
		return this.getGeracaoDevolucaoON().pesquisarGeracaoPendenciasDevolucao(numeroNr);
	}

	protected GeracaoDevolucaoON getGeracaoDevolucaoON() {
		return geracaoDevolucaoON;
	}

	@Override
	public SceNotaRecebProvisorio preReceberItensAdiantamentoAF(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoAdiantamentoVO> listaItensAdiantamento) throws ApplicationBusinessException {
		return getRecebimentoSemAFON().preReceberItensAdiantamentoAF(filtro, listaItensAdiantamento);
	}

	protected void setSceDocumentoFiscalEntradaDAO(SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO) {
		this.sceDocumentoFiscalEntradaDAO = sceDocumentoFiscalEntradaDAO;
	}

	protected RecebimentoSemAFON getRecebimentoSemAFON() {
		return recebimentoSemAFON;
	}

	protected void setRecebimentoSemAFON(RecebimentoSemAFON recebimentoSemAFON) {
		this.recebimentoSemAFON = recebimentoSemAFON;
	}

	@Override
	public Boolean verificaExisteBoletimOcorrencia(Integer seqNotaRecebimento) {
		return this.getGeracaoDevolucaoON().verificaExisteBoletimOcorrencia(seqNotaRecebimento);

	}

	@Override
	public List<SceConversaoUnidadeConsumos> pesquisarConversaoPorMaterialCodigoUnidadeMaterialDescricao(Integer matCodigo, Object param) {
		return this.getSceConversaoUnidadeConsumosDAO().pesquisarConversaoPorMaterialCodigoUnidadeMaterialDescricao(matCodigo, param);
	}

	@Override
	public void gerarDevolucao(List<PendenciasDevolucaoVO> listaPendencias, SceNotaRecebimento notaRecebimento,
			SceDocumentoFiscalEntrada docFiscalEntrada, String nomeMicrocomputador) throws BaseException {
		this.getGeracaoDevolucaoON().gerarDevolucao(listaPendencias, notaRecebimento, docFiscalEntrada, nomeMicrocomputador);
	}

	public SceNotaRecebProvisorio clonarNotaRecebimentoProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorio)
			throws ApplicationBusinessException {
		return this.getConfirmacaoRecebimentoON().clonarNotaRecebimentoProvisorio(notaRecebimentoProvisorio);
	}

	protected SceBoletimOcorrenciasDAO getSceBoletimOcorrenciasDAO() {
		return sceBoletimOcorrenciasDAO;
	}

	@Override
	public List<SceBoletimOcorrencias> pesquisarBoletimOcorrenciaNotaRecebimentoSituacao(Integer seqNotaRecebimento,
			DominioBoletimOcorrencias situacao, Boolean indSituacaoDiferente) {
		return this.getSceBoletimOcorrenciasDAO().pesquisarBoletimOcorrenciaNotaRecebimentoSituacao(seqNotaRecebimento, situacao,
				indSituacaoDiferente);
	}

	@Override
	public List<SceBoletimOcorrencias> pesquisarBoletimOcorrenciaNotaRecebimento(Integer seqNotaRecebimento) {
		return this.getSceBoletimOcorrenciasDAO().pesquisarBoletimOcorrenciaNotaRecebimento(seqNotaRecebimento);
	}	
	@Override
	public List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntradaNumeroOuFornecedor(Object param, ScoFornecedor fornecedor) {
		return sceDocumentoFiscalEntradaRN.pesquisarNotafiscalEntradaNumeroOuFornecedor(param, fornecedor);
	}

	@Override
	public SceLote obterSceLotePorChavePrimaria(SceLoteId sceLoteId) {
		return this.getSceLoteDAO().obterPorChavePrimaria(sceLoteId);
	}

	@Override
	public boolean isNotaRecebProvisorioServico(SceNotaRecebProvisorio notaRecebProv) {
		return getSceNotaRecebimentoProvisorioRN().isNotaRecebProvisorioServico(notaRecebProv);
	}

	@Override
	public SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(final Short seqAlmoxarifadoOrigem,
			final Integer codigoMaterial, final Integer numeroFornecedor) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(seqAlmoxarifadoOrigem,
				codigoMaterial, numeroFornecedor);
	}

	@Override
	public Boolean isItemAFCurvaA(Integer afnNumero, Integer fornecedorPadrao) {
		return getSceEstoqueGeralDAO().isItemAFCurvaA(afnNumero, fornecedorPadrao);
	}

	@Override
	public SceAlmoxarifado obterSceAlmoxarifadoPorChavePrimaria(Short seq) {
		return this.getSceAlmoxarifadoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public SceEstoqueGeral obterSceEstoqueGeralPorMaterialDataCompetencia(ScoMaterial material, Date dataCompetencia) {
		return this.getSceEstoqueGeralDAO().obterSceEstoqueGeralPorMaterialDataCompetencia(material, dataCompetencia);
	}

	@Override
	public QuantidadesVO obterQuantidadeQuantidadeDevolvidaSceItemEntrSaidSemLicitacao(Integer afnNumero, Integer numero) {
		return getSceItemEntrSaidSemLicitacaoDAO().obterQuantidadeQuantidadeDevolvida(afnNumero, numero);
	}

	@Override
	public Long obterQuantidadeRecParcelasSceItemRecebProvisorio(Integer matCodigo) {
		return this.getSceItemRecebProvisorioDAO().obterQuantidadeRecParcelas(matCodigo);
	}

	@Override
	public DominioClassifABC obterClassificacaoABCMaterialSceEstoqueGeral(Integer matCodigo, Integer numeroFornecedor, Date dataCompetencia) {
		return this.getSceEstoqueGeralDAO().obterClassificacaoABCMaterial(matCodigo, numeroFornecedor, dataCompetencia);
	}

	@Override
	public QuantidadesVO obterQtdDisponivelQtdBloqueadaSceValidade(Integer matCodigo, boolean indControleValidade, Date dataReferencia,
			boolean almoxUnico) {
		return this.getSceValidadeDAO().obterQtdDisponivelQtdBloqueada(matCodigo, indControleValidade, dataReferencia, almoxUnico);
	}

	@Override
	public GrupoMaterialNumeroSolicitacaoVO obterCodigoGrupoMaterialNumeroSolicitacaoSceItemRecebProvisorio(Integer afnNumero,
			Integer numero) {
		return this.getSceItemRecebProvisorioDAO().obterCodigoGrupoMaterialNumeroSolicitacao(afnNumero, numero);
	}

	@Override
	public SceEstoqueAlmoxarifado obterSceEstoqueAlmoxarifadoPorMaterialFornecedor(Integer matCodigo, Integer numeroFornecedor) {
		return this.getSceEstoqueAlmoxarifadoDAO().buscarPorMaterialAlmoxarifadoFornecedor(matCodigo, numeroFornecedor);
	}

	@Override
	public Date obterDataValidadeSceValidade(Integer ealSeq) {
		return this.getSceValidadeDAO().obterDataValidade(ealSeq);
	}

	private SceItemEntrSaidSemLicitacaoDAO getSceItemEntrSaidSemLicitacaoDAO() {
		return sceItemEntrSaidSemLicitacaoDAO;
	}

	@Override
	public void popularHistoricoConsumo(EstatisticaEstoqueAlmoxarifadoVO estatistica, Integer codMaterial, Short almoxSeq,
			Date dtCompetencia) throws ApplicationBusinessException {
		this.getSceEstoqueAlmoxarifadoRN().popularHistoricoConsumo(estatistica, codMaterial, almoxSeq, dtCompetencia);
	}

	@Override
	public void popularConsumoMedio(EstatisticaEstoqueAlmoxarifadoVO estatistica) {
		this.getSceEstoqueAlmoxarifadoRN().popularConsumoMedio(estatistica);
	}

	@Override
	public void popularConsumoPonderado(EstatisticaEstoqueAlmoxarifadoVO estatistica) {
		this.getSceEstoqueAlmoxarifadoRN().popularConsumoPonderado(estatistica);
	}

	@Override
	public EstoqueMaterialVO obterEstoqueMaterial(Integer matCodigo, Date dataCompetencia, Integer numeroFornecedor) {
		return getSceEstoqueAlmoxarifadoDAO().obterEstoqueMaterial(matCodigo, dataCompetencia, numeroFornecedor);
	}

	@Override
	public Integer buscarEstoqueAlmoxarifadoPorAutFornNumeroItlNumeroFornPadrao(Integer afNumero, Short itlNumero, Integer fornecedorPadrao) {
		return getSceEstoqueAlmoxarifadoDAO().buscarEstoqueAlmoxarifadoPorAutFornNumeroItlNumeroFornPadrao(afNumero, itlNumero,
				fornecedorPadrao);
	}

	@Override
	public void atualizarEstoqueAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) {
		getSceEstoqueAlmoxarifadoDAO().atualizar(sceEstoqueAlmoxarifado);
		getSceEstoqueAlmoxarifadoDAO().flush();
	}

	@Override
	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoPorChavePrimaria(Integer seqEstoqueAlmoxarifado) {
		return getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(seqEstoqueAlmoxarifado);
	}

	@Override
	public SceEstoqueGeral pesquisarEstoqueGeralPorMatDtCompFornecedor(Integer codMaterial, Date dataCompetencia, Integer numero) {
		return getSceEstoqueGeralDAO().pesquisarEstoqueGeralPorMatDtCompFornecedor(codMaterial, dataCompetencia, numero);
	}

	@Override
	public Long consultarCampoObservacaoParcelaPendente(Integer iafAfnNumero, Integer iafNumero) {
		return getSceNotaRecebProvisorioDAO().consultarCampoObservacaoParcelaPendente(iafAfnNumero, iafNumero);
	}

	@Override
	public Date buscaDataEncerramento(Integer pac) {
		return getSceNotaRecebimentoDAO().buscaDataEncerramento(pac);
	}
	
	@Override
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMateriasHistorico(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String modl, Integer matCodigo) {
		return getSceDocumentoFiscalEntradaDAO().pesquisarUltimasComprasMateriasHistorico(firstResult, maxResult, orderProperty, asc, modl, matCodigo);
	}

	@Override
	public Long pesquisarUltimasComprasMateriasHistoricoCount(String modl,Integer matCodigo) {
		return getSceDocumentoFiscalEntradaDAO().pesquisarUltimasComprasMateriasHistoricoCount(modl, matCodigo);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarComplemento(Integer numeroAf, Object numComplementoAf, Integer numFornecedor) {
		return getRecebimentoSemAFON().pesquisarComplemento(numeroAf, numComplementoAf, numFornecedor);
	}

	@Override
	public List<ScoFornecedor> pesquisarFornecedor(Integer numeroAf, Short numComplementoAf, Object numFornecedor, Boolean indAdiantamentoAF) {
		return getRecebimentoSemAFON().pesquisarFornecedor(numeroAf, numComplementoAf, numFornecedor, indAdiantamentoAF);
	}

	@Override
	public List<ItensRecebimentoAdiantamentoVO> pesquisarItensRecebimentoAdiantamento(Integer numeroAF) {
		return getRecebimentoSemAFON().pesquisarItensRecebimentoAdiantamento(numeroAF);
	}

	@Override
	public List<SceDocumentoFiscalEntrada> pesquisarNFEntradaGeracaoNumeroOuFornecedor(Object param, ScoFornecedor fornecedor) {
		return this.getSceDocumentoFiscalEntradaDAO().pesquisarNFEntradaGeracaoNumeroOuFornecedor(param, fornecedor);
	}

	@Override
	public SceNotaRecebProvisorio preReceberItensMateriais(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoVO> listaItensMateriais) throws ApplicationBusinessException {
		return getRecebimentoSemAFON().preReceberItensMateriais(filtro, listaItensMateriais);
	}

	@Override
	public Double obterValorMaterialSemAF(Integer codigoMaterial) {
		return getSceMovimentoMaterialDAO().obterValorMaterialSemAF(codigoMaterial);
	}

	@Override
	public List<SceTipoMovimento> obterSiglaTipoMovimento(Object param) {
		return getSceTipoMovimentosDAO().obterSiglaTipoMovimento(param);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarAF(Object numeroAf, Short numComplementoAf, Integer numFornecedor) {
		return getRecebimentoSemAFON().pesquisarAF(numeroAf, numComplementoAf, numFornecedor);
	}

	@Override
	public ValidaConfirmacaoRecebimentoVO confirmarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador, Boolean validaRegras, boolean flush,
			boolean validarEsl) throws BaseException {
		return this.getConfirmacaoRecebimentoON().confirmarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal,
				nomeMicrocomputador, validaRegras, flush, validarEsl);
	}

	@Override
	public List<ScoSiasgMaterialMestre> obterCatMat(String catMat) {
		return getScoSiasgMaterialMestreDAO().listarMateriaisCatMat(catMat);
	}

	@Override
	public void validarInsercaoPacoteRequisicaoMaterial(SceReqMaterial reqMaterial, List<SceEstoqueAlmoxarifado> lstItemToAdd,
			Boolean isAlmoxarife) throws ApplicationBusinessException {
		getManterPacoteMateriaisON().validarInsercaoPacoteRequisicaoMaterial(reqMaterial, lstItemToAdd, isAlmoxarife);
	}

	@Override
	public void popularConsumosItemPacoteVO(ItemPacoteMateriaisVO itemPacoteMateriaisVO) throws ApplicationBusinessException {
		this.getManterPacoteMateriaisON().popularConsumosItemPacoteVO(itemPacoteMateriaisVO);
	}

	@Override
	public List<SceAlmoxarifadoGrupos> pesquisarGruposPorAlmoxarifado(SceAlmoxarifado almox) {
		return getSceAlmoxarifadoGruposDAO().pesquisarGruposPorAlmoxarifado(almox);
	}

	@Override
	public void cancelarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		getEstornarRequisicaoMaterialON().cancelarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}

	@Override
	public List<SceAlmoxarifadoComposicao> pesquisarPorAlmoxarifado(SceAlmoxarifado almox) {
		return getSceAlmoxarifadoComposicaoDAO().pesquisarPorAlmoxarifado(almox);
	}

	@Override
	public List<SceAlmoxarifadoGrupos> pesquisarGruposPorComposicao(SceAlmoxarifadoComposicao comp) {
		return this.getSceAlmoxarifadoGruposDAO().pesquisarGruposPorComposicao(comp);
	}

	@Override
	public List<ComposicaoGruposVO> pesquisarComposicaoAlmox(SceAlmoxarifado almoxarifado) {
		return getComposicaoGruposAlmoxON().pesquisarComposicaoAlmox(almoxarifado);
	}

	@Override
	public void persistirComposicaoAlmox(SceAlmoxarifado almoxarifado, List<ComposicaoGruposVO> listaVO)
			throws ApplicationBusinessException {
		getComposicaoGruposAlmoxON().persistirComposicaoAlmox(almoxarifado, listaVO);
	}

	@Override
	public Boolean verificarGrupoOutraComposicao(ScoGrupoMaterial grupo, Integer seqComposicao, SceAlmoxarifado almox,
			List<ComposicaoGruposVO> listaTela) {
		return getComposicaoGruposAlmoxON().verificarGrupoOutraComposicao(grupo, seqComposicao, almox, listaTela);
	}

	@Override
	public void removerComposicaoAlmox(SceAlmoxarifado almoxarifado) {
		getComposicaoGruposAlmoxON().removerComposicaoAlmox(almoxarifado);
	}

	@Override
	public List<RecebimentosProvisoriosVO> pesquisarRecebimentoProvisorio(ConfirmacaoRecebimentoFiltroVO filtroVO, Boolean indConfirmado,
			Boolean indEstorno) {
		return getConfirmacaoRecebimentoON().listarRecebimentosProvisorios(filtroVO, indConfirmado, indEstorno);
	}

	@Override
	public List<SceItemEntrSaidSemLicitacao> listaItensRecebimentoProvisorio(Integer seq) {
		return getSceItemEntrSaidSemLicitacaoDAO().listaItemEntrSaidSemLicitacao(seq);
	}

	@Override
	public void atualizarEntrSaidSemLicitacao(SceEntrSaidSemLicitacao sceEntrSaidSemLicitacao) {
		getSceEntrSaidSemLicitacaoDAO().atualizar(sceEntrSaidSemLicitacao);
	}

	@Override
	public SceEntrSaidSemLicitacao obterEntrSaidSemLicitacao(Integer seq) {
		return getSceEntrSaidSemLicitacaoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public Double verificarDoacoes(Integer matCodigo, String rmpSeq, Boolean porNotaFiscal) {
		return this.getSceRmrPacienteDAO().verificarDoacoes(matCodigo, rmpSeq, porNotaFiscal);
	}

	@Override
	public Double buscarCustoOrteseOuProteseUltimaEntrada(Integer matCodigo) {
		return getSceRmrPacienteDAO().buscarCustoOrteseOuProteseUltimaEntrada(matCodigo);
	}

	@Override
	public void atualizarSceLoteDocImpressao(SceLoteDocImpressao loteDocImpressao) throws BaseException {
		getSceLoteDocImpressaoRN().atualizar(loteDocImpressao);
	}

	@Override
	public void alterarSituacaoGeradaRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		getEstornarRequisicaoMaterialON().alterarSituacaoGeradaRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
	}

	protected SceAlmoxarifadoComposicaoDAO getSceAlmoxarifadoComposicaoDAO() {
		return sceAlmoxarifadoComposicaoDAO;
	}

	public SceLoteDocImpressaoRN getSceLoteDocImpressaoRN() {
		return sceLoteDocImpressaoRN;
	}

	protected SceEntrSaidSemLicitacaoDAO getSceEntrSaidSemLicitacaoDAO() {
		return sceEntrSaidSemLicitacaoDAO;
	}

	protected ScoSiasgMaterialMestreDAO getScoSiasgMaterialMestreDAO() {
		return scoSiasgMaterialMestreDAO;
	}

	protected SceAlmoxarifadoGruposDAO getSceAlmoxarifadoGruposDAO() {
		return sceAlmoxarifadoGruposDAO;
	}

	protected ComposicaoGruposAlmoxON getComposicaoGruposAlmoxON() {
		return composicaoGruposAlmoxON;
	}

	@Override
	public List<HistoricoScoMaterialVO> identificarAlteracoesScoMaterial(ScoMaterialJN atual, ScoMaterialJN anterior)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getConsultarHistoricoMaterialON().identificarAlteracoesScoMaterial(atual, anterior);
	}

	protected ConsultarHistoricoMaterialON getConsultarHistoricoMaterialON() {
		return consultarHistoricoMaterialON;
	}

	@Override
	public ScoMaterial obterMaterialPorId(Integer codigo) {
		return getConsultarHistoricoMaterialON().obterMaterialPorId(codigo);
	}

	@Override
	public Boolean habilitarConfaz() {
		return getManterMaterialON().habilitarConfaz();
	}

	@Override
	public List<ScoMaterialJN> pesquisarScoMaterialJNPorCodigoMaterial(Integer codigoMaterial, DominioOperacoesJournal operacao) {
		return getConsultarHistoricoMaterialON().pesquisarScoMaterialJNPorCodigoMaterial(codigoMaterial, operacao);
	}

	public List<ClassificacaoMaterialVO> pesquisarClassificacoes(
			Integer codGrupo, Object parametro) {
		return getClassificarMaterialON().pesquisarClassificacoes(codGrupo, parametro);
	}

	public Long pesquisarClassificacoesCount(Integer codGrupo, Object parametro) {
		return getClassificarMaterialON().pesquisarClassificacoesCount(codGrupo, parametro);
	}

	public List<MaterialClassificacaoVO> listarMateriasPorClassificacao(
			Long cn5, Integer codGrupo) {
		return getClassificarMaterialON().listarMateriasPorClassificacao(cn5, codGrupo);
	}
	
	public boolean verificarExistenciaSceReqMateriaisPorScoGrupoMaterial(Integer gmtCodigo) {
		return getSceReqMateriaisDAO().verificarExistenciaSceReqMateriaisPorScoGrupoMaterial(gmtCodigo);
	}

	@Secure("#{s:hasPermission('cadastrarMaterialClassificacao','executar') or s:hasPermission('cadastrarClassificacaoMaterial','executar')}")
	public void adicionarMaterialClassificacao(Long cn5Numero,
			Integer matCodigo, ScoMaterial material,
			String nomeMicrocomputador) throws BaseException {
		getClassificarMaterialON().adicionarMaterialClassificacao(cn5Numero, matCodigo, material, nomeMicrocomputador);
	}
	
	@Secure("#{s:hasPermission('cadastrarMaterialClassificacao','executar') or s:hasPermission('cadastrarClassificacaoMaterial','executar')}")
	public void removerMateriaisClassificacao(ScoMateriaisClassificacoesId id) throws BaseException {
		getClassificarMaterialON().removerMateriaisClassificacao(id);
	}
	
	@Override
	public void salvarMotivoMovimento(SceMotivoMovimento sceMotivoMovimento)
			throws BaseException {
		getManterMotivoMovimentoRN().salvarMotivoMovimento(sceMotivoMovimento);
	}

	@Override
	public void removerMotivoMovimento(SceMotivoMovimento sceMotivoMovimento)
			throws BaseException {
		getManterMotivoMovimentoRN().removerMotivoMovimento(sceMotivoMovimento);
		
	}

	@Override
	public List<SceMotivoMovimento> listarMotivoMovimento(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Short seq,
			Byte complemento) {
		return getSceMotivoMovimentoDAO().listarMotivoMovimento(firstResult, maxResult, orderProperty, asc, seq, complemento);
	}

	@Override
	public Long listarMotivoMovimentoCount(Short seq, Byte complemento) {
		return getSceMotivoMovimentoDAO().listarMotivoMovimentoCount(seq, complemento);
	}

	@Override
	public List<SceTipoMovimento> listarTipoMovimento(Object param) {
		return getSceTipoMovimentosDAO().obterListaTipoMovimentosAtivos(param);
	}

	@Override
	public Long listarTipoMovimentoCount(Object param) {
		return getSceTipoMovimentosDAO().obterListaTipoMovimentosAtivosCount(param);
	}

	@Override
	public List<ClassificacaoMaterialVO> pesquisarClassificacaoDoMaterial(
			Integer codMaterial) {
		return getClassificarMaterialON().pesquisarClassificacaoDoMaterial(codMaterial);
	}

	@Override
	public void efetuarDownloadCSVRelatorioContagemEstoqueInventario(
			String fileName) throws IOException {
		getGerarRelatorioContagemEstoqueInventarioON().efetuarDownloadCSVRelatorioContagemEstoqueInventario(fileName);
	}

	@Override
	public void efetuarDownloadCSVRelatorioAjusteEstoque(String fileName,
			Date dataCompetencia) throws IOException {
		getGerarRelatorioAjusteEstoqueON().efetuarDownloadCSVRelatorioAjusteEstoque(fileName, dataCompetencia);	
	}

	@Override
	public void efetuarDownloadCSVMensalMateriaisClassificacaoAbc(
			String fileName, Date competencia) throws IOException {
		getGerarRelatorioMateriaisClassificacaoABCON().efetuarDownloadCSVMensalMateriaisClassificacaoAbc(fileName, competencia);
	}

	@Override
	public void estornarNotaRecebimento(SceNotaRecebimento notaRecebimento,
			SceNotaRecebimento notaRecebimentoOriginal,
			String nomeMicrocomputador, Boolean flush) throws BaseException {
		getEstornarNotaRecebimentoON().estornarNotaRecebimento(notaRecebimento, notaRecebimentoOriginal, nomeMicrocomputador, flush);
	}

	@Override
	public void efetuarDownloadCSVRelatorioTransferenciaMaterial(
			String fileName, Integer numTransferenciaMaterial)
			throws IOException {
		getGerarRelatorioTransferenciaMaterialON().efetuarDownloadCSVRelatorioTransferenciaMaterial(fileName,numTransferenciaMaterial);
	}

	@Override
	public List<FcpAgenciaBanco> pesquisarAgenciaBanco(Object param) {
		return getFcpAgenciaBancoDAO().pesquisarAgenciaBanco(param);
	}

	@Override
	public Long pesquisarAgenciaBancoCount(Object param) {
		return getFcpAgenciaBancoDAO().pesquisarAgenciaBancoCount(param);
	}

	@Override
	public List<SociosFornecedoresVO> listarSociosFornecedores(
			Integer firstResults, Integer maxResults, String orderProperty,
			boolean asc, Integer filtroCodigo, String filtroNomeSocio,
			String filtroRG, Long filtroCPF, Integer filtroFornecedor) {
		return this.getManterSociosFornecedoresRN().listarSociosFornecedores(firstResults, maxResults, orderProperty, asc, 
				filtroCodigo, filtroNomeSocio, filtroRG, filtroCPF, filtroFornecedor);
	}

	@Override
	public Long listarSociosFornecedoresCount(Integer filtroCodigo,
			String filtroNomeSocio, String filtroRG, Long filtroCPF,
			Integer filtroFornecedor) {
		return this.getManterSociosFornecedoresRN().listarSociosFornecedoresCount(filtroCodigo, filtroNomeSocio, 
				filtroRG, filtroCPF, filtroFornecedor);
	}

	protected ClassificarMaterialON getClassificarMaterialON() {
		return classificarMaterialON;
	}

	protected ManterSociosFornecedoresRN getManterSociosFornecedoresRN() {
		return manterSociosFornecedoresRN;
	}

	protected ManterMotivoMovimentoRN getManterMotivoMovimentoRN() {
		return manterMotivoMovimentoRN;
	}

	protected FcpAgenciaBancoDAO getFcpAgenciaBancoDAO() {
		return fcpAgenciaBancoDAO;
	}

	@Override
	public void gravarSocioFornecedores(ScoSocios socio, List<ScoFornecedor> listaFornecedores) throws BaseException {
		this.getManterSociosFornecedoresRN().gravarSocio(socio, listaFornecedores);
	}
	
	@Override
	public void removerSocios(ScoSocios scoSocios) {
		this.getManterSociosFornecedoresRN().removerSocios(scoSocios);
	}
	
	@Override
	public void removerScoSociosFornecedores(ScoSociosFornecedores scoSociosFornecedores) {
		this.getManterSociosFornecedoresRN().removerSociosFornecedores(scoSociosFornecedores);
		
	}

	@Override
	public Long pesquisarListaCFOPCount(SceCfop cfop) throws BaseException {
		return this.getSceCfopOn().pesquisarListaCFOPCount(cfop);
	}

	@Override
	public List<SceCfop> pesquisarListaCFOP(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, SceCfop cfop) throws BaseException {
		return this.getSceCfopOn().pesquisarListaCFOP(firstResult, maxResult, orderProperty, asc, cfop);
	}

	@Override
	public SceCfop pesquisarCFOP(Short codigo) throws BaseException {
		return this.getSceCfopOn().pesquisarCFOP(codigo);
	}

	@Override
	public void excluirCFOP(SceCfop cfop) throws BaseException {
		this.getSceCfopOn().excluirCFOP(cfop);
	}

	@Override
	public void atualizarSCFOP(SceCfop cfop) throws BaseException {
		this.getSceCfopOn().atualizarSCFOP(cfop);
	}

	@Override
	public void inserirCFOP(SceCfop cfop) throws ApplicationBusinessException {
		this.getSceCfopOn().inserirCFOP(cfop);
	}

	/**
	 * @return the sceCfopOn
	 */
	protected SceCfopON getSceCfopOn() {
		return sceCfopOn;
	}

	/**
	 * @param sceCfopOn the sceCfopOn to set
	 */
	protected void setSceCfopOn(SceCfopON sceCfopOn) {
		this.sceCfopOn = sceCfopOn;
	}
	
	@Override
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarListagemPaginadaMaterialFornecedor(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) throws ApplicationBusinessException{
		return sceRelacionamentoMaterialFornecedorON.pesquisarListagemPaginadaMaterialFornecedor(filtroMaterialFornecedorVO);
	}
	
	@Override
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarMaterialFornecedor(Integer numeroFornecedor) throws ApplicationBusinessException {
		return sceRelacionamentoMaterialFornecedorON.pesquisarMaterialFornecedor(numeroFornecedor);
	}
	
	@Override
	public SceRelacionamentoMaterialFornecedor carregarMaterialFornecedor(
			Long codigoMaterialFornecedor) throws ApplicationBusinessException {
		return sceRelacionamentoMaterialFornecedorON.carregarMaterialFornecedor(codigoMaterialFornecedor);
	}
	
	@Override
	public List<SceRelacionamentoMaterialFornecedorJn> pesquisarHistoricoMaterialFornecedor(
			Long codigoMaterialFornecedor) throws ApplicationBusinessException {
		return sceRelacionamentoMaterialFornecedorON.pesquisarHistoricoMaterialFornecedor(codigoMaterialFornecedor);
	}
	
	@Override
	public Long pesquisarListagemPaginadaMaterialFornecedorCount(
			FiltroMaterialFornecedorVO filtroMaterialFornecedorVO) throws ApplicationBusinessException{
		return sceRelacionamentoMaterialFornecedorON.pesquisarListagemPaginadaMaterialFornecedorCount(filtroMaterialFornecedorVO);
	}
	
	@Override
	public void persistirMaterialFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException {
		sceRelacionamentoMaterialFornecedorON.persistirMaterialFornecedor(sceRelacionamentoMaterialFornecedor);
	}

	@Override
	public List<SceRelacionamentoMaterialFornecedor> pesquisarListaMateriaHospitalFornecedor(
			SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException{
		return sceRelacionamentoMaterialFornecedorON.pesquisarListaMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	@Override
	public Boolean verificarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) throws ApplicationBusinessException{
		return sceRelacionamentoMaterialFornecedorON.verificarMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	@Override
	public void alterarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		 sceRelacionamentoMaterialFornecedorON.alterarMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	@Override
	public void incluirMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		 sceRelacionamentoMaterialFornecedorON.incluirMateriaHospitalFornecedor(sceRelacionamentoMaterialFornecedor);
	}
	
	@Override
	public List<SceSuggestionBoxMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Integer numeroFornecedor, Object parametro) throws BaseException {
		return sceRelacionamentoMaterialFornecedorON.pesquisarMaterialPorFornecedor(numeroFornecedor, parametro);
	} 

	@Override
	public List<ScoMaterial> pesquisarMaterial(Object value){
		return sceRelacionamentoMaterialFornecedorON.pesquisarMaterial(value);
	}

	@Override
	public Long pesquisarMaterialCount(Object value){
		return sceRelacionamentoMaterialFornecedorON.pesquisarMaterialCount(value);
	}
	
	@Override
	public SceTipoMovimento obterSceTipoMovimentosAtivoPorSeq(BigDecimal vTmvSeq) throws ApplicationBusinessException {
		return getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(vTmvSeq.shortValue());
	}
	
	@Override
	public SceReqMaterial obterSceReqMaterialPorChavePrimaria(Integer vRmsSeq){
		return sceReqMateriaisDAO.buscarSceReqMateriaisPorId(vRmsSeq);
	}
	
	@Override
	public void atualizarSceReqMaterial(SceReqMaterial sceReqMaterial){
		 sceReqMateriaisDAO.atualizar(sceReqMaterial);
	}
	
	@Override
	public void inserirSceReqMaterial(SceReqMaterial sceReqMaterial){
		 sceReqMateriaisDAO.persistir(sceReqMaterial);
	}
	
	@Override
	public void inserirSceItemRms(SceItemRms sceItemRms){
		 sceItemRmsDAO.persistir(sceItemRms);
	}

	@Override
    public List<Double> buscarCustoMedicamento(Integer matCodigo, Date competencia, Short tmvSeq) {
        return getSceMovimentoMaterialDAO().buscarCustoMedicamento(matCodigo, competencia, tmvSeq);
    }

    @Override
    public List<Double> buscarCustoMedicamentoPelaUltimaEntradaEstoque(Integer matCodigo, Date competencia, Short tmvSeq) {
        return getSceMovimentoMaterialDAO().buscarCustoMedicamentoPelaUltimaEntradaEstoque(matCodigo, competencia, tmvSeq);
    }

    @Override
    public List<BigDecimal> obterCustoMedioPonderadoDoMaterialEstoqueGeral(Integer matCodigo, Date dtCompetencia, Integer frnNumero) {
        return getSceEstoqueGeralDAO().obterCustoMedioPonderadoDoMaterialEstoqueGeral(matCodigo, dtCompetencia, frnNumero);
    }	
	
	/*
  	 * (non-Javadoc)
  	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#verificarExistenciaAFRecebimento(java.lang.Integer)
  	 */
  	@Override
  	public Boolean verificarExistenciaAFRecebimento(Integer numeroRecebimento) {
  		return sceItemRecebProvisorioDAO.verificarExistenciaAFRecebimento(numeroRecebimento);
  	}

  	/*
  	 * (non-Javadoc)
  	 * @see br.gov.mec.aghu.estoque.business.IEstoqueFacade#obterItemRecebProvisorioPorChavePrimaria(br.gov.mec.aghu.model.SceItemRecebProvisorioId)
  	 */
	@Override
	public SceItemRecebProvisorio obterItemRecebProvisorioPorChavePrimaria(SceItemRecebProvisorioId id) {
		return sceItemRecebProvisorioDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public ScoFornecedor obterFornecedorPorId(Integer codigo) {		
		return scoFornecedorDAO.obterPorChavePrimaria(codigo);
	}
	
	@Override
	public BigDecimal obterTotalGeralComprasDia(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterTotalGeralComprasDia(dataGeracao);
	}
	
	@Override
	public BigDecimal obterTotalGeralDevolucoesDia(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterTotalGeralDevolucoesDia(dataGeracao);
	}
	
	@Override
	public BigDecimal obterTotalDiferencaFormula(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterTotalDiferencaFormula(dataGeracao);
	}
	
	@Override
	public BigDecimal obterQuantidadeMateriaisConsumidosDia(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterQuantidadeMateriaisConsumidosDia(dataGeracao);
	}
	
	@Override
	public Double obterQuantidadeServicosDia(Date dataGeracao) throws ApplicationBusinessException{
		return sceItemNotaRecebimentoRN.obterQuantidadeServicosDia(dataGeracao);
	}
	
	@Override
	public BigDecimal obterQuantidadePatrimonioEntradaDia(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterQuantidadePatrimonioEntradaDia(dataGeracao);
	}
	
	@Override
	public BigDecimal obterQuantidadeMateriaisConsumoEntradaDia(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterQuantidadeMateriaisConsumoEntradaDia(dataGeracao);
	}
	
	@Override
	public Double obterQuantidadeServicosMes(Date dataGeracao) throws ApplicationBusinessException{
		return sceItemNotaRecebimentoRN.obterQuantidadeServicosMes(dataGeracao);
	}
	
	@Override
	public BigDecimal obterQuantidadeServicosEntradaMes(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.obterQuantidadeServicosEntradaMes(dataGeracao);
	}
	
	@Override
	public BigDecimal consumoMatDiaFormula(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.consumoMatDiaFormula(dataGeracao);
	}
	
	@Override
	public BigDecimal consumoMatMesFormula(Date dataGeracao) throws ApplicationBusinessException{
		return scoMaterialRN.consumoMatMesFormula(dataGeracao);
	}
	
	//34163
    @Override
    public String gerarRelatoriosExcelESSL(EntradaSaidaSemLicitacaoVO filtroSL) throws IOException, ApplicationBusinessException{
          return geracaoRelatoriosExcelON.gerarRelatoriosExcelESSL(filtroSL);
    }
    
    @Override
    public List<EntradaSaidaSemLicitacaoVO> listarEntradaSaidaSemLicitacaoSL(EntradaSaidaSemLicitacaoVO filtroSL) throws ApplicationBusinessException{
          return sceMovimentoMaterialDAO.listarEntradaSaidaESSLComp(filtroSL);
    }
    
    @Override
    public EntradaSaidaSemLicitacaoVO listarCnpjRazaoSocial(EntradaSaidaSemLicitacaoVO objetoRetorno){
          return sceMovimentoMaterialDAO.listarCnpjRazaoSocial(objetoRetorno);
    }
    
    //42423
    public List<SceEntrSaidSemLicitacaoVO> obterNumeroESLPorSiglaTipoMovimento(String filter, String siglaTipoMovimento){return sceEntrSaidSemLicitacaoDAO.obterNumeroESLPorSiglaTipoMovimento(filter, siglaTipoMovimento);	}
	
	public Long obterNumeroESLPorSiglaTipoMovimentoCount(String filter, String siglaTipoMovimento){
		return sceEntrSaidSemLicitacaoDAO.obterNumeroESLPorSiglaTipoMovimentoCount(filter, siglaTipoMovimento);
	}
	
	@Override
	public void validaRegras(EntradaSaidaSemLicitacaoVO vo) throws ApplicationBusinessException{
		geracaoRelatoriosExcelON.validaRegras(vo);
	}
	
	public ScoAutorizacaoForn obterPorChavePrimaria(Integer pk) {
		return scoAutorizacaoFornDAO.obterPorChavePrimaria(pk);
	}
	//34348
    @Override
    public List<TipoMovimentoVO> obterTipoMovimentoListBox(){ 
		  return sceTipoMovimentosDAO.obterTipoMovimentoListBox();
    }
    
  	@Override
  	public void inserir(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) throws BaseException  {
  		sceDocumentoFiscalEntradaRN.inserir(sceDocumentoFiscalEntrada);		
  	}

  	@Override
  	public void atualizar(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) throws BaseException  {
  		sceDocumentoFiscalEntradaRN.atualizar(sceDocumentoFiscalEntrada);		
  	}
  	
  	@Override
	public List<SceTipoMovimento> obterTipoMovimentoPorSigla(String pesquisa){
		return sceTipoMovimentosDAO.obterTipoMovimentoPorSigla(pesquisa);
	}
  	
	@Override
	public void atualizarSceDocumentoFiscalEntrada(SceDocumentoFiscalEntrada elemento){
		sceDocumentoFiscalEntradaDAO.atualizar(elemento);
	}
	
	@Override
	public void atualizarDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada documentoFiscalEntrada)
			throws BaseException {
		sceDocumentoFiscalEntradaDAO.atualizar(documentoFiscalEntrada);
	}
	
	@Override
	public ScoFornecedor obterFornecedorPorCNPJ(Long cnpj) {
		return scoFornecedorDAO.obterFornecedorPorCNPJ(cnpj);
	}

	@Override
	public Long obterNotaFiscalItemRecebimento(Integer numRecebimento) {
		return sceNotaRecebProvisorioDAO.obterNotaPorNumeroRecebimento(numRecebimento);
	}

	@Override
	public RapServidores obterServidorAtivoPorUsuario(final String loginUsuario) throws ApplicationBusinessException {
		return rapServidoresDAO.obterServidorAtivoPorUsuario(loginUsuario);
	}
	
}
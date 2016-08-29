package br.gov.mec.aghu.compras.business;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.ScoAutorizacaoFornecedorPedidoRN;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoAdiantamentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoDataEnvioFornecedorVO;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.ScoUnidadeMedidaON;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.contaspagar.business.ConsultaGeralTitulosRN;
import br.gov.mec.aghu.compras.contaspagar.business.ConsultarTituloRN;
import br.gov.mec.aghu.compras.contaspagar.business.FcpPagamentosRealizadosPeriodoON;
import br.gov.mec.aghu.compras.contaspagar.business.FcpRelatorioDividaResumoON;
import br.gov.mec.aghu.compras.contaspagar.business.FcpRelatorioVencimentoFornecedorON;
import br.gov.mec.aghu.compras.contaspagar.business.FcpTipoDocumentoPagamentoON;
import br.gov.mec.aghu.compras.contaspagar.business.MovimentacaoFornecedorON;
import br.gov.mec.aghu.compras.contaspagar.business.RelatorioMovimentacaoFornecedorON;
import br.gov.mec.aghu.compras.contaspagar.business.SolicitacaoTituloRN;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpClassificacaoTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTipoTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaGeralTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroSolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.MovimentacaoFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfVO;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioDividaResumoVO;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioMovimentacaoFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloNrVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloSemLicitacaoVO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv1DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv2DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv3DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv4DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv5DAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoContaCorrenteFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoCriterioEscolhaFornDAO;
import br.gov.mec.aghu.compras.dao.ScoCumXProgrEntregaDAO;
import br.gov.mec.aghu.compras.dao.ScoDescricaoTecnicaPadraoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFnRamoComerClasDAO;
import br.gov.mec.aghu.compras.dao.ScoFormaPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoFornRamoComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorMarcaDAO;
import br.gov.mec.aghu.compras.dao.ScoGrupoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoGrupoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoAdvertFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoMultaFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoOcorrFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoSuspensFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaModeloDAO;
import br.gov.mec.aghu.compras.dao.ScoMateriaisClassificacoeJnDAO;
import br.gov.mec.aghu.compras.dao.ScoMateriaisClassificacoesDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDescTecnicaDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialJNDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialVinculoDAO;
import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoNomeComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoOrigemParecerTecnicoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPedItensMatExpedienteDAO;
import br.gov.mec.aghu.compras.dao.ScoPedidoMatExpedienteDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaServidorDAO;
import br.gov.mec.aghu.compras.dao.ScoPrazoPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAFParcelasDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoProgrAcessoFornAfpDAO;
import br.gov.mec.aghu.compras.dao.ScoProgrCodAcessoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoRamoComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoRefCodesDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSociosDAO;
import br.gov.mec.aghu.compras.dao.ScoSociosFornecedoresDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoCompraServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.dao.ScoTipoOcorrFornDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.compras.dao.VScoClasMaterialDAO;
import br.gov.mec.aghu.compras.dao.VScoComprMaterialDAO;
import br.gov.mec.aghu.compras.dao.VScoFornecedorDAO;
import br.gov.mec.aghu.compras.pac.business.ScoItemLicitacaoRN;
import br.gov.mec.aghu.compras.pac.business.ScoLicitacaoImpressaoRN;
import br.gov.mec.aghu.compras.pac.business.ScoLicitacaoON;
import br.gov.mec.aghu.compras.pac.business.ScoLicitacaoRN;
import br.gov.mec.aghu.compras.pac.vo.AtaRegistroPrecoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoCadastradaImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.compras.vo.AutFornEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornVO;
import br.gov.mec.aghu.compras.vo.CadastroContasCorrentesFornecedorVO;
import br.gov.mec.aghu.compras.vo.CadastroTipoOcorrenciaFornecedorVO;
import br.gov.mec.aghu.compras.vo.ClassificacaoVO;
import br.gov.mec.aghu.compras.vo.ContasCorrentesFornecedorVO;
import br.gov.mec.aghu.compras.vo.EntregaProgramadaGrupoMaterialVO;
import br.gov.mec.aghu.compras.vo.EtapaPACVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.FcpCalendarioVencimentoTributosVO;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.compras.vo.FiltroGrupoMaterialEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.FiltroPesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.FiltroRelatoriosExcelVO;
import br.gov.mec.aghu.compras.vo.HistoricoLogEtapaPacVO;
import br.gov.mec.aghu.compras.vo.ItemAutFornEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.compras.vo.JulgamentoPropostasLicitacaoVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.compras.vo.ModPacSolicCompraServicoVO;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralIAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPAFVO;
import br.gov.mec.aghu.compras.vo.PesquisaGeralPIAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoParcelaItemVO;
import br.gov.mec.aghu.compras.vo.PropostasVencedorasVO;
import br.gov.mec.aghu.compras.vo.ScoDescricaoTecnicaPadraoVO;
import br.gov.mec.aghu.compras.vo.ScoFornecedorVO;
import br.gov.mec.aghu.compras.vo.ScoPedidoMatExpedienteVO;
import br.gov.mec.aghu.compras.vo.ScoProgrCodAcessoFornVO;
import br.gov.mec.aghu.compras.vo.SolicitacaoDeComprasVO;
import br.gov.mec.aghu.compras.vo.VScoComprMaterialVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioAgrupadorItemFornecedorMarca;
import br.gov.mec.aghu.dominio.DominioAgruparRelMensal;
import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.dominio.DominioValidacaoNF;
import br.gov.mec.aghu.estoque.vo.GeracaoMovimentoEstoqueVO;
import br.gov.mec.aghu.estoque.vo.MarcaModeloMaterialVO;
import br.gov.mec.aghu.estoque.vo.MateriaisParalClassificacaoVO;
import br.gov.mec.aghu.estoque.vo.MaterialClassificacaoVO;
import br.gov.mec.aghu.estoque.vo.MaterialOpmeVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDiarioMateriaisComSaldoAteVinteDiasVO;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisEstocaveisGrupoCurvaAbcVO;
import br.gov.mec.aghu.estoque.vo.ScoServicoCriteriaVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.faturamento.vo.DadosConciliacaoVO;
import br.gov.mec.aghu.faturamento.vo.DadosMateriaisConciliacaoVO;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoAnexoDescricaoTecnica;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoClassifMatNiv1Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv2;
import br.gov.mec.aghu.model.ScoClassifMatNiv2Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv3;
import br.gov.mec.aghu.model.ScoClassifMatNiv3Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv4;
import br.gov.mec.aghu.model.ScoClassifMatNiv4Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoContatoFornecedorId;
import br.gov.mec.aghu.model.ScoCriterioEscolhaForn;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoEtapaPac;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.model.ScoFnRamoComerClasId;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedorMarca;
import br.gov.mec.aghu.model.ScoFornecedorMarcaId;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoHistoricoAdvertForn;
import br.gov.mec.aghu.model.ScoHistoricoAdvertFornId;
import br.gov.mec.aghu.model.ScoHistoricoMultaForn;
import br.gov.mec.aghu.model.ScoHistoricoMultaFornId;
import br.gov.mec.aghu.model.ScoHistoricoOcorrForn;
import br.gov.mec.aghu.model.ScoHistoricoOcorrFornId;
import br.gov.mec.aghu.model.ScoHistoricoSuspensForn;
import br.gov.mec.aghu.model.ScoHistoricoSuspensFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoeJn;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialDescTecnica;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.model.ScoMaterialVinculo;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPontoServidorId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoProgrAcessoFornAfp;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.ScoRefCodes;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.model.ScoSociosFornecedoresId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.suprimentos.vo.RelAutorizacaoFornecimentoSaldoEntregaVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPagamentoProposVO;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * Porta de entrada do módulo Compras.
 * 
 */
@Modulo(ModuloEnum.COMPRAS)
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
@Stateless
public class ComprasFacade extends BaseFacade implements IComprasFacade {
	
	@Inject
	private ScoFornRamoComercialDAO scoFornRamoComercialDAO;
	
	@Inject
	private FcpTituloDAO fcpTituloDAO;
	
	@Inject
	private ScoFnRamoComerClasDAO scoFnRamoComerClasDAO;
	
	@Inject
	private ScoCriterioEscolhaFornDAO scoCriterioEscolhaFornDAO;
	
	@Inject
	private FcpClassificacaoTituloDAO fcpClassificacaoTituloDAO;

	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private  CadastrarMateriaisRamoComercialFornecedorRN cadastrarMateriaisRamoComercialFornecedorRN;
	
	@EJB
	private CadastroMateriaisRamoComercialFornecedorON cadastroMateriaisRamoComercialFornecedorON;

	@EJB
	private ManterFornecedorRN manterFornecedorRN;
	
	@EJB
	private ConsultaGeralTitulosRN consultaGeralTitulosRN;
		
	@EJB
	private ScoGrupoServicoRN scoGrupoServicoRN;

	@EJB
	private ScoLicitacaoImpressaoRN scoLicitacaoImpressaoRN;

	@EJB
	private ScoUnidadeMedidaON scoUnidadeMedidaON;

	@EJB
	private AutorizacaoFornecimentoPendenteEntregaON autorizacaoFornecimentoPendenteEntregaON;

	@EJB
	private AutorizacaoFornecimentoPendenteEntregaRN autorizacaoFornecimentoPendenteEntregaRN;

	@EJB
	private ItemAutFornecimentoEntregasProgramadasRN itemAutFornecimentoEntregasProgramadasRN;

	@EJB
	private ImprimirPrevisaoProgramacaoRN imprimirPrevisaoProgramacaoRN;

	@EJB
	private AutFornecimentoEntregasProgramadasRN autFornecimentoEntregasProgramadasRN;

	@EJB
	private RelatorioExcelON relatorioExcelON;

	@EJB
	private GrupoMaterialEntregaProgramadaRN grupoMaterialEntregaProgramadaRN;

	@EJB
	private GrupoMaterialEntregaProgramadaON grupoMaterialEntregaProgramadaON;

	@EJB
	private PesquisaGeralAFON pesquisaGeralAFON;

	@EJB
	private AutFornecimentoEntregaProgramadaON autFornecimentoEntregaProgramadaON;

	@EJB
	private ItemAutFornecimentoEntregaProgramadaON itemAutFornecimentoEntregaProgramadaON;
	
	@EJB
	private ScoPedidoMatExpedienteRN scoPedidoMatExpedienteRN;

	@EJB
	private ManterPontoParadaServidorON manterPontoParadaServidorON;
	@EJB
	private ManterMarcaComercialON manterMarcaComercialON;
	@EJB
	private ManterNomesComerciaisMarcaRN manterNomesComerciaisMarcaRN;
	@EJB
	private ScoContatoFornecedorON scoContatoFornecedorON;
	@EJB
	private ScoServicoON scoServicoON;
	@EJB
	private ManterFornecedorON manterFornecedorON;
	@EJB
	FcpTipoDocumentoPagamentoON fcpTipoDocumentoPagamentoON;
	@EJB
	private ManterItemRmpsRN manterItemRmpsRN;
	@EJB
	private VisualizaAFsGeradasNaoEfetivadasON visualizaAFsGeradasNaoEfetivadasON;
	@EJB
	private ConsultaSCSSON consultaSCSSON;
	@EJB
	private ImprimirSolicitacaoDeComprasON imprimirSolicitacaoDeComprasON;
	@EJB
	private ScoFornecedorMarcaRN scoFornecedorMarcaRN;
	@EJB
	private ManterSceRmrPacienteON manterSceRmrPacienteON;
	@EJB
	private ManterItemRmpsON manterItemRmpsON;
	@EJB
	private ScoPedidoMatExpedienteON scoPedidoMatExpedienteON;
	@EJB
	private ScoContatoFornecedorRN scoContatoFornecedorRN;
	@EJB
	private ScoParcelasPagamentoRN scoParcelasPagamentoRN;
	@EJB
	private ScoCondicaoPagamentoProposRN scoCondicaoPagamentoProposRN;
	@EJB
	private ManterCadastroFornecedorON manterCadastroFornecedorON;
	@EJB
	private ScoFaseSolicitacaoRN scoFaseSolicitacaoRN;
	@EJB
	private AcessoFornecedorRN acessoFornecedorRN;
	@EJB
	private ScoMaterialDescTecnicaRN scoMaterialDescTecnicaRN;
	@EJB
	private VisualizarHistoricoMaterialON visualizarHistoricoMaterialON;
	@EJB
	private ImprimirJulgamentoPropostasLicitacaoON imprimirJulgamentoPropostasLicitacaoON;

	@EJB
	private ScoDescricaoTecnicaPadraoRN scoDescricaoTecnicaPadraoRN;

	@EJB
	private ScoMaterialVinculoRN scoMaterialVinculoRN;
	
	@EJB
	private ManterPenalidadeFornecedorON manterPenalidadeFornecedorON;

	@EJB
	private FcpTituloPendentePagamentoON tituloPendentePagamentoON;	
	
	@EJB
	private MovimentacaoFornecedorON movimentacaoFornecedorON;

	@Inject
	private ScoContaCorrenteFornecedorDAO scoContaCorrenteFornecedorDAO;

	@Inject
	private ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO;
	
	@EJB
	private ContasCorrentesFornecedorRN contasCorrentesFornecedorRN;
	
	@EJB
	private ScoLicitacaoRN scoLicitacaoRN;

	@EJB
	private ScoAutorizacaoFornecedorPedidoRN scoAutorizacaoFornecedorPedidoRN;

	@Inject
	private ScoDescricaoTecnicaPadraoDAO scoDescricaoTecnicaPadraoDAO;
	
	@Inject
	private ScoMaterialDescTecnicaDAO scoMaterialDescTecnicaDAO;
	
	@Inject 
	private ScoTipoOcorrFornDAO scoTipoOcorrFornDAO;
	
	@Inject 
	private ScoHistoricoAdvertFornDAO scoHistoricoAdvertFornDAO;
	
	@Inject 
	private ScoHistoricoMultaFornDAO scoHistoricoMultaFornDAO;
	
	@Inject 
	private ScoHistoricoOcorrFornDAO scoHistoricoOcorrFornDAO;
	
	@Inject 
	private	ScoHistoricoSuspensFornDAO scoHistoricoSuspensFornDAO;

	@Inject 
	private ScoSociosFornecedoresDAO scoSociosFornecedoresDAO;

	@Inject 
	private RapServidoresDAO rapServidoresDAO;

	@Inject
	private ScoProgrAcessoFornAfpDAO scoProgrAcessoFornAfpDAO;
	
	@Inject
	private ScoCumXProgrEntregaDAO scoCumXProgrEntregaDAO;

	@Inject
	private ScoProgrCodAcessoFornDAO scoProgrCodAcessoFornDAO;

	@Inject
	private ScoGrupoMaterialDAO scoGrupoMaterialDAO;

	@Inject
	private ScoMateriaisClassificacoesDAO scoMateriaisClassificacoesDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	@Inject
	private ScoMarcaModeloDAO scoMarcaModeloDAO;

	@Inject
	private ScoLoteLicitacaoDAO scoLoteLicitacaoDAO;
	
	@Inject
	private FcpTipoTituloDAO tipoTituloDAO;

	@Inject
	private ScoServicoDAO scoServicoDAO;

	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	@Inject
	private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;

	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;

	@Inject
	private ScoMaterialVinculoDAO scoMaterialVinculoDAO;

	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;

	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	@Inject
	private VScoFornecedorDAO vScoFornecedorDAO;

	@Inject
	private ScoMateriaisClassificacoeJnDAO scoMateriaisClassificacoeJnDAO;

	@Inject
	private ScoFornecedorMarcaDAO scoFornecedorMarcaDAO;

	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

	@Inject
	private ScoMaterialJNDAO scoMaterialJNDAO;

	@Inject
	private ScoPontoParadaServidorDAO scoPontoParadaServidorDAO;

	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;

	@Inject
	private ScoRefCodesDAO scoRefCodesDAO;

	@Inject
	private ScoSolicitacaoCompraServicoDAO scoSolicitacaoCompraServicoDAO;

	@Inject
	private ScoFormaPagamentoDAO scoFormaPagamentoDAO;

	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;

	@Inject
	private ScoNomeComercialDAO scoNomeComercialDAO;

	@Inject
	private ScoOrigemParecerTecnicoDAO scoOrigemParecerTecnicoDAO;

	@Inject
	private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;

	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;

	@Inject
	private ScoClassifMatNiv5DAO scoClassifMatNiv5DAO;

	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;

	@Inject
	private ScoGrupoServicoDAO scoGrupoServicoDAO;

	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	@Inject
	private VScoComprMaterialDAO vScoComprMaterialDAO;

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	@Inject
	private ScoPrazoPagamentoDAO scoPrazoPagamentoDAO;

	@Inject
	private ScoMarcaComercialDAO scoMarcaComercialDAO;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private VScoClasMaterialDAO vScoClasMaterialDAO;

	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	@Inject
	private ScoPedItensMatExpedienteDAO scoPedItensMatExpedienteDAO;

	@Inject
	private ScoProgEntregaItemAFParcelasDAO scoProgEntregaItemAFParcelasDAO;

	@EJB
	private MaterialFarmaciaON materialFarmaciaON;

	@EJB
	private ProgramacaoAutomaticaRN programacaoAutomaticaRN;

	@EJB
	private FcpRelatorioVencimentoFornecedorON fcpRelatorioVencimentoFornecedorON;

	@EJB
	private RelatorioMovimentacaoFornecedorON relatorioMovimentacaoFornecedorON;
	
	@EJB
	private PlanejamentoAndamentoPacRN planejamentoAndamentoPacRN;
	
	@EJB
	private FcpCalendarioVencimentoTributosON fcpCalendarioVencimentoTributosON;

	@Inject
	private ScoSociosDAO scoSociosDAO;
	
	@EJB
	private CadastrarMarcaModeloMaterialRN cadastrarMarcaModeloMaterialRN;

	@EJB
	private ScoGrupoMaterialRN scoGrupoMaterialRN;

	@EJB
	private ScoClassifMatRN scoClassifMatRN;
	
	@EJB
	private ScoCriterioEscolhaFornRN scoCriterioEscolhaFornRN;

	@Inject
	private ScoClassifMatNiv1DAO scoClassifMatNiv1DAO;

	@Inject
	private ScoClassifMatNiv2DAO scoClassifMatNiv2DAO;
	
	@Inject
	private ScoClassifMatNiv3DAO scoClassifMatNiv3DAO;

	@Inject
	private ScoClassifMatNiv4DAO scoClassifMatNiv4DAO;
	
	@EJB
	private ConsultarPropostasVencedorasRN consultarPropostasVencedorasRN;
	
	@EJB
	private CertificadoRegistroCadastroON certificadoRegistroCadastroON;
	
	@EJB
	private ConsultarTituloRN consultarTituloRN;

	@EJB
	private FcpRelatorioDividaResumoON fcpRelatorioDividaResumoON;
	
	@Inject
	private ScoRamoComercialDAO scoRamoComercialDAO;
	
	@EJB
	private FcpPagamentosRealizadosPeriodoON fcpPagamentosRealizadosPeriodoON;

	@EJB
	private CadastroTipoOcorrenciaFornecedorRN cadastroTipoOcorrenciaFornecedorRN;
	
	@Inject
	private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;
	
	@EJB
	private ScoLicitacaoON scoLicitacaoON;
	
	@EJB
	private ScoItemLicitacaoRN scoItemLicitacaoRN;
	
	@EJB
	private SolicitacaoTituloRN solicitacaoTituloRN;
	
	
	private static final long serialVersionUID = -2078824992788167621L;

	protected ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	@Override
	public List<ScoMaterial> listarScoMateriais(Object objPesquisa, Boolean indEstocavel) {
		return this.getScoMaterialDAO().listarScoMateriais(objPesquisa, indEstocavel);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	@Override
	public List<ScoMaterial> listarScoMateriais(Object objPesquisa, Boolean indEstocavel, final Boolean pesquisarDescricao) {
		return this.getScoMaterialDAO().listarScoMateriais(objPesquisa, indEstocavel, pesquisarDescricao);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pelo nome, descricao ou código.
	 * 
	 * @param objPesquisa
	 * @param indEstocavel
	 * @param pesquisarDescricao
	 * @return count
	 */
	@Override
	public Long listarScoMatriaisAtivosCount(Object objPesquisa) {
		return this.getScoMaterialDAO().listarScoMatriaisAtivosCount(objPesquisa);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @return List<ScoMaterial>
	 */
	@Override
	public List<ScoMaterial> listarScoMateriaisAtivos(Object objPesquisa) {
		return this.getScoMaterialDAO().listarScoMateriaisAtivos(objPesquisa);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo.
	 * 
	 * @param objPesquisa
	 * @param colunaOrdenacao
	 * @return List<ScoMaterial>
	 */
	@Override
	public List<ScoMaterial> listarScoMateriaisAtivos(Object objPesquisa, String colunaOrdenacao) {
		return this.getScoMaterialDAO().listarScoMateriaisAtivos(objPesquisa, colunaOrdenacao);
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por ScoMateriais,
	 * filtrando pela descricao ou pelo codigo. Ordenado por material.nome
	 * 
	 * @param objPesquisa
	 * @return
	 */
	@Override
	public List<ScoMaterial> pesquisarMateriais(Object objPesquisa) {
		return getScoMaterialDAO().pesquisarMateriais(objPesquisa);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pela descricao ou pelo codigo. Join com unidade
	 * medida e situação = A
	 * 
	 * @param objPesquisa
	 * @return count
	 */
	@Override
	public Long listarScoMateriaisCount(Object objPesquisa) {
		return this.getScoMaterialDAO().listarScoMateriaisCount(objPesquisa, null);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por
	 * ScoMateriais, filtrando pelo nome, descricao ou código.
	 * 
	 * @param objPesquisa
	 * @param indEstocavel
	 * @param pesquisarDescricao
	 * @return count
	 */
	public Long listarScoMateriaisCount(final Object objPesquisa, final Boolean indEstocavel, final Boolean pesquisarDescricao) {
		return this.getScoMaterialDAO().listarScoMateriaisCount(objPesquisa, indEstocavel, pesquisarDescricao);
	}

	/**
	 * Retorna fornecedores de acordo com número ou razão social.
	 * 
	 * @param objPesquisa
	 * @return Lista de fornecedor
	 */
	@Override
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(Object objPesquisa) {
		return getScoFornecedorDAO().pesquisarFornecedoresPorNumeroRazaoSocial(objPesquisa);
	}

	@Override
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocialNomeFantasia(Object parametro, int firstResult, int maxResults) {
		return getScoFornecedorDAO().pesquisarFornecedoresPorNumeroRazaoSocialNomeFantasia(parametro, firstResult, maxResults);
	}

	/**
	 * Recupera uma lista de grupos de material por código ou descrição
	 * 
	 * @param parametro
	 *            Código ou descrição
	 * @return Lista de Grupos de Material
	 */
	@Override
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(Object parametro) {
		return getScoGrupoMaterialDAO().pesquisarGrupoMaterialPorFiltro(parametro);
	}

	@Override
	public List<ScoMaterial> listarScoMateriaisPorGrupoEspecifico(Object objPesquisa, Integer firstResult, Integer maxResults,
			String orderProperty, Boolean asc, Integer pGrFarmIndustrial, Integer pGrMatMedic) {
		return getScoMaterialDAO().listarScoMateriaisPorGrupoEspecifico(objPesquisa, firstResult, maxResults, orderProperty, asc,
				pGrFarmIndustrial, pGrMatMedic);
	}

	@Override
	public DominioTipoFaseSolicitacao obterTipoFaseSolicitacaoPorNumeroAF(final Integer numeroAf) {
		return this.getScoFaseSolicitacaoDAO().obterTipoFaseSolicitacaoPorNumeroAF(numeroAf);
	}

	@Override
	public ScoMaterialJN inserirScoMaterialJn(ScoMaterialJN materialJN) {
		getScoMaterialJNDAO().persistir(materialJN);
		return materialJN;
	}

	private ScoMaterialJNDAO getScoMaterialJNDAO() {
		return scoMaterialJNDAO;
	}

	@Override
	public ScoMaterial obterScoMaterial(Object chavePrimaria) {
		return getScoMaterialDAO().obterPorChavePrimaria(chavePrimaria, true, ScoMaterial.Fields.GRUPO_MATERIAL );
	}

	private ScoGrupoMaterialDAO getScoGrupoMaterialDAO() {
		return scoGrupoMaterialDAO;
	}

	private ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}

	private ScoPontoParadaServidorDAO getScoPontoParadaServidorDAO() {
		return scoPontoParadaServidorDAO;
	}

	@Override
	public Long listarScoMateriaisPorGrupoEspecificoCount(Object objPesquisa, Integer pGrFarmIndustrial, Integer pGrMatMedic) {
		return getScoMaterialDAO().listarScoMateriaisPorGrupoEspecificoCount(objPesquisa, pGrFarmIndustrial, pGrMatMedic);
	}

	@Override
	public List<ScoMaterial> listarMateriaisAtivos(String param, Boolean indEstocavel) {
		return getScoMaterialDAO().listarMaterialAtivo(param, indEstocavel, null);
	}

	@Override
	public Long listarMateriaisAtivosCount(String param, Boolean indEstocavel) {
		return getScoMaterialDAO().listarMaterialAtivoCount(param, indEstocavel);
	}

	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}

	private ScoContatoFornecedorDAO getScoContatoFornecedorDAO() {
		return scoContatoFornecedorDAO;
	}

	@Override
	public Long contarScoMateriaisGrupoAtiva(Object objPesquisa, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao) {
		return this.getScoMaterialDAO().contarScoMateriaisGrupoAtiva(objPesquisa, grupoMat, pesquisarDescricao);
	}

	@Override
	public void atualizarScoFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao, ScoFaseSolicitacao faseSolicitacaoOld) throws BaseException {
		this.getScoFaseSolicitacaoRN().atualizar(faseSolicitacao, faseSolicitacaoOld);
	}

	protected ScoFaseSolicitacaoRN getScoFaseSolicitacaoRN() {
		return scoFaseSolicitacaoRN;
	}

	@Override
	public ScoItemPropostaFornecedor obterItemPropostaFornPorChavePrimaria(ScoItemPropostaFornecedorId id) {
		return getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(id);
	}

	public ScoUnidadeMedida obterUnidadeMedidaPorSeq(String seq) {
		return this.getScoUnidadeMedidaDAO().obterOriginal(seq);
	}

	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

	// # 6726 - Eduardo Ando

	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	// # 5260 - Eduardo Ando

	private ImprimirSolicitacaoDeComprasON getImprimirSolicitacaoDeComprasON() {
		return imprimirSolicitacaoDeComprasON;
	}

	@Override
	public List<SolicitacaoDeComprasVO> listarSolicitacoesDeComprasPorNumero(List<Integer> numSolicComp)
			throws ApplicationBusinessException, ApplicationBusinessException {
		return getImprimirSolicitacaoDeComprasON().pesquisaDadosSolicitacaoCompras(numSolicComp);
	}

	@Override
	public List<ScoFornecedor> obterFornecedor(String param) {
		return getScoFornecedorDAO().obterFornecedor(param);
	}

	@Override
	public List<ScoMarcaComercial> obterMarcas(Object param) {
		return getScoMarcaComercialDAO().obterMarcas(param);
	}

	@Override
	public void persistirFornecedorMarca(ScoFornecedorMarca scoFornecedorMarca) throws ApplicationBusinessException {
		getScoFornecedorMarcaRN().persistirFornecedorMarca(scoFornecedorMarca);
	}

	@Override
	public ScoFornecedorMarca obterScoFornecedorMarcaPorId(ScoFornecedorMarcaId id) {
		return getScoFornecedorMarcaDAO().obterPorChavePrimaria(id, true, ScoFornecedorMarca.Fields.MARCA_COMERCIAL);
	}

	@Override
	public void excluirScoFornecedorMarca(ScoFornecedorMarca fornecedorMarcaExcluir) throws ApplicationBusinessException {
		getScoFornecedorMarcaRN().remover(fornecedorMarcaExcluir);
	}

	@Override
	public List<ScoFornecedorMarca> listaFornecedorMarca(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoFornecedor fornecedor, String descricaoMarca) {
		return getScoFornecedorMarcaDAO().pesquisarFornecedorMarca(firstResult, maxResult, orderProperty, asc, fornecedor, descricaoMarca);
	}

	@Override
	public Long listaFornecedorMarcaCount(ScoFornecedor scoFornecedor) {
		return getScoFornecedorMarcaDAO().fornecedorMarcaCount(scoFornecedor);
	}

	private ScoFornecedorMarcaDAO getScoFornecedorMarcaDAO() {
		return scoFornecedorMarcaDAO;
	}

	private ScoFornecedorMarcaRN getScoFornecedorMarcaRN() {
		return scoFornecedorMarcaRN;
	}

	private ScoMarcaComercialDAO getScoMarcaComercialDAO() {
		return scoMarcaComercialDAO;
	}

	/**
	 * @author lucasbuzzo #5521
	 * 
	 * @param Integer
	 *            nroLicitacao, Integer itemInicial, Integer itemFinal
	 * @return imprimirJulgamentoPropostasLicitacaoVO
	 */
	@Override
	public List<JulgamentoPropostasLicitacaoVO> gerarRelatorioJulgamentoPropostasLicitacao(Integer nroLicitacao, Integer itemInicial,
			Integer itemFinal) {
		return imprimirJulgamentoPropostasLicitacaoON.gerarRelatorioSamberg(nroLicitacao, itemInicial, itemFinal);
	}

	@Override
	public void inserirScoFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws BaseException {
		getScoFaseSolicitacaoRN().inserir(faseSolicitacao);
	}
	
	@Override
	public void inserirScoFaseSolicitacaoSemRn(ScoFaseSolicitacao faseSolicitacao, Boolean flush) throws BaseException {
		this.getScoFaseSolicitacaoDAO().persistir(faseSolicitacao);
		if (flush) {
			this.getScoFaseSolicitacaoDAO().flush();
		}
	}
	
	
	
	@Override
	public List<ScoFornecedor> listarFornecedoresAtivos(Object pesquisa, Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getScoFornecedorDAO().listarFornecedoresAtivos(pesquisa, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public List<FornecedorVO> pesquisarFornecedoresAtivosVO(final Object pesquisa,
			final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		return getScoFornecedorDAO().pesquisarFornecedoresAtivosVO(pesquisa, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public void processarPedidosPapelaria(Date date, String cron, RapServidores servidorLogado) throws ApplicationBusinessException {
		// o timeout (e a propria transacao) sao controlados na RN
		getScoPedidoMatExpedienteRN().processarPedidosPapelaria(servidorLogado);
	}
	
	@Override
	public List<ScoGrupoServico> listarGrupoServico(Object pesquisa) {
		List<ScoGrupoServico> grupoServico = getScoGrupoServicoDAO().listarGrupoServico(pesquisa);
		return grupoServico;
	}

	protected ScoGrupoServicoDAO getScoGrupoServicoDAO() {
		return scoGrupoServicoDAO;
	}

	@Override
	public List<ScoMaterial> listarScoMateriaisGrupoAtiva(Object param, ScoGrupoMaterial grupoMat, Boolean pesquisarDescricao, Boolean orderNome)
			throws BaseException {
		return getScoMaterialDAO().listarScoMateriaisGrupoAtiva(param, grupoMat, pesquisarDescricao, orderNome);
	}

	@Override
	public List<ScoServico> listarServicosByNomeOrCodigoGrupoAtivo(Object param, ScoGrupoServico grupoSrv) throws BaseException {
		return scoServicoDAO.listarServicosByNomeOrCodigoGrupoAtivo(param, grupoSrv);
	}

	@Override
	public Long listarServicosByNomeOrCodigoGrupoAtivoCount(Object param, ScoGrupoServico grupoSrv) throws BaseException {
		return scoServicoDAO.listarServicosByNomeOrCodigoGrupoAtivoCount(param, grupoSrv);
	}

	@Override
	public Long contarServicos(ScoServicoCriteriaVO criteria) {
		return getScoServicoDAO().pesquisarServicoCount(criteria);
	}

	@Override
	public List<ScoServico> pesquisarScoServico(ScoServicoCriteriaVO criteria, Integer first, Integer max, String order, boolean asc) {
		return getScoServicoDAO().pesquisar(criteria, first, max, order, asc);
	}

	protected ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}

	@Override
	public List<ScoMarcaComercial> getListaMarcasByNomeOrCodigo(Object paramString) throws BaseException {
		return getScoMarcaComercialDAO().listarScoMarcasAtiva(paramString);
	}

	@Override
	public List<ScoMarcaComercial> getListaMarcasByNomeOrCodigo(Object objPesquisa, Integer firstResult, Integer maxResults,
			String orderProperty, Boolean asc) throws BaseException {
		return getScoMarcaComercialDAO().listarScoMarcasAtiva(objPesquisa, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long getListaMarcasByNomeOrCodigoCount(Object objPesquisa) {
		return getScoMarcaComercialDAO().listarScoMarcasAtivaCount(objPesquisa);
	}

	@Override
	public List<ScoMaterial> getListaMaterialByNomeOrCodigo(Object param) throws BaseException {
		return getScoMaterialDAO().listarScoMateriaisAtiva(param, Boolean.FALSE);
	}

	@Override
	public List<ScoMaterial> pesquisarMaterialPorFiltro(Object _input) {
		return getScoMaterialDAO().pesquisarMaterialPorFiltro(_input);
	}

	@Override
	public Long pesquisarMateriaisPorFiltroCount(Object _input) {
		return this.getScoMaterialDAO().pesquisarMaterialPorFiltroCount(_input);
	}

	@Override
	public List<ScoServico> listarServicosAtivos(Object pesquisa) {
		return getScoServicoDAO().listarServicosAtivos(pesquisa);
	}

	@Override
	public Long listarServicosAtivosCount(Object pesquisa) {
		return scoServicoDAO.listarServicosAtivosCount(pesquisa);
	}

	@Override
	public Long listarFornecedoresAtivosCount(Object pesquisa) {
		return getScoFornecedorDAO().listarFornecedoresAtivosCount(pesquisa);
	}

	@Override
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorFiltro(Object _input) {

		return this.getScoGrupoMaterialDAO().pesquisarGrupoMaterialPorFiltro(_input);
	}

	@Override
	public Long pesquisarGrupoMaterialPorFiltroCount(Object _input) {
		return this.getScoGrupoMaterialDAO().pesquisarGrupoMaterialPorFiltroCount(_input);
	}

	@Override
	public List<ScoFornecedor> listarFornecedoresAtivos(Object pesquisa) {
		return getScoFornecedorDAO().listarFornecedoresAtivos(pesquisa);
	}

	@Override
	public Boolean verificarMaterialImobilizado(Integer matCodigo) {
		return getScoMaterialDAO().verificarMaterialImobilizado(matCodigo);
	}

	@Override
	public List<ScoOrigemParecerTecnico> obterOrigemParecerTecnico(Object objPesquisa) {
		return this.scoOrigemParecerTecnicoDAO.obterOrigemParecerTecnicoPorSeqDescricao(objPesquisa);
	}

	@Override
	public Long obterOrigemParecerTecnicoCount(Object objPesquisa) {
		return this.scoOrigemParecerTecnicoDAO.obterOrigemParecerTecnicoPorSeqDescricaoCount(objPesquisa);
	}

	@Override
	public ScoClassifMatNiv5 obterScoClassifMatNiv5PorSeq(Long numero) {
		return scoClassifMatNiv5DAO.obterPorChavePrimaria(numero);
	}

	@Override
	public VScoClasMaterial obterVScoClasMaterialPorNumero(Long numero) {
		return vScoClasMaterialDAO.obterVScoClasMaterialPorNumero(numero);
	}

	@Override
	public List<VScoClasMaterial> pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(Object param, Short gmtCodigo) {
		return vScoClasMaterialDAO.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, gmtCodigo);
	}

	@Override
	public Boolean verificarComprasWeb(AghParametros param, ScoMaterial material) {
		return getScoMateriaisClassificacoesDAO().verificarComprasWeb(param, material);
	}

	@Override
	public List<ScoGrupoMaterial> obterGrupoMaterialPorSeqDescricao(Object param) {
		return getScoGrupoMaterialDAO().obterGrupoMaterialPorSeqDescricao(param);
	}

	@Override
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorFiltroAlmoxarifado(Short almoxSeq, Object _input) {
		return getScoGrupoMaterialDAO().pesquisarGrupoMaterialPorFiltroAlmoxarifado(almoxSeq, _input);
	}

	@Override
	public List<ScoMaterial> pesquisaMateriaisPorParamAlmox(Short almoxSeq, String paramPesq) {
		return getScoMaterialDAO().pesquisaMateriaisPorParamAlmox(almoxSeq, paramPesq);
	}

	@Override
	public List<ScoMaterial> obterMateriais(Object param) {
		return getScoMaterialDAO().listarScoMateriais(param, null);
	}

	@Override
	public List<ScoFornecedor> obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(String param, Short almoxSeq, Integer materialCodigo) {
		return getScoFornecedorDAO().obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(param, almoxSeq, materialCodigo);
	}

	@Override
	public ScoMaterial obterMaterialPorId(Integer codigo) {
		return this.getScoMaterialDAO().obterMaterialPorId(codigo);
	}

	@Override
	public ScoFornecedor obterFornecedorPorChavePrimaria(Integer numero) {
		return this.getScoFornecedorDAO().obterPorChavePrimaria(numero);
	}

	@Override
	public ScoServico obterServicoPorId(Integer codigo) {
		return this.getScoServicoDAO().obterServicoPorId(codigo);
	}

	@Override
	public ScoItemAutorizacaoForn obterItemAutorizacaoFornPorId(Integer afnNumero, Integer numero) {
		return this.scoItemAutorizacaoFornDAO.obterItemAutorizacaoFornPorId(afnNumero, numero);
	}

	@Override
	public Long pesquisarManterMaterialCount(ScoMaterial material,VScoClasMaterial classificacaoMaterial) {
		return getScoMaterialDAO().pesquisarManterMaterialCount(material, classificacaoMaterial);
	}

	@Override
	public List<ScoMaterial> pesquisarManterMaterial(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoMaterial material, VScoClasMaterial classificacaoMaterial) {
		return getScoMaterialDAO().pesquisarManterMaterial(firstResult, maxResult, orderProperty, asc, material,classificacaoMaterial);
	}

	@Override
	public void excluirFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) throws ApplicationBusinessException {
		this.getScoFaseSolicitacaoRN().excluirFaseSolicitacao(faseSolicitacao);
	}

	@Override
	public String montarRazaoSocialFornecedor(ScoFornecedor fornecedor) {
		return this.getScoFornecedorDAO().montarRazaoSocialFornecedor(fornecedor);
	}

	@Override
	@BypassInactiveModule
	public String obterCnpjCpfFornecedorFormatado(ScoFornecedor fornecedor) {
		return this.getScoFornecedorDAO().obterCnpjCpfFornecedorFormatado(fornecedor);
	}

	@Override
	public String obterFornecedorDescricao(Integer numero) {
		return this.getScoFornecedorDAO().obterFornecedorDescricao(numero);
	}

	/**
	 * Retorna a quantidade e itens da lista de materiais para catálogo
	 * 
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 */
	@Override
	public Long pesquisarListaMateriaisParaCatalogoCount(Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial,
			DominioSimNao estocavel, DominioSimNao generico, String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ,
			Integer codigoGrupoMaterial, Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {

		return getScoMaterialDAO().pesquisarListaMateriaisParaCatalogoCount(codigoMaterial, nomeMaterial, situacaoMaterial, estocavel,
				generico, codigoUnidadeMedida, classifyXYZ, codigoGrupoMaterial, codCatMat, codMatAntigo, classificacaoMaterial);
	}

	@Override
	public List<ScoMaterial> pesquisarMaterial(Object _input) {
		return getScoMaterialDAO().pesquisarMaterial(_input);
	}

	// Estória # 6795
	@Override
	public List<RelAutorizacaoFornecimentoSaldoEntregaVO> obterListaAutorizacaoFornecimentoSaldoEntrega(final Integer lctNumero,
			final Integer sequenciaAlteracao, final Short nroComplemento) {
		return scoAutorizacaoFornJnDAO.obterListaAutorizacaoFornecimentoSaldoEntrega(lctNumero, sequenciaAlteracao, nroComplemento);
	}

	// Estória # 6795
	@Override
	public ScoMarcaComercial obterMarcaComercialPorCodigo(final Integer mcmCodigo) {
		return getScoMarcaComercialDAO().obterMarcaComercialPorCodigo(mcmCodigo);
	}

	// Estória # 6795
	@Override
	public ScoNomeComercial obterNomeComercialPorMcmCodigoNumero(Integer mcmCodigo, Integer numero) {
		return scoNomeComercialDAO.obterNomeComercialPorMcmCodigoNumero(mcmCodigo, numero);
	}

	// Estória # 6795
	@Override
	public ScoSolicitacaoServico obterDescricaoSolicitacaoServicoPeloId(Integer numero) throws ApplicationBusinessException {
		return scoSolicitacaoServicoDAO.obterDescricaoSolicitacaoServicoPeloId(numero);
	}

	// Estória # 6795
	@Override
	public Long pesquisarNumeroParcelasCount(Integer cdpNumero) {
		return scoPrazoPagamentoDAO.pesquisarNumeroParcelasCount(cdpNumero);
	}

	@Override
	public ScoGrupoMaterial obterGrupoMaterialPorId(Integer id) {
		if (id == null) {
			return null;
		}
		return getScoGrupoMaterialDAO().obterPorChavePrimaria(id);
	}

	/**
	 * Método de pesquisa para a página de lista. Pesquisa por código e/ou
	 * descrição
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param marcaComercial
	 * @return
	 */
	@Override
	public List<ScoMarcaComercial> listaMarcaComercial(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoMarcaComercial marcaComercial) {
		return this.getScoMarcaComercialDAO().pesquisarMarcaComercial(firstResult, maxResult, orderProperty, asc, marcaComercial);
	}

	@Override
	public Long pesquisarMarcaComecialCount(ScoMarcaComercial marcaComercial) {
		return this.getScoMarcaComercialDAO().pesquisarMarcaComercialCount(marcaComercial);
	}

	@Override
	public ScoMarcaComercial buscaScoMarcaComercialPorId(Integer codigo) {
		return this.getScoMarcaComercialDAO().buscarScoMarcaComercialPorId(codigo);
	}

	@Override
	public ScoMarcaModelo buscaScoMarcaModeloPorId(Integer seqp, Integer codigo) {
		return this.scoMarcaModeloDAO.buscarScoMarcaModeloPorId(seqp, codigo);
	}

	@Override
	public List<ScoMarcaModelo> listaMarcaModelo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigo) {
		return scoMarcaModeloDAO.pesquisarMarcaModelo(firstResult, maxResult, orderProperty, asc, codigo);
	}

	@Override
	public Long pesquisarMarcaModeloCount(Integer codigo) {
		return this.scoMarcaModeloDAO.pesquisarMarcaModeloCount(codigo);
	}

	@Override
	public ScoUnidadeMedida obterUnidadeMedidaPorId(String idUnidade) {
		return scoUnidadeMedidaON.obterUnidadeMedidaPorId(idUnidade);
		}

	@Override
	public void persistirParcelaPagamento(ScoParcelasPagamento parcela) throws ApplicationBusinessException {
		scoParcelasPagamentoRN.persistir(parcela);
	}

	@Override
	public ScoFornecedor obterFornecedorPorNumeroAf(Integer numeroAf) {
		return this.getScoFornecedorDAO().obterFornecedorPorNumeroAf(numeroAf);
	}

	/**
	 * Obtém uma lista de ScoRefCodes que possuem o campo rvDomain = ao
	 * parâmetro informado.
	 * 
	 * @param rvDomain
	 * @return
	 * @author bruno.mourao
	 * @since 07/02/2012
	 */
	@Override
	public List<ScoRefCodes> buscarScoRefCodesPorDominio(String rvDomain) {
		return getScoRefCodesDAO().buscarScoRefCodesPorDominio(rvDomain);
	}

	/**
	 * Busca utilizando os campos que estiverem preenchidos
	 * 
	 * @param refCode
	 * @param valorExato
	 *            - True indica que o operador nos campos sera =, False indica
	 *            que o like será utilizado
	 * @return
	 * @author bruno.mourao
	 * @since 28/03/2012
	 */
	@Override
	public List<ScoRefCodes> pesquisarScoRefCodesPorScoRefCodes(ScoRefCodes refCode, Boolean valorExato) {
		return getScoRefCodesDAO().buscarScoRefCodesPorRefCodes(refCode, valorExato);

	}

	protected ScoRefCodesDAO getScoRefCodesDAO() {
		return scoRefCodesDAO;
	}

	protected ManterCadastroFornecedorON getManterCadastroFornecedorON() {
		return manterCadastroFornecedorON;
	}

	/**
	 * Pesquisa fornecedor por vários campos de fornecedor que estejam
	 * preenchidos.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fornecedor
	 * @return
	 * @author bruno.mourao
	 * @since 08/03/2012
	 */
	@Override
	public List<ScoFornecedor> pesquisarFornecedorCompleta(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc,
			ScoFornecedor fornecedor, String cpfCnpj) {
		return getManterCadastroFornecedorON().pesquisarFornecedorCompleta(firstResult, maxResult, orderProperty, asc, fornecedor, cpfCnpj);
	}

	@Override
	public Long pesquisarFornecedorCompletaCount(ScoFornecedor fornecedor, String cpfCnpj) {
		return getManterCadastroFornecedorON().pesquisarFornecedorCompletaCount(fornecedor, cpfCnpj);
	}

	/**
	 * Persite o fornecedor. Se id = null um novo objeto é inserido, caso
	 * contrário, é atualizado. Flush já é executado.
	 * 
	 * @param fornecedor
	 * @author bruno.mourao
	 * @param oldFornecedor
	 * @throws ApplicationBusinessException
	 * @throws BaseListException
	 * @since 20/03/2012
	 */
	@Override
	public void persistirFornecedor(ScoFornecedor fornecedor) throws ApplicationBusinessException, BaseListException {
		getManterCadastroFornecedorON().persistirFornecedor(fornecedor);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> listarSolicitacoesDeComprasPorCodigoPaciente(Integer pacCodigo) {
		return getScoSolicitacoesDeComprasDAO().listarSolicitacoesDeComprasPorCodigoPaciente(pacCodigo);
	}

	@Override
	public List<ScoMaterial> obterMateriaisOrteseseProteses(BigDecimal paramVlNumerico, Object objPesquisa) {
		return this.getScoMaterialDAO().obterMateriaisOrteseseProteses(paramVlNumerico, objPesquisa);
	}

	@Override
	public List<ScoMaterial> obterMateriaisOrteseseProtesesAgenda(BigDecimal paramVlNumerico, String objPesquisa) {
		return this.getScoMaterialDAO().obterMateriaisOrteseseProtesesAgenda(paramVlNumerico, objPesquisa);
	}

	@Override
	public Long obterMateriaisOrteseseProtesesAgendaCount(BigDecimal paramVlNumerico, String objPesquisa) {
		return this.getScoMaterialDAO().obterMateriaisOrteseseProtesesAgendaCount(paramVlNumerico, objPesquisa);
	}

	@Override
	public List<ScoMaterial> obterMateriaisRMAutomatica(Integer gmtCodigo, String nome) {
		return this.getScoMaterialDAO().obterMateriaisRMAutomatica(gmtCodigo, nome);
	}

	@Override
	public List<ScoMaterial> obterMateriaisRMAutomatica(Integer gmtCodigo, String nome, int maxResults) {
		return this.getScoMaterialDAO().obterMateriaisRMAutomatica(gmtCodigo, nome, maxResults);
	}

	@Override
	public List<ScoMaterial> listarScoMateriaisAtiva(Object objPesquisa) {
		return this.getScoMaterialDAO().listarScoMateriaisAtiva(objPesquisa, Boolean.FALSE);
	}

	@Override
	public List<RelUltimasComprasPACVO> obterItensRelatorioUltimasComprasMaterial(Integer numLicitacao, List<Short> itens,
			List<String> itensModalidade , DominioAgrupadorItemFornecedorMarca agrupador) {
		return this.getScoMaterialDAO().obterItensRelatorioUltimasComprasMaterial(numLicitacao, itens, itensModalidade, agrupador);
	}

	@Override
	public Long getFatItensContaHospitalarComMateriaisOrteseseProteses(Integer cthSeq, Short seq, Integer phiSeq, Integer codGrupo) {
		return this.getScoMaterialDAO().getFatItensContaHospitalarComMateriaisOrteseseProteses(cthSeq, seq, phiSeq, codGrupo);
	}

	@Override
	public ScoMaterial obterMaterialPorCodigoSituacao(ScoMaterial material) {
		return this.getScoMaterialDAO().obterMaterialPorCodigoSituacao(material);
	}

	@Override
	public Boolean existeMaterialEstocavelPorAlmoxarifadoCentral(Integer codigoMaterial, Short seqAlmoxarifado, Boolean indEstocavel) {
		return this.getScoMaterialDAO().existeMaterialEstocavelPorAlmoxarifadoCentral(codigoMaterial, seqAlmoxarifado, indEstocavel);
	}

	@Override
	public List<ScoMaterial> pesquisarListaMateriaisParaCatalogo(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial, DominioSimNao estocavel,
			DominioSimNao generico, String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ, Integer codigoGrupoMaterial,
			Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {
		return this.getScoMaterialDAO().pesquisarListaMateriaisParaCatalogo(firstResult, maxResults, orderProperty, asc, codigoMaterial,
				nomeMaterial, situacaoMaterial, estocavel, generico, codigoUnidadeMedida, classifyXYZ, codigoGrupoMaterial, codCatMat,
				codMatAntigo, classificacaoMaterial);
	}

	@Override
	public Date recuperarUltimaDataGeracaoMovMaterial(Integer matCodigo, Short almSeq) {
		return this.getScoMaterialDAO().recuperarUltimaDataGeracaoMovMaterial(matCodigo, almSeq);
	}

	@Override
	public RelatorioDiarioMateriaisComSaldoAteVinteDiasVO obterLicitacoesRelatorioMateriasAteVinteDias(Integer slcNumero) {
		return this.getScoMaterialDAO().obterLicitacoesRelatorioMateriasAteVinteDias(slcNumero);
	}

	@Override
	public RelatorioDiarioMateriaisComSaldoAteVinteDiasVO pesquisarLicitacoesRelatorioMateriasAteVinteDiasUnionAll(Integer slcNumero) {
		return this.getScoMaterialDAO().pesquisarLicitacoesRelatorioMateriasAteVinteDiasUnionAll(slcNumero);
	}

	@Override
	public List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarDadosRelatorioMateriaisAteVinteDiasInicio(Date dataCompetencia,
			Integer limiteDiasDuracaoEstoque, AghParametros paramClassificacaoComprasWeb) {
		return this.getScoMaterialDAO().pesquisarDadosRelatorioMateriaisAteVinteDiasInicio(dataCompetencia, limiteDiasDuracaoEstoque,
				paramClassificacaoComprasWeb);
	}

	@Override
	public Long obterQuantidadeBloqueada(Integer matCodigo, Short ealSeq) {
		return this.getScoMaterialDAO().obterQuantidadeBloqueada(matCodigo, ealSeq);
	}

	@Override
	public Long obterQuantidadeTotal(Integer matCodigo, Short ealSeq) {
		return this.getScoMaterialDAO().obterQuantidadeTotal(matCodigo, ealSeq);
	}

	@Override
	public Long obterQuantidadeDisponivel(Integer matCodigo, Short ealSeq) {
		return this.getScoMaterialDAO().obterQuantidadeDisponivel(matCodigo, ealSeq);
	}

	@Override
	public Long pesquisaScoMateriaisRelMensal(Integer gmtCodigo, DominioSimNao indEstoc) {
		return this.getScoMaterialDAO().pesquisaScoMateriaisRelMensal(gmtCodigo, indEstoc);
	}

	@Override
	public ScoMaterial obterScoMaterialPorChavePrimaria(Integer codigo) {
		return this.getScoMaterialDAO().obterPorChavePrimaria(codigo);
	}

	//	INCLUIDO PROVISÓRIAMENTE PARA NÃO QUEBRAR CONTROLLER
	@Override
	public ScoMaterial obterScoMaterialPorChavePrimaria(Long codigo) {
		return this.getScoMaterialDAO().obterPorChavePrimaria(codigo != null ? codigo.intValue() : null);
	}

	@Override
	public ScoMaterial obterScoMaterialDetalhadoPorChavePrimaria(Integer codigo) {
		return this.getScoMaterialDAO().obterScoMaterialDetalhadoPorChavePrimaria(codigo);
	}

	@Override
	public void efetuarInclusao(ScoMaterial entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException {
		this.getMaterialFarmaciaON().inserir(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void efetuarAlteracao(ScoMaterial entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException {
		this.getMaterialFarmaciaON().atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public void efetuarRemocao(ScoMaterial entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException {
		this.getMaterialFarmaciaON().remover(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	@Override
	public List<String> obterRazoesExcessaoMaterial() {
		return this.getMaterialFarmaciaON().getRazaoExecessao();
	}

	@Override
	public Long consultarMaterialFarmaciaCount(ScoMaterial material) {
		return this.getScoMaterialDAO().pesquisarCount(material);
	}

	@Override
	public List<ScoMaterial> consultarMaterialFarmacia(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoMaterial material) {
		return getScoMaterialDAO().pesquisar(firstResult, maxResult, orderProperty, asc, material);
	}

	@Override
	public ScoMaterial obterScoMaterialOriginal(Integer codigo) {
		return getScoMaterialDAO().obterOriginal(codigo);
	}

	@Override
	public ScoMaterial obterScoMaterialOriginal(ScoMaterial scoMaterial) {
		return getScoMaterialDAO().obterOriginal(scoMaterial);
	}

	@Override
	public void persistirScoMaterial(ScoMaterial scoMaterial) {
		getScoMaterialDAO().persistir(scoMaterial);
		}

	@Override
	public void atualizarScoMaterial(ScoMaterial scoMaterial) {
		scoMaterial = this.getScoMaterialDAO().merge(scoMaterial);
		this.getScoMaterialDAO().atualizar(scoMaterial);
		}

	@Override
	public List<ScoFornecedor> listarFornecedoresComProposta(Object pesquisa, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer numeroPac) {
		return this.getScoFornecedorDAO().listarFornecedoresComProposta(pesquisa, firstResult, maxResults, orderProperty, asc, numeroPac);
	}

	@Override
	public ScoFornecedor obterScoFornecedorPorChavePrimaria(Integer codigo) {
		return getScoFornecedorDAO().obterPorChavePrimaria(codigo);
	}
	
	@Override
	public ScoFornecedor obterScoFornecedorComCidadePorChavePrimaria(Integer codigo) {
		return getScoFornecedorDAO().obterPorChavePrimaria(codigo, true, ScoFornecedor.Fields.CIDADE);
	}

	protected MaterialFarmaciaON getMaterialFarmaciaON() {
		return materialFarmaciaON;
	}

	@Override
	public void persistirScoMaterial(ScoFornecedor scoFornecedor) {
		this.getScoFornecedorDAO().persistir(scoFornecedor);
	}

	@Override
	public List<ScoFornecedor> pesquisarFornecedoresSaldoEstoque(Object objPesquisa, Short almSeq, Integer matCodigo, Integer maxResults) {
		return this.getScoFornecedorDAO().pesquisarFornecedoresSaldoEstoque(objPesquisa, almSeq, matCodigo, maxResults);
	}

	@Override
	public ScoFornecedor obterOriginalScoFornecedor(ScoFornecedor scoFornecedor) {
		return this.getScoFornecedorDAO().obterOriginal(scoFornecedor);
	}

	@Override
	public ScoFornecedor atualizarScoFornecedor(ScoFornecedor scoFornecedor) {
		return this.getScoFornecedorDAO().merge(scoFornecedor);
	}

	@Override
	public List<ScoMateriaisClassificacoes> pesquisarScoMateriaisClassificacoesPorMaterial(Integer scoMaterialCodigo) {
		return this.getScoMateriaisClassificacoesDAO().pesquisarScoMateriaisClassificacoesPorMaterial(scoMaterialCodigo);
	}

	@Override
	public ScoMateriaisClassificacoes buscarPrimeiroScoMateriaisClassificacoesPorMaterial(Integer scoMaterialCodigo) {
		return this.getScoMateriaisClassificacoesDAO().buscarPrimeiroScoMateriaisClassificacoesPorMaterial(scoMaterialCodigo);
	}

	@Override
	public List<ScoMateriaisClassificacoes> buscarScoMateriaisClassificacoesPorMaterialEClassificacao(Integer scoMaterialCodigo,
			Long classIni, Long classFim) {
		return this.getScoMateriaisClassificacoesDAO().buscarScoMateriaisClassificacoesPorMaterialEClassificacao(scoMaterialCodigo,
				classIni, classFim);
	}

	@Override
	public ScoProgEntregaItemAutorizacaoFornecimento obterProgEntregaAutorizacaoFornecimentoPorId(
			ScoProgEntregaItemAutorizacaoFornecimentoId id) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(id);
	}

	protected ScoMateriaisClassificacoesDAO getScoMateriaisClassificacoesDAO() {
		return scoMateriaisClassificacoesDAO;
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	@Override
	public void persistirScoMateriaisClassificacoes(ScoMateriaisClassificacoes scoMateriaisClassificacoes) {
		this.getScoMateriaisClassificacoesDAO().persistir(scoMateriaisClassificacoes);
	}

	@Override
	public void removerScoMateriaisClassificacoes(ScoMateriaisClassificacoes scoMateriaisClassificacoes) {
		this.getScoMateriaisClassificacoesDAO().remover(scoMateriaisClassificacoes);
		}

	@Override
	public void persistirProgramacaoEntregaAf(ScoProgEntregaItemAutorizacaoFornecimento progEntrega) {
		this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(progEntrega);
	}

	@Override
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaItemAf(Integer iafAfnNum, Integer iafNumero,
			Boolean filtraQtde, Boolean filtraCanceladas) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisaProgEntregaItemAf(iafAfnNum, iafNumero, filtraQtde,
				filtraCanceladas);
	}

	@Override
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisaProgEntregaItemAfAss(Integer iafAfnNum, Integer iafNumero) {
		return this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisaProgEntregaItemAfAss(iafAfnNum, iafNumero, null);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarAutorizacaoFormAntigaNaoEncerrada(Integer codigoMaterial, String codigoUnidadeMedida) {
		return this.getScoFaseSolicitacaoDAO().pesquisarAutorizacaoFormAntigaNaoEncerrada(codigoMaterial, codigoUnidadeMedida);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarFasePorIafAfnNumeroIafNumero(Integer iafAfnNumero, Integer iafNumero) {
		return this.getScoFaseSolicitacaoDAO().pesquisarFasePorIafAfnNumeroIafNumero(iafAfnNumero, iafNumero);
	}

	@Override
	public List<ScoServico> pesquisaCodigoSolicitacaoServico(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaCodigoSolicitacaoServico(itemAutorizacaoFornId);
	}

	@Override
	public Boolean pesquisaTipoFaseSolicitacao(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaTipoFaseSolicitacao(itemAutorizacaoFornId);
	}

	@Override
	public List<ScoMaterial> pesquisaMaterialSolicitacaoCompras(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaMaterialSolicitacaoCompras(itemAutorizacaoFornId);
	}

	@Override
	public ScoSolicitacaoDeCompra pesquisaSolicitacaoCompras(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaSolicitacaoCompras(itemAutorizacaoFornId);
	}

	@Override
	public Integer obterNumeroSolicitacaoCompraOriginal(ScoItemAutorizacaoFornId itemAutorizacaoFornId, boolean isSC) {
		return this.getScoFaseSolicitacaoDAO().obterNumeroSolicitacaoCompraOriginal(itemAutorizacaoFornId, isSC);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisaItensAutPropostaFornecedor(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaItensAutPropostaFornecedor(itemAutorizacaoFornId);
	}

	@Override
	public Boolean pesquisaIndConsignado(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaIndConsignado(itemAutorizacaoFornId);
	}

	@Override
	public DominioTipoFaseSolicitacao pesquisaTipoFaseSolic(ScoItemAutorizacaoFornId itemAutorizacaoFornId) {
		return this.getScoFaseSolicitacaoDAO().pesquisaTipoFaseSolic(itemAutorizacaoFornId);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompra(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoPorNumeroSolCompra(numero);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(numero);
	}

	@Override
	public void inserirScoItemAutorizacaoForn(ScoItemAutorizacaoForn scoItemAutorizacaoForn) {
		this.scoItemAutorizacaoFornDAO.persistir(scoItemAutorizacaoForn);		
		}

	@Override
	public ScoItemAutorizacaoFornJn obterValorAssinadoPorItemNotaPorItemAutorizacaoForn(ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		return this.scoItemAutorizacaoFornJnDAO.obterValorAssinadoPorItemNotaPorItemAutorizacaoForn(scoItensAutorizacaoForn);
	}

	@Override
	public void inserirScoItemAutorizacaoFornJn(ScoItemAutorizacaoFornJn scoItemAutorizacaoFornJn) {
		this.scoItemAutorizacaoFornJnDAO.persistir(scoItemAutorizacaoFornJn);		
		}

	@Override
	public void atualizarScoMarcaComercialDepreciado(ScoMarcaComercial scoMarcaComercial) {
		this.getScoMarcaComercialDAO().atualizar(scoMarcaComercial);
		this.getScoMarcaComercialDAO().flush();
	}

	@Override
	public void inserirScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.getScoMarcaComercialDAO().persistir(scoMarcaComercial);
		}

	@Override
	public ScoMarcaComercial obterScoMarcaComercialOriginal(ScoMarcaComercial scoMarcaComercial) {
		return this.getScoMarcaComercialDAO().obterOriginal(scoMarcaComercial);
	}

	@Override
	public boolean verificarMarcaExistente(Integer codigo, String descricao) {
		return this.getScoMarcaComercialDAO().verificarMarcaExistente(codigo, descricao);
	}

	@Override
	public ScoMarcaComercial obterScoMarcaComercialPorChavePrimaria(Integer codigoMarca) {
		return this.getScoMarcaComercialDAO().obterPorChavePrimaria(codigoMarca);
	}

	@Override
	public ScoUnidadeMedida obterScoUnidadeMedidaOriginal(String codigo) {
		return this.getScoUnidadeMedidaDAO().obterOriginal(codigo);
	}

	@Override
	public ScoUnidadeMedida obterScoUnidadeMedidaPorChavePrimaria(String codigo) {
		return this.getScoUnidadeMedidaDAO().obterPorChavePrimaria(codigo);
	}

	private ScoUnidadeMedidaDAO getScoUnidadeMedidaDAO() {
		return scoUnidadeMedidaDAO;
	}

	@Override
	public void inserirScoAutorizacaoFornJnComFlush(ScoAutorizacaoFornJn journalAutorizacaoFornecimento) {
		this.scoAutorizacaoFornJnDAO.persistir(journalAutorizacaoFornecimento);
	}

	@Override
	public List<ScoGrupoMaterial> pesquisaRelMensalMateriais(Date dataCompetencia, Integer codigo, DominioAgruparRelMensal agrupar) {
		return this.getScoGrupoMaterialDAO().pesquisaRelMensalMateriais(dataCompetencia, codigo, agrupar);
	}

	@Override
	public ScoGrupoMaterial obterScoGrupoMaterialPorChavePrimaria(Integer codigo) {
		return this.getScoGrupoMaterialDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorMarcaComercial(Integer id) {
		return this.scoMarcaModeloDAO.pesquisarMarcaModeloPorMarcaComercial(id);
	}

	@Override
	public void atualizarScoMarcaModeloDepreciado(ScoMarcaModelo scoMarcaModelo) {
		this.scoMarcaModeloDAO.atualizar(scoMarcaModelo);		
	}

	@Override
	public void inserirScoMarcaModelo(ScoMarcaModelo scoMarcaModelo) {
		this.scoMarcaModeloDAO.persistir(scoMarcaModelo);
		}

	@Override
	public void inserirScoItemLicitacao(ScoItemLicitacao itemLicitacao) {
		this.getScoItemLicitacaoDAO().persistir(itemLicitacao);		
		}

	public void inserirScoLicitacao(ScoLicitacao scoLicitacao) {
		this.getScoLicitacaoDAO().persistir(scoLicitacao);		
		}

	@Override
	public Long validarScoRefCodesPorTipoOperConversao(final String valor, final String dominio) {
		return this.getScoRefCodesDAO().validarScoRefCodesPorTipoOperConversao(valor, dominio);
	}

	@Override
	public List<ScoRefCodes> buscarScoRefCodesPorSituacao(String rvLowValue, String rvMeaning, List<String> rvLowValues) {
		return this.getScoRefCodesDAO().buscarScoRefCodesPorSituacao(rvLowValue, rvMeaning, rvLowValues);
	}

	@Override
	public ScoClassifMatNiv5 obterClassifMatNiv5PorNumero(Long numero) {
		return this.scoClassifMatNiv5DAO.obterClassifMatNiv5PorNumero(numero);
	}

	@Override
	public List<Long> pesquisarNumerosClassificacaoGrupoMaterial(Integer codigoGrupo) {
		return this.scoClassifMatNiv5DAO.pesquisarNumerosClassificacaoGrupoMaterial(codigoGrupo);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public List<ScoCondicaoPagamentoProposVO> obterCondicaoPagamentoPropos(Integer nroFornecedor, Integer nroLicitacao, Short numeroItem,
			Integer first, Integer max, String order, boolean asc) {
		return scoCondicaoPagamentoProposRN.obterCondicaoPagamentoPropos(nroFornecedor, nroLicitacao, numeroItem, first, max, order,
				asc);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public Long obterCondicaoPagamentoProposCount(Integer nroFornecedor, Integer nroLicitacao, Short numeroItem) {
		return scoCondicaoPagamentoProposDAO.obterCondicaoPgtoProposCount(nroFornecedor, nroLicitacao, numeroItem);
	}

	@Override
	public boolean permitirNovaCondicaoPgtoProposta(Integer frnNumero, Integer numeroLicitacao, Short numeroItem) {
		return scoCondicaoPagamentoProposRN.permitirNovaCondicaoPgtoProposta(frnNumero, numeroLicitacao, numeroItem);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public ScoCondicaoPagamentoPropos obterCondicaoPagamentoPropos(Integer id) {
		return scoCondicaoPagamentoProposDAO.obterPorChavePrimaria(id);
	}

	@Override
	@Secure("#{s:hasPermission('consultarProposta','visualizar')}")
	public List<ScoParcelasPagamento> obterParcelas(ScoCondicaoPagamentoPropos condicao) {
		return scoParcelasPagamentoDAO.obterParcelasPgtoProposta(condicao.getNumero());
	}

	@Override
	public void inserir(ScoCondicaoPagamentoPropos condicao, List<ScoParcelasPagamento> parcelas) throws BaseException {
		scoCondicaoPagamentoProposRN.inserir(condicao, parcelas);
	}

	@Override
	public void atualizar(ScoCondicaoPagamentoPropos condicao, List<ScoParcelasPagamento> parcelas,
			List<ScoParcelasPagamento> parcelasExcluidas) throws BaseException {
		scoCondicaoPagamentoProposRN.atualizar(condicao, parcelas, parcelasExcluidas);
	}

	@Override
	public void excluirCondicaoPagamentoProposta(Integer id) throws ApplicationBusinessException {
		scoCondicaoPagamentoProposRN.excluir(id);
	}

	@Override
	public ScoFormaPagamento obterScoFormaPagamentoPorChavePrimaria(Short codigo) {
		return this.scoFormaPagamentoDAO.obterPorChavePrimaria(codigo);
	}

	@Override
	public void inserirScoItemPropostaFornecedorDAO(ScoItemPropostaFornecedor scoItemPropostaFornecedor) {
		this.getScoItemPropostaFornecedorDAO().persistir(scoItemPropostaFornecedor);		
		}

	@Override
	public void inserirScoLoteLicitacao(ScoLoteLicitacao scoLoteLicitacao) {
		this.scoLoteLicitacaoDAO.persistir(scoLoteLicitacao);		
		}

	@Override
	public void inserirScoPropostaFornecedor(ScoPropostaFornecedor scoPropostaFornecedor) {
		this.scoPropostaFornecedorDAO.persistir(scoPropostaFornecedor);		
		}

	@Override
	public List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisCurvaAbc(Date dtCompetencia) {
		return this.getScoSolicitacoesDeComprasDAO().buscarRelatorioMaterialEstocaveisCurvaAbc(dtCompetencia);
	}

	@Override
	public List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisSemComprasCurvaAbc(Date dtCompetencia) {
		return this.getScoSolicitacoesDeComprasDAO().buscarRelatorioMaterialEstocaveisSemComprasCurvaAbc(dtCompetencia);
	}

	@Override
	public void inserirScoMateriaisClassificacoeJn(ScoMateriaisClassificacoeJn scoMateriaisClassificacoeJn) {
		this.scoMateriaisClassificacoeJnDAO.persistir(scoMateriaisClassificacoeJn);		
		}

	@Override
	public void inserirScoMaterialJN(ScoMaterialJN scoMaterialJN) {
		this.getScoMaterialJNDAO().persistir(scoMaterialJN);		
		}

	@Override
	public boolean existeMateriaisClassificacoesPorConsumoSinteticoMaterial(Integer codigoMaterial, final Long cn5Numero) {
		return getScoMateriaisClassificacoesDAO().existeMateriaisClassificacoesPorConsumoSinteticoMaterial(codigoMaterial, cn5Numero);
	}

	private ManterFornecedorON getManterFornecedorON() {
		return manterFornecedorON;
	}

	@Override
	public List<ScoAutorizacaoForn> visualizarAFsGeradas(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega,
			DominioModalidadeEmpenho modalidadeEmpenho, ScoRefCodes situacao, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {
		return getVisualizaAFsGeradasNaoEfetivadasON().visualizarAFsGeradasNaoEfetivadas(numeroLicitacao, nroComplemento, dtGeracao,
				dtPrevEntrega, modalidadeEmpenho, situacao, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public void validaPreenchimentoCamposPesquisaAFsGeradas(Integer numeroLicitacao, Short nroComplemento)
			throws ApplicationBusinessException {
		getVisualizaAFsGeradasNaoEfetivadasON().validaPreenchimentoCampos(numeroLicitacao, nroComplemento);
	}

	@Override
	public Long visualizarAFsGeradasCount(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega,
			DominioModalidadeEmpenho modalidadeEmpenho, ScoRefCodes situacao) throws BaseListException, ApplicationBusinessException {
		return getVisualizaAFsGeradasNaoEfetivadasON().visualizarAFsGeradasNaoEfetivadasCount(numeroLicitacao, nroComplemento, dtGeracao,
				dtPrevEntrega, modalidadeEmpenho, situacao);
	}

	@Override
	public List<ScoRefCodes> buscarScoRefCodesPorSituacao(String consultaCampo) throws BaseListException, ApplicationBusinessException {
		return getVisualizaAFsGeradasNaoEfetivadasON().buscarScoRefCodesPorSituacao(consultaCampo);
	}

	private VisualizaAFsGeradasNaoEfetivadasON getVisualizaAFsGeradasNaoEfetivadasON() {
		return visualizaAFsGeradasNaoEfetivadasON;
	}

	@Override
	public ScoFornecedor obterFornecedorPorNumero(final Integer numero) {
		return getScoFornecedorDAO().obterFornecedorPorNumero(numero);
	}

	@Override
	public ScoContatoFornecedor obterContatoFornecedor(final ScoContatoFornecedorId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getScoContatoFornecedorDAO().obterPorChavePrimaria(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public ScoFornecedor clonarFornecedor(final ScoFornecedor fornecedor) throws BaseException {
		return getManterFornecedorON().clonarFornecedor(fornecedor);
	}

	@Override
	public SceItemRmps persistirItemRmps(final SceItemRmps itemNew, final SceItemRmps itemOld, final Boolean flush) throws BaseException {
		return this.getManterItemRmpsON().persistirItemRmps(itemNew, itemOld, flush);
	}

	protected ManterItemRmpsON getManterItemRmpsON() {
		return manterItemRmpsON;
	}

	@Override
	public ScoFornecedor inserirScoFornecedor(final ScoFornecedor fornecedor, final boolean gravar) throws ApplicationBusinessException {
		return getManterFornecedorRN().inserirScoFornecedor(fornecedor, gravar);
	}

	@Override
	public ScoFornecedor atualizarScoFornecedor(final ScoFornecedor fornecedor, final ScoFornecedor oldFornecedor, final boolean gravar)
			throws ApplicationBusinessException {
		return getManterFornecedorRN().atualizarScoFornecedor(fornecedor, oldFornecedor, gravar);
	}

	protected ManterFornecedorRN getManterFornecedorRN() {
		return manterFornecedorRN;
	}

	/**
	 * Retorna a lista de contatos do fornecedor.
	 * 
	 * @param numero
	 *            do Fornecedor
	 * @return lista de contatos do fornecedor
	 */
	public List<ScoContatoFornecedor> pesquisarContatosPorFornecedor(Integer numFornecedor) {
		return this.getScoContatoFornecedorDAO().pesquisarContatosPorFornecedor(numFornecedor);
	}

	@Override
	public SceItemRmps cloneSceItemRmps(final SceItemRmps itemRmps) throws BaseException {
		return getManterItemRmpsON().cloneSceItemRmps(itemRmps);
	}

	public ManterMarcaComercialON getManterMarcaComercialON() {
		return manterMarcaComercialON;
	}

	//#5574
	@Override
	public void persistirScoMarcaComercial(ScoMarcaComercial marcaComercial) throws ApplicationBusinessException {
		getManterMarcaComercialON().persistirMarcaComercial(marcaComercial);

	}

	@Override
	public void persistirScoMarcaModelo(ScoMarcaModelo marcaModelo, ScoMarcaComercial scoMarcaComercial) throws ApplicationBusinessException {
		getManterMarcaComercialON().persistirMarcaModelo(marcaModelo, scoMarcaComercial);
	}

	@Override
	public SceRmrPaciente persistirSceRmrPaciente(final SceRmrPaciente sceRmrPacienteNew, final SceRmrPaciente sceRmrPacienteOld,
			final Boolean flush) throws BaseException {
		return manterSceRmrPacienteON.persistirSceRmrPaciente(sceRmrPacienteNew, sceRmrPacienteOld, flush);
	}

	@Override
	public SceRmrPaciente cloneSceRmrPaciente(final SceRmrPaciente paciente) throws BaseException {
		return manterSceRmrPacienteON.cloneSceRmrPaciente(paciente);
	}

	@Override
	public void removerNotaFiscal(final FatItemContaHospitalar itemExcluirNota) throws BaseException {
		manterSceRmrPacienteON.removerSceItemRmpsParaNotaFiscal(itemExcluirNota);
	}

	@Override
	public FsoNaturezaDespesa obtemNaturezaDespesaPorMaterial(Integer materialId) {
		return getScoGrupoMaterialDAO().obtemNaturezaDespesaPorMaterial(materialId);
	}

	@Override
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {
		return this.scoMarcaModeloDAO.pesquisarMarcaModeloPorCodigoDescricao(param, marcaComercial, indAtivo);
	}

	@Override
	public Long pesquisarMarcaModeloPorCodigoDescricaoCount(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {
		return this.scoMarcaModeloDAO.pesquisarMarcaModeloPorCodigoDescricaoCount(param, marcaComercial, indAtivo);
	}

	@Override
	public List<ScoSolicitacaoDeCompra> listarSolicitacoesAparelho(Integer pacCodigo, Integer phi, Boolean indRecebimento, Long cn5Numero) {
		return getScoSolicitacoesDeComprasDAO().listarSolicitacoesAparelho(pacCodigo, phi, indRecebimento, cn5Numero);
	}

	@Override
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricao(Object param) {
		return getScoMarcaComercialDAO().pesquisarMarcaComercialPorCodigoDescricao(param);
	}

	@Override
	public Long pesquisarMarcaComercialPorCodigoDescricaoCount(Object param) {
		return getScoMarcaComercialDAO().pesquisarMarcaComercialPorCodigoDescricaoCount(param);
	}

	@Override
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoAtivo(Object param) {
		return getScoMarcaComercialDAO().pesquisarMarcaComercialPorCodigoDescricaoAtivo(param, DominioSituacao.A);
	}

	@Override
	public Long pesquisarMarcaComercialPorCodigoDescricaoAtivoCount(Object param) {
		return getScoMarcaComercialDAO().pesquisarMarcaComercialPorCodigoDescricaoAtivoCount(param, DominioSituacao.A);
	}

	/**
	 * @see ScoServicoON#alterar(ScoServico)
	 */
	@Override
	public void alterar(ScoServico servico) {
		scoServicoON.alterar(servico);
	}

	/**
	 * @see ScoServicoON#incluir(ScoServico)
	 */
	@Override
	public void incluir(ScoServico servico) {
		scoServicoON.incluir(servico);
	}

	@Override
	public List<ScoServico> listarServicos(Object param) {
		return this.getScoServicoDAO().listarServicos(param);
	}

	@Override
	public Long listarServicosCount(Object param) {
		return this.getScoServicoDAO().listarServicosCount(param);
	}

	/**
	 * 
	 * @param pontoServidor
	 * @throws BaseListException
	 */
	@Override
	public void inserirPontoParadaServidor(final ScoPontoServidor pontoServidor) throws BaseListException {
		manterPontoParadaServidorON.inserirPontoParadaServidor(pontoServidor);
	}

	/**
	 * Remove um ponto de parada servidor de acordo com o id.
	 * 
	 * @param idPontoServidor
	 */
	@Override
	public void removerPontoParadaServidor(ScoPontoServidorId idPontoServidor) throws ApplicationBusinessException {
		manterPontoParadaServidorON.removerPontoParadaServidor(idPontoServidor);
	}

	/**
	 * @author clayton.bras
	 * @param codigo
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	@Override
	public Long pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(final Short codigo, final Integer matricula, final Short vinculo) {
		return getScoPontoParadaServidorDAO().pesquisarPontoParadaServidorCodigoMatriculaVinculoCount(codigo, matricula, vinculo);
	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorNumeroSolCompraComAutForn(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().pesquisarFaseSolicitacaoPorNumeroSolCompraComAutForn(numero);
	}

	@Override
	public Boolean getScEmFases(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().getScEmFases(numero);
	}

	/**
	 * obtem a lista de fases a partir do tipo da Solicitacao se compra ou
	 * servico e se o item da licitacao está nulo ou não
	 * 
	 * @param numero
	 *            da solicitacao
	 * @param isNUll
	 *            , se o item da licitacao estpa nulo ou não
	 * @param tipoSolicitacao
	 *            , se compra ou servico
	 */
	public List<ScoFaseSolicitacao> obterFaseSolicitacao(Integer numero, boolean isNull, DominioTipoSolicitacao tipoSolicitacao) {
		return this.getScoFaseSolicitacaoDAO().obterFaseSolicitacao(numero, isNull, tipoSolicitacao);
	}

	@Override
	public Boolean getSsEmFases(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().getSsEmFases(numero);
	}

	/**
	 * @author clayton.bras
	 * @param codigo
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	@Override
	public List<ScoPontoServidor> pesquisarPontoParadaServidorCodigoMatriculaVinculo(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Short codigo, final Integer matricula, final Short vinculo) {
		return getScoPontoParadaServidorDAO().pesquisarPontoParadaServidorCodigoMatriculaVinculo(firstResult, maxResult, orderProperty,
				asc, codigo, matricula, vinculo);
	}

	protected ScoSolicitacaoCompraServicoDAO getScoSolicitacaoCompraServicoDAO() {
		return scoSolicitacaoCompraServicoDAO;
	}

	private ConsultaSCSSON getConsultaSCSSON() {
		return consultaSCSSON;
	}

	@Override
	public void validaFiltroPesquisa(FiltroConsSCSSVO filtroConsulta) throws ApplicationBusinessException {
		this.getConsultaSCSSON().validaFiltroPesquisa(filtroConsulta);
	}

	@Override
	public Boolean verificaCamposFiltro(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2)
			throws ApplicationBusinessException {
		return this.getConsultaSCSSON().verificaCamposFiltro(filtroConsulta, ignorarMetodo1, ignorarMetodo2);
	}
	
	@Override
	public Boolean verificaCamposFiltro(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2, String ignorarMetodo3)
			throws ApplicationBusinessException {
		return this.getConsultaSCSSON().verificaCamposFiltro(filtroConsulta, ignorarMetodo1, ignorarMetodo2, ignorarMetodo3);
	}

	@Override
	public Boolean verificaCamposFiltroData(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2)
			throws ApplicationBusinessException {
		return this.getConsultaSCSSON().verificaCamposFiltroData(filtroConsulta, ignorarMetodo1, ignorarMetodo2);
	}

	@Override
	public List<ScoFornecedor> listarFornecedoresByLicitacao(Object param, ScoLicitacao licitacao) {
		return getScoFornecedorDAO().listarFornecedorPropostaAceita(param, licitacao);
	}

	@Override
	public List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(Object pesquisa) {
		return getVScoFornecedorDAO().pesquisarVFornecedorPorCgcCpfRazaoSocial(pesquisa);
	}

	@Override
	public List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(final Object pesquisa, final Integer limite) {
		return getVScoFornecedorDAO().pesquisarVFornecedorPorCgcCpfRazaoSocial(pesquisa, limite);
	}

	@Override
	public Long contarVFornecedorPorCgcCpfRazaoSocial(Object pesquisa) {
		return getVScoFornecedorDAO().contarVFornecedorPorCgcCpfRazaoSocial(pesquisa);
	}

	@Override
	public Long pesquisarVFornecedorPorCgcCpfRazaoSocialCount(Object pesquisa) {
		return getVScoFornecedorDAO().pesquisarVFornecedorPorCgcCpfRazaoSocialCount(pesquisa);
	}

	private VScoFornecedorDAO getVScoFornecedorDAO() {
		return vScoFornecedorDAO;
	}

	@Override
	public List<ScoMaterial> pesquisarMateriaisAtivosGrupoMaterial(Object objPesquisa, List<Integer> listaGmtCodigo) {
		return getScoMaterialDAO().pesquisarMateriaisAtivosGrupoMaterial(objPesquisa, listaGmtCodigo);
	}

	@Override
	public Long pesquisarMateriaisAtivosGrupoMaterialCount(Object objPesquisa, List<Integer> listaGmtCodigo) {
		return getScoMaterialDAO().pesquisarMateriaisAtivosGrupoMaterialCount(objPesquisa, listaGmtCodigo);
	}

	@Override
	public void fatpAtuIchSce(final Integer rmpSeq, final Short numero, final Integer quantidade, final Integer phiSeqICH,
			final Date dataFimVinculoServidor) throws BaseException {
		manterItemRmpsRN.fatpAtuIchSce(rmpSeq, numero, quantidade, phiSeqICH, dataFimVinculoServidor);
	}

	@Override
	public ScoMaterial obterMaterialPorCodigoDescricaoUnidadeMedida(Integer matCodigo, Object param) {
		return getScoMaterialDAO().obterMaterialPorCodigoDescricaoUnidadeMedida(matCodigo, param);
	}

	@Override
	public Integer obterMaxItemAF(Integer numAf) {
		return this.scoItemAutorizacaoFornDAO.obterMaxItemAF(numAf);
	}

	@Override
	public ScoItemLicitacao obterItemLicitacaoPorNumeroLicitacaoENumeroItem(Integer numeroLCT, Short numeroItem) throws BaseException {
		return this.getScoItemLicitacaoDAO().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLCT, numeroItem);

	}

	@Override
	public List<ScoFaseSolicitacao> pesquisarFaseSolicitacaoPorItemAutorizacao(Integer iafAfnNumero, Integer iafNumero) {
		return getScoFaseSolicitacaoDAO().pesquisarFasePorIafAfnNumeroIafNumero(iafAfnNumero, iafNumero);
	}

	@Override
	public ScoAutorizacaoForn obterScoAutorizacaoFornPorChavePrimaria(final Integer numeroAf) {
		return this.scoAutorizacaoFornDAO.obterPorChavePrimaria(numeroAf);
	}

	@Secure("#{s:hasPermission('cadastrarContatosForn','gravar')}")
	public void persistirContatoFornecedor(Integer numeroFrn, ScoContatoFornecedor scoContatoFornecedor)
			throws ApplicationBusinessException {
		getScoContatoFornecedorON().persistirContatoFornecedor(numeroFrn, scoContatoFornecedor);
	}

	@Secure("#{s:hasPermission('cadastrarContatosForn','gravar')}")
	public void removerContatoFornecedor(ScoContatoFornecedorId id) throws ApplicationBusinessException {
		getScoContatoFornecedorRN().remover(id);
	}

	public boolean verificarAlteracaoContatoFornecedor(ScoContatoFornecedor scoContatoFornecedor) {
		return getScoContatoFornecedorON().verificarAlteracaoContatoFornecedor(scoContatoFornecedor);

	}

	public Long verificarValorAssinadoPorAf(Integer afnNumero) {
		return this.getScoLicitacaoDAO().verificarValorAssinadoPorAf(afnNumero);
	}

	public GeracaoMovimentoEstoqueVO obterGeracaoMovimentoEstoque(Integer afnNumero, Integer numero, Integer matCodigo) {
		return this.getScoFaseSolicitacaoDAO().obterGeracaoMovimentoEstoque(afnNumero, numero, matCodigo);
	}

	private ScoContatoFornecedorRN getScoContatoFornecedorRN() {
		return scoContatoFornecedorRN;
	}

	private ScoContatoFornecedorON getScoContatoFornecedorON() {
		return scoContatoFornecedorON;
	}

	@Override
	public List<ScoGrupoServico> pesquisarGrupoServico(String pesquisa) {
		return this.getScoGrupoServicoDAO().pesquisarGrupoServico(pesquisa);
	}

	@Override
	public Integer obterScoMaterialMaxId() {
		return getScoMaterialDAO().obterMaxId();
	}	

	@Override
	public ScoUnidadeMedida obterPorCodigo(String codigo) {
		return this.getScoUnidadeMedidaDAO().obterPorCodigo(codigo);
	}

	@Override
	public Long pesquisarGrupoServicoCount(String pesquisa) {
		return this.getScoGrupoServicoDAO().pesquisarGrupoServicoCount(pesquisa);
	}

	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	@Override
	public ScoFornecedor obterFornecedorComPropostaPorAF(ScoAutorizacaoForn scoAutorizacaoForn) {
		return this.getScoFornecedorDAO().obterFornecedorComPropostaPorAF(scoAutorizacaoForn);
	}

	@Override
	public List<ScoFornecedor> listarFornAtivosComPropostaAceitaAfsSemContratos(Object pesquisa) {
		return this.getScoFornecedorDAO().listarFornAtivosComPropostaAceitaAfsSemContratos(pesquisa);
	}

	public List<ScoFaseSolicitacao> pesquisarFasePorLicitacaoNumero(Integer lctNumero) {
		return this.getScoFaseSolicitacaoDAO().pesquisarFasePorLicitacaoNumero(lctNumero);
	}

	@Override
	public ScoFaseSolicitacao obterFaseSolicitacaoPorNumero(Integer numero) {
		return this.getScoFaseSolicitacaoDAO().obterPorChavePrimaria(numero);
	}

	@Override
	public List<ScoContaCorrenteFornecedor> obterScoContaCorrenteFornecedorPorFornecedor(final ScoFornecedor fornecedor) {
		return scoContaCorrenteFornecedorDAO.obterScoContaCorrenteFornecedorPorFornecedor(fornecedor);
	}

	@Override
	public List<ScoLicitacao> listarLicitacoesPorNumeroEDescricao(Object param) {
		return getScoLicitacaoDAO().pesquisarLicitacoesPorNumeroDescricao(param);
	}

//	@Override
//	public Long listarItensLicitacoesCount(Object param) {
//		return getScoItemLicitacaoDAO().pesquisarItensLicitacoesCount(param);
//	}

	@Override
	public Long listarLicitacoesCount(Object param) {
		return getScoLicitacaoDAO().pesquisarLicitacoesCount(param);
	}
		
	@Override
	public List<ScoLicitacao> pesquisarLicitacao(ScoLicitacao licitacao, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getScoLicitacaoDAO().pesquisarLicitacao(licitacao, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public List<PropostasVencedorasVO> listarItensLicitacao(Integer numeroLicitacao) {
		return consultarPropostasVencedorasRN.consultarPropostasVencedoras(numeroLicitacao);
	}

	@Override
	public List<DadosConciliacaoVO> pesquisarDadosConcilicacao(Integer seq) {
		return getScoMaterialDAO().pesquisarDadosConcilicacao(seq);
	}
	
	@Override
	public List<DadosMateriaisConciliacaoVO> pesquisarDadosMaterialConcilicacao(Integer seq) {
		return getScoMaterialDAO().pesquisarDadosMaterialConcilicacao(seq);
	}
	
	@Override
	public List<ScoHistoricoAdvertForn> pesquisarAdvertenciasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		return manterPenalidadeFornecedorON.pesquisarAdvertenciasFornecedor(numero, inicio, fim);
	}
	
	@Override
	public List<ScoHistoricoMultaForn> pesquisarMultasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		return manterPenalidadeFornecedorON.pesquisarMultasFornecedor(numero, inicio, fim);
	}
	
	@Override
	public List<ScoHistoricoSuspensForn> pesquisarSuspensoesFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		return manterPenalidadeFornecedorON.pesquisarSuspensoesFornecedor(numero, inicio, fim);
	}
	
	@Override
	public List<ScoHistoricoOcorrForn> pesquisarOcorrenciasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		return manterPenalidadeFornecedorON.pesquisarOcorrenciasFornecedor(numero, inicio, fim);
	}
	 
	@Override
	public void persistirMulta(ScoHistoricoMultaForn multa) throws BaseException {
		manterPenalidadeFornecedorON.persistirMulta(multa);
	}
	
	@Override
	public void persistirAdvertencia(ScoHistoricoAdvertForn advertencia) throws BaseException {
		manterPenalidadeFornecedorON.persistirAdvertencia(advertencia);
	}
	
	@Override
	public void persistirOcorrencia(ScoHistoricoOcorrForn ocorrencia) throws BaseException {
		manterPenalidadeFornecedorON.persistirOcorrencia(ocorrencia);
	}
	
	@Override
	public void persistirSuspensao(ScoHistoricoSuspensForn suspensao, RapServidores servidor) throws BaseException {
		manterPenalidadeFornecedorON.persistirSuspensao(suspensao, servidor);
	}
	 
	@Override
	public void removerAdvertencia(ScoHistoricoAdvertFornId advertenciaId) {
		manterPenalidadeFornecedorON.removerAdvertencia(advertenciaId);
	}
	
	@Override
	public void removerMulta(ScoHistoricoMultaFornId multaId) {
		manterPenalidadeFornecedorON.removerMulta(multaId);
	}
	
	@Override
	public void removerOcorrencia(ScoHistoricoOcorrFornId ocorrenciaId) {
		manterPenalidadeFornecedorON.removerOcorrencia(ocorrenciaId);
	}
	@Override
	public void removerSuspensao(ScoHistoricoSuspensFornId suspensaoId) {
		manterPenalidadeFornecedorON.removerSuspensao(suspensaoId);
	}
	
	@Override
	public List<ScoTipoOcorrForn> pesquisarTipoOcorrenciaPorCodigoOuDescricao(Object pesquisa) {
		return scoTipoOcorrFornDAO.pesquisarTipoOcorrenciaPorCodigoOuDescricao(pesquisa);
	}
	
	@Override
	public Long pesquisarTipoOcorrenciaPorCodigoOuDescricaoCount(Object pesquisa) {
		return scoTipoOcorrFornDAO.pesquisarTipoOcorrenciaPorCodigoOuDescricaoCount(pesquisa);
	}
	
	@Override
	public ScoHistoricoAdvertForn obterAdvertenciaPorId(ScoHistoricoAdvertFornId id) {
		return scoHistoricoAdvertFornDAO.obterPorChavePrimaria(id);
	}

	@Override
	public ScoHistoricoMultaForn obterMultaPorId(ScoHistoricoMultaFornId id) {
		return scoHistoricoMultaFornDAO.obterPorChavePrimaria(id);
	}

	@Override
	public ScoHistoricoOcorrForn obterOcorrenciaPorId(ScoHistoricoOcorrFornId id) {
		return scoHistoricoOcorrFornDAO.obterPorChavePrimaria(id);
	}

	@Override
	public ScoHistoricoSuspensForn obterSuspensaoPorId(ScoHistoricoSuspensFornId id) {
		return scoHistoricoSuspensFornDAO.obterPorChavePrimaria(id);
	}
	
	
	@Override
	public ScoSolicitacaoDeCompra obterScoSolicitacaoDeCompraPorChavePrimaria(Integer numero) {
		return this.getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(numero);
	}

	@Override
	public ScoDataEnvioFornecedorVO consultarRegistroComDetalhesDasParcelas(ParcelaAfPendenteEntregaVO parcelaAfPendenteEntregaVO) {
		return this.scoAutorizacaoFornecedorPedidoDAO.consultarRegistroComDetalhesDasParcelas(parcelaAfPendenteEntregaVO);
	}

	@Override
	public List<ScoPedItensMatExpediente> pesquisarItensNotaFiscalByNumeroNotaFiscal(Integer numeroNotaFiscal) {
		return getScoPedItensMatExpedienteDAO().pesquisarItensNotaFiscalByNumeroNotaFiscal(numeroNotaFiscal);
	}

	@Override
	public List<ScoPedItensMatExpediente> pesquisarItensPedidoByNotaFiscalCodigoMaterial(final Integer numeroNotaFiscal,
			Integer codigoMaterial) {
		return getScoPedItensMatExpedienteDAO().pesquisarItensPedidoByNotaFiscalCodigoMaterial(numeroNotaFiscal, codigoMaterial);
	}

	protected ScoPedItensMatExpedienteDAO getScoPedItensMatExpedienteDAO() {
		return scoPedItensMatExpedienteDAO;
	}

	protected void setScoPedItensMatExpedienteDAO(ScoPedItensMatExpedienteDAO scoPedItensMatExpedienteDAO) {
		this.scoPedItensMatExpedienteDAO = scoPedItensMatExpedienteDAO;
	}

	protected ScoPedidoMatExpedienteDAO getScoPedidoMatExpedienteDAO() {
		return scoPedidoMatExpedienteDAO;
	}

	protected void setScoPedidoMatExpedienteDAO(ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO) {
		this.scoPedidoMatExpedienteDAO = scoPedidoMatExpedienteDAO;
	}

	protected ScoPedidoMatExpedienteON getScoPedidoMatExpedienteON() {
		return scoPedidoMatExpedienteON;
	}

	protected void setScoPedidoMatExpedienteON(ScoPedidoMatExpedienteON scoPedidoMatExpedienteON) {
		this.scoPedidoMatExpedienteON = scoPedidoMatExpedienteON;
	}

	@Override
	public Long pesquisarNotasFiscaisCount(ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO) {
		return getScoPedidoMatExpedienteDAO().pesquisarNotasFiscaisCount(scoPedidoMatExpedienteVO);
	}

	@Override
	public List<ScoPedidoMatExpedienteVO> pesquisarNotasFiscais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO) {
		return getScoPedidoMatExpedienteDAO().pesquisarNotasFiscais(firstResult, maxResult, orderProperty, asc, scoPedidoMatExpedienteVO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void atualizarScePedidoMatExpedienteById(final Integer seq, final DominioValidacaoNF situacao)
			throws ApplicationBusinessException {
		this.getScoPedidoMatExpedienteRN().atualizarScePedidoMatExpedienteById(seq, situacao);
	}

	@Override
	public void validarDatas(Date dtInicial, Date dtFinal) throws ApplicationBusinessException {
		this.getScoPedidoMatExpedienteON().validaDatas(dtInicial, dtFinal);
	}

	@Secure("#{s:hasPermission('confirmarPedidoWeb','manter')}")
	public List<ScoPedidoMatExpediente> pesquisarScoPedidoMatExp(ScoPedidoMatExpedienteVO filtro, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) throws ApplicationBusinessException {
		return getScoPedidoMatExpedienteON().pesquisarPedidoMatExp(filtro, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarScoPedidoMatExpCount(ScoPedidoMatExpedienteVO filtro) {
		return getScoPedidoMatExpedienteDAO().pesquisarPedidoMatExpCount(filtro);
	}

	@Override
	public ScoPedidoMatExpediente obterScoPedidoMatExpPorChavePrimaria(Integer seq) {
		return this.getScoPedidoMatExpedienteON().obterScoPedidoMatExpPorChavePrimaria(seq);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void atualizarScoPedidoMatExp(ScoPedidoMatExpediente pedido, Boolean recusa, RapServidores servidorAlteracao)
			throws ApplicationBusinessException {
		this.scoPedidoMatExpedienteRN.atualizarScoPedidoMatExpediente(pedido, recusa, servidorAlteracao);
	}

        @Override
        public void procedureGeraAfpAutAutomatica(RapServidores servidorLogado) throws ApplicationBusinessException {
    		this.getScoPedidoMatExpedienteDAO().procedureGeraAfpAutAutomatica(servidorLogado);
        }
	
	@Override
	public void validaPesquisaGeralAF(FiltroPesquisaGeralAFVO filtro) throws ApplicationBusinessException {
		this.getPesquisaGeralAFON().validaPesquisaGeralAF(filtro);
	}

	@Override
	public List<PesquisaGeralAFVO> listarAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getPesquisaGeralAFON().listarAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public List<PesquisaGeralIAFVO> listarItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getPesquisaGeralAFON().listarItensAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public List<PesquisaGeralPAFVO> listarPedidosAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getPesquisaGeralAFON().listarPedidosAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public List<PesquisaGeralPIAFVO> listarParcelasItensAutorizacoesFornecimentoFiltrado(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, FiltroPesquisaGeralAFVO filtro) {
		return getPesquisaGeralAFON().listarParcelasItensAutorizacoesFornecimentoFiltrado(firstResult, maxResults, orderProperty, asc,
				filtro);
	}

	@Override
	public String geraArquivoConsultaPorAF(FiltroPesquisaGeralAFVO filtro) throws IOException {
		return getPesquisaGeralAFON().geraArquivoConsultaPorAF(filtro);
	}

	@Override
	public String geraArquivoConsultaPorIAF(FiltroPesquisaGeralAFVO filtro) throws IOException {
		return getPesquisaGeralAFON().geraArquivoConsultaPorIAF(filtro);
	}

	@Override
	public String geraArquivoConsultaPorPAF(FiltroPesquisaGeralAFVO filtro) throws IOException {
		return getPesquisaGeralAFON().geraArquivoConsultaPorPAF(filtro);
	}

	@Override
	public String geraArquivoConsultaPorPIAF(FiltroPesquisaGeralAFVO filtro) throws IOException {
		return getPesquisaGeralAFON().geraArquivoConsultaPorPIAF(filtro);
	}

//	@Override
//	public void downloaded(String fileName) throws IOException {
//		this.getPesquisaGeralAFON().downloaded(fileName);
//	}

	public PesquisaGeralAFON getPesquisaGeralAFON() {
		return this.pesquisaGeralAFON;
	}

	@Override
	public List<ScoMaterialJN> pesquisarScoMaterialJNPorCodigoMaterial(Integer codigoMaterial, DominioOperacoesJournal operacao) {
		return getScoMaterialJNDAO().pesquisarScoMaterialJNPorCodigoMaterial(codigoMaterial, operacao);
	}

	@Override
	public List<ScoMaterialVinculo> obterMateriaisVinculados(ScoMaterial material) {
		return this.scoMaterialVinculoDAO.obterMateriaisVinculados(material);
	}

	@Override
	public void excluirMaterialVinculado(ScoMaterialVinculo scoMaterialVinculo) throws ApplicationBusinessException {
		this.scoMaterialVinculoRN.remover(scoMaterialVinculo);
	}

	@Override
	public void persistirMaterialVinculado(ScoMaterial material, ScoMaterial materialVinculado) {
		this.scoMaterialVinculoRN.persistirMaterialVinculado(material, materialVinculado);
	}

	@Override
	public Boolean validarVisualizacaoHistoricoMaterial(Integer codigoMaterial) {
		return this.visualizarHistoricoMaterialON.validarVisualizacaoHistoricoMaterial(codigoMaterial);
	}

	@Override
	public List<ScoDescricaoTecnicaPadrao> listarDescricaoTecnica(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoDescricaoTecnicaPadraoVO vo, ScoMaterial material) {
		return this.scoDescricaoTecnicaPadraoDAO.listarDescricaoTecnica(firstResult, maxResult, orderProperty, asc, vo, material);
	}

	@Override
	public Long listarDescricaoTecnicaCount(ScoDescricaoTecnicaPadraoVO vo, ScoMaterial material) {
		return this.scoDescricaoTecnicaPadraoDAO.listarDescricaoTecnicaCount(vo, material);
	}

	@Override
	public void deletarDescricaoTecnica(Short codigo) {
		this.scoDescricaoTecnicaPadraoRN.deletarDescricaoTecnica(codigo);
	}

	@Override
	public String salvarDescricaoTecnica(ScoDescricaoTecnicaPadrao descricaoTecnica, String descricao, List<ScoMaterial> listaMateriais,
			List<ScoAnexoDescricaoTecnica> listaAnexo) throws BaseException {
		return this.scoDescricaoTecnicaPadraoRN.salvarDescricaoTecnica(descricaoTecnica, descricao, listaMateriais, listaAnexo);
	}

	@Override
	public ScoDescricaoTecnicaPadrao buscarScoDescricaoTecnicaPadraoByCodigo(Short codigo, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.scoDescricaoTecnicaPadraoDAO.obterPorChavePrimaria(codigo, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	public ScoMaterialDescTecnica obterScoDescricaoTecnicaPorCodigo(Integer codigo, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin){
		return this.scoMaterialDescTecnicaDAO.obterPorChavePrimaria(codigo, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	public ScoDescricaoTecnicaPadrao buscarScoDescricaoTecnicaPadraoByCodigo(Integer codigo) {
		return this.scoDescricaoTecnicaPadraoDAO.obterPorChavePrimaria(codigo.shortValue());
	}

	@Override
	public List<ScoDescricaoTecnicaPadrao> obterRelatorioDescricaoTecnica(Short codigoDescricaoTecnica) {
		return this.scoDescricaoTecnicaPadraoDAO.obterRelatorioDescricaoTecnica(codigoDescricaoTecnica);
	}

	@Override
	public List<ScoDescricaoTecnicaPadrao> listarScoDescricaoTecnicaPadrao(Object objPesquisa) {
		return this.scoDescricaoTecnicaPadraoDAO.listarScoDescricaoTecnicaPadrao(objPesquisa);
	}

	@Override
	public Long listarScoDescricaoTecnicaPadraoCount(Object objPesquisa) {
		return this.scoDescricaoTecnicaPadraoDAO.listarScoDescricaoTecnicaPadraoCount(objPesquisa);
	}

	@Override
	public Integer obterQuantidadeEslPendenteAF(Integer afnNumero, Integer numero) {
		return this.scoItemAutorizacaoFornDAO.obterQuantidadeEslPendenteAF(afnNumero, numero);
	}

	@Override
	public List<ScoAutorizacaoForn> pesquisarAFComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Object numFornecedor,
			String tipo) {
		return this.scoAutorizacaoFornDAO.pesquisarAFComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor, tipo);
	}

	@Override
	public List<ItensRecebimentoAdiantamentoVO> pesquisarItensRecebimentoAdiantamento(Integer numeroAF) {
		return this.getScoMarcaComercialDAO().pesquisarItensRecebimentoAdiantamento(numeroAF);
	}

	@Override
	public ScoOrigemParecerTecnico obterOrigemParecerTecnicoPorId(Integer id) {
		return this.scoOrigemParecerTecnicoDAO.obterPorChavePrimaria(id);
	}

	@Override
	public String associarMaterialDescricaoTecnicaByMaterial(final ScoMaterial material, List<ScoDescricaoTecnicaPadrao> listaDescricao) {
		return this.scoMaterialDescTecnicaRN.associarMaterialDescTecnica(material, listaDescricao);
	}

	@Override
	public List<ScoDescricaoTecnicaPadrao> buscarListaDescricaoTecnicaMaterial(final ScoMaterial material) {
		return this.scoDescricaoTecnicaPadraoDAO.buscarListaDescricaoTecnicaMaterial(material);
	}

	@Override
	public List<ScoMaterial> listarScoMateriaisAtivosOrderByCodigo(Object objPesquisa) {
		return this.getScoMaterialDAO().listarScoMateriaisAtivosOrderByCodigo(objPesquisa);
	}

	@Override
	public Long contarVFornecedorPorCnpjRaiz(String cnpjRaiz) {
		return this.vScoFornecedorDAO.contarVFornecedorPorCnpjRaiz(cnpjRaiz);
	}

	@Override
	public List<ScoMaterial> getListaMaterialByNomeOrDescOrCodigo(Object param) {
		return getScoMaterialDAO().listarScoMateriaisAtiva(param, Boolean.TRUE);
	}

	@Override
	public Long listarScoMateriaisAtivosCount(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao) {
		return getScoMaterialDAO().listarScoMateriaisAtivosCount(objPesquisa, indEstocavel, pesquisarDescricao);
	}

	@Override
	public Long listarFornecedoresCount(final ScoFornecedor fornecedor) {
		return this.scoProgrCodAcessoFornDAO.listarFornecedoresCount(fornecedor);
	}

	@Override
	public List<ScoProgrCodAcessoFornVO> listarFornecedores(final ScoFornecedor fornecedor, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.acessoFornecedorRN.listarFornecedores(fornecedor, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public List<ScoFornecedor> listarFornecedoresPorNumeroCnpjRazaoSocial(Object param) {
		return getScoFornecedorDAO().listarFornecedoresPorNumeroCnpjRazaoSocial(param);
	}

	@Override
	public Long listarFornecedoresPorNumeroCnpjRazaoSocialCount(Object param) {
		return getScoFornecedorDAO().listarFornecedoresPorNumeroCnpjRazaoSocialCount(param);
	}

	@Override
	public List<ScoFornecedor> listarFornecedoresAtivosPorNumeroCnpjRazaoSocial(Object param) {
		return this.getScoFornecedorDAO().listarFornecedoresAtivosPorNumeroCnpjRazaoSocial(param);
	}

	@Override
	public Long listarFornecedoresAtivosPorNumeroCnpjRazaoSocialCount(Object param) {
		return this.getScoFornecedorDAO().listarFornecedoresAtivosPorNumeroCnpjRazaoSocialCount(param);
	}

	@Override
	public ScoProgrCodAcessoForn buscarScoProgrCodAcessoForn(Integer seq) {
		return this.scoProgrCodAcessoFornDAO.obterPorChavePrimariaEPessoaFisica(seq);
	}

	@Override
	public void persistirAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) throws ApplicationBusinessException {
		this.acessoFornecedorRN.persistirAcessoFornecedor(acessoFornecedor);
	}

	@Override
	public void atualizarAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) throws ApplicationBusinessException {
		this.acessoFornecedorRN.atualizarAcessoFornecedor(acessoFornecedor);
	}

	@Override
	public String buscarPendenciaAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) {
		return this.acessoFornecedorRN.buscarPendenciaAcessoFornecedor(acessoFornecedor);
	}

	@Override
	public void enviarEmailContatos(Integer seq) throws ApplicationBusinessException {
		this.acessoFornecedorRN.enviarEmailContatos(seq);
	}

	@Override
	public void enviarEmailSenha(Integer seq, byte[] jasper) throws ApplicationBusinessException {
		this.acessoFornecedorRN.enviarEmailSenha(seq, jasper);
	}

	@Override
	public ScoProgrCodAcessoForn obterScoProgrCodAcessoFornPorChavePrimaria(Integer codigo) {
		return this.scoProgrCodAcessoFornDAO.obterPorChavePrimaria(codigo, false, ScoProgrCodAcessoForn.Fields.SCO_FORNECEDOR);
	}

	@Override
	public String buscarSituacaoAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor, String colorSituacao) {
		return this.acessoFornecedorRN.buscarSituacaoAcessoFornecedor(acessoFornecedor, colorSituacao);
	}

	private ProgramacaoAutomaticaRN getProgramacaoAutomaticaRN() {
		return this.programacaoAutomaticaRN;
	}

	private AutorizacaoFornecimentoPendenteEntregaON getAutorizacaoFornecimentoPendenteEntregaON() {
		return this.autorizacaoFornecimentoPendenteEntregaON;
	}

	private AutorizacaoFornecimentoPendenteEntregaRN getAutorizacaoFornecimentoPendenteEntregaRN() {
		return this.autorizacaoFornecimentoPendenteEntregaRN;
	}

	private AutFornecimentoEntregasProgramadasRN getAutFornecimentoEntregasProgramadasRN() {
		return this.autFornecimentoEntregasProgramadasRN;
	}

	private ImprimirPrevisaoProgramacaoRN getImprimirPrevisaoProgramacaoRN() {
		return imprimirPrevisaoProgramacaoRN;
	}

	@Override
	@Secure("#{s:hasPermission('planejarEntregasAF','editar')}")
	public String programacaoAutomaticaParcelasAF(Integer afnNumero, Integer numero) throws ApplicationBusinessException {
		return getProgramacaoAutomaticaRN().gerarParcelas(afnNumero, numero);
	}

	@Override
	@Secure("#{s:hasPermission('planejarEntregasAF','editar')}")
	public void programacaoAutomaticaParcelasAF(List<Integer> listNumeroAFs) throws ApplicationBusinessException {
		getProgramacaoAutomaticaRN().gerarParcelas(listNumeroAFs);
	}

	@Override
	public List<ParcelaAfPendenteEntregaVO> pesquisarParcelaAfPendenteEntrega(ParcelasAFPendEntVO vo) {
		return this.getAutorizacaoFornecimentoPendenteEntregaON().pesquisarParcelasPendentesEntrega(vo);
	}

	@Override
	public void validarPeriodoInformadoParcelasAfPendenteEntrega(ParcelasAFPendEntVO vo) throws ApplicationBusinessException {
		getAutorizacaoFornecimentoPendenteEntregaON().validarPeriodoInformadoParcelasAfPendenteEntrega(vo);
	}

	@Override
	public void validarCamposParaPesquisaParcelasAfPendenteEntrega(ParcelasAFPendEntVO vo) throws ApplicationBusinessException {
		getAutorizacaoFornecimentoPendenteEntregaON().validarCamposParaPesquisaParcelasAfPendenteEntrega(vo);
	}

	@Override
	public void selecionarParcelaAfPendenteEntrega(ParcelaAfPendenteEntregaVO vo) {
		getAutorizacaoFornecimentoPendenteEntregaON().selecionarParcelaAfPendenteEntrega(vo);
	}

	@Override
	public void gravarParcelaAfPendenteEntrega(ParcelaAfPendenteEntregaVO vo) throws ApplicationBusinessException {
		getAutorizacaoFornecimentoPendenteEntregaRN().gravarParcelaAfPendenteEntrega(vo);
	}

	@Override
	public List<ParcelasAFVO> recuperaAFParcelas(Integer numeroAutorizacao) {
		return getImprimirPrevisaoProgramacaoRN().recuperaAFPacelas(numeroAutorizacao);
	}

	@Override
	public List<AutFornEntregaProgramadaVO> obtemAutFornecimentosEntregasProgramadas(ScoGrupoMaterial grupoMaterial,
			ScoFornecedor fornecedor, Date dataInicial, Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso)
			throws ApplicationBusinessException {
		return this.getAutFornecimentoEntregasProgramadasRN().obtemAutFornecimentosEntregasProgramadas(grupoMaterial.getCodigo(),
				fornecedor.getNumero(), dataInicial, dataFinal, entregasGlobaisAcesso);
	}

	@Override
	public Long pesquisarContatoFornecedorPorNumeroCount(Integer frnNumero) {
		return getScoContatoFornecedorDAO().pesquisarContatoFornecedorPorNumeroCount(frnNumero);
	}

	@Override
	public List<ScoContatoFornecedor> pesquisarContatoFornecedorPorNumero(Integer frnNumero) {
		return getScoContatoFornecedorDAO().pesquisarContatoFornecedorPorNumero(frnNumero);
	}
	
	@Override
	public Long pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(Object pesquisa) {
		return getVScoFornecedorDAO().pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(pesquisa);
	}

	@Override
	public List<VScoFornecedor> pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(Object pesquisa) {
		return getVScoFornecedorDAO().pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(pesquisa);
	}

	@Override
	public List<ItemAutFornEntregaProgramadaVO> obtemItemAutFornecimentosEntregasProgramadas(Integer afNumero, Date dataInicial,
			Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso) throws ApplicationBusinessException {
		return this.itemAutFornecimentoEntregasProgramadasRN.obtemItemAutFornecimentosEntregasProgramadas(afNumero, dataInicial, dataFinal,
				entregasGlobaisAcesso);
	}

	@Override
	public VScoComprMaterialVO pesquisaUltimaEntrega(Integer codigoMaterial) {
		VScoComprMaterialVO dadosUltimaEntrega = this.vScoComprMaterialDAO.pesquisaUltimaEntrega(codigoMaterial);
		if (dadosUltimaEntrega != null) {
			/*Integer numeroAf = dadosUltimaEntrega.getNumeroLct();*/
			
			ScoFornecedor fornecedor = getScoFornecedorDAO().obterFornecedorPorNumero(dadosUltimaEntrega.getNumeroFrn());
			if (fornecedor != null) {
				dadosUltimaEntrega.setRazaoSocialFornecedor(fornecedor.getRazaoSocial());
				dadosUltimaEntrega.setCnpjFornecedor(fornecedor.getCgc());
			}
		}
		return dadosUltimaEntrega;
	}

	@Override
	public String gerarRelatorioCSVAFsPendentesComprador(ScoGrupoMaterial grupoMaterial, ScoMaterial material) throws IOException,
			ApplicationBusinessException {
		return this.relatorioExcelON.gerarRelatorioCSVAFsPendentesComprador(grupoMaterial, material);
	}

	@Override
	public List<EntregaProgramadaGrupoMaterialVO> obtemGrupoMateriaEntregaProgramada(FiltroGrupoMaterialEntregaProgramadaVO filtro,
			Date dataLiberacao) throws ApplicationBusinessException {
		return this.grupoMaterialEntregaProgramadaRN.obtemAutFornecimentosEntregasProgramadas(filtro, dataLiberacao);
	}

	@Override
	public Date calculaDataLiberacao(FiltroGrupoMaterialEntregaProgramadaVO filtro) throws ApplicationBusinessException {
		return this.grupoMaterialEntregaProgramadaON.calculaDataLiberacao(filtro);
	}

	@Override
	public String validaDatas(FiltroGrupoMaterialEntregaProgramadaVO filtro) throws ApplicationBusinessException {
		return this.grupoMaterialEntregaProgramadaON.validaDatas(filtro);
	}

	@Override
	public ProgramacaoEntregaGlobalTotalizadorVO totalizaValores(List<EntregaProgramadaGrupoMaterialVO> listagem) {
		return this.grupoMaterialEntregaProgramadaON.totalizaValores(listagem);
	}

	@Override
	public ProgramacaoEntregaGlobalTotalizadorVO totalizaValoresEntregaProgramada(List<AutFornEntregaProgramadaVO> listagem) {
		return this.autFornecimentoEntregaProgramadaON.totalizaValores(listagem);
	}

	@Override
	public ProgramacaoEntregaGlobalTotalizadorVO totalizaValoresItensEntregaProgramada(List<ItemAutFornEntregaProgramadaVO> listagem) {
		return this.itemAutFornecimentoEntregaProgramadaON.totalizaValores(listagem);
	}

	@Override
	public List<ScoProgEntregaItemAutorizacaoFornecimento> pesquisarProgEntregaItensAFParcelas(Integer iafAfnNumero, Integer iafNumero,
			Integer numero, Date dataInicial, Date dataFinal) {
		return this.scoProgEntregaItemAFParcelasDAO.pesquisarProgEntregaItensAFParcelas(iafAfnNumero, iafNumero, numero, dataInicial,
				dataFinal);
	}

	@Override
	public ProgramacaoParcelaItemVO pesquisarProgParcelaItem(Integer iafAfnNumero, Integer iafNumero) {
		return this.scoProgEntregaItemAFParcelasDAO.pesquisarProgParcelaItem(iafAfnNumero, iafNumero);
	}

	@Override
	public File gerarArquivoCSVConsultaProgramacaoGeralEntregaAF(List<ProgrGeralEntregaAFVO> listagem) throws ApplicationBusinessException,
			IOException {
		return getPesquisaGeralAFON().getGerarArquivoCSVConsultaProgramacaoGeralEntregaAF(listagem);
	}

	@Override
	public List<ScoMaterial> listarMateriaisAtivosPorGrupoMaterial(Object param, Integer codGrupoMaterial) {
		return getScoMaterialDAO().listarMaterialAtivoPorGrupoMaterial(param, codGrupoMaterial);
	}

	@Override
	public Long obterGrupoMaterialPorSeqDescricaoCount(Object param) {
		return getScoGrupoMaterialDAO().obterGrupoMaterialPorSeqDescricaoCount(param);
	}

	@Override
	public Long listarMaterialAtivoPorGrupoMaterialCount(Object param, Integer codGrupoMaterial) {
		return getScoMaterialDAO().listarMaterialAtivoPorGrupoMaterialCount(param, codGrupoMaterial);
	}

	@Override
	public List<AutorizacaoFornVO> listarAfComSaldoProgramar(Object objPesquisa) {
		return this.scoItemAutorizacaoFornDAO.listarAfComSaldoProgramar(objPesquisa);
	}

	@Override
	public Long listarAfComSaldoProgramarCount(Object objPesquisa) {
		return this.scoItemAutorizacaoFornDAO.listarAfComSaldoProgramarCount(objPesquisa);
	}

	@Override
	public Long listarSSItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSS) {
		return getConsultaSCSSON().listarSSItensSCSSVOCount(filtroConsultaSS);
	}

	@Override
	public Long listarSCItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSC) {
		return getConsultaSCSSON().listarSCItensSCSSVOCount(filtroConsultaSC);
	}

	@Override
	public List<ItensSCSSVO> listarSCItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FiltroConsSCSSVO filtroConsultaSC) {
		return getConsultaSCSSON().montaSCItensSCSSVO(firstResult, maxResults, orderProperty, asc, filtroConsultaSC);
	}

	@Override
	public List<ItensSCSSVO> listarSSItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FiltroConsSCSSVO filtroConsultaSS) {
		return getConsultaSCSSON().montaSSItensSCSSVO(firstResult, maxResults, orderProperty, asc, filtroConsultaSS);
	}

	@Override
	public void setaValoresDefaults(FiltroConsSCSSVO filtro) throws ApplicationBusinessException {
		this.getConsultaSCSSON().setaValoresDefaults(filtro);
	}

	@Override
	public List<ScoLicitacao> buscarLicitacaoPorNumero(Integer numeroPac, String[] modalidades) {
		return this.getScoLicitacaoDAO().buscarLicitacaoPorNumero(numeroPac, modalidades);
	}

	@Override
	public AtaRegistroPrecoVO pesquisarInfoAtaDeRegistroDePreco(Integer pacCodigo, Integer fornecedor) {
		return this.getScoLicitacaoDAO().pesquisarInfoAtaDeRegistroDePreco(pacCodigo, fornecedor);
	}

	@Override
	public int obterNumeroPenalidades(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterNumeroPenalidades(fornecedor);
	}

	@Override
	public long obterNumeroAdvertencias(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterNumeroAdvertencias(fornecedor);
	}

	@Override
	public long obterNumeroOcorrencias(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterNumeroOcorrencias(fornecedor);
	}

	@Override
	public long obterNumeroSuspensoes(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterNumeroSuspensoes(fornecedor);
	}

	@Override
	public long obterNumeroMulta(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterNumeroMulta(fornecedor);
	}

	@Override
	public boolean existePenalidade(ScoFornecedor fornecedor) {
		return getManterFornecedorON().existePenalidade(fornecedor);
	}

	@Override
	public Integer obterProximoNumeroCrcFornecedor(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterProximoNumeroCrcFornecedor(fornecedor);
	}

	@Override
	public Integer obterSeqFornecedorPorNumero(ScoFornecedor fornecedor) {
		ScoProgrCodAcessoForn acessoFornecedor = this.scoProgrCodAcessoFornDAO.obterPorFornecedor(fornecedor);
		return acessoFornecedor != null ? acessoFornecedor.getSeq() : null;
	}
	
	@Override
	public DominioTipoFaseSolicitacao consultaAFMaterialServico(Integer nroAF) {
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultaAFMaterialServico(nroAF);
	}

	@Override
	public List<ScoMaterial> obterMateriaisOrteseseProtesesAgenda(BigDecimal paramVlNumerico, String objPesquisa, String ordenacao) {
		return this.getScoMaterialDAO().obterMateriaisOrteseseProtesesAgenda(paramVlNumerico, objPesquisa, ordenacao);
	}

	@Override
	public List<MaterialOpmeVO> obterMateriaisOrteseseProtesesAgendaComValoreMarca(BigDecimal paramVlNumerico, String objPesquisa, String ordenacao, Date dtAgenda) {
		return this.getScoMaterialDAO().obterMateriaisOrteseseProtesesAgendaComValoreMarca(paramVlNumerico, objPesquisa, ordenacao,dtAgenda);
	}

	@Override
	public List<MaterialOpmeVO> obterValorMateriaisProcedimento(Short seq) {
		return this.getScoMaterialDAO().obterValorMateriaisProcedimento(seq);
	}
	
	
	@Override
	@Secure("#{s:hasPermission('pesquisarAndamentoProcessoCompras','consultar')}")
	public String gerarRelatorioAP(Integer numeroPac) throws ApplicationBusinessException, IOException {
		return this.relatorioExcelON.gerarRelatorioAP(numeroPac);
	}

	@Override
	@Secure("#{s:hasPermission('gerarArquivoEncerramentoPAC','consultar')}")
	public String gerarRelatorioEP(Integer anoEP) throws ApplicationBusinessException, IOException {
		return this.relatorioExcelON.gerarRelatorioEP(anoEP);
	}

	@Override
	public List<ScoMaterial> obterMateriaisOrteseseProtesesSB(BigDecimal paramVlNumerico, Object objPesquisa) {
		return this.getScoMaterialDAO().obterMateriaisOrteseseProtesesSB(paramVlNumerico, objPesquisa);
	}
	
	public ArquivoURINomeQtdVO gerarArquivoTextoContasPeriodo(FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) 
			throws ApplicationBusinessException {
		return this.relatorioExcelON.gerarArquivoTextoContasPeriodo(filtroRelatoriosExcelVO);
	}
	
	/**
	 * Método responsável por realizar a chamada do método gerarArquivoTextoTitulosBloqueados do objeto relatorioExcelON
	 * 
	 * @return Retorna um objeto ArquivoURINomeQtdVO com a uri, nome, tamanho do arquivo CSV para download
	 */
	public ArquivoURINomeQtdVO gerarArquivoTextoTitulosBloqueados() 
			throws ApplicationBusinessException {
		return this.relatorioExcelON.gerarArquivoTextoTitulosBloqueados();
	}

	protected ScoPedidoMatExpedienteRN getScoPedidoMatExpedienteRN() {
		return scoPedidoMatExpedienteRN;
	}

	protected void setScoPedidoMatExpedienteRN(ScoPedidoMatExpedienteRN scoPedidoMatExpedienteRN) {
		this.scoPedidoMatExpedienteRN = scoPedidoMatExpedienteRN;
	}

	@Override
	public void persistirSocio(ScoSocios socio) {
		this.getScoSociosDAO().persistir(socio);
	}

	@Override
	public boolean verificarSocioExistentePorCPF(Long cpf) {
		return getScoSociosDAO().verificarSocioExistentePorCPF(cpf);
	}

	@Override
	public void atualizarSocio(ScoSocios socio) {
		this.getScoSociosDAO().atualizar(socio);
	}

	@Override
	public void removerScoSocios(ScoSocios socio) {
		this.getScoSociosDAO().remover(socio);
	}

	@Override
	public Long listarSociosCount(Integer codigo, String nome, String rg, Long cpf) {
		return getScoSociosDAO().listarSociosFornecedoresCount(codigo, nome, rg, cpf);
	}

	@Override
	public List<ScoSocios> listarSocios(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer codigo, String nome, String rg, Long cpf) {
		return getScoSociosDAO().listarSociosFornecedores(firstResult, maxResult, orderProperty, asc, codigo, nome, rg, cpf);
	}

	@Override
	public boolean verificarSocioExistentePorRG(String rg) {
		return this.getScoSociosDAO().verificarSocioExistentePorRG(rg);
	}

	private ScoSociosDAO getScoSociosDAO(){
		return scoSociosDAO;
	}

	@Override
	public void persistirSocioFornecedor(ScoSociosFornecedores socioFornecedor) {
		this.getScoSociosFornecedoresDAO().persistir(socioFornecedor);
		
	}	

	@Override
	public void atualizarSocioFornecedor(ScoSociosFornecedores socioFornecedor) {
		this.getScoSociosFornecedoresDAO().atualizar(socioFornecedor);
	}

	@Override
	public ScoSociosFornecedores buscarScoSociosFornecedores(ScoSociosFornecedoresId id) {
		return getScoSociosFornecedoresDAO().obterPorChavePrimaria(id);
	}


	@Override
	public void removerScoSocioFornecedor(ScoSociosFornecedores scoSociosFornecedores) {
		this.getScoSociosFornecedoresDAO().removerPorId(scoSociosFornecedores.getId());
	}

	@Override
	public Long listarSociosFornecedoresCount(Integer codigo, String nome, String rg, Long cpf, Integer numeroFornecedor) {
		return getScoSociosFornecedoresDAO().listarSociosFornecedoresCount(codigo, nome, rg, cpf, numeroFornecedor);
	}

	@Override
	public Long quantidadeFornecedorPorSeqSocio(Integer seq) {
		return getScoSociosFornecedoresDAO().quantidadeFornecedorPorSeqSocio(seq);
	}

	
	@Override
	public List<ScoSociosFornecedores> listarSociosFornecedores(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, String nome, String rg, Long cpf,	Integer numeroFornecedor) {
		return getScoSociosFornecedoresDAO().listarSociosFornecedores(firstResult, maxResult, orderProperty, asc, codigo, 
				nome, rg, cpf, numeroFornecedor);
	}

	private ScoSociosFornecedoresDAO getScoSociosFornecedoresDAO(){
		return scoSociosFornecedoresDAO;	
	}

	@Override
	public Long listarClassifMatNiv5PorGrupoCount(Integer codGrupo, Object parametro) {
		return this.scoClassifMatNiv5DAO.listarClassifMatNiv5PorGrupoCount(codGrupo, parametro);
	}

	@Override
	public List<ScoClassifMatNiv5> listarClassifMatNiv5PorGrupo(Integer codGrupo, Object parametro) {
		return this.scoClassifMatNiv5DAO.listarClassifMatNiv5PorGrupo(codGrupo,parametro);
	}

	@Override
	public List<MaterialClassificacaoVO> listarMateriasPorClassificacao(Long cn5, Integer codGrupo) {
		return getScoMateriaisClassificacoesDAO().listarMateriasPorClassificacao(cn5, codGrupo);
	}

	@Override
	public ScoMateriaisClassificacoes obterScoMateriaisClassificacoes(Object idRemocao) {
		return getScoMateriaisClassificacoesDAO().obterPorChavePrimaria(idRemocao);
	}

	@Override
	public List<TituloPendenteVO> pesquisarTituloPendentePagamento(
			Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo,
			Date dtInicialEmissaoDocumentoFiscal,
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor)
			throws BaseException {
		return this.getTituloPendentePagamentoON()
				.pesquisarTituloPendentePagamento(dtInicialVencimentoTitulo,
						dtFinalVencimentoTitulo,
						dtInicialEmissaoDocumentoFiscal,
						dtFinalEmissaoDocumentoFiscal, fornecedor);
	}
	
	@Override
	public ArquivoURINomeQtdVO gerarCSVTituloPendentePagamento(
			Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo,
			Date dtInicialEmissaoDocumentoFiscal,
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor)
			throws BaseException {
		return this.getTituloPendentePagamentoON()
				.gerarCSVTituloPendentePagamento(dtInicialVencimentoTitulo,
						dtFinalVencimentoTitulo,
						dtInicialEmissaoDocumentoFiscal,
						dtFinalEmissaoDocumentoFiscal, fornecedor);
	}	
	
	@Override
	public List<ScoGrupoServico> pesquisarGrupoServicos(Integer codigo,
			String descricao, Boolean engenharia, Boolean tituloAvulso, Integer first, Integer max,
			String order, boolean asc) {
		return getScoGrupoServicoDAO().pesquisarGrupoServicoPorCodigoDescricao(
				codigo, descricao, engenharia, tituloAvulso, first, max, order, asc);
	}

	@Override
	public Long pesquisarGrupoServicosCount(Integer codigo, String descricao,
			Boolean tituloAvulso, Boolean engenharia) {
		return this.getScoGrupoServicoDAO()
				.pesquisarGrupoServicoPorCodigoDescricaoCount(codigo,
						descricao, tituloAvulso, engenharia);
	}

	@Override
	public void salvarScoGrupoServico(ScoGrupoServico grupoServico)
			throws BaseException {
		this.scoGrupoServicoRN.inserir(grupoServico);
	}

	@Override
	public void editarScoGrupoServico(ScoGrupoServico grupoServico)
			throws BaseException {
		this.scoGrupoServicoRN.atualizar(grupoServico);
	}

	@Override
	public ScoGrupoServico obterGrupoServico(Integer codigo) {
		return getScoGrupoServicoDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public void excluirScoGrupoServico(ScoGrupoServico grupoServico)
			throws BaseException {
		this.scoGrupoServicoRN.excluir(grupoServico);
	}

	public FcpTituloPendentePagamentoON getTituloPendentePagamentoON() {
		return this.tituloPendentePagamentoON;
	}
	
	/**
	 * 
	 */
	@Override
	public FcpCalendarioVencimentoTributos pesquisarFcpCalendarioVencimentoTributoPorCodigo(Integer numeroCalendarioVencimento){
		return fcpCalendarioVencimentoTributosON.pesquisarFcpCalendarioVencimentoTributoPorCodigo(numeroCalendarioVencimento); 
	}

	/**
	 * Métoque que recebe o objeto com os parâmetros de pesquisa e chama o método on correspondente a pesquisa de calendários de vencimentos
	 * 
	 * @param inicioApuracao Data de início de apuração dos calendários
	 * @param fimApuracao    Data do fim da apuração dos calendários
	 * @param tipoTributo    Tipo do tributo do calendárdio(s)
	 * @return Lista com os calendários de vencimentos de tributo
	 * */
	@Override
	public List<FcpCalendarioVencimentoTributosVO> listarCalendariosVencimentos(Date dataApuracao, DominioTipoTributo tipoTributoVencimento)
			throws BaseException {
		return fcpCalendarioVencimentoTributosON.pesquisarFcpCalendarioVencimentoTributo(dataApuracao, tipoTributoVencimento);
	}

	@Override
	public List<MovimentacaoFornecedorVO> pesquisarMovimentacaoFornecedores(
			Integer frnNumero, DominioSituacaoTitulo situacao,
			Integer notaRecebimento, String serie, Integer nroAf,
			Short nroComplemento, Long nf, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc)
			throws ApplicationBusinessException {
		return movimentacaoFornecedorON.pesquisarMovimentacaoFornecedores(frnNumero, situacao, notaRecebimento, serie, nroAf, 
				nroComplemento, nf, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarMovimentacaoFornecedoresCount(Integer frnNumero,
			DominioSituacaoTitulo situacao, Integer notaRecebimento,
			String serie, Integer nroAf, Short nroComplemento, Long nf)
			throws ApplicationBusinessException {		
		return movimentacaoFornecedorON.pesquisarMovimentacaoFornecedoresCount(frnNumero, situacao, notaRecebimento, serie, nroAf, nroComplemento, nf);
	}
	
	/**
	 * Método responsável por retornar a coleção de dados
	 */
	@Override
	public List<DatasVencimentosFornecedorVO> pesquisaDatasVencimentoFornecedor(Object fornecedor) {
		return fcpRelatorioVencimentoFornecedorON.pesquisarDatasVencimentoFornecedor(fornecedor);
	}
	
	@Override
	public List<RelatorioMovimentacaoFornecedorVO> pesquisaMovimentacaoFornecedor(Object fornecedor, Date dataInicio, Date dataFim) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		return relatorioMovimentacaoFornecedorON.pesquisaMovimentacaoFornecedor(fornecedor, dataInicio, dataFim);
	}
	
	@Override public void enviarEmailMovimentacaoFornecedor(String contatoEmail, byte[] jasper) throws ApplicationBusinessException {
		this.relatorioMovimentacaoFornecedorON.enviarEmailMovimentacaoFornecedor(contatoEmail, jasper);
	}
	
	/**
	 * Método responsável por retornar o objeto com as informções do hospital
	 */
	@Override
	public AghParametros pesquisarHospital(Object parametro) {
		return fcpRelatorioVencimentoFornecedorON.pesquisarHospital(parametro);
	}
	
	/**
	 * Insere um registro na tabela da entidade
	 * Lança exceção se os períodos da apuração se sobrepõem
	 * 
	 * @param inicioApuracao
	 * @param fimApuracao
	 * @param tipoTributo
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	@Override
	public FcpCalendarioVencimentoTributos persistirCalendarioVencimento( FcpCalendarioVencimentoTributos fcpCalendarioVencimento)
			throws ApplicationBusinessException {
		return fcpCalendarioVencimentoTributosON.persistirCalendarioVencimento(fcpCalendarioVencimento);
	}
	
	/**
	 * Remove um registro na tabela da entidade FcpCalendarioVencimentoTributo
	 * 
	 * @param calendarioVencimentoTributo Calendário do vencimento do tributo a ser removido
	 * */
	@Override
	public void removerCalendarioVencimentoTributo(
			FcpCalendarioVencimentoTributos calendarioVencimentoTributo) {
		fcpCalendarioVencimentoTributosON.remover(calendarioVencimentoTributo);
	}

	/******************************************************************************************************************/

	@Override
	public List<ScoMarcaModelo> pesquisarMacaModelo(Object marcaModelo) {
		return this.scoMarcaModeloDAO.pesquisarMarcaModelo(marcaModelo);
	}

	@Override
	public void adicionarMarcaModelo(ScoMarcaModelo marcaModelo,
			ScoMaterial material) throws ApplicationBusinessException {
		this.getCadastrarMarcaModeloMaterialRN().adicionarMarcaModelo(marcaModelo, material);
	}

	@Override
	public void excluirMarcaModelo(MarcaModeloMaterialVO itemExclusao,
			ScoMaterial material) {
			this.getCadastrarMarcaModeloMaterialRN().excluirMarcaModelo(itemExclusao, material);
	}

	@Override
	public List<MarcaModeloMaterialVO> pesquisarMarcaModeloMaterial(Integer codigoMaterial, Integer codigoMarca, Integer codigoModelo) throws ApplicationBusinessException {
		return this.getCadastrarMarcaModeloMaterialRN().pesquisarMarcaModeloMaterial(codigoMaterial, codigoMarca, codigoModelo);
	}


	public CadastrarMarcaModeloMaterialRN getCadastrarMarcaModeloMaterialRN(){
		return cadastrarMarcaModeloMaterialRN;
	}

	@Override
	public List<ScoGrupoMaterial> pesquisarScoGrupoMaterial(Integer codigo, String descricao, Boolean patrimonio, Boolean engenhari, Boolean nutricao,
			Boolean exigeForn, Boolean geraMovEstoque, Boolean controleValidade, Boolean dispensario, Integer ntdCodigo, Integer codMercadoriaBb,
			DominioDiaSemanaMes diaFavEntgMaterial, Short seqAgrupa) {
		return getScoGrupoMaterialDAO().pesquisarScoGrupoMaterial(codigo, descricao, patrimonio, engenhari, nutricao, exigeForn, geraMovEstoque, controleValidade,
				dispensario, ntdCodigo, codMercadoriaBb, diaFavEntgMaterial, seqAgrupa);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarGrupoMaterial','executar')}")
	public void excluirScoGrupoMaterial(Integer codigo)
			throws ApplicationBusinessException {
		getScoGrupoMaterialRN().excluirScoGrupoMaterial(codigo);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarGrupoMaterial','executar')}")
	public void persistirScoGrupoMaterial(ScoGrupoMaterial grupoMaterial)throws ApplicationBusinessException{
		getScoGrupoMaterialRN().persistirScoGrupoMaterial(grupoMaterial);
		
	}

	private ScoGrupoMaterialRN getScoGrupoMaterialRN(){
		return scoGrupoMaterialRN;	
	}

	@Override
	public List<MateriaisParalClassificacaoVO> pesquisarMateriaisParaClassificar(
			ScoMaterial material) {
		return getScoMaterialDAO().pesquisarClassificaMaterial(material);
	}

	@Override
	public List<ScoMaterial> listarScoMateriaisPorGrupo(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao, Integer codGrupoMaterial) {
		return getScoMaterialDAO().listarScoMateriaisPorGrupo(objPesquisa, indEstocavel, pesquisarDescricao, codGrupoMaterial);
	}

	@Override
	public Long listarScoMateriaisPorGrupoCount(Object objPesquisa, Boolean indEstocavel, Boolean pesquisarDescricao, Integer codGrupoMaterial) {
		return this.getScoMaterialDAO().listarScoMateriaisPorGrupoCount(objPesquisa, indEstocavel, pesquisarDescricao, codGrupoMaterial);
	}

	@Override
	public ScoFornecedor buscarFornecedorPorNumero(Integer numeroFornecedor) {
		return this.getScoFornecedorDAO().obterFornecedorPorNumero(numeroFornecedor);
	}

	@Override
	public List<ScoSociosFornecedores> listarFornecedoresPorSeqSocio(Integer seq) {
		return getScoSociosFornecedoresDAO().listarFornecedoresPorSeqSocio(seq);
	}

	@Override
	public ScoSocios buscarSocioPorSeq(Integer seq) {
		return this.getScoSociosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void removerScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1)
			throws ApplicationBusinessException {
		getScoClassifMatRN().removerScoClassifMatNiv1(scoClassifMatNiv1);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void removerScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2)
			throws ApplicationBusinessException {
		getScoClassifMatRN().removerScoClassifMatNiv2(scoClassifMatNiv2);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void removerScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3)
			throws ApplicationBusinessException {
		getScoClassifMatRN().removerScoClassifMatNiv3(scoClassifMatNiv3);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void removerScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4)
			throws ApplicationBusinessException {
		getScoClassifMatRN().removerScoClassifMatNiv4(scoClassifMatNiv4);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void removerScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5) {
		getScoClassifMatRN().removerScoClassifMatNiv5(scoClassifMatNiv5);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void inserirScoClassifMatNiv1(Integer codigo, String descricao) {
		getScoClassifMatRN().inserirScoClassifMatNiv1(codigo, descricao);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void inserirScoClassifMatNiv2(Integer gmtCodigo, Integer codigo, String descricao) {
		getScoClassifMatRN().inserirScoClassifMatNiv2(gmtCodigo, codigo, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void inserirScoClassifMatNiv3(Integer cn2cn1GmtCodigo, Integer cn2cn1Codigo, Integer cn2Codigo, String descricao) {
		getScoClassifMatRN().inserirScoClassifMatNiv3(cn2cn1GmtCodigo, cn2cn1Codigo, cn2Codigo, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void inserirScoClassifMatNiv4(Integer cn3cn2cn1GmtCodigo, Integer cn3cn2cn1Codigo, Integer cn3cn2Codigo, Integer cn3Codigo, String descricao) {
		getScoClassifMatRN().inserirScoClassifMatNiv4(cn3cn2cn1GmtCodigo, cn3cn2cn1Codigo, cn3cn2Codigo, cn3Codigo, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void inserirScoClassifMatNiv5(ScoClassifMatNiv4 scoClassifMatNiv4,
			String descricaoNiv5) {
		getScoClassifMatRN().inserirScoClassifMatNiv5(scoClassifMatNiv4, descricaoNiv5);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void atualizarScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1,
			String descricao) {
		getScoClassifMatRN().atualizarScoClassifMatNiv1(scoClassifMatNiv1, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void atualizarScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2,
			String descricao) {
		getScoClassifMatRN().atualizarScoClassifMatNiv2(scoClassifMatNiv2, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void atualizarScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3,
			String descricao) {
		getScoClassifMatRN().atualizarScoClassifMatNiv3(scoClassifMatNiv3, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void atualizarScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4,
			String descricao) {
		getScoClassifMatRN().atualizarScoClassifMatNiv4(scoClassifMatNiv4, descricao);
		
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarClassificacaoGrupoMaterial','executar')}")
	public void atualizarScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5,
			String descricao) {
		getScoClassifMatRN().atualizarScoClassifMatNiv5(scoClassifMatNiv5, descricao);
	}

	private ScoClassifMatRN getScoClassifMatRN(){
		return scoClassifMatRN;
	}

	@Override
	public ScoClassifMatNiv1 obterscoClassifMatNiv1PorChavePrimaria(
			ScoClassifMatNiv1Id id) {
		return scoClassifMatNiv1DAO.obterPorChavePrimaria(id, true, ScoClassifMatNiv1.Fields.SCO_CLASSIF_MAT_NIV2);
	}

	@Override
	public ScoClassifMatNiv2 obterscoClassifMatNiv2PorChavePrimaria(
			ScoClassifMatNiv2Id id) {
		return scoClassifMatNiv2DAO.obterPorChavePrimaria(id, true, ScoClassifMatNiv2.Fields.SCO_CLASSIF_MAT_NIV3);
	}

	@Override
	public ScoClassifMatNiv3 obterscoClassifMatNiv3PorChavePrimaria(
			ScoClassifMatNiv3Id id) {
		return scoClassifMatNiv3DAO.obterPorChavePrimaria(id, true, ScoClassifMatNiv3.Fields.SCO_CLASSIF_MAT_NIV4);
	}

	@Override
	public ScoClassifMatNiv4 obterscoClassifMatNiv4PorChavePrimaria(
			ScoClassifMatNiv4Id id) {
		return scoClassifMatNiv4DAO.obterPorChavePrimaria(id, true, ScoClassifMatNiv4.Fields.SCO_CLASSIF_MAT_NIV5);
	}
	
	@Override
	public List<ScoFornecedorVO> buscarCertificadoDeRegistroDeCadastro(Integer numeroFornecedor) {
		return this.getScoFornecedorDAO().buscarCertificadoDeRegistroDeCadastro(numeroFornecedor);
	}

	@Override
	public List<ScoRefCodes> obterClassificacaoEconomica(String rvDomain, String lowValue) {
		return this.getScoRefCodesDAO().obterClassificacaoEconomica(rvDomain, lowValue);
	}

	@Override
	public ScoFornecedorVO bindValoresLista(List<ScoFornecedorVO> listVO) {
		return certificadoRegistroCadastroON.bindValoresLista(listVO);
	}
	
	@Override
	public ScoLicitacao buscarLicitacaoPorNumero(Integer numeroParam) {
		return getScoLicitacaoDAO().buscaLicitacoesPorNumero(numeroParam);
	}
	
	@Override
     public List<ScoFornRamoComercial> obterRamosComerciais(Object param, Integer numeroFornecedor) {
          return scoFornRamoComercialDAO.obterRamosComerciais(param, numeroFornecedor);
    }
	        
    @Override
    public Long obterRamosComerciaisCount(Object param, Integer numero){
          return scoFornRamoComercialDAO.obterRamosComerciaisCount(param, numero);
     }
    
	@Override
	public List<ClassificacaoVO> obterClassificacoes(Object param) {
		return cadastroMateriaisRamoComercialFornecedorON
				.obterClassificacoes(param);
	}

	@Override
	public Long obterClassificacoesCount(Object param) {
		return scoClassifMatNiv5DAO.pesquisarClassificacoesCount(param);
	}
	
	@Override
    public Long listarClassificacoesCount(Integer numero, Short rcmCodigo) throws BaseException{                
          return this.scoClassifMatNiv5DAO.listarClassificacoesCount(numero, rcmCodigo);                
    }
	
	@Override
    public List<ClassificacaoVO> listarClassificacoes(Integer numero, Short rcmCodigo, String orderProperty, boolean asc) throws BaseException{                
		return this.cadastroMateriaisRamoComercialFornecedorON.listarClassificacoes(numero, rcmCodigo, orderProperty, asc);        
    }
	
	@Override
	public void inserirScoFnRamoComerClas(ScoFnRamoComerClas scoFnRamoComerClas)
			throws BaseException {
		this.cadastrarMateriaisRamoComercialFornecedorRN
				.inserirScoFnRamoComerClas(scoFnRamoComerClas);
	}

	@Override
	public void removerScoFnRamoComerClas(
			ScoFnRamoComerClasId scoFnRamoComerClasId) throws BaseException {
		this.scoFnRamoComerClasDAO
				.removerScoFnRamoComerClas(scoFnRamoComerClasId);
	}

	@Override
	public String geraArquivoUltimasCompras(
			List<ScoUltimasComprasMaterialVO> lista) throws IOException {
		return this.relatorioExcelON.geraArquivoUltimasCompras(lista);
	}
	
	@Override
	public List<ScoClassifMatNiv1> listarScoClassifMatNiv1PorGrupoMaterial(
			Integer  codigoGrupoMaterial) {
		return this.scoClassifMatNiv1DAO.listarScoClassifMatNiv1PorGrupoMaterial(codigoGrupoMaterial);
	}
	
	@Override
	public List<ScoRamoComercial> listarRamosComerciaisAtivos(Object param) {
		return scoRamoComercialDAO.listarRamosComerciaisSugestion(param);
	}

	@Override
	public List<ContasCorrentesFornecedorVO> buscarDadosContasCorrentesFornecedor(
			CadastroContasCorrentesFornecedorVO filtro) {
		return getContasCorrentesFornecedorRN().buscarDadosContasCorrentesFornecedor(filtro);
	}
	
	@Override
	public void deletarContaCorrenteFornecedor(
			ContasCorrentesFornecedorVO itemExclusao) throws BaseException {
		getContasCorrentesFornecedorRN().deletarContaCorrenteFornecedor(itemExclusao);
	}

	@Override
	public void verificaRegistrosExistentes(
			List<ContasCorrentesFornecedorVO> validationData) throws ApplicationBusinessException {
		getContasCorrentesFornecedorRN().verificaRegistrosExistentes(validationData);		
	}

	@Override
	public void verificaContaPreferencialParaFornecedor(Integer numeroFornecedor,
			DominioSimNao preferencial, boolean isUpdate)
			throws ApplicationBusinessException {
		getContasCorrentesFornecedorRN().verificaContaPreferencialParaFornecedor(numeroFornecedor, preferencial, isUpdate);
		
	}

	@Override
	public ScoContaCorrenteFornecedor montarContaCorrenteFornecedor(
			CadastroContasCorrentesFornecedorVO filtro) {
		return getContasCorrentesFornecedorRN().montarContaCorrenteFornecedor(filtro);
	}

	
	@Override
	public void atualizarContaCorrenteFornecedor(
			ContasCorrentesFornecedorVO item) throws BaseException {
		getContasCorrentesFornecedorRN().atualizarContaCorrenteFornecedor(item);
		
	}

	protected ContasCorrentesFornecedorRN getContasCorrentesFornecedorRN() {
		return contasCorrentesFornecedorRN;
	}

	@Override
	public void inserirContaCorrenteFornecedor(
			ScoContaCorrenteFornecedor element) {
		scoContaCorrenteFornecedorDAO.persistir(element);	
		
	}

	@Override
	public Long listarCriterioEscolhaFornCount(Short codigo, String descricao,
			DominioSituacao situacao) {
		return getScoCriterioEscolhaFornDAO().listaCriterioEscolhaFornCount(codigo, descricao, situacao);
	}

	@Override
	public List<ScoCriterioEscolhaForn> listarCriterioEscolhaForn(Short codigo,
			String descricao, DominioSituacao situacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getScoCriterioEscolhaFornDAO().listaCriterioEscolhaForn(codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public ScoCriterioEscolhaForn obterCriterioEscolhaForn(Short codigo) {
		return getScoCriterioEscolhaFornDAO().obterPorChavePrimaria(codigo);
	}

	private ScoCriterioEscolhaFornDAO getScoCriterioEscolhaFornDAO() {
		return scoCriterioEscolhaFornDAO;
	}

	@Override
	public void persistirCriterioEscolhaForn(
			ScoCriterioEscolhaForn criterioEscolhaForn)
			throws ApplicationBusinessException {
		getScoCriterioEscolhaFornRN().inserir(criterioEscolhaForn);
	}

	@Override
	public void atualizarCriterioEscolhaForn(
			ScoCriterioEscolhaForn criterioEscolhaForn)
			throws ApplicationBusinessException {
		getScoCriterioEscolhaFornRN().atualizar(criterioEscolhaForn);
		
	}

	private ScoCriterioEscolhaFornRN getScoCriterioEscolhaFornRN() {
		return scoCriterioEscolhaFornRN;
	}

	@Override
	public List<ScoEtapaPac> obterEtapaPacPorLicitacao(Integer numero) {
		return this.getPlanejamentoAndamentoPacRN().obterEtapaPacPorLicitacao(numero);
	}

	@Override
	public ModPacSolicCompraServicoVO obterModaldiadePacSocilitacao(
			Integer numero, String codigo) {
		return this.getPlanejamentoAndamentoPacRN().obterModaldiadePacSocilitacao(numero, codigo);
	}

	@Override
	public List<EtapasRelacionadasPacVO> acompanharHistorico(Integer numero,
			String descricaoObjeto, String codigoModalidade) {
		return this.getPlanejamentoAndamentoPacRN().acompanharHistorico(numero, descricaoObjeto, codigoModalidade);
	}

	@Override
	public List<LocalPACVO> pesquisarLocaisPac(Integer numero,
			String codigoModalidade, String descricaoObjeto) {
		return this.getPlanejamentoAndamentoPacRN().pesquisarLocaisPac(numero, codigoModalidade, descricaoObjeto);
	}

	@Override
	public List<EtapaPACVO> pesquisaEtapasPac(Object etapa, Integer numeroLicitacao, LocalPACVO localPACVO, String descricaoObjeto, String codigoModalidade){
		return this.getPlanejamentoAndamentoPacRN().pesquisaEtapasPac(etapa, numeroLicitacao, localPACVO, descricaoObjeto, codigoModalidade);
	}

	@Override
	public List<EtapasRelacionadasPacVO> pesquisarEtapas(Integer numeroLicitacao, ModPacSolicCompraServicoVO modPacSolicCompraServicoVO,
															DominioSituacaoEtapaPac situacaoEtapa, LocalPACVO localPACVO,
															RapServidoresId idServidor, EtapaPACVO etapaVO, Date dataPac) {
		return this.getPlanejamentoAndamentoPacRN().pesquisarEtapas(numeroLicitacao, modPacSolicCompraServicoVO, situacaoEtapa, localPACVO,
																idServidor, etapaVO, dataPac);
	}

	@Override
	public void gravarNovaEtapa(ScoLocalizacaoProcesso localNovaEtapa, Integer numeroLicitacao,
							String descricaoNovaEtapa, Short novaEtapaTempoPrevisto) throws ApplicationBusinessException{
		this.getPlanejamentoAndamentoPacRN().gravarNovaEtapa(localNovaEtapa, numeroLicitacao, descricaoNovaEtapa, novaEtapaTempoPrevisto);
		
	}

	@Override
	public void atualizarEtapa(Integer codigoEtapa, DominioSituacaoEtapaPac situacaoEtapaAtualizar, Short tempoPrevistoAtualizar, String descricaoObsAtualizar) {
		this.getPlanejamentoAndamentoPacRN().atualizarEtapa(codigoEtapa, situacaoEtapaAtualizar, tempoPrevistoAtualizar, descricaoObsAtualizar);
	}

	@Override
	public List<HistoricoLogEtapaPacVO> pesquisarHistoricoEtapa(Integer codigoEtapa) {
		return this.getPlanejamentoAndamentoPacRN().pesquisarHistoricoEtapa(codigoEtapa);
	}

	protected PlanejamentoAndamentoPacRN getPlanejamentoAndamentoPacRN() {
		return planejamentoAndamentoPacRN;
	}
	
	public List<TituloNrVO> pesquisarListaTitulosNR(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, TituloNrVO tituloNrVO) throws ApplicationBusinessException {
		return this.consultarTituloRN.pesquisarListaTitulosNR(firstResult, maxResult, orderProperty, asc, tituloNrVO);
	}
	
	public Long pesquisarCountTitulosNR(TituloNrVO tituloNrVO) throws ApplicationBusinessException {
		return this.consultarTituloRN.pesquisarCountTitulosNR(tituloNrVO);
	}

	/**
	 * @return the consultarTituloRN
	 */
	public ConsultarTituloRN getConsultarTituloRN() {
		return consultarTituloRN;
	}

	/**
	 * @param consultarTituloRN the consultarTituloRN to set
	 */
	public void setConsultarTituloRN(ConsultarTituloRN consultarTituloRN) {
		this.consultarTituloRN = consultarTituloRN;
	}
	
	private FcpRelatorioDividaResumoON getFcpRelatorioDividaResumoON() {
		return fcpRelatorioDividaResumoON;
	}
	
	@Override
	public List<RelatorioDividaResumoVO> pesquisarDividaResumoAtrasados(Date dataFinal)
			throws BaseException {
		return this.getFcpRelatorioDividaResumoON()
				.pesquisarDividaResumoAtrasados(dataFinal);
	}

	@Override
	public List<RelatorioDividaResumoVO> pesquisarDividaResumoAVencer(
			Date dataFinal) throws BaseException {
		return this.getFcpRelatorioDividaResumoON()
				.pesquisarDividaResumoAVencer(dataFinal);
	}
	
	@Override
	public String gerarCSVDividaResumo(Date dataFinal)
			throws IOException, BaseException {
		return this.getFcpRelatorioDividaResumoON()
				.gerarCSVDividaResumo(dataFinal);
	}
	
	@Override
	public List<PagamentosRealizadosPeriodoPdfVO> pesquisarPagamentosRealizadosPeriodoPDF(Date inicioPeriodo, Date finalPeriodo, Integer codVerbaGestao) throws ApplicationBusinessException {
		return fcpPagamentosRealizadosPeriodoON.pesquisarPagamentosRealizadosPeriodoPDF(inicioPeriodo, finalPeriodo, codVerbaGestao);
	}
	
	
	/**
	 * Método para gerar o arquivo CSV dos pagamentos realizados em um período
	 * 
	 * @param dataDividaInicial Data inicial da busca dos pagamentos realizados
	 * @param dataDividaFinal Data final da busca dos pagamentos realizados
	 * 
	 * @return Retorno da lista de vo's dos pagamentos realizados
	 * */
	public ArquivoURINomeQtdVO gerarArquivoTextoPagamentosRealizadosPeriodo (FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) throws ApplicationBusinessException {
		return this.relatorioExcelON.gerarArquivoTextoPagamentosRealizadosPeriodo(filtroRelatoriosExcelVO);
	}	

	
	@Override
	public List<FcpTipoDocumentoPagamento> pesquisarTiposDocumentoPagamento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		return fcpTipoDocumentoPagamentoON.pesquisarTiposDocumentoPagamento(firstResult, maxResult, orderProperty, asc, fcpTipoDocumentoPagamento);
	}

	@Override
	public Long pesquisarCountTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) {
		return fcpTipoDocumentoPagamentoON.pesquisarCountTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
	}

	@Override
	public void inserirTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException {
		this.fcpTipoDocumentoPagamentoON.inserirTipoDocumentoDocumentoPagamento(fcpTipoDocumentoPagamento);
	}

	@Override
	public void atualizarTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException {
		this.fcpTipoDocumentoPagamentoON.atualizarTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
	}

	@Override
	public void cadastrarNomeComercialDaMarca(ScoMarcaComercial marcaComercial,
			String nome, boolean active) throws BaseException {
		getManterNomesComerciaisMarcaRN().cadastrarNomeComercialDaMarca(marcaComercial, nome, active);	
	}

	@Override
	public void alterarNovoNomeComercialDaMarca(ScoNomeComercial nomeSobEdicao,
			String nome, boolean active) throws BaseException {
		getManterNomesComerciaisMarcaRN().alterarNovoNomeComercialDaMarca(nomeSobEdicao, nome, active);
	}

	@Override
	public Long buscaMarcasComeriaisCount(ScoMarcaComercial marcaComercial) {
		return getManterNomesComerciaisMarcaRN().buscaMarcasComeriaisCount(marcaComercial);
	}

	@Override
	public List<ScoNomeComercial> buscaMarcasComeriais(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			ScoMarcaComercial marcaComercial) {
		return getManterNomesComerciaisMarcaRN().buscaMarcasComeriais(firstResult, maxResult, orderProperty, asc, marcaComercial);
	}

	protected ManterNomesComerciaisMarcaRN getManterNomesComerciaisMarcaRN() {
		return manterNomesComerciaisMarcaRN;
	}

	protected void setManterNomesComerciaisMarcaRN(
			ManterNomesComerciaisMarcaRN manterNomesComerciaisMarcaRN) {
		this.manterNomesComerciaisMarcaRN = manterNomesComerciaisMarcaRN;
	}

	@Override
	public List<ScoTipoOcorrForn> buscarTipoOcorrenciaFornecedor(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, CadastroTipoOcorrenciaFornecedorVO filtro) {
		return this.getCadastroTipoOcorrenciaFornecedorRN().buscarTipoOcorrenciaFornecedor(firstResult, maxResults, orderProperty, asc, filtro);
	}

	@Override
	public Long buscarTipoOcorrenciaFornecedorCount(
			CadastroTipoOcorrenciaFornecedorVO filtro) {
		return this.getCadastroTipoOcorrenciaFornecedorRN().buscarTipoOcorrenciaFornecedorCount(filtro);
	}

	protected CadastroTipoOcorrenciaFornecedorRN getCadastroTipoOcorrenciaFornecedorRN() {
		return cadastroTipoOcorrenciaFornecedorRN;
	}

	@Override
	public ScoTipoOcorrForn buscarOcorrenciaFornecedor(Short codigo) {
		return this.scoTipoOcorrFornDAO.obterPorChavePrimaria(codigo);
	}

	@Override
	public void inserirOcorrenciaFornecedor(ScoTipoOcorrForn ocorrencia)
			throws BaseException {
		this.scoTipoOcorrFornDAO.persistir(ocorrencia);	
	}
	
	@Override
	@Secure("#{s:hasPermission('consultarESEntradasSemEmpenhoOuAssinatura','imprimir')}")
	public String gerarRealatorioES() throws ApplicationBusinessException,
			IOException {
		return relatorioExcelON.gerarRealatorioES();
	}
	@Override
	public Integer contadorLinhasRelatorioEntradaSemEmpenhoSemAssinaturaAF()
			throws ApplicationBusinessException {
		return relatorioExcelON.contadorLinhasRelatorioEntradaSemEmpenhoSemAssinaturaAF();
	}

	@Override
	public ScoMaterial getMaterialPreferencialCUM(Integer codigoMat) {
		return scoMaterialDAO.getMaterialPreferencialCUM(codigoMat);
	}
	
	@Override
	public String montarArqPregaoBB(List<Integer> nrosPac, AghParametros paramDir) throws ApplicationBusinessException{
		return scoItemLicitacaoRN.montarArqPregaoBB(nrosPac, paramDir);
	}
	
	@Override
	public List<ScoMaterial> obterMateriaisPorCodigoOuDescricao(String parametro) {
		return this.scoMaterialDAO.obterMateriaisPorCodigoOuDescricao(parametro);
	}

	@Override
	public Long obterMateriaisPorCodigoOuDescricaoCount(String parametro) {
		return this.scoMaterialDAO.obterMateriaisPorCodigoOuDescricaoCount(parametro);
	}
	
	@Override
	public List<ScoFaseSolicitacao> pesquisarFasePorItemLicitacao(Integer lctNumero, Short numeroItem) {
		return this.getScoFaseSolicitacaoDAO().pesquisarFasePorItemLicitacao(lctNumero,numeroItem);
	}

	@Override
	public List<ScoMaterial> listarMaterial(String objPesquisa, Boolean indEstocavel, Integer gmtCodigo, DominioSituacao situacao) {
		return this.getScoMaterialDAO().listarMaterialAtivo(objPesquisa, indEstocavel, gmtCodigo, null);
	}
	
	public String montarSolCodBB(List<Integer>  P_PG_BB_NrosCli, AghParametros P_PG_BB_ParamDir) throws ApplicationBusinessException{
		return scoItemLicitacaoRN.montarSolCodBB(P_PG_BB_NrosCli, P_PG_BB_ParamDir);
	}

	@Override
	public List<ScoModalidadeLicitacao> pesquisarModalidadesProcessoAdministrativo(
			String parametro) {
		return scoModalidadeLicitacaoDAO.pesquisarModalidadesProcessoAdministrativo(parametro);
	}

	@Override
	public List<ScoLicitacao> pesquisarLicitacoesPorNumero(String parametro) {
		return scoLicitacaoDAO.pesquisarLicitacoesPorNumero(parametro);
	}

	@Override
	public List<RapServidoresVO> pesquisarGestorPorNomeOuMatricula(String parametro) {
		return rapServidoresDAO.pesquisarGestorPorNomeOuMatricula(parametro);
	}

	@Override
	public Long pesquisarLicitacoesVOCount(ScoLicitacaoVO scoLicitacaoVOFiltro,
			Date dataInicio, Date dataFim, Date dataInicioGerArqRem,
			Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno) {
		return scoLicitacaoDAO.pesquisarLicitacoesVOCount(scoLicitacaoVOFiltro, dataInicio, dataFim, dataInicioGerArqRem, dataFimGerArqRem, pacPendenteEnvio, pacPendenteRetorno);
	}

	@Override
	public List<ScoLicitacaoVO> pesquisarLicitacoesVO(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoLicitacaoVO scoLicitacaoVOFiltro,
			Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno) {
		return scoLicitacaoDAO.pesquisarLicitacoesVO(firstResult, maxResult, orderProperty, asc, scoLicitacaoVOFiltro, dataInicio, dataFim, dataInicioGerArqRem, dataFimGerArqRem, pacPendenteEnvio, pacPendenteRetorno);
	}

	@Override
	public void validarDataPregaoEletronicoBB(Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		scoLicitacaoON.validarDatasPregaoEletronicoBB(dataInicio, dataFim);
	}

	@Override
	public List<ScoLicitacao> pesquisarLicitacoesPorNomeArquivoRetorno(String nome) {
		return scoLicitacaoDAO.pesquisarLicitacoesPorNomeArquivoRetorno(nome);
	}

	@Override
	public void validarFormatoDataPregaoEletronicoBB(Date data)
			throws ApplicationBusinessException {
		scoLicitacaoON.validarFormatoDataPregaoEletronicoBB(data);
	}

	@Override
	public Long pesquisarModalidadesProcessoAdministrativoCount(String parametro) {
		return scoModalidadeLicitacaoDAO.pesquisarModalidadesProcessoAdministrativoCount(parametro);
	}

	@Override
	public Long pesquisarLicitacoesPorNumeroCount(String parametro) {
		return scoLicitacaoDAO.pesquisarLicitacoesPorNumeroCount(parametro);
	}

	@Override
	public Long pesquisarGestorPorNomeOuMatriculaCount(String parametro) {
		return rapServidoresDAO.pesquisarGestorPorNomeOuMatriculaCount(parametro);
	}
	
	@Override
	public void importarArqPregaoBBRetorno(final AghJobDetail job) throws ApplicationBusinessException{
		scoLicitacaoRN.importarArqPregaoBBRetorno(job);
	}	
	
	@Override
	public List<Long> obterLoteLicitacaoSelecionada(Integer nroPac) {
		return this.scoLoteLicitacaoDAO.obterLoteLicitacaoSelecionada(nroPac);
	}
	
	@Override
	public List<ScoLicitacaoImpressaoVO> imprimirHistoricoDetalhadoLicitacaoHomologada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException {
		return this.scoLicitacaoImpressaoRN.imprimirHistoricoDetalhadoLicitacaoHomologada(numPac, nomeArquivoRetorno);
	}
	
	@Override
	public void validarSolicitacaoPregaoEletronico(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException {
		scoLicitacaoImpressaoRN.validarSolicitacaoPregaoEletronico(licitacoesSelecionadas);
	}
  	@Override
	public String validarNomeArqCad(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException {
		return scoLicitacaoImpressaoRN.validarNomeArqCad(licitacoesSelecionadas);
	}
	
	@Override
	public List<ScoLicitacaoCadastradaImpressaoVO> imprimirLicitacaoCadastrada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException {
		return this.scoLicitacaoImpressaoRN.imprimirLicitacaoCadastrada(numPac, nomeArquivoRetorno);
	}

	@Override
	public boolean consultarFornecedorAdjudicado(Integer numLicitacao, Integer numFornecedor){
		return scoItemPropostaFornecedorDAO.consultarFornecedorAdjudicado(numLicitacao, numFornecedor);
	}
	
	@Override
	public List<ScoLicitacaoImpressaoVO> lerArquivoProcessadoLicitacaoHomologada(Integer numPac, String nomeArquivoProcessado) throws ApplicationBusinessException {
		return this.scoLicitacaoImpressaoRN.lerArquivoProcessadoLicitacaoHomologada(numPac, nomeArquivoProcessado);
	}
	
	@Override
	public Long listarMaterialCount(String objPesquisa, Boolean indEstocavel, DominioSituacao situacao) {
		return this.listarMaterialCount(objPesquisa, indEstocavel, situacao);
	}
	
	@Override
	public Long contarParcelasPagamentoProposta(ScoCondicaoPagamentoPropos cond){
		return this.scoParcelasPagamentoDAO.contarParcelasPagamentoProposta(cond);
	} 
	
	@Override
	public List<ScoMarcaComercial> pesquisarMarcaComercialPorCodigoDescricaoSemLucene(Object param) {
		return getScoMarcaComercialDAO().pesquisarMarcaComercialPorCodigoDescricaoSemLucene(param);
	}
	
	@Override
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricaoSemLucene(Object param, ScoMarcaComercial marcaComercial, Boolean indAtivo) {
		return this.scoMarcaModeloDAO.pesquisarMarcaModeloPorCodigoDescricaoSemLucene(param, marcaComercial, indAtivo);
	}

	@Override
	public List<ConsultaGeralTituloVO> consultaGeralTitulos(FiltroConsultaGeralTituloVO filtro){
		return fcpTituloDAO.consultaGeralTitulos(filtro);
	}
	
	@Override
	public void excluirTitulosTelaConsultaGeral(ConsultaGeralTituloVO item){
		consultaGeralTitulosRN.excluirTitulo(item);
	}
	
	@Override
	public List<FcpTipoTitulo> obterListaTipoTitulo(String parametro){
		return tipoTituloDAO.obterListaTipoTitulo(parametro);
	}
	
	@Override
	public Long obterCountTipoTitulo(String parametro){
		return tipoTituloDAO.obterCountTipoTitulo(parametro);				
	}
	
	@Override
	public List<ScoGrupoServico> obterListaGrupoServicoPorCodigoOuDescricao(String filter) {
		return this.getScoGrupoServicoDAO().obterListaGrupoServicoPorCodigoOuDescricao(filter);
	}

	@Override
	public Long obterCountGrupoServicoPorCodigoOuDescricao(String filter) {
		return this.getScoGrupoServicoDAO().obterCountGrupoServicoPorCodigoOuDescricao(filter);
	}

	@Override
	public List<ScoGrupoMaterial> obterListaGrupoMaterialPorCodigoOuDescricao(String filter) {
		return this.getScoGrupoMaterialDAO().obterListaGrupoMaterialPorCodigoOuDescricao(filter);
	}

	@Override
	public Long obterCountGrupoMaterialPorCodigoOuDescricao(String filter) {
		return this.getScoGrupoMaterialDAO().obterCountGrupoMaterialPorCodigoOuDescricao(filter);
	}

	@Override
	public List<ScoServico> obterListaServicoAtivosPorCodigoOuNome(String filter) {
		return this.getScoServicoDAO().obterListaServicosAtivosPorCodigoOuNome(filter);
	}

	@Override
	public Long obterCountServicoAtivosPorCodigoOuNome(String filter) {
		return this.getScoServicoDAO().obterCountServicosAtivosPorCodigoOuNome(filter);
	}

	@Override
	public List<ScoMaterial> obterListaMaterialAtivosPorCodigoNomeOuDescricao(String filter) {
		return this.getScoMaterialDAO().obterListaMateriaisAtivosPorCodigoNomeOuDescricao(filter);
	}

	@Override
	public Long obterCountMaterialAtivosPorCodigoNomeOuDescricao(String filter) {
		return this.getScoMaterialDAO().obterCountMateriaisAtivosPorCodigoNomeOuDescricao(filter);
	}

	@Override
	public List<SolicitacaoTituloVO> recuperarListaPaginadaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return solicitacaoTituloRN.recuperarListaPaginadaSolicitacaoTitulo(filtro, pontoParada, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public List<SolicitacaoTituloVO> recuperarListaCompletaSolicitacaoTitulo(FiltroSolicitacaoTituloVO filtro, Short pontoParada) {
		return solicitacaoTituloRN.recuperarListaCompletaSolicitacaoTitulo(filtro, pontoParada);
	}

	@Override
	public List<FcpClassificacaoTitulo> obterListaClassificacaoTituloAtivosPorCodigoOuDescricao(String filter) {
		return fcpClassificacaoTituloDAO.obterListaClassificacaoTituloAtivosPorCodigoOuDescricao(filter);
	}

	@Override
	public Long obterCountClassificacaoTituloAtivosPorCodigoOuDescricao(String filter) {
		return fcpClassificacaoTituloDAO.obterCountClassificacaoTituloAtivosPorCodigoOuDescricao(filter);
	}
	
	@Override
	public List<SolicitacaoTituloVO>  obterTitulosSolicitacaoCompraServico(Integer ttlseq){
		return solicitacaoTituloRN.obterTitulosSolicitacaoCompraServico(ttlseq);
	}
	
	@Override
	public void alterarSolicitacoes(List<SolicitacaoTituloVO> listaSolicitacao,ConsultaGeralTituloVO tituloAlterado) throws  ApplicationBusinessException, EJBException {
		solicitacaoTituloRN.alterarSolicitacoes(listaSolicitacao,tituloAlterado);
	}
	
	@Override
	public List<FcpTitulo> gerarTitulosSemLicitacao(TituloSemLicitacaoVO titulo, List<SolicitacaoTituloVO> listaSolicitacao) throws ApplicationBusinessException {
		return solicitacaoTituloRN.gerarTitulosSemLicitacao(titulo, listaSolicitacao);
	}

	@Override
	public Boolean existeAcessoFornecedorPorFornecedorDtEnvio(ScoFornecedor fornecedor) {
		return this.scoProgrCodAcessoFornDAO.existeAcessoFornecedorPorFornecedorDtEnvio(fornecedor);
	}
	
	@Override
	public ScoAutorizacaoFornecedorPedido obterScoAutorizacaoFornecimentoPedido(ScoAutorizacaoFornecedorPedidoId id) {
		return this.scoAutorizacaoFornecedorPedidoDAO.obterPorChavePrimaria(id);
	}
	
	@Override
	public void persistirScoAutorizacaoFornecedorPedido(ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido){
		this.scoAutorizacaoFornecedorPedidoRN.persistir(autorizacaoFornecedorPedido);
	}
	
	@Override
	public void persistirScoProgrAcessoFornAfp(ScoProgrAcessoFornAfp scoProgrAcessoFornAfp){
		scoProgrAcessoFornAfpDAO.persistir(scoProgrAcessoFornAfp);
	}
	
	@Override
	public Long contarProgrEntregasPorAfeAfeNum(Integer afNum, Integer afeNumero) {
		return scoCumXProgrEntregaDAO.contarProgrEntregasPorAfeAfeNum(afNum, afeNumero);
	}
	
	@Override
	public Boolean verificarAcessosFronAFP(Integer afnNumero, Integer afeNumero, ScoFornecedor forcenedor){
		return this.scoProgrAcessoFornAfpDAO.verificarAcessosFronAFP(afnNumero, afeNumero, forcenedor);
	}

}
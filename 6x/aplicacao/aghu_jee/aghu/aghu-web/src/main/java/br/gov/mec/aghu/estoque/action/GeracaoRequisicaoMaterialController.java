package br.gov.mec.aghu.estoque.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioOrderBy;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirRequisicaoMaterialController;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirRequisicaoMaterialRemotaController;
import br.gov.mec.aghu.estoque.pesquisa.action.PesquisaRequisicaoMaterialPaginatorController;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemRmsId;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

import com.itextpdf.text.DocumentException;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class GeracaoRequisicaoMaterialController extends ActionController {

	private static final long serialVersionUID = 5492261471420258066L;

	private static final Log LOG = LogFactory.getLog(GeracaoRequisicaoMaterialController.class);

	private static final String EXCECAO_CAPTURADA = "Exceção Capturada:";
	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";
	
	
	private static final String IMPRIMIR_REQUISICAO_MATERIAL_PDF = "estoque-imprimirRequisicaoMaterialPdf";

	private static final String REQUISICAO_MATERIAL_EFETIVAR = "estoque-efetivarRequisicaoMaterial";

	private static final String PAGE_GERACAO_RM = "estoque-geracaoRequisicaoMaterial";
	private static final String PAGE_CONSULTAR_CATALOGO_MATERIAL = "estoque-consultarCatalogoMaterial";

	public enum GeracaoRequisicaoMaterialControllerExceptionCode implements BusinessExceptionCode {
		AIP_PACIENTE_NAO_ENCONTRADO, MENSAGEM_ITEM_RM_JA_CADASTRADO, MENSAGEM_COMPRAS_MATERIAL_ITEM_WEB;
	}

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB 
	private IParametroFacade parametroFacade;
	
	@Inject
	private PesquisaRequisicaoMaterialPaginatorController pesquisaRequisicaoMaterialPaginatorController;

	@Inject
	private ImprimirRequisicaoMaterialController imprimirRequisicaoMaterialController;

	@Inject
	private ImprimirRequisicaoMaterialRemotaController imprimirRequisicaoMaterialRemotaController;

	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";

	private SceReqMaterial reqMaterial;
	private Integer seqReq;

	/* variáveis usadas para ou alterar excluir os itens de uma requisição */
	private Integer rmsSeq;
	private Integer ealSeq;

	private Integer rmsSeqEx;
	private Integer ealSeqEx;
	private SceItemRms sceItemRmseSelecionado;


	private List<SceItemRms> sceItemRmses = new ArrayList<SceItemRms>();
	private SceEstoqueAlmoxarifado estoqueAlmo;
	private Integer quantidade;
	private Boolean verificaDisableSB = false;

	private String origemPesquisa;

	private ScoMaterial material;
	private String prontuario;
	private Integer pacCodigo;
	private String pacNome;
	private Integer atdSeq;
	private AipPacientes paciente;
	private AghAtendimentos atendimento;
	private Integer atdSeqSelecionado;
	private Boolean mostrarPanelAvisoAlmox;
	private Boolean cameFromCatalogo;
	private Integer codigoMaterial;

	private Integer pacCodigoFonetica;
	private List<AghAtendimentosVO> atendimentosPaciente;
	
	private Boolean buscaManual = false;

	private RapServidores servidorLogado;
	private FccCentroCustos ccAtuacao;
	private boolean isAlmoxarife;

	private Set<Integer> listaHierarquica;
	private boolean possuiCaractGppg;
	private FccCentroCustos ccFipe;

	// Leitura de Código de Barra
	private String numeroEtiqueta;
	private Boolean trocouMaterial;
	private boolean chkCcSolic;
	private boolean chkCcAplic;
	private ScoCaracteristicaUsuarioCentroCusto sugereCcSolic;
	private ScoCaracteristicaUsuarioCentroCusto sugereCcAplic;
	
	// #42040
	private boolean comprarItemMaterialWeb;
	private boolean materialCarregou;
	
	private boolean imprimir;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void inicio() {
		try {
			this.limparPaciente();

			if (seqReq != null) {
				reqMaterial = this.estoqueFacade.obterRequisicaoMaterial(this.seqReq);
				cancelarEdicao();
				atualizaListaItens();
				if (reqMaterial != null && reqMaterial.getAtendimento() != null && reqMaterial.getAtendimento().getPaciente() != null
						&& pacCodigoFonetica == null) {
					this.prontuario = reqMaterial.getAtendimento().getPaciente().getProntuario().toString();
					this.pacCodigo = reqMaterial.getAtendimento().getPaciente().getCodigo();
					this.selecionarPacienteConsulta();
				}
			}

			if (servidorLogado == null) {
				try {
					this.servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
				} catch (ApplicationBusinessException e) {
					LOG.error("Não foi possível obter o servidor logado");
				}
				this.setCcAtuacao(this.centroCustoFacade.obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao());
				isAlmoxarife = this.getCascaFacade().usuarioTemPerfil(this.obterLoginUsuarioLogado(), "ADM29");

				ccFipe = this.comprasCadastrosBasicosFacade.obterCcAplicacaoAlteracaoRmGppg(this.getServidorLogadoFacade()
						.obterServidorLogado());
				setPossuiCaractGppg(false);
				if (ccFipe != null) {
					this.listaHierarquica = centroCustoFacade.pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
					possuiCaractGppg = (this.listaHierarquica != null && !this.listaHierarquica.isEmpty());
				}
				this.setarPreferencias();
			}

			if (reqMaterial == null && seqReq == null) {
				this.reqMaterial = new SceReqMaterial();
				this.setSceItemRmses(new ArrayList<SceItemRms>());
				this.cancelarEdicao();

				this.setarCentroCusto();
			}

			if (pacCodigoFonetica != null) {
				this.pacCodigo = pacCodigoFonetica;
				this.selecionarPacienteConsulta();
			}
			if (this.cameFromCatalogo != null && this.cameFromCatalogo && this.codigoMaterial != null) {
				this.carregarMaterialCatalogo();
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}


	public void setarPreferencias() {
		this.setSugereCcSolic(this.comprasCadastrosBasicosFacade
				.obterCaracteristica(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_RM));
		this.setSugereCcAplic(this.comprasCadastrosBasicosFacade
				.obterCaracteristica(DominioCaracteristicaCentroCusto.PREENCHER_CC_APLIC_RM));

		if (this.getSugereCcSolic() != null) {
			this.setChkCcSolic(true);
		} else {
			this.setChkCcSolic(false);
		}

		if (this.getSugereCcAplic() != null) {
			this.setChkCcAplic(true);
		} else {
			this.setChkCcAplic(false);
		}
	}

	public void setarCentroCusto() {
		/* centro de custo do usuario logado */
		this.reqMaterial.setCentroCusto(centroCustoFacade.pesquisarCentroCustoAtuacaoLotacaoServidor());
		this.reqMaterial.setCentroCustoAplica(this.reqMaterial.getCentroCusto());

		if (!isChkCcSolic()) {
			this.reqMaterial.setCentroCusto(null);
		}

		if (!isChkCcAplic()) {
			this.reqMaterial.setCentroCustoAplica(null);
		}
	}
	
	private void carregarMaterialCatalogo() {
		this.setCameFromCatalogo(false);
		if (codigoMaterial != null) {
			this.material = this.comprasFacade.obterMaterialPorId(codigoMaterial);
			this.verificarMaterialSelecionado();
		} else {
			this.material = null;
			this.estoqueAlmo = null;
		}		
	}
	
	public void atualizarPreferenciaSugestaoCc(Boolean isAplic) {
		if (isAplic) {
			if (this.getSugereCcAplic() == null) {
				this.setSugereCcAplic(this.comprasCadastrosBasicosFacade
						.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.PREENCHER_CC_APLIC_RM));
				try {
					this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.getSugereCcAplic());
				} catch (ApplicationBusinessException e) {
					this.setSugereCcAplic(null);
					this.setChkCcAplic(false);
					this.apresentarExcecaoNegocio(e);
				}
			} else {
				try {
					this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.getSugereCcAplic()/*.getSeq()*/);
					this.setSugereCcAplic(null);
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
					this.setChkCcAplic(true);
				}
			}
		} else {
			if (this.getSugereCcSolic() == null) {
				this.setSugereCcSolic(this.comprasCadastrosBasicosFacade
						.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_RM));
				try {
					this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.getSugereCcSolic());
				} catch (ApplicationBusinessException e) {
					this.setSugereCcSolic(null);
					this.setChkCcSolic(false);
					this.apresentarExcecaoNegocio(e);
				}
			} else {
				try {
					this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.getSugereCcSolic()/*.getSeq()*/);
					this.setSugereCcSolic(null);
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
					this.setChkCcSolic(true);
				}
			}
		}
	}

	public String selecionarMaterial() {
		String ret = null;
		if (reqMaterial.getAlmoxarifado() != null) {
			ret = PAGE_CONSULTAR_CATALOGO_MATERIAL;
		} else {
			String msg = this.getBundle().getString("MENSAGEM_ALMOXARIFADO_RM_CATALOGO");
			apresentarMsgNegocio(Severity.WARN, msg);
		}
		return ret;
	}

	public Boolean habilitarEdicao() {
		Boolean ret = Boolean.FALSE;

		if (this.reqMaterial != null) {
			// se ainda nao tiver situacao eh inclusao, pode fazer tudo
			if (this.reqMaterial.getIndSituacao() == null) {
				ret = true;
			} else {
				// se eh o usuario solicitante somente pode alterar Geradas...
				if (this.reqMaterial.getServidor().equals(this.servidorLogado)) {
					if (this.reqMaterial.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)) {
						ret = true;
					}
				}

				// se foi solicitado pelo almoxarifado para o centro de custo do
				// usuario logado pode alterar Geradas e Confirmadas
				FccCentroCustos centroCusto = this.estoqueFacade.obterRequisicaoMaterial(reqMaterial.getSeq()).getAlmoxarifado().getCentroCusto(); 
				if(this.reqMaterial.getCentroCustoAplica() != null){
					if (centroCusto.equals(this.reqMaterial.getCentroCusto())
							&& this.reqMaterial.getCentroCustoAplica().equals(this.ccAtuacao)) {
						if (this.reqMaterial.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)
								&& this.reqMaterial.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
							ret = true;
						}
					}
				}
				// se eh o almoxarife
				if (centroCusto.equals(this.ccAtuacao) && isAlmoxarife) {
					if (this.reqMaterial.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)
							|| (this.reqMaterial.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C))) {
						ret = true;
					}
				}

				// se eh do gppg pode alterar RMs da hierarquia do CC Aplic =
				// <cc da hierarquia> geradas
				if (possuiCaractGppg && listaHierarquica.contains(this.reqMaterial.getCentroCustoAplica().getCodigo())
						&& (this.reqMaterial.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G))) {
					ret = true;
				}
			}
		}
		return ret;
	}

	public String efetivar() {
		return REQUISICAO_MATERIAL_EFETIVAR;
	}

	public void limparEstoqueAlmoarifado() {
		this.estoqueAlmo = null;
	}
	
	public void verificarMaterialSelecionado() {
		try{
			validaMaterialInformado();
			
			this.estoqueFacade.validarMaterialRM(material, reqMaterial, sceItemRmses, isAlmoxarife);
			
			if (!reqMaterial.getAlmoxarifado().equals(this.material.getAlmoxarifado())) {
				this.mostrarPanelAvisoAlmox = true;
			} else {
				this.mostrarPanelAvisoAlmox = false;
			}
			this.estoqueAlmo = this.estoqueFacade.pesquisarEstoqueAlmoxarifadoFornecedorPadrao(this.reqMaterial.getAlmoxarifado().getSeq(), this.material.getCodigo());
			if (ealSeq != null && rmsSeq != null) {
				this.trocouMaterial = true;
			} else {
				this.trocouMaterial = false;
			}
		}
		catch(ApplicationBusinessException e){
			this.setMaterial(null);
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String cancelar(Boolean confirmar){
		
		if (confirmar) {
			try {
				this.confirmar();
			} catch (JRException e) {
				this.getBundle().getString("MENSAGEM_ERRO_CONFIRMAR_RM");
			} catch (SystemException e) {
				this.getBundle().getString("MENSAGEM_ERRO_CONFIRMAR_RM");
			} catch (IOException e) {
				this.getBundle().getString("MENSAGEM_ERRO_CONFIRMAR_RM");
			}
		}
		this.seqReq = null;
		this.reqMaterial = null;
		this.pacCodigoFonetica = null;
		this.atdSeqSelecionado = null;
		pesquisaRequisicaoMaterialPaginatorController.setIsGeracaoDeRequisicao(Boolean.FALSE);
		
		return getOrigemPesquisa();
	}

	public String cancelarRm() {
		SceReqMaterial sceReqMateriaisEstornar = this.estoqueFacade.obterRequisicaoMaterial(this.reqMaterial.getSeq());
		
		if (sceReqMateriaisEstornar != null) {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}
			
			try {
				estoqueFacade.cancelarRequisicaoMaterial(sceReqMateriaisEstornar, nomeMicrocomputador);
				//estoqueFacade.flush();
				this.getBundle().getString("MENSAGEM_SUCESSO_CANCELAMENTO_REQUISICAO_MATERIAL");
				return this.cancelar(false);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	
	
	/**
	 * Visualizacao da Impressap
	 * @throws DocumentException 
	 */
	public String visualizar() throws BaseException, JRException, SystemException, IOException, DocumentException {
		imprimirRequisicaoMaterialController.setNumeroRM(reqMaterial.getSeq());
		imprimirRequisicaoMaterialController.setDuasVias(DominioSimNao.N);
		imprimirRequisicaoMaterialController.setVoltarPara("geracao");
		imprimirRequisicaoMaterialController.print();

		return IMPRIMIR_REQUISICAO_MATERIAL_PDF;
	}

	/**
	 * Impressão Direta
	 * 
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() throws BaseException, JRException, SystemException, IOException {
		imprimirRequisicaoMaterialController.setNumeroRM(reqMaterial.getSeq());
		imprimirRequisicaoMaterialController.setDuasVias(DominioSimNao.N);
		imprimirRequisicaoMaterialController.directPrint();
	}

	/**
	 * Grava RM e item da RM através do Leitor de Código de Barras
	 */
	public void adicionarCodigoBarras() {

		try {

			if (this.reqMaterial.getAlmoxarifado() == null) {
				this.apresentarMsgNegocio("sbAlmoxarifado", Severity.ERROR, CAMPO_OBRIGATORIO, "Almoxarifado");
				return;
			}

			if (this.reqMaterial.getCentroCusto() == null) {
				this.apresentarMsgNegocio("sbCCReq", Severity.ERROR, CAMPO_OBRIGATORIO, "CC Requisição");
				return;
			}

			if (this.reqMaterial.getCentroCustoAplica() == null) {
				this.apresentarMsgNegocio("sbCCAplicacao", Severity.ERROR, CAMPO_OBRIGATORIO, "CC Aplicação");
				return;
			}

			// Quando o número da etiqueta é válido...
			if (this.numeroEtiqueta != null && (this.numeroEtiqueta.length() == 14 || this.numeroEtiqueta.length() == 15)) {

				// Obtém a Dispensação do medicamento
				SceLoteDocImpressao dispensacao = this.estoqueFacade.getLoteDocImpressaoByNroEtiqueta(this.numeroEtiqueta);

				if (dispensacao != null) {

					// Obtém o Material
					ScoMaterial material = dispensacao.getMaterial();

					// Obtém o Estoque Almoxarifado através do Leitor de Código
					// de Barras
					Short seqAlmoxarifado = this.reqMaterial.getAlmoxarifado().getSeq();
					Integer codigoMaterial = material.getCodigo();

					this.estoqueAlmo = this.estoqueFacade.pesquisarEstoqueAlmoxarifadoFornecedorPadrao(seqAlmoxarifado, codigoMaterial);
					this.quantidade = 1;

					// Adiciona Automaticamente e informa ao método que a
					// leitura foi através do Código de Barras
					this.adicionar(true);
				}

				this.numeroEtiqueta = null;

			}

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} finally {

			// Resgata identificadores do Item de RM
			this.rmsSeq = null;
			this.ealSeq = null;

			// O padrão é sempre limpar o número da etiqueta lido
			this.numeroEtiqueta = null;
		}

	}

	public String iniciarRequisicao() {
		this.setSeqReq(null);
		this.setRmsSeq(null);
		this.setEalSeq(null);
		this.setReqMaterial(null);
		this.mostrarPanelAvisoAlmox = false;
		this.trocouMaterial = false;
		return PAGE_GERACAO_RM;
	}

	/**
	 * Grava item da RM
	 */
	public void adicionar() {

		if (this.reqMaterial.getAlmoxarifado() == null) {
			this.apresentarMsgNegocio("sbAlmoxarifado", Severity.ERROR, CAMPO_OBRIGATORIO, "Almoxarifado");
			return;
		}

		if (this.reqMaterial.getCentroCusto() == null) {
			this.apresentarMsgNegocio("sbCCReq", Severity.ERROR, CAMPO_OBRIGATORIO, "CC Requisição");
			return;
		}

		if (this.reqMaterial.getCentroCustoAplica() == null) {
			this.apresentarMsgNegocio("sbCCAplicacao", Severity.ERROR, CAMPO_OBRIGATORIO, "CC Aplicação");
			return;
		}

		this.adicionar(false);
		this.material = null;
		this.mostrarPanelAvisoAlmox = false;		
	}

	/**
	 * Grava item da RM
	 */
	public void adicionar(Boolean isCodigoBarras) {

		// Grava a requisição de material caso a mesma não esteja gravado.

		Boolean novoRegistroRequisicao = false;

		try {

			if (this.reqMaterial.getSeq() == null) {

				novoRegistroRequisicao = true;
				this.reqMaterial.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);

				// Resgata e força Centro de Custo Requisitante
				final FccCentroCustos centroCustoAtachadoAplica = this.estoqueFacade.obterFccCentroCustos(this.reqMaterial
						.getCentroCustoAplica().getCodigo());
				this.reqMaterial.setCentroCustoAplica(centroCustoAtachadoAplica);

				// Resgata e força Centro de Custo Requisitante
				final FccCentroCustos centroCustoAtachado = this.estoqueFacade.obterFccCentroCustos(this.reqMaterial.getCentroCusto()
						.getCodigo());
				this.reqMaterial.setCentroCusto(centroCustoAtachado);

				this.gravarAtualizarRequisicaoMaterialEmGravarItens();
				this.gravarAtualizarItemRequisicaoMaterial(isCodigoBarras);

			} else if (this.reqMaterial.getAlmoxarifado() == null || this.reqMaterial.getCentroCusto() == null
					|| this.reqMaterial.getCentroCustoAplica() == null) {

				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_REQUISICAO");

			} else {

				this.gravarAtualizarItemRequisicaoMaterial(isCodigoBarras);

			}

		} catch (BaseException e) {

			if (novoRegistroRequisicao) {

				this.reqMaterial.setSeq(null);
				this.reqMaterial.setIndSituacao(null);
				this.reqMaterial.setDtGeracao(null);
				this.reqMaterial.setIndImpresso(null);
				this.reqMaterial.setAtendimento(null);

			}

			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Altera item da RM
	 * 
	 * @throws BaseException
	 */
	public void alterarItemRequisicaoMaterial() throws BaseException {
		SceItemRms itemRmsOriginal = null;

		try {
			if (rmsSeq != null && ealSeq != null) {

				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}

				// nao pode trocar o material direto pois ele indiretamente faz parte da pk do item (eal_seq)
				if (trocouMaterial) {
					this.ealSeqEx = this.ealSeq;
					this.rmsSeqEx = this.rmsSeq;
					Integer oldEalSeq = Integer.valueOf(this.estoqueAlmo.getSeq());
					Integer seqRm = Integer.valueOf(this.reqMaterial.getSeq());
					Integer oldQtd = Integer.valueOf(this.quantidade);
					DominioSituacaoRequisicaoMaterial situacaoAntiga = reqMaterial.getIndSituacao();
					this.excluir();
					//this.estoqueFacade.flush();
					this.estoqueAlmo = this.estoqueFacade.buscaSceEstoqueAlmoxarifadoPorId(oldEalSeq);
					reqMaterial = this.estoqueFacade.obterRequisicaoMaterial(seqRm);
					this.quantidade = oldQtd;
					reqMaterial.setIndSituacao(situacaoAntiga);
					reqMaterial.setDtCancelamento(null);
					reqMaterial.setServidorCancelado(null);
					this.ealSeq = null;
					this.rmsSeq = null;					
					this.adicionar(false);
				} else {				
					for (SceItemRms sceItemRms : sceItemRmses) {
	
						if (sceItemRms.getId().getEalSeq() != null && sceItemRms.getId().getEalSeq().equals(ealSeq)
								&& sceItemRms.getId().getRmsSeq() != null && sceItemRms.getId().getRmsSeq().equals(rmsSeq)) {
	
							sceItemRms.setQtdeRequisitada(this.quantidade);
	
							itemRmsOriginal = estoqueFacade.obterItemRmsOriginal(sceItemRms);
	
							/* grava/atualiza o item */
							this.estoqueBeanFacade.gravarItensRequisicaoMaterial(sceItemRms, nomeMicrocomputador);
							apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ITEM_REQUISICAO");
							cancelarEdicao();
							atualizaListaItens();
	
						}
	
					}
	
				}
				quantidade = null;
				this.material = null;
			}
		} catch (BaseException e) {
			// Neste trecho está sendo feito o refresh manual do objeto alterado
			// devido ao uso de flush na estoria #12310
			for (int i = 0; i < sceItemRmses.size(); i++) {
				if (sceItemRmses.get(i).equals(itemRmsOriginal)) {
					sceItemRmses.set(i, itemRmsOriginal);
					break;
				}
			}
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		this.mostrarPanelAvisoAlmox = false;
	}

	/**
	 * Remove o item da RM
	 */
	public String excluir() {

		try {
			if (sceItemRmses != null && sceItemRmses.size() == 1 && !this.trocouMaterial) {
				return this.cancelarRm();
			} else {
				SceItemRms sceItemRmsToRemove = null;
				for (SceItemRms sceItemRms : sceItemRmses) {
					if (sceItemRms.getId().getEalSeq().equals(ealSeqEx) && sceItemRms.getId().getRmsSeq().equals(rmsSeqEx)) {
						sceItemRmsToRemove = sceItemRms;
					}
				}
	
				if (sceItemRmsToRemove != null && sceItemRmsToRemove.getId() != null) {
					this.estoqueBeanFacade.excluirItemRequisicaoMaterial(sceItemRmsToRemove, sceItemRmses.size(), Boolean.FALSE);
				}
	
				if ((rmsSeq != null && ealSeq != null) && (rmsSeqEx != null && ealSeqEx != null)
						&& (rmsSeqEx.equals(rmsSeq) && ealSeqEx.equals(ealSeqEx))) {
					cancelarEdicao();
				}
	
				this.rmsSeqEx = null;
				this.ealSeqEx = null;
	
				atualizaListaItens();
	
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_REQUISICAO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
		return null;
	}

	/**
	 * Atualiza a lista de itens da requisição de material
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void atualizaListaItens() throws ApplicationBusinessException {

		setSceItemRmses(this.estoqueFacade.pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(reqMaterial.getSeq(), DominioOrderBy.E, null));
		if (getSceItemRmses() != null && getSceItemRmses().size() > 0) {
			for (SceItemRms itemRms : getSceItemRmses()) {
				this.estoqueFacade.preencheConsumoMedioItemRequisicao(itemRms, this.reqMaterial.getCentroCustoAplica().getCodigo());
			}
		}
	}

	/**
	 * Grava a requisição de material
	 */
	public void gravarAtualizarRequisicaoMaterial() {

		if (this.reqMaterial.getAlmoxarifado() == null || this.reqMaterial.getCentroCusto() == null
				|| this.reqMaterial.getCentroCustoAplica() == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_REQUISICAO");
		} else {
			Boolean novoRegistro = false;
			reqMaterial.setAtendimento(this.atendimento);
			try {
				if (this.reqMaterial.getSeq() == null) {
					novoRegistro = true;
					this.reqMaterial.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
				} else {
					novoRegistro = false;
				}

				// Resgata e força Centro de Custo Requisitante
				final FccCentroCustos centroCustoAtachadoAplica = this.estoqueFacade.obterFccCentroCustos(this.reqMaterial
						.getCentroCustoAplica().getCodigo());
				this.reqMaterial.setCentroCustoAplica(centroCustoAtachadoAplica);

				// Resgata e força Centro de Custo Requisitante
				final FccCentroCustos centroCustoAtachado = this.estoqueFacade.obterFccCentroCustos(this.reqMaterial.getCentroCusto()
						.getCodigo());
				this.reqMaterial.setCentroCusto(centroCustoAtachado);

				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error(EXCECAO_CAPTURADA, e);
				}
				this.estoqueBeanFacade.gravarRequisicaoMaterial(this.reqMaterial, nomeMicrocomputador);

				if (this.reqMaterial.getPacoteMaterial() != null) {
					this.adicionarItensPacote();
					this.atualizaListaItens();
				}

				if (novoRegistro) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_REQUISICAO");
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_REQUISICAO");
				}

			} catch (BaseException e) {
				if (novoRegistro) {
					this.reqMaterial.setSeq(null);
					this.reqMaterial.setIndSituacao(null);
					this.reqMaterial.setDtGeracao(null);
					this.reqMaterial.setIndImpresso(null);
					this.reqMaterial.setAtendimento(null);
				}
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Grava a requisição de material no gravar itens
	 */
	public void gravarAtualizarRequisicaoMaterialEmGravarItens() {

		if (this.reqMaterial.getAlmoxarifado() == null
				|| this.reqMaterial.getCentroCusto() == null
				|| this.reqMaterial.getCentroCustoAplica() == null) {
			apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_ERRO_CRIACAO_REQUISICAO");
		} else {
			reqMaterial.setAtendimento(this.atendimento);

			// Resgata e força Centro de Custo Requisitante
			final FccCentroCustos centroCustoAtachadoAplica = this.estoqueFacade
					.obterFccCentroCustos(this.reqMaterial
							.getCentroCustoAplica().getCodigo());
			this.reqMaterial.setCentroCustoAplica(centroCustoAtachadoAplica);

			// Resgata e força Centro de Custo Requisitante
			final FccCentroCustos centroCustoAtachado = this.estoqueFacade
					.obterFccCentroCustos(this.reqMaterial.getCentroCusto()
							.getCodigo());
			this.reqMaterial.setCentroCusto(centroCustoAtachado);

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}
			try {
				this.estoqueBeanFacade.gravarRequisicaoMaterial(
						this.reqMaterial, nomeMicrocomputador);

				if (this.reqMaterial.getPacoteMaterial() != null) {
					this.adicionarItensPacote();
					this.atualizaListaItens();
				}
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}

		}
	}
	
	/**
	 * Grava o item da RM
	 * 
	 * @throws BaseException
	 */
	public void gravarAtualizarItemRequisicaoMaterial(boolean isCodigoBarras) throws BaseException {

		if (isCodigoBarras) {
			final SceItemRms itemRms = this.getItemRequisicaoMaterialAtual();
			if (itemRms == null) {
				this.quantidade = 1;
				this.gravarItemRequisicaoMaterial();
			} else {

				// Resgata identificadores do Item de RM
				this.rmsSeq = itemRms.getId().getRmsSeq();
				this.ealSeq = itemRms.getId().getEalSeq();

				// Incrementa a Quantidade Requisitada em uma unidade no caso de
				// alteração
				this.quantidade = itemRms.getQtdeRequisitada() + 1;

				this.alterarItemRequisicaoMaterial();
			}
		} else {
			final boolean temRegistro = this.getItemRequisicaoMaterialAtual() != null;
			if (!temRegistro) {
				this.gravarItemRequisicaoMaterial();
			} else {
				throw new BaseException(GeracaoRequisicaoMaterialControllerExceptionCode.MENSAGEM_ITEM_RM_JA_CADASTRADO);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private SceItemRms getItemRequisicaoMaterialAtual() {

		SceItemRms retorno = null;
		for (SceItemRms sceItemRms : this.sceItemRmses) {
			if (sceItemRms.getId() != null && sceItemRms.getId().getEalSeq() != null 
					&& sceItemRms.getId().getEalSeq().equals(this.getEstoqueAlmo().getSeq())
					&& sceItemRms.getId().getRmsSeq() != null 
					&& sceItemRms.getId().getRmsSeq().equals(reqMaterial.getSeq())) {

				retorno = sceItemRms;
				break;
			}
		}
		return retorno;
	}

	private Boolean verificarMaterialExisteLista() {
		Boolean existe = false;
		if (this.sceItemRmses != null) {
			for (SceItemRms item : this.sceItemRmses) {
				if (item.getId().getEalSeq().equals(this.getEstoqueAlmo().getSeq())) {
					existe = true;
					break;
				}
			}
		}

		return existe;
	}

	/**
	 * @throws BaseException
	 * 
	 */
	private void gravarItemRequisicaoMaterial() throws BaseException {

		this.estoqueFacade.validaCamposObrigatorios(reqMaterial);
		if (verificarMaterialExisteLista()) {
			throw new BaseException(GeracaoRequisicaoMaterialControllerExceptionCode.MENSAGEM_ITEM_RM_JA_CADASTRADO);
		} else {
			// ID do item da RM
			SceItemRmsId idItem = new SceItemRmsId();
			idItem.setRmsSeq(reqMaterial.getSeq());
			idItem.setEalSeq(this.getEstoqueAlmo().getSeq());

			// Item da RM
			SceItemRms item = new SceItemRms();
			item.setId(idItem);
			item.setQtdeRequisitada(this.quantidade);
			item.setEstoqueAlmoxarifado(this.getEstoqueAlmo());
			item.setScoUnidadeMedida(this.getEstoqueAlmo().getUnidadeMedida());
			item.setIndTemEstoque(true);
			item.setSceReqMateriais(reqMaterial);

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}

			// Grava/Atualiza Item RM
			this.estoqueBeanFacade.gravarItensRequisicaoMaterial(item, nomeMicrocomputador);

			this.cancelarEdicao();
			this.atualizaListaItens();
		}

	}

	public String confirmar() throws JRException, SystemException, IOException {

		try {
			if (this.pacCodigo != null && this.atdSeq == null) {
				apresentarMsgNegocio(Severity.ERROR, "MSG_ATENDIMENTO_PACIENTE_NAO_INFORMADO");
				return null;
			}

			if (this.sceItemRmses == null || this.sceItemRmses.size() == 0) {
				apresentarMsgNegocio(Severity.ERROR, "MSG_NUMERO_ITENS_NAO_PERMITIDO");
				return null;
			}

			for (SceItemRms item : this.sceItemRmses) {
				if (item.getQtdeRequisitada() == 0) {
					this.estoqueBeanFacade.excluirItemRequisicaoMaterial(item, sceItemRmses.size(), Boolean.FALSE);
				}
			}

			this.reqMaterial.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}

			this.estoqueBeanFacade.gravarRequisicaoMaterial(this.reqMaterial, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CONFIRMACAO_REQUISICAO");
			
			atualizaListaItens();
			imprimir = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	
	public void imprimirNoAlmoxarifadoDaRequisicao() throws BaseException, JRException, SystemException, IOException{
		ImpImpressora impressora = null;
		
		try {
			impressora = this.estoqueFacade.defineImpressoraImpressao(reqMaterial);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (impressora != null) {
			imprimirRequisicaoMaterialRemotaController.setNumeroRM(reqMaterial.getSeq());
			imprimirRequisicaoMaterialRemotaController.setDuasVias(DominioSimNao.N);
			imprimirRequisicaoMaterialRemotaController.directPrint(impressora);
		}
	}
	
	public void confirmarEImprimir() throws JRException, SystemException, IOException, BaseException{
		confirmar();
		if(imprimir) {
			imprimirNoAlmoxarifadoDaRequisicao();
		}
	}

	public void cancelarEdicao() {
		this.mostrarPanelAvisoAlmox = false;
		this.rmsSeq = null;
		this.ealSeq = null;
		this.material = null;
		this.setEstoqueAlmo(null);
		this.setQuantidade(null);
		verificaDisableSB();
		this.trocouMaterial = false;
		this.rmsSeqEx = null;
		this.ealSeqEx = null;
		this.materialCarregou = false;
	}

	public Boolean verificaDisableSB() {
		verificaDisableSB = (rmsSeq != null && ealSeq != null);
		return verificaDisableSB;
	}

	public boolean isItemSelecionado(SceItemRms sceItemRmsParam) {
		if (sceItemRmsParam != null && (sceItemRmsParam.getId().getEalSeq() != null && sceItemRmsParam.getId().getEalSeq().equals(ealSeq)
				&& sceItemRmsParam.getId().getRmsSeq() != null && sceItemRmsParam.getId().getRmsSeq().equals(rmsSeq))) {
			return true;
		}
		return false;
	}

	public void editar() {
		if ((rmsSeq != null && ealSeq != null)) {
			for (SceItemRms sceItemRms : sceItemRmses) {
				if ((sceItemRms.getId().getEalSeq() != null && sceItemRms.getId().getEalSeq().equals(ealSeq)
						&& sceItemRms.getId().getRmsSeq() != null && sceItemRms.getId().getRmsSeq().equals(rmsSeq))) {
					estoqueAlmo = sceItemRms.getEstoqueAlmoxarifado();
					quantidade = sceItemRms.getQtdeRequisitada();
					this.material = estoqueAlmo.getMaterial();
				}
			}
			this.trocouMaterial = false;
		}
		verificaDisableSB();
		this.materialCarregou = true;
	}

	/**
	 * Insert dos itens do Pacote
	 * 
	 * @throws BaseException
	 */
	private void adicionarItensPacote() throws BaseException {

		if (reqMaterial.getSeq() != null) {

			Integer codigoGrupoMaterial = null;

			if (this.reqMaterial.getGrupoMaterial() != null) {
				codigoGrupoMaterial = this.reqMaterial.getGrupoMaterial().getCodigo();
			}

			List<SceEstoqueAlmoxarifado> lstItemToAdd = this.estoqueFacade.pesquisarEstoqueAlmoxarifadoPorPacote(codigoGrupoMaterial,
					this.reqMaterial.getPacoteMaterial().getId());

			this.estoqueFacade.validarInsercaoPacoteRequisicaoMaterial(this.reqMaterial, lstItemToAdd, this.isAlmoxarife);

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}

			for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : lstItemToAdd) {
				/* ID do item de requisicao */
				SceItemRmsId idItem = new SceItemRmsId();
				// idItem.setRmsSeq(reqMaterial.getSeq());
				idItem.setEalSeq(estoqueAlmoxarifado.getSeq());
				idItem.setRmsSeq(reqMaterial.getSeq());

				SceItemRms item = new SceItemRms();
				item.setId(idItem);
				item.setQtdeRequisitada(estoqueAlmoxarifado.getQuantidade());
				item.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
				item.setScoUnidadeMedida(estoqueAlmoxarifado.getUnidadeMedida());
				item.setIndTemEstoque(true);
				item.setSceReqMateriais(reqMaterial);

				this.estoqueFacade.preencheConsumoMedioItemRequisicao(item, reqMaterial.getCentroCustoAplica().getCodigo());
				this.estoqueBeanFacade.gravarItensRequisicaoMaterial(item, nomeMicrocomputador);
			}
		}
	}

	public void selecionarPacienteConsultaManual() {
		this.buscaManual = true;
		this.selecionarPacienteConsulta();
	}

	public void selecionarPacienteConsulta() {
		if (this.pacCodigo != null || this.prontuario != null) {
			if (this.pacCodigo != null) {
				this.paciente = pacienteFacade.obterPacientesComUnidadeFuncional(pacCodigo);
			} else if (!StringUtils.isEmpty(this.prontuario)) {
				this.paciente = pacienteFacade.obterPacientePorProntuario(Integer.valueOf(prontuario));
			}
		}

		if (this.paciente == null) {
			apresentarMsgNegocio(Severity.ERROR, GeracaoRequisicaoMaterialControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());
		} else {
			this.pacCodigo = this.paciente.getCodigo();
			if (this.paciente.getProntuario() != null) {
				this.prontuario = this.paciente.getProntuario().toString();
			}
			this.pacNome = this.paciente.getNome();
		}

		if (this.pacCodigoFonetica == null && this.paciente != null && !this.buscaManual) {
			Set<AghAtendimentos> atendimentosSet = paciente.getAghAtendimentos();
			Iterator<AghAtendimentos> it = atendimentosSet.iterator();
			while (it.hasNext()) {
				this.atendimento = it.next();
			}
			if (atendimento != null) {
				this.atdSeq = atendimento.getSeq();
				this.reqMaterial.setCentroCustoAplica(this.atendimento.getUnidadeFuncional().getCentroCusto());
			}
		}
	}

	public void limparPaciente() {
		this.paciente = null;
		this.pacCodigo = null;
		this.pacNome = null;
		this.prontuario = null;
		this.atendimento = null;
		this.atdSeq = null;
		this.buscaManual = false;
	}

	public void limparAtendimento() {
		this.atdSeqSelecionado = null;
	}

	public void selecionaAtendimento() {
		this.atdSeq = this.atdSeqSelecionado;
		this.atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.atdSeq);
		if (this.atendimento != null) {
			this.reqMaterial.setCentroCustoAplica(this.atendimento.getUnidadeFuncional().getCentroCusto());
		}
	}

	public void listarAtendimentosPaciente() throws ApplicationBusinessException {
		atendimentosPaciente = this.estoqueFacade.listarAtendimentosPaciente(pacCodigo);
		if (!atendimentosPaciente.isEmpty()) {
			openDialog("modalVincularAtendimentoRMWG");
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NAO_EXISTEM_ATENDIMENTOS_PACIENTE");
		}
	}

	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de CC Requisicao
	public List<FccCentroCustos> obterFccCentroCustos(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosServidorOrdemDescricao(objPesquisa);
	}

	public List<FccCentroCustos> obterFccCentroCustosReq(String parametro) throws ApplicationBusinessException,
			ApplicationBusinessException {
		return this.centroCustoFacade.pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(parametro,
				DominioCaracteristicaCentroCusto.GERAR_RM);
	}

	// Metodo para pesquisa na suggestion box de CC Aplicacao
	public List<FccCentroCustos> obterFccCentroCustosAplicacao(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de Grupo de Materiais
	public List<ScoGrupoMaterial> obterScoGrupoMaterial(String objPesquisa) {
		Short almoSeq = (reqMaterial.getAlmoxarifado() != null) ? reqMaterial.getAlmoxarifado().getSeq() : null;
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroAlmoxarifado(almoSeq, objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de Pacote
	@SuppressWarnings("PMD.NPathComplexity")
	public List<ScePacoteMateriais> obterScePacoteMaterial(String objPesquisa) {

		final Short seqAlmoxarifado = reqMaterial.getAlmoxarifado() != null ? reqMaterial.getAlmoxarifado().getSeq() : null;
		final Integer codigoCentroCustoProprietario = reqMaterial.getCentroCusto() != null ? reqMaterial.getCentroCusto().getCodigo()
				: null;
		final Integer codigoCentroCustoAplicacao = reqMaterial.getCentroCustoAplica() != null ? reqMaterial.getCentroCustoAplica()
				.getCodigo() : null;
		final Integer codigoGrupoMaterial = reqMaterial.getGrupoMaterial() != null ? reqMaterial.getGrupoMaterial().getCodigo() : null;

		if (seqAlmoxarifado == null || codigoCentroCustoProprietario == null || codigoCentroCustoAplicacao == null) {
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CAMPOS_OBRIGATORIOS_PACOTE_RM");
			return new ArrayList<ScePacoteMateriais>();
		}

		return this.estoqueFacade.pesquisarPacoteMateriaisGerarRequisicaoMaterial(seqAlmoxarifado, codigoCentroCustoProprietario,
				codigoCentroCustoAplicacao, codigoGrupoMaterial, objPesquisa);

	}

	/**
	 * Limpa centro dados do Grupo Material e Pacote
	 */
	public void limparPacote() {
		this.reqMaterial.setGrupoMaterial(null);
		this.reqMaterial.setPacoteMaterial(null);
	}

	/**
	 * Limpa Item de Estoque Almoxarifado utilizado que sera acrescentado na
	 * lista de itens
	 */
	public void limparItemEstoqueAlmoxarifado() {
		this.estoqueAlmo = null;
		this.quantidade = 0;
	}

	public List<ScoMaterial> listarMateriais(String param) {
		return this.solicitacaoComprasFacade.listarMateriaisSC(param, this.reqMaterial.getGrupoMaterial() != null ? this.reqMaterial.getGrupoMaterial().getCodigo() : null,
				this.reqMaterial.getAlmoxarifado() != null ? this.reqMaterial.getAlmoxarifado().getSeq() : null);
	}
	
   private void validaMaterialInformado() throws ApplicationBusinessException{
		
		AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRUPO_CLASSIF_COMPRAS_WEB); 
		this.setComprarItemMaterialWeb(this.comprasFacade.verificarComprasWeb(param, this.material != null ? this.material : null));
		
		if(this.isComprarItemMaterialWeb()){
			throw new ApplicationBusinessException(GeracaoRequisicaoMaterialControllerExceptionCode.MENSAGEM_COMPRAS_MATERIAL_ITEM_WEB,this.material.getCodigo().toString()+" - "+this.material.getNome());
		}		
	}  
	
   public Integer listarMateriaisCount(String param)	{
		return this.solicitacaoComprasFacade.listarMateriaisSCCount(param, this.reqMaterial.getGrupoMaterial() != null ? this.reqMaterial.getGrupoMaterial().getCodigo() : null);
	}
	
	// Redireciona para a Pesquisa Fonética
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}

	public SceReqMaterial getReqMaterial() {
		return reqMaterial;
	}

	public void setReqMaterial(SceReqMaterial reqMaterial) {
		this.reqMaterial = reqMaterial;
	}

	public Integer getSeqReq() {
		return seqReq;
	}

	public void setSeqReq(Integer seqReq) {
		this.seqReq = seqReq;
	}

	public Integer getRmsSeq() {
		return rmsSeq;
	}

	public void setRmsSeq(Integer rmsSeq) {
		this.rmsSeq = rmsSeq;
	}

	public Integer getEalSeq() {
		return ealSeq;
	}

	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}

	public SceEstoqueAlmoxarifado getEstoqueAlmo() {
		return estoqueAlmo;
	}

	public void setEstoqueAlmo(SceEstoqueAlmoxarifado estoqueAlmo) {
		this.estoqueAlmo = estoqueAlmo;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public List<SceItemRms> getSceItemRmses() {
		return sceItemRmses;
	}

	public void setSceItemRmses(List<SceItemRms> sceItemRmses) {
		this.sceItemRmses = sceItemRmses;
	}

	public Boolean getVerificaDisableSB() {
		return verificaDisableSB;
	}

	public void setVerificaDisableSB(Boolean verificaDisableSB) {
		this.verificaDisableSB = verificaDisableSB;
	}

	public Integer getRmsSeqEx() {
		return rmsSeqEx;
	}

	public void setRmsSeqEx(Integer rmsSeqEx) {
		this.rmsSeqEx = rmsSeqEx;
	}

	public Integer getEalSeqEx() {
		return ealSeqEx;
	}

	public void setEalSeqEx(Integer ealSeqEx) {
		this.ealSeqEx = ealSeqEx;
	}

	public String getOrigemPesquisa() {
		return origemPesquisa;
	}

	public void setOrigemPesquisa(String origemPesquisa) {
		this.origemPesquisa = origemPesquisa;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setAtendimentosPaciente(List<AghAtendimentosVO> atendimentosPaciente) {
		this.atendimentosPaciente = atendimentosPaciente;
	}

	public List<AghAtendimentosVO> getAtendimentosPaciente() {
		return atendimentosPaciente;
	}

	public void setAtdSeqSelecionado(Integer atdSeqSelecionado) {
		this.atdSeqSelecionado = atdSeqSelecionado;
	}

	public Integer getAtdSeqSelecionado() {
		return atdSeqSelecionado;
	}

	public String getNumeroEtiqueta() {
		return numeroEtiqueta;
	}

	public void setNumeroEtiqueta(String numeroEtiqueta) {
		this.numeroEtiqueta = numeroEtiqueta;
	}

	public void setBuscaManual(Boolean buscaManual) {
		this.buscaManual = buscaManual;
	}

	public Boolean getBuscaManual() {
		return buscaManual;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected void setCascaFacade(ICascaFacade cascaFacade) {
		this.cascaFacade = cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	protected void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	protected FccCentroCustos getCcAtuacao() {
		return ccAtuacao;
	}

	protected void setCcAtuacao(FccCentroCustos ccAtuacao) {
		this.ccAtuacao = ccAtuacao;
	}

	protected FccCentroCustos getCcFipe() {
		return ccFipe;
	}

	protected void setCcFipe(FccCentroCustos ccFipe) {
		this.ccFipe = ccFipe;
	}

	protected Set<Integer> getListaHierarquica() {
		return listaHierarquica;
	}

	protected void setListaHierarquica(Set<Integer> listaHierarquica) {
		this.listaHierarquica = listaHierarquica;
	}

	protected boolean isPossuiCaractGppg() {
		return possuiCaractGppg;
	}

	protected void setPossuiCaractGppg(boolean possuiCaractGppg) {
		this.possuiCaractGppg = possuiCaractGppg;
	}
	

	public Boolean getCameFromCatalogo() {
		return cameFromCatalogo;
	}

	public void setCameFromCatalogo(Boolean cameFromCatalogo) {
		this.cameFromCatalogo = cameFromCatalogo;
	}

	public boolean isAlmoxarife() {
		return isAlmoxarife;
	}

	public void setAlmoxarife(boolean isAlmoxarife) {
		this.isAlmoxarife = isAlmoxarife;
	}

	public boolean isChkCcSolic() {
		return chkCcSolic;
	}

	public void setChkCcSolic(boolean chkCcSolic) {
		this.chkCcSolic = chkCcSolic;
	}

	public boolean isChkCcAplic() {
		return chkCcAplic;
	}

	public void setChkCcAplic(boolean chkCcAplic) {
		this.chkCcAplic = chkCcAplic;
	}

	public ScoCaracteristicaUsuarioCentroCusto getSugereCcAplic() {
		return sugereCcAplic;
	}

	public void setSugereCcAplic(ScoCaracteristicaUsuarioCentroCusto sugereCcAplic) {
		this.sugereCcAplic = sugereCcAplic;
	}

	public ScoCaracteristicaUsuarioCentroCusto getSugereCcSolic() {
		return sugereCcSolic;
	}

	public void setSugereCcSolic(ScoCaracteristicaUsuarioCentroCusto sugereCcSolic) {
		this.sugereCcSolic = sugereCcSolic;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public Boolean getMostrarPanelAvisoAlmox() {
		return mostrarPanelAvisoAlmox;
	}

	public void setMostrarPanelAvisoAlmox(Boolean mostrarPanelAvisoAlmox) {
		this.mostrarPanelAvisoAlmox = mostrarPanelAvisoAlmox;
	}

	public Boolean getTrocouMaterial() {
		return trocouMaterial;
	}

	public void setTrocouMaterial(Boolean trocouMaterial) {
		this.trocouMaterial = trocouMaterial;
	}	
	
	public SceItemRms getSceItemRmseSelecionado() {
		return sceItemRmseSelecionado;
	}

	public void setSceItemRmseSelecionado(SceItemRms sceItemRmseSelecionado) {
		this.sceItemRmseSelecionado = sceItemRmseSelecionado;
	}
	public boolean isComprarItemMaterialWeb() {
		return comprarItemMaterialWeb;
	}

	public void setComprarItemMaterialWeb(boolean comprarItemMaterialWeb) {
		this.comprarItemMaterialWeb = comprarItemMaterialWeb;
	}

	public boolean isMaterialCarregou() {
		return materialCarregou;
	}

	public void setMaterialCarregou(boolean materialCarregou) {
		this.materialCarregou = materialCarregou;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	public boolean isRenderizaModalImpressao() {
		return imprimir;
	}

	public void setRenderizaModalImpressao(boolean renderizaModalImpressao) {
		this.imprimir = renderizaModalImpressao;
	}
	
}
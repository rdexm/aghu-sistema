package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroPesquisaAssinarAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioTipoCadastroItemContrato;
import br.gov.mec.aghu.dominio.DominioTipoConsultaAssinarAF;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AssinaturaAutorizacaoFornecimentoController extends ActionController implements ActionPaginator {

	
	@Inject @Paginator
	private DynamicDataModel<PesquisaAutorizacaoFornecimentoVO> dataModel;

	private static final String ITENS_PENDENTES_PAC = "itensPendentesPac";

	private static final String ITENS_AUTORIZACAO_FORNECIMENTO = "itensAutorizacaoFornecimento";

	private static final String SICON_MANTER_CONTRATO_MANUAL = "sicon-manterContratoManual";

	private static final String SICON_MANTER_CONTRATO_AUTOMATICO = "sicon-manterContratoAutomatico";

	private static final String ITENS_AUTORIZACAO_FORNECIMENTO_PEDIDO = "itensAutorizacaoFornecimentoPedido";

	private static final Log LOG = LogFactory.getLog(AssinaturaAutorizacaoFornecimentoController.class);
	
	private static final long serialVersionUID = -6005769024181822851L;

	@Inject
	private SecurityController securityController;
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;
	
	@Inject
	protected EnvioEmailAssinaturaAFController envioEmailAssinaturaAFController;
	
	@Inject
	protected RelatorioPacientesCUMController relatorioPacientesCUMController;
	
	@Inject
	protected RelatorioAutorizacaoFornecimentoController relatorioAutorizacaoFornecimentoController;
	
	@EJB
	private ISiconFacade siconFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;

	private Integer numeroAf;
	private Short numeroComplemento;
	private Integer numeroContrato;
	private VScoFornecedor fornecedor;
	private RapServidores servidorGestor;
	private ScoModalidadeLicitacao modalidadeCompra;
	private ScoGrupoMaterial grupoMaterial;
	private ScoGrupoServico grupoServico;
	private FsoVerbaGestao verbaGestao;
	private FsoNaturezaDespesa naturezaDespesa;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private List<PesquisaAutorizacaoFornecimentoVO> resultado;
	private Integer contSelecionadoNro;
		
	private List<PesquisaAutorizacaoFornecimentoVO> listaChecked;
	private List<PesquisaAutorizacaoFornecimentoVO> allChecked;
	private List<PesquisaAutorizacaoFornecimentoVO> listaAtual;

	private List<PesquisaAutorizacaoFornecimentoVO> listaAfsAssinadas = new ArrayList<PesquisaAutorizacaoFornecimentoVO>();
	
	private DominioTipoCadastroItemContrato tipoCompra;
	private DominioTipoConsultaAssinarAF tipoConsulta;
	private Boolean emContrato;
	
	
	// utilizados na tela de visualização dos itens da AF
	private ScoAutorizacaoForn autFornecimento;
		
	private final String CONSTANTE_ASSINAR_AF_MATERIAL="ASSINAR AF MATERIAL";
	private final String CONSTANTE_ASSINAR_AF_SERVICO="ASSINAR AF SERVICO";
	private Boolean exibeTodas;
	private Boolean exibeMaterial;
	private Boolean exibeServico;	
	private Boolean pesqItens = Boolean.FALSE;
		
	// controle voltar da tela de itens da AF
	private String voltarPara;
	
	private PesquisaAutorizacaoFornecimentoVO itemSelecionado;
	
	private Boolean exibeModal = Boolean.FALSE;
	
	private Boolean possuiPermissaoAssinar;
	private Boolean possuiPermissaoConsultarAssinar;
	private Boolean possuiPermissaoGerenciarContratos;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		if (this.pesqItens == null || !this.pesqItens) {
				pesqItens = true;
				tipoConsulta = DominioTipoConsultaAssinarAF.T;
				String caracteristica = this.autFornecimentoFacade.verificarCaracteristicaAssinarAf();
				if (caracteristica == null) {
					tipoCompra = null;
					exibeMaterial = false;
					exibeServico = false;
					exibeTodas = true;
				} else if (caracteristica.equals(CONSTANTE_ASSINAR_AF_MATERIAL)) {
					tipoCompra = DominioTipoCadastroItemContrato.M;
					exibeMaterial = true;
					exibeServico = false;
					exibeTodas = false;
				} else if (caracteristica.equals(CONSTANTE_ASSINAR_AF_SERVICO)) {
					tipoCompra = DominioTipoCadastroItemContrato.S;
					exibeMaterial = false;
					exibeServico = true;
					exibeTodas = false;
				}
				
				this.possuiPermissaoAssinar = securityController.usuarioTemPermissao("assinarAF", "assinar");
				this.possuiPermissaoConsultarAssinar = securityController.usuarioTemPermissao("consultarAFaAssinar", "visualizar");
				this.possuiPermissaoGerenciarContratos = securityController.usuarioTemPermissao("gerenciarContratos", "habilitar");
		}
	}
	
	public void atualizarCampos() {
		if (this.getTipoCompra() == DominioTipoCadastroItemContrato.M) {
			exibeMaterial = true;
			exibeServico = false;
			exibeTodas = false;
			this.grupoServico = null;
		} else if(this.getTipoCompra() == DominioTipoCadastroItemContrato.S){
			exibeMaterial = false;
			exibeServico = true;
			exibeTodas = false;
			this.grupoMaterial = null;
		} else {
			exibeMaterial = false;
			exibeServico = false;
			exibeTodas = true;
			this.grupoMaterial = null;
			this.grupoServico = null;
			this.verbaGestao = null;
			this.grupoNaturezaDespesa = null;
			this.naturezaDespesa = null;
		}
	}
	
	public FiltroPesquisaAssinarAFVO montarFiltro() {
		final FiltroPesquisaAssinarAFVO filtro = new FiltroPesquisaAssinarAFVO();
		filtro.setNumeroAf(this.numeroAf);
		filtro.setNumeroComplemento(this.numeroComplemento);
		filtro.setIndContrato(this.emContrato);
		filtro.setNumeroContrato(this.numeroContrato);
		filtro.setTipoCompra(this.tipoCompra);
		filtro.setTipoConsulta(this.tipoConsulta);
		filtro.setIndContrato(this.getEmContrato());
		if (this.fornecedor == null) {
			filtro.setNumeroFornecedor(null);
		} else {
			filtro.setNumeroFornecedor(this.fornecedor.getNumeroFornecedor());
		}
		if (this.servidorGestor == null) {
			filtro.setMatriculaGestor(null);
			filtro.setVinCodigoGestor(null);
		} else {
			filtro.setMatriculaGestor(this.servidorGestor.getId().getMatricula());
			filtro.setVinCodigoGestor(this.servidorGestor.getId().getVinCodigo());
		}
		if (this.modalidadeCompra == null) {
			filtro.setCodigoModalidadeCompra(null);
		} else {
			filtro.setCodigoModalidadeCompra(this.modalidadeCompra.getCodigo());
		}
		if(this.tipoCompra==null){
			filtro.setTipoCompra(null);
		} else {
			filtro.setTipoCompra(this.tipoCompra);
		}
		if (this.grupoMaterial == null) {
			filtro.setCodigoGrupoMaterial(null);
		} else {
			filtro.setCodigoGrupoMaterial(this.grupoMaterial.getCodigo());
		}
		if (this.grupoServico == null) {
			filtro.setCodigoGrupoServico(null);
		} else {
			filtro.setCodigoGrupoServico(this.grupoServico.getCodigo());
		}
		if (this.verbaGestao == null) {
			filtro.setSeqVerbaGestao(null);
		} else {
			filtro.setSeqVerbaGestao(this.verbaGestao.getSeq());
		}
		if (this.naturezaDespesa == null) {
			filtro.setCodigoNaturezaDespesa(null);
		} else {
			filtro.setCodigoNaturezaDespesa(this.naturezaDespesa.getId().getCodigo());
		}
		if (this.grupoNaturezaDespesa == null) {
			filtro.setCodigoGrupoNaturezaDespesa(null);
		} else {
			filtro.setCodigoGrupoNaturezaDespesa(this.grupoNaturezaDespesa.getCodigo());
		}
		return filtro;
	}
	
	public void pesquisar() {
		try{
			this.limparControleGrid();
			this.autFornecimentoFacade.validarComplementoAssinarAf(montarFiltro());
			this.dataModel.reiniciarPaginator();
			
		} catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	}

	@Override
	public Long recuperarCount() {
		return autFornecimentoFacade.pesquisarListaAfsAssinarCount(montarFiltro());
	}

	@Override
	public List<PesquisaAutorizacaoFornecimentoVO> recuperarListaPaginada(Integer first, Integer max,
			String order, boolean asc) {	
		
		setResultado(autFornecimentoFacade.pesquisarListaAfsAssinar(first, max, order, asc, montarFiltro()));

		listaAtual.clear();
		listaAtual.addAll(resultado);
		
		return resultado;
	}

	public void atualizarAllChecked(PageEvent event) {
		this.dataModel.onPageChange(event);
		this.listaChecked.clear();
		for (PesquisaAutorizacaoFornecimentoVO item : this.allChecked) {
			this.listaChecked.add(item);
		}
	}

	public void marcarTodos() {
		Integer paginaAtual = this.dataModel.getDataTableComponent().getPage() + 1;
		Integer paginaFinal = this.dataModel.getDataTableComponent().getPageCount();
		Integer totalRegistros = this.dataModel.getDataTableComponent().getRowCount();
		Integer registroInicial = this.dataModel.getDataTableComponent().getFirst();
		
		if ((paginaAtual < paginaFinal  &&  this.listaChecked.size() == this.dataModel.getPageSize()) ||
				paginaAtual == paginaFinal && this.listaChecked.size() == (totalRegistros - registroInicial)) {
			for(PesquisaAutorizacaoFornecimentoVO item : listaAtual) {
				if (!this.allChecked.contains(item)) {
					this.allChecked.add(item);
				}
				}
		} else if (this.listaChecked.size() == 0) {
			for(PesquisaAutorizacaoFornecimentoVO item : listaAtual) {
				if (this.allChecked.contains(item)) {
					this.allChecked.remove(item);
				}
			}
		}
	}

	public void selecionarLinha(SelectEvent event) {
		PesquisaAutorizacaoFornecimentoVO item = (PesquisaAutorizacaoFornecimentoVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
	}

	public void desmarcarLinha(UnselectEvent event) {
		PesquisaAutorizacaoFornecimentoVO item = (PesquisaAutorizacaoFornecimentoVO)event.getObject();
		if (this.allChecked.contains(item)) {
			this.allChecked.remove(item);
		} else {
			this.allChecked.add(item);
		}
	}

	private void limparControleGrid() {
		if (this.listaChecked == null) {
			this.setListaChecked(new ArrayList<PesquisaAutorizacaoFornecimentoVO>());
		}
		this.listaChecked.clear();
		if (this.allChecked == null) {
			this.allChecked = new ArrayList<PesquisaAutorizacaoFornecimentoVO>();
		}
		if (this.listaAtual == null) {
			this.listaAtual = new ArrayList<PesquisaAutorizacaoFornecimentoVO>();
		}
		this.allChecked.clear();
	}
		
	
	public void confirmarAssinatura(){
		try {
			
			listaAfsAssinadas = this.autFornecimentoFacade.confirmarAssinaturaAf(new LinkedHashSet<PesquisaAutorizacaoFornecimentoVO>(allChecked));			
			listaAfsAssinadas = new ArrayList<PesquisaAutorizacaoFornecimentoVO>(allChecked);			
					
			if (this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HU_UTILIZA_PORTAL_FORN).getVlrTexto().equalsIgnoreCase("P")){
			    this.gerarRelatorio(listaAfsAssinadas);
			}
			else {
			   this.envioEmailAssinaturaAFController.enviarEmail(listaAfsAssinadas, null);
			}						
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ASSINATURA_AF_SUCESSO");
			this.pesquisar();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);			
		}  catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_ENVIAR_EMAIL");
		}	
	}
	
	public void cancelarAssinatura(){
		try {
			this.autFornecimentoFacade.cancelarAssinaturaAf(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_DEVOLUCAO_AF_SUCESSO");
			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
		
	}
	
	public void limpar() {
		this.emContrato = Boolean.FALSE;
		this.resultado = null;
		this.numeroAf = null;
		this.numeroComplemento = null;
		this.numeroContrato = null;
		this.fornecedor = null;
		this.servidorGestor = null;
		this.modalidadeCompra = null;
		this.grupoMaterial = null;
		this.grupoServico = null;
		this.verbaGestao = null;
		this.naturezaDespesa = null;
		this.grupoNaturezaDespesa = null;
		this.exibeMaterial = false;
		this.exibeServico = false;
		this.exibeTodas = true;
		this.pesqItens = false;
		tipoConsulta = DominioTipoConsultaAssinarAF.T;
		limparControleGrid();
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String editarContrato(Integer numContrato) {
		this.contSelecionadoNro = numContrato;
		ScoContrato contrato = this.siconFacade.obterContratoPorNumeroContrato(numContrato.longValue());
		
			if (DominioOrigemContrato.A.equals(contrato.getIndOrigem())) {
				return SICON_MANTER_CONTRATO_AUTOMATICO;
			}
			else if (DominioOrigemContrato.M.equals(contrato.getIndOrigem())) {
				return SICON_MANTER_CONTRATO_MANUAL;
			}
		return null;
	}
	
	public String verItens(){
		return ITENS_AUTORIZACAO_FORNECIMENTO;
	}
	
	public String verItensPendentes(){
		return ITENS_PENDENTES_PAC;
	}
	
	
	public List<VScoFornecedor> pesquisarFornecedores(String param) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(param),pesquisarFornecedoresCount(param));
	}
	
	public Long pesquisarFornecedoresCount(String param) {
		return this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocialCount(param);
	}
	
	public List<RapServidores> listarServidores(String objPesquisa) {
			return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidoresPorVinculoMatriculaDescVinculoNome(objPesquisa),listarServidoresCount(objPesquisa));
	}
	
	public Integer listarServidoresCount(String objPesquisa) {
		return this.registroColaboradorFacade.pesquisarServidoresPorVinculoMatriculaDescVinculoNomeCount(objPesquisa);
	}

	
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter);
	}
	
	public List<ScoGrupoMaterial> listarGrupoMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter),listarGrupoMateriaisCount(filter));
	}
	
	public Long listarGrupoMateriaisCount(String filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}
	
	public List<ScoGrupoServico> listarGrupoServico(String filter){
		return this.comprasFacade.listarGrupoServico(filter);
	}
	
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(String objParam)
			throws ApplicationBusinessException {
				return this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(objParam);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesaPorGrupo(String objParam)
			throws ApplicationBusinessException {
				return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezaDespesaPorGrupo(this.grupoNaturezaDespesa, objParam);
	}
	
	public Long pesquisarNaturezaDespesaPorGrupoCount(Object objParam)
			throws ApplicationBusinessException {
			return this.cadastrosBasicosOrcamentoFacade.pesquisarNaturezaDespesaPorGrupoCount(this.grupoNaturezaDespesa, objParam);
	}
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaDespesaPorCodigoEDescricaoAtivos(
			String filter) {
		return cadastrosBasicosOrcamentoFacade
				.pesquisarGrupoNaturezaDespesaPorCodigoEDescricaoAtivos(filter);
	}
	
	public void setItemSelecionadoParaDevolucao(PesquisaAutorizacaoFornecimentoVO item) {
		itemSelecionado = item;
	}
	public String getMensagemsModal() {
		try {
			String msg = this.getBundle().getString("MENSAGEM_DEVOLUCAO_ASSINATURA_AF");
			String numeroAf = itemSelecionado.getLctNumero().toString();
			// Faz a interpolacao de parametros na mensagem
			msg = MessageFormat.format(msg, numeroAf, itemSelecionado.getNumeroComplemento());	
			return msg;
		} catch (Exception e) {
			return "MENSAGEM_DEVOLUCAO_ASSINATURA_AF ";
		}
	}

	
	public String verItensAFP(final Integer numAf) {
		this.setAutFornecimento(new ScoAutorizacaoForn());
		this.getAutFornecimento().setNumero(numAf);
		this.pesqItens = true;
		return ITENS_AUTORIZACAO_FORNECIMENTO_PEDIDO;
	}
	
	public String voltar() {
		this.pesqItens = true;
		return voltarPara;
	}

	public String getStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public void setComplementoNulo() {
		if (this.getNumeroAf() == null) {
			this.setNumeroComplemento(null);
		}
	}
	
	
	public void gerarRelatorio(List<PesquisaAutorizacaoFornecimentoVO> listaAfsAssinadas) throws ApplicationBusinessException, JRException, IOException, DocumentException {
		
		for(PesquisaAutorizacaoFornecimentoVO afAssinada:listaAfsAssinadas){
			
			if (comprasFacade.existeAcessoFornecedorPorFornecedorDtEnvio(afAssinada.getFornecedor()) &&
				afAssinada.getNumeroAFP() != null){
				relatorioAutorizacaoFornecimentoController.setNumPac(afAssinada.getLctNumero());
				relatorioAutorizacaoFornecimentoController.setNroComplemento(afAssinada.getNumeroComplemento());
				relatorioAutorizacaoFornecimentoController.setAfpNumero(afAssinada.getNumeroAFP());
				relatorioAutorizacaoFornecimentoController.setItemVersaoAf(afAssinada.getNumeroAFP() != null ? null : "0");
				relatorioAutorizacaoFornecimentoController.gerarArquivoPDF();
			}
			
			if (afAssinada.getPublicaCUM()){
				relatorioPacientesCUMController.setAfeAfnNumero(afAssinada.getAfnNumero());
				relatorioPacientesCUMController.setAfeNumero(afAssinada.getNumeroAFP());
				relatorioPacientesCUMController.gerarArquivoPdf();
			}
		}
		
	}
	
	public void setNaturezaDespesaParaNulo() {
		this.naturezaDespesa = null;
	}

	
	public DominioTipoConsultaAssinarAF getTipoConsulta() {
		return tipoConsulta;
	}


	public void setTipoConsulta(DominioTipoConsultaAssinarAF tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}


	public Boolean getEmContrato() {
		return emContrato;
	}


	public void setEmContrato(Boolean emContrato) {
		this.emContrato = emContrato;
	}


	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGruposervico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	public void setResultado(List<PesquisaAutorizacaoFornecimentoVO> resultado) {
		this.resultado = resultado;
	}

	public List<PesquisaAutorizacaoFornecimentoVO> getResultado() {
		return resultado;
	}
	
	
	public DominioTipoCadastroItemContrato getTipoCompra() {
		return tipoCompra;
	}


	public void setTipoCompra(DominioTipoCadastroItemContrato tipoCompra) {
		this.tipoCompra = tipoCompra;
	}
	

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public Boolean getExibeTodas() {
		return exibeTodas;
	}

	public void setExibeTodas(Boolean exibeTodas) {
		this.exibeTodas = exibeTodas;
	}

	public Boolean getExibeMaterial() {
		return exibeMaterial;
	}

	public void setExibeMaterial(Boolean exibeMaterial) {
		this.exibeMaterial = exibeMaterial;
	}

	public Boolean getExibeServico() {
		return exibeServico;
	}

	public void setExibeServico(Boolean exibeServico) {
		this.exibeServico = exibeServico;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public ScoAutorizacaoForn getAutFornecimento() {
		return autFornecimento;
	}

	public void setAutFornecimento(ScoAutorizacaoForn autFornecimento) {
		this.autFornecimento = autFornecimento;
	}
	
	public Boolean getPesqItens() {
		return pesqItens;
	}

	public void setPesqItens(Boolean pesqItens) {
		this.pesqItens = pesqItens;
	}

	public PesquisaAutorizacaoFornecimentoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(PesquisaAutorizacaoFornecimentoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<PesquisaAutorizacaoFornecimentoVO> getListaAfsAssinadas() {
		return listaAfsAssinadas;
	}

	public void setListaAfsAssinadas(
			List<PesquisaAutorizacaoFornecimentoVO> listaAfsAssinadas) {
		this.listaAfsAssinadas = listaAfsAssinadas;
	}

	public Boolean getExibeModal() {
		return exibeModal;
	}

	public void setExibeModal(Boolean exibeModal) {
		this.exibeModal = exibeModal;
	}
	
	public Integer getContSelecionadoNro() {
			return contSelecionadoNro;
	}
	
	public void setContSelecionadoNro(Integer contSelecionadoNro) {
		this.contSelecionadoNro = contSelecionadoNro;
	}

	public Boolean getPossuiPermissaoAssinar() {
		return possuiPermissaoAssinar;
	}

	public void setPossuiPermissaoAssinar(Boolean possuiPermissaoAssinar) {
		this.possuiPermissaoAssinar = possuiPermissaoAssinar;
	}

	public Boolean getPossuiPermissaoConsultarAssinar() {
		return possuiPermissaoConsultarAssinar;
	}

	public void setPossuiPermissaoConsultarAssinar(
			Boolean possuiPermissaoConsultarAssinar) {
		this.possuiPermissaoConsultarAssinar = possuiPermissaoConsultarAssinar;
	}

	public Boolean getPossuiPermissaoGerenciarContratos() {
		return possuiPermissaoGerenciarContratos;
	}

	public void setPossuiPermissaoGerenciarContratos(
			Boolean possuiPermissaoGerenciarContratos) {
		this.possuiPermissaoGerenciarContratos = possuiPermissaoGerenciarContratos;
	}

	public DynamicDataModel<PesquisaAutorizacaoFornecimentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<PesquisaAutorizacaoFornecimentoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<PesquisaAutorizacaoFornecimentoVO> getListaChecked() {
		return listaChecked;
	}

	public void setListaChecked(List<PesquisaAutorizacaoFornecimentoVO> listaChecked) {
		this.listaChecked = listaChecked;
	}

	public List<PesquisaAutorizacaoFornecimentoVO> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<PesquisaAutorizacaoFornecimentoVO> allChecked) {
		this.allChecked = allChecked;
	}

	public List<PesquisaAutorizacaoFornecimentoVO> getListaAtual() {
		return listaAtual;
	}

	public void setListaAtual(List<PesquisaAutorizacaoFornecimentoVO> listaAtual) {
		this.listaAtual = listaAtual;
	}
	
}

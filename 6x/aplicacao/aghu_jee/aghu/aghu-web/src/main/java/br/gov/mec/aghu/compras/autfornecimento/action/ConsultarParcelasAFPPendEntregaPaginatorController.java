package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailAtrasoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.action.ManterAutorizacaoFornecimentoPendenteEntregaController;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.compras.vo.FiltroParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class ConsultarParcelasAFPPendEntregaPaginatorController extends ActionController implements ActionPaginator {

    private static final long serialVersionUID = 4994637553347979220L;
    private static final String MANTER_AUTORIZACAO_FORNECIMENTO_PENDENTE_ENTREGA = "compras-manterAutorizacaoFornecimentoPendenteEntrega";
    private static final String PESQUISA_PROG_ENTREGA_FORNECEDOR = "pesquisaProgEntregaFornecedor";
    private static final String MANTER_CADASTRO_FORNECEDOR = "compras-manterCadastroFornecedor";
    
	@Inject @Paginator
	private DynamicDataModel<ParcelasAFPendEntVO> dataModel;
	
    @EJB
    private IComprasFacade comprasFacade;

    @EJB
    protected IRegistroColaboradorFacade registroColaboradorFacade;

    @EJB
    private IAutFornecimentoFacade autFornecimentoFacade;


    @Inject
    private ManterAutorizacaoFornecimentoPendenteEntregaController manterAFController;

    @Inject
    private AutorizacaoFornecimentoEmailAtrasoController atrasoController;

    @Inject
    private PesquisaProgEntregaFornecedorPaginatorController pesquisaProgEntregaFornecedorPaginatorController;

    @Inject
    private ManterAutorizacaoFornecimentoPendenteEntregaController manterAutorizacaoFornecimentoPendenteEntregaController;

    private AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO = new AutorizacaoFornecimentoEmailAtrasoVO();

    private Boolean desabilitarPeriodoEntrega = false;

    private List<ParcelasAFPendEntVO> afsSelecionados = new ArrayList<ParcelasAFPendEntVO>();

    private FiltroParcelasAFPendEntVO filtro = new FiltroParcelasAFPendEntVO();

    // Parâmetro a ser passado para estória #24874
    private ParcelasAFPendEntVO afSelecionadoVO = new ParcelasAFPendEntVO();

    private List<ParcelasAFPendEntVO> afsPendentes = new ArrayList<ParcelasAFPendEntVO>();

    private Integer numeroFrn;

    private Boolean pesquisou = Boolean.FALSE;

    private Boolean habilitarNotificacaoEmail = Boolean.FALSE;

    private Boolean habilitarCadastroFornecedor = Boolean.FALSE;

    private Integer itemSelecionado;

    private Boolean allChecked = Boolean.FALSE;
    private Boolean isAllChecked = Boolean.FALSE;

    private String fromCompradorAF;

    private ParcelasAFPendEntVO parcelaSelecionada;

    // ManterAutorizacaoFornecimentoPendenteEntrega
    private Boolean bloquearCamposTelaMAFPE= Boolean.FALSE;

    private boolean ativo = Boolean.FALSE;
    
    @PostConstruct
    protected void inicializar(){
        this.begin(conversation);
    }

    public void iniciar() {




        if(this.afSelecionadoVO != null && this.afSelecionadoVO.getFornecedor() != null) {
            ScoContatoFornecedor contato = this.autFornecimentoFacade.obterPrimeiroContatoPorFornecedor(this.afSelecionadoVO.getFornecedor());
            this.afSelecionadoVO.setContato(contato);
        }

    }


    public void desativarAtivarPeriodoEntrega() {
        this.desabilitarPeriodoEntrega = autFornecimentoFacade
                .desativarAtivarPeriodoEntrega(filtro);
    }

    public void controlaSelecaoTodos() {
        if (isAllChecked == false) {
            this.isAllChecked = true;
            this.allChecked =true;
        }else{
            this.isAllChecked = false;
            this.allChecked =false;
        }
    }

    public void selecionarTodasAFs() {
    	controlaSelecaoTodos();
		
		for(ParcelasAFPendEntVO vo : afsPendentes) {
			vo.setAtivo(this.getAllChecked());
		}
		
		if (isAllChecked) {
			autFornecimentoFacade.selecionarTodasAFs(getAllChecked(), afsPendentes, afsSelecionados);
		    this.habilitarNotificacaoEmail = Boolean.TRUE;
		    this.habilitarCadastroFornecedor = Boolean.TRUE;
		} else {
			this.desfazerSelecaoTodasAFs();
			this.habilitarNotificacaoEmail = Boolean.FALSE;
		    this.habilitarCadastroFornecedor = Boolean.FALSE;
		    this.itemSelecionado = null;
		    this.afSelecionadoVO = null;
		}
    }

    private void desfazerSelecaoTodasAFs() {
        autFornecimentoFacade.desfazerSelecaoTodasAFs(afsPendentes,
                afsSelecionados);
    }

    public String manterCadastroFornecedor() {
        this.setFromCompradorAF("Sim");
        return MANTER_CADASTRO_FORNECEDOR;
    }

    public String pesquisarAcessoFornecedor(Integer indice) {
        this.selecionarItem(indice);
        this.setFromCompradorAF("Sim");
        setParametros();
        return PESQUISA_PROG_ENTREGA_FORNECEDOR;
    }

    /**
     * Chamada para estoria #24874
     *
     * @return String <AUTORIZACAO_FORNECIMENTO_PENDENTE_ENTREGA>
     */
    public String retornarManterAutorizacaoFornecimentoPendenteEntrega(Integer indice) {
        this.selecionarItem(indice);
        this.manterAFController.setAfSelecionadoVO(getAfSelecionadoVO());
        this.manterAFController.setFiltros(true);
        return MANTER_AUTORIZACAO_FORNECIMENTO_PENDENTE_ENTREGA;
    }

    public String visualizarParcelas() {
        return MANTER_AUTORIZACAO_FORNECIMENTO_PENDENTE_ENTREGA;
    }

    public String botaoParcelas() {
		
		if (parcelaSelecionada == null){
			parcelaSelecionada = new ParcelasAFPendEntVO();
		}

        if (this.filtro.getFornecedor() != null) {
            parcelaSelecionada.setFornecedor(this.filtro.getFornecedor());
            manterAutorizacaoFornecimentoPendenteEntregaController.setAfSelecionadoVO(new ParcelasAFPendEntVO());
            manterAutorizacaoFornecimentoPendenteEntregaController.getAfSelecionadoVO().setFornecedor(this.filtro.getFornecedor());
            manterAutorizacaoFornecimentoPendenteEntregaController.setFiltros(Boolean.TRUE);
        } else {
        	manterAutorizacaoFornecimentoPendenteEntregaController.setFiltros(Boolean.FALSE);
        }

        if (getAfSelecionadoVO()!= null){
        	manterAutorizacaoFornecimentoPendenteEntregaController.setAfSelecionadoVO(getAfSelecionadoVO());	
        }

        manterAutorizacaoFornecimentoPendenteEntregaController.setPesquisou(Boolean.FALSE);
        return MANTER_AUTORIZACAO_FORNECIMENTO_PENDENTE_ENTREGA;
    }

    public String determinarProcessamentoAFs() {
        String retorno = "";

        try {
            atrasoController.setAfsSelecionadas(afsSelecionados);
            retorno = autFornecimentoFacade.determinarProcessamentoAFs(autorizacaoFornecimentoEmailAtrasoVO, afsSelecionados);
        } catch (BaseException e) {
        	this.apresentarExcecaoNegocio(e);
        }

        return retorno;
    }

    public void selecionarItem(Integer indice) {
    	if (indice == null && this.afSelecionadoVO.getIndiceLista() != null){
    		indice = this.afSelecionadoVO.getIndiceLista();
    	}
    	
        if (indice != null && !indice.equals(this.itemSelecionado)) {
            this.itemSelecionado = indice;
            if (!afsPendentes.isEmpty()) {
                ParcelasAFPendEntVO afSelecionado = this.getAfsPendentes().get(indice);
                this.afSelecionadoVO = afSelecionado;
                this.parcelaSelecionada = afSelecionado;
            } else if (parcelaSelecionada != null) {
                ParcelasAFPendEntVO afSelecionado = this.getParcelaSelecionada();
                this.afSelecionadoVO = afSelecionado;            	
            }
            this.habilitarCadastroFornecedor = Boolean.TRUE;
            ScoFornecedor forn = this.afSelecionadoVO.getFornecedor();
            if (forn != null) {
                ScoContatoFornecedor contato = this.autFornecimentoFacade.obterPrimeiroContatoPorFornecedor(forn);
                this.afSelecionadoVO.setContato(contato);
                this.numeroFrn = forn.getNumero();
            }
        }


        this.allChecked = false;
    }

    private void setParametros() {
        AcessoFornProgEntregaFiltrosVO vo = new AcessoFornProgEntregaFiltrosVO();
        vo.setNumeroAFP(this.getAfSelecionadoVO().getNumeroAFP());
        vo.setNumeroAF(this.getAfSelecionadoVO().getNumeroAF());
        vo.setComplemento(this.getAfSelecionadoVO().getComplemento());
        pesquisaProgEntregaFornecedorPaginatorController.setFiltro(vo);
        pesquisaProgEntregaFornecedorPaginatorController.setFromCompradorAF(this.getFromCompradorAF());
    }

    public void selecionarAFs(ParcelasAFPendEntVO parcela) {
        autFornecimentoFacade.selecionarAFs(parcela, afsPendentes, afsSelecionados);
        parcelaSelecionada = parcela;

        if (this.afsSelecionados.isEmpty() && this.habilitarNotificacaoEmail) {
            this.habilitarNotificacaoEmail = Boolean.FALSE;
            this.habilitarCadastroFornecedor = Boolean.FALSE;
        } else {
        	selecionarItem(parcelaSelecionada.getIndiceLista());
            this.habilitarNotificacaoEmail = Boolean.TRUE;
            this.habilitarCadastroFornecedor = Boolean.TRUE;
        }

        this.allChecked = Boolean.FALSE;

        if (this.ativo){
        	this.ativo = Boolean.FALSE;
        	this.parcelaSelecionada = new ParcelasAFPendEntVO();
        	this.afSelecionadoVO = new ParcelasAFPendEntVO();
        }else{
        	this.ativo = Boolean.TRUE;
        	this.afSelecionadoVO = parcela;
        }
        
    }

    public List<RapServidores> listarServidores(String objPesquisa) {
        return this.returnSGWithCount(this.registroColaboradorFacade
                .pesquisarServidorPorMatriculaNome(objPesquisa), listarServidoresCount(objPesquisa));
    }

    public Long listarServidoresCount(Object objPesquisa) {
        return this.registroColaboradorFacade
                .pesquisarServidorPorMatriculaNomeCount(objPesquisa);
    }

    public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(
            String objPesquisa) {
        return this.returnSGWithCount(this.comprasFacade
                .getListaMaterialByNomeOrDescOrCodigo(objPesquisa),listarMateriaisCount(objPesquisa));
    }

    public Long listarMateriaisCount(String param) {
        return this.comprasFacade.listarScoMateriaisAtivosCount(param, null,
                Boolean.TRUE);
    }

	public List<ScoGrupoMaterial> obterGrupos(String objPesquisa) {
        return this.returnSGWithCount(this.comprasFacade
                .obterGrupoMaterialPorSeqDescricao(objPesquisa), obterGruposCount(objPesquisa));
    }

    public Long obterGruposCount(String param){
    	return this.comprasFacade.obterGrupoMaterialPorSeqDescricaoCount(param);
    }
    
    @Override
    public Long recuperarCount() {
        return autFornecimentoFacade.listarParcelasAFsPendentesCount(this
                .getFiltro());
    }

	@Override
	public List<ParcelasAFPendEntVO> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		try {
			afsPendentes = this.autFornecimentoFacade
			        .listarParcelasAFsPendentes(this.getFiltro(), firstResult, maxResult, order, asc);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
        
    	for(ParcelasAFPendEntVO vo : afsPendentes) {
    		vo.setAtivo(this.getAllChecked());
    	}

        return afsPendentes;
	}

    public void pesquisar() {
    	this.dataModel.reiniciarPaginator();
    }

    public void limpar() {
        this.setDesabilitarPeriodoEntrega(false);
        this.setFiltro(null);
        this.setAfsPendentes(null);
        this.setAfSelecionadoVO(null);
        this.setAfsSelecionados(null);
        this.setParcelaSelecionada(null);
        this.pesquisou = Boolean.FALSE;
        this.filtro = new FiltroParcelasAFPendEntVO();
        this.afsPendentes = new ArrayList<ParcelasAFPendEntVO>();
        this.afsSelecionados = new ArrayList<ParcelasAFPendEntVO>();
        this.desabilitarPeriodoEntrega = false;
        this.itemSelecionado = null;
        this.allChecked = Boolean.FALSE;
        this.numeroFrn = null;
        this.afSelecionadoVO = new ParcelasAFPendEntVO();
        this.autorizacaoFornecimentoEmailAtrasoVO = new AutorizacaoFornecimentoEmailAtrasoVO();
        this.setHabilitarNotificacaoEmail(Boolean.FALSE);
        this.setHabilitarCadastroFornecedor(Boolean.FALSE);
		this.dataModel.limparPesquisa();
    }

    public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(
            String parametro) {
        return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, null,
                100, null, true),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
    }

    public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
        return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
    }

    public void setAfsSelecionados(List<ParcelasAFPendEntVO> afsSelecionados) {
        this.afsSelecionados = afsSelecionados;
    }

    public List<ParcelasAFPendEntVO> getAfsSelecionados() {
        return afsSelecionados;
    }

    public void setFiltro(FiltroParcelasAFPendEntVO filtro) {
        this.filtro = filtro;
    }

    public FiltroParcelasAFPendEntVO getFiltro() {
        return filtro;
    }

    public void setAfSelecionadoVO(ParcelasAFPendEntVO afSelecionadoVO) {
        this.afSelecionadoVO = afSelecionadoVO;
    }

    public ParcelasAFPendEntVO getAfSelecionadoVO() {
        return afSelecionadoVO;
    }

    public void setAfsPendentes(List<ParcelasAFPendEntVO> afsPendentes) {
        this.afsPendentes = afsPendentes;
    }

    public List<ParcelasAFPendEntVO> getAfsPendentes() {
        return afsPendentes;
    }

    public void setPesquisou(Boolean pesquisou) {
        this.pesquisou = pesquisou;
    }

    public Boolean getPesquisou() {
        return pesquisou;
    }

    public void setNumeroFrn(Integer numeroFrn) {
        this.numeroFrn = numeroFrn;
    }

    public Integer getNumeroFrn() {
        return numeroFrn;
    }

    public void setItemSelecionado(Integer itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public Integer getItemSelecionado() {
        return itemSelecionado;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setDesabilitarPeriodoEntrega(Boolean desabilitarPeriodoEntrega) {
        this.desabilitarPeriodoEntrega = desabilitarPeriodoEntrega;
    }

    public Boolean getDesabilitarPeriodoEntrega() {
        return desabilitarPeriodoEntrega;
    }

    public void setAutorizacaoFornecimentoEmailAtrasoVO(
            AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO) {
        this.autorizacaoFornecimentoEmailAtrasoVO = autorizacaoFornecimentoEmailAtrasoVO;
    }

    public AutorizacaoFornecimentoEmailAtrasoVO getAutorizacaoFornecimentoEmailAtrasoVO() {
        return autorizacaoFornecimentoEmailAtrasoVO;
    }

    public void setFromCompradorAF(String fromCompradorAF) {
        this.fromCompradorAF = fromCompradorAF;
    }

    public String getFromCompradorAF() {
        return fromCompradorAF;
    }

    public void setHabilitarNotificacaoEmail(Boolean habilitarNotificacaoEmail) {
        this.habilitarNotificacaoEmail = habilitarNotificacaoEmail;
    }

    public Boolean getHabilitarNotificacaoEmail() {
        return habilitarNotificacaoEmail;
    }

    public void setHabilitarCadastroFornecedor(
            Boolean habilitarCadastroFornecedor) {
        this.habilitarCadastroFornecedor = habilitarCadastroFornecedor;
    }

    public Boolean getHabilitarCadastroFornecedor() {
        return habilitarCadastroFornecedor;
    }

    public ParcelasAFPendEntVO getParcelaSelecionada() {
        return parcelaSelecionada;
    }

    public void setParcelaSelecionada(ParcelasAFPendEntVO parcelaSelecionada) {
        this.parcelaSelecionada = parcelaSelecionada;
    }

    public Boolean getBloquearCamposTelaMAFPE() {
        return bloquearCamposTelaMAFPE;
    }

    public void setBloquearCamposTelaMAFPE(Boolean bloquearCamposTelaMAFPE) {
        this.bloquearCamposTelaMAFPE = bloquearCamposTelaMAFPE;
    }

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public DynamicDataModel<ParcelasAFPendEntVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ParcelasAFPendEntVO> dataModel) {
		this.dataModel = dataModel;
	}

	
}
package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.action.ConsultarParcelasAFPPendEntregaPaginatorController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class ManterAutorizacaoFornecimentoPendenteEntregaController extends ActionController {

    private static final String CONSULTAR_PROGRAMACAO_GERAL_ENTREGA_AF = "compras-consultarProgramacaoGeralEntregaAF";


    private static final String MANTER_CADASTRO_FORNECEDOR = "compras-manterCadastroFornecedor";


    private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";


    private static final long serialVersionUID = -1087176913074436833L;


    @Inject
    private ConsultarParcelasAFPPendEntregaPaginatorController consultarParcelasAFPPendEntregaPaginatorController;

    @EJB
    private IComprasFacade comprasFacade;

    private ParcelasAFPendEntVO afSelecionadoVO;
    private boolean toggleAberto;
    private Integer numeroAFSelecionado;
    private List<ParcelaAfPendenteEntregaVO> listaParcelasPendentes;
    private ParcelaAfPendenteEntregaVO parcelaSelecionada;
    private boolean filtros;
    private boolean pesquisou;
    private String voltarParaUrl;
    private boolean desabilitarCampos;

    private ParcelaAfPendenteEntregaVO selecionado = new ParcelaAfPendenteEntregaVO();

    @PostConstruct
    protected void inicializar(){
        this.begin(conversation);
    }

    public void iniciar() {
        setToggleAberto(Boolean.TRUE);

        if (this.afSelecionadoVO == null) {
            this.afSelecionadoVO = new ParcelasAFPendEntVO();
        }
        desabilitarCampos = Boolean.FALSE;
        if (filtros) {
            desabilitarCampos = Boolean.TRUE;
            this.pesquisar();
        }

        if (getAfSelecionadoVO().getNumeroAFP() != null){
            desabilitarCampos = Boolean.TRUE;
            this.pesquisar();
        }
    }

    /*
     * se retorna -1 o accordion fecha, 0 abre o primeiro accordion
     */
    public int controlaAccordion(){
    	if (getListaParcelasPendentes() != null) {
    		return this.getListaParcelasPendentes().size() > 0 ? -1 : 0;
    	}
    	return 0;
    }

    public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(
            String parametro) {
        return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, null,
                Integer.valueOf(100), null, true),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
    }

    public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
        return this.comprasFacade.listarFornecedoresAtivosCount(parametro);
    }

    public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(
            String objPesquisa) throws BaseException {
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

    public void pesquisar() {
		try {
            setParcelaSelecionada(null);
            setToggleAberto(Boolean.FALSE);
            this.comprasFacade.validarCamposParaPesquisaParcelasAfPendenteEntrega(getAfSelecionadoVO());
            this.comprasFacade.validarPeriodoInformadoParcelasAfPendenteEntrega(getAfSelecionadoVO());
            this.listaParcelasPendentes = this.comprasFacade.pesquisarParcelaAfPendenteEntrega(getAfSelecionadoVO());
            setPesquisou(true);
        } catch (ApplicationBusinessException e) {
            apresentarExcecaoNegocio(e);
        }
    }

    public void limpar() {
        this.listaParcelasPendentes = new ArrayList<ParcelaAfPendenteEntregaVO>();
        setToggleAberto(Boolean.TRUE);
        setAfSelecionadoVO(new ParcelasAFPendEntVO());
        setParcelaSelecionada(null);
        if (!filtros) {
            setPesquisou(false);
        } else {
            setPesquisou(true);
        }
        desabilitarCampos = Boolean.FALSE;
    }

    public String cancelar() {
        this.limpar();
        consultarParcelasAFPPendEntregaPaginatorController.setParcelaSelecionada(null);
        setAfSelecionadoVO(null);
        setListaParcelasPendentes(null);
        return voltarParaUrl;
    }

    public String redirecionarEstatisticaConsumo(){
        return ESTOQUE_ESTATISTICA_CONSUMO;
    }

    public void gravar() {
        try {
            this.comprasFacade.gravarParcelaAfPendenteEntrega(getParcelaSelecionada());
            this.apresentarMsgNegocio(Severity.INFO, "MSG_AF_PENDENTE_ENTREGA_M3");
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
        }
    }

    public void atualizarDetalheParcela() {
        this.comprasFacade.selecionarParcelaAfPendenteEntrega(selecionado);
        setParcelaSelecionada(selecionado);
        this.parcelaSelecionada.setFornecedor(this.comprasFacade.obterFornecedorPorChavePrimaria(selecionado.getNumeroFrn()));
    }

    public String editarFornecedor() {
        return MANTER_CADASTRO_FORNECEDOR;
    }

    public String voltar() {
        this.limpar();
        return this.voltarParaUrl;
    }

    public void setAfSelecionadoVO(ParcelasAFPendEntVO afSelecionadoVO) {
        this.afSelecionadoVO = afSelecionadoVO;
    }

    public ParcelasAFPendEntVO getAfSelecionadoVO() {
        return this.afSelecionadoVO;
    }

    public void setToggleAberto(Boolean toggleAberto) {
        this.toggleAberto = toggleAberto.booleanValue();
    }

    public Boolean getToggleAberto() {
        return this.toggleAberto;
    }

    public void setNumeroAFSelecionado(Integer numeroAFSelecionado) {
        this.numeroAFSelecionado = numeroAFSelecionado;
    }

    public Integer getNumeroAFSelecionado() {
        return this.numeroAFSelecionado;
    }

    public List<ParcelaAfPendenteEntregaVO> getListaParcelasPendentes() {
        return this.listaParcelasPendentes;
    }

    public void setListaParcelasPendentes(
            List<ParcelaAfPendenteEntregaVO> listaParcelasPendentes) {
        this.listaParcelasPendentes = listaParcelasPendentes;
    }

    public ParcelaAfPendenteEntregaVO getParcelaSelecionada() {
        return this.parcelaSelecionada;
    }

    public void setParcelaSelecionada(
            ParcelaAfPendenteEntregaVO parcelaSelecionada) {
        this.parcelaSelecionada = parcelaSelecionada;
    }

    public boolean isFiltros() {
        return this.filtros;
    }

    public void setFiltros(boolean filtros) {
        this.filtros = filtros;
    }

    public boolean isPesquisou() {
        return pesquisou;
    }

    public void setPesquisou(boolean pesquisou) {
        this.pesquisou = pesquisou;
    }

    public void controleToggle() {
        if (getToggleAberto() == Boolean.TRUE) {
            setToggleAberto(Boolean.FALSE);
        } else {
            setToggleAberto(Boolean.TRUE);
        }
    }

    public void setVoltarParaUrl(String voltarParaUrl) {
        this.voltarParaUrl = voltarParaUrl;
    }

    public String getVoltarParaUrl() {
        return voltarParaUrl;
    }

    public String consultarGeral() {
        return CONSULTAR_PROGRAMACAO_GERAL_ENTREGA_AF;
    }

    public String getDescricaoFiltros() {
        StringBuilder sb = new StringBuilder(20);
        if (isPesquisou()) {
            if (getAfSelecionadoVO().getNumeroAF() != null) {
                sb.append("|Numero AF: " ).append(getAfSelecionadoVO().getNumeroAF());
            }

            getDescricaoFiltroCpAfpPerIni(sb);

            getDescFiltrosPerFimFornEntrAtra(sb);

            getDescFiltrosEmpRecMat(sb);

            if (getAfSelecionadoVO().getGrupoMaterial() != null) {
                sb.append("|Grupo Material: " ).append( getAfSelecionadoVO().getGrupoMaterial().getCodigo());
            }
        }

        return sb.toString();
    }

    private void getDescFiltrosEmpRecMat(StringBuilder sb) {
        if (getAfSelecionadoVO().getEmpenhada() != null) {
            sb.append("|Empenhada: " ).append( getAfSelecionadoVO().getEmpenhada().getDescricao());
        }

        if (getAfSelecionadoVO().getRecebido() != null) {
            sb.append("|Recebido: " ).append( getAfSelecionadoVO().getRecebido().getDescricao());
        }

        if (getAfSelecionadoVO().getMaterial() != null) {
            sb.append("|Material: " ).append( getAfSelecionadoVO().getMaterial().getCodigo());
        }
    }

    private void getDescFiltrosPerFimFornEntrAtra(StringBuilder sb) {
        if (getAfSelecionadoVO().getPeriodoEntregaFim() != null) {
            sb.append("|Periodo Fim: " ).append( DateUtil.dataToString(getAfSelecionadoVO().getPeriodoEntregaFim(), "dd/MM/yyyy"));
        }

        if (getAfSelecionadoVO().getFornecedor() != null && getAfSelecionadoVO().getFornecedor().getRazaoSocial().length() >= 10) {
            sb.append("|Fornecedor: " ).append( getAfSelecionadoVO().getFornecedor().getRazaoSocial().substring(0, 10));
        }

        if (getAfSelecionadoVO().getEntregaAtrasada() != null) {
            sb.append("|Entr Atrasada: " ).append( getAfSelecionadoVO().getEntregaAtrasada().getDescricao());
        }
    }

    private void getDescricaoFiltroCpAfpPerIni(StringBuilder sb) {
        if (getAfSelecionadoVO().getComplemento() != null) {
            sb.append("|Complemento: " ).append( getAfSelecionadoVO().getComplemento());
        }

        if (getAfSelecionadoVO().getNumeroAFP() != null) {
            sb.append("|Numero AFP: " ).append( getAfSelecionadoVO().getNumeroAFP());
        }

        if (getAfSelecionadoVO().getPeriodoEntregaInicio() != null) {
            sb.append("|Periodo Início: " ).append( DateUtil.dataToString(getAfSelecionadoVO().getPeriodoEntregaInicio(), "dd/MM/yyyy"));
        }
    }

    public boolean isDesabilitarCampos() {
        return desabilitarCampos;
    }

    public void setDesabilitarCampos(boolean desabilitarCampos) {
        this.desabilitarCampos = desabilitarCampos;
    }

	public ParcelaAfPendenteEntregaVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ParcelaAfPendenteEntregaVO selecionado) {
		this.selecionado = selecionado;
	}

}
package br.gov.mec.aghu.compras.solicitacaomaterial.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ScoPedidoMatExpedienteVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * 
 * @author Paulo Silveira
 * 
 */

public class PedidoMaterialExpedientePaginatorController extends ActionController implements ActionPaginator {

    @PostConstruct
    protected void inicializar() {
	this.begin(conversation);
    }

    @Inject
    @Paginator
    private DynamicDataModel<ScoPedidoMatExpediente> dataModel;

    private static final Log LOG = LogFactory.getLog(PedidoMaterialExpedientePaginatorController.class);

    private static final long serialVersionUID = -2297735947071880519L;

    private final String PAGE_VISUALIZAR_DETALHE_PEDIDO = "pedidoMaterialExpediente";

    @EJB
    private ICentroCustoFacade centroCustoFacade;

    @EJB
    private IRegistroColaboradorFacade colaboradorFacade;

    @EJB
    private IComprasFacade comprasFacade;

    private Integer seq;
    private FccCentroCustos centroCusto;
    private ScoPedidoMatExpedienteVO filtroVO;
    private RapServidores solicitante;

    public void iniciar() {
	if (this.filtroVO == null) {
	    this.filtroVO = new ScoPedidoMatExpedienteVO();
	}

    }

    private ScoPedidoMatExpedienteVO popularFiltro() {
	if (this.filtroVO == null) {
	    this.filtroVO = new ScoPedidoMatExpedienteVO();
	}
	if (this.centroCusto != null) {
	    this.filtroVO.setCodigoCentroCusto(this.centroCusto.getCodigo());

	} else {
	    this.filtroVO.setCodigoCentroCusto(null);
	}
	if (this.solicitante != null && this.solicitante.getId() != null) {
	    this.filtroVO.setMatriculaSolicitante(this.solicitante.getId().getMatricula());
	    this.filtroVO.setVinculoSolicitante(this.solicitante.getId().getVinCodigo());

	} else {
	    this.filtroVO.setMatriculaSolicitante(null);
	    this.filtroVO.setVinculoSolicitante(null);
	}
	return filtroVO;
    }

    public void limpar() {
	this.dataModel.limparPesquisa();
    }

    public void pesquisar() {
	try {
	    comprasFacade.validarDatas(filtroVO.getDataInicioEmissao(), filtroVO.getDataFimEmissao());
	    comprasFacade.validarDatas(filtroVO.getDataPedRecebInicial(), filtroVO.getDataPedRecebFinal());
	    this.dataModel.reiniciarPaginator();
	} catch (ApplicationBusinessException e) {
	    apresentarExcecaoNegocio(e);
	    LOG.error(e.getMessage(), e);
	}
    }

    public void sincronizar() {
	try {
	    RapServidores servidorLogado = colaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
	    this.comprasFacade.processarPedidosPapelaria(null, null, servidorLogado);
	    apresentarMsgNegocio("MENSAGEM_SINCRONIZACAO_SUCESSO");
	} catch (BaseException e) {
	    apresentarExcecaoNegocio(e);
	}

    }

    public void confTodos() {
	RapServidores servidorLogado;
	try {
	    servidorLogado = colaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
	    comprasFacade.procedureGeraAfpAutAutomatica(servidorLogado);
	} catch (BaseException e) {
	    apresentarExcecaoNegocio(e);
	}

    }

    public String detalhar() {
	return PAGE_VISUALIZAR_DETALHE_PEDIDO;
    }

    @Override
    public Long recuperarCount() {
	return this.comprasFacade.pesquisarScoPedidoMatExpCount(this.popularFiltro());
    }

    @Override
    public List<ScoPedidoMatExpediente> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
	try {
	    return this.comprasFacade.pesquisarScoPedidoMatExp(this.popularFiltro(), firstResult, maxResults, orderProperty, asc);
	} catch (ApplicationBusinessException e) {
	    apresentarExcecaoNegocio(e);
	    LOG.error(e.getMessage(), e);
	    return null;
	}
    }

    public List<FccCentroCustos> pesquisarCentroCusto(String param) {
	return this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao((String) param),
		pesquisarCentroCustoCount(param));
    }

    public Long pesquisarCentroCustoCount(String param) {
	return this.centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount((String) param);
    }

    public List<RapServidores> pesquisarSolicitante(String param) {
	return this.returnSGWithCount(this.colaboradorFacade.pesquisarServidorPorMatriculaNome(param), pesquisarSolicitanteCount(param));
    }

    public Long pesquisarSolicitanteCount(String param) {
	return this.colaboradorFacade.pesquisarServidorPorMatriculaNomeCount(param);
    }

    public void setCentroCusto(FccCentroCustos centroCusto) {
	this.centroCusto = centroCusto;
    }

    public FccCentroCustos getCentroCusto() {
	return centroCusto;
    }

    public void setFiltroVO(ScoPedidoMatExpedienteVO filtroVO) {
	this.filtroVO = filtroVO;
    }

    public ScoPedidoMatExpedienteVO getFiltroVO() {
	return filtroVO;
    }

    public void setSolicitante(RapServidores solicitante) {
	this.solicitante = solicitante;
    }

    public RapServidores getSolicitante() {
	return solicitante;
    }

    public void setSeq(Integer seq) {
	this.seq = seq;
    }

    public Integer getSeq() {
	return seq;
    }

    public DynamicDataModel<ScoPedidoMatExpediente> getDataModel() {
	return dataModel;
    }

    public void setDataModel(DynamicDataModel<ScoPedidoMatExpediente> dataModel) {
	this.dataModel = dataModel;
    }
}
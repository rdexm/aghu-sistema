package br.gov.mec.aghu.compras.solicitacaomaterial.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ScoPedidoMatExpedienteVO;
import br.gov.mec.aghu.dominio.DominioValidacaoNF;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ValidacaoNotaFiscalPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725406240783393551L;

	private final String PAGE_VISUALIZAR_DETALHE = "validacaoNotaFiscal";
	
	@Inject @Paginator
	private DynamicDataModel<ScoPedidoMatExpedienteVO> dataModel;
	
	private ScoPedidoMatExpedienteVO pedidoVO = new ScoPedidoMatExpedienteVO();
	private ScoPedidoMatExpediente pedidoEdicao;

	/*
	 * Enviado como parametro através do validacaoNotaFiscalList.page.xml
	 */
	private Integer seqVisualizacao;
	private Integer seqAcao;


	//filtros
	private Integer numeroNotaFiscal;
	private Date dataNotaFiscal;
	private Date dataInicioEmissao;
	private Date dataFimEmissao;
	private Date dataInicioPedido;
	private Date dataFimPedido;

	@EJB
	private IComprasFacade comprasFacade;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return this.getComprasFacade().pesquisarNotasFiscaisCount(this.getPedidoVO());
	}

	@Override
	public List<ScoPedidoMatExpedienteVO> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return this.getComprasFacade().pesquisarNotasFiscais(firstResult, maxResult, orderProperty, asc, this.getPedidoVO());
	}

	public void pesquisar() {

		try{
			this.getComprasFacade().validarDatas(dataInicioEmissao, dataFimEmissao);
			this.getComprasFacade().validarDatas(dataInicioPedido, dataFimPedido);

			pedidoVO.setDataFimEmissao(dataFimEmissao);
			pedidoVO.setDataFimPedido(dataFimPedido);
			pedidoVO.setDataInicioEmissao(dataInicioEmissao);
			pedidoVO.setDataInicioPedido(dataInicioPedido);
			pedidoVO.setNumeroNotaFiscal(numeroNotaFiscal);

			this.dataModel.reiniciarPaginator();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*
	 * Atualizar o pedido para validar sua nota fiscal.
	 */
	public void aprovar() throws ApplicationBusinessException {
		this.getComprasFacade().atualizarScePedidoMatExpedienteById(this.getNumeroNotaFiscal(), DominioValidacaoNF.S);
		this.pesquisar();
	}

	/*
	 * Atualizar o pedido para rejeitar sua nota fiscal.
	 */
	public void recusar() throws ApplicationBusinessException {
		this.getComprasFacade().atualizarScePedidoMatExpedienteById(this.getNumeroNotaFiscal(), DominioValidacaoNF.R);
		this.pesquisar();
	}

	public String visualizar() {
		//this.setSeqVisualizacao(notaFiscal.getNumeroNotaFiscal());
		//this.setDataNotaFiscal(notaFiscal.getDataNotaFiscal());
		return PAGE_VISUALIZAR_DETALHE;
	}

	public void limparPesquisa() {
		this.numeroNotaFiscal= null;
		this.dataInicioEmissao = null;
		this.dataFimEmissao = null;
		this.dataInicioPedido = null;
		this.dataFimPedido = null;
		this.dataModel.limparPesquisa();
	}

	public ScoPedidoMatExpedienteVO getPedidoVO() {
		return pedidoVO;
	}

	public void setPedidoVO(ScoPedidoMatExpedienteVO pedidoVO) {
		this.pedidoVO = pedidoVO;
	}

	public ScoPedidoMatExpediente getPedidoEdicao() {
		return pedidoEdicao;
	}

	public void setPedidoEdicao(ScoPedidoMatExpediente pedidoEdicao) {
		this.pedidoEdicao = pedidoEdicao;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public String visualizar(ScoPedidoMatExpediente scePedidoMatExpediente) {
		this.setSeqVisualizacao(scePedidoMatExpediente.getSeq());
		return "editarJobDetail";
	}

	public Integer getSeqVisualizacao() {
		return seqVisualizacao;
	}

	public void setSeqVisualizacao(Integer seqVisualizacao) {
		this.seqVisualizacao = seqVisualizacao;
	}

	public Integer getSeqAcao() {
		return seqAcao;
	}

	public void setSeqAcao(Integer seqAcao) {
		this.seqAcao = seqAcao;
	}

	public Integer getNumeroNotaFiscal() {
		return numeroNotaFiscal;
	}

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = Integer.valueOf(numeroNotaFiscal);
    }

	public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}

	public Date getDataInicioEmissao() {
		return dataInicioEmissao;
	}

	public void setDataInicioEmissao(Date dataInicioEmissao) {
		this.dataInicioEmissao = dataInicioEmissao;
	}

	public Date getDataFimEmissao() {
		return dataFimEmissao;
	}

	public void setDataFimEmissao(Date dataFimEmissao) {
		this.dataFimEmissao = dataFimEmissao;
	}

	public Date getDataInicioPedido() {
		return dataInicioPedido;
	}

	public void setDataInicioPedido(Date dataInicioPedido) {
		this.dataInicioPedido = dataInicioPedido;
	}

	public Date getDataFimPedido() {
		return dataFimPedido;
	}

	public void setDataFimPedido(Date dataFimPedido) {
		this.dataFimPedido = dataFimPedido;
	}

	public Date getDataNotaFiscal() {
		return dataNotaFiscal;
	}

	public void setDataNotaFiscal(Date dataNotaFiscal) {
		this.dataNotaFiscal = dataNotaFiscal;
	}

	public DynamicDataModel<ScoPedidoMatExpedienteVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoPedidoMatExpedienteVO> dataModel) {
		this.dataModel = dataModel;
	}

}


package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelMarcador;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class AelMarcadorPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4084581975610103477L;
	
	private static final String AEL_MARCADOR_CRUD = "aelMarcadorCRUD";
	@Inject @Paginator
	private DynamicDataModel<AelMarcador> dataModel;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private AelMarcador aelMarcador = new AelMarcador();
	
	private AelMarcador aelMarcadorEdicao = new AelMarcador();
	
	private AelMarcador aelMarcadorSelecionado;

	private Integer seq;

	private Boolean exibirBotaoNovo = false;
	
	private ScoMarcaComercial fabricante;
	
	private DominioSituacao indSituacaoMarcador;
	
	private String marcadorPedido;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void ativarInativar(final Integer seq) {
		try {

			if (seq != null) {
				aelMarcadorEdicao = examesPatologiaFacade.obterAelMarcadorPorChavePrimaria(seq);

				String mensagem = this.examesPatologiaFacade.ativarInativarAelMarcador(aelMarcadorEdicao);
				
				this.apresentarMsgNegocio(Severity.INFO, mensagem, aelMarcador.getMarcadorPedido());
				
				examesPatologiaFacade.alterarAelMarcador(aelMarcadorEdicao);
				aelMarcadorEdicao = new AelMarcador();
			}
			reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	public void pesquisar() {
		aelMarcador.setFabricante(fabricante);
		aelMarcador.setMarcadorPedido(marcadorPedido);
		aelMarcador.setIndSituacao(indSituacaoMarcador);
		this.setExibirBotaoNovo(true);
		this.reiniciarPaginator();
	}

	public void limpar() {
		aelMarcador = new AelMarcador();
		fabricante = null;
		marcadorPedido = null;
		indSituacaoMarcador = null;
		seq = null;
		this.setExibirBotaoNovo(false);
		this.setAtivo(false);
	}

	@Override
	public Long recuperarCount() {
		return examesPatologiaFacade.pesquisarAelMarcadorCount(this.getAelMarcador());
	}

	@Override
	public List<AelMarcador> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return examesPatologiaFacade.pesquisarAelMarcador(firstResult, maxResults, null, true, this.getAelMarcador());
	}

	public List<ScoMarcaComercial> pesquisarFabricante(String parametro) {
		try {
			return this.returnSGWithCount(this.comprasFacade.getListaMarcasByNomeOrCodigo(parametro, 0, 100, ScoMarcaComercial.Fields.DESCRICAO.toString(), true),pesquisarFabricanteCount(parametro));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String iniciarInclusao(){
		return AEL_MARCADOR_CRUD;
	}
	
	public Long pesquisarFabricanteCount(String parametro) {
		return comprasFacade.getListaMarcasByNomeOrCodigoCount(parametro);
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public void setAelMarcador(AelMarcador aelMarcador) {
		this.aelMarcador = aelMarcador;
	}

	public AelMarcador getAelMarcador() {
		return aelMarcador;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public DynamicDataModel<AelMarcador> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelMarcador> dataModel) {
		this.dataModel = dataModel;
	}
	
	public boolean isAtivo() {
		return this.dataModel.getPesquisaAtiva();
	}

	public void setAtivo(boolean ativo) {
		this.dataModel.setPesquisaAtiva(ativo);
	}

	public AelMarcador getAelMarcadorSelecionado() {
		return aelMarcadorSelecionado;
	}

	public void setAelMarcadorSelecionado(AelMarcador aelMarcadorSelecionado) {
		this.aelMarcadorSelecionado = aelMarcadorSelecionado;
	}

	public ScoMarcaComercial getFabricante() {
		return fabricante;
	}

	public void setFabricante(ScoMarcaComercial fabricante) {
		this.fabricante = fabricante;
	}

	public DominioSituacao getIndSituacaoMarcador() {
		return indSituacaoMarcador;
	}

	public void setIndSituacaoMarcador(DominioSituacao indSituacaoMarcador) {
		this.indSituacaoMarcador = indSituacaoMarcador;
	}

	public String getMarcadorPedido() {
		return marcadorPedido;
	}

	public void setMarcadorPedido(String marcadorPedido) {
		this.marcadorPedido = marcadorPedido;
	}
	
}
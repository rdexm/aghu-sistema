package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisaTituloResultadoPadraoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5772761590940924862L;

	private static final String MANTER_TITULO_RESULTADO_PADRAO = "exames-manterTituloResultadoPadrao";

	@EJB
	private IExamesFacade examesFacade;
	
	// Campos de filtro para pesquisa
	private Integer seq; // Código
	private String descricao;
	private DominioSituacao situacao;

	@Inject @Paginator
	private DynamicDataModel<AelResultadosPadrao> dataModel;
	
	private AelResultadosPadrao selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Recupera uma instancia com os filtros de pesquisa atualizados
	 */
	private AelResultadosPadrao getFiltroPesquisa(){
		
		final AelResultadosPadrao filtro = new AelResultadosPadrao();

		filtro.setSeq(this.seq);
		filtro.setDescricao(StringUtils.trim(this.descricao));
		filtro.setSituacao(this.situacao);

		return filtro;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarResultadosPadraoCount(this.getFiltroPesquisa());
	}
	
	@Override
	public List<AelResultadosPadrao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisarResultadosPadrao(firstResult, maxResult, orderProperty, asc, this.getFiltroPesquisa());
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.seq = null;
		this.descricao = null;
		this.situacao = null;	
	}
	
	public String inserir(){
		return MANTER_TITULO_RESULTADO_PADRAO;
	}
	
	public String editar(){
		return MANTER_TITULO_RESULTADO_PADRAO;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<AelResultadosPadrao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelResultadosPadrao> dataModel) {
		this.dataModel = dataModel;
	}

	public AelResultadosPadrao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelResultadosPadrao selecionado) {
		this.selecionado = selecionado;
	}
}
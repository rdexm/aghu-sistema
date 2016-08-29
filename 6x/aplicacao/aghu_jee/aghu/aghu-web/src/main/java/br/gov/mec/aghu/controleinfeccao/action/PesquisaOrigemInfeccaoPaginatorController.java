package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * @author israel.haas
 */

public class PesquisaOrigemInfeccaoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3735283656688571771L;
	
	private static final String REDIRECIONA_EDITAR_ORIGEM_INFECCAO = "cadastroOrigemInfeccao";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private CadastroOrigemInfeccaoController cadastroOrigemInfeccaoController;

	private String codigoOrigem;
	private String descricaoOrigem;
	private DominioSituacao situacaoOrigem;

	private OrigemInfeccoesVO origemSelecionada;
	
	@Inject @Paginator
	private DynamicDataModel<OrigemInfeccoesVO> dataModel;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigoOrigem = null;
		this.descricaoOrigem = null;
		this.situacaoOrigem = null;
		this.dataModel.setPesquisaAtiva(false);
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return this.controleInfeccaoFacade.listarOrigemInfeccoesCount(this.codigoOrigem, this.descricaoOrigem, this.situacaoOrigem);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrigemInfeccoesVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<OrigemInfeccoesVO> result = this.controleInfeccaoFacade.listarOrigemInfeccoes(firstResult, maxResults, orderProperty,
				asc, this.codigoOrigem, this.descricaoOrigem, this.situacaoOrigem);

		if (result == null) {
			result = new ArrayList<OrigemInfeccoesVO>();
		}

		return result;
	}
	
	public String editar() {
		this.cadastroOrigemInfeccaoController.setOrigemSelecionada(this.origemSelecionada);
		return REDIRECIONA_EDITAR_ORIGEM_INFECCAO;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}

	// ### GETs e SETs ###

	public String getCodigoOrigem() {
		return codigoOrigem;
	}

	public void setCodigoOrigem(String codigoOrigem) {
		this.codigoOrigem = codigoOrigem;
	}

	public String getDescricaoOrigem() {
		return descricaoOrigem;
	}

	public void setDescricaoOrigem(String descricaoOrigem) {
		this.descricaoOrigem = descricaoOrigem;
	}

	public DominioSituacao getSituacaoOrigem() {
		return situacaoOrigem;
	}

	public void setSituacaoOrigem(DominioSituacao situacaoOrigem) {
		this.situacaoOrigem = situacaoOrigem;
	}

	public OrigemInfeccoesVO getOrigemSelecionada() {
		return origemSelecionada;
	}

	public void setOrigemSelecionada(OrigemInfeccoesVO origemSelecionada) {
		this.origemSelecionada = origemSelecionada;
	}

	public DynamicDataModel<OrigemInfeccoesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<OrigemInfeccoesVO> dataModel) {
		this.dataModel = dataModel;
	}
}

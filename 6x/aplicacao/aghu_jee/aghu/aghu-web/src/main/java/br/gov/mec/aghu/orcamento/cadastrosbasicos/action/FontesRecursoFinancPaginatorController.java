package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class FontesRecursoFinancPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5860595457653656519L;

	private static final String FONTES_RECURSO_FINANC_CRUD = "fontesRecursoFinancCRUD";

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	private FsoFontesRecursoFinanc fontesRecursoFinanc;

	@Inject @Paginator
	private DynamicDataModel<FsoFontesRecursoFinanc> dataModel;
	
	private FsoFontesRecursoFinanc selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		fontesRecursoFinanc = new FsoFontesRecursoFinanc();
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setFontesRecursoFinanc(new FsoFontesRecursoFinanc());
	}

	public String inserir() {		
		return FONTES_RECURSO_FINANC_CRUD;
	}

	public String editar() {		
		return FONTES_RECURSO_FINANC_CRUD;
	}
	
	public void excluir() {
		try {
			
			this.cadastrosBasicosOrcamentoFacade.excluirFontesRecursoFinanc(selecionado.getCodigo());			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_FONTES_RECURSO_FINANC_M04", selecionado.getDescricao());

		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);				
		}
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosOrcamentoFacade.countPesquisaFontesRecursoFinanc(this.getFontesRecursoFinanc());
	}

	@Override
	public List<FsoFontesRecursoFinanc> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return cadastrosBasicosOrcamentoFacade.listaPesquisaFontesRecursoFinanc(firstResult, maxResults, orderProperty, asc, fontesRecursoFinanc);
	}

	public void setFontesRecursoFinanc(
			final FsoFontesRecursoFinanc fontesRecursoFinanc) {
		this.fontesRecursoFinanc = fontesRecursoFinanc;
	}

	public FsoFontesRecursoFinanc getFontesRecursoFinanc() {
		return fontesRecursoFinanc;
	}

	public DynamicDataModel<FsoFontesRecursoFinanc> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FsoFontesRecursoFinanc> dataModel) {
		this.dataModel = dataModel;
	}

	public FsoFontesRecursoFinanc getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(FsoFontesRecursoFinanc selecionado) {
		this.selecionado = selecionado;
	}
}

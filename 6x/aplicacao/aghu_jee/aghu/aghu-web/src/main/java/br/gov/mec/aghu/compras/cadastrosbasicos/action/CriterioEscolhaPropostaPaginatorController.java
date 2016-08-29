package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class CriterioEscolhaPropostaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4243919536767889238L;

	private static final String CRITERIO_ESCOLHA_PROPOSTA_CRUD = "criterioEscolhaPropostaCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private SecurityController securityController;	

	// filtros
	private Short codigoCriterio;
	private String descricaoCriterio;
	private DominioSituacao situacaoCriterio;
	
	@Inject @Paginator
	private DynamicDataModel<ScoCriterioEscolhaProposta> dataModel;
	
	private ScoCriterioEscolhaProposta selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioCompras,gravar") || securityController.usuarioTemPermissao("cadastrarAdmCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}
	
	@Override
	public Long recuperarCount() {
		return this.comprasCadastrosBasicosFacade.pesquisarCriterioEscolhaPropostaCount (
				this.codigoCriterio, this.descricaoCriterio, this.situacaoCriterio);		
	}

	@Override
	public List<ScoCriterioEscolhaProposta> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarCriterioEscolhaProposta(firstResult, maxResult,	orderProperty, asc, codigoCriterio, descricaoCriterio, situacaoCriterio);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.codigoCriterio = null;
		this.descricaoCriterio = null;
		this.situacaoCriterio = null;
	}

	public String inserir() {
		return CRITERIO_ESCOLHA_PROPOSTA_CRUD;
	}

	public String editar() {
		return CRITERIO_ESCOLHA_PROPOSTA_CRUD;
	}

	public String visualizar() {
		return CRITERIO_ESCOLHA_PROPOSTA_CRUD;
	}

	public Short getCodigoCriterio() {
		return codigoCriterio;
	}

	public void setCodigoCriterio(Short codigoCriterio) {
		this.codigoCriterio = codigoCriterio;
	}

	public String getDescricaoCriterio() {
		return descricaoCriterio;
	}

	public void setDescricaoCriterio(String descricaoCriterio) {
		this.descricaoCriterio = descricaoCriterio;
	}

	public DominioSituacao getSituacaoCriterio() {
		return situacaoCriterio;
	}

	public void setSituacaoCriterio(DominioSituacao situacaoCriterio) {
		this.situacaoCriterio = situacaoCriterio;
	}

	public DynamicDataModel<ScoCriterioEscolhaProposta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ScoCriterioEscolhaProposta> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoCriterioEscolhaProposta getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoCriterioEscolhaProposta selecionado) {
		this.selecionado = selecionado;
	}
}
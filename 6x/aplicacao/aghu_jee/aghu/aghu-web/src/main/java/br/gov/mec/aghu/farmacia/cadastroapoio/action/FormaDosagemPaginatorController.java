package br.gov.mec.aghu.farmacia.cadastroapoio.action;



import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class FormaDosagemPaginatorController  extends AbstractCrudMedicamentoPaginatorController<AfaFormaDosagem> {

	private static final long serialVersionUID = 188167398533109116L;
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@PostConstruct
	public void iniciar(){
		this.getDataModel().setUserEditPermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "formaDosagem", "alterar"));
		this.getDataModel().setUserRemovePermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "formaDosagem", "excluir"));
	}
	
	@Inject
	private FormaDosagemController formaDosagemController;
	@Inject
	private HistoricoFormaDosagemPaginatorController historicoFormaDosagemPaginatorController;
	
	@EJB
	private IPermissionService permissionService;
	
	
	

	@Override
	public List<AfaFormaDosagem> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.farmaciaApoioFacade.recuperarListaPaginadaFormaDosagem(this.getMedicamento(), firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade.recuperarCountFormaDosagem(this.getMedicamento());
	}

	@Override
	public String paginaHist() {
		historicoFormaDosagemPaginatorController.setMedicamento(getMedicamento());
		historicoFormaDosagemPaginatorController.getDataModel().reiniciarPaginator();
		return "historicoFormaDosagemList";
	}

	@Override
	public AbstractCrudMedicamentoController getCrudController() {
		return formaDosagemController;
	}

	@Override
	public String paginaCrud() {
		return "formaDosagemCRUD";
	}
	
	public String cancelar(){
		if (getFromList()){
			return "medicamentoList";
		}else{
			return "medicamentoCRUD";
		}
		
	}
	
	public void excluir(){
		getCrudController().setEntidade(getEntidadeSelecionada());
		getCrudController().excluir();
	}
	
}

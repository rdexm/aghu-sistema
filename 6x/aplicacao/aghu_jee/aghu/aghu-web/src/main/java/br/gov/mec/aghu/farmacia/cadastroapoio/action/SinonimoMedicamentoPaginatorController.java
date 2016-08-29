package br.gov.mec.aghu.farmacia.cadastroapoio.action;



import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class SinonimoMedicamentoPaginatorController extends AbstractCrudMedicamentoPaginatorController<AfaSinonimoMedicamento> {
	
	private static final long serialVersionUID = 8786654530365414559L;

	@Inject
	private SinonimoMedicamentoController sinonimoMedicamentoController;
	
	@Inject
	private HistoricoSinonimoMedicamentoPaginatorController historicoSinonimoMedicamentoPaginatorController;
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	
	
	@Override
	public List<AfaSinonimoMedicamento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		return this.farmaciaApoioFacade.recuperarListaPaginadaSinonimoMedicamento(this.getMedicamento(), firstResult, maxResult, true);
	}
	
	@PostConstruct
	public void init(){
		this.getDataModel().setUserEditPermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "sinonimoMedicamento", "alterar"));
		this.getDataModel().setUserRemovePermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "sinonimoMedicamento", "excluir"));
		begin(conversation);
	}

	public void excluir() {
		try {
			this.farmaciaApoioFacade.removerSinonimoMedicamento(getEntidadeSelecionada());
			
			this.apresentarMsgNegocio(Severity.INFO, 
					"MENSAGEM_SUCESSO_REMOCAO_SINONIMO",
					getEntidadeSelecionada().getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade.recuperarCountSinonimoMedicamento(this.getMedicamento());
	}

	@Override
	public String paginaHist() {
		historicoSinonimoMedicamentoPaginatorController.setMedicamento(getMedicamento());
		historicoSinonimoMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
		return "historicoSinonimoMedicamentoList";
	}

	@Override
	public AbstractCrudMedicamentoController getCrudController() {
		return sinonimoMedicamentoController;
	}

	@Override
	public String paginaCrud() {
		return "sinonimoMedicamentoCRUD";
	}
	
	public String cancelar(){
		if (getFromList()){
			return "medicamentoList";
		}else{
			return "medicamentoCRUD";
		}
		
	}

}
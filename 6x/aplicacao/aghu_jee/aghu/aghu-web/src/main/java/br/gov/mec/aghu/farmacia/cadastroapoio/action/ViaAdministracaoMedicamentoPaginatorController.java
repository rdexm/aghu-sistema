package br.gov.mec.aghu.farmacia.cadastroapoio.action;



import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class ViaAdministracaoMedicamentoPaginatorController extends AbstractCrudMedicamentoPaginatorController<AfaViaAdministracaoMedicamento> {
	
	private static final long serialVersionUID = 5690202505278957754L;
	
	@Inject
	private ViaAdministracaoMedicamentoController viaAdministracaoMedicamentoController;
	
	@Inject
	private HistoricoViasAdministracaoPaginatorController historicoViasAdministracaoPaginatorController;
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@Inject
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IPermissionService permissionService;

	@Override
	public List<AfaViaAdministracaoMedicamento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.farmaciaApoioFacade.recuperarListaPaginadaViaAdministracaoMedicamento(this.getMedicamento(), firstResult,
				maxResult, orderProperty, asc);
	}
	
	@PostConstruct
	public void init(){
		this.getDataModel().setUserEditPermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterViasAdmMdtos", "alterar"));
		begin(conversation);
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade.recuperarCountViaAdministracaoMedicamento(this.getMedicamento());
	}  
	
	@Override
	public AbstractCrudMedicamentoController getCrudController() {
		return viaAdministracaoMedicamentoController;
	}

	@Override
	public String paginaCrud() {
		return "viaAdministracaoMedicamentoCRUD";
	}
	
	@Override
	public String novo() {
		//Resolve lazy de vias adm que serão utilizadas na tela CRUD
		getMedicamento().
			setViasAdministracaoMedicamento(
					new HashSet<>(farmaciaFacade.pesquisarAfaViaAdministracaoMedicamento(getMedicamento())));
		return super.novo();
	}
	
	public String cancelar(){
		if (getFromList()){
			return "medicamentoList";
		}else{
			return "medicamentoCRUD";
		}
		
	}

	@Override
	public String paginaHist() {
		historicoViasAdministracaoPaginatorController.setMedicamento(getMedicamento());
		historicoViasAdministracaoPaginatorController.getDataModel().reiniciarPaginator();
		return "historicoViasDeAdministracaoList";
	}
}

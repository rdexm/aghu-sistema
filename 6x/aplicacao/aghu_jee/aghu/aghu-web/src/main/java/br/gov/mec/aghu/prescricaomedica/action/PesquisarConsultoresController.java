package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisarConsultoresController extends ActionController implements
		ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4898846495059037204L;

	@Inject
	@Paginator
	private DynamicDataModel<AghProfEspecialidades> dataModel;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private AghEspecialidades especialidades;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		if(especialidades != null){
			dataModel.reiniciarPaginator();
		}
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limpar() {
		this.dataModel.limparPesquisa();
		this.especialidades = null;
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadePorNomeOuSiglaAtivo(objPesquisa),
				pesquisarEspecialidadesCount(objPesquisa));
	}	

	public Long pesquisarEspecialidadesCount(String objPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadePorNomeOuSiglaAtivoCount(objPesquisa);
	}

	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.prescricaoMedicaFacade
				.listarConsultoresPorEspecialidade(firstResult, maxResult,
						orderProperty, asc, this.especialidades);
	}
	
	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade
				.listarConsultoresPorEspecialidadeCount(this.especialidades);
	}

	public DynamicDataModel<AghProfEspecialidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghProfEspecialidades> dataModel) {
		this.dataModel = dataModel;
	}

	public AghEspecialidades getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(AghEspecialidades especialidades) {
		this.especialidades = especialidades;
	}
}

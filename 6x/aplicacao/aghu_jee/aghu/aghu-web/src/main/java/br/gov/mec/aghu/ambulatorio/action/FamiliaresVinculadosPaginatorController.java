package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.vo.PacienteGrupoFamiliarVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class FamiliaresVinculadosPaginatorController extends ActionController implements ActionPaginator{

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	private AipPacientes pacienteContexto;
	@Inject	@Paginator
	private DynamicDataModel<PacienteGrupoFamiliarVO> dataModel;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		if(this.pacienteContexto!=null){
			return this.ambulatorioFacade.obterFamiliaresVinculadosCount(this.pacienteContexto);
		}else{
			return 0L;
		}
	}

	public DynamicDataModel<PacienteGrupoFamiliarVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PacienteGrupoFamiliarVO> dataModel) {
		this.dataModel = dataModel;
	}

	@Override
	public List<PacienteGrupoFamiliarVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<PacienteGrupoFamiliarVO> result=null;
		if( this.pacienteContexto != null){ 
			result = this.ambulatorioFacade.obterFamiliaresVinculados(firstResult, maxResult, orderProperty, asc, this.pacienteContexto);	
		}
		if (result == null) {
			result = new ArrayList<PacienteGrupoFamiliarVO>();
		}
		return result;
	}
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}

	public AipPacientes getPacienteContexto() {
		return pacienteContexto;
	}

	public void setPacienteContexto(AipPacientes pacienteContexto) {
		this.pacienteContexto = pacienteContexto;
	}


}

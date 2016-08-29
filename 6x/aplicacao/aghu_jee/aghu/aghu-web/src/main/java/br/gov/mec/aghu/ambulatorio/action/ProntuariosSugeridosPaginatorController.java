package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.vo.PacienteGrupoFamiliarVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ProntuariosSugeridosPaginatorController extends ActionController implements ActionPaginator {

	


	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@Inject	@Paginator
	private DynamicDataModel<PacienteGrupoFamiliarVO> dataModel;
	private AipEnderecosPacientes enderecoPacienteContexto;
	private AipPacientes pacienteContexto;
	

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	@Override
	public Long recuperarCount() {
		if(this.pacienteContexto != null){
			if(pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()== null && this.enderecoPacienteContexto != null){
				return this.ambulatorioFacade.obterProntuariosSugeridosVinculadosCount(this.pacienteContexto,this.enderecoPacienteContexto);
			}else if(this.enderecoPacienteContexto != null){
				return this.ambulatorioFacade.obterProntuariosSugeridosNaoVinculadosCount(this.pacienteContexto,this.enderecoPacienteContexto);
			}
		}
		return 0L;
	}

	@Override
	public List<PacienteGrupoFamiliarVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<PacienteGrupoFamiliarVO> result=null;
		if(this.pacienteContexto != null){
			if(pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()== null && this.enderecoPacienteContexto != null){
				result = this.ambulatorioFacade.obterProntuariosSugeridosVinculados(firstResult, maxResult, orderProperty, asc, this.pacienteContexto,this.enderecoPacienteContexto);
			}else if(this.enderecoPacienteContexto != null){
				result = this.ambulatorioFacade.obterProntuariosSugeridosNaoVinculados(firstResult, maxResult, orderProperty, asc, this.pacienteContexto,this.enderecoPacienteContexto);
			}
		}
		if (result == null) { 
			result = new ArrayList<PacienteGrupoFamiliarVO>();
		}
		return result;
	}
	
	public AipPacientes getPacienteContexto() {
		return pacienteContexto;
	}

	public void setPacienteContexto(AipPacientes pacienteContexto) {
		this.pacienteContexto = pacienteContexto;
	}
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	public DynamicDataModel<PacienteGrupoFamiliarVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PacienteGrupoFamiliarVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public AipEnderecosPacientes getEnderecoPacienteContexto() {
		return enderecoPacienteContexto;
	}

	public void setEnderecoPacienteContexto(AipEnderecosPacientes enderecoPacienteContexto) {
		this.enderecoPacienteContexto = enderecoPacienteContexto;
	}
}
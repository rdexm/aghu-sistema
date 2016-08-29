package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ProjetosPesquisaPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8764119058026431909L;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;	

	@Inject @Paginator
	private DynamicDataModel<AelProjetoPacientes> dataModel;

	private Integer prontuario;
	private Integer codPaciente;
	private List<AelProjetoPacientes> projetos;	
	private AelProjetoPacientes registroSelecionado;	
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;		
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	
	public void inicio(){		
		if (itemPOL!=null){
			prontuario=itemPOL.getProntuario();
			codPaciente=(Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE);
		}
		dataModel.reiniciarPaginator();				
		registroSelecionado = new AelProjetoPacientes();
	}	
	
	@Override
	public Long recuperarCount() {
		try {
			return prontuarioOnlineFacade.pesquisarProjetosPesquisaPacientePOLCount(codPaciente);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0l;
	}

	@Override
	public List<AelProjetoPacientes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			projetos = prontuarioOnlineFacade.pesquisarProjetosPesquisaPacientePOL(firstResult, maxResult, orderProperty, asc,	codPaciente);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<AelProjetoPacientes>();
		}
		return projetos;		
	}
	
	public void exibirMsgFuncionalidadeNaoImplementada() {
		this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_FUNCIONALIDADE_NAO_IMPLEMENTADA");
	}
	
	// Getters e Setters
	  
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}	

	public List<AelProjetoPacientes> getProjetos() {
		return projetos;
	}

	public void setProjetos(List<AelProjetoPacientes> projetos) {
		this.projetos = projetos;
	}

	public AelProjetoPacientes getRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(AelProjetoPacientes registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}		

	public DynamicDataModel<AelProjetoPacientes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelProjetoPacientes> dataModel) {
	 this.dataModel = dataModel;
	}
}
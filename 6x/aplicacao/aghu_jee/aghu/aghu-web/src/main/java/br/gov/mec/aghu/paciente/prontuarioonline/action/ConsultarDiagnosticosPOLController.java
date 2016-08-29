package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * @author tfelini
 * 
 */

public class ConsultarDiagnosticosPOLController extends ActionController implements ActionPaginator {

	private static final String PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE = "prescricaomedica-manterDiagnosticosPaciente";

	@Inject @Paginator
	private DynamicDataModel<MamDiagnostico> dataModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4934814947870039276L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private AipPacientes paciente;

	@Inject
	@SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;

	private boolean pol = false;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation, true);
		inicio();
	}	
	
	public void inicio() {
		if (itemPOL.getProntuario() != null) {
			this.dataModel.setPesquisaAtiva(true);
			this.paciente = pacienteFacade.pesquisarPacientePorProntuario(this.itemPOL.getProntuario());
			this.dataModel.reiniciarPaginator();
		}
	}

	@Override
	public Long recuperarCount() {
		Long count = 0l;
		try {
			count = this.prontuarioOnlineFacade.pesquisaDiagnosticosCount(this.paciente).longValue();
		} catch (ApplicationBusinessException e) {
			this.dataModel.setPesquisaAtiva(false);
			apresentarExcecaoNegocio(e);
		}

		return count;
	}

	@Override
	public List<MamDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<MamDiagnostico> diagnosticos = new ArrayList<MamDiagnostico>();

		try {
			diagnosticos = this.prontuarioOnlineFacade.pesquisaDiagnosticos(firstResult, maxResult, orderProperty, asc, this.paciente);
		} catch (ApplicationBusinessException e) {
			this.dataModel.setPesquisaAtiva(false);
			apresentarExcecaoNegocio(e);
		}

		if (diagnosticos == null) {
			diagnosticos = new ArrayList<MamDiagnostico>(0);
		}

		return diagnosticos;
	}

	public boolean isPol() {
		return pol;
	}

	public void setPol(boolean pol) {
		this.pol = pol;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean habilitarBotaoDiagnostico() {
		Boolean habilitar = Boolean.FALSE;
		try {
			habilitar = prontuarioOnlineFacade.habilitarBotaoDiagnostico();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return habilitar;
	}

	public String incluirDiagnostico() {
		return PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE;
	}

	public DynamicDataModel<MamDiagnostico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamDiagnostico> dataModel) {
		this.dataModel = dataModel;
	}
}

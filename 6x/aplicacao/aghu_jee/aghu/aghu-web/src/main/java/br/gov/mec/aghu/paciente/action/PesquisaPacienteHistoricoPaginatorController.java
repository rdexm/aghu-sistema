package br.gov.mec.aghu.paciente.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por controlar as ações do listagem de Pacientes no
 * Histórico.
 * 
 * @author david.laks
 */


public class PesquisaPacienteHistoricoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6973360623968415948L;
	private static final Log LOG = LogFactory.getLog(PesquisaPacienteHistoricoPaginatorController.class);
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AipPacientesHist> dataModel;

	/**
	 * Atributo referente ao campo de filtro de nome do Paciente no Histórico na
	 * tela de pesquisa.
	 */
	private String nomePesquisaPacienteHistorico;
	

	@PostConstruct
	public void init(){
		LOG.info("Inicio conversação");
		this.begin(this.conversation);
	}
	
	public void pesquisar() {
		this.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.nomePesquisaPacienteHistorico = null;
		this.dataModel.limparPesquisa();
	}

	// ### Paginação ###

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		Long count = 0L;
		try {
			count = pacienteFacade
					.pesquisaPacientesHistoricoCount(this.nomePesquisaPacienteHistorico);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.dataModel.setPesquisaAtiva(false);
		}

		return count;
	}

	@Override
	public List<AipPacientesHist> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<AipPacientesHist> result = null;

		try {
			result = pacienteFacade.pesquisaPacientesHistorico(
					firstResult, maxResults, orderProperty, asc,
					this.nomePesquisaPacienteHistorico);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.dataModel.setPesquisaAtiva(false);
		}

		if (result == null) {
			result = new ArrayList<AipPacientesHist>();
		}

		return result;
	}

	// ### GETs e SETs ###

	public String getNomePesquisaPacienteHistorico() {
		return nomePesquisaPacienteHistorico;
	}

	public void setNomePesquisaPacienteHistorico(
			String nomePesquisaPacienteHistorico) {
		this.nomePesquisaPacienteHistorico = nomePesquisaPacienteHistorico;
	}

	public DynamicDataModel<AipPacientesHist> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientesHist> dataModel) {
		this.dataModel = dataModel;
	}

}

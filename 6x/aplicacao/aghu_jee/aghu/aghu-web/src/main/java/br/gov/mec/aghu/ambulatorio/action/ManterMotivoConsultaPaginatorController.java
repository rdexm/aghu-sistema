package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Classe responsável por controlar as ações do listagem de Motivo Consulta.
 */
public class ManterMotivoConsultaPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<AacMotivos> dataModel;

	private static final long serialVersionUID = 61628471238451736L;
	
	private static final String MOTIVO_CONSULTA_CRUD = "manterMotivoConsultaCRUD";


	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private Short codigoPesquisaMotivoConsulta;

	private String descricaoPesquisaMotivoConsulta;

	private DominioSituacao situacaoPesquisaMotivoConsulta;

	private AacMotivos motivoSelecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigoPesquisaMotivoConsulta = null;
		this.descricaoPesquisaMotivoConsulta = null;
		this.situacaoPesquisaMotivoConsulta = null;
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return ambulatorioFacade.pesquisaCount(codigoPesquisaMotivoConsulta, descricaoPesquisaMotivoConsulta, situacaoPesquisaMotivoConsulta);
	}

	@Override
	public List<AacMotivos> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return ambulatorioFacade.pesquisa(firstResult, maxResults, orderProperty, asc, codigoPesquisaMotivoConsulta, descricaoPesquisaMotivoConsulta, situacaoPesquisaMotivoConsulta);
	}
	
	public String iniciarInclusao() {
		return MOTIVO_CONSULTA_CRUD;
	}
	
	public String editar(){
		return MOTIVO_CONSULTA_CRUD;
	}

	public Short getCodigoPesquisaMotivoConsulta() {
		return codigoPesquisaMotivoConsulta;
	}

	public void setCodigoPesquisaMotivoConsulta(
			Short codigoPesquisaMotivoConsulta) {
		this.codigoPesquisaMotivoConsulta = codigoPesquisaMotivoConsulta;
	}

	public String getDescricaoPesquisaMotivoConsulta() {
		return descricaoPesquisaMotivoConsulta;
	}

	public void setDescricaoPesquisaMotivoConsulta(
			String descricaoPesquisaMotivoConsulta) {
		this.descricaoPesquisaMotivoConsulta = descricaoPesquisaMotivoConsulta;
	}

	public DominioSituacao getSituacaoPesquisaMotivoConsulta() {
		return situacaoPesquisaMotivoConsulta;
	}

	public void setSituacaoPesquisaMotivoConsulta(
			DominioSituacao situacaoPesquisaMotivoConsulta) {
		this.situacaoPesquisaMotivoConsulta = situacaoPesquisaMotivoConsulta;
	}

	public DynamicDataModel<AacMotivos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacMotivos> dataModel) {
		this.dataModel = dataModel;
	}

	public AacMotivos getMotivoSelecionado() {
		return motivoSelecionado;
	}

	public void setMotivoSelecionado(AacMotivos motivoSelecionado) {
		this.motivoSelecionado = motivoSelecionado;
	}
}

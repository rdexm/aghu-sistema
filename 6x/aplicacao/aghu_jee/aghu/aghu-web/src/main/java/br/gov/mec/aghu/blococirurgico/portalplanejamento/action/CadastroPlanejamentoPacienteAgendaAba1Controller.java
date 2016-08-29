package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class CadastroPlanejamentoPacienteAgendaAba1Controller extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7540475350823389660L;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController principalController;
	
	@Inject
	private SecurityController securityController;
	
	private MbcTipoAnestesias tipoAnestesia;
	private Boolean modificouAnestesia;
	
	private List<MbcAgendaAnestesia> listaAgendaAnestesias;
	private List<MbcAgendaAnestesia> listaRemocao = new ArrayList<MbcAgendaAnestesia>();
	
	private MbcAgendaAnestesia itemSelecionado;
	
	private Boolean readOnlySuggestionAnestesia = false;
	private Boolean renderedColunaAcaoAnestesia = true;
	
	
	//utilizado em suggestionAction da suggestion de tipo de anestesia
	public List<MbcTipoAnestesias> pesquisarTipoAnestesia(String objParam) throws ApplicationBusinessException {
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSB(objParam, null),pesquisarTipoAnestesiaCount(objParam));
	}
	
	public Long pesquisarTipoAnestesiaCount(String objParam) throws ApplicationBusinessException {
		return this.blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSBCount(objParam, null);
	}
	
	public void inicio() {
		listaRemocao = new ArrayList<MbcAgendaAnestesia>();
		setListaAgendaAnestesias(blocoCirurgicoPortalPlanejamentoFacade.listarAgendaAnestesiaPorAgdSeq(getAgenda().getSeq()));
		modificouAnestesia=false;
		
		getValidarRegrasPermissao();
	}
	
	private void getValidarRegrasPermissao() {
		Boolean permissaoExecutar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaAnestesiaExecutar", "cadastroPlanejamentoPacienteAgendaAbaAnestesiaExecutar");
		Boolean permissaoAlterar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaAnestesiaAlterar", "cadastroPlanejamentoPacienteAgendaAbaAnestesiaAlterar");
		readOnlySuggestionAnestesia = !permissaoExecutar && !permissaoAlterar;
		renderedColunaAcaoAnestesia = permissaoExecutar || permissaoAlterar;
	}

	public void removerAgendaAnestesia(MbcAgendaAnestesia agendaAnestesia) {
		modificouAnestesia = true;
		for(int i = 0; i < listaAgendaAnestesias.size(); i++){
			if(agendaAnestesia.getMbcTipoAnestesias().getSeq().equals(listaAgendaAnestesias.get(i).getMbcTipoAnestesias().getSeq())) {
				if(agendaAnestesia.getId() != null) {
					listaRemocao.add(agendaAnestesia);
				}
				getListaAgendaAnestesias().remove(i);
				break;
			}
		}
	}
	
	public void adicionar() {
		
		modificouAnestesia=true;
		
		if(tipoAnestesia != null) {
			MbcAgendaAnestesia agendaAnestesia = new MbcAgendaAnestesia();
			agendaAnestesia.setMbcTipoAnestesias(tipoAnestesia);
			agendaAnestesia.setMbcAgendas(getAgenda());
			try {
				tipoAnestesia = null;
				if(getListaAgendaAnestesias() != null && getListaAgendaAnestesias().size()>0 ){
					blocoCirurgicoPortalPlanejamentoFacade.validarAnestesiaAdicionadaExistente(getListaAgendaAnestesias(), agendaAnestesia);
				}else{
					setListaAgendaAnestesias(new ArrayList<MbcAgendaAnestesia>());
				}
				getListaAgendaAnestesias().add(agendaAnestesia);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	
	public void limparParametros() {
		tipoAnestesia = null;
		listaAgendaAnestesias = null;
		listaRemocao = null;
		itemSelecionado = null;
	}

	public MbcTipoAnestesias getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(MbcTipoAnestesias tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public List<MbcAgendaAnestesia> getListaAgendaAnestesias() {
		return listaAgendaAnestesias;
	}
	
	public void setListaAgendaAnestesias(List<MbcAgendaAnestesia> listaAgendaAnestesias) {
		this.listaAgendaAnestesias = listaAgendaAnestesias;
	}

	public MbcAgendas getAgenda() {
		return principalController.getAgenda();
	}

	public Boolean getModificouAnestesia() {
		return modificouAnestesia;
	}

	public void setModificouAnestesia(Boolean modificouAnestesia) {
		this.modificouAnestesia = modificouAnestesia;
	}

	public List<MbcAgendaAnestesia> getListaRemocao() {
		return listaRemocao;
	}

	public void setListaRemocao(List<MbcAgendaAnestesia> listaRemocao) {
		this.listaRemocao = listaRemocao;
	}

	public Boolean getReadOnlySuggestionAnestesia() {
		return readOnlySuggestionAnestesia;
	}

	public void setReadOnlySuggestionAnestesia(Boolean readOnlySuggestionAnestesia) {
		this.readOnlySuggestionAnestesia = readOnlySuggestionAnestesia;
	}

	public Boolean getRenderedColunaAcaoAnestesia() {
		return renderedColunaAcaoAnestesia;
	}

	public void setRenderedColunaAcaoAnestesia(Boolean renderedColunaAcaoAnestesia) {
		this.renderedColunaAcaoAnestesia = renderedColunaAcaoAnestesia;
	}

	public void setItemSelecionado(MbcAgendaAnestesia itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
	public MbcAgendaAnestesia getItemSelecionado() {
		return this.itemSelecionado;
	}
}

package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.MamLembrete;
import br.gov.mec.aghu.core.action.ActionController;

public class ManterResumoCasoListController extends ActionController{

	private static final long serialVersionUID = 5609277372596617736L;
	
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS_EVOLUCAO = "ambulatorio-atenderPacientesEvolucao";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private Conversation conversation;

	private boolean exibirBotaoIncluirPais = false;
	
	private List<MamLembrete> lista;
	
	private Integer numProntuario;
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		carregarLista();
	}
	
	public List<MamLembrete> carregarLista(){
		this.lista = ambulatorioFacade.obterResumoDeCaso(numProntuario);
		return this.lista;
	}

	public String voltar(){
		return PAGE_PESQUISAR_PACIENTES_AGENDADOS_EVOLUCAO;
	}
	
	public boolean isExibirBotaoIncluirPais() {
		return exibirBotaoIncluirPais;
	}

	public void setExibirBotaoIncluirPais(
			final boolean exibirBotaoIncluirPais) {
		this.exibirBotaoIncluirPais = exibirBotaoIncluirPais;
	}

	public List<MamLembrete> getLista() {
		return lista;
	}

	public void setLista(List<MamLembrete> lista) {
		this.lista = lista;
	}

	public Integer getNumProntuario() {
		return numProntuario;
	}

	public void setNumProntuario(Integer numProntuario) {
		this.numProntuario = numProntuario;
	}	
	
}
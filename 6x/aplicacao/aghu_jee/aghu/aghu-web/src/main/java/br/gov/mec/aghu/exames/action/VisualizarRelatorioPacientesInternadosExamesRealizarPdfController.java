package br.gov.mec.aghu.exames.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;

public class VisualizarRelatorioPacientesInternadosExamesRealizarPdfController extends ActionController{

	private static final long serialVersionUID = -6753859875130043014L;
	
	private static final Integer JEJUM_NPO = 0;
	private static final Integer PREPARO = 1; 
	private static final Integer DIETA_DIFERENCIADA = 2; 
	private static final Integer UNIDADE_EMERGENCIA = 3;	
	private static final Integer TODOS = 4;
	
	private static final String EMITIR_RELACAO_PACIENTES_INTERNADOS_EXAMES_REALIZAR = "exames-emitirRelacaoPacientesInternadosExamesRealizar";

	@Inject
	private RelatorioPacientesInternadosExamesRealizarJejumNpoController relatorioPacientesInternadosExamesRealizarJejumNpoController; 
	
	
	// Referencias responsaveis pela ativacao das abas da interface de visualizacao de relatorios gerados
	private boolean ativarAbaJejumNpo;
	private boolean ativarAbaPreparo;
	private boolean ativarAbaDietaDiferenciada;
	private boolean ativarAbaUnidadeEmergencia;
	private boolean ativarAbaTodos;
	
	private Integer currentTab;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
		setacurrentTabInicial();
	}
	
	/**
	 * Determina qual sera aba inicial selecionada
	 */
	private void setacurrentTabInicial(){
		
		if(ativarAbaJejumNpo){
			this.currentTab = JEJUM_NPO;
			relatorioPacientesInternadosExamesRealizarJejumNpoController.setDieta(false);
			
		} else if(ativarAbaPreparo){
			this.currentTab = PREPARO;
			
		} else if(ativarAbaDietaDiferenciada){
			this.currentTab = DIETA_DIFERENCIADA;
			relatorioPacientesInternadosExamesRealizarJejumNpoController.setDieta(true);
			
		} else if(ativarAbaUnidadeEmergencia){
			this.currentTab = UNIDADE_EMERGENCIA;
			
		} else if(ativarAbaTodos){
			this.currentTab = TODOS;
		} else {
			this.currentTab = JEJUM_NPO;
		}
	}
	
	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void renderAbas() {
		
		if(currentTab == JEJUM_NPO) {
			relatorioPacientesInternadosExamesRealizarJejumNpoController.setDieta(false);
		} else if(currentTab == DIETA_DIFERENCIADA){
			relatorioPacientesInternadosExamesRealizarJejumNpoController.setDieta(true);
		}
		
	}
	
	public String voltar(){
		return EMITIR_RELACAO_PACIENTES_INTERNADOS_EXAMES_REALIZAR;
	}

	public Integer getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(Integer currentTab) {
		this.currentTab = currentTab;
	}

	public boolean isAtivarAbaJejumNpo() {
		return ativarAbaJejumNpo;
	}

	public void setAtivarAbaJejumNpo(boolean ativarAbaJejumNpo) {
		this.ativarAbaJejumNpo = ativarAbaJejumNpo;
	}

	public boolean isAtivarAbaPreparo() {
		return ativarAbaPreparo;
	}

	public void setAtivarAbaPreparo(boolean ativarAbaPreparo) {
		this.ativarAbaPreparo = ativarAbaPreparo;
	}

	public boolean isAtivarAbaDietaDiferenciada() {
		return ativarAbaDietaDiferenciada;
	}

	public void setAtivarAbaDietaDiferenciada(boolean ativarAbaDietaDiferenciada) {
		this.ativarAbaDietaDiferenciada = ativarAbaDietaDiferenciada;
	}

	public boolean isAtivarAbaUnidadeEmergencia() {
		return ativarAbaUnidadeEmergencia;
	}

	public void setAtivarAbaUnidadeEmergencia(boolean ativarAbaUnidadeEmergencia) {
		this.ativarAbaUnidadeEmergencia = ativarAbaUnidadeEmergencia;
	}

	public boolean isAtivarAbaTodos() {
		return ativarAbaTodos;
	}

	public void setAtivarAbaTodos(boolean ativarAbaTodos) {
		this.ativarAbaTodos = ativarAbaTodos;
	}
}
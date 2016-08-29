package br.gov.mec.aghu.transplante.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.core.action.ActionController;

public class ListarTransplantesOrgaoController extends ActionController {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8978802764833889115L;
	private final Integer TAB_1=0;
	private final Integer TAB_2=1;
	private final Integer TAB_3=2;
	private final Integer TAB_4=3;
//	private final Integer TAB_5=4;
	
	
	private static final String REDIRECT_MUDAR_STATUS_PACIENTES_TRANSPLANTE = "transplante-mudarStatusPacienteRins";
	private static final String PAGE_INCLUIR_COMORBIDADE_PACIENTE = "incluirComorbidadePaciente";

	private FiltroTransplanteOrgaoVO filtro = new FiltroTransplanteOrgaoVO();
	private Integer selectedTab;
	private Boolean desabilitarDataTransplante = Boolean.TRUE;
	private Boolean botoesDesabilitados = Boolean.TRUE;
	private PacienteAguardandoTransplanteOrgaoVO itemSelecionado;
	private PacienteTransplantadosOrgaoVO itemSelecionado2; 
	
	@Inject
	private ListarTransplantesOrgaoAba1PaginatorController controllerAba1;
	@Inject
	private ListarTransplantesOrgaoAba4PaginatorController controllerAba4;
	@Inject
	private ExtratoAlteracoesListaOrgaosController extratoAlteracoesListaOrgaosController;
	
	@Inject
	private ListarTransplantesOrgaoAba3PaginatorController controllerAba3;
	@Inject
	private ListarTransplantesOrgaoAba2PaginatorController controllerAba2;
	
	@Inject 
	private IncluirComorbidadePacienteController incluirComorbidadePacienteController;
	
	boolean exibirBotoes;
	private static final String REDIRECT_ULTIMOS_RESULTADOS_EXAMES = "transplante-ultimosResultadosExamesList";
	
	private PacienteTransplantadosOrgaoVO itemSelecionadoPacTransplantado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.selectedTab = TAB_1;
		pesquisar();
	}
	
	public void pesquisar(){
		extratoAlteracoesListaOrgaosController.setExtratoAlteracoesListaOrgaosVO(null);
		controllerAba1.setItemSelecionado(null);
		controllerAba2.setItemSelecionado(null);
		controllerAba3.setItemSelecionado(null);
		controllerAba4.setItemSelecionado(null);
		if(selectedTab.equals(TAB_1)){
			controllerAba1.pesquisar(filtro);
		}else if(selectedTab.equals(TAB_4)){
			controllerAba4.pesquisar(filtro);
		}else if(selectedTab.equals(TAB_3)){
			controllerAba3.pesquisar(filtro);
		}else if(selectedTab.equals(TAB_2)){
			controllerAba2.pesquisar(filtro);
		}
		limparOutrasAbas();
	}
	
	public String carregarTelaIncluirComorbidadePaciente(){
		if(itemSelecionado != null){
			incluirComorbidadePacienteController.setNumProntuario(itemSelecionado.getPacProntuario());
			incluirComorbidadePacienteController.setDominioTipoOrgao(itemSelecionado.getTipoOrgao());
		}else if(itemSelecionadoPacTransplantado != null){
			incluirComorbidadePacienteController.setNumProntuario(itemSelecionadoPacTransplantado.getPacProntuario());
			incluirComorbidadePacienteController.setDominioTipoOrgao(itemSelecionadoPacTransplantado.getTipoOrgao());
		}else{
			return null;
		}
		incluirComorbidadePacienteController.setDominioSituacaoTmo(null);
		incluirComorbidadePacienteController.setVoltarParaOrgaos(true);
		return PAGE_INCLUIR_COMORBIDADE_PACIENTE;
	}

	public void limpar(){
		filtro = new FiltroTransplanteOrgaoVO();
		botoesDesabilitados=true;
		desabilitarDataTransplante = Boolean.TRUE;
		itemSelecionado = null;
		itemSelecionado2 = null;
		if(selectedTab.equals(TAB_1)){
			controllerAba1.getDataModel().limparPesquisa();
			controllerAba1.setItemSelecionado(null);
			pesquisar();
		}else if(selectedTab.equals(TAB_4)){
			controllerAba4.getDataModel4().limparPesquisa();
			controllerAba4.setItemSelecionado(null);
		}else if(selectedTab.equals(TAB_3)){
			controllerAba3.getDataModel().limparPesquisa();
			controllerAba3.setItemSelecionado(null);
			pesquisar();
		}else if(selectedTab.equals(TAB_2)){
			controllerAba2.getDataModel2().limparPesquisa();
			controllerAba2.setItemSelecionado(null);
		}
	}
	
	private void limparOutrasAbas(){
		
		if(selectedTab.equals(TAB_1)){
			desabilitarDataTransplante = Boolean.TRUE; 
			if(controllerAba4.getDataModel4() != null){
				controllerAba4.getDataModel4().limparPesquisa();
			}else if(controllerAba2.getDataModel2() != null){
				controllerAba2.getDataModel2().limparPesquisa();
			}else if(controllerAba3.getDataModel() != null){
				controllerAba3.getDataModel().limparPesquisa();
			}
		}else if(selectedTab.equals(TAB_2)){
			desabilitarDataTransplante = Boolean.FALSE; 
			if(controllerAba1.getDataModel() != null){
				controllerAba1.getDataModel().limparPesquisa();
			}else if(controllerAba4.getDataModel4() != null){
				controllerAba4.getDataModel4().limparPesquisa();
			}else if(controllerAba3.getDataModel() != null){
				controllerAba3.getDataModel().limparPesquisa();
			}
		}else if(selectedTab.equals(TAB_3)){
			desabilitarDataTransplante = Boolean.TRUE; 
			if(controllerAba1.getDataModel() != null){
				controllerAba1.getDataModel().limparPesquisa();
			}else if(controllerAba4.getDataModel4() != null){
				controllerAba4.getDataModel4().limparPesquisa();
			}else if(controllerAba2.getDataModel2() != null){
				controllerAba2.getDataModel2().limparPesquisa();
			}
		}else if(selectedTab.equals(TAB_4)){
			desabilitarDataTransplante = Boolean.TRUE; 
			if(controllerAba1.getDataModel() != null){
				controllerAba1.getDataModel().limparPesquisa();
			}else if(controllerAba2.getDataModel2() != null){
				controllerAba2.getDataModel2().limparPesquisa();
			}else if(controllerAba3.getDataModel() != null){
				controllerAba3.getDataModel().limparPesquisa();
			}
		}
		
	}
	
	
	
	public String botaoCadPaciente(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoCadPaciente();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoCadPaciente();
		}else if(selectedTab.equals(TAB_3)){
			return controllerAba3.botaoCadPaciente();
		}else if(selectedTab.equals(TAB_2)){
			return controllerAba2.botaoCadPaciente();
		}
		return null;
	}
	
	public String botaoExames(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoExames();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoExames();
		}else if(selectedTab.equals(TAB_3)){
			return controllerAba3.botaoExames();
		}else if(selectedTab.equals(TAB_2)){
			return controllerAba2.botaoExames();
		}
		
		return null;
	}
	
	public String botaoConsultas(){
		if (selectedTab == 0){
			return controllerAba1.botaoConsultas();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoConsultas();
		}else if(selectedTab.equals(TAB_3)){
			return controllerAba3.botaoConsultas();
		}else if(selectedTab.equals(TAB_2)){
			return controllerAba2.botaoConsultas();
		}
		return null;
	}
	
	public String botaoLaudoAIH(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoLaudoAIH();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoLaudoAIH();
		}else if(selectedTab.equals(TAB_3)){
			return controllerAba3.botaoLaudoAIH();
		}else if(selectedTab.equals(TAB_2)){
			return controllerAba2.botaoLaudoAIH();
		}
		return null;
	}
	
	public void mudarAba(){
		itemSelecionado = null;
		itemSelecionado2 = null;
		if(selectedTab == TAB_2){
			desabilitarDataTransplante = Boolean.FALSE; 
		}else{
			desabilitarDataTransplante = Boolean.TRUE; 
		}
		pesquisar();
	}
	
	/**#50088 - 
	Para funcionar corretamente Ã© necessÃ¡rio incluir a seguinte linha no xhtml dentro do serverDataTable:
	<p:ajax event="rowSelect" process="@this" listener="#{listarTransplantesController.carregarItemSelecionado()}" update="@(#botoesGridP)"/>
	 **/
	public void carregarItemSelecionado(){
		if(selectedTab == TAB_1){
			itemSelecionado = controllerAba1.getItemSelecionado();
		}else if(selectedTab == TAB_2){
			itemSelecionado2 = controllerAba2.getItemSelecionado();
		}else if(selectedTab == TAB_3){
			itemSelecionado = controllerAba3.getItemSelecionado();
		}else if(selectedTab == TAB_4){
			itemSelecionado = controllerAba4.getItemSelecionado();
		}
	}
	
	public void setNullItemSelecionado(){
		itemSelecionado = null;
		itemSelecionado2 = null;
	}
	
	/**
	 * Botão que faz chamada pra tela de resultados de exames 
	 * @return
	 */
	public String botaoUltimosResultadosExames() {
		return REDIRECT_ULTIMOS_RESULTADOS_EXAMES; 
	}
	
	public String alterarSituacao(){
		return REDIRECT_MUDAR_STATUS_PACIENTES_TRANSPLANTE;
	}
	
	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public FiltroTransplanteOrgaoVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTransplanteOrgaoVO filtro) {
		this.filtro = filtro;
	}

	public PacienteAguardandoTransplanteOrgaoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(PacienteAguardandoTransplanteOrgaoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public PacienteTransplantadosOrgaoVO getItemSelecionadoPacTransplantado() {
		return itemSelecionadoPacTransplantado;
	}

	public void setItemSelecionadoPacTransplantado(PacienteTransplantadosOrgaoVO itemSelecionadoPacTransplantado) {
		this.itemSelecionadoPacTransplantado = itemSelecionadoPacTransplantado;
	}

	public Boolean getDesabilitarDataTransplante() {
		return desabilitarDataTransplante;
	}

	public void setDesabilitarDataTransplante(Boolean desabilitarDataTransplante) {
		this.desabilitarDataTransplante = desabilitarDataTransplante;
	}

	public Boolean getBotoesDesabilitados() {
		return botoesDesabilitados;
	}

	public void setBotoesDesabilitados(Boolean botoesDesabilitados) {
		this.botoesDesabilitados = botoesDesabilitados;
	}

	public PacienteTransplantadosOrgaoVO getItemSelecionado2() {
		return itemSelecionado2;
	}

	public void setItemSelecionado2(PacienteTransplantadosOrgaoVO itemSelecionado2) {
		this.itemSelecionado2 = itemSelecionado2;
	}

}

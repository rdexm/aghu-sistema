package br.gov.mec.aghu.transplante.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.GerarExtratoListaTransplantesVO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ListarTransplantesController extends ActionController {
	
	private static final long serialVersionUID = 1265708805795829989L;
	
	private static final String REDIRECT_MUDAR_STATUS_PACIENTES_TRANSPLANTE = "transplante-mudarStatusPacienteTMO";
	private static final String PAGE_INCLUIR_COMORBIDADE_PACIENTE = "incluirComorbidadePaciente";
	
	private final Integer TAB_1=0;
	private final Integer TAB_2=1;
	private final Integer TAB_3=2;
	private final Integer TAB_4=3;
	private final Integer TAB_5=4;
	
	private ListarTransplantesVO filtro = new ListarTransplantesVO();
	private Boolean ativarCombo = Boolean.FALSE;
	private Boolean ativarDataTransplante = Boolean.FALSE; 
	private Boolean realizouPesquisa = Boolean.FALSE; 
	private Integer selectedTab = TAB_1;
	private Boolean ativarAba1 = Boolean.TRUE;
	
	private List<GerarExtratoListaTransplantesVO>listaExtratoAlteracoes;
	private ListarTransplantesVO itemSelecionado;
	private GerarExtratoListaTransplantesVO selectModal;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@Inject
	private ListarTransplantesAba1PaginatorController controllerAba1;
	
	@Inject
	private ListarTransplantesAba3PaginatorController controllerAba3;
	
	@Inject
	private ListarTransplantesAba2PaginatorController controllerAba2;
	
	@Inject
	private ListarTransplantesAba4PaginatorController controllerAba4;
	
	@Inject
	private ListarTransplantesAba5PaginatorController controllerAba5;
	
	@Inject 
	private IncluirComorbidadePacienteController incluirComorbidadePacienteController;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.selectedTab = TAB_1;
	}
	
	public void changeTipoTransplante(){
		if (filtro.getTransplanteTipoTmo() == DominioSituacaoTmo.G){
			ativarCombo = Boolean.TRUE;
		} else {
			ativarCombo = Boolean.FALSE;
			filtro.setTransplanteTipoAlogenico(null);
		}
	}
	
	public String carregarTelaIncluirComorbidadePaciente(){
		if(itemSelecionado != null){
			incluirComorbidadePacienteController.setNumProntuario(itemSelecionado.getProntuarioPaciente());
			incluirComorbidadePacienteController.setDominioSituacaoTmo(itemSelecionado.getTransplanteTipoTmo());
			incluirComorbidadePacienteController.setDominioTipoOrgao(null);
			incluirComorbidadePacienteController.setVoltarParaOrgaos(false);
			return PAGE_INCLUIR_COMORBIDADE_PACIENTE;
		}
		return null;
	}
	
	public void pesquisar(){
		realizouPesquisa = Boolean.TRUE;
		try {
			transplanteFacade.validarTipoTransplante(filtro);
			if(filtro.getTransplanteTipoTmo().getDescricao().equals(DominioSituacaoTmo.U.getDescricao())){
				ativarAba1 = Boolean.FALSE;
				if(selectedTab == TAB_1){
					selectedTab = TAB_2;
				}
			}else{
				ativarAba1 = Boolean.TRUE;
			}
			controllerAba1.setItemSelecionado1(null);
			controllerAba2.setItemSelecionado2(null);
			controllerAba3.setItemSelecionado(null);
			controllerAba4.setItemSelecionado4(null);
			controllerAba5.setItemSelecionado5(null);
			if (selectedTab.equals(TAB_1)) {
				controllerAba1.pesquisar(filtro);
			} else if (selectedTab.equals(TAB_2)) {
				controllerAba2.pesquisar(filtro);
			} else if (selectedTab.equals(TAB_3)) {
				controllerAba3.pesquisar(filtro);
			} else if (selectedTab.equals(TAB_4)) {
				controllerAba4.pesquisar(filtro);
			} else if (selectedTab.equals(TAB_5)) {
				controllerAba5.pesquisar(filtro);
			}
			limparOutrasAbas();
		}catch (ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
			realizouPesquisa = Boolean.FALSE;
			ativarAba1 = Boolean.TRUE;
			selectedTab = TAB_1;
		}
		
	}
	
	private void limparOutrasAbas(){
		if(selectedTab.equals(TAB_1)){
			limparAbasExcetoAba1();
		}else if(selectedTab.equals(TAB_2)){
			limparAbasExcetoAba2();
		}else if(selectedTab.equals(TAB_3)){
			limparAbasExcetoAba3();
		}else if(selectedTab.equals(TAB_4)){
			limparAbasExcetoAba4();
		}if(selectedTab.equals(TAB_5)){
			limparAbasExcetoAba5();
		}
		
	}

	private void limparAbasExcetoAba5() {
		if(controllerAba1.getDataModel() != null){
			controllerAba1.getDataModel().limparPesquisa();
		}
		if(controllerAba2.getDataModel2() != null){
			controllerAba2.getDataModel2().limparPesquisa();
		}
		if(controllerAba3.getDataModel3() != null){
			controllerAba3.getDataModel3().limparPesquisa();
		}
		if(controllerAba4.getDataModel4() != null){
			controllerAba4.getDataModel4().limparPesquisa();
		}
	}

	private void limparAbasExcetoAba4() {
		if(controllerAba1.getDataModel() != null){
			controllerAba1.getDataModel().limparPesquisa();
		}
		if(controllerAba2.getDataModel2() != null){
			controllerAba2.getDataModel2().limparPesquisa();
		}
		if(controllerAba3.getDataModel3() != null){
			controllerAba3.getDataModel3().limparPesquisa();
		}
		if(controllerAba5.getDataModel5() != null){
			controllerAba5.getDataModel5().limparPesquisa();
		}
	}

	private void limparAbasExcetoAba3() {
		if(controllerAba1.getDataModel() != null){
			controllerAba1.getDataModel().limparPesquisa();
		}
		if(controllerAba4.getDataModel4() != null){
			controllerAba4.getDataModel4().limparPesquisa();
		}
		if(controllerAba2.getDataModel2() != null){
			controllerAba2.getDataModel2().limparPesquisa();
		}
		if(controllerAba5.getDataModel5() != null){
			controllerAba5.getDataModel5().limparPesquisa();
		}
	}

	private void limparAbasExcetoAba2() {
		if(controllerAba1.getDataModel() != null){
			controllerAba1.getDataModel().limparPesquisa();
		} 
		if(controllerAba4.getDataModel4() != null){
			controllerAba4.getDataModel4().limparPesquisa();
		}
		if(controllerAba3.getDataModel3() != null){
			controllerAba3.getDataModel3().limparPesquisa();
		}
		if(controllerAba5.getDataModel5() != null){
			controllerAba5.getDataModel5().limparPesquisa();
		}
	}

	private void limparAbasExcetoAba1() {
		if(controllerAba4.getDataModel4() != null){
			controllerAba4.getDataModel4().limparPesquisa();
		} 
		if(controllerAba2.getDataModel2() != null){
			controllerAba2.getDataModel2().limparPesquisa();
		}
		if(controllerAba3.getDataModel3() != null){
			controllerAba3.getDataModel3().limparPesquisa();
		}
		if(controllerAba5.getDataModel5() != null){
			controllerAba5.getDataModel5().limparPesquisa();
		}
	}
	
	public void limpar(){
		filtro = new ListarTransplantesVO();
		ativarCombo = Boolean.FALSE;
		realizouPesquisa = Boolean.FALSE;
		this.ativarAba1 = Boolean.TRUE;
		itemSelecionado = null;
		listaExtratoAlteracoes = null;
	}
	
	public void mudarAba(){
		if(selectedTab == 2){
			ativarDataTransplante = Boolean.TRUE;
		}else
		{
			ativarDataTransplante = Boolean.FALSE;
		}
		if(realizouPesquisa){
			pesquisar();
		}
		
		itemSelecionado = null;
	}
	
	public String botaoCadPaciente(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoCadPaciente();
		}else if(selectedTab == TAB_2){
			return controllerAba2.botaoCadPaciente();
		}else if(selectedTab == TAB_3){
			return controllerAba3.botaoCadPaciente();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoCadPaciente();
		}else if(selectedTab == TAB_5){
			return controllerAba5.botaoCadPaciente();
		}
		return null;
	}
	
	public String botaoExames(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoExames();
		}else if(selectedTab == TAB_2){
			return controllerAba2.botaoExames();
		}else if(selectedTab == TAB_3){
			return controllerAba3.botaoExames();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoExames();
		}else if(selectedTab == TAB_5){
			return controllerAba5.botaoExames();
		}
		return null;
	}
	
	public String botaoConsultas(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoConsultas();
		}else if(selectedTab == TAB_2){
			return controllerAba2.botaoConsultas();
		}else if(selectedTab == TAB_3){
			return controllerAba3.botaoConsultas();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoConsultas();
		}else if(selectedTab == TAB_5){
			return controllerAba5.botaoConsultas();
		}
		return null;
	}
	
	public String botaoLaudoAIH(){
		if (selectedTab == TAB_1){
			return controllerAba1.botaoLaudoAIH();
		}else if(selectedTab == TAB_2){
			return controllerAba2.botaoLaudoAIH();
		}else if(selectedTab == TAB_3){
			return controllerAba3.botaoLaudoAIH();
		}else if(selectedTab == TAB_4){
			return controllerAba4.botaoLaudoAIH();
		}else if(selectedTab == TAB_5){
			return controllerAba5.botaoLaudoAIH();
		}
		return null;
	}
	
	/**#46771 - Descomentar e editar trechos de acordo com a implementaÃ§Ã£o das abas.
	Para funcionar corretamente Ã© necessÃ¡rio incluir a seguinte linha no xhtml dentro do serverDataTable:
	<p:ajax event="rowSelect" process="@this" listener="#{listarTransplantesController.carregarItemSelecionado()}" update="@(#botoesGridP)"/>
	 **/
	public void carregarItemSelecionado(){
		if(selectedTab == TAB_1){
			itemSelecionado = controllerAba1.getItemSelecionado1();
		}
		
		if(selectedTab == TAB_2){
			itemSelecionado = controllerAba2.getItemSelecionado2();
		}
		
		if(selectedTab == TAB_3){
			itemSelecionado = controllerAba3.getItemSelecionado();
		}
		
		if(selectedTab == TAB_4){
			itemSelecionado = controllerAba4.getItemSelecionado4();
		}
		
		if(selectedTab == TAB_5){
			itemSelecionado = controllerAba5.getItemSelecionado5();
		}
	}
	
	public void iniciarModal(){
		if(itemSelecionado != null && itemSelecionado.getCodigoMtxTransplante() != null){
			listaExtratoAlteracoes = transplanteFacade.consultarListagemExtratoTransplante(itemSelecionado.getCodigoMtxTransplante());
			openDialog("modalGerarExtratoAlteracoesListaTransplantesWG");
		}
	}
	
	public String alterarSituacao(){
		return REDIRECT_MUDAR_STATUS_PACIENTES_TRANSPLANTE;
	}
	
	public Integer obterProntuarioFormatado(String prontuario){
		if(prontuario != null && StringUtils.isNotEmpty(prontuario)){
			return Integer.parseInt(prontuario);
		}else{	
			return null;
		}
	}
	
	public ListarTransplantesVO getFiltro() {
		return filtro;
	}
	
	public void setFiltro(ListarTransplantesVO filtro) {
		this.filtro = filtro;
	}
	
	public Boolean getAtivarCombo() {
		return ativarCombo;
	}

	public void setAtivarCombo(Boolean ativarCombo) {
		this.ativarCombo = ativarCombo;
	}

	public Boolean getAtivarDataTransplante() {
		return ativarDataTransplante;
	}

	public void setAtivarDataTransplante(Boolean ativarDataTransplante) {
		this.ativarDataTransplante = ativarDataTransplante;
	}

	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public List<GerarExtratoListaTransplantesVO> getListaExtratoAlteracoes() {
		return listaExtratoAlteracoes;
	}

	public void setListaExtratoAlteracoes(
			List<GerarExtratoListaTransplantesVO> listaExtratoAlteracoes) {
		this.listaExtratoAlteracoes = listaExtratoAlteracoes;
	}

	public ListarTransplantesVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ListarTransplantesVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public GerarExtratoListaTransplantesVO getSelectModal() {
		return selectModal;
	}

	public void setSelectModal(GerarExtratoListaTransplantesVO selectModal) {
		this.selectModal = selectModal;
	}

	public Boolean getRealizouPesquisa() {
		return realizouPesquisa;
	}

	public void setRealizouPesquisa(Boolean realizouPesquisa) {
		this.realizouPesquisa = realizouPesquisa;
	}
	
	public Boolean getAtivarAba1() {
		return ativarAba1;
	}

	public void setAtivarAba1(Boolean ativarAba1) {
		this.ativarAba1 = ativarAba1;
	}
	
}

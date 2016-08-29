package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class ManterAnamneseEvolucaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 14350067867867L;
	
	//Controle de Navegacao
	private String selectedTab;
	private Integer selectedTabInt;
	private String voltarPara;
	private Integer indexSelecionadoAnamnese;
	private Integer indexSelecionadoEvolucao;
	
	//Parametros das Abas e Cabecalho
	private Integer seqAtendimento;
	private Long seqAnamnese;
	private Long seqEvolucao;
	private boolean incluirNotaAdicionalAnamneses;
	private boolean incluirNotaAdicionalEvolucao;
	private boolean habilitaModalAlteracao;
	
	@Inject
	private CabecalhoAnamneseEvolucaoController cabecalhoAnamneseEvolucaoController;	

	//Controllers Abas
	@Inject
	private ManterAnamneseEvolucaoAbaAnamneseController manterAnamneseEvolucaoAbaAnamneseController;
	
	@Inject
	private ManterAnamneseEvolucaoAbaEvolucaoController manterAnamneseEvolucaoAbaEvolucaoController;
	
	@Inject
	private ManterAnamneseEvolucaoAbaEvolucoesAnterioresController manterAnamneseEvolucaoAbaEvolucoesAnterioresController;
	
	private static final String REDIRECIONA_LISTA_ANAMENSE_EVOLUCAO = "prescricaomedica-listarAnamneseEvolucoes";
	private static final String ABA_1 = "aba1";
	private static final String ABA_2 = "aba2";
	private static final String ABA_3 = "aba3";

	private static final Integer INDEX_SLIDER_NA_EVOLUCAO = 1;
	private static final Integer INDEX_SLIDER_NA_ANAMNESE = 1;

	public void iniciar() {
		if(StringUtils.isEmpty(this.selectedTab)) {
			this.selectedTab = ABA_1;
		}	
		if(incluirNotaAdicionalEvolucao){
			indexSelecionadoEvolucao = INDEX_SLIDER_NA_EVOLUCAO;
		} 
		if(incluirNotaAdicionalAnamneses){
			indexSelecionadoAnamnese = INDEX_SLIDER_NA_ANAMNESE;
		} 
		iniciarCabecalho();	
		iniciarAbaEvolucoesAnteriores();
		iniciarAbaAnamnese();
		iniciarAbaEvolucao();
	}

	private void iniciarCabecalho() {
		this.cabecalhoAnamneseEvolucaoController.setSeqAtendimento(this.seqAtendimento);
		this.cabecalhoAnamneseEvolucaoController.iniciar();
	}

	private void iniciarAbaEvolucoesAnteriores() {
		this.manterAnamneseEvolucaoAbaEvolucoesAnterioresController.setSeqAnamnese(this.seqAnamnese);
	}	
	
	private void iniciarAbaAnamnese() {
		this.manterAnamneseEvolucaoAbaAnamneseController.setSeqAnamnese(this.seqAnamnese);
		this.manterAnamneseEvolucaoAbaAnamneseController.setSeqAtendimento(this.seqAtendimento);
		this.manterAnamneseEvolucaoAbaAnamneseController.iniciar();
	}

	private void iniciarAbaEvolucao() {
		this.manterAnamneseEvolucaoAbaEvolucaoController.setSeqEvolucao(this.seqEvolucao);
		this.manterAnamneseEvolucaoAbaEvolucaoController.iniciar();
	}
	
	public String verificarAltercao() throws ApplicationBusinessException{
		if(manterAnamneseEvolucaoAbaEvolucaoController.verificarAlteracao()){
			openDialog("modalConfirmacaoOperacaoVoltarWG");
			return null;
		}
		if(manterAnamneseEvolucaoAbaAnamneseController.verificarAlteracao()){
			openDialog("modalConfirmacaoOperacaoVoltarWG");
			return null;
		}
		
		return voltarPara();
	}
	
	
	public String voltarPara() throws ApplicationBusinessException{
		if(this.seqAnamnese != null){
			this.manterAnamneseEvolucaoAbaAnamneseController.removerAnamnese();
		}
		
		if(this.seqEvolucao != null){
			this.manterAnamneseEvolucaoAbaEvolucaoController.removerEvolucao();
		}
		limparVariaveis();
		return REDIRECIONA_LISTA_ANAMENSE_EVOLUCAO;
	}
	
	private void limparVariaveis() {
		incluirNotaAdicionalAnamneses = false;
		incluirNotaAdicionalEvolucao = false;
		indexSelecionadoAnamnese = 0;
		indexSelecionadoEvolucao = 0;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public Long getSeqEvolucao() {
		return seqEvolucao;
	}

	public void setSeqEvolucao(Long seqEvolucao) {
		this.seqEvolucao = seqEvolucao;
	}

	public void setIncluirNotaAdicionalEvolucao(boolean incluirNotaAdicionalEvolucao) {
		this.incluirNotaAdicionalEvolucao = incluirNotaAdicionalEvolucao;
	}

	public boolean isIncluirNotaAdicionalEvolucao() {
		return incluirNotaAdicionalEvolucao;
	}

	public void setIncluirNotaAdicionalAnamneses(boolean incluirNotaAdicionalAnamneses) {
		this.incluirNotaAdicionalAnamneses = incluirNotaAdicionalAnamneses;
	}

	public boolean isIncluirNotaAdicionalAnamneses() {
		return incluirNotaAdicionalAnamneses;
	}

	public boolean isHabilitaModalAlteracao() {
		return habilitaModalAlteracao;
	}

	public void setHabilitaModalAlteracao(boolean habilitaModalAlteracao) {
		this.habilitaModalAlteracao = habilitaModalAlteracao;
	}

	public Integer getSelectedTabInt() {
		selectedTabInt = 1;
		if (!StringUtils.isEmpty(this.selectedTab)) {
			if (this.selectedTab.equalsIgnoreCase(ABA_1)) {
				selectedTabInt = 0;
			} else if (this.selectedTab.equalsIgnoreCase(ABA_2)) {
				selectedTabInt = 1;
			} else if (this.selectedTab.equalsIgnoreCase(ABA_3)) {
				selectedTabInt = 2;
			}
		}
		return selectedTabInt;
	}

	public void setSelectedTabInt(Integer selectedTabInt) {
		this.selectedTabInt = selectedTabInt;
	}

	public Integer getIndexSelecionadoAnamnese() {
		return indexSelecionadoAnamnese;
	}

	public void setIndexSelecionadoAnamnese(Integer indexSelecionadoAnamnese) {
		this.indexSelecionadoAnamnese = indexSelecionadoAnamnese;
	}

	public Integer getIndexSelecionadoEvolucao() {
		return indexSelecionadoEvolucao;
	}

	public void setIndexSelecionadoEvolucao(Integer indexSelecionadoEvolucao) {
		this.indexSelecionadoEvolucao = indexSelecionadoEvolucao;
	}

}

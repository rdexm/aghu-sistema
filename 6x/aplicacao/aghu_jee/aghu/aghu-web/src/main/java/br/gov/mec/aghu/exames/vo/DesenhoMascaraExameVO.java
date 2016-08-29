package br.gov.mec.aghu.exames.vo;

import groovy.lang.Script;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;

import org.primefaces.component.outputpanel.OutputPanel;

/**
 * 
 * @HIST DesenhoMascaraExameHistVO
 *
 */
public class DesenhoMascaraExameVO implements Serializable {


	private static final long serialVersionUID = -1610487076899695681L;

	/**
	 * Panel no qual o formulario dinâmico será incluído.
	 */
	private OutputPanel formularioDinamicoPanel;

	/**
	 * Mapa que relaciona um campo do formulários com outros que dependem dele
	 * (nos quais ele precisa fazer reRender).
	 */
	private Map<String, Set<String>> mapaDependentesExpressao = new HashMap<String, Set<String>>();

	/**
	 * Mapa que relaciona um campo do tipo Expressão com sua formula ainda não
	 * 'parseda'.
	 */
	private Map<String,String> mapaFormulas = new HashMap<String, String>();
	
	/**
	 * Mapra que relaciona um campo do tipo Expressão com o script gerado a partir de sua formula.
	 */
	private Map<String,Script> mapaScripts = new HashMap<String, Script>();

	/**
	 * Representa o item da solicitação de exame ao qual a máscara esta relacionada.
	 * Alterado para Object para poder representar AelItemSolicitacaoExames E AelItemSolicExameHist
	 */
	private Object itemSolicitacaoExame;
	
	/**
	 * Representa a descricao do exame
	 */
	private String descricaoExameMaterial;

	/**
	 * Representa o nome do exame da patologia
	 * Exemplo: Anatomopatológico, Imunohistoquímica
	 */
	private String nomeExamePatologia;
	
	/**
	 * Utilizado para controle das quebras de página na geração do pdf de impressão de resultado.
	 * */
	private boolean quebrarPagina= false;
	
	private UIComponent assinaturaEletronica;
	
	private AelpCabecalhoLaudoVO cabecalhoLaudo;
		
	/**
	 * Utilizado para controle dos botões na tela de preenchimento de resultado.
	 * */
	private boolean possuiResultados = false;
	
	private String idade;
	
	private String sexo;

	public OutputPanel getFormularioDinamicoPanel() {
		return formularioDinamicoPanel;
	}

	public void setFormularioDinamicoPanel(OutputPanel formularioDinamicoPanel) {
		this.formularioDinamicoPanel = formularioDinamicoPanel;
	}

	public Map<String, Set<String>> getMapaDependentesExpressao() {
		return mapaDependentesExpressao;
	}

	public void setMapaDependentesExpressao(Map<String, Set<String>> mapaDependentesExpressao) {
		this.mapaDependentesExpressao = mapaDependentesExpressao;
	}

	public Map<String, String> getMapaFormulas() {
		return mapaFormulas;
	}

	public void setMapaFormulas(Map<String, String> mapaFormulas) {
		this.mapaFormulas = mapaFormulas;
	}

	public Map<String, Script> getMapaScripts() {
		return mapaScripts;
	}

	public void setMapaScripts(Map<String, Script> mapaScripts) {
		this.mapaScripts = mapaScripts;
	}

	public Object getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(Object itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}

	public String getDescricaoExameMaterial() {
		return descricaoExameMaterial;
	}

	public void setDescricaoExameMaterial(String descricaoExameMaterial) {
		this.descricaoExameMaterial = descricaoExameMaterial;
	}

	public boolean isQuebrarPagina() {
		return quebrarPagina;
	}

	public void setQuebrarPagina(boolean quebrarPagina) {
		this.quebrarPagina = quebrarPagina;
	}

	public boolean isPossuiResultados() {
		return possuiResultados;
	}

	public void setPossuiResultados(boolean possuiResultados) {
		this.possuiResultados = possuiResultados;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public AelpCabecalhoLaudoVO getCabecalhoLaudo() {
		return cabecalhoLaudo;
	}

	public void setCabecalhoLaudo(AelpCabecalhoLaudoVO cabecalhoLaudo) {
		this.cabecalhoLaudo = cabecalhoLaudo;
	}

	public UIComponent getAssinaturaEletronica() {
		return assinaturaEletronica;
	}

	public void setAssinaturaEletronica(UIComponent assinaturaEletronica) {
		this.assinaturaEletronica = assinaturaEletronica;
	}

	public String getNomeExamePatologia() {
		return nomeExamePatologia;
	}

	public void setNomeExamePatologia(String nomeExamePatologia) {
		this.nomeExamePatologia = nomeExamePatologia;
	}
	
}
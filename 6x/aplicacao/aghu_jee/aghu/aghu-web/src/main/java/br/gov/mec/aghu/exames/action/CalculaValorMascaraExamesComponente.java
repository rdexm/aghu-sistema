package br.gov.mec.aghu.exames.action;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import groovy.util.Eval;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.component.outputpanel.OutputPanel;

import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.exames.vo.VariaveisValorFormulaMascaraExameVO;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;

public class CalculaValorMascaraExamesComponente implements Serializable{
	
	private static final long serialVersionUID = -8418009158047727214L;
	
	private static final Log LOG = LogFactory.getLog(CalculaValorMascaraExamesComponente.class);
	
	private List<DesenhoMascaraExameVO> desenhosMascarasExamesVO = null;
	/**
	 * Constante para o nome do atributo de cada componente que armazenará a
	 * instância de AelParametroCamposLaudo correspondente.
	 */
	private static final String NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO = "PARAMETRO_CAMPO_LAUDO";
	
	/**
	 * Constante para o nome do atributo que representa o identificador, que
	 * seria o atributo nome de AelCampoLaudo concatenado com o sequencial de
	 * AelParametrosCampoLaudo. Usado principalmente nas formulas de campo do
	 * tipo expressão.
	 */
	private static final String NOME_ATRIBUTO_IDENTIFICADOR = "IDENTIFICADOR";
	
	/**
	 * Informa se a execução do calculo do valor é para prévia
	 * */
	private Boolean previa = Boolean.FALSE; 
	
	/** Idade usada na prévia da mascara de laudo*/
	private static final Integer IDADE_PREVIA = 50;

	/** Sexo usado na prévia da mascara de laudo*/
	private static final String SEXO = "M"; 
	
	private static final String OPER_IDADE = "IDADE";
	private static final String OPER_SEXO = "SEXO";

	/**
	 * Calcula o valor de um campo do tipo expressão.
	 * 
	 * @param formula
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object calcularValor(String idCampo) throws MissingPropertyException {
		Object retorno = null;
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		if (desenhosMascarasExamesVO != null) {
			for (DesenhoMascaraExameVO desenhoMascaraExameVO : desenhosMascarasExamesVO) {
				OutputPanel formularioDinamicoPanel = desenhoMascaraExameVO.getFormularioDinamicoPanel();
	
				UIInput input = (UIInput) this.encontrarComponentePorId(idCampo, formularioDinamicoPanel.getChildren());
	
				if (input != null) {
					Script script = desenhoMascaraExameVO.getMapaScripts().get(idCampo);
					
					if(script == null){
						
						String formula = desenhoMascaraExameVO.getMapaFormulas().get(idCampo);
						if(formula!=null){
							
							if(possuiOperadores(formula)){
								StringBuilder formulasb = new StringBuilder("import static br.gov.mec.aghu.core.persistence.dialect.FuncoesOracle.*; ");
								formulasb.append(formula);
								retorno = Eval.me(formulasb.toString());
							}else{
								retorno = formula;
							}
						}else{
							retorno = input.getValue();
						}
					}else{
						Binding binding = script.getBinding();
		
						Set<String> operandos = binding.getVariables().keySet();
						boolean flagVariaveisIncompletas = false;
						if (operandos != null && !operandos.isEmpty()) {
							UIInput _input = null;
							for (String operando : operandos) {
								Object valor = null;
								_input = (UIInput) this.encontrarComponentePorId(operando, formularioDinamicoPanel.getChildren());
								
								valor = request.getParameter(_input.getId());
								//if((valor==null || valor.toString().isEmpty()) && previa){
								if(valor==null){
									valor = _input.getValue();
								}
	
								if(valor!= null && !StringUtils.isBlank(valor.toString())){
									if(StringUtils.isNumeric(valor.toString())){
										valor = Double.parseDouble(valor.toString());
	
									}else if(valor.toString().indexOf('.')>-1 && valor.toString().indexOf(",")>-1){
										valor = valor.toString().replace(".", "");
										valor = valor.toString().replace(",", ".");
										valor = Double.parseDouble(valor.toString());
										
									}else if(valor.toString().indexOf(',')>-1){
										valor = valor.toString().replace(",", ".");
										valor = Double.parseDouble(valor.toString());
										
									}else if(valor.toString().indexOf('.')>-1){
										valor = Double.parseDouble(valor.toString());
									}
								}
	
								if (valor==null || StringUtils.isBlank(valor.toString())) {
									flagVariaveisIncompletas = true;
									break;
								}
								binding.setVariable(operando, valor);
							}
						}
	
						if (!flagVariaveisIncompletas) {
							try {
								retorno = script.run();
							} catch(MissingMethodException e) {
								LOG.error(e.getMessage(), e);
								retorno = 	null;
							}
						}
					}

					if (retorno instanceof Number) {
						if(Double.isInfinite(Double.valueOf(retorno.toString()))) {
							retorno = null;
						} else {
							AelParametroCamposLaudo parametroCampoLaudo = (AelParametroCamposLaudo) input.getAttributes().get(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO);
							NumberFormat format = NumberFormat.getInstance(new Locale("pt","BR"));
							format.setMaximumFractionDigits(parametroCampoLaudo.getQuantidadeCasasDecimais());
							retorno = format.format(retorno);
						}
					}
					break;
				}
			}
		}

		return retorno;
	}
	
	private boolean possuiOperadores(final String formula){
		String[] operadores = {"POWER","SQRT","DECODE","GREATEST","SIGN","+","-","*","/"};
		for (String operador : operadores) {
			if(formula.toUpperCase().contains(operador)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Procura recursivamente um componente por id
	 * 
	 * @param id
	 * @param lista
	 * @return
	 */
	private UIComponent encontrarComponentePorId(String id, List<UIComponent> lista) {
		UIComponent retorno = null;
		if (lista != null && !lista.isEmpty()) {
			for (UIComponent filho : lista) {
				if (id.equals(filho.getId())) {
					retorno = filho;
				} else {
					retorno = this.encontrarComponentePorId(id, filho.getChildren());
				}
				if (retorno != null) {
					break;
				}
			}
		}
		return retorno;
	}
	
	public List<VariaveisValorFormulaMascaraExameVO> obterListaVariaveis(String formula){

		Set<VariaveisValorFormulaMascaraExameVO> resultadosMap = new HashSet<VariaveisValorFormulaMascaraExameVO>();

		for (DesenhoMascaraExameVO desenhoMascaraExameVO : desenhosMascarasExamesVO) {
			OutputPanel formularioDinamicoPanel = desenhoMascaraExameVO.getFormularioDinamicoPanel();

			/*os operandos*/
			String[] operandos = StringUtils.substringsBetween(formula, "[", "]");

			if (operandos != null) {
				UIInput input = null;
				for (String operando : operandos) {
					if(operando.equals(OPER_IDADE)){
						resultadosMap.add(new VariaveisValorFormulaMascaraExameVO(OPER_IDADE, BigDecimal.valueOf(IDADE_PREVIA), 4, (short) 0));
						desenhoMascaraExameVO.setIdade(IDADE_PREVIA.toString());
					}else if(operando.equals(OPER_SEXO)){
						resultadosMap.add(new VariaveisValorFormulaMascaraExameVO(OPER_SEXO, SEXO, 1, (short) 0));
						desenhoMascaraExameVO.setSexo(SEXO);
					}else{
						input = (UIInput) this.encontrarComponentePorAtributoIdentificador(operando, formularioDinamicoPanel.getChildren());
						UIInput _input = (UIInput)encontrarComponentePorId(input.getId(), formularioDinamicoPanel.getChildren());
						AelParametroCamposLaudo parametroCampoLaudo = (AelParametroCamposLaudo) input.getAttributes().get(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO);
						
						Integer qtdeCaracteres = parametroCampoLaudo.getQuantidadeCaracteres()+1;
						Short qtdeCasasDecimais = parametroCampoLaudo.getQuantidadeCasasDecimais();
						
						if(_input !=  null && _input.getValue() != null){
							resultadosMap.add(new VariaveisValorFormulaMascaraExameVO(operando, BigDecimal.valueOf(((Number) _input.getValue()).longValue()), qtdeCaracteres, qtdeCasasDecimais));
						}
					}
				}
			}
		}
		return new ArrayList<VariaveisValorFormulaMascaraExameVO>(resultadosMap);
	}
	
	public void atualizarValorVariavel(String idCampo, String operando, String novoValor) {
		for (DesenhoMascaraExameVO desenhoMascaraExameVO : desenhosMascarasExamesVO) {
			OutputPanel formularioDinamicoPanel = desenhoMascaraExameVO.getFormularioDinamicoPanel();

			if (operando.equals(OPER_IDADE) || operando.equals(OPER_SEXO)) {
				UIInput input = (UIInput) this.encontrarComponentePorId(idCampo, formularioDinamicoPanel.getChildren());
				AelParametroCamposLaudo campo = (AelParametroCamposLaudo) input.getAttributes().get(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO);
				
				String formula = comandosToLowerCase(campo.getTextoLivre());
				String[] operandos = StringUtils.substringsBetween(formula, "[", "]");
				boolean dependeCamposMascara = false;
				Binding binding = new Binding();

				if (operandos != null) {
					for (String oper : operandos) {
						if (oper.equals(operando)) {
							formula = StringUtils.replace(formula, "[" + oper + "]", novoValor);
							this.atualizaValorIdadeSexo(desenhoMascaraExameVO, operando, novoValor);
							
						} else if (oper.equals(OPER_IDADE) || oper.equals(OPER_SEXO)) {
							String valor = this.obterValorOperando(desenhoMascaraExameVO, oper);
							formula = StringUtils.replace(formula, "[" + oper + "]", valor);
							
						} else {
							dependeCamposMascara = true;
							input = (UIInput) this.encontrarComponentePorAtributoIdentificador(oper, formularioDinamicoPanel.getChildren());
							String id = input.getId();
							formula = StringUtils.replace(formula, "[" + oper + "]", id);
							binding.setVariable(id, null);
						}
					}
					desenhoMascaraExameVO.getMapaFormulas().remove(idCampo);
					desenhoMascaraExameVO.getMapaFormulas().put(campo.getId().toString(), formula);
				}

				if (!dependeCamposMascara) {
					if (possuiOperadores(formula)) {
						StringBuilder formulasb = new StringBuilder(
								"import static br.gov.mec.aghu.core.persistence.dialect.FuncoesOracle.*; ");
						formulasb.append(formula);
						input.setValue(Eval.me(formulasb.toString()));
					} else {
						input.setValue(formula);
					}
				} else {
					GroovyShell shell = new GroovyShell(binding);
					StringBuffer formulasb = new StringBuffer("import static br.gov.mec.aghu.core.persistence.dialect.FuncoesOracle.*; ");
					formulasb.append(formula);
					Script script = shell.parse(formulasb.toString());
					
					desenhoMascaraExameVO.getMapaScripts().remove(idCampo);
					desenhoMascaraExameVO.getMapaScripts().put(idCampo, script);
				}
				
			} else {
				UIInput input = (UIInput) this.encontrarComponentePorAtributoIdentificador(operando, formularioDinamicoPanel.getChildren());
				UIInput _input = (UIInput)encontrarComponentePorId(input.getId(), formularioDinamicoPanel.getChildren());
				_input.setValue(novoValor);
			}
		}
	}
	
	private String comandosToLowerCase(String formula) {
		if (formula != null) {
			return formula.replace("POWER", "power").replace("SQRT", "sqrt").replace("DECODE", "decode").replace("GREATEST", "greatest")
					.replace("SIGN", "sign");
		}
		return null;
	}
	
	private void atualizaValorIdadeSexo(DesenhoMascaraExameVO desenhoMascaraExameVO, String operando, String novoValor) {
		if (operando.equals(OPER_IDADE)) {
			desenhoMascaraExameVO.setIdade(novoValor);
			
		} else {
			desenhoMascaraExameVO.setSexo(novoValor);
		}
	}
	
	private String obterValorOperando(DesenhoMascaraExameVO desenhoMascaraExameVO, String operando) {
		if (operando.equals(OPER_IDADE)) {
			return desenhoMascaraExameVO.getIdade();
			
		} else {
			return desenhoMascaraExameVO.getSexo();
		}
	}

	private UIComponent encontrarComponentePorAtributoIdentificador(String identificador, List<UIComponent> lista) {
		UIComponent retorno = null;
		if (lista != null && !lista.isEmpty()) {
			for (UIComponent filho : lista) {
				if (identificador.equals(filho.getAttributes().get(NOME_ATRIBUTO_IDENTIFICADOR)  )) {
					retorno = filho;
				} else {
					retorno = this.encontrarComponentePorAtributoIdentificador(identificador,
							filho.getChildren());
				}
				if (retorno != null) {
					break;
				}
			}
		}
		return retorno;

	}

	public List<DesenhoMascaraExameVO> getDesenhosMascarasExamesVO() {
		return desenhosMascarasExamesVO;
	}

	public void setDesenhosMascarasExamesVO(
			List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		this.desenhosMascarasExamesVO = desenhosMascarasExamesVO;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

}
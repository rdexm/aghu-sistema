package br.gov.mec.aghu.exames.cadastrosapoio.business;

import groovy.util.Eval;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioAlinhamentoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioOpcoesFormulaParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudo;

@Stateless
public class AelParametroCamposLaudoON extends BaseBusiness {


private static final String _CEM_ = "100";

@EJB
private AelParametroCamposLaudoRN aelParametroCamposLaudoRN;

private static final Log LOG = LogFactory.getLog(AelParametroCamposLaudoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;
	
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	private static final long serialVersionUID = 6861588587927494361L;
	
	/** Idade usada na prévia da mascara de laudo*/
	private static final Integer IDADE_PREVIA = 50;
	
	/** Sexo usado na prévia da mascara de laudo*/
	private static final String SEXO = "M";

	private static final String STYLE = "style";

	private static final String COR_DEFAULT = "clWindowText";
	
	public enum AelParametroCamposLaudoONExceptionCode implements	BusinessExceptionCode {
		MSG_EXCESSAO_EXPRESSAO_PARENTESES,//
		MSG_EXCESSAO_EXPRESSAO_COLCHETES,
		MSG_EXCESSAO_EXPRESSAO;
	}
	
	/**
	 * Remover {@link AelParametroCamposLaudoRN}
	 * 
	 * @param campoLaudo
	 * @throws BaseException
	 */
	public void remover(AelParametroCamposLaudo campoLaudo) throws BaseException {
		this.getAelParametroCamposLaudoRN().remover(campoLaudo);
	}

	
	public void persistir(final List<AelParametroCamposLaudo> aelParametroCamposLaudos) throws BaseException {
		persistir(aelParametroCamposLaudos, false);
	}
	
	public void persistir(final List<AelParametroCamposLaudo> aelParametroCamposLaudos, final boolean novaVersao) throws BaseException {
		for (AelParametroCamposLaudo parametro : aelParametroCamposLaudos){
			salvaObjeto(parametro, novaVersao);
		}
	}
	
	public void persistir(final AelParametroCamposLaudo aelParametroCamposLaudos) throws BaseException{
		persistir(aelParametroCamposLaudos, false);
	}
	
	public void persistir(final AelParametroCamposLaudo aelParametroCamposLaudos, final boolean novaVersao) throws BaseException{
		salvaObjeto(aelParametroCamposLaudos, novaVersao);
	}
	
	private void salvaObjeto(AelParametroCamposLaudo parametro, boolean novaVersao)throws BaseException{
		/*Valida se o Campo Laudo de cada campo foi informado*/ 
		getAelParametroCamposLaudoRN().validaCampoLaudo(parametro);
		defineEstiloFonte(parametro);
		 /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
		if ((parametro.getId()==null || parametro.getId().getSeqp()==null) || novaVersao){
			defineParametroCampoLaudoId(parametro);
			defineValoresDefaults(parametro);
			getAelParametroCamposLaudoRN().inserir(parametro);
		}else{
			defineValoresDefaults(parametro);
			getAelParametroCamposLaudoRN().atualizar(parametro);
		}
	}
	
	
	private void defineValoresDefaults(AelParametroCamposLaudo parametroCampoLaudo){
		AelExamesMaterialAnalise exameMaterialAnalise =	this.getAelExamesMaterialAnaliseDAO().buscarAelExamesMaterialAnalisePorId(parametroCampoLaudo.getId().getVelEmaExaSigla(), parametroCampoLaudo.getId().getVelEmaManSeq());

		if(DominioSumarioExame.S.equals(exameMaterialAnalise.getPertenceSumario())){
			parametroCampoLaudo.setSumarioSemMascara(Boolean.TRUE);
		}else{
			parametroCampoLaudo.setSumarioSemMascara(Boolean.FALSE);
		}

		if(parametroCampoLaudo.getFonte() == null){
			parametroCampoLaudo.setFonte("Courier New");
		}

		if(parametroCampoLaudo.getItalico() == null){
			parametroCampoLaudo.setItalico(false);
		}

		if(parametroCampoLaudo.getRiscado() == null){
			parametroCampoLaudo.setRiscado(false);
		}

		if(parametroCampoLaudo.getNegrito() == null){
			parametroCampoLaudo.setNegrito(false);
		}

		if(parametroCampoLaudo.getSublinhado() == null){
			parametroCampoLaudo.setSublinhado(false);
		}

		if(parametroCampoLaudo.getTamanhoFonte() == null){
			parametroCampoLaudo.setTamanhoFonte((short)8);
		}

		if (parametroCampoLaudo.getLarguraObjetoVisual() == null) {
			parametroCampoLaudo.setLarguraObjetoVisual((short)30);
		}
		
		if (parametroCampoLaudo.getAlturaObjetoVisual() == null) {
			parametroCampoLaudo.setAlturaObjetoVisual((short)20);
		}
		
		parametroCampoLaudo.setPosicaoColunaImpressao(parametroCampoLaudo.getPosicaoColunaTela());

		parametroCampoLaudo.setPosicaoLinhaImpressao(parametroCampoLaudo.getPosicaoLinhaTela());

		defineValoresDefaultCasasECaracteres(parametroCampoLaudo);
	}
	
	public void montaFormulaParametroCampoLaudo(AelParametroCamposLaudo parametroCampoLaudo, AelParametroCamposLaudo parametroCampoLaudoAdicionado, DominioOpcoesFormulaParametroCamposLaudo opcao){
	
		if(parametroCampoLaudoAdicionado == null){
			switch (opcao) {
			case IDADE:
				adicionaOpcao(parametroCampoLaudo, " [IDADE]");
				break;
			case SEXO:
				adicionaOpcao(parametroCampoLaudo, " [SEXO]");
				break;
			case POWER:
				adicionaOpcao(parametroCampoLaudo, " POWER(,)");
				break;
			case RAIZ:
				adicionaOpcao(parametroCampoLaudo, " SQRT()");
				break;
			case DECODE:
				adicionaOpcao(parametroCampoLaudo, " DECODE(,,)");
				break;
			case GREATEST:
				adicionaOpcao(parametroCampoLaudo, " GREATEST(,,)");
				break;
			case SIGN:
				adicionaOpcao(parametroCampoLaudo, " SIGN( )");
				break;
			case ADICAO:
				adicionaOpcao(parametroCampoLaudo, " + ");
				break;
			case SUBTRACAO:
				adicionaOpcao(parametroCampoLaudo, " - ");
				break;
			case MULTIPLICACAO:
				adicionaOpcao(parametroCampoLaudo, " * ");
				break;
			case DIVISAO:
				adicionaOpcao(parametroCampoLaudo, " / ");
				break;
			case ABRE_PARENTESES:
				adicionaOpcao(parametroCampoLaudo, " ( ");
				break;
			case FECHA_PARENTESES:
				adicionaOpcao(parametroCampoLaudo, " ) ");
				break;
			default:
				adicionaOpcao(parametroCampoLaudo, "");
				break;
			}
		}else{
			if(parametroCampoLaudoAdicionado != null && parametroCampoLaudoAdicionado.getCampoLaudo() != null){
				adicionaOpcao(parametroCampoLaudo, " [" + parametroCampoLaudoAdicionado.getCampoLaudo().getNome()+parametroCampoLaudoAdicionado.getId().getSeqp()+"]");
			}else{
				adicionaOpcao(parametroCampoLaudo, " [Defina o Campo Laudo para '"+parametroCampoLaudoAdicionado.getObjetoVisual().getDescricao()+"' ]");
			}
		}
	}
	
	private void adicionaOpcao(AelParametroCamposLaudo parametroCampoLaudo, String novaOpcao){
		if(parametroCampoLaudo.getTextoLivre() != null){
			parametroCampoLaudo.setTextoLivre((parametroCampoLaudo.getTextoLivre() + novaOpcao).trim());
		}else{
			parametroCampoLaudo.setTextoLivre(novaOpcao.trim());
		}
	}
	
	
	public AelParametroCamposLaudo inserirNovoCampoTela(AelVersaoLaudo versaoLaudo, DominioObjetoVisual objetoVisual){
		AelParametroCamposLaudo novo = new AelParametroCamposLaudo();

		//--[DEFAULTS]
		novo.setObjetoVisual(objetoVisual);
		novo.setAelVersaoLaudo(versaoLaudo);

		/*
		 * VALORES DEFAULT DE COR DE FONTE E ALINHAMENTO
		 */
		novo.setAlinhamento(DominioAlinhamentoParametroCamposLaudo.E);
		novo.setCor(COR_DEFAULT);
		
		if (DominioObjetoVisual.TEXTO_FIXO.equals(novo.getObjetoVisual())){
			novo.setTamanhoFonte(Short.valueOf("12"));
			novo.setTextoLivre("Texto Fixo");
			
		}else if (DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf("68"));
			novo.setAlturaObjetoVisual(Short.valueOf("16"));			
			novo.setTextoLivre("Texto Alfanumérico");

		}else if (DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf("120"));
			novo.setAlturaObjetoVisual(Short.valueOf("16"));
			novo.setQuantidadeCasasDecimais((short)0);
			//novo.setTextoLivre("Numérico/Expressão");

		}else if (DominioObjetoVisual.TEXTO_LONGO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf("450"));
			novo.setAlturaObjetoVisual(Short.valueOf(_CEM_));
			novo.setQuantidadeCaracteres(4000);
			novo.setTextoLivre("Texto Longo");
			
		}else if (DominioObjetoVisual.TEXTO_CODIFICADO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf("68"));
			novo.setAlturaObjetoVisual(Short.valueOf("16"));			
			novo.setTextoLivre("Codificado");

		}else if (DominioObjetoVisual.EQUIPAMENTO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf(_CEM_));
			novo.setAlturaObjetoVisual(Short.valueOf("14"));
			novo.setTextoLivre("Equipamento");
			
		}else if (DominioObjetoVisual.METODO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf(_CEM_));
			novo.setAlturaObjetoVisual(Short.valueOf("14"));
			novo.setTextoLivre("Método");
			
		}else if (DominioObjetoVisual.RECEBIMENTO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf(_CEM_));
			novo.setAlturaObjetoVisual(Short.valueOf("14"));
			novo.setTextoLivre("Recebimento");
			
		}else if (DominioObjetoVisual.HISTORICO.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf(_CEM_));
			novo.setAlturaObjetoVisual(Short.valueOf("14"));
			novo.setTextoLivre("Histórico");
			
		}else if (DominioObjetoVisual.VALORES_REFERENCIA.equals(novo.getObjetoVisual())){
			novo.setLarguraObjetoVisual(Short.valueOf(_CEM_));
			novo.setAlturaObjetoVisual(Short.valueOf("14"));
			novo.setTextoLivre("Valores de Referência");
		}else{
			novo.setLarguraObjetoVisual(Short.valueOf("68"));
			novo.setAlturaObjetoVisual(Short.valueOf("16"));
		}
		

		if (novo.getCampoLaudo()==null && !DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(novo.getObjetoVisual())){
			DominioTipoCampoCampoLaudo tipoLaudo = DominioTipoCampoCampoLaudo.valueOf(novo.getObjetoVisual().getSiglaLaudo());
			if (getAelCampoLaudoDAO().countAelCampoLaudoTipo(tipoLaudo)==1){
				List<AelCampoLaudo> campoLaudoList = getAelCampoLaudoDAO().pesquisarAelCampoLaudoTipo(tipoLaudo, null);		
				AelCampoLaudo campoLaudo = campoLaudoList.get(0);
				campoLaudo.getTipoCampo();
				novo.setCampoLaudo(campoLaudo);
			}
		}
		return novo;
		
	}
	
	private void defineEstiloFonte(AelParametroCamposLaudo parametroCampoLaudo){
		/*
		if(	parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_FIXO)
			|| parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_ALFANUMERICO)
			|| parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO)
			|| parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_LONGO)
			|| parametroCampoLaudo.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_CODIFICADO)){
			*/

			if(parametroCampoLaudo.getTextoLivre() != null){
				try{
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();

					parametroCampoLaudo.setTextoLivre(parametroCampoLaudo.getTextoLivre().replace("<br>", ""));
					String textoLivre = ("<root>"+parametroCampoLaudo.getTextoLivre().trim()+"</root>").replaceAll("&", "#");

				    ByteArrayInputStream encXML = new  ByteArrayInputStream(textoLivre.getBytes("UTF-8"));

				    Document doc = db.parse(encXML);

				    
//				    
//					if(doc.getElementsByTagName("strike").getLength() > 0){
//						parametroCampoLaudo.setRiscado(Boolean.TRUE);
//					}else{
//						parametroCampoLaudo.setRiscado(Boolean.FALSE);
//					}
//
//					if(doc.getElementsByTagName("b").getLength() > 0 || doc.getElementsByTagName("strong").getLength() > 0){
//						parametroCampoLaudo.setNegrito(Boolean.TRUE);
//					}else{
//						parametroCampoLaudo.setNegrito(Boolean.FALSE);
//					}
//
//					if(doc.getElementsByTagName("u").getLength() > 0){
//						parametroCampoLaudo.setSublinhado(Boolean.TRUE);
//					}else{
//						parametroCampoLaudo.setSublinhado(Boolean.FALSE);
//					}
//
//					if(doc.getElementsByTagName("i").getLength() > 0 || doc.getElementsByTagName("em").getLength() > 0){
//						parametroCampoLaudo.setItalico(Boolean.TRUE);
//					}else{
//						parametroCampoLaudo.setItalico(Boolean.FALSE);
//					}
	
					/*Informações da fonte (AGHU)*/
					obterInformacoesFonte(doc, parametroCampoLaudo);
	
					/*Informações do AGHUSE (que usa CSS ao inves de tags html) */
					obterInformacoesAghuse(doc, parametroCampoLaudo);
					obterInformacoesAlinhamentoAghuse(doc, parametroCampoLaudo);
					
					/*Alinhamento do componente*/
					if(doc.getElementsByTagName("p").getLength()>0){
						String alinhamento = ((Element)doc.getElementsByTagName("p").item(0)).getAttribute("align");
						parametroCampoLaudo.setAlinhamento(DominioAlinhamentoParametroCamposLaudo.getInstance(alinhamento));
					}else{
						parametroCampoLaudo.setAlinhamento(DominioAlinhamentoParametroCamposLaudo.E);
					}
				}catch (ParserConfigurationException e) {
					LOG.error("Erro ao parsear HTML", e); 
				}catch (SAXException e) {
					LOG.error("Erro ao parsear HTML", e);
				}catch (IOException e) {
					LOG.error("Erro ao parsear HTML", e);
				}
			}
		//}
	}
	
	private String obterValorParametroCss(String style) {
		return style.substring(style.indexOf(':') + 1, style.indexOf(';')).trim();
	}
	
	private void obterInformacoesAlinhamentoAghuse(Document doc, AelParametroCamposLaudo parametroCampoLaudo) {
		NodeList divElement = doc.getElementsByTagName("div");
		if(divElement !=  null && divElement.getLength() > 0){
						
			for (int i = 0; i < divElement.getLength(); i++) {
				Element element = (Element) divElement.item(i);
				
				String style = null;
				
				if(!element.getAttribute(STYLE).isEmpty()){
					style = element.getAttribute(STYLE);
				}
				
				if (style != null) {
					if (style.indexOf("text-align:") >= 0) {
						String textAlign = obterValorParametroCss(style);
						parametroCampoLaudo.setAlinhamento(DominioAlinhamentoParametroCamposLaudo.getInstance(textAlign));
					}
				}							
			}
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void obterInformacoesAghuse(Document doc, AelParametroCamposLaudo parametroCampoLaudo) {
		NodeList spanElement = doc.getElementsByTagName("span");
		if(spanElement !=  null && spanElement.getLength() > 0){
			String cor = null;
			String face = null;
			Boolean negrito = false;
			Boolean italico = false;
			Boolean sublinhado = false;
			Boolean subscrito = false;
						
			for (int i = 0; i < spanElement.getLength(); i++) {
				Element element = (Element) spanElement.item(i);
				
				String style = null;
				
				if(!element.getAttribute(STYLE).isEmpty()){
					style = element.getAttribute(STYLE);
				}
				
				if (style != null) {
					String[] conteudo = StringUtils.substringsBetween(style,"text-decoration:", ";");
					if (conteudo != null && conteudo.length > 0) {
						for(String textDecoration : conteudo) {
							style = StringUtils.remove(style, "text-decoration:" + textDecoration + ";");
							if (StringUtils.trim(textDecoration).equalsIgnoreCase("underline")) {
								sublinhado = true; 
							}
							if (StringUtils.trim(textDecoration).equalsIgnoreCase("line-through")) {
								subscrito = true; 
							}
						}
					}

					conteudo = StringUtils.substringsBetween(style,"font-style:", ";");
					if (conteudo != null && conteudo.length > 0) {
						for(String textDecoration : conteudo) {
							style = StringUtils.remove(style, "font-style:" + textDecoration + ";");
							if (StringUtils.trim(textDecoration).equalsIgnoreCase("italic")) {
								italico = true;  
							}
						}
					}

					conteudo = StringUtils.substringsBetween(style,"font-weight:", ";");
					if (conteudo != null && conteudo.length > 0) {
						for(String textDecoration : conteudo) {
							style = StringUtils.remove(style, "font-weight:" + textDecoration + ";");
							if (StringUtils.trim(textDecoration).equalsIgnoreCase("bold")) {
								negrito = true;  
							}
						}
					}

					conteudo = StringUtils.substringsBetween(style,"color:", ";");
					if (conteudo != null && conteudo.length > 0) {
						for(String textDecoration : conteudo) {
							style = StringUtils.remove(style, "color:" + textDecoration + ";");
							String params = StringUtils.trim(textDecoration).replace("rgb(", "").replace(")", "");
							String r = params.substring(0, params.indexOf(',')).trim();
							String g = params.substring(params.indexOf(',') + 1, params.lastIndexOf(',')).trim();
							String b = params.substring(params.lastIndexOf(',') + 1, params.length()).trim();
							cor = String.format("#%02x%02x%02x", Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b));
						}
					}

					conteudo = StringUtils.substringsBetween(style,"font-family:", ";");
					if (conteudo != null && conteudo.length > 0) {
						for(String textDecoration : conteudo) {
							style = StringUtils.remove(style, "font-family:" + textDecoration + ";");
							face = StringUtils.trim(textDecoration);
						}
					}
				}							
			}
			
			parametroCampoLaudo.setRiscado(subscrito);
			parametroCampoLaudo.setNegrito(negrito);
			parametroCampoLaudo.setSublinhado(sublinhado);
			parametroCampoLaudo.setItalico(italico);
			
			if(cor != null && !cor.isEmpty()){
				parametroCampoLaudo.setCor(cor);
			}else{
				parametroCampoLaudo.setCor(COR_DEFAULT);
			}
	
			if(face != null && !face.isEmpty()){
				parametroCampoLaudo.setFonte(face);
			}else{
				parametroCampoLaudo.setFonte("Courier New");
			}
		}
	}
	
	private void obterInformacoesFonte(Document doc, AelParametroCamposLaudo parametroCampoLaudo){
		NodeList fontElement = doc.getElementsByTagName("font");
		if(fontElement !=  null && fontElement.getLength() > 0){
			String cor = null;
			String face = null;
			String tamanho = null;
			
			for (int i = 0; i < fontElement.getLength(); i++) {
				Element element = (Element) fontElement.item(i);
				if(!element.getAttribute("color").isEmpty()){
					cor = element.getAttribute("color");
				}
				if(face == null && !element.getAttribute("face").isEmpty()){
					face = element.getAttribute("face");
				}
				if(tamanho == null && !element.getAttribute("size").isEmpty()){
					tamanho = element.getAttribute("size");
				}
			}
			
			if(cor != null && !cor.isEmpty()){
				parametroCampoLaudo.setCor(cor);
			}else{
				parametroCampoLaudo.setCor(COR_DEFAULT);
			}
	
			if(face != null && !face.isEmpty()){
				parametroCampoLaudo.setFonte(face);
			}else{
				parametroCampoLaudo.setFonte("Courier New");
			}
	
			if(tamanho != null && !tamanho.isEmpty()){
				Short size = Short.parseShort(tamanho);
				parametroCampoLaudo.setTamanhoFonte(getTamhoFontPixels(size));
			}else{
				parametroCampoLaudo.setTamanhoFonte((short)8);
			}
		}
	}
	
	private Short getTamhoFontPixels(Short size){
		if(size.shortValue() == 1){
			size = 8;
		}else if(size.shortValue() == 2){
			size = 10;
		}else if(size.shortValue() == 3){
			size = 12;
		}else if(size.shortValue() == 4){
			size = 14;
		}else if(size.shortValue() == 5){
			size = 18;
		}else if(size.shortValue() == 6){
			size = 24;
		}else if(size.shortValue() == 7){
			size = 36;
		}
		return size;
	}
	
	public void validarExpressaoFormula(String formula) throws ApplicationBusinessException{
		if(formula!=null){
			/*Validação da quantidade de parenteses e colchetes*/
			validaParentesesEColchetes(formula);
		    
		    try{
		    	
		    	String[] operandos = StringUtils.substringsBetween(formula, "[", "]");
		    	String formulaTemp = formula;
		    	boolean dependeCamposMascara = false;
		    	
		    	if (operandos != null) {
					for (String operando : operandos) {
						if (operando.equals("IDADE")) {
							String valor = IDADE_PREVIA.toString();
							formulaTemp = StringUtils.replace(formulaTemp, "[" + operando + "]", valor);
						} else if (operando.equals("SEXO")) {
							String valor = SEXO;
							formulaTemp = StringUtils.replace(formulaTemp, "[" + operando + "]", valor);
						} else {
							dependeCamposMascara = true;
						}
					}
				}
		    	
		    	boolean possuiOperadores = possuiOperadores(formulaTemp);
		    	if(possuiOperadores){
		    		formulaTemp = comandosToLowerCase(formulaTemp);	
		    	}

				if (!dependeCamposMascara && possuiOperadores) {
					// se não depende de nenhum outro campo da máscara, avalia
					// logo a expressão e deixa o valor fixo no campo.
					StringBuilder formulasb = new StringBuilder("import static br.gov.mec.seam.exemplos.FuncoesOracle.*; ");
					formulasb.append(formulaTemp);
					Eval.me(formulasb.toString());
				}
		    }catch(Exception e){
		    	throw new ApplicationBusinessException(AelParametroCamposLaudoONExceptionCode.MSG_EXCESSAO_EXPRESSAO);
		    }
		}
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
	
	private String comandosToLowerCase(String formula){
		if(formula != null){
			return formula
						.replace("POWER", "power")
						.replace("SQRT", "sqrt")
						.replace("DECODE", "decode")
						.replace("GREATEST", "greatest")
						.replace("SIGN", "sign");
		}
		return null;
	}
	
	private void validaParentesesEColchetes(final String formula) throws ApplicationBusinessException{
		int contadorAbreParenteses = 0;
		int contadorFechaParenteses = 0;
		
		int contadorAbreColchetes = 0;
		int contadorFechaColchetes = 0;
		
		char abrePare = '('; // ocorrências de "("
	    char fechaPare = ')'; // ocorrências de ")"
	    
	    char abreCol = '['; // ocorrências de "("
	    char fechaCol = ']'; // ocorrências de ")"
	    
	    for(int i = 0; i < formula.length(); i++){
	    	if(formula.charAt(i) == abrePare){
	    		contadorAbreParenteses++; 
	        }
	    	if(formula.charAt(i) == fechaPare){
	    		contadorFechaParenteses++; 
	        }
	    	if(formula.charAt(i) == abreCol){
	    		contadorAbreColchetes++; 
	        }
	    	if(formula.charAt(i) == fechaCol){
	    		contadorFechaColchetes++; 
	        }
	    }
	    
	    if(contadorAbreParenteses != contadorFechaParenteses){
	    	throw new ApplicationBusinessException(AelParametroCamposLaudoONExceptionCode.MSG_EXCESSAO_EXPRESSAO_PARENTESES);	
	    }
	    
	    if(contadorAbreColchetes != contadorFechaColchetes){
	    	throw new ApplicationBusinessException(AelParametroCamposLaudoONExceptionCode.MSG_EXCESSAO_EXPRESSAO_COLCHETES);	
	    }
	} 
	
	
	
	private void defineValoresDefaultCasasECaracteres(AelParametroCamposLaudo parametroCampoLaudo){
		if(DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(parametroCampoLaudo.getObjetoVisual())
				|| DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())
				|| DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(parametroCampoLaudo.getObjetoVisual())){
			if(parametroCampoLaudo.getQuantidadeCaracteres()==null){
				parametroCampoLaudo.setQuantidadeCaracteres(100);
			}
			if(parametroCampoLaudo.getQuantidadeCasasDecimais() == null && DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(parametroCampoLaudo.getObjetoVisual())){
				parametroCampoLaudo.setQuantidadeCasasDecimais((short)0);
			}
		}else{
			parametroCampoLaudo.setQuantidadeCaracteres(0);
		}
	}
	
	private void defineParametroCampoLaudoId(AelParametroCamposLaudo parametroCamposLaudo){
	
		AelParametroCampoLaudoId id = new AelParametroCampoLaudoId();
		id.setVelEmaExaSigla(parametroCamposLaudo.getAelVersaoLaudo().getId().getEmaExaSigla());
		id.setVelEmaManSeq(parametroCamposLaudo.getAelVersaoLaudo().getId().getEmaManSeq());
		id.setVelSeqp(parametroCamposLaudo.getAelVersaoLaudo().getId().getSeqp());
		id.setCalSeq(parametroCamposLaudo.getCampoLaudo().getSeq());
		
		if (parametroCamposLaudo.getId() != null) {
		
			id.setSeqp(parametroCamposLaudo.getId().getSeqp());
		
		}
		
		parametroCamposLaudo.setId(id);
	
	}

	/**
	 * Getters para RNs e DAOs
	 */
	
	protected AelParametroCamposLaudoRN getAelParametroCamposLaudoRN(){
		return aelParametroCamposLaudoRN;
	}
	
	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}
	
	private AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	public void recarregaValoresPerdidos(AelParametroCamposLaudo parametro) {
		AelParametroCamposLaudo parametroCamposLaudoOLD = this.aelParametroCamposLaudoDAO.obterOriginal(parametro);
		parametro.setSumarioSemMascara(parametroCamposLaudoOLD.getSumarioSemMascara());
		parametro.setAlturaObjetoVisual(parametroCamposLaudoOLD.getAlturaObjetoVisual());
		parametro.setLarguraObjetoVisual(parametroCamposLaudoOLD.getLarguraObjetoVisual());
	}


	public List<AelParametroCamposLaudo> pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		List<AelParametroCamposLaudo> lista = aelParametroCamposLaudoDAO.pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(versaoLaudo);
		
		lista = new ArrayList<AelParametroCamposLaudo>(new LinkedHashSet<AelParametroCamposLaudo>(lista));
		
		return lista;
	}
}
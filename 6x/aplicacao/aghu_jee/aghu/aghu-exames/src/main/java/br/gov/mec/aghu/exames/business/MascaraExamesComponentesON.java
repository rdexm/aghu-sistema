package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class MascaraExamesComponentesON extends BaseBusiness {

	private static final long serialVersionUID = 1917289430785811445L;
	
	private static final Log LOG = LogFactory.getLog(MascaraExamesComponentesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/*
	*//**
	 * Cria os componentes JSF de acordo com a instância de
	 * AelParametroCamposLaudo passada por parâmetro.
	 * 
	 * @HIST MascaraExamesHistON.criarComponenteHist
	 * @param context
	 * @param campo
	 * @param itemSolicitacaoExame
	 * @param mapaDependentesExpressao
	 * @param mapaFormulas
	 * @param desenhoMascaraExameVO
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException 
	 *//*
	protected UIComponent criarComponente(FacesContext context,
			AelParametroCamposLaudo campo, Object resultadoObject,
			Object itemSolicitacaoExameObject,
			Map<String, String> mapaFormulas,
			Map<String, Set<String>> mapaDependentesExpressao,
			DesenhoMascaraExameVO desenhoMascaraExameVO,
			TipoMascaraExameEnum tipoMascaraExame, boolean isPrevia)
			throws BaseException {
		UIComponent componente;
		Object aelDescricoesResultadoObject = null;
		if(resultadoObject instanceof AelResultadoExame){
			AelResultadoExame resul = (AelResultadoExame) resultadoObject;
			aelDescricoesResultadoObject = resul.getDescricaoResultado();
		}else{if(resultadoObject instanceof AelResultadosExamesHist){
			AelResultadosExamesHist resul = (AelResultadosExamesHist) resultadoObject;
			aelDescricoesResultadoObject = resul.getDescricaoResultado();
		}}
		switch (campo.getObjetoVisual()) {
		case TEXTO_FIXO:
			componente = this.criarOutputText(campo, context, tipoMascaraExame);Não precisa ser verificado se é prévia
			break;

		case TEXTO_ALFANUMERICO:
			componente = getMascaraExamesON().criarInputText(campo, context, resultadoObject, itemSolicitacaoExameObject, mapaFormulas, mapaDependentesExpressao,
					desenhoMascaraExameVO, tipoMascaraExame, isPrevia, aelDescricoesResultadoObject);
			break;

		case TEXTO_NUMERICO_EXPRESSAO:
			componente = getMascaraExamesON().criarInputText(campo, context, resultadoObject, itemSolicitacaoExameObject, mapaFormulas, mapaDependentesExpressao,
					desenhoMascaraExameVO, tipoMascaraExame, isPrevia, aelDescricoesResultadoObject);
			break;

		case TEXTO_LONGO:
			componente = this.criarInputTextArea(campo, context, resultadoObject, tipoMascaraExame, aelDescricoesResultadoObject);Não precisa ser verificado se é prévia
			break;

		case TEXTO_CODIFICADO:
			componente = this.criarSelectOneMenu(campo, context, resultadoObject, tipoMascaraExame, aelDescricoesResultadoObject);Não precisa ser verificado se é prévia
			break;

		case EQUIPAMENTO:
			componente = this.criarEquipamento(campo, context, itemSolicitacaoExameObject, tipoMascaraExame, isPrevia);
			break;

		case METODO:
			componente = this.criarMetodo(campo, context, itemSolicitacaoExameObject, tipoMascaraExame, isPrevia);
			break;

		case RECEBIMENTO:
			componente = this.criarRecebimento(campo, context, itemSolicitacaoExameObject, tipoMascaraExame, isPrevia);
			break;

		case HISTORICO:
			componente = this.criarHistorico(campo, context, itemSolicitacaoExameObject, tipoMascaraExame, isPrevia);
			break;

		case VALORES_REFERENCIA:
			componente = getMascaraExamesON().criarValoresReferencia(campo, context, itemSolicitacaoExameObject, tipoMascaraExame, isPrevia);
			break;

		default:
			componente = null;
			break;
		}

		return componente;
	}


	*//**
	 * @HIST MascaraExamesHistON.criarComponenteInformacaoRespiracaoHist
	 * @param context
	 * @param itemSolicitacaoExame
	 * @return
	 * @throws BaseException
	 *//*
	protected UIComponent criarComponenteInformacaoRespiracao(
			FacesContext context, Object itemSolicitacaoExameObject)
			throws BaseException {
		DominioFormaRespiracao formaRespiracao = null;
		BigDecimal litrosOxigenio = null;
		Short percOxigenio = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames item = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			formaRespiracao = item.getFormaRespiracao();
			litrosOxigenio = item.getLitrosOxigenio();
			percOxigenio= item.getPercOxigenio();
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicExameHist){
			AelItemSolicExameHist item = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			formaRespiracao = item.getFormaRespiracao();
			litrosOxigenio = item.getLitrosOxigenio();
			percOxigenio= item.getPercOxigenio();
		}}
		HtmlOutputText output = null;

		if(formaRespiracao != null){

			Application application = context.getApplication();

			output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			output.setId("temp_"+ getMascaraExamesON().UNIQUE++);
			output.setStyle("font-family: Courier New; font-size: 10pt; font-weight: bold;");

			if(formaRespiracao.equals(DominioFormaRespiracao.UM)){
				output.setValue("Respirando em ar ambiente.");
			}else if(formaRespiracao.equals(DominioFormaRespiracao.DOIS)){
				output.setValue("Recebendo " + litrosOxigenio.toString() + " litros/minuto de oxigênio.");
			}else if(formaRespiracao.equals(DominioFormaRespiracao.TRES)){
				output.setValue("Em ventilação mecânica com fração inspirada de oxigênio de " + percOxigenio + "%.");
			}
		}
		return output;
	}

	*//**
	 * @HIST MascaraExamesHistON.criarComponenteInformacaoMedicoHist
	 * @param context
	 * @param itemSolicitacaoExame
	 * @param isPrevia
	 * @return
	 * @throws BaseException
	 *//*
	protected UIComponent criarComponenteInformacaoMedico(FacesContext context,
			Object itemSolicitacaoExameObject, boolean isPrevia)
			throws BaseException {
		RapServidores servidorResponsabilidade = null;
		AelUnfExecutaExames unfExecutaExame = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames item = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			servidorResponsabilidade = item.getServidorResponsabilidade();
			unfExecutaExame = item.getAelUnfExecutaExames();
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicExameHist){
			AelItemSolicExameHist item = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			servidorResponsabilidade = item.getServidorResponsabilidade();
			unfExecutaExame = item.getAelUnfExecutaExames();
		}}
		
		HtmlOutputText output = null;

		if(isPrevia){
			Application application = context.getApplication();

			output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			output.setId("temp_"+ getMascaraExamesON().UNIQUE++);
			output.setStyle("font-family: Courier New; font-size: 8pt;");
			output.setValue("Dr.(a) Testes  - PVA:123456789");

		}else{

			if(servidorResponsabilidade != null){

				Boolean uniFechada = (unfExecutaExame != null 
						&& unfExecutaExame.getUnidadeFuncional() != null 
						&& unfExecutaExame.getUnidadeFuncional().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.AREA_FECHADA));

				//RapServidores servidor = itemSolicitacaoExame.getServidorResponsabilidade();

				List<ConselhoProfissionalServidorVO> conselhos = getRegistroColaboradorFacade()
						.buscaConselhosProfissionalServidorAtivoInativo(
								servidorResponsabilidade.getId().getMatricula(),
								servidorResponsabilidade.getId().getVinCodigo());

				if(uniFechada && conselhos.size() > 0){
					ConselhoProfissionalServidorVO conselho = conselhos.get(0);

					Application application = context.getApplication();

					output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

					output.setId("temp_"+ getMascaraExamesON().UNIQUE++);
					output.setStyle("font-family: Courier New; font-size: 8pt;");
					output.setValue("Dr.(a)" + conselho.getNome() + " - " + conselho.getSiglaConselho() + ":" + (conselho.getNumeroRegistroConselho() != null ? conselho.getNumeroRegistroConselho() : ""));
				}
			}
		}

		return output;
	}


	protected UIComponent criarComponenteInformacaoColeta(FacesContext context, String textoComponent) throws BaseException {
		HtmlOutputText output = (HtmlOutputText) context.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);

		output.setId("temp_"+ getMascaraExamesON().UNIQUE++);
		output.setStyle("font-family: Courier New; font-size: 9pt;");
		output.setValue(textoComponent);

		return output;
	}

	*//**
	 * Cria um HtmlInputTextarea a partir de um AelParametroCamposLaudo.
	 * 
	 * @param campo
	 * @param context
	 * @param tipoMascaraExame
	 * @return
	 *//*
	private UIComponent criarInputTextArea(AelParametroCamposLaudo campo,
			FacesContext context, Object resultadoObject,
			TipoMascaraExameEnum tipoMascaraExame, Object aelDescricoesResultadoObject) {
		
		String descricaoResultadoExame = null;
		if(resultadoObject instanceof AelResultadoExame){
			AelDescricoesResultado descResult = (AelDescricoesResultado) aelDescricoesResultadoObject;
			descricaoResultadoExame = descResult != null ? descResult.getDescricao() : null;
		}else{if(resultadoObject instanceof AelResultadosExamesHist){
			AelDescricoesResultadoHist descResult = (AelDescricoesResultadoHist) aelDescricoesResultadoObject;
			descricaoResultadoExame = descResult != null ? descResult.getDescricao() : null;
		}}
		
		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			Application application = context.getApplication();

			HtmlOutputText output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

			if (resultadoObject != null && aelDescricoesResultadoObject != null && descricaoResultadoExame!=null) {
				
				String textoLaudo = descricaoResultadoExame.replaceAll("\n<br/>","<br />");
				
				if (textoLaudo.contains("rtf")) {
					
					output.setValue(AghuUtil.rtfToHtml(textoLaudo.replaceAll("\n|\r","<br />")));
					
				} else {
					
					output.setValue(AghuUtil.converterRTF2Text(textoLaudo.replaceAll("\n|\r","<br />")));
					
				}
				
 
			}
			output.setEscape(false);

			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			output.setId(campo.getId().toString()+ "_" + new Date().getTime());

			output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));

			return output;
		} else {
			Application application = context.getApplication();

			HtmlEditor inputTextArea = (HtmlEditor) application.createComponent(HtmlEditor.COMPONENT_TYPE);

			String identificador = campo.getCampoLaudo().getNome() + ((campo.getId()!= null && campo.getId().getSeqp()!= null)?campo.getId().getSeqp():new Date().getTime());
			inputTextArea.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			inputTextArea.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);

			inputTextArea.setId(campo.getId().toString());
			inputTextArea.setLabel(campo.getCampoLaudo().getNome());
			inputTextArea.setTheme("advanced");

			if (campo.getLarguraObjetoVisual() != null) {
				inputTextArea.setWidth(campo.getLarguraObjetoVisual().intValue());
			}			
			if (campo.getAlturaObjetoVisual() != null) {
				inputTextArea.setHeight(campo.getAlturaObjetoVisual().intValue());
			}

			inputTextArea.setConfiguration("configEditorPreviaMascaraExames");
			if (resultadoObject != null && aelDescricoesResultadoObject != null) {
				inputTextArea.setValue(descricaoResultadoExame);
			}

			return inputTextArea;
		}
	}
	
	*//**
	 * Cria um HtmlOutputText a partir de um AelParametroCamposLaudo
	 * 
	 * @param campo
	 * @param context
	 * @param itemSolicitacaoExame 
	 * @return
	 *//*
	protected UIComponent criarOutputText(AelParametroCamposLaudo campo, FacesContext context, TipoMascaraExameEnum tipoMascaraExame) {
		Application application = context.getApplication();

		HtmlOutputText output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

		String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

		String textoLivre = getMascaraExamesON().retornaParametroCampoLaudoTextoLivreSemTag(campo.getTextoLivre());

		if (textoLivre != null) {
			output.setValue(textoLivre);
		}
		output.setEscape(false);
		output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
		output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);

		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			output.setId(campo.getId().toString()+"_"+getMascaraExamesON().UNIQUE++);
		}else{
			output.setId(campo.getId().toString());
		}

		output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));

		return output;
	}
	
	*//**
	 * Cria um HtmlSelectOneMenu a partir de um AelParametroCamposLaudo
	 * 
	 * @HIST MascaraExamesHistON.criarSelectOneMenuHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 *//*
	private UIComponent criarSelectOneMenu(AelParametroCamposLaudo campo,
			FacesContext context, Object resultadoObject,
			TipoMascaraExameEnum tipoMascaraExame, Object aelDescricoesResultadoObject) {
		
		String descricaoResultadoExame = null;
		AelResultadoCodificado 		aelResultadoCodificado = null;
		AelResultadoCaracteristica  aelResultadoCaracteristica = null;
		
		if(resultadoObject instanceof AelResultadoExame){
			AelResultadoExame aelResultadoExame = (AelResultadoExame) resultadoObject;
			aelResultadoCodificado = aelResultadoExame.getResultadoCodificado();
			aelResultadoCaracteristica  = aelResultadoExame.getResultadoCaracteristica();
			
			AelDescricoesResultado descResult = (AelDescricoesResultado) aelDescricoesResultadoObject;
			descricaoResultadoExame = descResult != null ? descResult.getDescricao() : null;
		}else{if(resultadoObject instanceof AelResultadosExamesHist){
			AelResultadosExamesHist aelResultadoExame = (AelResultadosExamesHist) resultadoObject;
			aelResultadoCodificado = aelResultadoExame.getResultadoCodificado();
			aelResultadoCaracteristica  = aelResultadoExame.getResultadoCaracteristica();
			
			AelDescricoesResultadoHist descResult = (AelDescricoesResultadoHist) aelDescricoesResultadoObject;
			descricaoResultadoExame = descResult != null ? descResult.getDescricao() : null;
		}}
		
		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			Application application = context.getApplication();
			HtmlOutputText output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

			if (resultadoObject != null) {
				if (campo.getCampoLaudo().getGrupoResultadoCodificado() != null){
					if (aelResultadoCodificado != null) {
						output.setValue(aelResultadoCodificado.getDescricao());
					}
				} else {
					if (aelResultadoCaracteristica != null) {
						output.setValue(aelResultadoCaracteristica.getDescricao());
					}
				}
			}

			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			output.setId(campo.getId().toString()+"_"+getMascaraExamesON().UNIQUE++);

			output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));

			return output;
		} else {
			Application application = context.getApplication();

			HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) application
			.createComponent(HtmlSelectOneMenu.COMPONENT_TYPE);

			UISelectItem selectitem = null;		

			if (campo.getCampoLaudo().getGrupoResultadoCodificado() != null){
				List<AelResultadoCodificado> resultadosCodificados = this.getAelResultadoCodificadoDAO().pesquisarResultadosCodificadosPorCampoLaudo(campo.getCampoLaudo());

				// Descrição do resultado do exame
				String descricaoResultado = null;
				if(resultadoObject != null){
					descricaoResultado = aelDescricoesResultadoObject != null ? descricaoResultadoExame : null;
				}

				for (AelResultadoCodificado resultadoCodificado : resultadosCodificados) {
					selectitem = (UISelectItem) application.createComponent(UISelectItem.COMPONENT_TYPE);
					selectitem.setItemLabel(resultadoCodificado.getDescricao());
					selectitem.setItemValue("RESULTADO_CODIFICADO:gtcSeq="+resultadoCodificado.getId().getGtcSeq()+",seqp="+resultadoCodificado.getId().getSeqp());

					selectOneMenu.getChildren().add(selectitem);
					if (resultadoObject != null
							&& aelResultadoCodificado != null
							&& resultadoCodificado.getId().getGtcSeq()	.equals(aelResultadoCodificado.getId().getGtcSeq())
							&& resultadoCodificado.getId().getSeqp()	.equals(aelResultadoCodificado.getId().getSeqp())) {
						selectOneMenu.setValue("RESULTADO_CODIFICADO:gtcSeq="+resultadoCodificado.getId().getGtcSeq()+",seqp="+resultadoCodificado.getId().getSeqp());
					} else{

						// Descrição do resultado codificado
						String descricaoResultadoCodificado = resultadoCodificado.getDescricao();

						// Verifica se a descrição do resultado do exame é equivalente a do resultado codificado atual
						if (descricaoResultado != null
								&& descricaoResultadoCodificado != null
								&& descricaoResultadoCodificado.trim().equalsIgnoreCase(descricaoResultado.trim())) {
							selectOneMenu.setValue("RESULTADO_CODIFICADO:gtcSeq="+resultadoCodificado.getId().getGtcSeq()+",seqp="+resultadoCodificado.getId().getSeqp());
						}
					}

				}	
			} else {
				List<AelExameGrupoCaracteristica> exameGrupoCaracteristicaList = this.getAelExameGrupoCaracteristicaDAO().pesquisarExameGrupoCarateristicaPorCampo(campo);

				for(AelExameGrupoCaracteristica exameGrupoCaracteristica : exameGrupoCaracteristicaList){
					selectitem = (UISelectItem) application
					.createComponent(UISelectItem.COMPONENT_TYPE);
					selectitem.setItemLabel(exameGrupoCaracteristica.getResultadoCaracteristica().getDescricao());
					selectitem.setItemValue("GRUPO_CARACTERISTICA:emaExaSigla="+exameGrupoCaracteristica.getId().getEmaExaSigla()
							+",emaManSeq="+exameGrupoCaracteristica.getId().getEmaManSeq()
							+",cacSeq="+exameGrupoCaracteristica.getId().getCacSeq()
							+",gcaSeq="+exameGrupoCaracteristica.getId().getGcaSeq());

					selectOneMenu.getChildren().add(selectitem);

					if (resultadoObject != null 
							&& exameGrupoCaracteristica.getId().getCacSeq().equals(aelResultadoCaracteristica.getSeq())){
						selectOneMenu.setValue("GRUPO_CARACTERISTICA:emaExaSigla="+exameGrupoCaracteristica.getId().getEmaExaSigla()
								+",emaManSeq="+exameGrupoCaracteristica.getId().getEmaManSeq()
								+",cacSeq="+exameGrupoCaracteristica.getId().getCacSeq()
								+",gcaSeq="+exameGrupoCaracteristica.getId().getGcaSeq());
					}
				}
			}		

			selectitem = (UISelectItem) application
			.createComponent(UISelectItem.COMPONENT_TYPE);
			selectitem.setItemLabel("-- Selecione --");
			selectitem.setItemValue(null);
			selectOneMenu.getChildren().add(selectitem);

			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();		

			selectOneMenu.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			selectOneMenu.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			selectOneMenu.setId(campo.getId().toString());
			selectOneMenu.setLabel(campo.getCampoLaudo().getNome());

			selectOneMenu.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));

			return selectOneMenu;
		}
	}

	*//**
	 * Exibe os dados de equipamento a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarEquipamentoHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 *//*
	private UIComponent criarEquipamento(AelParametroCamposLaudo campo,
			FacesContext context, Object itemSolicitacaoExameObject,
			TipoMascaraExameEnum tipoMascaraExame, boolean isPrevia) {
		Integer itemSolicSoeSeq = null;
		Short 	itemSolicSeqpP = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames itemSolic = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicExameHist){
			AelItemSolicExameHist itemSolic = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}}
		
		HtmlOutputText output = null;

		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			String equipamento = null;

			if(isPrevia){
				equipamento = "Equipamento teste prévia";
			}else{
				equipamento = this.getMascaraExamesRN()
						.buscaInformacaoEquipamento(
								itemSolicSoeSeq,
								itemSolicSeqpP);
			}

			Application application = context.getApplication();

			output = (HtmlOutputText) application
			.createComponent(HtmlOutputText.COMPONENT_TYPE);

			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

			if (campo.getTextoLivre() != null) {
				output.setValue(equipamento);
			}

			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			output.setId(campo.getId().toString()+"_"+getMascaraExamesON().UNIQUE++);

			output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));
		}

		return output;
	}

	*//**
	 * Exibe os dados de método a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarMetodoHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException 
	 *//*
	private UIComponent criarMetodo(AelParametroCamposLaudo campo, FacesContext context, Object itemSolicitacaoExameObject, 
			TipoMascaraExameEnum tipoMascaraExame, boolean isPrevia) throws BaseException {
		Integer itemSolicSoeSeq = null;
		Short 	itemSolicSeqpP = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames itemSolic = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicExameHist){
			AelItemSolicExameHist itemSolic = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}}
		
		HtmlOutputText output = null;

		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			String metodo = null;
			if(isPrevia){
				metodo = "Método teste prévia";
			}else{
				metodo = this.getMascaraExamesRN().buscaInformacaoMetodo(itemSolicSoeSeq, itemSolicSeqpP);
			}

			Application application = context.getApplication();

			output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

			if (campo.getTextoLivre() != null) {
				output.setValue(metodo);
			}

			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			output.setId(campo.getId().toString()+"_"+getMascaraExamesON().UNIQUE++);

			output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));
		}

		return output;
	}

	*//**
	 * Exibe os dados de recebimento a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarRecebimentoHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException 
	 *//*
	private UIComponent criarRecebimento(AelParametroCamposLaudo campo,
			FacesContext context, Object itemSolicitacaoExameObject,
			TipoMascaraExameEnum tipoMascaraExame, boolean isPrevia)
			throws BaseException {
		Integer itemSolicSoeSeq = null;
		Short 	itemSolicSeqpP = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames itemSolic = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicExameHist itemSolic = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}}
		
		HtmlOutputText output = null;

		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			String recebimento = null;
			if(isPrevia){
				recebimento = "Material Prévia Recebido em: "+ new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(new Date());
			}else{
				recebimento = this.getMascaraExamesRN().buscaInformacaoRecebimento(itemSolicSoeSeq, itemSolicSeqpP); 
			}

			Application application = context.getApplication();

			output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

			if (campo.getTextoLivre() != null) {
				output.setValue(recebimento);
			}

			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			output.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			output.setId(campo.getId().toString()+"_"+getMascaraExamesON().UNIQUE++);

			output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));
		}

		return output;
	}

	*//**
	 * Exibe os dados de histórico a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarHistoricoHist
	 * @param campo
	 * @param contexto
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException 
	 *//*
	private UIComponent criarHistorico(AelParametroCamposLaudo campo,
			FacesContext context,
			Object itemSolicitacaoExameObject,
			TipoMascaraExameEnum tipoMascaraExame, boolean isPrevia)
			throws BaseException {
		Integer itemSolicSoeSeq = null;
		Short 	itemSolicSeqpP = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames itemSolic = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicExameHist){
			AelItemSolicExameHist itemSolic = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
		}}
		
		HtmlOutputText output = null;
		List<String> listaRestorno = new ArrayList<String>();
		Application application = context.getApplication();

		if (TipoMascaraExameEnum.RELATORIO.equals(tipoMascaraExame)) {
			if(isPrevia){

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
				AghParametros parametroNumResultAnterior = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NRO_RESULTADOS_ANTERIORES);

				for (int i = 0; i < parametroNumResultAnterior.getVlrNumerico().intValue(); i++) {
					StringBuffer linhaRetorno = new StringBuffer(30);
					linhaRetorno.append(sdf.format(new Date()));
					linhaRetorno.append("  ");
					linhaRetorno.append("12345,67");
					linhaRetorno.append("Valor prévia normalidade");

					listaRestorno.add(linhaRetorno.toString());
				}
			}else if(campo.getCampoLaudoRelacionado()!=null && campo.getCampoLaudoRelacionado().getSeq()!=null){

				listaRestorno = this.getMascaraExamesRN().buscaInformacaoHistorico(itemSolicSoeSeq, itemSolicSeqpP, campo.getCampoLaudoRelacionado().getSeq());
			}

			String identificador = campo.getCampoLaudo().getNome() + campo.getId().getSeqp();

			UIComponent div = getMascaraExamesON().criarDiv(identificador, "");
			div.getAttributes().put(getMascaraExamesON().NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);

			div.setId(campo.getId().toString()+"_"+getMascaraExamesON().UNIQUE++);

			if(!listaRestorno.isEmpty()){
				for (String resultAnt : listaRestorno) {
					if(resultAnt != null && !resultAnt.isEmpty()){

						UIComponent subDiv = getMascaraExamesON().criarDiv("subDiv"+campo.getId().getCalSeq()+"_"+getMascaraExamesON().UNIQUE++, "");

						output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
						output.setValue(resultAnt);
						output.setStyle(getMascaraExamesON().obterStyleParametroCampoLaudo(campo, tipoMascaraExame));

						subDiv.getChildren().add(output);

						div.getChildren().add(subDiv);		
					}
				}
			}
			return div;
		}

		return null;
	}

	protected UIComponent criarAssinaturaEletronica(FacesContext context,
			Object itemSolicitacaoExameObject,
			DesenhoMascaraExameVO desenhoMascaraExameVO)
			throws BaseException {
		Integer itemSolicSoeSeq = null;
		Short 	itemSolicSeqpP = null;
		AelUnfExecutaExames unfExecutaExames = null;
		Integer matriculaConselho = null;
		Short vinCodigoConselho = null;
		if(itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames){
			AelItemSolicitacaoExames itemSolic = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
			unfExecutaExames = itemSolic.getAelUnfExecutaExames();
			AelExtratoItemSolicitacao extratoItemSolic = 
					getAelExtratoItemSolicitacaoDAO().
						obterUltimoItemSolicitacaoSitCodigo(itemSolicSoeSeq, 
															itemSolicSeqpP, 
															DominioSituacaoItemSolicitacaoExame.LI.toString());
			if (extratoItemSolic == null) {
				return null;
			} else {
				if(extratoItemSolic.getServidorEhResponsabilide() != null){
					matriculaConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getMatricula();
					vinCodigoConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getVinCodigo();
				}else{
					matriculaConselho = extratoItemSolic.getServidor().getId().getMatricula();
					vinCodigoConselho = extratoItemSolic.getServidor().getId().getVinCodigo();
				}
			}
			
		}else{if(itemSolicitacaoExameObject instanceof AelItemSolicExameHist){
			AelItemSolicExameHist itemSolic = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
			unfExecutaExames = itemSolic.getAelUnfExecutaExames();
			AelExtratoItemSolicHist extratoItemSolic = 
				getAelExtratoItemSolicHistDAO().
					obterUltimoItemSolicitacaoSitCodigo(itemSolicSoeSeq, 
														itemSolicSeqpP, 
														DominioSituacaoItemSolicitacaoExame.LI.toString());
			
				if (extratoItemSolic == null) {
					return null;
				} else {
					if (extratoItemSolic.getSerMatriculaEhResponsabilid() != null) {
						matriculaConselho = extratoItemSolic
								.getSerMatriculaEhResponsabilid();
						vinCodigoConselho = extratoItemSolic
								.getSerVinCodigoEhResponsabili();
					} else {
						matriculaConselho = extratoItemSolic.getSerMatricula();
						vinCodigoConselho = extratoItemSolic.getSerVinCodigo();
					}
				}
			}
		}
		
		UIComponent uiComponent = null;
		String assinatura = null;

		boolean uniFechada = unfExecutaExames.getUnidadeFuncional().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.AREA_FECHADA);

		//if(!uniFechada){

		boolean chefiaAssEletro = unfExecutaExames.getUnidadeFuncional().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.CHEFIA_ASS_ELET);

		if(chefiaAssEletro){
			if(desenhoMascaraExameVO.getCabecalhoLaudo() != null){
				if(desenhoMascaraExameVO.getCabecalhoLaudo().getpChefeUnidade() != null){
					assinatura = desenhoMascaraExameVO.getCabecalhoLaudo().getpChefeUnidade() + " - " +
					(desenhoMascaraExameVO.getCabecalhoLaudo().getpConselhoUnid() != null ? desenhoMascaraExameVO.getCabecalhoLaudo().getpConselhoUnid() + ": ": "")  + 
					(desenhoMascaraExameVO.getCabecalhoLaudo().getpNroConselhoUnid() != null ? desenhoMascaraExameVO.getCabecalhoLaudo().getpNroConselhoUnid():"");
				}
			}

		}else{

			List<ConselhoProfissionalServidorVO> conselhos = null;

			conselhos = getRegistroColaboradorFacade().buscaConselhosProfissionalServidorAtivoInativo(matriculaConselho, vinCodigoConselho);

			if(conselhos.size() > 0){
				ConselhoProfissionalServidorVO conselho = conselhos.get(0);

				assinatura = conselho.getNome() + " - " + conselho.getSiglaConselho() + (conselho.getNumeroRegistroConselho() != null ? " : " + conselho.getNumeroRegistroConselho() : "");
			}
		}

		if(assinatura != null){
			uiComponent = criarDivAssinaturaEletronica(context, assinatura,
					uniFechada);
		}			

		return uiComponent;
	}


	private UIComponent criarDivAssinaturaEletronica(FacesContext context,
			String assinatura, boolean uniFechada) throws BaseException {
		UIComponent uiComponent;
		uiComponent = getMascaraExamesON().criarDiv("assEletro"+getMascaraExamesON().UNIQUE++, "position: absolute; width:745px; text-align: right;");

		Application application = context.getApplication();
		HtmlOutputText output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

		output.setId("temp_"+ getMascaraExamesON().UNIQUE++);
		output.setStyle("font-family: Courier New; font-style: italic; font-size: 12px;");
		//			if(uniFechada){
		//				output.setValue("Dr.(a) " + assinatura);
		//			}else{
		//				output.setValue(assinatura);
		//			}

		if(!uniFechada){
			output.setValue(assinatura);
		}

		UIComponent subDiv = getMascaraExamesON().criarDiv("subDiv" + getMascaraExamesON().UNIQUE++, "width:745px; text-align: right;");
		subDiv.getChildren().add(output);

		uiComponent.getChildren().add(subDiv);

		application = context.getApplication();

		if(!uniFechada){
			HtmlOutputText outputConf = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			outputConf.setId("temp_"+ getMascaraExamesON().UNIQUE++);
			outputConf.setStyle("font-family: Courier New; font-size: 8px;");
			outputConf.setValue("Conferência por Vídeo");

			UIComponent subDivConf = getMascaraExamesON().criarDiv("subDiv" + getMascaraExamesON().UNIQUE++, "width:745px; text-align: right;");
			subDivConf.getChildren().add(outputConf);

			uiComponent.getChildren().add(subDivConf);
		}
		return uiComponent;
	}
	private MascaraExamesON getMascaraExamesON(){
		return new MascaraExamesON();
	}
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return new AelExtratoItemSolicitacaoDAO();
	}
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return new AelItemSolicitacaoExameDAO();
	}
	protected AelExtratoItemSolicHistDAO getAelExtratoItemSolicHistDAO() {
		return new AelExtratoItemSolicHistDAO();
	}
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.obterDoContexto(IRegistroColaboradorFacade.class,
		"registroColaboradorFacade");
	}
	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return new AelResultadoCodificadoDAO();
	}
	protected AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return new AelExameGrupoCaracteristicaDAO();
	}
	protected MascaraExamesRN getMascaraExamesRN() {
		return new MascaraExamesRN();
	}
	protected IParametroFacade getParametroFacade() {
		return obterDoContexto(IParametroFacade.class, "parametroFacade");
	}
	protected IExamesLaudosFacade getExamesLaudosFacade(){
		return obterDoContexto(IExamesLaudosFacade.class, "examesLaudosFacade");
	}
	*/
}
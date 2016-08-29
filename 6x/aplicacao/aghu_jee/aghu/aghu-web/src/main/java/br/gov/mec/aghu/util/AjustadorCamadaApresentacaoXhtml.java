package br.gov.mec.aghu.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;

@SuppressWarnings("PMD")
public class AjustadorCamadaApresentacaoXhtml {
	
	private File arquivoXHTML;
	private String nomeArquivo;
	
	
	public AjustadorCamadaApresentacaoXhtml(File xhtml, String arquivo) {
		super();
		this.arquivoXHTML = xhtml;
		this.nomeArquivo = arquivo;
	}




	public String processar() throws IOException {//NOPMD
		File arquivoPAGEXML = AjustadorCamadaApresentacao.getArquivoPAGEXML(nomeArquivo);
		String mapNavegacao = arquivoPAGEXML!=null?pegarMapaNavegacao(arquivoPAGEXML):null;
		
		String strFile = FileUtils.readFileToString(this.arquivoXHTML);
		
		if (mapNavegacao != null) {
			StringBuilder navegacao = new StringBuilder("\n<!-- Tag de Navegacao extraida do page.xml\n");
			navegacao.append(mapNavegacao);
			navegacao.append("\n-->\n");
			
			int indexDef = strFile.indexOf("</ui:define>");
			indexDef = strFile.indexOf('>', indexDef);

			StringBuilder strBuilder = new StringBuilder(strFile);
			strBuilder.insert((indexDef+1), navegacao);
			
			strFile = strBuilder.toString();
		}
		
		strFile = strFile.replace("xmlns:s=\"http://jboss.com/products/seam/taglib\"", "");
		strFile = strFile.replace("xmlns:mec=\"http://www.mec.gov.br/seam\"", "xmlns:mec=\"http://www.mec.gov.br/components\"");
		strFile = strFile.replace("template=\"/layout/template.xhtml\"", "template=\"/WEB-INF/templates/template.xhtml\"");
		
		strFile = strFile.replace("mec:columnCustom", "<rich:column");
		
		String controllerName = "_CONTROLLER_";
		String controllerNameWithBrackets = "#{" + controllerName + "}";
		
		
		int indexBody = strFile.indexOf("body");
		if (indexBody > 0) {
			indexBody = strFile.indexOf('>', indexBody);
			StringBuilder strBuilder = new StringBuilder(strFile);
			StringBuilder pageConfig = new StringBuilder("\n		<mec:pageConfig controller=\"" + controllerNameWithBrackets + "\" ");
			
			String strStopEnter = "stopEnter";
			if (strFile.contains(strStopEnter)) {
				int stopEnterIndex = strFile.indexOf(strStopEnter);
				int tagInit = strFile.indexOf("<", (stopEnterIndex-25));
				int tagEnd = strFile.indexOf("/>", tagInit);
				
				strBuilder.replace(tagInit, (tagEnd+2), "");
				
				pageConfig.append("ignoreEnter=\"true\"");
			}
			pageConfig.append(" />\n");
			
			strBuilder.insert(indexBody+1, pageConfig.toString());
			strFile = strBuilder.toString();
		}
		
		String docFields = "<mec:docField>";
		if (strFile.contains(docFields)) {
			int count = StringUtils.countMatches(strFile, docFields);

			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = strFile.indexOf(docFields, indexInit);
				strFile = processarDocField(strFile, indexInit, docFields);
				indexInit = indexInit + 10;
			}
		}
		
		String sButton = "<s:button";
		if (strFile.contains(sButton)) {
			int count = StringUtils.countMatches(strFile, sButton);
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = strFile.indexOf(sButton, indexInit);
				if (indexInit > 0){
					strFile = processarSbutton(strFile, indexInit, sButton);
				}	
				indexInit = indexInit + 10;
			}
		}
		
		String sLink = "<s:link";
		if (strFile.contains(sLink)) {
			int count = StringUtils.countMatches(strFile, sLink);
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = strFile.indexOf(sLink, indexInit);
				strFile = processarSlink(strFile, indexInit, sLink);
				indexInit = indexInit + 10;
			}
		}
		String mecServerDataTable = "<mec:serverDataTable";
		if (strFile.contains(mecServerDataTable)) {
			strFile = processarMecServerDataTable(strFile,controllerNameWithBrackets);
		}
		/*
		<mec:selectOneMenu id="indConfirmado"
				items="#{simNaoItens}" value="#{recebimentoController.indConfirmado}"
				label="#{messages.LABEL_CONFIRMADO_RECEBIMENTO}" title="#{messages.TITLE_CONFIRMADO_RECEBIMENTO}" />
		*/
		String richColumnTag = "<rich:column";
		if (strFile.contains(richColumnTag)) {
			int count = StringUtils.countMatches(strFile, richColumnTag);
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = strFile.indexOf(richColumnTag, indexInit);
				strFile = processarRichColumn(strFile, indexInit, richColumnTag);
				indexInit = indexInit + 10;
			}
		}
		
		String sHasPermission = "s:hasPermission";
		if (strFile.contains(sHasPermission)) {
			strFile = processarSHasPermission(strFile, controllerName);
		}
		
		String paramCid = "<f:param name=\"cid\" value=\"#{javax.enterprise.context.conversation.id}\" />";
		strFile = strFile.replace("<s:conversationId value=\"#{conversation.id}\"/>", paramCid);
		strFile = strFile.replace("<s:conversationId />", paramCid);
		
		return strFile;
	}




	private String processarSHasPermission(String strFile, String controllerName) {
		Integer indexOfHasPermission = strFile.indexOf("s:hasPermission");

		while (indexOfHasPermission != -1) {
			String mecHasPermission = "mec:hasPermission(securityController,";
			int indexAbreParentesesDepois = strFile.indexOf('(', indexOfHasPermission);
			int indexFechaParentesesDepois = strFile.indexOf(')', indexOfHasPermission);

			String stPermissions = strFile.substring(indexAbreParentesesDepois + 1, indexFechaParentesesDepois);

			stPermissions = stPermissions.replace("','", ",");

			mecHasPermission += stPermissions + ")";//NOPMD

			String stHasPermission = strFile.substring(indexOfHasPermission, indexFechaParentesesDepois + 1);

			strFile = strFile.replace(stHasPermission, mecHasPermission);
			indexOfHasPermission = strFile.indexOf("s:hasPermission");
		}
		return strFile;
	}




	private String processarMecServerDataTable(String strFile, String controllerNameWithBrackets) {
			Integer indexOfDataTable = strFile.indexOf("<mec:serverDataTable");
			while (indexOfDataTable != -1) {
				strFile = strFile.replace("<mec:serverDataTable", "<mec:serverDataTable dataModel=\"dataModel\" mbean=\""
						+ controllerNameWithBrackets + "\" ");
				// esta substituindo .ativo de pojos/entidades,
				//strFile = strFile.replace(".ativo", ".dataModel.pesquisaAtiva");
				Integer indexOfDataTableINI = strFile.indexOf("<mec:serverDataTable");
				Integer indexOfDataTableFIM = strFile.indexOf("</mec:serverDataTable>");

				String stInicio = strFile.substring(0, indexOfDataTableINI);
				String stMeio = strFile.substring(indexOfDataTableINI, indexOfDataTableFIM + 22);
				String stFim = strFile.substring(indexOfDataTableFIM + 22, strFile.length());

				stMeio = stMeio.replace("<h:column", "<rich:column");
				stMeio = stMeio.replace("</h:column", "</rich:column");

				strFile = stInicio + stMeio + stFim;
				indexOfDataTable = strFile.indexOf("<mec:serverDataTable", indexOfDataTableFIM + 22);
			}
		return strFile;
	}
	
	private String processarRichColumn(String strFile, int indexInit, String richColumnTag) {
		StringBuilder strBuilder = new StringBuilder(strFile);
		
		int indexTagEnd  = strBuilder.indexOf(">", indexInit);
		indexTagEnd = indexTagEnd + 1;
		
		String tagContent = strBuilder.substring(indexInit, indexTagEnd);
		
		if (tagContent.contains("label")){
			String headerLabel = this.getParamBy(tagContent, indexInit, "label");
			
			String tagHeaderPattern = "\n<f:facet name=\"header\"><h:outputText value=\"{0}\" /></f:facet>\n";
			String tagHeader = MessageFormat.format(tagHeaderPattern, headerLabel);
			strBuilder.insert(indexTagEnd, tagHeader);
			
			int initLabel = strBuilder.indexOf("label", indexInit);
			int endLabel  = strBuilder.indexOf("\"", initLabel);
			endLabel  = strBuilder.indexOf("\"", endLabel+1);
			endLabel = endLabel + 1;
			if (initLabel>0){
				strBuilder.replace(initLabel, endLabel, "");
			}
		}	
		
		strFile = strBuilder.toString();
		return strFile;
	}




	private String processarDocField(String strFile, int indexInit, String docFields) {
		String tagEnd = "</mec:docField>";
		int indexTagEnd = strFile.indexOf(tagEnd);
		indexTagEnd = indexTagEnd + tagEnd.length();
		
		StringBuilder strBuilder = new StringBuilder(strFile);
		strBuilder.replace(indexInit, indexTagEnd, "");
		
		strFile = strBuilder.toString();
		return strFile;
	}
	
	private String processarSlink(String strFile, int init, String sLink) throws IOException {
		try {
		String sLinkEnd = "</s:link>";
		int end  = strFile.indexOf(sLinkEnd, init);
		end = end + sLinkEnd.length();
		
			String tagLinkFull = strFile.substring(init, end);
		
		
		String newtagLinkFull = new String(tagLinkFull);//NOPMD
		
		newtagLinkFull = newtagLinkFull.replace(sLink, "<h:commandLink");
		newtagLinkFull = newtagLinkFull.replace("includePageParams=\"false\"", "");
		newtagLinkFull = newtagLinkFull.replace("includePageParams=\"true\"", "");
		newtagLinkFull = newtagLinkFull.replace("propagation=\"nest\"", "");
		newtagLinkFull = newtagLinkFull.replace(sLinkEnd, "</h:commandLink>");
		
		String strView = "view";
		if (newtagLinkFull.contains(strView)) {
			int viewInit = newtagLinkFull.indexOf(strView);
			int arqInit = newtagLinkFull.indexOf('\"', viewInit);
			int arqEnd = newtagLinkFull.indexOf('\"', arqInit+1);
			
			String arquivoTarget = newtagLinkFull.substring(arqInit, arqEnd);
			arquivoTarget = arquivoTarget.substring(2);
			
			File file = AjustadorCamadaApresentacao.getArquivoPAGEXML(arquivoTarget);
			
			if(file != null){
				String pageArquivoAlvo = FileUtils.readFileToString(file);
				Map<String, String> mapParam = this.extrairParametrosPage(pageArquivoAlvo);
				
				String strParam = "<f:param";
				if (newtagLinkFull.contains(strParam)) {
					int count = StringUtils.countMatches(newtagLinkFull, strParam);
					
					int indexInit = 0;
					for (int i = 0; i < count; i++) {
						indexInit = newtagLinkFull.indexOf(strParam, indexInit);
						newtagLinkFull = this.substituirParametros(newtagLinkFull, indexInit, mapParam);
						indexInit = indexInit + 10;
					}
				}
			}
			
			String strViewReplace = newtagLinkFull.substring(viewInit, arqEnd+1);
			newtagLinkFull = newtagLinkFull.replace(strViewReplace, "");
			
			StringBuilder viewReplace = new StringBuilder("\n<!-- ");
			viewReplace.append(strViewReplace).append(" -->\n");
			StringBuilder builder = new StringBuilder(newtagLinkFull);
			builder.insert((newtagLinkFull.indexOf(">")+1), viewReplace.toString());
			
			newtagLinkFull = builder.toString();
		}//view
		
		strFile = strFile.replace(tagLinkFull, newtagLinkFull);
		
		} catch (StringIndexOutOfBoundsException e) {
			Log.error(e.getMessage(),e);
		}		
		return strFile;
	}

	private String processarSbutton(String strFile, int init, String sButton) throws IOException {
		String sButtonEnd = "</s:button>";
		String sButtonNewEnd="</h:commandButton>";
		int end  = strFile.indexOf(sButtonEnd, init);
		if (end<0) {
			sButtonEnd="/>";
			sButtonNewEnd="></h:commandButton>";
			end=strFile.indexOf(sButtonEnd, init);
		}
		end = end + sButtonEnd.length();	
		
		String tagButtonFull = strFile.substring(init, end);
		String newtagButtonFull = new String(tagButtonFull);//NOPMD
		
		newtagButtonFull = newtagButtonFull.replace(sButton, "<h:commandButton");
		newtagButtonFull = newtagButtonFull.replace("includePageParams=\"false\"", "");
		newtagButtonFull = newtagButtonFull.replace("includePageParams=\"true\"", "");
		newtagButtonFull = newtagButtonFull.replace("propagation=\"nest\"", "");
		newtagButtonFull = newtagButtonFull.replace(sButtonEnd, sButtonNewEnd);
		
		String strView = "view";
		if (newtagButtonFull.contains(strView)) {
			int viewInit = newtagButtonFull.indexOf(strView);
			int arqInit = newtagButtonFull.indexOf('\"', viewInit);
			int arqEnd = newtagButtonFull.indexOf('\"', arqInit+1);
			
			String arquivoTarget = newtagButtonFull.substring(arqInit, arqEnd);
			arquivoTarget = arquivoTarget.substring(2);
			
			File file = AjustadorCamadaApresentacao.getArquivoPAGEXML(arquivoTarget);
			if (file != null) {
				String pageArquivoAlvo = FileUtils.readFileToString(file);
				Map<String, String> mapParam = this.extrairParametrosPage(pageArquivoAlvo);
				
				String strParam = "<f:param";
				if (newtagButtonFull.contains(strParam)) {
					int count = StringUtils.countMatches(newtagButtonFull, strParam);
					
					int indexInit = 0;
					for (int i = 0; i < count; i++) {
						indexInit = newtagButtonFull.indexOf(strParam, indexInit);
						newtagButtonFull = this.substituirParametros(newtagButtonFull, indexInit, mapParam);
						indexInit = indexInit + 10;
					}
				}
				
				String strViewReplace = newtagButtonFull.substring(viewInit, arqEnd+1);
				newtagButtonFull = newtagButtonFull.replace(strViewReplace, "");
				
				StringBuilder viewReplace = new StringBuilder("\n<!-- ");
				viewReplace.append(strViewReplace).append(" -->\n");
				StringBuilder builder = new StringBuilder(newtagButtonFull);
				builder.insert((newtagButtonFull.indexOf(">")+1), viewReplace.toString());
				
				newtagButtonFull = builder.toString();
			}// if file pageXML
		}//view
		
		strFile = strFile.replace(tagButtonFull, newtagButtonFull);
		
		return strFile;
	}




	private String substituirParametros(String strFile, int indexInit, Map<String, String> mapParam) {
		int end = strFile.indexOf("/>", indexInit);
		end = end + 2;
		String tag = strFile.substring(indexInit, end);
		String replacement = new String(tag);//NOPMD
		
		String paramKey = this.getParamBy(tag, indexInit, "name");
		//String paramValue = this.getParamBy(tag, indexInit, "value");
		String paramValueReplacement = mapParam.get(paramKey);
		
		if(paramKey!= null && paramValueReplacement!=null){
			replacement = replacement.replace("param", "setPropertyActionListener");
			replacement = replacement.replace("name", "target");
			replacement = replacement.replace(paramKey, paramValueReplacement);
		}
		
		strFile = strFile.replace(tag, replacement);
		
		return strFile;
	}




	private Map<String, String> extrairParametrosPage(String pageArquivoAlvo) {
		final Map<String, String> mapParam = new HashMap<>();
		
		String strParam = "<param";
		if (pageArquivoAlvo.contains(strParam)) {
			int count = StringUtils.countMatches(pageArquivoAlvo, strParam);
			
			int indexInit = 0;
			for (int i = 0; i < count; i++) {
				indexInit = pageArquivoAlvo.indexOf(strParam, indexInit);
				this.doExtrairParam(pageArquivoAlvo, indexInit, mapParam);
				indexInit = indexInit + 10;
			}
		}
		
		return mapParam;
	}
	
	private void doExtrairParam(String pageArquivoAlvo, int indexInit, Map<String, String> mapParam) {
		String paramKey = this.getParamBy(pageArquivoAlvo, indexInit, "name");
		String paramValeu = this.getParamBy(pageArquivoAlvo, indexInit, "value");
		
		if (paramValeu != null && paramValeu.contains("#")) {
			mapParam.put(paramKey, paramValeu);
		}
	}
	
	private String getParamBy(String pageArquivoAlvo, int indexInit, String type) {
		int indexNameInit = pageArquivoAlvo.indexOf(type, indexInit);
		indexNameInit = pageArquivoAlvo.indexOf('\"', indexNameInit);
		indexNameInit = indexNameInit + 1;
		int indexNamaEnd = pageArquivoAlvo.indexOf('\"', indexNameInit);

		return pageArquivoAlvo.substring(indexNameInit, indexNamaEnd);
	}
	
	private String pegarMapaNavegacao(File arquivoPAGEXML) throws IOException {
		String navegacao = null;
		String strFile = FileUtils.readFileToString(arquivoPAGEXML);
		
		String tagInit = "<navigation";
		if (strFile.contains(tagInit)) {
			String tagEnd = "</navigation>";
			int sizeTagEnd = tagEnd.length();
			
			int beginIndex = strFile.indexOf(tagInit);
			int endIndex = strFile.indexOf(tagEnd);
			navegacao = strFile.substring(beginIndex, (endIndex+sizeTagEnd));
		}
		
		return navegacao;
	}

}

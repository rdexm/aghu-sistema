package br.gov.mec.aghu.core.merges;

import java.util.Arrays;
import java.util.List;

/**
 * Modificacao feita no SVN.
 * 
 * Deve ser um path para um arquivo. Ou seja, deve conter ponto
 * 
 * @author rcorvalao
 *
 */
public class ChangeItem {
	
	private static final List<String> MODULES_UNACCEPTABLE = Arrays.asList("dao", "business", "resources", "util", "action"); 
	private static final List<String> MODULES_UNACCEPTABLE_ENTITY = Arrays.asList("dominio", "model"); 

	
	private static final String MODULO_ID_RESOURCES = "src/main/resources";
	private static final String MODULO_ID_JASPER = "src/main/jasperreports";
	private static final String MODULO_ID_JAVA = "src/main/java";
	private static final String MODULO_ID_TEST = "src/test/java";
	private static final String MODULO_ID_WEB = "src/main/webapp";
	private static final String MODULO_ID_PACKAGE = "br/gov/mec/aghu";
	private static final String MODULO_ID_PACKAGE_SEAM = "br/gov/mec/seam";
	private static final String MODULO_ID_AGHU = "/aghu/";
	private static final String MODULO_ID_AGHU_ENTITY = "aghu_entidades";
	
	
	/**
	 * Verifica se o conteudo da mudança tem um ponto para indicar que eh um arquivo.
	 *  
	 * @param content
	 * @return
	 */
	public static boolean isArquivo(String content) {
		return (content != null && content.contains("."));
	}
	
	
	private String content;
	
	private String tipo;
	private String arquivo;
	
	private MergeItem mergeItem;
	private String module;

	public ChangeItem(String content, MergeItem parent) {
		super();
		if (content == null || "".equals(content.trim())) {
			throw new IllegalArgumentException("ChangeItem nao pode ser inicia com string nula ou vazia.");
		}
		if (!isArquivo(content)) {
			throw new IllegalArgumentException("ChangeItem deve conter um ponto para indicar path de arquivo.");			
		}
		this.setContent(content);
		this.setMergeItem(parent);
		
		setTipo(getContent().substring(0, 1).trim());
		
		
		setArquivo(getContent().substring(1, getContent().length()).trim());
		
	}

	private String getContent() {
		return content;
	}

	private void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * Indica se o arquivo foi Adicionado, Modificado, Deletado
	 * 
	 * @return
	 */
	public String getTipo() {
		return tipo;
	}

	private void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	/**
	 * Nome do arquivo da Modificacao
	 * @return
	 */
	public String getArquivo() {
		return arquivo;
	}

	private void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public MergeItem getMergeItem() {
		return mergeItem;
	}

	public void setMergeItem(MergeItem mergeItem) {
		this.mergeItem = mergeItem;
	}
	
	public String getModule() {
		if (module == null) {
			if (this.getArquivo().endsWith("pom.xml")) {
				module = "pom";
			} //IF
			else if (this.getArquivo().contains(MODULO_ID_WEB)) {
				module = doModuleName(this, MODULO_ID_WEB);
			}//IF
			else if (this.getArquivo().contains(MODULO_ID_JAVA) 
					|| this.getArquivo().contains(MODULO_ID_TEST)
					|| this.getArquivo().contains(MODULO_ID_RESOURCES)
					|| this.getArquivo().contains(MODULO_ID_JASPER)
					) {
				if (this.getArquivo().contains(MODULO_ID_PACKAGE)) {
					module = doModuleName(this, MODULO_ID_PACKAGE);
				} else if (this.getArquivo().contains(MODULO_ID_PACKAGE_SEAM)) {
					module = doModuleName(this, MODULO_ID_PACKAGE_SEAM);
				}
			}//IF
			
			// caso nao encontre os padroes anteriores
			if (module == null) {
				if (this.getArquivo().contains(MODULO_ID_AGHU)) {
					module = extractInfo(this.getArquivo(), MODULO_ID_AGHU);
				} else {
					module = "modulonegocial";
				}
			}
			
			if (module == null) {
				module = "moduloindefinido";
			}
		}//IF
		
		return module;
	}
	
	private String extractInfo(String content, String strPatternInit) {
		String returnValue = null;
		
		int indexIni = content.indexOf(strPatternInit);
		if (indexIni != -1) {
			int indexVar = 1;
			if (strPatternInit.endsWith(MergeItem.SEPARADOR_PASTA)) {
				indexVar = 0;
			}
			indexIni = indexIni + strPatternInit.length() + indexVar;
			int indexFim = content.indexOf(MergeItem.SEPARADOR_PASTA, indexIni);
			if (indexFim != -1) {
				returnValue = content.substring(indexIni, indexFim);
			}	
		}
		
		return returnValue;
	}
	
	private String doModuleName(ChangeItem changeItem, String strInit) {
		String module = extractInfo(changeItem.getArquivo(), strInit);
		if (module == null) {
			module = extractInfo(changeItem.getArquivo(), MODULO_ID_AGHU);
			if (module == null) {
				module = "raiz";				
			}
		}
		
		if (MODULES_UNACCEPTABLE_ENTITY.contains(module)) {
			if (changeItem.getArquivo().contains(MODULO_ID_AGHU_ENTITY)) {
				module = MODULO_ID_AGHU_ENTITY;
			} else {
				module = "modules-unacceptable-entity";
			}
		}
		
		if (MODULES_UNACCEPTABLE.contains(module)) {
			module = extractInfo(changeItem.getArquivo(), MODULO_ID_AGHU);
			if (module == null) {
				module = "modules-unacceptable";
			}
		}
		
		return module;
	}	
	
	
	
	public static String header() {
		return "" //NOPMD
			+ MergeItem.SEPARADOR_INFO + "FileName"//NOPMD 
			+ MergeItem.SEPARADOR_INFO + "Type" //NOPMD
			+ MergeItem.SEPARADOR_INFO + "" //NOPMD
			+ MergeItem.SEPARADOR_INFO + ""//NOPMD
			+ MergeItem.SEPARADOR_INFO + ""//NOPMD
			;//NOPMD
	}
	
	@Override
	public String toString() {
		return "" //NOPMD
				+ MergeItem.SEPARADOR_INFO + getArquivo()//NOPMD 
				+ MergeItem.SEPARADOR_INFO + getTipo()//NOPMD
				+ MergeItem.SEPARADOR_INFO + "" //NOPMD
				+ MergeItem.SEPARADOR_INFO + ""//NOPMD
				+ MergeItem.SEPARADOR_INFO + ""//NOPMD
				; //NOPMD
	}


}

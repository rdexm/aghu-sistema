package br.gov.mec.aghu.core.merges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MergeItem implements Comparable<MergeItem>{
	
	public static final String CHANGE_PATHS = "Changed paths:";
	public static final List<String> CHANGE_PATHS_FIM = Arrays.asList("- r", "#");
	
	public static final String CHANGE_ITEM_ID = "/aghu-core";
	public static final String CHANGE_ITEM_ID_FIM = "\n";
	
	public static final String RESPONSAVEL_ROTINAS_AUTOMATICAS = "aghu_rotinas";

	public static final String SEPARADOR_INFO = ";";
	public static final String SEPARADOR_PASTA = "/";
	
	private String revison;
	private String content;
	private String user;
	private String userOriginal;
	private Date date;
	
	private List<ChangeItem> validChangeItems;
	private SortedSet<String> modules;
	

	public MergeItem(String content) {
		super();
		if (content == null || "".equals(content.trim())) {
			throw new IllegalArgumentException("MergeItem nao pode ser inicia com string nula ou vazia.");
		}
		this.setContent(content);
	}

	private String getContent() {
		return content;
	}

	public Date getDate() {
		if (this.date == null) {
			setDate(this.doMakeDate());
		}
		return this.date;
	}

	private Date doMakeDate() {
		int indexDate = getContent().indexOf(this.getUser());
		indexDate = getContent().indexOf('|', indexDate);
		indexDate = indexDate + 1;
		int indexDateEnd = getContent().indexOf('\n', indexDate);
		
		String str = getContent().substring(indexDate, indexDateEnd);
		String strDate = str != null ? str.trim() : str;
		
		Date makeDate = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			makeDate = format.parse(strDate);
		} catch (ParseException e) {
			makeDate = null;
		}
		
		return makeDate;
	}

	private void setDate(Date date) {
		this.date = date;
	}

	private void setContent(String content) {
		this.content = content;
	}
	
	public String getRevison() {
		if (revison == null) {
			int indexRev = getContent().indexOf('r'); 
			int indexRevEnd = getContent().indexOf('|', indexRev); 
			
			String str = getContent().substring(indexRev, indexRevEnd);
			revison = str != null ? str.trim() : str;
		}
		return revison;
	}
	
	public Integer getRevisionAsInteger() {
		String str = this.getRevison();
		return Integer.valueOf(str.substring(1));
	}
	
	public String getUser() {
		if (user == null) {
			int indexRev = getContent().indexOf('|'); 
			indexRev = indexRev + 1;
			int indexRevEnd = getContent().indexOf('|', indexRev); 
			
			String str = getContent().substring(indexRev, indexRevEnd);
			user = str != null ? str.trim() : str;
		}
		
		return user;
	}
	
	public String getUserOriginal() {
		if (userOriginal == null) {
			String respOriginal = this.getUser();
			if (RESPONSAVEL_ROTINAS_AUTOMATICAS.equalsIgnoreCase(this.getUser())) {
				try {
					String strChanges = getStringChanges();
					int indexRespIni = strChanges.lastIndexOf('\n');
					
					respOriginal = strChanges.substring(indexRespIni, strChanges.length());
					if (respOriginal != null) {
						respOriginal = respOriginal.trim();
					}
				} catch (Exception e) {
					respOriginal = "indefinido";
				}
			}
			userOriginal = respOriginal;
		}
		
		return userOriginal;
	}
	
	/**
	 * Gera uma lista contendo apenas items que representem arquivos modificados.
	 * 
	 * @return
	 */
	public List<ChangeItem> getValidChangeItems() {
		if (validChangeItems == null) {
			validChangeItems = makeValidChangeItems();
		}
		
		return validChangeItems;
	}
	
	public boolean hasValidChanges() {
		return !this.getValidChangeItems().isEmpty();
	}
	
	public SortedSet<String> getModules() {
		if (modules == null) {
			modules = makeModules();
		}
		
		return modules;
	}

	private SortedSet<String> makeModules() {
		SortedSet<String> listaOrdenada = new TreeSet<>();
		
		for (ChangeItem changeItem : getValidChangeItems()) {
			String module = changeItem.getModule();
			
			if (module != null) {
				listaOrdenada.add(module.toLowerCase());
			} else {
				System.out.println("problema..." + changeItem.getArquivo());//NOPMD
			}
		}//FOR
		
		if (listaOrdenada.isEmpty()) {
			listaOrdenada.add("moduloindefinido");
		}
		
		return listaOrdenada;
	}
	
	private List<ChangeItem> makeValidChangeItems() {
		List<ChangeItem> lista = new LinkedList<>();
		
		List<String> strLista = getChanges();
		for (String change : strLista) {
			if (ChangeItem.isArquivo(change)) {
				lista.add(new ChangeItem(change, this));
			}
		}//FOR
		
		return lista;
	}
	
	private String getStringChanges() {
		int indexPaths = getContent().indexOf(CHANGE_PATHS);
		indexPaths = indexPaths + CHANGE_PATHS.length();
		int indexPathsFim = getIndexPathsFim(indexPaths);
		String strChanges = getContent().substring(indexPaths, indexPathsFim);
		return strChanges;
	}
	
	/**
	 * Metodo responsavel por extrair a lista de Modificacoes
	 * @return
	 */
	private List<String> getChanges() {
		List<String> listChanges = new LinkedList<>();
		
		String strChanges = getStringChanges();
		
		int indexItemInicio;
		int indexItemFim;
		int indexTemp;
		int indexItem = 0;
		int fim = strChanges.length();
		
		while (indexItem < fim) {
			indexTemp = strChanges.indexOf(CHANGE_ITEM_ID, indexItem);
			if (indexTemp != -1) {
				indexItem = indexTemp; 
				indexItemInicio = indexItem - 2;
				
				indexItemFim = strChanges.indexOf(CHANGE_ITEM_ID_FIM, indexItemInicio);
				
				String strItem = strChanges.substring(indexItemInicio, indexItemFim);
				listChanges.add(strItem);
			}
			
			// Passo para possibilitar pegar o proximo item.
			indexItem = indexItem + 10;
		}//While
		
		return listChanges;
	}
	
	/**
	 * Procura por '- r' ou '#' depois do 'Changed paths:'
	 * Para finalizar a lista de mudanças.
	 * 
	 * @param indexPaths
	 * @return
	 */
	private int getIndexPathsFim(int indexPaths) {
		int indexReturn = 0;
		
		for (String strPathsFim : CHANGE_PATHS_FIM) {
			int indexFim = getContent().indexOf(strPathsFim, indexPaths);
			if (indexFim != -1) {
				indexReturn = indexFim;
				break;
			}
		}
		
		return indexReturn;
	}
	
	public static String header() {
		return "Revision"
			+ SEPARADOR_INFO + "User" 
			+ SEPARADOR_INFO + "UserOriginal" 
			+ SEPARADOR_INFO + "HasValidChanges" 
			+ SEPARADOR_INFO + "ValidChangessize"
			+ SEPARADOR_INFO + "Modules"
			;//NOPMD
	}
	
	@Override
	public String toString() {
		return getRevison() 
				+ SEPARADOR_INFO + getUser() 
				+ SEPARADOR_INFO + getUserOriginal()
				+ SEPARADOR_INFO + hasValidChanges() 
				+ SEPARADOR_INFO + getValidChangeItems().size()
				+ SEPARADOR_INFO + this.getModules()
				; //NOPMD
	}

	@Override
	public int compareTo(MergeItem o) {
		return this.getRevisionAsInteger().compareTo(o.getRevisionAsInteger());
	}

}

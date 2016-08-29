package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.io.FileUtils;

public class RevisionAnalyzerMain {
	
	
	
	// Arquivo de entrada - change log gerado pelo eclipse.
	private static final String revisionsFileName = "/opt/merges-aghu/compare/change-log-20140715";
	private static final String revisionsFileNameAghu = revisionsFileName + "-aghu.txt";
	private static final String revisionsFileNameEntidades = revisionsFileName + "-entidades.txt";
	// Arquivo de saida
	private static final String mergeReportFileName = "/opt/merges-aghu/RelatorioGeral.csv";
	
	//private static final String mergeAlfa1FileName  = "/opt/merges-aghu/merges-aghu-relatorio-alfa1.csv";
	//private static final String mergeResponsavelFileName  = "/opt/merges-aghu/merges-aghu-relatorio-responsaveis.csv";
	//private static final String mergeRevisionsPorModulosFileName = "/opt/merges-aghu/merges-aghu-relatorio-revisions-por-modulo.csv";
	
	private static final String mergeMudancasFileName = "/opt/merges-aghu/";
	
	
	public static void main(String[] args) {
		System.out.println("RevisionAnalyzerMain ..."); //NOPMD
		
		//verificarFuncaoSortedSetAdd();
		revisionAnalyzerMethod();
		
		System.out.println("RevisionAnalyzerMain."); //NOPMD
	}


	public static void verificarFuncaoSortedSetAdd() {
		SortedSet<String> listaOrdenada = new TreeSet<>();
		
		listaOrdenada.add("rafael");
		listaOrdenada.add("Rafael");
		listaOrdenada.add("Rafael1");
		listaOrdenada.add("rafael1");
		listaOrdenada.add("Rafael2".toLowerCase());
		listaOrdenada.add("Rafael1");
		listaOrdenada.add("Rafael");
		listaOrdenada.add("Rafael2".toLowerCase());
		listaOrdenada.add("rafael2");
		
		System.out.println(listaOrdenada);//NOPMD
	}
	
	
	public static void revisionAnalyzerMethod() {
		
		try {
			// Gera a lista de revisions e suas mudancas.
			System.out.println("Extracao das merge info do change log.");//NOPMD
			System.out.println("-----------------------------------");//NOPMD
			GenerateMergeItems generateMergeItemsAghu = new GenerateMergeItems();
			generateMergeItemsAghu.execute(revisionsFileNameAghu);
			List<MergeItem> mergeItemsAghu = generateMergeItemsAghu.getMergeItems();
			
			GenerateMergeItems generateMergeItemsEntidades = new GenerateMergeItems();
			generateMergeItemsEntidades.execute(revisionsFileNameEntidades);
			List<MergeItem> mergeItemsEntidades = generateMergeItemsEntidades.getMergeItems();
			System.out.println("-----------------------------------");//NOPMD
			
			System.out.println("Valores brutos dos 2 change log");//NOPMD
			System.out.println("-----------------------------------");//NOPMD
			System.out.println("mergeItemsEntidades " + mergeItemsEntidades.size());//NOPMD
			System.out.println("mergeItemsAghu " + mergeItemsAghu.size());//NOPMD
			System.out.println("-----------------------------------" + (mergeItemsEntidades.size() + mergeItemsAghu.size()));//NOPMD
			
			List<String> aghuRev = (List<String>)
				CollectionUtils.collect(mergeItemsAghu, new Transformer() {
					@Override
					public Object transform(Object item) {
						MergeItem mergeItem = (MergeItem) item;
						return mergeItem.getRevison();
					}
				});
			
			List<String> entidadeRev = (List<String>)
					CollectionUtils.collect(mergeItemsEntidades, new Transformer() {
						@Override
						public Object transform(Object item) {
							MergeItem mergeItem = (MergeItem) item;
							return mergeItem.getRevison();
						}
					});
			
			System.out.println("-----------------------------------");//NOPMD
			System.out.println("entidadeRev " + entidadeRev.size());//NOPMD
			System.out.println("aghuRev " + aghuRev.size());//NOPMD
			System.out.println("----------------------------------- " + (entidadeRev.size() + aghuRev.size()));//NOPMD

			System.out.println("-----------------------------------");//NOPMD
			System.out.println("Valores eliminando as revisions repetidas do entidades:");//NOPMD
			System.out.println("-----------------------------------");//NOPMD
			entidadeRev.removeAll(aghuRev);
			System.out.println("entidadeRev " + entidadeRev.size());//NOPMD
			System.out.println("aghuRev " + aghuRev.size());//NOPMD
			System.out.println("-----------------------------------" + (entidadeRev.size() + aghuRev.size()));//NOPMD
			
			SortedSet<MergeItem> mergeItems = new TreeSet<>();
			mergeItems.addAll(mergeItemsAghu);
			mergeItems.addAll(mergeItemsEntidades);
			System.out.println("Lista de mergeItems mergeItems:");//NOPMD
			System.out.println("-----------------------------------");//NOPMD
			System.out.println("mergeItems: " + mergeItems.size());//NOPMD
			System.out.println("-----------------------------------");//NOPMD
			
			
			GenereteBasicMergeReport genereteBasicMergeReport = new GenereteBasicMergeReport(revisionsFileName);
			genereteBasicMergeReport.execute(mergeReportFileName, mergeItems);
			
			GenerateMudancaMergeReport generateMudancaMergeReport = new GenerateMudancaMergeReport(revisionsFileName);
			generateMudancaMergeReport.execute(mergeMudancasFileName+"ListaGeral", mergeItems);

			GenerateMudancaPorModuloMergeReport generateMudancaPorModuloMergeReport = new GenerateMudancaPorModuloMergeReport(revisionsFileName);
			generateMudancaPorModuloMergeReport.execute(mergeMudancasFileName+"Resumo", mergeItems);
			
			GenerateMudancaPorModuloTotalMergeReport generateMudancaPorModuloTotalMergeReport = new GenerateMudancaPorModuloTotalMergeReport(revisionsFileName);
			generateMudancaPorModuloTotalMergeReport.execute(mergeMudancasFileName+"M", mergeItems);
			
			//GenereteAlfa1MergeReport genereteAlfa1MergeReport = new GenereteAlfa1MergeReport(revisionsFileName);
			//genereteAlfa1MergeReport.execute(mergeAlfa1FileName, mergeItems);
			
			//GenereteResponsavelMergeReport genereteResponsavelMergeReport = new GenereteResponsavelMergeReport();
			//genereteResponsavelMergeReport.execute(mergeResponsavelFileName, mergeItems);
			
			//GenerateRevisionsPorModuloMergeReport generateRevisionsPorModuloMergeReport = new GenerateRevisionsPorModuloMergeReport();
			//generateRevisionsPorModuloMergeReport.execute(mergeRevisionsPorModulosFileName, mergeItems);

			
			//revisionAnalyzerOthers(mergeItems);
		} catch (IOException e) {
			e.printStackTrace();//NOPMD
		}
		
	}


	protected static void revisionAnalyzerOthers(List<MergeItem> mergeItems) throws IOException {
		Map<String, List<ChangeItem>> mapChangeItem = new HashMap<>();
		for (MergeItem mergeItem : mergeItems) {
			List<ChangeItem> changeItems = mergeItem.getValidChangeItems();
			for (ChangeItem changeItem : changeItems) {
				List<ChangeItem> list; 
				if (mapChangeItem.containsKey(changeItem.getTipo())) {
					list = mapChangeItem.get(changeItem.getTipo());
				} else {
					list = new LinkedList<>();
					mapChangeItem.put(changeItem.getTipo(), list);
				}
				list.add(changeItem);
			}
		}
		
		File listFileName = new File("/opt/merges-aghu/merges-aghu-filenames.txt");
		StringBuilder resultFileNames = new StringBuilder();
		SortedSet<String> fileNamesOrder = new TreeSet<>();
		int countChangeItens = 0;
		
		String strFileNameCut;
		Set<String> listKeys = mapChangeItem.keySet();
		for (String key : listKeys) {
			List<ChangeItem> list = mapChangeItem.get(key);
			countChangeItens = countChangeItens + list.size();
			resultFileNames.append(key).append(": ").append(list.size()).append(" ").append(GenereteBasicMergeReport.SEPARATOR_LINHA);
			for (ChangeItem changeItem : list) {
				int indexT = changeItem.getArquivo().indexOf("/aghu_seguranca/");
				if (indexT != -1) {
					strFileNameCut = changeItem.getArquivo().substring(indexT);
					resultFileNames.append(strFileNameCut).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
					fileNamesOrder.add(strFileNameCut);
				} else {
					indexT = changeItem.getArquivo().indexOf("/aghu_entidades/");
					if (indexT != -1) {
						strFileNameCut = changeItem.getArquivo().substring(indexT);
						resultFileNames.append(changeItem.getArquivo().substring(indexT)).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
						fileNamesOrder.add(strFileNameCut);
					} else {
						indexT = changeItem.getArquivo().indexOf("/aghu/");
						if (indexT != -1) {
							strFileNameCut = changeItem.getArquivo().substring(indexT);
							resultFileNames.append(strFileNameCut).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
							fileNamesOrder.add(strFileNameCut);
						} else {
							strFileNameCut = changeItem.getArquivo().substring(indexT);
							resultFileNames.append(strFileNameCut).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
							fileNamesOrder.add(strFileNameCut);
						}
					}
				}
			}
		}
		resultFileNames.append("Total de Modificacoes: ").append(countChangeItens).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
		
		FileUtils.writeStringToFile(listFileName, resultFileNames.toString());
		
		
		File listFileNameUnique = new File("/opt/merges-aghu/merges-arquivos-unicos.txt");
		StringBuilder listFileNameUniqueStr = new StringBuilder();
		for (String fileName : fileNamesOrder) {
			listFileNameUniqueStr.append(fileName).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
		}
		listFileNameUniqueStr.append("Total de Arquivos: ").append(fileNamesOrder.size()).append(GenereteBasicMergeReport.SEPARATOR_LINHA);
		FileUtils.writeStringToFile(listFileNameUnique, listFileNameUniqueStr.toString());
	}

}

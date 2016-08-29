package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

public class GenerateMudancaPorModuloMergeReport extends GenerateMudancaMergeReport {

	public GenerateMudancaPorModuloMergeReport(String fileName) {
		super(fileName);
	}
	
	
	@Override
	public void execute(String mergeReportFileName, Collection<MergeItem> mergeItems) throws IOException {
		List<ChangeItem> mudancasList = this.getMudancas(mergeItems);
		
		Map<String, List<ChangeItem>> mapMudancasPorModulos = getMudancasPorModulo(mudancasList);
		
		List<String> listaModulos = getListaModulosOrdenada(mapMudancasPorModulos);
		
		List<ChangeItem> itens = null;
		StringBuilder strModulos = new StringBuilder();
		strModulos.append("Modulo").append(MergeItem.SEPARADOR_INFO);
		strModulos.append("Qt Mudancas").append(SEPARATOR_LINHA);
		for (String moduleName : listaModulos) {
			itens = mapMudancasPorModulos.get(moduleName);
			
			strModulos.append(moduleName).append(MergeItem.SEPARADOR_INFO);
			strModulos.append(itens.size()).append(SEPARATOR_LINHA);
		}//FOR listaModulos
		
		SortedSet<String> totalRevisions = new TreeSet<>();
		for (ChangeItem changeItem : mudancasList) {
			totalRevisions.add(changeItem.getMergeItem().getRevison());
		}//FOR mudancasList
		
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append(RODAPE).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Total Revisions: ").append(totalRevisions.size()).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Total Mudancas: ").append(mudancasList.size()).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Total Modulos: ").append(listaModulos.size()).append(SEPARATOR_LINHA);
		
				
		//super.execute(mergeReportFileName, mergeItems);
		File arquivoMergeRelatorio = new File(getFileNameCsv(mergeReportFileName));
		FileUtils.writeStringToFile(arquivoMergeRelatorio, strModulos.toString());
	}


	protected List<String> getListaModulosOrdenada(Map<String, List<ChangeItem>> mapMudancasPorModulos) {
		Set<String> nomeModulos = mapMudancasPorModulos.keySet();
		List<String> listaModulos = new ArrayList<>(nomeModulos.size());
		listaModulos.addAll(nomeModulos);
		Collections.sort(listaModulos);
		return listaModulos;
	}
	
	protected Map<String, List<ChangeItem>> getMudancasPorModulo(List<ChangeItem> mudancasList) {
		Map<String, List<ChangeItem>> mapMudancasPorModulos = new HashMap<>();
		SortedSet<String> totalRevisions = new TreeSet<>();
		
		List<ChangeItem> itens = null;
		for (ChangeItem changeItem : mudancasList) {
			String modulo = changeItem.getModule();
			
			if (mapMudancasPorModulos.containsKey(modulo)) {
				itens = mapMudancasPorModulos.get(modulo);
			} else {
				itens = new LinkedList<>();
				mapMudancasPorModulos.put(modulo, itens);
			}
			
			itens.add(changeItem);
			
			totalRevisions.add(changeItem.getMergeItem().getRevison());
		}//FOR mudancasList
		
		return mapMudancasPorModulos;
	}

}

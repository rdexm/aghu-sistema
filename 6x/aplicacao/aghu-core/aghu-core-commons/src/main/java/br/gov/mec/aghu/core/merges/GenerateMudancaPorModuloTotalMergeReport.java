package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

public class GenerateMudancaPorModuloTotalMergeReport extends GenerateMudancaPorModuloMergeReport {

	public GenerateMudancaPorModuloTotalMergeReport(String fileName) {
		super(fileName);
	}
	
	@Override
	public void execute(String mergeReportFileName, Collection<MergeItem> mergeItems) throws IOException {
		List<ChangeItem> mudancasList = this.getMudancas(mergeItems);
		
		Map<String, List<ChangeItem>> mapMudancasPorModulos = getMudancasPorModulo(mudancasList);
		
		List<String> listaModulos = getListaModulosOrdenada(mapMudancasPorModulos);
		
		for (String modulo : listaModulos) {
			buildModulo(mergeReportFileName, modulo, mapMudancasPorModulos.get(modulo));
		}//FOR listaModulos
		
		
	}
	
	private void buildModulo(String mergeReportFileName, String modulo, List<ChangeItem> mudancasList) throws IOException {
		StringBuilder strModulos = new StringBuilder();
		ShowShortChangeItemBuilder builder = new ShowShortChangeItemBuilder();
		
		this.ordenarListaMudancas(mudancasList);
		
		strModulos.append(builder.getHeader()).append(SEPARATOR_LINHA);
		for (ChangeItem change : mudancasList) {
			strModulos.append(builder.build(change)).append(SEPARATOR_LINHA);
		}
		
		Map<String, Integer> mapRevisionMaiorMenor = getMapRevisionMaiorMenor(mudancasList);
		
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append(RODAPE).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("ChangeLog Origem: ").append(this.getBaseFileName()).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Range de Revisions: ").append(mapRevisionMaiorMenor.get("MIN")).append(" <-> ").append(mapRevisionMaiorMenor.get("MAX")).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Qt mudanças: ").append(mudancasList.size()).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Modulo: ").append(modulo).append(SEPARATOR_LINHA);
		
		String moduleFileName = mergeReportFileName + modulo;
		System.out.println(moduleFileName);//NOPMD
		File arquivoMergeRelatorio = new File(this.getFileNameCsv(moduleFileName));
		FileUtils.writeStringToFile(arquivoMergeRelatorio, strModulos.toString());
			
	}
	

}

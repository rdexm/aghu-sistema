package br.gov.mec.aghu.patrimonio.vo;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

public class AvaliacaoTecnicaVOComparator {
	
	public static class OrderByRecebItemFormatado implements Comparator<AvaliacaoTecnicaVO>{

		@Override
		public int compare(AvaliacaoTecnicaVO arg0, AvaliacaoTecnicaVO arg1) {
			String descricao1 = StringUtils.trimToEmpty(arg0.getRecebItemFormatado());
			String descricao2 = StringUtils.trimToEmpty(arg1.getRecebItemFormatado());
			descricao1 = descricao1.replaceAll("/", "");
			descricao2 = descricao2.replaceAll("/", "");
			Integer receb1 = null;
			Integer receb2 = null;
			if(descricao1.isEmpty()){
				receb1 = 0;
			}else{
				receb1 = Integer.valueOf(descricao1);
			}
			
			if(descricao2.isEmpty()){
				receb2 = 0;
			}else{
				receb2 = Integer.valueOf(descricao2);
			}
			
			return receb1.compareTo(receb2);
		}
		
	}
	
	public static class OrderByPatrimonioTruncado implements Comparator<AvaliacaoTecnicaVO>{

		@Override
		public int compare(AvaliacaoTecnicaVO arg0, AvaliacaoTecnicaVO arg1) {
			
			String descricao1 = StringUtils.trimToEmpty(arg0.getPatrimonioTruncado());
			String descricao2 = StringUtils.trimToEmpty(arg1.getPatrimonioTruncado());

			descricao1 = descricao1.replaceAll(",", "");
			descricao2 = descricao2.replaceAll(",", "");
			
			descricao1 = descricao1.replaceAll(" ", "");
			descricao2 = descricao2.replaceAll(" ", "");
			
			descricao1 = descricao1.replaceAll("\\.", ""); 
			descricao2 = descricao2.replaceAll("\\.", "");
			
			Long receb1 = null;
			Long receb2 = null;
			if(descricao1.isEmpty()){
				receb1 = 0L;
			}else{
				receb1 = Long.valueOf(descricao1);
			}
			
			if(descricao2.isEmpty()){
				receb2 = 0L;
			}else{
				receb2 = Long.valueOf(descricao2);
			}
			
			return receb1.compareTo(receb2);
		}
		
	}

}

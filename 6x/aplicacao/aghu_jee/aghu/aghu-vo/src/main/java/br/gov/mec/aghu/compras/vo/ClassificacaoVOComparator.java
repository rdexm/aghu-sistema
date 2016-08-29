package br.gov.mec.aghu.compras.vo;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class ClassificacaoVOComparator {
	
	public static class OrderByDescricao implements Comparator<ClassificacaoVO> {

        @Override
        public int compare(ClassificacaoVO o1, ClassificacaoVO o2) {
        	final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			
			String descricao1 = StringUtils.trimToEmpty(o1.getDescricao());
			String descricao2 = StringUtils.trimToEmpty(o2.getDescricao());
			    
			return collator.compare(descricao1, descricao2);	
        }
    }
	
	public static class OrderByCodigo implements Comparator<ClassificacaoVO> {

        @Override
        public int compare(ClassificacaoVO o1, ClassificacaoVO o2) {
        	Long codigo1 = o1.getCodigo();
			Long codigo2 = o2.getCodigo();
			
			return codigo1.compareTo(codigo2);	
        }
    }	
}

package br.gov.mec.aghu.patrimonio.vo;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;

public class AceiteTecnicoParaSerRealizadoVOComparator {

	public static class OrderByMaterial implements Comparator<AceiteTecnicoParaSerRealizadoVO> {

        @Override
        public int compare(AceiteTecnicoParaSerRealizadoVO o1, AceiteTecnicoParaSerRealizadoVO o2) {
        	final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			
			String descricao1 = StringUtils.trimToEmpty(o1.getMaterial());
			String descricao2 = StringUtils.trimToEmpty(o2.getMaterial());
			    
			return collator.compare(descricao1, descricao2);	
        }
    }
	
	public static class OrderByNroSolicCompras implements Comparator<AceiteTecnicoParaSerRealizadoVO> {

        @Override
        public int compare(AceiteTecnicoParaSerRealizadoVO o1, AceiteTecnicoParaSerRealizadoVO o2) {
        	Integer numero1 = o1.getNroSolicCompras();
			Integer numero2 = o2.getNroSolicCompras();
			
			if (numero1 == null) {
		         if (numero2 == null) {
		            return 0;
		         } else {
		            return -1; 
		         }
			} else {
		         if (numero2 == null) {
		            return 1;
		         } else {
		            return numero1.compareTo(numero2);	
		         }
			}
        }
    }
	
	public static class OrderByCodigo implements Comparator<AceiteTecnicoParaSerRealizadoVO> {

        @Override
        public int compare(AceiteTecnicoParaSerRealizadoVO o1, AceiteTecnicoParaSerRealizadoVO o2) {
        	Integer codigo1 = o1.getCodigo();
			Integer codigo2 = o2.getCodigo();
			
			if (codigo1 == null) {
		         if (codigo2 == null) {
		            return 0;
		         } else {
		            return -1; 
		         }
			} else {
		         if (codigo2 == null) {
		            return 1;
		         } else {
		            return codigo1.compareTo(codigo2);	
		         }
			}
        }
    }	
	
	public static class OrderByAreaTecAvaliacao implements Comparator<AceiteTecnicoParaSerRealizadoVO> {
		
		@Override
		public int compare(AceiteTecnicoParaSerRealizadoVO o1, AceiteTecnicoParaSerRealizadoVO o2) {
			Integer area1 = o1.getAreaTecAvaliacao();
			Integer area2 = o2.getAreaTecAvaliacao();
			
			if (area1 == null) {
		         if (area2 == null) {
		            return 0;
		         } else {
		            return -1; 
		         }
			} else {
		         if (area2 == null) {
		            return 1;
		         } else {
		            return area1.compareTo(area2);	
		         }
			}
		}
	}	
	
	public static class OrderByReceb implements Comparator<AceiteTecnicoParaSerRealizadoVO> {
		
		@Override
		public int compare(AceiteTecnicoParaSerRealizadoVO o1, AceiteTecnicoParaSerRealizadoVO o2) {
			Integer receb1 = o1.getRecebimento();
			Integer receb2 = o2.getRecebimento();
			
			if (receb1 == null) {
		         if (receb2 == null) {
		            return 0;
		         } else {
		            return -1; 
		         }
			} else {
		         if (receb2 == null) {
		            return 1;
		         } else {
		            return receb1.compareTo(receb2);	
		         }
			}	
		}
	}
	
	public static class OrderByNomeAreaTecnica implements Comparator<PtmAreaTecAvaliacao> {
		
		@Override
		public int compare(PtmAreaTecAvaliacao o1, PtmAreaTecAvaliacao o2) {	
			final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			
			String descricao1 = StringUtils.trimToEmpty(o1.getNomeAreaTecAvaliacao());
			String descricao2 = StringUtils.trimToEmpty(o2.getNomeAreaTecAvaliacao());
			    
			return collator.compare(descricao1, descricao2);
		}
	}
}

package br.gov.mec.aghu.compras.contaspagar.vo;

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * @author rafael.silvestre
 */
public class SolicitacaoTituloVOComparator {

	public static class OrderByTipo implements Comparator<SolicitacaoTituloVO> {

        @Override
        public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
        	final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			
			String tipo1 = StringUtils.trimToEmpty(o1.getTipo());
			String tipo2 = StringUtils.trimToEmpty(o2.getTipo());
			    
			return collator.compare(tipo1, tipo2);	
        }
    }
	
	public static class OrderBySolicitacao implements Comparator<SolicitacaoTituloVO> {

        @Override
        public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
        	Integer numero1 = o1.getSolicitacao();
			Integer numero2 = o2.getSolicitacao();
			
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
	
	public static class OrderByCodigo implements Comparator<SolicitacaoTituloVO> {
		
		@Override
		public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
			Integer numero1 = o1.getCodigo();
			Integer numero2 = o2.getCodigo();
			
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
	
	public static class OrderByServicoMaterial implements Comparator<SolicitacaoTituloVO> {

        @Override
        public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
        	final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			
			String descricao1 = StringUtils.trimToEmpty(o1.getServicoMaterial());
			String descricao2 = StringUtils.trimToEmpty(o2.getServicoMaterial());
			    
			return collator.compare(descricao1, descricao2);	
        }
    }	
	
	public static class OrderByGrupoNaturezaDespesa implements Comparator<SolicitacaoTituloVO> {
		
		@Override
		public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
			Integer numero1 = o1.getGrupoNaturezaDespesa();
			Integer numero2 = o2.getGrupoNaturezaDespesa();
			
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
	
	public static class OrderByNtdCodigo implements Comparator<SolicitacaoTituloVO> {
		
		@Override
		public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
			Byte numero1 = o1.getNtdCodigo();
			Byte numero2 = o2.getNtdCodigo();
			
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
	
	public static class OrderByNaturezaDespesa implements Comparator<SolicitacaoTituloVO> {
		
		@Override
		public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
			final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			
			String descricao1 = StringUtils.trimToEmpty(o1.getNaturezaDespesa());
			String descricao2 = StringUtils.trimToEmpty(o2.getNaturezaDespesa());
			    
			return collator.compare(descricao1, descricao2);	
		}
	}
	
	public static class OrderByDataGeracao implements Comparator<SolicitacaoTituloVO> {
		
		@Override
		public int compare(SolicitacaoTituloVO o1, SolicitacaoTituloVO o2) {
			Date data1 = o1.getDataGeracao();
			Date data2 = o2.getDataGeracao();
			
			if (data1 == null) {
		         if (data2 == null) {
		            return 0;
		         } else {
		            return -1; 
		         }
			} else {
		         if (data2 == null) {
		            return 1;
		         } else {
		            return data1.compareTo(data2);	
		         }
			}	
		}
	}
}

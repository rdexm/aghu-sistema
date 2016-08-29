package br.gov.mec.aghu.dominio;

import java.util.Comparator;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a origem de inserção do contrato
 * 
 * @author agerling
 * 
 */
public enum DominioOrigemContrato implements Dominio {
	
	/**
	 * Origem Manual
	 */
	M,
	
	/**
	 * Origem Automática
	 */
	A;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Origem Manual";
		case A:
			return "Origem Automática";
		default:
			return "";
		}
	}
	
	class DominioOrigemContratoContratoComparator implements Comparator<DominioOrigemContrato>
	{
	    public int compare(DominioOrigemContrato o1, DominioOrigemContrato o2)
	    {
	    	int i = -1;
	        if(o1.getCodigo()>o2.getCodigo()){
	        	i = 1;
	        }
	        if(o1.getCodigo()==o2.getCodigo()){
	        	i=0;
	        }
	        return i;
	    }
	}	

}

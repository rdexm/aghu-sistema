package br.gov.mec.aghu.dominio;

import java.util.Comparator;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSortableSitEnvioContrato implements Dominio {
	RED(1),
	YELLOW(2),
	GREEN(3);

	int index;
	DominioSortableSitEnvioContrato(int ordinal) {
		index = ordinal;
	}

	@Override
	public int getCodigo() {
		return index;
	}

	@Override
	public String getDescricao() {
		// TODO Auto-generated method stub
		return null;
	}
	
	class DominioSortableSitEnvioContratoComparator implements Comparator<DominioSortableSitEnvioContrato>
	{
	    public int compare(DominioSortableSitEnvioContrato o1, DominioSortableSitEnvioContrato o2)
	    {
	    	int i = -1;
	        if(o1.index>o2.index){
	        	i = 1;
	        }
	        if(o1.index==o2.index){
	        	i=0;
	        }
	        return i;
	    }
	}


}

package br.gov.mec.aghu.dominio;

import java.util.HashMap;
import java.util.Map;

import br.gov.mec.aghu.core.dominio.Dominio;

/*
Os valores do dominio foram definidos conforme o select abaixo.
	SELECT RV_LOW_VALUE, rv_meaning
	FROM sco_REF_CODES
	WHERE
	RV_DOMAIN = 'MOD_EMP'
 */
public enum DominioModalidadeEmpenho implements Dominio {
	ORDINARIO(1),
	ESTIMATIVA(3),
	CONTRATO(5);
	
    private static final Map<Integer, DominioModalidadeEmpenho> typesByValue = new HashMap<Integer, DominioModalidadeEmpenho>();
	private int value;
	
	 static {
	        for (DominioModalidadeEmpenho type : DominioModalidadeEmpenho.values()) {
	            typesByValue.put(type.value, type);
	        }
	    }

	 public static DominioModalidadeEmpenho forValue(int value) {
	        return typesByValue.get(value);
	    }

	
	private DominioModalidadeEmpenho(int value){
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}
	
	@Override
	public String getDescricao() {
		switch(this){
		case ORDINARIO:
			return this.value + " - Ordinário";
		case ESTIMATIVA:
			return this.value + " - Estimativo";
		case CONTRATO:
			return this.value + " - Contrato";
		default: return "";
		}
	}
	
	public String getDescricaoSimples() {
		switch(this){
		case ORDINARIO:
			return "Ordinário";
		case ESTIMATIVA:
			return "Estimativo";
		case CONTRATO:
			return "Contrato";
		default: 
			return "";
		}
	}
}

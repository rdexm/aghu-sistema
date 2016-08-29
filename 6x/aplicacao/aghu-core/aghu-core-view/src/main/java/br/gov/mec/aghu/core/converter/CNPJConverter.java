package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;


@FacesConverter("cnpjConverter")
public class CNPJConverter extends LongMaskConverter {
	
	private static final long serialVersionUID = -7893580117954662081L;

	/**
	 * Coloca no formato 99.999.999/9999-99 
	 */
	@Override
	public String getAsString(Object valor) {
		StringBuilder sb = new StringBuilder(super.getAsString(valor));
		
		while (sb.length() < 14)
		{
			//Adiciona 0 a esquerda até da o número de caracteres correto
			sb.insert(0, '0');
		}
		
		//Adiciona a máscara
		sb.insert(2, '.');
		sb.insert(6, '.');
		sb.insert(10, '/');
		sb.insert(15, '-');
		
		
		return sb.toString();
	}
}

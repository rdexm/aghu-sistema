package br.gov.mec.aghu.core.converter;

import javax.faces.convert.FacesConverter;


@FacesConverter("cpfConverter")
public class CPFConverter extends LongMaskConverter {
	
	private static final long serialVersionUID = 4280186466706467258L;

	/**
	 * Coloca no formato 111.111.111-11 
	 */
	@Override
	public String getAsString(Object valor) {
		StringBuilder sb = new StringBuilder(super.getAsString(valor));
		
		while (sb.length() < 11)
		{
			//Adiciona 0 a esquerda até da o número de caracteres correto
			sb.insert(0, '0');
		}
		
		//Adiciona a máscara
		sb.insert(3, '.');
		sb.insert(7, '.');
		sb.insert(11, '-');
		
		return sb.toString();
	}
}

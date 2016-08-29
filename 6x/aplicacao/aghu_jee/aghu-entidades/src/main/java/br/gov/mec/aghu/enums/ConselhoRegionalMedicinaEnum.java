package br.gov.mec.aghu.enums;

import java.util.ArrayList;
import java.util.List;

public enum ConselhoRegionalMedicinaEnum {

	CRM, // Conselho Regional Medicina (Geral)
	CREMAC, // Acre
	CREMAL, // Alagoas
	CREMAP, // Amapá
	CRMAM, // Amazonas
	CREMEB, // Bahia
	CREMEC, // Ceará
	CRMDF, // Distrito Federal
	CREMES, // Espírito Santo
	CRMGO, // Goiás
	CRMMA, // Maranhão
	CRMMT, // Mato Grosso
	CRMMS, // Mato Grosso do Sul
	CREMEMG, // Minas Gerais
	CREMEPA, // Pará
	CRMPB, // Paraíba
	CRMPR, // Paraná
	CREMEPE, // Pernambuco
	CREMEPI, // Piauí
	CREMERJ, // Rio de Janeiro
	CRMRN, // Rio Grande do Norte
	CREMERS, // Rio Grande do Sul
	CREMERO, // Rondônia
	CRMRR, // Roraima
	CREMESC, // Santa Catarina
	CREMESP, // São Paulo
	CREMESE, // Sergipe
	CRMTO // Tocantins
	;

	/**
	 * Método para montar em um List<String> todos valores do enum (para ser
	 * utilizado em Criterias)
	 * 
	 * @return List<String>
	 */
	public static List<String> getListaValores() {
		List<String> valores = new ArrayList<String>();
		for (ConselhoRegionalMedicinaEnum valor : ConselhoRegionalMedicinaEnum
				.values()) {
			valores.add(valor.name());
		}
		return valores;
	}
	
	/**
	 * Método para montar uma String com todos valores do enum separados por
	 * vírgulas e com aspas simples (para ser utilizados em HQLs)
	 * 
	 * @return
	 */
	public static String getValores() {
		StringBuffer valores = new StringBuffer();
		for (ConselhoRegionalMedicinaEnum valor : ConselhoRegionalMedicinaEnum
				.values()) {
			if (valores.length() == 0) {
				valores.append('\'').append(valor.name()).append('\'');
			} else {
				valores.append(", '").append(valor.name()).append('\'');
			}
		}
		return valores.toString();
	}

}

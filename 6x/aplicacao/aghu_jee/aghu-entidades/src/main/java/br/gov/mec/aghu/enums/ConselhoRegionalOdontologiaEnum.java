package br.gov.mec.aghu.enums;

import java.util.ArrayList;
import java.util.List;

public enum ConselhoRegionalOdontologiaEnum {

	CRO, // Conselho Regional Odontologia (Geral)
	CROAC, // Acre
	CROAL, // Alagoas
	CROAP, // Amapá
	CROAM, // Amazonas
	CROBA, // Bahia
	CROCE, // Ceará
	CRODF, // Distrito Federal
	CROES, // Espírito Santo
	CROGO, // Goiás
	CROMA, // Maranhão
	CROMT, // Mato Grosso
	CROMS, // Mato Grosso do Sul
	CROMG, // Minas Gerais
	CROPA, // Pará
	CROPB, // Paraíba
	CROPR, // Paraná
	CROPE, // Pernambuco
	CROPI, // Piauí
	CRORJ, // Rio de Janeiro
	CRORN, // Rio Grande do Norte
	CRORS, // Rio Grande do Sul
	CRORO, // Rondônia
	CRORR, // Roraima
	CROSC, // Santa Catarina
	CROSP, // São Paulo
	CROSE, // Sergipe
	CROTO // Tocantins
	;

	/**
	 * Método para montar em um List<String> todos valores do enum (para ser
	 * utilizado em Criterias).
	 * 
	 * @return List<String>
	 */
	public static List<String> getListaValores() {
		List<String> valores = new ArrayList<String>();
		for (ConselhoRegionalOdontologiaEnum valor : ConselhoRegionalOdontologiaEnum
				.values()) {
			valores.add(valor.name());
		}
		return valores;
	}

	/**
	 * Método para montar uma String com todos valores do enum separados por
	 * vírgulas e com aspas simples (para ser utilizados em HQLs).
	 * 
	 * @return
	 */
	public static String getValores() {
		StringBuffer valores = new StringBuffer();
		for (ConselhoRegionalMedicinaEnum valor : ConselhoRegionalMedicinaEnum
				.values()) {
			if ("".equals(valores)) {
				valores.append('\'').append(valor.name()).append('\'');
			} else {
				valores.append(", '").append(valor.name()).append('\'');
			}
		}
		return valores.toString();
	}

}

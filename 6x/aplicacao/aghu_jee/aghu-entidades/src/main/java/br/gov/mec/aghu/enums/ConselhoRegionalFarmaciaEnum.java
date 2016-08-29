package br.gov.mec.aghu.enums;

import java.util.ArrayList;
import java.util.List;

public enum ConselhoRegionalFarmaciaEnum {

	CRF, // Conselho Regional Farmácia (Geral)
	CRFAC, // Acre
	CRFAL, // Alagoas
	CRFAP, // Amapá
	CRFAM, // Amazonas
	CRFBA, // Bahia
	CRFCE, // Ceará
	CRFDF, // Distrito Federal
	CRFES, // Espírito Santo
	CRFGO, // Goiás
	CRFMA, // Maranhão
	CRFMT, // Mato Grosso
	CRFMS, // Mato Grosso do Sul
	CRFMG, // Minas Gerais
	CRFPA, // Pará
	CRFPB, // Paraíba
	CRFPR, // Paraná
	CRFPE, // Pernambuco
	CRFPI, // Piauí
	CRFRJ, // Rio de Janeiro
	CRFRN, // Rio Grande do Norte
	CRFRS, // Rio Grande do Sul
	CRFRO, // Rondônia
	CRFRR, // Roraima
	CRFSC, // Santa Catarina
	CRFSP, // São Paulo
	CRFSE, // Sergipe
	CRFTO // Tocantins
	;

	/**
	 * Método para montar em um List<String> todos valores do enum (para ser
	 * utilizado em Criterias)
	 * 
	 * @return List<String>
	 */
	public static List<String> getListaValores() {
		List<String> valores = new ArrayList<String>();
		for (ConselhoRegionalFarmaciaEnum valor : ConselhoRegionalFarmaciaEnum
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
		for (ConselhoRegionalFarmaciaEnum valor : ConselhoRegionalFarmaciaEnum
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

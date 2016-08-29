package br.gov.mec.aghu.enums;

import java.util.ArrayList;
import java.util.List;

public enum ConselhoRegionalEnfermagemEnum {

	COREN,   // Conselho Regional Enfermagem (Geral)
	CORENAC, //Acre 
	CORENAL, //Alagoas
	CORENNP,
	CORENAM, //Amazonas
	CORENBA, //Bahia 
	CORENCE, //Ceara
	CORENDF, //Distrito Federal
	CORENES, //Espírito Santo
	CORENGO, //Goiás
	CORENMA, //Maranhão
	CORENMT, //Mato Grosso
	CORENMS, //Mato Grosso do Sul 
	CORENMG, //Minas Gerais
	CORENPA, //Pará 
	CORENPB, //Paraíba
	CORENPR, //Paraná
	CORENPE, //Pernambuco
	CORENPI, //Piauí
	CORENRJ, //Rio de Janeiro
	CORENRN, //Rio Grande do Norte 
	CORENRS, //Rio Grande do Sul
	CORENRO, //Rondônia
	CORENRR, //Roraima
	CORENSC, //Santa Catarina 
	CORENSP, //São Paulo 
	CORENSE, //Sergipe 
	CORENTO  //Tocantins
	;
	
	/**	 * Método para montar em um List<String> todos valores do enum (para ser
	 * utilizado em Criterias)
	 * 
	 * @return List<String>
	 */
	public static List<String> getListaValores() {
		List<String> valores = new ArrayList<String>();
		for (ConselhoRegionalEnfermagemEnum valor : ConselhoRegionalEnfermagemEnum
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
		for (ConselhoRegionalEnfermagemEnum valor : ConselhoRegionalEnfermagemEnum
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

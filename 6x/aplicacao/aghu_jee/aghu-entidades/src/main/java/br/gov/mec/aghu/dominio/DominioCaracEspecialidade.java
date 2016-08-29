package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica a situação de uma entidade, se ativa ou inativa.
 * 
 * @author gmneto
 * 
 */
public enum DominioCaracEspecialidade implements DominioString {
	
	/**
	 * Bloqueio prescricao medica
	 */
	BLOQUEIO_PRESCRICAO_MEDICA,

	/**
	 * Cand Apac Otorrino
	 */
	CAND_APAC_OTORRINO,

	/**
	 * Central Leitos
	 */
	CENTRAL_LEITOS,

	/**
	 * Consultoria para Enfermagem
	 */
	CONSULTORIA_PARA_ENFERMAGEM,

	/**
	 * Emergencia
	 */
	EMERGENCIA,

	/**
	 * Excecao Lista Interconsultas
	 */
	EXCECAO_LISTA_INTERCONSULTAS,

	/**
	 * Func SMO
	 */
	FUNC_SMO,

	/**
	 * Funcionarios
	 */
	FUNCIONARIOS,

	/**
	 * Gera Prontuario Virtual
	 */
	GERA_PRONTUARIO_VIRTUAL,

	/**
	 * Informa Cartao SUS
	 */
	INFORMA_CARTAO_SUS,
	/**
	 * Informa CNS transplantado
	 */
	INFORMA_CNS_TRANSPLANTADO,
	/**
	 * Ingresso SO
	 */
	INGRESSO_SO,
	/**
	 * Lança Item Ambulatorio
	 */
	LANCA_ITEM_AMBULATORIO,
	/**
	 * Laudo SMO
	 */
	LAUDO_SMO,
	/**
	 * Permite Supervisao fora zona
	 */
	PERMITE_SUPERVISAO_FORA_ZONA,
	/**
	 * Servico Otorrino Oftalmo
	 */
	SERVICO_OTORRINO_OFTALMO,
	/**
	 * Solic Interconsulta Suspensa
	 */
	SOLIC_INTERCONSULTA_SUSPENSA,
	/* 
	 * Transfere Starh
	 * */
	TRANSFERE_STARH,

	/**
	 * Regulacao Gestor
	 */
	REGULACAO_GESTOR,
	
	/**
	 * Unidade Psiquiatrica
	 */
	UNID_PSIQUIATRICA,
	
	
	/**
	 * Valor default ao carregar a tela 
	 */
	SELECIONE,
	
	 /**
     * Candidato APAC Fotocoagulação
     */
    CAND_APAC_FOTO;


	
	@Override
	public String getCodigo() {
		
		switch (this) {
		case BLOQUEIO_PRESCRICAO_MEDICA:
			return "Bloqueio prescricao medica";		
		case CAND_APAC_OTORRINO:
			return "Cand Apac Otorrino";
		case CENTRAL_LEITOS:
			return "Central Leitos";
		case CONSULTORIA_PARA_ENFERMAGEM:
			return "Consultoria para Enfermagem";
		case EMERGENCIA:
			return "Emergencia";
		case EXCECAO_LISTA_INTERCONSULTAS:
			return "Excecao Lista Interconsultas";
		case FUNC_SMO:
			return "Func SMO";
		case FUNCIONARIOS:
			return "Funcionarios";
		case GERA_PRONTUARIO_VIRTUAL:
			return "Gera Prontuario Virtual";
		case INFORMA_CARTAO_SUS:
			return "Informa Cartao SUS";
		case INFORMA_CNS_TRANSPLANTADO:
			return "Informa CNS transplantado";
		case INGRESSO_SO:
			return "Ingresso SO";
		case LANCA_ITEM_AMBULATORIO:
			return "Lança Item Ambulatorio";
		case LAUDO_SMO:
			return "Laudo SMO";
		case PERMITE_SUPERVISAO_FORA_ZONA:
			return "Permite Supervisao fora zona";
		case SERVICO_OTORRINO_OFTALMO:
			return "Servico Otorrino Oftalmo";
		case SOLIC_INTERCONSULTA_SUSPENSA:
			return "Solic Interconsulta Suspensa";
		case TRANSFERE_STARH:
			return "Transfere Starh";
		case REGULACAO_GESTOR:
			return "Regulacao Gestor";
		case UNID_PSIQUIATRICA:
			return "Unidade Psiquiatrica";
		case CAND_APAC_FOTO:
            return "Cand Apac Foto";
		case SELECIONE:
			return "Selecione";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case BLOQUEIO_PRESCRICAO_MEDICA:
			return "Bloqueio prescricao medica";		
		case CAND_APAC_OTORRINO:
			return "Candidato APAC Otorrino";
		case CENTRAL_LEITOS:
			return "Pertence à Central de Leitos";
		case CONSULTORIA_PARA_ENFERMAGEM:
			return "Consultoria para Enfermagem";
		case EMERGENCIA:
			return "Seleção Marcação Emergência";
		case EXCECAO_LISTA_INTERCONSULTAS:
			return "Exceção Lista Interconsultas";
		case FUNC_SMO:
			return "Marcação Funcionários Serviço de Medicina Ocupacional";
		case FUNCIONARIOS:
			return "Seleção Marcação Funcionários";
		case GERA_PRONTUARIO_VIRTUAL:
			return "Gera Prontuário Virtual";
		case INFORMA_CARTAO_SUS:
			return "Informa Cartão SUS";
		case INFORMA_CNS_TRANSPLANTADO:
			return "Informa Cartão Nacional de Saúde do Transplantado";
		case INGRESSO_SO:
			return "Ingresso na Sala de Observação";
		case LANCA_ITEM_AMBULATORIO:
			return "Lança Item Ambulatório";
		case LAUDO_SMO:
			return "Laudo Serviço de Medicina Ocupacional";
		case PERMITE_SUPERVISAO_FORA_ZONA:
			return "Permite Supervisão Fora Zona";
		case SERVICO_OTORRINO_OFTALMO:
			return "Serviço Otorrinolaringologia e Oftalmologia";
		case SOLIC_INTERCONSULTA_SUSPENSA:
			return "Solicitação Interconsulta Suspensa";
		//Transfere Starh
		case TRANSFERE_STARH:
			return "Transfere Starh";
		case REGULACAO_GESTOR:
			return "Regulação Gestor";
		case UNID_PSIQUIATRICA:
			return "Unidade Psiquiatrica";
		 case CAND_APAC_FOTO:
             return "Candidato APAC Fotocoagulação";
		default:
			return "";
		}
	}

	/**
	 * Obter o dominio pelo codigo passado
	 * @param caractCodigo
	 * @return
	 */
	public static DominioCaracEspecialidade getDominioPorCodigo(String caractCodigo) {
		DominioCaracEspecialidade retorno = null;
		for (DominioCaracEspecialidade dominio : values()) {
			if (caractCodigo.equals(dominio.getCodigo())) {
				retorno = dominio;
				break;
			}
		}
		return retorno;
	}

}

package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio com os tipos de impressoras.
 * 
 * @author riccosta
 */
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public enum DominioTipoImpressora implements Dominio {

	AF_ENGENHARIA, AF_IMPRESSORA_LASER_COMPRAS, AVISO_INTERNACAO, BA_EMERGENCIA, BA_SAMIS, BA_SMO, CONTRATO_HOSPITALIZACAO, ETIQUETA_DE_BARRAS_OP, ETIQUETA_PULSEIRA_PACIENTE, ETIQUETAS_BARRAS, ETIQUETAS_BARRAS_BOLSA, ETIQUETAS_BARRAS_PACIENTE, ETIQUETAS_BARRAS_PRONTUARIO, ETIQUETAS_CARACTER, ETIQ_QUIMIO_FRASCO, ETIQUETA_BARRAS_MEDICAMENTOS, ETIQ_QUIMIO_SERINGA, ETIQUETA_BARRAS_REDOME, ETIQUETAS_MEDICAMENTOS, ETIQUETAS_NUMERO_AP, FARMACIA_DISP, IMPRIME_AF, NOTA_CONSUMO, NR_RCEBIMENTO_MATERIAIS, PRESCRICAO_MEDICA, RECEP_ZONA_AMBULATORIAL, RM_PEDIDO, RM_PEDIDO_CNPQ, RM_PEDIDO_ENG, RM_PEDIDO_EXPEDIENTE, RM_PEDIDO_FARM_CENTRAL, RM_PEDIDO_FARM_IND, RM_PEDIDO_GRAFICA, RM_PEDIDO_HIGIENE, RM_PEDIDO_MEDICAMENTOS, RM_PEDIDO_MEDICO_HOSP, RM_PEDIDO_ORTESE_PROTESE, RM_PEDIDO_PATOCLI, RM_PEDIDO_PRCR, RM_PEDIDO_ROUPARIA, RM_PEDIDO_SND, RM_PEDIDO_SOROS, TICKET_CO, TICKET_CONSULTA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AF_ENGENHARIA:
			return "AF -  Engenharia";
		case AF_IMPRESSORA_LASER_COMPRAS:
			return "AF - Impressora Laser Compras";
		case AVISO_INTERNACAO:
			return "Aviso Internação";
		case BA_EMERGENCIA:
			return "BA Emergência";
		case BA_SAMIS:
			return "BA Samis";
		case BA_SMO:
			return "BA SMO";
		case CONTRATO_HOSPITALIZACAO:
			return "Contrato Hospitalização";
		case ETIQUETA_DE_BARRAS_OP:
			return "Etiqueta de Barras - OP";
		case ETIQUETA_PULSEIRA_PACIENTE:
			return "Etiqueta Pulseira Paciente";
		case ETIQUETAS_BARRAS:
			return "Etiquetas Barras";
		case ETIQUETAS_BARRAS_BOLSA:
			return "Etiquetas Barras das Bolsas";
		case ETIQUETAS_BARRAS_PACIENTE:
			return "Etiquetas Barras para Paciente";
		case ETIQUETAS_BARRAS_PRONTUARIO:
			return "Etiquetas Barras Prontuario";
		case ETIQUETAS_CARACTER:
			return "Etiquetas Caracter";
		case ETIQ_QUIMIO_FRASCO:
			return "Etiquetas de Barras para Frascos de Quimioterapia";
		case ETIQUETA_BARRAS_MEDICAMENTOS:
			return "Etiquetas de Barras para Medicamentos";
		case ETIQ_QUIMIO_SERINGA:
			return "Etiquetas de Barras para Seringas da Quimioterapia";
		case ETIQUETA_BARRAS_REDOME:
			return "Etiquetas de Barras Redome";
		case ETIQUETAS_MEDICAMENTOS:
			return "Etiquetas medicamentos";
		case ETIQUETAS_NUMERO_AP:
			return "Etiquetas Numero AP";
		case FARMACIA_DISP:
			return "Farmácia Dispensação";
		case IMPRIME_AF:
			return "Imprime AF";
		case NOTA_CONSUMO:
			return "Nota Consumo";
		case NR_RCEBIMENTO_MATERIAIS:
			return "NR - Rcebimento Materiais";
		case PRESCRICAO_MEDICA:
			return "Prescrição Médica";
		case RECEP_ZONA_AMBULATORIAL:
			return "Recep Zona Ambulatorial";
		case RM_PEDIDO:
			return "RM - Pedido";
		case RM_PEDIDO_CNPQ:
			return "RM - Pedido CNPQ";
		case RM_PEDIDO_ENG:
			return "RM - Pedido Eng";
		case RM_PEDIDO_EXPEDIENTE:
			return "RM - Pedido Expediente";
		case RM_PEDIDO_FARM_CENTRAL:
			return "RM - Pedido Farm Central";
		case RM_PEDIDO_FARM_IND:
			return "RM - Pedido Farm Ind";
		case RM_PEDIDO_GRAFICA:
			return "RM - Pedido Grafica";
		case RM_PEDIDO_HIGIENE:
			return "RM - Pedido Higiene";
		case RM_PEDIDO_MEDICAMENTOS:
			return "RM - Pedido Medicamentos";
		case RM_PEDIDO_MEDICO_HOSP:
			return "RM - Pedido Medico Hosp";
		case RM_PEDIDO_ORTESE_PROTESE:
			return "RM - Pedido Ortese Protese";
		case RM_PEDIDO_PATOCLI:
			return "RM - Pedido PATOCLI";
		case RM_PEDIDO_PRCR:
			return "RM - Pedido PRCR";
		case RM_PEDIDO_ROUPARIA:
			return "RM - Pedido Rouparia";
		case RM_PEDIDO_SND:
			return "RM - Pedido SND";
		case RM_PEDIDO_SOROS:
			return "RM - Pedido Soros";
		case TICKET_CO:
			return "Ticket CO";
		case TICKET_CONSULTA:
			return "Ticket Consulta";
		default:
			return "";
		}
	}
}

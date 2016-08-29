/**
 * 
 */
package br.gov.mec.aghu.prescricaomedica.util;

/**
 * @author rcorvalao
 *
 */
public enum PrescricaoMedicaTypes {
	// O:
	CONSULTORIA,
	// C:
	CUIDADOS_MEDICOS,
	// D:
	DIETA,
	// H:
	HEMOTERAPIA,
	// M:
	MEDICAMENTO,
	// S:
	SOLUCAO,
	// N:
	NUTRICAO_PARENTAL,
	// P:
	PROCEDIMENTO;
	
	public String getUrl() {
		String url;
		switch (this) {
			case CONSULTORIA:
				url = "/prescricaomedica/consultoria/solicitarConsultoria.xhtml";
				break;
			case CUIDADOS_MEDICOS:
				url = "/prescricaomedica/manterPrescricaoCuidado.xhtml";
				break;
			case DIETA:
				url = "/prescricaomedica/manterPrescricaoDieta.xhtml";
				break;
			case HEMOTERAPIA:
				url = "/prescricaomedica/solicitacaohemoterapia/manterSolicitacaoHemoterapia.xhtml";
				break;
			case MEDICAMENTO:
				url = "/prescricaomedica/prescricaomedicamento/manterPrescricaoMedicamento.xhtml";
				break;
			case SOLUCAO:
				url = "/prescricaomedica/prescricaosolucao/manterPrescricaoSolucao.xhtml";
				break;
			case NUTRICAO_PARENTAL:
				url = "/url_a_definir";
				break;
			case PROCEDIMENTO:
				url = "/prescricaomedica/procedimentos/manterProcedimentoEspecial.xhtml";
				break;
			default:
				url = "/url_a_definir";
				break;
		}
		
		return url;
	}
	
	public String getTitulo() {
		String titulo;
		switch (this) {
			case CONSULTORIA:
				titulo = "Consultoria";
				break;
			case CUIDADOS_MEDICOS:
				titulo = "Cuidados Médicos";
				break;
			case DIETA:
				titulo = "Dieta";
				break;
			case HEMOTERAPIA:
				titulo = "Hemoterapia";
				break;
			case MEDICAMENTO:
				titulo = "Medicamento";
				break;
			case SOLUCAO:
				titulo = "Solução";
				break;
			case NUTRICAO_PARENTAL:
				titulo = "Nutrição Parenteral";
				break;
			case PROCEDIMENTO:
				titulo = "Procedimento";
				break;
			default:
				titulo = "";
				break;
		}
		
		return titulo;
	}
	
	public Character getSigla() {
		Character sigla;
		switch (this) {
			case CONSULTORIA:
				sigla = 'O';
				break;
			case CUIDADOS_MEDICOS:
				sigla = 'C';
				break;
			case DIETA:
				sigla = 'D';
				break;
			case HEMOTERAPIA:
				sigla = 'H';
				break;
			case MEDICAMENTO:
				sigla = 'M';
				break;
			case SOLUCAO:
				sigla = 'S';
				break;
			case NUTRICAO_PARENTAL:
				sigla = 'N';
				break;
			case PROCEDIMENTO:
				sigla = 'P';
				break;
			default:
				sigla = ' ';
				break;
		}
		
		return sigla;
	}

	public String getTituloAmbulatorial() {
		String titulo;
		switch (this) {
			case CONSULTORIA:
				titulo = "Consultorias";
				break;
			case CUIDADOS_MEDICOS:
				titulo = "Cuidados";
				break;
			case DIETA:
				titulo = "Dieta";
				break;
			case HEMOTERAPIA:
				titulo = "Hemoterapia";
				break;
			case MEDICAMENTO:
				titulo = "Medicamentos";
				break;
			case SOLUCAO:
				titulo = "Soluções";
				break;
			case NUTRICAO_PARENTAL:
				titulo = "Nutrição Parenteral";
				break;
			case PROCEDIMENTO:
				titulo = "Procedimentos Especiais";
				break;
			default:
				titulo = "";
				break;
		}
		
		return titulo;
	}
	
}

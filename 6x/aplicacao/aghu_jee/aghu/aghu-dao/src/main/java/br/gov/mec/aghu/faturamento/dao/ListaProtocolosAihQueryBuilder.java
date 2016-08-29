package br.gov.mec.aghu.faturamento.dao;

public class ListaProtocolosAihQueryBuilder {
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public String build() {
	
		StringBuilder sql = new StringBuilder(3000);
		
		sql.append("SELECT DISTINCT 'REALIZADO: ' TIPO, \n")
		.append("  CTH.SEQ CTHSEQ , \n")
		.append("  CTH.DT_INT_ADMINISTRATIVA DATAINTERNACAO , \n")
		.append("  CTH.DT_ALTA_ADMINISTRATIVA DATAALTA , \n")
		.append("  EAI.PAC_PRONTUARIO PRONTUARIO, \n")
		.append("  EAI.PAC_NOME NOME, \n")
		.append("  EAI.IPH_COD_SUS_REALIZ CODTABELA , \n")
		.append("  VAAP.IPH_DESCRICAO DESCRICAO \n")
		.append("FROM AGH.FAT_CONTAS_HOSPITALARES CTH \n")
		.append("RIGHT OUTER JOIN AGH.FAT_ESPELHOS_AIH EAI \n")
		.append("ON EAI.CTH_SEQ = CTH.SEQ \n")
		.append("RIGHT OUTER JOIN AGH.V_FAT_ASSOCIACAO_PROCEDIMENTOS VAAP \n")
		.append("ON VAAP.COD_TABELA           = EAI.IPH_COD_SUS_REALIZ \n")
		.append("AND VAAP.IPH_SEQ             = EAI.IPH_SEQ_REALIZ \n")
		.append("AND VAAP.IPH_PHO_SEQ         = EAI.IPH_PHO_SEQ_REALIZ \n")
		.append("WHERE CTH.DT_ENVIO_SMS       = :data \n")
		.append("AND CTH.IND_SITUACAO         = :situacao \n")
		.append("AND (CTH.IND_AUTORIZADO_SMS IS NULL \n")
		.append("OR CTH.IND_AUTORIZADO_SMS    = :autSms) \n")
		.append("UNION \n")
		.append("SELECT DISTINCT 'CMA: ' TIPO, \n")
		.append("  CTH.SEQ CTHSEQ , \n")
		.append("  CTH.DT_INT_ADMINISTRATIVA DATAINTERNACAO , \n")
		.append("  CTH.DT_ALTA_ADMINISTRATIVA DATAALTA , \n")
		.append("  EAI.PAC_PRONTUARIO PRONTUARIO, \n")
		.append("  EAI.PAC_NOME NOME , \n")
		.append("  VAAP.COD_TABELA CODTABELA , \n")
		.append("  VAAP.IPH_DESCRICAO DESCRICAO \n")
		.append("FROM AGH.FAT_CONTAS_HOSPITALARES CTH \n")
		.append("RIGHT OUTER JOIN AGH.FAT_ESPELHOS_AIH EAI \n")
		.append("ON EAI.CTH_SEQ = CTH.SEQ \n")
		.append("RIGHT OUTER JOIN AGH.FAT_CAMPOS_MEDICO_AUDIT_AIH CAH \n")
		.append("ON CAH.EAI_CTH_SEQ = CTH.SEQ \n")
		.append("RIGHT OUTER JOIN AGH.V_FAT_ASSOCIACAO_PROCEDIMENTOS VAAP \n")
		.append("ON VAAP.IPH_SEQ              = CAH.IPH_SEQ \n")
		.append("AND VAAP.IPH_PHO_SEQ         = CAH.IPH_PHO_SEQ \n")
		.append("WHERE CTH.DT_ENVIO_SMS       = :data \n")
		.append("AND CTH.IND_SITUACAO         = :situacao \n")
		.append("AND (CTH.IND_AUTORIZADO_SMS IS NULL \n")
		.append("OR CTH.IND_AUTORIZADO_SMS    = :autSms) \n")
		.append("ORDER BY NOME ASC, \n")
		.append("  TIPO DESC");
	
		return sql.toString();
	}

}

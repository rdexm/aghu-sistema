package br.gov.mec.aghu.faturamento.dao;

import javax.persistence.Table;

import br.gov.mec.aghu.faturamento.vo.CursorVerificacaoDtNascDtInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;


public class CursorVerificacaoDtNascDtInternacaoQueryBuilder {

	private static final String SELECT = " SELECT ";
	private static final String IGUAL = " = ";
	private static final String FROM = " FROM ";
	private static final String WHERE = " WHERE ";
	private static final String AS = " AS ";
	private static final String AND = " AND ";
	
	private static final String ALIAS_AIP = " AIP";	// aip_pacientes aip
	private static final String ALIAS_ATD = " ATD";	// agh_atendimentos atd
	private static final String ALIAS_COI = " COI";	// fat_contas_internacao coi
	private static final String ALIAS_CTH = " CTH";	// fat_contas_hospitalares cth

	public enum Parameter{
		P_CTH_SEQ {
			@Override
			String getParametro() {
				return ":P_CTH_SEQ";
			}
		};
		
		abstract String getParametro();
	}
	
	public String obterCursorVerificacaoDtNascDtInternacao(){

		StringBuilder builder =  new StringBuilder(1000);
		
		builder
			.append(SELECT)
				 // TO_NUMBER(TO_CHAR(aip.dt_nascimento, 'YYYYMMDD')) dt_nas
				.append(ALIAS_AIP).append('.').append(AipPacientes.Fields.DT_NASCIMENTO.name())
				.append(AS).append(CursorVerificacaoDtNascDtInternacaoVO.Fields.DATA_NASCIMENTO.toString())
				.append(',')
				 // TO_NUMBER(TO_CHAR(cth.dt_int_administrativa, 'YYYYMMDD')) dt_int
				.append(ALIAS_CTH).append('.').append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.name())
				.append(AS).append(CursorVerificacaoDtNascDtInternacaoVO.Fields.DATA_INTERNACAO.toString())

			.append(FROM)
			.append(AipPacientes.class.getAnnotation(Table.class).schema()).append('.')
				.append(AipPacientes.class.getAnnotation(Table.class).name()).append(ALIAS_AIP).append(", ")
				
			.append(AghAtendimentos.class.getAnnotation(Table.class).schema()).append('.')
				.append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(ALIAS_ATD).append(", ")
				
			.append(FatContasInternacao.class.getAnnotation(Table.class).schema()).append('.')	
				.append(FatContasInternacao.class.getAnnotation(Table.class).name()).append(ALIAS_COI).append(", ")
				
			.append(FatContasHospitalares.class.getAnnotation(Table.class).schema()).append('.')
				.append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(ALIAS_CTH)
				
			.append(WHERE)
				.append(ALIAS_COI).append('.').append(FatContasInternacao.Fields.CTH_SEQ.name()).append(IGUAL).append(Parameter.P_CTH_SEQ.getParametro())
				
				.append(AND).append(ALIAS_ATD).append('.').append(AghAtendimentos.Fields.INT_SEQ.name()).append(IGUAL)
					.append(ALIAS_COI).append('.').append(FatContasInternacao.Fields.INT_SEQ.name())
				
				.append(AND).append(ALIAS_COI).append('.').append(FatContasInternacao.Fields.CTH_SEQ.name()).append(IGUAL)
					.append(ALIAS_CTH).append('.').append(FatContasHospitalares.Fields.SEQ.name())
					
				.append(AND).append(ALIAS_AIP).append('.').append(AipPacientes.Fields.CODIGO.name()).append(IGUAL)
					.append(ALIAS_ATD).append('.').append(AghAtendimentos.Fields.PAC_CODIGO.name());	

		return builder.toString();
	}
	
	public String builder() {
		return obterCursorVerificacaoDtNascDtInternacao();	
	}
}

package br.gov.mec.aghu.faturamento.dao;

import javax.persistence.Table;

import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoItemConta;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ClinicaPorProcedimentoQueryBuilder {

	private static final String _2_ = " 2 ";
	private static final String DIFERENTE = " <> ";
	private static final String _LP_ = " ( ";
	private static final String _RP_ = " ) ";
	private static final String _1_ = " 1 ";
	private static final String IGUAL = " = ";
	private static final String SELECT = " SELECT ";
	private static final String FROM = " FROM ";
	private static final String WHERE = " WHERE ";
	private static final String AS = " AS ";
	private static final String COUNT = " COUNT ";
	private static final String COUNT_ALL = " COUNT (*) ";
	private static final String COALESCE = " COALESCE ";
	private static final String IS_NULL = " IS NULL ";
	private static final String UNION_ALL = " UNION ALL ";
	private static final String GROUP_BY = " GROUP BY ";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String CASE = " CASE ";
	private static final String WHEN = " WHEN ";
	private static final String THEN = " THEN ";
	private static final String ELSE = " ELSE ";
	private static final String END = " END ";
	private static final String SUM = " SUM ";
	private static final String AND = " AND ";
	
	private static final String ALIAS_CLC = " CLC"; // AGH_CLINICAS 				CLC
	private static final String ALIAS_IPH = " IPH"; // FAT_ITENS_PROCED_HOSPITALAR 	IPH
	private static final String ALIAS_EAI = " EAI"; // FAT_ESPELHOS_AIH            	EAI
	private static final String ALIAS_CTH = " CTH"; // FAT_CONTAS_HOSPITALARES     	CTH
	private static final String ALIAS_CAH = " CAH"; // FAT_CAMPOS_MEDICO_AUDIT_AIH 	CAH
	private static final String ALIAS_AAM = " AAM"; // FAT_ATOS_MEDICOS_AIH 		AAM

	public enum Parameter{
		P_DT_HR_INICIO {
			@Override
			String getParametro() {
				return ":P_DT_HR_INICIO";
			}
		},
		P_INT {
			@Override
			String getParametro() {
				return ":P_INT";
			}
		},
		P_MES {
			@Override
			String getParametro() {
				return ":P_MES";
			}
		},
		P_ANO {
			@Override
			String getParametro() {
				return ":P_ANO";
			}
		},
		P_COD_ATO_OPM {
			@Override
			String getParametro() {
				return ":P_COD_ATO_OPM";
			}
		};
		
		abstract String getParametro();
	}
	
	public String builder() {
		
		StringBuilder sql =  new StringBuilder(2000);
		
		sql.append(obterSqlConsultaUm())
			.append(UNION_ALL)
			.append(obterSqlConsultaDois())
			.append(UNION_ALL)
			.append(obterSqlConsultaTres())
			.append(obterOrderBy());
		
		return sql.toString();	
	}

	private String  obterSqlProjectionGenerica() {
		StringBuilder sqlProjection =  new StringBuilder(2000);
		
		sqlProjection.append(SELECT)
			//  EAI.ESPECIALIDADE_AIH
			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name())
        	.append(AS).append(ClinicaPorProcedimentoVO.Fields.ESPECIALIDADE_AIH.toString())
        	.append(',')
        	
        	//	CLC.DESCRICAO CLINICA
        	.append(ALIAS_CLC).append('.').append(AghClinicas.Fields.DESCRICAO.name())
        	.append(AS).append(ClinicaPorProcedimentoVO.Fields.CLINICA_DESCRICAO.toString())
        	.append(',')
        	
        	//	IPH.DESCRICAO PROCEDIMENTO
        	.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.DESCRICAO.name())
        	.append(AS).append(ClinicaPorProcedimentoVO.Fields.PROCEDIMENTO_DESCRICAO.toString())
        	.append(',');
        	
		return sqlProjection.toString();
	}
	
	private String obterSqlFromGenerico() {
		
		StringBuilder sqlFromBasico =  new StringBuilder(2000);
		
		sqlFromBasico.append(FROM)
		
			.append(AghClinicas.class.getAnnotation(Table.class).schema()).append('.')
			.append(AghClinicas.class.getAnnotation(Table.class).name()).append(ALIAS_CLC).append(", ")
			
			.append(FatItensProcedHospitalar.class.getAnnotation(Table.class).schema()).append('.')
			.append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(ALIAS_IPH).append(", ")
			
			.append(FatEspelhoAih.class.getAnnotation(Table.class).schema()).append('.')
			.append(FatEspelhoAih.class.getAnnotation(Table.class).name()).append(ALIAS_EAI).append(", ")
			
			.append(FatContasHospitalares.class.getAnnotation(Table.class).schema()).append('.')
			.append(FatContasHospitalares.class.getAnnotation(Table.class).name()).append(ALIAS_CTH);
		
		return sqlFromBasico.toString();
	}
	
	private String obterSqlWhereGenerico(){
		StringBuilder sqlWhere =  new StringBuilder(500);
		sqlWhere
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name()).append(IGUAL)
				.append(ALIAS_CLC).append('.').append(AghClinicas.Fields.CODIGO.name())	
			
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.SEQP.name()).append(IGUAL).append(_1_)
			
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(IGUAL)
				.append(ALIAS_CTH).append('.').append(FatContasHospitalares.Fields.SEQ.name())
					
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.name()).append(" = ").append(Parameter.P_DT_HR_INICIO.getParametro())
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.DCI_CPE_MODULO.name()).append(" = ").append(Parameter.P_INT.getParametro())
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.DCI_CPE_MES.name()).append(" = ").append(Parameter.P_MES.getParametro())
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.DCI_CPE_ANO.name()).append(" = ").append(Parameter.P_ANO.getParametro())
			.append(AND).append(ALIAS_CTH).append('.').append(FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.name()).append(IS_NULL);

		return sqlWhere.toString();
	}
	
	private String obterSqlConsultaUm(){
		
		StringBuilder sql =  new StringBuilder(500);
		
		sql.append(obterSqlProjectionConsultaUm())
			.append(obterSqlFromConsultaUm())
			.append(obterWhereConsultaUm())
			.append(obterGroupBy());
		
		return sql.toString();
	}
	
	private String  obterSqlProjectionConsultaUm() {
		StringBuilder sqlProjection =  new StringBuilder(300);
		
		sqlProjection.append(obterSqlProjectionGenerica())
		
	     //	COUNT(*) * DECODE(IPH.IND_PROC_ESPECIAL, 'S', nvl(IPH.QTD_PROCEDIMENTOS_ITEM,1), 1)  QUANTIDADE
    	.append(COUNT_ALL)
    		.append(" * ")
    		.append(CASE)
    			.append(WHEN).append(_LP_)
    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_PROC_ESPECIAL.name())
    					.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'').append(_RP_)
    				.append(THEN)
        				.append(COALESCE)
        					.append('(')
        						.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.name())
        						.append(',').append(" 1")
        					.append(_RP_)
        		.append(ELSE)
        			.append(_LP_)
        				.append('1')
        			.append(_RP_)
        		.append(END).append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANTIDADE.toString())
	    .append(',')
       
    	//	SUM(EAI.VALOR_SADT_REALIZ)	VALOR_ESP_SADT
        .append(SUM)
        	.append("( ")
        		.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.VALOR_SADT_REALIZ.name())
        	.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SADT.toString())
    	.append(',')
    	
    	// SUM(EAI.VALOR_SH_REALIZ) VALOR_ESP_SERV_HOSP
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.VALOR_SH_REALIZ.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_HOSP.toString())
    	.append(',')
    	
    	// SUM(EAI.VALOR_SP_REALIZ) VALOR_ESP_SERV_PROF
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.VALOR_SP_REALIZ.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_PROF.toString())
    	.append(',')
    	
		// ,0 QUANT_PROC
		.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANT_PROC.toString())
    	.append(',')
    	
    	// SADT_PROC
		.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SADT_PROC.toString())
    	.append(',')
    	
    	 // SERV_HOSP_PROC
		.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SERV_HOSP_PROC.toString())
    	.append(',')
    	
    	 // SERV_PROF_PROC
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SERV_PROF_PROC.toString())
    	.append(',')

    	// COUNT(EAI.IPH_COD_SUS_REALIZ)  QUANT_AIH
    	.append(COUNT)
    		.append("( ")
    			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name())
			.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANT_AIH.toString())
    	.append(',')
    	
    	//	SUM(EAI.VALOR_SADT_REALIZ) SADT_AIH
        .append(SUM)
        	.append("( ")
        		.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.VALOR_SADT_REALIZ.name())
        	.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SADT_AIH.toString())
    	.append(',')
    	
    	// SUM(EAI.VALOR_SH_REALIZ) HOSP_AIH
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.VALOR_SH_REALIZ.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.HOSP_AIH.toString())
    	.append(',')
    	
    	// SUM(EAI.VALOR_SP_REALIZ) PROF_AIH
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.VALOR_SP_REALIZ.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.PROF_AIH.toString())
    	.append(',')
    	
    	// DECODE(IPH.IND_INTERNACAO, 'S', 1, 2) ORDEM
    	.append(CASE)
    			.append(WHEN)
    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.name())
    				.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'')
    				.append(THEN)
        				.append(_1_)
	        		.append(ELSE)
	        			.append(_2_)
        .append(END).append(AS).append(ClinicaPorProcedimentoVO.Fields.ORDEM.toString());
		
		return sqlProjection.toString();
	}
	
	private String obterSqlFromConsultaUm() {
		return obterSqlFromGenerico();
	}
	
	private String obterWhereConsultaUm() {
		
		StringBuilder sqlWhere =  new StringBuilder(500);
		
		sqlWhere.append(WHERE).append(" 1 = 1")
			// EAI.IPH_PHO_SEQ_REALIZ = IPH.PHO_SEQ
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.name()).append(IGUAL)
				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
			
			// EAI.IPH_SEQ_REALIZ = IPH.SEQ	
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.IPH_SEQ_REALIZ.name()).append(IGUAL)
				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.SEQ.name())

			.append(obterSqlWhereGenerico());
		
		return sqlWhere.toString();
	}
	
	private String obterSqlConsultaDois(){
		
		StringBuilder sql =  new StringBuilder(500);
		
		sql.append(obterSqlProjectionConsultaDois())
			.append(obterSqlFromConsultaDois())
			.append(obterWhereConsultaDois())
			.append(obterGroupBy());
		
		return sql.toString();
	}
	
	private String  obterSqlProjectionConsultaDois() {
		StringBuilder sqlProjection =  new StringBuilder(300);
		
		sqlProjection.append(obterSqlProjectionGenerica())

	     //	COUNT(*) * DECODE(IPH.IND_PROC_ESPECIAL, 'S', nvl(IPH.QTD_PROCEDIMENTOS_ITEM,1), 1)  QUANTIDADE
    	.append(COUNT_ALL)
    		.append(" * ")
    		.append(CASE)
    			.append(WHEN).append(_LP_)
    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_PROC_ESPECIAL.name())
    					.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'').append(_RP_)
    				.append(THEN)
        				.append(COALESCE)
        					.append('(')
        						.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.name())
        						.append(',').append(" 1")
        					.append(_RP_)
        		.append(ELSE)
        			.append(_LP_)
        				.append('1')
        			.append(_RP_)
        		.append(END).append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANTIDADE.toString())
	    .append(',')		
		
    	 // SUM(CAH.VALOR_SADT)
        .append(SUM)
        	.append("( ")
        		.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.VALOR_SADT.name())
        	.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SADT.toString())
    	.append(',')
    	
    	// SUM(CAH.VALOR_SERV_HOSP)
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.VALOR_SERV_HOSP.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_HOSP.toString())
    	.append(',')
    	
    	// SUM(CAH.VALOR_SERV_PROF)
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.VALOR_SERV_PROF.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_PROF.toString())
    	.append(',')
	
		// QUANT_PROC
		.append(COUNT_ALL).append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANT_PROC.toString())
    	.append(',')
    	
    	// SUM(CAH.VALOR_SADT) SADT_PROC
        .append(SUM)
        	.append("( ")
        		.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.VALOR_SADT.name())
        	.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SADT_PROC.toString())
    	.append(',')
    	
    	 // SUM(CAH.VALOR_SERV_HOSP) SERV_HOSP_PROC
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.VALOR_SERV_HOSP.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SERV_HOSP_PROC.toString())
    	.append(',')
    	
    	// SUM(CAH.VALOR_SERV_PROF ) SERV_PROF_PROC
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.VALOR_SERV_PROF.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SERV_PROF_PROC.toString())
    	.append(',')
    	
    	// 0 QUANT_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANT_AIH.toString())	
    	.append(',')
    		
    	 //13  ,0 SADT_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SADT_AIH.toString())
    	.append(',')
    	
    	//14  ,0 HOSP_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.HOSP_AIH.toString())
    	.append(',')
    	
    	//15  ,0 PROF_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.PROF_AIH.toString())
    	.append(',')
    		
    	// DECODE(IPH.IND_INTERNACAO, 'S', 1, 2) ORDEM
    	.append(CASE)
    			.append(WHEN)
    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.name())
    				.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'')
    				.append(THEN)
        				.append(_1_)
	        		.append(ELSE)
	        			.append(_2_)
        .append(END).append(AS).append(ClinicaPorProcedimentoVO.Fields.ORDEM.toString());
		
		return sqlProjection.toString();
	}
	
	private String obterSqlFromConsultaDois() {
		
		StringBuilder sqlFrom =  new StringBuilder(500);
		
		sqlFrom.append(obterSqlFromGenerico())
			.append(" ,")
			
			.append(FatCampoMedicoAuditAih.class.getAnnotation(Table.class).schema()).append('.')
			.append(FatCampoMedicoAuditAih.class.getAnnotation(Table.class).name()).append(ALIAS_CAH);
		
		return sqlFrom.toString();
	}
	
	private String obterWhereConsultaDois() {

		StringBuilder sqlWhere =  new StringBuilder(500);
		
		sqlWhere.append(WHERE).append(" 1 = 1")
			// EAI.CTH_SEQ = CAH.EAI_CTH_SEQ
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(IGUAL)
				.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.name())

			// EAI.IPH_COD_SUS_REALIZ    <> CAH.IPH_COD_SUS
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(DIFERENTE)
				.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.IPH_COD_SUS.name())
			
			// CAH.IND_MODO_COBRANCA = 'V'	
			.append(AND).append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.IND_MODO_COBRANCA.name()).append(IGUAL)
				.append('\'').append(DominioModoCobranca.V.name()).append('\'')
			
			// CAH.IPH_SEQ = IPH.SEQ	
			.append(AND).append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.IPH_SEQ.name()).append(IGUAL)
				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.SEQ.name())
				
			// CAH.IPH_PHO_SEQ = IPH.PHO_SEQ	
			.append(AND).append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.IPH_PHO_SEQ.name()).append(IGUAL)
				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())	
				
			//NVL(CAH.IND_CONSISTENTE, 'D') <> 'R'
			.append(AND)
				.append(COALESCE)
	        		.append('(')
	        			.append(ALIAS_CAH).append('.').append(FatCampoMedicoAuditAih.Fields.IND_CONSISTENTE.name()).append(',')
	        				.append('\'').append(DominioTipoItemConta.D.name()).append('\'').append(_RP_)
	        	.append(DIFERENTE).append('\'').append(DominioTipoItemConta.R.name()).append('\'')
	        	
			.append(obterSqlWhereGenerico());
		
		return sqlWhere.toString();
	}
	
	private String obterSqlConsultaTres(){
		
		StringBuilder sql =  new StringBuilder(500);
		
		sql.append(obterSqlProjectionConsultaTres())
		.append(obterSqlFromConsultaTres())
		.append(obterWhereConsultaTres())
		.append(obterGroupBy());
		
		return sql.toString();
	}
	
	private String  obterSqlProjectionConsultaTres() {
		StringBuilder sqlProjection =  new StringBuilder(300);
		
		sqlProjection.append(obterSqlProjectionGenerica())

	     //	SUM(AAM.QUANTIDADE)  * DECODE(IPH.IND_PROC_ESPECIAL, 'S', nvl(IPH.QTD_PROCEDIMENTOS_ITEM,1), 1)  QUANTIDADE
    	.append(SUM)
    		.append('(').append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.QUANTIDADE.name()).append(_RP_)
    		.append(" * ")
    		.append(CASE)
    			.append(WHEN).append(_LP_)
    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_PROC_ESPECIAL.name())
    					.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'').append(_RP_)
    				.append(THEN)
        				.append(COALESCE)
        					.append('(')
        						.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.name())
        						.append(',').append(" 1")
        					.append(_RP_)
        		.append(ELSE)
        			.append(_LP_)
        				.append('1')
        			.append(_RP_)
        		.append(END).append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANTIDADE.toString())
	    .append(',')		
		
        	// SUM(AAM.VALOR_SADT)
	        .append(SUM)
	        	.append("( ")
	        		.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.VALOR_SADT.name())
        	.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SADT.toString())
        	.append(',')
        	
        	// SUM(AAM.VALOR_SERV_HOSP)
        	.append(SUM)
        		.append("( ")
        			.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_HOSP.toString())
        	.append(',')
        	
    	// SUM(AAM.VALOR_SERV_PROF)
        	.append(SUM)
        		.append("( ")
        			.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.VALOR_SERV_PROF.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.VALOR_ESP_SERV_PROF.toString())
        	.append(',')
		
		// SUM(NVL(AAM.QUANTIDADE,0)) QUANT_PROC
			.append(SUM).append('(')
				.append(COALESCE)
				.append('(')
					.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.QUANTIDADE.name()).append(", 0")
				.append(')')
			.append(')').append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANT_PROC.toString())
    	.append(',')
	    	
    	// SUM(AAM.VALOR_SADT) SADT_PROC
        .append(SUM)
        	.append("( ")
        		.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.VALOR_SADT.name())
        	.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SADT_PROC.toString())
    	.append(',')
    	
    	// SUM(AAM.VALOR_SERV_HOSP) SERV_HOSP_PROC
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.VALOR_SERV_HOSP.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SERV_HOSP_PROC.toString())
    	.append(',')
    	
    	// SUM(AAM.VALOR_SERV_PROF) SERV_PROF_PROC
    	.append(SUM)
    		.append("( ")
    			.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.VALOR_SERV_PROF.name())
    		.append(") ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SERV_PROF_PROC.toString())
    	.append(',')
    	
	    	// 0 QUANT_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.QUANT_AIH.toString())	
	    	.append(',')
	    		
    	 // 0 SADT_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.SADT_AIH.toString())
    	.append(',')
    	
    	// 0 HOSP_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.HOSP_AIH.toString())
    	.append(',')
    	
    	// 0 PROF_AIH
    	.append(" 0 ").append(AS).append(ClinicaPorProcedimentoVO.Fields.PROF_AIH.toString())
    	.append(',')
    	
	    	// DECODE(IPH.IND_INTERNACAO, 'S', 1, 2) ORDEM
	    	.append(CASE)
	    			.append(WHEN)
	    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.name())
	    				.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'')
	    				.append(THEN)
	        				.append(_1_)
		        		.append(ELSE)
		        			.append(_2_)
        .append(END).append(AS).append(ClinicaPorProcedimentoVO.Fields.ORDEM.toString());
		
		return sqlProjection.toString();
	}
	
	private String obterSqlFromConsultaTres() {
	
		StringBuilder sqlFrom =  new StringBuilder(500);
		
		sqlFrom.append(obterSqlFromGenerico())
			.append(" ,")
			
			.append(FatAtoMedicoAih.class.getAnnotation(Table.class).schema()).append('.')
			.append(FatAtoMedicoAih.class.getAnnotation(Table.class).name()).append(ALIAS_AAM);
		
		return sqlFrom.toString();
	}
	
	
	private String obterWhereConsultaTres() {

		StringBuilder sqlWhere =  new StringBuilder(500);

		sqlWhere.append(WHERE).append(" 1 = 1")
			
			// NVL(AAM.IND_CONSISTENTE, 'D') <> 'R'
			.append(AND)
				.append(COALESCE)
	        		.append('(')
	        			.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.IND_CONSISTENTE.name()).append(',')
	        				.append('\'').append(DominioTipoItemConta.D.name()).append('\'').append(_RP_)
	        	.append(DIFERENTE).append('\'').append(DominioTipoItemConta.R.name()).append('\'')
		
	       // EAI.CTH_SEQ = AAM.EAI_CTH_SEQ
	       .append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.CTH_SEQ.name()).append(" = ")
				.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ.name())
	        	
	        // EAI.IPH_COD_SUS_REALIZ <> AAM.IPH_COD_SUS	
			.append(AND).append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.name()).append(" <> ")
				.append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.IPH_COD_SUS.name())
				
			// AAM.IND_MODO_COBRANCA  = 'V'	
			.append(AND).append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.IND_MODO_COBRANCA.name()).append(IGUAL)
				.append('\'').append(DominioModoCobranca.V.name()).append('\'')	
	        	
			// AAM.IPH_SEQ = IPH.SEQ	
			.append(AND).append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.IPH_SEQ.name()).append(IGUAL)
				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.SEQ.name())
				
			// AAM.IPH_PHO_SEQ = IPH.PHO_SEQ	
			.append(AND).append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ.name()).append(IGUAL)
				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.PHO_SEQ.name())
				
			// AAM.TAO_SEQ <> <P_COD_ATO_OPM>
			.append(AND).append(ALIAS_AAM).append('.').append(FatAtoMedicoAih.Fields.TAO_SEQ.name()).append(DIFERENTE).append(Parameter.P_COD_ATO_OPM.getParametro())

			.append(obterSqlWhereGenerico());
		
		return sqlWhere.toString();
	}
	
	private String obterGroupBy(){
		
		StringBuilder sqlGroupBy =  new StringBuilder(500);
		
		sqlGroupBy.append(GROUP_BY)
			.append(ALIAS_EAI).append('.').append(FatEspelhoAih.Fields.ESPECIALIDADE_AIH.name()).append(", ")
			.append(ALIAS_CLC).append('.').append(AghClinicas.Fields.DESCRICAO.name()).append(", ")
			.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.DESCRICAO.name()).append(", ")
			
			//DECODE(IPH.IND_PROC_ESPECIAL, 'S', nvl(IPH.QTD_PROCEDIMENTOS_ITEM,1), 1)
			.append(CASE)
        		.append(WHEN).append(_LP_)
        			.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_PROC_ESPECIAL.name())
        				.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'').append(_RP_)
        			.append(THEN)
	        			.append(COALESCE)
	        					.append('(')
	        						.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.name())
	        						.append(',').append(" 1")
	        					.append(_RP_)
	        		.append(ELSE)
	        			.append(_LP_)
	        				.append('1')
	        			.append(_RP_)
	        .append(END).append(", ")
	        
			// DECODE(IPH.IND_INTERNACAO, 'S', 1, 2)
        	.append(CASE)
    			.append(WHEN)
    				.append(ALIAS_IPH).append('.').append(FatItensProcedHospitalar.Fields.IND_INTERNACAO.name())
    					.append(IGUAL).append('\'').append(DominioSimNao.S.toString()).append('\'')
				.append(THEN)
    				.append(_1_)
        		.append(ELSE)
        			.append(_2_)
	        .append(END);
		
		return sqlGroupBy.toString();
	}
	
	private String obterOrderBy(){
		StringBuilder sqlOrderBy =  new StringBuilder(500);
		sqlOrderBy
			.append(ORDER_BY)
			/*.append(ClinicaPorProcedimentoVO.Fields.ORDEM.toString())
			.append(',')*/
			.append(ClinicaPorProcedimentoVO.Fields.ESPECIALIDADE_AIH.toString());
		
		return sqlOrderBy.toString();
	}
}

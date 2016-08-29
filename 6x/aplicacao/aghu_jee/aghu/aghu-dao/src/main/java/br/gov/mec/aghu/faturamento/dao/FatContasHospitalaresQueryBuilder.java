package br.gov.mec.aghu.faturamento.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Builder responsável pela consulta de {@link FatContasHospitalares} por {@link FatSituacaoSaidaPaciente}
 */
public class FatContasHospitalaresQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 8733901992426707567L;

	private FatSituacaoSaidaPaciente fatSituacaoSaidaPaciente;

	@Override
	protected DetachedCriteria createProduct() {

		return DetachedCriteria.forClass(FatContasHospitalares.class, "FCH");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {

		criteria = DetachedCriteria.forClass(FatContasHospitalares.class);
		criteria.createAlias(
				FatContasHospitalares.Fields.SITUACAO_SAIDA_PACIENTE.toString(),
				FatContasHospitalares.Fields.SITUACAO_SAIDA_PACIENTE.toString());
		criteria.createAlias(
				FatContasHospitalares.Fields.VALOR_CONTA_HOSPITALAR.toString(),
				FatContasHospitalares.Fields.VALOR_CONTA_HOSPITALAR.toString());
		criteria.createAlias(
				FatContasHospitalares.Fields.DIARIAS_UTI_DIGITADA.toString(),
				FatContasHospitalares.Fields.DIARIAS_UTI_DIGITADA.toString());
		criteria.add(Restrictions.eq(
				FatContasHospitalares.Fields.SITUACAO_SAIDA_PACIENTE.toString()
						+ "." + FatSituacaoSaidaPaciente.Fields.SEQ.toString(),
				fatSituacaoSaidaPaciente.getId().getSeq()));
		criteria.add(Restrictions.eq(
				FatContasHospitalares.Fields.SITUACAO_SAIDA_PACIENTE.toString()
						+ "."
						+ FatSituacaoSaidaPaciente.Fields.MSP_SEQ.toString(),
				fatSituacaoSaidaPaciente.getId().getMspSeq()));
	}

	/** 
	 * Constrói query a partir dos parâmetros. 
	 */
	public DetachedCriteria build(FatSituacaoSaidaPaciente fatSituacaoSaidaPaciente) {

		this.fatSituacaoSaidaPaciente = fatSituacaoSaidaPaciente;
		
		return super.build();
	}
	public SQLQuery getClausulasProtocolosAIh(SQLQuery q,final Integer prontuario, final String nomePaciente, final Integer codPaciente,
			final String leito, final Integer conta,final Date dtInternacao,final Date dtAlta,final Date dtEnvio,final String envio){
		
		DateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
		String data = null;
		
		if(prontuario != null){
			q.setInteger("prontuario", prontuario);
		}
		if(codPaciente != null){
			q.setInteger("codpaciente", codPaciente);
		}
		if(StringUtils.isNotBlank(leito)){
			q.setString("leito", leito);
		}
		if(conta != null){
			q.setInteger("seq", conta);
		}
		if(dtInternacao != null){
			data = formatter1.format(dtInternacao);
			q.setString("datainternacao", data);
		}
		if(dtAlta != null){
			data = formatter1.format(dtAlta);
			q.setString("dataalta", data);
		}
		if(dtEnvio != null){
			data = formatter1.format(dtEnvio);
			q.setString("dataenviosms", data);
		}
		if(StringUtils.isNotBlank(envio)){
			q.setString("envio", envio);
		}
		
		q.addScalar("prontuario",IntegerType.INSTANCE)
		 .addScalar("paciente", StringType.INSTANCE)
		 .addScalar("leito", StringType.INSTANCE)
		 .addScalar("conta", IntegerType.INSTANCE)
		 .addScalar("datainternacao", TimestampType.INSTANCE)
		 .addScalar("dataalta",TimestampType.INSTANCE)
		 .addScalar("dataenviosms", TimestampType.INSTANCE)
		 .addScalar("envio",StringType.INSTANCE);
		return q;
	}
	
	public StringBuilder getQueryProtocolosAIh(StringBuilder SQL,final Integer prontuario, final String nomePaciente, final Integer codPaciente,
			final String leito, final Integer conta,final Date dtInternacao,final Date dtAlta,final Date dtEnvio,final String envio){
		SQL.append("SELECT v1.pac_prontuario    prontuario, 		  v1.pac_nome               paciente, 		  v1.int_lto_id             leito, 		  c.seq                     conta, 		  c.dt_int_administrativa   datainternacao, 		  c.dt_alta_administrativa  dataalta, 		  c.dt_envio_sms            dataenviosms, 		  c.ind_enviado_sms         envio 		FROM agh.fat_contas_hospitalares c 		LEFT JOIN agh.v_fat_contas_hosp_pacientes v1 		ON v1.cth_seq                = c.seq 		WHERE ((c.ind_situacao       = 'E' 		AND (c.ind_autorizado_sms    = 'N' 		OR c.ind_autorizado_sms     IS NULL) 		AND ((c.tah_seq              = 1) 		OR (c.tah_seq                = 5 		AND c.nro_aih               IS NOT NULL))) 		OR (c.ind_situacao           = 'E' 		AND (c.ind_enviado_sms       = 'N' 		OR c.ind_enviado_sms        IS NULL) 		AND ((c.tah_seq              = 1) 		OR (c.tah_seq                = 5 		AND c.nro_aih               IS NOT NULL)))) ");
		if(prontuario != null){
			SQL.append("AND v1.pac_prontuario        = :prontuario ");
		}
		if(StringUtils.isNotBlank(null)){
			SQL.append("AND v1.pac_nome   like '%"+nomePaciente+"%' ");
		}
		if(codPaciente != null){
			SQL.append("AND v1.pac_codigo  =  :codpaciente ");
		}
		if(StringUtils.isNotBlank(leito)){
			SQL.append("AND v1.int_lto_id            = :leito ");
		}
		if(conta != null){
			SQL.append("AND c.seq                    = :seq ");
		}
		if(dtInternacao != null){
			SQL.append("AND to_char(c.dt_int_administrativa,'YYYYMMDD')  = :datainternacao ");
		}
		if(dtAlta != null){
			SQL.append("AND to_char(c.dt_alta_administrativa,'YYYYMMDD')  = :dataalta ");
		}
		if(dtEnvio != null){
			SQL.append("AND to_char(c.dt_envio_sms,'YYYYMMDD')  = :dataenviosms ");
		}
		if(StringUtils.isNotBlank(envio)){
			if("N".equals(envio)){
				SQL.append("AND (c.ind_enviado_sms  = :envio OR c.ind_enviado_sms  = null) ");
			}else{
				SQL.append("AND c.ind_enviado_sms  = :envio ");
			}
		}
		SQL.append("ORDER BY v1.pac_nome ");
		
		return SQL;
	}
	
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public StringBuilder getQUeryAihsFat(StringBuilder SQL,final Integer ano, final Integer mes, 
			final Date dtHrInicio, final String iniciaisPaciente){
		SQL.append("SELECT DCI.CODIGO_DCIH 		dcih , \n		  DCI.CODIGO_DCIH 			dcihlabel , \n  CLC.CODIGO 				codcli , \n		  CLC.DESCRICAO 			desccli , \n		  EAI.PAC_PRONTUARIO 		prontuario , \n		  EAI.PAC_NOME 				pacnome , \n  CTH.DT_INT_ADMINISTRATIVA dtint , \n		 CTH.DT_ALTA_ADMINISTRATIVA dtalta , \n		  CTH.NRO_AIH 				aih , \n		  EAI.IPH_COD_SUS_REALIZ 	ssmrealiz \n");
		
		// Relacionamento entre FAT_DOCUMENTO_COBRANCA_AIHS e  AGH_CLINICAS não existe
		SQL.append(" FROM AGH.FAT_DOCUMENTO_COBRANCA_AIHS DCI , \n");
		SQL.append("  AGH.AGH_CLINICAS CLC , \n");
		
		SQL.append("  AGH.FAT_CONTAS_HOSPITALARES CTH , \n");
		SQL.append("  AGH.FAT_ESPELHOS_AIH EAI \n");
		
		// Relacionamento entre FAT_DOCUMENTO_COBRANCA_AIHS e  AGH_CLINICAS não existe
		SQL.append(" WHERE CLC.CODIGO         = DCI.CLC_CODIGO \n");
		
		SQL.append(" AND CTH.DCI_CODIGO_DCIH  = DCI.CODIGO_DCIH \n");
		SQL.append(" AND EAI.CTH_SEQ          = CTH.SEQ \n");
		SQL.append(" AND DCI.CPE_MODULO       = 'INT' \n");
		if(ano != null){
			SQL.append(" AND DCI.CPE_ANO          = :pAno \n");
		}
		if(dtHrInicio != null){
			SQL.append(" AND DCI.CPE_DT_HR_INICIO = :pDtHr \n");
		}
		if(mes != null){
			SQL.append(" AND DCI.CPE_MES          = :pMes \n");
		}
		SQL.append(" AND EAI.SEQP             = 1 \n");
		if(StringUtils.isNotBlank(iniciaisPaciente)) {		
			SQL.append(" and upper(substr(eai.pac_nome,1,1) ) in (:prm_iniciais_paciente) \n");
		}
		SQL.append(" ORDER BY DCI.CODIGO_DCIH,  EAI.PAC_NOME \n");
		return SQL;
	}
	
	public StringBuilder getNutricaoEnteralDigitada(StringBuilder SQL){
		SQL.append("SELECT seq        as seq, \n dt_encerramento as dtencerramento, \n nro_aih         as nroaih, \n  ind_situacao    as  indsituacao\n FROM agh.fat_contas_hospitalares \n WHERE ind_situacao IN ('F','E' ) \n AND csp_cnv_codigo  = 1 \n AND csp_seq         = 1 \n AND seq            IN \n  (SELECT ich.cth_seq \n   FROM agh.FAT_PROCED_HOSP_INTERNOS phi, \n    agh.fat_itens_conta_hospitalar ich, \n    agh.fat_contas_hospitalares conta \n  WHERE phi.tipo_nutricao_enteral IS NOT NULL \n  AND phi.seq                      = ich.phi_seq \n  AND IND_ORIGEM                  IN ('ANU') \n  AND conta.seq                    = ich.cth_seq \n  AND conta.ind_situacao          IN ('F','E' ) \n  AND ich.cth_seq                 IN \n    (SELECT ich.cth_seq \n    FROM agh.FAT_PROCED_HOSP_INTERNOS phi, \n   agh.fat_itens_conta_hospitalar ich, \n     agh.fat_contas_hospitalares conta \n    WHERE phi.tipo_nutricao_enteral IS NOT NULL \n   AND phi.seq                      = ich.phi_seq \n    AND conta.seq                    = ich.cth_seq \n  AND conta.ind_situacao          IN ('F','E' ) \n    AND IND_ORIGEM                  IN ('DIG') \n   ) \n )");
		
		return SQL;
	}
	
}

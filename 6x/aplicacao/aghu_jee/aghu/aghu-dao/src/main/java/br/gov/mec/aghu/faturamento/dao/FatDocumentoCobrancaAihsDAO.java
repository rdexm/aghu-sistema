package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoCobrancaAih;
import br.gov.mec.aghu.faturamento.vo.AIHFaturadaPorPacienteVO;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatDocumentoCobrancaAihsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatDocumentoCobrancaAihs> {

	private static final long serialVersionUID = 6838027332365779505L;

	@SuppressWarnings("PMD.NPathComplexity")
	public FatDocumentoCobrancaAihs buscarPrimeiroDocumentosCobrancaAihDataFechamentoApresentacaoNulos(Byte clcCodigo, Byte tcsSeq,
			DominioTipoDocumentoCobrancaAih tipo, Date cpeDtHrInicio, Integer cpeMes, Integer cpeAno,
			DominioModuloCompetencia cpeModulo, String mesAnoAlta, Short quantidadeContasHosp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDocumentoCobrancaAihs.class);

		if (clcCodigo != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CLC_CODIGO.toString(), clcCodigo));
		}

		if (tcsSeq != null) {
			criteria.createAlias(FatDocumentoCobrancaAihs.Fields.FAT_TIPO_CLASSIF_SEC_SAUDE.toString(),
					FatDocumentoCobrancaAihs.Fields.FAT_TIPO_CLASSIF_SEC_SAUDE.toString());
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.TCS_SEQ.toString(), tcsSeq));
		}

		if (tipo != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.TIPO.toString(), tipo));
		}

		if (cpeDtHrInicio != null || cpeMes != null || cpeAno != null || cpeModulo != null) {
			criteria.createAlias(FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString(),
					FatDocumentoCobrancaAihs.Fields.FAT_COMPETENCIA.toString());
		}

		if (cpeDtHrInicio != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), cpeDtHrInicio));
		}

		if (cpeMes != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), cpeMes));
		}

		if (cpeAno != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), cpeAno));
		}

		if (cpeModulo != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), cpeModulo));
		}

		if (mesAnoAlta != null) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.MES_ANO_ALTA.toString(), mesAnoAlta));
		}

		if (quantidadeContasHosp != null) {
			criteria.add(Restrictions.lt(FatDocumentoCobrancaAihs.Fields.QUANTIDADE_CONTAS_HOSP.toString(), quantidadeContasHosp));
		}

		criteria.add(Restrictions.isNull(FatDocumentoCobrancaAihs.Fields.DT_FECHAMENTO.toString()));

		criteria.add(Restrictions.isNull(FatDocumentoCobrancaAihs.Fields.DT_APRESENTACAO.toString()));

		List<FatDocumentoCobrancaAihs> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public BigDecimal buscaMaxSequenciaDcih(Byte clcCodigo, Date cpeDtHrInicio, Integer cpeMes, Integer cpeAno,
			DominioModuloCompetencia cpeModulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDocumentoCobrancaAihs.class);

		criteria.setProjection(Projections.max(FatDocumentoCobrancaAihs.Fields.SEQUENCIA.toString()));

		criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CLC_CODIGO.toString(), clcCodigo));

		criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), cpeDtHrInicio));

		criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), cpeMes));

		criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), cpeAno));

		criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), cpeModulo));

		BigDecimal sequencia = (BigDecimal) this.executeCriteriaUniqueResult(criteria);

		if (sequencia == null) {
			sequencia = BigDecimal.ONE;
		} else {
			sequencia = sequencia.add(BigDecimal.ONE);
		}

		return sequencia;
	}
	
	
	
	public List<AIHFaturadaPorPacienteVO> obterAIHsFaturadasPorPaciente(final Date dtHrInicio, final Integer ano, final Integer mes, final String iniciaisPaciente, final Boolean reapresentada, final Integer clinica){
		
		final StringBuffer sql = new StringBuffer(1100);
		
		sql.append("select ")
		   .append(" dci.codigo_dcih 	 		 as codigodcih ")
		   .append(" ,clc.codigo     	 		 as codigo ")
		   .append(" ,clc.descricao  	 		 as descricao ")
		   .append(" ,eai.pac_prontuario 		 as prontuario ")
		   .append(" ,eai.pac_nome		 		 as pacnome ")
		   .append(" ,cth.seq 			 		 as cthseq ")
		   .append(" ,cth.dt_int_administrativa  as datainternacaoadministrativa ")
		   .append(" ,cth.dt_alta_administrativa as dtaltaadministrativa ")
		   .append(" ,cth.nro_aih				 as nroaih ")
		   .append(" ,eai.iph_cod_sus_realiz	 as iphcodsusrealiz ")
		   
		   .append(" from ")
		   
		   .append("  agh.fat_documento_cobranca_aihs dci ")
		   .append(" ,agh.agh_clinicas                clc ")
		   .append(" ,agh.fat_contas_hospitalares     cth ")
		   .append(" ,agh.fat_espelhos_aih            eai ")
		   
		   .append(" where ")
		   
		   .append("     clc.codigo           = dci.clc_codigo ")
		   .append(" and cth.dci_codigo_dcih  = dci.codigo_dcih")
		   .append(" and eai.cth_seq          = cth.seq")
		   .append(" and dci.cpe_modulo       = :prm_modulo")
		   .append(" and dci.cpe_dt_hr_inicio >= :prm_dt_hr_inicio_ini")
		   .append(" and dci.cpe_dt_hr_inicio < :prm_dt_hr_inicio_fim")
		   .append(" and dci.cpe_mes          = :prm_mes ")
		   .append(" and dci.cpe_ano          = :prm_ano ")
		   .append(" and eai.seqp     		 = 1 ");
		
		if(clinica != null){
			sql.append(" and clc.codigo = :prmClinica ");
		}
		
		if(StringUtils.isNotBlank(iniciaisPaciente)) {			
			sql.append(" and upper(substr(eai.pac_nome,1,1)) in (:prm_iniciais_paciente)");
		}
		
		if(reapresentada){
			sql.append(" and cth.cth_seq_reapresentada is not null "); 
		} else {
			sql.append(" and cth.cth_seq_reapresentada is null ");
		}
		
		sql.append(" order by eai.pac_nome ");
		
		final SQLQuery q = createSQLQuery(sql.toString());
		
		q.setString("prm_modulo", DominioModuloCompetencia.INT.toString());

		if(dtHrInicio != null){     	
			q.setDate("prm_dt_hr_inicio_ini", DateUtil.obterDataComHoraFinal(dtHrInicio));
			q.setDate("prm_dt_hr_inicio_fim", DateUtil.adicionaDias(dtHrInicio,1));
		}

		if(mes != null){     	
			q.setInteger("prm_mes", mes); 
		}

		if(ano != null){     	
			q.setInteger("prm_ano", ano); 
		}
		
		if(clinica != null){
			q.setInteger("prmClinica", clinica);
		}
		
		if(StringUtils.isNotBlank(iniciaisPaciente)) {
			
			final List<String> lst = new ArrayList<String>();
			for(char a : iniciaisPaciente.toCharArray()){
				lst.add(Character.toString(a));
			}
			
			q.setParameterList("prm_iniciais_paciente", lst);
		}
		
		final List<AIHFaturadaPorPacienteVO> result =  q.addScalar("codigodcih",StringType.INSTANCE)
												 		.addScalar("codigo",IntegerType.INSTANCE)
												 		.addScalar("descricao", StringType.INSTANCE)
													    .addScalar("prontuario",IntegerType.INSTANCE)
													    .addScalar("pacnome", StringType.INSTANCE)
													    .addScalar("cthseq",IntegerType.INSTANCE)
													    .addScalar("datainternacaoadministrativa",DateType.INSTANCE)
													    .addScalar("dtaltaadministrativa",DateType.INSTANCE)
													    .addScalar("nroaih",LongType.INSTANCE)
													    .addScalar("iphcodsusrealiz",LongType.INSTANCE)
													    .setResultTransformer(Transformers.aliasToBean(AIHFaturadaPorPacienteVO.class)).list();
		
		return result;
	}

	public List<FatDocumentoCobrancaAihs> listarDocumentosCobranca(String codigoDcih) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDocumentoCobrancaAihs.class);

		if (StringUtils.isNotBlank(codigoDcih)) {
			criteria.add(Restrictions.eq(FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString(), codigoDcih));
		}

		return executeCriteria(criteria);
	}

}

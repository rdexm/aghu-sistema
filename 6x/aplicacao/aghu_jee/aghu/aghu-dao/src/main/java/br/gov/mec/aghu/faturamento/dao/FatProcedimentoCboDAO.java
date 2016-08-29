package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatProcedimentoCboDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedimentoCbo> {

	private static final long serialVersionUID = -1731266367758355507L;

	private DetachedCriteria obterCriteriaFatProcedimentoCbo() {
		return DetachedCriteria.forClass(FatProcedimentoCbo.class);
	}

	/**
	 * Listar FatProcedimentoCbo filtrando por iphSeq e iphPhoSeq
	 * 
	 * @param iphSeq
	 * @param iphPhoSeq
	 * @return List<FatProcedimentoCbo>
	 */
	public List<FatProcedimentoCbo> listarProcedimentoCboPorIphSeqEPhoSeq(Integer iphSeq, Short iphPhoSeq) {
		DetachedCriteria criteria = this.obterCriteriaFatProcedimentoCbo();

		criteria.createAlias(FatProcedimentoCbo.Fields.ITEM_PROCED_HOSP.toString(), FatProcedimentoCbo.Fields.ITEM_PROCED_HOSP.toString());
		criteria.createAlias(FatProcedimentoCbo.Fields.CBO.toString(), FatProcedimentoCbo.Fields.CBO.toString());
		
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		return executeCriteria(criteria);

	}

	/**
	 * Listar FatProcedimentoCbo filtrando por cbo
	 * 
	 * @param codigoCbo
	 * @return List<FatProcedimentoCbo>
	 */
	public List<FatProcedimentoCbo> listarProcedimentoCboPorCbo(String codigoCbo) {
		DetachedCriteria criteria = this.obterCriteriaFatProcedimentoCbo();

		// alias criado, devido ao order by, que pode nao ser pela chave da entidade.
		criteria.createAlias(FatProcedimentoCbo.Fields.ITEM_PROCED_HOSP.toString(), FatProcedimentoCbo.Fields.ITEM_PROCED_HOSP.toString());
		criteria.createAlias(FatProcedimentoCbo.Fields.CBO.toString(), FatProcedimentoCbo.Fields.CBO.toString());

		criteria.add(Restrictions.ilike(FatProcedimentoCbo.Fields.CBO.toString() + "." + FatCbos.Fields.CODIGO.toString(), codigoCbo, MatchMode.EXACT));

		criteria.addOrder(Order.asc(FatProcedimentoCbo.Fields.ITEM_PROCED_HOSP.toString() +"."+ FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		criteria.addOrder(Order.desc(FatProcedimentoCbo.Fields.DT_INICIO.toString()));
		
		
		return executeCriteria(criteria);
	}

	/**
	 * ORADB: FATK_CAP_UNI.C_CBO_PROC
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param cboProf
	 * @param dtRealizado
	 * @return
	 */
	public FatProcedimentoCbo obterFatProcedimentoCbo(final Short iphPhoSeq, final Integer iphSeq, final String cboProf, final Date dtRealizado) {
		final Date lastDay = DateUtil.truncaData(CoreUtil.obterUltimoDiaDoMes(new Date()));
		final Date dtRealizadoTruncado = DateUtil.truncaData(dtRealizado);
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoCbo.class, "PCB");

		criteria.createAlias(FatProcedimentoCbo.Fields.CBO.toString(), "CBO");

		criteria.add(Restrictions.eq("PCB." + FatProcedimentoCbo.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("PCB." + FatProcedimentoCbo.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("CBO." + FatCbos.Fields.CODIGO.toString(), cboProf));

		// AND   TRUNC(c_dt_realizacao) BETWEEN pcb.dt_inicio AND NVL(pcb.dt_fim,TRUNC(LAST_DAY(SYSDATE)))
		criteria.add(Restrictions.sqlRestriction(" ? BETWEEN {alias}.dt_inicio AND CASE WHEN {alias}.dt_fim IS NULL THEN ? ELSE {alias}.dt_fim END ",
				new Object[] { dtRealizadoTruncado, lastDay }, new Type[] { TimestampType.INSTANCE, TimestampType.INSTANCE }));

//		criteria.add(Restrictions.le("PCB." + FatProcedimentoCbo.Fields.DT_INICIO.toString(), dtRealizado));
//		criteria.add(Restrictions.or(Restrictions.ge("PCB." + FatProcedimentoCbo.Fields.DT_FIM.toString(), dtRealizado),
//					 				 Restrictions.ge("PCB." + FatProcedimentoCbo.Fields.DT_FIM.toString(),DateUtil.truncaData(lastDay))
//					 				)
//					);
		
		// AND   TRUNC(c_dt_realizacao) BETWEEN cbo.dt_inicio AND NVL(cbo.dt_fim,TRUNC(LAST_DAY(SYSDATE)));
		criteria.add(Restrictions.sqlRestriction(" ? BETWEEN cbo1_.dt_inicio AND CASE WHEN cbo1_.dt_fim IS NULL THEN ? ELSE cbo1_.dt_fim END ",
				new Object[] { dtRealizadoTruncado, lastDay }, new Type[] { TimestampType.INSTANCE, TimestampType.INSTANCE }));

//		criteria.add(Restrictions.le("CBO." + FatCbos.Fields.DT_INICIO.toString(), dtRealizado));
//		criteria.add(Restrictions.or(Restrictions.ge("CBO." + FatCbos.Fields.DT_FIM.toString(), dtRealizado),
//					 				 Restrictions.ge("CBO." + FatCbos.Fields.DT_FIM.toString(),DateUtil.truncaData(lastDay))
//					 				)
//					);

		final List<FatProcedimentoCbo> pCbos = executeCriteria(criteria);

		if (pCbos != null && !pCbos.isEmpty()) {
			return pCbos.get(0);

		} else {
			return null;
		}
	}

	/**
	 * ORADB: FATK_CTH6_RN_UN.RN_CTHC_VALIDA_CBO_RESP.cursor.C_CBO_PROC
	 * 
	 * Ney 26/08/2011 Cbo por competencia
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param codigo
	 * @param competencia
	 * @return
	 */
	@SuppressWarnings("ucd")
	public FatProcedimentoCbo obterFatProcedimentoCboPorPhoSeqIphSeqPessoaTipoInformacoesECompetencia(final Short iphPhoSeq, final Integer iphSeq,
			final String codigo, final Date competencia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoCbo.class, "FPC");

		criteria.createAlias(FatProcedimentoCbo.Fields.CBO.toString(), "CBO");

		criteria.add(Restrictions.eq("FPC." + FatProcedimentoCbo.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("FPC." + FatProcedimentoCbo.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("CBO." + FatCbos.Fields.CODIGO.toString(), codigo));

		// Ney 26/08/2011 Cbo por competencia
		if (competencia != null) {
			// and trunc(c_data) between PII.DT_inicio and
			// nvl(PII.DT_FIM,trunc(sysdate))
			criteria.add(Restrictions.le("CBO." + RapPessoaTipoInformacoes.Fields.DT_INICIO.toString(), competencia));
			criteria.add(Restrictions.or(Restrictions.ge("CBO." + RapPessoaTipoInformacoes.Fields.DT_FIM.toString(), competencia),
					Restrictions.isNull("CBO." + RapPessoaTipoInformacoes.Fields.DT_FIM.toString())));
		}
		/*
		 * SELECT cbo.codigo FROM fat_cbos cbo, FAT_PROCEDIMENTOS_CBO pcb WHERE
		 * pcb.iph_pho_seq = c_iph_pho_seq and pcb.iph_seq = c_iph_seq and
		 * CBO.SEQ = PCB.CBO_SEQ and CBO.CODIGO = c_cbo_prof and
		 * trunc(c_dt_realizado) between CBO.DT_INICIO and
		 * nvl(CBO.DT_FIM,trunc(sysdate))
		 */

		final List<FatProcedimentoCbo> pCbos = executeCriteria(criteria);

		if (pCbos != null && !pCbos.isEmpty()) {
			return pCbos.get(0);

		} else {
			return null;
		}
	}

	public DetachedCriteria obterCriteriaPorPhoIphSeqCboCompetencia(final Short pPhoSeq, final Integer pIphSeq, final String valor,
			final Date dtRealizado) {
		/*
		 * CURSOR c_cbo_proc (c_iph_pho_seq IN NUMBER, c_iph_seq IN NUMBER,
		 * c_cbo_prof IN VARCHAR2 ,c_dt_realizado in date) IS SELECT cbo.codigo
		 * FROM fat_cbos cbo, FAT_PROCEDIMENTOS_CBO pcb WHERE pcb.iph_pho_seq =
		 * c_iph_pho_seq and pcb.iph_seq = c_iph_seq and CBO.SEQ = PCB.CBO_SEQ
		 * and CBO.CODIGO = c_cbo_prof and trunc(c_dt_realizado) between
		 * CBO.DT_INICIO and nvl(CBO.DT_FIM,trunc(sysdate)) ;
		 */

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoCbo.class);
		criteria.createAlias(FatProcedimentoCbo.Fields.CBO.toString(), "CBO");
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.IPH_SEQ.toString(), pIphSeq));
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.IPH_PHO_SEQ.toString(), pPhoSeq));
		criteria.add(Restrictions.eq("CBO." + FatCbos.Fields.CODIGO.toString(), valor));
		criteria.add(Restrictions
				.sqlRestriction(
						isOracle() ? " trunc(?) between cbo1_.DT_INICIO and nvl(cbo1_.DT_FIM, trunc(sysdate)) "
								: "  date_trunc('month', ?::timestamp) between cbo1_.DT_INICIO and case when cbo1_.DT_FIM is null then date_trunc('month', now()) else cbo1_.DT_FIM end ",
						dtRealizado, DateType.INSTANCE));
		return criteria;
	}

	public FatProcedimentoCbo obterPorPhoIphSeqCboCompetencia(final Short pPhoSeq, final Integer pIphSeq, final String valor, final Date dtRealizado) {
		List<FatProcedimentoCbo> result = executeCriteria(obterCriteriaPorPhoIphSeqCboCompetencia(pPhoSeq, pIphSeq, valor, dtRealizado));
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public FatProcedimentoCbo obterPorPhoIphSeqCboSeqVigente(final Short pPhoSeq, final Integer pIphSeq, final Integer cboSeq) {
		List<FatProcedimentoCbo> result = executeCriteria(this.obterCriteriaPorPhoIphSeqCboSeqVigente(pPhoSeq, pIphSeq, cboSeq, false));
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
		
	}

	public Date obterMaiorCompetenciaSemDataFim() {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoCbo.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.max(FatProcedimentoCbo.Fields.DT_FIM.toString())));
		criteria.add(Restrictions.isNotNull(FatProcedimentoCbo.Fields.DT_FIM.toString()));
		Object obj = executeCriteriaUniqueResult(criteria);
		if (obj != null) {
			return (Date) obj;
		}
		return null;
	}
	
	public List<FatProcedimentoCbo> listarProcedimentosCboAtivosOrdenadoPorCodTabelaECbo() {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoCbo.class, "pc");
		criteria.createAlias("pc." + FatProcedimentoCbo.Fields.ITEM_PROCED_HOSP.toString(), "iph");
		criteria.createAlias("pc." + FatProcedimentoCbo.Fields.CBO.toString(), "cbo");
		criteria.add(Restrictions.isNull(FatProcedimentoCbo.Fields.DT_FIM.toString()));
		criteria.addOrder(Order.asc("iph." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
		criteria.addOrder(Order.asc("cbo." + FatCbos.Fields.CODIGO.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaPorPhoIphSeqCboSeqVigente(final Short pPhoSeq, final Integer pIphSeq, final Integer cboSeq, boolean vigente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoCbo.class);
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.IPH_SEQ.toString(), pIphSeq));
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.IPH_PHO_SEQ.toString(), pPhoSeq));
		criteria.add(Restrictions.eq(FatProcedimentoCbo.Fields.CBO_SEQ.toString(), cboSeq));
		criteria.add(Restrictions.isNull(FatProcedimentoCbo.Fields.DT_FIM.toString()));

		if(vigente){
			criteria.add(Restrictions.isNull(FatProcedimentoCbo.Fields.DT_FIM.toString()));
		}
		return criteria;
	}
	
	public Integer atualizarFatProcedimentoCbo(Integer cboSeq, Short iphPhoSeq, Integer iphSeq, Date dtFim) {
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();

		hql.append("update ")
		.append(FatProcedimentoCbo.class.getName())
		.append(" set ")
		.append(FatProcedimentoCbo.Fields.DT_FIM.toString())
		.append(" = :dtFim")
		
		.append(" where ")
		.append(FatProcedimentoCbo.Fields.CBO_SEQ.toString())
		.append(" = :cboSeq")
		.append(" and ")
		.append(FatProcedimentoCbo.Fields.IPH_PHO_SEQ.toString())
		.append(" = :iphPhoSeq")
		.append(" and ")
		.append(FatProcedimentoCbo.Fields.IPH_SEQ.toString())
		.append(" = :iphSeq")
		.append(" and ")
		.append(FatProcedimentoCbo.Fields.DT_FIM.toString())
		.append(" is null");

		query = createHibernateQuery(hql.toString());
		query.setParameter("dtFim", dtFim);
		query.setParameter("cboSeq", cboSeq);
		query.setParameter("iphPhoSeq", iphPhoSeq);
		query.setParameter("iphSeq", iphSeq);
		
		return query.executeUpdate();
	}

	public FatProcedimentoCbo obterPorPhoIphSeqCboSeq(final Short pPhoSeq, final Integer pIphSeq, final Integer cboSeq) {

		final DetachedCriteria criteria = this.obterCriteriaPorPhoIphSeqCboSeqVigente(pPhoSeq, pIphSeq, cboSeq, false);
		List<FatProcedimentoCbo> result = executeCriteria(criteria);

		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
}

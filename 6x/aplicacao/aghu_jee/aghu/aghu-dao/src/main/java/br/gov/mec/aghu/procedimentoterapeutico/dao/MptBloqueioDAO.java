package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.Date;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import br.gov.mec.aghu.model.MptBloqueio;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptBloqueioDAO extends BaseDao<MptBloqueio>{

	private static final long serialVersionUID = 5414545464654564L;
	/**
	 * #44227 
	 */
	
	
	private static final String PONTO 		= ".";
	private static final String ALIAS_BLQ   = "BLQ";
	private static final String ALIAS_TPS   = "TPS";
	private static final String ALIAS_SAL   = "SAL";
	private static final String ALIAS_LOA   = "LOA";
	private static final String ALIAS_SER   = "SER";
	private static final String ALIAS_RPF   = "RPF";
	private static final String ALIAS_JUS   = "JUS";
	

	public List<MptBloqueio> listarSalaEAcomodacaoIndisponiveis(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MptBloqueio filtro) {
		DetachedCriteria criteria = montarQuerySalaAcomodacaoIndisponiveis(filtro);
		
		criteria.addOrder(Order.desc(ALIAS_BLQ + PONTO + MptBloqueio.Fields.APARTIR_DE.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long listarSalaEAcomodacaoIndisponiveisCount(MptBloqueio filtro) {
		DetachedCriteria criteria = montarQuerySalaAcomodacaoIndisponiveis(filtro);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarQuerySalaAcomodacaoIndisponiveis(MptBloqueio filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptBloqueio.class, ALIAS_BLQ);
		
		criteria.createAlias(MptBloqueio.Fields.SER.toString(), ALIAS_SER, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SER + PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_RPF, JoinType.INNER_JOIN);
		criteria.createAlias(MptBloqueio.Fields.TPS.toString(), ALIAS_TPS, JoinType.INNER_JOIN);
		criteria.createAlias(MptBloqueio.Fields.SAL.toString(), ALIAS_SAL, JoinType.INNER_JOIN);
		criteria.createAlias(MptBloqueio.Fields.LOA.toString(), ALIAS_LOA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MptBloqueio.Fields.JUS.toString(), ALIAS_JUS, JoinType.INNER_JOIN);
		
		if(filtro.getTipoSessao() != null){
			criteria.add(Restrictions.eq(ALIAS_TPS + PONTO + MptTipoSessao.Fields.SEQ.toString(), filtro.getTipoSessao().getSeq()));
		}
		
		if(filtro.getSala() != null && filtro.getSala().getSeq() != null){
			criteria.add(Restrictions.eq(ALIAS_SAL + PONTO + MptSalas.Fields.SEQ.toString(), filtro.getSala().getSeq()));
		}
		
		if(filtro.getLocalAtendimento() != null && filtro.getLocalAtendimento().getSeq() != null){
			criteria.add(Restrictions.eq(ALIAS_LOA + PONTO + MptLocalAtendimento.Fields.SEQ.toString(), filtro.getLocalAtendimento().getSeq()));
		}
		
		return criteria;
	}
	
	/**
	 * #44202 - Verifica se possui bloqueio para o período.
	 * @param tpsSeq
	 * @param loaSeq
	 * @param dataIni
	 * @param dtFim
	 * @return
	 */
	public boolean possuiLocaisIndisponiveisNoPeriodo(Short tpsSeq, Short loaSeq, Date dataIni, Date dtFim) {
		DetachedCriteria criteria = obterCriteriaLocaisIndispNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim);
		
		return executeCriteriaExists(criteria);
	}

	public List<MptBloqueio> obterLocaisIndisponiveisNoPeriodo(Short tpsSeq, Short loaSeq, Date dataIni, Date dtFim) {
		DetachedCriteria criteria = obterCriteriaLocaisIndispNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim);
		criteria.addOrder(Order.desc(ALIAS_BLQ + PONTO + MptBloqueio.Fields.ATE.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaLocaisIndispNoPeriodo(Short tpsSeq,
			Short loaSeq, Date dataIni, Date dtFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptBloqueio.class, ALIAS_BLQ);
		
		criteria.createAlias(MptBloqueio.Fields.TPS.toString(), ALIAS_TPS, JoinType.INNER_JOIN);
		criteria.createAlias(MptBloqueio.Fields.SAL.toString(), ALIAS_SAL, JoinType.INNER_JOIN);
		criteria.createAlias(MptBloqueio.Fields.LOA.toString(), ALIAS_LOA, JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(ALIAS_TPS + PONTO +  MptTipoSessao.Fields.SEQ.toString(), tpsSeq));
		criteria.add(Restrictions.eq(ALIAS_LOA + PONTO +  MptLocalAtendimento.Fields.SEQ.toString(), loaSeq));
		
		Criterion crit1 = Restrictions.le(ALIAS_BLQ + PONTO + MptBloqueio.Fields.APARTIR_DE.toString(), dataIni);
		Criterion crit2 = Restrictions.ge(ALIAS_BLQ + PONTO + MptBloqueio.Fields.ATE.toString(), dtFim);
		Criterion crit3 = Restrictions.ge(ALIAS_BLQ + PONTO + MptBloqueio.Fields.APARTIR_DE.toString(), dataIni);
		Criterion crit4 = Restrictions.le(ALIAS_BLQ + PONTO + MptBloqueio.Fields.ATE.toString(), dtFim);
		
		Criterion crit5 = Restrictions.gt(ALIAS_BLQ + PONTO + MptBloqueio.Fields.APARTIR_DE.toString(), dataIni);
		Criterion crit6 = Restrictions.lt(ALIAS_BLQ + PONTO + MptBloqueio.Fields.APARTIR_DE.toString(), dtFim);
		Criterion crit7 = Restrictions.gt(ALIAS_BLQ + PONTO + MptBloqueio.Fields.ATE.toString(), dataIni);
		Criterion crit8 = Restrictions.lt(ALIAS_BLQ + PONTO + MptBloqueio.Fields.ATE.toString(), dtFim);
		
		criteria.add(Restrictions.or(Restrictions.or(Restrictions.and(crit1, crit2), Restrictions.and(crit3, crit4)),
				Restrictions.or(Restrictions.and(crit5, crit6), Restrictions.and(crit7, crit8))));
		return criteria;
	}
	
	public boolean existeVinculoBloqueio(MptJustificativa mptJustificativa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptBloqueio.class, "BLQ");
		criteria.createAlias("BLQ."+MptBloqueio.Fields.JUS.toString(), "JUS", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("JUS."+MptJustificativa.Fields.SEQ.toString(), mptJustificativa.getSeq()));
		
		return executeCriteriaExists(criteria);
	}
	
}

package br.gov.mec.aghu.protocolos.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptPrescIntervaloTempo;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.core.commons.CoreUtil;



public class MpaProtocoloAssistencialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaProtocoloAssistencial> {

	private static final long serialVersionUID = -2018226566702242366L;

	// #17309 - Listar sessões terapeuticas Quimio POL - C3 (lista de protocolos) 
	@SuppressWarnings("unchecked")
	public List<MpaProtocoloAssistencial> pesquisarMpaProtocoloAssistencial (Integer seqMptTratamentoTerapeutico, Integer firstResult, Integer maxResult){
		StringBuffer hql = new StringBuffer(montarHqlProtocolo());
		hql.append(" order by 1 ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqMptTratamentoTerapeutico", seqMptTratamentoTerapeutico);
		if (firstResult != null && maxResult != null) {
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResult);
		}
		List<Object[]> valores = query.list();
		List<MpaProtocoloAssistencial> lstRetorno = new ArrayList<MpaProtocoloAssistencial>();
		if(valores != null && valores.size() > 0){
			for (Object[] objects : valores) {
				MpaProtocoloAssistencial protocolo = new MpaProtocoloAssistencial();
				protocolo.setTitulo(objects[2].toString());
				lstRetorno.add(protocolo);
			}
		}
		return lstRetorno;
	}
	
	public Long pesquisarMpaProtocoloAssistencialCount (Integer seqMptTratamentoTerapeutico){
		Query query = createHibernateQuery(montarHqlProtocolo());
		query.setParameter("seqMptTratamentoTerapeutico", seqMptTratamentoTerapeutico);
		return (long) query.list().size();
	}
	
	//Utilizado hql na implementação pois com criteria não é possível fazer a restrição imposta pela consulta original. 
	protected String montarHqlProtocolo(){
		StringBuffer hql = new StringBuffer("SELECT DISTINCT pta." + MpaProtocoloAssistencial.Fields.SEQ.toString() + ", atd.")
		.append(AghAtendimentos.Fields.TRP_SEQ.toString() +  ", pta.")
		.append(MpaProtocoloAssistencial.Fields.TITULO.toString())
		.append(" FROM ")
		.append(MptPrescricaoPaciente.class.getName()).append(" pte ")
		.append(" LEFT JOIN pte." + MptPrescricaoPaciente.Fields.PRESC_INTERVALO_TEMPO+ " pei ")
		.append(" JOIN pte." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString()+ " atd, ")
		.append(MpaProtocoloAssistencial.class.getName()).append(" pta ")
		.append(" WHERE atd.").append(AghAtendimentos.Fields.TRP_SEQ.toString())
		.append(" = :seqMptTratamentoTerapeutico")
		.append(" AND pte.").append(MptPrescricaoPaciente.Fields.DTHR_VALIDA.toString())
		.append(" IS NOT NULL ")
		.append(" AND ( ")
		.append("pta.seq = (CASE WHEN pei." + MptPrescIntervaloTempo.Fields.CIT_VPA_PTA_SEQ.toString() + " IS NOT NULL THEN pei." + MptPrescIntervaloTempo.Fields.CIT_VPA_PTA_SEQ.toString() + " ELSE pte." + MptPrescricaoPaciente.Fields.CIT_VPA_PTA_SEQ.toString() + " END)")
		.append(')')
		;
		return hql.toString();
	}
	
	
	
	/**
	 * Carrega o SuggestionBox Protocolo. (C2)
	 * @param parametro
	 * @return List<MptTipoSessao>
	 */
	public List<MpaProtocoloAssistencial> buscarProtocolo(Object parametro, Short codigoTipoSessao) {
		DetachedCriteria criteria = montarCriteriaProtocolo(parametro, codigoTipoSessao);
		criteria.addOrder(Order.asc("PTA."+MpaProtocoloAssistencial.Fields.TITULO.toString()));
		return executeCriteria(criteria, 0, 100, null);
	}
	
	
	/**
	 * Carrega o SuggestionBox Protocolo. (C2)
	 * @param parametro
	 * @return Long
	 */
	public Long buscarProtocoloCount(Object parametro, Short codigoSala) {
		DetachedCriteria criteria = montarCriteriaProtocolo(parametro, codigoSala);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Carrega o SuggestionBox Protocolo. (C2)
	 * @param parametro
	 * @return DetachedCriteria
	 */
	public DetachedCriteria montarCriteriaProtocolo(Object parametro, Short codigoTipoSessao){
		String codigoDescricao = StringUtils.trimToNull(parametro.toString());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaProtocoloAssistencial.class, "PTA");
		
		criteria.createAlias("PTA."+MpaProtocoloAssistencial.Fields.MPA_VERSAO_PROT_ASSISTENCIALES.toString(), "VPA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("VPA."+MpaVersaoProtAssistencial.Fields.IND_SITUACAO.toString(), "L"));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(MptProtocoloCiclo.class, "PC");
		
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.property("PC."+MptProtocoloCiclo.Fields.SEQ.toString())));
		
		subQuery.createAlias("PC."+MptProtocoloCiclo.Fields.MPT_PRESCRICAO_CICLO.toString(), "PRC");
		subQuery.createAlias("PRC."+MptPrescricaoCiclo.Fields.MPT_SESSAO.toString(), "SE");
		subQuery.createAlias("SE."+MptSessao.Fields.HORARIO_SESSAO.toString(), "HS");
		subQuery.createAlias("HS."+MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AG");
		
		subQuery.add(Restrictions.eqProperty("PC." + MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL_ID.toString(), "VPA." + MpaVersaoProtAssistencial.Fields.ID.toString()));
		subQuery.add(Restrictions.eq("AG."+MptAgendamentoSessao.Fields.TPS_SEQ.toString(), codigoTipoSessao));
		
		criteria.add(Subqueries.exists(subQuery));		
		
		if (StringUtils.isNotEmpty(codigoDescricao)) {
			if (CoreUtil.isNumeroLong(codigoDescricao)) {
				criteria.add(Restrictions.eq("PTA."+MpaProtocoloAssistencial.Fields.SEQ.toString(), Short.parseShort(codigoDescricao))); 
			}else{
				criteria.add(Restrictions.ilike("PTA."+MpaProtocoloAssistencial.Fields.TITULO.toString(), codigoDescricao, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
}
	
		
	
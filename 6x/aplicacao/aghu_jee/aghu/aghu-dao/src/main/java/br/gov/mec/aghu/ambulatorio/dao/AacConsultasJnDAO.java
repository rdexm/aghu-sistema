package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoConsulta;
import br.gov.mec.aghu.model.AacConsultasJn;

public class AacConsultasJnDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultasJn> {

	private static final long serialVersionUID = -6931948919577197423L;

	public List<AacConsultasJn> obterHistoricoConsultasPorNumero(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasJn.class);
		
		criteria.createAlias(AacConsultasJn.Fields.SITUACAO_CONSULTA.toString(), "SC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AacConsultasJn.Fields.NUMERO.toString(), numero));
		criteria.addOrder(Order.desc(AacConsultasJn.Fields.ALTERADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AacConsultasJn> listarConsultasJnOrdenadoPorJnDateTimeDesc(Integer numeroConsulta, String indSituacaoConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasJn.class);

		criteria.add(Restrictions.eq(AacConsultasJn.Fields.NUMERO.toString(), numeroConsulta));

		criteria.add(Restrictions.eq(AacConsultasJn.Fields.SITUACAO_CONSULTA_SIT.toString(), indSituacaoConsulta));
		
		criteria.addOrder(Order.desc(AacConsultasJn.Fields.DATA_ALTERACAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AacConsultasJn> listaConsultasJn(Integer numeroConsulta, String indSituacaoConsulta) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacConsultasJn.class);
		
		criteria.add(Restrictions.eq(AacConsultasJn.Fields.NUMERO.toString(), numeroConsulta)).add(
				Restrictions.ilike(AacConsultasJn.Fields.SITUACAO_CONSULTA_SIT
						.toString(), indSituacaoConsulta));
		
		criteria.addOrder(Order.desc(AacConsultasJn.Fields.DATA_ALTERACAO
				.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public List<AacConsultasJn> pesquisarUsuariosMarcadorConsulta(Integer numeroConsulta){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacConsultasJn.class);
		
		criteria.add(Restrictions.eq(AacConsultasJn.Fields.NUMERO.toString(), numeroConsulta));
		criteria.add(
				Restrictions.or(
						Restrictions.eq(AacConsultasJn.Fields.IND_SIT_CONSULTA.toString(), "L"),
						Restrictions.eq(AacConsultasJn.Fields.STC_SITUACAO_COLUM.toString(), "L"))
						);
		
		criteria.addOrder(Order.desc(AacConsultasJn.Fields.JN_DATE_TIME.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	public AacConsultasJn pesquisarCondicaoAtendimentoGerada(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasJn.class);
		criteria.add(Restrictions.eq(AacConsultasJn.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(AacConsultasJn.Fields.IND_SIT_CONSULTA.toString(), DominioSituacaoConsulta.G));
		
		criteria.addOrder(Order.asc(AacConsultasJn.Fields.JN_DATE_TIME.toString()));
		
		List<AacConsultasJn> lista = executeCriteria(criteria);
		
		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null;
	}
	
}

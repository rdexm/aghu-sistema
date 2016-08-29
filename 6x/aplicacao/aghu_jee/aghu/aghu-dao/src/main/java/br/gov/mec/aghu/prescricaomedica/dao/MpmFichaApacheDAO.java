package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoApache;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmEscalaGlasgow;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MpmFichaApacheId;

/**
 * @see MpmFichaApache
 * 
 */
public class MpmFichaApacheDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmFichaApache> {

	private static final long serialVersionUID = -4674891503913655397L;
	private static final String SEPARADOR = ".";
	
	/**
	 * Método para buscar o próximo seqp a ser utilizado para o atendimento
	 * passado por parâmetro.
	 * 
	 * @param seqAtendimento
	 * @return
	 */
	public Short obterProximoSeqP(Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmFichaApache.class);

		criteria.setProjection(Projections.projectionList().add(
				Projections.max(MpmFichaApache.Fields.ID.toString() + SEPARADOR
						+ MpmFichaApacheId.Fields.SEQP.toString())));
		criteria.add(Restrictions.eq(
				MpmFichaApache.Fields.ATENDIMENTO.toString() + SEPARADOR
						+ AghAtendimentos.Fields.SEQ.toString(),
				seqAtendimento));
		
		Object o = this.executeCriteriaUniqueResult(criteria);
		
		Integer retorno = 0;
		
		if (o == null || Integer.valueOf(o.toString()).equals(0)) {
			retorno++;
		} else {
			retorno = Integer.valueOf(o.toString()) + 1;
		}

		return retorno.shortValue();
	}
	
	public List<Date> listarDataIngressoUnidadeOrdenadoPorSeqpDesc(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmFichaApache.class);

		criteria.add(Restrictions.eq(MpmFichaApache.Fields.ATD_SEQ.toString(), atdSeq));

		criteria.addOrder(Order.desc(MpmFichaApache.Fields.SEQP.toString()));

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property(MpmFichaApache.Fields.DTHR_INGRESSO_UNIDADE.toString()));

		criteria.setProjection(p);
		
		return this.executeCriteria(criteria);
	}

	public Boolean verificaPendenciaFichaApache(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmFichaApache.class);

		criteria.add(Restrictions.eq(MpmFichaApache.Fields.ATD_SEQ.toString(), atdSeq));

		criteria.addOrder(Order.desc(MpmFichaApache.Fields.SEQP.toString()));

		List<MpmFichaApache> result = this.executeCriteria(criteria);
		
		return result != null && !result.isEmpty() && DominioSituacaoApache.P.equals(result.get(0).getSituacaoApache());
	}

	/**
	 * Método que obtém as fichas apache de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmFichaApache> pesquisarFichasApachePorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmFichaApache.class);
		criteria.add(Restrictions.eq(MpmFichaApache.Fields.ATD_SEQ.toString(), atdSeq));
		List<MpmFichaApache> listaFichasApache = executeCriteria(criteria);
		return listaFichasApache;
	}
	
	public MpmFichaApache pesquisarFichaApacheComEscalaGrasgows(Integer atdSeq, Short seqp) {
		final StringBuilder hql = new StringBuilder(200).append("select apache from ");
		
		hql.append(MpmFichaApache.class.getName()).append(" apache ")
		
		.append(" where ")
		.append(" apache.").append(MpmFichaApache.Fields.ATD_SEQ.toString()).append(" = :atdSeq ")
		.append(" and apache.").append(MpmFichaApache.Fields.SEQP.toString()).append(" = :seqp ")
		.append(" and (exists (")

			.append(" select 1 from ")
			.append(MpmEscalaGlasgow.class.getName()).append(" egw ")
			.append(" where ")

			.append(" apache.").append(MpmFichaApache.Fields.ESCALA_GLASGOW.toString())
			.append(" = ")
			.append(" egw.").append(MpmEscalaGlasgow.Fields.SEQ.toString())
		
			.append(" and apache.").append(MpmFichaApache.Fields.ATD_SEQ.toString())
			.append(" = ")
			.append(" egw.").append(MpmEscalaGlasgow.Fields.ATENDIMENTO.toString())
		
		.append(") or apache.").append(MpmFichaApache.Fields.ESCALA_GLASGOW.toString()).append(" is null)");
		
		final Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("seqp", seqp);
		
		MpmFichaApache apache = (MpmFichaApache) query.uniqueResult();
		
		Hibernate.initialize(apache.getEscalaGlasgow());
		Hibernate.initialize(apache.getServidor());
		if(apache!= null && apache.getServidor() != null && apache.getServidor().getPessoaFisica() !=null){
			Hibernate.initialize(apache.getServidor().getPessoaFisica());
		}
		return apache;
	}
}
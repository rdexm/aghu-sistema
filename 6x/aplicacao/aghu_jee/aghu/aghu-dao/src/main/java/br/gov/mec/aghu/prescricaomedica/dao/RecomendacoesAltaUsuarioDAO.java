package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.MpmServRecomendacaoAltaId;
import br.gov.mec.aghu.model.RapServidores;

public class RecomendacoesAltaUsuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmServRecomendacaoAlta> {

	private static final long serialVersionUID = 5084432545145986904L;

	@Override
	protected void obterValorSequencialId(MpmServRecomendacaoAlta elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getSerMatricula() == null || elemento.getId().getSerVinCodigo() == null) {
			throw new IllegalArgumentException("MpmServRecomendacaoAltaId ou Servidor nao foi informado corretamente.");
		}

		Integer seqp = buscaMaxSeqRecomendacaoUsuario(elemento.getId()).intValue() + 1;
		elemento.getId().setSeqp(seqp);
	}
	
	/**
	 * Busca o maior sequencial associado a MpmServRecomendacaoAlta
	 * @param id
	 * @return
	 */
	private Integer buscaMaxSeqRecomendacaoUsuario(MpmServRecomendacaoAltaId id) {
		
		Integer returnValue = null;
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(recomendacao.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmServRecomendacaoAlta.class.getName()).append(" recomendacao");
		sql.append(" where recomendacao.").append(MpmServRecomendacaoAlta.Fields.SERVIDOR_MAT.toString()).append(" = :serMat ");
		sql.append(" and recomendacao.").append(MpmServRecomendacaoAlta.Fields.SERVIDOR_VIN.toString()).append(" = :serVin ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("serMat", id.getSerMatricula());
		query.setParameter("serVin", id.getSerVinCodigo());			
			
		Object maxSeq = query.getSingleResult();
		
		if (maxSeq == null) {
			returnValue = 0;
		} else {
			returnValue = (Integer)maxSeq; 
		}

		return returnValue;
	}


	/**
	 * Busca Recomendação.
	 * 
	 * @param {String} sigla
	 * @return {MpmServRecomendacaoAlta}
	 */
	public MpmServRecomendacaoAlta obterRecomendacaoAltaPorId(MpmServRecomendacaoAltaId id){

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmServRecomendacaoAlta.class);
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_MAT.toString(), id.getSerMatricula()));
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_VIN.toString(), id.getSerVinCodigo()));
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SEQP.toString(), id.getSeqp()));
		return (MpmServRecomendacaoAlta) executeCriteriaUniqueResult(criteria);

	}


	public Long pesquisarRecomendacoesUsuarioCount(RapServidores usuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmServRecomendacaoAlta.class);

		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_MAT.toString(), usuario.getId().getMatricula()));
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_VIN.toString(), usuario.getId().getVinCodigo()));

		return this.executeCriteriaCount(criteria);
	}
	
	public List<MpmServRecomendacaoAlta> pesquisarRecomendacoesUsuario(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores usuario) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmServRecomendacaoAlta.class);

		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_MAT.toString(), usuario.getId().getMatricula()));
		criteria.add(Restrictions.eq(MpmServRecomendacaoAlta.Fields.SERVIDOR_VIN.toString(), usuario.getId().getVinCodigo()));

		criteria.addOrder(Order
				.asc(MpmServRecomendacaoAlta.Fields.SEQP.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	/**
	 * Busca o maior sequencial associado a MpmServRecomendacaoAlta
	 * @param id
	 * @return
	 */
	public Boolean exiteVinculoAltaRecomendacoes(MpmServRecomendacaoAltaId id) {
		
		boolean returnValue = false;
		//MpmAltaRecomendacao
		StringBuilder sql = new StringBuilder(200);
		sql.append("select count(altaRecomendacao.id.seqp) as count ");
		sql.append("from ").append(MpmAltaRecomendacao.class.getName()).append(" altaRecomendacao");
		sql.append(" where altaRecomendacao.").append(MpmAltaRecomendacao.Fields.REC_ALTA_SERV_MAT.toString()).append(" = :serMat ");
		sql.append(" and altaRecomendacao.").append(MpmAltaRecomendacao.Fields.REC_ALTA_SERV_VIN.toString()).append(" = :serVin ");
		sql.append(" and altaRecomendacao.").append(MpmAltaRecomendacao.Fields.REC_ALTA_SERV_SEQ).append(" = :serSeqp ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("serMat", id.getSerMatricula());
		query.setParameter("serVin", id.getSerVinCodigo());
		query.setParameter("serSeqp", id.getSeqp());
		
			
		Object maxSeq = query.getSingleResult();
		
		if (maxSeq == null || ((Long)maxSeq)==0) {
			returnValue = false;
		} else {
			returnValue = true; 
		}

		return returnValue;
	}

}
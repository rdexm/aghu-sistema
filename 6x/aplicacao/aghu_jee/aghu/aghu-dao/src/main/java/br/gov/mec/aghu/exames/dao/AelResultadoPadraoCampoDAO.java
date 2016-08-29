package br.gov.mec.aghu.exames.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadoPadraoCampoId;

public class AelResultadoPadraoCampoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResultadoPadraoCampo> {
	
	private static final long serialVersionUID = 2794961917679958185L;


	@Override
    protected void obterValorSequencialId(AelResultadoPadraoCampo elemento) {
        if (elemento == null || elemento.getResultadoPadrao() == null) {
            throw new IllegalArgumentException("Parametro Invalido!!!");
        }

        AelResultadoPadraoCampoId id = new AelResultadoPadraoCampoId();
        id.setRpaSeq(elemento.getResultadoPadrao().getSeq());
        elemento.setId(id);
       
        Integer sequence = this.obterSequence(elemento.getId());
        if(sequence == null) {
            elemento.getId().setSeqp(Integer.valueOf(1));
        }else {
            elemento.getId().setSeqp(++sequence);
        }
    }


	/**
	 * Retorna um objeto AelResultadoPadrao <br>
	 * conforme parametros.
	 * 
	 * @param calSeq
	 * @param seqP
	 * @param rpaSeq
	 * @return
	 */
	public AelResultadoPadraoCampo obterResultadoPadraoCampoPorParametro(Integer calSeq, Integer seqP, Integer rpaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoPadraoCampo.class);

		criteria.createAlias(AelResultadoPadraoCampo.Fields.RESULTADO_CODIFICADO.toString(), "RCO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelResultadoPadraoCampo.Fields.RESULTADO_CARACTERISTICA.toString(), "RCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelResultadoPadraoCampo.Fields.DESCRICAO_RESUL_PADRAO.toString(), "RDR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelResultadoPadraoCampo.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelResultadoPadraoCampo.Fields.CAMPO_LAUDO.toString(), "CAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelResultadoPadraoCampo.Fields.RESULTADO_PADRAO.toString(), "RPA", JoinType.LEFT_OUTER_JOIN);
		
		this.obterCriteriaQueryResultadoPadraoCampoPorParametro(criteria, calSeq, seqP, rpaSeq);
		
		List<AelResultadoPadraoCampo> result = executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	
	private void obterCriteriaQueryResultadoPadraoCampoPorParametro(DetachedCriteria criteria,
			Integer calSeq, Integer seqP, Integer rpaSeq) {
		
		if(calSeq != null) {
			criteria.add(Restrictions.eq(AelResultadoPadraoCampo.Fields.CAL_SEQ.toString(), calSeq));
		}
		if(seqP != null) {
			criteria.add(Restrictions.ne(AelResultadoPadraoCampo.Fields.SEQP.toString(), seqP));
		}
		if(rpaSeq != null) {
			criteria.add(Restrictions.eq(AelResultadoPadraoCampo.Fields.RPA_SEQ.toString(), rpaSeq));
		}
	}
	
	
	public Integer obterSequence(AelResultadoPadraoCampoId id) {

		StringBuilder hql = new StringBuilder(80);
		hql.append("select max(o.").append(
				AelResultadoPadraoCampo.Fields.SEQP.toString());
		hql.append(") ");

		hql.append(" from ").append(AelResultadoPadraoCampo.class.getSimpleName())
				.append(" o ");
		
		if(id.getRpaSeq() != null && id.getSeqp() == null) {
			hql.append(" where o.")
			.append(AelResultadoPadraoCampo.Fields.RPA_SEQ.toString())
			.append(" = :rpaSeq ");
		}else {
			hql.append(" where o.")
					.append(AelResultadoPadraoCampo.Fields.ID.toString())
					.append(" = :entityId ");
		}	

		Query query = this.createQuery(hql.toString());
		if(id.getRpaSeq() != null && id.getSeqp() == null) {
			query.setParameter("rpaSeq", id.getRpaSeq());
		}else {
			query.setParameter("entityId", id);
		}

		Integer campo = null;
		@SuppressWarnings("rawtypes")
		List list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			campo = (Integer) list.get(0);
		}

		return campo;
	}
	
	
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelResultadoPadraoCampo.class);
    }
	
	public boolean existeDependenciaResultadoCodificado(Integer resultadoCodificado, Integer grupo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelResultadoPadraoCampo.Fields.RESULTADO_CODIFICADO.toString(), "rex", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq("rex."+AelResultadoCodificado.Fields.SEQ.toString(), resultadoCodificado));
		criteria.add(Restrictions.eq("rex."+AelResultadoCodificado.Fields.GTC_SEQ.toString(), grupo));
		return (executeCriteriaCount(criteria) > 0);
	}
	
	
	/**
	 * Lista registros da tabela <br>
	 * AEL_RESUL_PADROES_CAMPOS <br>
	 * por exame material.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @return
	 */
	public List<AelResultadoPadraoCampo> listarResultadoPadraoCampoPorExameMaterial(String emaExaSigla, Integer emaManSeq, Integer seqp){
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createAlias(AelResultadoPadraoCampo.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()), seqp));
		
		return this.executeCriteria(criteria);
		
	}
	
	
	/**
	 * Pesquisa AelResultadoPadraoCampo através do material de análise e o seqp do resultado padrão do campo
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @param rpaSeq
	 * @return
	 */
	public List<AelResultadoPadraoCampo> pesquisarResultadoPadraoCampoPorExameMaterialResultadoPadraoSeq(String emaExaSigla, Integer emaManSeq, Integer seqp, Integer rpaSeq){
		
		DetachedCriteria criteria = obterCriteria();
		
		criteria.createAlias(AelResultadoPadraoCampo.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()), seqp));
		criteria.add(Restrictions.eq(AelResultadoPadraoCampo.Fields.RPA_SEQ.toString(), rpaSeq));
		
		return this.executeCriteria(criteria);
		
	}
	
	/**
	 * Pesquisa AelResultadoPadraoCampo através do AelResultadosPadrao
	 * @param rpaSeq
	 * @return
	 */
	public List<AelResultadoPadraoCampo> pesquisarResultadoPadraoCampoPorResultadoPadraoSeq(Integer rpaSeq){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelResultadoPadraoCampo.Fields.RPA_SEQ.toString(), rpaSeq));
		return this.executeCriteria(criteria);
		
	}
	
	
	/**
	 * Obtém o número máximo do seqp através do exame material resultado
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return
	 */
	public Integer obterMaxSeqpPorExameMaterialResultado(String emaExaSigla, Integer emaManSeq){
		
		DetachedCriteria criteria = obterCriteria();
		
		criteria.setProjection(Projections.max(AelResultadoPadraoCampo.Fields.SEQP.toString()));
		
		criteria.createAlias(AelResultadoPadraoCampo.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		
		final Integer seqp = (Integer) this.executeCriteriaUniqueResult(criteria);
		
		// Quando o seqp for nulo o valor padrão é ZERO
		return seqp == null ? 0 : seqp; 
		
	}
	
}

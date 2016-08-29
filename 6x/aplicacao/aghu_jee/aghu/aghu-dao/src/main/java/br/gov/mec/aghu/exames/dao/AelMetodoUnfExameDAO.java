package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.model.AelMetodoUnfExame;
import br.gov.mec.aghu.model.AelMetodoUnfExameId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @author lalegre
 *
 */
public class AelMetodoUnfExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMetodoUnfExame> {
	
	private static final long serialVersionUID = -4883454204855422232L;

	@Override
	protected void obterValorSequencialId(AelMetodoUnfExame elemento) {
		if (elemento == null || elemento.getMetodo() == null || elemento.getUnfExecutaExames() == null) {
			throw new IllegalArgumentException("Método ou Unidade Executora de Exames nao foram informados corretamente.");
		}
		
		AelMetodoUnfExameId id = new AelMetodoUnfExameId();
		id.setMtdSeq(elemento.getMetodo().getSeq());
		id.setUfeEmaExaSigla(elemento.getUnfExecutaExames().getId().getEmaExaSigla());
		id.setUfeEmaManSeq(elemento.getUnfExecutaExames().getId().getEmaManSeq());
		id.setUfeUnfSeq(elemento.getUnfExecutaExames().getId().getUnfSeq().getSeq());
		buscaMaxSeqMetodoUnfExame(id);
	
		elemento.setId(id);
		
	}
	
	/**
	 * Busca o maior sequencial associado a MpmAltaSumario
	 * @param id
	 * @return
	 */
	private void buscaMaxSeqMetodoUnfExame(AelMetodoUnfExameId id) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMetodoUnfExame.class);
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.EXA_SIGLA.toString(), id.getUfeEmaExaSigla()));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.MAN_SEQ.toString(), id.getUfeEmaManSeq()));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.UNF_SEQ.toString(), id.getUfeUnfSeq()));
		//criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.MTD_SEQ.toString(), id.getMtdSeq()));
		criteria.setProjection(Projections.max(AelMetodoUnfExame.Fields.SEQP.toString()));
		
		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setSeqp(++seqp);
		
	}
	
	/**
	 * Retorna lista AelMetodoUnfExame por AelUnfExecutaExames
	 * @param unfExecutaExames
	 * @return
	 */
	public List<AelMetodoUnfExame> obterAelMetodoUnfExamePorUnfExecutaExames(AelUnfExecutaExames unfExecutaExames) {
		
		if (unfExecutaExames != null) {
			
			DetachedCriteria criteria = DetachedCriteria.forClass(AelMetodoUnfExame.class);
			
			criteria.createAlias(AelMetodoUnfExame.Fields.METODO.toString(), "MET", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.EXA_SIGLA.toString(), unfExecutaExames.getId().getEmaExaSigla()));
			criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.MAN_SEQ.toString(), unfExecutaExames.getId().getEmaManSeq()));
			criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.UNF_SEQ.toString(), unfExecutaExames.getId().getUnfSeq().getSeq()));
			
			return executeCriteria(criteria);
			
		} else {
			
			return null;
			
		}
		
	}
	
	/**
	 * Verifica se existe apenas um método ativo para um exame por unidade executora
	 * @param id
	 * @return
	 */
	public boolean possuiMetodosAtivos(AelMetodoUnfExameId id) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMetodoUnfExame.class);
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.EXA_SIGLA.toString(), id.getUfeEmaExaSigla()));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.MAN_SEQ.toString(), id.getUfeEmaManSeq()));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.UNF_SEQ.toString(), id.getUfeUnfSeq()));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ne(AelMetodoUnfExame.Fields.SEQP.toString(), id.getSeqp()));
		criteria.add(Restrictions.isNull(AelMetodoUnfExame.Fields.DATA_FIM.toString()));

		List<AelMetodoUnfExame> retorno = this.executeCriteria(criteria);
		
		if (retorno != null && !retorno.isEmpty()) {
			
			return true;
			
		}
		
		return false;
		
	}
	
	/**
	 * Verifica se existe apenas um método ativo para um exame por unidade executora
	 * @param id
	 * @return
	 */
	public List<AelMetodoUnfExame> obterAelMetodoUnfExame(AelMetodoUnfExame metodoUnfExame) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelMetodoUnfExame.class);
		
		final String exaSigla = metodoUnfExame.getUnfExecutaExames().getId().getEmaExaSigla();
		final Integer manSeq = metodoUnfExame.getUnfExecutaExames().getId().getEmaManSeq();
		final Short unSeq = metodoUnfExame.getUnfExecutaExames().getId().getUnfSeq().getSeq();
		final Short seq = metodoUnfExame.getMetodo().getSeq();
		
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.MAN_SEQ.toString(), manSeq));
		criteria.add(Restrictions.eq(AelMetodoUnfExame.Fields.UNF_SEQ.toString(), unSeq));
		if(seq != null){
			criteria.add(Restrictions.ne(AelMetodoUnfExame.Fields.SEQP.toString(), seq));
		}
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Retorna AelMetodoUnfExame original
	 * @param id
	 * @return
	 */
	public AelMetodoUnfExame obterOriginal(AelMetodoUnfExameId id) {
		
		StringBuilder hql = new StringBuilder(200);
		
		hql.append("select o.").append(AelMetodoUnfExame.Fields.METODO.toString());
		hql.append(", o.").append(AelMetodoUnfExame.Fields.DATA_INICIO.toString());
		hql.append(", o.").append(AelMetodoUnfExame.Fields.DATA_FIM.toString());
		hql.append(", o.").append(AelMetodoUnfExame.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelMetodoUnfExame.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelMetodoUnfExame.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelMetodoUnfExame.Fields.UNF_EXECUTA_EXAMES.toString());
		
		hql.append(" from ").append(AelMetodoUnfExame.class.getSimpleName()).append(" o ");
		
		hql.append(" left outer join o.").append(AelMetodoUnfExame.Fields.UNF_EXECUTA_EXAMES.toString());
		hql.append(" left outer join o.").append(AelMetodoUnfExame.Fields.SERVIDOR.toString());

		
		hql.append(" where o.").append(AelMetodoUnfExame.Fields.ID.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		AelMetodoUnfExame retorno = null;
		
		Object[] campos = null;
		@SuppressWarnings("rawtypes")
		List list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			campos = (Object[]) list.get(0);			
		}
		 
		if (campos != null) {
			
			retorno = new AelMetodoUnfExame();
			
			retorno.setId(id);
		
			retorno.setMetodo((AelMetodo) campos[0]);
			retorno.setDthrInicio((Date) campos[1]);
			retorno.setDthrFim((Date) campos[2]);
			retorno.setCriadoEm((Date) campos[3]);
			retorno.setSituacao((DominioSituacao) campos[4]);
			retorno.setServidor((RapServidores) campos[5]);
			retorno.setUnfExecutaExames((AelUnfExecutaExames) campos[6]);
			
		}		
		
		return retorno;
		
	}


}

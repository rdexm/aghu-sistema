package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.MamReceituarioCuidadoVO;
import br.gov.mec.aghu.estoque.vo.MaterialMDAFVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamItemReceitCuidado;
import br.gov.mec.aghu.model.MamReceituarioCuidado;


public class MamItemReceitCuidadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamItemReceitCuidado> {
	
	private static final long serialVersionUID = 510465055484661400L;
	
	/** 
	 * 
	 * @param seq
	 * @return
	 */
	
	//C3
	public List<MamReceituarioCuidadoVO> obterNomeEDescricaoReceita(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceitCuidado.class, "IRC");
	
			criteria.createAlias("IRC." + MamItemReceitCuidado.Fields.MAM_RECEITUARIO_CUIDADO.toString(), "MRC", JoinType.INNER_JOIN);
			criteria.createAlias("MRC." + MamReceituarioCuidado.Fields.PACIENTE.toString(), "P", JoinType.INNER_JOIN);
			criteria.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property("P." + AipPacientes.Fields.NOME.toString()), MamReceituarioCuidadoVO.Fields.NOME.toString())));
			criteria.add(Restrictions.eq("MRC." + MamReceituarioCuidado.Fields.SEQ.toString(), seq));
			criteria.setResultTransformer(Transformers.aliasToBean(MamReceituarioCuidadoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public String obterNomeReceita(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceitCuidado.class, "IRC");
	
			criteria.createAlias("IRC." + MamItemReceitCuidado.Fields.MAM_RECEITUARIO_CUIDADO.toString(), "MRC", JoinType.INNER_JOIN);
			criteria.createAlias("MRC." + MamReceituarioCuidado.Fields.PACIENTE.toString(), "P", JoinType.INNER_JOIN);
			criteria.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property("P." + AipPacientes.Fields.NOME.toString()))));
			criteria.add(Restrictions.eq("MRC." + MamReceituarioCuidado.Fields.SEQ.toString(), seq));
				
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MaterialMDAFVO> obterDescricaoReceita(Long seq, String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceitCuidado.class, "IRC");
	
			criteria.createAlias("IRC." + MamItemReceitCuidado.Fields.MAM_RECEITUARIO_CUIDADO.toString(), "MRC", JoinType.INNER_JOIN);
			criteria.createAlias("MRC." + MamReceituarioCuidado.Fields.PACIENTE.toString(), "P", JoinType.INNER_JOIN);
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("IRC." + MamItemReceitCuidado.Fields.DESCRICAO.toString()), MaterialMDAFVO.Fields.DESCRICAO.toString()));
			criteria.add(Restrictions.eq("MRC." + MamReceituarioCuidado.Fields.SEQ.toString(), seq));
			criteria.add(Restrictions.eq("P." + AipPacientes.Fields.NOME.toString(), nome));
			criteria.setResultTransformer(Transformers.aliasToBean(MaterialMDAFVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MamItemReceitCuidado> listarMamItemReceitCuidadoPorReceituarioCuidado(Long receituarioSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceitCuidado.class, "IRC");
		criteria.add(Restrictions.eq("IRC." + MamItemReceitCuidado.Fields.RCU_SEQ.toString(), receituarioSeq));
		return executeCriteria(criteria);
	}
	
	public Short buscarSeqp(Long seqReceituarioCuidado){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceitCuidado.class, "IRC");
		criteria.add(Restrictions.eq("IRC." + MamItemReceitCuidado.Fields.RCU_SEQ.toString(), seqReceituarioCuidado));
		criteria.setProjection(Projections
				.max("IRC." + MamItemReceitCuidado.Fields.SEQP.toString()));
		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		if (seqp == null) {
			seqp = 0;
		}
		return ++seqp;
	}
	
	
	   
		public List<MamItemReceitCuidado> obterMamItemReceitCuidadoPorConsulta(MamReceituarioCuidado mamReceituarioCuidado) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceitCuidado.class, "IRC");
				criteria.createAlias("IRC." + MamItemReceitCuidado.Fields.MAM_RECEITUARIO_CUIDADO.toString(), "MRC", JoinType.INNER_JOIN);
				criteria.add(Restrictions.eq("IRC." + MamItemReceitCuidado.Fields.MAM_RECEITUARIO_CUIDADO.toString(), mamReceituarioCuidado));
			return executeCriteria(criteria);
		}
		
		
		
		public void removerItemRelacionadoPorReceituario(Long seqReceituario){
			String hql = "delete from MamItemReceitCuidado where id.rcuSeq = "

			+ seqReceituario;

			this.createQuery(hql).executeUpdate();


		}
		
}



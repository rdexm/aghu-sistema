package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MamTipoItemEvolucaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTipoItemEvolucao> {

	private static final long serialVersionUID = 7433139340473153791L;
	
	public List<Long> buscaSeqTipoItemEvolucao(final Integer atdSeq, final Date data, final Integer seqMTIE, final Integer catSeq){
		final StringBuilder hql = new StringBuilder(300);
		
		hql.append("SELECT ")
		   .append(" EVO.").append(MamEvolucoes.Fields.SEQ.toString())
		   
		   .append(" FROM ")
		   .append(MamTipoItemEvolucao.class.getSimpleName()).append(" TIE, ")
		   .append(MamItemEvolucoes.class.getSimpleName()).append(" IEO, ")
		   .append(MamEvolucoes.class.getSimpleName()).append(" EVO, ")
		   .append(MamRegistro.class.getSimpleName()).append(" RGT ")
		   
		   .append(" WHERE 1=1 ")
		   .append("  AND RGT.").append(MamRegistro.Fields.ATD_SEQ.toString()).append(" = :PRM_ATD_SEQ ")
		   .append("  AND RGT.").append(MamRegistro.Fields.SEQ.toString()).append(" = EVO.").append(MamEvolucoes.Fields.RGT_SEQ.toString())
		   .append("  AND EVO.").append(MamEvolucoes.Fields.IND_PENDENTE.toString()).append(" = :PRM_IND_PENDENTE ")
		   .append("  AND EVO.").append(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()).append(" IS NULL ");
		   
		//se existe evolução no dia
		if (isOracle()) {
		    hql.append("  AND TRUNC(EVO.").append(MamEvolucoes.Fields.DTHR_VALIDA.toString()).append(") = :PRM_DTHR_VALIDA ");   
		} else {
			hql.append("  AND DATE_TRUNC('day', EVO.").append(MamEvolucoes.Fields.DTHR_VALIDA.toString()).append(") = :PRM_DTHR_VALIDA ");  
		}
		   
		hql.append("  AND EVO.").append(MamEvolucoes.Fields.SEQ.toString()).append(" = IEO.").append(MamItemEvolucoes.Fields.EVO_SEQ.toString())
		   .append("  AND IEO.").append(MamItemEvolucoes.Fields.TIE_SEQ.toString()).append(" = TIE.").append(MamTipoItemEvolucao.Fields.SEQ.toString())
		   .append("  AND TIE.").append(MamTipoItemEvolucao.Fields.CAG_SEQ.toString()).append(" = :PRM_CAG_SEQ ");
		   					
		   // conduta
		if(seqMTIE != null){
			hql.append("  AND TIE.").append(MamTipoItemEvolucao.Fields.SEQ.toString()).append(" <> :PRM_C_TIPO_ITEM ");
		}

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("PRM_ATD_SEQ", atdSeq);
		query.setParameter("PRM_IND_PENDENTE", DominioIndPendenteAmbulatorio.V);
		query.setDate("PRM_DTHR_VALIDA", DateUtil.truncaData(data));
		query.setParameter("PRM_CAG_SEQ", catSeq);
		
		if(seqMTIE != null){
			query.setParameter("PRM_C_TIPO_ITEM", seqMTIE);
		}
		
		
		/*
    AND tie.cag_seq            = c_categoria; --médica ou outros profissionais		
		 */
		
		return query.list();
	}

	public List<MamTipoItemEvolucao> buscaTipoItemEvolucaoAtivoOrdenado(Integer seq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (seq!=null){
			criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.SEQ.toString(), seq));	
		}
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MamTipoItemEvolucao> buscaTipoItemEvolucaoPorCategoriaOrdenado(Integer seq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ne(MamTipoItemEvolucao.Fields.SIGLA.toString(), "NA"));
		if (seq!=null){
			criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.CATEGORIA_PROFISSIONAL.toString() +".seq", seq));	
		}
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	
	
	public List<MamTipoItemEvolucao> pesquisarTipoItemEvolucaoOrdenado() {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	// #49956 C2
	public List<MamTipoItemEvolucao> pesquisarTipoItemEvolucaoBotoes(Integer cagSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.CAG_SEQ.toString(),cagSeq));
		criteria.add(Restrictions.ne(MamTipoItemEvolucao.Fields.SIGLA.toString(), "NA"));
		criteria.add(Restrictions.ne(MamTipoItemEvolucao.Fields.SIGLA.toString(), "Na"));
		criteria.add(Restrictions.ne(MamTipoItemEvolucao.Fields.SIGLA.toString(), "na"));
		criteria.add(Restrictions.ne(MamTipoItemEvolucao.Fields.SIGLA.toString(), "nA"));
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.ORDEM.toString()));
		
		return executeCriteria(criteria);
	}	
	
	@SuppressWarnings("unchecked")
	public List<Long> buscaSeqTipoItemEvolucaoPrescEnf(final Integer atdSeq, final Date data, final Integer seqMTIE, final Integer catSeq){
		final StringBuilder hql = new StringBuilder(400);
		
		hql.append("SELECT ")
		   .append(" EVO.").append(MamEvolucoes.Fields.SEQ.toString())
		   
		   .append(" FROM ")
		   .append(MamTipoItemEvolucao.class.getSimpleName()).append(" TIE, ")
		   .append(MamItemEvolucoes.class.getSimpleName()).append(" IEO, ")
		   .append(MamEvolucoes.class.getSimpleName()).append(" EVO, ")
		   .append(MamRegistro.class.getSimpleName()).append(" RGT ")
		   
		   .append(" WHERE 1=1 ")
		   .append("  AND RGT.").append(MamRegistro.Fields.ATD_SEQ.toString()).append(" = :PRM_ATD_SEQ ")
		   .append("  AND RGT.").append(MamRegistro.Fields.SEQ.toString()).append(" = EVO.").append(MamEvolucoes.Fields.RGT_SEQ.toString())
		   .append("  AND EVO.").append(MamEvolucoes.Fields.IND_PENDENTE.toString()).append(" = :PRM_IND_PENDENTE ")
		   .append("  AND EVO.").append(MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()).append(" IS NULL ");
		   
		//se existe evolução no dia
		if (isOracle()) {
		    hql.append("  AND TRUNC(EVO.").append(MamEvolucoes.Fields.DTHR_VALIDA.toString()).append(") = :PRM_DTHR_VALIDA ");   
		} else {
			hql.append("  AND DATE_TRUNC('day', EVO.").append(MamEvolucoes.Fields.DTHR_VALIDA.toString()).append(") = :PRM_DTHR_VALIDA ");  
		}
		   
		hql.append("  AND EVO.").append(MamEvolucoes.Fields.SEQ.toString()).append(" = IEO.").append(MamItemEvolucoes.Fields.EVO_SEQ.toString())
		   .append("  AND IEO.").append(MamItemEvolucoes.Fields.TIE_SEQ.toString()).append(" = TIE.").append(MamTipoItemEvolucao.Fields.SEQ.toString())
		   .append("  AND TIE.").append(MamTipoItemEvolucao.Fields.CAG_SEQ.toString()).append(" = :PRM_CAG_SEQ ");
		   					
		   // conduta
		if(seqMTIE != null){
			hql.append("  AND TIE.").append(MamTipoItemEvolucao.Fields.SEQ.toString()).append(" <> :PRM_C_TIPO_ITEM ");
		}
		
		hql.append("AND EVO.").append(MamEvolucoes.Fields.SEQ.toString());
		hql.append(" NOT IN ( ")
		   		   	.append(" SELECT EVO1.").append(MamEvolucoes.Fields.EVO_SEQ.toString())
					.append(" FROM ")
					.append(MamEvolucoes.class.getSimpleName()).append(" EVO1 ")
					.append(" WHERE EVO1.").append(MamEvolucoes.Fields.EVO_SEQ.toString()).append(" = ").append("EVO.").append(MamEvolucoes.Fields.SEQ.toString())
					.append(" AND EVO1.").append(MamEvolucoes.Fields.IND_PENDENTE.toString())
				    .append(" IN ('")
					    	.append(DominioIndPendenteAmbulatorio.P.toString()).append("', '")
						    .append(DominioIndPendenteAmbulatorio.V.toString()).append("', '")
						    .append(DominioIndPendenteAmbulatorio.E.toString()).append("', '")
						    .append(DominioIndPendenteAmbulatorio.A.toString())
						    .append("') ")
					.append(" )");
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("PRM_ATD_SEQ", atdSeq);
		query.setParameter("PRM_IND_PENDENTE", DominioIndPendenteAmbulatorio.V);
		query.setDate("PRM_DTHR_VALIDA", DateUtil.truncaData(data));
		query.setParameter("PRM_CAG_SEQ", catSeq);
		
		if(seqMTIE != null){
			query.setParameter("PRM_C_TIPO_ITEM", seqMTIE);
		}
		
		return query.list();
	}
	
	public List<Integer> buscaSeqTipoItemEvolucaoPorCategoria(Integer seq){

        final StringBuilder hql = new StringBuilder(300);
		
		hql.append("SELECT ")
		   .append(" TIE.").append(MamTipoItemEvolucao.Fields.SEQ.toString())
		   .append(" FROM ")
		   .append(MamTipoItemEvolucao.class.getSimpleName()).append(" TIE")
		   .append(" WHERE 1=1 ") 
		   .append("  AND TIE.").append(MamTipoItemEvolucao.Fields.CAG_SEQ.toString()).append(" = :PRM_CAG_SEQ ");
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("PRM_CAG_SEQ", seq);
		
		return query.list();
	}
	
	public MamTipoItemEvolucao tipoItemEvolucaoPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.SEQ.toString(), seq));
		return (MamTipoItemEvolucao) executeCriteriaUniqueResult(criteria);
	}

	public Long retornaQtdQuestionariosComTipoItensEvolucao(MamTipoItemEvolucao item) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class);
		criteria.add(Restrictions.eq(MamQuestionario.Fields.MAM_TIPO_ITEM_EVOLUCAO.toString() + ".seq", item.getSeq()));
		return executeCriteriaCount(criteria);
	}
	
	public List<MamTipoItemEvolucao> pesquisarListaTipoItemEvoulucaoPelaSigla(String sigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.add(Restrictions.ne(MamTipoItemEvolucao.Fields.SIGLA.toString(), sigla));
		return executeCriteria(criteria);
	}	

	public List<MamTipoItemEvolucao> pesquisarListaTipoItemEvoulucaoPorCategoriaProfissional(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.CATEGORIA_PROFISSIONAL.toString() + ".seq", seq));
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamTipoItemEvolucao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	
	
	/**
	 * #50701 - P3 - c_tie
	 * @param seqCategoriaProfissional
	 * @return List<MamTipoItemEvolucao>
	 */
	public List<MamTipoItemEvolucao> obterTipoItensAtivosPorCategoria(Integer seqCategoriaProfissional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoItemEvolucao.class);
		
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.CAG_SEQ.toString(), seqCategoriaProfissional));
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MamTipoItemEvolucao.Fields.RES_EXAMES.toString(), Boolean.TRUE));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(MamTipoItemEvolucao.Fields.SEQ.toString()), MamTipoItemEvolucao.Fields.SEQ.toString());
		criteria.setProjection(projList);
    	
    	criteria.setResultTransformer(Transformers.aliasToBean(MamTipoItemEvolucao.class));
		
		return executeCriteria(criteria);		
	}

}
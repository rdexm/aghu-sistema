package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptTipoIntercorrenciaVO;

public class MptTipoIntercorrenciaDAO extends BaseDao<MptTipoIntercorrencia> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8203192580298637838L;
	
	
	public List<MptTipoIntercorrencia> carregarTiposIntercorrencia(String pesquisa){
		DetachedCriteria criteria = criteriaCarregarTiposIntercorrencia(pesquisa);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptTipoIntercorrencia.Fields.SEQ.toString()),MptTipoIntercorrencia.Fields.SEQ.toString())
				.add(Projections.property(MptTipoIntercorrencia.Fields.DESCRICAO.toString()),MptTipoIntercorrencia.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptTipoIntercorrencia.class));

		return executeCriteria(criteria, 0, 100, MptTipoIntercorrencia.Fields.DESCRICAO.toString(), true);
	}
	
	public Long carregarTiposIntercorrenciaCount(String pesquisa){
		DetachedCriteria criteria = criteriaCarregarTiposIntercorrencia(pesquisa);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criteriaCarregarTiposIntercorrencia(String pesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoIntercorrencia.class);
		criteria.add(Restrictions.eq(MptTipoIntercorrencia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(pesquisa != null && StringUtils.isNotEmpty(pesquisa)){
			if(CoreUtil.isNumeroShort(pesquisa)){
				criteria.add(Restrictions.eq(MptTipoIntercorrencia.Fields.SEQ.toString(), Short.parseShort(pesquisa)));
				if(!executeCriteriaExists(criteria)){
					criteria = null;
					criteria = DetachedCriteria.forClass(MptTipoIntercorrencia.class);
					criteria.add(Restrictions.eq(MptTipoIntercorrencia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
					criteria.add(Restrictions.ilike(MptTipoIntercorrencia.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE));
					return criteria;
				}
			}else{
				criteria.add(Restrictions.ilike(MptTipoIntercorrencia.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	/**
	 * #46469 
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	public Long listarTiposIntercorrentesCount(String descricao, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoIntercorrencia.class);
		return executeCriteriaCount(criarCriteria(descricao, situacao, criteria));
	}
	
	/**
	 * #46469 C1
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	public List<MptTipoIntercorrencia> listarTiposIntercorrentes(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoIntercorrencia.class); 
		criteria = criarCriteria(descricao, situacao, criteria);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	/**
	 * #46469
	 * 
	 * @param descricao
	 * @param situacao
	 * @param criteria
	 * @return
	 */
	private DetachedCriteria criarCriteria(String descricao, DominioSituacao situacao, DetachedCriteria criteria) {
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MptTipoIntercorrencia.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(MptTipoIntercorrencia.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	/**
	 * #46469 
	 * 
	 * @param descricao
	 * @return
	 */
	public boolean verificarDescricaoExistente(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoIntercorrencia.class);
		criteria.add(Restrictions.eq(MptTipoIntercorrencia.Fields.DESCRICAO.toString(), descricao));
		return executeCriteriaExists(criteria);
	}
	
	public List<MptTipoIntercorrenciaVO> obterIntercorrencias(){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoIntercorrencia.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptTipoIntercorrencia.Fields.DESCRICAO.toString()), MptTipoIntercorrenciaVO.Fields.DESCRICAO.toString())
				.add(Projections.property(MptTipoIntercorrencia.Fields.SEQ.toString()), MptTipoIntercorrenciaVO.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(MptTipoIntercorrencia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptTipoIntercorrenciaVO.class));
		return executeCriteria(criteria);
	}
}

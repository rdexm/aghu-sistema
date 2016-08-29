package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MptTipoSessaoJn;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HistoricoSessaoJnVO;

public class MptTipoSessaoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptTipoSessaoJn> {

	private static final long serialVersionUID = 6572685454312424789L;
	
	/**#44223 C3
	 * 
	 * @param sessaoSeq
	 */
	private DetachedCriteria obterCriteriaHistoricoSessao(Short sessaoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTipoSessaoJn.class, "TSJ");
		criteria.createAlias("TSJ."+MptTipoSessaoJn.Fields.TPS_SEQ.toString(), "TPS", JoinType.INNER_JOIN);
		criteria.createAlias("TSJ."+MptTipoSessaoJn.Fields.UNID_FUNCIONAL.toString(), "UFN", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("TSJ."+MptTipoSessaoJn.Fields.SEQ.toString(), sessaoSeq));
		return criteria;
	}
	
	/**#44223 C3
	 * 
	 * @param sessaoSeq
	 */
	public Long obterHistoricoSessaoJnCount(Short sessaoSeq){
		DetachedCriteria criteria = obterCriteriaHistoricoSessao(sessaoSeq);
		return executeCriteriaCount(criteria);
	}
	
	/**#44223 C3
	 * 
	 * @param sessaoSeq
	 */
	public List<HistoricoSessaoJnVO> obterHistoricoSessaoJn(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short sessaoSeq){
		DetachedCriteria criteria = obterCriteriaHistoricoSessao(sessaoSeq);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.SEQ_JN.toString()),HistoricoSessaoJnVO.Fields.SEQ_JN.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.DATA_ALTERACAO.toString()),HistoricoSessaoJnVO.Fields.DATA_ALTERACAO.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.JN_USER.toString()),HistoricoSessaoJnVO.Fields.JN_USER.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.DESCRICAO.toString()),HistoricoSessaoJnVO.Fields.DESCRICAO_JN.toString())
				.add(Projections.property("UFN."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()),HistoricoSessaoJnVO.Fields.DESCRICAO_UNIDADE.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.TIPO_AGENDA.toString()),HistoricoSessaoJnVO.Fields.TIPO_AGENDA.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.TEMPO_FIXO.toString()),HistoricoSessaoJnVO.Fields.TEMPO_FIXO.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.TEMPO_DISPONIVEL.toString()),HistoricoSessaoJnVO.Fields.TEMPO_DISPONIVEL.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.TIPO_AGENDA.toString()),HistoricoSessaoJnVO.Fields.TIPO_AGENDA.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.IND_APAC.toString()),HistoricoSessaoJnVO.Fields.IND_APAC.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.IND_CONSENTIMENTO.toString()),HistoricoSessaoJnVO.Fields.IND_CONSENTIMENTO.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.IND_FREQUENCIA.toString()),HistoricoSessaoJnVO.Fields.IND_FREQUENCIA.toString())
				.add(Projections.property("TSJ."+MptTipoSessaoJn.Fields.IND_SITUACAO.toString()),HistoricoSessaoJnVO.Fields.IND_SITUACAO.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(HistoricoSessaoJnVO.class));
		criteria.addOrder(Order.desc("TSJ."+MptTipoSessaoJn.Fields.DATA_ALTERACAO.toString()));executeCriteria(criteria);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

}
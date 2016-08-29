package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AcomodacaoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MptLocalAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptLocalAtendimento> {

	private static final long serialVersionUID = 4713449855913231289L;
	
	private static final String ALIAS_LOA   = "LOA";
	private static final String PONTO 		= ".";

	public List<Short> listarLocaisPorSala(Short salaSeq, DominioTipoLocal acomodacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class);
		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.SAL_SEQ.toString(), salaSeq));
		
		if (!acomodacao.equals(DominioTipoLocal.T)) {
			criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), acomodacao));
		}
		criteria.addOrder(Order.asc(MptLocalAtendimento.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	public List<Short> obterLocaisAtendimentoPorSala(Short salaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class);
		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.SAL_SEQ.toString(), salaSeq));
		
		return executeCriteria(criteria);
	}

	public boolean verificarExisteLocalAtendimentoParaSala(Short salaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class);
		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.SEQ.toString()));

		if(salaSeq != null){
			criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.SAL_SEQ.toString(), salaSeq));	
		}
		
		return executeCriteriaExists(criteria);
	}

	private DetachedCriteria obterCriteriaListarMptLocaisAtendimentoPorSala(Short salaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class);
		if (salaSeq != null) {
			criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.SAL_SEQ.toString(), salaSeq));
		}
		return criteria;
	}
	
	public List<MptLocalAtendimento> listarMptLocaisAtendimentoPorSala(Short salaSeq,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaListarMptLocaisAtendimentoPorSala(salaSeq);
		
/*		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.DESCRICAO.toString()));
		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.TIPO_LOCAL.toString()));
		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.IND_RESERVA.toString()));		
		criteria.setProjection(Projections.property(MptLocalAtendimento.Fields.IND_SITUACAO.toString()));*/
		
		criteria.addOrder(Order.asc(MptLocalAtendimento.Fields.SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, null, true);
	}
	
	public Long listarMptLocaisAtendimentoPorSalaCount(Short salaSeq) {
		DetachedCriteria criteria = obterCriteriaListarMptLocaisAtendimentoPorSala(salaSeq);
		return executeCriteriaCount(criteria);
	}
	
	
	/**
	 * Carrega o SuggestionBox Acomodação. (C4)
	 * @param parametro
	 * @return List<MptTipoSessao>
	 */
	public List<MptLocalAtendimento> buscarLocalAtendimento(Object parametro, Short codigoSala) {
		DetachedCriteria criteria = montarCriteriaLocalAtendimento(parametro, codigoSala);
		criteria.addOrder(Order.asc("LOA."+MptLocalAtendimento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null);
	}
	
	/**
	 * Carrega o SuggestionBox Acomodação. (C4)
	 * @param parametro
	 * @return Long
	 */
	public Long buscarLocalAtendimentoCount(Object parametro, Short codigoSala) {
		DetachedCriteria criteria = montarCriteriaLocalAtendimento(parametro, codigoSala);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * 	Carrega o SuggestionBox Acomodação. (C4)
	 * @param parametro
	 * @return DetachedCriteria
	 */
	public DetachedCriteria montarCriteriaLocalAtendimento(Object parametro, Short codigoSala){
		String codigoDescricao = StringUtils.trimToNull(parametro.toString());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class, "LOA");
					
		criteria.add(Restrictions.eq("LOA."+MptLocalAtendimento.Fields.SAL_SEQ.toString(), codigoSala));
		
		if (StringUtils.isNotEmpty(codigoDescricao)) {
			if (CoreUtil.isNumeroLong(codigoDescricao)) {
				criteria.add(Restrictions.eq("LOA."+MptLocalAtendimento.Fields.SEQ.toString(), Short.parseShort(codigoDescricao))); 
			}else{
				criteria.add(Restrictions.ilike("LOA."+MptLocalAtendimento.Fields.DESCRICAO.toString(), codigoDescricao, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}

	public List<MptLocalAtendimento> pesquisarAcomodacaoPorSala(Short codigoSala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class, ALIAS_LOA);
		
		criteria.add(Restrictions.eq(ALIAS_LOA + PONTO + MptLocalAtendimento.Fields.SAL_SEQ.toString(), codigoSala));
		criteria.add(Restrictions.eq(ALIAS_LOA + PONTO + MptLocalAtendimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(ALIAS_LOA + PONTO + MptLocalAtendimento.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AcomodacaoVO> pesquisarAcomodacaoVOPorSala(Short salSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimento.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptLocalAtendimento.Fields.SEQ.toString())
						, AcomodacaoVO.Fields.LOA_SEQ.toString())
				.add(Projections.property(MptLocalAtendimento.Fields.DESCRICAO.toString())
						, AcomodacaoVO.Fields.DESCRICAO_LOCAL.toString()));
		
		criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MptLocalAtendimento.Fields.SAL_SEQ.toString(), salSeq));
		criteria.addOrder(Order.asc(MptLocalAtendimento.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AcomodacaoVO.class));
		
		return executeCriteria(criteria);
	}
	
}
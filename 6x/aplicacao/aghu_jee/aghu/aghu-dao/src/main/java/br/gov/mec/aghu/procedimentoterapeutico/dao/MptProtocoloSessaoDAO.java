package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.model.MptProtocoloAssociacao;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.protocolos.vo.ProtocoloSessaoVO;


public class MptProtocoloSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptProtocoloSessao> {

	private static final long serialVersionUID = 9069743268660153768L;

	public MptProtocoloSessao verificaProtocoloPorDescricao (String descrProtocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloSessao.class);
		criteria.add(Restrictions.eq(MptProtocoloSessao.Fields.TITULO.toString(), descrProtocolo));
		
		return (MptProtocoloSessao) executeCriteriaUniqueResult(criteria);
		
	}
	

	public MptProtocoloSessao obterProtocoloServidorResponsavel (Integer seqProtocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloSessao.class);
		criteria.add(Restrictions.eq(MptProtocoloSessao.Fields.SEQ.toString(), seqProtocolo));
		
		return (MptProtocoloSessao) executeCriteriaUniqueResult(criteria);
		
	}
	
	public MptProtocoloSessao obterProtocoloPorSeq (Integer seqProtocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloSessao.class);
		criteria.createAlias(MptProtocoloSessao.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MptProtocoloSessao.Fields.SEQ.toString(), seqProtocolo));
		
		return (MptProtocoloSessao) executeCriteriaUniqueResult(criteria);
		
	}

	public List<MptProtocoloSessao> listarProtocolos(String strPesquisa, Integer seqProtocolo) {
		
		DetachedCriteria criteria = this.listarProtocolosSuggestion(strPesquisa, seqProtocolo);
		criteria.addOrder(Order.asc("MPS." + MptProtocoloSessao.Fields.TITULO.toString()));		
				
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("MPS." + MptProtocoloSessao.Fields.SEQ.toString()).as(MptProtocoloSessao.Fields.SEQ.toString())))
				.add(Projections.property("MPS." + MptProtocoloSessao.Fields.TITULO.toString()).as(MptProtocoloSessao.Fields.TITULO.toString())));
		criteria.setResultTransformer(Transformers.aliasToBean(MptProtocoloSessao.class));
		
		return executeCriteria(criteria);
	}


	public Long listarProtocolosCount(String strPesquisa, Integer seqProtocolo) {		 
		DetachedCriteria criteria = this.listarProtocolosSuggestion(strPesquisa, seqProtocolo);		
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("MPS." + MptProtocoloSessao.Fields.SEQ.toString()).as(MptProtocoloSessao.Fields.SEQ.toString())))
				.add(Projections.property("MPS." + MptProtocoloSessao.Fields.TITULO.toString()).as(MptProtocoloSessao.Fields.TITULO.toString())));		
		criteria.setResultTransformer(Transformers.aliasToBean(MptProtocoloSessao.class));
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria listarProtocolosSuggestion(String strPesquisa, Integer seqProtocolo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, "MVPS");		
		
		criteria.createAlias("MVPS." + MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), "MPS", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MVPS." + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoProtocolo.L));
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike("MPS." + MptProtocoloSessao.Fields.TITULO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.ne("MPS." + MptProtocoloSessao.Fields.SEQ.toString(), seqProtocolo));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(MptProtocoloAssociacao.class, "MPA");
		subQuery.setProjection(Projections.property("MPA." + MptProtocoloAssociacao.Fields.SEQ_PROTOCOLO_SESSAO.toString()));
		
		criteria.add(Subqueries.propertyNotIn("MPS." + MptProtocoloSessao.Fields.SEQ.toString(), subQuery));
		
		return criteria;		
	}
	
	public Boolean verificarStatusProtocolo(Integer seqProtocolo, Integer seqVersaoProtocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, "MVPS");
		
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property("MPS." + MptProtocoloSessao.Fields.SEQ.toString()).as(MptProtocoloSessao.Fields.SEQ.toString())))
				.add(Projections.property("MPS." + MptProtocoloSessao.Fields.TITULO.toString()).as(MptProtocoloSessao.Fields.TITULO.toString())));
		
		criteria.createAlias("MVPS." + MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), "MPS", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MVPS." + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoProtocolo.L));
		
		criteria.add(Restrictions.eq("MPS." + MptProtocoloSessao.Fields.SEQ.toString(), seqProtocolo));
		
		criteria.add(Restrictions.eq("MVPS." + MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocolo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptProtocoloSessao.class));
		
		MptProtocoloSessao item = (MptProtocoloSessao) executeCriteriaUniqueResult(criteria);
		
		if(item != null){
			return true;
		}else{
			return false;
		}		
	}
	
	/**
	 * Consulta carrega todos os itens da versão do protocolo selecionado na grid.
	 * @param seqVersaoProtocoço
	 * @return 
	 */
	public ProtocoloSessaoVO obterItemVersaoProtocolo(Integer seqVersaoProtocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, "VPS");
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("PSE." + MptProtocoloSessao.Fields.TIPO_SESSAO_SEQ.toString()), ProtocoloSessaoVO.Fields.SEQ_TIPO_SESSAO.toString());
		projectionList.add(Projections.property("TPS." + MptTipoSessao.Fields.DESCRICAO.toString()), ProtocoloSessaoVO.Fields.DESCRICAO.toString());
		projectionList.add(Projections.property("VPS." + MptVersaoProtocoloSessao.Fields.QTD_CICLOS.toString()), ProtocoloSessaoVO.Fields.QTD_CICLO.toString());
		projectionList.add(Projections.property("VPS." + MptVersaoProtocoloSessao.Fields.DIAS_TRATAMENTO.toString()), ProtocoloSessaoVO.Fields.DIAS_TRATAMENTO.toString());
		criteria.setProjection(Projections.distinct(projectionList));
		
		criteria.createAlias("VPS." + MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), "PSE", JoinType.INNER_JOIN);		
		criteria.createAlias("PSE." + MptProtocoloSessao.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("VPS." + MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocolo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloSessaoVO.class));
		return (ProtocoloSessaoVO) executeCriteriaUniqueResult(criteria);
		
	}
}
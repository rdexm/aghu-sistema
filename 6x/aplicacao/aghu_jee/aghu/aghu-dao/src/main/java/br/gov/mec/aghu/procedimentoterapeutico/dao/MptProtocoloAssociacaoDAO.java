package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptProtocoloAssociacao;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.protocolos.vo.ProtocoloAssociadoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptProtocoloAssociacaoDAO extends BaseDao<MptProtocoloAssociacao> {

	private static final String MPA_PONTO = "MPA.";
	private static final long serialVersionUID = -4904364343340847100L;
	
	/**
	 * Pesquisa protocolos associação por protocolo.
	 * @param seqProtocoloSessao
	 * @return lista de protocolos associação
	 */
	public List<MptProtocoloAssociacao> obterProtocolosAssociacaoPorProtocolo(Integer seqProtocoloSessao) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloAssociacao.class);
		criteria.add(Restrictions.eq(MptProtocoloAssociacao.Fields.SEQ_PROTOCOLO_SESSAO.toString(), seqProtocoloSessao));
		
		return executeCriteria(criteria);
	}

	public List<ProtocoloAssociadoVO> listarProtocolosRelacionados(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer seqProtocolo) {
		List<MptProtocoloAssociacao> item = this.obterProtocolosAssociacaoPorProtocolo(seqProtocolo);
		
		if(item.size() > 0){
			DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloAssociacao.class, "MPA");
			
			criteria.setProjection(Projections.projectionList().add(Projections.property("PSE." + MptProtocoloSessao.Fields.TITULO.toString()), "titulo")
					.add(Projections.property(MPA_PONTO + MptProtocoloAssociacao.Fields.SEQ_PROTOCOLO_SESSAO.toString()), "seqProtocoloAssociacao")
					.add(Projections.property(MPA_PONTO + MptProtocoloAssociacao.Fields.AGRUPADOR.toString()), "agrupador")
					.add(Projections.property(MPA_PONTO + MptProtocoloAssociacao.Fields.SEQ.toString()), "seq"));		
			
			criteria.createAlias(MptProtocoloAssociacao.Fields.PROTOCOLO_SESSAO.toString(), "PSE", JoinType.INNER_JOIN);
			
			criteria.add(Restrictions.eq(MPA_PONTO + MptProtocoloAssociacao.Fields.AGRUPADOR.toString(), item.get(0).getAgrupador()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloAssociadoVO.class));
			
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);			
		}else{
			return null;
		}
		
	}

	public Long listarProtocolosRelacionadosCount(Integer seqProtocolo) {
		
		List<MptProtocoloAssociacao> item = this.obterProtocolosAssociacaoPorProtocolo(seqProtocolo);
		
		if(item.size() > 0){
			DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloAssociacao.class, "MPA");
			
			criteria.setProjection(Projections.projectionList().add(Projections.property("PSE." + MptProtocoloSessao.Fields.TITULO.toString()), "titulo")
					.add(Projections.property(MPA_PONTO + MptProtocoloAssociacao.Fields.SEQ_PROTOCOLO_SESSAO.toString()), "seqProtocoloAssociacao")
					.add(Projections.property(MPA_PONTO + MptProtocoloAssociacao.Fields.AGRUPADOR.toString()), "agrupador"));		
			
			criteria.createAlias(MptProtocoloAssociacao.Fields.PROTOCOLO_SESSAO.toString(), "PSE", JoinType.INNER_JOIN);
			
			criteria.add(Restrictions.eq(MPA_PONTO + MptProtocoloAssociacao.Fields.AGRUPADOR.toString(), item.get(0).getAgrupador()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloAssociadoVO.class));
			
			return executeCriteriaCount(criteria);			
		}else{
			return (long) 0;
		}
		
	}
	
	public List<MptProtocoloAssociacao> buscarAssociacoes(Integer protocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloAssociacao.class, "MPA");
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(MptProtocoloAssociacao.class, "MPA2");
		subQuery.setProjection(Projections.property("MPA2." + MptProtocoloAssociacao.Fields.AGRUPADOR.toString()));
		subQuery.add(Restrictions.eq("MPA2." + MptProtocoloAssociacao.Fields.SEQ_PROTOCOLO_SESSAO.toString(), protocolo));
		
		criteria.add(Subqueries.propertyIn(MPA_PONTO + MptProtocoloAssociacao.Fields.AGRUPADOR.toString(), subQuery));
		
		return executeCriteria(criteria);		
	}	

	public Integer gerarNovoAgrupador() {		 
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloAssociacao.class);
		criteria.setProjection(Projections.max(MptProtocoloAssociacao.Fields.AGRUPADOR.toString()));
		Integer item = (Integer) executeCriteriaUniqueResult(criteria); 
		if(item != null){
			return  item + 1;			
		}else{
			return 1;
		}
	}

	public Boolean verificarExistenciaProtocolo(Integer seqProtocoloSessao) {	
		List<MptProtocoloAssociacao> item = this.buscarAssociacoes(seqProtocoloSessao);
		if(item.size() > 0){
			return true;
		}else{
			return false;
		}		
	}
	
	public List<MptProtocoloAssociacao> buscarAssociacoesPorAgrupador(Integer agrupador){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloAssociacao.class);
		criteria.add(Restrictions.eq(MptProtocoloAssociacao.Fields.AGRUPADOR.toString(), agrupador));
		return executeCriteria(criteria);
	}

}

package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.DuracaoMedidasPreventivasVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.RapServidores;

public class MciDuracaoMedidaPreventivasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciDuracaoMedidaPreventiva> {

	private static final long serialVersionUID = -4314368515782385937L;

	// #36265 C1
	public List<MciDuracaoMedidaPreventiva> obterDuracaoMedidaPreventivaPorDescricaoSituacao(String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciDuracaoMedidaPreventiva.class);
		if(descricao != null){
			criteria.add(Restrictions.like(MciDuracaoMedidaPreventiva.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indSituacao != null){
			criteria.add(Restrictions.eq(MciDuracaoMedidaPreventiva.Fields.SITUACAO.toString(), indSituacao));
		}
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	// #36265 C1 COUNT
	public Long obterDuracaoMedidaPreventivaPorDescricaoSituacaoCount(String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciDuracaoMedidaPreventiva.class);
		if(descricao != null){
			criteria.add(Restrictions.like(MciDuracaoMedidaPreventiva.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indSituacao != null){
			criteria.add(Restrictions.eq(MciDuracaoMedidaPreventiva.Fields.SITUACAO.toString(), indSituacao));
		}
		return this.executeCriteriaCount(criteria);
	}
	
	// #36265 C2
	public MciDuracaoMedidaPreventiva obterDuracaoMedidaPreventivaPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciDuracaoMedidaPreventiva.class);
		criteria.add(Restrictions.eq(MciDuracaoMedidaPreventiva.Fields.SEQ.toString(), seq));		
		return (MciDuracaoMedidaPreventiva) this.executeCriteriaUniqueResult(criteria);
	}
	
	// #36265 D1
	public void remover(Short seq){
		MciDuracaoMedidaPreventiva entity = obterPorChavePrimaria(seq);
		remover(entity);
	}
	
	// #36265 I2
	public MciDuracaoMedidaPreventiva inserir(MciDuracaoMedidaPreventiva entity) {
		 super.persistir(entity);
		 return entity;
	}
	
	// #36265 U1
	public MciDuracaoMedidaPreventiva atualizarDuracao(MciDuracaoMedidaPreventiva entity) {
		 super.merge(entity);
		 return entity;
	}
	
	// #36265 
	public DuracaoMedidasPreventivasVO obterDuracaoMedidaPreventivaComRelacionamento(Short seq) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MciDuracaoMedidaPreventiva.class,"DMP");
			criteria.add(Restrictions.eq(MciDuracaoMedidaPreventiva.Fields.SEQ.toString(), seq));
			
			criteria.createAlias("DMP." + MciDuracaoMedidaPreventiva.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
			criteria.createAlias("DMP." + MciDuracaoMedidaPreventiva.Fields.SERVIDOR_MOVIMENTADO.toString(), "SER_MOVI", JoinType.LEFT_OUTER_JOIN);
			
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), DuracaoMedidasPreventivasVO.Fields.MATRICULA.toString())
					.add(Projections.property("SER." + RapServidores.Fields.CODIGO_VINCULO.toString()), DuracaoMedidasPreventivasVO.Fields.CODIGO_VINCULO.toString())
					.add(Projections.property("SER_MOVI." + RapServidores.Fields.MATRICULA.toString()), DuracaoMedidasPreventivasVO.Fields.MATRICULA_MOVI.toString())
					.add(Projections.property("SER_MOVI." + RapServidores.Fields.CODIGO_VINCULO.toString()), DuracaoMedidasPreventivasVO.Fields.CODIGO_VINCULO_MOVI.toString())
			);
				
			criteria.setResultTransformer(Transformers.aliasToBean(DuracaoMedidasPreventivasVO.class));
			
			
			return (DuracaoMedidasPreventivasVO) this.executeCriteriaUniqueResult(criteria);
	}
			
}

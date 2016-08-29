package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.FatBancoCapacidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatBancoCapacidade;

public class FatBancoCapacidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatBancoCapacidade> {

	private static final long serialVersionUID = 1296180880776099937L;
	
	
	public List<FatBancoCapacidadeVO> pesquisarBancosCapacidade(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica, Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteria(mes, ano, numeroLeitos, capacidade, utilizacao, clinica, true);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);	
	}	
	public Long pesquisarBancosCapacidadeCount(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica) {
		DetachedCriteria criteria = montarCriteria(mes, ano, numeroLeitos, capacidade, utilizacao, clinica, false);
		return executeCriteriaCount(criteria);	
	}
	
	private DetachedCriteria montarCriteria(Integer mes, Integer ano, Integer numeroLeitos, Integer capacidade, Integer utilizacao, AghClinicas clinica, boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatBancoCapacidade.class, "bca");
		criteria.createAlias("bca." + FatBancoCapacidade.Fields.CLINICA.toString(), "cli", JoinType.INNER_JOIN);
			
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("bca." + FatBancoCapacidade.Fields.MES.toString()), FatBancoCapacidadeVO.Fields.MES.toString()).add(
				Projections.property("bca." + FatBancoCapacidade.Fields.ANO.toString()), FatBancoCapacidadeVO.Fields.ANO.toString()).add(
				Projections.property("bca." + FatBancoCapacidade.Fields.CLINICA_ID.toString()), FatBancoCapacidadeVO.Fields.CLINICA.toString()).add(
				Projections.property("cli." + AghClinicas.Fields.DESCRICAO.toString()), FatBancoCapacidadeVO.Fields.CLINICA_DESCRICAO.toString()).add(
				Projections.property("bca." + FatBancoCapacidade.Fields.NRO_LEITOS.toString()), FatBancoCapacidadeVO.Fields.NRO_LEITOS.toString()).add(
				Projections.property("bca." + FatBancoCapacidade.Fields.CAPACIDADE.toString()), FatBancoCapacidadeVO.Fields.CAPACIDADE.toString()).add(
				Projections.property("bca." + FatBancoCapacidade.Fields.UTILIZADO.toString()), FatBancoCapacidadeVO.Fields.UTILIZACAO.toString()));
		
		if (mes != null) {
			criteria.add(Restrictions.eq(("bca." + FatBancoCapacidade.Fields.MES.toString()), mes));
		}
		if (ano != null) {
			criteria.add(Restrictions.eq(("bca." + FatBancoCapacidade.Fields.ANO.toString()), ano));
		}
		if (numeroLeitos != null) {
			criteria.add(Restrictions.eq(("bca." + FatBancoCapacidade.Fields.NRO_LEITOS.toString()), numeroLeitos));
		}
		if (capacidade != null) {
			criteria.add(Restrictions.eq(("bca." + FatBancoCapacidade.Fields.CAPACIDADE.toString()), capacidade));
		}
		if (utilizacao != null) {
			criteria.add(Restrictions.eq(("bca." + FatBancoCapacidade.Fields.UTILIZADO.toString()), utilizacao));
		}
		if (clinica != null) {
			criteria.add(Restrictions.eq(("bca." + FatBancoCapacidade.Fields.CLINICA_ID.toString()), clinica.getCodigo()));
		}
		if (ordenar) {
			criteria.addOrder(Order.desc("bca." + FatBancoCapacidade.Fields.ANO.toString()));
			criteria.addOrder(Order.desc("bca." + FatBancoCapacidade.Fields.MES.toString()));
			criteria.addOrder(Order.asc("bca." + FatBancoCapacidade.Fields.CLINICA_ID.toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(FatBancoCapacidadeVO.class));
		
		return criteria;
	}
	
}

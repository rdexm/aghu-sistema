package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;

public class SceItemDasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemDas> {
	
	private static final long serialVersionUID = 3350595511744094028L;



	public List<SceItemDas> pesquisarItensPorDevolucao(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemDas.class);
		criteria.createAlias(SceItemDas.Fields.DEVOLUCAO_ALMOXARIFADO.toString(), SceItemDas.Fields.DEVOLUCAO_ALMOXARIFADO.toString());
		criteria.createAlias(SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString(), SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString());
		criteria.add(Restrictions.eq(SceItemDas.Fields.DEVOLUCAO_ALMOXARIFADO.toString()+"."+SceDevolucaoAlmoxarifado.Fields.SEQ.toString(),seq));
		return executeCriteria(criteria);		
	}

	
	
	public List<SceItemDas> pesquisarItensComMaterialPorDevolucaoAlmoxarifado(Integer dalSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemDas.class);
		criteria.createAlias(SceItemDas.Fields.DEVOLUCAO_ALMOXARIFADO.toString(), SceItemDas.Fields.DEVOLUCAO_ALMOXARIFADO.toString());
		criteria.createAlias(SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString(), SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString());
		criteria.createAlias(SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString()+"."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString()+"."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString());
		criteria.createAlias(SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString()+"."+SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), SceItemDas.Fields.ESTOQUE_ALMOXARIFADO.toString()+"."+SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(),Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(SceItemDas.Fields.DEVOLUCAO_ALMOXARIFADO.toString()+"."+SceDevolucaoAlmoxarifado.Fields.SEQ.toString(),dalSeq));
		return executeCriteria(criteria);		
	}
}

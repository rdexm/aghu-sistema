package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTransplante;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;

public class MtxProcedimentoTransplantesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxProcedimentoTransplantes> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1821828293141594165L;


	/*
	 * C1
	 */
	public List<MtxProcedimentoTransplantes> obterProcedimentosAssociados(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MtxProcedimentoTransplantes mtxProcedimentoTransplantes, String procedimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxProcedimentoTransplantes.class, "PTR");
		
		criteria.createAlias("PTR."+MtxProcedimentoTransplantes.Fields.PCI_SEQ.toString(),"PCI", JoinType.INNER_JOIN);
		
		if(mtxProcedimentoTransplantes.getOrgao() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.TIPO_ORGAO.toString(), mtxProcedimentoTransplantes.getOrgao()));
		}
		
		if(mtxProcedimentoTransplantes.getIndSituacao() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_SITUACAO.toString(), mtxProcedimentoTransplantes.getIndSituacao()));
		}
		
		if(mtxProcedimentoTransplantes.getTipo() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_TIPO.toString(), mtxProcedimentoTransplantes.getTipo()));
		}
		
		if(procedimento != null && StringUtils.isNotBlank(procedimento)){
			criteria.add(Restrictions.ilike("PCI."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), procedimento));
		}
		criteria.addOrder(Order.asc("PTR."+MtxProcedimentoTransplantes.Fields.TIPO_ORGAO.toString()));
		criteria.addOrder(Order.asc("PCI."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	
	/*
	 * C2
	 * Consulta SB
	 */
	

	public List<MbcProcedimentoCirurgicos> obterListaProcedimentoTransplantes(String procedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if(StringUtils.isNumeric(procedimento) && StringUtils.isNotBlank(procedimento)&&(procedimento != null)){
			Integer seq = Integer.valueOf(procedimento);
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(), seq));
		}
		
		else if(StringUtils.isNotBlank(procedimento)&&(procedimento != null)){
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), procedimento, MatchMode.ANYWHERE));
		}	
		criteria.addOrder(Order.asc(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
		
	}

	public Long obterListaProcedimentoTransplantesCount(String procedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoCirurgicos.class);
		
		criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	
		if(StringUtils.isNumeric(procedimento) && StringUtils.isNotBlank(procedimento)&&(procedimento != null)){
			Integer seq = Integer.valueOf(procedimento);
			criteria.add(Restrictions.eq(MbcProcedimentoCirurgicos.Fields.SEQ.toString(), seq));
		}
		
		else if(StringUtils.isNotBlank(procedimento)&&(procedimento != null)){
			criteria.add(Restrictions.ilike(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), procedimento, MatchMode.ANYWHERE));
		}	
		
		return executeCriteriaCount(criteria);
	}	
	
	public Long pesquisarListaProcedimentoTransplantesCount(MtxProcedimentoTransplantes mtxProcedimentoTransplantes, String procedimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxProcedimentoTransplantes.class, "PTR");
		
		criteria.createAlias("PTR."+MtxProcedimentoTransplantes.Fields.PCI_SEQ.toString(),"PCI", JoinType.INNER_JOIN);
				
		if(mtxProcedimentoTransplantes.getOrgao() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.TIPO_ORGAO.toString(), mtxProcedimentoTransplantes.getOrgao()));
		}
		
		if(mtxProcedimentoTransplantes.getIndSituacao() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_SITUACAO.toString(), mtxProcedimentoTransplantes.getIndSituacao()));
		}
		
		if(mtxProcedimentoTransplantes.getTipo() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_TIPO.toString(), mtxProcedimentoTransplantes.getTipo()));
		}
		
		if((procedimento != null) && (StringUtils.isNotBlank(procedimento))){
			criteria.add(Restrictions.ilike("PCI."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), procedimento));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	
	public List<MtxProcedimentoTransplantes> verificarMtxProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxProcedimentoTransplantes.class, "PTR");
		
		criteria.createAlias("PTR."+MtxProcedimentoTransplantes.Fields.PCI_SEQ.toString(),"PCI", JoinType.INNER_JOIN);
		
		if(mtxProcedimentoTransplantes.getOrgao() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.TIPO_ORGAO.toString(), mtxProcedimentoTransplantes.getOrgao()));
		}
		
		if(mtxProcedimentoTransplantes.getIndSituacao() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_SITUACAO.toString(), mtxProcedimentoTransplantes.getIndSituacao()));
		}
		
		if(mtxProcedimentoTransplantes.getTipo() != null){
			criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_TIPO.toString(), mtxProcedimentoTransplantes.getTipo()));
		}
		
		if((mtxProcedimentoTransplantes.getPciSeq().getDescricao() != null) && (StringUtils.isNotBlank(mtxProcedimentoTransplantes.getPciSeq().getDescricao()))){
			criteria.add(Restrictions.ilike("PCI."+MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), mtxProcedimentoTransplantes.getPciSeq().getDescricao()));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<MtxProcedimentoTransplantes>  procedimentosVinculadosAoOrgao(List<MbcProcedimentoCirurgicos> procedimentos){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxProcedimentoTransplantes.class, "PTR");
		
		criteria.createAlias("PTR."+MtxProcedimentoTransplantes.Fields.PCI_SEQ.toString(),"PCI", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.in("PTR."+MtxProcedimentoTransplantes.Fields.PCI_SEQ.toString(), procedimentos));  
		criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PTR."+MtxProcedimentoTransplantes.Fields.IND_TIPO.toString(), DominioTransplante.T));
		
		return executeCriteria(criteria);
	}
	
}

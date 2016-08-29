package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatGrupoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatGrupo> {
	
	private static final long serialVersionUID = 115537911140903734L;
	
	private DetachedCriteria obterDetachedCrieriaFatGrupo(){
		return  DetachedCriteria.forClass(FatGrupo.class);
	}

	/**
	 * Metodo para montar uma criteria para pesquisar por grupos,
	 * filtrando pela descricao ou pelo seq do grupo.
	 * @param objPesquisa
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaListarGruposPorSeqOuDescricao(Object objPesquisa){
		DetachedCriteria criteria = this.obterDetachedCrieriaFatGrupo();
		String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatGrupo.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatGrupo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.START));
		}
		return criteria;
	}
	
	/**
	 * Monta criteria para pesquisa de grupos ativos.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarGruposAtivosPorSeqOuDescricao(Object objPesquisa){
		DetachedCriteria criteria = this.obterDetachedCrieriaFatGrupo();
		String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatGrupo.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatGrupo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatGrupo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	/**
	 * Metodo listar grupos,
	 * filtrando pela descricao ou pelo seq do grupo.
	 * @param objPesquisa
	 * @return
	 */
	public List<FatGrupo> listarGruposPorCodigoOuDescricao(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaListarGruposPorSeqOuDescricao(objPesquisa);
		
//		criteria.addOrder(Order.asc("GRUPO."+FatGrupo.Fields.SEQ.toString()));
//		criteria.addOrder(Order.asc("GRUPO."+FatGrupo.Fields.CODIGO.toString()));
//		criteria.addOrder(Order.asc("GRUPO."+FatGrupo.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FatGrupo.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(FatGrupo.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(FatGrupo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
		
	}

	/**
	 * Lista grupos ativos.
	 * @param objPesquisa
	 * @return
	 */
	public List<FatGrupo> listarGruposAtivosPorCodigoOuDescricao(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaListarGruposAtivosPorSeqOuDescricao(objPesquisa);
		
//		criteria.addOrder(Order.asc("GRUPO."+FatGrupo.Fields.SEQ.toString()));
//		criteria.addOrder(Order.asc("GRUPO."+FatGrupo.Fields.CODIGO.toString()));
//		criteria.addOrder(Order.asc("GRUPO."+FatGrupo.Fields.DESCRICAO.toString()));
//		criteria.addOrder(Order.asc(FatGrupo.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(FatGrupo.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc(FatGrupo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Metodo que retorna o count de grupos,
	 * filtrando pela descricao ou pelo seq do grupo.
	 * @param objPesquisa
	 * @return
	 */
	public Long listarGruposPorCodigoOuDescricaoCount(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaListarGruposPorSeqOuDescricao(objPesquisa);
		
		return executeCriteriaCount(criteria);
		
	}
	
	/** Obtem grupo por codigo
	 * 
	 * @param codigo
	 * @return
	 */
	public FatGrupo obterGruposPorCodigo(Short codigo) {
		DetachedCriteria criteria = obterDetachedCrieriaFatGrupo();
		criteria.add(Restrictions.eq(FatGrupo.Fields.CODIGO.toString(), codigo));
		return (FatGrupo) executeCriteriaUniqueResult(criteria);
	}
	
	/** Listar grupos por situacao
	 * 
	 * @param situacao
	 * @return
	 */
	public List<FatGrupo> listarGruposPorSituacao(DominioSituacao situacao) {
		DetachedCriteria criteria = obterDetachedCrieriaFatGrupo();
		criteria.add(Restrictions.eq(FatGrupo.Fields.IND_SITUACAO.toString(), situacao));
		return executeCriteria(criteria);
	}
	
}

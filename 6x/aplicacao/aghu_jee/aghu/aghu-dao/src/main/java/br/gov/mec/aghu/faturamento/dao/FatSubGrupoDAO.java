package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatSubGrupoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatSubGrupo> {
	
	private static final long serialVersionUID = 4201376693908631968L;

	private DetachedCriteria obterDetachedCrieriaFatSubGrupo(){
		return  DetachedCriteria.forClass(FatSubGrupo.class);
	}

	
	/**
	 * Metodo para montar uma criteria para pesquisar por subgrupos,
	 * filtrando pela descricao ou pelo seq do subgrupo, em um subconjunto de subgrupos pertencente a um determinado grupo.
	 * @param objPesquisa
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaListarSubGruposPorSeqOuDescricao(Object objPesquisa, Short grpSeq){
		DetachedCriteria criteria = this.obterDetachedCrieriaFatSubGrupo();
		
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.ID_GRP_SEQ.toString(), grpSeq));
		
		String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatSubGrupo.Fields.ID_SUB_GRUPO.toString(), Byte.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatSubGrupo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.START));
		}
		return criteria;
	}
	
	/**
	 * 
	 * @param objPesquisa
	 * @param grpSeq
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarSubGruposAtivosPorSeqOuDescricao(Object objPesquisa, Short grpSeq){
		DetachedCriteria criteria = this.obterDetachedCrieriaFatSubGrupo();
		
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.ID_GRP_SEQ.toString(), grpSeq));
		
		String strPesquisa = (String) objPesquisa;
		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(FatSubGrupo.Fields.ID_SUB_GRUPO.toString(), Byte.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(FatSubGrupo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		
		return criteria;
	}
	
	/**
	 * Metodo listar subgrupos,
	 * filtrando pela descricao ou pelo seq do grupo.
	 * @param objPesquisa
	 * @return
	 */
	public List<FatSubGrupo> listarSubGruposPorCodigoOuDescricao(Object objPesquisa, Short grpSeq){
		DetachedCriteria criteria = montarCriteriaListarSubGruposPorSeqOuDescricao(objPesquisa, grpSeq);
		
//		criteria.addOrder(Order.asc("SUBGRUPO."+FatSubGrupo.Fields.ID_SUB_GRUPO.toString()));
//		criteria.addOrder(Order.asc("SUBGRUPO."+FatSubGrupo.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FatSubGrupo.Fields.ID_SUB_GRUPO.toString()));
		criteria.addOrder(Order.asc(FatSubGrupo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
		
	}

	/**
	 * Lista subgrupos ativos.
	 * @param objPesquisa
	 * @param grpSeq
	 * @return
	 */
	public List<FatSubGrupo> listarSubGruposAtivosPorCodigoOuDescricao(Object objPesquisa, Short grpSeq){
		DetachedCriteria criteria = montarCriteriaListarSubGruposAtivosPorSeqOuDescricao(objPesquisa, grpSeq);
		
//		criteria.addOrder(Order.asc("SUBGRUPO."+FatSubGrupo.Fields.ID_SUB_GRUPO.toString()));
//		criteria.addOrder(Order.asc("SUBGRUPO."+FatSubGrupo.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FatSubGrupo.Fields.ID_SUB_GRUPO.toString()));
		criteria.addOrder(Order.asc(FatSubGrupo.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Metodo que retorna o count de subgrupos,
	 * filtrando pela descricao ou pelo seq do grupo.
	 * @param objPesquisa
	 * @return
	 */
	public Long listarSubGruposPorCodigoOuDescricaoCount(Object objPesquisa, Short grpSeq){
		DetachedCriteria criteria = montarCriteriaListarSubGruposPorSeqOuDescricao(objPesquisa, grpSeq);
		
		return executeCriteriaCount(criteria);
		
	}
	
	/** Obtem subGrupo por grupo e subgruop
	 * 
	 * @param grpSeq
	 * @param subGrupo
	 * @return
	 */
	public FatSubGrupo obterSubGruposPorGrupoSubGrupo(Short grpSeq, Byte subGrupo) {
		DetachedCriteria criteria = obterDetachedCrieriaFatSubGrupo();
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.ID_GRP_SEQ.toString(), grpSeq));
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.ID_SUB_GRUPO.toString(), subGrupo));
		return (FatSubGrupo) executeCriteriaUniqueResult(criteria);
	}
	
	/** Listar subGrupos por situacao
	 * 
	 * @param situacao
	 * @return
	 */
	public List<FatSubGrupo> listarSubGruposPorSituacao(DominioSituacao situacao) {
		DetachedCriteria criteria = obterDetachedCrieriaFatSubGrupo();
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.IND_SITUACAO.toString(), situacao));
		return executeCriteria(criteria);
	}	

	public Long listarSubGruposPorSituacaoCount(DominioSituacao situacao) {
		DetachedCriteria criteria = obterDetachedCrieriaFatSubGrupo();
		criteria.add(Restrictions.eq(FatSubGrupo.Fields.IND_SITUACAO.toString(), situacao));
		return executeCriteriaCount(criteria);
	}
}

package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.model.MtxTransplantes;

public class MtxDoencaBasesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxDoencaBases> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4754372233300891704L;

	/**
	 * Obtem lista de Doença Base conforme o orgão selecionado.
	 * 
	 * @param tipoOrgao Orgão selecionado
	 * @return {@link List} de {@link MtxDoencaBases}
	 */
	public List<MtxDoencaBases> obterListaDoencaBasePorTipoOrgao(DominioTipoOrgao tipoOrgao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxDoencaBases.class);
		
		criteria.add(Restrictions.eq(MtxDoencaBases.Fields.TIPO_ORGAO.toString(), tipoOrgao));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #46358
	 * C1 - Consulta para listagem de doenças base
	 * 
	 *  SELECT SEQ SEQ,
 		Tipo_Orgao ORGAO,
		Descricao DESCRICAO,
		Ind_Situacao SITUACAO
		From Mtx_Doenca_Bases
		Where Tipo_Orgao = <Orgão>
		And Descricao = <Descrição>
		and ind_situacao = <Situação>
		ORDER BY Descricao */
	
	private DetachedCriteria obterCriteriaListaDoencasBase(MtxDoencaBases mtxDoencaBases){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxDoencaBases.class);
				
		if(mtxDoencaBases.getTipoOrgao() != null){
		   criteria.add(Restrictions.eq(MtxDoencaBases.Fields.TIPO_ORGAO.toString(), mtxDoencaBases.getTipoOrgao() ));
		}
		if(StringUtils.isNotEmpty(mtxDoencaBases.getDescricao())){
			criteria.add(Restrictions.ilike(MtxDoencaBases.Fields.DESCRICAO.toString(), mtxDoencaBases.getDescricao().toString(), MatchMode.ANYWHERE));
		}
		if(mtxDoencaBases.getIndSituacao() != null){
			   criteria.add(Restrictions.eq(MtxDoencaBases.Fields.IND_SITUACAO.toString(), mtxDoencaBases.getIndSituacao()));
		}
		
		return criteria;
	}
	
	public List<MtxDoencaBases> obterListaDoencasBase(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MtxDoencaBases mtxDoencaBases) {
				
		 DetachedCriteria criteria = obterCriteriaListaDoencasBase(mtxDoencaBases);		 
		 criteria.addOrder(Order.asc(MtxDoencaBases.Fields.DESCRICAO.toString()));
		 
		 return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long obterListaDoencasBaseCount(MtxDoencaBases mtxDoencaBases) {
		return executeCriteriaCount(obterCriteriaListaDoencasBase(mtxDoencaBases));
	}
	
	public Boolean pesquisarRegistrosIguais(MtxDoencaBases mtxDoencaBases){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxDoencaBases.class);
		criteria.add(Restrictions.eq(MtxDoencaBases.Fields.TIPO_ORGAO.toString(), mtxDoencaBases.getTipoOrgao() ));
		criteria.add(Restrictions.eq(MtxDoencaBases.Fields.DESCRICAO.toString(), mtxDoencaBases.getDescricao()).ignoreCase());
		return executeCriteriaExists(criteria);
	}
	
	public Boolean atualisarRegistrosIguais(MtxDoencaBases mtxDoencaBases){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxDoencaBases.class);
		criteria.add(Restrictions.eq(MtxDoencaBases.Fields.TIPO_ORGAO.toString(), mtxDoencaBases.getTipoOrgao() ));
		criteria.add(Restrictions.eq(MtxDoencaBases.Fields.DESCRICAO.toString(), mtxDoencaBases.getDescricao()).ignoreCase());
		criteria.add(Restrictions.not(Restrictions.eq(MtxDoencaBases.Fields.SEQ.toString(),mtxDoencaBases.getSeq())));
		return executeCriteriaExists(criteria);
	}

	
	/*** #46358
	 * C2 -  select *  from mtx_transplantes where DOB_SEQ = <Doença Base> */
	
	
	public Boolean existeRegistroTransplante(MtxDoencaBases mtxDoencaBases) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class);
		criteria.add(Restrictions.eq(MtxTransplantes.Fields.DOENCA_BASE.toString() + "." + MtxDoencaBases.Fields.SEQ.toString(),
				mtxDoencaBases.getSeq()));
		
		return executeCriteriaExists(criteria);
	}
	
}
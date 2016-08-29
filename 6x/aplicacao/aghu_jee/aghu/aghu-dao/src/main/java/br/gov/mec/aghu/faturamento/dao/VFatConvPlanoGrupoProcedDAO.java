package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.VFatConvPlanoGrupoProced;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VFatConvPlanoGrupoProcedDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<VFatConvPlanoGrupoProced> {

	private static final long serialVersionUID = 4793280445803109100L;
	
	/**
	 * Metodo que monta uma criteria para pesquisar Tabelas filtrando
	 *  pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarTabelas(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatConvPlanoGrupoProced.class, "VFC");
		String strPesquisa = (String) objPesquisa;
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString())), VFatConvPlanoGrupoProcedVO.Fields.CPH_PHO_SEQ.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.PHO_DESCRICAO.toString()), VFatConvPlanoGrupoProcedVO.Fields.PHO_DESCRICAO.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.GRC_SEQ.toString()), VFatConvPlanoGrupoProcedVO.Fields.GRC_SEQ.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.GRC_DESCRICAO.toString()), VFatConvPlanoGrupoProcedVO.Fields.GRC_DESCRICAO.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString()), VFatConvPlanoGrupoProcedVO.Fields.IPH_PHO_SEQ.toString())
		);

		
		if(CoreUtil.isNumeroShort(strPesquisa)){
			criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString(), Short.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike("VFC."+VFatConvPlanoGrupoProced.Fields.PHO_DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(VFatConvPlanoGrupoProcedVO.class));
		
		return criteria;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por Tabela,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return List<VFatConvPlanoGrupoProcedVO>
	 */
	public List<VFatConvPlanoGrupoProcedVO> listarTabelas(Object objPesquisa){
		List<VFatConvPlanoGrupoProcedVO> lista = null;
		DetachedCriteria criteria = montarCriteriaListarTabelas(objPesquisa);
		
		criteria.addOrder(Order.asc("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por Tabela,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarTabelasCount(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaListarTabelas(objPesquisa);

		return executeCriteriaCountDistinct(criteria, "VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString(), true);
	}


	
	/**
	 * Metodo que monta uma criteria para pesquisar Convenios filtrando
	 *  pela descricao ou pelo codigo, conforme o grupo e a tabela já selecionados.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarConvenios(Object objPesquisa, Short grcSeq, Short cphPhoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatConvPlanoGrupoProced.class, "VFC");
		String strPesquisa = (String) objPesquisa;
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString())), VFatConvPlanoGrupoProcedVO.Fields.CPH_CSP_CNV_CODIGO.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CNV_DESCRICAO.toString()), VFatConvPlanoGrupoProcedVO.Fields.CNV_DESCRICAO.toString())
		);
		
		if(CoreUtil.isNumeroShort(strPesquisa)){
			criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString(), Short.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike("VFC."+VFatConvPlanoGrupoProced.Fields.CNV_DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.GRC_SEQ.toString(), grcSeq));
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString(), cphPhoSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatConvPlanoGrupoProcedVO.class));

		return criteria;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por Convenio,
	 * filtrando pela descricao ou pelo codigo, conforme o grupo e a tabela já selecionados.
	 * @param objPesquisa
	 * @return List<VFatConvPlanoGrupoProcedVO>
	 */
	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(Object objPesquisa, Short grcSeq, Short cphPhoSeq){
		DetachedCriteria criteria = montarCriteriaListarConvenios(objPesquisa, grcSeq, cphPhoSeq);
		
		criteria.addOrder(Order.asc("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por Convenio,
	 * filtrando pela descricao ou pelo codigo, conforme o grupo e a tabela já selecionados.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarConveniosCount(Object objPesquisa, Short grcSeq, Short cphPhoSeq){
		DetachedCriteria criteria = montarCriteriaListarConvenios(objPesquisa, grcSeq, cphPhoSeq);


		return executeCriteriaCountDistinct(criteria, "VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString(), true);
	}

	/**
	 * Metodo que monta uma criteria para pesquisar Planos filtrando
	 *  pela descricao ou pelo codigo, conforme o grupo, a tabela e o convênio já selecionados.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaListarPlanos(Object objPesquisa, Short grcSeq, Short cphPhoSeq, Short cphCspCnvCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatConvPlanoGrupoProced.class, "VFC");
		String strPesquisa = (String) objPesquisa;
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_SEQ.toString())), VFatConvPlanoGrupoProcedVO.Fields.CPH_CSP_SEQ.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CSP_DESCRICAO.toString()), VFatConvPlanoGrupoProcedVO.Fields.CSP_DESCRICAO.toString())
		);
		
		if(CoreUtil.isNumeroByte(strPesquisa)){
			criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_SEQ.toString(), Byte.valueOf(strPesquisa)));
			
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike("VFC."+VFatConvPlanoGrupoProced.Fields.CSP_DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.GRC_SEQ.toString(), grcSeq));
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString(), cphPhoSeq));
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString(), cphCspCnvCodigo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatConvPlanoGrupoProcedVO.class));

		return criteria;
	}
	
	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por Planos,
	 * filtrando pela descricao ou pelo codigo, conforme o grupo, a tabela e o convênio já selecionados.
	 * @param objPesquisa
	 * @return List<VFatConvPlanoGrupoProcedVO>
	 */
	public List<VFatConvPlanoGrupoProcedVO> listarPlanos(Object objPesquisa, Short grcSeq, Short cphPhoSeq, Short cphCspCnvCodigo){
		DetachedCriteria criteria = montarCriteriaListarPlanos(objPesquisa, grcSeq, cphPhoSeq, cphCspCnvCodigo);
		
		criteria.addOrder(Order.asc("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_SEQ.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por Planos,
	 * filtrando pela descricao ou pelo codigo, conforme o grupo, a tabela e o convênio já selecionados.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarPlanosCount(Object objPesquisa, Short grcSeq, Short cphPhoSeq, Short cphCspCnvCodigo){
		DetachedCriteria criteria = montarCriteriaListarPlanos(objPesquisa, grcSeq, cphPhoSeq, cphCspCnvCodigo);

		return executeCriteriaCountDistinct(criteria, "VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_SEQ.toString(), true);
	}

	public List<VFatConvPlanoGrupoProcedVO> listarConveniosPlanos(Short grcSeq, Short cphPhoSeq, Short cphCspCnvCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(VFatConvPlanoGrupoProced.class, "VFC");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_SEQ.toString())), VFatConvPlanoGrupoProcedVO.Fields.CPH_CSP_SEQ.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CSP_DESCRICAO.toString()), VFatConvPlanoGrupoProcedVO.Fields.CSP_DESCRICAO.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString()), VFatConvPlanoGrupoProcedVO.Fields.CPH_CSP_CNV_CODIGO.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString()), VFatConvPlanoGrupoProcedVO.Fields.IPH_PHO_SEQ.toString())
				.add(Projections.property("VFC."+VFatConvPlanoGrupoProced.Fields.CNV_DESCRICAO.toString()), VFatConvPlanoGrupoProcedVO.Fields.CNV_DESCRICAO.toString())
		);
		
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.GRC_SEQ.toString(), grcSeq));
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_PHO_SEQ.toString(), cphPhoSeq));
		criteria.add(Restrictions.eq("VFC."+VFatConvPlanoGrupoProced.Fields.CPH_CSP_CNV_CODIGO.toString(), cphCspCnvCodigo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatConvPlanoGrupoProcedVO.class));

		return executeCriteria(criteria);
	}
	
}

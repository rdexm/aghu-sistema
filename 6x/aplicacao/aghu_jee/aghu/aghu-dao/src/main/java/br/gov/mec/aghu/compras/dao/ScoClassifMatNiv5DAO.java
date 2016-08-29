package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ConsultaClassificacaoVO;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoClassifMatNiv2;
import br.gov.mec.aghu.model.ScoClassifMatNiv3;
import br.gov.mec.aghu.model.ScoClassifMatNiv4;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * 
 * @modulo compras
 *
 */
public class ScoClassifMatNiv5DAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoClassifMatNiv5>{
	
	
	private static final long serialVersionUID = 8538897399692799697L;


	/**
	 * Obtem ScoClassifMatNiv5 por número
	 * @param codigoGrupo
	 * @return
	 */
	public ScoClassifMatNiv5 obterClassifMatNiv5PorNumero(Long numero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv5.class, "CN5");
		
		criteria.createAlias("CN5." + ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString(), "CN4",  JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CN4." + ScoClassifMatNiv4.Fields.SCO_CLASSIF_MAT_NIV3.toString(), "CN3", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CN3." + ScoClassifMatNiv3.Fields.SCO_CLASSIF_MAT_NIV2.toString(), "CN2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CN2." + ScoClassifMatNiv2.Fields.SCO_CLASSIF_MAT_NIV1.toString(), "CN1", JoinType.LEFT_OUTER_JOIN);
	
		criteria.add(Restrictions.eq(ScoClassifMatNiv5.Fields.NUMERO.toString(), numero));
		
		return (ScoClassifMatNiv5) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Pesquisa uma lista de códigos/números de classificação para um grupo de material
	 * @param codigoGrupo
	 * @return
	 */
	public List<Long> pesquisarNumerosClassificacaoGrupoMaterial(Integer codigoGrupo){

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv5.class);
		criteria.setProjection(Projections.property(ScoClassifMatNiv5.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString() + "." + ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString(), codigoGrupo));
		criteria.addOrder(Order.asc(ScoClassifMatNiv5.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}

	public List<ScoClassifMatNiv5> listarClassifMatNiv5PorGrupo(Integer codGrupo, Object parametro){
		DetachedCriteria criteria = createClassifMatNiv5PorGrupoCriteria(codGrupo, parametro);
		if(codGrupo!=null){
			return executeCriteria(criteria);
		}else{
			return executeCriteria(criteria, 0, 100, null, true);
		}
	}

	public Long listarClassifMatNiv5PorGrupoCount(Integer codGrupo, Object parametro){
		DetachedCriteria criteria = createClassifMatNiv5PorGrupoCriteria(codGrupo, parametro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createClassifMatNiv5PorGrupoCriteria(Integer codGrupo, Object parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv5.class, "CN5");
		criteria.createAlias("CN5." + ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString(), "CN4");
		criteria.createAlias("CN4." + ScoClassifMatNiv4.Fields.SCO_CLASSIF_MAT_NIV3.toString(), "CN3");
		criteria.createAlias("CN3." + ScoClassifMatNiv3.Fields.SCO_CLASSIF_MAT_NIV2.toString(), "CN2");
		criteria.createAlias("CN2." + ScoClassifMatNiv2.Fields.SCO_CLASSIF_MAT_NIV1.toString(), "CN1");
		criteria.createAlias("CN1." + ScoClassifMatNiv1.Fields.GMT.toString(), "GMT");
		if(codGrupo!=null){
			criteria.add(Restrictions.eq("GMT."+ScoGrupoMaterial.Fields.CODIGO.toString(), codGrupo));
		}
		
		String descricao = (String) parametro;
		if (CoreUtil.isNumeroInteger(descricao)){
			descricao = null;
		}	

		if (descricao != null) {
			criteria.add(Restrictions.or(Restrictions.ilike("CN5." + ScoClassifMatNiv5.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE), 
						 Restrictions.or(Restrictions.ilike("CN4." + ScoClassifMatNiv4.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE), 
						 Restrictions.or(Restrictions.ilike("CN3." + ScoClassifMatNiv3.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE),
						 Restrictions.or(Restrictions.ilike("CN2." + ScoClassifMatNiv2.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE), 
						 Restrictions.ilike("CN1." + ScoClassifMatNiv1.Fields.DESCRICAO.toString(),descricao, MatchMode.ANYWHERE))))));
		}
		return criteria;
	}
	

	/**
	 * C10 de #5758
	 * 
	 * Obter máximo código da classificação nível 5
	 */
	@Override
	protected void obterValorSequencialId(ScoClassifMatNiv5 elemento) {
		if (elemento == null || elemento.getScoClassifMatNiv4() == null) {
			throw new IllegalStateException("Elemento não pode ser null");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv5.class);
		criteria.add(Restrictions.eq(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString(), elemento.getScoClassifMatNiv4()));
		criteria.setProjection(Projections.max(ScoClassifMatNiv5.Fields.CODIGO.toString()));
		Integer codigo = (Integer) super.executeCriteriaUniqueResult(criteria);
		if (codigo == null) {
			codigo = 0;
		} else {
			codigo++;
		}
		elemento.setCodigo(codigo);

		StringBuffer numero = new StringBuffer();
		numero.append(StringUtil.adicionaZerosAEsquerda(elemento.getScoClassifMatNiv4().getId().getCn3Cn2Cn1GmtCodigo(), 2));
		numero.append(StringUtil.adicionaZerosAEsquerda(elemento.getScoClassifMatNiv4().getId().getCn3Cn2Cn1Codigo(), 2));
		numero.append(StringUtil.adicionaZerosAEsquerda(elemento.getScoClassifMatNiv4().getId().getCn3Cn2Codigo(), 2));
		numero.append(StringUtil.adicionaZerosAEsquerda(elemento.getScoClassifMatNiv4().getId().getCn3Codigo(), 2));
		numero.append(StringUtil.adicionaZerosAEsquerda(elemento.getScoClassifMatNiv4().getId().getCodigo(), 2));
		numero.append(StringUtil.adicionaZerosAEsquerda(elemento.getCodigo(), 2));

		elemento.setNumero(Long.valueOf(numero.toString()));
	}
	
	public List<ConsultaClassificacaoVO> pesquisarClassificacoes(Object param){
				
		String consulta = this.montarConsultaSuggestionClassificacoes(param, false);		
		org.hibernate.Query query = this.createHibernateQuery(consulta);
			
		if (param != null) {
			String strPesquisa = (String) param;
			if (StringUtils.isNotBlank(strPesquisa)) {
				if (CoreUtil.isNumeroLong(param)) {
					query.setParameter("codigo", Long.valueOf(strPesquisa));	
				} 
			}
		}
				
		query.setFirstResult(0);
		query.setMaxResults(100);		
		query.setResultTransformer(Transformers.aliasToBean(ConsultaClassificacaoVO.class));
			
		return query.list();
	}
			
	public Long pesquisarClassificacoesCount(Object param){
		
		String consulta = this.montarConsultaSuggestionClassificacoes(param, true);
		org.hibernate.Query query = this.createHibernateQuery(consulta);
			
		if (param != null) {
			String strPesquisa = (String) param;
			if (StringUtils.isNotBlank(strPesquisa)) {
				if (CoreUtil.isNumeroLong(param)) {
					query.setParameter("codigo", Long.valueOf(strPesquisa));	
				}
			}
		}
		
		Number resultado = (Number) query.uniqueResult();
		return resultado != null ? resultado.longValue() : 0L;		
	}
	
	private String montarConsultaSuggestionClassificacoes(Object param, boolean count){
				
		StringBuilder hql = new StringBuilder(400);
			
		if(count){
			hql.append("select count(*)");
		} else {		
			hql.append("select CN5.").append(ScoClassifMatNiv5.Fields.NUMERO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CODIGO.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO5.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO4.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO3.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO2.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO1.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO5.toString()).append(", ");
			hql.append(" CN4.").append(ScoClassifMatNiv4.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO4.toString()).append(", ");
			hql.append(" CN3.").append(ScoClassifMatNiv3.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO3.toString()).append(", ");
			hql.append(" CN2.").append(ScoClassifMatNiv2.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO2.toString()).append(", ");
			hql.append(" CN1.").append(ScoClassifMatNiv1.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO1.toString()).append(", ");
			hql.append(" GMT.").append(ScoGrupoMaterial.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO0.toString()).append(' ');
		}
				
		hql.append(" 			from ").append(ScoGrupoMaterial.class.getSimpleName()).append(" GMT, ");
		hql.append(ScoClassifMatNiv1.class.getSimpleName()).append(" CN1, ");
		hql.append(ScoClassifMatNiv2.class.getSimpleName()).append(" CN2, ");
		hql.append(ScoClassifMatNiv3.class.getSimpleName()).append(" CN3, ");
		hql.append(ScoClassifMatNiv4.class.getSimpleName()).append(" CN4, ");
		hql.append(ScoClassifMatNiv5.class.getSimpleName()).append(" CN5 ");
		
		hql.append("where CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CODIGO.toString());
		
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_GMT_CODIGO.toString());
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_CODIGO.toString());
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CN2_CODIGO.toString());
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CODIGO.toString());
		
		hql.append(" and CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_GMT_CODIGO.toString()).append(" = ").append("CN2.").append(ScoClassifMatNiv2.Fields.CN1_GMT_CODIGO.toString());
		hql.append(" and CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_CODIGO.toString()).append(" = ").append("CN2.").append(ScoClassifMatNiv2.Fields.CN1_CODIGO.toString());
		hql.append(" and CN3.").append(ScoClassifMatNiv3.Fields.CN2_CODIGO.toString()).append(" = ").append("CN2.").append(ScoClassifMatNiv2.Fields.CODIGO.toString());
		
		hql.append(" and CN2.").append(ScoClassifMatNiv2.Fields.CN1_GMT_CODIGO.toString()).append(" = ").append("CN1.").append(ScoClassifMatNiv1.Fields.GMT.toString()).append('.').append(ScoGrupoMaterial.Fields.CODIGO.toString());
		hql.append(" and CN2.").append(ScoClassifMatNiv2.Fields.CN1_CODIGO.toString()).append(" = ").append("CN1.").append(ScoClassifMatNiv1.Fields.CODIGO.toString());
		
		hql.append(" and CN1.").append(ScoClassifMatNiv1.Fields.GMT.toString()).append('.').append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" = ").append("GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString());
		
		if (param != null) {
			String strPesquisa = (String) param;
			if (StringUtils.isNotBlank(strPesquisa)) {
				if (CoreUtil.isNumeroLong(param)) {
					hql.append(" and CN5."+ScoClassifMatNiv5.Fields.NUMERO.toString()+" = :codigo");	
				} else {
					hql.append(" and (CN5.").append(ScoClassifMatNiv5.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%'");
					hql.append(" or CN4.").append(ScoClassifMatNiv4.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%'");
					hql.append(" or CN3.").append(ScoClassifMatNiv3.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%'");
					hql.append(" or CN2.").append(ScoClassifMatNiv2.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%'");
					hql.append(" or CN1.").append(ScoClassifMatNiv1.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%'");
					hql.append(" or GMT.").append(ScoGrupoMaterial.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%')");					
				}
			}
		}
			
		return hql.toString();
	}

	public List<ConsultaClassificacaoVO> listarClassificacoes(Integer numero, Short rcmCodigo) {
			
		String consulta = this.montarConsultaListagemClassificacoes(false);
		org.hibernate.Query query = this.createHibernateQuery(consulta);
		
		query.setParameter("numero", numero);	
		query.setParameter("rcmCodigo", rcmCodigo);
			
		query.setResultTransformer(Transformers.aliasToBean(ConsultaClassificacaoVO.class));
		
		return query.list();		
	}
		
	public Long listarClassificacoesCount(Integer numero, Short rcmCodigo) {
		
		String consulta = this.montarConsultaListagemClassificacoes(true);
		org.hibernate.Query query = this.createHibernateQuery(consulta);
		
		query.setParameter("numero", numero);	
		query.setParameter("rcmCodigo", rcmCodigo);
		
		Number resultado = (Number) query.uniqueResult();
		return resultado != null ? resultado.longValue() : 0L;		
	}
		
	private String montarConsultaListagemClassificacoes(boolean count){
			
		StringBuilder hql = new StringBuilder(400);
		
		if(count){
			hql.append("select count(*)");
		} else {
			hql.append("select FRC.").append(ScoFnRamoComerClas.Fields.ID_CN5NUMERO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CODIGO.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO5.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO4.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO3.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO2.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CLASSIFICACAO1.toString()).append(", ");
			hql.append(" CN5.").append(ScoClassifMatNiv5.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO5.toString()).append(", ");
			hql.append(" CN4.").append(ScoClassifMatNiv4.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO4.toString()).append(", ");
			hql.append(" CN3.").append(ScoClassifMatNiv3.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO3.toString()).append(", ");
			hql.append(" CN2.").append(ScoClassifMatNiv2.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO2.toString()).append(", ");
			hql.append(" CN1.").append(ScoClassifMatNiv1.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO1.toString()).append(", ");
			hql.append(" GMT.").append(ScoGrupoMaterial.Fields.DESCRICAO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.DESCRICAO0.toString()).append(", ");
			hql.append(" GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" as ").append(ConsultaClassificacaoVO.Fields.CODIGO_GRUPO_MATERIAL.toString()).append(' ');
		}
		
		hql.append(" 			from ").append(ScoFnRamoComerClas.class.getSimpleName()).append(" FRC, ");
		hql.append(ScoGrupoMaterial.class.getSimpleName()).append(" GMT, ");
		hql.append(ScoClassifMatNiv1.class.getSimpleName()).append(" CN1, ");
		hql.append(ScoClassifMatNiv2.class.getSimpleName()).append(" CN2, ");
		hql.append(ScoClassifMatNiv3.class.getSimpleName()).append(" CN3, ");
		hql.append(ScoClassifMatNiv4.class.getSimpleName()).append(" CN4, ");
		hql.append(ScoClassifMatNiv5.class.getSimpleName()).append(" CN5 ");
		
		hql.append("where CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString());
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.SCO_CLASSIF_MAT_NIV4.toString()).append('.').append(ScoClassifMatNiv4.Fields.CODIGO.toString()).append(" = ").append("CN4.").append(ScoClassifMatNiv4.Fields.CODIGO.toString());
		
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_GMT_CODIGO.toString());
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_CODIGO.toString());
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CN2_CODIGO.toString());
		hql.append(" and CN4.").append(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString()).append(" = ").append("CN3.").append(ScoClassifMatNiv3.Fields.CODIGO.toString());
		
		hql.append(" and CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_GMT_CODIGO.toString()).append(" = ").append("CN2.").append(ScoClassifMatNiv2.Fields.CN1_GMT_CODIGO.toString());
		hql.append(" and CN3.").append(ScoClassifMatNiv3.Fields.CN2_CN1_CODIGO.toString()).append(" = ").append("CN2.").append(ScoClassifMatNiv2.Fields.CN1_CODIGO.toString());
		hql.append(" and CN3.").append(ScoClassifMatNiv3.Fields.CN2_CODIGO.toString()).append(" = ").append("CN2.").append(ScoClassifMatNiv2.Fields.CODIGO.toString());
		
		hql.append(" and CN2.").append(ScoClassifMatNiv2.Fields.CN1_GMT_CODIGO.toString()).append(" = ").append("CN1.").append(ScoClassifMatNiv1.Fields.GMT.toString()).append('.').append(ScoGrupoMaterial.Fields.CODIGO.toString());
		hql.append(" and CN2.").append(ScoClassifMatNiv2.Fields.CN1_CODIGO.toString()).append(" = ").append("CN1.").append(ScoClassifMatNiv1.Fields.CODIGO.toString());
		
		hql.append(" and CN1.").append(ScoClassifMatNiv1.Fields.GMT.toString()).append('.').append(ScoGrupoMaterial.Fields.CODIGO.toString()).append(" = ").append("GMT.").append(ScoGrupoMaterial.Fields.CODIGO.toString());
		
		hql.append(" and CN5.").append(ScoClassifMatNiv5.Fields.NUMERO.toString()).append(" = ").append("FRC.").append(ScoFnRamoComerClas.Fields.ID_CN5NUMERO.toString());
		
		//parametros
		hql.append(" and FRC.").append(ScoFnRamoComerClas.Fields.ID_FRMFRNNUMERO.toString()).append(" = :numero");
		hql.append(" and FRC.").append(ScoFnRamoComerClas.Fields.ID_FRMRCMCODIGO.toString()).append(" = :rcmCodigo");
				
		return hql.toString();
	}
	
}

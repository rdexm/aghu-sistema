package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ClassificacaoVO;
import br.gov.mec.aghu.compras.vo.RamoComercialVO;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoFornRamoComercialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFornRamoComercial> {

	private static final long serialVersionUID = -2074233284525025837L;

	public List<ScoFornRamoComercial> pesquisarScoRamosComerciaisPorFornecedor(VScoFornecedor fornecedor, int firstResult, int maxResults, 
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaPesquisaRamosComerciais(fornecedor);
		
		
		return executeCriteria(criteria, firstResult,maxResults, orderProperty, asc);
	}

	private DetachedCriteria montarCriteriaPesquisaRamosComerciais(
			VScoFornecedor fornecedor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornRamoComercial.class,"FRC");
		criteria.createAlias("FRC."+ScoFornRamoComercial.Fields.SCO_RAMOS_COMERCIAIS.toString(), "SRC");
				
		criteria.add(Restrictions.eq("FRC."+ScoFornRamoComercial.Fields.SCO_FORNECEDOR_NUMERO.toString(),fornecedor.getNumeroFornecedor()));
		return criteria;
	}
	
	public Long pesquisarScoRamosComerciaisPorFornecedorCount(VScoFornecedor fornecedor) {
		return executeCriteriaCount(montarCriteriaPesquisaRamosComerciais(fornecedor));
	}
	
	public ScoFornRamoComercial pesquisarScoFornRamoComerciailPorForneCodigo(ScoFornRamoComercial fornRamoComercial){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornRamoComercial.class,"FRC");
		montarConsultaPadrao(fornRamoComercial.getId().getFrnNumero(), fornRamoComercial.getId().getRcmCodigo(), criteria);
		return (ScoFornRamoComercial) executeCriteriaUniqueResult(criteria);
	}
	
	
	public ScoFornRamoComercial pesquisarScoFornRamoComerciailPorForneCodigo(Integer numero, Short rcmCod ){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornRamoComercial.class,"FRC");
		montarConsultaPadrao(numero, rcmCod, criteria);
		return (ScoFornRamoComercial) executeCriteriaUniqueResult(criteria);
	}

	private void montarConsultaPadrao(Integer numero, Short rcmCod,
			DetachedCriteria criteria) {
		criteria.createAlias("FRC."+ScoFornRamoComercial.Fields.SCO_RAMOS_COMERCIAIS.toString(), "SRC");
		criteria.add(Restrictions.eq("FRC."+ScoFornRamoComercial.Fields.SCO_FORNECEDOR_NUMERO.toString(),numero));
		criteria.add(Restrictions.eq("SRC."+ScoRamoComercial.Fields.CODIGO.toString(),rcmCod));
	}
	
	public List<ScoFornRamoComercial> obterRamosComerciais(Object param, Integer numeroFornecedor){
		
		String consulta = this.montarConsultaSuggestionRamosComerciais(param, numeroFornecedor, false);		
		org.hibernate.Query query = createHibernateQuery(consulta);
		
		query.setParameter("numeroFornecedor", numeroFornecedor);
		
		if (param != null) {
			String strPesquisa = (String) param;
			if (StringUtils.isNotBlank(strPesquisa)) {
				if (CoreUtil.isNumeroShort(param)) {
					query.setParameter("codigo", Short.valueOf(strPesquisa));	
				} 
			}
		}
		
		query.setFirstResult(0);
		query.setMaxResults(100);		
		query.setResultTransformer(Transformers.aliasToBean(RamoComercialVO.class));
		
		return query.list();
	}
	
	public Long obterRamosComerciaisCount(Object param, Integer numeroFornecedor) {
		
		String consulta = this.montarConsultaSuggestionRamosComerciais(param, numeroFornecedor, true);
		org.hibernate.Query query = createHibernateQuery(consulta);
		
		query.setParameter("numeroFornecedor", numeroFornecedor);
		
		if (param != null) {
			String strPesquisa = (String) param;
			if (StringUtils.isNotBlank(strPesquisa)) {
				if (CoreUtil.isNumeroShort(param)) {
					query.setParameter("codigo", Short.valueOf(strPesquisa));	
				} 
			}
		}
	
		Number resultado = (Number) query.uniqueResult();
		return resultado != null ? resultado.longValue() : 0L;	
	}
	
	private String montarConsultaSuggestionRamosComerciais(Object param, Integer numeroFornecedor, boolean count){
		
		StringBuilder hql = new StringBuilder(100);
		
		if(count){
			hql.append("select count(*)");
		} else {
			hql.append("select FRC.").append(ScoFornRamoComercial.Fields.ID_RCMCODIGO.toString()).append(" as ").append(ClassificacaoVO.Fields.CODIGO.toString()).append(", ");
			hql.append(" RCM.").append(ScoRamoComercial.Fields.DESCRICAO.toString()).append(" as ").append(ClassificacaoVO.Fields.DESCRICAO.toString());
		}
		
		hql.append(" 			from ").append(ScoFornRamoComercial.class.getSimpleName()).append(" FRC, ");
		hql.append(ScoRamoComercial.class.getSimpleName()).append(" RCM ");
		
		hql.append("where RCM.").append(ScoRamoComercial.Fields.CODIGO.toString()).append(" = ").append("FRC.").append(ScoFornRamoComercial.Fields.ID_RCMCODIGO.toString());
		//parametros
		hql.append(" and FRC.").append(ScoFornRamoComercial.Fields.ID_FRNNUMERO.toString()).append(" = :numeroFornecedor");	
		
		
		if (param != null) {
			String strPesquisa = (String) param;
			if (StringUtils.isNotBlank(strPesquisa)) {
				if (CoreUtil.isNumeroShort(param)) {
					hql.append(" and FRC.").append(ScoFornRamoComercial.Fields.ID_RCMCODIGO.toString()).append(" = :codigo");	
				} else {
					hql.append(" and RCM.").append(ScoRamoComercial.Fields.DESCRICAO.toString()).append(" LIKE '%").append(strPesquisa.toUpperCase()).append("%'");
				}
			}
		}
		
		if(!count){
			hql.append(" order by RCM.").append(ScoRamoComercial.Fields.DESCRICAO.toString());
		}
		return hql.toString();
	}
	
}

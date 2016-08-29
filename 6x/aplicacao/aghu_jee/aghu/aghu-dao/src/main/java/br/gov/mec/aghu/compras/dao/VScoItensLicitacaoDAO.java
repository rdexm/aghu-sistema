package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoItensLicitacao;

public class VScoItensLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VScoItensLicitacao>{
	
	
	private static final long serialVersionUID = 1127755406540935071L;

	/** 
	 * #5521
	 * @author felipecruz
	 * @param  Short item,
	 * @param  Integer licitacao, 
	 * @return List<Object[]>
	 */	
	public List<Object[]> obterItensLicitacaoVil1(Short item, Integer licitacao){
		StringBuffer hql = new StringBuffer(230);
		hql.append("SELECT VIL1.").append(VScoItensLicitacao.Fields.SOLICIT.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.CODIGO.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.QTDE_APROVADA.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.UNID_MEDIDA.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.NOME.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.DESCR_COD.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.DESCR_SOLIC.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.LICITACAO.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.ITEM.toString());
		hql.append("	,MAT.").append(ScoMaterial.Fields.IND_MENOR_PRECO.toString());
		hql.append("	FROM ").append(VScoItensLicitacao.class.getSimpleName()).append(" VIL1,");
		hql.append('	').append(ScoMaterial.class.getSimpleName()).append(" MAT,");
		hql.append('	').append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC");
		hql.append("	WHERE VIL1.").append(VScoItensLicitacao.Fields.IND_EXCLUSAO_FASE_LIC.toString()).append(" = 'N'");
		hql.append("	AND VIL1.").append(VScoItensLicitacao.Fields.LICITACAO.toString()).append(" = :licitacao");
		hql.append("	AND VIL1.").append(VScoItensLicitacao.Fields.ITEM.toString()).append(" = :item");
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = 'N'");
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = VIL1.").append(VScoItensLicitacao.Fields.SOLICIT.toString());
		hql.append("	AND MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = VIL1.").append(VScoItensLicitacao.Fields.CODIGO.toString());
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(" IS NOT NULL");
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("item", item);
		query.setParameter("licitacao", licitacao);
		
		
		return query.list();  	
		 
	}

	/**
	 * @param Integer número da licitação
	 * A leitura dos itens da licitação deve ser baseado na  QUERY V_SCO_ITENS_LICITACAO
	 * @return List<Object[]>
	 */	
	private List<Object[]> obterDetalhesItensLicitacaoMat(Integer licitacao){
		StringBuffer hql = new StringBuffer(230);
		hql.append("SELECT VIL1.").append(VScoItensLicitacao.Fields.LICITACAO.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.ITEM.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.DESC_TIPO.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.UNID_MEDIDA.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.QTDE_APROVADA.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.VALOR_UNITARIO.toString());
		hql.append("	FROM ").append(VScoItensLicitacao.class.getSimpleName()).append(" VIL1,");
		hql.append('	').append(ScoMaterial.class.getSimpleName()).append(" MAT,");
		hql.append('	').append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC");
		hql.append("	WHERE VIL1.").append(VScoItensLicitacao.Fields.IND_EXCLUSAO_FASE_LIC.toString()).append(" = 'N'");
		hql.append("	AND VIL1.").append(VScoItensLicitacao.Fields.LICITACAO.toString()).append(" = :licitacao");
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = 'N'");
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = VIL1.").append(VScoItensLicitacao.Fields.SOLICIT.toString());
		hql.append("	AND MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = VIL1.").append(VScoItensLicitacao.Fields.CODIGO.toString());
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(" IS NOT NULL");
		hql.append(" ORDER BY " + "VIL1." + VScoItensLicitacao.Fields.ITEM.toString() + " asc");
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("licitacao", licitacao);
		
		return query.list();  	
	}


	/**
	 * @param Integer número da licitação
	 * A leitura dos itens da licitação deve ser baseado na  QUERY V_SCO_ITENS_LICITACAO
	 * @return List<Object[]>
	 */	
	private List<Object[]> obterDetalhesItensLicitacaoServ(Integer licitacao){
		StringBuffer hql = new StringBuffer(230);
		hql.append("SELECT VIL1.").append(VScoItensLicitacao.Fields.LICITACAO.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.ITEM.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.DESC_TIPO.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.UNID_MEDIDA.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.QTDE_APROVADA.toString());
		hql.append("	,VIL1.").append(VScoItensLicitacao.Fields.VALOR_UNITARIO.toString());
		hql.append("	FROM ").append(VScoItensLicitacao.class.getSimpleName()).append(" VIL1,");
		hql.append('	').append(ScoServico.class.getSimpleName()).append(" SRV,");
		hql.append('	').append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC");
		hql.append("	WHERE VIL1.").append(VScoItensLicitacao.Fields.IND_EXCLUSAO_FASE_LIC.toString()).append(" = 'N'");
		hql.append("	AND VIL1.").append(VScoItensLicitacao.Fields.LICITACAO.toString()).append(" = :licitacao");
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = 'N'");
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = VIL1.").append(VScoItensLicitacao.Fields.SOLICIT.toString());
		hql.append("	AND SRV.").append(ScoServico.Fields.CODIGO.toString()).append(" = VIL1.").append(VScoItensLicitacao.Fields.CODIGO.toString());
		hql.append("	AND FSC.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(" IS NOT NULL");
		hql.append(" ORDER BY " + "VIL1." + VScoItensLicitacao.Fields.ITEM.toString() + " asc");
		
		Query query = createHibernateQuery(hql.toString());
		
		query.setParameter("licitacao", licitacao);
		
		return query.list();  	
	}
	
	/**
	 * @param Integer número da licitação
	 * A leitura dos itens da licitação deve ser baseado na  QUERY V_SCO_ITENS_LICITACAO
	 * @return List<Object[]>
	 */	
	public List<Object[]> obterDetalhesItensLicitacao(Integer licitacao){
		List<Object[]> listaItensMat = obterDetalhesItensLicitacaoMat(licitacao);
		if(listaItensMat.isEmpty()){
			List<Object[]> listaItensServ = obterDetalhesItensLicitacaoServ(licitacao);
			return listaItensServ;
		}else{
			return listaItensMat;
		}
	}
}

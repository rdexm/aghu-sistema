package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.PareceresAvaliacaoVO;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;

public class ScoParecerAvaliacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParecerAvaliacao> {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6130654931838521211L;
	

	public ScoParecerAvaliacao obterUltimaAvaliacaoParecer(ScoParecerMaterial scoParecerMaterial){	
		if (scoParecerMaterial != null) {
				
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "SCOPAV");
			DetachedCriteria subQueryAvaliacoes = DetachedCriteria.forClass(ScoParecerAvaliacao.class,"PARECER_AVAL");		
			subQueryAvaliacoes.add(Restrictions.eqProperty("SCOPAV."+ ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), "PARECER_AVAL."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString())); 
			subQueryAvaliacoes.setProjection(Projections.max("PARECER_AVAL."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
			criteria.add(Subqueries.propertyIn("SCOPAV."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryAvaliacoes));
			criteria.add(Restrictions.eq("SCOPAV." + ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), scoParecerMaterial));
			return (ScoParecerAvaliacao) this.executeCriteriaUniqueResult(criteria);		
		}
		return null;		
	}
	

	public String obterParecerAvaliacaoMarcaModeloItemProposta(ScoItemPropostaFornecedor itemProposta, ScoMaterial material, Boolean testaMarca) {
		String ret = "";
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParecerAvaliacao.class, "PAV");
					
		criteria.createAlias(ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMT");
				
		criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MATERIAL.toString(), material));
		if (!testaMarca) {
			criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MARCA_MODELO.toString(), itemProposta.getModeloComercial()));
		} else {
			criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), itemProposta.getMarcaComercial()));
			criteria.add(Restrictions.isNull("PMT."+ScoParecerMaterial.Fields.MARCA_MODELO.toString()));			
		}
		
		criteria.setProjection(Projections.property("PAV." + ScoParecerAvaliacao.Fields.PARECER_GERAL.toString()));
		
		DetachedCriteria subQueryData = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "PAV1");
		subQueryData.setProjection(Projections.max("PAV1."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
		subQueryData.add(Restrictions.eqProperty("PAV1."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), 
												 "PMT."+ScoParecerMaterial.Fields.CODIGO.toString()));		
		criteria.add(Subqueries.propertyEq("PAV."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryData));		
		
		List<DominioParecer> listaParecerAvaliacao = executeCriteria(criteria);
		
		if (listaParecerAvaliacao  != null && !listaParecerAvaliacao .isEmpty()) {
			ret = listaParecerAvaliacao .get(0).getDescricao();
		}
		
		return ret;
	}
	
	
	public List<ScoParecerAvaliacao> pesquisarParecerAvaliacaoItemProposta(ScoItemPropostaFornecedor itemProposta, ScoMaterial material, boolean testaMarcaModelo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "PAV");
        
        criteria.createAlias(ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMT");
        criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MATERIAL.toString(), material));   
        
		if (testaMarcaModelo) {
			criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MARCA_MODELO.toString(), itemProposta.getModeloComercial()));
		} else {
			criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), itemProposta.getMarcaComercial()));
			criteria.add(Restrictions.isNull("PMT."+ScoParecerMaterial.Fields.MARCA_MODELO.toString()));			
		}
                        
        DetachedCriteria subQueryItemProposta = DetachedCriteria
                        .forClass(ScoItemPropostaFornecedor.class, "IPF");
        
        subQueryItemProposta.add(Restrictions.eq("IPF."+ScoItemPropostaFornecedor.Fields.ID.toString(), itemProposta.getId()));
        subQueryItemProposta.setProjection(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString()));
        
        criteria.add(Subqueries.propertyIn("PMT."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), subQueryItemProposta));
        
		DetachedCriteria subQueryData = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "PAV1");
		subQueryData.setProjection(Projections.max("PAV1."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
		subQueryData.add(Restrictions.eqProperty("PAV1."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), 
												 "PMT."+ScoParecerMaterial.Fields.CODIGO.toString()));		
		criteria.add(Subqueries.propertyEq("PAV."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryData));		
        
        return executeCriteria(criteria);
   }
	
     public List<PareceresAvaliacaoVO> listaAvaliacaoParecer(ScoParecerMaterial scoParecerMaterial){	
		if (scoParecerMaterial != null) {
				
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "SCOPAV");	
			criteria.createAlias("SCOPAV." + ScoParecerAvaliacao.Fields.AVALIACOES_TECNICAS.toString(), "SCOPAVTEC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SCOPAV." + ScoParecerAvaliacao.Fields.AVALIACOES_CONSUL.toString(), "SCOPAVCONSUL", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SCOPAV." + ScoParecerAvaliacao.Fields.AVALIACOES_DESEMP.toString(), "SCOPAVDESEMP", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.eq("SCOPAV." + ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), scoParecerMaterial));			
			criteria.addOrder(Order.desc("SCOPAV." + ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
			
			
			ProjectionList projection = Projections.projectionList()
					.add(Projections.property("SCOPAV."+ ScoParecerAvaliacao.Fields.CODIGO.toString()), PareceresAvaliacaoVO.Fields.CODIGO_PARECER_AVALIACAO.toString())
					.add(Projections.property("SCOPAV."+ ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString()), PareceresAvaliacaoVO.Fields.CODIGO_PARECER_MATERIAL.toString())
					.add(Projections.property("SCOPAV."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()), PareceresAvaliacaoVO.Fields.DT_CRIACAO_AVAL.toString())
					.add(Projections.property("SCOPAV."+ ScoParecerAvaliacao.Fields.LOTE.toString()), PareceresAvaliacaoVO.Fields.LOTE_AVAL.toString())
					.add(Projections.property("SCOPAV."+ ScoParecerAvaliacao.Fields.PARECER_GERAL.toString()), PareceresAvaliacaoVO.Fields.PARECER_AVAL.toString())
					.add(Projections.property("SCOPAVTEC."+ ScoParecerAvalTecnica.Fields.PARECER.toString()), PareceresAvaliacaoVO.Fields.PARECER_AVAL_TEC.toString())
					.add(Projections.property("SCOPAVCONSUL."+ ScoParecerAvalConsul.Fields.PARECER.toString()), PareceresAvaliacaoVO.Fields.PARECER_AVAL_CONSUL.toString())
					.add(Projections.property("SCOPAVDESEMP."+ ScoParecerAvalDesemp.Fields.PARECER.toString()), PareceresAvaliacaoVO.Fields.PARECER_AVAL_DESEMP.toString());		 
									

			criteria.setProjection(projection);			
			
			criteria.setResultTransformer(Transformers.aliasToBean(PareceresAvaliacaoVO.class));
			return executeCriteria(criteria);
			
			
			
				
		}
		return null;		
	}
	     
     public String obterParecerTecnicoItem(Integer codigo, DominioSituacao situacao){
     	String ret = "";
     	
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "SCOPAV");
		criteria.createAlias("SCOPAV."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMT");
		
		DetachedCriteria subQueryAvaliacoes = DetachedCriteria.forClass(ScoParecerAvaliacao.class,"PARECER_AVAL");
		subQueryAvaliacoes.createAlias("PARECER_AVAL."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMT1");
		subQueryAvaliacoes.add(Restrictions.eq("PMT1." + ScoParecerMaterial.Fields.MATERIAL_CODIGO.toString(), codigo));
		if (situacao != null){
			subQueryAvaliacoes.add(Restrictions.eq("PMT1." + ScoParecerMaterial.Fields.SITUACAO.toString(), situacao));
 		}    	
     
		//subQueryAvaliacoes.add(Restrictions.eqProperty("SCOPAV."+ ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), "PARECER_AVAL."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString())); 
		subQueryAvaliacoes.setProjection(Projections.max("PARECER_AVAL."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
		
		/*criteria.createAlias("SCOPAV."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMT");
		DetachedCriteria subQueryAvaliacoes = DetachedCriteria.forClass(ScoParecerAvaliacao.class,"PARECER_AVAL");		
		subQueryAvaliacoes.add(Restrictions.eqProperty("SCOPAV."+ ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), "PARECER_AVAL."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString())); 
		subQueryAvaliacoes.setProjection(Projections.max("PARECER_AVAL."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));*/
		criteria.add(Subqueries.propertyIn("SCOPAV."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryAvaliacoes));
		criteria.add(Restrictions.eq("PMT." + ScoParecerMaterial.Fields.MATERIAL_CODIGO.toString(), codigo));
		
		if (situacao != null){
			criteria.add(Restrictions.eq("PMT." + ScoParecerMaterial.Fields.SITUACAO.toString(), situacao));
 		} 
     
 		
 		List<ScoParecerAvaliacao> listaParecerAvaliacao = executeCriteria(criteria,true);
 		if (listaParecerAvaliacao  != null && !listaParecerAvaliacao .isEmpty()) {
 			ret = listaParecerAvaliacao.get(0).getParecerGeral().toString();
 		}
 		return ret;	
  	 }
     
     public String obterParecerTecnicoItem(Integer codigo){
    	String ret = "";
      	DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "PAV");
      	criteria.add(Restrictions.eq("PAV."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), codigo));  
      	
      	criteria.setProjection(Projections.property("PAV." + ScoParecerAvaliacao.Fields.PARECER_GERAL.toString()));
  		DetachedCriteria subQueryData = DetachedCriteria.forClass(ScoParecerAvaliacao.class, "PAV1");
  		subQueryData.createAlias("PAV1."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL.toString(), "PMT");
  		subQueryData.setProjection(Projections.max("PAV1."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
  		subQueryData.add(Restrictions.eqProperty("PAV1."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), 
  												 "PAV."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString()));		
  		criteria.add(Subqueries.propertyEq("PAV."+ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryData));	
  		
  		List<DominioParecer> listaParecerAvaliacao = executeCriteria(criteria);
  		if (listaParecerAvaliacao  != null && !listaParecerAvaliacao .isEmpty()) {
  			ret = listaParecerAvaliacao .get(0).toString();
  		}
  		return ret;	
   	 }
     
}

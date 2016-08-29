package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioParecerOcorrencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;

public class ScoParecerOcorrenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParecerOcorrencia> {		
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8587973875226107044L;
	
	public ScoParecerOcorrencia obterUltimaOcorrenciaParecer(ScoParecerMaterial scoParecerMaterial){		
		if (scoParecerMaterial != null) {
				
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerOcorrencia.class, "SCOPOCO");
			
            //DetachedCriteria subQueryOcorrencias = DetachedCriteria.forClass(ScoParecerOcorrencia.class,"PARECER_OCOR");			
			//subQueryOcorrencias.add(Restrictions.eqProperty("SCOPOCO."+ ScoParecerOcorrencia.Fields.PARECER_MATERIAL_CODIGO.toString(), "PARECER_OCOR."+ScoParecerOcorrencia.Fields.PARECER_MATERIAL_CODIGO.toString()));
			//subQueryOcorrencias.setProjection(Projections.max("PARECER_OCOR."+ ScoParecerOcorrencia.Fields.DT_CRIACAO.toString()));			
			//criteria.add(Subqueries.propertyIn("SCOPOCO."+ ScoParecerOcorrencia.Fields.DT_CRIACAO.toString(), subQueryOcorrencias));
			criteria.add(Restrictions.eq("SCOPOCO." + ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString(), scoParecerMaterial));
			criteria.add(Restrictions.eq("SCOPOCO." + ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));
			return (ScoParecerOcorrencia) this.executeCriteriaUniqueResult(criteria);			
		}
		return null;		
	}

	public String obterParecerOcorrenciaMarcaModeloItemProposta(ScoItemPropostaFornecedor itemProposta, ScoMaterial material, Boolean testaMarca) {
		String ret = "";
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParecerOcorrencia.class, "POC");
		criteria.add(Restrictions.eq("POC."+ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));
			
		criteria.createAlias(ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString(), "PMT");
		criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MATERIAL.toString(), material));
		if (!testaMarca) {
			criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MARCA_MODELO.toString(), itemProposta.getModeloComercial()));
		} else {
			criteria.add(Restrictions.eq("PMT."+ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), itemProposta.getMarcaComercial()));
			criteria.add(Restrictions.isNull("PMT."+ScoParecerMaterial.Fields.MARCA_MODELO.toString()));
			
		}
		criteria.setProjection(Projections.property("POC." + ScoParecerOcorrencia.Fields.PARECER_OCORRENCIA.toString()));
		
		List<DominioParecerOcorrencia> listaParecerOcorrencia = executeCriteria(criteria);
		
		if (listaParecerOcorrencia != null && !listaParecerOcorrencia.isEmpty()) {
			ret = listaParecerOcorrencia.get(0).getDescricao();
		}
		return ret;
	}
	
	public  List<ScoParecerOcorrencia> listaOcorrenciaParecer(ScoParecerMaterial scoParecerMaterial, DominioSituacao situacao){
		if (scoParecerMaterial != null) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerOcorrencia.class, "SCOPOCO");
			
			criteria.createAlias("SCOPOCO." + ScoParecerOcorrencia.Fields.SERVIDOR_RESPONSAVEL.toString(), "SRV");
			criteria.createAlias("SRV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF_SRV");
			
			criteria.add(Restrictions.eq("SCOPOCO." + ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString(), scoParecerMaterial));
			
			if (situacao != null){
				criteria.add(Restrictions.eq("SCOPOCO." + ScoParecerOcorrencia.Fields.SITUACAO.toString(), situacao));
			}
			
			criteria.addOrder(Order.asc("SCOPOCO." + ScoParecerOcorrencia.Fields.SITUACAO.toString()));
			criteria.addOrder(Order.desc("SCOPOCO." + ScoParecerOcorrencia.Fields.DT_OCORRENCIA.toString()));
			return this.executeCriteria(criteria);			
		}
		return null;	
	}
	
	
	public List<ScoParecerOcorrencia> pesquisarParecerOcorrenciaItemProposta(ScoItemPropostaFornecedor itemProposta, ScoMaterial material, boolean testaMarcaModelo) {
         DetachedCriteria criteria =  DetachedCriteria
                         .forClass(ScoParecerOcorrencia.class, "POC");
         criteria.add(Restrictions.eq("POC."+ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));            
       
         
         criteria.createAlias(ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString(), "PMT");
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
         
         return executeCriteria(criteria);
    }	
	
	
	
}

package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Responsavel por motar a busca de sistemas por nome.<br>
 * Usando o valor de busca para filtar nos campos:<br>
 * <ol>
 * 	<li>SIGLA: MatchMode.EXACT</li>
 * 	<li>ou NOME: MatchMode.ANYWHERE</li>
 * </ol><br>
 * 
 * Classe concretas de build devem sempre ter modificador de acesso Default.<br>
 * 
 * <p>Exemplo de uso do QueryBuilder para org.hibernate.criterion.DetachedCriteria.
 * Com passagem dos filtros no proprio metodo build.</p>
 * 
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
class ObterCriteriaParecerOcorrenciaItemPropostaQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private List<DominioSituacaoParecer> listaSituacaoParecer;
	private boolean testaMarcaModelo;

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria subQueryCriteria = DetachedCriteria.forClass(ScoParecerOcorrencia.class, "POC");
		subQueryCriteria.createAlias(ScoParecerOcorrencia.Fields.PARECER_MATERIAL.toString(), "PMT");
		return subQueryCriteria;
	}

	@Override
	protected void doBuild(DetachedCriteria aProduct) {
		
		aProduct.add(Restrictions.eq("POC."+ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		aProduct.add(Restrictions.eqProperty("PMT."+ScoParecerMaterial.Fields.MATERIAL_CODIGO.toString(), "MATERIAL." + ScoMaterial.Fields.CODIGO.toString()));
        
		if (listaSituacaoParecer != null){
			aProduct.add(Restrictions.in("POC." + ScoParecerOcorrencia.Fields.PARECER_OCORRENCIA.toString(), listaSituacaoParecer));
		}
     
       if (testaMarcaModelo) {
    	   aProduct.add(Restrictions.eqProperty("PMT."+ScoParecerMaterial.Fields.MODELO_COMERCIAL_MARCA_CODIGO.toString(), "MARCA_ITL." + ScoMarcaComercial.Fields.CODIGO.toString()));
    	   aProduct.add(Restrictions.eqProperty("PMT."+ScoParecerMaterial.Fields.MODELO_COMERCIAL_SEQP.toString(), "MODELO_ITL." + ScoMarcaModelo.Fields.SEQP.toString()));
       } else {
    	   aProduct.add(Restrictions.eqProperty("PMT."+ScoParecerMaterial.Fields.MARCA_COMERCIAL_CODIGO.toString(), "MARCA_ITL." + ScoMarcaComercial.Fields.CODIGO.toString()));
    	   aProduct.add(Restrictions.isNull("PMT."+ScoParecerMaterial.Fields.MODELO_COMERCIAL_SEQP.toString()));		
       }
       aProduct.setProjection(Projections.property("POC." + ScoParecerOcorrencia.Fields.PARECER_OCORRENCIA.toString()));  
       
	   aProduct.add(Restrictions.eqProperty("PMT."+ScoParecerMaterial.Fields.MATERIAL_CODIGO.toString(), "MATERIAL." + ScoMaterial.Fields.CODIGO.toString())); 
 		
	}
	
	public DetachedCriteria build(boolean testaMarcaModelo, List<DominioSituacaoParecer> listaSituacaoParecer) {
		
		this.testaMarcaModelo = testaMarcaModelo;
		this.listaSituacaoParecer = listaSituacaoParecer;
		
		return super.build();
	}

}

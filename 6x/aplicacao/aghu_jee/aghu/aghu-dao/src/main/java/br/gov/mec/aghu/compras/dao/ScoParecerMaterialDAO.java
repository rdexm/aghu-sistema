package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.PareceresVO;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;

public class ScoParecerMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParecerMaterial> {	

	private static final long serialVersionUID = -419046106821575536L;
	
	public List<PareceresVO> pesquisarPareceresAvaliacoes(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro , Boolean matIndEstocavel) {
		
		
		
		final DetachedCriteria criteria = obterCriteriaPesquisaPareceres(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, false, nroRegistro, matIndEstocavel);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_PARECER.toString())
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.SITUACAO.toString()), PareceresVO.Fields.SITUACAO.toString())
				.add(Projections.property("MATERIAL."+ ScoMaterial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("MATERIAL."+ ScoMaterial.Fields.NOME.toString()), PareceresVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("MATERIAL."+ ScoMaterial.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property("GRUPO_MAT."+ ScoGrupoMaterial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_GRUPO_MATERIAL.toString())
				.add(Projections.property("GRUPO_MAT."+ ScoGrupoMaterial.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_GRUPO_MATERIAL.toString())
				.add(Projections.property("MARCA_COMERCIAL."+ ScoMarcaComercial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_MARCA_COMERCIAL.toString())
				.add(Projections.property("MARCA_COMERCIAL."+ ScoMarcaComercial.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_MARCA_COMERCIAL.toString())
				.add(Projections.property("MODELO_COMERCIAL."+ ScoMarcaModelo.Fields.SEQP.toString()), PareceresVO.Fields.CODIGO_MODELO_COMERCIAL.toString())
				.add(Projections.property("MODELO_COMERCIAL."+ ScoMarcaModelo.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_MODELO_COMERCIAL.toString())
				.add(Projections.property("ORIGEM_PARECER."+ ScoOrigemParecerTecnico.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_PASTA.toString())				
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.NUMERO_SUB_PASTA.toString()), PareceresVO.Fields.NUMERO_SUBPASTA.toString())
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.NR_REGISTRO.toString()), PareceresVO.Fields.NUMERO_REGISTRO.toString())
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.DT_VENCT_REGISTRO.toString()), PareceresVO.Fields.DT_VENCT_REGISTRO.toString())
				.add(Projections.property("ITL_AVAL."+ ScoParecerAvaliacao.Fields.PARECER_GERAL.toString()), PareceresVO.Fields.PARECER_GERAL.toString())
				.add(Projections.property("ITL_AVAL."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()), PareceresVO.Fields.DT_PARECER.toString())
				.add(Projections.property("ITL_AVAL."+ ScoParecerAvaliacao.Fields.SERVIDOR_CRIACAO.toString()), PareceresVO.Fields.SERVIDOR.toString());

		criteria.setProjection(projection);		
				
		criteria.setResultTransformer(Transformers.aliasToBean(PareceresVO.class));
		
		return this.executeCriteria(criteria, true);
		 
	}

	public List<PareceresVO> pesquisarPareceresOcorrencias(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro,  Boolean matIndEstocavel) {
		
		final DetachedCriteria criteria = obterCriteriaPesquisaPareceres(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, true, nroRegistro, matIndEstocavel);	
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_PARECER.toString())
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.SITUACAO.toString()), PareceresVO.Fields.SITUACAO.toString())
				.add(Projections.property("MATERIAL."+ ScoMaterial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("MATERIAL."+ ScoMaterial.Fields.NOME.toString()), PareceresVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("MATERIAL."+ ScoMaterial.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property("GRUPO_MAT."+ ScoGrupoMaterial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_GRUPO_MATERIAL.toString())
				.add(Projections.property("GRUPO_MAT."+ ScoGrupoMaterial.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_GRUPO_MATERIAL.toString())
				.add(Projections.property("MARCA_COMERCIAL."+ ScoMarcaComercial.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_MARCA_COMERCIAL.toString())
				.add(Projections.property("MARCA_COMERCIAL."+ ScoMarcaComercial.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_MARCA_COMERCIAL.toString())
				.add(Projections.property("MODELO_COMERCIAL."+ ScoMarcaModelo.Fields.SEQP.toString()), PareceresVO.Fields.CODIGO_MODELO_COMERCIAL.toString())
				.add(Projections.property("MODELO_COMERCIAL."+ ScoMarcaModelo.Fields.DESCRICAO.toString()), PareceresVO.Fields.DESCRICAO_MODELO_COMERCIAL.toString())
				.add(Projections.property("ORIGEM_PARECER."+ ScoOrigemParecerTecnico.Fields.CODIGO.toString()), PareceresVO.Fields.CODIGO_PASTA.toString())				
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.NUMERO_SUB_PASTA.toString()), PareceresVO.Fields.NUMERO_SUBPASTA.toString())
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.NR_REGISTRO.toString()), PareceresVO.Fields.NUMERO_REGISTRO.toString())
				.add(Projections.property("SCOPMT."+ ScoParecerMaterial.Fields.DT_VENCT_REGISTRO.toString()), PareceresVO.Fields.DT_VENCT_REGISTRO.toString())
				.add(Projections.property("ITL_OCOR."+ ScoParecerOcorrencia.Fields.PARECER_OCORRENCIA.toString()), PareceresVO.Fields.PARECER_OCORRENCIA.toString())
				.add(Projections.property("ITL_OCOR."+ ScoParecerOcorrencia.Fields.DT_CRIACAO.toString()), PareceresVO.Fields.DT_PARECER.toString())
				.add(Projections.property("ITL_OCOR."+ ScoParecerOcorrencia.Fields.SERVIDOR_CRIACAO.toString()), PareceresVO.Fields.SERVIDOR.toString());

		criteria.setProjection(projection);			
		
		criteria.setResultTransformer(Transformers.aliasToBean(PareceresVO.class));
		return this.executeCriteria(criteria,true);
	}
	
	
	public Long pesquisarPareceresAvaliacoesCount(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro, Boolean matIndEstocavel) {
		
		
		
		final DetachedCriteria criteria = obterCriteriaPesquisaPareceres(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, false, nroRegistro, matIndEstocavel);
				
		return this.executeCriteriaCount(criteria);
		 
	}

	public Long pesquisarPareceresOcorrenciasCount(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro, Boolean matIndEstocavel) {
		
		final DetachedCriteria criteria = obterCriteriaPesquisaPareceres(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, true, nroRegistro, matIndEstocavel);	
		
		 return this.executeCriteriaCount(criteria);
	}

	
	
	
	public void adicionarCriteriaAvaliacoesOcorrencias(Boolean apenasUltimosPareceres, boolean isOcorrencia,DetachedCriteria criteria) {
       if (!isOcorrencia){
			
			if (apenasUltimosPareceres.equals(Boolean.TRUE)){
				
				DetachedCriteria subQueryAvaliacoes = DetachedCriteria.forClass(ScoParecerAvaliacao.class,"PARECER_AVAL");
				
				subQueryAvaliacoes.add(Restrictions.eqProperty("SCOPMT."+ ScoParecerMaterial.Fields.CODIGO.toString(), "PARECER_AVAL."+ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString()));
				subQueryAvaliacoes.setProjection(Projections.max(ScoParecerAvaliacao.Fields.DT_CRIACAO.toString()));
				
				criteria.add(Subqueries.propertyIn("ITL_AVAL."+ ScoParecerAvaliacao.Fields.DT_CRIACAO.toString(), subQueryAvaliacoes));								
			
			    DetachedCriteria subQueryOcorrencias = DetachedCriteria.forClass(ScoParecerOcorrencia.class,"PARECER_OCOR");
			
			    subQueryOcorrencias.add(Restrictions.eqProperty("SCOPMT."+ ScoParecerMaterial.Fields.CODIGO.toString(), "PARECER_OCOR."+ScoParecerOcorrencia.Fields.PARECER_MATERIAL_CODIGO.toString()));
			    subQueryOcorrencias.add(Restrictions.eq("PARECER_OCOR."+ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));
			    subQueryOcorrencias.setProjection(Projections.property(ScoParecerOcorrencia.Fields.PARECER_MATERIAL_CODIGO.toString()));
			
			    criteria.add(Subqueries.propertyNotIn("ITL_AVAL."+ ScoParecerAvaliacao.Fields.PARECER_MATERIAL_CODIGO.toString(), subQueryOcorrencias));
			}
	 }else{
    	   if (apenasUltimosPareceres.equals(Boolean.TRUE)){
    		   criteria.add(Restrictions.eq("ITL_OCOR."+ScoParecerOcorrencia.Fields.SITUACAO.toString(), DominioSituacao.A));
    	   }
       }  
	}
	
	public void adicionarCriteriaMaterialGrupo(ScoMaterial material, ScoGrupoMaterial grupoMaterial, Boolean matIndEstocavel, DetachedCriteria criteria){
		if (material != null || grupoMaterial != null) {

			Criterion codigoMaterial = null;
			Criterion codigoGrupoMaterial = null; 

			if (material != null) {
				codigoMaterial = Restrictions.eq("MATERIAL." + ScoMaterial.Fields.CODIGO.toString(), material.getCodigo());				
			
			}
			if (grupoMaterial != null) {
				codigoGrupoMaterial = Restrictions.eq("GRUPO_MAT." + ScoGrupoMaterial.Fields.CODIGO.toString(), grupoMaterial.getCodigo());			
			}
			
			if (codigoMaterial !=null && codigoGrupoMaterial !=null){
				criteria.add(Restrictions.and(codigoMaterial, codigoGrupoMaterial));
			}
			else {
				criteria.add(codigoMaterial != null ? codigoMaterial : codigoGrupoMaterial);				
			}		
		}
		
		if (matIndEstocavel != null){
			criteria.add(Restrictions.eq("MATERIAL." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), matIndEstocavel));
		}
	}
	
	public DetachedCriteria obterCriteriaPesquisaPareceres(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta,
			boolean isOcorrencia, String nroRegistro, Boolean matIndEstocavel){

		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerMaterial.class, "SCOPMT");
		criteria.createAlias( "SCOPMT." + ScoParecerMaterial.Fields.ORIGEM_PARECER_TECNICO.toString(), "ORIGEM_PARECER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SCOPMT." + ScoParecerMaterial.Fields.MATERIAL.toString(), "MATERIAL", JoinType.INNER_JOIN);
		criteria.createAlias("MATERIAL." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GRUPO_MAT", JoinType.INNER_JOIN);	
		criteria.createAlias("SCOPMT." + ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), "MARCA_COMERCIAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SCOPMT." + ScoParecerMaterial.Fields.MARCA_MODELO.toString(), "MODELO_COMERCIAL", JoinType.LEFT_OUTER_JOIN);			
		
		if (isOcorrencia) {
		   criteria.createAlias("SCOPMT." + ScoParecerMaterial.Fields.ITENS_OCORRENCIA.toString(), "ITL_OCOR", JoinType.INNER_JOIN);
		}
		else {
			criteria.createAlias("SCOPMT." + ScoParecerMaterial.Fields.ITENS_AVALIACAO.toString(), "ITL_AVAL", JoinType.LEFT_OUTER_JOIN);	
		}
	
		this.adicionarCriteriaMaterialGrupo(material, grupoMaterial, matIndEstocavel, criteria);

		if (marcaComercial != null) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), marcaComercial));
		}
		if (modeloComercial != null) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.MARCA_MODELO.toString(), modeloComercial));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.SITUACAO.toString(), situacao));
		}
		if (parecerFinal != null) {
			if (isOcorrencia){
			     criteria.add(Restrictions.eq("ITL_OCOR." + ScoParecerOcorrencia.Fields.PARECER_OCORRENCIA.toString(), parecerFinal));
			}
			else {
				criteria.add(Restrictions.eq("ITL_AVAL." + ScoParecerAvaliacao.Fields.PARECER_GERAL.toString(), parecerFinal));
			}
		}
		if (pasta != null) {			
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.ORIGEM_PARECER_TECNICO.toString(), pasta));
			
		}
		if (nroSubPasta != null) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.NUMERO_SUB_PASTA.toString(), nroSubPasta));
		}
		
		if (StringUtils.isNotBlank(nroRegistro)) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.NR_REGISTRO.toString(), nroRegistro));
		}	
		
		this.adicionarCriteriaAvaliacoesOcorrencias(apenasUltimosPareceres, isOcorrencia, criteria);		
		return criteria;
	}	
	
	public ScoParecerMaterial obterParecerTecnicoDuplicidade(final ScoParecerMaterial scoParecerMaterial)
	{
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerMaterial.class, "SCOPMT");

		if (scoParecerMaterial != null){
			if (scoParecerMaterial.getCodigo() != null) {
				criteria.add(Restrictions.ne(ScoParecerMaterial.Fields.CODIGO.toString(), scoParecerMaterial.getCodigo()));
			}
			
			if (scoParecerMaterial.getMaterial() != null){
				criteria.add(Restrictions.eq(ScoParecerMaterial.Fields.MATERIAL.toString(), scoParecerMaterial.getMaterial()));	
			}
			
			if (scoParecerMaterial.getMarcaComercial() != null){
				criteria.add(Restrictions.eq(ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), scoParecerMaterial.getMarcaComercial()));	
			}
			if (scoParecerMaterial.getScoMarcaModelo() != null){
				criteria.add(Restrictions.eq(ScoParecerMaterial.Fields.MARCA_MODELO.toString(), scoParecerMaterial.getScoMarcaModelo()));	
			}	
			else {
				criteria.add(Restrictions.isNull(ScoParecerMaterial.Fields.MARCA_MODELO.toString()));
			}
			
			criteria.addOrder(Order.desc(ScoParecerMaterial.Fields.DT_CRIACAO.toString()));
			
			List<ScoParecerMaterial> listaParecerMaterial = this.executeCriteria(criteria);
			
			if (listaParecerMaterial.size() > 0) {
			   return listaParecerMaterial.get(0);
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Método para buscar registros com OPT_CODIGO específico
	 * @param pasta
	 * @author dilceia.alves
	 * @since 15/04/2013
	 */
	public List<ScoParecerMaterial> pesquisarParecerPorCodOrigem(ScoOrigemParecerTecnico pasta) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerMaterial.class);
		
		criteria.add(Restrictions.eq(
				ScoParecerMaterial.Fields.ORIGEM_PARECER_TECNICO.toString(), pasta));

		return this.executeCriteria(criteria);
	}
	
	public List<ScoParecerMaterial> pesquisarParecerTecnicoMaterialMarca(final ScoMaterial scoMaterial, final ScoMarcaComercial scoMarcaComercial){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerMaterial.class, "SCOPMT");				
		
		if (scoMaterial != null) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.MATERIAL.toString(), scoMaterial));
		}
		
		if (scoMarcaComercial != null) {
			criteria.add(Restrictions.eq("SCOPMT." + ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), scoMarcaComercial));
		}		
		
		return this.executeCriteria(criteria);
	
	}
	
	
	public Integer obterMaxNumeroSubPasta(ScoOrigemParecerTecnico pasta) {
		if(pasta==null){
			return null;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerMaterial.class);
		criteria.createAlias(ScoParecerMaterial.Fields.ORIGEM_PARECER_TECNICO.toString(), "OPT");
		criteria.add(Restrictions.eq("OPT."+ScoOrigemParecerTecnico.Fields.CODIGO.toString(), pasta.getCodigo()));
		criteria.setProjection(Projections.max(ScoParecerMaterial.Fields.NUMERO_SUB_PASTA.toString()));
		Integer max = (Integer) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			return 1;
		}
		return ++max;
	}
	
	public ScoParecerMaterial obterParecerTecnicoAtivo(final ScoParecerMaterial scoParecerMaterial, Boolean isMarca) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(ScoParecerMaterial.class, "SCOPMT");
			if (scoParecerMaterial != null){
				criteria.add(Restrictions.eq(ScoParecerMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
				if (scoParecerMaterial.getMaterial() != null){
					criteria.add(Restrictions.eq(ScoParecerMaterial.Fields.MATERIAL.toString(), scoParecerMaterial.getMaterial()));	
				}
				if (isMarca && scoParecerMaterial.getMaterial() != null && scoParecerMaterial.getMarcaComercial() != null){
					criteria.add(Restrictions.eq(ScoParecerMaterial.Fields.MARCA_COMERCIAL.toString(), scoParecerMaterial.getMarcaComercial()));	
				}
				criteria.addOrder(Order.desc(ScoParecerMaterial.Fields.DT_CRIACAO.toString()));
				List<ScoParecerMaterial> listaParecerMaterial = this.executeCriteria(criteria);
				if (listaParecerMaterial.size() > 0) {
				   return listaParecerMaterial.get(0);
				}
				else {
					return null;
				}
			}
			return null;
	}
	
}

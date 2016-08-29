package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.PacoteMateriaisVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemPacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Classe de acesso à entidade <code>br.gov.mec.aghu.model.ScePacoteMateriais</code>
 * 
 * @author guilherme.finotti
 *
 */
public class ScePacoteMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScePacoteMateriais> {
	
	
	private static final long serialVersionUID = 2626931282866903129L;

	/**
	 * Retorna um pacote de acordo com a chave informada.
	 * 
	 * @param pacoteId
	 * @return
	 */
	public ScePacoteMateriais obterPorChavePrimaria(ScePacoteMateriaisId pacoteId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class, "pacote");
		criteria.add(Restrictions.eq("pacote.id", pacoteId));
		return (ScePacoteMateriais) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScePacoteMateriais> pesquisarPacoteMateriaisParaTrasnferenciaEventual(String _input) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class);
		
        criteria.createAlias(ScePacoteMateriais.Fields.CENTRO_CUSTO_APLICACAO.toString(), "CCA");
        criteria.createAlias(ScePacoteMateriais.Fields.CENTRO_CUSTO_PROPRIETARIO.toString(), "CCP");

		String descricao = null;
		Integer codigo = null;

		if(CoreUtil.isNumeroInteger(_input)){
			codigo = Integer.valueOf(_input);			
		}else{
			descricao = _input;
		}
		
		if (codigo != null) {
			Criterion criterion1 = Restrictions.eq(ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO.toString(), codigo);
			Criterion criterion2 = Restrictions.eq(ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString(), codigo);
			Criterion criterion3 = Restrictions.eq(ScePacoteMateriais.Fields.NUMERO.toString(), codigo);
			criteria.add(Restrictions.or(criterion1, Restrictions.or(criterion2, criterion3)));
		}

		if (descricao != null && !descricao.isEmpty()) {
			criteria.add(Restrictions.ilike(ScePacoteMateriais.Fields.DESCRICAO.toString(),	descricao, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(ScePacoteMateriais.Fields.NUMERO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param seqAlmoxarifado
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param codigoGrupoMaterial
	 * @param objPesquisa
	 * @return
	 */
	public List<ScePacoteMateriais> pesquisarPacoteMateriaisGerarRequisicaoMaterial(Short seqAlmoxarifado, Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer codigoGrupoMaterial, String objPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class,"PMT");
		
		criteria.createAlias("PMT." + ScePacoteMateriais.Fields.ALMOXARIFADO.toString(), "ALM");
        criteria.createAlias("PMT." + ScePacoteMateriais.Fields.CENTRO_CUSTO_APLICACAO.toString(), "CCA");
        criteria.createAlias("PMT." + ScePacoteMateriais.Fields.CENTRO_CUSTO_PROPRIETARIO.toString(), "CCP");
        
        criteria.add(Restrictions.eq("ALM." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifado));
        criteria.add(Restrictions.eq("CCA." + FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCustoAplicacao));
        criteria.add(Restrictions.eq("CCP." + FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCustoProprietario));
        
        if(objPesquisa != null){
     	   
      	  String parametro = objPesquisa.trim();
   
      	  if(StringUtils.isNotBlank(parametro) && StringUtils.isNumeric(parametro)){
      		  criteria.add(Restrictions.eq("PMT." + ScePacoteMateriais.Fields.NUMERO.toString(), Integer.valueOf(parametro)));
      	  } else if(StringUtils.isNotBlank(parametro)){
      		  criteria.add(Restrictions.ilike("PMT." + ScePacoteMateriais.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
      	  }
      	  
         }
        
       if(codigoGrupoMaterial != null){
    	   
           DetachedCriteria subQuery = DetachedCriteria.forClass(SceItemPacoteMateriais.class, "IPM");
           
           subQuery.setProjection(Projections.property("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString()));
           
           subQuery.createAlias("IPM." + SceItemPacoteMateriais.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAM");
           subQuery.createAlias("EAM." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
           subQuery.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
           
           subQuery.add(Property.forName("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString()).eqProperty("PMT." + ScePacoteMateriais.Fields.NUMERO.toString()));
           subQuery.add(Property.forName("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString()).eqProperty("CCA." + FccCentroCustos.Fields.CODIGO.toString()));
           subQuery.add(Property.forName("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString()).eqProperty("CCP." + FccCentroCustos.Fields.CODIGO.toString()));
           
           subQuery.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), codigoGrupoMaterial));
           
           criteria.add(Subqueries.exists(subQuery));
        }

        criteria.add(Restrictions.eq("PMT." + ScePacoteMateriais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        criteria.addOrder(Order.asc("PMT." + ScePacoteMateriais.Fields.NUMERO.toString()));
        
        return this.executeCriteria(criteria);
        
	}
	
	
	/**
	 * Retorna o próximo número de pacote de acordo com os centros custo informados
	 * @param centroCustoProprietario
	 * @param centroCustoAplicacao
	 * @return
	 */
	public Integer obterProximoNumeroPacote(Integer centroCustoProprietario, Integer centroCustoAplicacao) {
		
		final int SOMA_MAIS_UM_PROX_NUMERO = 1;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class, "pacote");
		ProjectionList proj = Projections.projectionList();
		criteria.setProjection(proj.add(Projections.max("pacote." + ScePacoteMateriais.Fields.NUMERO.toString())));
		
		criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO.toString(), centroCustoProprietario));
		criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString(), centroCustoAplicacao));
		
		Object resultado = executeCriteriaUniqueResult(criteria);
		
		if(resultado == null){
			resultado = 0;
		}
		
		return (Integer) resultado + SOMA_MAIS_UM_PROX_NUMERO;
	}
	
	/**
	 * Pesquisa PacoteMateriais de acordo com os parâmetros informados
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @param numeroAlmoxarifado
	 * @param situacao
	 * @return
	 */
	public List<ScePacoteMateriais> pesquisarPacoteMateriais(Integer firstResult, 
															 Integer maxResult, 
															 String orderProperty,
															 boolean asc,
															 Integer codigoCentroCustoProprietario, 
		     												 Integer codigoCentroCustoAplicacao,
		     												 Integer numeroPacote,
		     												 Short numeroAlmoxarifado,
		     												 DominioSituacao situacao) {
		
		DetachedCriteria criteria = montarCriteriaPesquisaPacoteMateriais(codigoCentroCustoProprietario, codigoCentroCustoAplicacao, 
				numeroPacote, numeroAlmoxarifado, situacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Retorna quantidade de registros.
	 * @return
	 */	
	public Long pesquisarPacoteMateriaisCount(Integer codigoCentroCustoProprietario, 
			 Integer codigoCentroCustoAplicacao,
			 Integer numeroPacote,
			 Short numeroAlmoxarifado,
			 DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaPesquisaPacoteMateriais(codigoCentroCustoProprietario, 
				codigoCentroCustoAplicacao, 
				numeroPacote, numeroAlmoxarifado, situacao);
		criteria.createAlias("pacote." + ScePacoteMateriais.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.LEFT_OUTER_JOIN);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisaPacoteMateriais(Integer codigoCentroCustoProprietario, 
			 Integer codigoCentroCustoAplicacao,
			 Integer numeroPacote,
			 Short numeroAlmoxarifado,
			 DominioSituacao situacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class, "pacote");
		
		//criteria.createAlias("pacote." + ScePacoteMateriais.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pacote." + ScePacoteMateriais.Fields.CENTRO_CUSTO_APLICACAO.toString(), "CCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pacote." + ScePacoteMateriais.Fields.CENTRO_CUSTO_PROPRIETARIO.toString(), "CCP", JoinType.LEFT_OUTER_JOIN);

		if(codigoCentroCustoProprietario != null) {
			criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO.toString(), codigoCentroCustoProprietario));
		}
		if(codigoCentroCustoAplicacao != null) {
			criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString(), codigoCentroCustoAplicacao));
		}
		if(numeroPacote != null && numeroPacote.intValue() != 0) {			
			criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.NUMERO.toString(), numeroPacote));
		}
		if(numeroAlmoxarifado != null) {			
			criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.ALMOXARIFADO_CODIGO.toString(), numeroAlmoxarifado));
		}
		if(situacao != null && !"".equals(situacao)) {			
			criteria.add(Restrictions.eq("pacote." + ScePacoteMateriais.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		return criteria;
	}
	
	/**
	 * Obtém pacote de materiais como objeto de valor, com variáveis
	 * primitivas, necessárias para pesquisa
	 * @param id
	 * @return
	 */
	public PacoteMateriaisVO obterPacoteMaterialVO(ScePacoteMateriaisId id) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class, "PAC");
		
		criteria.createAlias("PAC." + ScePacoteMateriais.Fields.ALMOXARIFADO.toString(), "ALM", Criteria.INNER_JOIN);
		criteria.createAlias("PAC." + ScePacoteMateriais.Fields.CENTRO_CUSTO_PROPRIETARIO.toString(), "CCP", Criteria.INNER_JOIN);
		criteria.createAlias("PAC." + ScePacoteMateriais.Fields.CENTRO_CUSTO_APLICACAO.toString(), "CCA", Criteria.INNER_JOIN);
		
		ProjectionList pList = Projections.projectionList();

		pList.add(Projections.property("PAC." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO.toString()), PacoteMateriaisVO.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO.toString());
		pList.add(Projections.property("PAC." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString()), PacoteMateriaisVO.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString());
		pList.add(Projections.property("CCP." + FccCentroCustos.Fields.DESCRICAO.toString()), PacoteMateriaisVO.Fields.DESCRICAO_CENTRO_CUSTO_PROPRIETARIO.toString());
		pList.add(Projections.property("CCA." + FccCentroCustos.Fields.DESCRICAO.toString()), PacoteMateriaisVO.Fields.DESCRICAO_CENTRO_CUSTO_APLICACAO.toString());
		pList.add(Projections.property("PAC." + ScePacoteMateriais.Fields.ALMOXARIFADO_CODIGO.toString()), PacoteMateriaisVO.Fields.CODIGO_ALMOXARIFADO.toString());
		pList.add(Projections.property("ALM." + SceAlmoxarifado.Fields.DESCRICAO.toString()), PacoteMateriaisVO.Fields.DESCRICAO_ALMOXARIFADO.toString());
		pList.add(Projections.property("PAC." + ScePacoteMateriais.Fields.IND_SITUACAO.toString()), PacoteMateriaisVO.Fields.SITUACAO.toString());
		pList.add(Projections.property("PAC." + ScePacoteMateriais.Fields.DESCRICAO.toString()), PacoteMateriaisVO.Fields.DESCRICAO.toString());
		pList.add(Projections.property("PAC." + ScePacoteMateriais.Fields.NUMERO.toString()), PacoteMateriaisVO.Fields.NUMERO.toString());
		
		criteria.setProjection(pList);
		
		
		criteria.add(Restrictions.eq("PAC." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO.toString(),
				id.getCodigoCentroCustoProprietario()));
		
		criteria.add(Restrictions.eq("PAC." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO.toString(),
					id.getCodigoCentroCustoAplicacao()));
		
		criteria.add(Restrictions.eq("PAC." + ScePacoteMateriais.Fields.NUMERO.toString(),
				id.getNumero()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacoteMateriaisVO.class));
		
		return (PacoteMateriaisVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtém pacote com ítens, por chave primária
	 * @param pacoteId
	 * @return
	 */
	public ScePacoteMateriais obterPacoteMateriaisComItensPorChavePrimaria(
			ScePacoteMateriaisId pacoteId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScePacoteMateriais.class, "pacote");
		criteria.setFetchMode("pacote.itens", FetchMode.JOIN);
		criteria.add(Restrictions.eq("pacote.id", pacoteId));
		return (ScePacoteMateriais) executeCriteriaUniqueResult(criteria);
	}
	
}
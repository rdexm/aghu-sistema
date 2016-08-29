package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;

public class ScoProgrCodAcessoFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoProgrCodAcessoForn>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 128742292662407258L;

	public Long listarFornecedoresCount(final ScoFornecedor fornecedor) {
		DetachedCriteria criteria = obterCriteriaDefault(fornecedor);
		return executeCriteriaCount(criteria);
	}
	
	public List<ScoProgrCodAcessoForn> listarFornecedores(final ScoFornecedor fornecedor, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaDefault(fornecedor);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public ScoProgrCodAcessoForn obterPorFornecedor(final ScoFornecedor fornecedor) {
		DetachedCriteria criteria = obterCriteriaDefault(fornecedor);
		List<ScoProgrCodAcessoForn> resultado = executeCriteria(criteria);
		return !resultado.isEmpty() ? resultado.get(0) : null;
	}
	
	private DetachedCriteria obterCriteriaDefault(final ScoFornecedor fornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrCodAcessoForn.class);
		
		criteria.createAlias(ScoProgrCodAcessoForn.Fields.SCO_FORNECEDOR.toString(), "scoFornecedor");
		
		if (fornecedor != null) {
			criteria.add(Restrictions.eq("scoFornecedor.numero", fornecedor.getNumero()));
		}
		return criteria;
	}
	
	public String consultarCodigoAcesso(String codigoAcesso, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrCodAcessoForn.class);
		
		criteria.createAlias(ScoProgrCodAcessoForn.Fields.SCO_FORNECEDOR.toString(), "FORN");
		criteria.add(Restrictions.eq(ScoProgrCodAcessoForn.Fields.COD_ACESSO.toString(), codigoAcesso));
		criteria.add(Restrictions.eq("FORN.numero", numeroFornecedor));
		
		criteria.setProjection(Projections.property(ScoProgrCodAcessoForn.Fields.COD_ACESSO.toString()));
		
		return (String) executeCriteriaUniqueResult(criteria); 
	}

	public boolean existeAcessoFornecedorPorFornecedor(ScoFornecedor fornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrCodAcessoForn.class);
		criteria.add(Restrictions.eq(ScoProgrCodAcessoForn.Fields.SCO_FORNECEDOR.toString(), fornecedor));
		criteria.add(Restrictions.isNotNull(ScoProgrCodAcessoForn.Fields.COD_ACESSO.toString()));
		return this.executeCriteriaCount(criteria) > 0;
	}
	
	public ScoProgrCodAcessoForn obterPorChavePrimariaEPessoaFisica (Integer seq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrCodAcessoForn.class);
		
		criteria.createAlias(ScoProgrCodAcessoForn.Fields.RAP_SERVIDORES_BY_SCO_CAF_SER_FK1.toString(),"RAP_SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoProgrCodAcessoForn.Fields.RAP_SERVIDORES_BY_SCO_CAF_SER_ALTERACAO_FK1.toString(),"RAP_SERV_ALT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP_SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PESSOA_FISICA",JoinType.INNER_JOIN);
		criteria.createAlias("RAP_SERV_ALT." + RapServidores.Fields.PESSOA_FISICA.toString(), "PESSOA_FISICA_ALT",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoProgrCodAcessoForn.Fields.SCO_FORNECEDOR.toString(), "SCO_FORNECEDOR",JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.idEq(seq));
		
		ScoProgrCodAcessoForn result = (ScoProgrCodAcessoForn) executeCriteriaUniqueResult(criteria);
		
		return result;
	}
	
	public boolean existeAcessoFornecedorPorFornecedorDtEnvio(ScoFornecedor fornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgrCodAcessoForn.class);
		criteria.add(Restrictions.eq(ScoProgrCodAcessoForn.Fields.SCO_FORNECEDOR.toString(), fornecedor));
		criteria.add(Restrictions.isNotNull(ScoProgrCodAcessoForn.Fields.DT_ENVIO_FORNECEDOR.toString()));
		return this.executeCriteriaCount(criteria) > 0;
	}
	
}

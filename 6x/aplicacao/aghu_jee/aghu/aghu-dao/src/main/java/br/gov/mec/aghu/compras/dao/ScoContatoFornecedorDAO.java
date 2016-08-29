package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ScoContatoFornecedorDAO extends BaseDao<ScoContatoFornecedor>{
	
	private static final long serialVersionUID = 8972996021421662015L;
	
	
	/**
	 * Retorna a lista de contatos por email do fornecedor.
	 * 
	 * @param ScoFornecedor fornecedor
	 * @return lista de emails do fornecedor que permite receber emails
	 */
	public List<String> obterContatosPorEmailFornecedor(final ScoFornecedor fornecedor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class);
		criteria.setProjection(Projections.property(ScoContatoFornecedor.Fields._E_MAIL.toString()));
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), fornecedor));
		criteria.add(Restrictions.isNotNull(ScoContatoFornecedor.Fields._E_MAIL.toString()));
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.IND_REC_EMAIL_AF.toString(), true));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Retorna a lista de contatos do fornecedor.
	 * 
	 * @param numero do Fornecedor
	 * @return lista de contatos do fornecedor
	 */
	public List<ScoContatoFornecedor> pesquisarContatosPorFornecedor(Integer numFornecedor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "CF");
		criteria.createAlias("CF."+ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), "FORN");
		criteria.add(Restrictions.eq("FORN."+ScoFornecedor.Fields.NUMERO.toString(), numFornecedor));
		criteria.addOrder(Order.asc("FORN."+ScoFornecedor.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("CF."+ScoContatoFornecedor.Fields.NUMERO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna o próximo numero a ser inserido na tabela SCO_CONTATO_FORNECEDOR
	 * 
	 * @param numero do Fornecedor
	 * @return Short
	 */
	public Short  buscarNumeroContatoFornecedor(Integer numFornecedor){
				
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "CF");
		criteria.createAlias("CF."+ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), "FORN");
		criteria.setProjection(Projections.max("CF."+ScoContatoFornecedor.Fields.NUMERO));  
		criteria.add(Restrictions.eq("FORN."+ScoFornecedor.Fields.NUMERO.toString(), numFornecedor));
		
		Short numRetorno = (Short) executeCriteriaUniqueResult(criteria);
		if (numRetorno!=null ){
			return (short) (numRetorno + 1);
		}else {
			return numRetorno = 1;
		}
	}
	
	public Long verificarContatosFornecedor(Integer numeroFornecedor, Date dtEnvioContato) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "CF");
		criteria.createAlias("CF."+ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), "FORN");
		criteria.add(Restrictions.eq("FORN."+ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		criteria.add(Restrictions.eq("CF."+ScoContatoFornecedor.Fields.IND_ATU_CONTATO.toString(), true));
		criteria.add(Restrictions.gt("CF."+ScoContatoFornecedor.Fields.DT_ATU_CONTATO.toString(), dtEnvioContato));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna a lista de contatos por email do fornecedor.
	 * 
	 * @param ScoFornecedor fornecedor
	 * @return lista de emails do fornecedor que permite receber emails
	 */
	public List<ScoContatoFornecedor> obterContatosParaReceberEmailFornecedor(final ScoFornecedor fornecedor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class);
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), fornecedor));
		criteria.add(Restrictions.isNotNull(ScoContatoFornecedor.Fields._E_MAIL.toString()));
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.IND_REC_EMAIL_AF.toString(), true));
		criteria.addOrder(Order.asc(ScoContatoFornecedor.Fields._E_MAIL.toString()));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria montarCriteriaMinimoNumContatoForn() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "CF2");
		criteria.add(Restrictions.eqProperty("CF."+ScoContatoFornecedor.Fields.FRN_NUMERO.toString(), "CF2."+ScoContatoFornecedor.Fields.FRN_NUMERO.toString()));
		criteria.setProjection(Projections.min("CF2."+ScoContatoFornecedor.Fields.NUMERO.toString()));
		
		return criteria;
	}

	/**
	 * C7
	 * @param numFornecedor
	 * @return
	 */
	public ScoContatoFornecedor pesquisarContatoPorFornecedor(Integer numFornecedor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "CF");
		criteria.createAlias("CF."+ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), "FORN");
		criteria.add(Restrictions.eq("FORN."+ScoFornecedor.Fields.NUMERO.toString(), numFornecedor));
		final Criterion subqueryMinimoNumContatoForn = Property.forName("CF."+ScoContatoFornecedor.Fields.NUMERO.toString()).in(montarCriteriaMinimoNumContatoForn());
		criteria.add(subqueryMinimoNumContatoForn);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CF."+ScoContatoFornecedor.Fields.NOME.toString()),"nome")
				.add(Projections.property("CF."+ScoContatoFornecedor.Fields._E_MAIL.toString()),"EMail")
				.add(Projections.property("CF."+ScoContatoFornecedor.Fields.FONE.toString()),"fone"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoContatoFornecedor.class));
		Object obj = executeCriteriaUniqueResult(criteria);
		return (ScoContatoFornecedor) obj;
	}
	
	/**
	 * Consulta fornecedor pelo numero de autorizacao
	 * #24965
	 * 
	 * @param numeroAutorizacaoFornecedor numero de autorizacao do fornecedor
	 */
	public ScoContatoFornecedor obterFornecedorPorNumeroAutorizacaoFornecedor(Integer numeroAutorizacaoFornecedor) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "contForn");
		criteria.createAlias("contForn." + ScoContatoFornecedor.Fields.SCO_FORNECEDOR.toString(), "forn");
		
		final DetachedCriteria subAutForn = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "autForn");
		subAutForn.createAlias("autForn." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "propForn");
		subAutForn.add(Restrictions.eq("autForn." + ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAutorizacaoFornecedor));
		subAutForn.add(Restrictions.eqProperty("autForn." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), "forn." + ScoFornecedor.Fields.NUMERO.toString()));
		ProjectionList projectionListSubAutForn = Projections.projectionList();
		projectionListSubAutForn.add(Projections.property("autForn." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
		subAutForn.setProjection(projectionListSubAutForn);
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoContatoFornecedor.class, "FORN2");
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.min("FORN2." + ScoContatoFornecedor.Fields.NUMERO.toString()));
		subCriteria.setProjection(projectionList);
		subCriteria.add(Restrictions.eqProperty("FORN2." + ScoContatoFornecedor.Fields.SCO_FORNECEDOR_NUMERO.toString(), "contForn." + ScoContatoFornecedor.Fields.SCO_FORNECEDOR_NUMERO.toString()));
		
		criteria.add(Property.forName("contForn." + ScoContatoFornecedor.Fields.NUMERO.toString()).in(subCriteria));	
		criteria.add(Subqueries.exists(subAutForn));
				
		
		return (ScoContatoFornecedor)executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScoContatoFornecedor> buscaContatosFornecedor(Integer pfrFrnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class);
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.IND_REC_EMAIL_AF.toString(), true));
		criteria.add(Restrictions.isNotNull(ScoContatoFornecedor.Fields._E_MAIL.toString()));
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.FRN_NUMERO.toString(), pfrFrnNumero));
		return executeCriteria(criteria);
	}
	
	public List<ScoContatoFornecedor> buscaEmail(Integer pfrFrnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class);
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.IND_REC_EMAIL_AF.toString(), true));
		criteria.add(Restrictions.isNotNull(ScoContatoFornecedor.Fields._E_MAIL.toString()));
		criteria.add(Restrictions.eq(ScoContatoFornecedor.Fields.FRN_NUMERO.toString(), pfrFrnNumero));
		return executeCriteria(criteria);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// #37798 - C4
	
	public List<ScoContatoFornecedor> pesquisarContatoFornecedorPorNumero(final Integer frnNumero) {
		return this.executeCriteria(criarCriteriaPesquisarContatoFornecedorPorNumero(frnNumero), 0, 100, null, false);
	}
	
	public Long pesquisarContatoFornecedorPorNumeroCount(Integer frnNumero) {
		return executeCriteriaCount(criarCriteriaPesquisarContatoFornecedorPorNumero(frnNumero));
	}	

	private DetachedCriteria criarCriteriaPesquisarContatoFornecedorPorNumero(final Integer frnNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContatoFornecedor.class);

		if (frnNumero != null){
			criteria.add((Restrictions.eq(ScoContatoFornecedor.Fields.FRN_NUMERO.toString(), frnNumero)));
		}
		return criteria;
	}
	
}

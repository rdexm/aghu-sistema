package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoContaCorrenteFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoContaCorrenteFornecedorDAO extends BaseDao<ScoContaCorrenteFornecedor> {

	private static final long serialVersionUID = -6390416994902382284L;

	public List<ScoContaCorrenteFornecedor> obterScoContaCorrenteFornecedorPorFornecedor(final ScoFornecedor fornecedor){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContaCorrenteFornecedor.class, "SCCF");
		criteria.add(Restrictions.eq(ScoContaCorrenteFornecedor.Fields.SCO_FORNECEDOR.toString(), fornecedor));
		return executeCriteria(criteria);
	}
	
	public boolean existeContaPreferencialParaFornecedor(Integer numeroFornecedor, DominioSimNao preferencial, boolean isUpdate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContaCorrenteFornecedor.class);
		criteria.add(Restrictions.eq(ScoContaCorrenteFornecedor.Fields.SCO_FORNECEDOR_NUMERO.toString(), numeroFornecedor));
		if(isUpdate){
			criteria.add(Restrictions.eq(ScoContaCorrenteFornecedor.Fields.IND_PREFERENCIAL.toString(), DominioSimNao.S));
		}else {
			criteria.add(Restrictions.eq(ScoContaCorrenteFornecedor.Fields.IND_PREFERENCIAL.toString(), preferencial));
		}
		return executeCriteriaExists(criteria);
	}
	
	public Long countContaCorrenteAssociadasPorAgenciaBanco(Short bcoCodigo, Integer codigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoContaCorrenteFornecedor.class, "SCCF");
		
		criteria.add(Restrictions.eq(ScoContaCorrenteFornecedor.Fields.AGB_BCO_CODIGO.toString(), Integer.valueOf(bcoCodigo)));
		criteria.add(Restrictions.eq(ScoContaCorrenteFornecedor.Fields.AGB_CODIGO.toString(), codigo));
		
		return executeCriteriaCount(criteria);
	}
}

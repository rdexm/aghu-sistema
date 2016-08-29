package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpValorTributos;
import br.gov.mec.aghu.model.SceNotaRecebimento;

public class FcpValorTributosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FcpValorTributos> {

	private static final long serialVersionUID = -4781542075367250688L;

	public FcpValorTributos buscarValorTributoPorNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpValorTributos.class);
		criteria.add(Restrictions.eq(FcpValorTributos.Fields.NOTA_RECEBIMENTO.toString(), notaRecebimento));
		return (FcpValorTributos) executeCriteriaUniqueResult(criteria);
	}

	public boolean verificarExclusao(FcpRetencaoAliquota fcpRetencaoAliquota) {

		StringBuffer hql = new StringBuffer(250);
		hql.append("SELECT 1 FROM FcpValorTributos fvt ");
		hql.append("WHERE fvt.id.farFriCodigo = :codigoRecolhimento ");
		hql.append("AND ");
		hql.append("fvt.id.farDtInicioValidade = :dataInicial ");
		hql.append("AND ");
		hql.append("fvt.id.farImposto = :imposto ");
		hql.append("AND ");
		hql.append("fvt.id.farNumero = :numero ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("codigoRecolhimento", fcpRetencaoAliquota.getId().getFriCodigo());
		query.setParameter("dataInicial", fcpRetencaoAliquota.getId().getDtInicioValidade());
		query.setParameter("imposto", fcpRetencaoAliquota.getId().getImposto());
		query.setParameter("numero", fcpRetencaoAliquota.getId().getNumero());

		// query.setResultTransformer(Transformers.aliasToBean(CadastroVO.class));
		if (query.list().size() > 0) {
			return false;

		} else {

			return true;
		}

	}
	
	public Double obterTotalTributosPorNrsSeqETitulo(Integer titulo, Integer nrsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpValorTributos.class);

		criteria.setProjection(Projections.sum(FcpValorTributos.Fields.VALOR.toString()));
		
		if (titulo != null) {
			criteria.add(Restrictions.eq(FcpValorTributos.Fields.TTL_SEQ.toString(), titulo));
		}
		if (nrsSeq != null) {
			criteria.add(Restrictions.eq(FcpValorTributos.Fields.INS_NRS_SEQ.toString(), nrsSeq));
		}
		
		return (Double) executeCriteriaUniqueResult(criteria);
	}	
	
	public List<String> pesquisarTributosNotaRecebimentoTitulo(Integer notaRecebimentoNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpValorTributos.class);
		criteria.createAlias(FcpValorTributos.Fields.NOTA_RECEBIMENTO.toString(), "NRS", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.distinct(Projections.property(FcpValorTributos.Fields.FAR_IMPOSTO.toString())));
		criteria.add(Restrictions.eq("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), notaRecebimentoNumero));
		return executeCriteria(criteria);
	}

}

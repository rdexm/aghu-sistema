package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.VSceItemNotaRecebimentoAutorizacaoFornecimento;

public class VSceItemNotaRecebimentoAutorizacaoFornecimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VSceItemNotaRecebimentoAutorizacaoFornecimento> {
	
	private static final long serialVersionUID = -3827236346059799043L;

	public List<VSceItemNotaRecebimentoAutorizacaoFornecimento> pesquisarMaterialPorAutorizacaoFornecimentoNaoEfetivadaOuParcialmenteAtendida(Integer afnNumero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VSceItemNotaRecebimentoAutorizacaoFornecimento.class);
		
		criteria.add(Restrictions.eq(VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.AFN_NUMERO.toString(), afnNumero));
		
		DominioSituacaoAutorizacaoFornecimento[] situacoesInvalidas = {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA};
		criteria.add(Restrictions.in(VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.SITUACAO.toString(), situacoesInvalidas));
		
		criteria.addOrder(Order.asc(VSceItemNotaRecebimentoAutorizacaoFornecimento.Fields.NUMERO.toString()));

		return executeCriteria(criteria);
	}

}

package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelInformacaoSolicitacaoUnidadeExecutora;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelInformacaoSolicitacaoUnidadeExecutoraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelInformacaoSolicitacaoUnidadeExecutora> {

	private static final long serialVersionUID = -7946771693432817324L;

	public List<AelInformacaoSolicitacaoUnidadeExecutora> pesquisarInformacaoPorItemSolicitacaoExame(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		CoreUtil.validaParametrosObrigatorios(itemSolicitacaoExame);
		DetachedCriteria criteria = DetachedCriteria.forClass(AelInformacaoSolicitacaoUnidadeExecutora.class);
		//criteria.createAlias(AelInformacaoSolicitacaoUnidadeExecutora.Fields.ITEM_SOLICITACAOEXAME.toString(), "ISE");
		criteria.add(Restrictions.eq(AelInformacaoSolicitacaoUnidadeExecutora.Fields.ITEM_SOLICITACAOEXAME.toString(), itemSolicitacaoExame));
		return executeCriteria(criteria);
	}

}

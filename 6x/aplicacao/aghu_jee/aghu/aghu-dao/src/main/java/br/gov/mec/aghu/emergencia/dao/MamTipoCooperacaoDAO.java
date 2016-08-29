package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamTipoCooperacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamTipoCooperacao
 * 
 * @author luismoura
 * 
 */
public class MamTipoCooperacaoDAO extends BaseDao<MamTipoCooperacao> {
	private static final long serialVersionUID = -1503768956751868166L;

	public List<MamTipoCooperacao> pesquisarAtivos(String descricao) {
		final DetachedCriteria criteria = this.montarPesquisaAtivos();
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.eq(MamTipoCooperacao.Fields.DESCRICAO.toString(), descricao));
		}
		criteria.addOrder(Order.asc(MamTipoCooperacao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarPesquisaAtivos() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoCooperacao.class, "MamTipoCooperacao");
		criteria.add(Restrictions.eq(MamTipoCooperacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		return criteria;
	}
}

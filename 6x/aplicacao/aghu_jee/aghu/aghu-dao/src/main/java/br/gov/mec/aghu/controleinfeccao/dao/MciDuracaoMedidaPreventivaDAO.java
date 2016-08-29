package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;

public class MciDuracaoMedidaPreventivaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciDuracaoMedidaPreventiva> {

	private static final long serialVersionUID = 1080401234339923868L;

	private DetachedCriteria obterCriteriaDuracaoMedidaPreventivaPorSeqSituacao(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciDuracaoMedidaPreventiva.class);
		String strParametro = (String) parametro;
		if (StringUtils.isNotBlank(strParametro) && StringUtils.isNumeric(strParametro)) {
			criteria.add(Restrictions.eq(MciDuracaoMedidaPreventiva.Fields.SEQ.toString(), Short.valueOf(strParametro)));
		} else if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.ilike(MciDuracaoMedidaPreventiva.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MciDuracaoMedidaPreventiva.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<MciDuracaoMedidaPreventiva> pesquisarDuracaoMedidaPreventivaPatologiaInfeccao(Object parametro) {
		return this.executeCriteria(obterCriteriaDuracaoMedidaPreventivaPorSeqSituacao(parametro));
	}

	public Long pesquisarDuracaoMedidaPreventivaPatologiaInfeccaoCount(Object parametro) {
		return this.executeCriteriaCount(obterCriteriaDuracaoMedidaPreventivaPorSeqSituacao(parametro));
	}

}

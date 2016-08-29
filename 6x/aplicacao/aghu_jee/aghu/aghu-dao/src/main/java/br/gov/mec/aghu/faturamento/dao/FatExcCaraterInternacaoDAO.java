package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatExcCaraterInternacao;

public class FatExcCaraterInternacaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatExcCaraterInternacao> {

	private static final long serialVersionUID = 9007052504142156824L;

	public List<FatExcCaraterInternacao> listarExtCaraterInternacoes(
			Short phoSeq, Integer iphSeq, String uf) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatExcCaraterInternacao.class);

		criteria.add(Restrictions.eq(FatExcCaraterInternacao.Fields.IPH_PHO_SEQ
				.toString(), phoSeq));

		criteria.add(Restrictions.eq(FatExcCaraterInternacao.Fields.IPH_SEQ
				.toString(), iphSeq));

		criteria.add(Restrictions.eq(FatExcCaraterInternacao.Fields.UF_SIGLA
				.toString(), uf));

		return executeCriteria(criteria);
	}

}

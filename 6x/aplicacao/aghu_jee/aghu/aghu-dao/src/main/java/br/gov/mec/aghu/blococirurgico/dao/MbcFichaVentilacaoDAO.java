package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaVentilacao;

public class MbcFichaVentilacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaVentilacao> {

	private static final long serialVersionUID = 506388695726372025L;

	public List<MbcFichaVentilacao> pesquisarMbcFichaVentilacaoByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaVentilacao.class);
		criteria.createAlias(MbcFichaVentilacao.Fields.MBC_FICHA_ANESTESIAS.toString(), "fic");
		criteria.createAlias(MbcFichaVentilacao.Fields.MBC_VENTILACOES.toString(), "vet");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}
}
package br.gov.mec.aghu.internacao.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.AinObservacoesCenso;

public class AinObservacoesCensoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinObservacoesCenso> {

	private static final long serialVersionUID = -6853621534537104077L;

	@Override
	protected void obterValorSequencialId(AinObservacoesCenso elemento) {
		elemento.getId().setSeq(this.getNextVal(SequenceID.AIN_AOC_SQ1));
	}

	/**
	 * Obtem a observacaoCenso de uma internacao com data de criacao menor ou
	 * igual a data passada como parametro e também a partir do id da
	 * internacao.
	 * 
	 * @param intSeq
	 * @return AinObservacoesCenso
	 */
	public AinObservacoesCenso obterObservacaoDaInternacao(Integer intSeq, Date data) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinObservacoesCenso.class);

		cri.add(Restrictions.eq(AinObservacoesCenso.Fields.ID_INT_SEQ.toString(), intSeq));
		// FIXME: Não deveria buscar pelos 2 campos da chave?
		return (AinObservacoesCenso) this.executeCriteriaUniqueResult(cri);
	}

}

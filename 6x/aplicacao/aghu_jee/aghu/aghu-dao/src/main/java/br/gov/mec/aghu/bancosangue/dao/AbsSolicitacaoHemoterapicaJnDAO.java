package br.gov.mec.aghu.bancosangue.dao;

import br.gov.mec.aghu.model.AbsSolicitacaoHemoterapicaJn;

public class AbsSolicitacaoHemoterapicaJnDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsSolicitacaoHemoterapicaJn> {

	
	private static final long serialVersionUID = -7036466278752777122L;

	public void persistirSolicitacaoHemoterapicaJn(
			AbsSolicitacaoHemoterapicaJn solHemoterapica) {

		if (solHemoterapica != null && solHemoterapica.getSeqJn() == null) {
			this.persistir(solHemoterapica);
		}
	}

}

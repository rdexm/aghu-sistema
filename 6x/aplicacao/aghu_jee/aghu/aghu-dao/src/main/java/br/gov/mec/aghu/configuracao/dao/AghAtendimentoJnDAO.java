package br.gov.mec.aghu.configuracao.dao;

import br.gov.mec.aghu.model.AghAtendimentoJn;

/**
 * 
 * DAO para AghAtendimentoJn.
 * 
 * @see AghAtendimentoJn
 */
public class AghAtendimentoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghAtendimentoJn> {

	private static final long serialVersionUID = 6045859891145770182L;

	@Override
	protected void obterValorSequencialId(AghAtendimentoJn elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
	}

}
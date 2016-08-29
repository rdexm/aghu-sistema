package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface EstornarNotaRecebimentoBeanLocal {

	void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException;

}

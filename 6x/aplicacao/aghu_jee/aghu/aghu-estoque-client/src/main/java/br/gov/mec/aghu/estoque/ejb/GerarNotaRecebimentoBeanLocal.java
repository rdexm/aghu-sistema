package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface GerarNotaRecebimentoBeanLocal {

	void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException;
	
	void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException;

	void atualizarImpressaoNotaRecebimento(final Integer seq, String nomeMicrocomputador) throws BaseException;
}

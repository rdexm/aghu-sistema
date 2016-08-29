package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface GerarItemNotaRecebimentoBeanLocal {
	
	void gerarItemNotaRecebimento(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException;
	
	void gerarItemNotaRecebimentoSolicitacaoCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento, Short numeroItem, ScoMarcaComercial marcaComercial, Integer fatorConversao, String nomeMicrocomputador) throws BaseException;
	

}

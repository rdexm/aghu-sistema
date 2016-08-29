package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface GerarListaTransferenciaAutoAlmoxarifadoBeanLocal {

	void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;
	
	void removerTransferenciaAutoAlmoxarifado(Integer seqTransferencia) throws BaseException;
	
	void removerTransferenciaAutomaticaNaoEfetivadaAlmoxarifadoDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento, Long numeroClassifMatNiv5) throws BaseException;
	
	void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException;
	
	void removerItemTransferenciaAutoAlmoxarifado(final Integer ealSeq, final Integer trfSeq) throws BaseException;
	
}

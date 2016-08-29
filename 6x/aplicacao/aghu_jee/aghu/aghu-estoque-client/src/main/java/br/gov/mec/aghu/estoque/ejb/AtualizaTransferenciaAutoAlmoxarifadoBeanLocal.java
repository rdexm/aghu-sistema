package br.gov.mec.aghu.estoque.ejb;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface AtualizaTransferenciaAutoAlmoxarifadoBeanLocal {

	void atualizarItensTransfAutoAlmoxarifados(List<ItemTransferenciaAutomaticaVO>  listaItemTransferenciaAutomaticaVO, String nomeMicrocomputador)throws BaseException;
	
	void efetivarTransferenciaAutoAlmoxarifado(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException;

	void estornarTransferenciaAutoAlmoxarifado(Integer seqTransferencia, String nomeMicrocomputador) throws BaseException;

	void atualizarTransferenciaAutoAlmoxarifado(SceTransferencia  transferencia, String nomeMicrocomputador) throws BaseException;

}

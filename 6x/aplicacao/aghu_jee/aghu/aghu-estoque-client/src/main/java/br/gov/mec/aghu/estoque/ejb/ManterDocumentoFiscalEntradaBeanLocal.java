package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface ManterDocumentoFiscalEntradaBeanLocal {

	void persistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada)throws BaseException;

	void removerDocumentoFiscalEntrada(Integer seq)throws BaseException;
}

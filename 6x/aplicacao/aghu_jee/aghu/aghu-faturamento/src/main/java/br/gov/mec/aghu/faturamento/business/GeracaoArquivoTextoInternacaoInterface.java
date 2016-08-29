package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Local;

import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface GeracaoArquivoTextoInternacaoInterface {

	/**
	 * <p>
	 * ORADB: <code>FATF_ARQ_TXT_INT.EVT_WHEN_BUTTON_PRESSED</code> <br/>
	 * ORADB: <code>FATF_ARQ_TXT_INT.BUSCA_CONTA</code>
	 * </p>
	 * @return
	 * @throws MECBaseException 
	 */
	public abstract ArquivoURINomeQtdVO gerarArquivoTextoContasPeriodo()
			throws BaseException;

}

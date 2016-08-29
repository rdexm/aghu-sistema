package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.Local;

import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IMbcCirurgiasBean {
	

	AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(final boolean emEdicao, final boolean confirmaDigitacaoNS, CirurgiaTelaVO vo, 
			final String nomeMicrocomputador) throws BaseException;
	
	void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo, AlertaModalVO alertaVO,
			boolean isPressionouBotaoSimModal) throws BaseException;
	
}

package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class InternacaoCOON extends BaseBusiness {
	/**
	 * 
	 */
	private static final Log LOG = LogFactory.getLog(InternacaoCOON.class);
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8363189745789848256L;

	public void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo,
			Short gestacaoSeqp, String nivelContaminacao,
			Date dataInicioProcedimento, Short salaCirurgicaSeqp,
			Short tempoDuracaoProcedimento, Short anestesiaSeqp,
			Short equipeSeqp, String tipoNascimento) throws BaseException {
		
		this.internacaoFacade.inserirCirurgiaDoCentroObstetrico(pacCodigo, gestacaoSeqp, nivelContaminacao, dataInicioProcedimento, salaCirurgicaSeqp, tempoDuracaoProcedimento, anestesiaSeqp, equipeSeqp, tipoNascimento);
		
	}
}

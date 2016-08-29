package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.vo.FatLogErrorVO;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FatLogErrorON extends BaseBusiness {

	@EJB
	private FatLogErrorRN fatLogErrorRN;
	
	private static final Log LOG = LogFactory.getLog(FatLogErrorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private FatLogErrorDAO fatLogErrorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8918170274400062781L;

	/**
	 * Persiste um objeto logError
	 * 
	 * @param fle
	 */
	public void persistirLogError(FatLogError fle) {

		this.getLogErrorRN().executarAntesDeInserirLogErro(fle);

		getFatLogErrorDAO().persistir(fle);
//		getFatLogErrorDAO().flush();
	}

	public void persistirLogError(FatLogError fle, boolean flush) {

		this.getLogErrorRN().executarAntesDeInserirLogErro(fle);

		getFatLogErrorDAO().persistir(fle);
		if (flush){
			getFatLogErrorDAO().flush();
		}
	}

	protected FatLogErrorRN getLogErrorRN() {
		return fatLogErrorRN;
	}

	protected FatLogErrorDAO getFatLogErrorDAO() {
		return fatLogErrorDAO;
	}

	public List<FatLogError> pesquisarFatLogError( final Integer cthSeq, final Integer pacCodigo, final Short ichSeqp, final String erro,
											final Integer phiSeqItem1, final Long codItemSus1,
											final DominioModuloCompetencia modulo, final Integer firstResult,
											final Integer maxResult, final String orderProperty,
											final boolean asc) {

		return getFatLogErrorDAO().pesquisarFatLogError( cthSeq, pacCodigo, ichSeqp, erro, phiSeqItem1, 
											   		     codItemSus1, modulo, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarFatLogErrorCount( final Integer cthSeq,
									   final Integer pacCodigo, final Short ichSeqp, final String erro,
									   final Integer phiSeqItem1, final Long codItemSus1,
									   final DominioModuloCompetencia modulo) {
		return getFatLogErrorDAO().pesquisarFatLogErrorCount(cthSeq, pacCodigo,ichSeqp, erro, 
															 phiSeqItem1, codItemSus1, modulo);
	}

	public FatLogError obterFatLogError(final Integer seq) {
		return getFatLogErrorDAO().obterFatLogError(seq);
	}

	public FatLogErrorVO obterFatLogErrorVo(final Short iphPhoSeqItem1,
									 final Integer iphSeqItem1, final Short iphPhoSeqItem2,
									 final Integer iphSeqItem2, final Short iphPhoSeqRealizado,
									 final Integer iphSeqRealizado, final Short iphPhoSeq,
									 final Integer iphSeq) {

		return getFatLogErrorDAO().obterFatLogErrorVo( iphPhoSeqItem1, iphSeqItem1, iphPhoSeqItem2, iphSeqItem2, 
													   iphPhoSeqRealizado, iphSeqRealizado, iphPhoSeq, iphSeq);
	}
	
	public Integer removerPorCthModulo(final Integer cthSeq, final DominioModuloCompetencia modulo){
		Integer retorno = getFatLogErrorDAO().removerPorCthModulo(cthSeq, modulo);
		getFatLogErrorDAO().flush();
		return retorno;
	}
}
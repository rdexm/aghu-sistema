package br.gov.mec.aghu.paciente.prontuarioonline.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SumarioAdmissaoObstetricaExameFisicoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaExameFisicoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = -4695657119760263498L;

	/**
	 * CF_1_FORMULA
	 * @param vo
	 */
	public String executarCF1Formula(SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		String AVC = (String) vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_ACV);
		String AR  = (String) vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_AR);
		StringBuilder retorno = new StringBuilder();
	
		if(AVC != null && !AVC.trim().equals("")){
			retorno.append(AVC);
		}
		
		if(AR != null && !AR.trim().equals("")){
			if(AVC != null && !AVC.trim().equals("")){
				retorno.append("  ");
			}
			retorno.append(AR);
		}
		return retorno.toString();
	}
}

package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaExamesCondutaVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SumarioAdmissaoObstetricaCondutaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaCondutaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPerinatologiaFacade perinatologiaFacade;

	private static final long serialVersionUID = -3415479298231227428L;

	/**
	 * Acesso ao modulo perinatologia
	 * @return
	 */
	private IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	/**
	 * Q_CDT
	 * @param vo
	 */
	public void executarQCdt(SumarioAdmissaoObstetricaExamesCondutaVO vo) {		
		McoConduta conduta = getPerinatologiaFacade().obterMcoCondutaPorChavePrimaria(vo.getCdtSeq());
		vo.setDescricaoConduta(conduta.getDescricao());
		if (conduta.getCod() != null) {
			StringBuilder cod = new StringBuilder();
			cod.append('(').append(conduta.getCod()).append(')');
			vo.setCodigoConduta(cod.toString());
		}							
	}
	
	/**
	 * Q_PLI
	 * @param vo
	 */
	public void executarQPli(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_GSO_PAC_CODIGO);
		Short gsoSeqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_GSO_SEQP);
		Integer conNumero = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_BA);
		
		List<McoPlanoIniciais> pli = getPerinatologiaFacade().listarPlanosIniciaisPorPacienteSequenceNumeroConsulta(pacCodigo, gsoSeqp, conNumero);
			
		for(McoPlanoIniciais planoInicial: pli){
			SumarioAdmissaoObstetricaExamesCondutaVO condutaVO = new SumarioAdmissaoObstetricaExamesCondutaVO();
			condutaVO.setCdtSeq(planoInicial.getId().getCdtSeq());
			condutaVO.setComplementoConduta(planoInicial.getComplemento());
			executarQCdt(condutaVO);
			vo.getCondutas().add(condutaVO);
		}
		
	}

}

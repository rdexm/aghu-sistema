package br.gov.mec.aghu.paciente.prontuarioonline.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SumarioAdmissaoObstetricaDiagPrincipalRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaDiagPrincipalRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 6349680710351428778L;


	/**
	 * Q_CID
	 * @param vo 
	 */
	public void executarQCid(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer cidSeq = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_EFI_CID_SEQ);
		AghCid cid = getAghuFacade().obterAghCidPorSeq(cidSeq);
		if(cid != null){
			StringBuilder cidCodigo = new StringBuilder();
			StringBuilder cidDescricao = new StringBuilder();
			cidCodigo.append('(').append(cid.getCodigo()).append(')');
			cidDescricao.append(cid.getDescricao()).append(' ');
			
			vo.setMotivoInternacao(cidDescricao.toString());
			vo.setCidInternacao(cidCodigo.toString());
		}

	}
	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
}

package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class BotaoDescricaoListaProcedimentosRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(BotaoDescricaoListaProcedimentosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private static final long serialVersionUID = 7980068113224803320L;

	/**
	 * História 16638 RN2
	 * @ORADB Procedure P_IMPRIME_DESCRICAO 
	 * @author Cristiano Alexandre Moretti
	 *  
	 * @since 11/05/2012
	 */
	public Object[] pImprimeDescricao(Integer seqCirurgia) throws ApplicationBusinessException {
		List<MbcDescricaoCirurgica> descricaoCirurgica = getBlocoCirurgicoFacade().listarDescricaoCirurgicaPorSeqCirurgia(seqCirurgia);
		Object[] relatorioParametros = new Object[3];
		relatorioParametros[0] = "MBCR_DESCR_CIRURGICA";
		relatorioParametros[1] = getProntuarioOnlineFacade().processaDescricaoComNomeResponsavelMbc(descricaoCirurgica);
		return relatorioParametros; 
	}
	
	/**
	 * História 16638 RN3
	 * @ORADB Procedure P_IMPRIME_DESCR_PDT
	 * @author Cristiano Alexandre Moretti
	 *  
	 * @since 11/05/2012
	 */
	public Object[] pImprimeDescrPdt(Integer seqCirurgia) throws ApplicationBusinessException {
		List<PdtDescricao> descricao = getBlocoCirurgicoProcDiagTerapFacade().listarDescricaoPorSeqCirurgia(seqCirurgia);
		Object[] relatorioParametros = new Object[3];
		relatorioParametros[0] = "PDTR_DESCRICAO"; 
		relatorioParametros[1] = getProntuarioOnlineFacade().processaDescricaoComNomeResponsavel(descricao); 
		return relatorioParametros; 
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return this.prontuarioOnlineFacade;
	}

	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return blocoCirurgicoProcDiagTerapFacade;
	}	
}

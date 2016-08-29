package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class BotaoDescricaoListaProcedimentosON extends BaseBusiness {

	
	@EJB
	private BotaoDescricaoListaProcedimentosRN botaoDescricaoListaProcedimentosRN;
	
	private static final Log LOG = LogFactory.getLog(BotaoDescricaoListaProcedimentosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private static final long serialVersionUID = -5015404727784321351L;
	
	public Object[] getRelatorio(Integer seqCirurgia) throws ApplicationBusinessException {
		Object[] relatorioParametros = new Object[3];
		List<PdtDescricao> listaPdt = getBlocoCirurgicoProcDiagTerapFacade().listarDescricaoPorSeqCirurgia(seqCirurgia);
		if (listaPdt != null && !listaPdt.isEmpty()) {
			relatorioParametros = getBotaoDescricaoRN().pImprimeDescrPdt(seqCirurgia); 
		} else {
			List<MbcDescricaoCirurgica> desc = getBlocoCirurgicoFacade().listarDescricaoCirurgicaPorSeqCirurgia(seqCirurgia);
			if ( desc!= null && !desc.isEmpty())  {
				relatorioParametros = getBotaoDescricaoRN().pImprimeDescricao(seqCirurgia);
			} else {
				relatorioParametros = null; 
			}
		}
		return relatorioParametros; 
	}

	private BotaoDescricaoListaProcedimentosRN getBotaoDescricaoRN() {
		return botaoDescricaoListaProcedimentosRN; 
	}
	
	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return blocoCirurgicoProcDiagTerapFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
}

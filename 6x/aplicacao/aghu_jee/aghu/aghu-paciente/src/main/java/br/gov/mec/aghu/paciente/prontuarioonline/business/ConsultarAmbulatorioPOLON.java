package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ConsultarAmbulatorioPOLON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConsultarAmbulatorioPOLON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private static final long serialVersionUID = 6759508234178326710L;

	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer numeroProntuario, String atendimentos) {
		List<AghAtendimentos> atds = getAghuFacade().pesquisarAtendimentoAmbPorPacCodigo(firstResult, maxResult, orderProperty, asc, numeroProntuario, atendimentos);
		for(AghAtendimentos atd : atds){
			atd.setControleAtd	(getBlocoCirurgicoFacade().obterMbcFichaAnestesiaByConsulta(atd.getConsulta().getNumero()) != null);
			
			atd.setMamAltasSumarios(getMamAltaSumariosByRelatorioAltaAmbulatorial(atd));
		}
		return atds;
	}

	private List<MamAltaSumario> getMamAltaSumariosByRelatorioAltaAmbulatorial(
			AghAtendimentos atd) {
		
		List<MamAltaSumario> lista = getAmbulatorioFacade().pesquisarMamAltaSumarioDtValidaNullByConsulta(
				atd.getConsulta().getNumero(), 
				DominioIndPendenteDiagnosticos.A,
				DominioIndPendenteDiagnosticos.E,
				DominioIndPendenteDiagnosticos.V);
		
		if(lista != null && !lista.isEmpty()){
			return lista;
		}else{
			return getAmbulatorioFacade().pesquisarMamAltaSumarioDtValidaNullAndAlsSeqNull(
					atd.getConsulta().getNumero(), 
					DominioIndPendenteDiagnosticos.P);
		}
	}

	public Long pesquisarAtendimentoAmbPorPacCodigoCount(
			Integer numeroProntuario, String atendimentos) {
		return getAghuFacade().pesquisarAtendimentoAmbPorPacCodigoCount(numeroProntuario, atendimentos);
	}
	
	private IAghuFacade getAghuFacade(){
		return aghuFacade; 
	}
	
	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return blocoCirurgicoFacade; 
	}
	
	private IAmbulatorioFacade getAmbulatorioFacade(){
		return ambulatorioFacade; 
	}

}

package br.gov.mec.aghu.emergencia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.service.IControlePacienteService;
import br.gov.mec.aghu.controlepaciente.vo.EcpItemControleVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteServiceVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceException;
/**
 * @author fpalma
 * 
 */
@Stateless
public class ControlesPacientesON extends BaseBusiness {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1893295908188331480L;
	
	@EJB
	private IControlePacienteService controlePacienteService;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<EcpItemControleVO> buscarItensControlePorPacientePeriodo(
			Integer pacCodigo, Long trgSeq) {
		return this.controlePacienteService.buscarItensControlePorPacientePeriodo(pacCodigo, trgSeq);
	}
	
	public List<RegistroControlePacienteServiceVO> pesquisarRegistrosPaciente(
			Integer pacCodigo, List<EcpItemControleVO> listaItensControle, Long trgSeq) throws ApplicationBusinessException {
		return this.controlePacienteService.pesquisarRegistrosPaciente(pacCodigo, listaItensControle, trgSeq);
	}
	
	public boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) throws ServiceException {
		return this.controlePacienteService.verificarExisteSinalVitalPorPaciente(pacCodigo);
	}
	
	public void excluirRegistroControlePaciente(Long seqHorarioControle) throws ApplicationBusinessException {
		//this.controlePacienteService.excluirRegistroControlePaciente(seqHorarioControle);
		this.controlePacienteFacade.excluirRegistroControlePaciente(seqHorarioControle);
	}
	
	public Boolean validaUnidadeFuncionalInformatizada(Short unfSeq) throws ApplicationBusinessException {
		return this.controlePacienteService.validaUnidadeFuncionalInformatizada(unfSeq);
	}

}

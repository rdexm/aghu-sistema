package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AlterarDispensacaoDeMedicamentosON extends BaseBusiness implements Serializable {

private static final Log LOG = LogFactory.getLog(AlterarDispensacaoDeMedicamentosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -146086057063741780L;

	public List<MpmPrescricaoMedica> pesquisarAlterarDispensacaoDeMedicamentos(
			Integer firstResult, Integer maxResult, String orderProperty,
			Boolean asc, AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente) {
		
		if(paciente != null && paciente.getCodigo() == null) {
			paciente = null;
		}
		
		return getPrescricaoMedicaFacade()
				.pesquisarAlterarDispensacaoDeMedicamentos(firstResult,
						maxResult, orderProperty, asc, leito, numeroPrescricao,
						dthrDataInicioValidade, dthrDataFimValidade,
						numeroProntuario, paciente);
	}

	public Long pesquisarAlterarDispensacaoDeMedicamentosCount(
			AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente) {
		
		if(paciente != null && paciente.getCodigo() == null) {
			paciente = null;
		}
		
		return getPrescricaoMedicaFacade().pesquisarAlterarDispensacaoDeMedicamentosCount(leito, numeroPrescricao,
				dthrDataInicioValidade, dthrDataFimValidade, numeroProntuario, paciente);
	}
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
}

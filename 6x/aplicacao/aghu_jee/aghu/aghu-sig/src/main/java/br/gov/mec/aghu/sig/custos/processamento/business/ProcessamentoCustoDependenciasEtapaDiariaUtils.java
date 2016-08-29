package br.gov.mec.aghu.sig.custos.processamento.business;

import javax.ejb.EJB;

import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICP;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPConsultorias;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPCuidadosEnfermagem;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPDietas;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPExames;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPHemoterapias;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPMedicamentos;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPNutricaoParenteral;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPOrtesesProteses;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPPermanencia;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICPProcedimentosEspeciais;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemICProdCirurgicos;
import br.gov.mec.aghu.sig.custos.processamento.diario.business.ProcessamentoDiarioContagemPacientes;

public class ProcessamentoCustoDependenciasEtapaDiariaUtils extends ProcessamentoCustoDependenciasDAOUtils {

	@EJB
	private ProcessamentoDiarioContagemPacientes processamentoDiarioContagemPacientesRN;
	
	@EJB
	private ProcessamentoDiarioContagemICPNutricaoParenteral processamentoDiarioContagemICPNutricaoParenteralON;
	
	@EJB
	private ProcessamentoDiarioContagemICPHemoterapias processamentoDiarioContagemICPHemoterapiasON;

	@EJB
	private ProcessamentoDiarioContagemICPProcedimentosEspeciais processamentoDiarioContagemICPProcedimentosEspeciaisON;

	@EJB
	private ProcessamentoDiarioContagemICPConsultorias processamentoDiarioContagemICPConsultoriasON;

	@EJB
	private ProcessamentoDiarioContagemICProdCirurgicos processamentoDiarioContagemICProdCirurgicosRN;

	@EJB
	private ProcessamentoDiarioContagemICPCuidadosEnfermagem processamentoDiarioContagemICPCuidadosEnfermagemRN;

	@EJB
	private ProcessamentoDiarioContagemICPPermanencia processamentoDiarioContagemICPPermanenciaRN;

	@EJB
	private ProcessamentoDiarioContagemICPMedicamentos processamentoDiarioContagemICPMedicamentosRN;

	@EJB
	private ProcessamentoDiarioContagemICPExames processamentoDiarioContagemICPExamesRN;

	@EJB
	private ProcessamentoDiarioContagemICP processamentoDiarioContagemICPRN;

	@EJB
	private ProcessamentoDiarioContagemICPOrtesesProteses processamentoDiarioContagemICPOrtesesProteses;

	@EJB
	private ProcessamentoDiarioContagemICPDietas processamentoDiarioContagemICPDietas;
	
	public ProcessamentoDiarioContagemICPExames getProcessamentoDiarioContagemICPExamesRN() {
		return processamentoDiarioContagemICPExamesRN;
	}

	public ProcessamentoDiarioContagemICPMedicamentos getProcessamentoDiarioContagemICPMedicamentosRN() {
		return processamentoDiarioContagemICPMedicamentosRN;
	}

	public ProcessamentoDiarioContagemICPPermanencia getProcessamentoDiarioContagemICPPermanenciaRN() {
		return processamentoDiarioContagemICPPermanenciaRN;
	}

	public ProcessamentoDiarioContagemICPCuidadosEnfermagem getProcessamentoDiarioContagemICPCuidadosEnfermagemRN() {
		return processamentoDiarioContagemICPCuidadosEnfermagemRN;
	}

	public ProcessamentoDiarioContagemICProdCirurgicos getProcessamentoDiarioContagemICProdCirurgicosRN() {
		return processamentoDiarioContagemICProdCirurgicosRN;
	}

	public ProcessamentoDiarioContagemPacientes getProcessamentoDiarioContagemPacientesRN() {
		return processamentoDiarioContagemPacientesRN;
	}

	public ProcessamentoDiarioContagemICPConsultorias getProcessamentoDiarioContagemICPConsultoriasON() {
		return processamentoDiarioContagemICPConsultoriasON;
	}

	public ProcessamentoDiarioContagemICPProcedimentosEspeciais getProcessamentoDiarioContagemICPProcedimentosEspeciaisON() {
		return processamentoDiarioContagemICPProcedimentosEspeciaisON;
	}
	
	public ProcessamentoDiarioContagemICPHemoterapias getProcessamentoDiarioContagemICPHemoterapiasON(){
		return processamentoDiarioContagemICPHemoterapiasON;			
	}
	
	public ProcessamentoDiarioContagemICPNutricaoParenteral getProcessamentoDiarioContagemICPNutricaoParenteralON(){
		return processamentoDiarioContagemICPNutricaoParenteralON;
	}
	
	public ProcessamentoDiarioContagemICPOrtesesProteses getProcessamentoDiarioContagemICPOrtesesProteses(){
		return processamentoDiarioContagemICPOrtesesProteses;
	}
	
	public ProcessamentoDiarioContagemICPDietas getProcessamentoDiarioContagemICPDietas(){
		return processamentoDiarioContagemICPDietas;
	}
	
	public ProcessamentoDiarioContagemICP getProcessamentoDiarioContagemICPRN(){
		return processamentoDiarioContagemICPRN;
	}
}

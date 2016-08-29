package br.gov.mec.aghu.sig.custos.processamento.business;

import javax.ejb.EJB;

import br.gov.mec.aghu.sig.custos.business.IntegracaoCentralPendenciasON;
import br.gov.mec.aghu.sig.custos.processamento.mensal.business.*;

public class ProcessamentoCustoDependenciasEtapaUtils extends ProcessamentoCustoDependenciasEtapaDiariaUtils {

	@EJB
	private IntegracaoCentralPendenciasON integracaoCentralPendenciasON;
	
	@EJB
	private ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN processamentoMensalContagemRN;
	
	@EJB
	private ProcessamentoCustosMensalIndiretoClienteRN processamentoMensalIndiretoClienteON;
	
	@EJB
	private ProcessamentoCustosMensalSumarizarProdAssistencialRN processamentoMensalSumarizarProdAssistencialON;

	@EJB
	private ProcessamentoCustosMensalContabilizacaoConsumoInsumoCCRN processamentoMensalContabilizacaoConsumoInsumoCCON;

	@EJB
	private ProcessamentoCustosMensalContabilizacaoDebitoParcelaMaterialRN processamentoMensalContabilizacaoDebitoParcelaMaterialON;

	@EJB
	private ProcessamentoCustosMensalContabilizacaoDepreciacaoEquipamentoRN processamentoMensalContabilizacaoDepreciacaoEquipamentoON;

	@EJB
	private ProcessamentoCustosMensalContabilizacaoFolhaPagamentoPessoalRN processamentoMensalContabilizacaoFolhaPagamentoPessoalON;

	@EJB
	private ProcessamentoCustosMensalContabilizacaoContratoServicoCCRN processamentoMensalContabilizacaoContratoServicoCCON;

	@EJB
	private ProcessamentoCustosMensalContabilizacaoProducaoExameRN processamentoMensalContabilizacaoProducaoExameON;

	@EJB
	private ProcessamentoCustosMensalCriacaoObjetoCustoProducaoRN processamentoMensalCriacaoObjetoCustoProducaoON;

	@EJB
	private ProcessamentoCustosMensalCustoInsumoRN processamentoMensalCustoInsumoON;

	@EJB
	private ProcessamentoCustosMensalCustoEquipamentoRN processamentoMensalCustoEquipamentoON;

	@EJB
	private ProcessamentoCustosMensalCustoServicoRN processamentoMensalCustoServicoON;

	@EJB
	private ProcessamentoCustosMensalCustoPessoalRN processamentoMensalCustoPessoalON;

	@EJB
	private ProcessamentoCustosMensalCargaObjetoCustoApoioRN processamentoMensalCargaObjetoCustoApoioON;

	@EJB
	private ProcessamentoCustosMensalRateioDiretoEquipamentoRN processamentoMensalRateioDiretoEquipamentoON;

	@EJB
	private ProcessamentoCustosMensalRateioDiretoPessoalRN processamentoMensalRateioDiretoPessoalON;

	@EJB
	private ProcessamentoCustosMensalRateioDiretoServicoRN processamentoMensalRateioDiretoServicoON;
	
	@EJB
	private ProcessamentoCustosMensalRateioDiretoInsumoRN processamentoMensalRateioDiretoInsumoON;

	@EJB
	private ProcessamentoCustosMensalIndiretoObjetoCustoRN processamentoMensalIndiretoObjetoCustoON;

	@EJB
	private ProcessamentoCustosMensalSumarizarCustoMedioRN processamentoMensalSumarizarCustoMedioON;
	
	@EJB
	private ProcessamentoCustosMensalAlocarOCPacienteRN processamentoMensalAlocarOCPacienteON;
	
	@EJB
	private ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN processamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN;
	
	@EJB
	private ProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN processamentoCustosMensalContagemCuidadosQuimioterapiaRN;
	
	@EJB
	private ProcessamentoCustosMensalContagemDialisePacienteRN processamentoCustosMensalContagemDialisePacienteRN;
	
	@EJB
	private ProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN processamentoCustosMensalContagemMedicamentosDTAPRN;
	
	@EJB
	private ProcessamentoCustosMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPacienteRN processamentoCustosMensalContagemProcedimentoMDTAPRN;

	@EJB
	private ProcessamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN processamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN;
	
	@EJB
	private ProcessamentoCustosMensalCalculoOrtesesEProtesesRN processamentoCustosMensalCalculoOrtesesEProtesesRN;

    @EJB
    private ProcessamentoCustosMensalCalculoMedicamentosDialiseRN processamentoCustosMensalCalculoMedicamentosDialiseRN;
	
	@EJB
	private ProcessamentoCustosMensalAssociarPacienteReceitaPHIRN processamentoCustosMensalAssociarPacienteReceitaPHIRN;

    @EJB
    private ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN processamentoCustosMensalCustoProcedimentosMaterialDialiseRN;

    @EJB
    private ProcessamentoCustosMensalCalculoMedicamentosRN processamentoCustosMensalCalculoMedicamentosRN;
    
    @EJB
    private ProcessamentoCustosMensalReceitaSusCustosPacienteRN processamentoCustosMensalReceitaSusCustosPacienteRN;
	
	
	public ProcessamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN getProcessamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN() {
		return processamentoCustosMensalCalculoCustoNptQuimioCustoVariavelInsumosRN;
	}

	public ProcessamentoCustosMensalCalculoOrtesesEProtesesRN getProcessamentoCustosMensalCalculoOrtesesEProtesesRN() {
		return processamentoCustosMensalCalculoOrtesesEProtesesRN;
	}

    public ProcessamentoCustosMensalCalculoMedicamentosDialiseRN getProcessamentoCustosMensalCalculoMedicamentosDialiseRN() {
        return processamentoCustosMensalCalculoMedicamentosDialiseRN;
    }
	
	public ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN getProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN() {
		return processamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN;
	}

	public ProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN getProcessamentoCustosMensalContagemCuidadosQuimioterapiaRN() {
		return processamentoCustosMensalContagemCuidadosQuimioterapiaRN;
	}

	public ProcessamentoCustosMensalContagemDialisePacienteRN getProcessamentoCustosMensalContagemDialisePacienteRN() {
		return processamentoCustosMensalContagemDialisePacienteRN;
	}

	public ProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN getProcessamentoCustosMensalContagemMedicamentosDialiseTipoAtendimentoPacienteRN() {
		return processamentoCustosMensalContagemMedicamentosDTAPRN;
	}

	public ProcessamentoCustosMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPacienteRN getProcessamentoCustosMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPacienteRN() {
		return processamentoCustosMensalContagemProcedimentoMDTAPRN;
	}

	public ProcessamentoCustosMensalSumarizarProdAssistencialRN getProcessamentoMensalSumarizarProdAssistencialON() {
		return processamentoMensalSumarizarProdAssistencialON;
	}

	public ProcessamentoCustosMensalContabilizacaoConsumoInsumoCCRN getProcessamentoMensalContabilizacaoConsumoInsumoCCON() {
		return processamentoMensalContabilizacaoConsumoInsumoCCON;
	}

	public ProcessamentoCustosMensalContabilizacaoDebitoParcelaMaterialRN getProcessamentoMensalContabilizacaoDebitoParcelaMaterialON() {
		return processamentoMensalContabilizacaoDebitoParcelaMaterialON;
	}

	public ProcessamentoCustosMensalContabilizacaoDepreciacaoEquipamentoRN getProcessamentoMensalContabilizacaoDepreciacaoEquipamentoON() {
		return processamentoMensalContabilizacaoDepreciacaoEquipamentoON;
	}

	public ProcessamentoCustosMensalContabilizacaoFolhaPagamentoPessoalRN getProcessamentoMensalContabilizacaoFolhaPagamentoPessoalON() {
		return processamentoMensalContabilizacaoFolhaPagamentoPessoalON;
	}

	public ProcessamentoCustosMensalContabilizacaoContratoServicoCCRN getProcessamentoMensalContabilizacaoContratoServicoCCON() {
		return processamentoMensalContabilizacaoContratoServicoCCON;
	}

	public ProcessamentoCustosMensalContabilizacaoProducaoExameRN getProcessamentoMensalContabilizacaoProducaoExameON() {
		return processamentoMensalContabilizacaoProducaoExameON;
	}

	public ProcessamentoCustosMensalCriacaoObjetoCustoProducaoRN getProcessamentoMensalCriacaoObjetoCustoProducaoON() {
		return processamentoMensalCriacaoObjetoCustoProducaoON;
	}

	public ProcessamentoCustosMensalCustoInsumoRN getProcessamentoMensalCustoInsumoON() {
		return processamentoMensalCustoInsumoON;
	}

	public ProcessamentoCustosMensalCustoEquipamentoRN getProcessamentoMensalCustoEquipamentoON() {
		return processamentoMensalCustoEquipamentoON;
	}

	public ProcessamentoCustosMensalCustoServicoRN getProcessamentoMensalCustoServicoON() {
		return processamentoMensalCustoServicoON;
	}

	public ProcessamentoCustosMensalCustoPessoalRN getProcessamentoMensalCustoPessoalON() {
		return processamentoMensalCustoPessoalON;
	}

	public ProcessamentoCustosMensalCargaObjetoCustoApoioRN getProcessamentoMensalCargaObjetoCustoApoioON() {
		return processamentoMensalCargaObjetoCustoApoioON;
	}

	public ProcessamentoCustosMensalRateioDiretoEquipamentoRN getProcessamentoMensalRateioDiretoEquipamentoON() {
		return processamentoMensalRateioDiretoEquipamentoON;
	}

	public ProcessamentoCustosMensalRateioDiretoPessoalRN getProcessamentoMensalRateioDiretoPessoalON() {
		return processamentoMensalRateioDiretoPessoalON;
	}

	public ProcessamentoCustosMensalRateioDiretoServicoRN getProcessamentoMensalRateioDiretoServicoON() {
		return processamentoMensalRateioDiretoServicoON;
	}

	public ProcessamentoCustosMensalRateioDiretoInsumoRN getProcessamentoMensalRateioDiretoInsumoON() {
		return processamentoMensalRateioDiretoInsumoON;
	}

	public ProcessamentoCustosMensalIndiretoObjetoCustoRN getProcessamentoMensalIndiretoObjetoCustoON() {
		return processamentoMensalIndiretoObjetoCustoON;
	}

	public ProcessamentoCustosMensalSumarizarCustoMedioRN getProcessamentoMensalSumarizarCustoMedioON() {
		return processamentoMensalSumarizarCustoMedioON;
	}

	public ProcessamentoCustosMensalAlocarOCPacienteRN getProcessamentoMensalAlocarOCPacienteON() {
		return processamentoMensalAlocarOCPacienteON;
	}

	public IntegracaoCentralPendenciasON getIntegracaoCentralPendenciasON() {
		return integracaoCentralPendenciasON;
	}

	public ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN getProcessamentoMensalContagemRN(){
		return processamentoMensalContagemRN;
	}	
	
	public ProcessamentoCustosMensalIndiretoClienteRN getProcessamentoMensalIndiretoClienteON() {
		return processamentoMensalIndiretoClienteON;
	}
	
	public ProcessamentoCustosMensalAssociarPacienteReceitaPHIRN getProcessamentoCustosMensalAssociarPacienteReceitaPHIRN() {
		return processamentoCustosMensalAssociarPacienteReceitaPHIRN;
	}

    public ProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN getProcessamentoCustosMensalCustoProcedimentosMaterialDialiseRN() {
        return processamentoCustosMensalCustoProcedimentosMaterialDialiseRN;
    }

    public ProcessamentoCustosMensalCalculoMedicamentosRN getProcessamentoCustosMensalCalculoMedicamentosRN() {
        return processamentoCustosMensalCalculoMedicamentosRN;
    }

	public ProcessamentoCustosMensalReceitaSusCustosPacienteRN getProcessamentoCustosMensalReceitaSusCustosPacienteRN() {
		return processamentoCustosMensalReceitaSusCustosPacienteRN;
	}

}

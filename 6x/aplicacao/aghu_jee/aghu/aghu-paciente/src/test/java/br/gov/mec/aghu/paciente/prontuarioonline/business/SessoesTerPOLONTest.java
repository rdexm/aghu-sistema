package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioItensPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioSessoesQuimioVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SessoesTerPOLONTest extends AGHUBaseUnitTest<SessoesTerapeuticasPOLON>{

	@Mock
	private IProcedimentoTerapeuticoFacade mockedProcedimentoTerapeuticoFacade;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IProntuarioOnlineFacade mockedProntuarioOnlineFacade;
	@Mock
	private SessoesTerapeuticasPOLRN mockedSessoesTerapeuticasPOLRN;
	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */
	
	@Test
	public void montarQuinzenaSessoesQuimioTest() throws ApplicationBusinessException {
		esperarProcedimentoTerapeuticoFacadeObterPrescricaoPacienteporTrpSeq();
		esperarProcedimentoTerapeuticoFacadeListarPeriodosSessoesQuimio();
		esperarPacienteFacadeGerarAtendimentoPaciente();
		esperarAghuFacadeListarDadosCabecalhoSumarioPrescricaoQuimio();
		esperarAmbulatorioFacadeMpmcMinusculo();
		esperarPrescricaoMedicaFacadeObterDataInternacao();
		esperarProntuarioOnlineFacadeObterLocalizacaoPacienteParaRelatorio();
		esperarSessoesTerapeuticasPOLRNObterUnidadeAtendimentoPaciente();
		esperarProcedimentoTerapeuticoFacadeListarDadosPeriodosSumarioPrescricaoQuimio();
		esperarProcedimentoTerapeuticoFacadeListarTituloProtocoloAssistencial();
		
		List<SumarioSessoesQuimioVO> retorno = systemUnderTest.montarQuinzenaSessoesQuimio(1);
		Assert.assertEquals(retorno.size(), 2);
	}
	
	private void esperarProcedimentoTerapeuticoFacadeListarPeriodosSessoesQuimio() {
		List<MptPrescricaoPaciente> list = new ArrayList<MptPrescricaoPaciente>();
		for (int i = 0; i < 16; i++) {
			MptPrescricaoPaciente item = new MptPrescricaoPaciente();
			list.add(item);	
		}
		Mockito.when(mockedProcedimentoTerapeuticoFacade.listarPeriodosSessoesQuimio(Mockito.anyInt())).thenReturn(list);
	}
	
	private void esperarProcedimentoTerapeuticoFacadeObterPrescricaoPacienteporTrpSeq() {
		Mockito.when(mockedProcedimentoTerapeuticoFacade.obterPrescricaoPacienteporTrpSeq(Mockito.anyInt())).thenReturn(1);
	}
	
	private void esperarPacienteFacadeGerarAtendimentoPaciente() throws ApplicationBusinessException {
		Mockito.when(mockedPacienteFacade.gerarAtendimentoPaciente(Mockito.anyInt())).thenReturn(1);
	}
	
	private void esperarAghuFacadeListarDadosCabecalhoSumarioPrescricaoQuimio() {
		SumarioQuimioPOLVO retorno = new SumarioQuimioPOLVO();
		retorno.setPacProntuario(9999);
		Mockito.when(mockedAghuFacade.listarDadosCabecalhoSumarioPrescricaoQuimio(Mockito.anyInt(), Mockito.anyInt())).thenReturn(retorno);
	}
	
	private void esperarAmbulatorioFacadeMpmcMinusculo() {
		Mockito.when(mockedAmbulatorioFacade.mpmcMinusculo(Mockito.anyString(), Mockito.anyInt())).thenReturn("");
	}
	
	private void esperarPrescricaoMedicaFacadeObterDataInternacao() throws ApplicationBusinessException {
		Mockito.when(mockedPrescricaoMedicaFacade.obterDataInternacao(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(new Date());
	}
	
	private void esperarProntuarioOnlineFacadeObterLocalizacaoPacienteParaRelatorio() {
		Mockito.when(mockedProntuarioOnlineFacade.obterLocalizacaoPacienteParaRelatorio(Mockito.anyString(), Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn("");
	}
	
	private void esperarSessoesTerapeuticasPOLRNObterUnidadeAtendimentoPaciente() throws ApplicationBusinessException {
		Mockito.when(mockedSessoesTerapeuticasPOLRN.obterUnidadeAtendimentoPaciente(Mockito.anyInt()))
		.thenReturn("");
	}
	
	private void esperarProcedimentoTerapeuticoFacadeListarDadosPeriodosSumarioPrescricaoQuimio() {
		Mockito.when(mockedProcedimentoTerapeuticoFacade.listarDadosPeriodosSumarioPrescricaoQuimio(Mockito.any(Date.class), Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyInt()))
		.thenReturn(new ArrayList<SumarioQuimioItensPOLVO>());
	}
	
	private void esperarProcedimentoTerapeuticoFacadeListarTituloProtocoloAssistencial() {
		Mockito.when(mockedProcedimentoTerapeuticoFacade.listarTituloProtocoloAssistencial(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
		.thenReturn(new ArrayList<String>());
	}
}

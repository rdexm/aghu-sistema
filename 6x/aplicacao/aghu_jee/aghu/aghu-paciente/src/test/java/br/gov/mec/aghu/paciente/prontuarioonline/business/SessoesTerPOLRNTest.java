package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SessoesTerPOLRNTest extends AGHUBaseUnitTest<SessoesTerapeuticasPOLRN>{
	
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private IProcedimentoTerapeuticoFacade mockedProcedimentoTerapeuticoFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */
	
	@Test
	public void obterOrdemSumarioTest(){
		esperarFarmaciaFacadelistarOrdemSumarioPrecricao();
		Short retorno = systemUnderTest.obterOrdemSumario(1);
		Assert.assertNotNull(retorno);
	}
	
	@Test
	public void obterUnidadeAtendimentoPacienteTest() throws ApplicationBusinessException {
		esperarAghuFacadeObterAtendimentoComUnidadeFuncional();
		esperarAghuFacadeObterAtendimentoPorPacienteEOrigem();
		esperarPrescricaoMedicaFacadeBuscarResumoLocalPaciente();
		esperarAghuFacadeVerificarCaracteristicaDaUnidadeFuncional();
		String retorno = systemUnderTest.obterUnidadeAtendimentoPaciente(1);
		Assert.assertNotNull(retorno);
	}
	
	private void esperarFarmaciaFacadelistarOrdemSumarioPrecricao() {
		List<Short> list = new ArrayList<Short>();
		list.add(Short.valueOf("10"));

		Mockito.when(mockedFarmaciaFacade.listarOrdemSumarioPrecricao(Mockito.anyInt())).thenReturn(list);
	}
	
	private void esperarAghuFacadeObterAtendimentoComUnidadeFuncional() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setUnidadeFuncional(new AghUnidadesFuncionais());
		atendimento.setPaciente(new AipPacientes());

		Mockito.when(mockedAghuFacade.obterAtendimentoComUnidadeFuncional(Mockito.anyInt())).thenReturn(atendimento);
	}
	
	private void esperarAghuFacadeObterAtendimentoPorPacienteEOrigem() {
		AghAtendimentos atendimento = new AghAtendimentos();
		Mockito.when(mockedAghuFacade.obterAtendimentoPorPacienteEOrigem(Mockito.anyInt())).thenReturn(atendimento);
	}
	
	private void esperarPrescricaoMedicaFacadeBuscarResumoLocalPaciente() throws ApplicationBusinessException {
		Mockito.when(mockedPrescricaoMedicaFacade.buscarResumoLocalPaciente(new AghAtendimentos())).thenReturn("");
	}
	
	private void esperarAghuFacadeVerificarCaracteristicaDaUnidadeFuncional(){
		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);
	}
}

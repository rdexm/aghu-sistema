package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.prescricaomedica.business.ManterPrescricaoMedicaRN.LocalAtendimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Testes basicos para os metodos de verificacao da classe ManterPrescricaoMedicaRN. <br>
 * 
 * @author gfmenezes
 *
 */
@RunWith(PowerMockRunner.class)
public class ManterPrescricaoMedicaRNTest extends AGHUBaseUnitTest<ManterPrescricaoMedicaRN>{
	
	@Mock
	private MessagesUtils messagesUtils;
	@Mock
	private AghAtendimentoDAO mockedAghAtendimentoDAO;
	
	/**
	 * Nao encontra nenhum local de antendimento, 
	 * lancando a excecao ApplicationBusinessException.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void testarBuscarResumoLocalPaciente001() throws ApplicationBusinessException {
		final  AghAtendimentos aghAtendimentos = new AghAtendimentos();
		
		Mockito.when(mockedAghAtendimentoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aghAtendimentos);
		systemUnderTest.buscarResumoLocalPaciente(aghAtendimentos);
		Assert.fail();
	}
	
	/**
	 * Valida se o local de
	 * atendimento e do tipo leito.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testarBuscarResumoLocalPaciente003() throws ApplicationBusinessException {
		final  AghAtendimentos aghAtendimentos = new AghAtendimentos();
		final AinLeitos leito = new AinLeitos();
		leito.setLeitoID("0003W");
		aghAtendimentos.setLeito(leito);
				
		Mockito.when(messagesUtils.getResourceBundleValue(Mockito.anyString())).thenReturn(LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString());
		Mockito.when(mockedAghAtendimentoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aghAtendimentos);

		String message = systemUnderTest.buscarResumoLocalPaciente(aghAtendimentos);
		
		Assert.assertTrue(LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString().equals(message));
	}
	
	/**
	 * Valida se o local de
	 * atendimento e do tipo quarto.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testarBuscarResumoLocalPaciente004() throws ApplicationBusinessException {
		final  AghAtendimentos aghAtendimentos = new AghAtendimentos();
		final AinQuartos quarto = new AinQuartos();
		quarto.setNumero(Short.valueOf("373"));
		aghAtendimentos.setQuarto(quarto);
		
		Mockito.when(messagesUtils.getResourceBundleValue(Mockito.anyString())).thenReturn(LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString());
		Mockito.when(mockedAghAtendimentoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aghAtendimentos);
		String message = systemUnderTest.buscarResumoLocalPaciente(aghAtendimentos);
		
		Assert.assertTrue(LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString().equals(message));
	}
	
	/**
	 * Valida se o local de
	 * atendimento e do tipo unidade funcional.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testarBuscarResumoLocalPaciente005() throws ApplicationBusinessException {
		final  AghAtendimentos aghAtendimentos = new AghAtendimentos();
		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setAndar("2");
		unidadeFuncional.setIndAla(AghAla.N);
		unidadeFuncional.setDescricao("HEMODIALISE");
		aghAtendimentos.setUnidadeFuncional(unidadeFuncional);
		
		Mockito.when(messagesUtils.getResourceBundleValue(Mockito.anyString())).thenReturn(LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL.toString());
		Mockito.when(mockedAghAtendimentoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(aghAtendimentos);
		String message = systemUnderTest.buscarResumoLocalPaciente(aghAtendimentos);
		
		Assert.assertTrue(LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL.toString().equals(message));
	}
	
	
}

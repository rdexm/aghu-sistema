package br.gov.mec.aghu.ambulatorio.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author diego.pacheco
 *
 */
public class ProcedimentoAtendimentoConsultaRNTest extends AGHUBaseUnitTest<ProcedimentoAtendimentoConsultaRN>{

	@Mock
	private MamProcedimentoDAO mamProcedimentoDAOMocked;
	@Mock
	private IAghuFacade aghuFacadeMocked;
	@Mock
	private MarcacaoConsultaRN marcacaoConsultaRNMocked;
	@Mock
	private IPacienteFacade pacienteFacadeMocked;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	
	@Test
	/**
	 * Testa um procedimento pendente (não validado)
	 */
	public void testValidarProcedimentoAtendimentoBeforeInsertSuccess() {
		final MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
		procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
		final MamProcedimento procedimento = new MamProcedimento();
		procedimento.setSeq(7);
		procedimento.setSituacao(DominioSituacao.A);
		procedimentoRealizado.setProcedimento(procedimento);
		procedimentoRealizado.setServidor(null);
		procedimentoRealizado.setPaciente(null);
		final AacConsultas consulta = new AacConsultas();
		consulta.setNumero(1);
		procedimentoRealizado.setConsulta(consulta);
		final AipPacientes paciente = new AipPacientes();
		final Integer codigoPaciente = 2;
		
		try {
			
			Mockito.when(mamProcedimentoDAOMocked.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(procedimento);
			
			Mockito.when(marcacaoConsultaRNMocked.obterCodigoPacienteOrigem(Mockito.anyInt(), Mockito.anyInt())).thenReturn(codigoPaciente);

			Mockito.when(pacienteFacadeMocked.obterAipPacientesPorChavePrimaria(Mockito.anyInt())).thenReturn(paciente);
			
			whenObterServidorLogado();
			
			systemUnderTest.validarProcedimentoAtendimentoBeforeInsert(procedimentoRealizado);

		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	/**
	 * Testa um procedimento validado
	 */
	public void testValidarProcedimentoAtendimentoBeforeInsertError() {
		final MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
		procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.V);
		
		try {
			whenObterServidorLogado();
			
			systemUnderTest.validarProcedimentoAtendimentoBeforeInsert(procedimentoRealizado);
		}
		catch (BaseException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00644);
		}
	}
	
	@Test
	public void testValidarProcedimentoAtendimentoBeforeUpdateSuccess() {
		final MamProcedimentoRealizado oldProcedimentoRealizado = new MamProcedimentoRealizado();
		oldProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);

		final MamProcedimentoRealizado newProcedimentoRealizado = new MamProcedimentoRealizado();
		newProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);

		try {
			systemUnderTest.validarProcedimentoAtendimentoBeforeUpdate(oldProcedimentoRealizado, newProcedimentoRealizado);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testValidarProcedimentoAtendimentoBeforeUpdateError() {
		final MamProcedimento oldProcedimento = new MamProcedimento();
		oldProcedimento.setSeq(1);
		final MamProcedimentoRealizado oldProcedimentoRealizado = new MamProcedimentoRealizado();
		oldProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.V);
		oldProcedimentoRealizado.setProcedimento(oldProcedimento);
		oldProcedimentoRealizado.setSituacao(DominioSituacao.A);
		
		final MamProcedimento newProcedimento = new MamProcedimento();
		newProcedimento.setSeq(2);
		final MamProcedimentoRealizado newProcedimentoRealizado = new MamProcedimentoRealizado();
		newProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.V);
		newProcedimentoRealizado.setProcedimento(newProcedimento);
		newProcedimentoRealizado.setSituacao(DominioSituacao.I);
		
		try {
			systemUnderTest.validarProcedimentoAtendimentoBeforeUpdate(oldProcedimentoRealizado, newProcedimentoRealizado);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00645);
		}
	}
	
	@Test
	public void testValidarProcedimentoAtendimentoBeforeDeleteSuccess() {
		final MamProcedimentoRealizado oldProcedimentoRealizado = new MamProcedimentoRealizado();
		oldProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
		
		try {
			systemUnderTest.validarProcedimentoAtendimentoBeforeDelete(oldProcedimentoRealizado);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testValidarProcedimentoAtendimentoBeforeDeleteError() {
		final MamProcedimentoRealizado oldProcedimentoRealizado = new MamProcedimentoRealizado();
		oldProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.V);
		
		try {
			systemUnderTest.validarProcedimentoAtendimentoBeforeDelete(oldProcedimentoRealizado);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00646);
		}
	}
	
	
	@Test
	public void testVerificarProcedimentoAtendimentoAtivoSuccess() {
		final MamProcedimento procedimento = new MamProcedimento();
		procedimento.setSeq(7);
		procedimento.setSituacao(DominioSituacao.A);
		
		Mockito.when(mamProcedimentoDAOMocked.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(procedimento);
		
		try {
			systemUnderTest.verificarProcedimentoAtendimentoAtivo(procedimento.getSeq());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarProcedimentoAtendimentoAtivoError() {
		final MamProcedimento procedimento = new MamProcedimento();
		procedimento.setSeq(7);
		procedimento.setSituacao(DominioSituacao.I);
		
		Mockito.when(mamProcedimentoDAOMocked.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(procedimento);

		try {
			systemUnderTest.verificarProcedimentoAtendimentoAtivo(procedimento.getSeq());
			fail("testVerificarProcedimentoAtendimentoAtivoError: Exceção esperada não gerada.");
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00643);
		}
	}	
	
	@Test
	public void testVerificarProcedimentoAtendimentoValidadoInsert() {
		try {
			systemUnderTest.verificarProcedimentoAtendimentoValidado(DominioOperacaoBanco.INS);
			fail("testVerificarProcedimentoAtendimentoValidadoInsert: Exceção esperada não gerada.");

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00644);
		}
	}
	
	@Test
	public void testVerificarProcedimentoAtendimentoValidadoUpdate() {
		try {
			systemUnderTest.verificarProcedimentoAtendimentoValidado(DominioOperacaoBanco.UPD);
			fail("testVerificarProcedimentoAtendimentoValidadoUpdate: Exceção esperada não gerada.");
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00645);
		}
	}
	
	@Test
	public void testVerificarProcedimentoAtendimentoValidadoDelete() {
		try {
			systemUnderTest.verificarProcedimentoAtendimentoValidado(DominioOperacaoBanco.DEL);
			fail("testVerificarProcedimentoAtendimentoValidadoDelete: Exceção esperada não gerada.");
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), 
					ProcedimentoAtendimentoConsultaRN.ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00646);
		}
	}	
	
	
	@Test
	public void testVerificarProcedimentoValidadoAnteriormente() {
		final MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
		procedimentoRealizado.setDthrMovimento(new Date());
		procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.A);
		
		systemUnderTest.verificarProcedimentoValidadoAnteriormente(procedimentoRealizado);
	}
	
    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
}


}

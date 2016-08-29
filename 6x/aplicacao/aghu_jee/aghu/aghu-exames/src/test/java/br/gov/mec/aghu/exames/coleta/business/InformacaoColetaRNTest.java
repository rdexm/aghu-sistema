package br.gov.mec.aghu.exames.coleta.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.InformacaoColetaRN.InformacaoColetaRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaJnDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaDAO;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class InformacaoColetaRNTest  extends AGHUBaseUnitTest<InformacaoColetaRN>{

	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelInformacaoColetaDAO mockedAelInformacaoColetaDAO;
	@Mock
	private AelInformacaoMdtoColetaDAO mockedAelInformacaoMdtoColetaDAO;
	@Mock
	private AelInformacaoColetaJnDAO mockedAelInformacaoColetaJnDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Before
	public void iniciar() {
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}
	
	@Test
	public void testExecutarBeforeInsertInformacaoColeta() throws ApplicationBusinessException {
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setDtUltMenstruacao(new Date());
		informacaoColeta.setSolicitacaoExame(solicitacaoExame);		
		
		try {
			Mockito.when(mockedExamesFacade.obterLaudoSexoPaciente(solicitacaoExame)).thenReturn("F");
			
			systemUnderTest.executarBeforeInsertInformacaoColeta(informacaoColeta);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	public void testExecutarEnforce() {
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setId(informacaoColetaId);
		final DominioOperacaoBanco operacaoBanco = DominioOperacaoBanco.INS;
	
		Mockito.when(mockedAelInformacaoColetaDAO.obterCountInformacaoColetaPorSoeSeq(Mockito.anyInt())).thenReturn(1l);
		
		try {
			systemUnderTest.executarEnforce(informacaoColeta, operacaoBanco);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	@Test
	public void testVerificarTotalRegistrosSuccess() {
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setId(informacaoColetaId);
	
		Mockito.when(mockedAelInformacaoColetaDAO.obterCountInformacaoColetaPorSoeSeq(Mockito.anyInt())).thenReturn(1l);

		try {
			systemUnderTest.verificarTotalRegistros(informacaoColeta);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarTotalRegistrosError() {
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setId(informacaoColetaId);
		
		Mockito.when(mockedAelInformacaoColetaDAO.obterCountInformacaoColetaPorSoeSeq(Mockito.anyInt())).thenReturn(2l);

		try {
			systemUnderTest.verificarTotalRegistros(informacaoColeta);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), InformacaoColetaRNExceptionCode.AEL_02267);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateInformacaoColeta() {
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		informacaoColetaId.setSeqp(Short.valueOf("1"));
		final AelInformacaoColeta newInformacaoColeta = new AelInformacaoColeta();
		newInformacaoColeta.setId(informacaoColetaId);
		final AelInformacaoColeta oldInformacaoColeta = new AelInformacaoColeta();
		
		try {
			systemUnderTest.executarBeforeUpdateInformacaoColeta(oldInformacaoColeta, newInformacaoColeta);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarMedicamentoSuccess() {
		Boolean infMedicacao = Boolean.TRUE;
		Integer soeSeq = 7;
		Short seqp = Short.valueOf("1");
		
		Mockito.when(mockedAelInformacaoMdtoColetaDAO.obterCountInformacaoMdtoColetaPorSoeSeqESeqp(Mockito.anyInt(), Mockito.anyShort())).thenReturn(0l);

		try {
			systemUnderTest.verificarMedicamento(soeSeq, seqp, infMedicacao);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarMedicamentoError() {
		Boolean infMedicacao = Boolean.TRUE;
		Integer soeSeq = 7;
		Short seqp = Short.valueOf("1");
		
		Mockito.when(mockedAelInformacaoMdtoColetaDAO.obterCountInformacaoMdtoColetaPorSoeSeqESeqp(Mockito.anyInt(), Mockito.anyShort())).thenReturn(1l);

		try {
			systemUnderTest.verificarMedicamento(soeSeq, seqp, infMedicacao);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), InformacaoColetaRNExceptionCode.AEL_02250);
		}
	}	
	
	@Test
	public void testVerificarSexoPacienteSuccess() {
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames(); 
		
		Mockito.when(mockedExamesFacade.obterLaudoSexoPaciente(solicitacaoExame)).thenReturn("F");
		
		try {
			systemUnderTest.verificarSexoPaciente(solicitacaoExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarSexoPacienteError() {
		final AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames(); 
		
		Mockito.when(mockedExamesFacade.obterLaudoSexoPaciente(solicitacaoExame)).thenReturn("F");
		
		try {
			systemUnderTest.verificarSexoPaciente(solicitacaoExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), InformacaoColetaRNExceptionCode.AEL_02268);
		}
	}
	
	@Test
	public void testExecutarAfterUpdateInformacaoColeta() throws ApplicationBusinessException {
		final AelInformacaoColeta newInformacaoColeta = new AelInformacaoColeta();
		newInformacaoColeta.setInformacoesAdicionais("teste");
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		informacaoColetaId.setSeqp(Short.valueOf("1"));
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelInformacaoColeta oldInformacaoColeta = new AelInformacaoColeta();
		oldInformacaoColeta.setId(informacaoColetaId);	
		oldInformacaoColeta.setServidor(servidor);
				
		systemUnderTest.executarAfterUpdateInformacaoColeta(oldInformacaoColeta, newInformacaoColeta);		
	}
	
	@Test
	public void testExecutarAfterDeleteInformacaoColeta() throws ApplicationBusinessException {
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		informacaoColetaId.setSeqp(Short.valueOf("1"));
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelInformacaoColeta oldInformacaoColeta = new AelInformacaoColeta();
		oldInformacaoColeta.setId(informacaoColetaId);	
		oldInformacaoColeta.setServidor(servidor);
		
		systemUnderTest.executarAfterDeleteInformacaoColeta(oldInformacaoColeta);		
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
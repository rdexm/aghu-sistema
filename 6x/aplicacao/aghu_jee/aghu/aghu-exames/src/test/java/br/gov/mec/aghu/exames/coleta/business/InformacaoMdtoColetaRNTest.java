package br.gov.mec.aghu.exames.coleta.business;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaJnDAO;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaId;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColetaId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class InformacaoMdtoColetaRNTest  extends AGHUBaseUnitTest<InformacaoMdtoColetaRN>{

	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelInformacaoMdtoColetaJnDAO mockedAelInformacaoMdtoColetaJnDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Before
	public void doBeforeEachTestCase() {
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}

	@Test
	public void testExecutarBeforeInsertInformacaoMdtoColeta() throws ApplicationBusinessException {
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setInfMedicacao(Boolean.FALSE);
		final AelInformacaoMdtoColeta informacaoMdtoColeta = new AelInformacaoMdtoColeta();
		informacaoMdtoColeta.setInformacaoColeta(informacaoColeta);
		
		try {
			systemUnderTest.executarBeforeInsertInformacaoMdtoColeta(informacaoMdtoColeta);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	//COMENTADO, TAREFA #24988
//	@Test
//	public void testVerificarInformacaoMedicamentoSuccess() throws ApplicationBusinessException {
//		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
//		informacaoColeta.setInfMedicacao(Boolean.FALSE);
//		final AelInformacaoMdtoColeta informacaoMdtoColeta = new AelInformacaoMdtoColeta();
//		informacaoMdtoColeta.setInformacaoColeta(informacaoColeta);
//		
//		try {
//			systemUnderTest.verificarInformacaoMedicamento(informacaoMdtoColeta);
//			
//		} catch (ApplicationBusinessException e) {
//			Assert.fail("Exceção gerada: " + e.getCode());
//		}
//	}
	//COMENTADO, TAREFA #24988
//	@Test
//	public void testVerificarInformacaoMedicamentoError() throws ApplicationBusinessException {
//		final RapServidores servidor = new RapServidores();
//		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
//		informacaoColeta.setInfMedicacao(Boolean.TRUE);
//		final AelInformacaoMdtoColeta informacaoMdtoColeta = new AelInformacaoMdtoColeta();
//		informacaoMdtoColeta.setInformacaoColeta(informacaoColeta);
//		
//		try {
//			systemUnderTest.verificarInformacaoMedicamento(informacaoMdtoColeta);
//			
//		} catch (ApplicationBusinessException e) {
//			Assert.assertEquals(e.getCode(), InformacaoMdtoColetaRNExceptionCode.AEL_02251);
//		}
//	}
	
	@Test
	public void testExecutarAfterUpdateInformacaoMdtoColeta() throws ApplicationBusinessException {
		final AelInformacaoMdtoColeta newInformacaoMdtoColeta = new AelInformacaoMdtoColeta();
		newInformacaoMdtoColeta.setMedicamento("teste");
		final AelInformacaoMdtoColetaId informacaoColetaMdtoId = new AelInformacaoMdtoColetaId();
		informacaoColetaMdtoId.setIclSoeSeq(7);
		informacaoColetaMdtoId.setIclSeqp(Short.valueOf("1"));
		informacaoColetaMdtoId.setSeqp(1);
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		informacaoColetaId.setSeqp(Short.valueOf("1"));		
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setId(informacaoColetaId);
		final AelInformacaoMdtoColeta oldInformacaoMdtoColeta = new AelInformacaoMdtoColeta();
		oldInformacaoMdtoColeta.setId(informacaoColetaMdtoId);	
		oldInformacaoMdtoColeta.setServidor(servidor);
		oldInformacaoMdtoColeta.setInformacaoColeta(informacaoColeta);
		
		systemUnderTest.executarAfterUpdateInformacaoMdtoColeta(oldInformacaoMdtoColeta, newInformacaoMdtoColeta);
	}
	
	@Test
	public void testExecutarAfterDeleteInformacaoMdtoColeta() throws ApplicationBusinessException {
		final AelInformacaoMdtoColetaId informacaoColetaMdtoId = new AelInformacaoMdtoColetaId();
		informacaoColetaMdtoId.setIclSoeSeq(7);
		informacaoColetaMdtoId.setIclSeqp(Short.valueOf("1"));
		informacaoColetaMdtoId.setSeqp(1);
		final RapServidoresId servidorId = new RapServidoresId(111, Short.valueOf("2"));
		final RapServidores servidor = new RapServidores();
		servidor.setId(servidorId);
		final AelInformacaoColetaId informacaoColetaId = new AelInformacaoColetaId();
		informacaoColetaId.setSoeSeq(7);
		informacaoColetaId.setSeqp(Short.valueOf("1"));		
		final AelInformacaoColeta informacaoColeta = new AelInformacaoColeta();
		informacaoColeta.setId(informacaoColetaId);
		final AelInformacaoMdtoColeta oldInformacaoMdtoColeta = new AelInformacaoMdtoColeta();
		oldInformacaoMdtoColeta.setId(informacaoColetaMdtoId);	
		oldInformacaoMdtoColeta.setServidor(servidor);
		oldInformacaoMdtoColeta.setInformacaoColeta(informacaoColeta);
				
		systemUnderTest.executarAfterDeleteInformacaoMdtoColeta(oldInformacaoMdtoColeta);
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

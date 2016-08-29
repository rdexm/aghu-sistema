package br.gov.mec.aghu.exames.contratualizacao.business;

import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.business.MedicoExternoContratualizacaoCommand.MedicoExternoContratualizacaoActionExceptionCode;
import br.gov.mec.aghu.exames.dao.AghMedicoExternoDAO;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MedicoExternoContratualizacaoCommandTest extends AGHUBaseUnitTest<MedicoExternoContratualizacaoCommand>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AghMedicoExternoDAO mockedAghMedicoExternoDAO;
	@Mock
	private ICadastrosApoioExamesFacade mockedCadastrosApoioExamesFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

    @Before
    public void inicar() {
    	try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
    }
    
	@Test
	public void localizarMedicoExternoNomeMedicoNullTest() {

		try {
			systemUnderTest.localizarMedicoExterno(null, String.valueOf(12345));
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_MEDICO_EXTERNO);
		}

	}

	@Test
	public void localizarMedicoExternoCrmMedicoNullTest() {

		try {
			systemUnderTest.localizarMedicoExterno("TESTE NOME MEDICO", null);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_MEDICO_EXTERNO);
		}

	}

	@Test
	public void localizarMedicoExternoExistenteSucessoTest() {

		final AghMedicoExterno medicoExterno = new AghMedicoExterno();

		Mockito.when(mockedAghMedicoExternoDAO.obterMedicoExternoPeloNomeECrm(Mockito.anyString(), Mockito.anyString())).thenReturn(medicoExterno);
		
		try {
			AghMedicoExterno m = systemUnderTest.localizarMedicoExterno(
					"TESTE NOME MEDICO", String.valueOf(12345));
			Assert.assertTrue(m != null);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void localizarMedicoExternoNaoExistenteTest() {

		final AghMedicoExterno medicoExterno = new AghMedicoExterno();

		Mockito.when(mockedAghMedicoExternoDAO.obterMedicoExternoPeloNomeECrm(Mockito.anyString(), Mockito.anyString())).thenReturn(medicoExterno);


//		mockingContext.checking(new Expectations() {
//			{
//				oneOf(mockedAghMedicoExternoDAO)
//						.obterMedicoExternoPeloNomeECrm(null, 12345);
//				will(returnValue(medicoExterno));
//			}
//		});

		try {
			systemUnderTest.localizarMedicoExterno("TESTE NOME MEDICO", String.valueOf(12345));
//			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.fail("Não Deveria ter gerado uma exception!");
//			Assert.assertEquals(
//					e.getCode(),
//					MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_MEDICO_EXTERNO_JA_CADASTRADO);
		}

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

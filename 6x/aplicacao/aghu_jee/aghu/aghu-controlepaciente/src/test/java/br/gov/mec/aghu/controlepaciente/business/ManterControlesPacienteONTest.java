package br.gov.mec.aghu.controlepaciente.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpControlePacienteDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ManterControlesPacienteON.<br>
 * 
 * 
 * @author ebicca
 * 
 */
public class ManterControlesPacienteONTest extends AGHUBaseUnitTest<ManterControlesPacienteON>{
	
	private static final Log log = LogFactory.getLog(ManterControlesPacienteONTest.class);

	@Mock
	private EcpHorarioControleDAO mockedEcpHorarioControleDAO;
	@Mock
	private EcpControlePacienteDAO mockedEcpControlePacienteDAO;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void testValidacaoServidorAlteracaoOK() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(1,(short) 1));
		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setServidor(servidor);

		try {
			whenObterServidorLogado();
			this.systemUnderTest.validaServidorAlteracao(controlePaciente);
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testValidacaoServidorAlteracaoErro() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(2, (short) 2));
		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setServidor(servidor);
		
		try {
			whenObterServidorLogadoNull();
			this.systemUnderTest.validaServidorAlteracao(controlePaciente);
			Assert.fail("Não deve permitir chegar aqui");
		} catch (BaseException e) {
			log.error("ManterRegistrosControleONTest.testValidacaoServidorAlteracaoErro(): " + e.getMessage());
			assert true;
		}
	}
	
	@Test
	public void testValidacaoServidorExclusaoOK() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));
		EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setServidor(servidor);
		
		try {
			whenObterServidorLogado();
			this.systemUnderTest.validarServidorExclusao(horarioControle);
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testValidacaoServidorExclusaoErro() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(2, (short) 2));
		EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setServidor(servidor);
		
		try {
			whenObterServidorLogado();
			this.systemUnderTest.validarServidorExclusao(horarioControle);
			Assert.fail("Não deve permitir chegar aqui");
		} catch (BaseException e) {
			log.error("ManterRegistrosControleONTest.testValidacaoServidorAlteracaoErro(): " + e.getMessage());
			assert true;
		}
	}
	
	@Test
	public void testValidacaoServidorExclusaoParamNulo() {
		try {
			this.systemUnderTest.validarServidorExclusao(null);
			Assert.fail("Não deve permitir chegar aqui");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
	}
	
	@Test
	public void testValidacaoParametroExclusaoOK() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));
		EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setServidor(servidor);
		horarioControle.setDataHora(new Date());
		
		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, BigDecimal.ONE,
				null, "TESTE PARAMETRO", null);

		// Implementação do Mock
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);
		} catch (BaseException e2) {
			log.error("ManterControlesPacienteONTest.testValidacaoParametroExclusaoOK(): " + e2.getMessage());
			Assert.fail("erro");
		}
		
		try {
			this.systemUnderTest.validarParametroExclusao(horarioControle);
			assert true;
		} catch (BaseException e) {
			log.error("ManterRegistrosControleONTest.testValidacaoParametroExclusaoOK(): " + e.getMessage());
			Assert.fail("erro");
		}
	}
	
	@Test
	public void testValidacaoParametroExclusaoNulo() {
		final RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));
		EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setServidor(servidor);
		horarioControle.setDataHora(new Date());
		
		// Implementação do Mock
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);
		} catch (BaseException e2) {
			log.error("ManterControlesPacienteONTest.testValidacaoParametroExclusaoNulo(): " + e2.getMessage());
			Assert.fail("erro");
		}
		
		try {
			this.systemUnderTest.validarParametroExclusao(horarioControle);
			Assert.fail("Não deveria chegar aqui");
		} catch (BaseException e) {
			log.error("ManterRegistrosControleONTest.testValidacaoParametroExclusaoNulo(): " + e.getMessage());
			assert true;
		}
	}	
	
    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
		
		Mockito.when(mockedRegistroColaboradorFacade.obterRapServidor(Mockito.any(RapServidoresId.class))).thenReturn(rap);
    }

    private void whenObterServidorLogadoNull() throws BaseException {
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(null);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(null);
		
		Mockito.when(mockedRegistroColaboradorFacade.obterRapServidor(Mockito.any(RapServidoresId.class))).thenReturn(null);
    }

}
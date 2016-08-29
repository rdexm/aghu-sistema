package br.gov.mec.aghu.controlepaciente.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.business.VisualizarRegistrosControleON.VisualizarRegistrosControleONExceptionCode;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * VisualizarRegistrosControleON.<br>
 * Criado com base no teste {@link ListarPacientesRegistroControleONTest}.
 * 
 * @author dcastro
 * 
 */
public class VisualizarRegistrosControleONTest extends AGHUBaseUnitTest<VisualizarRegistrosControleON>{
	
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

	@Test(expected = IllegalArgumentException.class)
	public void excluirRegistroControlePacienteTest001() throws BaseException {

		this.systemUnderTest.excluirRegistroControlePaciente(null);
		Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");

	}

	@Test
	public void excluirRegistroControlePacienteTest002() throws BaseException {

		final EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setSeq((long) 1);
		horarioControle.setDataHora(Calendar.getInstance().getTime());

		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setSeq((long) 1);
		controlePaciente.setHorario(horarioControle);

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, new BigDecimal(
				12), null, "TESTE PARAMETRO", null);

		final RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));
		horarioControle.setServidor(servidor);

		Mockito.when(mockedEcpHorarioControleDAO.obterPorChavePrimaria(Mockito.anyLong())).thenReturn(horarioControle);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			whenObterServidorLogado();

			this.systemUnderTest.excluirRegistroControlePaciente((long) 1);
			assert true;
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void excluirRegistroControlePacienteTest003() throws BaseException {

		final EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setSeq((long) 1);

		Calendar dataHorarioControle = Calendar.getInstance();
		dataHorarioControle.add(Calendar.HOUR_OF_DAY, -13);
		horarioControle.setDataHora(dataHorarioControle.getTime());

		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setSeq((long) 1);
		controlePaciente.setHorario(horarioControle);

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, new BigDecimal(
				12), null, "TESTE PARAMETRO", null);

		Mockito.when(mockedEcpHorarioControleDAO.obterPorChavePrimaria(Mockito.anyLong())).thenReturn(horarioControle);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			this.systemUnderTest.excluirRegistroControlePaciente((long) 1);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(VisualizarRegistrosControleONExceptionCode.MENSAGEM_PRAZO_EXCEDIDO_PARA_EXCLUSAO, e.getCode());
		}

	}

	@Test
	public void excluirRegistroControlePacienteTest004() throws BaseException {

		final EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setSeq((long) 1);
		horarioControle.setDataHora(Calendar.getInstance().getTime());

		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setSeq((long) 1);

		Set<EcpControlePaciente> list = new HashSet<EcpControlePaciente>();
		list.add(controlePaciente);
		horarioControle.setControlePacientes(list);

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, new BigDecimal(
				12), null, "TESTE PARAMETRO", null);

		horarioControle.setServidor(new RapServidores(new RapServidoresId(2, (short) 2)));

		Mockito.when(mockedEcpHorarioControleDAO.obterPorChavePrimaria(Mockito.anyLong())).thenReturn(horarioControle);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			whenObterServidorLogado();
			this.systemUnderTest.excluirRegistroControlePaciente((long) 1);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(VisualizarRegistrosControleONExceptionCode.MENSAGEM_USUARIO_INVALIDO_EXCLUSAO, e.getCode());
		}

	}

	@Test
	public void excluirRegistroControlePacienteTest005() throws BaseException {

		final EcpHorarioControle horarioControle = new EcpHorarioControle();
		horarioControle.setSeq((long) 1);
		horarioControle.setDataHora(Calendar.getInstance().getTime());

		EcpControlePaciente controlePaciente = new EcpControlePaciente();
		controlePaciente.setSeq((long) 1);
		controlePaciente.setServidor(new RapServidores(new RapServidoresId(2, (short) 2)));
		// controlePaciente.setHorario(horarioControle);
		Set<EcpControlePaciente> list = new HashSet<EcpControlePaciente>();
		list.add(controlePaciente);
		horarioControle.setControlePacientes(list);

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, new BigDecimal(
				12), null, "TESTE PARAMETRO", null);

		final RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 1));
		horarioControle.setServidor(servidor);

		Mockito.when(mockedEcpHorarioControleDAO.obterPorChavePrimaria(Mockito.anyLong())).thenReturn(horarioControle);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			whenObterServidorLogado();
			this.systemUnderTest.excluirRegistroControlePaciente((long) 1);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(VisualizarRegistrosControleONExceptionCode.MENSAGEM_USUARIO_INVALIDO_EXCLUSAO, e.getCode());
		}

	}

	@Test
	public void obterParametroPrazoExclusao001() throws ApplicationBusinessException {

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, null, null,
				"TESTE PARAMETRO", null);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			this.systemUnderTest.obterParametroPrazoExclusao();
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(VisualizarRegistrosControleONExceptionCode.MENSAGEM_PARAMETRO_PRAZO_NAO_DEFINIDO, e.getCode());
		}

	}

	@Test
	public void obterParametroPrazoExclusao002() throws ApplicationBusinessException {

		final AghParametros parametro = new AghParametros(1, "AGHU", "P_AGHU_PRAZO_EXCLUI_CONTROLES", DominioSimNao.N, Calendar
				.getInstance().getTime(), "TESTE CRIADO POR", Calendar.getInstance().getTime(), "TESTE ALTERADO POR", null, BigDecimal.ONE,
				null, "TESTE PARAMETRO", null);

		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

		try {
			Integer result = this.systemUnderTest.obterParametroPrazoExclusao();
			Assert.assertEquals(result, (Integer) 1);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
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

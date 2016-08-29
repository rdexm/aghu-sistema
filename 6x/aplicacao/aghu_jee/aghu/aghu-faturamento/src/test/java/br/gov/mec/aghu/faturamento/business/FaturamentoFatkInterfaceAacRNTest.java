package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatLogInterfaceDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FaturamentoFatkInterfaceAacRNTest extends AGHUBaseUnitTest<FaturamentoFatkInterfaceAacRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private FatProcedAmbRealizadoDAO mockedFatProcedAmbRealizadoDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private FatLogInterfaceDAO mockedFatLogInterfaceDAO;
	@Mock
	private VerificacaoFaturamentoSusRN mockedVerificacaoFaturamentoSusRN;
	private static final DominioModuloCompetencia MODULO_COMPETENCIA_AMB = DominioModuloCompetencia.AMB;

	@Before
	public void doBeforeEachTestCase() {
	}


	/**
	 * Testa caso em que fatura == true e phiSeq != null
	 * 
	 */
	@Test
	public void testAtualizarFaturamentoProcedimentoConsultaComFaturaPhiSeqNaoNulo() {
		final Integer conNumero = 15;
		final Integer phiSeq = 9;
		final Short quantidade = 2;
		final AacRetornos retorno = new AacRetornos();
		retorno.setSeq(41);
		final FatCompetencia competencia = new FatCompetencia();

		try {
			List<FatCompetencia> listaCompetencias = new ArrayList<FatCompetencia>();
			listaCompetencias.add(competencia);
			Mockito.when(mockedVerificacaoFaturamentoSusRN.obterCompetenciasAbertasNaoFaturadasPorModulo(MODULO_COMPETENCIA_AMB)).thenReturn(listaCompetencias);

			retorno.setSeq(41);
			retorno.setFaturaSus(Boolean.TRUE);
			Mockito.when(mockedAmbulatorioFacade.obterDescricaoRetornoPorCodigo(retorno.getSeq())).thenReturn(retorno);

			List<FatProcedAmbRealizado> listaProcedAmbRealizados = new ArrayList<FatProcedAmbRealizado>();
			listaProcedAmbRealizados.add(new FatProcedAmbRealizado());
			Mockito.when(mockedFatProcedAmbRealizadoDAO.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(conNumero, phiSeq)).thenReturn(listaProcedAmbRealizados);

			systemUnderTest.atualizarFaturamentoProcedimentoConsulta(conNumero, phiSeq, quantidade, retorno, NOME_MICROCOMPUTADOR, new Date());

		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}

	/**
	 * Testa inserção com sucesso na FatProcedAmbRealizado
	 */
	@Test
	public void testInserirFaturamentoProcedimentoConsultaSuccess() {
		final Integer conNumero = 15;
		final Integer phiSeq = 9;
		final Date dthrRealizado = new Date();
		final FatCompetencia competencia = new FatCompetencia();
		final AacUnidFuncionalSalas unidFuncSala = new AacUnidFuncionalSalas();
		unidFuncSala.setUnidadeFuncional(new AghUnidadesFuncionais());
		final AacGradeAgendamenConsultas grade = new AacGradeAgendamenConsultas();
		grade.setAacUnidFuncionalSala(unidFuncSala);
		final AacConsultas consulta = new AacConsultas();
		consulta.setDtConsulta(new Date());
		consulta.setPaciente(new AipPacientes());
		consulta.setGradeAgendamenConsulta(grade);
		final AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
		consultaProcedHospitalar.setConsultas(consulta);

		try {
			
			List<FatCompetencia> listaCompetencias = new ArrayList<FatCompetencia>();
			listaCompetencias.add(competencia);
			Mockito.when(mockedVerificacaoFaturamentoSusRN.obterCompetenciasAbertasNaoFaturadasPorModulo(MODULO_COMPETENCIA_AMB)).thenReturn(listaCompetencias);

			List<AghAtendimentos> listaAtendimentos = new ArrayList<AghAtendimentos>();
			listaAtendimentos.add(new AghAtendimentos());
			Mockito.when(mockedAghuFacade.pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(conNumero, dthrRealizado)).thenReturn(listaAtendimentos);

			List<FatProcedAmbRealizado> listaProcedAmbRealizados = new ArrayList<FatProcedAmbRealizado>();
			listaProcedAmbRealizados.add(new FatProcedAmbRealizado());
			Mockito.when(mockedFatProcedAmbRealizadoDAO.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(conNumero, phiSeq)).thenReturn(listaProcedAmbRealizados);

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("AGH-CARGA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_USUARIO_CRIADO_POR)).thenReturn(parametro);

			//systemUnderTest.inserirFaturamentoProcedimentoConsulta(conNumero, phiSeq, quantidade, dthrRealizado);

		} catch (BaseException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		} catch (Exception e) {
			Assert.fail("Exceção gerada: " + Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 * Testa caso de falha na inserção de um FatProcedAmbRealizado
	 */
	@Test
	public void testInserirFaturamentoProcedimentoConsultaError() {
		final Integer conNumero = 15;
		final Integer phiSeq = 9;
		final Date dthrRealizado = new Date();
		final FatCompetencia competencia = new FatCompetencia();
		final AacUnidFuncionalSalas unidFuncSala = new AacUnidFuncionalSalas();
		unidFuncSala.setUnidadeFuncional(new AghUnidadesFuncionais());
		final AacGradeAgendamenConsultas grade = new AacGradeAgendamenConsultas();
		grade.setAacUnidFuncionalSala(unidFuncSala);
		final AacConsultas consulta = new AacConsultas();
		consulta.setDtConsulta(new Date());
		consulta.setPaciente(new AipPacientes());
		consulta.setGradeAgendamenConsulta(grade);
		final AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
		consultaProcedHospitalar.setConsultas(consulta);

		try {
		
			List<FatCompetencia> listaCompetencias = new ArrayList<FatCompetencia>();
			listaCompetencias.add(competencia);
			Mockito.when(mockedVerificacaoFaturamentoSusRN.obterCompetenciasAbertasNaoFaturadasPorModulo(MODULO_COMPETENCIA_AMB)).thenReturn(listaCompetencias);

			List<AghAtendimentos> listaAtendimentos = new ArrayList<AghAtendimentos>();
			listaAtendimentos.add(new AghAtendimentos());
			Mockito.when(mockedAghuFacade.pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(conNumero, dthrRealizado)).thenReturn(listaAtendimentos);

			List<FatProcedAmbRealizado> listaProcedAmbRealizados = new ArrayList<FatProcedAmbRealizado>();
			Mockito.when(mockedFatProcedAmbRealizadoDAO.buscarPorNumeroConsultaEProcedHospInternos(conNumero, phiSeq)).thenReturn(listaProcedAmbRealizados);
			
			Mockito.when(mockedFatProcedAmbRealizadoDAO.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(conNumero, phiSeq)).thenReturn(listaProcedAmbRealizados);

			AghParametros parametro = new AghParametros();
			parametro.setVlrTexto("AGH-CARGA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_USUARIO_CRIADO_POR)).thenReturn(parametro);

			AghParametros parametro1 = new AghParametros();
			parametro1.setVlrTexto("AGH-CARGA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_CONSULTA)).thenReturn(parametro1);

			Mockito.when(mockedFaturamentoFacade.clonarFatProcedAmbRealizado(Mockito.any(FatProcedAmbRealizado.class))).thenReturn(new FatProcedAmbRealizado());

			//TODO verificar teste
			//systemUnderTest.inserirFaturamentoProcedimentoConsulta(conNumero, phiSeq, quantidade, dthrRealizado);

		} catch (Exception e) {
		}
	}

}

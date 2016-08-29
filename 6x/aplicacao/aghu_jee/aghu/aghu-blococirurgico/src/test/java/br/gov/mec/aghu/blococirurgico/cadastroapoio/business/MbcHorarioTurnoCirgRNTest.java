package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcHorarioTurnoCirgRN.MbcHorarioTurnoCirgRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcHorarioTurnoCirgRNTest extends AGHUBaseUnitTest<MbcHorarioTurnoCirgRN> {

	@Mock
	private MbcCaractSalaEspDAO mockMbcCaractSalaEspDAO;
	@Mock
	private MbcHorarioTurnoCirgDAO mockMbcHorarioTurnoCirgDAO;
	
	private Calendar novoInicio;
	private Calendar novoFim;
	private Calendar inicio;
	private Calendar fim;
	
//	@Before
//	public void doBeforeEachTestCase() {
//		novoInicio = Calendar.getInstance();
//		novoInicio.set(Calendar.HOUR_OF_DAY, 8);
//		novoInicio.set(Calendar.MINUTE, 0);
//		novoInicio.set(Calendar.SECOND, 0);
//		novoInicio.set(Calendar.MILLISECOND, 0);
//		
//		novoFim = Calendar.getInstance();
//		novoFim.set(Calendar.HOUR_OF_DAY, 10);
//		novoFim.set(Calendar.MINUTE, 0);
//		novoFim.set(Calendar.SECOND, 0);
//		novoFim.set(Calendar.MILLISECOND, 0);
//		
//		inicio = Calendar.getInstance();
//		inicio.set(Calendar.HOUR_OF_DAY, 6);
//		inicio.set(Calendar.MINUTE, 0);
//		inicio.set(Calendar.SECOND, 0);
//		inicio.set(Calendar.MILLISECOND, 0);
//		
//		fim = Calendar.getInstance();
//		fim.set(Calendar.HOUR_OF_DAY, 9);
//		fim.set(Calendar.MINUTE, 0);
//		fim.set(Calendar.SECOND, 0);
//		fim.set(Calendar.MILLISECOND, 0);
//		
//		
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//		mockMbcCaractSalaEspDAO = mockingContext.mock(MbcCaractSalaEspDAO.class);
//		mockMbcHorarioTurnoCirgDAO = mockingContext.mock(MbcHorarioTurnoCirgDAO.class);
//		systemUnderTest = new MbcHorarioTurnoCirgRN(){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 3481012498808348133L;
//
//			@Override
//			protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
//				return mockMbcCaractSalaEspDAO;
//			};
//			
//			@Override
//			protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
//				return mockMbcHorarioTurnoCirgDAO;
//			};
//		};
//	}

	/**
	 * Horário igual, mostra mensagem.
	 */
	@Test
	public void testVerificarColisaoHorario() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		final Calendar inicio = Calendar.getInstance();
		inicio.set(Calendar.HOUR_OF_DAY, 1);
		inicio.set(Calendar.MINUTE, 0);
		inicio.set(Calendar.SECOND, 0);
		inicio.set(Calendar.MILLISECOND, 0);
		
		final Calendar fim = Calendar.getInstance();
		fim.set(Calendar.HOUR_OF_DAY, 2);
		fim.set(Calendar.MINUTE, 0);
		fim.set(Calendar.SECOND, 0);
		fim.set(Calendar.MILLISECOND, 0);
		
		novo.setHorarioInicial(inicio.getTime());
		novo.setHorarioFinal(fim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 05:00
	 * Novo horario Final: 07:00
	 * Horario Inicial 06:00
	 * Horario Final: 09:00 
	 */
	@Test
	public void testVerificarColisaoHorario02() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 5);
		novoFim.set(Calendar.HOUR_OF_DAY, 7);

		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 6);
//			fim.set(Calendar.HOUR_OF_DAY, 9);
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 08:00
	 * Novo horario Final: 10:00
	 * Horario Inicial 06:00
	 * Horario Final: 09:00 
	 */
	@Test
	public void testVerificarColisaoHorario03() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 8);
		novoFim.set(Calendar.HOUR_OF_DAY, 10);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 6);
//			fim.set(Calendar.HOUR_OF_DAY, 9);
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 07:00
	 * Novo horario Final: 08:00
	 * Horario Inicial 06:00
	 * Horario Final: 09:00 
	 */
	@Test
	public void testVerificarColisaoHorario04() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 7);
		novoFim.set(Calendar.HOUR_OF_DAY, 8);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 6);
//			fim.set(Calendar.HOUR_OF_DAY, 9);
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 21:00
	 * Novo horario Final: 23:00
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario05() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 23:00
	 * Novo horario Final: 02:00
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario06() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 02);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 23:00
	 * Novo horario Final: 00:00
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario07() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 00);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_00202");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_00202, e.getCode());
		}
	}
	
	/**
	 * Com 2 horarios, um ok e outro com erro
	 * Novo horario inicial: 23:00
	 * Novo horario Final: 00:00
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario08() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 00);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 02);
//			fim.set(Calendar.HOUR_OF_DAY, 04);
//			
//			Calendar inicio2 = Calendar.getInstance();
//			inicio2.set(Calendar.HOUR_OF_DAY, 22);
//			inicio2.set(Calendar.MINUTE, 0);
//			inicio2.set(Calendar.SECOND, 0);
//			inicio2.set(Calendar.MILLISECOND, 0);
//			
//			Calendar fim2 = Calendar.getInstance();
//			fim2.set(Calendar.HOUR_OF_DAY, 11);
//			fim2.set(Calendar.MINUTE, 0);
//			fim2.set(Calendar.SECOND, 0);
//			fim2.set(Calendar.MILLISECOND, 0);
//			
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg2 = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg2.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg2.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	/**
	 * Teste horarios OK
	 * Novo horario inicial: 21:00
	 * Novo horario Final: 22:00
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario09() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 00);
		
		novo.setHorarioInicial(novoInicio.getTime());
		novo.setHorarioFinal(novoFim.getTime());
		
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 02);
//			fim.set(Calendar.HOUR_OF_DAY, 04);
//			
//			Calendar inicio2 = Calendar.getInstance();
//			inicio2.set(Calendar.HOUR_OF_DAY, 22);
//			inicio2.set(Calendar.MINUTE, 0);
//			inicio2.set(Calendar.SECOND, 0);
//			inicio2.set(Calendar.MILLISECOND, 0);
//			
//			Calendar fim2 = Calendar.getInstance();
//			fim2.set(Calendar.HOUR_OF_DAY, 11);
//			fim2.set(Calendar.MINUTE, 0);
//			fim2.set(Calendar.SECOND, 0);
//			fim2.set(Calendar.MILLISECOND, 0);
//			
//			
//			List<MbcHorarioTurnoCirg> lista = new ArrayList<MbcHorarioTurnoCirg>();
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg.setHorarioFinal(fim.getTime());
//			MbcHorarioTurnoCirg mbcHorarioTurnoCirg2 = new MbcHorarioTurnoCirg();
//			mbcHorarioTurnoCirg2.setHorarioInicial(inicio.getTime());
//			mbcHorarioTurnoCirg2.setHorarioFinal(fim.getTime());
//			lista.add(mbcHorarioTurnoCirg);
//			
//			oneOf(mockMbcHorarioTurnoCirgDAO).buscarHorariosCirgOutrosTurnos(with(any(Short.class)), with(any(String.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	/**
	 * Fim Equipe: 00:00
	 * Novo Fim: 23:00
	 * Estourar exceção MBC-01141
	 */
	public void testVerificarHorarioEquipe() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			fim.set(Calendar.HOUR_OF_DAY, 0);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
		
	}
	
	/**
	 * Inicio Equipe: 22:00
	 * Novo início turno: 23:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe02() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 00);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
		
	}
	
	/**
	 * Inicio Equipe: 22:00
	 * Novo fim turno: 21:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe03() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 22);
		novoFim.set(Calendar.HOUR_OF_DAY, 21);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
		
	}
	
	
	/**
	 * Fim Equipe: 22:00
	 * Novo Inicio: 23:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe04() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 23);
//			fim.set(Calendar.HOUR_OF_DAY, 22);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
		
	}
	
	/**
	 * Fim Equipe: 22:00
	 * Novo Horario Final: 21:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe05() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 22);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 21);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
		
	}
	
	/**
	 * Inicio Equipe: 21:00
	 * Fim Equipe: 23:00
	 * Novo Horario Inicial: 21:00
	 * Novo Horario Final: 23:00
	 * Passa OK!
	 */
	@Test
	public void testVerificarHorarioEquipe06() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 21);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
		
	}
	
	/**
	 * Inicio Equipe: 22:00
	 * Fim Equipe: 23:00
	 * Novo Horario Inicial: 21:00
	 * Novo Horario Final: 00:00
	 * Passa OK!
	 */
	@Test
	public void testVerificarHorarioEquipe07() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
		
	}
	
	/**
	 * Inicio Equipe: 20:00
	 * Fim Equipe: 23:00
	 * Novo Horario Inicial: 21:00
	 * Novo Horario Final: 00:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe08() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 20);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
	}
	
	/**
	 * Inicio Equipe: 22:00
	 * Fim Equipe: 01:00
	 * Novo Horario Inicial: 21:00
	 * Novo Horario Final: 00:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe09() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 20);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
	}
	
	/**
	 * Inicio Equipe: 01::00
	 * Fim Equipe: 02:00
	 * Novo Horario Inicial: 21:00
	 * Novo Horario Final: 00:00
	 * Estourar exceção MBC-01141
	 */
	@Test
	public void testVerificarHorarioEquipe10() {
		MbcHorarioTurnoCirg novo = new MbcHorarioTurnoCirg();
		novo.setAghUnidadesFuncionais(new AghUnidadesFuncionais());
		novo.setId(new MbcHorarioTurnoCirgId());
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHorarioFinal(novoFim.getTime());
		novo.setHorarioInicial(novoInicio.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 1);
//			fim.set(Calendar.HOUR_OF_DAY, 2);
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorHorarioTurnoCirg(with(any(Short.class)), with(any(MbcTurnos.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcHorarioTurnoCirgRNExceptionCode.MBC_01141, e.getCode());
		}
	}
}

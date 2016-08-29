package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcCaracteristicaSalaCirgRN.MbcCaracteristicaSalaCirgRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoCaractSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTurnosDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;

@Ignore
public class MbcCaracteristicaSalaCirgRNTest extends AGHUBaseUnitTest<MbcCaracteristicaSalaCirgRN> {

	@Mock
	private MbcCaractSalaEspDAO mockMbcCaractSalaEspDAO;
	@Mock
	private MbcMvtoCaractSalaCirgDAO mockMbcMvtoCaractSalaCirgDAO;
	@Mock
	private MbcTurnosDAO mockMbcTurnosDAO;

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
//		mockMbcMvtoCaractSalaCirgDAO = mockingContext.mock(MbcMvtoCaractSalaCirgDAO.class);
//		mockMbcTurnosDAO = mockingContext.mock(MbcTurnosDAO.class);
//		systemUnderTest = new MbcCaracteristicaSalaCirgRN(){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 3639667940849259483L;
//
//			@Override
//			protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
//				return mockMbcCaractSalaEspDAO;
//			};
//			
//			@Override
//			protected MbcMvtoCaractSalaCirgDAO getMbcMvtoCaractSalaCirgDAO() {
//				return mockMbcMvtoCaractSalaCirgDAO;
//			};
//			
//			protected MbcTurnosDAO getMbcTurnosDAO() {
//				return mockMbcTurnosDAO;
//			}
//		};
//	}

	@Test
	/**
	 * Estourar exceção MBC_01139
	 */
	public void testVerificarHorarioEquipe() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01139");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01139, e.getCode());
		}
		
	}
	
	
	@Test
	/**
	 * Estourar exceção MBC_00407
	 */
	public void testVerificarEspecialidadeUrgenciaMapeamento() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
//		mockingContext.checking(new Expectations() {{
//			MbcTurnos turno = new MbcTurnos();
//			turno.setTurno("M");
//			oneOf(mockMbcTurnosDAO).obterOriginal(with(any(String.class))); 
//			will(returnValue(turno));
//		}});
//		
//		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setIndSituacao(DominioSituacao.A);
//			salaEsps.add(salaEsp);
//			
//			MbcCaractSalaEsp salaEsp2 = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp2.setIndSituacao(DominioSituacao.A);
//			salaEsps.add(salaEsp2);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorUnidadeSalaTurnoDiaSemana(with(any(Short.class)), with(any(Short.class)), with(any(MbcTurnos.class)), with(any(DominioDiaSemana.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		MbcHorarioTurnoCirgId cirgId = new MbcHorarioTurnoCirgId();
		cirgId.setTurno("M");
		mbcHorarioTurnoCirg.setId(cirgId);
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
		novo.setIndDisponivel(true);
		novo.setIndUrgencia(true);
		
		
		MbcSalaCirurgica salaCirurgica = new MbcSalaCirurgica();
		MbcSalaCirurgicaId salaCirurgicaId = new MbcSalaCirurgicaId();
		salaCirurgicaId.setSeqp(new Short("1"));
		salaCirurgica.setId(salaCirurgicaId);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(new Short("126"));
		salaCirurgica.setUnidadeFuncional(unidadeFuncional);
		novo.setMbcSalaCirurgica(salaCirurgica);
		
		try {
			systemUnderTest.validarCaractSalas(novo);
			Assert.fail("Exceção não gerada: MBC_00407");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_00407, e.getCode());
		}
	}
	
	/**
	 * Inicio Equipe: 22:00
	 * Novo início turno: 23:00
	 * Estourar exceção MBC_01140
	 */
	@Test
	public void testVerificarHorarioEquipe02() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 00);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01140");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
		}
		
	}
	
	/**
	 * Inicio Equipe: 22:00
	 * Novo fim turno: 21:00
	 * Estourar exceção MBC_01140
	 */
	@Test
	public void testVerificarHorarioEquipe03() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 22);
		novoFim.set(Calendar.HOUR_OF_DAY, 21);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01140");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
		}
		
	}
	
	
	/**
	 * Fim Equipe: 22:00
	 * Novo Inicio: 23:00
	 * Estourar exceção MBC_01140
	 */
	@Test
	public void testVerificarHorarioEquipe04() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 23);
//			fim.set(Calendar.HOUR_OF_DAY, 22);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01140");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
		}
		
	}
	
	/**
	 * Fim Equipe: 22:00
	 * Novo Horario Final: 21:00
	 * Estourar exceção MBC_01140
	 */
	@Test
	public void testVerificarHorarioEquipe05() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 22);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 21);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01140");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
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
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 21);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
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
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
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
	 * Estourar exceção MBC_01140
	 */
	@Test
	public void testVerificarHorarioEquipe08() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 20);
//			fim.set(Calendar.HOUR_OF_DAY, 23);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01140");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
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
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
		}
	}
	
	/**
	 * Inicio Equipe: 01:00
	 * Fim Equipe: 02:00
	 * Novo Horario Inicial: 21:00
	 * Novo Horario Final: 00:00
	 * Estourar exceção MBC_01140
	 */
	@Test
	public void testVerificarHorarioEquipe10() {
		MbcCaracteristicaSalaCirg novo = new MbcCaracteristicaSalaCirg();
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		mbcHorarioTurnoCirg.setHorarioInicial(novoInicio.getTime());
		mbcHorarioTurnoCirg.setHorarioFinal(novoFim.getTime());
		
		novo.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> salaEsps = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp salaEsp = new MbcCaractSalaEsp();
//			inicio.set(Calendar.HOUR_OF_DAY, 1);
//			fim.set(Calendar.HOUR_OF_DAY, 2);
//			salaEsp.setHoraFimEquipe(fim.getTime());
//			salaEsp.setHoraInicioEquipe(inicio.getTime());
//			salaEsps.add(salaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).pesquisarCaractSalaEspPorCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(salaEsps));
//		}});
		
		try {
			systemUnderTest.verificarHorarioEquipe(novo);
			Assert.fail("Exceção não gerada: MBC_01141");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01140, e.getCode());
		}
	}
	
	/**
	 * Gera exceção MBC_00204
	 */
	@Test
	public void testExecutarAntesDeInserir() {
		/* FR try {
			systemUnderTest.executarAntesDeInserir(new MbcCaracteristicaSalaCirg());
			Assert.fail("Exceção não gerada: MBC_00204");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_00204, e.getCode());
		}*/
	}
	
	/**
	 * Gera exceção MBC_01267
	 */
	@Test
	public void testatualizarHistoricoMovimentacoes() {
		MbcCaracteristicaSalaCirg mbcCaractSalaCirg = new MbcCaracteristicaSalaCirg();
		mbcCaractSalaCirg.setSeq(Short.valueOf("1"));
		
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockMbcMvtoCaractSalaCirgDAO).pesquisarUltimoMovimentoDaCaractSalaCirg(with(any(Short.class))); 
//			will(returnValue(null));
//		}});
		
		/* FR try {
			systemUnderTest.atualizarHistoricoMovimentacoes(mbcCaractSalaCirg, true);
			Assert.fail("Exceção não gerada: MBC_01267");
		} catch (BaseException e) {
			Assert.assertEquals(MbcCaracteristicaSalaCirgRNExceptionCode.MBC_01267, e.getCode());
		}*/
	}
}

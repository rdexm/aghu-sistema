package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.MbcCaractSalaEspRN.MbcCaractSalaEspRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaractSalaEspId;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcCaractSalaEspRNTest extends AGHUBaseUnitTest<MbcCaractSalaEspRN> {
	
	@Mock
	private MbcCaractSalaEspDAO mockMbcCaractSalaEspDAO;
	@Mock
	private MbcCaracteristicaSalaCirgDAO mockMbcCaracteristicaSalaCirgDAO;
	@Mock
	private MbcBloqSalaCirurgicaDAO mockMbcBloqSalaCirurgicaDAO;
	
	private Calendar novoInicio;
	private Calendar novoFim;
	private Calendar inicio;
	private Calendar fim;
	
//	@Before
//	public void doBeforeEachTestCase() {
//		
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
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//		mockMbcCaractSalaEspDAO = mockingContext.mock(MbcCaractSalaEspDAO.class);
//		mockMbcCaracteristicaSalaCirgDAO = mockingContext.mock(MbcCaracteristicaSalaCirgDAO.class);
//		mockMbcBloqSalaCirurgicaDAO = mockingContext.mock(MbcBloqSalaCirurgicaDAO.class);
//		systemUnderTest = new MbcCaractSalaEspRN(){
//			private static final long serialVersionUID = 5319394627758815702L;
//
//			@Override
//			protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
//				return mockMbcCaractSalaEspDAO;
//			};
//			
//			@Override
//			protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
//				return mockMbcCaracteristicaSalaCirgDAO;
//			};
//			
//			@Override
//			protected MbcBloqSalaCirurgicaDAO getMbcBloqSalaCirurgicaDAO() {
//				return mockMbcBloqSalaCirurgicaDAO;
//			};
//		};
//	}
	
	/**
	 * Estourar exceção MBC_00232
	 */
	@Test
	public void verificarEspecialidade01() {
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		
		try {
			systemUnderTest.verificarEspecialidade(novo);
			Assert.fail("Exceção não gerada: MBC_00232");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_00232, e.getCode());
		}
		
	}
	
	/**
	 * Estourar exceção MBC_01324
	 */
	@Test
	public void verificarEspecialidade02() {
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		
		AghEspecialidades esp = new AghEspecialidades();
		esp.setIndSituacao(DominioSituacao.I);
		novo.setAghEspecialidades(esp);
		
		try {
			systemUnderTest.verificarEspecialidade(novo);
			Assert.fail("Exceção não gerada: MBC_01324");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01324, e.getCode());
		}
		
	}
	
	
	/**
	 * Estourar exceção MBC_00234
	 */
	@Test
	public void verificarCaracteristica01() {
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		
		try {
			systemUnderTest.verificarCaracteristica(novo);
			Assert.fail("Exceção não gerada: MBC_00234");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_00234, e.getCode());
		}
		
	}
	
	/**
	 * Estourar exceção MBC_00235
	 */
	@Test
	public void verificarCaracteristica02() {
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		MbcCaracteristicaSalaCirg caract = new MbcCaracteristicaSalaCirg();
		caract.setSituacao(DominioSituacao.I);
		novo.setMbcCaracteristicaSalaCirg(caract);
		
		try {
			systemUnderTest.verificarCaracteristica(novo);
			Assert.fail("Exceção não gerada: MBC_00235");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_00235, e.getCode());
		}
		
	}
	
	/**
	 * Horário igual, mostra mensagem.
	 */
	@Test
	public void testVerificarColisaoHorario() {
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
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
		
		novo.setHoraInicioEquipe(inicio.getTime());
		novo.setHoraFimEquipe(fim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01136");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01136, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 5);
		novoFim.set(Calendar.HOUR_OF_DAY, 7);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 6);
//			fim.set(Calendar.HOUR_OF_DAY, 9);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01136");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01136, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 8);
		novoFim.set(Calendar.HOUR_OF_DAY, 10);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 6);
//			fim.set(Calendar.HOUR_OF_DAY, 9);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01136");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01136, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 7);
		novoFim.set(Calendar.HOUR_OF_DAY, 8);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 6);
//			fim.set(Calendar.HOUR_OF_DAY, 9);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01136");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01136, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01322");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01322, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 02);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01322");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01322, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 22);
//			fim.set(Calendar.HOUR_OF_DAY, 1);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01322");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01322, e.getCode());
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
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
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
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			MbcCaractSalaEsp mbcCaractSalaEsp2 = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp2.setHoraInicioEquipe(inicio2.getTime());
//			mbcCaractSalaEsp2.setHoraFimEquipe(fim2.getTime());
//			lista.add(mbcCaractSalaEsp);
//			lista.add(mbcCaractSalaEsp2);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01322");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01322, e.getCode());
		}
	}
	
	/**
	 * Teste horarios OK
	 * Novo horario inicial: 23:00
	 * Novo horario Final: 00:00
	 * Horario Inicial 02:00
	 * Horario Final: 04:00 
	 */
	@Test
	public void testVerificarColisaoHorario09() {
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
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
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 23:00
	 * Novo horario Final: 00:00
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario10() {
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 0);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 17);
//			fim.set(Calendar.HOUR_OF_DAY, 15);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01322");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01322, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: 23:00
	 * Novo horario Final: null
	 * Horario Inicial 22:00
	 * Horario Final: 01:00 
	 */
	@Test
	public void testVerificarColisaoHorario11() {
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(null);
		
//		mockingContext.checking(new Expectations() {{
//			inicio.set(Calendar.HOUR_OF_DAY, 17);
//			fim.set(Calendar.HOUR_OF_DAY, 15);
//			
//			List<MbcCaractSalaEsp> lista = new ArrayList<MbcCaractSalaEsp>();
//			MbcCaractSalaEsp mbcCaractSalaEsp = new MbcCaractSalaEsp();
//			mbcCaractSalaEsp.setHoraInicioEquipe(inicio.getTime());
//			mbcCaractSalaEsp.setHoraFimEquipe(fim.getTime());
//			lista.add(mbcCaractSalaEsp);
//			
//			oneOf(mockMbcCaractSalaEspDAO).buscarOutrosHorariosCaractSala(with(any(MbcCaracteristicaSalaCirg.class)),
//					with(any(Short.class)), with(any(Date.class)), with(any(Date.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
			Assert.fail("Exceção não gerada: MBC_01135");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01135, e.getCode());
		}
	}
	
	/**
	 * Novo horario inicial: null
	 * Novo horario Final: null
	 */
	@Test
	public void testVerificarColisaoHorario12() {
		MbcCaractSalaEspId id = new MbcCaractSalaEspId();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setId(id);
		
		novo.setHoraInicioEquipe(null);
		novo.setHoraFimEquipe(null);
		
		try {
			systemUnderTest.verificarColisaoHorario(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deveria falhar, validação deve ser ignorada quando horarios forem vazios");
		}
	}
	
	
	@Test
	/**
	 * Fim Equipe: 00:00
	 * Novo Fim: 23:00
	 * Estourar exceção MBC_01138
	 */
	public void testVerificarHorarioTurno() {
		MbcHorarioTurnoCirg hrTurno = new MbcHorarioTurnoCirg();
		fim.set(Calendar.HOUR_OF_DAY, 0);
		hrTurno.setHorarioInicial(fim.getTime());
		fim.set(Calendar.HOUR_OF_DAY, 6);
		hrTurno.setHorarioFinal(fim.getTime());
		
		MbcCaracteristicaSalaCirg caractSala = new MbcCaracteristicaSalaCirg();
		caractSala.setSeq(Short.valueOf("1"));
		caractSala.setMbcHorarioTurnoCirg(hrTurno);
		
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setMbcCaracteristicaSalaCirg(caractSala);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 0);
		novoFim.set(Calendar.HOUR_OF_DAY, 6);
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
		try {
			systemUnderTest.verificarHorarioTurno(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deveria gerar exceção");
		}
		
	}
	
	@Test
	/**
	 * Fim Equipe: 00:00
	 * Novo Fim: 23:00
	 * Estourar exceção MBC_01137
	 */
	public void testVerificarHorarioTurno02() {
		
		MbcCaracteristicaSalaCirg caractSala = new MbcCaracteristicaSalaCirg();
		caractSala.setSeq(Short.valueOf("1"));
		
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setMbcCaracteristicaSalaCirg(caractSala);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 23);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
		try {
			systemUnderTest.verificarHorarioTurno(novo);
			Assert.fail("Exceção não gerada: MBC_01137");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01137, e.getCode());
		}
	}
	
	@Test
	/**
	 * Fim Equipe: 00:00
	 * Novo Fim: 23:00
	 * Execução com sucesso
	 */
	public void testVerificarHorarioTurno03() {
		MbcHorarioTurnoCirg turno = new MbcHorarioTurnoCirg();
		inicio.set(Calendar.HOUR_OF_DAY, 21);
		fim.set(Calendar.HOUR_OF_DAY, 23);
		turno.setHorarioInicial(inicio.getTime());
		turno.setHorarioFinal(fim.getTime());
		
		MbcCaracteristicaSalaCirg caractSala = new MbcCaracteristicaSalaCirg();
		caractSala.setSeq(Short.valueOf("1"));
		caractSala.setMbcHorarioTurnoCirg(turno);
		
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setMbcCaracteristicaSalaCirg(caractSala);
		
		novoInicio.set(Calendar.HOUR_OF_DAY, 21);
		novoFim.set(Calendar.HOUR_OF_DAY, 23);
		novo.setHoraInicioEquipe(novoInicio.getTime());
		novo.setHoraFimEquipe(novoFim.getTime());
		
		try {
			systemUnderTest.verificarHorarioTurno(novo);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deveria ter falhado");
		}
	}
	
	
	/**
	 * Estoura erro MBC_01210
	 */
	@Test
	public void testVerificarPercentualReserva() {
		MbcProfAtuaUnidCirgs prof = new MbcProfAtuaUnidCirgs();
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setMbcProfAtuaUnidCirgs(prof);
		novo.setIndSituacao(DominioSituacao.A);
		
		
		try {
			systemUnderTest.verificarPercentualReserva(novo);
			Assert.fail("Exceção não gerada: MBC_01210");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01210, e.getCode());
		}
	}
	
	@Test
	/**
	 * Estourar exceção MBC_01296
	 */
	public void testVerificarPossibilidadeInativar() {
		
		MbcSalaCirurgica mbcSalaCirurgica = new MbcSalaCirurgica();
		MbcCaracteristicaSalaCirg caractSala = new MbcCaracteristicaSalaCirg();
		caractSala.setMbcSalaCirurgica(mbcSalaCirurgica);
		
		MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
		
		MbcCaractSalaEsp novo = new MbcCaractSalaEsp();
		novo.setMbcCaracteristicaSalaCirg(caractSala);
		novo.setMbcProfAtuaUnidCirgs(mbcProfAtuaUnidCirgs);
		
//		mockingContext.checking(new Expectations() {{
//			List <MbcBloqSalaCirurgica> lista = new ArrayList<MbcBloqSalaCirurgica>();
//			lista.add(new MbcBloqSalaCirurgica());
//			oneOf(mockMbcBloqSalaCirurgicaDAO).buscarBloqSalaPorCaractSalaEsp(with(any(MbcSalaCirurgica.class)), with(any(MbcProfAtuaUnidCirgs.class))); 
//			will(returnValue(lista));
//		}});
		
		try {
			systemUnderTest.verificarPossibilidadeInativar(novo);
			Assert.fail("Exceção não gerada: MBC_01296");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(MbcCaractSalaEspRNExceptionCode.MBC_01296, e.getCode());
		}
	}
	
}

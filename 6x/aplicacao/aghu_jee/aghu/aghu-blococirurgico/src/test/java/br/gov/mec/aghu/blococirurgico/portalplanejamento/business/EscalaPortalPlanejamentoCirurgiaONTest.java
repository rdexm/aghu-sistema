package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class EscalaPortalPlanejamentoCirurgiaONTest extends AGHUBaseUnitTest<EscalaPortalPlanejamentoCirurgiaON> {

	private MbcAgendasDAO mockMbcAgendasDAO;
	private MbcCaracteristicaSalaCirgDAO mockMbcCaracteristicaSalaCirgDAO;
	
//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//		
//		mockMbcAgendasDAO =  mockingContext.mock(MbcAgendasDAO.class);
//		mockMbcCaracteristicaSalaCirgDAO = mockingContext.mock(MbcCaracteristicaSalaCirgDAO.class);
//		
//		
//		systemUnderTest = new EscalaPortalPlanejamentoCirurgiaON(){
//		/**
//		 * 
//		 */
//			private static final long serialVersionUID = 7810403200971170267L;
//				
//			protected MbcAgendasDAO getMbcAgendasDAO() {
//				return mockMbcAgendasDAO;
//			};
//				
//			protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
//				return mockMbcCaracteristicaSalaCirgDAO;
//			};
//		};
//	}

	
	//regimeSusIguais
	@Test
	public void verificarRegimeSusIguais() {
		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendasDAO).buscarRegimeSusPorId(with(any(Integer.class))); 
//			will(returnValue(getRegimeSusAmbulatorio()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendasDAO).buscarRegimeSusPacientePorId(with(any(Integer.class))); 
//			will(returnValue(getAgendaPacienteAmbulatorio()));
//		}});
		
		Assert.assertNull(systemUnderTest.verificarRegimeMinimoSus(2));
	
	}
	
	//regimeSusMenor
	@Test
	public void verificarRegimeSusMenor() {
		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendasDAO).buscarRegimeSusPorId(with(any(Integer.class))); 
//			will(returnValue(getRegimeSusInternacao()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendasDAO).buscarRegimeSusPacientePorId(with(any(Integer.class))); 
//			will(returnValue(getAgendaPacienteAmbulatorio()));
//		}});
		
		Assert.assertNotNull(systemUnderTest.verificarRegimeMinimoSus(2));
	
	}
	
	//regimeSusMaior
	@Test
	public void verificarRegimeSusMaior() {
		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendasDAO).buscarRegimeSusPorId(with(any(Integer.class))); 
//			will(returnValue(getRegimeSusAmbulatorio()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockMbcAgendasDAO).buscarRegimeSusPacientePorId(with(any(Integer.class))); 
//			will(returnValue(getAgendaPacienteInternacao()));
//		}});
		
		Assert.assertNull(systemUnderTest.verificarRegimeMinimoSus(2));
	
	}
	
	private List<MbcAgendas> getAgendaPacienteAmbulatorio(){
		List<MbcAgendas> agendas = new ArrayList<MbcAgendas>();
		MbcAgendas agenda = new MbcAgendas();
		agenda.setRegime(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO);
		agendas.add(agenda);
		return agendas;
	}
	
	private List<MbcAgendas> getAgendaPacienteInternacao(){
		List<MbcAgendas> agendas = new ArrayList<MbcAgendas>();
		MbcAgendas agenda = new MbcAgendas();
		agenda.setRegime(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO);
		agendas.add(agenda);
		return agendas;
	}
	
	private List<DominioRegimeProcedimentoCirurgicoSus> getRegimeSusAmbulatorio(){
		List<DominioRegimeProcedimentoCirurgicoSus> regimes = new ArrayList<DominioRegimeProcedimentoCirurgicoSus>();
		regimes.add(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO);
		return regimes;
	}
	
	private List<DominioRegimeProcedimentoCirurgicoSus> getRegimeSusInternacao(){
		List<DominioRegimeProcedimentoCirurgicoSus> regimes = new ArrayList<DominioRegimeProcedimentoCirurgicoSus>();
		regimes.add(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO);
		return regimes;
	}
	
}

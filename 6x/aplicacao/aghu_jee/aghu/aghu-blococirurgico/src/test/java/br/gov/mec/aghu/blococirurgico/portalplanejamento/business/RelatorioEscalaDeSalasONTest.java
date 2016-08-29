package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class RelatorioEscalaDeSalasONTest extends AGHUBaseUnitTest<RelatorioEscalaDeSalasON>{

	private RelatorioEscalaDeSalasRN mockRelatorioEscalaDeSalasRN;
	private MbcCaracteristicaSalaCirgDAO mockMbcCaracteristicaSalaCirgDAO;
	private IAghuFacade mockIAghuFacade;

//	@Before
//	public void doBeforeEachTestCase() {
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
//
//		mockRelatorioEscalaDeSalasRN = mockingContext.mock(RelatorioEscalaDeSalasRN.class);
//		mockMbcCaracteristicaSalaCirgDAO = mockingContext.mock(MbcCaracteristicaSalaCirgDAO.class);
//		mockIAghuFacade = mockingContext.mock(IAghuFacade.class);
//
//		systemUnderTest = new RelatorioEscalaDeSalasON(){
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 7810403200971170267L;
//
//			@Override
//			protected RelatorioEscalaDeSalasRN getRelatorioEscalaDeSalasRN() {
//				return mockRelatorioEscalaDeSalasRN;
//			};
//			
//			@Override
//			protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
//				return mockMbcCaracteristicaSalaCirgDAO;
//			};
//			
//			@Override
//			protected br.gov.mec.aghu.business.IAghuFacade getAghuFacade() {
//				return mockIAghuFacade;
//			};
//		};
//	}

	@Test
	public void testListarEquipeSalas() {

//		mockingContext.checking(new Expectations() {{
//			oneOf(mockIAghuFacade).obterAghUnidadesFuncionaisPorChavePrimaria(with(any(Short.class))); 
//			will(returnValue(new AghUnidadesFuncionais()));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockIAghuFacade).getRazaoSocial(); 
//			will(returnValue(with(any(String.class))));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			allowing(mockRelatorioEscalaDeSalasRN).obterEquipeSala(with(any(Short.class)), with(any(Short.class)), with(any(MbcTurnos.class)), with(any(DominioDiaSemana.class))); 
//			will(returnValue(with(any(String.class))));
//		}});
//		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mockMbcCaracteristicaSalaCirgDAO).listarCaracteristicaSalaCirgPorUnidade(with(any(Short.class)), with(DominioSituacao.A));
//			will(returnValue(getListarMbcCaracteristicaSalaCirg()));
//		}});
			
		Assert.assertNotNull(systemUnderTest.listarEquipeSalas(Short.valueOf("0")));
	}
	
	private List<MbcCaracteristicaSalaCirg> getListarMbcCaracteristicaSalaCirg() {
		List<MbcCaracteristicaSalaCirg> list = new ArrayList<MbcCaracteristicaSalaCirg>();
		
		MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		MbcSalaCirurgica mbcSalaCirurgica = new MbcSalaCirurgica();
		mbcSalaCirurgica.setId(new MbcSalaCirurgicaId(Short.valueOf("0"), Short.valueOf("0")));
		mbcCaracteristicaSalaCirg.setMbcSalaCirurgica(mbcSalaCirurgica);
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		
		MbcTurnos turno = new MbcTurnos();
		turno.setTurno("N");
		mbcHorarioTurnoCirg.setMbcTurnos(turno);
		// FIXME: alterado pela criação da tabela MBC_TURNOS 
		//mbcHorarioTurnoCirg.setId(new MbcHorarioTurnoCirgId(Short.valueOf("0"), DominioTurnoMvtCaractSalaCirg.M));
		mbcCaracteristicaSalaCirg.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		list.add(mbcCaracteristicaSalaCirg);

		return list;
	};
}

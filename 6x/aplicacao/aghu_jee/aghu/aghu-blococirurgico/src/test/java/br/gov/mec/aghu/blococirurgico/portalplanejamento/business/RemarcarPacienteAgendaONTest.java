package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.RemarcarPacienteAgendaON.RemarcarPacienteAgendaONExceptionCode;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class RemarcarPacienteAgendaONTest extends AGHUBaseUnitTest<RemarcarPacienteAgendaON>{

	@Mock
	private MbcSalaCirurgicaDAO mockedMbcSalaCirurgicaDAO;
	@Mock
	private MbcControleEscalaCirurgicaDAO mockedMbcControleEscalaCirurgicaDAO;

	private MbcAgendas agenda;

//	@Before
//	public void doBeforeEachTestCase() {
//
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedMbcSalaCirurgicaDAO = mockingContext
//				.mock(MbcSalaCirurgicaDAO.class);
//		mockedMbcControleEscalaCirurgicaDAO = mockingContext
//				.mock(MbcControleEscalaCirurgicaDAO.class);
//
//		agenda = new MbcAgendas();
//		agenda.setEspecialidade(new AghEspecialidades());
//		agenda.setProfAtuaUnidCirgs(new MbcProfAtuaUnidCirgs());
//		agenda.setUnidadeFuncional(new AghUnidadesFuncionais());
//
//		systemUnderTest = new RemarcarPacienteAgendaON() {
//		
//			private static final long serialVersionUID = -4180364916655310344L;
//
//			protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
//				return mockedMbcSalaCirurgicaDAO;
//			}
//
//			protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
//				return mockedMbcControleEscalaCirurgicaDAO;
//			}
//		};
//	}

	/**
	 * Valida mensagem exceção 
	 * ERRO MBC_01016
	 */
	@Test
	public void testValidarDataReagendamentoExcecao() {

//		mockingContext.checking(new Expectations() {
//			{
//				List<MbcSalaCirurgica> listaRetorno = new ArrayList<MbcSalaCirurgica>();
//				oneOf(mockedMbcSalaCirurgicaDAO)
//						.validarDataRemarcacaoAgendaEquipe(
//								with(any(MbcProfAtuaUnidCirgs.class)),
//								with(any(Short.class)),
//								with(any(Short.class)),
//								with(any(Date.class)));
//				will(returnValue(listaRetorno));
//
//			}
//		});

		Boolean retorno = systemUnderTest.validarDataReagendamento(new Date(), agenda.getProfAtuaUnidCirgs(),
				agenda.getEspecialidade().getSeq(), agenda.getUnidadeFuncional().getSeq());
		Assert.assertFalse(retorno);
	}
	
	/**
	 * Valida mensagem exceção 
	 * SUCESSO
	 */
	@Test
	public void testValidarDataReagendamentoSucesso() {

//		mockingContext.checking(new Expectations() {
//			{
//				List<MbcSalaCirurgica> listaRetorno = new ArrayList<MbcSalaCirurgica>();
//				listaRetorno.add(new MbcSalaCirurgica());
//				oneOf(mockedMbcSalaCirurgicaDAO)
//						.validarDataRemarcacaoAgendaEquipe(
//								with(any(MbcProfAtuaUnidCirgs.class)),
//								with(any(Short.class)),
//								with(any(Short.class)),
//								with(any(Date.class)));
//				will(returnValue(listaRetorno));
//
//			}
//		});

		Boolean retorno = systemUnderTest.validarDataReagendamento(new Date(), agenda.getProfAtuaUnidCirgs(),
				agenda.getEspecialidade().getSeq(), agenda.getUnidadeFuncional().getSeq());
		Assert.assertFalse(!retorno);
	}

	/**
	 * Valida método validarDataReagendamentoComDataAtual sem excecao
	 * SUCESSO
	 */
	@Test
	public void testValidarDataReagendamentoComDataAtual() {
		Calendar data = Calendar.getInstance();
		data.set(2050, 4, 20);
		
		try {
			systemUnderTest.validarDataReagendamentoComDataAtual(data.getTime());
		} catch (BaseException e) {
			Assert.fail("Não deveria ocorrer erro." + e);
		}
	}
	
	/**
	 * Valida método validarDataReagendamentoComDataAtual
	 * ERRO MBC-01019
	 */
	@Test
	public void testValidarDataReagendamentoComDataAtualExcecao() {
		
		try {
			systemUnderTest.validarDataReagendamentoComDataAtual(new Date());
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(RemarcarPacienteAgendaONExceptionCode.MBC_01019));
		}
	}
}

package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.business.MbcProfCirurgiasRN.MbcProfCirurgiasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcProfCirurgiasRNTest extends AGHUBaseUnitTest<MbcProfCirurgiasRN> {

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private MbcProfCirurgiasDAO mockedMbcProfCirurgiasDAO;
	@Mock
	private MbcCirurgiasDAO mockedMbcCirurgiasDAO;
	@Mock
	private MbcProfAtuaUnidCirgsDAO mockedMbcProfAtuaUnidCirgsDAO;
	@Mock
	private MbcControleEscalaCirurgicaDAO mockedMbcControleEscalaCirurgicaDAO;
	@Mock
	private MbcCirurgiasRN mockedMbcCirurgiasRN;
	@Mock
	private ICascaFacade mockedCascaFacade;

	private final static String MSG_DEVERIA_OCORRER = "Deveria ocorrer: ";
	private final static String MSG_NAO_DEVERIA_OCORRER = "Não deveria ocorrer: ";

//	@Before
//	public void doBeforeEachTestCase() {
//
//		// contexto dos mocks, usado para criar os mocks e definir a expectativa
//		// de chamada dos métodos.
//		mockingContext = new Mockery() {
//			{
//				setImposteriser(ClassImposteriser.INSTANCE);
//			}
//		};
//
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedMbcProfCirurgiasDAO = mockingContext.mock(MbcProfCirurgiasDAO.class);
//		mockedMbcCirurgiasDAO = mockingContext.mock(MbcCirurgiasDAO.class);
//		mockedMbcProfAtuaUnidCirgsDAO = mockingContext.mock(MbcProfAtuaUnidCirgsDAO.class);
//		mockedMbcControleEscalaCirurgicaDAO = mockingContext.mock(MbcControleEscalaCirurgicaDAO.class);
//		mockedMbcCirurgiasRN = mockingContext.mock(MbcCirurgiasRN.class);
//		mockedCascaFacade = mockingContext.mock(ICascaFacade.class);
//
//		systemUnderTest = new MbcProfCirurgiasRN() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 8885203544342879374L;
//
//			protected IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			}
//
//			protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
//				return mockedMbcProfCirurgiasDAO;
//			}
//
//			protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
//				return mockedMbcCirurgiasDAO;
//			}
//
//			protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
//				return mockedMbcProfAtuaUnidCirgsDAO;
//			}
//
//			protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
//				return mockedMbcControleEscalaCirurgicaDAO;
//			}
//
//			protected MbcCirurgiasRN getMbcCirurgiasRN() {
//				return mockedMbcCirurgiasRN;
//			}
//
//			protected ICascaFacade getICascaFacade() {
//				return mockedCascaFacade;
//			}
//
//		};
//
//	}

	@Test
	public void verificarFuncaoProfissionalCirurgiaSuccess() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		final MbcProfAtuaUnidCirgs profAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
		final MbcProfAtuaUnidCirgsId idProfAtuaUnidCirgs = new MbcProfAtuaUnidCirgsId();
		profAtuaUnidCirgs.setId(idProfAtuaUnidCirgs);

		profCirurgias.setMbcProfAtuaUnidCirgs(profAtuaUnidCirgs);
		profCirurgias.setId(new MbcProfCirurgiasId());

		try {

//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcProfAtuaUnidCirgsDAO).obterPorChavePrimaria(with(any(MbcProfAtuaUnidCirgsId.class)));
//					will(returnValue(profAtuaUnidCirgs));
//				}
//			});

			systemUnderTest.verificarFuncaoProfissionalCirurgia(profCirurgias);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}
	}

	@Test
	public void verificarFuncaoProfissionalCirurgiaError001() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		final MbcProfAtuaUnidCirgs profAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
		final MbcProfAtuaUnidCirgsId idProfAtuaUnidCirgs = new MbcProfAtuaUnidCirgsId();
		profAtuaUnidCirgs.setId(idProfAtuaUnidCirgs);

		profCirurgias.setMbcProfAtuaUnidCirgs(profAtuaUnidCirgs);
		profCirurgias.setId(new MbcProfCirurgiasId());

		try {

//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcProfAtuaUnidCirgsDAO).obterPorChavePrimaria(with(any(MbcProfAtuaUnidCirgsId.class)));
//					will(returnValue(null)); // Teste aqui!
//				}
//			});

			systemUnderTest.verificarFuncaoProfissionalCirurgia(profCirurgias);
			Assert.fail(MSG_DEVERIA_OCORRER + MbcProfCirurgiasRNExceptionCode.MBC_00326);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcProfCirurgiasRNExceptionCode.MBC_00326));
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}
	}

	@Test
	public void verificarFuncaoProfissionalCirurgiaError002() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		final MbcProfAtuaUnidCirgs profAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
		final MbcProfAtuaUnidCirgsId idProfAtuaUnidCirgs = new MbcProfAtuaUnidCirgsId();
		profAtuaUnidCirgs.setId(idProfAtuaUnidCirgs);

		profCirurgias.setMbcProfAtuaUnidCirgs(profAtuaUnidCirgs);

		profAtuaUnidCirgs.setSituacao(DominioSituacao.I); // Teste aqui!
		profCirurgias.setId(new MbcProfCirurgiasId());

		try {

//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcProfAtuaUnidCirgsDAO).obterPorChavePrimaria(with(any(MbcProfAtuaUnidCirgsId.class)));
//					will(returnValue(profAtuaUnidCirgs));
//				}
//			});

			systemUnderTest.verificarFuncaoProfissionalCirurgia(profCirurgias);
			Assert.fail(MSG_DEVERIA_OCORRER + MbcProfCirurgiasRNExceptionCode.MBC_00327);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcProfCirurgiasRNExceptionCode.MBC_00327));
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}
	}

	@Test
	public void verificarProfissionalResponsavelRealizouCirurgiaSuccess() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		profCirurgias.setIndRealizou(true);

		DominioFuncaoProfissional[] p = { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MAX };

		for (DominioFuncaoProfissional dominioFuncaoProfissional : p) {
			profCirurgias.setFuncaoProfissional(dominioFuncaoProfissional);

			try {
				systemUnderTest.verificarProfissionalResponsavelRealizouCirurgia(profCirurgias); // Teste aqui!
				Assert.assertTrue(true);
			} catch (BaseException e) {
				Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
			} catch (Exception e) {
				Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
			}
		}

	}

	@Test
	public void verificarProfissionalResponsavelRealizouCirurgiaError() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		profCirurgias.setIndRealizou(true);

		profCirurgias.setFuncaoProfissional(DominioFuncaoProfissional.ANC); // Teste aqui!

		try {
			systemUnderTest.verificarProfissionalResponsavelRealizouCirurgia(profCirurgias);
			Assert.fail(MSG_DEVERIA_OCORRER + MbcProfCirurgiasRNExceptionCode.MBC_00341);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcProfCirurgiasRNExceptionCode.MBC_00341));
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}

	}

//	@Test
//	public void verificarUnidadeFuncionalProfissionalCirurgiaSuccess() {
//
//		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
//
//		final MbcCirurgias cirurgia = new MbcCirurgias(0);
//
//		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
//
//		profCirurgias.setUnidadeFuncional(unidadeFuncional); // Teste aqui!
//		cirurgia.setUnidadeFuncional(unidadeFuncional); // Teste aqui!
//
//		profCirurgias.setCirurgia(cirurgia);
//		profCirurgias.setId(new MbcProfCirurgiasId());
//
//		try {
//
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcCirurgiasDAO).obterPorChavePrimaria(with(any(Integer.class)));
//					will(returnValue(cirurgia));
//					oneOf(mockedAghuFacade).obterAghUnidadesFuncionaisPorChavePrimaria(chavePrimaria)
//				}
//			});
//
//			systemUnderTest.verificarUnidadeFuncionalProfissionalCirurgia(profCirurgias);
//			Assert.assertTrue(true);
//		} catch (BaseException e) {
//			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
//		} catch (Exception e) {
//			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
//			e.printStackTrace();
//		}
//	}

//	@Test
//	public void verificarUnidadeFuncionalProfissionalCirurgiaError001() {
//
//		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
//
//		final MbcCirurgias cirurgia = new MbcCirurgias(0);
//
//		profCirurgias.setCirurgia(cirurgia);
//
//		try {
//
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcCirurgiasDAO).obterPorChavePrimaria(with(any(Integer.class)));
//					will(returnValue(null)); // Teste aqui!
//				}
//			});
//
//			systemUnderTest.verificarUnidadeFuncionalProfissionalCirurgia(profCirurgias);
//			Assert.fail(MSG_DEVERIA_OCORRER + MbcProfCirurgiasRNExceptionCode.MBC_00338);
//		} catch (BaseException e) {
//			Assert.assertTrue(e.getCode().equals(MbcProfCirurgiasRNExceptionCode.MBC_00338));
//		} catch (Exception e) {
//			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
//		}
//	}
//
//	@Test
//	public void verificarUnidadeFuncionalProfissionalCirurgiaError002() {
//
//		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
//
//		final MbcCirurgias cirurgia = new MbcCirurgias(0);
//
//		profCirurgias.setUnidadeFuncional(new AghUnidadesFuncionais(Short.valueOf("0"))); // Teste aqui!
//		cirurgia.setUnidadeFuncional(new AghUnidadesFuncionais(Short.valueOf("1"))); // Teste aqui!
//
//		profCirurgias.setCirurgia(cirurgia);
//
//		try {
//
//			mockingContext.checking(new Expectations() {
//				{
//					oneOf(mockedMbcCirurgiasDAO).obterPorChavePrimaria(with(any(Integer.class)));
//					will(returnValue(cirurgia));
//				}
//			});
//
//			systemUnderTest.verificarUnidadeFuncionalProfissionalCirurgia(profCirurgias);
//			Assert.fail(MSG_DEVERIA_OCORRER + MbcProfCirurgiasRNExceptionCode.MBC_00339);
//		} catch (BaseException e) {
//			Assert.assertTrue(e.getCode().equals(MbcProfCirurgiasRNExceptionCode.MBC_00339));
//		} catch (Exception e) {
//			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
//		}
//	}

	@Test
	public void verificarNaturezaAgendamentoEletivaSuccess() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		final MbcCirurgias cirurgia = new MbcCirurgias(0);

		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.ELE);

		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		cirurgia.setUnidadeFuncional(unidadeFuncional);

		profCirurgias.setCirurgia(cirurgia);

		/* FR try {

			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedMbcCirurgiasDAO).obterPorChavePrimaria(with(any(Integer.class)));
					will(returnValue(cirurgia));

					oneOf(mockedCascaFacade).usuarioTemPermissao(with(any(String.class)), with(any(String.class)));
					will(returnValue(Boolean.FALSE));

					oneOf(mockedMbcControleEscalaCirurgicaDAO).verificaExistenciaPeviaDefinitivaPorUNFData(with(any(Short.class)), with(any(Date.class)),
							with(any(DominioTipoEscala.class)));
					will(returnValue(Boolean.FALSE));
				}
			});

			systemUnderTest.verificarNaturezaAgendamentoEletiva(cirurgia.getSeq());
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
			e.printStackTrace();
		}*/
	}

	@Test
	public void verificarNaturezaAgendamentoEletivaError() {

		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();

		final MbcCirurgias cirurgia = new MbcCirurgias(0);

		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.ELE);

		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		cirurgia.setUnidadeFuncional(unidadeFuncional);

		profCirurgias.setCirurgia(cirurgia);

		/* FR try {

			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedMbcCirurgiasDAO).obterPorChavePrimaria(with(any(Integer.class)));
					will(returnValue(cirurgia));

					oneOf(mockedCascaFacade).usuarioTemPermissao(with(any(String.class)), with(any(String.class)));
					will(returnValue(Boolean.FALSE));

					oneOf(mockedMbcControleEscalaCirurgicaDAO).verificaExistenciaPeviaDefinitivaPorUNFData(with(any(Short.class)), with(any(Date.class)),
							with(any(DominioTipoEscala.class)));
					will(returnValue(Boolean.TRUE)); // Teste aqui!
				}
			});

			systemUnderTest.verificarNaturezaAgendamentoEletiva(cirurgia.getSeq());
			Assert.fail(MSG_DEVERIA_OCORRER + MbcProfCirurgiasRNExceptionCode.MBC_00453);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcProfCirurgiasRNExceptionCode.MBC_00453));
		} catch (Exception e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e);
		}*/
	}

}

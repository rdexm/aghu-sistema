package br.gov.mec.aghu.blococirurgico.business;

import java.security.Identity;

import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.dao.AgfaAdtDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRefCodeDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class MbcCirurgiasRNTest extends AGHUBaseUnitTest<MbcCirurgiasVerificacoesRN> {


//	private MbcCirurgiasVerificacoesRN systemVerificacoesUnderTest;
//	private MbcCirurgiasAjusteValoresRN systemAjustesUnderTest;
	
	private MbcCirurgias cirurgia = new MbcCirurgias();
	private MbcCirurgias cirurgiaOld = new MbcCirurgias();
	
	private ICascaFacade mockedCascaFacade;
	private Identity mockedIdentity;
	
	private IAghuFacade mockedAghuFacade;
	private IParametroFacade mockedParametroFacade;
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
	private MbcRefCodeDAO mockedMbcRefCodeDAO;
	
	private MbcProfCirurgiasDAO mockedMbcProfCirurgiasDAO;
	private MbcProfAtuaUnidCirgsDAO mockedMbcProfAtuaUnidCirgsDAO;
	private MbcControleEscalaCirurgicaDAO mockedMbcControleEscalaCirurgicaDAO;
	private MbcProfCirurgiasRN mockedMbcProfCirurgiasRN;
	private MbcProcEspPorCirurgiasDAO mockedMbcProcEspPorCirurgiasDAO;
	private AgfaAdtDAO mockedAgfaAdtDAO;
	
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
//		//MbcCirurgiaRN mocks
//		mockedCascaFacade = mockingContext.mock(ICascaFacade.class);
//		mockedIdentity = mockingContext.mock(Identity.class);
//		mockedMbcControleEscalaCirurgicaDAO = mockingContext.mock(MbcControleEscalaCirurgicaDAO.class);
//		mockedMbcRefCodeDAO = mockingContext.mock(MbcRefCodeDAO.class);
//		
//		mockedAghuFacade = mockingContext.mock(IAghuFacade.class);
//		mockedSolicitacaoExameFacade = mockingContext.mock(ISolicitacaoExameFacade.class);
//		mockedParametroFacade = mockingContext.mock(IParametroFacade.class);
//		mockedMbcProfCirurgiasDAO = mockingContext.mock(MbcProfCirurgiasDAO.class);
//		mockedMbcProfAtuaUnidCirgsDAO = mockingContext.mock(MbcProfAtuaUnidCirgsDAO.class);
//		mockedMbcProfCirurgiasRN = mockingContext.mock(MbcProfCirurgiasRN.class);
//		mockedMbcProcEspPorCirurgiasDAO = mockingContext.mock(MbcProcEspPorCirurgiasDAO.class);
//		mockedAgfaAdtDAO = mockingContext.mock(AgfaAdtDAO.class);
//		
//		systemVerificacoesUnderTest = new MbcCirurgiasVerificacoesRN(){
//			private static final long serialVersionUID = 1L;
//			
//			protected ICascaFacade getCascaFacade() {
//				return mockedCascaFacade;
//			}
//			
//			protected Identity getIdentity() {
//				return mockedIdentity;
//			}
//			
//			protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
//				return mockedMbcControleEscalaCirurgicaDAO;
//			}
//			
//			protected IAghuFacade getAghuFacade() {
//				return mockedAghuFacade;
//			}
//			
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}
//			
//			protected MbcRefCodeDAO getMbcRefCodeDAO(){
//				return mockedMbcRefCodeDAO;
//			}
//			
//			protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO(){
//				return mockedMbcProcEspPorCirurgiasDAO;
//			}
//			
//			protected AgfaAdtDAO getAgfaAdtDAO(){
//				return mockedAgfaAdtDAO;
//			}
//			
//		};
//		
//		systemAjustesUnderTest = new MbcCirurgiasAjusteValoresRN(){
//			private static final long serialVersionUID = 1L;
//			
//			protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
//				return mockedSolicitacaoExameFacade;
//			}
//			
//			protected IParametroFacade getParametroFacade() {
//				return mockedParametroFacade;
//			}
//			
//			protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO(){
//				return mockedMbcProfCirurgiasDAO;								
//			}
//			
//			protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO(){
//				return mockedMbcProfAtuaUnidCirgsDAO;
//			}
//			
//			protected MbcProfCirurgiasRN getMbcProfCirurgiasRN(){
//				return mockedMbcProfCirurgiasRN;
//			}
//			
//		};
//		
//		//nova cirurgia
//		cirurgia.setDataDigitacaoNotaSala(new Date());
//		cirurgia.setSituacao(DominioSituacaoCirurgia.TRAN);
//		cirurgia.setDigitaNotaSala(Boolean.TRUE);
//		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.ELE);
//		cirurgia.setDataPrevisaoInicio(new Date());
//		cirurgia.setDataPrevisaoFim(new Date());
//		cirurgia.setDataEntradaSala(new Date());
//		cirurgia.setDataSaidaSala(new Date());
//		cirurgia.setDataInicioCirurgia(new Date());
//		cirurgia.setDataFimCirurgia(new Date());
//		
//		cirurgia.setData(new Date());
//		cirurgia.setNumeroAgenda((short)123);
//		cirurgia.setCriadoEm(new Date());
//		cirurgia.setMotivoCancelamento(new MbcMotivoCancelamento());
//		cirurgia.getMotivoCancelamento().setDestSr(Boolean.TRUE);
//		cirurgia.setTemDescricao(Boolean.FALSE);
//		cirurgia.setUtilizaO2(Boolean.TRUE);
//		cirurgia.setDataInicioAnestesia(new Date());
//		cirurgia.setDataFimAnestesia(DateUtil.adicionaHoras(new Date(), 1));
//		cirurgia.setUtilizaProAzot(Boolean.TRUE);
//		cirurgia.setTempoPrevistoHoras((short)2);
//		cirurgia.setTempoPrevistoMinutos((byte)55);
//
//		cirurgia.setSalaCirurgica(new MbcSalaCirurgica());
//		cirurgia.getSalaCirurgica().setId(new MbcSalaCirurgicaId((short)151, (short)1));
//		cirurgia.getSalaCirurgica().setSituacao(DominioSituacao.I);
//
//		cirurgia.setUnidadeFuncional(new AghUnidadesFuncionais((short)143));
//		cirurgia.getUnidadeFuncional().setTempoMaximoCirurgia((short)170);
//		cirurgia.getUnidadeFuncional().setTempoMinimoCirurgia((short)100);
//		
//		//old
//		cirurgiaOld.setDataDigitacaoNotaSala(new Date());
//		cirurgiaOld.setSituacao(DominioSituacaoCirurgia.RZDA);
//		cirurgiaOld.setDigitaNotaSala(Boolean.FALSE);
//		
//		
//		cirurgiaOld.setData(cirurgia.getData());
//		cirurgiaOld.setNumeroAgenda(cirurgia.getNumeroAgenda());
//		cirurgiaOld.setCriadoEm(cirurgia.getCriadoEm());
//		
//		
//	}
	
/*
	@Test
	public void atualizarDigitoNotaSala() {
		systemAjustesUnderTest.atualizarDigitoNotaSala(cirurgia, cirurgiaOld);
	}
	
	@Test
	public void verificarAlteracaoEletiva(){
		Boolean V_VEIO_GERA_ESCALA_D = Boolean.FALSE;
		try {
			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq((short)143);
			cirurgia.setUnidadeFuncional(unidadeFuncional);
			
			mockingContext.checking(new Expectations() {
				{
//					oneOf(mockedIdentity).getPrincipal().getName();// (with(any(MbcProfAtuaUnidCirgsId.class)));
//					will(returnValue(new Principal() {						
//						@Override
//						public String getName() {
//							return "aghu";
//						}
//					}));
//					
//					oneOf(mockedIdentity).getPrincipal().getName();// (with(any(MbcProfAtuaUnidCirgsId.class)));
//					will(returnValue(new Principal() {						
//						@Override
//						public String getName() {
//							return "aghu";
//						}
//					}));
					
					oneOf(mockedCascaFacade).usuarioTemPermissao(with(any(String.class)), with(any(String.class)));
					
					oneOf(mockedCascaFacade).usuarioTemPermissao(with(any(String.class)), with(any(String.class)));
					oneOf(mockedMbcControleEscalaCirurgicaDAO).verificaExistenciaPeviaDefinitivaPorUNFData(with(any(Short.class)), with(any(Date.class)), with(any(DominioTipoEscala.class)));
					will(returnValue(true));
				}
			});
			
			systemVerificacoesUnderTest.verificarAlteracaoEletiva(cirurgia, V_VEIO_GERA_ESCALA_D);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00438));
		}
	}

	@Test
	public void verificarDataPrevisaoInicio(){
		systemAjustesUnderTest.verificarDataPrevisaoInicio(cirurgia);
	}

	@Test
	public void verificarDataPrevisaoFim(){
		systemAjustesUnderTest.verificarDataPrevisaoFim(cirurgia);
	}
	
	@Test
	public void verificarObrigacaoNotaSala(){
		try {
			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq((short)143);
			unidadeFuncional.getCaracteristicas().add(new AghCaractUnidFuncionais(new AghCaractUnidFuncionaisId(null, ConstanteAghCaractUnidFuncionais.BLOCO)));

			cirurgia.setUnidadeFuncional(unidadeFuncional);

			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedAghuFacade).obterUnidadeFuncional(with(any(Short.class)));
					will(returnValue(unidadeFuncional));
					
					oneOf(mockedAghuFacade).verificarCaracteristicaUnidadeFuncional(with(any(Short.class)),with(any(ConstanteAghCaractUnidFuncionais.class)));
					will(returnValue(true));
				}
			});

			systemVerificacoesUnderTest.verificarObrigacaoNotaSala(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
	
	@Test
	public void atualizarUltDigitacaoNotaSala(){
		try {
			systemAjustesUnderTest.atualizarUltDigitacaoNotaSala(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
	
	@Test
	public void verificarCamposNaoAlteraveis(){
		try {
			systemVerificacoesUnderTest.verificarCamposNaoAlteraveis(cirurgia, cirurgiaOld);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
	
	@Test
	public void verificarSituacaoCancelada(){
		try {
			systemAjustesUnderTest.verificarSituacaoCancelada(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
	
	@Test
	public void varificaCancelamento(){
		try {
			cirurgiaOld.setSituacao(DominioSituacaoCirurgia.CANC);
			
			systemVerificacoesUnderTest.varificaCancelamento(cirurgia, cirurgiaOld);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00318));
		}
	}
	
	@Test
	public void atualizarInformacaoSituacao(){
		try {
			systemAjustesUnderTest.atualizarInformacaoSituacao(cirurgia, cirurgiaOld);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void atualizarTempoO2(){
		try {
			systemAjustesUnderTest.atualizarTempoO2(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void atualizarTempoAZ(){
		try{
			systemAjustesUnderTest.atualizarTempoAZ(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
	
	@Test
	public void verificarSalaCirurgica(){
		try{
			systemVerificacoesUnderTest.verificarSalaCirurgica(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00348));
		}
	}
	
	@Test
	public void verificarSalaCirurgicaAtiva(){
		try{
			systemVerificacoesUnderTest.verificarSalaCirurgicaAtiva(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_01326));
		}
	}
	
	@Test
	public void verificarTempoCirurgia(){
		try{

			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedAghuFacade).obterAghUnidadesFuncionaisPorChavePrimaria(with(any(Short.class)));
				}
			});
			
			systemVerificacoesUnderTest.verificarTempoCirurgia(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00368));
		}
	}
	
	@Test
	public void verificarConvenio(){
		try{
			systemVerificacoesUnderTest.verificarConvenio(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00219));
		}
	}
	
	@Test
	public void verificarDemoraSR(){
		try{
			cirurgia.setMotivoDemoraSalaRecuperacao(new MbcMotivoDemoraSalaRec());
			systemVerificacoesUnderTest.verificarDemoraSR(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00223));
		}
	}
	
	
	@Test
	public void verificarAtraso(){
		try {
			systemVerificacoesUnderTest.verificarAtraso(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void verificarMotivoCancelamento(){
		try {
			cirurgia.getMotivoCancelamento().setSituacao(DominioSituacao.I);
			systemVerificacoesUnderTest.verificarMotivoCancelamento(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00222));
		}
	}

	@Test
	public void verificarDestino(){
		try {
			cirurgia.setDestinoPaciente(new MbcDestinoPaciente());
			cirurgia.getDestinoPaciente().setSituacao(DominioSituacao.I);
			systemVerificacoesUnderTest.verificarDestino(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00242));
		}
	}

*/
	@Test
	public void atualizarSolicitacoesExames(){
		/* FR try {
			systemVerificacoesUnderTest.atualizarSolicitacoesExames(cirurgiaOld, "nomecomp");
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}*/
		
	}
	
	
/*
	@Test
	public void atualizarProcPaciente(){
		try {
			cirurgia.setPaciente(new AipPacientes());
			cirurgia.getPaciente().setDtUltProcedimento(new Date());
			
			systemAjustesUnderTest.atualizarProcPaciente(cirurgia, cirurgiaOld);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void cancelarQuimio(){
		try {
			systemAjustesUnderTest.cancelarQuimio(cirurgia, cirurgiaOld);
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void atualizarCancelamento(){
		try {
			systemAjustesUnderTest.atualizarCancelamento(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
*/
	@Test
	public void atualizarAtendimentoSolicitacaoExames(){
		/* FR try {
			
			cirurgia.setAtendimento(new AghAtendimentos());
			cirurgia.getAtendimento().setDthrInicio(new Date());
			
			cirurgiaOld.setAtendimento(new AghAtendimentos());
			
			AelSolicitacaoExames solicOld = new AelSolicitacaoExames();
			solicOld.setCriadoEm(DateUtil.adicionaMinutos(new Date(), 15));
			cirurgiaOld.getAtendimento().getAelSolicitacaoExames().add(solicOld);
			
				mockingContext.checking(new Expectations() {
					{
						oneOf(mockedSolicitacaoExameFacade).atualizar(with(any(AelSolicitacaoExames.class)), with(any(List.class)), with(any(String.class)));
					}
				});
			
			systemAjustesUnderTest.atualizarAtendimentoSolicitacaoExames(cirurgia, cirurgiaOld, "nomecomp");
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}*/
	}

	@Test
	public void atualizarAgendaCancelamento(){
		/* FR try {
			cirurgia.setConvenioSaudePlano(new FatConvenioSaudePlano(new FatConvenioSaudePlanoId((short)1,(byte)1)));
			cirurgia.getUnidadeFuncional().setIntervaloEscalaCirurgia((byte)30);
			cirurgia.getUnidadeFuncional().setIntervaloEscalaProced((byte)30);
			
			MbcProfCirurgias profCirur = new MbcProfCirurgias();
			profCirur.setIndResponsavel(true);
			cirurgia.setProfCirurgias(new HashSet<MbcProfCirurgias>());
			cirurgia.getProfCirurgias().add(profCirur);
			
			final AghParametros parametroSusPadrao = new AghParametros(new BigDecimal(1));
			final AghParametros parametroMotivoDesmarcar = new AghParametros();
			final AghParametros parametroMotivoDesmarcarAdm = new AghParametros();

			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class)));
					will(returnValue(parametroSusPadrao));

					oneOf(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class)));
					will(returnValue(parametroMotivoDesmarcar));

					oneOf(mockedParametroFacade).buscarAghParametro(with(any(AghuParametrosEnum.class)));
					will(returnValue(parametroMotivoDesmarcarAdm));
				}
			});
			
			
			systemAjustesUnderTest.atualizarAgendaCancelamento(cirurgia);
		} catch (BaseException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_00977));
		}*/
	}

	@Test
	public void atualizarAgendaBanc(){
		/* FR try {
			systemAjustesUnderTest.atualizarAgendaBanc(cirurgia, cirurgiaOld, Boolean.TRUE);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}*/
	}
/*
	@Test
	public void veriricarAlteracaoUnidadeCirurgica(){
		try {
			
			mockingContext.checking(new Expectations() {
				{
//					oneOf(mockedIdentity).getPrincipal().getName();// (with(any(MbcProfAtuaUnidCirgsId.class)));
//					will(returnValue(new Principal() {						
//						@Override
//						public String getName() {
//							return "aghu";
//						}
//					}));

					oneOf(mockedCascaFacade).usuarioTemPermissao(with(any(String.class)), with(any(String.class)));
					will(returnValue(true));
				}
			});

			systemVerificacoesUnderTest.verificarAlteracaoUnidadeCirurgica(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
*/
	@Test
	public void atualizarUnidadeProfissional(){
		/* FR try {
			
			final MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
			profCirurgias.setMbcProfAtuaUnidCirgs(new MbcProfAtuaUnidCirgs());
			profCirurgias.getMbcProfAtuaUnidCirgs().setRapServidores(new RapServidores());
			
			cirurgia.setSeq(12);
			
			cirurgiaOld.setUnidadeFuncional(new AghUnidadesFuncionais((short)143));
			
			
			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedMbcProfCirurgiasDAO).obterEquipePorCirurgiaUnidade(with(any(Integer.class)), with(any(Short.class)));
					will(returnValue(profCirurgias));

					oneOf(mockedMbcProfAtuaUnidCirgsDAO).pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(with(any(RapServidores.class)), with(any(Short.class)));

					oneOf(mockedMbcProfCirurgiasDAO).obterEquipePorCirurgiaUnidade(with(any(Integer.class)), with(any(Short.class)));
					will(returnValue(profCirurgias));

					oneOf(mockedMbcProfCirurgiasRN).removerMbcProfCirurgias(with(any(MbcProfCirurgias.class)), with(any(Boolean.class)));
					
					oneOf(mockedMbcProfCirurgiasRN).inserirMbcProfCirurgias(with(any(MbcProfCirurgias.class)), with(any(Boolean.class)), with(any(Boolean.class)));
				}
			});

			systemAjustesUnderTest.atualizarUnidadeProfissional(cirurgia, cirurgiaOld, Boolean.FALSE);
		} catch (BaseException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}*/
	}

/*
	@Test
	public void verificarProjetoPesquisa(){
		try {
			systemVerificacoesUnderTest.verificarProjetoPesquisa(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void atualizarCotaProcedimentos(){
		try {
			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedMbcProcEspPorCirurgiasDAO).atualizarCotaProcedimetosCirurgiaCallableStatement(with(any(Integer.class)), with(any(Integer.class)));					
				}
			});

			systemVerificacoesUnderTest.atualizarCotaProcedimentos(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}

	@Test
	public void atualizarSistemaImagensAGFA(){
		try {
			
			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedAgfaAdtDAO).atualizarInformacoesPacienteAGFACallableStatement(with(any(Integer.class)), with(any(Integer.class)), with(any(Integer.class)));
				}
			});

			systemVerificacoesUnderTest.atualizarSistemaImagensAGFA(cirurgia, cirurgiaOld);
		} catch (ApplicationBusinessException e) {
			Assert.fail(MSG_NAO_DEVERIA_OCORRER + e.getCode());
		}
	}
 
	@Test
	public void verificarCirurgiaRegime(){
		try {
			mockingContext.checking(new Expectations() {
				{
					oneOf(mockedMbcProcEspPorCirurgiasDAO).listarMbcProcEspPorCirurgiasPorCrgSeq(with(any(Integer.class)), with(any(Boolean.class)), with(any(Boolean.class)), with(any(Boolean.class)));
				}
			});
			
			cirurgia.setConvenioSaude(new FatConvenioSaude());
			cirurgia.getConvenioSaude().setGrupoConvenio(DominioGrupoConvenio.S);
			
			cirurgia.setProcEspPorCirurgias(new HashSet<MbcProcEspPorCirurgias>());
			
			MbcProcEspPorCirurgias procEspCirur = new MbcProcEspPorCirurgias();
			MbcProcedimentoCirurgicos procedimento = new MbcProcedimentoCirurgicos();
			procedimento.setRegimeProcedSus(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO_ATE_72H);
			procEspCirur.setProcedimentoCirurgico(procedimento);
			cirurgia.getProcEspPorCirurgias().add(procEspCirur);
			cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.A);
			
			systemVerificacoesUnderTest.verificarCirurgiaRegime(cirurgia);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode().equals(MbcCirurgiasRNExceptionCode.MBC_01826));
		}
	}
*/
}

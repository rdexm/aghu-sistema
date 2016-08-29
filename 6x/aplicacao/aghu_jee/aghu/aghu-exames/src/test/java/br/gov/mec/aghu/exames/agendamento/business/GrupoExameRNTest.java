package br.gov.mec.aghu.exames.agendamento.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.GrupoExameRN.GrupoExameRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExamesDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GrupoExameRNTest extends AGHUBaseUnitTest<GrupoExameRN>{

	@Mock
	private AelGrupoExamesDAO mockedAelGrupoExameDAO;
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelGradeAgendaExameDAO mockedGradeAgendaExameDAO;
	
	@Test
	public void testVerificarDelecaoSuccess() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		final Date dataCriadoEm = cal.getTime();
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));
		
		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);
			
			systemUnderTest.executarBeforeDeleteGrupoExame(dataCriadoEm);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testVerificarDelecaoError() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -12);
		final Date dataCriadoEm = cal.getTime();
		final AghParametros paramDiasDel = new AghParametros();
		paramDiasDel.setVlrNumerico(new BigDecimal(300));
		
		try {
			Mockito.when(mockedParametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL)).thenReturn(paramDiasDel);
			
			systemUnderTest.executarBeforeDeleteGrupoExame(dataCriadoEm);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00343);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError01() {
		try {
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(new AelGrupoExameUnidExame());
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00470);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError02_1() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.I);
			executaExame.setAelExamesMaterialAnalise(new AelExamesMaterialAnalise());
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.ERRO_AEL_00506);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError02_2() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.I);
			executaExame.setDthrReativaTemp(new Date());
			executaExame.setAelExamesMaterialAnalise(new AelExamesMaterialAnalise());
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.ERRO_AEL_00506_DATA);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError02_3() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.I);
			executaExame.setMotivoDesativacao("DESATIVADO");
			executaExame.setAelExamesMaterialAnalise(new AelExamesMaterialAnalise());
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.ERRO_AEL_00506_MOTIVO);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError02_4() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.I);
			executaExame.setMotivoDesativacao("DESATIVADO");
			executaExame.setDthrReativaTemp(new Date());
			executaExame.setAelExamesMaterialAnalise(new AelExamesMaterialAnalise());
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.ERRO_AEL_00506_DATA_MOTIVO);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError03() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.A);
			executaExame.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
			executaExame.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.N);
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00831);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError04() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.A);
			executaExame.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
			executaExame.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.S);
			
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId();
			id.setGexSeq(Integer.valueOf(25));
			
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setId(id);
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			Mockito.when(mockedAelGrupoExameDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(null);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00503);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError05() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.A);
			executaExame.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
			executaExame.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.S);
			
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId();
			id.setGexSeq(Integer.valueOf(25));
			
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setId(id);
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			final AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setSituacao(DominioSituacao.I);
			
			Mockito.when(mockedAelGrupoExameDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(grupoExame);

			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00504);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameError06() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.A);
			executaExame.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
			executaExame.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.S);
			
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId();
			id.setGexSeq(Integer.valueOf(25));
			id.setUfeUnfSeq(Short.valueOf("13"));
			
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setId(id);
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq(Short.valueOf("15"));
			final AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setSituacao(DominioSituacao.A);
			grupoExame.setUnidadeFuncional(unidadeFuncional);
			
			Mockito.when(mockedAelGrupoExameDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(grupoExame);

			Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Short.valueOf("13"), ConstanteAghCaractUnidFuncionais.AREA_FECHADA))
			.thenReturn(false);

			Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Short.valueOf("15"), ConstanteAghCaractUnidFuncionais.AREA_FECHADA))
			.thenReturn(true);
			
			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00505);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameUnidExameSuccess() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.A);
			executaExame.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.N);
			executaExame.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.S);
			
			AelGrupoExameUnidExameId id = new AelGrupoExameUnidExameId();
			id.setGexSeq(Integer.valueOf(25));
			id.setUfeUnfSeq(Short.valueOf("13"));
			
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setId(id);
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			
			AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq(Short.valueOf("13"));
			final AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setSituacao(DominioSituacao.A);
			grupoExame.setUnidadeFuncional(unidadeFuncional);
			
			Mockito.when(mockedAelGrupoExameDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(grupoExame);

			Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Short.valueOf("13"), ConstanteAghCaractUnidFuncionais.AREA_FECHADA))
			.thenReturn(true);

			Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Short.valueOf("13"), ConstanteAghCaractUnidFuncionais.AREA_FECHADA))
			.thenReturn(true);

			systemUnderTest.executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateGrupoExameUnidExameSuccess() {
		try {
			AelUnfExecutaExames executaExame = new AelUnfExecutaExames();
			executaExame.setIndSituacao(DominioSituacao.A);
			executaExame.setIndAgendamPrevioInt(DominioSimNaoRestritoAreaExecutora.S);
			executaExame.setIndAgendamPrevioNaoInt(DominioSimNaoRestritoAreaExecutora.N);
			AelGrupoExameUnidExame grupoExameUnidExame = new AelGrupoExameUnidExame();
			grupoExameUnidExame.setUnfExecutaExame(executaExame);
			grupoExameUnidExame.setSituacao(DominioSituacao.A);
			
			systemUnderTest.executarBeforeUpdateGrupoExameUnidExame(grupoExameUnidExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameError01() {
		try {
			AelGrupoExames grupoExame = new AelGrupoExames();
			systemUnderTest.executarBeforeInsertGrupoExame(grupoExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00353);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameError02() {
		try {
			AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq(Short.valueOf("15"));
			AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setUnidadeFuncional(unidadeFuncional);
			grupoExame.setServidor(new RapServidores());
			
			Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(null);

			systemUnderTest.executarBeforeInsertGrupoExame(grupoExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00370);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameError03() {
		try {
			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq(Short.valueOf("15"));
			unidadeFuncional.setIndSitUnidFunc(DominioSituacao.I);
			AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setUnidadeFuncional(unidadeFuncional);
			grupoExame.setServidor(new RapServidores());
			
			Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeFuncional);

			systemUnderTest.executarBeforeInsertGrupoExame(grupoExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00371);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameError04() {
		try {
			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq(Short.valueOf("15"));
			unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
			AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setUnidadeFuncional(unidadeFuncional);
			grupoExame.setServidor(new RapServidores());
			
			Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeFuncional);

			systemUnderTest.executarBeforeInsertGrupoExame(grupoExame);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00677);
		}
	}
	
	@Test
	public void testExecutarBeforeInsertGrupoExameSuccess() {
		try {
			AghCaractUnidFuncionaisId idCaract = new AghCaractUnidFuncionaisId();
			idCaract.setCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_COLETA);
			AghCaractUnidFuncionais caract = new AghCaractUnidFuncionais();
			caract.setId(idCaract);
			Set<AghCaractUnidFuncionais> listaCaract = new HashSet<AghCaractUnidFuncionais>();
			listaCaract.add(caract);
			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
			unidadeFuncional.setSeq(Short.valueOf("15"));
			unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
			unidadeFuncional.setCaracteristicas(listaCaract);
			AelGrupoExames grupoExame = new AelGrupoExames();
			grupoExame.setUnidadeFuncional(unidadeFuncional);
			grupoExame.setServidor(new RapServidores());
			
			Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeFuncional);

			systemUnderTest.executarBeforeInsertGrupoExame(grupoExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Exceção gerada: " + e.getCode());
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateGrupoExameError01() {
		try {
			AelGrupoExames grupoExameNew = new AelGrupoExames();
			grupoExameNew.setDescricao("Descrição New");
			
			AelGrupoExames grupoExameOld = new AelGrupoExames();
			grupoExameOld.setDescricao("Descrição Old");
			
			systemUnderTest.executarBeforeUpdateGrupoExame(grupoExameNew, grupoExameOld);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00346);
		}
	}
	
	@Test
	public void testExecutarBeforeUpdateGrupoExameError02() {
		try {
			Date criadoEm = new Date();
			RapServidores servidor = new RapServidores();
			AelGrupoExames grupoExameNew = new AelGrupoExames();
			grupoExameNew.setDescricao("Descrição");
			grupoExameNew.setCriadoEm(criadoEm);
			grupoExameNew.setServidor(null);
			
			AelGrupoExames grupoExameOld = new AelGrupoExames();
			grupoExameOld.setDescricao("Descrição");
			grupoExameOld.setCriadoEm(criadoEm);
			grupoExameOld.setServidor(servidor);
			
			systemUnderTest.executarBeforeUpdateGrupoExame(grupoExameNew, grupoExameOld);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00369);
		}
	}
	
//	@Test
//	public void testExecutarBeforeUpdateGrupoExameError03() {
//		try {
//			AghCaractUnidFuncionaisId idCaract = new AghCaractUnidFuncionaisId();
//			idCaract.setCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_COLETA);
//			AghCaractUnidFuncionais caract = new AghCaractUnidFuncionais();
//			caract.setId(idCaract);
//			Set<AghCaractUnidFuncionais> listaCaract = new HashSet<AghCaractUnidFuncionais>();
//			listaCaract.add(caract);
//			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
//			unidadeFuncional.setSeq(Short.valueOf("15"));
//			unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
//			unidadeFuncional.setCaracteristicas(listaCaract);
//			
//			Date criadoEm = new Date();
//			RapServidoresId servId = new RapServidoresId();
//			servId.setMatricula(Integer.valueOf(3));
//			servId.setVinCodigo(Short.valueOf("4"));
//			RapServidores servidor = new RapServidores();
//			servidor.setId(servId);
//			final AelGrupoExames grupoExameNew = new AelGrupoExames();
//			grupoExameNew.setDescricao("Descrição");
//			grupoExameNew.setCriadoEm(criadoEm);
//			grupoExameNew.setServidor(servidor);
//			grupoExameNew.setUnidadeFuncional(unidadeFuncional);
//			grupoExameNew.setSituacao(DominioSituacao.I);
//			
//			AelGrupoExames grupoExameOld = new AelGrupoExames();
//			grupoExameOld.setDescricao("Descrição");
//			grupoExameOld.setCriadoEm(criadoEm);
//			grupoExameOld.setServidor(servidor);
//			grupoExameOld.setUnidadeFuncional(new AghUnidadesFuncionais());
//			grupoExameOld.setSituacao(DominioSituacao.I);
//			
//			mockingContext.checking(new Expectations() {{
//				oneOf(mockedGradeAgendaExameDAO).pesquisarGradeExame(null, true, null, null, null, null, grupoExameNew, null, null);
//				will(returnValue(null));
//			}});
//			
//			systemUnderTest.executarBeforeUpdateGrupoExame(grupoExameNew, grupoExameOld);
//		} catch (ApplicationBusinessException e) {
//			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_00516);
//		}
//	}
//	
//	@Test
//	public void testExecutarBeforeUpdateGrupoExameError04() {
//		try {
//			AghCaractUnidFuncionaisId idCaract = new AghCaractUnidFuncionaisId();
//			idCaract.setCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_COLETA);
//			AghCaractUnidFuncionais caract = new AghCaractUnidFuncionais();
//			caract.setId(idCaract);
//			Set<AghCaractUnidFuncionais> listaCaract = new HashSet<AghCaractUnidFuncionais>();
//			listaCaract.add(caract);
//			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
//			unidadeFuncional.setSeq(Short.valueOf("15"));
//			unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
//			unidadeFuncional.setCaracteristicas(listaCaract);
//			
//			Date criadoEm = new Date();
//			RapServidoresId servId = new RapServidoresId();
//			servId.setMatricula(Integer.valueOf(3));
//			servId.setVinCodigo(Short.valueOf("4"));
//			RapServidores servidor = new RapServidores();
//			servidor.setId(servId);
//			
//			final AelGradeAgendaExame grade = new AelGradeAgendaExame();
//			grade.setSituacao(DominioSituacao.A);
//			Set<AelGradeAgendaExame> listaGrades = new HashSet<AelGradeAgendaExame>();
//			listaGrades.add(grade);
//			
//			final AelGrupoExames grupoExameNew = new AelGrupoExames();
//			grupoExameNew.setDescricao("Descrição");
//			grupoExameNew.setCriadoEm(criadoEm);
//			grupoExameNew.setServidor(servidor);
//			grupoExameNew.setUnidadeFuncional(unidadeFuncional);
//			grupoExameNew.setSituacao(DominioSituacao.I);
//			grupoExameNew.setGradeAgendaExame(listaGrades);
//			
//			AelGrupoExames grupoExameOld = new AelGrupoExames();
//			grupoExameOld.setDescricao("Descrição");
//			grupoExameOld.setCriadoEm(criadoEm);
//			grupoExameOld.setServidor(servidor);
//			grupoExameOld.setUnidadeFuncional(new AghUnidadesFuncionais());
//			grupoExameOld.setSituacao(DominioSituacao.I);
//			
//			systemUnderTest.executarBeforeUpdateGrupoExame(grupoExameNew, grupoExameOld);
//		} catch (ApplicationBusinessException e) {
//			Assert.assertEquals(e.getCode(), GrupoExameRNExceptionCode.AEL_01036);
//		}
//	}
//	
//	@Test
//	public void testExecutarBeforeUpdateGrupoExameSuccess() {
//		try {
//			AghCaractUnidFuncionaisId idCaract = new AghCaractUnidFuncionaisId();
//			idCaract.setCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_COLETA);
//			AghCaractUnidFuncionais caract = new AghCaractUnidFuncionais();
//			caract.setId(idCaract);
//			Set<AghCaractUnidFuncionais> listaCaract = new HashSet<AghCaractUnidFuncionais>();
//			listaCaract.add(caract);
//			final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
//			unidadeFuncional.setSeq(Short.valueOf("15"));
//			unidadeFuncional.setIndSitUnidFunc(DominioSituacao.A);
//			unidadeFuncional.setCaracteristicas(listaCaract);
//			
//			Date criadoEm = new Date();
//			RapServidoresId servId = new RapServidoresId();
//			servId.setMatricula(Integer.valueOf(3));
//			servId.setVinCodigo(Short.valueOf("4"));
//			RapServidores servidor = new RapServidores();
//			servidor.setId(servId);
//			
//			final AelGradeAgendaExame grade = new AelGradeAgendaExame();
//			grade.setSituacao(DominioSituacao.I);
//			Set<AelGradeAgendaExame> listaGrades = new HashSet<AelGradeAgendaExame>();
//			listaGrades.add(grade);
//			
//			final AelGrupoExames grupoExameNew = new AelGrupoExames();
//			grupoExameNew.setDescricao("Descrição");
//			grupoExameNew.setCriadoEm(criadoEm);
//			grupoExameNew.setServidor(servidor);
//			grupoExameNew.setUnidadeFuncional(unidadeFuncional);
//			grupoExameNew.setSituacao(DominioSituacao.I);
//			grupoExameNew.setGradeAgendaExame(listaGrades);
//			
//			AelGrupoExames grupoExameOld = new AelGrupoExames();
//			grupoExameOld.setDescricao("Descrição");
//			grupoExameOld.setCriadoEm(criadoEm);
//			grupoExameOld.setServidor(servidor);
//			grupoExameOld.setUnidadeFuncional(new AghUnidadesFuncionais());
//			grupoExameOld.setSituacao(DominioSituacao.I);
//			
//			systemUnderTest.executarBeforeUpdateGrupoExame(grupoExameNew, grupoExameOld);
//		} catch (ApplicationBusinessException e) {
//			Assert.fail("Exceção gerada: " + e.getCode());
//		}
//	}
	
}

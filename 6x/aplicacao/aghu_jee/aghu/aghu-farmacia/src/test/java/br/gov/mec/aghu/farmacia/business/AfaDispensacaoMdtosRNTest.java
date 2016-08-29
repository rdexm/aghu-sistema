package br.gov.mec.aghu.farmacia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AfaDispensacaoMdtosRNTest extends AGHUBaseUnitTest<AfaDispensacaoMdtosRN> {

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AfaDispensacaoMdtosDAO mockedAfaDispensacaoMdtosDAO;
	@Mock
	private AfaDispMdtoCbSpsDAO mockedAfaDispMdtoCbSpsDAO;
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	private Short SEQ_TIPO_OCOR_MEDICAMENTO_EM_GELADEIRA = Short.valueOf("4");

	@Test
	public void rnDsmpVerAltera() {
		try {
			systemUnderTest.rnDsmpVerAltera(DominioSituacaoDispensacaoMdto.E);
			Assert.fail("Deveria ter lançado exceção =>" + "AFA_00260");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00260);
		}
	}

	@Test
	public void rnDsmpVerAltQtdeSituacaoD() {
		Integer qtdeEtiquetasLidas = 52;
		esperaObterAfaDispMdtoCbSpsDAO(qtdeEtiquetasLidas);

		try {
			DominioSituacaoDispensacaoMdto resultadoObtido = systemUnderTest.rnDsmpVerAltQtde(1323l, new BigDecimal(51), new BigDecimal(
					qtdeEtiquetasLidas), DominioSituacaoDispensacaoMdto.D);
			DominioSituacaoDispensacaoMdto resultadoEsperado = DominioSituacaoDispensacaoMdto.D;
			Assert.assertEquals(resultadoEsperado, resultadoObtido);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NÃO deveria ter lançado exceção");
		}
	}

	@Test
	public void rnDsmpVerAltQtdeSituacaoT() {
		Integer qtdeEtiquetasLidas = 42;
		esperaObterAfaDispMdtoCbSpsDAO(qtdeEtiquetasLidas);

		try {
			DominioSituacaoDispensacaoMdto resultadoObtido = systemUnderTest.rnDsmpVerAltQtde(1323l, new BigDecimal(qtdeEtiquetasLidas),
					new BigDecimal(842), DominioSituacaoDispensacaoMdto.D);
			DominioSituacaoDispensacaoMdto resultadoEsperado = DominioSituacaoDispensacaoMdto.T;
			Assert.assertEquals(resultadoEsperado, resultadoObtido);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deveria ter lançado exceção");
		}
	}

	@Test
	public void rnDsmpVerAltQtdeSituacaoException() {
		Integer qtdeEtiquetasLidas = 42;
		esperaObterAfaDispMdtoCbSpsDAO(qtdeEtiquetasLidas);

		try {
			systemUnderTest.rnDsmpVerAltQtde(1323l, new BigDecimal(qtdeEtiquetasLidas), BigDecimal.TEN, DominioSituacaoDispensacaoMdto.D);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_01473);
		}
	}

	@Test
	public void rnDsmpVerSerBuscTest() {
		RapServidores serv = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(123456);
		id.setVinCodigo(Short.valueOf("32145"));
		serv.setId(id);
		serv.setIndSituacao(DominioSituacaoVinculo.I);
		try {
			systemUnderTest.rnDsmpVerSerBusc(serv);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00253);
		}
	}

	@Test
	public void rnAfacVerSerAtivTestException() {
		try {
			systemUnderTest.rnAfacVerSerAtiv(null);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00169);
		}
	}

	@Test
	public void rnAfacVerSerAtivTestTRUE() {
		RapServidores serv = new RapServidores();
		serv.setIndSituacao(DominioSituacaoVinculo.A);
		try {
			Boolean resultadoObtido = systemUnderTest.rnAfacVerSerAtiv(serv);
			Boolean resultadoEsperado = Boolean.TRUE;
			Assert.assertEquals(resultadoEsperado, resultadoObtido);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NÃO Deveria ter lançado exceção");
		}
	}

	@Test
	public void rnAfacVerSerAtivTestFALSE() {
		RapServidores serv = new RapServidores();
		serv.setIndSituacao(DominioSituacaoVinculo.I);
		try {
			Boolean resultadoObtido = systemUnderTest.rnAfacVerSerAtiv(serv);
			Boolean resultadoEsperado = Boolean.FALSE;
			Assert.assertEquals(resultadoEsperado, resultadoObtido);
		} catch (ApplicationBusinessException e) {
			Assert.fail("NÃO Deveria ter lançado exceção");
		}
	}

	@Test
	public void rnDsmpVerSitAltTest() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraQtdeDispensada(getListaDispensacaoMdto(), 0);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(0), listaOriginal.get(0), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00298);
		}
	}

	@Test
	public void rnDsmpVerAltSitTest() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraSituacao(getListaDispensacaoMdto(), 0, DominioSituacaoDispensacaoMdto.S);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(0), listaOriginal.get(0), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00299);
		}
	}

	@Test
	public void rnDsmpVerTodAtivTestException1() {
		AfaTipoOcorDispensacao tod = new AfaTipoOcorDispensacao();
		try {
			systemUnderTest.rnDsmpVerTodAtiv(tod);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00284);
		}
	}

	@Test
	public void rnDsmpVerTodAtivTestException2() {
		AfaTipoOcorDispensacao tod = new AfaTipoOcorDispensacao();
		tod.setSituacao(DominioSituacao.I);
		try {
			systemUnderTest.rnDsmpVerTodAtiv(tod);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00285);
		}
	}

	@Test
	public void rnDsmpVerUnfDispTestException1() {
		try {
			systemUnderTest.rnDsmpVerUnfDisp(null);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.MPM_00938);
		}
	}

	@Test
	public void rnDsmpVerUnfDispTestException2() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
		unf.setIndSitUnidFunc(DominioSituacao.I);
		try {
			systemUnderTest.rnDsmpVerUnfDisp(unf);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.MPM_00778);
		}
	}

	@Test
	public void rnDsmpVerUnfDispTestException3() {
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
		unf.setIndSitUnidFunc(DominioSituacao.A);
		unf.setSeq(Short.valueOf("3"));
		esperarUnidadeFuncionalPossuiCaracteristicaFalse();
		try {
			systemUnderTest.rnDsmpVerUnfDisp(unf);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00286);
		}
	}

	@Test
	public void aFA00445() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraSituacao(getListaDispensacaoMdto(), 1, DominioSituacaoDispensacaoMdto.D);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			whenObterServidorLogadoNull();
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(1), listaOriginal.get(1), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00445);
		}
	}

	@Test
	public void aFA01493() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraSituacao(getListaDispensacaoMdto(), 1, DominioSituacaoDispensacaoMdto.T);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(1), listaOriginal.get(1), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_01493);
		}
	}

	@Test
	public void aFA01494() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraSituacao(getListaDispensacaoMdto(), 2, DominioSituacaoDispensacaoMdto.D);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			whenObterServidorLogadoNull();
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(2), listaOriginal.get(2), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_01494);
		}
	}

	@Test
	public void aFA00446() {
		List<AfaDispensacaoMdtos> listaOriginal = alteraSituacao(getListaDispensacaoMdto(), 3, DominioSituacaoDispensacaoMdto.C);
		List<AfaDispensacaoMdtos> listaAlterada = alteraSituacao(getListaDispensacaoMdto(), 3, DominioSituacaoDispensacaoMdto.C);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			whenObterServidorLogado();
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(3), listaOriginal.get(3), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00446);
		}
	}

	@Test
	public void aFA00447Exception1() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraSituacao(getListaDispensacaoMdto(), 0, DominioSituacaoDispensacaoMdto.E);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			whenObterServidorLogadoNull();
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(0), listaOriginal.get(0), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00447);
		}
	}

	@Test
	public void aFA01234() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraQtdeEstornada(getListaDispensacaoMdto(), 2);
		listaAlterada = alteraSituacao(listaAlterada, 2, DominioSituacaoDispensacaoMdto.S);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(2), listaOriginal.get(2), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_01234);
		}
	}

	@Test
	public void aFA00447Exception2() {
		List<AfaDispensacaoMdtos> listaOriginal = getListaDispensacaoMdto();
		List<AfaDispensacaoMdtos> listaAlterada = alteraQtdeEstornada(getListaDispensacaoMdto(), 0);
		listaAlterada = alteraSituacao(listaAlterada, 0, DominioSituacaoDispensacaoMdto.D);
		esperaObterAfaDispMdtoCbSpsDAO(0);
		try {
			whenObterServidorLogadoNull();
			systemUnderTest.atualizaAfaDispMdto(listaAlterada.get(0), listaOriginal.get(0), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00447);
		}
	}

	/**
	 * Teste para ORADB PROCEDURE afak_dsm_rn.RN_DSMP_ATU_UNF_SOL
	 */
	@Test
	public void atualizarUnidadeSolicitanteComAtendimento() {

		AfaDispensacaoMdtos dispMdto = new AfaDispensacaoMdtos();
		Integer atdSeq = 123;
		esperaObterAghAtendimentosPopulado();
		try {
			dispMdto = systemUnderTest.atualizarUnidadeSolicitante(dispMdto, atdSeq);
			Assert.assertNotNull(dispMdto.getUnidadeFuncionalSolicitante());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deve lancar excecao");
		}
	}

	/**
	 * Teste para ORADB PROCEDURE afak_dsm_rn.RN_DSMP_ATU_UNF_SOL
	 */
	@Test
	public void atualizarUnidadeSolicitanteSemAtendimento() {

		AfaDispensacaoMdtos dispMdto = new AfaDispensacaoMdtos();
		Integer atdSeq = 456;
		esperaObterAghAtendimentosNulo();
		try {
			dispMdto = systemUnderTest.atualizarUnidadeSolicitante(dispMdto, atdSeq);
			Assert.assertNull(dispMdto.getUnidadeFuncionalSolicitante());
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não Deve lancar excecao");
		}
	}

	/**
	 * Teste para ORADB PROCEDURE AFAK_DSM_RN.RN_DSMP_VER_DELECAO
	 */
	@Test
	public void testelancarExcecaoPermissaoExclusao() {
		try {
			systemUnderTest.lancarExcecaoPermissaoExclusao();
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00258);
		}
	}

	/**
	 * ORADB Trigger "AGH".AFAT_DSM_BRI Exception AFA_00444
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testecriaDispMdtoTriagemPrescricaoExceptionAFA_00444() throws BaseException {
		AfaDispensacaoMdtos admNew = criaDispensacaoMdtoDispensadoSemServidor();
		admNew.setItemPrescricaoMdto(new MpmItemPrescricaoMdto());
		admNew.getItemPrescricaoMdto().setId(new MpmItemPrescricaoMdtoId());
		esperarUnidadeFuncionalPossuiCaracteristicaTrue();
		try {
			whenObterServidorLogadoNull();
			systemUnderTest.criaDispMdtoTriagemPrescricao(admNew, NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção AFA_00444");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.AFA_00444);
		}
	}

	private AfaDispensacaoMdtos criaDispensacaoMdtoDispensadoSemServidor() {
		AfaDispensacaoMdtos admNew = new AfaDispensacaoMdtos();
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
		unf.setSeq(Short.valueOf("1"));
		unf.setIndSitUnidFunc(DominioSituacao.A);
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		admNew.setUnidadeFuncional(unf);
		admNew.setUnidadeFuncionalSolicitante(unfSolicitante);
		admNew.setIndSituacao(DominioSituacaoDispensacaoMdto.D);

		return admNew;
	}

	private void esperarUnidadeFuncionalPossuiCaracteristicaTrue() {
		Mockito.when(
				mockedAghuFacade.unidadeFuncionalPossuiCaracteristica(Mockito.anyShort(),
						Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.TRUE);
	}

	private void esperarUnidadeFuncionalPossuiCaracteristicaFalse() {
		Mockito.when(
				mockedAghuFacade.unidadeFuncionalPossuiCaracteristica(Mockito.anyShort(),
						Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(Boolean.FALSE);
	}

	private List<AfaDispensacaoMdtos> alteraQtdeEstornada(List<AfaDispensacaoMdtos> listaOriginal, int index) {
		AfaDispensacaoMdtos adm = listaOriginal.get(index);
		adm.setQtdeEstornada(new BigDecimal(951));
		listaOriginal.set(index, adm);

		return listaOriginal;
	}

	private List<AfaDispensacaoMdtos> alteraSituacao(List<AfaDispensacaoMdtos> listaOriginal, int index, DominioSituacaoDispensacaoMdto sit) {
		AfaDispensacaoMdtos adm = listaOriginal.get(index);
		adm.setIndSituacao(sit);
		listaOriginal.set(index, adm);

		return listaOriginal;
	}

	private List<AfaDispensacaoMdtos> alteraQtdeDispensada(List<AfaDispensacaoMdtos> listaOriginal, int index) {
		AfaDispensacaoMdtos adm = listaOriginal.get(index);
		adm.setQtdeDispensada(new BigDecimal(951));
		listaOriginal.set(index, adm);

		return listaOriginal;
	}

	private void esperaObterAfaDispMdtoCbSpsDAO(final int count) {
		Mockito.when(
				mockedAfaDispMdtoCbSpsDAO.getDispMdtoCbSpsExcluidoByDispensacaoMdtoCount(Mockito.anyLong(),
						Mockito.any(DominioIndExcluidoDispMdtoCbSps.class))).thenReturn(Long.valueOf(count));

	}

	private List<AfaDispensacaoMdtos> getListaDispensacaoMdto() {
		AfaDispensacaoMdtos adm1 = new AfaDispensacaoMdtos();
		adm1.setQtdeDispensada(new BigDecimal(12));
		AfaTipoOcorDispensacao tipoOcor = new AfaTipoOcorDispensacao();
		tipoOcor.setSeq(Short.valueOf("123"));
		adm1.setTipoOcorrenciaDispensacao(tipoOcor);
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais();
		unf.setSeq(Short.valueOf("32"));
		adm1.setUnidadeFuncional(unf);
		adm1.setIndSituacao(DominioSituacaoDispensacaoMdto.C);

		AfaDispensacaoMdtos adm2 = new AfaDispensacaoMdtos();
		adm2.setQtdeDispensada(new BigDecimal(9));
		AfaTipoOcorDispensacao tipoOcor1 = new AfaTipoOcorDispensacao();
		tipoOcor1.setSeq(Short.valueOf("985"));
		adm2.setTipoOcorrenciaDispensacao(tipoOcor1);
		AghUnidadesFuncionais unf1 = new AghUnidadesFuncionais();
		unf1.setSeq(Short.valueOf("41"));
		adm2.setUnidadeFuncional(unf1);
		adm2.setIndSituacao(DominioSituacaoDispensacaoMdto.S);

		AfaDispensacaoMdtos adm3 = new AfaDispensacaoMdtos();
		adm3.setQtdeDispensada(new BigDecimal(9));
		AfaTipoOcorDispensacao tipoOcor3 = new AfaTipoOcorDispensacao();
		tipoOcor3.setSeq(Short.valueOf("141"));
		adm3.setTipoOcorrenciaDispensacao(tipoOcor3);
		AghUnidadesFuncionais unf3 = new AghUnidadesFuncionais();
		unf3.setSeq(Short.valueOf("781"));
		adm3.setUnidadeFuncional(unf3);
		adm3.setIndSituacao(DominioSituacaoDispensacaoMdto.T);

		AfaDispensacaoMdtos adm4 = new AfaDispensacaoMdtos();
		adm4.setQtdeDispensada(new BigDecimal(9));
		AfaTipoOcorDispensacao tipoOcor4 = new AfaTipoOcorDispensacao();
		tipoOcor4.setSeq(Short.valueOf("531"));
		adm4.setTipoOcorrenciaDispensacao(tipoOcor4);
		AghUnidadesFuncionais unf4 = new AghUnidadesFuncionais();
		unf4.setSeq(Short.valueOf("751"));
		adm4.setUnidadeFuncional(unf4);
		adm4.setIndSituacao(DominioSituacaoDispensacaoMdto.D);

		List<AfaDispensacaoMdtos> list = new ArrayList<AfaDispensacaoMdtos>();
		list.add(adm1);
		list.add(adm2);
		list.add(adm3);
		list.add(adm4);

		return list;
	}

	protected AfaTipoOcorDispensacao getTipoOcorDispGeladeira() {
		AfaTipoOcorDispensacao disp = new AfaTipoOcorDispensacao();
		disp.setSeq(SEQ_TIPO_OCOR_MEDICAMENTO_EM_GELADEIRA);
		return disp;
	}

	private void esperaObterAghAtendimentosPopulado() {
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).thenReturn(getAghAtendimentos());
	}

	private void esperaObterAghAtendimentosNulo() {
		Mockito.when(mockedAghuFacade.obterAghAtendimentoPorChavePrimaria(Mockito.anyInt())).thenReturn(null);
	}

	private AghAtendimentos getAghAtendimentos() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(123);
		AghUnidadesFuncionais unfSolicitante = new AghUnidadesFuncionais();
		unfSolicitante.setSeq(Short.valueOf("456"));
		atendimento.setUnidadeFuncional(unfSolicitante);
		return atendimento;
	}

	@Test
	public void mpmpGeraDispMVtoMpm03088() {
		try {
			esperaObterAghParametro();
			whenObterServidorLogadoNull();
			systemUnderTest.mpmpGeraDispMVto(1, 2, new Date(), new Date(), new Date(), NOME_MICROCOMPUTADOR);
			Assert.fail("DEVERIA ter lançado exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), AfaDispensacaoMdtosRN.AfaDispensacaoMdtosRNExceptionCode.MPM_03088);
		}
	}

	private List<AfaDispensacaoMdtos> getListaAfaDisp() {
		List<AfaDispensacaoMdtos> list = new ArrayList<AfaDispensacaoMdtos>();
		AfaDispensacaoMdtos adm = new AfaDispensacaoMdtos();
		adm.setIndSitItemPrescrito(DominioSituacaoItemPrescritoDispensacaoMdto.DG);
		list.add(adm);
		return list;
	}

	protected void esperaObterAfaDispensacaoMdto() {
		Mockito.when(
				mockedAfaDispensacaoMdtosDAO.pesquisarAfaDispensacaoMdto(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong(),
						Mockito.any(DominioSituacaoItemPrescritoDispensacaoMdto.class))).thenReturn(getListaAfaDisp());
	}

	protected void esperaObterAfaDispensacaoMdto2() {
		Mockito.when(
				mockedAfaDispensacaoMdtosDAO.pesquisarAfaDispensacaoMdto(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong(),
						Mockito.any(DominioSituacaoItemPrescritoDispensacaoMdto.class))).thenReturn(getListaAfaDisp());

	}

	private void whenObterServidorLogado() throws BaseException {
		RapServidores rap = new RapServidores(new RapServidoresId(1, (short) 1));
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
	}

	private void whenObterServidorLogadoNull() throws BaseException {
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(null);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(null);
	}

	private void esperaObterAghParametro() throws BaseException {
		AghParametros aghParametro = new AghParametros();
		aghParametro.setVlrNumerico(BigDecimal.ONE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(null);
	}

}
package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.SessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTelaOriginouSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVariaveisVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class SolicitacaoExameONTest extends AGHUBaseUnitTest<SolicitacaoExameON>{

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private AelSitItemSolicitacoesDAO mockedAelSitItemSolicitacoesDAO;
	@Mock
	private AelTipoAmostraExameDAO mockedAelTipoAmostraExameDAO;
	@Mock
	private SessionContext mockedSessionContext;	
	@Mock
	private ICascaFacade mockedCascaFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogado;
	
	
	@Before
	public void doBeforeEachTestCase() {
		try {
			Mockito.when(mockedSessionContext.getCallerPrincipal())
			.thenReturn(new Principal() {
				@Override
				public String getName() {
					return "teste";
				}
			});
		} catch (Exception e) {
			fail();
		}
	}
	
	
	/**
	 * Data da consulta dentro do range (menos 365 dias e mais 30 dias).
	 * No meio do range.
	 * ok: quando nao ocorrer exception.
	 */
	@Test
	public void testarValidacaoAmbulatorioParaExecutorInterno001() {
		
    	try {
    		final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(365));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);

    		final AghParametros pFinal = new AghParametros();
    		pFinal.setVlrNumerico(new BigDecimal(30));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(pFinal);

    	} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}

		
		try {
			systemUnderTest.validacaoAmbulatorioParaExecutorInterno(new Date(), AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Data da consulta fora do range (menos 365 dias e mais 30 dias).
	 * Um dias fora do range. Limite Superior.
	 * ok: quando ocorrer exception. 
	 */
	@Test
	public void testarValidacaoAmbulatorioParaExecutorInterno002() {
    	try {
    		final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(365));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);
			
    		final AghParametros pFinal = new AghParametros();
    		pFinal.setVlrNumerico(new BigDecimal(30));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(pFinal);
		} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}

		
		try {
			Date hjMaisXXdias = DateUtil.adicionaDias(new Date(), 31);
			systemUnderTest.validacaoAmbulatorioParaExecutorInterno(hjMaisXXdias, AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Testando Limite Superior.
	 * Data da consulta dentro do range (menos 365 dias e mais 30 dias).
	 * Exatamente no ultimo dia do range.
	 * ok: quando NAO ocorrer exception. 
	 */
	@Test
	public void testarValidacaoAmbulatorioParaExecutorInterno003() {
    	try {
    		final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(365));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);
			
    		final AghParametros pFinal = new AghParametros();
    		pFinal.setVlrNumerico(new BigDecimal(30));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(pFinal);
		} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}

		
		try {
			Date hjMaisXXdias = DateUtil.adicionaDias(new Date(), 30);
			systemUnderTest.validacaoAmbulatorioParaExecutorInterno(hjMaisXXdias, AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		}
	}

	/**
	 * Testando Limite Inferior.
	 * Data da consulta dentro do range (menos 365 dias e mais 30 dias).
	 * Exatamente no primeiro dia do range.
	 * ok: quando NAO ocorrer exception. 
	 */
	@Test
	public void testarValidacaoAmbulatorioParaExecutorInterno004() {
    	try {
    		final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(365));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);
			
    		final AghParametros pFinal = new AghParametros();
    		pFinal.setVlrNumerico(new BigDecimal(30));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(pFinal);
		} catch (ApplicationBusinessException e1) {
		}

		
		try {
			Date hjMaisXXdias = DateUtil.adicionaDias(new Date(), -365);
			systemUnderTest.validacaoAmbulatorioParaExecutorInterno(hjMaisXXdias, AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR);
			Assert.assertTrue(true);
		} catch (BaseException e) {
		}
	}
	
	/**
	 * Data da consulta fora do range (menos 365 dias e mais 30 dias).
	 * Um dias fora do range. Limite Inferior.
	 * ok: quando ocorrer exception. 
	 */
	@Test
	public void testarValidacaoAmbulatorioParaExecutorInterno005() {
    	try {
    		final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(365));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);
			
    		final AghParametros pFinal = new AghParametros();
    		pFinal.setVlrNumerico(new BigDecimal(30));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(pFinal);
		} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}

		
		try {
			Date hjMaisXXdias = DateUtil.adicionaDias(new Date(), -366);
			systemUnderTest.validacaoAmbulatorioParaExecutorInterno(hjMaisXXdias, AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Eh dia da Cirurgia.
	 * ok: quando retornar true;
	 */
	@Test
	public void testarEhDiaDaCirurgiaOuDiaSubsequente001() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(new Date());
		
		boolean ok = systemUnderTest.ehDiaDaCirurgiaOuDiaSubsequente(atendimento);
		Assert.assertTrue(ok);
	}

	/**
	 * Eh dia subsequente ao dia da Cirurgia.
	 * ok: quando retornar true;
	 */
	@Test
	public void testarEhDiaDaCirurgiaOuDiaSubsequente002() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(DateUtil.adicionaDias(new Date(), -1));
		
		boolean ok = systemUnderTest.ehDiaDaCirurgiaOuDiaSubsequente(atendimento);
		Assert.assertTrue(ok);
	}
	
	/**
	 * NAO eh dia subsequente ao dia da Cirurgia e NEM o dia da Cirurgia.
	 * ok: quando retornar false;
	 */
	@Test
	public void testarEhDiaDaCirurgiaOuDiaSubsequente003() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(DateUtil.adicionaDias(new Date(), -2));
		
		boolean ok = systemUnderTest.ehDiaDaCirurgiaOuDiaSubsequente(atendimento);
		Assert.assertFalse(ok);
	}

	/**
	 * NAO eh dia subsequente ao dia da Cirurgia e NEM o dia da Cirurgia.
	 * ok: quando retornar false;
	 */
	@Test
	public void testarEhDiaDaCirurgiaOuDiaSubsequente004() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(DateUtil.adicionaDias(new Date(), 2));
		
		boolean ok = systemUnderTest.ehDiaDaCirurgiaOuDiaSubsequente(atendimento);
		Assert.assertFalse(ok);
	}
	
	/**
	 * A data da consulta eh amanha.
	 * ok: quando retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioEOutros001() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(DateUtil.adicionaDias(new Date(), 1));
		try {
			systemUnderTest.validacaoAmbulatorioEOutros(atendimento, null, false);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * A data da consulta eh hoje.
	 * ok: quando NAO retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioEOutros002() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(new Date());
		try {
			systemUnderTest.validacaoAmbulatorioEOutros(atendimento, null, false);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * A data da consulta foi ontem.
	 * ok: quando NAO retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioEOutros003() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setDthrInicio(DateUtil.adicionaDias(new Date(), -1));
		try {
			systemUnderTest.validacaoAmbulatorioEOutros(atendimento, null, false);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
	private void getParametroSistemaMocado() {
		try {
			//AghuParametrosEnum.P_HORAS_SOL_EX_POSTERIOR_AMB_OBSTETRICA = 48
			//AghuParametrosEnum.P_HORAS_SOL_EX_POSTERIOR_AMB_PRONTO_ATD = 24
    		final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(24));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);
			
		} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}
	}
	
	/**
	 * Mesmo dia da consulta.
	 * ok: quando NAO retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioCentroObstetricoProntoAtd001() {
    	getParametroSistemaMocado();
		
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setDthrInicio(new Date());
			systemUnderTest.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, null);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		}
	}
	
	/**
	 * Outra data qualquer. Nao hoje e nem dentro do range. (24 ou 48 horas)
	 * ok: quando retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioCentroObstetricoProntoAtd002() {
    	getParametroSistemaMocado();
		
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setDthrInicio(DateUtil.adicionaDias(new Date(), -3));
			systemUnderTest.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Hoje esta no periodo valido.
	 * ok: quando NAO retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioCentroObstetricoProntoAtd003() {
    	getParametroSistemaMocado();
		
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setDthrInicio(DateUtil.adicionaHoras(new Date(), -23));
			systemUnderTest.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, null);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		} 
	}

	/**
	 * Hoje NAO esta no periodo valido.
	 * ok: quando retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioCentroObstetricoProntoAtd004() {
    	getParametroSistemaMocado();
		
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setDthrInicio(DateUtil.adicionaHoras(new Date(), -25));
			systemUnderTest.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, null);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertTrue(true);
		} 
	}
	
	/**
	 * Uma consulta no futuro.
	 * ok: quando NAO retornar erro.
	 */
	@Test
	public void testarValidacaoAmbulatorioCentroObstetricoProntoAtd005() {
    	getParametroSistemaMocado();
		
		try {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setDthrInicio(DateUtil.adicionaHoras(new Date(), 5));
			systemUnderTest.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, null);
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		} 
	}
	
	
	/**
	 * Testa se alguns dados do itemSolicitacaoExame base são copiados e a dataHoraProgramada é calculada corretamente
	 * ao gerar novos itemSolicitacaoExame para as amostras.
	 */
	@Test
	public void gerarNovoItemSolicitacaoExameTestEDataHoraProgramada() {
		AelItemSolicitacaoExames itemSolicitacaoExameBase = new AelItemSolicitacaoExames();
		AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();
		solicitacao.setSeq(111);
		itemSolicitacaoExameBase.setSolicitacaoExame(solicitacao);
		AelExames exame = new AelExames();
		exame.setSigla("ABC");
		itemSolicitacaoExameBase.setExame(exame);
		AelMateriaisAnalises materialAnalise = new AelMateriaisAnalises();
		materialAnalise.setSeq(222);
		itemSolicitacaoExameBase.setMaterialAnalise(materialAnalise);
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("33"));
		itemSolicitacaoExameBase.setUnidadeFuncional(unidadeFuncional);
		AelSitItemSolicitacoes situacao = new AelSitItemSolicitacoes();
		situacao.setCodigo("XYZ");
		itemSolicitacaoExameBase.setSituacaoItemSolicitacao(situacao);
		
		ItemSolicitacaoExameVariaveisVO variaveis = new ItemSolicitacaoExameVariaveisVO();
		variaveis.setItemSolicitacaoExame(itemSolicitacaoExameBase);
		Date dthrProgramada = new Date();
		variaveis.setDthrProgramada(dthrProgramada);
		variaveis.setTempoColetas(new Float(0.25));
		Date dthrProgramadaNova = DateUtil.adicionaHoras(dthrProgramada, 6);
		variaveis.setTempoAmostraDias(Byte.valueOf("30"));
		
		ItemSolicitacaoExameVO retorno = systemUnderTest.gerarNovoItemSolicitacaoExame(null, new ItemSolicitacaoExameVO(), variaveis, Byte.valueOf("3"));
		
		assertEquals(DominioTipoColeta.N, retorno.getTipoColeta());
		assertEquals(dthrProgramadaNova, retorno.getDataProgramada());
		assertEquals("XYZ", retorno.getSituacaoCodigo().getCodigo());
		assertEquals(Integer.valueOf(3), retorno.getNumeroAmostra());
		assertEquals(Boolean.TRUE, retorno.getIndGeradoAutomatico());
	}
	
	/**
	 * Testa o calculo do intervalo de dias ao gerar novos itemSolicitacaoExame para as amostras.
	 */
	@Test
	public void gerarNovoItemSolicitacaoExameTestIntervaloDias() {
		AelItemSolicitacaoExames itemSolicitacaoExameBase = new AelItemSolicitacaoExames();
		itemSolicitacaoExameBase.setIntervaloHoras(new Date());
		itemSolicitacaoExameBase.setIntervaloDias(Byte.valueOf("55"));
		
		ItemSolicitacaoExameVariaveisVO variaveis = new ItemSolicitacaoExameVariaveisVO();
		variaveis.setItemSolicitacaoExame(itemSolicitacaoExameBase);
		
		
		variaveis.setTempoColetas(new Float(1.25));
		variaveis.setTempoAmostraDias(Byte.valueOf("30"));
		
		ItemSolicitacaoExameVO retorno = systemUnderTest.gerarNovoItemSolicitacaoExame(null, new ItemSolicitacaoExameVO(), variaveis, Byte.valueOf("3"));
		
		assertEquals(Integer.valueOf(31), retorno.getIntervaloDias());
	}
	
	/**
	 * Testa o calculo do intervalo de horas ao gerar novos itemSolicitacaoExame para as amostras.
	 */
	@Test
	public void gerarNovoItemSolicitacaoExameTestIntervaloHoras() {
		AelItemSolicitacaoExames itemSolicitacaoExameBase = new AelItemSolicitacaoExames();
		itemSolicitacaoExameBase.setIntervaloHoras(new Date());
		
		ItemSolicitacaoExameVariaveisVO variaveis = new ItemSolicitacaoExameVariaveisVO();
		variaveis.setItemSolicitacaoExame(itemSolicitacaoExameBase);
		
		Date dateIntervalo = new Date();
		Date dateIntervaloNovo = DateUtil.adicionaHoras(dateIntervalo, 12);
		variaveis.setIntervaloHorasAux(dateIntervalo);
		variaveis.setTempoColetas(new Float(12));
		
		ItemSolicitacaoExameVO retorno = systemUnderTest.gerarNovoItemSolicitacaoExame(null, new ItemSolicitacaoExameVO(), variaveis, Byte.valueOf("3"));
		
		assertEquals(dateIntervaloNovo, retorno.getIntervaloHoras());
	}
	
	@Test
	public void imprimirSolicitacoesColetar(){
		try{
			
			List<AelAmostraItemExames> listaAmostraItemExames = new ArrayList<AelAmostraItemExames>();
			AelAmostraItemExames item = new AelAmostraItemExames();
			listaAmostraItemExames.add(item);
			Mockito.when(mockedAelItemSolicitacaoExameDAO.imprimirSolicitacoesColetar(Mockito.any(List.class), Mockito.any(AghUnidadesFuncionais.class))).thenReturn(listaAmostraItemExames);
			
			AelSitItemSolicitacoes sit = new AelSitItemSolicitacoes();
			Mockito.when(mockedAelSitItemSolicitacoesDAO.obterPeloId(Mockito.anyString())).thenReturn(sit);

			final AghParametros p = new AghParametros();
    		p.setVlrNumerico(new BigDecimal(24));
    		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(p);
    		
    		Mockito.when(mockedAelTipoAmostraExameDAO.buscarAelTipoAmostraExamePorAelExamesMaterialAnaliseAelAmostrasRecipienteColetaResponsavelCS(
    				Mockito.any(AelExamesMaterialAnalise.class), Mockito.any(AelAmostras.class), Mockito.any(AelRecipienteColeta.class))).thenReturn(null);
    		
		} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}
		
		List<Integer> solicitacoes = new ArrayList<Integer>();
		AghUnidadesFuncionais unidadeExecutora = new AghUnidadesFuncionais();
		
		solicitacoes.add(1);
		solicitacoes.add(2);
		
		try {
			systemUnderTest.imprimirSolicitacoesColetar(solicitacoes, unidadeExecutora, NOME_MICROCOMPUTADOR, new Date());
		} catch (BaseException e) {
			getLog().debug("Exceção ignorada.");
		}
		
	}
	@Test
	public void chamarRelatorioTicketPacienteTestRealizouImpressao() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setPaciente(new AipPacientes());//Necessário para rodar apenas
		atendimento.setLeito(new AinLeitos());//Necessário para rodar apenas
		
		AelAtendimentoDiversos atendimentoDiverso = new AelAtendimentoDiversos();
		AelProjetoPesquisas pjq = new AelProjetoPesquisas();
		pjq.setSeq(111);
		atendimentoDiverso.setAelProjetoPesquisas(pjq);
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSeq(Short.valueOf("22"));
		
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO();
		AelUnfExecutaExames unfExecutaExame = new AelUnfExecutaExames();
		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndImpTicketPaciente(true);
		unfExecutaExame.setAelExamesMaterialAnalise(aelExamesMaterialAnalise);
		unfExecutaSinonimoExameVO.setUnfExecutaExame(unfExecutaExame);
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		
		List<ItemSolicitacaoExameVO> itemSolicitacaoExameVos = new ArrayList<ItemSolicitacaoExameVO>();
		itemSolicitacaoExameVos.add(itemSolicitacaoExameVO);
		
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setAtendimentoDiverso(atendimentoDiverso);
		solicitacaoExameVO.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_PESQUISA_SOLICITACAO_EXAME);
		solicitacaoExameVO.setUnidadeFuncional(unidadeFuncional);
		solicitacaoExameVO.setItemSolicitacaoExameVos(itemSolicitacaoExameVos);
		
		Mockito.when(mockedInternacaoFacade.verificarCaracteristicaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(false);
		
		systemUnderTest.chamarRelatorioTicketPaciente(solicitacaoExameVO);
		
		assertEquals(true, solicitacaoExameVO.getImprimiuTicketPaciente());
	}
	/**
	 * Testa a lógica sem a unidade de trabalho (1ª parte da regra)
	 */
	@Test
	public void chamarRelatorioQuestionarioTest() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setPaciente(new AipPacientes());//Necessário para rodar apenas
		atendimento.setLeito(new AinLeitos());//Necessário para rodar apenas
//		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
//		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_PESQUISA_SOLICITACAO_EXAME);

		systemUnderTest.chamarRelatorioQuestionario(solicitacaoExameVO);
		Assert.assertTrue(solicitacaoExameVO.isImprimirQuestionario());
		
	}
	/**
	 * Testa a lógica com a unidade de trabalho e com caracteristica de ticket pac externo(2ª parte da regra)
	 */
	@Test
	public void chamarRelatorioQuestionarioTest2() {
		
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(true);

		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.X);
		atendimento.setPaciente(new AipPacientes());//Necessário para rodar apenas
		atendimento.setLeito(new AinLeitos());//Necessário para rodar apenas
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_PESQUISA_SOLICITACAO_EXAME);

		systemUnderTest.chamarRelatorioQuestionario(solicitacaoExameVO);
		Assert.assertTrue(solicitacaoExameVO.isImprimirQuestionario());
		
	}
	/**
	 * Testa a lógica com a unidade de trabalho e SEM caracteristica de ticket pac externo(3ª parte da regra)
	 */
	@Test
	public void chamarRelatorioQuestionarioTest3() {
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(false);

		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setPaciente(new AipPacientes());//Necessário para rodar apenas
		atendimento.setLeito(new AinLeitos());//Necessário para rodar apenas
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_PESQUISA_SOLICITACAO_EXAME);

		systemUnderTest.chamarRelatorioQuestionario(solicitacaoExameVO);
		Assert.assertFalse(solicitacaoExameVO.isImprimirQuestionario());
		
	}
	/**
	 * Testa a lógica com a unidade de trabalho e com caracteristica de ticket pac externo mas vindo de tela ambulatorio(5ª parte da regra)
	 */
	@Test
	public void chamarRelatorioQuestionarioTest4() {
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(true);

		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		atendimento.setPaciente(new AipPacientes());//Necessário para rodar apenas
		atendimento.setLeito(new AinLeitos());//Necessário para rodar apenas
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_AMBULATORIO);

		systemUnderTest.chamarRelatorioQuestionario(solicitacaoExameVO);
		Assert.assertFalse(solicitacaoExameVO.isImprimirQuestionario());
		
	}
	/**
	 * Testa a lógica com a unidade de trabalho e com caracteristica de ticket pac externo mas vindo de origem internação(4ª parte da regra)
	 */
	@Test
	public void chamarRelatorioQuestionarioTest5() {
		Mockito.when(mockedAghuFacade.validarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(true);

		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setPaciente(new AipPacientes());//Necessário para rodar apenas
		atendimento.setLeito(new AinLeitos());//Necessário para rodar apenas
		AghUnidadesFuncionais unidadeTrabalho = new AghUnidadesFuncionais();
		SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(atendimento);
		solicitacaoExameVO.setUnidadeTrabalho(unidadeTrabalho);
		solicitacaoExameVO.setTelaOriginouSolicitacao(DominioTelaOriginouSolicitacaoExame.TELA_ATENDIMENTO_EXTERNO);

		systemUnderTest.chamarRelatorioQuestionario(solicitacaoExameVO);
		Assert.assertFalse(solicitacaoExameVO.isImprimirQuestionario());
		
	}
	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
	}
}

package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ImportarArquivoSusONTest extends AGHUBaseUnitTest<ImportarArquivoSusON>{
	
	private static final String NOME_ARQUIVO_TESTE = "arquivo_mock_test.txt";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private FatCompetenciaDAO mockedFatCompetenciaDAO;
	@Mock
	private ProcessadorArquivosImportacaoSus mockedProcessadorArquivosImportacaoSus;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Before
	public void inicio() throws BaseException {
		whenObterServidorLogado();
		
		final AghParametros paramNomeArquivoOcupacao = new AghParametros();
		paramNomeArquivoOcupacao.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_MODALIDADE)).
		thenReturn(paramNomeArquivoOcupacao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_MODALIDADE)).
		thenReturn(NOME_ARQUIVO_TESTE);

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_COMPATIBILIDADE)).
		thenReturn(paramNomeArquivoOcupacao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_COMPATIBILIDADE)).
		thenReturn(NOME_ARQUIVO_TESTE);

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCED_SERVICO)).
		thenReturn(paramNomeArquivoOcupacao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCED_SERVICO)).
		thenReturn(NOME_ARQUIVO_TESTE);

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO)).
		thenReturn(paramNomeArquivoOcupacao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO)).
		thenReturn(NOME_ARQUIVO_TESTE);

		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO_CLASSIFICACAO)).
		thenReturn(paramNomeArquivoOcupacao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO_CLASSIFICACAO)).
		thenReturn(NOME_ARQUIVO_TESTE);

		final AghParametros paramArquivoProcedimentosCBO = new AghParametros();
		paramArquivoProcedimentosCBO.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CBO)).
		thenReturn(paramArquivoProcedimentosCBO);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CBO)).
		thenReturn(NOME_ARQUIVO_TESTE);

		
		final AghParametros paramArquivoSubGrupo = new AghParametros();
		paramArquivoSubGrupo.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_SUB_GRUPO)).
		thenReturn(paramArquivoSubGrupo);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_SUB_GRUPO)).
		thenReturn(NOME_ARQUIVO_TESTE);
		
		final AghParametros paramArquivoFormaOrganizacao = new AghParametros();
		paramArquivoFormaOrganizacao.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_FORMA_ORGANIZACAO)).
		thenReturn(paramArquivoFormaOrganizacao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_FORMA_ORGANIZACAO)).
		thenReturn(NOME_ARQUIVO_TESTE);

		final AghParametros paramArquivoModalidade = new AghParametros();
		paramArquivoModalidade.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_MODALIDADE)).
		thenReturn(paramArquivoModalidade);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_MODALIDADE)).
		thenReturn(NOME_ARQUIVO_TESTE);
		
		final AghParametros paramPhiSeqImportacaoCID = new AghParametros();
		paramPhiSeqImportacaoCID.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHI_SEQ_IMPORTACAO_CID)).
		thenReturn(paramPhiSeqImportacaoCID);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_PHI_SEQ_IMPORTACAO_CID)).
		thenReturn(NOME_ARQUIVO_TESTE);

		final AghParametros paramTabelaFaturPadrao = new AghParametros();
		paramTabelaFaturPadrao.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO)).
		thenReturn(paramTabelaFaturPadrao);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_TABELA_FATUR_PADRAO)).
		thenReturn(NOME_ARQUIVO_TESTE);
		
		final AghParametros paramArquivoGrupo = new AghParametros();
		paramArquivoGrupo.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_GRUPO)).
		thenReturn(paramArquivoGrupo);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_GRUPO)).
		thenReturn(NOME_ARQUIVO_TESTE);
		
		final AghParametros paramArquivoOcupacaoCBO = new AghParametros();
		paramArquivoOcupacaoCBO.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_OCUPACAO_CBO)).
		thenReturn(paramArquivoOcupacaoCBO);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_OCUPACAO_CBO)).
		thenReturn(NOME_ARQUIVO_TESTE);

		final AghParametros paramArquivoItensProcedimentoHospitalar = new AghParametros();
		paramArquivoItensProcedimentoHospitalar.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP)).
		thenReturn(paramArquivoItensProcedimentoHospitalar);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP)).
		thenReturn(NOME_ARQUIVO_TESTE);

		final AghParametros paramArquivoProcedimentosCid = new AghParametros();
		paramArquivoProcedimentosCid.setVlrTexto(NOME_ARQUIVO_TESTE);
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CID)).
		thenReturn(paramArquivoProcedimentosCid);
		Mockito.when(mockedParametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CID)).
		thenReturn(NOME_ARQUIVO_TESTE);

		final AghParametros paramCodProcedimentoFixo = new AghParametros();
		paramCodProcedimentoFixo.setVlrNumerico(BigDecimal.valueOf(2575));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHI_SEQ_IMPORTACAO_PROCEDIMENTO_CBO)).
		thenReturn(paramCodProcedimentoFixo);

		final AghParametros paramSiglaFaturamento = new AghParametros();
		paramCodProcedimentoFixo.setVlrNumerico(BigDecimal.valueOf(2575));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO)).
		thenReturn(paramSiglaFaturamento);
		
		final AghParametros paramLimiteTempo = new AghParametros();
		paramLimiteTempo.setVlrNumerico(BigDecimal.valueOf(30));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_TEMPO_LIMITE_PROCESSAMENTO_ARQUIVOS)).
		thenReturn(paramLimiteTempo);

		final AghParametros fcf = new AghParametros();
		fcf.setVlrNumerico(BigDecimal.valueOf(1));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCF)).
		thenReturn(fcf);

		final AghParametros ignorados = new AghParametros();
		ignorados.setVlrTexto("310010039,411010042,411010034");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_PROCEDIMENTOS_IGNORADOS_IMPORTACAO_VALOR)).
		thenReturn(ignorados);
					
		final AghParametros fcc = new AghParametros();
		fcc.setVlrNumerico(BigDecimal.valueOf(3));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCC)).
		thenReturn(fcc);
		
		final AghSistemas sistema = new AghSistemas();
		sistema.setSigla(AghuParametrosEnum.P_SIGLA_FATURAMENTO.toString());
		Mockito.when(mockedAghuFacade.obterAghSistema(Mockito.anyString())).
		thenReturn(sistema);

		final AghArquivoProcessamento arquivo = new AghArquivoProcessamento();
		sistema.setSigla(AghuParametrosEnum.P_SIGLA_FATURAMENTO.toString());
		Mockito.when(mockedAghuFacade.persistirAghArquivoProcessamento(Mockito.any(AghArquivoProcessamento.class))).
		thenReturn(arquivo);

		final AghParametros grupo = new AghParametros();
		grupo.setVlrNumerico(BigDecimal.valueOf(6));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS)).
		thenReturn(grupo);
		
		final AghParametros convenio = new AghParametros();
		convenio.setVlrNumerico(BigDecimal.valueOf(1));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS)).
		thenReturn(convenio);
		
		final AghParametros tabela = new AghParametros();
		tabela.setVlrNumerico(BigDecimal.valueOf(12));
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO)).
		thenReturn(tabela);
		
		final AghParametros planos = new AghParametros();
		planos.setVlrTexto("1,2");
		Mockito.when(mockedParametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPORTACAO_CID_PLANOS_SUS)).
		thenReturn(planos);

		Mockito.when(mockedAghuFacade.obterArquivoNaoProcessado(Mockito.any(AghSistemas.class), Mockito.anyString())).
		thenReturn(null);

		Mockito.when(mockedAghuFacade.pesquisarArquivosAbortados(Mockito.any(AghSistemas.class), Mockito.anyInt())).
		thenReturn(new ArrayList<AghArquivoProcessamento>());
	}
	
	@After
	public void tearDown()  {
		apagarArquivoValidoProcedimentoOcupacao(NOME_ARQUIVO_TESTE);
	}

	/**
	 * Testa verificaNomeArquivoZip em condicoes válidas
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificaNomeArquivoZip()  {
		final List<FatCompetencia> list = new ArrayList<FatCompetencia>(1);
		list.add(new FatCompetencia(new FatCompetenciaId(DominioModuloCompetencia.SIS, 5, 2011, new Date()), true, "User", "User"));
		Mockito.when(mockedFatCompetenciaDAO.obterListaAtivaPorModulo(Mockito.any(DominioModuloCompetencia.class))).
		thenReturn(list);
		final String nomeArquivo = "TabelaUnificada_201105.zip";
		
		try {
			systemUnderTest.verificaNomeArquivoZip(nomeArquivo);
		} catch (ApplicationBusinessException e) {
			getLog().error(e.getMessage());
		}
	}
	
	/**
	 * Testa verificaNomeArquivoZip em condicoes válidas com mês maior que 9
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificaNomeArquivoZip2()  {
		final List<FatCompetencia> list = new ArrayList<FatCompetencia>(1);
		list.add(new FatCompetencia(new FatCompetenciaId(DominioModuloCompetencia.SIS, 11, 2011, new Date()), true, "User", "User"));
		Mockito.when(mockedFatCompetenciaDAO.obterListaAtivaPorModulo(Mockito.any(DominioModuloCompetencia.class))).
		thenReturn(list);
		final String nomeArquivo = "TabelaUnificada_201111.zip";
		try {
			systemUnderTest.verificaNomeArquivoZip(nomeArquivo);
		} catch (ApplicationBusinessException e) {
			getLog().error(e.getMessage());
		}
	}

	/**
	 * Testa verificaNomeArquivoZip com nome inválido
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testVerificaNomeArquivoZipNomeInvalido()  {
		final List<FatCompetencia> list = new ArrayList<FatCompetencia>(1);
		list.add(new FatCompetencia(new FatCompetenciaId(DominioModuloCompetencia.SIS, 5, 2011, new Date()), true, "User", "User"));
		Mockito.when(mockedFatCompetenciaDAO.obterListaAtivaPorModulo(Mockito.any(DominioModuloCompetencia.class))).
		thenReturn(list);

		final String nomeArquivo = "TabelaUnificada_201110.zip";
		try {
			systemUnderTest.verificaNomeArquivoZip(nomeArquivo);
			Assert.fail("Nome inválido passou.");
		} catch (final ApplicationBusinessException e) {
			getLog().debug("Exceção ignorada.");
		}
	}

	/**
	 * Testa verificaNomeArquivoZip em condicoes válidas
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarCidProcedimento()  {
		try {
			if (criarArquivoValidoProcedimentoOcupacao(NOME_ARQUIVO_TESTE)) {
				final List<String> lista = new ArrayList<String>();
				lista.add(NOME_ARQUIVO_TESTE);
				systemUnderTest.atualizarCidProcedimentoNovo(lista);
			}
		} catch (final BaseException e) {
			getLog().error(e.getMessage());
		} catch (final IOException e) {
			getLog().error(e.getMessage());
		}
		
	}

	/**
	 * Testa atualizarGeral em condicoes válidas
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarGeral()  {
		try {
			if (criarArquivoValidoProcedimentoOcupacao(NOME_ARQUIVO_TESTE)) {
				final List<String> lista = new ArrayList<String>();
				lista.add(NOME_ARQUIVO_TESTE);
				systemUnderTest.atualizarGeral(lista);
			}
		} catch (final BaseException e) {
			getLog().error(e.getMessage());
		} catch (final IOException e) {
			getLog().error(e.getMessage());
		}
			
	}

	/**
	 * Testa atualizarItensProcedHosp em condicoes válidas
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarItensProcedimentoHospitalar()  {
		try {
			if (criarArquivoValidoProcedimentoOcupacao(NOME_ARQUIVO_TESTE)) {
				final List<String> lista = new ArrayList<String>();
				lista.add(NOME_ARQUIVO_TESTE);
				systemUnderTest.atualizarItensProcedimentoHospitalar(lista);
			}
		} catch (final BaseException e) {
			getLog().error(e.getMessage());
		} catch (final IOException e) {
			getLog().error(e.getMessage());
		}
	}

	/**
	 * Testa atualizarCboProcedimento em condicoes válidas
	 * 
	 * @
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAtualizarCboProcedimento()  {
		try {
			if (criarArquivoValidoProcedimentoOcupacao(NOME_ARQUIVO_TESTE)) {
				final List<String> lista = new ArrayList<String>();
				lista.add(NOME_ARQUIVO_TESTE);
				systemUnderTest.atualizarCboProcedimento(lista);
			}
		} catch (final BaseException e) {
			getLog().error(e.getMessage());
		} catch (final IOException e) {
			getLog().error(e.getMessage());
		}
	}

	private boolean criarArquivoValidoProcedimentoOcupacao(final String nomeArquivo) throws IOException  {
		final File file = new File(nomeArquivo);
		if (file.exists()) {
			return true;
		} else {
			return file.createNewFile();
		}
	}

	private void apagarArquivoValidoProcedimentoOcupacao(final String nomeArquivo)  {
		try {
			final File file = new File(nomeArquivo);
			if (file.exists()) {
				file.delete();
			}
		} catch (final Exception e) {
			getLog().error(e.getMessage());
		}
	}

	protected Log getLog() {
		return LogFactory.getLog(this.getClass());
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}

package br.gov.mec.aghu.exames.contratualizacao.business;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.business.AtendimentoPacExternoContratualizacaoCommand.AtendimentoPacExternoContratualizacaoActionExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AtendimentoPacExternoContratualizacaoCommandTest extends AGHUBaseUnitTest<AtendimentoPacExternoContratualizacaoCommand>{

	public enum GerarSolicitacaoExamesTestExceptionCode implements
		BusinessExceptionCode {
		MENSAGEM_NRO_CARTAO_SAUDE_INVALIDO;

	}
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	//private AghUnidadesFuncionaisDAO mockedAghUnidadesFuncionaisDAO;
	@Mock
	private IExamesFacade mockedExamesFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	protected Throwable e;

	@Test
	public void inserirAtendimentoExternoPacienteNullTest() {

		try {
			systemUnderTest.inserirAtendimentoPacExterno(null,
					new AghMedicoExterno(), (short) 1, (byte) 1, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ATENDIMENTO);
		}

	}

	@Test
	public void inserirAtendimentoExternoMedicoNullTest() {

		try {
			systemUnderTest.inserirAtendimentoPacExterno(new AipPacientes(),
					null, (short) 1, (byte) 1, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ATENDIMENTO);
		}

	}

	@Test
	public void inserirAtendimentoExternoConvenioNullTest() {

		try {
			systemUnderTest.inserirAtendimentoPacExterno(new AipPacientes(),
					new AghMedicoExterno(), null, (byte) 1, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ATENDIMENTO);
		}

	}

	@Test
	public void inserirAtendimentoExternoPlanoNullTest() {

		try {
			systemUnderTest.inserirAtendimentoPacExterno(new AipPacientes(),
					new AghMedicoExterno(), (short) 1, null, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ATENDIMENTO);
		}

	}

	@Test
	public void inserirAtendimentoExternoConvenioPlanoInvalidoTest() {

		Mockito.when(mockedFaturamentoFacade.obterConvenioSaudePlanoAtivo(Mockito.anyShort(), Mockito.anyByte())).thenReturn(null);
		try {
			systemUnderTest.inserirAtendimentoPacExterno(new AipPacientes(),
					new AghMedicoExterno(), (short) 1, (byte) 1, NOME_MICROCOMPUTADOR);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_CONVENIO_PLANO_INVALIDO);
		}

	}

	@Test
	public void inserirAtendimentoExternoSucessoTest() {

		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		final RapServidores servidor = new RapServidores(new RapServidoresId(
				10, (short) 1));
		BigDecimal valorNNumerico = new BigDecimal(BigInteger.TEN);
		final AghParametros parametro = new AghParametros(valorNNumerico);		
		final AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais(
				(short) 1);
		final AghAtendimentosPacExtern atendimentoPacExterno = new AghAtendimentosPacExtern();
		AghAtendimentos atendimento = new AghAtendimentos(1);
		atendimentoPacExterno.getAtendimentos().add(atendimento);

		try {
			Mockito.when(mockedFaturamentoFacade.obterConvenioSaudePlanoAtivo(Mockito.anyShort(), Mockito.anyByte())).thenReturn(convenioSaudePlano);

			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

			Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(unidadeFuncional);

			Mockito.when(mockedExamesFacade.gravarAghAtendimentoPacExtern(Mockito.any(AghAtendimentosPacExtern.class), Mockito.anyString(), 
					Mockito.any(RapServidores.class))).thenReturn(atendimentoPacExterno);

		} catch (ApplicationBusinessException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		} catch (BaseException e) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}

		try {
			expectServidor(1);			
			AghAtendimentos atd = systemUnderTest.inserirAtendimentoPacExterno(
					new AipPacientes(), new AghMedicoExterno(), (short) 1,
					(byte) 1, NOME_MICROCOMPUTADOR);
			Assert.assertTrue(atd != null);
			Assert.assertTrue(atd.getSeq().equals(1));
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void inserirAtendimentoExternoParametroNullTest() throws BaseException {

		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();

		Mockito.when(mockedFaturamentoFacade.obterConvenioSaudePlanoAtivo(Mockito.anyShort(), Mockito.anyByte())).thenReturn(convenioSaudePlano);

		Mockito.when(mockedExamesFacade.gravarAghAtendimentoPacExtern(Mockito.any(AghAtendimentosPacExtern.class), Mockito.anyString(), 
				Mockito.any(RapServidores.class))).thenReturn(new AghAtendimentosPacExtern());

		try {
			expectServidor(1);
			systemUnderTest.inserirAtendimentoPacExterno(
					new AipPacientes(), new AghMedicoExterno(), (short) 1,
					(byte) 1, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_PARAMETRO_P_UNID_SOL_PREFEITURA_NAO_DEFINIDO);
		}

	}

	@Test
	public void inserirAtendimentoExternoUnidadeFuncionalInvalidaTest() {

		final FatConvenioSaudePlano convenioSaudePlano = new FatConvenioSaudePlano();
		final RapServidores servidor = new RapServidores(new RapServidoresId(
				10, (short) 1));
		BigDecimal valorNNumerico = new BigDecimal(BigInteger.TEN);
		final AghParametros parametro = new AghParametros(valorNNumerico);


		try {

			Mockito.when(mockedFaturamentoFacade.obterConvenioSaudePlanoAtivo(Mockito.anyShort(), Mockito.anyByte())).thenReturn(convenioSaudePlano);

			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(parametro);

			Mockito.when(mockedAghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(Mockito.anyShort())).thenReturn(null);

			Mockito.when(mockedExamesFacade.gravarAghAtendimentoPacExtern(Mockito.any(AghAtendimentosPacExtern.class), Mockito.anyString(), 
					Mockito.any(RapServidores.class))).thenReturn(null);

		} catch (BaseException e1) {
			Assert.fail("Metodo mocado retornou exception!!!");
		}

		try {
			expectServidor(1);			
			systemUnderTest.inserirAtendimentoPacExterno(
					new AipPacientes(), new AghMedicoExterno(), (short) 1,
					(byte) 1, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					AtendimentoPacExternoContratualizacaoActionExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA);
		}

	}
	
	private void expectServidor(final Integer vinCodigo) throws ApplicationBusinessException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
	}	

}

package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.solicitacao.business.EtiquetasON.EtiquetasONExceptionCode;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EtiquetasONTest extends AGHUBaseUnitTest<EtiquetasON>{

	@Mock
	private IExamesFacade mockedExamesFacade;
	@Mock
	private AelAmostrasDAO mockedAelAmostrasDAO;
	@Mock
	private AelAmostraItemExamesDAO mockedAelAmostraItemExamesDAO;
	@Mock
	private AelItemHorarioAgendadoDAO mockedAelItemHorarioAgendadoDAO;
	
			/**
	 * Testa a abreviação de alguns nomes de pacientes e recém-nascidos.
	 * 
	 */
	@Test
	public void testarFormatarNome() {
    	String nome1 = "FERNANDO SALDANHA THOME";
    	String nome2 = "JOAO JORGE DE OLIVEIRA BIANCHINI";
    	String nome3 = "MARIA HELENA DOS SANTOS MICHELS";
    	String nome4 = "REGINA ISABEL FERREIRA DA SILVA";
    	String nome5 = "INDIO DO BRASIL LEAL L. DE LIMA";
    	
    	String nome6 = "RN Nxxxx Cxxx Sxxx DA Cxxxxx";
    	String nome7 = "RN Cxxxx Rxxxxxx E Sxxxxx";
    	String nome8 = "RN I Cxxx Axxxx DE Sxxx Mxxxx";
    	String nome9 = "RN I DE Exxx Gxxx Bxxx DA Sxxx";
    	String nome10 = "RN II Zxxx Bxxx Pxxx";
    	String nome11 = "RN VIII Rxx Vxx Cxx";
		
		String retorno;
		
		retorno = systemUnderTest.formatarNome(nome1);
		Assert.assertEquals("FST", retorno);
		
		retorno = systemUnderTest.formatarNome(nome2);
		Assert.assertEquals("JJOB", retorno);
		
		retorno = systemUnderTest.formatarNome(nome3);
		Assert.assertEquals("MHSM", retorno);
		
		retorno = systemUnderTest.formatarNome(nome4);
		Assert.assertEquals("RIFS", retorno);
		
		retorno = systemUnderTest.formatarNome(nome5);
		Assert.assertEquals("IBLLL", retorno);
		
		retorno = systemUnderTest.formatarNome(nome6);
		Assert.assertEquals("RN NCSC", retorno);
		
		retorno = systemUnderTest.formatarNome(nome7);
		Assert.assertEquals("RN CRES", retorno);
		
		retorno = systemUnderTest.formatarNome(nome8);
		Assert.assertEquals("RN I CASM", retorno);
		
		retorno = systemUnderTest.formatarNome(nome9);
		Assert.assertEquals("RN I EGBS", retorno);
		
		retorno = systemUnderTest.formatarNome(nome10);
		Assert.assertEquals("RN II ZBP", retorno);
		
		retorno = systemUnderTest.formatarNome(nome11);
		Assert.assertEquals("RN VIII RVC", retorno);
	}
	
	/**
	 * Testa a localização do paciente sem atendimento.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteSemAtendimento() {
		try {
			String retorno = systemUnderTest.localizarPaciente(null);
			Assert.assertEquals("   ", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
		
	/**
	 * Testa a localização do paciente de origem X.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteOrigemX() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.X);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("U:EXT", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa a localização do paciente de origem D.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteOrigemD() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.D);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("U:DOA", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa a localização do paciente de origem C.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteOrigemC() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.C);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("U:CIR", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa a localização do paciente de origem U.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteOrigemU() {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSigla("TST2");
		atendimento.setUnidadeFuncional(unidadeFuncional);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("TST2", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa a localização do paciente de origem diferente das previstas
	 * e com leito.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteLeito() {
		AghAtendimentos atendimento = new AghAtendimentos();
		
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID("LTO123");
		atendimento.setLeito(leito);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("LTO123", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa a localização do paciente de origem diferente das previstas
	 * e com número do quarto.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteQuarto() {
		AghAtendimentos atendimento = new AghAtendimentos();
		
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID("LTO123");
		AinQuartos quarto = new AinQuartos();
		quarto.setNumero(Short.valueOf("234"));
		quarto.setDescricao("234");
		atendimento.setQuarto(quarto);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("234", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa a localização do paciente de origem diferente das previstas
	 * e sem número de quarto ou leito.
	 * 
	 */
	@Test
	public void testarLocalizarPacienteUnidadeFuncional() {
		AghAtendimentos atendimento = new AghAtendimentos();
		
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setAndar("5");
		unidadeFuncional.setIndAla(AghAla.N);
		atendimento.setUnidadeFuncional(unidadeFuncional);
		
		try {
			String retorno = systemUnderTest.localizarPaciente(atendimento);
			Assert.assertEquals("U:5 N", retorno);
		} catch (ApplicationBusinessException e) {
			Assert.fail("Gerada excecao " + e.getCode());
		}
	}
	
	/**
	 * Testa geração da lista de siglas de exames.
	 * 
	 */
	@Test
	public void testarGerarListaExames() {
		
		List<AelAmostraItemExames> amostraItensExames = new ArrayList<AelAmostraItemExames>();
		
		AelExames exame3 = new AelExames();
		exame3.setSigla("ADG");
		AelItemSolicitacaoExames itemSolicitacaoExame3 = new AelItemSolicitacaoExames();
		itemSolicitacaoExame3.setExame(exame3);
		AelAmostraItemExames amostraItemExame3 = new AelAmostraItemExames();
		amostraItemExame3.setAelItemSolicitacaoExames(itemSolicitacaoExame3);
		
		AelExames exame2 = new AelExames();
		exame2.setSigla("JRW");
		AelItemSolicitacaoExames itemSolicitacaoExame2 = new AelItemSolicitacaoExames();
		itemSolicitacaoExame2.setExame(exame2);
		AelAmostraItemExames amostraItemExame2 = new AelAmostraItemExames();
		amostraItemExame2.setAelItemSolicitacaoExames(itemSolicitacaoExame2);
		
		AelExames exame = new AelExames();
		exame.setSigla("DBCA");
		AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
		itemSolicitacaoExame.setExame(exame);
		AelAmostraItemExames amostraItemExame = new AelAmostraItemExames();
		amostraItemExame.setAelItemSolicitacaoExames(itemSolicitacaoExame);
		
		amostraItensExames.add(amostraItemExame);
		amostraItensExames.add(amostraItemExame2);
		amostraItensExames.add(amostraItemExame3);
		
		String retorno = systemUnderTest.gerarListaExames(amostraItensExames);
		Assert.assertEquals("DBCA,JRW,ADG", retorno);
	}
	
	
	/**
	 * Testa geração de etiquetas quando nenhuma amostra é encontrada.
	 * 
	 */
	@Test
	public void testarGerarEtiquetasSemAmostras() {
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setOrigem(DominioOrigemAtendimento.D);
		
		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		solicitacaoExame.setAtendimento(atendimento);
		
		List<AelAmostras> amostras = new ArrayList<AelAmostras>();
		
		Mockito.when(mockedExamesFacade.buscarLaudoProntuarioPaciente(Mockito.any(AelSolicitacaoExames.class))).thenReturn("12345678");
		
		Mockito.when(mockedExamesFacade.buscarLaudoNomePaciente(Mockito.any(AelSolicitacaoExames.class))).thenReturn("TESTE");
			
		Mockito.when(mockedExamesFacade.buscarAmostrasPorSolicitacaoExame(Mockito.anyInt(), Mockito.anyShort())).thenReturn(amostras);
		
		
		try {
			systemUnderTest.gerarEtiquetas(solicitacaoExame, null, null, null, false);
			Assert.fail("Excecao nao gerada");
		} catch (BaseException e) {
			Assert.assertEquals(EtiquetasONExceptionCode.AEL_00895, e.getCode());
		}
	}
	
	
}

package br.gov.mec.aghu.exames.contratualizacao.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.exames.contratualizacao.business.ItemSolicitacaoExamesContratualizacaoCommand.ItemSolicitacaoExamesContratualizacaoActionExceptionCode;
import br.gov.mec.aghu.exames.contratualizacao.util.Item;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ItemSolicitacaoExamesContratualizacaoCommandTest extends AGHUBaseUnitTest<ItemSolicitacaoExamesContratualizacaoCommand>{

	@Mock
	private AelUnfExecutaExamesDAO mockedAelUnfExecutaExamesDAO;

	@Test
	public void buscarItemSolicitacaoExameErroUnfSeqObrigatorio() {
		try {
			Item item = new Item();
			item.setSiglaExame("TESTE");
			item.setMaterialAnalise("5");

			systemUnderTest.buscarItemSolicitacaoExame(item);
			
			Assert.fail("Deveria ter gerado uma exception: MENSAGEM_UNF_SEQ_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_UNF_SEQ_OBRIGATORIO);
		} 

	}
	
	@Test
	public void buscarItemSolicitacaoExameErroEmaExaSiglaObrigatorio() {
		try {
			Item item = new Item();
			item.setMaterialAnalise("5");
			item.setUnidadeExecutora("5");
			
			
			systemUnderTest.buscarItemSolicitacaoExame(item);
			
			Assert.fail("Deveria ter gerado uma exception: MENSAGEM_UNF_SEQ_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_EMA_EXA_SIGLA_OBRIGATORIO);
		} 

	}
	
	@Test
	public void buscarItemSolicitacaoExameErroEmaManSeqObrigatorio() {
		try {
			Item item = new Item();
			item.setSiglaExame("TESTE");
			item.setUnidadeExecutora("5");

			systemUnderTest.buscarItemSolicitacaoExame(item);
			
			Assert.fail("Deveria ter gerado uma exception: MENSAGEM_UNF_SEQ_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_EMA_MAN_SEQ_OBRIGATORIO);
		} 

	}
	
	@Test
	public void buscarItemSolicitacaoExameErroExameInexistente() {
		try {
			Item item = new Item();
			item.setSiglaExame("TESTE");
			item.setMaterialAnalise("5");
			item.setUnidadeExecutora("5");
			
			Mockito.when(mockedAelUnfExecutaExamesDAO.obterAelUnfExecutaExames(Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(null);

			systemUnderTest.buscarItemSolicitacaoExame(item);
			
			Assert.fail("Deveria ter gerado uma exception: MENSAGEM_UNF_SEQ_OBRIGATORIO");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_EXAME_INEXISTENTE);
		} 

	}
	

	@Test
	public void buscarItemSolicitacaoExameSuccess() {
		Item item = new Item();
		item.setSiglaExame("TESTE");
		item.setMaterialAnalise("5");
		item.setUnidadeExecutora("5");
		
		Mockito.when(mockedAelUnfExecutaExamesDAO.obterAelUnfExecutaExames(Mockito.anyString(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(new AelUnfExecutaExames());

		try {
			systemUnderTest.buscarItemSolicitacaoExame(item);
			
		} catch (BaseException e) {
			Assert.fail("Não deveria apresentar exceção: " + e.getCode() + e.getMessage());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}
	
	@Test
	public void buscarIntervaloColetaErro() {
		Item item = new Item();
		
		AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndUsaIntervaloCadastrado(Boolean.TRUE);
		AelExames aelExames = new AelExames();
		aelExames.setSigla("ABC");
		aelExamesMaterialAnalise.setAelExames(aelExames);
		AelMateriaisAnalises material = new AelMateriaisAnalises();
		material.setDescricao("TESTE");
		aelExamesMaterialAnalise.setAelMateriaisAnalises(material);
		aelUnfExecutaExames.setAelExamesMaterialAnalise(aelExamesMaterialAnalise);
		ise.setAelUnfExecutaExames(aelUnfExecutaExames);
		

		try {
			systemUnderTest.buscarIntervaloColeta(item, ise);
			Assert.fail("Deveria ocorrer uma exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_OBRIGATORIO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarIntervaloPertencenteExameSucesso() {
		List<AelIntervaloColeta> listaIntervalos = new ArrayList<AelIntervaloColeta>();
		AelIntervaloColeta intervalo = new AelIntervaloColeta();
		intervalo.setSeq(Short.valueOf("2"));
		
		AelIntervaloColeta intervalo2 = new AelIntervaloColeta();
		intervalo2.setSeq(Short.valueOf("2"));
		AelIntervaloColeta intervalo3 = new AelIntervaloColeta();
		intervalo3.setSeq(Short.valueOf("3"));
		listaIntervalos.add(intervalo2);
		listaIntervalos.add(intervalo3);
		

		try {
			systemUnderTest.verificarIntervaloPertencenteExame(listaIntervalos, intervalo , 0);
			
		} catch (BaseException e) {
			Assert.fail("Não deveria apresentar exceção: " + e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarIntervaloPertencenteExameErro() {
		List<AelIntervaloColeta> listaIntervalos = new ArrayList<AelIntervaloColeta>();
		AelIntervaloColeta intervalo = new AelIntervaloColeta();
		intervalo.setSeq(Short.valueOf("2"));
		
		AelIntervaloColeta intervalo2 = new AelIntervaloColeta();
		intervalo.setSeq(Short.valueOf("1"));
		AelIntervaloColeta intervalo3 = new AelIntervaloColeta();
		intervalo.setSeq(Short.valueOf("3"));
		listaIntervalos.add(intervalo2);
		listaIntervalos.add(intervalo3);
		

		try {
			systemUnderTest.verificarIntervaloPertencenteExame(listaIntervalos, intervalo , 0);
			Assert.fail("Deveria ocorrer uma exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_INVALIDO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarIntervaloPertencenteExameErro2() {
		List<AelIntervaloColeta> listaIntervalos = null;
		AelIntervaloColeta intervalo = new AelIntervaloColeta();
		intervalo.setSeq(Short.valueOf("2"));
		
		try {
			systemUnderTest.verificarIntervaloPertencenteExame(listaIntervalos, intervalo , 0);
			Assert.fail("Deveria ocorrer uma exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_INVALIDO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarRegiaoAnatomicaSucesso() {
		AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.FALSE);
		aelUnfExecutaExames.setAelExamesMaterialAnalise(aelExamesMaterialAnalise);
		ise.setAelUnfExecutaExames(aelUnfExecutaExames);
		
		try {
			systemUnderTest.verificarRegiaoAnatomica(ise);
		} catch (BaseException e) {
			Assert.fail("Exceção não esperada. " + e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarRegiaoAnatomicaErro() {
		AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndExigeRegiaoAnatomica(Boolean.TRUE);
		aelUnfExecutaExames.setAelExamesMaterialAnalise(aelExamesMaterialAnalise);
		ise.setAelUnfExecutaExames(aelUnfExecutaExames);
		
		try {
			systemUnderTest.verificarRegiaoAnatomica(ise);
			Assert.fail("Deveria ocorrer uma exceção");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_REGIAO_ANATOMICA);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarNumeroAmostrasSucesso() {
		AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		AelExamesMaterialAnalise aelExamesMaterialAnalise = new AelExamesMaterialAnalise();
		aelExamesMaterialAnalise.setIndSolicInformaColetas(Boolean.TRUE);
		aelUnfExecutaExames.setAelExamesMaterialAnalise(aelExamesMaterialAnalise);
		ise.setAelUnfExecutaExames(aelUnfExecutaExames);
		
		try {
			systemUnderTest.verificarNumeroAmostras(ise);
			Assert.assertTrue(ise.getNroAmostras() == 1);
		} catch (BaseException e) {
			Assert.fail("Exceção não esperada. " + e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarDataProgramadaColetaSucesso() {
		SolicitacaoExame solicitacao = new SolicitacaoExame();
		solicitacao.setDataHoraColeta("040120120908");
		
		try {
			Assert.assertNotNull(systemUnderTest.getDataProgramadaColeta(solicitacao));
		} catch (BaseException e) {
			Assert.fail("Exceção não esperada. " + e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarDataProgramadaColetaErroData() {
		SolicitacaoExame solicitacao = new SolicitacaoExame();
		solicitacao.setDataHoraColeta("300220122359");
		
		try {
			systemUnderTest.getDataProgramadaColeta(solicitacao);
			Assert.fail("Exceção Esperada não lançada.");
		} catch (BaseException e) {
			Assert.assertEquals(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_INVALIDA , e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarDataProgramadaColetaErroHora() {
		SolicitacaoExame solicitacao = new SolicitacaoExame();
		solicitacao.setDataHoraColeta("040120122459");
		
		try {
			systemUnderTest.getDataProgramadaColeta(solicitacao);
			Assert.fail("Exceção Esperada não lançada.");
		} catch (BaseException e) {
			Assert.assertEquals(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_INVALIDA , e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarDataProgramadaColetaErroMinuto() {
		SolicitacaoExame solicitacao = new SolicitacaoExame();
		solicitacao.setDataHoraColeta("040120120960");
		
		try {
			systemUnderTest.getDataProgramadaColeta(solicitacao);
			Assert.fail("Exceção Esperada não lançada.");
		} catch (BaseException e) {
			Assert.assertEquals(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_INVALIDA , e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void verificarDataProgramadaColetaErroDataNula() {
		SolicitacaoExame solicitacao = new SolicitacaoExame();
		solicitacao.setDataHoraColeta("");
		
		try {
			systemUnderTest.getDataProgramadaColeta(solicitacao);
			Assert.fail("Exceção Esperada não lançada.");
		} catch (BaseException e) {
			Assert.assertEquals(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_OBRIGATORIO , e.getCode());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}

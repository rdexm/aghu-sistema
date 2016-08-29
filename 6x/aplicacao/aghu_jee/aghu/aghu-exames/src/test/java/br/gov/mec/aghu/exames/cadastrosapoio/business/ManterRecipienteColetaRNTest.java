package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ManterRecipienteColetaRN.ManterRecipienteColetaRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelRecipienteColetaDAO;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterRecipienteColetaRNTest extends AGHUBaseUnitTest<ManterRecipienteColetaRN>{

	@Mock
	private AelRecipienteColetaDAO mockedAelRecipienteColetaDAO;
	@Mock
	private ManterTipoAmostraExameON mockedManterTipoAmostraExameON;
	
	/**
	 * Verifica se a alteracao ocorreu sem problemas.
	 */
	@Test
	public void testPreAtualizar001() {
		final AelRecipienteColeta entityMocked = new AelRecipienteColeta();
		entityMocked.setSeq(Integer.valueOf(1));
		entityMocked.setDescricao("equipo");
		
		Mockito.when(mockedAelRecipienteColetaDAO.obterOriginal(Mockito.anyInt())).thenReturn(entityMocked);

		final List<AelRecipienteColeta> lista = new ArrayList<AelRecipienteColeta>();
		lista.add(entityMocked);
		Mockito.when(mockedAelRecipienteColetaDAO.pesquisarDescricao(Mockito.any(AelRecipienteColeta.class))).thenReturn(lista);

		AelRecipienteColeta entity = new AelRecipienteColeta();
		entity.setSeq(Integer.valueOf(1));
		entity.setDescricao("equipo");
		
		try {
			systemUnderTest.preAtualizarRecipienteColeta(entity);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail();
		}
		
	}
	
	/**
	 * Verifica se a descricao foi alterada.
	 */
	@Test
	public void testPreAtualizar002() {
		final AelRecipienteColeta entityMocked = new AelRecipienteColeta();
		entityMocked.setSeq(Integer.valueOf(1));
		entityMocked.setDescricao("equipo1");
		
		Mockito.when(mockedAelRecipienteColetaDAO.obterOriginal(Mockito.anyInt())).thenReturn(entityMocked);

		final List<AelRecipienteColeta> lista = new ArrayList<AelRecipienteColeta>();
		lista.add(entityMocked);
		Mockito.when(mockedAelRecipienteColetaDAO.pesquisarDescricao(Mockito.any(AelRecipienteColeta.class))).thenReturn(lista);
		
		AelRecipienteColeta entity = new AelRecipienteColeta();
		entity.setSeq(Integer.valueOf(1));
		entity.setDescricao("equipo2");
		
		try {
			systemUnderTest.preAtualizarRecipienteColeta(entity);
			Assert.fail();
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterRecipienteColetaRNExceptionCode.AEL_00346);
		}
		
	}
	
	
	/**
	 * Verifica se a data foi alterada.
	 * 
	 */
	@Test
	public void testPreAtualizar003() {
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_WEEK, -1);
		
		final AelRecipienteColeta entityMocked = new AelRecipienteColeta();
		entityMocked.setSeq(Integer.valueOf(1));
		entityMocked.setDescricao("equipo1");
		entityMocked.setCriadoEm(calendar.getTime());
		
		Mockito.when(mockedAelRecipienteColetaDAO.obterOriginal(Mockito.anyInt())).thenReturn(entityMocked);

		final List<AelRecipienteColeta> lista = new ArrayList<AelRecipienteColeta>();
		lista.add(entityMocked);
		Mockito.when(mockedAelRecipienteColetaDAO.pesquisarDescricao(Mockito.any(AelRecipienteColeta.class))).thenReturn(lista);
		
		AelRecipienteColeta entity = new AelRecipienteColeta();
		entity.setSeq(Integer.valueOf(1));
		entity.setDescricao("equipo1");
		entity.setCriadoEm(new Date());
		
		try {
			systemUnderTest.preAtualizarRecipienteColeta(entity);
			Assert.fail();
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterRecipienteColetaRNExceptionCode.AEL_00369);
		}
		
	}
	
	/**
	 * Verifica se o tipo de situacao nao e de A para I. 
	 */
	@Test
	public void testPreAtualizar004() {
		final AelRecipienteColeta entityMocked = new AelRecipienteColeta();
		entityMocked.setSeq(Integer.valueOf(1));
		entityMocked.setDescricao("equipo1");
		entityMocked.setIndSituacao(DominioSituacao.A);
		
		Mockito.when(mockedAelRecipienteColetaDAO.obterOriginal(Mockito.anyInt())).thenReturn(entityMocked);

		final List<AelRecipienteColeta> lista = new ArrayList<AelRecipienteColeta>();
		lista.add(entityMocked);
		Mockito.when(mockedAelRecipienteColetaDAO.pesquisarDescricao(Mockito.any(AelRecipienteColeta.class))).thenReturn(lista);

		AelRecipienteColeta entity = new AelRecipienteColeta();
		entity.setSeq(Integer.valueOf(1));
		entity.setDescricao("equipo1");
		entity.setIndSituacao(DominioSituacao.A);
		
		try {
			systemUnderTest.preAtualizarRecipienteColeta(entity);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	
	}
	
	/**
	 * Verifica se o tipo de situacao esta passando de A para I. 
	 */
	@Test
	public void testPreAtualizar005() {
		final AelRecipienteColeta entityMocked = new AelRecipienteColeta();
		entityMocked.setSeq(Integer.valueOf(1));
		entityMocked.setDescricao("equipo1");
		entityMocked.setIndSituacao(DominioSituacao.A);
		
		Mockito.when(mockedAelRecipienteColetaDAO.obterOriginal(Mockito.anyInt())).thenReturn(entityMocked);

		final List<AelRecipienteColeta> lista = new ArrayList<AelRecipienteColeta>();
		lista.add(entityMocked);
		Mockito.when(mockedAelRecipienteColetaDAO.pesquisarDescricao(Mockito.any(AelRecipienteColeta.class))).thenReturn(lista);

		Mockito.when(mockedManterTipoAmostraExameON.temTipoAmostraExame(Mockito.any(AelRecipienteColeta.class))).thenReturn(Boolean.FALSE);

		AelRecipienteColeta entity = new AelRecipienteColeta();
		entity.setSeq(Integer.valueOf(1));
		entity.setDescricao("equipo1");
		entity.setIndSituacao(DominioSituacao.I);
		
		try {
			systemUnderTest.preAtualizarRecipienteColeta(entity);
			Assert.assertTrue(true);
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}
	
	}
	
	
	/**
	 * Verifica se tem exame. 
	 */
	@Test
	public void testPreAtualizar006() {
		final AelRecipienteColeta entityMocked = new AelRecipienteColeta();
		entityMocked.setSeq(Integer.valueOf(1));
		entityMocked.setDescricao("equipo1");
		entityMocked.setIndSituacao(DominioSituacao.A);
		
		Mockito.when(mockedAelRecipienteColetaDAO.obterOriginal(Mockito.anyInt())).thenReturn(entityMocked);

		final List<AelRecipienteColeta> lista = new ArrayList<AelRecipienteColeta>();
		lista.add(entityMocked);
		Mockito.when(mockedAelRecipienteColetaDAO.pesquisarDescricao(Mockito.any(AelRecipienteColeta.class))).thenReturn(lista);

		Mockito.when(mockedManterTipoAmostraExameON.temTipoAmostraExame(Mockito.any(AelRecipienteColeta.class))).thenReturn(Boolean.TRUE);
		
		AelRecipienteColeta entity = new AelRecipienteColeta();
		entity.setSeq(Integer.valueOf(1));
		entity.setDescricao("equipo1");
		entity.setIndSituacao(DominioSituacao.I);
		
		try {
			systemUnderTest.preAtualizarRecipienteColeta(entity);
			Assert.fail();
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(e.getCode() == ManterRecipienteColetaRNExceptionCode.AEL_01102);
		}
	
	}
	
}

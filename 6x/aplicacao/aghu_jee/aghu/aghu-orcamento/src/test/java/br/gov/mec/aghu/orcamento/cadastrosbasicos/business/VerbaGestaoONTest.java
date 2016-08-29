package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioPrioridadeFonteRecurso;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.VerbaGestaoON.CadastroVerbaGestaoONExceptionCode;
import br.gov.mec.aghu.orcamento.dao.FsoFontesXVerbaGestaoDAO;
import br.gov.mec.aghu.orcamento.dao.FsoVerbaGestaoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class VerbaGestaoONTest extends AGHUBaseUnitTest<VerbaGestaoON>{
	
	@Mock
	private FsoVerbaGestaoDAO mockedVerbaDAO;
	@Mock
	private FsoFontesXVerbaGestaoDAO mockedFontesXVerbaDAO;
	
	@SuppressWarnings("deprecation")
	@Test
	public void validaPeriodoVerbaGestao() throws ApplicationBusinessException {
		try {
			final FsoVerbaGestao verbaGestao = new FsoVerbaGestao();
			Date dataInicial = new Date();			 
			Date dataFinal = new Date();
			
			dataInicial.setDate(dataInicial.getDate() + 10);
			
			verbaGestao.setDtIniConv(dataInicial);
			verbaGestao.setDtFimConv(dataFinal);
			
			Mockito.when(mockedVerbaDAO.obterOriginal(Mockito.any(FsoVerbaGestao.class))).thenReturn(verbaGestao);
			
			systemUnderTest.gravaFontesRecursoXVerbaGestao(verbaGestao, null, null);
			fail("Deveria ter ocorrido uma exceção com Verba de Gestão com data de vigência final menor que vigência inicial !");

		}catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					CadastroVerbaGestaoONExceptionCode.DATA_INICIAL_MAIOR_QUE_FINAL_VERBA_GESTAO,
					e.getCode());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void validaPeriodoFonteRecurso() throws ApplicationBusinessException {
		try {
			List<FsoFontesXVerbaGestao> listaFonteXVerba  = new ArrayList<FsoFontesXVerbaGestao>();
			FsoFontesXVerbaGestao FonteRecurso = new FsoFontesXVerbaGestao();
			Date dataInicial = new Date();			 
			Date dataFinal = new Date();
			
			dataInicial.setDate(dataInicial.getDate() + 10);
			
			FonteRecurso.setDtVigIni(dataInicial);
			FonteRecurso.setDtVigFim(dataFinal);
			
			listaFonteXVerba.add(FonteRecurso);
			
			systemUnderTest.gravaFontesRecursoXVerbaGestao(null, listaFonteXVerba, null);
			fail("Deveria ter ocorrido uma exceção de Fonte de Recurso com data de vigência final menor que vigência inicial!");

		}catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					CadastroVerbaGestaoONExceptionCode.DATA_INICIAL_MAIOR_QUE_FINAL_FONTE_RECURSO,
					e.getCode());
		}
	}
	
	@Test
	public void verbaGestaoSemFonteRecurso() throws ApplicationBusinessException {
		try {
			final FsoVerbaGestao verbaGestao = new FsoVerbaGestao();
			verbaGestao.setSituacao(DominioSituacao.A);

			Mockito.when(mockedVerbaDAO.obterOriginal(Mockito.any(FsoVerbaGestao.class))).thenReturn(verbaGestao);

			systemUnderTest.gravaFontesRecursoXVerbaGestao(verbaGestao, Collections.EMPTY_LIST, null);
			fail("Deveria ter ocorrido uma exceção de Verba de Gestão sem fontes de recurso!");

		}catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					CadastroVerbaGestaoONExceptionCode.MENSAGEM_FALTA_FONTE_RECURSO_ATIVA,
					e.getCode());
		}
	}
		
	@SuppressWarnings("deprecation")
	@Test
	public void validaInterseccaoVigenciaFonteRecurso() throws ApplicationBusinessException {
		try {
			final FsoVerbaGestao verbaGestao = new FsoVerbaGestao();
			List<FsoFontesXVerbaGestao> listaFonteXVerba  = new ArrayList<FsoFontesXVerbaGestao>();
			FsoFontesRecursoFinanc fonteRecurso = new FsoFontesRecursoFinanc();
			FsoFontesXVerbaGestao fonte1 = new FsoFontesXVerbaGestao();
			FsoFontesXVerbaGestao fonte2 = new FsoFontesXVerbaGestao();
			
			verbaGestao.setSituacao(DominioSituacao.A);

			fonteRecurso.setCodigo(Long.valueOf(1));
			fonte1.setFonteRecursoFinanceiro(fonteRecurso);
			fonte2.setFonteRecursoFinanceiro(fonteRecurso);
			
			fonte1.setIndPrioridade(DominioPrioridadeFonteRecurso.PRIMARIO);
			fonte2.setIndPrioridade(DominioPrioridadeFonteRecurso.SECUNDARIO);
			
			Date dataInicial1 = new Date();			 
			Date dataFinal1 = new Date();		
			dataFinal1.setDate(dataFinal1.getDate() + 10);
			
			fonte1.setDtVigIni(dataInicial1);
			fonte1.setDtVigFim(dataFinal1);
			
			Date dataInicial2 = new Date();			 
			Date dataFinal2 = new Date();
			dataInicial2.setDate(dataInicial2.getDate() + 1);
			dataFinal2.setDate(dataFinal2.getDate() + 11);
			
			fonte2.setDtVigIni(dataInicial2);
			fonte2.setDtVigFim(dataFinal2);
			
			listaFonteXVerba.add(fonte1);
			listaFonteXVerba.add(fonte2);
			

			Mockito.when(mockedVerbaDAO.obterOriginal(Mockito.any(FsoVerbaGestao.class))).thenReturn(verbaGestao);

			systemUnderTest.gravaFontesRecursoXVerbaGestao(verbaGestao, listaFonteXVerba, Collections.EMPTY_LIST);
			fail("Deveria ter ocorrido uma exceção com intervalo de datas sobrepostas!!!");

		}catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					CadastroVerbaGestaoONExceptionCode.MENSAGEM_FONTE_RECURSO_JA_VINCULADA,
					e.getCode());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void validaPrioridadeFonteRecurso() throws ApplicationBusinessException {
		try {
			final FsoVerbaGestao verbaGestao = new FsoVerbaGestao();
			verbaGestao.setSituacao(DominioSituacao.A);
			
			List<FsoFontesXVerbaGestao> listaFonteXVerba  = new ArrayList<FsoFontesXVerbaGestao>();
			FsoFontesRecursoFinanc fonteRecurso1 = new FsoFontesRecursoFinanc();
			FsoFontesRecursoFinanc fonteRecurso2 = new FsoFontesRecursoFinanc();
			FsoFontesXVerbaGestao fonte1 = new FsoFontesXVerbaGestao();
			FsoFontesXVerbaGestao fonte2 = new FsoFontesXVerbaGestao();

			fonteRecurso1.setCodigo(Long.valueOf(1));
			fonteRecurso2.setCodigo(Long.valueOf(2));
			fonte1.setFonteRecursoFinanceiro(fonteRecurso1);
			fonte2.setFonteRecursoFinanceiro(fonteRecurso2);

			fonte1.setIndPrioridade(DominioPrioridadeFonteRecurso.PRIMARIO);
			fonte2.setIndPrioridade(DominioPrioridadeFonteRecurso.PRIMARIO);
			
			// Atributos para passar em validações anteriores
			Date dataInicial1 = new Date();			 
			Date dataFinal1 = new Date();		
			dataFinal1.setDate(dataFinal1.getDate() + 4);			
			fonte1.setDtVigIni(dataInicial1);
			fonte1.setDtVigFim(dataFinal1);
			
			Date dataInicial2 = new Date();			 
			Date dataFinal2 = new Date();
			dataInicial2.setDate(dataInicial2.getDate() + 2);
			dataFinal2.setDate(dataFinal2.getDate() + 6);			
			fonte2.setDtVigIni(dataInicial2);
			fonte2.setDtVigFim(dataFinal2);		
			
			listaFonteXVerba.add(fonte1);
			listaFonteXVerba.add(fonte2);

			Mockito.when(mockedVerbaDAO.obterOriginal(Mockito.any(FsoVerbaGestao.class))).thenReturn(verbaGestao);

			systemUnderTest.gravaFontesRecursoXVerbaGestao(verbaGestao, listaFonteXVerba, Collections.EMPTY_LIST);
			fail("Deveria ter ocorrido uma exceção com fontes de recurso com mesma prioridade!");

		}catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					CadastroVerbaGestaoONExceptionCode.MENSAGEM_FONTE_RECURSO_PRIORIDADE,
					e.getCode());
		}
	}

}

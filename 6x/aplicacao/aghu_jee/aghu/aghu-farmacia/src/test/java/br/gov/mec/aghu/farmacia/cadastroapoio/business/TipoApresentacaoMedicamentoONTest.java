package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe de teste unitário para TipoApresentacaoMedicamentoON
 * 
 * @author lcmoura
 * 
 */
public class TipoApresentacaoMedicamentoONTest extends AGHUBaseUnitTest<TipoApresentacaoMedicamentoON> {

	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoIn;
	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoAt;
	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoAtOld;

	@Mock
	private AfaTipoApresentacaoMedicamentoDAO mockedAfaTipoApresentacaoMedicamentoDAO;
	@Mock
	private TipoApresentacaoMedicamentoRN mockedTipoApresentacaoMedicamentoRN;



	@Test
	public void atualizar() throws ApplicationBusinessException {
		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.obterPorChavePrimaria(Mockito.any(AfaTipoApresentacaoMedicamento.class))).thenReturn(tipoApresentacaoMedicamentoAtOld);

		AfaTipoApresentacaoMedicamento retorno = systemUnderTest
				.inserirAtualizar(tipoApresentacaoMedicamentoAt, Boolean.FALSE);

		Assert.assertEquals(tipoApresentacaoMedicamentoAt, retorno);
	}

	public void obterPorChavePrimaria() throws ApplicationBusinessException {
		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.obterPorChavePrimaria(Mockito.any(AfaTipoApresentacaoMedicamento.class))).thenReturn(tipoApresentacaoMedicamentoAtOld);
		
		AfaTipoApresentacaoMedicamento retorno = systemUnderTest
				.obterPorChavePrimaria(tipoApresentacaoMedicamentoAtOld.getSigla());

		Assert.assertEquals(tipoApresentacaoMedicamentoAtOld, retorno);
	}

	@Test
	public void pesquisar() throws ApplicationBusinessException {		
		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.pesquisar(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(AfaTipoApresentacaoMedicamento.class)))
				.thenReturn(Collections.singletonList(tipoApresentacaoMedicamentoIn));

		List<AfaTipoApresentacaoMedicamento> result = systemUnderTest.pesquisar(1, 10, "sigla", true, tipoApresentacaoMedicamentoAt);

		Assert.assertEquals(Collections.singletonList(tipoApresentacaoMedicamentoIn), result);
	}

	@Test
	public void pesquisarCount() throws ApplicationBusinessException {
		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.pesquisarCount(Mockito.any(AfaTipoApresentacaoMedicamento.class))).thenReturn(Long.valueOf(1));

		Long result = systemUnderTest.pesquisarCount(tipoApresentacaoMedicamentoAt);
		Assert.assertEquals(Long.valueOf(1), result);
	}
}

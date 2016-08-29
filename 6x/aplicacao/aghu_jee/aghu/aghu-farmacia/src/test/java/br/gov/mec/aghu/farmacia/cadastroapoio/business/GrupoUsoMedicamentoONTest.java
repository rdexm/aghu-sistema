package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoUsoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;

/**
 * Classe de teste unitário para GrupoUsoMedicamentoON
 * 
 * @author lcmoura
 * 
 */
public class GrupoUsoMedicamentoONTest extends AGHUBaseUnitTest<GrupoUsoMedicamentoON> {

	private static final String DESCRICAO = "descrição";
	private AfaGrupoUsoMedicamento grupoUsoMedicamentoIn;
	private AfaGrupoUsoMedicamento grupoUsoMedicamentoAt;
	private AfaGrupoUsoMedicamento grupoUsoMedicamentoAtOld;

	@Mock
	private AfaGrupoUsoMedicamentoDAO mockedAfaGrupoUsoMedicamentoDAO;
	
	@Mock
	private GrupoUsoMedicamentoRN mockedGrupoUsoMedicamentoRN;

	@Test
	public void inserir() throws ApplicationBusinessException {
		grupoUsoMedicamentoAtOld = new AfaGrupoUsoMedicamento(Integer.valueOf(2), DESCRICAO.toUpperCase(), DominioIndRespAvaliacao.C);
		Mockito.when(mockedAfaGrupoUsoMedicamentoDAO.obterPorChavePrimaria(Mockito.any(AfaGrupoUsoMedicamento.class))).thenReturn(grupoUsoMedicamentoAtOld);
		
		AfaGrupoUsoMedicamento parametro = new AfaGrupoUsoMedicamento();
		AfaGrupoUsoMedicamento retorno = systemUnderTest.inserirAtualizar(parametro);

		Assert.assertEquals(parametro, retorno);
	}

	@Test
	public void atualizar() throws ApplicationBusinessException {
		grupoUsoMedicamentoAtOld = new AfaGrupoUsoMedicamento(Integer.valueOf(2), DESCRICAO.toUpperCase(), DominioIndRespAvaliacao.C);
		grupoUsoMedicamentoAt = new AfaGrupoUsoMedicamento(Integer.valueOf(2), DESCRICAO, DominioIndRespAvaliacao.C);

		Mockito.when(mockedAfaGrupoUsoMedicamentoDAO.obterPorChavePrimaria(Mockito.any(AfaGrupoUsoMedicamento.class))).thenReturn(grupoUsoMedicamentoAtOld);
		Mockito.when(mockedAfaGrupoUsoMedicamentoDAO.atualizar(Mockito.any(AfaGrupoUsoMedicamento.class))).thenReturn(grupoUsoMedicamentoAt);

		AfaGrupoUsoMedicamento retorno = systemUnderTest.inserirAtualizar(grupoUsoMedicamentoAt);
		Assert.assertEquals(grupoUsoMedicamentoAt, retorno);
	}

	public void obterPorChavePrimaria() throws ApplicationBusinessException {

		AfaGrupoUsoMedicamento retorno = systemUnderTest
				.obterPorChavePrimaria(grupoUsoMedicamentoAtOld.getSeq());

		Assert.assertEquals(grupoUsoMedicamentoAtOld, retorno);
	}

	@Test
	public void pesquisar() throws ApplicationBusinessException {

		Mockito.when(mockedAfaGrupoUsoMedicamentoDAO.pesquisar(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(AfaGrupoUsoMedicamento.class)))
				.thenReturn(Collections.singletonList(grupoUsoMedicamentoIn));
		
		List<AfaGrupoUsoMedicamento> result = systemUnderTest.pesquisar(1, 10, "seq", true, grupoUsoMedicamentoAt);

		Assert.assertEquals(Collections.singletonList(grupoUsoMedicamentoIn), result);

	}

	@Test
	public void pesquisarCount() throws ApplicationBusinessException {

		Mockito.when(mockedAfaGrupoUsoMedicamentoDAO.pesquisarCount(Mockito.any(AfaGrupoUsoMedicamento.class))).thenReturn(Long.valueOf(1));
		Long result = systemUnderTest.pesquisarCount(grupoUsoMedicamentoAt);

		Assert.assertEquals(Long.valueOf(1), result);
	}
}

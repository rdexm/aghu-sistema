package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoReinternacaoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe de teste unitário para MotivoReinternacaoON
 * 
 * @author lcmoura
 * 
 */
public class MotivoReinternacaoONTest extends AGHUBaseUnitTest<MotivoReinternacaoON> {

	private static final String DESCRICAO = "descrição";
	private MpmMotivoReinternacao motivoReinternacaoIn;
	private MpmMotivoReinternacao motivoReinternacaoAt;
	private MpmMotivoReinternacao motivoReinternacaoAtOld;

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private MotivoReinternacaoRN mockedMotivoReinternacaoRN;
	@Mock
	private MpmMotivoReinternacaoDAO mockedMpmMotivoReinternacaoDAO;

	@Before
	public void doBeforeEachTestCase() {

		motivoReinternacaoIn = new MpmMotivoReinternacao(null, DESCRICAO, DominioSituacao.A, Boolean.TRUE,
				Boolean.TRUE);
		motivoReinternacaoAt = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO, DominioSituacao.A, Boolean.TRUE,
				Boolean.TRUE);
		motivoReinternacaoAtOld = new MpmMotivoReinternacao(Integer.valueOf(1), DESCRICAO, DominioSituacao.A,
				Boolean.TRUE, Boolean.TRUE);
	}

	@Test
	public void inserir() throws ApplicationBusinessException {

		Mockito.when(
				mockedPrescricaoMedicaFacade.inserirMpmMotivoReinternacao(Mockito.any(MpmMotivoReinternacao.class)))
				.thenReturn(motivoReinternacaoIn);

		Mockito.when(
				mockedPrescricaoMedicaFacade.atualizarMpmMotivoReinternacao(Mockito.any(MpmMotivoReinternacao.class)))
				.thenReturn(motivoReinternacaoAt);

		MpmMotivoReinternacao retorno = systemUnderTest.inserirAtualizar(new MpmMotivoReinternacao());
		Assert.assertEquals(motivoReinternacaoIn, retorno);
	}

	@Test
	public void atualizar() throws ApplicationBusinessException {

		Mockito.when(
				mockedPrescricaoMedicaFacade.inserirMpmMotivoReinternacao(Mockito.any(MpmMotivoReinternacao.class)))
				.thenReturn(motivoReinternacaoIn);

		Mockito.when(
				mockedPrescricaoMedicaFacade.atualizarMpmMotivoReinternacao(Mockito.any(MpmMotivoReinternacao.class)))
				.thenReturn(motivoReinternacaoAt);

		Mockito.when(mockedPrescricaoMedicaFacade.obterMotivoReinternacaoPeloId(Mockito.anyInt())).thenReturn(null);

		MpmMotivoReinternacao retorno = systemUnderTest.inserirAtualizar(motivoReinternacaoAtOld);
		Assert.assertEquals(motivoReinternacaoAt, retorno);
	}

	public void obterPorChavePrimaria() throws ApplicationBusinessException {

		MpmMotivoReinternacao retorno = systemUnderTest.obterPorChavePrimaria(Integer.valueOf(1));
		Assert.assertEquals(motivoReinternacaoAtOld, retorno);
	}

	@Test
	public void remover() throws BaseException {

		systemUnderTest.remover(motivoReinternacaoIn.getSeq());
	}

	@Test
	public void pesquisar() throws ApplicationBusinessException {

		Mockito.when(mockedPrescricaoMedicaFacade.pesquisarMpmMotivoReinternacao(Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(MpmMotivoReinternacao.class)))
				.thenReturn(Collections.singletonList(motivoReinternacaoIn));

		List<MpmMotivoReinternacao> result = systemUnderTest.pesquisar(1, 10, "seq", true, motivoReinternacaoAt);

		Assert.assertEquals(Collections.singletonList(motivoReinternacaoIn), result);

	}

	@Test
	public void pesquisarCount() throws ApplicationBusinessException {

		Mockito.when(mockedPrescricaoMedicaFacade
				.pesquisarMpmMotivoReinternacaoCount(Mockito.any(MpmMotivoReinternacao.class))).thenReturn(1l);

		Long result = systemUnderTest.pesquisarCount(motivoReinternacaoAt);
		Assert.assertEquals(Long.valueOf(1), result);
	}
}

package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.business.VerificarPrescricaoONTest;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ManterDietasModeloBasicoON.<br>
 * Criado com base no teste {@link VerificarPrescricaoONTest}.
 * 
 *@author cvagheti
 * 
 */
public class ManterDietasModeloBasicoONTest extends AGHUBaseUnitTest<ManterDietasModeloBasicoON>{
	
	@Mock
	private INutricaoFacade mockedNutricaoFacade;
	@Mock
	private MpmItemModeloBasicoDietaDAO mockedMpmItemModeloBasicoDietaDAO;
	@Mock
	private MpmModeloBasicoDietaDAO mockedMpmModeloBasicoDietaDAO;
	@Mock
	private MpmModeloBasicoPrescricaoDAO mockedMpmModeloBasicoPrescricaoDAO;
	@Mock
	private MpmTipoFrequenciaAprazamentoDAO mockedMpmTipoFrequenciaAprazamentoDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void inserirItemDietaTest() {
		final MpmItemModeloBasicoDieta itemDieta = new MpmItemModeloBasicoDieta();
		final MpmModeloBasicoDieta modeloBasicoDieta = new MpmModeloBasicoDieta(
				new MpmModeloBasicoDietaId(1, 1));
		final AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta(1);
		tipoItemDieta.setIndItemUnico(false);
		final MpmModeloBasicoPrescricao modeloBasico = new MpmModeloBasicoPrescricao(
				1);

		modeloBasicoDieta.setModeloBasicoPrescricao(modeloBasico);
		itemDieta.setModeloBasicoDieta(modeloBasicoDieta);
		itemDieta.setTipoItemDieta(tipoItemDieta);
		final RapServidores servidor = new RapServidores(new RapServidoresId(1,
				(short) 1));
		itemDieta.setServidor(servidor);
		modeloBasico.setServidor(servidor);
		
		Mockito.when(mockedMpmModeloBasicoDietaDAO.obterPorChavePrimaria(Mockito.any(MpmModeloBasicoDietaId.class))).thenReturn(modeloBasicoDieta);

		Mockito.when(mockedNutricaoFacade.obterAnuTipoItemDietaPorChavePrimaria(Mockito.anyInt())).thenReturn(tipoItemDieta);

		Mockito.when(mockedNutricaoFacade.obterAnuTipoItemDietaPorChavePrimaria(Mockito.anyInt())).thenReturn(tipoItemDieta);

		Mockito.when(mockedMpmItemModeloBasicoDietaDAO.obterPorChavePrimaria(Mockito.any(MpmItemModeloBasicoDietaId.class))).thenReturn(null);
		
		Mockito.when(mockedMpmItemModeloBasicoDietaDAO.pesquisar(1, 1)).thenReturn(new ArrayList<MpmItemModeloBasicoDieta>());
		
		Mockito.when(mockedMpmModeloBasicoPrescricaoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(modeloBasico);

		Mockito.when(mockedRegistroColaboradorFacade.buscaServidor(Mockito.any(RapServidoresId.class))).thenReturn(servidor);

		try {
			whenObterServidorLogado();
			systemUnderTest.inserir(itemDieta);

			// atributos de auditoria informados
			Assert.assertNotNull(itemDieta.getServidor());
			Assert.assertNotNull(itemDieta.getCriadoEm());

		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void obterItemDietaTest() {

		final MpmItemModeloBasicoDieta itemModeloBasicoDieta = new MpmItemModeloBasicoDieta(
				new MpmItemModeloBasicoDietaId(99, 99, 99));

		Mockito.when(mockedMpmItemModeloBasicoDietaDAO.obterPorChavePrimaria(Mockito.any(MpmItemModeloBasicoDietaId.class))).thenReturn(itemModeloBasicoDieta);

		try {
			systemUnderTest.obterItemDieta(null, 1, 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}

		try {
			systemUnderTest.obterItemDieta(1, null, 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}

		try {
			systemUnderTest.obterItemDieta(1, 1, null);
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void obterItemDietaNaoExistente() {

		Mockito.when(mockedMpmItemModeloBasicoDietaDAO.obterPorChavePrimaria(Mockito.any(MpmItemModeloBasicoDietaId.class))).thenReturn(null);

		try {
			systemUnderTest.obterItemDieta(1, 1, 1);

			Assert.fail("Não retornou excecao esperada");

		} catch (ApplicationBusinessException e) {
			Assert
					.assertTrue(e
							.getCode()
							.equals(
									ManterDietasModeloBasicoON.ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ITEM_NAO_INFORMADO));
		}
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

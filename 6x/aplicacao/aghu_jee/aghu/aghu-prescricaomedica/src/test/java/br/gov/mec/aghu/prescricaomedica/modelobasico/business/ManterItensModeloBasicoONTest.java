package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimentoId;
import br.gov.mec.aghu.prescricaomedica.business.VerificarPrescricaoONTest;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoProcedimentoDAO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ManterItensModeloBasicoON.<br>
 * Criado com base no teste {@link VerificarPrescricaoONTest}.
 * 
 * @author cvagheti
 * 
 */
public class ManterItensModeloBasicoONTest extends AGHUBaseUnitTest<ManterItensModeloBasicoON>{
	
	private static final Log log = LogFactory.getLog(ManterItensModeloBasicoONTest.class);

	@Mock
	protected ManterDietasModeloBasicoON mockedManterDietasModeloBasicoON;
	@Mock
	protected ManterCuidadosModeloBasicoON mockedManterCuidadosModeloBasicoON;
	@Mock
	protected ManterMedicamentosModeloBasicoON mockedManterMedicamentosModeloBasicoON;
	@Mock
	protected MpmModeloBasicoDietaDAO mockedModeloBasicoDietaDAO;
	@Mock
	protected MpmModeloBasicoPrescricaoDAO mockedMpmModeloBasicoPrescricaoDAO;
	@Mock
	protected MpmModeloBasicoCuidadoDAO mockedModeloBasicoCuidadoDAO;
	@Mock
	protected MpmModeloBasicoMedicamentoDAO mockedModeloBasicoMedicamentoDAO;
	@Mock
	protected MpmModeloBasicoProcedimentoDAO mockedModeloBasicoProcedimentoDAO;
	@Mock
	protected MpmModeloBasicoModoUsoProcedimentoDAO mockedMpmModeloBasicoModoUsoProcedimentoDAO;

	@Test
	public void descricaoEditadaDietaTest() {

		MpmModeloBasicoDietaId id = new MpmModeloBasicoDietaId(1, 1);
		MpmModeloBasicoDieta dieta = new MpmModeloBasicoDieta(id);
		dieta.setObservacao("observacao");
		dieta.setIndBombaInfusao(false);

		final List<MpmItemModeloBasicoDieta> itens = new ArrayList<MpmItemModeloBasicoDieta>();

		MpmItemModeloBasicoDieta e = new MpmItemModeloBasicoDieta(
				new MpmItemModeloBasicoDietaId(1, 1, 1));

		AnuTipoItemDieta tipoItemDieta = new AnuTipoItemDieta();
		tipoItemDieta.setDescricao("Descrição 1");

		e.setTipoItemDieta(tipoItemDieta);

		itens.add(e);

		// outro item
		e = new MpmItemModeloBasicoDieta(
				new MpmItemModeloBasicoDietaId(1, 2, 2));

		tipoItemDieta = new AnuTipoItemDieta();
		tipoItemDieta.setDescricao("Descrição 2");

		e.setTipoItemDieta(tipoItemDieta);

		itens.add(e);

		Mockito.when(mockedManterDietasModeloBasicoON.obterListaItensDieta(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itens);

		// este método está sendo testado
		String descricao = systemUnderTest.getDescricaoEditadaDieta(dieta);

		Assert.assertNotNull(descricao);
		Assert.assertTrue(descricao.indexOf("observacao") > -1);

	}

	/**
	 * Teste de exclusão de item de dieta
	 */
	@Test
	public void excluirDietaTest() {

		final MpmModeloBasicoDieta object = new MpmModeloBasicoDieta(
				new MpmModeloBasicoDietaId(1, 1));
		object.setObservacao("Observação do teste de exclusão");

		// teste método de exclusão
		try {
			systemUnderTest.excluir(object);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	/**
	 * Teste de exclusão de cuidado
	 */
	@Test
	public void excluirCuidadoTest() {

		final MpmModeloBasicoCuidado object = new MpmModeloBasicoCuidado();
		
		// teste método de exclusão
		try {
			systemUnderTest.excluir(object);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	/**
	 * Teste de exclusão de cuidado
	 */
	@Test
	public void excluirMedicamentoTest() {

		final MpmModeloBasicoMedicamento object = new MpmModeloBasicoMedicamento();
		object.setId(new MpmModeloBasicoMedicamentoId());
		
		// teste método de exclusão
		try {
			systemUnderTest.excluir(object);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	/**
	 * Teste de exclusão de procedimento
	 */
	@Test
	public void excluirProcedimentoTest() {

		final MpmModeloBasicoProcedimento object = new MpmModeloBasicoProcedimento();
		object.setId(new MpmModeloBasicoProcedimentoId());
		
		// teste método de exclusão
		try {
			systemUnderTest.excluir(object);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	/**
	 * Teste de exclusão passando um tipo não tratado pelo método de exclusão,
	 * deve passar pelo else de excessão do método.
	 */
	@Test
	public void excluirComFalhaTest() {

		final Object tipoErrado = new Object();

		// teste de exclusão
		try {
			systemUnderTest.excluir(tipoErrado);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail();
		}

	}
}

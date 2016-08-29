package br.gov.mec.aghu.farmacia.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.ItemGrupoMedicamentoRN.ItemGrupoMedicamentoRNExceptionCode;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ItemGrupoMedicamentoRNTest extends AGHUBaseUnitTest<ItemGrupoMedicamentoRN>{

	private static final Log log = LogFactory.getLog(ItemGrupoMedicamentoRNTest.class);
	
	/**
	 * Valida os casos em que lança a exceção AFA_00433.
	 */
	@Test
	public void verificaMedicamentoTest() {
		final AfaGrupoMedicamento grupoMedicamentoInativo = new AfaGrupoMedicamento();
		grupoMedicamentoInativo.setSituacao(DominioSituacao.I);

		try {
			systemUnderTest.verificaMedicamento(null, null);
			Assert.fail("Deveria ter lançado exceção AFA_00433");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00433", e
					.getCode(), ItemGrupoMedicamentoRNExceptionCode.AFA_00433);
		}
		
		try {
			systemUnderTest.verificaMedicamento(grupoMedicamentoInativo, null);
			Assert.fail("Deveria ter lançado exceção AFA_00433");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00433", e
					.getCode(), ItemGrupoMedicamentoRNExceptionCode.AFA_00433);
		}
	}

	/**
	 * Valida os casos em que lança a exceção AFA_00434.
	 */
	@Test
	public void verificaMedicamentoTest2() {
		final AfaGrupoMedicamento grupoMedicamentoAtivo = new AfaGrupoMedicamento();
		grupoMedicamentoAtivo.setSituacao(DominioSituacao.A);
		final AfaMedicamento medicamentoPendente = new AfaMedicamento();
		medicamentoPendente.setIndSituacao(DominioSituacaoMedicamento.P);

		try {
			systemUnderTest.verificaMedicamento(grupoMedicamentoAtivo,
					null);
			Assert.fail("Deveria ter lançado exceção AFA_00434");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00434", e
					.getCode(), ItemGrupoMedicamentoRNExceptionCode.AFA_00434);
		}

		try {
			systemUnderTest.verificaMedicamento(grupoMedicamentoAtivo,
					medicamentoPendente);
			Assert.fail("Deveria ter lançado exceção AFA_00434");
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.assertEquals("Exceção lançada deveria ser a AFA_00434", e
					.getCode(), ItemGrupoMedicamentoRNExceptionCode.AFA_00434);
		}
	}

	/**
	 * Valida o caso em que não ocorre exceção
	 */
	@Test
	public void verificaMedicamentoTest3() {
		try {
			final AfaGrupoMedicamento grupoMedicamentoAtivo = new AfaGrupoMedicamento();
			grupoMedicamentoAtivo.setSituacao(DominioSituacao.A);
			final AfaMedicamento medicamentoAtivo = new AfaMedicamento();
			medicamentoAtivo.setIndSituacao(DominioSituacaoMedicamento.A);

			// Retorna grupoMedicamento e medicamento com situação ativa
			systemUnderTest.verificaMedicamento(grupoMedicamentoAtivo,
					medicamentoAtivo);
		} catch (BaseException e) {
			log.error(e.getMessage());
			Assert.fail("Ocorreu uma exceção: " + e.getMessage());
		}
	}

}

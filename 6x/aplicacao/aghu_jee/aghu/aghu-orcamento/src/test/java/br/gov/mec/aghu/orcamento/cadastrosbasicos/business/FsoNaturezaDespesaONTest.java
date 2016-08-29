package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.FsoNaturezaDespesaON.FsoNaturezaDespesaONExceptionCode;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário da classe FsoNaturezaDespesaON
 * 
 * @author amenegotto
 */
public class FsoNaturezaDespesaONTest extends AGHUBaseUnitTest<FsoNaturezaDespesaON> {

	@Mock
	private FsoNaturezaDespesaDAO mockedFsoNaturezaDespesaDAO;

	@Test
	public void testInclusaoNaturezaAtivaEmGrupoInativo() {
		// cria um grupo inativo
		FsoGrupoNaturezaDespesa objGrupo = new FsoGrupoNaturezaDespesa(1, "Grupo Inativo", DominioSituacao.I);

		// cria uma natureza ativa
		FsoNaturezaDespesa objNatureza = new FsoNaturezaDespesa();

		objNatureza.setId(new FsoNaturezaDespesaId((Integer) 1, (byte) 12));
		objNatureza.setDescricao("Natureza 1");
		objNatureza.setIndSituacao(DominioSituacao.A);
		objNatureza.setGrupoNaturezaDespesa(objGrupo);
		objNatureza.setCodClassifNatureza((byte) 12);
		objNatureza.setContaPatrimonial(null);

		// tenta inserir: RN01
		try {
			this.systemUnderTest.inserirNaturezaDespesa(objNatureza);

			Assert.fail("Deveria ter gerado uma exception");
		} catch (ApplicationBusinessException e) {

			Assert.assertEquals(FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M03, e.getCode());
		}
	}

	@Test
	public void testInclusaoCodigoNaturezaDuplicado() {

		// cria um grupo ativo
		final FsoGrupoNaturezaDespesa objGrupo = new FsoGrupoNaturezaDespesa(1, "Grupo Ativo", DominioSituacao.A);

		// cria uma natureza ativa
		final FsoNaturezaDespesa objNatureza = new FsoNaturezaDespesa();

		objNatureza.setId(new FsoNaturezaDespesaId((Integer) 1, (byte) 12));
		objNatureza.setDescricao("Natureza 1");
		objNatureza.setIndSituacao(DominioSituacao.A);
		objNatureza.setGrupoNaturezaDespesa(objGrupo);
		objNatureza.setCodClassifNatureza((byte) 12);
		objNatureza.setContaPatrimonial(null);

		Mockito.when(
				mockedFsoNaturezaDespesaDAO.verificarNaturezaDespesaEmGrupoNatureza(Mockito.any(FsoGrupoNaturezaDespesa.class),
						Mockito.any(FsoNaturezaDespesaId.class))).thenReturn(2l);

		// tenta inserir código duplicado: RN03
		try {
			systemUnderTest.inserirNaturezaDespesa(objNatureza);

			Assert.fail("Deveria ter gerado uma exception");
		} catch (ApplicationBusinessException e) {

			Assert.assertEquals(FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M05, e.getCode());
		}
	}
}

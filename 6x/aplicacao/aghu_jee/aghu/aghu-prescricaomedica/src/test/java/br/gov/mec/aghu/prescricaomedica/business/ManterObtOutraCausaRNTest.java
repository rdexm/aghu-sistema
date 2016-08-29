package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lalegre
 *
 */
public class ManterObtOutraCausaRNTest extends AGHUBaseUnitTest<ManterObtOutraCausaRN>{

	@Test
	public void verificarAlteracoesTest001() {

		final MpmObtOutraCausa novoObtOutraCausa = new MpmObtOutraCausa();
		final MpmObtOutraCausa antigoMpmObtOutraCausa = new MpmObtOutraCausa();

		novoObtOutraCausa.setIndCarga(DominioSimNao.S);
		novoObtOutraCausa.setDescCid("teste");
		antigoMpmObtOutraCausa.setDescCid("descricao diferente");

		try {

			systemUnderTest.verificarAlteracoes(novoObtOutraCausa, antigoMpmObtOutraCausa);

		} catch (ApplicationBusinessException e) {

			Assert.assertEquals("MPM_02726", e.getMessage());

		}

	}
	
	@Test
	public void verificarAlteracoesTest002() {

		final MpmObtOutraCausa novoObtOutraCausa = new MpmObtOutraCausa();
		final MpmObtOutraCausa antigoMpmObtOutraCausa = new MpmObtOutraCausa();

		novoObtOutraCausa.setIndCarga(DominioSimNao.N);
		
		novoObtOutraCausa.setDescCid("teste");
		novoObtOutraCausa.setComplCid("teste");
		novoObtOutraCausa.setCid(new AghCid());
		antigoMpmObtOutraCausa.setDescCid("descricao diferente");
		antigoMpmObtOutraCausa.setComplCid("teste");
		antigoMpmObtOutraCausa.setCid(new AghCid());

		try {

			systemUnderTest.verificarAlteracoes(novoObtOutraCausa, antigoMpmObtOutraCausa);

		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MPM_02727", e.getMessage());

		}

	}
	
	@Test
	public void verificarSituacaoTest() {
		
		final DominioSituacao novaSituacao = DominioSituacao.I;
		final DominioSituacao antigaSituacao = DominioSituacao.A;
		
		try {

			systemUnderTest.verificarSituacao(novaSituacao, antigaSituacao);

		} catch (ApplicationBusinessException e) {

			Assert.assertEquals("MPM_02729", e.getMessage());

		}
		
	}
	
	@Test
	public void verificarIndicadorDeCarga() {
		
		final DominioSimNao novoIndCarga = DominioSimNao.S;
		final DominioSimNao antigoIndCarga = DominioSimNao.N;
		
		try {

			systemUnderTest.verificarIndicadorDeCarga(novoIndCarga, antigoIndCarga);

		} catch (ApplicationBusinessException e) {

			Assert.assertEquals("MPM_02618", e.getMessage());

		}
		
	}


}

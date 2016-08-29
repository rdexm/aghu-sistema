package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterCuidadoUsualONTest extends AGHUBaseUnitTest<ManterCuidadoUsualON>{

	/**
	 * Classe responsável pelos testes unitários da classe ManterCuidadoUsualON.<br>
	 * 
	 * @author mgoulart
	 * 
	 */
	@Mock
	private MpmCuidadoUsualDAO mockedMpmCuidadoUsualDAO;
	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;

	@Test
	public void validaFrequenciaAprazamentoFrequenciaNaoCadastrada() {
		final MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento = null;

		MpmCuidadoUsual mpmCuidadoUsual = new MpmCuidadoUsual(); 
		mpmCuidadoUsual.setFrequencia(Short.valueOf("1"));
		mpmCuidadoUsual.setMpmTipoFreqAprazamentos(mpmTipoFrequenciaAprazamento);

		try {

			systemUnderTest.validaFrequenciaAprazamento(mpmCuidadoUsual);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException atual) {

			assertEquals("MENSAGEM_TIPO_FREQUENCIA_NAO_CADASTRADO", atual
					.getMessage());

		}
	}

	@Test
	public void validaFrequenciaAprazamentoFrequenciaInativa() {
		final MpmTipoFrequenciaAprazamento mpmTipoFreqAprazamentos = new MpmTipoFrequenciaAprazamento();
		mpmTipoFreqAprazamentos.setIndSituacao(DominioSituacao.I);
		
		MpmCuidadoUsual mpmCuidadoUsual = new MpmCuidadoUsual(); 
		mpmCuidadoUsual.setFrequencia(Short.valueOf("1"));
		mpmCuidadoUsual.setMpmTipoFreqAprazamentos(mpmTipoFreqAprazamentos);

		
		try {

			systemUnderTest.validaFrequenciaAprazamento(mpmCuidadoUsual);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException atual) {

			assertEquals("MENSAGEM_TIPO_FREQUENCIA_INATIVO", atual.getMessage());

		}
	}

	@Test
	public void validaFrequenciaAprazamentoNaoInformarFrequencia() {
		final MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		mpmTipoFrequenciaAprazamento.setIndSituacao(DominioSituacao.A);
		mpmTipoFrequenciaAprazamento.setIndDigitaFrequencia(false);

		MpmCuidadoUsual mpmCuidadoUsual = new MpmCuidadoUsual(); 
		mpmCuidadoUsual.setFrequencia(Short.valueOf("1"));
		mpmCuidadoUsual.setMpmTipoFreqAprazamentos(mpmTipoFrequenciaAprazamento);
		
		
		try {

			systemUnderTest.validaFrequenciaAprazamento(mpmCuidadoUsual);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException atual) {

			assertEquals("MENSAGEM_NAO_INFORMAR_FREQUENCIA", atual.getMessage());

		}
	}

	@Test
	public void validaFrequenciaAprazamentoFrequenciaObrigatoria() {
		final MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		mpmTipoFrequenciaAprazamento.setIndDigitaFrequencia(true);

		MpmCuidadoUsual mpmCuidadoUsual = new MpmCuidadoUsual();
		mpmCuidadoUsual.setMpmTipoFreqAprazamentos(new MpmTipoFrequenciaAprazamento());
		mpmCuidadoUsual.getMpmTipoFreqAprazamentos().setIndDigitaFrequencia(true);
		mpmCuidadoUsual.setFrequencia(null);
		mpmCuidadoUsual.setMpmTipoFreqAprazamentos(mpmTipoFrequenciaAprazamento);
		

		try {

			systemUnderTest.validaFrequenciaAprazamento(mpmCuidadoUsual);
			fail("Deveria ter ocorrido uma exceção!!!");

		} catch (ApplicationBusinessException atual) {

			assertEquals("MENSAGEM_FREQUENCIA_OBRIGATORIA", atual.getMessage());

		}
	}

}

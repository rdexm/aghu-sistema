package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterModoUsoPrescProcedRNTest extends AGHUBaseUnitTest<ManterModoUsoPrescProcedRN>{

	@Mock
	private MpmPrescricaoProcedimentoDAO mockedMpmPrescricaoProcedimentoDAO;
	@Mock
	private MpmTipoModoUsoProcedimentoDAO mockedMpmTipoModoUsoProcedimentoDAO;
	@Mock
	private MpmModoUsoPrescProcedDAO mockedMpmModoUsoProcedimentoDAO;

	/**
	 * Deve gerar exceção por não encontrar procedimento
	 * no banco de dados.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarAlteracao001() throws ApplicationBusinessException {

		final MpmPrescricaoProcedimento prescricaoProcedimento = null;
		//new MpmPrescricaoProcedimento();

		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.obterProcedimentoPeloId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(prescricaoProcedimento);

		try {

			systemUnderTest.verificarAlteracao(null, null);

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_00081");

		}	

	}

	/**
	 * Nesse caso deve gerar uma exceção, pois o procedimento não está
	 * pendente.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarAlteracao002() throws ApplicationBusinessException {

		final MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();
		prescricaoProcedimento.setIndPendente(DominioIndPendenteItemPrescricao.N);

		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.obterProcedimentoPeloId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(prescricaoProcedimento);

		try {

			systemUnderTest.verificarAlteracao(null, null);

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_01003");

		}	

	}

	/**
	 * Passa P como pendente, logo não deve gerar exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarAlteracao003() throws ApplicationBusinessException {

		final MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();
		prescricaoProcedimento.setIndPendente(DominioIndPendenteItemPrescricao.P);

		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.obterProcedimentoPeloId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(prescricaoProcedimento);

		systemUnderTest.verificarAlteracao(null, null);

	}

	/**
	 * Não seta nada como pendente (NULL), logo não deve gerar exceção.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarAlteracao004() throws ApplicationBusinessException {

		final MpmPrescricaoProcedimento prescricaoProcedimento = new MpmPrescricaoProcedimento();

		Mockito.when(mockedMpmPrescricaoProcedimentoDAO.obterProcedimentoPeloId(Mockito.anyInt(), Mockito.anyLong())).thenReturn(prescricaoProcedimento);

		systemUnderTest.verificarAlteracao(null, null);

	}

	/**
	 * Deve gerar exceção por não encontrar tipo modo de uso
	 * do procedimento no banco de dados.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento001() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = null; //new MpmTipoModoUsoProcedimento();

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		try {

			systemUnderTest.verificarTipoModoUsoProcedimento(null, null, null);

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_00277");

		}	

	}
	
	/**
	 * Deve gerar exceção por ter situação diferente de A.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento002() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		try {

			systemUnderTest.verificarTipoModoUsoProcedimento(null, null, null);

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_01004");

		}	

	}
	
	/**
	 * Deve gerar exceção por ter situação diferente de A.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento003() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();
		tipoModoUsoProcedimento.setIndSituacao(DominioSituacao.I);

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		try {

			systemUnderTest.verificarTipoModoUsoProcedimento(null, null, null);

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_01004");

		}	

	}
	
	/**
	 * Deve passar normalmente, pois não exige quantidade.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento004() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();
		tipoModoUsoProcedimento.setIndSituacao(DominioSituacao.A);

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		systemUnderTest.verificarTipoModoUsoProcedimento(null, null, null);

	}
	
	/**
	 * Deve gerar exceção por exigir quantidade e
	 * a mesma estar nula.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento005() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();
		tipoModoUsoProcedimento.setIndSituacao(DominioSituacao.A);
		tipoModoUsoProcedimento.setIndExigeQuantidade(Boolean.TRUE);

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		try {

			systemUnderTest.verificarTipoModoUsoProcedimento(null, null, null);

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_01005");

		}	

	}
	
	/**
	 * Deve gerar exceção por exigir quantidade e
	 * a mesma estar zerada.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento006() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();
		tipoModoUsoProcedimento.setIndSituacao(DominioSituacao.A);
		tipoModoUsoProcedimento.setIndExigeQuantidade(Boolean.TRUE);

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		try {

			systemUnderTest.verificarTipoModoUsoProcedimento(null, null, Short.valueOf("0"));

		} catch (ApplicationBusinessException expected) {

			assertEquals(expected.getMessage(), "MPM_01005");

		}	

	}
	
	/**
	 * Deve passar sem gerar erros, pois não exige quantidade,
	 * por estar nulo.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento007() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();
		tipoModoUsoProcedimento.setIndSituacao(DominioSituacao.A);

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		systemUnderTest.verificarTipoModoUsoProcedimento(null, null, Short.valueOf("0"));

	}
	
	/**
	 * Deve passar sem gerar erros, pois não exige quantidade.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarTipoModoUsoProcedimento008() throws ApplicationBusinessException {

		final MpmTipoModoUsoProcedimento tipoModoUsoProcedimento = new MpmTipoModoUsoProcedimento();
		tipoModoUsoProcedimento.setIndSituacao(DominioSituacao.A);
		tipoModoUsoProcedimento.setIndExigeQuantidade(Boolean.FALSE);

		Mockito.when(mockedMpmTipoModoUsoProcedimentoDAO.obterTipoModoUsoProcedimentoPeloId(Mockito.anyShort(), Mockito.anyShort())).thenReturn(tipoModoUsoProcedimento);

		systemUnderTest.verificarTipoModoUsoProcedimento(null, null, Short.valueOf("0"));

	}

}

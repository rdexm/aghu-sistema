package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvGrupoItemProcedId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.PrescricaoMedicaRN.PrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaIndicadoresConvenioInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * 26/10/2010
 * 
 * @author bsoliveira
 * 
 */
public class PrescricaoMedicaRNTest extends AGHUBaseUnitTest<PrescricaoMedicaRN>{

	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private MpmTipoFrequenciaAprazamentoDAO mockedMpmTipoFrequenciaAprazamentoDAO;
	@Mock
	private IAghuFacade aghuFacade;

	@Before
	public void doBeforeEachTestCase() {
	}

	/**
	 * Deve retornar objeto nulo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresConvenioInternacao001()
			throws ApplicationBusinessException {

		final FatConvGrupoItemProced convGrupoItensProced = null;

		// Implementação do Mock
		Mockito.when(mockedFaturamentoFacade.obterFatConvGrupoItensProcedPeloId(Mockito.anyShort(), Mockito.anyByte(), 
				Mockito.anyInt())).thenReturn(convGrupoItensProced);

		VerificaIndicadoresConvenioInternacaoVO atual = systemUnderTest
				.verificaIndicadoresConvenioInternacao(null, null, null);

		assertNull(atual);

	}

	/**
	 * Deve retornar objeto não nulo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresConvenioInternacao002()
			throws ApplicationBusinessException {

		final FatConvGrupoItemProced convGrupoItensProced = new FatConvGrupoItemProced();
		convGrupoItensProced.setId(new FatConvGrupoItemProcedId());

		// Implementação do Mock
		Mockito.when(mockedFaturamentoFacade.obterFatConvGrupoItensProcedPeloId(Mockito.anyShort(), Mockito.anyByte(), 
				Mockito.anyInt())).thenReturn(convGrupoItensProced);

		VerificaIndicadoresConvenioInternacaoVO atual = systemUnderTest
				.verificaIndicadoresConvenioInternacao(null, null, null);

		assertNotNull(atual);

	}

	/**
	 * Deve retornar dados conforme foram preenchidos.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaIndicadoresConvenioInternacao003()
			throws ApplicationBusinessException {

		final FatConvGrupoItemProced convGrupoItensProced = new FatConvGrupoItemProced();
		FatConvGrupoItemProcedId id = new FatConvGrupoItemProcedId();
		id.setPhiSeq(Integer.valueOf("1"));
		convGrupoItensProced.setId(id);
		convGrupoItensProced.setIndExigeJustificativa(true);
		convGrupoItensProced.setIndImprimeLaudo(false);
		convGrupoItensProced.setIndCobrancaFracionada(true);

		// Implementação do Mock
		Mockito.when(mockedFaturamentoFacade.obterFatConvGrupoItensProcedPeloId(Mockito.anyShort(), Mockito.anyByte(), 
				Mockito.anyInt())).thenReturn(convGrupoItensProced);

		VerificaIndicadoresConvenioInternacaoVO atual = systemUnderTest
				.verificaIndicadoresConvenioInternacao(null, null, null);

		assertEquals(Integer.parseInt("1"), atual.getPhiSeq()
				.intValue());
		assertTrue(atual.getIndExigeJustificativa());
		assertFalse(atual.getIndImprimeLaudo());
		assertTrue(atual.getIndCobrancaFracionada());

	}

	/**
	 * Tipo de freqüência aprazamento deve estar ativo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void validaAprazamento() throws ApplicationBusinessException {

		final MpmTipoFrequenciaAprazamento tipoFrequanciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequanciaAprazamento.setSeq((short) 1);

		// Implementação do Mock
		Mockito.when(mockedMpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(tipoFrequanciaAprazamento);

		try {
			systemUnderTest.validaAprazamento((short) 1, (short) 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(),
					PrescricaoMedicamentoExceptionCode.MPM_00750);
		}

	}

	/**
	 * Tipo de freqüência aprazamento não cadastrado.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void validaAprazamento001() throws ApplicationBusinessException {

		final MpmTipoFrequenciaAprazamento tipoFrequanciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequanciaAprazamento.setSeq((short) 1);

		// Implementação do Mock
		Mockito.when(mockedMpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(null);

		try {
			systemUnderTest.validaAprazamento((short) 1, (short) 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(),
					PrescricaoMedicamentoExceptionCode.MPM_00749);
		}

	}

	/**
	 * Tipo de freqüência exige a informação da freqüência.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void validaAprazamento002() throws ApplicationBusinessException {

		final MpmTipoFrequenciaAprazamento tipoFrequanciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequanciaAprazamento.setSeq((short) 1);
		tipoFrequanciaAprazamento.setIndDigitaFrequencia(true);
		tipoFrequanciaAprazamento.setIndSituacao(DominioSituacao.A);

		// Implementação do Mock
		Mockito.when(mockedMpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(tipoFrequanciaAprazamento);

		try {
			systemUnderTest.validaAprazamento((short) 1, null);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(),
					PrescricaoMedicamentoExceptionCode.MPM_00751);
		}

	}

	/**
	 * Tipo de freqüência não permite a informação de freqüência.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void validaAprazamento003() throws ApplicationBusinessException {

		final MpmTipoFrequenciaAprazamento tipoFrequanciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequanciaAprazamento.setSeq((short) 1);
		tipoFrequanciaAprazamento.setIndDigitaFrequencia(false);
		tipoFrequanciaAprazamento.setIndSituacao(DominioSituacao.A);

		// Implementação do Mock
		Mockito.when(mockedMpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(tipoFrequanciaAprazamento);

		try {
			systemUnderTest.validaAprazamento((short) 1, (short) 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(),
					PrescricaoMedicamentoExceptionCode.MPM_00752);
		}

	}

	/**
	 * A frequencia e o tipo devem ser informados ou somente o tipo
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void validaAprazamento004() throws ApplicationBusinessException {

		final MpmTipoFrequenciaAprazamento tipoFrequanciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequanciaAprazamento.setSeq((short) 1);
		tipoFrequanciaAprazamento.setIndDigitaFrequencia(false);
		tipoFrequanciaAprazamento.setIndSituacao(DominioSituacao.A);

		// Implementação do Mock
		Mockito.when(mockedMpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(tipoFrequanciaAprazamento);

		try {
			systemUnderTest.validaAprazamento((Short) null, (short) 1);
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(e.getCode(),
					PrescricaoMedicamentoExceptionCode.MPM_01376);
		}

	}

	/**
	 * tipo de frequancia de aprazamento informado corretamente
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void validaAprazamento005() throws ApplicationBusinessException {

		final MpmTipoFrequenciaAprazamento tipoFrequanciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequanciaAprazamento.setSeq((short) 1);
		tipoFrequanciaAprazamento.setIndDigitaFrequencia(true);
		tipoFrequanciaAprazamento.setIndSituacao(DominioSituacao.A);

		Mockito.when(mockedMpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(Mockito.anyShort())).thenReturn(tipoFrequanciaAprazamento);

		try {
			systemUnderTest.validaAprazamento((short) 1, (short) 1);
		} catch (ApplicationBusinessException e) {
			Assert.fail();
		}

	}
	
	@Test
	public void testGetEspecialidades() {
		Mockito.when(aghuFacade.pesquisarPorIdSigla(Mockito.any(Object.class))).thenReturn(null);

		AghEspecialidades aghEspecialidades = new AghEspecialidades();
		final List <AghEspecialidades> especialidades = new ArrayList<AghEspecialidades>();
		especialidades.add(aghEspecialidades);

		Mockito.when(aghuFacade.pesquisarPorNomeIdSiglaIndices(Mockito.any(Object.class))).thenReturn(especialidades);

		assertEquals(1, systemUnderTest.getEspecialidades(null).size());
	}
	
	@Test
	public void testGetEspecialidades02() {
		AghEspecialidades aghEspecialidades = new AghEspecialidades();
		final List <AghEspecialidades> especialidades = new ArrayList<AghEspecialidades>();
		especialidades.add(aghEspecialidades);
		especialidades.add(aghEspecialidades);

		Mockito.when(aghuFacade.pesquisarPorIdSigla(Mockito.any(Object.class))).thenReturn(especialidades);
		
		Mockito.when(aghuFacade.pesquisarPorNomeIdSiglaIndices(Mockito.any(Object.class))).thenReturn(null);
		
		assertEquals(2, systemUnderTest.getEspecialidades(null).size());
	}
	
	@Test
	public void testGetEspecialidades03() {

		Mockito.when(aghuFacade.pesquisarPorIdSigla(Mockito.any(Object.class))).thenReturn(new ArrayList<AghEspecialidades>());
		
		AghEspecialidades aghEspecialidades = new AghEspecialidades();
		final List <AghEspecialidades> especialidades = new ArrayList<AghEspecialidades>();
		especialidades.add(aghEspecialidades);
		especialidades.add(aghEspecialidades);
		
		Mockito.when(aghuFacade.pesquisarPorNomeIdSiglaIndices(Mockito.any(Object.class))).thenReturn(especialidades);
		
		assertEquals(2, systemUnderTest.getEspecialidades(null).size());
	}

}

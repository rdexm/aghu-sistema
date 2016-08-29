package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterialRetornos;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterItemRmpsRNTest extends AGHUBaseUnitTest<ManterItemRmpsRN> {

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IEstoqueFacade mockedEstoqueFacade;

	/**
	 * Testa Procedure RN_IPSP_VER_FORN_IRR
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void rnIpspVerFornIrr1() throws Exception {

		List<SceEstoqueAlmoxarifado> listaEstoqueAlmox = new ArrayList<SceEstoqueAlmoxarifado>();
		Mockito.when(mockedEstoqueFacade.listaEstoqueAlmoxarifado(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(listaEstoqueAlmox);

		SceRmrPaciente paciente = new SceRmrPaciente();
		paciente.setScoFornecedor(new ScoFornecedor());
		Mockito.when(mockedEstoqueFacade.obterSceRmrPacientePorChavePrimaria(Mockito.anyInt())).thenReturn(paciente);

		systemUnderTest.rnIpspVerFornIrr(1, 1);
	}

	/**
	 * Testa Procedure RN_IPSP_VER_FORN_IRR
	 * 
	 * @throws ApplicationBusinessException
	 */
	// @Test
	public void rnIpspVerFornIrr2() throws Exception {

		List<SceEstoqueAlmoxarifado> listaEstoqueAlmox = new ArrayList<SceEstoqueAlmoxarifado>();
		Mockito.when(mockedEstoqueFacade.listaEstoqueAlmoxarifado(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(listaEstoqueAlmox);

		SceRmrPaciente paciente = new SceRmrPaciente();
		paciente.setScoFornecedor(new ScoFornecedor());
		Mockito.when(mockedEstoqueFacade.obterSceRmrPacientePorChavePrimaria(Mockito.anyInt())).thenReturn(paciente);

		systemUnderTest.rnIpspVerFornIrr(1, 1);
	}

	/**
	 * Testa Procedure RN_IPSP_VER_RMR_EFET
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void rnIpspVerRmrEfet1() throws Exception {

		SceReqMaterialRetornos reqMaterialRetorno = new SceReqMaterialRetornos();
		reqMaterialRetorno.setIndSituacao("G"); // TODO refatorar quando criar o
												// domínio no POJO
		Mockito.when(mockedEstoqueFacade.obterSceReqMaterialRetornosPorChavePrimaria(Mockito.anyInt()))
				.thenReturn(reqMaterialRetorno);

		systemUnderTest.rnIpspVerRmrEfet(1);
	}

	/**
	 * Testa Procedure RN_IPSP_VER_RMR_EFET
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void rnIpspVerRmrEfet2() throws Exception {

		SceReqMaterialRetornos reqMaterialRetorno = new SceReqMaterialRetornos();
		reqMaterialRetorno.setIndSituacao("E"); // TODO refatorar quando criar o
												// domínio no POJO
		Mockito.when(mockedEstoqueFacade.obterSceReqMaterialRetornosPorChavePrimaria(Mockito.anyInt()))
				.thenReturn(reqMaterialRetorno);

		systemUnderTest.rnIpspVerRmrEfet(1);
	}

	/**
	 * Testa Procedure RN_IPSP_VER_RMR_EFET
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void rnIpspVerRmrEfet3() throws Exception {

		Mockito.when(mockedEstoqueFacade.obterSceReqMaterialRetornosPorChavePrimaria(Mockito.anyInt()))
				.thenReturn(null);

		systemUnderTest.rnIpspVerRmrEfet(1);
	}
}

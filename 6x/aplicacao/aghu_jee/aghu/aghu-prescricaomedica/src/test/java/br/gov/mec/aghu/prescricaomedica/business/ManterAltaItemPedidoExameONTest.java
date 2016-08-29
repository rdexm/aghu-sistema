package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaItemPedidoExameDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaItemPedidoExameONTest extends AGHUBaseUnitTest<ManterAltaItemPedidoExameON>{

	private static final Log log = LogFactory.getLog(ManterAltaItemPedidoExameONTest.class);
	
	@Mock
	private MpmAltaItemPedidoExameDAO mockedMpmAltaItemPedidoExameDAO;
	@Mock
	private ManterAltaItemPedidoExameRN mockedManterAltaItemPedidoExameRN;

	private void considerarObterMpmAltaItemPedidoExames(
			final List<MpmAltaItemPedidoExame> exames) {

		try {
			Mockito.when(mockedMpmAltaItemPedidoExameDAO.obterMpmAltaItemPedidoExame(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(exames);
		} catch (ApplicationBusinessException e) {
			log.error(e.getMessage());
		}
	}


	private void considerarBuscarAelUnfExecutaExamesPorID(
			final AelUnfExecutaExames aelUnfExecutaExames) {
		Mockito.when(mockedMpmAltaItemPedidoExameDAO.buscarAelUnfExecutaExamesPorID(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt())).thenReturn(aelUnfExecutaExames);
	}

	private void considerarObterNomeExames(
			final List<VAelExamesSolicitacao> exames) {
		Mockito.when(mockedMpmAltaItemPedidoExameDAO.obterNomeExames(Mockito.anyObject())).thenReturn(exames);
	}

	private void considerarBuscarAelMateriaisAnalisesPorAelUnfExecutaExames(
			final AelMateriaisAnalises material) {
		Mockito.when(mockedMpmAltaItemPedidoExameDAO.buscarAelMateriaisAnalisesPorAelUnfExecutaExames(Mockito.any(AelUnfExecutaExames.class))).thenReturn(material);
	}

	private void considerarBuscarAghUnidadesFuncionaisPorAelUnfExecutaExames(
			final AghUnidadesFuncionais aghUnidadesFuncionais) {
		Mockito.when(mockedMpmAltaItemPedidoExameDAO.buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(Mockito.any(AelUnfExecutaExames.class))).thenReturn(aghUnidadesFuncionais);
	}

	@Test
	public void atualizarAltaItemPedidoExameTest() {
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		final Short antigoAsuSeqp = 1;

		altaSumario.setId(new MpmAltaSumarioId());

		MpmAltaItemPedidoExame mpmAltaItemPedidoExame1 = new MpmAltaItemPedidoExame();
		MpmAltaItemPedidoExame mpmAltaItemPedidoExame2 = new MpmAltaItemPedidoExame();

		final List<MpmAltaItemPedidoExame> list = new ArrayList<MpmAltaItemPedidoExame>();
		list.add(mpmAltaItemPedidoExame1);
		list.add(mpmAltaItemPedidoExame2);

		considerarObterMpmAltaItemPedidoExames(list);

		try {
			systemUnderTest.versionarAltaItemPedidoExame(altaSumario,
					antigoAsuSeqp);
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void obterMpmAltaItemPedidoExameTest() {
		Integer altanAtdSeq = 0;
		Integer altanApaSeq = 0;
		Short altanAsuSeqp = 0;

		MpmAltaItemPedidoExame mpmAltaItemPedidoExame1 = new MpmAltaItemPedidoExame();
		MpmAltaItemPedidoExame mpmAltaItemPedidoExame2 = new MpmAltaItemPedidoExame();

		final List<MpmAltaItemPedidoExame> list = new ArrayList<MpmAltaItemPedidoExame>();
		list.add(mpmAltaItemPedidoExame1);
		list.add(mpmAltaItemPedidoExame2);

		considerarObterMpmAltaItemPedidoExames(list);

		try {
			systemUnderTest.obterMpmAltaItemPedidoExame(altanAtdSeq,
					altanApaSeq, altanAsuSeqp);
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void buscarAelUnfExecutaExamesPorIDTest() {
		Integer manSeq = 0;
		String sigla = "0";
		Integer unfSeq = 0;

		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		considerarBuscarAelUnfExecutaExamesPorID(aelUnfExecutaExames);

		systemUnderTest.buscarAelUnfExecutaExamesPorID(manSeq, sigla, unfSeq);

	}

	@Test
	public void inserirAltaItemPedidoExameTest() {
		MpmAltaItemPedidoExame item = new MpmAltaItemPedidoExame();

		try {
			systemUnderTest.inserirAltaItemPedidoExame(item);
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void obterNomeExamesTest() {
		Object object = new Object();

		List<VAelExamesSolicitacao> exames = new ArrayList<VAelExamesSolicitacao>();
		VAelExamesSolicitacao exame1 = new VAelExamesSolicitacao();
		VAelExamesSolicitacao exame2 = new VAelExamesSolicitacao();

		exames.add(exame1);
		exames.add(exame2);

		considerarObterNomeExames(exames);

		systemUnderTest.obterNomeExames(object);

	}

	@Test
	public void buscarAelMateriaisAnalisesPorAelUnfExecutaExamesTest() {
		AelUnfExecutaExames exame = new AelUnfExecutaExames();

		AelMateriaisAnalises material = new AelMateriaisAnalises();

		considerarBuscarAelMateriaisAnalisesPorAelUnfExecutaExames(material);

		systemUnderTest.buscarAelMateriaisAnalisesPorAelUnfExecutaExames(exame);

	}

	@Test
	public void buscarAghUnidadesFuncionaisPorAelUnfExecutaExamesTest() {
		AelUnfExecutaExames aelUnfExecutaExames = new AelUnfExecutaExames();
		AghUnidadesFuncionais aghUnidadesFuncionais = new AghUnidadesFuncionais();

		considerarBuscarAghUnidadesFuncionaisPorAelUnfExecutaExames(aghUnidadesFuncionais);

		systemUnderTest
				.buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(aelUnfExecutaExames);

	}

	@Test
	public void excluirMpmAltaItemPedidoExameTest() {
		MpmAltaItemPedidoExame altaItemPedidoExame = new MpmAltaItemPedidoExame();

		try {
			systemUnderTest.excluirMpmAltaItemPedidoExame(altaItemPedidoExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

}

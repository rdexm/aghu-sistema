package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptDataItemSumarioId;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoMedicamentoId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GeraDadosSumPrescQuimioSolRNTest extends AGHUBaseUnitTest<GeraDadosSumPrescQuimioSolRN> {

	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IProcedimentoTerapeuticoFacade mockedProcedimentoTerapeuticoFacade;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private ICadastrosBasicosPrescricaoMedicaFacade mockedCadastrosBasicosPrescricaoMedicaFacade;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private IFarmaciaApoioFacade mockedFarmaciaApoioFacade;
	@Mock
	private GeraDadosSumPrescQuimioSolRN mockedGeraDadosSumPrescQuimioSolRN;

	@Test
	public void processarListaPrescricaoCuidadoTest() throws ApplicationBusinessException {

		esperarPesquisarDataItemSumario();

		//Mockito.when(
			//	systemUnderTest.mptSintSumSolQ(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
				//.thenReturn(2);

		Mockito.when(mockedProcedimentoTerapeuticoFacade.obterMptPrescricaoMedicamentoComJoin(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
		.thenReturn(new MptPrescricaoMedicamento());

		systemUnderTest.processarListaPrescricaoMedicamento(123, 321, 456, 654, new Date(), getListaMockMptPrescricaoMedicamento(),
				Boolean.TRUE);

	}

	private void esperarPesquisarDataItemSumario() {
		Integer qtd = 2;
		Mockito.when(mockedProcedimentoTerapeuticoFacade.pesquisarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(getListaMockMptDataItemSumario(qtd));

		Mockito.when(
				mockedProcedimentoTerapeuticoFacade.listarPrescricaoMedicamentoHierarquia(Mockito.anyInt(), Mockito.anyInt(),
						Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyBoolean())).thenReturn(null);
	}

	protected List<MptDataItemSumario> getListaMockMptDataItemSumario(Integer qtd) {
		List<MptDataItemSumario> listaMptDataItemSumario = new ArrayList<MptDataItemSumario>();
		for (int i = 0; i < qtd; i++) {
			MptDataItemSumario mptDataIs = new MptDataItemSumario();
			MptDataItemSumarioId id = new MptDataItemSumarioId();
			id.setSeqp(i);
			mptDataIs.setId(id);
			mptDataIs.setValor("S");
			mptDataIs.setData(new Date());

			listaMptDataItemSumario.add(mptDataIs);
		}
		return listaMptDataItemSumario;
	}

	private List<MptPrescricaoMedicamento> getListaMockMptPrescricaoMedicamento() {
		MptPrescricaoMedicamento pm = new MptPrescricaoMedicamento();
		MptPrescricaoMedicamentoId id = new MptPrescricaoMedicamentoId();
		id.setPteAtdSeq(1);
		id.setPteSeq(1);
		id.setSeq((int) 1);
		pm.setId(id);
		pm.setAlteradoEm(new Date());
		pm.setSituacaoItem(DominioSituacaoItemPrescricaoMedicamento.V);

		MptPrescricaoMedicamento pm2 = new MptPrescricaoMedicamento();
		MptPrescricaoMedicamentoId id2 = new MptPrescricaoMedicamentoId();
		id2.setPteAtdSeq(1);
		id2.setPteSeq(1);
		id2.setSeq((int) 1);
		pm2.setId(id);
		pm2.setAlteradoEm(new Date());
		pm2.setSituacaoItem(DominioSituacaoItemPrescricaoMedicamento.V);

		return Arrays.asList(pm, pm2);
	}

}

package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamentoId;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoMedicamentoId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GeraDadosSumPrescQuimioMdtoRNTest extends AGHUBaseUnitTest<GeraDadosSumPrescQuimioMdtoRN> {
//	private static final Log log = LogFactory.getLog(GeraDadosSumPrescQuimioMdtoRNTest.class);	

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
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private IFarmaciaApoioFacade mockedFarmaciaApoioFacade;
	
	@Test
	public void processarListaPrescricaoMedicamentoTestNunca() throws ApplicationBusinessException {
		
		esperarPesquisarDataItemSumarioNunca();
		
		systemUnderTest.processarListaPrescricaoMedicamento(123, 321, 654, 456,
				new Date(), new ArrayList<MptPrescricaoMedicamento>(),
				Boolean.TRUE);
		
	}
	
	private void esperarPesquisarDataItemSumarioNunca() {
	}

	private void esperarPesquisarDataItemSumarioNull() {
		Mockito.when(mockedProcedimentoTerapeuticoFacade.pesquisarDataItemSumario(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(new ArrayList<MptDataItemSumario>());

		Mockito.when(mockedProcedimentoTerapeuticoFacade.listarPrescricaoMedicamentoHierarquia(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyBoolean()))
				.thenReturn(null);

	}
	
	@Test
	public void mptSintSumMdtoQTest() throws ApplicationBusinessException {
		
		esperarObterMptPrescricaoMedicamentoComJoinNull();
		Integer retorno = systemUnderTest.mptSintSumMdtoQ(123, 456, 789, 321, 654);
		Assert.assertNull(retorno);
	}

	private void esperarObterMptPrescricaoMedicamentoComJoinNull() {
		Mockito.when(mockedProcedimentoTerapeuticoFacade.obterMptPrescricaoMedicamentoComJoin(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
				.thenReturn(null);
	}

	private List<MptPrescricaoMedicamento> getListaMockMptPrescricaoMedicamento() {
		List<MptPrescricaoMedicamento> listaPrescricaoMedicamento = new ArrayList<MptPrescricaoMedicamento>();
		
		MptPrescricaoMedicamento item1 = new MptPrescricaoMedicamento();
		MptPrescricaoMedicamentoId id1 = new MptPrescricaoMedicamentoId();
		id1.setSeq(123);
		item1.setId(id1);
		item1.setDthrAltValida(new Date());
		
	
		
		/*MptPrescricaoMedicamento item2 = new MptPrescricaoMedicamento();
		MptPrescricaoMedicamentoId id2 = new MptPrescricaoMedicamentoId();
		id2.setSeq(123);
		item2.setId(id2);
		item2.setDthrAltValida(new Date());*/
		
		listaPrescricaoMedicamento.add(item1);
		//listaPrescricaoMedicamento.add(item2);
		return listaPrescricaoMedicamento;
	}

	@Test
	public void mptSintSumMdtoQTestNotNull() throws ApplicationBusinessException {
		
		esperarObterMptPrescricaoMedicamentoComJoin();
		
		esperarListarItensPrescricaoMedicamento();
		
		esperarMpmcMinusculo();
		
		esperarPesquisarItemPrescricaoSumario();
		
		esperarMpmcMinusculo();
		
		Integer retorno = systemUnderTest.mptSintSumMdtoQ(123, 456, 789, 321, 654);
		
		Assert.assertEquals(new Integer(0), retorno);
	}

	private void esperarPesquisarItemPrescricaoSumario() {
		Mockito.when(mockedProcedimentoTerapeuticoFacade.pesquisarItemPrescricaoSumario(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any(DominioTipoItemPrescricaoSumario.class)))
				.thenReturn(Arrays.asList(new MptItemPrescricaoSumario()));

	}

	private void esperarMpmcMinusculo() {
		Mockito.when(mockedAmbulatorioFacade.mpmcMinusculo(
				Mockito.anyString(), Mockito.anyInt()))
				.thenReturn("");
	}

	private void esperarListarItensPrescricaoMedicamento() {
		//retorna uma lista com um item do tipo MptItemPrescricaoMedicamento porém com todos seus atributos nulos
		List<MptItemPrescricaoMedicamento> listaItemPrescricaoMedicamento = new ArrayList<MptItemPrescricaoMedicamento>();
		MptItemPrescricaoMedicamento item1 = new MptItemPrescricaoMedicamento();
		MptItemPrescricaoMedicamentoId id1 = new MptItemPrescricaoMedicamentoId();
		id1.setPmoPteAtdSeq(1234);
		id1.setPmoPteSeq(123);
		id1.setPmoSeq(12);
		id1.setSeqp((short) 1);
		item1.setId(id1);
		AfaMedicamento medicamento = new AfaMedicamento();
		medicamento.setMatCodigo(1);
		item1.setMedicamento(medicamento);
		listaItemPrescricaoMedicamento.add(item1);

		Mockito.when(mockedProcedimentoTerapeuticoFacade.listarItensPrescricaoMedicamento(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(listaItemPrescricaoMedicamento);
	}

	private void esperarObterMptPrescricaoMedicamentoComJoin() {
		MptPrescricaoMedicamento item = new MptPrescricaoMedicamento();
		AfaViaAdministracao viaAdm = new AfaViaAdministracao();
		viaAdm.setSigla("VO");
		item.setViaAdministracao(viaAdm);
		item.setSeNecessario(Boolean.FALSE);

		Mockito.when(mockedProcedimentoTerapeuticoFacade.obterMptPrescricaoMedicamentoComJoin(
				Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
				.thenReturn(item);
	}
	
}

	

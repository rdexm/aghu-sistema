package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptDataItemSumarioId;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoCuidadoId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class GeraDadosSumarioPrescricaoQuimioRNTest extends AGHUBaseUnitTest<GeraDadosSumarioPrescricaoQuimioRN>{

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
	
	/**
	 * Método executado antes da execução dos testes. Criar os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */  	
	@Before
	public void doBeforeEachTestCase() {
	}	
	
	
	@Test
	public void gerarDadosSumarioPrescricaoQuimioPOLTest() throws ApplicationBusinessException {
		esperarNaoChamarGerarAtendimentoPaciente();
		systemUnderTest.gerarDadosSumarioPrescricaoQuimioPOL(123, 321, null);
	}
	
	private void esperarNaoChamarGerarAtendimentoPaciente() throws ApplicationBusinessException {
	}
		
	
	private void esperarPesquisarDataItemSumario() {
		MpmCuidadoUsual cuidado = new MpmCuidadoUsual();
		cuidado.setIndOutros(true);
		MpmTipoFrequenciaAprazamento aprazamento = new MpmTipoFrequenciaAprazamento();
		aprazamento.setDescricao("");
		MptPrescricaoCuidado prescricao = new MptPrescricaoCuidado();
		prescricao.setCuidadoUsual(cuidado);
		prescricao.setTipoFreqAprazamento(aprazamento);
		
		Mockito.when(mockedProcedimentoTerapeuticoFacade.obterMptPrescricaoCuidadoComJoin(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), 
				Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(prescricao);
		
		Mockito.when(mockedProcedimentoTerapeuticoFacade.pesquisarDataItemSumario(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(getListaMockMptDataItemSumario(2));

		Mockito.when(mockedProcedimentoTerapeuticoFacade.listarPrescricaoCuidadoHierarquia(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Date.class)))
		.thenReturn(null);
	}


	protected List<MptDataItemSumario> getListaMockMptDataItemSumario(Integer qtd) {
		List<MptDataItemSumario> listaMptDataItemSumario = new ArrayList<MptDataItemSumario>();
		for(int i =0; i< qtd; i++){
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


	private List<MptPrescricaoCuidado> getListaMockMptPrescricaoCuidado() {
		MptPrescricaoCuidado pc = new MptPrescricaoCuidado();
		MptPrescricaoCuidadoId id = new MptPrescricaoCuidadoId();
		id.setPteAtdSeq(1);
		id.setPteSeq(1);
		id.setSeq((int)1);
		pc.setId(id);
		pc.setAlteradoEm(new Date());		
		pc.setIndSituacaoItem(DominioSituacaoItemPrescricaoMedicamento.V);		
		
		MptPrescricaoCuidado pc2 = new MptPrescricaoCuidado();
		MptPrescricaoCuidadoId id2 = new MptPrescricaoCuidadoId();
		id2.setPteAtdSeq(1);
		id2.setPteSeq(1);
		id2.setSeq((int)1);
		pc2.setId(id);
		pc2.setAlteradoEm(new Date());		
		pc2.setIndSituacaoItem(DominioSituacaoItemPrescricaoMedicamento.V);		

		return Arrays.asList(pc, pc2);
	}

}

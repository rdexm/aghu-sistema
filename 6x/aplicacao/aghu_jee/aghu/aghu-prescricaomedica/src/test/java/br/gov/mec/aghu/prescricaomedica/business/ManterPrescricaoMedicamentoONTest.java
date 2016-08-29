package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterPrescricaoMedicamentoONTest extends AGHUBaseUnitTest<ManterPrescricaoMedicamentoON>{

	@Mock
	private ItemPrescricaoMedicamentoRN mockedItemPrescricaoMedicamentoRN;
	@Mock
	private PrescricaoMedicamentoRN mockedPrescricaoMedicamentoRN;
	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtoDAO;

	
	@Test
	public void validarExcluirPrescricaoMedicamento() throws ApplicationBusinessException{
		
		MpmPrescricaoMdto prescricaoMedicamento = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(1);
		id.setSeq((long)2);
		prescricaoMedicamento.setId(id);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.merge(Mockito.any(MpmPrescricaoMdto.class))).thenReturn(prescricaoMedicamento);

		systemUnderTest.excluirPrescricaoMedicamento(prescricaoMedicamento);
	} 

	@Test
	public void validarExcluirPrescricaoMedicamentoComListaDeItens() throws ApplicationBusinessException{
		
		MpmPrescricaoMdto prescricaoMedicamento = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(1);
		id.setSeq((long)2);
		prescricaoMedicamento.setId(id);

		List<MpmItemPrescricaoMdto> listaItemPrescricaoMdtos = new ArrayList<MpmItemPrescricaoMdto>();
		MpmItemPrescricaoMdto mpmItemPrescricaoMdto = new MpmItemPrescricaoMdto();
		MpmItemPrescricaoMdtoId itemPrescricaoMdtoId = new MpmItemPrescricaoMdtoId(); 
		mpmItemPrescricaoMdto.setId(itemPrescricaoMdtoId);
		listaItemPrescricaoMdtos.add(mpmItemPrescricaoMdto);
		
		prescricaoMedicamento.setItensPrescricaoMdtos(listaItemPrescricaoMdtos);

		Mockito.when(mockedMpmPrescricaoMdtoDAO.merge(Mockito.any(MpmPrescricaoMdto.class))).thenReturn(prescricaoMedicamento);

		systemUnderTest.excluirPrescricaoMedicamento(prescricaoMedicamento);
	} 


}

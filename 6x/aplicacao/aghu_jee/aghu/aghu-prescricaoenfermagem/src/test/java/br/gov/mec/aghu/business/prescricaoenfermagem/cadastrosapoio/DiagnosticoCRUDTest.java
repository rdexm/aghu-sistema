package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;


public class DiagnosticoCRUDTest extends AGHUBaseUnitTest<DiagnosticoCRUD>{

	@Mock
	private EpeDiagnosticoDAO mockedEpeDiagnosticoDAO;
	@Mock
	private DiagnosticoRN mockedDiagnosticoRN;

	@Test
	public void testPersistirDiagnostico() throws BaseException {
		EpeDiagnostico diagnostico = new EpeDiagnostico();
		EpeDiagnosticoId id = new EpeDiagnosticoId();
		diagnostico.setId(id);
		id.setSnbGnbSeq(Short.valueOf("0"));
		id.setSnbSequencia(Short.valueOf("0"));
		id.setSequencia(null);
		
		diagnostico.setSubgrupoNecesBasica(new EpeSubgrupoNecesBasica());
		
		esperarEpeDiagnosticoDAOPesquisarMaxSequenciaDiagnosticos();
		
		final List<AelItemSolicitacaoExames> iseList = new LinkedList<AelItemSolicitacaoExames>();
		iseList.add(new AelItemSolicitacaoExames());

		
		systemUnderTest.persistirDiagnostico(diagnostico);
		Assert.assertNotNull(diagnostico.getId().getSequencia());
	}
	
	public void esperarEpeDiagnosticoDAOPesquisarMaxSequenciaDiagnosticos() throws ApplicationBusinessException{
		Mockito.when(mockedEpeDiagnosticoDAO.pesquisarMaxSequenciaDiagnosticos( Mockito.anyShort(), Mockito.anyShort()))
		.thenReturn(Short.valueOf("0"));
	}

}

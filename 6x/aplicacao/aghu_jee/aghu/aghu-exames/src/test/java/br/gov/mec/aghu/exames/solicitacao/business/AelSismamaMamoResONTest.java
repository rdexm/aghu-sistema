package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoCadDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaMamoCad;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelSismamaMamoResONTest extends AGHUBaseUnitTest<AelSismamaMamoResON>{

	@Mock
	private AelSismamaMamoCadDAO	mockedAelSismamaMamoCadDAO;
	@Mock
	private AelSismamaMamoResDAO	mockedAelSismamaMamoResDAO;
	@Mock
	private AelItemSolicitacaoExameDAO	mockedAelItemSolicitacaoExameDAO;


	private void when() {
		Mockito.when(mockedAelSismamaMamoCadDAO.obterPorChavePrimaria("1")).thenReturn(new AelSismamaMamoCad());
		Mockito.when(mockedAelItemSolicitacaoExameDAO.obterPorId(Mockito.anyInt(), Mockito.anyShort())).thenReturn(new AelItemSolicitacaoExames());
	}

	@Test
	public void testGravarAelSismamaMamoResVazio() {

		final Short iseSeqp = Short.valueOf("1");
		final Integer iseSoeSeq = 1;
		final Map<DominioSismamaMamoCadCodigo, Object> values = new HashMap<DominioSismamaMamoCadCodigo, Object>();
		
		try {

			when();
			
			systemUnderTest.gravarAelSismamaMamoRes(values, iseSoeSeq, iseSeqp);
		} catch (final Exception e) {
			Assert.fail("Gerada excecao " + e.getMessage());
		}
	}


}
package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FinalizaAtendimentoRNTest extends AGHUBaseUnitTest<FinalizaAtendimentoRN> {

	@Mock
	private MamControlesDAO mockedMamControlesDAO;

	@Mock
	private MarcacaoConsultaRN mockedMarcacaoConsultaRN;

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	@Test
	public void atualizarSituacaoControleTest() throws ApplicationBusinessException {
			MamControles controle = new MamControles();
			controle.setSituacao(DominioSituacaoControle.U);
			List<MamControles> listaControle = new ArrayList<MamControles>();
			listaControle.add(controle);
			this.esperarListaControle(listaControle);

				systemUnderTest.atualizarSituacaoControle(Integer.valueOf(9999), NOME_MICROCOMPUTADOR);
				if(!controle.getSituacao().equals(DominioSituacaoControle.L)){
				Assert.fail("Controle deveria ter situacao L");		
		}
	}
	
	private void esperarListaControle(final List<MamControles> listaControle) {
		Mockito.when(mockedMamControlesDAO.pesquisarControlePorNumeroConsulta(Mockito.anyInt())).thenReturn(listaControle);
			}
	}

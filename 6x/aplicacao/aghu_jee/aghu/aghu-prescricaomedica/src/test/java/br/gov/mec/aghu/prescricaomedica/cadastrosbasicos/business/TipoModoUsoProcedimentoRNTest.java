package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class TipoModoUsoProcedimentoRNTest extends AGHUBaseUnitTest<TipoModoUsoProcedimentoRN>{

	@Mock
	private ProcedEspecialDiversoRN mockedProcedEspecialDiversoRN;
	@Mock
	private MpmTipoModoUsoProcedimentoDAO mockedMpmTipoModoUsoProcedimentoDAO;

	
	@Test
	public void inserir() {
		
		MpmProcedEspecialDiversos esp = new MpmProcedEspecialDiversos();
		MpmTipoModoUsoProcedimento modoUso = new MpmTipoModoUsoProcedimento();
		esp.setIndSituacao(DominioSituacao.A);
		modoUso.setProcedimentoEspecialDiverso(esp);
		
		try {
			systemUnderTest.inserir(modoUso, new RapServidores());
		} catch (BaseException e) {
			Assert.fail();
		}
	}

}

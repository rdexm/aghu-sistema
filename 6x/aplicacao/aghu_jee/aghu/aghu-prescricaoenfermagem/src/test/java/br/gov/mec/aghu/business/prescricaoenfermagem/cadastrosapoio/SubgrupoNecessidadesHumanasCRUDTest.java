package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSubgrupoNecesBasicaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SubgrupoNecessidadesHumanasCRUDTest extends AGHUBaseUnitTest<SubgrupoNecessidadesHumanasCRUD>{

	@Mock
	private EpeSubgrupoNecesBasicaDAO mockedEpeSubgrupoNecesBasicaDAO;
	@Mock
	private SubgrupoNecessidadesHumanasRN mockedSubgrupoNecessidadesHumanasRN;

	@Test
	public void testPersistirSubgrupoNecessidadesHumanas() throws BaseException {
		EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId();
		EpeSubgrupoNecesBasica epeSubgrupoNecesBasica = new EpeSubgrupoNecesBasica();
		Boolean checkboxSubGrupoAtivo = true;
		Short seqGrupoNecessidadesHumanas = 0;
		epeSubgrupoNecesBasica.setId(idSubgrupo);
		
		esperarEpeSubgrupoNecesBasicaDAOObterMaxSequenciaSubgrupoNecessBasica();
		
		systemUnderTest.persistirSubgrupoNecessidadesHumanas(epeSubgrupoNecesBasica, checkboxSubGrupoAtivo, seqGrupoNecessidadesHumanas);
		Assert.assertEquals(Short.valueOf("1"), epeSubgrupoNecesBasica.getId().getSequencia());
	}
	
	public void esperarEpeSubgrupoNecesBasicaDAOObterMaxSequenciaSubgrupoNecessBasica() throws ApplicationBusinessException{
		Mockito.when(mockedEpeSubgrupoNecesBasicaDAO.obterMaxSequenciaSubgrupoNecessBasica(Mockito.anyShort())).thenReturn(Short.valueOf("0"));
	}
	
}

package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MotivoAltaMedicaCRUDTest extends AGHUBaseUnitTest<MotivoAltaMedicaCRUD>{

	@Mock
	private MpmMotivoAltaMedicaDAO mockedMpmMotivoAltaMedicaDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void removerMotivoAltaMedica() throws ApplicationBusinessException {
		MpmMotivoAltaMedica motivo = new MpmMotivoAltaMedica();
		motivo.setCriadoEm(new Date());
		
		Mockito.when(mockedMpmMotivoAltaMedicaDAO.obterMotivoAltaMedicaPeloId(Mockito.anyShort())).thenReturn(motivo);
		
		try {
			systemUnderTest.removerMotivoAltaMedica(Short.valueOf("1"), 1);
		} catch (BaseException e) {
			fail();
		}
	}


	@Test
	public void persistPlanoPosAlta() {
		MpmMotivoAltaMedica motivo = new MpmMotivoAltaMedica();
		motivo.setIndOutros(false);
		motivo.setIndExigeComplemento(false);
		motivo.setDescricao("");
		motivo.setSigla("");
		
		try {
			whenObterServidorLogado();
			
			Mockito.when(mockedMpmMotivoAltaMedicaDAO.pesquisarMotivoAltaMedicaSiglaCount(Mockito.anyString())).thenReturn(null);
			
			systemUnderTest.persistMotivoAltaMedica(motivo);
		} catch (BaseException e) {
			fail();
		}
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }


}
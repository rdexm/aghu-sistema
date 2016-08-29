package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ProcedimentoRNTest extends AGHUBaseUnitTest<ProcedimentoRN>{

	@Mock
	private MamProcedimentoDAO mamProcedimentoDAO;
    @Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade; 
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	@Test
	public void testPreInsert() throws BaseException{		
		MamProcedimento procedimento = new MamProcedimento();
		
		whenObterServidorLogado();
		
		systemUnderTest.preInsert(procedimento);
		
		Assert.assertNotNull(procedimento.getCriadoEm());
		Assert.assertNotNull(procedimento.getServidor());
		Assert.assertNotNull(procedimento.getSituacao());
		Assert.assertNotNull(procedimento.getGenerico());
		
	}
	
	@Test(expected=BaseException.class)
	public void testVerificaProcedimento() throws BaseException {
		final List<MamProcedimento> listaProcedimentos= new ArrayList<MamProcedimento>();
		listaProcedimentos.add(new MamProcedimento(1, "teste1",DominioSituacao.A,new Date(),null, true));
		
		Mockito.when(mamProcedimentoDAO.obterProcedimentosPeloProcedimentoEspecialDiverso(Mockito.anyInt(), Mockito.anyShort())).thenReturn(listaProcedimentos);
		
		MamProcedimento procedimento = new MamProcedimento();
		procedimento.setProcedEspecialDiverso(new MpmProcedEspecialDiversos(Short.parseShort("1"),"teste",DominioSituacao.A));
		systemUnderTest.verificaProcedimento(procedimento);
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

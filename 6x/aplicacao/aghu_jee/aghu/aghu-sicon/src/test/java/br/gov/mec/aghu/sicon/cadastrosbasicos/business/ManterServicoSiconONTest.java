package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ManterServicoSiconON.ManterRelacionamentoServicoSiconONExceptionCode;
import br.gov.mec.aghu.sicon.dao.ScoServicoSiconDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterServicoSiconONTest extends AGHUBaseUnitTest<ManterServicoSiconON>{

	/** The dao. */
	@Mock
	private ScoServicoSiconDAO dao;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	
	@Test
	public void alterarComException() throws ApplicationBusinessException {

		try {
			systemUnderTest.alterar(null);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					ManterRelacionamentoServicoSiconONExceptionCode.MENSAGEM_SERVICO_SICON_OBRIGATORIO,
					e.getCode());
		}
	}

	@Test
	public void inserirComException() throws ApplicationBusinessException {

		try {
			whenObterServidorLogado();
			systemUnderTest.inserir(null);
			Assert.fail("Deveria ter gerado uma exception");
		} catch (BaseException e) {
			Assert.assertEquals(
					ManterRelacionamentoServicoSiconONExceptionCode.MENSAGEM_SERVICO_SICON_OBRIGATORIO,
					e.getCode());
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

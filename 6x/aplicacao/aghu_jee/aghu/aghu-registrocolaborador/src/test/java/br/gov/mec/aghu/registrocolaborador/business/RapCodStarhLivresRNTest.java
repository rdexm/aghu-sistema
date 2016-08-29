package br.gov.mec.aghu.registrocolaborador.business;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.model.RapCodStarhLivres;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapCodStarhLivresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class RapCodStarhLivresRNTest extends AGHUBaseUnitTest<RapCodStarhLivresRN> {

	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;
	@Mock
	private RapServidoresDAO mockedRapServidoresDAO;
	@Mock
	private RapCodStarhLivresDAO mcokedRapCodStarhLivresDAO;

	@Before
	public void inicar() throws BaseException {
		whenObterServidorLogado();

		Mockito.when(mockedRapServidoresDAO.obterServidor(Mockito.any(RapServidoresId.class))).thenReturn(new RapServidores());

	}

	@Test
	public void insert() throws ApplicationBusinessException {
		systemUnderTest.insert((Mockito.any(RapCodStarhLivres.class)));
	}

	@Test
	public void obterCodStarhLivre() throws ApplicationBusinessException {
		systemUnderTest.obterCodStarhLivre(Mockito.anyInt());
	}

	private void whenObterServidorLogado() throws BaseException {
		RapServidores rap = new RapServidores(new RapServidoresId(1, (short) 1));
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
	}

}

package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaSinonimoMedicamentoJnDAO;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoId;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * @author lcmoura
 * 
 */
public class SinonimoMedicamentoRNTest extends AGHUBaseUnitTest<SinonimoMedicamentoRN> {

	private static final String NOME_PESSOA_FISICA = "PESSOA FÍSICA";
	private static final String DESCRICAO_SINONIMO = "DESCRIÇÃO";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AfaSinonimoMedicamentoJnDAO mockedAfaSinonimoMedicamentoJnDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;

	@Test
	public void preInsertSinonimoMedicamentoSuccess()
			throws ApplicationBusinessException {

		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento();
		sinonimoMedicamento.setDescricao(DESCRICAO_SINONIMO);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
		
		systemUnderTest.preInsertSinonimoMedicamento(sinonimoMedicamento);

		//Assert.assertEquals(DESCRICAO_SINONIMO, sinonimoMedicamento.getDescricao());
		Assert.assertNotNull(sinonimoMedicamento.getCriadoEm());

	}
	
	@Test
	public void preInsertSinonimoMedicamentoFail() throws ApplicationBusinessException {

		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento();
		sinonimoMedicamento.setDescricao(DESCRICAO_SINONIMO);

		try {
			systemUnderTest.preInsertSinonimoMedicamento(sinonimoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00169");
		} catch (Exception e) {
			Assert.assertEquals("AFA_00169", e.getMessage());
		}
	}

	@Test
	public void preUpdateSinonimoMedicamentoSuccess()
			throws ApplicationBusinessException {

		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento();
		sinonimoMedicamento.setDescricao(DESCRICAO_SINONIMO);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
		
		systemUnderTest.preUpdateSinonimoMedicamento(sinonimoMedicamento);

		Assert.assertEquals(DESCRICAO_SINONIMO, sinonimoMedicamento
				.getDescricao());
		Assert.assertNull(sinonimoMedicamento.getCriadoEm());
	}

	@Test
	public void preUpdateSinonimoMedicamentoFail() throws ApplicationBusinessException {

		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento();
		sinonimoMedicamento.setDescricao(DESCRICAO_SINONIMO);

		try {
			systemUnderTest.preUpdateSinonimoMedicamento(sinonimoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00169");
		} catch (Exception e) {
			Assert.assertEquals("AFA_00169", e.getMessage());
		}
	}

	@Test
	public void posUpdateSinonimoMedicamentoSemJournal()
			throws ApplicationBusinessException {

		AfaSinonimoMedicamentoId id = new AfaSinonimoMedicamentoId(0, 0);
		Date agora = Calendar.getInstance().getTime();

		AfaSinonimoMedicamento sinonimoMedicamentoOld = new AfaSinonimoMedicamento(
				id, getServidor(), DESCRICAO_SINONIMO, agora, DominioSituacao.A);
		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento(
				id, getServidor(), DESCRICAO_SINONIMO, agora, DominioSituacao.A);

		final AfaSinonimoMedicamentoJn sinonimoMedicamentoJnExpected = new AfaSinonimoMedicamentoJn();
		sinonimoMedicamentoJnExpected.setOperacao(DominioOperacoesJournal.UPD);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
		
		AfaSinonimoMedicamentoJn sinonimoMedicamentoJn = systemUnderTest
				.posUpdateSinonimoMedicamento(sinonimoMedicamentoOld,
						sinonimoMedicamento);
		Assert.assertNull(sinonimoMedicamentoJn);
	}

	@Test
	public void posUpdateSinonimoMedicamentoComJournal()
			throws ApplicationBusinessException {

		AfaSinonimoMedicamentoId id = new AfaSinonimoMedicamentoId(0, 0);
		Date agora = Calendar.getInstance().getTime();

		AfaSinonimoMedicamento sinonimoMedicamentoOld = new AfaSinonimoMedicamento(
				id, getServidor(), DESCRICAO_SINONIMO, agora, DominioSituacao.A);
		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento(
				id, getServidor(), DESCRICAO_SINONIMO, agora, DominioSituacao.I);

		final AfaSinonimoMedicamentoJn sinonimoMedicamentoJnExpected = new AfaSinonimoMedicamentoJn();
		sinonimoMedicamentoJnExpected.setOperacao(DominioOperacoesJournal.UPD);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
		
		// Insere JN
		AfaSinonimoMedicamentoJn sinonimoMedicamentoJn = systemUnderTest
				.posUpdateSinonimoMedicamento(sinonimoMedicamentoOld,
						sinonimoMedicamento);
		Assert.assertNotNull(sinonimoMedicamentoJn);
		Assert.assertEquals(sinonimoMedicamentoJn,
				sinonimoMedicamentoJn);

	}

	@Test
	public void posDeleteSinonimoMedicamento() throws ApplicationBusinessException {

		AfaSinonimoMedicamentoId id = new AfaSinonimoMedicamentoId(0, 0);
		Date agora = Calendar.getInstance().getTime();

		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento(
				id, getServidor(), DESCRICAO_SINONIMO, agora, DominioSituacao.A);

		final AfaSinonimoMedicamentoJn sinonimoMedicamentoJnExpected = new AfaSinonimoMedicamentoJn();
		sinonimoMedicamentoJnExpected.setOperacao(DominioOperacoesJournal.DEL);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());

		// Insere JN
		AfaSinonimoMedicamentoJn sinonimoMedicamentoJn = systemUnderTest
				.posDeleteSinonimoMedicamento(sinonimoMedicamento);
		Assert.assertNotNull(sinonimoMedicamentoJn);
		Assert.assertEquals(sinonimoMedicamentoJn,
				sinonimoMedicamentoJn);
	}

	/**
	 * Cria uma instancia de RapServidores
	 * 
	 * @return
	 */
	private RapServidores getServidor() {
		RapServidores servidor = new RapServidores();
		RapServidoresId servidorId = new RapServidoresId();
		servidorId.setMatricula(Integer.valueOf("12345"));
		servidorId.setVinCodigo(Short.valueOf("1"));
		RapPessoasFisicas pessoaFisica = new RapPessoasFisicas();
		pessoaFisica.setNome(NOME_PESSOA_FISICA);
		servidor.setId(servidorId);
		servidor.setPessoaFisica(pessoaFisica);
		servidor.setUsuario("AGHU");
		return servidor;
	}
	
}

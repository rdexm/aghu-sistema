package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe de teste unitário para TipoApresentacaoMedicamentoRN
 * 
 * @author lcmoura
 * 
 */
public class TipoApresentacaoMedicamentoRNTest extends AGHUBaseUnitTest<TipoApresentacaoMedicamentoRN> {

	private static final String NOME_PESSOA_FISICA = "PESSOA FÍSICA";
	private static final String DESCRICAO_TIPO_APRESENTACAO = "DESCRIÇÃO";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private AfaTipoApresentacaoMedicamentoDAO mockedAfaTipoApresentacaoMedicamentoDAO;
	@Mock
	private AfaMedicamentoDAO mockedAfaMedicamentoDAO;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void preInsertTipoApresentacaoMedicamentoSuccess()
			throws ApplicationBusinessException {

		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(null);
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null, null);

		systemUnderTest
				.preInsertTipoApresentacaoMedicamento(tipoApresentacaoMedicamento);

		Assert.assertEquals(DESCRICAO_TIPO_APRESENTACAO,
				tipoApresentacaoMedicamento.getDescricao());
		Assert.assertNotNull(tipoApresentacaoMedicamento.getCriadoEm());

	}

	@Test
	public void preInsertTipoApresentacaoMedicamentoFailServidor()
			throws ApplicationBusinessException {

		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(null);
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(null);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null, null);

		try {
			systemUnderTest
					.preInsertTipoApresentacaoMedicamento(tipoApresentacaoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00169");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00169", e.getMessage());
		}

	}

	@Test
	public void preInsertTipoApresentacaoMedicamentoFailSigla()
			throws ApplicationBusinessException {

		final AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoOLD = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null, null);
		
		Mockito.when(mockedAfaTipoApresentacaoMedicamentoDAO.obterPorChavePrimaria(Mockito.anyString())).thenReturn(tipoApresentacaoMedicamentoOLD);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null, null);

		try {
			systemUnderTest
					.preInsertTipoApresentacaoMedicamento(tipoApresentacaoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00027");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00027", e.getMessage());
		}

	}

	@Test
	public void preUpdateTipoApresentacaoMedicamentoSuccess()
			throws ApplicationBusinessException {
		
		Mockito.when(mockedAfaMedicamentoDAO.obterMedicamentosAtivosPorTipoApresentacaoCount(Mockito.anyString())).thenReturn(Long.valueOf(0));
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null, null);
		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoOld = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null, null);

		systemUnderTest.preUpdateTipoApresentacaoMedicamento(
				tipoApresentacaoMedicamentoOld, tipoApresentacaoMedicamento);

		Assert.assertEquals(DESCRICAO_TIPO_APRESENTACAO,
				tipoApresentacaoMedicamento.getDescricao());
	}

	@Test
	public void preUpdateTipoApresentacaoMedicamentoFailDescricao()
			throws ApplicationBusinessException {
		
		Mockito.when(mockedAfaMedicamentoDAO.obterMedicamentosAtivosPorTipoApresentacaoCount(Mockito.anyString())).thenReturn(Long.valueOf(0));
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO + " ALTERADO", null,
				DominioSituacao.I);
		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoOld = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, null,
				DominioSituacao.A);
		try {
			systemUnderTest
					.preUpdateTipoApresentacaoMedicamento(
							tipoApresentacaoMedicamentoOld,
							tipoApresentacaoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00188");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00188", e.getMessage());
		}
	}

	@Test
	public void preUpdateTipoApresentacaoMedicamentoFailEmUso()
			throws ApplicationBusinessException {
		
		Mockito.when(mockedAfaMedicamentoDAO.obterMedicamentosAtivosPorTipoApresentacaoCount(Mockito.anyString())).thenReturn(Long.valueOf(10));
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, anteOntem.getTime(),
				DominioSituacao.I);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoOld = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, anteOntem.getTime(),
				DominioSituacao.A);

		try {
			systemUnderTest
					.preUpdateTipoApresentacaoMedicamento(
							tipoApresentacaoMedicamentoOld,
							tipoApresentacaoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00187");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("AFA_00187", e.getMessage());
		}

	}

	@Test
	public void preDeleteTipoApresentacaoMedicamentoSuccess()
			throws BaseException {

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, anteOntem.getTime(),
				null);

		systemUnderTest
				.preDeleteTipoApresentacaoMedicamento(tipoApresentacaoMedicamento);

		Assert.assertEquals(tipoApresentacaoMedicamento.getCriadoEm(),
				anteOntem.getTime());
	}

	@Test
	public void preDeleteTipoApresentacaoMedicamentoFailParam()
			throws BaseException {

		Mockito.doThrow(new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00173)).doNothing().when(
				mockedFarmaciaFacade).verificarDelecao(Mockito.any(Date.class));
		
		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, anteOntem.getTime(),
				null);

		try {
			systemUnderTest
					.preDeleteTipoApresentacaoMedicamento(tipoApresentacaoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00173");
		} catch (BaseException e) {
			Assert.assertEquals("AFA_00173", e.getMessage());
		}
	}

	@Test
	public void preDeleteTipoApresentacaoMedicamentoFailPrazo()
			throws BaseException {

		Mockito.doThrow(new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00172)).doNothing().when(mockedFarmaciaFacade).verificarDelecao(Mockito.any(Date.class));

		Calendar mesPassado = Calendar.getInstance();
		mesPassado.add(Calendar.MONTH, -1);

		AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento(
				"TAP", null, DESCRICAO_TIPO_APRESENTACAO, mesPassado.getTime(),
				null);

		try {
			systemUnderTest
					.preDeleteTipoApresentacaoMedicamento(tipoApresentacaoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00172");
		} catch (BaseException e) {
			Assert.assertEquals("AFA_00172", e.getMessage());
		}

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

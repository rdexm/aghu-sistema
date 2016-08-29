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
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe de teste unitário para GrupoUsoMedicamentoRN
 * 
 * @author lcmoura
 * 
 */
public class GrupoUsoMedicamentoRNTest extends AGHUBaseUnitTest<GrupoUsoMedicamentoRN>{

	private static final String NOME_PESSOA_FISICA = "PESSOA FÍSICA";
	private static final String DESCRICAO_GRUPO_USO = "DESCRIÇÃO";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IFarmaciaFacade mockedFarmaciaFacade;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Test
	public void preInsertGrupoUsoMedicamentoSuccess()
			throws ApplicationBusinessException {

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null, null);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
		
		systemUnderTest.preInsertGrupoUsoMedicamento(grupoUsoMedicamento);

		Assert.assertEquals(DESCRICAO_GRUPO_USO, grupoUsoMedicamento
				.getDescricao());
		Assert.assertNotNull(grupoUsoMedicamento.getCriadoEm());

	}

	@Test
	public void preInsertGrupoUsoMedicamentoFail() throws ApplicationBusinessException {

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null, null);

		try {
			systemUnderTest.preInsertGrupoUsoMedicamento(grupoUsoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00169");
		} catch (BaseException  e) {
			Assert.assertEquals("AFA_00169", e.getMessage());
		}

	}

	@Test
	public void preUpdateGrupoUsoMedicamentoSuccess()
			throws ApplicationBusinessException {

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null,
				DominioIndRespAvaliacao.C);
		AfaGrupoUsoMedicamento grupoUsoMedicamentoOld = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null,
				DominioIndRespAvaliacao.C);

		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
		
		systemUnderTest.preUpdateGrupoUsoMedicamento(grupoUsoMedicamentoOld,
				grupoUsoMedicamento);

		Assert.assertEquals(DESCRICAO_GRUPO_USO, grupoUsoMedicamento
				.getDescricao());
	}

	@Test
	public void preUpdateGrupoUsoMedicamentoFailDescricao()
			throws ApplicationBusinessException {

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO + " ALTERADO", null, null,
				DominioIndRespAvaliacao.C);
		AfaGrupoUsoMedicamento grupoUsoMedicamentoOld = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null,
				DominioIndRespAvaliacao.C);

		try {
			Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
			
			systemUnderTest.preUpdateGrupoUsoMedicamento(
					grupoUsoMedicamentoOld, grupoUsoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00175");
		} catch (BaseException  e) {
			Assert.assertEquals("AFA_00175", e.getMessage());
		}
	}

	@Test
	public void preUpdateGrupoUsoMedicamentoFailResponsavel()
			throws ApplicationBusinessException {

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null,
				DominioIndRespAvaliacao.C);
		AfaGrupoUsoMedicamento grupoUsoMedicamentoOld = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, null, null,
				DominioIndRespAvaliacao.P);

		try {
			Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(this.getServidor());
			
			systemUnderTest.preUpdateGrupoUsoMedicamento(
					grupoUsoMedicamentoOld, grupoUsoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00175");
		} catch (BaseException  e) {
			Assert.assertEquals("AFA_00175", e.getMessage());
		}
	}

	@Test
	public void preDeleteGrupoUsoMedicamentoSuccess() throws BaseException {

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, anteOntem.getTime(), null,
				DominioIndRespAvaliacao.C);

		systemUnderTest.preDeleteGrupoUsoMedicamento(grupoUsoMedicamento);

		Assert.assertEquals(grupoUsoMedicamento.getCriadoEm(), anteOntem
				.getTime());
	}

	@Test
	public void preDeleteGrupoUsoMedicamentoFailParam()
			throws BaseException {

		Calendar anteOntem = Calendar.getInstance();
		anteOntem.add(Calendar.DAY_OF_MONTH, -2);

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, anteOntem.getTime(), null,
				DominioIndRespAvaliacao.C);

		try {
			Mockito.doThrow(new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00173))
					.when(mockedFarmaciaFacade).verificarDelecao(Mockito.any(Date.class));
			
			systemUnderTest.preDeleteGrupoUsoMedicamento(grupoUsoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00173");
		} catch (BaseException e) {
			Assert.assertEquals("AFA_00173", e.getMessage());
		}
	}

	@Test
	public void preDeleteGrupoUsoMedicamentoFailPrazo()
			throws BaseException {

		Calendar mesPassado = Calendar.getInstance();
		mesPassado.add(Calendar.MONTH, -1);

		AfaGrupoUsoMedicamento grupoUsoMedicamento = new AfaGrupoUsoMedicamento(
				null, null, DESCRICAO_GRUPO_USO, mesPassado.getTime(), null,
				DominioIndRespAvaliacao.C);

		try {
			Mockito.doThrow(new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00172))
					.when(mockedFarmaciaFacade).verificarDelecao(Mockito.any(Date.class));
			
			systemUnderTest.preDeleteGrupoUsoMedicamento(grupoUsoMedicamento);
			Assert.fail("Deveria ter ocorrido a excessão AFA_00172");
		} catch (BaseException e) {
			Assert.assertEquals("AFA_00172", e.getMessage());
		}

	}

	/**
	 * Cria uma instancia de RapServidores
	 * 
	 * 
	 * @return
	 */
	private RapServidores getServidor() {
		RapServidores servidor = new RapServidores();
		RapPessoasFisicas pessoaFisica = new RapPessoasFisicas();
		pessoaFisica.setNome(NOME_PESSOA_FISICA);
		servidor.setPessoaFisica(pessoaFisica);
		return servidor;
	}
	
}

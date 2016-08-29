package br.gov.mec.aghu.estoque.business;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.estoque.business.SceReqMateriaisRN.SceReqMateriaisRNExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class SceReqMateriaisRNTest extends AGHUBaseUnitTest<SceReqMateriaisRN> {

	@Mock
	private SceReqMateriaisDAO mockedSceReqMateriaisDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private SceItemRmsDAO mockedSceItemRmsDAO;
	@Mock
	private SceTipoMovimentosRN mockedSceTipoMovimentosRN;
	@Mock
	private SceAlmoxarifadosRN mockedSceAlmoxarifadosRN;
	@Mock
	private SceMovimentoMaterialRN mockedSceMovimentoMaterialRN;
	@Mock
	private SceItemRmsRN mockedSceItemRmsRN;
	@Mock
	private SceEstoqueAlmoxarifadoDAO mockedSceEstoqueAlmoxarifadoDAO;
	@Mock
	private ICentroCustoFacade mockedCentroCustoFacade;
	@Mock
	private ICascaFacade mockedCascaFacade;
	@Mock
	private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
	@Mock
	private IServidorLogadoFacade mockedServidorLogadoFacade;

	private final static String MENSAGEM_DEVERIA_TER_OCORRIDO = "Deveria ter ocorrido a exceção ";
	private final static String MENSAGEM_OCORREU = "Ocorreu ";

	@Before
	public void doBeforeEachTestCase() {
		Mockito.when(mockedCentroCustoFacade.obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao())
				.thenReturn(new FccCentroCustos());

		Mockito.when(mockedCascaFacade.usuarioTemPerfil(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Boolean.TRUE);

		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}

	public SceReqMaterial getDefaultInstance() {

		SceReqMaterial sceReqMateriais = new SceReqMaterial();

		// Tipo Movimento
		SceTipoMovimento tipoMovimento = new SceTipoMovimento();
		tipoMovimento.setId(new SceTipoMovimentoId());
		sceReqMateriais.setTipoMovimento(tipoMovimento);

		// Servidor
		RapServidores servidor = new RapServidores();
		RapServidoresId servidorId = new RapServidoresId();
		servidorId.setMatricula(0);
		servidorId.setVinCodigo((short) 0);
		sceReqMateriais.setServidor(servidor);
		sceReqMateriais.setDtGeracao(new Date(0));
		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.A);

		// Servidor Confirmado
		RapServidores servidorConfirmado = new RapServidores();
		RapServidoresId servidorConfirmadoId = new RapServidoresId();
		servidorConfirmadoId.setMatricula(0);
		servidorConfirmadoId.setVinCodigo((short) 0);
		servidorConfirmado.setId(servidorConfirmadoId);
		sceReqMateriais.setDtConfirmacao(new Date());
		sceReqMateriais.setServidorConfirmado(servidorConfirmado);

		// Servidor Cancelado
		RapServidores servidorCancelado = new RapServidores();
		RapServidoresId servidorCanceladoId = new RapServidoresId();
		servidorCanceladoId.setMatricula(0);
		servidorCanceladoId.setVinCodigo((short) 0);
		servidorCancelado.setId(servidorCanceladoId);
		sceReqMateriais.setDtCancelamento(new Date());
		sceReqMateriais.setServidorCancelado(servidorCancelado);

		// Servidor Efetivado
		RapServidores servidorEfetivado = new RapServidores();
		RapServidoresId servidorEfetivadoId = new RapServidoresId();
		servidorEfetivadoId.setMatricula(0);
		servidorEfetivadoId.setVinCodigo((short) 0);
		servidorEfetivado.setId(servidorEfetivadoId);
		sceReqMateriais.setDtEfetivacao(new Date());
		sceReqMateriais.setServidorEfetivado(servidorEfetivado);

		// Servidor Estornado
		RapServidores servidorEstornado = new RapServidores();
		RapServidoresId servidorEstornadoId = new RapServidoresId();
		servidorEstornadoId.setMatricula(0);
		servidorEstornadoId.setVinCodigo((short) 0);
		servidorEstornado.setId(servidorEstornadoId);
		sceReqMateriais.setDtEstorno(new Date());
		sceReqMateriais.setServidorEstornado(servidorEstornado);

		// Almoxarifado
		SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq((short) 0);
		sceReqMateriais.setAlmoxarifado(almoxarifado);

		// Outros
		sceReqMateriais.setEstorno(false);

		return sceReqMateriais;
	}

	@Test
	public void testVerificarRequisicaoEstornadaError001() {

		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		sceReqMateriaisOriginal.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
		sceReqMateriaisOriginal.setEstorno(true);

		try {
			systemUnderTest.verificarRequisicaoEstornada(sceReqMateriaisOriginal);

			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00887);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00887, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError001() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		RapServidores servidor = new RapServidores();
		RapServidoresId id = new RapServidoresId();
		servidor.setId(id);
		sceReqMateriaisOriginal.setServidor(servidor); // Teste

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00307);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00307, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError002() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
		sceReqMateriaisOriginal.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00308);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00308, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError003() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.A);
		sceReqMateriaisOriginal.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00316);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00316, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError004() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		sceReqMateriaisOriginal.setIndSituacao(DominioSituacaoRequisicaoMaterial.A);
		sceReqMateriais.setEstorno(true);
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00309);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00309, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError005() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		RapServidores servidorConfirmado = new RapServidores();
		RapServidoresId servidorConfirmadoId = new RapServidoresId();
		servidorConfirmadoId.setMatricula(0);
		servidorConfirmadoId.setVinCodigo((short) 0);
		servidorConfirmado.setId(servidorConfirmadoId);
		sceReqMateriaisOriginal.setDtConfirmacao(new Date(1000));
		sceReqMateriaisOriginal.setServidorConfirmado(servidorConfirmado);
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00311);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00311, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError006() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		RapServidores servidorCancelado = new RapServidores();
		RapServidoresId servidorCanceladoId = new RapServidoresId();
		servidorCanceladoId.setMatricula(1);
		servidorCanceladoId.setVinCodigo((short) 1);
		servidorCancelado.setId(servidorCanceladoId);
		sceReqMateriais.setDtConfirmacao(new Date());
		sceReqMateriais.setServidorCancelado(servidorCancelado);
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.C); // Teste

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00796);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00796, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError007() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		RapServidores servidorEfetivado = new RapServidores();
		RapServidoresId servidorEfetivadoId = new RapServidoresId();
		servidorEfetivadoId.setMatricula(1);
		servidorEfetivadoId.setVinCodigo((short) 1);
		servidorEfetivado.setId(servidorEfetivadoId);
		sceReqMateriais.setDtConfirmacao(new Date());
		sceReqMateriais.setServidorEfetivado(servidorEfetivado);
		new SceAlmoxarifado();
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.G); // Teste

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00312);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00312, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError008() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();

		// Teste
		RapServidores servidorEstornado = new RapServidores();
		RapServidoresId servidorEstornadoId = new RapServidoresId();
		servidorEstornadoId.setMatricula(1);
		servidorEstornadoId.setVinCodigo((short) 1);
		servidorEstornado.setId(servidorEstornadoId);
		sceReqMateriais.setDtEstorno(new Date());
		sceReqMateriais.setServidorEstornado(servidorEstornado);
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		sceReqMateriais.setEstorno(false); // Teste

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00313);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00313, e.getCode());
		}

	}

	@Test
	public void testVerificarAlteracoesRequisicaoMateriaisError009() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();
		SceReqMaterial sceReqMateriaisOriginal = this.getDefaultInstance();
		sceReqMateriais.setServidor(new RapServidores(new RapServidoresId()));
		sceReqMateriaisOriginal.setServidor(new RapServidores(new RapServidoresId()));

		// Teste
		SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
		almoxarifado.setSeq((short) 1);
		sceReqMateriaisOriginal.setAlmoxarifado(almoxarifado);

		try {
			systemUnderTest.verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.SCE_00315);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(), SceReqMateriaisRNExceptionCode.SCE_00315, e.getCode());
		}

	}

	@Test
	public void testValidarSceReqMateriaisNumeroCUMError001() {

		SceReqMaterial sceReqMateriais = this.getDefaultInstance();

		// Teste
		SceRmrPaciente rmrPaciente = new SceRmrPaciente();
		sceReqMateriais.setRmrPaciente(rmrPaciente);

		try {
			systemUnderTest.validarSceReqMateriaisNumeroCUM(sceReqMateriais);
			Assert.fail(MENSAGEM_DEVERIA_TER_OCORRIDO + SceReqMateriaisRNExceptionCode.NUMERO_CUM_NAO_ENCONTRADA);
		} catch (BaseException e) {
			Assert.assertEquals(MENSAGEM_OCORREU + e.getCode(),
					SceReqMateriaisRNExceptionCode.NUMERO_CUM_NAO_ENCONTRADA, e.getCode());
		}

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

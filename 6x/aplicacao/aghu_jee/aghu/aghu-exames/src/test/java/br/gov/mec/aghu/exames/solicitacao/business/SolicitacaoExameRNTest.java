package br.gov.mec.aghu.exames.solicitacao.business;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameRN.SolicitacaoExameRNExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author bsoliveira
 *
 */
public class SolicitacaoExameRNTest extends AGHUBaseUnitTest<SolicitacaoExameRN>{

	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;
	@Mock
	private AelSolicitacaoExameDAO mockedAelSolicitacaoExameDAO;
	@Mock
	private AelItemSolicitacaoExameDAO mockedAelItemSolicitacaoExameDAO;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private ISolicitacaoExameFacade mockedSolicitacaoExameFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	private AelSolicitacaoExames oldEntity = new AelSolicitacaoExames();
	private AelSolicitacaoExames newEntity = new AelSolicitacaoExames();
 	
	@Before
	public void inicio() {
		try {
			whenObterServidorLogado();
		} catch (BaseException e) {
			fail();
		}
	}
	
	/**
	 * Verifica se o servidor foi preenchido corretamente.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizaUsuarioEDataHoraCriacaoVerificaServidorTest() throws BaseException{

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();

		systemUnderTest.atualizaUsuarioEDataHoraCriacao(solicitacaoExame);

		assertNotNull(solicitacaoExame.getServidor());

	}

	/**
	 * Verifica se a data foi preenchida.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizaUsuarioEDataHoraCriacaoVerificaDataHoraTest() throws BaseException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();

		systemUnderTest.atualizaUsuarioEDataHoraCriacao(solicitacaoExame);

		assertNotNull(solicitacaoExame.getCriadoEm());

	}

	/**
	 * Verifica se a data foi preenchida corretamente.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizaUsuarioEDataHoraCriacaoVerificaDataHoraCorrenteTest() throws BaseException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();

		systemUnderTest.atualizaUsuarioEDataHoraCriacao(solicitacaoExame);

		assertTrue(DateUtil.truncaData(solicitacaoExame.getCriadoEm()).compareTo(DateUtil.truncaData(new Date())) == 0);

	}

	/**
	 * Se unidade for nula, deve pegar a unidade funcional do atendimento 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalPegaUnidadeDeAtendimentoTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();

		atendimento.setUnidadeFuncional(unidadeFuncional);
		solicitacaoExame.setAtendimento(atendimento);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);

		assertNotNull(solicitacaoExame.getUnidadeFuncional());

	}

	/**
	 * Se unidade NÃO for nula, deve pegar a unidade funcional da solicitação 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalPegaUnidadeFuncionalTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);

		Assert.assertEquals(unidadeFuncional2.getSeq(), solicitacaoExame.getUnidadeFuncional().getSeq());

	}

	/**
	 * Se unidade estiver inativa deve subir uma exceção. 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalEstaInativaTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.I);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		try {

			systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);
			fail("Deveria ter ocorrido uma exception (AEL_00395)!!!");

		} catch (BaseException e) {

			Assert.assertEquals("AEL_00395", e.getMessage());

		}

	}

	/**
	 * Se unidade estiver ativa, nada acontece. 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalEstaAtivaTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.A);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);


		systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);
		Assert.assertEquals(unidadeFuncional2.getSeq(), solicitacaoExame.getUnidadeFuncional().getSeq());

	}

	/**
	 * Caso a unidade do obstetrica ou co o usuário deve preencher a informação
	 * de recem nascido. 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalPermiteRecemNascidoMasEhNuloTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.A);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		try {

			systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);
			fail("Deveria ter ocorrido uma exception AEL_00396!!!");

		} catch (BaseException e) {

			Assert.assertEquals("AEL_00396", e.getMessage());

		}

	}

	/**
	 * Caso a unidade do obstetrica ou co o usuário deve preencher a informação
	 * de recem nascido. 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalPermiteRecemNascidoMasNaoEhNuloTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.A);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);
		solicitacaoExame.setRecemNascido(false);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.S);

		systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);
		Assert.assertEquals(unidadeFuncional2.getSeq(), solicitacaoExame.getUnidadeFuncional().getSeq());

	}

	/**
	 * Caso a unidade do obstetrica ou co o usuário deve preencher a informação
	 * de recem nascido. 
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaUnidadeFuncionalPermiteRecemNascidoTrueTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.A);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);
		solicitacaoExame.setRecemNascido(true);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);
		

		try {

			systemUnderTest.verificaUnidadeFuncional(solicitacaoExame);
			fail("Deveria ter ocorrido uma exception AEL_01261!!!");

		} catch (BaseException e) {

			Assert.assertEquals("AEL_01261", e.getMessage());

		}

	}

	/**
	 * Para atendimento de origem diferente de Paciente Externo,
	 * responsável deve ser preenchido.
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificaAtendimentoExternoSemResponsavelTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.A);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);
		solicitacaoExame.setRecemNascido(true);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(DominioSimNao.N);

		try {

			systemUnderTest.verificaAtendimento(solicitacaoExame);
			fail("Deveria ter ocorrido uma exception AEL_00407!!!");

		} catch (BaseException e) {

			Assert.assertEquals("AEL_00407", e.getMessage());

		}

	}

	/**
	 * Atualiza do valor do convênio plano (CSP_CNV_CODIGO, CSP_SEQ).
	 *   
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void atualizaConvenioAtendimentoTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional1 = new AghUnidadesFuncionais();
		unidadeFuncional1.setSeq(Short.valueOf("1"));
		AghUnidadesFuncionais unidadeFuncional2 = new AghUnidadesFuncionais();
		unidadeFuncional2.setSeq(Short.valueOf("2"));
		unidadeFuncional2.setIndSitUnidFunc(DominioSituacao.A);

		atendimento.setUnidadeFuncional(unidadeFuncional1);
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		solicitacaoExame.setAtendimento(atendimento);
		solicitacaoExame.setUnidadeFuncional(unidadeFuncional2);
		solicitacaoExame.setRecemNascido(true);

		Mockito.when(mockedPacienteFacade.buscarConvenioExamesLaudos(Mockito.anyInt())).thenReturn(new ConvenioExamesLaudosVO());
		Mockito.when(mockedFaturamentoFacade.obterConvenioSaudePlano(Mockito.anyShort(), Mockito.anyByte())).thenReturn(new FatConvenioSaudePlano());

		systemUnderTest.atualizaConvenio(solicitacaoExame);
		assertNotNull(solicitacaoExame.getConvenioSaudePlano());	

	}

	@Test
	public void recuperarLocalPacienteTest() throws ApplicationBusinessException {

		String resultado = null;

		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		atendimento.setUnidadeFuncional(unidadeFuncional);

		Mockito.when(mockedAghuFacade.verificarCaracteristicaDaUnidadeFuncional(Mockito.anyShort(), Mockito.any(ConstanteAghCaractUnidFuncionais.class)))
		.thenReturn(DominioSimNao.N);

		// Origem do atendimento Ambulatorio
		atendimento.setOrigem(DominioOrigemAtendimento.A);
		resultado = systemUnderTest.recuperarLocalPaciente(atendimento);
		Assert.assertEquals(resultado, "U:AMB");

		// Origem do atendimento Paciente Externo
		atendimento.setOrigem(DominioOrigemAtendimento.X);
		resultado = systemUnderTest.recuperarLocalPaciente(atendimento);
		Assert.assertEquals(resultado, "U:EXT");

		// Origem do atendimento Doacao de sangue
		atendimento.setOrigem(DominioOrigemAtendimento.D);
		resultado = systemUnderTest.recuperarLocalPaciente(atendimento);
		Assert.assertEquals(resultado, "U:DOA");

		// Origem do atendimento Cirurgia
		atendimento.setOrigem(DominioOrigemAtendimento.C);
		resultado = systemUnderTest.recuperarLocalPaciente(atendimento);
		Assert.assertEquals(resultado, "U:CIR");

		// Origem do atendimento Urgencia com sigla de atendimento
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		unidadeFuncional.setSigla("UF");
		resultado = systemUnderTest.recuperarLocalPaciente(atendimento);
		Assert.assertEquals(resultado, "U:"+atendimento.getUnidadeFuncional().getSigla());

		// Origem do atendimento Urgencia com sigla de atendimento nulla
		atendimento.setOrigem(DominioOrigemAtendimento.U);
		unidadeFuncional.setSigla(null);
		resultado = systemUnderTest.recuperarLocalPaciente(atendimento);
		Assert.assertEquals(resultado.trim(), "");

	}

	@Test
	/**
	 * Verifica com sucesso o método verificarCamposAlterados
	 */
	public void verificarCamposAlteradosSuccess() {
		AelSolicitacaoExames oldEntity = new AelSolicitacaoExames();
		AelSolicitacaoExames newEntity = new AelSolicitacaoExames();

		RapServidores servidor1 = new RapServidores();
		RapServidores servidor2 = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setVinCodigo(Short.valueOf("1"));
		id1.setMatricula(Integer.valueOf("2"));
		RapServidoresId id2 = new RapServidoresId();
		id2.setVinCodigo(Short.valueOf("1"));
		id2.setMatricula(Integer.valueOf("2"));
		servidor1.setId(id1);
		servidor2.setId(id2);

		Calendar cal = Calendar.getInstance();
		oldEntity.setCriadoEm(cal.getTime());
		newEntity.setCriadoEm(cal.getTime());
		oldEntity.setServidor(servidor1);
		newEntity.setServidor(servidor2);

		try {
			systemUnderTest.verificarCamposAlterados(oldEntity, newEntity);
		} catch (BaseException e) {
			fail("Exceção não esperada lançada" + e.getMessage());
		}
	}

	@Test
	/**
	 * Verifica com erro o método verificarCamposAlterados, alterando o campo Criado Em
	 */
	public void verificarCamposAlteradosCriadoEmAlterado() {
		AelSolicitacaoExames oldEntity = new AelSolicitacaoExames();
		AelSolicitacaoExames newEntity = new AelSolicitacaoExames();

		Calendar cal = Calendar.getInstance();
		oldEntity.setCriadoEm(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		newEntity.setCriadoEm(cal.getTime());

		try {
			systemUnderTest.verificarCamposAlterados(oldEntity, newEntity);
			fail("Exceção esperada não lançada");
		} catch (BaseException e) {
			Assert.assertEquals(SolicitacaoExameRNExceptionCode.AEL_00369, e.getCode());
		}

	}

	@Test
	/**
	 * Verifica com erro o método verificarCamposAlterados, alterando o campo Ser_Matricula
	 */
	public void verificarCamposAlteradosServidorAlterado() {
		AelSolicitacaoExames oldEntity = new AelSolicitacaoExames();
		AelSolicitacaoExames newEntity = new AelSolicitacaoExames();

		RapServidores servidor1 = new RapServidores();
		RapServidores servidor2 = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setVinCodigo(Short.valueOf("1"));
		id1.setMatricula(Integer.valueOf("2"));
		RapServidoresId id2 = new RapServidoresId();
		id2.setVinCodigo(Short.valueOf("1"));
		id2.setMatricula(Integer.valueOf("3"));
		servidor1.setId(id1);
		servidor2.setId(id2);

		oldEntity.setServidor(servidor1);
		newEntity.setServidor(servidor2);

		try {
			systemUnderTest.verificarCamposAlterados(oldEntity, newEntity);
			fail("Exceção esperada não lançada");
		} catch (BaseException e) {
			Assert.assertEquals(SolicitacaoExameRNExceptionCode.AEL_00369, e.getCode());
		}
	}

	@Test
	/**
	 * Verifica com erro o método verificarCamposAlterados, alterando o campo Vin_Codigo
	 */
	public void verificarCamposAlteradosServidorAlterado02() {
		RapServidores servidor1 = new RapServidores();
		RapServidores servidor2 = new RapServidores();
		RapServidoresId id1 = new RapServidoresId();
		id1.setVinCodigo(Short.valueOf("1"));
		id1.setMatricula(Integer.valueOf("2"));
		RapServidoresId id2 = new RapServidoresId();
		id2.setVinCodigo(Short.valueOf("3"));
		id2.setMatricula(Integer.valueOf("2"));
		servidor1.setId(id1);
		servidor2.setId(id2);

		oldEntity.setServidor(servidor1);
		newEntity.setServidor(servidor2);

		try {
			systemUnderTest.verificarCamposAlterados(oldEntity, newEntity);
			fail("Exceção esperada não lançada");
		} catch (BaseException e) {
			Assert.assertEquals(SolicitacaoExameRNExceptionCode.AEL_00369, e.getCode());
		}
	}

	@Test
	/**
	 * Verifica o sucesso do método verificarInformacoesClinicas
	 */
	public void verificarInformacoesClinicasSuccess() {
		systemUnderTest = new SolicitacaoExameRN() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2328376252143888287L;
			@Override
			protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
				return mockedAelItemSolicitacaoExameDAO;
			}
			@Override
			protected boolean verificarInfClinicasItensSolicExames(String parametroCA, Integer soeSeq) {
				return false;
			};
			@Override
			protected IAghuFacade getAghuFacade() {
				return mockedAghuFacade;
			}
			
			@Override
			protected IParametroFacade getParametroFacade() {
				return mockedParametroFacade;
			}
		};
		
		
		oldEntity.setInformacoesClinicas("INFCLIN");
		newEntity.setInformacoesClinicas(null);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.valueOf("1"));
		newEntity.setAtendimento(atendimento);

		try {
			AghParametros param = new AghParametros();
			param.setVlrTexto("CA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenReturn(param);

			systemUnderTest.verificarInformacoesClinicas(oldEntity, newEntity);
		} catch (Exception e) {
			fail("Exceção não esperada lançada" + e.getMessage());
		}
	}


	@Test
	/**
	 * Verifica o sucesso do método verificarInformacoesClinicas quando o atd_seq é = null
	 */
	public void verificarInformacoesClinicasSuccessInfoClinAlteradaAtendNull() {
		oldEntity.setInformacoesClinicas("INFCLIN");
		newEntity.setInformacoesClinicas(null);
		newEntity.setAtendimento(null);
		try {
			systemUnderTest.verificarInformacoesClinicas(oldEntity, newEntity);
		} catch (Exception e) {
			fail("Exceção não esperada lançada" + e.getMessage());
		}
	}

	@Test
	/**
	 * Verifica o sucesso do método verificarInformacoesClinicas quando as inf.clinicas é != null
	 */
	public void verificarInformacoesClinicasSuccessInfoClinAlteradaENotNull() {
		oldEntity.setInformacoesClinicas("INFCLIN");
		newEntity.setInformacoesClinicas("INFCLIN2");
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.valueOf("1"));
		newEntity.setAtendimento(atendimento);

		try {
			systemUnderTest.verificarInformacoesClinicas(oldEntity, newEntity);
		} catch (Exception e) {
			fail("Exceção não esperada lançada" + e.getMessage());
		}
	}

	@Test
	/**
	 * Verifica o erro do método verificarInformacoesClinicas quando um exame possui um indice
	 */
	public void verificarInformacoesClinicasError() {
		systemUnderTest = new SolicitacaoExameRN() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5735518596758506687L;
			@Override
			protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
				return mockedAelItemSolicitacaoExameDAO;
			}
			@Override
			protected boolean verificarInfClinicasItensSolicExames(String parametroCA, Integer soeSeq) {
				return true;
			};
			@Override
			protected IAghuFacade getAghuFacade() {
				return mockedAghuFacade;
			}
			
			@Override
			protected IParametroFacade getParametroFacade() {
				return mockedParametroFacade;
			}
		};
		
		oldEntity.setInformacoesClinicas("INFCLIN");
		newEntity.setInformacoesClinicas(null);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.valueOf("1"));
		newEntity.setAtendimento(atendimento);

		try {
			AghParametros param = new AghParametros();
			param.setVlrTexto("CA");
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenReturn(param);

			systemUnderTest.verificarInformacoesClinicas(oldEntity, newEntity);
			fail("Exceção esperada não lançada");
		} catch (BaseException e) {
			Assert.assertEquals(SolicitacaoExameRNExceptionCode.AEL_00422, e.getCode());
		}
	}
	
	@Test
	/**
	 * Verifica o erro do método verificarInformacoesClinicas quando o parametro P_SITUACAO_CANCELADO não esta cadastrado
	 */
	public void verificarInformacoesClinicasErrorParametro() {
		oldEntity.setInformacoesClinicas("INFCLIN");
		newEntity.setInformacoesClinicas(null);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(Integer.valueOf("1"));
		newEntity.setAtendimento(atendimento);

		
		AghParametros param = new AghParametros();
		param.setVlrTexto(null);
		try {
			Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
			.thenReturn(param);

			systemUnderTest.verificarInformacoesClinicas(oldEntity, newEntity);
			fail("Exceção esperada não lançada");
		} catch (BaseException e) {
			Assert.assertEquals(SolicitacaoExameRNExceptionCode.AEL_00421, e.getCode());
		}
	}
	
	@Test
	/**
	 * Verifica o retorno = true do método verificarInfClinicasItensSolicExames quando um exame possui um indice
	 */
	public void verificarInfClinicasItensSolicExamesTrue() {
		
			List<AelItemSolicitacaoExames> lista = new ArrayList<AelItemSolicitacaoExames>();
			AelItemSolicitacaoExames ise1 = new AelItemSolicitacaoExames();
			AelItemSolicitacaoExames ise2 = new AelItemSolicitacaoExames();
			AelItemSolicitacaoExames ise3 = new AelItemSolicitacaoExames();
			AelUnfExecutaExames uee1 = new AelUnfExecutaExames();
			uee1.setIndExigeInfoClin(Boolean.FALSE);
			AelUnfExecutaExames uee2 = new AelUnfExecutaExames();
			uee2.setIndExigeInfoClin(Boolean.TRUE);
			AelUnfExecutaExames uee3 = new AelUnfExecutaExames();
			uee3.setIndExigeInfoClin(Boolean.FALSE);
			
			ise1.setAelUnfExecutaExames(uee1);
			ise2.setAelUnfExecutaExames(uee2);
			ise3.setAelUnfExecutaExames(uee3);
			lista.add(ise1);
			lista.add(ise2);
			lista.add(ise3);
			
			Mockito.when(mockedAelItemSolicitacaoExameDAO.pesquisarItemSolicitacaoExamePorSituacaoSOE(Mockito.anyString(), Mockito.anyInt()))
			.thenReturn(lista);
		
		assertTrue(systemUnderTest.verificarInfClinicasItensSolicExames("1", Integer.valueOf("1")));
	}
	
	@Test
	/**
	 * Verifica o retorno = False do método verificarInfClinicasItensSolicExames quando nenhum exame possui um indice
	 */
	public void verificarInfClinicasItensSolicExamesFalse() {
		
			List<AelItemSolicitacaoExames> lista = new ArrayList<AelItemSolicitacaoExames>();
			AelItemSolicitacaoExames ise1 = new AelItemSolicitacaoExames();
			AelItemSolicitacaoExames ise2 = new AelItemSolicitacaoExames();
			AelItemSolicitacaoExames ise3 = new AelItemSolicitacaoExames();
			AelUnfExecutaExames uee1 = new AelUnfExecutaExames();
			uee1.setIndExigeInfoClin(Boolean.FALSE);
			AelUnfExecutaExames uee2 = new AelUnfExecutaExames();
			uee2.setIndExigeInfoClin(Boolean.FALSE);
			AelUnfExecutaExames uee3 = new AelUnfExecutaExames();
			uee3.setIndExigeInfoClin(Boolean.FALSE);
			
			ise1.setAelUnfExecutaExames(uee1);
			ise2.setAelUnfExecutaExames(uee2);
			ise3.setAelUnfExecutaExames(uee3);
			lista.add(ise1);
			lista.add(ise2);
			lista.add(ise3);

			Mockito.when(mockedAelItemSolicitacaoExameDAO.pesquisarItemSolicitacaoExamePorSituacaoSOE(Mockito.anyString(), Mockito.anyInt()))
			.thenReturn(lista);

		Assert.assertFalse(systemUnderTest.verificarInfClinicasItensSolicExames("1", Integer.valueOf("1")));
	}
	
	@Test
	public void atualizaLocalizadorValidoPrimeiraTentativaTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();

		AghParametros param = new AghParametros();
		param.setVlrTexto("S");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
		.thenReturn(param);

		Mockito.when(mockedAelSolicitacaoExameDAO.localizadorValido(Mockito.anyString()))
		.thenReturn(true);
		
		systemUnderTest.atualizaLocalizador(solicitacaoExame);
		assertNotNull(solicitacaoExame.getLocalizador());	

	}

	@Test
	public void atualizaLocalizadorValidoSegundaTentativaTest() throws ApplicationBusinessException {

		AelSolicitacaoExames solicitacaoExame = new AelSolicitacaoExames();

		AghParametros param = new AghParametros();
		param.setVlrTexto("S");
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class)))
		.thenReturn(param);

		Mockito.when(mockedAelSolicitacaoExameDAO.localizadorValido(Mockito.anyString()))
		.thenReturn(false);

		Mockito.when(mockedAelSolicitacaoExameDAO.localizadorValido(Mockito.anyString()))
		.thenReturn(true);

		systemUnderTest.atualizaLocalizador(solicitacaoExame);
		assertNotNull(solicitacaoExame.getLocalizador());

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

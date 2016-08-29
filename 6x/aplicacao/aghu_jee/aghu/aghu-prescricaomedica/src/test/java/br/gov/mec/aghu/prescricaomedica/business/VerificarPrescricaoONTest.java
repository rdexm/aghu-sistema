package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEscoreSaps3DAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoIngressoCtiDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPim2DAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelos testes unitários da classe VerificarPrescricaoON.
 * 
 * @author gmneto
 * 
 */
public class VerificarPrescricaoONTest extends AGHUBaseUnitTest<VerificarPrescricaoON>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IAmbulatorioFacade mockedAmbulatorioFacade;
	@Mock
	private MpmPrescricaoMedicaDAO mockedPrescricaoMedicaDAO;
	@Mock
	private MpmPim2DAO mockedmpmPim2DAO;
	@Mock
	private MpmMotivoIngressoCtiDAO mockedMpmMotivoIngressoCtiDAO;
	@Mock
	private MpmEscoreSaps3DAO mockedMpmEscoreSaps3DAO;
	@Mock
	private PrescricaoMedicaRN mockedPrescricaoMedicaRN;
    @Mock
    private IFarmaciaFacade mockedFarmacisFacade;
	@Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	private Date dataAtual;
	private Integer seq;

	/**
	 * Método executado antes da execução dos testes. Crias os mocks e uma
	 * instancia da classe a ser testada (Com os devidos métodos sobrescritos)
	 */
	@Before
	public void doBeforeEachTestCase() {

		try {
			whenObterServidorLogado();
			
			Mockito.when(mockedAghuFacade.possuiCaracteristicaPorUnidadeEConstante(Mockito.anyShort(), 
					Mockito.any(ConstanteAghCaractUnidFuncionais.class))).thenReturn(true);

			Mockito.when(mockedmpmPim2DAO.pesquisarPim2PorAtendimentoSituacao(Mockito.anyInt(), 
					Mockito.any(DominioSituacaoPim2.class))).thenReturn(null);

			Mockito.when(mockedFarmacisFacade.existeDispensacaoAnteriorPacienteUTI(Mockito.anyInt(), Mockito.anyShort())).thenReturn(Boolean.FALSE);
			
			Mockito.when(mockedMpmMotivoIngressoCtiDAO.pesquisarMotivoIngressoCtisPorAtendimento(Mockito.any(AghAtendimentos.class))).thenReturn(null);
			
			Mockito.when(mockedMpmEscoreSaps3DAO.pesquisarEscorePorAtendimento(Mockito.anyInt())).thenReturn(null);
			
		} catch (BaseException e) {
			fail();
		}
		
		// Valores default para os testes
		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		setSeq(1);
		considerarValorSequencialId(getSeq());

	}

	/**
	 * Data fixa, considerada atual pelos testes
	 * @return
	 */
	private Date getDataAtual() {
		return dataAtual;
	}
	
	private void setDataAtual(Date data) {
		dataAtual = data;
	}
	
	/**
	 * Indica que o mock do DAO de PrescricaoMedica permite que o método
	 * prescricoesAtendimentoDataFim seja chamado durante a execução do teste,
	 * retornando a coleção prescricoes, passada por parâmetro para este método.
	 * 
	 * @param prescricoes
	 */
	private void considerarPrescricoes(final List<MpmPrescricaoMedica> prescricoes) {
		Mockito.when(mockedPrescricaoMedicaDAO.prescricoesAtendimentoDataFim(Mockito.any(AghAtendimentos.class), Mockito.any(Date.class))).thenReturn(prescricoes);
	}
	
	/**
	 * Indica ao mock do DAO da classe Prescricao medica que o metodo
	 * obterValorSequencialId será chamado durante a execução do método,
	 * retornando o valor seq, passado como parâmetro para este método.
	 * 
	 * @param seq
	 */
	private void considerarValorSequencialId(final Integer seq) {
		Mockito.when(mockedPrescricaoMedicaDAO.obterValorSequencialId()).thenReturn(seq);
	}

	private Integer getSeq() {
		return seq;
	}
	
	private void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	/**
	 * Retorna um fake de um servidor logado equivalente àquele retornado pelo
	 * método sobrescrito na classe a ser testada.
	 * 
	 * @return
	 */
	private RapServidores getServidorLogado() {
		RapServidores servidor = new RapServidores();
		servidor.setId(new RapServidoresId(1, (short) 1));
		RapPessoasFisicas pf = new RapPessoasFisicas();
		servidor.setPessoaFisica(pf);
		return servidor;
	}
	
	
	/**
	 * Retorna uma unidade funcional, com horario de validade da prescricao informada
	 * 
	 * @return
	 */
	private AghUnidadesFuncionais obterUnidadeFuncional(int hora, int minuto) {
		AghUnidadesFuncionais uf = new AghUnidadesFuncionais();
		uf.setHrioValidadePme(
				DateUtil.obterData(2000, 1, 1, hora, minuto)
		);
		uf.setNroUnidTempoPmeAdiantadas((short)4);
		uf.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);
		return uf;
	}
	
	private AghAtendimentos obterAtendimento() {
		return obterAtendimento(null);
	}
	
	private AghAtendimentos obterAtendimento(AghUnidadesFuncionais unidadeFuncional) {
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setIndPacAtendimento(DominioPacAtendimento.S);
		atendimento.setOrigem(DominioOrigemAtendimento.I);
		atendimento.setDthrInicio(DateUtil.adicionaDias(getDataAtual(), -2));
		atendimento.setUnidadeFuncional(unidadeFuncional);
		atendimento.setSeq(10);
		return atendimento;
	}

	/**
	 * Valida pós condições após criar nova prescrição 
	 */
	private void validarPosCondicoesNovaPrescricao(
			AghAtendimentos atendimento, 
			MpmPrescricaoMedica prescricao
	) {
		
	}
	
	/**
	 * Método que testa o 'caminho feliz' a edição de uma prescrição, quando não
	 * há nenhuma outra prescrição ativa para o atendimento.
	 */
	@Test
	public void editarPrescricaoTest() {
		final AghAtendimentos atendimento = obterAtendimento();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.L);
		prescricao.setAtendimento(atendimento);
		prescricao.setCriadoEm(new Date());
		prescricao.setDtReferencia(new Date());
		prescricao.setServidor(getServidorLogado());

		considerarPrescricoes(new ArrayList<MpmPrescricaoMedica>());

		try {
			systemUnderTest.editarPrescricao(prescricao, false);

			Assert.assertEquals(DominioSituacaoPrescricao.U, prescricao
					.getSituacao());
			Assert.assertEquals(getServidorLogado(), prescricao
					.getServidorAtualizada());
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}

		
	}

	/**
	 * Método que testa a edição de uma prescrição quando a mesma está em uso.
	 * Neste caso o usuário deve ser notificado que a prescrição está em uso -
	 * para isto a camada de negócio levanta uma exceção com o exceptionCode =
	 * PRESCRICAO_ATUAL_EM_USO
	 */
	@Test
	public void editarPrescricaoEmUsoTest() {
		final AghAtendimentos atendimento = obterAtendimento();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setCriadoEm(DateUtil.obterData(2010, 9, 14, 12, 00));
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		prescricao.setAtendimento(atendimento);
		prescricao.setServidorAtualizada(getServidorLogado());

		considerarPrescricoes(new ArrayList<MpmPrescricaoMedica>());

		try {
			systemUnderTest.editarPrescricao(prescricao, false);
			
		} catch (ApplicationBusinessException e) {
			Assert.assertTrue(true);
		} catch (BaseException e) {
			Assert.fail();
		
		}

		
	}

	/**
	 * Método que testa a edição de uma prescrição quando outra prescrição ativa
	 * para o mesmo atendimento já está em uso. Neste caso a peração não deve
	 * ser permitida.
	 */
	@Test
	public void editarPrescricaoOutraEmUsoTest() {
		final AghAtendimentos atendimento = obterAtendimento();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.L);
		prescricao.setAtendimento(atendimento);
		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(1);
		id.setSeq(1);
		prescricao.setId(id); // setando o id para que a prescrição a ser
		// editada não seja "confundida" com a que está
		// em uso.

		MpmPrescricaoMedica outraPrescricaoEmUso = new MpmPrescricaoMedica();
		outraPrescricaoEmUso.setDtReferencia(
				DateUtil.obterData(2010, 9, 15)
		);
		outraPrescricaoEmUso.setSituacao(DominioSituacaoPrescricao.U);
		outraPrescricaoEmUso.setAtendimento(atendimento);
		MpmPrescricaoMedicaId outroId = new MpmPrescricaoMedicaId();
		outroId.setAtdSeq(1);
		outroId.setSeq(2);
		outraPrescricaoEmUso.setId(outroId); // setando o id para que a
		// prescrição a ser editada não
		// seja "confundida" com a que
		// está em uso.
		outraPrescricaoEmUso.setServidorAtualizada(getServidorLogado());

		final List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		prescricoesDoAtendimento.add(prescricao);
		prescricoesDoAtendimento.add(outraPrescricaoEmUso);

		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest.editarPrescricao(prescricao, false);
			Assert.fail();
		} catch (BaseException e) {
			Assert
					.assertEquals(
							VerificarPrescricaoON.ExceptionCode.OUTRA_PRESCRICAO_EM_USO,
							e.getCode());
		}

		
	}

	/**
	 * Verifica a edição de uma prescrição, quando há outras prescrições ativas
	 * para o atendimento, mas nenhuma delas em uso.
	 */
	@Test
	public void editarPrescricaoOutrasNaoEmUsoTest() {
		final AghAtendimentos atendimento = obterAtendimento();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.L);
		prescricao.setAtendimento(atendimento);
		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(1);
		id.setSeq(1);
		prescricao.setId(id);
		prescricao.setCriadoEm(new Date());
		prescricao.setDtReferencia(new Date());
		prescricao.setServidor(getServidorLogado());

		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setSituacao(DominioSituacaoPrescricao.L);
		outraPrescricao.setAtendimento(atendimento);
		MpmPrescricaoMedicaId outroId = new MpmPrescricaoMedicaId();
		outroId.setAtdSeq(1);
		outroId.setSeq(2);
		outraPrescricao.setId(outroId);

		MpmPrescricaoMedica outraPrescricao2 = new MpmPrescricaoMedica();
		outraPrescricao2.setSituacao(DominioSituacaoPrescricao.L);
		outraPrescricao2.setAtendimento(atendimento);
		MpmPrescricaoMedicaId outroid2 = new MpmPrescricaoMedicaId();
		outroid2.setAtdSeq(1);
		outroid2.setSeq(3);
		outraPrescricao2.setId(outroid2);

		final List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		prescricoesDoAtendimento.add(prescricao);
		prescricoesDoAtendimento.add(outraPrescricao);
		prescricoesDoAtendimento.add(outraPrescricao2);

		considerarPrescricoes(prescricoesDoAtendimento);

		try {

			systemUnderTest.editarPrescricao(prescricao, false);
			Assert.assertEquals(DominioSituacaoPrescricao.U, prescricao
					.getSituacao());
			Assert.assertEquals(getServidorLogado(), prescricao
					.getServidorAtualizada());
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}

		
	}

	/**
	 * Testa a edição de uma prescrição quando a mesma está em uso, após o
	 * usuário já tiver sido alertado (assinalado pelo segundo parâmetro no
	 * método de edição setada para true)
	 */
	@Test
	public void editarPrescricaoCienteEmUsoTest() {
		final AghAtendimentos atendimento = obterAtendimento();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		prescricao.setAtendimento(atendimento);
		prescricao.setCriadoEm(new Date());
		prescricao.setDtReferencia(new Date());
		prescricao.setServidor(getServidorLogado());

		final List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		prescricoesDoAtendimento.add(prescricao);

		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest.editarPrescricao(prescricao, true);
			Assert.assertEquals(DominioSituacaoPrescricao.U, prescricao
					.getSituacao());
			Assert.assertEquals(getServidorLogado(), prescricao
					.getServidorAtualizada());
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}

		
	}

	/**
	 * Testa a criação de uma nova prescrição quando já existe uma outra
	 * prescrição, do mesmo atendimento, em uso.
	 * 
	 * 
	 */
	@Test
	public void criarPrescricaoOutraEmUsoTest() {

		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20,0)
		);
		
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(
				DateUtil.obterData(2010, 9, 15)
		);
		outraPrescricao.setDthrInicio(
				DateUtil.obterData(2010, 9, 15, 20, 0)
		);
		outraPrescricao.setDthrFim(
				DateUtil.obterData(2010, 9, 16, 20, 0)
		);
		outraPrescricao.setSituacao(DominioSituacaoPrescricao.U);
		MpmPrescricaoMedicaId outroId = new MpmPrescricaoMedicaId();
		outroId.setAtdSeq(1);
		outroId.setSeq(2);
		outraPrescricao.setId(outroId);
		outraPrescricao.setServidorAtualizada(getServidorLogado());
		//outraPrescricao.setDtReferencia(date1);

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		prescricoesDoAtendimento.add(outraPrescricao);

		considerarValorSequencialId(seq);
		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);

			Assert.fail();
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.OUTRA_PRESCRICAO_EM_USO,
					e.getCode()
			);
		}

	}

	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) as prescrições não são informatizadas para a unidade funcional do atendimento
	 * 
	 * Resultado esperado:
	 * 
	 * Exceção PRESCRICAO_NAO_INFORMATIZADA.
	 * 
	 */
	@Test
	public void criarPrescricaoNaoInformatizada() {

		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setHrioValidadePme(new Date());
		
		AghAtendimentos atendimento = obterAtendimento(
				//unidade, sem caracteristica de prescricao informatizada
				unidade
		);

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarValorSequencialId(seq);
		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest
					.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.PRESCRICAO_NAO_INFORMATIZADA,
					e.getCode()
			);
		}

		
	}
	
	
	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) não há nenhuma outra prescrição válida p/ o atendimento 
	 * b) o HrioValidade da unidade funcional é anterior ao momento de criação da
	 * prescrição.
	 * 
	 */
	@Test
	public void criarPrescricaoValidadeAnteriorTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 21, 30));
		
		// Atendimento de unidade funcional com HrioValidadePme igual a uma hora atrás.
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20, 0)
		);

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest
					.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);
			
		} catch (BaseException e) {
		}

		
	}
	
	
	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) não há nenhuma outra prescrição válida p/ o atendimento b) o
	 * HrioValidade da unidade funcional é posterior ao momento de criação da
	 * prescrição.
	 * 
	 */
	@Test
	public void criarPrescricaoValidadePosteriorTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 18, 30));
		// Atendimento de unidade funcional com HrioValidadePme igual a uma hora adiante.
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20, 0)
		);

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest
					.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);
			

		} catch (BaseException e) {
		}

		
	}

	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) não há nenhuma outra prescrição válida p/ o atendimento 
	 * b) data de referência é uma data futura dentro do permitido
	 *    (configurado na unidade funcional)
	 * 
	 */
	@Test
	public void criarPrescricaoAdiantadaDentroPermitidoTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 21, 30));
		
		// Criando unidade funcional com tempo de adiantamento de 4 dias
		AghUnidadesFuncionais uf = obterUnidadeFuncional(20,0); 
		uf.setNroUnidTempoPmeAdiantadas((short)4);
		uf.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);
		AghAtendimentos atendimento = obterAtendimento(uf);
		
		// Data de referencia 3 dias após data atual
		Date dataReferencia = DateUtil.obterData(2010, 9, 18); 

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest
					.criarPrescricao(atendimento, dataReferencia, NOME_MICROCOMPUTADOR);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);

			// valida data de referencia
			Assert.assertEquals(
					dataReferencia,
					novaPrescricao.getDtReferencia()
			);

			// testa data de início
			Assert.assertEquals(
					DateUtil.obterData(2010, 9, 18, 20, 0),
					novaPrescricao.getDthrInicio()
			);

			// testa a data de fim
			Assert.assertEquals(
					DateUtil.obterData(2010, 9, 19, 20, 0),
					novaPrescricao.getDthrFim()
			);

		} catch (BaseException e) {
		}
		
	}

	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) não há nenhuma outra prescrição válida p/ o atendimento 
	 * b) data de referência é uma data futura maior do que o permitido
	 *    (configurado na unidade funcional)
	 *     
	 */
	@Test
	public void criarPrescricaoAdiantadaAlemPermitidoTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 21, 30));
		// Criando unidade funcional com tempo de adiantamento de 4 dias
		AghUnidadesFuncionais uf = obterUnidadeFuncional(20,0); 
		uf.setNroUnidTempoPmeAdiantadas((short)4);
		uf.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);
		AghAtendimentos atendimento = obterAtendimento(uf);


		// Data de referencia 5 dias após data atual
		Date dataReferencia = DateUtil.obterData(2010, 9, 20); 
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarValorSequencialId(seq);
		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest
					.criarPrescricao(atendimento, dataReferencia, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals (
					VerificarPrescricaoON.ExceptionCode.PRESCRICAO_NO_PASSADO, 
					e.getCode()
			);
		}

		
	}
	
	
	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) Há outra prescrição válida p/ o atendimento
	 * 
	 */
	@Test
	public void criarPrescricaoOutrasPrescricoesTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 18, 30));
		
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20, 0)
		);

		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 14));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 14, 20, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 15, 20, 0));

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest
					.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);

		} catch (BaseException e) {
		}

	}
	
	/**
	 * R09 - Cálculo data de início quando há outras prescrições
	 * 
	 * GIVEN
	 * Prescrição anterior com data fim maior do que seria a data início da prescrição a ser criada
	 * 
	 * SHOULD
	 * Nova prescrição criada com data inicio igual a data fim da prescrição anterior
	 * 
	 */
	@Test
	public void criarPrescricaoPrescricaoAnteriorSobrepostaTest() {

		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(19,0)
		);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 15));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 15, 20, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 16, 20, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarValorSequencialId(seq);
		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 9, 16), 
					NOME_MICROCOMPUTADOR
			);
			
			Assert.assertEquals(
					outraPrescricao.getDthrFim(),
					novaPrescricao.getDthrInicio()
			);
			
		} catch (BaseException e) {
		}

		
		
	}
	
	
	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) atendimento é de tipo que não permite prescrição 
	 * 
	 */
	@Test
	public void criarPrescricaoAtendimentoNaoPermite() {

		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20, 0)
		);
		atendimento.setOrigem(DominioOrigemAtendimento.D);
		//Necessário que seja uma origem que não permira prescricao

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO,
					e.getCode()
			);
		}

		
	}

	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) Prescição é criada fora do período de vigência do atendimento  
	 * 
	 */
	@Test
	public void criarPrescricaoForaPeriodoVigencia() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		
		AghUnidadesFuncionais uf = obterUnidadeFuncional(20, 0);
		uf.setNroUnidTempoPmeAdiantadas((short)7);
		uf.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);

		AghAtendimentos atendimento = obterAtendimento(uf);
		atendimento.setDthrInicio(DateUtil.obterData(2010, 9, 20, 19, 0));
		atendimento.setDthrFim(DateUtil.obterData(2010, 9, 20, 19, 0));
		Date dataReferencia = DateUtil.obterData(2010, 9, 20);

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest.criarPrescricao(atendimento, dataReferencia, NOME_MICROCOMPUTADOR);
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.PRESCRICAO_NO_PASSADO,
					e.getCode()
			);
		}

		
	}
	
	/**
	 * testa a criação de uma nova prescrição quando:
	 * 
	 * a) Atendimento não está em andamento  
	 * 
	 */
	@Test
	public void criarPrescricaoAtendimentoEncerrado() {

		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20, 0)
		);
		atendimento.setIndPacAtendimento(DominioPacAtendimento.N);

		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();

		considerarPrescricoes(prescricoesDoAtendimento);
		expectPesquisarAtendimentoParaPrescricaoMedica();
		try {
			systemUnderTest.criarPrescricao(atendimento, null, NOME_MICROCOMPUTADOR);
			Assert.fail();
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO,
					e.getCode()
			);
		}

		
	}
	
	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=58 
	 * Não deve poder criar prescrições futuras sobrepostas (com mesma data de referência, início e fim)
	 *  
	 */
	@Test
	public void criarPrescricaoFuturaMesmoPeriodoTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20,0)
		);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 15));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 15, 20, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 16, 20, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			
			systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 9, 15), 
					NOME_MICROCOMPUTADOR
			);
			
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.PRESCRICOES_SOBREPOSTAS, 
					e.getCode()
			);
		}

		
		
	}
	
	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=60 
	 * Quando uma prescrição termina num horário anterior ao que deveria começar a próxima
	 * É criado uma prescrição apenas para preencher o horário entre o fim da anterior e o 
	 * que seria o início da próxima
	 * 
	 */
	@Test
	public void criarPrescricaoHrioValidadeMaiorTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20,0)
		);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 16));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 16, 14, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 17, 14, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 9, 17), 
					NOME_MICROCOMPUTADOR
			);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);
			

		} catch (BaseException e) {
		}

		
		
	}
	
	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=60 
	 * Quando uma prescrição termina num horário posterior ao que deveria começar a próxima
	 * A prescrição é criada iniciando no horário de fim da anterior 
	 * 
	 */
	@Test
	public void criarPrescricaoHrioValidadeMenorTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20,0)
		);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 16));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 16, 22, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 17, 22, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 9, 17), 
					NOME_MICROCOMPUTADOR
			);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);
			
		} catch (BaseException e) {
		}

		
		
	}
	
	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=60 
	 * Criação de prescrição não consecutiva, permitido
	 * 
	 */
	@Test
	public void criarPrescricaoNaoConsecutivaPermitidoTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		
		AghAtendimentos atendimento = obterAtendimento(
				obterUnidadeFuncional(20,0)
		);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 16));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 16, 20, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 17, 20, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 9, 18), 
					NOME_MICROCOMPUTADOR
			);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);
			

		} catch (BaseException e) {
		}

		
		
	}

	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=60 
	 * Criação de prescrição não consecutiva, não permitido
	 * 
	 */
	@Test
	public void criarPrescricaoNaoConsecutivaNaoPermitidoTest() {

		setDataAtual(DateUtil.obterData(2010, 9, 15, 17, 30));
		
		AghUnidadesFuncionais uf = obterUnidadeFuncional(20,0);
		AghAtendimentos atendimento = obterAtendimento(uf);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 9, 16));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 9, 16, 20, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 9, 17, 20, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);

		try {
			systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 9, 18), 
					NOME_MICROCOMPUTADOR
			);
			
		} catch (BaseException e) {
			Assert.assertEquals(
					VerificarPrescricaoON.ExceptionCode.PRESCRICAO_CONSECUTIVA_NAO_ENCONTRADA, 
					e.getCode()
			);
		}

		
		
	}

	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=202 
	 * Testa o seguinte caso:
	 * Existe uma prescrição ref 24/11, início 24/11 14:00 e fim 24/11 15:00.
	 * Criar nova com ref 24/11, com data de troca 8:00.
	 * O período normal seria início 24/11 08:00 (antes do início da existente) e 
	 * fim 25/11 8:00 (após o fim da existente)
	 * Neste caso, deve puxar a data de início para o fim da existente, ficando
	 * Ref: 24/11. Inicio: 24/11 15:00. Fim: 25/11 8:00.
	 */
	@Test
	public void criarPrescricaoAnteriorContidaTest() {

		setDataAtual(DateUtil.obterData(2010, 11, 16, 17, 15));
		
		AghUnidadesFuncionais uf = obterUnidadeFuncional(8,0);
		uf.setNroUnidTempoPmeAdiantadas((short)10);
		uf.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);

		AghAtendimentos atendimento = obterAtendimento(uf);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 11, 24));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 11, 24, 14, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 11, 24, 15, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);
		

		try {
			
			
			
			MpmPrescricaoMedica novaPrescricao = (MpmPrescricaoMedica)systemUnderTest.criarPrescricao(
					atendimento,
					DateUtil.obterData(2010, 11, 24), 
					NOME_MICROCOMPUTADOR
			);
			
			validarPosCondicoesNovaPrescricao(atendimento, novaPrescricao);
			
			// valida data de referencia
			Assert.assertEquals(
					DateUtil.obterData(2010, 11, 24),
					novaPrescricao.getDtReferencia()
			);

			// testa data de início
			Assert.assertEquals(
					DateUtil.obterData(2010, 11, 24, 15, 0),
					novaPrescricao.getDthrInicio()
			);

		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Se não existe prescrições, a data de referência sugerida para a próxima prescição
	 * é o dia anteror ao atual se for antes do horário de validade.  
	 */
	@Test
	public void obterDataReferenciaProximaPrescricaoDataAnteriorTest() {
		setDataAtual(DateUtil.obterData(2010, 11, 16, 11, 15));
		
		AghUnidadesFuncionais uf = obterUnidadeFuncional(14,0);

		AghAtendimentos atendimento = obterAtendimento(uf);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		considerarPrescricoes(prescricoesDoAtendimento);
		
		try {
			Date dataReferencia = systemUnderTest.obterDataReferenciaProximaPrescricao(atendimento);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * Se não existe prescrições, a data de referência sugerida para a próxima prescição
	 * é o dia atual se for após do horário de validade.  
	 */
	@Test
	public void obterDataReferenciaProximaPrescricaoDataPosteriorTest() {
		setDataAtual(DateUtil.obterData(2010, 11, 16, 15, 15));
		
		AghUnidadesFuncionais uf = obterUnidadeFuncional(14,0);

		AghAtendimentos atendimento = obterAtendimento(uf);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		considerarPrescricoes(prescricoesDoAtendimento);
		
		try {
			Date dataReferencia = systemUnderTest.obterDataReferenciaProximaPrescricao(atendimento);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * Teste criado em função do bug http://qos-aghu.mec.gov.br/mantis/view.php?id=461 
	 * Existindo prescrições, a data de referência sugerida para a próxima prescição
	 * Deve ser após a data da última prescrição, mesmo que exista a possibilidade
	 * de criar prescrições anteriores (existe algum período sem prescrição entre a data 
	 * atual e a última prescrição.  
	 */
	@Test
	public void obterDataReferenciaProximaPrescricaoTest() {
		setDataAtual(DateUtil.obterData(2010, 11, 16, 17, 15));
		
		AghUnidadesFuncionais uf = obterUnidadeFuncional(14,0);
		uf.setNroUnidTempoPmeAdiantadas((short)10);
		uf.setIndUnidTempoPmeAdiantada(DominioUnidTempo.D);

		AghAtendimentos atendimento = obterAtendimento(uf);
		
		List<MpmPrescricaoMedica> prescricoesDoAtendimento = new ArrayList<MpmPrescricaoMedica>();
		MpmPrescricaoMedica outraPrescricao = new MpmPrescricaoMedica();
		outraPrescricao.setAtendimento(atendimento);
		outraPrescricao.setDtReferencia(DateUtil.obterData(2010, 11, 20));
		outraPrescricao.setDthrInicio(DateUtil.obterData(2010, 11, 20, 14, 0));
		outraPrescricao.setDthrFim(DateUtil.obterData(2010, 11, 21, 14, 0));
		prescricoesDoAtendimento.add(outraPrescricao);

		considerarPrescricoes(prescricoesDoAtendimento);
		try {
			Date dataReferencia = systemUnderTest.obterDataReferenciaProximaPrescricao(atendimento);
			Assert.assertEquals(DateUtil.obterData(2010,11,21), dataReferencia);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getLocalizedMessage());
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

	private void expectPesquisarAtendimentoParaPrescricaoMedica() {
//		TODO migrar futuramente para nova arquitetura.
//		mockingContext.checking(new Expectations() {
//			{
//				allowing(mockedAmbulatorioFacade)
//						.pesquisarAtendimentoParaPrescricaoMedica(
//								with(any(Integer.class)),
//								with(any(Integer.class)));
//				will(returnValue(new ArrayList<AghAtendimentos>()));
//			}
//		});
	}
	
}

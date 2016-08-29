package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicasId;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelItemComponenteSangSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSolHemoterapicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SolicitacaoHemoterapicaON.class)
public class SolicitacaoHemoterapicaONTest extends AGHUBaseUnitTest<SolicitacaoHemoterapicaON>{
	
	@Mock
	private MpmTipoFrequenciaAprazamentoDAO mockedMpmTipoFrequenciaAprazamentoDAO;
	@Mock
	private MpmCidAtendimentoDAO mockedMpmCidAtendimentoDAO;
	@Mock
	private PrescricaoMedicaON mockedPrescricaoMedicaON;
	@Mock
	private LaudoProcedimentoSusRN mockedLaudoProcedimentoSusRN; 
	@Mock
	private IInternacaoFacade mockedInternacaoFacade;
	@Mock
	private IBancoDeSangueFacade mockedBancodeSangueFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	/**
	 * 
	 * @param statusItem
	 */
	private void considerarStatusItem(final EnumStatusItem statusItem){
		try {
			Mockito.when(mockedPrescricaoMedicaON.buscarStatusItem(Mockito.any(ItemPrescricaoMedica.class), Mockito.any(Date.class))).
			thenReturn(statusItem);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	
	private void verificarReimpressao(){
		Mockito.when(mockedPrescricaoMedicaON.verificarPrimeiraImpressao(Mockito.any(RapServidores.class))).
		thenReturn(true);
	}
	
	
	/**
	 * 
	 * @param solicitacoesHemoterapicas
	 */
	private void considerarSolicitacoesHemoterapicas(final List<AbsSolicitacoesHemoterapicas> solicitacoesHemoterapicas) {
		Mockito.when(mockedBancodeSangueFacade.pesquisarSolicitacoesHemoterapicasRelatorio(Mockito.any(MpmPrescricaoMedica.class))).
		thenReturn(solicitacoesHemoterapicas);
	}
	
	private void consideraConselhoProfissionalServidorVO(final BuscaConselhoProfissionalServidorVO conselhoProfissionalServidorVO) throws BaseException {
		try {
			Mockito.when(mockedLaudoProcedimentoSusRN.buscaConselhoProfissionalServidorVO(Mockito.any(RapServidores.class))).
			thenReturn(conselhoProfissionalServidorVO);
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}
	
	/**
	 * 
	 * @param componentesSanguineos
	 */
	private void consideraItensComponentesSanguineos(final List<AbsItensSolHemoterapicas> itensComponentesSanguineos) {
		Mockito.when(mockedBancodeSangueFacade.pesquisarItensHemoterapiaComponentesSanguineos(Mockito.anyInt(), Mockito.anyInt())).
		thenReturn(itensComponentesSanguineos);
	}

	/**
	 * 
	 * @param procedimentosHemoterapicos
	 */
	private void consideraProcedimentosHemoterapicos(final List<AbsItensSolHemoterapicas> procedimentosHemoterapicos) {
		Mockito.when(mockedBancodeSangueFacade.pesquisarItensHemoterapiaProcedimentos(Mockito.anyInt(), Mockito.anyInt())).
		thenReturn(procedimentosHemoterapicos);
	}
	
	private void consideraCidsAtendimento(final List<MpmCidAtendimento> cidsAtendimento) {
		Mockito.when(mockedMpmCidAtendimentoDAO.listar(Mockito.any(AghAtendimentos.class))).
		thenReturn(cidsAtendimento);
	}
	
	private void consideraJustificativas(final List<AbsItemSolicitacaoHemoterapicaJustificativa> justificativas) {
		Mockito.when(mockedBancodeSangueFacade.pesquisarJustificativasItemHemoterapia(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).
		thenReturn(justificativas);
	}

	
	/**
	 * Método que valida a atribuição dos valores da prescrição médica que irão constar no cabeçalho
	 */
	@Test
	public void validarAtribuirValoresPrescricao() {
		final Integer prontuario = 123456;
		final String nome = "Juca Batista";
		final Integer seqPrescricao = 1111111;
		Date dataAtual = new Date();
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();

		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setSeq(1111111);
		prescricaoMedica.setId(id);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(prontuario);
		paciente.setNome(nome);
		paciente.setSexo(DominioSexo.M);
		paciente.setDtNascimento(dataAtual);
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setPaciente(paciente);
		prescricaoMedica.setAtendimento(atendimento);

		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.atribuirValoresPrescricao(prescricaoMedica, relSolHemoterapicaVO);
		Assert.assertEquals(relSolHemoterapicaVO.getPacienteProntuario(),prontuario);
		Assert.assertTrue(relSolHemoterapicaVO.getPacienteNome().equalsIgnoreCase(nome));
		Assert.assertEquals(relSolHemoterapicaVO.getPrescricao(),seqPrescricao);
		Assert.assertTrue(relSolHemoterapicaVO.getPacienteSexo().equals("M"));
		Assert.assertTrue(relSolHemoterapicaVO.getDataNascimento().compareTo(dataAtual) == 0);
	}
	
	
	@Test
	public void validarPopularLocalizacaoPacienteLeito() {
		final String leitoID = "1101C";
		final String labelLocalizacao = "Leito";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		AghAtendimentos atendimento = new AghAtendimentos();
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID(leitoID);
		
		atendimento.setLeito(leito);
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.popularLocalizacaoPaciente(relSolHemoterapicaVO, hemoterapia);
		Assert.assertTrue(relSolHemoterapicaVO.getLabelLocalizacao().equalsIgnoreCase(labelLocalizacao));
		Assert.assertTrue(relSolHemoterapicaVO.getLocalizacao().equalsIgnoreCase(leitoID));
	}
	
	
	@Test
	public void validarPopularLocalizacaoPacienteQuarto() {
		final Short numero = 1101;
		final String labelLocalizacao = "Quarto";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		AghAtendimentos atendimento = new AghAtendimentos();
		AinQuartos quarto = new AinQuartos();
		quarto.setNumero(numero);
		quarto.setDescricao(numero.toString());
		
		atendimento.setQuarto(quarto);
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.popularLocalizacaoPaciente(relSolHemoterapicaVO, hemoterapia);
		Assert.assertTrue(relSolHemoterapicaVO.getLabelLocalizacao().equalsIgnoreCase(labelLocalizacao));
		Assert.assertTrue(relSolHemoterapicaVO.getLocalizacao().equalsIgnoreCase(numero.toString()));	
	}
	
	
	
	@Test
	public void validarPopularLocalizacaoPacienteUnidadeFuncionalSigla() {
		final String sigla = "CMED";
		final String labelLocalizacao = "Unidade";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSigla(sigla);
		
		atendimento.setUnidadeFuncional(unidade);
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.popularLocalizacaoPaciente(relSolHemoterapicaVO, hemoterapia);
		Assert.assertTrue(relSolHemoterapicaVO.getLabelLocalizacao().equalsIgnoreCase(labelLocalizacao));
		Assert.assertTrue(relSolHemoterapicaVO.getLocalizacao().equalsIgnoreCase(sigla));
		
	}
	
	
	@Test
	public void validarPopularLocalizacaoPacienteUnidadeFuncionalAndar() {
		final String andar = "1";
		final AghAla ala = AghAla.N;
		final String labelLocalizacao = "Unidade";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		AghAtendimentos atendimento = new AghAtendimentos();
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setAndar(andar);
		unidade.setIndAla(ala);
		
		atendimento.setUnidadeFuncional(unidade);
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.popularLocalizacaoPaciente(relSolHemoterapicaVO, hemoterapia);
		Assert.assertTrue(relSolHemoterapicaVO.getLabelLocalizacao().equalsIgnoreCase(labelLocalizacao));
		Assert.assertTrue(relSolHemoterapicaVO.getLocalizacao().equalsIgnoreCase(andar.toString() + " " + ala.toString()));
		
	}
	

	@Test
	public void validarBuscarConvenioPacienteInternacao() {
		final String descConvenio = "SUS";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		AghAtendimentos atendimento = new AghAtendimentos();
		AinInternacao internacao = new AinInternacao();
		
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setDescricao(descConvenio);
		internacao.setConvenioSaude(convenioSaude);

		atendimento.setInternacao(internacao);
		
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		SolicitacaoHemoterapicaON spy = PowerMockito.spy(systemUnderTest);
		try {
			 /*PowerMockito.doAnswer(new Answer<Void>() {
			        @Override
			        public Void answer(InvocationOnMock invocation) {
			        	System.out.println("MÉTODO CHAMADO");
			            return null;
			        }
			    }).when(spy).refresh(Mockito.any(AinInternacao.class));;
			*/
			PowerMockito.doNothing().when(spy, "refresh", Mockito.any(AinInternacao.class));
			
			//PowerMockito.doNothing().when(spy).refresh(Mockito.any(BaseEntity.class));
		//	PowerMockito.doNothing().when(spy).refresh(Mockito.any(AinInternacao.class));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
		String retorno = spy.buscarConvenioPaciente(hemoterapia);
		Assert.assertTrue(retorno.equalsIgnoreCase(descConvenio));
		
	}
	
	
	//@Test
	public void validarBuscarConvenioPacienteAtendimentoUrgencia() {
		final String descConvenio = "UNIMED";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		AghAtendimentos atendimento = new AghAtendimentos();
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setDescricao(descConvenio);
		atendimentoUrgencia.setConvenioSaude(convenioSaude);

		atendimento.setAtendimentoUrgencia(atendimentoUrgencia);
		
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		String retorno = systemUnderTest.buscarConvenioPaciente(hemoterapia);
		Assert.assertTrue(retorno.equalsIgnoreCase(descConvenio));
		
	}
	
	
	//@Test
	public void validarBuscarConvenioPacienteHospitalDia() {
		final String descConvenio = "IPE";
		
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();

		AghAtendimentos atendimento = new AghAtendimentos();
		AhdHospitaisDia hospitalDia = new AhdHospitaisDia();
		
		FatConvenioSaude convenioSaude = new FatConvenioSaude();
		convenioSaude.setDescricao(descConvenio);
		hospitalDia.setConvenioSaude(convenioSaude);

		atendimento.setHospitalDia(hospitalDia);
		
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		prescricaoMedica.setAtendimento(atendimento);
		hemoterapia.setPrescricaoMedica(prescricaoMedica);
		
		String retorno = systemUnderTest.buscarConvenioPaciente(hemoterapia);
		Assert.assertTrue(retorno.equalsIgnoreCase(descConvenio));
		
	}
	
	@Test
	public void validarObterAprazamento() {
		String sintaxe = "de #/# horas";
		String frequencia = "1";
		String aprazamento = "de 1/1 horas";
		String retorno = systemUnderTest.obterAprazamento(sintaxe, frequencia);
		Assert.assertTrue(retorno.equalsIgnoreCase(aprazamento));
		
	}

	
	@Test
	public void validarAtribuirQuantidadeItemComponenteSanguineoUn() {
		Byte quantidadeUnidades = 5;
		String quantidadeComparacao = "5 un";
		AbsItensSolHemoterapicas itemCompSanguineo = new AbsItensSolHemoterapicas();
		itemCompSanguineo.setQtdeUnidades(quantidadeUnidades);
		
		RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO = new RelItemComponenteSangSolHemoterapicaVO();
		systemUnderTest.atribuirQuantidadeItemComponenteSanguineo(itemCompSanguineo, itemGrupoSangVO);
		Assert.assertTrue(itemGrupoSangVO.getQuantidade().equalsIgnoreCase(quantidadeComparacao));
		
	}
	
	
	@Test
	public void validarAtribuirQuantidadeItemComponenteSanguineoMl() {
		Short quantidadeMl = 35;
		String quantidadeComparacao = "35 ml";
		AbsItensSolHemoterapicas itemCompSanguineo = new AbsItensSolHemoterapicas();
		itemCompSanguineo.setQtdeMl(quantidadeMl);
		
		RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO = new RelItemComponenteSangSolHemoterapicaVO();
		systemUnderTest.atribuirQuantidadeItemComponenteSanguineo(itemCompSanguineo, itemGrupoSangVO);
		Assert.assertTrue(itemGrupoSangVO.getQuantidade().equalsIgnoreCase(quantidadeComparacao));
		
	}
	
	
	@Test
	public void validarAtribuirAprazamentoItemComponenteSanguineoSemSintaxe() {
		final String descricao = "VEZES AO DIA";
		AbsItensSolHemoterapicas itemCompSanguineo = new AbsItensSolHemoterapicas();
		MpmTipoFrequenciaAprazamento tipoFreqAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFreqAprazamento.setDescricao(descricao);
		itemCompSanguineo.setTipoFreqAprazamento(tipoFreqAprazamento);
		
		RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO = new RelItemComponenteSangSolHemoterapicaVO();
		systemUnderTest.atribuirAprazamentoItemComponenteSanguineo(itemCompSanguineo, itemGrupoSangVO);
		Assert.assertTrue(itemGrupoSangVO.getAprazamento().equalsIgnoreCase(descricao));
		
	}
	
	
	@Test
	public void validarAtribuirQuantidadeAplicacoes(){
		Short qtdeAplicacoes = 3;
		AbsItensSolHemoterapicas itemCompSanguineo = new AbsItensSolHemoterapicas();
		itemCompSanguineo.setQtdeAplicacoes(qtdeAplicacoes);

		RelItemComponenteSangSolHemoterapicaVO itemGrupoSangVO = new RelItemComponenteSangSolHemoterapicaVO();
		systemUnderTest.atribuirQuantidadeAplicacoes(itemCompSanguineo, itemGrupoSangVO);
		Assert.assertTrue(itemGrupoSangVO.getQuantidadeAplicacoes().equalsIgnoreCase(qtdeAplicacoes.toString()));
		
	}
	
	
	@Test
	public void validarAtribuirSituacaoColetaP(){
		final String situacaoAmostra = "Será coletada pela coleta";
		DominioSituacaoColeta situacaoColeta = DominioSituacaoColeta.P;
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();
		hemoterapia.setIndSituacaoColeta(situacaoColeta);
		hemoterapia.setIndResponsavelColeta(DominioResponsavelColeta.C);

		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.atribuirSituacaoColeta(hemoterapia, relSolHemoterapicaVO);
		Assert.assertTrue(relSolHemoterapicaVO.getSituacaoAmostra().equalsIgnoreCase(situacaoAmostra));
		
	}
	
	
	@Test
	public void validarAtribuirSituacaoColetaE(){
		final String situacaoAmostra = "Será coletada pelo solicitante";
		DominioSituacaoColeta situacaoColeta = DominioSituacaoColeta.E;
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();
		hemoterapia.setIndSituacaoColeta(situacaoColeta);
		hemoterapia.setIndResponsavelColeta(DominioResponsavelColeta.S);

		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.atribuirSituacaoColeta(hemoterapia, relSolHemoterapicaVO);
		Assert.assertTrue(relSolHemoterapicaVO.getSituacaoAmostra().equalsIgnoreCase(situacaoAmostra));
		
	}
	
	@Test
	public void validarAtribuirSituacaoColetaD(){
		final String situacaoAmostra = "Existe amostra válida no Banco de Sangue";
		DominioSituacaoColeta situacaoColeta = DominioSituacaoColeta.D;
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();
		hemoterapia.setIndSituacaoColeta(situacaoColeta);
		hemoterapia.setIndResponsavelColeta(DominioResponsavelColeta.N);

		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.atribuirSituacaoColeta(hemoterapia, relSolHemoterapicaVO);
		Assert.assertTrue(relSolHemoterapicaVO.getSituacaoAmostra().equalsIgnoreCase(situacaoAmostra));
		
	}
	
	
	@Test
	public void validarVerificarIndUrgente(){
		final String urgente = "Urgente";
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();
		hemoterapia.setIndUrgente(true);

		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.verificarIndUrgente(hemoterapia, relSolHemoterapicaVO);
		Assert.assertTrue(relSolHemoterapicaVO.getUrgente().equalsIgnoreCase(urgente));
		
	}
	
	
	@Test
	public void validarVerificarAtribuirObservacao(){
		final String observacao = "teste";
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();
		hemoterapia.setObservacao(observacao);

		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		systemUnderTest.verificarAtribuirObservacao(hemoterapia, relSolHemoterapicaVO);
		Assert.assertTrue(relSolHemoterapicaVO.getObservacao().equalsIgnoreCase(observacao));
		
	}
	
	//@Test
	public void validarPesquisarRelSolHemoterapicaVOs() throws BaseException {
		
		// Dados de entrada
		final Integer atendimentoSeq = 483;
		final Integer solicitacaoHemoterapicaSeq = 192;
		final Integer prescricaoMedicaSeq = 823;
		final Integer prontuario = 987654321;
		final String leitoId = "H100";
		final String nomeMedico = "Julius Hibbert";
		final String siglaConselho = "TESTMED";
		final String numeroConselho = "2345678";
		
		// Resultados Esperados
		final String tipo = "**  SOLICITAÇÃO  **";
		
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(prontuario);
		paciente.setSexo(DominioSexo.M);
		paciente.setDtNascimento(DateUtil.obterData(1956, 3, 1));
		
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID(leitoId);
		
		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setPaciente(paciente);
		atendimento.setLeito(leito);
		
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		prescricaoMedica.setId(
				new MpmPrescricaoMedicaId(atendimentoSeq, prescricaoMedicaSeq)
		);
		prescricaoMedica.setAtendimento(atendimento);
		
		List<AbsSolicitacoesHemoterapicas> solicitacoesHemoterapicas = new ArrayList<AbsSolicitacoesHemoterapicas>();
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		solicitacaoHemoterapica.setId(
				new AbsSolicitacoesHemoterapicasId(atendimentoSeq, solicitacaoHemoterapicaSeq)
		);
		solicitacaoHemoterapica.setPrescricaoMedica(prescricaoMedica);
		solicitacoesHemoterapicas.add(solicitacaoHemoterapica);
		considerarSolicitacoesHemoterapicas(solicitacoesHemoterapicas);
		
		BuscaConselhoProfissionalServidorVO conselhoProfissionalServidorVO = new BuscaConselhoProfissionalServidorVO();
		conselhoProfissionalServidorVO.setNome(nomeMedico);
		conselhoProfissionalServidorVO.setSiglaConselho(siglaConselho);
		conselhoProfissionalServidorVO.setNumeroRegistroConselho(numeroConselho);
		
		consideraConselhoProfissionalServidorVO(conselhoProfissionalServidorVO);
		
		consideraItensComponentesSanguineos(new ArrayList<AbsItensSolHemoterapicas>());
		
		List<AbsItensSolHemoterapicas> procedimentosHemoterapicos = new ArrayList<AbsItensSolHemoterapicas>();
		consideraProcedimentosHemoterapicos(procedimentosHemoterapicos);
		
		List<MpmCidAtendimento> cidsAtendimento = new ArrayList<MpmCidAtendimento>(); 
		consideraCidsAtendimento(cidsAtendimento);
		
		EnumStatusItem statusItem = EnumStatusItem.INCLUIDO;
		considerarStatusItem(statusItem);
		
		verificarReimpressao();
		
		try {
			EnumTipoImpressao tipoImpressao = EnumTipoImpressao.IMPRESSAO;
			RapServidores servidorValida = new RapServidores(new RapServidoresId(324234, Short.valueOf("342424")));
			List<RelSolHemoterapicaVO> relSolHemoterapicaVO = 
				systemUnderTest.pesquisarRelSolHemoterapicaVOs(prescricaoMedica, tipoImpressao,servidorValida, new Date());
			
			Assert.assertEquals(1, relSolHemoterapicaVO.size());
			RelSolHemoterapicaVO vo1 = relSolHemoterapicaVO.get(0);
			Assert.assertEquals(siglaConselho+":", vo1.getSiglaConselho());
			Assert.assertEquals(numeroConselho, vo1.getNumeroRegistroConselho());
			Assert.assertEquals(nomeMedico, vo1.getSolicitante());
			Assert.assertEquals(tipo, vo1.getTipo());
			
		} catch (BaseException e) {
			Assert.fail(e.getLocalizedMessage());
		}
		
	}
	
	@Test
	public void validarPesquisarItensComponentesSanguineosRel() {
		
		// Dados de entrada
		final Integer atendimentoSeq = 483;
		final Integer solicitacaoHemoterapicaSeq = 192;
		final Integer prescricaoMedicaSeq = 823;
		final Short itemComponenteSanguineoSeq = 91;
		final String descricao1 = "Hemácias";
		final Boolean irradiado1 = true;
		final Boolean filtrado1 = false;
		final Boolean lavado1 = false;
		final Boolean aferese1 = false;
		final Short qtdeAplicacoes1 = 3;
		
		// Resultados Esperados
		final String descricaoFormatada1 = "Hemácias Irradiado,";
		
		AghAtendimentos atendimento = new AghAtendimentos();
		
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		prescricaoMedica.setId(
				new MpmPrescricaoMedicaId(atendimentoSeq, prescricaoMedicaSeq)
		);
		prescricaoMedica.setAtendimento(atendimento);

		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = new AbsSolicitacoesHemoterapicas();
		solicitacaoHemoterapica.setId(
				new AbsSolicitacoesHemoterapicasId(atendimentoSeq, solicitacaoHemoterapicaSeq)
		);
		solicitacaoHemoterapica.setPrescricaoMedica(prescricaoMedica);
		
		List<AbsItensSolHemoterapicas> itensComponentesSanguineos = new ArrayList<AbsItensSolHemoterapicas>();
		AbsItensSolHemoterapicas itemComponenteSanguineo = new AbsItensSolHemoterapicas();
		AbsComponenteSanguineo componenteSanguineo = new AbsComponenteSanguineo();
		componenteSanguineo.setDescricao(descricao1);
		itemComponenteSanguineo.setId(new AbsItensSolHemoterapicasId(
				atendimentoSeq, solicitacaoHemoterapicaSeq, itemComponenteSanguineoSeq 
		));		
		itemComponenteSanguineo.setIndIrradiado(irradiado1);
		itemComponenteSanguineo.setIndFiltrado(filtrado1);
		itemComponenteSanguineo.setIndLavado(lavado1);
		itemComponenteSanguineo.setIndAferese(aferese1);
		itemComponenteSanguineo.setQtdeAplicacoes(qtdeAplicacoes1);
		itemComponenteSanguineo.setComponenteSanguineo(componenteSanguineo);
		itensComponentesSanguineos.add(itemComponenteSanguineo);
		consideraItensComponentesSanguineos(itensComponentesSanguineos);
		
		List<AbsItemSolicitacaoHemoterapicaJustificativa> justificativas;
		justificativas = new ArrayList<AbsItemSolicitacaoHemoterapicaJustificativa>();
		consideraJustificativas(justificativas);
		
		List<RelItemComponenteSangSolHemoterapicaVO> componentesSanguineosVO = 
			systemUnderTest.pesquisarItensComponentesSanguineosRel(solicitacaoHemoterapica);
		
		Assert.assertEquals(1, componentesSanguineosVO.size());
		RelItemComponenteSangSolHemoterapicaVO cs1 = componentesSanguineosVO.get(0);
		
		Assert.assertEquals(descricaoFormatada1, cs1.getDescricao());
		Assert.assertEquals(0, cs1.getListaJustificativas().size());
		Assert.assertEquals(qtdeAplicacoes1.toString(), cs1.getQuantidadeAplicacoes());
		
	}
	
	@Test
	public void validarProvidenciarReimpressaoSolicitacao(){
		
		EnumTipoImpressao tipoImpressao = EnumTipoImpressao.IMPRESSAO;
		EnumStatusItem statusItem = EnumStatusItem.INCLUIDO;
		RelSolHemoterapicaVO relSolHemoterapicaVO = new RelSolHemoterapicaVO();
		List<RelSolHemoterapicaVO> listaRelSolHemoterapicaVO = new ArrayList<RelSolHemoterapicaVO>();
		AbsSolicitacoesHemoterapicas hemoterapia = new AbsSolicitacoesHemoterapicas();
		systemUnderTest.providenciarReimpressao(tipoImpressao, statusItem, relSolHemoterapicaVO, listaRelSolHemoterapicaVO, hemoterapia);
		
		Assert.assertTrue(listaRelSolHemoterapicaVO.size() == 1);
		Assert.assertTrue(relSolHemoterapicaVO.getTipo().equals("**  SOLICITAÇÃO  **"));
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
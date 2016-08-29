package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghNodoPol;
import br.gov.mec.aghu.model.AipOrigemDocGEDs;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MptCidTratTerapeutico;
import br.gov.mec.aghu.model.MptControleFreqSessao;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidade;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidadeHist;
import br.gov.mec.aghu.model.VAipPolMdtos;
import br.gov.mec.aghu.model.VAipPolMdtosAghuHist;
import br.gov.mec.aghu.paciente.prontuario.vo.InformacoesPerinataisVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolImpressaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtosAnestesicosPolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.DescricaoCirurgiaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistObstetricaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NotasPolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PdtDescricaoProcedimentoCirurgiaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosImagemPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelExameFisicoRecemNascidoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioAltaAtendEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioTransferenciaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioAssistenciaPartoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioSessoesQuimioVO;
import br.gov.mec.aghu.paciente.vo.ImpressaoPIM2VO;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaVO;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoMedicaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;


public interface IProntuarioOnlineFacade extends Serializable {

	/**
	 * @param tipo
	 * @param sequence
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#buscaDetalhes(java.lang.String, java.lang.Integer)
	 */
	
	public InternacaoVO buscaDetalhes(String tipo, Integer sequence)
			throws ApplicationBusinessException;

	/**
	 * @param numeroProntuario
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#pesquisaInternacoesCount(java.lang.Integer)
	 */
	public Long pesquisaInternacoesCount(Integer numeroProntuario)
			throws ApplicationBusinessException;

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param numeroProntuario
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#pesquisaInternacoes(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Integer)
	 */
	
	public List<InternacaoVO> pesquisaInternacoes(Integer numeroProntuario) throws ApplicationBusinessException;

	public InternacaoVO obterInternacao(Integer seq, String tipo)
			throws ApplicationBusinessException;

	
	/**
	 * @param internacao
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#montaQuinzenaPrescricaoMedica(br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO, int)
	 */
	public List<SumarioPrescricaoMedicaVO> montaQuinzenaPrescricaoMedica(
			InternacaoVO internacao, int codPaciente)
			throws ApplicationBusinessException;

	/**
	 * @param vo
	 * @param limitaResultado
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#buscaRelSumarioPrescricao(br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoVO, boolean)
	 */
	public List<RelSumarioPrescricaoVO> buscaRelSumarioPrescricao(
			SumarioPrescricaoMedicaVO vo, boolean limitaResultado);

	/**
	 * @param vo
	 * @param limitaResultado
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#buscaRelSumarioPrescricaoEnfermagem(br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO, boolean)
	 */
	public List<RelSumarioPrescricaoEnfermagemVO> buscaRelSumarioPrescricaoEnfermagem(
			SumarioPrescricaoEnfermagemVO vo, boolean limitaResultado);

	/**
	 * @param atdSeq
	 * @param inicio
	 * @param Fim
	 * @throws ApplicationBusinessException  
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#atualizarDataImpSumario(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public void atualizarDataImpSumario(Integer atdSeq, Date inicio, Date Fim, String enderecoHost) throws ApplicationBusinessException;

	/**
	 * @param atdSeq
	 * @param inicio
	 * @param Fim
	 * @throws BaseException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#atualizarDataImpSumarioEnfermagem(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	public void atualizarDataImpSumarioEnfermagem(Integer atdSeq, Date inicio,
			Date Fim) throws BaseException;

	/**
	 * Busca dados para montagem da arvore de Laboratórios/Serviços.
	 * @param {Integer} codigoPaciente
	 * @return 
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultarExamesPolON#buscaArvoreLaboratorioServicosDeExames(java.lang.Integer)
	 */
	public List<VAelPesquisaPolExameUnidade> buscaArvoreLaboratorioServicosDeExames(
			Integer codigoPaciente);
	
	public List<VAelPesquisaPolExameUnidadeHist> buscarArvoreLaboratorioServicosDeExamesHist(Integer codigoPaciente);

	/**
	 * Busca exames liberados por paciente e situação.
	 * 
	 * @param {Integer} codigoPaciente
	 * @param {DominioSituacaoItemSolicitacaoExame} situacaoItemExame
	 * 
	 * @return Lista de objetos do tipo ItemSolicitacaoExamePolVO
	 * @throws ApplicationBusinessException 
	 *  
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultarExamesPolON#buscaExamesPeloCodigoDoPacienteESituacao(java.lang.Integer, java.lang.Short, java.util.Date, br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame)
	 */
	public List<ItemSolicitacaoExamePolVO> buscaExamesPeloCodigoDoPacienteESituacao(
			Integer codigoPaciente, Short unfSeq, Date data,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame,
			Integer gmaSeq) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public List<ItemSolicitacaoExamePolVO> buscaExamesPeloCodigoDoPacienteESituacaoHist(Integer codigoPaciente, Short unfSeq, Date data, DominioSituacaoItemSolicitacaoExame situacaoItemExame, Integer gmaSeq) throws ApplicationBusinessException;


	/**
	 * Busca pdf de um resultado de exame
	 * 
	 * @param {Integer} iseSoeSeq: Seq da solicitacao de exame
	 * @param {Short} iseSeqp: Seq do item solicitacao Exame
	 * 
	 * @return Array de bytes representando o arquivo
	 * @throws BaseException 
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultarExamesPolON#buscarArquivoResultadoExame(java.lang.Integer, java.lang.Short)
	 */
	public ByteArrayOutputStream buscarArquivoResultadoExame(Integer iseSoeSeq,
			Short iseSeqp) throws BaseException;
	
	
	/**
	 * Busca pdf de um resultado de exame hist
	 * 
	 * @param {Integer} iseSoeSeq: Seq da solicitacao de exame hist
	 * @param {Short} iseSeqp: Seq do item solicitacao Exame hist
	 * 
	 * @return Array de bytes representando o arquivo
	 * @throws BaseException 
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultarExamesPolON#buscarArquivoResultadoExameHist(java.lang.Integer, java.lang.Short)
	 */
	public ByteArrayOutputStream buscarArquivoResultadoExameHist(Integer iseSoeSeq,
			Short iseSeqp) throws BaseException;

	Long obterMedicamentosCount(Integer codPaciente, String tipo, Date dataInicio);
	List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio, int firstResult, int maxResults);
	List<Date> obterDataMedicamentos(Integer codPaciente, String tipo, Date dataInicio);

	/**
	 * @param atdSeq
	 * @param descMedicamento
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterQuantidadeMedicamentos(java.lang.Integer, java.lang.String)
	 */
	public Integer obterQuantidadeMedicamentos(Integer atdSeq,
			String descMedicamento);
	
	/**
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.AghNodoPolON#recuperarAghNodoPolPorOrdem()
	 */
	public List<AghNodoPol> recuperarAghNodoPolPorOrdem();

	/**
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaDiagnosticoON#pesquisaDiagnosticosCount(br.gov.mec.aghu.model.AipPacientes)
	 */
	public Integer pesquisaDiagnosticosCount(AipPacientes paciente)
			throws ApplicationBusinessException;

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaDiagnosticoON#pesquisaDiagnosticos(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AipPacientes)
	 */
	public List<MamDiagnostico> pesquisaDiagnosticos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente) throws ApplicationBusinessException;

	/**
	 * Realiza a pesquisa de exames e retorna em ordem cronológica para o prontuário online.
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 15/02/2012
	 */
	public List<ExameOrdemCronologicaVO> pesquisarExamesOrdemCronologica(
			Integer codPaciente) throws ApplicationBusinessException;

	/**
	 * Realiza a pesquisa de exames e retorna em ordem de amostras coletadas para o prontuário online.
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 17/02/2012
	 */
	public List<ExameAmostraColetadaVO> pesquisarExamesAmostrasColetadas(
			Integer codPaciente) throws ApplicationBusinessException;

	public List<ExameAmostraColetadaVO> pesquisarExamesAmostrasColetadasHist(Integer codPaciente) throws ApplicationBusinessException;

	/**
	 * Método que carrega a lista quinzenal de seleção para o relatório
	 * de prescrição enfermagem
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioPrescricaoEnfermagemVO> montaQuinzenaPrescricaoEnfermagem(
			InternacaoVO internacao, Integer codigo)
			throws ApplicationBusinessException;

	public List<AltasAmbulatoriasPolVO> pesquisarAltasAmbulatoriaisPol(
			Integer pacCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws ApplicationBusinessException;

	public RelatorioSumarioTransferenciaVO criarRelatorioSumarioTransferencia(
			AghAtendimentos atendimento, String tipoImpressao,
			Short seqpAltaSumario) throws ApplicationBusinessException;

	// # 12004 - IMPRIME ALTA AMBULATORIAL POL
	public List<AltaAmbulatorialPolImpressaoVO> recuperarAltaAmbuPolImpressaoVO(
			Long seqMamAltaSumario) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public String getEnderecoCompleto() throws ApplicationBusinessException;

	public String getTextoPostoSaude() throws ApplicationBusinessException;

	/**
	 * Método que carrega a lista de notas para o relatório
	 * @param nota
	 * @return
	 * @throws BaseException 
	 */
	public List<NotasPolVO> pesquisarNotasPol(Integer seqNota)
			throws BaseException;

	/**
	 * Método que verifica se a NOTA será impressa
	 * @param nota
	 * @return
	 */
	public Boolean visualizarRelatorio(Integer seqNota);

	public List<MamInterconsultas> pesquisarConsultoriasAmbulatoriais(
			Integer codigoPaciente);

	public List<MamNotaAdicionalEvolucoes> pesquisarNotasPorCodigoPaciente
		(Integer codigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws ApplicationBusinessException;
	
	public Long pesquisarNotasPorCodigoPacienteCount(Integer codigo);

	public MamNotaAdicionalEvolucoes incluirNotaPOL(String textoIncluiNota, AipPacientes paciente)
			throws ApplicationBusinessException;

	public Integer recuperarVersaoDoc(Integer seqNota);

	/**
	 * Método que carrega a lista de descrição cirurgicas para o relatório
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws BaseException 
	 *  
	 */
	public Collection<DescricaoCirurgiaVO> pesquisarDescricaoCirurgicaPol(
			Integer crgSeq, Short seqp, Boolean previa, String contextPath)
			throws ApplicationBusinessException, BaseException;

	public List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOL(
			Integer codigo);

	public MbcExtratoCirurgia buscarMotivoCancelCirurgia(Integer seq);

	public Boolean verificarSeDocumentoDescricaoCirugiaAssinado(
			Integer seqCirurgia) throws ApplicationBusinessException;

	public Integer chamarDocCertifCirurg(Integer seqCirurgia);

	public Boolean validarBotaoAdicionarIncluirNotasPOL()
			throws ApplicationBusinessException;

	public void gravarJustificarExclusaoIncluirNotasPol(
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao) throws ApplicationBusinessException;

	public List<AtosAnestesicosPolVO> pesquisarRelatorioAtosAnestesicos(
			Long seqMbcFichaAnestesia, String vSessao)
			throws ApplicationBusinessException;

	public String mbccGetFarmaco(Integer seqMbcFichaFarmaco);

	public List<AltaSumarioVO> carregarListaSumarioVO(Integer apaAtdSeq)
			throws ApplicationBusinessException;

	public Boolean verificarSeInternacaoVariasTransferencias(Integer apaAtdSeq);

	public Boolean habilitarBotaoDescricaoCirurgiasPol(Integer seq);

	public Boolean habilitarBotaoDocAssinadoCirurgiasPol(Integer seq);

	public Boolean habilitarBotaoAtoAnestesicoCirurgiasPol(Integer seq);

	public Boolean habilitarBotaoExameAnatopatologico(Integer seq)
			throws ApplicationBusinessException;

	public Boolean habilitarBotaoImagemProc(
			ProcedimentosPOLVO registroSelecionado, Integer parametroVerificaImagem);

	public Boolean habilitarBotaoExameAnatopatologicoProc(
			ProcedimentosPOLVO registroSelecionado) throws ApplicationBusinessException;

	public List<ProcedimentosPOLVO> pesquisarProcedimentosPOL(Integer codigo)
			throws ApplicationBusinessException;

	public Boolean verificarSeDocumentoAtoAnestesicoAssinado(Integer seq)
			throws ApplicationBusinessException;

	public Integer chamarDocCertifFicha(Integer seq);

	public Object[] imprimirDescricao(Integer seq) throws ApplicationBusinessException;

	String visualizarNotaAnamneseEMG(Long numRegistro, Integer tipo)
			throws ApplicationBusinessException;
	
	public List<AtendimentosEmergenciaPOLVO> pesquisarAtendimentosEmergenciaPOL(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade);

	public Long pesquisarAtendimentosEmergenciaPOLCount(Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade);

	public List<AtendimentosVO> pesquisarAtendimentosPorProntuario(
			Integer numeroProntuario) throws ApplicationBusinessException;

	public List<PdtDescricao> processaDescricaoComNomeResponsavel(
			List<PdtDescricao> descricaoPdt);

	public List<MbcDescricaoCirurgica> processaDescricaoComNomeResponsavelMbc(
			List<MbcDescricaoCirurgica> descricaoCirurgica);

	public Boolean habilitarBotaoConsObs(HistObstetricaVO registroSelecionado);

	public Boolean habilitarBotaoAdmObs(
			DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, Integer conNumero);

	public Boolean habilitarBotaoExameFisico(DominioOrigemAtendimento tipoOrigemAtendimento,
			Integer mcoGestacoesPacCodigo, Short mcoGestacoesSeqP, DominioOrigemAtendimento ...origensRestritas);

	public Boolean habilitarBotaoSumarioAlta(InformacoesPerinataisVO registroSelecionado);	

	public Boolean habilitarBotaoAtoAnestesico(HistObstetricaVO registroSelecionado);

	public List<NodoPOLVO> obterNodosExpandidos() throws ApplicationBusinessException;
	
//	Integer obterProntuarioSelecionado();
	
	void carregaNosGestacao(NodoPOLVO no, Integer gsoPacCodigo);

	public Boolean habilitarBotaoEvolucaoAnamnese(AtendimentosEmergenciaPOLVO registroSelecionado);

	public Boolean habilitarBotaoConsultoria(AtendimentosEmergenciaPOLVO registroSelecionado);

	public Boolean habilitarBotaoSumarioAlta(AtendimentosEmergenciaPOLVO registroSelecionado);

	public Long listarSessoesFisioterapiaCount(Integer trpSeq);

	public List<MptControleFreqSessao> listarSessoesFisioterapia(Integer trpSeq,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	/**
	 * Valida as permissões para exibição da árvore de prontuário online.<br>
	 * Loga as tentativas de acesso aos prontuários.<BR>
	 * Retorna a lista de pacientes que o usuário tem acesso à árvore, alimentará o parametro listaExcept com os prontuários que não tiver acesso.
	 * @param HashMap de parâmetros
	 * @param listaExcept
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	List<AipPacientes> validarPermissoesPOL(
			AipPacientes paciente, Map<ParametrosPOLEnum, Object> parametros,
			String nomeMicrocomputador, Short processoConsultaPOL) throws ApplicationBusinessException, BaseListException;
	
	public void processarNodosEmergenciaPol(NodoPOLVO emergenciaNodo, Integer codPaciente) throws ApplicationBusinessException;

	public MptCidTratTerapeutico obterCidTratamentoQuimio(
			Integer seqTratamentoTerQuimio);

	String visualizarNotaEvolucaoEMG(Long numRegistro, Integer tipo) throws ApplicationBusinessException;

	public List<AelProjetoPacientes> pesquisarProjetosPesquisaPacientePOL(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo) throws BaseException;

	public Long pesquisarProjetosPesquisaPacientePOLCount(Integer codigo) throws BaseException;
	
	public Object[] getRelatorioProcedimentosCirurgiaPol(Integer seqCirurgia) throws ApplicationBusinessException;

	public String buscarIdade(Date dtNascimento, Date dataCirurgia);

	public List<PdtDescricaoProcedimentoCirurgiaVO> recuperarPdtDescricaoProcedimentoCirurgiaVO(
			Integer seqPdtDescricao) throws ApplicationBusinessException;

	public String obterLocalizacaoPacienteParaRelatorio(String leitoID,	Short numeroQuarto, Short seqUnidadeFuncional);
	
	public Long pesquisarAltasAmbulatoriaisPolCount(Integer pacCodigo);

	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer numeroProntuario, String atendimentos);

	public Long pesquisarAtendimentoAmbPorPacCodigoCount(
			Integer numeroProntuario, String atendimentos);
	
	public boolean verificarPermissaoVisualizarNotasAdicionais(Integer soeSeq, Short seqp);
	
	void verificarListaExamesPossuemImagem(Map<Integer, Vector<Short>> itensSelecionados) throws BaseException;
	
	/**
	 * @param paciente
	 * @param tipo
	 * @param dataInicio
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterMedicamentos(br.gov.mec.aghu.model.AipPacientes, java.lang.String, java.util.Date)
	 */
	public List<VAipPolMdtosAghuHist> obterMedicamentosHist(Integer codPaciente,
			String tipo, Date dataInicio);

	/**
	 * @param paciente
	 * @param tipo
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterMedicamentosCount(br.gov.mec.aghu.model.AipPacientes, java.lang.String)
	 */
	public Long obterMedicamentosHistCount(Integer codPaciente, String tipo);
	
	public SumarioAtdRecemNascidoSlPartoVO obterRelatorioSumarioAtdRecemNascidoSlParto(
			Integer atdSeq, Integer pacCodigo, Integer conConsulta,
			Short gsoSeqp, Byte rnaSeqp)
			throws ApplicationBusinessException;
	
	public Map<Integer, Vector<Short>> processarResultadosNotasAdicionaisPorCirurgia (
			Integer seqMbcCirurgia, Integer ordemImpressao, Boolean prntolAdmin) throws ApplicationBusinessException;

	
	Boolean habilitarBotaoDescricao(ProcedimentosPOLVO registroSelecionado);

	List<ExameOrdemCronologicaVO> pesquisarExamesOrdemCronologicaHist(
			Integer codPaciente) throws ApplicationBusinessException;

	Boolean verificarExibicaoNodoDadoHistorico(Integer codPaciente);

	public List<SumarioAssistenciaPartoVO> recuperarSumarioAssistenciaPartoVO(Integer pacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException;;
	
	public String obterDescricaoConvenio(Integer pPacCodigo, Integer pConNumero);
	
	public void aippInativaDocCertif(Integer seq);

	
	String obterIdadeRecemNascido(Date dataNascimento, Date criadoEm);
	
	String obterDuracaoDoParto(Integer nasGsoPacCodigo, Short nasGspSeqp,
			Short periodoDilatacao, Short periodoExpulsivo, Date nasDthrNascimento);
	
	String obterNomeProfissional(Integer matricula, Short vinCodigo);
	
	String obterNomeNumeroConselho(Integer matricula, Short vinCodigo);
	
	RelatorioAtendEmergObstetricaVO obterRelatorioAtendEmergObstetrica(Date criadoEm, Integer matricula, Short vinculo, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException;

	public List<SumarioSessoesQuimioVO> montarQuinzenaSessoesQuimio(
			Integer trpSeq) throws ApplicationBusinessException ;
			
	SumarioAdmissaoObstetricaInternacaoVO obterRelatorioSumarioAdmissaoObstetricaInternacao(Integer serMatricula, 
																							Short serVinCodigo, 
																							Integer pacCodigo, 
																							Integer conNumero, 
																							Short gsoSeqp, 
																							Date dthrMovimento)  throws ApplicationBusinessException;
	//BufferedImage getGraficoFrequenciaCardiacaFetalSumAssistParto() throws ApplicationBusinessException;

	String obterUnidadeInternacao(Integer atdSeq, Integer seq);

	public void gerarDadosSumarioPrescricaoQuimioPOL(Integer atdSeq, Integer pacCodigo, DominioTipoEmissaoSumario dominioTipoEmissaoSumario) throws ApplicationBusinessException;

	String obterNomeArquivoRelatorioSumarioParadaAtual(Integer atdSeq) throws ApplicationBusinessException;

	String diferencaEmHorasEMinutosFormatado(Date dataInicial, Date dataFinal);

	/*public boolean habilitarBotaoExamefisicoRN(Integer codigoPaciente,
		Integer numeroConsulta);*/
			
	RelatorioParadaInternacaoVO obterRelatorioParadaInternacao(Integer atdSeq, Date dthrCriacao) throws ApplicationBusinessException;

	List<RelExameFisicoRecemNascidoVO> recuperarRelExameFisicoRecemNascidoVO(
			Integer pacCodigo, Short gsoSeqp, Byte seqp, Integer conNumero)
			throws ApplicationBusinessException;
	
	String formataNomeProf(Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException;

	public List<McoRecemNascidos> listarSeqpRecemNascido(
			Integer codigoPaciente, Integer numeroConsulta);
	
	public List<RapQualificacao> buscarQualificacoesDoProfissional(
			Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException;

	public String obterNomeProfissionalQ(Integer serMatricula,
			Short serVinCodigo, List<RapQualificacao> qualificacoes);
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(Integer gsoPacCodigo, Short gsoSeqp);

	public Integer obterAtdSeq(Integer trpSeq);
	
	public List<ProcedimentosImagemPOLVO> montarListaImagens(Integer seq);
	
	List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPaciente(Integer pacCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	public Integer pesquisarInformacoesPerinataisCodigoPacienteCount(
			Integer pacCodigo);

	public boolean verificaSeExisteImagem(Integer paramVerificaImages);

	Boolean habilitarImprimirParadaInternacao(Integer atdSeq);	
	public ImpressaoPIM2VO gerarDadosImpressaoPIM2(Long seqPim2) throws ApplicationBusinessException;

	public List<PlanejamentoCirurgicoVO> recuperarPlanejamentoCirurgicoVO(
			Integer seqMbcAgenda, Integer seqMbcCirurgia) throws ApplicationBusinessException;
	
	public boolean validarGeracaoPendenciaAssinaturaPlanejamentoPaciente(Integer agdSeq) throws BaseException;

	public MbcAgendas obtemAgendaPlanejamentoCirurgico(Integer seqCirurgia) throws ApplicationBusinessException;

	public Boolean verificarSeDocumentoPlanejamentoCirugicoAssinado(Integer seq) throws ApplicationBusinessException;

	public Integer chamarDocCertifPlanejamentoCirurg(Integer seq);	

	Boolean habilitarBotaoDiagnostico() throws ApplicationBusinessException;

	String mbccIdaAnoMesDia(Integer pacCodigo);

	void verificarListaExamesPossuemImagemHist(
			Map<Integer, Vector<Short>> itensSelecionados)
			throws BaseException;

	void verificarListaExamesPossuemImagemHistOrigemPol(
			Map<Integer, Vector<Short>> itensSelecionados)
			throws BaseException;

	boolean verificarExisteMedicamento(Integer codPaciente, String tipo);

	public void validarRelatorioAltasAmbulatorias(Long seqMamAltaSumario) throws ApplicationBusinessException;

	/*BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			Integer matricula, Short vinculo)
			throws ApplicationBusinessException;*/
	
	List<PdtDescricaoProcedimentoCirurgiaVO> pesquisarRelatorioPdtDescricaoProcedimentoCirurgiaPorCrgSeq(Integer crgSeq) throws ApplicationBusinessException;

	public Boolean verificarSeEscalaPortalAgendamentoTemCirurgia(Integer pAgdSeq, Date pDtAgenda);

	void persistirAipOrigemDocGEDs(AipOrigemDocGEDs origemDocGED, RapServidores servidor) throws ApplicationBusinessException;

	List<AipOrigemDocGEDs> pesquisarAipOrigemDocGEDs(AipOrigemDocGEDs origemFiltro, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	Long pesquisarAipOrigemDocGEDsCount(AipOrigemDocGEDs origemFiltro);

	public AipOrigemDocGEDs obterAipOrigemDocGEDs(Integer seq);
	
	public List<RelatorioSumarioAltaAtendEmergenciaPOLVO> recuperarRelatorioSumarioAltaAtendEmergenciaPOLVO(
			Integer atdSeq) throws ApplicationBusinessException;

	public Boolean verificarDadosRelatorioSumarioAltaAtendEmergencia(
			Integer seqAtendimento);
	
	
	public Boolean habilitarBotaoParto(Integer codigoPaciente, 	Short gsoSeqp);
	public Boolean habilitarBotaoPartoComAtendimento(Integer codigoPaciente, Integer numeroConsulta, Short gsoSeqp);
	public Boolean habilitarBotaoPartoComRestricaoDeOrigem(	DominioOrigemAtendimento origemAtedimento, Integer pacCodigo, Short gsoSeqp, DominioOrigemAtendimento... origensRestritas);
	
	public Boolean habilitarBotaoNascimento(Integer codigoPaciente, Short gsoSeqp);
	public Boolean habilitarBotaoNascimentoComAtendimento(Integer codPaciente, Integer numConsulta, Short gsoSeqp);
	public Boolean habilitarBotaoNascimentoComRestricaoDeOrigem(DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,Short gsoSeqp,DominioOrigemAtendimento... origensRestritas);

	List<NodoPOLVO> montarListaPOL(Integer prontuario, Integer codigo,
			List<AghNodoPol> nodoOriginalList, Map<ParametrosPOLEnum, Object> parametros, Boolean obito)
			throws ApplicationBusinessException;

	Object[] processarRelatorioAtosAnestesicos(Integer seqMbcCirugia,
			Integer soeSeqAnestesico, Short seqpAnestesico, String idSessao)
			throws ApplicationBusinessException;

	void expandirNodosPOL(NodoPOLVO polvo,
			Map<ParametrosPOLEnum, Object> permissoes)
			throws ApplicationBusinessException;
	
	void verificarPerfilUBS(String usuario, String micro, AipPacientes paciente) throws ApplicationBusinessException;

	List<NodoPOLVO> carregaNosFisioterapia(Integer codPaciente, String tipo,
			String pagina, String icone) throws ApplicationBusinessException;

	List<NodoPOLVO> carregaNosQuimioterapia(Integer codPaciente, String tipo,
			String pagina, String icone) throws ApplicationBusinessException;
	
	boolean verificarPermissaoVisualizarResultadoExames(Integer soeSeq,
			Short seqp);

	boolean verificarPermissaoVisualizarResultadoExamesHist(Integer soeSeq,
			Short seqp);

	/**
	 * Gera imagem do gráfico de Frequencia Cardiaca
	 * 
	 * Web Service #37282
	 * 
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	BufferedImage getGraficoFrequenciaCardiacaFetalSumAssistParto(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException;

	/**
	 * Gera imagem do gráfico de Partograma
	 * 
	 * Web Service #37282
	 * 
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	BufferedImage getGraficoPartogramaSumAssistParto(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException;
	
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(Integer matricula, Short vinculo) throws ApplicationBusinessException;

	List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo,
			Date dataInicio);
}
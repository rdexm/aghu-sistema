package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
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
import br.gov.mec.aghu.paciente.dao.VAipPolMdtosDAO;
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



@Modulo(ModuloEnum.PACIENTES)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class ProntuarioOnlineFacade extends BaseFacade implements IProntuarioOnlineFacade {


@EJB
private AtendimentosEmergenciaPOLON atendimentosEmergenciaPOLON;

@EJB
private BotaoDescricaoListaProcedimentosON botaoDescricaoListaProcedimentosON;

@EJB
private RelatorioAtosAnestesicosPOLRN relatorioAtosAnestesicosPOLRN;

@EJB
private ProjetosPesquisaPOLON projetosPesquisaPOLON;

@EJB
private ConsultarExamesHistON consultarExamesHistON;

@EJB
private GeraDadosSumarioPrescricaoQuimioRN geraDadosSumarioPrescricaoQuimioRN;

@EJB
private RelatorioPlanejamentoCirurgicoON relatorioPlanejamentoCirurgicoON;

@EJB
private SumarioAtendimentoRecemNascidoON sumarioAtendimentoRecemNascidoON;

@EJB
private ConsultaMedicamentoHistON consultaMedicamentoHistON;

@EJB
private IncluirNotasRN incluirNotasRN;

@EJB
private ImprimeAltaAmbulatorialPolRN imprimeAltaAmbulatorialPolRN;

@EJB
private IncluiNotasPOLON incluiNotasPOLON;

@EJB
private ConsultarAmbulatorioPOLON consultarAmbulatorioPOLON;

@EJB
private ConsultaMedicamentoON consultaMedicamentoON;

@EJB
private SumarioAdmissaoObstetricaInternacaoON sumarioAdmissaoObstetricaInternacaoON;

@EJB
private RelatorioAtendEmergObstetricaON relatorioAtendEmergObstetricaON;

@EJB
private ProcedimentosPOLRN procedimentosPOLRN;

@EJB
private MamNotasAnaEvoON mamNotasAnaEvoON;

@EJB
private SumarioAtendimentoRecemNascidoRN sumarioAtendimentoRecemNascidoRN;

@EJB
private CirurgiasInternacaoPOLON cirurgiasInternacaoPOLON;

@EJB
private InformacoesPerinataisON informacoesPerinataisON;

@EJB
private ExibirConsultoriasAmbulatoriaisON exibirConsultoriasAmbulatoriaisON;

@EJB
private SumarioAdmissaoObstetricaNotasAdRN sumarioAdmissaoObstetricaNotasAdRN;

@EJB
private ConsultaDescricaoCirurgicaPolRN consultaDescricaoCirurgicaPolRN;

@EJB
private SessoesTerapeuticasPOLON sessoesTerapeuticasPOLON;

@EJB
private RelatorioAtendEmergObstetricaRN relatorioAtendEmergObstetricaRN;

@EJB
private SegurancaPOLON segurancaPOLON;

@EJB
private ConsultarExamesPolON consultarExamesPolON;

@EJB
private CirurgiasInternacaoPOLRN cirurgiasInternacaoPOLRN;

@EJB
private RelatorioSumarioAssistenciaPartoON relatorioSumarioAssistenciaPartoON;

@EJB
private ImpressaoPIM2ON impressaoPIM2ON;

@EJB
private ProcedimentosPOLON procedimentosPOLON;

@EJB
private ManterOrigemDocGEDsON manterOrigemDocGEDsON;

@EJB
private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;

@EJB
private RelatorioSumarioAltaAtendEmergenciaPOLON relatorioSumarioAltaAtendEmergenciaPOLON;

@EJB
private ConsultaDescricaoCirurgicaPolON consultaDescricaoCirurgicaPolON;

@EJB
private RelatorioPdtDescProcCirurgiaON relatorioPdtDescProcCirurgiaON;

@EJB
private AghNodoPolON aghNodoPolON;

@EJB
private ConsultaDiagnosticoON consultaDiagnosticoON;

@EJB
private HistoriaObstetricaGestacaoPacienteON historiaObstetricaGestacaoPacienteON;

@EJB
private ConsultaInternacaoON consultaInternacaoON;

@EJB
private RelatorioAtosAnestesicosPOLON relatorioAtosAnestesicosPOLON;

@EJB
private ConsultaAtendimentosPacientePOLON consultaAtendimentosPacientePOLON;

@EJB
private ConsultaNotasPolON consultaNotasPolON;

@EJB
private RelatorioSumarioTransferenciaON relatorioSumarioTransferenciaON;

@EJB
private RelatorioParadaInternacaoON relatorioParadaInternacaoON;

@EJB
private ExamesAnatomopatologicosPOLRN examesAnatomopatologicosPOLRN;

@EJB
private ImprimeAltaAmbulatorialPolON imprimeAltaAmbulatorialPolON;

@EJB
private AltasAmbulatoriaisPolON altasAmbulatoriaisPolON;

@EJB
private RelatorioSumarioAssistenciaPartoGraficoON relatorioSumarioAssistenciaPartoGraficoON;

	@Inject
	private VAipPolMdtosDAO vAipPolMdtosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8685275664454851465L;

	protected AghNodoPolON getAghNodoPolON() {
		return aghNodoPolON;
	}
	
	protected ConsultaDiagnosticoON getConsultaDiagnosticoON() {
		return consultaDiagnosticoON;
	}
	
	protected ConsultaInternacaoON getConsultaInternacaoON() {
		return consultaInternacaoON;
	}
	
	protected ConsultaMedicamentoON getConsultaMedicamentoON() {
		return consultaMedicamentoON;
	}
	
	protected ConsultarExamesPolON getConsultarExamesPolON() {
		return consultarExamesPolON;
	} 

	protected ConsultarExamesHistON getConsultarExamesHistON() {
		return consultarExamesHistON;
	} 
	
	protected ConsultaNotasPolON getConsultaNotasPolON() {
		return consultaNotasPolON;
	}
	
	protected ConsultaDescricaoCirurgicaPolON getConsultaDescricaoCirurgicaPolON() {
		return consultaDescricaoCirurgicaPolON;
	}
	
	protected BotaoDescricaoListaProcedimentosON getBotaoDescricaoListaProcedimentosON() {
		return botaoDescricaoListaProcedimentosON; 
	}
	
	protected SegurancaPOLON getSegurancaPOLON(){
		return segurancaPOLON;
	}

	protected ImpressaoPIM2ON getImpressaoPIM2ON(){
		return impressaoPIM2ON;
	}
	
	protected ManterOrigemDocGEDsON getManterOrigemDocGEDsON(){
		return manterOrigemDocGEDsON;
	}
	
	@Override
	public Object[] getRelatorioProcedimentosCirurgiaPol(Integer seqCirurgia) throws ApplicationBusinessException {
		return getBotaoDescricaoListaProcedimentosON().getRelatorio(seqCirurgia);  
	}

	/**
	 * @param tipo
	 * @param sequence
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#buscaDetalhes(java.lang.String, java.lang.Integer)
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public InternacaoVO buscaDetalhes(String tipo, Integer sequence) throws ApplicationBusinessException {
		return getConsultaInternacaoON().buscaDetalhes(tipo, sequence);
	}

	/**
	 * @param numeroProntuario
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#pesquisaInternacoesCount(java.lang.Integer)
	 */
	@Override
	public Long pesquisaInternacoesCount(Integer numeroProntuario) throws ApplicationBusinessException {
		return getConsultaInternacaoON().pesquisaInternacoesCount(numeroProntuario);
	}

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
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<InternacaoVO> pesquisaInternacoes(Integer numeroProntuario) throws ApplicationBusinessException {
		return getConsultaInternacaoON().pesquisaInternacoes(numeroProntuario);
	}

	@Override
	public InternacaoVO obterInternacao(Integer seq, String tipo) throws ApplicationBusinessException {
		return getConsultaInternacaoON().obterInternacao(seq, tipo);
	}
	/**
	 * @param internacao
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#montaQuinzenaPrescricaoMedica(br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO, int)
	 */
	@Override
	public List<SumarioPrescricaoMedicaVO> montaQuinzenaPrescricaoMedica(InternacaoVO internacao, int codPaciente)
			throws ApplicationBusinessException {
		return getConsultaInternacaoON().montaQuinzenaPrescricaoMedica(internacao, codPaciente);
	}

	/**
	 * @param vo
	 * @param limitaResultado
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#buscaRelSumarioPrescricao(br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoVO, boolean)
	 */
	@Override
	public List<RelSumarioPrescricaoVO> buscaRelSumarioPrescricao(SumarioPrescricaoMedicaVO vo, boolean limitaResultado) {
		return getConsultaInternacaoON().buscaRelSumarioPrescricao(vo, limitaResultado);
	}
	
	/**
	 * @param vo
	 * @param limitaResultado
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#buscaRelSumarioPrescricaoEnfermagem(br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO, boolean)
	 */
	@Override
	public List<RelSumarioPrescricaoEnfermagemVO> buscaRelSumarioPrescricaoEnfermagem(SumarioPrescricaoEnfermagemVO vo, boolean limitaResultado) {
		return getConsultaInternacaoON().buscaRelSumarioPrescricaoEnfermagem(vo, limitaResultado);
	}
	
	
	/**
	 * @param atdSeq
	 * @param inicio
	 * @param Fim
	 * @throws ApplicationBusinessException  
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#atualizarDataImpSumario(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@Override
	public void atualizarDataImpSumario(Integer atdSeq, Date inicio, Date fim, String enderecoHost) throws ApplicationBusinessException {		
		getConsultaInternacaoON().atualizarDataImpSumario(atdSeq, inicio, fim, enderecoHost);
	}
	
	
	
	/**
	 * @param atdSeq
	 * @param inicio
	 * @param Fim
	 * @throws BaseException 
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaInternacaoON#atualizarDataImpSumarioEnfermagem(java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@Override
	public void atualizarDataImpSumarioEnfermagem(Integer atdSeq, Date inicio, Date Fim) throws BaseException {
		getConsultaInternacaoON().atualizarDataImpSumarioEnfermagem(atdSeq, inicio, Fim);
	}

	/**
	 * Busca dados para montagem da arvore de Laboratórios/Serviços.
	 * @param {Integer} codigoPaciente
	 * @return 
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultarExamesPolON#buscaArvoreLaboratorioServicosDeExames(java.lang.Integer)
	 */
	@Override
	public List<VAelPesquisaPolExameUnidade> buscaArvoreLaboratorioServicosDeExames(Integer codigoPaciente) {
		return getConsultarExamesPolON().buscaArvoreLaboratorioServicosDeExames(codigoPaciente);
	}
	
	/**
	 * Busca dados para montagem da arvore de Laboratórios/Serviços.
	 * @param {Integer} codigoPaciente
	 * @return 
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultarExamesPolON#buscarArvoreLaboratorioServicosDeExamesHist(java.lang.Integer)
	 */
	@Override
	public List<VAelPesquisaPolExameUnidadeHist> buscarArvoreLaboratorioServicosDeExamesHist(Integer codigoPaciente) {
		return getConsultarExamesPolON().buscarArvoreLaboratorioServicosDeExamesHist(codigoPaciente);
	}

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
	@Override
	public List<ItemSolicitacaoExamePolVO> buscaExamesPeloCodigoDoPacienteESituacao(Integer codigoPaciente, Short unfSeq, Date data,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame, Integer gmaSeq) throws ApplicationBusinessException {
		return getConsultarExamesPolON().buscaExamesPeloCodigoDoPacienteESituacao(codigoPaciente, unfSeq, data, situacaoItemExame, gmaSeq);
	}

	@Override
	public List<ItemSolicitacaoExamePolVO> buscaExamesPeloCodigoDoPacienteESituacaoHist(Integer codigoPaciente, Short unfSeq, Date data, DominioSituacaoItemSolicitacaoExame situacaoItemExame, Integer gmaSeq) throws ApplicationBusinessException {
		return getConsultarExamesHistON().buscaExamesPeloCodigoDoPacienteESituacao(codigoPaciente,unfSeq,data,situacaoItemExame,gmaSeq);
	}
	
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
	@Override
	public ByteArrayOutputStream buscarArquivoResultadoExame(Integer iseSoeSeq, Short iseSeqp) throws BaseException {
		return getConsultarExamesPolON().buscarArquivoResultadoExame(iseSoeSeq, iseSeqp);
	}
	
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
	@Override
	public ByteArrayOutputStream buscarArquivoResultadoExameHist(Integer iseSoeSeq, Short iseSeqp) throws BaseException {
		return getConsultarExamesPolON().buscarArquivoResultadoExameHist(iseSoeSeq, iseSeqp);
	}

	/**
	 * @param paciente
	 * @param tipo
	 * @param dataInicio
	 * @param firstResult 
	 * @param maxResults 
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterMedicamentos(br.gov.mec.aghu.model.AipPacientes, java.lang.String, java.util.Date)
	 */
	@Override
	public List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio, int firstResult, int maxResults) {
		return getConsultaMedicamentoON().obterMedicamentos(codPaciente, tipo, dataInicio, firstResult, maxResults);
	}
	
	@Override
	public List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio) {
		return getConsultaMedicamentoON().obterMedicamentos(codPaciente, tipo, dataInicio);
	}
	
	
	public Long obterMedicamentosCount(Integer codPaciente, String tipo, Date dataInicio){
		return getVAipPolMdtosDAO().obterMedicamentosCount(codPaciente, tipo, dataInicio);
	}
	
	
	@Override
	public List<Date> obterDataMedicamentos(Integer codPaciente, String tipo, Date dataInicio) {
		return  this.getVAipPolMdtosDAO().obterDataMedicamentos(codPaciente, tipo, dataInicio);
	}
	
	@Override
	public boolean verificarExisteMedicamento(Integer codPaciente, String tipo){
		return this.getVAipPolMdtosDAO().verificarExisteMedicamento(codPaciente, tipo);
	}

	
	protected VAipPolMdtosDAO getVAipPolMdtosDAO() {
		return vAipPolMdtosDAO;
	}

	/**
	 * @param atdSeq
	 * @param descMedicamento
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterQuantidadeMedicamentos(java.lang.Integer, java.lang.String)
	 */
	@Override
	public Integer obterQuantidadeMedicamentos(Integer atdSeq, String descMedicamento) {
		return getConsultaMedicamentoON().obterQuantidadeMedicamentos(atdSeq, descMedicamento);
	}

	/**
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.AghNodoPolON#recuperarAghNodoPolPorOrdem()
	 */
	@Override
	public List<AghNodoPol> recuperarAghNodoPolPorOrdem() {
		return getAghNodoPolON().recuperarAghNodoPolPorOrdem();
	}

	/**
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaDiagnosticoON#pesquisaDiagnosticosCount(br.gov.mec.aghu.model.AipPacientes)
	 */
	@Override
	public Integer pesquisaDiagnosticosCount(AipPacientes paciente) throws ApplicationBusinessException {
		return getConsultaDiagnosticoON().pesquisaDiagnosticosCount(paciente);
	}

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
	@Override
	public List<MamDiagnostico> pesquisaDiagnosticos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AipPacientes paciente) throws ApplicationBusinessException {
		return getConsultaDiagnosticoON().pesquisaDiagnosticos(firstResult, maxResult, orderProperty, asc, paciente);
	}

	
	/**
	 * Realiza a pesquisa de exames e retorna em ordem cronológica para o prontuário online.
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 15/02/2012
	 */
	@Override
	public List<ExameOrdemCronologicaVO> pesquisarExamesOrdemCronologica(Integer codPaciente) throws ApplicationBusinessException{
		return getConsultarExamesPolON().pesquisarExamesOrdemCronologica(codPaciente);
	}
	
	/**
	 * Realiza a pesquisa de exames e retorna em ordem cronológica para o prontuário online (Hist).
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<ExameOrdemCronologicaVO> pesquisarExamesOrdemCronologicaHist(Integer codPaciente) throws ApplicationBusinessException{
		return getConsultarExamesPolON().pesquisarExamesOrdemCronologicaHist(codPaciente);
	}
	
	/**
	 * Realiza a pesquisa de exames e retorna em ordem de amostras coletadas para o prontuário online.
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 17/02/2012
	 */
	@Override
	public List<ExameAmostraColetadaVO> pesquisarExamesAmostrasColetadas(Integer codPaciente) throws ApplicationBusinessException{
		return getConsultarExamesPolON().pesquisarExamesAmostrasColetadas(codPaciente);
	}

	@Override
	public List<ExameAmostraColetadaVO> pesquisarExamesAmostrasColetadasHist(Integer codPaciente) throws ApplicationBusinessException{
		return getConsultarExamesHistON().pesquisarExamesAmostrasColetadas(codPaciente);
	}
	
	/**
	 * Verifica se o exame tem permissão para visualizar Resultados de exames
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	@Override
	//@Restrict("#{s:hasPermission('acessoAdminPOL', 'acessar')}")
	public boolean verificarPermissaoVisualizarResultadoExames(Integer soeSeq, Short seqp){
		return getConsultarExamesPolON().verificarPermissaoVisualizarResultadoExames(soeSeq, seqp);
	}
	
	@Override
	//@Restrict("#{s:hasPermission('acessoAdminPOL', 'acessar')}")
	public boolean verificarPermissaoVisualizarResultadoExamesHist(Integer soeSeq, Short seqp){
		return getConsultarExamesPolON().verificarPermissaoVisualizarResultadoExamesHist(soeSeq, seqp);
	}
	
	
	/**
	 * Verifica se o exame tem permissão para visualizar Notas Adicionais
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	@Override
	public boolean verificarPermissaoVisualizarNotasAdicionais(Integer soeSeq, Short seqp){
		return getConsultarExamesPolON().verificarPermissaoVisualizarNotasAdicionais(soeSeq, seqp);
	}
	
	@Override
	@BypassInactiveModule
	public void verificarListaExamesPossuemImagem(Map<Integer, Vector<Short>> itensSelecionados) throws BaseException{
		getConsultarExamesPolON().verificarListaExamesPossuemImagem(itensSelecionados);
	}
	
	@Override
	@BypassInactiveModule
	public void verificarListaExamesPossuemImagemHist(Map<Integer, Vector<Short>> itensSelecionados) throws BaseException{
		getConsultarExamesPolON().verificarListaExamesPossuemImagemHist(itensSelecionados);
	}

	@Override
	@BypassInactiveModule
	public void verificarListaExamesPossuemImagemHistOrigemPol(Map<Integer, Vector<Short>> itensSelecionados) throws BaseException{
		getConsultarExamesPolON().verificarListaExamesPossuemImagemHist(itensSelecionados, true);
	}


	/**
	 * Método que carrega a lista quinzenal de seleção para o relatório
	 * de prescrição enfermagem
	 * @throws ApplicationBusinessException
	 */
	@Override
	public List<SumarioPrescricaoEnfermagemVO> montaQuinzenaPrescricaoEnfermagem(
			InternacaoVO internacao, Integer codigo) throws ApplicationBusinessException {
		return getConsultaInternacaoON().montaQuinzenaPrescricaoEnfermagem(internacao, codigo);
	}
	
	@Override
	public List<AltasAmbulatoriasPolVO> pesquisarAltasAmbulatoriaisPol(Integer pacCodigo, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) throws ApplicationBusinessException {
		return getAltasAmbulatoriaisPolON().pesquisarAltasAmbulatoriaisPol(pacCodigo, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarAltasAmbulatoriaisPolCount(Integer pacCodigo){
		return getAltasAmbulatoriaisPolON().pesquisarAltasAmbulatoriaisPolCount(pacCodigo);
	}
	
	private AltasAmbulatoriaisPolON getAltasAmbulatoriaisPolON(){
		return altasAmbulatoriaisPolON;
	}

	@Override
	public RelatorioSumarioTransferenciaVO criarRelatorioSumarioTransferencia(
			AghAtendimentos atendimento, String tipoImpressao, Short seqpAltaSumario) throws ApplicationBusinessException {
		
		return this.getRelatorioSumarioTransferenciaON().criarRelatorioSumarioTransferencia(atendimento,
				tipoImpressao, seqpAltaSumario);
	}
	
	private RelatorioSumarioTransferenciaON getRelatorioSumarioTransferenciaON(){
		return relatorioSumarioTransferenciaON;
	}
	

	// # 12004 - IMPRIME ALTA AMBULATORIAL POL
	@Override
	public List<AltaAmbulatorialPolImpressaoVO> recuperarAltaAmbuPolImpressaoVO(Long seqMamAltaSumario) throws ApplicationBusinessException {
		return getImprimeAltaAmbulatorialON().recuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);
	}
		
	private ImprimeAltaAmbulatorialPolON getImprimeAltaAmbulatorialON(){
		return imprimeAltaAmbulatorialPolON;
	}

	@Override
	public String getEnderecoCompleto() throws ApplicationBusinessException {
		return this.getImprimeAltaAmbulatorialPolRN().getEnderecoCompleto();
	}
	
	@Override
	public String getTextoPostoSaude() throws ApplicationBusinessException {
		return this.getImprimeAltaAmbulatorialPolRN().getTextoPostoSaude();
	}
	
	private ImprimeAltaAmbulatorialPolRN getImprimeAltaAmbulatorialPolRN(){
		return imprimeAltaAmbulatorialPolRN;
	}

	/**
	 * Método que carrega a lista de notas para o relatório
	 * @param nota
	 * @return
	 * @throws BaseException 
	 */
	@Override
	public List<NotasPolVO> pesquisarNotasPol(Integer seqNota) throws BaseException {
		return getConsultaNotasPolON().pesquisarNotasPol(seqNota);
	}

	/**
	 * Método que verifica se a NOTA será impressa
	 * @param nota
	 * @return
	 */
	@Override
	public Boolean visualizarRelatorio(Integer seqNota) {
		return getConsultaNotasPolON().visualizarRelatorio(seqNota);
	}
	
	// 11732
	private ExibirConsultoriasAmbulatoriaisON getExibirConsultoriasAmbulatoriaisON() {
		return exibirConsultoriasAmbulatoriaisON; 
	}
	
	@Override
	public List<MamInterconsultas> pesquisarConsultoriasAmbulatoriais(Integer codigoPaciente) {
		return getExibirConsultoriasAmbulatoriaisON().pesquisarConsultoriasAmbulatoriais(codigoPaciente); 
	}
	@Override
	public List<MamNotaAdicionalEvolucoes> pesquisarNotasPorCodigoPaciente
		(Integer codigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws ApplicationBusinessException {
		return this.getIncluiNotasPOLON().pesquisarNotasPorCodigoPaciente(codigo, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarNotasPorCodigoPacienteCount(Integer codigo) {
		return this.getIncluiNotasPOLON().pesquisarNotasPorCodigoPacienteCount(codigo);
	}	
	
	private IncluiNotasPOLON getIncluiNotasPOLON(){
		return incluiNotasPOLON;
	}
	
	@Override
	public MamNotaAdicionalEvolucoes incluirNotaPOL(String textoIncluiNota, AipPacientes paciente) throws ApplicationBusinessException {
				return getIncluiNotasPOLON().incluirNotaPOL(textoIncluiNota, paciente);
		
	}
	
	@Override
	public Integer recuperarVersaoDoc(Integer seqNota) {
		return getConsultaNotasPolON().recuperarVersaoDoc(seqNota);
	}

	/**
	 * Método que carrega a lista de descrição cirurgicas para o relatório
	 * @param crgSeq
	 * @param seqp
	 * @return
	 * @throws BaseException 
	 *  
	 */
	@Override
	public Collection<DescricaoCirurgiaVO> pesquisarDescricaoCirurgicaPol(
			Integer crgSeq, Short seqp, Boolean previa, String contextPath) throws BaseException {
		return getConsultaDescricaoCirurgicaPolON().pesquisarDescricaoCirurgicaPol(crgSeq, seqp, previa, contextPath);
	}
	
	@Override
	public List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOL(Integer codigo) {
		return getCirurgiasInternacaoPOLON().pesquisarCirurgiasInternacaoPOL(codigo);
	}
	
	@Override
	public MbcExtratoCirurgia buscarMotivoCancelCirurgia(Integer seq) {
		return getCirurgiasInternacaoPOLRN().buscarMotivoCancelCirurgia(seq);
	}

	private CirurgiasInternacaoPOLON getCirurgiasInternacaoPOLON() {
		return cirurgiasInternacaoPOLON;
	}

	private CirurgiasInternacaoPOLRN getCirurgiasInternacaoPOLRN() {
		return cirurgiasInternacaoPOLRN;
	}

	@Override
	public Boolean verificarSeDocumentoDescricaoCirugiaAssinado(
			Integer seqCirurgia) throws ApplicationBusinessException {
		return getCirurgiasInternacaoPOLRN().verificarSeDocumentoDescricaoCirugiaAssinado(seqCirurgia);
	}

	@Override
	public Integer chamarDocCertifCirurg(Integer seqCirurgia) {
		return getCirurgiasInternacaoPOLRN().chamarDocCertifCirurg(seqCirurgia);
	}
	
	@Override
	public Boolean validarBotaoAdicionarIncluirNotasPOL() throws ApplicationBusinessException {
		return getIncluiNotasPOLON().validarBotaoAdicionarIncluirNotasPOL();
	}

/*	@Override
	public Boolean validarLinkDiagnosticosIncluirNotasPOL() throws ApplicationBusinessException {
		return getIncluiNotasPOLON().validarLinkDiagnosticosIncluirNotasPOL(usuarioLogado);
	}*/

	@Override
	public void gravarJustificarExclusaoIncluirNotasPol(
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao) throws ApplicationBusinessException {
		getIncluiNotasPOLON().gravarJustificarExclusaoIncluirNotasPol(notaAdicionalEvolucao);
		
	}

	@Override
	public List<AtosAnestesicosPolVO> pesquisarRelatorioAtosAnestesicos(
			Long seqMbcFichaAnestesia, String vSessao) throws ApplicationBusinessException {
		return getRelatorioAtosAnestesicosPOLON().pesquisarRelatorioAtosAnestesicos(seqMbcFichaAnestesia, vSessao);
	}
	
	private RelatorioAtosAnestesicosPOLON getRelatorioAtosAnestesicosPOLON(){
		return relatorioAtosAnestesicosPOLON;
	}
	
	@Override
	public String mbccGetFarmaco(Integer seqMbcFichaFarmaco) {
		return getRelatorioAtosAnestesicosPOLRN().mbccGetFarmaco(seqMbcFichaFarmaco);
	}
	
	private RelatorioAtosAnestesicosPOLRN getRelatorioAtosAnestesicosPOLRN(){
		return relatorioAtosAnestesicosPOLRN;
	}

	@Override
	public List<AltaSumarioVO> carregarListaSumarioVO(Integer apaAtdSeq) throws ApplicationBusinessException {
		
		return getConsultaInternacaoON().carregarListaSumarioVO(apaAtdSeq);
	}

	@Override
	public Boolean verificarSeInternacaoVariasTransferencias(Integer apaAtdSeq) {
		return getConsultaInternacaoON().verificarSeInternacaoVariasTransferencias(apaAtdSeq);
	}

	@Override
	public Boolean habilitarBotaoDescricaoCirurgiasPol(Integer seq) {
		return getCirurgiasInternacaoPOLRN().habilitarBotaoDescricao(seq);
	}

	@Override
	public Boolean habilitarBotaoDocAssinadoCirurgiasPol(Integer seq) {
		return getCirurgiasInternacaoPOLRN().habilitarBotaoDocAssinado(seq);
	}

	@Override
	public Boolean habilitarBotaoAtoAnestesicoCirurgiasPol(Integer seq) {
		return getCirurgiasInternacaoPOLRN().habilitarBotaoAtoAnestesico(seq);
	}

	@Override
	public Boolean habilitarBotaoExameAnatopatologico(Integer seq) throws ApplicationBusinessException {
		return getCirurgiasInternacaoPOLRN().habilitarBotaoExameAnatopatologico(seq);
	}
	
	@Override
	public Boolean habilitarBotaoDescricao(ProcedimentosPOLVO registroSelecionado) {
		return getProcedimentosPOLON().habilitarBotaoDescricao(registroSelecionado);
	}
	
	@Override
	public Boolean habilitarBotaoImagemProc(ProcedimentosPOLVO registroSelecionado, Integer parametroVerificaImagem) {
		return getProcedimentosPOLON().habilitarBotaoImagem(registroSelecionado, parametroVerificaImagem);
	}

	@Override
	public Boolean habilitarBotaoExameAnatopatologicoProc(ProcedimentosPOLVO registroSelecionado) throws ApplicationBusinessException {
		return getProcedimentosPOLON().habilitarBotaoExamesAnatomopatologicos(registroSelecionado);
	}
	
	@Override
	public List<ProcedimentosPOLVO> pesquisarProcedimentosPOL(Integer codigo) throws ApplicationBusinessException {
		return getProcedimentosPOLON().pesquisarProcedimentosPOL(codigo);
	}
	
	private ProcedimentosPOLON getProcedimentosPOLON() {
		return procedimentosPOLON;
	}
	
	private InformacoesPerinataisON getInformacoesPerinataisON() {
		return informacoesPerinataisON;
	}	

	@Override
	public Boolean verificarSeDocumentoAtoAnestesicoAssinado(Integer seq) throws ApplicationBusinessException {
		return getCirurgiasInternacaoPOLRN().verificarSeDocumentoAtoAnestesicoAssinado(seq);	}

	@Override
	public Integer chamarDocCertifFicha(Integer seq) {
		return getCirurgiasInternacaoPOLRN().chamarDocCertifFicha(seq);	
	}

	@Override
	public Object[] imprimirDescricao(Integer seq) throws ApplicationBusinessException {
		return getCirurgiasInternacaoPOLRN().imprimirDescricao(seq);	
	}
	
	private ExamesAnatomopatologicosPOLRN getExamesAnatomopatologicosPOLRN() {
		return examesAnatomopatologicosPOLRN;
	}

	@Override
	public Object[] processarRelatorioAtosAnestesicos(Integer seqMbcCirugia, Integer soeSeqAnestesico, Short seqpAnestesico, String idSessao) throws ApplicationBusinessException {
		return getCirurgiasInternacaoPOLRN().processarRelatorioAtosAnestesicos(seqMbcCirugia, soeSeqAnestesico, seqpAnestesico, idSessao);
	}
	
	@Override
	public String visualizarNotaAnamneseEMG(Long numRegistro, Integer tipo) throws ApplicationBusinessException{
		return this.getMamNotasAnaEvoON().visualizarNotaAnamneseEMG(numRegistro, tipo);
	}
	
	
	protected MamNotasAnaEvoON getMamNotasAnaEvoON() {
		return mamNotasAnaEvoON;
	}	
	
	/**
	 * Método que pesquisa Atendimentos de Emergencia do POL
	 * @param codigo do paciente
	 * @return	
	 */	

	public List<AtendimentosEmergenciaPOLVO> pesquisarAtendimentosEmergenciaPOL(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade) {
		return getAtendimentosEmergenciaPOLON().pesquisarAtendimentosEmergenciaPOL(firstResult, maxResult, orderProperty, asc, codigo,
				dthrInicio, dthrFim, numeroConsulta, seqEspecialidade);
	}
	
	public Long pesquisarAtendimentosEmergenciaPOLCount(Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade) {
		return getAtendimentosEmergenciaPOLON().pesquisarAtendimentosEmergenciaPOLCount(codigo, dthrInicio, dthrFim, numeroConsulta, seqEspecialidade);
	}
	
	private AtendimentosEmergenciaPOLON getAtendimentosEmergenciaPOLON(){
		return atendimentosEmergenciaPOLON;
	}	

	public Boolean habilitarBotaoEvolucaoAnamnese(AtendimentosEmergenciaPOLVO registroSelecionado) {
		return getAtendimentosEmergenciaPOLON().habilitarBotaoEvolucaoAnamnese(registroSelecionado);
	}

	public Boolean habilitarBotaoConsultoria(AtendimentosEmergenciaPOLVO registroSelecionado) {
		return getAtendimentosEmergenciaPOLON().habilitarBotaoConsultoria(registroSelecionado);
	}

	public Boolean habilitarBotaoSumarioAlta(AtendimentosEmergenciaPOLVO registroSelecionado) {
		return getAtendimentosEmergenciaPOLON().habilitarBotaoSumarioAlta(registroSelecionado);
	}
	
	@Override
	public List<MbcDescricaoCirurgica> processaDescricaoComNomeResponsavelMbc(List<MbcDescricaoCirurgica> descricaoCirurgica) {
		return getCirurgiasInternacaoPOLRN().processaDescricaoComNomeResponsavelMbc(descricaoCirurgica); 
	}

	@Override
	public List<PdtDescricao> processaDescricaoComNomeResponsavel(List<PdtDescricao> descricaoPdt) {
		return getCirurgiasInternacaoPOLRN().processaDescricaoComNomeResponsavel(descricaoPdt);
	}
	
	protected HistoriaObstetricaGestacaoPacienteON getHistoriaObstetricaGestacaoPacienteON() {
		return historiaObstetricaGestacaoPacienteON;
	}
	
	@Override
	public void carregaNosGestacao(NodoPOLVO no, Integer gsoPacCodigo) {
		getHistoriaObstetricaGestacaoPacienteON().carregaNosGestacao(no,gsoPacCodigo); 
	}
	
	protected SessoesTerapeuticasPOLON getSessoesTerapeuticasPOLON() {
		return sessoesTerapeuticasPOLON;
	}


	@Override
	public Boolean habilitarBotaoConsObs(HistObstetricaVO registroSelecionado) {
		return getHistoriaObstetricaGestacaoPacienteON().habilitarBotaoConsObs(registroSelecionado);
	}

	@Override
	public Boolean habilitarBotaoAdmObs(
			DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, Integer conNumero) {
		return getHistoriaObstetricaGestacaoPacienteON().habilitarBotaoAdmObs(
				origemAtedimento, pacCodigo, gsoSeqp, conNumero);
	}

	@Override
	public Boolean habilitarBotaoAtoAnestesico(HistObstetricaVO registroSelecionado) {
		return getHistoriaObstetricaGestacaoPacienteON().habilitarBotaoAtoAnestesicoProc(registroSelecionado);
	}

	@Override
	public List<MptControleFreqSessao> listarSessoesFisioterapia(Integer trpSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getSessoesTerapeuticasPOLON().listarSessoesFisioterapia(trpSeq, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public Long listarSessoesFisioterapiaCount(Integer trpSeq) {
		return getSessoesTerapeuticasPOLON().listarSessoesFisioterapiaCount(trpSeq);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosPorProntuario(
			Integer numeroProntuario) throws ApplicationBusinessException {
		return this.getConsultaAtendimentosPacientePOLON()
				.pesquisarAtendimentosPorProntuario(numeroProntuario);
	}
	
	protected ConsultaAtendimentosPacientePOLON getConsultaAtendimentosPacientePOLON() {
		return consultaAtendimentosPacientePOLON;
	}

	@Override
	public List<NodoPOLVO> obterNodosExpandidos() throws ApplicationBusinessException {
		return getAghNodoPolON().obterNodosExpandidos();
	}
	
	public String buscarDescricaoCirurgiaPol(Integer crgSeq, Short seqp, Integer pNumero) {
		return consultaDescricaoCirurgicaPolRN.buscarNumDesenho(crgSeq, seqp, pNumero);
	}
	
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<AipPacientes> validarPermissoesPOL (
			AipPacientes paciente,
			Map<ParametrosPOLEnum, Object> parametros,
			String nomeMicrocomputador, Short processoConsultaPOL) throws ApplicationBusinessException, BaseListException  {
		
		return getSegurancaPOLON().validarPermissoesPOL(paciente, parametros, nomeMicrocomputador, processoConsultaPOL);		
	}
	
	
	public void verificarPerfilUBS(String usuario, String micro, AipPacientes paciente) throws ApplicationBusinessException{
		getSegurancaPOLON().verificarPerfilUBS(usuario, micro, paciente);
	}
	

	@Override
	public List<NodoPOLVO> carregaNosFisioterapia(Integer codPaciente, String tipo, String pagina, String icone) throws ApplicationBusinessException {
		return getSessoesTerapeuticasPOLON().carregaNosFisioterapia(codPaciente, tipo, pagina, icone);
	}
	
	@Override
	public List<NodoPOLVO> carregaNosQuimioterapia(Integer codPaciente, String tipo, String pagina, String icone)  throws ApplicationBusinessException {
		return getSessoesTerapeuticasPOLON().carregaNosQuimioterapia(codPaciente, tipo, pagina, icone);
	}

	@Override
	public Long pesquisarProjetosPesquisaPacientePOLCount(Integer codigo) throws BaseException {
		return getProjetosPesquisaPOLON().pesquisarProjetosPesquisaPacientePOLCount(codigo);
	}

	@Override
	public List<AelProjetoPacientes> pesquisarProjetosPesquisaPacientePOL(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo) throws BaseException {
		return getProjetosPesquisaPOLON().pesquisarProjetosPesquisaPacientePOL(firstResult, maxResult, orderProperty, asc, codigo);
	}
	
	private ProjetosPesquisaPOLON getProjetosPesquisaPOLON(){
		return projetosPesquisaPOLON;
	}
	
	@Override
	public void processarNodosEmergenciaPol(NodoPOLVO emergenciaNodo, Integer codPaciente) throws ApplicationBusinessException {
		getAtendimentosEmergenciaPOLON().processarNodosEmergenciaPol(emergenciaNodo, codPaciente);
	}

	@Override
	public MptCidTratTerapeutico obterCidTratamentoQuimio(
			Integer seqTratamentoTerQuimio) {
		return getSessoesTerapeuticasPOLON().obterCidTratamentoQuimio(seqTratamentoTerQuimio);
	}
	
	@Override
	public String visualizarNotaEvolucaoEMG(Long numRegistro, Integer tipo) throws ApplicationBusinessException{
		return getMamNotasAnaEvoON().visualizarNotaEvolucaoEMG(numRegistro, tipo);
	}

	@Override
	public String buscarIdade(Date dtNascimento, Date dataCirurgia) {		
		return getConsultaDescricaoCirurgicaPolRN().buscarIdade(dtNascimento, dataCirurgia);
	}
	
	private ConsultaDescricaoCirurgicaPolRN getConsultaDescricaoCirurgicaPolRN(){
		return consultaDescricaoCirurgicaPolRN;
	}

	@Override
	public List<PdtDescricaoProcedimentoCirurgiaVO> recuperarPdtDescricaoProcedimentoCirurgiaVO(Integer seqPdtDescricao) throws ApplicationBusinessException {
		return getRelatorioPdtDescProcCirurgiaON().pesquisarRelatorioPdtDescricaoProcedimentoCirurgia(seqPdtDescricao);
	}
	
	@Override
	public List<PdtDescricaoProcedimentoCirurgiaVO> pesquisarRelatorioPdtDescricaoProcedimentoCirurgiaPorCrgSeq(Integer crgSeq) throws ApplicationBusinessException {
		return getRelatorioPdtDescProcCirurgiaON().pesquisarRelatorioPdtDescricaoProcedimentoCirurgiaPorCrgSeq(crgSeq);
	}

	private RelatorioPdtDescProcCirurgiaON getRelatorioPdtDescProcCirurgiaON(){
		return relatorioPdtDescProcCirurgiaON;
	}

	@Override
	public String obterLocalizacaoPacienteParaRelatorio(String leitoID, Short numeroQuarto, Short seqUnidadeFuncional) {
		return this.getRelatorioSumarioTransferenciaON().obterLocalizacaoPacienteParaRelatorio(leitoID, numeroQuarto, seqUnidadeFuncional);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer numeroProntuario, String atendimentos) {
		return getConsultarAmbulatorioPOLON()
				.pesquisarAtendimentoAmbPorPacCodigo(firstResult, maxResult,
						orderProperty, asc, numeroProntuario, atendimentos);
	}

	@Override
	public Long pesquisarAtendimentoAmbPorPacCodigoCount(
			Integer numeroProntuario, String atendimentos) {
		return getConsultarAmbulatorioPOLON()
				.pesquisarAtendimentoAmbPorPacCodigoCount(numeroProntuario,
						atendimentos);
	}
	
	private ConsultarAmbulatorioPOLON getConsultarAmbulatorioPOLON(){
		return consultarAmbulatorioPOLON;
	}

	protected ConsultaMedicamentoHistON getConsultaMedicamentoHistON() {
		return consultaMedicamentoHistON;
	}
	
	

/**
	 * @param paciente
	 * @param tipo
	 * @param dataInicio
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterMedicamentos(br.gov.mec.aghu.model.AipPacientes, java.lang.String, java.util.Date)
	 */
	@Override
	public List<VAipPolMdtosAghuHist> obterMedicamentosHist(Integer codPaciente, String tipo, Date dataInicio) {
		return getConsultaMedicamentoHistON().obterMedicamentosHist(codPaciente, tipo, dataInicio);
	}

/**
	 * @param paciente
	 * @param tipo
	 * @return
	 * @see br.gov.mec.aghu.paciente.prontuarioonline.business.ConsultaMedicamentoON#obterMedicamentosCount(br.gov.mec.aghu.model.AipPacientes, java.lang.String)
	 */
	@Override
	public Long obterMedicamentosHistCount(Integer codPaciente, String tipo) {
		return getConsultaMedicamentoHistON().obterMedicamentosHistCount(codPaciente, tipo);
	}
	
	protected RelatorioParadaInternacaoON getRelatorioParadaInternacaoON() {
		return relatorioParadaInternacaoON;
	}	
	
	protected SumarioAtendimentoRecemNascidoON getSumarioAtendimentoRecemNascidoON() {
		return sumarioAtendimentoRecemNascidoON;
	}

	@Override
	public SumarioAtdRecemNascidoSlPartoVO obterRelatorioSumarioAtdRecemNascidoSlParto(
			Integer atdSeq, Integer pacCodigo, Integer conConsulta,
			Short gsoSeqp, Byte rnaSeqp) throws ApplicationBusinessException {
		return getSumarioAtendimentoRecemNascidoON().montarRelatorio(atdSeq, pacCodigo, conConsulta, gsoSeqp, rnaSeqp);
	}
	
	@Override
	public Map<Integer, Vector<Short>> processarResultadosNotasAdicionaisPorCirurgia(
			Integer seqMbcCirurgia, Integer ordemImpressao, Boolean prntolAdmin) throws ApplicationBusinessException {
		return getExamesAnatomopatologicosPOLRN().processarResultadosNotasAdicionaisPorCirurgia(seqMbcCirurgia, ordemImpressao, prntolAdmin );
	}
	
	
	
	@Override
	public Boolean verificarExibicaoNodoDadoHistorico(Integer codPaciente) {
		return getConsultarExamesHistON().verificarExibicaoNodoDadoHistorico(codPaciente);
	}

	@Override
	public List<SumarioAssistenciaPartoVO> recuperarSumarioAssistenciaPartoVO(Integer pacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException{
		return getRelatorioSumarioAssistenciaPartoON().pesquisarRelatorioSumarioAssistenciaParto(pacCodigo, gsoSeqp, conNumero);
	}
	
	private RelatorioSumarioAssistenciaPartoON getRelatorioSumarioAssistenciaPartoON(){
		return relatorioSumarioAssistenciaPartoON;
	}
	
	@Override
	public String obterDescricaoConvenio(Integer pPacCodigo, Integer pConNumero) {
		return getSumarioAtendimentoRecemNascidoRN().obterDescricaoConvenio(pPacCodigo, pConNumero);
	}
	
	private SumarioAtendimentoRecemNascidoRN getSumarioAtendimentoRecemNascidoRN(){
		return sumarioAtendimentoRecemNascidoRN;
	}

	@Override
	public String obterIdadeRecemNascido(Date dataNascimento, Date criadoEm) {
		return getSumarioAtendimentoRecemNascidoON().obterIdadeRecemNascido(dataNascimento, criadoEm);
	}

	@Override
	public String obterDuracaoDoParto(Integer nasGsoPacCodigo, Short nasGspSeqp, 
			Short periodoDilatacao, Short periodoExpulsivo, Date nasDthrNascimento) {
		return getSumarioAtendimentoRecemNascidoON().obterDuracaoDoParto(nasGsoPacCodigo, nasGspSeqp, periodoDilatacao, periodoExpulsivo, nasDthrNascimento );
	}

	@Override
	public String obterNomeProfissional(Integer matricula, Short vinCodigo) {		
		return getSumarioAtendimentoRecemNascidoRN().obterNomeProfissional(matricula, vinCodigo); 
	}

	@Override
	public String obterNomeNumeroConselho(Integer matricula, Short vinCodigo) {
		return getSumarioAtendimentoRecemNascidoRN().obterNomeNumeroConselho(matricula, vinCodigo);
	}

	private RelatorioAtendEmergObstetricaON getRelatorioAtendEmergObstetricaON() {
		return relatorioAtendEmergObstetricaON;
	}

	@Override
	public RelatorioAtendEmergObstetricaVO obterRelatorioAtendEmergObstetrica(Date criadoEm, Integer matricula, Short vinculo, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		return getRelatorioAtendEmergObstetricaON().montarRelatorio(criadoEm, matricula, vinculo, gsoPacCodigo, gsoSeqp, conNumero);
	}

	@Override
	public List<SumarioSessoesQuimioVO> montarQuinzenaSessoesQuimio(Integer trpSeq) throws ApplicationBusinessException {
		return getSessoesTerapeuticasPOLON().montarQuinzenaSessoesQuimio(trpSeq);
	}

	@Override
	public void aippInativaDocCertif(Integer seq) {
		getIncluirNotasRN().aippInativaDocCertif(seq);
	}

	private IncluirNotasRN getIncluirNotasRN() {
		return incluirNotasRN;
	}
	
	protected SumarioAdmissaoObstetricaInternacaoON getSumarioAdmissaoObstetricaInternacaoON() {
		return sumarioAdmissaoObstetricaInternacaoON;
	}

	@Override
	public SumarioAdmissaoObstetricaInternacaoVO 
								obterRelatorioSumarioAdmissaoObstetricaInternacao(Integer serMatricula, 
																					Short serVinCodigo, 
																					Integer pacCodigo, 
																					Integer conNumero, 
																					Short gsoSeqp, 
																					Date dthrMovimento) throws ApplicationBusinessException
			 {
		return getSumarioAdmissaoObstetricaInternacaoON().montarRelatorio(serMatricula, serVinCodigo, pacCodigo, conNumero, gsoSeqp, dthrMovimento); 
	}

	@Override
	public void gerarDadosSumarioPrescricaoQuimioPOL(Integer atdSeq, Integer pacCodigo, DominioTipoEmissaoSumario dominioTipoEmissaoSumario) throws ApplicationBusinessException {
		getGeraDadosSumarioPrescricaoQuimioRN().gerarDadosSumarioPrescricaoQuimioPOL(atdSeq, pacCodigo, dominioTipoEmissaoSumario); 
	}

	private GeraDadosSumarioPrescricaoQuimioRN getGeraDadosSumarioPrescricaoQuimioRN() { 
		return geraDadosSumarioPrescricaoQuimioRN;
	}	
	
	@Override
	public String obterUnidadeInternacao(Integer atdSeq, Integer seq) {
		return getConsultaInternacaoON().obterUnidadeInternacao(atdSeq, seq);
	}

	@Override
	public String obterNomeArquivoRelatorioSumarioParadaAtual(Integer atdSeq) throws ApplicationBusinessException {
		return getRelatorioParadaInternacaoON().obterNomeArquivoRelatorioSumarioParadaAtual(atdSeq);
	}
	
	@Override
	public String diferencaEmHorasEMinutosFormatado(Date dataInicial, Date dataFinal){
		return getRelatorioAtosAnestesicosPOLON().diferencaEmHorasEMinutosFormatado(dataInicial, dataFinal);
	}

/*	@Override
	public boolean habilitarBotaoExamefisicoRN(Integer codigoPaciente, Integer numeroConsulta) {
		return getRelExameFisicoRecemNascidoPOLON().habilitarBotaoExamefisicoRN(codigoPaciente, numeroConsulta);
	}
*/	
	
	@Override
	public RelatorioParadaInternacaoVO obterRelatorioParadaInternacao(
			Integer atdSeq, Date dthrCriacao) throws ApplicationBusinessException {
		return getRelatorioParadaInternacaoON().montarRealatorio(atdSeq, dthrCriacao);
	}
	
	@Override
	public List<RelExameFisicoRecemNascidoVO> recuperarRelExameFisicoRecemNascidoVO(Integer pacCodigo, Short gsoSeqp, Byte seqP, Integer conNumero) throws ApplicationBusinessException{
		return getRelExameFisicoRecemNascidoPOLON().preencheRelExameFisicoRecemNascidoVO(pacCodigo, gsoSeqp, seqP, conNumero);
	}
	
	@Override
	public String formataNomeProf(Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException {
		return getRelExameFisicoRecemNascidoPOLON().formataNomeProf(serMatricula, serVinCodigo);
	}
																						
	private RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON(){
		return relExameFisicoRecemNascidoPOLON;
	}

	@Override
	public List<McoRecemNascidos> listarSeqpRecemNascido(
			Integer codigoPaciente, Integer numeroConsulta) {
		return getRelExameFisicoRecemNascidoPOLON().listarSeqpRecemNascido(codigoPaciente, numeroConsulta);
	}
	
	@Override
	public List<RapQualificacao> buscarQualificacoesDoProfissional(
			Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException {
		return getSumarioAdmissaoObstetricaNotasAdRN().buscarQualificacoesDoProfissional(serMatricula, serVinCodigo);
		
	}

	private SumarioAdmissaoObstetricaNotasAdRN getSumarioAdmissaoObstetricaNotasAdRN() {
		return sumarioAdmissaoObstetricaNotasAdRN;
	}

	@Override
	public String obterNomeProfissionalQ(Integer serMatricula,
			Short serVinCodigo, List<RapQualificacao> qualificacoes) {
		return getSumarioAdmissaoObstetricaNotasAdRN().obterNomeProfissional(serMatricula, serVinCodigo, qualificacoes);
	}

	@Override
	public List<McoRecemNascidos> pesquisarMcoRecemNascidoPorGestacaoOrdenado(Integer gsoPacCodigo, Short gsoSeqp) {
		return getSumarioAtendimentoRecemNascidoON().pesquisarMcoRecemNascidoPorGestacaoOrdenado(gsoPacCodigo, gsoSeqp);
	}
	
	@Override
	public Integer obterAtdSeq(Integer trpSeq) {
		return getSessoesTerapeuticasPOLON().obterAtdSeq(trpSeq);
	}
	public boolean verificaSeExisteImagem(Integer paramVerificaImagem){
		return getProcedimentosPOLON().verificaSeExisteImagem(paramVerificaImagem); 
	}
	
	public List<ProcedimentosImagemPOLVO> montarListaImagens(Integer seq){
		return getProcedimentosPOLON().montarListaImagens(seq);
	}

	@Override
	public Boolean habilitarBotaoExameFisico(
			DominioOrigemAtendimento tipoOrigemAtendimento,
			Integer mcoGestacoesPacCodigo, Short mcoGestacoesSeqP, DominioOrigemAtendimento ...origensRestritas) {
		return getInformacoesPerinataisON().habilitarBotaoExameFisico(tipoOrigemAtendimento, mcoGestacoesPacCodigo, mcoGestacoesSeqP, origensRestritas);
	}

	@Override
	public Boolean habilitarBotaoSumarioAlta(InformacoesPerinataisVO registroSelecionado) {
		return getInformacoesPerinataisON().habilitarBotaoSumarioAlta(registroSelecionado);
	}
	
	public ImpressaoPIM2VO gerarDadosImpressaoPIM2(Long seqPim2) throws ApplicationBusinessException{
		return getImpressaoPIM2ON().montarRelatorio(seqPim2);
	}

	@Override
	public List<PlanejamentoCirurgicoVO> recuperarPlanejamentoCirurgicoVO(
			Integer seqMbcAgenda, Integer seqMbcCirurgia) throws ApplicationBusinessException{
		return getRelatorioPlanejamentoCirurgicoON().pesquisarRelatorioPlanejamentoCirurgico(seqMbcAgenda, seqMbcCirurgia);
	}
	
	@Override
	public boolean validarGeracaoPendenciaAssinaturaPlanejamentoPaciente(Integer agdSeq) throws BaseException{
		return getRelatorioPlanejamentoCirurgicoON().validarGeracaoPendenciaAssinaturaPlanejamentoPaciente(agdSeq);
	}
	
	@Override
	public Boolean habilitarImprimirParadaInternacao(Integer atdSeq) {
		return getRelatorioParadaInternacaoON().habilitarImprimirParadaInternacao(atdSeq);
	}
	
	@Override
	public MbcAgendas obtemAgendaPlanejamentoCirurgico(Integer seqCirurgia) throws ApplicationBusinessException {
		return getRelatorioPlanejamentoCirurgicoON().obtemAgendaPlanejamentoCirurgico(seqCirurgia);
	}
	
	private RelatorioPlanejamentoCirurgicoON getRelatorioPlanejamentoCirurgicoON(){
		return relatorioPlanejamentoCirurgicoON;
	}

	@Override
	public Boolean verificarSeDocumentoPlanejamentoCirugicoAssinado(Integer seqCirurgia) throws ApplicationBusinessException {
		return getRelatorioPlanejamentoCirurgicoON().verificarSeDocumentoPlanejamentoCirugicoAssinado(seqCirurgia);
	}

	@Override
	public Integer chamarDocCertifPlanejamentoCirurg(Integer seq) {
		return getRelatorioPlanejamentoCirurgicoON().chamarDocCertifPlanejamentoCirurgico(seq);	
	}


	
	@Override
	public Boolean habilitarBotaoDiagnostico() throws ApplicationBusinessException {
		return getConsultaDiagnosticoON().habilitarBotaoDiagnostico();
	}
	
	@Override
	public
	List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPaciente(Integer pacCodigo, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getInformacoesPerinataisON()
				.pesquisarInformacoesPerinataisCodigoPaciente(pacCodigo,
						firstResult, maxResult, orderProperty, asc);
	}
	
	public Integer pesquisarInformacoesPerinataisCodigoPacienteCount(Integer pacCodigo){		
		return getInformacoesPerinataisON().pesquisarInformacoesPerinataisCodigoPacienteCount(pacCodigo);
	}
	
	@Override
	public String mbccIdaAnoMesDia(Integer pacCodigo){
		return getRelatorioAtosAnestesicosPOLRN().mbccIdaAnoMesDia(pacCodigo);
	}

	@Override
	public void validarRelatorioAltasAmbulatorias(Long seqMamAltaSumario) throws ApplicationBusinessException {
		getImprimeAltaAmbulatorialON().validarRelatorioAltasAmbulatorias(seqMamAltaSumario);
	}
	
	@Override
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			Integer matricula, Short vinculo)
			throws ApplicationBusinessException {
		return relatorioAtendEmergObstetricaRN.buscaConselhoProfissionalServidorVO(matricula, vinculo);
	}
	
	/*private RelatorioAtendEmergObstetricaRN getRelatorioAtendEmergObstetricaRN() {
		return relatorioAtendEmergObstetricaRN;
	}*/
	
	@Override
	public Boolean verificarSeEscalaPortalAgendamentoTemCirurgia(Integer pAgdSeq, Date pDtAgenda) {
		return getProcedimentosPOLRN().verificarSeEscalaPortalAgendamentoTemCirurgia(pAgdSeq, pDtAgenda);
	}
	
	protected ProcedimentosPOLRN getProcedimentosPOLRN() {
		return procedimentosPOLRN;
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarOrigemDocumento', 'pesquisar')}")
	public List<AipOrigemDocGEDs> pesquisarAipOrigemDocGEDs(AipOrigemDocGEDs origemFiltro, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getManterOrigemDocGEDsON().pesquisar(origemFiltro, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarOrigemDocumento', 'pesquisar')}")
	public Long pesquisarAipOrigemDocGEDsCount(AipOrigemDocGEDs origemFiltro) {
		return getManterOrigemDocGEDsON().pesquisarCount(origemFiltro);
	}

	@Override
	@Secure("#{s:hasPermission('manterOrigemDocumento', 'gravar')}")
	public void persistirAipOrigemDocGEDs(AipOrigemDocGEDs origemDocGED, RapServidores servidor) throws ApplicationBusinessException {
		getManterOrigemDocGEDsON().persistir(origemDocGED, servidor);
	}

	@Override
	@Secure("#{s:hasPermission('pesquisarOrigemDocumento', 'pesquisar')}")
	public AipOrigemDocGEDs obterAipOrigemDocGEDs(Integer seq) {
		return getManterOrigemDocGEDsON().obter(seq);
	}
	
	private RelatorioSumarioAltaAtendEmergenciaPOLON getRelatorioSumarioAltaAtendEmergenciaPOLON() {
		return relatorioSumarioAltaAtendEmergenciaPOLON;
	}

	@Override
	public List<RelatorioSumarioAltaAtendEmergenciaPOLVO> recuperarRelatorioSumarioAltaAtendEmergenciaPOLVO(
			Integer atdSeq) throws ApplicationBusinessException {
		return getRelatorioSumarioAltaAtendEmergenciaPOLON().recuperarRelatorioSumarioAltaAtendEmergenciaPOLVO(atdSeq);
	}

	@Override
	public Boolean verificarDadosRelatorioSumarioAltaAtendEmergencia(
			Integer seqAtendimento) {
		return getRelatorioSumarioAltaAtendEmergenciaPOLON().verificarDadosRelatorioSumarioAltaAtendEmergencia(seqAtendimento);
	}
	
	@Override
	public Boolean habilitarBotaoParto(Integer codigoPaciente, 	Short gsoSeqp) {
		return getInformacoesPerinataisON().habilitarBotaoParto(codigoPaciente, gsoSeqp);
	}
	@Override
	public Boolean habilitarBotaoPartoComRestricaoDeOrigem(
			DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, DominioOrigemAtendimento... origensRestritas) {
		return getHistoriaObstetricaGestacaoPacienteON().habilitarBotaoPartoComRestricaoDeOrigem(origemAtedimento, pacCodigo, gsoSeqp, origensRestritas);
	}
	@Override
	public Boolean habilitarBotaoPartoComAtendimento(Integer codigoPaciente, Integer numeroConsulta, Short gsoSeqp){
		return getConsultaInternacaoON().habilitarBotaoPartoComAtendimento(codigoPaciente, numeroConsulta, gsoSeqp);
	}
	
	
	@Override
	public Boolean habilitarBotaoNascimento(Integer codigoPaciente, Short gsoSeqp){
		return getInformacoesPerinataisON().habilitarBotaoNascimento(codigoPaciente, gsoSeqp);
	}
	@Override
	public Boolean habilitarBotaoNascimentoComAtendimento(Integer codPaciente, Integer numConsulta, Short gsoSeqp){
		return getConsultaInternacaoON().habilitarBotaoNascimentoComAtendimento(codPaciente, numConsulta, gsoSeqp);
	}
	@Override
	public Boolean habilitarBotaoNascimentoComRestricaoDeOrigem(
			DominioOrigemAtendimento origemAtedimento, Integer pacCodigo,
			Short gsoSeqp, DominioOrigemAtendimento... origensRestritas) {
		return getHistoriaObstetricaGestacaoPacienteON()
				.habilitarBotaoNascimentoComRestricaoDeOrigem(origemAtedimento,
						pacCodigo, gsoSeqp, origensRestritas);
	}
	
	
	@Override
	public List<NodoPOLVO> montarListaPOL(Integer prontuario, Integer codigo, List<AghNodoPol> nodoOriginalList, Map<ParametrosPOLEnum, Object> parametros, Boolean obito) throws ApplicationBusinessException{
		return aghNodoPolON.montarListaPOL(prontuario, codigo, nodoOriginalList, parametros, obito);
	}
	
	@Override
	public void expandirNodosPOL(NodoPOLVO polvo, Map<ParametrosPOLEnum, Object> permissoes) throws ApplicationBusinessException {
		 aghNodoPolON.expandirNodosPOL(polvo, permissoes);
	}

	public RelatorioSumarioAssistenciaPartoGraficoON getRelatorioSumarioAssistenciaPartoGraficoON() {
		return relatorioSumarioAssistenciaPartoGraficoON;
	}
	
	@Override
	public BufferedImage getGraficoFrequenciaCardiacaFetalSumAssistParto(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		return getRelatorioSumarioAssistenciaPartoGraficoON().getGraficoFrequenciaCardiacaFetalSumAssistParto(pacCodigo, gsoSeqp);
	}
	
	@Override
	public BufferedImage getGraficoPartogramaSumAssistParto(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		return getRelatorioSumarioAssistenciaPartoGraficoON().getGraficoPartogramaSumAssistParto(pacCodigo, gsoSeqp);
	}
	
	
}
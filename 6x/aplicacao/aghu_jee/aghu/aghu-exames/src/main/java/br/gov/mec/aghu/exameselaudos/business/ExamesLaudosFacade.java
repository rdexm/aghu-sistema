package br.gov.mec.aghu.exameselaudos.business;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioRecomendacaoMamografia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioSituacaoProjetoPesquisa;
import br.gov.mec.aghu.exames.business.MascaraExamesJasperReportON;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelDataRespostaProtocolosDAO;
import br.gov.mec.aghu.exames.dao.AelExamesNotificacaoDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelJustificativaExclusoesDAO;
import br.gov.mec.aghu.exames.dao.AelPacAgrpPesqExamesDAO;
import br.gov.mec.aghu.exames.dao.AelPacUnidFuncionaisDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoPacientesDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloInternoUnidsDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuesitosDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResHistDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.VAelSerSismamaDAO;
import br.gov.mec.aghu.exames.vo.ExameNotificacaoVO;
import br.gov.mec.aghu.exameselaudos.BuscarResultadosExamesVO;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelDataRespostaProtocolos;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExamesNotificacaoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelPacAgrpPesqExames;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelProjetoIntercorrenciaInternacao;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelRespostaQuesitos;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.AelSismamaMamoResHist;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.model.VAelSerSismama;
import br.gov.mec.aghu.model.VRapPessoaServidor;

/**
 * Porta de entrada do módulo Exames e Laudos.
 */


@Modulo(ModuloEnum.EXAMES_LAUDOS)
@SuppressWarnings({"PMD.CouplingBetweenObjects"})
@Stateless
public class ExamesLaudosFacade extends BaseFacade implements IExamesLaudosFacade {

	@EJB
	private SolicitacaoExamesRN solicitacaoExamesRN;
	
	@EJB
	private ExamesLaudosRN examesLaudosRN;
	
	@EJB
	private PacUnidFuncionaisON pacUnidFuncionaisON;
	
	@EJB
	private ProtocoloInternoUnidsRN protocoloInternoUnidsRN;
	
	@EJB
	private ExamesLaudosON examesLaudosON;
	
	@EJB
	private ResultadoMamografiaRN resultadoMamografiaRN;
	
	@EJB
	private ResultadoMamografiaON resultadoMamografiaON;
	
	@EJB
	private PacUnidFuncionaisRN pacUnidFuncionaisRN;
	
	@EJB
	private ApoioEmissaoLaudosON apoioEmissaoLaudosON;
	
	@EJB
	private VerificaQuestoesSismamaON verificaQuestoesSismamaON;
	
	@EJB
	private ResultadoMamografiaCarregaDadosON resultadoMamografiaCarregaDadosON;
	
	@EJB
	private ResultadoMamografiaInicializaDadosON resultadoMamografiaInicializaDadosON;
	
	@EJB
	private MascaraExamesJasperReportON mascaraExamesJasperReportON;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelProjetoPesquisasDAO aelProjetoPesquisasDAO;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;
	
	@Inject
	private AelSismamaMamoResHistDAO aelSismamaMamoResHistDAO;
	
	@Inject
	private AelPacAgrpPesqExamesDAO aelPacAgrpPesqExamesDAO;
	
	@Inject
	private AelRespostaQuesitosDAO aelRespostaQuesitosDAO;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelPacUnidFuncionaisDAO aelPacUnidFuncionaisDAO;
	
	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;
	
	@Inject
	private AelJustificativaExclusoesDAO aelJustificativaExclusoesDAO;
	
	@Inject
	private VAelSerSismamaDAO vAelSerSismamaDAO;
	
	@Inject
	private AelProtocoloInternoUnidsDAO aelProtocoloInternoUnidsDAO;
	
	@Inject
	private AelProjetoPacientesDAO aelProjetoPacientesDAO;
	
	@Inject
	private AelExamesNotificacaoDAO aelExamesNotificacaoDAO;
	
	@Inject
	private AelDataRespostaProtocolosDAO aelDataRespostaProtocolosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116321235468806647L;

	@Override
	@BypassInactiveModule
	public DominioOrigemAtendimento buscaLaudoOrigemPaciente(Integer soeSeq) {
		return getExamesLaudosON().buscaLaudoOrigemPaciente(soeSeq);
	}

	@Override
	@BypassInactiveModule
	public DominioOrigemAtendimento buscaLaudoOrigemPacienteRN(Integer soeSeq) {
		return getExamesLaudosRN().buscaLaudoOrigemPaciente(soeSeq);
	}

	@Override
	@BypassInactiveModule
	public String buscaJustificativaLaudo(Integer seqAtendimento, Integer phiSeq) {
		return getExamesLaudosON().buscaJustificativaLaudo(seqAtendimento,
				phiSeq);
	}

	@Override
	@BypassInactiveModule
	public List<BuscarResultadosExamesVO> buscarResultadosExames(
			Integer pacCodigo, Date dataLiberacao, String nomeCampo)
			throws ApplicationBusinessException {
		return getExamesLaudosON().buscarResultadosExames(pacCodigo,
				dataLiberacao, nomeCampo);
	}

	protected ExamesLaudosON getExamesLaudosON() {
		return examesLaudosON;
	}
	
	protected ResultadoMamografiaInicializaDadosON getResultadoMamografiaInicializaDadosON() {
		return resultadoMamografiaInicializaDadosON;
	}

	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('emissaoLaudo','pesquisar')}")
	public String pesquisarProtocolosPorPacienteString(Integer codigo) {
		return getApoioEmissaoLaudosON().pesquisarProtocolosPorPacienteString(
				codigo);
	}

	/** 
	 * 14/02/2012
	 * Autorizado a chamada pela Consultora Milena (CGTI), enquanto essa função não é totalmente implementada no AGH
	 * Atualmente não tem suporte no caso de existirem consultas vinculadas ao paciente, ocorrendo violação de FK.
	 */
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('emissaoLaudo','excluir')}")
	public void removerProtocolosNeurologia(Integer codigo)
			throws ApplicationBusinessException {
		this.getApoioEmissaoLaudosON().removerProtocolosNeurologia(codigo);
	}

	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('emissaoLaudo','pesquisar')}")
	public AelProjetoPesquisas obterProjetoPesquisa(Integer seq) {
		return this.getApoioEmissaoLaudosON().obterProjetoPesquisa(seq);
	}

	@Override
	@Secure("#{s:hasPermission('emissaoLaudo','pesquisar')}")
	public AelProjetoIntercorrenciaInternacao obterProjetoIntercorrenciaInternacao(
			Integer codigo, Integer seq, Short object) {
		return this.getApoioEmissaoLaudosON()
				.obterProjetoIntercorrenciaInternacao(codigo, seq, object);
	}

	@Override
	@Secure("#{s:hasPermission('emissaoLaudo','alterar')}")
	public void persistirProjetoIntercorrenciaInternacao(
			AelProjetoIntercorrenciaInternacao projetoIntercorrenciaInternacao) {
		this.getApoioEmissaoLaudosON()
				.persistirProjetoIntercorrenciaInternacao(
						projetoIntercorrenciaInternacao);

		this.flush();

	}

	private ExamesLaudosRN getExamesLaudosRN() {
		return examesLaudosRN;
	}

	private PacUnidFuncionaisRN getPacUnidFuncionaisRN() {
		return pacUnidFuncionaisRN;
	}

	@Override
	public void excluirAelPacienteUnidadeFuncional(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		this.getPacUnidFuncionaisON().excluir(elemento);

	}
	

	@Override
	public void inserirAelPacienteUnidadeFuncional(AelPacUnidFuncionais puf)
			throws ApplicationBusinessException {
		this.getPacUnidFuncionaisRN().inserir(puf);

	}

	@Override
	public void excluirAelProtocoloInternoUnids(
			AelProtocoloInternoUnids piuOrigem) throws BaseException {
		this.getProtocoloInternoUnidsRN().excluir(piuOrigem);

	}

	@Override
	public void inserirAelProtocoloInternoUnids(AelProtocoloInternoUnids piu)
			throws ApplicationBusinessException {
		this.getProtocoloInternoUnidsRN().inserir(piu);

	}

	@Override
	public void atualizarSolicitacaoExame(AelSolicitacaoExames solicitacaoExame) {
		this.getSolicitacaoExamesRN().atualizarSolicitacaoExame(
				solicitacaoExame);

	}

	/**
	 * Obtém um AelItemSolicitacaoExames pela chave primária
	 * 
	 * @param chavePrimaria
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public AelItemSolicitacaoExames obterAelItemSolicitacaoExamesPorChavePrimaria(
			AelItemSolicitacaoExamesId chavePrimaria) {
		return getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
				chavePrimaria);
	}
	
	@Override
	public AelExamesNotificacao obterAelExamesNotificacaoPorChavePrimaria(
			AelExamesNotificacaoId chavePrimaria){
		return getAelExamesNotificacaoDAO().obterPorChavePrimaria(
				chavePrimaria);
	}


	@Override
	@BypassInactiveModule
	public Long listarSolicitacoesExamesCount(Integer numeroConsulta) {
		return this.getAelSolicitacaoExameDAO().listarSolicitacoesExamesCount(numeroConsulta);
	}

	@Override
	@BypassInactiveModule
	public Long listarSolicitacoesExamesCount(Integer numeroConsulta, String codigoSituacaoItemSolicitacaoExame) {
		return this.getAelSolicitacaoExameDAO().listarSolicitacoesExamesCount(numeroConsulta, codigoSituacaoItemSolicitacaoExame);
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelExamesNotificacaoDAO getAelExamesNotificacaoDAO() {
		return aelExamesNotificacaoDAO;
	}

	private SolicitacaoExamesRN getSolicitacaoExamesRN() {
		return solicitacaoExamesRN;
	}

	protected ApoioEmissaoLaudosON getApoioEmissaoLaudosON() {
		return apoioEmissaoLaudosON;
	}

	protected ProtocoloInternoUnidsRN getProtocoloInternoUnidsRN() {
		return protocoloInternoUnidsRN;
	}
	
	@Override
	@BypassInactiveModule
	public AelProjetoPesquisas obterAelProjetoPesquisasPorChavePrimaria(Integer seq) {
		return this.getAelProjetoPesquisasDAO().obterPorChavePrimaria(seq);
	}

	protected AelProjetoPesquisasDAO getAelProjetoPesquisasDAO() {
		return aelProjetoPesquisasDAO;
	}
	
	@Override
	@BypassInactiveModule	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(String strPesquisa, Integer codigoPaciente) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaInternacao(strPesquisa, codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacaoNumero(String strPesquisa,
			DominioSituacaoProjetoPesquisa[] dominios) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaInternacaoNumero(strPesquisa, dominios);
	}

	@Override
	@BypassInactiveModule
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacaoNomeOuDescricao(String strPesquisa,
			DominioSituacaoProjetoPesquisa[] dominios) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaInternacaoNumero(strPesquisa, dominios);
	}
	
	@Override
	@BypassInactiveModule
	public List<AelSolicitacaoExames> listarSolicitacoesExames(
			Integer seqAtendimento, Date dataMaiorIgualCriadoEm) {
		return getAelSolicitacaoExameDAO().listarSolicitacoesExames(seqAtendimento, dataMaiorIgualCriadoEm);
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}

	protected AelDataRespostaProtocolosDAO getAelDataRespostaProtocolosDAO() {
		return aelDataRespostaProtocolosDAO;
	}

	protected AelJustificativaExclusoesDAO getAelJustificativaExclusoesDAO() {
		return aelJustificativaExclusoesDAO;
	}

	protected AelPacAgrpPesqExamesDAO getAelPacAgrpPesqExamesDAO() {
		return aelPacAgrpPesqExamesDAO;
	}

	protected AelPacUnidFuncionaisDAO getAelPacUnidFuncionaisDAO() {
		return aelPacUnidFuncionaisDAO;
	}

	protected AelProjetoPacientesDAO getAelProjetoPacientesDAO() {
		return aelProjetoPacientesDAO;
	}

	protected AelProtocoloInternoUnidsDAO getAelProtocoloInternoUnidsDAO() {
		return aelProtocoloInternoUnidsDAO;
	}

	protected AelRespostaQuesitosDAO getAelRespostaQuesitosDAO() {
		return aelRespostaQuesitosDAO;
	}

	@Override
	public List<AelProtocoloInternoUnids> pesquisarAelProtocoloInternoUnids(
			Integer pacCodigo) {
		return this.getAelProtocoloInternoUnidsDAO()
				.pesquisarAelProtocoloInternoUnids(pacCodigo);
	}

	@Override
	public void removerAelProtocoloInternoUnids(
			AelProtocoloInternoUnids aelProtocoloInternoUnids, boolean flush) {
		this.getAelProtocoloInternoUnidsDAO().remover(aelProtocoloInternoUnids);
		if (flush){
			this.getAelProtocoloInternoUnidsDAO().flush();
		}
	}
	
	@Override
	@BypassInactiveModule
	public AelSolicitacaoExames obterAelSolicitacaoExamesPorChavePrimaria(Integer seq) {
		return this.getAelSolicitacaoExameDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorAtendimento(
			Integer seqAtendimento, List<String> parametros) {
		return this.getAelSolicitacaoExameDAO()
				.pesquisarSolicitacaoExamePorAtendimento(seqAtendimento,
						parametros);
	}

	@Override
	public List<AelRespostaQuesitos> listarRespostasQuisitorPorDroPpjPacCodigo(
			Integer droPpjPacCodigo) {
		return this.getAelRespostaQuesitosDAO()
				.listarRespostasQuisitorPorDroPpjPacCodigo(droPpjPacCodigo);
	}

	@Override
	public void inserirAelRespostaQuesitos(
			AelRespostaQuesitos aelRespostaQuesitos, boolean flush) {
		this.getAelRespostaQuesitosDAO().persistir(aelRespostaQuesitos);
		if (flush){
			this.getAelRespostaQuesitosDAO().flush();
		}
	}

	@Override
	public void removerAelRespostaQuesitos(
			AelRespostaQuesitos aelRespostaQuesitos) {
		this.getAelRespostaQuesitosDAO().remover(aelRespostaQuesitos);
		this.getAelRespostaQuesitosDAO().flush();
	}

	@Override
	public List<AelProjetoPacientes> listarProjetosPaciente(
			Integer codigoPaciente) {
		return this.getAelProjetoPacientesDAO().listarProjetosPaciente(codigoPaciente);
	}

	@Override
	@BypassInactiveModule
	public List<Integer> pesquisarCProjPac(final Integer codigoPaciente, final Date dataTruncada) {
		return this.getAelProjetoPacientesDAO().pesquisarCProjPac(codigoPaciente, dataTruncada);
	}
	
	@BypassInactiveModule
	public Boolean verificarExisteCProjPac(final Integer codigoPaciente, final Date dataTruncada) {
		return this.getAelProjetoPacientesDAO().verificarExisteCProjPac(codigoPaciente, dataTruncada);
	}	

	@Override
	public void persistirAelProjetoPacientes(
			AelProjetoPacientes aelProjetoPacientes) {
		this.getAelProjetoPacientesDAO().persistir(aelProjetoPacientes);
	}

	@Override
	public void removerAelProjetoPacientes(
			AelProjetoPacientes aelProjetoPacientes) {
		this.getAelProjetoPacientesDAO().remover(aelProjetoPacientes);
		this.getAelProjetoPacientesDAO().flush();
	}

	@Override
	public Integer listarMaxNumeroControleExamePaciente(Integer pacCodigo) {
		return this.getAelPacUnidFuncionaisDAO()
				.listarMaxNumeroControleExamePaciente(pacCodigo);
	}

	@Override
	public List<AelPacUnidFuncionais> pesquisarAelPacUnidFuncionais(
			Integer pacCodigo) {
		return this.getAelPacUnidFuncionaisDAO().pesquisarAelPacUnidFuncionais(
				pacCodigo);
	}

	@Override
	public List<Integer> listarAxeSeqsPacAgrpPesq(Integer pacCodigo) {
		return this.getAelPacAgrpPesqExamesDAO().listarAxeSeqsPacAgrpPesq(
				pacCodigo);
	}

	@Override
	public List<AelPacAgrpPesqExames> listarPacAgrpPesqExames(
			Integer pacCodigo, Integer[] axeSeqs) {
		return this.getAelPacAgrpPesqExamesDAO().listarPacAgrpPesqExames(
				pacCodigo, axeSeqs);
	}

	@Override
	public List<AelPacAgrpPesqExames> listarPacAgrpPesqExamesPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAelPacAgrpPesqExamesDAO()
				.listarPacAgrpPesqExamesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void removerAelPacAgrpPesqExames(
			AelPacAgrpPesqExames aelPacAgrpPesqExames, boolean flush) {
		this.getAelPacAgrpPesqExamesDAO().remover(aelPacAgrpPesqExames);
		if (flush){
			this.getAelPacAgrpPesqExamesDAO().flush();
		}
	}

	@Override
	public void persistirAelPacAgrpPesqExames(
			AelPacAgrpPesqExames aelPacAgrpPesqExames) {
		this.getAelPacAgrpPesqExamesDAO().persistir(aelPacAgrpPesqExames);
	}

	@Override
	@BypassInactiveModule
	public List<AelProjetoPacientes> pesquisarProjetosDePesquisaPorPaciente(
			AipPacientes paciente) {
		return this.getAelJustificativaExclusoesDAO()
				.pesquisarProjetosDePesquisaPorPaciente(paciente);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarProjetosDePesquisaPorPacienteCount(
			AipPacientes paciente) {
		return this.getAelJustificativaExclusoesDAO()
				.pesquisarProjetosDePesquisaPorPacienteCount(paciente);
	}

	@Override
	public List<AelDataRespostaProtocolos> listarDatasRespostasProtocolosPorPpjPacCodigo(
			Integer ppjPacCodigo) {
		return this.getAelDataRespostaProtocolosDAO()
				.listarDatasRespostasProtocolosPorPpjPacCodigo(ppjPacCodigo);
	}

	@Override
	public void inserirAelDataRespostaProtocolos(
			AelDataRespostaProtocolos aelDataRespostaProtocolos, boolean flush) {
		this.getAelDataRespostaProtocolosDAO().persistir(
				aelDataRespostaProtocolos);
		if (flush){
			this.getAelDataRespostaProtocolosDAO().flush();
		}
	}

	@Override
	public void removerAelDataRespostaProtocolos(
			AelDataRespostaProtocolos aelDataRespostaProtocolos) {
		this.getAelDataRespostaProtocolosDAO().remover(
				aelDataRespostaProtocolos);
		this.getAelDataRespostaProtocolosDAO().flush();
	}

	@Override
	public AelAtendimentoDiversos obterAelAtendimentoDiversosPorId(final Integer seq){
		return getAelAtendimentoDiversosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AelAtendimentoDiversos> pesquisarAtendimentosDiversorPorPacCodigo(
			Integer pacCodigo) {
		return this.getAelAtendimentoDiversosDAO()
				.pesquisarAtendimentosDiversorPorPacCodigo(pacCodigo);
	}

	@Override
	public Long pesquisarAelAtendimentoDiversosCount(final AelAtendimentoDiversos filtros){
		return this.getAelAtendimentoDiversosDAO().pesquisarAelAtendimentoDiversosCount(filtros);
	}

	@Override
	public List<AelAtendimentoDiversos> pesquisarAelAtendimentoDiversos(final AelAtendimentoDiversos filtros, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.getAelAtendimentoDiversosDAO().pesquisarAelAtendimentoDiversos(filtros, firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	public void atualizarAelAtendimentoDiversos(
			AelAtendimentoDiversos aelAtendimentoDiversos, boolean flush) {
		this.getAelAtendimentoDiversosDAO().atualizar(aelAtendimentoDiversos);
		if (flush){
			this.getAelAtendimentoDiversosDAO().flush();
		}
	}

	@Override
	public List<AelProtocoloInternoUnids> listarProtocolosInternosUnids(
			AipPacientes paciente, Date criadoEm) {
		return this.getAelProtocoloInternoUnidsDAO()
				.listarProtocolosInternosUnids(paciente, criadoEm);
	}

	@Override
	@BypassInactiveModule
	public List<AelItemSolicitacaoExames> buscaItemSolicitacaoExamesComRespostaQuestao(
			List<Integer> listaSoeSeq) {
		return getAelItemSolicitacaoExameDAO().buscaItemSolicitacaoExamesComRespostaQuestao(listaSoeSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AelProjetoPacientes> pesquisarProjetosPesquisaPacientePOL(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, Integer matricula, Short vinCodigo) {
		return this.getAelProjetoPacientesDAO().pesquisarProjetosPesquisaPacientePOL(firstResult, maxResult, orderProperty, asc, codigo, matricula, vinCodigo);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarProjetosPesquisaPacientePOLCount(Integer codigo, Integer matricula, Short vinCodigo) {
		return this.getAelProjetoPacientesDAO().pesquisarProjetosPesquisaPacientePOLCount(codigo, matricula, vinCodigo);
	}

	@Override
	public Date buscaMaiorDataRecebimento(Integer itemSolicitacaoExameSoeSeq,
			Short itemSolicitacaoExameSeqp, String situacaoItemSolicitacaoCodigo) {
		return getExamesLaudosRN().buscaMaiorDataRecebimento(itemSolicitacaoExameSoeSeq, itemSolicitacaoExameSeqp, situacaoItemSolicitacaoCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public Date buscaMaiorDataLiberacao(Integer soeSeq, Short unidadeFuncionalSeqp) {
		return getExamesLaudosRN().buscaMaiorDataLiberacao(soeSeq, unidadeFuncionalSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public Date buscaMaiorDataLiberacaoHist(Integer soeSeq, Short unidadeFuncionalSeqp) {
		return getAelItemSolicExameHistDAO().buscaMaiorDataLiberacao(soeSeq, unidadeFuncionalSeqp);
	}
	
	private AelItemSolicExameHistDAO getAelItemSolicExameHistDAO(){
		return aelItemSolicExameHistDAO;
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarSePacientePossuiDadoHistoricoPOLAghAtendimento(Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame){
		return getAelItemSolicExameHistDAO().verificarSePacientePossuiDadoHistoricoPOLAghAtendimento(codigoPaciente, sitExame);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarSePacientePossuiDadoHistoricoPOLAelAtdDiverso(Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame){
		return getAelItemSolicExameHistDAO().verificarSePacientePossuiDadoHistoricoPOLAelAtdDiverso(codigoPaciente, sitExame);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarSePacientePossuiDadoHistoricoPOLAipMdtoHist(Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame){
		return getAelItemSolicExameHistDAO().verificarSePacientePossuiDadoHistoricoPOLAipMdtoHist(codigoPaciente, sitExame);
	}
	
	@Override
	public List<ExameNotificacaoVO> pesquisarExamesNotificacao(Integer firstResult, Integer maxResults, 
			String orderProperty, Boolean asc,
			String exaSigla, Integer manSeq, Integer calSeq, DominioSituacao situacao) {
		return this.getAelExamesNotificacaoDAO().pesquisarExameNotificacao(firstResult, maxResults, orderProperty, asc, exaSigla, manSeq, calSeq, situacao);
	}
	
	@Override
	public Long pesquisarExamesNotificacaoCount(
			String exaSigla, Integer manSeq, Integer calSeq, DominioSituacao situacao) {
		return this.getAelExamesNotificacaoDAO().pesquisarExameNotificacaoCount(exaSigla, manSeq, calSeq, situacao);
	}
	
	private AelResultadoExameDAO getAelResultadoExameDAO(){
		return aelResultadoExameDAO;
	}
	
	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO(){
		return vAelExameMatAnaliseDAO;
	}
	
	private VAelSerSismamaDAO getVAelSerSismamaDAO() {
		return vAelSerSismamaDAO;
	}	
	
	@BypassInactiveModule
	public List<AelResultadoExame> listarResultadoVersaoLaudo(Integer iseSoeSeq, Short iseSeqp) {
		return getAelResultadoExameDAO().listarResultadoVersaoLaudo(iseSoeSeq, iseSeqp);
	}
	
	@Override
	public List<VAelExameMatAnalise> pesquisarVExamesMaterialAnalise(
			final Object objPesquisa) {
		return this.getVAelExameMatAnaliseDAO()
				.pesquisarVAelExameMatAnalisePelaSiglaDescricao(objPesquisa);
	}
	
	@Override
	public Long pesquisarVExamesMaterialAnaliseCount(
			final Object objPesquisa) {
		return this.getVAelExameMatAnaliseDAO()
				.pesquisarVAelExameMatAnalisePelaSiglaDescricaoCount(objPesquisa);
	}
	
	@Override
	public List<AelProtocoloInternoUnids> pesquisarProtocoloInterno(
			Short unfSeq, Integer numeroProtocolo, Integer codigoPaciente, Integer prontuario,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
	
		return this.getAelProtocoloInternoUnidsDAO()
			.pesquisarProtocoloInterno(unfSeq, numeroProtocolo, codigoPaciente, prontuario, firstResult, 
					maxResult, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarProtocoloInternoCount(Short unfSeq, 
			Integer numeroProtocolo, Integer codigoPaciente, Integer prontuario) {
		return this.getAelProtocoloInternoUnidsDAO()
			.pesquisarProtocoloInternoCount(
					unfSeq, numeroProtocolo, codigoPaciente, prontuario);
	}
	
	
	@Override
	public AelProtocoloInternoUnids obterProtocoloInterno(
			Integer codigoPaciente, Short unfSeq, Integer numeroProtocolo) {
		return this.getAelProtocoloInternoUnidsDAO().obterProtocoloInterno(codigoPaciente, unfSeq);
	}

	@Override
	public List<AelPacUnidFuncionais> listarUnidadesFuncionaisPaciente(
			Integer codigoPaciente, Short unfSeq) {
		return this.getAelPacUnidFuncionaisDAO()
			.listarUnidadesFuncionaisPaciente(codigoPaciente, unfSeq);
	}

	public void atualizarExameProtocolado(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		this.getPacUnidFuncionaisON().atualizar(elemento);
	}
	
	@Secure("#{s:hasPermission('protocolarPaciente','executar')}")
	public void inserirExameProtocolado(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		this.getPacUnidFuncionaisON().inserir(elemento);
	}
	
	protected PacUnidFuncionaisON getPacUnidFuncionaisON() {
		return pacUnidFuncionaisON;
	}
	
	protected ResultadoMamografiaRN getResultadoMamografiaRN() {
		return resultadoMamografiaRN;
	}
	
	@Override
	public String obterRespostaMamo(AelItemSolicitacaoExames ise) {
		return getResultadoMamografiaCarregaDadosON().carregarInformacoesClinicas(ise);
	}
	
	@Override
	public List<AelSismamaMamoRes> obterRespostasMamo(Integer soeSeq, Short seqp) {
		return getResultadoMamografiaRN().obterMapaRespostasMamo(soeSeq,seqp);
	}
	
	@Override
	public List<AelSismamaMamoRes> obterRespostasMamografiaRespNull(Integer soeSeq, Short seqp) {
		return getResultadoMamografiaRN().obterRespostasMamografiaRespNull(soeSeq,seqp);
	}
	
	@Override
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExames(Integer soeSeq, Short ufeUnfSeq) throws ApplicationBusinessException {
		return getResultadoMamografiaON().pesquisarItemSolicitacaoExames(soeSeq, ufeUnfSeq);
	}

	@Override
	public DominioRecomendacaoMamografia onChangeCategoria(Map<String, AelSismamaMamoResVO> mapAbaConclusao, boolean mamaDireita) {
		return getResultadoMamografiaON().onChangeCategoria(mapAbaConclusao, mamaDireita);
	}
	
	@Override
	public List<VAelSerSismama> pesquisarResponsavel(Object obj) {
		return getVAelSerSismamaDAO().pesquisarResponsavel(obj);		
	}
	
	@Override
	public Long pesquisarResponsavelCount(Object obj) {
		return getVAelSerSismamaDAO().pesquisarResponsavelCount(obj);		
	}
	
	@Override
	public List<RapServidores> pesquisarResidente(Object obj) throws ApplicationBusinessException {
		return getResultadoMamografiaON().pesquisarResidente(obj);
	}	
	
	@Override
	public Long pesquisarResidenteCount(Object obj) throws ApplicationBusinessException {
		return getResultadoMamografiaON().pesquisarResidenteCount(obj);
	}
	
	@Override
	public void reabrirLaudo(String situacaoAreaExecutora, String situacaoLiberado, Integer solicitacao, Short item) {
		getResultadoMamografiaON().reabrirLaudo(situacaoAreaExecutora, situacaoLiberado, solicitacao, item);
	}

	@Override
	public Integer assinarLaudo(String situacaoLiberado, Integer solicitacao, Short item, Map<String, AelSismamaMamoResVO> mapMamaD, 
			Map<String, AelSismamaMamoResVO> mapMamaE, Map<String, AelSismamaMamoResVO> mapAbaConclusao, String rxMamaBilateral, 
			String sexoPaciente, String habilitaMamaEsquerda, String habilitaMamaDireita, 
			String nomeResponsavel, Integer matricula, Integer vinCodigo,
			String infoClinicas, String nomeMicrocomputador) throws BaseException, BaseListException {
		return getResultadoMamografiaON().assinarLaudo(situacaoLiberado, solicitacao, item, mapMamaD, mapMamaE, mapAbaConclusao, 
				rxMamaBilateral, sexoPaciente, habilitaMamaEsquerda, habilitaMamaDireita, nomeResponsavel, matricula, vinCodigo, infoClinicas, nomeMicrocomputador);
	}

	@Override
	public Boolean exibirModalAssinarLaudo(String situacaoAreaExecutora,
			String situacaoExecutando, Integer solicitacao, Short item) {
		return getResultadoMamografiaON().exibirModalAssinarLaudo(situacaoAreaExecutora, situacaoExecutando, solicitacao, item);
	}

	@Override
	public Boolean exibirModalReabrirLaudo(String situacaoLiberado,
			Integer solicitacao, Short item) {
		return getResultadoMamografiaON().exibirModalReabrirLaudo(situacaoLiberado, solicitacao, item);
	}	
	
	protected ResultadoMamografiaON getResultadoMamografiaON() {
		return resultadoMamografiaON;
	}
	
	protected ResultadoMamografiaCarregaDadosON getResultadoMamografiaCarregaDadosON() {
		return resultadoMamografiaCarregaDadosON;
	}

	@Override
	@BypassInactiveModule
	public Boolean habilitarBotaoQuestaoSismama(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist) {
		return getVerificaQuestoesSismamaON().habilitarBotaoQuestaoSismama(solicitacoes, isHist);
	}
	
/*	@Override
	public Boolean habilitarBotaoQuestaoSismamaHist(Map<Integer, Vector<Short>> solicitacoes) {
		return getVerificaQuestoesSismamaON().habilitarBotaoQuestaoSismamaHist(solicitacoes);
	}*/

	
	@Override
	public Map<String, String> obterON2VerificaQuestoesSismama(Map<String, String> mapResposta) {
		return getVerificaQuestoesSismamaON().obterON2VerificaQuestoesSismama(mapResposta);
	}
	

	protected VerificaQuestoesSismamaON getVerificaQuestoesSismamaON() {
		return verificaQuestoesSismamaON;
	}
	
	@Override
	public void validarResponsavel(Integer matricula, Integer vinCodigo) throws ApplicationBusinessException {
		getResultadoMamografiaON().validarResponsavel(matricula, vinCodigo);
	}

	@Override
	public Map<String, Object> montarControleAbas(Short iseSeqpLida,
			Integer solicitacao, Short item, String rxMamaEsquerda,
			String rxMamaDireita) {
		return getResultadoMamografiaON().montarControleAbas(iseSeqpLida, solicitacao, item, rxMamaEsquerda, rxMamaDireita);
	}

	@Override
	public Map<String, Boolean> montarControleTela(Integer solicitacao,
			Short item, String situacaoLiberado, String situacaoAreaExecutora,
			String situacaoExecutando, String habilitaMamaDireita,
			String habilitaMamaEsquerda) {
		return getResultadoMamografiaON().montarControleTela(solicitacao, item, situacaoLiberado, 
				situacaoAreaExecutora, situacaoExecutando, habilitaMamaDireita, habilitaMamaEsquerda);
	}

	public Map<String, AelSismamaMamoResVO> inicializarMapDireita(){
		return getResultadoMamografiaON().inicializarMapDireita();
	}
	
	public Map<String, AelSismamaMamoResVO> inicializarMapEsquerda(){
		return getResultadoMamografiaON().inicializarMapEsquerda();
	}
	
	public Map<String, AelSismamaMamoResVO> inicializarMapConclusao() {
		return getResultadoMamografiaON().inicializarMapConclusao();
	}
	
	@Override
	public String obterDadosInformacaoClinica(Integer solicitacao, Short item) {
		return getResultadoMamografiaInicializaDadosON().obterDadosInformacaoClinica(solicitacao, item);
	}
	
	@Override
	public void obterInformacoesMama(Integer solicitacao, Short item,
			String habilitaMamaDireita,
			Map<String, AelSismamaMamoResVO> mapMamaD,
			String habilitaMamaEsquerda,
			Map<String, AelSismamaMamoResVO> mapMamaE,
			Map<String, AelSismamaMamoResVO> mapConclusao) {
		getResultadoMamografiaInicializaDadosON().obterInformacoesMama(solicitacao, item, habilitaMamaDireita,
				mapMamaD, habilitaMamaEsquerda, mapMamaE, mapConclusao);
	}
	
	public void salvarDados(AelItemSolicitacaoExames aelItemSolicitacaoExames, 
			 Map<String, AelSismamaMamoResVO> mapMamaD, 
			 Map<String, AelSismamaMamoResVO> mapMamaE, 
			 Map<String, AelSismamaMamoResVO> mapConclusao,
			 String rxMamaBilateral,
			 String vSexoPaciente,
			 boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita,
			 String medicoResponsavel,String infoClinicas) throws ApplicationBusinessException, BaseListException{
		
		getResultadoMamografiaON().salvarDados(aelItemSolicitacaoExames,
														mapMamaD, 
														mapMamaE, 
														mapConclusao, 
														rxMamaBilateral, 
														vSexoPaciente, 
														vHabilitaMamaEsquerda, 
														vHabilitaMamaDireita, 
														medicoResponsavel, 
														infoClinicas);
		
	}
	
	
	@Override
	public VAelSerSismama obterResponsavelResultadoMamografia(Integer solicitacao, Short item,
			String habilitaMamaDireita, Map<String, AelSismamaMamoResVO> mapMamaD) throws ApplicationBusinessException {
		return getResultadoMamografiaInicializaDadosON().obterResponsavel(solicitacao, item, mapMamaD);
	}
	
	@Override
	public VRapPessoaServidor obterResidenteResultadoMamografia(Integer solicitacao, Short item) throws ApplicationBusinessException {
		return getResultadoMamografiaInicializaDadosON().obterResidente(solicitacao, item);
	}
	
	@Override
	public Boolean verificarResidenteConectadoResultadoMamografia() throws ApplicationBusinessException {
		return getResultadoMamografiaInicializaDadosON().verificarResidenteConectado();
	}
	@Override
	@BypassInactiveModule
	public Map<String, String> preencherQuestionarioSisMama(
			Integer codSolicitacao, Short codItemSolicitacao, Boolean isHist){
		return getVerificaQuestoesSismamaON().preencherQuestionarioSisMama(codSolicitacao, codItemSolicitacao, isHist);
	}

	@Override
	public List<AelSismamaMamoResHist> obterRespostasMamoHist(
			Integer codSolicitacao, Short codItemSolicitacao) {
		return getAelSismamaMamoResHistDAO().obterRespostasMamoHist(codSolicitacao, codItemSolicitacao);
	}

	@Override
	public List<AelSismamaMamoResHist> obterRespostasMamografiaHistRespNull(
			Integer soeSeq, Short seqp) {
		return getAelSismamaMamoResHistDAO().obterRespostasMamografiaHistRespNull(soeSeq, seqp);
	}
	
	protected AelSismamaMamoResHistDAO getAelSismamaMamoResHistDAO(){
		return aelSismamaMamoResHistDAO;
	}

	@Override
	public void verificaLaudoPatologia(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist) {
		getExamesLaudosON().verificaLaudoPatologia(solicitacoes, isHist);
	}

	@Override
	public void verificaLaudoPatologia(List<AelItemSolicitacaoExames> listaSolics, Boolean isHist) {
		getExamesLaudosON().verificaLaudoPatologia(listaSolics, isHist);
	}

	@Override
	public Boolean verificarUnidadeFuncionalTemCaracteristica(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return resultadoMamografiaRN.verificarUnidadeFuncionalTemCaracteristica(unfSeq, caracteristica);
	}
	
	@Override
	public String obterTipoExamePatologico(Long numeroAp, Integer lu2Seq) {
		return aelItemSolicitacaoExameDAO.obterTipoExamePatologico(numeroAp, lu2Seq);
	}
	
	@Override
	public AelExamesNotificacao retornaExameNotificacaoPorId(AelExamesNotificacaoId id){
		return getAelExamesNotificacaoDAO().retornaExameNotificacaoPorId(id);
	}

	@Override
	public List<String> montaRecebimentoPatologia(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {
		return mascaraExamesJasperReportON.montaRecebimentoPatologia(numeroAp, lu2Seq);
	}

	@Override
	public AelAtendimentoDiversos obterAtendimentoDiversoComPaciente(Integer seq) {
		return getAelAtendimentoDiversosDAO().obterAtendimentoDiversoComPaciente(seq);
	}

}

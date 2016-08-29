package br.gov.mec.aghu.exames.questionario.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.exames.dao.AelCadCtrlQualidadesDAO;
import br.gov.mec.aghu.exames.dao.AelDadosCadaveresDAO;
import br.gov.mec.aghu.exames.dao.AelExQuestionarioOrigensDAO;
import br.gov.mec.aghu.exames.dao.AelExamesQuestionarioDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelQuestionariosConvUnidDAO;
import br.gov.mec.aghu.exames.dao.AelQuestionariosDAO;
import br.gov.mec.aghu.exames.dao.AelQuestoesQuestionarioDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelValorValidoQuestaoDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.dao.VAelItemSolicExamesDAO;
import br.gov.mec.aghu.exames.questionario.vo.InformacaoComplementarVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExQuestionarioOrigensId;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelExamesQuestionarioId;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.AelQuestionariosConvUnidId;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelQuestoesQuestionarioId;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelValorValidoQuestao;
import br.gov.mec.aghu.model.VAelExameMatAnalise;

/**
 * Porta de entrada do sub módulo de questionário do módulo exames.
 * 
 * @author fpalma
 * 
 */

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class QuestionarioExamesFacade extends BaseFacade implements IQuestionarioExamesFacade {


	@EJB
	private GrupoQuestaoON grupoQuestaoON;
	
	@EJB
	private InformacaoComplementarON informacaoComplementarON;
	
	@EJB
	private RespostaQuestaoON respostaQuestaoON;
	
	@EJB
	private QuestaoRN questaoRN;
	
	@EJB
	private QuestionarioRN questionarioRN;
	
	@EJB
	private QuestaoQuestionarioON questaoQuestionarioON;
	
	@EJB
	private AssociaOrigensON associaOrigensON;
	
	@EJB
	private ValorValidoQuestaoON valorValidoQuestaoON;
	
	@EJB
	private AssociaRequisitanteON associaRequisitanteON;
	
	@EJB
	private ExameQuestionarioON exameQuestionarioON;
	
	@EJB
	private QuestionarioON questionarioON;
	
	@EJB
	private GrupoQuestaoRN grupoQuestaoRN;
	
	@Inject
	private VAelItemSolicExamesDAO vAelItemSolicExamesDAO;
	
	@Inject
	private AelQuestionariosDAO aelQuestionariosDAO;
	
	@Inject
	private AelExamesQuestionarioDAO aelExamesQuestionarioDAO;
	
	@Inject
	private AelProjetoPesquisasDAO aelProjetoPesquisasDAO;
	
	@Inject
	private AelGrupoQuestaoDAO aelGrupoQuestaoDAO;
	
	@Inject
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;
	
	@Inject
	private AelQuestionariosConvUnidDAO aelQuestionariosConvUnidDAO;
	
	@Inject
	private AelExQuestionarioOrigensDAO aelExQuestionarioOrigensDAO;
	
	@Inject
	private AelQuestaoDAO aelQuestaoDAO;
	
	@Inject
	private AelCadCtrlQualidadesDAO aelCadCtrlQualidadesDAO;
	
	@Inject
	private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;
	
	@Inject
	private AelDadosCadaveresDAO aelDadosCadaveresDAO;
	
	@Inject
	private AelValorValidoQuestaoDAO aelValorValidoQuestaoDAO;
	
	@Inject
	private AelQuestoesQuestionarioDAO aelQuestoesQuestionarioDAO;
	
	private static final long serialVersionUID = 4063087259987573927L;

	@Override
	public void persistirAelExamesQuestionario(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		this.getExameQuestionarioON().persistir(examesQuestionario);
	}

	@Override
	public List<AelExamesQuestionario> buscarAelExamesQuestionario(final String emaExaSigla, 
																   final Integer emaManSeq, 
																   final Integer firstResult, 
																   final Integer maxResult,
																   final String orderProperty, 
																   final boolean asc) {
		return getAelExamesQuestionarioDAO().buscarAelExamesQuestionario(emaExaSigla, emaManSeq, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public AelExamesQuestionario obterAelExamesQuestionario(final String emaExaSigla, final Integer emaManSeq, final Integer qtnSeq) {
		return this.getAelExamesQuestionarioDAO().obterAelExamesQuestionario(emaExaSigla, emaManSeq, qtnSeq);
	}

	@Override
	public void excluirAelExamesQuestionario(final AelExamesQuestionarioId aelExamesQuestionario) throws BaseException {
		this.getExameQuestionarioON().remover(aelExamesQuestionario);
	}

	@Override
	public List<AelQuestionarios> pesquisarQuestionarios(Integer seq, String descricao, Byte nroVias, DominioSituacao situacao) {
		return getAelQuestionariosDAO().pesquisarGrupoExamePorSeqDescricaoNroViasSitacao(seq, descricao, nroVias, situacao);
	}

	@Override
	public void excluirQuestionario(Integer seq) throws ApplicationBusinessException {
		getQuestionarioON().validarRelacionamentosQuestionarioBeforeDelete(seq);
	}

	@Override
	public AelQuestionarios obterQuestionarioPorChavePrimaria(Integer seq) {
		return getAelQuestionariosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public void persistirQuestionario(AelQuestionarios questionario) throws ApplicationBusinessException {
		getQuestionarioRN().executarBeforeInsertQuestionario(questionario);
	}

	@Override
	public void atualizarQuestionario(AelQuestionarios questionarioNew) throws ApplicationBusinessException {
		getQuestionarioRN().executarBeforeUpdateQuestionario(questionarioNew);
	}

	@Override
	public AelQuestionarios obterQuestionarioPorId(Integer qtnSeq) {
		return getAelQuestionariosDAO().obterPorChavePrimaria(qtnSeq);
	}

	@Override
	public List<AelExamesQuestionario> buscarAelExamesQuestionarioPorQuestionario(final Integer qtnSeq) {
		return this.getAelExamesQuestionarioDAO().buscarAelExamesQuestionarioPorQuestionario(qtnSeq);
	}

	@Override
	public List<AelQuestao> buscarAelQuestao(final AelQuestao questao, final Integer arg0, final Integer arg1, final String arg2, final boolean arg3) {
		return getAelQuestaoDAO().buscarAelQuestao(questao, arg0, arg1, arg2, arg3);
	}
	
	@Override
	@Secure("#{s:hasPermission('grupoQuestao','pesquisar')}")
	public List<AelGrupoQuestao> buscarAelGrupoQuestao(final AelGrupoQuestao questao, final Integer arg0, final Integer arg1, final String arg2, final boolean arg3) {
		return getAelGrupoQuestaoDAO().buscarAelGrupoQuestao(questao, arg0, arg1, arg2, arg3);
	}


	@Override
	public Long contarAelQuestao(final AelQuestao questao) {
		return getAelQuestaoDAO().contarAelQuestao(questao);
	}
	
	@Override
	@Secure("#{s:hasPermission('grupoQuestao','pesquisar')}")
	public Long contarAelGrupoQuestao(final AelGrupoQuestao grupoQuestao) {
		return getAelGrupoQuestaoDAO().contarAelGrupoQuestao(grupoQuestao);
	}
	
	private AelGrupoQuestaoDAO getAelGrupoQuestaoDAO() {
		return aelGrupoQuestaoDAO;
	}

	@Override
	public AelQuestao obterAelQuestaoById(final Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getAelQuestaoDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	public AelGrupoQuestao obterAelGrupoQuestaoById(final Integer seq) {
		return getAelGrupoQuestaoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('questao','excluir')}")
	public void excluirQuestao(final Integer questaoSeq) throws ApplicationBusinessException {
		this.getQuestaoRN().remover(questaoSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('grupoQuestao','excluir')}")
	public void excluirGrupoQuestao(final Integer grupoQuestaoId) throws ApplicationBusinessException {
		this.getGrupoQuestaoON().remover(grupoQuestaoId);
	}

	private GrupoQuestaoON getGrupoQuestaoON() {
		return grupoQuestaoON;
	}

	@Override
	@Secure("#{s:hasPermission('questao','persistir')}")
	public void gravarQuestao(final AelQuestao questao) throws ApplicationBusinessException {
		this.getQuestaoRN().persistir(questao);
	}
	
	@Override
	@Secure("#{s:hasPermission('grupoQuestao','persistir')}")
	public void gravarGrupoQuestao(final AelGrupoQuestao grupoQuestao) throws ApplicationBusinessException {
		this.getGrupoQuestaoRN().persistir(grupoQuestao);
	}
	
	private GrupoQuestaoRN getGrupoQuestaoRN() {
		return grupoQuestaoRN;
	}

	@Override
	@Secure("#{s:hasPermission('valorValido','gravar')}")
	public void persistirValorValidoQuestao(AelValorValidoQuestao valorValidoQuestao) {
		this.getValorValidoQuestaoON().persistirValorValidoQuestao(valorValidoQuestao);
	}
	
	@Override
	@Secure("#{s:hasPermission('valorValido','alterar')}")
	public void atualizarValorValidoQuestao(AelValorValidoQuestao valorValidoQuestao) {
		this.getAelValorValidoQuestaoDAO().merge(valorValidoQuestao);
	}
	
	@Override
	@Secure("#{s:hasPermission('valorValido','excluir')}")
	public void excluirValorValidoQuestao(Integer qaoSeq, Short seqP) throws ApplicationBusinessException {
		getValorValidoQuestaoON().validarRelacionamentosValorValidoBeforeDelete(qaoSeq, seqP);
	}
	
	@Override
	@Secure("#{s:hasPermission('valorValido','pesquisar')}")
	public List<AelValorValidoQuestao> buscarValoresValidosQuestaoPorQuestao(Integer questaoId) {
		return getAelValorValidoQuestaoDAO().buscarValoresValidosQuestaoPorQuestao(questaoId);
	}
	
	@Override
	public List<AelQuestao> buscarAelQuestaoSuggestion(final Object objPesquisa, final Integer first, final Integer max, final String order, final boolean asc) {
		return this.getAelQuestaoDAO().buscarAelQuestaoSuggestion(objPesquisa, first, max, order, asc);
	}

	@Override
	public Long contarAelQuestaoSuggestion(final Object objPesquisa) {
		return this.getAelQuestaoDAO().contarAelQuestaoSuggestion(objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('questaoQuestionario','persistir')}")
	public void persistirAelQuestoesQuestionario(final AelQuestoesQuestionario questoesQuestionario) throws BaseException {
		this.getQuestaoQuestionarioON().persistir(questoesQuestionario);
	}

	@Override
	@Secure("#{s:hasPermission('questaoQuestionario','pesquisar')}")
	public List<AelQuestoesQuestionario> buscarAelQuestoesQuestionarioPorQuestionario(final Integer qtnSeq, final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return this.getAelQuestoesQuestionarioDAO().buscarAelQuestoesQuestionarioPorQuestionario(qtnSeq, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('questaoQuestionario','pesquisar')}")
	public AelQuestoesQuestionario obterAelQuestoesQuestionario(final AelQuestoesQuestionarioId id) {
		return this.getAelQuestoesQuestionarioDAO().obterPorChavePrimaria(id);
	}

	@Override
	@Secure("#{s:hasPermission('questaoQuestionario','pesquisar')}")
	public Long contarBuscarAelQuestoesQuestionarioPorQuestionario(final Integer qtnSeq) {
		return this.getAelQuestoesQuestionarioDAO().contarAelQuestoesQuestionarioPorQuestionario(qtnSeq);
	}
			
	@Override
	@Secure("#{s:hasPermission('questaoQuestionario','excluir')}")
	public void excluirAelQuestoesQuestionario(final AelQuestoesQuestionarioId id) throws ApplicationBusinessException {
		this.getQuestaoQuestionarioON().excluir(id);
		
	}

	@Override
	public List<AelGrupoQuestao> buscarAelGrupoQuestaoSuggestion(Object objPesquisa, Integer first, Integer max, String order, boolean asc) {
		return this.getAelGrupoQuestaoDAO().buscarAelGrupoQuestaoSuggestion(objPesquisa, first, max, order, asc);
	}

	@Override
	public Long contarAelGrupoQuestaoSuggestion(Object objPesquisa) {
		return this.getAelGrupoQuestaoDAO().contarAelGrupoQuestaoSuggestion(objPesquisa);
	}

	protected QuestaoQuestionarioON getQuestaoQuestionarioON() {
		return questaoQuestionarioON;
	}
	
	protected QuestaoRN getQuestaoRN(){
		return questaoRN;
	}

	protected AelQuestaoDAO getAelQuestaoDAO() {
		return aelQuestaoDAO;
	}

	protected AelQuestoesQuestionarioDAO getAelQuestoesQuestionarioDAO() {
		return aelQuestoesQuestionarioDAO;
	}
	
	protected AelQuestionariosDAO getAelQuestionariosDAO() {
		return aelQuestionariosDAO;
	}

	protected QuestionarioRN getQuestionarioRN() {
		return questionarioRN;
	}

	protected ExameQuestionarioON getExameQuestionarioON() {
		return exameQuestionarioON;
	}

	protected AelExamesQuestionarioDAO getAelExamesQuestionarioDAO() {
		return aelExamesQuestionarioDAO;
	}

	protected QuestionarioON getQuestionarioON() {
		return questionarioON;
	}
	
	protected AelValorValidoQuestaoDAO getAelValorValidoQuestaoDAO() {
		return aelValorValidoQuestaoDAO;
	}
	
	protected ValorValidoQuestaoON getValorValidoQuestaoON() {
		return valorValidoQuestaoON;
	}
	
	protected AelProjetoPesquisasDAO getAelProjetoPesquisasDAO() {
		return aelProjetoPesquisasDAO;
	}
	
	protected AelCadCtrlQualidadesDAO getAelCadCtrlQualidadesDAO() {
		return aelCadCtrlQualidadesDAO;
	}
	
	protected AelDadosCadaveresDAO getAelDadosCadaveresDAO() {
		return aelDadosCadaveresDAO;
	}
	
	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}

	private AssociaRequisitanteON getAssociarRequisitanteON() {
		return associaRequisitanteON;
	}
	
	
	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO(){
		return vAelExameMatAnaliseDAO;
	}

	@Override
	public VAelExameMatAnalise buscarVAelExameMatAnalisePelaSiglaESeq(String exaSigla, Integer manSeq) {
		return this.getVAelExameMatAnaliseDAO().buscarVAelExameMatAnalisePelaSiglaESeq(exaSigla, manSeq);
	}
	
	@Override
	@Secure("#{s:hasPermission('associarRequisitante','persistir')}")
	public void gravarRequisitante(AelQuestionariosConvUnid questionario) throws ApplicationBusinessException {
		getAssociarRequisitanteON().gravar(questionario);
	}
	
	@Override
	public Integer obterProximoSequencialAelQuestionariosConvUnid() {
		return getAssociarRequisitanteON().obterProximoSequencialAelQuestionariosConvUnid();
	}
	
	private AssociaOrigensON getAssociaOrigensON() {
		return associaOrigensON;
	}

	@Override
	@Secure("#{s:hasPermission('associarOrigens','persistir')}")
	public void gravarOrigem(AelExQuestionarioOrigens questionarioOrigem, boolean edicao) throws ApplicationBusinessException {
		getAssociaOrigensON().gravar(questionarioOrigem, edicao);
	}
	
	private AelExQuestionarioOrigensDAO getAelExQuestionarioOrigensDAO(){
		return aelExQuestionarioOrigensDAO;
	}

	@Override
	public List<AelExQuestionarioOrigens> pesquisarAelExQuestionarioOrigens(String eqeEmaExaSigla, Integer eqeEmaManSeq, Integer eqeQtnSeq) {
		return getAelExQuestionarioOrigensDAO().pesquisarAelExQuestionarioOrigens(eqeEmaExaSigla, eqeEmaManSeq, eqeQtnSeq);
	}

	@Override
	@Secure("#{s:hasPermission('associarOrigens','excluir')}")
	public void removerOrigem(AelExQuestionarioOrigensId questionarioOrigem) throws ApplicationBusinessException {
		getAelExQuestionarioOrigensDAO().removerPorId(questionarioOrigem);
	}
	
	@Override
	public AelQuestionariosConvUnid obterPorChavePrimaria(AelQuestionariosConvUnidId id) {
		return getAssociarRequisitanteON().obterPorChavePrimaria(id);
	}
	
	
	@Override
	public List<AelQuestionarios> pesquisarQuestionarioPorItemSolicitacaoExame(final String sigla, final Integer manSeq, final Short codigoConvenioSaude, final DominioOrigemAtendimento origem, final DominioTipoTransporteQuestionario tipoTransporte) {
		return this.getAelQuestionariosDAO().pesquisarQuestionarioPorItemSolicitacaoExame(sigla, manSeq, codigoConvenioSaude, origem, tipoTransporte);
	}
	
	@Override
	public List<AelRespostaQuestao> criarRespostasQuestionario(List<AelQuestionarios> questionarios){
		return this.getRespostaQuestaoON().criarRespostasQuestionario(questionarios);
	}

	@Override
	public List<AelQuestionariosConvUnid> buscarAelQuestionariosConvUnidPorQuestionario(Integer seqQuestionario,
											Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		return getAelQuestionariosConvUnidDAO().buscarAelQuestionariosConvUnidPorQuestionario(seqQuestionario,
																		firstResult,maxResult, orderProperty, asc);
	}

	@Override
	public Long buscarAelQuestionariosConvUnidPorQuestionarioCount(Integer seqQuestionario) {
		return getAelQuestionariosConvUnidDAO().buscarAelQuestionariosConvUnidPorQuestionarioCount(seqQuestionario);
	}
	
	@Override
	public void persistirAelRespostaQuestao(final AelRespostaQuestao aelRespostaQuestao) throws ApplicationBusinessException {
		this.getRespostaQuestaoON().persistir(aelRespostaQuestao);
	}
	
	private RespostaQuestaoON getRespostaQuestaoON() {
		return respostaQuestaoON;
	}
	
	protected InformacaoComplementarON getInformacaoComplementarON() {
		return informacaoComplementarON;
	}
	
	protected VAelItemSolicExamesDAO getVAelItemSolicExamesDAO() {
		return vAelItemSolicExamesDAO;
	}
	
	@Override
	@BypassInactiveModule
	@Secure("#{s:hasPermission('reimprimirInformacoesComplementares','reimprimir')}")
	public List<InformacaoComplementarVO> pesquisarInformacoesComplementares(Integer pacCodigo, Integer soeSeq, Short seqp, Integer qtnSeq) throws ApplicationBusinessException {
		return getInformacaoComplementarON().pesquisarInformacoesComplementares(pacCodigo, soeSeq, seqp, qtnSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Integer obterPacCodigoPelaSolicitacao(Integer soeSeq) {
		return getInformacaoComplementarON().obterPacCodigoPelaSolicitacao(soeSeq);
	}
	
	private AelQuestionariosConvUnidDAO getAelQuestionariosConvUnidDAO() {
		return aelQuestionariosConvUnidDAO;
	}


	@Override
	public List<AelQuestionariosConvUnid> buscarAelQuestionariosConvUnidPorQuestionario(Integer seqQuestionario) {
		return getAelQuestionariosConvUnidDAO().buscarAelQuestionariosConvUnidPorQuestionario(seqQuestionario);
	}
	
	@Override
	@BypassInactiveModule
	public void validarEnderecoPaciente(Integer pacCodigo) throws ApplicationBusinessException {
		getInformacaoComplementarON().validarEnderecoPaciente(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public Byte obterNumeroViasImpressaoInformacoesComplementares(Integer soeSeq, Short seqp, Integer qtnSeq) {
		return getInformacaoComplementarON().obterNumeroViasImpressaoInformacoesComplementares(soeSeq, seqp, qtnSeq);
	}

	@Override
	@Secure("#{s:hasPermission('valorValido','pesquisar')}")
	public List<AelValorValidoQuestao> buscarValoresValidosAtivosQuestaoPorQuestao(Integer seq) {
		return getAelValorValidoQuestaoDAO().buscarValoresValidosAtivosQuestaoPorQuestao(seq);
	}
	
	@Override
	public Boolean verificarSuggestionCidSeraExibidaNoQuestionarioExame(ItemSolicitacaoExameVO itemSolicitacaoExameVo) throws ApplicationBusinessException{
		return getQuestaoQuestionarioON().verificarSuggestionCidSeraExibidaNoQuestionarioExame(itemSolicitacaoExameVo);
	}
	
	@Override
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaPorNumeroOuNome(String strPesquisa) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaPorNumeroOuNome(strPesquisa);
	}

	@Override
	public Long pesquisarProjetosPesquisaPorNumeroOuNomeCount(String parametro) {
		return getAelProjetoPesquisasDAO().pesquisarProjetosPesquisaPorNumeroOuNomeCount(parametro);
	}

	@Override
	public Long obterCadCtrlQualidadesListCount(String parametro) {
		return getAelCadCtrlQualidadesDAO().obterCadCtrlQualidadesListCount(parametro);
	}
	
	@Override
	public List<AelCadCtrlQualidades> obterCadCtrlQualidadesList(String parametro) {
		return getAelCadCtrlQualidadesDAO().obterCadCtrlQualidadesList(parametro);
	}

	@Override
	public List<AelDadosCadaveres> obterDadosCadaveresList(String parametro) {
		return getAelDadosCadaveresDAO().obterDadosCadaveresList(parametro);
	}
	
	@Override
	public Long obterDadosCadaveresListCount(String parametro) {
		return getAelDadosCadaveresDAO().obterDadosCadaveresListCount(parametro);
	}
	
}


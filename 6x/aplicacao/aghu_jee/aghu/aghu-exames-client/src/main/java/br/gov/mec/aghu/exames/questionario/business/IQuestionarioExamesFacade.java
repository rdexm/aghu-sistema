package br.gov.mec.aghu.exames.questionario.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public interface IQuestionarioExamesFacade extends Serializable {

	
	public List<AelQuestionarios> pesquisarQuestionarios(Integer seq, String descricao, Byte nroVias, DominioSituacao situacao);
	
	public void excluirQuestionario(Integer seq) throws ApplicationBusinessException;

	public AelQuestionarios obterQuestionarioPorChavePrimaria(Integer seq);
	
	public void persistirQuestionario(AelQuestionarios questionario) throws ApplicationBusinessException;
	
	public void atualizarQuestionario(AelQuestionarios questionarioNew) throws ApplicationBusinessException;
	
	public void persistirAelExamesQuestionario(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException;
	
	List<AelExamesQuestionario> buscarAelExamesQuestionario(final String emaExaSigla, 
														    final Integer emaManSeq, 
														    final Integer firstResult, 
														    final Integer maxResult,
														    final String orderProperty, 
														    final boolean asc);

	public AelExamesQuestionario obterAelExamesQuestionario(String emaExaSigla, Integer emaManSeq, Integer qtnSeq);
	
	public void excluirAelExamesQuestionario(AelExamesQuestionarioId aelExamesQuestionario) throws BaseException;

	public AelQuestionarios obterQuestionarioPorId(Integer qtnSeq);
	
	public List<AelExamesQuestionario> buscarAelExamesQuestionarioPorQuestionario(Integer qtnSeq);
	
	public List<AelQuestao> buscarAelQuestao(final AelQuestao questao, final Integer arg0, final Integer arg1, final String arg2, final boolean arg3);

	public AelQuestao obterAelQuestaoById(final Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);
	
	public AelGrupoQuestao obterAelGrupoQuestaoById(final Integer seq);
	
	public void excluirQuestao(final Integer questaoSeq) throws ApplicationBusinessException;
	
	public void excluirGrupoQuestao(final Integer grupoQuestaoId) throws ApplicationBusinessException;
	
	public void gravarQuestao(final AelQuestao questao) throws ApplicationBusinessException;
	
	public void gravarGrupoQuestao(final AelGrupoQuestao grupoQuestao) throws ApplicationBusinessException;
	
	public Long contarAelQuestao(final AelQuestao questao);
	
	public Long contarAelGrupoQuestao(final AelGrupoQuestao grupoQuestao);
	
	public List<AelGrupoQuestao> buscarAelGrupoQuestao(final AelGrupoQuestao questao, final Integer arg0, final Integer arg1, final String arg2, final boolean arg3);
	
	public void persistirValorValidoQuestao(AelValorValidoQuestao valorValidoQuestao);
	
	public void atualizarValorValidoQuestao(AelValorValidoQuestao valorValidoQuestao);
	
	public void excluirValorValidoQuestao(Integer qaoSeq, Short seqP) throws ApplicationBusinessException;
	
	public List<AelValorValidoQuestao> buscarValoresValidosQuestaoPorQuestao(
			Integer questaoId);

	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaPorNumeroOuNome(String strPesquisa);

	public List<AelCadCtrlQualidades> obterCadCtrlQualidadesList(String parametro);

	public List<AelDadosCadaveres> obterDadosCadaveresList(String parametro);
	
	public VAelExameMatAnalise buscarVAelExameMatAnalisePelaSiglaESeq(String exaSigla, Integer manSeq);
	
	void gravarRequisitante(AelQuestionariosConvUnid questionario) throws ApplicationBusinessException;
	
	void gravarOrigem(AelExQuestionarioOrigens questionarioOrigem, boolean edicao) throws ApplicationBusinessException;
	
	void removerOrigem(AelExQuestionarioOrigensId questionarioOrigem) throws ApplicationBusinessException;
	
	List<AelExQuestionarioOrigens> pesquisarAelExQuestionarioOrigens(String eqeEmaExaSigla,	Integer eqeEmaManSeq, Integer eqeQtnSeq);
	
	public List<AelQuestao> buscarAelQuestaoSuggestion(final Object objPesquisa, final Integer first, final Integer max, final String order, final boolean asc);

	public Long contarAelQuestaoSuggestion(final Object objPesquisa);
	
	public void persistirAelQuestoesQuestionario(final AelQuestoesQuestionario questoesQuestionario) throws ApplicationBusinessException, BaseException;
	
	public List<AelQuestoesQuestionario> buscarAelQuestoesQuestionarioPorQuestionario(final Integer qtnSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	AelQuestoesQuestionario obterAelQuestoesQuestionario(final AelQuestoesQuestionarioId id);
	
	public void excluirAelQuestoesQuestionario(final AelQuestoesQuestionarioId id) throws ApplicationBusinessException;

	public List<AelGrupoQuestao> buscarAelGrupoQuestaoSuggestion(final Object objPesquisa, final Integer first, final Integer max, final String order, final boolean asc);

	public Long contarAelGrupoQuestaoSuggestion(final Object objPesquisa);
	
	public Long contarBuscarAelQuestoesQuestionarioPorQuestionario(Integer qtnSeq);
	
	public Integer obterProximoSequencialAelQuestionariosConvUnid();

	public AelQuestionariosConvUnid obterPorChavePrimaria(AelQuestionariosConvUnidId id);
	
	public List<AelQuestionarios> pesquisarQuestionarioPorItemSolicitacaoExame(final String sigla, final Integer manSeq, final Short codigoConvenioSaude, final DominioOrigemAtendimento origem, final DominioTipoTransporteQuestionario tipoTransporte);
	
	public List<AelQuestionariosConvUnid> buscarAelQuestionariosConvUnidPorQuestionario(Integer seqQuestionario,
														Integer firstResult,Integer maxResult, String orderProperty, boolean asc);
	
	public Long buscarAelQuestionariosConvUnidPorQuestionarioCount(Integer seqQuestionario);

	public List<AelRespostaQuestao> criarRespostasQuestionario(List<AelQuestionarios> questionarios);
	
	public List<InformacaoComplementarVO> pesquisarInformacoesComplementares(Integer pacCodigo, Integer soeSeq, Short seqp, Integer qtnSeq) throws ApplicationBusinessException;

	public List<AelQuestionariosConvUnid> buscarAelQuestionariosConvUnidPorQuestionario(Integer seqQuestionario);

	public void validarEnderecoPaciente(Integer pacCodigo) throws ApplicationBusinessException;
	
	public Byte obterNumeroViasImpressaoInformacoesComplementares(Integer soeSeq, Short seqp, Integer qtnSeq);

	public void persistirAelRespostaQuestao(final AelRespostaQuestao aelRespostaQuestao) throws ApplicationBusinessException;
	
	Integer obterPacCodigoPelaSolicitacao(Integer soeSeq);
	
	public List<AelValorValidoQuestao> buscarValoresValidosAtivosQuestaoPorQuestao(final Integer seq);

	public Long pesquisarProjetosPesquisaPorNumeroOuNomeCount(String parametro);

	public Long obterCadCtrlQualidadesListCount(String parametro);

	public Long obterDadosCadaveresListCount(String parametro);

	Boolean verificarSuggestionCidSeraExibidaNoQuestionarioExame(
			ItemSolicitacaoExameVO itemSolicitacaoExameVo)
			throws ApplicationBusinessException;
	
}
package br.gov.mec.aghu.exameselaudos.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioRecomendacaoMamografia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioSituacaoProjetoPesquisa;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;

public interface IExamesLaudosFacade extends Serializable {

	public DominioOrigemAtendimento buscaLaudoOrigemPaciente(Integer soeSeq);

	public DominioOrigemAtendimento buscaLaudoOrigemPacienteRN(Integer soeSeq);

	public String buscaJustificativaLaudo(Integer seqAtendimento, Integer phiSeq);

	public List<BuscarResultadosExamesVO> buscarResultadosExames(
			Integer pacCodigo, Date dataLiberacao, String nomeCampo)
			throws ApplicationBusinessException;

	public String pesquisarProtocolosPorPacienteString(Integer codigo);

	/**
	 * 14/02/2012 Autorizado a chamada pela Consultora Milena (CGTI), enquanto
	 * essa função não é totalmente implementada no AGH Atualmente não tem
	 * suporte no caso de existirem consultas vinculadas ao paciente, ocorrendo
	 * violação de FK.
	 */
	public void removerProtocolosNeurologia(Integer codigo)
			throws ApplicationBusinessException;
	
	public AelProjetoPesquisas obterProjetoPesquisa(Integer seq);

	public AelProjetoIntercorrenciaInternacao obterProjetoIntercorrenciaInternacao(
			Integer codigo, Integer seq, Short object);
	
	public void persistirProjetoIntercorrenciaInternacao(
			AelProjetoIntercorrenciaInternacao projetoIntercorrenciaInternacao);

	public void excluirAelPacienteUnidadeFuncional(AelPacUnidFuncionais elemento) throws ApplicationBusinessException;
	
	public void inserirAelPacienteUnidadeFuncional(AelPacUnidFuncionais puf)
			throws ApplicationBusinessException;

	public void excluirAelProtocoloInternoUnids(AelProtocoloInternoUnids piuOrigem) throws BaseException;

	public void inserirAelProtocoloInternoUnids(AelProtocoloInternoUnids piu) throws ApplicationBusinessException;

	public void atualizarSolicitacaoExame(AelSolicitacaoExames solicitacaoExame);

	/**
	 * Obtém um AelItemSolicitacaoExames pela chave primária
	 * 
	 * @param chavePrimaria
	 * @return
	 */
	public AelItemSolicitacaoExames obterAelItemSolicitacaoExamesPorChavePrimaria(
			AelItemSolicitacaoExamesId chavePrimaria);

	public AelExamesNotificacao obterAelExamesNotificacaoPorChavePrimaria(
			AelExamesNotificacaoId chavePrimaria);

	public Long listarSolicitacoesExamesCount(Integer numeroConsulta);

	public Long listarSolicitacoesExamesCount(Integer numeroConsulta,
			String codigoSituacaoItemSolicitacaoExame);

	public AelProjetoPesquisas obterAelProjetoPesquisasPorChavePrimaria(
			Integer seq);

	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			String strPesquisa, Integer codigoPaciente);

	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacaoNumero(
			String strPesquisa, DominioSituacaoProjetoPesquisa[] dominios);

	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacaoNomeOuDescricao(
			String strPesquisa, DominioSituacaoProjetoPesquisa[] dominios);

	public List<AelSolicitacaoExames> listarSolicitacoesExames(
			Integer seqAtendimento, Date dataMaiorIgualCriadoEm);

	public List<AelProtocoloInternoUnids> pesquisarAelProtocoloInternoUnids(
			Integer pacCodigo);

	public void removerAelProtocoloInternoUnids(
			AelProtocoloInternoUnids aelProtocoloInternoUnids, boolean flush);

	public List<AelSolicitacaoExames> pesquisarSolicitacaoExamePorAtendimento(
			Integer seqAtendimento, List<String> parametros);

	public AelSolicitacaoExames obterAelSolicitacaoExamesPorChavePrimaria(
			Integer seq);

	public List<AelRespostaQuesitos> listarRespostasQuisitorPorDroPpjPacCodigo(
			Integer droPpjPacCodigo);

	public void inserirAelRespostaQuesitos(
			AelRespostaQuesitos aelRespostaQuesitos, boolean flush);

	public void removerAelRespostaQuesitos(
			AelRespostaQuesitos aelRespostaQuesitos);

	public List<AelProjetoPacientes> listarProjetosPaciente(
			Integer codigoPaciente);

	public void persistirAelProjetoPacientes(
			AelProjetoPacientes aelProjetoPacientes);
	
	List<Integer> pesquisarCProjPac(final Integer codigoPaciente, final Date dataTruncada);
	
	Boolean verificarExisteCProjPac(final Integer codigoPaciente, final Date dataTruncada);	

	public void removerAelProjetoPacientes(
			AelProjetoPacientes aelProjetoPacientes);

	public Integer listarMaxNumeroControleExamePaciente(Integer pacCodigo);

	public List<AelPacUnidFuncionais> pesquisarAelPacUnidFuncionais(
			Integer pacCodigo);

	public List<Integer> listarAxeSeqsPacAgrpPesq(Integer pacCodigo);

	public List<AelPacAgrpPesqExames> listarPacAgrpPesqExames(
			Integer pacCodigo, Integer[] axeSeqs);

	public List<AelPacAgrpPesqExames> listarPacAgrpPesqExamesPorCodigoPaciente(
			Integer pacCodigo);

	public void removerAelPacAgrpPesqExames(
			AelPacAgrpPesqExames aelPacAgrpPesqExames, boolean flush);

	public void persistirAelPacAgrpPesqExames(
			AelPacAgrpPesqExames aelPacAgrpPesqExames);

	public List<AelProjetoPacientes> pesquisarProjetosDePesquisaPorPaciente(
			AipPacientes paciente);

	public List<AelDataRespostaProtocolos> listarDatasRespostasProtocolosPorPpjPacCodigo(
			Integer ppjPacCodigo);

	public void inserirAelDataRespostaProtocolos(
			AelDataRespostaProtocolos aelDataRespostaProtocolos, boolean flush);

	public void removerAelDataRespostaProtocolos(
			AelDataRespostaProtocolos aelDataRespostaProtocolos);

	public List<AelAtendimentoDiversos> pesquisarAtendimentosDiversorPorPacCodigo(
			Integer pacCodigo);

	
	Long pesquisarAelAtendimentoDiversosCount(
			final AelAtendimentoDiversos filtros);

	
	List<AelAtendimentoDiversos> pesquisarAelAtendimentoDiversos(
			final AelAtendimentoDiversos filtros, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	
	AelAtendimentoDiversos obterAelAtendimentoDiversosPorId(final Integer seq);

	public void atualizarAelAtendimentoDiversos(
			AelAtendimentoDiversos aelAtendimentoDiversos, boolean flush);

	public List<AelProtocoloInternoUnids> listarProtocolosInternosUnids(
			AipPacientes paciente, Date criadoEm);

	public List<AelItemSolicitacaoExames> buscaItemSolicitacaoExamesComRespostaQuestao(
			final List<Integer> listaSoeSeq);

	public List<AelProjetoPacientes> pesquisarProjetosPesquisaPacientePOL(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, Integer matricula, Short vinCodigo);

	public Long pesquisarProjetosPesquisaPacientePOLCount(Integer codigo,
			Integer matricula, Short vinCodigo);

	Date buscaMaiorDataRecebimento(Integer itemSolicitacaoExameSoeSeq,
			Short itemSolicitacaoExameSeqp, String situacaoItemSolicitacaoCodigo);

	Date buscaMaiorDataLiberacao(Integer soeSeq, Short unidadeFuncionalSeqp);

	Boolean verificarSePacientePossuiDadoHistoricoPOLAghAtendimento(
			Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame);

	Boolean verificarSePacientePossuiDadoHistoricoPOLAelAtdDiverso(
			Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame);

	Boolean verificarSePacientePossuiDadoHistoricoPOLAipMdtoHist(
			Integer codigoPaciente, DominioSituacaoItemSolicitacaoExame sitExame);

	List<AelResultadoExame> listarResultadoVersaoLaudo(Integer iseSoeSeq,
			Short iseSeqp);
	
	public List<ExameNotificacaoVO> pesquisarExamesNotificacao(
			Integer firstResult, Integer maxResults, String orderProperty,
			Boolean asc, String exaSigla, Integer manSeq, Integer calSeq,
			DominioSituacao situacao);
	
	public Long pesquisarExamesNotificacaoCount(String exaSigla,
			Integer manSeq, Integer calSeq, DominioSituacao situacao);

	public List<VAelExameMatAnalise> pesquisarVExamesMaterialAnalise(
			final Object objPesquisa);

	public Long pesquisarVExamesMaterialAnaliseCount(final Object objPesquisa);

	public List<AelProtocoloInternoUnids> pesquisarProtocoloInterno(
			Short unfSeq, Integer numeroProtocolo, Integer codigoPaciente,
			Integer prontuario, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public Long pesquisarProtocoloInternoCount(Short unfSeq,
			Integer numeroProtocolo, Integer codigoPaciente, Integer prontuario);

	public AelProtocoloInternoUnids obterProtocoloInterno(
			Integer codigoPaciente, Short unfSeq, Integer numeroProtocolo);

	public List<AelPacUnidFuncionais> listarUnidadesFuncionaisPaciente(
			Integer codigoPaciente, Short unfSeq);

	public void inserirExameProtocolado(AelPacUnidFuncionais elemento) throws ApplicationBusinessException;

	public void atualizarExameProtocolado(AelPacUnidFuncionais elemento)
			throws ApplicationBusinessException;

	String obterRespostaMamo(AelItemSolicitacaoExames ise);

	List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExames(Integer soeSeq, Short ufeUnfSeq) throws ApplicationBusinessException;

	DominioRecomendacaoMamografia onChangeCategoria(Map<String, AelSismamaMamoResVO> mapAbaConclusao, boolean mamaDireita);
	
	List<VAelSerSismama> pesquisarResponsavel(Object obj);
	
	Long pesquisarResponsavelCount(Object obj);
	
	List<RapServidores> pesquisarResidente(Object obj) throws ApplicationBusinessException;
	
	Long pesquisarResidenteCount(Object obj) throws ApplicationBusinessException;
	
	Boolean habilitarBotaoQuestaoSismama(Map<Integer, Vector<Short>> solicitacoes, Boolean false1);

	void reabrirLaudo(String situacaoAreaExecutora, String situacaoLiberado, Integer solicitacao, Short item);
	
	Integer assinarLaudo(String situacaoLiberado, Integer solicitacao, Short item, Map<String, AelSismamaMamoResVO> mapMamaD, 
			Map<String, AelSismamaMamoResVO> mapMamaE, Map<String, AelSismamaMamoResVO> mapAbaConclusao, 
			String rxMamaBilateral, String sexoPaciente, String habilitaMamaEsquerda, String habilitaMamaDireita, 
			String nomeResponsavel, Integer matricula, Integer vinCodigo, 
			String infoClinicas, String nomeMicrocomputador) throws BaseException, BaseListException;
	
	Boolean exibirModalAssinarLaudo(String situacaoAreaExecutora, String situacaoExecutando, Integer solicitacao, Short item);
	
	Boolean exibirModalReabrirLaudo(String situacaoLiberado, Integer solicitacao, Short item);
	
	void validarResponsavel(Integer matricula, Integer vinCodigo) throws ApplicationBusinessException ;
	
	Map<String, Object> montarControleAbas(Short iseSeqpLida, Integer solicitacao, Short item, String rxMamaEsquerda, String rxMamaDireita);
	
	Map<String, Boolean> montarControleTela(Integer solicitacao, Short item, String situacaoLiberado, String situacaoAreaExecutora, 
			String situacaoExecutando, String habilitaMamaDireita, String habilitaMamaEsquerda);
	
	Map<String, AelSismamaMamoResVO> inicializarMapDireita();
	Map<String, AelSismamaMamoResVO> inicializarMapEsquerda();
	Map<String, AelSismamaMamoResVO> inicializarMapConclusao();
	
	String obterDadosInformacaoClinica(Integer solicitacao, Short item);
	
	List<AelSismamaMamoRes> obterRespostasMamo(Integer soeSeq, Short seqp);
	
	void obterInformacoesMama(Integer solicitacao, Short item,
			String habilitaMamaDireita,
			Map<String, AelSismamaMamoResVO> mapMamaD,
			String habilitaMamaEsquerda,
			Map<String, AelSismamaMamoResVO> mapMamaE,
			Map<String, AelSismamaMamoResVO> mapConclusao);
	
	List<AelSismamaMamoRes> obterRespostasMamografiaRespNull(Integer soeSeq, Short seqp);

	Map<String, String> obterON2VerificaQuestoesSismama(Map<String, String> mapResposta);
	
	public void salvarDados(AelItemSolicitacaoExames aelItemSolicitacaoExames, 
			 Map<String, AelSismamaMamoResVO> mapMamaD, 
			 Map<String, AelSismamaMamoResVO> mapMamaE, 
			 Map<String, AelSismamaMamoResVO> mapConclusao,
			 String rxMamaBilateral,
			 String vSexoPaciente,
			 boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita,
			 String medicoResponsavel, String infoClinicas) throws ApplicationBusinessException,BaseListException;
	
	VAelSerSismama obterResponsavelResultadoMamografia(Integer solicitacao, Short item,
			String habilitaMamaDireita,
			Map<String, AelSismamaMamoResVO> mapMamaD) throws ApplicationBusinessException;

	VRapPessoaServidor obterResidenteResultadoMamografia(Integer solicitacao, Short item) throws ApplicationBusinessException;
	
	Boolean verificarResidenteConectadoResultadoMamografia() throws ApplicationBusinessException;

	Long pesquisarProjetosDePesquisaPorPacienteCount(AipPacientes paciente);

	public Map<String, String> preencherQuestionarioSisMama(
			Integer codSolicitacao, Short codItemSolicitacao, Boolean isHist);

	public List<AelSismamaMamoResHist> obterRespostasMamoHist(
			Integer codSolicitacao, Short codItemSolicitacao);

	public List<AelSismamaMamoResHist> obterRespostasMamografiaHistRespNull(Integer soeSeq, Short seqp);

	public Date buscaMaiorDataLiberacaoHist(Integer seqSolicitacaoExame,
			Short unfSeq);
	
	void verificaLaudoPatologia(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist);		

	Boolean verificarUnidadeFuncionalTemCaracteristica(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica);

	String obterTipoExamePatologico(Long numeroAp, Integer lu2Seq);
	
	public AelExamesNotificacao retornaExameNotificacaoPorId(AelExamesNotificacaoId id);
	
	List<String> montaRecebimentoPatologia(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException;

	void verificaLaudoPatologia(List<AelItemSolicitacaoExames> listaSolics, Boolean isHist);
	
	AelAtendimentoDiversos obterAtendimentoDiversoComPaciente(Integer seq);
}

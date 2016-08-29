package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.exames.vo.AelpCabecalhoLaudoVO;
import br.gov.mec.aghu.exames.vo.DataLiberacaoVO;
import br.gov.mec.aghu.exames.vo.DataRecebimentoSolicitacaoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExtratoItemSolicHist;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaHist;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelRefCode;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;

public interface IMascaraExamesFacade {

	List<AelRefCode> obterCodigosPorDominioELowValue(String dominio, String lowValue);

	Map<AelParametroCamposLaudo, AelResultadoExame> obterMapaResultadosFicticiosExames(
			List<AelParametroCamposLaudo> camposDaVersaoLaudoPrevia);

	List<AelItemSolicExameHist> pesquisarItensSolicitacoesExamesHist(Integer soeSeq, Short seqp);

	List<AelItemSolicitacaoExames> pesquisarItensSolicitacoesExames(Integer soeSeq, Short seqp);

	AelVersaoLaudo pesquisarItensSolicitacoesExames(AelExames exame, AelMateriaisAnalises materialAnalise, Integer velSeqp);

	List<AelParametroCamposLaudo> pesquisarCamposTelaPorVersaoLaudo(AelVersaoLaudo versaoLaudo);

	List<AelParametroCamposLaudo> pesquisarCamposRelatorioPorVersaoLaudo(AelVersaoLaudo versaoLaudo);

	List<AelInformacaoColeta> listarInformacoesPorSoeSeq(Integer soeSeq);

	List<AelInformacaoColetaHist> listarInformacoesPorSoeSeqHist(Integer soeSeq);

	AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(AelItemSolicitacaoExamesId id);

	AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(Integer soeSeq, Short seqp);

	AelVersaoLaudo obterVersaoLaudoPorEmaExaSiglaEManSeq(String exaSigla, Integer manSeq, Integer velSeqp);

	Date buscaMaiorDataRecebimento(Integer itemSolicitacaoExameSoeSeq, Short itemSolicitacaoExameSeqp, String situacaoItemSolicitacaoCodigo);

	List<AelResultadoCodificado> pesquisarResultadosCodificadosPorCampoLaudo(AelCampoLaudo campoLaudo);

	List<AelExameGrupoCaracteristica> pesquisarExameGrupoCarateristicaPorCampo(AelParametroCamposLaudo parametroCampoLaudo);

	String buscaInformacaoEquipamento(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq);

	String buscaInformacaoRecebimento(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException;

	String buscaInformacaoRecebimentoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException;

	List<String> buscaInformacaoHistorico(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, Integer campoLaudoSeq)
			throws BaseException;

	AelExtratoItemSolicitacao obterUltimoItemSolicitacaoSitCodigo(Integer soeSeq, Short seqp, String sitCodigo);

	String buscaInformacaoMetodo(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException;

	String buscaInformacaoMetodoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException;

	String buscaInformacaoValoresReferencia(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, AelParametroCamposLaudo campo)
			throws BaseException;

	Map<AelParametroCampoLaudoId, Object> obterMapaResultadosExames(Object itemSolicitacaoExameObject);

	List<AelParametroCamposLaudo> obterListaCamposExibicao(Object itemSolicitacaoExameObject,
			Map<AelParametroCamposLaudo, Object> resultados, List<AelParametroCamposLaudo> camposDaVersao, Boolean isHist);

	void processarDescricaoConvenioPaciente(AelpCabecalhoLaudoVO cabecalhoLaudo, Object itemSolicitacaoExameObject);

	String recuperaLeito(Object itemSolicitacaoExameObject);

	Map<AelParametroCampoLaudoId, AelResultadoExame> obterMapaResultadosPadraoExames(AelResultadosPadrao resultPadrao);

	String montaNomeExameCabecalho(Object itemSolicitacaoExameObject, boolean isPrevia);

	Integer obterIdadePaciente(Object itemSolicitacaoExameObject);

	String obterSexoPaciente(Object itemSolicitacaoExameObject);

	void verificaSeMostraSeloDeAcreditacao(AelpCabecalhoLaudoVO cabecalhoLaudo, Object itemSolicitacaoExameObject);

	String buscaInformacaoValoresReferenciaHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, AelParametroCamposLaudo campo)
			throws BaseException;

	AelVersaoLaudo obterVersaoLaudoPorItemSolicitacaoExame(AelExames exame, AelMateriaisAnalises materialAnalise, Integer velSeqp);

	AelExtratoItemSolicHist obterUltimoItemSolicitacaoSitCodigoHist(Integer soeSeq, Short seqp, String sitCodigo);

	String buscaNomeMedicoSolicitante(Object itemSolicitacaoExameObject);

	List<DataLiberacaoVO> listarDataLiberacaoTipoExamePatologico(Long numeroAp, Integer lu2Seq);

	List<DataRecebimentoSolicitacaoVO> listarDataRecebimentoTipoExamePatologico(Long numeroAp, Integer lu2Seq, String situacao);

	boolean existemInformacoesColeta(Integer soeSeq, Short seqp, List<Short> seqps);

	void desatacharAelParametroCamposLaudo(AelParametroCamposLaudo elemento);
}

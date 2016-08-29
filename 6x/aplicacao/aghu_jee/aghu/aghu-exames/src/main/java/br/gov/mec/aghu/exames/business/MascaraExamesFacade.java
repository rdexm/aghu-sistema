package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicHistDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicExameHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
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

@Stateless
public class MascaraExamesFacade extends BaseFacade implements IMascaraExamesFacade {

	private static final long serialVersionUID = 3672907393345578671L;

	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

	@Inject
	private AelInformacaoColetaDAO aelInformacaoColetaDAO;

	@Inject
	private AelInformacaoColetaHistDAO aelInformacaoColetaHistDAO;

	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;

	@Inject
	private AelRefCodeDAO aelRefCodeDAO;

	@Inject
	private AelVersaoLaudoDAO aelVersaoLaudoDAO;

	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelItemSolicExameHistDAO aelItemSolicExameHistDAO;

	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;

	@Inject
	private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;

	@Inject
	private AelExtratoItemSolicHistDAO aelExtratoItemSolicHistDAO;

	@EJB
	private MascaraExamesON mascaraExamesON;

	@EJB
	private MascaraExamesRN mascaraExamesRN;

	@Override
	public List<AelRefCode> obterCodigosPorDominioELowValue(String dominio, String lowValue) {
		return aelRefCodeDAO.obterCodigosPorDominioELowValue(dominio, lowValue);
	}

	@Override
	public Map<AelParametroCamposLaudo, AelResultadoExame> obterMapaResultadosFicticiosExames(
			List<AelParametroCamposLaudo> camposDaVersaoLaudoPrevia) {
		return mascaraExamesON.obterMapaResultadosFicticiosExames(camposDaVersaoLaudoPrevia);
	}

	@Override
	public List<AelItemSolicExameHist> pesquisarItensSolicitacoesExamesHist(Integer soeSeq, Short seqp) {
		return aelItemSolicExameHistDAO.pesquisarItensSolicitacoesExames(soeSeq, seqp);
	}

	@Override
	public List<AelItemSolicitacaoExames> pesquisarItensSolicitacoesExames(Integer soeSeq, Short seqp) {
		return aelItemSolicitacaoExameDAO.pesquisarItensSolicitacoesExames(soeSeq, seqp);
	}

	@Override
	public AelVersaoLaudo pesquisarItensSolicitacoesExames(AelExames exame, AelMateriaisAnalises materialAnalise, Integer velSeqp) {
		return aelVersaoLaudoDAO.obterVersaoLaudoPorItemSolicitacaoExame(exame, materialAnalise, velSeqp);
	}

	@Override
	public List<AelParametroCamposLaudo> pesquisarCamposTelaPorVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		return aelParametroCamposLaudoDAO.pesquisarCamposTelaPorVersaoLaudo(versaoLaudo);
	}

	@Override
	public void desatacharAelParametroCamposLaudo(AelParametroCamposLaudo elemento) {
		aelParametroCamposLaudoDAO.desatachar(elemento);
	}

	@Override
	public List<AelParametroCamposLaudo> pesquisarCamposRelatorioPorVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		return aelParametroCamposLaudoDAO.pesquisarCamposRelatorioPorVersaoLaudo(versaoLaudo);
	}

	@Override
	public List<AelInformacaoColetaHist> listarInformacoesPorSoeSeqHist(Integer soeSeq) {
		return aelInformacaoColetaHistDAO.listarInformacoesPorSoeSeq(soeSeq);
	}

	@Override
	public List<AelInformacaoColeta> listarInformacoesPorSoeSeq(Integer soeSeq) {
		return aelInformacaoColetaDAO.listarInformacoesPorSoeSeq(soeSeq);
	}

	@Override
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(AelItemSolicitacaoExamesId id) {
		return aelItemSolicitacaoExameDAO.obterItemSolicitacaoExamePorId(id);
	}

	@Override
	public AelItemSolicitacaoExames obterItemSolicitacaoExamePorId(final Integer soeSeq, final Short seqp) {
		return aelItemSolicitacaoExameDAO.obterItemSolicitacaoExamePorId(soeSeq, seqp);
	}

	@Override
	public AelVersaoLaudo obterVersaoLaudoPorEmaExaSiglaEManSeq(final String exaSigla, final Integer manSeq, Integer velSeqp) {
		return aelVersaoLaudoDAO.obterVersaoLaudoPorEmaExaSiglaEManSeq(exaSigla, manSeq, velSeqp);
	}

	@Override
	public AelVersaoLaudo obterVersaoLaudoPorItemSolicitacaoExame(AelExames exame, AelMateriaisAnalises materialAnalise, Integer velSeqp) {
		return aelVersaoLaudoDAO.obterVersaoLaudoPorItemSolicitacaoExame(exame, materialAnalise, velSeqp);
	}

	@Override
	public Date buscaMaiorDataRecebimento(final Integer itemSolicitacaoExameSoeSeq, final Short itemSolicitacaoExameSeqp,
			final String situacaoItemSolicitacaoCodigo) {
		return aelExtratoItemSolicitacaoDAO.buscaMaiorDataRecebimento(itemSolicitacaoExameSoeSeq, itemSolicitacaoExameSeqp,
				situacaoItemSolicitacaoCodigo);
	}

	@Override
	public List<AelResultadoCodificado> pesquisarResultadosCodificadosPorCampoLaudo(AelCampoLaudo campoLaudo) {
		return aelResultadoCodificadoDAO.pesquisarResultadosCodificadosPorCampoLaudo(campoLaudo);
	}

	@Override
	public List<AelExameGrupoCaracteristica> pesquisarExameGrupoCarateristicaPorCampo(AelParametroCamposLaudo parametroCampoLaudo) {
		return aelExameGrupoCaracteristicaDAO.pesquisarExameGrupoCarateristicaPorCampo(parametroCampoLaudo);
	}

	@Override
	public String buscaInformacaoEquipamento(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) {
		return mascaraExamesRN.buscaInformacaoEquipamento(solicitacaoExameSeq, itemSolicitacaoExameSeq);
	}

	@Override
	public String buscaInformacaoRecebimento(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		return mascaraExamesRN.buscaInformacaoRecebimento(solicitacaoExameSeq, itemSolicitacaoExameSeq);
	}

	@Override
	public String buscaInformacaoRecebimentoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		return mascaraExamesRN.buscaInformacaoRecebimentoHist(solicitacaoExameSeq, itemSolicitacaoExameSeq);
	}

	@Override
	public List<String> buscaInformacaoHistorico(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, Integer campoLaudoSeq)
			throws BaseException {
		return mascaraExamesRN.buscaInformacaoHistorico(solicitacaoExameSeq, itemSolicitacaoExameSeq, campoLaudoSeq);
	}

	@Override
	public AelExtratoItemSolicitacao obterUltimoItemSolicitacaoSitCodigo(final Integer soeSeq, final Short seqp, final String sitCodigo) {
		return aelExtratoItemSolicitacaoDAO.obterUltimoItemSolicitacaoSitCodigo(soeSeq, seqp, sitCodigo);
	}

	@Override
	public String buscaInformacaoMetodo(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		return mascaraExamesRN.buscaInformacaoMetodo(solicitacaoExameSeq, itemSolicitacaoExameSeq);
	}

	@Override
	public String buscaInformacaoMetodoHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq) throws BaseException {
		return mascaraExamesRN.buscaInformacaoMetodoHist(solicitacaoExameSeq, itemSolicitacaoExameSeq);
	}

	@Override
	public String buscaInformacaoValoresReferencia(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq, AelParametroCamposLaudo campo)
			throws BaseException {
		return mascaraExamesRN.buscaInformacaoValoresReferencia(solicitacaoExameSeq, itemSolicitacaoExameSeq, campo);
	}

	@Override
	public String buscaInformacaoValoresReferenciaHist(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq,
			AelParametroCamposLaudo campo) throws BaseException {
		return mascaraExamesRN.buscaInformacaoValoresReferenciaHist(solicitacaoExameSeq, itemSolicitacaoExameSeq, campo);
	}

	@Override
	public Map<AelParametroCampoLaudoId, Object> obterMapaResultadosExames(Object itemSolicitacaoExameObject) {
		return mascaraExamesON.obterMapaResultadosExames(itemSolicitacaoExameObject);
	}

	@Override
	public List<AelParametroCamposLaudo> obterListaCamposExibicao(Object itemSolicitacaoExameObject,
			Map<AelParametroCamposLaudo, Object> resultados, List<AelParametroCamposLaudo> camposDaVersao, Boolean isHist) {
		return mascaraExamesON.obterListaCamposExibicao(itemSolicitacaoExameObject, resultados, camposDaVersao, isHist);
	}

	@Override
	public void verificaSeMostraSeloDeAcreditacao(AelpCabecalhoLaudoVO cabecalhoLaudo, Object itemSolicitacaoExameObject) {
		mascaraExamesON.verificaSeMostraSeloDeAcreditacao(cabecalhoLaudo, itemSolicitacaoExameObject);
	}

	@Override
	public void processarDescricaoConvenioPaciente(AelpCabecalhoLaudoVO cabecalhoLaudo, Object itemSolicitacaoExameObject) {
		mascaraExamesON.processarDescricaoConvenioPaciente(cabecalhoLaudo, itemSolicitacaoExameObject);
	}

	@Override
	public String recuperaLeito(final Object itemSolicitacaoExameObject) {
		return mascaraExamesON.recuperaLeito(itemSolicitacaoExameObject);
	}

	@Override
	public Map<AelParametroCampoLaudoId, AelResultadoExame> obterMapaResultadosPadraoExames(AelResultadosPadrao resultPadrao) {
		return mascaraExamesON.obterMapaResultadosPadraoExames(resultPadrao);
	}

	@Override
	public String montaNomeExameCabecalho(final Object itemSolicitacaoExameObject, boolean isPrevia) {
		return mascaraExamesON.montaNomeExameCabecalho(itemSolicitacaoExameObject, isPrevia);
	}

	@Override
	public Integer obterIdadePaciente(Object itemSolicitacaoExameObject) {
		return mascaraExamesON.obterIdadePaciente(itemSolicitacaoExameObject);
	}

	@Override
	public String obterSexoPaciente(Object itemSolicitacaoExameObject) {
		return mascaraExamesON.obterSexoPaciente(itemSolicitacaoExameObject);
	}

	@Override
	public AelExtratoItemSolicHist obterUltimoItemSolicitacaoSitCodigoHist(final Integer soeSeq, final Short seqp, final String sitCodigo) {
		return aelExtratoItemSolicHistDAO.obterUltimoItemSolicitacaoSitCodigo(soeSeq, seqp, sitCodigo);
	}

	@Override
	public String buscaNomeMedicoSolicitante(Object itemSolicitacaoExameObject) {
		return mascaraExamesON.buscaNomeMedicoSolicitante(itemSolicitacaoExameObject);
	}

	@Override
	public List<DataLiberacaoVO> listarDataLiberacaoTipoExamePatologico(Long numeroAp, Integer lu2Seq) {
		return this.aelItemSolicitacaoExameDAO.listarDataLiberacaoTipoExamePatologico(numeroAp, lu2Seq);
	}

	@Override
	public List<DataRecebimentoSolicitacaoVO> listarDataRecebimentoTipoExamePatologico(Long numeroAp, Integer lu2Seq, String situacao) {
		return this.aelItemSolicitacaoExameDAO.listarDataRecebimentoTipoExamePatologico(numeroAp, lu2Seq, situacao);
	}

	@Override
	public boolean existemInformacoesColeta(Integer soeSeq, Short seqp, List<Short> seqps) {
		return this.aelTipoAmostraExameDAO.existemInformacoesColeta(soeSeq, seqp, seqps);
	}

}
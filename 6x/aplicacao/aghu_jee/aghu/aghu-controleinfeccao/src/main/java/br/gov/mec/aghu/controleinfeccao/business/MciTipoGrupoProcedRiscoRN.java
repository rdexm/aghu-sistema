package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciProcedimentoRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTipoGrupoProcedRiscoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTipoGrupoProcedRiscoJnDAO;
import br.gov.mec.aghu.controleinfeccao.vo.ProcedRiscoVO;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRiscoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MciTipoGrupoProcedRiscoRN extends BaseBusiness {

	private static final long serialVersionUID = 9143822763028473543L;

	private static final Log LOG = LogFactory
			.getLog(MciTipoGrupoProcedRiscoRN.class);

	@Inject
	private MciTipoGrupoProcedRiscoDAO mciTipoGrupoProcedRiscoDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private MciProcedimentoRiscoDAO mciProcedRiscoDAO;

	@Inject
	private MciTipoGrupoProcedRiscoJnDAO mciTipoGrupoProcedRiscoJnDAO;

	private enum TipoGrupoProcedimentoRiscoRNException implements
			BusinessExceptionCode {
		AGPR_MENSAGEM_PERIODO, AGPR_MENSAGEM_RESTRICAO_ALTERACAO, AGPR_MENSAGEM_DADOS_INCOMPLETOS, AGPR_MENSAGEM_LISTA_VAZIA, AGPR_MENSAGEM_RESTRICAO_EXCLUSAO;

	}

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}

	// RN3
	public void inserirTipoGrupoProcedimentoRisco(
			final MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		validarDescricaoMciTipoGrupoProcedRisco(entidade);
		entidade.setRapServidoresByMciTgpSerFk1(usuarioLogado);
		entidade.setCriadoEm(new Date());
		getMciTipoGrupoProcedRiscoDAO().persistir(entidade);
		registrarOperacaoJournal(DominioOperacoesJournal.INS, entidade,
				usuarioLogado);
	}

	private void registrarOperacaoJournal(
			final DominioOperacoesJournal operacao,
			final MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) {
		MciTipoGrupoProcedRiscoJn entidadeJournal = criarMciTipoGrupoProcedRiscoJn(
				operacao, entidade, usuarioLogado);
		inserirMciTipoGrupoProcedRiscoJn(entidadeJournal);
	}

	// RN1
	public void excluirMciTipoGrupoProcedRisco(
			MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado)
			throws ApplicationBusinessException {

		if (!isDentroPrazoExclusao(entidade.getCriadoEm())) {
			throw new ApplicationBusinessException(
					TipoGrupoProcedimentoRiscoRNException.AGPR_MENSAGEM_PERIODO);
		}
		List<ProcedRiscoVO> gruposProcedRisco = getMciProcedRiscoDAO()
				.pesquisarMciProcedRiscoPorSeqTipoGrupo(entidade.getSeq());

		if (isRegistrosAssociados(gruposProcedRisco)) {
			throw new ApplicationBusinessException(
					TipoGrupoProcedimentoRiscoRNException.AGPR_MENSAGEM_RESTRICAO_EXCLUSAO,
					buscarDescricaoItensAssociados(gruposProcedRisco));

		} else {
			registrarOperacaoJournal(DominioOperacoesJournal.DEL, entidade,
					usuarioLogado);
			MciTipoGrupoProcedRisco original = getMciTipoGrupoProcedRiscoDAO()
					.obterPorChavePrimaria(entidade.getSeq());
			getMciTipoGrupoProcedRiscoDAO().remover(original);
		}
	}

	private String buscarDescricaoItensAssociados(List<ProcedRiscoVO> itens) {
		StringBuilder descricoes = new StringBuilder();
		for (ProcedRiscoVO mciProcedimentoRisco : itens) {
			descricoes.append(mciProcedimentoRisco.getDescricao());
			if (itens.indexOf(mciProcedimentoRisco) < (itens.size() - 1)) {
				descricoes.append(", ");
			}
		}
		return descricoes.toString().concat(".");
	}

	// RN2
	public void alterarMciTipoGrupoProcedRisco(
			MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		validarDescricaoMciTipoGrupoProcedRisco(entidade);
		entidade.setRapServidoresByMciTgpSerFk2(usuarioLogado);
		entidade.setAlteradoEm(new Date());
		if (isAlterandoDescricao(entidade)) {
			throw new ApplicationBusinessException(
					TipoGrupoProcedimentoRiscoRNException.AGPR_MENSAGEM_RESTRICAO_ALTERACAO);
		}
		entidade.setAlteradoEm(new Date());
		registrarOperacaoJournal(DominioOperacoesJournal.UPD, entidade,
				usuarioLogado);
		getMciTipoGrupoProcedRiscoDAO().merge(entidade);
	}

	private void validarDescricaoMciTipoGrupoProcedRisco(
			MciTipoGrupoProcedRisco entidade)
			throws ApplicationBusinessException {
		if (StringUtils.isEmpty(entidade.getDescricao())) {
			throw new ApplicationBusinessException(
					TipoGrupoProcedimentoRiscoRNException.AGPR_MENSAGEM_DADOS_INCOMPLETOS);
		}
	}

	private boolean isAlterandoDescricao(MciTipoGrupoProcedRisco entidade) {
		MciTipoGrupoProcedRisco original = getMciTipoGrupoProcedRiscoDAO()
				.obterPorChavePrimaria(entidade.getSeq());
		if (original == null) {
			return false;
		}
		return !(original.getDescricao().equals(entidade.getDescricao()));
	}

	private void inserirMciTipoGrupoProcedRiscoJn(
			MciTipoGrupoProcedRiscoJn entidade) {
		getMciTipoGrupoProcedRiscoJnDAO().persistir(entidade);
	}

	private MciTipoGrupoProcedRiscoJn criarMciTipoGrupoProcedRiscoJn(
			DominioOperacoesJournal operacao,
			MciTipoGrupoProcedRisco tipoGrupoProcedRisco,
			RapServidores usuarioLogado) {
		Integer serMatricula = null;
		Short serVinCodigo = null;
		Integer serMatriculaMov = null;
		Short serVinCodigoMov = null;

		if (tipoGrupoProcedRisco.getRapServidoresByMciTgpSerFk1() != null) {
			serMatricula = tipoGrupoProcedRisco
					.getRapServidoresByMciTgpSerFk1().getId().getMatricula();
			serVinCodigo = tipoGrupoProcedRisco
					.getRapServidoresByMciTgpSerFk1().getId().getVinCodigo();
		}
		if (tipoGrupoProcedRisco.getRapServidoresByMciTgpSerFk2() != null) {
			serMatriculaMov = tipoGrupoProcedRisco
					.getRapServidoresByMciTgpSerFk2().getId().getMatricula();
			serVinCodigoMov = tipoGrupoProcedRisco
					.getRapServidoresByMciTgpSerFk2().getId().getVinCodigo();
		}
		MciTipoGrupoProcedRiscoJn entidade = new MciTipoGrupoProcedRiscoJn(
				usuarioLogado.getUsuario(), operacao,
				tipoGrupoProcedRisco.getSeq(),
				tipoGrupoProcedRisco.getIndSituacao(),
				tipoGrupoProcedRisco.getCriadoEm(), serMatricula, serVinCodigo,
				tipoGrupoProcedRisco.getAlteradoEm(), serMatriculaMov,
				serVinCodigoMov, tipoGrupoProcedRisco.getDescricao());
		return entidade;
	}

	private boolean isRegistrosAssociados(List<ProcedRiscoVO> gruposProcedRisco) {
		return !gruposProcedRisco.isEmpty();
	}

	// RN5
	public boolean isDentroPrazoExclusao(Date dataCriacao)
			throws ApplicationBusinessException {
		int diferenca = DateUtil
				.calcularDiasEntreDatas(dataCriacao, new Date());
		Integer limiteDiasExclusao = getParametroFacade().buscarValorInteiro(
				AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		if (limiteDiasExclusao == null) {
			return false;
		}
		if (diferenca > limiteDiasExclusao) {
			return false;
		}
		return true;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public MciProcedimentoRiscoDAO getMciProcedRiscoDAO() {
		return mciProcedRiscoDAO;
	}

	public MciTipoGrupoProcedRiscoDAO getMciTipoGrupoProcedRiscoDAO() {
		return mciTipoGrupoProcedRiscoDAO;
	}

	public MciTipoGrupoProcedRiscoJnDAO getMciTipoGrupoProcedRiscoJnDAO() {
		return mciTipoGrupoProcedRiscoJnDAO;
	}

}

package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.MpmServRecomendacaoAltaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.RecomendacoesAltaUsuarioDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RecomendacoesAltaUsuarioCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RecomendacoesAltaUsuarioCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RecomendacoesAltaUsuarioDAO recomendacoesAltaUsuarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6790313186160013973L;

	protected enum RecomendacoesAltaUsuarioCRUDExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_REMOCAO_RECOMENDACAO_POR_VINCULO, MENSAGEM_ERRO_REMOCAO_RECOMENDACAO_POR_USUARIO_FORA_PERIODO;
	}

	protected RecomendacoesAltaUsuarioDAO getRecomendacoesAltaUsuarioDAO() {
		return recomendacoesAltaUsuarioDAO;
	}

	public Long pesquisarRecomendacoesUsuarioCount(RapServidores usuario) {
		return this.getRecomendacoesAltaUsuarioDAO()
				.pesquisarRecomendacoesUsuarioCount(usuario);
	}

	public List<MpmServRecomendacaoAlta> pesquisarRecomendacoesUsuario(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores usuario) {

		return this.getRecomendacoesAltaUsuarioDAO()
				.pesquisarRecomendacoesUsuario(firstResult, maxResult,
						orderProperty, asc, usuario);
	}
	
	public void persistRecomendacaoAlta(MpmServRecomendacaoAlta recomendacao)
			throws ApplicationBusinessException {
		if (recomendacao.getId().getSeqp() == null) {
			getRecomendacoesAltaUsuarioDAO().persistir(recomendacao);
		} else {
			getRecomendacoesAltaUsuarioDAO().atualizar(recomendacao);
		}
		getRecomendacoesAltaUsuarioDAO().flush();
	}

	public void removerRecomendacao(MpmServRecomendacaoAltaId id,
			Integer periodo) throws ApplicationBusinessException {
		MpmServRecomendacaoAlta recomendacao = this.getRecomendacoesAltaUsuarioDAO().obterPorChavePrimaria(id);
		preRemoveremoverRecomendacao(recomendacao, periodo);
		getRecomendacoesAltaUsuarioDAO().remover(recomendacao);
		getRecomendacoesAltaUsuarioDAO().flush();

	}

	/**
	 * @ORADB AFAT_VAD_BRD
	 * @param viaAdministracao
	 * @throws ApplicationBusinessException
	 */
	private void preRemoveremoverRecomendacao(
			MpmServRecomendacaoAlta recomendacao, Integer periodo)
			throws ApplicationBusinessException {

		if (DateUtil.calcularDiasEntreDatas(new Date(),
				recomendacao.getCriadoEm()) > periodo) {
			throw new ApplicationBusinessException(
					RecomendacoesAltaUsuarioCRUDExceptionCode.MENSAGEM_ERRO_REMOCAO_RECOMENDACAO_POR_USUARIO_FORA_PERIODO);
		}

		if (getRecomendacoesAltaUsuarioDAO()
				.exiteVinculoAltaRecomendacoes(recomendacao.getId())) {
			throw new ApplicationBusinessException(
					RecomendacoesAltaUsuarioCRUDExceptionCode.MENSAGEM_ERRO_REMOCAO_RECOMENDACAO_POR_VINCULO);
		}

	}

}

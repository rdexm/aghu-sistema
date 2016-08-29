package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoQualificacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GraduacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GraduacaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private RapTipoQualificacaoDAO rapTipoQualificacaoDAO;

	private static final long serialVersionUID = -2428708716897868439L;

	public enum GraduacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO_GRADUACAO, //
		MENSAGEM_ERRO_REMOVER_GRADUACAO_CONSTRAINT_QUALIFICACOES, //
		MENSAGEM_IMPEDE_GRAVAR_GRADUACAO_CONSELHO,
		MENSAGEM_GRADUACAO_JA_EXISTENTE;
	}

	
	public void salvarGraduacao(RapTipoQualificacao rapTipoQualificacao) throws ApplicationBusinessException {
		this.validarDadosGraduacao(rapTipoQualificacao);
		rapTipoQualificacaoDAO.persistir(rapTipoQualificacao);
	}

	
	public void alterarGraduacao(RapTipoQualificacao rapTipoQualificacao) throws ApplicationBusinessException {
		this.validarDadosGraduacao(rapTipoQualificacao);
		rapTipoQualificacaoDAO.merge(rapTipoQualificacao);
	}

	private void validarDadosGraduacao(RapTipoQualificacao rapTipoQualificacao)
			throws ApplicationBusinessException {

		RapTipoQualificacao graduacaoAux = getTipoQualificacaoDAO().graduacaoPorDescricaoSituacaoTipoCount(
				rapTipoQualificacao.getDescricao(), rapTipoQualificacao
						.getIndSituacao(), rapTipoQualificacao
						.getTipoQualificacao(), rapTipoQualificacao
						.getConselhoProfissional());

		Integer codAux1 = graduacaoAux != null ? graduacaoAux.getCodigo() : null;
		Integer codAux2 = rapTipoQualificacao.getCodigo();
		
		if (graduacaoAux != null && !codAux1.equals(codAux2) ){
			throw new ApplicationBusinessException(GraduacaoONExceptionCode.MENSAGEM_GRADUACAO_JA_EXISTENTE);
		}
	}

	public RapTipoQualificacao obterGraduacao(Integer codGraduacao, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) throws ApplicationBusinessException {
		return rapTipoQualificacaoDAO.obterPorChavePrimaria(codGraduacao, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	
	public void removerGraduacao(Integer codigo) throws ApplicationBusinessException {
		try {
			RapTipoQualificacao rtq = rapTipoQualificacaoDAO.obterPorChavePrimaria(codigo);
			rapTipoQualificacaoDAO.remover(rtq);
			rapTipoQualificacaoDAO.flush();
		} catch (Exception e) {
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "RAP_QLF_TQL_FK1")) {
					throw new ApplicationBusinessException(GraduacaoONExceptionCode.MENSAGEM_ERRO_REMOVER_GRADUACAO_CONSTRAINT_QUALIFICACOES);
				}
			}
			
			throw e;
		}
	}

	/**
	 * Verificar se um novo registro de graduação deve ser inserido no banco. Quando o campo "tipo" for "CSC" e o nome do conselho tiver sido escolhido,
	 * a gravação
	 * 
	 * @param _rapTipoQualificacao Objeto com os tipos de qualificação retornados na pesquisa.
	 * @return
	 */
	public void validarRegistroGraduacaoConselho (RapTipoQualificacao _rapTipoQualificacao) 
		throws ApplicationBusinessException{
		
		// se o tipo escolhido for "csc", não pode escolher um conselho. nesse caso, impede o registro e mostra mensagem na tela. 
		if ((_rapTipoQualificacao.getTipoQualificacao() == DominioTipoQualificacao.CSC) && 
			(_rapTipoQualificacao.getConselhoProfissional() != null)) {
						
			throw new ApplicationBusinessException(
					GraduacaoONExceptionCode.MENSAGEM_IMPEDE_GRAVAR_GRADUACAO_CONSELHO);
		}
	}
	
	protected RapTipoQualificacaoDAO getTipoQualificacaoDAO() {
		return rapTipoQualificacaoDAO;
	}
}
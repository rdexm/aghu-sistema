package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoInformacoesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TipoInformacoesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoInformacoesON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private RapTipoInformacoesDAO rapTipoInformacoesDAO;

	private static final long serialVersionUID = -1883524434491649375L;

	public enum TipoInformacoesExceptionCode implements BusinessExceptionCode {
		ERRO_DESCRICAO_TIPO_INFORMACAO_JA_EXISTE, MENSAGEM_PARAMETRO_NAO_INFORMADO, MENSAGEM_ERRO_REMOVER_TIPO_INFORMACAO_COM_PESSOA, MENSAGEM_ERRO_REMOVER_TIPO_INFORMACAO;
	}

	/**
	 * Método responsável pela persistência e atualização de Tipo Informacoes
	 * Complementares
	 */
	public void persistirTipoInformacoes(RapTipoInformacoes rapTipoInformacoes) throws ApplicationBusinessException {

		if (getTipoInformacoesDAO().pesquisarTipoInformacoesPorDescricaoCount(rapTipoInformacoes.getDescricao()) > 0) {
			throw new ApplicationBusinessException(TipoInformacoesExceptionCode.ERRO_DESCRICAO_TIPO_INFORMACAO_JA_EXISTE);
		}

		if (rapTipoInformacoes.getSeq() == null) {
			// inclusão
			this.incluirTipoInformacoes(rapTipoInformacoes);
		} else {
			// edição
			this.atualizarTipoInformacoes(rapTipoInformacoes);
		}
	}

	/**
	 * Método responsável por incluir um TipoInformacoes novo.
	 */
	private void incluirTipoInformacoes(RapTipoInformacoes rapTipoInformacoes) {
		rapTipoInformacoesDAO.persistir(rapTipoInformacoes);
	}

	/**
	 * Método responsável por atualizar um TipoInformacoes existente.
	 */
	private void atualizarTipoInformacoes(RapTipoInformacoes rapTipoInformacoes) throws ApplicationBusinessException {
		rapTipoInformacoesDAO.merge(rapTipoInformacoes);
	}

	/**
	 * Obtem o TipoInformacoes através de seu código.
	 */
	public RapTipoInformacoes obterTipoInformacoes(Integer tipoInformacoesCodigo) throws ApplicationBusinessException {
		RapTipoInformacoes retorno = rapTipoInformacoesDAO.obterPorChavePrimaria(tipoInformacoesCodigo);
		
		return retorno;
	}

	/**
	 * Exclui um objeto rapTipoInformacoes
	 */
	public void excluirTipoInformacoes(Integer tipoInformacoesCodigo) throws ApplicationBusinessException {
		try {
			rapTipoInformacoesDAO.remover(obterTipoInformacoes(tipoInformacoesCodigo));
			
		} catch (Exception e) {
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),"RAP_PTI_TII_FK1")) {
					throw new ApplicationBusinessException(TipoInformacoesExceptionCode.MENSAGEM_ERRO_REMOVER_TIPO_INFORMACAO_COM_PESSOA);
				}
			}
			throw new ApplicationBusinessException( TipoInformacoesExceptionCode.MENSAGEM_ERRO_REMOVER_TIPO_INFORMACAO);
		}
	}

	protected RapTipoInformacoesDAO getTipoInformacoesDAO() {
		return rapTipoInformacoesDAO;
	}
}
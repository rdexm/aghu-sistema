package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapTipoAfastamento;
import br.gov.mec.aghu.registrocolaborador.dao.RapTipoAfastamentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TipoAfastamentoON extends BaseBusiness {


@EJB
private TipoAfastamentoRN tipoAfastamentoRN;

private static final Log LOG = LogFactory.getLog(TipoAfastamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapTipoAfastamentoDAO rapTipoAfastamentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 945260349223765928L;

	public enum TipoAfastamentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TIPO_AFASTAMENTO_NAO_INFORMADO, MENSAGEM_TIPO_AFASTAMENTO_JA_CADASTRADO, MENSAGEM_PARAMETRO_NAO_INFORMADO;
	}

	
	public void salvar(RapTipoAfastamento rapTipoAfastamento, boolean alteracao)
			throws ApplicationBusinessException {

		getTipoAfastamentoRN().validaPersistenciaTipoAfastamento(rapTipoAfastamento);
		getTipoAfastamentoRN().validaConsistenciaDescricaoSituacao(rapTipoAfastamento);
		
		RapTipoAfastamentoDAO tipoAfastamentoDAO = getTipoAfastamentoDAO();
		
		if (!alteracao) { // esta cadastrando um novo
			if (rapTipoAfastamento.getCodigo() != null) {
				RapTipoAfastamento instExistente = obterTipoAfastamento(rapTipoAfastamento.getCodigo());
				if (instExistente != null) {
					throw new ApplicationBusinessException(TipoAfastamentoONExceptionCode.MENSAGEM_TIPO_AFASTAMENTO_JA_CADASTRADO);
				}
			} 
			tipoAfastamentoDAO.persistir(rapTipoAfastamento);
		} else {
			if (rapTipoAfastamento == null || StringUtils.isEmpty(rapTipoAfastamento.getCodigo())) {
				throw new ApplicationBusinessException(TipoAfastamentoONExceptionCode.MENSAGEM_TIPO_AFASTAMENTO_NAO_INFORMADO);
			}
			if (!tipoAfastamentoDAO.contains(rapTipoAfastamento)) {
				tipoAfastamentoDAO.merge(rapTipoAfastamento);
			}
		}
		tipoAfastamentoDAO.flush();
	}

	public RapTipoAfastamento obterTipoAfastamento(String codigo)
			throws ApplicationBusinessException {
		if (codigo == null) {
			throw new ApplicationBusinessException(TipoAfastamentoONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return getTipoAfastamentoDAO().obterPorChavePrimaria(codigo);
	}

	
	public void excluirTipoAfastamento(RapTipoAfastamento tipoAfastamento)
			throws ApplicationBusinessException {
		if (tipoAfastamento == null) {
			throw new ApplicationBusinessException(
					TipoAfastamentoONExceptionCode.MENSAGEM_TIPO_AFASTAMENTO_NAO_INFORMADO);
		}
		
		RapTipoAfastamento paraExcluir = obterTipoAfastamento(tipoAfastamento.getCodigo());
		getTipoAfastamentoRN().validaExclusaoTipoAfastamento(paraExcluir);
		
		RapTipoAfastamentoDAO tipoAfastamentoDAO = getTipoAfastamentoDAO();
		tipoAfastamentoDAO.remover(paraExcluir);
		tipoAfastamentoDAO.flush();
	}

	protected TipoAfastamentoRN getTipoAfastamentoRN() {
		return tipoAfastamentoRN;
	}
	
	protected RapTipoAfastamentoDAO getTipoAfastamentoDAO() {
		return rapTipoAfastamentoDAO;
	}
}

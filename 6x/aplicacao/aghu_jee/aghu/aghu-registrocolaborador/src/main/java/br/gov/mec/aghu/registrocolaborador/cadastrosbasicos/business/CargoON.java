package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.registrocolaborador.dao.RapCargosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CargoON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(CargoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private RapCargosDAO rapCargosDAO;

	private static final long serialVersionUID = 767498384236437397L;

	public enum CargoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO_CARGO, MENSAGEM_CODIGO_CARGO_OBRIGATORIO, MENSAGEM_DESCRICAO_CARGO_OBRIGATORIO, MENSAGEM_SITUACAO_CARGO_OBRIGATORIO, MENSAGEM_CODIGO_CARGO_JA_EXISTENTE, MENSAGEM_ERRO_REMOVER_CARGO_CONSTRAINT_OCUPACOES, MENSAGEM_DESCRICAO_CARGO_JA_EXISTENTE;
	}

	public void salvarCargo(RapCargos rapCargos) throws ApplicationBusinessException {
		this.validarDadosCargo(rapCargos, true);
		rapCargosDAO.persistir(rapCargos);
	}

	public void alterarCargo(RapCargos rapCargos) throws ApplicationBusinessException {
		this.validarDadosCargo(rapCargos, false);
		rapCargosDAO.merge(rapCargos);
	}

	private void validarDadosCargo(RapCargos rapCargos, boolean novoCargo) throws ApplicationBusinessException {
		
		if (StringUtils.isBlank(rapCargos.getCodigo())) {
			throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_CODIGO_CARGO_OBRIGATORIO);
		}

		if (StringUtils.isBlank(rapCargos.getDescricao())) {
			throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_DESCRICAO_CARGO_OBRIGATORIO);
		}

		if (rapCargos.getSituacao() == null) {
			throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_SITUACAO_CARGO_OBRIGATORIO);
		}

		// Na criação, valida se já existe um Cargo com a mesma codigo
		if (novoCargo) {
			RapCargos cargoAux = obterCargo(rapCargos.getCodigo());
			if (cargoAux != null) {
				throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_CODIGO_CARGO_JA_EXISTENTE);
			}
		}

		// Valida se existe um cargo com a mesma descrição
		RapCargos descricaoAux = getRapCargosDAO().obterDescricao(rapCargos.getDescricao());
		if ((descricaoAux != null && novoCargo)
				|| (!novoCargo && descricaoAux != null && !descricaoAux.getCodigo().equalsIgnoreCase(rapCargos.getCodigo()))) {
			
			throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_DESCRICAO_CARGO_JA_EXISTENTE);
		}

	}

	public RapCargos obterCargo(String codCargo) throws ApplicationBusinessException {
		if (!StringUtils.isNotBlank(codCargo)) {
			throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO_CARGO);
		}
		
		return getRapCargosDAO().obterPorChavePrimaria(codCargo);
	}
	
	
	public void removerCargo(String codCargo) throws ApplicationBusinessException {
		try {
			rapCargosDAO.remover(obterCargo(codCargo));
		} catch (Exception e) {
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "RAP_OCA_CAR_FK1")) {
					throw new ApplicationBusinessException(CargoONExceptionCode.MENSAGEM_ERRO_REMOVER_CARGO_CONSTRAINT_OCUPACOES);
				}
			} else {
				throw e;
			}
		}
	}
	
	protected RapCargosDAO getRapCargosDAO() {
		return rapCargosDAO;
	}
}

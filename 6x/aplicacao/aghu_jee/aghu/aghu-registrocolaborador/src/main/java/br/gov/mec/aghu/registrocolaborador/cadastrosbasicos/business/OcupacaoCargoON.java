package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapOcupacoesCargoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapOcupacaoCargoDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapOcupacaoCargoDAO.OcupacaoCargoONExceptionCode;
import br.gov.mec.aghu.registrocolaborador.vo.OcupacaoCargoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class OcupacaoCargoON extends BaseBusiness {

	@EJB
	private CargoON cargoON;
	
	private static final Log LOG = LogFactory.getLog(OcupacaoCargoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private RapOcupacaoCargoDAO rapOcupacaoCargoDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8605335027962675271L;

	private void validarDadosOcupacaoCargo(RapOcupacaoCargo rapOcupacaoCargo,
			boolean novaOcupacaoCargo) throws ApplicationBusinessException {
		if (rapOcupacaoCargo.getId().getCodigo() == null) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_CODIGO_OCUPACAO_CARGO_OBRIGATORIO);
		}
		
		if (rapOcupacaoCargo.getId().getCargoCodigo() == null) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_CARGO_CODIGO_OCUPACAO_CARGO_OBRIGATORIO);
		}

		RapCargos cargo = getCargoON().obterCargo(rapOcupacaoCargo.getId()
				.getCargoCodigo());
		if (cargo == null) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_CARGO_NAO_EXISTE);
		}

		if (StringUtils.isBlank(rapOcupacaoCargo.getDescricao())) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_DESCRICAO_OCUPACAO_CARGO_OBRIGATORIO);
		}

		if (rapOcupacaoCargo.getIndSituacao() == null) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_SITUACAO_OCUPACAO_CARGO_OBRIGATORIO);
		}

		// Na criação, valida se já existe uma Ocupacao Cargo com o mesmo codigo
		if (novaOcupacaoCargo) {
			// verifica se chave primaria ja existe
			Long ocupacaoCargoCount = getRapOcupacaoCargoDAO().ocupacaoCargoPorCodigoCargoCount(rapOcupacaoCargo);

			if (ocupacaoCargoCount > 0) {
				throw new ApplicationBusinessException(
						OcupacaoCargoONExceptionCode.MENSAGEM_CODIGO_OCUPACAO_CARGO_JA_EXISTENTE);
			}

			ocupacaoCargoCount = getRapOcupacaoCargoDAO().ocupacaoCargoPorCodigoSituacaoCount(rapOcupacaoCargo);

			if (ocupacaoCargoCount > 0) {
				throw new ApplicationBusinessException(
						OcupacaoCargoONExceptionCode.MENSAGEM_CODIGO_SITUACAO_OCUPACAO_CARGO_JA_EXISTENTE);
			}

		}

	}

	/**
	 * Retorna instância pelo codigo da ocupação.
	 * 
	 * @param codigoOcupacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public RapOcupacaoCargo obterOcupacaoCargoPorCodigo(Integer codigoOcupacao,
			boolean somenteAtivo) throws ApplicationBusinessException {
		if (codigoOcupacao == null) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		List<RapOcupacaoCargo> list = getRapOcupacaoCargoDAO().listarOcupacaoPorCodigo(codigoOcupacao);

		if (list.isEmpty()) {
			throw new ApplicationBusinessException(
					OcupacaoCargoONExceptionCode.MENSAGEM_CARGO_NAO_LOCALIZADO);
		}

		RapOcupacoesCargoId id = new RapOcupacoesCargoId(list.get(0).getId()
				.getCargoCodigo(), codigoOcupacao);

		RapOcupacaoCargo ocupacaoCargo = getRapOcupacaoCargoDAO().obterOcupacaoCargo(id);

		if (somenteAtivo && ocupacaoCargo.getIndSituacao() != DominioSituacao.A) {
			return null;
		}

		if (super.isHCPA()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			String descricaoOcupacao = getObjetosOracleDAO()
					.buscarDescricaoOcupacao(
							ocupacaoCargo.getCodigo(),
							servidorLogado);
			ocupacaoCargo.setDescricao(descricaoOcupacao);
		}

		return ocupacaoCargo;
	}
	
	
	public void incluir(RapOcupacaoCargo ocupacaoCargo)
			throws ApplicationBusinessException {

		if (ocupacaoCargo == null) {
			throw new ApplicationBusinessException(OcupacaoCargoONExceptionCode.MENSAGEM_OCUPACAO_CARGO_NAO_INFORMADA);
		}

		this.validarDadosOcupacaoCargo(ocupacaoCargo, true);
		getRapOcupacaoCargoDAO().incluir(ocupacaoCargo);
	}	
	
	/**
	 * Retorna os cargos encontrados com a string fornecida no atributo descrição.
	 */
	public List<OcupacaoCargoVO> pesquisarOcupacaoPorCodigo(String ocupacaoCargo, boolean somenteAtivos) throws ApplicationBusinessException {
		
		List<OcupacaoCargoVO> listaRetorno = getRapOcupacaoCargoDAO().pesquisarOcupacaoPorCodigo(ocupacaoCargo, somenteAtivos);
		
		if(isHCPA()){
			listaRetorno = ajustarDescricaoOcupacao(listaRetorno);
		}
		
		return listaRetorno;
	}

	/**
	 * Buscar a descrição correta quando executado no HCPA.
	 * @param listaOcupacoesCargo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<OcupacaoCargoVO> ajustarDescricaoOcupacao(
			List<OcupacaoCargoVO> listaOcupacoesCargo)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<OcupacaoCargoVO> listaRetorno = new ArrayList<OcupacaoCargoVO>();

		for (OcupacaoCargoVO lista : listaOcupacoesCargo) {
			String descricaoOcupacao = getObjetosOracleDAO().buscarDescricaoOcupacao(lista.getCodigoOcupacao(), servidorLogado);
			lista.setDescricaoOcupacao(descricaoOcupacao);
			listaRetorno.add(lista);
		}
		return listaRetorno;
	}

	protected CargoON getCargoON() {
		return cargoON;
	}
	
	protected RapOcupacaoCargoDAO getRapOcupacaoCargoDAO() {
		return rapOcupacaoCargoDAO; 
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}

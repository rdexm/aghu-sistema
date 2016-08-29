package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelMarcadorDAO;
import br.gov.mec.aghu.model.AelMarcador;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AelMarcadorRN extends BaseBusiness {

	@Inject
	private AelMarcadorDAO aelMarcadorDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -2367469270526213726L;

	private static final Log LOG = LogFactory.getLog(AelMarcadorRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum AelMarcadorRNExceptionCode implements BusinessExceptionCode {

		MENSAGEM_MARCADOR_JA_CADASTRADO, MENSAGEM_MARCADOR_PEDIDO_OBRIGATORIO, MENSAGEM_MARCADOR_LAUDO_OBRIGATORIO, MENSAGEM_CLONE_OBRIGATORIO, MENSAGEM_FABRICANTE_OBRIGATORIO;

	}

	public void inserir(final AelMarcador aelMarcador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		validarAtributosObrigatorios(aelMarcador);

		verificaMarcadorPedidoDuplicado(aelMarcador.getMarcadorPedido());

		aelMarcador.setServidorInclusao(servidorLogado);
		aelMarcador.setServidorAlteracao(servidorLogado);
		aelMarcador.setCriadoEm(DateUtil.truncaData(new Date()));
		getAelMarcadorDAO().persistir(aelMarcador);
	}

	private void validarAtributosObrigatorios(final AelMarcador aelMarcador) throws ApplicationBusinessException {
		if (StringUtils.isBlank(aelMarcador.getMarcadorPedido())) {
			throw new ApplicationBusinessException(AelMarcadorRNExceptionCode.MENSAGEM_MARCADOR_PEDIDO_OBRIGATORIO);
		} else if (StringUtils.isBlank(aelMarcador.getMarcadorLaudo())) {
			throw new ApplicationBusinessException(AelMarcadorRNExceptionCode.MENSAGEM_MARCADOR_LAUDO_OBRIGATORIO);
		} else if (StringUtils.isBlank(aelMarcador.getCloneMarcador())) {
			throw new ApplicationBusinessException(AelMarcadorRNExceptionCode.MENSAGEM_CLONE_OBRIGATORIO);
		} else if (aelMarcador.getFabricante() == null) {
			throw new ApplicationBusinessException(AelMarcadorRNExceptionCode.MENSAGEM_FABRICANTE_OBRIGATORIO);
		}
	}

	public void alterar(final AelMarcador aelMarcador) throws ApplicationBusinessException {
		aelMarcador.setServidorAlteracao(getServidorLogadoFacade().obterServidorLogado());
		aelMarcador.setAlteradoEm(DateUtil.truncaData(new Date()));
		getAelMarcadorDAO().atualizar(aelMarcador);
	}

	private void verificaMarcadorPedidoDuplicado(String marcadorPedido) throws ApplicationBusinessException {
		AelMarcador aelMarcador = getAelMarcadorDAO().obterAelMarcadorPorMarcadorPedido(marcadorPedido);
		if (aelMarcador != null) {
			throw new ApplicationBusinessException(AelMarcadorRNExceptionCode.MENSAGEM_MARCADOR_JA_CADASTRADO);
		}
	}

	protected AelMarcadorDAO getAelMarcadorDAO() {
		return this.aelMarcadorDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}

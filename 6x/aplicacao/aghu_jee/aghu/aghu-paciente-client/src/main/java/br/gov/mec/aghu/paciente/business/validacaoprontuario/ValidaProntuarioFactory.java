package br.gov.mec.aghu.paciente.business.validacaoprontuario;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.locator.ServiceLocator;

@ApplicationScoped
public class ValidaProntuarioFactory {

	private static final Log LOG = LogFactory.getLog(ValidaProntuarioFactory.class);

	private InterfaceValidaProntuario validaProntuario;
	
	@EJB
	private IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	public enum ValidaProtuarioConfig {
		MODULO_10_EXTENDIDO, MODULO_10, MODULO_11;
	}

	private Map<ValidaProtuarioConfig, Class> validadoresMap;

	public void init() {
		if (validadoresMap == null) {
			validadoresMap = new HashMap<ValidaProtuarioConfig, Class>();
			validadoresMap.put(ValidaProtuarioConfig.MODULO_10_EXTENDIDO,
					ValidaProntuarioModulo10Extendido.class);
			validadoresMap.put(ValidaProtuarioConfig.MODULO_10,
					ValidaProntuarioModulo10.class);
			validadoresMap.put(ValidaProtuarioConfig.MODULO_11,
					ValidaProntuarioModulo11.class);
		}
	}

	public InterfaceValidaProntuario getValidaProntuario(boolean reload) throws ValidaProntuarioException {
		init();
		return getValidaProntuario(reload, loadProntuarioConfig());
	}

	private ValidaProtuarioConfig loadProntuarioConfig() throws ValidaProntuarioException {
		AghParametros parametro = null;

		try {
			parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_VALIDADOR_PRONTUARIO);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage() + " Utilizando valor padrão MODULO_10.");
			return ValidaProtuarioConfig.MODULO_10;
		}

		if (parametro == null || parametro.getVlrTexto() == null) {
			LOG.warn("Validador de prontuário não configurado. Utilizando valor padrão MODULO_10.");
			return ValidaProtuarioConfig.MODULO_10;
		}

		String validadorNome = parametro.getVlrTexto();
		ValidaProtuarioConfig toConfig = null;

		try {
			LOG.info("Utilizando validador de prontuário configurado ["
					+ validadorNome + "]");

			if (validadorNome != null) {
				toConfig = ValidaProtuarioConfig.valueOf(validadorNome);
			} else {
				throw new ValidaProntuarioException(
						"Nome do validador ["
								+ validadorNome
								+ "] não pode ser nulo, ajuste o prametro de configuração.");
			}
		} catch (IllegalArgumentException e) {
			throw new ValidaProntuarioException("Nome do validador ["
					+ validadorNome + "] inválido, as opções válidas são "
					+ validadoresMap.keySet()
					+ ", ajuste o prametro de configuração.", e);
		}

		return toConfig;
	}

	public InterfaceValidaProntuario getValidaProntuario(boolean reload,
			ValidaProtuarioConfig prontuarioConfig)
			throws ValidaProntuarioException {
		if (reload || validaProntuario == null){
			validaProntuario = loadValidador(prontuarioConfig);
		}
		return validaProntuario;
	}

	private InterfaceValidaProntuario loadValidador(
			ValidaProtuarioConfig toLoad) throws ValidaProntuarioException {
		Class classeVal = validadoresMap.get(toLoad);
		try {
			return (InterfaceValidaProntuario) classeVal.newInstance();
		} catch (InstantiationException e) {
			throw new ValidaProntuarioException(
					"Erro ao instanciar classe para o validador [" + toLoad
							+ "].", e);
		} catch (IllegalAccessException e) {
			throw new ValidaProntuarioException(
					"Erro de acesso inválido ao instanciar classe para o validador ["
							+ toLoad + "].", e);
		}
	}
}

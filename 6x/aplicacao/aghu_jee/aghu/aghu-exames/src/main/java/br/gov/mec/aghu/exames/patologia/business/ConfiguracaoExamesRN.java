package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelSecaoConfExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConfiguracaoExamesRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConfiguracaoExamesRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;

	private static final long serialVersionUID = 7189790469844518795L;

	public enum ConfiguracaoExamesRNExceptionCode implements BusinessExceptionCode {
		ERRO_SIGLA_CONFIGURACAO_EXAME;
	}

	public void validaSigla(String sigla) throws ApplicationBusinessException {
		if (sigla.length() != 2) {
			throw new ApplicationBusinessException(ConfiguracaoExamesRNExceptionCode.ERRO_SIGLA_CONFIGURACAO_EXAME);
		}
	}

	/*
	 * Persiste os objetos AelSecaoConfExames baseado nos mesmos valores,
	 * variando os valores de descrição e aba;
	 */
	public void persisteSecaoConfigurcaoExames(AelConfigExLaudoUnico configExame) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelSecaoConfExames secaoConfExameBasic;
		for (DominioSecaoConfiguravel domain : DominioSecaoConfiguravel.values()) {
			secaoConfExameBasic = buildBasicAelSecaoConfExames(configExame, servidorLogado);
			secaoConfExameBasic.setDescricao(domain);
			secaoConfExameBasic.setAba(domain.getAba());
			getAelSecaoConfExamesDAO().persistir(secaoConfExameBasic);
		}
	}

	/*
	 * Monta o objeto base com os valores definidos na demanda #33808
	 */
	protected AelSecaoConfExames buildBasicAelSecaoConfExames(AelConfigExLaudoUnico configExame, RapServidores servidorLogado) {
		AelSecaoConfExames secaoConfExames = new AelSecaoConfExames();
		secaoConfExames.setVersaoConf(1);
		secaoConfExames.setAelConfigExLaudoUnico(configExame);
		secaoConfExames.setIndSituacao(DominioSituacao.A);
		secaoConfExames.setCriadoEm(new Date());
		secaoConfExames.setRapServidor(servidorLogado);
		secaoConfExames.setIndObrigatorio(false);
		secaoConfExames.setIndImprimir(true);
		secaoConfExames.setEtapaLaudo(null);
		return secaoConfExames;
	}

	protected AelSecaoConfExamesDAO getAelSecaoConfExamesDAO() {
		return this.aelSecaoConfExamesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}

package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterAlmoxarifadoON extends BaseBusiness {

	@EJB
	private SceAlmoxarifadosRN sceAlmoxarifadosRN;

	private static final Log LOG = LogFactory.getLog(ManterAlmoxarifadoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1482918786166103907L;

	private final static String ATIVAR_DESATIVAR = "ativarDesativar";
	private final static String CENTRALIZAR_DESCENTRALIZAR = "centralizarDescentralizar";
	private final static String CALCULAR_NAO_CALCULAR_MP = "calcularNaoCalcularMP";
	private final static String BLOQUEIA_DESBLOQUEIA_ENT_TRANSF = "bloqueiaDesbloqueiaEntTransf";

	private void prePersist(SceAlmoxarifado alterado, SceAlmoxarifado original) throws ApplicationBusinessException {
		getSceAlmoxarifadosRN().executarAntesPersistencia(alterado, original);
	}

	public void persist(SceAlmoxarifado alterado) throws ApplicationBusinessException {

		SceAlmoxarifado original = getSceAlmoxarifadoDAO().obterOriginal(alterado.getSeq());

		prePersist(alterado, original);
		
		if (alterado.getSeq() == null) {
			getSceAlmoxarifadoDAO().persistir(alterado);
		} else {
			getSceAlmoxarifadoDAO().merge(alterado);
		}
		
		// TODO falta implementar posPersist
		// posPersist(alterado, original);
	}


	public void atualizarProcessoAlmoxarifado(final Short seq, String processo) throws ApplicationBusinessException {
		if (StringUtils.isEmpty(processo)) {
			throw new IllegalArgumentException();
		}
		processo = processo.trim();
		SceAlmoxarifado alterado = getSceAlmoxarifadoDAO().obterPorChavePrimaria(seq);
		if (StringUtils.equalsIgnoreCase(ATIVAR_DESATIVAR, processo)) {
			alterado.setIndSituacao(alterado.getIndSituacao().isAtivo() ? DominioSituacao.I : DominioSituacao.A);
		} else if (StringUtils.equalsIgnoreCase(BLOQUEIA_DESBLOQUEIA_ENT_TRANSF, processo)) {
			alterado.setIndBloqEntrTransf(!alterado.getIndBloqEntrTransf());
		} else if (StringUtils.equalsIgnoreCase(CALCULAR_NAO_CALCULAR_MP, processo)) {
			alterado.setIndCalculaMediaPonderada(!alterado.getIndCalculaMediaPonderada());
		} else if (StringUtils.equalsIgnoreCase(CENTRALIZAR_DESCENTRALIZAR, processo)) {
			alterado.setIndCentral(!alterado.getIndCentral());
		}
		this.persist(alterado);
	}

	protected SceAlmoxarifadosRN getSceAlmoxarifadosRN() {
		return sceAlmoxarifadosRN;
	}

	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}
}

package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.ScoMaterial;

@Stateless
public class ListaMateriaisON extends BaseBusiness {
	
	private static final long serialVersionUID = -9070377690679591264L;
	private static final Log LOG = LogFactory.getLog(ListaMateriaisON.class);
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public List<ScoMaterial> listarMaterial(Object param, String servidorLogado) {

		if (possuiPermissaoCadastrarSCComprador(servidorLogado) || possuiPermissaoSolicitacaoCompras(servidorLogado)
				|| possuiPermissaoCadastrarSCChefias(servidorLogado)
				|| possuiPermissaoCadastrarSCAreasEspecificas(servidorLogado)
				|| possuiPermissaoCadastrarSCGeral(servidorLogado)) {

			return scoMaterialDAO.listarMaterialAtivo(param, Boolean.FALSE, null);
		} else {
			return scoMaterialDAO.listarMaterialAtivo(param, null, null);
		}
	}

	private boolean possuiPermissaoCadastrarSCGeral(String servidorLogado) {
		return cascaFacade.usuarioTemPermissao(servidorLogado, "cadastrarSCGeral");
	}

	private boolean possuiPermissaoCadastrarSCAreasEspecificas(String servidorLogado) {
		return cascaFacade.usuarioTemPermissao(servidorLogado, "cadastrarSCAreasEspecificas");
	}

	private boolean possuiPermissaoCadastrarSCChefias(String servidorLogado) {
		return cascaFacade.usuarioTemPermissao(servidorLogado, "cadastrarSCChefias");
	}

	private boolean possuiPermissaoSolicitacaoCompras(String servidorLogado) {
		return cascaFacade.usuarioTemPermissao(servidorLogado, "cadastrarSolicitacaoCompras");
	}

	private boolean possuiPermissaoCadastrarSCComprador(String servidorLogado) {
		return cascaFacade.usuarioTemPermissao(servidorLogado, "cadastrarSCComprador");
	}

	public Long listarMaterialCount(Object param, String servidorLogado) {
		String strPesquisa = StringUtils.EMPTY;
		if (param != null) {
			strPesquisa = param.toString();
		}
		if (possuiPermissaoCadastrarSCComprador(servidorLogado) || possuiPermissaoSolicitacaoCompras(servidorLogado)
				|| possuiPermissaoCadastrarSCChefias(servidorLogado)
				|| possuiPermissaoCadastrarSCAreasEspecificas(servidorLogado)
				|| possuiPermissaoCadastrarSCGeral(servidorLogado)) {
			return scoMaterialDAO.listarMaterialAtivoCount(strPesquisa, Boolean.FALSE);
		} else {
			return scoMaterialDAO.listarMaterialAtivoCount(strPesquisa, null);
		}
	}

	private Short filtraPorAlmoxarifadoRM(Short almCodigo) {
		try {
			String parametroFiltroAlmox = parametroFacade
					.buscarValorTexto(AghuParametrosEnum.P_AGHU_FILTRO_ALMOXARIFADO_RM);
			if (StringUtils.isBlank(parametroFiltroAlmox) || parametroFiltroAlmox.equals("N")) {
				return null;
			}
		} catch (ApplicationBusinessException e) {
			almCodigo = null;
		}
		return almCodigo;
	}

	public List<ScoMaterial> listarMaterialSC(Object param, Integer gmtCodigo) {
		return listarMaterialSC(param, gmtCodigo, null);
	}

	public List<ScoMaterial> listarMaterialSC(Object param, Integer gmtCodigo, Short almCodigo) {
		return scoMaterialDAO.listarMaterialAtivo(param, null, gmtCodigo, filtraPorAlmoxarifadoRM(almCodigo));
	}

	public Integer listarMaterialSCCount(Object param, Integer gmtCodigo) {
		return this.listarMaterialSC(param, gmtCodigo).size();
	}

}

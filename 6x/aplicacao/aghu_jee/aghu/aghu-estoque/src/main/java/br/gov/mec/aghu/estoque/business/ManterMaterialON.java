package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioClassifyXYZ;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #595 -
 * Gerar requisição de material
 * 
 * @author aghu
 * 
 */
@Stateless
public class ManterMaterialON extends BaseBusiness {

	@EJB
	private ScoMaterialRN scoMaterialRN;

	private static final Log LOG = LogFactory.getLog(ManterMaterialON.class);

	private static final String LIBERAR_CONFAZ = "S";

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 2771985552789896042L;

	/**
	 * Persiste material
	 * 
	 * @param material
	 * @throws BaseException
	 */
	public void persistirMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		if (material.getCodigo() == null) {
			this.inserirMaterial(material, nomeMicrocomputador);
		} else {
			this.atualizarMaterial(material, nomeMicrocomputador);
		}
	}

	/**
	 * Regras de FORMS relacionadas ao insert e ao update
	 * 
	 * @param material
	 */

	public void inserirMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		// Chamada para RNs com regras de banco
		this.getScoMaterialRN().inserir(material, nomeMicrocomputador);
	}

	public void atualizarMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		// Chamada para RNs com regras de banco
		this.getScoMaterialRN().atualizar(material, nomeMicrocomputador);
	}

	/**
	 * Obtém lista de materiais para catálogo
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigoMaterial
	 * @param nomeMaterial
	 * @param situacaoMaterial
	 * @param estocavel
	 * @param generico
	 * @param codigoUnidadeMedida
	 * @param classifyXYZ
	 * @param codigoGrupoMaterial
	 * @return
	 */
	public List<ScoMaterial> pesquisarListaMateriaisParaCatalogo(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoMaterial, String nomeMaterial, DominioSituacao situacaoMaterial, DominioSimNao estocavel,
			DominioSimNao generico, String codigoUnidadeMedida, DominioClassifyXYZ classifyXYZ, Integer codigoGrupoMaterial,
			Integer codCatMat, Long codMatAntigo, VScoClasMaterial classificacaoMaterial) {

		return getComprasFacade().pesquisarListaMateriaisParaCatalogo(firstResult, maxResults, orderProperty, asc, codigoMaterial,
				nomeMaterial, situacaoMaterial, estocavel, generico, codigoUnidadeMedida, classifyXYZ, codigoGrupoMaterial, codCatMat,
				codMatAntigo, classificacaoMaterial);
	}

	public boolean isFatItensContaHospitalarComMateriaisOrteseseProteses(final Integer cthSeq, final Short seq, final Integer phiSeq)
			throws ApplicationBusinessException {
		final Integer codGrupo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_ORT_PROT).getVlrNumerico()
				.intValue();
		final Long result = getComprasFacade().getFatItensContaHospitalarComMateriaisOrteseseProteses(cthSeq, seq, phiSeq, codGrupo);
		return result != null && result > 0;
	}

	public Boolean habilitarConfaz() {
		String parametro;
		try {
			parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONFAZ).getVlrTexto();
			return LIBERAR_CONFAZ.equals(parametro);
		} catch (ApplicationBusinessException e) {
			return Boolean.FALSE;
		}
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	/**
	 * get de RNs e DAOs
	 */
	private ScoMaterialRN getScoMaterialRN() {
		return scoMaterialRN;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public boolean isHabilitarExcluirHCPA() {
		return (!isHCPA() ? true : false);
	}

}

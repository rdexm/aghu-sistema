package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoAgrupamentoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoOrigemParecerTecnicoDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;

/**
 * Facade criada para as estorias de cadastros básicos do parecer técnico
 */

@Modulo(ModuloEnum.COMPRAS)
@Stateless
public class ParecerCadastrosBasicosFacade extends BaseFacade implements IParecerCadastrosBasicosFacade {

	@EJB
	private ScoAgrupamentoMaterialON scoAgrupamentoMaterialON;
	@EJB
	private ScoOrigemParecerTecnicoON scoOrigemParecerTecnicoON;

	@Inject
	private ScoOrigemParecerTecnicoDAO scoOrigemParecerTecnicoDAO;

	@Inject
	private ScoAgrupamentoMaterialDAO scoAgrupamentoMaterialDAO;

	private static final long serialVersionUID = -4399006673641561948L;

	// #5577 - Agrupamento de Materiais
	@Override
	public ScoAgrupamentoMaterial obterAgrupamentoMaterial(final Short codigo) {
		return getScoAgrupamentoMaterialDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<ScoAgrupamentoMaterial> listarAgrupamentoMaterial(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final ScoAgrupamentoMaterial scoAgrupMaterial) {

		return this.getScoAgrupamentoMaterialDAO().listarAgrupamentoMaterial(firstResult, maxResult, orderProperty, asc, scoAgrupMaterial);
	}

	@Override
	public Long listarAgrupamentoMaterialCount(final ScoAgrupamentoMaterial scoAgrupMaterial) {

		return this.getScoAgrupamentoMaterialDAO().listarAgrupamentoMaterialCount(scoAgrupMaterial);
	}

	@Override
	public void inserirAgrupamentoMaterial(ScoAgrupamentoMaterial scoAgrupMaterial) throws ApplicationBusinessException {
		this.getScoAgrupamentoMaterialON().inserirAgrupamentoMaterial(scoAgrupMaterial);
	}

	@Override
	public void alterarAgrupamentoMaterial(ScoAgrupamentoMaterial scoAgrupMaterial) throws ApplicationBusinessException {
		this.getScoAgrupamentoMaterialON().alterarAgrupamentoMaterial(scoAgrupMaterial);
	}

	@Override
	public List<ScoAgrupamentoMaterial> pesquisarAgrupamentoMaterialPorCodigoOuDescricao(Object parametro, Boolean indAtivo) {
		return this.getScoAgrupamentoMaterialDAO().pesquisarAgrupamentoMaterialPorCodigoOuDescricao(parametro, indAtivo);
	}

	private ScoAgrupamentoMaterialON getScoAgrupamentoMaterialON() {
		return scoAgrupamentoMaterialON;
	}

	private ScoAgrupamentoMaterialDAO getScoAgrupamentoMaterialDAO() {
		return scoAgrupamentoMaterialDAO;
	}

	// #5576 - Pastas de Parecer Técnico
	@Override
	public ScoOrigemParecerTecnico obterOrigemParecer(final Integer codigo) {
		return getScoOrigemParecerTecnicoDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<ScoOrigemParecerTecnico> listarOrigemParecer(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			final ScoOrigemParecerTecnico scoOrigemParecer) {

		return this.getScoOrigemParecerTecnicoDAO().listarOrigemParecer(firstResult, maxResult, orderProperty, asc, scoOrigemParecer);
	}

	@Override
	public Long listarOrigemParecerCount(final ScoOrigemParecerTecnico scoOrigemParecer) {

		return this.getScoOrigemParecerTecnicoDAO().listarOrigemParecerCount(scoOrigemParecer);
	}

	@Override
	public void inserirOrigemParecer(ScoOrigemParecerTecnico scoOrigemParecer) throws ApplicationBusinessException {
		this.getScoOrigemParecerTecnicoON().inserirOrigemParecer(scoOrigemParecer);
	}

	@Override
	public void alterarOrigemParecer(ScoOrigemParecerTecnico scoOrigemParecer) throws ApplicationBusinessException {
		this.getScoOrigemParecerTecnicoON().alterarOrigemParecer(scoOrigemParecer);
	}

	@Override
	public void excluirOrigemParecer(Integer codigo) throws ApplicationBusinessException {
		this.getScoOrigemParecerTecnicoON().excluirOrigemParecer(codigo);
	}

	private ScoOrigemParecerTecnicoON getScoOrigemParecerTecnicoON() {
		return scoOrigemParecerTecnicoON;
	}

	private ScoOrigemParecerTecnicoDAO getScoOrigemParecerTecnicoDAO() {
		return scoOrigemParecerTecnicoDAO;
	}
}

package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Interface para facade do submódulo de cadastros basicos do modulo de compras/parecer.
 */
public interface IParecerCadastrosBasicosFacade extends Serializable {
	
	// Estoria #5577 - Agrupamento de Materiais
	ScoAgrupamentoMaterial obterAgrupamentoMaterial(final Short codigo);
		
	List<ScoAgrupamentoMaterial> listarAgrupamentoMaterial(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, 
			final ScoAgrupamentoMaterial scoAgrupMaterial);

	Long listarAgrupamentoMaterialCount(final ScoAgrupamentoMaterial scoAgrupMaterial);

	void inserirAgrupamentoMaterial(final ScoAgrupamentoMaterial scoAgrupMaterial)
			throws ApplicationBusinessException;

	void alterarAgrupamentoMaterial(final ScoAgrupamentoMaterial scoAgrupMaterial)
			throws ApplicationBusinessException;
	
	List<ScoAgrupamentoMaterial> pesquisarAgrupamentoMaterialPorCodigoOuDescricao(
			final Object parametro, final Boolean indAtivo);
	
	// Estoria #5576 - Pastas de Parecer Técnico
	ScoOrigemParecerTecnico obterOrigemParecer(final Integer codigo);
	
	List<ScoOrigemParecerTecnico> listarOrigemParecer(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, 
			final ScoOrigemParecerTecnico scoOrigemParecer);

	Long listarOrigemParecerCount(final ScoOrigemParecerTecnico scoOrigemParecer);

	void inserirOrigemParecer(final ScoOrigemParecerTecnico scoOrigemParecer)
			throws ApplicationBusinessException;

	void alterarOrigemParecer(final ScoOrigemParecerTecnico scoOrigemParecer)
			throws ApplicationBusinessException;
	
	void excluirOrigemParecer(final Integer codigo) throws ApplicationBusinessException;
}
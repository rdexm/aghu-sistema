package br.gov.mec.aghu.paciente.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * AghUnidadesFuncionais.
 * 
 * @author lalegre
 * 
 */
@Stateless
public class UnidadeFuncionaisON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(UnidadeFuncionaisON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822204714505520462L;

	/**
	 * Método que pesquisa por código ou descrição de Unidade Funcional.
	 * 
	 * @param filtro
	 * @return Lista de <code>AghUnidadesFuncionais</code>.
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(String filtro) {
		return this.getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricao(filtro);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(Object filtro,
			Object[] caracteristicas) {
		return this.getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(filtro,
				caracteristicas);
	}

	/**
	 * Método para retornar Unidades Funcionais cadastradas que possuam unidades funcionais, que
	 * a característica da unidade seja igual a Unid Internacao ou Unid Emergencia
	 * e que o parametro passado seja igual ao id da unidade funcional ou seja encontrado
	 * na string formada pelo andar + ala + descricao da unidade.
	 * 
	 * @return Lista com Unidades Funcionais
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(Object objPesquisa,
			boolean ordernarPorCodigoAlaDescricao, boolean apenasAtivos, Object[] caracteristicas) {
		return this.getAghuFacade().pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(objPesquisa,
				ordernarPorCodigoAlaDescricao, apenasAtivos, caracteristicas);
	}

	/**
	 * Método para retornar Unidades Funcionais cadastradas em que
	 * a característica da unidade seja igual a Unid Internacao ou Unid Emergencia
	 * e que o parametro passado seja igual ao id da unidade funcional ou seja encontrado
	 * na string formada pelo andar + ala + descricao da unidade.
	 * 
	 * @return Lista com Unidades Funcionais
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(Object objPesquisa,
			boolean ordernarPorCodigoAlaDescricao, boolean apenasAtivos, Object[] caracteristicas) {
		return this.getAghuFacade().pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(objPesquisa,
				ordernarPorCodigoAlaDescricao, apenasAtivos, caracteristicas);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}

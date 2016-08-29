package br.gov.mec.aghu.estoque.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioCamposValidosHistMaterial;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.vo.HistoricoScoMaterialVO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

@Stateless
public class ConsultarHistoricoMaterialON extends BaseBusiness {

	private static final long serialVersionUID = -3964912991283085422L;
	private static final Log LOG = LogFactory.getLog(ConsultarHistoricoMaterialON.class);
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	public List<HistoricoScoMaterialVO> identificarAlteracoesScoMaterial(
			ScoMaterialJN atual, ScoMaterialJN anterior)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		List<HistoricoScoMaterialVO> propriedadesAlteradas = new ArrayList<HistoricoScoMaterialVO>();

		for (DominioCamposValidosHistMaterial propriedade : DominioCamposValidosHistMaterial
				.values()) {
			ComparatorChain comparatorChain = new ComparatorChain();
			BeanComparator bean = new BeanComparator(propriedade.toString(),new NullComparator());

			comparatorChain.addComparator(bean);

			int resultado = comparatorChain.compare(atual, anterior);

			if (resultado != 0) {
				propriedadesAlteradas.add(new HistoricoScoMaterialVO(
						getValorPropriedade(atual, propriedade.toString()),
						getValorPropriedade(anterior, propriedade.toString()),
						propriedade.getDescricao()));
			}

		}

		return propriedadesAlteradas;
	}

	private String getValorPropriedade(ScoMaterialJN item, String propriedade)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Object valor = PropertyUtils.getProperty(item, propriedade);
		if(valor == null || StringUtils.isBlank(propriedade)){
			return "";
		}
		if (DominioCamposValidosHistMaterial.ALM_SEQ_LOCAL_ESTQ.toString().equals(propriedade)) {			
			SceAlmoxarifado estoque = getEstoqueFacade().obterAlmoxarifadoPorId((Short) valor);
			return estoque.getDescricao();
		}		
		if (DominioCamposValidosHistMaterial.GMT_CODIGO.toString().equals(propriedade)) {
			Byte idByte = Byte.valueOf(valor.toString());
			Integer id = idByte.intValue();
			ScoGrupoMaterial grupo = getComprasFacade().obterScoGrupoMaterialPorChavePrimaria(id);
			return grupo.getDescricao();
		}
		if (DominioCamposValidosHistMaterial.UMD_CODIGO.toString().equals(propriedade)) {
			ScoUnidadeMedida unidadeMedida = getComprasFacade().obterUnidadeMedidaPorId(valor.toString());
			return unidadeMedida.getDescricao();
		}
		if (DominioCamposValidosHistMaterial.OPT_CODIGO.toString().equals(propriedade)) {
			ScoOrigemParecerTecnico origemParecerTec = getComprasFacade().obterOrigemParecerTecnicoPorId((Integer)valor);
			return origemParecerTec.getDescricao();
		}
		return getDescricaoCompletaPropriedade(valor);

	}

	private String getDescricaoCompletaPropriedade(Object propriedade) {
		String resultado = "";
		if (propriedade instanceof Dominio) {
			resultado = ((Dominio) propriedade)
					.getDescricao();
		} else if (propriedade instanceof Boolean) {
			resultado = DominioSimNao.getInstance((Boolean) (propriedade)).getDescricao();
		} else {
			resultado = propriedade.toString();
		}
		return resultado;
	}

	public ScoMaterial obterMaterialPorId(Integer codigo) {
		return getComprasFacade().obterScoMaterialDetalhadoPorChavePrimaria(codigo);
	}

	public List<ScoMaterialJN> pesquisarScoMaterialJNPorCodigoMaterial(Integer codigoMaterial,DominioOperacoesJournal operacao) {
		return getComprasFacade().pesquisarScoMaterialJNPorCodigoMaterial(codigoMaterial, operacao);
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	
}

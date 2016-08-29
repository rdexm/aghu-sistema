package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ProfConveniosListON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ProfConveniosListON.class);

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
	private static final long serialVersionUID = -337481838939282797L;

	/***
	 * Realiza a contagem de itens da pesquisa de convênios
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @return Quantidade de itens da pesquisa
	 * */
	public Integer pesquisaProfConveniosListCount(Integer vinCodigo, Integer matricula, String nome, Long cpf,
			String siglaEspecialidade) {
		return this.getAghuFacade().pesquisaProfConveniosListCount(vinCodigo, matricula, nome, cpf, siglaEspecialidade);
	}

	/***
	 * Realiza a pesquisa de detalhamento de leitos
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @param codigo
	 *            do leito
	 * @return lista
	 * */
	public List<ProfConveniosListVO> pesquisaProfConvenioslist(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer vinCodigo, Integer matricula, String nome, Long cpf, String siglaEspecialidade) {
		return this.getAghuFacade().pesquisaProfConvenioslist(firstResult, maxResult, orderProperty, asc, vinCodigo,
				matricula, nome, cpf, siglaEspecialidade);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}

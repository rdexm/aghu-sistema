package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PesquisarExamesPorUnidadeFuncionalON  extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(PesquisarExamesPorUnidadeFuncionalON.class);

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
	private static final long serialVersionUID = -5199648003033094260L;
	
	/**
	 * Pesquisa as unidades funcionais. Retorna todas se nenhum parametro informado, ou por sequêncial ou descrição. 
	 * @param parametro
	 * @return
	 * @author bruno.mourao
	 * @since 23/10/2012
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqDescricao(String parametro){		
		return getAghuFacade().pesquisarUnidadeFuncionalPorSeqDescricao(parametro, false);		
	}
	
	private IAghuFacade getAghuFacade(){
		return aghuFacade;
	}
	

}

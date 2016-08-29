package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Centralizar a funcoes reutilizaveis do Modulo de Exames.
 * 
 * @author rcorvalao
 *
 */
@Stateless
public class ModuloExamesRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ModuloExamesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4339756737618884794L;

	/**
	 * Verifica se o servidor pode Solicitar exames.<br>
	 * Retorna null caso o servidor nao tenha permissao de solicitar exames.<br>
	 * 
	 * Verifica se o servidor pertence:<br>
	 * - a um conselho profissional com numero de registro;<br>
	 * - e tenha permissao de solicitar exame.<br>
	 * 
	 * 
	 * @param vinculo
	 * @param matricula
	 * @return
	 */
	public List<RapServidores> buscarServidoresSolicitacaoExame(Short vinculo, Integer matricula) {
		Integer diasPermitidos;
		try {
			AghParametros param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_FIM_VINCULO_PERMITIDO);
			diasPermitidos = param.getVlrNumerico().intValue();
		} catch (ApplicationBusinessException e) {
			// Se o parametro nao foi criado / configurado. Entao usa o valor default do metodo de pesquisa.
			diasPermitidos = null;
		}
		
		List<RapServidores> lista = null;
		if (vinculo != null && matricula != null) {
			lista = this.getRegistroColaboradorFacade().pesquisarServidoresSolicitacaoExame(vinculo, matricula, diasPermitidos);
		}
		
		return lista;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}

package br.gov.mec.aghu.controleinfeccao.business;

import javax.ejb.EJB;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MciTipoGrupoProcedRiscoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2913162975976111610L;
	
	private static final Log LOG = LogFactory.getLog(MciTipoGrupoProcedRiscoON.class);
	
	@EJB
	private MciTipoGrupoProcedRiscoRN grupoProcedRiscoRN;	

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public void inserirMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) throws ApplicationBusinessException{
		getGrupoProcedRiscoRN().inserirTipoGrupoProcedimentoRisco(entidade, usuarioLogado);
	}
	
	public void alterarMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) throws ApplicationBusinessException{
		getGrupoProcedRiscoRN().alterarMciTipoGrupoProcedRisco(entidade, usuarioLogado);
	}
	
	
	public void excluirMciTipoGrupoProcedRisco(MciTipoGrupoProcedRisco entidade, RapServidores usuarioLogado) throws ApplicationBusinessException{
		getGrupoProcedRiscoRN().excluirMciTipoGrupoProcedRisco(entidade, usuarioLogado);
	}	

	/**
	 * @return the grupoProcedRiscoRN
	 */
	protected MciTipoGrupoProcedRiscoRN getGrupoProcedRiscoRN() {
		return grupoProcedRiscoRN;
	}
}

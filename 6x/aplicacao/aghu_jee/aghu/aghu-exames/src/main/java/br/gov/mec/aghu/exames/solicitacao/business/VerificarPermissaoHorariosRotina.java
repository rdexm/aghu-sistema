package br.gov.mec.aghu.exames.solicitacao.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class VerificarPermissaoHorariosRotina extends DesenhaDataHoraISE {

	private static final Log LOG = LogFactory.getLog(VerificarPermissaoHorariosRotina.class);
    private static final long serialVersionUID = 4813863637563033166L;

    @Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Override
	public TipoCampoDataHoraISE processRequest(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws ApplicationBusinessException{
		TipoCampoDataHoraISE campo = TipoCampoDataHoraISE.CALENDAR; 

		AghUnidadesFuncionais unidadeDefault = getUnidadeDefault();

        if (getAelPermissaoUnidSolic() != null) {
            if (unidadeDefault.equals(getAelPermissaoUnidSolic().getUnfSeqAvisa())
                    && getAelPermissaoUnidSolic().getIndPermiteProgramarExames() != null
                    && DominioSimNaoRotina.R == getAelPermissaoUnidSolic().getIndPermiteProgramarExames()) {
                campo = TipoCampoDataHoraISE.COMBO;
            } else if (getAelPermissaoUnidSolic().getIndPermiteProgramarExames() != null
                    && DominioSimNaoRotina.N == getAelPermissaoUnidSolic().getIndPermiteProgramarExames()) {
                campo = TipoCampoDataHoraISE.CALENDAR_BLOQUEADO;
            } else if (getAelPermissaoUnidSolic().getIndPermiteProgramarExames() != null
                    && DominioSimNaoRotina.S == getAelPermissaoUnidSolic().getIndPermiteProgramarExames()) {
                campo = TipoCampoDataHoraISE.CALENDAR;
            }
        }
		return campo;
	}

	protected AghUnidadesFuncionais getUnidadeDefault() {
		AghParametros parametro;
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();

		try {
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT);
			//AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO = AGHUDAOFactory.getDAO(AghUnidadesFuncionaisDAO.class);
			unidade = getAghuFacade().obterUnidadeFuncional(Short.valueOf(parametro.getVlrNumerico().shortValue()));
		} catch (ApplicationBusinessException e) {
			unidade = null;
		}
		return unidade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}

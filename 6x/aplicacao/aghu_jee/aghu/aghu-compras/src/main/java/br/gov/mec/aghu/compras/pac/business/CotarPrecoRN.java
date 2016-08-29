package br.gov.mec.aghu.compras.pac.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoDeCompraDAO;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class CotarPrecoRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = -4216766104835407921L;

	public enum DuplicaPACONExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_CLONAR_PAC;
	}
	
	private static final Log LOG = LogFactory.getLog(CotarPrecoRN.class);
	
	@Inject
	private ScoSolicitacaoDeCompraDAO scoSolicitacaoDeCompraDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	/**
	 * RN03 - 22176
	 * @return
	 */
	public String gerarIdentificacaoCotacao() {
		String dataAtual = DateUtil.obterDataFormatada(new Date(), "ddMMyyy");
		if (dataAtual != null) {
			Integer codigoCotacao = this.getScoSolicitacaoDeCompraDAO().getNextVal(SequenceID.SCO_SEQ_COTACAO);
			if (codigoCotacao != null) {
				String codigoCotacaoString = codigoCotacao.toString();
				if (codigoCotacaoString.length() == 1) {
					return dataAtual + "00" + codigoCotacao;
				} else if (codigoCotacaoString.length() == 2) {
					return dataAtual + "0" + codigoCotacaoString;
				} else {
					return dataAtual + codigoCotacaoString;
				}
			}
		}
		return null;
	}
	
	/**
	 * RN04 - 22176
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String obterRamalCotacao() throws ApplicationBusinessException {
		
		String telefone = null;
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		
		if (servidor != null && servidor.getRamalTelefonico() != null) {
			String dd = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DDD_FONE_HOSPITAL);
			String prefixo = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_PREFIXO_FONE_HOSPITAL);
			telefone = "(" + dd + ")" + prefixo + servidor.getRamalTelefonico();
		}else{
			telefone = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_FONE_DEPT_COMPRAS);	
		}


		return telefone;
	}


	private ScoSolicitacaoDeCompraDAO getScoSolicitacaoDeCompraDAO() {
		return scoSolicitacaoDeCompraDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

}

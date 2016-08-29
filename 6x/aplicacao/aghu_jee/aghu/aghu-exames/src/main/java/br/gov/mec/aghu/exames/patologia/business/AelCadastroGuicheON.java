package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.exames.dao.AelCadGuicheDAO;
import br.gov.mec.aghu.exames.vo.AelIdentificarGuicheVO;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class AelCadastroGuicheON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelCadastroGuicheON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelCadGuicheDAO aelCadGuicheDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = -798406697870064652L;

	public List<AelIdentificarGuicheVO> pesquisarAelCadGuiche(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final AelCadGuiche aelCadGuiche) {
		
		List<AelIdentificarGuicheVO> lista = getAelCadGuicheDAO().pesquisarAelCadGuiche(firstResult, maxResults, orderProperty, asc, aelCadGuiche);
		
		IRegistroColaboradorFacade ircf = getRegistroColaboradorFacade();
		
		String nomeUsuario;
		for (AelIdentificarGuicheVO vo : lista ) {
			nomeUsuario = null;
			try {
				if(vo.getUsuario() != null){
					nomeUsuario = ircf.obterServidorPorUsuario(vo.getUsuario()).getPessoaFisica().getNome();
					if (nomeUsuario == null) {
						nomeUsuario = this.getICascaFacade().recuperarUsuario(vo.getUsuario()).getNome();
					}
					vo.setNomeUsuario(nomeUsuario);
				}
				if(vo.getUnidadeFuncional() != null){
					vo.setSeqAndarAlaDescricao(vo.getUnidadeFuncional().getSeqAndarAlaDescricao());
				}
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
			}
		}
		
		return lista;
	}
	
	private AelCadGuicheDAO getAelCadGuicheDAO() {
		return aelCadGuicheDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return (ICascaFacade) cascaFacade;
	}
	
}
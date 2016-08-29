package br.gov.mec.aghu.casca.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.RapServidores;


@Modulo(ModuloEnum.CASCA)
@Stateless
public class CentralPendenciaFacade extends BaseFacade implements ICentralPendenciaFacade {

	@EJB
	private CentralPendenciaON centralPendenciaON;
	
	private static final long serialVersionUID = -2650433857022166407L;
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.casca.business.ICentralPendenciaFacade#getListaPendencias()
	 */
	@Override
	public List<PendenciaVO> getListaPendencias()  throws ApplicationBusinessException {

		return this.getCentralPendenciaON().getListaPendencias();
	}

	@Override
	public void excluirPendencia(Long seqCaixaPostal) throws ApplicationBusinessException{
		this.getCentralPendenciaON().excluirPendencia(seqCaixaPostal);
	}
	
	@Override
	public void adicionarPendenciaAcao(String mensagem, String url,
			String descricaoAba, List<RapServidores> listaServidores,
			Boolean enviarEmail) throws ApplicationBusinessException {
		this.getCentralPendenciaON().adicionarPendenciaAcao(mensagem, url, descricaoAba, listaServidores, enviarEmail);
	}
	
	@Override
	public void adicionarPendenciaAcao(String mensagem, String url, String descricaoAba, RapServidores servidor, Boolean enviarEmail)
			throws ApplicationBusinessException {
		this.getCentralPendenciaON().adicionarPendenciaAcao(mensagem, url, descricaoAba, servidor, enviarEmail);
	}

	@Override
	public void adicionarPendenciaInformacao(String mensagem, List<RapServidores> listaServidores, Boolean enviarEmail) throws ApplicationBusinessException{
		this.getCentralPendenciaON().adicionarPendenciaInformacao(mensagem, listaServidores, enviarEmail);
	}

	@Override
	public void adicionarPendenciaParaServidores(AghCaixaPostal caixaPostal,
			List<RapServidores> listaServidores, Boolean enviarEmail) throws ApplicationBusinessException {
		this.getCentralPendenciaON().adicionarPendenciaParaServidores(caixaPostal, listaServidores, enviarEmail);
	}

	@Override
	public Integer buscaTempoRefreshPendencias(RapServidores servidor) {
		return this.getCentralPendenciaON().buscaTempoRefreshPendencias(servidor);
	}
	
	@Override
	public void excluirPendenciaComUsuarioSelecionado(Long seq, RapServidores usuarioSelecionado) {
		this.getCentralPendenciaON().excluirPendenciaComUsuarioSelecionado(seq, usuarioSelecionado);
	}

	protected CentralPendenciaON getCentralPendenciaON(){
		return centralPendenciaON;
	}
}
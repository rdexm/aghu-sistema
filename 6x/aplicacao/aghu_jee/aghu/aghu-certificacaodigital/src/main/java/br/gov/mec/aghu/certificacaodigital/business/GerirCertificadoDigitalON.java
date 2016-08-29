package br.gov.mec.aghu.certificacaodigital.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.dao.AghCertificadoDigitalDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghCertificadoDigital;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class GerirCertificadoDigitalON extends BaseBusiness {


private static final Log LOG = LogFactory.getLog(GerirCertificadoDigitalON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private ICascaFacade cascaFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AghCertificadoDigitalDAO aghCertificadoDigitalDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8644599687561837338L;

	public AghCertificadoDigital persistirAghCertificadoDigital(
			AghCertificadoDigital aghCertificadoDigital)
			throws ApplicationBusinessException {

		AghCertificadoDigital retorno = null;

		if (aghCertificadoDigital.getSeq() == null) {

			// Se o usuário está emitindo um certificado e não preencheu a data
			// de emissão
			// grava com a data atual
			if (aghCertificadoDigital.getIndEmissaoCertif().equals(
					DominioSimNao.S)
					&& aghCertificadoDigital.getDtEmissaoCertif() == null) {
				aghCertificadoDigital.setDtEmissaoCertif(GregorianCalendar
						.getInstance().getTime());
			}

			// Realiza inserção
			getAghCertificadoDigitalDAO().persistir(
					aghCertificadoDigital);
			
			retorno = aghCertificadoDigital;
		} else {
			// Realiza atualização
			aghCertificadoDigital.setIndEmissaoCertif(DominioSimNao.S);

			short quantidade = aghCertificadoDigital.getQtdEmissoes()
					.shortValue();

			// Se não é emissão da 1ª via do certificado
			if (quantidade > 0) {
				// Grava a data de emissao do certifico na data de emissao do
				// certificado anterior
				aghCertificadoDigital
						.setDtEmissaoCertifAnt(aghCertificadoDigital
								.getDtEmissaoCertif());
			}

			quantidade++;
			aghCertificadoDigital.setQtdEmissoes(quantidade);
			aghCertificadoDigital.setDtEmissaoCertif(Calendar.getInstance()
					.getTime());

			retorno = getAghCertificadoDigitalDAO().atualizar(
					aghCertificadoDigital);
		}

		return retorno;
	}

	public List<RapServidores> pesquisarServidorAtivoPermissaoAssinaturaDigital(
			Object paramPesquisa) {

		List<RapServidores> servidoresComPermissao = new ArrayList<RapServidores>();
		IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();

		List<String> listaUsuariosDosServidores = registroColaboradorFacade
				.pesquisarServidorAtivo(paramPesquisa);

		List<String> usuariosComPermissao = getICascaFacade()
				.pesquisarUsuariosComPermissao("assinaturaDigital");

		if (listaUsuariosDosServidores != null
				&& !listaUsuariosDosServidores.isEmpty()
				&& usuariosComPermissao != null
				&& !usuariosComPermissao.isEmpty()) {

			listaUsuariosDosServidores.retainAll(usuariosComPermissao);

			// 100 é o limite atualmente usado nas suggestion-box
			for (int i = 0; i < 100 && i < listaUsuariosDosServidores.size(); i++) {
				try {
					servidoresComPermissao.add(registroColaboradorFacade
							.obterServidorAtivoPorUsuario(listaUsuariosDosServidores.get(i)));
				} catch (ApplicationBusinessException e) {
					logError(e);
					// apenas faz o log da exceção pois neste caso não é
					// necessário
					// para o negócio exibi-la uma vez que esta consulta é usada
					// em uma suggestion-box
				}
			}
		}

		return servidoresComPermissao;
	}

	public boolean validarServidorPossuiPermissaoAssinaturaDigital(Object paramPesquisa) {

		RapServidores servidor = null;
		IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
		List<String> usuariosDosServidores = null;
		if(paramPesquisa instanceof String){
			usuariosDosServidores = new ArrayList<String>();
			usuariosDosServidores.add(paramPesquisa.toString());
		}

		List<String> usuariosComPermissao = getICascaFacade().pesquisarUsuariosComPermissao("assinaturaDigital");
		
		if (usuariosDosServidores != null && !usuariosDosServidores.isEmpty()
				&& usuariosComPermissao != null && !usuariosComPermissao.isEmpty()) {
			
			usuariosDosServidores.retainAll(usuariosComPermissao);
			try {
				String servidorString = usuariosDosServidores.get(0);
				servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(servidorString);
			} catch (ApplicationBusinessException e) {
				logError(e);
			}catch (IndexOutOfBoundsException ex) {
				logError(ex);
			}
				
		}
		if(servidor == null){
			return false;
		}
		else {
			return true;
		}
		
	}

	protected AghCertificadoDigitalDAO getAghCertificadoDigitalDAO() {
		return aghCertificadoDigitalDAO;
	}

	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}

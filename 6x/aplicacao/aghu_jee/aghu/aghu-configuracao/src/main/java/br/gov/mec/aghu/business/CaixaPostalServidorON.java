package br.gov.mec.aghu.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class CaixaPostalServidorON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CaixaPostalServidorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7972605096964942398L;

	
	public void excluirMensagem(AghCaixaPostalServidorId id) {
		AghCaixaPostalServidor msg = getAghCaixaPostalServidorDAO().obterPorChavePrimaria(id);
		if(msg != null) {
			msg.setDthrExcluida(new Date());
			msg.setSituacao(DominioSituacaoCxtPostalServidor.E);
			getAghCaixaPostalServidorDAO().atualizar(msg);
		}
	}
	
	private AghCaixaPostalServidorDAO getAghCaixaPostalServidorDAO() {
		return aghCaixaPostalServidorDAO;
	}
}

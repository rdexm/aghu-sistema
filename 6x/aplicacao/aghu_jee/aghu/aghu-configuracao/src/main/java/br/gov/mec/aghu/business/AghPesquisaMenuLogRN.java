package br.gov.mec.aghu.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghPesquisaMenuLogDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.model.AghPesquisaMenuLog;
import br.gov.mec.aghu.model.RapServidores;


@Stateless
public class AghPesquisaMenuLogRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5520159856034775415L;
	
	private static final Log LOG = LogFactory.getLog(AghPesquisaMenuLogRN.class);
	
	@Inject
	private AghPesquisaMenuLogDAO aghPesquisaMenuLogDAO;

	private AghPesquisaMenuLogDAO getAghPesquisaMenusDAO(){
		return aghPesquisaMenuLogDAO;
	}
	
	public void gravarPesquisaMenuLog(String nome, String url, RapServidores servidor) {
		AghPesquisaMenuLog log = new AghPesquisaMenuLog();
		log.setCriadoEm(new Date());
		log.setServidor(servidor);
		log.setUrl(url);
		log.setMenuPesquisado(nome);
		getAghPesquisaMenusDAO().persistir(log);
		getAghPesquisaMenusDAO().flush();
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}

package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoTipoOcorrFornDAO;
import br.gov.mec.aghu.compras.vo.CadastroTipoOcorrenciaFornecedorVO;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class CadastroTipoOcorrenciaFornecedorRN extends BaseBusiness{

	private static final long serialVersionUID = -5220563566996169146L;
	
	private static final Log LOG = LogFactory.getLog(CadastroTipoOcorrenciaFornecedorRN.class);
	
	@Inject
	private ScoTipoOcorrFornDAO scoTipoOcorrFornDAO;

	public List<ScoTipoOcorrForn> buscarTipoOcorrenciaFornecedor(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, CadastroTipoOcorrenciaFornecedorVO filtro) {
		return getScoTipoOcorrFornDAO().buscarTipoOcorrenciaFornecedor(firstResult, maxResults, orderProperty, asc, filtro);
	}

	public Long buscarTipoOcorrenciaFornecedorCount(CadastroTipoOcorrenciaFornecedorVO filtro) {
		return getScoTipoOcorrFornDAO().buscarTipoOcorrenciaFornecedorCount(filtro);
	}

	protected ScoTipoOcorrFornDAO getScoTipoOcorrFornDAO() {
		return scoTipoOcorrFornDAO;
	}

	protected void setScoTipoOcorrFornDAO(ScoTipoOcorrFornDAO scoTipoOcorrFornDAO) {
		this.scoTipoOcorrFornDAO = scoTipoOcorrFornDAO;
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return LOG;
	}
	
	

}

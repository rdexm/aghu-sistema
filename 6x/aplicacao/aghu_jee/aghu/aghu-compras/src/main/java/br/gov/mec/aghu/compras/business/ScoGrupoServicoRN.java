package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoGrupoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoGrupoServicoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoGrupoServicoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	private static final long serialVersionUID = 4700050836781652284L;
	
	@Inject
	private ScoServicoDAO scoServicoDAO;
	
	@Inject ScoGrupoServicoDAO scoGrupoServicoDAO;

	public enum ScoGrupoServicoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_M1_GRUPO_SERVICO;
	}

	public void inserir(ScoGrupoServico grupoServico) throws BaseException {
		this.getScoGrupoServicoDAO().persistir(grupoServico);
	}

	public void atualizar(ScoGrupoServico grupoServico) throws BaseException {
		this.getScoGrupoServicoDAO().atualizar(grupoServico);
	}

	public void excluir(ScoGrupoServico grupoServico) throws ApplicationBusinessException {
		this.preExcluir(grupoServico);
		this.getScoGrupoServicoDAO().remover(grupoServico);
	}
	
	private void preExcluir(ScoGrupoServico grupoServico) throws ApplicationBusinessException {
		List<ScoServico> servicos = this.getScoServicoDAO().listarServicosByCodigoGrupo(grupoServico.getCodigo());
		if(servicos!=null && !servicos.isEmpty()){
			//Este Grupo de Serviços está sendo utilizado e não pode ser excluído.  M1
			throw new ApplicationBusinessException(ScoGrupoServicoRNExceptionCode.MENSAGEM_M1_GRUPO_SERVICO);
		}
	}
	
	private ScoGrupoServicoDAO getScoGrupoServicoDAO() {
		return this.scoGrupoServicoDAO;
	}
	
	private ScoServicoDAO getScoServicoDAO() {
		return this.scoServicoDAO;
	}
	
}
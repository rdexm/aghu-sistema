package br.gov.mec.aghu.business.bancosangue;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ManterProcedHemoterapicoON  extends BaseBusiness{


	private static final long serialVersionUID = 5169327019264822308L;
	
	private static final Log LOG = LogFactory.getLog(ManterProcedHemoterapicoON.class);

	@Inject
	private AbsProcedHemoterapicoDAO absProcedHemoterapicoDAO;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO  absSolicitacoesHemoterapicasDAO;
	
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}


	/**
	 * Obtém lista de Procedimentos Hemoterápicos utilizando vários filtros, se informados. Ordena por código e depois descrição, de forma ascendente.
	 */
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(String codigo, String descricao, Boolean indAmostra, Boolean indJustificativa, DominioSituacao indSituacao, int firstResult, int maxResults){
		return getAbsProcedHemoterapicoDAO().listarAbsProcedHemoterapicos(codigo, descricao, indAmostra, indJustificativa, indSituacao, firstResult, maxResults);
	}
	
	public Long listarAbsProcedHemoterapicosCount(String codigo, String descricao, Boolean indAmostra, Boolean indJustificativa, DominioSituacao indSituacao) {
		return getAbsProcedHemoterapicoDAO().listarAbsProcedHemoterapicosCount(codigo, descricao, indAmostra, indJustificativa, indSituacao);
	}
	
	protected AbsProcedHemoterapicoDAO getAbsProcedHemoterapicoDAO() {
		return absProcedHemoterapicoDAO;
	}

	protected AbsSolicitacoesHemoterapicasDAO getAbsSolicitacoesHemoterapicasDAO() {
		return absSolicitacoesHemoterapicasDAO;
	}
	

	public AbsSolicitacoesHemoterapicas obterPorChavePrimariaComItensSolicitacoes(Integer atdSeq, Integer seq) {
		AbsSolicitacoesHemoterapicas solicitacoesHemoterapicas = 
				this.getAbsSolicitacoesHemoterapicasDAO().obterPorChavePrimariaComItensSolicitacoes(new AbsSolicitacoesHemoterapicasId(atdSeq, seq));
		
		return solicitacoesHemoterapicas;
	}
}

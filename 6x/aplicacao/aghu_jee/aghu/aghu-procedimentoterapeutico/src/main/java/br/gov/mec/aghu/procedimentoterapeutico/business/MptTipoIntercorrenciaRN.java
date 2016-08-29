package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoIntercorrenciaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoIntercorrenciaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoIntercorrenciaJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptTipoIntercorrenciaRN extends BaseBusiness {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8136824783425221701L;

	private static final Log LOG = LogFactory.getLog(MptTipoIntercorrenciaRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MptTipoIntercorrenciaDAO mptTipoIntercorrenciaDAO;
	
	@Inject
	private MptTipoIntercorrenciaJnDAO mptTipoIntercorrenciaJnDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	public void salvarTipoIntercorrente(MptTipoIntercorrencia tipoIntercorrencia, boolean situacao, boolean emEdicao) throws ApplicationBusinessException {
	
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		if(emEdicao){
			MptTipoIntercorrencia tipoIntercorrenciaOld = this.mptTipoIntercorrenciaDAO.obterPorChavePrimaria(tipoIntercorrencia.getSeq());
			salvarHistorico(tipoIntercorrenciaOld, servidor, emEdicao);
			if(situacao){
				tipoIntercorrenciaOld.setIndSituacao(DominioSituacao.A);
			}else{
				tipoIntercorrenciaOld.setIndSituacao(DominioSituacao.I);
			}
			tipoIntercorrenciaOld.setDescricao(tipoIntercorrencia.getDescricao());
			this.mptTipoIntercorrenciaDAO.atualizar(tipoIntercorrenciaOld);
			this.mptTipoIntercorrenciaDAO.flush();
			
		}else{
			MptTipoIntercorrencia newTipoIntercorrencia = new MptTipoIntercorrencia();
			newTipoIntercorrencia.setServidor(servidor);
			newTipoIntercorrencia.setVinCodigo(servidor.getId().getVinCodigo().intValue());
			newTipoIntercorrencia.setSerVinCodigo(servidor.getId().getMatricula());
			newTipoIntercorrencia.setCriadoEm(new Date());
			newTipoIntercorrencia.setDescricao(tipoIntercorrencia.getDescricao());
			if(situacao){
				newTipoIntercorrencia.setIndSituacao(DominioSituacao.A);
			}else{
				newTipoIntercorrencia.setIndSituacao(DominioSituacao.I);
			}
			this.mptTipoIntercorrenciaDAO.persistir(newTipoIntercorrencia);
		}
	}
	
	public void salvarHistorico(MptTipoIntercorrencia tipoIntercorrencia, RapServidores servidor, boolean emEdicao){
		MptTipoIntercorrenciaJn tipIntercorJn = new MptTipoIntercorrenciaJn();
		tipIntercorJn.setCriadoEm(new Date());
		tipIntercorJn.setDescricao(tipoIntercorrencia.getDescricao());
		tipIntercorJn.setIndSituacao(tipoIntercorrencia.getIndSituacao());
		tipIntercorJn.setOperacao(DominioOperacaoBanco.UPD);
		tipIntercorJn.setSeq(tipoIntercorrencia.getSeq().intValue());
		tipIntercorJn.setServidor(servidor);
		tipIntercorJn.setUsuario(servidor.getUsuario());
		tipIntercorJn.setSerVinCodigo(servidor.getId().getMatricula());
		tipIntercorJn.setVinCodigo(servidor.getId().getVinCodigo().intValue());
		mptTipoIntercorrenciaJnDAO.persistir(tipIntercorJn);
	}

	public MptTipoIntercorrenciaJnDAO getMptTipoIntercorrenciaJnDAO() {
		return mptTipoIntercorrenciaJnDAO;
	}

	public void setMptTipoIntercorrenciaJnDAO(MptTipoIntercorrenciaJnDAO mptTipoIntercorrenciaJnDAO) {
		this.mptTipoIntercorrenciaJnDAO = mptTipoIntercorrenciaJnDAO;
	}
	
	public MptTipoIntercorrenciaDAO getMptTipoIntercorrenciaDAO() {
		return mptTipoIntercorrenciaDAO;
	}

	public void setMptTipoIntercorrenciaDAO(MptTipoIntercorrenciaDAO mptTipoIntercorrenciaDAO) {
		this.mptTipoIntercorrenciaDAO = mptTipoIntercorrenciaDAO;
	}
}
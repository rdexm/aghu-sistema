package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.SigComunicacaoEventos;
import br.gov.mec.aghu.sig.dao.SigComunicacaoEventosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterComunicacaoEventoON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterComunicacaoEventoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigComunicacaoEventosDAO sigComunicacaoEventosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5781463457106124466L;
	
	public enum ManterComunicacaoEventoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_COMUNICACAO_EVENTO_DUPLICIDADE_COM_CC,
		MENSAGEM_COMUNICACAO_EVENTO_DUPLICIDADE_SEM_CC;
	}

	
	public void persistirComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos) throws ApplicationBusinessException {
		if(this.verificaDuplicidadeComunicacaoEvento(sigComunicacaoEventos)){
			if(sigComunicacaoEventos.getFccCentroCustos() != null){
				throw new ApplicationBusinessException(ManterComunicacaoEventoONExceptionCode.MENSAGEM_COMUNICACAO_EVENTO_DUPLICIDADE_COM_CC, sigComunicacaoEventos.getTipoEvento().getDescricao(),
						sigComunicacaoEventos.getServidor().getPessoaFisica().getNome(), sigComunicacaoEventos.getFccCentroCustos().getCodigo(), sigComunicacaoEventos.getFccCentroCustos().getDescricao());
			}else {
				throw new ApplicationBusinessException(ManterComunicacaoEventoONExceptionCode.MENSAGEM_COMUNICACAO_EVENTO_DUPLICIDADE_SEM_CC, sigComunicacaoEventos.getTipoEvento().getDescricao(), 
						sigComunicacaoEventos.getServidor().getPessoaFisica().getNome());
			}
			
		}else {
			if(sigComunicacaoEventos.getSeq() == null){
				sigComunicacaoEventos.setCriadoEm(new Date());
				this.getSigComunicacaoEventosDAO().persistir(sigComunicacaoEventos);
			}else {
				this.getSigComunicacaoEventosDAO().atualizar(sigComunicacaoEventos);
			}
		}
	}

	private boolean verificaDuplicidadeComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos) {
		List<SigComunicacaoEventos> list = this.getSigComunicacaoEventosDAO().buscaDuplicidadeComunicacaoEvento(sigComunicacaoEventos);
		if(list != null && !list.isEmpty()){
			return true;
		}else {
			return false;
		}
	}
	
	public SigComunicacaoEventos obterComunicacaoEvento(
			Integer seqComunicacaoEvento) {
		SigComunicacaoEventos comunicacaoEvento = this.getSigComunicacaoEventosDAO().obterPorChavePrimaria(seqComunicacaoEvento, true, SigComunicacaoEventos.Fields.CENTRO_CUSTO, SigComunicacaoEventos.Fields.SERVIDOR);
		if(comunicacaoEvento != null && comunicacaoEvento.getServidor() != null && comunicacaoEvento.getServidor().getPessoaFisica() != null){
			comunicacaoEvento.getServidor().getPessoaFisica().getNome();
		}
		return comunicacaoEvento;
	}
	
	
	// DAOs
	protected SigComunicacaoEventosDAO getSigComunicacaoEventosDAO() {
		return sigComunicacaoEventosDAO;
	}

	

}

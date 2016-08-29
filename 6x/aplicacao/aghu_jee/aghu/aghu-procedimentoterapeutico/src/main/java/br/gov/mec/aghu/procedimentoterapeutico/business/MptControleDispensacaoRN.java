package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptControleDispensacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptControleDispensacaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MptControleDispensacaoRN extends BaseBusiness {


@Inject
private MptControleDispensacaoDAO mptControleDispensacaoDAO;
	
	private static final Log LOG = LogFactory.getLog(MptControleDispensacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 6128807011427618199L;

	public void atualizar(MptControleDispensacao elemento) {
		MptControleDispensacao elementoAntigo = getMptControleDispensacaoDAO().obterOriginal(elemento);
		preAtualizar(elemento,elementoAntigo);
		getMptControleDispensacaoDAO().atualizar(elemento);
	}
	
	private void preAtualizar(MptControleDispensacao elemento,
			MptControleDispensacao elementoAntigo) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(!elementoAntigo.getIndSolicDispensacao().equals(elemento.getIndSolicDispensacao())){
			if(elemento.getIndSolicDispensacao()){
				elemento.setSerMatriculaSolicita(servidorLogado.getId().getMatricula());
				elemento.setSerVinCodigoSolicita(servidorLogado.getId().getVinCodigo());
				elemento.setDthrSolicDispensacao(new Date());
			}else{
				elemento.setSerMatriculaSolicita(null);
				elemento.setSerVinCodigoSolicita(null);
				elemento.setDthrSolicDispensacao(null);
			}
		}
		
		if(!elementoAntigo.getTipoRetorno().equals(elemento.getTipoRetorno())){
			if(elemento.getTipoRetorno() != null){
				elemento.setSerMatriculaRetorno(servidorLogado.getId().getMatricula());
				elemento.setSerVinCodigoRetorno(servidorLogado.getId().getVinCodigo());
				elemento.setDthrInfRetorno(new Date());
			}else{
				elemento.setSerMatriculaSolicita(null);
				elemento.setSerVinCodigoSolicita(null);
				elemento.setDthrSolicDispensacao(null);
			}
		}
	}

	protected MptControleDispensacaoDAO getMptControleDispensacaoDAO(){
		return mptControleDispensacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}

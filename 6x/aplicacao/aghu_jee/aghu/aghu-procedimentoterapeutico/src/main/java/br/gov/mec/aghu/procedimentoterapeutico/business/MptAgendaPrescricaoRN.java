package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptControleDispensacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendaPrescricaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptControleDispensacaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MptAgendaPrescricaoRN extends BaseBusiness {

	@Inject
	private MptAgendaPrescricaoDAO mptAgendaPrescricaoDAO;
	
	@Inject
	private MptControleDispensacaoDAO mptControleDispensacaoDAO;
	
	@EJB
	private MptControleDispensacaoRN mptControleDispensacaoRN;
	
	private static final Log LOG = LogFactory.getLog(MptAgendaPrescricaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 8980074441235150835L;

	public void atualizar(MptAgendaPrescricao elemento){
		MptAgendaPrescricao elementoAntigo = getMptAgendaPrescricaoDAO().obterOriginal(elemento);
		preAtualizar(elemento,elementoAntigo);
		getMptAgendaPrescricaoDAO().atualizar(elemento);
	}
	
	/**
	 * @ORADB MPTT_AGP_BRU
	 * @param elemento
	 * @param elementoAntigo
	 */
	private void preAtualizar(MptAgendaPrescricao elemento,
			MptAgendaPrescricao elementoAntigo) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if( !DateUtil.isDatasIguais(elementoAntigo.getDthrRetorno(), elemento.getDthrRetorno())  &&
				!elementoAntigo.getTipoRetorno().equals(elemento.getTipoRetorno())){
			elemento.setSerMatricula(servidorLogado.getId().getMatricula());
			elemento.setSerVinCodigo(servidorLogado.getId().getVinCodigo());		
			
			if(elemento.getDthrRetorno() != null && elemento.getTipoRetorno() != null){
				elemento.setSerMatriculaRetorno(servidorLogado.getId().getMatricula());
				elemento.setSerVinCodigoRetorno(servidorLogado.getId().getVinCodigo());
				elemento.setDthrInfRetorno(new Date());
			}else{
				elemento.setSerMatriculaRetorno(null);
				elemento.setSerVinCodigoRetorno(null);
				elemento.setDthrInfRetorno(null);
			}
		}
		
		if(!elementoAntigo.getSituacao().equals(elemento.getSituacao()) && elemento.getSituacao().equals(DominioSituacao.I)){
			atualizarIndicadorControleDispensacao(elemento);
		}
	}

	/**
	 * @ORADB mptk_agp_rn.rn_agpp_atu_disp
	 * @param elemento
	 */
	private void atualizarIndicadorControleDispensacao(
			MptAgendaPrescricao elemento) {		
		//[REFEITO O MAPEAMENTO NO POJO]
		//List<MptControleDispensacao> lista = getMptControleDispensacaoDAO().pesquisarControlesDispensacaoPorParam(elemento.getId().getPteAtdSeq(),elemento.getId().getPteSeq(),elemento.getUnfSeq(), "S");
				
		List<MptControleDispensacao> lista = getMptControleDispensacaoDAO().pesquisarControlesDispensacaoPorParam(elemento.getId().getPteAtdSeq(),elemento.getId().getPteSeq(),elemento.getUnidadeFuncional().getSeq(), "S");
		for(MptControleDispensacao controle : lista){
			getMptControleDispensacaoRN().atualizar(controle);
		}
	}

	protected MptAgendaPrescricaoDAO getMptAgendaPrescricaoDAO(){
		return mptAgendaPrescricaoDAO;
	}
	
	protected MptControleDispensacaoDAO getMptControleDispensacaoDAO(){
		return mptControleDispensacaoDAO;
	}
	
	protected MptControleDispensacaoRN getMptControleDispensacaoRN(){
		return mptControleDispensacaoRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}

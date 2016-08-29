package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaTipoComposicoesDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AfaTipoComposicoesRN extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(AfaTipoComposicoesRN.class);
	
	@Inject
	private AfaTipoComposicoesDAO afaTipoComposicoesDAO;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private AfaComposGrupoComponentesRN afaComposGrupoComponentesRN;

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public enum AfaTipoComposicoesRNExceptionCode implements BusinessExceptionCode {
		TIPO_COMPOSICAO_MS06,TIPO_COMPOSICAO_MS04,TIPO_COMPOSICAO_MS05;
	}

	/**
	 * 
	 * @param afaTipoComposicoes
	 * @param firstResult, maxResult, orderProperty, asc
	 * @return
	 */
	public List<ConsultaTipoComposicoesVO> pesquisarTiposComposicaoNPT(AfaTipoComposicoes afaTipoComposicoes){
		List<ConsultaTipoComposicoesVO> listaComposicoes = new ArrayList<ConsultaTipoComposicoesVO>();
		listaComposicoes = afaTipoComposicoesDAO.pesquisarTiposComposicaoNPT(afaTipoComposicoes);
		
		for (ConsultaTipoComposicoesVO consultaTipoComposicoesVO : listaComposicoes){
			String criadoPor = afaTipoComposicoesDAO.listaInfoCriacaoTipoComposicao(consultaTipoComposicoesVO.getSeq());
			consultaTipoComposicoesVO.setCriadoPor(criadoPor);
		}
		return listaComposicoes;
	}
	
	public void removerAfaTipoComposicoes(Short seq) throws ApplicationBusinessException{
		AfaTipoComposicoes afaTipoComposicoes = afaTipoComposicoesDAO.obterPorChavePrimaria(seq);
		preRemover(afaTipoComposicoes);
		afaTipoComposicoesDAO.remover(afaTipoComposicoes);
	}
	
	/**
	 * #3506
	 * Antes de remover verifica RN01
	 * @param afaTipoComposicoes
	 * @throws ApplicationBusinessException
	 */
	public void preRemover(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException{
		
		AghParametros parametro = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_DIAS_PERM_DEL_AFA.toString());
		if(!afaComposGrupoComponentesRN.pesquisaGrupoComponenteNPT(afaTipoComposicoes.getSeq()).isEmpty()){
			throw new ApplicationBusinessException(AfaTipoComposicoesRNExceptionCode.TIPO_COMPOSICAO_MS06);
		}
		Integer dias = CoreUtil.diferencaEntreDatasEmDias(new Date(), afaTipoComposicoes.getCriadoEm()).intValue();
		if(dias > parametro.getVlrNumerico().intValue()){
			throw new ApplicationBusinessException(AfaTipoComposicoesRNExceptionCode.TIPO_COMPOSICAO_MS05);
		}
	}
		
	public void gravarAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException{
		if(afaTipoComposicoes != null){
			preInserir(afaTipoComposicoes);
			afaTipoComposicoesDAO.persistir(afaTipoComposicoes);
		}
	}
	public void alterarAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException{
		
		if(afaTipoComposicoes != null){
			AfaTipoComposicoes afaTipoComposicoesOriginal = afaTipoComposicoesDAO.obterOriginal(afaTipoComposicoes.getSeq());
			preUpdate(afaTipoComposicoes, afaTipoComposicoesOriginal);
			
			if(afaTipoComposicoes.getIndSituacao() == DominioSituacao.I && 
					afaTipoComposicoesOriginal.getIndSituacao() == DominioSituacao.A){
				if(!pesquisarListaGrupoComposicoesNPT(afaTipoComposicoes.getSeq()).isEmpty()){
					throw new ApplicationBusinessException(AfaTipoComposicoesRNExceptionCode.TIPO_COMPOSICAO_MS04);
				}
			}
			afaTipoComposicoesOriginal.setIndSituacao(afaTipoComposicoes.getIndSituacao());
			afaTipoComposicoesDAO.merge(afaTipoComposicoesOriginal);
		}
	}

	private void preUpdate(AfaTipoComposicoes afaTipoComposicoes, AfaTipoComposicoes afaTipoComposicoesOriginal) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		afaTipoComposicoesOriginal.setDescricao(afaTipoComposicoes.getDescricao());
		afaTipoComposicoesOriginal.setOrdem(afaTipoComposicoes.getOrdem());
		afaTipoComposicoesOriginal.setIndProducao(afaTipoComposicoes.getIndProducao());
		afaTipoComposicoesOriginal.setServidor(servidorLogado);
	}
	
	/**
	 * #3506
	 * @param afaTipoComposicoes
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException{
		afaTipoComposicoes.setCriadoEm(new Date());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		afaTipoComposicoes.setServidor(servidorLogado);
	}
	
	/**C6
	 * #3506
	 * Exibir lista de grupos de composição
	 * @param seq
	 * @return
	 */
	public List<ConsultaCompoGrupoComponenteVO> pesquisarListaGrupoComposicoesNPT(Short seq){
		return afaComposGrupoComponentesRN.pesquisaGrupoComponenteNPT(seq);
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
}
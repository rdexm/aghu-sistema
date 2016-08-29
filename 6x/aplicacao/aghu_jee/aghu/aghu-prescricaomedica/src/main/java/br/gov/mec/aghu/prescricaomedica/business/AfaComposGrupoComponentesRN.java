package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaCompoGrupoComponenteId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.AfaComposGrupoComponentesDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class AfaComposGrupoComponentesRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4924044359930323363L;

	private static final Log LOG = LogFactory.getLog(AfaComposGrupoComponentesRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaComposGrupoComponentesDAO afaComposGrupoComponentesDAO;
	
	private AfaCompoGrupoComponente afaCompoGrupoComponente = new AfaCompoGrupoComponente();
	private AfaCompoGrupoComponenteId afaCompoGrupoComponenteId =  new AfaCompoGrupoComponenteId();

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public enum AfaComposGrupoComponentesRNExceptionCode implements BusinessExceptionCode {
		TIPO_COMPOSICAO_MS07, TIPO_COMPOSICAO_MS03;
	}
	
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public List<ConsultaCompoGrupoComponenteVO> pesquisaGrupoComponenteNPT(Short seq){
		List<ConsultaCompoGrupoComponenteVO> listaComposicoes = new ArrayList<ConsultaCompoGrupoComponenteVO>();
		listaComposicoes = afaComposGrupoComponentesDAO.pesquisarGrupoComponente(seq);
		
		for (ConsultaCompoGrupoComponenteVO consultaCompoGrupoComponenteVO : listaComposicoes){
			String criadoPor = afaComposGrupoComponentesDAO.pesquisaInfoCriacaoGrupoComponente(consultaCompoGrupoComponenteVO.getSeq(), consultaCompoGrupoComponenteVO.getTicSeq());
			consultaCompoGrupoComponenteVO.setCriadoPor(criadoPor);
		}
		return listaComposicoes;
	}
	
	/**
	 * RN03
	 * @param seqGrupo
	 * @param seqTipoComposicao
	 * @param ativoGrupo
	 * @throws ApplicationBusinessException
	 */
	public void adicionarGrupoComponentesAssociados(Short seqGrupo, Short seqTipoComposicao, boolean ativoGrupo) throws ApplicationBusinessException{
		
		if(validarGrupoJaExistente(seqGrupo, seqTipoComposicao)){
			throw new ApplicationBusinessException(AfaComposGrupoComponentesRNExceptionCode.TIPO_COMPOSICAO_MS07, Severity.ERROR);
		}
		else{
			if(seqGrupo == null || seqTipoComposicao == null){
				throw new ApplicationBusinessException(AfaComposGrupoComponentesRNExceptionCode.TIPO_COMPOSICAO_MS03, Severity.ERROR);
			}
			preAdicionarGrupoComponentesAssociados(afaCompoGrupoComponente);
			afaCompoGrupoComponente.setIndSituacao(ativoGrupo == true ? DominioSituacao.A : DominioSituacao.I);
			
			afaCompoGrupoComponenteId.setGcnSeq(seqGrupo);
			afaCompoGrupoComponenteId.setTicSeq(seqTipoComposicao);
			afaCompoGrupoComponente.setId(afaCompoGrupoComponenteId);
			afaComposGrupoComponentesDAO.persistir(afaCompoGrupoComponente);
		}
	}
	
	/**
	 * Valida a existência de um grupo com a mesma chave primaria
	 * @param seqGrupo
	 * @param seqTipoComposicao
	 * @return
	 */
	public boolean validarGrupoJaExistente(Short seqGrupo, Short seqTipoComposicao){
		if(seqGrupo!=null && seqTipoComposicao!=null){
			if(afaComposGrupoComponentesDAO.pesquisaInfoCriacaoGrupoComponente(seqGrupo, seqTipoComposicao) != null){
				return true;
			}
		}
		return false;
	}
	
	public void alterarGrupoComponentesAssociados(Short seqGrupo, Short seqTipoComposicao, boolean ativoGrupo){
		
		afaCompoGrupoComponenteId.setGcnSeq(seqGrupo);
		afaCompoGrupoComponenteId.setTicSeq(seqTipoComposicao);
		
		AfaCompoGrupoComponente afaCompoGrupoComponenteOriginal = afaComposGrupoComponentesDAO.obterOriginal(afaCompoGrupoComponenteId);
		
		afaCompoGrupoComponenteOriginal.setIndSituacao(ativoGrupo == true ? DominioSituacao.A : DominioSituacao.I);
		
		preUpdate(afaCompoGrupoComponenteOriginal);
		
		afaComposGrupoComponentesDAO.atualizar(afaCompoGrupoComponenteOriginal);
	}
	
	private void preUpdate(
		AfaCompoGrupoComponente afaCompoGrupoComponenteOriginal) {
		afaCompoGrupoComponenteOriginal.setAlteradoEm(new Date());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		afaCompoGrupoComponenteOriginal.setRapServidoresByAfaTcgSerFk2(servidorLogado);
	}
	
	/**#3506
	 * @param afaCompoGrupoComponente
	 * @throws ApplicationBusinessException
	 */
	private void preAdicionarGrupoComponentesAssociados(AfaCompoGrupoComponente afaCompoGrupoComponente) throws ApplicationBusinessException{
		afaCompoGrupoComponente.setCriadoEm(new Date());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		afaCompoGrupoComponente.setRapServidoresByAfaTcgSerFk1(servidorLogado);
	}
	
	public void removerGrupoComponentesAssociados(ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado){
		afaCompoGrupoComponenteId.setGcnSeq(listaGrupoComponenteSelecionado.getSeq());
		afaCompoGrupoComponenteId.setTicSeq(listaGrupoComponenteSelecionado.getTicSeq());
		afaCompoGrupoComponente.setId(afaCompoGrupoComponenteId);
		afaCompoGrupoComponente = afaComposGrupoComponentesDAO.obterPorChavePrimaria(afaCompoGrupoComponente.getId());
		afaComposGrupoComponentesDAO.remover(afaCompoGrupoComponente);
	}
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
}
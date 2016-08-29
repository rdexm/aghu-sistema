package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaGrupoComponenteNptJnDAO;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaCompoGrupoComponenteId;
import br.gov.mec.aghu.model.AfaGrupoComponNptJn;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.AfaComposGrupoComponentesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaTipoComposicoesDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

@Stateless
public class AfaGrupoComponenteNptRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AfaGrupoComponenteNptRN.class);
	
	@Inject
	private AfaTipoComposicoesDAO afaTipoComposicoesDAO;

	@Inject
	private AfaGrupoComponenteNptJnDAO afaGrupoComponenteNptJnDAO;
	
	@Inject 
	private AfaComposGrupoComponentesDAO afaComposGrupoComponenteDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;	
	
	private AfaCompoGrupoComponente afaCompoGrupoComponente = new AfaCompoGrupoComponente();

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	/**
	 * 
	 * @param afaTipoComposicoes
	 * @param firstResult, maxResult, orderProperty, asc
	 * @return
	 */
	public List<ConsultaTipoComposicoesVO> pesquisarTiposComposicaoNPT(AfaTipoComposicoes afaTipoComposicoes, int firstResult, 
			int maxResult, String orderProperty,Boolean asc){
		List<ConsultaTipoComposicoesVO> listaComposicoes = new ArrayList<ConsultaTipoComposicoesVO>();
		listaComposicoes = afaTipoComposicoesDAO.pesquisarTiposComposicaoNPT(afaTipoComposicoes);
		
		for (ConsultaTipoComposicoesVO consultaTipoComposicoesVO : listaComposicoes){
			String criadoPor = afaTipoComposicoesDAO.listaInfoCriacaoTipoComposicao(consultaTipoComposicoesVO.getSeq());
			consultaTipoComposicoesVO.setCriadoPor(criadoPor);
		}
		return listaComposicoes;
	}
	/**#3506
	 * afat_gcn_ard
	 * @param listaGrupoComponenteSelecionado
	 */
	public void afterRemoverGrupoComponente(ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado){
		AfaGrupoComponNptJn afaGrupoComponNptJn = new AfaGrupoComponNptJn();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		afaGrupoComponNptJn.setSeq(listaGrupoComponenteSelecionado.getSeq());
		afaGrupoComponNptJn.setNomeUsuario(servidorLogado.getPessoaFisica().getNome());
		afaGrupoComponNptJn.setOperacao(DominioOperacoesJournal.DEL);
		afaGrupoComponNptJn.setSerMatricula(servidorLogado.getId().getMatricula());
		afaGrupoComponNptJn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		afaGrupoComponNptJn.setDescricao(listaGrupoComponenteSelecionado.getDescricao());
		afaGrupoComponNptJn.setCriadoEm(listaGrupoComponenteSelecionado.getCriadoEm());
		afaGrupoComponNptJn.setIndSituacao(listaGrupoComponenteSelecionado.getIndSituacao());
		afaGrupoComponenteNptJnDAO.persistir(afaGrupoComponNptJn);
	}
	/**#3506
	 * afat_gcn_aru
	 * @param listaGrupoComponenteSelecionado
	 */
	public void afterUpdateGrupoComponente(ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado){
		
		AfaGrupoComponNptJn afaGrupoComponNptJn = new AfaGrupoComponNptJn();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AfaCompoGrupoComponenteId id = new AfaCompoGrupoComponenteId();
		id.setGcnSeq(listaGrupoComponenteSelecionado.getSeq());
		id.setTicSeq(listaGrupoComponenteSelecionado.getTicSeq());
		
		afaCompoGrupoComponente = afaComposGrupoComponenteDAO.obterOriginal(id);

		if (afaCompoGrupoComponente.getIndSituacao() != listaGrupoComponenteSelecionado.getIndSituacao()){
			afaGrupoComponNptJn.setNomeUsuario(servidorLogado.getPessoaFisica().getNome());
			afaGrupoComponNptJn.setOperacao(DominioOperacoesJournal.UPD);
			afaGrupoComponNptJn.setSeq(listaGrupoComponenteSelecionado.getSeq());
			afaGrupoComponNptJn.setSerMatricula(servidorLogado.getId().getMatricula());
			afaGrupoComponNptJn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
			afaGrupoComponNptJn.setDescricao(listaGrupoComponenteSelecionado.getDescricao());
			afaGrupoComponNptJn.setCriadoEm(listaGrupoComponenteSelecionado.getCriadoEm());
			afaGrupoComponNptJn.setIndSituacao(listaGrupoComponenteSelecionado.getIndSituacao());
			afaGrupoComponenteNptJnDAO.persistir(afaGrupoComponNptJn);
		}
		
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
}
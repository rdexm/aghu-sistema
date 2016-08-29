package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMateriaisItemOpmesJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesJnDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmesJn;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmesJn;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcRequisicaoOpmesJn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.model.BaseJournal;


@Stateless
public class JournalOpmeRN extends BaseBusiness {
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 2904224092932761752L;
	
	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;
	
	@Inject
	private MbcMateriaisItemOpmesJnDAO mbcMateriaisItemOpmesJnDAO;
	
	@Inject
	private MbcMateriaisItemOpmesDAO mbcMateriaisItemOpmesDAO;
	
	@Inject
	private MbcItensRequisicaoOpmesJnDAO mbcItensRequisicaoOpmesJnDAO;
	
	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;
	
	@Inject 
	private MbcRequisicaoOpmesJnDAO mbcRequisicaoOpmesJnDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	

	
	public void inserirMbcItensRequisicaoOpmesJn(MbcItensRequisicaoOpmes opme, DominioOperacoesJournal tipoAcao){
		if(opme != null){
			if(opme.getSeq() != null){
				opme = mbcItensRequisicaoOpmesDAO.obterPorChavePrimaria(opme.getSeq());
			}
			MbcItensRequisicaoOpmesJn journal = this.bindMbcItensRequisicaoOpmesJn(opme);
			this.complementarCamposJournal(tipoAcao, journal);
			this.mbcItensRequisicaoOpmesJnDAO.persistir(journal);
			flush();
		}	
	}
	
	public void inserirMbcMateriaisItemOpmesJn(MbcMateriaisItemOpmes opme, DominioOperacoesJournal tipoAcao){
		if(opme != null){
			if(opme.getSeq() != null){
				opme = mbcMateriaisItemOpmesDAO.obterPorChavePrimaria(opme.getSeq());
			}
			MbcMateriaisItemOpmesJn journal = this.bindMbcMateriaisItemOpmesJn(opme);
			this.complementarCamposJournal(tipoAcao, journal);
			this.mbcMateriaisItemOpmesJnDAO.persistir(journal);
			flush();
		}	
	}
	
	public void inserirMbcRequisicaoOpmes(MbcRequisicaoOpmes opme, DominioOperacoesJournal tipoAcao){
		if(opme != null){
			if(opme.getSeq() != null){
				opme = mbcRequisicaoOpmesDAO.obterPorChavePrimaria(opme.getSeq());
			}
			MbcRequisicaoOpmesJn journal = this.bindMbcRequisicaoOpmesJn(opme);
			journal.setOperacao(tipoAcao);
			journal.setNomeUsuario(obterLoginUsuarioLogado());
			this.mbcRequisicaoOpmesJnDAO.persistir(journal);
			flush();
		}	
	}
	
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void complementarCamposJournal(DominioOperacoesJournal tipoAcao, BaseJournal journal) {
		journal.setOperacao(tipoAcao);
		journal.setNomeUsuario(obterLoginUsuarioLogado());
	}

	private MbcMateriaisItemOpmesJn bindMbcMateriaisItemOpmesJn(MbcMateriaisItemOpmes opme) {
		
		return new MbcMateriaisItemOpmesJn(
				opme.getSeq(),
				opme.getItensRequisicaoOpmes(),
				opme.getProcedHospInternos(),
				opme.getMaterial(),
				opme.getSituacao(),
				opme.getQuantidadeSolicitada(),
				opme.getQuantidadeConsumida(),
				new Date(),
				opme.getModificadoEm(),
				opme.getRapServidores(),
				opme.getRapServidoresModificacao());
	}	

	private MbcItensRequisicaoOpmesJn bindMbcItensRequisicaoOpmesJn(MbcItensRequisicaoOpmes opme) {
		return new MbcItensRequisicaoOpmesJn(
				opme.getSeq(),
				opme.getRequisicaoOpmes() != null ? this.mbcRequisicaoOpmesDAO.obterPorChavePrimaria(opme.getRequisicaoOpmes().getSeq()) : null,
				opme.getFatItensProcedHospitalar() != null ? this.faturamentoFacade.obterItemProcedHospitalarPorChavePrimaria(opme.getFatItensProcedHospitalar().getId()) : null,
				opme.getRequerido(),
				opme.getIndCompativel(),
				opme.getIndAutorizado(),
				opme.getIndConsumido(),
				opme.getQuantidadeAutorizadaSus(),
				opme.getValorUnitarioIph(),
				opme.getQuantidadeSolicitada(),
				opme.getQuantidadeAutorizadaHospital(),
				opme.getSolicitacaoNovoMaterial(),
				opme.getValorNovoMaterial(),
				opme.getEspecificacaoNovoMaterial(),
				opme.getQuantidadeConsumida(),
				opme.getAnexoOrcamento(),
				new Date(),
				opme.getRapServidores(),
				opme.getModificadoEm(),
				opme.getRapServidoresModificacao()
		);
	}
	
	

	private MbcRequisicaoOpmesJn bindMbcRequisicaoOpmesJn(MbcRequisicaoOpmes opme) {
		return new MbcRequisicaoOpmesJn(
				opme.getSeq(), 
				opme.getAgendas(),
				opme.getCirurgia(), 
				opme.getSituacao().toString(), 
				opme.getObservacaoOpme(),
				opme.getJustificativaRequisicaoOpme(),
				opme.getJustificativaConsumoOpme(), 
				new Date(), 
				opme.getModificadoEm(),
				opme.getRapServidores(),
				opme.getRapServidoresModificacao(), 
				opme.getIndCompativel(),
				opme.getIndAutorizado(), 
				opme.getIndConsAprovacao(), 
				opme.getDataFim(),
				opme.getFluxo());
	}
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}

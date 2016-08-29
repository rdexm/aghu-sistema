package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnicaJn;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmDesmembramento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmArquivosAnexosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAvaliacaoTecnicaDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAvaliacaoTecnicaJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmDesmembramentoDAO;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

@Stateless
public class PtmAvaliacaoTecnicaRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 1837813620450376028L;
	private static final Log LOG = LogFactory.getLog(PtmAvaliacaoTecnicaRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private PtmAvaliacaoTecnicaDAO ptmAvaliacaoTecnicaDAO;
	@Inject
	private PtmAvaliacaoTecnicaJnDAO ptmAvaliacaoTecnicaJNDAO;
	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	@Inject
	private PtmDesmembramentoDAO ptmDesmembramentoDAO;
	@EJB
	private PtmDesmembramentoRN ptmDesmembramentoRN;
	@EJB
	private PtmBemPermanenteRN ptmBemPermanenteRN;
	@Inject
	private PtmArquivosAnexosDAO ptmArquivosAnexosDAO;
	
	public List<AvaliacaoTecnicaVO> carregarAnaliseTecnico(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<AvaliacaoTecnicaVO> retornoAvaliacaoTecnica = ptmAvaliacaoTecnicaDAO.carregarAnaliseTecnico(itemRecebimento, firstResult, maxResult, orderProperty, asc);
		for (AvaliacaoTecnicaVO item : retornoAvaliacaoTecnica) {			
			if (item.getAvtSeq() != null){				
				item.setNumerosBensAvaliacaoTecnica(ptmAvaliacaoTecnicaDAO.numeroPatrimonioAvaliacaoTecnica(itemRecebimento, item));				
			}
		}			
		return retornoAvaliacaoTecnica;
	}
	
	public void persistirPtmAvaliacaoTecnica(PtmAvaliacaoTecnica avaliacaoTecnica, RapServidores servidor){
		avaliacaoTecnica.setDataCriacao(new Date());
		avaliacaoTecnica.setServidor(servidor);
		if(avaliacaoTecnica.getMarcaComercial() != null){
			avaliacaoTecnica.setMcmCod(avaliacaoTecnica.getMarcaComercial().getCodigo());
		}
		if(avaliacaoTecnica.getMarcaModelo() != null && avaliacaoTecnica.getMarcaModelo().getId() != null){
			avaliacaoTecnica.setMomCod(avaliacaoTecnica.getMarcaModelo().getId().getSeqp());
		}
		this.ptmAvaliacaoTecnicaDAO.persistir(avaliacaoTecnica);
//		this.ptmAvaliacaoTecnicaDAO.flush();
		posInsert(avaliacaoTecnica);
	}
	
	private void posInsert(PtmAvaliacaoTecnica avaliacaoTecnica){
		gravarPtmAvaliacaoTecnicaJN(avaliacaoTecnica, DominioOperacoesJournal.INS);
	}
	
	public void updatePtmAvaliacaoTecnica(PtmAvaliacaoTecnica avaliacaoTecnica){
		avaliacaoTecnica.setDataUltimaAlteracao(new Date());
		if(avaliacaoTecnica.getMarcaComercial() != null){
			avaliacaoTecnica.setMcmCod(avaliacaoTecnica.getMarcaComercial().getCodigo());
		}
		if(avaliacaoTecnica.getMarcaModelo() != null && avaliacaoTecnica.getMarcaModelo().getId() != null){
			avaliacaoTecnica.setMomCod(avaliacaoTecnica.getMarcaModelo().getId().getSeqp());
		}
		this.ptmAvaliacaoTecnicaDAO.atualizar(avaliacaoTecnica);
//		this.ptmAvaliacaoTecnicaDAO.flush();
		posUpdate(avaliacaoTecnica);
	}
	
	private void posUpdate(PtmAvaliacaoTecnica avaliacaoTecnica){
		gravarPtmAvaliacaoTecnicaJN(avaliacaoTecnica, DominioOperacoesJournal.UPD);
	}
	
	private void preExcluir(Integer avtSeq, RapServidores servidor){
		List<PtmDesmembramento> listDesmebramento = ptmDesmembramentoDAO.pesquisarDesmembramentoPorAvtSeq(avtSeq);
		if (listDesmebramento != null && !listDesmebramento.isEmpty()) {
			for (PtmDesmembramento desmebramento : listDesmebramento) {
				this.ptmDesmembramentoRN.excluirPtmDesmembramento(desmebramento.getSeq(), servidor);
			}
		}
		
		List<PtmBemPermanentes> listBemPermanentes = ptmBemPermanentesDAO.obterPorAvtSeq(avtSeq);
		if (listBemPermanentes != null && !listBemPermanentes.isEmpty()) {
			for (PtmBemPermanentes bem : listBemPermanentes) {
				bem.setAvaliacaoTecnica(null);
				this.ptmBemPermanenteRN.updatePtmBemPermanentes(bem, servidor);
			}
		}
		
	}
	public void excluirPtmAvaliacaoTecnica(Integer seq, RapServidores servidor){
		PtmAvaliacaoTecnica avaliacaoTecnica = ptmAvaliacaoTecnicaDAO.obterPorChavePrimaria(seq);
		if (avaliacaoTecnica != null && avaliacaoTecnica.getSeq() != null) {
			List<PtmArquivosAnexos> arquivosAnexos = ptmArquivosAnexosDAO.obterAnexoArquivoAceiteTecnico(seq);
			preExcluir(avaliacaoTecnica.getSeq(), servidor);
			for (PtmArquivosAnexos ptmArquivosAnexos : arquivosAnexos) {
				ptmArquivosAnexosDAO.remover(ptmArquivosAnexos);
			}
			ptmAvaliacaoTecnicaDAO.remover(avaliacaoTecnica);
			posExcluir(avaliacaoTecnica);
		}
	}
	private void posExcluir(PtmAvaliacaoTecnica avaliacaoTecnica){
		gravarPtmAvaliacaoTecnicaJN(avaliacaoTecnica, DominioOperacoesJournal.DEL);
	}
	private void gravarPtmAvaliacaoTecnicaJN(PtmAvaliacaoTecnica avaliacaoTecnica, DominioOperacoesJournal operacao) {
		
		PtmAvaliacaoTecnicaJn avaliacaoTecnicaJN = new PtmAvaliacaoTecnicaJn();
		avaliacaoTecnicaJN.setNomeUsuario(super.obterLoginUsuarioLogado());
		avaliacaoTecnicaJN.setOperacao(operacao);
		avaliacaoTecnicaJN.setSeq(avaliacaoTecnica.getSeq());
		avaliacaoTecnicaJN.setVidaUtil(avaliacaoTecnica.getVidaUtil());
		avaliacaoTecnicaJN.setDataInicioGarantia(avaliacaoTecnica.getDataInicioGarantia());
		avaliacaoTecnicaJN.setTempoGarantia(avaliacaoTecnica.getTempoGarantia());
		avaliacaoTecnicaJN.setJustificativa(avaliacaoTecnica.getJustificativa());
		avaliacaoTecnicaJN.setDescricaoMaterial(avaliacaoTecnica.getDescricaoMaterial());
		avaliacaoTecnicaJN.setIndStatus(avaliacaoTecnica.getIndStatus());
		avaliacaoTecnicaJN.setIndSituacao(avaliacaoTecnica.getIndSituacao());
		avaliacaoTecnicaJN.setDataCriacao(avaliacaoTecnica.getDataCriacao());
		avaliacaoTecnicaJN.setDataUltimaAlteracao(avaliacaoTecnica.getDataUltimaAlteracao());
		avaliacaoTecnicaJN.setServidor(avaliacaoTecnica.getServidor());
		avaliacaoTecnicaJN.setServidorFinalizado(avaliacaoTecnica.getServidorFinalizado());
		avaliacaoTecnicaJN.setServidorCertificado(avaliacaoTecnica.getServidorCertificado());
		avaliacaoTecnicaJN.setItemRecebProvisorio(avaliacaoTecnica.getItemRecebProvisorio());
		if(avaliacaoTecnica.getMarcaComercial() != null){
			avaliacaoTecnicaJN.setMcmCod(avaliacaoTecnica.getMarcaComercial().getCodigo());
		}
		if(avaliacaoTecnica.getMarcaModelo() != null && avaliacaoTecnica.getMarcaModelo().getId() != null){
			avaliacaoTecnicaJN.setMomCod(avaliacaoTecnica.getMarcaModelo().getId().getSeqp());
		}
		this.ptmAvaliacaoTecnicaJNDAO.persistir(avaliacaoTecnicaJN);
	}
}

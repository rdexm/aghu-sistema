package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.PtmDesmembramento;
import br.gov.mec.aghu.model.PtmDesmembramentoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmDesmembramentoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmDesmembramentoJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

@Stateless
public class PtmDesmembramentoRN extends BaseBusiness implements Serializable{
	
	private static final long serialVersionUID = -188089196496200052L;
	
	private static final Log LOG = LogFactory.getLog(PtmDesmembramentoRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private PtmDesmembramentoDAO ptmDesmembramentoDAO;
	
	@Inject
	private PtmDesmembramentoJnDAO ptmDesmembramentoJNDAO;
	
	public void persistirPtmDesmembramento(PtmDesmembramento desmebramento, RapServidores servidor){
		if (desmebramento.getSeq() != null && desmebramento.getSeq() > 0) {
			updatePtmDesmembramento(desmebramento, servidor);
			return;
		}
		desmebramento.setDataCriacao(new Date());
		desmebramento.setSeq(null);
		this.ptmDesmembramentoDAO.persistir(desmebramento);
//		this.ptmDesmembramentoDAO.flush();
		posInsert(desmebramento, servidor);
	}
	
	private void posInsert(PtmDesmembramento desmebramento, RapServidores servidor){
		gravarPtmDesmembramentoJN(desmebramento, DominioOperacoesJournal.INS, servidor);
	}

	public void updatePtmDesmembramento(PtmDesmembramento desmebramento, RapServidores servidor){
		this.ptmDesmembramentoDAO.merge(desmebramento);
//		this.ptmDesmembramentoDAO.flush();
		posUpdate(desmebramento, servidor);
	}
	
	private void posUpdate(PtmDesmembramento desmebramento, RapServidores servidor){
		gravarPtmDesmembramentoJN(desmebramento, DominioOperacoesJournal.UPD, servidor);
	}
	
	public void excluirPtmDesmembramento(Integer seq, RapServidores servidor){
		PtmDesmembramento desmebramento = ptmDesmembramentoDAO.obterPorChavePrimaria(seq);
		if (desmebramento != null && desmebramento.getSeq() != null) {
			ptmDesmembramentoDAO.removerPorId(seq);
			posExcluir(desmebramento, servidor);
		}
	}
	
	private void posExcluir(PtmDesmembramento desmebramento, RapServidores servidor){
		gravarPtmDesmembramentoJN(desmebramento, DominioOperacoesJournal.DEL, servidor );
	}
	
	private void gravarPtmDesmembramentoJN(PtmDesmembramento desmebramento, DominioOperacoesJournal operacao, RapServidores servidor) {
		PtmDesmembramentoJn desmebramentoJN = new PtmDesmembramentoJn();
			desmebramentoJN.setNomeUsuario(super.obterLoginUsuarioLogado());
			desmebramentoJN.setOperacao(operacao);
			desmebramentoJN.setSeq(desmebramento.getSeq());
			desmebramentoJN.setAvaliacaoTecnica(desmebramento.getAvaliacaoTecnica());
			desmebramentoJN.setDescricao(desmebramento.getDescricao());
			desmebramentoJN.setPercentual(desmebramento.getPercentual());
			desmebramentoJN.setValor(desmebramento.getValor());
			desmebramentoJN.setServidor(servidor);
			desmebramentoJN.setDataCriacao(desmebramento.getDataCriacao());
		this.ptmDesmembramentoJNDAO.persistir(desmebramentoJN);
	}
}

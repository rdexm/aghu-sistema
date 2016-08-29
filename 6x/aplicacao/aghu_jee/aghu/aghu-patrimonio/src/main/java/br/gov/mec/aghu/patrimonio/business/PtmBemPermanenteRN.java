package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmBemPermanentesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PtmBemPermanenteRN extends BaseBusiness implements Serializable{

	private static final long serialVersionUID = 2453276997330932133L;

	private static final Log LOG = LogFactory.getLog(PtmBemPermanenteRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	@Inject
	private PtmBemPermanentesJnDAO ptmBemPermanentesJnDAO;
	
	public void persistirPtmBemPermanentes(PtmBemPermanentes bemPermanente, RapServidores servidor){
		if (bemPermanente.getSeq() != null) {
			updatePtmBemPermanentes(bemPermanente, servidor);
			return;
		}
		bemPermanente.setDataCriacao(new Date());
		bemPermanente.setServidor(servidor);
		this.ptmBemPermanentesDAO.persistir(bemPermanente);
		posInsert(bemPermanente,servidor);
	}
	
	private void posInsert(PtmBemPermanentes bemPermanente, RapServidores servidor){
		gravarPtmBemPermanentesJN(bemPermanente, DominioOperacaoBanco.INS, servidor);
	}

	public void updatePtmBemPermanentes(PtmBemPermanentes bemPermanente, RapServidores servidor){
		this.ptmBemPermanentesDAO.merge(bemPermanente);
		posUpdate(bemPermanente, servidor);
	}
	
	private void posUpdate(PtmBemPermanentes bemPermanente, RapServidores servidor){
		gravarPtmBemPermanentesJN(bemPermanente, DominioOperacaoBanco.UPD, servidor);
	}
	
	public void excluirPtmBemPermanentes(Integer seq, RapServidores servidor){
		PtmBemPermanentes bemPermanente = new PtmBemPermanentes();
		bemPermanente = this.ptmBemPermanentesDAO.obterPorChavePrimaria(seq);
		if (bemPermanente != null && bemPermanente.getSeq() != null) {
			this.ptmBemPermanentesDAO.removerPorId(seq);
			posExcluir(bemPermanente, servidor);
		}
	}
	private void posExcluir(PtmBemPermanentes bemPermanente, RapServidores servidor){
		gravarPtmBemPermanentesJN(bemPermanente, DominioOperacaoBanco.DEL, servidor);
	}
	
	private void gravarPtmBemPermanentesJN(PtmBemPermanentes bemPermanente, DominioOperacaoBanco operacao, RapServidores servidor) {
		
		PtmBemPermanentesJn bemPermanenteJN = new PtmBemPermanentesJn();
			bemPermanenteJN.setCcSeq(bemPermanente.getCcSeq());// nullable
			bemPermanenteJN.setAtaSeq(bemPermanente.getAtaSeq());
			bemPermanenteJN.setForSeq(bemPermanente.getForSeq());
			bemPermanenteJN.setGndSeq(bemPermanente.getGndSeq());
			bemPermanenteJN.setMatSeq(bemPermanente.getMatSeq());
			bemPermanenteJN.setVgeSeq(bemPermanente.getVgeSeq());
			bemPermanenteJN.setSeq(bemPermanente.getSeq().intValue());// nullable
			bemPermanenteJN.setIrpSeq(bemPermanente.getIrpSeq().intValue());// nullable
			bemPermanenteJN.setNumeroBem(bemPermanente.getNumeroBem());
			bemPermanenteJN.setBemPenhora(bemPermanente.getBemPenhora());
			bemPermanenteJN.setNumeroSerie(bemPermanente.getNumeroSerie());
			bemPermanenteJN.setNumeroProcesso(bemPermanente.getNumeroProcesso());
			bemPermanenteJN.setSituacao(bemPermanente.getSituacao());// nullable
			bemPermanenteJN.setTipo(bemPermanente.getTipo());// nullable
			bemPermanenteJN.setJnData(new Date());// nullable
			bemPermanenteJN.setDataAlteracao(new Date());
			bemPermanenteJN.setDataCriacao(bemPermanente.getDataCriacao());
			bemPermanenteJN.setDataAquisicao(bemPermanente.getDataAquisicao());
			bemPermanenteJN.setDetalhamento(bemPermanente.getDetalhamento());
			bemPermanenteJN.setProSeq(bemPermanente.getProSeq());
			bemPermanenteJN.setJnUsuario(servidor.getUsuario());// nullable
			bemPermanenteJN.setSerMatricula(servidor.getId().getMatricula());// nullable
			bemPermanenteJN.setSerVinCodigo(servidor.getId().getVinCodigo().intValue());// nullable
			bemPermanenteJN.setValorAtual(bemPermanente.getValorAtual());
			bemPermanenteJN.setValorInicial(bemPermanente.getValorInicial());
			bemPermanenteJN.setJnOperacao(operacao);// nullable
			bemPermanenteJN.setAvaliacaoTecnica(bemPermanente.getAvaliacaoTecnica());// nullable
			bemPermanenteJN.setVidaUtil(bemPermanente.getVidaUtil());
			bemPermanenteJN.setDataInicioGarantia(bemPermanente.getDataInicioGarantia());
			bemPermanenteJN.setTempoGarantia(bemPermanente.getTempoGarantia());
			bemPermanenteJN.setStatusAceiteTecnico(bemPermanente.getStatusAceiteTecnico());
		this.ptmBemPermanentesJnDAO.persistir(bemPermanenteJN);
		
	}
}
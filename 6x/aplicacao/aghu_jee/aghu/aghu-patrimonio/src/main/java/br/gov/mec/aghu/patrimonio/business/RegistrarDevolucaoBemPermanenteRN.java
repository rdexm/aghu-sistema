package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmBemPermanentesJn;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmItemRecebProvisoriosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosJnDAO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class RegistrarDevolucaoBemPermanenteRN extends BaseBusiness implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5775257440302794724L;
	
	private static final Log LOG = LogFactory.getLog(RegistrarDevolucaoBemPermanenteRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;

	@Inject
	private PtmBemPermanentesJnDAO ptmBemPermanentesJnDAO;

	@Inject
	private AghParametrosDAO aghParametrosDAO;

	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO;
	
	@Inject
	private PtmItemRecebProvisoriosJnDAO ptmItemRecebProvisoriosJnDAO;

	public enum RegistrarDevolucaoBemPermanenteRNExceptionCode implements BusinessExceptionCode {
		
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void atualizar(List<DevolucaoBemPermanenteVO> itensParaDevolucao) {

		for (DevolucaoBemPermanenteVO vo : itensParaDevolucao) {
			PtmBemPermanentes pbp = ptmBemPermanentesDAO
					.obterBemPermanentePorSeq(vo.getPbpSeq());
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			pbp.setDataAlteracao(new Date());
			pbp.setCcSeq(this.aghParametrosDAO.obterValorNumericoAghParametros(
					"P_AGHU_CENTRO_CUSTO_PATRIMONIO").intValue());
			
			gravarHistoricoBemPermanente(pbp, servidorLogado);
			
			pbp.setServidor(servidorLogado);

			ptmBemPermanentesDAO.atualizar(pbp);
			ptmBemPermanentesDAO.flush();
			
			PtmItemRecebProvisorios pirp = this.ptmItemRecebProvisoriosDAO
					.obterPorIdItensEstoque(vo.getSirpNrpSeq(),
							vo.getSirpNroItem());
			
			garavrHistoricoItemReceProvisorio(pirp, servidorLogado);

			Integer vlrNumerico = Integer.valueOf(aghParametrosDAO.obterValorNumericoAghParametros("P_AGHU_CENTRO_CUSTO_PATRIMONIO").toString());

			Long countDispDiferentePA01 = this.ptmBemPermanentesDAO.countQtdDisp(vo.getPirpSeq(), false, vlrNumerico);
			Long countDispIgualPA01 = this.ptmBemPermanentesDAO.countQtdDisp(vo.getPirpSeq(), true, vlrNumerico);

			if (countDispDiferentePA01 != null && countDispIgualPA01 != null) {
				pirp.setQuantidadeDisp((countDispDiferentePA01 - countDispIgualPA01) < 0 ? (countDispDiferentePA01 - countDispIgualPA01)
						* (-1)
						: countDispDiferentePA01 - countDispIgualPA01);
			} else if (countDispDiferentePA01 != null
					&& countDispIgualPA01 == null) {
				pirp.setQuantidadeDisp(countDispDiferentePA01.longValue());
			} else if (countDispIgualPA01 != null
					&& countDispDiferentePA01 == null) {
				pirp.setQuantidadeDisp(countDispIgualPA01.longValue());
			}

			ptmItemRecebProvisoriosDAO.atualizar(pirp);
			ptmItemRecebProvisoriosDAO.flush();
		}
	}
	
	public void gravarHistoricoBemPermanente(PtmBemPermanentes pbp, RapServidores servidor) {

		PtmBemPermanentesJn pbpJn = new PtmBemPermanentesJn();

		pbpJn.setCcSeq(pbp.getCcSeq());// nullable
		pbpJn.setAtaSeq(pbp.getAtaSeq());
		pbpJn.setForSeq(pbp.getForSeq());
		pbpJn.setGndSeq(pbp.getGndSeq());
		pbpJn.setMatSeq(pbp.getMatSeq());
		pbpJn.setVgeSeq(pbp.getVgeSeq());
		pbpJn.setSeq(pbp.getSeq().intValue());// nullable
		pbpJn.setIrpSeq(pbp.getIrpSeq().intValue());// nullable
		pbpJn.setNumeroBem(pbp.getNumeroBem());
		pbpJn.setBemPenhora(pbp.getBemPenhora());
		pbpJn.setNumeroSerie(pbp.getNumeroSerie());
		pbpJn.setNumeroProcesso(pbp.getNumeroProcesso());
		pbpJn.setSituacao(pbp.getSituacao());// nullable
		pbpJn.setTipo(pbp.getTipo());// nullable
		pbpJn.setJnData(new Date());// nullable
		pbpJn.setDataAlteracao(new Date());
		pbpJn.setDataCriacao(pbp.getDataCriacao());
		pbpJn.setDataAquisicao(pbp.getDataAquisicao());
		pbpJn.setDetalhamento(pbp.getDetalhamento());
		pbpJn.setProSeq(pbp.getProSeq());
		pbpJn.setJnUsuario(servidor.getUsuario());// nullable
		pbpJn.setSerMatricula(servidor.getId().getMatricula());// nullable
		pbpJn.setSerVinCodigo(servidor.getId().getVinCodigo().intValue());// nullable
		pbpJn.setValorAtual(pbp.getValorAtual());
		pbpJn.setValorInicial(pbp.getValorInicial());
		pbpJn.setJnOperacao(DominioOperacaoBanco.UPD);// nullable

		this.ptmBemPermanentesJnDAO.persistir(pbpJn);
	}
	
	public void garavrHistoricoItemReceProvisorio(PtmItemRecebProvisorios pirp, RapServidores servidor){

		PtmItemRecebProvisoriosJn ptmItemRecebProvisoriosJn = new PtmItemRecebProvisoriosJn();
		
		ptmItemRecebProvisoriosJn.setNomeUsuario(servidor.getUsuario());
		ptmItemRecebProvisoriosJn.setOperacao(DominioOperacoesJournal.UPD);
		if(pirp.getAtaSeq() != null){			
			ptmItemRecebProvisoriosJn.setAtaSeq(pirp.getAtaSeq());
		}
		ptmItemRecebProvisoriosJn.setDataRecebimento(pirp.getDataRecebimento());
		ptmItemRecebProvisoriosJn.setDataUltimaAlteracao(pirp.getDataUltimaAlteracao());
		ptmItemRecebProvisoriosJn.setPagamentoParcial(pirp.getPagamentoParcial());
		ptmItemRecebProvisoriosJn.setSceItemRecebProvisorio(pirp.getSceItemRecebProvisorio());
		ptmItemRecebProvisoriosJn.setSeq(pirp.getSeq());
		ptmItemRecebProvisoriosJn.setServidor(pirp.getServidor());
		if(pirp.getServidorTecPadrao() != null){			
			ptmItemRecebProvisoriosJn.setServidorTecPadrao(pirp.getServidorTecPadrao());
		}
		ptmItemRecebProvisoriosJn.setStatus(pirp.getStatus());
		this.ptmItemRecebProvisoriosJnDAO.persistir(ptmItemRecebProvisoriosJn);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
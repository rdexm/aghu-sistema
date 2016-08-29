package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAceiteTecnico;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmDesmembramento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmAvaliacaoTecnicaDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmBemPermanentesDAO;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVOComparator;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AceiteTecnicoRN extends BaseBusiness implements Serializable {
	
	private static final long serialVersionUID = 5071008542012072511L;
	private static final Log LOG = LogFactory.getLog(AceiteTecnicoRN.class);
	
	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;
	
	@Inject
	private PtmBemPermanentesDAO ptmBemPermanentesDAO;
	
	@Inject
	private PtmAvaliacaoTecnicaDAO ptmAvaliacaoTecnicaDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private PtmAvaliacaoTecnicaRN ptmAvaliacaoTecnicaRN;
	
	@EJB
	private PtmDesmembramentoRN ptmDesmembramentoRN;
	
	@EJB
	private PtmItemRecebProvisoriosRN ptmItemRecebProvisoriosRN;
	
	@EJB
	private PtmBemPermanenteRN ptmBemPermanenteRN;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public List<AvaliacaoTecnicaVO> recuperarListaPaginadaAceiteTecnico(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AvaliacaoTecnicaVO filtro, boolean vinculoCentroCusto, RapServidores servidor){
		List<AvaliacaoTecnicaVO> lista = this.ptmAvaliacaoTecnicaDAO.recuperarListaPaginadaAceiteTecnico(
				firstResult, maxResult, orderProperty, asc, filtro, vinculoCentroCusto, servidor);
		for (AvaliacaoTecnicaVO avaliacaoTecnicaVO : lista) {
			List<DevolucaoBemPermanenteVO> listfaBens = this.ptmAvaliacaoTecnicaDAO.obterPatrimonioPorAvaliacao(
					avaliacaoTecnicaVO.getSceNrpSeq(), avaliacaoTecnicaVO.getSceNrpItem(), avaliacaoTecnicaVO.getSeqAvaliacaoTec());
			if(listfaBens != null && !listfaBens.isEmpty()){
				avaliacaoTecnicaVO.setNumerosBens(new ArrayList<DevolucaoBemPermanenteVO>(listfaBens));
			}
			
			
			avaliacaoTecnicaVO.setRecebItemFormatado(avaliacaoTecnicaVO.obterRecebItemFormatado());
			avaliacaoTecnicaVO.setPatrimonioTruncado(patrimonioTruncado(avaliacaoTecnicaVO.getNumerosBens(), 20));
		}
		
		if(orderProperty != null && !orderProperty.isEmpty()){
			Comparator<AvaliacaoTecnicaVO> comparator = null;
			
			if(orderProperty.equals(AvaliacaoTecnicaVO.Fields.RECEB_ITEM_FORMATADO.toString())){
				comparator = new AvaliacaoTecnicaVOComparator.OrderByRecebItemFormatado();
			}else if(orderProperty.equals(AvaliacaoTecnicaVO.Fields.PATRIMONIO_TRUNCADO.toString())){
				comparator = new AvaliacaoTecnicaVOComparator.OrderByPatrimonioTruncado();
			}
			
			Collections.sort(lista, comparator);
			
			if(!asc){
				Collections.reverse(lista);
			}
			
		}
		
		return lista;
	}
	
	private String patrimonioTruncado(List<DevolucaoBemPermanenteVO> itens, Integer tamanhoMaximo) {
		String resultado = "";
		StringBuilder patrimonioBuilder = new StringBuilder();
		resultado = concatenarPatrimonio(itens, resultado, patrimonioBuilder);
		if (resultado.length() > tamanhoMaximo) {
			resultado = StringUtils.abbreviate(resultado, tamanhoMaximo);
		}
		return resultado;
	}
	
	private String concatenarPatrimonio(List<DevolucaoBemPermanenteVO> listaNumeroBens, String resultado, StringBuilder patrimonioBuilder) {
		if (listaNumeroBens == null || listaNumeroBens.isEmpty()) {
			return resultado;
		} else {
			boolean primeira = true;
			for (DevolucaoBemPermanenteVO numeroBem : listaNumeroBens) {
				if (numeroBem.getPbpNrBem() != null) {
					if (primeira) {
						resultado = patrimonioBuilder.append(numeroBem.getPbpNrBem()).toString();
						primeira = false;
					} else {
						resultado = patrimonioBuilder.append(" , " + numeroBem.getPbpNrBem()).toString();
					}
				}
			}
		}
		return resultado;
	}
	
	public Double carregarCampoDaTela(PtmAvaliacaoTecnica avaliacaoTecnica){
		AvaliacaoTecnicaVO dadosTela = this.sceItemRecebProvisorioDAO.carregarCampoDaTela(avaliacaoTecnica.getItemRecebProvisorio());
		if (dadosTela != null) {
			if (dadosTela.getIafMcmCodigo() != null) {
				avaliacaoTecnica.setMarcaComercial(comprasFacade.obterScoMarcaComercialPorChavePrimaria(dadosTela.getIafMcmCodigo()));
			}
			if (dadosTela.getIafMomSeqp() != null && dadosTela.getIafMomMcmCodigo() != null) {
				avaliacaoTecnica.setMarcaModelo(comprasFacade.buscaScoMarcaModeloPorId(dadosTela.getIafMomSeqp(), dadosTela.getIafMomMcmCodigo()));
			}
			if (dadosTela.getMatDescricao() != null) {
				avaliacaoTecnica.setDescricaoMaterial(dadosTela.getMatDescricao());
			}
			if (dadosTela.getIrpValor() != null) {
				return dadosTela.getIrpValor();
			}
			
		}
		return 0D;
	}
	
	public void registrarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes,
			DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
//		List<PtmBemPermanentes> listaBens = new ArrayList<PtmBemPermanentes>(); 
//		avaliacaoTecnica.setDesmembramento(new HashSet<PtmDesmembramento>(listaDesmembramento));
//		for(DevolucaoBemPermanenteVO bem : listBensPermanteSelecionados){
//			PtmBemPermanentes bemPermanente = ptmBemPermanentesDAO.obterPorChavePrimaria(bem.getPbpSeq());
//			listaBens.add(bemPermanente);
//		}
//		avaliacaoTecnica.setBemPermanentes(new HashSet<PtmBemPermanentes>(listaBens));
		avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.N); 
		if(avaliacaoTecnica.getSeq() == null){
			this.ptmAvaliacaoTecnicaRN.persistirPtmAvaliacaoTecnica(avaliacaoTecnica, servidor);
		}else{
			this.ptmAvaliacaoTecnicaRN.updatePtmAvaliacaoTecnica(avaliacaoTecnica);
		}
		
		gravarDadosPosteriores(avaliacaoTecnica, listaDesmembramento, listBensPermantes, listBensPermanteSelecionados, servidor);
	}
	
	public void finalizarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes,
			DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
	//Finalizar	
//		List<PtmBemPermanentes> listaBens = new ArrayList<PtmBemPermanentes>(); 
//		avaliacaoTecnica.setDesmembramento(new HashSet<PtmDesmembramento>(listaDesmembramento));
//		for(DevolucaoBemPermanenteVO bem : listBensPermanteSelecionados){
//			PtmBemPermanentes bemPermanente = ptmBemPermanentesDAO.obterPorChavePrimaria(bem.getPbpSeq());
//			listaBens.add(bemPermanente);
//		}
//		avaliacaoTecnica.setBemPermanentes(new HashSet<PtmBemPermanentes>(listaBens));
		avaliacaoTecnica.setServidorFinalizado(servidor);
		avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.F); 
		if (avaliacaoTecnica.getSeq() != null) {
			ptmAvaliacaoTecnicaRN.updatePtmAvaliacaoTecnica(avaliacaoTecnica);
		}
		else{
			ptmAvaliacaoTecnicaRN.persistirPtmAvaliacaoTecnica(avaliacaoTecnica, servidor);
		}
		
		gravarDadosPosteriores(avaliacaoTecnica, listaDesmembramento, listBensPermantes, listBensPermanteSelecionados, servidor);
	}

	public void certificarAceiteTecnico(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento, List<DevolucaoBemPermanenteVO> listBensPermantes,
			DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
	//Certificar
//		List<PtmBemPermanentes> listaBens = new ArrayList<PtmBemPermanentes>(); 
//		avaliacaoTecnica.setDesmembramento(new HashSet<PtmDesmembramento>(listaDesmembramento));
//		for(DevolucaoBemPermanenteVO bem : listBensPermanteSelecionados){
//			PtmBemPermanentes bemPermanente = ptmBemPermanentesDAO.obterPorChavePrimaria(bem.getPbpSeq());
//			listaBens.add(bemPermanente);
//		}
//		avaliacaoTecnica.setBemPermanentes(new HashSet<PtmBemPermanentes>(listaBens));
		avaliacaoTecnica.setServidorCertificado(servidor);
		avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.C); 
		if (avaliacaoTecnica.getSeq() != null) {
			ptmAvaliacaoTecnicaRN.updatePtmAvaliacaoTecnica(avaliacaoTecnica);
		}
		else{
			ptmAvaliacaoTecnicaRN.persistirPtmAvaliacaoTecnica(avaliacaoTecnica, servidor);
		}
		gravarDadosPosteriores(avaliacaoTecnica, listaDesmembramento, listBensPermantes, listBensPermanteSelecionados, servidor);
		
		this.ptmItemRecebProvisoriosRN.atualizarItemRecebProvAceiteTec(avaliacaoTecnica);
	}
	
	public void excluirPtmAvaliacaoTecnica(Integer seq, RapServidores servidor){
		this.ptmAvaliacaoTecnicaRN.excluirPtmAvaliacaoTecnica(seq, servidor);
	}
	
	private void gravarDadosPosteriores(PtmAvaliacaoTecnica avaliacaoTecnica, List<PtmDesmembramento> listaDesmembramento,
			List<DevolucaoBemPermanenteVO> listBensPermantes, DevolucaoBemPermanenteVO[] listBensPermanteSelecionados, RapServidores servidor){
		List<DevolucaoBemPermanenteVO> listBensPermantesAux = new ArrayList<DevolucaoBemPermanenteVO>(listBensPermantes);
		
		for (DevolucaoBemPermanenteVO bem : listBensPermanteSelecionados) {
			if(listBensPermantesAux.contains(bem)){
				listBensPermantesAux.remove(bem);
			}
			PtmBemPermanentes bemPermanente = ptmBemPermanentesDAO.obterPorChavePrimaria(bem.getPbpSeq());
			bemPermanente.setAvaliacaoTecnica(avaliacaoTecnica);
			if(DominioSituacaoAceiteTecnico.C.equals(avaliacaoTecnica.getIndSituacao())){
				bemPermanente.setVidaUtil(avaliacaoTecnica.getVidaUtil());
				bemPermanente.setDataInicioGarantia(avaliacaoTecnica.getDataInicioGarantia());
				bemPermanente.setTempoGarantia(avaliacaoTecnica.getTempoGarantia());
				bemPermanente.setStatusAceiteTecnico(avaliacaoTecnica.getIndStatus());
			}
			this.ptmBemPermanenteRN.persistirPtmBemPermanentes(bemPermanente, servidor);
		}
		
		for (DevolucaoBemPermanenteVO bem : listBensPermantesAux){
			if(bem.getPbpSeq() != null){
				PtmBemPermanentes bemPermanente = ptmBemPermanentesDAO.obterPorChavePrimaria(bem.getPbpSeq());
				bemPermanente.setAvaliacaoTecnica(null);
				this.ptmBemPermanenteRN.updatePtmBemPermanentes(bemPermanente, servidor);
			}
		}
		if (listaDesmembramento != null && !listaDesmembramento.isEmpty()) {
			for (PtmDesmembramento ptmDesmembramento : listaDesmembramento) {
				ptmDesmembramento.setAvaliacaoTecnica(avaliacaoTecnica);
				this.ptmDesmembramentoRN.persistirPtmDesmembramento(ptmDesmembramento, servidor);
			}
		}
	}
}
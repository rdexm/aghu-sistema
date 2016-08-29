package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

@Stateless
public class ListaCirurgiasCanceladasRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ListaCirurgiasCanceladasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -471797191868027724L;

	/**
	 * Método que retorna a descricao do Procedimento de acordo com o seq da cirurgia
	 * ORADB function MBCC_PROC_PRIN_DESCR
	 * 
	 * @param crg_seq
	 * @return
	 */
	public String pesquisarProcEspCirurgico(Integer crgSeq) {
		
		List<MbcProcEspPorCirurgias> listaProc = this.getMbcProcEspPorCirurgiasDAO().
				pesquisarProcEspCirurgico(crgSeq);

		if (!listaProc.isEmpty()) {
			Collections.sort(listaProc, new MbcProcEspPorCirurgiasComparator());
			
			MbcProcEspPorCirurgias procEspPorCirurgia = listaProc.get(0);
			
			MbcProcedimentoCirurgicos procedimentoCirurgico = 
					getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(procEspPorCirurgia.getId().getEprPciSeq());
			
			return procedimentoCirurgico.getDescricao();
		}
		
		return "";
	}
	
	/**
	 * Comparator para ordenar pesquisa da entidade MbcProcEspPorCirurgias pelo DominioIndRespProc.
	 * 
	 * @author dpacheco
	 * 
	 */
	class MbcProcEspPorCirurgiasComparator implements Comparator<MbcProcEspPorCirurgias> {
		@Override
		public int compare(MbcProcEspPorCirurgias o1, MbcProcEspPorCirurgias o2) {
			return o1.getId().getIndRespProc().getCodigoCirurgiasCanceladas().
					compareTo(o2.getId().getIndRespProc().getCodigoCirurgiasCanceladas());
		}
	}
	
	/**
	 * Método que retorna equipe de acordo com o seq da cirurgia
	 * ORADB function MBCC_BUSCA_RESP_USUL
	 * 
	 * @param crg_seq
	 * @return
	 */
	public String pesquisarEquipeporCirurgia(Integer crgSeq) {
		
		MbcProfCirurgias profCirurgias = this.getMbcProfCirurgiasDAO().obterEquipePorCirurgia(crgSeq);
		
		if (profCirurgias!=null){
			if (profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual() != null){
				return profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual();
			}else { 
				return profCirurgias.getServidorPuc().getPessoaFisica().getNome();
			}
		} else { 
			return null;
		}
			
	}


	/**
	 * Método que retorna EprPciSeq de acordo com o seq da cirurgia
	 * ORADB function MBCC_PROC_PRIN_SEQ
	 * 
	 * Utilizar o método pesquisarMbcProcEspPorCirurgiasByCirurgia
	 * porque este retorna somente o SEQ
	 * 
	 * @deprecated
	 * @param crg_seq
	 * @return
	 */
	public Integer pesquisarEprPciSeqporCirurgia(Integer crgSeq) {
		List<MbcProcEspPorCirurgias> listaProc = this.getMbcProcEspPorCirurgiasDAO().
		pesquisarProcEspCirurgico(crgSeq);
		if (!listaProc.isEmpty()){
		
			CoreUtil.ordenarLista(listaProc, 
					MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()+".descricao", true);
			
			MbcProcEspPorCirurgias lista = listaProc.get(0);
			
			return  lista.getProcedimentoCirurgico().getSeq();
		
		} return null;
	}
	
	public MbcProcedimentoCirurgicos pesquisarMbcProcEspPorCirurgiasByCirurgia(Integer crgSeq) {
		
		List<MbcProcEspPorCirurgias> listaProc = this.getMbcProcEspPorCirurgiasDAO().pesquisarProcEspCirurgico(crgSeq);
		if (!listaProc.isEmpty()){
			CoreUtil.ordenarLista(listaProc, 
					MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString()+".descricao", true);
			
			return  listaProc.get(0).getProcedimentoCirurgico();
		
		} 
		return null;
	}
	
	private MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	private MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}

	private MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
}

package br.gov.mec.aghu.exames.solicitacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioTipoPesquisaExame;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameSuggestionVO;

@Dependent
public class ExamesSuggestionMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1806465257874391444L;
	
	private static final int SEQ_SANGUE = 67;
	private static final int SEQ_URINA = 1;
	private static final int SEQ_OUTROS_ESP = 77;

	List<ExameSuggestionVO> listaExames;
	
	private Integer atdSeq;
	

	@Inject
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	public List<ExameSuggestionVO> obterExamesSuggetion(String nomeExame, Integer seqAtendimento, Boolean isProtocoloEnf, Boolean isOrigemInternacao, DominioTipoPesquisaExame tipoPesquisa) {
		
		setAtdSeq(seqAtendimento);
		listaExames = this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExame(nomeExame, null, null, isOrigemInternacao, seqAtendimento, isProtocoloEnf, true, tipoPesquisa);
		ordenarLista();
		return listaExames;
	}

	private void ordenarLista() {
		List<ExameSuggestionVO>	listaSangue = new ArrayList<ExameSuggestionVO>();
		List<ExameSuggestionVO>	listaUrina = new ArrayList<ExameSuggestionVO>();
		List<ExameSuggestionVO>	listaOutrosEsp = new ArrayList<ExameSuggestionVO>();
		List<ExameSuggestionVO>	listaResto = new ArrayList<ExameSuggestionVO>();
		for (ExameSuggestionVO vo : listaExames) {
			switch (vo.getManSeq()) {
			case SEQ_SANGUE:
				listaSangue.add(vo);
				break;
			case SEQ_URINA:
				listaUrina.add(vo);
				break;
			case SEQ_OUTROS_ESP:
				listaOutrosEsp.add(vo);
				break;	
			default:
				listaResto.add(vo);
				break;
			}
		}
		listaExames.clear();
		listaExames.addAll(listaSangue);
		listaExames.addAll(listaUrina);
		listaExames.addAll(listaOutrosEsp);
		listaExames.addAll(listaResto);
	}

	
	public Long getCountExames(){
		if(listaExames!=null){
			return Long.valueOf(listaExames.size());
		} else {
			return 0L;
		}
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
}
